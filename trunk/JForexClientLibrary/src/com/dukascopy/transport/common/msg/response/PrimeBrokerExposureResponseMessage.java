/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.Money;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ import java.math.BigDecimal;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class PrimeBrokerExposureResponseMessage extends RequestMessage
/*    */ {
/*    */   private static final String EXPOSURE = "expo";
/*    */   private static final String PRIME_BROKER = "broker";
/*    */   public static final String TYPE = "expoResponse";
/*    */ 
/*    */   public PrimeBrokerExposureResponseMessage(ProtocolMessage message)
/*    */   {
/* 22 */     super(message);
/* 23 */     setType("expoResponse");
/* 24 */     put("expo", message.getString("expo"));
/* 25 */     put("broker", message.getString("broker"));
/*    */   }
/*    */ 
/*    */   public PrimeBrokerExposureResponseMessage(String primeBroker)
/*    */   {
/* 31 */     setType("expoResponse");
/* 32 */     setPrimeBroker(primeBroker);
/*    */   }
/*    */ 
/*    */   public List<InstrumentExposure> getExposureList()
/*    */   {
/* 38 */     List res = new ArrayList();
/* 39 */     StringTokenizer array = new StringTokenizer(getString("expo"), ",");
/* 40 */     while (array.hasMoreTokens()) {
/* 41 */       res.add(new InstrumentExposure(array.nextToken(), array.nextToken(), array.nextToken()));
/*    */     }
/* 43 */     return res;
/*    */   }
/*    */ 
/*    */   public void setExposureList(List<InstrumentExposure> exposures) {
/* 47 */     StringBuffer buff = new StringBuffer();
/* 48 */     for (int i = 0; i < exposures.size(); i++) {
/* 49 */       if (i == 0) {
/* 50 */         buff.append(((InstrumentExposure)exposures.get(i)).getInstrument());
/* 51 */         buff.append(",").append(((InstrumentExposure)exposures.get(i)).getAmountPrimary().getValue().toPlainString());
/* 52 */         buff.append(",").append(((InstrumentExposure)exposures.get(i)).getAmountSecondary().getValue().toPlainString());
/*    */       } else {
/* 54 */         buff.append(",").append(((InstrumentExposure)exposures.get(i)).getInstrument());
/* 55 */         buff.append(",").append(((InstrumentExposure)exposures.get(i)).getAmountPrimary().getValue().toPlainString());
/* 56 */         buff.append(",").append(((InstrumentExposure)exposures.get(i)).getAmountSecondary().getValue().toPlainString());
/*    */       }
/*    */     }
/* 59 */     put("expo", buff.toString());
/*    */   }
/*    */ 
/*    */   public void setPrimeBroker(String primeBroker)
/*    */   {
/* 66 */     put("broker", primeBroker);
/*    */   }
/*    */ 
/*    */   public String getPrimeBroker()
/*    */   {
/* 74 */     return getString("broker");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.PrimeBrokerExposureResponseMessage
 * JD-Core Version:    0.6.0
 */