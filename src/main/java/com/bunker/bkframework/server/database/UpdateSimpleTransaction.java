package com.bunker.bkframework.server.database;

import com.bunker.jsqlbuilder.setters.PreparedSetter;

public class UpdateSimpleTransaction implements UpdateTransaction {
	public String from;
	public String query;
	public UpdateResultDelegate delegator;
	public PreparedSetter []setters;
	
	@Override
	public String toString() {
		return "UpdateTransaction-> " + from + ", query:" + query + ", setters" + setters;
	}

	@Override
	public String getQuery() {
		return query;
	}
	
	@Override
	public String getFrom() {
		return from;
	}

	@Override
	public UpdateResultDelegate getDelegator() {
		return delegator;
	}
}