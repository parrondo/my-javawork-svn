/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderSymbol extends AgentBase.CommonExecution
/*    */ {
/* 13 */   private static Logger log = LoggerFactory.getLogger(MOrderSymbol.class);
/*    */ 
/*    */   public String execute(int id) throws MTAgentException {
/* 16 */     String returnValue = "";
/*    */ 
/* 18 */     Integer mtId = new Integer(id);
/* 19 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 20 */     if (msg != null) {
/* 21 */       StringBuffer buff = new StringBuffer();
/* 22 */       buff.append(msg.getOpeningOrder().getInstrument().substring(0, 3));
/* 23 */       buff.append(msg.getOpeningOrder().getInstrument().substring(4, msg.getOpeningOrder().getInstrument().length()));
/*    */ 
/* 26 */       returnValue = buff.toString();
/* 27 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 29 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderSymbol
 * JD-Core Version:    0.6.0
 */