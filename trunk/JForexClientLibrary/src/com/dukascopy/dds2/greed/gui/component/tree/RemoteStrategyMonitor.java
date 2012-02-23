/*    */ package com.dukascopy.dds2.greed.gui.component.tree;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.RemoteStrategiesListResponseAction;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.strategy.StrategiesListRequestMessage;
/*    */ import com.dukascopy.transport.common.msg.strategy.StrategiesListResponseMessage;
/*    */ import com.dukascopy.transport.common.msg.strategy.StrategyProcessDescriptor;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.TimeoutException;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class RemoteStrategyMonitor extends Thread
/*    */ {
/* 29 */   private static final Logger LOGGER = LoggerFactory.getLogger(RemoteStrategyMonitor.class);
/*    */   private static final String THREAD_NAME = "Remote Strategy Monitoring thread";
/* 31 */   private static final long THREAD_PERIOD = TimeUnit.MINUTES.toMillis(2L);
/* 32 */   private static final long REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(5L);
/*    */ 
/*    */   public RemoteStrategyMonitor()
/*    */   {
/* 39 */     super("Remote Strategy Monitoring thread");
/* 40 */     setPriority(1);
/* 41 */     setDaemon(true);
/* 42 */     start();
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 47 */     LOGGER.debug("Started");
/*    */     do {
/*    */       try {
/* 50 */         GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/* 51 */         if (transport != null) {
/*    */           try {
/* 53 */             ProtocolMessage response = transport.controlSynchRequest(new StrategiesListRequestMessage(), REQUEST_TIMEOUT);
/* 54 */             if ((response instanceof StrategiesListResponseMessage)) {
/* 55 */               processResponse((StrategiesListResponseMessage)response);
/*    */             }
/*    */             else
/*    */             {
/* 59 */               cleanRemotelyRunningStrategies();
/*    */             }
/*    */           } catch (TimeoutException ex) {
/* 62 */             LOGGER.debug("Timeout while requesting remote strategies list", ex);
/*    */           }
/*    */           try
/*    */           {
/* 66 */             Thread.sleep(THREAD_PERIOD);
/*    */           } catch (InterruptedException e) {
/* 68 */             break;
/*    */           }
/*    */         } else {
/*    */           try {
/* 72 */             Thread.sleep(200L);
/*    */           } catch (InterruptedException e) {
/* 74 */             break;
/*    */           }
/*    */         }
/*    */       } catch (Exception ex) {
/* 78 */         if (LOGGER.isDebugEnabled())
/* 79 */           LOGGER.error("Error while requesting remote strategies list", ex);
/*    */       }
/*    */     }
/* 82 */     while (!isInterrupted());
/*    */   }
/*    */ 
/*    */   protected void processResponse(StrategiesListResponseMessage response) {
/* 86 */     updateStrategiesList(response.getStrategies());
/*    */   }
/*    */ 
/*    */   protected void cleanRemotelyRunningStrategies() {
/* 90 */     updateStrategiesList(new ArrayList());
/*    */   }
/*    */ 
/*    */   private void updateStrategiesList(Collection<StrategyProcessDescriptor> descriptors) {
/* 94 */     RemoteStrategiesListResponseAction strategiesListResponseAction = new RemoteStrategiesListResponseAction(this, descriptors);
/* 95 */     GreedContext.publishEvent(strategiesListResponseAction);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.RemoteStrategyMonitor
 * JD-Core Version:    0.6.0
 */