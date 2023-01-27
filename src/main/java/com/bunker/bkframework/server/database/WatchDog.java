package com.bunker.bkframework.server.database;

import java.util.LinkedList;
import java.util.List;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.bkframework.server.reserved.LogComposite;
import com.bunker.bkframework.server.reserved.Pair;
import sun.security.util.Debug;

public class WatchDog extends Thread implements LogComposite {
	private final String _TAG = "WatchDog";
	public interface DatabaseHelperFactory {
		public DatabaseHelper getDatabaseHelper();
	}

	private DatabaseHelperFactory mHelperFactory;
	private DatabaseConnectorBase mBase;
	private boolean mUsingCommonDb;
	private String mName;
	private boolean mCurrentState = true;;
	private boolean mPastState = true;;
	private int reconnectLoopGuard = 1000;

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
				Thread.sleep(900000);
//				Thread.sleep(3000);
				try {
					mCurrentState = checkLoop();
				} catch (Exception e) {
					Logger.err("WatchDog", "watch dog exception", e);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean checkLoop() {
		if (reconnectLoopGuard < 0)
			return false;
		Logger.logging("WatchDog", "looping");
		boolean state = true;
		QueryResult result;
		if (mBase.isReadConnected()) {
			ConnectionWrapper []wrappers = mHelperFactory.getDatabaseHelper().getReadConnectionPool().getConnections();

			for (ConnectionWrapper wrapper : wrappers) {
				result = mHelperFactory.getDatabaseHelper().executeQuery(wrapper, "select 1", "WatchDog");
				if (!result.succed()) {
					reconnectLoopGuard--;
					result.close();
					wrapper.reConnect();
					Logger.logging(_TAG, "db read reconnect");
					break;
				} else {
					result.close();
				}
			}
		} else {
			state = false;
			mPastState = false;
			mBase.reConnectReadDb();
		}

		if (!mUsingCommonDb) {
			if (mBase.isWriteConnected()) {
				ConnectionWrapper []wrappers = mHelperFactory.getDatabaseHelper().getWriteConnectionPool().getConnections();

				for (ConnectionWrapper wrapper : wrappers) {
					result = mHelperFactory.getDatabaseHelper().executeQuery(wrapper, "select 1", "WatchDog");
					if (!result.succed()) {
						reconnectLoopGuard--;
						result.close();
						wrapper.reConnect();
						Logger.logging(_TAG, "db write reconnect");
						break;
					}
					result.close();
				}
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