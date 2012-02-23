/*    */ package com.dukascopy.transport.common.msg.news;
/*    */ 
/*    */ public enum MarketSector
/*    */ {
/*  5 */   NCY("Consumer Goods"), 
/*  6 */   TEC("Technology"), 
/*  7 */   UTI("utilities"), 
/*  8 */   BSC("Basic Materials"), 
/*  9 */   CYC("Consumer Services"), 
/* 10 */   ENE("Oil & Gas"), 
/* 11 */   FCL("Financial"), 
/* 12 */   IDU("Industrials"), 
/* 13 */   HCR("Healf Care"), 
/* 14 */   TEL("Telecommunications");
/*    */ 
/*    */   public static final String PREFIX = "M";
/*    */   private String name;
/*    */ 
/* 21 */   private MarketSector(String name) { this.name = name; }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 25 */     return this.name;
/*    */   }
/*    */ 
/*    */   public static MarketSector toMarketSector(String value) {
/* 29 */     MarketSector marketSector = null;
/*    */     try {
/* 31 */       marketSector = valueOf(value); } catch (IllegalArgumentException exc) {
/*    */     }
/* 33 */     return marketSector;
/*    */   }
/*    */ 
/*    */   public String getCode() {
/* 37 */     return "M" + '/' + name();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.MarketSector
 * JD-Core Version:    0.6.0
 */