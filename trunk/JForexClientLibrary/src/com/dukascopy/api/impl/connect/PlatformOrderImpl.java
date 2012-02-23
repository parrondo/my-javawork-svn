/*      */ package com.dukascopy.api.impl.connect;
/*      */ 
/*      */ import com.dukascopy.api.IEngine;
/*      */ import com.dukascopy.api.IEngine.OrderCommand;
/*      */ import com.dukascopy.api.IEngine.StrategyMode;
/*      */ import com.dukascopy.api.IMessage;
/*      */ import com.dukascopy.api.IMessage.Type;
/*      */ import com.dukascopy.api.IOrder;
/*      */ import com.dukascopy.api.IOrder.State;
/*      */ import com.dukascopy.api.ISignal.Type;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.JFException.Error;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.impl.execution.ScienceWaitForUpdate;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.util.CurrencyConverter;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*      */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*      */ import com.dukascopy.transport.client.TransportClient;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.NotificationMessageCode;
/*      */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderState;
/*      */ import com.dukascopy.transport.common.model.type.PositionSide;
/*      */ import com.dukascopy.transport.common.model.type.StopDirection;
/*      */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*      */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.RoundingMode;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class PlatformOrderImpl
/*      */   implements IOrder, Cloneable, ScienceWaitForUpdate
/*      */ {
/*   56 */   private static final Logger LOGGER = LoggerFactory.getLogger(PlatformOrderImpl.class);
/*      */ 
/*   58 */   private JForexTaskManager taskManager = null;
/*      */   private IEngine engine;
/*   61 */   String comment = null;
/*      */ 
/*   63 */   Instrument instrument = null;
/*      */ 
/*   65 */   double requestedAmount = 0.0D;
/*      */ 
/*   67 */   double filledAmount = 0.0D;
/*      */ 
/*   69 */   double filledAmountInitial = 0.0D;
/*      */ 
/*   71 */   String label = null;
/*      */ 
/*   73 */   double tpPrice = 0.0D;
/*      */ 
/*   75 */   double slPrice = 0.0D;
/*      */ 
/*   77 */   OfferSide slSide = null;
/*      */ 
/*   79 */   double slTrailStep = 0.0D;
/*      */ 
/*   81 */   double openPrice = 0.0D;
/*      */ 
/*   83 */   double slippage = (0.0D / 0.0D);
/*      */ 
/*   85 */   double closePrice = 0.0D;
/*      */ 
/*   87 */   double commission = 0.0D;
/*      */ 
/*   89 */   private IOrder.State state = IOrder.State.CREATED;
/*      */ 
/*   91 */   String groupId = null;
/*      */ 
/*   93 */   String openingOrderId = null;
/*   94 */   String pendingOrderId = null;
/*      */   String parentOrderId;
/*   96 */   String slOrderId = null;
/*   97 */   String tpOrderId = null;
/*      */ 
/*   99 */   long goodTillTime = 0L;
/*      */ 
/*  101 */   long creationTime = 0L;
/*      */ 
/*  103 */   long fillTime = 0L;
/*      */ 
/*  105 */   long closeTime = 0L;
/*      */ 
/*  107 */   IEngine.OrderCommand orderCommand = null;
/*      */ 
/*  109 */   long localCreationTime = 0L;
/*      */ 
/*  111 */   private volatile boolean updated = true;
/*      */   private volatile IMessage updatedMessage;
/*      */   private boolean awaitingResubmit;
/*      */   private Map<String, OrderMessage> ordersToAttach;
/*  121 */   ServerRequest lastServerRequest = ServerRequest.NONE;
/*      */   private long stopLossChangeTime;
/*      */   private long takeProfitChangeTime;
/*      */   private long requestedAmountChangeTime;
/*      */   private long openPriceChangeTime;
/*      */   private long closeAttemptTime;
/*      */   private long goodTillTimeChangeTime;
/*  130 */   private static ResponseMessageGenerator responseMessageGenerator = new ResponseMessageGenerator();
/*      */ 
/*      */   public void setLastServerRequest(ServerRequest lastServerRequest) {
/*  133 */     this.lastServerRequest = lastServerRequest;
/*      */   }
/*      */ 
/*      */   public PlatformOrderImpl(JForexTaskManager taskManager) {
/*  137 */     this.taskManager = taskManager;
/*  138 */     this.localCreationTime = System.currentTimeMillis();
/*  139 */     this.engine = taskManager.getEngine();
/*      */   }
/*      */ 
/*      */   public PlatformOrderImpl(JForexTaskManager taskManager, IEngine engine) {
/*  143 */     this.taskManager = taskManager;
/*  144 */     this.localCreationTime = System.currentTimeMillis();
/*  145 */     this.engine = engine;
/*      */   }
/*      */ 
/*      */   public PlatformOrderImpl(JForexTaskManager taskManager, String comment, Instrument instrument, double requestedAmount, double filledAmount, String label, double tpPrice, double slPrice, OfferSide slSide, double slTrailStep, double openPrice, double closePrice, IOrder.State state, String groupId, String openingOrderId, String pendingOrderId, String slOrderId, String tpOrderId, long goodTillTime, long creationTime, long fillTime, long closeTime, IEngine.OrderCommand orderCommand, double commission) {
/*  149 */     this.taskManager = taskManager;
/*  150 */     this.engine = taskManager.getEngine();
/*  151 */     this.comment = comment;
/*  152 */     this.instrument = instrument;
/*  153 */     this.requestedAmount = requestedAmount;
/*  154 */     this.filledAmount = filledAmount;
/*  155 */     this.filledAmountInitial = filledAmount;
/*  156 */     this.label = label;
/*  157 */     this.tpPrice = tpPrice;
/*  158 */     this.slPrice = slPrice;
/*  159 */     this.slSide = slSide;
/*  160 */     this.slTrailStep = slTrailStep;
/*  161 */     this.openPrice = openPrice;
/*  162 */     this.closePrice = closePrice;
/*  163 */     this.state = state;
/*  164 */     this.groupId = groupId;
/*  165 */     this.openingOrderId = openingOrderId;
/*  166 */     this.pendingOrderId = pendingOrderId;
/*  167 */     this.slOrderId = slOrderId;
/*  168 */     this.tpOrderId = tpOrderId;
/*  169 */     this.goodTillTime = goodTillTime;
/*  170 */     this.creationTime = creationTime;
/*  171 */     this.fillTime = fillTime;
/*  172 */     this.closeTime = closeTime;
/*  173 */     this.orderCommand = orderCommand;
/*  174 */     this.localCreationTime = System.currentTimeMillis();
/*  175 */     this.commission = commission;
/*      */   }
/*      */ 
/*      */   public PlatformOrderImpl(JForexTaskManager taskManager, IEngine engine, String comment, Instrument instrument, double requestedAmount, double filledAmount, String label, double tpPrice, double slPrice, OfferSide slSide, double slTrailStep, double openPrice, double closePrice, IOrder.State state, String groupId, String openingOrderId, String pendingOrderId, String slOrderId, String tpOrderId, long goodTillTime, long creationTime, long fillTime, long closeTime, IEngine.OrderCommand orderCommand, double commission) {
/*  179 */     this.taskManager = taskManager;
/*  180 */     this.engine = engine;
/*  181 */     this.comment = comment;
/*  182 */     this.instrument = instrument;
/*  183 */     this.requestedAmount = requestedAmount;
/*  184 */     this.filledAmount = filledAmount;
/*  185 */     this.filledAmountInitial = filledAmount;
/*  186 */     this.label = label;
/*  187 */     this.tpPrice = tpPrice;
/*  188 */     this.slPrice = slPrice;
/*  189 */     this.slSide = slSide;
/*  190 */     this.slTrailStep = slTrailStep;
/*  191 */     this.openPrice = openPrice;
/*  192 */     this.closePrice = closePrice;
/*  193 */     this.state = state;
/*  194 */     this.groupId = groupId;
/*  195 */     this.openingOrderId = openingOrderId;
/*  196 */     this.pendingOrderId = pendingOrderId;
/*  197 */     this.slOrderId = slOrderId;
/*  198 */     this.tpOrderId = tpOrderId;
/*  199 */     this.goodTillTime = goodTillTime;
/*  200 */     this.creationTime = creationTime;
/*  201 */     this.fillTime = fillTime;
/*  202 */     this.closeTime = closeTime;
/*  203 */     this.orderCommand = orderCommand;
/*  204 */     this.localCreationTime = System.currentTimeMillis();
/*  205 */     this.commission = commission;
/*      */   }
/*      */ 
/*      */   public void setRequestedAmount(double amount) throws JFException
/*      */   {
/*  210 */     if (!this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/*  211 */       throw new JFException(JFException.Error.THREAD_INCORRECT);
/*      */     }
/*      */ 
/*  214 */     amount = StratUtils.round(amount, 8);
/*      */ 
/*  216 */     if ((amount > 0.0D) && (this.state != IOrder.State.OPENED)) {
/*  217 */       throw new JFException("Cannot change amount of created (not yet accepted), filled, closed or canceled order");
/*      */     }
/*  219 */     if ((this.orderCommand == IEngine.OrderCommand.PLACE_BID) || (this.orderCommand == IEngine.OrderCommand.PLACE_OFFER)) {
/*  220 */       throw new JFException("Cannot change amount of PLACE_BID or PLACE_OFFER");
/*      */     }
/*  222 */     if (amount == 0.0D) {
/*  223 */       if ((this.state != IOrder.State.FILLED) && (this.state != IOrder.State.OPENED)) {
/*  224 */         throw new JFException("Cannot cancel created (not yet accepted), closed or canceled order");
/*      */       }
/*  226 */       if ((this.state == IOrder.State.FILLED) && (this.pendingOrderId == null)) {
/*  227 */         throw new JFException("Cannot cancel pending part of the filled order when it doesn't have pending part");
/*      */       }
/*      */     }
/*      */ 
/*  231 */     if (this.requestedAmountChangeTime + 1000L > System.currentTimeMillis()) {
/*  232 */       String content = new StringBuilder().append("Position #").append(this.groupId).append(" ENTRY #").append(this.openingOrderId).append(" amount change REJECTED").append(", REASON: can't change requested amount more than once in a second").toString();
/*      */ 
/*  234 */       LOGGER.warn(content);
/*  235 */       NotificationUtilsProvider.getNotificationUtils().postInfoMessage(content, true);
/*  236 */       this.taskManager.onMessage(new PlatformMessageImpl(content, this, IMessage.Type.ORDER_CHANGED_REJECTED, FeedDataProvider.getDefaultInstance().getCurrentTime()));
/*      */ 
/*  238 */       return;
/*      */     }
/*  240 */     this.requestedAmountChangeTime = System.currentTimeMillis();
/*      */ 
/*  242 */     if (amount == 0.0D)
/*      */     {
/*  244 */       this.engine.createSignal(this, ISignal.Type.ORDER_CLOSE);
/*      */ 
/*  246 */       if (this.engine.getStrategyMode().equals(IEngine.StrategyMode.INDEPENDENT)) {
/*  247 */         this.lastServerRequest = ServerRequest.CANCEL_ORDER;
/*  248 */         TransportClient transportClient = this.taskManager.getTransportClient();
/*  249 */         OrderMessage om = JForexAPI.cancelOrder(this.taskManager.getStrategyKey(), this.label, this.instrument, getGroupId(), this.pendingOrderId, this.parentOrderId, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*      */ 
/*  251 */         if (LOGGER.isDebugEnabled()) {
/*  252 */           LOGGER.debug(new StringBuilder().append("Canceling pending order [").append(om).append("]").toString());
/*      */         }
/*  254 */         ProtocolMessage submitResult = transportClient.controlRequest(om);
/*  255 */         if ((submitResult instanceof ErrorResponseMessage)) {
/*  256 */           ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/*  257 */           this.taskManager.onErrorMessage(error, this);
/*      */         }
/*  259 */         DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/*  260 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  261 */         NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Cancelling order #").append(this.pendingOrderId).append(" ").append(this.orderCommand == IEngine.OrderCommand.PLACE_OFFER ? "PLACE OFFER" : this.orderCommand == IEngine.OrderCommand.PLACE_BID ? "PLACE BID" : new StringBuilder().append("ENTRY ").append(isLong() ? "BUY" : "SELL").toString()).append(" ").append(BigDecimal.valueOf(getRequestedAmount()).subtract(BigDecimal.valueOf(getAmount())).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(this.instrument).append(" @ ").append((this.orderCommand != IEngine.OrderCommand.PLACE_BID) && (this.orderCommand != IEngine.OrderCommand.PLACE_OFFER) ? new StringBuilder().append("LIMIT ").append(this.orderCommand.isLong() ? BigDecimal.valueOf(this.openPrice).add(BigDecimal.valueOf(this.slippage)).toPlainString() : BigDecimal.valueOf(this.openPrice).subtract(BigDecimal.valueOf(this.slippage)).toPlainString()).toString() : Double.isNaN(this.slippage) ? "MKT" : BigDecimal.valueOf(this.openPrice).toPlainString()).append((this.orderCommand == IEngine.OrderCommand.PLACE_BID) || (this.orderCommand == IEngine.OrderCommand.PLACE_OFFER) ? new StringBuilder().append(" EXPIRES:").append(this.goodTillTime == 0L ? "GTC" : format.format(Long.valueOf(this.goodTillTime))).toString() : new StringBuilder().append(" IF ").append((this.orderCommand == IEngine.OrderCommand.BUYLIMIT) || (this.orderCommand == IEngine.OrderCommand.BUYSTOP) || (this.orderCommand == IEngine.OrderCommand.SELLLIMIT_BYASK) || (this.orderCommand == IEngine.OrderCommand.SELLSTOP_BYASK) ? "ASK" : "BID").append(" ").append((this.orderCommand == IEngine.OrderCommand.BUYLIMIT) || (this.orderCommand == IEngine.OrderCommand.BUYLIMIT_BYBID) || (this.orderCommand == IEngine.OrderCommand.SELLSTOP) || (this.orderCommand == IEngine.OrderCommand.SELLSTOP_BYASK) ? "<=" : "=>").append(" ").append(BigDecimal.valueOf(this.openPrice).toPlainString()).toString()).append(" at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  276 */       this.engine.createSignal(this, ISignal.Type.ORDER_MODIFY);
/*      */ 
/*  278 */       if (this.engine.getStrategyMode().equals(IEngine.StrategyMode.INDEPENDENT)) {
/*  279 */         this.lastServerRequest = ServerRequest.SET_REQ_AMOUNT;
/*  280 */         TransportClient transportClient = this.taskManager.getTransportClient();
/*  281 */         OrderMessage om = JForexAPI.modifyAmount(this.instrument, this.label, getGroupId(), this.pendingOrderId, amount, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*  282 */         if (LOGGER.isDebugEnabled()) {
/*  283 */           LOGGER.debug(new StringBuilder().append("Changing amount of pending order [").append(om).append("]").toString());
/*      */         }
/*  285 */         ProtocolMessage submitResult = transportClient.controlRequest(om);
/*  286 */         if ((submitResult instanceof ErrorResponseMessage)) {
/*  287 */           ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/*  288 */           this.taskManager.onErrorMessage(error, this);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setOpenPrice(double price) throws JFException {
/*  295 */     if (!this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/*  296 */       throw new JFException(JFException.Error.THREAD_INCORRECT);
/*      */     }
/*  298 */     if ((this.state != IOrder.State.OPENED) && ((this.state != IOrder.State.FILLED) || (getAmount() == getRequestedAmount()))) {
/*  299 */       throw new JFException("Order not in OPENED or FILLED(partially) state");
/*      */     }
/*      */ 
/*  302 */     this.engine.createSignal(this, ISignal.Type.ORDER_MODIFY);
/*      */ 
/*  304 */     if (this.engine.getStrategyMode().equals(IEngine.StrategyMode.INDEPENDENT)) {
/*  305 */       if (this.openPriceChangeTime + 1000L > System.currentTimeMillis()) {
/*  306 */         String content = new StringBuilder().append("Position #").append(this.groupId).append(" ENTRY #").append(this.openingOrderId).append(" price stop change REJECTED").append(", REASON: can't change price more than once in a second").toString();
/*      */ 
/*  308 */         LOGGER.warn(content);
/*  309 */         NotificationUtilsProvider.getNotificationUtils().postInfoMessage(content, true);
/*  310 */         this.taskManager.onMessage(new PlatformMessageImpl(content, this, IMessage.Type.ORDER_CHANGED_REJECTED, FeedDataProvider.getDefaultInstance().getCurrentTime()));
/*      */ 
/*  312 */         return;
/*      */       }
/*  314 */       this.openPriceChangeTime = System.currentTimeMillis();
/*      */ 
/*  316 */       price = StratUtils.round(price, 7);
/*  317 */       this.lastServerRequest = ServerRequest.SET_OPEN_PRICE;
/*  318 */       TransportClient transportClient = this.taskManager.getTransportClient();
/*      */ 
/*  320 */       if ((this.orderCommand == IEngine.OrderCommand.PLACE_BID) || (this.orderCommand == IEngine.OrderCommand.PLACE_OFFER)) {
/*  321 */         OrderMessage om = JForexAPI.modifyBidOfferPrice(this.instrument, this.label, getGroupId(), this.openingOrderId, price, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*      */ 
/*  323 */         if (LOGGER.isDebugEnabled()) {
/*  324 */           LOGGER.debug(new StringBuilder().append("Changing price of bid/offer order [").append(om).append("]").toString());
/*      */         }
/*  326 */         ProtocolMessage submitResult = transportClient.controlRequest(om);
/*  327 */         if ((submitResult instanceof ErrorResponseMessage)) {
/*  328 */           ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/*  329 */           this.taskManager.onErrorMessage(error, this);
/*      */         }
/*      */       } else {
/*  332 */         OrderMessage om = JForexAPI.modifyPrice(this.taskManager.getStrategyKey(), this.label, this.instrument, getGroupId(), this.openingOrderId, price, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*      */ 
/*  334 */         if (LOGGER.isDebugEnabled()) {
/*  335 */           LOGGER.debug(new StringBuilder().append("Changing price of pending order [").append(om).append("]").toString());
/*      */         }
/*  337 */         ProtocolMessage submitResult = transportClient.controlRequest(om);
/*  338 */         if ((submitResult instanceof ErrorResponseMessage)) {
/*  339 */           ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/*  340 */           this.taskManager.onErrorMessage(error, this);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void close(double amount, double price, double slippage) throws JFException {
/*  347 */     if (!this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/*  348 */       throw new JFException(JFException.Error.THREAD_INCORRECT);
/*      */     }
/*      */ 
/*  351 */     if (((amount > 0.0D) || (price > 0.0D) || (slippage >= 0.0D)) && (getState() == IOrder.State.OPENED)) {
/*  352 */       throw new JFException(JFException.Error.ORDER_CANCEL_INCORRECT);
/*      */     }
/*      */ 
/*  355 */     this.engine.createSignal(this, ISignal.Type.ORDER_CLOSE);
/*      */ 
/*  357 */     if (this.engine.getStrategyMode().equals(IEngine.StrategyMode.INDEPENDENT)) {
/*  358 */       if (this.closeAttemptTime + 1000L > System.currentTimeMillis()) {
/*  359 */         String content = new StringBuilder().append("Position #").append(this.groupId).append(" order close REJECTED").append(", REASON: can't send request to fully close order more than once in a second").toString();
/*      */ 
/*  361 */         LOGGER.warn(content);
/*  362 */         NotificationUtilsProvider.getNotificationUtils().postInfoMessage(content, true);
/*  363 */         this.taskManager.onMessage(new PlatformMessageImpl(content, this, IMessage.Type.ORDER_CLOSE_REJECTED, FeedDataProvider.getDefaultInstance().getCurrentTime()));
/*      */ 
/*  365 */         return;
/*      */       }
/*      */ 
/*  368 */       if (Double.compare(amount, 0.0D) > 0) {
/*  369 */         if (Double.compare(getAmount(), 1.0E-006D) <= 0)
/*      */         {
/*  371 */           amount = 0.0D;
/*      */         }
/*      */         else {
/*  374 */           RoundingMode roundingMode = RoundingMode.HALF_EVEN;
/*  375 */           if (Double.compare(amount, 1.0E-006D) < 0) {
/*  376 */             roundingMode = RoundingMode.CEILING;
/*      */           }
/*  378 */           amount = new BigDecimal(amount).setScale(6, roundingMode).doubleValue();
/*      */         }
/*      */       }
/*  381 */       if ((amount == 0.0D) || (amount == getAmount()) || (getState() == IOrder.State.OPENED))
/*      */       {
/*  383 */         this.closeAttemptTime = System.currentTimeMillis();
/*      */       }
/*      */ 
/*  387 */       if (slippage < 0.0D) {
/*  388 */         slippage = 5.0D;
/*      */       }
/*      */ 
/*  392 */       slippage = StratUtils.round(slippage * this.instrument.getPipValue(), 7);
/*  393 */       price = StratUtils.round(price, 7);
/*      */ 
/*  395 */       if (getState() == IOrder.State.OPENED) {
/*  396 */         this.lastServerRequest = ServerRequest.CLOSE;
/*  397 */         TransportClient transportClient = this.taskManager.getTransportClient();
/*  398 */         OrderMessage om = JForexAPI.cancelOrder(this.taskManager.getStrategyKey(), this.label, this.instrument, getGroupId(), this.openingOrderId, this.parentOrderId, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*  399 */         if (LOGGER.isDebugEnabled()) {
/*  400 */           LOGGER.debug(new StringBuilder().append("Canceling pending order order [").append(om).append("]").toString());
/*      */         }
/*  402 */         ProtocolMessage submitResult = transportClient.controlRequest(om);
/*  403 */         if ((submitResult instanceof ErrorResponseMessage)) {
/*  404 */           ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/*  405 */           this.taskManager.onErrorMessage(error, this);
/*      */         }
/*      */ 
/*  408 */         DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/*  409 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  410 */         NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Cancelling order #").append(this.openingOrderId).append(" ").append(this.orderCommand == IEngine.OrderCommand.PLACE_OFFER ? "PLACE OFFER" : this.orderCommand == IEngine.OrderCommand.PLACE_BID ? "PLACE BID" : new StringBuilder().append("ENTRY ").append(isLong() ? "BUY" : "SELL").toString()).append(" ").append(BigDecimal.valueOf(getAmount()).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(this.instrument).append(" @ ").append((this.orderCommand != IEngine.OrderCommand.PLACE_BID) && (this.orderCommand != IEngine.OrderCommand.PLACE_OFFER) ? new StringBuilder().append("LIMIT ").append(this.orderCommand.isLong() ? BigDecimal.valueOf(this.openPrice).add(BigDecimal.valueOf(slippage)).toPlainString() : BigDecimal.valueOf(this.openPrice).subtract(BigDecimal.valueOf(slippage)).toPlainString()).toString() : Double.isNaN(slippage) ? "MKT" : BigDecimal.valueOf(this.openPrice).toPlainString()).append((this.orderCommand == IEngine.OrderCommand.PLACE_BID) || (this.orderCommand == IEngine.OrderCommand.PLACE_OFFER) ? new StringBuilder().append(" EXPIRES:").append(this.goodTillTime == 0L ? "GTC" : format.format(Long.valueOf(this.goodTillTime))).toString() : new StringBuilder().append(" IF ").append((this.orderCommand == IEngine.OrderCommand.BUYLIMIT) || (this.orderCommand == IEngine.OrderCommand.BUYSTOP) || (this.orderCommand == IEngine.OrderCommand.SELLLIMIT_BYASK) || (this.orderCommand == IEngine.OrderCommand.SELLSTOP_BYASK) ? "ASK" : "BID").append(" ").append((this.orderCommand == IEngine.OrderCommand.BUYLIMIT) || (this.orderCommand == IEngine.OrderCommand.BUYLIMIT_BYBID) || (this.orderCommand == IEngine.OrderCommand.SELLSTOP) || (this.orderCommand == IEngine.OrderCommand.SELLSTOP_BYASK) ? "<=" : "=>").append(" ").append(BigDecimal.valueOf(this.openPrice).toPlainString()).toString()).append(" at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*      */       }
/*  423 */       else if (getState() == IOrder.State.FILLED) {
/*  424 */         if ((amount == 0.0D) && (this.requestedAmount > this.filledAmount)) {
/*  425 */           TransportClient transportClient = this.taskManager.getTransportClient();
/*  426 */           OrderMessage om = JForexAPI.cancelOrder(this.taskManager.getStrategyKey(), this.label, this.instrument, getGroupId(), this.pendingOrderId, this.parentOrderId, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*  427 */           if (LOGGER.isDebugEnabled()) {
/*  428 */             LOGGER.debug(new StringBuilder().append("Canceling pending part of partially filled order [").append(om).append("]").toString());
/*      */           }
/*  430 */           ProtocolMessage submitResult = transportClient.controlRequest(om);
/*  431 */           if ((submitResult instanceof ErrorResponseMessage)) {
/*  432 */             ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/*  433 */             this.taskManager.onErrorMessage(error, this);
/*      */           }
/*  435 */           DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/*  436 */           format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  437 */           NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Cancelling order #").append(this.pendingOrderId).append(" ").append(this.orderCommand == IEngine.OrderCommand.PLACE_OFFER ? "PLACE OFFER" : this.orderCommand == IEngine.OrderCommand.PLACE_BID ? "PLACE BID" : new StringBuilder().append("ENTRY ").append(isLong() ? "BUY" : "SELL").toString()).append(" ").append(BigDecimal.valueOf(getRequestedAmount()).subtract(BigDecimal.valueOf(getAmount())).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(this.instrument).append(" @ ").append((this.orderCommand != IEngine.OrderCommand.PLACE_BID) && (this.orderCommand != IEngine.OrderCommand.PLACE_OFFER) ? new StringBuilder().append("LIMIT ").append(this.orderCommand.isLong() ? BigDecimal.valueOf(this.openPrice).add(BigDecimal.valueOf(slippage)).toPlainString() : BigDecimal.valueOf(this.openPrice).subtract(BigDecimal.valueOf(slippage)).toPlainString()).toString() : Double.isNaN(slippage) ? "MKT" : BigDecimal.valueOf(this.openPrice).toPlainString()).append((this.orderCommand == IEngine.OrderCommand.PLACE_BID) || (this.orderCommand == IEngine.OrderCommand.PLACE_OFFER) ? new StringBuilder().append(" EXPIRES:").append(this.goodTillTime == 0L ? "GTC" : format.format(Long.valueOf(this.goodTillTime))).toString() : new StringBuilder().append(" IF ").append((this.orderCommand == IEngine.OrderCommand.BUYLIMIT) || (this.orderCommand == IEngine.OrderCommand.BUYSTOP) || (this.orderCommand == IEngine.OrderCommand.SELLLIMIT_BYASK) || (this.orderCommand == IEngine.OrderCommand.SELLSTOP_BYASK) ? "ASK" : "BID").append(" ").append((this.orderCommand == IEngine.OrderCommand.BUYLIMIT) || (this.orderCommand == IEngine.OrderCommand.BUYLIMIT_BYBID) || (this.orderCommand == IEngine.OrderCommand.SELLSTOP) || (this.orderCommand == IEngine.OrderCommand.SELLSTOP_BYASK) ? "<=" : "=>").append(" ").append(BigDecimal.valueOf(this.openPrice).toPlainString()).toString()).append(" at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*      */         }
/*  450 */         else if (this.taskManager.isGlobal()) {
/*  451 */           throw new JFException("Cannot close orders on global accounts. Please open opposite order instead");
/*      */         }
/*      */ 
/*  454 */         if (!this.taskManager.isGlobal()) {
/*  455 */           this.lastServerRequest = ServerRequest.CLOSE;
/*  456 */           TransportClient transportClient = this.taskManager.getTransportClient();
/*  457 */           OrderMessage om = JForexAPI.closePosition(getOrderCommand(), getInstrument(), getGroupId(), this.taskManager.getStrategyKey(), this.label, amount == 0.0D ? getAmount() : amount, price, slippage, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*      */ 
/*  459 */           if (LOGGER.isDebugEnabled()) {
/*  460 */             LOGGER.debug(new StringBuilder().append("Closing order [").append(om).append("]").toString());
/*      */           }
/*  462 */           ProtocolMessage submitResult = transportClient.controlRequest(om);
/*  463 */           if ((submitResult instanceof ErrorResponseMessage)) {
/*  464 */             ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/*  465 */             this.taskManager.onErrorMessage(error, this);
/*      */           }
/*  467 */           DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/*  468 */           format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  469 */           NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Closing order ").append(isLong() ? "SELL" : "BUY").append(" ").append(BigDecimal.valueOf(getAmount()).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(this.instrument).append(" @ MKT").append(" is sent at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  475 */         throw new JFException(JFException.Error.ORDER_STATE_IMMUTABLE, new StringBuilder().append(" state is ").append(getState()).toString());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void close(double amount, double price) throws JFException
/*      */   {
/*  482 */     close(amount, price, -1.0D);
/*      */   }
/*      */ 
/*      */   public void close(double amount) throws JFException {
/*  486 */     close(amount, 0.0D);
/*      */   }
/*      */ 
/*      */   public void close() throws JFException {
/*  490 */     close(0.0D);
/*      */   }
/*      */ 
/*      */   public double getAmount() {
/*  494 */     if ((this.state == IOrder.State.CREATED) || (this.state == IOrder.State.CANCELED) || (this.state == IOrder.State.OPENED))
/*  495 */       return this.requestedAmount;
/*  496 */     if (this.state == IOrder.State.CLOSED) {
/*  497 */       return this.filledAmountInitial;
/*      */     }
/*      */ 
/*  500 */     return this.filledAmount;
/*      */   }
/*      */ 
/*      */   public double getClosePrice()
/*      */   {
/*  505 */     return this.closePrice;
/*      */   }
/*      */ 
/*      */   public String getComment() {
/*  509 */     return this.comment;
/*      */   }
/*      */ 
/*      */   public long getCreationTime() {
/*  513 */     return this.creationTime;
/*      */   }
/*      */ 
/*      */   public long getCloseTime() {
/*  517 */     return this.closeTime;
/*      */   }
/*      */ 
/*      */   public long getFillTime() {
/*  521 */     return this.fillTime;
/*      */   }
/*      */ 
/*      */   public Instrument getInstrument() {
/*  525 */     return this.instrument;
/*      */   }
/*      */ 
/*      */   public String getLabel() {
/*  529 */     return this.label;
/*      */   }
/*      */ 
/*      */   public double getOpenPrice() {
/*  533 */     return this.openPrice;
/*      */   }
/*      */ 
/*      */   public IOrder.State getState() {
/*  537 */     return this.state;
/*      */   }
/*      */ 
/*      */   public void setState(IOrder.State state) {
/*  541 */     this.state = state;
/*      */   }
/*      */ 
/*      */   public double getStopLossPrice() {
/*  545 */     return this.slPrice;
/*      */   }
/*      */ 
/*      */   public double getTakeProfitPrice() {
/*  549 */     return this.tpPrice;
/*      */   }
/*      */ 
/*      */   public OfferSide getStopLossSide() {
/*  553 */     return this.slSide;
/*      */   }
/*      */ 
/*      */   public double getTrailingStep() {
/*  557 */     return this.slTrailStep;
/*      */   }
/*      */ 
/*      */   public String getId() {
/*  561 */     return this.groupId;
/*      */   }
/*      */ 
/*      */   private String getGroupId() {
/*  565 */     if (this.taskManager.isGlobal()) {
/*  566 */       return new StringBuilder().append(this.taskManager.getUserId()).append(this.instrument.toString()).toString();
/*      */     }
/*  568 */     return this.groupId;
/*      */   }
/*      */ 
/*      */   public double getRequestedAmount()
/*      */   {
/*  573 */     return this.requestedAmount;
/*      */   }
/*      */ 
/*      */   public IEngine.OrderCommand getOrderCommand() {
/*  577 */     return this.orderCommand;
/*      */   }
/*      */ 
/*      */   public boolean isLong() {
/*  581 */     return (this.orderCommand != null) && (this.orderCommand.isLong());
/*      */   }
/*      */ 
/*      */   public void setGoodTillTime(long goodTillTime) throws JFException {
/*  585 */     if (!this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/*  586 */       throw new JFException(JFException.Error.THREAD_INCORRECT);
/*      */     }
/*  588 */     if ((this.orderCommand != IEngine.OrderCommand.PLACE_BID) && (this.orderCommand != IEngine.OrderCommand.PLACE_OFFER)) {
/*  589 */       throw new JFException("Order should be \"place bid\" or \"place offer\"");
/*      */     }
/*  591 */     if ((this.state != IOrder.State.OPENED) && ((this.state != IOrder.State.FILLED) || (getAmount() == getRequestedAmount()))) {
/*  592 */       throw new JFException("Order not in OPENED or FILLED(partially) state");
/*      */     }
/*  594 */     if (goodTillTime < 0L) {
/*  595 */       throw new JFException(JFException.Error.INVALID_GTT);
/*      */     }
/*  597 */     if ((goodTillTime > 0L) && (goodTillTime < 63072000000L))
/*      */     {
/*  599 */       throw new JFException(JFException.Error.INVALID_GTT);
/*      */     }
/*      */ 
/*  602 */     if (this.goodTillTimeChangeTime + 1000L > System.currentTimeMillis()) {
/*  603 */       String content = new StringBuilder().append("Position #").append(this.groupId).append(" ENTRY #").append(this.openingOrderId).append(" good till time change REJECTED").append(", REASON: can't change good till time more than once in a second").toString();
/*      */ 
/*  605 */       LOGGER.warn(content);
/*  606 */       NotificationUtilsProvider.getNotificationUtils().postInfoMessage(content, true);
/*  607 */       this.taskManager.onMessage(new PlatformMessageImpl(content, this, IMessage.Type.ORDER_CHANGED_REJECTED, FeedDataProvider.getDefaultInstance().getCurrentTime()));
/*      */ 
/*  609 */       return;
/*      */     }
/*  611 */     this.goodTillTimeChangeTime = System.currentTimeMillis();
/*      */ 
/*  613 */     this.lastServerRequest = ServerRequest.SET_EXPIRATION;
/*  614 */     TransportClient transportClient = this.taskManager.getTransportClient();
/*  615 */     OrderMessage om = JForexAPI.modifyGTT(this.instrument, this.label, getGroupId(), this.openingOrderId, goodTillTime > 0L ? goodTillTime : -1L, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*  616 */     if (LOGGER.isDebugEnabled()) {
/*  617 */       LOGGER.debug(new StringBuilder().append("Changing expiration time of bid/offer order [").append(om).append("]").toString());
/*      */     }
/*  619 */     ProtocolMessage protocolMessage = transportClient.controlRequest(om);
/*  620 */     if ((protocolMessage instanceof ErrorResponseMessage)) {
/*  621 */       ErrorResponseMessage error = (ErrorResponseMessage)protocolMessage;
/*  622 */       this.taskManager.onErrorMessage(error, this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getGoodTillTime() {
/*  627 */     return this.goodTillTime;
/*      */   }
/*      */ 
/*      */   public void setStopLossPrice(double price) throws JFException {
/*  631 */     setStopLossPrice(price, isLong() ? OfferSide.BID : OfferSide.ASK, this.slTrailStep);
/*      */   }
/*      */ 
/*      */   public void setStopLossPrice(double price, OfferSide side) throws JFException {
/*  635 */     setStopLossPrice(price, side, this.slTrailStep);
/*      */   }
/*      */ 
/*      */   public void setStopLossPrice(double price, OfferSide side, double trailingStepInPips) throws JFException {
/*  639 */     if ((this.taskManager.isGlobal()) && (price > 0.0D)) {
/*  640 */       throw new JFException("Stop loss orders are not allowed on global accounts");
/*      */     }
/*  642 */     if (!this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/*  643 */       throw new JFException(JFException.Error.THREAD_INCORRECT);
/*      */     }
/*  645 */     if ((this.state != IOrder.State.FILLED) && (this.state != IOrder.State.OPENED)) {
/*  646 */       throw new JFException(JFException.Error.ORDER_STATE_IMMUTABLE);
/*      */     }
/*  648 */     if ((trailingStepInPips != 0.0D) && (trailingStepInPips < 10.0D)) {
/*  649 */       throw new JFException("Trailing step must be >= 10 or equals to 0 (cancel)");
/*      */     }
/*      */ 
/*  652 */     if (this.stopLossChangeTime + 1000L > System.currentTimeMillis()) {
/*  653 */       String content = new StringBuilder().append("Order REJECTED: STOP LOSS ").append(isLong() ? "SELL" : "BUY").append(" ").append(this.instrument).append(" @MKT IF ").append(side.name()).append(" ").append(isLong() ? "<=" : "=>").append(" ").append(price).append(" - Position #").append(this.groupId).append(", REASON: can't change stop loss price more than once in a second").toString();
/*      */ 
/*  656 */       LOGGER.warn(content);
/*  657 */       NotificationUtilsProvider.getNotificationUtils().postInfoMessage(content, true);
/*  658 */       this.taskManager.onMessage(new PlatformMessageImpl(content, this, IMessage.Type.ORDER_CHANGED_REJECTED, FeedDataProvider.getDefaultInstance().getCurrentTime()));
/*      */ 
/*  660 */       return;
/*      */     }
/*  662 */     this.stopLossChangeTime = System.currentTimeMillis();
/*      */ 
/*  664 */     price = StratUtils.round(price, 7);
/*  665 */     trailingStepInPips = StratUtils.round(trailingStepInPips, 7);
/*      */ 
/*  667 */     if (price > 0.0D)
/*      */     {
/*  669 */       this.engine.createSignal(this, ISignal.Type.ORDER_MODIFY);
/*      */ 
/*  671 */       if (this.engine.getStrategyMode().equals(IEngine.StrategyMode.INDEPENDENT)) {
/*  672 */         this.lastServerRequest = ServerRequest.SET_SL;
/*      */ 
/*  674 */         double absoluteTrailingPrice = 0.0D;
/*  675 */         if (trailingStepInPips > 0.0D) {
/*  676 */           absoluteTrailingPrice = StratUtils.round(trailingStepInPips * this.instrument.getPipValue(), 7);
/*      */         }
/*  678 */         if (this.slPrice > 0.0D)
/*      */         {
/*  680 */           TransportClient transportClient = this.taskManager.getTransportClient();
/*  681 */           OrderMessage om = JForexAPI.modifyStopLoss(this.taskManager.getStrategyKey(), this.label, this.instrument, getGroupId(), this.slOrderId, isLong(), price, side, absoluteTrailingPrice, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*      */ 
/*  683 */           if (LOGGER.isDebugEnabled()) {
/*  684 */             LOGGER.debug(new StringBuilder().append("Modifying stop loss order [").append(om).append("]").toString());
/*      */           }
/*  686 */           ProtocolMessage protocolMessage = transportClient.controlRequest(om);
/*  687 */           if ((protocolMessage instanceof ErrorResponseMessage)) {
/*  688 */             ErrorResponseMessage error = (ErrorResponseMessage)protocolMessage;
/*  689 */             this.taskManager.onErrorMessage(error, this);
/*      */           }
/*      */         } else {
/*  692 */           TransportClient transportClient = this.taskManager.getTransportClient();
/*  693 */           OrderMessage om = JForexAPI.addStopLoss(this.taskManager.getStrategyKey(), this.label, getGroupId(), this.openingOrderId, this.instrument, price, getAmount(), absoluteTrailingPrice, isLong(), side, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*      */ 
/*  695 */           if (LOGGER.isDebugEnabled()) {
/*  696 */             LOGGER.debug(new StringBuilder().append("Submitting stop loss order [").append(om).append("]").toString());
/*      */           }
/*  698 */           ProtocolMessage protocolMessage = transportClient.controlRequest(om);
/*  699 */           if ((protocolMessage instanceof ErrorResponseMessage)) {
/*  700 */             ErrorResponseMessage error = (ErrorResponseMessage)protocolMessage;
/*  701 */             this.taskManager.onErrorMessage(error, this);
/*      */           }
/*  703 */           DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/*  704 */           format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  705 */           NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Order STOP LOSS ").append(isLong() ? "SELL" : "BUY").append(" ").append(BigDecimal.valueOf(getAmount()).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(this.instrument).append(" @ MKT IF ").append(side == null ? "ASK" : isLong() ? "BID" : side.name()).append(isLong() ? " <=" : " =>").append(" ").append(BigDecimal.valueOf(price).toPlainString()).append(" is sent at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*  716 */     else if (this.slOrderId != null)
/*      */     {
/*  718 */       this.engine.createSignal(this, ISignal.Type.ORDER_CANCEL);
/*      */ 
/*  720 */       if (this.engine.getStrategyMode().equals(IEngine.StrategyMode.INDEPENDENT)) {
/*  721 */         TransportClient transportClient = this.taskManager.getTransportClient();
/*  722 */         OrderMessage om = JForexAPI.cancelOrder(this.taskManager.getStrategyKey(), this.label, this.instrument, getGroupId(), this.slOrderId, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*  723 */         if (LOGGER.isDebugEnabled()) {
/*  724 */           LOGGER.debug(new StringBuilder().append("Canceling stop loss order [").append(om).append("]").toString());
/*      */         }
/*  726 */         ProtocolMessage protocolMessage = transportClient.controlRequest(om);
/*  727 */         if ((protocolMessage instanceof ErrorResponseMessage)) {
/*  728 */           ErrorResponseMessage error = (ErrorResponseMessage)protocolMessage;
/*  729 */           this.taskManager.onErrorMessage(error, this);
/*      */         }
/*  731 */         DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/*  732 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  733 */         NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Canceling order #").append(this.slOrderId).append(" STOP LOSS ").append(isLong() ? "SELL" : "BUY").append(" ").append(BigDecimal.valueOf(getAmount()).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(this.instrument).append(" @ MKT IF ").append(this.slSide.name()).append(isLong() ? " <=" : " =>").append(" ").append(BigDecimal.valueOf(this.slPrice).toPlainString()).append(" at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTakeProfitPrice(double price)
/*      */     throws JFException
/*      */   {
/*  745 */     if ((this.taskManager.isGlobal()) && (price > 0.0D)) {
/*  746 */       throw new JFException("Stop loss orders are not allowed on global accounts");
/*      */     }
/*  748 */     if (!this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/*  749 */       throw new JFException(JFException.Error.THREAD_INCORRECT);
/*      */     }
/*  751 */     if ((this.state != IOrder.State.FILLED) && (this.state != IOrder.State.OPENED)) {
/*  752 */       throw new JFException(JFException.Error.ORDER_STATE_IMMUTABLE);
/*      */     }
/*      */ 
/*  755 */     if (this.takeProfitChangeTime + 1000L > System.currentTimeMillis()) {
/*  756 */       String content = new StringBuilder().append("Order REJECTED: TAKE PROFIT ").append(isLong() ? "SELL" : "BUY").append(" ").append(this.instrument).append(" @MKT IF ").append(isLong() ? "BID" : "ASK").append(" ").append(isLong() ? "=>" : "<=").append(" ").append(price).append(" - Position #").append(this.groupId).append(", REASON: can't change take profit price more than once in a second").toString();
/*      */ 
/*  759 */       LOGGER.warn(content);
/*  760 */       NotificationUtilsProvider.getNotificationUtils().postInfoMessage(content, true);
/*  761 */       this.taskManager.onMessage(new PlatformMessageImpl(content, this, IMessage.Type.ORDER_CHANGED_REJECTED, FeedDataProvider.getDefaultInstance().getCurrentTime()));
/*      */ 
/*  763 */       return;
/*      */     }
/*  765 */     this.takeProfitChangeTime = System.currentTimeMillis();
/*      */ 
/*  767 */     price = StratUtils.round(price, 7);
/*  768 */     TransportClient transportClient = this.taskManager.getTransportClient();
/*  769 */     if (price > 0.0D)
/*      */     {
/*  771 */       this.engine.createSignal(this, ISignal.Type.ORDER_MODIFY);
/*      */ 
/*  773 */       if (this.engine.getStrategyMode().equals(IEngine.StrategyMode.INDEPENDENT)) {
/*  774 */         this.lastServerRequest = ServerRequest.SET_TP;
/*  775 */         if (this.tpPrice > 0.0D) {
/*  776 */           OrderMessage om = JForexAPI.modifyPrice(this.taskManager.getStrategyKey(), this.label, this.instrument, getGroupId(), this.tpOrderId, price, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*      */ 
/*  778 */           if (LOGGER.isDebugEnabled()) {
/*  779 */             LOGGER.debug(new StringBuilder().append("Modifying take profit order [").append(om).append("]").toString());
/*      */           }
/*  781 */           ProtocolMessage protocolMessage = transportClient.controlRequest(om);
/*  782 */           if ((protocolMessage instanceof ErrorResponseMessage)) {
/*  783 */             ErrorResponseMessage error = (ErrorResponseMessage)protocolMessage;
/*  784 */             this.taskManager.onErrorMessage(error, this);
/*      */           }
/*      */         }
/*      */         else {
/*  788 */           OrderMessage om = JForexAPI.addTakeProfit(this.taskManager.getStrategyKey(), this.label, getGroupId(), this.openingOrderId, this.instrument, price, getAmount(), isLong(), this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*      */ 
/*  790 */           if (LOGGER.isDebugEnabled()) {
/*  791 */             LOGGER.debug(new StringBuilder().append("Submitting take profit order [").append(om).append("]").toString());
/*      */           }
/*  793 */           ProtocolMessage protocolMessage = transportClient.controlRequest(om);
/*  794 */           if ((protocolMessage instanceof ErrorResponseMessage)) {
/*  795 */             ErrorResponseMessage error = (ErrorResponseMessage)protocolMessage;
/*  796 */             this.taskManager.onErrorMessage(error, this);
/*      */           }
/*  798 */           DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/*  799 */           format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  800 */           NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Order TAKE PROFIT ").append(isLong() ? "SELL" : "BUY").append(" ").append(BigDecimal.valueOf(getAmount()).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(this.instrument).append(" @ LIMIT IF ").append(isLong() ? "BID =>" : "ASK <=").append(" ").append(BigDecimal.valueOf(price).toPlainString()).append(" is sent at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*  808 */     else if (this.tpOrderId != null) {
/*  809 */       this.engine.createSignal(this, ISignal.Type.ORDER_CANCEL);
/*      */ 
/*  811 */       if (this.engine.getStrategyMode().equals(IEngine.StrategyMode.INDEPENDENT)) {
/*  812 */         OrderMessage om = JForexAPI.cancelOrder(this.taskManager.getStrategyKey(), this.label, this.instrument, getGroupId(), this.tpOrderId, this.taskManager.getExternalIP(), this.taskManager.getInternalIP(), this.taskManager.getSessionID());
/*  813 */         if (LOGGER.isDebugEnabled()) {
/*  814 */           LOGGER.debug(new StringBuilder().append("Canceling take profit order [").append(om).append("]").toString());
/*      */         }
/*  816 */         ProtocolMessage protocolMessage = transportClient.controlRequest(om);
/*  817 */         if ((protocolMessage instanceof ErrorResponseMessage)) {
/*  818 */           ErrorResponseMessage error = (ErrorResponseMessage)protocolMessage;
/*  819 */           this.taskManager.onErrorMessage(error, this);
/*      */         }
/*  821 */         DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/*  822 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  823 */         NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Cancelling order #").append(this.tpOrderId).append(" TAKE PROFIT ").append(isLong() ? "SELL" : "BUY").append(" ").append(BigDecimal.valueOf(getAmount()).multiply(BigDecimal.valueOf(1000000L)).stripTrailingZeros().toPlainString()).append(" ").append(this.instrument).append(" @ LIMIT IF ").append(isLong() ? "BID =>" : "ASK <=").append(" ").append(BigDecimal.valueOf(this.slPrice).toPlainString()).append(" at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" by the strategy \"").append(this.taskManager.getStrategyName()).append("\": from the ").append(this.taskManager.getEnvironment() == JForexTaskManager.Environment.REMOTE ? "remote server" : "local computer").toString());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isStopLoss(OrderMessage orderMessage)
/*      */   {
/*  841 */     if ((OrderDirection.CLOSE == orderMessage.getOrderDirection()) && (null != orderMessage.getPriceStop())) {
/*  842 */       if (OrderSide.SELL == orderMessage.getSide()) {
/*  843 */         return (StopDirection.ASK_LESS == orderMessage.getStopDirection()) || (StopDirection.BID_LESS == orderMessage.getStopDirection());
/*      */       }
/*  845 */       return (StopDirection.ASK_GREATER == orderMessage.getStopDirection()) || (StopDirection.BID_GREATER == orderMessage.getStopDirection());
/*      */     }
/*      */ 
/*  848 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean isTakeProfit(OrderMessage orderMessage)
/*      */   {
/*  859 */     if ((OrderDirection.CLOSE == orderMessage.getOrderDirection()) && (null != orderMessage.getPriceStop())) {
/*  860 */       if (OrderSide.BUY == orderMessage.getSide()) {
/*  861 */         return (StopDirection.ASK_LESS == orderMessage.getStopDirection()) || (StopDirection.BID_LESS == orderMessage.getStopDirection());
/*      */       }
/*  863 */       return (StopDirection.ASK_GREATER == orderMessage.getStopDirection()) || (StopDirection.BID_GREATER == orderMessage.getStopDirection());
/*      */     }
/*      */ 
/*  866 */     return false;
/*      */   }
/*      */ 
/*      */   public Map<String, OrderMessage> getOrdersToAttach()
/*      */   {
/*  872 */     return this.ordersToAttach;
/*      */   }
/*      */ 
/*      */   public String getOpeningOrderId() {
/*  876 */     return this.openingOrderId;
/*      */   }
/*      */ 
/*      */   public PlatformMessageImpl update(OrderGroupMessage groupMessage) {
/*  880 */     if (LOGGER.isDebugEnabled()) {
/*  881 */       LOGGER.debug(new StringBuilder().append("processing OrderGroupMessage [").append(groupMessage).append("]").toString());
/*      */     }
/*      */     try
/*      */     {
/*  885 */       groupMessage = new OrderGroupMessage(new ProtocolMessage(groupMessage.toString()));
/*      */     } catch (ParseException e) {
/*  887 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */ 
/*  891 */     if (this.label == null) {
/*  892 */       this.label = extractLabel(groupMessage);
/*      */     }
/*      */ 
/*  895 */     OrderMessage openingOrder = null;
/*      */ 
/*  897 */     OrderMessage slOrder = null;
/*      */ 
/*  899 */     OrderMessage tpOrder = null;
/*  900 */     PlatformMessageImpl platformMessageImpl = null;
/*      */ 
/*  902 */     this.filledAmount = 0.0D;
/*  903 */     this.pendingOrderId = null;
/*      */ 
/*  905 */     List ordersList = groupMessage.getOrders();
/*  906 */     this.groupId = groupMessage.getOrderGroupId();
/*      */ 
/*  909 */     Money orderGroupCommission = groupMessage.getOrderGroupCommission();
/*      */ 
/*  911 */     if (orderGroupCommission != null) {
/*  912 */       if (!this.taskManager.getAccountCurrency().equals(orderGroupCommission.getCurrency()))
/*  913 */         LOGGER.error("Order commission currency {} differs from current account's currency {}", orderGroupCommission.getCurrency(), this.taskManager.getAccountCurrency());
/*      */       else {
/*  915 */         this.commission = orderGroupCommission.getValue().doubleValue();
/*      */       }
/*      */     }
/*      */ 
/*  919 */     if (!ordersList.isEmpty())
/*      */     {
/*  921 */       Collections.sort(ordersList, new Comparator()
/*      */       {
/*      */         public int compare(OrderMessage o1, OrderMessage o2) {
/*  924 */           int i1 = 0;
/*  925 */           int i2 = 0;
/*  926 */           OrderState orderState = o1.getOrderState();
/*  927 */           if (o1.getOrderDirection() == OrderDirection.OPEN) {
/*  928 */             if (orderState == OrderState.PENDING)
/*  929 */               i1 = 1;
/*  930 */             else if (orderState == OrderState.EXECUTING)
/*  931 */               i1 = 2;
/*  932 */             else if (orderState == OrderState.FILLED)
/*  933 */               i1 = 3;
/*      */           }
/*      */           else {
/*  936 */             i1 = 4;
/*      */           }
/*  938 */           orderState = o2.getOrderState();
/*  939 */           if (o2.getOrderDirection() == OrderDirection.OPEN) {
/*  940 */             if (orderState == OrderState.PENDING)
/*  941 */               i2 = 1;
/*  942 */             else if (orderState == OrderState.EXECUTING)
/*  943 */               i2 = 2;
/*  944 */             else if (orderState == OrderState.FILLED)
/*  945 */               i2 = 3;
/*      */           }
/*      */           else {
/*  948 */             i2 = 4;
/*      */           }
/*  950 */           return i1 <= i2 ? -1 : 1;
/*      */         }
/*      */       });
/*  953 */       groupMessage.setOrders(ordersList);
/*  954 */       if (this.taskManager.isGlobal())
/*      */       {
/*  956 */         for (OrderMessage orderMessage : groupMessage.getOrders()) {
/*  957 */           OrderState orderState = orderMessage.getOrderState();
/*  958 */           if (orderMessage.getOrderDirection() == OrderDirection.OPEN) {
/*  959 */             if (orderState == OrderState.FILLED)
/*      */             {
/*  961 */               BigDecimal amountPriceSum = BigDecimal.ZERO;
/*  962 */               BigDecimal amountTotal = BigDecimal.ZERO;
/*  963 */               BigDecimal price = orderMessage.getPriceClient().getValue();
/*  964 */               if ((this.ordersToAttach != null) && (!this.ordersToAttach.isEmpty())) {
/*  965 */                 for (OrderMessage filledMessage : this.ordersToAttach.values()) {
/*  966 */                   amountPriceSum = amountPriceSum.add(filledMessage.getPriceClient().getValue().multiply(filledMessage.getAmount().getValue()));
/*  967 */                   amountTotal = amountTotal.add(filledMessage.getAmount().getValue());
/*      */                 }
/*  969 */                 amountPriceSum = amountPriceSum.add(orderMessage.getPriceClient().getValue().multiply(orderMessage.getAmount().getValue()));
/*  970 */                 price = amountPriceSum.divide(amountTotal.add(orderMessage.getAmount().getValue()), 6, 6);
/*      */               }
/*      */ 
/*  973 */               if (this.ordersToAttach == null) {
/*  974 */                 this.ordersToAttach = new HashMap();
/*      */               }
/*  976 */               if (!this.ordersToAttach.containsKey(orderMessage.getOrderId())) {
/*  977 */                 this.ordersToAttach.put(orderMessage.getOrderId(), (OrderMessage)ProtocolMessage.parse(orderMessage.toProtocolString()));
/*      */               }
/*  979 */               orderMessage.setPriceClient(new Money(price, this.instrument.getPrimaryCurrency()));
/*      */ 
/*  981 */               orderMessage.setAmount(new Money(orderMessage.getAmount().getValue().add(amountTotal), this.instrument.getPrimaryCurrency()));
/*  982 */               orderMessage.setOrigAmount(new Money(orderMessage.getOrigAmount().getValue().add(amountTotal), this.instrument.getPrimaryCurrency()));
/*      */ 
/*  984 */               IEngine.OrderCommand orderMessageOrderCommand = detectOrderCommand(orderMessage);
/*  985 */               if ((orderMessageOrderCommand != IEngine.OrderCommand.BUY) && (orderMessageOrderCommand != IEngine.OrderCommand.SELL) && (orderMessage.getOrigAmount().getValue().subtract(orderMessage.getAmount().getValue()).compareTo(BigDecimal.ZERO) > 0)) {
/*  986 */                 OrderMessage pendingMessage = (OrderMessage)ProtocolMessage.parse(orderMessage.toProtocolString());
/*  987 */                 pendingMessage.setAmount(new Money(orderMessage.getOrigAmount().getValue().subtract(orderMessage.getAmount().getValue()), this.instrument.getPrimaryCurrency()));
/*  988 */                 if (orderMessageOrderCommand.isConditional())
/*  989 */                   pendingMessage.setOrderState(OrderState.PENDING);
/*      */                 else {
/*  991 */                   pendingMessage.setOrderState(OrderState.EXECUTING);
/*      */                 }
/*      */ 
/*  994 */                 pendingMessage.setOrderId(this.openingOrderId);
/*  995 */                 pendingMessage.put("fake_part", true);
/*  996 */                 List newOrders = new ArrayList();
/*  997 */                 newOrders.addAll(groupMessage.getOrders());
/*  998 */                 newOrders.add(pendingMessage);
/*  999 */                 groupMessage.setOrders(newOrders);
/*      */               }
/* 1001 */             } else if ((this.ordersToAttach != null) && (!this.ordersToAttach.isEmpty()))
/*      */             {
/* 1003 */               BigDecimal amountPriceSum = BigDecimal.ZERO;
/* 1004 */               BigDecimal amountTotal = BigDecimal.ZERO;
/* 1005 */               OrderMessage lastFilledOrder = null;
/* 1006 */               for (OrderMessage filledMessage : this.ordersToAttach.values()) {
/* 1007 */                 amountPriceSum = amountPriceSum.add(filledMessage.getPriceClient().getValue().multiply(filledMessage.getAmount().getValue()));
/* 1008 */                 amountTotal = amountTotal.add(filledMessage.getAmount().getValue());
/* 1009 */                 lastFilledOrder = filledMessage;
/*      */               }
/* 1011 */               if (lastFilledOrder != null) {
/* 1012 */                 BigDecimal price = amountPriceSum.divide(amountTotal, 6, 6);
/* 1013 */                 lastFilledOrder = (OrderMessage)ProtocolMessage.parse(lastFilledOrder.toProtocolString());
/* 1014 */                 lastFilledOrder.setPriceClient(new Money(price, this.instrument.getPrimaryCurrency()));
/* 1015 */                 lastFilledOrder.setAmount(new Money(amountTotal, this.instrument.getPrimaryCurrency()));
/* 1016 */                 lastFilledOrder.setOrigAmount(new Money(amountTotal, this.instrument.getPrimaryCurrency()));
/* 1017 */                 List newOrders = new ArrayList();
/* 1018 */                 newOrders.add(lastFilledOrder);
/* 1019 */                 newOrders.addAll(groupMessage.getOrders());
/* 1020 */                 groupMessage.setOrders(newOrders);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1027 */       double calculatedRequestedAmount = 0.0D;
/* 1028 */       for (OrderMessage orderMessage : groupMessage.getOrders()) {
/* 1029 */         OrderState orderState = orderMessage.getOrderState();
/* 1030 */         if (orderMessage.getOrderDirection() == OrderDirection.OPEN) {
/* 1031 */           openingOrder = orderMessage;
/* 1032 */           calculatedRequestedAmount += Double.parseDouble(orderMessage.getString("amount"));
/*      */ 
/* 1034 */           this.slippage = (orderMessage.getPriceTrailingLimit() == null ? (0.0D / 0.0D) : orderMessage.getPriceTrailingLimit().getValue().doubleValue());
/*      */ 
/* 1036 */           if (orderState == OrderState.EXECUTING) {
/* 1037 */             if (this.state == IOrder.State.CREATED) {
/* 1038 */               this.state = IOrder.State.OPENED;
/* 1039 */               this.creationTime = orderMessage.getCreatedDate().getTime();
/* 1040 */               String text = orderMessage.getNotes();
/* 1041 */               if (this.orderCommand == null)
/*      */               {
/* 1043 */                 this.orderCommand = detectOrderCommand(orderMessage);
/*      */               }
/* 1045 */               if (this.orderCommand.isConditional())
/* 1046 */                 this.openPrice = Double.parseDouble(orderMessage.getString("priceStop"));
/* 1047 */               else if ((this.orderCommand == IEngine.OrderCommand.PLACE_BID) || (this.orderCommand == IEngine.OrderCommand.PLACE_OFFER)) {
/* 1048 */                 this.openPrice = openingOrder.getPriceClient().getValue().doubleValue();
/*      */               }
/*      */ 
/* 1051 */               platformMessageImpl = new PlatformMessageImpl(text, this, IMessage.Type.ORDER_SUBMIT_OK, this.creationTime);
/* 1052 */               LOGGER.debug("transiting order from CREATED state to OPENED as a response to EXECUTING order update message");
/* 1053 */               this.closeAttemptTime = 0L;
/* 1054 */               if ((this.orderCommand != IEngine.OrderCommand.BUY) && (this.orderCommand != IEngine.OrderCommand.SELL))
/*      */               {
/* 1056 */                 this.lastServerRequest = ServerRequest.NONE;
/*      */               }
/* 1058 */               if (orderMessage.isPlaceOffer())
/* 1059 */                 this.pendingOrderId = orderMessage.getOrderId();
/*      */             }
/* 1061 */             else if ((this.state == IOrder.State.OPENED) && (orderMessage.isPlaceOffer()))
/*      */             {
/* 1063 */               if ((this.taskManager.isGlobal()) && (this.openingOrderId != null) && (this.openingOrderId.equals(orderMessage.getOrderId())) && (!orderMessage.getBoolean("fake_part")))
/*      */               {
/* 1066 */                 this.filledAmount = this.filledAmountInitial;
/* 1067 */                 this.pendingOrderId = orderMessage.getOrderId();
/* 1068 */                 return null;
/* 1069 */               }if ((!this.taskManager.isGlobal()) && (groupMessage.getOrders().size() == 1) && (this.openingOrderId != null) && (this.openingOrderId.equals(orderMessage.getOrderId())) && (Math.abs(Double.parseDouble(orderMessage.getString("amount")) - this.requestedAmount) > 1.0E-006D))
/*      */               {
/* 1073 */                 this.filledAmount = this.filledAmountInitial;
/* 1074 */                 this.pendingOrderId = orderMessage.getOrderId();
/* 1075 */                 return null;
/*      */               }
/* 1077 */               long newGoodTillTime = 0L;
/* 1078 */               if (openingOrder.getExecTimeoutMillis() != null) {
/* 1079 */                 newGoodTillTime = openingOrder.getExecTimeoutMillis().longValue();
/*      */               }
/* 1081 */               double newOpenPrice = this.openPrice;
/* 1082 */               if ((openingOrder.getPriceClient() != null) && (openingOrder.getPriceClient().getValue() != null)) {
/* 1083 */                 newOpenPrice = openingOrder.getPriceClient().getValue().doubleValue();
/*      */               }
/*      */ 
/* 1087 */               if ((newGoodTillTime != this.goodTillTime) || (newOpenPrice != this.openPrice)) {
/* 1088 */                 String text = orderMessage.getNotes();
/* 1089 */                 if (orderMessage.getTimestamp() == null) {
/* 1090 */                   LOGGER.warn("order update doesn't have timestamp set");
/*      */                 }
/* 1092 */                 platformMessageImpl = new PlatformMessageImpl(text, this, IMessage.Type.ORDER_CHANGED_OK, orderMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : orderMessage.getTimestamp().getTime());
/* 1093 */                 this.goodTillTime = newGoodTillTime;
/* 1094 */                 this.openPrice = newOpenPrice;
/* 1095 */                 LOGGER.debug("updating order as a response to EXECUTING order update message");
/*      */               } else {
/* 1097 */                 LOGGER.debug("ignoring EXECUTING order update message, nothing changed");
/*      */               }
/* 1099 */               switch (2.$SwitchMap$com$dukascopy$api$impl$connect$PlatformOrderImpl$ServerRequest[this.lastServerRequest.ordinal()]) {
/*      */               case 1:
/*      */               case 2:
/*      */               case 3:
/*      */               case 4:
/*      */               case 5:
/*      */               case 6:
/* 1106 */                 this.lastServerRequest = ServerRequest.NONE;
/*      */               }
/* 1108 */               this.pendingOrderId = orderMessage.getOrderId();
/*      */             } else {
/* 1110 */               if ((this.state == IOrder.State.FILLED) && (orderMessage.isPlaceOffer()) && (this.taskManager.isGlobal()) && (this.openingOrderId != null) && (this.openingOrderId.equals(orderMessage.getOrderId())) && (!orderMessage.getBoolean("fake_part")))
/*      */               {
/* 1113 */                 this.filledAmount = this.filledAmountInitial;
/* 1114 */                 this.pendingOrderId = orderMessage.getOrderId();
/* 1115 */                 return null;
/* 1116 */               }if ((this.state == IOrder.State.FILLED) && (orderMessage.isPlaceOffer()) && (groupMessage.getOrders().size() == 1) && (!this.taskManager.isGlobal()) && (this.openingOrderId != null) && (this.openingOrderId.equals(orderMessage.getOrderId())) && (Math.abs(Double.parseDouble(orderMessage.getString("amount")) - (this.requestedAmount - this.filledAmountInitial)) > 1.0E-006D))
/*      */               {
/* 1121 */                 this.filledAmount = this.filledAmountInitial;
/* 1122 */                 this.pendingOrderId = orderMessage.getOrderId();
/* 1123 */                 return null;
/* 1124 */               }if (this.state == IOrder.State.FILLED)
/* 1125 */                 this.pendingOrderId = orderMessage.getOrderId();
/*      */             }
/* 1127 */           } else if (orderState == OrderState.FILLED)
/*      */           {
/* 1132 */             this.filledAmount += Double.parseDouble(orderMessage.getString("amount"));
/*      */ 
/* 1134 */             if (this.state == IOrder.State.OPENED) {
/* 1135 */               String text = orderMessage.getNotes();
/* 1136 */               this.fillTime = orderMessage.getTimestamp().getTime();
/* 1137 */               platformMessageImpl = new PlatformMessageImpl(text, this, IMessage.Type.ORDER_FILL_OK, this.fillTime);
/* 1138 */               LOGGER.debug("transiting order from OPENED to FILLED state as a response to FILLED order update message");
/* 1139 */             } else if ((this.state == IOrder.State.CREATED) && ((this.lastServerRequest == ServerRequest.MERGE_TARGET) || (groupMessage.isOcoMerge())))
/*      */             {
/* 1141 */               this.creationTime = orderMessage.getTimestamp().getTime();
/* 1142 */               this.fillTime = orderMessage.getTimestamp().getTime();
/* 1143 */               LOGGER.debug("transiting order from CREATED merge target to FILLED state as a response to FILLED order update message");
/* 1144 */             } else if (this.state == IOrder.State.CREATED)
/*      */             {
/* 1146 */               String text = orderMessage.getNotes();
/* 1147 */               this.creationTime = orderMessage.getCreatedDate().getTime();
/* 1148 */               this.fillTime = orderMessage.getTimestamp().getTime();
/* 1149 */               platformMessageImpl = new PlatformMessageImpl(text, this, IMessage.Type.ORDER_CHANGED_OK, this.fillTime);
/* 1150 */               LOGGER.debug("updating new order with data from FILLED order update message");
/*      */             }
/* 1154 */             else if (this.filledAmountInitial > this.filledAmount)
/*      */             {
/* 1156 */               if (groupMessage.getTimestamp() != null)
/* 1157 */                 this.closeTime = groupMessage.getTimestamp().getTime();
/*      */               else {
/* 1159 */                 LOGGER.warn("partial close message doesn't have timestamp set");
/*      */               }
/* 1161 */               if (groupMessage.getPriceOpen() != null) {
/* 1162 */                 this.closePrice = groupMessage.getPriceOpen().getValue().doubleValue();
/*      */               }
/* 1164 */               String text = orderMessage.getNotes();
/* 1165 */               platformMessageImpl = new PlatformMessageImpl(text, this, IMessage.Type.ORDER_CLOSE_OK, groupMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : groupMessage.getTimestamp().getTime());
/* 1166 */               LOGGER.debug("partially closing order as a response to FILLED order update message");
/*      */             }
/* 1169 */             else if ((this.filledAmountInitial < this.filledAmount) || (this.openPrice != orderMessage.getPriceClient().getValue().doubleValue()))
/*      */             {
/* 1171 */               this.fillTime = orderMessage.getTimestamp().getTime();
/* 1172 */               String text = orderMessage.getNotes();
/* 1173 */               platformMessageImpl = new PlatformMessageImpl(text, this, IMessage.Type.ORDER_CHANGED_OK, this.fillTime);
/* 1174 */               LOGGER.debug("updating new order with data from FILLED order update message");
/*      */             }
/*      */ 
/* 1179 */             switch (2.$SwitchMap$com$dukascopy$api$impl$connect$PlatformOrderImpl$ServerRequest[this.lastServerRequest.ordinal()]) {
/*      */             case 1:
/*      */             case 2:
/*      */             case 3:
/*      */             case 4:
/*      */             case 5:
/*      */             case 6:
/* 1186 */               this.lastServerRequest = ServerRequest.NONE;
/*      */             }
/*      */ 
/* 1189 */             this.filledAmountInitial = this.filledAmount;
/* 1190 */             this.openPrice = orderMessage.getPriceClient().getValue().doubleValue();
/* 1191 */             this.state = IOrder.State.FILLED;
/* 1192 */             this.closeAttemptTime = 0L;
/* 1193 */           } else if (orderState == OrderState.PENDING) {
/* 1194 */             if (this.state == IOrder.State.CREATED) {
/* 1195 */               this.state = IOrder.State.OPENED;
/* 1196 */               this.creationTime = orderMessage.getCreatedDate().getTime();
/* 1197 */               this.openPrice = Double.parseDouble(orderMessage.getString("priceStop"));
/* 1198 */               String text = orderMessage.getNotes();
/*      */ 
/* 1200 */               platformMessageImpl = new PlatformMessageImpl(text, this, IMessage.Type.ORDER_SUBMIT_OK, this.creationTime);
/* 1201 */               LOGGER.debug("transiting order from CREATED to OPENED state as a response to PENDING order update message");
/* 1202 */               this.closeAttemptTime = 0L;
/* 1203 */             } else if (this.state == IOrder.State.OPENED)
/*      */             {
/* 1205 */               double newOpenPrice = Double.parseDouble(orderMessage.getString("priceStop"));
/* 1206 */               if (newOpenPrice != this.openPrice) {
/* 1207 */                 this.openPrice = newOpenPrice;
/* 1208 */                 String text = orderMessage.getNotes();
/* 1209 */                 if (orderMessage.getTimestamp() == null) {
/* 1210 */                   LOGGER.warn("pending order update doesn't have timestamp set");
/*      */                 }
/* 1212 */                 platformMessageImpl = new PlatformMessageImpl(text, this, IMessage.Type.ORDER_CHANGED_OK, orderMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : orderMessage.getTimestamp().getTime());
/*      */ 
/* 1214 */                 LOGGER.debug("updating OPENED order with data from PENDING order update message");
/*      */               }
/* 1216 */             } else if ((this.state == IOrder.State.FILLED) && (groupMessage.getOrders().size() == 1))
/*      */             {
/* 1218 */               if (this.filledAmountInitial > this.filledAmount)
/*      */               {
/* 1220 */                 if (groupMessage.getTimestamp() != null)
/* 1221 */                   this.closeTime = groupMessage.getTimestamp().getTime();
/*      */                 else {
/* 1223 */                   LOGGER.warn("filled partial close message doesn't have timestamp set");
/*      */                 }
/* 1225 */                 if (groupMessage.getPriceOpen() != null) {
/* 1226 */                   this.closePrice = groupMessage.getPriceOpen().getValue().doubleValue();
/*      */                 }
/* 1228 */                 String text = orderMessage.getNotes();
/* 1229 */                 platformMessageImpl = new PlatformMessageImpl(text, this, IMessage.Type.ORDER_CLOSE_OK, groupMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : groupMessage.getTimestamp().getTime());
/* 1230 */                 this.closeAttemptTime = 0L;
/* 1231 */                 LOGGER.debug("closing filled part of the order that also has pending part as a response to group update with only PENDING part");
/* 1232 */                 this.state = IOrder.State.OPENED;
/* 1233 */                 this.openPrice = Double.parseDouble(orderMessage.getString("priceStop"));
/*      */               }
/*      */             }
/* 1236 */             switch (2.$SwitchMap$com$dukascopy$api$impl$connect$PlatformOrderImpl$ServerRequest[this.lastServerRequest.ordinal()]) {
/*      */             case 1:
/*      */             case 2:
/*      */             case 3:
/*      */             case 4:
/*      */             case 5:
/*      */             case 6:
/* 1243 */               this.lastServerRequest = ServerRequest.NONE;
/*      */             }
/* 1245 */             this.pendingOrderId = orderMessage.getOrderId();
/* 1246 */             this.parentOrderId = orderMessage.getParentOrderId();
/* 1247 */           } else if ((orderState == OrderState.REJECTED) && (this.taskManager.isGlobal())) {
/* 1248 */             if (this.awaitingResubmit)
/*      */             {
/* 1250 */               this.awaitingResubmit = false;
/*      */             } else {
/* 1252 */               PlatformOrderImpl orderImpl = this.taskManager.getOrdersInternalCollection().removeById(this.groupId);
/* 1253 */               if (orderImpl == null) {
/* 1254 */                 this.taskManager.getOrdersInternalCollection().removeByLabel(this.label);
/*      */               }
/* 1256 */               if (getState() != IOrder.State.CANCELED)
/*      */               {
/* 1258 */                 if (orderMessage.getTimestamp() != null)
/* 1259 */                   this.closeTime = orderMessage.getTimestamp().getTime();
/*      */                 else {
/* 1261 */                   LOGGER.warn("rejected message doesn't have timestamp set");
/*      */                 }
/* 1263 */                 if ((this.state == IOrder.State.OPENED) && (!this.orderCommand.isConditional()) && (this.orderCommand != IEngine.OrderCommand.PLACE_BID) && (this.orderCommand != IEngine.OrderCommand.PLACE_OFFER))
/*      */                 {
/* 1265 */                   this.state = IOrder.State.CANCELED;
/* 1266 */                   LOGGER.debug("transiting order to CANCELED state as a response to order update message");
/* 1267 */                   platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDER_FILL_REJECTED, orderMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : orderMessage.getTimestamp().getTime());
/* 1268 */                 } else if (this.state == IOrder.State.FILLED)
/*      */                 {
/* 1270 */                   LOGGER.debug("canceling PENDING part as a response to order update message");
/* 1271 */                   calculatedRequestedAmount -= Double.parseDouble(orderMessage.getString("amount"));
/* 1272 */                   platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDER_CHANGED_OK, orderMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : orderMessage.getTimestamp().getTime());
/*      */                 } else {
/* 1274 */                   this.state = IOrder.State.CANCELED;
/* 1275 */                   LOGGER.debug("transiting order to CANCELED state as a response to order update message");
/* 1276 */                   platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDER_CLOSE_OK, orderMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : orderMessage.getTimestamp().getTime());
/* 1277 */                   this.closeAttemptTime = 0L;
/*      */                 }
/* 1279 */                 this.lastServerRequest = ServerRequest.NONE;
/*      */               }
/*      */             }
/* 1282 */           } else if ((orderState == OrderState.CANCELLED) && (this.taskManager.isGlobal()) && (!orderMessage.isBidOfferCancellReplace().booleanValue()))
/*      */           {
/* 1284 */             PlatformOrderImpl orderImpl = this.taskManager.getOrdersInternalCollection().removeById(this.groupId);
/* 1285 */             if (orderImpl == null) {
/* 1286 */               this.taskManager.getOrdersInternalCollection().removeByLabel(this.label);
/*      */             }
/* 1288 */             if (getState() != IOrder.State.CANCELED)
/*      */             {
/* 1290 */               if (orderMessage.getTimestamp() != null)
/* 1291 */                 this.closeTime = orderMessage.getTimestamp().getTime();
/*      */               else {
/* 1293 */                 LOGGER.warn("cancel message doesn't have timestamp set");
/*      */               }
/* 1295 */               LOGGER.debug("transiting order to CANCELED state as a response to order update message");
/* 1296 */               if ((this.state == IOrder.State.OPENED) && (!this.orderCommand.isConditional()) && (this.orderCommand != IEngine.OrderCommand.PLACE_BID) && (this.orderCommand != IEngine.OrderCommand.PLACE_OFFER))
/*      */               {
/* 1298 */                 this.state = IOrder.State.CANCELED;
/* 1299 */                 LOGGER.debug("transiting order to CANCELED state as a response to order update message");
/* 1300 */                 platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDER_FILL_REJECTED, orderMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : orderMessage.getTimestamp().getTime());
/* 1301 */               } else if (this.state == IOrder.State.FILLED)
/*      */               {
/* 1303 */                 LOGGER.debug("canceling PENDING part as a response to order update message");
/* 1304 */                 calculatedRequestedAmount -= Double.parseDouble(orderMessage.getString("amount"));
/* 1305 */                 platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDER_CHANGED_OK, orderMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : orderMessage.getTimestamp().getTime());
/*      */               } else {
/* 1307 */                 this.state = IOrder.State.CANCELED;
/* 1308 */                 LOGGER.debug("transiting order to CANCELED state as a response to order update message");
/* 1309 */                 platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDER_CLOSE_OK, orderMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : orderMessage.getTimestamp().getTime());
/* 1310 */                 this.closeAttemptTime = 0L;
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1316 */           if (this.state == IOrder.State.FILLED) {
/* 1317 */             this.orderCommand = (orderMessage.getSide() == OrderSide.BUY ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL);
/*      */           } else {
/* 1319 */             this.orderCommand = detectOrderCommand(orderMessage);
/*      */ 
/* 1322 */             if (this.orderCommand == null) {
/* 1323 */               this.orderCommand = (orderMessage.getSide() == OrderSide.BUY ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/* 1328 */         else if ((orderMessage.getOrderDirection() == OrderDirection.CLOSE) && 
/* 1329 */           (orderState == OrderState.EXECUTING))
/*      */         {
/* 1332 */           platformMessageImpl = null;
/* 1333 */           LOGGER.debug("closing in progress, do nothing");
/*      */         }
/*      */ 
/* 1342 */         if (isStopLoss(orderMessage)) {
/* 1343 */           slOrder = orderMessage;
/*      */         }
/* 1345 */         if (isTakeProfit(orderMessage)) {
/* 1346 */           tpOrder = orderMessage;
/*      */         }
/*      */       }
/*      */ 
/* 1350 */       if ((this.requestedAmount > calculatedRequestedAmount) && (platformMessageImpl == null))
/*      */       {
/* 1352 */         LOGGER.debug("canceling PENDING part as a response to order update message");
/* 1353 */         if (groupMessage.getTimestamp() == null) {
/* 1354 */           LOGGER.warn("order group message doesn't have timestamp set");
/*      */         }
/* 1356 */         platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDER_CHANGED_OK, groupMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : groupMessage.getTimestamp().getTime());
/*      */       }
/*      */ 
/* 1359 */       this.requestedAmount = StratUtils.round(calculatedRequestedAmount, 8);
/*      */ 
/* 1361 */       if (openingOrder != null) {
/* 1362 */         this.openingOrderId = openingOrder.getOrderId();
/* 1363 */         this.parentOrderId = openingOrder.getParentOrderId();
/*      */ 
/* 1366 */         if (openingOrder.getTag() != null) {
/* 1367 */           this.comment = openingOrder.getTag();
/*      */         }
/* 1369 */         this.instrument = Instrument.fromString(openingOrder.getInstrument());
/*      */ 
/* 1372 */         String notes = null;
/*      */         double newTpPrice;
/* 1373 */         if (tpOrder != null) {
/* 1374 */           double newTpPrice = tpOrder.getPriceStop().getValue().doubleValue();
/* 1375 */           this.tpOrderId = tpOrder.getOrderId();
/* 1376 */           if (notes == null)
/* 1377 */             notes = tpOrder.getNotes();
/*      */         }
/*      */         else {
/* 1380 */           newTpPrice = 0.0D;
/* 1381 */           this.tpOrderId = null;
/*      */         }
/*      */         double newSlPrice;
/*      */         OfferSide newSlSide;
/*      */         double newSlTrailStep;
/* 1387 */         if (slOrder != null) {
/* 1388 */           double newSlPrice = slOrder.getPriceStop().getValue().doubleValue();
/* 1389 */           this.slOrderId = slOrder.getOrderId();
/* 1390 */           StopDirection slStopDirection = slOrder.getStopDirection();
/*      */           OfferSide newSlSide;
/*      */           OfferSide newSlSide;
/* 1391 */           if ((slStopDirection == StopDirection.ASK_EQUALS) || (slStopDirection == StopDirection.ASK_GREATER) || (slStopDirection == StopDirection.ASK_LESS))
/* 1392 */             newSlSide = OfferSide.ASK;
/*      */           else
/* 1394 */             newSlSide = OfferSide.BID;
/*      */           double newSlTrailStep;
/* 1396 */           if (slOrder.has("priceLimit")) {
/* 1397 */             double newSlTrailStep = Double.parseDouble(slOrder.getString("priceLimit"));
/* 1398 */             newSlTrailStep /= this.instrument.getPipValue();
/*      */           } else {
/* 1400 */             newSlTrailStep = 0.0D;
/*      */           }
/* 1402 */           if (notes == null)
/* 1403 */             notes = slOrder.getNotes();
/*      */         }
/*      */         else {
/* 1406 */           newSlPrice = 0.0D;
/* 1407 */           this.slOrderId = null;
/* 1408 */           newSlSide = null;
/* 1409 */           newSlTrailStep = 0.0D;
/*      */         }
/*      */ 
/* 1412 */         if ((newTpPrice != this.tpPrice) || (newSlPrice != this.slPrice) || (newSlSide != this.slSide) || (newSlTrailStep != this.slTrailStep)) {
/* 1413 */           this.tpPrice = newTpPrice;
/* 1414 */           this.slPrice = newSlPrice;
/* 1415 */           this.slSide = newSlSide;
/* 1416 */           this.slTrailStep = newSlTrailStep;
/*      */ 
/* 1418 */           if (platformMessageImpl == null) {
/* 1419 */             if (groupMessage.getTimestamp() == null) {
/* 1420 */               LOGGER.warn("order group message doesn't have timestamp set");
/*      */             }
/* 1422 */             platformMessageImpl = new PlatformMessageImpl(notes, this, IMessage.Type.ORDER_CHANGED_OK, groupMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : groupMessage.getTimestamp().getTime());
/* 1423 */             LOGGER.debug("updating order because of changes in sl/tp");
/*      */           }
/*      */         }
/*      */ 
/* 1427 */         if ((openingOrder.isPlaceOffer()) && (openingOrder.getExecTimeoutMillis() != null) && (this.goodTillTime != openingOrder.getExecTimeoutMillis().longValue())) {
/* 1428 */           this.goodTillTime = openingOrder.getExecTimeoutMillis().longValue();
/* 1429 */           if (platformMessageImpl == null) {
/* 1430 */             if (groupMessage.getTimestamp() == null) {
/* 1431 */               LOGGER.warn("order group message doesn't have timestamp set");
/*      */             }
/* 1433 */             platformMessageImpl = new PlatformMessageImpl(openingOrder.getNotes(), this, IMessage.Type.ORDER_CHANGED_OK, groupMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : groupMessage.getTimestamp().getTime());
/* 1434 */             LOGGER.debug("updating order because of changes in gtt");
/*      */           }
/*      */         }
/*      */       }
/* 1438 */     } else if ((this.lastServerRequest == ServerRequest.MERGE_TARGET) || ((this.state == IOrder.State.CREATED) && (groupMessage.isOcoMerge()) && (groupMessage.getAmount() != null) && (groupMessage.getAmount().getValue().compareTo(BigDecimal.ZERO) == 0)))
/*      */     {
/* 1442 */       this.instrument = Instrument.fromString(groupMessage.getInstrument());
/* 1443 */     } else if (this.taskManager.isGlobal()) {
/* 1444 */       this.label = groupMessage.getOrderGroupId();
/* 1445 */       double oldPrice = this.openPrice;
/* 1446 */       IEngine.OrderCommand oldOrderCommand = this.orderCommand;
/* 1447 */       this.orderCommand = (groupMessage.getSide() == PositionSide.SHORT ? IEngine.OrderCommand.SELL : IEngine.OrderCommand.BUY);
/* 1448 */       if (groupMessage.getAmount() != null)
/* 1449 */         this.filledAmount = Double.parseDouble(groupMessage.getString("amount"));
/*      */       else {
/* 1451 */         this.filledAmount = 0.0D;
/*      */       }
/* 1453 */       this.requestedAmount = this.filledAmount;
/* 1454 */       this.openPrice = groupMessage.getPricePosOpen().getValue().doubleValue();
/* 1455 */       this.instrument = Instrument.fromString(groupMessage.getInstrument());
/*      */ 
/* 1457 */       if ((oldOrderCommand != this.orderCommand) || (this.filledAmount != this.filledAmountInitial) || (oldPrice != this.openPrice)) {
/* 1458 */         this.fillTime = groupMessage.getTimestamp().getTime();
/* 1459 */         platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDER_CHANGED_OK, this.fillTime);
/* 1460 */         LOGGER.debug("updating global position with data from order group update message");
/* 1461 */         this.lastServerRequest = ServerRequest.NONE;
/*      */       }
/*      */ 
/* 1464 */       this.filledAmountInitial = this.filledAmount;
/* 1465 */       this.state = IOrder.State.FILLED;
/* 1466 */       if (this.filledAmount == 0.0D)
/*      */       {
/* 1468 */         this.taskManager.getOrdersInternalCollection().removeById(this.groupId);
/*      */       }
/*      */     } else {
/* 1471 */       PlatformOrderImpl orderImpl = this.taskManager.getOrdersInternalCollection().removeById(this.groupId);
/* 1472 */       if (orderImpl == null) {
/* 1473 */         orderImpl = this.taskManager.getOrdersInternalCollection().removeByLabel(this.label);
/*      */       }
/* 1475 */       if ((orderImpl != null) && (orderImpl.getState() != IOrder.State.CANCELED))
/*      */       {
/* 1477 */         if (groupMessage.getTimestamp() != null)
/* 1478 */           this.closeTime = groupMessage.getTimestamp().getTime();
/*      */         else {
/* 1480 */           LOGGER.warn("order group message doesn't have timestamp set");
/*      */         }
/* 1482 */         if (groupMessage.getPriceOpen() != null) {
/* 1483 */           this.closePrice = groupMessage.getPriceOpen().getValue().doubleValue();
/*      */         }
/* 1485 */         IOrder.State previousState = this.state;
/* 1486 */         if (orderImpl.getState() == IOrder.State.FILLED) {
/* 1487 */           this.state = IOrder.State.CLOSED;
/* 1488 */           LOGGER.debug("transiting order from FILLED to CLOSED state as a response to empty order group update message");
/*      */         } else {
/* 1490 */           this.state = IOrder.State.CANCELED;
/* 1491 */           LOGGER.debug("transiting order to CANCELED state as a response to empty order group update message");
/*      */         }
/*      */ 
/* 1494 */         if (this.lastServerRequest == ServerRequest.MERGE_TARGET) {
/* 1495 */           platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDERS_MERGE_OK, groupMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : groupMessage.getTimestamp().getTime());
/* 1496 */           this.instrument = Instrument.fromString(groupMessage.getInstrument());
/* 1497 */           this.state = IOrder.State.CLOSED;
/*      */         } else {
/* 1499 */           if ((previousState == IOrder.State.OPENED) && (!this.orderCommand.isConditional()) && (this.orderCommand != IEngine.OrderCommand.PLACE_BID) && (this.orderCommand != IEngine.OrderCommand.PLACE_OFFER))
/*      */           {
/* 1501 */             platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDER_FILL_REJECTED, groupMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : groupMessage.getTimestamp().getTime());
/*      */           } else {
/* 1503 */             platformMessageImpl = new PlatformMessageImpl(null, this, IMessage.Type.ORDER_CLOSE_OK, groupMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : groupMessage.getTimestamp().getTime());
/* 1504 */             this.closeAttemptTime = 0L;
/*      */           }
/* 1506 */           this.lastServerRequest = ServerRequest.NONE;
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1511 */         this.lastServerRequest = ServerRequest.NONE;
/*      */       }
/*      */     }
/* 1514 */     if (platformMessageImpl != null) {
/* 1515 */       if (!this.updated) {
/* 1516 */         this.updatedMessage = platformMessageImpl;
/*      */       }
/* 1518 */       this.updated = true;
/* 1519 */       synchronized (this) {
/* 1520 */         notifyAll();
/*      */       }
/*      */     }
/* 1523 */     return platformMessageImpl;
/*      */   }
/*      */ 
/*      */   public PlatformMessageImpl update(MergePositionsMessage mergeMessage) {
/* 1527 */     if (this.filledAmount == 0.0D) {
/* 1528 */       this.taskManager.getOrdersInternalCollection().removeByLabel(this.label);
/* 1529 */       this.state = IOrder.State.CLOSED;
/* 1530 */       this.closeTime = (this.creationTime = mergeMessage.getTimestamp().getTime());
/*      */     }
/* 1532 */     StringBuilder notification = new StringBuilder("Positions: ");
/* 1533 */     for (String positionId : mergeMessage.getPositionsList()) {
/* 1534 */       notification.append(positionId).append("; ");
/*      */     }
/* 1536 */     notification.setLength(notification.length() - 2);
/* 1537 */     notification.append(" MERGED");
/* 1538 */     if (this.state == IOrder.State.CLOSED)
/* 1539 */       notification.append(", result position closed");
/*      */     else {
/* 1541 */       notification.append(" to position: ").append(mergeMessage.getNewOrderGroupId());
/*      */     }
/* 1543 */     this.lastServerRequest = ServerRequest.NONE;
/* 1544 */     PlatformMessageImpl platformMessageImpl = new PlatformMessageImpl(notification.toString(), this, IMessage.Type.ORDERS_MERGE_OK, mergeMessage.getTimestamp().getTime());
/* 1545 */     if (!this.updated) {
/* 1546 */       this.updatedMessage = platformMessageImpl;
/*      */     }
/* 1548 */     this.updated = true;
/* 1549 */     synchronized (this) {
/* 1550 */       notifyAll();
/*      */     }
/* 1552 */     return platformMessageImpl;
/*      */   }
/*      */ 
/*      */   public PlatformMessageImpl update(NotificationMessage notificationMessage)
/*      */   {
/* 1558 */     NotificationMessageCode code = notificationMessage.getNotificationCode();
/* 1559 */     String text = notificationMessage.getText();
/*      */ 
/* 1561 */     if ((this.taskManager.isGlobal()) && ((code == NotificationMessageCode.REJECT_AND_RESUBMIT) || ((code == NotificationMessageCode.REJECTED_COUNTERPARTY) && ((this.orderCommand == IEngine.OrderCommand.PLACE_BID) || (this.orderCommand == IEngine.OrderCommand.PLACE_OFFER)) && (notificationMessage.getOrderId().equals(this.openingOrderId)))))
/*      */     {
/* 1566 */       this.awaitingResubmit = true;
/* 1567 */       return new PlatformMessageImpl(new StringBuilder().append(code).append("-").append(text).toString(), this, IMessage.Type.NOTIFICATION, notificationMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : notificationMessage.getTimestamp().getTime());
/*      */     }
/*      */ 
/* 1570 */     if ((code == NotificationMessageCode.REJECTED_COUNTERPARTY) && (((this.orderCommand != IEngine.OrderCommand.PLACE_BID) && (this.orderCommand != IEngine.OrderCommand.PLACE_OFFER)) || ((notificationMessage.getOrderId().equals(this.openingOrderId)) || ((this.state == IOrder.State.FILLED) && (notificationMessage.getOrderId().equals(this.pendingOrderId))))))
/*      */     {
/* 1572 */       return null;
/*      */     }
/*      */ 
/* 1575 */     IMessage.Type type = responseMessageGenerator.generateResponse(this.lastServerRequest, this.state, code, text);
/*      */ 
/* 1577 */     if (type != null) {
/* 1578 */       PlatformMessageImpl platformMessageImpl = new PlatformMessageImpl(new StringBuilder().append(code).append("-").append(text).toString(), this, type, notificationMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : notificationMessage.getTimestamp().getTime());
/* 1579 */       switch (2.$SwitchMap$com$dukascopy$api$IMessage$Type[type.ordinal()]) {
/*      */       case 1:
/* 1581 */         this.closeAttemptTime = 0L;
/*      */       case 2:
/* 1583 */         this.stopLossChangeTime = 0L;
/* 1584 */         this.takeProfitChangeTime = 0L;
/* 1585 */         this.requestedAmountChangeTime = 0L;
/* 1586 */         this.openPriceChangeTime = 0L;
/*      */       case 3:
/*      */       case 4:
/* 1589 */         if (!this.updated) {
/* 1590 */           this.updatedMessage = platformMessageImpl;
/*      */         }
/* 1592 */         this.updated = true;
/* 1593 */         synchronized (this) {
/* 1594 */           notifyAll();
/*      */         }
/* 1596 */         this.lastServerRequest = ServerRequest.NONE;
/* 1597 */         break;
/*      */       case 5:
/* 1599 */         platformMessageImpl = null;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1604 */       return null;
/*      */     }
/*      */     PlatformMessageImpl platformMessageImpl;
/* 1609 */     switch (2.$SwitchMap$com$dukascopy$api$IMessage$Type[type.ordinal()]) {
/*      */     case 3:
/* 1611 */       if (getState() != IOrder.State.CREATED) break;
/* 1612 */       this.taskManager.getOrdersInternalCollection().removeByLabel(this.label);
/* 1613 */       this.state = IOrder.State.CANCELED;
/* 1614 */       if (notificationMessage.getTimestamp() == null) break;
/* 1615 */       this.closeTime = notificationMessage.getTimestamp().getTime(); break;
/*      */     case 5:
/* 1621 */       platformMessageImpl = null;
/* 1622 */       break;
/*      */     case 4:
/* 1625 */       this.taskManager.getOrdersInternalCollection().removeByLabel(this.label);
/* 1626 */       this.state = IOrder.State.CANCELED;
/* 1627 */       if (notificationMessage.getTimestamp() == null) break;
/* 1628 */       this.closeTime = notificationMessage.getTimestamp().getTime(); break;
/*      */     }
/*      */ 
/* 1635 */     return platformMessageImpl;
/*      */   }
/*      */ 
/*      */   public PlatformMessageImpl update(ErrorResponseMessage errorResponseMessage)
/*      */   {
/* 1640 */     PlatformMessageImpl platformMessageImpl = null;
/* 1641 */     if (this.state == IOrder.State.CREATED) {
/* 1642 */       this.taskManager.getOrdersInternalCollection().removeByLabel(this.label);
/* 1643 */       this.state = IOrder.State.CANCELED;
/* 1644 */       platformMessageImpl = new PlatformMessageImpl(errorResponseMessage.getReason(), this, IMessage.Type.ORDER_SUBMIT_REJECTED, errorResponseMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : errorResponseMessage.getTimestamp().getTime());
/*      */     }
/* 1646 */     if (platformMessageImpl != null) {
/* 1647 */       if (!this.updated) {
/* 1648 */         this.updatedMessage = platformMessageImpl;
/*      */       }
/* 1650 */       this.updated = true;
/* 1651 */       synchronized (this) {
/* 1652 */         notifyAll();
/*      */       }
/*      */     }
/* 1655 */     return platformMessageImpl;
/*      */   }
/*      */ 
/*      */   private IEngine.OrderCommand detectOrderCommand(OrderMessage orderMessage) {
/* 1659 */     OrderSide orderSide = orderMessage.getSide();
/* 1660 */     StopDirection stopDirection = orderMessage.getStopDirection();
/*      */ 
/* 1662 */     IEngine.OrderCommand orderCommand = null;
/*      */ 
/* 1664 */     if (stopDirection == StopDirection.ASK_GREATER) {
/* 1665 */       if (orderSide == OrderSide.BUY)
/* 1666 */         orderCommand = IEngine.OrderCommand.BUYSTOP;
/* 1667 */       else if (orderSide == OrderSide.SELL)
/* 1668 */         orderCommand = IEngine.OrderCommand.SELLSTOP;
/*      */       else {
/* 1670 */         LOGGER.error(new StringBuilder().append("Assertion error in detecting OrderCommand[1] ").append(orderSide).toString());
/*      */       }
/*      */     }
/* 1673 */     else if (stopDirection == StopDirection.ASK_LESS) {
/* 1674 */       if (orderSide == OrderSide.BUY)
/* 1675 */         orderCommand = IEngine.OrderCommand.BUYLIMIT;
/* 1676 */       else if (orderSide == OrderSide.SELL)
/* 1677 */         orderCommand = IEngine.OrderCommand.SELLSTOP_BYASK;
/*      */       else {
/* 1679 */         LOGGER.error(new StringBuilder().append("Assertion error in detecting OrderCommand[2] ").append(orderSide).toString());
/*      */       }
/*      */     }
/* 1682 */     else if (stopDirection == StopDirection.BID_GREATER) {
/* 1683 */       if (orderSide == OrderSide.BUY)
/* 1684 */         orderCommand = IEngine.OrderCommand.BUYSTOP_BYBID;
/* 1685 */       else if (orderSide == OrderSide.SELL)
/* 1686 */         orderCommand = IEngine.OrderCommand.SELLLIMIT;
/*      */       else {
/* 1688 */         LOGGER.error(new StringBuilder().append("Assertion error in detecting OrderCommand[3] ").append(orderSide).toString());
/*      */       }
/*      */     }
/* 1691 */     else if (stopDirection == StopDirection.BID_LESS) {
/* 1692 */       if (orderSide == OrderSide.BUY)
/* 1693 */         orderCommand = IEngine.OrderCommand.BUYLIMIT_BYBID;
/* 1694 */       else if (orderSide == OrderSide.SELL)
/* 1695 */         orderCommand = IEngine.OrderCommand.SELLSTOP;
/*      */       else {
/* 1697 */         LOGGER.error(new StringBuilder().append("Assertion error in detecting OrderCommand[4] ").append(orderSide).toString());
/*      */       }
/*      */     }
/* 1700 */     else if (stopDirection == null) {
/* 1701 */       if (orderMessage.isPlaceOffer()) {
/* 1702 */         if (orderSide == OrderSide.BUY)
/* 1703 */           orderCommand = IEngine.OrderCommand.PLACE_BID;
/* 1704 */         else if (orderSide == OrderSide.SELL)
/* 1705 */           orderCommand = IEngine.OrderCommand.PLACE_OFFER;
/*      */         else {
/* 1707 */           LOGGER.error(new StringBuilder().append("Assertion error in detecting OrderCommand[8] ").append(orderSide).toString());
/*      */         }
/*      */       }
/* 1710 */       else if (orderSide == OrderSide.BUY)
/* 1711 */         orderCommand = IEngine.OrderCommand.BUY;
/* 1712 */       else if (orderSide == OrderSide.SELL)
/* 1713 */         orderCommand = IEngine.OrderCommand.SELL;
/*      */       else {
/* 1715 */         LOGGER.error(new StringBuilder().append("Assertion error in detecting OrderCommand[5] ").append(orderSide).toString());
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1721 */       LOGGER.error(new StringBuilder().append("Assertion error in detecting OrderCommand[6] ").append(stopDirection).toString());
/*      */     }
/*      */ 
/* 1724 */     return orderCommand;
/*      */   }
/*      */ 
/*      */   public static String extractLabel(OrderGroupMessage orderGroupMessage) {
/* 1728 */     String label = null;
/* 1729 */     OrderMessage openingOrder = orderGroupMessage.getOpeningOrder();
/* 1730 */     if (openingOrder != null) {
/* 1731 */       label = openingOrder.getExternalSysId();
/* 1732 */       if (label == null)
/* 1733 */         LOGGER.error(new StringBuilder().append("PROBLEM WITH OPENING ORDER ").append(openingOrder).toString());
/*      */     }
/*      */     else {
/* 1736 */       for (OrderMessage orderMessage : orderGroupMessage.getOrders()) {
/* 1737 */         if ((orderMessage != null) && (orderMessage.getExternalSysId() != null)) {
/* 1738 */           label = orderMessage.getExternalSysId();
/* 1739 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1743 */     if (label == null) {
/* 1744 */       label = orderGroupMessage.getExternalSysId();
/*      */     }
/* 1746 */     return label;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1752 */     return new StringBuilder().append("[").append(getLabel()).append("]-").append(getState()).append(" / ").append(getInstrument()).append(" / ").append(getOpenPrice()).append(" / ").append(getRequestedAmount()).append(" / ").append(getAmount()).toString();
/*      */   }
/*      */ 
/*      */   public String toStringDetail() {
/* 1756 */     StringBuffer buff = new StringBuffer(128);
/* 1757 */     buff.append(new StringBuilder().append("label=").append(getLabel()).append(";").toString()).append(new StringBuilder().append("getId()=").append(getId()).append(";").toString()).append(new StringBuilder().append("groupId=").append(getGroupId()).append(";").toString()).append(new StringBuilder().append("openingOrderId=").append(getOpeningOrderId()).append(";").toString()).append(new StringBuilder().append("pendingOrderId=").append(this.pendingOrderId).append(";").toString()).append(new StringBuilder().append("parentOrderId=").append(this.parentOrderId).append(";").toString()).append(new StringBuilder().append("tpOrderId=").append(this.tpOrderId).append(";").toString()).append(new StringBuilder().append("slOrderId=").append(this.slOrderId).append(";").toString()).append(new StringBuilder().append("state=").append(getState()).append(";").toString()).append(new StringBuilder().append("instrument=").append(getInstrument()).append(";").toString()).append(new StringBuilder().append("openPrice=").append(getOpenPrice()).append(";").toString()).append(new StringBuilder().append("requestedAmount=").append(getRequestedAmount()).append(";").toString()).append(new StringBuilder().append("amount=").append(getAmount()).append(";").toString()).append(new StringBuilder().append("lastServerRequest=").append(this.lastServerRequest).append(";").toString()).append(new StringBuilder().append("awaitingResubmit=").append(this.awaitingResubmit).append(";").toString()).append(new StringBuilder().append("localCreationTime=").append(this.localCreationTime).append(";").toString());
/*      */ 
/* 1774 */     if (this.updatedMessage != null) {
/* 1775 */       buff.append(new StringBuilder().append("updatedMessage.type=").append(this.updatedMessage.getType()).append(";").toString());
/* 1776 */       buff.append(new StringBuilder().append("updatedMessage.creationTime=").append(this.updatedMessage.getCreationTime()).append(";").toString());
/*      */ 
/* 1778 */       IOrder order = this.updatedMessage.getOrder();
/* 1779 */       if (order != null) {
/* 1780 */         buff.append(new StringBuilder().append("updatedMessage.getOrder().getLabel()=").append(order.getLabel()).append(";").toString());
/* 1781 */         buff.append(new StringBuilder().append("updatedMessage.getOrder().getId()=").append(order.getId()).append(";").toString());
/*      */       }
/*      */     }
/*      */ 
/* 1785 */     return buff.toString();
/*      */   }
/*      */ 
/*      */   public boolean updated()
/*      */   {
/* 1790 */     return this.updated;
/*      */   }
/*      */ 
/*      */   public boolean updated(String[] states)
/*      */     throws JFException
/*      */   {
/* 1798 */     if ((ObjectUtils.isNullOrEmpty(states)) || (states.length == 0))
/*      */     {
/* 1800 */       return this.updated;
/*      */     }
/* 1802 */     Set orderStates = new HashSet();
/* 1803 */     if (!ObjectUtils.isNullOrEmpty(states)) {
/* 1804 */       for (String state : states) {
/* 1805 */         orderStates.add(IOrder.State.valueOf(state));
/*      */       }
/*      */     }
/* 1808 */     if (orderStates.contains(this.state)) {
/* 1809 */       return this.updated;
/*      */     }
/* 1811 */     boolean stateValid = false;
/* 1812 */     for (IOrder.State expectedState : orderStates) {
/* 1813 */       if ((expectedState.ordinal() > this.state.ordinal()) && (!ObjectUtils.isEqual(this.state, IOrder.State.CLOSED))) {
/* 1814 */         stateValid = true;
/* 1815 */         break;
/*      */       }
/*      */     }
/* 1818 */     if (!stateValid) {
/* 1819 */       throw new JFException(JFException.Error.ORDER_STATE_IMMUTABLE, new StringBuilder().append(" state is ").append(getState()).toString());
/*      */     }
/* 1821 */     return false;
/*      */   }
/*      */ 
/*      */   public synchronized void waitForUpdate(long timeoutMillis)
/*      */   {
/* 1828 */     if ((this.state != IOrder.State.CLOSED) && (this.state != IOrder.State.CANCELED)) {
/* 1829 */       this.updated = false;
/* 1830 */       if (this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/*      */         try {
/* 1832 */           this.taskManager.waitForUpdate(this, timeoutMillis, TimeUnit.MILLISECONDS);
/*      */         } catch (InterruptedException e) {
/* 1834 */           LOGGER.warn(e.getMessage(), e);
/*      */         }
/*      */       }
/*      */       else {
/* 1838 */         long startTime = System.currentTimeMillis();
/*      */         long currentTime;
/* 1840 */         while ((!this.taskManager.isStrategyStopping()) && (!updated()) && ((currentTime = System.currentTimeMillis()) - startTime < timeoutMillis)) {
/*      */           try {
/* 1842 */             wait(timeoutMillis - (currentTime - startTime));
/*      */           } catch (InterruptedException e) {
/* 1844 */             LOGGER.warn(e.getMessage(), e);
/* 1845 */             this.updatedMessage = null;
/* 1846 */             return;
/*      */           }
/*      */         }
/*      */       }
/* 1850 */       this.updatedMessage = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized IMessage waitForUpdate(long timeout, TimeUnit unit)
/*      */   {
/* 1856 */     if ((this.state != IOrder.State.CLOSED) && (this.state != IOrder.State.CANCELED)) {
/* 1857 */       this.updated = false;
/* 1858 */       if (this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/*      */         try {
/* 1860 */           this.taskManager.waitForUpdate(this, timeout, unit);
/*      */         } catch (InterruptedException ie) {
/* 1862 */           LOGGER.warn("Interrupted");
/*      */         }
/*      */       }
/*      */       else {
/* 1866 */         long startTime = System.currentTimeMillis();
/* 1867 */         long timeoutMillis = unit.toMillis(timeout);
/*      */         long currentTime;
/* 1869 */         while ((!this.taskManager.isStrategyStopping()) && (!updated()) && ((currentTime = System.currentTimeMillis()) - startTime < timeoutMillis)) {
/*      */           try {
/* 1871 */             wait(timeoutMillis - (currentTime - startTime));
/*      */           } catch (InterruptedException e) {
/* 1873 */             LOGGER.warn(e.getMessage(), e);
/* 1874 */             this.updatedMessage = null;
/* 1875 */             return null;
/*      */           }
/*      */         }
/*      */       }
/* 1879 */       IMessage message = this.updatedMessage;
/* 1880 */       this.updatedMessage = null;
/* 1881 */       return message;
/*      */     }
/* 1883 */     return null;
/*      */   }
/*      */ 
/*      */   public IMessage waitForUpdate(IOrder.State[] states)
/*      */     throws JFException
/*      */   {
/* 1892 */     return waitForUpdate(9223372036854775807L, TimeUnit.MILLISECONDS, states);
/*      */   }
/*      */ 
/*      */   public IMessage waitForUpdate(long timeoutMills, IOrder.State[] states)
/*      */     throws JFException
/*      */   {
/* 1900 */     return waitForUpdate(timeoutMills, TimeUnit.MILLISECONDS, states);
/*      */   }
/*      */ 
/*      */   public synchronized IMessage waitForUpdate(long timeout, TimeUnit unit, IOrder.State[] states)
/*      */     throws JFException
/*      */   {
/* 1908 */     if ((this.state != IOrder.State.CLOSED) && (this.state != IOrder.State.CANCELED)) {
/* 1909 */       this.updated = false;
/* 1910 */       if (this.taskManager.isThreadOk(Thread.currentThread().getId())) {
/*      */         try {
/* 1912 */           this.taskManager.waitForUpdate(this, timeout, unit, states);
/*      */         } catch (InterruptedException ie) {
/* 1914 */           LOGGER.warn("Interrupted");
/*      */         }
/*      */       }
/*      */       else {
/* 1918 */         long startTime = System.currentTimeMillis();
/* 1919 */         long timeoutMillis = unit.toMillis(timeout);
/*      */         long currentTime;
/* 1921 */         while ((!this.taskManager.isStrategyStopping()) && (!updated()) && ((currentTime = System.currentTimeMillis()) - startTime < timeoutMillis)) {
/*      */           try {
/* 1923 */             wait(timeoutMillis - (currentTime - startTime));
/*      */           } catch (InterruptedException e) {
/* 1925 */             LOGGER.warn(e.getMessage(), e);
/* 1926 */             this.updatedMessage = null;
/* 1927 */             return null;
/*      */           }
/*      */         }
/*      */       }
/* 1931 */       IMessage message = this.updatedMessage;
/* 1932 */       this.updatedMessage = null;
/* 1933 */       return message;
/*      */     }
/* 1935 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized double getProfitLossInPips()
/*      */   {
/*      */     double closePrice;
/* 1941 */     if (this.state == IOrder.State.FILLED)
/*      */     {
/*      */       double closePrice;
/* 1942 */       if (isLong())
/* 1943 */         closePrice = FeedDataProvider.getDefaultInstance().getLastBid(this.instrument);
/*      */       else
/* 1945 */         closePrice = FeedDataProvider.getDefaultInstance().getLastAsk(this.instrument);
/*      */     }
/*      */     else
/*      */     {
/*      */       double closePrice;
/* 1947 */       if (this.state == IOrder.State.CLOSED)
/* 1948 */         closePrice = this.closePrice;
/*      */       else
/* 1950 */         return 0.0D;
/*      */     }
/*      */     double closePrice;
/*      */     double plInPips;
/*      */     double plInPips;
/* 1953 */     if (isLong())
/* 1954 */       plInPips = StratUtils.roundHalfEven((closePrice - this.openPrice) / this.instrument.getPipValue(), 1);
/*      */     else {
/* 1956 */       plInPips = StratUtils.roundHalfEven((this.openPrice - closePrice) / this.instrument.getPipValue(), 1);
/*      */     }
/* 1958 */     return plInPips;
/*      */   }
/*      */ 
/*      */   public synchronized double getProfitLossInUSD()
/*      */   {
/*      */     double amount;
/* 1965 */     if (this.state == IOrder.State.FILLED)
/*      */     {
/*      */       double closePrice;
/*      */       double closePrice;
/* 1966 */       if (isLong())
/* 1967 */         closePrice = FeedDataProvider.getDefaultInstance().getLastBid(this.instrument);
/*      */       else {
/* 1969 */         closePrice = FeedDataProvider.getDefaultInstance().getLastAsk(this.instrument);
/*      */       }
/* 1971 */       amount = this.filledAmount;
/*      */     }
/*      */     else
/*      */     {
/*      */       double amount;
/* 1972 */       if (this.state == IOrder.State.CLOSED) {
/* 1973 */         double closePrice = this.closePrice;
/* 1974 */         amount = this.filledAmountInitial;
/*      */       } else {
/* 1976 */         return 0.0D;
/*      */       }
/*      */     }
/*      */     double amount;
/*      */     double closePrice;
/*      */     double profLossInSecondaryCCY;
/*      */     double profLossInSecondaryCCY;
/* 1979 */     if (isLong())
/* 1980 */       profLossInSecondaryCCY = StratUtils.roundHalfEven((closePrice - this.openPrice) * amount * 1000000.0D, 2);
/*      */     else {
/* 1982 */       profLossInSecondaryCCY = StratUtils.roundHalfEven((this.openPrice - closePrice) * amount * 1000000.0D, 2);
/*      */     }
/*      */ 
/* 1985 */     return StratUtils.roundHalfEven(CurrencyConverter.getCurrencyConverter().convert(profLossInSecondaryCCY, this.instrument.getSecondaryCurrency(), Instrument.EURUSD.getSecondaryCurrency(), null), 2);
/*      */   }
/*      */ 
/*      */   public synchronized double getProfitLossInAccountCurrency()
/*      */   {
/*      */     double amount;
/* 1992 */     if (this.state == IOrder.State.FILLED)
/*      */     {
/*      */       double closePrice;
/*      */       double closePrice;
/* 1993 */       if (isLong())
/* 1994 */         closePrice = FeedDataProvider.getDefaultInstance().getLastBid(this.instrument);
/*      */       else {
/* 1996 */         closePrice = FeedDataProvider.getDefaultInstance().getLastAsk(this.instrument);
/*      */       }
/* 1998 */       amount = this.filledAmount;
/*      */     }
/*      */     else
/*      */     {
/*      */       double amount;
/* 1999 */       if (this.state == IOrder.State.CLOSED) {
/* 2000 */         double closePrice = this.closePrice;
/* 2001 */         amount = this.filledAmountInitial;
/*      */       } else {
/* 2003 */         return 0.0D;
/*      */       }
/*      */     }
/*      */     double amount;
/*      */     double closePrice;
/*      */     double profLossInSecondaryCCY;
/*      */     double profLossInSecondaryCCY;
/* 2006 */     if (isLong())
/* 2007 */       profLossInSecondaryCCY = StratUtils.roundHalfEven((closePrice - this.openPrice) * amount * 1000000.0D, 2);
/*      */     else {
/* 2009 */       profLossInSecondaryCCY = StratUtils.roundHalfEven((this.openPrice - closePrice) * amount * 1000000.0D, 2);
/*      */     }
/*      */ 
/* 2012 */     return StratUtils.roundHalfEven(CurrencyConverter.getCurrencyConverter().convert(profLossInSecondaryCCY, this.instrument.getSecondaryCurrency(), this.taskManager.getAccountCurrency(), null), 2);
/*      */   }
/*      */ 
/*      */   public double getCommissionInUSD()
/*      */   {
/* 2022 */     return StratUtils.roundHalfEven(CurrencyConverter.getCurrencyConverter().convert(this.commission, this.taskManager.getAccountCurrency(), Instrument.EURUSD.getSecondaryCurrency(), null), 2);
/*      */   }
/*      */ 
/*      */   public double getCommission()
/*      */   {
/* 2030 */     return StratUtils.roundHalfEven(this.commission, 2);
/*      */   }
/*      */ 
/*      */   public synchronized void resetTimes() {
/* 2034 */     this.stopLossChangeTime = 0L;
/* 2035 */     this.takeProfitChangeTime = 0L;
/* 2036 */     this.requestedAmountChangeTime = 0L;
/* 2037 */     this.openPriceChangeTime = 0L;
/*      */   }
/*      */ 
/*      */   public boolean compare(IOrder order)
/*      */   {
/* 2054 */     return ((this.comment == null) || (order.getComment() != null) || (this.comment.equalsIgnoreCase(order.getComment()))) && (this.instrument.toString().equalsIgnoreCase(order.getInstrument().toString())) && (this.requestedAmount == order.getRequestedAmount()) && (this.label.equalsIgnoreCase(order.getLabel())) && (this.tpPrice == order.getTakeProfitPrice()) && (this.slPrice == order.getStopLossPrice()) && ((this.slSide == null) || (order.getStopLossSide() == null) || (this.slSide.equals(order.getStopLossSide()))) && (this.openPrice == order.getOpenPrice()) && (this.closePrice == order.getClosePrice()) && (this.state.equals(order.getState()));
/*      */   }
/*      */ 
/*      */   public PlatformOrderImpl clone()
/*      */   {
/* 2061 */     PlatformOrderImpl cloneOrder = new PlatformOrderImpl(this.taskManager, this.comment, this.instrument, this.requestedAmount, this.filledAmount, this.label, this.tpPrice, this.slPrice, this.slSide, this.slTrailStep, this.openPrice, this.closePrice, this.state, this.groupId, this.openingOrderId, this.pendingOrderId, this.slOrderId, this.tpOrderId, this.goodTillTime, this.creationTime, this.fillTime, this.closeTime, this.orderCommand, this.commission);
/*      */ 
/* 2086 */     cloneOrder.slippage = this.slippage;
/* 2087 */     cloneOrder.lastServerRequest = this.lastServerRequest;
/*      */ 
/* 2089 */     return cloneOrder;
/*      */   }
/*      */ 
/*      */   public static enum ServerRequest
/*      */   {
/*  118 */     NONE, SUBMIT, SET_REQ_AMOUNT, SET_OPEN_PRICE, CLOSE, SET_EXPIRATION, SET_SL, SET_TP, MERGE_SOURCE, MERGE_TARGET, CANCEL_ORDER;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.PlatformOrderImpl
 * JD-Core Version:    0.6.0
 */