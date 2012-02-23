/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ 
/*     */ public final class BitVector
/*     */   implements Cloneable
/*     */ {
/*     */   private byte[] bits;
/*     */   private int size;
/*     */   private int count;
/*     */   private static final byte[] BYTE_COUNTS;
/*     */   private static String CODEC;
/*     */   private static final int VERSION_PRE = -1;
/*     */   private static final int VERSION_START = 0;
/*     */   private static final int VERSION_CURRENT = 0;
/*     */ 
/*     */   public BitVector(int n)
/*     */   {
/*  45 */     this.size = n;
/*  46 */     this.bits = new byte[getNumBytes(this.size)];
/*  47 */     this.count = 0;
/*     */   }
/*     */ 
/*     */   BitVector(byte[] bits, int size) {
/*  51 */     this.bits = bits;
/*  52 */     this.size = size;
/*  53 */     this.count = -1;
/*     */   }
/*     */ 
/*     */   private int getNumBytes(int size) {
/*  57 */     int bytesLength = size >>> 3;
/*  58 */     if ((size & 0x7) != 0) {
/*  59 */       bytesLength++;
/*     */     }
/*  61 */     return bytesLength;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  66 */     byte[] copyBits = new byte[this.bits.length];
/*  67 */     System.arraycopy(this.bits, 0, copyBits, 0, this.bits.length);
/*  68 */     BitVector clone = new BitVector(copyBits, this.size);
/*  69 */     clone.count = this.count;
/*  70 */     return clone;
/*     */   }
/*     */ 
/*     */   public final void set(int bit)
/*     */   {
/*  75 */     if (bit >= this.size)
/*  76 */       throw new ArrayIndexOutOfBoundsException("bit=" + bit + " size=" + this.size);
/*     */     int tmp54_53 = (bit >> 3);
/*     */     byte[] tmp54_48 = this.bits; tmp54_48[tmp54_53] = (byte)(tmp54_48[tmp54_53] | 1 << (bit & 0x7));
/*  79 */     this.count = -1;
/*     */   }
/*     */ 
/*     */   public final boolean getAndSet(int bit)
/*     */   {
/*  85 */     if (bit >= this.size) {
/*  86 */       throw new ArrayIndexOutOfBoundsException("bit=" + bit + " size=" + this.size);
/*     */     }
/*  88 */     int pos = bit >> 3;
/*  89 */     int v = this.bits[pos];
/*  90 */     int flag = 1 << (bit & 0x7);
/*  91 */     if ((flag & v) != 0) {
/*  92 */       return true;
/*     */     }
/*  94 */     this.bits[pos] = (byte)(v | flag);
/*  95 */     if (this.count != -1)
/*  96 */       this.count += 1;
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */   public final void clear(int bit)
/*     */   {
/* 103 */     if (bit >= this.size)
/* 104 */       throw new ArrayIndexOutOfBoundsException(bit);
/*     */     int tmp24_23 = (bit >> 3);
/*     */     byte[] tmp24_18 = this.bits; tmp24_18[tmp24_23] = (byte)(tmp24_18[tmp24_23] & (1 << (bit & 0x7) ^ 0xFFFFFFFF));
/* 107 */     this.count = -1;
/*     */   }
/*     */ 
/*     */   public final boolean get(int bit)
/*     */   {
/* 113 */     assert ((bit >= 0) && (bit < this.size)) : ("bit " + bit + " is out of bounds 0.." + (this.size - 1));
/* 114 */     return (this.bits[(bit >> 3)] & 1 << (bit & 0x7)) != 0;
/*     */   }
/*     */ 
/*     */   public final int size()
/*     */   {
/* 120 */     return this.size;
/*     */   }
/*     */ 
/*     */   public final int count()
/*     */   {
/* 128 */     if (this.count == -1) {
/* 129 */       int c = 0;
/* 130 */       int end = this.bits.length;
/* 131 */       for (int i = 0; i < end; i++)
/* 132 */         c += BYTE_COUNTS[(this.bits[i] & 0xFF)];
/* 133 */       this.count = c;
/*     */     }
/* 135 */     return this.count;
/*     */   }
/*     */ 
/*     */   public final int getRecomputedCount()
/*     */   {
/* 140 */     int c = 0;
/* 141 */     int end = this.bits.length;
/* 142 */     for (int i = 0; i < end; i++)
/* 143 */       c += BYTE_COUNTS[(this.bits[i] & 0xFF)];
/* 144 */     return c;
/*     */   }
/*     */ 
/*     */   public final void write(Directory d, String name)
/*     */     throws IOException
/*     */   {
/* 181 */     IndexOutput output = d.createOutput(name);
/*     */     try {
/* 183 */       output.writeInt(-2);
/* 184 */       CodecUtil.writeHeader(output, CODEC, 0);
/* 185 */       if (isSparse())
/* 186 */         writeDgaps(output);
/*     */       else
/* 188 */         writeBits(output);
/*     */     }
/*     */     finally {
/* 191 */       output.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeBits(IndexOutput output) throws IOException
/*     */   {
/* 197 */     output.writeInt(size());
/* 198 */     output.writeInt(count());
/* 199 */     output.writeBytes(this.bits, this.bits.length);
/*     */   }
/*     */ 
/*     */   private void writeDgaps(IndexOutput output) throws IOException
/*     */   {
/* 204 */     output.writeInt(-1);
/* 205 */     output.writeInt(size());
/* 206 */     output.writeInt(count());
/* 207 */     int last = 0;
/* 208 */     int n = count();
/* 209 */     int m = this.bits.length;
/* 210 */     for (int i = 0; (i < m) && (n > 0); i++)
/* 211 */       if (this.bits[i] != 0) {
/* 212 */         output.writeVInt(i - last);
/* 213 */         output.writeByte(this.bits[i]);
/* 214 */         last = i;
/* 215 */         n -= BYTE_COUNTS[(this.bits[i] & 0xFF)];
/*     */       }
/*     */   }
/*     */ 
/*     */   private boolean isSparse()
/*     */   {
/* 223 */     int setCount = count();
/* 224 */     if (setCount == 0) {
/* 225 */       return true;
/*     */     }
/*     */ 
/* 228 */     int avgGapLength = this.bits.length / setCount;
/*     */     int expectedDGapBytes;
/*     */     int expectedDGapBytes;
/* 232 */     if (avgGapLength <= 128) {
/* 233 */       expectedDGapBytes = 1;
/*     */     }
/*     */     else
/*     */     {
/*     */       int expectedDGapBytes;
/* 234 */       if (avgGapLength <= 16384) {
/* 235 */         expectedDGapBytes = 2;
/*     */       }
/*     */       else
/*     */       {
/*     */         int expectedDGapBytes;
/* 236 */         if (avgGapLength <= 2097152) {
/* 237 */           expectedDGapBytes = 3;
/*     */         }
/*     */         else
/*     */         {
/*     */           int expectedDGapBytes;
/* 238 */           if (avgGapLength <= 268435456)
/* 239 */             expectedDGapBytes = 4;
/*     */           else {
/* 241 */             expectedDGapBytes = 5;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 246 */     int bytesPerSetBit = expectedDGapBytes + 1;
/*     */ 
/* 249 */     long expectedBits = 32 + 8 * bytesPerSetBit * count();
/*     */ 
/* 252 */     long factor = 10L;
/* 253 */     return 10L * expectedBits < size();
/*     */   }
/*     */ 
/*     */   public BitVector(Directory d, String name)
/*     */     throws IOException
/*     */   {
/* 260 */     IndexInput input = d.openInput(name);
/*     */     try
/*     */     {
/* 263 */       int firstInt = input.readInt();
/*     */ 
/* 265 */       if (firstInt == -2)
/*     */       {
/* 267 */         int version = CodecUtil.checkHeader(input, CODEC, 0, 0);
/* 268 */         this.size = input.readInt();
/*     */       } else {
/* 270 */         int version = -1;
/* 271 */         this.size = firstInt;
/*     */       }
/* 273 */       if (this.size == -1)
/* 274 */         readDgaps(input);
/*     */       else
/* 276 */         readBits(input);
/*     */     }
/*     */     finally {
/* 279 */       input.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readBits(IndexInput input) throws IOException
/*     */   {
/* 285 */     this.count = input.readInt();
/* 286 */     this.bits = new byte[getNumBytes(this.size)];
/* 287 */     input.readBytes(this.bits, 0, this.bits.length);
/*     */   }
/*     */ 
/*     */   private void readDgaps(IndexInput input) throws IOException
/*     */   {
/* 292 */     this.size = input.readInt();
/* 293 */     this.count = input.readInt();
/* 294 */     this.bits = new byte[(this.size >> 3) + 1];
/* 295 */     int last = 0;
/* 296 */     int n = count();
/* 297 */     while (n > 0) {
/* 298 */       last += input.readVInt();
/* 299 */       this.bits[last] = input.readByte();
/* 300 */       n -= BYTE_COUNTS[(this.bits[last] & 0xFF)];
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 147 */     BYTE_COUNTS = new byte[] { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8 };
/*     */ 
/* 166 */     CODEC = "BitVector";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.BitVector
 * JD-Core Version:    0.6.0
 */