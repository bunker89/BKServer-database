package com.bunker.bkframework.server.database.field;

import com.google.common.base.CaseFormat;

public class FieldData {
	public final String packetField;
	public final String storageField;
	
	public FieldData(String packetField, String dataField) {
		this.packetField = packetField;
		this.storageField = dataField;
	}
	
	public FieldData(String commomField, String dataPrefix, byte c) {
		this(commomField, dataPrefix + "." + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, commomField));
	}
	
	public FieldData(String commomField) {
		this(commomField, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, commomField));
	}
}