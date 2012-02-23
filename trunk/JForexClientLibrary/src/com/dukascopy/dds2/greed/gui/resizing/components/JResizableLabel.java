/*    */ package com.dukascopy.dds2.greed.gui.resizing.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.resizing.Resizable;
/*    */ import java.awt.Font;
/*    */ import javax.swing.JLabel;
/*    */ 
/*    */ public class JResizableLabel extends JLabel
/*    */   implements Resizable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public JResizableLabel()
/*    */   {
/*    */   }
/*    */ 
/*    */   public JResizableLabel(String text)
/*    */   {
/* 27 */     super(text);
/*    */   }
/*    */ 
/*    */   public JResizableLabel(String text, int horizontalAlignment)
/*    */   {
/* 32 */     super(text, horizontalAlignment);
/*    */   }
/*    */ 
/*    */   public Object getDefaultSize()
/*    */   {
/* 38 */     return Integer.valueOf(getFont().getSize());
/*    */   }
/*    */ 
/*    */   public void setSizeMode(Object size)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.resizing.components.JResizableLabel
 * JD-Core Version:    0.6.0
 */