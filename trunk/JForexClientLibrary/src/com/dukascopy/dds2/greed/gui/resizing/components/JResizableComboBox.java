/*    */ package com.dukascopy.dds2.greed.gui.resizing.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.resizing.Resizable;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*    */ import java.awt.Font;
/*    */ import java.util.Vector;
/*    */ import javax.swing.ComboBoxModel;
/*    */ import javax.swing.JComboBox;
/*    */ 
/*    */ public class JResizableComboBox extends JComboBox
/*    */   implements Resizable
/*    */ {
/*    */   private final ResizingManager.ComponentSize deaultSize;
/*    */ 
/*    */   public JResizableComboBox()
/*    */   {
/* 17 */     this(ResizingManager.ComponentSize.SIZE_30X24);
/*    */   }
/*    */ 
/*    */   public JResizableComboBox(Object[] items, ResizingManager.ComponentSize deaultSize) {
/* 21 */     super(items);
/* 22 */     this.deaultSize = (deaultSize == null ? ResizingManager.ComponentSize.SIZE_30X24 : deaultSize);
/* 23 */     apllySize();
/* 24 */     ResizingManager.addResizable(this);
/*    */   }
/*    */ 
/*    */   public JResizableComboBox(ResizingManager.ComponentSize deaultSize)
/*    */   {
/* 29 */     this.deaultSize = (deaultSize == null ? ResizingManager.ComponentSize.SIZE_30X24 : deaultSize);
/* 30 */     apllySize();
/* 31 */     ResizingManager.addResizable(this);
/*    */   }
/*    */ 
/*    */   protected JResizableComboBox(ComboBoxModel comboBoxModel, ResizingManager.ComponentSize deaultSize) {
/* 35 */     super(comboBoxModel);
/* 36 */     this.deaultSize = (deaultSize == null ? ResizingManager.ComponentSize.SIZE_30X24 : deaultSize);
/* 37 */     ResizingManager.addResizable(this);
/*    */   }
/*    */ 
/*    */   protected JResizableComboBox(Vector<?> items, ResizingManager.ComponentSize deaultSize) {
/* 41 */     super(items);
/* 42 */     this.deaultSize = (deaultSize == null ? ResizingManager.ComponentSize.SIZE_30X24 : deaultSize);
/* 43 */     ResizingManager.addResizable(this);
/*    */   }
/*    */ 
/*    */   public Object getDefaultSize()
/*    */   {
/* 48 */     return Integer.valueOf(getFont().getSize());
/*    */   }
/*    */ 
/*    */   public void setSizeMode(Object size)
/*    */   {
/* 53 */     setFont(getFont().deriveFont(((Float)size).floatValue()));
/* 54 */     apllySize();
/*    */   }
/*    */ 
/*    */   private void apllySize() {
/* 58 */     setPreferredSize(this.deaultSize.getSize());
/* 59 */     setMaximumSize(this.deaultSize.getSize());
/* 60 */     setMinimumSize(this.deaultSize.getSize());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.resizing.components.JResizableComboBox
 * JD-Core Version:    0.6.0
 */