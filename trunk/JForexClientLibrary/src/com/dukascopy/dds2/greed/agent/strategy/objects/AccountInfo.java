/*    */ package com.dukascopy.dds2.greed.agent.strategy.objects;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.Money;
/*    */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class AccountInfo
/*    */ {
/* 30 */   public double balance = 0.0D;
/*    */ 
/* 32 */   public double margin = 0.0D;
/*    */ 
/* 34 */   public double equity = 0.0D;
/*    */ 
/*    */   public AccountInfo(AccountInfoMessage accountInfo)
/*    */   {
/* 15 */     if (accountInfo.getUsableMargin() != null) {
/* 16 */       this.margin = accountInfo.getUsableMargin().getValue().doubleValue();
/*    */     }
/* 18 */     if (accountInfo.getBalance() != null) {
/* 19 */       this.balance = accountInfo.getBalance().getValue().doubleValue();
/*    */     }
/* 21 */     if (accountInfo.getEquity() != null)
/* 22 */       this.equity = accountInfo.getEquity().getValue().doubleValue();
/*    */   }
/*    */ 
/*    */   public AccountInfo()
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.objects.AccountInfo
 * JD-Core Version:    0.6.0
 */