/*     */ package com.dukascopy.dds2.greed.api;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.calc.PriceUtil;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*     */ import com.dukascopy.dds2.greed.actions.MergePositionsAction;
/*     */ import com.dukascopy.dds2.greed.actions.OrderEntryAction;
/*     */ import com.dukascopy.dds2.greed.actions.PostMessageAction;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.model.OrderGoodTillType;
/*     */ import com.dukascopy.dds2.greed.model.StopOrderType;
/*     */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrderFactory
/*     */ {
/*  40 */   private static Logger LOGGER = LoggerFactory.getLogger(OrderFactory.class);
/*     */ 
/*     */   public static void placeBidOffer(Object source, String instrument, OfferSide side, BigDecimal amount, BigDecimal price, OrderGoodTillType gtcType, Long timeToLive, String externalId, String strategyId, String comment)
/*     */   {
/*  57 */     String[] currencies = instrument.split("/");
/*     */ 
/*  60 */     List orders = new ArrayList();
/*  61 */     OrderGroupMessage orderGroup = new OrderGroupMessage();
/*     */ 
/*  63 */     orderGroup.setTimestamp(new Date());
/*  64 */     orderGroup.setInstrument(instrument);
/*     */ 
/*  66 */     OrderMessage order = new OrderMessage();
/*     */ 
/*  68 */     setExternalSystemId(externalId, order);
/*  69 */     setStrategyId(strategyId, order);
/*  70 */     setCommentTag(comment, order);
/*     */ 
/*  72 */     order.setPlaceOffer(Boolean.valueOf(true));
/*  73 */     order.setSide(side == OfferSide.BID ? OrderSide.BUY : OrderSide.SELL);
/*  74 */     order.setOrderDirection(OrderDirection.OPEN);
/*  75 */     order.setAmount(new Money(amount, Currency.getInstance(currencies[0])));
/*  76 */     order.setPriceClient(new Money(price.toPlainString(), currencies[0]));
/*  77 */     order.setOrderGroupId(orderGroup.getOrderGroupId());
/*  78 */     order.setInstrument(instrument);
/*     */ 
/*  80 */     if (-1L != timeToLive.longValue()) {
/*  81 */       order.setExecTimeoutMillis(OrderMessageUtils.convertGoodTill2ExecTimeoutMIllis(gtcType, timeToLive));
/*     */     }
/*     */ 
/*  84 */     orders.add(order);
/*  85 */     orderGroup.setOrders(orders);
/*     */ 
/*  87 */     AppActionEvent orderEntryAction = new OrderEntryAction(source, false, orderGroup);
/*  88 */     GreedContext.publishEvent(orderEntryAction);
/*     */   }
/*     */ 
/*     */   public static void updateBidOffer(Object source, String orderGroupId, OrderGoodTillType gtcType, Long timeToLive, BigDecimal price)
/*     */   {
/*  96 */     OrderGroupMessage ogm = OrderMessageUtils.getOrderGroupById(orderGroupId);
/*  97 */     OrderMessage order = ogm.getOpeningOrder();
/*  98 */     if (-1L != timeToLive.longValue())
/*  99 */       order.setExecTimeoutMillis(OrderMessageUtils.convertGoodTill2ExecTimeoutMIllis(gtcType, timeToLive));
/*     */     else {
/* 101 */       order.remove("execTimeoutMillis");
/*     */     }
/* 103 */     String[] currencies = order.getInstrument().split("/");
/* 104 */     if (price != null) {
/* 105 */       order.setPriceClient(new Money(price.toPlainString(), currencies[0]));
/*     */     }
/* 107 */     AppActionEvent orderEntryAction = new OrderEntryAction(source, false, ogm);
/* 108 */     GreedContext.publishEvent(orderEntryAction);
/*     */   }
/*     */ 
/*     */   public static EntryOrderResultCode addStopLoss(Object source, String orderGroupId, String openingOrderId, BigDecimal priceParam, StopDirection stopDirection, BigDecimal trailingStep)
/*     */   {
/* 116 */     return addSlOrTp(source, orderGroupId, openingOrderId, priceParam, StopOrderType.STOP_LOSS, stopDirection, trailingStep);
/*     */   }
/*     */ 
/*     */   public static EntryOrderResultCode addTakeProfit(Object source, String orderGroupId, String openingOrderId, BigDecimal priceParam, StopDirection stopDirection) {
/* 120 */     return addSlOrTp(source, orderGroupId, openingOrderId, priceParam, StopOrderType.TAKE_PROFIT, stopDirection, null);
/*     */   }
/*     */ 
/*     */   private static EntryOrderResultCode addSlOrTp(Object source, String orderGroupId, String openingOrderId, BigDecimal priceParam, StopOrderType stopOrderType, StopDirection stopDirection, BigDecimal trailingStep)
/*     */   {
/* 126 */     EntryOrderResultCode resultCode = EntryOrderResultCode.OK;
/*     */ 
/* 130 */     OrderGroupMessage ogm = OrderMessageUtils.getOrderGroupById(orderGroupId);
/*     */ 
/* 134 */     if (ogm == null) {
/* 135 */       return EntryOrderResultCode.POSITION_NOT_FOUND;
/*     */     }
/* 137 */     Position position = ogm.calculatePositionModified();
/*     */ 
/* 139 */     Money amount = calcAmount(ogm, position);
/* 140 */     PositionSide positionSide = calcPositionSide(ogm, position);
/*     */ 
/* 142 */     OrderMessage stopOrder = null;
/*     */ 
/* 145 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$model$StopOrderType[stopOrderType.ordinal()]) {
/*     */     case 1:
/* 147 */       return EntryOrderResultCode.ORDER_TYPE_NOT_SUPPORTED;
/*     */     case 2:
/* 149 */       stopOrder = ogm.getStopLossOrder();
/* 150 */       if (stopDirection != null) break;
/* 151 */       stopDirection = positionSide.equals(PositionSide.LONG) ? StopDirection.BID_LESS : StopDirection.ASK_GREATER; break;
/*     */     case 3:
/* 154 */       stopOrder = ogm.getTakeProfitOrder();
/* 155 */       if (stopDirection != null) break;
/* 156 */       stopDirection = positionSide.equals(PositionSide.LONG) ? StopDirection.BID_GREATER : StopDirection.ASK_LESS; break;
/*     */     default:
/* 159 */       return EntryOrderResultCode.ORDER_TYPE_NOT_SUPPORTED;
/*     */     }
/*     */ 
/* 165 */     String currencyPrimary = ogm.getCurrencyPrimary();
/*     */ 
/* 167 */     boolean isNewOrder = false;
/*     */ 
/* 170 */     if (null == stopOrder) {
/* 171 */       isNewOrder = true;
/* 172 */       stopOrder = createNewStopOrder(ogm);
/* 173 */       stopOrder.setStopDirection(stopDirection);
/* 174 */       stopOrder.setSide(positionSide.equals(PositionSide.LONG) ? OrderSide.SELL : OrderSide.BUY);
/*     */     }
/*     */ 
/* 178 */     stopOrder.setAmount(amount);
/*     */ 
/* 180 */     stopOrder.setPriceStop(new Money(priceParam.toPlainString(), currencyPrimary));
/*     */ 
/* 182 */     stopOrder.setStopDirection(stopDirection);
/* 183 */     stopOrder.setSide(positionSide.equals(PositionSide.LONG) ? OrderSide.SELL : OrderSide.BUY);
/*     */ 
/* 185 */     String[] currencies = ogm.getInstrument().split("/");
/* 186 */     if ((stopOrder.isTakeProfit()) && ((StopDirection.BID_GREATER.equals(stopOrder.getStopDirection())) || (StopDirection.ASK_LESS.equals(stopOrder.getStopDirection()))))
/*     */     {
/* 190 */       stopOrder.setPriceTrailingLimit(new Money(BigDecimal.ZERO, Currency.getInstance(currencies[1])));
/*     */     }
/* 192 */     if ((stopOrder.isStopLoss()) && (trailingStep != null) && (trailingStep.compareTo(BigDecimal.ZERO) > 0)) {
/* 193 */       BigDecimal trailingPrice = trailingStep.multiply(BigDecimal.valueOf(Instrument.fromString(ogm.getInstrument()).getPipValue()));
/* 194 */       stopOrder.setPriceLimit(new Money(trailingPrice, Currency.getInstance(currencies[1])));
/*     */     } else {
/* 196 */       stopOrder.remove("priceLimit");
/*     */     }
/*     */ 
/* 199 */     stopOrder.setIfdParentOrderId(openingOrderId);
/*     */     OrderGroupMessage newGroup;
/*     */     try {
/* 204 */       newGroup = OrderMessageUtils.copyOrderGroup(ogm);
/*     */     } catch (ParseException e) {
/* 206 */       LOGGER.error(e.getMessage(), e);
/* 207 */       return EntryOrderResultCode.SYSTEM_ERROR;
/*     */     }
/*     */ 
/* 210 */     if (isNewOrder) {
/* 211 */       List orders = ogm.getOrders();
/* 212 */       orders.add(stopOrder);
/* 213 */       newGroup.setOrders(orders);
/*     */     } else {
/* 215 */       newGroup.replaceOrder(stopOrder);
/*     */     }
/*     */ 
/* 219 */     List orders = ogm.getOrders();
/* 220 */     orders.add(stopOrder);
/*     */ 
/* 223 */     fireOrderGroupOpenAction(source, newGroup);
/*     */ 
/* 225 */     return resultCode;
/*     */   }
/*     */ 
/*     */   private static Money calcAmount(OrderGroupMessage ogm, Position position)
/*     */   {
/*     */     Money amount;
/*     */     Money amount;
/* 231 */     if (position == null)
/* 232 */       amount = new Money("0", ogm.getCurrencyPrimary());
/*     */     else {
/* 234 */       amount = position.getAmount();
/*     */     }
/*     */ 
/* 237 */     if (amount.getValue().compareTo(BigDecimal.ZERO) <= 0)
/*     */     {
/* 239 */       OrderMessage opening = ogm.getOpeningOrder();
/* 240 */       if (opening != null) {
/* 241 */         amount = opening.getAmount();
/*     */       }
/*     */     }
/* 244 */     return amount;
/*     */   }
/*     */ 
/*     */   private static PositionSide calcPositionSide(OrderGroupMessage ogm, Position position)
/*     */   {
/*     */     PositionSide positionSide;
/*     */     PositionSide positionSide;
/* 249 */     if (null != position) {
/* 250 */       positionSide = position.getPositionSide();
/*     */     } else {
/* 252 */       Money buyAmount = new Money("0", ogm.getCurrencyPrimary());
/* 253 */       Money sellAmount = new Money("0", ogm.getCurrencyPrimary());
/* 254 */       for (OrderMessage order : ogm.getOrders())
/* 255 */         if (OrderDirection.OPEN == order.getOrderDirection())
/* 256 */           if (order.getSide() == OrderSide.BUY)
/* 257 */             buyAmount = buyAmount.add(order.getAmount().abs());
/* 258 */           else if (order.getSide() == OrderSide.SELL)
/* 259 */             sellAmount = sellAmount.add(order.getAmount().abs());
/*     */       PositionSide positionSide;
/* 263 */       if (buyAmount.compareTo(sellAmount) > 0)
/* 264 */         positionSide = PositionSide.LONG;
/*     */       else {
/* 266 */         positionSide = PositionSide.SHORT;
/*     */       }
/*     */     }
/* 269 */     return positionSide; } 
/* 273 */   private static OrderMessage createNewStopOrder(OrderGroupMessage ogm) { OrderMessage order = new OrderMessage();
/* 274 */     order.setInstrument(ogm.getInstrument());
/* 275 */     order.setOrderDirection(OrderDirection.CLOSE);
/* 276 */     order.setOrderGroupId(ogm.getOrderGroupId());
/*     */     Money amount;
/*     */     try { Position position = ogm.calculatePositionModified();
/*     */       Money amount;
/* 280 */       if (position != null)
/* 281 */         amount = position.getAmount();
/*     */       else
/* 283 */         amount = new Money("0", ogm.getCurrencyPrimary());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 287 */       LOGGER.error(e.getMessage(), e);
/* 288 */       amount = new Money("0", ogm.getCurrencyPrimary());
/*     */     }
/*     */ 
/* 292 */     if (amount.getValue().compareTo(BigDecimal.ZERO) <= 0)
/*     */     {
/* 294 */       OrderMessage opening = ogm.getOpeningOrder();
/* 295 */       if (opening != null) {
/* 296 */         amount = opening.getAmount();
/*     */       }
/*     */     }
/*     */ 
/* 300 */     order.setAmount(amount);
/* 301 */     order.setOrderState(OrderState.CREATED);
/* 302 */     return order;
/*     */   }
/*     */ 
/*     */   public static EntryOrderResultCode modifyEntryOrder(Object source, String orderGroupId, StopOrderType orderType, String orderId, String slippageParam, BigDecimal amountParam, OrderSide sideParam, String instrumentParam, BigDecimal priceParam, StopDirection openIfStopDirection, String tag)
/*     */   {
/* 310 */     if ((StopOrderType.PART_CLOSE.equals(orderType)) || (orderType == null)) {
/* 311 */       return EntryOrderResultCode.ORDER_TYPE_NOT_SUPPORTED;
/*     */     }
/*     */ 
/* 314 */     EntryOrderResultCode result = EntryOrderResultCode.OK;
/* 315 */     result = validateEntryParams(amountParam, instrumentParam, openIfStopDirection);
/*     */ 
/* 317 */     if (!result.equals(EntryOrderResultCode.OK)) {
/* 318 */       return result;
/*     */     }
/*     */ 
/* 321 */     OrderGroupMessage ogm = OrderMessageUtils.getOrderGroupById(orderGroupId);
/*     */ 
/* 323 */     if (ogm == null) {
/* 324 */       return EntryOrderResultCode.POSITION_NOT_FOUND;
/*     */     }
/* 326 */     OrderMessage stopOrder = null;
/* 327 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$model$StopOrderType[orderType.ordinal()]) {
/*     */     case 1:
/* 329 */       stopOrder = ogm.getOpenIfOrder();
/* 330 */       break;
/*     */     case 2:
/* 332 */       stopOrder = ogm.getStopLossOrder();
/* 333 */       break;
/*     */     case 3:
/* 335 */       stopOrder = ogm.getTakeProfitOrder();
/* 336 */       break;
/*     */     }
/*     */ 
/* 343 */     if (stopOrder == null) {
/* 344 */       return EntryOrderResultCode.ORDER_NOT_FOUND;
/*     */     }
/*     */ 
/* 347 */     String[] currencies = instrumentParam.split("/");
/* 348 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 349 */     OfferSide offerSide = sideParam.equals(OrderSide.BUY) ? OfferSide.ASK : OfferSide.BID;
/* 350 */     CurrencyOffer bestOffer = marketView.getBestOffer(instrumentParam, offerSide);
/* 351 */     Money bestPrice = bestOffer.getPrice();
/*     */ 
/* 354 */     if (slippageParam != null) {
/* 355 */       String trailingLimitString = slippageParam;
/* 356 */       BigDecimal trailingLimitValue = new BigDecimal(trailingLimitString).multiply(PriceUtil.pipValue(bestPrice.getValue()));
/* 357 */       Money trailingLimit = new Money(trailingLimitValue, Currency.getInstance(currencies[1]));
/* 358 */       stopOrder.setPriceTrailingLimit(trailingLimit);
/*     */     }
/*     */ 
/* 363 */     stopOrder.setPriceClient(bestPrice);
/* 364 */     Money amount = new Money(amountParam, Currency.getInstance(currencies[0]));
/* 365 */     stopOrder.setAmount(amount);
/*     */ 
/* 369 */     stopOrder.setPriceStop(new Money(priceParam, Currency.getInstance(currencies[0])));
/*     */ 
/* 373 */     fireOrderGroupOpenAction(source, ogm);
/*     */ 
/* 375 */     return result;
/*     */   }
/*     */ 
/*     */   public static EntryOrderResultCode createEntryOrder(Object source, String slippageParam, BigDecimal amountParam, OrderSide sideParam, String instrumentParam, BigDecimal bestMarketOrderPrice, BigDecimal entryPriceParam, StopDirection openIfStopDirection, BigDecimal stopLossPriceParam, StopDirection stopLossStopDirection, BigDecimal takeProfitPriceParam, StopDirection takeProfitStopDirection, String externalSystemId, String strategyId, String comment)
/*     */   {
/* 386 */     EntryOrderResultCode result = validateEntryParams(amountParam, instrumentParam, openIfStopDirection);
/*     */ 
/* 388 */     if (!result.equals(EntryOrderResultCode.OK)) {
/* 389 */       return result;
/*     */     }
/* 391 */     OrderGroupMessage ogm = new OrderGroupMessage();
/*     */     try
/*     */     {
/* 394 */       ogm.setTimestamp(new Date());
/* 395 */       ogm.setInstrument(instrumentParam);
/* 396 */       List orderList = createEntryOrderAndConditionals(slippageParam, amountParam, sideParam, instrumentParam, bestMarketOrderPrice, entryPriceParam, openIfStopDirection, stopLossPriceParam, stopLossStopDirection, takeProfitPriceParam, takeProfitStopDirection, externalSystemId, strategyId, comment);
/* 397 */       ogm.setOrders(orderList);
/*     */ 
/* 399 */       fireOrderGroupOpenAction(source, ogm);
/*     */     }
/*     */     catch (NumberFormatException ex) {
/* 402 */       result = EntryOrderResultCode.NUMBER_FORMAT_ERROR;
/*     */     }
/*     */ 
/* 405 */     return result;
/*     */   }
/*     */ 
/*     */   private static EntryOrderResultCode validateEntryParams(BigDecimal amountParam, String instrumentParam, StopDirection openIfStopDirection)
/*     */   {
/* 414 */     if (null == instrumentParam) {
/* 415 */       return EntryOrderResultCode.NO_INSTRUMENT;
/*     */     }
/* 417 */     if (null == amountParam) {
/* 418 */       return EntryOrderResultCode.NO_AMOUNT;
/*     */     }
/* 420 */     if (new Double(amountParam.doubleValue()).doubleValue() < LotAmountChanger.getMinTradableAmount().doubleValue()) {
/* 421 */       return EntryOrderResultCode.AMOUNT_TOO_SMALL;
/*     */     }
/* 423 */     return EntryOrderResultCode.OK;
/*     */   }
/*     */ 
/*     */   public static List<OrderMessage> createEntryOrderAndConditionals(String slippageParam, BigDecimal amountParam, OrderSide sideParam, String instrumentParam, BigDecimal bestMarketOrderPrice, BigDecimal entryPriceParam, StopDirection openIfStopDirection, BigDecimal stopLossPriceParam, StopDirection stopLossStopDirection, BigDecimal takeProfitPriceParam, StopDirection takeProfitStopDirection, String externalSystemId, String strategyId, String comment)
/*     */   {
/* 433 */     List result = new ArrayList();
/*     */ 
/* 437 */     String[] currencies = instrumentParam.split("/");
/*     */ 
/* 440 */     Money bestPrice = null;
/* 441 */     if ((bestMarketOrderPrice == null) || (bestMarketOrderPrice.compareTo(BigDecimal.ZERO) == 0)) {
/* 442 */       MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 443 */       OfferSide offerSide = sideParam.equals(OrderSide.BUY) ? OfferSide.ASK : OfferSide.BID;
/* 444 */       CurrencyOffer bestOffer = marketView.getBestOffer(instrumentParam, offerSide);
/* 445 */       bestPrice = bestOffer.getPrice();
/*     */     } else {
/* 447 */       bestPrice = new Money(bestMarketOrderPrice, Money.getCurrency(currencies[0]));
/*     */     }
/*     */ 
/* 451 */     Money amount = new Money(amountParam.multiply(GuiUtilsAndConstants.ONE_MILLION), Money.getCurrency(currencies[0]));
/*     */ 
/* 453 */     OrderMessage openingOrder = createOpeningOrder(slippageParam, amount.getValue(), sideParam, instrumentParam, bestPrice, externalSystemId, strategyId, comment);
/*     */ 
/* 458 */     if (openIfStopDirection != null) {
/* 459 */       openingOrder.setPriceStop(new Money(entryPriceParam, Money.getCurrency(currencies[1])));
/* 460 */       if (openIfStopDirection.equals(StopDirection.ASK_EQUALS)) {
/* 461 */         openingOrder.setStopDirection(StopDirection.ASK_LESS);
/* 462 */         openingOrder.setPriceTrailingLimit(new Money(BigDecimal.ZERO, Money.getCurrency(currencies[1])));
/* 463 */       } else if (openIfStopDirection.equals(StopDirection.BID_EQUALS)) {
/* 464 */         openingOrder.setStopDirection(StopDirection.BID_GREATER);
/* 465 */         openingOrder.setPriceTrailingLimit(new Money(BigDecimal.ZERO, Money.getCurrency(currencies[1])));
/*     */       } else {
/* 467 */         openingOrder.setStopDirection(openIfStopDirection);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 472 */     result.add(openingOrder);
/*     */ 
/* 475 */     OrderSide closeSide = sideParam.equals(OrderSide.BUY) ? OrderSide.SELL : OrderSide.BUY;
/*     */ 
/* 487 */     if (stopLossPriceParam != null) {
/* 488 */       OrderMessage closingOrder = createStopLoss(sideParam, stopLossPriceParam, stopLossStopDirection, currencies, amount, closeSide);
/* 489 */       result.add(closingOrder);
/*     */     }
/*     */ 
/* 492 */     if (takeProfitPriceParam != null) {
/* 493 */       OrderMessage closingOrder = createTakeProfit(sideParam, takeProfitPriceParam, takeProfitStopDirection, currencies, amount, closeSide);
/* 494 */       result.add(closingOrder);
/*     */     }
/*     */ 
/* 497 */     return result;
/*     */   }
/*     */ 
/*     */   private static OrderMessage createTakeProfit(OrderSide sideParam, BigDecimal takeProfitPriceParam, StopDirection takeProfitStopDirection, String[] currencies, Money amount, OrderSide closeSide) {
/* 501 */     OrderMessage closingOrder = new OrderMessage();
/* 502 */     closingOrder.setAmount(amount);
/* 503 */     closingOrder.setInstrument(currencies[0] + "/" + currencies[1]);
/* 504 */     closingOrder.setOrderDirection(OrderDirection.CLOSE);
/* 505 */     closingOrder.setSide(closeSide);
/* 506 */     closingOrder.setPriceStop(new Money(takeProfitPriceParam, Currency.getInstance(currencies[1])));
/* 507 */     if (null == takeProfitStopDirection) {
/* 508 */       closingOrder.setStopDirection(sideParam.equals(OrderSide.BUY) ? StopDirection.BID_GREATER : StopDirection.ASK_LESS);
/* 509 */       closingOrder.setPriceTrailingLimit(new Money(BigDecimal.ZERO, Currency.getInstance(currencies[1])));
/*     */     }
/* 511 */     else if (takeProfitStopDirection.equals(StopDirection.ASK_EQUALS)) {
/* 512 */       closingOrder.setStopDirection(StopDirection.ASK_LESS);
/* 513 */       closingOrder.setPriceTrailingLimit(new Money(BigDecimal.ZERO, Currency.getInstance(currencies[1])));
/* 514 */     } else if (takeProfitStopDirection.equals(StopDirection.BID_EQUALS)) {
/* 515 */       closingOrder.setStopDirection(StopDirection.BID_GREATER);
/* 516 */       closingOrder.setPriceTrailingLimit(new Money(BigDecimal.ZERO, Currency.getInstance(currencies[1])));
/*     */     }
/*     */     else {
/* 519 */       closingOrder.setStopDirection(takeProfitStopDirection);
/*     */     }
/*     */ 
/* 522 */     return closingOrder;
/*     */   }
/*     */ 
/*     */   private static OrderMessage createStopLoss(OrderSide sideParam, BigDecimal stopLossPriceParam, StopDirection stopLossStopDirection, String[] currencies, Money amount, OrderSide closeSide) {
/* 526 */     OrderMessage closingOrder = new OrderMessage();
/* 527 */     closingOrder.setAmount(amount);
/* 528 */     closingOrder.setInstrument(currencies[0] + "/" + currencies[1]);
/* 529 */     closingOrder.setOrderDirection(OrderDirection.CLOSE);
/* 530 */     closingOrder.setSide(closeSide);
/* 531 */     closingOrder.setPriceStop(new Money(stopLossPriceParam, Currency.getInstance(currencies[1])));
/* 532 */     if (null == stopLossStopDirection)
/* 533 */       closingOrder.setStopDirection(sideParam.equals(OrderSide.BUY) ? StopDirection.BID_LESS : StopDirection.ASK_GREATER);
/*     */     else
/* 535 */       closingOrder.setStopDirection(stopLossStopDirection);
/* 536 */     return closingOrder;
/*     */   }
/*     */ 
/*     */   private static OrderMessage createOpeningOrder(String slippageParam, BigDecimal amountParam, OrderSide sideParam, String instrumentParam, Money bestPrice, String externalSystemId, String strategyId, String comment)
/*     */   {
/* 541 */     OrderMessage openingOrder = new OrderMessage();
/* 542 */     String[] currencies = instrumentParam.split("/");
/* 543 */     openingOrder.setInstrument(instrumentParam);
/* 544 */     openingOrder.setOrderDirection(OrderDirection.OPEN);
/* 545 */     openingOrder.setSide(sideParam);
/* 546 */     openingOrder.setPriceClient(bestPrice);
/* 547 */     Money amount = new Money(amountParam, Currency.getInstance(currencies[0]));
/* 548 */     openingOrder.setAmount(amount);
/*     */ 
/* 550 */     if (slippageParam != null) {
/* 551 */       openingOrder.setPriceTrailingLimit(slippageParam);
/*     */     }
/*     */ 
/* 554 */     setExternalSystemId(externalSystemId, openingOrder);
/* 555 */     setStrategyId(strategyId, openingOrder);
/* 556 */     setCommentTag(comment, openingOrder);
/*     */ 
/* 558 */     return openingOrder;
/*     */   }
/*     */ 
/*     */   private static void setStrategyId(String strategyId, OrderMessage orderToTag)
/*     */   {
/* 565 */     if (strategyId != null)
/* 566 */       orderToTag.setStrategySysId(strategyId);
/*     */   }
/*     */ 
/*     */   private static void setExternalSystemId(String externalSysId, OrderMessage orderToTag)
/*     */   {
/* 573 */     if (externalSysId != null)
/* 574 */       orderToTag.setExternalSysId(externalSysId);
/*     */   }
/*     */ 
/*     */   private static void setCommentTag(String tag, OrderMessage orderToTag)
/*     */   {
/* 581 */     if (tag != null)
/* 582 */       orderToTag.setTag(tag);
/*     */   }
/*     */ 
/*     */   public static void quickie(Object source, String slippageParam, BigDecimal amountParam, BigDecimal priceParam, OrderSide sideParam, String instrumentParam, String externalSystemId, String strategyId, String comment)
/*     */   {
/* 589 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 590 */     OfferSide offerSide = sideParam.equals(OrderSide.BUY) ? OfferSide.ASK : OfferSide.BID;
/* 591 */     CurrencyOffer bestOffer = marketView.getBestOffer(instrumentParam, offerSide);
/*     */     Money bestPrice;
/* 593 */     if (priceParam.doubleValue() > 0.0D) {
/* 594 */       bestPrice = new Money(priceParam, Money.getCurrency("USD"));
/*     */     }
/*     */     else
/*     */     {
/*     */       Money bestPrice;
/* 596 */       if ((bestOffer != null) && (bestOffer.getPrice() != null)) {
/* 597 */         bestPrice = bestOffer.getPrice();
/*     */       } else {
/* 599 */         Notification notification = new Notification(null, "No liquidity for " + sideParam.asString() + " " + instrumentParam + "!");
/* 600 */         notification.setPriority("WARNING");
/* 601 */         PostMessageAction post = new PostMessageAction(source, notification);
/* 602 */         GreedContext.publishEvent(post);
/* 603 */         return;
/*     */       }
/*     */     }
/*     */     Money bestPrice;
/* 607 */     OrderGroupMessage ogm = new OrderGroupMessage();
/* 608 */     ogm.setTimestamp(new Date());
/* 609 */     ogm.setInstrument(instrumentParam);
/*     */ 
/* 611 */     OrderMessage openingOrder = createOpeningOrder(slippageParam, amountParam, sideParam, instrumentParam, bestPrice, externalSystemId, strategyId, comment);
/*     */ 
/* 613 */     List orders = new ArrayList();
/* 614 */     orders.add(openingOrder);
/* 615 */     ogm.setOrders(orders);
/*     */ 
/* 617 */     fireOrderGroupOpenAction(source, ogm);
/*     */   }
/*     */ 
/*     */   private static void fireOrderGroupOpenAction(Object source, OrderGroupMessage ogm)
/*     */   {
/* 625 */     OrderEntryAction orderEntryAction = new OrderEntryAction(source, ogm);
/* 626 */     GreedContext.publishEvent(orderEntryAction);
/*     */   }
/*     */ 
/*     */   public static MergeValidationResultCode mergePositions(List<String> mergeOrderGroupIdList)
/*     */   {
/* 639 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/*     */ 
/* 641 */     String assumedMergedOrderGroupInstrument = null;
/* 642 */     for (String selectedOrderGroupId : mergeOrderGroupIdList) {
/* 643 */       ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 644 */       OrderGroupMessage orderGroup = gui.getOrdersPanel().getOrderGroup(selectedOrderGroupId);
/* 645 */       String selectedOrderGroupInstrument = orderGroup.getInstrument();
/* 646 */       if (orderGroup == null) {
/* 647 */         LOGGER.debug("contains conditionals (or executing/pending) : " + selectedOrderGroupId);
/*     */ 
/* 649 */         continue;
/*     */       }
/* 651 */       if (containsConditionals(orderGroup)) {
/* 652 */         LOGGER.debug("contains conditionals (or executing/pending) : " + selectedOrderGroupId);
/* 653 */         return MergeValidationResultCode.CONDITIONALS_PRESENT;
/*     */       }
/* 655 */       if (null == assumedMergedOrderGroupInstrument) {
/* 656 */         assumedMergedOrderGroupInstrument = selectedOrderGroupInstrument;
/*     */       }
/*     */ 
/* 659 */       if (!assumedMergedOrderGroupInstrument.equals(selectedOrderGroupInstrument)) {
/* 660 */         LOGGER.debug("instruments do not match: " + assumedMergedOrderGroupInstrument + " vs. " + selectedOrderGroupInstrument);
/* 661 */         return MergeValidationResultCode.NO_INSTRUMENT_MATCH;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 666 */     MergePositionsAction mergeAction = new MergePositionsAction(OrderFactory.class, mergeOrderGroupIdList);
/* 667 */     GreedContext.publishEvent(mergeAction);
/*     */ 
/* 669 */     return MergeValidationResultCode.OK;
/*     */   }
/*     */ 
/*     */   private static boolean containsConditionals(OrderGroupMessage orderGroup)
/*     */   {
/* 674 */     String orderGroupId = orderGroup.getOrderGroupId();
/* 675 */     for (OrderMessage o : orderGroup.getOrders())
/*     */     {
/* 677 */       if (OrderState.EXECUTING.equals(o.getOrderState())) {
/* 678 */         LOGGER.debug("existing order in EXECUTING status");
/* 679 */         return true;
/*     */       }
/*     */ 
/* 682 */       if (OrderState.PENDING.equals(o.getOrderState())) {
/* 683 */         LOGGER.debug("existing order in PENDING status");
/* 684 */         return true;
/*     */       }
/* 686 */       if ((o.isPlaceOffer()) && (!OrderState.FILLED.equals(o.getOrderState())) && (!OrderState.CANCELLED.equals(o.getOrderState())))
/*     */       {
/* 690 */         LOGGER.debug("MERGE: CC: offer filled//cancelled " + orderGroupId);
/* 691 */         return true;
/*     */       }
/* 693 */       if ((OrderDirection.OPEN.equals(o.getOrderDirection())) && (!OrderState.FILLED.equals(o.getOrderState())) && (!OrderState.REJECTED.equals(o.getOrderState())))
/*     */       {
/* 697 */         LOGGER.debug("MERGE: CC: open/ !filled/rejected   " + orderGroupId + " state:" + o.getOrderState());
/* 698 */         return true;
/*     */       }
/*     */     }
/* 701 */     return false;
/*     */   }
/*     */ 
/*     */   public static enum MergeValidationResultCode
/*     */   {
/*  44 */     NO_INSTRUMENT_MATCH, CONDITIONALS_PRESENT, OK;
/*     */   }
/*     */ 
/*     */   public static enum EntryOrderResultCode
/*     */   {
/*  42 */     NO_INSTRUMENT, NO_AMOUNT, NO_ENTRY_CONDITION, AMOUNT_TOO_SMALL, NUMBER_FORMAT_ERROR, ORDER_TYPE_NOT_SUPPORTED, OK, POSITION_NOT_FOUND, ORDER_NOT_FOUND, SLTP_ALREADY_EXISTS, SYSTEM_ERROR;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.api.OrderFactory
 * JD-Core Version:    0.6.0
 */