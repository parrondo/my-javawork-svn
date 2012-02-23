/*      */ package com.dukascopy.transport.common.msg.pojo;
/*      */ 
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderState;
/*      */ import com.dukascopy.transport.common.model.type.RejectReason;
/*      */ import com.dukascopy.transport.common.model.type.StopDirection;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.group.TradeMessage;
/*      */ import java.io.Serializable;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.RoundingMode;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.TimeZone;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ 
/*      */ public class Order
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 200706200945L;
/*      */   public static final char ORDER_ID_PATH_SEPARATOR = '.';
/*      */   private static final Pattern regexLastIdPart;
/*   36 */   private boolean isDisabled = false;
/*      */ 
/*   38 */   private boolean isSelected = false;
/*      */ 
/*   40 */   private boolean isInClosingState = false;
/*      */   private static final BigDecimal PIP2;
/*      */   private static final BigDecimal PIP4;
/*   46 */   private Long execTimeoutMillis = Long.valueOf(10000L);
/*      */ 
/*   48 */   private boolean placeOffer = false;
/*      */ 
/*   50 */   private boolean hsexUser = false;
/*      */ 
/*   52 */   private boolean mcOrder = false;
/*      */   private String instrument;
/*      */   private String orderId;
/*      */   private String parentOrderId;
/*      */   private String rootId;
/*      */   private Money amount;
/*      */   private OrderSide orderSide;
/*      */   private Money priceLimit;
/*      */   private Money priceTrailingLimit;
/*      */   private Money priceClient;
/*      */   private Money priceClientInitial;
/*      */   private Money priceStop;
/*      */   private BigDecimal pipsStop;
/*      */   private String orderGroupId;
/*      */   private OrderDirection direction;
/*   82 */   private List<String> executorsBlacklist = new ArrayList();
/*      */ 
/*   84 */   private boolean exposureTransfer = false;
/*      */ 
/*   86 */   private boolean oco = false;
/*      */   private StopDirection stopDirection;
/*      */   private OrderState orderState;
/*   92 */   private List<Trade> trades = new ArrayList();
/*      */   private String notes;
/*      */   private RejectReason rejectReason;
/*      */   private Money orderCommission;
/*      */   private Date createdDate;
/*      */   private String executionSeqTime;
/*      */   private Money trailingStop;
/*      */   private String transactionId;
/*  108 */   private transient Map<String, Object> attributes = new HashMap();
/*      */   private String userId;
/*      */   public static final long GTC_THRESHOLD = 63072000000L;
/*      */ 
/*      */   public Order()
/*      */   {
/*      */   }
/*      */ 
/*      */   public Order(OrderMessage orderMessage)
/*      */   {
/*  117 */     this.amount = orderMessage.getAmount();
/*  118 */     this.createdDate = orderMessage.getCreatedDate();
/*  119 */     this.direction = orderMessage.getOrderDirection();
/*  120 */     this.execTimeoutMillis = orderMessage.getExecTimeoutMillis();
/*  121 */     this.executionSeqTime = orderMessage.getExecutionTime();
/*  122 */     this.executorsBlacklist = orderMessage.getExecutorsBlacklist();
/*  123 */     this.exposureTransfer = orderMessage.isExposureTransferOrder();
/*  124 */     this.hsexUser = orderMessage.isHsexUser();
/*  125 */     this.instrument = orderMessage.getInstrument();
/*  126 */     this.isDisabled = orderMessage.isDisabled();
/*  127 */     this.isInClosingState = orderMessage.isInClosingState();
/*  128 */     this.isSelected = orderMessage.isSelected();
/*  129 */     this.mcOrder = orderMessage.isMcOrder();
/*  130 */     this.notes = orderMessage.getNotes();
/*  131 */     this.oco = orderMessage.isOco().booleanValue();
/*  132 */     this.orderCommission = orderMessage.getOrderCommission();
/*  133 */     this.orderGroupId = orderMessage.getOrderGroupId();
/*  134 */     this.orderId = orderMessage.getOrderId();
/*  135 */     this.orderSide = orderMessage.getSide();
/*  136 */     this.orderState = orderMessage.getOrderState();
/*  137 */     this.parentOrderId = orderMessage.getParentOrderId();
/*  138 */     this.pipsStop = orderMessage.getPipsStop();
/*  139 */     this.placeOffer = orderMessage.isPlaceOffer();
/*  140 */     this.priceClient = orderMessage.getPriceClient();
/*  141 */     this.priceClientInitial = orderMessage.getPriceClientInitial();
/*  142 */     this.priceLimit = orderMessage.getPriceLimit();
/*  143 */     this.priceStop = orderMessage.getPriceStop();
/*  144 */     this.priceTrailingLimit = orderMessage.getPriceTrailingLimit();
/*  145 */     this.rejectReason = orderMessage.getRejectReason();
/*  146 */     this.rootId = orderMessage.getRootOrderId();
/*  147 */     this.stopDirection = orderMessage.getStopDirection();
/*  148 */     this.trailingStop = orderMessage.getTrailingStop();
/*  149 */     this.transactionId = orderMessage.getTransactionId();
/*  150 */     this.userId = orderMessage.getUserId();
/*  151 */     for (TradeMessage tm : orderMessage.getTrades()) {
/*  152 */       Trade t = new Trade(tm);
/*  153 */       this.trades.add(t);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Order(Order order) {
/*  158 */     this.amount = order.getAmount();
/*  159 */     this.createdDate = order.getCreatedDate();
/*  160 */     this.direction = order.getOrderDirection();
/*  161 */     this.execTimeoutMillis = order.getExecTimeoutMillis();
/*  162 */     this.executionSeqTime = order.getExecutionTime();
/*  163 */     this.executorsBlacklist = order.getExecutorsBlacklist();
/*  164 */     this.exposureTransfer = order.isExposureTransferOrder();
/*  165 */     this.hsexUser = order.isHsexUser();
/*  166 */     this.instrument = order.getInstrument();
/*  167 */     this.isDisabled = order.isDisabled();
/*  168 */     this.isInClosingState = order.isInClosingState();
/*  169 */     this.isSelected = order.isSelected();
/*  170 */     this.mcOrder = order.isMcOrder();
/*  171 */     this.notes = order.getNotes();
/*  172 */     this.oco = order.isOco().booleanValue();
/*  173 */     this.orderCommission = order.getOrderCommission();
/*  174 */     this.orderGroupId = order.getOrderGroupId();
/*  175 */     this.orderId = order.getOrderId();
/*  176 */     this.orderSide = order.getSide();
/*  177 */     this.orderState = order.getOrderState();
/*  178 */     this.parentOrderId = order.getParentOrderId();
/*  179 */     this.pipsStop = order.getPipsStop();
/*  180 */     this.placeOffer = order.isPlaceOffer();
/*  181 */     this.priceClient = order.getPriceClient();
/*  182 */     this.priceClientInitial = order.getPriceClientInitial();
/*  183 */     this.priceLimit = order.getPriceLimit();
/*  184 */     this.priceStop = order.getPriceStop();
/*  185 */     this.priceTrailingLimit = order.getPriceTrailingLimit();
/*  186 */     this.rejectReason = order.getRejectReason();
/*  187 */     this.rootId = order.getRootOrderId();
/*  188 */     this.stopDirection = order.getStopDirection();
/*  189 */     this.trailingStop = order.getTrailingStop();
/*  190 */     this.transactionId = order.getTransactionId();
/*  191 */     this.trades = order.getTrades();
/*  192 */     this.userId = order.getUserId();
/*      */   }
/*      */ 
/*      */   public String getUserId()
/*      */   {
/*  201 */     return this.userId;
/*      */   }
/*      */ 
/*      */   public void setUserId(String userId)
/*      */   {
/*  208 */     this.userId = userId;
/*      */   }
/*      */ 
/*      */   public OrderMessage toOrderMessage() {
/*  212 */     OrderMessage orderMessage = new OrderMessage();
/*  213 */     List tradeList = new ArrayList();
/*  214 */     for (Trade t : getTrades()) {
/*  215 */       tradeList.add(t.toTradeMessage());
/*      */     }
/*  217 */     orderMessage.setTrades(tradeList);
/*  218 */     orderMessage.setAmount(getAmount());
/*  219 */     orderMessage.setCreatedDate(getCreatedDate());
/*  220 */     orderMessage.setOrderDirection(getOrderDirection());
/*  221 */     orderMessage.setExecTimeoutMillis(getExecTimeoutMillis());
/*  222 */     orderMessage.put("execTime", getExecutionTime());
/*  223 */     orderMessage.setExecutorsBlacklist(getExecutorsBlacklist());
/*  224 */     orderMessage.setExposureTransfer(isExposureTransferOrder());
/*  225 */     orderMessage.setHsexUser(Boolean.valueOf(isHsexUser()));
/*  226 */     orderMessage.setInstrument(getInstrument());
/*  227 */     orderMessage.setDisabled(isDisabled());
/*  228 */     orderMessage.setInClosingState(isInClosingState());
/*  229 */     orderMessage.setSelected(isSelected());
/*  230 */     orderMessage.setIsMcOrder(Boolean.valueOf(isMcOrder()));
/*  231 */     orderMessage.setNotes(getNotes());
/*  232 */     orderMessage.setOco(isOco());
/*  233 */     orderMessage.setOrderCommission(getOrderCommission());
/*  234 */     orderMessage.setOrderGroupId(getOrderGroupId());
/*  235 */     orderMessage.setOrderId(getOrderId());
/*  236 */     orderMessage.setSide(getSide());
/*  237 */     orderMessage.setOrderState(getOrderState());
/*  238 */     orderMessage.setParentOrderId(getParentOrderId());
/*  239 */     if (getPipsStop() != null) {
/*  240 */       orderMessage.setPipsStop(getPipsStop().toPlainString());
/*      */     }
/*  242 */     orderMessage.setPlaceOffer(Boolean.valueOf(isPlaceOffer()));
/*  243 */     orderMessage.setPriceClient(getPriceClient());
/*  244 */     orderMessage.setPriceClientInitial(getPriceClientInitial());
/*  245 */     orderMessage.setPriceLimit(getPriceLimit());
/*  246 */     orderMessage.setPriceStop(getPriceStop());
/*  247 */     orderMessage.setPriceTrailingLimit(getPriceTrailingLimit());
/*  248 */     orderMessage.setRejectReason(getRejectReason());
/*  249 */     orderMessage.setRootOrderId(getRootOrderId());
/*  250 */     orderMessage.setStopDirection(getStopDirection());
/*  251 */     orderMessage.setTrailingStop(getTrailingStop());
/*  252 */     orderMessage.setTransactionId(getTransactionId());
/*  253 */     orderMessage.setUserId(getUserId());
/*  254 */     return orderMessage;
/*      */   }
/*      */ 
/*      */   public String asString()
/*      */   {
/*  263 */     StringBuffer result = new StringBuffer();
/*  264 */     BigDecimal amountInMillions = BigDecimal.ZERO;
/*  265 */     if (getAmount() != null) {
/*  266 */       amountInMillions = getAmount().getValue().divide(new BigDecimal("1000000"), 2, RoundingMode.HALF_UP).stripTrailingZeros();
/*      */     }
/*      */ 
/*  270 */     Money priceStop = getPriceStop();
/*  271 */     Money priceTrailingLimit = getPriceTrailingLimit();
/*  272 */     StopDirection stopDirection = getStopDirection();
/*      */ 
/*  274 */     if ((getOrderId() != null) && (getOrderId().equalsIgnoreCase(getParentOrderId())))
/*  275 */       result.append("#").append(getOrderId()).append(" ");
/*  276 */     else if (getOrderId() != null) {
/*  277 */       result.append("Parent Order #").append(getParentOrderId()).append(" ");
/*      */     }
/*      */ 
/*  280 */     if (priceStop != null) {
/*  281 */       if (isOpening())
/*  282 */         result.append("ENTRY ");
/*  283 */       else if (isStopLoss())
/*  284 */         result.append("STOP LOSS ");
/*  285 */       else if (isTakeProfit()) {
/*  286 */         result.append("TAKE PROFIT ");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  291 */     if (isPlaceOffer()) {
/*  292 */       result.append("PLACE ").append(OrderSide.BUY.equals(getSide()) ? "BID" : "OFFER").append(" ").append(amountInMillions.toPlainString()).append(" mil. ").append(getInstrument()).append(" @ ").append(getPriceClient().getValue().stripTrailingZeros().toPlainString());
/*      */ 
/*  295 */       if (this.execTimeoutMillis != null)
/*  296 */         result.append(getTTLAsString());
/*      */     }
/*      */     else {
/*  299 */       result.append(getSide()).append(" ");
/*  300 */       result.append(amountInMillions.toPlainString()).append(" mil. ");
/*  301 */       result.append(getInstrument());
/*  302 */       if ((priceStop != null) && (priceTrailingLimit != null)) {
/*  303 */         if (!isStopLoss())
/*  304 */           result.append(" @ LIMIT ");
/*      */       }
/*      */       else {
/*  307 */         result.append(" @ MKT");
/*      */       }
/*      */     }
/*  310 */     if (priceTrailingLimit != null) {
/*  311 */       BigDecimal trailingLimitValue = priceTrailingLimit.getValue();
/*  312 */       if (priceStop == null)
/*  313 */         result.append(" MAX SLIPPAGE ").append(trailingLimitValue.stripTrailingZeros().toPlainString());
/*  314 */       else if (priceStop != null) {
/*  315 */         if (getSide() == OrderSide.BUY)
/*  316 */           result.append(priceStop.getValue().add(trailingLimitValue).stripTrailingZeros().toPlainString());
/*  317 */         else if (getSide() == OrderSide.SELL) {
/*  318 */           result.append(priceStop.getValue().subtract(trailingLimitValue).stripTrailingZeros().toPlainString());
/*      */         }
/*      */       }
/*      */     }
/*  322 */     if ((priceStop != null) && (stopDirection != null)) {
/*  323 */       switch (1.$SwitchMap$com$dukascopy$transport$common$model$type$StopDirection[stopDirection.ordinal()]) {
/*      */       case 1:
/*  325 */         result.append(" IF ASK  => ");
/*  326 */         break;
/*      */       case 2:
/*  328 */         result.append(" IF ASK <= ");
/*  329 */         break;
/*      */       case 3:
/*  331 */         result.append(" IF BID => ");
/*  332 */         break;
/*      */       case 4:
/*  334 */         result.append(" IF BID <= ");
/*      */       }
/*      */ 
/*  337 */       result.append(priceStop.getValue().stripTrailingZeros().toPlainString());
/*      */     }
/*  339 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public String getTTLAsString()
/*      */   {
/*  352 */     if (this.execTimeoutMillis == null) {
/*  353 */       return "";
/*      */     }
/*  355 */     long currentPlatformTime = System.currentTimeMillis();
/*      */ 
/*  359 */     if (getExecTimeoutMillis().longValue() > currentPlatformTime) {
/*  360 */       long deltaMillis = getExecTimeoutMillis().longValue() - currentPlatformTime;
/*  361 */       if (deltaMillis > 63072000000L)
/*      */       {
/*  363 */         return " EXPIRES: GTC";
/*      */       }
/*  365 */       Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  366 */       cal.setTimeInMillis(getExecTimeoutMillis().longValue());
/*      */ 
/*  368 */       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
/*  369 */       simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */ 
/*  371 */       return " EXPIRES: " + simpleDateFormat.format(cal.getTime());
/*      */     }
/*      */ 
/*  374 */     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH' hour 'mm' min 'ss' sec'");
/*  375 */     return " EXPIRES after: " + simpleDateFormat.format(new Date(getExecTimeoutMillis().longValue()));
/*      */   }
/*      */ 
/*      */   public boolean isPlaceOffer()
/*      */   {
/*  386 */     return this.placeOffer;
/*      */   }
/*      */ 
/*      */   public void setPlaceOffer(boolean placeOffer)
/*      */   {
/*  397 */     this.placeOffer = placeOffer;
/*      */   }
/*      */ 
/*      */   public boolean isHsexUser()
/*      */   {
/*  406 */     return this.hsexUser;
/*      */   }
/*      */ 
/*      */   public void setHsexUser(Boolean hsex)
/*      */   {
/*  416 */     this.hsexUser = hsex.booleanValue();
/*      */   }
/*      */ 
/*      */   public boolean isMcOrder()
/*      */   {
/*  425 */     return this.mcOrder;
/*      */   }
/*      */ 
/*      */   public void setIsMcOrder(Boolean isMcOrder)
/*      */   {
/*  435 */     this.mcOrder = isMcOrder.booleanValue();
/*      */   }
/*      */ 
/*      */   public String getCurrencyPrimary()
/*      */   {
/*  445 */     return this.instrument.substring(0, 3);
/*      */   }
/*      */ 
/*      */   public String getCurrencySecondary()
/*      */   {
/*  455 */     return this.instrument.substring(4);
/*      */   }
/*      */ 
/*      */   public String getOrderId()
/*      */   {
/*  464 */     return this.orderId;
/*      */   }
/*      */ 
/*      */   public void setOrderId(String orderId)
/*      */   {
/*  474 */     this.orderId = orderId;
/*      */ 
/*  477 */     if (getRootOrderId() == null)
/*  478 */       setRootOrderId(orderId);
/*      */   }
/*      */ 
/*      */   public String getRootOrderId()
/*      */   {
/*  488 */     return this.rootId;
/*      */   }
/*      */ 
/*      */   public void setRootOrderId(String rootOrderId)
/*      */   {
/*  498 */     this.rootId = rootOrderId;
/*      */   }
/*      */ 
/*      */   public String getInstrument()
/*      */   {
/*  507 */     return this.instrument;
/*      */   }
/*      */ 
/*      */   public void setInstrument(String instrument)
/*      */   {
/*  517 */     this.instrument = instrument;
/*      */   }
/*      */ 
/*      */   public Money getAmount()
/*      */   {
/*  526 */     return this.amount;
/*      */   }
/*      */ 
/*      */   public void setAmount(Money amount)
/*      */   {
/*  536 */     this.amount = amount;
/*      */   }
/*      */ 
/*      */   public OrderSide getSide()
/*      */   {
/*  545 */     return this.orderSide;
/*      */   }
/*      */ 
/*      */   public void setSide(OrderSide side)
/*      */   {
/*  555 */     this.orderSide = side;
/*      */   }
/*      */ 
/*      */   public Money getPriceLimit()
/*      */   {
/*  564 */     return this.priceLimit;
/*      */   }
/*      */ 
/*      */   public String getParentOrderId()
/*      */   {
/*  573 */     return this.parentOrderId;
/*      */   }
/*      */ 
/*      */   public void setPriceLimit(Money priceLimit)
/*      */   {
/*  583 */     this.priceLimit = priceLimit;
/*      */   }
/*      */ 
/*      */   public void setPriceLimit(String priceLimit)
/*      */     throws NumberFormatException
/*      */   {
/*  596 */     new BigDecimal(priceLimit);
/*  597 */     this.priceLimit = new Money(priceLimit, getCurrencySecondary());
/*      */   }
/*      */ 
/*      */   public Money getPriceTrailingLimit()
/*      */   {
/*  608 */     return this.priceTrailingLimit;
/*      */   }
/*      */ 
/*      */   public void setPriceTrailingLimit(Money priceTrailingLimit)
/*      */   {
/*  620 */     this.priceTrailingLimit = priceTrailingLimit;
/*      */   }
/*      */ 
/*      */   public Money getPriceClient()
/*      */   {
/*  630 */     return this.priceClient;
/*      */   }
/*      */ 
/*      */   public void setPriceClient(Money price)
/*      */   {
/*  641 */     this.priceClient = price;
/*      */   }
/*      */ 
/*      */   public Money getPriceClientInitial()
/*      */   {
/*  651 */     return this.priceClientInitial;
/*      */   }
/*      */ 
/*      */   public void setPriceClientInitial(Money priceInitial)
/*      */   {
/*  663 */     this.priceClientInitial = priceInitial;
/*      */   }
/*      */ 
/*      */   public void setPriceTrailingLimit(String priceLimit)
/*      */     throws NumberFormatException
/*      */   {
/*  676 */     new BigDecimal(priceLimit);
/*  677 */     this.priceTrailingLimit = new Money(priceLimit, getCurrencySecondary());
/*      */   }
/*      */ 
/*      */   public Money getPriceStop()
/*      */   {
/*  686 */     return this.priceStop;
/*      */   }
/*      */ 
/*      */   public BigDecimal getPipsStop()
/*      */   {
/*  695 */     return this.pipsStop;
/*      */   }
/*      */ 
/*      */   public void setPriceStop(Money priceStop)
/*      */   {
/*  705 */     this.priceStop = priceStop;
/*      */   }
/*      */ 
/*      */   public void setPriceStop(String priceStop)
/*      */     throws NumberFormatException
/*      */   {
/*  718 */     new BigDecimal(priceStop);
/*  719 */     this.priceStop = new Money(priceStop, getCurrencySecondary());
/*      */   }
/*      */ 
/*      */   public void setPipsStop(String pipsStop)
/*      */     throws NumberFormatException
/*      */   {
/*  732 */     new BigDecimal(pipsStop);
/*  733 */     this.pipsStop = new BigDecimal(pipsStop);
/*      */   }
/*      */ 
/*      */   public String getOrderGroupId()
/*      */   {
/*  743 */     return this.orderGroupId;
/*      */   }
/*      */ 
/*      */   public void setOrderGroupId(String orderGroupId)
/*      */   {
/*  754 */     this.orderGroupId = orderGroupId;
/*      */   }
/*      */ 
/*      */   public OrderDirection getOrderDirection()
/*      */   {
/*  764 */     return this.direction;
/*      */   }
/*      */ 
/*      */   public void setExecutorsBlacklist(List<String> blacklist)
/*      */   {
/*  773 */     this.executorsBlacklist = blacklist;
/*      */   }
/*      */ 
/*      */   public List<String> getExecutorsBlacklist()
/*      */   {
/*  780 */     return this.executorsBlacklist;
/*      */   }
/*      */ 
/*      */   public void setExposureTransfer(boolean flag)
/*      */   {
/*  787 */     this.exposureTransfer = flag;
/*      */   }
/*      */ 
/*      */   public boolean isExposureTransferOrder()
/*      */   {
/*  796 */     return this.exposureTransfer;
/*      */   }
/*      */ 
/*      */   public boolean isOpening()
/*      */   {
/*  805 */     OrderDirection orderDirection = getOrderDirection();
/*  806 */     return OrderDirection.OPEN == orderDirection;
/*      */   }
/*      */ 
/*      */   public boolean isClosing()
/*      */   {
/*  815 */     OrderDirection orderDirection = getOrderDirection();
/*  816 */     return OrderDirection.CLOSE == orderDirection;
/*      */   }
/*      */ 
/*      */   public void setStopDirection(StopDirection stopDirection)
/*      */   {
/*  827 */     this.stopDirection = stopDirection;
/*      */   }
/*      */ 
/*      */   public StopDirection getStopDirection()
/*      */   {
/*  837 */     return this.stopDirection;
/*      */   }
/*      */ 
/*      */   public void setOrderDirection(OrderDirection orderDirection)
/*      */   {
/*  848 */     this.direction = orderDirection;
/*      */   }
/*      */ 
/*      */   public Boolean isOco()
/*      */   {
/*  858 */     return Boolean.valueOf(this.oco);
/*      */   }
/*      */ 
/*      */   public void setOco(Boolean oco)
/*      */   {
/*  870 */     this.oco = oco.booleanValue();
/*      */   }
/*      */ 
/*      */   public OrderState getOrderState()
/*      */   {
/*  883 */     return this.orderState;
/*      */   }
/*      */ 
/*      */   public void setOrderState(OrderState orderState)
/*      */   {
/*  898 */     this.orderState = orderState;
/*      */   }
/*      */ 
/*      */   public List<Trade> getTrades()
/*      */   {
/*  907 */     return this.trades;
/*      */   }
/*      */ 
/*      */   public void addTrade(Trade trade)
/*      */   {
/*  917 */     this.trades.add(trade);
/*      */   }
/*      */ 
/*      */   public void setTrades(List<Trade> trades)
/*      */   {
/*  927 */     this.trades = trades;
/*      */   }
/*      */ 
/*      */   public Money getExecutedAmount()
/*      */   {
/*  936 */     Money result = new Money(BigDecimal.ZERO, Money.getCurrency(getCurrencyPrimary()));
/*  937 */     for (int i = 0; i < this.trades.size(); i++) {
/*  938 */       Trade trade = (Trade)this.trades.get(i);
/*  939 */       result = result.add(new Money(trade.getAmountPrimary().getValue().abs(), trade.getAmountPrimary().getCurrency()));
/*      */     }
/*      */ 
/*  943 */     return result;
/*      */   }
/*      */ 
/*      */   public String getNotes()
/*      */   {
/*  952 */     return this.notes;
/*      */   }
/*      */ 
/*      */   public void setNotes(String notes)
/*      */   {
/*  962 */     this.notes = notes;
/*      */   }
/*      */ 
/*      */   public void setParentOrderId(String parentOrderId)
/*      */   {
/*  972 */     this.parentOrderId = parentOrderId;
/*      */   }
/*      */ 
/*      */   public RejectReason getRejectReason()
/*      */   {
/*  981 */     return this.rejectReason;
/*      */   }
/*      */ 
/*      */   public void setRejectReason(RejectReason rejectReason)
/*      */   {
/*  991 */     this.rejectReason = rejectReason;
/*      */   }
/*      */ 
/*      */   public Long getExecTimeoutMillis()
/*      */   {
/* 1000 */     return this.execTimeoutMillis;
/*      */   }
/*      */ 
/*      */   public void setExecTimeoutMillis(Long execTimeoutMillis)
/*      */   {
/* 1010 */     this.execTimeoutMillis = execTimeoutMillis;
/*      */   }
/*      */ 
/*      */   public Money getExecutionPrice()
/*      */   {
/* 1021 */     Collection trades = getTrades();
/* 1022 */     if (trades.size() == 0) {
/* 1023 */       return null;
/*      */     }
/*      */ 
/* 1026 */     BigDecimal value = BigDecimal.ZERO;
/* 1027 */     BigDecimal amount = BigDecimal.ZERO;
/*      */ 
/* 1029 */     for (Trade trade : trades) {
/* 1030 */       BigDecimal tradeAmount = trade.getAmountPrimary().getValue().abs();
/* 1031 */       BigDecimal tradeValue = tradeAmount.multiply(trade.getPrice().getValue());
/* 1032 */       amount = amount.add(tradeAmount);
/* 1033 */       value = value.add(tradeValue);
/*      */     }
/*      */ 
/* 1036 */     if (amount.compareTo(BigDecimal.ZERO) == 0) {
/* 1037 */       return null;
/*      */     }
/* 1039 */     return new Money(value.divide(amount, RoundingMode.HALF_EVEN).stripTrailingZeros(), Money.getCurrency(getCurrencySecondary()));
/*      */   }
/*      */ 
/*      */   public boolean isStopLoss()
/*      */   {
/* 1051 */     if ((OrderDirection.CLOSE == getOrderDirection()) && (null != getPriceStop())) {
/* 1052 */       if (OrderSide.SELL == getSide()) {
/* 1053 */         return (StopDirection.ASK_LESS == getStopDirection()) || (StopDirection.BID_LESS == getStopDirection());
/*      */       }
/* 1055 */       return (StopDirection.ASK_GREATER == getStopDirection()) || (StopDirection.BID_GREATER == getStopDirection());
/*      */     }
/* 1057 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isTakeProfit()
/*      */   {
/* 1067 */     if ((OrderDirection.CLOSE == getOrderDirection()) && (null != getPriceStop())) {
/* 1068 */       if (OrderSide.BUY == getSide()) {
/* 1069 */         return (StopDirection.ASK_LESS == getStopDirection()) || (StopDirection.BID_LESS == getStopDirection());
/*      */       }
/* 1071 */       return (StopDirection.ASK_GREATER == getStopDirection()) || (StopDirection.BID_GREATER == getStopDirection());
/*      */     }
/* 1073 */     return false;
/*      */   }
/*      */ 
/*      */   public Money getOrderCommission()
/*      */   {
/* 1082 */     return this.orderCommission;
/*      */   }
/*      */ 
/*      */   public void setOrderCommission(Money commission)
/*      */   {
/* 1092 */     this.orderCommission = commission;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   private boolean isStopLossTakeProfit(boolean stopLoss)
/*      */   {
/* 1105 */     boolean isStopLoss = false;
/* 1106 */     OrderSide sideCheck = stopLoss ? OrderSide.SELL : OrderSide.BUY;
/* 1107 */     if ((getOrderDirection() == OrderDirection.CLOSE) && (getPriceStop() != null)) {
/* 1108 */       if (getSide() == sideCheck)
/* 1109 */         isStopLoss = (getStopDirection() == StopDirection.ASK_LESS) || (getStopDirection() == StopDirection.BID_LESS);
/*      */       else {
/* 1111 */         isStopLoss = (getStopDirection() == StopDirection.ASK_GREATER) || (getStopDirection() == StopDirection.BID_GREATER);
/*      */       }
/*      */     }
/*      */ 
/* 1115 */     return isStopLoss;
/*      */   }
/*      */ 
/*      */   public boolean isDisabled() {
/* 1119 */     return this.isDisabled;
/*      */   }
/*      */ 
/*      */   public void setDisabled(boolean disabled) {
/* 1123 */     this.isDisabled = disabled;
/*      */   }
/*      */ 
/*      */   public boolean isSelected() {
/* 1127 */     return this.isSelected;
/*      */   }
/*      */ 
/*      */   public void setSelected(boolean selected) {
/* 1131 */     this.isSelected = selected;
/*      */   }
/*      */ 
/*      */   public boolean isInClosingState() {
/* 1135 */     return this.isInClosingState;
/*      */   }
/*      */ 
/*      */   public void setInClosingState(boolean inClosingState) {
/* 1139 */     this.isInClosingState = inClosingState;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object o) {
/* 1143 */     if (o == null)
/* 1144 */       return false;
/* 1145 */     if (getOrderId() == null)
/* 1146 */       return false;
/* 1147 */     return getOrderId().equalsIgnoreCase(((Order)o).getOrderId());
/*      */   }
/*      */ 
/*      */   public int hashCode() {
/* 1151 */     return getOrderId() != null ? getOrderId().hashCode() : 0;
/*      */   }
/*      */ 
/*      */   private BigDecimal toPips(BigDecimal price, BigDecimal difference) {
/* 1155 */     BigDecimal differenceAbs = difference.abs();
/* 1156 */     BigDecimal pipValue = price.compareTo(new BigDecimal("20.0")) >= 0 ? PIP2 : PIP4;
/* 1157 */     return differenceAbs.divide(pipValue, 1, RoundingMode.HALF_UP).stripTrailingZeros();
/*      */   }
/*      */ 
/*      */   public Date getCreatedDate()
/*      */   {
/* 1166 */     return this.createdDate;
/*      */   }
/*      */ 
/*      */   public void setCreatedDate(Date timestamp)
/*      */   {
/* 1176 */     this.createdDate = timestamp;
/*      */   }
/*      */ 
/*      */   public void startNewSequence()
/*      */   {
/* 1183 */     assert (getRootOrderId() != null);
/* 1184 */     assert (getOrderId() != null);
/*      */ 
/* 1186 */     String orderId = getOrderId();
/* 1187 */     setParentOrderId(orderId);
/*      */ 
/* 1189 */     String newOrderId = orderId + '.' + 0;
/* 1190 */     setOrderId(newOrderId);
/*      */   }
/*      */ 
/*      */   public String incrementOrderId()
/*      */   {
/* 1200 */     assert (getRootOrderId() != null);
/* 1201 */     assert (getOrderId() != null);
/* 1202 */     assert (getParentOrderId() != null);
/*      */ 
/* 1204 */     String orderId = getOrderId();
/*      */ 
/* 1206 */     Matcher matcher = regexLastIdPart.matcher(orderId);
/* 1207 */     if (!matcher.matches()) {
/* 1208 */       throw new IllegalStateException("Incorrect order id composition.");
/*      */     }
/* 1210 */     int subseqId = Integer.valueOf(matcher.group(2)).intValue();
/* 1211 */     subseqId++;
/*      */ 
/* 1213 */     String newOrderId = matcher.group(1) + '.' + subseqId;
/* 1214 */     setOrderId(newOrderId);
/*      */ 
/* 1216 */     return newOrderId;
/*      */   }
/*      */ 
/*      */   public void addExecutionTime(String cp, long time) {
/* 1220 */     String rez = this.executionSeqTime;
/* 1221 */     if (rez == null)
/* 1222 */       rez = cp + ":" + time;
/*      */     else {
/* 1224 */       rez = rez + "," + cp + ":" + time;
/*      */     }
/* 1226 */     this.executionSeqTime = rez;
/*      */   }
/*      */ 
/*      */   public void addExecutionTime(String cp) {
/* 1230 */     addExecutionTime(cp, System.currentTimeMillis());
/*      */   }
/*      */ 
/*      */   public String getExecutionTime() {
/* 1234 */     return this.executionSeqTime;
/*      */   }
/*      */ 
/*      */   public Money getTrailingStop()
/*      */   {
/* 1244 */     return this.trailingStop;
/*      */   }
/*      */ 
/*      */   public void setTrailingStop(Money trailingStop)
/*      */   {
/* 1255 */     this.trailingStop = trailingStop;
/*      */   }
/*      */ 
/*      */   public String getTransactionId()
/*      */   {
/* 1264 */     return this.transactionId;
/*      */   }
/*      */ 
/*      */   public void setTransactionId(String transactionId)
/*      */   {
/* 1274 */     this.transactionId = transactionId;
/*      */   }
/*      */ 
/*      */   public Object getAttribute(Object key)
/*      */   {
/* 1283 */     return this.attributes.get(key);
/*      */   }
/*      */ 
/*      */   public Object putAttribute(String key, Object value)
/*      */   {
/* 1293 */     return this.attributes.put(key, value);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   33 */     regexLastIdPart = Pattern.compile("^(.*)\\.([^\\.]+)$");
/*      */ 
/*   42 */     PIP2 = new BigDecimal("0.01");
/*      */ 
/*   44 */     PIP4 = new BigDecimal("0.0001");
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.pojo.Order
 * JD-Core Version:    0.6.0
 */