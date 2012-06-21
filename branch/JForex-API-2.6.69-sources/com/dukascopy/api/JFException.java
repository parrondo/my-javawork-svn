/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Used to indicate errors when calling API functions
 * 
 * @author Denis Larka
 */
@SuppressWarnings("serial")
public class JFException extends Exception {

    /**
     * Used to easily create exceptions with appropriate message
     */
    public enum Error {
        LABEL_INCONSISTENT, LABEL_NOT_UNIQUE, ORDER_INCORRECT, INVALID_AMOUNT, CALL_INCORRECT, QUEUE_OVERLOADED, THREAD_INCORRECT,COMMAND_IS_NULL,ZERO_PRICE_NOT_ALLOWED,ORDERS_UNAVAILABLE,ORDER_STATE_IMMUTABLE,INVALID_GTT,ORDER_CANCEL_INCORRECT

    };

    public JFException(Error errorCode) {

        super(getMessageByError(errorCode));
    }

    public JFException(Error errorCode, String message) {

        super(message == null ? getMessageByError(errorCode) : message);
    }

    public JFException(String message, Throwable cause) {
        super(message, cause);
    }

    public JFException(String message) {
        super(message);
    }

    public JFException(Throwable cause) {
        super(cause);
    }

    private static String getMessageByError(Error errorCode) {
        String rc = "Unknown Error";
        switch (errorCode) {
        case LABEL_INCONSISTENT:
            rc = "Label inconsistent (Label must not be null, longer than 64 symbols, contain restricted symbols or start with number)";
            break;
        case LABEL_NOT_UNIQUE:
            rc = "Label not unique. (Order already exists)";
            break;
        case COMMAND_IS_NULL:
            rc = "OrderCommand is null";
            break;
        case ZERO_PRICE_NOT_ALLOWED:
            rc = "Zero price not allowed in this order type";
            break;
        case THREAD_INCORRECT:
            rc = "Incorrect thread";
            break;
        case ORDERS_UNAVAILABLE:
            rc = "Orders data unavailable";
            break;
        case ORDER_STATE_IMMUTABLE:
            rc = "Order state immutable";
            break;
        case INVALID_GTT:
            rc = "Good till time is incorrect";
            break;
        case ORDER_CANCEL_INCORRECT:       	
            rc = "Order cancel with amount not 0, price not 0 or slippage 0 or positive";
            break;
            
            
            
            
            
            
        default:
            break;
        }
        return rc;
    }
}
