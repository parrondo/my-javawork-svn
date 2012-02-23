/*     */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.utils.file.DCFileChooser;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.DataField;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportDataParameters;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl.State;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControlListener;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class DirectorySelectionPanel extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  40 */   private static final Logger LOGGER = LoggerFactory.getLogger(DirectorySelectionPanel.class);
/*     */   private ExportProcessControl exportProcessControl;
/*     */   private ExportDataParameters exportDataParameters;
/*  46 */   private JHintTextField directoryNameField = new JHintTextField("hdm.export.data.to");
/*  47 */   private JLocalizableButton btnBrowseFile = new JLocalizableButton("tester.file.browse");
/*     */ 
/*  49 */   File currentDir = FileSystemView.getFileSystemView().getDefaultDirectory();
/*     */ 
/*     */   public DirectorySelectionPanel(ExportProcessControl exportProcessControl, ExportDataParameters exportDataParameters)
/*     */   {
/*  55 */     this.exportProcessControl = exportProcessControl;
/*  56 */     this.exportDataParameters = exportDataParameters;
/*     */ 
/*  58 */     buildUI();
/*  59 */     subscribeToExportProcessControlListener();
/*     */   }
/*     */ 
/*     */   private void buildUI() {
/*  63 */     setLayout(new GridBagLayout());
/*  64 */     add(this.directoryNameField, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 21, 2, new Insets(0, 0, 0, 0), 0, 0));
/*     */ 
/*  67 */     add(this.btnBrowseFile, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 21, 2, new Insets(0, 5, 0, 0), 0, 0));
/*     */ 
/*  71 */     this.directoryNameField.setText(this.currentDir.getAbsolutePath());
/*     */ 
/*  73 */     this.btnBrowseFile.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  76 */         DirectorySelectionPanel.this.selectDirectory();
/*     */       }
/*     */     });
/*  80 */     this.directoryNameField.setMinimumSize(new Dimension(613, this.directoryNameField.getPreferredSize().height));
/*  81 */     this.directoryNameField.setPreferredSize(new Dimension(613, this.directoryNameField.getPreferredSize().height));
/*     */   }
/*     */ 
/*     */   private void subscribeToExportProcessControlListener() {
/*  85 */     this.exportProcessControl.addExportControlListener(new ExportProcessControlListener()
/*     */     {
/*     */       public void stateChanged(ExportProcessControl.State state) {
/*  88 */         switch (DirectorySelectionPanel.3.$SwitchMap$com$dukascopy$dds2$greed$export$historicaldata$ExportProcessControl$State[state.ordinal()]) {
/*     */         case 1:
/*  90 */           DirectorySelectionPanel.this.setUIInitialState();
/*  91 */           break;
/*     */         case 2:
/*  93 */           DirectorySelectionPanel.this.exportDataParameters.setOutputDirectory(DirectorySelectionPanel.this.directoryNameField.getText().trim());
/*  94 */           DirectorySelectionPanel.this.setUIExecutionState();
/*  95 */           break;
/*     */         case 3:
/*  97 */           DirectorySelectionPanel.this.setUIInitialState();
/*  98 */           break;
/*     */         case 4:
/* 100 */           DirectorySelectionPanel.this.setUIInitialState();
/* 101 */           break;
/*     */         default:
/* 102 */           throw new IllegalArgumentException("Incorrect state type: " + state);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void progressChanged(int progressValue, String progressBarText)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void validated(DataField dataField, boolean error, Instrument instrument, int column, String errorText)
/*     */       {
/* 112 */         if (DataField.EXPORT_DIRECTORY == dataField)
/* 113 */           DirectorySelectionPanel.this.directoryNameField.setError(error);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void setUIInitialState()
/*     */   {
/* 121 */     this.btnBrowseFile.setEnabled(true);
/* 122 */     this.directoryNameField.setEnabled(true);
/*     */ 
/* 124 */     String outputDirectory = this.exportDataParameters.getOutputDirectory();
/* 125 */     if ((outputDirectory != null) && (outputDirectory.length() > 0))
/* 126 */       this.directoryNameField.setText(outputDirectory);
/*     */   }
/*     */ 
/*     */   private void setUIExecutionState()
/*     */   {
/* 131 */     this.btnBrowseFile.setEnabled(false);
/* 132 */     this.directoryNameField.setEnabled(false);
/*     */   }
/*     */ 
/*     */   private void selectDirectory() {
/* 136 */     String title = LocalizationManager.getText("dialog.save.data.folder.chooser.title");
/* 137 */     String buttonText = LocalizationManager.getText("dialog.save.data.folder.chooser.select");
/* 138 */     UIManager.put("FileChooser.cancelButtonText", LocalizationManager.getText("cancel.button.text"));
/*     */ 
/* 140 */     DCFileChooser chooser = DCFileChooser.createDCFileChooser(this.currentDir.getAbsolutePath(), null);
/* 141 */     chooser.setDialogTitle(title);
/* 142 */     chooser.setMultiSelectionEnabled(false);
/* 143 */     chooser.setFileSelectionMode(1);
/*     */ 
/* 145 */     File currentFile = new File(this.directoryNameField.getText());
/* 146 */     if (currentFile.exists()) {
/* 147 */       chooser.setSelectedFile(currentFile);
/* 148 */       chooser.setCurrentDirectory(currentFile.getParentFile());
/*     */     }
/*     */ 
/* 151 */     int confirm = chooser.showDialog(getParent(), buttonText);
/* 152 */     if (confirm == 0)
/* 153 */       this.directoryNameField.setText(chooser.getSelectedFile().getAbsolutePath());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.DirectorySelectionPanel
 * JD-Core Version:    0.6.0
 */