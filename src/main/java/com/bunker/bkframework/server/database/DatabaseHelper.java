package com.bunker.bkframework.server.database;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.database.WatchDog.DatabaseHelperFactory;
import com.bunker.bkframework.server.resilience.SystemModule;

import java.sql.*;
import java.util.LinkedHashSet;

public class DatabaseHelper implements DatabaseHelperFactory, SystemModule, DatabaseConnectorBase {
	private final String _TAG = "DatabaseHelper";
	public class QueryResult {
		private String _TAG;
		public PreparedStatement psmt;
		private boolean succed = true;
		public long result;
		public ResultSet set;
		private ConnectionWrapper wrapper;

		public void close() {
			if (wrapper != null) {
				wrapper.freeConnection();
				wrapper = null;
			}
			try {
				if (psmt != null)
					psmt.close();
				psmt = null;
				mGarbages.remove(this);
			} catch (SQLException e) {
				Logger.err(_TAG, "QueryResult:close remove err", e);
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
	private ConnectionPool mWriteConnection, mReadConnection;
	private int mQueryCount = 0;
	private int queryReport = 100;
	private WatchDog watchDog;

	public static final int SUCCESS = 0;
	public static final int FAIL = 1;

	protected int poolCount = 3;
	protected String mWriteUrl, mReadUrl;
	protected String mWriteId, mReadId;
	protected String mWritePass, mReadPass;
	protected boolean mUsingCommonDb = false;
	private boolean autoCommit = true;
	private final String _LOG = "DatabaseHelper";
	protected String mWatchDogName = "WatchDog";
	
	public ConnectionPool getWriteConnectionPool() {
		return mWriteConnection;
	}
	
	public ConnectionPool getReadConnectionPool() {
		return mReadConnection;
	}

	private ConnectionPool createConnectionPool(String url, String id, String pass) {
		Logger.logging(_LOG, "Database Connect");
		return new ConnectionPool(poolCount, new ConnectionFactory() {

			@Override
			public Connection createConnection() throws SQLException {
				return DriverManager.getConnection(url, id, pass);
			}
		});
	}
	
	protected void connectToDb() {
		mWriteConnection = createConnectionPool(mWriteUrl, mWriteId, mWritePass);
		
		if (!mUsingCommonDb)
			mReadConnection = createConnectionPool(mReadUrl, mReadId, mReadPass);
		else
			mReadConnection = mWriteConnection;
		watchDog = startWatchDog(this);
	}

	public void setAutoCommit(boolean b) {
		autoCommit = b;
		try {
			mWriteConnection.setAutoCommit(b);
		} catch (SQLException e) {
			Logger.err(_TAG, "set autocommit error", e);
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
	public boolean isWriteConnected() {
		return mWriteConnection != null;
	}

	@Override
	public boolean isReadConnected() {
		return mReadConnection != null;
	}

	@Override
	public WatchDog startWatchDog(DatabaseHelperFactory factory) {
		WatchDog watchdog = new WatchDog(this, factory, mUsingCommonDb, mWatchDogName); 
		watchdog.start();
		return watchdog;
	}

	@Override
	public void reConnectReadDb() {
		mReadConnection = createConnectionPool(mReadUrl, mReadId, mReadPass);
		if (mUsingCommonDb) {
			mWriteConnection = mReadConnection;
			if (!autoCommit)
				try {
					mWriteConnection.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					Logger.err(_TAG, "re connect db auto commit eror", e);
				}
		}
	}

	@Override
	public void reConnectWriteDb() {
		if (!mUsingCommonDb) {
			mWriteConnection = createConnectionPool(mWriteUrl, mWriteId, mWritePass);
			if (!autoCommit)
				try {
					mWriteConnection.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					Logger.err(_TAG, "re connect db auto commit eror", e);
				}
		}
	}

	QueryResult executeQuery(ConnectionWrapper wrapper, String query, String tag) {
		QueryResult result = new QueryResult();
		PreparedStatement psmt = null;
		result.wrapper = wrapper;
		try {
			psmt = wrapper.getConnection().prepareStatement(query);
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
					Logger.err(_TAG, query + "close error", e);
				}
		}
		mGarbages.add(result);
		queryFinish();
		return result;
	}
	
	public QueryResult executeQuery(String query, String tag) {
		QueryResult result = new QueryResult();
		PreparedStatement psmt = null;
		ConnectionWrapper wrapper = mReadConnection.allocateConnection();
		result.wrapper = wrapper;
		try {
			psmt = wrapper.getConnection().prepareStatement(query);
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
					Logger.err(_TAG, query + "close error", e);
				}
		}
		mGarbages.add(result);
		queryFinish();
		return result;
	}

	public QueryResult executeQueryCursor(String query, String tag) {
		QueryResult result = new QueryResult();
		PreparedStatement psmt = null;
		ConnectionWrapper wrapper = mReadConnection.allocateConnection();
		result.wrapper = wrapper;
		try {
			psmt = wrapper.getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
					Logger.err(_TAG, query + "close error", e);
				}
		}
		mGarbages.add(result);
		queryFinish();
		return result;
	}

	public QueryResult executeUpdateResult(String query, String tag) {
		QueryResult result = new QueryResult();
		result.result = -1;
		PreparedStatement psmt = null;
		ConnectionWrapper wrapper = mReadConnection.allocateConnection();
		result.wrapper = wrapper;

		try {
			psmt = wrapper.getConnection().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
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
					Logger.err(_TAG, query + "close error", e);
				}
		}

		mGarbages.add(result);
		queryFinish();
		return result;
	}

