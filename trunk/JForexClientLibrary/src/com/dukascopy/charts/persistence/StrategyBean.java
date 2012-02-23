/*     */ package com.dukascopy.charts.persistence;
/*     */ 
/*     */ import java.io.File;
/*     */ 
/*     */ public class StrategyBean extends ServiceBean
/*     */ {
/*     */   private static final String LOCAL_STRATEGY_TYPE = "_LOCAL";
/*     */   private final String strategyType;
/*     */   private final Long remoteStrategyId;
/*     */   private final String remoteStrategyName;
/*     */   private final boolean parametersDefined;
/*     */   private String remoteProcessId;
/*     */   private boolean remoteRunAllowed;
/*     */ 
/*     */   public StrategyBean(Integer id, String sourceFullFileName, String binaryFullFileName)
/*     */   {
/*  17 */     this(id, sourceFullFileName, binaryFullFileName, null, null, null, false);
/*     */   }
/*     */ 
/*     */   public StrategyBean(int id, File sourceFile, File binaryFile) {
/*  21 */     this(id, sourceFile, binaryFile, null, null, null, false);
/*     */   }
/*     */ 
/*     */   public StrategyBean(Integer id, String sourceFullFileName, String binaryFullFileName, String strategyType, Long remoteStrategyId, String remoteStrategyName, boolean parametersDefined) {
/*  25 */     super(id, sourceFullFileName, binaryFullFileName);
/*  26 */     if (strategyType == null)
/*  27 */       this.strategyType = "_LOCAL";
/*     */     else {
/*  29 */       this.strategyType = strategyType;
/*     */     }
/*  31 */     this.remoteStrategyId = remoteStrategyId;
/*  32 */     this.remoteStrategyName = remoteStrategyName;
/*  33 */     this.parametersDefined = parametersDefined;
/*     */   }
/*     */ 
/*     */   public StrategyBean(int id, File sourceFile, File binaryFile, String strategyType, Long remoteStrategyId, String remoteStrategyName, boolean parametersDefined) {
/*  37 */     super(id, sourceFile, binaryFile);
/*  38 */     if (strategyType == null)
/*  39 */       this.strategyType = "_LOCAL";
/*     */     else {
/*  41 */       this.strategyType = strategyType;
/*     */     }
/*  43 */     this.remoteStrategyId = remoteStrategyId;
/*  44 */     this.remoteStrategyName = remoteStrategyName;
/*  45 */     this.parametersDefined = parametersDefined;
/*     */   }
/*     */ 
/*     */   public boolean isRemote()
/*     */   {
/*  54 */     return (this.strategyType != null) && (!"_LOCAL".equals(this.strategyType));
/*     */   }
/*     */ 
/*     */   public Long getRemoteStrategyId()
/*     */   {
/*  62 */     return this.remoteStrategyId;
/*     */   }
/*     */ 
/*     */   public String getRemoteStrategyType()
/*     */   {
/*  70 */     return this.strategyType != null ? this.strategyType : "_LOCAL";
/*     */   }
/*     */ 
/*     */   public String getRemoteStrategyName()
/*     */   {
/*  78 */     return this.remoteStrategyName;
/*     */   }
/*     */ 
/*     */   public boolean areParametersDefined()
/*     */   {
/*  86 */     return this.parametersDefined;
/*     */   }
/*     */ 
/*     */   public String getRemoteProcessId() {
/*  90 */     return this.remoteProcessId;
/*     */   }
/*     */ 
/*     */   public void setRemoteProcessId(String pid) {
/*  94 */     this.remoteProcessId = pid;
/*     */   }
/*     */ 
/*     */   public boolean isRemoteRunAllowed() {
/*  98 */     return this.remoteRunAllowed;
/*     */   }
/*     */ 
/*     */   public void setRemoteRunAllowed(boolean allowed) {
/* 102 */     this.remoteRunAllowed = allowed;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.StrategyBean
 * JD-Core Version:    0.6.0
 */