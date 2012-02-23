/*     */ package org.eclipse.jdt.internal.compiler.classfmt;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
/*     */ import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
/*     */ import org.eclipse.jdt.internal.compiler.env.IBinaryField;
/*     */ import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ByteConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CharConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.IntConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.LongConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ShortConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.StringConstant;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class FieldInfo extends ClassFileStruct
/*     */   implements IBinaryField, Comparable
/*     */ {
/*     */   protected int accessFlags;
/*     */   protected int attributeBytes;
/*     */   protected Constant constant;
/*     */   protected char[] descriptor;
/*     */   protected char[] name;
/*     */   protected char[] signature;
/*     */   protected int signatureUtf8Offset;
/*     */   protected long tagBits;
/*     */   protected Object wrappedConstantValue;
/*     */ 
/*     */   public static FieldInfo createField(byte[] classFileBytes, int[] offsets, int offset)
/*     */   {
/*  33 */     FieldInfo fieldInfo = new FieldInfo(classFileBytes, offsets, offset);
/*  34 */     AnnotationInfo[] annotations = fieldInfo.readAttributes();
/*  35 */     if (annotations == null)
/*  36 */       return fieldInfo;
/*  37 */     return new FieldInfoWithAnnotation(fieldInfo, annotations);
/*     */   }
/*     */ 
/*     */   protected FieldInfo(byte[] classFileBytes, int[] offsets, int offset)
/*     */   {
/*  46 */     super(classFileBytes, offsets, offset);
/*  47 */     this.accessFlags = -1;
/*  48 */     this.signatureUtf8Offset = -1;
/*     */   }
/*     */   private AnnotationInfo[] decodeAnnotations(int offset, boolean runtimeVisible) {
/*  51 */     int numberOfAnnotations = u2At(offset + 6);
/*  52 */     if (numberOfAnnotations > 0) {
/*  53 */       int readOffset = offset + 8;
/*  54 */       AnnotationInfo[] newInfos = (AnnotationInfo[])null;
/*  55 */       int newInfoCount = 0;
/*  56 */       for (int i = 0; i < numberOfAnnotations; i++)
/*     */       {
/*  58 */         AnnotationInfo newInfo = new AnnotationInfo(this.reference, this.constantPoolOffsets, 
/*  59 */           readOffset + this.structOffset, runtimeVisible, false);
/*  60 */         readOffset += newInfo.readOffset;
/*  61 */         long standardTagBits = newInfo.standardAnnotationTagBits;
/*  62 */         if (standardTagBits != 0L) {
/*  63 */           this.tagBits |= standardTagBits;
/*     */         } else {
/*  65 */           if (newInfos == null)
/*  66 */             newInfos = new AnnotationInfo[numberOfAnnotations - i];
/*  67 */           newInfos[(newInfoCount++)] = newInfo;
/*     */         }
/*     */       }
/*  70 */       if (newInfos != null) {
/*  71 */         if (newInfoCount != newInfos.length)
/*  72 */           System.arraycopy(newInfos, 0, newInfos = new AnnotationInfo[newInfoCount], 0, newInfoCount);
/*  73 */         return newInfos;
/*     */       }
/*     */     }
/*  76 */     return null;
/*     */   }
/*     */   public int compareTo(Object o) {
/*  79 */     return new String(getName()).compareTo(new String(((FieldInfo)o).getName()));
/*     */   }
/*     */   public boolean equals(Object o) {
/*  82 */     if (!(o instanceof FieldInfo)) {
/*  83 */       return false;
/*     */     }
/*  85 */     return CharOperation.equals(getName(), ((FieldInfo)o).getName());
/*     */   }
/*     */   public int hashCode() {
/*  88 */     return CharOperation.hashCode(getName());
/*     */   }
/*     */ 
/*     */   public Constant getConstant()
/*     */   {
/*  96 */     if (this.constant == null)
/*     */     {
/*  98 */       readConstantAttribute();
/*     */     }
/* 100 */     return this.constant;
/*     */   }
/*     */   public char[] getGenericSignature() {
/* 103 */     if (this.signatureUtf8Offset != -1) {
/* 104 */       if (this.signature == null)
/*     */       {
/* 106 */         this.signature = utf8At(this.signatureUtf8Offset + 3, u2At(this.signatureUtf8Offset + 1));
/*     */       }
/* 108 */       return this.signature;
/*     */     }
/* 110 */     return null;
/*     */   }
/*     */ 
/*     */   public int getModifiers()
/*     */   {
/* 119 */     if (this.accessFlags == -1)
/*     */     {
/* 121 */       this.accessFlags = u2At(0);
/* 122 */       readModifierRelatedAttributes();
/*     */     }
/* 124 */     return this.accessFlags;
/*     */   }
/*     */ 
/*     */   public char[] getName()
/*     */   {
/* 131 */     if (this.name == null)
/*     */     {
/* 133 */       int utf8Offset = this.constantPoolOffsets[u2At(2)] - this.structOffset;
/* 134 */       this.name = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*     */     }
/* 136 */     return this.name;
/*     */   }
/*     */   public long getTagBits() {
/* 139 */     return this.tagBits;
/*     */   }
/*     */ 
/*     */   public char[] getTypeName()
/*     */   {
/* 153 */     if (this.descriptor == null)
/*     */     {
/* 155 */       int utf8Offset = this.constantPoolOffsets[u2At(4)] - this.structOffset;
/* 156 */       this.descriptor = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*     */     }
/* 158 */     return this.descriptor;
/*     */   }
/*     */ 
/*     */   public IBinaryAnnotation[] getAnnotations()
/*     */   {
/* 164 */     return null;
/*     */   }
/*     */ 
/*     */   public Object getWrappedConstantValue()
/*     */   {
/* 172 */     if ((this.wrappedConstantValue == null) && 
/* 173 */       (hasConstant())) {
/* 174 */       Constant fieldConstant = getConstant();
/* 175 */       switch (fieldConstant.typeID()) {
/*     */       case 10:
/* 177 */         this.wrappedConstantValue = new Integer(fieldConstant.intValue());
/* 178 */         break;
/*     */       case 3:
/* 180 */         this.wrappedConstantValue = new Byte(fieldConstant.byteValue());
/* 181 */         break;
/*     */       case 4:
/* 183 */         this.wrappedConstantValue = new Short(fieldConstant.shortValue());
/* 184 */         break;
/*     */       case 2:
/* 186 */         this.wrappedConstantValue = new Character(fieldConstant.charValue());
/* 187 */         break;
/*     */       case 9:
/* 189 */         this.wrappedConstantValue = new Float(fieldConstant.floatValue());
/* 190 */         break;
/*     */       case 8:
/* 192 */         this.wrappedConstantValue = new Double(fieldConstant.doubleValue());
/* 193 */         break;
/*     */       case 5:
/* 195 */         this.wrappedConstantValue = Util.toBoolean(fieldConstant.booleanValue());
/* 196 */         break;
/*     */       case 7:
/* 198 */         this.wrappedConstantValue = new Long(fieldConstant.longValue());
/* 199 */         break;
/*     */       case 11:
/* 201 */         this.wrappedConstantValue = fieldConstant.stringValue();
/*     */       case 6:
/*     */       }
/*     */     }
/* 205 */     return this.wrappedConstantValue;
/*     */   }
/*     */ 
/*     */   public boolean hasConstant()
/*     */   {
/* 212 */     return getConstant() != Constant.NotAConstant;
/*     */   }
/*     */ 
/*     */   protected void initialize()
/*     */   {
/* 219 */     getModifiers();
/* 220 */     getName();
/* 221 */     getConstant();
/* 222 */     getTypeName();
/* 223 */     getGenericSignature();
/* 224 */     reset();
/*     */   }
/*     */ 
/*     */   public boolean isSynthetic()
/*     */   {
/* 231 */     return (getModifiers() & 0x1000) != 0;
/*     */   }
/*     */   private AnnotationInfo[] readAttributes() {
/* 234 */     int attributesCount = u2At(6);
/* 235 */     int readOffset = 8;
/* 236 */     AnnotationInfo[] annotations = (AnnotationInfo[])null;
/* 237 */     for (int i = 0; i < attributesCount; i++)
/*     */     {
/* 239 */       int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
/* 240 */       char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/* 241 */       if (attributeName.length > 0) {
/* 242 */         switch (attributeName[0]) {
/*     */         case 'S':
/* 244 */           if (!CharOperation.equals(AttributeNamesConstants.SignatureName, attributeName)) break;
/* 245 */           this.signatureUtf8Offset = (this.constantPoolOffsets[u2At(readOffset + 6)] - this.structOffset);
/* 246 */           break;
/*     */         case 'R':
/* 248 */           AnnotationInfo[] decodedAnnotations = (AnnotationInfo[])null;
/* 249 */           if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName))
/* 250 */             decodedAnnotations = decodeAnnotations(readOffset, true);
/* 251 */           else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
/* 252 */             decodedAnnotations = decodeAnnotations(readOffset, false);
/*     */           }
/* 254 */           if (decodedAnnotations == null) break;
/* 255 */           if (annotations == null) {
/* 256 */             annotations = decodedAnnotations;
/*     */           } else {
/* 258 */             int length = annotations.length;
/* 259 */             AnnotationInfo[] combined = new AnnotationInfo[length + decodedAnnotations.length];
/* 260 */             System.arraycopy(annotations, 0, combined, 0, length);
/* 261 */             System.arraycopy(decodedAnnotations, 0, combined, length, decodedAnnotations.length);
/* 262 */             annotations = combined;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 267 */       readOffset = (int)(readOffset + (
/* 267 */         6L + u4At(readOffset + 2)));
/*     */     }
/* 269 */     this.attributeBytes = readOffset;
/* 270 */     return annotations;
/*     */   }
/*     */   private void readConstantAttribute() {
/* 273 */     int attributesCount = u2At(6);
/* 274 */     int readOffset = 8;
/* 275 */     boolean isConstant = false;
/* 276 */     for (int i = 0; i < attributesCount; i++) {
/* 277 */       int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
/* 278 */       char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*     */ 
/* 280 */       if (CharOperation.equals(attributeName, AttributeNamesConstants.ConstantValueName)) {
/* 281 */         isConstant = true;
/*     */ 
/* 283 */         int relativeOffset = this.constantPoolOffsets[u2At(readOffset + 6)] - this.structOffset;
/* 284 */         switch (u1At(relativeOffset)) {
/*     */         case 3:
/* 286 */           char[] sign = getTypeName();
/* 287 */           if (sign.length == 1)
/* 288 */             switch (sign[0]) {
/*     */             case 'Z':
/* 290 */               this.constant = BooleanConstant.fromValue(i4At(relativeOffset + 1) == 1);
/* 291 */               break;
/*     */             case 'I':
/* 293 */               this.constant = IntConstant.fromValue(i4At(relativeOffset + 1));
/* 294 */               break;
/*     */             case 'C':
/* 296 */               this.constant = CharConstant.fromValue((char)i4At(relativeOffset + 1));
/* 297 */               break;
/*     */             case 'B':
/* 299 */               this.constant = ByteConstant.fromValue((byte)i4At(relativeOffset + 1));
/* 300 */               break;
/*     */             case 'S':
/* 302 */               this.constant = ShortConstant.fromValue((short)i4At(relativeOffset + 1));
/* 303 */               break;
/*     */             default:
/* 305 */               this.constant = Constant.NotAConstant; break;
/*     */             }
/*     */           else {
/* 308 */             this.constant = Constant.NotAConstant;
/*     */           }
/* 310 */           break;
/*     */         case 4:
/* 312 */           this.constant = FloatConstant.fromValue(floatAt(relativeOffset + 1));
/* 313 */           break;
/*     */         case 6:
/* 315 */           this.constant = DoubleConstant.fromValue(doubleAt(relativeOffset + 1));
/* 316 */           break;
/*     */         case 5:
/* 318 */           this.constant = LongConstant.fromValue(i8At(relativeOffset + 1));
/* 319 */           break;
/*     */         case 8:
/* 321 */           utf8Offset = this.constantPoolOffsets[u2At(relativeOffset + 1)] - this.structOffset;
/* 322 */           this.constant = 
/* 323 */             StringConstant.fromValue(
/* 324 */             String.valueOf(utf8At(utf8Offset + 3, u2At(utf8Offset + 1))));
/*     */         case 7:
/*     */         }
/*     */       }
/* 328 */       readOffset = (int)(readOffset + (
/* 328 */         6L + u4At(readOffset + 2)));
/*     */     }
/* 330 */     if (!isConstant)
/* 331 */       this.constant = Constant.NotAConstant;
/*     */   }
/*     */ 
/*     */   private void readModifierRelatedAttributes() {
/* 335 */     int attributesCount = u2At(6);
/* 336 */     int readOffset = 8;
/* 337 */     for (int i = 0; i < attributesCount; i++) {
/* 338 */       int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
/* 339 */       char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*     */ 
/* 341 */       if (attributeName.length != 0) {
/* 342 */         switch (attributeName[0]) {
/*     */         case 'D':
/* 344 */           if (!CharOperation.equals(attributeName, AttributeNamesConstants.DeprecatedName)) break;
/* 345 */           this.accessFlags |= 1048576;
/* 346 */           break;
/*     */         case 'S':
/* 348 */           if (!CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName)) break;
/* 349 */           this.accessFlags |= 4096;
/*     */         }
/*     */       }
/*     */ 
/* 353 */       readOffset = (int)(readOffset + (
/* 353 */         6L + u4At(readOffset + 2)));
/*     */     }
/*     */   }
/*     */ 
/*     */   public int sizeInBytes()
/*     */   {
/* 362 */     return this.attributeBytes;
/*     */   }
/*     */   public void throwFormatException() throws ClassFormatException {
/* 365 */     throw new ClassFormatException(17);
/*     */   }
/*     */   public String toString() {
/* 368 */     StringBuffer buffer = new StringBuffer(getClass().getName());
/* 369 */     toStringContent(buffer);
/* 370 */     return buffer.toString();
/*     */   }
/*     */   protected void toStringContent(StringBuffer buffer) {
/* 373 */     int modifiers = getModifiers();
/* 374 */     buffer
/* 375 */       .append('{')
/* 376 */       .append(
/* 377 */       ((modifiers & 0x100000) != 0 ? "deprecated " : Util.EMPTY_STRING) + (
/* 378 */       (modifiers & 0x1) == 1 ? "public " : Util.EMPTY_STRING) + (
/* 379 */       (modifiers & 0x2) == 2 ? "private " : Util.EMPTY_STRING) + (
/* 380 */       (modifiers & 0x4) == 4 ? "protected " : Util.EMPTY_STRING) + (
/* 381 */       (modifiers & 0x8) == 8 ? "static " : Util.EMPTY_STRING) + (
/* 382 */       (modifiers & 0x10) == 16 ? "final " : Util.EMPTY_STRING) + (
/* 383 */       (modifiers & 0x40) == 64 ? "volatile " : Util.EMPTY_STRING) + (
/* 384 */       (modifiers & 0x80) == 128 ? "transient " : Util.EMPTY_STRING))
/* 385 */       .append(getTypeName())
/* 386 */       .append(' ')
/* 387 */       .append(getName())
/* 388 */       .append(' ')
/* 389 */       .append(getConstant())
/* 390 */       .append('}')
/* 391 */       .toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.FieldInfo
 * JD-Core Version:    0.6.0
 */