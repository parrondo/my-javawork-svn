/**
 * The file PriceRange.java was created on Mar 26, 2010 at 3:51:10 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class is used for Range Bar and Point&Figures  price interval in pips identification
 * 
 * @author Mark Vilkel
 */
public final class PriceRange implements Comparable<PriceRange> {
	
	private String name;
	private int pipCount;
	
	public static final PriceRange ONE_PIP;
	public static final PriceRange TWO_PIPS;
	public static final PriceRange THREE_PIPS;
	public static final PriceRange FOUR_PIPS;
	public static final PriceRange FIVE_PIPS;
	public static final PriceRange SIX_PIPS;
	
	public static int MAXIMAL_PIP_COUNT = 100;
	
	private static final List<PriceRange> JFOREX_PRICE_RANGES;
	
	static {
		ONE_PIP = new PriceRange("ONE_PIP", 1);
		TWO_PIPS = new PriceRange("TWO_PIPS", 2);
		THREE_PIPS = new PriceRange("THREE_PIPS", 3);
		FOUR_PIPS = new PriceRange("FOUR_PIPS", 4);
		FIVE_PIPS = new PriceRange("FIVE_PIPS", 5);
		SIX_PIPS = new PriceRange("SIX_PIPS", 6);
		
		JFOREX_PRICE_RANGES = Collections.unmodifiableList(createJForexPriceRanges());
	}
	
	private PriceRange(String name, int pipCount) {
		this.name = name;
		this.pipCount = pipCount;
	}

	/**
	 * Method returns the price range name
	 * 
	 * @return the name of price range
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method returns pip count of price range
	 * 
	 * @return pip count
	 */
	public int getPipCount() {
		return pipCount;
	}

	/**
	 * The method returns appropriate PriceRange for the passed String
	 * 
	 * @param str
	 * @return price range
	 */
	public static PriceRange valueOf(String str) {
		if (str == null) {
			return null;
		}
		
		for (PriceRange pr : JFOREX_PRICE_RANGES) {
			if (pr.getName().toLowerCase().startsWith(str.toLowerCase())) {
				return pr;
			}
		}
		
		try {
			Integer pipCount = Integer.valueOf(str);
			return valueOf(pipCount.intValue());
		} catch (Throwable t) {
			return null;
		}
		
	}
	
	/**
	 * The method returns appropriate PriceRange for the passed pip count
	 * 
	 * @param str
	 * @return price range
	 */
	public static PriceRange valueOf(int pipCount) {
		for (PriceRange pr : JFOREX_PRICE_RANGES) {
			if (pr.getPipCount() == pipCount) {
				return pr;
			}
		}
		return null;
	}

	/**
	 * Method creates and returns all available to JForex Price Ranges. 
	 * 
	 * @return the list of price ranges
	 */
	public static List<PriceRange> createJForexPriceRanges() {
		List<PriceRange> result = new ArrayList<PriceRange>();
		
		result.add(ONE_PIP);
		result.add(TWO_PIPS);
		result.add(THREE_PIPS);
		result.add(FOUR_PIPS);
		result.add(FIVE_PIPS);
		result.add(SIX_PIPS);
		
		for (int i = 7; i < MAXIMAL_PIP_COUNT + 1; i++) {
			PriceRange pr = new PriceRange(i + "_PIPS", i);
			result.add(pr);
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * The method compares passed PriceRange with the current one
	 * if (PriceRange == null) or (PriceRange.getPipCount() < this.getPipCount()) Returns 1  
	 * if PriceRange.getPipCount() > this.getPipCount() Returns -1
	 * Otherwise Returns 0 
	 * 
	 * @param tickBarSize
	 * @return
	 */
	@Override
	public int compareTo(PriceRange priceRange) {
		if (priceRange == null) {
			return 1;
		}
		if (getPipCount() < priceRange.getPipCount()) {
			return -1;
		}
		else if (getPipCount() > priceRange.getPipCount()) {
			return 1;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pipCount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PriceRange other = (PriceRange) obj;
		if (getPipCount() != other.getPipCount()) {
			return false;
		}
		return true;
	}

}
