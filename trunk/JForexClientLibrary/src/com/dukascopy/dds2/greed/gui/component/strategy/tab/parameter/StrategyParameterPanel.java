/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategyComponentWrapper;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategyLabel;
/*    */ import java.awt.FlowLayout;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class StrategyParameterPanel extends JPanel
/*    */ {
/*    */   public static final int HGAP = 6;
/*    */   public static final int VGAP = 2;
/*    */   private StrategyParameterLocal parameter;
/*    */   private StrategyLabel name;
/*    */   private StrategyComponentWrapper value;
/*    */ 
/*    */   public StrategyParameterPanel(StrategyParameterLocal parameter, StrategyLabel name, StrategyComponentWrapper value)
/*    */   {
/* 27 */     this.parameter = parameter;
/* 28 */     this.name = name;
/* 29 */     this.value = value;
/*    */ 
/* 31 */     setLayout(new FlowLayout(1, 6, 2));
/*    */ 
/* 33 */     add(name);
/* 34 */     add(value.getComponent());
/*    */   }
/*    */ 
/*    */   public String getPresetId() {
/* 38 */     return this.parameter.getPresetId();
/*    */   }
/*    */ 
/*    */   public String getParameterId() {
/* 42 */     return this.parameter.getId();
/*    */   }
/*    */ 
/*    */   public Class<?> getParameterType() {
/* 46 */     return this.parameter.getType();
/*    */   }
/*    */ 
/*    */   public double getParameterStepSize() {
/* 50 */     return this.parameter.getStepSize();
/*    */   }
/*    */ 
/*    */   public boolean isMandatory() {
/* 54 */     return this.parameter.isMandatory();
/*    */   }
/*    */ 
/*    */   public boolean isReadOnly() {
/* 58 */     return this.parameter.isReadOnly();
/*    */   }
/*    */ 
/*    */   public String getDescription() {
/* 62 */     return this.parameter.getDescription();
/*    */   }
/*    */ 
/*    */   public boolean isDateTimeAsLong() {
/* 66 */     return this.parameter.isDateAsLong();
/*    */   }
/*    */ 
/*    */   public StrategyLabel getNameComponent()
/*    */   {
/* 71 */     return this.name;
/*    */   }
/*    */ 
/*    */   public StrategyComponentWrapper getValueWrapper() {
/* 75 */     return this.value;
/*    */   }
/*    */ 
/*    */   public JComponent getValueComponent() {
/* 79 */     return this.value.getComponent();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterPanel
 * JD-Core Version:    0.6.0
 */