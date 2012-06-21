/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.List;

/**
 * Interface to the main engine, that allows orders submission, merging etc
 * 
 * @author Denis Larka
 */
public interface IEngine {

    /**
     * Type of the engine
     */
    enum Type {
        LIVE, DEMO, TEST
    }

    /**
     * Specifies type of the order
     */
    enum OrderCommand {
        /**
         *  Buy by current market price. You can specify price and slippage, if current market price at execution moment
         *  (when order reaches server) is worse than specified price, and slippage is not big enough to execute order by current market price,
         *  then order will be rejected
         */
        BUY,
        /**
         *  Sell by current market price. You can specify price and slippage, if current market price at execution moment
         *  (when order reaches server) is worse than specified price, and slippage is not big enough to execute order by current market price,
         *  then order will be rejected
         */
        SELL,
        /**
         * Buy when ask price is <= specified price
         */
        BUYLIMIT,
        /**
         * Sell when bid price is >= specified price
         */
        SELLLIMIT,
        /**
         * Buy when ask price is >= specified price
         */
        BUYSTOP,
        /**
         * Sell when bid price is <= specified price
         */
        SELLSTOP,
        /**
         * Buy when bid price is <= specified price
         */
        BUYLIMIT_BYBID,
        /**
         * Sell when ask price is >= specified price
         */
        SELLLIMIT_BYASK,
        /**
         * Buy when bid price is >= specified price
         */
        BUYSTOP_BYBID,
        /**
         * Sell when ask price is <= specified price
         */
        SELLSTOP_BYASK,
        /**
         * Place bid at specified price
         */
        PLACE_BID,
        /**
         * Place offer at specified price
         */
        PLACE_OFFER;

        /**
         * Returns true if order is LONG and false if order is SHORT
         * @return true if order is LONG and false if order is SHORT
         */
        public boolean isLong() {
            return this == BUY || this == BUYLIMIT || this == BUYSTOP || this == BUYLIMIT_BYBID || this == BUYSTOP_BYBID || this == PLACE_BID;
        }

        /**
         * Returns true if order is SHORT and false if order is LONG
         * @return true if order is SHORT and false if order is LONG
         */
        public boolean isShort() {
            return !isLong();
        }
        
        /**
         * Returns true if order is one of STOP or LIMIT orders
         * 
         * @return true if STOP or LIMIT order, false otherwise
         */
        public boolean isConditional() {
            return this != BUY && this != SELL && this != PLACE_BID && this != PLACE_OFFER; 
        }
        
        /**
         * Returns OrderCommand object converted from Metatrader long value
         * 	
	     * P_BUY = 0;// Buying position.
	     * OP_SELL = 1;// Selling position.
	     * OP_BUYLIMIT = 2;// buy ask<
	     * OP_SELLLIMIT = 3;// sell bid>
	     * OP_BUYSTOP = 4;// buy ask>.
	     * OP_SELLSTOP = 5;// sell bid<
     	 * OP_BUYLIMIT_BYBID = 6;// buy bid<
	     * OP_SELLLIMIT_BYASK = 7;// sell ask>
	     * OP_BUYSTOP_BYBID = 8;// buy bid>
	     * OP_SELLSTOP_BYASK = 9;// sell ask<
	     * 
         * @return OrderCommand
         */
        public static OrderCommand getValue(long mtValue) {
        	Long index = new Long(mtValue);
        	OrderCommand returnValue = OrderCommand.values()[index.intValue()];
        	return returnValue;
        }
    }

    /**
     * Specifies strategy running mode.
     * 
     */
    enum StrategyMode {
        
        /**
         * Strategy works as usual - it can create, close, modify etc orders on it's own 
         */
        INDEPENDENT("independent"),
        
        /**
         * Strategy does not create, close or modify orders.
         * It can only signalize about intention to make any activity.  
         */
        SIGNALS("signals");
        
        private String modeString;

        public static StrategyMode fromString(String modeString) {
            for (StrategyMode mode : StrategyMode.values()) {
                if (mode.modeString.equalsIgnoreCase(modeString)) {
                    return mode;
                }
            }
            
            return null;
        } 
        
        private StrategyMode(String modeString) {
            this.modeString = modeString;
        }

