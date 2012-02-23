/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.properties;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.IStrategyParameterPanelBuilder;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterChangeNotifier;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterLocal;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterPanelBuilder;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParamsPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.GridLayout;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.JComboBox;
/*    */ 
/*    */ public class StrategyPropertiesPanelBuilder
/*    */   implements IStrategyPropertiesPanelBuilder
/*    */ {
/* 25 */   public static final Dimension PARAMETER_DEFAULT_SIZE = new Dimension(150, 20);
/*    */ 
/* 27 */   private IStrategyParameterPanelBuilder parametersPanelFactory = new StrategyParameterPanelBuilder();
/*    */ 
/*    */   public StrategyParamsPanel buildParametersPanel(JComboBox presetComboBox, StrategyNewBean strategy, StrategyPreset preset)
/*    */   {
/* 31 */     if ((strategy == null) || (preset == null)) {
/* 32 */       return null;
/*    */     }
/*    */ 
/* 35 */     StrategyParamsPanel parametersPanel = new StrategyParamsPanel();
/* 36 */     parametersPanel.setLayout(new GridLayout(0, 1));
/*    */ 
/* 38 */     List strategyParameters = preset.getStrategyParameters();
/* 39 */     List strategyParametersPanels = new ArrayList();
/*    */ 
/* 41 */     for (StrategyParameterLocal strategyParameter : strategyParameters)
/*    */     {
/* 43 */       StrategyParameterPanel panel = this.parametersPanelFactory.buildParameterPanel(strategyParameter, new StrategyParameterChangeNotifier(presetComboBox, parametersPanel));
/*    */ 
/* 46 */       strategyParametersPanels.add(panel);
/*    */     }
/*    */ 
/* 49 */     parametersPanel.setParams(strategyParametersPanels);
/* 50 */     return parametersPanel;
/*    */   }
/*    */ 
/*    */   public void updateParametersPanel(StrategyParamsPanel parametersPanel, List<StrategyParameterLocal> params)
/*    */   {
/* 56 */     int size = parametersPanel.getParams().size();
/*    */ 
/* 58 */     for (int i = 0; i < size; i++)
/* 59 */       this.parametersPanelFactory.updateParameterPanel((StrategyParameterLocal)params.get(i), (StrategyParameterPanel)parametersPanel.getParams().get(i));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.properties.StrategyPropertiesPanelBuilder
 * JD-Core Version:    0.6.0
 */