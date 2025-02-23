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
import { DynamoDBClient, PutItemCommand } from '@aws-sdk/client-dynamodb';

import { AttributeValue, Context, DynamoDBStreamEvent } from 'aws-lambda';
let isColdStart = true;
const client = new DynamoDBClient({});
const ROW_FLIGHT_DATA_TABLE_NAME = process['env']?.ROW_DATA_TABLE;
const TIMESTAMP_TABLE_NAME = process['env']?.TIMESTAMP_TABLE;
const ERROR_MESS_BAD_TIMESTAMP = `Error putting item in ${TIMESTAMP_TABLE_NAME} table: timestamp is undefined`;
const DRONE_ID_ABSENT_ERROR_MESS = `Error putting item in ${TIMESTAMP_TABLE_NAME} table: drone_id is undefined`;
const NEW_IMAGE_ABSENT_ERROR_MESS = 'Error processing DynamoDb stream: expecting NewImage but it is absent';

export const lambdaHandler = async (event: DynamoDBStreamEvent, context: Context) => {
    if (isColdStart) {
        logger.trace(`Cold start detected`);
        isColdStart = false;
    } else {
        logger.trace(`Warm start detected`);
    }
    withRequest(event, context);
    context.callbackWaitsForEmptyEventLoop = false;
    event.Records.filter((record) => record.eventName === 'INSERT').forEach((record) =>
        putItemIntoTable(record?.dynamodb?.NewImage),
    );
};
async function putItemIntoTable(
    newImage: { [key: string]: import('aws-lambda').AttributeValue } | undefined,
): Promise<void> {
    try {
        logger.trace(`Entering putItemIntoTable`);
        const parametersForInsert: { droneId: string; timestamp: string } = getParameters(newImage);
        logger.trace(`getParameters returns ${JSON.stringify(parametersForInsert)}`);
        const params = {
            TableName: ROW_FLIGHT_DATA_TABLE_NAME,
            Item: {
                drone_id: { S: parametersForInsert.droneId },
                timestamp: { N: parametersForInsert.timestamp },
            },
        };
        const command = new PutItemCommand(params);
        const data = await client.send(command);
        logger.trace('Result of DymanoDb API send command : ' + JSON.stringify(data));
    } catch (error) {
        logger.error('Error:', error);
    }
}

function getUnixTimeStampSec(timestamp: AttributeValue | undefined): number {
    if (timestamp?.S === undefined) {
        logger.error(ERROR_MESS_BAD_TIMESTAMP);
        throw new Error(ERROR_MESS_BAD_TIMESTAMP);
    }
    const date = new Date(timestamp.S);
    return Math.floor(date.getTime() / 1000);
}
function getParameters(newImage: { [key: string]: AttributeValue } | undefined): {
    droneId: string;
    timestamp: string;
} {
    if (newImage === undefined) {
        logger.error(NEW_IMAGE_ABSENT_ERROR_MESS);
        throw new Error(NEW_IMAGE_ABSENT_ERROR_MESS);
    }
    const droneId: string = getDroneId(newImage.drone_id);
    const timestamp: string = getUnixTimeStampSec(newImage.timestamp).toString();
    return { droneId, timestamp };
}

function getDroneId(droneIdAttr: AttributeValue | undefined): string {
    if (droneIdAttr?.S === undefined) {
        logger.error(ERROR_MESS_BAD_TIMESTAMP);
        throw new Error(DRONE_ID_ABSENT_ERROR_MESS);
    }
    return droneIdAttr.S;
}
