/*     */ package com.dukascopy.api.impl;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.IMessage;
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.CurrencyConverter;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Currency;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class HistoryOrder
/*     */   implements IOrder
/*     */ {
/*     */   private Instrument instrument;
/*     */   private String label;
/*     */   private String id;
/*     */   private long fillTime;
/*     */   private long closeTime;
/*     */   private IEngine.OrderCommand orderCommand;
/*     */   private double filledAmount;
/*     */   private double openPrice;
/*     */   private double closePrice;
/*     */   private String comment;
/*     */   private Currency accountCurrency;
/*  34 */   private double commission = 0.0D;
/*     */ 
/*     */   public HistoryOrder(Instrument instrument, String label, String id, long fillTime, long closeTime, IEngine.OrderCommand orderCommand, double filledAmount, double openPrice, double closePrice, String comment, Currency accountCurrency, double commission)
/*     */   {
/*  39 */     this.instrument = instrument;
/*  40 */     this.label = label;
/*  41 */     this.id = id;
/*  42 */     this.fillTime = fillTime;
/*  43 */     this.closeTime = closeTime;
/*  44 */     this.orderCommand = orderCommand;
/*  45 */     this.filledAmount = filledAmount;
/*  46 */     this.openPrice = openPrice;
/*  47 */     this.closePrice = closePrice;
/*  48 */     this.comment = comment;
/*  49 */     this.accountCurrency = accountCurrency;
/*  50 */     this.commission = commission;
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/*  55 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/*  60 */     return this.label;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  65 */     return this.id;
/*     */   }
/*     */ 
/*     */   public long getCreationTime()
/*     */   {
/*  70 */     return this.fillTime;
/*     */   }
/*     */ 
/*     */   public long getCloseTime()
/*     */   {
/*  75 */     return this.closeTime;
/*     */   }
/*     */ 
/*     */   public IEngine.OrderCommand getOrderCommand()
/*     */   {
/*  80 */     return this.orderCommand;
/*     */   }
/*     */ 
/*     */   public boolean isLong()
/*     */   {
/*  85 */     return (this.orderCommand != null) && (this.orderCommand.isLong());
/*     */   }
/*     */ 
/*     */   public long getFillTime()
/*     */   {
/*  90 */     return this.fillTime;
/*     */   }
/*     */ 
/*     */   public double getAmount()
/*     */   {
/*  95 */     return this.filledAmount;
/*     */   }
/*     */ 
/*     */   public double getRequestedAmount()
/*     */   {
/* 100 */     return this.filledAmount;
/*     */   }
/*     */ 
/*     */   public double getOpenPrice()
/*     */   {
/* 105 */     return this.openPrice;
/*     */   }
/*     */ 
/*     */   public double getClosePrice()
/*     */   {
/* 110 */     return this.closePrice;
/*     */   }
/*     */ 
/*     */   public double getStopLossPrice()
/*     */   {
/* 115 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public double getTakeProfitPrice()
/*     */   {
/* 120 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public void setStopLossPrice(double price) throws JFException
/*     */   {
/* 125 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void setStopLoss(double price) throws JFException
/*     */   {
/* 130 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void setStopLossPrice(double price, OfferSide side) throws JFException
/*     */   {
/* 135 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void setStopLoss(double price, OfferSide side) throws JFException
/*     */   {
/* 140 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void setStopLossPrice(double price, OfferSide side, double trailingStep) throws JFException
/*     */   {
/* 145 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void setStopLoss(double price, OfferSide side, double trailingStep) throws JFException
/*     */   {
/* 150 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public OfferSide getStopLossSide()
/*     */   {
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   public double getTrailingStep()
/*     */   {
/* 160 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public void setTakeProfitPrice(double price) throws JFException
/*     */   {
/* 165 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void setTakeProfit(double price) throws JFException
/*     */   {
/* 170 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public String getComment()
/*     */   {
/* 175 */     return this.comment;
/*     */   }
/*     */ 
/*     */   public void setRequestedAmount(double amount) throws JFException
/*     */   {
/* 180 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void setOpenPrice(double price) throws JFException
/*     */   {
/* 185 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void close(double amount, double price, double slippage) throws JFException
/*     */   {
/* 190 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void close(double amount, double price) throws JFException
/*     */   {
/* 195 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void close(double amount) throws JFException
/*     */   {
/* 200 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public void close() throws JFException
/*     */   {
/* 205 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public IOrder.State getState()
/*     */   {
/* 210 */     return IOrder.State.CLOSED;
/*     */   }
/*     */ 
/*     */   public void setGoodTillTime(long goodTillTime) throws JFException
/*     */   {
/* 215 */     throw new JFException("Can't change historical order");
/*     */   }
/*     */ 
/*     */   public long getGoodTillTime()
/*     */   {
/* 220 */     return 0L;
/*     */   }
/*     */ 
/*     */   public void waitForUpdate(long timeoutMills)
/*     */   {
/*     */   }
/*     */ 
/*     */   public IMessage waitForUpdate(long timeout, TimeUnit unit)
/*     */   {
/* 229 */     return null;
/*     */   }
/*     */ 
/*     */   public IMessage waitForUpdate(IOrder.State[] states)
/*     */     throws JFException
/*     */   {
/* 238 */     return null;
/*     */   }
/*     */ 
/*     */   public IMessage waitForUpdate(long timeoutMills, IOrder.State[] states)
/*     */     throws JFException
/*     */   {
/* 247 */     return null;
/*     */   }
/*     */ 
/*     */   public IMessage waitForUpdate(long timeout, TimeUnit unit, IOrder.State[] states)
/*     */     throws JFException
/*     */   {
/* 256 */     return null;
/*     */   }
/*     */ 
/*     */   public double getProfitLossInPips()
/*     */   {
/* 261 */     double closePrice = this.closePrice;
/*     */     double plInPips;
/*     */     double plInPips;
/* 263 */     if (isLong())
/* 264 */       plInPips = StratUtils.roundHalfEven((closePrice - this.openPrice) / this.instrument.getPipValue(), 1);
/*     */     else {
/* 266 */       plInPips = StratUtils.roundHalfEven((this.openPrice - closePrice) / this.instrument.getPipValue(), 1);
/*     */     }
/* 268 */     return plInPips;
/*     */   }
/*     */ 
/*     */   public double getProfitLossInUSD()
/*     */   {
/* 273 */     double profLossInSecondaryCCY = StratUtils.roundHalfEven(getProfitLossInPips() * this.instrument.getPipValue() * this.filledAmount * 1000000.0D, 2);
/* 274 */     return StratUtils.roundHalfEven(getCurrencyConverter().convert(profLossInSecondaryCCY, this.instrument.getSecondaryCurrency(), Instrument.EURUSD.getSecondaryCurrency(), null), 2);
/*     */   }
/*     */ 
/*     */   protected AbstractCurrencyConverter getCurrencyConverter() {
/* 278 */     return CurrencyConverter.getCurrencyConverter();
/*     */   }
/*     */ 
/*     */   public double getProfitLossInAccountCurrency()
/*     */   {
/* 283 */     double profLossInSecondaryCCY = StratUtils.roundHalfEven(getProfitLossInPips() * this.instrument.getPipValue() * this.filledAmount * 1000000.0D, 2);
/* 284 */     return StratUtils.roundHalfEven(getCurrencyConverter().convert(profLossInSecondaryCCY, this.instrument.getSecondaryCurrency(), this.accountCurrency, null), 2);
/*     */   }
/*     */ 
/*     */   public double getCommissionInUSD()
/*     */   {
/* 292 */     return StratUtils.roundHalfEven(getCurrencyConverter().convert(this.commission, this.accountCurrency, Instrument.EURUSD.getSecondaryCurrency(), null), 2);
/*     */   }
/*     */ 
/*     */   public double getCommission()
/*     */   {
/* 300 */     return StratUtils.roundHalfEven(this.commission, 2);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 304 */     DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/* 305 */     format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 306 */     return "Fill time: " + format.format(Long.valueOf(this.fillTime)) + ", order command: " + this.orderCommand + ", amount: " + getAmount() + ", fill price " + getOpenPrice();
/*     */   }
/*     */ 
/*     */   public boolean compare(IOrder order)
/*     */   {
/* 314 */     return false;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.HistoryOrder
 * JD-Core Version:    0.6.0
 */