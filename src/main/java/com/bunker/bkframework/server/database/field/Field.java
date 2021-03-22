package com.bunker.bkframework.server.database.field;

import java.sql.SQLException;

import org.json.JSONObject;

import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.jsqlbuilder.WriteQueryBuilder;

public interface Field {
	public void toJSON(JSONObject json, QueryResult result, FieldData fieldData) throws SQLException;
	public void toData(WriteQueryBuilder writeBuilder, JSONObject json, FieldData fieldData);
}