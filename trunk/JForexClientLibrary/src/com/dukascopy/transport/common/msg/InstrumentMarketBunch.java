/*    */ package com.dukascopy.transport.common.msg;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*    */ import java.io.Serializable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class InstrumentMarketBunch
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 200712141030L;
/* 14 */   private Map<Long, Map<String, String>> markets = new HashMap();
/*    */ 
/*    */   public InstrumentMarketBunch(InstrumentMarketBunch marketBunch) {
/* 17 */     this.markets.putAll(marketBunch.getMarkets());
/*    */   }
/*    */ 
/*    */   protected Map<Long, Map<String, String>> getMarkets() {
/* 21 */     return this.markets;
/*    */   }
/*    */ 
/*    */   public InstrumentMarketBunch()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Set<Long> getTimeline()
/*    */   {
/* 30 */     return this.markets.keySet();
/*    */   }
/*    */ 
/*    */   public void addCurrencyMarket(Long time, String source, CurrencyMarket market) {
/* 34 */     synchronized (this.markets) {
/* 35 */       Map map = (Map)this.markets.get(time);
/* 36 */       if (map == null) {
/* 37 */         map = new HashMap();
/*    */       }
/* 39 */       map.put(source, market.toProtocolString());
/* 40 */       this.markets.put(time, map);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Map<String, CurrencyMarket> getMarketMap(Long time) {
/* 45 */     synchronized (this.markets) {
/* 46 */       Map m = new HashMap();
/* 47 */       Map map = (Map)this.markets.get(time);
/* 48 */       if (map != null) {
/* 49 */         for (String s : map.keySet()) {
/* 50 */           CurrencyMarket cm = (CurrencyMarket)ProtocolMessage.parse((String)map.get(s));
/* 51 */           m.put(s, cm);
/*    */         }
/*    */       }
/* 54 */       return m;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void addMarketMap(Long time, Map<String, CurrencyMarket> markets) {
/* 59 */     synchronized (markets) {
/* 60 */       Map m = new HashMap();
/* 61 */       for (String s : markets.keySet()) {
/* 62 */         CurrencyMarket cm = (CurrencyMarket)markets.get(s);
/* 63 */         m.put(s, cm.toProtocolString());
/*    */       }
/* 65 */       this.markets.put(time, m);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.InstrumentMarketBunch
 * JD-Core Version:    0.6.0
 */