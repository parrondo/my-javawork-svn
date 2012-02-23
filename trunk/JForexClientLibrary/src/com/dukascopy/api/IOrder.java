/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.concurrent.TimeUnit;

/**
 * Contains order data and allows order manipulation
 * 
 * @author Denis Larka
 */
public interface IOrder {

    /**
     * Indicates state of the order
     */
    enum State {
        /**
         * Set right after order submission and before order acceptance by the server
         */
        CREATED,
        
        /**
         * Set after order submission for conditional orders. Simple BUY or SELL orders can have this state before filling or can get FILLED
         * state right after CREATED state
         */
        OPENED,
        
        /**
         * Set after order was fully or partially filled. Partially filled orders have different values returned from getRequestedAmount
         * and getAmount methods
         */
        FILLED,
        
        /**
         * Set after order was closed
         */
        CLOSED,
        
        /**
         * Set after order was canceled
         */
        CANCELED
    }

    /**
     * Returns instrument of the order
     * 
     * @return instrument
     */
    public Instrument getInstrument();

    /**
     * Returns label
     * 
     * @return label
     */
    public String getLabel();
    
    /**
     * Returns position or entry ID
     * 
     * @return id
     */
    public String getId();
    
    

    /**
     * Returns creation time. This is the time when server accepted the order, not the time when it was submitted
     * 
     * @return creation time
     */
    public long getCreationTime();
    
    /**
     * Returns time when server closed the order
     * 
     * @return time when order was closed
     */
    public long getCloseTime();
    
    /**
     * Returns {@link IEngine.OrderCommand} of this message
     * 
     * @return order command of this message
     */
    public IEngine.OrderCommand getOrderCommand();
    
    /**
     * Returns true if order is LONG. Equals to <code>getOrderCommand().isLong()</code>
     * 
     * @return true if order is LONG
     */
    public boolean isLong();

    /**
     * Returns time of the fill
     * 
     * @return time of the order fill
     */
    public long getFillTime();

    /**
     * Returns amount of the order. For orders in {@link State#OPENED} state returns amount requested. For orders in {@link State#FILLED}
     * state will return filled amount. Filled amount can be different from requested amount (partial fill).
     * 
     * @return amount of the order
     */
    public double getAmount();
    
    /**
     * Returns requested amount
     * 
     * @return amount requested
     */
    public double getRequestedAmount();

    /**
     * Returns entry level price for conditional orders in {@link State#CREATED} and {@link State#OPENED} state or price at which order was
     * filled for orders in {@link State#FILLED} or {@link State#CLOSED} states
     * 
     * @return entry level for conditional orders or open price for positions
     */
    public double getOpenPrice();

    /**
     * Returns price at which order was closed or 0 if order or order part wasn't closed. This is the price of latest close operation in case
     * of partial close
     * 
     * @return close price for closed orders, return 0 for any other type of order
     */
    public double getClosePrice();

    /**
     * Returns price of stop loss condition or 0 if stop loss condition is not set. Orders submitted with stop loss condition, will have this
     * price set only after server accepts order
     * 
     * @return stop loss price or 0
     */
    double getStopLossPrice();

    /**
     * Returns price of take profit condition or 0 if take profit condition is not set. Orders submitted with take profit condition,
     * will have this price set only after server accepts order
     * 
     * @return take profit price or 0
     */
    double getTakeProfitPrice();

    /**
     * Sets stop loss price. If price is 0, then stop loss condition will be removed. Default stop loss side is BID for long orders and ASK
     * for short. This method will send command to the server, {@link #getStopLossPrice()} method will still return old value until server
     * will accept this changes
     * 
     * @param price price to set
     * @throws JFException when method fails for some reason
     */
    void setStopLossPrice(double price) throws JFException;

    /**
     * Sets stop loss price. If price is 0, then stop loss condition will be removed. This method will send command to the server,
     * {@link #getStopLossPrice()} method will still return old value until server will accept this changes
     * 
     * @param price price to set
     * @param side side that will be used to check stop loss condition
     * @throws JFException when method fails for some reason
     */
    public void setStopLossPrice(double price, OfferSide side) throws JFException;
    
