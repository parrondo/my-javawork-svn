/*     */ package com.dukascopy.dds2.greed.gui.component.dowjones.calendar;
/*     */ 
/*     */ import com.dukascopy.api.ICalendarMessage;
/*     */ import com.dukascopy.api.ICalendarMessage.Detail;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.IColumn;
/*     */ import com.dukascopy.dds2.greed.gui.table.ColumnDescriptor;
/*     */ import com.dukascopy.dds2.greed.gui.table.renderers.Link;
/*     */ import com.dukascopy.transport.common.msg.news.EventCategory;
/*     */ import java.util.Date;
/*     */ 
/*     */ public enum CalendarColumn
/*     */   implements IColumn<ICalendarMessage>
/*     */ {
/*  26 */   EVENT_DATE, 
/*     */ 
/*  33 */   COUNTRY, 
/*     */ 
/*  39 */   EVENT, 
/*     */ 
/*  44 */   PERIOD, 
/*     */ 
/*  50 */   DESCRIPTION, 
/*     */ 
/*  55 */   EVENT_CATEGORY, 
/*     */ 
/*  60 */   EXPECTED, 
/*     */ 
/*  66 */   PREVIOUS, 
/*     */ 
/*  72 */   ACTUAL;
/*     */ 
/*     */   public static Object getValue(CalendarColumn column, ICalendarMessage newsMessage)
/*     */   {
/*  80 */     return column.getValue(newsMessage);
/*     */   }
/*     */ 
/*     */   public Object getValue(ICalendarMessage newsMessage)
/*     */   {
/*  88 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$dowjones$calendar$CalendarColumn[ordinal()]) { case 1:
/*  89 */       return new Date(newsMessage.getEventDate());
/*     */     case 2:
/*  90 */       return newsMessage.getCountry();
/*     */     case 3:
/*  92 */       String organisation = newsMessage.getOrganisation();
/*  93 */       String eventUrl = newsMessage.getEventURL();
/*  94 */       if ((eventUrl != null) && (eventUrl.isEmpty())) {
/*  95 */         eventUrl = null;
/*     */       }
/*  97 */       if ((organisation != null) && (!organisation.isEmpty())) {
/*  98 */         return new Link(organisation + " - " + newsMessage.getContent(), eventUrl);
/*     */       }
/* 100 */       return new Link(newsMessage.getContent(), eventUrl);
/*     */     case 4:
/* 102 */       return EventCategory.fromCodeString(newsMessage.getEventCategory());
/*     */     case 5:
/* 103 */       return newsMessage.getPeriod();
/*     */     case 6:
/* 105 */       return newsMessage.getDetails();
/*     */     case 7:
/* 106 */       return newsMessage.getDetails();
/*     */     case 8:
/* 107 */       return newsMessage.getDetails();
/*     */     case 9:
/* 108 */       return newsMessage.getDetails();
/*     */     }
/* 110 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.calendar.CalendarColumn
 * JD-Core Version:    0.6.0
 */