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
import { getKeyValue } from './lib/getKeyValue';

import { APIGatewayProxyEvent, APIGatewayProxyResult, Context } from 'aws-lambda';

let isColdStart = true;

export const lambdaHandler = async (event: APIGatewayProxyEvent, context: Context): Promise<APIGatewayProxyResult> => {
    if (isColdStart) {
        logger.trace(`Cold start detected`);
        isColdStart = false;
    } else {
        logger.trace(`Warm start detected`);
    }
    withRequest(event, context);
    context.callbackWaitsForEmptyEventLoop = false;
    try {
        const result = await getKeyValue('key');
        return {
            statusCode: 200,
            body: JSON.stringify({
                message: 'hello world ' + result,
            }),
        };
    } catch (error: unknown) {
        logger.trace('Exception in handler happend', error);
        return {
            statusCode: 500,
            body: JSON.stringify({
                message: 'Internal Server Error',
                error: error instanceof Error && error.message,
            }),
        };
    }
};
