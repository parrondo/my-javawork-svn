/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import java.util.Date;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderOpenTime extends AgentBase.CommonExecution
/*    */ {
/* 13 */   private static Logger log = LoggerFactory.getLogger(MOrderOpenTime.class);
/*    */ 
/*    */   public long execute(int id) throws MTAgentException {
/* 16 */     long returnValue = 0L;
/*    */ 
/* 18 */     Integer mtId = new Integer(id);
/* 19 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 20 */     if (msg != null) {
/* 21 */       returnValue = msg.getOpeningOrder().getCreatedDate().getTime() / 1000L;
/* 22 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 24 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderOpenTime
 * JD-Core Version:    0.6.0
 */