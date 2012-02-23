/*    */ package com.dukascopy.dds2.greed.gui.component.table;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*    */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*    */ import com.dukascopy.transport.common.model.type.Position;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import java.awt.event.ItemEvent;
/*    */ import java.awt.event.ItemListener;
/*    */ import javax.swing.AbstractButton;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.table.AbstractTableModel;
/*    */ import javax.swing.table.DefaultTableModel;
/*    */ import javax.swing.table.TableModel;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class CheckBoxHeaderItemListener
/*    */   implements ItemListener
/*    */ {
/* 26 */   private static final Logger LOGGER = LoggerFactory.getLogger(CheckBoxHeaderItemListener.class);
/*    */   private JTable table;
/*    */ 
/*    */   public CheckBoxHeaderItemListener(JTable table)
/*    */   {
/* 31 */     this.table = table;
/*    */   }
/*    */ 
/*    */   public void itemStateChanged(ItemEvent e) {
/* 35 */     Object source = e.getSource();
/* 36 */     if (!(source instanceof AbstractButton)) {
/* 37 */       return;
/*    */     }
/*    */ 
/* 41 */     boolean statebool = e.getStateChange() == 1;
/* 42 */     int column = ((CheckBoxHeader)(CheckBoxHeader)e.getItem()).getColumn();
/* 43 */     if ((this.table != null) && (this.table.getRowCount() != 0)) {
/* 44 */       TableModel tableModel = this.table.getModel();
/* 45 */       TableModel dataModel = tableModel;
/* 46 */       if ((tableModel instanceof TableSorter)) {
/* 47 */         dataModel = ((TableSorter)tableModel).getTableModel();
/*    */       }
/* 49 */       for (int i = 0; i < this.table.getRowCount(); i++) {
/* 50 */         if ((dataModel instanceof PositionsTableModel))
/*    */         {
/*    */           Position pos;
/*    */           try
/*    */           {
/* 56 */             pos = (Position)tableModel.getValueAt(i, this.table.convertColumnIndexToModel(column));
/*    */           } catch (ClassCastException ex) {
/* 58 */             LOGGER.error(ex.getMessage(), ex);
/* 59 */             return;
/*    */           }
/* 61 */           if (!pos.isDisabled())
/* 62 */             dataModel.setValueAt(Boolean.valueOf(statebool), i, 0);
/* 63 */         } else if (((tableModel instanceof TableSorter)) && ((dataModel instanceof OrderCommonTableModel))) {
/* 64 */           OrderMessage order = (OrderMessage)tableModel.getValueAt(i, OrderCommonTableModel.COLUMN_CHECK);
/*    */ 
/* 66 */           if (!order.isDisabled())
/* 67 */             dataModel.setValueAt(Boolean.valueOf(statebool), i, OrderCommonTableModel.COLUMN_CHECK);
/*    */         } else {
/* 69 */           tableModel.setValueAt(Boolean.valueOf(statebool), i, this.table.convertColumnIndexToModel(column));
/*    */         }
/*    */       }
/* 72 */       if ((tableModel instanceof DefaultTableModel)) {
/* 73 */         ((DefaultTableModel)tableModel).fireTableDataChanged();
/*    */       }
/* 75 */       if ((tableModel instanceof AbstractTableModel))
/* 76 */         ((AbstractTableModel)tableModel).fireTableDataChanged();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeaderItemListener
 * JD-Core Version:    0.6.0
 */