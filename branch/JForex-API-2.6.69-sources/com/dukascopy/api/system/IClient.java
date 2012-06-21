/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

import com.dukascopy.api.INewsFilter;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.Instrument;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

/**
 * Main entry point for the connection to the server
 *
 * @author Denis Larka, Dmitry Shohov
 */
public interface IClient {
    /**
     * Authenticates and connects to dukascopy servers
     *
     * @param jnlp address of jnlp file, that is used to launch platform
     * @param username user name
     * @param password password
     * @throws JFAuthenticationException authentication error, incorrect username or password,
     *      IP address unrecognized in case of LIVE systems
     * @throws JFVersionException version is blocked on the server, update your libraries
     * @throws Exception all kinds of errors that resulted in exception
     */
    public void connect(String jnlp, String username, String password) throws JFAuthenticationException, JFVersionException, Exception;

    /**
     * Authenticates and connects to dukascopy servers
     *
     * @param jnlp address of jnlp file, that is used to launch platform
     * @param username user name
     * @param password password
     * @param pin pin code generated with the captcha from the last {@link #getCaptchaImage} call
     * @throws JFAuthenticationException authentication error, incorrect username or password,
     *      IP address unrecognized in case of LIVE systems
     * @throws JFVersionException version is blocked on the server, update your libraries
     * @throws Exception all kinds of errors that resulted in exception
     */
    public void connect(String jnlp, String username, String password, String pin) throws JFAuthenticationException, JFVersionException, Exception;
    
    /**
     * Returns the image that can be provided to the user to generate correct pin code
     *
     * @param jnlp address of jnlp file, that is used to launch platform
     * @return captha image
     * @throws Exception if request for captcha failed
     */
    public BufferedImage getCaptchaImage(String jnlp) throws Exception;

    /**
     * Tries to reconnect transport without reauthenticating. Method is asynchronous, meaning it will exit immidiately
     * after sending connection request without waiting for the response. Caller will receive notification through
     * {@link ISystemListener} interface
     */
    public void reconnect();
    
    /**
     * Stops all running strategies and disconnect from dukascopy server.
     */
    public void disconnect();

    /**
     * Returns true if client is authenticated authorized and transport is in connected state
     *
     * @return true if there is open and working connection to the server
     */
    public boolean isConnected();

    /**
     * Starts the strategy with default exception handler that will stop strategy if it trows exception
     *
     * @param strategy strategy to run
     * @return returns id assigned to the strategy
     * @throws IllegalStateException if not connected
     * @throws NullPointerException if one of the parameters is null
     */
    public long startStrategy(IStrategy strategy) throws IllegalStateException, NullPointerException;

    /**
     * Starts the strategy
     *
     * @param strategy strategy to run
     * @param exceptionHandler if not null then passed exception handler will be called when strategy throws exception
     * @return returns id assigned to the strategy
     * @throws IllegalStateException if not connected
     * @throws NullPointerException if one of the parameters is null
     */
    public long startStrategy(IStrategy strategy, IStrategyExceptionHandler exceptionHandler) throws IllegalStateException, NullPointerException;

    /**
     * Loads strategy from jfx file
     *
     * @param strategyBinaryFile jfx file
     * @return loaded strategy
     * @throws Exception if loading failed
     */
    public IStrategy loadStrategy(File strategyBinaryFile) throws Exception;

    /**
     * Stops the strategy with the specified id
     *
     * @param processId id of the strategy
     */
    public void stopStrategy(long processId);

    /**
     * Returns map with ids mapped to associated strategies
     * 
     * @return started strategies
     */
    public Map<Long, IStrategy> getStartedStrategies();

    /**
     * Sets the listener, that will receive notifications about connects disconnects and strategies starts and stops
     *
     * @param systemListener listener
     */
    public void setSystemListener(ISystemListener systemListener);

    /**
     * Adds news filter
     *
     * @param newsFilter news filter
     */
    public void addNewsFilter(INewsFilter newsFilter);

    /**
     * Returns news filter for the source
     *
     * @param newsSource news source
     * @return news filter
     */
    public INewsFilter getNewsFilter(INewsFilter.NewsSource newsSource);

    /**
     * Removes news filter, resetting it to the default value
     *
     * @param newsSource news source
     * @return news filter removed
     */
    public INewsFilter removeNewsFilter(INewsFilter.NewsSource newsSource);

    /**
     * Subscribes to the specified instruments set.
     * Ticks passed in onTick method will have full depth for this instruments, while other instruments are not guaranteed to have full depth.
     *
     * @param instruments set of the instruments
     */
    public void setSubscribedInstruments(Set<Instrument> instruments);

    /**
     * Returns subscribed instruments
     *
     * @return set of the subscribed instruments
     */
    public Set<Instrument> getSubscribedInstruments();

    /**
     * Sets stream that will be passed to the strategy through IConsole. Default out is System.out
     *
     * @param out stream
     */
    public void setOut(PrintStream out);

    /**
     * Sets stream that will be passed to the strategy through IConsole. Default err is System.err
     *
     * @param err stream
     */
    public void setErr(PrintStream err);

    /**
     * Sets the location of the cache files. Default is <code>System.getProperty("java.io.tmpdir") + "/.cache"</code>
     * 
     * WARNING: JForex might delete all folder's content if folder already existed AND was not created by this method call.  
     *
     * @param cacheDirectory directory where cache files should be saved
     */
    public void setCacheDirectory(File cacheDirectory);
    
    /**
     * Compile .java strategy file to .jfx file. Destination .jfx file will be located in the same directory as the source.
     *
     * @param strategy .java source file to be compiled
     * @param obfuscate if true, the strategy will be obfuscated
     * @return returns id assigned to the strategy
     * @throws IllegalStateException if not connected
     */
    public void compileStrategy(String srcJavaFile, boolean obfuscate);
    
}
