/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Font;
/*    */ import javax.swing.BoundedRangeModel;
/*    */ import javax.swing.JSlider;
/*    */ 
/*    */ public class JLocalizableSlider extends JSlider
/*    */   implements Localizable
/*    */ {
/*    */   private String toolTipKey;
/*    */ 
/*    */   public JLocalizableSlider()
/*    */   {
/* 18 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableSlider(int orientation) {
/* 22 */     super(orientation);
/* 23 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableSlider(int min, int max) {
/* 27 */     super(min, max);
/* 28 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableSlider(int min, int max, int value) {
/* 32 */     super(min, max, value);
/* 33 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableSlider(int orientation, int min, int max, int value) {
/* 37 */     super(orientation, min, max, value);
/* 38 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableSlider(BoundedRangeModel borderRangeModel) {
/* 42 */     super(borderRangeModel);
/* 43 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 48 */     if (this.toolTipKey != null) {
/* 49 */       setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/* 50 */       setToolTipText(this.toolTipKey);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setToolTipText(String toolTipKey)
/*    */   {
/* 56 */     this.toolTipKey = toolTipKey;
/* 57 */     super.setToolTipText(LocalizationManager.getText(toolTipKey));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableSlider
 * JD-Core Version:    0.6.0
 */