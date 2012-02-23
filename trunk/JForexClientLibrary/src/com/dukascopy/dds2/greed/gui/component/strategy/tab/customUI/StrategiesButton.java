/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*    */ import java.awt.Dimension;
/*    */ 
/*    */ public class StrategiesButton extends JLocalizableButton
/*    */ {
/* 14 */   public static final Dimension DEFAULT_SIZE = ResizingManager.ComponentSize.SIZE_24X24.getSize();
/*    */ 
/*    */   public StrategiesButton(ResizableIcon regularIcon) {
/* 17 */     this(regularIcon, null);
/*    */   }
/*    */ 
/*    */   public StrategiesButton(ResizableIcon regularIcon, ResizableIcon disabledIcon) {
/* 21 */     super(regularIcon, ResizingManager.ComponentSize.SIZE_24X24);
/*    */ 
/* 23 */     if (disabledIcon != null) {
/* 24 */       setDisabledIcon(disabledIcon);
/*    */     }
/*    */ 
/* 27 */     setHorizontalAlignment(0);
/*    */ 
/* 29 */     setPreferredSize(DEFAULT_SIZE);
/* 30 */     setMinimumSize(DEFAULT_SIZE);
/* 31 */     setMaximumSize(DEFAULT_SIZE);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesButton
 * JD-Core Version:    0.6.0
 */