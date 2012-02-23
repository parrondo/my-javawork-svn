/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Font;
/*    */ import javax.swing.JRadioButton;
/*    */ 
/*    */ public class JLocalizableRadioButton extends JRadioButton
/*    */   implements Localizable
/*    */ {
/*    */   private String textKey;
/*    */   private String toolTipKey;
/*    */ 
/*    */   public JLocalizableRadioButton(String textKey)
/*    */   {
/* 16 */     this.textKey = textKey;
/*    */ 
/* 18 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableRadioButton(String textKey, String toolTipKey)
/*    */   {
/* 23 */     this.textKey = textKey;
/* 24 */     this.toolTipKey = toolTipKey;
/*    */ 
/* 26 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 31 */     setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/* 32 */     setText(this.textKey);
/* 33 */     if (this.toolTipKey != null)
/* 34 */       setToolTipText(this.toolTipKey);
/*    */   }
/*    */ 
/*    */   public void setText(String textKey)
/*    */   {
/* 40 */     this.textKey = textKey;
/* 41 */     super.setText(LocalizationManager.getText(textKey));
/*    */   }
/*    */ 
/*    */   public void setToolTipText(String key)
/*    */   {
/* 46 */     this.toolTipKey = key;
/* 47 */     setToolTipText(LocalizationManager.getText(this.toolTipKey));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButton
 * JD-Core Version:    0.6.0
 */