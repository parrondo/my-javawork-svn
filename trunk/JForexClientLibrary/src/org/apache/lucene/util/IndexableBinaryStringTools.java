/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ 
/*     */ public final class IndexableBinaryStringTools
/*     */ {
/*     */   private static final CodingCase[] CODING_CASES;
/*     */ 
/*     */   @Deprecated
/*     */   public static int getEncodedLength(ByteBuffer original)
/*     */     throws IllegalArgumentException
/*     */   {
/*  88 */     if (original.hasArray()) {
/*  89 */       return getEncodedLength(original.array(), original.arrayOffset(), original.limit() - original.arrayOffset());
/*     */     }
/*     */ 
/*  92 */     throw new IllegalArgumentException("original argument must have a backing array");
/*     */   }
/*     */ 
/*     */   public static int getEncodedLength(byte[] inputArray, int inputOffset, int inputLength)
/*     */   {
/* 107 */     return (int)((8L * inputLength + 14L) / 15L) + 1;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static int getDecodedLength(CharBuffer encoded)
/*     */     throws IllegalArgumentException
/*     */   {
/* 124 */     if (encoded.hasArray()) {
/* 125 */       return getDecodedLength(encoded.array(), encoded.arrayOffset(), encoded.limit() - encoded.arrayOffset());
/*     */     }
/*     */ 
/* 128 */     throw new IllegalArgumentException("encoded argument must have a backing array");
/*     */   }
/*     */ 
/*     */   public static int getDecodedLength(char[] encoded, int offset, int length)
/*     */   {
/* 141 */     int numChars = length - 1;
/* 142 */     if (numChars <= 0) {
/* 143 */       return 0;
/*     */     }
/*     */ 
/* 146 */     long numFullBytesInFinalChar = encoded[(offset + length - 1)];
/* 147 */     long numEncodedChars = numChars - 1;
/* 148 */     return (int)((numEncodedChars * 15L + 7L) / 8L + numFullBytesInFinalChar);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void encode(ByteBuffer input, CharBuffer output)
/*     */   {
/* 167 */     if ((input.hasArray()) && (output.hasArray())) {
/* 168 */       int inputOffset = input.arrayOffset();
/* 169 */       int inputLength = input.limit() - inputOffset;
/* 170 */       int outputOffset = output.arrayOffset();
/* 171 */       int outputLength = getEncodedLength(input.array(), inputOffset, inputLength);
/*     */ 
/* 173 */       output.limit(outputLength + outputOffset);
/* 174 */       output.position(0);
/* 175 */       encode(input.array(), inputOffset, inputLength, output.array(), outputOffset, outputLength);
/*     */     }
/*     */     else {
/* 178 */       throw new IllegalArgumentException("Arguments must have backing arrays");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void encode(byte[] inputArray, int inputOffset, int inputLength, char[] outputArray, int outputOffset, int outputLength)
/*     */   {
/* 196 */     assert (outputLength == getEncodedLength(inputArray, inputOffset, inputLength));
/*     */ 
/* 198 */     if (inputLength > 0) {
/* 199 */       int inputByteNum = inputOffset;
/* 200 */       int caseNum = 0;
/* 201 */       int outputCharNum = outputOffset;
/*     */ 
/* 203 */       for (; inputByteNum + CODING_CASES[caseNum].numBytes <= inputLength; outputCharNum++) {
/* 204 */         CodingCase codingCase = CODING_CASES[caseNum];
/* 205 */         if (2 == codingCase.numBytes) {
/* 206 */           outputArray[outputCharNum] = (char)(((inputArray[inputByteNum] & 0xFF) << codingCase.initialShift) + ((inputArray[(inputByteNum + 1)] & 0xFF) >>> codingCase.finalShift & codingCase.finalMask) & 0x7FFF);
/*     */         }
/*     */         else {
/* 209 */           outputArray[outputCharNum] = (char)(((inputArray[inputByteNum] & 0xFF) << codingCase.initialShift) + ((inputArray[(inputByteNum + 1)] & 0xFF) << codingCase.middleShift) + ((inputArray[(inputByteNum + 2)] & 0xFF) >>> codingCase.finalShift & codingCase.finalMask) & 0x7FFF);
/*     */         }
/*     */ 
/* 213 */         inputByteNum += codingCase.advanceBytes;
/* 214 */         caseNum++; if (caseNum == CODING_CASES.length) {
/* 215 */           caseNum = 0;
/*     */         }
/*     */       }
/*     */ 
/* 219 */       CodingCase codingCase = CODING_CASES[caseNum];
/*     */ 
/* 221 */       if (inputByteNum + 1 < inputLength) {
/* 222 */         outputArray[(outputCharNum++)] = (char)(((inputArray[inputByteNum] & 0xFF) << codingCase.initialShift) + ((inputArray[(inputByteNum + 1)] & 0xFF) << codingCase.middleShift) & 0x7FFF);
/*     */ 
/* 224 */         outputArray[(outputCharNum++)] = '\001';
/* 225 */       } else if (inputByteNum < inputLength) {
/* 226 */         outputArray[(outputCharNum++)] = (char)((inputArray[inputByteNum] & 0xFF) << codingCase.initialShift & 0x7FFF);
/*     */ 
/* 228 */         outputArray[(outputCharNum++)] = (caseNum == 0 ? 1 : '\000');
/*     */       }
/*     */       else {
/* 231 */         outputArray[(outputCharNum++)] = '\001';
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void decode(CharBuffer input, ByteBuffer output)
/*     */   {
/* 251 */     if ((input.hasArray()) && (output.hasArray())) {
/* 252 */       int inputOffset = input.arrayOffset();
/* 253 */       int inputLength = input.limit() - inputOffset;
/* 254 */       int outputOffset = output.arrayOffset();
/* 255 */       int outputLength = getDecodedLength(input.array(), inputOffset, inputLength);
/*     */ 
/* 257 */       output.limit(outputLength + outputOffset);
/* 258 */       output.position(0);
/* 259 */       decode(input.array(), inputOffset, inputLength, output.array(), outputOffset, outputLength);
/*     */     }
/*     */     else {
/* 262 */       throw new IllegalArgumentException("Arguments must have backing arrays");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void decode(char[] inputArray, int inputOffset, int inputLength, byte[] outputArray, int outputOffset, int outputLength)
/*     */   {
/* 281 */     assert (outputLength == getDecodedLength(inputArray, inputOffset, inputLength));
/*     */ 
/* 283 */     int numInputChars = inputLength - 1;
/* 284 */     int numOutputBytes = outputLength;
/*     */ 
/* 286 */     if (numOutputBytes > 0) {
/* 287 */       int caseNum = 0;
/* 288 */       int outputByteNum = outputOffset;
/* 289 */       int inputCharNum = inputOffset;
/*     */ 
/* 292 */       for (; inputCharNum < numInputChars - 1; inputCharNum++) {
/* 293 */         CodingCase codingCase = CODING_CASES[caseNum];
/* 294 */         short inputChar = (short)inputArray[inputCharNum];
/* 295 */         if (2 == codingCase.numBytes) {
/* 296 */           if (0 == caseNum) {
/* 297 */             outputArray[outputByteNum] = (byte)(inputChar >>> codingCase.initialShift);
/*     */           }
/*     */           else
/*     */           {
/*     */             int tmp107_105 = outputByteNum;
/*     */             byte[] tmp107_104 = outputArray; tmp107_104[tmp107_105] = (byte)(tmp107_104[tmp107_105] + (byte)(inputChar >>> codingCase.initialShift));
/*     */           }
/* 301 */           outputArray[(outputByteNum + 1)] = (byte)((inputChar & codingCase.finalMask) << codingCase.finalShift);
/*     */         }
/*     */         else
/*     */         {
/*     */           int tmp148_146 = outputByteNum;
/*     */           byte[] tmp148_145 = outputArray; tmp148_145[tmp148_146] = (byte)(tmp148_145[tmp148_146] + (byte)(inputChar >>> codingCase.initialShift));
/* 304 */           outputArray[(outputByteNum + 1)] = (byte)((inputChar & codingCase.middleMask) >>> codingCase.middleShift);
/* 305 */           outputArray[(outputByteNum + 2)] = (byte)((inputChar & codingCase.finalMask) << codingCase.finalShift);
/*     */         }
/* 307 */         outputByteNum += codingCase.advanceBytes;
/* 308 */         caseNum++; if (caseNum == CODING_CASES.length) {
/* 309 */           caseNum = 0;
/*     */         }
/*     */       }
/*     */ 
/* 313 */       short inputChar = (short)inputArray[inputCharNum];
/* 314 */       CodingCase codingCase = CODING_CASES[caseNum];
/* 315 */       if (0 == caseNum)
/* 316 */         outputArray[outputByteNum] = 0;
/*     */       int tmp264_262 = outputByteNum;
/*     */       byte[] tmp264_261 = outputArray; tmp264_261[tmp264_262] = (byte)(tmp264_261[tmp264_262] + (byte)(inputChar >>> codingCase.initialShift));
/* 319 */       int bytesLeft = numOutputBytes - outputByteNum;
/* 320 */       if (bytesLeft > 1)
/* 321 */         if (2 == codingCase.numBytes) {
/* 322 */           outputArray[(outputByteNum + 1)] = (byte)((inputChar & codingCase.finalMask) >>> codingCase.finalShift);
/*     */         } else {
/* 324 */           outputArray[(outputByteNum + 1)] = (byte)((inputChar & codingCase.middleMask) >>> codingCase.middleShift);
/* 325 */           if (bytesLeft > 2)
/* 326 */             outputArray[(outputByteNum + 2)] = (byte)((inputChar & codingCase.finalMask) << codingCase.finalShift);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static ByteBuffer decode(CharBuffer input)
/*     */   {
/* 348 */     byte[] outputArray = new byte[getDecodedLength(input)];
/* 349 */     ByteBuffer output = ByteBuffer.wrap(outputArray);
/* 350 */     decode(input, output);
/* 351 */     return output;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static CharBuffer encode(ByteBuffer input)
/*     */   {
/* 367 */     char[] outputArray = new char[getEncodedLength(input)];
/* 368 */     CharBuffer output = CharBuffer.wrap(outputArray);
/* 369 */     encode(input, output);
/* 370 */     return output;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  58 */     CODING_CASES = new CodingCase[] { new CodingCase(7, 1), new CodingCase(14, 6, 2), new CodingCase(13, 5, 3), new CodingCase(12, 4, 4), new CodingCase(11, 3, 5), new CodingCase(10, 2, 6), new CodingCase(9, 1, 7), new CodingCase(8, 0) };
/*     */   }
/*     */ 
/*     */   static class CodingCase
/*     */   {
/*     */     int numBytes;
/*     */     int initialShift;
/*     */     int middleShift;
/*     */     int finalShift;
/* 374 */     int advanceBytes = 2;
/*     */     short middleMask;
/*     */     short finalMask;
/*     */ 
/*     */     CodingCase(int initialShift, int middleShift, int finalShift)
/*     */     {
/* 378 */       this.numBytes = 3;
/* 379 */       this.initialShift = initialShift;
/* 380 */       this.middleShift = middleShift;
/* 381 */       this.finalShift = finalShift;
/* 382 */       this.finalMask = (short)(255 >>> finalShift);
/* 383 */       this.middleMask = (short)(255 << middleShift);
/*     */     }
/*     */ 
/*     */     CodingCase(int initialShift, int finalShift) {
/* 387 */       this.numBytes = 2;
/* 388 */       this.initialShift = initialShift;
/* 389 */       this.finalShift = finalShift;
/* 390 */       this.finalMask = (short)(255 >>> finalShift);
/* 391 */       if (finalShift != 0)
/* 392 */         this.advanceBytes = 1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.IndexableBinaryStringTools
 * JD-Core Version:    0.6.0
 */