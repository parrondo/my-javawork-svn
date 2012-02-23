/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Font;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.JRadioButtonMenuItem;
/*    */ 
/*    */ public class JLocalizableRadioButtonMenuItem extends JRadioButtonMenuItem
/*    */   implements Localizable
/*    */ {
/*    */   private String textKey;
/*    */ 
/*    */   public JLocalizableRadioButtonMenuItem()
/*    */   {
/* 17 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableRadioButtonMenuItem(String key)
/*    */   {
/* 22 */     this.textKey = key;
/*    */ 
/* 24 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableRadioButtonMenuItem(Action action) {
/* 28 */     super(action);
/*    */ 
/* 30 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 35 */     setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/* 36 */     setText(this.textKey);
/*    */   }
/*    */ 
/*    */   public void setText(String key)
/*    */   {
/* 41 */     this.textKey = key;
/*    */ 
/* 43 */     if (this.textKey != null)
/* 44 */       super.setText(LocalizationManager.getText(this.textKey));
/*    */   }
/*    */ 
/*    */   public String getTextKey()
/*    */   {
/* 49 */     return this.textKey;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButtonMenuItem
 * JD-Core Version:    0.6.0
 */