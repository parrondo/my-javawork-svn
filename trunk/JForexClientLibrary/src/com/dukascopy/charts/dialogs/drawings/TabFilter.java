/*     */ package com.dukascopy.charts.dialogs.drawings;
/*     */ 
/*     */ import javax.swing.text.AttributeSet;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.DocumentFilter;
/*     */ import javax.swing.text.DocumentFilter.FilterBypass;
/*     */ 
/*     */ class TabFilter extends DocumentFilter
/*     */ {
/*     */   public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr)
/*     */     throws BadLocationException
/*     */   {
/* 422 */     fb.insertString(offset, text.replaceAll("\t", ""), attr);
/*     */   }
/*     */ 
/*     */   public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
/* 426 */     fb.replace(offset, length, text.replaceAll("\t", ""), attrs);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.drawings.TabFilter
 * JD-Core Version:    0.6.0
 */