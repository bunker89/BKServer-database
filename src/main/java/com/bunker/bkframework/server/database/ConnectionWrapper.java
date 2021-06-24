package com.bunker.bkframework.server.database;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionWrapper {
	private Connection connection;
	private ConnectionPool pool;
	private int index;
	
	ConnectionWrapper(Connection connection, ConnectionPool pool, int index) {
		this.connection = connection;
		this.pool = pool;
		this.index = index;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void freeConnection() {
		pool.freeConnection(index);
	}
	
	void rollback() throws SQLException {
		connection.rollback();
	}
	
	void commit() throws SQLException {
		connection.commit();
	}
	
	int getIndex() {
		return index;
	}
}
