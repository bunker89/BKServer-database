package com.bunker.bkframework.server.database;

import java.util.LinkedList;
import java.util.List;

import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.bkframework.server.reserved.LogComposite;
import com.bunker.bkframework.server.reserved.Pair;

public class WatchDog extends Thread implements LogComposite {
	public interface DatabaseHelperFactory {
		public DatabaseHelper getDatabaseHelper();
	}

	private DatabaseHelperFactory mHelperFactory;
	private DatabaseConnectorBase mBase;
	private boolean mUsingCommonDb;
	private String mName;
	private boolean mCurrentState = true;;
	private boolean mPastState = true;;
	private int reconnectLoopGuard = 5;

	public WatchDog(DatabaseConnectorBase base, DatabaseHelperFactory factory, boolean usingCommonDb) {
		this(base, factory, usingCommonDb, null);
	}

	public WatchDog(DatabaseConnectorBase base, DatabaseHelperFactory factory, boolean usingCommonDb, String name) {
		mHelperFactory = factory;
		mBase = base;
		setDaemon(true);
		mUsingCommonDb = usingCommonDb;
		mName = name;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1800000);
				mCurrentState = checkLoop();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean checkLoop() {
		if (reconnectLoopGuard < 0)
			return false;
		boolean state = true;
		QueryResult result;
		if (mBase.isReadConnected()) {
			result = mHelperFactory.getDatabaseHelper().executeQuery("select * from dummy", "WatchDog");
			if (!result.succed()) {
				reconnectLoopGuard--;
				result.close();
				mBase.reConnectReadDb();
			} else
				result.close();
		} else {
			state = false;
			mPastState = false;
			mBase.reConnectReadDb();
		}

		if (!mUsingCommonDb) {
			if (mBase.isWriteConnected()) {
				result = mHelperFactory.getDatabaseHelper().executeQueryAtWriteDatabase("select * from dummy", "WatchDog");
				if (!result.succed()) {
					reconnectLoopGuard--;
					result.close();
					mBase.reConnectWriteDb();
				}
				result.close();
			} else {
				state = false;
				mPastState = false;
				mBase.reConnectWriteDb();
			}
		}
		return state;
	}
	
	@Override
	public void bindAction() {
	}
	
	@Override
	public List<Pair> logging() {
		Pair currentLog = new Pair(mName, mCurrentState);
		Pair pastLog = new Pair(mName, mPastState);
		List<Pair> list = new LinkedList<>();
		list.add(currentLog);
		list.add(pastLog);
		return list;
	}
	
	@Override
	public void releaseLog() {
	}

	@Override
	public void invokeTestErr() {
		// TODO Auto-generated method stub
		
	}
}