/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Font;
/*    */ import java.awt.Frame;
/*    */ import javax.swing.JDialog;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class JLocalizableDialog extends JDialog
/*    */   implements Localizable
/*    */ {
/*    */   String titleKey;
/*    */   Object[] params;
/*    */   Object[] paramKeys;
/*    */ 
/*    */   public JLocalizableDialog(String key)
/*    */   {
/* 19 */     this.titleKey = key;
/* 20 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableDialog(Frame owner, boolean modal, String key) {
/* 24 */     super(owner, modal);
/* 25 */     this.titleKey = key;
/* 26 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableDialog(String key, Object[] params) {
/* 30 */     this.titleKey = key;
/* 31 */     this.params = params;
/* 32 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableDialog(JFrame parent) {
/* 36 */     super(parent);
/* 37 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableDialog() {
/* 41 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void setTitle(String key)
/*    */   {
/* 46 */     this.titleKey = key;
/*    */ 
/* 48 */     if (this.paramKeys != null) {
/* 49 */       super.setTitle(LocalizationManager.getTextWithArgumentKeys(this.titleKey, this.paramKeys));
/*    */     }
/* 51 */     else if (this.params != null)
/* 52 */       super.setTitle(LocalizationManager.getTextWithArguments(this.titleKey, this.params));
/*    */     else
/* 54 */       super.setTitle(LocalizationManager.getText(this.titleKey));
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 60 */     if (this.titleKey != null) {
/* 61 */       setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/* 62 */       setTitle(this.titleKey);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getTitleKey() {
/* 67 */     return this.titleKey;
/*    */   }
/*    */ 
/*    */   public void setTitleKey(String titleKey) {
/* 71 */     this.titleKey = titleKey;
/*    */   }
/*    */ 
/*    */   public Object[] getParams() {
/* 75 */     return this.params;
/*    */   }
/*    */ 
/*    */   public void setParams(Object[] params) {
/* 79 */     this.params = params;
/*    */   }
/*    */ 
/*    */   public Object[] getParamKeys() {
/* 83 */     return this.paramKeys;
/*    */   }
/*    */ 
/*    */   public void setParamKeys(Object[] paramKeys) {
/* 87 */     this.paramKeys = paramKeys;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableDialog
 * JD-Core Version:    0.6.0
 */