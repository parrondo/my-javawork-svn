/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.agent.DDSAgent;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*    */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*    */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*    */ import java.util.List;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MergePositionsAction extends AppActionEvent
/*    */ {
/* 22 */   private static final Logger LOGGER = LoggerFactory.getLogger(MergePositionsAction.class);
/*    */   private ProtocolMessage response;
/*    */   private String orderGroupIdListAsString;
/* 26 */   private String externalSysId = null;
/* 27 */   private String strategyId = null;
/*    */ 
/*    */   public MergePositionsAction(Object source, List<String> mergeOrderGroupIdList, String externalSysId, String strategyId) {
/* 30 */     super(source, false, true);
/* 31 */     StringBuilder delimSeparatedPosIdList = new StringBuilder();
/* 32 */     for (String orderGroupId : mergeOrderGroupIdList) {
/* 33 */       if (0 != delimSeparatedPosIdList.length()) {
/* 34 */         delimSeparatedPosIdList.append(";");
/*    */       }
/* 36 */       delimSeparatedPosIdList.append(orderGroupId);
/*    */     }
/* 38 */     this.externalSysId = externalSysId;
/* 39 */     this.strategyId = strategyId;
/* 40 */     this.orderGroupIdListAsString = delimSeparatedPosIdList.toString();
/*    */   }
/*    */ 
/*    */   public MergePositionsAction(Object source, List<String> mergeOrderGroupIdList) {
/* 44 */     this(source, mergeOrderGroupIdList, null, null);
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 49 */     if (GreedContext.isReadOnly()) {
/* 50 */       LOGGER.info("Not possible action for view mode");
/* 51 */       return;
/*    */     }
/*    */ 
/* 54 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/* 55 */     MergePositionsMessage mergeRequest = new MergePositionsMessage(this.orderGroupIdListAsString);
/*    */ 
/* 57 */     if (this.externalSysId == null) {
/* 58 */       this.externalSysId = DDSAgent.generateLabel(null);
/*    */     }
/* 60 */     mergeRequest.setExternalSysId(this.externalSysId);
/*    */ 
/* 62 */     if (this.strategyId != null) {
/* 63 */       mergeRequest.setStrategySysId(this.strategyId);
/*    */     }
/* 65 */     if (LOGGER.isDebugEnabled()) {
/* 66 */       LOGGER.info(new StringBuilder().append("MERGE: ").append(mergeRequest).toString());
/*    */     }
/*    */ 
/* 69 */     PlatformInitUtils.setExtSysIdForMergeMessage(mergeRequest);
/* 70 */     this.response = client.controlRequest(mergeRequest);
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 75 */     if (GreedContext.isReadOnly()) {
/* 76 */       LOGGER.info("Not possible action for view mode");
/* 77 */       return;
/*    */     }
/*    */ 
/* 80 */     if ((this.response instanceof OkResponseMessage)) {
/* 81 */       String posIdList = this.orderGroupIdListAsString.replaceAll(";", ", ");
/* 82 */       Notification notification = new Notification(null, new StringBuilder().append("Positions ").append(posIdList).append(": merge request accepted").toString());
/* 83 */       notification.setServerTimestamp(GreedContext.getPlatformTimeForLogger());
/* 84 */       PostMessageAction post = new PostMessageAction(this, notification);
/* 85 */       GreedContext.publishEvent(post);
/* 86 */     } else if ((this.response instanceof ErrorResponseMessage)) {
/* 87 */       ErrorResponseMessage error = (ErrorResponseMessage)this.response;
/* 88 */       Notification notification = new Notification(this.response.getTimestamp(), error.getReason());
/* 89 */       notification.setPriority("ERROR");
/* 90 */       PostMessageAction post = new PostMessageAction(this, notification);
/* 91 */       GreedContext.publishEvent(post);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.MergePositionsAction
 * JD-Core Version:    0.6.0
 */