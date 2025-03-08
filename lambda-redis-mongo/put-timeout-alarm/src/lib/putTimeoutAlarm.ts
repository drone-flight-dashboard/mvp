const DB_NAME = 'drone-flight-monitoring';
const COLLECTION_NAME = 'drone-flight-alarms';
import { logger } from '../loggerInit';
import getMongoDbClient from './getMongoClient';
import { AlarmCollection } from '../../types/AlarmCollection';

export async function putTimeoutAlarm(data: AlarmCollection) {
    logger.trace(`Entering getFromMongoDb with arg ${JSON.stringify(data)}`);

    try {
        const client = await getMongoDbClient();
        const collection = client.db(DB_NAME).collection<AlarmCollection>(COLLECTION_NAME);
        const result = await collection.insertOne(data);
        logger.trace(`Method insertOne returns ${result}`);
    } catch (error) {
        logger.error(`Error while working with MongoDb: ${error}`);
        throw new Error(`Error while working with MongoDb: ${error}`);
    }
}
