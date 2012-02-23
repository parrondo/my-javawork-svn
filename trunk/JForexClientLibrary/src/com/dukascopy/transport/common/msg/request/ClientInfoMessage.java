/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.AccountState;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class ClientInfoMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "clientInfo";
/*     */   public static final String CLIENT_ID = "clientId";
/*     */   public static final String RATIO = "ratio";
/*     */   public static final String LOSS_LIMIT = "lossLimit";
/*     */   public static final String DAY_LOSS_LIMIT = "dayLossLimit";
/*     */   public static final String LOSS_LIMIT_CUR = "lossLimitCur";
/*     */   public static final String STATE = "state";
/*     */   public static final String CLOSED_PL = "clPl";
/*     */   public static final String BALANCE_SYS_USD = "sysUSD";
/*     */   public static final String BALANCE_MUTIPLICATOR = "balMult";
/*     */   public static final String TAKE_PROFIT = "tprofit";
/*     */   public static final String MIN_LOSS_LIMIT = "minLossLimit";
/*     */   public static final String BALANCE = "bal";
/*     */   public static final String BALANCE_CUR = "balCur";
/*     */   public static final String COMM = "comm";
/*     */ 
/*     */   public ClientInfoMessage(String s)
/*     */     throws ParseException
/*     */   {
/*  45 */     super(s);
/*  46 */     setType("clientInfo");
/*     */   }
/*     */ 
/*     */   public ClientInfoMessage(JSONObject s) throws ParseException {
/*  50 */     super(s);
/*  51 */     setType("clientInfo");
/*     */   }
/*     */ 
/*     */   public ClientInfoMessage()
/*     */   {
/*  59 */     setType("clientInfo");
/*     */   }
/*     */ 
/*     */   public ClientInfoMessage(ProtocolMessage message)
/*     */   {
/*  68 */     super(message);
/*  69 */     put("clientId", message.getString("clientId"));
/*  70 */     put("ratio", message.getString("ratio"));
/*  71 */     put("lossLimit", message.getString("lossLimit"));
/*  72 */     put("dayLossLimit", message.getString("dayLossLimit"));
/*  73 */     put("lossLimitCur", message.getString("lossLimitCur"));
/*  74 */     put("state", message.getString("state"));
/*  75 */     put("clPl", message.getJSONArray("clPl"));
/*  76 */     put("sysUSD", message.getString("sysUSD"));
/*  77 */     put("balMult", message.getString("balMult"));
/*  78 */     put("tprofit", message.getString("tprofit"));
/*  79 */     put("minLossLimit", message.getString("minLossLimit"));
/*  80 */     put("bal", message.getString("bal"));
/*  81 */     put("balCur", message.getString("balCur"));
/*  82 */     put("comm", message.getJSONArray("comm"));
/*     */   }
/*     */ 
/*     */   public String getClientId()
/*     */   {
/*  91 */     return getString("clientId");
/*     */   }
/*     */ 
/*     */   public void setClientId(String clientId)
/*     */   {
/* 100 */     put("clientId", clientId);
/*     */   }
/*     */ 
/*     */   public BigDecimal getRatio()
/*     */   {
/* 109 */     String ratio = getString("ratio");
/* 110 */     if (ratio == null) {
/* 111 */       return null;
/*     */     }
/* 113 */     return new BigDecimal(ratio);
/*     */   }
/*     */ 
/*     */   public void setRatio(BigDecimal ratio)
/*     */   {
/* 123 */     if (ratio != null)
/* 124 */       put("ratio", ratio.toPlainString());
/*     */   }
/*     */ 
/*     */   public Money getLossLimit()
/*     */   {
/* 134 */     String lossLimit = getString("lossLimit");
/* 135 */     String lossLimitCur = getString("lossLimitCur");
/* 136 */     if (lossLimit != null) {
/* 137 */       return new Money(lossLimit, lossLimitCur);
/*     */     }
/* 139 */     return null;
/*     */   }
/*     */ 
/*     */   public void setLossLimit(BigDecimal lossLimit)
/*     */   {
/* 149 */     if (lossLimit != null)
/* 150 */       put("lossLimit", lossLimit.toPlainString());
/*     */   }
/*     */ 
/*     */   public void setBalance(BigDecimal balance)
/*     */   {
/* 160 */     if (balance != null)
/* 161 */       put("bal", balance.toPlainString());
/*     */   }
/*     */ 
/*     */   public Money getTakeProfit()
/*     */   {
/* 172 */     String takeProfit = getString("tprofit");
/* 173 */     String lossLimitCur = getString("lossLimitCur");
/* 174 */     if (takeProfit != null) {
/* 175 */       return new Money(takeProfit, lossLimitCur);
/*     */     }
/* 177 */     return null;
/*     */   }
/*     */ 
/*     */   public void setTakeProfit(BigDecimal takeProfit)
/*     */   {
/* 187 */     if (takeProfit != null)
/* 188 */       put("tprofit", takeProfit.toPlainString());
/*     */   }
/*     */ 
/*     */   public Money getDayLossLimit()
/*     */   {
/* 198 */     String lossLimit = getString("dayLossLimit");
/* 199 */     String lossLimitCur = getString("lossLimitCur");
/* 200 */     if (lossLimit != null) {
/* 201 */       return new Money(lossLimit, lossLimitCur);
/*     */     }
/* 203 */     return null;
/*     */   }
/*     */ 
/*     */   public Money getBalance()
/*     */   {
/* 214 */     String balance = getString("bal");
/* 215 */     String balanceCur = getString("balCur");
/* 216 */     if (balance != null) {
/* 217 */       return new Money(balance, balanceCur);
/*     */     }
/* 219 */     return null;
/*     */   }
/*     */ 
/*     */   public void setDayLossLimit(BigDecimal lossLimit)
/*     */   {
/* 229 */     if (lossLimit != null)
/* 230 */       put("dayLossLimit", lossLimit.toPlainString());
/*     */   }
/*     */ 
/*     */   public void setLossLimitCur(String lossLimitCur)
/*     */   {
/* 240 */     put("lossLimitCur", lossLimitCur);
/*     */   }
/*     */ 
/*     */   public void setBalanceCur(String balCur)
/*     */   {
/* 249 */     put("balCur", balCur);
/*     */   }
/*     */ 
/*     */   public AccountState getAccountState()
/*     */   {
/* 258 */     String stateString = getString("state");
/* 259 */     if (stateString != null) {
/* 260 */       return AccountState.fromString(stateString);
/*     */     }
/* 262 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAccountState(AccountState state)
/*     */   {
/* 272 */     put("state", state.asString());
/*     */   }
/*     */ 
/*     */   public List<MoneyMessage> getClosedPl()
/*     */     throws ParseException
/*     */   {
/* 282 */     JSONArray a = getJSONArray("clPl");
/*     */ 
/* 284 */     if (a == null) {
/* 285 */       return new ArrayList(0);
/*     */     }
/* 287 */     List set = new ArrayList(a.length());
/* 288 */     for (int i = 0; i < a.length(); i++) {
/* 289 */       MoneyMessage money = new MoneyMessage(a.getJSONObject(i));
/* 290 */       set.add(money);
/*     */     }
/* 292 */     return set;
/*     */   }
/*     */ 
/*     */   public void setClosedPl(List<MoneyMessage> pls)
/*     */   {
/* 301 */     JSONArray a = new JSONArray();
/* 302 */     for (MoneyMessage mon : pls) {
/* 303 */       a.put(mon);
/*     */     }
/* 305 */     put("clPl", a);
/*     */   }
/*     */ 
/*     */   public BigDecimal getBalSysUSD()
/*     */   {
/* 314 */     String bal = getString("sysUSD");
/* 315 */     if (bal == null) {
/* 316 */       return null;
/*     */     }
/* 318 */     return new BigDecimal(bal);
/*     */   }
/*     */ 
/*     */   public void setBalSysUSD(BigDecimal bal)
/*     */   {
/* 328 */     if (bal != null)
/* 329 */       put("sysUSD", bal.toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getBalanceMultiplicator()
/*     */   {
/* 339 */     String balanceString = getString("balMult");
/* 340 */     if (balanceString != null) {
/* 341 */       return new BigDecimal(balanceString);
/*     */     }
/* 343 */     return null;
/*     */   }
/*     */ 
/*     */   public void setBalanceMultiplicator(BigDecimal balance)
/*     */   {
/* 353 */     put("balMult", balance.toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getMinLossLimit()
/*     */   {
/* 364 */     String lossLimit = getString("minLossLimit");
/* 365 */     if (lossLimit != null) {
/* 366 */       return new BigDecimal(lossLimit);
/*     */     }
/* 368 */     return null;
/*     */   }
/*     */ 
/*     */   public void setMinLossLimit(BigDecimal lossLimit)
/*     */   {
/* 378 */     if (lossLimit != null)
/* 379 */       put("minLossLimit", lossLimit.toPlainString());
/*     */   }
/*     */ 
/*     */   public List<MoneyMessage> getComm()
/*     */     throws ParseException
/*     */   {
/* 390 */     JSONArray a = getJSONArray("comm");
/*     */ 
/* 392 */     if (a == null) {
/* 393 */       return new ArrayList(0);
/*     */     }
/* 395 */     List set = new ArrayList(a.length());
/* 396 */     for (int i = 0; i < a.length(); i++) {
/* 397 */       MoneyMessage money = new MoneyMessage(a.getJSONObject(i));
/* 398 */       set.add(money);
/*     */     }
/* 400 */     return set;
/*     */   }
/*     */ 
/*     */   public void setComm(List<MoneyMessage> pls)
/*     */   {
/* 409 */     JSONArray a = new JSONArray();
/* 410 */     for (MoneyMessage mon : pls) {
/* 411 */       a.put(mon);
/*     */     }
/* 413 */     put("comm", a);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ClientInfoMessage
 * JD-Core Version:    0.6.0
 */