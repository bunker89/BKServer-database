package com.bunker.bkframework.server.database.field;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.bunker.bkframework.server.database.DatabaseHelper.QueryResult;
import com.bunker.jsqlbuilder.WriteQueryBuilder;

public class JSONObjectField implements Field {
	FieldSet[] fields;
	
	public static class Builder {
		List<FieldSet> fields = new LinkedList<>();
		
		public Builder pushField(FieldData fieldData, Field field) {
			FieldSet fieldSet = new FieldSet(fieldData, field);
			fields.add(fieldSet);
			return this;
		}
		
		public JSONObjectField build() {
			JSONObjectField jsonField = new JSONObjectField();
			jsonField.fields = (FieldSet[]) fields.toArray();
			return jsonField;
		}
	}
	
	private JSONObjectField() {
	}
	
	public JSONObjectField(FieldSet[] fields) {
		this.fields = fields;
	}
	
	@Override
	public void toJSON(JSONObject json, QueryResult result, FieldData fieldData) throws SQLException {
		JSONObject subJSON = new JSONObject();
		for (FieldSet set : fields) {
			set.field.toJSON(subJSON, result, set.fieldData);
		}
		json.put(fieldData.packetField, subJSON);
	}
	
	@Override
	public void toData(WriteQueryBuilder writeBuilder, JSONObject json, FieldData fieldData) {
		JSONObject subJSON = json.getJSONObject(fieldData.packetField);
		for (FieldSet set : fields) {
			set.field.toData(writeBuilder, subJSON, set.fieldData);
		}
	}
}