	public QueryResult executeUpdateNoAutoFree(ConnectionWrapper wrapper, String query, String tag) {
		QueryResult result = new QueryResult();
		result.result = -1;
		PreparedStatement psmt = null;

		try {
			psmt = wrapper.getConnection().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
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
					Logger.err(_TAG, query + "close error", e);
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
		ConnectionWrapper wrapper = mReadConnection.allocateConnection();
		
		try {
			psmt = wrapper.getConnection().prepareStatement(query, columnNames);
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
					Logger.err(_TAG, query + "close error", e);
				}
		}
		queryFinish();
		mGarbages.add(result);
		return result;
	}

	public QueryResult executeCall(ConnectionWrapper wrapper, String query, CallDelegate call, String tag) {
		QueryResult result = new QueryResult();
		result._TAG = tag;
		CallableStatement psmt = null;
		result.wrapper = wrapper;

		try {
			psmt = wrapper.getConnection().prepareCall(query);
			call.bindCall(psmt);
			psmt.execute();
			result.succed = true;
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
					Logger.err(_TAG, query + "close error", e);
				}
		}
		queryFinish();
		mGarbages.add(result);
		return result;
	}
	
	public QueryResult executeQueryAtWriteDatabase(String query, String tag) {
		QueryResult result = new QueryResult();
		PreparedStatement psmt = null;
		ConnectionWrapper wrapper = mReadConnection.allocateConnection();
		result.wrapper = wrapper;

		try {
			psmt = wrapper.getConnection().prepareStatement(query);
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
					Logger.err(_TAG, query + "close error", e);
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

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this;
	}

	private void queryFinish() {
		mQueryCount++;
		if (mQueryCount % queryReport == 0) {
			Logger.logging(_TAG, getClass().getSimpleName() + " query count through " + mQueryCount);
		}
		if (mGarbages.size() > 500) {
			String garbageTags = "----------------------------------------------\n";

			for (QueryResult q : mGarbages) {
				garbageTags += q._TAG + "\n";
			}

			garbageTags += "\n----------------------------------------------";
			Logger.logging(_TAG, "prepared statement garbages accumulated\n" + garbageTags);
		}
	}

	@Override
	public boolean isStoped() {
		return !watchDog.checkLoop();
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