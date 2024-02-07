package com.ld.poetry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许的域（你的前端域）
//        config.addAllowedOrigin("https://syml.online");
            config.addAllowedOriginPattern("*");
        // 允许的方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("OPTIONS");  // 需要支持OPTIONS请求
        config.addAllowedMethod("POST");  // 需要支持OPTIONS请求
        config.addAllowedMethod("PUT");  // 需要支持OPTIONS请求

        // 允许的头部
        config.addAllowedHeader("*");

        // 是否允许携带凭证（例如，带有Cookie）
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);  // 配置对应的路径

        return new CorsFilter(source);
    }
}
