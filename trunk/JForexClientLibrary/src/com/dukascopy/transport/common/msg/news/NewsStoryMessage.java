/*     */ package com.dukascopy.transport.common.msg.news;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.datafeed.AbstractDFSMessage;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class NewsStoryMessage extends AbstractDFSMessage
/*     */ {
/*     */   public static final String TYPE = "news_story";
/*     */   public static final String UUID = "uuid";
/*     */   public static final String PUBLISH_DATE = "pub_date";
/*     */   public static final String NEWS_SOURCE = "news_src";
/*     */   public static final String NEWS_ID = "news_id";
/*     */   public static final String HOT = "hot";
/*     */   public static final String MARKET_SECTORS = "markets";
/*     */   public static final String CURRENCIES = "currs";
/*     */   public static final String INDICIES = "index";
/*     */   public static final String GEO = "geo";
/*     */   public static final String HEADER = "header";
/*     */   public static final String CONTENT = "content";
/*     */   public static final String COPYRIGHT = "c";
/*     */   public static final String END = "end";
/*     */   private JSONObject content;
/*     */ 
/*     */   public NewsStoryMessage()
/*     */   {
/*  46 */     setType("news_story");
/*     */   }
/*     */ 
/*     */   public NewsStoryMessage(ProtocolMessage message) {
/*  50 */     super(message);
/*  51 */     setType("news_story");
/*  52 */     put("pub_date", message.getString("pub_date"));
/*  53 */     put("news_src", message.getString("news_src"));
/*  54 */     put("news_id", message.getString("news_id"));
/*  55 */     put("uuid", message.getString("uuid"));
/*  56 */     put("hot", message.getString("hot"));
/*  57 */     put("markets", message.getJSONArray("markets"));
/*  58 */     put("currs", message.getJSONArray("currs"));
/*  59 */     put("index", message.getJSONArray("index"));
/*  60 */     put("geo", message.getJSONArray("geo"));
/*  61 */     put("header", message.getString("header"));
/*  62 */     put("content", message.getJSONObject("content"));
/*  63 */     put("c", message.getString("c"));
/*  64 */     put("end", message.getString("end"));
/*     */   }
/*     */ 
/*     */   public void setNewsSource(NewsSource source) {
/*  68 */     if (source != null)
/*  69 */       put("news_src", source.toString());
/*     */   }
/*     */ 
/*     */   public NewsSource getSource()
/*     */   {
/*  74 */     String str = getString("news_src");
/*  75 */     if (str != null) {
/*  76 */       return NewsSource.valueOf(str);
/*     */     }
/*  78 */     return null;
/*     */   }
/*     */ 
/*     */   public void setNewsId(String newsId) {
/*  82 */     if (newsId != null)
/*  83 */       put("news_id", newsId);
/*     */   }
/*     */ 
/*     */   public String getNewsId()
/*     */   {
/*  88 */     return getString("news_id");
/*     */   }
/*     */ 
/*     */   public void setUUID(String uuid) {
/*  92 */     if (uuid != null)
/*  93 */       put("uuid", uuid);
/*     */   }
/*     */ 
/*     */   public String getUUID()
/*     */   {
/*  98 */     return getString("uuid");
/*     */   }
/*     */ 
/*     */   public Date getPublishDate() {
/* 102 */     return getDate("pub_date");
/*     */   }
/*     */ 
/*     */   public void setPublishDate(Date publishDate) {
/* 106 */     putDate("pub_date", publishDate);
/*     */   }
/*     */ 
/*     */   public void setHeader(String header) {
/* 110 */     if (header != null)
/* 111 */       put("header", escapeCharacters(header));
/*     */   }
/*     */ 
/*     */   public String getHeader()
/*     */   {
/* 116 */     return restoreCharacters(getString("header"));
/*     */   }
/*     */ 
/*     */   public void setHot(boolean hot) {
/* 120 */     put("hot", hot);
/*     */   }
/*     */ 
/*     */   public boolean isHot() {
/* 124 */     String hotStr = getString("hot");
/* 125 */     if (hotStr != null) {
/* 126 */       return Boolean.parseBoolean(hotStr);
/*     */     }
/* 128 */     return false;
/*     */   }
/*     */ 
/*     */   public void setContent(JSONObject jsonContent) {
/* 132 */     if (jsonContent != null)
/* 133 */       put("content", jsonContent);
/*     */   }
/*     */ 
/*     */   public JSONObject getContent()
/*     */   {
/* 138 */     if (this.content == null) {
/* 139 */       JSONObject json = getJSONObject("content");
/* 140 */       if (json != null) {
/* 141 */         if (getSource() == NewsSource.DJ_NEWSWIRES)
/* 142 */           this.content = new PlainContent(json);
/* 143 */         else if (getSource() == NewsSource.DJ_LIVE_CALENDAR) {
/* 144 */           this.content = new CalendarEvent(json);
/*     */         }
/*     */       }
/*     */     }
/* 148 */     return this.content;
/*     */   }
/*     */ 
/*     */   public void setCopyright(String copyright) {
/* 152 */     if (copyright != null)
/* 153 */       put("c", copyright);
/*     */   }
/*     */ 
/*     */   public String getCopyright()
/*     */   {
/* 158 */     return getString("c");
/*     */   }
/*     */ 
/*     */   public void setMarketSectors(Set<MarketSector> markets) {
/* 162 */     if (markets != null) {
/* 163 */       JSONArray jsMarkets = new JSONArray(markets);
/* 164 */       put("markets", jsMarkets);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<MarketSector> getMarketSectors() {
/* 169 */     JSONArray jsMarkets = getJSONArray("markets");
/* 170 */     if (jsMarkets != null) {
/* 171 */       Set markets = new HashSet(jsMarkets.length());
/* 172 */       for (int i = 0; i < jsMarkets.length(); i++) {
/* 173 */         markets.add(MarketSector.valueOf(jsMarkets.getString(i)));
/*     */       }
/* 175 */       return markets;
/*     */     }
/* 177 */     return null;
/*     */   }
/*     */ 
/*     */   public void setCurrencies(Set<Currency> currencies) {
/* 181 */     if (currencies != null) {
/* 182 */       JSONArray jsCurrencies = new JSONArray(currencies);
/* 183 */       put("currs", jsCurrencies);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<Currency> getCurrencies() {
/* 188 */     JSONArray jsCurrencies = getJSONArray("currs");
/* 189 */     if (jsCurrencies != null) {
/* 190 */       Set currencies = new HashSet(jsCurrencies.length());
/* 191 */       for (int i = 0; i < jsCurrencies.length(); i++) {
/* 192 */         currencies.add(Currency.valueOf(jsCurrencies.getString(i)));
/*     */       }
/* 194 */       return currencies;
/*     */     }
/* 196 */     return null;
/*     */   }
/*     */ 
/*     */   public void setIndicies(Set<StockIndex> indicies) {
/* 200 */     if (indicies != null) {
/* 201 */       JSONArray jsIndicies = new JSONArray(indicies);
/* 202 */       put("index", jsIndicies);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<StockIndex> getIndicies() {
/* 207 */     JSONArray jsIndicies = getJSONArray("index");
/* 208 */     if (jsIndicies != null) {
/* 209 */       Set indicies = new HashSet(jsIndicies.length());
/* 210 */       for (int i = 0; i < jsIndicies.length(); i++) {
/* 211 */         indicies.add(StockIndex.valueOf(jsIndicies.getString(i)));
/*     */       }
/* 213 */       return indicies;
/*     */     }
/* 215 */     return null;
/*     */   }
/*     */ 
/*     */   public void setGeoRegions(Set<GeoRegion> geoRegions)
/*     */   {
/* 220 */     if (geoRegions != null) {
/* 221 */       JSONArray jsRegions = new JSONArray(geoRegions);
/* 222 */       put("geo", jsRegions);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<GeoRegion> getGeoRegions() {
/* 227 */     JSONArray jsRegions = getJSONArray("geo");
/* 228 */     if (jsRegions != null) {
/* 229 */       Set regions = new HashSet(jsRegions.length());
/* 230 */       for (int i = 0; i < jsRegions.length(); i++) {
/* 231 */         regions.add(GeoRegion.valueOf(jsRegions.getString(i)));
/*     */       }
/* 233 */       return regions;
/*     */     }
/* 235 */     return null;
/*     */   }
/*     */ 
/*     */   public void setEndOfStory(boolean end) {
/* 239 */     put("end", end);
/*     */   }
/*     */ 
/*     */   public boolean isEndOfStory() {
/* 243 */     String endStr = getString("end");
/* 244 */     if (endStr != null) {
/* 245 */       return Boolean.parseBoolean(endStr);
/*     */     }
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   protected static String escapeCharacters(String str) {
/* 251 */     if (str != null) {
/* 252 */       str = str.replace('"', '\'');
/* 253 */       str = str.replace("\n", "#nln#");
/* 254 */       str = str.replace("{", "#lcb#");
/* 255 */       str = str.replace("}", "#rcb#");
/* 256 */       str = str.replace("[", "#lbb#");
/* 257 */       str = str.replace("]", "#rbb#");
/*     */     }
/* 259 */     return str;
/*     */   }
/*     */ 
/*     */   protected static String restoreCharacters(String str) {
/* 263 */     if (str != null) {
/* 264 */       str = str.replace("#nln#", "\n");
/* 265 */       str = str.replace("#lcb#", "{");
/* 266 */       str = str.replace("#rcb#", "}");
/* 267 */       str = str.replace("#lbb#", "[");
/* 268 */       str = str.replace("#rbb#", "]");
/*     */     }
/* 270 */     return str;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.NewsStoryMessage
 * JD-Core Version:    0.6.0
 */