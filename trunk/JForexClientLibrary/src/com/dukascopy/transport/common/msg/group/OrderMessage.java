/*      */ package com.dukascopy.transport.common.msg.group;
/*      */ 
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderState;
/*      */ import com.dukascopy.transport.common.model.type.RejectReason;
/*      */ import com.dukascopy.transport.common.model.type.StopDirection;
/*      */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*      */ import java.io.Serializable;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.RoundingMode;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Currency;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.TimeZone;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.json.JSONArray;
/*      */ import org.json.JSONObject;
/*      */ 
/*      */ public class OrderMessage extends ProtocolMessage
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 220706151507L;
/*      */   public static final char ORDER_ID_PATH_SEPARATOR = '.';
/*      */   private static final Pattern regexLastIdPart;
/*      */   public static final String TYPE = "order";
/*      */   public static final String ROOT_ORDER_ID = "rootId";
/*      */   public static final String ORDER_ID = "orderId";
/*      */   public static final String INSTRUMENT = "instrument";
/*      */   public static final String AMOUNT = "amount";
/*      */   public static final String SIDE = "side";
/*      */   public static final String PRICE_LIMIT = "priceLimit";
/*      */   public static final String PRICE_TRAILING_LIMIT = "trailingLimit";
/*      */   public static final String PIPS_STOP = "pipsStop";
/*      */   public static final String PRICE_STOP = "priceStop";
/*      */   public static final String PRICE_CLIENT = "priceClient";
/*      */   public static final String PRICE_CLIENT_INITIAL = "priceClientInitial";
/*      */   public static final String STOP_DIRECTION = "stopDir";
/*      */   public static final String ORDER_GROUP_ID = "orderGroupId";
/*      */   public static final String DIRECTION = "dir";
/*      */   public static final String OCO = "oco";
/*      */   public static final String STATE = "state";
/*      */   public static final String PROCESS_TYPE = "otype";
/*      */   public static final String TRADES = "trades";
/*      */   public static final String NOTES = "notes";
/*      */   public static final String REJECT_REASON = "rejectReason";
/*      */   public static final String EXEC_TIMEOUT_MILLIS = "execTimeoutMillis";
/*      */   public static final String HSEX_USER = "hsexUser";
/*      */   public static final String EXECUTOR_BLACKLIST = "execBlacklist";
/*      */   public static final String EXPOSURE_TRANSFER = "expTrans";
/*      */   public static final String ORDER_COMMISSION = "commission";
/*      */   public static final String PLACE_BID_PLACE_OFFER = "pbpo";
/*      */   public static final String PARENT_ORDER_ID = "parentId";
/*      */   public static final String CREATED_DATE = "createdDate";
/*      */   public static final String IS_MC_ORDER = "isMcOrder";
/*      */   public static final String EXECUTION_SEQ_TIME = "execTime";
/*      */   public static final String TRAILING_STOP = "tralingStop";
/*      */   public static final String EXTERNAL_SYS_ID = "extSysId";
/*      */   public static final String SIGNAL_ID = "signalId";
/*      */   public static final String STRATEGY_ID = "strategyId";
/*      */   public static final String SERVICE = "serv";
/*      */   public static final String SERVICE_TO = "serv_to";
/*      */   public static final String IFD_PARENT_ORDER_ID = "ifd_par_id";
/*      */   public static final String FUND_ACCOUNT_ID = "facct_id";
/*      */   public static final String FUND_RATIO = "fundRatio";
/*      */   public static final String IS_ROLLOVER = "isRollover";
/*      */   public static final String SD_EXECUTION_DELAY = "sdDelay";
/*      */   public static final String SD_TRAILING_USD = "sdSleepage";
/*      */   public static final String PRICE_POS_OPEN = "pricePosOpen";
/*      */   public static final String CLIENT_ID = "clientId";
/*      */   public static final String ORIG_ORD_GR_ID = "origOrdGrId";
/*      */   public static final String ORIG_AMOUNT = "origAmount";
/*      */   public static final String SP_WINER = "spWiner";
/*      */   public static final String ZL_PRESENT = "zl_present";
/*      */   public static final String ZL_PREVIOUS = "zl_previous";
/*      */   public static final String ZL_ON_RECEIVE = "zl_onReceive";
/*      */   public static final String DEAL_TYPE = "dealType";
/*      */   public static final String ZL_ALL_PRESENT = "zl_all_present";
/*      */   public static final String ZL_ALL_PREVIOUS = "zl_all_previous";
/*      */   public static final String ZL_ALL_ON_RECEIVE = "zl_all_onReceive";
/*      */   public static final String ZL_CORRECTION = "zl_correction";
/*      */   public static final String SPREAD_EX_CORRECTION = "spread_ex_correction";
/*      */   public static final String NSE_OWN_AMOUNT = "nse_own_amount";
/*      */   public static final String COMMON_ID = "common_id";
/*      */   public static final String IS_BO_CANCELL_REPLACE = "isBOcr";
/*      */   public static final String IS_FIL_OR_KILL = "isFOK";
/*      */   public static final String FEED_COMMISSION = "fcomm";
/*      */   public static final String PRICE_OPEN = "priceOpen";
/*      */   public static final String IMMEDIATE_OR_CANCEL = "ioc";
/*      */   public static final String EXECUTOR_ID = "exec_id";
/*      */   public static final String SESSION_ID = "sessId";
/*      */   public static final String INTERNAL_IP = "intIp";
/*      */   public static final String EXTERNAL_IP = "extIp";
/*      */   public static final String PRIME_BROKER = "pBroker";
/*      */   public static final String WL_EXPOSURE = "wlExp";
/*      */   public static final String WL_MAX_TRADE = "wlMaxTr";
/*      */   public static final String WL_IS_FILTER = "wlFilter";
/*      */   public static final String WL_IS_VIRTUAL = "wlVirt";
/*      */   public static final String IS_REVERSE = "reverse";
/*      */   public static final String WL_REF_ORDER_ID = "rorderId";
/*      */   public static final String ALLOW_SDEX = "allowSdx";
/*      */   public static final String BEST_BID = "bBid";
/*      */   public static final String BEST_ASK = "bAsk";
/*      */   public static final String IFD_TYPE = "ifdt";
/*  173 */   private Money cachedExecutedAmount = null;
/*  174 */   private boolean isDisabled = false;
/*  175 */   private boolean isSelected = false;
/*  176 */   private boolean isInClosingState = false;
/*      */   private static final BigDecimal PIP2;
/*      */   private static final BigDecimal PIP4;
/*      */   private static final BigDecimal DEFAULT_SLIPPAGE;
/*      */   public static final BigDecimal ONE_MILLION;
/*      */   public static final BigDecimal ONE_THUSAND;
/*      */   public static final BigDecimal ONE;
/*      */   public static final String IFD_MASTER = "ifdm";
/*      */   public static final String IFD_SLAVE = "ifds";
/*      */   public static final String WL_PARTNER_ID = "wlPart";
/*      */   public static final String WL_TIMES = "wlTimes";
/*      */   public static final long GTC_THRESHOLD = 63072000000L;
/*      */ 
/*      */   public OrderMessage()
/*      */   {
/*  200 */     setType("order");
/*  201 */     setOrderState(OrderState.CREATED);
/*  202 */     setHsexUser(Boolean.valueOf(false));
/*      */   }
/*      */ 
/*      */   public OrderMessage(String s)
/*      */     throws ParseException
/*      */   {
/*  212 */     super(s);
/*  213 */     setType("order");
/*  214 */     setHsexUser(Boolean.valueOf(false));
/*      */   }
/*      */ 
/*      */   public OrderMessage(JSONObject s)
/*      */     throws ParseException
/*      */   {
/*  224 */     super(s);
/*  225 */     setType("order");
/*  226 */     setHsexUser(Boolean.valueOf(false));
/*      */   }
/*      */ 
/*      */   public OrderMessage(ProtocolMessage message)
/*      */   {
/*  235 */     super(message);
/*  236 */     setType("order");
/*  237 */     put("orderId", message.getString("orderId"));
/*  238 */     put("rootId", message.getString("rootId"));
/*  239 */     put("instrument", message.getString("instrument"));
/*  240 */     put("amount", message.getString("amount"));
/*  241 */     put("side", message.getString("side"));
/*  242 */     put("priceLimit", message.getString("priceLimit"));
/*  243 */     put("trailingLimit", message.getString("trailingLimit"));
/*  244 */     put("priceStop", message.getString("priceStop"));
/*  245 */     put("priceClient", message.getString("priceClient"));
/*  246 */     put("priceClientInitial", message.getString("priceClientInitial"));
/*  247 */     put("orderGroupId", message.getString("orderGroupId"));
/*  248 */     put("dir", message.getString("dir"));
/*  249 */     put("stopDir", message.getString("stopDir"));
/*  250 */     put("oco", message.getString("oco"));
/*  251 */     put("state", message.getString("state"));
/*  252 */     put("trades", message.getJSONArray("trades"));
/*  253 */     put("hsexUser", message.getBool("hsexUser"));
/*  254 */     put("rejectReason", message.getString("rejectReason"));
/*  255 */     put("execBlacklist", message.getJSONArray("execBlacklist"));
/*  256 */     put("expTrans", message.getBool("expTrans"));
/*  257 */     put("otype", message.getBool("otype"));
/*  258 */     put("notes", message.getString("notes"));
/*  259 */     put("execTimeoutMillis", message.getString("execTimeoutMillis"));
/*  260 */     put("pbpo", message.getBool("pbpo"));
/*  261 */     put("parentId", message.getString("parentId"));
/*  262 */     put("createdDate", message.getString("createdDate"));
/*  263 */     put("isMcOrder", message.getBool("isMcOrder"));
/*  264 */     put("execTime", message.getString("execTime"));
/*  265 */     put("tralingStop", message.getString("tralingStop"));
/*  266 */     put("extSysId", message.getString("extSysId"));
/*  267 */     put("strategyId", message.getString("strategyId"));
/*  268 */     put("serv", message.getString("serv"));
/*  269 */     put("serv_to", message.getString("serv_to"));
/*  270 */     put("ifd_par_id", message.getString("ifd_par_id"));
/*  271 */     put("facct_id", message.getString("facct_id"));
/*  272 */     put("fundRatio", message.getString("fundRatio"));
/*  273 */     put("isRollover", message.getString("isRollover"));
/*  274 */     put("sdDelay", message.getString("sdDelay"));
/*  275 */     put("sdSleepage", message.getString("sdSleepage"));
/*  276 */     put("pricePosOpen", message.getString("pricePosOpen"));
/*  277 */     put("clientId", message.getString("clientId"));
/*  278 */     put("origOrdGrId", message.getString("origOrdGrId"));
/*  279 */     put("origAmount", message.getString("origAmount"));
/*  280 */     put("spWiner", message.getString("spWiner"));
/*  281 */     put("zl_present", message.getString("zl_present"));
/*  282 */     put("zl_previous", message.getString("zl_previous"));
/*  283 */     put("zl_onReceive", message.getString("zl_onReceive"));
/*  284 */     put("dealType", message.getString("dealType"));
/*  285 */     put("zl_all_present", message.getString("zl_all_present"));
/*  286 */     put("zl_all_previous", message.getString("zl_all_previous"));
/*  287 */     put("zl_all_onReceive", message.getString("zl_all_onReceive"));
/*  288 */     put("zl_correction", message.getString("zl_correction"));
/*  289 */     put("spread_ex_correction", message.getString("spread_ex_correction"));
/*  290 */     put("nse_own_amount", message.getString("nse_own_amount"));
/*  291 */     put("common_id", message.getString("common_id"));
/*  292 */     put("isBOcr", message.getBool("isBOcr"));
/*  293 */     put("isFOK", message.getBool("isFOK"));
/*  294 */     put("fcomm", message.getString("fcomm"));
/*  295 */     put("priceOpen", message.getString("priceOpen"));
/*  296 */     put("ioc", message.getBool("ioc"));
/*  297 */     put("exec_id", message.getString("exec_id"));
/*  298 */     put("sessId", message.getString("sessId"));
/*  299 */     put("signalId", message.getString("signalId"));
/*  300 */     put("intIp", message.getString("intIp"));
/*  301 */     put("extIp", message.getString("extIp"));
/*  302 */     put("pBroker", message.getJSONArray("pBroker"));
/*  303 */     put("wlExp", message.getString("wlExp"));
/*  304 */     put("wlMaxTr", message.getString("wlMaxTr"));
/*  305 */     put("wlFilter", message.getBool("wlFilter"));
/*  306 */     put("wlVirt", message.getBool("wlVirt"));
/*  307 */     put("reverse", message.getBool("reverse"));
/*  308 */     put("rorderId", message.getString("rorderId"));
/*  309 */     put("allowSdx", message.getBool("allowSdx"));
/*  310 */     put("bBid", message.getString("bBid"));
/*  311 */     put("bAsk", message.getString("bAsk"));
/*  312 */     put("commission", message.getString("commission"));
/*  313 */     put("ifdt", message.getString("ifdt"));
/*  314 */     put("wlPart", message.getString("wlPart"));
/*  315 */     put("wlTimes", message.getString("wlTimes"));
/*      */   }
/*      */ 
/*      */   public String asString()
/*      */   {
/*  324 */     return asString(ONE_MILLION);
/*      */   }
/*      */ 
/*      */   public String asString(BigDecimal lotAmount)
/*      */   {
/*  329 */     String lotAmountString = null;
/*  330 */     if (ONE_MILLION.equals(lotAmount)) lotAmountString = " mil. ";
/*  331 */     if (ONE_THUSAND.equals(lotAmount)) lotAmountString = " thousand ";
/*  332 */     if (ONE.equals(lotAmount)) lotAmountString = " ";
/*      */ 
/*  334 */     StringBuffer result = new StringBuffer();
/*      */     try {
/*  336 */       BigDecimal amountInMillions = BigDecimal.ZERO;
/*  337 */       if (getAmount() != null) {
/*  338 */         amountInMillions = getAmount().getValue().divide(lotAmount, 3, RoundingMode.HALF_UP).stripTrailingZeros();
/*      */       }
/*      */ 
/*  342 */       Money priceStop = getPriceStop();
/*  343 */       Money priceTrailingLimit = getPriceTrailingLimit();
/*  344 */       StopDirection stopDirection = getStopDirection();
/*  345 */       Money price = getPriceClient();
/*  346 */       if (price == null) {
/*  347 */         price = new Money("0", getCurrencySecondary());
/*      */       }
/*      */ 
/*  350 */       if ((getOrderId() != null) && (getOrderId().equalsIgnoreCase(getParentOrderId())))
/*  351 */         result.append("#").append(getOrderId()).append(" ");
/*  352 */       else if (getOrderId() != null) {
/*  353 */         result.append("Parent Order #").append(getParentOrderId()).append(" ");
/*      */       }
/*      */ 
/*  356 */       if (priceStop != null) {
/*  357 */         if (isMIT())
/*  358 */           result.append("MIT ");
/*  359 */         else if (isOpening())
/*  360 */           result.append("ENTRY ");
/*  361 */         else if (isStopLoss())
/*  362 */           result.append("STOP LOSS ");
/*  363 */         else if (isTakeProfit()) {
/*  364 */           result.append("TAKE PROFIT ");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  369 */       if (isPlaceOffer()) {
/*  370 */         result.append("PLACE ").append(OrderSide.BUY.equals(getSide()) ? "BID" : "OFFER").append(" ").append(amountInMillions.toPlainString()).append(lotAmountString).append(getInstrument()).append(" @ ").append(price.getValue().stripTrailingZeros().toPlainString());
/*      */ 
/*  373 */         if (getString("execTimeoutMillis") != null)
/*  374 */           result.append(getTTLAsString());
/*      */       }
/*      */       else {
/*  377 */         result.append(getSide()).append(" ");
/*  378 */         result.append(amountInMillions.toPlainString()).append(lotAmountString);
/*  379 */         result.append(getInstrument());
/*  380 */         if ((priceStop != null) && (priceTrailingLimit != null)) {
/*  381 */           if (!isStopLoss()) {
/*  382 */             result.append(" @ LIMIT ");
/*  383 */             BigDecimal trailingLimitValue = priceTrailingLimit.getValue();
/*  384 */             if (getSide() == OrderSide.BUY)
/*  385 */               result.append(priceStop.getValue().add(trailingLimitValue).stripTrailingZeros().toPlainString());
/*  386 */             else if (getSide() == OrderSide.SELL)
/*  387 */               result.append(priceStop.getValue().subtract(trailingLimitValue).stripTrailingZeros().toPlainString());
/*      */           }
/*      */           else {
/*  390 */             result.append(" ");
/*      */           }
/*      */         } else {
/*  393 */           result.append(" @ MKT");
/*  394 */           if (priceStop == null) {
/*  395 */             BigDecimal trailingLimitValue = priceTrailingLimit != null ? priceTrailingLimit.getValue() : DEFAULT_SLIPPAGE;
/*  396 */             result.append(" MAX SLIPPAGE ").append(trailingLimitValue.stripTrailingZeros().toPlainString());
/*      */           }
/*      */         }
/*      */       }
/*  400 */       if ((priceStop != null) && (stopDirection != null)) {
/*  401 */         switch (1.$SwitchMap$com$dukascopy$transport$common$model$type$StopDirection[stopDirection.ordinal()]) {
/*      */         case 1:
/*  403 */           result.append(" IF ASK  => ");
/*  404 */           break;
/*      */         case 2:
/*  406 */           result.append(" IF ASK <= ");
/*  407 */           break;
/*      */         case 3:
/*  409 */           result.append(" IF BID => ");
/*  410 */           break;
/*      */         case 4:
/*  412 */           result.append(" IF BID <= ");
/*      */         }
/*      */ 
/*  415 */         result.append(priceStop.getValue().stripTrailingZeros().toPlainString());
/*      */       }
/*      */     }
/*      */     catch (Exception ex) {
/*      */     }
/*  420 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public String getTTLAsString()
/*      */   {
/*  433 */     if (getString("execTimeoutMillis") == null) {
/*  434 */       return "";
/*      */     }
/*  436 */     long currentPlatformTime = System.currentTimeMillis();
/*      */ 
/*  440 */     if (getExecTimeoutMillis().longValue() > currentPlatformTime) {
/*  441 */       long deltaMillis = getExecTimeoutMillis().longValue() - currentPlatformTime;
/*  442 */       if (deltaMillis > 63072000000L)
/*      */       {
/*  444 */         return " EXPIRES: GTC";
/*      */       }
/*  446 */       Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  447 */       cal.setTimeInMillis(getExecTimeoutMillis().longValue());
/*      */ 
/*  449 */       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
/*  450 */       simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */ 
/*  452 */       return " EXPIRES: " + simpleDateFormat.format(cal.getTime());
/*      */     }
/*      */ 
/*  455 */     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH' hour 'mm' min 'ss' sec'");
/*  456 */     simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  457 */     return " EXPIRES after: " + simpleDateFormat.format(new Date(getExecTimeoutMillis().longValue()));
/*      */   }
/*      */ 
/*      */   public String getClientId()
/*      */   {
/*  468 */     return getString("clientId");
/*      */   }
/*      */ 
/*      */   public void setClientId(String clientId)
/*      */   {
/*  477 */     put("clientId", clientId);
/*      */   }
/*      */ 
/*      */   public boolean isPlaceOffer()
/*      */   {
/*  486 */     if (getBool("pbpo") == null) {
/*  487 */       return false;
/*      */     }
/*  489 */     return getBool("pbpo").booleanValue();
/*      */   }
/*      */ 
/*      */   public void setPlaceOffer(Boolean placeOffer)
/*      */   {
/*  500 */     put("pbpo", placeOffer);
/*      */   }
/*      */ 
/*      */   public boolean isHsexUser()
/*      */   {
/*  509 */     return getBool("hsexUser").booleanValue();
/*      */   }
/*      */ 
/*      */   public void setHsexUser(Boolean hsex)
/*      */   {
/*  518 */     put("hsexUser", hsex);
/*      */   }
/*      */ 
/*      */   public boolean isMcOrder()
/*      */   {
/*  527 */     return getBoolean("isMcOrder");
/*      */   }
/*      */ 
/*      */   public void setIsMcOrder(Boolean isMcOrder)
/*      */   {
/*  536 */     put("isMcOrder", isMcOrder);
/*      */   }
/*      */ 
/*      */   public String getCurrencyPrimary()
/*      */   {
/*  546 */     return getString("instrument").substring(0, 3);
/*      */   }
/*      */ 
/*      */   public String getCurrencySecondary()
/*      */   {
/*  556 */     return getString("instrument").substring(4);
/*      */   }
/*      */ 
/*      */   public String getOrderId()
/*      */   {
/*  565 */     return getString("orderId");
/*      */   }
/*      */ 
/*      */   public void setOrderId(String orderId)
/*      */   {
/*  574 */     put("orderId", orderId);
/*      */ 
/*  577 */     if (getRootOrderId() == null)
/*  578 */       setRootOrderId(orderId);
/*      */   }
/*      */ 
/*      */   public String getRootOrderId()
/*      */   {
/*  588 */     return getString("rootId");
/*      */   }
/*      */ 
/*      */   public void setRootOrderId(String rootOrderId)
/*      */   {
/*  597 */     put("rootId", rootOrderId);
/*      */   }
/*      */ 
/*      */   public String getInstrument()
/*      */   {
/*  606 */     return getString("instrument");
/*      */   }
/*      */ 
/*      */   public void setInstrument(String instrument)
/*      */   {
/*  615 */     put("instrument", instrument);
/*      */   }
/*      */ 
/*      */   public Money getAmount()
/*      */   {
/*  624 */     String amountString = getString("amount");
/*  625 */     if (amountString != null) {
/*  626 */       return new Money(amountString, getCurrencyPrimary()).multiply(ONE_MILLION);
/*      */     }
/*  628 */     return null;
/*      */   }
/*      */ 
/*      */   public void setAmount(Money amount)
/*      */   {
/*  638 */     put("amount", amount.getValue().divide(ONE_MILLION).toPlainString());
/*      */   }
/*      */ 
/*      */   public Money getOrigAmount()
/*      */   {
/*  647 */     String amountString = getString("origAmount");
/*  648 */     if (amountString != null) {
/*  649 */       return new Money(amountString, getCurrencyPrimary()).multiply(ONE_MILLION);
/*      */     }
/*  651 */     return null;
/*      */   }
/*      */ 
/*      */   public void setOrigAmount(Money amount)
/*      */   {
/*  661 */     put("origAmount", amount.getValue().divide(ONE_MILLION).toPlainString());
/*      */   }
/*      */ 
/*      */   public OrderSide getSide()
/*      */   {
/*  670 */     String sideString = getString("side");
/*  671 */     if (sideString != null) {
/*  672 */       return OrderSide.fromString(sideString);
/*      */     }
/*  674 */     return null;
/*      */   }
/*      */ 
/*      */   public void setSide(OrderSide side)
/*      */   {
/*  684 */     put("side", side);
/*      */   }
/*      */ 
/*      */   public Money getPriceLimit()
/*      */   {
/*  693 */     String priceString = getString("priceLimit");
/*  694 */     if (priceString != null) {
/*  695 */       return new Money(priceString, getCurrencySecondary());
/*      */     }
/*  697 */     return null;
/*      */   }
/*      */ 
/*      */   public String getParentOrderId()
/*      */   {
/*  707 */     return getString("parentId");
/*      */   }
/*      */ 
/*      */   public void setPriceLimit(Money priceLimit)
/*      */   {
/*  716 */     if (priceLimit != null)
/*  717 */       put("priceLimit", priceLimit.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public void setPriceLimit(String priceLimit)
/*      */     throws NumberFormatException
/*      */   {
/*  729 */     new BigDecimal(priceLimit);
/*  730 */     put("priceLimit", priceLimit);
/*      */   }
/*      */ 
/*      */   public Money getPriceTrailingLimit()
/*      */   {
/*  741 */     String priceString = getString("trailingLimit");
/*  742 */     if (priceString != null) {
/*  743 */       return new Money(priceString, getCurrencySecondary());
/*      */     }
/*  745 */     return null;
/*      */   }
/*      */ 
/*      */   public Money getSdTrailingUSD()
/*      */   {
/*  754 */     String priceString = getString("sdSleepage");
/*  755 */     if (priceString != null) {
/*  756 */       return new Money(priceString, "USD");
/*      */     }
/*  758 */     return null;
/*      */   }
/*      */ 
/*      */   public void setPriceTrailingLimit(Money priceLimit)
/*      */   {
/*  770 */     if (priceLimit != null)
/*  771 */       put("trailingLimit", priceLimit.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public void setSdTrailingUSD(Money trailing)
/*      */   {
/*  782 */     if (trailing != null)
/*  783 */       put("sdSleepage", trailing.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public Money getPriceClient()
/*      */   {
/*  794 */     String priceString = getString("priceClient");
/*  795 */     if (priceString != null) {
/*  796 */       return new Money(priceString, getCurrencySecondary());
/*      */     }
/*  798 */     return null;
/*      */   }
/*      */ 
/*      */   public void setPriceClient(Money price)
/*      */   {
/*  809 */     if (price != null)
/*  810 */       put("priceClient", price.getValue().toPlainString());
/*      */     else
/*  812 */       put("priceClient", null);
/*      */   }
/*      */ 
/*      */   public Money getPriceClientInitial()
/*      */   {
/*  823 */     String priceString = getString("priceClientInitial");
/*  824 */     if (priceString != null) {
/*  825 */       return new Money(priceString, getCurrencySecondary());
/*      */     }
/*  827 */     return null;
/*      */   }
/*      */ 
/*      */   public void setPriceClientInitial(Money priceInitial)
/*      */   {
/*  839 */     if (priceInitial != null)
/*  840 */       put("priceClientInitial", priceInitial.getValue().toPlainString());
/*      */     else
/*  842 */       put("priceClientInitial", null);
/*      */   }
/*      */ 
/*      */   public void setPriceTrailingLimit(String priceLimit)
/*      */     throws NumberFormatException
/*      */   {
/*  854 */     new BigDecimal(priceLimit);
/*  855 */     put("trailingLimit", priceLimit);
/*      */   }
/*      */ 
/*      */   public Money getPriceStop()
/*      */   {
/*  864 */     String priceString = getString("priceStop");
/*  865 */     if (priceString != null) {
/*  866 */       return new Money(priceString, getCurrencySecondary());
/*      */     }
/*  868 */     return null;
/*      */   }
/*      */ 
/*      */   public BigDecimal getPipsStop()
/*      */   {
/*  878 */     String pipsString = getString("pipsStop");
/*  879 */     if (pipsString != null) {
/*  880 */       return new BigDecimal(pipsString);
/*      */     }
/*  882 */     return null;
/*      */   }
/*      */ 
/*      */   public void setPriceStop(Money priceStop)
/*      */   {
/*  892 */     if (priceStop != null)
/*  893 */       put("priceStop", priceStop.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public void setPriceStop(String priceStop)
/*      */     throws NumberFormatException
/*      */   {
/*  905 */     new BigDecimal(priceStop);
/*  906 */     put("priceStop", priceStop);
/*      */   }
/*      */ 
/*      */   public void setPipsStop(String pipsStop)
/*      */     throws NumberFormatException
/*      */   {
/*  917 */     new BigDecimal(pipsStop);
/*  918 */     put("pipsStop", pipsStop);
/*      */   }
/*      */ 
/*      */   public String getOrderGroupId()
/*      */   {
/*  928 */     return getString("orderGroupId");
/*      */   }
/*      */ 
/*      */   public void setOrderGroupId(String orderGroupId)
/*      */   {
/*  938 */     put("orderGroupId", orderGroupId);
/*      */   }
/*      */ 
/*      */   public OrderDirection getOrderDirection()
/*      */   {
/*  948 */     String orderDirection = getString("dir");
/*  949 */     if (orderDirection != null) {
/*  950 */       return OrderDirection.fromString(orderDirection);
/*      */     }
/*  952 */     return null;
/*      */   }
/*      */ 
/*      */   public void setExecutorsBlacklist(List<String> blacklist)
/*      */   {
/*  962 */     JSONArray execArray = new JSONArray();
/*  963 */     for (String exec : blacklist) {
/*  964 */       execArray.put(exec);
/*      */     }
/*  966 */     put("execBlacklist", execArray);
/*      */   }
/*      */ 
/*      */   public List<String> getExecutorsBlacklist()
/*      */   {
/*  973 */     List blacklist = new ArrayList();
/*  974 */     JSONArray execArray = getJSONArray("execBlacklist");
/*  975 */     if (execArray != null) {
/*  976 */       for (int i = 0; i < execArray.length(); i++) {
/*  977 */         blacklist.add(execArray.getString(i));
/*      */       }
/*      */     }
/*  980 */     return blacklist;
/*      */   }
/*      */ 
/*      */   public void setExposureTransfer(boolean flag)
/*      */   {
/*  987 */     put("expTrans", flag);
/*      */   }
/*      */ 
/*      */   public boolean isExposureTransferOrder()
/*      */   {
/*  996 */     Boolean isExpTransfer = getBool("expTrans");
/*  997 */     if (isExpTransfer == null) {
/*  998 */       isExpTransfer = Boolean.valueOf(false);
/*      */     }
/* 1000 */     return isExpTransfer.booleanValue();
/*      */   }
/*      */ 
/*      */   public boolean isOpening()
/*      */   {
/* 1009 */     OrderDirection orderDirection = getOrderDirection();
/* 1010 */     return OrderDirection.OPEN == orderDirection;
/*      */   }
/*      */ 
/*      */   public boolean isClosing()
/*      */   {
/* 1019 */     OrderDirection orderDirection = getOrderDirection();
/* 1020 */     return OrderDirection.CLOSE == orderDirection;
/*      */   }
/*      */ 
/*      */   public void setStopDirection(StopDirection stopDirection)
/*      */   {
/* 1030 */     if (stopDirection != null)
/* 1031 */       put("stopDir", stopDirection.asString());
/*      */   }
/*      */ 
/*      */   public StopDirection getStopDirection()
/*      */   {
/* 1042 */     String stopDirection = getString("stopDir");
/* 1043 */     if (stopDirection != null) {
/* 1044 */       return StopDirection.fromString(stopDirection);
/*      */     }
/* 1046 */     return null;
/*      */   }
/*      */ 
/*      */   public void setOrderDirection(OrderDirection orderDirection)
/*      */   {
/* 1057 */     if (orderDirection != null)
/* 1058 */       put("dir", orderDirection.asString());
/*      */   }
/*      */ 
/*      */   public Boolean isOco()
/*      */   {
/* 1069 */     String ret = getString("oco");
/* 1070 */     if (ret == null)
/* 1071 */       return Boolean.valueOf(false);
/* 1072 */     if ("false".equals(ret)) {
/* 1073 */       return Boolean.valueOf(false);
/*      */     }
/* 1075 */     return Boolean.valueOf(true);
/*      */   }
/*      */ 
/*      */   public void setOco(Boolean oco)
/*      */   {
/* 1087 */     put("oco", oco);
/*      */   }
/*      */ 
/*      */   public void setOcoGroup(String ocoOrderGroupId) {
/* 1091 */     put("oco", ocoOrderGroupId);
/*      */   }
/*      */ 
/*      */   public String getOcoGroup() {
/* 1095 */     String ret = getString("oco");
/* 1096 */     if (ret == null) {
/* 1097 */       return null;
/*      */     }
/* 1099 */     return ret;
/*      */   }
/*      */ 
/*      */   public OrderState getOrderState()
/*      */   {
/* 1113 */     String orderState = getString("state");
/* 1114 */     if (orderState != null) {
/* 1115 */       return OrderState.fromString(orderState);
/*      */     }
/* 1117 */     return null;
/*      */   }
/*      */ 
/*      */   public void setOrderState(OrderState orderState)
/*      */   {
/* 1132 */     if (orderState != null)
/* 1133 */       put("state", orderState.asString());
/*      */   }
/*      */ 
/*      */   public List<TradeMessage> getTrades()
/*      */   {
/* 1143 */     List trades = new ArrayList();
/*      */     try {
/* 1145 */       JSONArray tradesArray = getJSONArray("trades");
/* 1146 */       if (tradesArray != null) {
/* 1147 */         for (int i = 0; i < tradesArray.length(); i++)
/* 1148 */           trades.add(new TradeMessage(tradesArray.getJSONObject(i)));
/*      */       }
/*      */     }
/*      */     catch (ParseException e)
/*      */     {
/*      */     }
/* 1154 */     return trades;
/*      */   }
/*      */ 
/*      */   public void addTrade(TradeMessage trade)
/*      */   {
/* 1163 */     JSONArray tradesArray = getJSONArray("trades");
/* 1164 */     if (tradesArray == null) {
/* 1165 */       put("trades", new JSONArray());
/* 1166 */       tradesArray = getJSONArray("trades");
/*      */     }
/* 1168 */     tradesArray.put(trade);
/* 1169 */     this.cachedExecutedAmount = null;
/*      */   }
/*      */ 
/*      */   public void setTrades(List<TradeMessage> trades)
/*      */   {
/* 1178 */     put("trades", new JSONArray());
/* 1179 */     JSONArray tradesArray = getJSONArray("trades");
/* 1180 */     for (TradeMessage trade : trades) {
/* 1181 */       tradesArray.put(trade);
/*      */     }
/* 1183 */     this.cachedExecutedAmount = null;
/*      */   }
/*      */ 
/*      */   public Money getExecutedAmount()
/*      */   {
/* 1192 */     if (this.cachedExecutedAmount == null) {
/* 1193 */       JSONArray tradesArray = getJSONArray("trades");
/* 1194 */       Money result = new Money(BigDecimal.ZERO, Money.getCurrency(getCurrencyPrimary()));
/* 1195 */       if (tradesArray != null) {
/* 1196 */         for (int i = 0; i < tradesArray.length(); i++) {
/*      */           try {
/* 1198 */             TradeMessage trade = new TradeMessage(tradesArray.getJSONObject(i));
/* 1199 */             result = result.add(new Money(trade.getAmountPrimary().getValue().abs(), trade.getAmountPrimary().getCurrency()));
/*      */           }
/*      */           catch (ParseException e)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/* 1206 */       this.cachedExecutedAmount = result;
/*      */     }
/* 1208 */     return this.cachedExecutedAmount;
/*      */   }
/*      */ 
/*      */   public String getNotes()
/*      */   {
/* 1217 */     return getString("notes");
/*      */   }
/*      */ 
/*      */   public void setNotes(String notes)
/*      */   {
/* 1226 */     put("notes", notes);
/*      */   }
/*      */ 
/*      */   public void setParentOrderId(String parentOrderId)
/*      */   {
/* 1235 */     put("parentId", parentOrderId);
/*      */   }
/*      */ 
/*      */   public RejectReason getRejectReason()
/*      */   {
/* 1244 */     String reasonString = getString("rejectReason");
/* 1245 */     if (reasonString != null) {
/* 1246 */       return RejectReason.fromString(reasonString);
/*      */     }
/* 1248 */     return null;
/*      */   }
/*      */ 
/*      */   public void setRejectReason(RejectReason rejectReason)
/*      */   {
/* 1258 */     if (rejectReason != null)
/* 1259 */       put("rejectReason", rejectReason.asString());
/*      */   }
/*      */ 
/*      */   public Long getExecTimeoutMillis()
/*      */   {
/* 1269 */     return getLong("execTimeoutMillis");
/*      */   }
/*      */ 
/*      */   public void setExecTimeoutMillis(Long execTimeoutMillis)
/*      */   {
/* 1278 */     put("execTimeoutMillis", Long.toString(execTimeoutMillis.longValue()));
/*      */   }
/*      */ 
/*      */   public Long getSdExecutionDelay()
/*      */   {
/* 1285 */     return getLong("sdDelay");
/*      */   }
/*      */ 
/*      */   public void setSdExecutionDelay(Long delay)
/*      */   {
/* 1294 */     if (delay == null) {
/* 1295 */       put("sdDelay", null);
/* 1296 */       return;
/*      */     }
/* 1298 */     put("sdDelay", Long.toString(delay.longValue()));
/*      */   }
/*      */ 
/*      */   public Money getExecutionPrice()
/*      */   {
/* 1309 */     Collection trades = getTrades();
/* 1310 */     if (trades.size() == 0) {
/* 1311 */       return null;
/*      */     }
/*      */ 
/* 1314 */     BigDecimal value = BigDecimal.ZERO;
/* 1315 */     BigDecimal amount = BigDecimal.ZERO;
/*      */ 
/* 1317 */     for (TradeMessage trade : trades)
/*      */     {
/* 1319 */       BigDecimal tradeAmount = trade.getAmountPrimary().getValue().abs();
/* 1320 */       BigDecimal tradeValue = tradeAmount.multiply(trade.getPrice().getValue());
/* 1321 */       amount = amount.add(tradeAmount);
/* 1322 */       value = value.add(tradeValue);
/*      */     }
/*      */ 
/* 1325 */     if (amount.compareTo(BigDecimal.ZERO) == 0) {
/* 1326 */       return null;
/*      */     }
/* 1328 */     return new Money(value.divide(amount, RoundingMode.HALF_EVEN).stripTrailingZeros(), Money.getCurrency(getCurrencySecondary()));
/*      */   }
/*      */ 
/*      */   public boolean isStopLoss()
/*      */   {
/* 1340 */     if ((OrderDirection.CLOSE == getOrderDirection()) && (null != getPriceStop())) {
/* 1341 */       if (OrderSide.SELL == getSide()) {
/* 1342 */         return (StopDirection.ASK_LESS == getStopDirection()) || (StopDirection.BID_LESS == getStopDirection());
/*      */       }
/* 1344 */       return (StopDirection.ASK_GREATER == getStopDirection()) || (StopDirection.BID_GREATER == getStopDirection());
/*      */     }
/* 1346 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isTakeProfit()
/*      */   {
/* 1356 */     if ((OrderDirection.CLOSE == getOrderDirection()) && (null != getPriceStop())) {
/* 1357 */       if (OrderSide.BUY == getSide()) {
/* 1358 */         return (StopDirection.ASK_LESS == getStopDirection()) || (StopDirection.BID_LESS == getStopDirection());
/*      */       }
/* 1360 */       return (StopDirection.ASK_GREATER == getStopDirection()) || (StopDirection.BID_GREATER == getStopDirection());
/*      */     }
/* 1362 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isIfdStop()
/*      */   {
/* 1371 */     if ((OrderDirection.OPEN == getOrderDirection()) && (null != getPriceStop()) && ("ifds".equals(getIfdType())))
/*      */     {
/* 1373 */       if (OrderSide.SELL == getSide()) {
/* 1374 */         return (StopDirection.ASK_LESS == getStopDirection()) || (StopDirection.BID_LESS == getStopDirection());
/*      */       }
/* 1376 */       return (StopDirection.ASK_GREATER == getStopDirection()) || (StopDirection.BID_GREATER == getStopDirection());
/*      */     }
/*      */ 
/* 1379 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isIfdLimit()
/*      */   {
/* 1388 */     if ((OrderDirection.OPEN == getOrderDirection()) && (null != getPriceStop()) && ("ifds".equals(getIfdType())))
/*      */     {
/* 1391 */       if (OrderSide.BUY == getSide()) {
/* 1392 */         return StopDirection.ASK_LESS == getStopDirection();
/*      */       }
/* 1394 */       return StopDirection.BID_GREATER == getStopDirection();
/*      */     }
/* 1396 */     return false;
/*      */   }
/*      */ 
/*      */   public Currency getSecondaryCurrency()
/*      */   {
/* 1407 */     if (getInstrument() != null) {
/* 1408 */       String code = getInstrument().substring(4, 7);
/* 1409 */       Currency currency = Currency.getInstance(code);
/* 1410 */       if (currency == null)
/* 1411 */         throw new IllegalStateException("order instrument secondary currency can't be null");
/*      */     }
/*      */     else {
/* 1414 */       throw new IllegalStateException("order instrument can't be null");
/*      */     }
/*      */     Currency currency;
/* 1417 */     return currency;
/*      */   }
/*      */ 
/*      */   public StopDirection convertStopDirection(StopDirection stopDirection)
/*      */   {
/* 1430 */     if (OrderSide.BUY.equals(getSide())) {
/* 1431 */       if (StopDirection.ASK_EQUALS == stopDirection)
/* 1432 */         stopDirection = StopDirection.ASK_LESS;
/* 1433 */       else if (StopDirection.BID_EQUALS == stopDirection) {
/* 1434 */         stopDirection = StopDirection.BID_LESS;
/*      */       }
/*      */     }
/* 1437 */     else if (StopDirection.ASK_EQUALS == stopDirection)
/* 1438 */       stopDirection = StopDirection.ASK_GREATER;
/* 1439 */     else if (StopDirection.BID_EQUALS == stopDirection) {
/* 1440 */       stopDirection = StopDirection.BID_GREATER;
/*      */     }
/*      */ 
/* 1443 */     return stopDirection;
/*      */   }
/*      */ 
/*      */   public void makeLimit(BigDecimal priceStop, StopDirection stopDirection)
/*      */   {
/* 1458 */     if (priceStop == null) {
/* 1459 */       throw new IllegalStateException("price stop cannot be null for Limit orders.");
/*      */     }
/* 1461 */     setPriceStop(new Money(priceStop, getSecondaryCurrency()));
/*      */ 
/* 1464 */     stopDirection = convertStopDirection(stopDirection);
/*      */ 
/* 1466 */     if (OrderSide.BUY == getSide()) {
/* 1467 */       if ((stopDirection == StopDirection.ASK_LESS) || (stopDirection == StopDirection.BID_LESS))
/* 1468 */         setStopDirection(stopDirection);
/*      */       else {
/* 1470 */         throw new IllegalStateException("stopDirection for buy must be ASK LESS or BID LESS.");
/*      */       }
/*      */     }
/* 1473 */     else if ((stopDirection == StopDirection.ASK_GREATER) || (stopDirection == StopDirection.BID_GREATER))
/* 1474 */       setStopDirection(stopDirection);
/*      */     else {
/* 1476 */       throw new IllegalStateException("stopDirection for buy must be ASK GREATER or BID GREATER.");
/*      */     }
/*      */ 
/* 1479 */     setPriceTrailingLimit(new Money(BigDecimal.ZERO, getSecondaryCurrency()));
/*      */   }
/*      */ 
/*      */   public void makeMIT(BigDecimal priceStop, BigDecimal trailingLimit, StopDirection stopDirection)
/*      */   {
/* 1494 */     if (priceStop == null) {
/* 1495 */       throw new IllegalStateException("price stop cannot be null for Limit orders.");
/*      */     }
/* 1497 */     setPriceStop(new Money(priceStop, getSecondaryCurrency()));
/*      */ 
/* 1500 */     stopDirection = convertStopDirection(stopDirection);
/*      */ 
/* 1502 */     if (OrderSide.BUY == getSide()) {
/* 1503 */       if ((stopDirection == StopDirection.ASK_LESS) || (stopDirection == StopDirection.BID_LESS))
/* 1504 */         setStopDirection(stopDirection);
/*      */       else {
/* 1506 */         throw new IllegalStateException("stopDirection for buy must be ASK LESS or BID LESS.");
/*      */       }
/*      */     }
/* 1509 */     else if ((stopDirection == StopDirection.ASK_GREATER) || (stopDirection == StopDirection.BID_GREATER))
/* 1510 */       setStopDirection(stopDirection);
/*      */     else {
/* 1512 */       throw new IllegalStateException("stopDirection for buy must be ASK GREATER or BID GREATER.");
/*      */     }
/*      */ 
/* 1516 */     if ((trailingLimit != null) && (trailingLimit.compareTo(BigDecimal.ZERO) != 0))
/* 1517 */       setPriceTrailingLimit(new Money(trailingLimit, getSecondaryCurrency()));
/*      */     else
/* 1519 */       throw new IllegalStateException("trailingLimit must be defined and can't be 0");
/*      */   }
/*      */ 
/*      */   public boolean isLimit()
/*      */   {
/* 1532 */     if ((null != getPriceStop()) && (getPriceTrailingLimit() != null) && (getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) == 0))
/*      */     {
/* 1535 */       if (OrderSide.BUY == getSide()) {
/* 1536 */         return (StopDirection.ASK_LESS == getStopDirection()) || (StopDirection.BID_LESS == getStopDirection());
/*      */       }
/* 1538 */       return (StopDirection.ASK_GREATER == getStopDirection()) || (StopDirection.BID_GREATER == getStopDirection());
/*      */     }
/* 1540 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isMIT()
/*      */   {
/* 1550 */     if ((null != getPriceStop()) && (getPriceTrailingLimit() != null) && (getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) != 0))
/*      */     {
/* 1553 */       if (OrderSide.BUY == getSide()) {
/* 1554 */         return (StopDirection.ASK_LESS == getStopDirection()) || (StopDirection.BID_LESS == getStopDirection());
/*      */       }
/* 1556 */       return (StopDirection.ASK_GREATER == getStopDirection()) || (StopDirection.BID_GREATER == getStopDirection());
/*      */     }
/* 1558 */     return false;
/*      */   }
/*      */ 
/*      */   public Money getOrderCommission()
/*      */   {
/* 1567 */     String commissionString = getString("commission");
/* 1568 */     if (commissionString != null) {
/*      */       try {
/* 1570 */         return Money.of(commissionString);
/*      */       } catch (IllegalArgumentException e) {
/* 1572 */         return null;
/*      */       }
/*      */     }
/* 1575 */     return null;
/*      */   }
/*      */ 
/*      */   public void setOrderCommission(Money commission)
/*      */   {
/* 1585 */     if (commission != null)
/* 1586 */       put("commission", commission.toString());
/*      */     else
/* 1588 */       remove("commission");
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   private boolean isStopLossTakeProfit(boolean stopLoss)
/*      */   {
/* 1602 */     boolean isStopLoss = false;
/* 1603 */     OrderSide sideCheck = stopLoss ? OrderSide.SELL : OrderSide.BUY;
/* 1604 */     if ((getOrderDirection() == OrderDirection.CLOSE) && (getPriceStop() != null)) {
/* 1605 */       if (getSide() == sideCheck)
/* 1606 */         isStopLoss = (getStopDirection() == StopDirection.ASK_LESS) || (getStopDirection() == StopDirection.BID_LESS);
/*      */       else {
/* 1608 */         isStopLoss = (getStopDirection() == StopDirection.ASK_GREATER) || (getStopDirection() == StopDirection.BID_GREATER);
/*      */       }
/*      */     }
/*      */ 
/* 1612 */     return isStopLoss;
/*      */   }
/*      */ 
/*      */   public boolean isDisabled() {
/* 1616 */     return this.isDisabled;
/*      */   }
/*      */ 
/*      */   public void setDisabled(boolean disabled) {
/* 1620 */     this.isDisabled = disabled;
/*      */   }
/*      */ 
/*      */   public boolean isSelected() {
/* 1624 */     return this.isSelected;
/*      */   }
/*      */ 
/*      */   public void setSelected(boolean selected) {
/* 1628 */     this.isSelected = selected;
/*      */   }
/*      */ 
/*      */   public boolean isInClosingState() {
/* 1632 */     return this.isInClosingState;
/*      */   }
/*      */ 
/*      */   public void setInClosingState(boolean inClosingState) {
/* 1636 */     this.isInClosingState = inClosingState;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object o) {
/* 1640 */     if (o == null)
/* 1641 */       return false;
/* 1642 */     if (getOrderId() == null)
/* 1643 */       return false;
/* 1644 */     return getOrderId().equalsIgnoreCase(((OrderMessage)o).getOrderId());
/*      */   }
/*      */ 
/*      */   public int hashCode() {
/* 1648 */     return getOrderId() != null ? getOrderId().hashCode() : 0;
/*      */   }
/*      */ 
/*      */   private BigDecimal toPips(BigDecimal price, BigDecimal difference)
/*      */   {
/* 1653 */     BigDecimal differenceAbs = difference.abs();
/* 1654 */     BigDecimal pipValue = price.compareTo(new BigDecimal("20.0")) >= 0 ? PIP2 : PIP4;
/* 1655 */     return differenceAbs.divide(pipValue, 1, RoundingMode.HALF_UP).stripTrailingZeros();
/*      */   }
/*      */ 
/*      */   public Date getCreatedDate()
/*      */   {
/* 1664 */     return getDate("createdDate");
/*      */   }
/*      */ 
/*      */   public void setCreatedDate(Date timestamp)
/*      */   {
/* 1673 */     putDate("createdDate", timestamp);
/*      */   }
/*      */ 
/*      */   public void startNewSequence()
/*      */   {
/* 1680 */     assert (getRootOrderId() != null);
/* 1681 */     assert (getOrderId() != null);
/*      */ 
/* 1683 */     String orderId = getOrderId();
/* 1684 */     setParentOrderId(orderId);
/*      */ 
/* 1686 */     String newOrderId = orderId + '.' + 0;
/* 1687 */     setOrderId(newOrderId);
/*      */   }
/*      */ 
/*      */   public String incrementOrderId()
/*      */   {
/* 1697 */     assert (getRootOrderId() != null);
/* 1698 */     assert (getOrderId() != null);
/* 1699 */     assert (getParentOrderId() != null);
/*      */ 
/* 1701 */     String orderId = getOrderId();
/*      */ 
/* 1703 */     Matcher matcher = regexLastIdPart.matcher(orderId);
/* 1704 */     if (!matcher.matches()) {
/* 1705 */       throw new IllegalStateException("Incorrect order id composition.");
/*      */     }
/* 1707 */     int subseqId = Integer.valueOf(matcher.group(2)).intValue();
/* 1708 */     subseqId++;
/*      */ 
/* 1710 */     String newOrderId = matcher.group(1) + '.' + subseqId;
/* 1711 */     setOrderId(newOrderId);
/*      */ 
/* 1713 */     return newOrderId;
/*      */   }
/*      */ 
/*      */   public void addExecutionTime(String cp, long time) {
/* 1717 */     String rez = getString("execTime");
/* 1718 */     if (rez == null)
/* 1719 */       rez = cp + ":" + time;
/*      */     else {
/* 1721 */       rez = rez + "," + cp + ":" + time;
/*      */     }
/* 1723 */     put("execTime", rez);
/*      */   }
/*      */ 
/*      */   public void setExecutingTimes(String times) {
/* 1727 */     put("execTime", times);
/*      */   }
/*      */ 
/*      */   public void addExecutionTime(String cp)
/*      */   {
/* 1732 */     addExecutionTime(cp, System.currentTimeMillis());
/*      */   }
/*      */ 
/*      */   public String getExecutionTime() {
/* 1736 */     return getString("execTime");
/*      */   }
/*      */ 
/*      */   public Money getTrailingStop()
/*      */   {
/* 1746 */     String priceString = getString("tralingStop");
/* 1747 */     if (priceString != null) {
/* 1748 */       return new Money(priceString, getCurrencySecondary());
/*      */     }
/* 1750 */     return null;
/*      */   }
/*      */ 
/*      */   public void setTrailingStop(Money trailingStop)
/*      */   {
/* 1761 */     if (trailingStop != null)
/* 1762 */       put("tralingStop", trailingStop.getValue().toPlainString());
/*      */   }
/*      */ 
/*      */   public String getExternalSysId()
/*      */   {
/* 1773 */     return getString("extSysId");
/*      */   }
/*      */ 
/*      */   public void setExternalSysId(String extId)
/*      */   {
/* 1782 */     put("extSysId", extId);
/*      */   }
/*      */ 
/*      */   public String getStrategyId()
/*      */   {
/* 1791 */     return getString("strategyId");
/*      */   }
/*      */ 
/*      */   public void setStrategySysId(String strategyId)
/*      */   {
/* 1800 */     put("strategyId", strategyId);
/*      */   }
/*      */ 
/*      */   public String getService()
/*      */   {
/* 1809 */     return getString("serv");
/*      */   }
/*      */ 
/*      */   public void setService(String serv)
/*      */   {
/* 1818 */     put("serv", serv);
/*      */   }
/*      */ 
/*      */   public String getServiceTo()
/*      */   {
/* 1827 */     return getString("serv_to");
/*      */   }
/*      */ 
/*      */   public void setServiceTo(String serv)
/*      */   {
/* 1836 */     put("serv_to", serv);
/*      */   }
/*      */ 
/*      */   public String getIfdParentOrderId()
/*      */   {
/* 1846 */     return getString("ifd_par_id");
/*      */   }
/*      */ 
/*      */   public void setIfdParentOrderId(String ifdId)
/*      */   {
/* 1855 */     put("ifd_par_id", ifdId);
/*      */   }
/*      */ 
/*      */   public String getFundAcctId()
/*      */   {
/* 1864 */     return getString("facct_id");
/*      */   }
/*      */ 
/*      */   public void setFundAcctId(String id)
/*      */   {
/* 1873 */     put("facct_id", id);
/*      */   }
/*      */ 
/*      */   public BigDecimal getPricePosOpen()
/*      */   {
/* 1883 */     String val = getString("pricePosOpen");
/* 1884 */     if (val == null) {
/* 1885 */       return null;
/*      */     }
/* 1887 */     return new BigDecimal(val);
/*      */   }
/*      */ 
/*      */   public void setPricePosOpen(BigDecimal price)
/*      */   {
/* 1897 */     put("pricePosOpen", price.toPlainString());
/*      */   }
/*      */ 
/*      */   public Integer getFundRatio()
/*      */   {
/* 1906 */     String fundRatio = getString("fundRatio");
/* 1907 */     if (fundRatio != null) {
/* 1908 */       return new Integer(fundRatio);
/*      */     }
/* 1910 */     return null;
/*      */   }
/*      */ 
/*      */   public void setFundRatio(Integer fundRatio)
/*      */   {
/* 1920 */     if (fundRatio == null)
/* 1921 */       put("fundRatio", null);
/*      */     else
/* 1923 */       put("fundRatio", fundRatio);
/*      */   }
/*      */ 
/*      */   public boolean isRolloverOrder()
/*      */   {
/* 1934 */     return getBoolean("isRollover");
/*      */   }
/*      */ 
/*      */   public void setIsRolloverOrder(Boolean isRolloverOrder)
/*      */   {
/* 1943 */     put("isRollover", isRolloverOrder);
/*      */   }
/*      */ 
/*      */   public String getOrigOrderGroupId()
/*      */   {
/* 1952 */     return getString("origOrdGrId");
/*      */   }
/*      */ 
/*      */   public void setOrigOrderGroupId(String origOrderGroupId)
/*      */   {
/* 1961 */     put("origOrdGrId", origOrderGroupId);
/*      */   }
/*      */ 
/*      */   public BigDecimal getSPWiner()
/*      */   {
/* 1970 */     String ratio = getString("spWiner");
/* 1971 */     if (ratio == null) {
/* 1972 */       return null;
/*      */     }
/* 1974 */     return new BigDecimal(ratio);
/*      */   }
/*      */ 
/*      */   public void setSPWiner(BigDecimal spWiner)
/*      */   {
/* 1984 */     if (spWiner != null)
/* 1985 */       put("spWiner", spWiner.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getZLPresent()
/*      */   {
/* 1995 */     String zlPresent = getString("zl_present");
/* 1996 */     if (zlPresent != null) {
/* 1997 */       return new BigDecimal(zlPresent);
/*      */     }
/* 1999 */     return null;
/*      */   }
/*      */ 
/*      */   public void setZLPresent(BigDecimal zlPresent)
/*      */   {
/* 2009 */     if (zlPresent == null)
/* 2010 */       put("zl_present", null);
/*      */     else
/* 2012 */       put("zl_present", zlPresent.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getZLPrevious()
/*      */   {
/* 2022 */     String zlPrevious = getString("zl_previous");
/* 2023 */     if (zlPrevious != null) {
/* 2024 */       return new BigDecimal(zlPrevious);
/*      */     }
/* 2026 */     return null;
/*      */   }
/*      */ 
/*      */   public void setZLPrevious(BigDecimal zlPrevious)
/*      */   {
/* 2036 */     if (zlPrevious == null)
/* 2037 */       put("zl_previous", null);
/*      */     else
/* 2039 */       put("zl_previous", zlPrevious.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getZLOnReceive()
/*      */   {
/* 2049 */     String zlOnReceive = getString("zl_onReceive");
/* 2050 */     if (zlOnReceive != null) {
/* 2051 */       return new BigDecimal(zlOnReceive);
/*      */     }
/* 2053 */     return null;
/*      */   }
/*      */ 
/*      */   public void setZLOnReceive(BigDecimal zlOnReceive)
/*      */   {
/* 2063 */     if (zlOnReceive == null)
/* 2064 */       put("zl_onReceive", null);
/*      */     else
/* 2066 */       put("zl_onReceive", zlOnReceive.toPlainString());
/*      */   }
/*      */ 
/*      */   public String getDealType()
/*      */   {
/* 2076 */     return getString("dealType");
/*      */   }
/*      */ 
/*      */   public void setDealType(String dealType)
/*      */   {
/* 2085 */     put("dealType", dealType);
/*      */   }
/*      */ 
/*      */   public BigDecimal getZLAllPresent()
/*      */   {
/* 2094 */     String zlAllPresent = getString("zl_all_present");
/* 2095 */     if (zlAllPresent != null) {
/* 2096 */       return new BigDecimal(zlAllPresent);
/*      */     }
/* 2098 */     return null;
/*      */   }
/*      */ 
/*      */   public void setZLAllPresent(BigDecimal zlAllPresent)
/*      */   {
/* 2108 */     if (zlAllPresent == null)
/* 2109 */       put("zl_all_present", null);
/*      */     else
/* 2111 */       put("zl_all_present", zlAllPresent.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getZLAllPrevious()
/*      */   {
/* 2121 */     String zlAllPrevious = getString("zl_all_previous");
/* 2122 */     if (zlAllPrevious != null) {
/* 2123 */       return new BigDecimal(zlAllPrevious);
/*      */     }
/* 2125 */     return null;
/*      */   }
/*      */ 
/*      */   public void setZLAllPrevious(BigDecimal zlAllPrevious)
/*      */   {
/* 2135 */     if (zlAllPrevious == null)
/* 2136 */       put("zl_all_previous", null);
/*      */     else
/* 2138 */       put("zl_all_previous", zlAllPrevious.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getZLAllOnReceive()
/*      */   {
/* 2148 */     String zlAllOnReceive = getString("zl_all_onReceive");
/* 2149 */     if (zlAllOnReceive != null) {
/* 2150 */       return new BigDecimal(zlAllOnReceive);
/*      */     }
/* 2152 */     return null;
/*      */   }
/*      */ 
/*      */   public void setZLAllOnReceive(BigDecimal zlAllOnReceive)
/*      */   {
/* 2162 */     if (zlAllOnReceive == null)
/* 2163 */       put("zl_all_onReceive", null);
/*      */     else
/* 2165 */       put("zl_all_onReceive", zlAllOnReceive.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getZLCorrection()
/*      */   {
/* 2175 */     String ZLCorrection = getString("zl_correction");
/* 2176 */     if (ZLCorrection != null) {
/* 2177 */       return new BigDecimal(ZLCorrection);
/*      */     }
/* 2179 */     return null;
/*      */   }
/*      */ 
/*      */   public void setZLCorrection(BigDecimal zlCorrection)
/*      */   {
/* 2189 */     if (zlCorrection == null)
/* 2190 */       put("zl_correction", null);
/*      */     else
/* 2192 */       put("zl_correction", zlCorrection.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getSpreadExCorrection()
/*      */   {
/* 2202 */     String spreadExCorrection = getString("spread_ex_correction");
/* 2203 */     if (spreadExCorrection != null) {
/* 2204 */       return new BigDecimal(spreadExCorrection);
/*      */     }
/* 2206 */     return null;
/*      */   }
/*      */ 
/*      */   public void setSpreadExCorrection(BigDecimal spreadExCorrection)
/*      */   {
/* 2216 */     if (spreadExCorrection == null)
/* 2217 */       put("spread_ex_correction", null);
/*      */     else
/* 2219 */       put("spread_ex_correction", spreadExCorrection.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getNSEOwnAmount()
/*      */   {
/* 2229 */     String nseOwnAmount = getString("nse_own_amount");
/* 2230 */     if (nseOwnAmount != null) {
/* 2231 */       return new BigDecimal(nseOwnAmount);
/*      */     }
/* 2233 */     return null;
/*      */   }
/*      */ 
/*      */   public void setNSEOwnAmount(BigDecimal nseOwnAmount)
/*      */   {
/* 2243 */     if (nseOwnAmount == null)
/* 2244 */       put("nse_own_amount", null);
/*      */     else
/* 2246 */       put("nse_own_amount", nseOwnAmount.toPlainString());
/*      */   }
/*      */ 
/*      */   public String getCommonId()
/*      */   {
/* 2256 */     return getString("common_id");
/*      */   }
/*      */ 
/*      */   public void setCommonId(String commonId)
/*      */   {
/* 2265 */     if (commonId == null)
/* 2266 */       put("common_id", null);
/*      */     else
/* 2268 */       put("common_id", commonId);
/*      */   }
/*      */ 
/*      */   public Boolean isBidOfferCancellReplace()
/*      */   {
/* 2278 */     return getBool("isBOcr");
/*      */   }
/*      */ 
/*      */   public void setBidOfferCancellReplace(Boolean bocr)
/*      */   {
/* 2287 */     put("isBOcr", bocr);
/*      */   }
/*      */ 
/*      */   public Boolean isFillOrKill()
/*      */   {
/* 2297 */     return getBool("isFOK");
/*      */   }
/*      */ 
/*      */   public void setFillOrKill(Boolean bocr)
/*      */   {
/* 2306 */     put("isFOK", bocr);
/*      */   }
/*      */ 
/*      */   public Boolean isImmediateOrCancel()
/*      */   {
/* 2316 */     return getBool("ioc");
/*      */   }
/*      */ 
/*      */   public void setImmediateOrCancel(Boolean bocr)
/*      */   {
/* 2325 */     put("ioc", bocr);
/*      */   }
/*      */ 
/*      */   public BigDecimal getFeedCommssion()
/*      */   {
/* 2334 */     String fcomm = getString("fcomm");
/* 2335 */     if (fcomm != null) {
/* 2336 */       return new BigDecimal(fcomm);
/*      */     }
/* 2338 */     return null;
/*      */   }
/*      */ 
/*      */   public void setFeedCommission(BigDecimal fcomm)
/*      */   {
/* 2348 */     if (fcomm == null)
/* 2349 */       put("fcomm", null);
/*      */     else
/* 2351 */       put("fcomm", fcomm.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getPriceOpen()
/*      */   {
/* 2361 */     String popen = getString("priceOpen");
/* 2362 */     if (popen != null) {
/* 2363 */       return new BigDecimal(popen);
/*      */     }
/* 2365 */     return null;
/*      */   }
/*      */ 
/*      */   public void setPriceOpen(BigDecimal popen)
/*      */   {
/* 2375 */     if (popen == null)
/* 2376 */       put("priceOpen", null);
/*      */     else
/* 2378 */       put("priceOpen", popen.toPlainString());
/*      */   }
/*      */ 
/*      */   public String getExecutorId()
/*      */   {
/* 2383 */     return getString("exec_id");
/*      */   }
/*      */ 
/*      */   public void setExecutorId(String executorId) {
/* 2387 */     put("exec_id", executorId);
/*      */   }
/*      */ 
/*      */   public String getSessionId()
/*      */   {
/* 2398 */     return getString("sessId");
/*      */   }
/*      */ 
/*      */   public void setSessionId(String sessionId)
/*      */   {
/* 2407 */     put("sessId", sessionId);
/*      */   }
/*      */ 
/*      */   public String getExternalIp()
/*      */   {
/* 2418 */     return getString("extIp");
/*      */   }
/*      */ 
/*      */   public void setExternalIp(String externalIp)
/*      */   {
/* 2427 */     put("extIp", externalIp);
/*      */   }
/*      */ 
/*      */   public String getSignalId()
/*      */   {
/* 2436 */     return getString("signalId");
/*      */   }
/*      */ 
/*      */   public void setSignalId(String extId)
/*      */   {
/* 2445 */     put("signalId", extId);
/*      */   }
/*      */ 
/*      */   public String getInternalIp()
/*      */   {
/* 2454 */     return getString("intIp");
/*      */   }
/*      */ 
/*      */   public void setInternalIp(String internalIp)
/*      */   {
/* 2463 */     put("intIp", internalIp);
/*      */   }
/*      */ 
/*      */   public void setPrimeBroker(List<String> primeBroker)
/*      */   {
/* 2474 */     JSONArray execArray = new JSONArray();
/* 2475 */     for (String exec : primeBroker) {
/* 2476 */       execArray.put(exec);
/*      */     }
/* 2478 */     put("pBroker", execArray);
/*      */   }
/*      */ 
/*      */   public List<String> getPrimeBroker()
/*      */   {
/* 2485 */     List primeBroker = new ArrayList();
/* 2486 */     JSONArray execArray = getJSONArray("pBroker");
/* 2487 */     if (execArray != null) {
/* 2488 */       for (int i = 0; i < execArray.length(); i++) {
/* 2489 */         primeBroker.add(execArray.getString(i));
/*      */       }
/*      */     }
/* 2492 */     return primeBroker;
/*      */   }
/*      */ 
/*      */   public BigDecimal getWlExposure() {
/* 2496 */     String ratio = getString("wlExp");
/* 2497 */     if (ratio == null) {
/* 2498 */       return null;
/*      */     }
/* 2500 */     return new BigDecimal(ratio);
/*      */   }
/*      */ 
/*      */   public void setWlExposure(BigDecimal wlExposure)
/*      */   {
/* 2505 */     if (wlExposure != null)
/* 2506 */       put("wlExp", wlExposure.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getWlMaxTrade()
/*      */   {
/* 2511 */     String ratio = getString("wlMaxTr");
/* 2512 */     if (ratio == null) {
/* 2513 */       return null;
/*      */     }
/* 2515 */     return new BigDecimal(ratio);
/*      */   }
/*      */ 
/*      */   public void setWlMaxTrade(BigDecimal wlMaxTrade)
/*      */   {
/* 2520 */     if (wlMaxTrade != null)
/* 2521 */       put("wlMaxTr", wlMaxTrade.toPlainString());
/*      */   }
/*      */ 
/*      */   public boolean isWlFIlter()
/*      */   {
/* 2526 */     return getBoolean("wlFilter");
/*      */   }
/*      */ 
/*      */   public void setIsWlFIlter(Boolean filter) {
/* 2530 */     put("wlFilter", filter);
/*      */   }
/*      */   public boolean isWlVirtual() {
/* 2533 */     return getBoolean("wlVirt");
/*      */   }
/*      */ 
/*      */   public void setIsWlVirtual(Boolean virtual) {
/* 2537 */     put("wlVirt", virtual);
/*      */   }
/*      */ 
/*      */   public String getWlRefOrderId()
/*      */   {
/* 2546 */     return getString("rorderId");
/*      */   }
/*      */ 
/*      */   public void setWlRefOrderId(String wlRefOrderId)
/*      */   {
/* 2555 */     put("rorderId", wlRefOrderId);
/*      */   }
/*      */ 
/*      */   public boolean isAllowSdex() {
/* 2559 */     String allowSdex = getString("allowSdx");
/* 2560 */     if (allowSdex == null) {
/* 2561 */       return true;
/*      */     }
/* 2563 */     return getBoolean("allowSdx");
/*      */   }
/*      */ 
/*      */   public void setAllowSdexr(Boolean allow)
/*      */   {
/* 2568 */     put("allowSdx", allow);
/*      */   }
/*      */ 
/*      */   public BigDecimal getBestBid()
/*      */   {
/* 2574 */     String bid = getString("bBid");
/* 2575 */     if (bid == null) {
/* 2576 */       return null;
/*      */     }
/* 2578 */     return new BigDecimal(bid);
/*      */   }
/*      */ 
/*      */   public void setBestBid(BigDecimal bid)
/*      */   {
/* 2583 */     if (bid != null)
/* 2584 */       put("bBid", bid.toPlainString());
/*      */   }
/*      */ 
/*      */   public BigDecimal getBestAsk()
/*      */   {
/* 2589 */     String ask = getString("bAsk");
/* 2590 */     if (ask == null) {
/* 2591 */       return null;
/*      */     }
/* 2593 */     return new BigDecimal(ask);
/*      */   }
/*      */ 
/*      */   public void setBestAsk(BigDecimal ask)
/*      */   {
/* 2598 */     if (ask != null)
/* 2599 */       put("bAsk", ask.toPlainString());
/*      */   }
/*      */ 
/*      */   public String getIfdType()
/*      */   {
/* 2610 */     return getString("ifdt");
/*      */   }
/*      */ 
/*      */   public void setIfdType(String ifdType)
/*      */   {
/* 2619 */     put("ifdt", ifdType);
/*      */   }
/*      */ 
/*      */   public String getWlPartnerId()
/*      */   {
/* 2628 */     return getString("wlPart");
/*      */   }
/*      */ 
/*      */   public void setWlPartnerId(String id)
/*      */   {
/* 2637 */     put("wlPart", id);
/*      */   }
/*      */ 
/*      */   public String getWlTimes() {
/* 2641 */     return getString("wlTimes");
/*      */   }
/*      */ 
/*      */   public void setWlTimes(String times) {
/* 2645 */     put("wlTimes", times);
/*      */   }
/*      */   public void setWlTimes(int dayOpen, int timeOpen, int dayClose, int timeClose) {
/* 2648 */     setWlTimes(dayOpen + "_" + timeOpen + "_" + dayClose + "_" + timeClose);
/*      */   }
/*      */ 
/*      */   public boolean isWlActive() {
/* 2652 */     boolean ret = true;
/* 2653 */     if (getWlTimes() != null)
/*      */     {
/* 2655 */       Calendar date = Calendar.getInstance();
/* 2656 */       int day = date.get(7);
/* 2657 */       int hour = date.get(7);
/* 2658 */       String[] sarr = getWlTimes().split("_");
/* 2659 */       ret = (day >= Integer.parseInt(sarr[0])) && (hour >= Integer.parseInt(sarr[1])) && (day <= Integer.parseInt(sarr[2])) && (hour < Integer.parseInt(sarr[3]));
/*      */     }
/* 2661 */     return ret;
/*      */   }
/*      */ 
/*      */   public boolean isReverse() {
/* 2665 */     return getBoolean("reverse");
/*      */   }
/*      */ 
/*      */   public void setReversr(Boolean allow) {
/* 2669 */     put("reverse", allow);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   77 */     regexLastIdPart = Pattern.compile("^(.*)\\.([^\\.]+)$");
/*      */ 
/*  177 */     PIP2 = new BigDecimal("0.01");
/*  178 */     PIP4 = new BigDecimal("0.0001");
/*  179 */     DEFAULT_SLIPPAGE = new BigDecimal("0.0005");
/*      */ 
/*  182 */     ONE_MILLION = new BigDecimal("1000000");
/*  183 */     ONE_THUSAND = new BigDecimal("1000");
/*  184 */     ONE = new BigDecimal("1");
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.group.OrderMessage
 * JD-Core Version:    0.6.0
 */