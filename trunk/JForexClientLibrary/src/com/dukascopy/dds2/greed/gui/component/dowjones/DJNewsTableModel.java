/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones;
/*    */ 
/*    */ import com.dukascopy.api.INewsMessage;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableAnnotatedTableModel;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public class DJNewsTableModel<ColumnBean extends Enum<ColumnBean>,  extends IColumn<Info>, Info extends INewsMessage> extends JLocalizableAnnotatedTableModel<ColumnBean, Info>
/*    */ {
/*    */   private static final int HIGHLIGHT_TIME = 30;
/*    */   private final Map<String, Long> highligtedRows;
/*    */ 
/*    */   public DJNewsTableModel(Class<ColumnBean> columnBeanClass, Class<Info> infoClass)
/*    */   {
/* 22 */     super(columnBeanClass, infoClass);
/*    */ 
/* 24 */     this.highligtedRows = new HashMap();
/*    */   }
/*    */ 
/*    */   public void insert(Info newsMessage) {
/* 28 */     String messageId = newsMessage.getId();
/* 29 */     int index = find(messageId);
/*    */ 
/* 31 */     if (index >= 0) {
/* 32 */       this.highligtedRows.put(messageId, Long.valueOf(System.currentTimeMillis()));
/* 33 */       update(index, newsMessage);
/*    */     } else {
/* 35 */       add(newsMessage);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void remove(String id) {
/* 40 */     int index = find(id);
/*    */ 
/* 42 */     if (index >= 0) {
/* 43 */       remove(index);
/* 44 */       if (this.highligtedRows.containsKey(Integer.valueOf(index)))
/* 45 */         this.highligtedRows.remove(Integer.valueOf(index));
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean isHighlighted(int rowIndex)
/*    */   {
/* 51 */     String messageId = ((INewsMessage)get(rowIndex)).getId();
/*    */ 
/* 53 */     if (this.highligtedRows.containsKey(messageId)) {
/* 54 */       long time = ((Long)this.highligtedRows.get(messageId)).longValue();
/*    */ 
/* 56 */       if (System.currentTimeMillis() - time > TimeUnit.SECONDS.toMillis(30L)) {
/* 57 */         this.highligtedRows.remove(messageId);
/* 58 */         return false;
/*    */       }
/*    */ 
/* 61 */       return true;
/*    */     }
/*    */ 
/* 64 */     return false;
/*    */   }
/*    */ 
/*    */   private int find(String id) {
/* 68 */     for (int i = 0; i < getRowCount(); i++) {
/* 69 */       INewsMessage newsMessage = (INewsMessage)get(i);
/* 70 */       if (newsMessage.getId().equals(id)) {
/* 71 */         return i;
/*    */       }
/*    */     }
/*    */ 
/* 75 */     return -1;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.DJNewsTableModel
 * JD-Core Version:    0.6.0
 */