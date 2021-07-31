package com.bunker.bkframework.server.database;

import java.sql.SQLException;

import org.junit.Test;

import com.bunker.bkframework.newframework.Logger;

public class TransactionTest extends TestBase {
	private final String _TAG = "TransactionTest";
	@Test
	public void test() {
		try {
			TestDatabaseHelper.getInstance().getWriteConnectionPool().setAutoCommit(false);
		} catch (SQLException e) {
			Logger.err(_TAG, "set autocommit fail", e);
		}

		for (int i = 0; i < 20; i++) {
			final int index = i;
			new Thread(new Runnable() {

				@Override
				public void run() {
					ConnectionWrapper connection = TestDatabaseHelper.
							getInstance().
							getWriteConnectionPool().
							allocateConnection();

					synchronized (connection) {
						TestDatabaseHelper.getInstance().executeUpdateNoAutoFree(connection,
								"insert into test"
										+ " (test_index)"
										+ " values"
										+ " (" + index + ")", 
								"TransactionTest");
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					try {
						if (index % 2 == 0) {
							connection.getConnection().commit();
						} else {
							connection.getConnection().rollback();						
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					Logger.logging(_TAG, connection.getIndex() + "");
				}
			}).start();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}