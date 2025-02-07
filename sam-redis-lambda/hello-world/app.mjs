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
import Redis from 'ioredis';

let client;

export const lambdaHandler = async (event, context) => {
  if (!client) {
    client = new Redis.Cluster(
      [{ host: 'clustercfg.redis.ri1ocn.euc1.cache.amazonaws.com', port: 6379 }],
      {
        dnsLookup: (address, callback) => callback(null, address),
        redisOptions: {
          tls: {},
        },
      });
  }

  try {
    return {
      statusCode: 200,
      body: JSON.stringify({ message: 'hello world ' + await client.get("key")}),
    };
  } catch (error) {
    console.error("Redis connection error:", error);
    return {
      statusCode: 500,
      body: JSON.stringify({ message: "Internal Server Error", error: error.message }),
    };
  } finally {
    client.disconnect();
  }
};

  