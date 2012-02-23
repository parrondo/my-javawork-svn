/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedClientListener;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import com.dukascopy.transport.common.msg.request.QuitRequestMessage;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class KickAction extends AppActionEvent
/*    */ {
/* 16 */   private static final Logger LOGGER = LoggerFactory.getLogger(KickAction.class.getName());
/* 17 */   private static final long DELAY = TimeUnit.SECONDS.toMillis(3L);
/*    */ 
/* 19 */   private final GreedClientListener greedClientListener = (GreedClientListener)GreedContext.get("clientListener");
/*    */ 
/*    */   public KickAction(Object source) {
/* 22 */     super(source, false, false);
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 27 */     GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 29 */     GreedContext.setConfig("logoff", Boolean.valueOf(true));
/*    */     try
/*    */     {
/* 32 */       transport.controlRequest(new QuitRequestMessage());
/*    */     } catch (Throwable ex) {
/* 34 */       LOGGER.error(ex.getMessage(), ex);
/*    */     }
/*    */ 
/* 37 */     transport.disconnect();
/* 38 */     transport.terminate();
/*    */ 
/* 40 */     Notification notification = new Notification("Disconnected.");
/* 41 */     PostMessageAction post = new PostMessageAction(this, notification);
/* 42 */     GreedContext.publishEvent(post);
/*    */     try
/*    */     {
/* 45 */       Thread.sleep(DELAY);
/*    */     } catch (InterruptedException ie) {
/*    */     }
/* 48 */     this.greedClientListener.renewApiUrl();
/* 49 */     transport.connect();
/*    */ 
/* 51 */     if (transport.isOnline()) {
/* 52 */       Notification notificationConnected = new Notification("Connected.");
/* 53 */       PostMessageAction postConnected = new PostMessageAction(this, notificationConnected);
/* 54 */       GreedContext.publishEvent(postConnected);
/*    */     } else {
/* 56 */       GreedContext.setConfig("logoff", Boolean.valueOf(false));
/* 57 */       transport.connect();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.KickAction
 * JD-Core Version:    0.6.0
 */