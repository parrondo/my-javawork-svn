/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*    */ 
/*    */ import javax.swing.ComboBoxEditor;
/*    */ import javax.swing.JTextField;
/*    */ import javax.swing.ListCellRenderer;
/*    */ import javax.swing.border.EmptyBorder;
/*    */ import javax.swing.plaf.basic.BasicComboBoxEditor;
/*    */ import javax.swing.plaf.basic.BasicComboBoxRenderer;
/*    */ import javax.swing.plaf.basic.BasicComboBoxUI;
/*    */ 
/*    */ public class StrategiesComboBoxUI extends BasicComboBoxUI
/*    */ {
/*    */   protected ListCellRenderer createRenderer()
/*    */   {
/* 15 */     BasicComboBoxRenderer renderer = (BasicComboBoxRenderer)super.createRenderer();
/* 16 */     renderer.setBorder(new EmptyBorder(CommonUIConstants.DEFAULT_COMPONENT_INSETS));
/* 17 */     return renderer;
/*    */   }
/*    */ 
/*    */   protected ComboBoxEditor createEditor()
/*    */   {
/* 22 */     BasicComboBoxEditor editor = (BasicComboBoxEditor)super.createEditor();
/* 23 */     JTextField textField = (JTextField)editor.getEditorComponent();
/* 24 */     textField.setBorder(new EmptyBorder(CommonUIConstants.DEFAULT_COMPONENT_INSETS));
/* 25 */     return editor;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesComboBoxUI
 * JD-Core Version:    0.6.0
 */