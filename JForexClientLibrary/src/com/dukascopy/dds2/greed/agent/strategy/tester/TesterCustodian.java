/*      */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*      */ 
/*      */ import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.IEngine;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.ISignal;
import com.dukascopy.api.ISignalsProcessor;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.impl.AbstractEngine;
import com.dukascopy.api.impl.StrategyEventsCallback;
import com.dukascopy.api.impl.StrategyWrapper;
import com.dukascopy.api.impl.connect.SignalImpl;
import com.dukascopy.api.system.Commissions;
import com.dukascopy.api.system.IStrategyExceptionHandler;
import com.dukascopy.api.system.Overnights;
import com.dukascopy.charts.data.datacache.FeedDataProvider;
import com.dukascopy.charts.data.datacache.feed.IFeedCommissionManager;
import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ 
/*      */ public class TesterCustodian extends AbstractEngine
/*      */   implements IEngine
/*      */ {
/*      */   public static String MC_MARGIN_CUT;
/*      */   private static final Logger LOGGER;
/*      */   private static final Instrument[] INSTRUMENT_VALUES;
/*      */   private static final long MILLION = 1000000L;
/*      */   private static final Currency USD;
/*      */   Map<Instrument, Double> minTradableAmounts;
/*   65 */   private List<TesterOrder>[] ordersByInstrument = new List[INSTRUMENT_VALUES.length];
/*   66 */   private List<TesterOrder> allOrders = new ArrayList();
/*   67 */   private long orderIdsSequence = 1L;
/*      */   private IStrategy strategy;
/*   69 */   private ITick[] lastTicks = new ITick[INSTRUMENT_VALUES.length];
/*   70 */   private boolean[] activeInstruments = new boolean[INSTRUMENT_VALUES.length];
/*   71 */   private List<TesterMessage> errors = new ArrayList();
/*      */   private INotificationUtils notificationUtils;
/*      */   private long currentTime;
/*   74 */   private List<Object[]> ordersToProcess = new ArrayList();
/*   75 */   private long lastAccountSendTime = -9223372036854775808L;
/*   76 */   private AtomicInteger margineCutLabelCounter = new AtomicInteger(0);
/*   77 */   private double[] tradedAmountsInSecCCY = new double[INSTRUMENT_VALUES.length];
/*   78 */   private long commissionsNextTime = -9223372036854775808L;
/*      */   private double currentCommission;
/*   80 */   private LinkedList<Double> turnoverLastMonth = new LinkedList();
/*      */   private TesterAccount account;
/*      */   private ITesterReport testerReportData;
/*      */   private IStrategyRunner strategyRunner;
/*      */   private TesterOrdersProvider testerOrdersProvider;
/*      */   private IStrategyExceptionHandler exceptionHandler;
/*      */   private TesterCurrencyConverter currencyConverter;
/*      */   private boolean recalculate;
/*   92 */   private long weekendsStart = -9223372036854775808L;
/*   93 */   private long weekendsEnd = -9223372036854775808L;
/*      */ 
/*   95 */   private long weekendsStartMarginCut = -9223372036854775808L;
/*   96 */   private long weekendsEndMarginCut = -9223372036854775808L;
/*      */ 
/*   98 */   private IEngine.StrategyMode mode = IEngine.StrategyMode.INDEPENDENT;
/*   99 */   private ISignalsProcessor signalsProcessor = null;
/*      */ 
/*      */   public TesterCustodian(Set<Instrument> activeInstruments, Map<Instrument, Double> minTradableAmounts, INotificationUtils notificationUtils, long currentTime, TesterAccount account, Map<Instrument, ITick> firstTicks, ITesterReport testerReportData, IStrategyRunner strategyRunner, TesterOrdersProvider testerOrdersProvider, IStrategyExceptionHandler exceptionHandler)
/*      */   {
/*  104 */     for (Instrument instrument : activeInstruments) {
/*  105 */       this.activeInstruments[instrument.ordinal()] = true;
/*      */     }
/*  107 */     this.minTradableAmounts = minTradableAmounts;
/*  108 */     this.notificationUtils = notificationUtils;
/*  109 */     this.currentTime = currentTime;
/*  110 */     this.account = account;
/*  111 */     this.testerReportData = testerReportData;
/*  112 */     this.strategyRunner = strategyRunner;
/*  113 */     this.testerOrdersProvider = testerOrdersProvider;
/*  114 */     this.exceptionHandler = exceptionHandler;
/*  115 */     for (int i = 0; i < this.ordersByInstrument.length; ++i) {
/*  116 */       this.ordersByInstrument[i] = new ArrayList();
/*      */     }
/*  118 */     for (Map.Entry entry : firstTicks.entrySet()) {
/*  119 */       Instrument instrument = (Instrument)entry.getKey();
/*  120 */       ITick tick = (ITick)entry.getValue();
/*  121 */       this.lastTicks[instrument.ordinal()] = tick;
/*      */     }
/*  123 */     this.currencyConverter = new TesterCurrencyConverter();
/*  124 */     Commissions commissions = account.getCommissions();
/*  125 */     if (commissions != null) {
/*  126 */       for (double turnover : commissions.getLast30DaysTurnoverAtStart()) {
/*  127 */         this.turnoverLastMonth.add(Double.valueOf(turnover));
/*      */       }
/*      */     }
/*  130 */     double turnoverLastMonthTotal = 0.0D;
/*  131 */     for (Double turnover : this.turnoverLastMonth) {
/*  132 */       turnoverLastMonthTotal += turnover.doubleValue();
/*      */     }
/*  134 */     calculateCurrentCommission(turnoverLastMonthTotal);
/*      */   }
/*      */ 
/*      */   public void setStrategy(IStrategy strategy) {
/*  138 */     this.strategy = strategy;
/*      */   }
/*      */ 
/*      */   public synchronized IOrder getOrder(String label) {
/*  142 */     if (label == null) {
/*  143 */       throw new NullPointerException("Label is null");
/*      */     }
/*  145 */     for (TesterOrder order : this.allOrders) {
/*  146 */       if (order.getLabel().equals(label)) {
/*  147 */         return order;
/*      */       }
/*      */     }
/*  150 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized List<IOrder> getOrders(Instrument instrument) {
/*  154 */     if (instrument == null) {
/*  155 */       throw new NullPointerException("Instrument is null");
/*      */     }
/*  157 */     return new ArrayList(this.ordersByInstrument[instrument.ordinal()]);
/*      */   }
/*      */ 
/*      */   public synchronized List<IOrder> getOrders() {
/*  161 */     return new ArrayList(this.allOrders);
/*      */   }
/*      */ 
/*      */   public synchronized List<IOrder> getOpenedPlaceBidsOffers() {
/*  165 */     List bidOffers = new ArrayList();
/*  166 */     for (TesterOrder order : this.allOrders) {
/*  167 */       if ((((order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID) || (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER))) && (order.getState() == IOrder.State.OPENED))
/*      */       {
/*  169 */         bidOffers.add(order);
/*      */       }
/*      */     }
/*  172 */     return bidOffers;
/*      */   }
/*      */ 
/*      */   public IEngine.Type getType() {
/*  176 */     return IEngine.Type.TEST;
/*      */   }
/*      */ 
/*      */   public synchronized IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime, String comment) throws JFException
/*      */   {
/*  181 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/*  182 */     slippage = StratUtils.roundHalfEven(slippage * instrument.getPipValue(), instrument.getPipScale() + 2);
/*  183 */     TesterOrder order = new TesterOrder(this, label, instrument, orderCommand, amount, StratUtils.roundHalfEven(price, instrument.getPipScale() + 2), slippage, comment);
/*  184 */     order.setStopLossSubmitted(StratUtils.roundHalfEven(stopLossPrice, instrument.getPipScale() + 2), (order.getOrderCommand().isLong()) ? OfferSide.BID : OfferSide.ASK, 0.0D, 0.0D);
/*  185 */     order.setTakeProfitSubmitted(StratUtils.roundHalfEven(takeProfitPrice, instrument.getPipScale() + 2));
/*  186 */     if (goodTillTime < 0L) {
/*  187 */       throw new JFException(JFException.Error.INVALID_GTT);
/*      */     }
/*  189 */     if ((goodTillTime > 0L) && (goodTillTime < 63072000000L)) {
/*  190 */       throw new JFException(JFException.Error.INVALID_GTT);
/*      */     }
/*  192 */     if (goodTillTime > 0L) {
/*  193 */       order.setGoodTillTimeSubmitted(goodTillTime);
/*      */     }
/*  195 */     submitOrder(order);
/*  196 */     return order;
/*      */   }
/*      */ 
/*      */   public synchronized IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime) throws JFException
/*      */   {
/*  201 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/*  202 */     slippage = StratUtils.roundHalfEven(slippage * instrument.getPipValue(), instrument.getPipScale() + 2);
/*  203 */     TesterOrder order = new TesterOrder(this, label, instrument, orderCommand, amount, StratUtils.roundHalfEven(price, instrument.getPipScale() + 2), slippage);
/*  204 */     order.setStopLossSubmitted(StratUtils.roundHalfEven(stopLossPrice, instrument.getPipScale() + 2), (order.getOrderCommand().isLong()) ? OfferSide.BID : OfferSide.ASK, 0.0D, 0.0D);
/*  205 */     order.setTakeProfitSubmitted(StratUtils.roundHalfEven(takeProfitPrice, instrument.getPipScale() + 2));
/*  206 */     if (goodTillTime < 0L) {
/*  207 */       throw new JFException(JFException.Error.INVALID_GTT);
/*      */     }
/*  209 */     if ((goodTillTime > 0L) && (goodTillTime < 63072000000L)) {
/*  210 */       throw new JFException(JFException.Error.INVALID_GTT);
/*      */     }
/*  212 */     if (goodTillTime != 0L) {
/*  213 */       order.setGoodTillTimeSubmitted(goodTillTime);
/*      */     }
/*  215 */     submitOrder(order);
/*  216 */     return order;
/*      */   }
/*      */ 
/*      */   public synchronized IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice) throws JFException
/*      */   {
/*  221 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/*  222 */     slippage = StratUtils.roundHalfEven(slippage * instrument.getPipValue(), instrument.getPipScale() + 2);
/*  223 */     TesterOrder order = new TesterOrder(this, label, instrument, orderCommand, amount, StratUtils.roundHalfEven(price, instrument.getPipScale() + 2), slippage);
/*  224 */     order.setStopLossSubmitted(StratUtils.roundHalfEven(stopLossPrice, instrument.getPipScale() + 2), (order.getOrderCommand().isLong()) ? OfferSide.BID : OfferSide.ASK, 0.0D, 0.0D);
/*  225 */     order.setTakeProfitSubmitted(StratUtils.roundHalfEven(takeProfitPrice, instrument.getPipScale() + 2));
/*  226 */     submitOrder(order);
/*  227 */     return order;
/*      */   }
/*      */ 
/*      */   public synchronized IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage) throws JFException
/*      */   {
/*  232 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/*  233 */     slippage = StratUtils.roundHalfEven(slippage * instrument.getPipValue(), instrument.getPipScale() + 2);
/*  234 */     TesterOrder order = new TesterOrder(this, label, instrument, orderCommand, amount, StratUtils.roundHalfEven(price, instrument.getPipScale() + 2), slippage);
/*  235 */     submitOrder(order);
/*  236 */     return order;
/*      */   }
/*      */ 
/*      */   public synchronized IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price) throws JFException {
/*  240 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/*  241 */     TesterOrder order = new TesterOrder(this, label, instrument, orderCommand, amount, StratUtils.roundHalfEven(price, instrument.getPipScale() + 2));
/*  242 */     submitOrder(order);
/*  243 */     return order;
/*      */   }
/*      */ 
/*      */   public synchronized IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount) throws JFException {
/*  247 */     if ((orderCommand != IEngine.OrderCommand.BUY) && (orderCommand != IEngine.OrderCommand.SELL)) {
/*  248 */       throw new JFException("STOP, LIMIT type orders and PLACE_BID, PLACE_OFFER should be submitted using method with price");
/*      */     }
/*  250 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/*  251 */     TesterOrder order = new TesterOrder(this, label, instrument, orderCommand, amount);
/*  252 */     submitOrder(order);
/*  253 */     return order;
/*      */   }
/*      */ 
/*      */   public synchronized IOrder submitOrderMC(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount) throws JFException {
/*  257 */     if ((orderCommand != IEngine.OrderCommand.BUY) && (orderCommand != IEngine.OrderCommand.SELL)) {
/*  258 */       throw new JFException("STOP, LIMIT type orders and PLACE_BID, PLACE_OFFER should be submitted using method with price");
/*      */     }
/*  260 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/*  261 */     TesterOrder order = new TesterOrder(this, label, instrument, orderCommand, amount);
/*  262 */     order.setMcOrder(true);
/*  263 */     submitOrder(order);
/*  264 */     return order;
/*      */   }
/*      */ 
/*      */   public synchronized void mergeOrders(IOrder[] orders) throws JFException {
/*  268 */     this.notificationUtils.postWarningMessage("mergeOrders method is deprecated and will be removed later, please use method with label parameter instead", true);
/*      */ 
/*  270 */     Instrument instrument = null;
/*  271 */     for (IOrder order : orders) {
/*  272 */       if (instrument == null) {
/*  273 */         instrument = order.getInstrument();
/*      */       }
/*  275 */       if (order.getInstrument() != instrument) {
/*  276 */         throw new JFException("Cannot merge orders with stop loss or take profit");
/*      */       }
/*  278 */       if (order.getState() != IOrder.State.FILLED) {
/*  279 */         throw new JFException("Cannot merge orders in state other than FILLED");
/*      */       }
/*  281 */       if (order.getStopLossPrice() != 0.0D) {
/*  282 */         throw new JFException("Cannot merge orders with stop loss");
/*      */       }
/*  284 */       if (order.getTakeProfitPrice() != 0.0D) {
/*  285 */         throw new JFException("Cannot merge orders with take profit");
/*      */       }
/*      */     }
/*  288 */     if (orders.length < 2)
/*  289 */       return;
/*      */     try
/*      */     {
/*  292 */       Method method = super.getClass().getDeclaredMethod("mergeOrdersImpl", new Class[] { TesterOrder.class, com.dukascopy.api.IOrder.class, TesterReportData.TesterEvent.CloseTrigger.class });
/*  293 */       this.ordersToProcess.add(new Object[] { method, { null, orders, TesterReportData.TesterEvent.CloseTrigger.MERGE_BY_STRATEGY } });
/*      */     } catch (Exception e) {
/*  295 */       LOGGER.error(e.getMessage(), e);
/*  296 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void mergeOrders(Collection<IOrder> orders) throws JFException {
/*  301 */     mergeOrders((IOrder[])orders.toArray(new IOrder[orders.size()]));
/*      */   }
/*      */ 
/*      */   public IOrder mergeOrders(String label, IOrder[] orders) throws JFException {
/*  305 */     Instrument instrument = null;
/*  306 */     for (IOrder order : orders) {
/*  307 */       if (instrument == null) {
/*  308 */         instrument = order.getInstrument();
/*      */       }
/*  310 */       if (order.getInstrument() != instrument) {
/*  311 */         throw new JFException("Cannot merge orders with stop loss or take profit");
/*      */       }
/*  313 */       if (order.getState() != IOrder.State.FILLED) {
/*  314 */         throw new JFException("Cannot merge orders in state other than FILLED");
/*      */       }
/*  316 */       if (order.getStopLossPrice() != 0.0D) {
/*  317 */         throw new JFException("Cannot merge orders with stop loss");
/*      */       }
/*  319 */       if (order.getTakeProfitPrice() != 0.0D) {
/*  320 */         throw new JFException("Cannot merge orders with take profit");
/*      */       }
/*      */ 
/*  323 */       createSignal(order, ISignal.Type.ORDER_MERGE);
/*      */     }
/*  325 */     if (orders.length < 2) {
/*  326 */       if (orders.length == 1) {
/*  327 */         return orders[0];
/*      */       }
/*  329 */       return null;
/*      */     }
/*      */ 
/*  332 */     label = validateLabel(label);
/*  333 */     for (TesterOrder oldOrder : this.allOrders) {
/*  334 */       if (oldOrder.getLabel().equals(label)) {
/*  335 */         throw new JFException("Cannot create order with label that already exists");
/*      */       }
/*      */     }
/*  338 */     TesterOrder order = new TesterOrder(this, label, instrument, null, 0.0D);
/*  339 */     this.ordersByInstrument[order.getInstrument().ordinal()].add(order);
/*  340 */     this.allOrders.add(order);
/*      */     try {
/*  342 */       Method method = super.getClass().getDeclaredMethod("mergeOrdersImpl", new Class[] { TesterOrder.class, com.dukascopy.api.IOrder.class, TesterReportData.TesterEvent.CloseTrigger.class });
/*  343 */       this.ordersToProcess.add(new Object[] { method, { order, orders, TesterReportData.TesterEvent.CloseTrigger.MERGE_BY_STRATEGY } });
/*      */     } catch (Exception e) {
/*  345 */       LOGGER.error(e.getMessage(), e);
/*  346 */       throw new JFException(e);
/*      */     }
/*  348 */     return order;
/*      */   }
/*      */ 
/*      */   public IOrder mergeOrders(String label, Collection<IOrder> orders) throws JFException {
/*  352 */     return mergeOrders(label, (IOrder[])orders.toArray(new IOrder[orders.size()]));
/*      */   }
/*      */ 
/*      */   protected boolean mergeOrdersImpl(TesterOrder resultingOrder, IOrder[] orders, TesterReportData.TesterEvent.CloseTrigger closeTrigger) {
/*  356 */     if (resultingOrder != null) {
/*  357 */       resultingOrder.createId();
/*      */     }
/*  359 */     String error = null;
/*  360 */     for (IOrder order : orders) {
/*  361 */       if (order.getState() != IOrder.State.FILLED) {
/*  362 */         error = "Cannot merge orders in state other than FILLED";
/*      */       }
/*  364 */       if (order.getStopLossPrice() != 0.0D) {
/*  365 */         error = "Cannot merge orders with stop loss";
/*      */       }
/*  367 */       if (order.getTakeProfitPrice() != 0.0D) {
/*  368 */         error = "Cannot merge orders with take profit";
/*      */       }
/*      */     }
/*  371 */     if (error != null) {
/*  372 */       if (resultingOrder != null) {
/*  373 */         resultingOrder.cancelOrder();
/*      */       }
/*  375 */       TesterMessage message = new TesterMessage(error, IMessage.Type.ORDERS_MERGE_REJECTED, resultingOrder, this.currentTime);
/*  376 */       if (resultingOrder != null) {
/*  377 */         resultingOrder.update(message);
/*      */       }
/*  379 */       fireOnMessage(message);
/*  380 */       return false;
/*      */     }
/*  382 */     double longAmount = 0.0D;
/*  383 */     double longPrice = 0.0D;
/*  384 */     double shortAmount = 0.0D;
/*  385 */     double shortPrice = 0.0D;
/*  386 */     for (IOrder order : orders) {
/*  387 */       double amount = ((TesterOrder)order).getAmountInUnits();
/*  388 */       if (order.getOrderCommand().isLong()) {
/*  389 */         longPrice += amount * order.getOpenPrice();
/*  390 */         longAmount += amount;
/*      */       } else {
/*  392 */         shortPrice += amount * order.getOpenPrice();
/*  393 */         shortAmount += amount;
/*      */       }
/*      */     }
/*  396 */     longAmount = StratUtils.roundHalfEven(longAmount, 2);
/*  397 */     shortAmount = StratUtils.roundHalfEven(shortAmount, 2);
/*  398 */     String lastLabel = "";
/*  399 */     for (IOrder order : orders) {
/*  400 */       ((TesterOrder)order).closeBeforeMerge();
/*  401 */       this.ordersByInstrument[order.getInstrument().ordinal()].remove(order);
/*  402 */       this.allOrders.remove(order);
/*  403 */       lastLabel = order.getLabel() + "_m";
/*      */     }
/*      */ 
/*  410 */     Instrument instrument = orders[0].getInstrument();
/*      */     double amountClosed;
/*      */     IEngine.OrderCommand orderCommand;
/*      */     double openAmount;
/*      */     double openPrice;
/*      */     double closePrice;
/*      */     double amountClosed;
/*  411 */     if (longAmount >= shortAmount) {
/*  412 */       IEngine.OrderCommand orderCommand = IEngine.OrderCommand.BUY;
/*  413 */       double openAmount = StratUtils.roundHalfEven(longAmount - shortAmount, 2);
/*  414 */       double openPrice = StratUtils.roundHalfEven(longPrice / longAmount, instrument.getPipScale() + 1);
/*      */       double amountClosed;
/*  415 */       if (shortAmount == 0.0D) {
/*  416 */         double closePrice = 0.0D;
/*  417 */         amountClosed = 0.0D;
/*      */       } else {
/*  419 */         double closePrice = StratUtils.roundHalfEven(shortPrice / shortAmount, instrument.getPipScale() + 1);
/*  420 */         amountClosed = shortAmount;
/*      */       }
/*      */     } else {
/*  423 */       orderCommand = IEngine.OrderCommand.SELL;
/*  424 */       openAmount = StratUtils.roundHalfEven(shortAmount - longAmount, 2);
/*  425 */       openPrice = StratUtils.roundHalfEven(shortPrice / shortAmount, instrument.getPipScale() + 1);
/*      */       double amountClosed;
/*  426 */       if (longAmount == 0.0D) {
/*  427 */         double closePrice = 0.0D;
/*  428 */         amountClosed = 0.0D;
/*      */       } else {
/*  430 */         closePrice = StratUtils.roundHalfEven(longPrice / longAmount, instrument.getPipScale() + 1);
/*  431 */         amountClosed = longAmount;
/*      */       }
/*      */     }
/*  434 */     if (longAmount != shortAmount) {
/*  435 */       if (resultingOrder != null) {
/*  436 */         resultingOrder.setOrderCommand(orderCommand);
/*  437 */         resultingOrder.setRequestedAmountSubmitted(openAmount);
/*  438 */         resultingOrder.setOpenPriceSubmitted(openPrice);
/*      */       } else {
/*  440 */         resultingOrder = new TesterOrder(this, lastLabel, instrument, orderCommand, openAmount, openPrice);
/*  441 */         this.ordersByInstrument[resultingOrder.getInstrument().ordinal()].add(resultingOrder);
/*  442 */         this.allOrders.add(resultingOrder);
/*      */       }
/*  444 */       resultingOrder.openOrder();
/*  445 */       resultingOrder.fillOrder(openPrice, openAmount);
/*      */     }
/*  447 */     else if (resultingOrder != null) {
/*  448 */       resultingOrder.closeNoOpenedOrder();
/*      */     }
/*      */ 
/*  451 */     if (amountClosed > 0.0D) {
/*  452 */       double profitLoss = calculateProfitLossInAccountCCY(instrument, orderCommand, openPrice, closePrice, amountClosed);
/*  453 */       this.account.setProfitLossOfClosedPositions(this.account.getProfitLossOfClosedPositions() + profitLoss);
/*  454 */       updateBalanceWithPLFromOrder(instrument, orderCommand, openPrice, closePrice, amountClosed);
/*      */     }
/*      */ 
/*  457 */     for (IOrder order : orders) {
/*  458 */       TesterMessage message = new TesterMessage("Order closed", IMessage.Type.ORDER_CLOSE_OK, order, this.currentTime);
/*  459 */       ((TesterOrder)order).update(message);
/*  460 */       fireOnMessage(message);
/*      */     }
/*  462 */     TesterMessage message = new TesterMessage("Orders merged", IMessage.Type.ORDERS_MERGE_OK, resultingOrder, this.currentTime);
/*  463 */     if (resultingOrder != null) {
/*  464 */       resultingOrder.update(message);
/*      */     }
/*  466 */     fireOnMessage(message);
/*      */ 
/*  468 */     addOrdersMergedReportData(orders, resultingOrder, closeTrigger);
/*  469 */     if (this.testerOrdersProvider != null) {
/*  470 */       this.testerOrdersProvider.ordersMergeOk(orders, resultingOrder, this.currentTime);
/*      */     }
/*  472 */     this.recalculate = true;
/*  473 */     return true;
/*      */   }
/*      */ 
/*      */   protected void addOrdersMergedReportData(IOrder[] mergedOrders, TesterOrder resultingOrder, TesterReportData.TesterEvent.CloseTrigger closeTrigger)
/*      */   {
/*  478 */     Instrument instrument = mergedOrders[0].getInstrument();
/*  479 */     InstrumentReportData instrumentReportData = this.testerReportData.getOrCreateInstrumentReportData(instrument);
/*  480 */     for (IOrder order : mergedOrders) {
/*  481 */       instrumentReportData.openedOrders.remove(order);
/*  482 */       instrumentReportData.closedOrders.add(order);
/*      */     }
/*      */ 
/*  485 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/*      */ 
/*  487 */     if (resultingOrder != null) {
/*  488 */       instrumentReportData.openedOrders.add(resultingOrder);
/*  489 */       event.label = resultingOrder.getLabel();
/*  490 */       event.amount = resultingOrder.getAmountInUnits();
/*  491 */       event.orderCommand = resultingOrder.getOrderCommand();
/*  492 */       event.openPrice = resultingOrder.getOpenPrice();
/*      */     }
/*  494 */     event.type = TesterReportData.TesterEvent.EventType.ORDERS_MERGED;
/*  495 */     event.instrument = instrument;
/*  496 */     event.time = this.currentTime;
/*  497 */     event.ordersMerged = mergedOrders;
/*  498 */     event.closeTrigger = closeTrigger;
/*  499 */     this.testerReportData.addEvent(event);
/*      */   }
/*      */ 
/*      */   private void submitOrder(TesterOrder order) throws JFException {
/*  503 */     if (!(order.isMcOrder())) {
/*  504 */       if (order.getInstrument() == null) {
/*  505 */         throw new JFException("Invalid parameter, instrument is null");
/*      */       }
/*  507 */       if (order.getOrderCommand() == null) {
/*  508 */         throw new JFException("Invalid parameter, orderCommand is null");
/*      */       }
/*      */     }
/*  511 */     order.setLabel(validateLabel(order.getLabel()));
/*  512 */     if (!(order.isMcOrder())) {
/*  513 */       validateOrder(false, order.getInstrument(), order.getOrderCommand(), order.getAmount(), order.getClientPrice(), order.getSlippage(), order.getStopLossPrice(), order.getTakeProfitPrice(), order.getGoodTillTime(), order.getComment());
/*      */ 
/*  515 */       if ((order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID) || (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER)) {
/*  516 */         FeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/*  517 */         if (feedDataProvider != null) {
/*  518 */           IFeedCommissionManager feedCommissionManager = FeedDataProvider.getDefaultInstance().getFeedCommissionManager();
/*  519 */           if (Double.compare(0.0D, feedCommissionManager.getFeedCommission(order.getInstrument(), this.currentTime)) != 0) {
/*  520 */             this.errors.add(new TesterMessage(generateFeedCommissionWarning(order.getInstrument()), IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime));
/*  521 */             return;
/*      */           }
/*      */         }
/*      */       }
/*  525 */       if (isWeekends()) {
/*  526 */         this.errors.add(new TesterMessage("System offline", IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime));
/*  527 */         return;
/*      */       }
/*  529 */       for (TesterOrder oldOrder : this.allOrders) {
/*  530 */         if (oldOrder.getLabel().equals(order.getLabel())) {
/*  531 */           throw new JFException("Cannot create order with label that already exists");
/*      */         }
/*      */       }
/*  534 */       if (this.activeInstruments[order.getInstrument().ordinal()] == 0) {
/*  535 */         this.errors.add(new TesterMessage("No liquidity", IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime));
/*  536 */         return;
/*      */       }
/*  538 */       ITick tick = this.lastTicks[order.getInstrument().ordinal()];
/*  539 */       if (tick == null) {
/*  540 */         this.errors.add(new TesterMessage("No liquidity", IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime));
/*  541 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  545 */     createSignal(order, (order.getOrderCommand().equals(IEngine.OrderCommand.BUY)) ? ISignal.Type.ORDER_BUY : ISignal.Type.ORDER_SELL);
/*      */ 
/*  547 */     if (this.mode == IEngine.StrategyMode.INDEPENDENT) {
/*  548 */       this.ordersByInstrument[order.getInstrument().ordinal()].add(order);
/*  549 */       this.allOrders.add(order);
/*      */       try {
/*  551 */         Method method = super.getClass().getDeclaredMethod("submitOrderImpl", new Class[] { TesterOrder.class });
/*  552 */         Method addOrderSubmittedReportDataMethod = super.getClass().getDeclaredMethod("addOrderSubmittedReportData", new Class[] { IOrder.class, Double.TYPE, Double.TYPE });
/*      */ 
/*  554 */         this.ordersToProcess.add(new Object[] { method, { order }, addOrderSubmittedReportDataMethod, { order, Double.valueOf(order.getAmountInUnits()), Double.valueOf(order.getClientPrice()) } });
/*      */       }
/*      */       catch (Exception e) {
/*  557 */         LOGGER.error(e.getMessage(), e);
/*  558 */         throw new JFException(e);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isWeekends() {
/*  564 */     if (this.weekendsStart == -9223372036854775808L) {
/*  565 */       Calendar stratThreadCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */ 
/*  567 */       stratThreadCalendar.setFirstDayOfWeek(2);
/*      */ 
/*  570 */       stratThreadCalendar.setTimeInMillis(this.currentTime);
/*  571 */       stratThreadCalendar = setDay(stratThreadCalendar, 6, 22);
/*  572 */       this.weekendsStart = stratThreadCalendar.getTimeInMillis();
/*  573 */       stratThreadCalendar = setDay(stratThreadCalendar, 1, 21);
/*  574 */       this.weekendsEnd = stratThreadCalendar.getTimeInMillis();
/*      */     }
/*  576 */     while (this.currentTime >= this.weekendsEnd)
/*      */     {
/*  578 */       this.weekendsStart += getWeekAsMillis();
/*  579 */       this.weekendsEnd += getWeekAsMillis();
/*      */     }
/*      */ 
/*  582 */     return (this.currentTime >= this.weekendsStart);
/*      */   }
/*      */ 
/*      */   public boolean isWeekendsMarginCut() {
/*  586 */     if (this.weekendsStartMarginCut == -9223372036854775808L) {
/*  587 */       Calendar stratThreadCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */ 
/*  590 */       stratThreadCalendar.setTimeInMillis(this.currentTime);
/*  591 */       stratThreadCalendar = setDay(stratThreadCalendar, 6, 18);
/*  592 */       this.weekendsStartMarginCut = stratThreadCalendar.getTimeInMillis();
/*  593 */       stratThreadCalendar = setDay(stratThreadCalendar, 1, 21);
/*  594 */       this.weekendsEndMarginCut = stratThreadCalendar.getTimeInMillis();
/*      */     }
/*  596 */     while (this.currentTime >= this.weekendsEndMarginCut)
/*      */     {
/*  598 */       this.weekendsStartMarginCut += getWeekAsMillis();
/*  599 */       this.weekendsEndMarginCut += getWeekAsMillis();
/*      */     }
/*      */ 
/*  602 */     return (this.currentTime >= this.weekendsStartMarginCut);
/*      */   }
/*      */ 
/*      */   private Calendar setDay(Calendar stratThreadCalendar, int dayIndex, int hours) {
/*  606 */     stratThreadCalendar.set(7, dayIndex);
/*  607 */     stratThreadCalendar.set(11, hours);
/*  608 */     stratThreadCalendar.set(12, 0);
/*  609 */     stratThreadCalendar.set(13, 0);
/*  610 */     stratThreadCalendar.set(14, 0);
/*      */ 
/*  612 */     return stratThreadCalendar;
/*      */   }
/*      */ 
/*      */   private long getWeekAsMillis() {
/*  616 */     return 604800000L;
/*      */   }
/*      */ 
/*      */   private void submitOrderImpl(TesterOrder order)
/*      */   {
/*  621 */     if (order.getState() == IOrder.State.CREATED) {
/*  622 */       if (order.getOrderCommand() == null)
/*      */       {
/*  624 */         return;
/*      */       }
/*      */ 
/*  627 */       if (order.getAmountInUnits() < ((Double)this.minTradableAmounts.get(order.getInstrument())).doubleValue()) {
/*  628 */         order.cancelOrder();
/*  629 */         TesterMessage message = new TesterMessage("Your order has been rejected due to validation failure. Invalid amount value, amount must be >= " + Long.toString(()((Double)this.minTradableAmounts.get(order.getInstrument())).doubleValue()), IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime);
/*  630 */         order.update(message);
/*  631 */         fireOnMessage(message);
/*  632 */         this.ordersByInstrument[order.getInstrument().ordinal()].remove(order);
/*  633 */         this.allOrders.remove(order);
/*  634 */         addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_VALIDATION);
/*  635 */         return;
/*      */       }
/*      */ 
/*  638 */       if ((order.getOrderCommand() != IEngine.OrderCommand.BUY) && (order.getOrderCommand() != IEngine.OrderCommand.SELL) && 
/*  639 */         (order.getOpenPrice() != StratUtils.round(order.getOpenPrice(), order.getInstrument().getPipScale() + 1))) {
/*  640 */         order.cancelOrder();
/*  641 */         TesterMessage message = new TesterMessage("Your order has been rejected due to validation failure. Invalid price format - please use increments of 0.1 pip", IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime);
/*  642 */         order.update(message);
/*  643 */         fireOnMessage(message);
/*  644 */         this.ordersByInstrument[order.getInstrument().ordinal()].remove(order);
/*  645 */         this.allOrders.remove(order);
/*  646 */         addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_VALIDATION);
/*  647 */         return;
/*      */       }
/*      */ 
/*  650 */       if ((order.getStopLossPrice() != 0.0D) && 
/*  651 */         (order.getStopLossPrice() != StratUtils.round(order.getStopLossPrice(), order.getInstrument().getPipScale() + 1))) {
/*  652 */         order.cancelOrder();
/*  653 */         TesterMessage message = new TesterMessage("Your order has been rejected due to validation failure. Invalid stop loss price - please use increments of 0.1 pip", IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime);
/*  654 */         order.update(message);
/*  655 */         fireOnMessage(message);
/*  656 */         this.ordersByInstrument[order.getInstrument().ordinal()].remove(order);
/*  657 */         this.allOrders.remove(order);
/*  658 */         addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_VALIDATION);
/*  659 */         return;
/*      */       }
/*      */ 
/*  662 */       if ((order.getTakeProfitPrice() != 0.0D) && 
/*  663 */         (order.getTakeProfitPrice() != StratUtils.round(order.getTakeProfitPrice(), order.getInstrument().getPipScale() + 1))) {
/*  664 */         order.cancelOrder();
/*  665 */         TesterMessage message = new TesterMessage("Your order has been rejected due to validation failure. Invalid take profit price - please use increments of 0.1 pip", IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime);
/*  666 */         order.update(message);
/*  667 */         fireOnMessage(message);
/*  668 */         this.ordersByInstrument[order.getInstrument().ordinal()].remove(order);
/*  669 */         this.allOrders.remove(order);
/*  670 */         addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_VALIDATION);
/*  671 */         return;
/*      */       }
/*      */ 
/*  675 */       if ((order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID) || (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER))
/*      */       {
/*  677 */         if (getAllowedAmountToTrade(order.getClientPrice(), order.getAmountInUnits(), order, true) <= 0.0D) {
/*  678 */           order.cancelOrder();
/*  679 */           TesterMessage message = new TesterMessage("No margin", IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime);
/*  680 */           order.update(message);
/*  681 */           fireOnMessage(message);
/*  682 */           this.ordersByInstrument[order.getInstrument().ordinal()].remove(order);
/*  683 */           this.allOrders.remove(order);
/*  684 */           addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_NO_MARGIN);
/*  685 */           return; }
/*  686 */         if ((order.getGoodTillTime() > 0L) && (order.getGoodTillTime() < getCurrentTime())) {
/*  687 */           order.cancelOrder();
/*  688 */           TesterMessage message = new TesterMessage("GoodTillTime in past", IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime);
/*  689 */           order.update(message);
/*  690 */           fireOnMessage(message);
/*  691 */           this.ordersByInstrument[order.getInstrument().ordinal()].remove(order);
/*  692 */           this.allOrders.remove(order);
/*  693 */           addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_VALIDATION);
/*  694 */           return;
/*      */         }
/*  696 */         this.recalculate = true;
/*      */       }
/*  698 */       order.openOrder();
/*  699 */       TesterMessage message = new TesterMessage("Order submitted", IMessage.Type.ORDER_SUBMIT_OK, order, this.currentTime);
/*  700 */       order.update(message);
/*  701 */       fireOnMessage(message);
/*  702 */       if (this.testerOrdersProvider != null)
/*  703 */         this.testerOrdersProvider.orderSubmitOk(order);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getNewOrderId()
/*      */   {
/*  709 */     return Long.toString(this.orderIdsSequence++);
/*      */   }
/*      */ 
/*      */   private void addOrderSubmittedReportData(IOrder order, double openAmount, double openPrice)
/*      */   {
/*  714 */     InstrumentReportData instrumentReportData = this.testerReportData.getOrCreateInstrumentReportData(order.getInstrument());
/*  715 */     instrumentReportData.ordersTotal += 1;
/*  716 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/*  717 */     event.type = TesterReportData.TesterEvent.EventType.ORDER_ENTRY;
/*  718 */     event.time = this.currentTime;
/*  719 */     event.label = order.getLabel();
/*  720 */     event.instrument = order.getInstrument();
/*  721 */     event.amount = openAmount;
/*  722 */     event.orderCommand = order.getOrderCommand();
/*  723 */     event.openPrice = openPrice;
/*  724 */     if (((TesterOrder)order).isMcOrder())
/*  725 */       event.openTrigger = TesterReportData.TesterEvent.OpenTrigger.OPEN_BY_MC;
/*      */     else {
/*  727 */       event.openTrigger = TesterReportData.TesterEvent.OpenTrigger.OPEN_BY_STRATEGY;
/*      */     }
/*  729 */     this.testerReportData.addEvent(event);
/*      */   }
/*      */ 
/*      */   private void executeConditionally(TesterOrder order) throws JFException {
/*  733 */     if (order.getState() == IOrder.State.OPENED) {
/*  734 */       fillOrderConditionally(order);
/*      */     }
/*  736 */     if (order.getState() == IOrder.State.FILLED) {
/*  737 */       closeOrderConditionally(order);
/*      */     }
/*  739 */     if ((order.getState() == IOrder.State.OPENED) || (order.getState() == IOrder.State.FILLED)) {
/*  740 */       updateTrailingStep(order);
/*      */     }
/*  742 */     if ((order.getState() == IOrder.State.CLOSED) && (order.getState() == IOrder.State.CANCELED)) {
/*  743 */       this.ordersByInstrument[order.getInstrument().ordinal()].remove(order);
/*  744 */       this.allOrders.remove(order);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateTrailingStep(TesterOrder order) {
/*  749 */     if ((order.getTrailingStep() != 0.0D) && (order.getLastClientPrice() != 0.0D)) {
/*  750 */       double currOrderPriceStop = order.getStopLossPrice();
/*  751 */       Instrument instrument = order.getInstrument();
/*  752 */       double currentPriceValue = (order.getStopLossSide() == OfferSide.ASK) ? this.lastTicks[instrument.ordinal()].getAsk() : this.lastTicks[instrument.ordinal()].getBid();
/*      */ 
/*  754 */       if (!(order.getOrderCommand().isLong())) {
/*  755 */         double interval = currOrderPriceStop - order.getLastClientPrice();
/*  756 */         if (StratUtils.roundHalfEven(currOrderPriceStop - currentPriceValue, instrument.getPipScale() + 2) >= StratUtils.roundHalfEven(interval + instrument.getPipValue() * order.getTrailingStep(), instrument.getPipScale() + 2))
/*      */         {
/*  758 */           order.setStopLossSubmitted(StratUtils.roundHalfEven(currentPriceValue + interval, instrument.getPipScale() + 2), order.getStopLossSide(), order.getTrailingStep(), currentPriceValue);
/*      */ 
/*  760 */           TesterMessage message = new TesterMessage("Stop loss price changed", IMessage.Type.ORDER_CHANGED_OK, order, this.currentTime);
/*  761 */           order.update(message);
/*  762 */           fireOnMessage(message);
/*  763 */           if (this.testerOrdersProvider != null)
/*  764 */             this.testerOrdersProvider.orderChangedOkSLTP(this.currentTime, order);
/*      */         }
/*      */       }
/*      */       else {
/*  768 */         double interval = order.getLastClientPrice() - currOrderPriceStop;
/*  769 */         if (StratUtils.roundHalfEven(currentPriceValue - currOrderPriceStop, instrument.getPipScale() + 2) < StratUtils.roundHalfEven(interval + instrument.getPipValue() * order.getTrailingStep(), instrument.getPipScale() + 2))
/*      */           return;
/*  771 */         order.setStopLossSubmitted(StratUtils.roundHalfEven(currentPriceValue - interval, instrument.getPipScale() + 2), order.getStopLossSide(), order.getTrailingStep(), currentPriceValue);
/*      */ 
/*  773 */         TesterMessage message = new TesterMessage("Stop loss price changed", IMessage.Type.ORDER_CHANGED_OK, order, this.currentTime);
/*  774 */         order.update(message);
/*  775 */         fireOnMessage(message);
/*  776 */         if (this.testerOrdersProvider != null)
/*  777 */           this.testerOrdersProvider.orderChangedOkSLTP(this.currentTime, order);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void closeOrderConditionally(TesterOrder order)
/*      */     throws JFException
/*      */   {
/*  785 */     ITick tick = this.lastTicks[order.getInstrument().ordinal()];
/*  786 */     if (order.getStopLossPrice() != 0.0D) {
/*  787 */       if (order.getOrderCommand().isLong()) {
/*  788 */         if (order.getStopLossSide() == OfferSide.BID) {
/*  789 */           double bid = tick.getBid();
/*  790 */           if (bid <= order.getStopLossPrice()) {
/*  791 */             closeOrderImpl(order, -1.0D, order.getAmountInUnits(), false, TesterReportData.TesterEvent.CloseTrigger.CLOSE_BY_STOP_LOSS);
/*  792 */             return;
/*      */           }
/*      */         } else {
/*  795 */           double ask = tick.getAsk();
/*  796 */           if (ask <= order.getStopLossPrice()) {
/*  797 */             closeOrderImpl(order, -1.0D, order.getAmountInUnits(), false, TesterReportData.TesterEvent.CloseTrigger.CLOSE_BY_STOP_LOSS);
/*  798 */             return;
/*      */           }
/*      */         }
/*      */       }
/*  802 */       else if (order.getStopLossSide() == OfferSide.BID) {
/*  803 */         double bid = tick.getBid();
/*  804 */         if (bid >= order.getStopLossPrice()) {
/*  805 */           closeOrderImpl(order, -1.0D, order.getAmountInUnits(), false, TesterReportData.TesterEvent.CloseTrigger.CLOSE_BY_STOP_LOSS);
/*  806 */           return;
/*      */         }
/*      */       } else {
/*  809 */         double ask = tick.getAsk();
/*  810 */         if (ask >= order.getStopLossPrice()) {
/*  811 */           closeOrderImpl(order, -1.0D, order.getAmountInUnits(), false, TesterReportData.TesterEvent.CloseTrigger.CLOSE_BY_STOP_LOSS);
/*  812 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  817 */     if (order.getTakeProfitPrice() != 0.0D)
/*  818 */       if (order.getOrderCommand().isLong()) {
/*  819 */         double bid = tick.getBid();
/*  820 */         if (bid >= order.getTakeProfitPrice()) {
/*  821 */           closeOrderImpl(order, -1.0D, order.getAmountInUnits(), false, TesterReportData.TesterEvent.CloseTrigger.CLOSE_BY_TAKE_PROFIT);
/*  822 */           return;
/*      */         }
/*      */       } else {
/*  825 */         double ask = tick.getAsk();
/*  826 */         if (ask <= order.getTakeProfitPrice()) {
/*  827 */           closeOrderImpl(order, -1.0D, order.getAmountInUnits(), false, TesterReportData.TesterEvent.CloseTrigger.CLOSE_BY_TAKE_PROFIT);
/*  828 */           return;
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   private void addOrderClosedReportData(IOrder order, double closePrice, double closeAmount, TesterReportData.TesterEvent.CloseTrigger closeTrigger)
/*      */   {
/*  835 */     InstrumentReportData instrumentReportData = this.testerReportData.getOrCreateInstrumentReportData(order.getInstrument());
/*  836 */     if (order.getState() == IOrder.State.CLOSED) {
/*  837 */       instrumentReportData.closedOrders.add(order);
/*  838 */       instrumentReportData.openedOrders.remove(order);
/*      */     }
/*      */ 
/*  841 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/*  842 */     event.type = TesterReportData.TesterEvent.EventType.ORDER_CLOSE;
/*  843 */     event.time = this.currentTime;
/*  844 */     event.label = order.getLabel();
/*  845 */     event.instrument = order.getInstrument();
/*  846 */     event.amount = ((TesterOrder)order).getLastCloseAmount();
/*  847 */     event.orderCommand = order.getOrderCommand();
/*  848 */     event.openPrice = order.getOpenPrice();
/*  849 */     event.closePrice = closePrice;
/*  850 */     event.closeAmount = closeAmount;
/*  851 */     event.closeTrigger = closeTrigger;
/*  852 */     this.testerReportData.addEvent(event);
/*      */   }
/*      */ 
/*      */   public synchronized void closeOrder(TesterOrder order) throws JFException {
/*      */     try {
/*  857 */       Method method = super.getClass().getDeclaredMethod("closeOrderImpl", new Class[] { TesterOrder.class, Double.TYPE, Double.TYPE, Boolean.TYPE, TesterReportData.TesterEvent.CloseTrigger.class });
/*  858 */       this.ordersToProcess.add(new Object[] { method, { order, Double.valueOf(-1.0D), Double.valueOf(order.getAmountInUnits()), Boolean.valueOf(false), TesterReportData.TesterEvent.CloseTrigger.CLOSE_BY_STRATEGY } });
/*      */     } catch (Exception e) {
/*  860 */       LOGGER.error(e.getMessage(), e);
/*  861 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private double calculateProfitLossInAccountCCY(Instrument instrument, IEngine.OrderCommand orderCommand, double openPrice, double closePrice, double amount)
/*      */   {
/*      */     double profLossInSecondaryCCY;
/*      */   
/*  867 */     if (orderCommand.isLong())
/*  868 */       profLossInSecondaryCCY = StratUtils.roundHalfEven((closePrice - openPrice) * amount, 2);
/*      */     else {
/*  870 */       profLossInSecondaryCCY = StratUtils.roundHalfEven((openPrice - closePrice) * amount, 2);
/*      */     }
/*  872 */     OfferSide side = (orderCommand.isLong()) ? OfferSide.ASK : OfferSide.BID;
/*  873 */     return StratUtils.roundHalfEven(this.currencyConverter.convert(profLossInSecondaryCCY, instrument.getSecondaryCurrency(), this.account.getCurrency(), side), 2);
/*      */   }
/*      */ 
/*      */   public synchronized void closeOrder(TesterOrder order, double price, double amount) throws JFException
/*      */   {
/*  878 */     if (amount < ((Double)this.minTradableAmounts.get(order.getInstrument())).doubleValue()) {
/*  879 */       String minTradableAmount = Long.toString(()((Double)this.minTradableAmounts.get(order.getInstrument())).doubleValue());
/*  880 */       String userTypeDescription = this.account.getUserTypeDescription();
/*  881 */       throw new JFException("userType=" + userTypeDescription + " Amount cannot be less than " + minTradableAmount);
/*      */     }
/*      */ 
/*  884 */     createSignal(order, ISignal.Type.ORDER_CLOSE);
/*      */ 
/*  886 */     if (!(this.mode.equals(IEngine.StrategyMode.INDEPENDENT))) return;
/*      */     try {
/*  888 */       Method method = super.getClass().getDeclaredMethod("closeOrderImpl", new Class[] { TesterOrder.class, Double.TYPE, Double.TYPE, Boolean.TYPE, TesterReportData.TesterEvent.CloseTrigger.class });
/*      */ 
/*  890 */       this.ordersToProcess.add(new Object[] { method, { order, Double.valueOf(price), Double.valueOf(amount), Boolean.valueOf(false), TesterReportData.TesterEvent.CloseTrigger.CLOSE_BY_STRATEGY } });
/*      */     }
/*      */     catch (Exception e) {
/*  893 */       LOGGER.error(e.getMessage(), e);
/*  894 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void closeOrders(IOrder[] orders) throws JFException
/*      */   {
/*  900 */     for (IOrder order : orders) {
/*  901 */       if (order.getState() != IOrder.State.FILLED) {
/*  902 */         throw new JFException("Cannot mass close orders that are not in FILLED state");
/*      */       }
/*      */     }
/*  905 */     for (IOrder order : orders) {
/*  906 */       createSignal(order, ISignal.Type.ORDER_CLOSE);
/*  907 */       if (this.mode.equals(IEngine.StrategyMode.INDEPENDENT))
/*  908 */         order.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean closeOrderImpl(TesterOrder order, double price, double amount, boolean noCheck, TesterReportData.TesterEvent.CloseTrigger closeTrigger)
/*      */   {
/*  914 */     if (order.getState() != IOrder.State.FILLED) {
/*  915 */       TesterMessage message = new TesterMessage("Cannot close order not in FILLED state", IMessage.Type.ORDER_CLOSE_REJECTED, order, this.currentTime);
/*  916 */       order.update(message);
/*  917 */       fireOnMessage(message);
/*  918 */       return false;
/*      */     }
/*  920 */     ITick tick = this.lastTicks[order.getInstrument().ordinal()];
/*      */     double closePrice;
/*  922 */     if (order.getOrderCommand().isLong()) {
/*  923 */       double bid = tick.getBid();
/*  924 */       if (price < 0.0D)
/*  925 */         price = bid;
/*      */       double closePrice;
/*  927 */       if (price <= bid) {
/*  928 */         closePrice = bid;
/*      */       } else {
/*  930 */         TesterMessage message = new TesterMessage("No liquidity at price [" + price + "]", IMessage.Type.ORDER_CLOSE_REJECTED, order, this.currentTime);
/*  931 */         order.update(message);
/*  932 */         fireOnMessage(message);
/*  933 */         return false;
/*      */       }
/*      */     } else {
/*  936 */       double ask = tick.getAsk();
/*  937 */       if (price < 0.0D)
/*  938 */         price = ask;
/*      */       double closePrice;
/*  940 */       if (price >= ask) {
/*  941 */         closePrice = ask;
/*      */       } else {
/*  943 */         TesterMessage message = new TesterMessage("No liquidity at price [" + price + "]", IMessage.Type.ORDER_CLOSE_REJECTED, order, this.currentTime);
/*  944 */         order.update(message);
/*  945 */         fireOnMessage(message);
/*  946 */         return false;
/*      */       }
/*      */     }
/*      */     double closePrice;
/*      */     double newAmount;
/*      */     double newAmount;
/*  951 */     if (noCheck)
/*  952 */       newAmount = amount;
/*      */     else {
/*  954 */       newAmount = getAllowedAmountToTrade(closePrice, amount, order, false);
/*      */     }
/*      */ 
/*  957 */     if (newAmount > 0.1D)
/*      */     {
/*  960 */       double amountInUnits = order.getAmountInUnits();
/*  961 */       if (amountInUnits - newAmount < 1.0E-007D) {
/*  962 */         double profitLoss = calculateProfitLossInAccountCCY(order.getInstrument(), order.getOrderCommand(), order.getOpenPrice(), closePrice, amountInUnits);
/*  963 */         order.closeOrder(closePrice, profitLoss);
/*  964 */         this.account.setProfitLossOfClosedPositions(this.account.getProfitLossOfClosedPositions() + profitLoss);
/*      */ 
/*  967 */         double amountSecCCY = amountInUnits * closePrice;
/*  968 */         double commission = calculateCommission(amountSecCCY, order.getInstrument().getSecondaryCurrency());
/*  969 */         order.addCommission(commission);
/*      */ 
/*  971 */         updateBalanceWithPLFromOrder(order.getInstrument(), order.getOrderCommand(), order.getOpenPrice(), closePrice, amountInUnits);
/*  972 */         this.tradedAmountsInSecCCY[order.getInstrument().ordinal()] = StratUtils.roundHalfEven(this.tradedAmountsInSecCCY[order.getInstrument().ordinal()] + amountSecCCY, 2);
/*  973 */         this.ordersByInstrument[order.getInstrument().ordinal()].remove(order);
/*  974 */         this.allOrders.remove(order);
/*  975 */         if (this.testerOrdersProvider != null) {
/*  976 */           this.testerOrdersProvider.orderCloseOk(order, amountInUnits);
/*      */         }
/*  978 */         TesterMessage message = new TesterMessage("Order closed", IMessage.Type.ORDER_CLOSE_OK, order, this.currentTime);
/*  979 */         order.update(message);
/*  980 */         fireOnMessage(message);
/*  981 */         addOrderClosedReportData(order, closePrice, amountInUnits, closeTrigger);
/*      */       } else {
/*  983 */         double profitLoss = calculateProfitLossInAccountCCY(order.getInstrument(), order.getOrderCommand(), order.getOpenPrice(), closePrice, newAmount);
/*  984 */         order.partialClose(closePrice, newAmount, profitLoss, getCurrentTime());
/*  985 */         this.account.setProfitLossOfClosedPositions(this.account.getProfitLossOfClosedPositions() + profitLoss);
/*      */ 
/*  988 */         double amountSecCCY = newAmount * closePrice;
/*  989 */         double commission = calculateCommission(amountSecCCY, order.getInstrument().getSecondaryCurrency());
/*  990 */         order.addCommission(commission);
/*      */ 
/*  992 */         updateBalanceWithPLFromOrder(order.getInstrument(), order.getOrderCommand(), order.getOpenPrice(), closePrice, newAmount);
/*  993 */         this.tradedAmountsInSecCCY[order.getInstrument().ordinal()] = StratUtils.roundHalfEven(this.tradedAmountsInSecCCY[order.getInstrument().ordinal()] + amountSecCCY, 2);
/*  994 */         if (this.testerOrdersProvider != null) {
/*  995 */           this.testerOrdersProvider.orderCloseOk(order, amountInUnits);
/*      */         }
/*  997 */         TesterMessage message = new TesterMessage("Order partially closed", IMessage.Type.ORDER_CLOSE_OK, order, this.currentTime);
/*  998 */         order.update(message);
/*  999 */         fireOnMessage(message);
/* 1000 */         addOrderClosedReportData(order, closePrice, newAmount, closeTrigger);
/*      */       }
/* 1002 */       this.recalculate = true;
/* 1003 */       return true;
/*      */     }
/* 1005 */     TesterMessage message = new TesterMessage("No margin to close order, try to merge it", IMessage.Type.ORDER_CLOSE_REJECTED, order, this.currentTime);
/* 1006 */     order.update(message);
/* 1007 */     fireOnMessage(message);
/* 1008 */     return false;
/*      */   }
/*      */ 
/*      */   private void updateBalanceWithPLFromOrder(Instrument instrument, IEngine.OrderCommand orderCommand, double openPrice, double closePrice, double amount)
/*      */   {
/*      */     OfferSide side;
/*      */     double proffLoseInSecCCY;
/*      */     OfferSide side;
/* 1015 */     if (orderCommand.isLong()) {
/* 1016 */       double proffLoseInSecCCY = (closePrice - openPrice) * amount;
/* 1017 */       side = OfferSide.BID;
/*      */     } else {
/* 1019 */       proffLoseInSecCCY = (openPrice - closePrice) * amount;
/* 1020 */       side = OfferSide.ASK;
/*      */     }
/* 1022 */     double convertedAmount = this.currencyConverter.convert(proffLoseInSecCCY, instrument.getSecondaryCurrency(), this.account.getCurrency(), side);
/* 1023 */     this.account.setRealizedEquity(StratUtils.roundHalfEven(this.account.getRealizedEquity() + convertedAmount, 2));
/*      */   }
/*      */ 
/*      */   private void fillOrderConditionally(TesterOrder order) throws JFException {
/* 1027 */     Instrument instrument = order.getInstrument();
/* 1028 */     ITick tick = this.lastTicks[instrument.ordinal()];
/* 1029 */     assert (tick != null);
/* 1030 */     double newAmount = order.getAmountInUnits();
/*      */     double fillPrice;
/*      */     double ask;
/*      */     double fillPrice;
/*      */     double bid;
/*      */     double fillPrice;
/*      */     double ask;
/*      */     double fillPrice;
/*      */     double fillPrice;
/*      */     double fillPrice;
/*      */     double ask;
/*      */     double fillPrice;
/*      */     double fillPrice;
/*      */     double fillPrice;
/* 1032 */     switch (4.$SwitchMap$com$dukascopy$api$IEngine$OrderCommand[order.getOrderCommand().ordinal()])
/*      */     {
/*      */     case 1:
/* 1034 */       fillPrice = tick.getAsk();
/* 1035 */       if ((order.getClientPrice() != 0.0D) && (StratUtils.roundHalfEven(order.getClientPrice() + order.getSlippage() * instrument.getPipValue(), instrument.getPipScale() + 2) < fillPrice))
/*      */       {
/* 1037 */         order.cancelOrder(this.currentTime);
/* 1038 */         this.ordersByInstrument[instrument.ordinal()].remove(order);
/* 1039 */         this.allOrders.remove(order);
/* 1040 */         TesterMessage message = new TesterMessage("No liquidity at price specified", IMessage.Type.ORDER_FILL_REJECTED, order, this.currentTime);
/* 1041 */         order.update(message);
/* 1042 */         fireOnMessage(message);
/* 1043 */         if (this.testerOrdersProvider != null) {
/* 1044 */           this.testerOrdersProvider.orderFillRejected(order);
/*      */         }
/* 1046 */         addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_NO_LIQUIDITY);
/* 1047 */         return;
/*      */       }
/*      */     case 2:
/* 1051 */       ask = tick.getAsk();
/* 1052 */       if (ask <= order.getOpenPrice())
/* 1053 */         fillPrice = ask;
/*      */       else {
/* 1055 */         fillPrice = -1.0D;
/*      */       }
/* 1057 */       break;
/*      */     case 3:
/* 1059 */       bid = tick.getBid();
/* 1060 */       if (bid <= order.getOpenPrice()) {
/* 1061 */         ask = tick.getAsk();
/* 1062 */         fillPrice = ask;
/*      */       } else {
/* 1064 */         fillPrice = -1.0D;
/*      */       }
/* 1066 */       break;
/*      */     case 4:
/* 1068 */       ask = tick.getAsk();
/* 1069 */       if (ask >= order.getOpenPrice())
/* 1070 */         fillPrice = ask;
/*      */       else {
/* 1072 */         fillPrice = -1.0D;
/*      */       }
/* 1074 */       break;
/*      */     case 5:
/* 1076 */       bid = tick.getBid();
/* 1077 */       if (bid >= order.getOpenPrice()) {
/* 1078 */         ask = tick.getAsk();
/* 1079 */         fillPrice = ask;
/*      */       } else {
/* 1081 */         fillPrice = -1.0D;
/*      */       }
/* 1083 */       break;
/*      */     case 6:
/* 1085 */       fillPrice = tick.getBid();
/* 1086 */       if ((order.getClientPrice() != 0.0D) && (StratUtils.roundHalfEven(order.getClientPrice() - (order.getSlippage() * instrument.getPipValue()), instrument.getPipScale() + 2) > fillPrice))
/*      */       {
/* 1088 */         order.cancelOrder(this.currentTime);
/* 1089 */         this.ordersByInstrument[instrument.ordinal()].remove(order);
/* 1090 */         this.allOrders.remove(order);
/* 1091 */         TesterMessage message = new TesterMessage("No liquidity at price specified", IMessage.Type.ORDER_FILL_REJECTED, order, this.currentTime);
/* 1092 */         order.update(message);
/* 1093 */         fireOnMessage(message);
/* 1094 */         if (this.testerOrdersProvider != null) {
/* 1095 */           this.testerOrdersProvider.orderFillRejected(order);
/*      */         }
/* 1097 */         addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_NO_LIQUIDITY);
/* 1098 */         return;
/*      */       }
/*      */     case 7:
/* 1102 */       bid = tick.getBid();
/* 1103 */       if (bid >= order.getOpenPrice())
/* 1104 */         fillPrice = bid;
/*      */       else {
/* 1106 */         fillPrice = -1.0D;
/*      */       }
/* 1108 */       break;
/*      */     case 8:
/* 1110 */       ask = tick.getAsk();
/* 1111 */       if (ask >= order.getOpenPrice())
/* 1112 */         fillPrice = tick.getBid();
/*      */       else {
/* 1114 */         fillPrice = -1.0D;
/*      */       }
/* 1116 */       break;
/*      */     case 9:
/* 1118 */       bid = tick.getBid();
/* 1119 */       if (bid <= order.getOpenPrice())
/* 1120 */         fillPrice = bid;
/*      */       else {
/* 1122 */         fillPrice = -1.0D;
/*      */       }
/* 1124 */       break;
/*      */     case 10:
/* 1126 */       ask = tick.getAsk();
/* 1127 */       if (ask <= order.getOpenPrice())
/* 1128 */         fillPrice = tick.getBid();
/*      */       else {
/* 1130 */         fillPrice = -1.0D;
/*      */       }
/* 1132 */       break;
/*      */     case 11:
/* 1134 */       ask = tick.getAsk();
/* 1135 */       if (ask <= order.getOpenPrice()) {
/* 1136 */         order.fillOrder(ask, newAmount);
/*      */ 
/* 1139 */         double amountSecCCY = newAmount * ask;
/* 1140 */         double commission = calculateCommission(amountSecCCY, order.getInstrument().getSecondaryCurrency());
/* 1141 */         order.addCommission(commission);
/*      */ 
/* 1143 */         TesterMessage message = new TesterMessage("Order filled", IMessage.Type.ORDER_FILL_OK, order, this.currentTime);
/* 1144 */         order.update(message);
/* 1145 */         fireOnMessage(message);
/* 1146 */         this.tradedAmountsInSecCCY[instrument.ordinal()] = StratUtils.roundHalfEven(this.tradedAmountsInSecCCY[instrument.ordinal()] + amountSecCCY, 2);
/* 1147 */         addOrderFilledReportData(order);
/* 1148 */         this.recalculate = true;
/* 1149 */         if (this.testerOrdersProvider != null)
/* 1150 */           this.testerOrdersProvider.orderFillOk(order);
/*      */       }
/* 1152 */       else if ((order.getGoodTillTime() > 0L) && (order.getGoodTillTime() < this.currentTime)) {
/* 1153 */         cancelOrderImpl(order);
/* 1154 */         addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_TIMEOUT);
/* 1155 */         this.recalculate = true;
/*      */       }
/* 1157 */       return;
/*      */     case 12:
/* 1159 */       bid = tick.getBid();
/* 1160 */       if (bid >= order.getOpenPrice()) {
/* 1161 */         order.fillOrder(bid, newAmount);
/*      */ 
/* 1164 */         double amountSecCCY = newAmount * bid;
/* 1165 */         double commission = calculateCommission(amountSecCCY, order.getInstrument().getSecondaryCurrency());
/* 1166 */         order.addCommission(commission);
/*      */ 
/* 1168 */         TesterMessage message = new TesterMessage("Order filled", IMessage.Type.ORDER_FILL_OK, order, this.currentTime);
/* 1169 */         order.update(message);
/* 1170 */         fireOnMessage(message);
/* 1171 */         this.tradedAmountsInSecCCY[instrument.ordinal()] = StratUtils.roundHalfEven(this.tradedAmountsInSecCCY[instrument.ordinal()] + amountSecCCY, 2);
/* 1172 */         addOrderFilledReportData(order);
/* 1173 */         this.recalculate = true;
/* 1174 */         if (this.testerOrdersProvider != null)
/* 1175 */           this.testerOrdersProvider.orderFillOk(order);
/*      */       }
/* 1177 */       else if ((order.getGoodTillTime() > 0L) && (order.getGoodTillTime() < this.currentTime)) {
/* 1178 */         cancelOrderImpl(order);
/* 1179 */         addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_TIMEOUT);
/* 1180 */         this.recalculate = true;
/*      */       }
/* 1182 */       return;
/*      */     default:
/* 1184 */       return;
/*      */     }
/*      */ 
/* 1187 */     if (fillPrice < 0.0D) {
/* 1188 */       return;
/*      */     }
/* 1190 */     newAmount = getAllowedAmountToTrade(fillPrice, newAmount, order, true);
/* 1191 */     if (newAmount > 0.1D) {
/* 1192 */       order.fillOrder(fillPrice, newAmount);
/*      */ 
/* 1195 */       double amountSecCCY = newAmount * fillPrice;
/* 1196 */       double commission = calculateCommission(amountSecCCY, order.getInstrument().getSecondaryCurrency());
/* 1197 */       order.addCommission(commission);
/*      */ 
/* 1199 */       addOrderFilledReportData(order);
/* 1200 */       TesterMessage message = new TesterMessage("Order filled", IMessage.Type.ORDER_FILL_OK, order, this.currentTime);
/* 1201 */       order.update(message);
/* 1202 */       fireOnMessage(message);
/* 1203 */       this.tradedAmountsInSecCCY[instrument.ordinal()] = StratUtils.roundHalfEven(this.tradedAmountsInSecCCY[instrument.ordinal()] + amountSecCCY, 2);
/* 1204 */       this.recalculate = true;
/* 1205 */       if (this.testerOrdersProvider != null)
/* 1206 */         this.testerOrdersProvider.orderFillOk(order);
/*      */     }
/*      */     else {
/* 1209 */       cancelOrderImpl(order);
/* 1210 */       addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_NO_MARGIN);
/* 1211 */       TesterMessage message = new TesterMessage("No margin available", IMessage.Type.ORDER_FILL_REJECTED, order, this.currentTime);
/* 1212 */       order.update(message);
/* 1213 */       fireOnMessage(message);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void cancelTimedoutPBPOOrders()
/*      */   {
/* 1219 */     for (IOrder order : this.allOrders)
/* 1220 */       if (order.getState() == IOrder.State.OPENED)
/*      */         try {
/* 1222 */           switch (4.$SwitchMap$com$dukascopy$api$IEngine$OrderCommand[order.getOrderCommand().ordinal()])
/*      */           {
/*      */           case 11:
/* 1224 */             if ((order.getGoodTillTime() > 0L) && (order.getGoodTillTime() < this.currentTime)) {
/* 1225 */               cancelOrderImpl((TesterOrder)order);
/* 1226 */               addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_TIMEOUT);
/* 1227 */               this.recalculate = true;
/*      */             }
/* 1229 */             return;
/*      */           case 12:
/* 1231 */             if ((order.getGoodTillTime() > 0L) && (order.getGoodTillTime() < this.currentTime)) {
/* 1232 */               cancelOrderImpl((TesterOrder)order);
/* 1233 */               addOrderCanceledReportData(order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_TIMEOUT);
/* 1234 */               this.recalculate = true;
/*      */             }
/* 1236 */             return;
/*      */           }
/*      */         } catch (Exception e) {
/* 1239 */           LOGGER.error(e.getMessage(), e);
/* 1240 */           TesterMessage message = new TesterMessage("Unexpected execution error", IMessage.Type.NOTIFICATION, order, this.currentTime);
/* 1241 */           ((TesterOrder)order).update(message);
/* 1242 */           fireOnMessage(message);
/*      */         }
/*      */   }
/*      */ 
/*      */   private void addOrderFilledReportData(TesterOrder order)
/*      */   {
/* 1249 */     InstrumentReportData instrumentReportData = this.testerReportData.getOrCreateInstrumentReportData(order.getInstrument());
/* 1250 */     instrumentReportData.openedOrders.add(order);
/* 1251 */     instrumentReportData.positionsTotal += 1;
/* 1252 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/* 1253 */     event.type = TesterReportData.TesterEvent.EventType.ORDER_FILLED;
/* 1254 */     event.time = this.currentTime;
/* 1255 */     event.label = order.getLabel();
/* 1256 */     event.instrument = order.getInstrument();
/* 1257 */     event.amount = order.getAmountInUnits();
/* 1258 */     event.orderCommand = order.getOrderCommand();
/* 1259 */     event.openPrice = order.getOpenPrice();
/* 1260 */     this.testerReportData.addEvent(event);
/*      */   }
/*      */ 
/*      */   private double getAllowedAmountToTrade(double openPriceDouble, double amount, TesterOrder order, boolean opening)
/*      */   {
/* 1265 */     double newAmount = amount;
/* 1266 */     double secondaryAmount = newAmount * openPriceDouble;
/* 1267 */     double amountInUSD = this.currencyConverter.convert(secondaryAmount, order.getInstrument().getSecondaryCurrency(), this.account.getCurrency(), (((opening) && (order.getOrderCommand().isLong())) || ((!(opening)) && (!(order.getOrderCommand().isLong())))) ? OfferSide.ASK : OfferSide.BID);
/*      */ 
/* 1270 */     if (amountInUSD > this.account.getCreditLineActual()) {
/* 1271 */       double usedMargineByInstrument = getUsedMargineByInstrumentWithOrder(order.getInstrument(), null, 0.0D, true, openPriceDouble);
/* 1272 */       double newUsedMargineByInstrument = getUsedMargineByInstrumentWithOrder(order.getInstrument(), order, amount, opening, openPriceDouble);
/* 1273 */       double margineDiff = ((newUsedMargineByInstrument <= 0.0D) ? -newUsedMargineByInstrument : newUsedMargineByInstrument) - ((usedMargineByInstrument <= 0.0D) ? -usedMargineByInstrument : usedMargineByInstrument);
/*      */ 
/* 1275 */       if (margineDiff > this.account.getCreditLineActual())
/*      */       {
/* 1278 */         if ((opening) && (((order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID) || (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER)))) {
/* 1279 */           newAmount = 0.0D;
/*      */         } else {
/* 1281 */           double marginWeDontHave = margineDiff - this.account.getCreditLineActual();
/* 1282 */           double amountWeCantTradeInSecCCY = this.currencyConverter.convert(marginWeDontHave, this.account.getCurrency(), order.getInstrument().getSecondaryCurrency(), (usedMargineByInstrument < 0.0D) ? OfferSide.ASK : OfferSide.BID);
/*      */ 
/* 1284 */           double amountWeCantTrade = StratUtils.roundHalfEven(amountWeCantTradeInSecCCY / openPriceDouble, 2);
/* 1285 */           newAmount = amount - amountWeCantTrade;
/*      */         }
/*      */       }
/*      */     }
/* 1289 */     newAmount = Math.floor(newAmount);
/* 1290 */     if ((opening) && (!(order.isMcOrder())) && (newAmount < ((Double)this.minTradableAmounts.get(order.getInstrument())).doubleValue())) {
/* 1291 */       return 0.0D;
/*      */     }
/* 1293 */     return newAmount;
/*      */   }
/*      */ 
/*      */   public synchronized void cancelOrder(TesterOrder order) throws JFException
/*      */   {
/* 1298 */     createSignal(order, ISignal.Type.ORDER_CANCEL);
/*      */ 
/* 1300 */     if (!(this.mode.equals(IEngine.StrategyMode.INDEPENDENT))) return;
/*      */     try {
/* 1302 */       Method method = super.getClass().getDeclaredMethod("cancelOrderImpl", new Class[] { TesterOrder.class });
/* 1303 */       Method addOrderCanceledReportDataMethod = super.getClass().getDeclaredMethod("addOrderCanceledReportData", new Class[] { IOrder.class, TesterReportData.TesterEvent.CloseTrigger.class });
/*      */ 
/* 1305 */       this.ordersToProcess.add(new Object[] { method, { order }, addOrderCanceledReportDataMethod, { order, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_STRATEGY } });
/*      */     }
/*      */     catch (Exception e) {
/* 1308 */       LOGGER.error(e.getMessage(), e);
/* 1309 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addOrderCanceledReportData(IOrder order, TesterReportData.TesterEvent.CloseTrigger closeTrigger)
/*      */   {
/* 1315 */     InstrumentReportData instrumentReportData = this.testerReportData.getOrCreateInstrumentReportData(order.getInstrument());
/* 1316 */     instrumentReportData.openedOrders.remove(order);
/*      */ 
/* 1318 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/* 1319 */     event.type = TesterReportData.TesterEvent.EventType.ORDER_CANCEL;
/* 1320 */     event.time = this.currentTime;
/* 1321 */     event.label = order.getLabel();
/* 1322 */     event.instrument = order.getInstrument();
/* 1323 */     event.amount = ((TesterOrder)order).getAmountInUnits();
/* 1324 */     event.orderCommand = order.getOrderCommand();
/* 1325 */     event.openPrice = order.getOpenPrice();
/* 1326 */     event.closeTrigger = closeTrigger;
/* 1327 */     this.testerReportData.addEvent(event);
/*      */   }
/*      */ 
/*      */   protected boolean cancelOrderImpl(TesterOrder order) {
/* 1331 */     return cancelOrderImpl(order, this.currentTime);
/*      */   }
/*      */ 
/*      */   private boolean cancelOrderImpl(TesterOrder order, long closeTime)
/*      */   {
/* 1336 */     if (order.getState() != IOrder.State.OPENED) {
/* 1337 */       TesterMessage message = new TesterMessage("Cannot cancel order in not OPENED state", IMessage.Type.ORDER_CLOSE_REJECTED, order, this.currentTime);
/* 1338 */       order.update(message);
/* 1339 */       fireOnMessage(message);
/* 1340 */       return false;
/*      */     }
/* 1342 */     order.cancelOrder(closeTime);
/* 1343 */     this.ordersByInstrument[order.getInstrument().ordinal()].remove(order);
/* 1344 */     this.allOrders.remove(order);
/* 1345 */     TesterMessage message = new TesterMessage("Order canceled", IMessage.Type.ORDER_CLOSE_OK, order, this.currentTime);
/* 1346 */     order.update(message);
/* 1347 */     fireOnMessage(message);
/* 1348 */     this.recalculate = true;
/* 1349 */     if (this.testerOrdersProvider != null) {
/* 1350 */       this.testerOrdersProvider.orderCancelOk(order);
/*      */     }
/* 1352 */     return true;
/*      */   }
/*      */ 
/*      */   public synchronized void addCurrentTime(long time) {
/* 1356 */     this.currentTime = time;
/* 1357 */     if ((this.lastAccountSendTime == -9223372036854775808L) || (this.lastAccountSendTime + 5000L < this.currentTime)) {
/* 1358 */       sendAccountInfo();
/* 1359 */       this.lastAccountSendTime = this.currentTime;
/*      */     }
/*      */ 
/* 1362 */     if (this.commissionsNextTime == -9223372036854775808L) {
/* 1363 */       calculateNextCommissionTime(this.currentTime);
/*      */     }
/*      */ 
/* 1366 */     if (this.commissionsNextTime < this.currentTime) {
/* 1367 */       calculateTurnoverAndCommission();
/* 1368 */       calculateNextCommissionTime(this.currentTime);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void onTick(Instrument instrument, ITick tick) {
/* 1373 */     this.lastTicks[instrument.ordinal()] = tick;
/* 1374 */     addCurrentTime(tick.getTime());
/*      */   }
/*      */ 
/*      */   public synchronized ITick[] getLastTicks() {
/* 1378 */     return this.lastTicks;
/*      */   }
/*      */ 
/*      */   public synchronized void updateLastTicks(ITick[] lastTicks, Instrument currentInstrument) {
/* 1382 */     this.lastTicks = lastTicks;
/* 1383 */     addCurrentTime(lastTicks[currentInstrument.ordinal()].getTime());
/*      */   }
/*      */ 
/*      */   public void calculateTurnoverAndCommission() {
/* 1387 */     double amountInUSDTotal = 0.0D;
/* 1388 */     double[] amountInUSD = new double[INSTRUMENT_VALUES.length];
/* 1389 */     for (int i = 0; i < this.tradedAmountsInSecCCY.length; ++i) {
/* 1390 */       Instrument commissionInstrument = INSTRUMENT_VALUES[i];
/* 1391 */       if (this.activeInstruments[commissionInstrument.ordinal()] == 0) {
/*      */         continue;
/*      */       }
/* 1394 */       double tradedAmountInSecCCY = this.tradedAmountsInSecCCY[i];
/* 1395 */       if (tradedAmountInSecCCY > 0.0D) {
/* 1396 */         amountInUSD[commissionInstrument.ordinal()] = this.currencyConverter.convert(tradedAmountInSecCCY, commissionInstrument.getSecondaryCurrency(), USD, null);
/*      */ 
/* 1398 */         amountInUSDTotal += amountInUSD[commissionInstrument.ordinal()];
/*      */       }
/* 1400 */       this.tradedAmountsInSecCCY[i] = 0.0D;
/*      */     }
/* 1402 */     this.turnoverLastMonth.addLast(Double.valueOf(amountInUSDTotal));
/* 1403 */     while (this.turnoverLastMonth.size() > 30) {
/* 1404 */       this.turnoverLastMonth.removeFirst();
/*      */     }
/*      */ 
/* 1407 */     double turnoverLastMonthTotal = 0.0D;
/* 1408 */     for (Double turnover : this.turnoverLastMonth) {
/* 1409 */       turnoverLastMonthTotal += turnover.doubleValue();
/*      */     }
/*      */ 
/* 1412 */     double commissionTotal = 0.0D;
/* 1413 */     for (int i = 0; i < INSTRUMENT_VALUES.length; ++i) {
/* 1414 */       Instrument commissionInstrument = INSTRUMENT_VALUES[i];
/* 1415 */       if (this.activeInstruments[commissionInstrument.ordinal()] == 0) {
/*      */         continue;
/*      */       }
/* 1418 */       double amountInUSDthisInstrument = amountInUSD[commissionInstrument.ordinal()];
/* 1419 */       double commissionThisInstrument = 0.0D;
/* 1420 */       if (amountInUSDthisInstrument > 0.0D) {
/* 1421 */         commissionThisInstrument = StratUtils.roundHalfUp(amountInUSDthisInstrument * this.currentCommission / 1000000.0D, 2);
/* 1422 */         commissionTotal += commissionThisInstrument;
/*      */       }
/* 1424 */       InstrumentReportData instrumentReportData = this.testerReportData.getInstrumentReportData(commissionInstrument);
/* 1425 */       if (instrumentReportData != null) {
/* 1426 */         instrumentReportData.turnover = StratUtils.roundHalfEven(instrumentReportData.turnover + amountInUSDthisInstrument, 2);
/* 1427 */         instrumentReportData.commission = StratUtils.roundHalfEven(instrumentReportData.commission + commissionThisInstrument, 2);
/*      */       }
/*      */     }
/* 1430 */     double commissionInAccountCurrency = this.currencyConverter.convert(commissionTotal, USD, this.account.getCurrency(), null);
/* 1431 */     this.account.setDeposit(StratUtils.roundHalfEven(this.account.getRealizedEquity() - commissionInAccountCurrency, 2));
/* 1432 */     this.account.setRealizedEquity(this.account.getDeposit());
/* 1433 */     this.account.setRealizedEquityWithCommissions(this.account.getDeposit());
/*      */ 
/* 1436 */     calculateCurrentCommission(turnoverLastMonthTotal);
/*      */ 
/* 1438 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/* 1439 */     event.type = TesterReportData.TesterEvent.EventType.COMMISSIONS;
/* 1440 */     event.time = this.commissionsNextTime;
/* 1441 */     event.amount = StratUtils.roundHalfEven(commissionTotal, 2);
/* 1442 */     this.testerReportData.addEvent(event);
/* 1443 */     this.testerReportData.addTurnover(amountInUSDTotal);
/* 1444 */     this.testerReportData.addCommission(commissionTotal);
/*      */ 
/* 1446 */     applyOvernights();
/*      */ 
/* 1448 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */     try {
/* 1450 */       recalculateAccountData();
/*      */     } finally {
/* 1452 */       this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ACCOUNT_INFO_CALCS);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void applyOvernights() {
/* 1457 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1458 */     cal.setTimeInMillis(this.commissionsNextTime);
/* 1459 */     int dayOfWeek = cal.get(7);
/*      */ 
/* 1461 */     Overnights overnights = this.account.getOvernights();
/* 1462 */     for (TesterOrder order : this.allOrders)
/* 1463 */       if (order.getState() == IOrder.State.FILLED) {
/* 1464 */         Double overnightCommissionDouble = (Double)((order.isLong()) ? overnights.getLongOvernights() : overnights.getShortOvernights()).get(order.getInstrument());
/* 1465 */         double overnightCommission = (overnightCommissionDouble == null) ? 0.0D : overnightCommissionDouble.doubleValue();
/* 1466 */         if (dayOfWeek == 4) {
/* 1467 */           overnightCommission *= 3.0D;
/*      */         }
/* 1469 */         TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/* 1470 */         event.type = TesterReportData.TesterEvent.EventType.OVERNIGHTS;
/* 1471 */         event.time = this.commissionsNextTime;
/* 1472 */         event.label = order.getLabel();
/* 1473 */         event.instrument = order.getInstrument();
/* 1474 */         event.amount = order.getAmountInUnits();
/* 1475 */         event.orderCommand = order.getOrderCommand();
/* 1476 */         event.openPrice = StratUtils.roundHalfEven(overnightCommission, 2);
/* 1477 */         this.testerReportData.addEvent(event);
/*      */ 
/* 1479 */         overnightCommission = StratUtils.roundHalfEven(order.getInstrument().getPipValue() * overnightCommission, 6);
/* 1480 */         order.overnights(overnightCommission);
/*      */ 
/* 1482 */         TesterMessage message = new TesterMessage("Overnight commissions applied", IMessage.Type.ORDER_CHANGED_OK, order, this.commissionsNextTime);
/* 1483 */         order.update(message);
/* 1484 */         fireOnMessage(message);
/*      */       }
/*      */   }
/*      */ 
/*      */   private void calculateCurrentCommission(double turnoverLastMonthTotal)
/*      */   {
/* 1490 */     Commissions commissions = this.account.getCommissions();
/* 1491 */     if (commissions == null) {
/* 1492 */       commissions = new Commissions(false);
/* 1493 */       this.account.setCommissions(commissions);
/*      */     }
/* 1495 */     double depositCommission = commissions.getMaxCommission();
/* 1496 */     double depositInUSD = this.currencyConverter.convert(this.account.getDeposit(), this.account.getCurrency(), USD, null);
/*      */ 
/* 1498 */     for (Map.Entry entry : commissions.getDepositLimits().entrySet()) {
/* 1499 */       if (depositInUSD < ((Double)entry.getKey()).doubleValue()) break;
/* 1500 */       depositCommission = ((Double)entry.getValue()).doubleValue();
/*      */     }
/*      */ 
/* 1506 */     double equityCommission = commissions.getMaxCommission();
/* 1507 */     double equityInUSD = this.currencyConverter.convert(this.account.getEquityActual(), this.account.getCurrency(), USD, null);
/*      */ 
/* 1509 */     for (Map.Entry entry : commissions.getEquityLimits().entrySet()) {
/* 1510 */       if (equityInUSD < ((Double)entry.getKey()).doubleValue()) break;
/* 1511 */       equityCommission = ((Double)entry.getValue()).doubleValue();
/*      */     }
/*      */ 
/* 1517 */     double amountCommission = commissions.getMaxCommission();
/* 1518 */     for (Map.Entry entry : commissions.getTurnoverLimits().entrySet()) {
/* 1519 */       if (turnoverLastMonthTotal < ((Double)entry.getKey()).doubleValue()) break;
/* 1520 */       amountCommission = ((Double)entry.getValue()).doubleValue();
/*      */     }
/*      */ 
/* 1526 */     this.currentCommission = Math.min(depositCommission, Math.min(equityCommission, amountCommission));
/*      */   }
/*      */ 
/*      */   public double getCurrentCommission() {
/* 1530 */     return this.currentCommission;
/*      */   }
/*      */ 
/*      */   private double calculateCommission(double amountInSecCCY, Currency secondaryCurrency) {
/* 1534 */     double commission = 0.0D;
/* 1535 */     if (amountInSecCCY > 0.0D) {
/* 1536 */       double amount = this.currencyConverter.convert(amountInSecCCY, secondaryCurrency, USD, null);
/*      */ 
/* 1538 */       commission = amount * this.currentCommission / 1000000.0D;
/*      */     }
/* 1540 */     return commission;
/*      */   }
/*      */ 
/*      */   private double calculateCommission() {
/* 1544 */     double commissionTotal = 0.0D;
/* 1545 */     for (int i = 0; i < this.tradedAmountsInSecCCY.length; ++i) {
/* 1546 */       Instrument comissionInstrument = INSTRUMENT_VALUES[i];
/* 1547 */       if (this.activeInstruments[comissionInstrument.ordinal()] == 0) {
/*      */         continue;
/*      */       }
/* 1550 */       double tradedAmount = this.tradedAmountsInSecCCY[i];
/* 1551 */       double commission = calculateCommission(tradedAmount, comissionInstrument.getSecondaryCurrency());
/* 1552 */       commissionTotal += StratUtils.roundHalfEven(commission, 2);
/*      */     }
/*      */ 
/* 1555 */     return this.currencyConverter.convert(commissionTotal, USD, this.account.getCurrency(), null);
/*      */   }
/*      */ 
/*      */   private void calculateNextCommissionTime(long tickTime) {
/* 1559 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1560 */     cal.setTimeInMillis(tickTime);
/* 1561 */     cal.set(14, 0);
/* 1562 */     cal.set(13, 0);
/* 1563 */     cal.set(12, 0);
/* 1564 */     cal.set(11, 21);
/* 1565 */     if (cal.getTimeInMillis() < tickTime) {
/* 1566 */       cal.add(6, 1);
/*      */     }
/* 1568 */     int dayOfWeek = cal.get(7);
/* 1569 */     while ((dayOfWeek == 7) || (dayOfWeek == 1)) {
/* 1570 */       cal.add(6, 1);
/* 1571 */       dayOfWeek = cal.get(7);
/*      */     }
/* 1573 */     this.commissionsNextTime = cal.getTimeInMillis();
/*      */   }
/*      */ 
/*      */   public synchronized void sendAccountInfo() {
/* 1577 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */     try {
/* 1579 */       recalculateAccountData();
/* 1580 */       this.account.update5SecDelayedValues();
/*      */     } finally {
/* 1582 */       this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ACCOUNT_INFO_CALCS);
/*      */     }
/* 1584 */     perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */     try {
/* 1586 */       this.strategy.onAccount(this.account);
/*      */     } catch (Throwable t) {
/* 1588 */       logClientThrowable(t, IStrategyExceptionHandler.Source.ON_ACCOUNT_INFO);
/*      */     } finally {
/* 1590 */       this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ON_ACCOUNT);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeAmount(TesterOrder order, double amount) throws JFException {
/* 1595 */     amount = StratUtils.roundHalfEven(amount * 1000000.0D, 2);
/* 1596 */     if ((order.getState() != IOrder.State.CREATED) && (order.getState() != IOrder.State.OPENED)) {
/* 1597 */       throw new JFException("Cannot change amount of filled, closed or canceled order");
/*      */     }
/* 1599 */     if ((order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID) || (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER)) {
/* 1600 */       throw new JFException("Cannot change amount of PLACE_BID or PLACE_OFFER");
/*      */     }
/* 1602 */     if (amount != 0.0D) {
/* 1603 */       if (amount < ((Double)this.minTradableAmounts.get(order.getInstrument())).doubleValue()) {
/* 1604 */         throw new JFException("Invalid parameter, amount is < " + Long.toString(()((Double)this.minTradableAmounts.get(order.getInstrument())).doubleValue()));
/*      */       }
/* 1606 */       if (order.getAmount() == amount)
/* 1607 */         this.notificationUtils.postWarningMessage("Attempt to change amount of the order [" + order.getLabel() + "] with the same value. Old amount [" + order.getAmount() + "], new amount [" + StratUtils.roundHalfEven(amount / 1000000.0D, 7) + "]", true);
/*      */       try
/*      */       {
/* 1610 */         Method method = super.getClass().getMethod("changeAmountImpl", new Class[] { TesterOrder.class, Double.TYPE });
/* 1611 */         Method addOrderChangedReportDataMethod = super.getClass().getDeclaredMethod("addOrderChangedReportData", new Class[] { TesterOrder.class });
/* 1612 */         this.ordersToProcess.add(new Object[] { method, { order, Double.valueOf(amount) }, addOrderChangedReportDataMethod, { order } });
/*      */       } catch (Exception e) {
/* 1614 */         LOGGER.error(e.getMessage(), e);
/* 1615 */         throw new JFException(e);
/*      */       }
/*      */     } else {
/* 1618 */       cancelOrder(order);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeAmountImpl(TesterOrder order, double amount) {
/* 1623 */     if (amount < ((Double)this.minTradableAmounts.get(order.getInstrument())).doubleValue()) {
/* 1624 */       TesterMessage message = new TesterMessage("Invalid parameter, amount is < " + Long.toString(()((Double)this.minTradableAmounts.get(order.getInstrument())).doubleValue()), IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime);
/* 1625 */       order.update(message);
/* 1626 */       fireOnMessage(message);
/* 1627 */       return;
/*      */     }
/* 1629 */     if ((order.getState() != IOrder.State.CREATED) && (order.getState() != IOrder.State.OPENED)) {
/* 1630 */       TesterMessage message = new TesterMessage("Cannot change amount of filled, closed or canceled order", IMessage.Type.ORDER_SUBMIT_REJECTED, order, this.currentTime);
/* 1631 */       order.update(message);
/* 1632 */       fireOnMessage(message);
/* 1633 */       return;
/*      */     }
/* 1635 */     if ((order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID) || (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER)) {
/* 1636 */       TesterMessage message = new TesterMessage("Cannot change amount of PLACE_BID or PLACE_OFFER", IMessage.Type.ORDER_CHANGED_REJECTED, order, this.currentTime);
/* 1637 */       order.update(message);
/* 1638 */       fireOnMessage(message);
/* 1639 */       return;
/*      */     }
/* 1641 */     order.setRequestedAmountSubmitted(amount);
/* 1642 */     TesterMessage message = new TesterMessage("Order updated", IMessage.Type.ORDER_CHANGED_OK, order, this.currentTime);
/* 1643 */     order.update(message);
/* 1644 */     fireOnMessage(message);
/* 1645 */     if (this.testerOrdersProvider != null)
/* 1646 */       this.testerOrdersProvider.orderChangedOkPending(order);
/*      */   }
/*      */ 
/*      */   public void changeOpenPrice(TesterOrder order, double price) throws JFException
/*      */   {
/* 1651 */     if ((order.getState() != IOrder.State.CREATED) && (order.getState() != IOrder.State.OPENED)) {
/* 1652 */       throw new JFException("Order not in CREATED or OPENED state");
/*      */     }
/* 1654 */     if (StratUtils.roundHalfEven(order.getOpenPrice(), order.getInstrument().getPipScale() + 2) == StratUtils.roundHalfEven(price, order.getInstrument().getPipScale() + 2))
/* 1655 */       this.notificationUtils.postWarningMessage("Attempt to change open price of the order [" + order.getLabel() + "] with the same price. Old price [" + order.getOpenPrice() + "], new price [" + price + "]", true);
/*      */     try
/*      */     {
/* 1658 */       Method method = super.getClass().getMethod("changeOpenPriceImpl", new Class[] { TesterOrder.class, Double.TYPE });
/* 1659 */       Method addOrderChangedReportDataMethod = super.getClass().getDeclaredMethod("addOrderChangedReportData", new Class[] { TesterOrder.class });
/* 1660 */       this.ordersToProcess.add(new Object[] { method, { order, Double.valueOf(price) }, addOrderChangedReportDataMethod, { order } });
/*      */     } catch (Exception e) {
/* 1662 */       LOGGER.error(e.getMessage(), e);
/* 1663 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeOpenPriceImpl(TesterOrder order, double price) {
/* 1668 */     if ((order.getState() != IOrder.State.CREATED) && (order.getState() != IOrder.State.OPENED)) {
/* 1669 */       TesterMessage message = new TesterMessage("Cannot change price of filled, closed or canceled order", IMessage.Type.ORDER_CHANGED_REJECTED, order, this.currentTime);
/* 1670 */       order.update(message);
/* 1671 */       fireOnMessage(message);
/* 1672 */       return;
/*      */     }
/* 1674 */     order.setOpenPriceSubmitted(price);
/* 1675 */     TesterMessage message = new TesterMessage("Order updated", IMessage.Type.ORDER_CHANGED_OK, order, this.currentTime);
/* 1676 */     order.update(message);
/* 1677 */     fireOnMessage(message);
/* 1678 */     if ((order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID) || (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER)) {
/* 1679 */       this.recalculate = true;
/*      */     }
/* 1681 */     if (this.testerOrdersProvider != null)
/* 1682 */       this.testerOrdersProvider.orderChangedOkPending(order);
/*      */   }
/*      */ 
/*      */   public void setStopLoss(TesterOrder order, double price, OfferSide side, double trailingStep) throws JFException
/*      */   {
/* 1687 */     if ((trailingStep != 0.0D) && (trailingStep < 10.0D)) {
/* 1688 */       throw new JFException("Trailing step can't be less than 10");
/*      */     }
/* 1690 */     if ((order.getState() != IOrder.State.FILLED) && (order.getState() != IOrder.State.OPENED)) {
/* 1691 */       throw new JFException("Cannot add stop loss on order in state other than FILLED or OPENED");
/*      */     }
/* 1693 */     price = StratUtils.roundHalfEven(price, order.getInstrument().getPipScale() + 2);
/* 1694 */     if (StratUtils.roundHalfEven(order.getStopLossPrice(), order.getInstrument().getPipScale() + 2) == price)
/* 1695 */       this.notificationUtils.postWarningMessage("Attempt to change stop loss of the order [" + order.getLabel() + "] with the same price. Old price [" + order.getStopLossPrice() + "], new price [" + price + "]", true);
/*      */     try
/*      */     {
/* 1698 */       Method method = super.getClass().getMethod("setStopLossImpl", new Class[] { IOrder.class, Double.TYPE, OfferSide.class, Double.TYPE });
/* 1699 */       this.ordersToProcess.add(new Object[] { method, { order, Double.valueOf(price), side, Double.valueOf(trailingStep) } });
/*      */     } catch (Exception e) {
/* 1701 */       LOGGER.error(e.getMessage(), e);
/* 1702 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setStopLossImpl(IOrder order, double price, OfferSide side, double trailingStep) {
/* 1707 */     if ((order.getState() != IOrder.State.FILLED) && (order.getState() != IOrder.State.OPENED)) {
/* 1708 */       TesterMessage message = new TesterMessage("Cannot add stop loss on order in state other than FILLED or OPENED", IMessage.Type.ORDER_CHANGED_REJECTED, order, this.currentTime);
/* 1709 */       ((TesterOrder)order).update(message);
/* 1710 */       fireOnMessage(message);
/* 1711 */       return;
/*      */     }
/* 1713 */     ((TesterOrder)order).setStopLossSubmitted(price, side, trailingStep, (side == OfferSide.BID) ? this.lastTicks[order.getInstrument().ordinal()].getBid() : this.lastTicks[order.getInstrument().ordinal()].getAsk());
/* 1714 */     TesterMessage message = new TesterMessage("Stop loss condition changed", IMessage.Type.ORDER_CHANGED_OK, order, this.currentTime);
/* 1715 */     ((TesterOrder)order).update(message);
/* 1716 */     fireOnMessage(message);
/* 1717 */     if (this.testerOrdersProvider != null)
/* 1718 */       this.testerOrdersProvider.orderChangedOkSLTP(this.currentTime, (TesterOrder)order);
/*      */   }
/*      */ 
/*      */   public void setTakeProfit(TesterOrder order, double price) throws JFException
/*      */   {
/* 1723 */     if ((order.getState() != IOrder.State.FILLED) && (order.getState() != IOrder.State.OPENED)) {
/* 1724 */       throw new JFException("Cannot add take profit on order in state [" + order.getState() + "] other than FILLED or OPENED");
/*      */     }
/* 1726 */     price = StratUtils.roundHalfEven(price, order.getInstrument().getPipScale() + 2);
/* 1727 */     if (StratUtils.roundHalfEven(order.getTakeProfitPrice(), order.getInstrument().getPipScale() + 2) == price)
/* 1728 */       this.notificationUtils.postWarningMessage("Attempt to change take profit of the order [" + order.getLabel() + "] with the same price. Old price [" + order.getTakeProfitPrice() + "], new price [" + price + "]", true);
/*      */     try
/*      */     {
/* 1731 */       Method method = super.getClass().getMethod("setTakeProfitImpl", new Class[] { TesterOrder.class, Double.TYPE });
/* 1732 */       this.ordersToProcess.add(new Object[] { method, { order, Double.valueOf(price) } });
/*      */     } catch (Exception e) {
/* 1734 */       LOGGER.error(e.getMessage(), e);
/* 1735 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTakeProfitImpl(TesterOrder order, double price) {
/* 1740 */     if ((order.getState() != IOrder.State.FILLED) && (order.getState() != IOrder.State.OPENED)) {
/* 1741 */       TesterMessage message = new TesterMessage("Cannot add take profit on order in state other than FILLED or OPENED", IMessage.Type.ORDER_CHANGED_REJECTED, order, this.currentTime);
/* 1742 */       order.update(message);
/* 1743 */       fireOnMessage(message);
/* 1744 */       return;
/*      */     }
/* 1746 */     order.setTakeProfitSubmitted(price);
/* 1747 */     TesterMessage message = new TesterMessage("Take profit condition changed", IMessage.Type.ORDER_CHANGED_OK, order, this.currentTime);
/* 1748 */     order.update(message);
/* 1749 */     fireOnMessage(message);
/* 1750 */     if (this.testerOrdersProvider != null)
/* 1751 */       this.testerOrdersProvider.orderChangedOkSLTP(this.currentTime, order);
/*      */   }
/*      */ 
/*      */   public void setGoodTillTime(TesterOrder order, long goodTillTime) throws JFException
/*      */   {
/* 1756 */     if ((order.getOrderCommand() != IEngine.OrderCommand.PLACE_BID) && (order.getOrderCommand() != IEngine.OrderCommand.PLACE_OFFER)) {
/* 1757 */       throw new JFException("Order should be \"place bid\" or \"place offer\"");
/*      */     }
/* 1759 */     if (goodTillTime < 0L) {
/* 1760 */       throw new JFException(JFException.Error.INVALID_GTT);
/*      */     }
/* 1762 */     if ((goodTillTime > 0L) && (goodTillTime < 63072000000L)) {
/* 1763 */       throw new JFException(JFException.Error.INVALID_GTT);
/*      */     }
/* 1765 */     if (order.getGoodTillTime() == goodTillTime) {
/* 1766 */       this.notificationUtils.postWarningMessage("Attempt to change goodTillTime of the order [" + order.getLabel() + "] with the same value. Old value [" + order.getGoodTillTime() + "], new value [" + goodTillTime + "]", true);
/*      */     }
/*      */     try
/*      */     {
/* 1770 */       Method method = super.getClass().getMethod("setGoodTillTimeImpl", new Class[] { TesterOrder.class, Long.TYPE });
/* 1771 */       Method addOrderChangedReportDataMethod = super.getClass().getDeclaredMethod("addOrderChangedReportData", new Class[] { TesterOrder.class });
/* 1772 */       this.ordersToProcess.add(new Object[] { method, { order, Long.valueOf(goodTillTime) }, addOrderChangedReportDataMethod, { order } });
/*      */     } catch (Exception e) {
/* 1774 */       LOGGER.error(e.getMessage(), e);
/* 1775 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setGoodTillTimeImpl(TesterOrder order, long goodTillTime) {
/* 1780 */     if ((order.getOrderCommand() != IEngine.OrderCommand.PLACE_BID) && (order.getOrderCommand() != IEngine.OrderCommand.PLACE_OFFER)) {
/* 1781 */       TesterMessage message = new TesterMessage("Order should be \"place bid\" or \"place offer\"", IMessage.Type.ORDER_CHANGED_REJECTED, order, this.currentTime);
/* 1782 */       order.update(message);
/* 1783 */       fireOnMessage(message);
/* 1784 */       return;
/*      */     }
/* 1786 */     if ((goodTillTime != 0L) && (goodTillTime < getCurrentTime())) {
/* 1787 */       TesterMessage message = new TesterMessage("GoodTillTime should be in the future, not in the past", IMessage.Type.ORDER_CHANGED_REJECTED, order, this.currentTime);
/* 1788 */       order.update(message);
/* 1789 */       fireOnMessage(message);
/* 1790 */       return;
/*      */     }
/* 1792 */     order.setGoodTillTimeSubmitted(goodTillTime);
/* 1793 */     TesterMessage message = new TesterMessage("Order updated", IMessage.Type.ORDER_CHANGED_OK, order, this.currentTime);
/* 1794 */     order.update(message);
/* 1795 */     fireOnMessage(message);
/*      */   }
/*      */ 
/*      */   protected void addOrderChangedReportData(TesterOrder order) {
/* 1799 */     InstrumentReportData instrumentReportData = this.testerReportData.getInstrumentReportData(order.getInstrument());
/* 1800 */     if (instrumentReportData == null) {
/* 1801 */       instrumentReportData = new InstrumentReportData();
/* 1802 */       this.testerReportData.setInstrumentReportData(order.getInstrument(), instrumentReportData);
/*      */     }
/* 1804 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/* 1805 */     event.type = TesterReportData.TesterEvent.EventType.ORDER_CHANGED;
/* 1806 */     event.time = this.currentTime;
/* 1807 */     event.label = order.getLabel();
/* 1808 */     event.instrument = order.getInstrument();
/* 1809 */     event.amount = order.getAmountInUnits();
/* 1810 */     event.orderCommand = order.getOrderCommand();
/* 1811 */     event.openPrice = order.getClientPrice();
/* 1812 */     if (order.getLabel().startsWith(MC_MARGIN_CUT))
/* 1813 */       event.openTrigger = TesterReportData.TesterEvent.OpenTrigger.OPEN_BY_MC;
/*      */     else {
/* 1815 */       event.openTrigger = TesterReportData.TesterEvent.OpenTrigger.OPEN_BY_STRATEGY;
/*      */     }
/* 1817 */     this.testerReportData.addEvent(event);
/*      */   }
/*      */ 
/*      */   private void recalculateAccountData()
/*      */   {
/* 1825 */     double sumOfOpenPositions = 0.0D;
/* 1826 */     double profLossOfOpenPositions = 0.0D;
/* 1827 */     for (int i = 0; i < this.ordersByInstrument.length; ++i) {
/* 1828 */       double secondaryExposure = 0.0D;
/* 1829 */       double sumOfAmounts = 0.0D;
/* 1830 */       double sumOfSecondaryAmounts = 0.0D;
/* 1831 */       double placeBidsInSecondaryCCY = 0.0D;
/* 1832 */       double placeOffersInSecondaryCCY = 0.0D;
/* 1833 */       List orders = this.ordersByInstrument[i];
/* 1834 */       if (this.activeInstruments[i] == 0) continue; if (orders.isEmpty()) {
/*      */         continue;
/*      */       }
/* 1837 */       Instrument instrument = INSTRUMENT_VALUES[i];
/* 1838 */       for (TesterOrder order : orders)
/* 1839 */         if (order.getState() == IOrder.State.FILLED) {
/* 1840 */           double secondaryAmount = -order.getAmountInUnits() * order.getOpenPrice();
/* 1841 */           if (order.getOrderCommand().isLong()) {
/* 1842 */             secondaryExposure += secondaryAmount;
/* 1843 */             sumOfAmounts += order.getAmountInUnits();
/* 1844 */             sumOfSecondaryAmounts -= order.getOpenPrice() * order.getAmountInUnits();
/*      */           } else {
/* 1846 */             secondaryExposure -= secondaryAmount;
/* 1847 */             sumOfAmounts -= order.getAmountInUnits();
/* 1848 */             sumOfSecondaryAmounts += order.getOpenPrice() * order.getAmountInUnits();
/*      */           }
/* 1850 */         } else if (order.getState() == IOrder.State.OPENED) {
/* 1851 */           double secondaryAmount = -order.getAmountInUnits() * order.getOpenPrice();
/* 1852 */           if (order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID)
/* 1853 */             placeBidsInSecondaryCCY += secondaryAmount;
/* 1854 */           else if (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER)
/* 1855 */             placeOffersInSecondaryCCY -= secondaryAmount;
/*      */         }
/*      */       double profitLossSecondary;
/*      */       double profitLossSecondary;
/* 1861 */       if ((sumOfAmounts > 0.001D) || (sumOfAmounts < 0.001D))
/* 1862 */         profitLossSecondary = sumOfSecondaryAmounts + sumOfAmounts * ((sumOfAmounts > 0.0D) ? this.lastTicks[instrument.ordinal()].getBid() : this.lastTicks[instrument.ordinal()].getAsk());
/*      */       else {
/* 1864 */         profitLossSecondary = sumOfSecondaryAmounts;
/*      */       }
/*      */ 
/* 1868 */       placeBidsInSecondaryCCY += secondaryExposure;
/* 1869 */       placeOffersInSecondaryCCY += secondaryExposure;
/* 1870 */       if (((placeBidsInSecondaryCCY <= 0.0D) ? -placeBidsInSecondaryCCY : placeBidsInSecondaryCCY) > ((placeOffersInSecondaryCCY <= 0.0D) ? -placeOffersInSecondaryCCY : placeOffersInSecondaryCCY))
/*      */       {
/* 1872 */         secondaryExposure = placeBidsInSecondaryCCY;
/*      */       }
/*      */       else secondaryExposure = placeOffersInSecondaryCCY;
/*      */ 
/* 1878 */       OfferSide side = OfferSide.ASK;
/* 1879 */       if (secondaryExposure < 0.0D) {
/* 1880 */         secondaryExposure = -secondaryExposure;
/*      */ 
/* 1882 */         side = OfferSide.BID;
/*      */       }
/*      */ 
/* 1886 */       double convertedAmount = this.currencyConverter.convert(secondaryExposure, instrument.getSecondaryCurrency(), this.account.getCurrency(), side);
/*      */ 
/* 1888 */       sumOfOpenPositions += StratUtils.roundHalfEven(convertedAmount, 2);
/*      */ 
/* 1890 */       double convertedProfLoss = this.currencyConverter.convert(profitLossSecondary, instrument.getSecondaryCurrency(), this.account.getCurrency(), null);
/* 1891 */       profLossOfOpenPositions += StratUtils.roundHalfEven(convertedProfLoss, 2);
/*      */     }
/*      */ 
/* 1894 */     double commission = calculateCommission();
/* 1895 */     double realizedEquityWithCommission = StratUtils.roundHalfEven(this.account.getRealizedEquity() - commission, 2);
/* 1896 */     double equity = StratUtils.roundHalfEven(realizedEquityWithCommission + profLossOfOpenPositions, 2);
/* 1897 */     double leverage = (equity == 0.0D) ? (1.0D / 0.0D) : StratUtils.roundHalfEven(sumOfOpenPositions / equity, 2);
/* 1898 */     double creditLine = StratUtils.roundHalfEven(this.account.getMaxLeverage() * equity - sumOfOpenPositions, 2);
/* 1899 */     if (creditLine < 0.0D) {
/* 1900 */       creditLine = 0.0D;
/*      */     }
/*      */ 
/* 1903 */     this.account.setEquity(equity);
/* 1904 */     this.account.setRealizedEquityWithCommissions(realizedEquityWithCommission);
/* 1905 */     this.account.setUseOfLeverage(leverage);
/* 1906 */     this.account.setCreditLine(creditLine);
/* 1907 */     this.account.setProfLossOfOpenPositions(profLossOfOpenPositions);
/*      */ 
/* 1909 */     if (this.testerOrdersProvider != null)
/* 1910 */       this.testerOrdersProvider.setCalculatedAccountData(equity, leverage, creditLine);
/*      */   }
/*      */ 
/*      */   private double getUsedMargineByInstrumentWithOrder(Instrument instrument, TesterOrder newOrder, double amount, boolean opening, double openPrice)
/*      */   {
/* 1917 */     double secondaryExposure = 0.0D;
/* 1918 */     List orders = new ArrayList(this.ordersByInstrument[instrument.ordinal()]);
/* 1919 */     for (TesterOrder order : orders) {
/* 1920 */       if (order.getState() == IOrder.State.FILLED) {
/* 1921 */         double orderOpenPrice = (order.getState() == IOrder.State.CREATED) ? openPrice : order.getOpenPrice();
/* 1922 */         double secondaryAmount = -order.getAmountInUnits() * orderOpenPrice;
/* 1923 */         if (order.getOrderCommand().isLong())
/* 1924 */           secondaryExposure += secondaryAmount;
/*      */         else {
/* 1926 */           secondaryExposure -= secondaryAmount;
/*      */         }
/*      */       }
/*      */     }
/* 1930 */     if ((newOrder != null) && (newOrder.getOrderCommand() != IEngine.OrderCommand.PLACE_BID) && (newOrder.getOrderCommand() != IEngine.OrderCommand.PLACE_OFFER)) {
/* 1931 */       double secondaryAmount = -amount * openPrice;
/* 1932 */       if (((opening) && (newOrder.getOrderCommand().isLong())) || ((!(opening)) && (!(newOrder.getOrderCommand().isLong()))))
/* 1933 */         secondaryExposure += secondaryAmount;
/*      */       else {
/* 1935 */         secondaryExposure -= secondaryAmount;
/*      */       }
/*      */     }
/*      */ 
/* 1939 */     double placeBidsInSecondaryCCY = 0.0D;
/* 1940 */     double placeOffersInSecondaryCCY = 0.0D;
/* 1941 */     for (TesterOrder order : orders) {
/* 1942 */       if (order.getState() == IOrder.State.OPENED) {
/* 1943 */         double orderOpenPrice = (order.getState() == IOrder.State.CREATED) ? openPrice : order.getOpenPrice();
/* 1944 */         double secondaryAmount = -order.getAmountInUnits() * orderOpenPrice;
/* 1945 */         if (order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID)
/* 1946 */           placeBidsInSecondaryCCY += secondaryAmount;
/* 1947 */         else if (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER) {
/* 1948 */           placeOffersInSecondaryCCY -= secondaryAmount;
/*      */         }
/*      */       }
/*      */     }
/* 1952 */     if ((newOrder != null) && (opening) && (((newOrder.getOrderCommand() == IEngine.OrderCommand.PLACE_BID) || (newOrder.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER)))) {
/* 1953 */       double secondaryAmount = -amount * openPrice;
/* 1954 */       if (newOrder.getOrderCommand() == IEngine.OrderCommand.PLACE_BID)
/* 1955 */         placeBidsInSecondaryCCY += secondaryAmount;
/* 1956 */       else if (newOrder.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER) {
/* 1957 */         placeOffersInSecondaryCCY -= secondaryAmount;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1962 */     placeBidsInSecondaryCCY += secondaryExposure;
/* 1963 */     placeOffersInSecondaryCCY += secondaryExposure;
/* 1964 */     if (((placeBidsInSecondaryCCY <= 0.0D) ? -placeBidsInSecondaryCCY : placeBidsInSecondaryCCY) > ((placeOffersInSecondaryCCY <= 0.0D) ? -placeOffersInSecondaryCCY : placeOffersInSecondaryCCY))
/*      */     {
/* 1966 */       secondaryExposure = placeBidsInSecondaryCCY;
/*      */     }
/*      */     else secondaryExposure = placeOffersInSecondaryCCY;
/*      */ 
/* 1970 */     OfferSide side = OfferSide.BID;
/* 1971 */     if (secondaryExposure < 0.0D)
/*      */     {
/* 1973 */       side = OfferSide.ASK;
/*      */     }
/*      */ 
/* 1977 */     return StratUtils.roundHalfEven(this.currencyConverter.convert(secondaryExposure, instrument.getSecondaryCurrency(), this.account.getCurrency(), side), 2);
/*      */   }
/*      */ 
/*      */   private double getExposureByInstrument(Instrument instrument)
/*      */   {
/* 1982 */     double exposure = 0.0D;
/* 1983 */     List orders = this.ordersByInstrument[instrument.ordinal()];
/* 1984 */     for (TesterOrder order : orders) {
/* 1985 */       if (order.getState() == IOrder.State.FILLED) {
/* 1986 */         if (order.getOrderCommand().isLong())
/* 1987 */           exposure += order.getAmountInUnits();
/*      */         else {
/* 1989 */           exposure -= order.getAmountInUnits();
/*      */         }
/*      */       }
/*      */     }
/* 1993 */     return StratUtils.roundHalfEven(exposure, 2);
/*      */   }
/*      */ 
/*      */   private Map<Instrument, Double> getTotalFilledAmountsByInstrument() {
/* 1997 */     Map exposures = new LinkedHashMap();
/* 1998 */     for (int i = 0; i < this.ordersByInstrument.length; ++i) {
/* 1999 */       double exposure = 0.0D;
/* 2000 */       Instrument instrument = INSTRUMENT_VALUES[i];
/* 2001 */       List orders = this.ordersByInstrument[i];
/* 2002 */       for (TesterOrder order : orders) {
/* 2003 */         if (order.getState() == IOrder.State.FILLED) {
/* 2004 */           if (order.getOrderCommand().isLong())
/* 2005 */             exposure += order.getAmountInUnits();
/*      */           else {
/* 2007 */             exposure -= order.getAmountInUnits();
/*      */           }
/*      */         }
/*      */       }
/* 2011 */       if (StratUtils.roundHalfEven(exposure, 0) != 0.0D) {
/* 2012 */         exposures.put(instrument, Double.valueOf(StratUtils.roundHalfEven(exposure, 0)));
/*      */       }
/*      */     }
/* 2015 */     return exposures;
/*      */   }
/*      */ 
/*      */   public synchronized void doDelayedTasks() {
/* 2019 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */     try {
/* 2021 */       recalculateAccountData();
/*      */     } finally {
/* 2023 */       this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ACCOUNT_INFO_CALCS);
/*      */     }
/* 2025 */     this.recalculate = false;
/* 2026 */     processUserCommands();
/* 2027 */     if (!(this.allOrders.isEmpty())) {
/* 2028 */       perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */       try {
/* 2030 */         if (isWeekends()) {
/* 2031 */           cancelTimedoutPBPOOrders();
/*      */           return;
/*      */         }
/* 2034 */         TesterOrder[] orders = (TesterOrder[])this.allOrders.toArray(new TesterOrder[this.allOrders.size()]);
/* 2035 */         for (IOrder order : orders)
/*      */           try {
/* 2037 */             executeConditionally((TesterOrder)order);
/*      */           } catch (Exception e) {
/* 2039 */             LOGGER.error(e.getMessage(), e);
/* 2040 */             TesterMessage message = new TesterMessage("Unexpected execution error", IMessage.Type.NOTIFICATION, order, this.currentTime);
/* 2041 */             ((TesterOrder)order).update(message);
/* 2042 */             fireOnMessage(message);
/*      */           }
/*      */       }
/*      */       finally {
/* 2046 */         this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.STOP_ORDERS);
/*      */       }
/* 2048 */     } else if (isWeekends()) {
/* 2049 */       return;
/*      */     }
/* 2051 */     if (!(this.errors.isEmpty())) {
/* 2052 */       TesterMessage[] errorsArr = (TesterMessage[])this.errors.toArray(new TesterMessage[this.errors.size()]);
/* 2053 */       this.errors.clear();
/* 2054 */       for (TesterMessage error : errorsArr) {
/* 2055 */         if (error.getOrder() != null) {
/* 2056 */           ((TesterOrder)error.getOrder()).update(error);
/*      */         }
/* 2058 */         fireOnMessage(error);
/*      */       }
/*      */     }
/* 2061 */     if (this.recalculate) {
/* 2062 */       perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */       try {
/* 2064 */         recalculateAccountData();
/*      */       } finally {
/* 2066 */         this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ACCOUNT_INFO_CALCS);
/*      */       }
/*      */     }
/*      */     try {
/* 2070 */       perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */       boolean marginCall;
/*      */       try {
/* 2073 */         marginCall = tryMarginCall();
/*      */       } finally {
/* 2075 */         this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.MC_CHECK);
/*      */       }
/* 2077 */       if (marginCall) {
/* 2078 */         perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */         try {
/* 2080 */           recalculateAccountData();
/*      */         } finally {
/* 2082 */           this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ACCOUNT_INFO_CALCS);
/*      */         }
/*      */       }
/*      */     } catch (JFException e) {
/* 2086 */       LOGGER.error(e.getMessage(), e);
/* 2087 */       TesterMessage message = new TesterMessage("Unexpected error while recalculation of account data", IMessage.Type.NOTIFICATION, null, this.currentTime);
/* 2088 */       fireOnMessage(message);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void processUserCommands() {
/* 2093 */     if (this.ordersToProcess.isEmpty()) {
/* 2094 */       return;
/*      */     }
/* 2096 */     long totalPerfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */     try {
/* 2098 */       Object[][] ordersToProcessArr = (Object[][])this.ordersToProcess.toArray(new Object[this.ordersToProcess.size()][]);
/* 2099 */       this.ordersToProcess.clear();
/* 2100 */       for (Object[] methodAndParams : ordersToProcessArr) {
/* 2101 */         Method method = (Method)methodAndParams[0];
/* 2102 */         Object[] params = (Object[])(Object[])methodAndParams[1];
/* 2103 */         boolean success = true;
/*      */         try {
/* 2105 */           Object retValue = method.invoke(this, params);
/* 2106 */           if (retValue != null)
/* 2107 */             success = ((Boolean)retValue).booleanValue();
/*      */         }
/*      */         catch (Exception e) {
/* 2110 */           LOGGER.error("Cannot execute method", e);
/* 2111 */           IOrder order = null;
/* 2112 */           if (params[0] instanceof IOrder) {
/* 2113 */             order = (IOrder)params[0];
/*      */           }
/* 2115 */           TesterMessage message = new TesterMessage("Unexpected merge/takeprofit/stoploss/close/cancel execution error", IMessage.Type.NOTIFICATION, order, this.currentTime);
/* 2116 */           ((TesterOrder)order).update(message);
/* 2117 */           fireOnMessage(message);
/*      */         }
/* 2119 */         if ((success) && (methodAndParams.length > 2)) {
/* 2120 */           Method reportMethod = (Method)methodAndParams[2];
/* 2121 */           Object[] reportParams = (Object[])(Object[])methodAndParams[3];
/*      */           try {
/* 2123 */             reportMethod.invoke(this, reportParams);
/*      */           } catch (Exception e) {
/* 2125 */             LOGGER.error("Cannot execute method [" + reportMethod.getName() + "]", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 2130 */       this.strategyRunner.perfStopTime(totalPerfStatTimeStart, ITesterReport.PerfStats.ORDER_CHANGES);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean tryMarginCall() throws JFException {
/* 2135 */     if ((this.account.getMaxLeverage() <= this.account.getUseOfLeverageActual()) || (this.account.getEquityActual() < this.account.getMCEquityLimit()))
/*      */     {
/* 2137 */       return doMarginCall();
/*      */     }
/* 2139 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean doMarginCall() throws JFException {
/* 2143 */     List bidsOffers = getOpenedPlaceBidsOffers();
/* 2144 */     if (bidsOffers.size() > 0) {
/* 2145 */       List toSort = new ArrayList(bidsOffers);
/* 2146 */       Collections.sort(toSort, new Comparator() {
/*      */         public int compare(IOrder o1, IOrder o2) {
/* 2148 */           long timestamp1 = o1.getCreationTime();
/* 2149 */           long timestamp2 = o2.getCreationTime();
/* 2150 */           return ((timestamp1 == timestamp2) ? 0 : (timestamp1 > timestamp2) ? 1 : -1); }  } );
/* 2153 */       Iterator i$ = toSort.iterator();
/*      */       TesterOrder orderToClose;
/*      */       double usableDiff;
/*      */       do { if (!(i$.hasNext())) break label267; IOrder aToSort = (IOrder)i$.next();
/* 2154 */         orderToClose = (TesterOrder)aToSort;
/*      */ 
/* 2156 */         double exposureByInstrument = getExposureByInstrument(orderToClose.getInstrument());
/*      */ 
/* 2158 */         if (exposureByInstrument != exposureByInstrument) break;
/* 2159 */         usableDiff = 0.0D;
/* 2160 */         if (exposureByInstrument != 0.0D) {
/* 2161 */           if ((orderToClose.getOrderCommand().isLong()) && (exposureByInstrument < 0.0D))
/* 2162 */             usableDiff = (exposureByInstrument <= 0.0D) ? -exposureByInstrument : exposureByInstrument;
/* 2163 */           else if ((orderToClose.getOrderCommand().isShort()) && (exposureByInstrument > 0.0D))
/* 2164 */             usableDiff = (exposureByInstrument <= 0.0D) ? -exposureByInstrument : exposureByInstrument;
/*      */         }
/*      */       }
/* 2167 */       while (usableDiff >= orderToClose.getAmountInUnits());
/*      */ 
/* 2172 */       String prefix = (orderToClose.getOrderCommand().isLong()) ? "BID" : "OFFER";
/* 2173 */       String message = "Strategy tester: MARGIN CALL: cancelling order " + prefix;
/* 2174 */       this.notificationUtils.postWarningMessage(message, true);
/* 2175 */       cancelOrderImpl(orderToClose);
/* 2176 */       addMCReportData(message, true);
/* 2177 */       addOrderCanceledReportData(orderToClose, TesterReportData.TesterEvent.CloseTrigger.CANCEL_BY_MC);
/* 2178 */       return true;
/*      */     }
/*      */ 
/* 2183 */     label267: double useOfLeverage = StratUtils.roundHalfEven(this.account.getUseOfLeverageActual() / 100.0D, 2);
/* 2184 */     double removingPart = 0.0D;
/* 2185 */     if (useOfLeverage > 0.0D) {
/* 2186 */       double useOfLeverageMin1 = useOfLeverage - 1.0D;
/* 2187 */       removingPart = StratUtils.roundHalfEven(((useOfLeverageMin1 <= 0.0D) ? -useOfLeverageMin1 : useOfLeverageMin1) / useOfLeverage, 2);
/*      */     }
/*      */ 
/* 2190 */     double marginCallLeverage = this.account.getMarginCutLevel();
/* 2191 */     if (isWeekendsMarginCut()) {
/* 2192 */       marginCallLeverage = this.account.getMarginWeekendCallLeverage();
/*      */     }
/* 2194 */     boolean isClosingExp = (useOfLeverage * 100.0D >= marginCallLeverage) && (removingPart > 0.0D);
/*      */ 
/* 2196 */     List ordersList = getOrders();
/* 2197 */     if ((ordersList.size() > 0) && (this.account.getEquityActual() < this.account.getMCEquityLimit()))
/*      */     {
/* 2199 */       String message = "Strategy tester: Minimal equity limit " + this.account.getMCEquityLimit() + " reached, merging and closing all groups!";
/* 2200 */       this.notificationUtils.postWarningMessage(message, true);
/* 2201 */       addMCReportData(message, true);
/* 2202 */       List filledOrders = new ArrayList();
/* 2203 */       for (List orders : this.ordersByInstrument) {
/* 2204 */         filledOrders.clear();
/* 2205 */         for (TesterOrder testerOrder : orders) {
/* 2206 */           if (testerOrder.getState() == IOrder.State.FILLED) {
/* 2207 */             testerOrder.setStopLossSubmitted(0.0D, null, 0.0D, 0.0D);
/* 2208 */             testerOrder.setTakeProfitSubmitted(0.0D);
/* 2209 */             filledOrders.add(testerOrder);
/*      */           }
/*      */         }
/* 2212 */         if (filledOrders.size() > 1) {
/* 2213 */           mergeOrdersImpl(null, (IOrder[])filledOrders.toArray(new IOrder[filledOrders.size()]), TesterReportData.TesterEvent.CloseTrigger.MERGE_BY_MC);
/*      */         }
/*      */       }
/* 2216 */       for (IOrder order : getOrders()) {
/* 2217 */         if (order.getState() == IOrder.State.FILLED) {
/* 2218 */           closeOrderImpl((TesterOrder)order, -1.0D, ((TesterOrder)order).getAmountInUnits(), true, TesterReportData.TesterEvent.CloseTrigger.CLOSE_BY_MC);
/*      */         }
/*      */       }
/* 2221 */       return true; }
/* 2222 */     if ((ordersList.size() > 0) && (isClosingExp))
/*      */     {
/* 2224 */       Map totalMargin = getTotalFilledAmountsByInstrument();
/* 2225 */       if (totalMargin.size() > 0) {
/* 2226 */         for (Map.Entry entry : totalMargin.entrySet()) {
/* 2227 */           Instrument instrument = (Instrument)entry.getKey();
/* 2228 */           double exposure = ((Double)entry.getValue()).doubleValue();
/* 2229 */           if (((exposure <= 0.0D) ? -exposure : exposure) > 0.0D) {
/* 2230 */             IEngine.OrderCommand orderCommand = (exposure > 0.0D) ? IEngine.OrderCommand.SELL : IEngine.OrderCommand.BUY;
/* 2231 */             double marginCutAmount = StratUtils.roundHalfEven(StratUtils.roundHalfEven(exposure * removingPart / 1000.0D, 2) * 1000.0D, 2);
/*      */ 
/* 2234 */             if (Math.abs(marginCutAmount) < ((Double)this.minTradableAmounts.get(entry.getKey())).doubleValue()) {
/* 2235 */               for (IOrder order : getOrders()) {
/* 2236 */                 if ((entry.getKey() == order.getInstrument()) && (order.getState() == IOrder.State.FILLED))
/*      */                 {
/* 2239 */                   closeOrderImpl((TesterOrder)order, -1.0D, ((TesterOrder)order).getAmountInUnits(), true, TesterReportData.TesterEvent.CloseTrigger.CLOSE_BY_MARGINCUT);
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 2248 */               return true;
/*      */             }
/*      */ 
/* 2251 */             marginCutAmount = (marginCutAmount <= 0.0D) ? -marginCutAmount : marginCutAmount;
/* 2252 */             String message = "Strategy tester: MARGIN CUT: reducing exposure by " + marginCutAmount;
/* 2253 */             this.notificationUtils.postWarningMessage(message, true);
/* 2254 */             addMCReportData(message, false);
/* 2255 */             IOrder cuttingOrder = submitOrderMC(MC_MARGIN_CUT + "_" + this.margineCutLabelCounter.incrementAndGet(), instrument, orderCommand, StratUtils.roundHalfEven(marginCutAmount / 1000000.0D, 6));
/*      */ 
/* 2257 */             processUserCommands();
/*      */             try {
/* 2259 */               executeConditionally((TesterOrder)cuttingOrder);
/*      */             } catch (Exception e) {
/* 2261 */               LOGGER.error(e.getMessage(), e);
/* 2262 */               TesterMessage testerMessage = new TesterMessage("Unexpected execution error", IMessage.Type.NOTIFICATION, cuttingOrder, this.currentTime);
/* 2263 */               ((TesterOrder)cuttingOrder).update(testerMessage);
/* 2264 */               fireOnMessage(testerMessage);
/*      */             }
/* 2266 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2271 */     return false;
/*      */   }
/*      */ 
/*      */   private void addMCReportData(String text, boolean isMarginCut) {
/* 2275 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/* 2276 */     if (isMarginCut)
/* 2277 */       event.type = TesterReportData.TesterEvent.EventType.MARGIN_CUT;
/*      */     else {
/* 2279 */       event.type = TesterReportData.TesterEvent.EventType.MARGIN_CALL;
/*      */     }
/* 2281 */     event.time = this.currentTime;
/* 2282 */     event.text = text;
/*      */   }
/*      */ 
/*      */   private void logClientThrowable(Throwable t, IStrategyExceptionHandler.Source source) {
/* 2286 */     LOGGER.error(t.getMessage(), t);
/* 2287 */     StringWriter out = new StringWriter();
/* 2288 */     PrintWriter pw = new PrintWriter(out);
/* 2289 */     t.printStackTrace(pw);
/* 2290 */     String error = StrategyWrapper.representError(this.strategy, t);
/* 2291 */     this.notificationUtils.postErrorMessage("Strategy tester: " + error, t, true);
/* 2292 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/* 2293 */     event.type = TesterReportData.TesterEvent.EventType.EXCEPTION;
/* 2294 */     event.time = this.currentTime;
/* 2295 */     event.text = error;
/* 2296 */     this.testerReportData.addEvent(event);
/* 2297 */     this.exceptionHandler.onException(1L, source, t);
/*      */   }
/*      */ 
/*      */   public synchronized long getCurrentTime() {
/* 2301 */     return this.currentTime;
/*      */   }
/*      */ 
/*      */   public String getAccount() {
/* 2305 */     return "tester_account";
/*      */   }
/*      */ 
/*      */   public void broadcast(String topic, String message)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized IOrder getOrderById(String orderId)
/*      */   {
/* 2315 */     if (orderId == null) {
/* 2316 */       throw new NullPointerException("OrderId is null");
/*      */     }
/* 2318 */     for (TesterOrder order : this.allOrders) {
/* 2319 */       if (order.getId().equals(orderId)) {
/* 2320 */         return order;
/*      */       }
/*      */     }
/* 2323 */     return null;
/*      */   }
/*      */ 
/*      */   public void waitForStateChange(TesterOrder order, long timeout, TimeUnit unit) throws InterruptedException {
/*      */     try {
/* 2328 */       AccessController.doPrivileged(new PrivilegedExceptionAction(order, timeout, unit) {
/*      */         public Object run() throws Exception {
/* 2330 */           if ((this.val$order.getState() != IOrder.State.CLOSED) && (this.val$order.getState() != IOrder.State.CANCELED)) {
/* 2331 */             TesterCustodian.this.strategyRunner.runUntilChange(this.val$order, this.val$timeout, this.val$unit);
/*      */           }
/* 2333 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException e) {
/* 2337 */       LOGGER.error(e.getMessage(), e);
/* 2338 */       if (e.getCause() == null)
/* 2339 */         logClientThrowable(e, null);
/*      */       else
/* 2341 */         logClientThrowable(e.getCause(), null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void waitForStateChange(TesterOrder order, long timeout, TimeUnit unit, IOrder.State[] expectedStates) throws InterruptedException
/*      */   {
/*      */     try {
/* 2348 */       AccessController.doPrivileged(new PrivilegedExceptionAction(order, timeout, unit, expectedStates) {
/*      */         public Object run() throws Exception {
/* 2350 */           if ((this.val$order.getState() != IOrder.State.CLOSED) && (this.val$order.getState() != IOrder.State.CANCELED)) {
/* 2351 */             TesterCustodian.this.strategyRunner.runUntilChange(this.val$order, this.val$timeout, this.val$unit, this.val$expectedStates);
/*      */           }
/* 2353 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException e) {
/* 2357 */       LOGGER.error(e.getMessage(), e);
/* 2358 */       if (e.getCause() == null)
/* 2359 */         logClientThrowable(e, null);
/*      */       else
/* 2361 */         logClientThrowable(e.getCause(), null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isStrategyThread()
/*      */   {
/* 2367 */     return this.strategyRunner.isStrategyThread();
/*      */   }
/*      */ 
/*      */   ITick getLastTick(Instrument instrument) {
/* 2371 */     return this.lastTicks[instrument.ordinal()];
/*      */   }
/*      */ 
/*      */   AbstractCurrencyConverter getCurrencyConverter() {
/* 2375 */     return this.currencyConverter;
/*      */   }
/*      */ 
/*      */   TesterAccount getTesterAccount() {
/* 2379 */     return this.account;
/*      */   }
/*      */ 
/*      */   private void fireOnMessage(TesterMessage message) {
/* 2383 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/* 2384 */     StrategyEventsCallback strategyEventsCallback = this.strategyRunner.getStrategyEventsCallback();
/*      */     try {
/* 2386 */       this.strategy.onMessage(message);
/* 2387 */       if (strategyEventsCallback != null)
/* 2388 */         strategyEventsCallback.onMessage(message);
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 2392 */       logClientThrowable(t, IStrategyExceptionHandler.Source.ON_MESSAGE);
/*      */     } finally {
/* 2394 */       this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ON_MESSAGE);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String groupToOCO(IOrder order1, IOrder order2)
/*      */     throws JFException
/*      */   {
/* 2419 */     return null;
/*      */   }
/*      */ 
/*      */   public String ungroupOCO(IOrder order)
/*      */     throws JFException
/*      */   {
/* 2425 */     return null;
/*      */   }
/*      */ 
/*      */   public IEngine.StrategyMode getStrategyMode()
/*      */   {
/* 2430 */     return this.mode;
/*      */   }
/*      */ 
/*      */   public void setStrategyMode(IEngine.StrategyMode mode)
/*      */   {
/* 2435 */     this.mode = mode;
/*      */   }
/*      */ 
/*      */   public void createSignal(IOrder order, ISignal.Type type)
/*      */   {
/* 2440 */     if (this.signalsProcessor != null)
/* 2441 */       this.signalsProcessor.add(new SignalImpl(order, type));
/*      */   }
/*      */ 
/*      */   public ISignalsProcessor getSignalsProcessor()
/*      */   {
/* 2446 */     return this.signalsProcessor;
/*      */   }
/*      */ 
/*      */   public TesterOrdersProvider getTesterOrdersProvider() {
/* 2450 */     return this.testerOrdersProvider;
/*      */   }
/*      */ 
/*      */   public IStrategyRunner getStrategyRunner() {
/* 2454 */     return this.strategyRunner;
/*      */   }
/*      */ 
/*      */   public void setSignalsProcessor(ISignalsProcessor signalsProcessor) {
/* 2458 */     this.signalsProcessor = signalsProcessor;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   56 */     MC_MARGIN_CUT = "MC_MARGIN_CUT";
/*      */ 
/*   58 */     LOGGER = LoggerFactory.getLogger(TesterCustodian.class);
/*   59 */     INSTRUMENT_VALUES = Instrument.values();
/*      */ 
/*   61 */     USD = Instrument.EURUSD.getSecondaryCurrency();
/*      */   }
/*      */ 
/*      */   private class TesterCurrencyConverter extends AbstractCurrencyConverter
/*      */   {
/*      */     protected double getLastMarketPrice(Instrument instrument, OfferSide side)
/*      */     {
/* 2401 */       ITick tick = TesterCustodian.this.lastTicks[instrument.ordinal()];
/* 2402 */       if (tick == null) {
/* 2403 */         throw new IllegalStateException("No market state found for currency pair: " + instrument);
/*      */       }
/* 2405 */       if (side == OfferSide.BID)
/* 2406 */         return tick.getBid();
/* 2407 */       if (side == OfferSide.ASK) {
/* 2408 */         return tick.getAsk();
/*      */       }
/* 2410 */       return StratUtils.roundHalfEven((tick.getBid() + tick.getAsk()) / 2.0D, 7);
/*      */     }
/*      */   }
/*      */ }

/* Location:           D:\javaworksvn\JForexClientLibrary_branch\libs\greed-common-177.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterCustodian
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */