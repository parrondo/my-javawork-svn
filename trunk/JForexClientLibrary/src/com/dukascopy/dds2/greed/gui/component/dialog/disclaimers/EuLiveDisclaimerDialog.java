/*     */ package com.dukascopy.dds2.greed.gui.component.dialog.disclaimers;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ 
/*     */ public class EuLiveDisclaimerDialog extends DisclaimerMessageDialog
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static EuLiveDisclaimerDialog instance;
/*  27 */   private boolean isAccepted = false;
/*     */ 
/*     */   private EuLiveDisclaimerDialog()
/*     */   {
/*  37 */     super("disclaimer.eu.live.message", null, "disclaimer.eu.live.window.title", "disclaimer.eu.live.revision.version", "disclaimer.eu.live.message.header", GuiUtilsAndConstants.EU_PLATFPORM_LOGO);
/*     */ 
/*  44 */     addActionListenersOnButtons();
/*     */   }
/*     */ 
/*     */   public static EuLiveDisclaimerDialog getInstance() {
/*  48 */     if (instance == null) {
/*  49 */       instance = new EuLiveDisclaimerDialog();
/*     */     }
/*  51 */     return instance;
/*     */   }
/*     */ 
/*     */   private void addActionListenersOnButtons()
/*     */   {
/*  56 */     addAgreeBtnActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  59 */         EuLiveDisclaimerDialog.access$002(EuLiveDisclaimerDialog.this, true);
/*  60 */         EuLiveDisclaimerDialog.this.finishConfirmation();
/*     */       }
/*     */     });
/*  65 */     addNotAgreeBtnActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  68 */         EuLiveDisclaimerDialog.access$002(EuLiveDisclaimerDialog.this, false);
/*  69 */         EuLiveDisclaimerDialog.this.finishConfirmation();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void finishConfirmation() {
/*  76 */     super.setModal(false);
/*  77 */     super.setVisible(false);
/*  78 */     super.dispose();
/*     */   }
/*     */ 
/*     */   public void showDialog()
/*     */   {
/*  83 */     super.showDisclaimer();
/*     */   }
/*     */ 
/*     */   protected void initCheckPanel() {
/*     */   }
/*     */ 
/*     */   public void reset() {
/*  90 */     this.isAccepted = false;
/*     */   }
/*     */ 
/*     */   public boolean isPermAccepted()
/*     */   {
/*  95 */     return this.isAccepted;
/*     */   }
/*     */ 
/*     */   public boolean isTempAccepted()
/*     */   {
/* 100 */     return isPermAccepted();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.EuLiveDisclaimerDialog
 * JD-Core Version:    0.6.0
 */