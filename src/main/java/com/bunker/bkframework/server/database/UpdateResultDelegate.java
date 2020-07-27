package com.bunker.bkframework.server.database;

import java.sql.SQLException;

import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.bkframework.server.working.WorkingResult;

public interface UpdateResultDelegate {
	public boolean delegate(QueryResult queryResult, TransactionManager manager, WorkingResult workingResult) throws SQLException;
}
