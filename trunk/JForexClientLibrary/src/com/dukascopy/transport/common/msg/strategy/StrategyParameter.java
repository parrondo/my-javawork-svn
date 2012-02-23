/*     */ package com.dukascopy.transport.common.msg.strategy;
/*     */ 
/*     */ import com.dukascopy.transport.util.Base64;
/*     */ import java.nio.charset.Charset;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class StrategyParameter extends JSONObject
/*     */ {
/*  14 */   private static final Charset UTF8 = Charset.forName("UTF-8");
/*     */   private static final String NAME = "name";
/*     */   private static final String TYPE = "type";
/*     */   private static final String VALUE = "value";
/*     */   private static final String TITLE = "title";
/*     */   private static final String DESCRIPTION = "description";
/*     */   private static final String STEP_SIZE = "stepSize";
/*     */   private static final String OBLIGATORY = "obligatory";
/*     */   private static final String READ_ONLY = "readOnly";
/*     */   private static final String DATETIME_AS_LONG = "dateTimeAsLong";
/*     */ 
/*     */   public StrategyParameter()
/*     */   {
/*     */   }
/*     */ 
/*     */   public StrategyParameter(JSONObject jsonObject)
/*     */   {
/*  32 */     super(jsonObject, new String[] { "name", "type", "value", "title", "description", "stepSize", "obligatory", "readOnly", "dateTimeAsLong" });
/*     */   }
/*     */ 
/*     */   public String getTitle() {
/*  36 */     String title = getString("title");
/*  37 */     if ((title != null) && (!title.isEmpty())) {
/*  38 */       return new String(Base64.decode(title), UTF8);
/*     */     }
/*  40 */     return null;
/*     */   }
/*     */ 
/*     */   public void setTitle(String title)
/*     */   {
/*  45 */     if (title != null)
/*  46 */       put("title", Base64.encode(String.valueOf(title).getBytes(UTF8)));
/*     */   }
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  51 */     String description = getString("description");
/*  52 */     if ((description != null) && (!description.isEmpty())) {
/*  53 */       return new String(Base64.decode(description), UTF8);
/*     */     }
/*  55 */     return null;
/*     */   }
/*     */ 
/*     */   public void setDescription(String description)
/*     */   {
/*  60 */     if (description != null)
/*  61 */       put("description", Base64.encode(String.valueOf(description).getBytes(UTF8)));
/*     */   }
/*     */ 
/*     */   public double getStepSize()
/*     */   {
/*  67 */     if (has("stepSize")) {
/*  68 */       return getDouble("stepSize");
/*     */     }
/*  70 */     return 1.0D;
/*     */   }
/*     */ 
/*     */   public void setStepSize(double stepSize)
/*     */   {
/*  75 */     put("stepSize", stepSize);
/*     */   }
/*     */ 
/*     */   public boolean isObligatory() {
/*  79 */     if (has("obligatory")) {
/*  80 */       return getBoolean("obligatory");
/*     */     }
/*  82 */     return false;
/*     */   }
/*     */ 
/*     */   public void setObligatory(boolean obligatory)
/*     */   {
/*  87 */     put("obligatory", obligatory);
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly() {
/*  91 */     if (has("readOnly")) {
/*  92 */       return getBoolean("readOnly");
/*     */     }
/*  94 */     return false;
/*     */   }
/*     */ 
/*     */   public void setReadOnly(boolean readOnly)
/*     */   {
/*  99 */     put("readOnly", readOnly);
/*     */   }
/*     */ 
/*     */   public boolean isDateTimeAsLong()
/*     */   {
/* 104 */     if (has("dateTimeAsLong")) {
/* 105 */       return getBoolean("dateTimeAsLong");
/*     */     }
/* 107 */     return false;
/*     */   }
/*     */ 
/*     */   public void setDateTimeAsLong(boolean asLong)
/*     */   {
/* 112 */     put("dateTimeAsLong", asLong);
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 116 */     return getString("name");
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/* 120 */     if (name == null) {
/* 121 */       throw new NullPointerException("name");
/*     */     }
/* 123 */     put("name", name);
/*     */   }
/*     */ 
/*     */   public String getType() {
/* 127 */     return getString("type");
/*     */   }
/*     */ 
/*     */   public void setType(String type) {
/* 131 */     if (type == null) {
/* 132 */       throw new NullPointerException("type");
/*     */     }
/* 134 */     put("type", type);
/*     */   }
/*     */ 
/*     */   public void setType(Class<?> type) {
/* 138 */     if (type == null) {
/* 139 */       throw new NullPointerException("type");
/*     */     }
/* 141 */     put("type", type.getName());
/*     */   }
/*     */ 
/*     */   public String getValue() {
/* 145 */     String value = getString("value");
/* 146 */     if ((value != null) && (!value.isEmpty())) {
/* 147 */       return new String(Base64.decode(value), UTF8);
/*     */     }
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   public void setValue(Object value)
/*     */   {
/* 154 */     if (value != null)
/* 155 */       put("value", Base64.encode(String.valueOf(value).getBytes(UTF8)));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategyParameter
 * JD-Core Version:    0.6.0
 */