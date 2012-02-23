/*    */ package com.dukascopy.dds2.greed.gui.util.spinners;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ 
/*    */ public class TrailingStepJSpinner extends CommonJSpinner
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int MIN_TRAILING_STEP;
/*    */   private static final int MAX_TRAILING_STEP = 10000;
/*    */ 
/*    */   public TrailingStepJSpinner(double value)
/*    */   {
/* 21 */     super(value, MIN_TRAILING_STEP, 10000.0D, 1.0D, 0, false, false);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 13 */     if ((!GreedContext.isDemo()) && (!GreedContext.isLive()))
/* 14 */       MIN_TRAILING_STEP = 4;
/*    */     else
/* 16 */       MIN_TRAILING_STEP = 10;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.spinners.TrailingStepJSpinner
 * JD-Core Version:    0.6.0
 */