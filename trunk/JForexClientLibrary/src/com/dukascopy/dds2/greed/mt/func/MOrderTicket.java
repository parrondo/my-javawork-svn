/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderTicket extends AgentBase.CommonExecution
/*    */ {
/* 13 */   private static Logger log = LoggerFactory.getLogger(MOrderTicket.class);
/*    */ 
/*    */   public int execute(int id) throws MTAgentException {
/* 16 */     int returnValue = 0;
/* 17 */     Integer mtId = new Integer(id);
/* 18 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 19 */     if (msg != null) {
/* 20 */       returnValue = Integer.parseInt(msg.getOrderGroupId());
/* 21 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 23 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderTicket
 * JD-Core Version:    0.6.0
 */