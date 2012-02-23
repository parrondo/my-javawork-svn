/*     */ package com.dukascopy.charts.data.orders;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.charts.data.datacache.ICurvesProtocolHandler.OrdersDataStruct;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.OpenData;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalDataMutable;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalDataMutable.CloseData;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalDataMutable.OpenData;
/*     */ import com.dukascopy.charts.data.datacache.OrdersListener;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.util.IOrderUtils;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.PositionStatus;
/*     */ import com.dukascopy.transport.common.msg.datafeed.MergeData;
/*     */ import com.dukascopy.transport.common.msg.datafeed.OrderData;
/*     */ import com.dukascopy.transport.common.msg.datafeed.OrderGroupData;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Currency;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractOrdersProvider
/*     */   implements IOrdersProvider
/*     */ {
/*     */   private static final Logger LOGGER;
/*     */   private static final Instrument[] INSTRUMENT_VALUES;
/*  46 */   private List<OrdersListener>[] ordersListenersByInstrument = new List[Instrument.values().length];
/*     */   private IOrderUtils orderUtils;
/*  49 */   protected Map<String, OrderHistoricalData>[] ordersByInstrument = new Map[Instrument.values().length];
/*  50 */   protected ExposureData[] exposures = new ExposureData[Instrument.values().length];
/*     */ 
/*  52 */   protected CalculatedAccount calculatedAccount = new CalculatedAccount();
/*     */   protected double closedCorrection;
/*     */ 
/*     */   public AbstractOrdersProvider()
/*     */   {
/*     */   }
/*     */ 
/*     */   public AbstractOrdersProvider(IOrderUtils orderUtils)
/*     */   {
/*  60 */     this.orderUtils = orderUtils;
/*     */   }
/*     */ 
/*     */   public IOrderUtils getOrderUtils() {
/*  64 */     return this.orderUtils;
/*     */   }
/*     */ 
/*     */   public synchronized void addOrdersListener(Instrument instrument, OrdersListener ordersListener) {
/*  68 */     List ordersListeners = this.ordersListenersByInstrument[instrument.ordinal()];
/*  69 */     if (ordersListeners == null) {
/*  70 */       ordersListeners = new ArrayList(1);
/*  71 */       this.ordersListenersByInstrument[instrument.ordinal()] = ordersListeners;
/*     */     }
/*  73 */     ordersListeners.add(ordersListener);
/*     */   }
/*     */ 
/*     */   public synchronized void removeOrdersListener(OrdersListener ordersListener)
/*     */   {
/*     */     Iterator iterator;
/*  77 */     for (List ordersListeners : this.ordersListenersByInstrument)
/*  78 */       if (ordersListeners != null)
/*  79 */         for (iterator = ordersListeners.iterator(); iterator.hasNext(); )
/*  80 */           if (iterator.next() == ordersListener)
/*  81 */             iterator.remove();
/*     */   }
/*     */ 
/*     */   public synchronized Collection<?>[] processHistoricalData(Instrument instrument, long from, long to, ICurvesProtocolHandler.OrdersDataStruct ordersData)
/*     */   {
/*  89 */     if (ordersData == null)
/*     */     {
/*  91 */       return new Collection[] { null, null };
/*     */     }
/*  93 */     Comparator ordersSortComparator = new Comparator()
/*     */     {
/*     */       public int compare(OrderData o1, OrderData o2) {
/*  96 */         long time1 = o1.getLastChanged();
/*  97 */         long time2 = o2.getLastChanged();
/*  98 */         return time1 == time2 ? 0 : time1 > time2 ? 1 : -1;
/*     */       }
/*     */     };
/* 101 */     Comparator mergesSortComparator = new Comparator()
/*     */     {
/*     */       public int compare(MergeData o1, MergeData o2) {
/* 104 */         long time1 = o1.getMergedTime();
/* 105 */         long time2 = o2.getMergedTime();
/* 106 */         return time1 == time2 ? 0 : time1 > time2 ? 1 : -1;
/*     */       }
/*     */     };
/* 110 */     List groupsList = ordersData.groups;
/* 111 */     Map groups = new HashMap();
/* 112 */     List ordersList = ordersData.orders;
/* 113 */     Map orders = new HashMap();
/* 114 */     List mergedPositions = ordersData.merges;
/* 115 */     for (OrderGroupData group : groupsList) {
/* 116 */       groups.put(group.getOrderGroupId(), group);
/*     */     }
/* 118 */     for (OrderData order : ordersList) {
/* 119 */       List groupOrders = (List)orders.get(order.getOrigGroupId());
/* 120 */       if (groupOrders == null) {
/* 121 */         groupOrders = new ArrayList();
/* 122 */         orders.put(order.getOrigGroupId(), groupOrders);
/*     */       }
/* 124 */       groupOrders.add(order);
/*     */     }
/* 126 */     for (List orderList : orders.values()) {
/* 127 */       Collections.sort(orderList, ordersSortComparator);
/*     */     }
/*     */ 
/* 130 */     Collection closedOrdersData = new ArrayList();
/* 131 */     Set openOrderIdsInPeriod = new HashSet();
/*     */ 
/* 135 */     Collections.sort(mergedPositions, mergesSortComparator);
/*     */ 
/* 137 */     Map rootNodes = new HashMap();
/* 138 */     for (MergeData merge : mergedPositions) {
/* 139 */       String newOrderGroupId = merge.getNewOrderGroupId();
/* 140 */       node = new MergeNode(null);
/* 141 */       node.orderGroupId = newOrderGroupId;
/* 142 */       node.createdDate = merge.getMergedTime();
/* 143 */       rootNodes.put(newOrderGroupId, node);
/* 144 */       for (String groupId : merge.getOrderGroupIds()) {
/* 145 */         MergeNode childNode = (MergeNode)rootNodes.remove(groupId);
/* 146 */         if (childNode == null) {
/* 147 */           childNode = new MergeNode(null);
/* 148 */           childNode.orderGroupId = groupId;
/*     */         }
/* 150 */         childNode.parentNode = node;
/* 151 */         if (node.childNodes == null) {
/* 152 */           node.childNodes = new ArrayList();
/*     */         }
/* 154 */         node.childNodes.add(childNode);
/*     */       }
/*     */     }
/*     */     MergeNode node;
/* 159 */     Deque stack = new ArrayDeque();
/* 160 */     for (MergeNode rootNode : rootNodes.values())
/*     */     {
/* 162 */       OrderGroupData orderGroup = (OrderGroupData)groups.get(rootNode.orderGroupId);
/* 163 */       if (orderGroup != null) {
/* 164 */         rootNode.childIndex = 0;
/* 165 */         if (orderGroup.getStatus() == PositionStatus.OPEN)
/*     */         {
/* 167 */           Map openOrders = this.ordersByInstrument[instrument.ordinal()];
/* 168 */           if (openOrders == null) {
/* 169 */             openOrders = new HashMap();
/* 170 */             this.ordersByInstrument[instrument.ordinal()] = openOrders;
/*     */           }
/* 172 */           OrderHistoricalData orderHistoricalData = (OrderHistoricalData)openOrders.get(rootNode.orderGroupId);
/* 173 */           if (orderHistoricalData == null)
/*     */           {
/*     */             continue;
/*     */           }
/* 177 */           rootNode.orderData = new OrderHistoricalDataMutable(orderHistoricalData);
/*     */         } else {
/* 179 */           rootNode.orderData = new OrderHistoricalDataMutable();
/* 180 */           rootNode.orderData.setOrderGroupId(rootNode.orderGroupId);
/* 181 */           rootNode.orderData.setClosed(true);
/*     */         }
/* 183 */         stack.push(rootNode);
/* 184 */         while (!stack.isEmpty()) {
/* 185 */           MergeNode node = (MergeNode)stack.peek();
/* 186 */           if ((node.childNodes == null) || (node.childIndex >= node.childNodes.size()))
/*     */           {
/* 188 */             stack.pop();
/*     */             List mergedFrom;
/*     */             List mergedFrom;
/* 190 */             if ((node.childNodes == null) || (node.childNodes.isEmpty())) {
/* 191 */               mergedFrom = Collections.emptyList();
/*     */             } else {
/* 193 */               mergedFrom = new ArrayList(node.childNodes.size());
/* 194 */               for (MergeNode childNode : node.childNodes) {
/* 195 */                 mergedFrom.add(childNode.orderData);
/*     */               }
/*     */             }
/* 198 */             groups.remove(node.orderGroupId);
/* 199 */             if (node.parentNode != null) {
/* 200 */               node.orderData.setMergedToGroupId(node.parentNode.orderGroupId);
/* 201 */               node.orderData.setMergedToTime(node.parentNode.createdDate);
/* 202 */               extendOrderHistoryRange(node.orderData, node.orderData.getMergedToTime());
/*     */             }
/* 204 */             processOrderGroup(node.orderData, orders, mergedFrom, from, to);
/* 205 */             if ((node.orderData.isClosed()) && (node.orderData.isOpened()) && (node.orderData.getHistoryStart() < to) && (node.orderData.getHistoryEnd() > from))
/* 206 */               closedOrdersData.add(new OrderHistoricalData(node.orderData));
/*     */           }
/*     */           else
/*     */           {
/* 210 */             MergeNode childNode = (MergeNode)node.childNodes.get(node.childIndex);
/* 211 */             childNode.childIndex = 0;
/* 212 */             childNode.orderData = new OrderHistoricalDataMutable();
/* 213 */             childNode.orderData.setOrderGroupId(childNode.orderGroupId);
/* 214 */             childNode.orderData.setClosed(true);
/* 215 */             stack.push(childNode);
/* 216 */             node.childIndex += 1;
/*     */           }
/*     */         }
/* 219 */         if (orderGroup.getStatus() == PositionStatus.OPEN) {
/* 220 */           Map openOrders = this.ordersByInstrument[instrument.ordinal()];
/* 221 */           OrderHistoricalData historicalData = new OrderHistoricalData(rootNode.orderData);
/* 222 */           openOrders.put(rootNode.orderData.getOrderGroupId(), historicalData);
/* 223 */           fireOrderChange(instrument, historicalData);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 229 */     for (OrderGroupData orderGroup : groups.values())
/*     */     {
/*     */       OrderHistoricalDataMutable orderData;
/*     */       OrderHistoricalDataMutable orderData;
/* 231 */       if (orderGroup.getStatus() == PositionStatus.OPEN)
/*     */       {
/* 233 */         Map openOrders = this.ordersByInstrument[instrument.ordinal()];
/* 234 */         if (openOrders == null) {
/* 235 */           openOrders = new HashMap();
/* 236 */           this.ordersByInstrument[instrument.ordinal()] = openOrders;
/*     */         }
/* 238 */         OrderHistoricalData orderHistoricalData = (OrderHistoricalData)openOrders.get(orderGroup.getOrderGroupId());
/* 239 */         if (orderHistoricalData == null)
/*     */         {
/*     */           continue;
/*     */         }
/* 243 */         orderData = new OrderHistoricalDataMutable(orderHistoricalData);
/*     */       } else {
/* 245 */         orderData = new OrderHistoricalDataMutable();
/* 246 */         orderData.setOrderGroupId(orderGroup.getOrderGroupId());
/* 247 */         orderData.setClosed(true);
/*     */       }
/*     */ 
/* 251 */       processOrderGroup(orderData, orders, Collections.emptyList(), from, to);
/* 252 */       if (orderGroup.getStatus() == PositionStatus.OPEN) {
/* 253 */         Map openOrders = this.ordersByInstrument[instrument.ordinal()];
/* 254 */         openOrders.put(orderData.getOrderGroupId(), new OrderHistoricalData(orderData));
/* 255 */         fireOrderChange(instrument, orderData);
/*     */       }
/* 257 */       if ((orderData.isClosed()) && (orderData.isOpened()) && (orderData.getHistoryStart() < to) && (orderData.getHistoryEnd() > from)) {
/* 258 */         closedOrdersData.add(new OrderHistoricalData(orderData));
/*     */       }
/*     */ 
/* 261 */       if (orderGroup.getStatus() == PositionStatus.OPEN) {
/* 262 */         Map openOrders = this.ordersByInstrument[instrument.ordinal()];
/* 263 */         if (openOrders == null) {
/* 264 */           openOrders = new HashMap();
/* 265 */           this.ordersByInstrument[instrument.ordinal()] = openOrders;
/*     */         }
/* 267 */         if (openOrders.containsKey(orderGroup.getOrderGroupId())) {
/* 268 */           openOrders.put(orderGroup.getOrderGroupId(), new OrderHistoricalData(orderData));
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 274 */     Map openOrders = this.ordersByInstrument[instrument.ordinal()];
/* 275 */     if (openOrders == null) {
/* 276 */       openOrders = new HashMap();
/* 277 */       this.ordersByInstrument[instrument.ordinal()] = openOrders;
/*     */     }
/* 279 */     for (OrderHistoricalData orderHistoricalData : openOrders.values()) {
/* 280 */       if (orderHistoricalData.getHistoryStart() <= to) {
/* 281 */         openOrderIdsInPeriod.add(orderHistoricalData.getOrderGroupId());
/*     */       }
/*     */     }
/*     */ 
/* 285 */     return new Collection[] { closedOrdersData, openOrderIdsInPeriod };
/*     */   }
/*     */ 
/*     */   private void processOrderGroup(OrderHistoricalDataMutable orderHistoricalData, Map<String, List<OrderData>> orders, List<OrderHistoricalDataMutable> mergedFrom, long from, long to)
/*     */   {
/* 291 */     List mergedIds = processMerges(orderHistoricalData, mergedFrom, from);
/*     */ 
/* 294 */     processOrders(orderHistoricalData, orders);
/* 295 */     if ((mergedIds != null) && (!mergedIds.isEmpty()) && (orderHistoricalData.getEntryOrder() != null))
/* 296 */       orderHistoricalData.getEntryOrder().setMergedFrom((String[])mergedIds.toArray(new String[mergedIds.size()]));
/*     */   }
/*     */ 
/*     */   private List<String> processMerges(OrderHistoricalDataMutable orderHistoricalData, List<OrderHistoricalDataMutable> mergedFrom, long from)
/*     */   {
/* 301 */     List mergedIds = new ArrayList();
/* 302 */     if ((mergedFrom != null) && (!mergedFrom.isEmpty()))
/*     */     {
/*     */       OrderHistoricalDataMutable.OpenData entryOrder;
/* 304 */       if (orderHistoricalData.getEntryOrder() == null) {
/* 305 */         OrderHistoricalDataMutable.OpenData entryOrder = new OrderHistoricalDataMutable.OpenData();
/* 306 */         entryOrder.setOrderId(orderHistoricalData.getOrderGroupId());
/*     */       } else {
/* 308 */         entryOrder = orderHistoricalData.getEntryOrder();
/*     */       }
/*     */ 
/* 311 */       BigDecimal amount = BigDecimal.ZERO;
/* 312 */       BigDecimal amountSum = BigDecimal.ZERO;
/* 313 */       BigDecimal amountSumLong = BigDecimal.ZERO;
/* 314 */       BigDecimal amountSumShort = BigDecimal.ZERO;
/* 315 */       BigDecimal amountMulPriceSum = BigDecimal.ZERO;
/* 316 */       BigDecimal amountMulPriceSumLong = BigDecimal.ZERO;
/* 317 */       BigDecimal amountMulPriceSumShort = BigDecimal.ZERO;
/* 318 */       long mergeTime = -9223372036854775808L;
/* 319 */       for (Iterator iterator = mergedFrom.iterator(); iterator.hasNext(); ) {
/* 320 */         OrderHistoricalDataMutable mergedData = (OrderHistoricalDataMutable)iterator.next();
/* 321 */         if (mergedData.isOpened()) {
/* 322 */           mergeTime = mergedData.getMergedToTime();
/*     */ 
/* 324 */           extendOrderHistoryRange(orderHistoricalData, mergedData.getEntryOrder().getFillTime());
/* 325 */           OrderHistoricalDataMutable.OpenData mergedDataEntryOrder = mergedData.getEntryOrder();
/* 326 */           if (mergedDataEntryOrder.getSide().isLong()) {
/* 327 */             amount = amount.add(mergedDataEntryOrder.getAmount());
/* 328 */             amountSum = amountSum.add(mergedDataEntryOrder.getAmount());
/* 329 */             amountSumLong = amountSumLong.add(mergedDataEntryOrder.getAmount());
/* 330 */             amountMulPriceSum = amountMulPriceSum.add(mergedDataEntryOrder.getAmount().multiply(mergedDataEntryOrder.getOpenPrice()));
/* 331 */             amountMulPriceSumLong = amountMulPriceSumLong.add(mergedDataEntryOrder.getAmount().multiply(mergedDataEntryOrder.getOpenPrice()));
/*     */           } else {
/* 333 */             amount = amount.subtract(mergedDataEntryOrder.getAmount());
/* 334 */             amountSum = amountSum.add(mergedDataEntryOrder.getAmount());
/* 335 */             amountSumShort = amountSumShort.add(mergedDataEntryOrder.getAmount());
/* 336 */             amountMulPriceSum = amountMulPriceSum.add(mergedDataEntryOrder.getAmount().multiply(mergedDataEntryOrder.getOpenPrice()));
/* 337 */             amountMulPriceSumShort = amountMulPriceSumShort.add(mergedDataEntryOrder.getAmount().multiply(mergedDataEntryOrder.getOpenPrice()));
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 342 */           iterator.remove();
/*     */         }
/* 344 */         mergedIds.add(mergedData.getOrderGroupId());
/*     */       }
/*     */ 
/* 347 */       if ((orderHistoricalData.getEntryOrder() == null) && (mergedFrom != null) && (mergedFrom.size() > 0)) {
/* 348 */         orderHistoricalData.setOpened(true);
/* 349 */         entryOrder.setFromMerges(true);
/*     */ 
/* 351 */         entryOrder.setAmount(amount.abs());
/* 352 */         int compareToRes = amount.compareTo(BigDecimal.ZERO);
/* 353 */         entryOrder.setSide(compareToRes >= 0 ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL);
/* 354 */         assert (mergeTime != -9223372036854775808L);
/* 355 */         entryOrder.setFillTime(mergeTime);
/* 356 */         entryOrder.setCreationTime(mergeTime);
/* 357 */         extendOrderHistoryRange(orderHistoricalData, entryOrder.getFillTime());
/* 358 */         entryOrder.setOpenPrice(compareToRes > 0 ? amountMulPriceSumLong.divide(amountSumLong, 7, 6) : compareToRes == 0 ? amountMulPriceSum.divide(amountSum, 7, 6) : amountMulPriceSumShort.divide(amountSumShort, 7, 6));
/*     */ 
/* 362 */         if ((compareToRes == 0) && 
/* 363 */           (orderHistoricalData.getCloseDataMap().size() == 0)) {
/* 364 */           OrderHistoricalDataMutable.CloseData closeData = new OrderHistoricalDataMutable.CloseData();
/* 365 */           closeData.setCloseTime(entryOrder.getFillTime());
/* 366 */           closeData.setClosePrice(entryOrder.getOpenPrice());
/* 367 */           orderHistoricalData.putCloseData(entryOrder.getOrderId(), closeData);
/*     */         }
/*     */ 
/* 370 */         orderHistoricalData.setEntryOrder(entryOrder);
/* 371 */       } else if (mergedFrom.isEmpty())
/*     */       {
/* 373 */         extendOrderHistoryRange(orderHistoricalData, from);
/*     */       } else {
/* 375 */         entryOrder.setFillTime(mergeTime);
/* 376 */         entryOrder.setCreationTime(mergeTime);
/*     */       }
/*     */     }
/* 379 */     return mergedIds;
/*     */   }
/*     */ 
/*     */   private void processOrders(OrderHistoricalDataMutable orderHistoricalData, Map<String, List<OrderData>> orders) {
/* 383 */     List groupOrderMessages = (List)orders.get(orderHistoricalData.getOrderGroupId());
/* 384 */     if (groupOrderMessages != null)
/* 385 */       for (int i = groupOrderMessages.size() - 1; i >= 0; i--) {
/* 386 */         OrderData orderMessage = (OrderData)groupOrderMessages.get(i);
/* 387 */         if (!orderMessage.isRollOver()) {
/* 388 */           if (orderMessage.getOrderDirection() == OrderDirection.CLOSE) {
/* 389 */             if (orderMessage.getOrderState() != OrderState.FILLED)
/*     */               continue;
/* 391 */             OrderHistoricalDataMutable.CloseData closeData = (OrderHistoricalDataMutable.CloseData)orderHistoricalData.getCloseDataMap().get(orderMessage.getOrderId());
/* 392 */             if (closeData == null) {
/* 393 */               closeData = new OrderHistoricalDataMutable.CloseData();
/* 394 */               closeData.setAmount(orderMessage.getAmount());
/* 395 */               closeData.setCloseTime(orderMessage.getLastChanged());
/* 396 */               closeData.setClosePrice(orderMessage.getPriceClient());
/* 397 */               orderHistoricalData.putCloseData(orderMessage.getOrderId(), closeData);
/* 398 */               extendOrderHistoryRange(orderHistoricalData, closeData.getCloseTime());
/*     */             }
/*     */           } else {
/* 401 */             if ((orderMessage.getOrderDirection() != OrderDirection.OPEN) || 
/* 402 */               (orderMessage.getOrderState() != OrderState.FILLED)) continue;
/* 403 */             orderHistoricalData.setOpened(true);
/* 404 */             if ((orderHistoricalData.getEntryOrder() != null) && (!orderHistoricalData.getEntryOrder().isRollovered()))
/*     */               continue;
/* 406 */             BigDecimal amountSum = orderMessage.getAmount();
/* 407 */             BigDecimal amountPriceSum = orderMessage.getAmount().multiply(orderMessage.getPriceClient());
/* 408 */             BigDecimal openPrice = orderMessage.getPriceClient();
/* 409 */             while (i - 1 >= 0) {
/* 410 */               OrderData orderFillMessage = (OrderData)groupOrderMessages.get(i - 1);
/* 411 */               if ((orderFillMessage.isRollOver()) || 
/* 412 */                 (orderFillMessage.getOrderDirection() != OrderDirection.OPEN)) break;
/* 413 */               if (orderFillMessage.getOrderState() == OrderState.FILLED)
/*     */               {
/* 415 */                 amountSum = amountSum.add(orderFillMessage.getAmount());
/* 416 */                 amountPriceSum = amountPriceSum.add(orderFillMessage.getAmount().multiply(orderFillMessage.getPriceClient()));
/* 417 */                 i--;
/*     */               }
/*     */               else {
/* 420 */                 i--;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 429 */             if (amountSum.compareTo(orderMessage.getAmount()) != 0) {
/* 430 */               openPrice = amountPriceSum.divide(amountSum, 6, 6);
/*     */             }
/* 432 */             if (orderHistoricalData.getEntryOrder() == null) {
/* 433 */               orderHistoricalData.setEntryOrder(new OrderHistoricalDataMutable.OpenData());
/*     */             }
/* 435 */             OrderHistoricalDataMutable.OpenData entryOrder = orderHistoricalData.getEntryOrder();
/* 436 */             if (!entryOrder.isRollovered()) {
/* 437 */               entryOrder.setOpenPrice(openPrice);
/*     */             }
/* 439 */             entryOrder.setRollovered(false);
/* 440 */             entryOrder.setOrderId(orderMessage.getOrderId());
/* 441 */             entryOrder.setAmount(amountSum);
/* 442 */             entryOrder.setCreationTime(orderMessage.getCreatedDate());
/* 443 */             entryOrder.setFillTime(orderMessage.getLastChanged());
/* 444 */             entryOrder.setSide(orderMessage.getSide() == OrderSide.BUY ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL);
/*     */ 
/* 446 */             extendOrderHistoryRange(orderHistoricalData, entryOrder.getCreationTime());
/* 447 */             extendOrderHistoryRange(orderHistoricalData, entryOrder.getFillTime());
/*     */           }
/*     */         }
/*     */         else {
/* 451 */           if (!orderMessage.isRollOver())
/*     */             continue;
/* 453 */           if ((orderMessage.getOrderDirection() != OrderDirection.OPEN) || 
/* 454 */             (orderMessage.getOrderState() != OrderState.FILLED)) continue;
/* 455 */           OrderHistoricalDataMutable.OpenData entryOrder = orderHistoricalData.getEntryOrder();
/* 456 */           if ((entryOrder != null) && (entryOrder.getOrderId().equals(orderMessage.getOrderId())))
/*     */           {
/* 459 */             entryOrder.setRollovered(true); } else {
/* 460 */             if ((entryOrder != null) && (!entryOrder.isFromMerges()))
/*     */               continue;
/* 462 */             if (entryOrder == null) {
/* 463 */               entryOrder = new OrderHistoricalDataMutable.OpenData();
/* 464 */               orderHistoricalData.setEntryOrder(entryOrder);
/*     */ 
/* 466 */               entryOrder.setRollovered(true);
/*     */             }
/* 468 */             entryOrder.setOrderId(orderMessage.getOrderId());
/* 469 */             entryOrder.setAmount(orderMessage.getAmount());
/* 470 */             entryOrder.setOpenPrice(orderMessage.getPriceClient());
/* 471 */             entryOrder.setSide(orderMessage.getSide() == OrderSide.BUY ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL);
/*     */ 
/* 473 */             entryOrder.setFromMerges(false);
/*     */           }
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void extendOrderHistoryRange(OrderHistoricalDataMutable mergedData, long mergeTime)
/*     */   {
/* 484 */     if (mergedData.getHistoryStart() > mergeTime) {
/* 485 */       mergedData.setHistoryStart(mergeTime);
/*     */     }
/* 487 */     if (mergedData.getHistoryEnd() < mergeTime)
/* 488 */       mergedData.setHistoryEnd(mergeTime);
/*     */   }
/*     */ 
/*     */   public synchronized Map<String, OrderHistoricalData> getOrdersForInstrument(Instrument instrument)
/*     */   {
/* 493 */     Map orders = this.ordersByInstrument[instrument.ordinal()];
/* 494 */     if (orders == null) {
/* 495 */       orders = new HashMap();
/* 496 */       this.ordersByInstrument[instrument.ordinal()] = orders;
/*     */     }
/* 498 */     return new HashMap(orders);
/*     */   }
/*     */ 
/*     */   public synchronized Collection<OrderHistoricalData> getOpenOrdersForInstrument(Instrument instrument, long from, long to) {
/* 502 */     Map orders = this.ordersByInstrument[instrument.ordinal()];
/* 503 */     if (orders == null) {
/* 504 */       orders = new HashMap();
/* 505 */       this.ordersByInstrument[instrument.ordinal()] = orders;
/*     */     }
/* 507 */     Collection result = new ArrayList();
/* 508 */     for (OrderHistoricalData data : orders.values()) {
/* 509 */       synchronized (data) {
/* 510 */         if (((data.getHistoryEnd() >= from) && (data.getHistoryStart() <= to) && (!data.isClosed())) || (data.getHistoryStart() == 9223372036854775807L) || ((!data.isClosed()) && (data.isOpened()) && ((data.getEntryOrder().getStopLossPrice().compareTo(BigDecimal.ZERO) >= 0) || (data.getEntryOrder().getTakeProfitPrice().compareTo(BigDecimal.ZERO) >= 0))))
/*     */         {
/* 513 */           result.add(data);
/*     */         }
/*     */       }
/*     */     }
/* 517 */     return result;
/*     */   }
/*     */ 
/*     */   public synchronized ExposureData getExposureForInstrument(Instrument instrument) {
/* 521 */     ExposureData exposure = this.exposures[instrument.ordinal()];
/* 522 */     if (exposure != null) {
/* 523 */       return exposure.clone();
/*     */     }
/* 525 */     return new ExposureData(instrument);
/*     */   }
/*     */ 
/*     */   public synchronized Collection<OrderHistoricalData> getAllOrders()
/*     */   {
/* 530 */     List orders = new ArrayList();
/* 531 */     for (Map map : this.ordersByInstrument) {
/* 532 */       if (map != null) {
/* 533 */         for (OrderHistoricalData orderHistoricalData : map.values()) {
/* 534 */           orders.add(orderHistoricalData);
/*     */         }
/*     */       }
/*     */     }
/* 538 */     return Collections.unmodifiableCollection(orders);
/*     */   }
/*     */ 
/*     */   protected synchronized void fireOrdersInvalidated(Instrument instrument) {
/* 542 */     List ordersListeners = this.ordersListenersByInstrument[instrument.ordinal()];
/* 543 */     if (ordersListeners == null) {
/* 544 */       ordersListeners = new ArrayList(1);
/* 545 */       this.ordersListenersByInstrument[instrument.ordinal()] = ordersListeners;
/*     */     }
/* 547 */     OrdersListener[] ordersListenersArr = (OrdersListener[])ordersListeners.toArray(new OrdersListener[ordersListeners.size()]);
/* 548 */     for (OrdersListener ordersListener : ordersListenersArr)
/* 549 */       ordersListener.ordersInvalidated(instrument);
/*     */   }
/*     */ 
/*     */   protected synchronized void fireOrderChange(Instrument instrument, OrderHistoricalData order)
/*     */   {
/* 554 */     List ordersListeners = this.ordersListenersByInstrument[instrument.ordinal()];
/* 555 */     if (ordersListeners == null) {
/* 556 */       ordersListeners = new ArrayList(1);
/* 557 */       this.ordersListenersByInstrument[instrument.ordinal()] = ordersListeners;
/*     */     }
/* 559 */     OrdersListener[] ordersListenersArr = (OrdersListener[])ordersListeners.toArray(new OrdersListener[ordersListeners.size()]);
/* 560 */     for (OrdersListener ordersListener : ordersListenersArr)
/* 561 */       ordersListener.orderChange(instrument, order);
/*     */   }
/*     */ 
/*     */   protected synchronized void fireOrderMerge(Instrument instrument, OrderHistoricalData resultingOrderData, List<OrderHistoricalData> mergedOrdersData)
/*     */   {
/* 566 */     mergedOrdersData = Collections.unmodifiableList(mergedOrdersData);
/* 567 */     List ordersListeners = this.ordersListenersByInstrument[instrument.ordinal()];
/* 568 */     if (ordersListeners == null) {
/* 569 */       ordersListeners = new ArrayList(1);
/* 570 */       this.ordersListenersByInstrument[instrument.ordinal()] = ordersListeners;
/*     */     }
/* 572 */     OrdersListener[] ordersListenersArr = (OrdersListener[])ordersListeners.toArray(new OrdersListener[ordersListeners.size()]);
/* 573 */     for (OrdersListener ordersListener : ordersListenersArr)
/* 574 */       ordersListener.orderMerge(instrument, resultingOrderData, mergedOrdersData);
/*     */   }
/*     */ 
/*     */   protected synchronized void fireOrderNew(Instrument instrument, OrderHistoricalData order)
/*     */   {
/* 579 */     List ordersListeners = this.ordersListenersByInstrument[instrument.ordinal()];
/* 580 */     if (ordersListeners == null) {
/* 581 */       ordersListeners = new ArrayList(1);
/* 582 */       this.ordersListenersByInstrument[instrument.ordinal()] = ordersListeners;
/*     */     }
/* 584 */     OrdersListener[] ordersListenersArr = (OrdersListener[])ordersListeners.toArray(new OrdersListener[ordersListeners.size()]);
/* 585 */     for (OrdersListener ordersListener : ordersListenersArr)
/* 586 */       ordersListener.newOrder(instrument, order);
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 591 */     for (int i = 0; i < this.ordersListenersByInstrument.length; i++) {
/* 592 */       List ordersListeners = this.ordersListenersByInstrument[i];
/* 593 */       if (ordersListeners != null) {
/* 594 */         ordersListeners.clear();
/*     */       }
/* 596 */       this.ordersListenersByInstrument[i] = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void recalculateAccountData()
/*     */   {
/* 602 */     double profLossOfOpenPositions = 0.0D;
/* 603 */     for (int i = 0; i < this.ordersByInstrument.length; i++) {
/* 604 */       double sumOfAmounts = 0.0D;
/* 605 */       double sumOfSecondaryAmounts = 0.0D;
/* 606 */       Map orders = this.ordersByInstrument[i];
/* 607 */       if ((orders == null) || (orders.isEmpty())) {
/*     */         continue;
/*     */       }
/* 610 */       Instrument instrument = INSTRUMENT_VALUES[i];
/* 611 */       for (OrderHistoricalData order : orders.values())
/* 612 */         synchronized (order) {
/* 613 */           if ((order.isOpened()) && (!order.isClosed())) {
/* 614 */             OrderHistoricalData.OpenData entryOrder = order.getEntryOrder();
/* 615 */             if (entryOrder.getSide().isLong()) {
/* 616 */               sumOfAmounts += entryOrder.getAmount().doubleValue();
/* 617 */               sumOfSecondaryAmounts -= entryOrder.getOpenPrice().doubleValue() * entryOrder.getAmount().doubleValue();
/*     */             } else {
/* 619 */               sumOfAmounts -= entryOrder.getAmount().doubleValue();
/* 620 */               sumOfSecondaryAmounts += entryOrder.getOpenPrice().doubleValue() * entryOrder.getAmount().doubleValue();
/*     */             }
/*     */           }
/*     */         }
/*     */       double profitLossSecondary;
/*     */       double profitLossSecondary;
/* 627 */       if ((sumOfAmounts > 0.001D) || (sumOfAmounts < 0.001D)) {
/* 628 */         ITick lastTick = getLastTick(instrument);
/* 629 */         if (lastTick == null)
/*     */         {
/* 631 */           this.calculatedAccount.setCalculatedEquity((0.0D / 0.0D));
/* 632 */           return;
/*     */         }
/* 634 */         profitLossSecondary = sumOfSecondaryAmounts + sumOfAmounts * (sumOfAmounts > 0.0D ? lastTick.getBid() : lastTick.getAsk());
/*     */       } else {
/* 636 */         profitLossSecondary = sumOfSecondaryAmounts;
/*     */       }
/*     */ 
/* 639 */       double convertedProfLoss = convert(profitLossSecondary, instrument.getSecondaryCurrency(), this.calculatedAccount.getCurrency(), null);
/* 640 */       if (!Double.isNaN(convertedProfLoss)) {
/* 641 */         profLossOfOpenPositions += StratUtils.roundHalfEven(convertedProfLoss, 2);
/*     */       }
/*     */     }
/*     */ 
/* 645 */     double equity = StratUtils.roundHalfEven(this.calculatedAccount.getBaseEquity() + this.closedCorrection + profLossOfOpenPositions, 2);
/*     */ 
/* 647 */     this.calculatedAccount.setCalculatedEquity(equity);
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getOrderInstruments() {
/* 651 */     Set orderInstruments = new HashSet();
/* 652 */     for (int i = 0; i < this.ordersByInstrument.length; i++) {
/* 653 */       Map orders = this.ordersByInstrument[i];
/* 654 */       if ((orders == null) || (orders.isEmpty())) {
/*     */         continue;
/*     */       }
/* 657 */       Instrument instrument = INSTRUMENT_VALUES[i];
/* 658 */       orderInstruments.add(instrument);
/*     */     }
/* 660 */     return orderInstruments;
/*     */   }
/*     */ 
/*     */   protected abstract ITick getLastTick(Instrument paramInstrument);
/*     */ 
/*     */   public abstract BigDecimal convert(BigDecimal paramBigDecimal, Currency paramCurrency1, Currency paramCurrency2, OfferSide paramOfferSide);
/*     */ 
/*     */   public abstract double convert(double paramDouble, Currency paramCurrency1, Currency paramCurrency2, OfferSide paramOfferSide);
/*     */ 
/*     */   public void clear()
/*     */   {
/* 674 */     int i = 0; for (int j = Instrument.values().length; i < j; i++) {
/* 675 */       if (this.ordersByInstrument[i] != null) {
/* 676 */         this.ordersByInstrument[i].clear();
/*     */       }
/* 678 */       this.exposures[i] = null;
/*     */     }
/* 680 */     this.calculatedAccount = new CalculatedAccount();
/*     */ 
/* 682 */     this.closedCorrection = 0.0D;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  43 */     LOGGER = LoggerFactory.getLogger(AbstractOrdersProvider.class);
/*  44 */     INSTRUMENT_VALUES = Instrument.values();
/*     */   }
/*     */ 
/*     */   private static class MergeNode
/*     */   {
/*     */     public String orderGroupId;
/*     */     public long createdDate;
/*     */     public OrderHistoricalDataMutable orderData;
/*     */     public MergeNode parentNode;
/*     */     public List<MergeNode> childNodes;
/*     */     public int childIndex;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.orders.AbstractOrdersProvider
 * JD-Core Version:    0.6.0
 */