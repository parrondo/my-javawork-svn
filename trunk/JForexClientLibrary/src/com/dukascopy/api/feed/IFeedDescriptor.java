/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Filter;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.TickBarSize;

/**
 * Interface, which describes feed data.
 * <p>
 * There might be several data types supported by JForex, see {@link DataType}
 * <p>
 * Ticks are defined by<br>
 * 		{@link DataType#TICKS} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)})
 * <p>
 * Candles/Bars are defined by<br>
 *    	{@link DataType#TIME_PERIOD_AGGREGATION} setter - ({@link #setDataType(DataType)}),<br>
 *    	{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 *    	{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)}),<br>
 *    	{@link Filter} (setter - {@link #setFilter(Filter)}),<br>
 *    	{@link Period} (setter - {@link #setPeriod(Period)})
 * <p>
 * RangeBars are defined by<br>
 * 		{@link DataType#PRICE_RANGE_AGGREGATION} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 * 		{@link PriceRange} (setter - {@link #setPriceRange(PriceRange)}),<br>
 * 		{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)})
 * <p>
 * Point and Figures are defined by<br>
 * 		{@link DataType#POINT_AND_FIGURE} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 * 		{@link PriceRange} called box size (setter - {@link #setPriceRange(PriceRange)}),<br>
 * 		{@link ReversalAmount} (setter - {@link #setReversalAmount(ReversalAmount)}),<br>
 * 		{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)})
 * <p>
 * Tick Bars are defined by<br>
 * 		{@link DataType#TICK_BAR} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 * 		{@link TickBarSize} (setter - {@link #setTickBarSize(TickBarSize)}),<br>
 * 		{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)})
 * <p> 
 * Renkos are defined by<br>
 * 		{@link DataType#RENKO} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 * 		{@link PriceRange} called brick size (setter - {@link #setPriceRange(PriceRange)}),<br>
 * 		{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)})
 * <p> 
 * 
 * @author Mark Vilkel
 */
public interface IFeedDescriptor {

	/**
	 * Getter for instrument
	 * 
	 * @return instrument
	 */
	Instrument getInstrument();

	/**
	 * Setter for instrument
	 * 
	 * @param instrument
	 */
	void setInstrument(Instrument instrument);
	

	/**
	 * Getter for offer side
	 * 
	 * @return
	 */
	OfferSide getOfferSide();

	/**
	 * Setter for offer side
	 * 
	 * @param offerSide
	 */
	void setOfferSide(OfferSide offerSide);
	

	/**
	 * Getter for data type
	 * 
	 * @return
	 */
	DataType getDataType();

	/**
	 * Setter for data type
	 * 
	 * @param dataType
	 */
	void setDataType(DataType dataType);
	

	/**
	 * Getter for period
	 * 
	 * @return
	 */
	Period getPeriod();

	/**
	 * Setter for period
	 * 
	 * @param period
	 */
	void setPeriod(Period period);
	

	/**
	 * Getter for price range
	 * 
	 * @return
	 */
	PriceRange getPriceRange();

	/**
	 * Setter for price range
	 * 
	 * @param priceRange
	 */
	void setPriceRange(PriceRange priceRange);
	

	/**
	 * Getter for reversal amount
	 * 
	 * @return reversal amount
	 */
	ReversalAmount getReversalAmount();

	/**
	 * Setter for reversal amount
	 * 
	 * @param reversalAmount
	 */
	void setReversalAmount(ReversalAmount reversalAmount);
	

	/**
	 * Getter for tick bar size
	 * 
	 * @return tick bar size
	 */
	TickBarSize getTickBarSize();

	/**
	 * Setter for tick bar size
	 * 
	 * @param tickBarSize
	 */
	void setTickBarSize(TickBarSize tickBarSize);
	

	/**
	 * Getter for filter
	 * 
	 * @return filter
	 */
	Filter getFilter();

	/**
	 * Setter for filter
	 * 
	 * @param filter
	 */
	void setFilter(Filter filter);

}
