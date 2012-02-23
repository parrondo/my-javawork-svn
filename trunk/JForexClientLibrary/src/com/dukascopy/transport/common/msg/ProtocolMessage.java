/*      */ package com.dukascopy.transport.common.msg;
/*      */ 
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.msg.api.request.ChangeOrderRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.api.request.ChangePositionRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.api.request.ClosePositionRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.api.request.PlaceOrderRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.api.request.PositionHistoryRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.api.request.StatRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.AccountUpdatedResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.ManyUsersWarningMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.MarketStateResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.MarketUpdateResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.OrderCancelledResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.OrderExecutedResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.OrderUpdatedResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.PlaceOrderOkResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.PositionClosedResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.PositionHistoryResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.PositionUpdatedResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.api.response.StatResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.blp.BloombergCandleSubscribeRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.blp.BloombergSwapSubscribeRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.blp.BloombergSwapUnsubscribeRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.candle.CandleGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.candle.CandleMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.CandleHistoryGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.CandleSubscribeRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.DFCandleGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.DFCandleMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.DFHistoryStartRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.DFHistoryStartResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.DFTickMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.DFWeekendsRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.DFWeekendsResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.OrderGroupsBinaryMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.OrderHistoryRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.esignal.ESignalTickMessage;
/*      */ import com.dukascopy.transport.common.msg.executor.ExecutionResultMessage;
/*      */ import com.dukascopy.transport.common.msg.executor.ExecutorInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.executor.ExecutorOrderMessage;
/*      */ import com.dukascopy.transport.common.msg.executor.ExecutorTradeMessage;
/*      */ import com.dukascopy.transport.common.msg.executor.OrderCancelMessage;
/*      */ import com.dukascopy.transport.common.msg.exposure.AccountExposureMessage;
/*      */ import com.dukascopy.transport.common.msg.exposure.AccountInstrumentExposureMessage;
/*      */ import com.dukascopy.transport.common.msg.exposure.ExposureReservationMessage;
/*      */ import com.dukascopy.transport.common.msg.group.MarketNewsMessageGroup;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderSyncMessage;
/*      */ import com.dukascopy.transport.common.msg.group.TradeMessage;
/*      */ import com.dukascopy.transport.common.msg.monitor.FeedHistoryRequest;
/*      */ import com.dukascopy.transport.common.msg.monitor.TaskProgressMessage;
/*      */ import com.dukascopy.transport.common.msg.news.NewsContentRequest;
/*      */ import com.dukascopy.transport.common.msg.news.NewsStoryMessage;
/*      */ import com.dukascopy.transport.common.msg.news.NewsSubscribeRequest;
/*      */ import com.dukascopy.transport.common.msg.properties.UserPropertiesChangeMessage;
/*      */ import com.dukascopy.transport.common.msg.properties.UserPropertiesRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.properties.UserPropertiesResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.quote.QuoteDealMessage;
/*      */ import com.dukascopy.transport.common.msg.quote.QuoteGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.quote.QuoteMessage;
/*      */ import com.dukascopy.transport.common.msg.quote.QuoteRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.request.AccountInfoRequest;
/*      */ import com.dukascopy.transport.common.msg.request.ActivityMessage;
/*      */ import com.dukascopy.transport.common.msg.request.AdminMessage;
/*      */ import com.dukascopy.transport.common.msg.request.AuthentificationRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.BestCurrencyMarketMessage;
/*      */ import com.dukascopy.transport.common.msg.request.CancelOrderRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.ClientInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.request.ConnectionCloseNotification;
/*      */ import com.dukascopy.transport.common.msg.request.ControlCommandRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyQuoteMessage;
/*      */ import com.dukascopy.transport.common.msg.request.CurvesWorkspaceRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.CustodianOnlineMessage;
/*      */ import com.dukascopy.transport.common.msg.request.DbWriteMessage;
/*      */ import com.dukascopy.transport.common.msg.request.DeliveryReportRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.DynaSqlMessage;
/*      */ import com.dukascopy.transport.common.msg.request.ExecutorAccountInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.request.ExecutorAccountsMessage;
/*      */ import com.dukascopy.transport.common.msg.request.ExposureTransferRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.FundRatioChangeMessage;
/*      */ import com.dukascopy.transport.common.msg.request.HaloRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.HeartbeatRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.IdRezMessage;
/*      */ import com.dukascopy.transport.common.msg.request.InitRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.InstrumentInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.request.LoginRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.MarketStateRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*      */ import com.dukascopy.transport.common.msg.request.MoneyMessage;
/*      */ import com.dukascopy.transport.common.msg.request.OrderExecutionFeedbackRequest;
/*      */ import com.dukascopy.transport.common.msg.request.OrderRequest;
/*      */ import com.dukascopy.transport.common.msg.request.PrimeBrokerExposureRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.QuitRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.QuoteSubscribeRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.QuoteUnsubscribeRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.ReportInfo;
/*      */ import com.dukascopy.transport.common.msg.request.ReportParameter;
/*      */ import com.dukascopy.transport.common.msg.request.RouterStatisticsMessage;
/*      */ import com.dukascopy.transport.common.msg.request.SessionListRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.SetFeedbackFrequencyMessage;
/*      */ import com.dukascopy.transport.common.msg.request.SetMarketDepthMessage;
/*      */ import com.dukascopy.transport.common.msg.request.TradableOfferSubscribeRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.TradeOffer;
/*      */ import com.dukascopy.transport.common.msg.request.UserControlMessage;
/*      */ import com.dukascopy.transport.common.msg.request.VersionInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.response.ApiOnlineMessage;
/*      */ import com.dukascopy.transport.common.msg.response.ApiSessionReportMessage;
/*      */ import com.dukascopy.transport.common.msg.response.AuthentificationResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.CurvesWorkspaceResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.DeliveryReportResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.ExecutorOnlineMessage;
/*      */ import com.dukascopy.transport.common.msg.response.ExecutorStateResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.FundAccountTransferMessage;
/*      */ import com.dukascopy.transport.common.msg.response.HaloResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.HeartbeatOkResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.InstrumentExposure;
/*      */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*      */ import com.dukascopy.transport.common.msg.response.LoginResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*      */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.OrderResponse;
/*      */ import com.dukascopy.transport.common.msg.response.PersonalNotificationMessage;
/*      */ import com.dukascopy.transport.common.msg.response.PrimeBrokerExposureResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.QuitResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.RequestInProcessMessage;
/*      */ import com.dukascopy.transport.common.msg.response.ServiceStateResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.TSSMessage;
/*      */ import com.dukascopy.transport.common.msg.response.TimeSyncResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.signals.SignalMessage;
/*      */ import com.dukascopy.transport.common.msg.state.StateMessage;
/*      */ import com.dukascopy.transport.common.msg.state.executor.ExecutorPropertyStateMessage;
/*      */ import com.dukascopy.transport.common.msg.state.session.SessionPropertyStateMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*      */ import com.dukascopy.transport.common.msg.strategy.FileMngRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.FileMngResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategiesListRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategiesListResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategyBroadcastMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategyRunChunkRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategyRunErrorResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategyRunRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategyRunResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategyStateMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategyStopRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategyUpdateRequestMessage;
/*      */ import java.io.PrintStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.text.ParseException;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import org.json.JSONArray;
/*      */ import org.json.JSONObject;
/*      */ 
/*      */ public class ProtocolMessage extends JSONObject
/*      */   implements Cloneable
/*      */ {
/*      */   public static final String PREFIX = "<script>parent.push(";
/*      */   public static final String POSTFIX = ")</script>";
/*      */   public static final String TIMESTAMP = "timestamp";
/*      */   public static final String CHECK_TIME = "check_time";
/*      */   public static final String TIMESYNC_MS = "timeSyncMs";
/*  179 */   private Map<String, Object> properties = new HashMap();
/*      */   public static final long PRIORITY_HIGH = 1000000L;
/*      */   public static final long PRIORITY_DEFAULT = 0L;
/*      */   public static final long PRIORITY_LOW = -1000000L;
/*  187 */   private long priority = 0L;
/*      */ 
/*  189 */   public static final BigDecimal ONE_MILLION = new BigDecimal("1000000.0");
/*      */ 
/*  191 */   private static long sequence = 0L;
/*      */   public static final String SEQUENCE = "seq";
/*      */   public static final String IS_BACKUP = "backup";
/*      */   public static final String TRANSACTION_ID = "transId";
/*      */   public static final String DELTA_MSG = "dmsg";
/*      */   public static final String PLATFORM = "platf";
/*      */   public static final String PLATFORM_JAVA = "JAVA";
/*      */   public static final String PLATFORM_WEB = "WEB";
/*      */   public static final String PLATFORM_FIX = "FIX";
/*      */   public static final String PLATFORM_JFOREX = "JFOREX";
/*      */   public static final String MANAGER_ID = "mgr_id";
/*      */   public static final String ACCOUNT_LOGIN_ID = "loginId";
/*      */   public static final String IS_INIT = "minit";
/*      */ 
/*      */   public ProtocolMessage()
/*      */   {
/*  218 */     setProperty("seq", Long.valueOf(sequence++));
/*      */   }
/*      */ 
/*      */   public ProtocolMessage(ProtocolMessage message)
/*      */   {
/*  228 */     setType(message.getType());
/*  229 */     setTag(message.getTag());
/*  230 */     setUserId(message.getString("userId"));
/*  231 */     put("timestamp", message.getString("timestamp"));
/*  232 */     put("check_time", message.getString("check_time"));
/*  233 */     put("timeSyncMs", message.getString("timeSyncMs"));
/*  234 */     put("transId", message.getString("transId"));
/*  235 */     put("platf", message.getString("platf"));
/*  236 */     put("mgr_id", message.getString("mgr_id"));
/*  237 */     put("dmsg", message.getJSONObject("dmsg"));
/*  238 */     put("loginId", message.getString("loginId"));
/*      */ 
/*  240 */     put("minit", message.getBool("minit"));
/*      */ 
/*  242 */     put("reqid", message.getString("reqid"));
/*  243 */     for (String key : message.getProperties().keySet()) {
/*  244 */       setProperty(key, message.getProperties().get(key));
/*      */     }
/*      */ 
/*  247 */     setProperty("seq", Long.valueOf(sequence++));
/*      */   }
/*      */ 
/*      */   public ProtocolMessage(String s)
/*      */     throws ParseException
/*      */   {
/*  258 */     super((s.startsWith("<script>parent.push(")) && (s.endsWith(")</script>")) ? s.substring("<script>parent.push(".length(), s.length() - ")</script>".length()) : s);
/*  259 */     setProperty("seq", Long.valueOf(sequence++));
/*      */   }
/*      */ 
/*      */   public ProtocolMessage(JSONObject jsonObj)
/*      */     throws ParseException
/*      */   {
/*  270 */     super(jsonObj.getMap());
/*  271 */     setProperty("seq", Long.valueOf(sequence++));
/*      */   }
/*      */ 
/*      */   public void setTag(String tag) {
/*  275 */     put("tag", tag);
/*      */   }
/*      */ 
/*      */   public String getTag() {
/*  279 */     return getString("tag");
/*      */   }
/*      */ 
/*      */   public Long getSynchRequestId() {
/*  283 */     if (getString("reqid") != null) {
/*  284 */       return Long.valueOf(getString("reqid"));
/*      */     }
/*  286 */     return null;
/*      */   }
/*      */ 
/*      */   public void setType(String type)
/*      */   {
/*  291 */     put("type", type);
/*      */   }
/*      */ 
/*      */   public String getType() {
/*  295 */     return getString("type");
/*      */   }
/*      */ 
/*      */   public void setUserId(String userId) {
/*  299 */     put("userId", userId);
/*      */   }
/*      */ 
/*      */   public String getUserId() {
/*  303 */     return getString("userId");
/*      */   }
/*      */ 
/*      */   public String getAcountLoginId()
/*      */   {
/*  312 */     return getString("loginId");
/*      */   }
/*      */ 
/*      */   public void setAcountLoginId(String loginId)
/*      */   {
/*  322 */     put("loginId", loginId);
/*      */   }
/*      */ 
/*      */   public Date getTimestamp()
/*      */   {
/*  331 */     return getDate("timestamp");
/*      */   }
/*      */ 
/*      */   public void setTimestamp(Date timestamp)
/*      */   {
/*  341 */     putDate("timestamp", timestamp);
/*      */   }
/*      */ 
/*      */   public String getTransactionId()
/*      */   {
/*  350 */     return getString("transId");
/*      */   }
/*      */ 
/*      */   public void setTransactionId(String transactionId)
/*      */   {
/*  360 */     put("transId", transactionId);
/*      */   }
/*      */ 
/*      */   public void setPlatform(String platf) {
/*  364 */     put("platf", platf);
/*      */   }
/*      */ 
/*      */   public String getPlatform() {
/*  368 */     return getString("platf");
/*      */   }
/*      */ 
/*      */   public DeltaMessage getDelta() {
/*  372 */     if (has("dmsg")) {
/*  373 */       JSONObject delta = getJSONObject("dmsg");
/*      */       try {
/*  375 */         return new DeltaMessage(delta);
/*      */       } catch (ParseException e) {
/*  377 */         e.printStackTrace();
/*      */       }
/*      */     }
/*  380 */     return null;
/*      */   }
/*      */ 
/*      */   public void setDelta(DeltaMessage delta) {
/*  384 */     put("dmsg", delta);
/*      */   }
/*      */ 
/*      */   public void addDelta(String key, String val) {
/*  388 */     DeltaMessage d = getDelta();
/*  389 */     if (d != null) {
/*  390 */       d.put(key, val);
/*  391 */       setDelta(d);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCheckTime(long time)
/*      */   {
/*  401 */     put("check_time", "" + time);
/*      */   }
/*      */ 
/*      */   public long getCheckTime() {
/*  405 */     return getLong("check_time").longValue();
/*      */   }
/*      */ 
/*      */   public void setTimeSyncMs(Long timeSyncMs)
/*      */   {
/*  415 */     put("timeSyncMs", timeSyncMs.toString());
/*      */   }
/*      */ 
/*      */   public void setIsBackup(boolean isGlobal)
/*      */   {
/*  425 */     put("backup", isGlobal);
/*      */   }
/*      */ 
/*      */   public boolean isBackup()
/*      */   {
/*  434 */     return getBool("backup").booleanValue();
/*      */   }
/*      */ 
/*      */   public void setIsInitState(boolean isInit)
/*      */   {
/*  443 */     put("minit", isInit);
/*      */   }
/*      */ 
/*      */   public boolean isInitState()
/*      */   {
/*  452 */     return getBool("minit").booleanValue();
/*      */   }
/*      */ 
/*      */   public Long getTimeSyncMs()
/*      */   {
/*  462 */     return getLong("timeSyncMs");
/*      */   }
/*      */ 
/*      */   public String getManagerId()
/*      */   {
/*  471 */     return getString("mgr_id");
/*      */   }
/*      */ 
/*      */   public void setManagerId(String managerId)
/*      */   {
/*  481 */     put("mgr_id", managerId);
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(String name)
/*      */   {
/*  492 */     BigDecimal result = null;
/*      */ 
/*  494 */     String s = getString(name);
/*  495 */     if (s != null) {
/*      */       try {
/*  497 */         result = new BigDecimal(s);
/*      */       }
/*      */       catch (NumberFormatException nfe)
/*      */       {
/*      */       }
/*      */     }
/*  503 */     return result;
/*      */   }
/*      */ 
/*      */   public Integer getInteger(String name)
/*      */   {
/*  514 */     Integer result = null;
/*      */ 
/*  516 */     String s = getString(name);
/*  517 */     if (s != null) {
/*      */       try {
/*  519 */         result = Integer.valueOf(Integer.parseInt(s));
/*      */       }
/*      */       catch (NumberFormatException nfe)
/*      */       {
/*      */       }
/*      */     }
/*  525 */     return result;
/*      */   }
/*      */ 
/*      */   public Long getLong(String name)
/*      */   {
/*  536 */     Long result = null;
/*      */ 
/*  538 */     String s = getString(name);
/*  539 */     if (s != null) {
/*      */       try {
/*  541 */         result = Long.valueOf(Long.parseLong(s));
/*      */       }
/*      */       catch (NumberFormatException nfe)
/*      */       {
/*      */       }
/*      */     }
/*  547 */     return result;
/*      */   }
/*      */ 
/*      */   public Boolean getBool(String name)
/*      */   {
/*  558 */     return Boolean.valueOf(super.optBoolean(name));
/*      */   }
/*      */ 
/*      */   public String getString(String name)
/*      */   {
/*  569 */     String result = null;
/*  570 */     result = super.getString(name);
/*  571 */     return result;
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(String key) throws ClassCastException, NoSuchElementException
/*      */   {
/*  576 */     boolean result = false;
/*      */     try {
/*  578 */       result = super.getBoolean(key);
/*      */     }
/*      */     catch (NoSuchElementException nsee) {
/*      */     }
/*  582 */     return result;
/*      */   }
/*      */ 
/*      */   public JSONArray getJSONArray(String name)
/*      */   {
/*  594 */     JSONArray result = null;
/*      */     try {
/*  596 */       result = super.getJSONArray(name);
/*      */     }
/*      */     catch (NoSuchElementException nsee) {
/*      */     }
/*  600 */     return result;
/*      */   }
/*      */ 
/*      */   public void putDate(String name, Date date)
/*      */   {
/*  612 */     if (date == null) {
/*  613 */       return;
/*      */     }
/*  615 */     put(name, String.valueOf(date.getTime()));
/*      */   }
/*      */ 
/*      */   public Date getDate(String name)
/*      */   {
/*  626 */     String val = getString(name);
/*  627 */     if (val != null)
/*      */       try {
/*  629 */         return new Date(Long.parseLong(val));
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */       }
/*  634 */     return null;
/*      */   }
/*      */ 
/*      */   public void setMoney(String name, Money money)
/*      */   {
/*  644 */     put(name, money.toString());
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMoney(Money money, String name)
/*      */   {
/*  655 */     put(name, money.toString());
/*      */   }
/*      */ 
/*      */   public Money getMoney(String name)
/*      */   {
/*  665 */     String s = getString(name);
/*  666 */     if (s != null) {
/*      */       Money result;
/*      */       try { result = Money.of(s);
/*      */       } catch (IllegalArgumentException e) {
/*  671 */         return null;
/*      */       }
/*  673 */       return result;
/*      */     }
/*  675 */     return null;
/*      */   }
/*      */ 
/*      */   public String toProtocolString()
/*      */   {
/*  680 */     StringBuffer sb = new StringBuffer("<script>parent.push(");
/*  681 */     sb.append(super.toString()).append(")</script>");
/*  682 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   public static ProtocolMessage parse(String string)
/*      */   {
/*  693 */     string = jsonWorkaround(string);
/*      */ 
/*  695 */     ProtocolMessage msg = null;
/*      */     try {
/*  697 */       msg = new ProtocolMessage(string);
/*      */     } catch (ParseException pe) {
/*  699 */       System.out.println("ParseException: " + pe);
/*  700 */       pe.printStackTrace(System.out);
/*      */     } catch (NullPointerException npe) {
/*  702 */       System.out.println("NullPointerException: " + npe);
/*  703 */       npe.printStackTrace(System.out);
/*      */     }
/*      */ 
/*  706 */     if (msg == null) {
/*  707 */       return null;
/*      */     }
/*      */ 
/*  710 */     ProtocolMessage result = null;
/*      */ 
/*  712 */     String type = msg.getType();
/*      */ 
/*  714 */     if (type == null) {
/*  715 */       return null;
/*      */     }
/*      */ 
/*  718 */     if (type.equals("hb"))
/*  719 */       result = new HeartbeatRequestMessage(msg);
/*  720 */     else if (type.equals("market_state"))
/*  721 */       result = new MarketStateRequestMessage(msg);
/*  722 */     else if (type.equals("error"))
/*  723 */       result = new ErrorResponseMessage(msg);
/*  724 */     else if (type.equals("hb_ok"))
/*  725 */       result = new HeartbeatOkResponseMessage(msg);
/*  726 */     else if (type.equals("ok"))
/*  727 */       result = new OkResponseMessage(msg);
/*  728 */     else if (type.equals("halo"))
/*  729 */       result = new HaloRequestMessage(msg);
/*  730 */     else if (type.equals("challenge"))
/*  731 */       result = new HaloResponseMessage(msg);
/*  732 */     else if (type.equals("login"))
/*  733 */       result = new LoginRequestMessage(msg);
/*  734 */     else if (type.equals("login_resp"))
/*  735 */       result = new LoginResponseMessage(msg);
/*  736 */     else if (type.equals("cancel_order"))
/*  737 */       result = new CancelOrderRequestMessage(msg);
/*  738 */     else if (type.equals("quote_subsc"))
/*  739 */       result = new QuoteSubscribeRequestMessage(msg);
/*  740 */     else if (type.equals("quote_unsubsc"))
/*  741 */       result = new QuoteUnsubscribeRequestMessage(msg);
/*  742 */     else if (type.equals("init"))
/*  743 */       result = new InitRequestMessage(msg);
/*  744 */     else if (type.equals("cm"))
/*  745 */       result = new CurrencyMarket(msg);
/*  746 */     else if (type.equals("marketState"))
/*  747 */       result = new CurrencyMarket(msg, true);
/*  748 */     else if (type.equals("order"))
/*  749 */       result = new OrderMessage(msg);
/*  750 */     else if (type.equals("orderGroup"))
/*  751 */       result = new OrderGroupMessage(msg);
/*  752 */     else if (type.equals("notification"))
/*  753 */       result = new NotificationMessage(msg);
/*  754 */     else if (type.equals("personal_notification"))
/*  755 */       result = new PersonalNotificationMessage(msg);
/*  756 */     else if (type.equals("instrumentStatus"))
/*  757 */       result = new InstrumentStatusUpdateMessage(msg);
/*  758 */     else if (type.equals("candle"))
/*  759 */       result = new CandleMessage(msg);
/*  760 */     else if (type.equals("candleGroup"))
/*  761 */       result = new CandleGroupMessage(msg);
/*  762 */     else if (type.equals("instrumentInfo"))
/*  763 */       result = new InstrumentInfoMessage(msg);
/*  764 */     else if (type.equals("accountInfo"))
/*  765 */       result = new AccountInfoMessage(msg);
/*  766 */     else if (type.equals("mmAccountInfo"))
/*  767 */       result = new ExecutorAccountInfoMessage(msg);
/*  768 */     else if (type.equals("timeSync"))
/*  769 */       result = new TimeSyncResponseMessage(msg);
/*  770 */     else if (type.equals("qGrp"))
/*  771 */       result = new QuoteGroupMessage(msg);
/*  772 */     else if (type.equals("qt"))
/*  773 */       result = new QuoteMessage(msg);
/*  774 */     else if (type.equals("qreq"))
/*  775 */       result = new QuoteRequestMessage(msg);
/*  776 */     else if (type.equals("qdeal"))
/*  777 */       result = new QuoteDealMessage(msg);
/*  778 */     else if (type.equals("setMarketDepth"))
/*  779 */       result = new SetMarketDepthMessage(msg);
/*  780 */     else if (type.equals("srv_state"))
/*  781 */       result = new ServiceStateResponseMessage(msg);
/*  782 */     else if (type.equals("srv_command"))
/*  783 */       result = new ControlCommandRequestMessage(msg);
/*  784 */     else if (type.equals("exec_state"))
/*  785 */       result = new ExecutorStateResponseMessage(msg);
/*  786 */     else if (type.equals("quitrq"))
/*  787 */       result = new QuitRequestMessage(msg);
/*  788 */     else if (type.equals("quit"))
/*  789 */       result = new QuitResponseMessage(msg);
/*  790 */     else if (type.equals("verInfo"))
/*  791 */       result = new VersionInfoMessage(msg);
/*  792 */     else if (type.equals("exec_online"))
/*  793 */       result = new ExecutorOnlineMessage(msg);
/*  794 */     else if (type.equals("cust_online"))
/*  795 */       result = new CustodianOnlineMessage(msg);
/*  796 */     else if (type.equals("cqm"))
/*  797 */       result = new CurrencyQuoteMessage(msg);
/*  798 */     else if (type.equals("bcm"))
/*  799 */       result = new BestCurrencyMarketMessage(msg);
/*  800 */     else if (type.equals("market_state_response"))
/*  801 */       result = new MarketStateResponseMessage(msg);
/*  802 */     else if (type.equals("pos_history"))
/*  803 */       result = new PositionHistoryRequestMessage(msg);
/*  804 */     else if (type.equals("pos_history_statement"))
/*  805 */       result = new PositionHistoryResponseMessage(msg);
/*  806 */     else if (type.equals("place_order_ok"))
/*  807 */       result = new PlaceOrderOkResponseMessage(msg);
/*  808 */     else if (type.equals("place_order"))
/*  809 */       result = new PlaceOrderRequestMessage(msg);
/*  810 */     else if (type.equals("change_position"))
/*  811 */       result = new ChangePositionRequestMessage(msg);
/*  812 */     else if (type.equals("change_order"))
/*  813 */       result = new ChangeOrderRequestMessage(msg);
/*  814 */     else if (type.equals("position_updated"))
/*  815 */       result = new PositionUpdatedResponseMessage(msg);
/*  816 */     else if (type.equals("stat"))
/*  817 */       result = new StatRequestMessage(msg);
/*  818 */     else if (type.equals("stat_response"))
/*  819 */       result = new StatResponseMessage(msg);
/*  820 */     else if (type.equals("account"))
/*  821 */       result = new AccountUpdatedResponseMessage(msg);
/*  822 */     else if (type.equals("news_headers"))
/*  823 */       result = new MarketNewsMessageGroup(msg);
/*  824 */     else if (type.equals("close_position"))
/*  825 */       result = new ClosePositionRequestMessage(msg);
/*  826 */     else if (type.equals("market_update"))
/*  827 */       result = new MarketUpdateResponseMessage(msg);
/*  828 */     else if (type.equals("order_updated_response"))
/*  829 */       result = new OrderUpdatedResponseMessage(msg);
/*  830 */     else if (type.equals("order_executed"))
/*  831 */       result = new OrderExecutedResponseMessage(msg);
/*  832 */     else if (type.equals("order_cancelled"))
/*  833 */       result = new OrderCancelledResponseMessage(msg);
/*  834 */     else if (type.equals("position_closed"))
/*  835 */       result = new PositionClosedResponseMessage(msg);
/*  836 */     else if (type.equals("state"))
/*  837 */       result = new StateMessage(msg);
/*  838 */     else if (type.equals("state_exec"))
/*  839 */       result = new ExecutorPropertyStateMessage(msg);
/*  840 */     else if (type.equals("state_session"))
/*  841 */       result = new SessionPropertyStateMessage(msg);
/*  842 */     else if (type.equals("session_list"))
/*  843 */       result = new SessionListRequestMessage(msg);
/*  844 */     else if (type.equals("exp_trans"))
/*  845 */       result = new ExposureTransferRequestMessage(msg);
/*  846 */     else if (type.equals("candle_subsc"))
/*  847 */       result = new CandleSubscribeRequestMessage(msg);
/*  848 */     else if (type.equals("smspc"))
/*  849 */       result = new SignalMessage(msg);
/*  850 */     else if (type.equals("reportInfo"))
/*  851 */       result = new ReportInfo(msg);
/*  852 */     else if (type.equals("reportParameter"))
/*  853 */       result = new ReportParameter(msg);
/*  854 */     else if (type.equals("admin"))
/*  855 */       result = new AdminMessage(msg);
/*  856 */     else if (type.equals("ordr_sync"))
/*  857 */       result = new OrderSyncMessage(msg);
/*  858 */     else if (type.equals("offer_subsc"))
/*  859 */       result = new TradableOfferSubscribeRequestMessage(msg);
/*  860 */     else if (type.equals("ch"))
/*  861 */       result = new CandleHistoryGroupMessage(msg);
/*  862 */     else if (type.equals("trade_offer"))
/*  863 */       result = new TradeOffer(msg);
/*  864 */     else if (type.equals("orderExFeedbackRequest"))
/*  865 */       result = new OrderExecutionFeedbackRequest(msg);
/*  866 */     else if (type.equals("mergePositions"))
/*  867 */       result = new MergePositionsMessage(msg);
/*  868 */     else if (type.equals("deliv_req"))
/*  869 */       result = new DeliveryReportRequestMessage(msg);
/*  870 */     else if (type.equals("deliv_rept"))
/*  871 */       result = new DeliveryReportResponseMessage(msg);
/*  872 */     else if (type.equals("workspace_request"))
/*  873 */       result = new CurvesWorkspaceRequestMessage(msg);
/*  874 */     else if (type.equals("workspace_response"))
/*  875 */       result = new CurvesWorkspaceResponseMessage(msg);
/*  876 */     else if (type.equals("childAcceptor"))
/*  877 */       result = new ChildSocketAuthAcceptorMessage(msg);
/*  878 */     else if (type.equals("serialWrapper"))
/*  879 */       result = new JSonSerializableWrapper(msg);
/*  880 */     else if (type.equals("serializableMessage"))
/*  881 */       result = new SerializableProtocolMessage(msg);
/*  882 */     else if (type.equals("dbWrite"))
/*  883 */       result = new DbWriteMessage(msg);
/*  884 */     else if (type.equals("act"))
/*  885 */       result = new ActivityMessage(msg);
/*  886 */     else if (type.equals("idRez"))
/*  887 */       result = new IdRezMessage(msg);
/*  888 */     else if (type.equals("accInfoReq"))
/*  889 */       result = new AccountInfoRequest(msg);
/*  890 */     else if (type.equals("orderReq"))
/*  891 */       result = new OrderRequest(msg);
/*  892 */     else if (type.equals("orderResp"))
/*  893 */       result = new OrderResponse(msg);
/*  894 */     else if (type.equals("dynaSQL"))
/*  895 */       result = new DynaSqlMessage(msg);
/*  896 */     else if (type.equals("trade"))
/*  897 */       result = new TradeMessage(msg);
/*  898 */     else if (type.equals("sff"))
/*  899 */       result = new SetFeedbackFrequencyMessage(msg);
/*  900 */     else if (type.equals("reqInProc"))
/*  901 */       result = new RequestInProcessMessage(msg);
/*  902 */     else if (type.equals("order_history_request"))
/*  903 */       result = new OrderHistoryRequestMessage(msg);
/*  904 */     else if (type.equals("execOrder"))
/*  905 */       result = new ExecutorOrderMessage(msg);
/*  906 */     else if (type.equals("execTrade"))
/*  907 */       result = new ExecutorTradeMessage(msg);
/*  908 */     else if (type.equals("execResult"))
/*  909 */       result = new ExecutionResultMessage(msg);
/*  910 */     else if (type.equals("routerStatistics"))
/*  911 */       result = new RouterStatisticsMessage(msg);
/*  912 */     else if (type.equals("ccn"))
/*  913 */       result = new ConnectionCloseNotification(msg);
/*  914 */     else if (type.equals("cmbunch"))
/*  915 */       result = new MarketBunch(msg);
/*  916 */     else if (type.equals("ratioChange"))
/*  917 */       result = new FundRatioChangeMessage(msg);
/*  918 */     else if (type.equals("sesn_report"))
/*  919 */       result = new ApiSessionReportMessage(msg);
/*  920 */     else if (type.equals("authresp"))
/*  921 */       result = new AuthentificationResponseMessage(msg);
/*  922 */     else if (type.equals("authreq"))
/*  923 */       result = new AuthentificationRequestMessage(msg);
/*  924 */     else if (type.equals("bpm"))
/*  925 */       result = new BinaryPartMessage(msg);
/*  926 */     else if (type.equals("streamHeader"))
/*  927 */       result = new StreamHeaderMessage(msg);
/*  928 */     else if (type.equals("streamState"))
/*  929 */       result = new StreamingStatus(msg);
/*  930 */     else if (type.equals("ftransfer"))
/*  931 */       result = new FundAccountTransferMessage(msg);
/*  932 */     else if (type.equals("delta"))
/*  933 */       result = new DeltaMessage(msg);
/*  934 */     else if (type.equals("clientInfo"))
/*  935 */       result = new ClientInfoMessage(msg);
/*  936 */     else if (type.equals("usrCtl"))
/*  937 */       result = new UserControlMessage(msg);
/*  938 */     else if (type.equals("expoRequest"))
/*  939 */       result = new PrimeBrokerExposureRequestMessage(msg);
/*  940 */     else if (type.equals("brokerInstrumentExpo"))
/*  941 */       result = new InstrumentExposure(msg);
/*  942 */     else if (type.equals("expoResponse"))
/*  943 */       result = new PrimeBrokerExposureResponseMessage(msg);
/*  944 */     else if (type.equals("execut"))
/*  945 */       result = new ExecutorAccountsMessage(msg);
/*  946 */     else if (type.equals("money"))
/*  947 */       result = new MoneyMessage(msg);
/*  948 */     else if (type.equals("ordCancel"))
/*  949 */       result = new OrderCancelMessage(msg);
/*  950 */     else if (type.equals("news_story"))
/*  951 */       result = new NewsStoryMessage(msg);
/*  952 */     else if (type.equals("news_subscr"))
/*  953 */       result = new NewsSubscribeRequest(msg);
/*  954 */     else if (type.equals("news_content_req"))
/*  955 */       result = new NewsContentRequest(msg);
/*  956 */     else if (type.equals("manyUsers"))
/*  957 */       result = new ManyUsersWarningMessage(msg);
/*  958 */     else if (type.equals("strategy_run"))
/*  959 */       result = new StrategyRunRequestMessage(msg);
/*  960 */     else if (type.equals("strategy_run_chunk"))
/*  961 */       result = new StrategyRunChunkRequestMessage(msg);
/*  962 */     else if (type.equals("strategy_run_resp"))
/*  963 */       result = new StrategyRunResponseMessage(msg);
/*  964 */     else if (type.equals("strategy_update"))
/*  965 */       result = new StrategyUpdateRequestMessage(msg);
/*  966 */     else if (type.equals("strategy_stop"))
/*  967 */       result = new StrategyStopRequestMessage(msg);
/*  968 */     else if (type.equals("strategies_list"))
/*  969 */       result = new StrategiesListRequestMessage(msg);
/*  970 */     else if (type.equals("strategies_list_resp"))
/*  971 */       result = new StrategiesListResponseMessage(msg);
/*  972 */     else if (type.equals("strategy.state.message"))
/*  973 */       result = new StrategyStateMessage(msg);
/*  974 */     else if (type.equals("strategy_broadcast"))
/*  975 */       result = new StrategyBroadcastMessage(msg);
/*  976 */     else if (type.equals("strategy_run_error"))
/*  977 */       result = new StrategyRunErrorResponseMessage(msg);
/*  978 */     else if (type.equals("fmngreq"))
/*  979 */       result = new FileMngRequestMessage(msg);
/*  980 */     else if (type.equals("fmngresp"))
/*  981 */       result = new FileMngResponseMessage(msg);
/*  982 */     else if (type.equals("fileitem"))
/*  983 */       result = new FileItem(msg);
/*  984 */     else if (type.equals("execInfo"))
/*  985 */       result = new ExecutorInfoMessage(msg);
/*  986 */     else if (type.equals("df_cm"))
/*  987 */       result = new DFTickMessage(msg);
/*  988 */     else if (type.equals("df_candle"))
/*  989 */       result = new DFCandleMessage(msg);
/*  990 */     else if (type.equals("df_candle_group"))
/*  991 */       result = new DFCandleGroupMessage(msg);
/*  992 */     else if (type.equals("user.properties.request"))
/*  993 */       result = new UserPropertiesRequestMessage(msg);
/*  994 */     else if (type.equals("user.properties.response"))
/*  995 */       result = new UserPropertiesResponseMessage(msg);
/*  996 */     else if (type.equals("user.properties.change"))
/*  997 */       result = new UserPropertiesChangeMessage(msg);
/*  998 */     else if (type.equals("orderGroupsBinaryMessage"))
/*  999 */       result = new OrderGroupsBinaryMessage(msg);
/* 1000 */     else if (type.equals("task_prog"))
/* 1001 */       result = new TaskProgressMessage(msg);
/* 1002 */     else if (type.equals("feedHistReq"))
/* 1003 */       result = new FeedHistoryRequest(msg);
/* 1004 */     else if (type.equals("blp_swap_subsc"))
/* 1005 */       result = new BloombergSwapSubscribeRequestMessage(msg);
/* 1006 */     else if (type.equals("blp_swap_unsubsc"))
/* 1007 */       result = new BloombergSwapUnsubscribeRequestMessage(msg);
/* 1008 */     else if (type.equals("blp_candle_subsc"))
/* 1009 */       result = new BloombergCandleSubscribeRequestMessage(msg);
/* 1010 */     else if (type.equals("esignal_tm"))
/* 1011 */       result = new ESignalTickMessage(msg);
/* 1012 */     else if (type.equals("df_history_start_request"))
/* 1013 */       result = new DFHistoryStartRequestMessage(msg);
/* 1014 */     else if (type.equals("df_history_start_response"))
/* 1015 */       result = new DFHistoryStartResponseMessage(msg);
/* 1016 */     else if (type.equals("df_weekends_request"))
/* 1017 */       result = new DFWeekendsRequestMessage(msg);
/* 1018 */     else if (type.equals("df_weekends_response"))
/* 1019 */       result = new DFWeekendsResponseMessage(msg);
/* 1020 */     else if (type.equals("accExp"))
/* 1021 */       result = new AccountExposureMessage(msg);
/* 1022 */     else if (type.equals("accInstrExp"))
/* 1023 */       result = new AccountInstrumentExposureMessage(msg);
/* 1024 */     else if (type.equals("expRes"))
/* 1025 */       result = new ExposureReservationMessage(msg);
/* 1026 */     else if (type.equals("api_online"))
/* 1027 */       result = new ApiOnlineMessage(msg);
/* 1028 */     else if (type.equals("tss_message")) {
/* 1029 */       result = new TSSMessage(msg);
/*      */     }
/*      */ 
/* 1035 */     return result;
/*      */   }
/*      */ 
/*      */   public static String jsonWorkaround(String string)
/*      */   {
/* 1048 */     if (string.indexOf(":\"[") >= 0) {
/* 1049 */       string = string.replace(":\"[", ":[");
/*      */     }
/* 1051 */     if (string.indexOf("]\"") >= 0) {
/* 1052 */       string = string.replace("]\"", "]");
/*      */     }
/* 1054 */     if (string.indexOf("\\\"") >= 0) {
/* 1055 */       string = string.replace("\\\"", "\"");
/*      */     }
/*      */ 
/* 1058 */     return string;
/*      */   }
/*      */ 
/*      */   public Map<String, Object> getProperties() {
/* 1062 */     return this.properties;
/*      */   }
/*      */ 
/*      */   public void setProperty(String name, Object property) {
/* 1066 */     this.properties.put(name, property);
/*      */   }
/*      */ 
/*      */   public Object getProperty(String name) {
/* 1070 */     return this.properties.get(name);
/*      */   }
/*      */ 
/*      */   public void clearProperty(String name) {
/* 1074 */     this.properties.remove(name);
/*      */   }
/*      */ 
/*      */   public long getPriority() {
/* 1078 */     return this.priority;
/*      */   }
/*      */ 
/*      */   public void setPriority(long priority) {
/* 1082 */     this.priority = priority;
/*      */   }
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*      */     while (true)
/*      */     {
/* 1093 */       String s = "<script>parent.push({\"hsexUser\":\"false\",\"instrument\":\"EUR/USD\",\"amount\":\"1\",\"priceClient\":\"1.21400\",\"type\":\"order\",\"timeSyncMs\":\"1143736114585\",\"orderId\":\"76548.2\",\"orderGroupId\":\"76548\",\"state\":\"EXECUTING\",\"dir\":\"OPEN\",\"side\":\"BUY\"})</script>";
/*      */ 
/* 1095 */       long startTimeMillis = System.currentTimeMillis();
/*      */ 
/* 1097 */       ProtocolMessage pm = parse(s);
/* 1098 */       System.out.println("pm.toString=" + pm.toProtocolString());
/*      */ 
/* 1100 */       long endTimeMillis = System.currentTimeMillis();
/* 1101 */       System.out.println("Time spent: " + (endTimeMillis - startTimeMillis));
/*      */       try
/*      */       {
/* 1104 */         Thread.sleep(2000L);
/*      */       } catch (InterruptedException ie) {
/* 1106 */         break;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public ProtocolMessage clone() throws CloneNotSupportedException {
/* 1112 */     super.clone();
/* 1113 */     return parse(toProtocolString());
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.ProtocolMessage
 * JD-Core Version:    0.6.0
 */