/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient.TRANSPORT_RC;
/*    */ import com.dukascopy.dds2.greed.gui.LoginForm;
/*    */ import com.dukascopy.dds2.greed.gui.component.LoginPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JProgressBar;
/*    */ 
/*    */ public class AuthFailedAction extends AppActionEvent
/*    */ {
/*    */   private MessagePanel messagePanel;
/*    */   private JProgressBar progress;
/*    */   private JButton connectButton;
/*    */   private GreedTransportClient.TRANSPORT_RC rc;
/* 28 */   private LoginPanel loginPanel = ((LoginForm)(LoginForm)GreedContext.get("loginForm")).getLoginPanel();
/*    */ 
/*    */   public AuthFailedAction(GreedTransportClient.TRANSPORT_RC rc)
/*    */   {
/* 33 */     super("Connect", false, true);
/* 34 */     this.messagePanel = this.loginPanel.getMessagePanel();
/* 35 */     this.progress = this.loginPanel.getProgressBar();
/* 36 */     this.connectButton = this.loginPanel.getLoginButton();
/* 37 */     this.rc = rc;
/*    */   }
/*    */   public void updateGuiBefore() {
/*    */   }
/*    */   public void doAction() {
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter() {
/* 45 */     String message = null;
/*    */ 
/* 47 */     String LINES_SPLITTER = "##";
/*    */ 
/* 49 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$connection$GreedTransportClient$TRANSPORT_RC[this.rc.ordinal()]) {
/*    */     case 1:
/* 51 */       message = "Authentication failed. ##Please check your login details.";
/* 52 */       break;
/*    */     case 2:
/* 54 */       message = "Bad url";
/* 55 */       break;
/*    */     case 3:
/* 57 */       message = "Initialisation error";
/* 58 */       break;
/*    */     case 4:
/* 60 */       message = "Connection error occurred";
/* 61 */       break;
/*    */     case 5:
/* 63 */       message = "Invalid application version.##Please launch the platform ## from www.dukascopy.com";
/* 64 */       break;
/*    */     case 6:
/* 66 */       message = "System is offline";
/* 67 */       break;
/*    */     case 7:
/* 69 */       message = "Connection error occurred";
/* 70 */       break;
/*    */     }
/*    */ 
/* 75 */     String[] messages = message.split("##");
/*    */ 
/* 77 */     if ((messages == null) || (messages.length <= 1)) {
/* 78 */       Notification notification = new Notification(null, message);
/* 79 */       notification.setPriority("ERROR");
/* 80 */       this.messagePanel.postMessage(notification);
/*    */     } else {
/* 82 */       String msg = null;
/* 83 */       int linesCount = messages.length - 1;
/* 84 */       for (int i = linesCount; i >= 0; i--) {
/* 85 */         msg = messages[i];
/* 86 */         Notification notification = new Notification(null, msg);
/* 87 */         notification.setPriority("ERROR");
/* 88 */         this.messagePanel.postMessage(notification);
/*    */       }
/*    */     }
/*    */ 
/* 92 */     this.messagePanel.repaint();
/*    */ 
/* 94 */     this.progress.setIndeterminate(false);
/* 95 */     this.connectButton.getAction().setEnabled(true);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.AuthFailedAction
 * JD-Core Version:    0.6.0
 */