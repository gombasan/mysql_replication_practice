package com.replication.practice.db.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.replication.practice.db.MultiDataSource;
import com.replication.practice.db.ReplicationRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class}) // DataSource 자동 설정을 제외시킨다.
@EnableConfigurationProperties(MultiDataSource.class)
public class MultiDataSourceConfig {
	private final MultiDataSource multiDataSource;
	private final JpaProperties jpaProperties;

	public DataSource createDataSource(String url) {
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.url(url)
			.driverClassName("com.mysql.cj.jdbc.Driver")
			.username(multiDataSource.getUsername())
			.password(multiDataSource.getPassword())
			.build();
	}

	@Bean
	public DataSource dataSource() {
		return new LazyConnectionDataSourceProxy(routingDataSource());
	}

	@Bean
	public DataSource routingDataSource() {
		DataSource defalutDataSource = createDataSource(multiDataSource.getUrl());
		MultiDataSource.Slave slave = multiDataSource.getSlave().get("slave1");
		Map<Object, Object> targetDataSource = new HashMap<>();
		targetDataSource.put("master", defalutDataSource);
		targetDataSource.put(slave.getName(), createDataSource(slave.getUrl()));
		ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();
		replicationRoutingDataSource.setDefaultTargetDataSource(defalutDataSource);
		replicationRoutingDataSource.setTargetDataSources(targetDataSource);

		return replicationRoutingDataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		EntityManagerFactoryBuilder entityManagerFactoryBuilder = createEntityManagerFactoryBuilder(jpaProperties);
		return entityManagerFactoryBuilder.dataSource(dataSource()).packages("com.replication.practice").build();
	}

	private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(JpaProperties jpaProperties) {
		AbstractJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		return new EntityManagerFactoryBuilder(vendorAdapter, jpaProperties.getProperties(), null);
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager tm = new JpaTransactionManager();
		tm.setEntityManagerFactory(entityManagerFactory);
		return tm;
	}
}
