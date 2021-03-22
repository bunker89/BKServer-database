package com.bunker.bkframework.server.database.field;

import java.sql.SQLException;

import org.json.JSONObject;

import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.jsqlbuilder.WriteQueryBuilder;

public class FloatField implements Field {
	public static final FloatField instance = new FloatField();
	
	@Override
	public void toJSON(JSONObject json, QueryResult result, FieldData fieldData) throws SQLException {
		json.put(fieldData.packetField, result.set.getFloat(fieldData.storageField));
	}
	
	@Override
	public void toData(WriteQueryBuilder writeBuilder, JSONObject json, FieldData fieldData) {
		writeBuilder.insertField(fieldData.storageField, json.getFloat(fieldData.packetField) + "");
	}
}