/*     */ package com.dukascopy.dds2.greed.agent.strategy;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*     */ 
/*     */ public class StrategyRunParameter
/*     */ {
/*     */   private final String name;
/*     */   private final Variable variable;
/*     */   private String title;
/*     */   private String description;
/*     */   private boolean mandatory;
/*     */   private boolean readOnly;
/*     */   private double stepSize;
/*     */   private String fileType;
/*     */   private boolean dateAsLong;
/*     */ 
/*     */   public StrategyRunParameter(String name, Class<?> type, Object value)
/*     */   {
/*  26 */     this.name = name;
/*  27 */     this.variable = new Variable(value, type);
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  31 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getTitle() {
/*  35 */     return this.title;
/*     */   }
/*     */ 
/*     */   public void setTitle(String title) {
/*  39 */     this.title = title;
/*     */   }
/*     */ 
/*     */   public Variable getVariable() {
/*  43 */     return this.variable;
/*     */   }
/*     */ 
/*     */   public boolean isMandatory() {
/*  47 */     return this.mandatory;
/*     */   }
/*     */ 
/*     */   public void setMandatory(boolean mandatory) {
/*  51 */     this.mandatory = mandatory;
/*     */   }
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  58 */     return this.description;
/*     */   }
/*     */ 
/*     */   public void setDescription(String description)
/*     */   {
/*  65 */     this.description = description;
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly()
/*     */   {
/*  72 */     return this.readOnly;
/*     */   }
/*     */ 
/*     */   public void setReadOnly(boolean readOnly)
/*     */   {
/*  79 */     this.readOnly = readOnly;
/*     */   }
/*     */ 
/*     */   public double getStepSize() {
/*  83 */     return this.stepSize;
/*     */   }
/*     */ 
/*     */   public void setStepSize(double stepSize) {
/*  87 */     this.stepSize = stepSize;
/*     */   }
/*     */ 
/*     */   public String getFileType() {
/*  91 */     return this.fileType;
/*     */   }
/*     */ 
/*     */   public void setFileType(String fileType) {
/*  95 */     this.fileType = fileType;
/*     */   }
/*     */ 
/*     */   public boolean isDateAsLong() {
/*  99 */     return this.dateAsLong;
/*     */   }
/*     */ 
/*     */   public void setDateAsLong(boolean dateAsLong) {
/* 103 */     this.dateAsLong = dateAsLong;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 108 */     return "StrategyRunParameter[" + "name=" + this.name + "," + "title=" + this.title + "," + "variable=" + this.variable + "," + "mandatory=" + this.mandatory + "," + "stepSize=" + this.stepSize + "," + "fileType=" + this.fileType + "," + "dateAsLong=" + this.dateAsLong + ",";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.StrategyRunParameter
 * JD-Core Version:    0.6.0
 */