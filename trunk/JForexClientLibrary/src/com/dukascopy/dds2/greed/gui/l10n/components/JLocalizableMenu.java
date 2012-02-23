/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Font;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.JMenu;
/*    */ 
/*    */ public class JLocalizableMenu extends JMenu
/*    */   implements Localizable
/*    */ {
/*    */   private String textKey;
/*    */ 
/*    */   public JLocalizableMenu()
/*    */   {
/* 17 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableMenu(String textKey)
/*    */   {
/* 22 */     this.textKey = textKey;
/*    */ 
/* 24 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableMenu(Action action) {
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
/* 42 */     if ((this.textKey != null) && (!"".equals(this.textKey.trim())))
/* 43 */       super.setText(LocalizationManager.getText(this.textKey));
/*    */   }
/*    */ 
/*    */   public String getTextKey()
/*    */   {
/* 48 */     return this.textKey;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenu
 * JD-Core Version:    0.6.0
 */