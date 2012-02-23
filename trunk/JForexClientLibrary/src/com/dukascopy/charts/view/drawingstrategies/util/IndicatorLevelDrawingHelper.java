/*     */ package com.dukascopy.charts.view.drawingstrategies.util;
/*     */ 
/*     */ import D;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.LevelInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.view.drawingstrategies.IndicatorDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYFormatterUtils;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Shape;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class IndicatorLevelDrawingHelper
/*     */ {
/*     */   private static final int LABEL_Y_OFFSET = -2;
/*     */   private static final int LABEL_X_OFFSET = -5;
/*  36 */   private final DecimalFormat formatter = new DecimalFormat("0.######");
/*     */   private final ChartState chartState;
/*     */ 
/*     */   public IndicatorLevelDrawingHelper(ChartState chartState)
/*     */   {
/*  42 */     this.chartState = chartState;
/*     */   }
/*     */ 
/*     */   public void drawLevelsForSubIndicator(Graphics g, IValueToYMapper valueToYMapper, IndicatorWrapper indicatorWrapper, int width, int height)
/*     */   {
/*  54 */     Graphics2D g2 = (Graphics2D)g.create();
/*     */     try {
/*  56 */       g2.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.DEFAULT));
/*  57 */       for (LevelInfo levelInfo : indicatorWrapper.getLevelInfoList()) {
/*  58 */         int y = valueToYMapper.yv(levelInfo.getValue());
/*  59 */         setGraphics(g2, levelInfo);
/*  60 */         GraphicHelper.drawSegmentLine(g2, 0.0D, y, width, y, width, height);
/*  61 */         drawLabel(g2, width, levelInfo.getValue(), levelInfo.getLabel(), y);
/*     */       }
/*     */     } finally {
/*  64 */       g2.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void drawLevelsForIndicator(Graphics g, List<IndicatorLevelDrawingBean> levelDrawingBeansList, Map<LevelInfo, Shape> indicatorLevelsPath, boolean lastOutput)
/*     */   {
/*  81 */     if ((levelDrawingBeansList.isEmpty()) || (levelDrawingBeansList.isEmpty())) {
/*  82 */       return;
/*     */     }
/*  84 */     Graphics2D g2 = (Graphics2D)g.create();
/*     */     try
/*     */     {
/*  87 */       g2.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.DEFAULT));
/*  88 */       for (IndicatorLevelDrawingBean levelDrawingBean : levelDrawingBeansList)
/*     */       {
/*  90 */         LevelInfo levelInfo = levelDrawingBean.getLevelInfo();
/*  91 */         setGraphics(g2, levelInfo);
/*  92 */         GeneralPath path = levelDrawingBean.getLevelPath();
/*  93 */         if (levelInfo.getDrawingStyle() == OutputParameterInfo.DrawingStyle.DOTS)
/*  94 */           g2.fill(path);
/*     */         else {
/*  96 */           g2.draw(path);
/*     */         }
/*     */ 
/*  99 */         indicatorLevelsPath.put(levelInfo, path);
/* 100 */         if (lastOutput)
/* 101 */           drawLabel(g2, levelDrawingBean.getLastX(), levelInfo.getValue(), levelInfo.getLabel(), levelDrawingBean.getLastY());
/*     */       }
/*     */     }
/*     */     finally {
/* 105 */       g2.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public double[] getAverageFormulaOutput(Object[] formulaOutputs, int[] outputShifts)
/*     */   {
/* 116 */     double[] result = null;
/* 117 */     double[][] outputs = new double[formulaOutputs.length][];
/* 118 */     if ((formulaOutputs != null) && (formulaOutputs.length > 0)) {
/* 119 */       if ((formulaOutputs.length == 1) && ((formulaOutputs[0] instanceof double[]))) {
/* 120 */         return (double[])((double[])(double[])formulaOutputs[0]).clone();
/*     */       }
/* 122 */       for (int i = 0; i < formulaOutputs.length; i++) {
/* 123 */         if ((formulaOutputs[i] instanceof double[])) {
/* 124 */           outputs[i] = ((double[])(double[])formulaOutputs[i]);
/*     */         }
/*     */       }
/*     */ 
/* 128 */       if (outputs[0] != null) {
/* 129 */         result = (double[])outputs[0].clone();
/* 130 */         int initShift = outputShifts[0];
/*     */ 
/* 132 */         for (int i = 0; i < result.length; i++) {
/* 133 */           int outpCount = 1;
/* 134 */           for (int j = 1; j < outputs.length; j++)
/*     */             try {
/* 136 */               result[i] += outputs[j][(i - outputShifts[j] + initShift)];
/* 137 */               outpCount++;
/*     */             }
/*     */             catch (ArrayIndexOutOfBoundsException e)
/*     */             {
/*     */             }
/*     */             catch (NullPointerException e) {
/*     */             }
/* 144 */           result[i] /= outpCount;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 149 */     return result;
/*     */   }
/*     */ 
/*     */   private void setGraphics(Graphics2D g2, LevelInfo levelInfo)
/*     */   {
/* 158 */     g2.setColor(levelInfo.getColor());
/* 159 */     Stroke stroke = IndicatorDrawingStrategy.getStroke(levelInfo.getDrawingStyle(), levelInfo.getLineWidth());
/* 160 */     if (stroke != null) {
/* 161 */       g2.setStroke(stroke);
/*     */     }
/* 163 */     g2.setComposite(AlphaComposite.getInstance(3, levelInfo.getOpacityAlpha()));
/*     */   }
/*     */ 
/*     */   private void drawLabel(Graphics2D g2, int rightX, double value, String label, int labelPriceY)
/*     */   {
/* 181 */     FontMetrics metrics = g2.getFontMetrics();
/* 182 */     String levelLabel = createLevelLabel(label, value);
/* 183 */     int stringLength = (int)metrics.getStringBounds(levelLabel, g2).getWidth();
/* 184 */     g2.drawString(levelLabel, rightX - stringLength + -5, labelPriceY + -2);
/*     */   }
/*     */ 
/*     */   private String createLevelLabel(String label, double value)
/*     */   {
/* 191 */     SubAxisYFormatterUtils.setup(this.formatter, value);
/* 192 */     return label + " " + this.formatter.format(value);
/*     */   }
/*     */ 
/*     */   public class IndicatorLevelDrawingBean
/*     */   {
/*     */     private LevelInfo levelInfo;
/*     */     private GeneralPath levelPath;
/*     */     private int lastX;
/*     */     private int lastY;
/*     */ 
/*     */     public IndicatorLevelDrawingBean(LevelInfo levelInfo)
/*     */     {
/* 211 */       this.levelInfo = levelInfo;
/* 212 */       this.levelPath = new GeneralPath();
/*     */     }
/*     */ 
/*     */     public LevelInfo getLevelInfo()
/*     */     {
/* 218 */       return this.levelInfo;
/*     */     }
/*     */ 
/*     */     public void setLevelInfo(LevelInfo levelInfo)
/*     */     {
/* 224 */       this.levelInfo = levelInfo;
/*     */     }
/*     */ 
/*     */     public GeneralPath getLevelPath()
/*     */     {
/* 230 */       return this.levelPath;
/*     */     }
/*     */ 
/*     */     public void setLevelPath(GeneralPath levelPath)
/*     */     {
/* 236 */       this.levelPath = levelPath;
/*     */     }
/*     */ 
/*     */     public int getLastX()
/*     */     {
/* 242 */       return this.lastX;
/*     */     }
/*     */ 
/*     */     public void setLastX(int lastX)
/*     */     {
/* 248 */       this.lastX = lastX;
/*     */     }
/*     */ 
/*     */     public int getLastY()
/*     */     {
/* 254 */       return this.lastY;
/*     */     }
/*     */ 
/*     */     public void setLastY(int lastY)
/*     */     {
/* 260 */       this.lastY = lastY;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.util.IndicatorLevelDrawingHelper
 * JD-Core Version:    0.6.0
 */