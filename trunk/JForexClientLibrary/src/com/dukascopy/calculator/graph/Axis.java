/*    */ package com.dukascopy.calculator.graph;
/*    */ 
/*    */ import java.text.DecimalFormat;
/*    */ import java.text.NumberFormat;
/*    */ 
/*    */ public abstract class Axis extends Element
/*    */ {
/*    */   protected boolean showMajorUnit;
/*    */   protected boolean showMinorUnit;
/*    */   protected int majorUnitTick;
/*    */   protected int minorUnitTick;
/*    */   NumberFormat numberFormat;
/*    */ 
/*    */   public Axis()
/*    */   {
/* 11 */     this.numberFormat = NumberFormat.getNumberInstance();
/* 12 */     if ((this.numberFormat instanceof DecimalFormat)) {
/* 13 */       DecimalFormat df = (DecimalFormat)this.numberFormat;
/* 14 */       df.setNegativePrefix("−");
/*    */     }
/* 16 */     this.majorUnitTick = 5;
/* 17 */     this.minorUnitTick = 2;
/* 18 */     setShowMajorUnit(true);
/* 19 */     setShowMinorUnit(true);
/*    */   }
/*    */ 
/*    */   public void setShowMajorUnit(boolean value)
/*    */   {
/* 28 */     this.showMajorUnit = value;
/*    */   }
/*    */ 
/*    */   public void setShowMinorUnit(boolean value)
/*    */   {
/* 36 */     this.showMinorUnit = value;
/*    */   }
/*    */ 
/*    */   public boolean getShowMajorUnit()
/*    */   {
/* 43 */     return this.showMajorUnit;
/*    */   }
/*    */ 
/*    */   public boolean getShowMinorUnit()
/*    */   {
/* 50 */     return this.showMinorUnit;
/*    */   }
/*    */ 
/*    */   public int getMajorUnitTickLength()
/*    */   {
/* 58 */     return this.majorUnitTick;
/*    */   }
/*    */ 
/*    */   public int getMinorUnitTickLength()
/*    */   {
/* 65 */     return this.minorUnitTick;
/*    */   }
/*    */ 
/*    */   public String convertDouble(double d)
/*    */   {
/* 71 */     return this.numberFormat.format(d);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.Axis
 * JD-Core Version:    0.6.0
 */