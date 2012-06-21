/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import com.dukascopy.api.Instrument;

/**
 * Listens to the daily maximum and minimum prices for corresponding {@link Instrument instrument}
 * @author Mark Vilkel
 */
public interface IDailyHighLowListener {
	
	/**
	 * The method is called when another the best high price (maximal) 'arrives' in the system
	 * 
	 * @param instrument
	 * @param high
	 */
	void highUpdated(
			Instrument instrument,
			double high
	);

	/**
	 * The method is called when another the best low price (minimal) 'arrives' in the system
	 * 
	 * @param instrument
	 * @param low
	 */
	void lowUpdated(
			Instrument instrument,
			double low
	);
}
