/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.transport.common.model.type.Money;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import java.math.BigDecimal;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderLots extends AgentBase.CommonExecution
/*    */ {
/* 13 */   private static Logger log = LoggerFactory.getLogger(MOrderLots.class);
/*    */ 
/*    */   public double execute(int id) throws MTAgentException {
/* 16 */     double returnValue = 0.0D;
/*    */ 
/* 18 */     Integer mtId = new Integer(id);
/*    */ 
/* 20 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 21 */     if (msg != null) {
/* 22 */       returnValue = msg.getOpeningOrder().getAmount().getValue().doubleValue() / 1000000.0D;
/*    */ 
/* 24 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 26 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderLots
 * JD-Core Version:    0.6.0
 */