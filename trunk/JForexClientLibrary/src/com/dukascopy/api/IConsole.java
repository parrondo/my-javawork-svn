/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.io.PrintStream;

/**
 * Allows to print messages to Messages table
 * 
 * @author Denis Larka
 */
public interface IConsole {
    /**
     * Returns <code>PrintStream<code> that prints messages with normal priority
     * 
     * @return <code>PrintStream<code> to print messages
     */
    public PrintStream getOut();

    /**
     * Returns <code>PrintStream<code> that prints messages with error priority. Messages are shown in red color
     * 
     * @return <code>PrintStream<code> to print messages
     */
    public PrintStream getErr();
    
    /**
     * Returns <code>PrintStream<code> that prints messages with warning priority. Messages by default are shown in yellow color
     * 
     * @return <code>PrintStream<code> to print messages
     */
    public PrintStream getWarn();
    
    /**
     * Returns <code>PrintStream<code> that prints messages with info priority. Messages by default are shown in green color
     * 
     * @return <code>PrintStream<code> to print messages
     */
    
    public PrintStream getInfo();
    /**
     * Returns <code>PrintStream<code> that prints messages with notification priority. Messages by default are shown in blue color
     * 
     * @return <code>PrintStream<code> to print messages
     */
    
    public PrintStream getNotif();
}
