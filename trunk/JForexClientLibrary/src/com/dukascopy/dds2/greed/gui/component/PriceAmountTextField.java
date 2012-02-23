/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import java.awt.Toolkit;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.text.AttributeSet;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.PlainDocument;
/*     */ 
/*     */ public class PriceAmountTextField extends JFormattedTextField
/*     */ {
/*     */   private int length;
/*  26 */   private int precision = -1;
/*     */   public static final String ID_JTXT_PRICEFIELD = "ID_JTXT_PRICEFIELD";
/*     */ 
/*     */   public PriceAmountTextField(int length)
/*     */   {
/*  37 */     setName("ID_JTXT_PRICEFIELD");
/*  38 */     build(length, 0);
/*     */   }
/*     */ 
/*     */   public PriceAmountTextField(int length, int precision)
/*     */   {
/*  52 */     setName("ID_JTXT_PRICEFIELD");
/*  53 */     build(length, precision);
/*     */   }
/*     */ 
/*     */   private void build(int length, int aPrecision) {
/*  57 */     this.length = length;
/*  58 */     setPrecision(aPrecision);
/*     */   }
/*     */ 
/*     */   public void setPrecision(int aPrecision) {
/*  62 */     if (this.precision != aPrecision) {
/*  63 */       this.precision = aPrecision;
/*     */       String pattern;
/*  65 */       if (0 == this.precision) {
/*  66 */         String pattern = "\\d+(\\x2e\\d*)?";
/*  67 */         ((PriceAmountDocument)getDocument()).setLength(this.length);
/*     */       } else {
/*  69 */         pattern = "(\\d{1," + this.length + "})?(\\x2e\\d{0," + this.precision + "})?";
/*     */       }
/*  71 */       ((PriceAmountDocument)getDocument()).setPattern(Pattern.compile(pattern));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  79 */     super.setText(null);
/*     */   }
/*     */ 
/*     */   public void setText(String text) {
/*     */     try {
/*  84 */       new BigDecimal(text);
/*     */     } catch (Exception e) {
/*  86 */       return;
/*     */     }
/*  88 */     super.setText(text.substring(0, Math.min(this.length + (0 == this.precision ? 0 : this.precision + 1), text.length())));
/*     */   }
/*     */ 
/*     */   protected Document createDefaultModel()
/*     */   {
/*  93 */     return new PriceAmountDocument(this);
/*     */   }
/*     */ 
/*     */   private class PriceAmountDocument extends PlainDocument
/*     */   {
/*     */     private Pattern pattern;
/*     */     private PriceAmountTextField originator;
/*  99 */     private int length = 2147483647;
/*     */ 
/*     */     public PriceAmountDocument(PriceAmountTextField originator)
/*     */     {
/* 103 */       this.originator = originator;
/*     */     }
/*     */ 
/*     */     public void insertString(int index, String string, AttributeSet attributeSet) throws BadLocationException {
/* 107 */       String text = this.originator.getText();
/*     */ 
/* 109 */       String temp = text.substring(0, index) + string + text.substring(index);
/* 110 */       Matcher matcher = this.pattern.matcher(temp);
/* 111 */       if ((matcher.matches()) && (temp.length() <= this.length)) {
/* 112 */         if (text.startsWith(".")) {
/* 113 */           super.insertString(0, "0", attributeSet);
/* 114 */           super.insertString(index + 1, string, attributeSet);
/*     */         } else {
/* 116 */           super.insertString(index, string, attributeSet);
/*     */         }
/*     */       } else {
/* 119 */         Toolkit.getDefaultToolkit().beep();
/* 120 */         return;
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void setPattern(Pattern pattern) {
/* 125 */       this.pattern = pattern;
/*     */     }
/*     */ 
/*     */     protected void setLength(int length) {
/* 129 */       this.length = length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.PriceAmountTextField
 * JD-Core Version:    0.6.0
 */