/*     */ package com.dukascopy.dds2.greed.gui.component.settings.workspace;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.AbstractSettingsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.settings.autosaving.IClientSettingsStorageAutoSaving;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JSpinner.NumberEditor;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.Document;
/*     */ 
/*     */ public class WorkspaceSettingsPanel extends AbstractSettingsPanel
/*     */   implements DocumentListener, ActionListener, ChangeListener
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private WorkspaceOptionsPanel workspaceOptionsPanel;
/*     */   private ClientSettingsStorage settingsStorage;
/*     */ 
/*     */   public WorkspaceSettingsPanel(SettingsTabbedFrame parent)
/*     */   {
/*  31 */     super(parent);
/*     */   }
/*     */ 
/*     */   protected void build()
/*     */   {
/*  36 */     setLayout(new GridLayout(1, 1));
/*  37 */     add(getWorkspaceOptionsPanel());
/*     */   }
/*     */ 
/*     */   public void applySettings()
/*     */   {
/*  42 */     applyWorkspaceOptions();
/*     */   }
/*     */ 
/*     */   private void applyWorkspaceOptions() {
/*  46 */     Long workspaceAutoSavePeriod = Long.valueOf(0L);
/*  47 */     JSpinner spinner = getWorkspaceOptionsPanel().getWorkspaceAutoSavingPeriod();
/*     */ 
/*  49 */     if (getWorkspaceOptionsPanel().getEnableWorkspaceAutoSaving().isSelected()) {
/*     */       try {
/*  51 */         spinner.commitEdit();
/*     */       }
/*     */       catch (Exception e) {
/*  54 */         ((JSpinner.NumberEditor)spinner.getEditor()).getTextField().setValue(spinner.getValue());
/*     */       }
/*  56 */       workspaceAutoSavePeriod = new Long(((Integer)spinner.getValue()).intValue());
/*     */     }
/*     */ 
/*  59 */     getSettingsStorageAutoSaving().stopAutoSaving();
/*  60 */     getSettingsStorage().saveWorkspaceAutoSavePeriodInMinutes(workspaceAutoSavePeriod);
/*     */ 
/*  64 */     getSettingsStorageAutoSaving().startAutoSaving();
/*     */ 
/*  66 */     boolean saveOnExitEnabled = getWorkspaceOptionsPanel().getEnableWorkspaceSaveOnExit().isSelected();
/*  67 */     getSettingsStorage().saveWorkspaceSaveOnExitEnabled(new Boolean(saveOnExitEnabled));
/*     */   }
/*     */ 
/*     */   public void resetFields()
/*     */   {
/*  72 */     resetFieldsForWorkspaceOptions();
/*     */   }
/*     */ 
/*     */   public void resetToDefaults()
/*     */   {
/*  77 */     Long defaultAutoSavePeriod = getSettingsStorage().getDefaultWorkspaceAutoSavePeriodInMinutes();
/*  78 */     getWorkspaceOptionsPanel().getEnableWorkspaceAutoSaving().setSelected(true);
/*  79 */     getWorkspaceOptionsPanel().getWorkspaceAutoSavingPeriod().setValue(defaultAutoSavePeriod);
/*     */ 
/*  81 */     Boolean defaultSaveOnExit = getSettingsStorage().getDefaultWorkspaceSaveOnExitEnabled();
/*  82 */     getWorkspaceOptionsPanel().getEnableWorkspaceSaveOnExit().setSelected(defaultSaveOnExit.booleanValue());
/*     */   }
/*     */ 
/*     */   private void resetFieldsForWorkspaceOptions() {
/*  86 */     Long workspaceAutoSavePeriod = getSettingsStorage().restoreWorkspaceAutoSavePeriodInMinutes();
/*  87 */     if (workspaceAutoSavePeriod.longValue() > 0L) {
/*  88 */       getWorkspaceOptionsPanel().getEnableWorkspaceAutoSaving().setSelected(true);
/*  89 */       getWorkspaceOptionsPanel().getWorkspaceAutoSavingPeriod().setValue(workspaceAutoSavePeriod);
/*     */     }
/*     */     else {
/*  92 */       getWorkspaceOptionsPanel().getEnableWorkspaceAutoSaving().setSelected(false);
/*  93 */       getWorkspaceOptionsPanel().getWorkspaceAutoSavingPeriod().setValue(getSettingsStorage().getDefaultWorkspaceAutoSavePeriodInMinutes());
/*     */     }
/*     */ 
/*  96 */     Boolean workspaceSaveOnExit = getSettingsStorage().restoreWorkspaceSaveOnExitEnabled();
/*  97 */     getWorkspaceOptionsPanel().getEnableWorkspaceSaveOnExit().setSelected(workspaceSaveOnExit.booleanValue());
/*     */   }
/*     */ 
/*     */   public boolean verifySettings()
/*     */   {
/* 103 */     return true;
/*     */   }
/*     */ 
/*     */   public WorkspaceOptionsPanel getWorkspaceOptionsPanel() {
/* 107 */     if (this.workspaceOptionsPanel == null) {
/* 108 */       this.workspaceOptionsPanel = new WorkspaceOptionsPanel();
/*     */ 
/* 110 */       this.workspaceOptionsPanel.getEnableWorkspaceAutoSaving().addActionListener(this);
/* 111 */       this.workspaceOptionsPanel.getWorkspaceAutoSavingPeriod().getModel().addChangeListener(this);
/* 112 */       ((JSpinner.NumberEditor)this.workspaceOptionsPanel.getWorkspaceAutoSavingPeriod().getEditor()).getTextField().getDocument().addDocumentListener(this);
/*     */ 
/* 114 */       this.workspaceOptionsPanel.getEnableWorkspaceSaveOnExit().addActionListener(this);
/*     */     }
/* 116 */     return this.workspaceOptionsPanel;
/*     */   }
/*     */ 
/*     */   public ClientSettingsStorage getSettingsStorage() {
/* 120 */     if (this.settingsStorage == null) {
/* 121 */       this.settingsStorage = ((ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*     */     }
/* 123 */     return this.settingsStorage;
/*     */   }
/*     */ 
/*     */   public void insertUpdate(DocumentEvent e)
/*     */   {
/* 128 */     this.parent.settingsChanged(true);
/*     */   }
/*     */ 
/*     */   public void removeUpdate(DocumentEvent e)
/*     */   {
/* 133 */     this.parent.settingsChanged(true);
/*     */   }
/*     */ 
/*     */   public void changedUpdate(DocumentEvent e)
/*     */   {
/* 138 */     this.parent.settingsChanged(true);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 143 */     this.parent.settingsChanged(true);
/*     */   }
/*     */ 
/*     */   public void stateChanged(ChangeEvent e)
/*     */   {
/* 148 */     this.parent.settingsChanged(true);
/*     */   }
/*     */ 
/*     */   public IClientSettingsStorageAutoSaving getSettingsStorageAutoSaving() {
/* 152 */     return (IClientSettingsStorageAutoSaving)GreedContext.get("settingsStorageAutosaving");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.workspace.WorkspaceSettingsPanel
 * JD-Core Version:    0.6.0
 */