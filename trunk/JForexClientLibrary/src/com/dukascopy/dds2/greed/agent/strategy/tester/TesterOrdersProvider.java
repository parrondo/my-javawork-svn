/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.OpenData;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalDataMutable;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalDataMutable.CloseData;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalDataMutable.OpenData;
/*     */ import com.dukascopy.charts.data.datacache.OrdersListener;
/*     */ import com.dukascopy.charts.data.orders.AbstractOrdersProvider;
/*     */ import com.dukascopy.charts.data.orders.CalculatedAccount;
/*     */ import com.dukascopy.charts.data.orders.IOrdersProvider;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.dds2.greed.util.IOrderUtils;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.awt.Color;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TesterOrdersProvider extends AbstractOrdersProvider
/*     */   implements IOrdersProvider
/*     */ {
/*     */   private static final Logger LOGGER;
/*     */ 
/*     */   public synchronized Collection<OrderHistoricalData> getOpenOrdersForInstrument(Instrument instrument, long from, long to)
/*     */   {
/*  42 */     Map orders = this.ordersByInstrument[instrument.ordinal()];
/*  43 */     if (orders == null) {
/*  44 */       orders = new HashMap();
/*  45 */       this.ordersByInstrument[instrument.ordinal()] = orders;
/*     */     }
/*  47 */     Collection result = new ArrayList();
/*  48 */     for (OrderHistoricalData data : orders.values()) {
/*  49 */       if (((data.getHistoryEnd() >= from) && (data.getHistoryStart() <= to)) || (data.getHistoryStart() == 9223372036854775807L) || ((data.isOpened()) && ((data.getEntryOrder().getStopLossPrice().compareTo(BigDecimal.ZERO) >= 0) || (data.getEntryOrder().getTakeProfitPrice().compareTo(BigDecimal.ZERO) >= 0))))
/*     */       {
/*  52 */         result.add(data);
/*     */       }
/*     */     }
/*  55 */     return result;
/*     */   }
/*     */ 
/*     */   public IOrderUtils getOrderUtils()
/*     */   {
/*  60 */     return new IOrderUtils() {
/*     */       public void addStopLoss(String orderGroupId, String orderId) {
/*     */       }
/*     */ 
/*     */       public void addTakeProfit(String orderGroupId, String orderId) {
/*     */       }
/*     */ 
/*     */       public boolean cancelOrder(String orderId) {
/*  68 */         return false;
/*     */       }
/*     */ 
/*     */       public void closeOrder(String orderGroupId) {
/*     */       }
/*     */ 
/*     */       public void condCloseOrder(String orderGroupId) {
/*     */       }
/*     */ 
/*     */       public void editOrder(String orderId, ActionListener cancelActionListener, ChartBean chartBean) {
/*     */       }
/*     */ 
/*     */       public void editOrder(String orderId, double price, ActionListener cancelActionListener, ChartBean chartBean) {
/*     */       }
/*     */ 
/*     */       public void createNewOrder(Window window, Instrument instrument, IEngine.OrderCommand command, double price, Integer chartId) {
/*     */       }
/*     */ 
/*     */       public void selectGroupIds(List<String> selectedGroupIds) {
/*     */       }
/*     */ 
/*     */       public AccountInfoMessage getAccountInfo() {
/*  90 */         return null;
/*     */       }
/*     */ 
/*     */       public void orderChangePreview(ChartBean chartBean, String orderId, BigDecimal newOrderPrice, String text, Color color, Stroke stroke)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void cancelOrderChangePreview(ChartBean chartBean, String orderId)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void setOrderLinesVisible(ChartBean chartBean, String orderId, boolean visible) {
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public synchronized void orderSubmitOk(TesterOrder order) {
/*     */     try {
/* 109 */       OrderHistoricalDataMutable lineData = getGraphicalData(order);
/*     */ 
/* 111 */       OrderHistoricalDataMutable.OpenData pendingOrder = new OrderHistoricalDataMutable.OpenData();
/* 112 */       pendingOrder.setAmount(BigDecimal.valueOf(order.getRequestedAmountInUnits()));
/* 113 */       pendingOrder.setSide(order.getOrderCommand());
/* 114 */       pendingOrder.setOpenPrice(BigDecimal.valueOf(order.getOpenPrice()));
/* 115 */       pendingOrder.setCreationTime(order.getCreationTime());
/*     */ 
/* 117 */       extendOrderHistoryRange(lineData, pendingOrder.getCreationTime());
/* 118 */       pendingOrder.setOrderId(order.getId());
/* 119 */       pendingOrder.setLabel(order.getLabel());
/* 120 */       if (order.getStopLossPrice() != 0.0D) {
/* 121 */         pendingOrder.setStopLossOrderId(order.getId());
/* 122 */         pendingOrder.setStopLossPrice(BigDecimal.valueOf(order.getStopLossPrice()));
/* 123 */         pendingOrder.setStopLossByBid(order.getStopLossSide() == OfferSide.BID);
/*     */       }
/* 125 */       if (order.getTakeProfitPrice() != 0.0D) {
/* 126 */         pendingOrder.setTakeProfitOrderId(order.getId());
/* 127 */         pendingOrder.setTakeProfitPrice(BigDecimal.valueOf(order.getTakeProfitPrice()));
/*     */       }
/* 129 */       lineData.addPendingOrder(pendingOrder);
/* 130 */       fireOrderNew(order.getInstrument(), saveGraphicalData(order.getInstrument(), lineData));
/*     */     } catch (Exception e) {
/* 132 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void orderChangedOkSLTP(long currentTime, TesterOrder order) {
/*     */     try {
/* 138 */       OrderHistoricalDataMutable lineData = getGraphicalData(order);
/*     */       OrderHistoricalDataMutable.OpenData openingOrder;
/*     */       OrderHistoricalDataMutable.OpenData openingOrder;
/* 141 */       if (!lineData.isOpened()) {
/* 142 */         openingOrder = (OrderHistoricalDataMutable.OpenData)lineData.getPendingOrders().get(0);
/*     */       } else {
/* 144 */         openingOrder = lineData.getEntryOrder();
/*     */ 
/* 146 */         extendOrderHistoryRange(lineData, currentTime);
/*     */       }
/*     */ 
/* 149 */       if (order.getStopLossPrice() != 0.0D) {
/* 150 */         openingOrder.setStopLossOrderId(order.getId());
/* 151 */         openingOrder.setStopLossPrice(BigDecimal.valueOf(order.getStopLossPrice()));
/* 152 */         openingOrder.setStopLossByBid(order.getStopLossSide() == OfferSide.BID);
/*     */       } else {
/* 154 */         openingOrder.setStopLossOrderId(null);
/* 155 */         openingOrder.setStopLossPrice(OrderHistoricalData.NEG_ONE);
/*     */       }
/*     */ 
/* 158 */       if (order.getTakeProfitPrice() != 0.0D) {
/* 159 */         openingOrder.setTakeProfitOrderId(order.getId());
/* 160 */         openingOrder.setTakeProfitPrice(BigDecimal.valueOf(order.getTakeProfitPrice()));
/*     */       } else {
/* 162 */         openingOrder.setTakeProfitOrderId(null);
/* 163 */         openingOrder.setTakeProfitPrice(OrderHistoricalData.NEG_ONE);
/*     */       }
/* 165 */       fireOrderChange(order.getInstrument(), saveGraphicalData(order.getInstrument(), lineData));
/*     */     } catch (Exception e) {
/* 167 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void orderChangedOkPending(TesterOrder order) {
/*     */     try {
/* 173 */       OrderHistoricalDataMutable lineData = getGraphicalData(order);
/* 174 */       OrderHistoricalDataMutable.OpenData pendingOrder = (OrderHistoricalDataMutable.OpenData)lineData.getPendingOrders().get(0);
/* 175 */       pendingOrder.setAmount(BigDecimal.valueOf(order.getRequestedAmountInUnits()));
/* 176 */       pendingOrder.setOpenPrice(BigDecimal.valueOf(order.getOpenPrice()));
/* 177 */       fireOrderChange(order.getInstrument(), saveGraphicalData(order.getInstrument(), lineData));
/*     */     } catch (Exception e) {
/* 179 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void orderFillOk(TesterOrder order) {
/*     */     try {
/* 185 */       OrderHistoricalDataMutable lineData = getGraphicalData(order);
/*     */ 
/* 187 */       OrderHistoricalDataMutable.OpenData entryOrder = new OrderHistoricalDataMutable.OpenData();
/* 188 */       entryOrder.setAmount(BigDecimal.valueOf(order.getAmountInUnits()));
/* 189 */       entryOrder.setOpenPrice(BigDecimal.valueOf(order.getOpenPrice()));
/* 190 */       entryOrder.setSide(order.getOrderCommand());
/* 191 */       entryOrder.setCreationTime(order.getCreationTime());
/* 192 */       entryOrder.setFillTime(order.getFillTime());
/*     */ 
/* 194 */       extendOrderHistoryRange(lineData, entryOrder.getCreationTime());
/* 195 */       extendOrderHistoryRange(lineData, entryOrder.getFillTime());
/* 196 */       entryOrder.setOrderId(order.getId());
/* 197 */       entryOrder.setLabel(order.getLabel());
/* 198 */       if (order.getStopLossPrice() != 0.0D) {
/* 199 */         entryOrder.setStopLossOrderId(order.getId());
/* 200 */         entryOrder.setStopLossPrice(BigDecimal.valueOf(order.getStopLossPrice()));
/* 201 */         entryOrder.setStopLossByBid(order.getStopLossSide() == OfferSide.BID);
/*     */       }
/* 203 */       if (order.getTakeProfitPrice() != 0.0D) {
/* 204 */         entryOrder.setTakeProfitOrderId(order.getId());
/* 205 */         entryOrder.setTakeProfitPrice(BigDecimal.valueOf(order.getTakeProfitPrice()));
/*     */       }
/* 207 */       lineData.setEntryOrder(entryOrder);
/* 208 */       lineData.clearPendingOrders();
/* 209 */       lineData.setOpened(true);
/* 210 */       fireOrderChange(order.getInstrument(), saveGraphicalData(order.getInstrument(), lineData));
/*     */     } catch (Exception e) {
/* 212 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void orderCancelOk(TesterOrder order) {
/*     */     try {
/* 218 */       Map orders = this.ordersByInstrument[order.getInstrument().ordinal()];
/* 219 */       if (orders == null) {
/* 220 */         orders = new HashMap();
/* 221 */         this.ordersByInstrument[order.getInstrument().ordinal()] = orders;
/*     */       }
/* 223 */       OrderHistoricalDataMutable lineData = new OrderHistoricalDataMutable((OrderHistoricalData)orders.remove(order.getId()));
/* 224 */       lineData.setClosed(true);
/* 225 */       fireOrderChange(order.getInstrument(), new OrderHistoricalData(lineData));
/*     */     } catch (Exception e) {
/* 227 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void orderFillRejected(TesterOrder order) {
/*     */     try {
/* 233 */       Map orders = this.ordersByInstrument[order.getInstrument().ordinal()];
/* 234 */       if (orders == null) {
/* 235 */         orders = new HashMap();
/* 236 */         this.ordersByInstrument[order.getInstrument().ordinal()] = orders;
/*     */       }
/* 238 */       OrderHistoricalDataMutable lineData = new OrderHistoricalDataMutable((OrderHistoricalData)orders.remove(order.getId()));
/* 239 */       lineData.setClosed(true);
/* 240 */       fireOrderChange(order.getInstrument(), new OrderHistoricalData(lineData));
/*     */     } catch (Exception e) {
/* 242 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void orderCloseOk(TesterOrder order, double closeAmount) {
/*     */     try {
/* 248 */       OrderHistoricalDataMutable lineData = getGraphicalData(order);
/*     */ 
/* 250 */       if (order.getState() == IOrder.State.CLOSED) {
/* 251 */         lineData.setClosed(true);
/*     */       }
/*     */ 
/* 254 */       OrderHistoricalDataMutable.CloseData closeOrder = new OrderHistoricalDataMutable.CloseData();
/* 255 */       closeOrder.setAmount(BigDecimal.valueOf(closeAmount));
/* 256 */       closeOrder.setClosePrice(BigDecimal.valueOf(order.getClosePrice()));
/* 257 */       closeOrder.setCloseTime(order.getCloseTime());
/*     */ 
/* 259 */       extendOrderHistoryRange(lineData, closeOrder.getCloseTime());
/* 260 */       lineData.putCloseData(order.getId() + "_" + lineData.getCloseDataMap().size(), closeOrder);
/* 261 */       fireOrderChange(order.getInstrument(), saveGraphicalData(order.getInstrument(), lineData));
/*     */     } catch (Exception e) {
/* 263 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void ordersMergeOk(IOrder[] orders, TesterOrder resultingOrder, long mergeTime) {
/*     */     try {
/* 269 */       Instrument instrument = orders[0].getInstrument();
/* 270 */       Map lineOrders = this.ordersByInstrument[instrument.ordinal()];
/* 271 */       OrderHistoricalDataMutable resultingLineData = new OrderHistoricalDataMutable();
/*     */       String id;
/*     */       String id;
/* 273 */       if (resultingOrder == null)
/* 274 */         id = orders[0].getId();
/*     */       else {
/* 276 */         id = resultingOrder.getId();
/*     */       }
/* 278 */       resultingLineData.setOrderGroupId(id);
/*     */ 
/* 280 */       OrderHistoricalDataMutable.CloseData closeData = new OrderHistoricalDataMutable.CloseData();
/* 281 */       OrderHistoricalDataMutable.OpenData entryOrder = new OrderHistoricalDataMutable.OpenData();
/* 282 */       resultingLineData.setEntryOrder(entryOrder);
/* 283 */       resultingLineData.setOpened(true);
/* 284 */       entryOrder.setOrderId(id);
/* 285 */       entryOrder.setLabel(resultingOrder.getLabel());
/* 286 */       entryOrder.setAmount(BigDecimal.ZERO);
/* 287 */       entryOrder.setSide(IEngine.OrderCommand.BUY);
/* 288 */       if ((resultingOrder != null) && (resultingOrder.getState() == IOrder.State.FILLED)) {
/* 289 */         entryOrder.setAmount(BigDecimal.valueOf(resultingOrder.getAmountInUnits()));
/* 290 */         entryOrder.setSide(resultingOrder.getOrderCommand());
/* 291 */         entryOrder.setOpenPrice(BigDecimal.valueOf(resultingOrder.getOpenPrice()));
/* 292 */         entryOrder.setCreationTime(resultingOrder.getCreationTime());
/* 293 */         entryOrder.setFillTime(resultingOrder.getFillTime());
/*     */ 
/* 295 */         extendOrderHistoryRange(resultingLineData, entryOrder.getCreationTime());
/* 296 */         extendOrderHistoryRange(resultingLineData, entryOrder.getFillTime());
/* 297 */         entryOrder.setOrderId(resultingOrder.getId());
/* 298 */         resultingLineData.setClosed(false);
/*     */       } else {
/* 300 */         resultingLineData.setClosed(true);
/* 301 */         closeData.setAmount(BigDecimal.ZERO);
/* 302 */         resultingLineData.putCloseData(id + "_0", closeData);
/*     */       }
/*     */ 
/* 305 */       List mergedOrders = new ArrayList(orders.length);
/* 306 */       String[] mergedFrom = new String[orders.length];
/* 307 */       BigDecimal closePrice = BigDecimal.ZERO;
/* 308 */       BigDecimal amount = BigDecimal.ZERO;
/* 309 */       for (int i = 0; i < orders.length; i++) {
/* 310 */         IOrder order = orders[i];
/* 311 */         mergedFrom[i] = order.getId();
/* 312 */         OrderHistoricalData mergedOrderUnmodifiable = (OrderHistoricalData)lineOrders.get(order.getId());
/* 313 */         if (mergedOrderUnmodifiable != null) {
/* 314 */           OrderHistoricalDataMutable mergedOrder = new OrderHistoricalDataMutable(mergedOrderUnmodifiable);
/* 315 */           mergedOrder.setMergedToGroupId(id);
/* 316 */           mergedOrder.setMergedToTime(mergeTime);
/* 317 */           extendOrderHistoryRange(mergedOrder, mergeTime);
/* 318 */           OrderHistoricalDataMutable.OpenData mergedOrderEntryOrder = mergedOrder.getEntryOrder();
/* 319 */           if (mergedOrderEntryOrder != null) {
/* 320 */             extendOrderHistoryRange(resultingLineData, mergedOrderEntryOrder.getFillTime());
/* 321 */             closePrice = closePrice.add(mergedOrderEntryOrder.getAmount().multiply(mergedOrderEntryOrder.getOpenPrice()));
/* 322 */             amount = amount.add(mergedOrderEntryOrder.getAmount());
/*     */           }
/* 324 */           mergedOrderUnmodifiable = new OrderHistoricalData(mergedOrder);
/* 325 */           mergedOrders.add(mergedOrderUnmodifiable);
/* 326 */           lineOrders.put(order.getId(), mergedOrderUnmodifiable);
/*     */         }
/*     */       }
/* 329 */       entryOrder.setMergedFrom(mergedFrom);
/*     */ 
/* 331 */       if (entryOrder.getCreationTime() == 0L) {
/* 332 */         entryOrder.setCreationTime(mergeTime);
/*     */ 
/* 334 */         extendOrderHistoryRange(resultingLineData, entryOrder.getCreationTime());
/*     */       }
/* 336 */       entryOrder.setFillTime(mergeTime);
/*     */ 
/* 338 */       extendOrderHistoryRange(resultingLineData, entryOrder.getFillTime());
/*     */ 
/* 340 */       if ((resultingOrder == null) || (resultingOrder.getState() != IOrder.State.FILLED)) {
/* 341 */         closeData.setCloseTime(mergeTime);
/*     */ 
/* 343 */         extendOrderHistoryRange(resultingLineData, closeData.getCloseTime());
/* 344 */         closeData.setClosePrice(closePrice.divide(amount, BigDecimal.valueOf(instrument.getPipValue()).scale() + 1, 6));
/* 345 */         entryOrder.setOpenPrice(closeData.getClosePrice());
/*     */       }
/* 347 */       OrderHistoricalData unmodifiableResult = new OrderHistoricalData(resultingLineData);
/* 348 */       lineOrders.put(resultingLineData.getOrderGroupId(), unmodifiableResult);
/*     */ 
/* 350 */       fireOrderMerge(instrument, unmodifiableResult, mergedOrders);
/*     */     } catch (Exception e) {
/* 352 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private OrderHistoricalDataMutable getGraphicalData(TesterOrder order) {
/* 357 */     assert (order.getInstrument() != null) : "Instrument cannot be null";
/* 358 */     Map orders = this.ordersByInstrument[order.getInstrument().ordinal()];
/* 359 */     if (orders == null) {
/* 360 */       orders = new HashMap();
/* 361 */       this.ordersByInstrument[order.getInstrument().ordinal()] = orders;
/*     */     }
/*     */ 
/* 364 */     String orderId = order.getId();
/* 365 */     OrderHistoricalData orderHistoricalData = (OrderHistoricalData)orders.get(orderId);
/* 366 */     if (orderHistoricalData == null)
/*     */     {
/* 368 */       OrderHistoricalDataMutable lineData = new OrderHistoricalDataMutable();
/* 369 */       lineData.setOrderGroupId(orderId);
/* 370 */       return lineData;
/*     */     }
/* 372 */     return new OrderHistoricalDataMutable(orderHistoricalData);
/*     */   }
/*     */ 
/*     */   private OrderHistoricalData saveGraphicalData(Instrument instrument, OrderHistoricalDataMutable lineData)
/*     */   {
/* 377 */     Map orders = this.ordersByInstrument[instrument.ordinal()];
/* 378 */     if (orders == null) {
/* 379 */       orders = new HashMap();
/* 380 */       this.ordersByInstrument[instrument.ordinal()] = orders;
/*     */     }
/* 382 */     OrderHistoricalData data = new OrderHistoricalData(lineData);
/* 383 */     orders.put(lineData.getOrderGroupId(), data);
/* 384 */     return data;
/*     */   }
/*     */ 
/*     */   protected ITick getLastTick(Instrument instrument)
/*     */   {
/* 389 */     return null;
/*     */   }
/*     */ 
/*     */   public double convert(double amount, Currency sourceCurrency, Currency targetCurrency, OfferSide side)
/*     */   {
/* 394 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency, OfferSide side)
/*     */   {
/* 399 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized void setCalculatedAccountData(double equity, double leverage, double creditLine) {
/* 403 */     this.calculatedAccount.setCalculatedEquity(equity);
/* 404 */     this.calculatedAccount.setCalculatedUseOfLeverage(leverage);
/* 405 */     this.calculatedAccount.setCalculatedCreditLine(creditLine);
/*     */   }
/*     */ 
/*     */   public synchronized double recalculateEquity()
/*     */   {
/* 410 */     return this.calculatedAccount.getEquity();
/*     */   }
/*     */ 
/*     */   public synchronized void getOrdersForInstrument(Instrument instrument, long from, long to, OrdersListener ordersListener, LoadingProgressListener progressListener) {
/* 414 */     Map orders = this.ordersByInstrument[instrument.ordinal()];
/* 415 */     if (orders == null) {
/* 416 */       orders = new HashMap();
/* 417 */       this.ordersByInstrument[instrument.ordinal()] = orders;
/*     */     }
/*     */ 
/* 420 */     for (OrderHistoricalData data : orders.values()) {
/* 421 */       if (((data.getHistoryEnd() >= from) && (data.getHistoryStart() <= to)) || (data.getHistoryStart() == 9223372036854775807L) || ((!data.isClosed()) && (data.isOpened()) && ((data.getEntryOrder().getStopLossPrice().compareTo(BigDecimal.ZERO) >= 0) || (data.getEntryOrder().getTakeProfitPrice().compareTo(BigDecimal.ZERO) >= 0))))
/*     */       {
/* 424 */         ordersListener.newOrder(instrument, data);
/*     */       }
/*     */     }
/* 427 */     progressListener.loadingFinished(true, from, to, to, null);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  36 */     LOGGER = LoggerFactory.getLogger(TesterOrdersProvider.class);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterOrdersProvider
 * JD-Core Version:    0.6.0
 */