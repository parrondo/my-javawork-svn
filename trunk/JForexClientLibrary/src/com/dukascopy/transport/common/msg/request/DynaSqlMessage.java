/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class DynaSqlMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "dynaSQL";
/*     */   public static final String ACTION = "act";
/*     */   public static final String REQUEST_PARAMS = "params";
/*     */   public static final String RET_DATA_ARRAY = "ret_data";
/*     */   public static final String SQL = "sql";
/*     */   public static final String IS_SYNC = "async";
/*     */   public static final String ACTION_EXECUTE = "act_exec";
/*     */   public static final String ACTION_SELECT = "act_sel";
/*     */ 
/*     */   public DynaSqlMessage()
/*     */   {
/*  36 */     setType("dynaSQL");
/*     */   }
/*     */ 
/*     */   public DynaSqlMessage(String action)
/*     */   {
/*  44 */     setType("dynaSQL");
/*  45 */     put("act", action);
/*     */   }
/*     */ 
/*     */   public DynaSqlMessage(ProtocolMessage message)
/*     */   {
/*  54 */     super(message);
/*  55 */     setType("dynaSQL");
/*  56 */     put("sql", message.getString("sql"));
/*  57 */     put("act", message.getString("act"));
/*  58 */     put("async", message.getBool("async"));
/*  59 */     put("params", message.getJSONArray("params"));
/*  60 */     put("ret_data", message.getJSONArray("ret_data"));
/*     */   }
/*     */ 
/*     */   public boolean isSync()
/*     */   {
/*  69 */     return getBoolean("async");
/*     */   }
/*     */ 
/*     */   public void setIsSync(Boolean isSync)
/*     */   {
/*  78 */     put("async", isSync);
/*     */   }
/*     */ 
/*     */   public String getAction()
/*     */   {
/*  87 */     return getString("act");
/*     */   }
/*     */ 
/*     */   public void setAction(String action)
/*     */   {
/*  96 */     put("act", action);
/*     */   }
/*     */ 
/*     */   public String getSQL()
/*     */   {
/* 105 */     return getString("sql");
/*     */   }
/*     */ 
/*     */   public void setSQL(String sql)
/*     */   {
/* 114 */     put("sql", sql);
/*     */   }
/*     */ 
/*     */   public void addRequestParam(int index, Object val)
/*     */   {
/* 119 */     JSONArray json = getRequestParams();
/* 120 */     if (json == null) {
/* 121 */       json = new JSONArray();
/*     */     }
/* 123 */     json.put(new RequestParam(index, val));
/* 124 */     setRequestParams(json);
/*     */   }
/*     */ 
/*     */   public void addNullRequestParam(int index, int type)
/*     */   {
/* 133 */     JSONArray json = getRequestParams();
/* 134 */     if (json == null) {
/* 135 */       json = new JSONArray();
/*     */     }
/* 137 */     json.put(new RequestParam(index, type));
/* 138 */     setRequestParams(json);
/*     */   }
/*     */ 
/*     */   public JSONArray getRequestParams()
/*     */   {
/* 147 */     return getJSONArray("params");
/*     */   }
/*     */ 
/*     */   public List<RequestParam> getParamsList()
/*     */   {
/* 156 */     JSONArray arr = getJSONArray("params");
/* 157 */     if (arr == null) {
/* 158 */       return null;
/*     */     }
/* 160 */     List ret = new ArrayList();
/* 161 */     for (int i = 0; i < arr.length(); i++) {
/* 162 */       ret.add(new RequestParam(arr.getJSONObject(i)));
/*     */     }
/* 164 */     return ret;
/*     */   }
/*     */ 
/*     */   public void setRequestParams(JSONArray params)
/*     */   {
/* 173 */     put("params", params);
/*     */   }
/*     */ 
/*     */   public JSONArray getReturnData()
/*     */   {
/* 182 */     return getJSONArray("ret_data");
/*     */   }
/*     */ 
/*     */   public void setReturnData(JSONArray data)
/*     */   {
/* 191 */     put("ret_data", data); } 
/*     */   public class RequestParam extends ProtocolMessage { public static final String INDEX = "idx";
/*     */     public static final String VALUE = "val";
/*     */     public static final String DATA_TYPE = "dt";
/*     */     public static final String TYPE_STRING = "string";
/*     */     public static final String TYPE_BIG_DECIMAL = "bigdec";
/*     */     public static final String TYPE_INTEGER = "int";
/*     */     public static final String TYPE_LONG = "long";
/*     */     public static final String TYPE_DATE = "date";
/*     */     public static final String TYPE_TIMESTAMP = "timest";
/*     */     public static final String NULL_TYPE = "null";
/*     */ 
/* 209 */     public RequestParam(int index, Object val) { setObject(index, val);
/*     */     }
/*     */ 
/*     */     public RequestParam(int index, int type)
/*     */     {
/* 218 */       setNull(index, type);
/*     */     }
/*     */ 
/*     */     public RequestParam(JSONObject obj) {
/* 222 */       put("dt", obj.getString("dt"));
/* 223 */       put("idx", obj.getString("idx"));
/* 224 */       put("val", obj.getString("val"));
/*     */     }
/*     */ 
/*     */     public void setObject(int index, Object x)
/*     */     {
/* 234 */       if (x == null) {
/* 235 */         throw new IllegalArgumentException(" NULL value not suported, use setNull method instead.");
/*     */       }
/* 237 */       put("idx", "" + index);
/* 238 */       if ((x instanceof String)) {
/* 239 */         put("dt", "string");
/* 240 */         put("val", x.toString());
/* 241 */       } else if ((x instanceof BigDecimal)) {
/* 242 */         put("dt", "bigdec");
/* 243 */         put("val", x.toString());
/* 244 */       } else if ((x instanceof Integer)) {
/* 245 */         put("dt", "int");
/* 246 */         put("val", "" + x);
/* 247 */       } else if ((x instanceof Long)) {
/* 248 */         put("dt", "long");
/* 249 */         put("val", "" + x);
/* 250 */       } else if ((x instanceof Timestamp)) {
/* 251 */         put("dt", "timest");
/* 252 */         put("val", "" + ((Timestamp)x).getTime());
/* 253 */       } else if ((x instanceof java.util.Date)) {
/* 254 */         put("dt", "date");
/* 255 */         putDate("val", (java.util.Date)x);
/*     */       } else {
/* 257 */         throw new IllegalArgumentException("Objects of type " + x.getClass() + " are not supported.");
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setNull(int index, int type)
/*     */     {
/* 268 */       put("idx", "" + index);
/* 269 */       put("dt", "null");
/* 270 */       put("val", "" + type);
/*     */     }
/*     */ 
/*     */     public int getNullType() {
/* 274 */       String ret = getString("val");
/* 275 */       return Integer.parseInt(ret);
/*     */     }
/*     */ 
/*     */     public int getIndex() {
/* 279 */       return Integer.parseInt(getString("idx"));
/*     */     }
/*     */ 
/*     */     public Object getObject() {
/* 283 */       String ret = getString("val");
/* 284 */       String type = getString("dt");
/* 285 */       if ("string".equals(type))
/* 286 */         return ret;
/* 287 */       if ("bigdec".equals(type))
/* 288 */         return new BigDecimal(ret);
/* 289 */       if ("int".equals(type))
/* 290 */         return new Integer(ret);
/* 291 */       if ("long".equals(type))
/* 292 */         return new Long(ret);
/* 293 */       if ("date".equals(type))
/* 294 */         return new java.sql.Date(getDate("val").getTime());
/* 295 */       if ("timest".equals(type)) {
/* 296 */         return new Timestamp(Long.parseLong(ret));
/*     */       }
/* 298 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.DynaSqlMessage
 * JD-Core Version:    0.6.0
 */