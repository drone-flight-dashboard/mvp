/**
 *
 * Event doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format
 * @param {Object} event - API Gateway Lambda Proxy Input Format
 *
 * Context doc: https://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-context.html
 * @param {Object} context
 *
 * Return doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html
 * @returns {Object} object - API Gateway Lambda Proxy Output Format
 *
 */
import { logger, withRequest } from './loggerInit';

import { SFNClient, StartExecutionCommand } from '@aws-sdk/client-sfn';

import { AttributeValue, Context, DynamoDBStreamEvent } from 'aws-lambda';
import type { MiddlewareStack, SerializeMiddleware, HandlerExecutionContext } from '@smithy/types';

let isColdStart = true;
const TIMESTAMP_TABLE_NAME = process['env']?.TIMESTAMP_TABLE;
const ERROR_MESS_BAD_TIMESTAMP = `Error putting item in ${TIMESTAMP_TABLE_NAME} table: timestamp is undefined`;
const DRONE_ID_ABSENT_ERROR_MESS = `Error putting item in ${TIMESTAMP_TABLE_NAME} table: drone_id is undefined`;
const NEW_IMAGE_ABSENT_ERROR_MESS = 'Error processing DynamoDb stream: expecting NewImage but it is absent';
const STEP_FUNCTION_ARN = process['env']?.SFN_ARN;

const loggingMiddleware = (next: any, context: HandlerExecutionContext) => async (args: any) => {
    logger.trace('RequesT: ' + JSON.stringify(args.request));
    const result = await next(args);
    logger.trace('Response: ' + JSON.stringify(result));
    return result;
};
const sfnClient = new SFNClient();

export const lambdaHandler = async (event: DynamoDBStreamEvent, context: Context) => {
    if (isColdStart) {
        logger.trace(`Cold start detected`);
        isColdStart = false;
    } else {
        logger.trace(`Warm start detected`);
    }
    withRequest(event, context);
    context.callbackWaitsForEmptyEventLoop = false;

    for (const record of event.Records.filter((record) => record.eventName === 'INSERT')) {
        await startStepFunction(record?.dynamodb?.NewImage);
    }
    event.Records.filter((record) => record.eventName === 'INSERT').forEach(
         async (record) => await startStepFunction(record?.dynamodb?.NewImage),
    );
    logger.trace(`command sent`);
};
async function runStepFunction(sfnClient: SFNClient, stateMachineArn: string, inputJSON: string) {
    logger.trace(`Entering runStepFunction with stateMachineArn: ${stateMachineArn}`);
    const res = await sfnClient.send(new StartExecutionCommand({ stateMachineArn, input: inputJSON }));
    logger.trace(`Entering sfn send returns: ${JSON.stringify(res)}`);
}
async function startStepFunction(
    newImage: { [key: string]: import('aws-lambda').AttributeValue } | undefined,
): Promise<void> {
    try {
        if (!newImage) {
            await runStepFunction(sfnClient, STEP_FUNCTION_ARN || '', JSON.stringify({ timeout: 15, flightId: 'xxx' }));
        } else {
            logger.trace(`Entering startStepFunction`);
            const parametersForExecution: { flightId: string; timeout: number } = getParameters(newImage);
            logger.trace(`getParameters returns ${JSON.stringify(parametersForExecution)}`);
            logger.trace(`Sending StartExecutionCommand for sfnArn: ${STEP_FUNCTION_ARN || ''}`);
            await runStepFunction(sfnClient, STEP_FUNCTION_ARN || '', JSON.stringify(parametersForExecution));
            logger.trace(`sent StartExecutionCommand}`);
        }
    } catch (error) {
        logger.trace('Exception while workoing with StepFunction API');
        logger.error('Error:', error);
    }
}

function getParameters(newImage: { [key: string]: AttributeValue } | undefined): {
    flightId: string;
    timeout: number;
} {
    if (newImage === undefined) {
        logger.error(NEW_IMAGE_ABSENT_ERROR_MESS);
        throw new Error(NEW_IMAGE_ABSENT_ERROR_MESS);
    }
    const droneId: string = getDroneId(newImage.drone_id);

    return { flightId: droneId, timeout: 15 };
}

function getDroneId(droneIdAttr: AttributeValue | undefined): string {
    if (droneIdAttr?.S === undefined) {
        logger.error(ERROR_MESS_BAD_TIMESTAMP);
        throw new Error(DRONE_ID_ABSENT_ERROR_MESS);
    }
    return droneIdAttr.S;
}
