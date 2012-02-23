/*    */ package com.dukascopy.dds2.greed.model;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*    */ 
/*    */ public class AccountStatement
/*    */   implements AccountInfoListener
/*    */ {
/*    */   private volatile AccountInfoMessage lastAccountState;
/*    */ 
/*    */   public AccountInfoMessage getLastAccountState()
/*    */   {
/* 18 */     return this.lastAccountState;
/*    */   }
/*    */ 
/*    */   private void setLastAccountState(AccountInfoMessage lastAccountState)
/*    */   {
/* 26 */     this.lastAccountState = lastAccountState;
/*    */   }
/*    */ 
/*    */   public void onAccountInfo(AccountInfoMessage accountInfo)
/*    */   {
/* 31 */     setLastAccountState(accountInfo);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.model.AccountStatement
 * JD-Core Version:    0.6.0
 */