package com.bunker.bkframework.server.database;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionWrapper {
	private ConnectionFactory factory;
	private Connection connection;
	private ConnectionPool pool;
	private int index;
	
	ConnectionWrapper(ConnectionFactory factory, ConnectionPool pool, int index) throws SQLException {
		this.factory = factory;
		this.connection = factory.createConnection();
		this.pool = pool;
		this.index = index;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void freeConnection() {
		pool.freeConnection(index);
	}

	public void reConnect() {
		try {
			if (!this.connection.isClosed()) {
				this.connection.close();
			}
			this.connection = factory.createConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
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
