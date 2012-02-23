/*    */ package com.dukascopy.dds2.greed.gui.component.alerter;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import java.math.BigDecimal;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.table.AbstractTableModel;
/*    */ 
/*    */ public class AlerterTableModel extends AbstractTableModel
/*    */ {
/*    */   public static final int COLUMN_CURRENCY = 0;
/*    */   public static final int COLUMN_CONDITION = 1;
/*    */   public static final int COLUMN_PRICE = 2;
/*    */   public static final int COLUMN_EVENT = 3;
/*    */   public static final int COLUMN_STATUS = 4;
/* 20 */   private List<Alert> alertList = new ArrayList();
/*    */ 
/*    */   public int getRowCount()
/*    */   {
/* 26 */     return this.alertList.size();
/*    */   }
/*    */ 
/*    */   public int getColumnCount() {
/* 30 */     return 5;
/*    */   }
/*    */ 
/*    */   public Object getValueAt(int rowIndex, int columnIndex) {
/* 34 */     Alert alert = (Alert)this.alertList.get(rowIndex);
/* 35 */     switch (columnIndex) {
/*    */     case 0:
/* 37 */       return alert.getInstrument();
/*    */     case 1:
/* 39 */       return alert.getCondition();
/*    */     case 2:
/* 41 */       return alert.getPrice();
/*    */     case 3:
/* 43 */       return alert.getNotification();
/*    */     case 4:
/* 45 */       return alert.getStatus();
/*    */     }
/* 47 */     throw new IllegalArgumentException("Column " + columnIndex + " is undefined!");
/*    */   }
/*    */ 
/*    */   public List<Alert> getAlertList()
/*    */   {
/* 52 */     return this.alertList;
/*    */   }
/*    */ 
/*    */   public void addAlert(Alert alert) {
/* 56 */     this.alertList.add(alert);
/* 57 */     fireTableRowsInserted(this.alertList.size() - 1, this.alertList.size() - 1);
/*    */   }
/*    */ 
/*    */   public void deleteAlert(int index) {
/* 61 */     this.alertList.remove(index);
/* 62 */     fireTableRowsDeleted(index, index);
/*    */   }
/*    */ 
/*    */   public boolean isCellEditable(int rowIndex, int columnIndex) {
/* 66 */     return true;
/*    */   }
/*    */ 
/*    */   public void setValueAt(Object value, int rowIndex, int columnIndex) {
/* 70 */     Alert alert = (Alert)this.alertList.get(rowIndex);
/* 71 */     if (alert != null) {
/* 72 */       switch (columnIndex) {
/*    */       case 0:
/* 74 */         alert.setInstrument((Instrument)value);
/* 75 */         break;
/*    */       case 1:
/* 77 */         alert.setCondition((Condition)value);
/* 78 */         break;
/*    */       case 2:
/* 80 */         alert.setPrice((BigDecimal)value);
/* 81 */         break;
/*    */       case 3:
/* 83 */         alert.setNotification((AlerterNotification)value);
/* 84 */         break;
/*    */       case 4:
/* 86 */         alert.setStatus((AlerterStatus)value);
/* 87 */         break;
/*    */       default:
/* 89 */         throw new IllegalArgumentException("Column " + columnIndex + " is not editable!");
/*    */       }
/*    */     }
/* 92 */     fireTableRowsUpdated(rowIndex, rowIndex);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.alerter.AlerterTableModel
 * JD-Core Version:    0.6.0
 */