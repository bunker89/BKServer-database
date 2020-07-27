package com.bunker.bkframework.server.database;

public interface UpdateTransaction {
	String getQuery();
	String getFrom();
	UpdateResultDelegate getDelegator();
}
