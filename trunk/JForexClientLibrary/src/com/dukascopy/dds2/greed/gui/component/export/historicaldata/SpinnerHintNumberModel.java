/*    */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*    */ 
/*    */ import javax.swing.SpinnerNumberModel;
/*    */ 
/*    */ public class SpinnerHintNumberModel extends SpinnerNumberModel
/*    */ {
/*  7 */   String hintKey = "";
/*    */ 
/*    */   public SpinnerHintNumberModel(int value, int minimum, int maximum, int stepSize, String hintKey) {
/* 10 */     super(value, minimum, maximum, stepSize);
/* 11 */     this.hintKey = hintKey;
/*    */   }
/*    */ 
/*    */   public String getHintKey() {
/* 15 */     return this.hintKey;
/*    */   }
/*    */ 
/*    */   public void setHintKey(String hintKey) {
/* 19 */     this.hintKey = hintKey;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.SpinnerHintNumberModel
 * JD-Core Version:    0.6.0
 */