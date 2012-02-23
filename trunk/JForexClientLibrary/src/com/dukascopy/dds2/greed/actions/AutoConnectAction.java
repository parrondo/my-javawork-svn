/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedConnectionUtils;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient.TRANSPORT_RC;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class AutoConnectAction extends AppActionEvent
/*    */ {
/*    */   private static Logger LOGGER;
/*    */   private String accountName;
/*    */   private String password;
/*    */   private String instanceId;
/*    */ 
/*    */   public AutoConnectAction()
/*    */   {
/* 23 */     super("Connect", true, false);
/*    */ 
/* 25 */     this.accountName = System.getProperty("jnlp.client.username");
/* 26 */     this.password = System.getProperty("jnlp.client.password");
/* 27 */     this.instanceId = ((String)GreedContext.getConfig("SESSION_ID"));
/*    */   }
/*    */ 
/*    */   public AutoConnectAction(String login, String password)
/*    */   {
/* 32 */     super("Connect", false, false);
/* 33 */     this.accountName = login;
/* 34 */     this.password = password;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 39 */     GreedContext.setConfig("account_name", this.accountName);
/* 40 */     GreedContext.setConfig(" ", this.password);
/* 41 */     GreedContext.setConfig("TICKET", null);
/*    */ 
/* 43 */     LOGGER.debug("Authenticating ...");
/*    */ 
/* 45 */     ticketAuth();
/*    */   }
/*    */ 
/*    */   public void ticketAuth()
/*    */   {
/* 50 */     assert (this.instanceId != null);
/*    */ 
/* 52 */     String httpResponse = GreedConnectionUtils.getTicketAndAPIUrl();
/*    */ 
/* 54 */     GreedTransportClient.TRANSPORT_RC validationResult = GreedConnectionUtils.validateResponse(httpResponse);
/*    */ 
/* 57 */     if (validationResult != GreedTransportClient.TRANSPORT_RC.OK) {
/* 58 */       GreedConnectionUtils.wrongAuth(validationResult);
/* 59 */       return;
/*    */     }
/*    */ 
/* 63 */     String[] urlAndTicket = httpResponse.split("@");
/* 64 */     GreedContext.publishEvent(new PlatformInitAction(this, urlAndTicket[1], urlAndTicket[0]));
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 15 */     LOGGER = LoggerFactory.getLogger(AutoConnectAction.class);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.AutoConnectAction
 * JD-Core Version:    0.6.0
 */