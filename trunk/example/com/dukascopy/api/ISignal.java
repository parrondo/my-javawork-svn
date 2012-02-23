package com.dukascopy.api;

public interface ISignal {

	public enum Type {
		ORDER_BUY, ORDER_SELL, ORDER_CLOSE, ORDER_MODIFY, ORDER_MERGE, ORDER_CANCEL;
	}
	
	public IOrder getOrder();

	public ISignal.Type getType();	
}
