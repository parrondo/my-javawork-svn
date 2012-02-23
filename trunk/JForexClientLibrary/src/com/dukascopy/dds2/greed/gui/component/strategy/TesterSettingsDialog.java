/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.file.filter.TemplateFileFilter;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ 
/*     */ public class TesterSettingsDialog extends JDialog
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  61 */   private final int DIALOG_WIDTH = 600;
/*  62 */   private final int DIALOG_HEIGHT = 330;
/*     */ 
/*  64 */   private TesterParameters testerParameters = null;
/*     */ 
/*  66 */   private boolean canceled = true;
/*     */   private JComboBox templateCombo;
/*     */   private JComboBox periodCombo;
/*     */   private JCheckBox equityCheckBox;
/*     */   private JCheckBox balanceCheckBox;
/*     */   private JCheckBox plCheckBox;
/*  74 */   private List<String> chartTemplates = null;
/*  75 */   private List<JForexPeriod> chartPeriods = null;
/*     */ 
/*  77 */   private TesterFileMessagesSelectionPanel fileMessagesSelectionPanel = null;
/*  78 */   private TesterFileReportsSelectionPanel fileReportsSelectionPanel = null;
/*     */   private JCheckBox showReportCheckBox;
/*     */ 
/*     */   public TesterSettingsDialog(TesterParameters testerParameters)
/*     */   {
/*  82 */     super((JFrame)GreedContext.get("clientGui"), true);
/*  83 */     setDefaultCloseOperation(2);
/*  84 */     if (testerParameters == null) {
/*  85 */       throw new IllegalArgumentException("testerParameters is empty");
/*     */     }
/*     */ 
/*  88 */     this.testerParameters = testerParameters;
/*     */ 
/*  90 */     buildUI();
/*     */ 
/*  92 */     setTitle(LocalizationManager.getText("dialog.tester.settings"));
/*  93 */     setSize(600, 330);
/*  94 */     setMinimumSize(new Dimension(600, 330));
/*     */ 
/*  96 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/*  97 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private void buildUI() {
/* 101 */     JPanel mainPanel = new JPanel(new GridBagLayout());
/*     */ 
/* 103 */     GridBagLayoutHelper.add(0, 0, 1.0D, 1.0D, 1, 1, 10, 10, 10, 0, 1, 18, new GridBagConstraints(), mainPanel, getVMSettingsPanel());
/* 104 */     GridBagLayoutHelper.add(0, 1, 1.0D, 1.0D, 1, 1, 10, 5, 10, 0, 1, 18, new GridBagConstraints(), mainPanel, getFilesMessagesPanel());
/* 105 */     GridBagLayoutHelper.add(0, 2, 1.0D, 1.0D, 1, 1, 10, 0, 10, 10, 1, 18, new GridBagConstraints(), mainPanel, getControlsPanel());
/*     */ 
/* 107 */     setContentPane(mainPanel);
/*     */   }
/*     */ 
/*     */   private JPanel getControlsPanel() {
/* 111 */     JPanel controlsPanel = new JPanel(new FlowLayout(2, 5, 0));
/*     */ 
/* 113 */     JButton okButton = new JLocalizableButton("button.ok");
/* 114 */     okButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 118 */         TesterSettingsDialog.this.testerParameters.setShowEquityIndicator(TesterSettingsDialog.this.equityCheckBox.isSelected());
/* 119 */         TesterSettingsDialog.this.testerParameters.setShowBalanceIndicator(TesterSettingsDialog.this.balanceCheckBox.isSelected());
/* 120 */         TesterSettingsDialog.this.testerParameters.setShowPlIndicator(TesterSettingsDialog.this.plCheckBox.isSelected());
/*     */ 
/* 122 */         if ((TesterSettingsDialog.this.templateCombo.getSelectedItem() instanceof String)) {
/* 123 */           String template = (String)TesterSettingsDialog.this.templateCombo.getSelectedItem();
/* 124 */           TesterSettingsDialog.this.testerParameters.setChartTemplate(template);
/*     */         } else {
/* 126 */           TesterSettingsDialog.this.testerParameters.setChartTemplate(null);
/*     */         }
/*     */ 
/* 129 */         if ((TesterSettingsDialog.this.periodCombo.getSelectedItem() instanceof JForexPeriod)) {
/* 130 */           JForexPeriod period = (JForexPeriod)TesterSettingsDialog.this.periodCombo.getSelectedItem();
/* 131 */           TesterSettingsDialog.this.testerParameters.setChartPeriod(period);
/*     */         } else {
/* 133 */           TesterSettingsDialog.this.testerParameters.setChartPeriod(null);
/*     */         }
/*     */ 
/* 136 */         TesterSettingsDialog.this.testerParameters.setSaveMessages(TesterSettingsDialog.this.fileMessagesSelectionPanel.isSelected());
/* 137 */         TesterSettingsDialog.this.testerParameters.setMessagesFile(TesterSettingsDialog.this.fileMessagesSelectionPanel.getFile());
/*     */ 
/* 139 */         TesterSettingsDialog.this.testerParameters.setSaveReportFile(TesterSettingsDialog.this.fileReportsSelectionPanel.isSelected());
/* 140 */         TesterSettingsDialog.this.testerParameters.setReportFile(TesterSettingsDialog.this.fileReportsSelectionPanel.getFile());
/*     */ 
/* 142 */         TesterSettingsDialog.this.testerParameters.setShowReport(TesterSettingsDialog.this.showReportCheckBox.isSelected());
/*     */ 
/* 144 */         TesterSettingsDialog.access$902(TesterSettingsDialog.this, false);
/* 145 */         TesterSettingsDialog.this.dispose();
/*     */       }
/*     */     });
/* 150 */     JButton cancelButton = new JLocalizableButton("button.cancel");
/* 151 */     cancelButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 155 */         TesterSettingsDialog.access$902(TesterSettingsDialog.this, true);
/* 156 */         TesterSettingsDialog.this.dispose();
/*     */       }
/*     */     });
/* 160 */     controlsPanel.add(okButton);
/* 161 */     controlsPanel.add(cancelButton);
/*     */ 
/* 163 */     return controlsPanel;
/*     */   }
/*     */ 
/*     */   private JPanel getVMSettingsPanel()
/*     */   {
/* 168 */     JPanel vmSettingsPanel = new JPanel();
/* 169 */     JLocalizableRoundedBorder roundedBorder = new JLocalizableRoundedBorder(vmSettingsPanel, "tester.visual.mode.settings");
/* 170 */     vmSettingsPanel.setBorder(roundedBorder);
/* 171 */     vmSettingsPanel.setLayout(new GridBagLayout());
/*     */ 
/* 173 */     JLabel templateLabel = new JLocalizableLabel("tester.parameters.table.column.template");
/*     */ 
/* 175 */     JLabel periodLabel = new JLocalizableLabel("tester.parameters.table.column.period");
/*     */ 
/* 177 */     this.templateCombo = new JComboBox(getChartTemplates().toArray(new String[0]));
/*     */ 
/* 179 */     this.templateCombo.setRenderer(new DefaultListCellRenderer() {
/*     */       private static final long serialVersionUID = 1L;
/*     */ 
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 184 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*     */ 
/* 186 */         String text = "";
/* 187 */         if (value != null) {
/* 188 */           text = value.toString();
/*     */         }
/* 190 */         return TesterSettingsDialog.this.getComboLabel(text, comp.getForeground(), comp.getBackground());
/*     */       }
/*     */     });
/* 195 */     if ((this.testerParameters.getChartTemplate() != null) && (!this.testerParameters.getChartTemplate().isEmpty())) {
/* 196 */       this.templateCombo.setSelectedItem(this.testerParameters.getChartTemplate());
/*     */     }
/*     */ 
/* 199 */     this.periodCombo = new JComboBox(getChartPeriods().toArray(new JForexPeriod[0]));
/* 200 */     this.periodCombo.insertItemAt(" ", 0);
/*     */ 
/* 202 */     this.periodCombo.setRenderer(new DefaultListCellRenderer() {
/*     */       private static final long serialVersionUID = 1L;
/*     */ 
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 207 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 208 */         if ((value instanceof JForexPeriod)) {
/* 209 */           JForexPeriod dtpw = (JForexPeriod)value;
/* 210 */           return TesterSettingsDialog.this.getComboLabel(ChartsLocalizator.localize(dtpw), comp.getForeground(), comp.getBackground());
/*     */         }
/*     */ 
/* 213 */         return comp;
/*     */       }
/*     */     });
/* 217 */     if (this.testerParameters.getChartPeriod() != null)
/* 218 */       this.periodCombo.setSelectedItem(this.testerParameters.getChartPeriod());
/*     */     else {
/* 220 */       this.periodCombo.setSelectedIndex(0);
/*     */     }
/*     */ 
/* 223 */     this.equityCheckBox = new JLocalizableCheckBox("button.show.equity");
/* 224 */     this.equityCheckBox.setSelected(this.testerParameters.isShowEquityIndicator());
/*     */ 
/* 226 */     this.balanceCheckBox = new JLocalizableCheckBox("button.show.balance");
/* 227 */     this.balanceCheckBox.setSelected(this.testerParameters.isShowBalanceIndicator());
/*     */ 
/* 229 */     this.plCheckBox = new JLocalizableCheckBox("button.show.profit.loss");
/* 230 */     this.plCheckBox.setSelected(this.testerParameters.isShowPlIndicator());
/*     */ 
/* 232 */     GridBagConstraints gbc = new GridBagConstraints();
/* 233 */     gbc.fill = 2;
/*     */ 
/* 235 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 2, 5, 5, 2, gbc, vmSettingsPanel, this.equityCheckBox);
/* 236 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, 0, 5, 5, 2, gbc, vmSettingsPanel, this.balanceCheckBox);
/* 237 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 1, 1, 0, 5, 5, 2, gbc, vmSettingsPanel, this.plCheckBox);
/*     */ 
/* 239 */     GridBagLayoutHelper.add(0, 1, 0.7D, 0.0D, 3, 1, 7, 5, 5, 2, gbc, vmSettingsPanel, templateLabel);
/* 240 */     GridBagLayoutHelper.add(3, 1, 0.3D, 0.0D, 1, 1, 2, 5, 5, 2, gbc, vmSettingsPanel, periodLabel);
/*     */ 
/* 242 */     GridBagLayoutHelper.add(0, 2, 0.6D, 0.0D, 3, 1, 5, 0, 5, 2, gbc, vmSettingsPanel, this.templateCombo);
/* 243 */     GridBagLayoutHelper.add(3, 2, 0.4D, 0.0D, 1, 1, 0, 0, 5, 2, gbc, vmSettingsPanel, this.periodCombo);
/*     */ 
/* 245 */     return vmSettingsPanel;
/*     */   }
/*     */ 
/*     */   private JPanel getFilesMessagesPanel() {
/* 249 */     JPanel filesMessagesPanel = new JPanel();
/* 250 */     filesMessagesPanel.setLayout(new GridBagLayout());
/*     */ 
/* 252 */     JLocalizableRoundedBorder roundedBorder = new JLocalizableRoundedBorder(filesMessagesPanel, "header.messages");
/* 253 */     filesMessagesPanel.setBorder(roundedBorder);
/*     */ 
/* 255 */     this.fileMessagesSelectionPanel = new TesterFileMessagesSelectionPanel("label.save.messages.to.file", "", this.testerParameters);
/*     */ 
/* 261 */     this.fileReportsSelectionPanel = new TesterFileReportsSelectionPanel("tester.save.reports.to.file", "", this.testerParameters);
/*     */ 
/* 267 */     this.showReportCheckBox = new JLocalizableCheckBox("tester.show.reports");
/* 268 */     this.showReportCheckBox.setSelected(this.testerParameters.isShowReport());
/*     */ 
/* 270 */     GridBagConstraints gbc = new GridBagConstraints();
/* 271 */     gbc.fill = 2;
/* 272 */     gbc.anchor = 17;
/*     */ 
/* 274 */     List fileMessagesComponents = this.fileMessagesSelectionPanel.getPanelComponents();
/* 275 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 10, 0, 3, gbc, filesMessagesPanel, (Component)fileMessagesComponents.get(0));
/* 276 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 0, 10, 0, 3, gbc, filesMessagesPanel, (Component)fileMessagesComponents.get(1));
/* 277 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 1, 1, 10, 10, 0, 3, gbc, filesMessagesPanel, (Component)fileMessagesComponents.get(2));
/*     */ 
/* 279 */     List fileReportsSelectionComponents = this.fileReportsSelectionPanel.getPanelComponents();
/* 280 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, filesMessagesPanel, (Component)fileReportsSelectionComponents.get(0));
/* 281 */     GridBagLayoutHelper.add(1, 1, 1.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, filesMessagesPanel, (Component)fileReportsSelectionComponents.get(1));
/* 282 */     GridBagLayoutHelper.add(2, 1, 0.0D, 0.0D, 1, 1, 10, 0, 0, 0, gbc, filesMessagesPanel, (Component)fileReportsSelectionComponents.get(2));
/*     */ 
/* 284 */     GridBagLayoutHelper.add(0, 2, 0.0D, 0.0D, 1, 1, 0, 2, 0, 0, gbc, filesMessagesPanel, this.showReportCheckBox);
/*     */ 
/* 286 */     return filesMessagesPanel;
/*     */   }
/*     */ 
/*     */   public boolean isCanceled() {
/* 290 */     return this.canceled;
/*     */   }
/*     */ 
/*     */   private List<String> getChartTemplates() {
/* 294 */     if (this.chartTemplates == null) {
/* 295 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 296 */       TemplateFileFilter templateFileFilter = new TemplateFileFilter(LocalizationManager.getText("jforex.chart.template.files"));
/* 297 */       File myDir = new File(clientSettingsStorage.getMyChartTemplatesPath());
/*     */ 
/* 299 */       File[] contents = myDir.listFiles(new FileFilter(templateFileFilter)
/*     */       {
/*     */         public boolean accept(File file)
/*     */         {
/* 308 */           return (file != null) && (!file.isDirectory()) && (file.getName() != null) && (file.getName().toLowerCase().endsWith("." + this.val$templateFileFilter.getExtension())) && (file.getName().length() > this.val$templateFileFilter.getExtension().length() + 1);
/*     */         }
/*     */       });
/* 314 */       this.chartTemplates = new ArrayList();
/*     */ 
/* 317 */       if (contents.length > 0) {
/* 318 */         this.chartTemplates.add(" ");
/*     */       }
/* 320 */       for (File file : contents) {
/* 321 */         String fileName = file.getName();
/* 322 */         int extensionIndex = fileName.lastIndexOf(".");
/* 323 */         if (extensionIndex != -1) {
/* 324 */           fileName = fileName.substring(0, extensionIndex);
/*     */         }
/* 326 */         this.chartTemplates.add(fileName);
/*     */       }
/*     */     }
/*     */ 
/* 330 */     Collections.sort(this.chartTemplates);
/*     */ 
/* 332 */     return this.chartTemplates;
/*     */   }
/*     */ 
/*     */   private List<JForexPeriod> getChartPeriods() {
/* 336 */     if (this.chartPeriods == null) {
/* 337 */       List periods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreChartPeriods();
/* 338 */       this.chartPeriods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).sortChartPeriods(periods);
/*     */     }
/*     */ 
/* 341 */     return this.chartPeriods;
/*     */   }
/*     */ 
/*     */   private JLabel getComboLabel(String text, Color fg, Color bg) {
/* 345 */     JLabel label = new JResizableLabel();
/* 346 */     label.setText(text);
/* 347 */     label.setOpaque(true);
/* 348 */     label.setForeground(fg);
/* 349 */     label.setBackground(bg);
/* 350 */     label.setBorder(new EmptyBorder(new Insets(0, 3, 0, 0)));
/* 351 */     return label;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.TesterSettingsDialog
 * JD-Core Version:    0.6.0
 */