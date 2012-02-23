/*     */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.LookAndFeelSpecific;
/*     */ import java.awt.Font;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JToggleButton;
/*     */ 
/*     */ public class JLocalizableToggleButton extends JToggleButton
/*     */   implements Localizable, LookAndFeelSpecific
/*     */ {
/*     */   private String textKey;
/*     */   private String toolTipKey;
/*     */ 
/*     */   public JLocalizableToggleButton()
/*     */   {
/*  19 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableToggleButton(Action a) {
/*  23 */     super(a);
/*  24 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableToggleButton(Icon icon, boolean selected) {
/*  28 */     super(icon, selected);
/*  29 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableToggleButton(Icon icon) {
/*  33 */     super(icon);
/*  34 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableToggleButton(String textKey, Icon icon, boolean selected) {
/*  38 */     super(icon, selected);
/*  39 */     this.textKey = textKey;
/*  40 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableToggleButton(String textKey, Icon icon) {
/*  44 */     super(icon);
/*  45 */     this.textKey = textKey;
/*  46 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableToggleButton(String textKey)
/*     */   {
/*  51 */     this.textKey = textKey;
/*  52 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableToggleButton(String textKey, String toolTipKey)
/*     */   {
/*  57 */     this.textKey = textKey;
/*  58 */     this.toolTipKey = toolTipKey;
/*  59 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/*  64 */     setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/*  65 */     setText(this.textKey);
/*  66 */     if (this.toolTipKey != null)
/*  67 */       setToolTipText(this.toolTipKey);
/*     */   }
/*     */ 
/*     */   public void setText(String key)
/*     */   {
/*  73 */     this.textKey = key;
/*  74 */     super.setText(LocalizationManager.getText(this.textKey));
/*     */   }
/*     */ 
/*     */   public void setToolTipText(String key)
/*     */   {
/*  79 */     this.toolTipKey = key;
/*  80 */     if ((this.toolTipKey != null) && (!"".equals(this.toolTipKey.trim())))
/*  81 */       super.setToolTipText(LocalizationManager.getText(this.toolTipKey));
/*     */   }
/*     */ 
/*     */   public String getTextKey()
/*     */   {
/*  86 */     return this.textKey;
/*     */   }
/*     */ 
/*     */   public void setTextKey(String textKey) {
/*  90 */     this.textKey = textKey;
/*  91 */     localize();
/*     */   }
/*     */ 
/*     */   public String getToolTipKey() {
/*  95 */     return this.toolTipKey;
/*     */   }
/*     */ 
/*     */   public void setToolTipKey(String toolTipKey) {
/*  99 */     this.toolTipKey = toolTipKey;
/* 100 */     localize();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableToggleButton
 * JD-Core Version:    0.6.0
 */