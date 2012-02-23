/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents periods of bars. This class is used as enum in java 1.5 and later, but defined as class to allow creation of custom
 * period.
 */
public final class Period implements Comparable<Period>, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Defines period of tick. All methods usually returning interval or unit will return null or -1
     */
    public final static Period TICK;

    public final static Period ONE_SEC;

    public final static Period TWO_SECS;

    public final static Period TEN_SECS;

    public final static Period TWENTY_SECS;

    public final static Period THIRTY_SECS;

    public final static Period ONE_MIN;

    public final static Period FIVE_MINS;

    public final static Period TEN_MINS;

    public final static Period FIFTEEN_MINS;

    public final static Period TWENTY_MINS;

    public final static Period THIRTY_MINS;

    public final static Period ONE_HOUR;

    public final static Period FOUR_HOURS;

    public final static Period DAILY;

    public final static Period DAILY_SUNDAY_IN_MONDAY;

    public final static Period DAILY_SKIP_SUNDAY;

    public final static Period WEEKLY;

    public final static Period MONTHLY;

    public final static Period ONE_YEAR;

    /**
     * Set of predefined periods
     */
    private final static Period[] PREDEFINED;

    /**
     * Set of predefined periods applicable to indicators
     */
    private final static Period[] PREDEFINED_IND;

    /**
     * Contains custom periods to exclude creation of the new objects of the same period
     */
    private final static Set<Period> customPeriods = new HashSet<Period>();

    private final int ordinal;

    private final String name;

    /**
     * Time unit
     * @deprecated use getUnit() instead
     */
    public final Unit unit;

    /**
     * Number of time units
     * @deprecated use getNumOfUnits() instead
     */
    public final int numOfUnits;

    /**
     * Time interval in milliseconds
     * @deprecated use getInterval() instead
     */
    public final long interval;

    static {
        int num = 0;
        TICK = new Period(num++, "TICK", null, -1);

        ONE_SEC = new Period(num++, "ONE_SEC", Unit.Second, 1);
        TWO_SECS = new Period(num++, "TWO_SECS", Unit.Second, 2);
        TEN_SECS = new Period(num++, "TEN_SECS", Unit.Second, 10);
        TWENTY_SECS = new Period(num++, "TWENTY_SECS", Unit.Second, 20);
        THIRTY_SECS = new Period(num++, "THIRTY_SECS", Unit.Second, 30);

        ONE_MIN = new Period(num++, "ONE_MIN", Unit.Minute, 1);
        FIVE_MINS = new Period(num++, "FIVE_MINS", Unit.Minute, 5);
        TEN_MINS = new Period(num++, "TEN_MINS", Unit.Minute, 10);
        FIFTEEN_MINS = new Period(num++, "FIFTEEN_MINS", Unit.Minute, 15);
        TWENTY_MINS = new Period(num++, "TWENTY_MINS", Unit.Minute, 20);
        THIRTY_MINS = new Period(num++, "THIRTY_MINS", Unit.Minute, 30);

        ONE_HOUR = new Period(num++, "ONE_HOUR", Unit.Hour, 1);
        FOUR_HOURS = new Period(num++, "FOUR_HOURS", Unit.Hour, 4);

        DAILY = new Period(num++, "DAILY", Unit.Day, 1);
        DAILY_SUNDAY_IN_MONDAY = new Period(DAILY.ordinal, "DAILY_SUNDAY_IN_MONDAY", Unit.Day, 1);
        DAILY_SKIP_SUNDAY = new Period(DAILY.ordinal, "DAILY_SKIP_SUNDAY", Unit.Day, 1);
        WEEKLY = new Period(num++, "WEEKLY", Unit.Week, 1);
        MONTHLY = new Period(num++, "MONTHLY", Unit.Month, 1);
        ONE_YEAR = new Period(num++, "ONE_YEAR", Unit.Year, 1);

        PREDEFINED = new Period[num];
        PREDEFINED[TICK.ordinal] = TICK;
        PREDEFINED[ONE_SEC.ordinal] = ONE_SEC;
        PREDEFINED[TWO_SECS.ordinal] = TWO_SECS;
        PREDEFINED[TEN_SECS.ordinal] = TEN_SECS;
        PREDEFINED[TWENTY_SECS.ordinal] = TWENTY_SECS;
        PREDEFINED[THIRTY_SECS.ordinal] = THIRTY_SECS;
        PREDEFINED[ONE_MIN.ordinal] = ONE_MIN;
        PREDEFINED[FIVE_MINS.ordinal] = FIVE_MINS;
        PREDEFINED[TEN_MINS.ordinal] = TEN_MINS;
        PREDEFINED[FIFTEEN_MINS.ordinal] = FIFTEEN_MINS;
        PREDEFINED[TWENTY_MINS.ordinal] = TWENTY_MINS;
        PREDEFINED[THIRTY_MINS.ordinal] = THIRTY_MINS;
        PREDEFINED[ONE_HOUR.ordinal] = ONE_HOUR;
        PREDEFINED[FOUR_HOURS.ordinal] = FOUR_HOURS;
        PREDEFINED[DAILY.ordinal] = DAILY;
        PREDEFINED[WEEKLY.ordinal] = WEEKLY;
        PREDEFINED[MONTHLY.ordinal] = MONTHLY;
        PREDEFINED[ONE_YEAR.ordinal] = ONE_YEAR;

        /*
         * See CURVE-43
         * 
         * Now there will be only four basic periods
         * 
         */
//        PREDEFINED_IND = new Period[] { TICK, TEN_SECS, ONE_MIN, FIVE_MINS, TEN_MINS, FIFTEEN_MINS, THIRTY_MINS, ONE_HOUR, FOUR_HOURS, DAILY, WEEKLY, MONTHLY };
        PREDEFINED_IND = new Period[] { TICK, ONE_MIN, ONE_HOUR, DAILY };
    }

    /**
     * Creates "enum" periods
     */
    private Period(int ordinal, String name, Unit unit, int nUnits) {
        this.ordinal = ordinal;
        this.name = name;
        this.unit = unit;
        this.numOfUnits = nUnits;
        this.interval = unit == null ? -1 : unit.getInterval() * nUnits;
    }

    /**
     * Returns time unit. Returns null for TICK
     * 
     * @return time unit
     */
    public final Unit getUnit() {
        return unit;
    }

    /**
     * Returns number of units. Returns -1 for TICK
     * 
     * @return number of units or -1 if it's TICK
     */
    public final int getNumOfUnits() {
        return numOfUnits;
    }

    /**
     * Returns interval in milliseconds. For TICK returns -1. For periods with variable length returns only one static value, MONTLY = 30 days,
     * ONE_YEAR = <code>(long) (365.24 * 24 * 60 * 60 * 1000L)</code>
     * @return interval in milliseconds or -1 if it's TICK
     */
    public final long getInterval() {
        return this == TICK ? -1 : interval;
    }

    public final boolean equals(Object other) {
        return other instanceof Period && this.getInterval() == ((Period) other).getInterval();
    }

    /**
     * Throws {@link CloneNotSupportedException}
     */
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Compares periods, but instead of using ordinal like enums, uses interval
     */
    public final int compareTo(Period another) {
        if (interval < another.interval) {
            return -1;
        } else if (interval > another.interval) {
            return 1;
        } else {
            return 0;
        }
    }

    public final int hashCode() {
        return (int) interval;
    }

    public final String toString() {
        if (this == TICK)
            return "Ticks";
        if (numOfUnits == 1) {
            switch (unit) {
            case Millisecond:
                return "Milliseconds";
            case Hour:
                return "Hourly";
            case Day:
                return "Daily";
            case Week:
                return "Weekly";
            case Month:
                return "Monthly";
            case Year:
                return "Yearly";
            default:
            }
        }

        StringBuilder sb = new StringBuilder(10).append(numOfUnits).append(" ").append(unit.getCompactDescription());
        if (numOfUnits > 1) {
            sb.append("s");
        }

        return sb.toString();
    }

    public int ordinal() {
        return ordinal;
    }

    /**
     * Name of predefined constant exactly how it was defined or null if it's custom period
     * 
     * @return name of period or null for custom periods
     */
    public String name() {
        return name;
    }

    /**
     * Returns one of the predefined periods with specified name. Throws IllegalArgumentException if there is no predefined period with
     * specified name (all custom periods will throw it)
     * 
     * @param name name of the period constant, exactly how it defined
     * @return period
     */
    public static final Period valueOf(String name) {
        if (name == null) {
            throw new NullPointerException("Name is null");
        }
        for (Period period : PREDEFINED) {
            if (period.name().equals(name)) {
                return period;
            }
        }
        if (Period.DAILY_SKIP_SUNDAY.name().equals(name)) {
            return Period.DAILY_SKIP_SUNDAY;
        } else if (Period.DAILY_SUNDAY_IN_MONDAY.name().equals(name)) {
            return Period.DAILY_SUNDAY_IN_MONDAY;
        }
        throw new IllegalArgumentException("No enum const Period." + name);
    }

    /**
     * Returns array of predefined periods
     * 
     * @return predefined periods
     */
    public static final Period[] values() {
        return PREDEFINED;
    }

    /**
     * Returns array of predefined periods applicable to indicators
     * 
     * @return predefined periods
     */
    public static final Period[] valuesForIndicator() {
        return PREDEFINED_IND;

    }

    /**
     * Checks whether this periods time interval is smaller than the one of period
     * @param period
     * @return  false if period time interval is smaller or is null. True if period time interval is bigger.
     */
    public boolean isSmallerThan(Period period) {
        return period != null && this.getInterval() < period.getInterval();
    }
    
    /**
     * @deprecated Please, use {@link #createCustomPeriod(Unit, int)}}
     */
    public static Period createCustomPeriod(String name, Unit unit, int unitsCount) {
    	return createCustomPeriod(unit, unitsCount);
    }
    
    /**
     * Method creates custom Period. Note, that the desired period has to be compliant to Period.getCompliancyPeriod() period
     * If desired period is not compliant, then IllegalArgumentException will thrown
     * If desired period is equal to one of the basic periods, the the appropriate basic period will be returned
     * 
     * @param unit
     * @param unitsCount
     * @return Period
     */
    public static Period createCustomPeriod(Unit unit, int unitsCount) {
        if (unit == null && unitsCount == -1) {
            return Period.TICK;
        }

    	checkPeriodName(unit);
    	
    	if (unitsCount <= 0) {
    		throw new IllegalArgumentException("Number of units should be greater than 0");
    	}

        /*
         * Don't let to create the same periods again
         */
        long newInterval = unit.getInterval() * unitsCount;
        for (Period period : customPeriods) {
            if (period.getInterval() == newInterval && period.getUnit() == unit) {
                return period;
            }
        }
        for (Period period : PREDEFINED) {
            if (period.getInterval() == newInterval && period.getUnit() == unit) {
                return period;
            }
        }

    	Period period = new Period(-1, null, unit, unitsCount);
    	/*
    	 * Period has to be compliant to the predefined period 
    	 */
    	boolean isPeriodCompliant = isPeriodCompliant(period);
    	if (!isPeriodCompliant) {
    		throw new IllegalArgumentException("Can not create period for arguments - " + unit + ", " + unitsCount + " because they are not compliant to '" + Period.getCompliancyPeriod() + "' time period");
    	}

        customPeriods.add(period);
    	return period;
    }

    /**
     * Returns correct instance when deserializing
     *
     * @return precreated instance
     */
    private Object readResolve() throws ObjectStreamException {
        for (Period period : PREDEFINED) {
            if (period.equals(this)) {
                return period;
            }
        }

        for (Period period : customPeriods) {
            if (period.equals(this)) {
                return period;
            }
        }

        customPeriods.add(this);
        return this;
    }

    /**
     * The method checks whether passed period is compliant to etalon period {DAYLY}
     * If compliant - true is returned
     * If not compliant - false is returned
     * 
     * @param period period to check
     * @return boolean
     */
    public static boolean isPeriodCompliant(Period period) {
    	return isPeriodCompliant(period, getCompliancyPeriod());
    }

    /**
     * The method returns compliancy period
     * 
     * @return Period
     */
    public static Period getCompliancyPeriod() {
    	return Period.DAILY; 
    }
    
	private static boolean isPeriodCompliant(Period period, Period etalon) {
		if (etalon.getInterval() > period.getInterval()) {
			if (etalon.getInterval() % period.getInterval() == 0) {   
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (period.getInterval() % etalon.getInterval() == 0) {   
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * The method checks whether the passed period is basic {TICK, TEN_SECS, ONE_MIN, FIVE_MINS, TEN_MINS, FIFTEEN_MINS, THIRTY_MINS, ONE_HOUR, FOUR_HOURS, DAILY, WEEKLY, MONTHLY}
	 * If it is basic - appropriate basic period is returned
	 * If it is not basic - null is returned
	 * 
	 * @param period period to check
	 * @return Basic Period
	 */
	public static Period isPeriodBasic(Period period) {
		for (Period p : PREDEFINED_IND) {
			if ((period.getUnit() == null && p.getUnit() == null || p.getUnit() == period.getUnit()) && p.getNumOfUnits() == period.getNumOfUnits()) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Returns the biggest possible period for passed custom one. 
	 * The returned period   
	 * 
	 * @param period custom period
	 * @return biggest possible period
	 */
	public static Period getBasicPeriodForCustom(Period period) {
    	/*
    	 * Don't use Arrays.asList(), because sorting will mix target array also
    	 */
		List<Period> basicPeriods = new ArrayList<Period>();
		for (Period p : Period.valuesForIndicator()) {
			basicPeriods.add(p);
		}
		
		/*
		 * Make sure periods are sorted by ascending 
		 */
		Collections.sort(basicPeriods, new Comparator<Period>() {
			@Override
			public int compare(Period o1, Period o2) {
				if (o1.getInterval() > o2.getInterval()) {
					return 1;
				}
				else return -1;
			}
		});
		
		Period lastSuitablePeriod = Period.TICK;
		for (Period p : basicPeriods) {
			if (p == Period.TICK) {
				continue;
			}
			else if (
					p.getInterval() <= period.getInterval() && 
					period.getInterval() % p.getInterval() == 0
			) {
				lastSuitablePeriod = p;
			}
		}
		
		return lastSuitablePeriod;
	}
	
	/**
	 * The method generates all compliant custom periods used in JForex
	 * 
	 * @return List<Period>
	 */
	public static List<Period> generateAllCompliantPeriods() {
		List<Period> periods = new ArrayList<Period>();
		periods.addAll(generateCompliantPeriods(Unit.Second, 1, 59));
		periods.addAll(generateCompliantPeriods(Unit.Minute, 59));
		periods.addAll(generateCompliantPeriods(Unit.Hour, 12));
		periods.addAll(generateCompliantPeriods(Unit.Day, 6));
		periods.addAll(generateCompliantPeriods(Unit.Week, 4));
		periods.addAll(generateCompliantPeriods(Unit.Month, 11));
		return periods;
	}
	
	/**
	 * @deprecated Please, use {@link #generateCompliantPeriods(Unit, int)}
	 */
	public static List<Period> generateCompliantPeriods(String name, Unit unit, int maxUnitCount) {
		return generateCompliantPeriods(unit, 1, maxUnitCount);
	}

	/**
	 * The method generates and returns the list of compliant periods for passed Unit from 1 to maxUnitCount
	 * 
	 * @param unit unit of the period
	 * @param maxUnitCount maximum number of units to check
	 * @return List<Period>
	 */
	public static List<Period> generateCompliantPeriods(Unit unit, int maxUnitCount) {
		return generateCompliantPeriods(unit, 1, maxUnitCount);
	}
	
	/**
	 * @deprecated Please, use {@link #generateCompliantPeriods(Unit, int, int)}
	 */
	public static List<Period> generateCompliantPeriods(String name, Unit unit, int startUnitCount, int maxUnitCount) {
		return generateCompliantPeriods(unit, startUnitCount, maxUnitCount);
	}
	
	/**
	 * The method generates and returns the list of compliant periods for passed Unit from startUnitCount to maxUnitCount
	 * 
     * @param unit unit of the period
     * @param startUnitCount minimum number of units to check
     * @param maxUnitCount maximum number of units to check
	 * @return List<Period>
	 */
	public static List<Period> generateCompliantPeriods(Unit unit, int startUnitCount, int maxUnitCount) {
		List<Period> periods = new ArrayList<Period>();
		for (int i = startUnitCount; i <= maxUnitCount; i ++) {
			try {
				Period p = Period.createCustomPeriod(unit, i);
				periods.add(p);
			} catch (Throwable t) {
				
			}
		}
		return periods;
	}

	private static void checkPeriodName(Unit unit) {
		switch (unit) {
			case Millisecond:
			case Second:
			case Minute:
			case Hour:
			case Day:
			case Week:
			case Month:
			case Year:
                return;
		}
		throw new IllegalArgumentException("Unsupported Unit - " + unit);
	}
}
