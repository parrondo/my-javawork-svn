/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedConnectionUtils;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient.TRANSPORT_RC;
/*    */ import com.dukascopy.dds2.greed.gui.LoginForm;
/*    */ import com.dukascopy.dds2.greed.gui.component.LoginPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.LoginPanel.LoginTimer;
/*    */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.EuLiveDisclaimerDialog;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.util.Map;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.JButton;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ConnectAction extends AppActionEvent
/*    */ {
/* 21 */   private static final Logger LOGGER = LoggerFactory.getLogger(ConnectAction.class);
/*    */   private static final long DELAY = 3000L;
/*    */   private final LoginPanel loginPanel;
/*    */   private final String pin;
/*    */   private final Map<String, BufferedImage> captchaMap;
/*    */ 
/*    */   public ConnectAction(LoginPanel loginPanel, Map<String, BufferedImage> captcha, String pin)
/*    */   {
/* 34 */     super("Connect", true, false);
/*    */ 
/* 36 */     this.loginPanel = loginPanel;
/*    */ 
/* 38 */     this.pin = pin;
/* 39 */     this.captchaMap = captcha;
/*    */   }
/*    */ 
/*    */   public void updateGuiBefore() {
/* 43 */     this.loginPanel.getLoginButton().getAction().setEnabled(false);
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 48 */     LOGGER.debug("Authenticating ...");
/* 49 */     ticketAuth();
/*    */   }
/*    */ 
/*    */   public void ticketAuth() {
/* 53 */     new Thread()
/*    */     {
/*    */       public void run()
/*    */       {
/* 57 */         String httpResponse = null;
/* 58 */         GreedTransportClient.TRANSPORT_RC validationResult = null;
/*    */ 
/* 63 */         while ((LoginPanel.LoginTimer.getInstance().isRunning()) && (GreedTransportClient.TRANSPORT_RC.OK != validationResult) && (GreedTransportClient.TRANSPORT_RC.ERROR_AUTH != validationResult) && (GreedTransportClient.TRANSPORT_RC.SYSTEM_ERROR != validationResult))
/*    */         {
/* 65 */           httpResponse = GreedConnectionUtils.getTicketAndAPIUrl(ConnectAction.this.captchaMap, ConnectAction.this.pin);
/* 66 */           validationResult = GreedConnectionUtils.validateResponse(httpResponse);
/*    */           try
/*    */           {
/* 69 */             sleep(3000L);
/*    */           } catch (InterruptedException e) {
/*    */           }
/*    */         }
/* 73 */         if (LoginPanel.LoginTimer.getInstance().isCanceled()) return;
/*    */ 
/* 76 */         if (GreedTransportClient.TRANSPORT_RC.OK != validationResult) {
/* 77 */           GreedConnectionUtils.wrongAuth(validationResult);
/* 78 */           return;
/*    */         }
/*    */ 
/* 82 */         String[] urlAndTicket = httpResponse.split("@");
/*    */ 
/* 84 */         if (GreedContext.IS_EU_LIVE) {
/* 85 */           EuLiveDisclaimerDialog.getInstance().showDialog();
/* 86 */           if (!EuLiveDisclaimerDialog.getInstance().isAccepted()) {
/* 87 */             LoginPanel panel = LoginForm.getInstance().getLoginPanel();
/* 88 */             if (panel != null) panel.cancelRequest(); 
/*    */           }
/*    */           else {
/* 90 */             GreedContext.publishEvent(new PlatformInitAction(this, urlAndTicket[1], urlAndTicket[0]));
/*    */           }
/*    */         } else {
/* 93 */           GreedContext.publishEvent(new PlatformInitAction(this, urlAndTicket[1], urlAndTicket[0]));
/*    */         }
/*    */       }
/*    */     }
/* 53 */     .start();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.ConnectAction
 * JD-Core Version:    0.6.0
 */