/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ 
/*    */ public class JLocalizableHeaderPanel extends HeaderPanel
/*    */   implements Localizable
/*    */ {
/*    */   private String titleKey;
/*    */   private Object[] params;
/*    */ 
/*    */   public JLocalizableHeaderPanel(String titleKey, boolean centered)
/*    */   {
/* 14 */     super(titleKey, centered);
/*    */ 
/* 16 */     this.titleKey = titleKey;
/* 17 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableHeaderPanel(String titleKey, boolean centered, boolean showRowCount) {
/* 21 */     super(titleKey, centered, showRowCount);
/* 22 */     this.titleKey = titleKey;
/* 23 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableHeaderPanel(String titleKey, Object[] params, boolean centered) {
/* 27 */     super(titleKey, centered);
/*    */ 
/* 29 */     this.titleKey = titleKey;
/* 30 */     this.params = params;
/*    */ 
/* 32 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 37 */     if (this.params != null)
/* 38 */       setTitle(LocalizationManager.getTextWithArguments(this.titleKey, this.params));
/*    */     else
/* 40 */       setTitle(LocalizationManager.getText(this.titleKey));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableHeaderPanel
 * JD-Core Version:    0.6.0
 */