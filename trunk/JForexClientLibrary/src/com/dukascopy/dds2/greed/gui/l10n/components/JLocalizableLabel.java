/*     */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableLabel;
/*     */ import java.awt.Font;
/*     */ 
/*     */ public class JLocalizableLabel extends JResizableLabel
/*     */   implements Localizable
/*     */ {
/*     */   private String textKey;
/*     */   private String toolTipKey;
/*     */   private Object[] textParams;
/*     */   private Object[] textKeyParams;
/*     */ 
/*     */   public JLocalizableLabel()
/*     */   {
/*  17 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableLabel(String textKey)
/*     */   {
/*  22 */     this.textKey = textKey;
/*     */ 
/*  24 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableLabel(String textKey, float aligment)
/*     */   {
/*  29 */     this.textKey = textKey;
/*  30 */     setAlignmentX(aligment);
/*     */ 
/*  32 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableLabel(String textKey, Object[] params)
/*     */   {
/*  37 */     this.textKey = textKey;
/*  38 */     this.textParams = params;
/*     */ 
/*  40 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableLabel(String textKey, String toolTipKey, int position) {
/*  44 */     super(textKey, position);
/*  45 */     this.textKey = textKey;
/*  46 */     this.toolTipKey = toolTipKey;
/*     */ 
/*  48 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public JLocalizableLabel(String textKey, String toolTipKey)
/*     */   {
/*  53 */     this.textKey = textKey;
/*  54 */     this.toolTipKey = toolTipKey;
/*     */ 
/*  56 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/*  62 */     if (getFont() != null) {
/*  63 */       setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/*     */     }
/*     */ 
/*  66 */     if (this.textKey != null) {
/*  67 */       setText(this.textKey);
/*     */     }
/*     */ 
/*  70 */     if (this.toolTipKey != null)
/*  71 */       setToolTipText(this.toolTipKey);
/*     */   }
/*     */ 
/*     */   public void setText(String textKey)
/*     */   {
/*  77 */     this.textKey = textKey;
/*     */ 
/*  79 */     String output = null;
/*  80 */     if ((textKey != null) && (!textKey.trim().equals("")) && (this.textKeyParams != null)) {
/*  81 */       output = LocalizationManager.getTextWithArgumentKeys(textKey, this.textKeyParams);
/*     */     }
/*  83 */     else if ((textKey != null) && (!textKey.trim().equals("")) && (this.textParams != null)) {
/*  84 */       output = LocalizationManager.getTextWithArguments(textKey, this.textParams);
/*     */     }
/*  86 */     else if ((textKey != null) && (!textKey.trim().equals(""))) {
/*  87 */       output = LocalizationManager.getText(textKey);
/*     */     }
/*     */ 
/*  90 */     super.setText(output);
/*     */   }
/*     */ 
/*     */   public void setToolTipText(String toolTipKey)
/*     */   {
/*  95 */     this.toolTipKey = toolTipKey;
/*  96 */     super.setToolTipText(LocalizationManager.getText(toolTipKey));
/*     */   }
/*     */ 
/*     */   public void setToolTip(String toolTipText)
/*     */   {
/* 104 */     super.setToolTipText(toolTipText);
/*     */   }
/*     */ 
/*     */   public Object[] getTextParams() {
/* 108 */     return this.textParams;
/*     */   }
/*     */ 
/*     */   public void setTextParams(Object[] textParams) {
/* 112 */     this.textParams = textParams;
/*     */   }
/*     */ 
/*     */   public Object[] getTextKeyParams() {
/* 116 */     return this.textKeyParams;
/*     */   }
/*     */ 
/*     */   public void setTextKeyParams(Object[] textKeyParams) {
/* 120 */     this.textKeyParams = textKeyParams;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel
 * JD-Core Version:    0.6.0
 */