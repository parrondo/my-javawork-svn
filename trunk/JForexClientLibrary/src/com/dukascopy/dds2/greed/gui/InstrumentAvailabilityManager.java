/*    */ package com.dukascopy.dds2.greed.gui;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class InstrumentAvailabilityManager
/*    */ {
/*    */   private static InstrumentAvailabilityManager instance;
/*    */   private Set<String> whiteList;
/*    */ 
/*    */   private InstrumentAvailabilityManager()
/*    */   {
/* 23 */     init();
/*    */   }
/*    */ 
/*    */   public static InstrumentAvailabilityManager getInstance()
/*    */   {
/* 30 */     if (instance == null) {
/* 31 */       instance = new InstrumentAvailabilityManager();
/*    */     }
/* 33 */     return instance;
/*    */   }
/*    */ 
/*    */   private void init()
/*    */   {
/* 38 */     this.whiteList = new HashSet();
/*    */   }
/*    */ 
/*    */   public boolean isAllowed(Instrument instrument)
/*    */   {
/* 58 */     return isAllowed(instrument.toString());
/*    */   }
/*    */ 
/*    */   public boolean isAllowed(String instrument)
/*    */   {
/* 64 */     if ((LotAmountChanger.isCommodity(Instrument.fromString(instrument))) && (GreedContext.isContest()))
/*    */     {
/* 66 */       return false;
/*    */     }
/* 68 */     if (this.whiteList.isEmpty()) return true;
/*    */ 
/* 70 */     return this.whiteList.contains(instrument);
/*    */   }
/*    */ 
/*    */   public Set<String> getWhiteList()
/*    */   {
/* 78 */     return this.whiteList;
/*    */   }
/*    */ 
/*    */   public void updateWhiteList() {
/* 82 */     Set supportedInstrumnts = GreedContext.getSupportedInstrument();
/* 83 */     this.whiteList.addAll(supportedInstrumnts);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager
 * JD-Core Version:    0.6.0
 */