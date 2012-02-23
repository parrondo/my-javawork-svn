/*     */ package com.dukascopy.dds2.greed.gui.component.dialog.disclaimers;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.IFullAccessDisclaimer;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ 
/*     */ public class FullAccessDisclaimDialog extends DisclaimerMessageDialog
/*     */   implements IFullAccessDisclaimer
/*     */ {
/*     */   private static FullAccessDisclaimDialog instance;
/*  18 */   private static ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */   private JFXPack pack;
/*  21 */   private boolean isTempAccepted = false;
/*     */ 
/*     */   public static IFullAccessDisclaimer getInstance() {
/*  24 */     if (instance == null) {
/*  25 */       instance = new FullAccessDisclaimDialog();
/*     */     }
/*  27 */     return instance;
/*     */   }
/*     */ 
/*     */   private FullAccessDisclaimDialog() {
/*  31 */     super("disclaimer.annotation.message", new Object[] { GuiUtilsAndConstants.LABEL_LONG_NAME, GuiUtilsAndConstants.LABEL_LONG_NAME }, "disclaimer.annotation.window.title", "disclaimer.annotation.revision.version", "disclaimer.annotation.message.header", null);
/*     */ 
/*  38 */     addActionListeners();
/*     */   }
/*     */ 
/*     */   private boolean isContDiclaimSelected() {
/*  42 */     return super.isContinueDiclaimingSelected();
/*     */   }
/*     */ 
/*     */   private void addActionListeners()
/*     */   {
/*  47 */     super.addAgreeBtnActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  49 */         FullAccessDisclaimDialog.access$002(FullAccessDisclaimDialog.this, true);
/*  50 */         if (FullAccessDisclaimDialog.this.pack != null) {
/*  51 */           FullAccessDisclaimDialog.this.pack.setFullAccess(true);
/*     */         }
/*  53 */         NotificationUtils.getInstance().postWarningMessage(LocalizationManager.getTextWithArgumentKeys("disclaimer.annotation.notif.message", new Object[] { "disclaimer.annotation.revision.version" }), false);
/*  54 */         if (FullAccessDisclaimDialog.this.isContDiclaimSelected()) {
/*  55 */           NotificationUtils.getInstance().postWarningMessage(LocalizationManager.getTextWithArgumentKeys("disclaimer.annotation.stop.disc.notif.msg", new Object[] { "disclaimer.annotation.revision.version" }), false);
/*  56 */           FullAccessDisclaimDialog.storage.saveFullAccessDiscState(true);
/*     */         }
/*  58 */         FullAccessDisclaimDialog.this.dispose();
/*     */       }
/*     */     });
/*  62 */     super.addNotAgreeBtnActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  64 */         FullAccessDisclaimDialog.access$002(FullAccessDisclaimDialog.this, false);
/*  65 */         if (FullAccessDisclaimDialog.this.pack != null)
/*  66 */           FullAccessDisclaimDialog.this.pack.setFullAccess(false);
/*  67 */         FullAccessDisclaimDialog.this.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public boolean isAccepted()
/*     */   {
/*  75 */     return (storage.restoreFullAccessDiscState()) || (this.isTempAccepted);
/*     */   }
/*     */ 
/*     */   public void showDialog()
/*     */   {
/*  82 */     super.showDisclaimer();
/*     */   }
/*     */ 
/*     */   public boolean showDialog(JFXPack pack)
/*     */   {
/*  87 */     this.pack = pack;
/*  88 */     if (!isAccepted())
/*  89 */       super.showDisclaimer();
/*     */     else {
/*  91 */       pack.setFullAccess(true);
/*     */     }
/*  93 */     return isAccepted();
/*     */   }
/*     */ 
/*     */   public static void reset()
/*     */   {
/*  98 */     instance = null;
/*     */   }
/*     */ 
/*     */   public boolean isPermAccepted()
/*     */   {
/* 103 */     return storage.restoreFullAccessDiscState();
/*     */   }
/*     */ 
/*     */   public boolean isTempAccepted()
/*     */   {
/* 108 */     return this.isTempAccepted;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.FullAccessDisclaimDialog
 * JD-Core Version:    0.6.0
 */