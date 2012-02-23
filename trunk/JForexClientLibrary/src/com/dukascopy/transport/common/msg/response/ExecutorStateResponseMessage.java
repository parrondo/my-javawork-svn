/*     */ package com.dukascopy.transport.common.msg.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.json.JSONArray;
/*     */ 
/*     */ public class ExecutorStateResponseMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "exec_state";
/*     */   public static final String ACCEPT_ORDERS = "0";
/*     */   public static final String MAX_EXPOSURE = "1";
/*     */   public static final String MIN_EXPOSURE = "2";
/*     */   public static final String CUR_EXPOSURE = "3";
/*     */   public static final String MAX_ORDER_VOLUME = "4";
/*     */   public static final String BALANCE = "5";
/*     */   public static final String TURN_OVER = "6";
/*     */   public static final String USD_BALANCE = "7";
/*     */   public static final String BALANCE_PRIMARY = "8";
/*     */   public static final String BALANCE_SECONDARY = "9";
/*     */   public static final String INSTRUMENT_LOCKED = "10";
/*     */   public static final String INSTRUMENT_MARGIN = "11";
/*     */   public static final String INSTRUMENT_LEVERAGE = "12";
/*     */   public static final String OFFLINE_INTERVAL = "13";
/*     */   public static final String CRITIC_PL_VALUE = "14";
/*     */ 
/*     */   public ExecutorStateResponseMessage()
/*     */   {
/*  45 */     setType("exec_state");
/*     */   }
/*     */ 
/*     */   public ExecutorStateResponseMessage(ProtocolMessage message)
/*     */   {
/*  54 */     super(message);
/*  55 */     setType("exec_state");
/*  56 */     put("k", message.getJSONArray("k"));
/*  57 */     put("v", message.getJSONArray("v"));
/*     */   }
/*     */ 
/*     */   public boolean isExecutorLocked() {
/*  61 */     Map vals = getStates();
/*  62 */     Object ls = vals.get("0");
/*  63 */     if (ls != null)
/*     */     {
/*  65 */       return ls.toString().equals("false");
/*     */     }
/*     */ 
/*  70 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isInstrumentLocked(String instrument)
/*     */   {
/*  75 */     Map vals = getStates();
/*  76 */     Object ls = vals.get("10." + instrument);
/*  77 */     if (ls != null)
/*     */     {
/*  79 */       return !ls.toString().equals("false");
/*     */     }
/*     */ 
/*  84 */     return true;
/*     */   }
/*     */ 
/*     */   public BigDecimal getInstrumentMargin(String instrument)
/*     */   {
/*  89 */     Map vals = getStates();
/*  90 */     Object ls = vals.get("11." + instrument);
/*  91 */     if (ls != null) {
/*  92 */       return new BigDecimal(ls.toString());
/*     */     }
/*  94 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public String[] getOffileInterwall()
/*     */   {
/*  99 */     Map vals = getStates();
/* 100 */     Object ls = vals.get("13");
/* 101 */     if (ls != null) {
/* 102 */       String s = ls.toString();
/* 103 */       String[] array = s.split(";");
/* 104 */       if (array.length > 5) {
/* 105 */         return new String[] { array[0] + ";" + array[1] + ";" + array[2], array[3] + ";" + array[4] + ";" + array[5] };
/*     */       }
/* 107 */       return new String[] { "", "" };
/*     */     }
/*     */ 
/* 110 */     return new String[] { "", "" };
/*     */   }
/*     */ 
/*     */   public BigDecimal getInstrumentLeaverage(String instrument)
/*     */   {
/* 115 */     Map vals = getStates();
/* 116 */     Object ls = vals.get("12." + instrument);
/* 117 */     if (ls != null) {
/* 118 */       return new BigDecimal(ls.toString());
/*     */     }
/* 120 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public BigDecimal getMaxExposure(String instrument)
/*     */   {
/* 125 */     Map vals = getStates();
/* 126 */     Object ls = vals.get("1." + instrument);
/* 127 */     if (ls != null) {
/* 128 */       return new BigDecimal(ls.toString());
/*     */     }
/* 130 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public BigDecimal getMinExposure(String instrument)
/*     */   {
/* 136 */     Map vals = getStates();
/* 137 */     Object ls = vals.get("2." + instrument);
/* 138 */     if (ls != null) {
/* 139 */       return new BigDecimal(ls.toString());
/*     */     }
/* 141 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public BigDecimal getCurExposure(String instrument)
/*     */   {
/* 147 */     Map vals = getStates();
/* 148 */     Object ls = vals.get("3." + instrument);
/* 149 */     if (ls != null) {
/* 150 */       return new BigDecimal(ls.toString());
/*     */     }
/* 152 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public BigDecimal getMaxOrderVolume(String instrument)
/*     */   {
/* 157 */     Map vals = getStates();
/* 158 */     Object ls = vals.get("4." + instrument);
/* 159 */     if (ls != null) {
/* 160 */       return new BigDecimal(ls.toString());
/*     */     }
/* 162 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public BigDecimal getBalance(String instrument)
/*     */   {
/* 167 */     Map vals = getStates();
/* 168 */     Object ls = vals.get("5." + instrument);
/* 169 */     if (ls != null) {
/* 170 */       return new BigDecimal(ls.toString());
/*     */     }
/* 172 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public BigDecimal getTurnOver()
/*     */   {
/* 177 */     Map vals = getStates();
/* 178 */     Object ls = vals.get("6");
/* 179 */     if (ls != null) {
/* 180 */       return new BigDecimal(ls.toString());
/*     */     }
/* 182 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public BigDecimal getUSDBalance(String instrument)
/*     */   {
/* 187 */     Map vals = getStates();
/* 188 */     Object ls = vals.get("7." + instrument);
/* 189 */     if (ls != null) {
/* 190 */       return new BigDecimal(ls.toString());
/*     */     }
/* 192 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public BigDecimal getCriticPLValue()
/*     */   {
/* 197 */     Map vals = getStates();
/* 198 */     Object ls = vals.get("14");
/* 199 */     if (ls != null) {
/* 200 */       return new BigDecimal(ls.toString());
/*     */     }
/* 202 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public BigDecimal getBalancePrimary(String instrument)
/*     */   {
/* 207 */     Map vals = getStates();
/* 208 */     Object ls = vals.get("8." + instrument);
/* 209 */     if (ls != null) {
/* 210 */       return new BigDecimal(ls.toString());
/*     */     }
/* 212 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public BigDecimal getBalanceSecondary(String instrument)
/*     */   {
/* 217 */     Map vals = getStates();
/* 218 */     Object ls = vals.get("9." + instrument);
/* 219 */     if (ls != null) {
/* 220 */       return new BigDecimal(ls.toString());
/*     */     }
/* 222 */     return new BigDecimal(0);
/*     */   }
/*     */ 
/*     */   public Map getStates()
/*     */   {
/* 229 */     Map res = new HashMap();
/* 230 */     JSONArray keys = getJSONArray("k");
/* 231 */     JSONArray values = getJSONArray("v");
/* 232 */     if ((keys != null) && (values != null)) {
/* 233 */       for (int i = 0; i < keys.length(); i++) {
/* 234 */         res.put(keys.getString(i), values.getString(i));
/*     */       }
/*     */     }
/* 237 */     return res;
/*     */   }
/*     */ 
/*     */   public void setStates(Map states) {
/* 241 */     put("k", new JSONArray());
/* 242 */     JSONArray keys = getJSONArray("k");
/* 243 */     put("v", new JSONArray());
/* 244 */     JSONArray values = getJSONArray("v");
/* 245 */     Iterator i = states.keySet().iterator();
/* 246 */     while (i.hasNext()) {
/* 247 */       String key = i.next().toString();
/* 248 */       keys.put(key);
/* 249 */       values.put(states.get(key));
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.ExecutorStateResponseMessage
 * JD-Core Version:    0.6.0
 */