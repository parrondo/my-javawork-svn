/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Interface that all strategies should implement
 * 
 * @author Denis Larka
 */
public interface IStrategy {

    /**
     * Called on strategy start
     * 
     * @param context allows access to all system functionality
     * @throws JFException when strategy author ignores exceptions 
     */
    void onStart(IContext context) throws JFException;

    /**
     * Called on every tick of every instrument that application is subscribed on
     * 
     * @param instrument instrument of the tick
     * @param tick tick data
     */
    void onTick(Instrument instrument, ITick tick) throws JFException;

    /**
     * Called on every bar for every basic period and instrument that application is subscribed on
     * 
     * @param instrument instrument of the bar
     * @param period period of the bar
     * @param askBar bar created of ask side of the ticks
     * @param bidBar bar created of bid side of the ticks
     */
    void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException;

    /**
     * Called when new message is received
     * 
     * @param message message
     */
    void onMessage(IMessage message) throws JFException;

    /**
     * Called when account information update is received
     * 
     * @param account updated account information
     */
    void onAccount(IAccount account) throws JFException;
    
    /**
     * Called before strategy is stopped
     */
    void onStop() throws JFException;
}
