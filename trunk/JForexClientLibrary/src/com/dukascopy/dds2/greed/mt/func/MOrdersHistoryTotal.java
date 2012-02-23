/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.dds2.greed.mt.helpers.MTAPIHelpers;
/*    */ import java.util.Collection;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrdersHistoryTotal extends AgentBase.CommonExecution
/*    */ {
/* 16 */   private static Logger log = LoggerFactory.getLogger(MOrdersHistoryTotal.class);
/*    */ 
/*    */   public int execute(int id)
/*    */     throws MTAgentException
/*    */   {
/* 52 */     int returnValue = 0;
/*    */ 
/* 54 */     Collection historicalData = MTAPIHelpers.getAllHistoricalData();
/* 55 */     returnValue = historicalData.size();
/* 56 */     setError(id, 0, "ERR_NO_ERROR_MSG");
/* 57 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrdersHistoryTotal
 * JD-Core Version:    0.6.0
 */