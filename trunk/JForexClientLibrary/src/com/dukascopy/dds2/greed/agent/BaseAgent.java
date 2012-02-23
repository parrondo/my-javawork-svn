/*     */ package com.dukascopy.dds2.greed.agent;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.agent.history.HTick;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.objects.AccountInfo;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.objects.Market;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.objects.Order;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class BaseAgent
/*     */   implements IDDSAgent
/*     */ {
/*  37 */   private static final Logger LOGGER = LoggerFactory.getLogger(BaseAgent.class);
/*     */ 
/*  39 */   static DecimalFormat df = new DecimalFormat("#0.######");
/*     */ 
/*  55 */   protected Date lastTickTime = null;
/*     */ 
/* 442 */   protected int tempStopLossPips = 0;
/*     */ 
/* 444 */   protected int tempTakeProfitPips = 0;
/*     */ 
/* 593 */   public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
/*     */ 
/* 600 */   protected double slipageControl = -1.0D;
/*     */ 
/* 607 */   private Map<String, Object> properties = new HashMap();
/*     */ 
/*     */   public static String format(double dd)
/*     */   {
/*  45 */     return df.format(dd);
/*     */   }
/*     */ 
/*     */   public String normalizePriceString(String price) {
/*  49 */     if (price.length() > 7) {
/*  50 */       price = price.substring(0, 7);
/*     */     }
/*  52 */     return price;
/*     */   }
/*     */ 
/*     */   public Collection<Order> getOrders(boolean filterWithNoTag, String filterSymbol)
/*     */   {
/* 101 */     Set set = new HashSet();
/*     */ 
/* 108 */     for (Position position : getPositionsListImpl(filterWithNoTag, filterSymbol)) {
/* 109 */       Order order = new Order(position);
/*     */ 
/* 111 */       order.setAgent(this);
/* 112 */       set.add(order);
/*     */     }
/* 114 */     for (OrderGroupMessage groupMessage : getEntryOrdersListImpl(filterWithNoTag, filterSymbol)) {
/* 115 */       Order order = new Order(groupMessage);
/* 116 */       order.setAgent(this);
/* 117 */       set.add(order);
/*     */     }
/*     */ 
/* 133 */     return set;
/*     */   }
/*     */   protected abstract List<OrderGroupMessage> getEntryOrdersListImpl(boolean paramBoolean, String paramString);
/*     */ 
/*     */   protected abstract List<Position> getPositionsListImpl(boolean paramBoolean, String paramString);
/*     */ 
/*     */   public int submitOrder(String label, String symbol, int cmd, double amount, double price, int stopLoss, int takeProfit) throws AgentException {
/* 142 */     return submitOrder(label, symbol, cmd, amount, price, stopLoss, takeProfit, "");
/*     */   }
/*     */ 
/*     */   public int submitOrder(String label, String symbol, int cmd, double amount, double price, int stopLoss, int takeProfit, String comment)
/*     */     throws AgentException
/*     */   {
/* 153 */     if ((label != null) && (label.length() > 8)) {
/* 154 */       return -4;
/*     */     }
/* 156 */     if (label == null) {
/* 157 */       return -4;
/*     */     }
/* 159 */     checkLabelUniqueness(label);
/* 160 */     symbol = StratUtils.normalizeSymbol(symbol);
/* 161 */     validateAmount(amount);
/* 162 */     checkLiquidity(symbol);
/*     */ 
/* 164 */     price = StratUtils.round(price, 7);
/*     */ 
/* 166 */     int positionId = -99;
/*     */ 
/* 168 */     if ((cmd == 0) || (cmd == 1))
/*     */     {
/* 176 */       positionId = submitMarketOrder(label, symbol, cmd, amount, price, comment);
/* 177 */       if ((stopLoss > 0) && (positionId > 0)) {
/* 178 */         submitStop(label, 0, stopLoss);
/*     */       }
/* 180 */       if ((takeProfit > 0) && (positionId > 0))
/* 181 */         submitStop(label, 1, takeProfit);
/*     */     }
/*     */     else
/*     */     {
/* 185 */       if (price == 0.0D) {
/* 186 */         return -10;
/*     */       }
/* 188 */       positionId = submitEntryOrder(label, symbol, cmd, amount, price, stopLoss, takeProfit, comment);
/*     */     }
/*     */ 
/* 192 */     return positionId;
/*     */   }
/*     */ 
/*     */   public int submitStop(String label, int command, int pips) throws AgentException {
/* 196 */     return submitStop(label, command, pips, 0.0D);
/*     */   }
/*     */ 
/*     */   public int submitStop(String label, int command, int pips, double price)
/*     */     throws AgentException
/*     */   {
/* 208 */     int rc = -13;
/*     */ 
/* 210 */     String groupId = getPositionIdByLabel(label);
/*     */ 
/* 213 */     boolean isEntryOrder = false;
/* 214 */     if (groupId == null) {
/* 215 */       OrderGroupMessage entry = getEntryOrderByLabelImpl(label);
/* 216 */       if (entry != null) {
/* 217 */         groupId = entry.getOrderGroupId();
/* 218 */         if (groupId != null) {
/* 219 */           isEntryOrder = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 224 */     if (groupId != null)
/*     */     {
/* 226 */       OrderGroupMessage orderGroup = getOrderGroupByIdImpl(groupId);
/* 227 */       if (orderGroup != null)
/*     */       {
/* 229 */         String stopOrderTypeo = null;
/* 230 */         if ((command == 0) || (command == 2) || (command == 3))
/* 231 */           stopOrderTypeo = "STOP_LOSS";
/* 232 */         else if ((command == 1) || (command == 4) || (command == 5))
/* 233 */           stopOrderTypeo = "TAKE_PROFIT";
/*     */         else {
/* 235 */           rc = -10;
/*     */         }
/*     */ 
/* 238 */         if (stopOrderTypeo != null) {
/* 239 */           if ((pips > 0) || (price > 0.0D))
/*     */           {
/* 241 */             BigDecimal openPrice = null;
/* 242 */             BigDecimal onePip = null;
/* 243 */             if (orderGroup.getOpeningOrder().getPriceClient() != null) {
/* 244 */               openPrice = orderGroup.getOpeningOrder().getPriceClient().getValue();
/* 245 */               onePip = BigDecimal.valueOf(pipValue(openPrice.doubleValue()));
/*     */             }
/*     */ 
/* 249 */             BigDecimal priceStop = null;
/* 250 */             if (orderGroup.getOpeningOrder().getPriceStop() != null) {
/* 251 */               priceStop = orderGroup.getOpeningOrder().getPriceStop().getValue();
/* 252 */               onePip = BigDecimal.valueOf(pipValue(priceStop.doubleValue()));
/*     */             }
/*     */ 
/* 255 */             BigDecimal stopPrice = null;
/* 256 */             if (pips > 0) {
/* 257 */               if (((stopOrderTypeo.equals("STOP_LOSS")) && (orderGroup.getOpeningOrder().getSide() == OrderSide.BUY)) || ((stopOrderTypeo.equals("TAKE_PROFIT")) && (orderGroup.getOpeningOrder().getSide() == OrderSide.SELL))) {
/* 258 */                 if (isEntryOrder)
/* 259 */                   stopPrice = priceStop.subtract(onePip.multiply(BigDecimal.valueOf(pips)));
/*     */                 else {
/* 261 */                   stopPrice = openPrice.subtract(onePip.multiply(BigDecimal.valueOf(pips)));
/*     */                 }
/*     */               }
/* 264 */               if (((stopOrderTypeo.equals("STOP_LOSS")) && (orderGroup.getOpeningOrder().getSide() == OrderSide.SELL)) || ((stopOrderTypeo.equals("TAKE_PROFIT")) && (orderGroup.getOpeningOrder().getSide() == OrderSide.BUY))) {
/* 265 */                 if (isEntryOrder)
/* 266 */                   stopPrice = priceStop.add(onePip.multiply(BigDecimal.valueOf(pips)));
/*     */                 else
/* 268 */                   stopPrice = openPrice.add(onePip.multiply(BigDecimal.valueOf(pips)));
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 273 */               stopPrice = BigDecimal.valueOf(price);
/*     */             }
/* 275 */             if (stopPrice.compareTo(BigDecimal.ZERO) <= 0) {
/* 276 */               throw new AgentException(-15);
/*     */             }
/* 278 */             String normailzedPriceStopStr = normalizePriceString(stopPrice.toPlainString());
/*     */ 
/* 280 */             StopDirection stopDirection = null;
/* 281 */             OrderSide positionSide = orderGroup.getOpeningOrder().getSide();
/*     */ 
/* 283 */             if ((positionSide == OrderSide.BUY) && (command == 4)) {
/* 284 */               stopDirection = StopDirection.ASK_GREATER;
/*     */             }
/*     */ 
/* 287 */             if ((positionSide == OrderSide.BUY) && (command == 2)) {
/* 288 */               stopDirection = StopDirection.ASK_LESS;
/*     */             }
/*     */ 
/* 291 */             if ((positionSide == OrderSide.SELL) && (command == 5)) {
/* 292 */               stopDirection = StopDirection.BID_LESS;
/*     */             }
/*     */ 
/* 295 */             if ((positionSide == OrderSide.SELL) && (command == 3)) {
/* 296 */               stopDirection = StopDirection.BID_GREATER;
/*     */             }
/*     */ 
/* 303 */             if ((positionSide == OrderSide.BUY) && ((command == 5) || (command == 1))) {
/* 304 */               stopDirection = StopDirection.BID_GREATER;
/*     */             }
/*     */ 
/* 307 */             if ((positionSide == OrderSide.BUY) && ((command == 3) || (command == 0))) {
/* 308 */               stopDirection = StopDirection.BID_LESS;
/*     */             }
/* 310 */             if ((positionSide == OrderSide.SELL) && ((command == 4) || (command == 1))) {
/* 311 */               stopDirection = StopDirection.ASK_LESS;
/*     */             }
/* 313 */             if ((positionSide == OrderSide.SELL) && ((command == 2) || (command == 0))) {
/* 314 */               stopDirection = StopDirection.ASK_GREATER;
/*     */             }
/*     */ 
/* 317 */             submitStopImpl(orderGroup, orderGroup.getOpeningOrder().getOrderId(), stopOrderTypeo, stopDirection, normailzedPriceStopStr);
/*     */           }
/* 321 */           else if (label != null) {
/* 322 */             submitStopCancelImpl(groupId, command);
/*     */           }
/*     */           try
/*     */           {
/* 326 */             rc = Integer.parseInt(groupId);
/*     */           } catch (Exception e) {
/* 328 */             LOGGER.error(e.getMessage(), e);
/* 329 */             rc = -12;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 336 */     return rc;
/*     */   }
/*     */ 
/*     */   protected abstract void submitStopCancelImpl(String paramString, int paramInt);
/*     */ 
/*     */   protected abstract void submitStopImpl(OrderGroupMessage paramOrderGroupMessage, String paramString1, String paramString2, StopDirection paramStopDirection, String paramString3);
/*     */ 
/*     */   protected abstract OrderGroupMessage getOrderGroupByIdImpl(String paramString);
/*     */ 
/*     */   private int getProfitLoss(String label)
/*     */   {
/* 364 */     int profitLossPips = -99;
/* 365 */     Position position = getPositionByLabelImpl(label);
/* 366 */     if ((position != null) && (position.getPriceOpen() != null) && (position.getPriceCurrent() != null)) {
/* 367 */       BigDecimal priceOpen = position.getPriceOpen().getValue();
/* 368 */       BigDecimal priceCurrent = position.getPriceCurrent().getValue();
/*     */ 
/* 370 */       BigDecimal pipValue = BigDecimal.valueOf(pipValue(priceOpen.doubleValue()));
/* 371 */       BigDecimal profitLossMoneyValue = priceCurrent.subtract(priceOpen);
/* 372 */       profitLossPips = profitLossMoneyValue.divide(pipValue).intValue();
/* 373 */       if (position.getPositionSide() == PositionSide.SHORT) {
/* 374 */         profitLossPips = -profitLossPips;
/*     */       }
/*     */     }
/* 377 */     return profitLossPips;
/*     */   }
/*     */ 
/*     */   public int closeProfitPosition(String label, int profitPips) {
/* 381 */     int profitLoss = getProfitLoss(label);
/* 382 */     if (profitLoss >= profitPips) {
/* 383 */       return closePosition(label);
/*     */     }
/* 385 */     return -99;
/*     */   }
/*     */ 
/*     */   public abstract boolean onMarketStateImpl(String paramString, HTick paramHTick, Market paramMarket);
/*     */ 
/*     */   public void onOrderGroupReceived(OrderGroupMessage orderGroup)
/*     */   {
/* 399 */     throw new RuntimeException("Not implemented.");
/*     */   }
/*     */ 
/*     */   protected int submitMarketOrder(String label, String symbol, int cmd, double amount, double price, String comment) {
/* 403 */     synchronized (this) {
/* 404 */       OrderSide orderSide = null;
/* 405 */       if (cmd == 0)
/* 406 */         orderSide = OrderSide.BUY;
/* 407 */       else if (cmd == 1)
/* 408 */         orderSide = OrderSide.SELL;
/*     */       else {
/* 410 */         return -10;
/*     */       }
/* 412 */       return submitMarketOrderImpl(label, symbol, amount, price, orderSide, comment);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateAccountInfo(AccountInfo accountInfo)
/*     */   {
/* 426 */     double margin = accountInfo.margin;
/* 427 */     setProperty("usableMargin", Double.valueOf(margin));
/*     */ 
/* 431 */     double equity = accountInfo.equity;
/* 432 */     setProperty("equity", Double.valueOf(equity));
/*     */ 
/* 435 */     double balance = accountInfo.balance;
/* 436 */     setProperty("balance", Double.valueOf(balance));
/*     */   }
/*     */ 
/*     */   protected int submitEntryOrder(String label, String symbol, int cmd, double amount, double price, int stopLossPips, int takeProfitPips, String comment)
/*     */   {
/* 451 */     this.tempStopLossPips = stopLossPips;
/* 452 */     this.tempTakeProfitPips = takeProfitPips;
/*     */ 
/* 454 */     synchronized (this)
/*     */     {
/* 456 */       OrderSide orderSide = null;
/* 457 */       if ((cmd == 2) || (cmd == 4) || (cmd == 6) || (cmd == 8))
/* 458 */         orderSide = OrderSide.BUY;
/* 459 */       else if ((cmd == 3) || (cmd == 5) || (cmd == 7) || (cmd == 9))
/* 460 */         orderSide = OrderSide.SELL;
/*     */       else {
/* 462 */         return -10;
/*     */       }
/*     */ 
/* 465 */       BigDecimal onePip = BigDecimal.valueOf(pipValue(price));
/* 466 */       BigDecimal stopLossPrice = null;
/* 467 */       BigDecimal takeProfitPrice = null;
/* 468 */       if (stopLossPips > 0) {
/* 469 */         if (orderSide == OrderSide.BUY) {
/* 470 */           stopLossPrice = BigDecimal.valueOf(price).subtract(onePip.multiply(BigDecimal.valueOf(stopLossPips)));
/*     */         }
/*     */ 
/* 473 */         if (orderSide == OrderSide.SELL) {
/* 474 */           stopLossPrice = BigDecimal.valueOf(price).add(onePip.multiply(BigDecimal.valueOf(stopLossPips)));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 479 */       if (takeProfitPips > 0) {
/* 480 */         if (orderSide == OrderSide.BUY) {
/* 481 */           takeProfitPrice = BigDecimal.valueOf(price).add(onePip.multiply(BigDecimal.valueOf(takeProfitPips)));
/*     */         }
/*     */ 
/* 484 */         if (orderSide == OrderSide.SELL) {
/* 485 */           takeProfitPrice = BigDecimal.valueOf(price).subtract(onePip.multiply(BigDecimal.valueOf(takeProfitPips)));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 492 */       StopDirection stopDirection = null;
/* 493 */       String buttonLabel = null;
/* 494 */       if (cmd == 2) {
/* 495 */         stopDirection = StopDirection.ASK_LESS;
/* 496 */         buttonLabel = "ask<";
/*     */       }
/* 498 */       if (cmd == 6) {
/* 499 */         stopDirection = StopDirection.BID_LESS;
/* 500 */         buttonLabel = "bid<";
/*     */       }
/*     */ 
/* 503 */       if (cmd == 4) {
/* 504 */         stopDirection = StopDirection.ASK_GREATER;
/* 505 */         buttonLabel = "ask>";
/*     */       }
/* 507 */       if (cmd == 8) {
/* 508 */         stopDirection = StopDirection.BID_GREATER;
/* 509 */         buttonLabel = "bid>";
/*     */       }
/*     */ 
/* 512 */       if (cmd == 3) {
/* 513 */         stopDirection = StopDirection.BID_GREATER;
/* 514 */         buttonLabel = "bid>";
/*     */       }
/* 516 */       if (cmd == 7) {
/* 517 */         stopDirection = StopDirection.ASK_GREATER;
/* 518 */         buttonLabel = "ask>";
/*     */       }
/*     */ 
/* 521 */       if (cmd == 5) {
/* 522 */         stopDirection = StopDirection.BID_LESS;
/* 523 */         buttonLabel = "bid<";
/*     */       }
/* 525 */       if (cmd == 9) {
/* 526 */         stopDirection = StopDirection.ASK_LESS;
/* 527 */         buttonLabel = "ask<";
/*     */       }
/*     */ 
/* 530 */       int rc = submitEntryOrderImpl(label, symbol, cmd, orderSide, amount, price, stopDirection, stopLossPrice, takeProfitPrice, buttonLabel, comment);
/*     */ 
/* 532 */       this.tempStopLossPips = 0;
/* 533 */       this.tempTakeProfitPips = 0;
/* 534 */       return rc;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract int submitEntryOrderImpl(String paramString1, String paramString2, int paramInt, OrderSide paramOrderSide, double paramDouble1, double paramDouble2, StopDirection paramStopDirection, BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2, String paramString3, String paramString4);
/*     */ 
/*     */   protected abstract int submitMarketOrderImpl(String paramString1, String paramString2, double paramDouble1, double paramDouble2, OrderSide paramOrderSide, String paramString3);
/*     */ 
/*     */   private static double pipValue(double basePrice)
/*     */   {
/* 549 */     return basePrice <= 20.0D ? 0.0001D : 0.01D;
/*     */   }
/*     */ 
/*     */   protected void checkLabelUniqueness(String label)
/*     */     throws AgentException
/*     */   {
/* 561 */     if (getPositionIdByLabel(label) != null) {
/* 562 */       throw new AgentException(-14, " (" + label + ")");
/*     */     }
/* 564 */     if (getEntryOrderByLabelImpl(label) != null)
/* 565 */       throw new AgentException(-14, " (" + label + ")");
/*     */   }
/*     */ 
/*     */   protected String getPositionIdByLabel(String label)
/*     */   {
/* 570 */     Position position = getPositionByLabelImpl(label);
/* 571 */     if (position != null) {
/* 572 */       return position.getPositionID();
/*     */     }
/* 574 */     return null;
/*     */   }
/*     */ 
/*     */   protected void validateAmount(double amount) throws AgentException {
/* 578 */     if (amount < 0.1D)
/* 579 */       throw new AgentException(-16);
/*     */   }
/*     */ 
/*     */   public Date getGMT()
/*     */   {
/* 596 */     return this.lastTickTime;
/*     */   }
/*     */ 
/*     */   public int setSlipageControl(double slipage)
/*     */   {
/* 603 */     this.slipageControl = slipage;
/* 604 */     return 0;
/*     */   }
/*     */ 
/*     */   public Object getProperty(String key)
/*     */   {
/* 611 */     return this.properties.get(key);
/*     */   }
/*     */ 
/*     */   public void setProperty(String key, Object value)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected abstract void checkLiquidity(String paramString)
/*     */     throws AgentException;
/*     */ 
/*     */   protected abstract Position getPositionByLabelImpl(String paramString);
/*     */ 
/*     */   protected abstract OrderGroupMessage getEntryOrderByLabelImpl(String paramString);
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.BaseAgent
 * JD-Core Version:    0.6.0
 */