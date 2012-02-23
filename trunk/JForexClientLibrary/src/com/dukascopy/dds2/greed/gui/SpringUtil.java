/*    */ package com.dukascopy.dds2.greed.gui;
/*    */ 
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.Spring;
/*    */ import javax.swing.SpringLayout.Constraints;
/*    */ 
/*    */ public abstract class SpringUtil
/*    */ {
/*    */   public static final void add(JComponent container, JComponent component, int x, int y, int width, int height)
/*    */   {
/* 15 */     container.add(component, new SpringLayout.Constraints(Spring.constant(x), Spring.constant(y), Spring.constant(width), Spring.constant(height)));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.SpringUtil
 * JD-Core Version:    0.6.0
 */