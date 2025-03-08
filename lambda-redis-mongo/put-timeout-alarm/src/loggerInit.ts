const LOG_LEVEL_VAR = 'LOG_LEVEL';
const DEFAULT_LOG_LEVEL = 'info';
import pino from 'pino';
import { lambdaRequestTracker, pinoLambdaDestination } from 'pino-lambda';
const destination = pinoLambdaDestination();
const loggerLevel = getLogLevel();
const logger = pino({ level: loggerLevel }, destination);

function getLogLevel() {
    return process.env[LOG_LEVEL_VAR] ?? DEFAULT_LOG_LEVEL;
}
const withRequest = lambdaRequestTracker();
export { logger, withRequest };
