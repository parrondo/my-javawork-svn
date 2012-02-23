/*    */ package com.dukascopy.transport.common.msg.datafeed;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*    */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*    */ import java.util.List;
/*    */ 
/*    */ public class DFTickMessage extends CurrencyMarket
/*    */ {
/*    */   public static final String TYPE = "df_cm";
/*    */   public static final String INSTRUMENT_ID = "instrument_id";
/*    */ 
/*    */   public DFTickMessage(String currencyPrimary, String currencySecondary, List<CurrencyOffer> bids, List<CurrencyOffer> asks)
/*    */   {
/* 21 */     super(currencyPrimary, currencySecondary, bids, asks);
/* 22 */     setType("df_cm");
/*    */   }
/*    */ 
/*    */   public DFTickMessage(ProtocolMessage message) {
/* 26 */     super(message);
/* 27 */     setType("df_cm");
/* 28 */     put("instrument_id", message.getString("instrument_id"));
/*    */   }
/*    */ 
/*    */   public void setInstrumentId(String instrumentId) {
/* 32 */     put("instrument_id", instrumentId);
/*    */   }
/*    */ 
/*    */   public String getInstrumentId() {
/* 36 */     return getString("instrument_id");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.DFTickMessage
 * JD-Core Version:    0.6.0
 */