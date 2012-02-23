/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.api.IEngine.OrderCommand;
/*    */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.transport.common.model.type.OrderSide;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderType extends AgentBase.CommonExecution
/*    */ {
/* 16 */   private static Logger log = LoggerFactory.getLogger(MOrderType.class);
/*    */ 
/*    */   public int execute(int id) throws MTAgentException {
/* 19 */     int returnValue = -1;
/*    */ 
/* 21 */     Integer mtId = new Integer(id);
/*    */ 
/* 23 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 24 */     if (msg != null) {
/* 25 */       OrderSide side = msg.getOpeningOrder().getSide();
/* 26 */       if (side != null) {
/* 27 */         IEngine.OrderCommand cmd = OrdersProvider.convert(side, msg.getOpeningOrder().getStopDirection(), false, msg.getOpeningOrder().isPlaceOffer());
/*    */ 
/* 30 */         returnValue = cmd.ordinal();
/*    */       }
/* 32 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 34 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderType
 * JD-Core Version:    0.6.0
 */