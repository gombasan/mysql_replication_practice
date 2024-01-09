package com.replication.practice.db.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.replication.practice.db.MultiDataSource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
// @RequiredArgsConstructor
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class}) // DataSource 자동 설정을 제외시킨다.
@EnableConfigurationProperties(MultiDataSource.class)
public class MultiDataSourceConfig {
	private final MultiDataSource multiDataSource;

	public MultiDataSourceConfig(MultiDataSource multiDataSource) {
		this.multiDataSource = multiDataSource;
		log.info("username = {}", multiDataSource.getUsername());
		log.info("password = {}", multiDataSource.getPassword());
		log.info("slave1 Name = {}" ,multiDataSource.getSlave().get("slave1").getName());
		log.info("slave1 url = {}", multiDataSource.getSlave().get("slave1").getUrl());
	}
}
