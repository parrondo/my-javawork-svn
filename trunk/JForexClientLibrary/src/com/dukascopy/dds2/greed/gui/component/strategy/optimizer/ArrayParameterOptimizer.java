/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Component;
/*    */ import java.awt.Window;
/*    */ import javax.swing.JComboBox;
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ public class ArrayParameterOptimizer extends AbstractLookupParameterOptimizer
/*    */ {
/*    */   private Object[] allElements;
/*    */ 
/*    */   public ArrayParameterOptimizer(Object[] elements, Object selected, boolean mandatory, boolean readOnly)
/*    */   {
/* 27 */     super(new JComboBox(elements), selected, mandatory, readOnly);
/* 28 */     if (!mandatory) {
/* 29 */       ((JComboBox)this.mainComponent).insertItemAt("", 0);
/*    */     }
/* 31 */     this.allElements = elements;
/*    */   }
/*    */ 
/*    */   protected void setValue(Component mainComponent, Object value)
/*    */   {
/* 36 */     ((JComboBox)mainComponent).setSelectedItem(value);
/*    */   }
/*    */ 
/*    */   protected Object getValue(Component mainComponent)
/*    */   {
/* 41 */     Object value = ((JComboBox)mainComponent).getSelectedItem();
/* 42 */     if ((value == null) || ("".equals(value))) {
/* 43 */       return null;
/*    */     }
/* 45 */     return value;
/*    */   }
/*    */ 
/*    */   protected void validateValue(Component mainComponent)
/*    */     throws CommitErrorException
/*    */   {
/* 51 */     Object value = ((JComboBox)mainComponent).getSelectedItem();
/* 52 */     if (((value == null) || ("".equals(value))) && 
/* 53 */       (isMandatory()))
/* 54 */       throw new CommitErrorException("optimizer.dialog.error.value.must.be.selected");
/*    */   }
/*    */ 
/*    */   protected String valueToString(Object value)
/*    */   {
/* 61 */     if (value == null) {
/* 62 */       return null;
/*    */     }
/* 64 */     return value.toString();
/*    */   }
/*    */ 
/*    */   protected Object[] showDialog(Component parent, Object[] values)
/*    */   {
/* 71 */     Window window = SwingUtilities.getWindowAncestor(parent);
/*    */ 
/* 73 */     AbstractArrayParameterDialog dialog = new AbstractArrayParameterDialog(window, this.allElements)
/*    */     {
/*    */       protected Object getValueAsString(Object object) {
/* 76 */         return ArrayParameterOptimizer.this.valueToString(object);
/*    */       }
/*    */     };
/* 79 */     dialog.setTitle(LocalizationManager.getText("optimizer.dialog.select.elements.title"));
/* 80 */     dialog.pack();
/* 81 */     dialog.setLocationRelativeTo(parent);
/* 82 */     return dialog.showModal(values);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.ArrayParameterOptimizer
 * JD-Core Version:    0.6.0
 */