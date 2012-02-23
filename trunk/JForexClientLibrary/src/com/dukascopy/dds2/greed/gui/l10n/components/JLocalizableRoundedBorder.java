/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorderSwitch;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Font;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class JLocalizableRoundedBorder extends JRoundedBorderSwitch
/*    */   implements Localizable
/*    */ {
/*    */   private String textKey;
/*    */ 
/*    */   public JLocalizableRoundedBorder(JComponent parent, String text, int topInset, int leftInset, int bottomInset, int rightInset)
/*    */   {
/* 14 */     super(parent, text, topInset, leftInset, bottomInset, rightInset);
/*    */ 
/* 16 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableRoundedBorder(JComponent parent, String text, boolean isHidable) {
/* 20 */     super(parent, text, isHidable);
/* 21 */     this.textKey = text;
/* 22 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableRoundedBorder(JComponent parent, String text) {
/* 26 */     super(parent, text);
/* 27 */     this.textKey = text;
/*    */ 
/* 29 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableRoundedBorder(JComponent parent) {
/* 33 */     super(parent);
/*    */ 
/* 35 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableRoundedBorder(JComponent parent, boolean isHidable) {
/* 39 */     super(parent, null, isHidable);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 44 */     performLocalization();
/*    */ 
/* 46 */     if (this.parent != null) {
/* 47 */       this.parent.revalidate();
/* 48 */       this.parent.repaint();
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void performLocalization() {
/* 53 */     setHeaderFont(LocalizationManager.getDefaultFont(getHeaderFont().getSize()));
/* 54 */     setHeaderText(LocalizationManager.getText(this.textKey));
/*    */   }
/*    */ 
/*    */   public String getTextKey() {
/* 58 */     return this.textKey;
/*    */   }
/*    */ 
/*    */   public void setTextKey(String textKey) {
/* 62 */     this.textKey = textKey;
/* 63 */     localize();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder
 * JD-Core Version:    0.6.0
 */