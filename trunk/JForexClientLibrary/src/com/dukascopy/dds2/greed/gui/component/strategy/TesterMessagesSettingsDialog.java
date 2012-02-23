/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.Component;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.List;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class TesterMessagesSettingsDialog extends JDialog
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  30 */   private final int DIALOG_WIDTH = 600;
/*  31 */   private final int DIALOG_HEIGHT = 140;
/*     */ 
/*  33 */   private TesterFileMessagesSelectionPanel fileMessagesSelectionPanel = null;
/*  34 */   private TesterFileReportsSelectionPanel fileReportsSelectionPanel = null;
/*     */ 
/*  36 */   private TesterParameters testerParameters = null;
/*     */ 
/*  38 */   private boolean canceled = true;
/*     */ 
/*     */   public TesterMessagesSettingsDialog(TesterParameters testerParameters) {
/*  41 */     super((JFrame)GreedContext.get("clientGui"), true);
/*  42 */     setDefaultCloseOperation(2);
/*  43 */     if (testerParameters == null) {
/*  44 */       throw new IllegalArgumentException("testerParameters is empty");
/*     */     }
/*     */ 
/*  47 */     this.testerParameters = testerParameters;
/*     */ 
/*  49 */     buildUI();
/*     */ 
/*  51 */     setTitle(LocalizationManager.getText("header.messages"));
/*  52 */     setSize(600, 140);
/*     */ 
/*  54 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/*  55 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public boolean isCanceled() {
/*  59 */     return this.canceled;
/*     */   }
/*     */ 
/*     */   private void buildUI() {
/*  63 */     JPanel mainPanel = new JPanel();
/*  64 */     mainPanel.setLayout(new GridLayout());
/*     */ 
/*  66 */     mainPanel.add(getFilesMessagesPanel());
/*  67 */     setContentPane(mainPanel);
/*     */   }
/*     */ 
/*     */   private JPanel getFilesMessagesPanel() {
/*  71 */     JPanel mainPanel = new JPanel();
/*  72 */     mainPanel.setLayout(new GridBagLayout());
/*     */ 
/*  74 */     this.fileMessagesSelectionPanel = new TesterFileMessagesSelectionPanel("label.save.messages.to.file", "tester-messages.csv", this.testerParameters);
/*     */ 
/*  80 */     this.fileReportsSelectionPanel = new TesterFileReportsSelectionPanel("tester.save.reports.to.file", "tester-report.html", this.testerParameters);
/*     */ 
/*  86 */     JButton okButton = new JLocalizableButton("button.ok");
/*  87 */     okButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*  91 */         TesterMessagesSettingsDialog.this.testerParameters.setSaveMessages(TesterMessagesSettingsDialog.this.fileMessagesSelectionPanel.isSelected());
/*  92 */         TesterMessagesSettingsDialog.this.testerParameters.setMessagesFile(TesterMessagesSettingsDialog.this.fileMessagesSelectionPanel.getFile());
/*     */ 
/*  94 */         TesterMessagesSettingsDialog.this.testerParameters.setSaveReportFile(TesterMessagesSettingsDialog.this.fileReportsSelectionPanel.isSelected());
/*  95 */         TesterMessagesSettingsDialog.this.testerParameters.setReportFile(TesterMessagesSettingsDialog.this.fileReportsSelectionPanel.getFile());
/*     */ 
/*  97 */         TesterMessagesSettingsDialog.access$302(TesterMessagesSettingsDialog.this, false);
/*  98 */         TesterMessagesSettingsDialog.this.dispose();
/*     */       }
/*     */     });
/* 103 */     JButton cancelButton = new JLocalizableButton("button.cancel");
/* 104 */     cancelButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 108 */         TesterMessagesSettingsDialog.access$302(TesterMessagesSettingsDialog.this, true);
/* 109 */         TesterMessagesSettingsDialog.this.dispose();
/*     */       }
/*     */     });
/* 114 */     GridBagConstraints gbc = new GridBagConstraints();
/* 115 */     gbc.fill = 2;
/* 116 */     gbc.anchor = 17;
/*     */ 
/* 118 */     List fileMessagesComponents = this.fileMessagesSelectionPanel.getPanelComponents();
/* 119 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 10, 15, 0, 0, gbc, mainPanel, (Component)fileMessagesComponents.get(0));
/* 120 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 0, 15, 0, 0, gbc, mainPanel, (Component)fileMessagesComponents.get(1));
/* 121 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 1, 1, 3, 15, 10, 0, gbc, mainPanel, (Component)fileMessagesComponents.get(2));
/*     */ 
/* 123 */     List fileReportsSelectionComponents = this.fileReportsSelectionPanel.getPanelComponents();
/* 124 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 10, 0, 0, 0, gbc, mainPanel, (Component)fileReportsSelectionComponents.get(0));
/* 125 */     GridBagLayoutHelper.add(1, 1, 1.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, mainPanel, (Component)fileReportsSelectionComponents.get(1));
/* 126 */     GridBagLayoutHelper.add(2, 1, 0.0D, 0.0D, 1, 1, 3, 0, 10, 0, gbc, mainPanel, (Component)fileReportsSelectionComponents.get(2));
/*     */ 
/* 128 */     gbc.anchor = 13;
/* 129 */     gbc.fill = 0;
/* 130 */     GridBagLayoutHelper.add(1, 2, 0.0D, 0.0D, 1, 1, 0, 10, 0, 15, gbc, mainPanel, okButton);
/*     */ 
/* 132 */     gbc.anchor = 17;
/* 133 */     gbc.fill = 2;
/* 134 */     GridBagLayoutHelper.add(2, 2, 0.0D, 0.0D, 1, 1, 3, 10, 10, 15, gbc, mainPanel, cancelButton);
/*     */ 
/* 136 */     return mainPanel;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.TesterMessagesSettingsDialog
 * JD-Core Version:    0.6.0
 */