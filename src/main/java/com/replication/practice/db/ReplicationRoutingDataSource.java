package com.replication.practice.db;

import java.sql.ConnectionBuilder;
import java.sql.SQLException;
import java.sql.ShardingKeyBuilder;
import java.util.Map;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

	private String slaveServerName;
	@Override
	public Object determineCurrentLookupKey() {
		boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		if(isReadOnly) {
			return slaveServerName;
		} else {
			return "master";
		}
	}

	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		slaveServerName = targetDataSources.keySet()
			.stream()
			.map(Object::toString)
			.filter(name -> name.contains("slave"))
			.findFirst()
			.orElseThrow(RuntimeException::new);
		super.setTargetDataSources(targetDataSources);
	}

	@Override
	public ConnectionBuilder createConnectionBuilder() throws SQLException {
		return super.createConnectionBuilder();
	}

	@Override
	public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
		return super.createShardingKeyBuilder();
	}
}
