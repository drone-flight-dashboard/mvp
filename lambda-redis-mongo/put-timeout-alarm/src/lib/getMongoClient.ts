import { MongoClient } from 'mongodb';
import { logger } from '../loggerInit';
import getDBURI from './getDbUri';
let mongoDbClient: MongoClient | null = null;

export default async function getMongoDbClient() {
    if (!mongoDbClient) {
        try {
            const uri = await getDBURI();
            mongoDbClient = new MongoClient(uri);
            await mongoDbClient.connect();
            mongoDbClient.on('close', () => {
                mongoDbClient = null;
                logger.warn(`MongoDb client status equal 'close': set client reference to null`);
            });
            mongoDbClient.on('error', () => {
                mongoDbClient = null;
                logger.warn(`MongoDb client status equal 'error': set client reference to null`);
            });
        } catch (error) {
            logger.error(`Error creating MongoDb client: ${error}`);
            throw new Error(`Error while creating Mongo Db client: ${error}`);
        }
    }
    return mongoDbClient;
}
