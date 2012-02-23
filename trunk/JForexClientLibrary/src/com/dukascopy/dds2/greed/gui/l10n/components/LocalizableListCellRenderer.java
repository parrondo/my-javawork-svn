/*     */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Font;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ 
/*     */ public class LocalizableListCellRenderer extends DefaultListCellRenderer
/*     */   implements Localizable
/*     */ {
/*     */   private String textKey;
/*     */   private Object[] textParams;
/*     */   private Object[] textKeyParams;
/*     */   private boolean ignoreTextSet;
/*     */ 
/*     */   public LocalizableListCellRenderer()
/*     */   {
/*  24 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public LocalizableListCellRenderer(String textKey)
/*     */   {
/*  30 */     this.textKey = textKey;
/*     */ 
/*  32 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public LocalizableListCellRenderer(String textKey, float alligment)
/*     */   {
/*  38 */     this.textKey = textKey;
/*  39 */     setAlignmentX(alligment);
/*     */ 
/*  41 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public LocalizableListCellRenderer(String textKey, Object[] params)
/*     */   {
/*  47 */     this.textKey = textKey;
/*  48 */     this.textParams = params;
/*     */ 
/*  50 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public boolean isIgnoreTextSet() {
/*  54 */     return this.ignoreTextSet;
/*     */   }
/*     */ 
/*     */   public void setIgnoreTextSet(boolean ignoreTextSet) {
/*  58 */     this.ignoreTextSet = ignoreTextSet;
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/*  63 */     if (this.ignoreTextSet) {
/*  64 */       String output = null;
/*  65 */       if ((this.textKey != null) && (!this.textKey.trim().equals("")) && (this.textKeyParams != null)) {
/*  66 */         output = LocalizationManager.getTextWithArgumentKeys(this.textKey, this.textKeyParams);
/*     */       }
/*  68 */       else if ((this.textKey != null) && (!this.textKey.trim().equals("")) && (this.textParams != null)) {
/*  69 */         output = LocalizationManager.getTextWithArguments(this.textKey, this.textParams);
/*     */       }
/*  71 */       else if ((this.textKey != null) && (!this.textKey.trim().equals(""))) {
/*  72 */         output = LocalizationManager.getText(this.textKey);
/*     */       }
/*     */ 
/*  75 */       super.setFont(LocalizationManager.getDefaultFont(super.getFont().getSize()));
/*  76 */       super.setText(output);
/*     */     }
/*  78 */     else if (this.textKey != null) {
/*  79 */       setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/*  80 */       setText(this.textKey);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getTextKey()
/*     */   {
/*  86 */     return this.textKey;
/*     */   }
/*     */ 
/*     */   public void setTextKey(String textKey) {
/*  90 */     this.textKey = textKey;
/*     */   }
/*     */ 
/*     */   public void setText(String textKey)
/*     */   {
/*  95 */     if (!this.ignoreTextSet) {
/*  96 */       this.textKey = textKey;
/*     */ 
/*  98 */       String output = null;
/*  99 */       if ((textKey != null) && (!textKey.trim().equals("")) && (this.textKeyParams != null)) {
/* 100 */         output = LocalizationManager.getTextWithArgumentKeys(textKey, this.textKeyParams);
/*     */       }
/* 102 */       else if ((textKey != null) && (!textKey.trim().equals("")) && (this.textParams != null)) {
/* 103 */         output = LocalizationManager.getTextWithArguments(textKey, this.textParams);
/*     */       }
/* 105 */       else if ((textKey != null) && (!textKey.trim().equals(""))) {
/* 106 */         output = LocalizationManager.getText(textKey);
/*     */       }
/*     */ 
/* 109 */       super.setText(output);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object[] getTextParams() {
/* 114 */     return this.textParams;
/*     */   }
/*     */ 
/*     */   public void setTextParams(Object[] textParams) {
/* 118 */     this.textParams = textParams;
/*     */   }
/*     */ 
/*     */   public Object[] getTextKeyParams() {
/* 122 */     return this.textKeyParams;
/*     */   }
/*     */ 
/*     */   public void setTextKeyParams(Object[] textKeyParams) {
/* 126 */     this.textKeyParams = textKeyParams;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.LocalizableListCellRenderer
 * JD-Core Version:    0.6.0
 */