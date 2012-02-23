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
/*     */ public class StrategyDisclaimerDialog extends DisclaimerMessageDialog
/*     */ {
/*     */   private static StrategyDisclaimerDialog instance;
/*  19 */   private static ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */   private IStrategyRunnable action;
/*  21 */   private boolean isRemote = false;
/*     */ 
/*     */   private StrategyDisclaimerDialog() {
/*  24 */     super("disclaimer.strategies.message", new Object[] { GuiUtilsAndConstants.LABEL_LONG_NAME }, "disclaimer.strategies.window.title", "disclaimer.strategies.revision.version", "disclaimer.strategies.message.header", null);
/*     */ 
/*  29 */     addActionListenersOnButtons();
/*  30 */     setAlwaysOnTop(true);
/*     */   }
/*     */ 
/*     */   public static StrategyDisclaimerDialog getInstance() {
/*  34 */     if (instance == null) {
/*  35 */       instance = new StrategyDisclaimerDialog();
/*     */     }
/*  37 */     return instance;
/*     */   }
/*     */ 
/*     */   private boolean isContDiclaimSelected() {
/*  41 */     return super.isContinueDiclaimingSelected();
/*     */   }
/*     */ 
/*     */   private void addActionListenersOnButtons()
/*     */   {
/*  46 */     super.isContinueDiclaimingSelected();
/*  47 */     super.addAgreeBtnActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  51 */         StrategyDisclaimerDialog.this.finishConfirmation((JLocalizableButton)e.getSource());
/*     */ 
/*  53 */         if (StrategyDisclaimerDialog.this.isRemote) {
/*  54 */           RemoteStrategyDisclaimerDialog.getInstance().showDialog(StrategyDisclaimerDialog.this.action);
/*  55 */         } else if (StrategyDisclaimerDialog.this.action != null) {
/*  56 */           StrategyDisclaimerDialog.this.action.runStrategy();
/*  57 */           StrategyDisclaimerDialog.access$102(StrategyDisclaimerDialog.this, false);
/*     */         }
/*     */ 
/*  60 */         NotificationUtils.getInstance().postWarningMessage(LocalizationManager.getTextWithArgumentKeys("disclaimer.strategies.notif.message", new Object[] { "disclaimer.strategies.revision.version" }), false);
/*     */ 
/*  62 */         if (StrategyDisclaimerDialog.this.isContDiclaimSelected()) {
/*  63 */           StrategyDisclaimerDialog.storage.saveStrategyDiscState(true);
/*  64 */           NotificationUtils.getInstance().postWarningMessage(LocalizationManager.getTextWithArgumentKeys("disclaimer.strategies.stop.disc.notif.msg", new Object[] { "disclaimer.strategies.revision.version" }), false);
/*     */         }
/*     */       }
/*     */     });
/*  73 */     super.addNotAgreeBtnActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  77 */         StrategyDisclaimerDialog.this.finishConfirmation((JLocalizableButton)e.getSource());
/*  78 */         StrategyDisclaimerDialog.storage.saveStrategyDiscState(false);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void finishConfirmation(JLocalizableButton button) {
/*  85 */     super.setModal(false);
/*  86 */     super.setVisible(false);
/*  87 */     super.dispose();
/*     */   }
/*     */ 
/*     */   public void showDialog()
/*     */   {
/*  92 */     super.showDisclaimer();
/*     */   }
/*     */ 
/*     */   public void showDialog(IStrategyRunnable action) {
/*  96 */     this.action = action;
/*  97 */     this.isRemote = false;
/*     */ 
/*  99 */     if (!isAccepted())
/* 100 */       super.showDisclaimer();
/*     */     else
/* 102 */       action.runStrategy();
/*     */   }
/*     */ 
/*     */   public void showDialog(IStrategyRunnable action, boolean isRemote) {
/* 106 */     this.action = action;
/* 107 */     this.isRemote = isRemote;
/*     */ 
/* 109 */     if (!isAccepted())
/* 110 */       super.showDisclaimer();
/* 111 */     else if (isRemote)
/* 112 */       RemoteStrategyDisclaimerDialog.getInstance().showDialog(action);
/*     */     else
/* 114 */       action.runStrategy();
/*     */   }
/*     */ 
/*     */   public boolean isPermAccepted()
/*     */   {
/* 120 */     return storage.restoreStrategyDiscState();
/*     */   }
/*     */ 
/*     */   public boolean isTempAccepted()
/*     */   {
/* 125 */     return isPermAccepted();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.StrategyDisclaimerDialog
 * JD-Core Version:    0.6.0
 */