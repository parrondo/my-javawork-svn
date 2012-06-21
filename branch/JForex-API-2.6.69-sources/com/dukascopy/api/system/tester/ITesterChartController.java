package com.dukascopy.api.system.tester;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Filter;
import com.dukascopy.api.IChart;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;

/**
 * Allows control over the chart object.
 *
 */
public interface ITesterChartController {
	
	/**
	 * 
	 * Changes chart time period. Use for the {@link com.dukascopy.api.DataType#TIME_PERIOD_AGGREGATION}
	 * 
	 * @param dataType - see {@link com.dukascopy.api.DataType}.
	 * @param period - see {@link com.dukascopy.api.Period}.
	 * 
	 * @Deprecated
	 * Use {@link com.dukascopy.api.system.tester.ITesterChartController#setFeedDescriptor(IFeedDescriptor)}.
	 */
    @Deprecated
	public void changePeriod(DataType dataType, Period period);
	
	/**
	 * Changes chart type.<br>
	 * 
	 * The <b>Instrument</b> and <b>OfferSide</b> of the <b>IFeedDescriptor</b> are ignored.<br>
	 * To change <b>OfferSide</b> use {@link com.dukascopy.api.system.tester.ITesterChartController#switchOfferSide(OfferSide)}.<br/>
	 * The <b>Filter</b> value of the <b>IFeedDescriptor</b> is set globally and propagated to all open charts.  
	 * @see #setFilter(Filter)
     * @see #getFilter()
	 * 
	 * @param feedDescriptor - see {@link com.dukascopy.api.feed.IFeedDescriptor}
	 */
	public void setFeedDescriptor(IFeedDescriptor feedDescriptor);
	
	/**
	 * Displays the "Add Indicator" dialog.  
	 * 
	 */
	public void addIndicators();
	
	/**
	 * The Price Marker activation allows to manually draw price marker lines on the chart.      
	 * 
	 */
	public void activatePriceMarker();
	
	/**
	 * The Time Marker activation allows to manually draw Time Marker line on the chart.
	 * 
	 */
	public void activateTimeMarker();
	
	/**
	 * The Percent Lines activation allows to manually draw Percent Lines on the chart.
	 * 
	 */
	public void activatePercentLines();
	
	/**
	 * The Channel Lines activation allows to manually draw Channel Lines on the chart.
	 * 
	 */
	public void activateChannelLines();
	
	/**
	 * The Poly Line activation allows to manually draw Poly Line on the chart.
	 * 
	 */
	public void activatePolyLine();
	
	/**
     * The Short Line activation allows to manually draw Short Line on the chart.
     * 
     */
    public void activateShortLine();
    
    /**
     * The Long Line activation allows to manually draw Long Line on the chart.
     * 
     */
    public void activateLongLine();
    
    /**
     * The Ray Line activation allows to manually draw Ray Line on the chart.
     * 
     */
    public void activateRayLine();
	
    /**
     * The Horizontal Line activation allows to manually draw Horizontal Line on the chart.
     * 
     */
    public void activateHorizontalLine();
    
    /**
     * The Vertical Line activation allows to manually draw Vertical Line on the chart.
     * 
     */
    public void activateVerticalLine();
    
    /**
     * The Text mode activation allows to add Text on the chart.
     * 
     */
    public void activateTextMode();
    
	/**
	 * Activates chart Auto Shift
	 * 
	 */
	public void setChartAutoShift();
	
	/**
	 * Zooms in chart
	 * 
	 */
	public void zoomIn();
	
	/**
	 * Zooms out chart
	 * 
	 */
	public void zoomOut();
	
	/**
	 * Adds OHLC Informer on the chart
	 * 
	 */
	public void addOHLCInformer();
	
	/**
	 * Switches OfferSide
	 * 
	 * @param offerSide
	 */
	public void switchOfferSide(final OfferSide offerSide);
	
	/**
	 * Makes the indicator visible or invisible.
	 *  
	 * @param show  true to make the indicator visible; false to make it invisible
	 */
	public void showEquityIndicator(boolean show);

    /**
     * Makes the indicator visible or invisible.
     *  
     * @param show  true to make the indicator visible; false to make it invisible
     */
	public void showProfitLossIndicator(boolean show);
	
    /**
     * Makes the indicator visible or invisible.
     *  
     * @param show  true to make the indicator visible; false to make it invisible
     */
	public void showBalanceIndicator(boolean show);
	
    /**
     * @return the {@link Filter} which is shared by all {@link IChart}s
     */
    public Filter getFilter();
    
    /**
     * Sets the {@link Filter} globally which will be propagated to all open charts. 
     * @param filter {@link Filter} to filter flats data
     */
    void setFilter(Filter filter);

}
