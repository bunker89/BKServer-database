package com.bunker.bkframework.server.database;

import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.bkframework.server.working.WorkingResult;

public class UpdateResultOneDelegate implements UpdateResultDelegate {
	@Override
	public boolean delegate(QueryResult result, TransactionManager transactionManager, WorkingResult workingResult) {
		return result.result == 1 ? true : false;
	}
}
