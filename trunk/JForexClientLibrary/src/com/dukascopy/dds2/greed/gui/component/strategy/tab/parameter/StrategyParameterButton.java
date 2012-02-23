/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesButton;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*    */ import java.awt.Dimension;
/*    */ 
/*    */ public class StrategyParameterButton extends StrategiesButton
/*    */ {
/* 12 */   public static final Dimension DEFAULT_SIZE = new Dimension(20, 18);
/*    */ 
/*    */   public StrategyParameterButton(ResizableIcon regularIcon, ResizableIcon disabledIcon) {
/* 15 */     super(regularIcon, disabledIcon);
/*    */ 
/* 17 */     setPreferredSize(DEFAULT_SIZE);
/* 18 */     setMinimumSize(DEFAULT_SIZE);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterButton
 * JD-Core Version:    0.6.0
 */