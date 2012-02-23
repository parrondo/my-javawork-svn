/*    */ package com.dukascopy.dds2.greed.gui.component.settings.workspace;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.settings.FormBuilder;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.GridBagLayout;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JSpinner;
/*    */ import javax.swing.SpinnerModel;
/*    */ import javax.swing.SpinnerNumberModel;
/*    */ import javax.swing.event.ChangeEvent;
/*    */ import javax.swing.event.ChangeListener;
/*    */ 
/*    */ public class WorkspaceOptionsPanel extends JPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private JLocalizableCheckBox enableWorkspaceAutoSaving;
/*    */   private JLocalizableCheckBox enableWorkspaceSaveOnExit;
/*    */   private JSpinner workspaceAutoSavingPeriod;
/*    */   private JLocalizableLabel workspaceAutoSavingPeriodLabel;
/*    */   private JLocalizableRoundedBorder mainBorder;
/*    */ 
/*    */   public WorkspaceOptionsPanel()
/*    */   {
/* 35 */     init();
/*    */   }
/*    */ 
/*    */   private void init()
/*    */   {
/* 40 */     setBorder(getMainBorder());
/* 41 */     setLayout(new GridBagLayout());
/*    */ 
/* 43 */     FormBuilder formBuilder = new FormBuilder(this);
/*    */ 
/* 45 */     formBuilder.addFirstField(getEnableWorkspaceSaveOnExit());
/* 46 */     formBuilder.startNewRow();
/* 47 */     formBuilder.addFirstField(getEnableWorkspaceAutoSaving());
/* 48 */     formBuilder.addMiddleField(getWorkspaceAutoSavingPeriod());
/* 49 */     formBuilder.addLastField(getWorkspaceAutoSavingPeriodLabel());
/*    */   }
/*    */ 
/*    */   public JLocalizableCheckBox getEnableWorkspaceAutoSaving() {
/* 53 */     if (this.enableWorkspaceAutoSaving == null) {
/* 54 */       this.enableWorkspaceAutoSaving = new JLocalizableCheckBox("label.enable.workspace.auto.saving");
/*    */ 
/* 56 */       this.enableWorkspaceAutoSaving.addChangeListener(new ChangeListener()
/*    */       {
/*    */         public void stateChanged(ChangeEvent e) {
/* 59 */           WorkspaceOptionsPanel.this.getWorkspaceAutoSavingPeriod().setEnabled(WorkspaceOptionsPanel.this.getEnableWorkspaceAutoSaving().isSelected());
/*    */         } } );
/*    */     }
/* 63 */     return this.enableWorkspaceAutoSaving;
/*    */   }
/*    */ 
/*    */   public JSpinner getWorkspaceAutoSavingPeriod() {
/* 67 */     if (this.workspaceAutoSavingPeriod == null) {
/* 68 */       SpinnerModel model = new SpinnerNumberModel(1, 1, 60, 1);
/* 69 */       this.workspaceAutoSavingPeriod = new JSpinner(model);
/*    */ 
/* 71 */       this.workspaceAutoSavingPeriod.setPreferredSize(new Dimension(40, this.workspaceAutoSavingPeriod.getPreferredSize().height));
/* 72 */       this.workspaceAutoSavingPeriod.setEnabled(getEnableWorkspaceAutoSaving().isSelected());
/*    */     }
/* 74 */     return this.workspaceAutoSavingPeriod;
/*    */   }
/*    */ 
/*    */   public JLocalizableRoundedBorder getMainBorder()
/*    */   {
/* 79 */     if (this.mainBorder == null) {
/* 80 */       this.mainBorder = new JLocalizableRoundedBorder(this, "border.workspace.options");
/*    */     }
/* 82 */     return this.mainBorder;
/*    */   }
/*    */ 
/*    */   public JLocalizableLabel getWorkspaceAutoSavingPeriodLabel()
/*    */   {
/* 87 */     if (this.workspaceAutoSavingPeriodLabel == null) {
/* 88 */       this.workspaceAutoSavingPeriodLabel = new JLocalizableLabel("label.auto.save.period");
/*    */     }
/* 90 */     return this.workspaceAutoSavingPeriodLabel;
/*    */   }
/*    */ 
/*    */   public JLocalizableCheckBox getEnableWorkspaceSaveOnExit() {
/* 94 */     if (this.enableWorkspaceSaveOnExit == null) {
/* 95 */       this.enableWorkspaceSaveOnExit = new JLocalizableCheckBox("label.enable.workspace.save.on.exit");
/*    */     }
/* 97 */     return this.enableWorkspaceSaveOnExit;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.workspace.WorkspaceOptionsPanel
 * JD-Core Version:    0.6.0
 */