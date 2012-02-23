/*    */ package com.dukascopy.transport.common.msg.group;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.json.JSONArray;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class MarketNewsMessageGroup extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "news_headers";
/* 20 */   private final String NEWS = "data";
/*    */ 
/*    */   public MarketNewsMessageGroup() {
/* 23 */     setType("news_headers");
/*    */   }
/*    */ 
/*    */   public MarketNewsMessageGroup(ProtocolMessage msg) {
/* 27 */     super(msg);
/* 28 */     setType("news_headers");
/* 29 */     put("data", msg.getJSONArray("data"));
/*    */   }
/*    */ 
/*    */   public void setType(String type) {
/* 33 */     put("type", type);
/*    */   }
/*    */ 
/*    */   public List<MarketNews> getMarketNewsList() {
/* 37 */     List news = new ArrayList();
/* 38 */     JSONArray newsArray = getJSONArray("data");
/* 39 */     int i = 0; for (int n = newsArray.length(); i < n; i++) {
/* 40 */       JSONObject jNews = newsArray.getJSONObject(i);
/* 41 */       news.add(new MarketNews(jNews.get("ordindex").toString(), (String)jNews.get("news_date"), (String)jNews.get("head_news")));
/*    */     }
/*    */ 
/* 45 */     return news;
/*    */   }
/*    */   public class MarketNews { private String ordindex;
/*    */     private String news_date;
/*    */     private String head_news;
/*    */ 
/* 54 */     protected MarketNews(String ordIndex, String newsDate, String headNews) { this.ordindex = ordIndex; this.news_date = newsDate; this.head_news = headNews; } 
/*    */     public String getOrdIndex() {
/* 56 */       return this.ordindex; } 
/* 57 */     public String getNewsDate() { return this.news_date; } 
/* 58 */     public String getHeadNews() { return this.head_news;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.group.MarketNewsMessageGroup
 * JD-Core Version:    0.6.0
 */