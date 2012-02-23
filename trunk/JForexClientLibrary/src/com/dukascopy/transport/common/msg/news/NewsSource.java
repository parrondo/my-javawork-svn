/*    */ package com.dukascopy.transport.common.msg.news;
/*    */ 
/*    */ public enum NewsSource
/*    */ {
/*  5 */   DJ_NEWSWIRES("Dow Jones Newswires", "DN"), 
/*  6 */   DJ_LIVE_CALENDAR("Dow Jones Live Calendar", "CD"), 
/*  7 */   ACM_MARKET_NEWS("Acquire Media Market News", "ACM");
/*    */ 
/*    */   private String name;
/*    */   private String provider;
/*    */ 
/* 14 */   private NewsSource(String name, String provider) { this.name = name;
/* 15 */     this.provider = provider; }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 19 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String getProvider() {
/* 23 */     return this.provider;
/*    */   }
/*    */ 
/*    */   public static NewsSource fromName(String name) {
/* 27 */     if (name.equals(DJ_NEWSWIRES.getName()))
/* 28 */       return DJ_NEWSWIRES;
/* 29 */     if (name.equals(DJ_LIVE_CALENDAR.getName()))
/* 30 */       return DJ_LIVE_CALENDAR;
/* 31 */     if (name.equals(ACM_MARKET_NEWS.getName())) {
/* 32 */       return ACM_MARKET_NEWS;
/*    */     }
/* 34 */     throw new IllegalArgumentException("Unknown news source name: " + name);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.NewsSource
 * JD-Core Version:    0.6.0
 */