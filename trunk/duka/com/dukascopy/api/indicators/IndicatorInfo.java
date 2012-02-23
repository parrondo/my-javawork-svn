/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

/**
 * Describes indicator
 * 
 * @author Dmitry Shohov
 */
public class IndicatorInfo {
    private String name;
    private String title;
    private String groupName;
    private boolean overChart;
    private boolean overVolumes;
    private boolean unstablePeriod;
    private int numberOfInputs;
    private int numberOfOptionalInputs;
    private int numberOfOutputs;
    private boolean showOnTicks = true;
    private boolean recalculateAll;
    private boolean sparseIndicator;
    
    /**
     * Creates IndicatorInfo without filling any fields
     */
    public IndicatorInfo() {
    }

    /**
     * Creates IndicatorInfo and fills all fields
     * 
     * @param name name of the indicator. Should be something simple like SMA for Simple Moving Average or BBANDS for Bollinger Bands
     * @param title title of the indicator
     * @param groupName name of the indicator group
     * @param overChart true if indicator should be drawn over candles/ticks
     * @param overVolumes true if indicator should be drawn over volume information
     * @param unstablePeriod true if indicator has unstable period (like EMA or SAR). This will add more candles in every call to stabilize function
     * @param candlesticks true if indicator is Pattern Recognition function and should be shown over bars
     * @param numberOfInputs number of inputs that user should provide
     * @param numberOfOptionalInputs number of optional inputs
     * @param numberOfOutputs number of outputs, that function returns
     * @deprecated all "candlesticks" indicators are just "overChart" indicators with specific output type
     */
    public IndicatorInfo(String name, String title, String groupName, boolean overChart, boolean overVolumes, boolean unstablePeriod, boolean candlesticks,
            int numberOfInputs, int numberOfOptionalInputs, int numberOfOutputs) {
        this.groupName = groupName;
        this.name = name;
        this.title = title;
        this.numberOfInputs = numberOfInputs;
        this.numberOfOptionalInputs = numberOfOptionalInputs;
        this.numberOfOutputs = numberOfOutputs;
        this.overChart = overChart || candlesticks;
        this.overVolumes = overVolumes;
        this.unstablePeriod = unstablePeriod;
    }

    /**
     * Creates IndicatorInfo and fills all fields
     *
     * @param name name of the indicator. Should be something simple like SMA for Simple Moving Average or BBANDS for Bollinger Bands
     * @param title title of the indicator
     * @param groupName name of the indicator group
     * @param overChart true if indicator should be drawn over candles/ticks
     * @param overVolumes true if indicator should be drawn over volume information
     * @param unstablePeriod true if indicator has unstable period (like EMA or SAR). This will add more candles in every call to stabilize function
     * @param numberOfInputs number of inputs that user should provide
     * @param numberOfOptionalInputs number of optional inputs
     * @param numberOfOutputs number of outputs, that function returns
     */
    public IndicatorInfo(String name, String title, String groupName, boolean overChart, boolean overVolumes, boolean unstablePeriod,
            int numberOfInputs, int numberOfOptionalInputs, int numberOfOutputs) {
        this.groupName = groupName;
        this.name = name;
        this.title = title;
        this.numberOfInputs = numberOfInputs;
        this.numberOfOptionalInputs = numberOfOptionalInputs;
        this.numberOfOutputs = numberOfOutputs;
        this.overChart = overChart;
        this.overVolumes = overVolumes;
        this.unstablePeriod = unstablePeriod;
    }

    /**
     * Returns name of the indicator. Should be something simple like SMA for Simple Moving Average or BBANDS for Bollinger Bands
     * 
     * @return name of the indicator
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets name of the indicator. Should be something simple like SMA for Simple Moving Average or BBANDS for Bollinger Bands
     * 
     * @param name name of the indicator
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns title of the indicator
     * @return title of the indicator
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title of the indicator
     * @param title title of the indicator
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns name of the group
     * 
     * @return name of the group
     */
    public String getGroupName() {
        return groupName;
    }
    
    /**
     * Sets name of the group
     * 
     * @param groupName name of the group
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    /**
     * Returns true if indicator should be drawn over chart
     * 
     * @return true if indicator should be drawn over chart
     */
    public boolean isOverChart() {
        return overChart;
    }
    
    /**
     * Sets flag that defines where indicator should be drawn
     * 
     * @param overChart true if indicator should be drawn over chart
     */
    public void setOverChart(boolean overChart) {
        this.overChart = overChart;
    }
    