    /**
     * Sets stop loss price. If price is 0, then stop loss condition will be removed. If trailingStep is bigger than 10, then trailing step
     * logic will be applied for stop loss price. This method will send command to the server, {@link #getStopLossPrice()} method will still
     * return old value until server will accept this changes
     * 
     * @param price price to set
     * @param side side that will be used to check stop loss condition
     * @param trailingStep if < 0 then adds stop loss order without trailing step. Should be 0 or >= 10
     * @throws JFException trailingStep is > 0 and < 10 or when method fails for some reason
     */
    public void setStopLossPrice(double price, OfferSide side, double trailingStep) throws JFException;
    
    
    /**
     * Returns side that is used to check stop loss condition
     * 
     * @return stop loss side
     */
    public OfferSide getStopLossSide();

    /**
     * Returns current trailing step or 0 if no trailing step is set
     * 
     * @return trailing step value
     */
    public double getTrailingStep();
    
    /**
     * Sets take profit price. If price is 0, then take profit condition will be removed. This method will send command to the server,
     * {@link #getTakeProfitPrice()} method will still return old value until server will accept this changes
     * 
     * @param price price to set
     * @throws JFException when method fails for some reason
     */
    void setTakeProfitPrice(double price) throws JFException;
    
    /**
     * Returns comment that was set when order was submitted
     * 
     * @return comment
     */
    public String getComment();

    /**
     * Sets amount of order in {@link State#OPENED} state or cancels pending part of partially
     * filled order when amount equals to zero is set
     * 
     * @param amount new amount
     * @throws JFException if order is not in {@link State#OPENED} state or amount is not 0
     * for {@link State#FILLED} state or if amount is less than a minimum allowed
     */
    public void setRequestedAmount(double amount) throws JFException;

    /**
     * Sets open price for order in {@link State#OPENED} state
     * 
     * @param price price of the opening condition
     * @throws JFException when price change fails
     */
    public void setOpenPrice(double price) throws JFException;

    /**
     * Sends a request to close the position with specified amount, price and slippage.
     * 
     * @param amount closing amount. Can be less than opened amount, in this case partial close will take place. If 0 is provided then all
     *              amount will be closed
     * @param price required close price. Close will be rejected if no liquidity at this price. This parameter doesn't affect 
     *              entry (conditional) orders.
     * @param slippage required price slippage. 
     * @throws JFException when called for order not in {@link State#FILLED} state
     */
    public void close(double amount, double price, double slippage) throws JFException;

    /**
     * Sends a request to close the position with specified amount, price and default slippage.
     * If order has both pending and filled parts and amount is greater than 0, only filled part will be closed.
     * If amount is 0 then both filled and pending parts will be closed/canceled
     *
     * @param amount closing amount. Can be less than opened amount, in this case partial close will take place. If 0 is provided then all
     *              amount will be closed
     * @param price required close price. Close will be rejected if no liquidity at this price. This parameter doesn't affect 
     *              entry (conditional) orders.
     * @throws JFException when called for order not in {@link State#FILLED} state
     */
    public void close(double amount, double price)throws JFException;

    /**
     * Sends a request to close the position with specified amount, by market price and default slippage.
     * If order has both pending and filled parts and amount is greater than 0, only filled part will be closed.
     * If amount is 0 then both filled and pending parts will be closed/canceled
     *
     * @param amount closing amount. Can be less than opened amount, in this case partial close will take place. If 0 is provided then all
     *              amount will be closed
     * @throws JFException when called for order not in {@link State#FILLED} state
     */
    public void close(double amount) throws JFException;

    /**
     * Sends a request to fully close position by market price or cancel entry order.
     * If order has both pending and filled parts, both will be closed/canceled
     * 
     * This is the only allowed {@link #close()} method for entry orders 
     */
    public void close()throws JFException;

    /**
     * Returns current {@link State} of the order
     * 
     * @return state
     */
    public State getState();

    /**
     * Sets "good till time" for BIDs and OFFERs
     * 
     * @param goodTillTime time when BID or OFFER should be canceled
     * @throws JFException when order is not place bid or offer
     */
    public void setGoodTillTime(long goodTillTime) throws JFException;
    
    /**
     * Returns time when order will be cancelled or 0 if order is "good till cancel"
     * 
     * @return cancel time or 0
     */
    public long getGoodTillTime();
    
    /**
     * Blocks until order changes it's state, values or until timeout is elapsed (unblocks when system receives any message related to this order).
     * All the ticks and bars that platform received while waiting will be dropped. Method shouldn't be used for waiting
     * long running events, like closing the order by stop loss, else buffer with
     * messages will overflow and strategy will be stopped.
     *
     * @param timeoutMills timeout to wait for order state change
     */
    public void waitForUpdate(long timeoutMills);

