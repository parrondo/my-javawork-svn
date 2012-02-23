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
/*    */ public class MOrderTakeProfit extends AgentBase.CommonExecution
/*    */ {
/* 13 */   private static Logger log = LoggerFactory.getLogger(MOrderTakeProfit.class);
/*    */ 
/*    */   public double execute(int id) throws MTAgentException {
/* 16 */     double returnValue = 0.0D;
/*    */ 
/* 18 */     Integer mtId = new Integer(id);
/* 19 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 20 */     if (msg != null) {
/* 21 */       if (msg.getTakeProfitOrder() != null) {
/* 22 */         if (msg.getTakeProfitOrder().getPriceTrailingLimit() != null) {
/* 23 */           returnValue = msg.getTakeProfitOrder().getPriceTrailingLimit().getValue().doubleValue();
/*    */         }
/*    */         else {
/* 26 */           returnValue = Double.parseDouble((String)msg.getStopLossOrder().getProperty("priceStop"));
/*    */         }
/*    */       }
/*    */ 
/* 30 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 32 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderTakeProfit
 * JD-Core Version:    0.6.0
 */