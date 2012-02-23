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
/*    */ import java.math.BigDecimal;
/*    */ import java.util.Map;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderClosePrice extends AgentBase.CommonExecution
/*    */ {
/* 16 */   private static Logger log = LoggerFactory.getLogger(MOrderClosePrice.class);
/*    */ 
/*    */   public double execute(int id) throws MTAgentException, JFException {
/* 19 */     double returnValue = 0.0D;
/*    */ 
/* 21 */     Integer mtId = new Integer(id);
/*    */ 
/* 23 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 24 */     if (msg != null) {
/* 25 */       OrderHistoricalData historicalData = MTAPIHelpers.getOrderGroupHistoricalData(msg.getOrderGroupId(), Instrument.fromString(msg.getInstrument()));
/*    */ 
/* 29 */       if ((historicalData != null) && (historicalData.isClosed())) {
/* 30 */         OrderHistoricalData.CloseData closeData = (OrderHistoricalData.CloseData)historicalData.getCloseDataMap().get(msg.getOrderGroupId());
/*    */ 
/* 32 */         returnValue = closeData.getClosePrice().doubleValue();
/*    */       } else {
/* 34 */         setError(mtId, 2, "ERR_COMMON_ERROR_MSG");
/*    */       }
/*    */ 
/* 37 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 39 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderClosePrice
 * JD-Core Version:    0.6.0
 */