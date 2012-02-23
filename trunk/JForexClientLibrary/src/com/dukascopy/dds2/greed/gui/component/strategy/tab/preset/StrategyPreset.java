/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.preset;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterLocal;
/*     */ import java.util.List;
/*     */ 
/*     */ public class StrategyPreset
/*     */ {
/*     */   private String id;
/*     */   private String name;
/*     */   private List<StrategyParameterLocal> initialParameters;
/*     */   private List<StrategyParameterLocal> strategyParameters;
/*  18 */   private boolean temporalModifiedState = false;
/*     */   private List<StrategyParameterLocal> temporalState;
/*  21 */   private boolean modified = false;
/*     */ 
/*     */   public StrategyPreset(String id, String name, List<StrategyParameterLocal> strategyParameters) {
/*  24 */     this.id = id;
/*  25 */     this.name = name;
/*  26 */     this.initialParameters = strategyParameters;
/*  27 */     this.strategyParameters = strategyParameters;
/*     */ 
/*  29 */     this.temporalState = null;
/*     */   }
/*     */ 
/*     */   public String getId() {
/*  33 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  37 */     if (isModified()) {
/*  38 */       return "*".concat(this.name);
/*     */     }
/*  40 */     return this.name;
/*     */   }
/*     */ 
/*     */   public List<StrategyParameterLocal> getStrategyParameters()
/*     */   {
/*  45 */     return this.strategyParameters;
/*     */   }
/*     */ 
/*     */   public void setStrategyParameters(List<StrategyParameterLocal> strategyParameters) {
/*  49 */     this.strategyParameters = strategyParameters;
/*  50 */     this.modified = true;
/*     */   }
/*     */ 
/*     */   public StrategyParameterLocal getStrategyParameterBy(String parameterId) {
/*  54 */     for (StrategyParameterLocal strategyParameter : this.strategyParameters) {
/*  55 */       if (strategyParameter.getId().equals(parameterId)) {
/*  56 */         return strategyParameter;
/*     */       }
/*     */     }
/*  59 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isModified() {
/*  63 */     return this.modified;
/*     */   }
/*     */ 
/*     */   public void saveCurrentState() {
/*  67 */     this.modified = false;
/*  68 */     this.initialParameters = this.strategyParameters;
/*  69 */     this.temporalState = null;
/*     */   }
/*     */ 
/*     */   public void restorePreset() {
/*  73 */     this.modified = false;
/*  74 */     this.strategyParameters = this.initialParameters;
/*     */   }
/*     */ 
/*     */   public StrategyPreset getInitialState() {
/*  78 */     return new StrategyPreset(this.id, this.name, this.initialParameters);
/*     */   }
/*     */ 
/*     */   public void saveTemporalState() {
/*  82 */     this.temporalModifiedState = isModified();
/*  83 */     this.temporalState = this.strategyParameters;
/*     */   }
/*     */ 
/*     */   public void removeTemporalState() {
/*  87 */     this.temporalState = null;
/*  88 */     this.temporalModifiedState = false;
/*     */   }
/*     */ 
/*     */   public void resetFromTemporalState() {
/*  92 */     if (this.temporalState != null) {
/*  93 */       this.modified = this.temporalModifiedState;
/*  94 */       this.strategyParameters = this.temporalState;
/*  95 */       this.temporalState = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 101 */     int prime = 31;
/* 102 */     int result = 1;
/* 103 */     result = 31 * result + (this.id == null ? 0 : this.id.hashCode());
/* 104 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 109 */     if (this == obj)
/* 110 */       return true;
/* 111 */     if (obj == null)
/* 112 */       return false;
/* 113 */     if (getClass() != obj.getClass())
/* 114 */       return false;
/* 115 */     StrategyPreset other = (StrategyPreset)obj;
/* 116 */     if (this.id == null) {
/* 117 */       if (other.id != null)
/* 118 */         return false;
/* 119 */     } else if (!this.id.equals(other.id))
/* 120 */       return false;
/* 121 */     return true;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 126 */     return getName();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset
 * JD-Core Version:    0.6.0
 */