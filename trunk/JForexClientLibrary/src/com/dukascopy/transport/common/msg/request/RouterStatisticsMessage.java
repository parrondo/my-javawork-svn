/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.dds2.router.statistics.RouterStatisticsDTO;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.SerializableProtocolMessage;
/*    */ 
/*    */ public class RouterStatisticsMessage extends SerializableProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "routerStatistics";
/*    */ 
/*    */   public RouterStatisticsMessage()
/*    */   {
/* 16 */     setType("routerStatistics");
/*    */   }
/*    */ 
/*    */   public RouterStatisticsMessage(ProtocolMessage message) {
/* 20 */     super(message);
/* 21 */     setType("routerStatistics");
/*    */   }
/*    */ 
/*    */   public RouterStatisticsDTO getStatistics()
/*    */   {
/* 31 */     return (RouterStatisticsDTO)getData();
/*    */   }
/*    */ 
/*    */   public void setStatistics(RouterStatisticsDTO statistics)
/*    */   {
/* 40 */     setData(statistics);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.RouterStatisticsMessage
 * JD-Core Version:    0.6.0
 */