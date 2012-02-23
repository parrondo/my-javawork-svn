/*     */ package com.dukascopy.dds2.greed.gui.component.message;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ 
/*     */ public class MessageTableModel extends AbstractTableModel
/*     */ {
/*  22 */   private List<Notification> messages = new ArrayList();
/*     */   public static final int TIME_COLUMN = 0;
/*     */   public static final int MESSAGE_COLUMN = 1;
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/*  41 */     return this.messages.size();
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/*  50 */     return 2;
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/*  70 */     Notification message = (Notification)this.messages.get(rowIndex);
/*  71 */     switch (columnIndex) {
/*     */     case 0:
/*  73 */       Date timestamp = message.getTimestamp();
/*  74 */       if (timestamp != null) {
/*  75 */         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*  76 */         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  77 */         return dateFormat.format(timestamp);
/*     */       }
/*  79 */       return null;
/*     */     case 1:
/*  82 */       return message.getContent();
/*     */     }
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */   public void addMessage(Notification message)
/*     */   {
/*  93 */     this.messages.add(0, message);
/*  94 */     if (this.messages.size() > 1000) {
/*  95 */       this.messages.remove(this.messages.size() - 1);
/*     */     }
/*  97 */     fireTableRowsInserted(0, 0);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 104 */     this.messages.clear();
/* 105 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public Notification getNotification(int row)
/*     */   {
/* 114 */     if (row < this.messages.size()) {
/* 115 */       return (Notification)this.messages.get(row);
/*     */     }
/* 117 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.message.MessageTableModel
 * JD-Core Version:    0.6.0
 */