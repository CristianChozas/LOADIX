package com.loadix.infrastructure.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class InfrastructureStartupVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfrastructureStartupVerifier.class);

    @Bean
    ApplicationRunner verifyInfrastructure(JdbcTemplate jdbcTemplate, StringRedisTemplate redisTemplate) {
        return args -> {
            jdbcTemplate.queryForObject("select 1", Integer.class);
            String redisStatus = redisTemplate.execute((RedisCallback<String>) connection -> ((StringRedisConnection) connection).ping());
            LOGGER.info("infrastructure_ready postgres=true redis_status={}", redisStatus);
        };
    }
}
