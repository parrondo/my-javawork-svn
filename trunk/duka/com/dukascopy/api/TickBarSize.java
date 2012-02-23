/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class indicates the number of ticks to be included to one Tick Bar
 *  
 * @author Mark Vilkel
 */
public final class TickBarSize implements Comparable<TickBarSize> {
	
	private static int MAXIMAL_SIZE = 100;
	
	public static final TickBarSize TWO;
	public static final TickBarSize THREE;
	public static final TickBarSize FOUR;
	public static final TickBarSize FIVE;

	public static List<TickBarSize> JFOREX_TRADE_BAR_SIZES;
	
	static {
		TWO = new TickBarSize(2);
		THREE = new TickBarSize(3);
		FOUR = new TickBarSize(4);
		FIVE = new TickBarSize(5);

		JFOREX_TRADE_BAR_SIZES = Collections.unmodifiableList(createJForexTickBarSizes());	
	}
	
	private int size;
	
	private TickBarSize(int size) {
		if (size <= 0 || size > MAXIMAL_SIZE) {
			throw new IllegalArgumentException("Illegal Tick Bar Size - " + size);
		}
		this.size = size;
	}

	/**
	 * Method creates and returns all available to JForex Tick Bar Sizes 
	 * 
	 * @return the list of Tick Bar Sizes
	 */
	public static List<TickBarSize> createJForexTickBarSizes() {
		List<TickBarSize> result = new ArrayList<TickBarSize>();
		
		result.add(TWO);
		result.add(THREE);
		result.add(FOUR);
		result.add(FIVE);
		for (int i = 6; i <= MAXIMAL_SIZE; i++) {
			result.add(new TickBarSize(i));
		}
		
		return result;
	}
	
	/**
	 * The method returns appropriate TickBarSize for the passed String
	 * 
	 * @param str
	 * @return TickBarSize
	 */
	public static TickBarSize valueOf(String str) {
		if (str == null) {
			return null;
		}
		
		try {
			int size = Integer.valueOf(str).intValue();
			
			for (TickBarSize bar : JFOREX_TRADE_BAR_SIZES) {
				if (bar.getSize() == size) {
					return bar;
				}
			}
 		} catch (Exception e) {
 			return null;
 		}
		
		
		return null;
	}
	
	/**
	 * The method returns appropriate TickBarSize for the passed amount
	 * 
	 * @param str
	 * @return TickBarSize
	 */
	public static TickBarSize valueOf(int size) {
		for (TickBarSize bar : JFOREX_TRADE_BAR_SIZES) {
			if (bar.getSize() == size) {
				return bar;
			}
		}
		return null;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + size;
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
		TickBarSize other = (TickBarSize) obj;
		if (size != other.size) {
			return false;
		}
		return true;
	}

	public int getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		return String.valueOf(size);
	}

	/**
	 * The method compares passed TickBarSize with the current one
	 * if (TickBarSize == null) or (TickBarSize.getSize() < this.getSize()) Returns 1  
	 * if TickBarSize.getSize() > this.getSize() Returns -1
	 * Otherwise Returns 0 
	 * 
	 * @param tickBarSize
	 * @return
	 */
	@Override
	public int compareTo(TickBarSize tickBarSize) {
		if (tickBarSize == null) {
			return 1;
		}
		else if (getSize() < tickBarSize.getSize()) {
			return -1;
		}
		else if (getSize() > tickBarSize.getSize()) {
			return 1;
		}
		return 0;
	}

}
