/*     */ package org.eclipse.jdt.internal.compiler.classfmt;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
/*     */ import org.eclipse.jdt.internal.compiler.env.ClassSignature;
/*     */ import org.eclipse.jdt.internal.compiler.env.EnumConstantSignature;
/*     */ import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
/*     */ import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
/*     */ import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ByteConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CharConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.IntConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.LongConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ShortConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.StringConstant;
/*     */ 
/*     */ public class AnnotationInfo extends ClassFileStruct
/*     */   implements IBinaryAnnotation
/*     */ {
/*     */   private char[] typename;
/*     */   private ElementValuePairInfo[] pairs;
/*  29 */   long standardAnnotationTagBits = 0L;
/*  30 */   int readOffset = 0;
/*     */ 
/*  32 */   static Object[] EmptyValueArray = new Object[0];
/*     */ 
/*     */   AnnotationInfo(byte[] classFileBytes, int[] contantPoolOffsets, int offset) {
/*  35 */     super(classFileBytes, contantPoolOffsets, offset);
/*     */   }
/*     */ 
/*     */   AnnotationInfo(byte[] classFileBytes, int[] contantPoolOffsets, int offset, boolean runtimeVisible, boolean populate)
/*     */   {
/*  43 */     this(classFileBytes, contantPoolOffsets, offset);
/*  44 */     if (populate)
/*  45 */       decodeAnnotation();
/*     */     else
/*  47 */       this.readOffset = scanAnnotation(0, runtimeVisible, true); 
/*     */   }
/*     */ 
/*     */   private void decodeAnnotation() {
/*  50 */     this.readOffset = 0;
/*  51 */     int utf8Offset = this.constantPoolOffsets[u2At(0)] - this.structOffset;
/*  52 */     this.typename = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*  53 */     int numberOfPairs = u2At(2);
/*     */ 
/*  55 */     this.readOffset += 4;
/*  56 */     this.pairs = (numberOfPairs == 0 ? ElementValuePairInfo.NoMembers : new ElementValuePairInfo[numberOfPairs]);
/*  57 */     for (int i = 0; i < numberOfPairs; i++)
/*     */     {
/*  59 */       utf8Offset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/*  60 */       char[] membername = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*  61 */       this.readOffset += 2;
/*  62 */       Object value = decodeDefaultValue();
/*  63 */       this.pairs[i] = new ElementValuePairInfo(membername, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   Object decodeDefaultValue() {
/*  67 */     Object value = null;
/*     */ 
/*  69 */     int tag = u1At(this.readOffset);
/*  70 */     this.readOffset += 1;
/*  71 */     int constValueOffset = -1;
/*  72 */     switch (tag) {
/*     */     case 90:
/*  74 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/*  75 */       value = BooleanConstant.fromValue(i4At(constValueOffset + 1) == 1);
/*  76 */       this.readOffset += 2;
/*  77 */       break;
/*     */     case 73:
/*  79 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/*  80 */       value = IntConstant.fromValue(i4At(constValueOffset + 1));
/*  81 */       this.readOffset += 2;
/*  82 */       break;
/*     */     case 67:
/*  84 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/*  85 */       value = CharConstant.fromValue((char)i4At(constValueOffset + 1));
/*  86 */       this.readOffset += 2;
/*  87 */       break;
/*     */     case 66:
/*  89 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/*  90 */       value = ByteConstant.fromValue((byte)i4At(constValueOffset + 1));
/*  91 */       this.readOffset += 2;
/*  92 */       break;
/*     */     case 83:
/*  94 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/*  95 */       value = ShortConstant.fromValue((short)i4At(constValueOffset + 1));
/*  96 */       this.readOffset += 2;
/*  97 */       break;
/*     */     case 68:
/*  99 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/* 100 */       value = DoubleConstant.fromValue(doubleAt(constValueOffset + 1));
/* 101 */       this.readOffset += 2;
/* 102 */       break;
/*     */     case 70:
/* 104 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/* 105 */       value = FloatConstant.fromValue(floatAt(constValueOffset + 1));
/* 106 */       this.readOffset += 2;
/* 107 */       break;
/*     */     case 74:
/* 109 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/* 110 */       value = LongConstant.fromValue(i8At(constValueOffset + 1));
/* 111 */       this.readOffset += 2;
/* 112 */       break;
/*     */     case 115:
/* 114 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/* 115 */       value = StringConstant.fromValue(String.valueOf(utf8At(constValueOffset + 3, u2At(constValueOffset + 1))));
/* 116 */       this.readOffset += 2;
/* 117 */       break;
/*     */     case 101:
/* 119 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/* 120 */       char[] typeName = utf8At(constValueOffset + 3, u2At(constValueOffset + 1));
/* 121 */       this.readOffset += 2;
/* 122 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/* 123 */       char[] constName = utf8At(constValueOffset + 3, u2At(constValueOffset + 1));
/* 124 */       this.readOffset += 2;
/* 125 */       value = new EnumConstantSignature(typeName, constName);
/* 126 */       break;
/*     */     case 99:
/* 128 */       constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
/* 129 */       char[] className = utf8At(constValueOffset + 3, u2At(constValueOffset + 1));
/* 130 */       value = new ClassSignature(className);
/* 131 */       this.readOffset += 2;
/* 132 */       break;
/*     */     case 64:
/* 134 */       value = new AnnotationInfo(this.reference, this.constantPoolOffsets, this.readOffset + this.structOffset, false, true);
/* 135 */       this.readOffset += ((AnnotationInfo)value).readOffset;
/* 136 */       break;
/*     */     case 91:
/* 138 */       int numberOfValues = u2At(this.readOffset);
/* 139 */       this.readOffset += 2;
/* 140 */       if (numberOfValues == 0) {
/* 141 */         value = EmptyValueArray;
/*     */       } else {
/* 143 */         Object[] arrayElements = new Object[numberOfValues];
/* 144 */         value = arrayElements;
/* 145 */         for (int i = 0; i < numberOfValues; i++)
/* 146 */           arrayElements[i] = decodeDefaultValue();
/*     */       }
/* 148 */       break;
/*     */     default:
/* 150 */       throw new IllegalStateException("Unrecognized tag " + (char)tag);
/*     */     }
/* 152 */     return value;
/*     */   }
/*     */   public IBinaryElementValuePair[] getElementValuePairs() {
/* 155 */     if (this.pairs == null)
/* 156 */       initialize();
/* 157 */     return this.pairs;
/*     */   }
/*     */   public char[] getTypeName() {
/* 160 */     return this.typename;
/*     */   }
/*     */   void initialize() {
/* 163 */     if (this.pairs == null)
/* 164 */       decodeAnnotation(); 
/*     */   }
/*     */ 
/*     */   private int readRetentionPolicy(int offset) {
/* 167 */     int currentOffset = offset;
/* 168 */     int tag = u1At(currentOffset);
/* 169 */     currentOffset++;
/* 170 */     switch (tag) {
/*     */     case 101:
/* 172 */       int utf8Offset = this.constantPoolOffsets[u2At(currentOffset)] - this.structOffset;
/* 173 */       char[] typeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 174 */       currentOffset += 2;
/* 175 */       if ((typeName.length == 38) && (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_RETENTIONPOLICY))) {
/* 176 */         utf8Offset = this.constantPoolOffsets[u2At(currentOffset)] - this.structOffset;
/* 177 */         char[] constName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 178 */         this.standardAnnotationTagBits |= Annotation.getRetentionPolicy(constName);
/*     */       }
/* 180 */       currentOffset += 2;
/* 181 */       break;
/*     */     case 66:
/*     */     case 67:
/*     */     case 68:
/*     */     case 70:
/*     */     case 73:
/*     */     case 74:
/*     */     case 83:
/*     */     case 90:
/*     */     case 99:
/*     */     case 115:
/* 192 */       currentOffset += 2;
/* 193 */       break;
/*     */     case 64:
/* 197 */       currentOffset = scanAnnotation(currentOffset, false, false);
/* 198 */       break;
/*     */     case 91:
/* 200 */       int numberOfValues = u2At(currentOffset);
/* 201 */       currentOffset += 2;
/* 202 */       for (int i = 0; i < numberOfValues; i++)
/* 203 */         currentOffset = scanElementValue(currentOffset);
/* 204 */       break;
/*     */     default:
/* 206 */       throw new IllegalStateException();
/*     */     }
/* 208 */     return currentOffset;
/*     */   }
/*     */   private int readTargetValue(int offset) {
/* 211 */     int currentOffset = offset;
/* 212 */     int tag = u1At(currentOffset);
/* 213 */     currentOffset++;
/* 214 */     switch (tag) {
/*     */     case 101:
/* 216 */       int utf8Offset = this.constantPoolOffsets[u2At(currentOffset)] - this.structOffset;
/* 217 */       char[] typeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 218 */       currentOffset += 2;
/* 219 */       if ((typeName.length == 34) && (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_ELEMENTTYPE))) {
/* 220 */         utf8Offset = this.constantPoolOffsets[u2At(currentOffset)] - this.structOffset;
/* 221 */         char[] constName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 222 */         this.standardAnnotationTagBits |= Annotation.getTargetElementType(constName);
/*     */       }
/* 224 */       currentOffset += 2;
/* 225 */       break;
/*     */     case 66:
/*     */     case 67:
/*     */     case 68:
/*     */     case 70:
/*     */     case 73:
/*     */     case 74:
/*     */     case 83:
/*     */     case 90:
/*     */     case 99:
/*     */     case 115:
/* 236 */       currentOffset += 2;
/* 237 */       break;
/*     */     case 64:
/* 241 */       currentOffset = scanAnnotation(currentOffset, false, false);
/* 242 */       break;
/*     */     case 91:
/* 244 */       int numberOfValues = u2At(currentOffset);
/* 245 */       currentOffset += 2;
/* 246 */       if (numberOfValues == 0)
/* 247 */         this.standardAnnotationTagBits |= 34359738368L;
/*     */       else {
/* 249 */         for (int i = 0; i < numberOfValues; i++)
/* 250 */           currentOffset = readTargetValue(currentOffset);
/*     */       }
/* 252 */       break;
/*     */     default:
/* 254 */       throw new IllegalStateException();
/*     */     }
/* 256 */     return currentOffset;
/*     */   }
/*     */ 
/*     */   private int scanAnnotation(int offset, boolean expectRuntimeVisibleAnno, boolean toplevel)
/*     */   {
/* 275 */     int currentOffset = offset;
/* 276 */     int utf8Offset = this.constantPoolOffsets[u2At(offset)] - this.structOffset;
/* 277 */     char[] typeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 278 */     if (toplevel)
/* 279 */       this.typename = typeName;
/* 280 */     int numberOfPairs = u2At(offset + 2);
/*     */ 
/* 282 */     currentOffset += 4;
/* 283 */     if ((expectRuntimeVisibleAnno) && (toplevel)) {
/* 284 */       switch (typeName.length) {
/*     */       case 22:
/* 286 */         if (!CharOperation.equals(typeName, ConstantPool.JAVA_LANG_DEPRECATED)) break;
/* 287 */         this.standardAnnotationTagBits |= 70368744177664L;
/* 288 */         return currentOffset;
/*     */       case 29:
/* 292 */         if (!CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_TARGET)) break;
/* 293 */         currentOffset += 2;
/* 294 */         return readTargetValue(currentOffset);
/*     */       case 33:
/* 298 */         if (!CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_DOCUMENTED)) break;
/* 299 */         this.standardAnnotationTagBits |= 140737488355328L;
/* 300 */         return currentOffset;
/*     */       case 32:
/* 304 */         if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_RETENTION)) {
/* 305 */           currentOffset += 2;
/* 306 */           return readRetentionPolicy(currentOffset);
/*     */         }
/* 308 */         if (!CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_INHERITED)) break;
/* 309 */         this.standardAnnotationTagBits |= 281474976710656L;
/* 310 */         return currentOffset;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 315 */     for (int i = 0; i < numberOfPairs; i++)
/*     */     {
/* 317 */       currentOffset += 2;
/* 318 */       currentOffset = scanElementValue(currentOffset);
/*     */     }
/* 320 */     return currentOffset;
/*     */   }
/*     */ 
/*     */   private int scanElementValue(int offset)
/*     */   {
/* 328 */     int currentOffset = offset;
/* 329 */     int tag = u1At(currentOffset);
/* 330 */     currentOffset++;
/* 331 */     switch (tag) {
/*     */     case 66:
/*     */     case 67:
/*     */     case 68:
/*     */     case 70:
/*     */     case 73:
/*     */     case 74:
/*     */     case 83:
/*     */     case 90:
/*     */     case 99:
/*     */     case 115:
/* 342 */       currentOffset += 2;
/* 343 */       break;
/*     */     case 101:
/* 345 */       currentOffset += 4;
/* 346 */       break;
/*     */     case 64:
/* 350 */       currentOffset = scanAnnotation(currentOffset, false, false);
/* 351 */       break;
/*     */     case 91:
/* 353 */       int numberOfValues = u2At(currentOffset);
/* 354 */       currentOffset += 2;
/* 355 */       for (int i = 0; i < numberOfValues; i++)
/* 356 */         currentOffset = scanElementValue(currentOffset);
/* 357 */       break;
/*     */     default:
/* 359 */       throw new IllegalStateException();
/*     */     }
/* 361 */     return currentOffset;
/*     */   }
/*     */   public String toString() {
/* 364 */     StringBuffer buffer = new StringBuffer();
/* 365 */     buffer.append('@');
/* 366 */     buffer.append(this.typename);
/* 367 */     if (this.pairs != null) {
/* 368 */       buffer.append('(');
/* 369 */       buffer.append("\n\t");
/* 370 */       int i = 0; for (int len = this.pairs.length; i < len; i++) {
/* 371 */         if (i > 0)
/* 372 */           buffer.append(",\n\t");
/* 373 */         buffer.append(this.pairs[i]);
/*     */       }
/* 375 */       buffer.append(')');
/*     */     }
/* 377 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo
 * JD-Core Version:    0.6.0
 */