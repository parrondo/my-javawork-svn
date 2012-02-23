/*     */ package com.dukascopy.dds2.greed.gui.component.dowjones.calendar;
/*     */ 
/*     */ import com.dukascopy.api.CalendarFilter;
/*     */ import com.dukascopy.api.ICalendarMessage;
/*     */ import com.dukascopy.api.INewsFilter;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.dowjones.NewsSubscribeActionEvent;
/*     */ import com.dukascopy.dds2.greed.actions.dowjones.NewsUnsubscribeActionEvent;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.DJNewsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.DJNewsTableModelFilter;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ 
/*     */ public class DowJonesCalendarPanel extends JPanel
/*     */ {
/*     */   private final DJNewsTableModelFilter<CalendarColumn, ICalendarMessage> tableModel;
/*     */   private final DJNewsTable<CalendarColumn, ICalendarMessage> table;
/*     */   private final FilterPanel filterPanel;
/*     */   private CalendarFilter calendarFilter;
/*  31 */   private boolean subscribed = false;
/*     */ 
/*     */   public DowJonesCalendarPanel() {
/*  34 */     super(new BorderLayout());
/*     */ 
/*  36 */     this.tableModel = new DJNewsTableModelFilter(CalendarColumn.class, ICalendarMessage.class);
/*  37 */     this.table = new DJNewsTable(this.tableModel);
/*     */ 
/*  39 */     this.calendarFilter = new CalendarFilter();
/*     */ 
/*  41 */     add(this.filterPanel = new FilterPanel(this.calendarFilter, new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  46 */         DowJonesCalendarPanel.this.subscribe();
/*     */       }
/*     */     }
/*     */     , new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  52 */         DowJonesCalendarPanel.this.setup();
/*     */       }
/*     */     }
/*     */     , new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  58 */         String txt = (String)e.getSource();
/*  59 */         DowJonesCalendarPanel.this.applyQuickTableDataFilter(txt);
/*     */       }
/*     */     }), "First");
/*     */ 
/*  64 */     add(new JPanel(new BorderLayout())
/*     */     {
/*     */     }
/*     */     , "Center");
/*     */   }
/*     */ 
/*     */   public INewsFilter getFilter()
/*     */   {
/*  71 */     return this.calendarFilter;
/*     */   }
/*     */ 
/*     */   public void setFilter(INewsFilter calendarFilter) {
/*  75 */     this.calendarFilter = ((CalendarFilter)calendarFilter);
/*  76 */     this.filterPanel.refresh(this.calendarFilter);
/*     */   }
/*     */ 
/*     */   public void unsubscribe() {
/*  80 */     if (this.subscribed) {
/*  81 */       GreedContext.publishEvent(new NewsUnsubscribeActionEvent(this, this.calendarFilter.getNewsSource()));
/*  82 */       this.subscribed = false;
/*  83 */       stopLoadingProgress();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void subscribe() {
/*  88 */     unsubscribe();
/*  89 */     GreedContext.publishEvent(new NewsSubscribeActionEvent(this, this.calendarFilter));
/*  90 */     this.subscribed = true;
/*  91 */     this.tableModel.clearAll();
/*  92 */     setInProgress(true);
/*     */   }
/*     */ 
/*     */   public boolean isSubscribed() {
/*  96 */     return this.subscribed;
/*     */   }
/*     */ 
/*     */   public void insert(ICalendarMessage calendarMessage) {
/* 100 */     this.tableModel.insert(calendarMessage);
/*     */   }
/*     */ 
/*     */   public void delete(String id) {
/* 104 */     this.tableModel.remove(id);
/*     */   }
/*     */ 
/*     */   public void stopLoadingProgress() {
/* 108 */     setInProgress(false);
/*     */   }
/*     */ 
/*     */   private void setup() {
/* 112 */     new SetupDialog(this.calendarFilter, new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 115 */         DowJonesCalendarPanel.this.setFilter((INewsFilter)e.getSource());
/* 116 */         DowJonesCalendarPanel.this.subscribe();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void applyQuickTableDataFilter(String filter) {
/* 123 */     this.table.setPattern(filter);
/*     */   }
/*     */ 
/*     */   private void setInProgress(boolean value)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.calendar.DowJonesCalendarPanel
 * JD-Core Version:    0.6.0
 */