/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.transport.common.msg.signals.SignalMessage;
/*    */ 
/*    */ public class SignalAction extends AppActionEvent
/*    */ {
/*    */   private final SignalMessage signal;
/*    */ 
/*    */   public SignalAction(Object source, SignalMessage signal)
/*    */   {
/* 17 */     super(source, false, false);
/* 18 */     this.signal = signal;
/*    */   }
/*    */ 
/*    */   public void doAction() {
/* 22 */     if (!GreedContext.isLogOff()) {
/* 23 */       GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 25 */       if (transport.isOnline())
/* 26 */         transport.controlRequest(this.signal);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.SignalAction
 * JD-Core Version:    0.6.0
 */