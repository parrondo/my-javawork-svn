/*    */ package com.dukascopy.dds2.greed.gui;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.component.LoginPanel;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorageImpl;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.event.WindowAdapter;
/*    */ import java.awt.event.WindowEvent;
/*    */ import javax.swing.ImageIcon;
/*    */ import javax.swing.JFrame;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class LoginForm extends JFrame
/*    */ {
/* 26 */   private static final Logger LOGGER = LoggerFactory.getLogger(LoginForm.class);
/*    */   public static final String ID_JF_LOGINFORM = "ID_JF_LOGINFORM";
/*    */   private static final String TITLE = "FX Marketplace Platform";
/* 31 */   private static final Dimension MAX_SIZE = new Dimension(270, 300);
/* 32 */   private static final Dimension MIN_SIZE = new Dimension(270, 70);
/*    */   private LoginPanel loginPanel;
/*    */ 
/*    */   public static LoginForm getInstance()
/*    */   {
/* 37 */     LoginForm form = (LoginForm)GreedContext.get("loginForm");
/*    */ 
/* 39 */     if (form == null) {
/* 40 */       form = new LoginForm();
/* 41 */       GreedContext.putInSingleton("loginForm", form);
/*    */     }
/*    */ 
/* 44 */     form.setName("ID_JF_LOGINFORM");
/* 45 */     return form;
/*    */   }
/*    */ 
/*    */   private LoginForm()
/*    */   {
/* 50 */     addWindowListener(new WindowAdapter()
/*    */     {
/*    */       public void windowClosing(WindowEvent e) {
/* 53 */         ClientSettingsStorageImpl.saveSystemSettings();
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public void loginRequest(String pinValue, String passwordValue, String loginValue, String instanceId) {
/* 60 */     GreedContext.setConfig("SESSION_ID", instanceId);
/*    */ 
/* 62 */     String ticket = System.getProperty("com.dukascopy.platform.ticket");
/* 63 */     GreedContext.setConfig("TICKET", ticket);
/* 64 */     GreedContext.setConfig("account_name", loginValue);
/* 65 */     GreedContext.setConfig(" ", passwordValue);
/*    */ 
/* 67 */     getLoginPanel().loginRequest(pinValue);
/*    */   }
/*    */ 
/*    */   public void display() {
/* 71 */     setTitle("FX Marketplace Platform");
/*    */     try
/*    */     {
/* 74 */       setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*    */     } catch (Exception e) {
/* 76 */       LOGGER.error(e.getMessage(), e);
/*    */     }
/*    */ 
/* 79 */     this.loginPanel = new LoginPanel(this);
/* 80 */     setContentPane(this.loginPanel);
/*    */ 
/* 82 */     setMaximumSize(MAX_SIZE);
/* 83 */     setMinimumSize(MIN_SIZE);
/*    */ 
/* 85 */     setDefaultCloseOperation(3);
/* 86 */     setResizable(false);
/* 87 */     pack();
/* 88 */     setLocationRelativeTo(null);
/* 89 */     setVisible(true);
/*    */   }
/*    */ 
/*    */   public LoginPanel getLoginPanel() {
/* 93 */     return this.loginPanel;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.LoginForm
 * JD-Core Version:    0.6.0
 */