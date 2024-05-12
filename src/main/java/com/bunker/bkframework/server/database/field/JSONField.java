package com.bunker.bkframework.server.database.field;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.jsqlbuilder.WriteQueryBuilder;
import org.json.JSONObject;

import java.sql.SQLException;

public class JSONField implements Field {
	public static final JSONField instance = new JSONField();

	@Override
	public void toJSON(JSONObject json, QueryResult result, FieldData fieldData) throws SQLException {
		String jsonString = result.set.getString(fieldData.storageField);
		if (jsonString != null) {
			try {
				JSONObject data = new JSONObject(jsonString);
				json.put(fieldData.packetField, data);
			} catch (Exception e) {
				Logger.err("JSONField", "json parse error" + "->" + jsonString, e);
			}
		}
	}

	@Override
	public void toData(WriteQueryBuilder writeBuilder, JSONObject json, FieldData fieldData) {
		writeBuilder.insertFieldWrap(fieldData.storageField, json.getJSONObject(fieldData.packetField).toString().replace("\\", "\\\\"));
	}
}