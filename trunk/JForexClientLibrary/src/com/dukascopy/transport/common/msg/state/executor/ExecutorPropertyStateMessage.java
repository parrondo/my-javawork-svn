/*     */ package com.dukascopy.transport.common.msg.state.executor;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.ExecutorProperty;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.state.StatePropertyMessage;
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ public class ExecutorPropertyStateMessage extends StatePropertyMessage
/*     */ {
/*     */   public static final String TYPE = "state_exec";
/*     */   private static final String PROPERTY = "p";
/*     */   private static final String INSTRUMENT = "i";
/*     */   private static final String VALUE = "a";
/*     */ 
/*     */   public ExecutorPropertyStateMessage()
/*     */   {
/*  24 */     setType("state_exec");
/*     */   }
/*     */ 
/*     */   public ExecutorPropertyStateMessage(ProtocolMessage msg) {
/*  28 */     super(msg);
/*  29 */     setType("state_exec");
/*  30 */     put("p", ExecutorProperty.fromString(msg.getString("p")));
/*  31 */     put("i", msg.getString("i"));
/*  32 */     put("a", msg.getString("a"));
/*     */   }
/*     */ 
/*     */   public ExecutorPropertyStateMessage(ExecutorProperty property) {
/*  36 */     this();
/*  37 */     setProperty(property);
/*     */   }
/*     */ 
/*     */   public ExecutorPropertyStateMessage(ExecutorProperty property, String value) {
/*  41 */     this();
/*  42 */     setProperty(property);
/*  43 */     setValue(value);
/*     */   }
/*     */ 
/*     */   public ExecutorPropertyStateMessage(ExecutorProperty property, boolean value) {
/*  47 */     this();
/*  48 */     setProperty(property);
/*  49 */     setValue(value);
/*     */   }
/*     */ 
/*     */   public ExecutorPropertyStateMessage(ExecutorProperty property, BigDecimal value) {
/*  53 */     this();
/*  54 */     setProperty(property);
/*  55 */     setValue(value);
/*     */   }
/*     */ 
/*     */   public ExecutorPropertyStateMessage(ExecutorProperty property, String instrument, String value) {
/*  59 */     this();
/*  60 */     setProperty(property);
/*  61 */     setInstrument(instrument);
/*  62 */     setValue(value);
/*     */   }
/*     */ 
/*     */   public ExecutorPropertyStateMessage(ExecutorProperty property, String instrument, boolean value) {
/*  66 */     this();
/*  67 */     setProperty(property);
/*  68 */     setInstrument(instrument);
/*  69 */     setValue(value);
/*     */   }
/*     */ 
/*     */   public ExecutorPropertyStateMessage(ExecutorProperty property, String instrument, BigDecimal value) {
/*  73 */     this();
/*  74 */     setProperty(property);
/*  75 */     setInstrument(instrument);
/*  76 */     setValue(value.toString());
/*     */   }
/*     */ 
/*     */   public void setProperty(ExecutorProperty property)
/*     */   {
/*  84 */     put("p", property);
/*     */   }
/*     */ 
/*     */   public ExecutorProperty getProperty()
/*     */   {
/*  91 */     return ExecutorProperty.fromString(getString("p"));
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument)
/*     */   {
/*  99 */     put("i", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/* 107 */     return getString("i");
/*     */   }
/*     */ 
/*     */   public String getStringValue()
/*     */   {
/* 115 */     return getString("a");
/*     */   }
/*     */ 
/*     */   public BigDecimal getBigDecimalValue()
/*     */   {
/* 123 */     return getBigDecimal("a");
/*     */   }
/*     */ 
/*     */   public boolean getBooleanValue()
/*     */   {
/* 131 */     return getBool("a").booleanValue();
/*     */   }
/*     */ 
/*     */   public void setValue(String value)
/*     */   {
/* 139 */     put("a", value);
/*     */   }
/*     */ 
/*     */   public void setValue(boolean value)
/*     */   {
/* 147 */     put("a", value);
/*     */   }
/*     */ 
/*     */   public void setValue(BigDecimal value)
/*     */   {
/* 155 */     put("a", value.toString());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.state.executor.ExecutorPropertyStateMessage
 * JD-Core Version:    0.6.0
 */