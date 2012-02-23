/*     */ package org.eclipse.jdt.internal.compiler.classfmt;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
/*     */ import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
/*     */ import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class MethodInfo extends ClassFileStruct
/*     */   implements IBinaryMethod, Comparable
/*     */ {
/*  21 */   private static final char[][] noException = CharOperation.NO_CHAR_CHAR;
/*  22 */   private static final char[][] noArgumentNames = CharOperation.NO_CHAR_CHAR;
/*     */   protected int accessFlags;
/*     */   protected int attributeBytes;
/*     */   protected char[] descriptor;
/*     */   protected char[][] exceptionNames;
/*     */   protected char[] name;
/*     */   protected char[] signature;
/*     */   protected int signatureUtf8Offset;
/*     */   protected long tagBits;
/*     */   protected char[][] argumentNames;
/*     */   protected int argumentNamesIndex;
/*     */ 
/*     */   public static MethodInfo createMethod(byte[] classFileBytes, int[] offsets, int offset)
/*     */   {
/*  35 */     MethodInfo methodInfo = new MethodInfo(classFileBytes, offsets, offset);
/*  36 */     int attributesCount = methodInfo.u2At(6);
/*  37 */     int readOffset = 8;
/*  38 */     AnnotationInfo[] annotations = (AnnotationInfo[])null;
/*  39 */     AnnotationInfo[][] parameterAnnotations = (AnnotationInfo[][])null;
/*  40 */     for (int i = 0; i < attributesCount; i++)
/*     */     {
/*  42 */       int utf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset)] - methodInfo.structOffset;
/*  43 */       char[] attributeName = methodInfo.utf8At(utf8Offset + 3, methodInfo.u2At(utf8Offset + 1));
/*  44 */       if (attributeName.length > 0) {
/*  45 */         switch (attributeName[0]) {
/*     */         case 'S':
/*  47 */           if (!CharOperation.equals(AttributeNamesConstants.SignatureName, attributeName)) break;
/*  48 */           methodInfo.signatureUtf8Offset = (methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset + 6)] - methodInfo.structOffset);
/*  49 */           break;
/*     */         case 'R':
/*  51 */           AnnotationInfo[] methodAnnotations = (AnnotationInfo[])null;
/*  52 */           AnnotationInfo[][] paramAnnotations = (AnnotationInfo[][])null;
/*  53 */           if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName))
/*  54 */             methodAnnotations = decodeMethodAnnotations(readOffset, true, methodInfo);
/*  55 */           else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName))
/*  56 */             methodAnnotations = decodeMethodAnnotations(readOffset, false, methodInfo);
/*  57 */           else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleParameterAnnotationsName))
/*  58 */             paramAnnotations = decodeParamAnnotations(readOffset, true, methodInfo);
/*  59 */           else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleParameterAnnotationsName)) {
/*  60 */             paramAnnotations = decodeParamAnnotations(readOffset, false, methodInfo);
/*     */           }
/*  62 */           if (methodAnnotations != null) {
/*  63 */             if (annotations == null) {
/*  64 */               annotations = methodAnnotations;
/*     */             } else {
/*  66 */               int length = annotations.length;
/*  67 */               AnnotationInfo[] newAnnotations = new AnnotationInfo[length + methodAnnotations.length];
/*  68 */               System.arraycopy(annotations, 0, newAnnotations, 0, length);
/*  69 */               System.arraycopy(methodAnnotations, 0, newAnnotations, length, methodAnnotations.length);
/*  70 */               annotations = newAnnotations;
/*     */             }
/*     */           } else {
/*  72 */             if (paramAnnotations == null) break;
/*  73 */             int numberOfParameters = paramAnnotations.length;
/*  74 */             if (parameterAnnotations == null)
/*  75 */               parameterAnnotations = paramAnnotations;
/*     */             else {
/*  77 */               for (int p = 0; p < numberOfParameters; p++) {
/*  78 */                 int numberOfAnnotations = paramAnnotations[p] == null ? 0 : paramAnnotations[p].length;
/*  79 */                 if (numberOfAnnotations > 0) {
/*  80 */                   if (parameterAnnotations[p] == null) {
/*  81 */                     parameterAnnotations[p] = paramAnnotations[p];
/*     */                   } else {
/*  83 */                     int length = parameterAnnotations[p].length;
/*  84 */                     AnnotationInfo[] newAnnotations = new AnnotationInfo[length + numberOfAnnotations];
/*  85 */                     System.arraycopy(parameterAnnotations[p], 0, newAnnotations, 0, length);
/*  86 */                     System.arraycopy(paramAnnotations[p], 0, newAnnotations, length, numberOfAnnotations);
/*  87 */                     parameterAnnotations[p] = newAnnotations;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*  96 */       readOffset = (int)(readOffset + (
/*  96 */         6L + methodInfo.u4At(readOffset + 2)));
/*     */     }
/*  98 */     methodInfo.attributeBytes = readOffset;
/*     */ 
/* 100 */     if (parameterAnnotations != null)
/* 101 */       return new MethodInfoWithParameterAnnotations(methodInfo, annotations, parameterAnnotations);
/* 102 */     if (annotations != null)
/* 103 */       return new MethodInfoWithAnnotations(methodInfo, annotations);
/* 104 */     return methodInfo;
/*     */   }
/*     */   static AnnotationInfo[] decodeAnnotations(int offset, boolean runtimeVisible, int numberOfAnnotations, MethodInfo methodInfo) {
/* 107 */     AnnotationInfo[] result = new AnnotationInfo[numberOfAnnotations];
/* 108 */     int readOffset = offset;
/* 109 */     for (int i = 0; i < numberOfAnnotations; i++) {
/* 110 */       result[i] = 
/* 111 */         new AnnotationInfo(methodInfo.reference, methodInfo.constantPoolOffsets, 
/* 111 */         readOffset + methodInfo.structOffset, runtimeVisible, false);
/* 112 */       readOffset += result[i].readOffset;
/*     */     }
/* 114 */     return result;
/*     */   }
/*     */   static AnnotationInfo[] decodeMethodAnnotations(int offset, boolean runtimeVisible, MethodInfo methodInfo) {
/* 117 */     int numberOfAnnotations = methodInfo.u2At(offset + 6);
/* 118 */     if (numberOfAnnotations > 0) {
/* 119 */       AnnotationInfo[] annos = decodeAnnotations(offset + 8, runtimeVisible, numberOfAnnotations, methodInfo);
/* 120 */       if (runtimeVisible) {
/* 121 */         int numStandardAnnotations = 0;
/* 122 */         for (int i = 0; i < numberOfAnnotations; i++) {
/* 123 */           long standardAnnoTagBits = annos[i].standardAnnotationTagBits;
/* 124 */           methodInfo.tagBits |= standardAnnoTagBits;
/* 125 */           if (standardAnnoTagBits != 0L) {
/* 126 */             annos[i] = null;
/* 127 */             numStandardAnnotations++;
/*     */           }
/*     */         }
/*     */ 
/* 131 */         if (numStandardAnnotations != 0) {
/* 132 */           if (numStandardAnnotations == numberOfAnnotations) {
/* 133 */             return null;
/*     */           }
/*     */ 
/* 136 */           AnnotationInfo[] temp = new AnnotationInfo[numberOfAnnotations - numStandardAnnotations];
/* 137 */           int tmpIndex = 0;
/* 138 */           for (int i = 0; i < numberOfAnnotations; i++)
/* 139 */             if (annos[i] != null)
/* 140 */               temp[(tmpIndex++)] = annos[i];
/* 141 */           annos = temp;
/*     */         }
/*     */       }
/* 144 */       return annos;
/*     */     }
/* 146 */     return null;
/*     */   }
/*     */   static AnnotationInfo[][] decodeParamAnnotations(int offset, boolean runtimeVisible, MethodInfo methodInfo) {
/* 149 */     AnnotationInfo[][] allParamAnnotations = (AnnotationInfo[][])null;
/* 150 */     int numberOfParameters = methodInfo.u1At(offset + 6);
/* 151 */     if (numberOfParameters > 0)
/*     */     {
/* 153 */       int readOffset = offset + 7;
/* 154 */       for (int i = 0; i < numberOfParameters; i++) {
/* 155 */         int numberOfAnnotations = methodInfo.u2At(readOffset);
/* 156 */         readOffset += 2;
/* 157 */         if (numberOfAnnotations > 0) {
/* 158 */           if (allParamAnnotations == null)
/* 159 */             allParamAnnotations = new AnnotationInfo[numberOfParameters][];
/* 160 */           AnnotationInfo[] annos = decodeAnnotations(readOffset, runtimeVisible, numberOfAnnotations, methodInfo);
/* 161 */           allParamAnnotations[i] = annos;
/* 162 */           for (int aIndex = 0; aIndex < annos.length; aIndex++)
/* 163 */             readOffset += annos[aIndex].readOffset;
/*     */         }
/*     */       }
/*     */     }
/* 167 */     return allParamAnnotations;
/*     */   }
/*     */ 
/*     */   protected MethodInfo(byte[] classFileBytes, int[] offsets, int offset)
/*     */   {
/* 176 */     super(classFileBytes, offsets, offset);
/* 177 */     this.accessFlags = -1;
/* 178 */     this.signatureUtf8Offset = -1;
/*     */   }
/*     */   public int compareTo(Object o) {
/* 181 */     MethodInfo otherMethod = (MethodInfo)o;
/* 182 */     int result = new String(getSelector()).compareTo(new String(otherMethod.getSelector()));
/* 183 */     if (result != 0) return result;
/* 184 */     return new String(getMethodDescriptor()).compareTo(new String(otherMethod.getMethodDescriptor()));
/*     */   }
/*     */   public boolean equals(Object o) {
/* 187 */     if (!(o instanceof MethodInfo)) {
/* 188 */       return false;
/*     */     }
/* 190 */     MethodInfo otherMethod = (MethodInfo)o;
/*     */ 
/* 192 */     return (CharOperation.equals(getSelector(), otherMethod.getSelector())) && 
/* 192 */       (CharOperation.equals(getMethodDescriptor(), otherMethod.getMethodDescriptor()));
/*     */   }
/*     */   public int hashCode() {
/* 195 */     return CharOperation.hashCode(getSelector()) + CharOperation.hashCode(getMethodDescriptor());
/*     */   }
/*     */ 
/*     */   public IBinaryAnnotation[] getAnnotations()
/*     */   {
/* 201 */     return null;
/*     */   }
/*     */ 
/*     */   public char[][] getArgumentNames()
/*     */   {
/* 207 */     if (this.argumentNames == null) {
/* 208 */       readCodeAttribute();
/*     */     }
/* 210 */     return this.argumentNames;
/*     */   }
/*     */   public Object getDefaultValue() {
/* 213 */     return null;
/*     */   }
/*     */ 
/*     */   public char[][] getExceptionTypeNames()
/*     */   {
/* 224 */     if (this.exceptionNames == null) {
/* 225 */       readExceptionAttributes();
/*     */     }
/* 227 */     return this.exceptionNames;
/*     */   }
/*     */   public char[] getGenericSignature() {
/* 230 */     if (this.signatureUtf8Offset != -1) {
/* 231 */       if (this.signature == null)
/*     */       {
/* 233 */         this.signature = utf8At(this.signatureUtf8Offset + 3, u2At(this.signatureUtf8Offset + 1));
/*     */       }
/* 235 */       return this.signature;
/*     */     }
/* 237 */     return null;
/*     */   }
/*     */ 
/*     */   public char[] getMethodDescriptor()
/*     */   {
/* 249 */     if (this.descriptor == null)
/*     */     {
/* 251 */       int utf8Offset = this.constantPoolOffsets[u2At(4)] - this.structOffset;
/* 252 */       this.descriptor = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*     */     }
/* 254 */     return this.descriptor;
/*     */   }
/*     */ 
/*     */   public int getModifiers()
/*     */   {
/* 263 */     if (this.accessFlags == -1)
/*     */     {
/* 265 */       this.accessFlags = u2At(0);
/* 266 */       readModifierRelatedAttributes();
/*     */     }
/* 268 */     return this.accessFlags;
/*     */   }
/*     */   public IBinaryAnnotation[] getParameterAnnotations(int index) {
/* 271 */     return null;
/*     */   }
/*     */ 
/*     */   public char[] getSelector()
/*     */   {
/* 280 */     if (this.name == null)
/*     */     {
/* 282 */       int utf8Offset = this.constantPoolOffsets[u2At(2)] - this.structOffset;
/* 283 */       this.name = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*     */     }
/* 285 */     return this.name;
/*     */   }
/*     */   public long getTagBits() {
/* 288 */     return this.tagBits;
/*     */   }
/*     */ 
/*     */   protected void initialize()
/*     */   {
/* 295 */     getModifiers();
/* 296 */     getSelector();
/* 297 */     getMethodDescriptor();
/* 298 */     getExceptionTypeNames();
/* 299 */     getGenericSignature();
/* 300 */     getArgumentNames();
/* 301 */     reset();
/*     */   }
/*     */ 
/*     */   public boolean isClinit()
/*     */   {
/* 308 */     char[] selector = getSelector();
/* 309 */     return (selector[0] == '<') && (selector.length == 8);
/*     */   }
/*     */ 
/*     */   public boolean isConstructor()
/*     */   {
/* 316 */     char[] selector = getSelector();
/* 317 */     return (selector[0] == '<') && (selector.length == 6);
/*     */   }
/*     */ 
/*     */   public boolean isSynthetic()
/*     */   {
/* 324 */     return (getModifiers() & 0x1000) != 0;
/*     */   }
/*     */   private void readExceptionAttributes() {
/* 327 */     int attributesCount = u2At(6);
/* 328 */     int readOffset = 8;
/* 329 */     for (int i = 0; i < attributesCount; i++) {
/* 330 */       int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
/* 331 */       char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 332 */       if (CharOperation.equals(attributeName, AttributeNamesConstants.ExceptionsName))
/*     */       {
/* 334 */         int entriesNumber = u2At(readOffset + 6);
/*     */ 
/* 336 */         readOffset += 8;
/* 337 */         if (entriesNumber == 0) {
/* 338 */           this.exceptionNames = noException;
/*     */         } else {
/* 340 */           this.exceptionNames = new char[entriesNumber][];
/* 341 */           for (int j = 0; j < entriesNumber; j++) {
/* 342 */             utf8Offset = 
/* 343 */               this.constantPoolOffsets[u2At(
/* 344 */               this.constantPoolOffsets[u2At(readOffset)] - this.structOffset + 1)] - 
/* 345 */               this.structOffset;
/* 346 */             this.exceptionNames[j] = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 347 */             readOffset += 2;
/*     */           }
/*     */         }
/*     */       } else {
/* 351 */         readOffset = (int)(readOffset + (6L + u4At(readOffset + 2)));
/*     */       }
/*     */     }
/* 354 */     if (this.exceptionNames == null)
/* 355 */       this.exceptionNames = noException;
/*     */   }
/*     */ 
/*     */   private void readModifierRelatedAttributes() {
/* 359 */     int attributesCount = u2At(6);
/* 360 */     int readOffset = 8;
/* 361 */     for (int i = 0; i < attributesCount; i++) {
/* 362 */       int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
/* 363 */       char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*     */ 
/* 365 */       if (attributeName.length != 0) {
/* 366 */         switch (attributeName[0]) {
/*     */         case 'D':
/* 368 */           if (!CharOperation.equals(attributeName, AttributeNamesConstants.DeprecatedName)) break;
/* 369 */           this.accessFlags |= 1048576;
/* 370 */           break;
/*     */         case 'S':
/* 372 */           if (!CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName)) break;
/* 373 */           this.accessFlags |= 4096;
/* 374 */           break;
/*     */         case 'A':
/* 376 */           if (!CharOperation.equals(attributeName, AttributeNamesConstants.AnnotationDefaultName)) break;
/* 377 */           this.accessFlags |= 131072;
/* 378 */           break;
/*     */         case 'V':
/* 380 */           if (!CharOperation.equals(attributeName, AttributeNamesConstants.VarargsName)) break;
/* 381 */           this.accessFlags |= 128;
/*     */         }
/*     */       }
/* 384 */       readOffset = (int)(readOffset + (
/* 384 */         6L + u4At(readOffset + 2)));
/*     */     }
/*     */   }
/*     */ 
/*     */   public int sizeInBytes()
/*     */   {
/* 393 */     return this.attributeBytes;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 397 */     StringBuffer buffer = new StringBuffer();
/* 398 */     toString(buffer);
/* 399 */     return buffer.toString();
/*     */   }
/*     */   void toString(StringBuffer buffer) {
/* 402 */     buffer.append(getClass().getName());
/* 403 */     toStringContent(buffer);
/*     */   }
/*     */   protected void toStringContent(StringBuffer buffer) {
/* 406 */     int modifiers = getModifiers();
/* 407 */     char[] desc = getGenericSignature();
/* 408 */     if (desc == null)
/* 409 */       desc = getMethodDescriptor();
/* 410 */     buffer
/* 411 */       .append('{')
/* 412 */       .append(
/* 413 */       ((modifiers & 0x100000) != 0 ? "deprecated " : Util.EMPTY_STRING) + (
/* 414 */       (modifiers & 0x1) == 1 ? "public " : Util.EMPTY_STRING) + (
/* 415 */       (modifiers & 0x2) == 2 ? "private " : Util.EMPTY_STRING) + (
/* 416 */       (modifiers & 0x4) == 4 ? "protected " : Util.EMPTY_STRING) + (
/* 417 */       (modifiers & 0x8) == 8 ? "static " : Util.EMPTY_STRING) + (
/* 418 */       (modifiers & 0x10) == 16 ? "final " : Util.EMPTY_STRING) + (
/* 419 */       (modifiers & 0x40) == 64 ? "bridge " : Util.EMPTY_STRING) + (
/* 420 */       (modifiers & 0x80) == 128 ? "varargs " : Util.EMPTY_STRING))
/* 421 */       .append(getSelector())
/* 422 */       .append(desc)
/* 423 */       .append('}');
/*     */   }
/*     */   private void readCodeAttribute() {
/* 426 */     int attributesCount = u2At(6);
/* 427 */     int readOffset = 8;
/* 428 */     if (attributesCount != 0) {
/* 429 */       for (int i = 0; i < attributesCount; i++) {
/* 430 */         int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
/* 431 */         char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 432 */         if (CharOperation.equals(attributeName, AttributeNamesConstants.CodeName)) {
/* 433 */           decodeCodeAttribute(readOffset);
/* 434 */           if (this.argumentNames == null) {
/* 435 */             this.argumentNames = noArgumentNames;
/*     */           }
/* 437 */           return;
/*     */         }
/* 439 */         readOffset = (int)(readOffset + (6L + u4At(readOffset + 2)));
/*     */       }
/*     */     }
/*     */ 
/* 443 */     this.argumentNames = noArgumentNames;
/*     */   }
/*     */   private void decodeCodeAttribute(int offset) {
/* 446 */     int readOffset = offset + 10;
/* 447 */     int codeLength = (int)u4At(readOffset);
/* 448 */     readOffset += 4 + codeLength;
/* 449 */     int exceptionTableLength = u2At(readOffset);
/* 450 */     readOffset += 2;
/* 451 */     if (exceptionTableLength != 0) {
/* 452 */       for (int i = 0; i < exceptionTableLength; i++) {
/* 453 */         readOffset += 8;
/*     */       }
/*     */     }
/* 456 */     int attributesCount = u2At(readOffset);
/* 457 */     readOffset += 2;
/* 458 */     for (int i = 0; i < attributesCount; i++) {
/* 459 */       int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
/* 460 */       char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 461 */       if (CharOperation.equals(attributeName, AttributeNamesConstants.LocalVariableTableName)) {
/* 462 */         decodeLocalVariableAttribute(readOffset, codeLength);
/*     */       }
/* 464 */       readOffset = (int)(readOffset + (6L + u4At(readOffset + 2)));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void decodeLocalVariableAttribute(int offset, int codeLength) {
/* 468 */     int readOffset = offset + 6;
/* 469 */     int length = u2At(readOffset);
/* 470 */     if (length != 0) {
/* 471 */       readOffset += 2;
/* 472 */       this.argumentNames = new char[length][];
/* 473 */       this.argumentNamesIndex = 0;
/* 474 */       for (int i = 0; i < length; i++) {
/* 475 */         int startPC = u2At(readOffset);
/* 476 */         if (startPC != 0) break;
/* 477 */         int nameIndex = u2At(4 + readOffset);
/* 478 */         int utf8Offset = this.constantPoolOffsets[nameIndex] - this.structOffset;
/* 479 */         char[] localVariableName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 480 */         if (!CharOperation.equals(localVariableName, ConstantPool.This)) {
/* 481 */           this.argumentNames[(this.argumentNamesIndex++)] = localVariableName;
/*     */         }
/*     */ 
/* 486 */         readOffset += 10;
/*     */       }
/* 488 */       if (this.argumentNamesIndex != this.argumentNames.length)
/*     */       {
/* 490 */         System.arraycopy(this.argumentNames, 0, this.argumentNames = new char[this.argumentNamesIndex][], 0, this.argumentNamesIndex);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.MethodInfo
 * JD-Core Version:    0.6.0
 */