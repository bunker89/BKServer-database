package com.bunker.bkframework.server.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.database.WatchDog.DatabaseHelperFactory;
import com.bunker.bkframework.server.resilience.SystemModule;

public class DatabaseHelper implements DatabaseHelperFactory, SystemModule, DatabaseConnectorBase {
	public class QueryResult {
		private String _TAG;
		public PreparedStatement psmt;
		private boolean succed = true;
		public long result;
		public ResultSet set;

		public void close() {
			try {
				if (psmt != null)
					psmt.close();
				psmt = null;
				mGarbages.remove(this);
			} catch (SQLException e) {
				Logger.err(_TAG, "QueryResult:close remove err");
				e.printStackTrace();
			}
		}
		
		public boolean succed() {
			return succed;
		}
	}
	
	public interface CallDelegate {
		public void bindCall(CallableStatement stmt) throws SQLException;
	}
	
	private LinkedHashSet<QueryResult> mGarbages = new LinkedHashSet<QueryResult>();
	private DatabaseConnectorBase connector;
	protected Connection mWriteConnection, mReadConnection;
	private int mQueryCount = 0;
	private int queryReport = 10;
	private WatchDog mLog;

	public static final int SUCCESS = 0;
	public static final int FAIL = 1;

	protected String mWriteUrl, mReadUrl;
	protected String mWriteId, mReadId;
	protected String mWritePass, mReadPass;
	protected boolean mUsingCommonDb = false;
	private final String _LOG = "DatabaseHelper";
	protected String mWatchDogName = "WatchDog";

	protected void connectToDb() {
		try {
			mWriteConnection = (Connection) DriverManager.getConnection(mWriteUrl, mWriteId, mWritePass);
			if (!mUsingCommonDb)
				mReadConnection = (Connection) DriverManager.getConnection(mReadUrl, mReadId, mReadPass);
			else
				mReadConnection = mWriteConnection;
			Logger.logging(_LOG, "Database Connect");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connect(this);
		}
	}

	//	private final String query = "select * from dummy";

	@Override
	public void destroy() {
		try {
			mWriteConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setQueryReportCount(int count) {
		this.queryReport = count;
	}

	@Override
	public Connection isWriteConnected() {
		return mWriteConnection;
	}

	@Override
	public WatchDog startWatchDog(DatabaseHelperFactory factory) {
		WatchDog watchdog = new WatchDog(this, factory, mUsingCommonDb, mWatchDogName); 
		watchdog.start();
		return watchdog;
	}

	@Override
	public Connection isReadConnected() {
		return mReadConnection;
	}

	@Override
	public void reConnectReadDb() {
		try {
			mReadConnection = (Connection) DriverManager.getConnection(mReadUrl, mReadId, mReadPass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void reConnectWriteDb() {
		if (!mUsingCommonDb) {
			try {
				mWriteConnection = (Connection) DriverManager.getConnection(mWriteUrl, mWriteId, mWritePass);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected void connect(DatabaseConnectorBase db) {
		mLog = db.startWatchDog(this);
	}

	public QueryResult executeQuery(String query, String tag) {
		QueryResult result = new QueryResult();
		PreparedStatement psmt = null;
		try {
			psmt = mReadConnection.prepareStatement(query);
			result.set = psmt.executeQuery();
			result.psmt = psmt;
			result._TAG = tag;
		} catch (SQLException e) {
			Logger.err(tag, query + "\n" + e.getMessage(), e);
			result.succed = false;
			if (psmt != null)
				try {
					psmt.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}
		mGarbages.add(result);
		queryFinish();
		return result;
	}
	
	@Deprecated
	public int executeUpdate(String query, String tag) throws SQLException {
		PreparedStatement psmt = mWriteConnection.prepareStatement(query);
		int result = psmt.executeUpdate();
		psmt.close();
		queryFinish();
		return result;
	}

	public QueryResult executeUpdateResult(String query, String tag) {
		QueryResult result = new QueryResult();
		result.result = -1;
		PreparedStatement psmt = null;

		try {
			psmt = mWriteConnection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			result.result = psmt.executeUpdate();
			result.set = psmt.getGeneratedKeys();
			result.psmt = psmt;
			result._TAG = tag;
		} catch (SQLException e) {
			Logger.err(tag, query + "\n" + e.getMessage(), e);
			result.succed = false;
			if (psmt != null)
				try {
					psmt.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}

		mGarbages.add(result);
		queryFinish();
		return result;
	}

	public QueryResult executeUpdateResult(String query, String []columnNames, String tag) {
		QueryResult result = new QueryResult();
		result._TAG = tag;
		PreparedStatement psmt = null;
		try {
			psmt = mWriteConnection.prepareStatement(query, columnNames);
			result.result = psmt.executeUpdate();
			result.set = psmt.getGeneratedKeys();
			result.psmt = psmt;
			result._TAG = tag;
		} catch (SQLException e) {
			Logger.err(tag, query, e);
			result.succed = false;
			if (psmt != null)
				try {
					psmt.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}
		queryFinish();
		mGarbages.add(result);
		return result;
	}

	public QueryResult executeCall(String query, CallDelegate call, String tag) {
		QueryResult result = new QueryResult();
		result._TAG = tag;
		CallableStatement psmt = null;
		try {
			psmt = mWriteConnection.prepareCall(query);
			call.bindCall(psmt);
			result.succed = psmt.execute();
			result.set = psmt.getGeneratedKeys();
			result.psmt = psmt;
			result._TAG = tag;
		} catch (SQLException e) {
			Logger.err(tag, query + "\n" + e.getMessage(), e);
			result.succed = false;
			if (psmt != null)
				try {
					psmt.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}
		queryFinish();
		mGarbages.add(result);
		return result;
	}
	
	public QueryResult executeQueryAtWriteDatabase(String query, String tag) {
		QueryResult result = new QueryResult();
		PreparedStatement psmt = null;
		try {
			psmt = mWriteConnection.prepareStatement(query);
			result.set = psmt.executeQuery();
			result.psmt = psmt;
			result._TAG = tag;
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.err(tag, query);
			result.succed = false;
			if (psmt != null)
				try {
					psmt.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}
		mGarbages.add(result);
		queryFinish();
		return result;
	}

	public static String wrapString(String str) {
//		if (str == null)
//			return null;
//		return new String("'" + str + "'");
		if (str == null)
			return null;

		str = str.replace("'", "\'\'");
		return new String("'" + str + "'");
	}

	public void setConnection() {
		mWriteConnection = connector.isWriteConnected();
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this;
	}

	private void queryFinish() {
		mQueryCount++;
		if (mQueryCount % queryReport == 0) {
			Logger.logging("DatabaseHelper", getClass().getSimpleName() + " query count through " + mQueryCount);
		}
		if (mGarbages.size() > 100) {
			String garbageTags = "----------------------------------------------\n";
			
			for (QueryResult q : mGarbages) {
				garbageTags += q._TAG + "\n";
			}

			garbageTags += "\n----------------------------------------------";
			Logger.logging("DatabaseHelper", "prepared statement garbages accumulated\n" + garbageTags);
		}
	}

	@Override
	public boolean isStoped() {
		return !mLog.checkLoop();
	}

	@Override
	public void moduleForceRestart() {
	}

	@Override
	public void moduleSafetyStop() {
	}

	@Override
	public boolean moduleStart() {
		return false;
	}
}