/*     */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Insets;
/*     */ import java.awt.RenderingHints;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.border.Border;
/*     */ 
/*     */ public class JHintTextField extends JTextField
/*     */   implements Localizable
/*     */ {
/*  20 */   private final int xHintOffset = 4;
/*  21 */   private final int errorOffset = 8;
/*  22 */   private final int spaceBetweenHintAndText = 0;
/*  23 */   private final Border errorModeborder = BorderFactory.createEmptyBorder(0, 8, 0, 0);
/*     */ 
/*  25 */   private Border originalBorder = null;
/*     */ 
/*  27 */   private Border hintModeborder = null;
/*  28 */   private Border errorAndHintModeBorder = null;
/*  29 */   private Border errorModeBorder = null;
/*     */ 
/*  31 */   private int leftErrorIconOffset = 0;
/*  32 */   private int topErrorIconOffset = 0;
/*  33 */   private int bottomErrorIconOffset = 0;
/*  34 */   private String hint = "";
/*  35 */   private String keyHint = "";
/*     */ 
/*  37 */   private boolean error = false;
/*     */ 
/*     */   public JHintTextField(String keyHint)
/*     */   {
/*  42 */     this.keyHint = keyHint;
/*  43 */     this.originalBorder = getBorder();
/*  44 */     LocalizationManager.addLocalizable(this);
/*     */ 
/*  46 */     if (this.originalBorder != null) {
/*  47 */       this.leftErrorIconOffset = this.originalBorder.getBorderInsets(this).left;
/*  48 */       this.topErrorIconOffset = this.originalBorder.getBorderInsets(this).top;
/*  49 */       this.bottomErrorIconOffset = this.originalBorder.getBorderInsets(this).top;
/*     */     }
/*     */ 
/*  52 */     this.errorModeBorder = BorderFactory.createCompoundBorder(this.originalBorder, this.errorModeborder);
/*  53 */     if ((this.hint != null) && (this.hint.length() > 0)) {
/*  54 */       createHintModeBorders();
/*  55 */       setBorder(this.hintModeborder);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createHintModeBorders() {
/*  60 */     FontMetrics fm = getFontMetrics(getFont());
/*  61 */     int stringWidth = fm.stringWidth(this.hint);
/*     */ 
/*  63 */     getClass(); getClass(); Border insideHintBorder = BorderFactory.createEmptyBorder(0, 4 + stringWidth + 0, 0, 0);
/*     */ 
/*  69 */     this.hintModeborder = BorderFactory.createCompoundBorder(this.originalBorder, insideHintBorder);
/*     */ 
/*  74 */     getClass(); getClass(); getClass(); Border insideHintAndErrorBorder = BorderFactory.createEmptyBorder(0, 4 + stringWidth + 0 + 8, 0, 0);
/*     */ 
/*  80 */     this.errorAndHintModeBorder = BorderFactory.createCompoundBorder(this.originalBorder, insideHintAndErrorBorder);
/*     */   }
/*     */ 
/*     */   private void drawHint(Graphics g)
/*     */   {
/*  87 */     if ((this.hint != null) && (this.hint.length() > 0)) {
/*  88 */       Graphics2D graphics = (Graphics2D)g;
/*  89 */       graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  90 */       FontMetrics fm = g.getFontMetrics(getFont());
/*  91 */       int fontHeight = fm.getHeight();
/*     */ 
/*  93 */       int offsetY = getHeight() / 2 + fontHeight / 3;
/*     */ 
/*  95 */       int xHint = 0;
/*  96 */       if (this.error) {
/*  97 */         getClass(); getClass(); xHint += 4 + 8;
/*     */       } else {
/*  99 */         getClass(); xHint = 4;
/*     */       }
/* 101 */       graphics.drawString(this.hint, xHint, offsetY);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void drawErrorIcon(Graphics g) {
/* 106 */     if (this.error) {
/* 107 */       g.setColor(Color.RED);
/*     */ 
/* 109 */       int signWidth = 2;
/*     */ 
/* 111 */       int marginLeft = this.leftErrorIconOffset;
/* 112 */       int marginTop = this.topErrorIconOffset + 1;
/* 113 */       int marginBottom = this.bottomErrorIconOffset + 1;
/*     */ 
/* 115 */       int space = 2;
/* 116 */       int part2Height = 2;
/*     */ 
/* 118 */       int signHeight = getHeight() - marginTop - marginBottom;
/* 119 */       int part1Y = marginTop;
/* 120 */       int part1Height = signHeight - space - part2Height;
/* 121 */       int part2Y = marginTop + space + part1Height;
/*     */ 
/* 123 */       g.fillRect(marginLeft, part1Y, signWidth, part1Height);
/* 124 */       g.fillRect(marginLeft, part2Y, signWidth, part2Height);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void paintComponent(Graphics g)
/*     */   {
/* 130 */     super.paintComponent(g);
/* 131 */     drawHint(g);
/* 132 */     drawErrorIcon(g);
/*     */   }
/*     */ 
/*     */   public boolean isError() {
/* 136 */     return this.error;
/*     */   }
/*     */ 
/*     */   public void setError(boolean error) {
/* 140 */     this.error = error;
/* 141 */     if ((this.hint != null) && (this.hint.length() > 0)) {
/* 142 */       if (this.error)
/* 143 */         setBorder(this.errorAndHintModeBorder);
/*     */       else {
/* 145 */         setBorder(this.hintModeborder);
/*     */       }
/*     */     }
/* 148 */     else if (this.error)
/* 149 */       setBorder(this.errorModeBorder);
/*     */     else
/* 151 */       setBorder(this.originalBorder);
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/* 158 */     setHint();
/*     */   }
/*     */ 
/*     */   private void setHint() {
/* 162 */     if ((this.keyHint != null) && (this.keyHint.length() > 0)) {
/* 163 */       this.hint = (LocalizationManager.getText("hdm.export.data.to") + ":");
/* 164 */       createHintModeBorders();
/* 165 */       setBorder(this.hintModeborder);
/* 166 */       repaint();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.JHintTextField
 * JD-Core Version:    0.6.0
 */