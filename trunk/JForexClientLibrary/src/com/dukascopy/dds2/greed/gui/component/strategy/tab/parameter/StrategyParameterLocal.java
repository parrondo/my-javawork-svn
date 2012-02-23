/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter;
/*     */ 
/*     */ public class StrategyParameterLocal
/*     */ {
/*     */   private String presetId;
/*     */   private String id;
/*     */   private String name;
/*     */   private String description;
/*     */   private boolean mandatory;
/*     */   private boolean readOnly;
/*     */   private double stepSize;
/*     */   private Class<?> type;
/*     */   private Object value;
/*     */   private boolean dateAsLong;
/*     */ 
/*     */   public StrategyParameterLocal(String presetId, String id, String name, String description, boolean mandatory, boolean readOnly, double stepSize, Class<?> type, Object value, boolean dateAsLong)
/*     */   {
/*  22 */     this.presetId = presetId;
/*  23 */     this.id = id;
/*  24 */     this.name = name;
/*  25 */     this.description = description;
/*  26 */     this.type = type;
/*  27 */     this.value = value;
/*  28 */     this.mandatory = mandatory;
/*  29 */     this.readOnly = readOnly;
/*  30 */     this.stepSize = stepSize;
/*  31 */     this.dateAsLong = dateAsLong;
/*     */   }
/*     */ 
/*     */   public String getPresetId() {
/*  35 */     return this.presetId;
/*     */   }
/*     */ 
/*     */   public String getId() {
/*  39 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  43 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/*  47 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public boolean isMandatory() {
/*  51 */     return this.mandatory;
/*     */   }
/*     */ 
/*     */   public void setMandatory(boolean mandatory) {
/*  55 */     this.mandatory = mandatory;
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly()
/*     */   {
/*  62 */     return this.readOnly;
/*     */   }
/*     */ 
/*     */   public void setReadOnly(boolean readOnly)
/*     */   {
/*  69 */     this.readOnly = readOnly;
/*     */   }
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  76 */     return this.description;
/*     */   }
/*     */ 
/*     */   public void setDescription(String description)
/*     */   {
/*  83 */     this.description = description;
/*     */   }
/*     */ 
/*     */   public double getStepSize() {
/*  87 */     return this.stepSize;
/*     */   }
/*     */ 
/*     */   public void setStepSize(double stepSize) {
/*  91 */     this.stepSize = stepSize;
/*     */   }
/*     */ 
/*     */   public Class<?> getType() {
/*  95 */     return this.type;
/*     */   }
/*     */ 
/*     */   public Object getValue() {
/*  99 */     return this.value;
/*     */   }
/*     */ 
/*     */   public void setValue(Object value) {
/* 103 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public boolean isDateAsLong()
/*     */   {
/* 110 */     return this.dateAsLong;
/*     */   }
/*     */ 
/*     */   public void setDateAsLong(boolean dateAsLong)
/*     */   {
/* 117 */     this.dateAsLong = dateAsLong;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 122 */     int prime = 31;
/* 123 */     int result = 1;
/* 124 */     result = 31 * result + (this.id == null ? 0 : this.id.hashCode());
/* 125 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 130 */     if (this == obj)
/* 131 */       return true;
/* 132 */     if (obj == null)
/* 133 */       return false;
/* 134 */     if (getClass() != obj.getClass())
/* 135 */       return false;
/* 136 */     StrategyParameterLocal other = (StrategyParameterLocal)obj;
/* 137 */     if (this.id == null) {
/* 138 */       if (other.id != null)
/* 139 */         return false;
/* 140 */     } else if (!this.id.equals(other.id))
/* 141 */       return false;
/* 142 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterLocal
 * JD-Core Version:    0.6.0
 */