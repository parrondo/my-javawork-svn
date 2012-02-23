/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public abstract class DataInput
/*     */   implements Cloneable
/*     */ {
/*     */   private boolean preUTF8Strings;
/*     */ 
/*     */   public void setModifiedUTF8StringsMode()
/*     */   {
/*  37 */     this.preUTF8Strings = true;
/*     */   }
/*     */ 
/*     */   public abstract byte readByte()
/*     */     throws IOException;
/*     */ 
/*     */   public abstract void readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   public void readBytes(byte[] b, int offset, int len, boolean useBuffer)
/*     */     throws IOException
/*     */   {
/*  70 */     readBytes(b, offset, len);
/*     */   }
/*     */ 
/*     */   public short readShort()
/*     */     throws IOException
/*     */   {
/*  77 */     return (short)((readByte() & 0xFF) << 8 | readByte() & 0xFF);
/*     */   }
/*     */ 
/*     */   public int readInt()
/*     */     throws IOException
/*     */   {
/*  84 */     return (readByte() & 0xFF) << 24 | (readByte() & 0xFF) << 16 | (readByte() & 0xFF) << 8 | readByte() & 0xFF;
/*     */   }
/*     */ 
/*     */   public int readVInt()
/*     */     throws IOException
/*     */   {
/* 105 */     byte b = readByte();
/* 106 */     int i = b & 0x7F;
/* 107 */     if ((b & 0x80) == 0) return i;
/* 108 */     b = readByte();
/* 109 */     i |= (b & 0x7F) << 7;
/* 110 */     if ((b & 0x80) == 0) return i;
/* 111 */     b = readByte();
/* 112 */     i |= (b & 0x7F) << 14;
/* 113 */     if ((b & 0x80) == 0) return i;
/* 114 */     b = readByte();
/* 115 */     i |= (b & 0x7F) << 21;
/* 116 */     if ((b & 0x80) == 0) return i;
/* 117 */     b = readByte();
/* 118 */     assert ((b & 0x80) == 0);
/* 119 */     return i | (b & 0x7F) << 28;
/*     */   }
/*     */ 
/*     */   public long readLong()
/*     */     throws IOException
/*     */   {
/* 126 */     return readInt() << 32 | readInt() & 0xFFFFFFFF;
/*     */   }
/*     */ 
/*     */   public long readVLong()
/*     */     throws IOException
/*     */   {
/* 144 */     byte b = readByte();
/* 145 */     long i = b & 0x7F;
/* 146 */     if ((b & 0x80) == 0) return i;
/* 147 */     b = readByte();
/* 148 */     i |= (b & 0x7F) << 7;
/* 149 */     if ((b & 0x80) == 0) return i;
/* 150 */     b = readByte();
/* 151 */     i |= (b & 0x7F) << 14;
/* 152 */     if ((b & 0x80) == 0) return i;
/* 153 */     b = readByte();
/* 154 */     i |= (b & 0x7F) << 21;
/* 155 */     if ((b & 0x80) == 0) return i;
/* 156 */     b = readByte();
/* 157 */     i |= (b & 0x7F) << 28;
/* 158 */     if ((b & 0x80) == 0) return i;
/* 159 */     b = readByte();
/* 160 */     i |= (b & 0x7F) << 35;
/* 161 */     if ((b & 0x80) == 0) return i;
/* 162 */     b = readByte();
/* 163 */     i |= (b & 0x7F) << 42;
/* 164 */     if ((b & 0x80) == 0) return i;
/* 165 */     b = readByte();
/* 166 */     i |= (b & 0x7F) << 49;
/* 167 */     if ((b & 0x80) == 0) return i;
/* 168 */     b = readByte();
/* 169 */     assert ((b & 0x80) == 0);
/* 170 */     return i | (b & 0x7F) << 56;
/*     */   }
/*     */ 
/*     */   public String readString()
/*     */     throws IOException
/*     */   {
/* 177 */     if (this.preUTF8Strings)
/* 178 */       return readModifiedUTF8String();
/* 179 */     int length = readVInt();
/* 180 */     byte[] bytes = new byte[length];
/* 181 */     readBytes(bytes, 0, length);
/* 182 */     return new String(bytes, 0, length, "UTF-8");
/*     */   }
/*     */ 
/*     */   private String readModifiedUTF8String() throws IOException {
/* 186 */     int length = readVInt();
/* 187 */     char[] chars = new char[length];
/* 188 */     readChars(chars, 0, length);
/* 189 */     return new String(chars, 0, length);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void readChars(char[] buffer, int start, int length)
/*     */     throws IOException
/*     */   {
/* 205 */     int end = start + length;
/* 206 */     for (int i = start; i < end; i++) {
/* 207 */       byte b = readByte();
/* 208 */       if ((b & 0x80) == 0)
/* 209 */         buffer[i] = (char)(b & 0x7F);
/* 210 */       else if ((b & 0xE0) != 224) {
/* 211 */         buffer[i] = (char)((b & 0x1F) << 6 | readByte() & 0x3F);
/*     */       }
/*     */       else
/* 214 */         buffer[i] = (char)((b & 0xF) << 12 | (readByte() & 0x3F) << 6 | readByte() & 0x3F);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 232 */     DataInput clone = null;
/*     */     try {
/* 234 */       clone = (DataInput)super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/*     */     }
/* 237 */     return clone;
/*     */   }
/*     */ 
/*     */   public Map<String, String> readStringStringMap() throws IOException {
/* 241 */     Map map = new HashMap();
/* 242 */     int count = readInt();
/* 243 */     for (int i = 0; i < count; i++) {
/* 244 */       String key = readString();
/* 245 */       String val = readString();
/* 246 */       map.put(key, val);
/*     */     }
/*     */ 
/* 249 */     return map;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.DataInput
 * JD-Core Version:    0.6.0
 */