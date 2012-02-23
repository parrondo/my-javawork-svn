/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Font;
/*    */ import javax.swing.JCheckBox;
/*    */ 
/*    */ public class JLocalizableCheckBox extends JCheckBox
/*    */   implements Localizable
/*    */ {
/*    */   private String textKey;
/*    */   private String toolTipKey;
/*    */ 
/*    */   public JLocalizableCheckBox(String key)
/*    */   {
/* 17 */     this.textKey = key;
/* 18 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableCheckBox(String key, boolean selected)
/*    */   {
/* 24 */     this.textKey = key;
/* 25 */     setSelected(selected);
/*    */ 
/* 27 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableCheckBox(String key, String toolTipKey)
/*    */   {
/* 33 */     this.textKey = key;
/* 34 */     this.toolTipKey = toolTipKey;
/*    */ 
/* 36 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 41 */     setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/* 42 */     setText(this.textKey);
/* 43 */     if (this.toolTipKey != null)
/* 44 */       setToolTipText(this.toolTipKey);
/*    */   }
/*    */ 
/*    */   public void setText(String textKey)
/*    */   {
/* 50 */     this.textKey = textKey;
/* 51 */     super.setText(LocalizationManager.getText(textKey));
/*    */   }
/*    */ 
/*    */   public void setToolTipText(String key)
/*    */   {
/* 56 */     this.toolTipKey = key;
/* 57 */     super.setToolTipText(LocalizationManager.getText(this.toolTipKey));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox
 * JD-Core Version:    0.6.0
 */