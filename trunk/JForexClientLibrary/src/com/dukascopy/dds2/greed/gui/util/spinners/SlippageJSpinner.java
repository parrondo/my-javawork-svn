/*    */ package com.dukascopy.dds2.greed.gui.util.spinners;
/*    */ 
/*    */ public class SlippageJSpinner extends CommonJSpinner
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public SlippageJSpinner(double value, double maximum, double stepSize, int precision, boolean round05Pip)
/*    */   {
/*  9 */     super(value, 0.0D, maximum, stepSize, precision, round05Pip, false);
/*    */   }
/*    */ 
/*    */   public SlippageJSpinner(double value, double minimum, double maximum, double stepSize, int precision, boolean round05Pip)
/*    */   {
/* 14 */     super(value, minimum, maximum, stepSize, precision, round05Pip, false);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.spinners.SlippageJSpinner
 * JD-Core Version:    0.6.0
 */