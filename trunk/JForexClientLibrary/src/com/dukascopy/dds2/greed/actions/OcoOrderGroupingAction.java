/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*    */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class OcoOrderGroupingAction extends AppActionEvent
/*    */ {
/* 17 */   private static final Logger LOGGER = LoggerFactory.getLogger(OcoOrderGroupingAction.class);
/*    */   private ProtocolMessage response;
/*    */   private OrderGroupMessage ocoGroup;
/*    */ 
/*    */   public OcoOrderGroupingAction(Object source, OrderGroupMessage ocoGroup)
/*    */   {
/* 23 */     super(source, false, true);
/* 24 */     this.ocoGroup = ocoGroup;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 29 */     if (GreedContext.isReadOnly()) {
/* 30 */       LOGGER.info("Not possible action for view mode");
/* 31 */       return;
/*    */     }
/*    */ 
/* 34 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/* 35 */     if (LOGGER.isDebugEnabled()) {
/* 36 */       LOGGER.info("Oco group: " + this.ocoGroup);
/*    */     }
/*    */ 
/* 39 */     if (this.ocoGroup.getOrders() != null) {
/* 40 */       for (OrderMessage order : this.ocoGroup.getOrders()) {
/* 41 */         PlatformInitUtils.setSecurityInfo4Order(order);
/*    */       }
/*    */     }
/*    */ 
/* 45 */     this.response = client.controlRequest(this.ocoGroup);
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter() {
/* 49 */     if (GreedContext.isReadOnly()) {
/* 50 */       LOGGER.info("Not possible action for view mode");
/* 51 */       return;
/*    */     }
/*    */ 
/* 54 */     if ((this.response instanceof OkResponseMessage)) {
/* 55 */       Notification notification = new Notification(null, "Oco grouping/ungrouping request accepted");
/*    */ 
/* 57 */       notification.setServerTimestamp(GreedContext.getPlatformTimeForLogger());
/* 58 */       PostMessageAction post = new PostMessageAction(this, notification);
/* 59 */       GreedContext.publishEvent(post);
/* 60 */     } else if ((this.response instanceof ErrorResponseMessage)) {
/* 61 */       ErrorResponseMessage error = (ErrorResponseMessage)this.response;
/* 62 */       Notification notification = new Notification(this.response.getTimestamp(), error.getReason());
/* 63 */       notification.setPriority("ERROR");
/* 64 */       PostMessageAction post = new PostMessageAction(this, notification);
/* 65 */       GreedContext.publishEvent(post);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.OcoOrderGroupingAction
 * JD-Core Version:    0.6.0
 */