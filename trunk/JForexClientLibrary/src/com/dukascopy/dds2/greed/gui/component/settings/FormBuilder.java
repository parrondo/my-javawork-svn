/*     */ package com.dukascopy.dds2.greed.gui.component.settings;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ 
/*     */ public class FormBuilder
/*     */ {
/*  19 */   private GridBagConstraints lastRowConstraints = null;
/*  20 */   private GridBagConstraints middleRowConstraints = null;
/*  21 */   private GridBagConstraints firstRowConstraints = null;
/*     */   private final Container parent;
/*     */ 
/*     */   public FormBuilder(Container parent)
/*     */   {
/*  25 */     this(parent, new Insets(5, 5, 5, 5));
/*     */   }
/*     */ 
/*     */   public FormBuilder(Container parent, Insets insets) {
/*  29 */     this.parent = parent;
/*     */ 
/*  31 */     this.firstRowConstraints = new GridBagConstraints();
/*     */ 
/*  37 */     this.lastRowConstraints = new GridBagConstraints();
/*     */ 
/*  40 */     this.lastRowConstraints.fill = 2;
/*     */ 
/*  44 */     this.lastRowConstraints.anchor = 19;
/*     */ 
/*  47 */     this.lastRowConstraints.weightx = 1.0D;
/*     */ 
/*  50 */     this.lastRowConstraints.gridwidth = 0;
/*     */ 
/*  53 */     this.lastRowConstraints.insets = insets;
/*     */ 
/*  56 */     this.middleRowConstraints = ((GridBagConstraints)this.lastRowConstraints.clone());
/*     */ 
/*  60 */     this.middleRowConstraints.gridwidth = -1;
/*     */ 
/*  64 */     this.firstRowConstraints = ((GridBagConstraints)this.middleRowConstraints.clone());
/*     */ 
/*  67 */     this.firstRowConstraints.weightx = 0.0D;
/*  68 */     this.firstRowConstraints.gridwidth = 1;
/*     */ 
/*  71 */     this.lastRowConstraints.gridy = 0;
/*  72 */     this.firstRowConstraints.gridy = 0;
/*  73 */     this.middleRowConstraints.gridy = 0;
/*     */   }
/*     */ 
/*     */   public void addLastField(Component c)
/*     */   {
/*  83 */     GridBagLayout gbl = (GridBagLayout)this.parent.getLayout();
/*  84 */     gbl.setConstraints(c, this.lastRowConstraints);
/*  85 */     this.parent.add(c);
/*     */   }
/*     */ 
/*     */   public void addFirstField(Component c)
/*     */   {
/*  95 */     GridBagLayout gbl = (GridBagLayout)this.parent.getLayout();
/*  96 */     gbl.setConstraints(c, this.firstRowConstraints);
/*  97 */     this.parent.add(c);
/*     */   }
/*     */ 
/*     */   public void addFirstLocalizableLabel(String key) {
/* 101 */     addFirstField(new JLocalizableLabel(key));
/*     */   }
/*     */ 
/*     */   public void addMiddleField(Component c)
/*     */   {
/* 111 */     GridBagLayout gbl = (GridBagLayout)this.parent.getLayout();
/* 112 */     gbl.setConstraints(c, this.middleRowConstraints);
/* 113 */     this.parent.add(c);
/*     */   }
/*     */ 
/*     */   public void startNewRow() {
/* 117 */     this.lastRowConstraints.gridy += 1;
/* 118 */     this.firstRowConstraints.gridy += 1;
/* 119 */     this.middleRowConstraints.gridy += 1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.FormBuilder
 * JD-Core Version:    0.6.0
 */