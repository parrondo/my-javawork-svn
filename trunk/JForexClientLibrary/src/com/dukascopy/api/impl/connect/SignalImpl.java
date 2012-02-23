/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import com.dukascopy.api.IOrder;
/*    */ import com.dukascopy.api.ISignal;
/*    */ import com.dukascopy.api.ISignal.Type;
/*    */ 
/*    */ public class SignalImpl
/*    */   implements ISignal
/*    */ {
/*    */   private final IOrder order;
/*    */   private final ISignal.Type type;
/*    */ 
/*    */   public SignalImpl(IOrder order, ISignal.Type type)
/*    */   {
/* 12 */     this.order = order;
/* 13 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public IOrder getOrder()
/*    */   {
/* 18 */     return this.order;
/*    */   }
/*    */ 
/*    */   public ISignal.Type getType()
/*    */   {
/* 23 */     return this.type;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.SignalImpl
 * JD-Core Version:    0.6.0
 */