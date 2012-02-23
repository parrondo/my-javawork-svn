/*    */ package com.dukascopy.dds2.greed.gui.component.alerter;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import java.awt.Component;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.math.BigDecimal;
/*    */ import java.math.RoundingMode;
/*    */ import java.util.EventObject;
/*    */ import java.util.List;
/*    */ import javax.swing.AbstractCellEditor;
/*    */ import javax.swing.JSpinner;
/*    */ import javax.swing.JSpinner.NumberEditor;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.SpinnerNumberModel;
/*    */ import javax.swing.table.TableCellEditor;
/*    */ 
/*    */ public class SpinnerEditor extends AbstractCellEditor
/*    */   implements TableCellEditor
/*    */ {
/*    */   private JSpinner spinner4;
/*    */   private JSpinner spinner2;
/*    */   private boolean mode2;
/*    */ 
/*    */   public SpinnerEditor()
/*    */   {
/* 27 */     this.spinner4 = new JSpinner(new SpinnerNumberModel(0.0001D, 5.E-005D, 99.999949999999998D, 5.E-005D));
/* 28 */     this.spinner4.setEditor(new JSpinner.NumberEditor(this.spinner4, "#0.00000"));
/* 29 */     this.spinner2 = new JSpinner(new SpinnerNumberModel(0.01D, 0.005D, 9999.9950000000008D, 0.005D));
/* 30 */     this.spinner2.setEditor(new JSpinner.NumberEditor(this.spinner2, "###0.000"));
/*    */   }
/*    */ 
/*    */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
/* 34 */     AlerterTableModel tableModel = (AlerterTableModel)table.getModel();
/* 35 */     Alert alert = (Alert)tableModel.getAlertList().get(row);
/* 36 */     if (alert.getInstrument().getPipScale() == 2) {
/* 37 */       this.mode2 = true;
/* 38 */       this.spinner2.setValue(Double.valueOf(alert.getPrice().doubleValue()));
/* 39 */       return this.spinner2;
/*    */     }
/* 41 */     this.mode2 = false;
/* 42 */     this.spinner4.setValue(Double.valueOf(alert.getPrice().doubleValue()));
/* 43 */     return this.spinner4;
/*    */   }
/*    */ 
/*    */   public boolean isCellEditable(EventObject anEvent)
/*    */   {
/* 48 */     if ((anEvent instanceof MouseEvent)) {
/* 49 */       return ((MouseEvent)anEvent).getClickCount() >= 2;
/*    */     }
/* 51 */     return true;
/*    */   }
/*    */ 
/*    */   public Object getCellEditorValue() {
/* 55 */     if (this.mode2) {
/* 56 */       return BigDecimal.valueOf(((Double)this.spinner2.getValue()).doubleValue()).setScale(3, RoundingMode.HALF_EVEN);
/*    */     }
/* 58 */     return BigDecimal.valueOf(((Double)this.spinner4.getValue()).doubleValue()).setScale(5, RoundingMode.HALF_EVEN);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.alerter.SpinnerEditor
 * JD-Core Version:    0.6.0
 */