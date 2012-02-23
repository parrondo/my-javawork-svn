/*     */ package com.dukascopy.dds2.greed.connection;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.impl.connect.AuthorizationClient;
/*     */ import com.dukascopy.api.impl.connect.PlatformCalendarMessageImpl;
/*     */ import com.dukascopy.api.impl.connect.PlatformNewsMessageImpl;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.OrderGroupListener;
/*     */ import com.dukascopy.charts.data.datacache.TickListener;
/*     */ import com.dukascopy.charts.data.datacache.feed.IFeedCommissionManager;
/*     */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.AccountUpdateAction;
/*     */ import com.dukascopy.dds2.greed.actions.AuthenticatedAction;
/*     */ import com.dukascopy.dds2.greed.actions.DisconnectAction;
/*     */ import com.dukascopy.dds2.greed.actions.KickAction;
/*     */ import com.dukascopy.dds2.greed.actions.MarketStateAction;
/*     */ import com.dukascopy.dds2.greed.actions.NewsUpdateAction;
/*     */ import com.dukascopy.dds2.greed.actions.OrderGroupUpdateAction;
/*     */ import com.dukascopy.dds2.greed.actions.OrderMessageUpdateAction;
/*     */ import com.dukascopy.dds2.greed.actions.PostMessageAction;
/*     */ import com.dukascopy.dds2.greed.actions.ReconnectInfoAction;
/*     */ import com.dukascopy.dds2.greed.actions.RemoteStrategyRunErrorResponseAction;
/*     */ import com.dukascopy.dds2.greed.actions.RemoteStrategyRunResponseAction;
/*     */ import com.dukascopy.dds2.greed.actions.RemoteStrategyUpdateAction;
/*     */ import com.dukascopy.dds2.greed.actions.UpdateTimeAction;
/*     */ import com.dukascopy.dds2.greed.actions.UpdateTradabilityAction;
/*     */ import com.dukascopy.dds2.greed.actions.dowjones.CalendarActionEvent;
/*     */ import com.dukascopy.dds2.greed.actions.dowjones.NewsActionEvent;
/*     */ import com.dukascopy.dds2.greed.agent.Strategies;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.LoginPanel.LoginTimer;
/*     */ import com.dukascopy.dds2.greed.gui.component.menu.MainMenu;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.mt.AgentManager;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.transport.client.ClientListener;
/*     */ import com.dukascopy.transport.client.TransportClient;
/*     */ import com.dukascopy.transport.client.events.DisconnectedEvent;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.NotificationMessageCode;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.MarketNewsMessageGroup;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderSyncMessage;
/*     */ import com.dukascopy.transport.common.msg.news.CalendarEvent;
/*     */ import com.dukascopy.transport.common.msg.news.NewsSource;
/*     */ import com.dukascopy.transport.common.msg.news.NewsStoryMessage;
/*     */ import com.dukascopy.transport.common.msg.news.PlainContent;
/*     */ import com.dukascopy.transport.common.msg.properties.UserPropertiesChangeMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyQuoteMessage;
/*     */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*     */ import com.dukascopy.transport.common.msg.request.UserControlMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*     */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.OrderResponse;
/*     */ import com.dukascopy.transport.common.msg.response.TimeSyncResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyBroadcastMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyRunErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyRunResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyStateMessage;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.MathContext;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.swing.JMenuItem;
/*     */ import org.json.JSONObject;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class GreedClientListener
/*     */   implements ClientListener
/*     */ {
/*  98 */   private static Logger LOGGER = LoggerFactory.getLogger(GreedClientListener.class);
/*     */ 
/* 100 */   private static GreedClientListener instance = null;
/*     */   private static final String AUTORECONNECT_KEY = "AUTORECONNECT";
/* 102 */   private static int MAX_LIGHT_RECONNECT_RETRIES = 2;
/*     */ 
/* 104 */   private static int MAX_RECONNECT_RETRIES = 250;
/* 105 */   private static int MAX_FORCED_RECONNECT_RETRIES = 2147483647;
/*     */   private static final int SYNC_INTERVAL = 30;
/* 109 */   private int timeUpdateCounter = -1;
/* 110 */   private boolean autoReconnect = true;
/*     */ 
/* 112 */   private static String previousAuthServerResponse = "";
/*     */ 
/* 114 */   private AtomicInteger retriesCounter = new AtomicInteger(0);
/*     */ 
/* 116 */   private final ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/*     */   private GreedClientListener()
/*     */   {
/* 122 */     GreedContext.setConfig("AUTORECONNECT", Boolean.TRUE);
/*     */   }
/*     */ 
/*     */   public static GreedClientListener getInstance() {
/* 126 */     if (instance == null) {
/* 127 */       instance = new GreedClientListener();
/*     */     }
/* 129 */     return instance;
/*     */   }
/*     */ 
/*     */   public static void killListener() {
/* 133 */     instance = null;
/*     */   }
/*     */ 
/*     */   public void authorized(TransportClient transportClient)
/*     */   {
/*     */     try
/*     */     {
/* 143 */       LOGGER.info("Authorized");
/*     */ 
/* 145 */       this.autoReconnect = true;
/*     */ 
/* 147 */       GreedContext.setConfig("AUTORECONNECT", Boolean.TRUE);
/* 148 */       GreedContext.setConfig("logoff", Boolean.FALSE);
/* 149 */       GreedContext.setConfig("backend.settings.updated", "false");
/*     */ 
/* 151 */       enableReconnectMenuItem(true);
/*     */ 
/* 153 */       resetAutoReconnecting();
/*     */ 
/* 155 */       GreedContext.publishEvent(new AuthenticatedAction(transportClient));
/*     */     } catch (RuntimeException e) {
/* 157 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void feedbackMessageReceived(TransportClient transportClient, ProtocolMessage protocolMessage)
/*     */   {
/*     */     try
/*     */     {
/* 169 */       if (protocolMessage != null)
/*     */       {
/* 171 */         if (protocolMessage.getString("t") != null) {
/* 172 */           onTimeSyncResponseMessage(protocolMessage);
/*     */         }
/*     */ 
/* 176 */         if (null != protocolMessage.getTimestamp()) {
/* 177 */           GreedContext.setConfig("timestamp", protocolMessage.getString("timestamp"));
/*     */         }
/*     */ 
/* 180 */         if ((LOGGER.isDebugEnabled()) && (protocolMessage.getTimestamp() != null) && (protocolMessage.getTimeSyncMs() != null)) {
/* 181 */           LOGGER.debug("timestamp = " + protocolMessage.getString("timestamp") + " timeSync = " + protocolMessage.getTimeSyncMs());
/*     */         }
/*     */ 
/* 184 */         if ((protocolMessage instanceof CurrencyMarket))
/* 185 */           onCurrencyMarket(protocolMessage);
/* 186 */         else if ((protocolMessage instanceof OrderGroupMessage))
/* 187 */           onOrderGroupMessage(protocolMessage);
/* 188 */         else if ((protocolMessage instanceof OrderMessage))
/* 189 */           onOrderMessage(protocolMessage);
/* 190 */         else if ((protocolMessage instanceof ErrorResponseMessage))
/* 191 */           onErrorResponseMessage(protocolMessage);
/* 192 */         else if ((protocolMessage instanceof NotificationMessage))
/* 193 */           onNotificationMessage(protocolMessage);
/* 194 */         else if ((protocolMessage instanceof InstrumentStatusUpdateMessage))
/* 195 */           onInstrumentStatusUpdateMessage(protocolMessage);
/* 196 */         else if ((protocolMessage instanceof AccountInfoMessage))
/* 197 */           onAccountInfoMessage(protocolMessage);
/* 198 */         else if ((protocolMessage instanceof TimeSyncResponseMessage))
/* 199 */           onTimeSyncResponseMessage(protocolMessage);
/* 200 */         else if ((protocolMessage instanceof OrderSyncMessage))
/* 201 */           onOrderSyncMessage(protocolMessage);
/* 202 */         else if ((protocolMessage instanceof MarketNewsMessageGroup))
/* 203 */           onMarketNewsUpdateMessageGroup(protocolMessage);
/* 204 */         else if ((protocolMessage instanceof MergePositionsMessage))
/* 205 */           onMergePositionsMessage(protocolMessage);
/* 206 */         else if ((protocolMessage instanceof UserControlMessage))
/* 207 */           onUserControlMessageAction(protocolMessage);
/* 208 */         else if ((protocolMessage instanceof NewsStoryMessage))
/* 209 */           onNewsStoryMessage((NewsStoryMessage)protocolMessage);
/* 210 */         else if ((protocolMessage instanceof StrategyRunErrorResponseMessage))
/* 211 */           onStrategyRunErrorResponseMessage((StrategyRunErrorResponseMessage)protocolMessage);
/* 212 */         else if ((protocolMessage instanceof StrategyRunResponseMessage))
/* 213 */           onStrategyRunResponseMessage((StrategyRunResponseMessage)protocolMessage);
/* 214 */         else if ((protocolMessage instanceof StrategyStateMessage))
/* 215 */           onStrategyStateMessage((StrategyStateMessage)protocolMessage);
/* 216 */         else if ((protocolMessage instanceof StrategyBroadcastMessage))
/* 217 */           onStrategyBroadcastMessage((StrategyBroadcastMessage)protocolMessage);
/* 218 */         else if ((protocolMessage instanceof UserPropertiesChangeMessage))
/* 219 */           onUserPropertiesChange((UserPropertiesChangeMessage)protocolMessage);
/* 220 */         else if ((protocolMessage instanceof OrderResponse)) {
/* 221 */           onSettelmentMessage((OrderResponse)protocolMessage);
/*     */         }
/* 223 */         else if (!(protocolMessage instanceof OkResponseMessage))
/* 224 */           LOGGER.debug("No processor for Message: " + protocolMessage);
/*     */       }
/*     */       else
/*     */       {
/* 228 */         LOGGER.error("Null message received!");
/*     */       }
/*     */     } catch (RuntimeException e) {
/* 231 */       LOGGER.error("Feedback received catched and throwed: " + e.getMessage());
/* 232 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void onStrategyRunResponseMessage(StrategyRunResponseMessage protocolMessage)
/*     */   {
/* 240 */     RemoteStrategyRunResponseAction runResponseAction = new RemoteStrategyRunResponseAction(this, protocolMessage.getStrategyProcessDescriptor());
/* 241 */     GreedContext.publishEvent(runResponseAction);
/*     */   }
/*     */ 
/*     */   private void onStrategyRunErrorResponseMessage(StrategyRunErrorResponseMessage errorMessage)
/*     */   {
/* 248 */     ErrorResponseMessage error = new ErrorResponseMessage(errorMessage);
/* 249 */     onErrorResponseMessage(error);
/* 250 */     RemoteStrategyRunErrorResponseAction runErrorResponseAction = new RemoteStrategyRunErrorResponseAction(this, errorMessage);
/* 251 */     GreedContext.publishEvent(runErrorResponseAction);
/*     */   }
/*     */ 
/*     */   private void onStrategyStateMessage(StrategyStateMessage protocolMessage)
/*     */   {
/* 258 */     RemoteStrategyUpdateAction strategyUpdateAction = new RemoteStrategyUpdateAction(this, protocolMessage);
/* 259 */     GreedContext.publishEvent(strategyUpdateAction);
/*     */   }
/*     */ 
/*     */   private void onOrderMessage(ProtocolMessage protocolMessage)
/*     */   {
/* 264 */     if (LOGGER.isDebugEnabled()) {
/* 265 */       LOGGER.debug("OrderMessage : {}", protocolMessage);
/*     */     }
/* 267 */     if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended())) {
/* 268 */       OrderMessage orderMessage = (OrderMessage)protocolMessage;
/*     */ 
/* 270 */       GreedContext.publishEvent(new OrderMessageUpdateAction(this, orderMessage));
/* 271 */       OrderGroupListener orderGroupListener = (OrderGroupListener)GreedContext.get("ordersDataProvider");
/* 272 */       orderGroupListener.updateOrder(orderMessage);
/* 273 */       if (GreedContext.isStrategyAllowed())
/* 274 */         Strategies.get().onOrderReceived(orderMessage);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void onAccountInfoMessage(ProtocolMessage protocolMessage)
/*     */   {
/* 280 */     if ((protocolMessage instanceof AccountInfoMessage)) {
/* 281 */       AccountInfoMessage accountInfo = (AccountInfoMessage)protocolMessage;
/*     */ 
/* 283 */       GreedContext.setConfig("AccountInfoMessage", accountInfo);
/* 284 */       if (LOGGER.isTraceEnabled()) {
/* 285 */         LOGGER.trace(accountInfo.toString());
/*     */       }
/*     */ 
/* 288 */       FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/* 289 */       if (accountInfo.getTimestamp() != null) {
/* 290 */         feedDataProvider.setCurrentTime(accountInfo.getTimestamp().getTime());
/*     */       }
/*     */ 
/* 293 */       Map feedCommssionsMap = accountInfo.getFeedCommissions();
/* 294 */       if (feedCommssionsMap != null) {
/* 295 */         feedDataProvider.getFeedCommissionManager().addFeedCommissions(feedCommssionsMap);
/*     */       }
/*     */ 
/* 298 */       OrdersProvider orderGroupListener = (OrdersProvider)GreedContext.get("ordersDataProvider");
/* 299 */       orderGroupListener.updateAccountInfoData(accountInfo);
/* 300 */       if (GreedContext.isStrategyAllowed()) {
/* 301 */         Strategies.get().updateAccountInfo(accountInfo);
/*     */       }
/*     */ 
/* 305 */       ((AccountStatement)GreedContext.get("accountStatement")).onAccountInfo(accountInfo);
/* 306 */       AccountUpdateAction accountUpdate = new AccountUpdateAction(this, accountInfo);
/* 307 */       GreedContext.publishEvent(accountUpdate);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void onTimeSyncResponseMessage(ProtocolMessage protocolMessage) {
/* 312 */     if (--this.timeUpdateCounter <= 0) {
/* 313 */       GreedContext.publishEvent(new UpdateTimeAction(this, protocolMessage));
/* 314 */       this.timeUpdateCounter = 30;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void onMarketNewsUpdateMessageGroup(ProtocolMessage protocolMessage) {
/* 319 */     if ("api".equals(GreedContext.getStringProperty("news.source"))) {
/* 320 */       MarketNewsMessageGroup mnmg = (MarketNewsMessageGroup)protocolMessage;
/* 321 */       NewsUpdateAction nua = new NewsUpdateAction(this, mnmg);
/* 322 */       GreedContext.publishEvent(nua);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void onOrderSyncMessage(ProtocolMessage protocolMessage) {
/* 327 */     if (LOGGER.isDebugEnabled()) {
/* 328 */       LOGGER.debug("OrderSyncMessage : {}", protocolMessage);
/*     */     }
/*     */ 
/* 331 */     OrdersProvider.getInstance().orderSynch((OrderSyncMessage)protocolMessage);
/* 332 */     if (GreedContext.isStrategyAllowed()) {
/* 333 */       Strategies.get().orderSynch((OrderSyncMessage)protocolMessage);
/*     */     }
/*     */ 
/* 336 */     if (GreedContext.isStrategyAllowed())
/* 337 */       Strategies.get().onConnect(true);
/*     */   }
/*     */ 
/*     */   private void onInstrumentStatusUpdateMessage(ProtocolMessage protocolMessage)
/*     */   {
/* 342 */     GreedContext.publishEvent(new UpdateTradabilityAction(this, (InstrumentStatusUpdateMessage)protocolMessage));
/* 343 */     if (GreedContext.isStrategyAllowed())
/* 344 */       Strategies.get().onInstrumentStatusUpdate((InstrumentStatusUpdateMessage)protocolMessage);
/*     */   }
/*     */ 
/*     */   private void onNotificationMessage(ProtocolMessage protocolMessage)
/*     */   {
/* 349 */     NotificationMessage notificationMessage = (NotificationMessage)protocolMessage;
/*     */ 
/* 351 */     if (LOGGER.isDebugEnabled()) {
/* 352 */       LOGGER.debug("Notification message : {}", notificationMessage);
/*     */     }
/*     */ 
/* 355 */     if (notificationMessage.getNotificationCode() == NotificationMessageCode.NEWS_REQUEST_LIMIT_EXCEEDED) {
/* 356 */       GreedContext.publishEvent(new CalendarActionEvent(this, null));
/* 357 */       return;
/*     */     }
/*     */ 
/* 360 */     if (GreedContext.isStrategyAllowed()) {
/* 361 */       Strategies.get().onNotifyMessage((NotificationMessage)protocolMessage);
/* 362 */       AgentManager agent = (AgentManager)GreedContext.get("ddsAgent");
/* 363 */       agent.onNotifyMessage(protocolMessage);
/*     */     }
/*     */ 
/* 366 */     Notification notification = new Notification(notificationMessage.getTimestamp(), notificationMessage.getText());
/* 367 */     notification.setServerTimestamp(GreedContext.getPlatformTimeForLogger());
/* 368 */     notification.setPriority(notificationMessage.getLevel());
/* 369 */     PostMessageAction post = new PostMessageAction(this, notification, notificationMessage.getTTL());
/* 370 */     GreedContext.publishEvent(post);
/*     */   }
/*     */ 
/*     */   private void onErrorResponseMessage(ProtocolMessage protocolMessage) {
/* 374 */     ErrorResponseMessage error = (ErrorResponseMessage)protocolMessage;
/* 375 */     if (LOGGER.isDebugEnabled()) {
/* 376 */       LOGGER.debug("ErrorResponseMessage: {}", error);
/*     */     }
/* 378 */     Notification notification = new Notification(error.getTimestamp(), error.getReason());
/* 379 */     notification.setPriority("ERROR");
/* 380 */     PostMessageAction post = new PostMessageAction(this, notification);
/* 381 */     GreedContext.publishEvent(post);
/*     */   }
/*     */ 
/*     */   private void onOrderGroupMessage(ProtocolMessage protocolMessage) {
/* 385 */     OrderGroupMessage orderGroup = (OrderGroupMessage)protocolMessage;
/* 386 */     if (LOGGER.isDebugEnabled()) {
/* 387 */       LOGGER.debug("OrderGroupMessage: {}", orderGroup);
/*     */     }
/*     */ 
/* 390 */     OrderGroupUpdateAction update = new OrderGroupUpdateAction(this, orderGroup);
/* 391 */     GreedContext.publishEvent(update);
/*     */ 
/* 393 */     OrderGroupListener orderGroupListener = (OrderGroupListener)GreedContext.get("ordersDataProvider");
/* 394 */     orderGroupListener.updateOrderGroup(orderGroup);
/* 395 */     if (GreedContext.isStrategyAllowed()) {
/* 396 */       Strategies.get().onOrderGroupReceived((OrderGroupMessage)protocolMessage);
/* 397 */       AgentManager agent = (AgentManager)GreedContext.get("ddsAgent");
/* 398 */       agent.onOrderGroupReceived((OrderGroupMessage)protocolMessage);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void onMergePositionsMessage(ProtocolMessage protocolMessage) {
/* 403 */     MergePositionsMessage mergePositionsMessage = (MergePositionsMessage)protocolMessage;
/* 404 */     if (LOGGER.isDebugEnabled()) {
/* 405 */       LOGGER.debug("MergePositionsMessage: {}", mergePositionsMessage);
/*     */     }
/*     */ 
/* 408 */     OrderGroupListener orderGroupListener = (OrderGroupListener)GreedContext.get("ordersDataProvider");
/* 409 */     orderGroupListener.groupsMerged(mergePositionsMessage);
/*     */ 
/* 412 */     if (GreedContext.isStrategyAllowed())
/* 413 */       Strategies.get().onOrdersMergedMessage((MergePositionsMessage)protocolMessage);
/*     */   }
/*     */ 
/*     */   private void onCurrencyMarket(ProtocolMessage protocolMessage)
/*     */   {
/* 418 */     if (GreedContext.getConfig("AccountInfoMessage") == null)
/*     */     {
/* 420 */       return;
/*     */     }
/*     */ 
/* 423 */     CurrencyMarket market = (CurrencyMarket)protocolMessage;
/* 424 */     if ((market.getInstrument() == null) || (market.getInstrument().trim().length() <= 0)) {
/* 425 */       LOGGER.error("Instrument is empty, skipping currency market");
/* 426 */       return;
/*     */     }
/*     */ 
/* 436 */     if (GreedContext.get("feedDataProvider") != null) {
/* 437 */       TickListener tickListener = (TickListener)GreedContext.get("feedDataProvider");
/* 438 */       tickListener.tickReceived(market);
/*     */     }
/*     */ 
/* 441 */     if (GreedContext.isStrategyAllowed()) {
/* 442 */       Strategies.get().onMarketState((CurrencyMarket)protocolMessage);
/*     */     }
/*     */ 
/* 445 */     MarketStateAction updateMarketState = new MarketStateAction(this, market);
/* 446 */     GreedContext.publishEvent(updateMarketState);
/*     */   }
/*     */ 
/*     */   private void onUserControlMessageAction(ProtocolMessage protocolMessage) {
/* 450 */     KickAction kickAction = new KickAction(this);
/* 451 */     GreedContext.publishEvent(kickAction);
/*     */   }
/*     */ 
/*     */   private void onNewsStoryMessage(NewsStoryMessage newsStoryMessage) {
/*     */     try {
/* 456 */       JSONObject json = newsStoryMessage.getContent();
/*     */ 
/* 458 */       String copyright = newsStoryMessage.getCopyright();
/* 459 */       Set currencies = newsStoryMessage.getCurrencies();
/* 460 */       Set geoRegions = newsStoryMessage.getGeoRegions();
/* 461 */       String header = newsStoryMessage.getHeader();
/* 462 */       Set marketSectors = newsStoryMessage.getMarketSectors();
/* 463 */       Set stockIndicies = newsStoryMessage.getIndicies();
/* 464 */       String newsId = newsStoryMessage.getNewsId();
/* 465 */       long publishTime = newsStoryMessage.getPublishDate() == null ? 0L : newsStoryMessage.getPublishDate().getTime();
/* 466 */       NewsSource source = newsStoryMessage.getSource();
/* 467 */       boolean endOfStory = newsStoryMessage.isEndOfStory();
/* 468 */       boolean isHot = newsStoryMessage.isHot();
/*     */ 
/* 470 */       if (source == NewsSource.DJ_LIVE_CALENDAR) {
/* 471 */         CalendarEvent calendarEvent = (CalendarEvent)json;
/* 472 */         GreedContext.publishEvent(new CalendarActionEvent(this, new PlatformCalendarMessageImpl(calendarEvent, copyright, header, newsId, publishTime, endOfStory, isHot, currencies, geoRegions, marketSectors, stockIndicies)));
/*     */       }
/* 487 */       else if (source == NewsSource.DJ_NEWSWIRES) {
/* 488 */         PlainContent plainContent = (PlainContent)json;
/* 489 */         GreedContext.publishEvent(new NewsActionEvent(this, new PlatformNewsMessageImpl(plainContent == null ? null : plainContent.getText(), copyright, header, newsId, publishTime, endOfStory, isHot, currencies, geoRegions, marketSectors, stockIndicies)));
/*     */       }
/*     */       else
/*     */       {
/* 505 */         LOGGER.warn("Unsupported news source : {}", source);
/*     */       }
/*     */     } catch (Exception ex) {
/* 508 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/* 510 */     if (GreedContext.isStrategyAllowed())
/* 511 */       Strategies.get().onNewsMessage(newsStoryMessage);
/*     */   }
/*     */ 
/*     */   private void onSettelmentMessage(OrderResponse<CurrencyQuoteMessage> orderResponse)
/*     */   {
/* 516 */     Map priceMap = new HashMap();
/*     */ 
/* 518 */     for (CurrencyQuoteMessage currencyQuoteMessage : orderResponse.getListResponse()) {
/* 519 */       if (Instrument.contains(currencyQuoteMessage.getInstrument())) {
/* 520 */         BigDecimal bid = currencyQuoteMessage.getBid().getPrice().getValue();
/* 521 */         BigDecimal ask = currencyQuoteMessage.getAsk().getPrice().getValue();
/* 522 */         BigDecimal avgSettelmentPrice = bid.add(ask).divide(new BigDecimal(2), new MathContext(5));
/* 523 */         if (avgSettelmentPrice.doubleValue() > 0.0D) {
/* 524 */           priceMap.put(Instrument.fromString(currencyQuoteMessage.getInstrument()), avgSettelmentPrice);
/*     */         }
/*     */       }
/*     */     }
/* 528 */     this.storage.saveContestRates(PlatformInitUtils.calculateSettelmentRates(priceMap));
/*     */   }
/*     */ 
/*     */   private void onUserPropertiesChange(UserPropertiesChangeMessage message) {
/* 532 */     GreedContext.setUserProperties(message.getUserProperties());
/*     */   }
/*     */ 
/*     */   private void onStrategyBroadcastMessage(StrategyBroadcastMessage message)
/*     */   {
/* 537 */     if (GreedContext.isStrategyAllowed())
/* 538 */       Strategies.get().onStrategyBroadcast(message);
/*     */   }
/*     */ 
/*     */   public void disconnected(DisconnectedEvent event)
/*     */   {
/* 547 */     LOGGER.warn("DISCONNECTED : [{}]", event.getReason());
/*     */ 
/* 549 */     if (GreedContext.isStrategyAllowed()) {
/* 550 */       Strategies.get().onConnect(false);
/*     */     }
/*     */ 
/* 553 */     if (LoginPanel.LoginTimer.getInstance().isCanceled()) return;
/*     */ 
/* 555 */     if ((this.autoReconnect) && (!GreedContext.isLogOff())) {
/* 556 */       this.retriesCounter.incrementAndGet();
/*     */ 
/* 558 */       if (this.retriesCounter.get() > MAX_LIGHT_RECONNECT_RETRIES) {
/* 559 */         if (this.retriesCounter.get() >= getMaxRetriesCount()) {
/* 560 */           doDisconnect();
/* 561 */           return;
/*     */         }
/*     */ 
/* 564 */         renewApi(event.getClient());
/*     */ 
/* 566 */         return;
/*     */       }
/*     */ 
/* 569 */       enableReconnectMenuItem(false);
/*     */ 
/* 571 */       doLightConnect(event.getClient());
/* 572 */       LOGGER.info("CONNECTED : [{}]", Boolean.valueOf(event.getClient().isOnline()));
/*     */     } else {
/* 574 */       LOGGER.info("Auto reconnect = " + this.autoReconnect + " isLogoff= " + GreedContext.isLogOff());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void enableReconnectMenuItem(boolean enabled) {
/*     */     try {
/* 580 */       ClientForm clientGui = (ClientForm)GreedContext.get("clientGui");
/* 581 */       clientGui.getMainMenu().getReconnect().setEnabled(enabled);
/*     */     }
/*     */     catch (NullPointerException exc) {
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doLightConnect(TransportClient transportClient) {
/* 588 */     int timeOut = this.retriesCounter.get() == 1 ? 0 : 1;
/*     */     try
/*     */     {
/* 591 */       LOGGER.info("Sleeping for reconnect : {}", Integer.valueOf(timeOut));
/* 592 */       Thread.sleep(TimeUnit.SECONDS.toMillis(timeOut));
/*     */     } catch (InterruptedException ie) {
/*     */     }
/* 595 */     if (!transportClient.isOnline()) {
/* 596 */       LOGGER.warn("CONNECTING attempt # {}", this.retriesCounter);
/*     */       try {
/* 598 */         LOGGER.debug("Transport client connecting ...");
/* 599 */         transportClient.connect();
/* 600 */         LOGGER.debug("Transport client connected");
/*     */       } catch (IllegalStateException e) {
/* 602 */         LOGGER.error("ERROR while connecting : " + e.getMessage(), e);
/*     */       } catch (Exception e) {
/* 604 */         LOGGER.error("Connection error : " + e.getMessage(), e);
/*     */       }
/*     */ 
/* 607 */       if (!transportClient.isOnline()) {
/* 608 */         ReconnectInfoAction reconnectInfo = new ReconnectInfoAction(this);
/* 609 */         GreedContext.publishEvent(reconnectInfo);
/*     */ 
/* 611 */         if (this.retriesCounter.get() > 0) {
/* 612 */           String message = null;
/* 613 */           message = this.retriesCounter.get() <= 1 ? "Disconnected." : "Reconnecting ...";
/*     */ 
/* 615 */           Notification notification = new Notification(Calendar.getInstance().getTime(), message);
/* 616 */           PostMessageAction post = new PostMessageAction(this, notification);
/* 617 */           GreedContext.publishEvent(post);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doDisconnect()
/*     */   {
/* 626 */     doDisconnect(false);
/*     */   }
/*     */ 
/*     */   private void doDisconnect(boolean networkFailure) {
/* 630 */     this.autoReconnect = false;
/* 631 */     GreedContext.setConfig("AUTORECONNECT", Boolean.FALSE);
/* 632 */     GreedContext.publishEvent(new DisconnectAction(this, networkFailure));
/* 633 */     resetAutoReconnecting();
/*     */   }
/*     */ 
/*     */   private void renewApi(TransportClient transportClient) {
/* 637 */     int minute = 60;
/* 638 */     int shortDelay = 5;
/*     */ 
/* 640 */     int timeOut = (this.retriesCounter.get() - 2) % (this.retriesCounter.get() < 10 ? 10 : 11) == 0 ? minute : shortDelay;
/*     */ 
/* 642 */     ReconnectInfoAction reconnectInfo = new ReconnectInfoAction(this);
/* 643 */     GreedContext.publishEvent(reconnectInfo);
/*     */ 
/* 645 */     String message = timeOut == shortDelay ? "Reconnecting ..." : "Unable to connect. One minute timeout.";
/*     */ 
/* 647 */     Notification notification = new Notification(Calendar.getInstance().getTime(), message);
/* 648 */     PostMessageAction post = new PostMessageAction(this, notification);
/* 649 */     GreedContext.publishEvent(post);
/*     */     try
/*     */     {
/* 652 */       Thread.sleep(TimeUnit.SECONDS.toMillis(timeOut));
/*     */     } catch (InterruptedException ie) {
/* 654 */       LOGGER.error(ie.getMessage(), ie);
/*     */     }
/*     */ 
/* 657 */     LOGGER.warn("AUTO RECONNECTING attempt # [{}]", this.retriesCounter);
/*     */ 
/* 659 */     if (!renewApiUrl()) {
/* 660 */       return;
/*     */     }
/*     */ 
/* 663 */     transportClient.connect();
/*     */ 
/* 665 */     if (transportClient.isOnline())
/* 666 */       resetAutoReconnecting();
/*     */   }
/*     */ 
/*     */   public boolean renewApiUrl()
/*     */   {
/* 671 */     GreedTransportClient transportClient = (GreedTransportClient)GreedContext.get("transportClient");
/*     */     try {
/* 673 */       String response = retrieveApiUrl();
/* 674 */       previousAuthServerResponse = response;
/*     */ 
/* 677 */       if ((response == null) || ("-3".equals(response))) {
/* 678 */         LOGGER.warn("Can't connect to API. Response : [{}]", response);
/* 679 */         return true;
/*     */       }
/*     */ 
/* 684 */       if (("-500".equals(response)) || ("-1".equals(response))) {
/* 685 */         LOGGER.warn("Can't connect to API. Response : [{}]", response);
/* 686 */         if (!this.storage.isForceReconnectsActive()) {
/* 687 */           LOGGER.warn("Disconnecting");
/* 688 */           doDisconnect(true);
/* 689 */           return false;
/*     */         }
/* 691 */         return true;
/*     */       }
/*     */ 
/* 695 */       Matcher matcher = AuthorizationClient.RESULT_PATTERN.matcher(response);
/* 696 */       if (!matcher.matches()) {
/* 697 */         LOGGER.warn("Authentication procedure returned unexpected result : [{}]", response);
/*     */ 
/* 699 */         return this.storage.isForceReconnectsActive();
/*     */       }
/*     */ 
/* 705 */       String newApiURL = response.split("@")[0];
/* 706 */       String newTicket = response.split("@")[1];
/* 707 */       if ("null".equals(newApiURL)) {
/* 708 */         LOGGER.warn("Can't connect to API");
/* 709 */         return true;
/*     */       }
/*     */ 
/* 712 */       LOGGER.warn("Trying connect to API : [{}]", newApiURL);
/*     */ 
/* 716 */       int semicolonIndex = newApiURL.indexOf(58);
/*     */       int port;
/*     */       String host;
/*     */       int port;
/* 717 */       if (semicolonIndex != -1) {
/* 718 */         String host = newApiURL.substring(0, semicolonIndex);
/*     */         int port;
/* 719 */         if (semicolonIndex + 1 >= newApiURL.length()) {
/* 720 */           LOGGER.warn("Port isn't set, using default 443");
/* 721 */           port = 443;
/*     */         } else {
/* 723 */           port = Integer.parseInt(newApiURL.substring(semicolonIndex + 1));
/*     */         }
/*     */       } else {
/* 726 */         host = newApiURL;
/* 727 */         port = 443;
/*     */       }
/* 729 */       transportClient.setAddress(new InetSocketAddress(host, port));
/* 730 */       transportClient.setPasswordTicket(newTicket);
/* 731 */       transportClient.setUsername((String)GreedContext.getConfig("account_name"));
/*     */ 
/* 734 */       GreedContext.setConfig("TICKET", newTicket);
/* 735 */       FeedDataProvider.setPlatformTicket(newTicket);
/* 736 */       return true;
/*     */     } catch (NoSuchAlgorithmException e) {
/* 738 */       LOGGER.error(e.getMessage(), e);
/* 739 */       return true;
/*     */     } catch (MalformedURLException e) {
/* 741 */       LOGGER.error("Bad URL : " + e.getMessage(), e);
/* 742 */       return true;
/*     */     } catch (IOException e) {
/* 744 */       LOGGER.error("Service unavailable : " + e.getMessage(), e);
/* 745 */     }return true;
/*     */   }
/*     */ 
/*     */   public void connect()
/*     */   {
/* 750 */     GreedContext.setConfig("AUTORECONNECT", Boolean.TRUE);
/* 751 */     this.autoReconnect = true;
/*     */   }
/*     */ 
/*     */   private void resetAutoReconnecting() {
/* 755 */     previousAuthServerResponse = "";
/* 756 */     this.retriesCounter = new AtomicInteger(0);
/*     */   }
/*     */ 
/*     */   private String retrieveApiUrl() throws NoSuchAlgorithmException, IOException {
/* 760 */     String login = (String)GreedContext.getConfig("account_name");
/* 761 */     String currentTicket = (String)GreedContext.getConfig("TICKET");
/* 762 */     String instanceId = (String)GreedContext.getConfig("SESSION_ID");
/* 763 */     String password = (String)GreedContext.getConfig(" ");
/*     */ 
/* 765 */     String response = null;
/* 766 */     if (this.storage.isForceReconnectsActive()) {
/* 767 */       if (isPreviusResponseValid())
/* 768 */         response = GreedContext.AUTHORIZATION_CLIENT.getNewTicketAfterReconnect(login, currentTicket, instanceId);
/*     */       else
/* 770 */         response = GreedContext.AUTHORIZATION_CLIENT.getUrlAndTicket(login, password, instanceId);
/*     */     }
/*     */     else {
/* 773 */       response = GreedContext.AUTHORIZATION_CLIENT.getNewTicketAfterReconnect(login, currentTicket, instanceId);
/*     */     }
/*     */ 
/* 776 */     if (response != null) {
/* 777 */       response = response.trim();
/*     */     }
/*     */ 
/* 780 */     return response;
/*     */   }
/*     */ 
/*     */   private boolean isPreviusResponseValid() {
/* 784 */     return (previousAuthServerResponse != null) && (!previousAuthServerResponse.startsWith("-")) && (!"500".equals(previousAuthServerResponse)) && (!previousAuthServerResponse.equals("null")) && (previousAuthServerResponse.contains("@")) && (!"".equals(previousAuthServerResponse.trim()));
/*     */   }
/*     */ 
/*     */   private int getMaxRetriesCount()
/*     */   {
/* 795 */     if (this.storage.isForceReconnectsActive()) {
/* 796 */       return MAX_FORCED_RECONNECT_RETRIES;
/*     */     }
/* 798 */     return MAX_RECONNECT_RETRIES;
/*     */   }
/*     */ 
/*     */   public boolean isAutoReconnect()
/*     */   {
/* 803 */     return this.autoReconnect;
/*     */   }
/*     */ 
/*     */   public void setAutoReconnect(boolean autoReconnect) {
/* 807 */     this.autoReconnect = autoReconnect;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connection.GreedClientListener
 * JD-Core Version:    0.6.0
 */