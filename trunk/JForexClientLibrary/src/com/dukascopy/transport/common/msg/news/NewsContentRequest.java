/*    */ package com.dukascopy.transport.common.msg.news;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.datafeed.AbstractDFSMessage;
/*    */ 
/*    */ public class NewsContentRequest extends AbstractDFSMessage
/*    */ {
/*    */   public static final String TYPE = "news_content_req";
/*    */   public static final String NEWS_SOURCE = "news_src";
/*    */   public static final String NEWS_ID = "news_id";
/*    */ 
/*    */   public NewsContentRequest()
/*    */   {
/* 16 */     setType("news_content_req");
/*    */   }
/*    */ 
/*    */   public NewsContentRequest(ProtocolMessage message) {
/* 20 */     super(message);
/* 21 */     setType("news_content_req");
/* 22 */     put("news_src", message.getString("news_src"));
/* 23 */     put("news_id", message.getString("news_id"));
/*    */   }
/*    */ 
/*    */   public void setNewsSource(NewsSource source) {
/* 27 */     if (source != null)
/* 28 */       put("news_src", source.toString());
/*    */   }
/*    */ 
/*    */   public NewsSource getSource()
/*    */   {
/* 33 */     String str = getString("news_src");
/* 34 */     if (str != null) {
/* 35 */       return NewsSource.valueOf(str);
/*    */     }
/* 37 */     return null;
/*    */   }
/*    */ 
/*    */   public void setNewsId(String newsId) {
/* 41 */     if (newsId != null)
/* 42 */       put("news_id", newsId);
/*    */   }
/*    */ 
/*    */   public String getNewsId()
/*    */   {
/* 47 */     return getString("news_id");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.NewsContentRequest
 * JD-Core Version:    0.6.0
 */