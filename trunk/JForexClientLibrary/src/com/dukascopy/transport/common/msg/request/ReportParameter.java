/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ 
/*     */ public class ReportParameter extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "reportParameter";
/*     */   public static final String PARAM_TYPE_INTEGER = "integer";
/*     */   public static final String PARAM_TYPE_DATE = "date";
/*     */   public static final String PARAM_TYPE_STRING = "string";
/*     */   public static final String PARAM_TYPE_BOOLEAN = "boolean";
/*     */ 
/*     */   public ReportParameter()
/*     */   {
/*  24 */     setType("reportParameter");
/*     */   }
/*     */ 
/*     */   public ReportParameter(String name, String key, String type, boolean isForInput, String value)
/*     */   {
/*  32 */     setType("reportParameter");
/*  33 */     setName(name);
/*  34 */     setKey(key);
/*  35 */     setDataType(type);
/*  36 */     setIsForInput(isForInput);
/*  37 */     if (value != null)
/*  38 */       setValue(value);
/*     */   }
/*     */ 
/*     */   public ReportParameter(ProtocolMessage message)
/*     */   {
/*  48 */     super(message);
/*  49 */     setType("reportParameter");
/*  50 */     put("name", message.getString("name"));
/*  51 */     put("key", message.getString("key"));
/*  52 */     put("dataType", message.getString("dataType"));
/*  53 */     put("value", message.getString("value"));
/*  54 */     put("isForInput", message.getBoolean("isForInput"));
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  62 */     put("name", name);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  71 */     return getString("name");
/*     */   }
/*     */ 
/*     */   public void setKey(String key)
/*     */   {
/*  79 */     put("key", key);
/*     */   }
/*     */ 
/*     */   public String getKey()
/*     */   {
/*  88 */     return getString("key");
/*     */   }
/*     */ 
/*     */   public void setDataType(String type)
/*     */   {
/*  96 */     put("dataType", type);
/*     */   }
/*     */ 
/*     */   public String getDataType()
/*     */   {
/* 105 */     return getString("dataType");
/*     */   }
/*     */ 
/*     */   public void setValue(String value)
/*     */   {
/* 114 */     put("value", value);
/*     */   }
/*     */ 
/*     */   public String getValue()
/*     */   {
/* 123 */     return getString("value");
/*     */   }
/*     */ 
/*     */   public void setIsForInput(boolean isForInput)
/*     */   {
/* 132 */     put("isForInput", isForInput);
/*     */   }
/*     */ 
/*     */   public boolean getIsForInput()
/*     */   {
/* 141 */     return getBoolean("isForInput");
/*     */   }
/*     */ 
/*     */   public String getParameterString()
/*     */   {
/* 150 */     return getString("key") + "=" + getString("value");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ReportParameter
 * JD-Core Version:    0.6.0
 */