/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class MappingCharFilter extends BaseCharFilter
/*     */ {
/*     */   private final NormalizeCharMap normMap;
/*     */   private LinkedList<Character> buffer;
/*     */   private String replacement;
/*     */   private int charPointer;
/*     */   private int nextCharCounter;
/*     */ 
/*     */   public MappingCharFilter(NormalizeCharMap normMap, CharStream in)
/*     */   {
/*  40 */     super(in);
/*  41 */     this.normMap = normMap;
/*     */   }
/*     */ 
/*     */   public MappingCharFilter(NormalizeCharMap normMap, Reader in)
/*     */   {
/*  46 */     super(CharReader.get(in));
/*  47 */     this.normMap = normMap;
/*     */   }
/*     */ 
/*     */   public int read() throws IOException
/*     */   {
/*     */     while (true) {
/*  53 */       if ((this.replacement != null) && (this.charPointer < this.replacement.length())) {
/*  54 */         return this.replacement.charAt(this.charPointer++);
/*     */       }
/*     */ 
/*  57 */       int firstChar = nextChar();
/*  58 */       if (firstChar == -1) return -1;
/*  59 */       NormalizeCharMap nm = this.normMap.submap != null ? (NormalizeCharMap)this.normMap.submap.get(Character.valueOf((char)firstChar)) : null;
/*     */ 
/*  61 */       if (nm == null) return firstChar;
/*  62 */       NormalizeCharMap result = match(nm);
/*  63 */       if (result == null) return firstChar;
/*  64 */       this.replacement = result.normStr;
/*  65 */       this.charPointer = 0;
/*  66 */       if (result.diff != 0) {
/*  67 */         int prevCumulativeDiff = getLastCumulativeDiff();
/*  68 */         if (result.diff < 0)
/*  69 */           for (int i = 0; i < -result.diff; i++)
/*  70 */             addOffCorrectMap(this.nextCharCounter + i - prevCumulativeDiff, prevCumulativeDiff - 1 - i);
/*     */         else
/*  72 */           addOffCorrectMap(this.nextCharCounter - result.diff - prevCumulativeDiff, prevCumulativeDiff + result.diff);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int nextChar() throws IOException
/*     */   {
/*  79 */     this.nextCharCounter += 1;
/*  80 */     if ((this.buffer != null) && (!this.buffer.isEmpty())) {
/*  81 */       return ((Character)this.buffer.removeFirst()).charValue();
/*     */     }
/*  83 */     return this.input.read();
/*     */   }
/*     */ 
/*     */   private void pushChar(int c) {
/*  87 */     this.nextCharCounter -= 1;
/*  88 */     if (this.buffer == null)
/*  89 */       this.buffer = new LinkedList();
/*  90 */     this.buffer.addFirst(Character.valueOf((char)c));
/*     */   }
/*     */ 
/*     */   private void pushLastChar(int c) {
/*  94 */     if (this.buffer == null) {
/*  95 */       this.buffer = new LinkedList();
/*     */     }
/*  97 */     this.buffer.addLast(Character.valueOf((char)c));
/*     */   }
/*     */ 
/*     */   private NormalizeCharMap match(NormalizeCharMap map) throws IOException {
/* 101 */     NormalizeCharMap result = null;
/* 102 */     if (map.submap != null) {
/* 103 */       int chr = nextChar();
/* 104 */       if (chr != -1) {
/* 105 */         NormalizeCharMap subMap = (NormalizeCharMap)map.submap.get(Character.valueOf((char)chr));
/* 106 */         if (subMap != null) {
/* 107 */           result = match(subMap);
/*     */         }
/* 109 */         if (result == null) {
/* 110 */           pushChar(chr);
/*     */         }
/*     */       }
/*     */     }
/* 114 */     if ((result == null) && (map.normStr != null)) {
/* 115 */       result = map;
/*     */     }
/* 117 */     return result;
/*     */   }
/*     */ 
/*     */   public int read(char[] cbuf, int off, int len) throws IOException
/*     */   {
/* 122 */     char[] tmp = new char[len];
/* 123 */     int l = this.input.read(tmp, 0, len);
/* 124 */     if (l != -1) {
/* 125 */       for (int i = 0; i < l; i++)
/* 126 */         pushLastChar(tmp[i]);
/*     */     }
/* 128 */     l = 0;
/* 129 */     for (int i = off; i < off + len; i++) {
/* 130 */       int c = read();
/* 131 */       if (c == -1) break;
/* 132 */       cbuf[i] = (char)c;
/* 133 */       l++;
/*     */     }
/* 135 */     return l == 0 ? -1 : l;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.MappingCharFilter
 * JD-Core Version:    0.6.0
 */