package com.bunker.bkframework.server.database;

public interface UpdateTransaction {
    String getQuery();

    String getFrom();

    UpdateResultDelegate getDelegator();

    DatabaseHelper.QueryResult schedule(DatabaseHelper dbHelper, ConnectionWrapper cWrapper, UpdateTransaction update, String tag);

	public interface Rollback {
		void rollback();
	}
}