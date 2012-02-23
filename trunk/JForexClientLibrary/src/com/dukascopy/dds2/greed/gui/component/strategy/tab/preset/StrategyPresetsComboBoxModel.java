/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.preset;
/*    */ 
/*    */ import java.util.List;
/*    */ import javax.swing.DefaultComboBoxModel;
/*    */ 
/*    */ public class StrategyPresetsComboBoxModel extends DefaultComboBoxModel
/*    */ {
/*    */   private List<StrategyPreset> strategyPresets;
/*    */ 
/*    */   public StrategyPresetsComboBoxModel(List<StrategyPreset> presets)
/*    */   {
/* 17 */     super(presets.toArray());
/* 18 */     this.strategyPresets = presets;
/*    */   }
/*    */ 
/*    */   public List<StrategyPreset> getStrategyPresets() {
/* 22 */     return this.strategyPresets;
/*    */   }
/*    */ 
/*    */   public void addElement(Object anObject)
/*    */   {
/* 27 */     super.addElement(anObject);
/* 28 */     this.strategyPresets.add((StrategyPreset)anObject);
/*    */   }
/*    */ 
/*    */   public void removeElement(Object anObject)
/*    */   {
/* 33 */     super.removeElement(anObject);
/* 34 */     this.strategyPresets.remove((StrategyPreset)anObject);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPresetsComboBoxModel
 * JD-Core Version:    0.6.0
 */