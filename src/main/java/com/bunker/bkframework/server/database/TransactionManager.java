package com.bunker.bkframework.server.database;

import java.util.LinkedList;
import java.util.List;

public class TransactionManager {
	List<UpdateTransaction> transactions = new LinkedList<UpdateTransaction>();
	
//	String singleParamId;
	Object singleParamValue;
	
	public void addTransaction(UpdateTransaction transaction) {
		transactions.add(transaction);
	}
	
	public void setSingleDynamicValue(String id, Object value) {
		//TODO implements multiple tynamic value
		singleParamValue = value;
	}
	
	public Object getSingleDynamicValue(String id) {
		return singleParamValue;
	}
	
	int size() {
		return transactions.size();
	}
	
	public List<UpdateTransaction> getTransactions() {
		return transactions;
	}
}