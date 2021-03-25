package com.bunker.bkframework.server.database.field;

import com.google.common.base.CaseFormat;

public class FieldData {
	public final String packetField;
	public final String storageField;
	
	public FieldData(String packetField, String dataField) {
		this.packetField = packetField;
		this.storageField = dataField;
	}
	
	public FieldData(String commonField, String dataPrefix, byte c) {
		this(commonField, dataPrefix + "." + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, commonField));
	}
	
	public FieldData(String commonField) {
		this(commonField, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, commonField));
	}
}