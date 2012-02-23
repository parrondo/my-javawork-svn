/*     */ package com.dukascopy.transport.common.pojoSerializer;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ public class TestObject
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 12131231231231L;
/*     */   private transient String key;
/*  12 */   private boolean enabled = false;
/*     */ 
/*  14 */   private Boolean disabled = Boolean.valueOf(false);
/*     */   private String value;
/*     */   private BigDecimal decimalValue;
/*     */   private short shortValue;
/*     */   private long longValue;
/*     */   private int intValue;
/*     */   private char charValue;
/*     */   private byte b;
/*     */   private String[] stringArray;
/*     */ 
/*     */   public String getKey()
/*     */   {
/*  35 */     return this.key;
/*     */   }
/*     */ 
/*     */   public void setKey(String key) {
/*  39 */     this.key = key;
/*     */   }
/*     */ 
/*     */   public String getValue() {
/*  43 */     return this.value;
/*     */   }
/*     */ 
/*     */   public void setValue(String value) {
/*  47 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public Boolean isEnabled()
/*     */   {
/*  53 */     return Boolean.valueOf(this.enabled);
/*     */   }
/*     */ 
/*     */   public void setEnabled(Boolean enabled) {
/*  57 */     this.enabled = enabled.booleanValue();
/*     */   }
/*     */ 
/*     */   public Boolean getDisabled() {
/*  61 */     return this.disabled;
/*     */   }
/*     */ 
/*     */   public void setDisabled(Boolean disabled) {
/*  65 */     this.disabled = disabled;
/*     */   }
/*     */ 
/*     */   public BigDecimal getDecimalValue() {
/*  69 */     return this.decimalValue;
/*     */   }
/*     */ 
/*     */   public void setDecimalValue(BigDecimal decimalValue) {
/*  73 */     this.decimalValue = decimalValue;
/*     */   }
/*     */ 
/*     */   public char getCharValue()
/*     */   {
/*  79 */     return this.charValue;
/*     */   }
/*     */ 
/*     */   public void setCharValue(char charValue) {
/*  83 */     this.charValue = charValue;
/*     */   }
/*     */ 
/*     */   public short getShortValue() {
/*  87 */     return this.shortValue;
/*     */   }
/*     */ 
/*     */   public void setShortValue(short shortValue) {
/*  91 */     this.shortValue = shortValue;
/*     */   }
/*     */ 
/*     */   public long getLongValue() {
/*  95 */     return this.longValue;
/*     */   }
/*     */ 
/*     */   public void setLongValue(long longValue) {
/*  99 */     this.longValue = longValue;
/*     */   }
/*     */ 
/*     */   public byte getB()
/*     */   {
/* 105 */     return this.b;
/*     */   }
/*     */ 
/*     */   public void setB(byte b) {
/* 109 */     this.b = b;
/*     */   }
/*     */ 
/*     */   public String[] getStringArray() {
/* 113 */     return this.stringArray;
/*     */   }
/*     */ 
/*     */   public void setStringArray(String[] stringArray) {
/* 117 */     this.stringArray = stringArray;
/*     */   }
/*     */ 
/*     */   public int getIntValue()
/*     */   {
/* 123 */     return this.intValue;
/*     */   }
/*     */ 
/*     */   public void setIntValue(int intValue) {
/* 127 */     this.intValue = intValue;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.pojoSerializer.TestObject
 * JD-Core Version:    0.6.0
 */