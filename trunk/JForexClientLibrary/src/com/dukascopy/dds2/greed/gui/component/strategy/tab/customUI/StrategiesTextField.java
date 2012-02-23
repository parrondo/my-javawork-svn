/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*    */ 
/*    */ import javax.swing.JTextField;
/*    */ import javax.swing.plaf.basic.BasicTextFieldUI;
/*    */ 
/*    */ public class StrategiesTextField extends JTextField
/*    */ {
/*    */   public StrategiesTextField(String text)
/*    */   {
/* 10 */     super(text);
/*    */ 
/* 12 */     setMargin(CommonUIConstants.DEFAULT_COMPONENT_INSETS);
/* 13 */     setUI(new BasicTextFieldUI());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesTextField
 * JD-Core Version:    0.6.0
 */