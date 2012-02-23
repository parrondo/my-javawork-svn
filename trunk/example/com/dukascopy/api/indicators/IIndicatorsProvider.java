/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import java.io.File;
import java.util.Collection;

import com.dukascopy.api.JFException;

/**
 * Interface to get indicator groups, indicator names, and indicators itself
 * 
 * @author Dmitry Shohov
 */
public interface IIndicatorsProvider {
    /**
     * Returns list of indicator groups
     * 
     * @return list of indicator groups
     */
    public Collection<String> getGroups();
    
    /**
     * Returns indicator names that belongs to specified group
     * 
     * @param groupName indicator group
     * @return indicator names
     */
    public Collection<String> getNames(String groupName);
    
    /**
     * Returns list of all indicator names
     * 
     * @return list of all indicator names
     */
    public Collection<String> getAllNames();
    
    /**
     * Returns indicator with specified name or null if no indicator was found
     * 
     * @param name name of the indicator
     * @return indicator
     */
    public IIndicator getIndicator(String name);

    /**
     * Returns true if indicator should be available in add indicator dialog
     * 
     * @param indicatorName name of the indicator
     * @return true if indicator should be available in add indicator dialog or false otherwise
     */
    public boolean isEnabledOnCharts(String indicatorName);

    /**
     * Returns indicator's title
     * 
     * @param name name of the indicator
     * @return indicator's title
     */
    public String getTitle(String name);
    
    /** 
     * Attempts to open and register custom indicator in the system 
     * 
     * @param compiledCustomIndcatorFile file with the compiled indicator (the one with .jfx extension) 
     * @throws JFException when indicator doesn't exists or can't be instantiated or don't pass validation 
     */ 
    public void registerUserIndicator(File compiledCustomIndcatorFile) throws JFException; 
}
