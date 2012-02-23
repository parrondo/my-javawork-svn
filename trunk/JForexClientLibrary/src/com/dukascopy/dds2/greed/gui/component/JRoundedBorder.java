/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Insets;
/*     */ import java.awt.RenderingHints;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.border.Border;
/*     */ 
/*     */ public class JRoundedBorder
/*     */   implements Border
/*     */ {
/*     */   protected final JComponent parent;
/*     */   private JLabel label;
/*  21 */   protected int rightBorder = 6;
/*  22 */   protected int topBorder = 7;
/*  23 */   protected int leftBorder = 6;
/*  24 */   protected int relativeWidth = 0;
/*  25 */   private int bottomBorder = 7;
/*     */ 
/*  27 */   private int radius = 10;
/*     */ 
/*  29 */   private int topInset = 18;
/*  30 */   private int leftInset = 15;
/*  31 */   private int bottomInset = 18;
/*  32 */   private int rightInset = 15;
/*     */ 
/*     */   public JRoundedBorder(JComponent parent, String text, int topInset, int leftInset, int bottomInset, int rightInset) {
/*  35 */     this(parent, text);
/*  36 */     this.topInset = topInset;
/*  37 */     this.leftInset = leftInset;
/*  38 */     this.bottomInset = bottomInset;
/*  39 */     this.rightInset = rightInset;
/*     */   }
/*     */ 
/*     */   public JRoundedBorder(JComponent parent, String textKey) {
/*  43 */     this(parent);
/*  44 */     this.label = new JLabel(textKey);
/*     */   }
/*     */ 
/*     */   public JRoundedBorder(JComponent parent) {
/*  48 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
/*  52 */     Graphics2D g2 = (Graphics2D)g;
/*  53 */     Object hint = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
/*  54 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/*  56 */     this.relativeWidth = (width - (this.rightBorder + this.leftBorder));
/*  57 */     int h = height - (this.topBorder + this.bottomBorder);
/*     */ 
/*  59 */     g.setColor(Color.LIGHT_GRAY);
/*  60 */     g.drawRoundRect(this.leftBorder, this.topBorder, this.relativeWidth, h, this.radius, this.radius);
/*  61 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, hint);
/*     */ 
/*  63 */     if ((this.label != null) && (this.label.getText() != null) && (!this.label.getText().equals("")))
/*     */     {
/*  65 */       Dimension preferredSize = this.label.getPreferredSize();
/*  66 */       this.label.setBounds(0, 0, preferredSize.width, preferredSize.height);
/*  67 */       int stringWidth = this.label.getWidth();
/*  68 */       g.setColor(this.parent.getBackground());
/*  69 */       g.fillRect(this.relativeWidth / 2 - stringWidth / 2 - 3, this.topBorder - 1, stringWidth + 4, 2);
/*  70 */       Graphics lg = g.create(this.relativeWidth / 2 - preferredSize.width / 2, this.topBorder - this.label.getHeight() / 2, preferredSize.width, preferredSize.height);
/*  71 */       this.label.paint(lg);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Insets getBorderInsets(Component c) {
/*  76 */     return new Insets(this.topInset, this.leftInset, this.bottomInset, this.rightInset);
/*     */   }
/*     */ 
/*     */   public boolean isBorderOpaque() {
/*  80 */     return true;
/*     */   }
/*     */ 
/*     */   public void setHeaderText(String text) {
/*  84 */     if (this.label == null) {
/*  85 */       this.label = new JLabel();
/*     */     }
/*  87 */     this.label.setText(text);
/*     */   }
/*     */ 
/*     */   public String getHeaderText() {
/*  91 */     return this.label.getText();
/*     */   }
/*     */ 
/*     */   public Font getHeaderFont() {
/*  95 */     if (this.label == null) return null;
/*  96 */     return this.label.getFont();
/*     */   }
/*     */ 
/*     */   public void setHeaderFont(Font font) {
/* 100 */     this.label.setFont(font);
/*     */   }
/*     */ 
/*     */   public void setTopBorder(int topBorder) {
/* 104 */     this.topBorder = topBorder;
/*     */   }
/*     */ 
/*     */   public int getTopBorder() {
/* 108 */     return this.topBorder;
/*     */   }
/*     */ 
/*     */   public void setRightBorder(int rightBorder) {
/* 112 */     this.rightBorder = rightBorder;
/*     */   }
/*     */ 
/*     */   public int getRightBorder() {
/* 116 */     return this.rightBorder;
/*     */   }
/*     */ 
/*     */   public void setLeftBorder(int leftBorder) {
/* 120 */     this.leftBorder = leftBorder;
/*     */   }
/*     */ 
/*     */   public int getLeftBorder() {
/* 124 */     return this.leftBorder;
/*     */   }
/*     */ 
/*     */   public void setBottomBorder(int bottomBorder) {
/* 128 */     this.bottomBorder = bottomBorder;
/*     */   }
/*     */ 
/*     */   public int getBottomBorder() {
/* 132 */     return this.bottomBorder;
/*     */   }
/*     */ 
/*     */   public void setTopInset(int topInset) {
/* 136 */     this.topInset = topInset;
/* 137 */     this.parent.repaint();
/*     */   }
/*     */ 
/*     */   public int getTopInset() {
/* 141 */     return this.topInset;
/*     */   }
/*     */ 
/*     */   public void setLeftInset(int leftInset) {
/* 145 */     this.leftInset = leftInset;
/*     */   }
/*     */ 
/*     */   public int getLeftInset() {
/* 149 */     return this.leftInset;
/*     */   }
/*     */ 
/*     */   public void setBottomInset(int bottomInset) {
/* 153 */     this.bottomInset = bottomInset;
/* 154 */     this.parent.repaint();
/*     */   }
/*     */ 
/*     */   public int getBottomInset() {
/* 158 */     return this.bottomInset;
/*     */   }
/*     */ 
/*     */   public void setRightInset(int rightInset) {
/* 162 */     this.rightInset = rightInset;
/*     */   }
/*     */ 
/*     */   public int getRightInset() {
/* 166 */     return this.rightInset;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.JRoundedBorder
 * JD-Core Version:    0.6.0
 */