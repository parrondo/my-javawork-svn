/*     */ package com.dukascopy.charts.main.nulls;
/*     */ 
/*     */ import com.dukascopy.api.ChartObjectListener;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.IChartObject.ATTR_BOOLEAN;
/*     */ import com.dukascopy.api.IChartObject.ATTR_COLOR;
/*     */ import com.dukascopy.api.IChartObject.ATTR_DOUBLE;
/*     */ import com.dukascopy.api.IChartObject.ATTR_INT;
/*     */ import com.dukascopy.api.IChartObject.ATTR_LONG;
/*     */ import com.dukascopy.api.IChartObject.ATTR_TEXT;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Stroke;
/*     */ import java.beans.PropertyChangeListener;
/*     */ 
/*     */ class NullIChartObject
/*     */   implements IChartObject
/*     */ {
/*     */   public void setChartObjectListener(ChartObjectListener listener)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setAttrLong(IChartObject.ATTR_LONG field, long value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public long getAttrLong(IChartObject.ATTR_LONG field)
/*     */   {
/*  21 */     return -1L;
/*     */   }
/*     */ 
/*     */   public void setAttrDouble(IChartObject.ATTR_DOUBLE field, double value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public double getAttrDouble(IChartObject.ATTR_DOUBLE field) {
/*  29 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public void setAttrInt(IChartObject.ATTR_INT field, int value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getAttrInt(IChartObject.ATTR_INT field) {
/*  37 */     return 0;
/*     */   }
/*     */ 
/*     */   public void setAttrBoolean(IChartObject.ATTR_BOOLEAN field, boolean value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean getAttrBoolean(IChartObject.ATTR_BOOLEAN field) {
/*  45 */     return false;
/*     */   }
/*     */ 
/*     */   public void setAttrColor(IChartObject.ATTR_COLOR field, Color value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Color getAttrColor(IChartObject.ATTR_COLOR field) {
/*  53 */     return null;
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setText(String text, Font font)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Color getColor() {
/*  69 */     return null;
/*     */   }
/*     */ 
/*     */   public void setColor(Color color)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Stroke getStroke() {
/*  77 */     return null;
/*     */   }
/*     */ 
/*     */   public void setStroke(Stroke stroke)
/*     */   {
/*     */   }
/*     */ 
/*     */   public IChart.Type getType() {
/*  85 */     return null;
/*     */   }
/*     */ 
/*     */   public String getKey() {
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */   public long getTime(int pointIndex) {
/*  93 */     return -1L;
/*     */   }
/*     */ 
/*     */   public double getPrice(int pointIndex) {
/*  97 */     return -1.0D;
/*     */   }
/*     */ 
/*     */   public int getPointsCount() {
/* 101 */     return -1;
/*     */   }
/*     */ 
/*     */   public void setSticky(boolean sticky)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isSticky()
/*     */   {
/* 110 */     return true;
/*     */   }
/*     */ 
/*     */   public void setText(String text, int horizontalAlignment)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setText(String text, Font font, int horizontalAlignment)
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   public String getAttrText(IChartObject.ATTR_TEXT field)
/*     */   {
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAttrText(IChartObject.ATTR_TEXT field, String value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isMenuEnabled()
/*     */   {
/* 144 */     return false;
/*     */   }
/*     */ 
/*     */   public void setMenuEnabled(boolean menuEnabled)
/*     */   {
/*     */   }
/*     */ 
/*     */   public float getOpacity()
/*     */   {
/* 156 */     return 0.0F;
/*     */   }
/*     */ 
/*     */   public void setOpacity(float alpha)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isUnderEdit()
/*     */   {
/* 166 */     return false;
/*     */   }
/*     */ 
/*     */   public void setUnderEdit(boolean isUnderEdit)
/*     */   {
/*     */   }
/*     */ 
/*     */   public synchronized void addPropertyChangeListener(PropertyChangeListener listener)
/*     */   {
/*     */   }
/*     */ 
/*     */   public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
/*     */   {
/*     */   }
/*     */ 
/*     */   public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
/* 182 */     return null;
/*     */   }
/*     */ 
/*     */   public void setLineStyle(int lineStyle)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setLineWidth(float width)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 198 */     return false;
/*     */   }
/*     */ 
/*     */   public void setTooltip(String tooltip)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.main.nulls.NullIChartObject
 * JD-Core Version:    0.6.0
 */