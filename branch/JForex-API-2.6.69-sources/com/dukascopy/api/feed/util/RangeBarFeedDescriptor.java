/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed.util;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.feed.FeedDescriptor;

/**
 * @author Mark Vilkel
 * 
 * The class extends {@link FeedDescriptor}
 */
public class RangeBarFeedDescriptor extends FeedDescriptor {

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
	 * 
	 * @param instrument
	 * @param priceRange
	 * @param offerSide
	 */
	public RangeBarFeedDescriptor(
			Instrument instrument,
			PriceRange priceRange,
			OfferSide offerSide
	) {
		setDataType(DataType.PRICE_RANGE_AGGREGATION);
		setInstrument(instrument);
		setPriceRange(priceRange);
		setOfferSide(offerSide);
	}

}
