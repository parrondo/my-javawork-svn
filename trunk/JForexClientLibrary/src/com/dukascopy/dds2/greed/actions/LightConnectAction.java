/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import java.util.Calendar;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class LightConnectAction extends AppActionEvent
/*    */ {
/* 16 */   private static final Logger LOGGER = LoggerFactory.getLogger(LightConnectAction.class);
/*    */   private GreedTransportClient transportClient;
/* 20 */   int reconnectTryCount = 0;
/*    */ 
/*    */   public LightConnectAction() {
/* 23 */     super("Connect", false, true);
/* 24 */     this.transportClient = ((GreedTransportClient)GreedContext.get("transportClient"));
/*    */   }
/*    */ 
/*    */   public LightConnectAction(int tryCount) {
/* 28 */     this();
/* 29 */     this.reconnectTryCount = tryCount;
/*    */   }
/*    */ 
/*    */   public void doAction() {
/*    */     try {
/* 34 */       if (LOGGER.isDebugEnabled()) {
/* 35 */         LOGGER.debug("tc connecting..");
/*    */       }
/* 37 */       this.transportClient.connect();
/* 38 */       if (LOGGER.isDebugEnabled())
/* 39 */         LOGGER.debug(".. connected ");
/*    */     }
/*    */     catch (IllegalStateException e) {
/* 42 */       LOGGER.error("ERROR connecting " + e.getMessage(), e);
/*    */     } catch (Exception e) {
/* 44 */       LOGGER.error("Connection error: " + e.getMessage(), e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 51 */     if (!this.transportClient.isOnline())
/*    */     {
/* 53 */       ReconnectInfoAction reconnectInfo = new ReconnectInfoAction(this);
/* 54 */       GreedContext.publishEvent(reconnectInfo);
/*    */ 
/* 56 */       if (this.reconnectTryCount > 0)
/*    */       {
/* 58 */         String message = null;
/* 59 */         message = this.reconnectTryCount <= 1 ? "Disconnected." : "Reconnecting...";
/*    */ 
/* 61 */         Notification notification = new Notification(Calendar.getInstance().getTime(), message);
/* 62 */         PostMessageAction post = new PostMessageAction(this, notification);
/* 63 */         GreedContext.publishEvent(post);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.LightConnectAction
 * JD-Core Version:    0.6.0
 */