/*     */ package com.dukascopy.transport.common.msg;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class DeltaMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "delta";
/*     */   public static final String TRANSACTION_TYPE = "tType";
/*     */   public static final String TRANSACTION_STATUS = "tStatus";
/*     */   public static final String ORDER_ID = "orderId";
/*     */   public static final String START_CLIENT_TIME = "c_tim";
/*     */   public static final String START_SERVER_TIME = "s_tim";
/*     */   public static final String DELTA_CLIENT = "d_cl";
/*     */   public static final String DELTA_API = "d_api";
/*     */   public static final String DELTA_CUSTODIAN = "d_cust";
/*     */   public static final String DELTA_ROUTER = "d_rout";
/*     */   public static final String DELTA_API_IN = "d_api_in";
/*     */   public static final String DELTA_API_OUT = "d_api_out";
/*     */   public static final String DELTA_CUSTODIAN_IN = "d_cust_in";
/*     */   public static final String DELTA_CUSTODIAN_OUT = "d_cust_out";
/*     */   public static final String DELTA_ROUTER_IN = "d_rout_in";
/*     */   public static final String DELTA_ROUTER_OUT = "d_rout_out";
/*     */   public static final String T_MARKET_OPEN = "t_mopen";
/*     */   public static final String T_MARKET_CLOSE = "t_mclose";
/*     */   public static final String T_NEW_PENDING = "t_new_pend";
/*     */   public static final String T_CH_PENDING = "t_ch_pend";
/*     */   public static final String T_BID_PLACE = "t_bo";
/*     */   public static final String T_BID_FILL = "t_bo_fill";
/*     */   public static final String T_STOP_TRIGGER = "t_st_trig";
/*     */   public static final String T_MC = "t_mc";
/*     */   public static final String T_REMOVE_CLIENT = "t_rc";
/*     */   public static final String T_CLIENT_WITHDRAVAL = "t_cw";
/*     */ 
/*     */   public DeltaMessage()
/*     */   {
/*  50 */     setType("delta");
/*     */   }
/*     */ 
/*     */   public DeltaMessage(ProtocolMessage message)
/*     */   {
/*  57 */     super(message);
/*  58 */     setType("delta");
/*     */ 
/*  60 */     put("orderId", message.getString("orderId"));
/*     */ 
/*  62 */     put("tType", message.getString("tType"));
/*  63 */     put("tStatus", message.getString("tStatus"));
/*  64 */     put("c_tim", message.getString("c_tim"));
/*  65 */     put("s_tim", message.getString("s_tim"));
/*     */ 
/*  67 */     put("d_cl", message.getString("d_cl"));
/*  68 */     put("d_api", message.getString("d_api"));
/*  69 */     put("d_cust", message.getString("d_cust"));
/*  70 */     put("d_rout", message.getString("d_rout"));
/*  71 */     put("d_api_in", message.getString("d_api_in"));
/*  72 */     put("d_cust_in", message.getString("d_cust_in"));
/*  73 */     put("d_rout_in", message.getString("d_rout_in"));
/*  74 */     put("d_api_out", message.getString("d_api_out"));
/*  75 */     put("d_cust_out", message.getString("d_cust_out"));
/*  76 */     put("d_rout_out", message.getString("d_rout_out"));
/*     */   }
/*     */ 
/*     */   public DeltaMessage(JSONObject obj) throws ParseException {
/*  80 */     super(obj);
/*  81 */     setType("delta");
/*     */   }
/*     */ 
/*     */   public void setStartServerTime(long time)
/*     */   {
/*  90 */     put("s_tim", "" + time);
/*     */   }
/*     */ 
/*     */   public Long getStartServerTime() {
/*  94 */     return getLong("s_tim");
/*     */   }
/*     */ 
/*     */   public void setStartClientTime(long time) {
/*  98 */     put("c_tim", "" + time);
/*     */   }
/*     */ 
/*     */   public Long getStartClientTime() {
/* 102 */     return getLong("c_tim");
/*     */   }
/*     */ 
/*     */   public String getOrderId()
/*     */   {
/* 111 */     return getString("orderId");
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId)
/*     */   {
/* 120 */     put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public String getTransactionType()
/*     */   {
/* 147 */     return getString("tType");
/*     */   }
/*     */ 
/*     */   public void setTransactionType(String transactionType)
/*     */   {
/* 156 */     put("tType", transactionType);
/*     */   }
/*     */ 
/*     */   public String getTransactionStatus()
/*     */   {
/* 166 */     return getString("tStatus");
/*     */   }
/*     */ 
/*     */   public void setTransactionStatus(String transactionStatus)
/*     */   {
/* 175 */     put("tStatus", transactionStatus);
/*     */   }
/*     */ 
/*     */   public void setDeltaClient(String delta) {
/* 179 */     put("d_cl", delta);
/*     */   }
/*     */   public String getDeltaClient() {
/* 182 */     return getString("d_cl");
/*     */   }
/*     */ 
/*     */   public void setDeltaApi(String delta) {
/* 186 */     put("d_api", delta);
/*     */   }
/*     */   public String getDeltaApi() {
/* 189 */     return getString("d_api");
/*     */   }
/*     */ 
/*     */   public void setDeltaCustodian(String delta) {
/* 193 */     put("d_cust", delta);
/*     */   }
/*     */ 
/*     */   public String getDeltaCustodian() {
/* 197 */     return getString("d_cust");
/*     */   }
/*     */   public String getDeltaRouter() {
/* 200 */     return getString("d_rout");
/*     */   }
/*     */ 
/*     */   public void setDeltaRouter(String delta) {
/* 204 */     put("d_rout", delta);
/*     */   }
/*     */ 
/*     */   public void setDeltaApiIn(String delta)
/*     */   {
/* 209 */     put("d_api_in", delta);
/*     */   }
/*     */   public String getDeltaApiIn() {
/* 212 */     return getString("d_api_in");
/*     */   }
/*     */ 
/*     */   public void setDeltaCustodianIn(String delta) {
/* 216 */     put("d_cust_in", delta);
/*     */   }
/*     */ 
/*     */   public String getDeltaCustodianIn() {
/* 220 */     return getString("d_cust_in");
/*     */   }
/*     */   public String getDeltaRouterIn() {
/* 223 */     return getString("d_rout_in");
/*     */   }
/*     */ 
/*     */   public void setDeltaRouterIn(String delta) {
/* 227 */     put("d_rout_in", delta);
/*     */   }
/*     */ 
/*     */   public void setDeltaApiOut(String delta)
/*     */   {
/* 232 */     put("d_api_out", delta);
/*     */   }
/*     */   public String getDeltaApiOut() {
/* 235 */     return getString("d_api_out");
/*     */   }
/*     */ 
/*     */   public void setDeltaCustodianOut(String delta) {
/* 239 */     put("d_cust_out", delta);
/*     */   }
/*     */ 
/*     */   public String getDeltaCustodianOut() {
/* 243 */     return getString("d_cust_out");
/*     */   }
/*     */   public String getDeltaRouterOut() {
/* 246 */     return getString("d_rout_out");
/*     */   }
/*     */ 
/*     */   public void setDeltaRouterOut(String delta) {
/* 250 */     put("d_rout_out", delta);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.DeltaMessage
 * JD-Core Version:    0.6.0
 */