/*      */ package com.dukascopy.charts.data.orders;
/*      */ 
/*      */ import com.dukascopy.api.IAccount.AccountState;
/*      */ import com.dukascopy.api.IEngine.OrderCommand;
/*      */ import com.dukascopy.api.ITick;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.OrderGroupListener;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.OpenData;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalDataMutable;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalDataMutable.CloseData;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalDataMutable.OpenData;
/*      */ import com.dukascopy.charts.data.datacache.TickData;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*      */ import com.dukascopy.dds2.greed.util.EnumConverter;
/*      */ import com.dukascopy.dds2.greed.util.IOrderUtils;
/*      */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderState;
/*      */ import com.dukascopy.transport.common.model.type.Position;
/*      */ import com.dukascopy.transport.common.model.type.PositionSide;
/*      */ import com.dukascopy.transport.common.model.type.StopDirection;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderSyncMessage;
/*      */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.RoundingMode;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Comparator;
/*      */ import java.util.Currency;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.SortedSet;
/*      */ import java.util.TreeSet;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class OrdersProvider extends AbstractOrdersProvider
/*      */   implements IOrdersProvider, OrderGroupListener
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   private static final Instrument[] INSTRUMENT_VALUES;
/*      */   private static OrdersProvider ordersProvider;
/*   55 */   private AbstractCurrencyConverter currencyConverter = new AbstractCurrencyConverter()
/*      */   {
/*      */     protected double getLastMarketPrice(Instrument instrument, OfferSide side) {
/*   58 */       TickData lastTick = FeedDataProvider.getDefaultInstance().getLastTick(instrument);
/*   59 */       if (lastTick == null) {
/*   60 */         return (0.0D / 0.0D);
/*      */       }
/*   62 */       if (side == OfferSide.ASK)
/*   63 */         return lastTick.getAsk();
/*   64 */       if (side == OfferSide.BID) {
/*   65 */         return lastTick.getBid();
/*      */       }
/*   67 */       return StratUtils.roundHalfEven((lastTick.getBid() + lastTick.getAsk()) / 2.0D, 7);
/*      */     }
/*   55 */   };
/*      */ 
/*      */   public static OrdersProvider getInstance()
/*      */   {
/*   73 */     if (ordersProvider == null) {
/*   74 */       throw new IllegalStateException();
/*      */     }
/*   76 */     return ordersProvider;
/*      */   }
/*      */ 
/*      */   public static void createInstance(IOrderUtils orderUtils) {
/*   80 */     if (ordersProvider == null)
/*   81 */       ordersProvider = new OrdersProvider(orderUtils);
/*      */   }
/*      */ 
/*      */   static void clearInstance()
/*      */   {
/*   89 */     ordersProvider = null;
/*      */   }
/*      */ 
/*      */   private OrdersProvider(IOrderUtils orderUtils) {
/*   93 */     super(orderUtils);
/*      */   }
/*      */ 
/*      */   public synchronized void clearOrders() {
/*   97 */     for (int i = 0; i < this.ordersByInstrument.length; i++) {
/*   98 */       Instrument instrument = Instrument.values()[i];
/*   99 */       Map orders = this.ordersByInstrument[i];
/*  100 */       if (orders != null) {
/*  101 */         boolean someOrdersDeleted = false;
/*  102 */         for (Iterator iterator = orders.entrySet().iterator(); iterator.hasNext(); ) {
/*  103 */           Map.Entry ordersEntry = (Map.Entry)iterator.next();
/*  104 */           OrderHistoricalData order = (OrderHistoricalData)ordersEntry.getValue();
/*  105 */           if (!order.isClosed()) {
/*  106 */             someOrdersDeleted = true;
/*  107 */             iterator.remove();
/*      */           }
/*      */         }
/*  110 */         if (someOrdersDeleted)
/*  111 */           fireOrdersInvalidated(instrument);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void orderSynch(OrderSyncMessage synchMessage)
/*      */   {
/*  118 */     Collection positionIds = synchMessage.getPositionIds();
/*  119 */     Collection orderIds = synchMessage.getOrderIds();
/*      */ 
/*  121 */     for (int i = 0; i < this.ordersByInstrument.length; i++) {
/*  122 */       Instrument instrument = Instrument.values()[i];
/*  123 */       Map orders = this.ordersByInstrument[i];
/*  124 */       if (orders != null) {
/*  125 */         boolean someOrdersDeleted = false;
/*  126 */         for (Iterator iterator = orders.values().iterator(); iterator.hasNext(); ) {
/*  127 */           OrderHistoricalData order = (OrderHistoricalData)iterator.next();
/*  128 */           if ((!positionIds.contains(order.getOrderGroupId())) && (!orderIds.contains(order.getOrderGroupId())))
/*      */           {
/*  130 */             List pendingOrders = order.getPendingOrders();
/*  131 */             if ((pendingOrders == null) || (pendingOrders.isEmpty()) || (!orderIds.contains(((OrderHistoricalData.OpenData)pendingOrders.get(0)).getOrderId()))) {
/*  132 */               iterator.remove();
/*  133 */               someOrdersDeleted = true;
/*      */             }
/*      */           }
/*      */         }
/*  137 */         if (someOrdersDeleted)
/*  138 */           fireOrdersInvalidated(instrument);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void groupsMerged(MergePositionsMessage mergePositionsMessage)
/*      */   {
/*  145 */     boolean debugEnabled = LOGGER.isDebugEnabled();
/*  146 */     String newOrderGroupId = mergePositionsMessage.getNewOrderGroupId();
/*  147 */     assert (newOrderGroupId != null) : "newOrderGroupId in mergePositionsMessage is null";
/*  148 */     Collection mergedGroupIds = mergePositionsMessage.getPositionsList();
/*  149 */     assert (mergedGroupIds.size() > 1) : "number of merged positions is less than 2, how possible?";
/*  150 */     if (debugEnabled)
/*  151 */       LOGGER.debug("groupsMerged method called with new group id [" + newOrderGroupId + "] and merged positions count [" + mergedGroupIds.size() + "]");
/*  152 */     OrderHistoricalDataMutable newOrderGroup = null;
/*  153 */     Instrument instrument = null;
/*  154 */     for (int i = 0; i < this.ordersByInstrument.length; i++) {
/*  155 */       Map orderHistData = this.ordersByInstrument[i];
/*  156 */       if ((orderHistData != null) && (orderHistData.containsKey(newOrderGroupId))) {
/*  157 */         OrderHistoricalData orderHistoricalData = (OrderHistoricalData)orderHistData.get(newOrderGroupId);
/*  158 */         newOrderGroup = orderHistoricalData == null ? null : new OrderHistoricalDataMutable(orderHistoricalData);
/*  159 */         instrument = INSTRUMENT_VALUES[i];
/*  160 */         break;
/*      */       }
/*      */     }
/*  163 */     boolean newOrder = false;
/*  164 */     if (newOrderGroup == null) {
/*  165 */       LOGGER.debug("merged resulting group with id [" + newOrderGroupId + "] was not found");
/*      */ 
/*  168 */       if (mergedGroupIds.isEmpty())
/*      */       {
/*  170 */         return;
/*      */       }
/*  172 */       String mergedGroupId = (String)mergedGroupIds.iterator().next();
/*  173 */       for (int i = 0; i < this.ordersByInstrument.length; i++) {
/*  174 */         Map orderHistData = this.ordersByInstrument[i];
/*  175 */         if ((orderHistData != null) && (orderHistData.containsKey(mergedGroupId))) {
/*  176 */           instrument = INSTRUMENT_VALUES[i];
/*  177 */           break;
/*      */         }
/*      */       }
/*      */ 
/*  181 */       if (instrument == null) {
/*  182 */         LOGGER.error("Could not find merged group");
/*  183 */         return;
/*      */       }
/*      */ 
/*  187 */       newOrder = true;
/*  188 */       if (debugEnabled) {
/*  189 */         LOGGER.debug("Order group [" + newOrderGroupId + "] is new merging-to-zerro order");
/*      */       }
/*      */ 
/*  192 */       newOrderGroup = new OrderHistoricalDataMutable();
/*  193 */       newOrderGroup.setOrderGroupId(newOrderGroupId);
/*      */ 
/*  195 */       newOrderGroup.setClosed(true);
/*  196 */       OrderHistoricalDataMutable.CloseData closeData = new OrderHistoricalDataMutable.CloseData();
/*  197 */       closeData.setAmount(BigDecimal.ZERO);
/*  198 */       newOrderGroup.putCloseData(newOrderGroupId, closeData);
/*  199 */       OrderHistoricalDataMutable.OpenData entryOrder = new OrderHistoricalDataMutable.OpenData();
/*  200 */       entryOrder.setAmount(BigDecimal.ZERO);
/*  201 */       newOrderGroup.setEntryOrder(entryOrder);
/*  202 */       newOrderGroup.setOpened(true);
/*      */     }
/*      */     else
/*      */     {
/*  206 */       newOrderGroup.getEntryOrder().setFillTime(mergePositionsMessage.getTimestamp().getTime());
/*      */     }
/*  208 */     assert (instrument != null) : "instrument cannot be null if merge resulting group was found";
/*      */ 
/*  211 */     List mergedOrders = new ArrayList(mergedGroupIds.size());
/*  212 */     BigDecimal closePrice = BigDecimal.ZERO;
/*  213 */     BigDecimal amount = BigDecimal.ZERO;
/*  214 */     OrderHistoricalDataMutable.OpenData entryOrder = newOrderGroup.getEntryOrder();
/*  215 */     for (String mergedGroupId : mergedGroupIds) {
/*  216 */       String[] mergedFrom = new String[entryOrder.getMergedFrom() == null ? 1 : entryOrder.getMergedFrom().length + 1];
/*  217 */       if (entryOrder.getMergedFrom() != null) {
/*  218 */         System.arraycopy(entryOrder.getMergedFrom(), 0, mergedFrom, 0, entryOrder.getMergedFrom().length);
/*      */       }
/*  220 */       mergedFrom[(mergedFrom.length - 1)] = mergedGroupId;
/*  221 */       entryOrder.setMergedFrom(mergedFrom);
/*      */ 
/*  223 */       Map orderGroups = this.ordersByInstrument[instrument.ordinal()];
/*  224 */       OrderHistoricalData orderHistoricalData = (OrderHistoricalData)orderGroups.get(mergedGroupId);
/*  225 */       if (orderHistoricalData == null) {
/*  226 */         LOGGER.error("merged group with id [" + mergedGroupId + "] was not found");
/*      */ 
/*  228 */         continue;
/*      */       }
/*  230 */       OrderHistoricalDataMutable mergedGroup = new OrderHistoricalDataMutable(orderHistoricalData);
/*  231 */       mergedGroup.removeCloseData(mergedGroupId);
/*  232 */       mergedGroup.setClosed(false);
/*  233 */       mergedGroup.setMergedToGroupId(newOrderGroupId);
/*  234 */       mergedGroup.setMergedToTime(mergePositionsMessage.getTimestamp().getTime());
/*  235 */       extendOrderHistoryRange(mergedGroup, mergedGroup.getMergedToTime());
/*  236 */       orderGroups.remove(mergedGroupId);
/*      */ 
/*  238 */       OrderHistoricalDataMutable.OpenData mergedGroupEntryOrder = mergedGroup.getEntryOrder();
/*  239 */       if (mergedGroupEntryOrder != null) {
/*  240 */         extendOrderHistoryRange(newOrderGroup, mergedGroupEntryOrder.getFillTime());
/*  241 */         closePrice = closePrice.add(mergedGroupEntryOrder.getAmount().multiply(mergedGroupEntryOrder.getOpenPrice()));
/*  242 */         amount = amount.add(mergedGroupEntryOrder.getAmount());
/*  243 */         if ((newOrder) && (entryOrder.getSide() == null)) {
/*  244 */           entryOrder.setSide(mergedGroupEntryOrder.getSide());
/*      */         }
/*      */       }
/*  247 */       if (debugEnabled)
/*  248 */         LOGGER.debug("group [" + mergedGroupId + "] merged and removed from global orders list");
/*  249 */       orderHistoricalData = new OrderHistoricalData(mergedGroup);
/*  250 */       mergedOrders.add(orderHistoricalData);
/*      */     }
/*  252 */     if (newOrder) {
/*  253 */       long timestamp = mergePositionsMessage.getTimestamp() != null ? mergePositionsMessage.getTimestamp().getTime() : System.currentTimeMillis();
/*      */ 
/*  255 */       entryOrder.setFillTime(timestamp);
/*  256 */       entryOrder.setCreationTime(timestamp);
/*  257 */       entryOrder.setOrderId(mergePositionsMessage.getNewOrderGroupId());
/*  258 */       entryOrder.setLabel(mergePositionsMessage.getExternalSysId());
/*  259 */       OrderHistoricalDataMutable.CloseData closeData = (OrderHistoricalDataMutable.CloseData)newOrderGroup.getCloseDataMap().get(newOrderGroupId);
/*  260 */       closeData.setCloseTime(timestamp);
/*      */ 
/*  262 */       closeData.setClosePrice(closePrice.divide(amount, BigDecimal.valueOf(instrument.getPipValue()).scale() + 1, 6));
/*  263 */       entryOrder.setOpenPrice(closeData.getClosePrice());
/*      */     }
/*      */ 
/*  266 */     extendOrderHistoryRange(newOrderGroup, entryOrder.getFillTime());
/*      */ 
/*  269 */     Map orderGroups = this.ordersByInstrument[instrument.ordinal()];
/*  270 */     if (orderGroups == null) {
/*  271 */       orderGroups = new HashMap();
/*  272 */       this.ordersByInstrument[instrument.ordinal()] = orderGroups;
/*      */     }
/*  274 */     OrderHistoricalData immutableOrderGroup = new OrderHistoricalData(newOrderGroup);
/*  275 */     orderGroups.put(newOrderGroupId, immutableOrderGroup);
/*      */ 
/*  277 */     fireOrderMerge(instrument, immutableOrderGroup, mergedOrders);
/*      */   }
/*      */ 
/*      */   public synchronized void updateOrderGroup(OrderGroupMessage orderGroup) {
/*  281 */     boolean debugEnabled = LOGGER.isDebugEnabled();
/*  282 */     if (debugEnabled)
/*  283 */       LOGGER.debug("Order group update recived [" + orderGroup.getOrderGroupId() + "]");
/*  284 */     if (orderGroup.getInstrument() == null) {
/*  285 */       LOGGER.debug("Instrument is not passed.");
/*  286 */       return;
/*      */     }
/*  288 */     Instrument instrument = Instrument.fromString(orderGroup.getInstrument());
/*  289 */     List ordersUnsorted = orderGroup.getOrders();
/*  290 */     assert (instrument != null) : ("Instrument of order group cannot be recognized [" + orderGroup.getInstrument() + "]");
/*  291 */     Map orderGroups = this.ordersByInstrument[instrument.ordinal()];
/*  292 */     if (orderGroups == null) {
/*  293 */       orderGroups = new HashMap();
/*  294 */       this.ordersByInstrument[instrument.ordinal()] = orderGroups;
/*      */     }
/*      */ 
/*  297 */     boolean isNew = false;
/*  298 */     OrderHistoricalData orderHistoricalData = (OrderHistoricalData)orderGroups.get(orderGroup.getOrderGroupId());
/*      */     OrderHistoricalDataMutable lineData;
/*  300 */     if (orderHistoricalData == null) {
/*  301 */       if (ordersUnsorted.isEmpty())
/*      */       {
/*  304 */         ExposureData exposure = this.exposures[instrument.ordinal()];
/*  305 */         if (exposure == null) {
/*  306 */           exposure = new ExposureData(instrument);
/*  307 */           this.exposures[instrument.ordinal()] = exposure;
/*      */         }
/*      */ 
/*  310 */         synchronized (exposure) {
/*  311 */           Money amount = orderGroup.getAmount();
/*  312 */           if (amount == null) {
/*  313 */             return;
/*      */           }
/*  315 */           if (amount.getValue().compareTo(BigDecimal.ZERO) == 0) {
/*  316 */             exposure.amount = BigDecimal.ZERO;
/*  317 */             exposure.side = null;
/*  318 */             exposure.price = null;
/*  319 */             exposure.time = 0L;
/*      */           } else {
/*  321 */             PositionSide side = orderGroup.getSide();
/*  322 */             Money price = orderGroup.getPricePosOpen();
/*  323 */             if ((price == null) || (side == null)) {
/*  324 */               return;
/*      */             }
/*  326 */             exposure.amount = amount.getValue();
/*  327 */             exposure.side = (side == PositionSide.LONG ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL);
/*  328 */             exposure.price = price.getValue();
/*  329 */             exposure.time = orderGroup.getTimestamp().getTime();
/*      */           }
/*      */         }
/*  332 */         return;
/*      */       }
/*  334 */       if (debugEnabled) {
/*  335 */         LOGGER.debug("Order group [" + orderGroup.getOrderGroupId() + "] is new");
/*      */       }
/*  337 */       OrderHistoricalDataMutable lineData = new OrderHistoricalDataMutable();
/*  338 */       lineData.setOrderGroupId(orderGroup.getOrderGroupId());
/*  339 */       isNew = true;
/*      */     } else {
/*  341 */       lineData = new OrderHistoricalDataMutable(orderHistoricalData);
/*      */     }
/*      */ 
/*  345 */     SortedSet orders = new TreeSet(new Comparator() {
/*      */       public int compare(OrderMessage o1, OrderMessage o2) {
/*  347 */         if ((o1.isOpening()) && (!o2.isOpening()))
/*  348 */           return -1;
/*  349 */         if ((o2.isOpening()) && (!o1.isOpening())) {
/*  350 */           return 1;
/*      */         }
/*  352 */         return o1.getOrderId().compareTo(o2.getOrderId());
/*      */       }
/*      */     });
/*  356 */     orders.addAll(ordersUnsorted);
/*  357 */     if (debugEnabled)
/*  358 */       LOGGER.debug("Number of orders [" + orders.size() + "]");
/*  359 */     if (orders.isEmpty()) {
/*  360 */       if (!lineData.isClosed()) {
/*  361 */         if (debugEnabled) {
/*  362 */           LOGGER.debug("Empty order group recived, order group [" + orderGroup.getOrderGroupId() + "] isn't closed, " + "saving closing data and marking lineData as closed");
/*      */         }
/*      */ 
/*  367 */         OrderHistoricalDataMutable.CloseData closeData = (OrderHistoricalDataMutable.CloseData)lineData.getCloseDataMap().get(orderGroup.getOrderGroupId());
/*  368 */         if (closeData == null) {
/*  369 */           closeData = new OrderHistoricalDataMutable.CloseData();
/*  370 */           lineData.putCloseData(orderGroup.getOrderGroupId(), closeData);
/*      */         }
/*  372 */         if (orderGroup.getTimestamp() != null) {
/*  373 */           closeData.setCloseTime(orderGroup.getTimestamp().getTime());
/*      */         }
/*      */         else
/*      */         {
/*  377 */           closeData.setCloseTime(System.currentTimeMillis());
/*      */         }
/*      */ 
/*  380 */         extendOrderHistoryRange(lineData, closeData.getCloseTime());
/*  381 */         if (lineData.getEntryOrder() != null) {
/*  382 */           closeData.setAmount(lineData.getEntryOrder().getAmount()); } else {
/*  383 */           if (!lineData.getPendingOrders().isEmpty())
/*      */           {
/*  385 */             orderGroups.remove(orderGroup.getOrderGroupId());
/*  386 */             lineData.setClosed(true);
/*  387 */             fireOrderChange(instrument, new OrderHistoricalData(lineData));
/*  388 */             return;
/*      */           }
/*      */ 
/*  392 */           orderGroups.remove(orderGroup.getOrderGroupId());
/*  393 */           return;
/*      */         }
/*      */ 
/*  397 */         Money priceOpen = orderGroup.getPriceOpen();
/*  398 */         if (priceOpen != null) {
/*  399 */           closeData.setClosePrice(priceOpen.getValue());
/*  400 */           if (lineData.isOpened()) {
/*  401 */             updateClosedCorrection(instrument, lineData.getEntryOrder().getSide(), lineData.getEntryOrder().getOpenPrice().doubleValue(), closeData.getClosePrice().doubleValue(), closeData.getAmount().doubleValue());
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  416 */           closeData.setClosePrice(OrderHistoricalData.NEG_ONE);
/*      */         }
/*  418 */         if (debugEnabled)
/*  419 */           LOGGER.debug("Close data - [" + orderGroup.getOrderGroupId() + ", " + closeData.getCloseTime() + ", " + closeData.getClosePrice() + "]");
/*      */       }
/*  421 */       lineData.setClosed(true);
/*      */ 
/*  423 */       if (!lineData.isOpened())
/*  424 */         orderGroups.remove(orderGroup.getOrderGroupId());
/*      */     }
/*      */     else
/*      */     {
/*  428 */       OrderHistoricalDataMutable.OpenData entryOrder = lineData.getEntryOrder();
/*  429 */       if (entryOrder != null) {
/*  430 */         entryOrder.setStopLossPrice(OrderHistoricalData.NEG_ONE);
/*  431 */         entryOrder.setStopLossSlippage(null);
/*  432 */         entryOrder.setStopLossOrderId(null);
/*  433 */         entryOrder.setTrailingStep(null);
/*  434 */         entryOrder.setTakeProfitPrice(OrderHistoricalData.NEG_ONE);
/*  435 */         entryOrder.setTakeProfitSlippage(null);
/*  436 */         entryOrder.setTakeProfitOrderId(null);
/*      */       }
/*      */ 
/*  439 */       lineData.clearPendingOrders();
/*      */ 
/*  441 */       lineData.setClosed(false);
/*      */ 
/*  443 */       lineData.setOco(false);
/*      */     }
/*  445 */     for (OrderMessage orderMessage : orders) {
/*  446 */       if (orderMessage.isOpening())
/*  447 */         switch (3.$SwitchMap$com$dukascopy$transport$common$model$type$OrderState[orderMessage.getOrderState().ordinal()])
/*      */         {
/*      */         case 1:
/*      */         case 2:
/*  451 */           OrderHistoricalDataMutable.OpenData pendingOpeningOrder = new OrderHistoricalDataMutable.OpenData();
/*  452 */           pendingOpeningOrder.setOrderId(orderMessage.getOrderId());
/*  453 */           pendingOpeningOrder.setCreationTime((orderMessage.getCreatedDate() == null ? null : Long.valueOf(orderMessage.getCreatedDate().getTime())).longValue());
/*  454 */           pendingOpeningOrder.setAmount(orderMessage.getAmount().getValue());
/*  455 */           pendingOpeningOrder.setOpenPrice(getStopPrice(orderMessage));
/*  456 */           pendingOpeningOrder.setOpenSlippage(orderMessage.getPriceTrailingLimit() == null ? null : orderMessage.getPriceTrailingLimit().getValue());
/*  457 */           if (orderMessage.getOrderState() == OrderState.EXECUTING) {
/*  458 */             pendingOpeningOrder.setExecuting(true);
/*      */           }
/*  460 */           OrderSide side = orderMessage.getSide();
/*  461 */           if (side != null) {
/*  462 */             pendingOpeningOrder.setSide(convert(side, orderMessage.getStopDirection(), false, orderMessage.isPlaceOffer()));
/*      */           }
/*  464 */           pendingOpeningOrder.setLabel(orderMessage.getExternalSysId());
/*  465 */           if (pendingOpeningOrder.getLabel() == null) {
/*  466 */             pendingOpeningOrder.setLabel("JF" + orderMessage.getOrderId());
/*      */           }
/*  468 */           pendingOpeningOrder.setComment(orderMessage.getTag());
/*  469 */           lineData.addPendingOrder(pendingOpeningOrder);
/*  470 */           if (orderMessage.isOco().booleanValue()) {
/*  471 */             lineData.setOco(true);
/*      */           }
/*  473 */           if (orderMessage.getExecTimeoutMillis() != null) {
/*  474 */             pendingOpeningOrder.setGoodTillTime(orderMessage.getExecTimeoutMillis().longValue());
/*      */           }
/*      */ 
/*  477 */           if (debugEnabled)
/*  478 */             LOGGER.debug("Pending opening order found [" + orderMessage.getOrderId() + ", " + pendingOpeningOrder.getSide() + ", " + pendingOpeningOrder.getOpenPrice() + "]"); break;
/*      */         case 3:
/*  481 */           if (!lineData.isOpened())
/*      */           {
/*  484 */             lineData.setOpened(true);
/*      */ 
/*  486 */             Date timestamp = orderMessage.getTimestamp();
/*      */ 
/*  488 */             assert ((orderMessage.getSide() != null) && (timestamp != null)) : "Opening side or time is null";
/*      */ 
/*  490 */             OrderHistoricalDataMutable.OpenData entryOrder = lineData.getEntryOrder();
/*  491 */             if (lineData.getEntryOrder() == null)
/*      */             {
/*  493 */               entryOrder = new OrderHistoricalDataMutable.OpenData();
/*  494 */               lineData.setEntryOrder(entryOrder);
/*  495 */               entryOrder.setOrderId(orderMessage.getOrderId());
/*  496 */               entryOrder.setCreationTime(orderMessage.getCreatedDate().getTime());
/*      */ 
/*  498 */               extendOrderHistoryRange(lineData, entryOrder.getCreationTime());
/*  499 */             } else if (!entryOrder.getOrderId().equals(orderMessage.getOrderId())) {
/*  500 */               LOGGER.warn("Got entry order with order id different than was saved for this order group message");
/*  501 */               entryOrder.setOrderId(orderMessage.getOrderId());
/*      */             }
/*  503 */             entryOrder.setSide(convert(orderMessage.getSide(), orderMessage.getStopDirection(), true, false));
/*      */ 
/*  505 */             entryOrder.setOpenPrice(orderGroup.getPosition().getPriceOpen().getValue());
/*  506 */             entryOrder.setFillTime(timestamp.getTime());
/*  507 */             entryOrder.setLabel(orderMessage.getExternalSysId());
/*  508 */             if (entryOrder.getLabel() == null) {
/*  509 */               entryOrder.setLabel("JF" + orderMessage.getOrderId());
/*      */             }
/*  511 */             entryOrder.setComment(orderMessage.getTag());
/*      */ 
/*  513 */             extendOrderHistoryRange(lineData, entryOrder.getFillTime());
/*  514 */             entryOrder.setOpenSlippage(null);
/*  515 */             lineData.setOco(false);
/*  516 */             entryOrder.setAmount(orderMessage.getAmount().getValue());
/*  517 */             if (debugEnabled)
/*  518 */               LOGGER.debug("Filled opening order found [" + orderMessage.getOrderId() + ", " + entryOrder.getSide() + ", " + entryOrder.getFillTime() + ", " + entryOrder.getOpenPrice() + "]");
/*      */           }
/*      */           else {
/*  521 */             if (debugEnabled)
/*  522 */               LOGGER.debug("Checking if order group was partially closed");
/*  523 */             if (orderMessage.getAmount() == null) {
/*  524 */               LOGGER.error("Order group [" + orderGroup.getOrderGroupId() + "], order [" + orderMessage.getOrderId() + "] doesn't have amount set");
/*  525 */               continue;
/*      */             }
/*  527 */             if (orderMessage.getOrderState() == null) {
/*  528 */               LOGGER.error("Order group [" + orderGroup.getOrderGroupId() + "], order [" + orderMessage.getOrderId() + "] doesn't have order state set");
/*  529 */               continue;
/*      */             }
/*  531 */             OrderHistoricalDataMutable.OpenData entryOrder = lineData.getEntryOrder();
/*  532 */             entryOrder.setLabel(orderMessage.getExternalSysId());
/*  533 */             if (entryOrder.getLabel() == null) {
/*  534 */               entryOrder.setLabel("JF" + orderMessage.getOrderId());
/*      */             }
/*  536 */             entryOrder.setComment(orderMessage.getTag());
/*  537 */             if ((!entryOrder.getOrderId().equals(orderMessage.getOrderId())) && (orderMessage.getAmount().getValue().compareTo(entryOrder.getAmount()) < 0))
/*      */             {
/*  540 */               Money priceOpen = orderGroup.getPriceOpen();
/*  541 */               if (priceOpen != null)
/*      */               {
/*  543 */                 OrderHistoricalDataMutable.CloseData closeData = (OrderHistoricalDataMutable.CloseData)lineData.getCloseDataMap().get(orderMessage.getOrderId());
/*  544 */                 if (closeData == null) {
/*  545 */                   closeData = new OrderHistoricalDataMutable.CloseData();
/*  546 */                   lineData.putCloseData(orderMessage.getOrderId(), closeData);
/*      */                 }
/*      */ 
/*  549 */                 closeData.setClosePrice(priceOpen.getValue());
/*      */ 
/*  551 */                 closeData.setCloseTime(orderGroup.getTimestamp().getTime());
/*      */ 
/*  553 */                 extendOrderHistoryRange(lineData, closeData.getCloseTime());
/*      */ 
/*  555 */                 closeData.setAmount(entryOrder.getAmount().subtract(orderMessage.getAmount().getValue()));
/*  556 */                 if (debugEnabled) {
/*  557 */                   LOGGER.debug("Filled closing order found [" + orderMessage.getOrderId() + ", " + closeData.getCloseTime() + ", " + closeData.getClosePrice() + ", " + closeData.getAmount() + "]");
/*      */                 }
/*  559 */                 entryOrder.setAmount(orderMessage.getAmount().getValue());
/*  560 */                 updateClosedCorrection(instrument, entryOrder.getSide(), entryOrder.getOpenPrice().doubleValue(), closeData.getClosePrice().doubleValue(), closeData.getAmount().doubleValue());
/*      */               }
/*      */             }
/*      */ 
/*  564 */             if ((!entryOrder.getOrderId().equals(orderMessage.getOrderId())) && (orderMessage.getAmount().getValue().compareTo(entryOrder.getAmount()) > 0))
/*      */             {
/*  568 */               extendOrderHistoryRange(lineData, orderMessage.getTimestamp().getTime());
/*  569 */               entryOrder.setAmount(orderMessage.getAmount().getValue());
/*      */             }
/*      */           }
/*      */         default:
/*  572 */           break;
/*      */         }
/*  574 */       if (orderMessage.isClosing()) {
/*  575 */         switch (3.$SwitchMap$com$dukascopy$transport$common$model$type$OrderState[orderMessage.getOrderState().ordinal()])
/*      */         {
/*      */         case 1:
/*  578 */           if (orderMessage.isStopLoss()) {
/*  579 */             String parentOrderId = orderMessage.getIfdParentOrderId();
/*  580 */             OrderHistoricalDataMutable.OpenData openData = null;
/*  581 */             if ((lineData.getEntryOrder() != null) && (lineData.getEntryOrder().getOrderId().equals(parentOrderId)))
/*  582 */               openData = lineData.getEntryOrder();
/*      */             else {
/*  584 */               for (OrderHistoricalData.OpenData orderOpenData : lineData.getPendingOrders()) {
/*  585 */                 if (orderOpenData.getOrderId().equals(parentOrderId)) {
/*  586 */                   openData = (OrderHistoricalDataMutable.OpenData)orderOpenData;
/*  587 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*  591 */             if (openData == null) {
/*  592 */               openData = lineData.getEntryOrder();
/*  593 */               if ((openData == null) && (lineData.getPendingOrders() != null) && (!lineData.getPendingOrders().isEmpty())) {
/*  594 */                 openData = (OrderHistoricalDataMutable.OpenData)lineData.getPendingOrders().get(0);
/*      */               }
/*      */             }
/*  597 */             if (openData != null) {
/*  598 */               openData.setStopLossPrice(getStopPrice(orderMessage));
/*  599 */               openData.setStopLossSlippage(orderMessage.getPriceTrailingLimit() == null ? null : orderMessage.getPriceTrailingLimit().getValue());
/*  600 */               openData.setStopLossByBid(((orderMessage.getStopDirection() == null) && (openData.getSide().isLong())) || ((orderMessage.getStopDirection() != null) && ((orderMessage.getStopDirection() == StopDirection.BID_GREATER) || (orderMessage.getStopDirection() == StopDirection.BID_LESS))));
/*  601 */               openData.setStopLossOrderId(orderMessage.getOrderId());
/*  602 */               openData.setTrailingStep(orderMessage.getPriceLimit() == null ? null : orderMessage.getPriceLimit().getValue());
/*  603 */               if (debugEnabled)
/*  604 */                 LOGGER.debug("Pending closing stop loss order found [" + orderMessage.getOrderId() + ", " + openData.getStopLossPrice() + "]");
/*  605 */               if (openData == lineData.getEntryOrder())
/*      */               {
/*  607 */                 extendOrderHistoryRange(lineData, orderMessage.getTimestamp().getTime());
/*      */               }
/*      */             } else {
/*  610 */               LOGGER.warn("Couldn't find where stop loss order [" + orderMessage.getOrderId() + "] belongs");
/*      */             }
/*      */           } else {
/*  612 */             if (!orderMessage.isTakeProfit()) break;
/*  613 */             String parentOrderId = orderMessage.getIfdParentOrderId();
/*  614 */             OrderHistoricalDataMutable.OpenData openData = null;
/*  615 */             if ((lineData.getEntryOrder() != null) && (lineData.getEntryOrder().getOrderId().equals(parentOrderId)))
/*  616 */               openData = lineData.getEntryOrder();
/*      */             else {
/*  618 */               for (OrderHistoricalData.OpenData orderOpenData : lineData.getPendingOrders()) {
/*  619 */                 if (orderOpenData.getOrderId().equals(parentOrderId)) {
/*  620 */                   openData = (OrderHistoricalDataMutable.OpenData)orderOpenData;
/*  621 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*  625 */             if (openData == null) {
/*  626 */               openData = lineData.getEntryOrder();
/*  627 */               if ((openData == null) && (lineData.getPendingOrders() != null) && (!lineData.getPendingOrders().isEmpty())) {
/*  628 */                 openData = (OrderHistoricalDataMutable.OpenData)lineData.getPendingOrders().get(0);
/*      */               }
/*      */             }
/*  631 */             if (openData != null) {
/*  632 */               openData.setTakeProfitPrice(getStopPrice(orderMessage));
/*  633 */               openData.setTakeProfitSlippage(orderMessage.getPriceTrailingLimit() == null ? null : orderMessage.getPriceTrailingLimit().getValue());
/*  634 */               openData.setTakeProfitOrderId(orderMessage.getOrderId());
/*  635 */               if (debugEnabled)
/*  636 */                 LOGGER.debug("Pending closing take profit order found [" + orderMessage.getOrderId() + ", " + openData.getTakeProfitPrice() + "]");
/*  637 */               if (openData == lineData.getEntryOrder())
/*      */               {
/*  639 */                 extendOrderHistoryRange(lineData, orderMessage.getTimestamp().getTime());
/*      */               }
/*      */             } else {
/*  642 */               LOGGER.warn("Couldn't find where take profit order [" + orderMessage.getOrderId() + "] belongs");
/*      */             }
/*      */           }
/*  644 */           break;
/*      */         case 2:
/*  648 */           break;
/*      */         case 3:
/*  650 */           OrderHistoricalDataMutable.CloseData closeData = (OrderHistoricalDataMutable.CloseData)lineData.getCloseDataMap().get(orderMessage.getOrderId());
/*  651 */           if (closeData == null) {
/*  652 */             closeData = new OrderHistoricalDataMutable.CloseData();
/*  653 */             lineData.putCloseData(orderMessage.getOrderId(), closeData);
/*      */           }
/*  655 */           closeData.setClosePrice(getStopPrice(orderMessage));
/*  656 */           closeData.setCloseTime(orderMessage.getCreatedDate().getTime());
/*      */ 
/*  658 */           extendOrderHistoryRange(lineData, closeData.getCloseTime());
/*  659 */           closeData.setAmount(orderMessage.getAmount().getValue());
/*  660 */           if (debugEnabled) {
/*  661 */             LOGGER.debug("Filled closing order found [" + orderMessage.getOrderId() + ", " + closeData.getCloseTime() + ", " + closeData.getClosePrice() + ", " + closeData.getAmount() + "], marking line data as closed");
/*      */           }
/*  663 */           lineData.setClosed(true);
/*  664 */           if (!lineData.isOpened())
/*  665 */             orderGroups.remove(orderGroup.getOrderGroupId());
/*      */           else {
/*  667 */             updateClosedCorrection(instrument, lineData.getEntryOrder().getSide(), lineData.getEntryOrder().getOpenPrice().doubleValue(), closeData.getClosePrice().doubleValue(), closeData.getAmount().doubleValue());
/*      */           }
/*      */ 
/*  670 */           break;
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*  674 */           if (debugEnabled)
/*  675 */             LOGGER.debug("Cancelled closing order found [" + orderMessage.getOrderId() + "]");
/*  676 */           if (lineData.removeCloseData(orderMessage.getOrderId()) == null) break;
/*  677 */           if (debugEnabled)
/*  678 */             LOGGER.debug("Cancelled closing order removed from closeDataMap");
/*  679 */           lineData.setClosed(false);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  685 */     Money orderGroupCommission = orderGroup.getOrderGroupCommission();
/*  686 */     if (orderGroupCommission != null) {
/*  687 */       if (!orderGroupCommission.getCurrency().equals(this.calculatedAccount.getCurrency()))
/*  688 */         LOGGER.error("Order commission currency {} differs from current account's currency {}", orderGroupCommission.getCurrency(), this.calculatedAccount.getCurrency());
/*      */       else {
/*  690 */         lineData.setCommission(orderGroupCommission.getValue());
/*      */       }
/*      */     }
/*      */ 
/*  694 */     OrderHistoricalData immutableLineData = new OrderHistoricalData(lineData);
/*  695 */     orderGroups.put(orderGroup.getOrderGroupId(), immutableLineData);
/*      */ 
/*  697 */     if (isNew)
/*  698 */       fireOrderNew(instrument, immutableLineData);
/*      */     else
/*  700 */       fireOrderChange(instrument, immutableLineData);
/*      */   }
/*      */ 
/*      */   public synchronized void updateOrder(OrderMessage orderMessage)
/*      */   {
/*  705 */     boolean debugEnabled = LOGGER.isDebugEnabled();
/*  706 */     if (debugEnabled) {
/*  707 */       LOGGER.debug("Order update recived [" + orderMessage.getOrderId() + "]");
/*      */     }
/*  709 */     Instrument instrument = Instrument.fromString(orderMessage.getInstrument());
/*  710 */     assert (instrument != null) : ("Instrument of order group cannot be recognized [" + orderMessage.getInstrument() + "]");
/*  711 */     Map orderGroups = this.ordersByInstrument[instrument.ordinal()];
/*  712 */     if (orderGroups == null) {
/*  713 */       orderGroups = new HashMap();
/*  714 */       this.ordersByInstrument[instrument.ordinal()] = orderGroups;
/*      */     }
/*      */ 
/*  717 */     boolean isNew = false;
/*  718 */     OrderHistoricalData orderHistoricalData = (OrderHistoricalData)orderGroups.get(orderMessage.getParentOrderId());
/*      */     OrderHistoricalDataMutable lineData;
/*  720 */     if (orderHistoricalData == null) {
/*  721 */       if (debugEnabled) {
/*  722 */         LOGGER.debug("Order [" + orderMessage.getOrderId() + "] is new");
/*      */       }
/*  724 */       OrderHistoricalDataMutable lineData = new OrderHistoricalDataMutable();
/*  725 */       lineData.setOrderGroupId(orderMessage.getParentOrderId());
/*  726 */       isNew = true;
/*      */     } else {
/*  728 */       lineData = new OrderHistoricalDataMutable(orderHistoricalData);
/*      */     }
/*      */ 
/*  732 */     lineData.clearPendingOrders();
/*  733 */     switch (3.$SwitchMap$com$dukascopy$transport$common$model$type$OrderState[orderMessage.getOrderState().ordinal()])
/*      */     {
/*      */     case 1:
/*      */     case 2:
/*  737 */       OrderHistoricalDataMutable.OpenData pendingOpeningOrder = new OrderHistoricalDataMutable.OpenData();
/*  738 */       pendingOpeningOrder.setOrderId(orderMessage.getOrderId());
/*  739 */       pendingOpeningOrder.setCreationTime(orderMessage.getCreatedDate().getTime());
/*  740 */       pendingOpeningOrder.setAmount(orderMessage.getAmount().getValue());
/*  741 */       pendingOpeningOrder.setOpenPrice(getStopPrice(orderMessage));
/*  742 */       pendingOpeningOrder.setOpenSlippage(orderMessage.getPriceTrailingLimit() == null ? null : orderMessage.getPriceTrailingLimit().getValue());
/*  743 */       if (orderMessage.getOrderState() == OrderState.EXECUTING) {
/*  744 */         pendingOpeningOrder.setExecuting(true);
/*      */       }
/*  746 */       OrderSide side = orderMessage.getSide();
/*  747 */       if (side != null) {
/*  748 */         pendingOpeningOrder.setSide(convert(side, orderMessage.getStopDirection(), false, orderMessage.isPlaceOffer()));
/*      */       }
/*  750 */       pendingOpeningOrder.setLabel(orderMessage.getExternalSysId());
/*  751 */       if (pendingOpeningOrder.getLabel() == null) {
/*  752 */         pendingOpeningOrder.setLabel("JF" + orderMessage.getOrderId());
/*      */       }
/*  754 */       pendingOpeningOrder.setComment(orderMessage.getTag());
/*  755 */       lineData.addPendingOrder(pendingOpeningOrder);
/*  756 */       if (orderMessage.getExecTimeoutMillis() != null) {
/*  757 */         pendingOpeningOrder.setGoodTillTime(orderMessage.getExecTimeoutMillis().longValue());
/*      */       }
/*      */ 
/*  760 */       if (!debugEnabled) break;
/*  761 */       LOGGER.debug("Pending opening order found [" + orderMessage.getOrderId() + ", " + pendingOpeningOrder.getSide() + ", " + pendingOpeningOrder.getOpenPrice() + "]"); break;
/*      */     case 3:
/*  764 */       Date timestamp = null;
/*  765 */       if (!lineData.isOpened()) {
/*  766 */         timestamp = orderMessage.getTimestamp();
/*  767 */         assert (timestamp != null) : "Opening time is null";
/*      */       }
/*      */ 
/*  770 */       lineData.setOpened(true);
/*      */ 
/*  773 */       assert (orderMessage.getSide() != null) : "Opening side is null";
/*      */ 
/*  775 */       OrderHistoricalDataMutable.OpenData entryOrder = lineData.getEntryOrder();
/*  776 */       if (entryOrder == null)
/*      */       {
/*  778 */         entryOrder = new OrderHistoricalDataMutable.OpenData();
/*  779 */         lineData.setEntryOrder(entryOrder);
/*  780 */         entryOrder.setOrderId(orderMessage.getOrderId());
/*  781 */         entryOrder.setCreationTime(orderMessage.getCreatedDate().getTime());
/*  782 */       } else if (!entryOrder.getOrderId().equals(orderMessage.getOrderId())) {
/*  783 */         LOGGER.warn("Got entry order with order id different than was saved for this order group message");
/*  784 */         entryOrder.setOrderId(orderMessage.getOrderId());
/*      */       }
/*  786 */       entryOrder.setSide(convert(orderMessage.getSide(), orderMessage.getStopDirection(), true, false));
/*      */ 
/*  788 */       entryOrder.setOpenPrice(orderMessage.getPriceClient().getValue());
/*  789 */       if (timestamp != null) {
/*  790 */         entryOrder.setFillTime(timestamp.getTime());
/*      */       }
/*  792 */       entryOrder.setLabel(orderMessage.getExternalSysId());
/*  793 */       if (entryOrder.getLabel() == null) {
/*  794 */         entryOrder.setLabel("JF" + orderMessage.getOrderId());
/*      */       }
/*  796 */       entryOrder.setComment(orderMessage.getTag());
/*      */ 
/*  798 */       extendOrderHistoryRange(lineData, entryOrder.getFillTime());
/*  799 */       entryOrder.setOpenSlippage(null);
/*  800 */       lineData.setOco(false);
/*  801 */       entryOrder.setAmount(orderMessage.getAmount().getValue());
/*  802 */       if (!debugEnabled) break;
/*  803 */       LOGGER.debug("Filled opening order found [" + orderMessage.getOrderId() + ", " + entryOrder.getSide() + ", " + entryOrder.getFillTime() + ", " + entryOrder.getOpenPrice() + "]"); break;
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*  810 */       lineData.setClosed(true);
/*  811 */       if (lineData.isOpened()) break;
/*  812 */       orderGroups.remove(orderMessage.getOrderId());
/*      */     }
/*      */ 
/*  817 */     OrderHistoricalData immutableLineData = new OrderHistoricalData(lineData);
/*  818 */     orderGroups.put(orderMessage.getParentOrderId(), immutableLineData);
/*      */ 
/*  820 */     if (isNew)
/*  821 */       fireOrderNew(instrument, immutableLineData);
/*      */     else
/*  823 */       fireOrderChange(instrument, immutableLineData);
/*      */   }
/*      */ 
/*      */   private void updateClosedCorrection(Instrument instrument, IEngine.OrderCommand orderCommand, double openPrice, double closePrice, double amount)
/*      */   {
/*  828 */     if (this.calculatedAccount.getCurrency() == null)
/*      */     {
/*  830 */       return;
/*      */     }
/*      */     OfferSide side;
/*      */     double proffLoseInSecCCY;
/*      */     OfferSide side;
/*  834 */     if (orderCommand.isLong()) {
/*  835 */       double proffLoseInSecCCY = (closePrice - openPrice) * amount;
/*  836 */       side = OfferSide.BID;
/*      */     } else {
/*  838 */       proffLoseInSecCCY = (openPrice - closePrice) * amount;
/*  839 */       side = OfferSide.ASK;
/*      */     }
/*  841 */     double val = this.currencyConverter.convert(proffLoseInSecCCY, instrument.getSecondaryCurrency(), this.calculatedAccount.getCurrency(), side);
/*  842 */     if (!Double.isNaN(val)) {
/*  843 */       BigDecimal bigDecimal = BigDecimal.valueOf(val);
/*  844 */       if (bigDecimal != null)
/*  845 */         this.closedCorrection += bigDecimal.doubleValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateAccountInfoData(AccountInfoMessage accountInfoMessage)
/*      */   {
/*  851 */     String mcLeverageUse = accountInfoMessage.getString("mcLevUse");
/*  852 */     if (mcLeverageUse != null) {
/*  853 */       this.calculatedAccount.setMarginCutLevel(Integer.parseInt(mcLeverageUse));
/*      */     }
/*      */ 
/*  856 */     if (accountInfoMessage.getWeekendLeverage() != null) {
/*  857 */       this.calculatedAccount.setOverTheWeekendLeverage(accountInfoMessage.getWeekendLeverage().intValue());
/*      */     }
/*      */ 
/*  860 */     String mcEquityLimit = accountInfoMessage.getString("equityLimit");
/*  861 */     if (!ObjectUtils.isNullOrEmpty(mcEquityLimit)) {
/*  862 */       this.calculatedAccount.setStopLossLevel(new BigDecimal(mcEquityLimit).doubleValue());
/*      */     }
/*      */ 
/*  865 */     this.calculatedAccount.setCurrency(accountInfoMessage.getCurrency());
/*  866 */     Integer leverage = accountInfoMessage.getLeverage();
/*  867 */     if (leverage == null)
/*  868 */       this.calculatedAccount.setLeverage(1.0D);
/*      */     else {
/*  870 */       this.calculatedAccount.setLeverage(leverage.intValue());
/*      */     }
/*  872 */     if (accountInfoMessage.getUsableMargin() != null)
/*  873 */       this.calculatedAccount.setAccountInfoCreditLine(accountInfoMessage.getUsableMargin().getValue().multiply(new BigDecimal(this.calculatedAccount.getLeverage())).doubleValue());
/*      */     else {
/*  875 */       this.calculatedAccount.setAccountInfoCreditLine(0.0D);
/*      */     }
/*  877 */     if ((accountInfoMessage.getEquity() != null) && (accountInfoMessage.getEquity().getValue().doubleValue() > 0.0D))
/*  878 */       this.calculatedAccount.setAccountInfoEquity(accountInfoMessage.getEquity().getValue().doubleValue());
/*      */     else {
/*  880 */       this.calculatedAccount.setAccountInfoEquity(0.0D);
/*      */     }
/*  882 */     if ((accountInfoMessage.getBaseEquity() != null) && (accountInfoMessage.getBaseEquity().getValue().doubleValue() > 0.0D)) {
/*  883 */       this.calculatedAccount.setBaseEquity(accountInfoMessage.getBaseEquity().getValue().doubleValue());
/*      */     }
/*  885 */     if ((accountInfoMessage.getBalance() != null) && (accountInfoMessage.getBalance().getValue().doubleValue() > 0.0D)) {
/*  886 */       this.calculatedAccount.setBalance(accountInfoMessage.getBalance().getValue().doubleValue());
/*      */     }
/*  888 */     if (accountInfoMessage.getAcountLoginId() != null) {
/*  889 */       this.calculatedAccount.setAccountId(accountInfoMessage.getUserId());
/*      */     }
/*  891 */     if (accountInfoMessage.getAccountState() != null) {
/*  892 */       this.calculatedAccount.setAccountState((IAccount.AccountState)EnumConverter.convert(accountInfoMessage.getAccountState(), IAccount.AccountState.class));
/*      */     }
/*  894 */     if ((accountInfoMessage.getEquity() != null) && (accountInfoMessage.getEquity().getValue().doubleValue() > 0.0D) && (accountInfoMessage.getUsableMargin() != null)) {
/*  895 */       double accountInfoUseOfLeverage = accountInfoMessage.getEquity().getValue().subtract(accountInfoMessage.getUsableMargin().getValue()).divide(accountInfoMessage.getEquity().getValue(), 2, RoundingMode.HALF_EVEN).doubleValue();
/*      */ 
/*  897 */       this.calculatedAccount.setAccountInfoUseOfLeverage(StratUtils.round(accountInfoUseOfLeverage * 100.0D, 5));
/*      */     } else {
/*  899 */       this.calculatedAccount.setAccountInfoUseOfLeverage(0.0D);
/*      */     }
/*  901 */     this.calculatedAccount.setGlobal(accountInfoMessage.isGlobal());
/*      */ 
/*  903 */     this.closedCorrection = 0.0D;
/*      */   }
/*      */ 
/*      */   public double recalculateEquity()
/*      */   {
/*  908 */     if (FeedDataProvider.getDefaultInstance().getCurrentTime() > this.calculatedAccount.getCalculationTime()) {
/*  909 */       recalculateAccountData();
/*  910 */       this.calculatedAccount.setCalculationTime(FeedDataProvider.getDefaultInstance().getCurrentTime());
/*      */     }
/*  912 */     return this.calculatedAccount.getEquity();
/*      */   }
/*      */ 
/*      */   protected ITick getLastTick(Instrument instrument)
/*      */   {
/*  917 */     return FeedDataProvider.getDefaultInstance().getLastTick(instrument);
/*      */   }
/*      */ 
/*      */   public double convert(double amount, Currency sourceCurrency, Currency targetCurrency, OfferSide side)
/*      */   {
/*  922 */     return this.currencyConverter.convert(amount, sourceCurrency, targetCurrency, side);
/*      */   }
/*      */ 
/*      */   public BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency, OfferSide side)
/*      */   {
/*  927 */     return this.currencyConverter.convert(amount, sourceCurrency, targetCurrency, side);
/*      */   }
/*      */ 
/*      */   private BigDecimal getStopPrice(OrderMessage order) {
/*  931 */     Money stopPrice = order.getPriceStop();
/*  932 */     if ((stopPrice == null) && 
/*  933 */       (order.isPlaceOffer())) {
/*  934 */       return order.getPriceClient().getValue();
/*      */     }
/*      */ 
/*  938 */     boolean stopDirExists = (null != order.getStopDirection()) && (null != stopPrice);
/*  939 */     if (stopDirExists) {
/*  940 */       return stopPrice.getValue();
/*      */     }
/*  942 */     if (order.getPriceClient() == null)
/*      */     {
/*  944 */       if (order.getSide() == OrderSide.BUY) {
/*  945 */         double val = FeedDataProvider.getDefaultInstance().getLastAsk(Instrument.fromString(order.getInstrument()));
/*  946 */         if (!Double.isNaN(val)) {
/*  947 */           return BigDecimal.valueOf(val);
/*      */         }
/*      */ 
/*  950 */         return BigDecimal.ZERO;
/*      */       }
/*      */ 
/*  953 */       double val = FeedDataProvider.getDefaultInstance().getLastBid(Instrument.fromString(order.getInstrument()));
/*  954 */       if (!Double.isNaN(val)) {
/*  955 */         return BigDecimal.valueOf(val);
/*      */       }
/*      */ 
/*  958 */       return BigDecimal.ZERO;
/*      */     }
/*      */ 
/*  962 */     return order.getPriceClient().getValue();
/*      */   }
/*      */ 
/*      */   public static IEngine.OrderCommand convert(OrderSide side, StopDirection stopDirection, boolean filled, boolean isBidOffer)
/*      */   {
/*  968 */     if (OrderSide.BUY == side) {
/*  969 */       if (((stopDirection == null) && (!isBidOffer)) || (filled))
/*  970 */         return IEngine.OrderCommand.BUY;
/*  971 */       if (isBidOffer) {
/*  972 */         return IEngine.OrderCommand.PLACE_BID;
/*      */       }
/*  974 */       switch (3.$SwitchMap$com$dukascopy$transport$common$model$type$StopDirection[stopDirection.ordinal()]) {
/*      */       case 1:
/*  976 */         return IEngine.OrderCommand.BUYSTOP_BYBID;
/*      */       case 2:
/*  978 */         return IEngine.OrderCommand.BUYLIMIT;
/*      */       case 3:
/*  980 */         return IEngine.OrderCommand.BUYSTOP;
/*      */       case 4:
/*  982 */         return IEngine.OrderCommand.BUYLIMIT;
/*      */       case 5:
/*  984 */         return IEngine.OrderCommand.BUYLIMIT_BYBID;
/*      */       case 6:
/*  986 */         return IEngine.OrderCommand.BUYLIMIT_BYBID;
/*      */       }
/*  988 */       return null;
/*      */     }
/*      */ 
/*  991 */     if (OrderSide.SELL == side) {
/*  992 */       if (((stopDirection == null) && (!isBidOffer)) || (filled))
/*  993 */         return IEngine.OrderCommand.SELL;
/*  994 */       if (isBidOffer) {
/*  995 */         return IEngine.OrderCommand.PLACE_OFFER;
/*      */       }
/*  997 */       switch (3.$SwitchMap$com$dukascopy$transport$common$model$type$StopDirection[stopDirection.ordinal()]) {
/*      */       case 1:
/*  999 */         return IEngine.OrderCommand.SELLLIMIT;
/*      */       case 2:
/* 1001 */         return IEngine.OrderCommand.SELLLIMIT;
/*      */       case 3:
/* 1003 */         return IEngine.OrderCommand.SELLLIMIT_BYASK;
/*      */       case 4:
/* 1005 */         return IEngine.OrderCommand.SELLSTOP_BYASK;
/*      */       case 5:
/* 1007 */         return IEngine.OrderCommand.SELLLIMIT;
/*      */       case 6:
/* 1009 */         return IEngine.OrderCommand.SELLSTOP;
/*      */       }
/* 1011 */       return null;
/*      */     }
/*      */ 
/* 1015 */     return null;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */   {
/* 1021 */     super.close();
/* 1022 */     if (this == getInstance())
/* 1023 */       ordersProvider = null;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   50 */     LOGGER = LoggerFactory.getLogger(OrdersProvider.class);
/*   51 */     INSTRUMENT_VALUES = Instrument.values();
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.orders.OrdersProvider
 * JD-Core Version:    0.6.0
 */