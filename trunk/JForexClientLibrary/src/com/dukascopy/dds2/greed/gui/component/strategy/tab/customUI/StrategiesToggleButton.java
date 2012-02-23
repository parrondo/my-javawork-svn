/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*    */ 
/*    */ public class StrategiesToggleButton extends StrategiesButton
/*    */ {
/*  8 */   boolean selected = false;
/*    */ 
/*    */   public StrategiesToggleButton(ResizableIcon regularIcon) {
/* 11 */     this(regularIcon, null);
/*    */   }
/*    */ 
/*    */   public StrategiesToggleButton(ResizableIcon regularIcon, ResizableIcon disabledIcon) {
/* 15 */     super(regularIcon, disabledIcon);
/*    */   }
/*    */ 
/*    */   public void setSelected(boolean selected)
/*    */   {
/* 21 */     super.setSelected(selected);
/* 22 */     this.selected = selected;
/*    */   }
/*    */ 
/*    */   public boolean isSelected()
/*    */   {
/* 27 */     return this.selected;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesToggleButton
 * JD-Core Version:    0.6.0
 */