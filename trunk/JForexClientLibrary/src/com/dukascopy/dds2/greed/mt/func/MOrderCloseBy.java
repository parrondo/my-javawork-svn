/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.api.JFException;
/*    */ import com.dukascopy.api.impl.connect.JForexAPI;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.dds2.greed.mt.helpers.MTAPIHelpers;
/*    */ import com.dukascopy.transport.common.model.type.Position;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*    */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderCloseBy extends AgentBase.CommonExecution
/*    */ {
/* 25 */   private static Logger log = LoggerFactory.getLogger(MOrderCloseBy.class);
/*    */ 
/*    */   public String execute(int id, int ticket, int opposite, long Color) throws MTAgentException, JFException
/*    */   {
/* 29 */     String label = "";
/*    */ 
/* 31 */     Set mergeOrderGroupIdList = new HashSet();
/* 32 */     OrderGroupMessage[] ticketGroup = MTAPIHelpers.getOrderGroupById(ticket);
/*    */ 
/* 34 */     OrderGroupMessage[] oppositeGroup = MTAPIHelpers.getOrderGroupById(opposite);
/*    */ 
/* 36 */     if ((ticketGroup != null) && (ticketGroup[0] != null)) {
/* 37 */       mergeOrderGroupIdList.add(ticketGroup[0].getPosition().getPositionID());
/* 38 */       label = ticketGroup[0].getExternalSysId();
/*    */     }
/*    */ 
/* 41 */     if (oppositeGroup[0] != null) {
/* 42 */       mergeOrderGroupIdList.add(oppositeGroup[0].getPosition().getPositionID());
/*    */     }
/*    */ 
/* 45 */     MergePositionsMessage mpm = JForexAPI.merge(null, label, mergeOrderGroupIdList);
/* 46 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*    */     try {
/* 48 */       ProtocolMessage submitResult = client.controlRequest(mpm);
/* 49 */       if ((submitResult instanceof ErrorResponseMessage)) {
/* 50 */         ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/* 51 */         throw new MTAgentException(-99, error.getReason());
/*    */       }
/*    */     }
/*    */     catch (Exception ex) {
/* 55 */       log.error(ex.getMessage(), ex);
/*    */     }
/* 57 */     setError(id, 0, "ERR_NO_ERROR_MSG");
/* 58 */     return label;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderCloseBy
 * JD-Core Version:    0.6.0
 */