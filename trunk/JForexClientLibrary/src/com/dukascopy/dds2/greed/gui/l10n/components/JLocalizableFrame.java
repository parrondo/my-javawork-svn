/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Font;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class JLocalizableFrame extends JFrame
/*    */   implements Localizable
/*    */ {
/*    */   String titleKey;
/*    */   Object[] params;
/*    */   Object[] paramKeys;
/*    */ 
/*    */   public JLocalizableFrame(String key)
/*    */   {
/* 16 */     this.titleKey = key;
/*    */ 
/* 18 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableFrame(String key, Object[] params) {
/* 22 */     this.titleKey = key;
/* 23 */     this.params = params;
/*    */ 
/* 25 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableFrame() {
/* 29 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void setTitle(String key)
/*    */   {
/* 34 */     this.titleKey = key;
/*    */ 
/* 36 */     if (this.paramKeys != null) {
/* 37 */       super.setTitle(LocalizationManager.getTextWithArgumentKeys(this.titleKey, this.paramKeys));
/*    */     }
/* 39 */     else if (this.params != null)
/* 40 */       super.setTitle(LocalizationManager.getTextWithArguments(this.titleKey, this.params));
/*    */     else
/* 42 */       super.setTitle(LocalizationManager.getText(this.titleKey));
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 48 */     if (this.titleKey != null) {
/* 49 */       setTitle(this.titleKey);
/* 50 */       if (getFont() != null)
/* 51 */         setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getTitleKey()
/*    */   {
/* 57 */     return this.titleKey;
/*    */   }
/*    */ 
/*    */   public void setTitleKey(String titleKey) {
/* 61 */     this.titleKey = titleKey;
/*    */   }
/*    */ 
/*    */   public Object[] getParams() {
/* 65 */     return this.params;
/*    */   }
/*    */ 
/*    */   public void setParams(Object[] params) {
/* 69 */     this.params = params;
/*    */   }
/*    */ 
/*    */   public Object[] getParamKeys() {
/* 73 */     return this.paramKeys;
/*    */   }
/*    */ 
/*    */   public void setParamKeys(Object[] paramKeys) {
/* 77 */     this.paramKeys = paramKeys;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableFrame
 * JD-Core Version:    0.6.0
 */