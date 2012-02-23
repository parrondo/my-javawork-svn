/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.util.UnicodeUtil;
/*     */ import org.apache.lucene.util.UnicodeUtil.UTF16Result;
/*     */ import org.apache.lucene.util.UnicodeUtil.UTF8Result;
/*     */ 
/*     */ final class TermBuffer
/*     */   implements Cloneable
/*     */ {
/*     */   private String field;
/*     */   private Term term;
/*     */   private boolean preUTF8Strings;
/*     */   private boolean dirty;
/*  31 */   private UnicodeUtil.UTF16Result text = new UnicodeUtil.UTF16Result();
/*  32 */   private UnicodeUtil.UTF8Result bytes = new UnicodeUtil.UTF8Result();
/*     */ 
/*     */   public final int compareTo(TermBuffer other) {
/*  35 */     if (this.field == other.field) {
/*  36 */       return compareChars(this.text.result, this.text.length, other.text.result, other.text.length);
/*     */     }
/*  38 */     return this.field.compareTo(other.field);
/*     */   }
/*     */ 
/*     */   private static final int compareChars(char[] chars1, int len1, char[] chars2, int len2)
/*     */   {
/*  43 */     int end = len1 < len2 ? len1 : len2;
/*  44 */     for (int k = 0; k < end; k++) {
/*  45 */       char c1 = chars1[k];
/*  46 */       char c2 = chars2[k];
/*  47 */       if (c1 != c2) {
/*  48 */         return c1 - c2;
/*     */       }
/*     */     }
/*  51 */     return len1 - len2;
/*     */   }
/*     */ 
/*     */   void setPreUTF8Strings()
/*     */   {
/*  58 */     this.preUTF8Strings = true;
/*     */   }
/*     */ 
/*     */   public final void read(IndexInput input, FieldInfos fieldInfos) throws IOException
/*     */   {
/*  63 */     this.term = null;
/*  64 */     int start = input.readVInt();
/*  65 */     int length = input.readVInt();
/*  66 */     int totalLength = start + length;
/*  67 */     if (this.preUTF8Strings) {
/*  68 */       this.text.setLength(totalLength);
/*  69 */       input.readChars(this.text.result, start, length);
/*     */     }
/*  72 */     else if (this.dirty)
/*     */     {
/*  74 */       UnicodeUtil.UTF16toUTF8(this.text.result, 0, this.text.length, this.bytes);
/*  75 */       this.bytes.setLength(totalLength);
/*  76 */       input.readBytes(this.bytes.result, start, length);
/*  77 */       UnicodeUtil.UTF8toUTF16(this.bytes.result, 0, totalLength, this.text);
/*  78 */       this.dirty = false;
/*     */     }
/*     */     else {
/*  81 */       this.bytes.setLength(totalLength);
/*  82 */       input.readBytes(this.bytes.result, start, length);
/*  83 */       UnicodeUtil.UTF8toUTF16(this.bytes.result, start, length, this.text);
/*     */     }
/*     */ 
/*  86 */     this.field = fieldInfos.fieldName(input.readVInt());
/*     */   }
/*     */ 
/*     */   public final void set(Term term) {
/*  90 */     if (term == null) {
/*  91 */       reset();
/*  92 */       return;
/*     */     }
/*  94 */     String termText = term.text();
/*  95 */     int termLen = termText.length();
/*  96 */     this.text.setLength(termLen);
/*  97 */     termText.getChars(0, termLen, this.text.result, 0);
/*  98 */     this.dirty = true;
/*  99 */     this.field = term.field();
/* 100 */     this.term = term;
/*     */   }
/*     */ 
/*     */   public final void set(TermBuffer other) {
/* 104 */     this.text.copyText(other.text);
/* 105 */     this.dirty = true;
/* 106 */     this.field = other.field;
/* 107 */     this.term = other.term;
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 111 */     this.field = null;
/* 112 */     this.text.setLength(0);
/* 113 */     this.term = null;
/* 114 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   public Term toTerm() {
/* 118 */     if (this.field == null) {
/* 119 */       return null;
/*     */     }
/* 121 */     if (this.term == null) {
/* 122 */       this.term = new Term(this.field, new String(this.text.result, 0, this.text.length), false);
/*     */     }
/* 124 */     return this.term;
/*     */   }
/*     */ 
/*     */   protected Object clone()
/*     */   {
/* 129 */     TermBuffer clone = null;
/*     */     try {
/* 131 */       clone = (TermBuffer)super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/*     */     }
/* 134 */     clone.dirty = true;
/* 135 */     clone.bytes = new UnicodeUtil.UTF8Result();
/* 136 */     clone.text = new UnicodeUtil.UTF16Result();
/* 137 */     clone.text.copyText(this.text);
/* 138 */     return clone;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermBuffer
 * JD-Core Version:    0.6.0
 */