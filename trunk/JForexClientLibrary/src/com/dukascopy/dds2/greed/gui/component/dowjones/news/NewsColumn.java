/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones.news;
/*    */ 
/*    */ import com.dukascopy.api.INewsMessage;
/*    */ import com.dukascopy.dds2.greed.gui.component.dowjones.IColumn;
/*    */ import com.dukascopy.dds2.greed.gui.table.ColumnDescriptor;
/*    */ import java.util.Date;
/*    */ import javax.swing.SortOrder;
/*    */ 
/*    */ public enum NewsColumn
/*    */   implements IColumn<INewsMessage>
/*    */ {
/* 19 */   PUBLISH_DATE, 
/*    */ 
/* 27 */   HEADER;
/*    */ 
/*    */   public static Object getValue(NewsColumn column, INewsMessage newsMessage)
/*    */   {
/* 35 */     return column.getValue(newsMessage);
/*    */   }
/*    */ 
/*    */   public Object getValue(INewsMessage newsMessage)
/*    */   {
/* 43 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$dowjones$news$NewsColumn[ordinal()]) { case 1:
/* 44 */       return newsMessage.getHeader();
/*    */     case 2:
/* 45 */       return new Date(newsMessage.getPublishDate());
/*    */     }
/* 47 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.news.NewsColumn
 * JD-Core Version:    0.6.0
 */