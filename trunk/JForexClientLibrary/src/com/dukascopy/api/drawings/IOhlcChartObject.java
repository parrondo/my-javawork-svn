/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.awt.Color;

import com.dukascopy.api.DataType;

public interface IOhlcChartObject extends IWidgetChartObject {
	
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_OHLC_ALIGNMENT = "ohlc.alignment";
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_OHLC_PARAM_VISIBILITY = "ohlc.param.visibility";
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_OHLC_POSX = "ohlc.posx";
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_OHLC_POSY = "ohlc.posy";
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_OHLC_SHOW_INDICATOR_INFO = "ohlc.show.indicator.info";
	
	
	public enum OhlcAlignment {
		HORIZONTAL,
		VERTICAL,
		AUTO
	}
	
	public enum CandleInfoParams {
	    DATE,
	    TIME,
		OPEN,
		HIGH,
		LOW,
		CLOSE,
		VOL,
		INDEX
	}
	
	public enum TickInfoParams {
	    DATE,
	    TIME,
		ASK,
		BID,
		ASK_VOL,
		BID_VOL
	}
	
	public enum PriceAgregatedInfoParams {
	    DATE,
	    START_TIME,
		END_TIME,
		OPEN,
		HIGH,
		LOW,
		CLOSE,
		VOL,
		INDEX
	}
	
	
	
	/**
     * Returns all InfoParams values for specified DataType
     * 
     * @param dataType one of <code>DataType</code> values
     * @return <code>Enum<?>[]</code> 
     */
    Enum<?>[] getAllInfoParamsByDataType(DataType dataType);
	
	/**
	 * Returns current alignment mode.
	 * @return <code>OhlcAlignment</code> value.
	 */
	OhlcAlignment getAlignment();
	
	/**
	 * Sets <code>OhlcAlignment</code> mode for this OHLC Informer.
	 * Use <code>OhlcAlignment.AUTO</code> to determine alignment automatically. 
	 * @param alignment <code>OhlcAlignment</code> value.
	 */
	void setAlignment(OhlcAlignment alignment);
	
	/**
	 * Returns whether parameter is displaying in current OHLC Informer or not.
	 * @param param  one of <code>IOhlcChartObject.CandleInfoParams</code>, <code>IOhlcChartObject.TickInfoParams</code> or <code>IOhlcChartObject.PriceAgregatedInfoParams</code> 
	 * @return
	 * @throws NullPointerException  in case of unsupported parameter type.
	 */
	<E extends Enum<E>> boolean getParamVisibility(Enum<E> param);
	
	/**
	 * Setup visibility property for specified parameter.
	 * @param param  one of <code>IOhlcChartObject.CandleInfoParams</code>, <code>IOhlcChartObject.TickInfoParams</code> or <code>IOhlcChartObject.PriceAgregatedInfoParams</code>
	 * @throws NullPointerException  in case of unsupported parameter type.
	 */
	<E extends Enum<E>> void setParamVisibility(Enum<E> param, boolean visible);
	
	/**
	 * Returns whether indicator values are displaying or not.
	 * @return
	 */
	boolean getShowIndicatorInfo();
	
	/**
	 * Sets property allowing to show indicator values.
	 * Default value: <b>true</b>
	 * @param showIndicatorInfo
	 */
	void setShowIndicatorInfo(boolean showIndicatorInfo);
	
	
	/**
	 * Clears all user's custom messages.
	 */
	void clearUserMessages();
	
	/**
	 * Adds user message.
	 * @param label  displaying at the left
	 * @param value  displaying at the right
	 * @param color  text color
	 */
	void addUserMessage(String label, String value, Color color);
	
	/**
	 * Adds user message.
	 * @param message
	 * @param color  text color
	 * @param textAlignment One of <code>SwingConstants.CENTER, SwingConstants.LEFT or SwingConstants.RIGHT</code>. Any other value will be replaced with SwingConstants.LEFT by default. 
	 * @param bold
	 */
	void addUserMessage(String message, Color color, int textAlignment, boolean bold);
}
