package com.bunker.bkframework.server.database.field;

import java.sql.SQLException;

import org.json.JSONObject;

import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.jsqlbuilder.WriteQueryBuilder;

public class StringField implements Field {
	public static final StringField instance = new StringField();
	
	@Override
	public void toJSON(JSONObject json, QueryResult result, FieldData fieldData) throws SQLException {
		json.put(fieldData.packetField, result.set.getString(fieldData.storageField));
	}
	
	@Override
	public void toData(WriteQueryBuilder writeBuilder, JSONObject json, FieldData fieldData) {
		writeBuilder.insertFieldWrap(fieldData.storageField, json.getString(fieldData.packetField));
	}
}