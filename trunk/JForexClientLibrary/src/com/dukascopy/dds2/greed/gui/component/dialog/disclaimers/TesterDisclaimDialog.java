/*    */ package com.dukascopy.dds2.greed.gui.component.dialog.disclaimers;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ 
/*    */ public class TesterDisclaimDialog extends DisclaimerMessageDialog
/*    */ {
/* 16 */   private static ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*    */   private static TesterDisclaimDialog instance;
/*    */   private boolean isAccepted;
/*    */ 
/*    */   private TesterDisclaimDialog()
/*    */   {
/* 21 */     super("disclaimer.tester.message", new Object[] { GuiUtilsAndConstants.LABEL_SHORT_NAME, GuiUtilsAndConstants.LABEL_SHORT_NAME, GuiUtilsAndConstants.LABEL_LONG_NAME }, "disclaimer.tester.window.title", "disclaimer.tester.revision.version", "disclaimer.tester.message.header", null);
/*    */ 
/* 29 */     addActionListeners();
/*    */   }
/*    */ 
/*    */   public static TesterDisclaimDialog getInstance() {
/* 33 */     if (instance == null) {
/* 34 */       instance = new TesterDisclaimDialog();
/*    */     }
/* 36 */     return instance;
/*    */   }
/*    */ 
/*    */   public static boolean isAcceptState() {
/* 40 */     return storage.restoreTesterDiscState();
/*    */   }
/*    */ 
/*    */   private void addActionListeners() {
/* 44 */     super.addAgreeBtnActionListener(new ActionListener() {
/*    */       public void actionPerformed(ActionEvent e) {
/* 46 */         TesterDisclaimDialog.access$002(TesterDisclaimDialog.this, true);
/*    */ 
/* 48 */         NotificationUtils.getInstance().postWarningMessage(LocalizationManager.getTextWithArgumentKeys("disclaimer.tester.notif.message", new Object[] { "disclaimer.tester.revision.version" }), false);
/* 49 */         if (TesterDisclaimDialog.this.isContDiclaimSelected()) {
/* 50 */           TesterDisclaimDialog.storage.saveTesterDiscState(true);
/* 51 */           NotificationUtils.getInstance().postWarningMessage(LocalizationManager.getTextWithArgumentKeys("disclaimer.tester.stop.disc.notif.msg", new Object[] { "disclaimer.tester.revision.version" }), false);
/*    */         }
/* 53 */         TesterDisclaimDialog.this.dispose();
/*    */       }
/*    */     });
/* 56 */     super.addNotAgreeBtnActionListener(new ActionListener() {
/*    */       public void actionPerformed(ActionEvent e) {
/* 58 */         TesterDisclaimDialog.access$002(TesterDisclaimDialog.this, false);
/* 59 */         TesterDisclaimDialog.storage.saveTesterDiscState(false);
/* 60 */         TesterDisclaimDialog.this.dispose();
/*    */       } } );
/*    */   }
/*    */ 
/*    */   private boolean isContDiclaimSelected() {
/* 66 */     return super.isContinueDiclaimingSelected();
/*    */   }
/*    */ 
/*    */   public boolean isTempAccepted()
/*    */   {
/* 71 */     return this.isAccepted;
/*    */   }
/*    */ 
/*    */   public boolean isPermAccepted()
/*    */   {
/* 76 */     return storage.restoreTesterDiscState();
/*    */   }
/*    */ 
/*    */   public void showDialog()
/*    */   {
/* 81 */     super.showDisclaimer();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.TesterDisclaimDialog
 * JD-Core Version:    0.6.0
 */