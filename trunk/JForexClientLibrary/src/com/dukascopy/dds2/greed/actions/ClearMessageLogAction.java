/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.LoginForm;
/*    */ import com.dukascopy.dds2.greed.gui.component.LoginPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.message.MessageList;
/*    */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*    */ 
/*    */ public class ClearMessageLogAction extends AppActionEvent
/*    */ {
/*    */   public ClearMessageLogAction(MessageList messageList)
/*    */   {
/* 14 */     super(messageList, false, true);
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 19 */     ClientForm form = (ClientForm)GreedContext.get("clientGui");
/* 20 */     if (form != null) {
/* 21 */       MessageList messageList = (MessageList)getSource();
/* 22 */       messageList.clearMessages();
/*    */     }
/*    */ 
/* 26 */     LoginForm loginForm = LoginForm.getInstance();
/* 27 */     if (loginForm != null) {
/* 28 */       LoginPanel panel = loginForm.getLoginPanel();
/* 29 */       if (panel != null) {
/* 30 */         MessagePanel messagePanel = panel.getMessagePanel();
/* 31 */         if (messagePanel != null)
/* 32 */           messagePanel.clearMessageLog();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.ClearMessageLogAction
 * JD-Core Version:    0.6.0
 */