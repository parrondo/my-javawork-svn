/*     */ package com.dukascopy.dds2.greed.gui.component.dowjones.news;
/*     */ 
/*     */ import com.dukascopy.api.INewsFilter;
/*     */ import com.dukascopy.api.INewsMessage;
/*     */ import com.dukascopy.api.NewsFilter;
/*     */ import com.dukascopy.api.NewsFilter.TimeFrame;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.dowjones.NewsContentRequestActionEvent;
/*     */ import com.dukascopy.dds2.greed.actions.dowjones.NewsSubscribeActionEvent;
/*     */ import com.dukascopy.dds2.greed.actions.dowjones.NewsUnsubscribeActionEvent;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.DJNewsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.DJNewsTableModelFilter;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class DowJonesNewsPanel extends JPanel
/*     */ {
/*     */   private final DJNewsTableModelFilter<NewsColumn, INewsMessage> tableModel;
/*     */   private final DJNewsTable<NewsColumn, INewsMessage> table;
/*     */   private final FilterPanel filterPanel;
/*     */   private NewsFilter newsFilter;
/*  40 */   private boolean subscribed = false;
/*     */ 
/*     */   public DowJonesNewsPanel() {
/*  43 */     super(new BorderLayout());
/*     */ 
/*  45 */     this.tableModel = new DJNewsTableModelFilter(NewsColumn.class, INewsMessage.class);
/*  46 */     this.table = new DJNewsTable(this.tableModel);
/*     */ 
/*  51 */     this.table.addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseClicked(MouseEvent e) {
/*  54 */         if (e.getClickCount() >= 2)
/*  55 */           DowJonesNewsPanel.this.showNewsContent(e.getPoint());
/*     */       }
/*     */     });
/*  59 */     this.newsFilter = new NewsFilter();
/*     */ 
/*  61 */     this.filterPanel = new FilterPanel(this.newsFilter, new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  65 */         DowJonesNewsPanel.this.subscribe();
/*     */       }
/*     */     }
/*     */     , new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  71 */         DowJonesNewsPanel.this.setup();
/*     */       }
/*     */     }
/*     */     , new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  77 */         String txt = (String)e.getSource();
/*  78 */         DowJonesNewsPanel.this.applyQuickTableDataFilter(txt);
/*     */       }
/*     */     });
/*  82 */     add(this.filterPanel, "First");
/*     */ 
/*  84 */     add(new JPanel(new BorderLayout())
/*     */     {
/*     */     }
/*     */     , "Center");
/*     */   }
/*     */ 
/*     */   public INewsFilter getFilter()
/*     */   {
/*  91 */     return this.newsFilter;
/*     */   }
/*     */ 
/*     */   public void setFilter(INewsFilter newsFilter) {
/*  95 */     this.newsFilter = ((NewsFilter)newsFilter);
/*     */   }
/*     */ 
/*     */   public void unsubscribe() {
/*  99 */     if (this.subscribed) {
/* 100 */       GreedContext.publishEvent(new NewsUnsubscribeActionEvent(this, this.newsFilter.getNewsSource()));
/* 101 */       this.subscribed = false;
/* 102 */       setInProgress(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void subscribe() {
/* 107 */     this.filterPanel.refresh(this.newsFilter);
/* 108 */     unsubscribe();
/* 109 */     GreedContext.publishEvent(new NewsSubscribeActionEvent(this, this.newsFilter));
/* 110 */     this.subscribed = true;
/* 111 */     this.tableModel.clearAll();
/*     */ 
/* 113 */     setInProgress(NewsFilter.TimeFrame.ONLINE != this.newsFilter.getTimeFrame());
/*     */   }
/*     */ 
/*     */   public boolean isSubscribed() {
/* 117 */     return this.subscribed;
/*     */   }
/*     */ 
/*     */   public void add(INewsMessage newsMessage) {
/* 121 */     NewsContentDialog contentDialog = NewsContentDialog.getCurrentInstance();
/* 122 */     if ((contentDialog != null) && (contentDialog.isShowing(newsMessage.getId()))) {
/* 123 */       contentDialog.add(newsMessage);
/*     */     }
/* 125 */     else if (newsMessage.getId() == null)
/* 126 */       setInProgress(false);
/*     */     else
/* 128 */       this.tableModel.insert(newsMessage);
/*     */   }
/*     */ 
/*     */   private void setup()
/*     */   {
/* 134 */     new SetupDialog(this.newsFilter, new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 137 */         DowJonesNewsPanel.this.setFilter((INewsFilter)e.getSource());
/* 138 */         DowJonesNewsPanel.this.subscribe();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void showNewsContent(Point point) {
/* 144 */     int rowIndex = this.table.rowAtPoint(point);
/* 145 */     if (rowIndex >= 0)
/* 146 */       SwingUtilities.invokeLater(new Runnable(rowIndex)
/*     */       {
/*     */         public void run() {
/* 149 */           INewsMessage newsMessage = (INewsMessage)DowJonesNewsPanel.this.tableModel.get(DowJonesNewsPanel.this.table.convertRowIndexToModel(this.val$rowIndex));
/* 150 */           String newsId = newsMessage.getId();
/* 151 */           GreedContext.publishEvent(new NewsContentRequestActionEvent(DowJonesNewsPanel.this, newsId));
/* 152 */           new NewsContentDialog(newsId, newsMessage)
/*     */           {
/*     */           };
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   private void applyQuickTableDataFilter(String filter) {
/* 162 */     this.table.setPattern(filter);
/*     */   }
/*     */ 
/*     */   private void setInProgress(boolean value)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.news.DowJonesNewsPanel
 * JD-Core Version:    0.6.0
 */