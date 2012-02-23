/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*    */ 
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JFormattedTextField;
/*    */ import javax.swing.JSpinner.DefaultEditor;
/*    */ import javax.swing.border.EmptyBorder;
/*    */ import javax.swing.plaf.basic.BasicSpinnerUI;
/*    */ 
/*    */ public class StrategiesSpinnerUI extends BasicSpinnerUI
/*    */ {
/*    */   protected JComponent createEditor()
/*    */   {
/* 14 */     JComponent editor = super.createEditor();
/* 15 */     JFormattedTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();
/* 16 */     textField.setBorder(new EmptyBorder(CommonUIConstants.DEFAULT_COMPONENT_INSETS));
/*    */ 
/* 18 */     return editor;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesSpinnerUI
 * JD-Core Version:    0.6.0
 */