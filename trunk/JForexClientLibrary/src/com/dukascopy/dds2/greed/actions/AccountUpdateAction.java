/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.indicator.AccountProvider;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.LoginForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.LoginPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.menu.MainMenu;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableDialog;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.util.IndicatorHelper;
/*     */ import com.dukascopy.transport.common.model.type.AccountState;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.awt.Container;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class AccountUpdateAction extends AppActionEvent
/*     */ {
/*  33 */   private static Logger LOGGER = LoggerFactory.getLogger(AccountUpdateAction.class);
/*     */   private AccountInfoMessage accountInfo;
/*     */   private ClientForm clientGui;
/*     */ 
/*     */   public AccountUpdateAction(Object source, AccountInfoMessage accountInfo)
/*     */   {
/*  39 */     super(source, false, true);
/*  40 */     this.accountInfo = accountInfo;
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*  47 */     AccountProvider.updateAccountInfo(this.accountInfo);
/*  48 */     this.clientGui = ((ClientForm)GreedContext.get("clientGui"));
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/*  57 */     this.clientGui.updateAccountState(this.accountInfo);
/*  58 */     WorkspacePanel workspacePanel = this.clientGui.getDealPanel().getWorkspacePanel();
/*  59 */     workspacePanel.setInitialInstruments();
/*     */ 
/*  61 */     if (GreedContext.isFirstAccountInfo())
/*     */     {
/*  63 */       IndicatorHelper.setAccountCurrency(this.accountInfo.getCurrency());
/*     */ 
/*  65 */       if ((AccountState.DISABLED == this.accountInfo.getAccountState()) || (AccountState.BLOCKED == this.accountInfo.getAccountState())) {
/*  66 */         showWarning();
/*     */       }
/*     */ 
/*  69 */       ClientForm mainWindow = (ClientForm)GreedContext.get("clientGui");
/*  70 */       mainWindow.getMainMenu().getReconnect().setEnabled(true);
/*     */ 
/*  72 */       if (!mainWindow.isVisible())
/*     */       {
/*  74 */         if ((LoginForm.getInstance() != null) && (LoginForm.getInstance().getLoginPanel() != null)) {
/*  75 */           LoginForm.getInstance().getLoginPanel().getLoginButton().setEnabled(true);
/*  76 */           LoginForm.getInstance().setVisible(false);
/*     */         }
/*  78 */         mainWindow.display();
/*     */       }
/*     */ 
/*  81 */       GreedContext.setUserProperty("ordgIdPref", this.accountInfo.getOrderGroupIdPrefix());
/*  82 */       GreedContext.setFirstAccountInfo(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void showWarning() {
/*  87 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run() {
/*  90 */         JLocalizableDialog warning = new JLocalizableDialog();
/*  91 */         warning.setTitle("title.warning");
/*  92 */         warning.setModal(true);
/*     */         try {
/*  94 */           warning.setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*     */         } catch (Exception e) {
/*  96 */           AccountUpdateAction.LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */ 
/*  99 */         JPanel messagePane = new JPanel();
/* 100 */         messagePane.add(new JLocalizableLabel("warning.bad.account.type"));
/* 101 */         warning.getContentPane().add(messagePane);
/* 102 */         JPanel buttonPane = new JPanel();
/*     */ 
/* 104 */         JLocalizableButton button = new JLocalizableButton("button.ok");
/* 105 */         buttonPane.add(button);
/*     */ 
/* 107 */         button.addActionListener(new ActionListener(warning)
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 110 */             this.val$warning.setVisible(false);
/* 111 */             this.val$warning.dispose();
/*     */           }
/*     */         });
/* 115 */         warning.getContentPane().add(buttonPane, "South");
/* 116 */         warning.setDefaultCloseOperation(2);
/* 117 */         warning.pack();
/* 118 */         warning.setLocationRelativeTo(null);
/* 119 */         warning.setResizable(false);
/* 120 */         warning.setVisible(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.AccountUpdateAction
 * JD-Core Version:    0.6.0
 */