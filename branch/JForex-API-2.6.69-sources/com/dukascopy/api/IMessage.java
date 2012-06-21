/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Represents message sent from server to client application
 * 
 * @author Denis Larka, Dmitry Shohov
 */
public interface IMessage {
    /**
     * Type of the message
     */
    public static enum Type {

        /**
         * Sent when order was rejected
         */
        ORDER_SUBMIT_REJECTED,

        /**
         * Sent after order submission was accepted by the server
         */
        ORDER_SUBMIT_OK,

        /**
         * Sent if server rejected order fill execution. One of the possible reasons is not enough margin
         */
        ORDER_FILL_REJECTED,

        /**
         * Sent if order close request was rejected
         */
        ORDER_CLOSE_REJECTED,

        /**
         * Sent after successful order closing
         */
        ORDER_CLOSE_OK,

        /**
         * Sent after successful order filling
         */
        ORDER_FILL_OK,

        /**
         * Sent after successful orders merge
         */
        ORDERS_MERGE_OK,

        /**
         * Sent if orders merge was rejected by the server
         */
        ORDERS_MERGE_REJECTED,

        /**
         * Sent after successful orders change
         */
        ORDER_CHANGED_OK,

        /**
         * Sent if orders change was rejected by the server
         */
        ORDER_CHANGED_REJECTED,
        /**
         * Message sent from broker
         */
        MAIL,

        /**
         * Market news
         */
        NEWS,
        /**
         * Market calendar
         */
        CALENDAR,
        /**
         * Notifications from server or events in the system like disconnect
         */
        NOTIFICATION,

        /**
         * Sent if system changes instrument status
         */
        INSTRUMENT_STATUS,

        /**
         * Sent when connection status changes
         */
        CONNECTION_STATUS,

        /**
         * Sent by strategy 
         */
        STRATEGY_BROADCAST,
        
        /**
         * Notifies that an order is to be sent to the server
         */
        SENDING_ORDER,

        
        /**
         * Client's stop loss level changed message 
         */
        STOP_LOSS_LEVEL_CHANGED,
        
        /**
         * Client's withdrawal message
         */
        WITHDRAWAL
    }

    /**
     * Returns type of the message
     * 
     * @return type of the message
     */
    public Type getType();

    /**
     * Returns textual content
     * 
     * @return string content of given message
     */
    String getContent();

    /**
     * Returns {@link IOrder} linked with this message or null if there is no related order
     * 
     * @return {@link IOrder} linked with this message. Can be null
     */
    IOrder getOrder();

    /**
     * Returns time when message was created. If it was created on server then returns exact time when it was created on server
     *
     * @return time when message was created
     */
    long getCreationTime();
}
