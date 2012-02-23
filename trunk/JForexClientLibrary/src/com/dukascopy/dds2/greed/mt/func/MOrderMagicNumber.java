/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderMagicNumber extends AgentBase.CommonExecution
/*    */ {
/* 15 */   private static Logger log = LoggerFactory.getLogger(MOrderMagicNumber.class);
/*    */ 
/*    */   public int getMagicNumber(OrderMessage orderMessage) throws MTAgentException {
/* 18 */     int rc = 0;
/* 19 */     String label = orderMessage.getExternalSysId();
/* 20 */     if ((label.startsWith("MTAG")) && (label.indexOf("__") != -1)) {
/*    */       try
/*    */       {
/* 23 */         label = label.substring("MTAG".length(), label.indexOf("__"));
/* 24 */         rc = Integer.parseInt(label);
/*    */       }
/*    */       catch (Exception e) {
/* 27 */         e.printStackTrace();
/* 28 */         throw new MTAgentException(-4, "ARSP_LABEL_INCONSISTENT_MSG");
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 33 */     return rc;
/*    */   }
/*    */ 
/*    */   public int execute(int id) throws MTAgentException {
/* 37 */     int returnValue = 0;
/*    */ 
/* 39 */     Integer mtId = new Integer(id);
/* 40 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 41 */     if (msg != null) {
/* 42 */       returnValue = getMagicNumber(msg.getOpeningOrder());
/* 43 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 45 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderMagicNumber
 * JD-Core Version:    0.6.0
 */