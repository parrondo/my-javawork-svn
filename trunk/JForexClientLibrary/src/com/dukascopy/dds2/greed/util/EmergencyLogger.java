/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.SendLogAction;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.moverview.MiniPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableQuoterPanel;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
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
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.net.UnknownHostException;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.Timer;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class EmergencyLogger
/*     */ {
/*     */   private static final Logger LOGGER;
/*     */   private final String SERVICE_URL;
/*  67 */   private long timeZoneOffset = 0L;
/*  68 */   private JSONArray jaForImmediateSend = new JSONArray();
/*     */ 
/*  71 */   private Map<Long, JSONObject> jaForDeferedSend = new HashMap();
/*     */ 
/*  73 */   private final String DATUM_TP = "datumType";
/*  74 */   private final String DT_TLOG = "trade";
/*  75 */   private final String DT_CORD = "order_cancel";
/*  76 */   private final String DT_CHNG = "conditions_change";
/*  77 */   private final String DT_MSSG = "message";
/*     */ 
/*  79 */   private final String KEY_INST = "instrument";
/*  80 */   private final String KEY_TYPE = "type";
/*  81 */   private final String KEY_SIDE = "side";
/*  82 */   private final String KEY_AMNT = "amount";
/*  83 */   private final String KEY_PRCE = "priceClient";
/*  84 */   private final String KEY_COND = "conditions";
/*  85 */   private final String KEY_CTRL = "buttonType";
/*  86 */   private final String KEY_ACTN = "actionTime";
/*  87 */   private final String KEY_EXTM = "requestTime";
/*  88 */   private final String KEY_STMP = "timestamp";
/*  89 */   private final String KEY_TIME = "clientTime";
/*  90 */   private final String KEY_ORID = "orderId";
/*  91 */   private final String KEY_OGID = "ogid";
/*  92 */   private final String KEY_COWS = "condWas";
/*  93 */   private final String KEY_COIS = "condIs";
/*  94 */   private final String KEY_MSSG = "message";
/*  95 */   private final String KEY_CLIK = "oneclickmode";
/*  96 */   private final String KEY_PSID = "positionId";
/*     */ 
/*  98 */   private final String COND_TP = "tp: ";
/*  99 */   private final String COND_SL = "sl: ";
/* 100 */   private final String COND_SLIPPAGE = "Slippage: ";
/* 101 */   private final String COND_PART = "Particulary close: ";
/* 102 */   private final String COND_OI = "Open if: ";
/* 103 */   private final String COND_AM = "Amount: ";
/*     */ 
/* 105 */   private final String PLATFORM_TYPE = "&platformType=";
/* 106 */   private final String TMZN = "&tzOffsetSeconds=";
/* 107 */   private final String SPACE = " ";
/* 108 */   private final String NEW_LINE = "\n";
/*     */ 
/* 110 */   private final SimpleDateFormat gmtDF = new SimpleDateFormat("yyyyMMddHHmmss");
/*     */ 
/*     */   public EmergencyLogger(int RATE, String SERVICE_URL) {
/* 113 */     this.SERVICE_URL = SERVICE_URL;
/* 114 */     ISpy spy = new ISpy(null);
/* 115 */     Calendar calendar = Calendar.getInstance();
/* 116 */     this.timeZoneOffset = (-(calendar.get(15) + calendar.get(16)));
/* 117 */     this.gmtDF.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 118 */     Timer timer = new Timer(RATE * 1000, spy);
/* 119 */     timer.setRepeats(true);
/* 120 */     timer.start();
/*     */   }
/*     */ 
/*     */   public void add(Object parent, OrderGroupMessage orderGroup, OrderDirection direction)
/*     */   {
/* 129 */     add(parent, orderGroup, direction, false, 0L);
/*     */   }
/*     */ 
/*     */   public void add(Object parent, OrderGroupMessage orderGroup, OrderDirection direction, boolean defered, long millisId)
/*     */   {
/* 140 */     assert (orderGroup != null);
/*     */ 
/* 142 */     LOGGER.info("open trade : " + orderGroup + " direction = " + direction);
/*     */ 
/* 145 */     String conditions = createConditions(orderGroup, OrderDirection.OPEN == direction);
/*     */ 
/* 147 */     List orders = orderGroup.getOrders();
/*     */ 
/* 150 */     for (OrderMessage co : orders) {
/* 151 */       if ((co != null) && 
/* 152 */         (OrderState.CREATED == co.getOrderState()) && (null != co.getPriceTrailingLimit())) {
/* 153 */         conditions = new StringBuffer(conditions).append("Slippage: ").append(co.getPriceTrailingLimit().getValue().toPlainString()).append("\n").toString();
/*     */ 
/* 158 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 164 */     for (OrderMessage co : orders) {
/* 165 */       if ((OrderDirection.CLOSE == co.getOrderDirection()) && (OrderState.CREATED == co.getOrderState())) {
/* 166 */         BigDecimal closingAmount = co.getAmount().getValue();
/*     */ 
/* 168 */         Position pos = orderGroup.calculatePositionModified();
/* 169 */         if (null == pos) {
/*     */           break;
/*     */         }
/* 172 */         BigDecimal actualAmount = pos.getAmount().getValue();
/* 173 */         if (closingAmount.compareTo(actualAmount) >= 0) break;
/* 174 */         conditions = new StringBuffer(conditions).append("Particulary close: ").append(closingAmount.movePointLeft(6).stripTrailingZeros().toPlainString()).append("/").append(actualAmount.movePointLeft(6).stripTrailingZeros().toPlainString()).toString(); break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 187 */     OrderMessage openingOrder = orderGroup.getOpeningOrder();
/* 188 */     String positionId = orderGroup.getOrderGroupId();
/* 189 */     Money amount = new Money("0", orderGroup.getCurrencyPrimary());
/* 190 */     if (OrderDirection.OPEN == direction)
/* 191 */       amount = openingOrder.getAmount();
/*     */     else {
/* 193 */       for (OrderMessage co : orderGroup.getOrders()) {
/* 194 */         if ((OrderDirection.CLOSE == co.getOrderDirection()) && (OrderState.CREATED == co.getOrderState()))
/*     */         {
/* 196 */           amount = amount.add(co.getAmount());
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 201 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 202 */     boolean oneClick = clientForm.getStatusBar().getAccountStatement().getOneClickCheckbox().isSelected();
/*     */ 
/* 213 */     PositionSide side = OrderMessageUtils.getOgmSide(orderGroup);
/*     */ 
/* 216 */     if (side == null) {
/* 217 */       LOGGER.error("ERROR: side calculation for group failed");
/* 218 */       return;
/*     */     }
/*     */ 
/* 221 */     String instrument = orderGroup.getInstrument();
/* 222 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 223 */     CurrencyOffer closingOffer = marketView.getBestOffer(instrument, PositionSide.LONG.equals(side) ? OfferSide.BID : OfferSide.ASK);
/* 224 */     CurrencyOffer openingOffer = marketView.getBestOffer(instrument, PositionSide.SHORT.equals(side) ? OfferSide.BID : OfferSide.ASK);
/*     */     Money clientPrice;
/*     */     OrderSide orderSide;
/* 227 */     if (OrderDirection.OPEN == direction) {
/* 228 */       Money clientPrice = openingOrder.getPriceClient();
/* 229 */       OrderSide orderSide = PositionSide.SHORT.equals(side) ? OrderSide.SELL : OrderSide.BUY;
/* 230 */       if (null == clientPrice)
/* 231 */         clientPrice = null != openingOffer ? openingOffer.getPrice() : null;
/*     */     }
/*     */     else {
/* 234 */       clientPrice = null != closingOffer ? closingOffer.getPrice() : null;
/* 235 */       orderSide = PositionSide.LONG.equals(side) ? OrderSide.SELL : OrderSide.BUY;
/*     */     }
/*     */ 
/* 238 */     JSONObject json = new JSONObject();
/*     */ 
/* 240 */     json.put("datumType", "trade");
/*     */ 
/* 242 */     json.put("instrument", instrument);
/* 243 */     json.put("type", direction.asString());
/* 244 */     json.put("side", orderSide.asString());
/* 245 */     json.put("amount", getAmount(amount));
/* 246 */     json.put("priceClient", getPriceClient(clientPrice));
/* 247 */     json.put("conditions", conditions);
/*     */ 
/* 250 */     json.put("buttonType", getControlType(GreedContext.getConfig("control"), parent, orderSide));
/* 251 */     long decisionTime = 0L;
/* 252 */     if (GreedContext.getConfig("timein") != null) {
/* 253 */       decisionTime = System.currentTimeMillis() - ((Long)GreedContext.getConfig("timein")).longValue();
/*     */     }
/* 255 */     json.put("actionTime", decisionTime);
/* 256 */     json.put("timestamp", getTimeStamp());
/* 257 */     json.put("clientTime", getClientTime());
/* 258 */     json.put("oneclickmode", oneClick ? "1" : "0");
/* 259 */     json.put("positionId", OrderDirection.CLOSE == direction ? positionId : null);
/*     */ 
/* 261 */     if (!defered) {
/* 262 */       this.jaForImmediateSend.put(json);
/*     */     } else {
/* 264 */       if (LOGGER.isDebugEnabled()) {
/* 265 */         LOGGER.debug("for defered send (start): " + millisId + " : " + json);
/*     */       }
/* 267 */       if (millisId > 0L)
/* 268 */         this.jaForDeferedSend.put(Long.valueOf(millisId), json);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void add(OrderGroupMessage prevOrderGroup, OrderGroupMessage modifiedOrderGroup)
/*     */   {
/* 281 */     String prevConditions = createConditions(prevOrderGroup, false);
/* 282 */     String modifiedConditions = createConditions(modifiedOrderGroup, false);
/*     */ 
/* 284 */     LOGGER.debug("modified ogm, initial: " + prevOrderGroup + " modified: " + modifiedOrderGroup + " initial conditions: " + prevConditions + " modified: " + modifiedConditions);
/*     */ 
/* 286 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 287 */     String instrument = modifiedOrderGroup.getInstrument();
/*     */ 
/* 290 */     CurrencyOffer offer = marketView.getBestOffer(instrument, OfferSide.BID);
/* 291 */     String clientPriceBid = (null != offer) && (null != offer.getAmount()) ? offer.getPrice().getValue().stripTrailingZeros().toPlainString() : "";
/*     */ 
/* 293 */     offer = marketView.getBestOffer(instrument, OfferSide.ASK);
/* 294 */     String clientPriceAsk = (null != offer) && (null != offer.getAmount()) ? offer.getPrice().getValue().stripTrailingZeros().toPlainString() : "";
/*     */ 
/* 298 */     Position pos = modifiedOrderGroup.calculatePositionModified();
/*     */     Money amount;
/*     */     Money amount;
/* 299 */     if (null == pos)
/* 300 */       amount = modifiedOrderGroup.getOpeningOrder().getAmount();
/*     */     else {
/* 302 */       amount = pos.getAmount();
/*     */     }
/*     */ 
/* 305 */     JSONObject json = new JSONObject();
/* 306 */     json.put("datumType", "conditions_change");
/*     */ 
/* 308 */     json.put("ogid", modifiedOrderGroup.getOrderGroupId());
/* 309 */     json.put("instrument", modifiedOrderGroup.getInstrument());
/* 310 */     json.put("amount", getAmount(amount));
/* 311 */     json.put("condWas", prevConditions);
/* 312 */     json.put("condIs", modifiedConditions);
/* 313 */     json.put("timestamp", getTimeStamp());
/* 314 */     json.put("clientTime", getClientTime());
/* 315 */     json.put("priceClient", clientPriceBid + "/" + clientPriceAsk);
/*     */ 
/* 317 */     this.jaForImmediateSend.put(json);
/*     */   }
/*     */ 
/*     */   public void add(String message) {
/* 321 */     add(message, getTimeStamp());
/*     */   }
/*     */ 
/*     */   public void add(String message, String serverTimestamp)
/*     */   {
/* 330 */     JSONObject json = new JSONObject();
/* 331 */     json.put("datumType", "message");
/*     */ 
/* 333 */     json.put("message", message);
/* 334 */     json.put("timestamp", serverTimestamp);
/* 335 */     json.put("clientTime", getClientTime());
/*     */ 
/* 337 */     this.jaForImmediateSend.put(json);
/*     */   }
/*     */ 
/*     */   public void add(OrderMessage cancelledOrder)
/*     */   {
/* 345 */     if (LOGGER.isDebugEnabled()) {
/* 346 */       LOGGER.info("Cancelling order: " + cancelledOrder);
/*     */     }
/*     */ 
/* 349 */     JSONObject json = new JSONObject();
/* 350 */     json.put("datumType", "order_cancel");
/*     */ 
/* 352 */     json.put("orderId", cancelledOrder.getOrderId());
/* 353 */     json.put("instrument", cancelledOrder.getInstrument());
/* 354 */     json.put("type", cancelledOrder.getOrderDirection().asString());
/* 355 */     json.put("side", cancelledOrder.getSide().asString());
/* 356 */     json.put("amount", getAmount(cancelledOrder.getAmount()));
/* 357 */     json.put("timestamp", getTimeStamp());
/* 358 */     json.put("clientTime", getClientTime());
/*     */ 
/* 360 */     this.jaForImmediateSend.put(json);
/*     */   }
/*     */ 
/*     */   private String createConditions(OrderGroupMessage pOrderGroup, boolean filled)
/*     */   {
/* 374 */     String conditions = "";
/*     */ 
/* 379 */     List openIfList = OrderMessageUtils.getOpenIfOrderList(pOrderGroup);
/*     */ 
/* 381 */     OrderMessage openIf = null;
/* 382 */     if (openIfList.size() > 0) {
/* 383 */       openIf = (OrderMessage)openIfList.get(0);
/*     */     }
/*     */ 
/* 388 */     if (null != openIf) {
/* 389 */       boolean QAPassed = (filled) || (openIf.getOrderState() != OrderState.FILLED);
/*     */ 
/* 391 */       if (QAPassed) {
/* 392 */         conditions = new StringBuffer(conditions).append("Open if: ").append(openIf.getStopDirection().asString()).append(" ").append(openIf.getPriceStop().getValue().toPlainString()).append("\n").append("Amount: ").append(getAmount(openIf.getAmount())).append("\n").toString();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 408 */     List orders = pOrderGroup.getOrders();
/* 409 */     for (OrderMessage coTp : orders) {
/* 410 */       if (coTp.isTakeProfit()) {
/* 411 */         boolean QAPassed = (filled) || (coTp.getOrderState() != OrderState.FILLED);
/* 412 */         if (!QAPassed) break;
/* 413 */         conditions = new StringBuffer(conditions).append("tp: ").append(coTp.getStopDirection().asString()).append(" ").append(coTp.getPriceStop().getValue().toPlainString()).append("\n").toString(); break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 425 */     for (OrderMessage coSl : orders) {
/* 426 */       if (coSl.isStopLoss()) {
/* 427 */         boolean QAPassed = (filled) || (coSl.getOrderState() != OrderState.FILLED);
/* 428 */         if (!QAPassed) break;
/* 429 */         conditions = new StringBuffer(conditions).append("sl: ").append(coSl.getStopDirection().asString()).append(" ").append(coSl.getPriceStop().getValue().toPlainString()).append("\n").toString(); break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 439 */     return conditions;
/*     */   }
/*     */ 
/*     */   public void sendLog()
/*     */   {
/* 449 */     int size = this.jaForImmediateSend.length();
/* 450 */     if (0 == size) {
/* 451 */       return;
/*     */     }
/* 453 */     String jameson = "";
/*     */     try {
/* 455 */       jameson = URLEncoder.encode(this.jaForImmediateSend.toString(), "ISO-8859-1");
/*     */     } catch (UnsupportedEncodingException e) {
/* 457 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 459 */     if (LOGGER.isDebugEnabled()) {
/* 460 */       LOGGER.info("jah: " + this.jaForImmediateSend.toString());
/*     */     }
/* 462 */     String login = (String)GreedContext.getConfig("account_name");
/* 463 */     String authorization = GuiUtilsAndConstants.buildAuthorizationRequest(login);
/* 464 */     if (null == authorization) {
/* 465 */       authorization = "";
/*     */     }
/* 467 */     String request = new StringBuffer(this.SERVICE_URL).append("?").append(authorization).append("&tzOffsetSeconds=").append(Long.toString(this.timeZoneOffset / 1000L)).append("&platformType=").append(GreedContext.isStrategyAllowed() ? "jforex" : "java").toString();
/*     */ 
/* 477 */     this.jaForImmediateSend = new JSONArray();
/* 478 */     GreedContext.publishEvent(new SendLogAction(this, request, jameson, true));
/*     */   }
/*     */ 
/*     */   private boolean isNetworkOn(String request)
/*     */   {
/* 487 */     boolean isNetworkOn = false;
/*     */     try {
/* 489 */       URL url = new URL(request);
/* 490 */       String host = url.getHost();
/* 491 */       LOGGER.debug("pinging host: " + host + " ");
/* 492 */       InetAddress hostInetAddress = Inet4Address.getByName(host);
/* 493 */       LOGGER.debug("pinging address: " + hostInetAddress + " ");
/* 494 */       isNetworkOn = hostInetAddress.isReachable(1000);
/*     */     } catch (UnknownHostException e) {
/* 496 */       LOGGER.warn(e.getMessage());
/*     */     } catch (IOException e) {
/* 498 */       LOGGER.warn(e.getMessage());
/*     */     }
/* 500 */     return isNetworkOn;
/*     */   }
/*     */ 
/*     */   private String getClientTime()
/*     */   {
/* 505 */     return Long.toString(System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */   private String getTimeStamp()
/*     */   {
/* 511 */     Date result = null;
/*     */ 
/* 513 */     String ts = GreedContext.getPlatformTimeForLogger();
/* 514 */     if (ts != null)
/*     */       try {
/* 516 */         result = this.gmtDF.parse(ts);
/*     */       }
/*     */       catch (ParseException pe)
/*     */       {
/*     */       }
/*     */       catch (NumberFormatException pe)
/*     */       {
/*     */       }
/* 524 */     if (null == result) {
/* 525 */       return null;
/*     */     }
/*     */ 
/* 528 */     return Long.toString(result.getTime());
/*     */   }
/*     */ 
/*     */   private String getAmount(Money amount) {
/* 532 */     return null == amount ? null : amount.getValue().divide(GuiUtilsAndConstants.ONE_MILLION, 2, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString();
/*     */   }
/*     */ 
/*     */   protected String getPriceClient(Money priceClient) {
/* 536 */     return null == priceClient ? null : priceClient.getValue().stripTrailingZeros().toPlainString();
/*     */   }
/*     */ 
/*     */   protected String getControlType(Object originator, Object parent, OrderSide side) {
/* 540 */     if ((originator instanceof JLocalizableQuoterPanel)) {
/* 541 */       if ((parent instanceof MiniPanel))
/* 542 */         return OrderSide.BUY == side ? "spot_ask_button" : "spot_bid_button";
/* 543 */       if (((parent instanceof OrderEntryPanel)) && ((((Component)parent).getParent() instanceof DealPanel))) {
/* 544 */         return OrderSide.BUY == side ? "npf_big_ask_button" : "npf_big_bid_button";
/*     */       }
/* 546 */       return OrderSide.BUY == side ? "detached_npf_big_ask_button" : "detached_npf_big_bid_button";
/*     */     }
/* 548 */     if ((originator instanceof JButton)) {
/* 549 */       if (((parent instanceof OrderEntryPanel)) && ((((Component)parent).getParent() instanceof DealPanel))) {
/* 550 */         return "npf_submit_button";
/*     */       }
/* 552 */       return "detached_npf_submit_button";
/*     */     }
/* 554 */     if ((originator instanceof JMenuItem))
/* 555 */       return (parent instanceof PositionsPanel) ? "positions_cmenu" : "exposure_cmenu";
/* 556 */     return "n/a";
/*     */   }
/*     */ 
/*     */   public void sendDefered(long start, long result)
/*     */   {
/* 566 */     LOGGER.debug("send defered ... " + result);
/* 567 */     JSONObject logObjToSend = (JSONObject)this.jaForDeferedSend.get(Long.valueOf(start));
/* 568 */     if (logObjToSend != null) {
/* 569 */       logObjToSend.put("requestTime", String.valueOf(result));
/* 570 */       if (result > 10000L) {
/* 571 */         LOGGER.debug("Too long waited for result: " + result + " ms " + logObjToSend);
/*     */       }
/* 573 */       LOGGER.debug("send defered with exectionTime: " + logObjToSend);
/* 574 */       this.jaForImmediateSend.put(logObjToSend);
/*     */ 
/* 576 */       this.jaForDeferedSend.remove(Long.valueOf(start));
/* 577 */       LOGGER.debug("defered waiting : " + this.jaForDeferedSend.entrySet());
/*     */     } else {
/* 579 */       LOGGER.debug("send defered failed .. ");
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  63 */     LOGGER = LoggerFactory.getLogger(EmergencyLogger.class);
/*     */   }
/*     */ 
/*     */   private class ISpy
/*     */     implements ActionListener
/*     */   {
/*     */     private ISpy()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 444 */       EmergencyLogger.this.sendLog();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.EmergencyLogger
 * JD-Core Version:    0.6.0
 */