    /**
     * Returns true if indicator should be shown over volumes
     * 
     * @return true if indicator should be shown over volumes
     */
    public boolean isOverVolumes() {
        return overVolumes;
    }
    
    /**
     * Sets flag that defines where indicator should be drawn
     * 
     * @param overVolumes true if indicator should be shown over volumes
     */
    public void setOverVolumes(boolean overVolumes) {
        this.overVolumes = overVolumes;
    }
    
    /**
     * Returns true if indicator has unstable period (like EMA or SAR). This will add more candles in every call to stabilize function
     * 
     * @return true if indicator has unstable period
     */
    public boolean isUnstablePeriod() {
        return unstablePeriod;
    }
    
    /**
     * Sets flag that defines if indicator has unstable period
     * 
     * @param unstablePeriod true if indicator has unstable period
     */
    public void setUnstablePeriod(boolean unstablePeriod) {
        this.unstablePeriod = unstablePeriod;
    }
    
    /**
     * @deprecated always returns false
     */
    public boolean isCandlesticks() {
        return false;
    }
    
    /**
     * @deprecated if true, then makes indicator "overChart"
     */
    public void setCandlesticks(boolean candlesticks) {
        this.overChart = overChart || candlesticks;
    }
    
    /**
     * Returns number of inputs, that should be provided before calling function
     * 
     * @return number of inputs
     */
    public int getNumberOfInputs() {
        return numberOfInputs;
    }
    
    /**
     * Sets number of inputs
     * 
     * @param numberOfInputs number of inputs
     */
    public void setNumberOfInputs(int numberOfInputs) {
        this.numberOfInputs = numberOfInputs;
    }
    
    /**
     * Returns number of optional inputs, that can be set to customize function
     * 
     * @return number of optional inputs
     */
    public int getNumberOfOptionalInputs() {
        return numberOfOptionalInputs;
    }
    
    /**
     * Sets number of optional inputs
     * 
     * @param numberOfOptionalInputs number of optional inputs
     */
    public void setNumberOfOptionalInputs(int numberOfOptionalInputs) {
        this.numberOfOptionalInputs = numberOfOptionalInputs;
    }
    
    /**
     * Returns number of indicator outputs. Usually every output represents one line
     * 
     * @return number of outputs
     */
    public int getNumberOfOutputs() {
        return numberOfOutputs;
    }
    
    /**
     * Sets number of outputs, that indicator will return
     * 
     * @param numberOfOutputs number of outputs
     */
    public void setNumberOfOutputs(int numberOfOutputs) {
        this.numberOfOutputs = numberOfOutputs;
    }

    /**
     * Returns true if indicator should be shown on ticks (true by default)
     * 
     * @return true if indicator should be shown on ticks
     */
    public boolean isShowOnTicks() {
        return showOnTicks;
    }

    /**
     * Set this to false if indicator shouldn't be shown on ticks
     * 
     * @param showOnTicks true if indicator should be shown on ticks
     */
    public void setShowOnTicks(boolean showOnTicks) {
        this.showOnTicks = showOnTicks;
    }

    /**
     * Returns true if indicator should be recalculated for all chart data
     * 
     * @return true if indicator should be recalculated for all chart data
     */
    public boolean isRecalculateAll() {
        return recalculateAll;
    }

    /**
     * If set to true, then indicator will be recalculated for all chart data, instead of calculating it only for new arriving candle
     * 
     * @param recalculateAll
     */
    public void setRecalculateAll(boolean recalculateAll) {
        this.recalculateAll = recalculateAll;
    }

    /**
     * @deprecated As of API ver. 2.6.45 replaced by {@link #isSparseIndicator()} 
     * 
     */
    public boolean isSparceIndicator() {
        return sparseIndicator;
    }
    
    /**     
     * @deprecated As of API ver. 2.6.45 replaced by {@link #setSparseIndicator(boolean)}
     *  
     */
    public void setSparceIndicator(boolean sparceIndicator) {
        this.sparseIndicator = sparceIndicator;
    }
    
    /**
     * 
     * @return true if the indicator is a "sparse indicator" which doesn't generate values for every candle, false otherwise
     */
    public boolean isSparseIndicator(){
    	return sparseIndicator;
    }
    
    /**
     * Set this flag to true if indicator doesn't draw values for every candle, e.g. the ZigZag indicator. This will force
     * drawing on more data than is visible on the screen making it slower but with correct lines that go beyond the
     * chart edge.
     *
     * @param sparseIndicator draw indicator on more data than is visible on the screen
     */
    public void setSparseIndicator(boolean sparseIndicator){
    	this.sparseIndicator = sparseIndicator;
    }
}
