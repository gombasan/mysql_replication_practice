package com.replication.practice.db.config;

import static org.junit.jupiter.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
public class MultiDataSourceConfigTest {

	@Autowired
	private MultiDataSourceConfig multiDataSourceConfig;

	@Test
	public void testCreateDataSource() {
		String url = "jdbc:mysql://localhost:3306/simple";
		DataSource dataSource = multiDataSourceConfig.createDataSource(url);
		assertNotNull(dataSource);
	}

	@Test
	public void testDataSource() {
		DataSource dataSource = multiDataSourceConfig.dataSource();
		assertNotNull(dataSource);
	}

	@Test
	public void testRoutingDataSource() {

		DataSource dataSource = multiDataSourceConfig.routingDataSource();
		assertNotNull(dataSource);
	}

	@Test
	public void testEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factoryBean = multiDataSourceConfig.entityManagerFactory();
		assertNotNull(factoryBean);
	}

	@Test
	public void testTransactionManager() {
		LocalContainerEntityManagerFactoryBean factoryBean = multiDataSourceConfig.entityManagerFactory();
		PlatformTransactionManager transactionManager = multiDataSourceConfig.transactionManager(factoryBean.getObject());
		assertNotNull(transactionManager);
	}
}
