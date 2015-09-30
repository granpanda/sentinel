package gp.e3.sentinel.infrastructure.healthchecks;

import com.codahale.metrics.health.HealthCheck;
import redis.clients.jedis.Jedis;

public class RedisHealthCheck extends HealthCheck {
	
	private Jedis redisClient;
	
	public RedisHealthCheck(Jedis redisClient) {
		this.redisClient = redisClient;
	}

	@Override
	protected Result check() throws Exception {
		String pong = redisClient.ping();
		return pong.equalsIgnoreCase("pong") ? Result.healthy() : Result.unhealthy("Redis client is not connected.");
	}
}