    /**
     * Blocks until order changes it's state, values or until timeout is elapsed (unblocks when system receives any message related to this order).
     * All the ticks and bars that platform received while waiting will be dropped. Method shouldn't be used for waiting
     * long running events, like closing the order by stop loss, else buffer with
     * messages will overflow and strategy will be stopped. Method returns message related to order update, which can be especially useful when
     * order state doesn't change (operation was rejected)
     *
     * @param timeout how long to wait before giving up, in units of
     *        <tt>unit</tt>
     * @param unit a <tt>TimeUnit</tt> determining how to interpret the
     *        <tt>timeout</tt> parameter
     * @return message generated as the result of order update or null if method exited by timeout
     */
    public IMessage waitForUpdate(long timeout, TimeUnit unit);
    
    
    /**
     * Blocks until order changes it's state value to one of the expected states (or to any value if expected states are not specified).<br/>
     * (unblocks when system receives message related to this order which changes order's state to one of expected).<br/>
     * All the ticks and bars that platform received while waiting will be dropped.<br/> 
     * Method shouldn't be used for waiting long running events, like closing the order by stop loss, else buffer with messages will overflow and strategy will be stopped. <br/>
     * The real time is not taken into consideration and the order simply waits until its state changes to one of the specified expected states or<br/>
     * {@link JFException} will be thrown due to order is in state that cannot be changed to one of expected ones.
     *
     * @param timeoutMills timeout to wait for order state change
     * @param states a set of expected states. If null or empty - blocks until order changes it's state to any value. 
     * 
     * @throws JFException when order is in state that cannot be changed to one of expected states
     * @return message generated as the result of order update. Can be null if method exited by timeout
     */
    public IMessage waitForUpdate(State ...states) throws JFException;
    
    /**
     * Blocks until order changes it's state value to one of the expected states (or to any value if expected states are not specified).<br/>
     * (unblocks when system receives message related to this order which changes order's state to one of expected).<br/>
     * All the ticks and bars that platform received while waiting will be dropped.<br/> 
     * Method shouldn't be used for waiting long running events, like closing the order by stop loss, else buffer with messages will overflow and strategy will be stopped. <br/>
     *
     * @param timeoutMills timeout to wait for order state change
     * @param states a set of expected states. If null or empty - blocks until order changes it's state to any value. 
     * 
     * @throws JFException when order is in state that cannot be changed to one of expected states
     * @return message generated as the result of order update. Can be null if method exited by timeout
     */
    public IMessage waitForUpdate(long timeoutMills, State ...states) throws JFException;
    
    /**
     * Blocks until order changes it's state value to one of the expected states (or to any value if expected states are not specified).<br/>
     * (unblocks when system receives message related to this order which changes order's state to one of expected).<br/>
     * All the ticks and bars that platform received while waiting will be dropped.<br/>
     * Method shouldn't be used for waiting long running events, like closing the order by stop loss, else buffer with messages will overflow and strategy will be stopped. <br/>
     * Method returns message related to order update.
     *
     * @param timeout how long to wait before giving up, in units of
     *        <tt>unit</tt>
     * @param unit a <tt>TimeUnit</tt> determining how to interpret the
     *        <tt>timeout</tt> parameter.
     * @param states a set of expected states. If null or empty - blocks until order changes it's state to any value. 
     * @throws JFException when order is in state that cannot be changed to one of expected states        
     * @return message generated as the result of order update. Can be null if method exited by timeout
     */
    public IMessage waitForUpdate(long timeout, TimeUnit unit, State ...states) throws JFException;

    /**
     * Returns profit/loss in pips
     *
     * @return profit/loss in pips
     */
    public double getProfitLossInPips();

    /**
     * Returns profit/loss in USD
     *
     * @return profit/loss in USD
     */
    public double getProfitLossInUSD();

    /**
     * Returns profit/loss in account currency
     *
     * @return profit/loss in account currency
     */
    public double getProfitLossInAccountCurrency();
    
    /**
     * Return order commission in account currency.
     * @return order commission in account currency
     */
    public double getCommission();
    
    /**
     * Return order commission in USD
     * @return order commission in USD
     */
    public double getCommissionInUSD();
    
    /**
     * Compares order to current one
     * @param order to compare with current
     * @return true if all fields are equal
     */
    public boolean compare(IOrder order);
}
