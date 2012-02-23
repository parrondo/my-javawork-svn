/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.api.JFException;
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.transport.common.model.type.Money;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import java.math.BigDecimal;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderCommission extends AgentBase.CommonExecution
/*    */ {
/* 14 */   private static Logger log = LoggerFactory.getLogger(MOrderCommission.class);
/*    */ 
/*    */   public double execute(int id) throws MTAgentException, JFException {
/* 17 */     double returnValue = 0.0D;
/* 18 */     Integer mtId = new Integer(id);
/* 19 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 20 */     if ((msg != null) && (msg.getOpeningOrder().getOrderCommission() != null)) {
/* 21 */       returnValue = msg.getOpeningOrder().getOrderCommission().getValue().doubleValue();
/*    */     }
/* 23 */     setError(id, 0, "ERR_NO_ERROR_MSG");
/* 24 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderCommission
 * JD-Core Version:    0.6.0
 */