/*     */ package com.dukascopy.charts.dialogs.drawings;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.io.Serializable;
/*     */ import java.text.AttributedCharacterIterator.Attribute;
/*     */ import java.text.AttributedString;
/*     */ import java.util.Arrays;
/*     */ import javax.swing.text.AttributeSet;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.DefaultStyledDocument;
/*     */ import javax.swing.text.Element;
/*     */ import javax.swing.text.SimpleAttributeSet;
/*     */ import javax.swing.text.StyleConstants;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class StyledStringTransformer
/*     */   implements Serializable
/*     */ {
/*  28 */   private static final Logger LOGGER = LoggerFactory.getLogger(StyledStringTransformer.class);
/*     */   private static final long serialVersionUID = 1L;
/*     */   private byte[] sourceAttrs;
/*     */   private String source;
/*  34 */   private String fontFamily = "Dialog";
/*  35 */   private Color fontColor = Color.BLACK;
/*  36 */   private int fontSize = 11;
/*     */   private int horizontalAlignment;
/*  39 */   public static final AttributedCharacterIterator.Attribute HORIZONTAL_ALIGNMENT_ATTRIBUTE = new DucascopyAttribute("HORIZONTAL_ALIGNMENT_ATTRIBUTE");
/*     */ 
/*     */   public StyledStringTransformer(String source, DefaultStyledDocument document)
/*     */   {
/*  43 */     this.source = source;
/*  44 */     this.sourceAttrs = new byte[source.length()];
/*  45 */     int sourceIndex = 0; for (int documentIndex = 0; sourceIndex < source.length(); sourceIndex++) {
/*  46 */       if (source.charAt(sourceIndex) == '\n')
/*     */       {
/*     */         continue;
/*     */       }
/*  50 */       AttributeSet attributeSet = document.getCharacterElement(documentIndex).getAttributes();
/*  51 */       if (StyleConstants.isBold(attributeSet)) {
/*  52 */         this.sourceAttrs[sourceIndex] = 4;
/*     */       }
/*  54 */       if (StyleConstants.isItalic(attributeSet))
/*     */       {
/*     */         int tmp105_104 = sourceIndex;
/*     */         byte[] tmp105_101 = this.sourceAttrs; tmp105_101[tmp105_104] = (byte)(tmp105_101[tmp105_104] + 2);
/*     */       }
/*  57 */       if (StyleConstants.isUnderline(attributeSet))
/*     */       {
/*     */         int tmp124_123 = sourceIndex;
/*     */         byte[] tmp124_120 = this.sourceAttrs; tmp124_120[tmp124_123] = (byte)(tmp124_120[tmp124_123] + 1);
/*     */       }
/*  60 */       documentIndex++;
/*     */     }
/*     */ 
/*  63 */     if (document.getLength() > 0) {
/*  64 */       AttributeSet attributeSet = document.getCharacterElement(0).getAttributes();
/*  65 */       this.fontColor = StyleConstants.getForeground(attributeSet);
/*  66 */       this.fontFamily = StyleConstants.getFontFamily(attributeSet);
/*  67 */       this.fontSize = StyleConstants.getFontSize(attributeSet);
/*  68 */       this.horizontalAlignment = resolveHorizontalAlignment(attributeSet.getAttribute(HORIZONTAL_ALIGNMENT_ATTRIBUTE));
/*     */     }
/*     */   }
/*     */ 
/*     */   public StyledStringTransformer(String source, String fontFamily, Color fontColor, int fontSize, int horizontalAlignment) {
/*  73 */     this.source = source;
/*  74 */     this.fontFamily = fontFamily;
/*  75 */     this.fontColor = fontColor;
/*  76 */     this.fontSize = fontSize;
/*  77 */     this.horizontalAlignment = horizontalAlignment;
/*     */ 
/*  79 */     this.sourceAttrs = new byte[source.length()];
/*  80 */     Arrays.fill(this.sourceAttrs, 0);
/*     */   }
/*     */ 
/*     */   public StyledStringTransformer(String source, byte[] styleAttrs, String fontFamily, Color fontColor, int fontSize) {
/*  84 */     if (source.length() != styleAttrs.length) {
/*  85 */       throw new IllegalArgumentException("StyledStringTransformer source.length() should be equal to sourceAttrs.length");
/*     */     }
/*     */ 
/*  88 */     this.source = source;
/*  89 */     this.sourceAttrs = styleAttrs;
/*  90 */     this.fontFamily = fontFamily;
/*  91 */     this.fontColor = fontColor;
/*  92 */     this.fontSize = fontSize;
/*     */   }
/*     */ 
/*     */   public AttributedString[] transformToAttributedString() {
/*  96 */     String[] sourceLines = this.source.split("\n");
/*  97 */     AttributedString[] attributedStrings = new AttributedString[sourceLines.length];
/*  98 */     int sourceIndex = 0;
/*  99 */     for (int lineIndex = 0; lineIndex < attributedStrings.length; lineIndex++) {
/* 100 */       attributedStrings[lineIndex] = new AttributedString(sourceLines[lineIndex]);
/*     */ 
/* 102 */       if (sourceLines[lineIndex].length() > 0) {
/* 103 */         attributedStrings[lineIndex].addAttribute(TextAttribute.FAMILY, this.fontFamily, 0, sourceLines[lineIndex].length());
/* 104 */         attributedStrings[lineIndex].addAttribute(TextAttribute.SIZE, Integer.valueOf(this.fontSize), 0, sourceLines[lineIndex].length());
/* 105 */         attributedStrings[lineIndex].addAttribute(TextAttribute.FOREGROUND, this.fontColor, 0, sourceLines[lineIndex].length());
/* 106 */         attributedStrings[lineIndex].addAttribute(HORIZONTAL_ALIGNMENT_ATTRIBUTE, Integer.valueOf(getHorizontalAlignment()), 0, sourceLines[lineIndex].length());
/*     */       }
/*     */ 
/* 109 */       for (int i = 0; i < sourceLines[lineIndex].length(); i++) {
/* 110 */         byte charStyle = this.sourceAttrs[sourceIndex];
/*     */ 
/* 112 */         if (charStyle > 3) {
/* 113 */           attributedStrings[lineIndex].addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, i, i + 1);
/* 114 */           charStyle = (byte)(charStyle - 4);
/*     */         }
/* 116 */         if (charStyle > 1) {
/* 117 */           attributedStrings[lineIndex].addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, i, i + 1);
/* 118 */           charStyle = (byte)(charStyle - 2);
/*     */         }
/* 120 */         if (charStyle > 0) {
/* 121 */           attributedStrings[lineIndex].addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, i, i + 1);
/*     */         }
/*     */ 
/* 124 */         sourceIndex++;
/*     */       }
/*     */ 
/* 127 */       sourceIndex++;
/*     */     }
/*     */ 
/* 130 */     return attributedStrings;
/*     */   }
/*     */ 
/*     */   public void transformToStyledDocument(DefaultStyledDocument styledDocument) {
/*     */     try {
/* 135 */       SimpleAttributeSet attributes = new SimpleAttributeSet();
/* 136 */       styledDocument.insertString(0, this.source, attributes);
/*     */ 
/* 138 */       for (int i = 0; i < this.source.length(); i++) {
/* 139 */         byte charStyle = this.sourceAttrs[i];
/*     */ 
/* 141 */         if (charStyle > 3) {
/* 142 */           StyleConstants.setBold(attributes, true);
/* 143 */           charStyle = (byte)(charStyle - 4);
/*     */         } else {
/* 145 */           StyleConstants.setBold(attributes, false);
/*     */         }
/*     */ 
/* 148 */         if (charStyle > 1) {
/* 149 */           StyleConstants.setItalic(attributes, true);
/* 150 */           charStyle = (byte)(charStyle - 2);
/*     */         } else {
/* 152 */           StyleConstants.setItalic(attributes, false);
/*     */         }
/*     */ 
/* 155 */         if (charStyle > 0)
/* 156 */           StyleConstants.setUnderline(attributes, true);
/*     */         else {
/* 158 */           StyleConstants.setUnderline(attributes, false);
/*     */         }
/*     */ 
/* 161 */         StyleConstants.setFontFamily(attributes, this.fontFamily);
/* 162 */         StyleConstants.setFontSize(attributes, this.fontSize);
/* 163 */         StyleConstants.setForeground(attributes, this.fontColor);
/*     */ 
/* 165 */         styledDocument.setCharacterAttributes(i, 1, attributes, true);
/*     */       }
/*     */     } catch (BadLocationException ex) {
/* 168 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getSource() {
/* 173 */     return this.source;
/*     */   }
/*     */ 
/*     */   public String getFontFamilyName() {
/* 177 */     return this.fontFamily;
/*     */   }
/*     */ 
/*     */   public int getFontSize() {
/* 181 */     return this.fontSize;
/*     */   }
/*     */ 
/*     */   public Color getFontColor() {
/* 185 */     return this.fontColor;
/*     */   }
/*     */ 
/*     */   public int getHorizontalAlignment() {
/* 189 */     return this.horizontalAlignment;
/*     */   }
/*     */ 
/*     */   public static int resolveHorizontalAlignment(Object horizontalAlignment)
/*     */   {
/* 201 */     int result = 4;
/* 202 */     if ((horizontalAlignment instanceof Integer)) {
/* 203 */       int ha = ((Integer)horizontalAlignment).intValue();
/* 204 */       if ((ha == 0) || (ha == 2) || (ha == 4)) {
/* 205 */         result = ha;
/*     */       }
/*     */     }
/* 208 */     return result;
/*     */   }
/*     */ 
/*     */   public static class DucascopyAttribute extends AttributedCharacterIterator.Attribute
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     public DucascopyAttribute(String name)
/*     */     {
/* 196 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.drawings.StyledStringTransformer
 * JD-Core Version:    0.6.0
 */