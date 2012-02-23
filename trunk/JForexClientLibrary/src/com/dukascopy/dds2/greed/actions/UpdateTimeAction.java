/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.component.ApplicationClock;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*    */ 
/*    */ public class UpdateTimeAction extends AppActionEvent
/*    */ {
/*    */   private ProtocolMessage tsMessage;
/*    */ 
/*    */   public UpdateTimeAction(Object source, ProtocolMessage pm)
/*    */   {
/* 14 */     super(source, false, true);
/*    */ 
/* 18 */     this.tsMessage = pm;
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 23 */     if ((this.tsMessage instanceof CurrencyMarket))
/*    */     {
/* 25 */       ((ApplicationClock)GreedContext.get("applicationClock")).syncTime(((CurrencyMarket)this.tsMessage).getCreationTimestamp());
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.UpdateTimeAction
 * JD-Core Version:    0.6.0
 */