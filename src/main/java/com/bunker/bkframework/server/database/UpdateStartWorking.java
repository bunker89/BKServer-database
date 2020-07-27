package com.bunker.bkframework.server.database;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.WorkConstants;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class UpdateStartWorking implements Working {

	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		TransactionManager transactionManager = new TransactionManager();
		result.putPrivateParam(BkDatabaseConstants.UPDATE_TRANSACTION_MANAGER, transactionManager);
		result.putReplyParam(WorkConstants.WORKING_RESULT, true);
		return result;
	}
	
	@Override
	public String getName() {
		return null;
	}
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}