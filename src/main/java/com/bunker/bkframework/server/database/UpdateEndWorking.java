package com.bunker.bkframework.server.database;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.WorkConstants;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class UpdateEndWorking implements Working {
	private final DatabaseHelper dbHelper;
	private final String _TAG = "UpdateEndWorking";
	
	public UpdateEndWorking(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
		try {
			dbHelper.mWriteConnection.setAutoCommit(false);
		} catch (SQLException e) {
			Logger.err(_TAG, "set auto commit error", e);
		}
	}
	
	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		result.putReplyParam(WorkConstants.WORKING_RESULT, true);
		
		TransactionManager transactionManager = (TransactionManager) object.get(BkDatabaseConstants.UPDATE_TRANSACTION_MANAGER);
		if (transactionManager.size() > 0)
			doTransaction(transactionManager, result);
		return result;
	}
	
	synchronized public void doTransaction(TransactionManager transactionManager, WorkingResult workingResult) {
		List<UpdateTransaction> updates = transactionManager.getTransactions();
		
		for (UpdateTransaction update : updates) {
			QueryResult result = dbHelper.executeUpdateResult(update.getQuery(), _TAG + ":" + update.getFrom());
			if (update.getDelegator() != null) {
				try {
					if (!update.getDelegator().delegate(result, transactionManager, workingResult)) {
						workingResult.putReplyParam(WorkConstants.WORKING_RESULT, false);
						Logger.warning(_TAG, "rollback\n" + update.getFrom() + ":" + update.getQuery());
						result.close();
						rollback();
						return;
					}
				} catch (SQLException e) {
					Logger.err(_TAG, update.getFrom() + ": delegate error:" + update.getQuery());
					result.close();
					rollback();
					return;
				}
			}
			result.close();
			if (!result.succed()) {
				Logger.err(_TAG, update.getFrom() + ":" + update.getQuery());
				rollback();
				return;
			}
		}
		try {
			dbHelper.mWriteConnection.commit();
		} catch (SQLException e) {
			Logger.err(_TAG, "commit error " + updates.toString(), e);
		}
	}
	
	private void rollback() {
		try {
			dbHelper.mWriteConnection.rollback();
			return;
		} catch (SQLException e) {
			Logger.err(_TAG, "rollback error", e);
			return;
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}