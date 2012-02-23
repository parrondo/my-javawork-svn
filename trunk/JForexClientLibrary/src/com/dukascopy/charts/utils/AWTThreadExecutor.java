/*    */ package com.dukascopy.charts.utils;
/*    */ 
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ public abstract class AWTThreadExecutor
/*    */ {
/*    */   protected abstract void invoke();
/*    */ 
/*    */   public void execute()
/*    */   {
/* 24 */     if (SwingUtilities.isEventDispatchThread()) {
/* 25 */       invoke();
/*    */     }
/*    */     else
/* 28 */       SwingUtilities.invokeLater(new Runnable()
/*    */       {
/*    */         public void run() {
/* 31 */           AWTThreadExecutor.this.invoke();
/*    */         }
/*    */       });
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.AWTThreadExecutor
 * JD-Core Version:    0.6.0
 */