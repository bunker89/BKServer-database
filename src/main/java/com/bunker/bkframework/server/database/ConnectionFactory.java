package com.bunker.bkframework.server.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {
	public Connection createConnection() throws SQLException;
}
