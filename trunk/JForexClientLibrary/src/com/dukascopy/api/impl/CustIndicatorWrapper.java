/*    */ package com.dukascopy.api.impl;
/*    */ 
/*    */ import com.dukascopy.api.indicators.IIndicator;
/*    */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*    */ import com.dukascopy.dds2.greed.util.FullAccessDisclaimerProvider;
/*    */ import com.dukascopy.dds2.greed.util.IFullAccessDisclaimer;
/*    */ import java.io.File;
/*    */ 
/*    */ public class CustIndicatorWrapper extends ServiceWrapper
/*    */ {
/*    */   private JFXPack pack;
/*    */ 
/*    */   public String getName()
/*    */   {
/* 15 */     if (this.isNewUnsaved) {
/* 16 */       return "*Indicator" + this.newFileIndex;
/*    */     }
/* 18 */     if (this.srcFile != null) {
/* 19 */       return this.srcFile.getName();
/*    */     }
/* 21 */     if (this.binFile != null) {
/* 22 */       return this.binFile.getName();
/*    */     }
/* 24 */     return null;
/*    */   }
/*    */ 
/*    */   public IIndicator getIndicator() {
/* 28 */     if (this.pack == null) {
/* 29 */       return null;
/*    */     }
/* 31 */     return (IIndicator)this.pack.getTarget();
/*    */   }
/*    */ 
/*    */   public void reinit() {
/* 35 */     this.pack = null;
/*    */   }
/*    */ 
/*    */   public boolean requestFullAccess() throws Exception {
/* 39 */     if (this.pack == null) {
/* 40 */       this.pack = JFXPack.loadFromPack(getBinaryFile());
/*    */     }
/* 42 */     if (this.pack != null) {
/* 43 */       if (this.pack.isFullAccessRequested()) {
/* 44 */         return FullAccessDisclaimerProvider.getDisclaimer().showDialog(this.pack);
/*    */       }
/* 46 */       return true;
/*    */     }
/*    */ 
/* 49 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.CustIndicatorWrapper
 * JD-Core Version:    0.6.0
 */