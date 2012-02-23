/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableMenuItem;
/*    */ import java.awt.Font;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.Icon;
/*    */ 
/*    */ public class JLocalizableMenuItem extends JResizableMenuItem
/*    */   implements Localizable
/*    */ {
/*    */   private String textKey;
/*    */ 
/*    */   public JLocalizableMenuItem(String textKey, Icon icon)
/*    */   {
/* 18 */     super(icon);
/* 19 */     this.textKey = textKey;
/*    */ 
/* 21 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableMenuItem()
/*    */   {
/* 26 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableMenuItem(String textKey)
/*    */   {
/* 31 */     this.textKey = textKey;
/*    */ 
/* 33 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableMenuItem(Action action) {
/* 37 */     super(action);
/*    */ 
/* 39 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableMenuItem(String textKey, ActionListener actionListener)
/*    */   {
/* 45 */     setText(textKey);
/* 46 */     addActionListener(actionListener);
/*    */ 
/* 48 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 53 */     setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/* 54 */     setText(this.textKey);
/*    */   }
/*    */ 
/*    */   public void setText(String textKey)
/*    */   {
/* 59 */     this.textKey = textKey;
/*    */ 
/* 61 */     if (textKey != null)
/* 62 */       super.setText(LocalizationManager.getText(textKey));
/*    */   }
/*    */ 
/*    */   public String getTextKey()
/*    */   {
/* 67 */     return this.textKey;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem
 * JD-Core Version:    0.6.0
 */