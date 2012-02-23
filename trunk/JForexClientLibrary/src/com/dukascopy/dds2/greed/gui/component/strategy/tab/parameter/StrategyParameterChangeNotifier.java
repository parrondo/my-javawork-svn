/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.DefaultStrategyPresetsController;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.IStrategyPresetsController;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset;
/*    */ import java.util.List;
/*    */ import javax.swing.ComboBoxEditor;
/*    */ import javax.swing.JComboBox;
/*    */ 
/*    */ public class StrategyParameterChangeNotifier
/*    */ {
/* 13 */   private boolean enabled = true;
/*    */   private IStrategyPresetsController presetsController;
/*    */   private JComboBox presetComboBox;
/*    */   private StrategyParamsPanel parametersContainerPanel;
/*    */ 
/*    */   public StrategyParameterChangeNotifier(JComboBox presetComboBox, StrategyParamsPanel parametersContainerPanel)
/*    */   {
/* 21 */     this.presetComboBox = presetComboBox;
/* 22 */     this.parametersContainerPanel = parametersContainerPanel;
/*    */ 
/* 24 */     this.presetsController = new DefaultStrategyPresetsController();
/*    */   }
/*    */ 
/*    */   public boolean isEnabled() {
/* 28 */     return this.enabled;
/*    */   }
/*    */ 
/*    */   public void setEnabled(boolean enabled) {
/* 32 */     this.enabled = enabled;
/*    */   }
/*    */ 
/*    */   public void parameterChanged() {
/* 36 */     if (isEnabled())
/*    */     {
/* 38 */       Object selectedItem = this.presetComboBox.getSelectedItem();
/* 39 */       if ((selectedItem instanceof StrategyPreset)) {
/* 40 */         StrategyPreset selectedPreset = (StrategyPreset)selectedItem;
/*    */ 
/* 42 */         List presetParameters = this.presetsController.retrievePresetParameters(selectedPreset.getId(), this.parametersContainerPanel);
/*    */ 
/* 44 */         selectedPreset.setStrategyParameters(presetParameters);
/*    */ 
/* 46 */         this.presetComboBox.setSelectedItem(selectedPreset);
/* 47 */         this.presetComboBox.getEditor().setItem(selectedPreset);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterChangeNotifier
 * JD-Core Version:    0.6.0
 */