/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import javax.swing.JDialog;
/*    */ import javax.swing.JFrame;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ParametersOptimizationDialog extends JDialog
/*    */ {
/* 24 */   private static final Logger LOGGER = LoggerFactory.getLogger(ParametersOptimizationDialog.class);
/* 25 */   private ParametersOptimizationPanel parametersOptimizationPanel = null;
/*    */ 
/*    */   public ParametersOptimizationDialog(JFrame parentFrame, IStrategy strategy)
/*    */   {
/* 34 */     super(parentFrame);
/* 35 */     this.parametersOptimizationPanel = new ParametersOptimizationPanel(this, strategy);
/*    */   }
/*    */ 
/*    */   public ParameterOptimizationData showModal(String titleKey, ParameterOptimizationData data)
/*    */   {
/* 46 */     setContentPane(this.parametersOptimizationPanel);
/* 47 */     this.parametersOptimizationPanel.setParameters(data);
/*    */ 
/* 49 */     setModal(true);
/* 50 */     setTitle(LocalizationManager.getText(titleKey));
/* 51 */     pack();
/* 52 */     setLocationRelativeTo(getParent());
/* 53 */     setVisible(true);
/*    */ 
/* 55 */     return this.parametersOptimizationPanel.getModalResult();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.ParametersOptimizationDialog
 * JD-Core Version:    0.6.0
 */