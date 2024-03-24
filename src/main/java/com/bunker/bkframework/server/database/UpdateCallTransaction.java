package com.bunker.bkframework.server.database;

public class UpdateCallTransaction extends UpdateSimpleTransaction {
    public DatabaseHelper.CallDelegate callDelegate;

    @Override
    public DatabaseHelper.QueryResult schedule(DatabaseHelper dbHelper, ConnectionWrapper cWrapper, UpdateTransaction update, String tag) {
        return dbHelper.executeCallWithWrapper(cWrapper, update.getQuery(), callDelegate, tag);
    }
}