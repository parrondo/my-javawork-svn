/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.api.JFException;
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.dds2.greed.mt.helpers.MTAPIHelpers;
/*    */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderSelect extends AgentBase.CommonExecution
/*    */ {
/* 19 */   private static Logger log = LoggerFactory.getLogger(MOrderSelect.class);
/*    */ 
/*    */   public boolean execute(int id, int index, int select, int pool) throws MTAgentException, JFException
/*    */   {
/* 23 */     boolean returnValue = false;
/* 24 */     if (pool == 1) {
/* 25 */       throw new MTAgentException(-23, "ARSP_HISTORY_NOT_SUPPORTED_MSG");
/*    */     }
/*    */ 
/* 28 */     OrderGroupMessage[] group = null;
/* 29 */     if (select == 1) {
/* 30 */       group = new OrderGroupMessage[1];
/* 31 */       group[0] = OrderMessageUtils.getOrderGroupById(String.valueOf(index));
/*    */     } else {
/* 33 */       group = MTAPIHelpers.getOrderGroupByRow(index, pool);
/*    */     }
/*    */ 
/* 36 */     if ((group != null) && (group[0] != null)) {
/* 37 */       putOrderGroup(Integer.valueOf(id), group[0]);
/* 38 */       returnValue = true;
/*    */     }
/* 40 */     setError(id, 0, "ERR_NO_ERROR_MSG");
/* 41 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderSelect
 * JD-Core Version:    0.6.0
 */