        public String getModeString() {
            return modeString;
        }
    }    
    
    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <tt>Double.isNaN(slippage) == true</tt> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @param stopLossPrice price of the stop loss. Price should be divisible by 0.1 pips or order will be rejected
     * @param takeProfitPrice price of the take profit. Price should be divisible by 0.1 pips or order will be rejected
     * @param goodTillTime how long order should live if not executed. Only if > 0, then orderCommand should be
     *          {@link IEngine.OrderCommand#PLACE_BID} or {@link IEngine.OrderCommand#PLACE_OFFER}
     * @param comment comment that will be saved in order
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if goodTillTime > 0 and orderCommand is not BID/OFFER, if amount is less
     *          than minimum allowed, if some of the required parameters is null
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime, String comment) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <tt>Double.isNaN(slippage) == true</tt> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @param stopLossPrice price of the stop loss. Price should be divisible by 0.1 pips or order will be rejected
     * @param takeProfitPrice price of the take profit. Price should be divisible by 0.1 pips or order will be rejected
     * @param goodTillTime how long order should live if not executed. Only if > 0, then orderCommand should be
     *          {@link IEngine.OrderCommand#PLACE_BID} or {@link IEngine.OrderCommand#PLACE_OFFER}
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if goodTillTime > 0 and orderCommand is not BID/OFFER, if amount is less
     *          than minimum allowed, if some of the required parameters is null
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <tt>Double.isNaN(slippage) == true</tt> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @param stopLossPrice price of the stop loss. Price should be divisible by 0.1 pips or order will be rejected
     * @param takeProfitPrice price of the take profit. Price should be divisible by 0.1 pips or order will be rejected
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if amount is less than minimum allowed, if some of the required
     *          parameters is null
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <tt>Double.isNaN(slippage) == true</tt> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if amount is less than minimum allowed, if some of the required
     *          parameters is null
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount, double price, double slippage) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     * <br/>
     * <b>Note:</b> default value of 5 pips slippage is used. To specify custom slippage, or disable slippage at all, please use extended <code>submitOrder(...)</code> methods.
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double, long)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double, long, String)
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders. 
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if amount is less than minimum allowed, if some of the required
     *          parameters is null
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount, double price) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     * <br/>
     * <b>Note:</b> default value of 5 pips slippage is used. To specify custom slippage, or disable slippage at all, please use extended <code>submitOrder(...)</code> methods.
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double, long)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double, long, String)
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order. Only {@link IEngine.OrderCommand#BUY} and {@link IEngine.OrderCommand#SELL} allowed in this method
     * @param amount amount in millions for the order
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if amount is less than minimum allowed, if some of the required
     *          parameters is null or if orderCommand is not BUY or SELL
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount) throws JFException;

    /**
     * Returns order by label, or null if no order was found
     * 
     * @param label order's label
     * @return order in {@link IOrder.State#CREATED}, {@link IOrder.State#OPENED} and {@link IOrder.State#FILLED} state or null
     * @throws JFException 
     */
    IOrder getOrder(String label) throws JFException;
    
    /**
     * Returns order in {@link IOrder.State#CREATED}, {@link IOrder.State#OPENED} and {@link IOrder.State#FILLED} state by id, or null if no order was found 
     * 
     * @param orderId order's id
     * @return order or null.
     */
    IOrder getOrderById(String orderId);

    /**
     * Returns list of orders in {@link IOrder.State#CREATED}, {@link IOrder.State#OPENED} and {@link IOrder.State#FILLED} state for
     * specified instrument
     * 
     * @param instrument instrument
     * @return list of orders
     * @throws JFException 
     */
    List<IOrder> getOrders(Instrument instrument) throws JFException;

    /**
     * Returns list of orders in {@link IOrder.State#CREATED}, {@link IOrder.State#OPENED} and {@link IOrder.State#FILLED} state
     * 
     * @return list of orders
     * @throws JFException 
     */
    List<IOrder> getOrders() throws JFException;
    
    /**
     * Merges orders. Merge process closes all passed in parameter orders notifying strategy with appropriate onMessage event. If there is
     * some amount left, opens new order.
     * Finally notifies strategy calling onMessage with message with {@link IMessage.Type#ORDERS_MERGE_OK} type and resulting order or
     * null if there is no amount left for order. If there is less than 2 orders, then method exits after checking conditions, which are one
     * instrument, {@link IOrder.State#FILLED} state and no stop loss/take profit conditions
     * 
     * @param orders orders to merge
     * @throws JFException if orders not in {@link IOrder.State#FILLED} state, have stop loss or take profit set or doesn't belong to one instrument
     * 
     * @deprecated use mergeOrders(String label,IOrder... orders) throws JFException;
     * 
     */
    void mergeOrders(IOrder... orders) throws JFException;
    
    /**
     * Merges orders. Merge process closes all passed in parameter orders notifying strategy with appropriate onMessage event. If there is
     * some amount left, opens new order with specified label.
     * Finally notifies strategy calling onMessage with message with {@link IMessage.Type#ORDERS_MERGE_OK} type and resulting order, which can
     * be in closed state if there is no amount left for order. If there is less than 2 orders, then method exits after checking conditions,
     * which are one instrument, {@link IOrder.State#FILLED} state and no stop loss/take profit conditions
     * 
     * @param label user defined identifier for the resulting order. Label must be unique for the given user account among the current orders
     * @param orders orders to merge
     * @return resulting order in CREATED state
     * @throws JFException if orders not in {@link IOrder.State#FILLED} state, have stop loss or take profit set or doesn't belong to one instrument
     */
    IOrder mergeOrders(String label, IOrder... orders) throws JFException;
    
    /**
     * Mass close. Closes all orders passed in parameter(s)
     * 
     * @param orders orders to close
     * @throws JFException if orders not in {@link IOrder.State#FILLED} state.
     */
    void closeOrders(IOrder... orders) throws JFException;
    
    /**
     * Returns type of the engine, one of the {@link IEngine.Type#LIVE}, {@link IEngine.Type#DEMO} or {@link IEngine.Type#TEST} for tester.
     * 
     * @return type of the engine
     */
    Type getType();
    
    /**
     * Returns account name
     * 
     * @return account name
     */
    String getAccount();

    /**
     * Broadcast message
     */
    void broadcast(String topic, String message) throws JFException;
    
    String groupToOCO(IOrder order1, IOrder order2) throws JFException;
    
    String ungroupOCO(IOrder order) throws JFException;

    /**
     * Returns strategy running mode, one of the {@link IEngine.StrategyMode#INDEPENDENT} or {@link IEngine.StrategyMode#SIGNALS}.
     * 
     * @return strategy running mode
     */
	StrategyMode getStrategyMode();
}