package com.bunker.bkframework.server.database.field;

public class FieldSet {
	public Field field;
	public FieldData fieldData;
	public boolean optional;
	
	public FieldSet(FieldData fieldData, Field field, boolean optional) {
		this.field = field;
		this.fieldData = fieldData;
		this.optional = optional;
	}
}
