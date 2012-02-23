/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.IAccount;
/*     */ import com.dukascopy.api.IConsole;
/*     */ import com.dukascopy.api.IEngine;
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.IMessage;
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.IStrategyBroadcastMessage;
/*     */ import com.dukascopy.api.IStrategyListener;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.IUserInterface;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.History;
/*     */ import com.dukascopy.api.impl.StrategyEventsListener;
/*     */ import com.dukascopy.api.impl.StrategyMessages;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.impl.execution.TaskFlush;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveCandleListener;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.CloseData;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.OpenData;
/*     */ import com.dukascopy.charts.data.datacache.OrdersListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.orders.ExposureData;
/*     */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.IStrategyRateDataNotificationFactory;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.StrategyRateDataNotificationFactory;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import com.dukascopy.transport.client.TransportClient;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.NotificationMessageCode;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderSyncMessage;
/*     */ import com.dukascopy.transport.common.msg.news.CalendarEvent;
/*     */ import com.dukascopy.transport.common.msg.news.NewsSource;
/*     */ import com.dukascopy.transport.common.msg.news.NewsStoryMessage;
/*     */ import com.dukascopy.transport.common.msg.news.PlainContent;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyChangeSupport;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.json.JSONObject;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JForexTaskManager
/*     */   implements LiveCandleListener
/*     */ {
/*     */   private static final Logger LOGGER;
/*     */   private static final String UID_FORMAT = "%1$s:%2$s:%3$s";
/*     */   private static final BigDecimal ONE_MILLION;
/*  93 */   private long strategyId = -9223372036854775808L;
/*     */   private volatile StrategyProcessor runningStrategy;
/*     */   private String strategyKey;
/*  97 */   private final AtomicBoolean strategyStopping = new AtomicBoolean(false);
/*     */   private final JForexEngineImpl forexEngineImpl;
/*     */   private final History history;
/*     */   private final IConsole console;
/*     */   private final DDSChartsController ddsChartsController;
/*     */   private final IUserInterface userInterface;
/*     */   private volatile ISystemListenerExtended systemListener;
/* 106 */   private List<IStrategyListener> strategyListeners = new ArrayList();
/*     */   private final TransportClient transportClient;
/* 110 */   private final OrdersInternalCollection ordersInternalCollection = new OrdersInternalCollection(this);
/*     */   private final IStrategyExceptionHandler exceptionHandler;
/*     */   private volatile PlatformAccountImpl account;
/*     */   private volatile AccountInfoMessage lastAccountInfo;
/* 116 */   private Set<Instrument> requiredInstruments = new HashSet();
/*     */   private final Environment environment;
/*     */   private Boolean connected;
/*     */   private final String externalIP;
/*     */   private final String internalIP;
/*     */   private final String sessionID;
/*     */   private PropertyChangeSupport propertyChangeSupport;
/*     */   private StrategyEventsListener strategyEventsListener;
/*     */ 
/*     */   public JForexTaskManager(Environment environment, boolean live, String accountName, IConsole console, TransportClient transportClient, DDSChartsController ddsChartsController, IUserInterface userInterface, IStrategyExceptionHandler exceptionHandler, AccountInfoMessage lastAccountInfo, String externalIP, String internalIP, String sessionID)
/*     */   {
/* 137 */     if (lastAccountInfo == null) {
/* 138 */       throw new NullPointerException("Account info is null");
/*     */     }
/* 140 */     this.history = new History(OrdersProvider.getInstance(), lastAccountInfo.getCurrency());
/* 141 */     this.console = console;
/* 142 */     this.transportClient = transportClient;
/* 143 */     this.ddsChartsController = ddsChartsController;
/* 144 */     this.userInterface = userInterface;
/* 145 */     this.exceptionHandler = exceptionHandler;
/* 146 */     this.lastAccountInfo = lastAccountInfo;
/* 147 */     this.account = new PlatformAccountImpl(lastAccountInfo);
/* 148 */     this.forexEngineImpl = new JForexEngineImpl(this, accountName, live);
/* 149 */     this.environment = environment;
/* 150 */     this.externalIP = externalIP;
/* 151 */     this.internalIP = internalIP;
/* 152 */     this.sessionID = sessionID;
/* 153 */     this.strategyEventsListener = null;
/*     */   }
/*     */ 
/*     */   private void initExistingOrders() {
/* 157 */     BigDecimal oneMillion = BigDecimal.valueOf(1000000L);
/* 158 */     for (Instrument instrument : FeedDataProvider.getDefaultInstance().getInstrumentsCurrentlySubscribed()) {
/* 159 */       Collection orders = OrdersProvider.getInstance().getOrdersForInstrument(instrument).values();
/* 160 */       for (OrderHistoricalData order : orders) {
/* 161 */         if ((!order.isClosed()) && ((!isGlobal()) || (!order.isOpened()))) {
/* 162 */           IOrder.State state = null;
/* 163 */           String orderGroupId = order.getOrderGroupId();
/* 164 */           String label = null;
/* 165 */           IEngine.OrderCommand orderCommand = null;
/* 166 */           BigDecimal requestedAmount = BigDecimal.ZERO;
/* 167 */           double filledAmount = 0.0D;
/* 168 */           String pendingOrderId = null;
/* 169 */           String openingOrderId = null;
/* 170 */           double price = 0.0D;
/* 171 */           String stopLossOrderId = null;
/* 172 */           double stopLossPrice = 0.0D;
/* 173 */           com.dukascopy.api.OfferSide stopLossSide = null;
/* 174 */           double trailingStep = 0.0D;
/* 175 */           String takeProfitOrderId = null;
/* 176 */           double takeProfitPrice = 0.0D;
/* 177 */           long goodTillTime = 0L;
/* 178 */           String comment = null;
/* 179 */           long creationTime = 0L;
/* 180 */           long closeTime = 0L;
/* 181 */           long fillTime = 0L;
/* 182 */           double closePrice = 0.0D;
/* 183 */           double commission = order.getCommission().doubleValue();
/* 184 */           if (order.getPendingOrders().size() == 1) {
/* 185 */             OrderHistoricalData.OpenData pendingOrderData = (OrderHistoricalData.OpenData)order.getPendingOrders().get(0);
/* 186 */             state = IOrder.State.OPENED;
/* 187 */             label = pendingOrderData.getLabel();
/* 188 */             orderCommand = pendingOrderData.getSide();
/* 189 */             requestedAmount = pendingOrderData.getAmount().divide(oneMillion, 7, 7);
/* 190 */             openingOrderId = pendingOrderId = pendingOrderData.getOrderId();
/* 191 */             if (pendingOrderData.getOpenPrice().compareTo(OrderHistoricalData.NEG_ONE) != 0) {
/* 192 */               price = pendingOrderData.getOpenPrice().doubleValue();
/*     */             }
/* 194 */             if (pendingOrderData.getStopLossPrice().compareTo(OrderHistoricalData.NEG_ONE) != 0) {
/* 195 */               stopLossPrice = pendingOrderData.getStopLossPrice().doubleValue();
/* 196 */               stopLossOrderId = pendingOrderData.getStopLossOrderId();
/* 197 */               stopLossSide = pendingOrderData.isStopLossByBid() ? com.dukascopy.api.OfferSide.BID : com.dukascopy.api.OfferSide.ASK;
/* 198 */               if (pendingOrderData.getTrailingStep() != null) {
/* 199 */                 trailingStep = pendingOrderData.getTrailingStep().divide(BigDecimal.valueOf(instrument.getPipValue()), 7).doubleValue();
/*     */               }
/*     */             }
/*     */ 
/* 203 */             if (pendingOrderData.getTakeProfitPrice().compareTo(OrderHistoricalData.NEG_ONE) != 0) {
/* 204 */               takeProfitOrderId = pendingOrderData.getTakeProfitOrderId();
/* 205 */               takeProfitPrice = pendingOrderData.getTakeProfitPrice().doubleValue();
/*     */             }
/* 207 */             goodTillTime = pendingOrderData.getGoodTillTime();
/* 208 */             comment = pendingOrderData.getComment();
/* 209 */             creationTime = pendingOrderData.getCreationTime();
/*     */           }
/* 211 */           OrderHistoricalData.OpenData entryOrder = order.getEntryOrder();
/* 212 */           if (entryOrder != null) {
/* 213 */             state = IOrder.State.FILLED;
/* 214 */             label = entryOrder.getLabel();
/* 215 */             orderCommand = entryOrder.getSide();
/* 216 */             requestedAmount = requestedAmount.add(entryOrder.getAmount().divide(oneMillion, 7, 7));
/* 217 */             filledAmount = entryOrder.getAmount().divide(oneMillion, 7, 7).doubleValue();
/* 218 */             openingOrderId = entryOrder.getOrderId();
/* 219 */             if (entryOrder.getOpenPrice().compareTo(OrderHistoricalData.NEG_ONE) != 0) {
/* 220 */               price = entryOrder.getOpenPrice().doubleValue();
/*     */             }
/* 222 */             if (entryOrder.getStopLossPrice().compareTo(OrderHistoricalData.NEG_ONE) != 0) {
/* 223 */               stopLossPrice = entryOrder.getStopLossPrice().doubleValue();
/* 224 */               stopLossOrderId = entryOrder.getStopLossOrderId();
/* 225 */               stopLossSide = entryOrder.isStopLossByBid() ? com.dukascopy.api.OfferSide.BID : com.dukascopy.api.OfferSide.ASK;
/* 226 */               if (entryOrder.getTrailingStep() != null) {
/* 227 */                 trailingStep = entryOrder.getTrailingStep().divide(BigDecimal.valueOf(instrument.getPipValue()), 7).doubleValue();
/*     */               }
/*     */             }
/*     */ 
/* 231 */             if (entryOrder.getTakeProfitPrice().compareTo(OrderHistoricalData.NEG_ONE) != 0) {
/* 232 */               takeProfitOrderId = entryOrder.getTakeProfitOrderId();
/* 233 */               takeProfitPrice = entryOrder.getTakeProfitPrice().doubleValue();
/*     */             }
/* 235 */             comment = entryOrder.getComment();
/* 236 */             creationTime = entryOrder.getCreationTime();
/* 237 */             fillTime = entryOrder.getFillTime();
/*     */           }
/* 239 */           if (!order.getCloseDataMap().isEmpty()) {
/* 240 */             OrderHistoricalData.CloseData lastCloseData = null;
/* 241 */             for (OrderHistoricalData.CloseData closeData : order.getCloseDataMap().values())
/*     */             {
/* 243 */               lastCloseData = closeData;
/*     */             }
/* 245 */             if (lastCloseData != null) {
/* 246 */               closeTime = lastCloseData.getCloseTime();
/* 247 */               if (lastCloseData.getClosePrice().compareTo(OrderHistoricalData.NEG_ONE) != 0) {
/* 248 */                 closePrice = lastCloseData.getClosePrice().doubleValue();
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 253 */           PlatformOrderImpl platformOrder = new PlatformOrderImpl(this, comment, instrument, requestedAmount.doubleValue(), filledAmount, label, takeProfitPrice, stopLossPrice, stopLossSide, trailingStep, price, closePrice, state, orderGroupId, openingOrderId, pendingOrderId, stopLossOrderId, takeProfitOrderId, goodTillTime, creationTime, fillTime, closeTime, orderCommand, commission);
/*     */ 
/* 258 */           this.ordersInternalCollection.add(platformOrder);
/*     */         }
/*     */       }
/* 261 */       if (isGlobal()) {
/* 262 */         ExposureData exposure = OrdersProvider.getInstance().getExposureForInstrument(instrument);
/* 263 */         if (exposure.amount.compareTo(BigDecimal.ZERO) > 0) {
/* 264 */           String orderGroupId = this.lastAccountInfo.getUserId() + instrument;
/* 265 */           PlatformOrderImpl platformOrder = new PlatformOrderImpl(this, null, instrument, exposure.amount.divide(oneMillion, 7, 7).doubleValue(), exposure.amount.divide(oneMillion, 7, 7).doubleValue(), orderGroupId, 0.0D, 0.0D, null, 0.0D, exposure.price.doubleValue(), 0.0D, IOrder.State.FILLED, orderGroupId, null, null, null, null, 0L, 0L, exposure.time, 0L, exposure.side, 0.0D);
/*     */ 
/* 271 */           this.ordersInternalCollection.add(platformOrder);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> Future<T> executeTask(Callable<T> callable) {
/* 278 */     if (this.runningStrategy != null) {
/* 279 */       return this.runningStrategy.executeTask(callable, false);
/*     */     }
/* 281 */     return null;
/*     */   }
/*     */ 
/*     */   public void onNewsMessage(NewsStoryMessage newsStoryMessage)
/*     */   {
/*     */     try {
/* 287 */       JSONObject json = newsStoryMessage.getContent();
/*     */ 
/* 289 */       String copyright = newsStoryMessage.getCopyright();
/* 290 */       Set currencies = newsStoryMessage.getCurrencies();
/* 291 */       Set geoRegions = newsStoryMessage.getGeoRegions();
/* 292 */       String header = newsStoryMessage.getHeader();
/* 293 */       Set marketSectors = newsStoryMessage.getMarketSectors();
/* 294 */       Set stockIndicies = newsStoryMessage.getIndicies();
/* 295 */       String newsId = newsStoryMessage.getNewsId();
/* 296 */       long publishTime = newsStoryMessage.getPublishDate() == null ? 0L : newsStoryMessage.getPublishDate().getTime();
/* 297 */       NewsSource source = newsStoryMessage.getSource();
/* 298 */       boolean endOfStory = newsStoryMessage.isEndOfStory();
/* 299 */       boolean isHot = newsStoryMessage.isHot();
/*     */ 
/* 301 */       PlatformNewsMessageImpl message = null;
/*     */ 
/* 303 */       switch (source) {
/*     */       case DJ_LIVE_CALENDAR:
/* 305 */         CalendarEvent calendarEvent = (CalendarEvent)json;
/* 306 */         message = new PlatformCalendarMessageImpl(calendarEvent, copyright, header, newsId, publishTime, endOfStory, isHot, currencies, geoRegions, marketSectors, stockIndicies);
/*     */ 
/* 319 */         break;
/*     */       case DJ_NEWSWIRES:
/* 321 */         PlainContent plainContent = (PlainContent)json;
/* 322 */         message = new PlatformNewsMessageImpl(plainContent == null ? null : plainContent.getText(), copyright, header, newsId, publishTime, endOfStory, isHot, currencies, geoRegions, marketSectors, stockIndicies);
/*     */ 
/* 335 */         break;
/*     */       default:
/* 337 */         LOGGER.warn("Unknown news source");
/*     */       }
/*     */ 
/* 340 */       if (message != null)
/* 341 */         syncMessage(message);
/*     */     }
/*     */     catch (Exception ex) {
/* 344 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onBroadcastMessage(String transactionId, IStrategyBroadcastMessage message)
/*     */   {
/* 350 */     if (!getUID().equals(transactionId))
/* 351 */       syncMessage(message);
/*     */   }
/*     */ 
/*     */   public void onErrorMessage(ErrorResponseMessage errorResponseMessage, PlatformOrderImpl platformOrderImpl)
/*     */   {
/* 356 */     if ((this.runningStrategy != null) && (!isStrategyStopping()))
/* 357 */       this.runningStrategy.updateOrder(platformOrderImpl, errorResponseMessage);
/*     */   }
/*     */ 
/*     */   public void onNotifyMessage(NotificationMessage notificationMessage)
/*     */   {
/* 362 */     if ((this.runningStrategy == null) || (isStrategyStopping())) {
/* 363 */       return;
/*     */     }
/* 365 */     this.runningStrategy.updateOrder(notificationMessage);
/*     */   }
/*     */ 
/*     */   public void onOrderGroupReceived(OrderGroupMessage orderGroupMessage) {
/* 369 */     if ((this.runningStrategy == null) || (isStrategyStopping())) {
/* 370 */       return;
/*     */     }
/* 372 */     this.runningStrategy.updateOrder(orderGroupMessage);
/*     */   }
/*     */ 
/*     */   public void onOrderReceived(OrderMessage orderMessage) {
/* 376 */     if ((this.runningStrategy == null) || (isStrategyStopping())) {
/* 377 */       return;
/*     */     }
/* 379 */     this.runningStrategy.updateOrder(orderMessage);
/*     */   }
/*     */ 
/*     */   public void onOrdersMergedMessage(MergePositionsMessage mergePositionsMessage) {
/* 383 */     if ((this.runningStrategy == null) || (isStrategyStopping())) {
/* 384 */       return;
/*     */     }
/* 386 */     this.runningStrategy.updateOrder(mergePositionsMessage);
/*     */   }
/*     */ 
/*     */   public void updateAccountInfo(AccountInfoMessage protocolMessage)
/*     */   {
/* 398 */     this.lastAccountInfo = protocolMessage;
/* 399 */     this.account.updateFromMessage(protocolMessage);
/* 400 */     if ((this.runningStrategy != null) && (!isStrategyStopping()))
/* 401 */       this.runningStrategy.updateAccountInfo(this.account);
/*     */   }
/*     */ 
/*     */   public void onMessage(PlatformMessageImpl platformMessageImpl)
/*     */   {
/* 406 */     if ((this.runningStrategy != null) && (!isStrategyStopping()))
/* 407 */       this.runningStrategy.onMessage(platformMessageImpl, true);
/*     */   }
/*     */ 
/*     */   private void syncMessage(IMessage message)
/*     */   {
/* 412 */     if ((this.runningStrategy != null) && (!isStrategyStopping()))
/* 413 */       this.runningStrategy.onMessage(message, false);
/*     */   }
/*     */ 
/*     */   public ITick onMarketState(CurrencyMarket market)
/*     */   {
/* 418 */     if ((this.runningStrategy == null) || (isStrategyStopping())) {
/* 419 */       return null;
/*     */     }
/*     */ 
/* 422 */     Instrument instrument = Instrument.fromString(market.getInstrument());
/* 423 */     TickData tick = FeedDataProvider.getDefaultInstance().getLastTick(instrument);
/* 424 */     if (tick == null) {
/* 425 */       CurrencyOffer askCurrencyOffer = market.getBestOffer(com.dukascopy.transport.common.model.type.OfferSide.ASK);
/* 426 */       CurrencyOffer bidCurrencyOffer = market.getBestOffer(com.dukascopy.transport.common.model.type.OfferSide.BID);
/* 427 */       if ((askCurrencyOffer != null) || (bidCurrencyOffer != null)) {
/* 428 */         LOGGER.warn("Got tick for instrument [" + instrument + "] that was not processed by FeedDataProvider... Instrument subscription status [" + FeedDataProvider.getDefaultInstance().isSubscribedToInstrument(instrument) + "] MarketState [" + market + "]");
/*     */       }
/* 430 */       return null;
/*     */     }
/* 432 */     tick = new StratTickData(tick, market.getTotalLiquidityAsk().divide(ONE_MILLION).doubleValue(), market.getTotalLiquidityBid().divide(ONE_MILLION).doubleValue());
/* 433 */     this.runningStrategy.onMarket(instrument, tick);
/* 434 */     return tick;
/*     */   }
/*     */ 
/*     */   public void waitForUpdate(PlatformOrderImpl platformOrderImpl, long timeout, TimeUnit unit) throws InterruptedException {
/* 438 */     if ((this.runningStrategy != null) && (!isStrategyStopping()))
/* 439 */       this.runningStrategy.waitForUpdate(platformOrderImpl, timeout, unit);
/*     */   }
/*     */ 
/*     */   public void waitForUpdate(PlatformOrderImpl platformOrderImpl, long timeout, TimeUnit unit, IOrder.State[] expectedStates) throws InterruptedException, JFException
/*     */   {
/* 444 */     if ((this.runningStrategy != null) && (!isStrategyStopping()))
/* 445 */       this.runningStrategy.waitForUpdate(platformOrderImpl, timeout, unit, expectedStates);
/*     */   }
/*     */ 
/*     */   public boolean flushQueue(long timeout)
/*     */   {
/* 451 */     Object lock = new Object();
/* 452 */     synchronized (lock) {
/* 453 */       Future future = isStrategyStopping() ? null : executeTask(new TaskFlush(lock));
/*     */       try {
/* 455 */         lock.wait(timeout);
/*     */ 
/* 457 */         return (future == null) || (future.isDone()) || (future.isCancelled());
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/* 462 */         return false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long startStrategy(IStrategy strategy, IStrategyListener listener, String strategyKey, boolean fullAccessGranted)
/*     */   {
/* 469 */     long rc = 0L;
/* 470 */     StrategyProcessor strategyProcessor = new StrategyProcessor(this, strategy, fullAccessGranted);
/*     */ 
/* 472 */     Future future = strategyProcessor.executeTask(new Callable(strategyProcessor, strategyKey, listener) {
/*     */       public Long call() throws Exception {
/* 474 */         StrategiesControl strategiesControl = new StrategiesControl(JForexTaskManager.this.systemListener, this.val$strategyProcessor);
/* 475 */         JForexContextImpl forexContextImpl = new JForexContextImpl(this.val$strategyProcessor, JForexTaskManager.this.forexEngineImpl, JForexTaskManager.this.history, JForexTaskManager.this.console, JForexTaskManager.this.ddsChartsController, JForexTaskManager.this.userInterface, strategiesControl);
/*     */ 
/* 477 */         JForexTaskManager.access$602(JForexTaskManager.this, this.val$strategyProcessor.getStrategyId());
/* 478 */         JForexTaskManager.access$702(JForexTaskManager.this, this.val$strategyProcessor);
/* 479 */         JForexTaskManager.access$802(JForexTaskManager.this, new PropertyChangeSupport(JForexTaskManager.this.runningStrategy.getStrategy()));
/*     */ 
/* 482 */         JForexTaskManager.this.initExistingOrders();
/*     */ 
/* 485 */         StrategyMessages.strategyIsStarted(JForexTaskManager.this.runningStrategy.getStrategy());
/*     */ 
/* 487 */         JForexTaskManager.access$1002(JForexTaskManager.this, new HashSet(0));
/*     */         try {
/* 489 */           JForexTaskManager.access$602(JForexTaskManager.this, this.val$strategyProcessor.onStart(forexContextImpl));
/*     */         } catch (Throwable t) {
/* 491 */           JForexTaskManager.access$602(JForexTaskManager.this, -9223372036854775808L);
/* 492 */           JForexTaskManager.LOGGER.error(t.getMessage(), t);
/*     */         }
/*     */ 
/* 495 */         if ((JForexTaskManager.this.strategyId > 0L) && (!forexContextImpl.isStopped())) {
/* 496 */           JForexTaskManager.access$1202(JForexTaskManager.this, this.val$strategyKey);
/* 497 */           if (this.val$listener != null) {
/* 498 */             JForexTaskManager.this.strategyListeners.add(this.val$listener);
/*     */           }
/* 500 */           JForexTaskManager.this.fireOnStart();
/*     */ 
/* 504 */           this.val$strategyProcessor.updateAccountInfo(JForexTaskManager.this.account);
/*     */         } else {
/* 506 */           JForexTaskManager.this.stopStrategy();
/*     */         }
/*     */ 
/* 509 */         return Long.valueOf(JForexTaskManager.this.strategyId);
/*     */       }
/*     */     }
/*     */     , false);
/*     */     try
/*     */     {
/* 514 */       rc = ((Long)future.get()).longValue();
/*     */     } catch (Exception e) {
/* 516 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 518 */     return rc;
/*     */   }
/*     */ 
/*     */   public void stopStrategy() {
/* 522 */     if (!this.strategyStopping.compareAndSet(false, true)) {
/* 523 */       return;
/*     */     }
/*     */ 
/* 526 */     if (this.runningStrategy != null) {
/* 527 */       StrategyMessages.stoppingStrategy(this.runningStrategy.getStrategy());
/*     */ 
/* 529 */       StopCallable cc = new StopCallable();
/*     */       try {
/* 531 */         IStrategy strategy = this.runningStrategy.getStrategy();
/*     */ 
/* 533 */         this.runningStrategy.executeStop(cc);
/* 534 */         Thread haltThread = new Thread("Strategy " + strategy.getClass().getSimpleName() + " stop timer", strategy) {
/*     */           public void run() {
/*     */             try {
/* 537 */               Thread.sleep(15000L);
/*     */             } catch (InterruptedException e) {
/* 539 */               JForexTaskManager.LOGGER.error(e.getMessage(), e);
/*     */             }
/*     */ 
/* 542 */             JForexTaskManager.this.runningStrategy.halt();
/*     */ 
/* 544 */             long strategyId = JForexTaskManager.this.strategyId;
/* 545 */             if (strategyId != -9223372036854775808L)
/*     */             {
/* 547 */               JForexTaskManager.this.fireOnStop(this.val$strategy);
/*     */             }
/*     */           }
/*     */         };
/* 551 */         haltThread.start();
/*     */       } catch (Exception e) {
/* 553 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnStart()
/*     */   {
/* 560 */     ISystemListenerExtended systemListener = this.systemListener;
/* 561 */     if (systemListener != null) {
/* 562 */       systemListener.onStart(this.strategyId);
/*     */     }
/* 564 */     IStrategyListener[] listeners = (IStrategyListener[])this.strategyListeners.toArray(new IStrategyListener[this.strategyListeners.size()]);
/* 565 */     for (IStrategyListener listener : listeners)
/* 566 */       listener.onStart(this.strategyId);
/*     */   }
/*     */ 
/*     */   private void fireOnStop(IStrategy strategy)
/*     */   {
/* 573 */     this.propertyChangeSupport = null;
/* 574 */     ISystemListenerExtended systemListener = this.systemListener;
/* 575 */     if (systemListener != null) {
/* 576 */       systemListener.onStop(this.strategyId);
/*     */     }
/*     */ 
/* 579 */     IStrategyListener[] listeners = (IStrategyListener[])this.strategyListeners.toArray(new IStrategyListener[this.strategyListeners.size()]);
/* 580 */     for (IStrategyListener listener : listeners) {
/* 581 */       listener.onStop(this.strategyId);
/*     */     }
/*     */ 
/* 584 */     StrategyRateDataNotificationFactory.getIsntance().unsubscribeFromAll(strategy);
/*     */ 
/* 586 */     this.strategyId = -9223372036854775808L;
/* 587 */     this.ordersInternalCollection.dispose();
/* 588 */     StrategyMessages.strategyIsStopped(strategy);
/*     */   }
/*     */ 
/*     */   public boolean isStrategyStopping() {
/* 592 */     return this.strategyStopping.get();
/*     */   }
/*     */ 
/*     */   public boolean isThreadOk(long id) {
/* 596 */     return this.strategyId == id;
/*     */   }
/*     */ 
/*     */   public void newCandle(Instrument instrument, Period period, CandleData askCandle, CandleData bidCandle) {
/* 600 */     CandleData askBar = new CandleData(askCandle.time, askCandle.open, askCandle.close, askCandle.low, askCandle.high, askCandle.vol);
/* 601 */     CandleData bidBar = new CandleData(bidCandle.time, bidCandle.open, bidCandle.close, bidCandle.low, bidCandle.high, bidCandle.vol);
/*     */ 
/* 603 */     if ((this.runningStrategy != null) && (!isStrategyStopping()))
/* 604 */       this.runningStrategy.onBar(instrument, period, askBar, bidBar);
/*     */   }
/*     */ 
/*     */   public void onIntrumentUpdate(Instrument instrument, boolean tradable, long creationTime)
/*     */   {
/* 609 */     InstrumentStatusMessageImpl instrumentStatusMessage = new InstrumentStatusMessageImpl(instrument, tradable, creationTime);
/* 610 */     if ((this.runningStrategy != null) && (!isStrategyStopping()))
/* 611 */       this.runningStrategy.onMessage(instrumentStatusMessage, false);
/*     */   }
/*     */ 
/*     */   public void onConnect(boolean value)
/*     */   {
/* 616 */     if ((this.connected == null) || (this.connected.booleanValue() != value)) {
/* 617 */       ConnectionStatusMessageImpl connectionStatusMessage = new ConnectionStatusMessageImpl(value, FeedDataProvider.getDefaultInstance().getCurrentTime());
/* 618 */       if ((this.runningStrategy != null) && (!isStrategyStopping())) {
/* 619 */         this.runningStrategy.onMessage(connectionStatusMessage, false);
/*     */       }
/* 621 */       this.connected = Boolean.valueOf(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isConnected() {
/* 626 */     return (this.connected == null) || (this.connected.booleanValue());
/*     */   }
/*     */ 
/*     */   public void setSystemListener(ISystemListenerExtended systemListener) {
/* 630 */     this.systemListener = systemListener;
/*     */   }
/*     */ 
/*     */   public void setStrategyEventsListener(StrategyEventsListener strategyEventsListener) {
/* 634 */     this.strategyEventsListener = strategyEventsListener;
/*     */   }
/*     */ 
/*     */   public StrategyEventsListener getStrategyEventsListener() {
/* 638 */     return this.strategyEventsListener;
/*     */   }
/*     */ 
/*     */   public TransportClient getTransportClient() {
/* 642 */     return this.transportClient;
/*     */   }
/*     */ 
/*     */   public OrdersInternalCollection getOrdersInternalCollection() {
/* 646 */     return this.ordersInternalCollection;
/*     */   }
/*     */ 
/*     */   public IStrategyExceptionHandler getExceptionHandler() {
/* 650 */     return this.exceptionHandler;
/*     */   }
/*     */ 
/*     */   public long getStrategyId() {
/* 654 */     return this.strategyId;
/*     */   }
/*     */ 
/*     */   public boolean isGlobal() {
/* 658 */     return this.lastAccountInfo.isGlobal();
/*     */   }
/*     */ 
/*     */   public Currency getAccountCurrency() {
/* 662 */     return this.lastAccountInfo.getCurrency();
/*     */   }
/*     */ 
/*     */   public String getUserId() {
/* 666 */     return this.lastAccountInfo.getUserId();
/*     */   }
/*     */ 
/*     */   public String getAccountLoginId() {
/* 670 */     return this.lastAccountInfo.getAcountLoginId();
/*     */   }
/*     */ 
/*     */   public IAccount getAccount() {
/* 674 */     return this.account;
/*     */   }
/*     */ 
/*     */   public BigDecimal getUsableMargin() {
/* 678 */     Money margin = this.lastAccountInfo.getUsableMargin();
/* 679 */     return margin == null ? null : margin.getValue();
/*     */   }
/*     */ 
/*     */   public Integer getLeverage() {
/* 683 */     return this.lastAccountInfo.getLeverage();
/*     */   }
/*     */ 
/*     */   public String getStrategyKey() {
/* 687 */     return this.strategyKey;
/*     */   }
/*     */ 
/*     */   public String getUID() {
/* 691 */     return String.format("%1$s:%2$s:%3$s", new Object[] { Integer.valueOf(hashCode()), this.sessionID, this.strategyKey });
/*     */   }
/*     */ 
/*     */   public void setSubscribedInstruments(Set<Instrument> requiredInstruments) {
/* 695 */     if (requiredInstruments == null) {
/* 696 */       requiredInstruments = new HashSet(0);
/*     */     }
/* 698 */     this.requiredInstruments = requiredInstruments;
/* 699 */     ISystemListenerExtended systemListener = this.systemListener;
/* 700 */     if (systemListener != null)
/* 701 */       systemListener.subscribeToInstruments(requiredInstruments);
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getSubscribedInstruments()
/*     */   {
/* 706 */     ISystemListenerExtended systemListener = this.systemListener;
/* 707 */     if (systemListener != null) {
/* 708 */       return systemListener.getSubscribedInstruments();
/*     */     }
/* 710 */     return new HashSet(0);
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getRequiredInstruments()
/*     */   {
/* 715 */     if ((this.runningStrategy != null) && (!isStrategyStopping())) {
/* 716 */       return this.requiredInstruments;
/*     */     }
/* 718 */     return new HashSet(0);
/*     */   }
/*     */ 
/*     */   public String getStrategyName()
/*     */   {
/* 723 */     return this.runningStrategy.getStrategy().getClass().getSimpleName();
/*     */   }
/*     */ 
/*     */   public Environment getEnvironment() {
/* 727 */     return this.environment;
/*     */   }
/*     */ 
/*     */   public String getSessionID() {
/* 731 */     return this.sessionID;
/*     */   }
/*     */ 
/*     */   public String getInternalIP() {
/* 735 */     return this.internalIP;
/*     */   }
/*     */ 
/*     */   public String getExternalIP() {
/* 739 */     return this.externalIP;
/*     */   }
/*     */ 
/*     */   public void orderSynch(OrderSyncMessage orderSyncMessage) {
/* 743 */     if ((this.runningStrategy == null) || (isStrategyStopping())) {
/* 744 */       return;
/*     */     }
/* 746 */     Collection positionIds = orderSyncMessage.getPositionIds();
/* 747 */     Collection orderIds = orderSyncMessage.getOrderIds();
/*     */ 
/* 749 */     if (!isGlobal()) {
/* 750 */       assert (orderIds.isEmpty());
/* 751 */       List orders = this.ordersInternalCollection.allAsOrders();
/* 752 */       for (IOrder iOrder : orders) {
/* 753 */         PlatformOrderImpl order = (PlatformOrderImpl)iOrder;
/* 754 */         String positionId = order.getId();
/* 755 */         if (!positionIds.contains(positionId))
/*     */         {
/* 757 */           LOGGER.warn("Order group id [" + positionId + "] doesn't exist anymore");
/* 758 */           if (order.getState() == IOrder.State.CREATED) {
/* 759 */             NotificationMessage message = new NotificationMessage();
/* 760 */             message.setTimestamp(new Date());
/* 761 */             message.setExternalSysId(order.getLabel());
/* 762 */             message.setUserId(getUserId());
/* 763 */             message.setText("Your order has been rejected");
/* 764 */             message.setNotificationCode(NotificationMessageCode.SYSTEM_UNAVAILABLE);
/* 765 */             message.setLevel("ERROR");
/* 766 */             this.runningStrategy.updateOrder(message);
/* 767 */           } else if (order.getState() == IOrder.State.OPENED) {
/* 768 */             OrderGroupMessage message = new OrderGroupMessage();
/* 769 */             message.setTimestamp(new Date());
/* 770 */             message.setUserId(getUserId());
/* 771 */             message.setAcountLoginId(getAccountLoginId());
/* 772 */             message.setAmount(new Money(BigDecimal.ZERO, order.getInstrument().getPrimaryCurrency()));
/* 773 */             message.setOrderGroupId(positionId);
/* 774 */             message.setIsOcoMerge(Boolean.valueOf(false));
/* 775 */             message.setInstrument(order.getInstrument().toString());
/* 776 */             this.runningStrategy.updateOrder(message);
/* 777 */           } else if (order.getState() == IOrder.State.FILLED) {
/* 778 */             FeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/* 779 */             OrderHistoricalData[] histOrder = new OrderHistoricalData[1];
/*     */             try {
/* 781 */               feedDataProvider.loadOrdersHistoricalData(order.getInstrument(), order.getFillTime() - 5L, feedDataProvider.getCurrentTime(), new OrdersListener(positionId, histOrder)
/*     */               {
/*     */                 public void newOrder(Instrument instrument, OrderHistoricalData orderData) {
/* 784 */                   if (orderData.getOrderGroupId().equals(this.val$positionId))
/* 785 */                     this.val$histOrder[0] = orderData;
/*     */                 }
/*     */ 
/*     */                 public void orderChange(Instrument instrument, OrderHistoricalData orderData)
/*     */                 {
/*     */                 }
/*     */ 
/*     */                 public void orderMerge(Instrument instrument, OrderHistoricalData resultingOrderData, List<OrderHistoricalData> mergedOrdersData)
/*     */                 {
/*     */                 }
/*     */ 
/*     */                 public void ordersInvalidated(Instrument instrument)
/*     */                 {
/*     */                 }
/*     */               }
/*     */               , new LoadingProgressListener(histOrder, order, positionId)
/*     */               {
/*     */                 public void dataLoaded(long start, long end, long currentPosition, String information)
/*     */                 {
/*     */                 }
/*     */ 
/*     */                 public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e)
/*     */                 {
/* 807 */                   OrderGroupMessage message = new OrderGroupMessage();
/* 808 */                   OrderHistoricalData.CloseData closeData = null;
/* 809 */                   if (this.val$histOrder[0] != null) {
/* 810 */                     for (OrderHistoricalData.CloseData closeData_ : this.val$histOrder[0].getCloseDataMap().values()) {
/* 811 */                       closeData = closeData_;
/*     */                     }
/*     */                   }
/* 814 */                   message.setTimestamp(closeData == null ? new Date() : new Date(closeData.getCloseTime()));
/* 815 */                   message.setUserId(JForexTaskManager.this.getUserId());
/* 816 */                   message.setAcountLoginId(JForexTaskManager.this.getAccountLoginId());
/* 817 */                   message.setAmount(new Money(BigDecimal.ZERO, this.val$order.getInstrument().getPrimaryCurrency()));
/* 818 */                   message.setOrderGroupId(this.val$positionId);
/* 819 */                   Money money = new Money(closeData == null ? BigDecimal.ZERO : closeData.getClosePrice(), this.val$order.getInstrument().getPrimaryCurrency());
/* 820 */                   message.setPriceOpen(money);
/* 821 */                   message.setPricePosOpen(money);
/* 822 */                   message.setIsOcoMerge(Boolean.valueOf(false));
/* 823 */                   message.setInstrument(this.val$order.getInstrument().toString());
/* 824 */                   JForexTaskManager.this.runningStrategy.updateOrder(message);
/*     */                 }
/*     */ 
/*     */                 public boolean stopJob()
/*     */                 {
/* 829 */                   return false;
/*     */                 } } );
/*     */             } catch (DataCacheException e) {
/* 833 */               LOGGER.error(e.getMessage(), e);
/* 834 */               OrderGroupMessage message = new OrderGroupMessage();
/* 835 */               message.setTimestamp(new Date());
/* 836 */               message.setUserId(getUserId());
/* 837 */               message.setAcountLoginId(getAccountLoginId());
/* 838 */               message.setAmount(new Money(BigDecimal.ZERO, order.getInstrument().getPrimaryCurrency()));
/* 839 */               message.setOrderGroupId(positionId);
/* 840 */               message.setPriceOpen(new Money(BigDecimal.ZERO, order.getInstrument().getPrimaryCurrency()));
/* 841 */               message.setPricePosOpen(new Money(BigDecimal.ZERO, order.getInstrument().getPrimaryCurrency()));
/* 842 */               message.setIsOcoMerge(Boolean.valueOf(false));
/* 843 */               message.setInstrument(order.getInstrument().toString());
/* 844 */               this.runningStrategy.updateOrder(message);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     } else {
/* 850 */       List orders = this.ordersInternalCollection.allAsOrders();
/* 851 */       for (IOrder iOrder : orders) {
/* 852 */         PlatformOrderImpl order = (PlatformOrderImpl)iOrder;
/* 853 */         String positionId = order.getId();
/* 854 */         String openingOrderId = order.getOpeningOrderId();
/* 855 */         if ((!positionIds.contains(positionId)) && (!orderIds.contains(openingOrderId)))
/*     */         {
/* 857 */           if (ObjectUtils.isNullOrEmpty(order.getInstrument())) {
/* 858 */             LOGGER.error("Order: {} has not assigned instrument", order);
/* 859 */             continue;
/*     */           }
/* 861 */           if (positionId.endsWith(order.getInstrument().toString()))
/*     */           {
/* 863 */             LOGGER.warn("Position id [" + positionId + "] doesn't exist anymore");
/* 864 */             OrderGroupMessage message = new OrderGroupMessage();
/* 865 */             message.setTimestamp(new Date());
/* 866 */             message.setUserId(getUserId());
/* 867 */             message.setAmount(new Money(BigDecimal.ZERO, order.getInstrument().getPrimaryCurrency()));
/* 868 */             message.setOrderGroupId(positionId);
/* 869 */             message.setIsOcoMerge(Boolean.valueOf(false));
/* 870 */             message.setSide(order.getOrderCommand().isLong() ? PositionSide.LONG : PositionSide.SHORT);
/* 871 */             message.setInstrument(order.getInstrument().toString());
/* 872 */             message.setPriceOpen(new Money(BigDecimal.ZERO, order.getInstrument().getPrimaryCurrency()));
/* 873 */             message.setPricePosOpen(new Money(BigDecimal.ZERO, order.getInstrument().getPrimaryCurrency()));
/* 874 */             this.runningStrategy.updateOrder(message);
/*     */           } else {
/* 876 */             LOGGER.warn("Order id [" + openingOrderId + "] doesn't exist anymore");
/*     */ 
/* 878 */             if (order.getState() == IOrder.State.CREATED) {
/* 879 */               NotificationMessage message = new NotificationMessage();
/* 880 */               message.setTimestamp(new Date());
/* 881 */               message.setExternalSysId(order.getLabel());
/* 882 */               message.setUserId(getUserId());
/* 883 */               message.setText("Your order has been rejected");
/* 884 */               message.setNotificationCode(NotificationMessageCode.INVALID_ORDER);
/* 885 */               message.setLevel("ERROR");
/* 886 */               this.runningStrategy.updateOrder(message);
/* 887 */             } else if (order.getState() == IOrder.State.OPENED) {
/* 888 */               OrderMessage message = new OrderMessage();
/* 889 */               message.setTimestamp(new Date());
/* 890 */               message.setUserId(getUserId());
/* 891 */               message.setAcountLoginId(getAccountLoginId());
/* 892 */               message.setPlaceOffer(Boolean.valueOf((order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID) || (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER)));
/* 893 */               message.setIsMcOrder(Boolean.valueOf(false));
/* 894 */               message.setOrderState(OrderState.CANCELLED);
/* 895 */               message.setAmount(new Money(BigDecimal.valueOf(order.getAmount()), order.getInstrument().getPrimaryCurrency()));
/* 896 */               message.setExternalSysId(order.getLabel());
/* 897 */               message.setOrderId(openingOrderId);
/* 898 */               message.setPriceClient(new Money(BigDecimal.valueOf(order.getOpenPrice()), order.getInstrument().getPrimaryCurrency()));
/* 899 */               message.setCreatedDate(new Date(order.getCreationTime()));
/* 900 */               message.setOrderDirection(OrderDirection.OPEN);
/* 901 */               message.setOco(Boolean.valueOf(false));
/* 902 */               message.setInstrument(order.getInstrument().toString());
/* 903 */               message.setSide(order.isLong() ? OrderSide.BUY : OrderSide.SELL);
/* 904 */               StopDirection stopDirection = null;
/* 905 */               switch (5.$SwitchMap$com$dukascopy$api$IEngine$OrderCommand[order.getOrderCommand().ordinal()]) {
/*     */               case 1:
/* 907 */                 stopDirection = StopDirection.ASK_EQUALS;
/* 908 */                 break;
/*     */               case 2:
/* 910 */                 stopDirection = StopDirection.BID_EQUALS;
/* 911 */                 break;
/*     */               case 3:
/* 913 */                 stopDirection = StopDirection.ASK_GREATER;
/* 914 */                 break;
/*     */               case 4:
/* 916 */                 stopDirection = StopDirection.BID_LESS;
/* 917 */                 break;
/*     */               case 5:
/* 919 */                 stopDirection = StopDirection.BID_GREATER;
/* 920 */                 break;
/*     */               case 6:
/* 922 */                 stopDirection = StopDirection.ASK_LESS;
/*     */               }
/*     */ 
/* 925 */               if (stopDirection != null) {
/* 926 */                 message.setStopDirection(stopDirection);
/*     */               }
/* 928 */               message.setPriceStop(new Money(BigDecimal.valueOf(order.getOpenPrice()), order.getInstrument().getPrimaryCurrency()));
/* 929 */               message.setParentOrderId(positionId);
/* 930 */               this.runningStrategy.updateOrder(message);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addConfigurationChangeListener(String parameter, PropertyChangeListener listener) {
/* 939 */     if (this.propertyChangeSupport != null)
/* 940 */       this.propertyChangeSupport.addPropertyChangeListener(parameter, listener);
/*     */   }
/*     */ 
/*     */   public void removeConfigurationChangeListener(String parameter, PropertyChangeListener listener)
/*     */   {
/* 945 */     if (this.propertyChangeSupport != null)
/* 946 */       this.propertyChangeSupport.removePropertyChangeListener(parameter, listener);
/*     */   }
/*     */ 
/*     */   public void fireConfigurationPropertyChange(PropertyChangeEvent event)
/*     */   {
/* 951 */     if (this.propertyChangeSupport != null)
/* 952 */       this.propertyChangeSupport.firePropertyChange(event);
/*     */   }
/*     */ 
/*     */   public IEngine getEngine()
/*     */   {
/* 978 */     return this.forexEngineImpl;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  88 */     LOGGER = LoggerFactory.getLogger(JForexTaskManager.class);
/*     */ 
/*  90 */     ONE_MILLION = BigDecimal.valueOf(1000000L);
/*     */   }
/*     */ 
/*     */   class StopCallable
/*     */     implements Callable<Object>
/*     */   {
/*     */     StopCallable()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object call()
/*     */       throws Exception
/*     */     {
/* 959 */       if (JForexTaskManager.this.runningStrategy != null) {
/* 960 */         IStrategy strategy = JForexTaskManager.this.runningStrategy.getStrategy();
/*     */         try {
/* 962 */           JForexTaskManager.this.runningStrategy.onStop();
/*     */ 
/* 966 */           JForexTaskManager.this.fireOnStop(strategy);
/*     */         } catch (Throwable t) {
/* 968 */           JForexTaskManager.LOGGER.error(t.getMessage(), t);
/* 969 */           String msg = StrategyWrapper.representError(strategy, t);
/* 970 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, t, false);
/*     */         }
/*     */       }
/* 973 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum Environment
/*     */   {
/* 130 */     LOCAL_JFOREX, LOCAL_EMBEDDED, REMOTE;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.JForexTaskManager
 * JD-Core Version:    0.6.0
 */