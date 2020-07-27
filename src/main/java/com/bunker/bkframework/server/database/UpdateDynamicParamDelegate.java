package com.bunker.bkframework.server.database;

import java.sql.SQLException;

import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.bkframework.server.working.WorkingResult;

public class UpdateDynamicParamDelegate implements UpdateResultDelegate {
	private final String paramId;

	public UpdateDynamicParamDelegate(String paramId) {
		this.paramId = paramId;
	}

	@Override
	public boolean delegate(QueryResult queryResult, TransactionManager manager, WorkingResult workingResult) throws SQLException {
		boolean result = false;
		boolean next = queryResult.set.next();
		if (next) {
			result = true;
			long id = queryResult.set.getLong(1);
			manager.setSingleDynamicValue(paramId, id);
		}
		return result;
	}
}
