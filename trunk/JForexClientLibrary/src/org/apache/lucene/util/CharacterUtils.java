/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ 
/*     */ public abstract class CharacterUtils
/*     */ {
/*  31 */   private static final Java4CharacterUtils JAVA_4 = new Java4CharacterUtils();
/*  32 */   private static final Java5CharacterUtils JAVA_5 = new Java5CharacterUtils();
/*     */ 
/*     */   public static CharacterUtils getInstance(Version matchVersion)
/*     */   {
/*  44 */     return matchVersion.onOrAfter(Version.LUCENE_31) ? JAVA_5 : JAVA_4;
/*     */   }
/*     */ 
/*     */   public abstract int codePointAt(char[] paramArrayOfChar, int paramInt);
/*     */ 
/*     */   public abstract int codePointAt(CharSequence paramCharSequence, int paramInt);
/*     */ 
/*     */   public abstract int codePointAt(char[] paramArrayOfChar, int paramInt1, int paramInt2);
/*     */ 
/*     */   public static CharacterBuffer newCharacterBuffer(int bufferSize)
/*     */   {
/* 122 */     if (bufferSize < 2)
/* 123 */       throw new IllegalArgumentException("buffersize must be >= 2");
/* 124 */     return new CharacterBuffer(new char[bufferSize], 0, 0);
/*     */   }
/*     */ 
/*     */   public abstract boolean fill(CharacterBuffer paramCharacterBuffer, Reader paramReader)
/*     */     throws IOException;
/*     */ 
/*     */   public static final class CharacterBuffer
/*     */   {
/*     */     private final char[] buffer;
/*     */     private int offset;
/*     */     private int length;
/* 239 */     private char lastTrailingHighSurrogate = '\000';
/*     */ 
/*     */     CharacterBuffer(char[] buffer, int offset, int length) {
/* 242 */       this.buffer = buffer;
/* 243 */       this.offset = offset;
/* 244 */       this.length = length;
/*     */     }
/*     */ 
/*     */     public char[] getBuffer()
/*     */     {
/* 253 */       return this.buffer;
/*     */     }
/*     */ 
/*     */     public int getOffset()
/*     */     {
/* 262 */       return this.offset;
/*     */     }
/*     */ 
/*     */     public int getLength()
/*     */     {
/* 272 */       return this.length;
/*     */     }
/*     */ 
/*     */     public void reset()
/*     */     {
/* 280 */       this.offset = 0;
/* 281 */       this.length = 0;
/* 282 */       this.lastTrailingHighSurrogate = '\000';
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class Java4CharacterUtils extends CharacterUtils
/*     */   {
/*     */     public final int codePointAt(char[] chars, int offset)
/*     */     {
/* 203 */       return chars[offset];
/*     */     }
/*     */ 
/*     */     public int codePointAt(CharSequence seq, int offset)
/*     */     {
/* 208 */       return seq.charAt(offset);
/*     */     }
/*     */ 
/*     */     public int codePointAt(char[] chars, int offset, int limit)
/*     */     {
/* 213 */       if (offset >= limit)
/* 214 */         throw new IndexOutOfBoundsException("offset must be less than limit");
/* 215 */       return chars[offset];
/*     */     }
/*     */ 
/*     */     public boolean fill(CharacterUtils.CharacterBuffer buffer, Reader reader) throws IOException
/*     */     {
/* 220 */       CharacterUtils.CharacterBuffer.access$102(buffer, 0);
/* 221 */       int read = reader.read(buffer.buffer);
/* 222 */       if (read == -1)
/* 223 */         return false;
/* 224 */       CharacterUtils.CharacterBuffer.access$302(buffer, read);
/* 225 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class Java5CharacterUtils extends CharacterUtils
/*     */   {
/*     */     public final int codePointAt(char[] chars, int offset)
/*     */     {
/* 161 */       return Character.codePointAt(chars, offset);
/*     */     }
/*     */ 
/*     */     public int codePointAt(CharSequence seq, int offset)
/*     */     {
/* 166 */       return Character.codePointAt(seq, offset);
/*     */     }
/*     */ 
/*     */     public int codePointAt(char[] chars, int offset, int limit)
/*     */     {
/* 171 */       return Character.codePointAt(chars, offset, limit);
/*     */     }
/*     */ 
/*     */     public boolean fill(CharacterUtils.CharacterBuffer buffer, Reader reader) throws IOException
/*     */     {
/* 176 */       char[] charBuffer = buffer.buffer;
/* 177 */       CharacterUtils.CharacterBuffer.access$102(buffer, 0);
/* 178 */       charBuffer[0] = CharacterUtils.CharacterBuffer.access$200(buffer);
/* 179 */       int offset = buffer.lastTrailingHighSurrogate == 0 ? 0 : 1;
/* 180 */       CharacterUtils.CharacterBuffer.access$202(buffer, '\000');
/* 181 */       int read = reader.read(charBuffer, offset, charBuffer.length - offset);
/*     */ 
/* 183 */       if (read == -1) {
/* 184 */         CharacterUtils.CharacterBuffer.access$302(buffer, offset);
/* 185 */         return offset != 0;
/*     */       }
/* 187 */       CharacterUtils.CharacterBuffer.access$302(buffer, read + offset);
/*     */ 
/* 189 */       if ((buffer.length > 1) && (Character.isHighSurrogate(charBuffer[(buffer.length - 1)])))
/*     */       {
/* 191 */         CharacterUtils.CharacterBuffer.access$202(buffer, charBuffer[CharacterUtils.CharacterBuffer.access$306(buffer)]);
/*     */       }
/* 193 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.CharacterUtils
 * JD-Core Version:    0.6.0
 */