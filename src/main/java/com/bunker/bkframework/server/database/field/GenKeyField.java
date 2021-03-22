package com.bunker.bkframework.server.database.field;

import java.util.UUID;

import org.json.JSONObject;

import com.bunker.jsqlbuilder.WriteQueryBuilder;

public class GenKeyField extends StringField {
	public static final GenKeyField instance = new GenKeyField();
	
	@Override
	public void toData(WriteQueryBuilder writeBuilder, JSONObject json, FieldData fieldData) {
		String key = UUID.randomUUID().toString().replace("-", "");
		writeBuilder.insertField(fieldData.storageField, key);
	}
}