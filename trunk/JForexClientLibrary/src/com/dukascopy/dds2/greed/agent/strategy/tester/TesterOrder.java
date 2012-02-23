/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.IMessage;
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.JFException.Error;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.impl.execution.ScienceWaitForUpdate;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TesterOrder
/*     */   implements IOrder, ScienceWaitForUpdate
/*     */ {
/*     */   private static final Logger LOGGER;
/*     */   private TesterCustodian custodian;
/*     */   private String label;
/*     */   private String orderId;
/*     */   private Instrument instrument;
/*     */   private IEngine.OrderCommand orderCommand;
/*     */   private double requestedAmount;
/*     */   private IOrder.State state;
/*     */   private double clientPrice;
/*     */   private double filledPrice;
/*     */   private double closePrice;
/*     */   private double closePriceTotal;
/*     */   private double closedAmount;
/*     */   private double slippage;
/*     */   private double takeProfitPrice;
/*     */   private long takeProfitDate;
/*     */   private double stopLossPrice;
/*     */   private long stopLossDate;
/*     */   private OfferSide stopLossSide;
/*     */   private double trailingStep;
/*     */   private long goodTillTime;
/*     */   private String comment;
/*     */   private long creationTime;
/*     */   private long fillTime;
/*     */   private double initiallyFilledAmount;
/*     */   private double filledAmount;
/*     */   private long closeTime;
/*  53 */   private boolean wasMerged = false;
/*  54 */   private double profitLossInAccountCCY = 0.0D;
/*  55 */   private double profitLossInPips = 0.0D;
/*  56 */   private double commissionInUSD = 0.0D;
/*     */   private double lastCloseAmount;
/*     */   private double lastClientPrice;
/*     */   private boolean mcOrder;
/*  60 */   private boolean updated = true;
/*     */   private IMessage updatedMessage;
/*     */ 
/*     */   public TesterOrder(TesterCustodian engine, String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount)
/*     */   {
/*  64 */     this.custodian = engine;
/*  65 */     this.state = IOrder.State.CREATED;
/*  66 */     this.slippage = (5.0D * instrument.getPipValue());
/*  67 */     this.label = label;
/*  68 */     this.instrument = instrument;
/*  69 */     this.orderCommand = orderCommand;
/*  70 */     this.requestedAmount = amount;
/*     */   }
/*     */ 
/*     */   public TesterOrder(TesterCustodian engine, String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double clientPrice) {
/*  74 */     this(engine, label, instrument, orderCommand, amount);
/*  75 */     this.clientPrice = clientPrice;
/*     */   }
/*     */ 
/*     */   public TesterOrder(TesterCustodian engine, String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double clientPrice, double slippage) throws JFException
/*     */   {
/*  80 */     this(engine, label, instrument, orderCommand, amount, clientPrice);
/*  81 */     if (((!Double.isNaN(slippage)) || (!orderCommand.isConditional())) && (
/*  82 */       (slippage < 0.0D) || (Double.isNaN(slippage)))) {
/*  83 */       slippage = StratUtils.roundHalfEven(5.0D * instrument.getPipValue(), instrument.getPipScale());
/*     */     }
/*     */ 
/*  86 */     if (slippage < 0.0D) {
/*  87 */       slippage = StratUtils.roundHalfEven(5.0D * instrument.getPipValue(), instrument.getPipScale());
/*     */     }
/*  89 */     this.slippage = slippage;
/*     */   }
/*     */ 
/*     */   public TesterOrder(TesterCustodian engine, String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double clientPrice, double slippage, String comment) throws JFException
/*     */   {
/*  94 */     this(engine, label, instrument, orderCommand, amount, clientPrice, slippage);
/*  95 */     this.comment = comment;
/*     */   }
/*     */ 
/*     */   public TesterCustodian getEngine() {
/*  99 */     return this.custodian;
/*     */   }
/*     */ 
/*     */   public IEngine.OrderCommand getOrderCommand() {
/* 103 */     return this.orderCommand;
/*     */   }
/*     */ 
/*     */   public void setOrderCommand(IEngine.OrderCommand orderCommand) {
/* 107 */     this.orderCommand = orderCommand;
/*     */   }
/*     */ 
/*     */   public boolean isLong() {
/* 111 */     return getOrderCommand().isLong();
/*     */   }
/*     */ 
/*     */   public void close(double amount, double price, double slippage) throws JFException {
/* 115 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/* 116 */     if (this.state == IOrder.State.FILLED)
/* 117 */       this.custodian.closeOrder(this, price, amount);
/*     */     else
/* 119 */       throw new JFException("Cannot close order at specified price in CREATED, OPENED, CLOSED or CANCELED state, current state - [" + getState() + "]");
/*     */   }
/*     */ 
/*     */   public synchronized void close(double amount, double price) throws JFException
/*     */   {
/* 124 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/* 125 */     if (this.state == IOrder.State.FILLED)
/* 126 */       this.custodian.closeOrder(this, price, amount);
/*     */     else
/* 128 */       throw new JFException("Cannot close order at specified price in CREATED, OPENED, CLOSED or CANCELED state, current state - [" + getState() + "]");
/*     */   }
/*     */ 
/*     */   public synchronized void close(double amount) throws JFException
/*     */   {
/* 133 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/* 134 */     if (this.state == IOrder.State.FILLED)
/* 135 */       this.custodian.closeOrder(this, -1.0D, amount);
/*     */     else
/* 137 */       throw new JFException("Cannot close order at specified price in CREATED, OPENED, CLOSED or CANCELED state, current state - [" + getState() + "]");
/*     */   }
/*     */ 
/*     */   public synchronized void close() throws JFException
/*     */   {
/* 142 */     if (this.state == IOrder.State.OPENED)
/* 143 */       this.custodian.cancelOrder(this);
/* 144 */     else if (this.state == IOrder.State.FILLED)
/* 145 */       this.custodian.closeOrder(this);
/*     */     else
/* 147 */       throw new JFException("Cannot close order in CREATED, CLOSED or CANCELED state, current state - [" + getState() + "]");
/*     */   }
/*     */ 
/*     */   public synchronized double getAmount()
/*     */   {
/* 152 */     return StratUtils.roundHalfEven(getAmountInUnits() / 1000000.0D, 7);
/*     */   }
/*     */ 
/*     */   public synchronized double getAmountInUnits() {
/* 156 */     if ((this.state == IOrder.State.CREATED) || (this.state == IOrder.State.CANCELED) || (this.state == IOrder.State.OPENED)) {
/* 157 */       return this.requestedAmount;
/*     */     }
/* 159 */     return this.filledAmount;
/*     */   }
/*     */ 
/*     */   public synchronized double getInitiallyFilledAmount()
/*     */   {
/* 164 */     return StratUtils.roundHalfEven(this.initiallyFilledAmount / 1000000.0D, 7);
/*     */   }
/*     */ 
/*     */   public void setRequestedAmount(double amount) throws JFException {
/* 168 */     this.custodian.changeAmount(this, amount);
/*     */   }
/*     */ 
/*     */   public synchronized void setRequestedAmountSubmitted(double amount) {
/* 172 */     this.requestedAmount = amount;
/*     */   }
/*     */ 
/*     */   public String getComment() {
/* 176 */     return this.comment;
/*     */   }
/*     */ 
/*     */   public long getCreationTime() {
/* 180 */     return this.creationTime;
/*     */   }
/*     */ 
/*     */   public synchronized long getCloseTime() {
/* 184 */     return this.closeTime;
/*     */   }
/*     */ 
/*     */   public synchronized long getFillTime() {
/* 188 */     return this.fillTime;
/*     */   }
/*     */ 
/*     */   public synchronized double getRequestedAmount() {
/* 192 */     return StratUtils.roundHalfEven(this.requestedAmount / 1000000.0D, 7);
/*     */   }
/*     */ 
/*     */   public synchronized double getRequestedAmountInUnits() {
/* 196 */     return this.requestedAmount;
/*     */   }
/*     */ 
/*     */   public String getLabel() {
/* 200 */     return this.label;
/*     */   }
/*     */ 
/*     */   public synchronized long getStopLossDate() {
/* 204 */     return this.stopLossDate;
/*     */   }
/*     */ 
/*     */   public synchronized double getStopLossPrice() {
/* 208 */     return this.stopLossPrice;
/*     */   }
/*     */ 
/*     */   public synchronized long getTakeProfitDate() {
/* 212 */     return this.takeProfitDate;
/*     */   }
/*     */ 
/*     */   public synchronized double getTakeProfitPrice() {
/* 216 */     return this.takeProfitPrice;
/*     */   }
/*     */ 
/*     */   public void setOpenPrice(double price) throws JFException {
/* 220 */     price = StratUtils.round(price, this.instrument.getPipScale() + 2);
/* 221 */     this.custodian.changeOpenPrice(this, price);
/*     */   }
/*     */ 
/*     */   public void setOpenPriceSubmitted(double price) {
/* 225 */     this.clientPrice = price;
/*     */   }
/*     */ 
/*     */   public synchronized double getSlippage() {
/* 229 */     return StratUtils.round(this.slippage / this.instrument.getPipValue(), 2);
/*     */   }
/*     */ 
/*     */   public synchronized void fillOrder(double price, double amount) {
/* 233 */     this.fillTime = this.custodian.getCurrentTime();
/* 234 */     this.filledPrice = price;
/* 235 */     this.filledAmount = amount;
/* 236 */     this.initiallyFilledAmount = amount;
/* 237 */     this.state = IOrder.State.FILLED;
/* 238 */     this.orderCommand = (this.orderCommand.isLong() ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL);
/*     */   }
/*     */ 
/*     */   public synchronized void overnights(double overnightCommission) {
/* 242 */     assert (this.state == IOrder.State.FILLED);
/* 243 */     this.filledPrice = StratUtils.round(this.filledPrice + overnightCommission, 7);
/*     */   }
/*     */ 
/*     */   public synchronized double getOpenPrice() {
/* 247 */     if ((this.state == IOrder.State.CREATED) || (this.state == IOrder.State.OPENED) || (this.state == IOrder.State.CANCELED)) {
/* 248 */       return this.clientPrice;
/*     */     }
/* 250 */     return this.filledPrice;
/*     */   }
/*     */ 
/*     */   public synchronized double getClientPrice()
/*     */   {
/* 255 */     return this.clientPrice;
/*     */   }
/*     */ 
/*     */   public synchronized double getClosePrice() {
/* 259 */     return this.closePrice;
/*     */   }
/*     */ 
/*     */   public synchronized double getClosePriceTotal() {
/* 263 */     return this.closePriceTotal;
/*     */   }
/*     */ 
/*     */   public synchronized void setStopLossPrice(double price) throws JFException {
/* 267 */     this.custodian.setStopLoss(this, price, this.orderCommand.isLong() ? OfferSide.BID : OfferSide.ASK, 0.0D);
/*     */   }
/*     */   @Deprecated
/*     */   public void setStopLoss(double price) throws JFException {
/* 272 */     NotificationUtilsProvider.getNotificationUtils().postWarningMessage("setStopLoss method is deprecated and will be removed later, please use setStopLossPrice method instead", true);
/*     */ 
/* 274 */     setStopLossPrice(price);
/*     */   }
/*     */ 
/*     */   public synchronized void setStopLossSubmitted(double price, OfferSide side, double trailingStep, double lastClientPrice) {
/* 278 */     this.stopLossPrice = price;
/* 279 */     if (price == 0.0D) {
/* 280 */       this.stopLossSide = null;
/* 281 */       this.stopLossDate = 0L;
/* 282 */       this.trailingStep = 0.0D;
/* 283 */       this.lastClientPrice = 0.0D;
/*     */     } else {
/* 285 */       this.stopLossSide = side;
/* 286 */       this.stopLossDate = this.custodian.getCurrentTime();
/* 287 */       this.trailingStep = trailingStep;
/* 288 */       this.lastClientPrice = lastClientPrice;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setStopLossPrice(double price, OfferSide side) throws JFException {
/* 293 */     this.custodian.setStopLoss(this, price, side, 0.0D);
/*     */   }
/*     */   @Deprecated
/*     */   public void setStopLoss(double price, OfferSide side) throws JFException {
/* 298 */     NotificationUtilsProvider.getNotificationUtils().postWarningMessage("setStopLoss method is deprecated and will be removed later, please use setStopLossPrice method instead", true);
/*     */ 
/* 300 */     setStopLossPrice(price, side);
/*     */   }
/*     */ 
/*     */   public synchronized OfferSide getStopLossSide() {
/* 304 */     return this.stopLossSide;
/*     */   }
/*     */ 
/*     */   public synchronized void setStopLossPrice(double price, OfferSide side, double trailingStep) throws JFException {
/* 308 */     this.custodian.setStopLoss(this, price, side, trailingStep);
/*     */   }
/*     */   @Deprecated
/*     */   public void setStopLoss(double price, OfferSide side, double trailingStep) throws JFException {
/* 313 */     NotificationUtilsProvider.getNotificationUtils().postWarningMessage("setStopLoss method is deprecated and will be removed later, please use setStopLossPrice method instead", true);
/*     */ 
/* 315 */     setStopLossPrice(price, side, trailingStep);
/*     */   }
/*     */ 
/*     */   public synchronized double getTrailingStep() {
/* 319 */     return this.trailingStep;
/*     */   }
/*     */ 
/*     */   public synchronized void setTakeProfitPrice(double price) throws JFException {
/* 323 */     this.custodian.setTakeProfit(this, price);
/*     */   }
/*     */   @Deprecated
/*     */   public void setTakeProfit(double price) throws JFException {
/* 328 */     NotificationUtilsProvider.getNotificationUtils().postWarningMessage("setTakeProfit method is deprecated and will be removed later, please use setTakeProfitPrice method instead", true);
/*     */ 
/* 330 */     setTakeProfitPrice(price);
/*     */   }
/*     */ 
/*     */   public synchronized void setTakeProfitSubmitted(double price) {
/* 334 */     this.takeProfitPrice = price;
/* 335 */     if (price == 0.0D)
/* 336 */       this.takeProfitDate = 0L;
/*     */     else
/* 338 */       this.takeProfitDate = this.custodian.getCurrentTime();
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/* 343 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public synchronized IOrder.State getState() {
/* 347 */     return this.state;
/*     */   }
/*     */ 
/*     */   public synchronized void setGoodTillTime(long goodTillTime) throws JFException {
/* 351 */     if ((this.orderCommand != IEngine.OrderCommand.PLACE_BID) && (this.orderCommand != IEngine.OrderCommand.PLACE_OFFER)) {
/* 352 */       throw new JFException("Order should be \"place bid\" or \"place offer\"");
/*     */     }
/* 354 */     this.custodian.setGoodTillTime(this, goodTillTime);
/*     */   }
/*     */ 
/*     */   public synchronized void setGoodTillTimeSubmitted(long goodTillTime) {
/* 358 */     this.goodTillTime = goodTillTime;
/*     */   }
/*     */ 
/*     */   public synchronized long getGoodTillTime() {
/* 362 */     return this.goodTillTime;
/*     */   }
/*     */ 
/*     */   public synchronized void cancelOrder() {
/* 366 */     cancelOrder(this.custodian.getCurrentTime());
/*     */   }
/*     */ 
/*     */   public synchronized void cancelOrder(long closeTime) {
/* 370 */     this.state = IOrder.State.CANCELED;
/* 371 */     this.closeTime = closeTime;
/*     */   }
/*     */ 
/*     */   public synchronized void addCommission(double commission) {
/* 375 */     this.commissionInUSD += commission;
/*     */   }
/*     */ 
/*     */   public synchronized void closeOrder(double price, double profitLossInAccountCCY) {
/* 379 */     closeOrder(price, profitLossInAccountCCY, this.custodian.getCurrentTime());
/*     */   }
/*     */ 
/*     */   public synchronized void closeOrder(double price, double profitLossInAccountCCY, long closeTime) {
/* 383 */     this.closePrice = price;
/* 384 */     if (this.closePriceTotal == 0.0D) {
/* 385 */       this.closePriceTotal = price;
/* 386 */       if (isLong())
/* 387 */         this.profitLossInPips = StratUtils.roundHalfEven((price - this.filledPrice) / this.instrument.getPipValue(), 1);
/*     */       else
/* 389 */         this.profitLossInPips = StratUtils.roundHalfEven((this.filledPrice - price) / this.instrument.getPipValue(), 1);
/*     */     }
/*     */     else {
/* 392 */       double allAmount = this.closedAmount + this.filledAmount;
/* 393 */       this.closePriceTotal = StratUtils.roundHalfEven((price * this.filledAmount + this.closePriceTotal * this.closedAmount) / allAmount, 7);
/* 394 */       if (isLong()) {
/* 395 */         this.profitLossInPips = StratUtils.roundHalfEven((StratUtils.roundHalfEven((price - this.filledPrice) / this.instrument.getPipValue(), 1) * this.filledAmount + this.profitLossInPips * this.closedAmount) / allAmount, 2);
/*     */       }
/*     */       else {
/* 398 */         this.profitLossInPips = StratUtils.roundHalfEven((StratUtils.roundHalfEven((this.filledPrice - price) / this.instrument.getPipValue(), 1) * this.filledAmount + this.profitLossInPips * this.closedAmount) / allAmount, 2);
/*     */       }
/*     */     }
/*     */ 
/* 402 */     this.closedAmount += this.filledAmount;
/* 403 */     this.lastCloseAmount = this.filledAmount;
/*     */ 
/* 405 */     this.state = IOrder.State.CLOSED;
/* 406 */     this.closeTime = closeTime;
/* 407 */     this.profitLossInAccountCCY += profitLossInAccountCCY;
/* 408 */     this.wasMerged = false;
/*     */   }
/*     */ 
/*     */   public synchronized void closeNoOpenedOrder() {
/* 412 */     if (this.state != IOrder.State.CREATED) {
/* 413 */       throw new RuntimeException("State not CREATED");
/*     */     }
/* 415 */     this.state = IOrder.State.CLOSED;
/* 416 */     this.closeTime = (this.creationTime = this.custodian.getCurrentTime());
/*     */   }
/*     */ 
/*     */   public synchronized void closeBeforeMerge() {
/* 420 */     this.state = IOrder.State.CLOSED;
/* 421 */     this.closePrice = this.filledPrice;
/* 422 */     this.lastCloseAmount = 0.0D;
/* 423 */     this.wasMerged = true;
/* 424 */     this.closeTime = this.custodian.getCurrentTime();
/*     */   }
/*     */ 
/*     */   public synchronized double getLastCloseAmount() {
/* 428 */     return this.lastCloseAmount;
/*     */   }
/*     */ 
/*     */   public synchronized void partialClose(double price, double amount, double profitLossInAccountCCY, long closeTime) {
/* 432 */     this.closePrice = price;
/* 433 */     if (this.closePriceTotal == 0.0D) {
/* 434 */       this.closePriceTotal = price;
/* 435 */       if (isLong())
/* 436 */         this.profitLossInPips = StratUtils.roundHalfEven((price - this.filledPrice) / this.instrument.getPipValue(), 1);
/*     */       else
/* 438 */         this.profitLossInPips = StratUtils.roundHalfEven((this.filledPrice - price) / this.instrument.getPipValue(), 1);
/*     */     }
/*     */     else {
/* 441 */       double allAmount = this.closedAmount + amount;
/*     */ 
/* 443 */       this.closePriceTotal = StratUtils.roundHalfEven((price * amount + this.closePriceTotal * this.closedAmount) / allAmount, 7);
/* 444 */       if (isLong()) {
/* 445 */         this.profitLossInPips = StratUtils.roundHalfEven((StratUtils.roundHalfEven((price - this.filledPrice) / this.instrument.getPipValue(), 1) * amount + this.profitLossInPips * this.closedAmount) / allAmount, 2);
/*     */       }
/*     */       else {
/* 448 */         this.profitLossInPips = StratUtils.roundHalfEven((StratUtils.roundHalfEven((this.filledPrice - price) / this.instrument.getPipValue(), 1) * amount + this.profitLossInPips * this.closedAmount) / allAmount, 2);
/*     */       }
/*     */     }
/*     */ 
/* 452 */     this.closedAmount = StratUtils.roundHalfEven(this.closedAmount + amount, 2);
/* 453 */     this.filledAmount = StratUtils.roundHalfEven(this.filledAmount - amount, 2);
/* 454 */     this.requestedAmount = StratUtils.roundHalfEven(this.requestedAmount - amount, 2);
/* 455 */     this.lastCloseAmount = amount;
/* 456 */     this.closeTime = closeTime;
/* 457 */     this.profitLossInAccountCCY = StratUtils.roundHalfEven(this.profitLossInAccountCCY + profitLossInAccountCCY, 2);
/* 458 */     this.wasMerged = false;
/*     */   }
/*     */ 
/*     */   public void setLabel(String label) {
/* 462 */     this.label = label;
/*     */   }
/*     */ 
/*     */   public synchronized void openOrder() {
/* 466 */     this.state = IOrder.State.OPENED;
/* 467 */     this.creationTime = this.custodian.getCurrentTime();
/* 468 */     this.orderId = this.custodian.getNewOrderId();
/*     */   }
/*     */ 
/*     */   public synchronized double getProfitLossInAccountCCY() {
/* 472 */     return this.profitLossInAccountCCY;
/*     */   }
/*     */ 
/*     */   public synchronized double getProfitLossInPips() {
/* 476 */     if (this.state == IOrder.State.FILLED)
/*     */     {
/*     */       double closePrice;
/*     */       double closePrice;
/* 478 */       if (isLong())
/* 479 */         closePrice = this.custodian.getLastTick(this.instrument).getBid();
/*     */       else
/* 481 */         closePrice = this.custodian.getLastTick(this.instrument).getAsk();
/*     */       double plInPips;
/*     */       double plInPips;
/* 484 */       if (isLong())
/* 485 */         plInPips = StratUtils.roundHalfEven((closePrice - this.filledPrice) / this.instrument.getPipValue(), 1);
/*     */       else {
/* 487 */         plInPips = StratUtils.roundHalfEven((this.filledPrice - closePrice) / this.instrument.getPipValue(), 1);
/*     */       }
/* 489 */       return plInPips;
/* 490 */     }if (this.state == IOrder.State.CLOSED) {
/* 491 */       return this.profitLossInPips;
/*     */     }
/* 493 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public double getProfitLossInUSD()
/*     */   {
/*     */     double amount;
/*     */     double amount;
/* 500 */     if (this.state == IOrder.State.CLOSED)
/* 501 */       amount = this.closedAmount;
/*     */     else {
/* 503 */       amount = this.filledAmount;
/*     */     }
/* 505 */     double profLossInSecondaryCCY = StratUtils.roundHalfEven(getProfitLossInPips() * this.instrument.getPipValue() * amount, 2);
/* 506 */     return StratUtils.roundHalfEven(this.custodian.getCurrencyConverter().convert(profLossInSecondaryCCY, this.instrument.getSecondaryCurrency(), Instrument.EURUSD.getSecondaryCurrency(), null), 2);
/*     */   }
/*     */ 
/*     */   public double getProfitLossInAccountCurrency()
/*     */   {
/*     */     double amount;
/*     */     double amount;
/* 513 */     if (this.state == IOrder.State.CLOSED)
/* 514 */       amount = this.closedAmount;
/*     */     else {
/* 516 */       amount = this.filledAmount;
/*     */     }
/* 518 */     double profLossInSecondaryCCY = StratUtils.roundHalfEven(getProfitLossInPips() * this.instrument.getPipValue() * amount, 2);
/* 519 */     return StratUtils.roundHalfEven(this.custodian.getCurrencyConverter().convert(profLossInSecondaryCCY, this.instrument.getSecondaryCurrency(), this.custodian.getTesterAccount().getCurrency(), null), 2);
/*     */   }
/*     */ 
/*     */   public double getCommissionInUSD()
/*     */   {
/* 527 */     return StratUtils.roundHalfEven(this.commissionInUSD, 2);
/*     */   }
/*     */ 
/*     */   public double getCommission()
/*     */   {
/* 536 */     return StratUtils.roundHalfEven(this.custodian.getCurrencyConverter().convert(this.commissionInUSD, Instrument.EURUSD.getSecondaryCurrency(), this.custodian.getTesterAccount().getCurrency(), null), 2);
/*     */   }
/*     */ 
/*     */   public synchronized double getClosedAmount() {
/* 540 */     return this.closedAmount;
/*     */   }
/*     */ 
/*     */   public boolean wasMerged() {
/* 544 */     return this.wasMerged;
/*     */   }
/*     */ 
/*     */   public double getLastClientPrice() {
/* 548 */     return this.lastClientPrice;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 553 */     return this.state.name() + " " + getOrderCommand() + " " + getAmount() + " @ " + getOpenPrice();
/*     */   }
/*     */ 
/*     */   public String getId() {
/* 557 */     return this.orderId;
/*     */   }
/*     */ 
/*     */   public void createId() {
/* 561 */     this.orderId = this.custodian.getNewOrderId();
/*     */   }
/*     */ 
/*     */   public void waitForUpdate(long timeoutMills) {
/* 565 */     if ((this.state != IOrder.State.CLOSED) && (this.state != IOrder.State.CANCELED)) {
/* 566 */       this.updated = false;
/*     */       try {
/* 568 */         this.custodian.waitForStateChange(this, timeoutMills, TimeUnit.MILLISECONDS);
/*     */       } catch (InterruptedException ie) {
/* 570 */         LOGGER.warn("Interrupted");
/*     */       }
/* 572 */       this.updatedMessage = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public IMessage waitForUpdate(long timeout, TimeUnit unit)
/*     */   {
/* 578 */     if ((this.state != IOrder.State.CLOSED) && (this.state != IOrder.State.CANCELED)) {
/* 579 */       this.updated = false;
/*     */       try {
/* 581 */         this.custodian.waitForStateChange(this, timeout, unit);
/*     */       } catch (InterruptedException ie) {
/* 583 */         LOGGER.warn("Interrupted");
/*     */       }
/* 585 */       IMessage message = this.updatedMessage;
/* 586 */       this.updatedMessage = null;
/* 587 */       return message;
/*     */     }
/* 589 */     return null;
/*     */   }
/*     */ 
/*     */   public IMessage waitForUpdate(IOrder.State[] states)
/*     */     throws JFException
/*     */   {
/* 598 */     return waitForUpdate(9223372036854775807L, TimeUnit.MILLISECONDS, states);
/*     */   }
/*     */ 
/*     */   public IMessage waitForUpdate(long timeoutMills, IOrder.State[] states)
/*     */     throws JFException
/*     */   {
/* 606 */     return waitForUpdate(timeoutMills, TimeUnit.MILLISECONDS, states);
/*     */   }
/*     */ 
/*     */   public synchronized IMessage waitForUpdate(long timeout, TimeUnit unit, IOrder.State[] states)
/*     */     throws JFException
/*     */   {
/* 614 */     if ((this.state != IOrder.State.CLOSED) && (this.state != IOrder.State.CANCELED)) {
/* 615 */       this.updated = false;
/*     */       try {
/* 617 */         this.custodian.waitForStateChange(this, timeout, unit, states);
/*     */       } catch (InterruptedException ie) {
/* 619 */         LOGGER.warn("Interrupted");
/*     */       }
/* 621 */       IMessage message = this.updatedMessage;
/* 622 */       this.updatedMessage = null;
/* 623 */       return message;
/*     */     }
/* 625 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isMcOrder() {
/* 629 */     return this.mcOrder;
/*     */   }
/*     */ 
/*     */   public void setMcOrder(boolean mcOrder) {
/* 633 */     this.mcOrder = mcOrder;
/*     */   }
/*     */ 
/*     */   public void update(IMessage message) {
/* 637 */     if (!this.updated) {
/* 638 */       this.updatedMessage = message;
/*     */     }
/* 640 */     this.updated = true;
/*     */   }
/*     */ 
/*     */   public boolean updated()
/*     */   {
/* 645 */     return this.updated;
/*     */   }
/*     */ 
/*     */   public boolean updated(String[] states)
/*     */     throws JFException
/*     */   {
/* 653 */     if ((ObjectUtils.isNullOrEmpty(states)) || (states.length == 0))
/*     */     {
/* 655 */       return this.updated;
/*     */     }
/* 657 */     Set orderStates = new HashSet();
/* 658 */     if (!ObjectUtils.isNullOrEmpty(states)) {
/* 659 */       for (String state : states) {
/* 660 */         orderStates.add(IOrder.State.valueOf(state));
/*     */       }
/*     */     }
/* 663 */     if (orderStates.contains(this.state)) {
/* 664 */       return this.updated;
/*     */     }
/* 666 */     boolean stateValid = false;
/* 667 */     for (IOrder.State expectedState : orderStates) {
/* 668 */       if ((expectedState.ordinal() > this.state.ordinal()) && (!ObjectUtils.isEqual(this.state, IOrder.State.CLOSED))) {
/* 669 */         stateValid = true;
/* 670 */         break;
/*     */       }
/*     */     }
/* 673 */     if (!stateValid) {
/* 674 */       throw new JFException(JFException.Error.ORDER_STATE_IMMUTABLE, " state is " + getState());
/*     */     }
/* 676 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean compare(IOrder order)
/*     */   {
/* 682 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  25 */     LOGGER = LoggerFactory.getLogger(TesterOrder.class);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterOrder
 * JD-Core Version:    0.6.0
 */