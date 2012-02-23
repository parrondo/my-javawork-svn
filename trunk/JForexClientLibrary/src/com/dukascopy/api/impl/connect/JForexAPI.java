/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class JForexAPI
/*     */ {
/*     */   public static OrderMessage cancelOrder(String strategyId, String externalSysId, Instrument instrument, String groupId, String orderId, String externalIP, String internalIP, String sessionID)
/*     */   {
/*  34 */     return cancelOrder(strategyId, externalSysId, instrument, groupId, orderId, null, externalIP, internalIP, sessionID);
/*     */   }
/*     */ 
/*     */   public static OrderMessage cancelOrder(String strategyId, String externalSysId, Instrument instrument, String groupId, String orderId, String parentOrderId, String externalIP, String internalIP, String sessionID)
/*     */   {
/*  39 */     if ((ObjectUtils.isNullOrEmpty(groupId)) || ((ObjectUtils.isNullOrEmpty(orderId)) && (ObjectUtils.isNullOrEmpty(parentOrderId))) || (ObjectUtils.isNullOrEmpty(externalSysId)) || (ObjectUtils.isNullOrEmpty(instrument)))
/*     */     {
/*  41 */       throw new NullPointerException();
/*     */     }
/*     */ 
/*  44 */     OrderMessage order = new OrderMessage();
/*  45 */     order.setOrderId(orderId);
/*  46 */     order.setOrderState(OrderState.CANCELLED);
/*  47 */     order.setOrderGroupId(groupId);
/*  48 */     order.setParentOrderId(parentOrderId);
/*  49 */     order.setStrategySysId(strategyId);
/*  50 */     order.setExternalSysId(externalSysId);
/*  51 */     order.setInstrument(instrument.toString());
/*  52 */     order.setExternalIp(externalIP);
/*  53 */     order.setInternalIp(internalIP);
/*  54 */     order.setSessionId(sessionID);
/*     */ 
/*  56 */     return order;
/*     */   }
/*     */ 
/*     */   public static OrderMessage modifyPrice(String strategyId, String externalSysId, Instrument instrument, String groupId, String orderId, double price, String externalIP, String internalIP, String sessionID)
/*     */   {
/*  62 */     OrderMessage order = new OrderMessage();
/*     */ 
/*  64 */     order.setStrategySysId(strategyId);
/*  65 */     order.setExternalSysId(externalSysId);
/*  66 */     order.setPriceStop(new StringBuilder().append(price).append("").toString());
/*  67 */     order.setOrderGroupId(groupId);
/*  68 */     order.setOrderId(orderId);
/*  69 */     order.setOrderState(OrderState.PENDING);
/*  70 */     order.setInstrument(instrument.toString());
/*  71 */     order.setExternalIp(externalIP);
/*  72 */     order.setInternalIp(internalIP);
/*  73 */     order.setSessionId(sessionID);
/*     */ 
/*  75 */     return order;
/*     */   }
/*     */ 
/*     */   public static OrderMessage modifyStopLoss(String strategyId, String externalSysId, Instrument instrument, String groupId, String orderId, boolean isParentOrderLong, double price, OfferSide side, double trailingPriceAbsolute, String externalIP, String internalIP, String sessionID)
/*     */   {
/*  82 */     OrderMessage order = new OrderMessage();
/*     */ 
/*  84 */     order.setStrategySysId(strategyId);
/*  85 */     order.setExternalSysId(externalSysId);
/*  86 */     order.setInstrument(instrument.toString());
/*  87 */     order.setPriceStop(new StringBuilder().append(price).append("").toString());
/*  88 */     order.setOrderGroupId(groupId);
/*  89 */     order.setOrderId(orderId);
/*  90 */     order.setOrderState(OrderState.PENDING);
/*  91 */     order.setPriceLimit(Double.toString(trailingPriceAbsolute));
/*     */     StopDirection stopDirection;
/*     */     StopDirection stopDirection;
/*  93 */     if (isParentOrderLong)
/*     */     {
/*     */       StopDirection stopDirection;
/*  94 */       if ((side == null) || (side == OfferSide.BID))
/*  95 */         stopDirection = StopDirection.BID_LESS;
/*     */       else
/*  97 */         stopDirection = StopDirection.ASK_LESS;
/*     */     }
/*     */     else
/*     */     {
/*     */       StopDirection stopDirection;
/* 100 */       if ((side == null) || (side == OfferSide.ASK))
/* 101 */         stopDirection = StopDirection.ASK_GREATER;
/*     */       else {
/* 103 */         stopDirection = StopDirection.BID_GREATER;
/*     */       }
/*     */     }
/* 106 */     order.setStopDirection(stopDirection);
/* 107 */     order.setExternalIp(externalIP);
/* 108 */     order.setInternalIp(internalIP);
/* 109 */     order.setSessionId(sessionID);
/*     */ 
/* 111 */     return order;
/*     */   }
/*     */ 
/*     */   public static OrderGroupMessage modifyOrderGroup(OrderGroupMessage parentMsg, double price, double stoploss, double takeprofit, long expiration, boolean isParentOrderlong, String externalIP, String internalIP, String sessionID)
/*     */     throws CloneNotSupportedException
/*     */   {
/* 117 */     String strategyId = parentMsg.getOpeningOrder().getStrategyId();
/* 118 */     String openingOrderId = parentMsg.getOpeningOrder().getOrderId();
/* 119 */     List orders = new ArrayList();
/* 120 */     OrderGroupMessage orderGroup = (OrderGroupMessage)parentMsg.clone();
/*     */ 
/* 122 */     OrderMessage order = (OrderMessage)parentMsg.getOpeningOrder().clone();
/* 123 */     order.setExternalSysId(orderGroup.getExternalSysId());
/* 124 */     order.setInstrument(orderGroup.getInstrument());
/* 125 */     order.setExternalIp(externalIP);
/* 126 */     order.setInternalIp(internalIP);
/* 127 */     order.setSessionId(sessionID);
/* 128 */     if (price > 0.0D) {
/* 129 */       order.setPriceStop(new StringBuilder().append(price).append("").toString());
/* 130 */       orders.add(order);
/*     */     }
/* 132 */     if (stoploss > 0.0D) {
/* 133 */       OrderMessage slOrder = null;
/* 134 */       if (parentMsg.getStopLossOrder() != null) {
/* 135 */         slOrder = (OrderMessage)parentMsg.getStopLossOrder().clone();
/* 136 */         slOrder.put("priceStop", new StringBuilder().append(price).append("").toString());
/* 137 */         slOrder.put("amount", order.getAmount().getValue().divide(BigDecimal.valueOf(1000000L), 6, 6).toPlainString());
/*     */       } else {
/* 139 */         slOrder = createStopLoss(strategyId, orderGroup.getExternalSysId(), orderGroup.getOrderGroupId(), openingOrderId, Instrument.fromString(orderGroup.getInstrument()), stoploss, order.getAmount().getValue().doubleValue() / 1000000.0D, isParentOrderlong, null, externalIP, internalIP, sessionID);
/*     */       }
/*     */ 
/* 146 */       orders.add(slOrder);
/*     */     }
/* 148 */     if (takeprofit > 0.0D) {
/* 149 */       OrderMessage tpOrder = null;
/* 150 */       if (parentMsg.getStopLossOrder() != null) {
/* 151 */         tpOrder = parentMsg.getStopLossOrder();
/* 152 */         tpOrder.put("priceStop", new StringBuilder().append(price).append("").toString());
/* 153 */         tpOrder.put("amount", order.getAmount().getValue().divide(BigDecimal.valueOf(1000000L), 6, 6).toPlainString());
/*     */       } else {
/* 155 */         tpOrder = createTakeProfit(strategyId, orderGroup.getExternalSysId(), orderGroup.getOrderGroupId(), openingOrderId, Instrument.fromString(orderGroup.getInstrument()), takeprofit, order.getAmount().getValue().doubleValue() / 1000000.0D, isParentOrderlong, externalIP, internalIP, sessionID);
/*     */       }
/*     */ 
/* 162 */       orders.add(tpOrder);
/*     */     }
/* 164 */     orderGroup.setOrders(orders);
/* 165 */     return orderGroup;
/*     */   }
/*     */ 
/*     */   public static OrderMessage modifyBidOfferPrice(Instrument instrument, String externalSysId, String groupId, String orderId, double price, String externalIP, String internalIP, String sessionID) {
/* 169 */     OrderMessage order = new OrderMessage();
/*     */ 
/* 171 */     order.put("priceClient", BigDecimal.valueOf(price).toPlainString());
/* 172 */     order.setExternalSysId(externalSysId);
/* 173 */     order.setOrderGroupId(groupId);
/* 174 */     order.setOrderId(orderId);
/* 175 */     order.setPlaceOffer(Boolean.valueOf(true));
/* 176 */     order.setOrderState(OrderState.EXECUTING);
/* 177 */     order.setInstrument(instrument.toString());
/* 178 */     order.setExternalIp(externalIP);
/* 179 */     order.setInternalIp(internalIP);
/* 180 */     order.setSessionId(sessionID);
/*     */ 
/* 182 */     return order;
/*     */   }
/*     */ 
/*     */   public static OrderMessage modifyAmount(Instrument instrument, String externalSysId, String groupId, String orderId, double amount, String externalIP, String internalIP, String sessionID) {
/* 186 */     OrderMessage order = new OrderMessage();
/* 187 */     order.put("amount", BigDecimal.valueOf(amount).toPlainString());
/* 188 */     order.setExternalSysId(externalSysId);
/* 189 */     order.setOrderGroupId(groupId);
/* 190 */     order.setOrderId(orderId);
/* 191 */     order.setOrderState(OrderState.PENDING);
/* 192 */     order.setInstrument(instrument.toString());
/* 193 */     order.setExternalIp(externalIP);
/* 194 */     order.setInternalIp(internalIP);
/* 195 */     order.setSessionId(sessionID);
/*     */ 
/* 197 */     return order;
/*     */   }
/*     */ 
/*     */   public static OrderMessage addTakeProfit(String strategyId, String externalSysId, String groupId, String openingOrderId, Instrument instrument, double price, double amount, boolean isParentOrderLong, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 205 */     OrderMessage order = createTakeProfit(strategyId, externalSysId, groupId, openingOrderId, instrument, price, amount, isParentOrderLong, externalIP, internalIP, sessionID);
/*     */ 
/* 208 */     return order;
/*     */   }
/*     */ 
/*     */   public static OrderMessage addStopLoss(String strategyId, String externalSysId, String groupId, String openingOrderId, Instrument instrument, double price, double amount, double trailingPriceAbsolute, boolean isParentOrderLong, OfferSide side, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 216 */     OrderMessage order = createStopLoss(strategyId, externalSysId, groupId, openingOrderId, instrument, price, amount, isParentOrderLong, side, externalIP, internalIP, sessionID);
/*     */ 
/* 218 */     if (trailingPriceAbsolute > 0.0D) {
/* 219 */       order.setPriceLimit(new StringBuilder().append(trailingPriceAbsolute).append("").toString());
/*     */     }
/*     */ 
/* 222 */     return order;
/*     */   }
/*     */ 
/*     */   public static OrderMessage modifyGTT(Instrument instrument, String externalSysId, String groupId, String orderId, long goodTilltime, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 227 */     OrderMessage order = new OrderMessage();
/* 228 */     order.setExternalSysId(externalSysId);
/* 229 */     order.setInstrument(instrument.toString());
/* 230 */     order.setOrderGroupId(groupId);
/* 231 */     order.setOrderId(orderId);
/* 232 */     order.setOrderState(OrderState.EXECUTING);
/* 233 */     if (-1L != goodTilltime)
/* 234 */       order.setExecTimeoutMillis(Long.valueOf(goodTilltime));
/*     */     else {
/* 236 */       order.setExecTimeoutMillis(Long.valueOf(System.currentTimeMillis() + 3153600000000L));
/*     */     }
/* 238 */     order.setExternalIp(externalIP);
/* 239 */     order.setInternalIp(internalIP);
/* 240 */     order.setSessionId(sessionID);
/*     */ 
/* 242 */     return order;
/*     */   }
/*     */ 
/*     */   public static OrderMessage closePosition(IEngine.OrderCommand openOrderCommand, Instrument instrument, String orderGroupId, String strategyKey, String label, double amount, double price, double slippage, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 249 */     return generatePositionClosingOrder(openOrderCommand, instrument, orderGroupId, strategyKey, label, amount, price, slippage, externalIP, internalIP, sessionID);
/*     */   }
/*     */ 
/*     */   public static OrderMessage closePosition(OrderMessage order, double amount, double price, double slippage, String externalIP, String internalIP, String sessionID)
/*     */     throws CloneNotSupportedException
/*     */   {
/* 255 */     return generatePositionClosingOrder(order, amount, price, slippage, externalIP, internalIP, sessionID);
/*     */   }
/*     */ 
/*     */   public static OrderGroupMessage massClose(List<OrderMessage> closingOrders)
/*     */   {
/* 260 */     OrderGroupMessage massCloseGroup = new OrderGroupMessage();
/* 261 */     massCloseGroup.setOrders(closingOrders);
/* 262 */     String id = generateLabel();
/* 263 */     massCloseGroup.setExternalSysId(id);
/* 264 */     return massCloseGroup;
/*     */   }
/*     */ 
/*     */   public static OrderMessage generatePositionClosingOrder(IEngine.OrderCommand openOrderCommand, Instrument instrument, String orderGroupId, String strategyKey, String label, double amount, double price, double slippage, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 271 */     OrderSide closeSide = openOrderCommand.isLong() ? OrderSide.SELL : OrderSide.BUY;
/* 272 */     OrderMessage result = new OrderMessage();
/* 273 */     result.put("amount", BigDecimal.valueOf(amount).toPlainString());
/* 274 */     result.setInstrument(instrument.toString());
/* 275 */     result.setOrderDirection(OrderDirection.CLOSE);
/* 276 */     result.setOrderGroupId(orderGroupId);
/* 277 */     result.setOrderState(OrderState.CREATED);
/* 278 */     result.setSide(closeSide);
/* 279 */     result.setExternalSysId(label);
/* 280 */     result.setStrategySysId(strategyKey);
/* 281 */     if (price > 0.0D)
/* 282 */       result.put("priceClient", BigDecimal.valueOf(price).toPlainString());
/*     */     else {
/* 284 */       result.setPriceClient(null);
/*     */     }
/*     */ 
/* 287 */     if (slippage >= 0.0D)
/* 288 */       result.put("trailingLimit", new StringBuilder().append(slippage).append("").toString());
/*     */     else {
/* 290 */       result.put("trailingLimit", null);
/*     */     }
/* 292 */     result.setExternalIp(externalIP);
/* 293 */     result.setInternalIp(internalIP);
/* 294 */     result.setSessionId(sessionID);
/* 295 */     return result;
/*     */   }
/*     */ 
/*     */   public static OrderMessage generatePositionClosingOrder(OrderMessage order, double amount, double price, double slippage, String externalIP, String internalIP, String sessionID) throws CloneNotSupportedException
/*     */   {
/* 300 */     OrderSide closeSide = order.getSide() == OrderSide.BUY ? OrderSide.SELL : OrderSide.BUY;
/* 301 */     order.getType();
/* 302 */     OrderMessage result = (OrderMessage)order.clone();
/* 303 */     if (amount > 0.0D)
/* 304 */       result.put("amount", BigDecimal.valueOf(amount).toPlainString());
/*     */     else {
/* 306 */       result.put("amount", order.getAmount());
/*     */     }
/* 308 */     result.setInstrument(order.getInstrument());
/* 309 */     result.setOrderDirection(OrderDirection.CLOSE);
/* 310 */     result.setOrderGroupId(order.getOrderGroupId());
/* 311 */     result.setOrderState(OrderState.CREATED);
/* 312 */     result.setSide(closeSide);
/* 313 */     result.setExternalSysId(order.getExternalSysId());
/*     */ 
/* 315 */     if (price > 0.0D)
/* 316 */       result.put("priceClient", new StringBuilder().append(price).append("").toString());
/*     */     else {
/* 318 */       result.setPriceClient((Money)null);
/*     */     }
/*     */ 
/* 321 */     if (slippage >= 0.0D)
/* 322 */       result.put("trailingLimit", new StringBuilder().append(slippage).append("").toString());
/*     */     else {
/* 324 */       result.put("trailingLimit", null);
/*     */     }
/* 326 */     result.setExternalIp(externalIP);
/* 327 */     result.setInternalIp(internalIP);
/* 328 */     result.setSessionId(sessionID);
/* 329 */     return result;
/*     */   }
/*     */ 
/*     */   public static MergePositionsMessage merge(String strategyId, String externalSysId, Set<String> mergedGroupsIDs)
/*     */   {
/* 334 */     StringBuilder delimSeparatedPosIdList = new StringBuilder();
/* 335 */     for (String orderGroupId : mergedGroupsIDs) {
/* 336 */       if (0 != delimSeparatedPosIdList.length()) {
/* 337 */         delimSeparatedPosIdList.append(";");
/*     */       }
/* 339 */       delimSeparatedPosIdList.append(orderGroupId);
/*     */     }
/*     */ 
/* 342 */     MergePositionsMessage mergeRequest = new MergePositionsMessage(delimSeparatedPosIdList.toString());
/* 343 */     if (externalSysId != null)
/* 344 */       mergeRequest.setExternalSysId(externalSysId);
/*     */     else {
/* 346 */       mergeRequest.setExternalSysId(generateLabel());
/*     */     }
/* 348 */     if (strategyId != null) {
/* 349 */       mergeRequest.setStrategySysId(strategyId);
/*     */     }
/* 351 */     return mergeRequest;
/*     */   }
/*     */ 
/*     */   public static String generateLabel() {
/* 355 */     String label = "JF";
/*     */ 
/* 357 */     Random random = new Random();
/* 358 */     while (label.length() < 10) {
/* 359 */       label = new StringBuilder().append(label).append(Integer.toString(random.nextInt(100000000), 32)).toString();
/*     */     }
/* 361 */     label = label.substring(0, 9);
/* 362 */     label = label.toLowerCase();
/* 363 */     return label;
/*     */   }
/*     */ 
/*     */   public static OrderGroupMessage submitOrder(String strategyId, String externalSysId, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, String comment, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 372 */     OrderGroupMessage orderGroup = createGroup(externalSysId, instrument, null);
/*     */ 
/* 374 */     OrderMessage openingOrder = createOpeningOrder(externalSysId, strategyId, instrument, orderCommand, amount, price, slippage, comment, externalIP, internalIP, sessionID);
/*     */ 
/* 377 */     StopDirection stopDirection = getStopDirection(orderCommand);
/* 378 */     if (stopDirection != null) {
/* 379 */       openingOrder.put("priceStop", new StringBuilder().append(price).append("").toString());
/* 380 */       openingOrder.setStopDirection(stopDirection);
/*     */     }
/*     */ 
/* 383 */     List orders = new ArrayList();
/*     */ 
/* 385 */     orders.add(openingOrder);
/*     */ 
/* 387 */     if (stopLossPrice > 0.0D)
/*     */     {
/* 389 */       OrderMessage slOrder = createStopLoss(strategyId, externalSysId, null, null, instrument, stopLossPrice, amount, orderCommand.isLong(), null, externalIP, internalIP, sessionID);
/*     */ 
/* 391 */       orders.add(slOrder);
/*     */     }
/* 393 */     if (takeProfitPrice > 0.0D)
/*     */     {
/* 395 */       OrderMessage tpOrder = createTakeProfit(strategyId, externalSysId, null, null, instrument, takeProfitPrice, amount, orderCommand.isLong(), externalIP, internalIP, sessionID);
/*     */ 
/* 397 */       orders.add(tpOrder);
/*     */     }
/*     */ 
/* 400 */     orderGroup.setOrders(orders);
/*     */ 
/* 403 */     return orderGroup;
/*     */   }
/*     */ 
/*     */   public static OrderGroupMessage placeBidOffer(String strategyId, String externalSysId, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double stopLossPrice, double takeProfitPrice, long goodTillTime, String comment, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 412 */     OrderGroupMessage orderGroup = createGroup(externalSysId, instrument, null);
/*     */ 
/* 414 */     OrderMessage openingOrder = createOpeningOrder(externalSysId, strategyId, instrument, orderCommand, amount, price, -1.0D, comment, externalIP, internalIP, sessionID);
/*     */ 
/* 417 */     openingOrder.setPlaceOffer(Boolean.valueOf(true));
/*     */ 
/* 419 */     if (goodTillTime > 0L) {
/* 420 */       openingOrder.setExecTimeoutMillis(Long.valueOf(goodTillTime));
/*     */     }
/*     */ 
/* 423 */     List orders = new ArrayList();
/* 424 */     orders.add(openingOrder);
/*     */ 
/* 426 */     if (stopLossPrice > 0.0D)
/*     */     {
/* 428 */       OrderMessage slOrder = createStopLoss(strategyId, externalSysId, null, null, instrument, stopLossPrice, amount, orderCommand.isLong(), null, externalIP, internalIP, sessionID);
/*     */ 
/* 430 */       orders.add(slOrder);
/*     */     }
/* 432 */     if (takeProfitPrice > 0.0D)
/*     */     {
/* 434 */       OrderMessage tpOrder = createTakeProfit(strategyId, externalSysId, null, null, instrument, takeProfitPrice, amount, orderCommand.isLong(), externalIP, internalIP, sessionID);
/*     */ 
/* 436 */       orders.add(tpOrder);
/*     */     }
/*     */ 
/* 439 */     orderGroup.setOrders(orders);
/* 440 */     return orderGroup;
/*     */   }
/*     */ 
/*     */   private static OrderMessage createStopLoss(String strategyId, String externalSysId, String groupId, String openingOrderId, Instrument instrument, double price, double amount, boolean isParentOrderlong, OfferSide stopLossSide, String externalIP, String internalIP, String sessionID)
/*     */   {
/*     */     StopDirection stopDirection;
/*     */     StopDirection stopDirection;
/* 448 */     if (isParentOrderlong)
/*     */     {
/*     */       StopDirection stopDirection;
/* 449 */       if ((stopLossSide == null) || (stopLossSide == OfferSide.BID))
/* 450 */         stopDirection = StopDirection.BID_LESS;
/*     */       else
/* 452 */         stopDirection = StopDirection.ASK_LESS;
/*     */     }
/*     */     else
/*     */     {
/*     */       StopDirection stopDirection;
/* 455 */       if ((stopLossSide == null) || (stopLossSide == OfferSide.ASK))
/* 456 */         stopDirection = StopDirection.ASK_GREATER;
/*     */       else {
/* 458 */         stopDirection = StopDirection.BID_GREATER;
/*     */       }
/*     */     }
/* 461 */     OrderSide slSide = isParentOrderlong ? OrderSide.SELL : OrderSide.BUY;
/* 462 */     OrderMessage closingOrder = createSLTPBase(strategyId, externalSysId, groupId, openingOrderId, instrument, price, amount, slSide, stopDirection, externalIP, internalIP, sessionID);
/*     */ 
/* 464 */     return closingOrder;
/*     */   }
/*     */ 
/*     */   private static OrderMessage createTakeProfit(String strategyId, String externalSysId, String groupId, String openingOrderId, Instrument instrument, double price, double amount, boolean isParentOrderlong, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 470 */     StopDirection stopDirection = null;
/* 471 */     if (isParentOrderlong)
/* 472 */       stopDirection = StopDirection.BID_GREATER;
/*     */     else {
/* 474 */       stopDirection = StopDirection.ASK_LESS;
/*     */     }
/* 476 */     OrderSide tpSide = isParentOrderlong ? OrderSide.SELL : OrderSide.BUY;
/* 477 */     OrderMessage closingOrder = createSLTPBase(strategyId, externalSysId, groupId, openingOrderId, instrument, price, amount, tpSide, stopDirection, externalIP, internalIP, sessionID);
/*     */ 
/* 479 */     closingOrder.put("trailingLimit", "0");
/* 480 */     return closingOrder;
/*     */   }
/*     */ 
/*     */   private static OrderMessage createSLTPBase(String strategyId, String externalSysId, String groupId, String openingOrderId, Instrument instrument, double price, double amount, OrderSide orderSide, StopDirection stopDirection, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 486 */     OrderMessage closingOrder = new OrderMessage();
/*     */ 
/* 488 */     closingOrder.setStrategySysId(strategyId);
/* 489 */     closingOrder.setExternalSysId(externalSysId);
/* 490 */     if (groupId != null) {
/* 491 */       closingOrder.setOrderGroupId(groupId);
/*     */     }
/* 493 */     if (openingOrderId != null) {
/* 494 */       closingOrder.setIfdParentOrderId(openingOrderId);
/*     */     }
/* 496 */     closingOrder.setOrderState(OrderState.CREATED);
/* 497 */     closingOrder.setInstrument(instrument.toString());
/* 498 */     closingOrder.setOrderDirection(OrderDirection.CLOSE);
/* 499 */     closingOrder.setSide(orderSide);
/*     */ 
/* 501 */     closingOrder.put("priceStop", new StringBuilder().append(price).append("").toString());
/* 502 */     closingOrder.put("amount", BigDecimal.valueOf(amount).toPlainString());
/*     */ 
/* 504 */     closingOrder.setStopDirection(stopDirection);
/* 505 */     closingOrder.setExternalIp(externalIP);
/* 506 */     closingOrder.setInternalIp(internalIP);
/* 507 */     closingOrder.setSessionId(sessionID);
/*     */ 
/* 509 */     return closingOrder;
/*     */   }
/*     */ 
/*     */   public static StopDirection getStopDirection(IEngine.OrderCommand orderCommand) {
/* 513 */     switch (1.$SwitchMap$com$dukascopy$api$IEngine$OrderCommand[orderCommand.ordinal()]) {
/*     */     case 1:
/* 515 */       return StopDirection.ASK_LESS;
/*     */     case 2:
/* 517 */       return StopDirection.BID_LESS;
/*     */     case 3:
/* 519 */       return StopDirection.ASK_GREATER;
/*     */     case 4:
/* 521 */       return StopDirection.BID_GREATER;
/*     */     case 5:
/* 523 */       return StopDirection.BID_GREATER;
/*     */     case 6:
/* 525 */       return StopDirection.ASK_GREATER;
/*     */     case 7:
/* 527 */       return StopDirection.BID_LESS;
/*     */     case 8:
/* 529 */       return StopDirection.ASK_LESS;
/*     */     }
/* 531 */     return null;
/*     */   }
/*     */ 
/*     */   private static OrderGroupMessage createGroup(String externalSysId, Instrument instrument, String groupId)
/*     */   {
/* 537 */     OrderGroupMessage orderGroup = new OrderGroupMessage();
/*     */ 
/* 539 */     orderGroup.setTimestamp(new Date());
/* 540 */     orderGroup.setInstrument(instrument.toString());
/* 541 */     if (externalSysId != null) {
/* 542 */       orderGroup.setExternalSysId(externalSysId);
/*     */     }
/* 544 */     if (groupId != null) {
/* 545 */       orderGroup.setOrderGroupId(groupId);
/*     */     }
/*     */ 
/* 548 */     return orderGroup;
/*     */   }
/*     */ 
/*     */   private static OrderMessage createOpeningOrder(String externalSysId, String strategyId, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage, String comment, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 556 */     OrderMessage openingOrder = new OrderMessage();
/* 557 */     openingOrder.setExternalSysId(externalSysId);
/* 558 */     openingOrder.setStrategySysId(strategyId);
/* 559 */     openingOrder.setTag(comment);
/* 560 */     openingOrder.setOrderState(OrderState.CREATED);
/* 561 */     openingOrder.setOrderDirection(OrderDirection.OPEN);
/* 562 */     openingOrder.put("amount", BigDecimal.valueOf(amount).toPlainString());
/* 563 */     if ((orderCommand == IEngine.OrderCommand.PLACE_BID) || (orderCommand == IEngine.OrderCommand.PLACE_OFFER)) {
/* 564 */       openingOrder.setPriceClient(new Money(BigDecimal.valueOf(price), instrument.getPrimaryCurrency()));
/*     */     }
/*     */     else
/*     */     {
/*     */       double priceClient;
/*     */       double priceClient;
/* 567 */       if (orderCommand.isLong())
/* 568 */         priceClient = FeedDataProvider.getDefaultInstance().getLastAsk(instrument);
/*     */       else {
/* 570 */         priceClient = FeedDataProvider.getDefaultInstance().getLastBid(instrument);
/*     */       }
/* 572 */       if (!Double.isNaN(priceClient)) {
/* 573 */         openingOrder.setPriceClient(new Money(BigDecimal.valueOf(priceClient), instrument.getPrimaryCurrency()));
/*     */       }
/* 575 */       if ((orderCommand == IEngine.OrderCommand.BUY) || (orderCommand == IEngine.OrderCommand.SELL))
/*     */       {
/* 577 */         if (price > 0.0D) {
/* 578 */           if (Double.isNaN(priceClient)) {
/* 579 */             throw new IllegalStateException("Instrument not tradable");
/*     */           }
/* 581 */           BigDecimal priceClientBD = BigDecimal.valueOf(priceClient);
/* 582 */           BigDecimal worstPrice = BigDecimal.valueOf(price);
/* 583 */           if ((!Double.isNaN(slippage)) && (slippage > 0.0D)) {
/* 584 */             if (orderCommand.isLong())
/* 585 */               worstPrice = worstPrice.add(BigDecimal.valueOf(slippage));
/*     */             else {
/* 587 */               worstPrice = worstPrice.subtract(BigDecimal.valueOf(slippage));
/*     */             }
/*     */           }
/* 590 */           if ((orderCommand.isLong()) && (worstPrice.compareTo(priceClientBD) > 0))
/* 591 */             slippage = worstPrice.subtract(priceClientBD).doubleValue();
/* 592 */           else if ((!orderCommand.isLong()) && (worstPrice.compareTo(priceClientBD) < 0))
/* 593 */             slippage = priceClientBD.subtract(worstPrice).doubleValue();
/*     */           else {
/* 595 */             openingOrder.setPriceClient(new Money(BigDecimal.valueOf(price), instrument.getPrimaryCurrency()));
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 601 */       if ((!Double.isNaN(slippage)) && (slippage >= 0.0D))
/* 602 */         openingOrder.put("trailingLimit", new StringBuilder().append(slippage).append("").toString());
/*     */       else {
/* 604 */         openingOrder.put("trailingLimit", null);
/*     */       }
/*     */     }
/* 607 */     openingOrder.setSide(orderCommand.isLong() ? OrderSide.BUY : OrderSide.SELL);
/* 608 */     openingOrder.setInstrument(instrument.toString());
/* 609 */     openingOrder.setExternalIp(externalIP);
/* 610 */     openingOrder.setInternalIp(internalIP);
/* 611 */     openingOrder.setSessionId(sessionID);
/* 612 */     return openingOrder;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.JForexAPI
 * JD-Core Version:    0.6.0
 */