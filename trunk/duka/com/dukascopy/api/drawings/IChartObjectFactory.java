/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import com.dukascopy.api.IChartObject;

public interface IChartObjectFactory {
    
    /**
     * Creates <code>IRectangleChartObject</code> with default parameters.
     * 
     * @return instance of <code>IRectangleChartObject</code>
     */
    IRectangleChartObject createRectangle();
	
	/**
	 * Creates <code>IRectangleChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IRectangleChartObject</code>
	 */
	IRectangleChartObject createRectangle(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IRectangleChartObject</code> with default parameteres. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IRectangleChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IRectangleChartObject createRectangle(Object... params);
	
	/**
     * Creates <code>IEllipseChartObject</code> with default parameters.
     * 
     * @return instance of <code>IEllipseChartObject</code>
     */
	IEllipseChartObject createEllipse();

	/**
	 * Creates <code>IEllipseChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IEllipseChartObject</code>
	 */
	IEllipseChartObject createEllipse(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IEllipseChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IEllipseChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IEllipseChartObject createEllipse(Object... params);
	
	/**
     * Creates <code>ITriangleChartObject</code> with default parameters.
     * 
     * @return instance of <code>ITriangleChartObject</code>
     */
    ITriangleChartObject createTriangle();

	/**
	 * Creates <code>ITriangleChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>ITriangleChartObject</code>
	 */
	ITriangleChartObject createTriangle(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>ITriangleChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>ITriangleChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    ITriangleChartObject createTriangle(Object... params);
	
	/**
     * Creates <code>IHorizontalLineChartObject</code> with default parameters.
     * 
     * @return instance of <code>IHorizontalLineChartObject</code>
     */
    IHorizontalLineChartObject createHorizontalLine();
	
	/**
	 * Creates <code>IHorizontalLineChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IHorizontalLineChartObject</code>
	 */
	IHorizontalLineChartObject createHorizontalLine(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IHorizontalLineChartObject</code> with default parameters.
     * Optionally, accepts additional constructor parameters as
     * <code>vararg</code> argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IHorizontalLineChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IHorizontalLineChartObject createHorizontalLine(Object... params);
	
	/**
     * Creates <code>IVerticalLineChartObject</code> with default parameters.
     * 
     * @return instance of <code>IVerticalLineChartObject</code>
     */
    IVerticalLineChartObject createVerticalLine();
	
	/**
	 * Creates <code>IVerticalLineChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IVerticalLineChartObject</code>
	 */
	IVerticalLineChartObject createVerticalLine(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IVerticalLineChartObject</code> with default parameters.
     * Optionally, accepts additional constructor parameters as
     * <code>vararg</code> argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IVerticalLineChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IVerticalLineChartObject createVerticalLine(Object... params);
	
	/**
     * Creates <code>ILongLineChartObject</code> with default parameters.
     * 
     * @return instance of <code>ILongLineChartObject</code>
     */
    ILongLineChartObject createLongLine();
	
	/**
	 * Creates <code>ILongLineChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>ILongLineChartObject</code>
	 */
	ILongLineChartObject createLongLine(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>ILongLineChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>ILongLineChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    ILongLineChartObject createLongLine(Object... params);
	
	/**
     * Creates <code>ITextChartObject</code> with default parameters.
     * 
     * @return instance of <code>ITextChartObject</code>
     */
    ITextChartObject createText();
	
	/**
	 * Creates <code>ITextChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>ITextChartObject</code>
	 */
	ITextChartObject createText(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>ITextChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>ITextChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    ITextChartObject createText(Object... params);
	
	/**
     * Creates <code>ISignalUpChartObject</code> with default parameters.
     * 
     * @return instance of <code>ISignalUpChartObject</code>
     */
    ISignalUpChartObject createSignalUp();
	
	/**
	 * Creates <code>ISignalUpChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>ISignalUpChartObject</code>
	 */
	ISignalUpChartObject createSignalUp(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>ISignalUpChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>ISignalUpChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    ISignalUpChartObject createSignalUp(Object... params);
	
	/**
     * Creates <code>ISignalDownChartObject</code> with default parameters.
     * 
     * @return instance of <code>ISignalDownChartObject</code>
     */
    ISignalDownChartObject createSignalDown();
	
	/**
	 * Creates <code>ISignalDownChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>ISignalDownChartObject</code>
	 */
	ISignalDownChartObject createSignalDown(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>ISignalDownChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>ISignalDownChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    ISignalDownChartObject createSignalDown(Object... params);
	
	/**
     * Creates <code>IShortLineChartObject</code> with default parameters.
     * 
     * @return instance of <code>IShortLineChartObject</code>
     */
    IShortLineChartObject createShortLine();
	
	/**
	 * Creates <code>IShortLineChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IShortLineChartObject</code>
	 */
	IShortLineChartObject createShortLine(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IShortLineChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IShortLineChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IShortLineChartObject createShortLine(Object... params);
	
	/**
     * Creates <code>IRayLineChartObject</code> with default parameters.
     * 
     * @return instance of <code>IRayLineChartObject</code>
     */
    IRayLineChartObject createRayLine();
	
	/**
	 * Creates <code>IRayLineChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IRayLineChartObject</code>
	 */
	IRayLineChartObject createRayLine(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IRayLineChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IRayLineChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IRayLineChartObject createRayLine(Object... params);
	
	/**
     * Creates <code>ITimeMarkerChartObject</code> with default parameters.
     * 
     * @return instance of <code>ITimeMarkerChartObject</code>
     */
    ITimeMarkerChartObject createTimeMarker();
	
	/**
	 * Creates <code>ITimeMarkerChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>ITimeMarkerChartObject</code>
	 */
	ITimeMarkerChartObject createTimeMarker(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>ITimeMarkerChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>ITimeMarkerChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    ITimeMarkerChartObject createTimeMarker(Object... params);
	
	/**
     * Creates <code>IPriceMarkerChartObject</code> with default parameters.
     * 
     * @return instance of <code>IPriceMarkerChartObject</code>
     */
    IPriceMarkerChartObject createPriceMarker();
    
	/**
	 * Creates <code>IPriceMarkerChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IPriceMarkerChartObject</code>
	 */
	IPriceMarkerChartObject createPriceMarker(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IPriceMarkerChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IPriceMarkerChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IPriceMarkerChartObject createPriceMarker(Object... params);
	
	/**
     * Creates <code>IPolyLineChartObject</code> with default parameters.
     * 
     * @return instance of <code>IPolyLineChartObject</code>
     */
    IPolyLineChartObject createPolyLine();
	
	/**
	 * Creates <code>IPolyLineChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IPolyLineChartObject</code>
	 */
	IPolyLineChartObject createPolyLine(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IPolyLineChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IPolyLineChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IPolyLineChartObject createPolyLine(Object... params);
	
	/**
     * Creates <code>IChannelChartObject</code> with default parameters.
     * 
     * @return instance of <code>IChannelChartObject</code>
     */
    IChannelChartObject createChannel();
	
	/**
	 * Creates <code>IChannelChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IChannelChartObject</code>
	 */
	IChannelChartObject createChannel(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IChannelChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IChannelChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IChannelChartObject createChannel(Object... params);
	
	/**
     * Creates <code>ICyclesChartObject</code> with default parameters.
     * 
     * @return instance of <code>ICyclesChartObject</code>
     */
    ICyclesChartObject createCycles();
	
	/**
	 * Creates <code>ICyclesChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>ICyclesChartObject</code>
	 */
	ICyclesChartObject createCycles(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>ICyclesChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>ICyclesChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    ICyclesChartObject createCycles(Object... params);
	
	/**
     * Creates <code>IPercentChartObject</code> with default parameters.
     * 
     * @return instance of <code>IPercentChartObject</code>
     */
    IPercentChartObject createPercent();
	
	/**
	 * Creates <code>IPercentChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IPercentChartObject</code>
	 */
	IPercentChartObject createPercent(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IPercentChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * @return instance of <code>IPercentChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IPercentChartObject createPercent(Object... params);
	
	/**
     * Creates <code>IFiboArcChartObject</code> with default parameters.
     * 
     * @return instance of <code>IFiboArcChartObject</code>
     */
    IFiboArcChartObject createFiboArc();
	
	/**
	 * Creates <code>IFiboArcChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IFiboArcChartObject</code>
	 */
	IFiboArcChartObject createFiboArc(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IFiboArcChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IFiboArcChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IFiboArcChartObject createFiboArc(Object... params);
	
	/**
     * Creates <code>IFiboFanChartObject</code> with default parameters.
     * 
     * @return instance of <code>IFiboFanChartObject</code>
     */
    IFiboFanChartObject createFiboFan();
	
	/**
	 * Creates <code>IFiboFanChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IFiboFanChartObject</code>
	 */
	IFiboFanChartObject createFiboFan(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IFiboFanChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IFiboFanChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IFiboFanChartObject createFiboFan(Object... params);
	
	/**
     * Creates <code>IFiboRetracementChartObject</code> with default parameters.
     * 
     * @return instance of <code>IFiboRetracementChartObject</code>
     */
    IFiboRetracementChartObject createFiboRetracement();
	
	/**
	 * Creates <code>IFiboRetracementChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IFiboRetracementChartObject</code>
	 */
	IFiboRetracementChartObject createFiboRetracement(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IFiboRetracementChartObject</code> with default parameters.
     * Optionally, accepts additional constructor parameters as
     * <code>vararg</code> argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IFiboRetracementChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IFiboRetracementChartObject createFiboRetracement(Object... params);
	
	/**
     * Creates <code>IFiboTimeZonesChartObject</code> with default parameters.
     * 
     * @return instance of <code>IFiboTimeZonesChartObject</code>
     */
    IFiboTimeZonesChartObject createFiboTimeZones();
	
	/**
	 * Creates <code>IFiboTimeZonesChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IFiboTimeZonesChartObject</code>
	 */
	IFiboTimeZonesChartObject createFiboTimeZones(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IFiboTimeZonesChartObject</code> with default parameters.
     * Optionally, accepts additional constructor parameters as
     * <code>vararg</code> argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IFiboTimeZonesChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IFiboTimeZonesChartObject createFiboTimeZones(Object... params);
	
	/**
     * Creates <code>IFiboExpansionChartObject</code> with default parameters.
     * 
     * @return instance of <code>IFiboExpansionChartObject</code>
     */
    IFiboExpansionChartObject createFiboExpansion();
	
	/**
	 * Creates <code>IFiboExpansionChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IFiboExpansionChartObject</code>
	 */
	IFiboExpansionChartObject createFiboExpansion(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IFiboExpansionChartObject</code> with default parameters.
     * Optionally, accepts additional constructor parameters as
     * <code>vararg</code> argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IFiboExpansionChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IFiboExpansionChartObject createFiboExpansion(Object... params);
	
	/**
     * Creates <code>IAndrewsPitchforkChartObject</code> with default parameters.
     * 
     * @return instance of <code>IAndrewsPitchforkChartObject</code>
     */
    IAndrewsPitchforkChartObject createAndrewsPitchfork();
	
	/**
	 * Creates <code>IAndrewsPitchforkChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IAndrewsPitchforkChartObject</code>
	 */
	IAndrewsPitchforkChartObject createAndrewsPitchfork(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IAndrewsPitchforkChartObject</code> with default parameters.
     * Optionally, accepts additional constructor parameters as
     * <code>vararg</code> argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IAndrewsPitchforkChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IAndrewsPitchforkChartObject createAndrewsPitchfork(Object... params);
	
	/**
     * Creates <code>IOrderLineChartObject</code> with default parameters.
     * 
     * @return instance of <code>IOrderLineChartObject</code>
     */
    IOrderLineChartObject createOrderLine();
	
	/**
	 * Creates <code>IOrderLineChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IOrderLineChartObject</code>
	 */
	IOrderLineChartObject createOrderLine(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>IOrderLineChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>IOrderLineChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    IOrderLineChartObject createOrderLine(Object... params);
	
	/**
     * Creates <code>ILabelChartObject</code> with default parameters.
     * 
     * @return instance of <code>ILabelChartObject</code>
     */
    ILabelChartObject createLabel();
	
	/**
	 * Creates <code>ILabelChartObject</code> with default parameters.
	 * 
	 * @param key String identifier
	 * @return instance of <code>ILabelChartObject</code>
	 */
	ILabelChartObject createLabel(String key);
	
	/**
	 * @deprecated
     * Helper method wrapping <code>create()</code> factory method.Constructs
     * <code>ILabelChartObject</code> with default parameters. Optionally,
     * accepts additional constructor parameters as <code>vararg</code>
     * argument.
     * 
     * @param params
     *            - <code>vararg</code> constructor parameters.
     * 
     * @return instance of <code>ILabelChartObject</code>
     * 
     * @author stanislavs.rubens
     */
	@Deprecated
    ILabelChartObject createLabel(Object... params);
	
	/**
     * Creates OHLC Informer chart object with default parameters.
     * 
     * @return instance of <code>IOhlcChartObject</code>
     */
    IOhlcChartObject createOhlcInformer();
	
	/**
	 * Creates OHLC Informer chart object with specified String id.
	 * 
	 * @param key String identifier
	 * @return instance of <code>IOhlcChartObject</code>
	 */
	IOhlcChartObject createOhlcInformer(String key);
	
	/**
     * Creates Gann Angles chart object with default parameters.
     * 
     * @return instance of newly created <code>IGannAnglesChartObject</code>
     */
    IGannAnglesChartObject createGannAnglesChartObject();
	
	/**
	 * Creates Gann Angles chart object with specified String id.
	 * 
	 * @param key String identifier
	 * @return instance of newly created <code>IGannAnglesChartObject</code>
	 */
	IGannAnglesChartObject createGannAnglesChartObject(String key);
	
	/**
     * Creates Gann Periods chart object with default parameters.
     * 
     * @return instance of newly created <code>IGannGridChartObject</code>
     */
    IGannGridChartObject createGannGridChartObject();
    
    /**
     * Creates Gann Periods chart object with specified String id.
     * 
     * @param key String identifier
     * @return instance of newly created <code>IGannGridChartObject</code>
     */
    IGannGridChartObject createGannGridChartObject(String key);

	/**
     * Factory method for creating drawing shapes of given type.
     * 
     * @param objectClass
     *            - <code>Class</code> of object to create.
     * @param key String identifier
     * @exception IllegalArgumentException - if object gets instantiated with <code>null</code> values.
     * @return instance of <code>IChartObject</code> super type.
     * @author stanislavs.rubens
     */
    <T extends IChartObject> T create(Class<T> objectClass, String key);
	
	/**
     * Factory method for creating drawing shapes of given type.
     * 
     * @param objectClass
     *            - <code>Class</code> of object to create. 
     * @exception IllegalArgumentException - if object gets instantiated with <code>null</code> values.
     * @return instance of <code>IChartObject</code> super type.
     * @author stanislavs.rubens
     */
    <T extends IChartObject> T create(Class<T> objectClass);
	
	/**
	 * @deprecated
	 * Factory method for creating drawing shapes of given type. Optionally,
	 * accepts additional constructor parameters as <code>vararg</code>
	 * argument.
	 * 
	 * @param objectClass
	 *            - <code>Class</code> of object to create.
	 * @param args
	 *            - <code>vararg</code> constructor parameters. When specified,
	 *            <code>create()</code> method will try to find suitable
	 *            constructor and invoke it with <code>params</code> given.
	 *            Please note that currently <code>Object</code> classes that
	 *            belong to Java core libraries are converted to it's primitives
	 *            (i.e. <b>Integer.class -> Integer.TYPE</b>). This is done due
	 *            to API compatibility issues.
	 * @exception IllegalArgumentException - if object gets instantiated with <code>null</code> values.
	 * @return instance of <code>IChartObject</code> super type.
	 * @author stanislavs.rubens
	 */
	<T extends IChartObject> T create(Class<T> objectClass, Object... args);

}
