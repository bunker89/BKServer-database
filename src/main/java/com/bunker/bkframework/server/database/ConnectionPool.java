package com.bunker.bkframework.server.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.bunker.bkframework.newframework.Logger;

public class ConnectionPool {
	private final String _TAG = "ConnectinoPool";
	private ConnectionWrapper []wrappers;
	private int currentIndex = 0;
	
	public ConnectionPool(int size, ConnectionFactory factory) {
		wrappers = new ConnectionWrapper[size];
		
		for (int i = 0; i < size; i++) {
			Connection connection;
			try {
				connection = factory.createConnection();
				wrappers[i] = new ConnectionWrapper(connection, this, i);
			} catch (SQLException e) {
				Logger.err(_TAG, null, e);
			}
		}
	}
	
	/**
	 * Not hard allocate.
	 * Multiple threads can preemptive one wrapper at the same time.
	 * It's only "Hey, You have to work now."
	 * 
	 * @return
	 */
	public ConnectionWrapper allocateConnection() {
		currentIndex %= wrappers.length;
		int index = currentIndex++;
		return wrappers[index];
	}
	
	ConnectionWrapper[] getConnections() {
		return wrappers;
	}
	
	/**
	 * 
	 * Connection can exit without free.
	 * It's only for "Hey I'm free!".
	 * @param index
	 */
	public void freeConnection(int index) {
	}
	
	void setAutoCommit(boolean bool) throws SQLException {
		for (ConnectionWrapper wrapper : wrappers) {
			wrapper.getConnection().setAutoCommit(bool);
		}
	}
	
	public void close() throws SQLException {
		for (ConnectionWrapper wrapper : wrappers) {
			wrapper.getConnection().close();
		}		
	}
}
