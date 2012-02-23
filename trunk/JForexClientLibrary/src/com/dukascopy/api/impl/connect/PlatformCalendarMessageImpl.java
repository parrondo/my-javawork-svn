/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.ICalendarMessage;
/*     */ import com.dukascopy.api.ICalendarMessage.Detail;
/*     */ import com.dukascopy.api.IMessage.Type;
/*     */ import com.dukascopy.api.INewsMessage.Action;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import com.dukascopy.transport.common.msg.news.CalendarEvent;
/*     */ import com.dukascopy.transport.common.msg.news.CalendarEvent.CalendarType;
/*     */ import com.dukascopy.transport.common.msg.news.CalendarEventDetail;
/*     */ import com.dukascopy.transport.common.msg.news.Currency;
/*     */ import com.dukascopy.transport.common.msg.news.GeoRegion;
/*     */ import com.dukascopy.transport.common.msg.news.MarketSector;
/*     */ import com.dukascopy.transport.common.msg.news.StockIndex;
/*     */ import java.text.DateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class PlatformCalendarMessageImpl extends PlatformNewsMessageImpl
/*     */   implements ICalendarMessage
/*     */ {
/*     */   private final String action;
/*     */   private final String companyURL;
/*     */   private final String country;
/*     */   private final String eventCode;
/*     */   private final String eventCategory;
/*     */   private long eventDate;
/*     */   private final String id;
/*     */   private final String eventURL;
/*     */   private final String ISIN;
/*     */   private final String organisation;
/*     */   private final String period;
/*     */   private final String ticker;
/*     */   private final String venue;
/*     */   private final boolean isConfirmed;
/*  37 */   private final List<ICalendarMessage.Detail> details = new ArrayList();
/*     */ 
/*     */   public PlatformCalendarMessageImpl(CalendarEvent calendarEvent, String copyright, String header, String newsId, long publishDate, boolean endOfStory, boolean isHot, Set<Currency> currencies, Set<GeoRegion> geoRegions, Set<MarketSector> marketSectors, Set<StockIndex> stockIndicies)
/*     */   {
/*  52 */     super(calendarEvent == null ? null : calendarEvent.getDescription(), copyright, header, newsId, publishDate, endOfStory, isHot, currencies, geoRegions, marketSectors, stockIndicies);
/*     */ 
/*  54 */     if (calendarEvent != null) {
/*  55 */       this.id = calendarEvent.getEventId();
/*  56 */       this.action = calendarEvent.getAction();
/*     */     } else {
/*  58 */       this.id = null;
/*  59 */       this.action = null;
/*     */     }
/*     */ 
/*  62 */     if ((getAction() == null) || (getAction() == INewsMessage.Action.DELETE)) {
/*  63 */       this.companyURL = null;
/*  64 */       this.country = null;
/*  65 */       this.eventCode = null;
/*  66 */       this.eventCategory = null;
/*  67 */       this.eventDate = 0L;
/*  68 */       this.eventURL = null;
/*  69 */       this.ISIN = null;
/*  70 */       this.organisation = null;
/*  71 */       this.period = null;
/*  72 */       this.ticker = null;
/*  73 */       this.venue = null;
/*  74 */       this.isConfirmed = false;
/*     */     } else {
/*  76 */       this.companyURL = calendarEvent.getCompanyUrl();
/*  77 */       this.country = calendarEvent.getCountry();
/*  78 */       this.eventCategory = calendarEvent.getEventCategory();
/*  79 */       if (calendarEvent.getCalendarType() != null)
/*  80 */         this.eventCode = calendarEvent.getCalendarType().name();
/*     */       else {
/*  82 */         this.eventCode = null;
/*     */       }
/*  84 */       this.eventDate = calendarEvent.getEventDate().getTime();
/*  85 */       this.eventURL = calendarEvent.getEventUrl();
/*  86 */       this.ISIN = calendarEvent.getIsin();
/*  87 */       this.organisation = calendarEvent.getOrganisation();
/*  88 */       this.period = calendarEvent.getPeriod();
/*  89 */       this.ticker = calendarEvent.getTicker();
/*  90 */       this.venue = calendarEvent.getVenue();
/*  91 */       this.isConfirmed = calendarEvent.isConfirmed();
/*     */ 
/*  93 */       for (CalendarEventDetail calendarEventDetail : calendarEvent.getDetails())
/*  94 */         this.details.add(new PlatformCalendarDetail(calendarEventDetail.getDetailId(), calendarEventDetail.getActual(), calendarEventDetail.getDelta(), calendarEventDetail.getDescription(), calendarEventDetail.getExpected(), calendarEventDetail.getPrevious()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getCompanyURL()
/*     */   {
/* 110 */     return this.companyURL;
/*     */   }
/*     */ 
/*     */   public String getCountry() {
/* 114 */     return this.country;
/*     */   }
/*     */ 
/*     */   public List<ICalendarMessage.Detail> getDetails()
/*     */   {
/* 119 */     return this.details;
/*     */   }
/*     */ 
/*     */   public String getEventCode() {
/* 123 */     return this.eventCode;
/*     */   }
/*     */ 
/*     */   public String getEventCategory()
/*     */   {
/* 129 */     return this.eventCategory;
/*     */   }
/*     */ 
/*     */   public long getEventDate()
/*     */   {
/* 134 */     return this.eventDate;
/*     */   }
/*     */ 
/*     */   public String getISIN() {
/* 138 */     return this.ISIN;
/*     */   }
/*     */ 
/*     */   public String getOrganisation() {
/* 142 */     return this.organisation;
/*     */   }
/*     */ 
/*     */   public String getPeriod() {
/* 146 */     return this.period;
/*     */   }
/*     */ 
/*     */   public String getTicker() {
/* 150 */     return this.ticker;
/*     */   }
/*     */ 
/*     */   public String getEventURL() {
/* 154 */     return this.eventURL;
/*     */   }
/*     */ 
/*     */   public String getVenue() {
/* 158 */     return this.venue;
/*     */   }
/*     */ 
/*     */   public boolean isConfirmed() {
/* 162 */     return this.isConfirmed;
/*     */   }
/*     */ 
/*     */   public INewsMessage.Action getAction() {
/* 166 */     if (this.action == null) {
/* 167 */       return null;
/*     */     }
/*     */ 
/* 170 */     return INewsMessage.Action.valueOf(this.action);
/*     */   }
/*     */ 
/*     */   public String getId() {
/* 174 */     return this.id;
/*     */   }
/*     */ 
/*     */   public IMessage.Type getType() {
/* 178 */     return IMessage.Type.CALENDAR;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 183 */     StringBuilder sb = new StringBuilder();
/* 184 */     sb.append(String.format("[MessageType %s]:%n \tcontent : %s, country : %s, company url : %s, event code : %s, event category: %s, organization : %s, period : %s, event date: %s", new Object[] { getType(), getContent(), getCountry(), getCompanyURL(), getEventCode(), getEventCategory(), getOrganisation(), getPeriod(), this.df.format(Long.valueOf(getEventDate())) }));
/*     */ 
/* 186 */     if (!ObjectUtils.isNullOrEmpty(getDetails())) {
/* 187 */       sb.append(String.format("%n", new Object[0]));
/* 188 */       sb.append("\tCalendar Details: ");
/* 189 */       for (ICalendarMessage.Detail detail : getDetails()) {
/* 190 */         sb.append(String.format("%n\t\tId:%s, Description:%s, Expected:%s, Actual:%s, Delta:%s, Previous:%s ", new Object[] { detail.getId(), detail.getDescription(), detail.getExpected(), detail.getActual(), detail.getDelta(), detail.getPrevious() }));
/*     */       }
/*     */     }
/*     */ 
/* 194 */     sb.append(String.format("%n", new Object[0])).append(getMetaDataInfo());
/* 195 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   class PlatformCalendarDetail
/*     */     implements ICalendarMessage.Detail
/*     */   {
/* 201 */     private String actual = null;
/* 202 */     private String delta = null;
/* 203 */     private String description = null;
/* 204 */     private String id = null;
/* 205 */     private String expected = null;
/* 206 */     private String previous = null;
/*     */ 
/*     */     public PlatformCalendarDetail(String id, String actual, String delta, String description, String expected, String previous) {
/* 209 */       this.id = id;
/* 210 */       this.actual = actual;
/* 211 */       this.delta = delta;
/* 212 */       this.description = description;
/* 213 */       this.id = id;
/* 214 */       this.expected = expected;
/* 215 */       this.previous = previous;
/*     */     }
/*     */ 
/*     */     public String getActual()
/*     */     {
/* 220 */       return this.actual;
/*     */     }
/*     */ 
/*     */     public String getDelta()
/*     */     {
/* 225 */       return this.delta;
/*     */     }
/*     */ 
/*     */     public String getDescription()
/*     */     {
/* 230 */       return this.description;
/*     */     }
/*     */ 
/*     */     public String getExpected()
/*     */     {
/* 235 */       return this.expected;
/*     */     }
/*     */ 
/*     */     public String getId()
/*     */     {
/* 240 */       return this.id;
/*     */     }
/*     */ 
/*     */     public String getPrevious()
/*     */     {
/* 245 */       return this.previous;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.PlatformCalendarMessageImpl
 * JD-Core Version:    0.6.0
 */