/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Parameter
/*     */ {
/*   7 */   boolean isReference = false;
/*   8 */   boolean isArray = false;
/*   9 */   boolean isInstrument = false;
/*     */   String type;
/*     */   String name;
/*  13 */   List<String> value = new ArrayList();
/*  14 */   List<Integer> dimention = new ArrayList();
/*     */ 
/*     */   public String getType() {
/*  17 */     return this.type;
/*     */   }
/*     */ 
/*     */   public void setType(String type) {
/*  21 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  25 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/*  29 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getValue() {
/*  33 */     if (this.value.size() < 1) {
/*  34 */       return null;
/*     */     }
/*  36 */     return (String)this.value.get(0);
/*     */   }
/*     */ 
/*     */   public void setValue(String value)
/*     */   {
/*  41 */     if (this.value.size() < 1)
/*  42 */       this.value.add(value);
/*     */     else
/*  44 */       this.value.set(0, value);
/*     */   }
/*     */ 
/*     */   public boolean isReference()
/*     */   {
/*  49 */     return this.isReference;
/*     */   }
/*     */ 
/*     */   public void setReference(boolean isReference) {
/*  53 */     this.isReference = isReference;
/*     */   }
/*     */ 
/*     */   public boolean isArray() {
/*  57 */     return this.isArray;
/*     */   }
/*     */ 
/*     */   public void setArray(boolean isArray) {
/*  61 */     this.isArray = isArray;
/*     */   }
/*     */ 
/*     */   public boolean isNumeric() {
/*  65 */     boolean result = (this.type.equals("int")) || (this.type.equals("long")) || (this.type.equals("double"));
/*  66 */     return result;
/*     */   }
/*     */ 
/*     */   public String getTypeToNumeric() {
/*  70 */     StringBuilder result = new StringBuilder();
/*  71 */     if ((isNumeric()) && (!isArray()))
/*  72 */       result.append("Number");
/*     */     else {
/*  74 */       result.append(getType());
/*     */     }
/*  76 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public String getNameToNumeric() {
/*  80 */     StringBuilder result = new StringBuilder();
/*  81 */     result.append(getName());
/*  82 */     if ((isNumeric()) && (!isArray())) {
/*  83 */       if (this.type.equals("int"))
/*  84 */         result.append(".intValue()");
/*  85 */       else if (this.type.equals("long"))
/*  86 */         result.append(".longValue()");
/*  87 */       else if (this.type.equals("double")) {
/*  88 */         result.append(".doubleValue()");
/*     */       }
/*     */     }
/*  91 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public String getValueToNumeric() {
/*  95 */     StringBuilder result = new StringBuilder();
/*  96 */     if ((getValue() != null) && (!getValue().isEmpty())) {
/*  97 */       if ((isNumeric()) && (!isArray())) {
/*  98 */         if (this.type.equals("int"))
/*  99 */           result.append("toInt(");
/* 100 */         else if (this.type.equals("long"))
/* 101 */           result.append("toLong(");
/* 102 */         else if (this.type.equals("double")) {
/* 103 */           result.append("toDouble(");
/*     */         }
/* 105 */         result.append(getValue());
/* 106 */         result.append(")");
/*     */       } else {
/* 108 */         result.append(getValue());
/*     */       }
/*     */     }
/* 111 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public boolean isInstrument() {
/* 115 */     return this.isInstrument;
/*     */   }
/*     */ 
/*     */   public void setInstrument(boolean isInstrument) {
/* 119 */     this.isInstrument = isInstrument;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.Parameter
 * JD-Core Version:    0.6.0
 */