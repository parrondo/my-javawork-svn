/*    */ package org.eclipse.jdt.internal.compiler.classfmt;
/*    */ 
/*    */ public abstract class ClassFileStruct
/*    */ {
/*    */   byte[] reference;
/*    */   int[] constantPoolOffsets;
/*    */   int structOffset;
/*    */ 
/*    */   public ClassFileStruct(byte[] classFileBytes, int[] offsets, int offset)
/*    */   {
/* 18 */     this.reference = classFileBytes;
/* 19 */     this.constantPoolOffsets = offsets;
/* 20 */     this.structOffset = offset;
/*    */   }
/*    */   public double doubleAt(int relativeOffset) {
/* 23 */     return Double.longBitsToDouble(i8At(relativeOffset));
/*    */   }
/*    */   public float floatAt(int relativeOffset) {
/* 26 */     return Float.intBitsToFloat(i4At(relativeOffset));
/*    */   }
/*    */   public int i4At(int relativeOffset) {
/* 29 */     int position = relativeOffset + this.structOffset;
/* 30 */     return (this.reference[(position++)] & 0xFF) << 24 | (this.reference[(position++)] & 0xFF) << 16 | ((this.reference[(position++)] & 0xFF) << 8) + (this.reference[position] & 0xFF);
/*    */   }
/*    */   public long i8At(int relativeOffset) {
/* 33 */     int position = relativeOffset + this.structOffset;
/* 34 */     return (this.reference[(position++)] & 0xFF) << 56 | 
/* 35 */       (this.reference[(position++)] & 0xFF) << 48 | 
/* 36 */       (this.reference[(position++)] & 0xFF) << 40 | 
/* 37 */       (this.reference[(position++)] & 0xFF) << 32 | 
/* 38 */       (this.reference[(position++)] & 0xFF) << 24 | 
/* 39 */       (this.reference[(position++)] & 0xFF) << 16 | 
/* 40 */       (this.reference[(position++)] & 0xFF) << 8 | 
/* 41 */       this.reference[(position++)] & 0xFF;
/*    */   }
/*    */   protected void reset() {
/* 44 */     this.reference = null;
/* 45 */     this.constantPoolOffsets = null;
/*    */   }
/*    */   public int u1At(int relativeOffset) {
/* 48 */     return this.reference[(relativeOffset + this.structOffset)] & 0xFF;
/*    */   }
/*    */   public int u2At(int relativeOffset) {
/* 51 */     int position = relativeOffset + this.structOffset;
/* 52 */     return (this.reference[(position++)] & 0xFF) << 8 | this.reference[position] & 0xFF;
/*    */   }
/*    */   public long u4At(int relativeOffset) {
/* 55 */     int position = relativeOffset + this.structOffset;
/* 56 */     return (this.reference[(position++)] & 0xFF) << 24 | (this.reference[(position++)] & 0xFF) << 16 | (this.reference[(position++)] & 0xFF) << 8 | this.reference[position] & 0xFF;
/*    */   }
/*    */   public char[] utf8At(int relativeOffset, int bytesAvailable) {
/* 59 */     int length = bytesAvailable;
/* 60 */     char[] outputBuf = new char[bytesAvailable];
/* 61 */     int outputPos = 0;
/* 62 */     int readOffset = this.structOffset + relativeOffset;
/*    */ 
/* 64 */     while (length != 0) {
/* 65 */       int x = this.reference[(readOffset++)] & 0xFF;
/* 66 */       length--;
/* 67 */       if ((0x80 & x) != 0) {
/* 68 */         if ((x & 0x20) != 0) {
/* 69 */           length -= 2;
/* 70 */           x = (x & 0xF) << 12 | (this.reference[(readOffset++)] & 0x3F) << 6 | this.reference[(readOffset++)] & 0x3F;
/*    */         } else {
/* 72 */           length--;
/* 73 */           x = (x & 0x1F) << 6 | this.reference[(readOffset++)] & 0x3F;
/*    */         }
/*    */       }
/* 76 */       outputBuf[(outputPos++)] = (char)x;
/*    */     }
/*    */ 
/* 79 */     if (outputPos != bytesAvailable) {
/* 80 */       System.arraycopy(outputBuf, 0, outputBuf = new char[outputPos], 0, outputPos);
/*    */     }
/* 82 */     return outputBuf;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.ClassFileStruct
 * JD-Core Version:    0.6.0
 */