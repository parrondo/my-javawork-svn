/*    */ package com.dukascopy.charts.view.drawingstrategies.util;
/*    */ 
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import java.awt.BasicStroke;
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Stroke;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ResistanceLevelDrawingHelper
/*    */ {
/* 15 */   static final Stroke DASHED_STROKE = new BasicStroke(0.2F, 0, 2, 0.0F, new float[] { 2.0F }, 0.0F);
/* 16 */   static final Color LINE_COLOR = Color.GRAY;
/*    */ 
/*    */   public void drawLevelsForIndicator(Graphics g, IValueToYMapper valueToYMapper, String indicatorName, int width, int height) {
/* 19 */     Graphics2D g2 = (Graphics2D)g;
/* 20 */     Stroke prevStroke = g2.getStroke();
/* 21 */     Color prevColor = g2.getColor();
/*    */ 
/* 23 */     g2.setColor(LINE_COLOR);
/* 24 */     g2.setStroke(DASHED_STROKE);
/*    */ 
/* 26 */     List levels = getLevels(indicatorName);
/*    */ 
/* 28 */     if (levels != null) {
/* 29 */       for (Integer level : levels) {
/* 30 */         int y = valueToYMapper.yv(level.intValue());
/* 31 */         g2.drawLine(0, y, width, y);
/*    */       }
/*    */     }
/*    */ 
/* 35 */     g2.setColor(prevColor);
/* 36 */     g2.setStroke(prevStroke);
/*    */   }
/*    */ 
/*    */   public static List<Integer> getLevels(String indicatorName)
/*    */   {
/* 43 */     List levels = new ArrayList();
/*    */ 
/* 45 */     if (indicatorName.equalsIgnoreCase("RSI")) {
/* 46 */       levels.add(Integer.valueOf(30));
/* 47 */       levels.add(Integer.valueOf(70));
/* 48 */     } else if (indicatorName.equalsIgnoreCase("STOCH")) {
/* 49 */       levels.add(Integer.valueOf(20));
/* 50 */       levels.add(Integer.valueOf(80));
/* 51 */     } else if (indicatorName.equalsIgnoreCase("RVI")) {
/* 52 */       levels.add(Integer.valueOf(0));
/* 53 */     } else if (indicatorName.equalsIgnoreCase("CCI")) {
/* 54 */       levels.add(Integer.valueOf(100));
/* 55 */       levels.add(Integer.valueOf(0));
/* 56 */       levels.add(Integer.valueOf(-100));
/* 57 */     } else if (indicatorName.equalsIgnoreCase("WILLR")) {
/* 58 */       levels.add(Integer.valueOf(-20));
/* 59 */       levels.add(Integer.valueOf(-80));
/* 60 */     } else if (indicatorName.equalsIgnoreCase("MFI")) {
/* 61 */       levels.add(Integer.valueOf(80));
/* 62 */       levels.add(Integer.valueOf(20));
/* 63 */     } else if (indicatorName.equalsIgnoreCase("OBV")) {
/* 64 */       levels.add(Integer.valueOf(0));
/*    */     } else {
/* 66 */       return null;
/*    */     }
/*    */ 
/* 69 */     return levels;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.util.ResistanceLevelDrawingHelper
 * JD-Core Version:    0.6.0
 */