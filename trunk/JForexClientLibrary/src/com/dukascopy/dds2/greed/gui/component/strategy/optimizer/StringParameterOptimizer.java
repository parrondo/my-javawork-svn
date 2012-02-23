/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import javax.swing.JTextField;
/*    */ 
/*    */ public class StringParameterOptimizer extends AbstractLookupParameterOptimizer
/*    */ {
/*    */   public StringParameterOptimizer(String value, boolean mandatory, boolean readOnly)
/*    */   {
/* 20 */     super(new JTextField(), value, mandatory, readOnly);
/* 21 */     ((JTextField)this.mainComponent).setColumns(15);
/*    */   }
/*    */ 
/*    */   protected void setValue(Component mainComponent, Object value)
/*    */   {
/* 26 */     ((JTextField)mainComponent).setText(valueToString(value));
/*    */   }
/*    */ 
/*    */   protected String valueToString(Object value)
/*    */   {
/* 31 */     return value == null ? "" : value.toString();
/*    */   }
/*    */ 
/*    */   protected Object getValue(Component mainComponent)
/*    */   {
/* 36 */     String text = ((JTextField)mainComponent).getText();
/* 37 */     if (text.length() < 1) {
/* 38 */       return null;
/*    */     }
/* 40 */     return text;
/*    */   }
/*    */ 
/*    */   protected void validateValue(Component mainComponent)
/*    */     throws CommitErrorException
/*    */   {
/* 46 */     String text = ((JTextField)mainComponent).getText();
/* 47 */     if ((text.length() < 1) && (isMandatory()))
/* 48 */       throw new CommitErrorException("optimizer.dialog.error.text.value.must.be.entered");
/*    */   }
/*    */ 
/*    */   protected Object[] showDialog(Component parent, Object[] values)
/*    */   {
/* 55 */     AbstractParameterOptimizerDialog dialog = new AbstractParameterOptimizerDialog(parent, "optimizer.dialog.select.elements.title", new JTextField(15))
/*    */     {
/*    */       protected String getValue(JTextField editor)
/*    */       {
/* 59 */         String text = editor.getText().trim();
/* 60 */         if (text.length() < 1) {
/* 61 */           return null;
/*    */         }
/* 63 */         return text;
/*    */       }
/*    */ 
/*    */       String getValueAsString(String value)
/*    */       {
/* 69 */         return value;
/*    */       }
/*    */     };
/*    */     Object[] result;
/*    */     Object[] result;
/* 74 */     if (values == null) {
/* 75 */       result = dialog.showModal(null);
/*    */     } else {
/* 77 */       String[] elements = new String[values.length];
/* 78 */       System.arraycopy(values, 0, elements, 0, values.length);
/* 79 */       result = dialog.showModal(elements);
/*    */     }
/* 81 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.StringParameterOptimizer
 * JD-Core Version:    0.6.0
 */