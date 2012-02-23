/*    */ package com.dukascopy.dds2.greed.agent.indicator;
/*    */ 
/*    */ import com.dukascopy.api.impl.connect.PlatformAccountImpl;
/*    */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*    */ 
/*    */ public class AccountProvider
/*    */ {
/*  7 */   private static PlatformAccountImpl account = null;
/*    */ 
/*    */   public static synchronized void updateAccountInfo(AccountInfoMessage info) {
/* 10 */     if (account == null)
/* 11 */       account = new PlatformAccountImpl(info);
/*    */     else
/* 13 */       account.updateFromMessage(info);
/*    */   }
/*    */ 
/*    */   public static PlatformAccountImpl getAccount()
/*    */   {
/* 18 */     return account;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.indicator.AccountProvider
 * JD-Core Version:    0.6.0
 */