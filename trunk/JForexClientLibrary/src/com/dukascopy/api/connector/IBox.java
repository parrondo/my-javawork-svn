package com.dukascopy.api.connector;

import com.dukascopy.api.IBar;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.IOrder.State;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import java.awt.Image;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.swing.Icon;

public abstract interface IBox
{
  public abstract double round(double paramDouble, int paramInt);

  public abstract double roundHalfPip(double paramDouble);

  public abstract IOrder submitOrderSync(Instrument paramInstrument, IEngine.OrderCommand paramOrderCommand, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt1, int paramInt2, int paramInt3)
    throws JFException;

  public abstract IOrder closeOrderSync(IOrder paramIOrder)
    throws JFException;

  public abstract void closeAllSync(List<IOrder> paramList)
    throws JFException;

  public abstract List<IOrder> getOrders(Instrument paramInstrument, IOrder.State paramState, Boolean paramBoolean, int paramInt)
    throws JFException;

  public abstract List<IOrder> getOrders(Instrument paramInstrument, IOrder.State paramState, int paramInt)
    throws JFException;

  public abstract List<IOrder> getOrders(Instrument paramInstrument, IOrder.State paramState)
    throws JFException;

  public abstract IOrder mergeOrdersSync(String paramString, List<IOrder> paramList)
    throws JFException;

  public abstract List<IOrder> mergeAllInstrumentsOrdersSync(int paramInt, List<IOrder> paramList)
    throws JFException;

  public abstract String generateLabel(int paramInt);

  public abstract int getMagicNumber(IOrder paramIOrder);

  public abstract Image loadImage(String paramString);

  public abstract byte[] loadResource(String paramString);

  public abstract boolean playSound(String paramString);

  public abstract Icon loadIcon(String paramString);

  public abstract void print(Object[] paramArrayOfObject);

  public abstract List<IBar> getLastBars(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, int paramInt, boolean paramBoolean)
    throws JFException;

  public abstract IBar[] getNBars(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, int paramInt1, int paramInt2)
    throws JFException;

  public abstract double calculateStopLoss(Instrument paramInstrument, IEngine.OrderCommand paramOrderCommand, double paramDouble, int paramInt)
    throws JFException;

  public abstract double calculateTakeProfit(Instrument paramInstrument, IEngine.OrderCommand paramOrderCommand, double paramDouble, int paramInt)
    throws JFException;

  public abstract double convertMoney(double paramDouble, Currency paramCurrency1, Currency paramCurrency2)
    throws JFException;

  public abstract double calculateProfitMoney(IOrder paramIOrder)
    throws JFException;

  public abstract double calculateProfitMoney(double paramDouble1, double paramDouble2, boolean paramBoolean, double paramDouble3);

  public abstract double calculateProfitPips(IOrder paramIOrder)
    throws JFException;

  public abstract double calculateProfitPips(double paramDouble1, double paramDouble2, boolean paramBoolean, double paramDouble3);

  public abstract double weightedPrice(List<IOrder> paramList)
    throws JFException;

  public abstract double getExposureByInstrument(Instrument paramInstrument)
    throws JFException;

  public abstract double getExposureByInstrument(Instrument paramInstrument, List<IOrder> paramList);

  public abstract double getExposureByCurrency(Currency paramCurrency)
    throws JFException;

  public abstract double getExposureByCurrency(Currency paramCurrency, List<IOrder> paramList);

  public abstract Map<Currency, Double> getExposureByCurrency()
    throws JFException;

  public abstract void sleep(int paramInt);

  public abstract boolean evaluation(boolean paramBoolean, String paramString);

  public abstract Scanner getScanner(String paramString);

  public abstract ISettings getSettings(String paramString);

  public abstract String multiargToString(String paramString, Object[] paramArrayOfObject);

  public abstract boolean isInTimeRange(long paramLong, String paramString)
    throws JFException;

  public abstract boolean isBeforeTime(long paramLong, String paramString)
    throws JFException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.IBox
 * JD-Core Version:    0.6.0
 */