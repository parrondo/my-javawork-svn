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
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.util.Date;
/*    */ import java.util.List;
/*    */ import javax.swing.SwingUtilities;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MassOrderGroupCloseAction extends AppActionEvent
/*    */ {
/* 26 */   private static final Logger LOGGER = LoggerFactory.getLogger(MassOrderGroupCloseAction.class);
/*    */   private OrderGroupMessage group;
/*    */   private ProtocolMessage response;
/*    */ 
/*    */   public MassOrderGroupCloseAction(Object source, OrderGroupMessage group)
/*    */   {
/* 32 */     super(source, false, false);
/* 33 */     this.group = group;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 38 */     if (GreedContext.isReadOnly()) {
/* 39 */       LOGGER.info("Not possible action for view mode");
/* 40 */       return;
/*    */     }
/*    */ 
/* 43 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/* 44 */     if (LOGGER.isDebugEnabled()) {
/* 45 */       LOGGER.debug("MASS CLOSE:" + this.group);
/*    */     }
/*    */ 
/* 48 */     PlatformInitUtils.setExtSysIdJUSTForOrderGroup(this.group);
/* 49 */     for (OrderMessage order : this.group.getOrders()) {
/* 50 */       PlatformInitUtils.setSecurityInfo4Order(order);
/*    */     }
/*    */ 
/* 53 */     this.response = client.controlRequest(this.group);
/*    */     try {
/* 55 */       SwingUtilities.invokeAndWait(new Runnable() {
/*    */         public void run() {
/* 57 */           if ((MassOrderGroupCloseAction.this.response instanceof OkResponseMessage)) {
/* 58 */             List orders = MassOrderGroupCloseAction.this.group.getOrders();
/* 59 */             StringBuffer sb = new StringBuffer("");
/* 60 */             for (OrderMessage order : orders) {
/* 61 */               if (sb.length() != 0)
/* 62 */                 sb.append(", ");
/* 63 */               sb.append(order.getOrderGroupId());
/*    */             }
/* 65 */             Notification notification = new Notification(new Date(), "Closing request sent for positions: " + sb);
/*    */ 
/* 67 */             PostMessageAction post = new PostMessageAction(this, notification);
/* 68 */             post.updateGuiAfter();
/* 69 */           } else if ((MassOrderGroupCloseAction.this.response instanceof ErrorResponseMessage)) {
/* 70 */             ErrorResponseMessage error = (ErrorResponseMessage)MassOrderGroupCloseAction.this.response;
/* 71 */             Notification notification = new Notification(MassOrderGroupCloseAction.this.response.getTimestamp(), error.getReason());
/*    */ 
/* 73 */             PostMessageAction post = new PostMessageAction(this, notification);
/* 74 */             post.updateGuiAfter();
/*    */           }
/*    */         } } );
/*    */     } catch (InterruptedException e) {
/* 79 */       LOGGER.error(e.getMessage(), e);
/*    */     } catch (InvocationTargetException e) {
/* 81 */       LOGGER.error(e.getMessage(), e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.MassOrderGroupCloseAction
 * JD-Core Version:    0.6.0
 */