/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.IMessage.Type;
/*     */ import com.dukascopy.api.INewsMessage;
/*     */ import com.dukascopy.api.INewsMessage.Action;
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.dds2.greed.util.EnumConverter;
/*     */ import com.dukascopy.transport.common.msg.news.Currency;
/*     */ import com.dukascopy.transport.common.msg.news.GeoRegion;
/*     */ import com.dukascopy.transport.common.msg.news.MarketSector;
/*     */ import com.dukascopy.transport.common.msg.news.StockIndex;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class PlatformNewsMessageImpl
/*     */   implements INewsMessage
/*     */ {
/*     */   private final String text;
/*     */   private final String copyright;
/*     */   private final String header;
/*     */   private final String newsId;
/*     */   private long publishDate;
/*     */   private boolean endOfStory;
/*     */   private boolean isHot;
/*     */   private final Set<Currency> currencies;
/*     */   private final Set<GeoRegion> geoRegions;
/*     */   private final Set<MarketSector> marketSectors;
/*     */   private final Set<StockIndex> stockIndicies;
/*  34 */   protected DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*     */ 
/*     */   public PlatformNewsMessageImpl(String text, String copyright, String header, String newsId, long publishDate, boolean endOfStory, boolean isHot, Set<Currency> currencies, Set<GeoRegion> geoRegions, Set<MarketSector> marketSectors, Set<StockIndex> stockIndicies)
/*     */   {
/*  49 */     this.text = text;
/*  50 */     this.copyright = copyright;
/*  51 */     this.header = header;
/*  52 */     this.newsId = newsId;
/*  53 */     this.publishDate = publishDate;
/*  54 */     this.endOfStory = endOfStory;
/*  55 */     this.isHot = isHot;
/*  56 */     this.currencies = currencies;
/*  57 */     this.geoRegions = geoRegions;
/*  58 */     this.marketSectors = marketSectors;
/*  59 */     this.stockIndicies = stockIndicies;
/*     */   }
/*     */ 
/*     */   public IOrder getOrder()
/*     */   {
/*  66 */     return null;
/*     */   }
/*     */ 
/*     */   public String getContent()
/*     */   {
/*  71 */     return this.text;
/*     */   }
/*     */ 
/*     */   public String getCopyright() {
/*  75 */     return this.copyright;
/*     */   }
/*     */ 
/*     */   public String getHeader() {
/*  79 */     return this.header;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  84 */     return this.newsId;
/*     */   }
/*     */ 
/*     */   public long getPublishDate() {
/*  88 */     return this.publishDate;
/*     */   }
/*     */ 
/*     */   public boolean isEndOfStory() {
/*  92 */     return this.endOfStory;
/*     */   }
/*     */ 
/*     */   public boolean isHot()
/*     */   {
/*  97 */     return this.isHot;
/*     */   }
/*     */ 
/*     */   public INewsMessage.Action getAction()
/*     */   {
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */   public Set<String> getCurrencies() {
/* 106 */     return EnumConverter.convert(this.currencies);
/*     */   }
/*     */ 
/*     */   public Set<String> getGeoRegions() {
/* 110 */     return EnumConverter.convert(this.geoRegions);
/*     */   }
/*     */ 
/*     */   public Set<String> getMarketSectors() {
/* 114 */     return EnumConverter.convert(this.marketSectors);
/*     */   }
/*     */ 
/*     */   public Set<String> getStockIndicies() {
/* 118 */     return EnumConverter.convert(this.stockIndicies);
/*     */   }
/*     */ 
/*     */   public long getCreationTime()
/*     */   {
/* 123 */     return this.publishDate;
/*     */   }
/*     */ 
/*     */   public IMessage.Type getType()
/*     */   {
/* 128 */     return IMessage.Type.NEWS;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 133 */     StringBuilder sb = new StringBuilder();
/* 134 */     sb.append(String.format("[MessageType %s]:%n \ttext : %s, publish date : %s, end of story : %s, hot : %s", new Object[] { getType(), getHeader(), this.df.format(Long.valueOf(getPublishDate())), Boolean.valueOf(isEndOfStory()), Boolean.valueOf(isHot()) }));
/*     */ 
/* 136 */     sb.append(String.format("%n", new Object[0])).append(getMetaDataInfo());
/* 137 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   protected String getMetaDataInfo() {
/* 141 */     return String.format("\tMeta info:%n\t\tStock Indicies: %s, Regions: %s, Market sectors: %s, Currencies: %s", new Object[] { getStockIndicies(), getGeoRegions(), getMarketSectors(), getCurrencies() });
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.PlatformNewsMessageImpl
 * JD-Core Version:    0.6.0
 */