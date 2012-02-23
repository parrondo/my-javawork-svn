/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import com.dukascopy.api.IChart.Type;
/*    */ import com.dukascopy.api.IChartObject.ATTR_COLOR;
/*    */ import com.dukascopy.api.IChartObject.ATTR_DOUBLE;
/*    */ import com.dukascopy.api.drawings.IFillableChartObject;
/*    */ import java.awt.Color;
/*    */ 
/*    */ public abstract class AbstractFillableChartObject extends AbstractStickablePointsChartObject
/*    */   implements IFillableChartObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private Float fillAlpha;
/*    */   private Color fillColor;
/*    */ 
/*    */   public AbstractFillableChartObject(String key, IChart.Type type)
/*    */   {
/* 19 */     super(key, type);
/*    */ 
/* 21 */     this.fillColor = this.color;
/*    */   }
/*    */ 
/*    */   public AbstractFillableChartObject(AbstractFillableChartObject chartObject) {
/* 25 */     super(chartObject);
/*    */ 
/* 27 */     this.fillAlpha = chartObject.fillAlpha;
/* 28 */     this.fillColor = chartObject.fillColor;
/*    */   }
/*    */ 
/*    */   public Color getFillColor() {
/* 32 */     if (this.fillColor == null) {
/* 33 */       this.fillColor = this.color;
/*    */     }
/* 35 */     return this.fillColor;
/*    */   }
/*    */ 
/*    */   public void setFillColor(Color fillColor) {
/* 39 */     Color old = getFillColor();
/* 40 */     this.fillColor = fillColor;
/* 41 */     firePropertyChange("fill.color", old, fillColor);
/*    */   }
/*    */ 
/*    */   public float getFillOpacity() {
/* 45 */     if (this.fillAlpha == null) {
/* 46 */       this.fillAlpha = new Float(0.1F);
/*    */     }
/* 48 */     return this.fillAlpha.floatValue();
/*    */   }
/*    */ 
/*    */   public void setFillOpacity(float fillAlpha) {
/* 52 */     Float old = new Float(getFillOpacity());
/* 53 */     this.fillAlpha = new Float(fillAlpha);
/* 54 */     firePropertyChange("fill.alpha", old, new Float(fillAlpha));
/*    */   }
/*    */ 
/*    */   public void setAttrColor(IChartObject.ATTR_COLOR field, Color value)
/*    */   {
/* 59 */     if (IChartObject.ATTR_COLOR.FILLCOLOR == field)
/* 60 */       setFillColor(value);
/*    */     else
/* 62 */       super.setAttrColor(field, value);
/*    */   }
/*    */ 
/*    */   public Color getAttrColor(IChartObject.ATTR_COLOR field)
/*    */   {
/* 68 */     if (IChartObject.ATTR_COLOR.FILLCOLOR == field) {
/* 69 */       return getFillColor();
/*    */     }
/* 71 */     return super.getAttrColor(field);
/*    */   }
/*    */ 
/*    */   public void setAttrDouble(IChartObject.ATTR_DOUBLE field, double value)
/*    */   {
/* 77 */     if (IChartObject.ATTR_DOUBLE.FILL_OPACITY == field)
/* 78 */       setFillOpacity((float)value);
/*    */     else
/* 80 */       super.setAttrDouble(field, value);
/*    */   }
/*    */ 
/*    */   public double getAttrDouble(IChartObject.ATTR_DOUBLE field)
/*    */   {
/* 86 */     if (IChartObject.ATTR_DOUBLE.FILL_OPACITY == field) {
/* 87 */       return getFillOpacity();
/*    */     }
/* 89 */     return super.getAttrDouble(field);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.AbstractFillableChartObject
 * JD-Core Version:    0.6.0
 */