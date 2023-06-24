package com.bunker.bkframework.server.database.field;

import org.json.JSONArray;
import org.json.JSONObject;

public class WorkingModelParser {
	public FieldSet[] parse(JSONObject fieldJSON) {
		JSONArray commonArray = fieldJSON.has("common") ? fieldJSON.getJSONArray("common") 
				: new JSONArray();
		JSONArray differentArray = fieldJSON.has("different") ? fieldJSON.getJSONArray("different")
				: new JSONArray();

		FieldSet[] fields = new FieldSet[commonArray.length() + differentArray.length()];
		
		for (int i = 0; i < commonArray.length(); i++) {
			JSONObject json = commonArray.getJSONObject(i);
			FieldData fieldData = new FieldData(json.getString("common"));
			Field field = getField(json, json.getString("type"));
			boolean optional = json.has("optional") ? json.getBoolean("optional") : false;
			fields[i] = new FieldSet(fieldData, field, optional);
		}
		
		int offset = commonArray.length();
		for (int i = 0; i < differentArray.length(); i++) {
			JSONObject json = differentArray.getJSONObject(i);
			Field field = getField(json, json.getString("type"));
			FieldData fieldData = new FieldData(json.getString("packet"), json.getString("storage"));
			boolean optional = json.has("optional") ? json.getBoolean("optional") : false;
			fields[offset + i] = new FieldSet(fieldData, field, optional);
		}
		return fields;
	}
	
	protected Field getField(JSONObject json, String type) {
		switch (type) {
		case "int":
			return IntField.instance;
			case "float":
				return FloatField.instance;
			case "double":
				return DoubleField.instance;
			case "boolean":
				return BooleanField.instance;
			case "string":
				return StringField.instance;
			case "long":
				return LongField.instance;
			case "json":
				return JSONField.instance;
			case "json-child":
				return new JSONObjectField(parse(json.getJSONObject("child")));
			case "genkey":
			return GenKeyField.instance;
		case "time":
			return TimeMSField.instance;
		default:
			throw new RuntimeException("working model parse error");
		}
	}
}