package com.example.demo.sso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;

/**
 * @author ryu
 */
public class TokenConfig {
    @Resource
    RedisConnectionFactory connectionFactory;
    @Bean
    public TokenStore tokenStore(){
        return new RedisTokenStore(connectionFactory);
    }

}
