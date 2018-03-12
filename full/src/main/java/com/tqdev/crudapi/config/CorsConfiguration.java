package com.tqdev.crudapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration {

	@Value("${rest.cors.allowed-origins:*}")
	private String[] allowedOrigins;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("OPTIONS", "GET", "PUT", "POST", "DELETE", "PATCH")
						.allowedHeaders("Content-Type", "X-XSRF-TOKEN").allowedOrigins(allowedOrigins)
						.allowCredentials(true);
			}
		};
	}
}