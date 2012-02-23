/*      */ package com.dukascopy.transport.common.msg.group;
/*      */ 
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderState;
/*      */ import com.dukascopy.transport.common.model.type.Position;
/*      */ import com.dukascopy.transport.common.model.type.PositionSide;
/*      */ import com.dukascopy.transport.common.model.type.PositionStatus;
/*      */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*      */ import java.io.PrintStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.RoundingMode;
/*      */ import java.text.ParseException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Currency;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.StringTokenizer;
/*      */ import org.json.JSONArray;
/*      */ 
/*      */ public class OrderGroupMessage extends ProtocolMessage
/*      */ {
/*   32 */   private List<OrderMessage> cashedOrders = new ArrayList();
/*      */   public static final String TYPE = "orderGroup";
/*      */   public static final String SUMMARY_COMISSION = "summaryComission";
/*      */   public static final String ORDER_GROUP_ID = "orderGroupId";
/*      */   public static final String ACCOUNT_NAME = "accountName";
/*      */   public static final String ORDERS = "orders";
/*      */   public static final String INSTRUMENT = "instrument";
/*      */   public static final String AMOUNT = "amount";
/*      */   public static final String PRICE_OPEN = "price";
/*      */   public static final String PRICE_POS_OPEN = "pricePO";
/*      */   public static final String SIDE = "side";
/*      */   public static final String STATUS = "status";
/*      */   public static final String IS_OCO_MERGE = "isOcoMerge";
/*      */   public static final String EXTERNAL_SYS_ID = "extSysId";
/*      */   public static final String SIGNAL_ID = "signalId";
/*      */   public static final String ACCOUNT_LOGIN_ID = "loginId";
/*      */   public static final String OCO_LIST = "ocolist";
/*   54 */   private boolean isReal = true;
/*   55 */   private boolean isNew = true;
/*   56 */   private Position cachedPosition = null;
/*      */ 
/*      */   public OrderGroupMessage()
/*      */   {
/*   60 */     setPriority(1000000L);
/*   61 */     setType("orderGroup");
/*      */   }
/*      */ 
/*      */   public OrderGroupMessage(ProtocolMessage message) {
/*   65 */     super(message);
/*   66 */     setPriority(1000000L);
/*   67 */     setType("orderGroup");
/*   68 */     put("orderGroupId", message.getString("orderGroupId"));
/*   69 */     put("accountName", message.getString("accountName"));
/*   70 */     put("orders", message.getJSONArray("orders"));
/*   71 */     put("instrument", message.getString("instrument"));
/*   72 */     put("summaryComission", message.getString("summaryComission"));
/*   73 */     put("amount", message.getString("amount"));
/*   74 */     put("price", message.getString("price"));
/*   75 */     put("pricePO", message.getString("pricePO"));
/*   76 */     put("side", message.getString("side"));
/*   77 */     put("status", message.getString("status"));
/*   78 */     put("isOcoMerge", message.getBool("isOcoMerge"));
/*   79 */     put("extSysId", message.getString("extSysId"));
/*   80 */     put("signalId", message.getString("signalId"));
/*   81 */     put("loginId", message.getString("loginId"));
/*   82 */     put("ocolist", message.getString("ocolist"));
/*   83 */     this.cashedOrders.addAll(getOrders());
/*      */   }
/*      */ 
/*      */   public OrderGroupMessage(String s) throws ParseException {
/*   87 */     super(s);
/*      */   }
/*      */ 
/*      */   public String getAcountLoginId()
/*      */   {
/*   96 */     return getString("loginId");
/*      */   }
/*      */ 
/*      */   public void setAcountLoginId(String loginId)
/*      */   {
/*  105 */     if (loginId != null)
/*  106 */       put("loginId", loginId);
/*      */   }
/*      */ 
/*      */   public String getOrderGroupId()
/*      */   {
/*  116 */     return getString("orderGroupId");
/*      */   }
/*      */ 
/*      */   public void setOrderGroupId(String orderGroupId)
/*      */   {
/*  125 */     put("orderGroupId", orderGroupId);
/*      */   }
/*      */ 
/*      */   public Money getAmount()
/*      */   {
/*  134 */     String amountString = getString("amount");
/*  135 */     if (amountString != null) {
/*  136 */       return new Money(amountString, getCurrencyPrimary()).multiply(ONE_MILLION);
/*      */     }
/*  138 */     return null;
/*      */   }
/*      */ 
/*      */   public void setAmount(Money amount)
/*      */   {
/*  148 */     if (amount != null)
/*  149 */       put("amount", amount.getValue().divide(ONE_MILLION).toPlainString());
/*      */     else
/*  151 */       put("amount", null);
/*      */   }
/*      */ 
/*      */   public Money getPriceOpen()
/*      */   {
/*  162 */     String priceString = getString("price");
/*  163 */     if (priceString != null) {
/*  164 */       return new Money(priceString, getCurrencySecondary());
/*      */     }
/*  166 */     return null;
/*      */   }
/*      */ 
/*      */   public void setPriceOpen(Money price)
/*      */   {
/*  177 */     if (price != null)
/*  178 */       put("price", price.getValue().toPlainString());
/*      */     else
/*  180 */       put("price", null);
/*      */   }
/*      */ 
/*      */   public Money getPricePosOpen()
/*      */   {
/*  191 */     String priceString = getString("pricePO");
/*  192 */     if (priceString != null) {
/*  193 */       return new Money(priceString, getCurrencySecondary());
/*      */     }
/*  195 */     return null;
/*      */   }
/*      */ 
/*      */   public void setPricePosOpen(Money price)
/*      */   {
/*  206 */     if (price != null)
/*  207 */       put("pricePO", price.getValue().toPlainString());
/*      */     else
/*  209 */       put("pricePO", null);
/*      */   }
/*      */ 
/*      */   public PositionSide getSide()
/*      */   {
/*  219 */     String sideString = getString("side");
/*  220 */     if (sideString != null) {
/*  221 */       return PositionSide.fromString(sideString);
/*      */     }
/*  223 */     return null;
/*      */   }
/*      */ 
/*      */   public void setSide(PositionSide side)
/*      */   {
/*  233 */     put("side", side);
/*      */   }
/*      */ 
/*      */   public PositionStatus getStatus()
/*      */   {
/*  243 */     String sideString = getString("status");
/*  244 */     if (sideString != null) {
/*  245 */       return PositionStatus.fromString(sideString);
/*      */     }
/*  247 */     return null;
/*      */   }
/*      */ 
/*      */   public void setStatus(PositionStatus status)
/*      */   {
/*  257 */     put("status", status);
/*      */   }
/*      */ 
/*      */   public Position getPosition()
/*      */   {
/*  266 */     Position result = new Position();
/*  267 */     List orders = getOrders();
/*  268 */     Money buyAmount = new Money("0", getCurrencyPrimary());
/*  269 */     Money sellAmount = new Money("0", getCurrencyPrimary());
/*  270 */     Money buySecAmount = new Money("0", getCurrencySecondary());
/*  271 */     Money sellSecAmount = new Money("0", getCurrencySecondary());
/*  272 */     Money buySecAmountMargin = new Money("0", getCurrencySecondary());
/*  273 */     Money sellSecAmountMargin = new Money("0", getCurrencySecondary());
/*  274 */     Money positionPrice = null;
/*  275 */     Money positionPriceMargin = null;
/*  276 */     for (OrderMessage order : orders)
/*  277 */       if (order.getOrderState() == OrderState.FILLED)
/*      */       {
/*  279 */         BigDecimal secAmount = order.getAmount().getValue().multiply(order.getPriceClient().getValue());
/*  280 */         BigDecimal secAmountMargin = order.getAmount().getValue().multiply(order.getPriceClientInitial().getValue());
/*  281 */         if (order.getSide() == OrderSide.BUY) {
/*  282 */           buyAmount = buyAmount.add(order.getAmount().abs());
/*  283 */           buySecAmount = buySecAmount.add(secAmount);
/*  284 */           buySecAmountMargin = buySecAmountMargin.add(secAmountMargin);
/*  285 */         } else if (order.getSide() == OrderSide.SELL) {
/*  286 */           sellAmount = sellAmount.add(order.getAmount().abs());
/*  287 */           sellSecAmount = sellSecAmount.add(secAmount);
/*  288 */           sellSecAmountMargin = sellSecAmountMargin.add(secAmountMargin);
/*      */         }
/*      */       }
/*      */     PositionSide side;
/*  294 */     if (buyAmount.compareTo(sellAmount) > 0) {
/*  295 */       PositionSide side = PositionSide.LONG;
/*  296 */       if (buyAmount.getValue().compareTo(BigDecimal.ZERO) > 0) {
/*  297 */         positionPrice = new Money(buySecAmount.getValue().divide(buyAmount.getValue(), RoundingMode.HALF_EVEN).stripTrailingZeros(), Money.getCurrency(getCurrencySecondary()));
/*      */ 
/*  301 */         positionPriceMargin = new Money(buySecAmountMargin.getValue().divide(buyAmount.getValue(), RoundingMode.HALF_EVEN).stripTrailingZeros(), Money.getCurrency(getCurrencySecondary()));
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  307 */       side = PositionSide.SHORT;
/*  308 */       if (sellAmount.getValue().compareTo(BigDecimal.ZERO) > 0) {
/*  309 */         positionPrice = new Money(sellSecAmount.getValue().divide(sellAmount.getValue(), RoundingMode.HALF_EVEN).stripTrailingZeros(), Money.getCurrency(getCurrencySecondary()));
/*      */ 
/*  313 */         positionPriceMargin = new Money(sellSecAmountMargin.getValue().divide(sellAmount.getValue(), RoundingMode.HALF_EVEN).stripTrailingZeros(), Money.getCurrency(getCurrencySecondary()));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  320 */     Money positionAmount = buyAmount.subtract(sellAmount).abs();
/*      */ 
/*  322 */     result.setAmount(positionAmount);
/*  323 */     result.setPositionID(getOrderGroupId());
/*  324 */     result.setInstrument(getInstrument());
/*  325 */     result.setPositionSide(side);
/*  326 */     result.setPriceOpen(positionPrice);
/*  327 */     result.setPriceOpenMargin(positionPriceMargin);
/*  328 */     result.setOrderGroup(this);
/*  329 */     result.setTag(getTag());
/*  330 */     return result;
/*      */   }
/*      */ 
/*      */   public Money getOrderGroupCommission()
/*      */   {
/*  341 */     String commissionString = getString("summaryComission");
/*  342 */     if (commissionString != null) {
/*      */       try {
/*  344 */         return Money.of(commissionString);
/*      */       } catch (IllegalArgumentException e) {
/*  346 */         return null;
/*      */       }
/*      */     }
/*  349 */     return null;
/*      */   }
/*      */ 
/*      */   public Money getSummaryComission()
/*      */   {
/*  360 */     Currency accountCurrency = null;
/*  361 */     BigDecimal commissionValue = BigDecimal.ZERO;
/*  362 */     for (OrderMessage order : getOrders()) {
/*  363 */       Money orderCommission = order.getOrderCommission();
/*  364 */       if (orderCommission != null) {
/*  365 */         commissionValue = commissionValue.add(orderCommission.getValue());
/*  366 */         accountCurrency = orderCommission.getCurrency();
/*      */       }
/*      */     }
/*  369 */     if (accountCurrency != null) {
/*  370 */       return new Money(commissionValue, accountCurrency);
/*      */     }
/*  372 */     return null;
/*      */   }
/*      */ 
/*      */   public String getInstrument()
/*      */   {
/*  382 */     String instrument = getString("instrument");
/*  383 */     if (instrument == null) {
/*  384 */       List orders = getOrders();
/*  385 */       if (orders.size() == 0) {
/*  386 */         return null;
/*      */       }
/*  388 */       return ((OrderMessage)orders.get(0)).getInstrument();
/*      */     }
/*  390 */     return instrument;
/*      */   }
/*      */ 
/*      */   public void setInstrument(String instrument)
/*      */   {
/*  399 */     put("instrument", instrument);
/*      */   }
/*      */ 
/*      */   public String getCurrencyPrimary()
/*      */   {
/*  408 */     return getInstrument().substring(0, 3);
/*      */   }
/*      */ 
/*      */   public String getCurrencySecondary()
/*      */   {
/*  417 */     return getInstrument().substring(4);
/*      */   }
/*      */ 
/*      */   public String getAccountName()
/*      */   {
/*  427 */     return getString("accountName");
/*      */   }
/*      */ 
/*      */   public void setAccountName(String accountName)
/*      */   {
/*  436 */     put("accountName", accountName);
/*      */   }
/*      */ 
/*      */   public List<OrderMessage> getOrders()
/*      */   {
/*  446 */     if (!this.cashedOrders.isEmpty()) {
/*  447 */       return new ArrayList(this.cashedOrders);
/*      */     }
/*  449 */     List orders = new ArrayList();
/*  450 */     JSONArray ordersArray = null;
/*      */     try {
/*  452 */       ordersArray = getJSONArray("orders");
/*  453 */       if (ordersArray != null)
/*  454 */         for (int i = 0; i < ordersArray.length(); i++)
/*  455 */           orders.add(new OrderMessage(ordersArray.getJSONObject(i)));
/*      */     }
/*      */     catch (ParseException e)
/*      */     {
/*      */     }
/*      */     catch (NoSuchElementException ex) {
/*  461 */       System.out.println("******** " + ordersArray);
/*      */     }
/*  463 */     for (OrderMessage order : orders) {
/*  464 */       if (order == null) {
/*  465 */         throw new NullPointerException("order is null");
/*      */       }
/*      */     }
/*  468 */     return orders;
/*      */   }
/*      */ 
/*      */   public void setOrders(List<OrderMessage> orders)
/*      */   {
/*  477 */     JSONArray ordersArray = new JSONArray();
/*  478 */     this.cashedOrders.clear();
/*  479 */     for (OrderMessage order : orders) {
/*  480 */       if (order == null) {
/*  481 */         throw new NullPointerException("One of the orders is null");
/*      */       }
/*  483 */       ordersArray.put(order);
/*  484 */       this.cashedOrders.add(order);
/*      */     }
/*  486 */     put("orders", ordersArray);
/*  487 */     this.cachedPosition = null;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public OrderMessage createClosingOrder(Money currentPrice)
/*      */   {
/*  499 */     Money openAmount = new Money("0", getCurrencyPrimary());
/*  500 */     Money closeAmount = new Money("0", getCurrencyPrimary());
/*  501 */     OrderSide closeSide = null;
/*  502 */     for (OrderMessage order : getOrders()) {
/*  503 */       if ((order.getOrderState() == OrderState.FILLED) && (order.getOrderDirection().equals(OrderDirection.OPEN))) {
/*  504 */         openAmount = openAmount.add(order.getExecutedAmount());
/*  505 */         closeSide = order.getSide() == OrderSide.BUY ? OrderSide.SELL : OrderSide.BUY;
/*  506 */       } else if ((order.getOrderState() == OrderState.FILLED) && (order.getOrderDirection().equals(OrderDirection.CLOSE))) {
/*  507 */         closeAmount = closeAmount.add(order.getExecutedAmount());
/*      */       }
/*      */     }
/*  510 */     Money difference = openAmount.subtract(closeAmount);
/*  511 */     if ((closeSide != null) && (difference.getValue().compareTo(BigDecimal.ZERO) > 0)) {
/*  512 */       OrderMessage result = new OrderMessage();
/*  513 */       result.setAmount(difference);
/*  514 */       result.setInstrument(getInstrument());
/*      */ 
/*  516 */       result.setOrderDirection(OrderDirection.CLOSE);
/*  517 */       result.setOrderGroupId(getOrderGroupId());
/*  518 */       result.setOrderState(OrderState.CREATED);
/*  519 */       result.setSide(closeSide);
/*  520 */       result.setPriceClient(currentPrice);
/*  521 */       return result;
/*      */     }
/*  523 */     return null;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Position calculatePosition()
/*      */   {
/*  535 */     if (this.cachedPosition == null) {
/*  536 */       Position result = new Position();
/*  537 */       List orders = getOrders();
/*  538 */       List openingOrders = new ArrayList();
/*  539 */       Money openAmount = new Money("0", getCurrencyPrimary());
/*  540 */       Money closeAmount = new Money("0", getCurrencyPrimary());
/*  541 */       PositionSide side = null;
/*  542 */       for (OrderMessage order : orders) {
/*  543 */         if (order.getOrderDirection() == OrderDirection.OPEN) {
/*  544 */           if (order.getOrderState() == OrderState.FILLED) {
/*  545 */             openingOrders.add(order);
/*  546 */             side = getSideByOpeningOrder(order);
/*  547 */             Money executedAmount = order.getExecutedAmount().abs();
/*  548 */             openAmount = openAmount.add(executedAmount);
/*      */           }
/*  550 */         } else if ((order.getOrderDirection() == OrderDirection.CLOSE) && 
/*  551 */           (order.getOrderState() == OrderState.FILLED)) {
/*  552 */           closeAmount = closeAmount.add(order.getExecutedAmount().abs());
/*      */         }
/*      */       }
/*      */ 
/*  556 */       if (openingOrders.size() == 0) {
/*  557 */         return null;
/*      */       }
/*      */ 
/*  560 */       Money positionAmount = openAmount.subtract(closeAmount);
/*      */ 
/*  562 */       if (positionAmount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
/*  563 */         return null;
/*      */       }
/*  565 */       result.setAmount(positionAmount);
/*  566 */       result.setPositionID(getOrderGroupId());
/*  567 */       result.setInstrument(getInstrument());
/*  568 */       result.setPositionSide(side);
/*  569 */       result.setPriceOpen(getWeightedAvgExecutionPrice(openingOrders));
/*  570 */       result.setOrderGroup(this);
/*  571 */       result.setTag(getTag());
/*      */ 
/*  573 */       Money summaryComission = getSummaryComission();
/*  574 */       if (summaryComission != null) {
/*  575 */         result.setCommission(summaryComission);
/*      */       }
/*  577 */       this.cachedPosition = result;
/*      */     }
/*  579 */     return this.cachedPosition;
/*      */   }
/*      */ 
/*      */   private PositionSide getSideByOpeningOrder(OrderMessage order)
/*      */   {
/*      */     PositionSide side;
/*      */     PositionSide side;
/*  585 */     if (order.getSide() == OrderSide.BUY)
/*  586 */       side = PositionSide.LONG;
/*      */     else {
/*  588 */       side = PositionSide.SHORT;
/*      */     }
/*  590 */     return side;
/*      */   }
/*      */ 
/*      */   public OrderMessage getOpeningOrder() {
/*  594 */     for (OrderMessage order : getOrders()) {
/*  595 */       if (order.getOrderDirection() == OrderDirection.OPEN) {
/*  596 */         return order;
/*      */       }
/*      */     }
/*  599 */     return null;
/*      */   }
/*      */ 
/*      */   public void replaceOrder(OrderMessage order)
/*      */   {
/*  608 */     if (order == null)
/*  609 */       throw new NullPointerException("Order is null");
/*      */     try
/*      */     {
/*  612 */       JSONArray ordersArray = getJSONArray("orders");
/*  613 */       if (ordersArray != null) {
/*  614 */         for (int i = 0; i < ordersArray.length(); i++) {
/*  615 */           OrderMessage currentOrder = new OrderMessage(ordersArray.getJSONObject(i));
/*  616 */           if (currentOrder.getOrderId().equals(order.getOrderId())) {
/*  617 */             ordersArray.put(i, order);
/*  618 */             break;
/*      */           }
/*      */         }
/*  621 */         for (int i = 0; i < this.cashedOrders.size(); i++) {
/*  622 */           OrderMessage currentOrder = (OrderMessage)this.cashedOrders.get(i);
/*  623 */           if (currentOrder.getOrderId().equals(order.getOrderId())) {
/*  624 */             this.cashedOrders.set(i, order);
/*  625 */             break;
/*      */           }
/*      */         }
/*      */       }
/*  629 */       put("orders", ordersArray);
/*  630 */       this.cachedPosition = null;
/*      */     }
/*      */     catch (ParseException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void replaceOrAddOrder(OrderMessage order)
/*      */   {
/*  642 */     if (order == null)
/*  643 */       throw new NullPointerException("Order is null");
/*      */     try
/*      */     {
/*  646 */       JSONArray ordersArray = getJSONArray("orders");
/*  647 */       if (ordersArray != null) {
/*  648 */         boolean found = false;
/*  649 */         for (int i = 0; i < ordersArray.length(); i++) {
/*  650 */           OrderMessage currentOrder = new OrderMessage(ordersArray.getJSONObject(i));
/*  651 */           if (currentOrder.getOrderId().equals(order.getOrderId())) {
/*  652 */             ordersArray.put(i, order);
/*  653 */             found = true;
/*  654 */             break;
/*      */           }
/*      */         }
/*  657 */         for (int i = 0; i < this.cashedOrders.size(); i++) {
/*  658 */           OrderMessage currentOrder = (OrderMessage)this.cashedOrders.get(i);
/*  659 */           if (currentOrder.getOrderId().equals(order.getOrderId())) {
/*  660 */             this.cashedOrders.set(i, order);
/*  661 */             found = true;
/*  662 */             break;
/*      */           }
/*      */         }
/*  665 */         if (!found) {
/*  666 */           ordersArray.put(order);
/*  667 */           this.cashedOrders.add(order);
/*      */         }
/*      */       } else {
/*  670 */         ordersArray = new JSONArray();
/*  671 */         ordersArray.put(order);
/*      */       }
/*  673 */       put("orders", ordersArray);
/*  674 */       this.cachedPosition = null;
/*      */     }
/*      */     catch (ParseException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeOrder(String orderId)
/*      */   {
/*  686 */     List orders = getOrders();
/*  687 */     List newOrders = new ArrayList();
/*  688 */     for (OrderMessage order : orders) {
/*  689 */       if (!orderId.equals(order.getOrderId())) {
/*  690 */         newOrders.add(order);
/*      */       }
/*      */     }
/*  693 */     setOrders(newOrders);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Money getWeightedAvgExecutionPrice(Collection<OrderMessage> orders)
/*      */   {
/*  705 */     BigDecimal amount = BigDecimal.ZERO;
/*  706 */     BigDecimal value = BigDecimal.ZERO;
/*  707 */     if (orders.size() == 0) {
/*  708 */       return null;
/*      */     }
/*  710 */     for (OrderMessage order : orders) {
/*  711 */       Money executedAmount = order.getExecutedAmount();
/*  712 */       Money executionPrice = order.getExecutionPrice();
/*  713 */       if ((executedAmount != null) && (executionPrice != null)) {
/*  714 */         BigDecimal orderValue = executedAmount.getValue().multiply(executionPrice.getValue());
/*  715 */         amount = amount.add(executedAmount.getValue());
/*  716 */         value = value.add(orderValue);
/*      */       }
/*  718 */       if (amount.compareTo(BigDecimal.ZERO) == 0) {
/*  719 */         return null;
/*      */       }
/*      */     }
/*  722 */     return new Money(value.divide(amount, RoundingMode.HALF_EVEN), Money.getCurrency(getCurrencySecondary()));
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Money getProfitLoss()
/*      */   {
/*  736 */     BigDecimal closingAmount = BigDecimal.ZERO;
/*  737 */     OrderMessage openingOrder = getOpeningOrder();
/*  738 */     Collection closingOrders = new ArrayList();
/*  739 */     List orders = getOrders();
/*  740 */     for (OrderMessage order : orders) {
/*  741 */       if ((OrderDirection.CLOSE == order.getOrderDirection()) && (OrderState.FILLED == order.getOrderState())) {
/*  742 */         closingAmount = closingAmount.add(order.getAmount().getValue());
/*  743 */         closingOrders.add(order);
/*      */       }
/*      */     }
/*  746 */     Money closingPrice = getWeightedAvgExecutionPrice(closingOrders);
/*  747 */     Money priceOpen = openingOrder.getExecutionPrice();
/*  748 */     BigDecimal profitLoss = closingAmount.multiply(priceOpen.getValue().subtract(closingPrice.getValue()).negate());
/*  749 */     if (OrderSide.SELL.equals(openingOrder.getSide())) {
/*  750 */       profitLoss = profitLoss.negate();
/*      */     }
/*  752 */     return new Money(profitLoss, Money.getCurrency(openingOrder.getCurrencyPrimary()));
/*      */   }
/*      */ 
/*      */   public OrderMessage getOpenIfOrder()
/*      */   {
/*  762 */     for (OrderMessage order : getOrders()) {
/*  763 */       if ((order.getOrderDirection() == OrderDirection.OPEN) && (order.getPriceStop() != null) && (OrderState.PENDING.equals(order.getOrderState())))
/*      */       {
/*  767 */         return order;
/*      */       }
/*      */     }
/*  770 */     return null;
/*      */   }
/*      */ 
/*      */   public void setOpenIfOrder(OrderMessage openIfOrder)
/*      */   {
/*  782 */     assert ((openIfOrder.getOrderDirection() == OrderDirection.OPEN) && (openIfOrder.getPriceStop() != null)) : ("Wrong open if order submitted: " + openIfOrder.asString());
/*      */ 
/*  784 */     OrderMessage existingOpenIfOrder = getOpenIfOrder();
/*  785 */     if (existingOpenIfOrder == null) {
/*  786 */       List orders = getOrders();
/*  787 */       orders.add(openIfOrder);
/*  788 */       setOrders(orders);
/*      */     } else {
/*  790 */       openIfOrder.setOrderId(existingOpenIfOrder.getOrderId());
/*  791 */       replaceOrder(openIfOrder);
/*      */     }
/*      */   }
/*      */ 
/*      */   public OrderMessage getStopLossOrder()
/*      */   {
/*  803 */     for (OrderMessage order : getOrders()) {
/*  804 */       if (order.isStopLoss()) {
/*  805 */         return order;
/*      */       }
/*      */     }
/*      */ 
/*  809 */     return null;
/*      */   }
/*      */ 
/*      */   public void setStopLossOrder(OrderMessage stopLossOrder)
/*      */   {
/*  820 */     assert (stopLossOrder.isStopLoss()) : ("Wrong stop loss order submitted: " + stopLossOrder.asString());
/*      */ 
/*  822 */     OrderMessage existingStopLossOrder = getStopLossOrder();
/*  823 */     if (existingStopLossOrder == null) {
/*  824 */       List orders = getOrders();
/*  825 */       orders.add(stopLossOrder);
/*  826 */       setOrders(orders);
/*      */     } else {
/*  828 */       stopLossOrder.setOrderId(existingStopLossOrder.getOrderId());
/*  829 */       replaceOrder(stopLossOrder);
/*      */     }
/*      */   }
/*      */ 
/*      */   public OrderMessage getTakeProfitOrder()
/*      */   {
/*  841 */     for (OrderMessage order : getOrders()) {
/*  842 */       if (order.isTakeProfit()) {
/*  843 */         return order;
/*      */       }
/*      */     }
/*      */ 
/*  847 */     return null;
/*      */   }
/*      */ 
/*      */   public OrderMessage getCloseLimitOrder()
/*      */   {
/*  856 */     for (OrderMessage order : getOrders()) {
/*  857 */       if (order.isIfdLimit()) {
/*  858 */         return order;
/*      */       }
/*      */     }
/*  861 */     return null;
/*      */   }
/*      */ 
/*      */   public OrderMessage getCloseStopOrder()
/*      */   {
/*  870 */     for (OrderMessage order : getOrders()) {
/*  871 */       if (order.isIfdStop()) {
/*  872 */         return order;
/*      */       }
/*      */     }
/*  875 */     return null;
/*      */   }
/*      */ 
/*      */   public void setTakeProfitOrder(OrderMessage takeProfitOrder)
/*      */   {
/*  886 */     assert (takeProfitOrder.isTakeProfit()) : ("Wrong take profit order submitted: " + takeProfitOrder.asString());
/*      */ 
/*  888 */     OrderMessage existingTakeProfitOrder = getTakeProfitOrder();
/*  889 */     if (existingTakeProfitOrder == null) {
/*  890 */       List orders = getOrders();
/*  891 */       orders.add(takeProfitOrder);
/*  892 */       setOrders(orders);
/*      */     } else {
/*  894 */       takeProfitOrder.setOrderId(existingTakeProfitOrder.getOrderId());
/*  895 */       replaceOrder(takeProfitOrder);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isNew()
/*      */   {
/*  905 */     return this.isNew;
/*      */   }
/*      */ 
/*      */   public void setNew(boolean isNew)
/*      */   {
/*  914 */     this.isNew = isNew;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean isPositionOpen()
/*      */   {
/*  925 */     Position position = calculatePosition();
/*  926 */     return (position != null) && (position.getAmount().getValue().compareTo(BigDecimal.ZERO) > 0);
/*      */   }
/*      */ 
/*      */   public Date getOpeningOrderTimestamp()
/*      */   {
/*  935 */     OrderMessage openingOrder = getOpeningOrder();
/*  936 */     if (openingOrder != null) {
/*  937 */       return openingOrder.getTimestamp();
/*      */     }
/*  939 */     return null;
/*      */   }
/*      */ 
/*      */   public OrderMessage getOrderById(String orderId, OrderState orderState)
/*      */   {
/*  951 */     for (OrderMessage order : getOrders()) {
/*  952 */       if ((order.getOrderId().equals(orderId)) && ((orderState == null) || (orderState == order.getOrderState()))) {
/*  953 */         return order;
/*      */       }
/*      */     }
/*  956 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean hasOrdersInState(OrderState state)
/*      */   {
/*  966 */     for (OrderMessage order : getOrders()) {
/*  967 */       if (order.getOrderState() == state) {
/*  968 */         return true;
/*      */       }
/*      */     }
/*  971 */     return false;
/*      */   }
/*      */ 
/*      */   public Money regetProfitLoss()
/*      */   {
/*  983 */     BigDecimal closingAmount = BigDecimal.ZERO;
/*  984 */     OrderMessage openingOrder = getOpeningOrder();
/*  985 */     Collection closingOrders = new ArrayList();
/*  986 */     List orders = getOrders();
/*  987 */     for (OrderMessage order : orders) {
/*  988 */       if ((OrderDirection.CLOSE == order.getOrderDirection()) && (OrderState.FILLED == order.getOrderState())) {
/*  989 */         closingAmount = closingAmount.add(order.getAmount().getValue());
/*  990 */         closingOrders.add(order);
/*      */       }
/*      */     }
/*  993 */     Money closingPrice = regetWeightedAvgExecutionPrice(closingOrders);
/*  994 */     Money priceOpen = openingOrder.getPriceClient();
/*  995 */     BigDecimal profitLoss = closingAmount.multiply(priceOpen.getValue().subtract(closingPrice.getValue()).negate());
/*  996 */     if (OrderSide.SELL.equals(openingOrder.getSide())) {
/*  997 */       profitLoss = profitLoss.negate();
/*      */     }
/*  999 */     return new Money(profitLoss, Money.getCurrency(openingOrder.getCurrencyPrimary()));
/*      */   }
/*      */ 
/*      */   private Money regetWeightedAvgExecutionPrice(Collection<OrderMessage> orders)
/*      */   {
/* 1009 */     Money buyAmount = new Money("0", getCurrencyPrimary());
/* 1010 */     Money sellAmount = new Money("0", getCurrencyPrimary());
/* 1011 */     Money buySecAmount = new Money("0", getCurrencySecondary());
/* 1012 */     Money sellSecAmount = new Money("0", getCurrencySecondary());
/* 1013 */     Money positionPrice = null;
/* 1014 */     for (OrderMessage order : orders) {
/* 1015 */       if (order.getOrderState() == OrderState.FILLED)
/*      */       {
/* 1018 */         Money executionPrice = order.getPriceClient();
/* 1019 */         BigDecimal secAmount = order.getAmount().getValue().multiply(executionPrice.getValue());
/* 1020 */         if (order.getSide() == OrderSide.BUY) {
/* 1021 */           buyAmount = buyAmount.add(order.getAmount().abs());
/* 1022 */           buySecAmount = buySecAmount.add(secAmount);
/* 1023 */         } else if (order.getSide() == OrderSide.SELL) {
/* 1024 */           sellAmount = sellAmount.add(order.getAmount().abs());
/* 1025 */           sellSecAmount = sellSecAmount.add(secAmount);
/*      */         }
/*      */       }
/*      */     }
/* 1029 */     if (buyAmount.compareTo(sellAmount) > 0) {
/* 1030 */       if (buyAmount.getValue().compareTo(BigDecimal.ZERO) > 0) {
/* 1031 */         positionPrice = new Money(buySecAmount.getValue().divide(buyAmount.getValue(), RoundingMode.HALF_EVEN), Money.getCurrency(getCurrencySecondary()));
/*      */       }
/*      */ 
/*      */     }
/* 1037 */     else if (sellAmount.getValue().compareTo(BigDecimal.ZERO) > 0) {
/* 1038 */       positionPrice = new Money(sellSecAmount.getValue().divide(sellAmount.getValue(), RoundingMode.HALF_EVEN), Money.getCurrency(getCurrencySecondary()));
/*      */     }
/*      */ 
/* 1044 */     return positionPrice;
/*      */   }
/*      */ 
/*      */   public Position recalculatePosition()
/*      */   {
/* 1054 */     if (this.cachedPosition == null) {
/* 1055 */       Position result = new Position();
/* 1056 */       List orders = getOrders();
/* 1057 */       List openingOrders = new ArrayList();
/* 1058 */       Money openAmount = new Money("0", getCurrencyPrimary());
/* 1059 */       Money closeAmount = new Money("0", getCurrencyPrimary());
/* 1060 */       PositionSide side = null;
/* 1061 */       for (OrderMessage order : orders) {
/* 1062 */         if (order.getOrderDirection() == OrderDirection.OPEN) {
/* 1063 */           if (order.getOrderState() == OrderState.FILLED) {
/* 1064 */             openingOrders.add(order);
/* 1065 */             side = getSideByOpeningOrder(order);
/* 1066 */             Money executedAmount = order.getAmount().abs();
/* 1067 */             openAmount = openAmount.add(executedAmount);
/*      */           }
/* 1069 */         } else if ((order.getOrderDirection() == OrderDirection.CLOSE) && 
/* 1070 */           (order.getOrderState() == OrderState.FILLED)) {
/* 1071 */           closeAmount = closeAmount.add(order.getAmount().abs());
/*      */         }
/*      */       }
/*      */ 
/* 1075 */       if (openingOrders.size() == 0) {
/* 1076 */         return null;
/*      */       }
/*      */ 
/* 1079 */       Money positionAmount = openAmount.subtract(closeAmount);
/*      */ 
/* 1081 */       if (positionAmount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
/* 1082 */         return null;
/*      */       }
/* 1084 */       result.setAmount(positionAmount);
/* 1085 */       result.setPositionID(getOrderGroupId());
/* 1086 */       result.setInstrument(getInstrument());
/* 1087 */       result.setPositionSide(side);
/* 1088 */       result.setPriceOpen(regetWeightedAvgExecutionPrice(openingOrders));
/* 1089 */       result.setOrderGroup(this);
/* 1090 */       result.setTag(getTag());
/*      */ 
/* 1092 */       Money summaryComission = getSummaryComission();
/* 1093 */       if (summaryComission != null) {
/* 1094 */         result.setCommission(summaryComission);
/*      */       }
/* 1096 */       this.cachedPosition = result;
/*      */     }
/* 1098 */     return this.cachedPosition;
/*      */   }
/*      */ 
/*      */   public OrderMessage recreateClosingOrder(Money currentPrice)
/*      */   {
/* 1108 */     Money openAmount = new Money("0", getCurrencyPrimary());
/* 1109 */     Money closeAmount = new Money("0", getCurrencyPrimary());
/* 1110 */     OrderSide closeSide = null;
/* 1111 */     for (OrderMessage order : getOrders()) {
/* 1112 */       if ((order.getOrderState() == OrderState.FILLED) && (order.getOrderDirection().equals(OrderDirection.OPEN))) {
/* 1113 */         openAmount = openAmount.add(order.getAmount());
/* 1114 */         closeSide = order.getSide() == OrderSide.BUY ? OrderSide.SELL : OrderSide.BUY;
/* 1115 */       } else if ((order.getOrderState() == OrderState.FILLED) && (order.getOrderDirection().equals(OrderDirection.CLOSE))) {
/* 1116 */         closeAmount = closeAmount.add(order.getAmount());
/*      */       }
/*      */     }
/* 1119 */     Money difference = openAmount.subtract(closeAmount);
/* 1120 */     if ((closeSide != null) && (difference.getValue().compareTo(BigDecimal.ZERO) > 0)) {
/* 1121 */       OrderMessage result = new OrderMessage();
/* 1122 */       result.setAmount(difference);
/* 1123 */       result.setInstrument(getInstrument());
/*      */ 
/* 1125 */       result.setOrderDirection(OrderDirection.CLOSE);
/* 1126 */       result.setOrderGroupId(getOrderGroupId());
/* 1127 */       result.setOrderState(OrderState.CREATED);
/* 1128 */       result.setSide(closeSide);
/* 1129 */       result.setPriceClient(currentPrice);
/* 1130 */       return result;
/*      */     }
/* 1132 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isPositionOpenRe()
/*      */   {
/* 1142 */     Position position = recalculatePosition();
/* 1143 */     return (position != null) && (position.getAmount().getValue().compareTo(BigDecimal.ZERO) > 0);
/*      */   }
/*      */ 
/*      */   public void setIsReal(boolean isReal) {
/* 1147 */     this.isReal = isReal;
/*      */   }
/*      */ 
/*      */   public boolean isReal() {
/* 1151 */     return this.isReal;
/*      */   }
/*      */ 
/*      */   public Position calculatePositionModified()
/*      */   {
/* 1156 */     Position result = new Position();
/* 1157 */     List orders = getOrders();
/* 1158 */     List openingOrders = new ArrayList();
/* 1159 */     Money buyAmount = new Money("0", getCurrencyPrimary());
/* 1160 */     Money sellAmount = new Money("0", getCurrencyPrimary());
/* 1161 */     for (OrderMessage order : orders)
/* 1162 */       if (order.getOrderState() == OrderState.FILLED) {
/* 1163 */         if (OrderDirection.OPEN == order.getOrderDirection()) {
/* 1164 */           openingOrders.add(order);
/*      */         }
/* 1166 */         if (order.getSide() == OrderSide.BUY)
/* 1167 */           buyAmount = buyAmount.add(order.getAmount().abs());
/* 1168 */         else if (order.getSide() == OrderSide.SELL)
/* 1169 */           sellAmount = sellAmount.add(order.getAmount().abs());
/*      */       }
/*      */     PositionSide side;
/*      */     Money positionAmount;
/*      */     PositionSide side;
/* 1176 */     if (buyAmount.compareTo(sellAmount) > 0) {
/* 1177 */       Money positionAmount = buyAmount.subtract(sellAmount);
/* 1178 */       side = PositionSide.LONG;
/*      */     } else {
/* 1180 */       positionAmount = sellAmount.subtract(buyAmount);
/* 1181 */       side = PositionSide.SHORT;
/*      */     }
/*      */ 
/* 1184 */     if (positionAmount.getValue().compareTo(BigDecimal.ZERO) == 0) {
/* 1185 */       return null;
/*      */     }
/* 1187 */     result.setAmount(positionAmount.abs());
/* 1188 */     result.setPositionID(getOrderGroupId());
/* 1189 */     result.setInstrument(getInstrument());
/* 1190 */     result.setPositionSide(side);
/* 1191 */     result.setPriceOpen(regetWeightedAvgExecutionPrice(openingOrders));
/* 1192 */     result.setOrderGroup(this);
/* 1193 */     result.setTag(getTag());
/*      */ 
/* 1195 */     return result;
/*      */   }
/*      */ 
/*      */   public OrderMessage createClosingOrderModified(Money currentPrice) {
/* 1199 */     Position pos = calculatePositionModified();
/* 1200 */     if (pos != null) {
/* 1201 */       OrderSide closeSide = pos.getPositionSide() == PositionSide.LONG ? OrderSide.SELL : OrderSide.BUY;
/* 1202 */       OrderMessage result = new OrderMessage();
/* 1203 */       result.setAmount(pos.getAmount());
/* 1204 */       result.setInstrument(getInstrument());
/*      */ 
/* 1206 */       result.setOrderDirection(OrderDirection.CLOSE);
/* 1207 */       result.setOrderGroupId(getOrderGroupId());
/* 1208 */       result.setOrderState(OrderState.CREATED);
/* 1209 */       result.setSide(closeSide);
/* 1210 */       result.setPriceClient(currentPrice);
/*      */ 
/* 1212 */       return result;
/*      */     }
/* 1214 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isOcoMerge()
/*      */   {
/* 1224 */     return getBool("isOcoMerge").booleanValue();
/*      */   }
/*      */ 
/*      */   public void setIsOcoMerge(Boolean isOcoMerge)
/*      */   {
/* 1233 */     put("isOcoMerge", isOcoMerge);
/*      */   }
/*      */ 
/*      */   public String getExternalSysId()
/*      */   {
/* 1253 */     return getString("extSysId");
/*      */   }
/*      */ 
/*      */   public void setExternalSysId(String extId)
/*      */   {
/* 1262 */     put("extSysId", extId);
/*      */   }
/*      */ 
/*      */   public String getSignalId()
/*      */   {
/* 1271 */     return getString("signalId");
/*      */   }
/*      */ 
/*      */   public void setSignalId(String extId)
/*      */   {
/* 1280 */     put("signalId", extId);
/*      */   }
/*      */ 
/*      */   public void setOcoOrdersStr(String s) {
/* 1284 */     put("ocolist", s);
/*      */   }
/*      */ 
/*      */   public void setOcoOrders(Map<String, String> order_pos)
/*      */     throws IllegalArgumentException
/*      */   {
/* 1294 */     StringBuffer sb = new StringBuffer();
/* 1295 */     for (Map.Entry ord : order_pos.entrySet()) {
/* 1296 */       if (0 < sb.length()) {
/* 1297 */         sb.append(";");
/*      */       }
/* 1299 */       sb.append((String)ord.getKey()).append("-").append((String)ord.getValue());
/*      */     }
/* 1301 */     put("ocolist", sb.toString());
/*      */   }
/*      */ 
/*      */   public String getOcoOrdersStr() {
/* 1305 */     return (String)get("ocolist");
/*      */   }
/*      */ 
/*      */   public Map<String, String> getOcoOrders()
/*      */   {
/* 1314 */     Map ord = new HashMap();
/* 1315 */     String positionString = getString("ocolist");
/* 1316 */     if (positionString == null) {
/* 1317 */       return ord;
/*      */     }
/* 1319 */     StringTokenizer tokenizer = new StringTokenizer(positionString, ";");
/* 1320 */     while (tokenizer.hasMoreTokens()) {
/* 1321 */       StringTokenizer to = new StringTokenizer(tokenizer.nextToken(), "-");
/* 1322 */       ord.put(to.nextToken(), to.nextToken());
/*      */     }
/* 1324 */     return ord;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.group.OrderGroupMessage
 * JD-Core Version:    0.6.0
 */