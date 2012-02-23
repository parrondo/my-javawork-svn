/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Font;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.JCheckBoxMenuItem;
/*    */ 
/*    */ public class JLocalizableCheckBoxMenuItem extends JCheckBoxMenuItem
/*    */   implements Localizable
/*    */ {
/*    */   private String textKey;
/*    */ 
/*    */   public JLocalizableCheckBoxMenuItem()
/*    */   {
/* 17 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableCheckBoxMenuItem(String textKey)
/*    */   {
/* 22 */     this.textKey = textKey;
/* 23 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableCheckBoxMenuItem(String textKey, Icon icon) {
/* 27 */     super(icon);
/* 28 */     this.textKey = textKey;
/* 29 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableCheckBoxMenuItem(Action action) {
/* 33 */     super(action);
/* 34 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 39 */     setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/* 40 */     setText(this.textKey);
/*    */   }
/*    */ 
/*    */   public void setText(String key) {
/* 44 */     this.textKey = key;
/* 45 */     if (this.textKey != null)
/* 46 */       super.setText(LocalizationManager.getText(this.textKey));
/*    */   }
/*    */ 
/*    */   public String getTextKey()
/*    */   {
/* 51 */     return this.textKey;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBoxMenuItem
 * JD-Core Version:    0.6.0
 */