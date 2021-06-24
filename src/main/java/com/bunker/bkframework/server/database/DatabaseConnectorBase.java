package com.bunker.bkframework.server.database;

import com.bunker.bkframework.server.database.WatchDog.DatabaseHelperFactory;

public interface DatabaseConnectorBase {
	/**
	 * construct is must be default.
	 */
	public void destroy();	
	public void setQueryReportCount(int report);
	public boolean isWriteConnected();
	public boolean isReadConnected();
	public WatchDog startWatchDog(DatabaseHelperFactory factory);
	public void reConnectReadDb();
	public void reConnectWriteDb();
}