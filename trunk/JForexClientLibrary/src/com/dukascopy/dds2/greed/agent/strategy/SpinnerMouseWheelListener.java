/*    */ package com.dukascopy.dds2.greed.agent.strategy;
/*    */ 
/*    */ import java.awt.event.MouseWheelEvent;
/*    */ import java.awt.event.MouseWheelListener;
/*    */ import javax.swing.JSpinner;
/*    */ 
/*    */ public class SpinnerMouseWheelListener
/*    */   implements MouseWheelListener
/*    */ {
/*    */   private final JSpinner source;
/*    */ 
/*    */   public SpinnerMouseWheelListener(JSpinner source)
/*    */   {
/* 16 */     this.source = source;
/*    */   }
/*    */ 
/*    */   public void mouseWheelMoved(MouseWheelEvent e) {
/* 20 */     if (this.source.isEnabled()) {
/* 21 */       int steps = e.getWheelRotation();
/*    */ 
/* 23 */       for (int i = 0; i < Math.abs(steps); i++)
/*    */       {
/*    */         Object value;
/*    */         Object value;
/* 25 */         if (steps > 0)
/* 26 */           value = this.source.getNextValue();
/*    */         else {
/* 28 */           value = this.source.getPreviousValue();
/*    */         }
/* 30 */         if (value != null)
/* 31 */           this.source.setValue(value);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.SpinnerMouseWheelListener
 * JD-Core Version:    0.6.0
 */