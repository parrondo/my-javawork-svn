/*     */ package org.apache.lucene.analysis.tokenattributes;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.nio.CharBuffer;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.AttributeImpl;
/*     */ import org.apache.lucene.util.AttributeReflector;
/*     */ 
/*     */ public class CharTermAttributeImpl extends AttributeImpl
/*     */   implements CharTermAttribute, TermAttribute, Cloneable, Serializable
/*     */ {
/*     */   private static int MIN_BUFFER_SIZE;
/*  34 */   private char[] termBuffer = new char[ArrayUtil.oversize(MIN_BUFFER_SIZE, 2)];
/*  35 */   private int termLength = 0;
/*     */ 
/*     */   @Deprecated
/*     */   public String term() {
/*  40 */     return new String(this.termBuffer, 0, this.termLength);
/*     */   }
/*     */ 
/*     */   public final void copyBuffer(char[] buffer, int offset, int length) {
/*  44 */     growTermBuffer(length);
/*  45 */     System.arraycopy(buffer, offset, this.termBuffer, 0, length);
/*  46 */     this.termLength = length;
/*     */   }
/*     */   @Deprecated
/*     */   public void setTermBuffer(char[] buffer, int offset, int length) {
/*  51 */     copyBuffer(buffer, offset, length);
/*     */   }
/*     */   @Deprecated
/*     */   public void setTermBuffer(String buffer) {
/*  56 */     int length = buffer.length();
/*  57 */     growTermBuffer(length);
/*  58 */     buffer.getChars(0, length, this.termBuffer, 0);
/*  59 */     this.termLength = length;
/*     */   }
/*     */   @Deprecated
/*     */   public void setTermBuffer(String buffer, int offset, int length) {
/*  64 */     assert (offset <= buffer.length());
/*  65 */     assert (offset + length <= buffer.length());
/*  66 */     growTermBuffer(length);
/*  67 */     buffer.getChars(offset, offset + length, this.termBuffer, 0);
/*  68 */     this.termLength = length;
/*     */   }
/*     */ 
/*     */   public final char[] buffer() {
/*  72 */     return this.termBuffer;
/*     */   }
/*     */   @Deprecated
/*     */   public char[] termBuffer() {
/*  77 */     return this.termBuffer;
/*     */   }
/*     */ 
/*     */   public final char[] resizeBuffer(int newSize) {
/*  81 */     if (this.termBuffer.length < newSize)
/*     */     {
/*  84 */       char[] newCharBuffer = new char[ArrayUtil.oversize(newSize, 2)];
/*  85 */       System.arraycopy(this.termBuffer, 0, newCharBuffer, 0, this.termBuffer.length);
/*  86 */       this.termBuffer = newCharBuffer;
/*     */     }
/*  88 */     return this.termBuffer;
/*     */   }
/*     */   @Deprecated
/*     */   public char[] resizeTermBuffer(int newSize) {
/*  93 */     return resizeBuffer(newSize);
/*     */   }
/*     */ 
/*     */   private void growTermBuffer(int newSize) {
/*  97 */     if (this.termBuffer.length < newSize)
/*     */     {
/* 100 */       this.termBuffer = new char[ArrayUtil.oversize(newSize, 2)];
/*     */     }
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public int termLength() {
/* 106 */     return this.termLength;
/*     */   }
/*     */ 
/*     */   public final CharTermAttribute setLength(int length) {
/* 110 */     if (length > this.termBuffer.length)
/* 111 */       throw new IllegalArgumentException("length " + length + " exceeds the size of the termBuffer (" + this.termBuffer.length + ")");
/* 112 */     this.termLength = length;
/* 113 */     return this;
/*     */   }
/*     */ 
/*     */   public final CharTermAttribute setEmpty() {
/* 117 */     this.termLength = 0;
/* 118 */     return this;
/*     */   }
/*     */   @Deprecated
/*     */   public void setTermLength(int length) {
/* 123 */     setLength(length);
/*     */   }
/*     */ 
/*     */   public final int length()
/*     */   {
/* 128 */     return this.termLength;
/*     */   }
/*     */ 
/*     */   public final char charAt(int index) {
/* 132 */     if (index >= this.termLength)
/* 133 */       throw new IndexOutOfBoundsException();
/* 134 */     return this.termBuffer[index];
/*     */   }
/*     */ 
/*     */   public final CharSequence subSequence(int start, int end) {
/* 138 */     if ((start > this.termLength) || (end > this.termLength))
/* 139 */       throw new IndexOutOfBoundsException();
/* 140 */     return new String(this.termBuffer, start, end - start);
/*     */   }
/*     */ 
/*     */   public final CharTermAttribute append(CharSequence csq)
/*     */   {
/* 146 */     if (csq == null)
/* 147 */       return appendNull();
/* 148 */     return append(csq, 0, csq.length());
/*     */   }
/*     */ 
/*     */   public final CharTermAttribute append(CharSequence csq, int start, int end) {
/* 152 */     if (csq == null)
/* 153 */       csq = "null";
/* 154 */     int len = end - start; int csqlen = csq.length();
/* 155 */     if ((len < 0) || (start > csqlen) || (end > csqlen))
/* 156 */       throw new IndexOutOfBoundsException();
/* 157 */     if (len == 0)
/* 158 */       return this;
/* 159 */     resizeBuffer(this.termLength + len);
/* 160 */     if (len > 4) {
/* 161 */       if ((csq instanceof String)) {
/* 162 */         ((String)csq).getChars(start, end, this.termBuffer, this.termLength);
/* 163 */       } else if ((csq instanceof StringBuilder)) {
/* 164 */         ((StringBuilder)csq).getChars(start, end, this.termBuffer, this.termLength);
/* 165 */       } else if ((csq instanceof Serializable)) {
/* 166 */         System.arraycopy(((Serializable)csq).buffer(), start, this.termBuffer, this.termLength, len);
/* 167 */       } else if (((csq instanceof CharBuffer)) && (((CharBuffer)csq).hasArray())) {
/* 168 */         CharBuffer cb = (CharBuffer)csq;
/* 169 */         System.arraycopy(cb.array(), cb.arrayOffset() + cb.position() + start, this.termBuffer, this.termLength, len);
/* 170 */       } else if ((csq instanceof StringBuffer)) {
/* 171 */         ((StringBuffer)csq).getChars(start, end, this.termBuffer, this.termLength);
/*     */       } else {
/* 173 */         while (start < end) {
/* 174 */           this.termBuffer[(this.termLength++)] = csq.charAt(start++);
/*     */         }
/* 176 */         return this;
/*     */       }
/* 178 */       this.termLength += len;
/* 179 */       return this;
/*     */     }
/* 181 */     while (start < end)
/* 182 */       this.termBuffer[(this.termLength++)] = csq.charAt(start++);
/* 183 */     return this;
/*     */   }
/*     */ 
/*     */   public final CharTermAttribute append(char c)
/*     */   {
/* 188 */     resizeBuffer(this.termLength + 1)[(this.termLength++)] = c;
/* 189 */     return this;
/*     */   }
/*     */ 
/*     */   public final CharTermAttribute append(String s)
/*     */   {
/* 195 */     if (s == null)
/* 196 */       return appendNull();
/* 197 */     int len = s.length();
/* 198 */     s.getChars(0, len, resizeBuffer(this.termLength + len), this.termLength);
/* 199 */     this.termLength += len;
/* 200 */     return this;
/*     */   }
/*     */ 
/*     */   public final CharTermAttribute append(StringBuilder s) {
/* 204 */     if (s == null)
/* 205 */       return appendNull();
/* 206 */     int len = s.length();
/* 207 */     s.getChars(0, len, resizeBuffer(this.termLength + len), this.termLength);
/* 208 */     this.termLength += len;
/* 209 */     return this;
/*     */   }
/*     */ 
/*     */   public final CharTermAttribute append(CharTermAttribute ta) {
/* 213 */     if (ta == null)
/* 214 */       return appendNull();
/* 215 */     int len = ta.length();
/* 216 */     System.arraycopy(ta.buffer(), 0, resizeBuffer(this.termLength + len), this.termLength, len);
/* 217 */     this.termLength += len;
/* 218 */     return this;
/*     */   }
/*     */ 
/*     */   private CharTermAttribute appendNull() {
/* 222 */     resizeBuffer(this.termLength + 4);
/* 223 */     this.termBuffer[(this.termLength++)] = 'n';
/* 224 */     this.termBuffer[(this.termLength++)] = 'u';
/* 225 */     this.termBuffer[(this.termLength++)] = 'l';
/* 226 */     this.termBuffer[(this.termLength++)] = 'l';
/* 227 */     return this;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 234 */     int code = this.termLength;
/* 235 */     code = code * 31 + ArrayUtil.hashCode(this.termBuffer, 0, this.termLength);
/* 236 */     return code;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 241 */     this.termLength = 0;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 246 */     CharTermAttributeImpl t = (CharTermAttributeImpl)super.clone();
/*     */ 
/* 248 */     t.termBuffer = new char[this.termLength];
/* 249 */     System.arraycopy(this.termBuffer, 0, t.termBuffer, 0, this.termLength);
/* 250 */     return t;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 255 */     if (other == this) {
/* 256 */       return true;
/*     */     }
/*     */ 
/* 259 */     if ((other instanceof CharTermAttributeImpl)) {
/* 260 */       CharTermAttributeImpl o = (CharTermAttributeImpl)other;
/* 261 */       if (this.termLength != o.termLength)
/* 262 */         return false;
/* 263 */       for (int i = 0; i < this.termLength; i++) {
/* 264 */         if (this.termBuffer[i] != o.termBuffer[i]) {
/* 265 */           return false;
/*     */         }
/*     */       }
/* 268 */       return true;
/*     */     }
/*     */ 
/* 271 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 286 */     return new String(this.termBuffer, 0, this.termLength);
/*     */   }
/*     */ 
/*     */   public void reflectWith(AttributeReflector reflector)
/*     */   {
/* 291 */     reflector.reflect(Serializable.class, "term", toString());
/*     */   }
/*     */ 
/*     */   public void copyTo(AttributeImpl target)
/*     */   {
/* 296 */     if ((target instanceof Serializable)) {
/* 297 */       CharTermAttribute t = (Serializable)target;
/* 298 */       t.copyBuffer(this.termBuffer, 0, this.termLength);
/*     */     } else {
/* 300 */       TermAttribute t = (TermAttribute)target;
/* 301 */       t.setTermBuffer(this.termBuffer, 0, this.termLength);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  32 */     MIN_BUFFER_SIZE = 10;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl
 * JD-Core Version:    0.6.0
 */