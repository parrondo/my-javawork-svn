/*    */ package com.dukascopy.dds2.greed.gui.component.filechooser;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.GridBagConstraints;
/*    */ import java.awt.GridBagLayout;
/*    */ import java.awt.Insets;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JToggleButton;
/*    */ 
/*    */ public class JButtonBar extends JPanel
/*    */ {
/*    */   private static final long serialVersionUID = 4400057910551420252L;
/* 17 */   private int buttonsCount = 0;
/*    */ 
/*    */   public JButtonBar()
/*    */   {
/* 21 */     setLayout(new GridBagLayout());
/*    */   }
/*    */ 
/*    */   public void addButton(JToggleButton b)
/*    */   {
/* 27 */     b.setVerticalTextPosition(3);
/* 28 */     b.setHorizontalTextPosition(0);
/* 29 */     b.setBackground(Color.white);
/*    */ 
/* 31 */     add(b, new GridBagConstraints(0, this.buttonsCount++, 1, 1, 0.0D, 1.0D, 11, 1, new Insets(1, 1, 1, 1), 0, 0));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.filechooser.JButtonBar
 * JD-Core Version:    0.6.0
 */