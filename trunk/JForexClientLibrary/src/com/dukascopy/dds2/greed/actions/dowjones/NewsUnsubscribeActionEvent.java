/*    */ package com.dukascopy.dds2.greed.actions.dowjones;
/*    */ 
/*    */ import com.dukascopy.api.INewsFilter.NewsSource;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.transport.common.msg.news.NewsRequestType;
/*    */ import com.dukascopy.transport.common.msg.news.NewsSource;
/*    */ import com.dukascopy.transport.common.msg.news.NewsSubscribeRequest;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class NewsUnsubscribeActionEvent extends AppActionEvent
/*    */ {
/* 18 */   private static final Logger LOGGER = LoggerFactory.getLogger(NewsSubscribeActionEvent.class);
/*    */   private final INewsFilter.NewsSource newsSource;
/*    */ 
/*    */   public NewsUnsubscribeActionEvent(Object source, INewsFilter.NewsSource newsSource)
/*    */   {
/* 23 */     super(source, false, false);
/* 24 */     this.newsSource = newsSource;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/*    */     try {
/* 30 */       GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 32 */       NewsSubscribeRequest newsSubscribeRequest = new NewsSubscribeRequest();
/* 33 */       newsSubscribeRequest.setRequestType(NewsRequestType.UNSUBSCRIBE);
/* 34 */       newsSubscribeRequest.setNewsSource(NewsSource.valueOf(this.newsSource.name()));
/*    */ 
/* 36 */       LOGGER.debug("Unsubscribing : " + this.newsSource);
/*    */ 
/* 38 */       transport.controlRequest(newsSubscribeRequest);
/*    */     } catch (Exception ex) {
/* 40 */       LOGGER.warn("Error while unsubscribing from " + this.newsSource + " : " + ex.getMessage(), ex);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.dowjones.NewsUnsubscribeActionEvent
 * JD-Core Version:    0.6.0
 */