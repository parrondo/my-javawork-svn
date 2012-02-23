/*     */ package com.dukascopy.dds2.greed.gui.component.dialog.disclaimers;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.IStrategyRunnable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ 
/*     */ public class RemoteStrategyDisclaimerDialog extends DisclaimerMessageDialog
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static RemoteStrategyDisclaimerDialog instance;
/*  34 */   private static ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */   private IStrategyRunnable action;
/*     */ 
/*     */   private RemoteStrategyDisclaimerDialog()
/*     */   {
/*  45 */     super("disclaimer.remote.strategies.message", new Object[] { GuiUtilsAndConstants.LABEL_LONG_NAME, GuiUtilsAndConstants.LABEL_LONG_NAME }, "disclaimer.remote.strategies.window.title", "disclaimer.remote.strategies.revision.version", "disclaimer.remote.strategies.message.header", null);
/*     */ 
/*  53 */     addActionListenersOnButtons();
/*  54 */     setAlwaysOnTop(true);
/*     */   }
/*     */ 
/*     */   public static RemoteStrategyDisclaimerDialog getInstance()
/*     */   {
/*  59 */     if (instance == null) {
/*  60 */       instance = new RemoteStrategyDisclaimerDialog();
/*     */     }
/*  62 */     return instance;
/*     */   }
/*     */ 
/*     */   private boolean isContDiclaimSelected() {
/*  66 */     return super.isContinueDiclaimingSelected();
/*     */   }
/*     */ 
/*     */   private void addActionListenersOnButtons()
/*     */   {
/*  71 */     super.isContinueDiclaimingSelected();
/*  72 */     super.addAgreeBtnActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  76 */         RemoteStrategyDisclaimerDialog.this.finishConfirmation((JLocalizableButton)e.getSource());
/*  77 */         if (RemoteStrategyDisclaimerDialog.this.action != null) {
/*  78 */           RemoteStrategyDisclaimerDialog.this.action.runStrategy();
/*     */         }
/*  80 */         NotificationUtils.getInstance().postWarningMessage(LocalizationManager.getTextWithArgumentKeys("disclaimer.remote.strategies.notif.message", new Object[] { "disclaimer.remote.strategies.revision.version" }), false);
/*     */ 
/*  82 */         if (RemoteStrategyDisclaimerDialog.this.isContDiclaimSelected()) {
/*  83 */           RemoteStrategyDisclaimerDialog.storage.saveRemoteStrategyDiscState(true);
/*  84 */           NotificationUtils.getInstance().postWarningMessage(LocalizationManager.getTextWithArgumentKeys("disclaimer.remote.strategies.stop.disc.notif.msg", new Object[] { "disclaimer.remote.strategies.revision.version" }), false);
/*     */         }
/*     */       }
/*     */     });
/*  90 */     super.addNotAgreeBtnActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  94 */         RemoteStrategyDisclaimerDialog.this.finishConfirmation((JLocalizableButton)e.getSource());
/*  95 */         RemoteStrategyDisclaimerDialog.storage.saveRemoteStrategyDiscState(false);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void finishConfirmation(JLocalizableButton button)
/*     */   {
/* 104 */     super.setModal(false);
/* 105 */     super.setVisible(false);
/* 106 */     super.dispose();
/*     */   }
/*     */ 
/*     */   public void showDialog()
/*     */   {
/* 111 */     super.showDisclaimer();
/*     */   }
/*     */ 
/*     */   public void showDialog(IStrategyRunnable action) {
/* 115 */     this.action = action;
/* 116 */     if (!isAccepted())
/* 117 */       super.showDisclaimer();
/*     */     else
/* 119 */       action.runStrategy();
/*     */   }
/*     */ 
/*     */   public boolean isPermAccepted()
/*     */   {
/* 124 */     return storage.restoreRemoteStrategyDiscState();
/*     */   }
/*     */ 
/*     */   public boolean isTempAccepted()
/*     */   {
/* 129 */     return isPermAccepted();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.RemoteStrategyDisclaimerDialog
 * JD-Core Version:    0.6.0
 */