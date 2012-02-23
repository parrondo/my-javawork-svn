/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ 
/*     */ public final class CharsRef
/*     */   implements Comparable<CharsRef>, CharSequence
/*     */ {
/*     */   private static final char[] EMPTY_ARRAY;
/*     */   public char[] chars;
/*     */   public int offset;
/*     */   public int length;
/*     */   private static final Comparator<CharsRef> utf16SortedAsUTF8SortOrder;
/*     */ 
/*     */   public CharsRef()
/*     */   {
/*  38 */     this(EMPTY_ARRAY, 0, 0);
/*     */   }
/*     */ 
/*     */   public CharsRef(int capacity)
/*     */   {
/*  46 */     this.chars = new char[capacity];
/*     */   }
/*     */ 
/*     */   public CharsRef(char[] chars, int offset, int length)
/*     */   {
/*  54 */     assert (chars != null);
/*  55 */     assert (chars.length >= offset + length);
/*  56 */     this.chars = chars;
/*  57 */     this.offset = offset;
/*  58 */     this.length = length;
/*     */   }
/*     */ 
/*     */   public CharsRef(String string)
/*     */   {
/*  66 */     this.chars = string.toCharArray();
/*  67 */     this.offset = 0;
/*  68 */     this.length = this.chars.length;
/*     */   }
/*     */ 
/*     */   public CharsRef(CharsRef other)
/*     */   {
/*  77 */     copy(other);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  82 */     return new CharsRef(this);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  87 */     int prime = 31;
/*  88 */     int result = 0;
/*  89 */     int end = this.offset + this.length;
/*  90 */     for (int i = this.offset; i < end; i++) {
/*  91 */       result = 31 * result + this.chars[i];
/*     */     }
/*  93 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/*  98 */     if (this == other) {
/*  99 */       return true;
/*     */     }
/*     */ 
/* 102 */     if ((other instanceof CharsRef)) {
/* 103 */       return charsEquals((CharsRef)other);
/*     */     }
/*     */ 
/* 106 */     if ((other instanceof CharSequence)) {
/* 107 */       CharSequence seq = (CharSequence)other;
/* 108 */       if (this.length == seq.length()) {
/* 109 */         int n = this.length;
/* 110 */         int i = this.offset;
/* 111 */         int j = 0;
/* 112 */         while (n-- != 0) {
/* 113 */           if (this.chars[(i++)] != seq.charAt(j++))
/* 114 */             return false;
/*     */         }
/* 116 */         return true;
/*     */       }
/*     */     }
/* 119 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean charsEquals(CharsRef other) {
/* 123 */     if (this.length == other.length) {
/* 124 */       int otherUpto = other.offset;
/* 125 */       char[] otherChars = other.chars;
/* 126 */       int end = this.offset + this.length;
/* 127 */       for (int upto = this.offset; upto < end; otherUpto++) {
/* 128 */         if (this.chars[upto] != otherChars[otherUpto])
/* 129 */           return false;
/* 127 */         upto++;
/*     */       }
/*     */ 
/* 132 */       return true;
/*     */     }
/* 134 */     return false;
/*     */   }
/*     */ 
/*     */   public int compareTo(CharsRef other)
/*     */   {
/* 140 */     if (this == other) {
/* 141 */       return 0;
/*     */     }
/* 143 */     char[] aChars = this.chars;
/* 144 */     int aUpto = this.offset;
/* 145 */     char[] bChars = other.chars;
/* 146 */     int bUpto = other.offset;
/*     */ 
/* 148 */     int aStop = aUpto + Math.min(this.length, other.length);
/*     */ 
/* 150 */     while (aUpto < aStop) {
/* 151 */       int aInt = aChars[(aUpto++)];
/* 152 */       int bInt = bChars[(bUpto++)];
/* 153 */       if (aInt > bInt)
/* 154 */         return 1;
/* 155 */       if (aInt < bInt) {
/* 156 */         return -1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 161 */     return this.length - other.length;
/*     */   }
/*     */ 
/*     */   public void copy(CharsRef other)
/*     */   {
/* 172 */     if (this.chars == null)
/* 173 */       this.chars = new char[other.length];
/*     */     else {
/* 175 */       this.chars = ArrayUtil.grow(this.chars, other.length);
/*     */     }
/* 177 */     System.arraycopy(other.chars, other.offset, this.chars, 0, other.length);
/* 178 */     this.length = other.length;
/* 179 */     this.offset = 0;
/*     */   }
/*     */ 
/*     */   public void grow(int newLength) {
/* 183 */     if (this.chars.length < newLength)
/* 184 */       this.chars = ArrayUtil.grow(this.chars, newLength);
/*     */   }
/*     */ 
/*     */   public void copy(char[] otherChars, int otherOffset, int otherLength)
/*     */   {
/* 192 */     this.offset = 0;
/* 193 */     append(otherChars, otherOffset, otherLength);
/*     */   }
/*     */ 
/*     */   public void append(char[] otherChars, int otherOffset, int otherLength)
/*     */   {
/* 200 */     grow(this.offset + otherLength);
/* 201 */     System.arraycopy(otherChars, otherOffset, this.chars, this.offset, otherLength);
/*     */ 
/* 203 */     this.length = otherLength;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 208 */     return new String(this.chars, this.offset, this.length);
/*     */   }
/*     */ 
/*     */   public int length() {
/* 212 */     return this.length;
/*     */   }
/*     */ 
/*     */   public char charAt(int index) {
/* 216 */     return this.chars[(this.offset + index)];
/*     */   }
/*     */ 
/*     */   public CharSequence subSequence(int start, int end) {
/* 220 */     return new CharsRef(this.chars, this.offset + start, this.offset + end - 1);
/*     */   }
/*     */ 
/*     */   public static Comparator<CharsRef> getUTF16SortedAsUTF8Comparator()
/*     */   {
/* 226 */     return utf16SortedAsUTF8SortOrder;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  29 */     EMPTY_ARRAY = new char[0];
/*     */ 
/* 223 */     utf16SortedAsUTF8SortOrder = new UTF16SortedAsUTF8Comparator(null);
/*     */   }
/*     */ 
/*     */   private static class UTF16SortedAsUTF8Comparator
/*     */     implements Comparator<CharsRef>
/*     */   {
/*     */     public int compare(CharsRef a, CharsRef b)
/*     */     {
/* 234 */       if (a == b) {
/* 235 */         return 0;
/*     */       }
/* 237 */       char[] aChars = a.chars;
/* 238 */       int aUpto = a.offset;
/* 239 */       char[] bChars = b.chars;
/* 240 */       int bUpto = b.offset;
/*     */ 
/* 242 */       int aStop = aUpto + Math.min(a.length, b.length);
/*     */ 
/* 244 */       while (aUpto < aStop) {
/* 245 */         char aChar = aChars[(aUpto++)];
/* 246 */         char bChar = bChars[(bUpto++)];
/* 247 */         if (aChar != bChar)
/*     */         {
/* 251 */           if ((aChar >= 55296) && (bChar >= 55296)) {
/* 252 */             if (aChar >= 57344)
/* 253 */               aChar = (char)(aChar - 'ࠀ');
/*     */             else {
/* 255 */               aChar = (char)(aChar + ' ');
/*     */             }
/*     */ 
/* 258 */             if (bChar >= 57344)
/* 259 */               bChar = (char)(bChar - 'ࠀ');
/*     */             else {
/* 261 */               bChar = (char)(bChar + ' ');
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 266 */           return aChar - bChar;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 271 */       return a.length - b.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.CharsRef
 * JD-Core Version:    0.6.0
 */