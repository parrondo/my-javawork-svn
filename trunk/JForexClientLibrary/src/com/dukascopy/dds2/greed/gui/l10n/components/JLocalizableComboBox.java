/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableComboBox;
/*    */ import java.awt.Font;
/*    */ import java.util.Vector;
/*    */ import javax.swing.ComboBoxModel;
/*    */ 
/*    */ public abstract class JLocalizableComboBox extends JResizableComboBox
/*    */   implements Localizable
/*    */ {
/*    */   private String toolTipKey;
/*    */ 
/*    */   public JLocalizableComboBox(Object[] items, ResizingManager.ComponentSize deaultSize)
/*    */   {
/* 18 */     super(items, deaultSize);
/* 19 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   protected JLocalizableComboBox(ComboBoxModel comboBoxModel, ResizingManager.ComponentSize deaultSize) {
/* 23 */     super(comboBoxModel, deaultSize);
/* 24 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   protected JLocalizableComboBox(Vector<?> items, ResizingManager.ComponentSize deaultSize) {
/* 28 */     super(items, deaultSize);
/* 29 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableComboBox()
/*    */   {
/* 34 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 39 */     setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/* 40 */     translate();
/* 41 */     setToolTipText(this.toolTipKey);
/*    */   }
/*    */ 
/*    */   public abstract void translate();
/*    */ 
/*    */   public void setToolTipText(String key) {
/* 48 */     this.toolTipKey = key;
/* 49 */     super.setToolTipText(LocalizationManager.getText(this.toolTipKey));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableComboBox
 * JD-Core Version:    0.6.0
 */