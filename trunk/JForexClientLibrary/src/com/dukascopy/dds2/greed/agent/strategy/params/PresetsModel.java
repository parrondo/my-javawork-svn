/*    */ package com.dukascopy.dds2.greed.agent.strategy.params;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Set;
/*    */ import java.util.SortedMap;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ public class PresetsModel
/*    */ {
/* 13 */   private SortedMap<Preset, HashMap<String, Variable>> model = new TreeMap();
/*    */ 
/* 15 */   private Preset defaultPreset = null;
/*    */ 
/*    */   public PresetsModel() {
/*    */   }
/*    */ 
/*    */   public PresetsModel(Preset defaultPreset, HashMap<String, Variable> variableMap) {
/* 21 */     this.model.put(defaultPreset, variableMap);
/* 22 */     this.defaultPreset = defaultPreset;
/*    */   }
/*    */ 
/*    */   public void deletePreset(Preset presetToDelete) {
/* 26 */     this.model.remove(presetToDelete);
/*    */   }
/*    */ 
/*    */   public void addPreset(Preset preset, HashMap<String, Variable> variableMap) {
/* 30 */     this.model.put(preset, variableMap);
/*    */   }
/*    */ 
/*    */   public void setDefaultPreset(Preset defaultPreset) {
/* 34 */     this.defaultPreset = defaultPreset;
/*    */   }
/*    */ 
/*    */   public Preset getDefaultPreset() {
/* 38 */     return this.defaultPreset;
/*    */   }
/*    */ 
/*    */   public Preset getPreset(String name) {
/* 42 */     String nameLowered = name.toLowerCase();
/* 43 */     Set presets = this.model.keySet();
/* 44 */     for (Preset preset : presets) {
/* 45 */       if (preset.getName().toLowerCase().equals(nameLowered)) {
/* 46 */         return preset;
/*    */       }
/*    */     }
/* 49 */     return null;
/*    */   }
/*    */ 
/*    */   public HashMap<String, Variable> getDefaultVariableMap() {
/* 53 */     if (this.model == null) {
/* 54 */       return null;
/*    */     }
/* 56 */     return (HashMap)this.model.get(this.defaultPreset);
/*    */   }
/*    */ 
/*    */   public HashMap<String, Variable> getVariableMap(Preset byPreset) {
/* 60 */     return (HashMap)this.model.get(byPreset);
/*    */   }
/*    */ 
/*    */   public Set<Preset> getAllPresets() {
/* 64 */     return this.model.keySet();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.params.PresetsModel
 * JD-Core Version:    0.6.0
 */