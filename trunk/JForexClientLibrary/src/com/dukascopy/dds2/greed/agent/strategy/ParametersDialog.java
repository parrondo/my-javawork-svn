/*     */ package com.dukascopy.dds2.greed.agent.strategy;
/*     */ 
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.impl.execution.IControlUI;
/*     */ import com.dukascopy.api.impl.execution.Task;
/*     */ import com.dukascopy.api.impl.execution.TaskParameter;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.io.File;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ 
/*     */ public class ParametersDialog extends JDialog
/*     */   implements IControlUI
/*     */ {
/*     */   public static final String DIMENSION_PROPERTY_KEY = "com.dukascopy.ParametersDialog.size";
/*  28 */   private static final Dimension DIALOG_SIZE = new Dimension(450, 450);
/*     */ 
/*  30 */   private ParametersPanel parametersPanel = null;
/*     */   private final Frame parent;
/*     */ 
/*     */   public ParametersDialog(JFrame parent, List<StrategyRunParameter> params, boolean isEditDialog)
/*     */   {
/*  34 */     super(parent, LocalizationManager.getText("strategy.parameters.dialog.title"), true);
/*  35 */     this.parent = parent;
/*  36 */     this.parametersPanel = new ParametersPanel(this, params, isEditDialog);
/*  37 */     initDialog();
/*     */   }
/*     */ 
/*     */   public ParametersDialog(Frame parent, IStrategy target, boolean isEditDialog, File binaryFile) {
/*  41 */     super(parent, LocalizationManager.getText("strategy.parameters.dialog.title"), true);
/*  42 */     this.parent = parent;
/*  43 */     this.parametersPanel = new ParametersPanel(this, target, isEditDialog, binaryFile);
/*     */ 
/*  45 */     initDialog();
/*     */   }
/*     */ 
/*     */   public List<String[]> getValues() {
/*  49 */     return this.parametersPanel.getValues();
/*     */   }
/*     */ 
/*     */   public Task<?> showParam(Map<String, Variable> parameters) {
/*  53 */     Task returnCode = null;
/*     */ 
/*  55 */     this.parametersPanel.setParameters(parameters);
/*  56 */     if (this.parametersPanel.isAnyParameterDetected()) {
/*  57 */       setVisible(true);
/*  58 */       returnCode = this.parametersPanel.getReturnCode();
/*     */     }
/*     */     else {
/*  61 */       returnCode = new TaskParameter();
/*     */     }
/*     */ 
/*  64 */     return returnCode;
/*     */   }
/*     */ 
/*     */   public List<StrategyRunParameter> showModal() {
/*  68 */     if (this.parametersPanel.isAnyParameterDetected()) {
/*  69 */       setVisible(true);
/*  70 */       if (this.parametersPanel.getReturnCode() != null)
/*     */       {
/*  72 */         return this.parametersPanel.getParameters();
/*     */       }
/*  74 */       return null;
/*     */     }
/*     */ 
/*  77 */     return new LinkedList();
/*     */   }
/*     */ 
/*     */   public void setControlField(JComponent component, boolean justCheckDoNotSetFields)
/*     */     throws Exception
/*     */   {
/*  83 */     this.parametersPanel.setControlField(component, justCheckDoNotSetFields);
/*     */   }
/*     */ 
/*     */   public List<StrategyRunParameter> getParameters() {
/*  87 */     return this.parametersPanel.getParameters();
/*     */   }
/*     */ 
/*     */   private void initDialog() {
/*  91 */     setSize();
/*  92 */     setModal(true);
/*  93 */     setResizable(true);
/*     */ 
/*  95 */     setContentPane(this.parametersPanel);
/*  96 */     setLocationRelativeTo(this.parent);
/*     */   }
/*     */ 
/*     */   private void setSize() {
/* 100 */     setMinimumSize(DIALOG_SIZE);
/* 101 */     Dimension customSize = ParametersPanel.getDimension();
/* 102 */     if (customSize != null)
/* 103 */       setSize(customSize);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ParametersDialog
 * JD-Core Version:    0.6.0
 */