package com.bunker.bkframework.server.database.field;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONArrayWrapper extends JSONObject {
	final JSONArray array;
	
	public JSONArrayWrapper() {
		this(new JSONArray());
	}
	
	public JSONArrayWrapper(JSONArray array) {
		this.array = array;
	}
	
	@Override
	public JSONObject put(String key, boolean value) throws JSONException {
		array.put(value);
		return this;
	}
	@Override
	public JSONObject put(String key, int value) throws JSONException {
		array.put(value);
		return this;
	}
	@Override
	public JSONObject put(String key, long value) throws JSONException {
		array.put(value);
		return this;
	}
	@Override
	public JSONObject put(String key, float value) throws JSONException {
		array.put(value);
		return this;
	}

	@Override
	public JSONObject put(String key, double value) throws JSONException {
		array.put(value);
		return this;
	}
	
	@Override
	public JSONObject put(String key, Object value) throws JSONException {
		return super.put(key, value);
	}
}