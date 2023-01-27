package com.bunker.bkframework.server.database;

public class TestDatabaseHelper extends DatabaseHelper {
	private static TestDatabaseHelper singleton;
	
	synchronized public static TestDatabaseHelper createInstance(String db, String id, String pass) {
		if (singleton == null) {
			singleton = new TestDatabaseHelper();
			singleton.mUsingCommonDb = true;
			singleton.mWriteUrl = singleton.mReadUrl = db;
			singleton.mWriteId = singleton.mReadId = id;
			singleton.mWritePass = singleton.mReadPass = pass;
			singleton.mWatchDogName = "AdverstudyDatabaseHelper";
			singleton.connectToDb();
		}
		return singleton;
	}

	synchronized public static TestDatabaseHelper getInstance() {
		return singleton;
	}
}