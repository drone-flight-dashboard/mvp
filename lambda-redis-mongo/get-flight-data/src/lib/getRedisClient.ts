import Redis, { Cluster } from 'ioredis';
import { logger } from '../loggerInit';

let redisClient: Cluster | undefined;
const REDIS_OPTIONS = {
    dnsLookup: (
        address: string,
        callback: (err: NodeJS.ErrnoException | null, address: string, family?: number) => void,
    ): void => {
        callback(null, address);
    },
    redisOptions: { tls: {} },
};

function createRedisClient() {
    try {
        const client = new Redis.Cluster([{ host: process.env.REDIS_HOST, port: 6379 }], REDIS_OPTIONS);
        client.on('ready', () => {
            logger.info('Connetion to ElasiCache Redis established');
        });
        client.on('error', (err: unknown) => {
            logger.error('Redis error:', err);
        });
        return client;
    } catch (error) {
        logger.error(`Failed to create Redis client - ${error}`);
        throw new Error(`Failure creating redis client: ${error}`);
    }
}

export function getRedisClient(): Cluster | null {
    logger.trace(`Entering getRedisClient function`);
    logger.trace(`redisClient=${redisClient}, redisClient.status=${redisClient?.status}`);
    let result = null;
    if (!redisClient || redisClient.status === 'end') {
        logger.trace(`Creating new redisClient object`);
        redisClient = createRedisClient();
    } else if (redisClient.status === 'ready') {
        logger.trace(`Finding existing client with ready status`);
        result = redisClient;
    }
    logger.trace(`Exiting getRedisClient with result ${result}`);
    return result;
}
