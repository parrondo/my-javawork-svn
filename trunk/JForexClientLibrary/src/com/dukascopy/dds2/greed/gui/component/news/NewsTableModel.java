/*    */ package com.dukascopy.dds2.greed.gui.component.news;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.group.MarketNewsMessageGroup.MarketNews;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.table.AbstractTableModel;
/*    */ 
/*    */ public class NewsTableModel extends AbstractTableModel
/*    */ {
/*    */   private static final int COLUMNS = 2;
/*    */   public static final int COL_DATE = 0;
/*    */   public static final int COL_NEWS = 1;
/* 21 */   private List<MarketNewsMessageGroup.MarketNews> newsList = new ArrayList();
/*    */ 
/*    */   public int getColumnCount() {
/* 24 */     return 2;
/*    */   }
/*    */ 
/*    */   public int getRowCount() {
/* 28 */     return this.newsList.size();
/*    */   }
/*    */ 
/*    */   public Object getValueAt(int row, int column) {
/* 32 */     if ((row < 0) || (row >= getRowCount())) return null;
/* 33 */     if ((column < 0) || (column >= getColumnCount())) return null;
/*    */ 
/* 35 */     MarketNewsMessageGroup.MarketNews news = (MarketNewsMessageGroup.MarketNews)this.newsList.get(row);
/* 36 */     switch (column) { case 0:
/* 37 */       return news.getNewsDate();
/*    */     case 1:
/* 38 */       return " " + news.getHeadNews().trim();
/*    */     }
/* 40 */     return null;
/*    */   }
/*    */   public void clear() {
/* 43 */     this.newsList.clear();
/*    */   }
/*    */   public void addNews(List<MarketNewsMessageGroup.MarketNews> newsList) {
/* 46 */     this.newsList.clear();
/* 47 */     for (MarketNewsMessageGroup.MarketNews news : newsList)
/* 48 */       this.newsList.add(news);
/*    */   }
/*    */ 
/*    */   public MarketNewsMessageGroup.MarketNews getNews(int index)
/*    */   {
/* 53 */     if ((index < 0) || (index >= getRowCount())) return null;
/* 54 */     return (MarketNewsMessageGroup.MarketNews)this.newsList.get(index);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.news.NewsTableModel
 * JD-Core Version:    0.6.0
 */