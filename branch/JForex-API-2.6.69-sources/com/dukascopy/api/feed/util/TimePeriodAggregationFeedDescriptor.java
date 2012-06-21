/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed.util;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Filter;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.FeedDescriptor;

/**
 * @author Mark Vilkel
 * 
 * The class extends {@link FeedDescriptor} used for CandleSticks
 */
public class TimePeriodAggregationFeedDescriptor extends FeedDescriptor {

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
	 * 
	 * @param instrument
	 * @param period
	 * @param offerSide
	 * @param filter
	 */
	public TimePeriodAggregationFeedDescriptor(
			Instrument instrument,
			Period period,
			OfferSide offerSide,
			Filter filter
	) {
		setDataType(DataType.TIME_PERIOD_AGGREGATION);
		setInstrument(instrument);
		setPeriod(period);
		setOfferSide(offerSide);
		setFilter(filter);
	}

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
	 * 
	 * @param instrument
	 * @param period
	 * @param offerSide
	 */
	public TimePeriodAggregationFeedDescriptor(
			Instrument instrument,
			Period period,
			OfferSide offerSide
	) {
		this(
				instrument,
				period,
				offerSide,
				Filter.NO_FILTER
		);
	}
	
}
