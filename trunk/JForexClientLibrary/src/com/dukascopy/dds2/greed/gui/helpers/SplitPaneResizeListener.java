/*    */ package com.dukascopy.dds2.greed.gui.helpers;
/*    */ 
/*    */ import java.beans.PropertyChangeEvent;
/*    */ import java.beans.PropertyChangeListener;
/*    */ import javax.swing.JSplitPane;
/*    */ 
/*    */ public class SplitPaneResizeListener
/*    */   implements PropertyChangeListener
/*    */ {
/*    */   private boolean inSplitPaneResized;
/* 11 */   private double prevLocation = -1.0D;
/*    */   private JSplitPane splitPane;
/*    */   private static final double MIN_BOTTOM_PROPORTION = 0.9300000000000001D;
/*    */   private static final double MIN_TOP_PROPORTION = 0.06D;
/*    */ 
/*    */   public SplitPaneResizeListener(JSplitPane splitPane)
/*    */   {
/* 18 */     this.splitPane = splitPane;
/*    */   }
/*    */ 
/*    */   public void propertyChange(PropertyChangeEvent evt) {
/* 22 */     splitPaneResized(evt);
/*    */   }
/*    */ 
/*    */   private void splitPaneResized(PropertyChangeEvent evt)
/*    */   {
/* 27 */     if (this.inSplitPaneResized) {
/* 28 */       return;
/*    */     }
/* 30 */     this.inSplitPaneResized = true;
/*    */ 
/* 32 */     if (evt.getPropertyName().equalsIgnoreCase("dividerLocation")) {
/* 33 */       int current = this.splitPane.getDividerLocation();
/* 34 */       int max = this.splitPane.getMaximumDividerLocation();
/* 35 */       double min = 0.0D;
/* 36 */       if (current / max > 0.9300000000000001D) {
/* 37 */         if (this.prevLocation < current) {
/* 38 */           this.splitPane.setDividerLocation(max);
/*    */         }
/* 40 */         if (this.prevLocation > current) {
/* 41 */           this.splitPane.setDividerLocation(0.9300000000000001D);
/*    */         }
/*    */       }
/* 44 */       if (current / max < 0.06D) {
/* 45 */         if (this.prevLocation > current) {
/* 46 */           this.splitPane.setDividerLocation(min);
/*    */         }
/* 48 */         if (this.prevLocation < current) {
/* 49 */           this.splitPane.setDividerLocation(0.06D);
/*    */         }
/*    */       }
/* 52 */       this.prevLocation = current;
/*    */     }
/* 54 */     this.inSplitPaneResized = false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.helpers.SplitPaneResizeListener
 * JD-Core Version:    0.6.0
 */