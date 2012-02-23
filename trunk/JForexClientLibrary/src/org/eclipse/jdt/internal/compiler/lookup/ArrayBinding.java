/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ 
/*     */ public final class ArrayBinding extends TypeBinding
/*     */ {
/*  22 */   public static final FieldBinding ArrayLength = new FieldBinding(TypeConstants.LENGTH, TypeBinding.INT, 17, null, Constant.NotAConstant);
/*     */   public TypeBinding leafComponentType;
/*     */   public int dimensions;
/*     */   LookupEnvironment environment;
/*     */   char[] constantPoolName;
/*     */   char[] genericTypeSignature;
/*     */ 
/*     */   public ArrayBinding(TypeBinding type, int dimensions, LookupEnvironment environment)
/*     */   {
/*  31 */     this.tagBits |= 1L;
/*  32 */     this.leafComponentType = type;
/*  33 */     this.dimensions = dimensions;
/*  34 */     this.environment = environment;
/*  35 */     if ((type instanceof UnresolvedReferenceBinding))
/*  36 */       ((UnresolvedReferenceBinding)type).addWrapper(this, environment);
/*     */     else
/*  38 */       this.tagBits |= type.tagBits & 0x60000880;
/*     */   }
/*     */ 
/*     */   public TypeBinding closestMatch() {
/*  42 */     if (isValidBinding()) {
/*  43 */       return this;
/*     */     }
/*  45 */     TypeBinding leafClosestMatch = this.leafComponentType.closestMatch();
/*  46 */     if (leafClosestMatch == null) {
/*  47 */       return null;
/*     */     }
/*  49 */     return this.environment.createArrayType(this.leafComponentType.closestMatch(), this.dimensions);
/*     */   }
/*     */ 
/*     */   public List collectMissingTypes(List missingTypes)
/*     */   {
/*  56 */     if ((this.tagBits & 0x80) != 0L) {
/*  57 */       missingTypes = this.leafComponentType.collectMissingTypes(missingTypes);
/*     */     }
/*  59 */     return missingTypes;
/*     */   }
/*     */ 
/*     */   public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint)
/*     */   {
/*  72 */     if ((this.tagBits & 0x20000000) == 0L) return;
/*  73 */     if (actualType == TypeBinding.NULL) return;
/*     */ 
/*  75 */     switch (actualType.kind()) {
/*     */     case 68:
/*  77 */       int actualDim = actualType.dimensions();
/*  78 */       if (actualDim == this.dimensions) {
/*  79 */         this.leafComponentType.collectSubstitutes(scope, actualType.leafComponentType(), inferenceContext, constraint); } else {
/*  80 */         if (actualDim <= this.dimensions) break;
/*  81 */         ArrayBinding actualReducedType = this.environment.createArrayType(actualType.leafComponentType(), actualDim - this.dimensions);
/*  82 */         this.leafComponentType.collectSubstitutes(scope, actualReducedType, inferenceContext, constraint);
/*     */       }
/*  84 */       break;
/*     */     case 4100:
/*     */     }
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf)
/*     */   {
/*  97 */     char[] brackets = new char[this.dimensions];
/*  98 */     for (int i = this.dimensions - 1; i >= 0; i--) brackets[i] = '[';
/*  99 */     return CharOperation.concat(brackets, this.leafComponentType.computeUniqueKey(isLeaf));
/*     */   }
/*     */ 
/*     */   public char[] constantPoolName()
/*     */   {
/* 108 */     if (this.constantPoolName != null) {
/* 109 */       return this.constantPoolName;
/*     */     }
/* 111 */     char[] brackets = new char[this.dimensions];
/* 112 */     for (int i = this.dimensions - 1; i >= 0; i--) brackets[i] = '[';
/* 113 */     return this.constantPoolName = CharOperation.concat(brackets, this.leafComponentType.signature());
/*     */   }
/*     */   public String debugName() {
/* 116 */     StringBuffer brackets = new StringBuffer(this.dimensions * 2);
/* 117 */     int i = this.dimensions;
/*     */     do { brackets.append("[]");
/*     */ 
/* 117 */       i--; } while (i >= 0);
/*     */ 
/* 119 */     return this.leafComponentType.debugName() + brackets.toString();
/*     */   }
/*     */   public int dimensions() {
/* 122 */     return this.dimensions;
/*     */   }
/*     */ 
/*     */   public TypeBinding elementsType()
/*     */   {
/* 131 */     if (this.dimensions == 1) return this.leafComponentType;
/* 132 */     return this.environment.createArrayType(this.leafComponentType, this.dimensions - 1);
/*     */   }
/*     */ 
/*     */   public TypeBinding erasure()
/*     */   {
/* 138 */     TypeBinding erasedType = this.leafComponentType.erasure();
/* 139 */     if (this.leafComponentType != erasedType)
/* 140 */       return this.environment.createArrayType(erasedType, this.dimensions);
/* 141 */     return this;
/*     */   }
/*     */   public LookupEnvironment environment() {
/* 144 */     return this.environment;
/*     */   }
/*     */ 
/*     */   public char[] genericTypeSignature()
/*     */   {
/* 149 */     if (this.genericTypeSignature == null) {
/* 150 */       char[] brackets = new char[this.dimensions];
/* 151 */       for (int i = this.dimensions - 1; i >= 0; i--) brackets[i] = '[';
/* 152 */       this.genericTypeSignature = CharOperation.concat(brackets, this.leafComponentType.genericTypeSignature());
/*     */     }
/* 154 */     return this.genericTypeSignature;
/*     */   }
/*     */ 
/*     */   public PackageBinding getPackage() {
/* 158 */     return this.leafComponentType.getPackage();
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 162 */     return this.leafComponentType == null ? super.hashCode() : this.leafComponentType.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean isCompatibleWith(TypeBinding otherType)
/*     */   {
/* 168 */     if (this == otherType) {
/* 169 */       return true;
/*     */     }
/* 171 */     switch (otherType.kind()) {
/*     */     case 68:
/* 173 */       ArrayBinding otherArray = (ArrayBinding)otherType;
/* 174 */       if (otherArray.leafComponentType.isBaseType())
/* 175 */         return false;
/* 176 */       if (this.dimensions == otherArray.dimensions)
/* 177 */         return this.leafComponentType.isCompatibleWith(otherArray.leafComponentType);
/* 178 */       if (this.dimensions >= otherArray.dimensions) break;
/* 179 */       return false;
/*     */     case 132:
/* 182 */       return false;
/*     */     case 516:
/*     */     case 8196:
/* 185 */       return ((WildcardBinding)otherType).boundCheck(this);
/*     */     case 4100:
/* 189 */       if (otherType.isCapture()) {
/* 190 */         CaptureBinding otherCapture = (CaptureBinding)otherType;
/*     */         TypeBinding otherLowerBound;
/* 192 */         if ((otherLowerBound = otherCapture.lowerBound) != null) {
/* 193 */           if (!otherLowerBound.isArrayType()) return false;
/* 194 */           return isCompatibleWith(otherLowerBound);
/*     */         }
/*     */       }
/* 197 */       return false;
/*     */     }
/*     */ 
/* 202 */     switch (otherType.leafComponentType().id) {
/*     */     case 1:
/*     */     case 36:
/*     */     case 37:
/* 206 */       return true;
/*     */     }
/* 208 */     return false;
/*     */   }
/*     */ 
/*     */   public int kind() {
/* 212 */     return 68;
/*     */   }
/*     */ 
/*     */   public TypeBinding leafComponentType() {
/* 216 */     return this.leafComponentType;
/*     */   }
/*     */ 
/*     */   public int problemId()
/*     */   {
/* 224 */     return this.leafComponentType.problemId();
/*     */   }
/*     */ 
/*     */   public char[] qualifiedSourceName()
/*     */   {
/* 233 */     char[] brackets = new char[this.dimensions * 2];
/* 234 */     for (int i = this.dimensions * 2 - 1; i >= 0; i -= 2) {
/* 235 */       brackets[i] = ']';
/* 236 */       brackets[(i - 1)] = '[';
/*     */     }
/* 238 */     return CharOperation.concat(this.leafComponentType.qualifiedSourceName(), brackets);
/*     */   }
/*     */   public char[] readableName() {
/* 241 */     char[] brackets = new char[this.dimensions * 2];
/* 242 */     for (int i = this.dimensions * 2 - 1; i >= 0; i -= 2) {
/* 243 */       brackets[i] = ']';
/* 244 */       brackets[(i - 1)] = '[';
/*     */     }
/* 246 */     return CharOperation.concat(this.leafComponentType.readableName(), brackets);
/*     */   }
/*     */   public char[] shortReadableName() {
/* 249 */     char[] brackets = new char[this.dimensions * 2];
/* 250 */     for (int i = this.dimensions * 2 - 1; i >= 0; i -= 2) {
/* 251 */       brackets[i] = ']';
/* 252 */       brackets[(i - 1)] = '[';
/*     */     }
/* 254 */     return CharOperation.concat(this.leafComponentType.shortReadableName(), brackets);
/*     */   }
/*     */   public char[] sourceName() {
/* 257 */     char[] brackets = new char[this.dimensions * 2];
/* 258 */     for (int i = this.dimensions * 2 - 1; i >= 0; i -= 2) {
/* 259 */       brackets[i] = ']';
/* 260 */       brackets[(i - 1)] = '[';
/*     */     }
/* 262 */     return CharOperation.concat(this.leafComponentType.sourceName(), brackets);
/*     */   }
/*     */   public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType, LookupEnvironment env) {
/* 265 */     if (this.leafComponentType == unresolvedType) {
/* 266 */       this.leafComponentType = env.convertUnresolvedBinaryToRawType(resolvedType);
/* 267 */       this.tagBits |= this.leafComponentType.tagBits & 0x60000080;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 271 */     return this.leafComponentType != null ? debugName() : "NULL TYPE ARRAY";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ArrayBinding
 * JD-Core Version:    0.6.0
 */