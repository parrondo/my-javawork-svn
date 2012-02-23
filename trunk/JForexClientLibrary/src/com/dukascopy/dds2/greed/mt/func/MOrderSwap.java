/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderSwap extends AgentBase.CommonExecution
/*    */ {
/* 12 */   private static Logger log = LoggerFactory.getLogger(MOrderSwap.class);
/*    */ 
/*    */   public double execute(int id) throws MTAgentException {
/* 15 */     double returnValue = 0.0D;
/* 16 */     Integer mtId = new Integer(id);
/*    */ 
/* 21 */     setError(id, 0, "ERR_NO_ERROR_MSG");
/* 22 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderSwap
 * JD-Core Version:    0.6.0
 */