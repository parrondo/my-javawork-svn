/*    */ package com.dukascopy.dds2.greed.actions.dowjones;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.transport.common.msg.news.NewsContentRequest;
/*    */ import com.dukascopy.transport.common.msg.news.NewsSource;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class NewsContentRequestActionEvent extends AppActionEvent
/*    */ {
/* 18 */   private static final Logger LOGGER = LoggerFactory.getLogger(NewsContentRequestActionEvent.class);
/*    */   private final String newsId;
/*    */ 
/*    */   public NewsContentRequestActionEvent(Object source, String newsId)
/*    */   {
/* 23 */     super(source, false, false);
/* 24 */     this.newsId = newsId;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/*    */     try {
/* 30 */       GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 32 */       NewsContentRequest newsContentRequest = new NewsContentRequest()
/*    */       {
/*    */       };
/* 37 */       LOGGER.debug("Requesting content for news # " + this.newsId);
/*    */ 
/* 39 */       transport.controlRequest(newsContentRequest);
/*    */     } catch (Exception ex) {
/* 41 */       LOGGER.warn("Error while requesting content for news # " + this.newsId, ex);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.dowjones.NewsContentRequestActionEvent
 * JD-Core Version:    0.6.0
 */