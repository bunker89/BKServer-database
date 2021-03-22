package com.bunker.bkframework.server.database.field;

import java.sql.SQLException;

import org.json.JSONObject;

import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.jsqlbuilder.WriteQueryBuilder;

public class DoubleField implements Field {
	public static final DoubleField instance = new DoubleField();
	
	@Override
	public void toJSON(JSONObject json, QueryResult result, FieldData fieldData) throws SQLException {
		json.put(fieldData.packetField, result.set.getDouble(fieldData.storageField));
	}
	
	@Override
	public void toData(WriteQueryBuilder writeBuilder, JSONObject json, FieldData fieldData) {
		writeBuilder.insertField(fieldData.storageField, json.getDouble(fieldData.packetField) + "");
	}
}