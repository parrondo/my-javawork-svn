/*    */ package com.dukascopy.dds2.greed.gui.resizing.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.resizing.Resizable;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.JMenuItem;
/*    */ 
/*    */ public class JResizableMenuItem extends JMenuItem
/*    */   implements Resizable
/*    */ {
/*    */   private static final int DEFAULT_SIZE = 12;
/*    */ 
/*    */   public JResizableMenuItem()
/*    */   {
/*    */   }
/*    */ 
/*    */   public JResizableMenuItem(Icon icon)
/*    */   {
/* 29 */     super(icon);
/*    */   }
/*    */ 
/*    */   public JResizableMenuItem(Action action)
/*    */   {
/* 34 */     super(action);
/*    */   }
/*    */ 
/*    */   public Object getDefaultSize()
/*    */   {
/* 40 */     return Integer.valueOf(12);
/*    */   }
/*    */ 
/*    */   public void setSizeMode(Object size)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.resizing.components.JResizableMenuItem
 * JD-Core Version:    0.6.0
 */