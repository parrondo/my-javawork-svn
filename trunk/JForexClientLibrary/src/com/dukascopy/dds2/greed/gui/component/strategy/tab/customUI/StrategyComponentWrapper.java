/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class StrategyComponentWrapper
/*    */ {
/*  9 */   public static final Dimension MINIMUM_SIZE = new Dimension(100, 20);
/* 10 */   public static final Dimension MAXIMUM_DIZE = new Dimension(150, 20);
/*    */   private JComponent component;
/*    */ 
/*    */   public StrategyComponentWrapper(JComponent component)
/*    */   {
/* 15 */     if (component == null) {
/* 16 */       throw new IllegalArgumentException("Component must not be null");
/*    */     }
/* 18 */     this.component = component;
/*    */ 
/* 20 */     setAllSizes(MAXIMUM_DIZE);
/*    */   }
/*    */ 
/*    */   public JComponent getComponent() {
/* 24 */     return this.component;
/*    */   }
/*    */ 
/*    */   private void setAllSizes(Dimension size) {
/* 28 */     this.component.setPreferredSize(size);
/* 29 */     this.component.setMinimumSize(size);
/* 30 */     this.component.setMaximumSize(size);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategyComponentWrapper
 * JD-Core Version:    0.6.0
 */