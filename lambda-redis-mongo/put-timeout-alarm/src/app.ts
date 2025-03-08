/* eslint-disable prettier/prettier */
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

import { InvokeEvent } from '../types/LambdaInvokeEvent';

import { Handler, Context } from 'aws-lambda';
import { putTimeoutAlarm } from './lib/putTimeoutAlarm';

let isColdStart = true;

export const lambdaHandler: Handler = async (event: InvokeEvent, context: Context) => {
    if (isColdStart) {
        logger.trace(`Cold start detected`);
        isColdStart = false;
    } else {
        logger.trace(`Warm start detected`);
    }
    withRequest(event, context);
    context.callbackWaitsForEmptyEventLoop = false;
    try {
        await putTimeoutAlarm({
            fligthId: event.flightId,
            alarmDescription: 'Timeout recieving fligth data',
            timestamp: new Date(),
        });
    } catch (error: unknown) {
        logger.error('Exception in handler happend', error);
    }
};
