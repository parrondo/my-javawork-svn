/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*    */ 
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Dimension;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JTextField;
/*    */ 
/*    */ public class StrategiesTextFieldAndButton extends JPanel
/*    */ {
/*    */   private JTextField textField;
/*    */   private StrategiesButton button;
/*    */ 
/*    */   public StrategiesTextFieldAndButton(JTextField textField, StrategiesButton button)
/*    */   {
/* 17 */     setLayout(new BorderLayout());
/*    */ 
/* 19 */     this.textField = textField;
/* 20 */     this.button = button;
/*    */ 
/* 22 */     add(textField, "Center");
/* 23 */     add(button, "East");
/*    */   }
/*    */ 
/*    */   public JTextField getTextField() {
/* 27 */     return this.textField;
/*    */   }
/*    */ 
/*    */   public JButton getButton() {
/* 31 */     return this.button;
/*    */   }
/*    */ 
/*    */   public void setPreferredSize(Dimension preferredSize)
/*    */   {
/* 37 */     super.setPreferredSize(preferredSize);
/*    */ 
/* 39 */     int textFieldWidth = (int)preferredSize.getWidth() - (int)this.button.getPreferredSize().getWidth();
/* 40 */     Dimension textFieldSize = new Dimension(textFieldWidth, (int)preferredSize.getHeight());
/*    */ 
/* 42 */     this.textField.setPreferredSize(textFieldSize);
/* 43 */     this.textField.setMinimumSize(textFieldSize);
/* 44 */     this.textField.setMaximumSize(textFieldSize);
/*    */   }
/*    */ 
/*    */   public Dimension getPreferredSize()
/*    */   {
/* 52 */     return super.getPreferredSize();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesTextFieldAndButton
 * JD-Core Version:    0.6.0
 */