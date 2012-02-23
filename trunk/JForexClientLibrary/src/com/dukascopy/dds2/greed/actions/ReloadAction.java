/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.request.InitRequestMessage;
/*    */ import java.util.Calendar;
/*    */ 
/*    */ public class ReloadAction extends AppActionEvent
/*    */ {
/*    */   private ClientForm gui;
/*    */   private Object source;
/* 16 */   private Calendar calendar = Calendar.getInstance();
/*    */   private boolean verbose;
/*    */ 
/*    */   public ReloadAction(Object source)
/*    */   {
/* 20 */     this(source, true);
/*    */   }
/*    */ 
/*    */   public ReloadAction(Object source, boolean verbose) {
/* 24 */     super(source, true, true);
/* 25 */     this.verbose = verbose;
/* 26 */     this.source = source;
/* 27 */     this.gui = ((ClientForm)GreedContext.get("clientGui"));
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 32 */     GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 36 */     if (transport.isOnline())
/*    */     {
/* 38 */       InitRequestMessage initRequest = new InitRequestMessage();
/* 39 */       ProtocolMessage localProtocolMessage = transport.controlRequest(initRequest);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter() {
/* 44 */     this.gui.clearOrderModels();
/*    */   }
/*    */ 
/*    */   public void updateGuiBefore() {
/* 48 */     if (this.verbose) {
/* 49 */       Notification notification = new Notification(this.calendar.getTime(), "reloading ...");
/* 50 */       PostMessageAction pma = new PostMessageAction(this.source, notification);
/* 51 */       GreedContext.publishEvent(pma);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.ReloadAction
 * JD-Core Version:    0.6.0
 */