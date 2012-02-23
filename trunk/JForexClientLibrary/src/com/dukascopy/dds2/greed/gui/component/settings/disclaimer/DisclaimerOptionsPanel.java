/*     */ package com.dukascopy.dds2.greed.gui.component.settings.disclaimer;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.FullAccessDisclaimDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.RemoteStrategyDisclaimerDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.StrategyDisclaimerDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.TesterDisclaimDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import com.dukascopy.dds2.greed.util.IFullAccessDisclaimer;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class DisclaimerOptionsPanel extends JPanel
/*     */ {
/*     */   private JLocalizableRoundedBorder mainBorder;
/*     */ 
/*     */   public DisclaimerOptionsPanel()
/*     */   {
/*  33 */     init();
/*     */   }
/*     */ 
/*     */   private void init() {
/*  37 */     setBorder(getMainBorder());
/*  38 */     setLayout(new GridBagLayout());
/*     */ 
/*  40 */     build();
/*     */   }
/*     */ 
/*     */   private JLocalizableRoundedBorder getMainBorder() {
/*  44 */     if (this.mainBorder == null) {
/*  45 */       this.mainBorder = new JLocalizableRoundedBorder(this, "border.disclaimer.options");
/*     */     }
/*  47 */     return this.mainBorder;
/*     */   }
/*     */ 
/*     */   private JLocalizableLabel getLabel(String labelText) {
/*  51 */     return new JLocalizableLabel(labelText);
/*     */   }
/*     */ 
/*     */   private JLocalizableButton getViewButton(DisclaimerType type)
/*     */   {
/*  56 */     JLocalizableButton viewButton = new JLocalizableButton("button.view");
/*  57 */     viewButton.setMinimumSize(new Dimension(5, 10));
/*     */ 
/*  59 */     viewButton.addActionListener(new ActionListener(type)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  64 */         SettingsTabbedFrame.getInstance().setAlwaysOnTop(false);
/*     */ 
/*  66 */         switch (DisclaimerOptionsPanel.2.$SwitchMap$com$dukascopy$dds2$greed$gui$component$settings$disclaimer$DisclaimerOptionsPanel$DisclaimerType[this.val$type.ordinal()]) {
/*     */         case 1:
/*  68 */           StrategyDisclaimerDialog.getInstance().showDialog(); break;
/*     */         case 2:
/*  70 */           TesterDisclaimDialog.getInstance().showDialog(); break;
/*     */         case 3:
/*  72 */           FullAccessDisclaimDialog.getInstance().showDialog(); break;
/*     */         case 4:
/*  74 */           RemoteStrategyDisclaimerDialog.getInstance().showDialog();
/*     */         }
/*     */       }
/*     */     });
/*  80 */     return viewButton;
/*     */   }
/*     */ 
/*     */   private void build()
/*     */   {
/*  85 */     setLayout(new GridBagLayout());
/*  86 */     GridBagConstraints gbc = new GridBagConstraints();
/*  87 */     gbc.fill = 2;
/*  88 */     gbc.anchor = 21;
/*  89 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, this, getLabel("disclaimer.options.strategies"));
/*  90 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, this, getViewButton(DisclaimerType.STRATEGIES));
/*  91 */     GridBagLayoutHelper.add(2, 0, 1.0D, 0.0D, 1, 3, 5, 5, 0, 0, gbc, this, new JPanel());
/*  92 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, this, getLabel("disclaimer.options.remote.strategies"));
/*  93 */     GridBagLayoutHelper.add(1, 1, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, this, getViewButton(DisclaimerType.REMOTE_STRATEGIES));
/*  94 */     GridBagLayoutHelper.add(0, 2, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, this, getLabel("disclaimer.options.tester"));
/*  95 */     GridBagLayoutHelper.add(1, 2, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, this, getViewButton(DisclaimerType.TESTER));
/*  96 */     GridBagLayoutHelper.add(0, 3, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, this, getLabel("disclaimer.options.annotations"));
/*  97 */     GridBagLayoutHelper.add(1, 3, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, this, getViewButton(DisclaimerType.ANNOTATION));
/*     */   }
/*     */ 
/*     */   private static enum DisclaimerType {
/* 101 */     TESTER, 
/* 102 */     ANNOTATION, 
/* 103 */     STRATEGIES, 
/* 104 */     REMOTE_STRATEGIES;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.disclaimer.DisclaimerOptionsPanel
 * JD-Core Version:    0.6.0
 */