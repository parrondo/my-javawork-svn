/*
 * Copyright 1998-2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Class allows detailed configuration of commissions.
 * <a href="http://www.dukascopy.com/swiss/english/forex/forex_trading_accounts/commission-policy/">Default values</a>
 * are filled when class is created
 *
 * @author Dmitry Shohov
 */
public class Commissions implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private SortedMap<Double, Double> depositLimits = new TreeMap<Double, Double>();
    private SortedMap<Double, Double> equityLimits = new TreeMap<Double, Double>();
    private SortedMap<Double, Double> turnoverLimits = new TreeMap<Double, Double>();

    private double maxCommission = 48d;

    private double[] last30DaysTurnoverAtStart = new double[30];

    private boolean custodianBankOrGuarantee;

    /**
     * Creates commissions class and fills in <a href="http://www.dukascopy.com/swiss/english/forex/forex_trading_accounts/commission-policy/">default values</a>
     *
     * @param custodianBankOrGuarantee if true then deposit with custodian bank or bank guarantee is assumed
     */
    public Commissions(boolean custodianBankOrGuarantee) {
        this.custodianBankOrGuarantee = custodianBankOrGuarantee;
        depositLimits.put(5000d, 38d);
        depositLimits.put(10000d, 32d);
        depositLimits.put(25000d, 25d);
        depositLimits.put(50000d, 18d);
        if (custodianBankOrGuarantee) {
            depositLimits.put(250000d, 18d);
            depositLimits.put(500000d, 18d);
            depositLimits.put(1000000d, 18d);
            depositLimits.put(5000000d, 18d);
            depositLimits.put(10000000d, 18d);
        } else {
            depositLimits.put(250000d, 16d);
            depositLimits.put(500000d, 14d);
            depositLimits.put(1000000d, 12d);
            depositLimits.put(5000000d, 9d);
            depositLimits.put(10000000d, 5d);
        }

        equityLimits.put(5000d, 38d);
        equityLimits.put(10000d, 32d);
        equityLimits.put(25000d, 25d);
        equityLimits.put(50000d, 18d);
        if (custodianBankOrGuarantee) {
            equityLimits.put(250000d, 18d);
            equityLimits.put(500000d, 18d);
            equityLimits.put(1000000d, 18d);
            equityLimits.put(5000000d, 18d);
            equityLimits.put(10000000d, 18d);
        } else {
            equityLimits.put(250000d, 16d);
            equityLimits.put(500000d, 14d);
            equityLimits.put(1000000d, 12d);
            equityLimits.put(5000000d, 9d);
            equityLimits.put(10000000d, 5d);
        }

        turnoverLimits.put(5000000d, 38d);
        turnoverLimits.put(10000000d, 32d);
        turnoverLimits.put(25000000d, 25d);
        turnoverLimits.put(50000000d, 18d);
        turnoverLimits.put(250000000d, 16d);
        turnoverLimits.put(500000000d, 14d);
        turnoverLimits.put(1000000000d, 12d);
        turnoverLimits.put(2000000000d, 9d);
        turnoverLimits.put(4000000000d, 5d);
    }

    /**
     * Sets commission for situations when deposit, equity and turnover is less than minimum limit
     *
     * @param maxCommission commission for minimum deposit, equity and turnover
     */
    public void setMaxCommission(double maxCommission) {
        this.maxCommission = maxCommission;
    }

    /**
     * Returns commission for situations when deposit, equity and turnover is less than minimum limit
     *
     * @return commission for minimum deposit, equity and turnover
     */
    public double getMaxCommission() {
        return maxCommission;
    }

    /**
     * Sets turnover values for the last 30 days that will be used at tester start time. Size of the array must be 30 elements
     *
     * @param last30DaysTurnoverAtStart turnover values for the last 30 days
     * @throws ArrayIndexOutOfBoundsException when array length is not 30
     */
    public void setLast30DaysTurnoverAtStart(double[] last30DaysTurnoverAtStart) throws ArrayIndexOutOfBoundsException {
        if (last30DaysTurnoverAtStart.length != 30) {
            throw new ArrayIndexOutOfBoundsException("Array length must be 30");
        }
        this.last30DaysTurnoverAtStart = last30DaysTurnoverAtStart;
    }

    /**
     * Returns turnover values for the last 30 days that will be used at tester start time. By default all values are
     * zeros. Changing values in array will change values in Commissions class
     *
     * @return turnover values for the last 30 days
     */
    public double[] getLast30DaysTurnoverAtStart() {
        return last30DaysTurnoverAtStart;
    }

    /**
     * Sets limits for deposit amounts. If deposit amount at the commission calculation time is >= limit then it's value
     * will be used. The biggest limit that complies the condition will be used to get the commission
     *
     * @param limit deposit amount limit
     * @param commission commission to use when deposit amount is over the limit
     */
    public void setDepositLimit(double limit, double commission) {
        depositLimits.put(limit, commission);
    }

    /**
     * Returns all deposit limits. Changes in map will result in changes of the actual map of this class
     *
     * @return deposit limits
     */
    public SortedMap<Double, Double> getDepositLimits() {
        return depositLimits;
    }

    /**
     * Sets limits for equity amounts. If equity amount at the commission calculation time is >= limit then it's value
     * will be used. The biggest limit that complies the condition will be used to get the commission
     *
     * @param limit equity amount limit
     * @param commission commission to use when equity amount is over the limit
     */
    public void setEquityLimit(double limit, double commission) {
        equityLimits.put(limit, commission);
    }

    /**
     * Returns all equity limits. Changes in map will result in changes of the actual map of this class
     *
     * @return equity limits
     */
    public SortedMap<Double, Double> getEquityLimits() {
        return equityLimits;
    }

    /**
     * Sets limits for turnover amounts. If turnover amount at the commission calculation time is >= limit then it's value
     * will be used. The biggest limit that complies the condition will be used to get the commission
     *
     * @param limit turnover amount limit
     * @param commission commission to use when turnover amount is over the limit
     */
    public void setTurnoverLimit(double limit, double commission) {
        turnoverLimits.put(limit, commission);
    }

    /**
     * Returns all turnover limits. Changes in map will result in changes of the actual map of this class
     *
     * @return turnover limits
     */
    public SortedMap<Double, Double> getTurnoverLimits() {
        return turnoverLimits;
    }

    /**
     * Returns true if deposit is with custodian bank or there is a bank guarantee. Once used in constructor to fill in
     * correct levels, this field becomes only informational
     *
     * @return true if deposit is with custodian bank or there is a bank guarantee
     */
    public boolean isCustodianBankOrGuarantee() {
        return custodianBankOrGuarantee;
    }
}
