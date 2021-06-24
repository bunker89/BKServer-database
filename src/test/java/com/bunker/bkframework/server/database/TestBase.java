package com.bunker.bkframework.server.database;

import com.bunker.bkframework.server.BKLauncher;

public class TestBase {
	public TestBase() {
		System.out.println("TestBase init");
		BKLauncher.init();
		BKLauncher launcher = BKLauncher.getLauncher();
		String dbUrl = (String) launcher.getSystemParam("db_url");
		String id = (String) launcher.getSystemParam("db_id");
		String pass = (String) launcher.getSystemParam("db_pass");
		TestDatabaseHelper.createInstance(dbUrl, id, pass);
	}
}