/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ public class FullAccessDisclaimerProvider
/*    */ {
/*    */   private static IFullAccessDisclaimer disclaimer;
/*    */ 
/*    */   public static synchronized void setDisclaimer(IFullAccessDisclaimer disclaimer)
/*    */   {
/*  8 */     disclaimer = disclaimer;
/*    */   }
/*    */ 
/*    */   public static synchronized IFullAccessDisclaimer getDisclaimer() {
/* 12 */     return disclaimer;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.FullAccessDisclaimerProvider
 * JD-Core Version:    0.6.0
 */