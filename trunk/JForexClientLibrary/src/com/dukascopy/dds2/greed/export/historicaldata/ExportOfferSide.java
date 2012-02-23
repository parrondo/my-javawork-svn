/*    */ package com.dukascopy.dds2.greed.export.historicaldata;
/*    */ 
/*    */ import com.dukascopy.api.OfferSide;
/*    */ 
/*    */ public enum ExportOfferSide
/*    */ {
/*  7 */   BID("Bid"), 
/*  8 */   ASK("Ask"), 
/*  9 */   BID_ASK("Bid and Ask");
/*    */ 
/*    */   private String text;
/*    */ 
/* 14 */   private ExportOfferSide(String text) { this.text = text; }
/*    */ 
/*    */   public OfferSide[] getOfferSides()
/*    */   {
/* 18 */     OfferSide[] offerSide = null;
/*    */ 
/* 20 */     if (equals(ASK))
/* 21 */       offerSide = new OfferSide[] { OfferSide.ASK };
/* 22 */     else if (equals(BID))
/* 23 */       offerSide = new OfferSide[] { OfferSide.BID };
/*    */     else {
/* 25 */       offerSide = new OfferSide[] { OfferSide.ASK, OfferSide.BID };
/*    */     }
/*    */ 
/* 28 */     return offerSide;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 34 */     return this.text;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.ExportOfferSide
 * JD-Core Version:    0.6.0
 */