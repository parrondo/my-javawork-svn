/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IIndicatorContext;
/*     */ import com.dukascopy.api.indicators.IMinMax;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.InputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ 
/*     */ public abstract class AbstractTesterIndicator
/*     */   implements IIndicator, IMinMax, Cloneable
/*     */ {
/*     */   private static final String GROUP_NAME = "Historical Tester";
/*     */   private Instrument instrument;
/*     */   private InputParameterInfo[] inputInfo;
/*     */   private OptInputParameterInfo[] optInputInfo;
/*     */   private OutputParameterInfo[] outputInfo;
/*     */   private IndicatorInfo indicatorInfo;
/*     */ 
/*     */   protected void configure()
/*     */   {
/*  40 */     this.inputInfo = createInputParamsInfo();
/*  41 */     this.optInputInfo = createOptionalParamsInfo();
/*  42 */     this.outputInfo = createOutputParamsInfo();
/*  43 */     this.indicatorInfo = new IndicatorInfo(getName(), getTitle(), "Historical Tester", false, false, false, this.inputInfo.length, this.optInputInfo.length, this.outputInfo.length); } 
/*     */   protected abstract String getName();
/*     */ 
/*     */   protected abstract String getTitle();
/*     */ 
/*     */   protected abstract InputParameterInfo[] createInputParamsInfo();
/*     */ 
/*     */   protected abstract OptInputParameterInfo[] createOptionalParamsInfo();
/*     */ 
/*     */   protected abstract OutputParameterInfo[] createOutputParamsInfo();
/*     */ 
/*  57 */   public void setInstrument(Instrument instrument) { this.instrument = instrument; }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/*  61 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public IndicatorInfo getIndicatorInfo()
/*     */   {
/*  66 */     return this.indicatorInfo;
/*     */   }
/*     */ 
/*     */   public InputParameterInfo getInputParameterInfo(int index)
/*     */   {
/*  71 */     return this.inputInfo[index];
/*     */   }
/*     */ 
/*     */   public OptInputParameterInfo getOptInputParameterInfo(int index)
/*     */   {
/*  76 */     return this.optInputInfo[index];
/*     */   }
/*     */ 
/*     */   public OutputParameterInfo getOutputParameterInfo(int index)
/*     */   {
/*  81 */     return this.outputInfo[index];
/*     */   }
/*     */ 
/*     */   public void onStart(IIndicatorContext context)
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getLookback()
/*     */   {
/*  90 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getLookforward()
/*     */   {
/*  95 */     return 0;
/*     */   }
/*     */ 
/*     */   protected double[] getMinMax(double[] values, int from, int to)
/*     */   {
/* 100 */     double minValue = values[from];
/* 101 */     double maxValue = values[from];
/*     */ 
/* 103 */     for (int i = from + 1; i <= to; i++) {
/* 104 */       double value = values[i];
/*     */ 
/* 106 */       minValue = Math.min(minValue, value);
/* 107 */       maxValue = Math.max(maxValue, value);
/*     */     }
/* 109 */     return new double[] { minValue - 1.0D, maxValue + 1.0D };
/*     */   }
/*     */ 
/*     */   public AbstractTesterIndicator clone() {
/*     */     try {
/* 114 */       AbstractTesterIndicator result = (AbstractTesterIndicator)super.clone();
/* 115 */       result.setInstrument(getInstrument());
/* 116 */       return result;
/*     */     } catch (CloneNotSupportedException ex) {
/*     */     }
/* 119 */     throw new RuntimeException("Cannot clone indicator.", ex);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.AbstractTesterIndicator
 * JD-Core Version:    0.6.0
 */