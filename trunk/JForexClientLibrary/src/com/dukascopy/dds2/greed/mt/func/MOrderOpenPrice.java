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
/*    */ public class MOrderOpenPrice extends AgentBase.CommonExecution
/*    */ {
/* 14 */   private static Logger log = LoggerFactory.getLogger(MOrderOpenPrice.class);
/*    */ 
/*    */   public double execute(int id) throws MTAgentException {
/* 17 */     double returnValue = 0.0D;
/*    */ 
/* 19 */     Integer mtId = new Integer(id);
/*    */ 
/* 21 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 22 */     if (msg != null) {
/* 23 */       if (msg.getOpeningOrder().getPriceClient() != null)
/* 24 */         returnValue = msg.getOpeningOrder().getPriceClient().getValue().doubleValue();
/*    */       else {
/* 26 */         returnValue = Double.parseDouble((String)msg.getOpeningOrder().get("priceClient"));
/*    */       }
/* 28 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 30 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderOpenPrice
 * JD-Core Version:    0.6.0
 */