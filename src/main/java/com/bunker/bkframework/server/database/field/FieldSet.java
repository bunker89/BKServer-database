package com.bunker.bkframework.server.database.field;

public class FieldSet {
	public Field field;
	public FieldData fieldData;
	
	public FieldSet(FieldData fieldData, Field field) {
		this.field = field;
		this.fieldData = fieldData;
	}
}
