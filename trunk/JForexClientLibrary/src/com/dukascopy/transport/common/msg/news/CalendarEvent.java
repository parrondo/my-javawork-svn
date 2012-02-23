/*     */ package com.dukascopy.transport.common.msg.news;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class CalendarEvent extends JSONObject
/*     */ {
/*     */   private static final String ACTION = "action";
/*     */   private static final String EVENT_ID = "event_id";
/*     */   private static final String EVENT_CATEGORY = "event_category";
/*     */   private static final String CONFIRMED = "confirmed";
/*     */   private static final String COUNTRY = "country";
/*     */   private static final String ORGANIZATION = "organization";
/*     */   private static final String ISIN = "isin";
/*     */   private static final String TICKER = "ticker";
/*     */   private static final String COMPANY_URL = "company_url";
/*     */   private static final String DESCRIPTION = "description";
/*     */   private static final String PERIOD = "period";
/*     */   private static final String EVENT_DATE = "event_date";
/*     */   private static final String EVENT_TIMESTAMP = "event_timestamp";
/*     */   private static final String EVENT_URL = "event_url";
/*     */   private static final String VENUE = "venue";
/*     */   private static final String EVENT_CODE = "event_code";
/*     */   private static final String EVENT_DETAILS = "event_details";
/*     */   private List<CalendarEventDetail> details;
/*     */ 
/*     */   public CalendarEvent()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CalendarEvent(JSONObject json)
/*     */   {
/*  72 */     super(json, new String[] { "action", "event_id", "event_category", "confirmed", "country", "organization", "isin", "ticker", "company_url", "description", "period", "event_date", "event_timestamp", "event_url", "venue", "event_code", "event_details" });
/*     */   }
/*     */ 
/*     */   public String getAction() {
/*  76 */     return getString("action");
/*     */   }
/*     */ 
/*     */   public void setAction(String action) {
/*  80 */     if (action != null)
/*  81 */       put("action", action);
/*     */   }
/*     */ 
/*     */   public String getEventId()
/*     */   {
/*  86 */     return getString("event_id");
/*     */   }
/*     */ 
/*     */   public void setEventId(String eventId) {
/*  90 */     if (eventId != null)
/*  91 */       put("event_id", eventId);
/*     */   }
/*     */ 
/*     */   public String getEventCategory()
/*     */   {
/*  96 */     return getString("event_category");
/*     */   }
/*     */ 
/*     */   public void setEventCategory(String eventCategory) {
/* 100 */     if (eventCategory != null)
/* 101 */       put("event_category", eventCategory);
/*     */   }
/*     */ 
/*     */   public boolean isConfirmed()
/*     */   {
/*     */     try
/*     */     {
/* 108 */       return getBoolean("confirmed"); } catch (NoSuchElementException e) {
/*     */     }
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   public void setConfirmed(boolean confirmed)
/*     */   {
/* 115 */     put("confirmed", String.valueOf(confirmed));
/*     */   }
/*     */ 
/*     */   public String getCountry() {
/* 119 */     return getString("country");
/*     */   }
/*     */ 
/*     */   public void setCountry(String country) {
/* 123 */     if (country != null)
/* 124 */       put("country", country);
/*     */   }
/*     */ 
/*     */   public String getOrganisation()
/*     */   {
/* 129 */     return NewsStoryMessage.restoreCharacters(getString("organization"));
/*     */   }
/*     */ 
/*     */   public void setOrganisation(String organisation) {
/* 133 */     if (organisation != null)
/* 134 */       put("organization", NewsStoryMessage.escapeCharacters(organisation));
/*     */   }
/*     */ 
/*     */   public String getIsin()
/*     */   {
/* 139 */     return getString("isin");
/*     */   }
/*     */ 
/*     */   public void setIsin(String isin) {
/* 143 */     if (isin != null)
/* 144 */       put("isin", isin);
/*     */   }
/*     */ 
/*     */   public String getTicker()
/*     */   {
/* 149 */     return getString("ticker");
/*     */   }
/*     */ 
/*     */   public void setTicker(String ticker) {
/* 153 */     if (ticker != null)
/* 154 */       put("ticker", ticker);
/*     */   }
/*     */ 
/*     */   public String getCompanyUrl()
/*     */   {
/* 159 */     return getString("company_url");
/*     */   }
/*     */ 
/*     */   public void setCompanyUrl(String companyUrl) {
/* 163 */     if (companyUrl != null)
/* 164 */       put("company_url", companyUrl);
/*     */   }
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 169 */     return NewsStoryMessage.restoreCharacters(getString("description"));
/*     */   }
/*     */ 
/*     */   public void setDescription(String description) {
/* 173 */     if (description != null)
/* 174 */       put("description", NewsStoryMessage.escapeCharacters(description));
/*     */   }
/*     */ 
/*     */   public String getPeriod()
/*     */   {
/* 179 */     return getString("period");
/*     */   }
/*     */ 
/*     */   public void setPeriod(String period) {
/* 183 */     if (period != null)
/* 184 */       put("period", period);
/*     */   }
/*     */ 
/*     */   public Date getEventDate()
/*     */   {
/* 189 */     String val = getString("event_date");
/* 190 */     if (val != null) {
/* 191 */       return new Date(Long.parseLong(val));
/*     */     }
/* 193 */     return null;
/*     */   }
/*     */ 
/*     */   public void setEventDate(Date date) {
/* 197 */     if (date != null)
/* 198 */       put("event_date", String.valueOf(date.getTime()));
/*     */   }
/*     */ 
/*     */   public Date getEventTimestamp()
/*     */   {
/* 203 */     String val = getString("event_timestamp");
/* 204 */     if (val != null) {
/* 205 */       return new Date(Long.parseLong(val));
/*     */     }
/* 207 */     return null;
/*     */   }
/*     */ 
/*     */   public void setEventTimestamp(Date date) {
/* 211 */     if (date != null)
/* 212 */       put("event_timestamp", String.valueOf(date.getTime()));
/*     */   }
/*     */ 
/*     */   public String getEventUrl()
/*     */   {
/* 217 */     return NewsStoryMessage.restoreCharacters(getString("event_url"));
/*     */   }
/*     */ 
/*     */   public void setEventUrl(String eventUrl) {
/* 221 */     if (eventUrl != null)
/* 222 */       put("event_url", NewsStoryMessage.escapeCharacters(eventUrl));
/*     */   }
/*     */ 
/*     */   public String getVenue()
/*     */   {
/* 227 */     return getString("venue");
/*     */   }
/*     */ 
/*     */   public void setVenue(String venue) {
/* 231 */     if (venue != null)
/* 232 */       put("venue", venue);
/*     */   }
/*     */ 
/*     */   public CalendarType getCalendarType()
/*     */   {
/* 237 */     String type = getString("event_code");
/* 238 */     if (type != null) {
/* 239 */       return CalendarType.valueOf(type);
/*     */     }
/* 241 */     return null;
/*     */   }
/*     */ 
/*     */   public void setCalendarType(CalendarType calendarType) {
/* 245 */     if (calendarType != null)
/* 246 */       put("event_code", calendarType.toString());
/*     */   }
/*     */ 
/*     */   public List<CalendarEventDetail> getDetails()
/*     */   {
/* 251 */     if (this.details == null) {
/* 252 */       this.details = new ArrayList();
/*     */       try {
/* 254 */         JSONArray jsonArray = getJSONArray("event_details");
/* 255 */         for (int i = 0; i < jsonArray.length(); i++) {
/* 256 */           CalendarEventDetail eventDetail = new CalendarEventDetail(jsonArray.getJSONObject(i));
/* 257 */           this.details.add(eventDetail);
/*     */         }
/*     */       } catch (NoSuchElementException nse) {
/*     */       }
/*     */     }
/* 262 */     return this.details;
/*     */   }
/*     */ 
/*     */   public void setDetails(List<CalendarEventDetail> details) {
/* 266 */     this.details = details;
/* 267 */     JSONArray jsonArray = new JSONArray();
/* 268 */     for (CalendarEventDetail detail : details) {
/* 269 */       jsonArray.put(detail);
/*     */     }
/* 271 */     put("event_details", jsonArray);
/*     */   }
/*     */ 
/*     */   public static enum CalendarType
/*     */   {
/*  15 */     ICC("Corporate"), 
/*  16 */     IEP("Economic"), 
/*  17 */     IDC("Debt Issuance");
/*     */ 
/*     */     private String description;
/*     */ 
/*     */     private CalendarType(String description) {
/*  23 */       this.description = description;
/*     */     }
/*     */ 
/*     */     public String getDescription() {
/*  27 */       return this.description;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.CalendarEvent
 * JD-Core Version:    0.6.0
 */