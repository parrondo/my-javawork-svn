/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import javax.swing.JLabel;
/*    */ 
/*    */ public class StrategyLabel extends JLabel
/*    */ {
/* 10 */   public static final Dimension LABEL_SIZE = new Dimension(120, 20);
/*    */ 
/*    */   public StrategyLabel(String name) {
/* 13 */     super(name);
/*    */ 
/* 15 */     setHorizontalAlignment(4);
/* 16 */     setAllSizes(LABEL_SIZE);
/*    */   }
/*    */ 
/*    */   public void setAllSizes(Dimension size) {
/* 20 */     setPreferredSize(size);
/* 21 */     setMinimumSize(size);
/* 22 */     setMaximumSize(size);
/* 23 */     setSize(size);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategyLabel
 * JD-Core Version:    0.6.0
 */