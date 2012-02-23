/*    */ package com.dukascopy.dds2.greed.gui.component.table;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTableModel.ExposureHolder;
/*    */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*    */ import com.dukascopy.transport.common.model.type.Position;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import java.awt.Component;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.table.DefaultTableCellRenderer;
/*    */ 
/*    */ public class CheckBoxCellRenderer extends DefaultTableCellRenderer
/*    */   implements PlatformSpecific
/*    */ {
/* 20 */   private JCheckBox ftf = new JCheckBox();
/*    */ 
/*    */   public CheckBoxCellRenderer() {
/* 23 */     this.ftf.setHorizontalAlignment(0);
/* 24 */     if (MACOSX)
/* 25 */       this.ftf.putClientProperty("JComponent.sizeVariant", "small");
/*    */   }
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */   {
/* 31 */     this.ftf.setFont(table.getFont());
/* 32 */     if (isSelected)
/* 33 */       this.ftf.setBackground(table.getSelectionBackground());
/*    */     else {
/* 35 */       this.ftf.setBackground(table.getBackground());
/*    */     }
/* 37 */     if (value == null) return this.ftf;
/* 38 */     if ((value instanceof Position)) {
/* 39 */       Position pos = (Position)value;
/* 40 */       this.ftf.setEnabled(!pos.isDisabled());
/* 41 */       this.ftf.setSelected(pos.isSelected());
/* 42 */     } else if ((value instanceof OrderMessage)) {
/* 43 */       OrderMessage om = (OrderMessage)value;
/* 44 */       this.ftf.setEnabled(!om.isDisabled());
/* 45 */       this.ftf.setSelected(om.isSelected());
/* 46 */     } else if ((value instanceof ExposureTableModel.ExposureHolder)) {
/* 47 */       ExposureTableModel.ExposureHolder exh = (ExposureTableModel.ExposureHolder)value;
/* 48 */       this.ftf.setEnabled(!exh.isDisabled());
/* 49 */       this.ftf.setSelected(exh.isSelected());
/*    */     }
/* 51 */     return this.ftf;
/*    */   }
/*    */ 
/*    */   protected void setValue(Object value) {
/* 55 */     if (((value instanceof Boolean)) && (this.ftf.isEnabled())) this.ftf.setSelected(((Boolean)value).booleanValue());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.CheckBoxCellRenderer
 * JD-Core Version:    0.6.0
 */