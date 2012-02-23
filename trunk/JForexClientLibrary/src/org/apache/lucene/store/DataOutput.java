/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.lucene.util.BytesRef;
/*     */ import org.apache.lucene.util.UnicodeUtil;
/*     */ 
/*     */ public abstract class DataOutput
/*     */ {
/*     */   private static int COPY_BUFFER_SIZE;
/*     */   private byte[] copyBuffer;
/*     */ 
/*     */   public abstract void writeByte(byte paramByte)
/*     */     throws IOException;
/*     */ 
/*     */   public void writeBytes(byte[] b, int length)
/*     */     throws IOException
/*     */   {
/*  43 */     writeBytes(b, 0, length);
/*     */   }
/*     */ 
/*     */   public abstract void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   public void writeInt(int i)
/*     */     throws IOException
/*     */   {
/*  58 */     writeByte((byte)(i >> 24));
/*  59 */     writeByte((byte)(i >> 16));
/*  60 */     writeByte((byte)(i >> 8));
/*  61 */     writeByte((byte)i);
/*     */   }
/*     */ 
/*     */   public final void writeVInt(int i)
/*     */     throws IOException
/*     */   {
/*  70 */     while ((i & 0xFFFFFF80) != 0) {
/*  71 */       writeByte((byte)(i & 0x7F | 0x80));
/*  72 */       i >>>= 7;
/*     */     }
/*  74 */     writeByte((byte)i);
/*     */   }
/*     */ 
/*     */   public void writeLong(long i)
/*     */     throws IOException
/*     */   {
/*  81 */     writeInt((int)(i >> 32));
/*  82 */     writeInt((int)i);
/*     */   }
/*     */ 
/*     */   public final void writeVLong(long i)
/*     */     throws IOException
/*     */   {
/*  91 */     while ((i & 0xFFFFFF80) != 0L) {
/*  92 */       writeByte((byte)(int)(i & 0x7F | 0x80));
/*  93 */       i >>>= 7;
/*     */     }
/*  95 */     writeByte((byte)(int)i);
/*     */   }
/*     */ 
/*     */   public void writeString(String s)
/*     */     throws IOException
/*     */   {
/* 102 */     BytesRef utf8Result = new BytesRef(10);
/* 103 */     UnicodeUtil.UTF16toUTF8(s, 0, s.length(), utf8Result);
/* 104 */     writeVInt(utf8Result.length);
/* 105 */     writeBytes(utf8Result.bytes, 0, utf8Result.length);
/*     */   }
/*     */ 
/*     */   public void copyBytes(DataInput input, long numBytes)
/*     */     throws IOException
/*     */   {
/* 113 */     assert (numBytes >= 0L) : ("numBytes=" + numBytes);
/* 114 */     long left = numBytes;
/* 115 */     if (this.copyBuffer == null)
/* 116 */       this.copyBuffer = new byte[COPY_BUFFER_SIZE];
/* 117 */     while (left > 0L)
/*     */     {
/*     */       int toCopy;
/*     */       int toCopy;
/* 119 */       if (left > COPY_BUFFER_SIZE)
/* 120 */         toCopy = COPY_BUFFER_SIZE;
/*     */       else
/* 122 */         toCopy = (int)left;
/* 123 */       input.readBytes(this.copyBuffer, 0, toCopy);
/* 124 */       writeBytes(this.copyBuffer, 0, toCopy);
/* 125 */       left -= toCopy;
/*     */     }
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void writeChars(String s, int start, int length)
/*     */     throws IOException
/*     */   {
/* 140 */     int end = start + length;
/* 141 */     for (int i = start; i < end; i++) {
/* 142 */       int code = s.charAt(i);
/* 143 */       if ((code >= 1) && (code <= 127)) {
/* 144 */         writeByte((byte)code);
/* 145 */       } else if (((code >= 128) && (code <= 2047)) || (code == 0)) {
/* 146 */         writeByte((byte)(0xC0 | code >> 6));
/* 147 */         writeByte((byte)(0x80 | code & 0x3F));
/*     */       } else {
/* 149 */         writeByte((byte)(0xE0 | code >>> 12));
/* 150 */         writeByte((byte)(0x80 | code >> 6 & 0x3F));
/* 151 */         writeByte((byte)(0x80 | code & 0x3F));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void writeChars(char[] s, int start, int length)
/*     */     throws IOException
/*     */   {
/* 166 */     int end = start + length;
/* 167 */     for (int i = start; i < end; i++) {
/* 168 */       int code = s[i];
/* 169 */       if ((code >= 1) && (code <= 127)) {
/* 170 */         writeByte((byte)code);
/* 171 */       } else if (((code >= 128) && (code <= 2047)) || (code == 0)) {
/* 172 */         writeByte((byte)(0xC0 | code >> 6));
/* 173 */         writeByte((byte)(0x80 | code & 0x3F));
/*     */       } else {
/* 175 */         writeByte((byte)(0xE0 | code >>> 12));
/* 176 */         writeByte((byte)(0x80 | code >> 6 & 0x3F));
/* 177 */         writeByte((byte)(0x80 | code & 0x3F));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeStringStringMap(Map<String, String> map) throws IOException {
/* 183 */     if (map == null) {
/* 184 */       writeInt(0);
/*     */     } else {
/* 186 */       writeInt(map.size());
/* 187 */       for (Map.Entry entry : map.entrySet()) {
/* 188 */         writeString((String)entry.getKey());
/* 189 */         writeString((String)entry.getValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 108 */     COPY_BUFFER_SIZE = 16384;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.DataOutput
 * JD-Core Version:    0.6.0
 */