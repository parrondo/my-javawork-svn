/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TicketAutoConnectAction extends AppActionEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*  9 */   private static Logger LOGGER = LoggerFactory.getLogger(TicketAutoConnectAction.class);
/*    */   private String accountName;
/*    */   private String apiURL;
/*    */   private String instanceId;
/*    */   private String ticket;
/*    */ 
/*    */   public TicketAutoConnectAction()
/*    */   {
/* 17 */     super("Connect", true, false);
/*    */ 
/* 19 */     this.accountName = System.getProperty("jnlp.client.username");
/* 20 */     this.instanceId = System.getProperty("jnlp.api.sid");
/* 21 */     this.apiURL = System.getProperty("jnlp.api.url");
/* 22 */     this.ticket = System.getProperty("jnlp.auth.ticket");
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 28 */     GreedContext.setConfig("account_name", this.accountName);
/* 29 */     GreedContext.setConfig("TICKET", this.ticket);
/* 30 */     GreedContext.setConfig("SESSION_ID", this.instanceId);
/*    */ 
/* 32 */     LOGGER.debug("Authenticating ... user: " + this.accountName);
/*    */ 
/* 34 */     GreedContext.publishEvent(new PlatformInitAction("init", this.ticket, this.apiURL));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.TicketAutoConnectAction
 * JD-Core Version:    0.6.0
 */