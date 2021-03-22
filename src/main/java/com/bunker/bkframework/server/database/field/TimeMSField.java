package com.bunker.bkframework.server.database.field;

import java.util.Calendar;

import org.json.JSONObject;

import com.bunker.jsqlbuilder.WriteQueryBuilder;

public class TimeMSField extends LongField {
	public static TimeMSField instance = new TimeMSField();

	@Override
	public void toData(WriteQueryBuilder writeBuilder, JSONObject json, FieldData fieldData) {
		writeBuilder.insertField(fieldData.storageField, Calendar.getInstance().getTimeInMillis());
	}
}