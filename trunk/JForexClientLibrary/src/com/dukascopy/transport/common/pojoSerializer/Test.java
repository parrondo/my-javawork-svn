/*    */ package com.dukascopy.transport.common.pojoSerializer;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.request.PrimeBrokerExposureRequestMessage;
/*    */ import com.dukascopy.transport.common.msg.response.InstrumentExposure;
/*    */ import com.dukascopy.transport.common.msg.response.PrimeBrokerExposureResponseMessage;
/*    */ import java.io.PrintStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class Test
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 27 */     PrimeBrokerExposureRequestMessage request = new PrimeBrokerExposureRequestMessage();
/* 28 */     request.setPrimeBroker("myBrok");
/* 29 */     String protocol = request.toProtocolString();
/* 30 */     System.out.println(protocol);
/* 31 */     ProtocolMessage pm = null;
/* 32 */     pm = ProtocolMessage.parse(protocol);
/*    */ 
/* 34 */     System.out.println(((PrimeBrokerExposureRequestMessage)pm).getPrimeBroker());
/* 35 */     InstrumentExposure ie = new InstrumentExposure("EUR/USD", "1000000", "-100000");
/* 36 */     InstrumentExposure ie1 = new InstrumentExposure("EUR/JPY", "2000000", "-1500000");
/* 37 */     PrimeBrokerExposureResponseMessage response = new PrimeBrokerExposureResponseMessage("myBrok");
/* 38 */     List list = new ArrayList();
/* 39 */     list.add(ie);
/* 40 */     list.add(ie1);
/* 41 */     response.setExposureList(list);
/* 42 */     protocol = response.toProtocolString();
/* 43 */     System.out.println(protocol);
/*    */ 
/* 45 */     pm = ProtocolMessage.parse(protocol);
/*    */ 
/* 47 */     List rrr = ((PrimeBrokerExposureResponseMessage)pm).getExposureList();
/* 48 */     for (InstrumentExposure ll : rrr)
/* 49 */       System.out.println(ll);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.pojoSerializer.Test
 * JD-Core Version:    0.6.0
 */