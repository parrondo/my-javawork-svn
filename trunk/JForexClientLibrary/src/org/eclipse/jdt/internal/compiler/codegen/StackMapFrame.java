/*     */ package org.eclipse.jdt.internal.compiler.codegen;
/*     */ 
/*     */ import java.text.MessageFormat;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ 
/*     */ public class StackMapFrame
/*     */ {
/*     */   public static final int USED = 1;
/*     */   public static final int SAME_FRAME = 0;
/*     */   public static final int CHOP_FRAME = 1;
/*     */   public static final int APPEND_FRAME = 2;
/*     */   public static final int SAME_FRAME_EXTENDED = 3;
/*     */   public static final int FULL_FRAME = 4;
/*     */   public static final int SAME_LOCALS_1_STACK_ITEMS = 5;
/*     */   public static final int SAME_LOCALS_1_STACK_ITEMS_EXTENDED = 6;
/*     */   public int pc;
/*     */   public int numberOfStackItems;
/*     */   private int numberOfLocals;
/*     */   public int localIndex;
/*     */   public VerificationTypeInfo[] locals;
/*     */   public VerificationTypeInfo[] stackItems;
/*  34 */   private int numberOfDifferentLocals = -1;
/*     */   public int tagBits;
/*     */ 
/*     */   public StackMapFrame(int initialLocalSize)
/*     */   {
/*  38 */     this.locals = new VerificationTypeInfo[initialLocalSize];
/*  39 */     this.numberOfLocals = -1;
/*  40 */     this.numberOfDifferentLocals = -1;
/*     */   }
/*     */   public int getFrameType(StackMapFrame prevFrame) {
/*  43 */     int offsetDelta = getOffsetDelta(prevFrame);
/*  44 */     switch (this.numberOfStackItems) {
/*     */     case 0:
/*  46 */       switch (numberOfDifferentLocals(prevFrame)) {
/*     */       case 0:
/*  48 */         return offsetDelta <= 63 ? 0 : 3;
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*  52 */         return 2;
/*     */       case -3:
/*     */       case -2:
/*     */       case -1:
/*  56 */         return 1;
/*     */       }
/*  58 */       break;
/*     */     case 1:
/*  60 */       switch (numberOfDifferentLocals(prevFrame)) {
/*     */       case 0:
/*  62 */         return offsetDelta <= 63 ? 5 : 6;
/*     */       }
/*     */     }
/*  65 */     return 4;
/*     */   }
/*     */   public void addLocal(int resolvedPosition, VerificationTypeInfo info) {
/*  68 */     if (this.locals == null) {
/*  69 */       this.locals = new VerificationTypeInfo[resolvedPosition + 1];
/*  70 */       this.locals[resolvedPosition] = info;
/*     */     } else {
/*  72 */       int length = this.locals.length;
/*  73 */       if (resolvedPosition >= length) {
/*  74 */         System.arraycopy(this.locals, 0, this.locals = new VerificationTypeInfo[resolvedPosition + 1], 0, length);
/*     */       }
/*  76 */       this.locals[resolvedPosition] = info;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addStackItem(VerificationTypeInfo info) {
/*  80 */     if (info == null) {
/*  81 */       throw new IllegalArgumentException("info cannot be null");
/*     */     }
/*  83 */     if (this.stackItems == null) {
/*  84 */       this.stackItems = new VerificationTypeInfo[1];
/*  85 */       this.stackItems[0] = info;
/*  86 */       this.numberOfStackItems = 1;
/*     */     } else {
/*  88 */       int length = this.stackItems.length;
/*  89 */       if (this.numberOfStackItems == length) {
/*  90 */         System.arraycopy(this.stackItems, 0, this.stackItems = new VerificationTypeInfo[length + 1], 0, length);
/*     */       }
/*  92 */       this.stackItems[(this.numberOfStackItems++)] = info;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addStackItem(TypeBinding binding) {
/*  96 */     if (this.stackItems == null) {
/*  97 */       this.stackItems = new VerificationTypeInfo[1];
/*  98 */       this.stackItems[0] = new VerificationTypeInfo(binding);
/*  99 */       this.numberOfStackItems = 1;
/*     */     } else {
/* 101 */       int length = this.stackItems.length;
/* 102 */       if (this.numberOfStackItems == length) {
/* 103 */         System.arraycopy(this.stackItems, 0, this.stackItems = new VerificationTypeInfo[length + 1], 0, length);
/*     */       }
/* 105 */       this.stackItems[(this.numberOfStackItems++)] = new VerificationTypeInfo(binding);
/*     */     }
/*     */   }
/*     */ 
/*     */   public StackMapFrame duplicate() {
/* 109 */     int length = this.locals.length;
/* 110 */     StackMapFrame result = new StackMapFrame(length);
/* 111 */     result.numberOfLocals = -1;
/* 112 */     result.numberOfDifferentLocals = -1;
/* 113 */     result.pc = this.pc;
/* 114 */     result.numberOfStackItems = this.numberOfStackItems;
/*     */ 
/* 116 */     if (length != 0) {
/* 117 */       result.locals = new VerificationTypeInfo[length];
/* 118 */       for (int i = 0; i < length; i++) {
/* 119 */         VerificationTypeInfo verificationTypeInfo = this.locals[i];
/* 120 */         if (verificationTypeInfo != null) {
/* 121 */           result.locals[i] = verificationTypeInfo.duplicate();
/*     */         }
/*     */       }
/*     */     }
/* 125 */     length = this.numberOfStackItems;
/* 126 */     if (length != 0) {
/* 127 */       result.stackItems = new VerificationTypeInfo[length];
/* 128 */       for (int i = 0; i < length; i++) {
/* 129 */         result.stackItems[i] = this.stackItems[i].duplicate();
/*     */       }
/*     */     }
/* 132 */     return result;
/*     */   }
/*     */   public int numberOfDifferentLocals(StackMapFrame prevFrame) {
/* 135 */     if (this.numberOfDifferentLocals != -1) return this.numberOfDifferentLocals;
/* 136 */     if (prevFrame == null) {
/* 137 */       this.numberOfDifferentLocals = 0;
/* 138 */       return 0;
/*     */     }
/* 140 */     VerificationTypeInfo[] prevLocals = prevFrame.locals;
/* 141 */     VerificationTypeInfo[] currentLocals = this.locals;
/* 142 */     int prevLocalsLength = prevLocals == null ? 0 : prevLocals.length;
/* 143 */     int currentLocalsLength = currentLocals == null ? 0 : currentLocals.length;
/* 144 */     int prevNumberOfLocals = prevFrame.getNumberOfLocals();
/* 145 */     int currentNumberOfLocals = getNumberOfLocals();
/*     */ 
/* 147 */     int result = 0;
/* 148 */     if (prevNumberOfLocals == 0) {
/* 149 */       if (currentNumberOfLocals != 0)
/*     */       {
/* 151 */         result = currentNumberOfLocals;
/* 152 */         int counter = 0;
/* 153 */         int i = 0;
/*     */         do { if (currentLocals[i] != null) {
/* 155 */             switch (currentLocals[i].id()) {
/*     */             case 7:
/*     */             case 8:
/* 158 */               i++;
/*     */             }
/* 160 */             counter++;
/*     */           } else {
/* 162 */             result = 2147483647;
/* 163 */             this.numberOfDifferentLocals = result;
/* 164 */             return result;
/*     */           }
/* 153 */           i++; if (i >= currentLocalsLength) break;  }
/* 153 */         while (counter < currentNumberOfLocals);
/*     */       }
/*     */ 
/*     */     }
/* 168 */     else if (currentNumberOfLocals == 0)
/*     */     {
/* 170 */       int counter = 0;
/* 171 */       result = -prevNumberOfLocals;
/* 172 */       int i = 0;
/*     */       do { if (prevLocals[i] != null) {
/* 174 */           switch (prevLocals[i].id()) {
/*     */           case 7:
/*     */           case 8:
/* 177 */             i++;
/*     */           }
/* 179 */           counter++;
/*     */         } else {
/* 181 */           result = 2147483647;
/* 182 */           this.numberOfDifferentLocals = result;
/* 183 */           return result;
/*     */         }
/* 172 */         i++; if (i >= prevLocalsLength) break;  }
/* 172 */       while (counter < prevNumberOfLocals);
/*     */     }
/*     */     else
/*     */     {
/* 188 */       int indexInPrevLocals = 0;
/* 189 */       int indexInCurrentLocals = 0;
/* 190 */       int currentLocalsCounter = 0;
/* 191 */       int prevLocalsCounter = 0;
/* 192 */       for (; (indexInCurrentLocals < currentLocalsLength) && (currentLocalsCounter < currentNumberOfLocals); indexInCurrentLocals++) {
/* 193 */         VerificationTypeInfo currentLocal = currentLocals[indexInCurrentLocals];
/* 194 */         if (currentLocal != null) {
/* 195 */           currentLocalsCounter++;
/* 196 */           switch (currentLocal.id()) {
/*     */           case 7:
/*     */           case 8:
/* 199 */             indexInCurrentLocals++;
/*     */           }
/*     */         }
/* 202 */         if ((indexInPrevLocals < prevLocalsLength) && (prevLocalsCounter < prevNumberOfLocals)) {
/* 203 */           VerificationTypeInfo prevLocal = prevLocals[indexInPrevLocals];
/* 204 */           if (prevLocal != null) {
/* 205 */             prevLocalsCounter++;
/* 206 */             switch (prevLocal.id()) {
/*     */             case 7:
/*     */             case 8:
/* 209 */               indexInPrevLocals++;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 214 */           if ((equals(prevLocal, currentLocal)) && (indexInPrevLocals == indexInCurrentLocals)) {
/* 215 */             if (result != 0) {
/* 216 */               result = 2147483647;
/* 217 */               this.numberOfDifferentLocals = result;
/* 218 */               return result;
/*     */             }
/*     */           }
/*     */           else {
/* 222 */             result = 2147483647;
/* 223 */             this.numberOfDifferentLocals = result;
/* 224 */             return result;
/*     */           }
/* 226 */           indexInPrevLocals++;
/*     */         }
/*     */         else
/*     */         {
/* 230 */           if (currentLocal != null) {
/* 231 */             result++;
/*     */           } else {
/* 233 */             result = 2147483647;
/* 234 */             this.numberOfDifferentLocals = result;
/* 235 */             return result;
/*     */           }
/* 237 */           indexInCurrentLocals++;
/* 238 */           break;
/*     */         }
/*     */       }
/* 240 */       if (currentLocalsCounter < currentNumberOfLocals) {
/*     */         do {
/* 242 */           VerificationTypeInfo currentLocal = currentLocals[indexInCurrentLocals];
/* 243 */           if (currentLocal == null) {
/* 244 */             result = 2147483647;
/* 245 */             this.numberOfDifferentLocals = result;
/* 246 */             return result;
/*     */           }
/* 248 */           result++;
/* 249 */           currentLocalsCounter++;
/* 250 */           switch (currentLocal.id()) {
/*     */           case 7:
/*     */           case 8:
/* 253 */             indexInCurrentLocals++;
/*     */           }
/* 241 */           indexInCurrentLocals++; if (indexInCurrentLocals >= currentLocalsLength) break; 
/* 241 */         }while (currentLocalsCounter < currentNumberOfLocals);
/*     */       }
/* 256 */       else if (prevLocalsCounter < prevNumberOfLocals) {
/* 257 */         result = -result;
/*     */ 
/* 259 */         for (; (indexInPrevLocals < prevLocalsLength) && (prevLocalsCounter < prevNumberOfLocals); indexInPrevLocals++) {
/* 260 */           VerificationTypeInfo prevLocal = prevLocals[indexInPrevLocals];
/* 261 */           if (prevLocal == null) {
/* 262 */             result = 2147483647;
/* 263 */             this.numberOfDifferentLocals = result;
/* 264 */             return result;
/*     */           }
/* 266 */           result--;
/* 267 */           prevLocalsCounter++;
/* 268 */           switch (prevLocal.id()) {
/*     */           case 7:
/*     */           case 8:
/* 271 */             indexInPrevLocals++;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 276 */     this.numberOfDifferentLocals = result;
/* 277 */     return result;
/*     */   }
/*     */   public int getNumberOfLocals() {
/* 280 */     if (this.numberOfLocals != -1) {
/* 281 */       return this.numberOfLocals;
/*     */     }
/* 283 */     int result = 0;
/* 284 */     int length = this.locals == null ? 0 : this.locals.length;
/* 285 */     for (int i = 0; i < length; i++) {
/* 286 */       if (this.locals[i] != null) {
/* 287 */         switch (this.locals[i].id()) {
/*     */         case 7:
/*     */         case 8:
/* 290 */           i++;
/*     */         }
/* 292 */         result++;
/*     */       }
/*     */     }
/* 295 */     this.numberOfLocals = result;
/* 296 */     return result;
/*     */   }
/*     */   public int getOffsetDelta(StackMapFrame prevFrame) {
/* 299 */     if (prevFrame == null) return this.pc;
/* 300 */     return prevFrame.pc == -1 ? this.pc : this.pc - prevFrame.pc - 1;
/*     */   }
/*     */   public String toString() {
/* 303 */     StringBuffer buffer = new StringBuffer();
/* 304 */     printFrame(buffer, this);
/* 305 */     return String.valueOf(buffer);
/*     */   }
/*     */   private void printFrame(StringBuffer buffer, StackMapFrame frame) {
/* 308 */     String pattern = "[pc : {0} locals: {1} stack items: {2}\nlocals: {3}\nstack: {4}\n]";
/* 309 */     int localsLength = frame.locals == null ? 0 : frame.locals.length;
/* 310 */     buffer.append(MessageFormat.format(
/* 311 */       pattern, 
/* 312 */       new String[] { 
/* 313 */       Integer.toString(frame.pc), 
/* 314 */       Integer.toString(frame.getNumberOfLocals()), 
/* 315 */       Integer.toString(frame.numberOfStackItems), 
/* 316 */       print(frame.locals, localsLength), 
/* 317 */       print(frame.stackItems, frame.numberOfStackItems) }));
/*     */   }
/*     */ 
/*     */   private String print(VerificationTypeInfo[] infos, int length)
/*     */   {
/* 322 */     StringBuffer buffer = new StringBuffer();
/* 323 */     buffer.append('[');
/* 324 */     if (infos != null) {
/* 325 */       for (int i = 0; i < length; i++) {
/* 326 */         if (i != 0) buffer.append(',');
/* 327 */         VerificationTypeInfo verificationTypeInfo = infos[i];
/* 328 */         if (verificationTypeInfo == null) {
/* 329 */           buffer.append("top");
/*     */         }
/*     */         else
/* 332 */           buffer.append(verificationTypeInfo);
/*     */       }
/*     */     }
/* 335 */     buffer.append(']');
/* 336 */     return String.valueOf(buffer);
/*     */   }
/*     */   public void putLocal(int resolvedPosition, VerificationTypeInfo info) {
/* 339 */     if (this.locals == null) {
/* 340 */       this.locals = new VerificationTypeInfo[resolvedPosition + 1];
/* 341 */       this.locals[resolvedPosition] = info;
/*     */     } else {
/* 343 */       int length = this.locals.length;
/* 344 */       if (resolvedPosition >= length) {
/* 345 */         System.arraycopy(this.locals, 0, this.locals = new VerificationTypeInfo[resolvedPosition + 1], 0, length);
/*     */       }
/* 347 */       this.locals[resolvedPosition] = info;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void replaceWithElementType() {
/* 351 */     VerificationTypeInfo info = this.stackItems[(this.numberOfStackItems - 1)];
/* 352 */     VerificationTypeInfo info2 = info.duplicate();
/* 353 */     info2.replaceWithElementType();
/* 354 */     this.stackItems[(this.numberOfStackItems - 1)] = info2;
/*     */   }
/*     */   public int getIndexOfDifferentLocals(int differentLocalsCount) {
/* 357 */     for (int i = this.locals.length - 1; i >= 0; i--) {
/* 358 */       VerificationTypeInfo currentLocal = this.locals[i];
/* 359 */       if (currentLocal == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 363 */       differentLocalsCount--;
/*     */ 
/* 365 */       if (differentLocalsCount == 0) {
/* 366 */         return i;
/*     */       }
/*     */     }
/* 369 */     return 0;
/*     */   }
/*     */   private boolean equals(VerificationTypeInfo info, VerificationTypeInfo info2) {
/* 372 */     if (info == null) {
/* 373 */       return info2 == null;
/*     */     }
/* 375 */     if (info2 == null) return false;
/* 376 */     return info.equals(info2);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.StackMapFrame
 * JD-Core Version:    0.6.0
 */