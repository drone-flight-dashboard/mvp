import { SSMClient, GetParameterCommand } from '@aws-sdk/client-ssm';
import { logger } from '../loggerInit';
const ENV_VAR_NAME_FOR_PARAMETER_NAME = 'PARAMETER_NAME';
let dbURICache: string;

export default async function getDBURI() {
    if (!dbURICache) {
        dbURICache = await extractDbUriFromAWSParametrStore();
    }
    return dbURICache;
}

async function extractDbUriFromAWSParametrStore(): Promise<string> {
    const parameterName: string | undefined = process.env[ENV_VAR_NAME_FOR_PARAMETER_NAME];
    if (parameterName === undefined) {
        throw new Error(`Enviroment variable for DbUri parameter name doesn't exist`);
    } else if (!parameterName.startsWith('/')) {
        throw new Error(
            `Wrong value of ${ENV_VAR_NAME_FOR_PARAMETER_NAME} variable: ${parameterName} - shouldn't start with /`,
        );
    } else {
        try {
            return await readFromSSMParameterStore(parameterName);
        } catch (error) {
            throw new Error(`Problem of getting parameter ${parameterName} from Parameter Store`);
        }
    }
}

async function readFromSSMParameterStore(parameterName: string): Promise<string> {
    try {
        const ssm = new SSMClient();
        logger.trace('Trying to create ssm command for getting parameter');
        const command = new GetParameterCommand({
            Name: parameterName,
            WithDecryption: true,
        });
        logger.trace('Send ssm command');
        const param = await ssm.send(command);
        const paramValue = param.Parameter?.Value;
        logger.trace(`got value ${paramValue}`);
        if (!paramValue) {
            throw new Error(`Value for ${parameterName} is absent`);
        }
        return paramValue;
    } catch (error) {
        logger.trace(`SSM Parameter Fetch Error: ${error}`);
        throw new Error('Failed to fetch dbURI');
    }
}
