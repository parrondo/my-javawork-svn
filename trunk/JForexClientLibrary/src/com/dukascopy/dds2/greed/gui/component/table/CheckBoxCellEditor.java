/*    */ package com.dukascopy.dds2.greed.gui.component.table;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTableModel.ExposureHolder;
/*    */ import com.dukascopy.transport.common.model.type.Position;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import java.awt.Component;
/*    */ import java.util.EventObject;
/*    */ import javax.swing.DefaultCellEditor;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JTable;
/*    */ 
/*    */ public class CheckBoxCellEditor extends DefaultCellEditor
/*    */ {
/*    */   JCheckBox chb;
/*    */ 
/*    */   public CheckBoxCellEditor()
/*    */   {
/* 23 */     super(new JCheckBox());
/* 24 */     this.chb = ((JCheckBox)getComponent());
/* 25 */     this.chb.setHorizontalAlignment(0);
/*    */   }
/*    */ 
/*    */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/*    */   {
/* 30 */     this.chb.setFont(table.getFont());
/* 31 */     if (isSelected)
/* 32 */       this.chb.setBackground(table.getSelectionBackground());
/*    */     else {
/* 34 */       this.chb.setBackground(table.getBackground());
/*    */     }
/* 36 */     if (value == null) return this.chb;
/* 37 */     if ((value instanceof Position)) {
/* 38 */       Position pos = (Position)value;
/* 39 */       this.chb.setEnabled(!pos.isDisabled());
/* 40 */       this.chb.setSelected(pos.isSelected());
/* 41 */     } else if ((value instanceof OrderMessage)) {
/* 42 */       OrderMessage om = (OrderMessage)value;
/* 43 */       this.chb.setEnabled(!om.isDisabled());
/* 44 */       this.chb.setSelected(om.isSelected());
/* 45 */     } else if ((value instanceof ExposureTableModel.ExposureHolder)) {
/* 46 */       ExposureTableModel.ExposureHolder exh = (ExposureTableModel.ExposureHolder)value;
/* 47 */       this.chb.setEnabled(!exh.isDisabled());
/* 48 */       this.chb.setSelected(exh.isSelected());
/*    */     }
/* 50 */     return this.chb;
/*    */   }
/*    */ 
/*    */   public Object getCellEditorValue()
/*    */   {
/* 55 */     JCheckBox ftf = (JCheckBox)getComponent();
/* 56 */     return Boolean.valueOf(ftf.isSelected());
/*    */   }
/*    */ 
/*    */   public boolean stopCellEditing()
/*    */   {
/* 65 */     return super.stopCellEditing();
/*    */   }
/*    */ 
/*    */   public boolean isCellEditable(EventObject anEvent)
/*    */   {
/* 73 */     return super.isCellEditable(anEvent);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.CheckBoxCellEditor
 * JD-Core Version:    0.6.0
 */