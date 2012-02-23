/*     */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import com.dukascopy.dds2.greed.util.LookAndFeelSpecific;
/*     */ import java.awt.Font;
/*     */ import javax.swing.Icon;
/*     */ 
/*     */ public class JLocalizableButton extends JResizableButton
/*     */   implements Localizable, LookAndFeelSpecific
/*     */ {
/*     */   private String textKey;
/*     */   private String toolTipKey;
/*     */ 
/*     */   public JLocalizableButton()
/*     */   {
/*  20 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableButton(Icon icon) {
/*  24 */     super(icon);
/*  25 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableButton(String textKey)
/*     */   {
/*  30 */     this.textKey = textKey;
/*  31 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableButton(String textKey, ResizingManager.ComponentSize size) {
/*  35 */     super(size);
/*  36 */     this.textKey = textKey;
/*  37 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableButton(ResizingManager.ComponentSize defaultSize) {
/*  41 */     super(defaultSize);
/*  42 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableButton(String textKey, String toolTipKey)
/*     */   {
/*  47 */     this.textKey = textKey;
/*  48 */     this.toolTipKey = toolTipKey;
/*     */ 
/*  50 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableButton(ResizableIcon icon, ResizingManager.ComponentSize size)
/*     */   {
/*  58 */     super(icon, size);
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
/*     */   public void setEmptyText()
/*     */   {
/*  86 */     this.textKey = "";
/*  87 */     super.setText(this.textKey);
/*     */   }
/*     */ 
/*     */   public String getTextKey() {
/*  91 */     return this.textKey;
/*     */   }
/*     */ 
/*     */   public void setTextKey(String textKey) {
/*  95 */     this.textKey = textKey;
/*  96 */     localize();
/*     */   }
/*     */ 
/*     */   public String getToolTipKey() {
/* 100 */     return this.toolTipKey;
/*     */   }
/*     */ 
/*     */   public void setToolTipKey(String toolTipKey) {
/* 104 */     this.toolTipKey = toolTipKey;
/* 105 */     localize();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton
 * JD-Core Version:    0.6.0
 */