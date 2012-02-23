/*    */ package com.dukascopy.dds2.greed.actions.dowjones;
/*    */ 
/*    */ import com.dukascopy.api.INewsFilter;
/*    */ import com.dukascopy.api.INewsFilter.Country;
/*    */ import com.dukascopy.api.INewsFilter.NewsSource;
/*    */ import com.dukascopy.api.INewsFilter.Region;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.util.EnumConverter;
/*    */ import com.dukascopy.transport.common.msg.news.CalendarEvent.CalendarType;
/*    */ import com.dukascopy.transport.common.msg.news.Currency;
/*    */ import com.dukascopy.transport.common.msg.news.EventCategory;
/*    */ import com.dukascopy.transport.common.msg.news.GeoRegion;
/*    */ import com.dukascopy.transport.common.msg.news.MarketSector;
/*    */ import com.dukascopy.transport.common.msg.news.NewsRequestType;
/*    */ import com.dukascopy.transport.common.msg.news.NewsSource;
/*    */ import com.dukascopy.transport.common.msg.news.NewsSubscribeRequest;
/*    */ import com.dukascopy.transport.common.msg.news.StockIndex;
/*    */ import java.util.Arrays;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class NewsSubscribeActionEvent extends AppActionEvent
/*    */ {
/* 31 */   private static final Logger LOGGER = LoggerFactory.getLogger(NewsSubscribeActionEvent.class);
/*    */   private final INewsFilter newsFilter;
/*    */ 
/*    */   public NewsSubscribeActionEvent(Object source, INewsFilter newsFilter)
/*    */   {
/* 36 */     super(source, false, false);
/* 37 */     this.newsFilter = newsFilter;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/*    */     try {
/* 43 */       GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 45 */       NewsSubscribeRequest newsSubscribeRequest = new NewsSubscribeRequest();
/*    */ 
/* 47 */       newsSubscribeRequest.setRequestType(NewsRequestType.SUBSCRIBE);
/* 48 */       newsSubscribeRequest.setNewsSource(NewsSource.valueOf(this.newsFilter.getNewsSource().name()));
/* 49 */       newsSubscribeRequest.setHot(this.newsFilter.isOnlyHot());
/*    */ 
/* 51 */       Set countries = new HashSet();
/*    */ 
/* 53 */       for (INewsFilter.Country country : this.newsFilter.getCountries()) {
/* 54 */         if (country.region == INewsFilter.Region.Combined)
/* 55 */           countries.addAll(Arrays.asList(country.countries));
/*    */         else {
/* 57 */           countries.add(country);
/*    */         }
/*    */       }
/*    */ 
/* 61 */       newsSubscribeRequest.setGeoRegions(EnumConverter.convert(countries, GeoRegion.class));
/* 62 */       newsSubscribeRequest.setMarketSectors(EnumConverter.convert(this.newsFilter.getMarketSectors(), MarketSector.class));
/* 63 */       newsSubscribeRequest.setIndicies(EnumConverter.convert(this.newsFilter.getStockIndicies(), StockIndex.class));
/* 64 */       newsSubscribeRequest.setCurrencies(EnumConverter.convert(this.newsFilter.getCurrencies(), Currency.class));
/* 65 */       newsSubscribeRequest.setEventCategories(EnumConverter.convert(this.newsFilter.getEventCategories(), EventCategory.class));
/* 66 */       newsSubscribeRequest.setKeywords(this.newsFilter.getKeywords());
/* 67 */       newsSubscribeRequest.setFromDate(this.newsFilter.getFrom());
/* 68 */       newsSubscribeRequest.setToDate(this.newsFilter.getTo());
/* 69 */       newsSubscribeRequest.setCalendarType((CalendarEvent.CalendarType)EnumConverter.convert(this.newsFilter.getType(), CalendarEvent.CalendarType.class));
/*    */ 
/* 71 */       LOGGER.debug("Subscribing : " + this.newsFilter + "\n" + newsSubscribeRequest);
/*    */ 
/* 73 */       transport.controlRequest(newsSubscribeRequest);
/*    */     } catch (Exception ex) {
/* 75 */       LOGGER.warn("Error while subscribing to news : " + ex.getMessage(), ex);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.dowjones.NewsSubscribeActionEvent
 * JD-Core Version:    0.6.0
 */