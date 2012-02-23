/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.JFException;
/*    */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*    */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.CloseData;
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.dds2.greed.mt.helpers.MTAPIHelpers;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import java.util.Map;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderCloseTime extends AgentBase.CommonExecution
/*    */ {
/* 15 */   private static Logger log = LoggerFactory.getLogger(MOrderCloseTime.class);
/*    */ 
/*    */   public long execute(int id) throws MTAgentException, JFException {
/* 18 */     long returnValue = 0L;
/*    */ 
/* 20 */     Integer mtId = new Integer(id);
/*    */ 
/* 22 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 23 */     if (msg != null) {
/*    */       try {
/* 25 */         OrderHistoricalData historicalData = MTAPIHelpers.getOrderGroupHistoricalData(msg.getOrderGroupId(), Instrument.fromString(msg.getInstrument()));
/*    */ 
/* 28 */         if ((historicalData != null) && (historicalData.isClosed())) {
/* 29 */           OrderHistoricalData.CloseData closeData = (OrderHistoricalData.CloseData)historicalData.getCloseDataMap().get(msg.getOrderGroupId());
/*    */ 
/* 31 */           returnValue = closeData.getCloseTime() / 1000L;
/*    */         } else {
/* 33 */           setError(mtId, 2, "ERR_COMMON_ERROR_MSG");
/*    */         }
/*    */       }
/*    */       catch (Exception ex) {
/* 37 */         log.error(ex.getMessage(), ex);
/*    */       }
/* 39 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 41 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderCloseTime
 * JD-Core Version:    0.6.0
 */