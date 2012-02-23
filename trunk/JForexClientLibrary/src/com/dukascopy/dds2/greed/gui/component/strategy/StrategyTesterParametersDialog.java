/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.impl.execution.IControlUI;
/*     */ import com.dukascopy.api.impl.execution.Task;
/*     */ import com.dukascopy.api.impl.execution.TaskParameter;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ParametersPanel;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.optimizer.ParameterOptimizationData;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.optimizer.ParametersOptimizationPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.CardLayout;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.io.File;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class StrategyTesterParametersDialog extends JDialog
/*     */   implements IControlUI
/*     */ {
/*     */   private static final String OPTIMIZED = "optimized";
/*     */   private static final String STANDARD = "standard";
/*     */   private final Frame parent;
/*  43 */   private JPanel mainPanel = null;
/*  44 */   private boolean optimizedMode = false;
/*  45 */   private ParametersPanel parametersPanel = null;
/*     */   private ParametersOptimizationPanel parametersOptimizationPanel;
/*     */   private ParameterOptimizationData parameterOptimizationData;
/*     */ 
/*     */   public StrategyTesterParametersDialog(Frame parent, IStrategy strategy, boolean isEditDialog, ParameterOptimizationData parameterOptimizationData, Map<String, Variable> parameters, File binaryFile, boolean optimizedMode)
/*     */   {
/*  59 */     super(parent, LocalizationManager.getText("strategy.parameters.dialog.title"), true);
/*  60 */     this.parent = parent;
/*  61 */     this.parameterOptimizationData = parameterOptimizationData;
/*     */ 
/*  63 */     this.parametersPanel = new ParametersPanel(this, strategy, isEditDialog, binaryFile);
/*  64 */     this.parametersPanel.setParameters(parameters);
/*     */ 
/*  67 */     if (!this.parametersPanel.isAnyParameterDetected()) {
/*  68 */       return;
/*     */     }
/*     */ 
/*  71 */     this.parametersOptimizationPanel = new ParametersOptimizationPanel(this, strategy);
/*  72 */     this.parametersOptimizationPanel.setParameters(this.parameterOptimizationData);
/*     */ 
/*  74 */     this.optimizedMode = optimizedMode;
/*     */ 
/*  76 */     initUI();
/*  77 */     switchPanels(this.mainPanel);
/*     */   }
/*     */ 
/*     */   public ParameterOptimizationData getParameterOptimizationData() {
/*  81 */     if (isOptimizedMode()) {
/*  82 */       return this.parametersOptimizationPanel.getModalResult();
/*     */     }
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */   public Task<?> getTaskParameter()
/*     */   {
/*  89 */     Task returnCode = null;
/*  90 */     if (this.parametersPanel.isAnyParameterDetected()) {
/*  91 */       returnCode = this.parametersPanel.getReturnCode();
/*     */     }
/*     */     else {
/*  94 */       returnCode = new TaskParameter();
/*     */     }
/*     */ 
/*  97 */     return returnCode;
/*     */   }
/*     */ 
/*     */   public List<String[]> getValues() {
/* 101 */     return this.parametersPanel.getValues();
/*     */   }
/*     */ 
/*     */   public boolean isOptimizedMode() {
/* 105 */     return this.optimizedMode;
/*     */   }
/*     */ 
/*     */   public void setControlField(JComponent component, boolean justCheckDoNotSetFields) throws Exception
/*     */   {
/* 110 */     if (!isOptimizedMode())
/* 111 */       this.parametersPanel.setControlField(component, justCheckDoNotSetFields);
/*     */   }
/*     */ 
/*     */   private void initUI()
/*     */   {
/* 116 */     JPanel root = new JPanel(new GridBagLayout());
/*     */ 
/* 118 */     GridBagConstraints gbc = new GridBagConstraints();
/* 119 */     gbc.fill = 2;
/* 120 */     gbc.anchor = 23;
/*     */ 
/* 126 */     gbc.fill = 1;
/* 127 */     GridBagLayoutHelper.add(0, 0, 1.0D, 1.0D, 1, 1, 0, 10, 0, 0, gbc, root, createMainPanel());
/*     */ 
/* 129 */     setContentPane(root);
/* 130 */     switchPanels(this.mainPanel);
/*     */ 
/* 132 */     setModal(true);
/* 133 */     setResizable(true);
/* 134 */     pack();
/*     */ 
/* 136 */     setLocationRelativeTo(this.parent);
/* 137 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private JPanel createMainPanel()
/*     */   {
/* 190 */     this.mainPanel = new JPanel();
/* 191 */     this.mainPanel.setLayout(new CardLayout());
/*     */ 
/* 194 */     this.mainPanel.add(this.parametersPanel, "standard");
/* 195 */     this.mainPanel.add(this.parametersOptimizationPanel, "optimized");
/* 196 */     return this.mainPanel;
/*     */   }
/*     */ 
/*     */   private void switchPanels(JPanel panel) {
/* 200 */     CardLayout layout = (CardLayout)this.mainPanel.getLayout();
/* 201 */     if (this.optimizedMode)
/* 202 */       layout.show(this.mainPanel, "optimized");
/*     */     else
/* 204 */       layout.show(this.mainPanel, "standard");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.StrategyTesterParametersDialog
 * JD-Core Version:    0.6.0
 */