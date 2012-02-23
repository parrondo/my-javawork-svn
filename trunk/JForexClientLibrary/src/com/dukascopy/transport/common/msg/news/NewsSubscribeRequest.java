/*     */ package com.dukascopy.transport.common.msg.news;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.datafeed.AbstractDFSMessage;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.json.JSONArray;
/*     */ 
/*     */ public class NewsSubscribeRequest extends AbstractDFSMessage
/*     */ {
/*     */   public static final String TYPE = "news_subscr";
/*     */   public static final String REQUEST_TYPE = "req_type";
/*     */   public static final String NEWS_SOURCE = "news_src";
/*     */   public static final String HOT = "hot";
/*     */   public static final String MARKET_SECTORS = "markets";
/*     */   public static final String CURRENCIES = "currs";
/*     */   public static final String INDICIES = "index";
/*     */   public static final String GEO = "geo";
/*     */   public static final String CATEGORIES = "categories";
/*     */   public static final String KEYWORDS = "keywd";
/*     */   public static final String CALENDAR_TYPE = "cal_type";
/*     */   public static final String FROM_DATE = "from_date";
/*     */   public static final String TO_DATE = "to_date";
/*     */ 
/*     */   public NewsSubscribeRequest()
/*     */   {
/*  42 */     setType("news_subscr");
/*     */   }
/*     */ 
/*     */   public NewsSubscribeRequest(ProtocolMessage message) {
/*  46 */     super(message);
/*  47 */     setType("news_subscr");
/*  48 */     put("req_type", message.getString("req_type"));
/*  49 */     put("news_src", message.getString("news_src"));
/*  50 */     put("hot", message.getString("hot"));
/*  51 */     put("markets", message.getJSONArray("markets"));
/*  52 */     put("currs", message.getJSONArray("currs"));
/*  53 */     put("index", message.getJSONArray("index"));
/*  54 */     put("geo", message.getJSONArray("geo"));
/*  55 */     put("categories", message.getJSONArray("categories"));
/*  56 */     put("cal_type", message.getString("cal_type"));
/*  57 */     put("keywd", message.getJSONArray("keywd"));
/*  58 */     put("from_date", message.getString("from_date"));
/*  59 */     put("to_date", message.getString("to_date"));
/*     */   }
/*     */ 
/*     */   public void setRequestType(NewsRequestType requestType) {
/*  63 */     if (requestType != null)
/*  64 */       put("req_type", requestType.toString());
/*     */   }
/*     */ 
/*     */   public NewsRequestType getRequestType()
/*     */   {
/*  69 */     String str = getString("req_type");
/*  70 */     if (str != null) {
/*  71 */       return NewsRequestType.valueOf(str);
/*     */     }
/*  73 */     return null;
/*     */   }
/*     */ 
/*     */   public void setNewsSource(NewsSource source) {
/*  77 */     if (source != null)
/*  78 */       put("news_src", source.toString());
/*     */   }
/*     */ 
/*     */   public NewsSource getSource()
/*     */   {
/*  83 */     String str = getString("news_src");
/*  84 */     if (str != null) {
/*  85 */       return NewsSource.valueOf(str);
/*     */     }
/*  87 */     return null;
/*     */   }
/*     */ 
/*     */   public void setHot(boolean hot) {
/*  91 */     put("hot", hot);
/*     */   }
/*     */ 
/*     */   public boolean isHot() {
/*  95 */     String hotStr = getString("hot");
/*  96 */     if (hotStr != null) {
/*  97 */       return Boolean.parseBoolean(hotStr);
/*     */     }
/*  99 */     return false;
/*     */   }
/*     */ 
/*     */   public void setMarketSectors(Set<MarketSector> markets) {
/* 103 */     if (markets != null) {
/* 104 */       JSONArray jsMarkets = new JSONArray(markets);
/* 105 */       put("markets", jsMarkets);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<MarketSector> getMarketSectors() {
/* 110 */     JSONArray jsMarkets = getJSONArray("markets");
/* 111 */     if (jsMarkets != null) {
/* 112 */       Set markets = new HashSet(jsMarkets.length());
/* 113 */       for (int i = 0; i < jsMarkets.length(); i++) {
/* 114 */         markets.add(MarketSector.valueOf(jsMarkets.getString(i)));
/*     */       }
/* 116 */       return markets;
/*     */     }
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */   public void setCurrencies(Set<Currency> currencies) {
/* 122 */     if (currencies != null) {
/* 123 */       JSONArray jsCurrencies = new JSONArray(currencies);
/* 124 */       put("currs", jsCurrencies);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<Currency> getCurrencies() {
/* 129 */     JSONArray jsCurrencies = getJSONArray("currs");
/* 130 */     if (jsCurrencies != null) {
/* 131 */       Set currencies = new HashSet(jsCurrencies.length());
/* 132 */       for (int i = 0; i < jsCurrencies.length(); i++) {
/* 133 */         currencies.add(Currency.valueOf(jsCurrencies.getString(i)));
/*     */       }
/* 135 */       return currencies;
/*     */     }
/* 137 */     return null;
/*     */   }
/*     */ 
/*     */   public void setIndicies(Set<StockIndex> indicies) {
/* 141 */     if (indicies != null) {
/* 142 */       JSONArray jsIndicies = new JSONArray(indicies);
/* 143 */       put("index", jsIndicies);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<StockIndex> getIndicies() {
/* 148 */     JSONArray jsIndicies = getJSONArray("index");
/* 149 */     if (jsIndicies != null) {
/* 150 */       Set indicies = new HashSet(jsIndicies.length());
/* 151 */       for (int i = 0; i < jsIndicies.length(); i++) {
/* 152 */         indicies.add(StockIndex.valueOf(jsIndicies.getString(i)));
/*     */       }
/* 154 */       return indicies;
/*     */     }
/* 156 */     return null;
/*     */   }
/*     */ 
/*     */   public void setGeoRegions(Set<GeoRegion> geoRegions) {
/* 160 */     if (geoRegions != null) {
/* 161 */       JSONArray jsRegions = new JSONArray(geoRegions);
/* 162 */       put("geo", jsRegions);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<GeoRegion> getGeoRegions() {
/* 167 */     JSONArray jsRegions = getJSONArray("geo");
/* 168 */     if (jsRegions != null) {
/* 169 */       Set regions = new HashSet(jsRegions.length());
/* 170 */       for (int i = 0; i < jsRegions.length(); i++) {
/* 171 */         regions.add(GeoRegion.valueOf(jsRegions.getString(i)));
/*     */       }
/* 173 */       return regions;
/*     */     }
/* 175 */     return null;
/*     */   }
/*     */ 
/*     */   public void setEventCategories(Set<EventCategory> eventCategories) {
/* 179 */     if (eventCategories != null) {
/* 180 */       JSONArray jsCategories = new JSONArray(eventCategories);
/* 181 */       put("categories", jsCategories);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<EventCategory> getEventCategories() {
/* 186 */     JSONArray jsCategories = getJSONArray("categories");
/* 187 */     if (jsCategories != null) {
/* 188 */       Set categories = new HashSet(jsCategories.length());
/* 189 */       for (int i = 0; i < jsCategories.length(); i++) {
/* 190 */         categories.add(EventCategory.valueOf(jsCategories.getString(i)));
/*     */       }
/* 192 */       return categories;
/*     */     }
/* 194 */     return null;
/*     */   }
/*     */ 
/*     */   public void setKeywords(Set<String> keyword) {
/* 198 */     if (keyword != null) {
/* 199 */       JSONArray jsKeyword = new JSONArray(keyword);
/* 200 */       put("keywd", jsKeyword);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<String> getKeywords() {
/* 205 */     JSONArray jsKeywords = getJSONArray("keywd");
/* 206 */     if (jsKeywords != null) {
/* 207 */       Set keywords = new HashSet(jsKeywords.length());
/* 208 */       for (int i = 0; i < jsKeywords.length(); i++) {
/* 209 */         keywords.add(jsKeywords.getString(i));
/*     */       }
/* 211 */       return keywords;
/*     */     }
/* 213 */     return null;
/*     */   }
/*     */ 
/*     */   public CalendarEvent.CalendarType getCalendarType() {
/* 217 */     String type = getString("cal_type");
/* 218 */     if (type != null) {
/* 219 */       return CalendarEvent.CalendarType.valueOf(type);
/*     */     }
/* 221 */     return null;
/*     */   }
/*     */ 
/*     */   public void setCalendarType(CalendarEvent.CalendarType calendarType) {
/* 225 */     if (calendarType != null)
/* 226 */       put("cal_type", calendarType.toString());
/*     */   }
/*     */ 
/*     */   public Date getFromDate()
/*     */   {
/* 231 */     return getDate("from_date");
/*     */   }
/*     */ 
/*     */   public void setFromDate(Date date) {
/* 235 */     putDate("from_date", date);
/*     */   }
/*     */ 
/*     */   public Date getToDate() {
/* 239 */     return getDate("to_date");
/*     */   }
/*     */ 
/*     */   public void setToDate(Date date) {
/* 243 */     putDate("to_date", date);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.NewsSubscribeRequest
 * JD-Core Version:    0.6.0
 */