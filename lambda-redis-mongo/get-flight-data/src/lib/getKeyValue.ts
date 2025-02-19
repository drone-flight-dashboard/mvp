import { getRedisClient } from './getRedisClient';
import { logger } from '../loggerInit';
import getMongoDbClient from './getMongoClient';
import { DroneFlightCollection } from '../../types/DroneFlightRouteCollection';
const DB_NAME = 'drone-flight-monitoring';
const COLLECTION_NAME = 'drone-flight-route';

export async function getKeyValue(key: string): Promise<string | null> {
    logger.trace(`Entering getKeyValue with arg ${key}`);
    let result = await getFromRedis(key);
    if (result === null) {
        logger.trace(`Trying to get value from MongoBd`);
        result = await getFromMongoDb(key);
        if (result !== null) {
            logger.trace(`Storing result in Redis`);
            setRedisKeyValue(key, result);
        }
    }
    logger.trace(`getKeyValue returns ${result}`);
    return result;
}

async function getFromRedis(key: string) {
    logger.trace(`Entering getFromRedis with arg= ${key}`);
    let result = null;
    try {
        const redisClient = getRedisClient();
        if (redisClient) {
            logger.trace(`Got client with ready status`);
            result = await redisClient.get(key);
        }
    } catch (error) {
        logger.error(`Error reading redis: ${error}`);
    }
    logger.trace(`getFromRedis returns for key ${key} value: ${result}`);
    return result;
}
function setRedisKeyValue(key: string, value: string): void {
    logger.trace(`Entering setRedisKeyValue with arg: key ${key}, value ${value}`);
    try {
        const redisClient = getRedisClient();
        if (redisClient) {
            logger.trace(`Got valid Redis client, calling set method`);
            redisClient.set(key, value);
        }
    } catch (error) {
        logger.error(`Exception in setRedisKeyValue: ${error}`);
    }
}

async function getFromMongoDb(key: string): Promise<string | null> {
    logger.trace(`Entering getFromMongoDb with arg ${key}`);
    let result: DroneFlightCollection | null = null;
    try {
        const client = await getMongoDbClient();
        const collection = client.db(DB_NAME).collection<DroneFlightCollection>(COLLECTION_NAME);
        result = await collection.findOne({ key });
        logger.trace(`Method finfOne returns ${result}`);
        return result !== null ? result['value'] : result;
    } catch (error) {
        logger.error(`Error while working with MongoDb: ${error}`);
        throw new Error(`Error while working with MongoDb: ${error}`);
    }
}
