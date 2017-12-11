package com.github.wuxudong.wechat;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jedis.JedisLockProvider;
import net.javacrumbs.shedlock.spring.ScheduledLockConfiguration;
import net.javacrumbs.shedlock.spring.ScheduledLockConfigurationBuilder;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import java.time.Duration;

@Configuration
public class Config {

    @Bean
    public RedissonClient redissonClient() {
        org.redisson.config.Config config = new org.redisson.config.Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        return Redisson.create(config);
    }

    @Bean
    public RedisTokenManager tokenManager(RedissonClient redissonClient) {
        RedisTokenManager.init(redissonClient, "appid", "appsecret");
        return new RedisTokenManager();
    }

    @Bean
    public RedisTicketManager ticketManager(RedissonClient redissonClient) {
        RedisTicketManager.init(redissonClient, "appid");
        return new RedisTicketManager();
    }

    @Bean
    public JedisPool jedisPool() {
        return new JedisPool("127.0.0.1", 6379);
    }

    @Bean
    public LockProvider lockProvider(JedisPool jedisPool) {
        return new JedisLockProvider(jedisPool);
    }

    @Bean
    public ScheduledLockConfiguration taskScheduler(LockProvider lockProvider) {
        return ScheduledLockConfigurationBuilder
                .withLockProvider(lockProvider)
                .withPoolSize(10)
                .withDefaultLockAtMostFor(Duration.ofMinutes(10))
                .build();
    }

}
