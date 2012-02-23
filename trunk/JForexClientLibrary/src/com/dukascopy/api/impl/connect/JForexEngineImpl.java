/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.IEngine;
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.IEngine.StrategyMode;
/*     */ import com.dukascopy.api.IEngine.Type;
/*     */ import com.dukascopy.api.IMessage.Type;
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.api.ISignal.Type;
/*     */ import com.dukascopy.api.ISignalsProcessor;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.JFException.Error;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.impl.AbstractEngine;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.feed.IFeedCommissionManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.transport.client.TransportClient;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyBroadcastMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JForexEngineImpl extends AbstractEngine
/*     */   implements IEngine
/*     */ {
/*  48 */   private static final Logger LOGGER = LoggerFactory.getLogger(JForexEngineImpl.class);
/*     */   private static final int BROADCAST_TOPIC_MAX_LENGTH = 100;
/*     */   private static final int BROADCAST_MESSAGE_MAX_LENGTH = 1000;
/*  51 */   private static final long BROADCAST_MIN_PERIOD = TimeUnit.SECONDS.toMillis(1L);
/*     */   static final double DEFAULT_SLIPPAGE = 5.0D;
/*  55 */   private IEngine.Type myType = IEngine.Type.DEMO;
/*     */   private IEngine.StrategyMode mode;
/*     */   private final JForexTaskManager taskManager;
/*  59 */   private String accountName = null;
/*  60 */   private volatile long lastBroadcastTime = 0L;
/*     */   private final ISignalsProcessor signalsProcessor;
/*     */ 
/*     */   public JForexEngineImpl(JForexTaskManager taskManager, String accountName, boolean live)
/*     */   {
/*  68 */     if (live) {
/*  69 */       this.myType = IEngine.Type.LIVE;
/*     */     }
/*     */ 
/*  72 */     this.taskManager = taskManager;
/*  73 */     this.accountName = accountName;
/*  74 */     this.mode = IEngine.StrategyMode.INDEPENDENT;
/*  75 */     this.signalsProcessor = null;
/*     */   }
/*     */ 
/*     */   public JForexEngineImpl(JForexTaskManager taskManager, ISignalsProcessor signalsProcessor, IEngine.StrategyMode mode, String accountName, boolean live) {
/*  79 */     if (live) {
/*  80 */       this.myType = IEngine.Type.LIVE;
/*     */     }
/*     */ 
/*  83 */     this.taskManager = taskManager;
/*  84 */     this.accountName = accountName;
/*  85 */     this.mode = mode;
/*  86 */     this.signalsProcessor = signalsProcessor;
/*     */   }
/*     */ 
/*     */   public IOrder getOrder(String label)
/*     */     throws JFException
/*     */   {
/*  93 */     List allOrders = getOrders();
/*  94 */     IOrder rc = null;
/*  95 */     for (IOrder order : allOrders) {
/*  96 */       if ((order.getLabel() != null) && (order.getLabel().equals(label))) {
/*  97 */         rc = order;
/*  98 */         break;
/*     */       }
/*     */     }
/* 101 */     return rc;
/*     */   }
/*     */ 
/*     */   public List<IOrder> getOrders(Instrument instrument)
/*     */     throws JFException
/*     */   {
/* 108 */     List rc = new ArrayList();
/* 109 */     for (IOrder order : getOrders()) {
/* 110 */       if (instrument == order.getInstrument()) {
/* 111 */         rc.add(order);
/*     */       }
/*     */     }
/* 114 */     return rc;
/*     */   }
/*     */ 
/*     */   public List<IOrder> getOrders()
/*     */     throws JFException
/*     */   {
/* 121 */     return this.taskManager.getOrdersInternalCollection().allAsOrders();
/*     */   }
/*     */ 
/*     */   public IEngine.Type getType()
/*     */   {
/* 128 */     return this.myType;
/*     */   }
/*     */ 
/*     */   public IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime, String comment)
/*     */     throws JFException
/*     */   {
/* 135 */     if (!this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/* 136 */       throw new JFException(JFException.Error.THREAD_INCORRECT);
/*     */     }
/*     */ 
/* 139 */     label = validateLabel(label);
/*     */ 
/* 141 */     validateOrder(this.taskManager.isGlobal(), instrument, orderCommand, amount, price, slippage, stopLossPrice, takeProfitPrice, goodTillTime, comment);
/*     */ 
/* 143 */     FeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/* 144 */     if (!feedDataProvider.isSubscribedToInstrument(instrument)) {
/* 145 */       throw new JFException(new StringBuilder().append("Not subscribed to the instrument [").append(instrument).append("]").toString());
/*     */     }
/* 147 */     Set conversionDeps = AbstractCurrencyConverter.getConversionDeps(instrument.getSecondaryCurrency(), Instrument.EURUSD.getSecondaryCurrency());
/* 148 */     for (Instrument instrumentDep : conversionDeps) {
/* 149 */       if (!feedDataProvider.isSubscribedToInstrument(instrumentDep)) {
/* 150 */         throw new JFException(new StringBuilder().append("Not subscribed to the instrument [").append(instrumentDep).append("]").toString());
/*     */       }
/*     */     }
/* 153 */     conversionDeps = AbstractCurrencyConverter.getConversionDeps(Instrument.EURUSD.getSecondaryCurrency(), this.taskManager.getAccountCurrency());
/* 154 */     for (Instrument instrumentDep : conversionDeps) {
/* 155 */       if (!feedDataProvider.isSubscribedToInstrument(instrumentDep)) {
/* 156 */         throw new JFException(new StringBuilder().append("Not subscribed to the instrument [").append(instrumentDep).append("]").toString());
/*     */       }
/*     */     }
/*     */ 
/* 160 */     price = StratUtils.round(price, 7);
/*     */ 
/* 163 */     amount = StratUtils.round(amount, 6);
/*     */ 
/* 165 */     stopLossPrice = StratUtils.round(stopLossPrice, 7);
/*     */ 
/* 167 */     takeProfitPrice = StratUtils.round(takeProfitPrice, 7);
/*     */ 
/* 170 */     if (!Double.isNaN(slippage)) {
/* 171 */       if (slippage < 0.0D) {
/* 172 */         slippage = 5.0D;
/*     */       }
/*     */ 
/* 175 */       slippage = StratUtils.round(slippage * instrument.getPipValue(), 7);
/*     */     }
/*     */ 
/* 180 */     PlatformOrderImpl order = new PlatformOrderImpl(this.taskManager);
/* 181 */     order.lastServerRequest = PlatformOrderImpl.ServerRequest.SUBMIT;
/* 182 */     order.label = label;
/* 183 */     order.requestedAmount = amount;
/* 184 */     order.comment = comment;
/* 185 */     order.slPrice = stopLossPrice;
/* 186 */     order.tpPrice = takeProfitPrice;
/* 187 */     order.openPrice = price;
/* 188 */     order.instrument = instrument;
/* 189 */     if (goodTillTime > 0L) {
/* 190 */       order.goodTillTime = goodTillTime;
/*     */     }
/* 192 */     order.orderCommand = orderCommand;
/*     */ 
/* 194 */     if (order.slPrice > 0.0D) {
/* 195 */       order.slSide = (orderCommand.isLong() ? OfferSide.BID : OfferSide.ASK);
/*     */     }
/*     */ 
/* 198 */     createSignal(order, orderCommand.equals(IEngine.OrderCommand.BUY) ? ISignal.Type.ORDER_BUY : ISignal.Type.ORDER_SELL);
/*     */ 
/* 202 */     if (this.mode.equals(IEngine.StrategyMode.INDEPENDENT))
/*     */     {
/* 204 */       this.taskManager.getOrdersInternalCollection().put(label, order, true);
/*     */ 
/* 206 */       DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/* 207 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/* 209 */       if (!this.taskManager.isConnected())
/*     */       {
/* 211 */         String content = new StringBuilder().append("Order REJECTED: ").append((orderCommand == IEngine.OrderCommand.PLACE_BID) || (orderCommand == IEngine.OrderCommand.PLACE_OFFER) ? "PLACE OFFER" : orderCommand == IEngine.OrderCommand.PLACE_BID ? "PLACE BID " : new StringBuilder().append(orderCommand.isConditional() ? "CONDITIONAL " : "").append(orderCommand.isLong() ? "SELL" : "BUY").toString()).append(" ").append(instrument).append(", REASON: disconnect").toString();
/*     */ 
/* 215 */         LOGGER.warn(content);
/* 216 */         ErrorResponseMessage error = new ErrorResponseMessage(content);
/* 217 */         this.taskManager.onErrorMessage(error, order);
/* 218 */         return order;
/*     */       }
/*     */ 
/* 221 */       BigDecimal usableMargin = this.taskManager.getUsableMargin();
/* 222 */       if ((usableMargin != null) && (usableMargin.compareTo(BigDecimal.ZERO) < 0))
/*     */       {
/* 224 */         if (!orderCommand.isConditional())
/*     */         {
/* 226 */           if (this.taskManager.getOrdersInternalCollection().isLongExposure(instrument) == orderCommand.isLong())
/*     */           {
/* 229 */             String content = new StringBuilder().append("Order REJECTED: ").append((orderCommand == IEngine.OrderCommand.PLACE_BID) || (orderCommand == IEngine.OrderCommand.PLACE_OFFER) ? "PLACE OFFER" : orderCommand == IEngine.OrderCommand.PLACE_BID ? "PLACE BID " : new StringBuilder().append(orderCommand.isConditional() ? "CONDITIONAL " : "").append(orderCommand.isLong() ? "SELL" : "BUY").toString()).append(" ").append(instrument).append(", REASON: no margin available").toString();
/*     */ 
/* 233 */             LOGGER.warn(content);
/*     */ 
/* 235 */             NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Order ").append(orderCommand == IEngine.OrderCommand.PLACE_OFFER ? "PLACE OFFER" : orderCommand == IEngine.OrderCommand.PLACE_BID ? "PLACE BID" : new StringBuilder().append("ENTRY ").append(orderCommand.isLong() ? "BUY" : "SELL").toString()).append(" ").append(BigDecimal.valueOf(order.getRequestedAmount()).subtract(BigDecimal.valueOf(order.getAmount())).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(instrument).append(" @ ").append((orderCommand != IEngine.OrderCommand.PLACE_BID) && (orderCommand != IEngine.OrderCommand.PLACE_OFFER) ? new StringBuilder().append("LIMIT ").append(BigDecimal.valueOf(order.getOpenPrice()).toPlainString()).toString() : !orderCommand.isConditional() ? "MKT" : BigDecimal.valueOf(order.getOpenPrice()).toPlainString()).append(orderCommand.isConditional() ? new StringBuilder().append(" IF ").append((orderCommand == IEngine.OrderCommand.BUYLIMIT) || (orderCommand == IEngine.OrderCommand.BUYSTOP) || (orderCommand == IEngine.OrderCommand.SELLLIMIT_BYASK) || (orderCommand == IEngine.OrderCommand.SELLSTOP_BYASK) ? "ASK" : "BID").append(" ").append((orderCommand == IEngine.OrderCommand.BUYLIMIT) || (orderCommand == IEngine.OrderCommand.BUYLIMIT_BYBID) || (orderCommand == IEngine.OrderCommand.SELLSTOP) || (orderCommand == IEngine.OrderCommand.SELLSTOP_BYASK) ? "<=" : "=>").append(" ").append(BigDecimal.valueOf(price).toPlainString()).toString() : (orderCommand == IEngine.OrderCommand.PLACE_BID) || (orderCommand == IEngine.OrderCommand.PLACE_OFFER) ? new StringBuilder().append(" EXPIRES:").append(goodTillTime == 0L ? "GTC" : format.format(Long.valueOf(goodTillTime))).toString() : "").append(" is sent at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*     */ 
/* 248 */             NotificationUtilsProvider.getNotificationUtils().postInfoMessage(content, false);
/*     */ 
/* 250 */             ErrorResponseMessage error = new ErrorResponseMessage(content);
/* 251 */             this.taskManager.onErrorMessage(error, order);
/* 252 */             return order;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 258 */       if ((orderCommand == IEngine.OrderCommand.PLACE_BID) || (orderCommand == IEngine.OrderCommand.PLACE_OFFER))
/*     */       {
/* 260 */         if (feedDataProvider.getFeedCommissionManager().hasCommission(instrument)) {
/* 261 */           String warning = generateFeedCommissionWarning(instrument);
/* 262 */           LOGGER.warn(warning);
/* 263 */           NotificationUtilsProvider.getNotificationUtils().postWarningMessage(warning);
/* 264 */           ErrorResponseMessage error = new ErrorResponseMessage(warning);
/* 265 */           this.taskManager.onErrorMessage(error, order);
/*     */         } else {
/* 267 */           if (goodTillTime < 0L) {
/* 268 */             throw new JFException(JFException.Error.INVALID_GTT);
/*     */           }
/* 270 */           if ((goodTillTime > 0L) && (goodTillTime < 63072000000L)) {
/* 271 */             throw new JFException(JFException.Error.INVALID_GTT);
/*     */           }
/* 273 */           TransportClient transportClient = this.taskManager.getTransportClient();
/* 274 */           OrderGroupMessage ogm = JForexAPI.placeBidOffer(this.taskManager.getStrategyKey(), label, instrument, orderCommand, amount, price, stopLossPrice, takeProfitPrice, goodTillTime, comment, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*     */ 
/* 276 */           if (LOGGER.isDebugEnabled()) {
/* 277 */             LOGGER.debug(new StringBuilder().append("Submitting bid/offer order [").append(ogm).append("]").toString());
/*     */           }
/* 279 */           ProtocolMessage submitResult = transportClient.controlRequest(ogm);
/*     */ 
/* 282 */           if ((submitResult instanceof ErrorResponseMessage)) {
/* 283 */             ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/* 284 */             this.taskManager.onErrorMessage(error, order);
/*     */           }
/* 286 */           NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Order ").append(orderCommand == IEngine.OrderCommand.PLACE_BID ? "PLACE BID" : "PLACE OFFER").append(" ").append(BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(instrument).append(" @ ").append(BigDecimal.valueOf(price).toPlainString()).append(" EXPIRES:").append(goodTillTime == 0L ? "GTC" : format.format(Long.valueOf(goodTillTime))).append(" is sent at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 296 */         TransportClient transportClient = this.taskManager.getTransportClient();
/*     */         OrderGroupMessage ogm;
/*     */         try
/*     */         {
/* 299 */           ogm = JForexAPI.submitOrder(this.taskManager.getStrategyKey(), label, instrument, orderCommand, amount, price, slippage, stopLossPrice, takeProfitPrice, comment, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*     */         }
/*     */         catch (IllegalStateException e) {
/* 302 */           throw new JFException(e.getMessage(), e);
/*     */         }
/* 304 */         if (LOGGER.isDebugEnabled()) {
/* 305 */           LOGGER.debug(new StringBuilder().append("Submitting order [").append(ogm).append("]").toString());
/*     */         }
/* 307 */         ProtocolMessage submitResult = transportClient.controlRequest(ogm);
/* 308 */         if ((submitResult instanceof ErrorResponseMessage)) {
/* 309 */           ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/* 310 */           this.taskManager.onErrorMessage(error, order);
/*     */         }
/* 312 */         NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Order ").append(orderCommand.isConditional() ? "ENTRY " : "").append(orderCommand.isLong() ? "BUY" : "SELL").append(" ").append(BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(instrument).append(" @ ").append((!orderCommand.isConditional()) || (Double.isNaN(slippage)) ? "MKT" : new StringBuilder().append("LIMIT ").append(orderCommand.isLong() ? BigDecimal.valueOf(price).add(BigDecimal.valueOf(slippage)).toPlainString() : BigDecimal.valueOf(price).subtract(BigDecimal.valueOf(slippage)).toPlainString()).toString()).append(orderCommand.isConditional() ? new StringBuilder().append(" IF ").append((orderCommand == IEngine.OrderCommand.BUYLIMIT) || (orderCommand == IEngine.OrderCommand.BUYSTOP) || (orderCommand == IEngine.OrderCommand.SELLLIMIT_BYASK) || (orderCommand == IEngine.OrderCommand.SELLSTOP_BYASK) ? "ASK" : "BID").append(" ").append((orderCommand == IEngine.OrderCommand.BUYLIMIT) || (orderCommand == IEngine.OrderCommand.BUYLIMIT_BYBID) || (orderCommand == IEngine.OrderCommand.SELLSTOP) || (orderCommand == IEngine.OrderCommand.SELLSTOP_BYASK) ? "<=" : "=>").append(" ").append(BigDecimal.valueOf(price).toPlainString()).toString() : "").append(" is sent at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 327 */     return order;
/*     */   }
/*     */ 
/*     */   public IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime) throws JFException
/*     */   {
/* 332 */     return submitOrder(label, instrument, orderCommand, amount, price, slippage, stopLossPrice, takeProfitPrice, goodTillTime, null);
/*     */   }
/*     */ 
/*     */   public IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice) throws JFException {
/* 336 */     return submitOrder(label, instrument, orderCommand, amount, price, slippage, stopLossPrice, takeProfitPrice, 0L);
/*     */   }
/*     */ 
/*     */   public IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage) throws JFException {
/* 340 */     return submitOrder(label, instrument, orderCommand, amount, price, slippage, 0.0D, 0.0D);
/*     */   }
/*     */ 
/*     */   public IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price) throws JFException {
/* 344 */     double slippage = 5.0D;
/*     */ 
/* 346 */     if ((orderCommand != null) && ((orderCommand == IEngine.OrderCommand.BUYLIMIT) || (orderCommand == IEngine.OrderCommand.BUYLIMIT_BYBID) || (orderCommand == IEngine.OrderCommand.SELLLIMIT) || (orderCommand == IEngine.OrderCommand.SELLLIMIT_BYASK))) {
/* 347 */       slippage = 0.0D;
/*     */     }
/* 349 */     return submitOrder(label, instrument, orderCommand, amount, price, slippage);
/*     */   }
/*     */ 
/*     */   public IOrder submitOrder(String label, Instrument instrument, IEngine.OrderCommand orderCommand, double amount) throws JFException {
/* 353 */     return submitOrder(label, instrument, orderCommand, amount, 0.0D);
/*     */   }
/*     */ 
/*     */   public PlatformOrderImpl getOrderById(String positionId)
/*     */   {
/* 359 */     return this.taskManager.getOrdersInternalCollection().getOrderById(positionId);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 364 */     return "Platform engine";
/*     */   }
/*     */ 
/*     */   public String getAccount()
/*     */   {
/* 369 */     return this.accountName;
/*     */   }
/*     */ 
/*     */   public void mergeOrders(IOrder[] orders) throws JFException {
/* 373 */     NotificationUtilsProvider.getNotificationUtils().postWarningMessage("mergeOrders method is deprecated and will be removed later, please use method with label parameter instead", true);
/* 374 */     mergeOrders(null, orders);
/*     */   }
/*     */ 
/*     */   public IOrder mergeOrders(String label, IOrder[] orders) throws JFException {
/* 378 */     if (this.taskManager.isGlobal()) {
/* 379 */       throw new JFException("Merges are not supported on global accounts");
/*     */     }
/* 381 */     if (!this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/* 382 */       throw new JFException(JFException.Error.THREAD_INCORRECT);
/*     */     }
/* 384 */     Instrument instrument = null;
/* 385 */     Set mergeOrderGroupIdList = new HashSet();
/* 386 */     for (IOrder order : orders) {
/* 387 */       if (instrument == null) {
/* 388 */         instrument = order.getInstrument();
/*     */       }
/* 390 */       if (order.getInstrument() != instrument) {
/* 391 */         throw new JFException("Cannot merge orders with instruments not equal");
/*     */       }
/* 393 */       if (order.getState() != IOrder.State.FILLED) {
/* 394 */         throw new JFException("Cannot merge orders in state other than FILLED");
/*     */       }
/* 396 */       if (order.getStopLossPrice() != 0.0D) {
/* 397 */         throw new JFException("Cannot merge orders with stop loss");
/*     */       }
/* 399 */       if (order.getTakeProfitPrice() != 0.0D) {
/* 400 */         throw new JFException("Cannot merge orders with take profit");
/*     */       }
/* 402 */       PlatformOrderImpl platformOrder = (PlatformOrderImpl)order;
/* 403 */       if (mergeOrderGroupIdList.contains(platformOrder.groupId)) {
/* 404 */         throw new JFException(new StringBuilder().append("Order [").append(platformOrder.getLabel()).append("] appears more than once in list of orders to merge").toString());
/*     */       }
/* 406 */       mergeOrderGroupIdList.add(platformOrder.groupId);
/*     */ 
/* 408 */       createSignal(order, ISignal.Type.ORDER_MERGE);
/*     */     }
/* 410 */     if (mergeOrderGroupIdList.size() < 2) {
/* 411 */       throw new JFException("Cannot merge less then 2 orders");
/*     */     }
/* 413 */     if (label == null)
/* 414 */       label = JForexAPI.generateLabel();
/*     */     else {
/* 416 */       label = validateLabel(label);
/*     */     }
/*     */ 
/* 421 */     PlatformOrderImpl order = new PlatformOrderImpl(this.taskManager);
/* 422 */     order.label = label;
/* 423 */     order.instrument = instrument;
/* 424 */     order.setLastServerRequest(PlatformOrderImpl.ServerRequest.MERGE_TARGET);
/*     */ 
/* 426 */     if (this.mode.equals(IEngine.StrategyMode.INDEPENDENT))
/*     */     {
/* 428 */       this.taskManager.getOrdersInternalCollection().put(label, order, true);
/*     */ 
/* 430 */       TransportClient transportClient = this.taskManager.getTransportClient();
/* 431 */       MergePositionsMessage mpm = JForexAPI.merge(this.taskManager.getStrategyKey(), label, mergeOrderGroupIdList);
/* 432 */       if (LOGGER.isDebugEnabled()) {
/* 433 */         LOGGER.debug(new StringBuilder().append("Sending merge request [").append(mpm).append("]").toString());
/*     */       }
/* 435 */       ProtocolMessage submitResult = transportClient.controlRequest(mpm);
/* 436 */       if ((submitResult instanceof ErrorResponseMessage)) {
/* 437 */         ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/* 438 */         this.taskManager.onErrorMessage(error, order);
/*     */       }
/* 440 */       DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/* 441 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 442 */       StringBuilder strBuilder = new StringBuilder();
/* 443 */       strBuilder.append("Merge request with label ").append(label).append(" sent for positions: ");
/* 444 */       for (String mergeOrderGroupId : mergeOrderGroupIdList) {
/* 445 */         strBuilder.append(mergeOrderGroupId).append(", ");
/*     */       }
/* 447 */       strBuilder.setLength(strBuilder.length() - 2);
/* 448 */       strBuilder.append(" at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ");
/* 449 */       strBuilder.append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer");
/* 450 */       NotificationUtilsProvider.getNotificationUtils().postInfoMessage(strBuilder.toString());
/*     */     }
/*     */ 
/* 453 */     return order;
/*     */   }
/*     */ 
/*     */   public void closeOrders(IOrder[] orders) throws JFException
/*     */   {
/* 458 */     if (this.taskManager.isGlobal()) {
/* 459 */       throw new JFException("Cannot close orders on global accounts. Please open opposite order instead");
/*     */     }
/* 461 */     if (!this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/* 462 */       throw new JFException(JFException.Error.THREAD_INCORRECT);
/*     */     }
/*     */ 
/* 465 */     List groupsToClose = new ArrayList();
/* 466 */     for (IOrder order : orders) {
/* 467 */       if (order.getState() != IOrder.State.FILLED) {
/* 468 */         throw new JFException("Cannot mass close orders in state other than FILLED");
/*     */       }
/*     */ 
/* 471 */       createSignal(order, ISignal.Type.ORDER_CLOSE);
/*     */ 
/* 473 */       if (this.mode.equals(IEngine.StrategyMode.INDEPENDENT)) {
/* 474 */         groupsToClose.add(JForexAPI.generatePositionClosingOrder(order.getOrderCommand(), order.getInstrument(), ((PlatformOrderImpl)order).groupId, this.taskManager.getStrategyKey(), order.getLabel(), order.getAmount(), 0.0D, -1.0D, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID()));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 480 */     if (this.mode.equals(IEngine.StrategyMode.INDEPENDENT))
/*     */     {
/* 482 */       for (IOrder order : orders) {
/* 483 */         ((PlatformOrderImpl)order).setLastServerRequest(PlatformOrderImpl.ServerRequest.CLOSE);
/*     */       }
/*     */ 
/* 486 */       TransportClient transportClient = this.taskManager.getTransportClient();
/* 487 */       OrderGroupMessage ogm = JForexAPI.massClose(groupsToClose);
/* 488 */       if (LOGGER.isDebugEnabled()) {
/* 489 */         LOGGER.debug(new StringBuilder().append("Sending mass close request [").append(ogm).append("]").toString());
/*     */       }
/* 491 */       ProtocolMessage submitResult = transportClient.controlRequest(ogm);
/* 492 */       if ((submitResult instanceof ErrorResponseMessage)) {
/* 493 */         ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/* 494 */         this.taskManager.onMessage(new PlatformMessageImpl(error.getReason(), null, IMessage.Type.ORDER_CLOSE_REJECTED, FeedDataProvider.getDefaultInstance().getCurrentTime()));
/*     */       }
/* 496 */       DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/* 497 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 498 */       StringBuilder strBuilder = new StringBuilder("Closing request sent for positions: ");
/* 499 */       for (OrderMessage orderMessage : groupsToClose) {
/* 500 */         strBuilder.append(orderMessage.getOrderGroupId()).append(", ");
/*     */       }
/* 502 */       strBuilder.setLength(strBuilder.length() - 2);
/* 503 */       strBuilder.append(" at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ");
/* 504 */       strBuilder.append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer");
/* 505 */       NotificationUtilsProvider.getNotificationUtils().postInfoMessage(strBuilder.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void broadcast(String topic, String message) throws JFException
/*     */   {
/* 511 */     if ((topic == null) || (topic.isEmpty())) {
/* 512 */       throw new JFException("Illegal broadcast usage : topic is empty");
/*     */     }
/* 514 */     if (topic.length() > 100) {
/* 515 */       throw new JFException(new StringBuilder().append("Broadcast topic max length exceeded : ").append(topic.length()).append(" / ").append(100).toString());
/*     */     }
/*     */ 
/* 518 */     if ((message == null) || (message.isEmpty())) {
/* 519 */       throw new JFException("Illegal broadcast usage : message is empty");
/*     */     }
/* 521 */     if (message.length() > 1000) {
/* 522 */       throw new JFException(new StringBuilder().append("Broadcast message max length exceeded : ").append(message.length()).append(" / ").append(1000).toString());
/*     */     }
/*     */ 
/* 525 */     long period = System.currentTimeMillis() - this.lastBroadcastTime;
/* 526 */     if ((period >= 0L) && (period < BROADCAST_MIN_PERIOD)) {
/* 527 */       throw new JFException(new StringBuilder().append("Broadcast min period exceeded : ").append(period).append(" / ").append(BROADCAST_MIN_PERIOD).toString());
/*     */     }
/*     */ 
/* 530 */     this.lastBroadcastTime = System.currentTimeMillis();
/*     */     try {
/* 532 */       StrategyBroadcastMessage broadcastMessage = new StrategyBroadcastMessage();
/* 533 */       broadcastMessage.setTopic(topic);
/* 534 */       broadcastMessage.setMessage(message);
/* 535 */       broadcastMessage.setTransactionId(this.taskManager.getUID());
/* 536 */       LOGGER.debug(new StringBuilder().append("Sending broadcast message : ").append(broadcastMessage).toString());
/* 537 */       ProtocolMessage response = this.taskManager.getTransportClient().controlRequest(broadcastMessage);
/* 538 */       if ((response instanceof ErrorResponseMessage))
/* 539 */         throw new JFException(new StringBuilder().append("Unable to send broadcast message : ").append(((ErrorResponseMessage)response).getReason()).toString());
/*     */     }
/*     */     catch (Exception ex) {
/* 542 */       throw new JFException("Broadcast error", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String groupToOCO(IOrder order1, IOrder order2)
/*     */     throws JFException
/*     */   {
/* 551 */     if ((order1 == null) || (order2 == null)) {
/* 552 */       throw new JFException("OCO grouping failed. One of the orders is null");
/*     */     }
/*     */ 
/* 556 */     OrderGroupMessage ocoGroup = new OrderGroupMessage();
/* 557 */     ocoGroup.setIsOcoMerge(Boolean.valueOf(true));
/*     */ 
/* 559 */     Map ocoOrders = new HashMap();
/*     */ 
/* 561 */     PlatformOrderImpl pOrder1 = (PlatformOrderImpl)order1;
/* 562 */     PlatformOrderImpl pOrder2 = (PlatformOrderImpl)order2;
/*     */ 
/* 564 */     ocoOrders.put(pOrder1.getOpeningOrderId(), pOrder1.groupId);
/* 565 */     ocoOrders.put(pOrder2.getOpeningOrderId(), pOrder2.groupId);
/* 566 */     ocoGroup.setOcoOrders(ocoOrders);
/*     */ 
/* 568 */     ProtocolMessage response = this.taskManager.getTransportClient().controlRequest(ocoGroup);
/*     */ 
/* 571 */     StringBuilder str = new StringBuilder();
/* 572 */     str.append("Oco group: ").append(ocoGroup).append(" response: ").append(response.toString());
/* 573 */     NotificationUtilsProvider.getNotificationUtils().postInfoMessage(str.toString());
/*     */ 
/* 575 */     return str.toString();
/*     */   }
/*     */ 
/*     */   public String ungroupOCO(IOrder order)
/*     */     throws JFException
/*     */   {
/* 581 */     if (order == null) {
/* 582 */       throw new JFException("OCO ungrouping failed.Order is null");
/*     */     }
/*     */ 
/* 585 */     OrderGroupMessage ocoGroup = new OrderGroupMessage();
/*     */ 
/* 589 */     ocoGroup.setOrderGroupId(((PlatformOrderImpl)order).getOpeningOrderId());
/* 590 */     ocoGroup.setIsOcoMerge(Boolean.valueOf(true));
/*     */ 
/* 592 */     ProtocolMessage response = this.taskManager.getTransportClient().controlRequest(ocoGroup);
/*     */ 
/* 595 */     StringBuilder str = new StringBuilder();
/* 596 */     str.append("Oco ungroup: ").append(ocoGroup).append(" response: ").append(response.toString());
/* 597 */     NotificationUtilsProvider.getNotificationUtils().postInfoMessage(str.toString());
/*     */ 
/* 599 */     return str.toString();
/*     */   }
/*     */ 
/*     */   public JForexTaskManager getTaskManager() {
/* 603 */     return this.taskManager;
/*     */   }
/*     */ 
/*     */   public IEngine.StrategyMode getStrategyMode()
/*     */   {
/* 608 */     return this.mode;
/*     */   }
/*     */ 
/*     */   public void setStrategyMode(IEngine.StrategyMode mode)
/*     */   {
/* 613 */     this.mode = mode;
/*     */   }
/*     */ 
/*     */   public void createSignal(IOrder order, ISignal.Type type)
/*     */   {
/* 618 */     if (this.signalsProcessor != null)
/* 619 */       this.signalsProcessor.add(new SignalImpl(order, type));
/*     */   }
/*     */ 
/*     */   public ISignalsProcessor getSignalsProcessor()
/*     */   {
/* 624 */     return this.signalsProcessor;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.JForexEngineImpl
 * JD-Core Version:    0.6.0
 */