/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ 
/*     */ public class TypeVariableBinding extends ReferenceBinding
/*     */ {
/*     */   public Binding declaringElement;
/*     */   public int rank;
/*     */   public TypeBinding firstBound;
/*     */   public ReferenceBinding superclass;
/*     */   public ReferenceBinding[] superInterfaces;
/*     */   public char[] genericTypeSignature;
/*     */   LookupEnvironment environment;
/*     */ 
/*     */   public TypeVariableBinding(char[] sourceName, Binding declaringElement, int rank, LookupEnvironment environment)
/*     */   {
/*  38 */     this.sourceName = sourceName;
/*  39 */     this.declaringElement = declaringElement;
/*  40 */     this.rank = rank;
/*  41 */     this.modifiers = 1073741825;
/*  42 */     this.tagBits |= 536870912L;
/*  43 */     this.environment = environment;
/*     */   }
/*     */ 
/*     */   public int boundCheck(Substitution substitution, TypeBinding argumentType)
/*     */   {
/*  50 */     if ((argumentType == TypeBinding.NULL) || (argumentType == this)) {
/*  51 */       return 0;
/*     */     }
/*  53 */     boolean hasSubstitution = substitution != null;
/*  54 */     if ((!(argumentType instanceof ReferenceBinding)) && (!argumentType.isArrayType())) {
/*  55 */       return 2;
/*     */     }
/*     */ 
/*  58 */     if (this.superclass == null) {
/*  59 */       return 0;
/*     */     }
/*  61 */     if (argumentType.kind() == 516) {
/*  62 */       WildcardBinding wildcard = (WildcardBinding)argumentType;
/*  63 */       switch (wildcard.boundKind) {
/*     */       case 1:
/*  65 */         TypeBinding wildcardBound = wildcard.bound;
/*  66 */         if (wildcardBound == this)
/*  67 */           return 0;
/*  68 */         boolean isArrayBound = wildcardBound.isArrayType();
/*  69 */         if (!wildcardBound.isInterface()) {
/*  70 */           TypeBinding substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superclass) : this.superclass;
/*  71 */           if (substitutedSuperType.id != 1) {
/*  72 */             if (isArrayBound) {
/*  73 */               if (!wildcardBound.isCompatibleWith(substitutedSuperType))
/*  74 */                 return 2;
/*     */             } else {
/*  76 */               TypeBinding match = wildcardBound.findSuperTypeOriginatingFrom(substitutedSuperType);
/*  77 */               if (match != null) {
/*  78 */                 if (substitutedSuperType.isProvablyDistinct(match))
/*  79 */                   return 2;
/*     */               }
/*     */               else {
/*  82 */                 match = substitutedSuperType.findSuperTypeOriginatingFrom(wildcardBound);
/*  83 */                 if (match != null) {
/*  84 */                   if (match.isProvablyDistinct(wildcardBound)) {
/*  85 */                     return 2;
/*     */                   }
/*     */                 }
/*  88 */                 else if ((!wildcardBound.isTypeVariable()) && (!substitutedSuperType.isTypeVariable())) {
/*  89 */                   return 2;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*  96 */         boolean mustImplement = (isArrayBound) || (((ReferenceBinding)wildcardBound).isFinal());
/*  97 */         int i = 0; for (int length = this.superInterfaces.length; i < length; i++) {
/*  98 */           TypeBinding substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superInterfaces[i]) : this.superInterfaces[i];
/*  99 */           if (isArrayBound) {
/* 100 */             if (!wildcardBound.isCompatibleWith(substitutedSuperType))
/* 101 */               return 2;
/*     */           } else {
/* 103 */             TypeBinding match = wildcardBound.findSuperTypeOriginatingFrom(substitutedSuperType);
/* 104 */             if (match != null) {
/* 105 */               if (substitutedSuperType.isProvablyDistinct(match))
/* 106 */                 return 2;
/*     */             }
/* 108 */             else if (mustImplement) {
/* 109 */               return 2;
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 114 */         break;
/*     */       case 2:
/* 117 */         return boundCheck(substitution, wildcard.bound);
/*     */       case 0:
/*     */       }
/*     */ 
/* 122 */       return 0;
/*     */     }
/* 124 */     boolean unchecked = false;
/* 125 */     if (this.superclass.id != 1) {
/* 126 */       TypeBinding substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superclass) : this.superclass;
/* 127 */       if (substitutedSuperType != argumentType) {
/* 128 */         if (!argumentType.isCompatibleWith(substitutedSuperType)) {
/* 129 */           return 2;
/*     */         }
/* 131 */         TypeBinding match = argumentType.findSuperTypeOriginatingFrom(substitutedSuperType);
/* 132 */         if (match != null)
/*     */         {
/* 134 */           if ((match.isRawType()) && (substitutedSuperType.isBoundParameterizedType()))
/* 135 */             unchecked = true;
/*     */         }
/*     */       }
/*     */     }
/* 139 */     int i = 0; for (int length = this.superInterfaces.length; i < length; i++) {
/* 140 */       TypeBinding substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superInterfaces[i]) : this.superInterfaces[i];
/* 141 */       if (substitutedSuperType != argumentType) {
/* 142 */         if (!argumentType.isCompatibleWith(substitutedSuperType)) {
/* 143 */           return 2;
/*     */         }
/* 145 */         TypeBinding match = argumentType.findSuperTypeOriginatingFrom(substitutedSuperType);
/* 146 */         if (match == null)
/*     */           continue;
/* 148 */         if ((match.isRawType()) && (substitutedSuperType.isBoundParameterizedType())) {
/* 149 */           unchecked = true;
/*     */         }
/*     */       }
/*     */     }
/* 153 */     return unchecked ? 1 : 0;
/*     */   }
/*     */ 
/*     */   public int boundsCount() {
/* 157 */     if (this.firstBound == null)
/* 158 */       return 0;
/* 159 */     if (this.firstBound == this.superclass) {
/* 160 */       return this.superInterfaces.length + 1;
/*     */     }
/* 162 */     return this.superInterfaces.length;
/*     */   }
/*     */ 
/*     */   public boolean canBeInstantiated()
/*     */   {
/* 170 */     return false;
/*     */   }
/*     */ 
/*     */   public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint)
/*     */   {
/* 183 */     if (this.declaringElement != inferenceContext.genericMethod) return;
/*     */ 
/* 186 */     switch (actualType.kind()) {
/*     */     case 132:
/* 188 */       if (actualType == TypeBinding.NULL) return;
/* 189 */       TypeBinding boxedType = scope.environment().computeBoxingType(actualType);
/* 190 */       if (boxedType == actualType) return;
/* 191 */       actualType = boxedType;
/* 192 */       break;
/*     */     case 516:
/* 194 */       return;
/*     */     }
/*     */     int variableConstraint;
/*     */     int variableConstraint;
/*     */     int variableConstraint;
/* 199 */     switch (constraint) {
/*     */     case 0:
/* 201 */       variableConstraint = 0;
/* 202 */       break;
/*     */     case 1:
/* 204 */       variableConstraint = 2;
/* 205 */       break;
/*     */     default:
/* 208 */       variableConstraint = 1;
/*     */     }
/*     */ 
/* 211 */     inferenceContext.recordSubstitute(this, actualType, variableConstraint);
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf)
/*     */   {
/* 220 */     StringBuffer buffer = new StringBuffer();
/* 221 */     Binding declaring = this.declaringElement;
/*     */     MethodBinding methodBinding;
/*     */     MethodBinding[] methods;
/*     */     int i;
/*     */     int length;
/* 222 */     label134: if ((!isLeaf) && (declaring.kind() == 8)) {
/* 223 */       methodBinding = (MethodBinding)declaring;
/* 224 */       ReferenceBinding declaringClass = methodBinding.declaringClass;
/* 225 */       buffer.append(declaringClass.computeUniqueKey(false));
/* 226 */       buffer.append(':');
/* 227 */       methods = declaringClass.methods();
/* 228 */       if (methods == null) break label134; i = 0; length = methods.length;
/*     */     }while (true) { MethodBinding binding = methods[i];
/* 231 */       if (binding == methodBinding) {
/* 232 */         buffer.append(i);
/*     */       }
/*     */       else
/*     */       {
/* 229 */         i++; if (i < length) continue; break;
/*     */ 
/* 237 */         buffer.append(declaring.computeUniqueKey(false));
/* 238 */         buffer.append(':');
/*     */       } }
/* 240 */     buffer.append(genericTypeSignature());
/* 241 */     int length = buffer.length();
/* 242 */     char[] uniqueKey = new char[length];
/* 243 */     buffer.getChars(0, length, uniqueKey, 0);
/* 244 */     return uniqueKey;
/*     */   }
/*     */   public char[] constantPoolName() {
/* 247 */     if (this.firstBound != null) {
/* 248 */       return this.firstBound.constantPoolName();
/*     */     }
/* 250 */     return this.superclass.constantPoolName();
/*     */   }
/*     */ 
/*     */   public String debugName()
/*     */   {
/* 256 */     return new String(this.sourceName);
/*     */   }
/*     */   public TypeBinding erasure() {
/* 259 */     if (this.firstBound != null) {
/* 260 */       return this.firstBound.erasure();
/*     */     }
/* 262 */     return this.superclass;
/*     */   }
/*     */ 
/*     */   public char[] genericSignature()
/*     */   {
/* 269 */     StringBuffer sig = new StringBuffer(10);
/* 270 */     sig.append(this.sourceName).append(':');
/* 271 */     int interfaceLength = this.superInterfaces == null ? 0 : this.superInterfaces.length;
/* 272 */     if (((interfaceLength == 0) || (this.firstBound == this.superclass)) && 
/* 273 */       (this.superclass != null)) {
/* 274 */       sig.append(this.superclass.genericTypeSignature());
/*     */     }
/* 276 */     for (int i = 0; i < interfaceLength; i++) {
/* 277 */       sig.append(':').append(this.superInterfaces[i].genericTypeSignature());
/*     */     }
/* 279 */     int sigLength = sig.length();
/* 280 */     char[] genericSignature = new char[sigLength];
/* 281 */     sig.getChars(0, sigLength, genericSignature, 0);
/* 282 */     return genericSignature;
/*     */   }
/*     */ 
/*     */   public char[] genericTypeSignature()
/*     */   {
/* 289 */     if (this.genericTypeSignature != null) return this.genericTypeSignature;
/* 290 */     return this.genericTypeSignature = CharOperation.concat('T', this.sourceName, ';');
/*     */   }
/*     */ 
/*     */   boolean hasOnlyRawBounds() {
/* 294 */     if ((this.superclass != null) && (this.firstBound == this.superclass) && 
/* 295 */       (!this.superclass.isRawType())) {
/* 296 */       return false;
/*     */     }
/* 298 */     if (this.superInterfaces != null) {
/* 299 */       int i = 0; for (int l = this.superInterfaces.length; i < l; i++)
/* 300 */         if (!this.superInterfaces[i].isRawType())
/* 301 */           return false;
/*     */     }
/* 303 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isErasureBoundTo(TypeBinding type)
/*     */   {
/* 310 */     if (this.superclass.erasure() == type)
/* 311 */       return true;
/* 312 */     int i = 0; for (int length = this.superInterfaces.length; i < length; i++) {
/* 313 */       if (this.superInterfaces[i].erasure() == type)
/* 314 */         return true;
/*     */     }
/* 316 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isHierarchyConnected() {
/* 320 */     return (this.modifiers & 0x2000000) == 0;
/*     */   }
/*     */ 
/*     */   public boolean isInterchangeableWith(TypeVariableBinding otherVariable, Substitution substitute)
/*     */   {
/* 329 */     if (this == otherVariable)
/* 330 */       return true;
/* 331 */     int length = this.superInterfaces.length;
/* 332 */     if (length != otherVariable.superInterfaces.length) {
/* 333 */       return false;
/*     */     }
/* 335 */     if (this.superclass != Scope.substitute(substitute, otherVariable.superclass)) {
/* 336 */       return false;
/*     */     }
/* 338 */     for (int i = 0; i < length; i++) {
/* 339 */       TypeBinding superType = Scope.substitute(substitute, otherVariable.superInterfaces[i]);
/* 340 */       int j = 0;
/* 341 */       while (superType != this.superInterfaces[j])
/*     */       {
/* 340 */         j++; if (j >= length)
/*     */         {
/* 343 */           return false;
/*     */         }
/*     */       }
/*     */     }
/* 345 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isTypeVariable()
/*     */   {
/* 352 */     return true;
/*     */   }
/*     */ 
/*     */   public int kind()
/*     */   {
/* 377 */     return 4100;
/*     */   }
/*     */ 
/*     */   public TypeBinding[] otherUpperBounds() {
/* 381 */     if (this.firstBound == null)
/* 382 */       return Binding.NO_TYPES;
/* 383 */     if (this.firstBound == this.superclass)
/* 384 */       return this.superInterfaces;
/* 385 */     int otherLength = this.superInterfaces.length - 1;
/* 386 */     if (otherLength > 0)
/*     */     {
/*     */       TypeBinding[] otherBounds;
/* 388 */       System.arraycopy(this.superInterfaces, 1, otherBounds = new TypeBinding[otherLength], 0, otherLength);
/* 389 */       return otherBounds;
/*     */     }
/* 391 */     return Binding.NO_TYPES;
/*     */   }
/*     */ 
/*     */   public char[] readableName()
/*     */   {
/* 398 */     return this.sourceName;
/*     */   }
/*     */   ReferenceBinding resolve() {
/* 401 */     if ((this.modifiers & 0x2000000) == 0) {
/* 402 */       return this;
/*     */     }
/* 404 */     TypeBinding oldSuperclass = this.superclass; TypeBinding oldFirstInterface = null;
/* 405 */     if (this.superclass != null) {
/* 406 */       ReferenceBinding resolveType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.superclass, this.environment, true);
/* 407 */       this.tagBits |= resolveType.tagBits & 0x800;
/* 408 */       this.superclass = resolveType;
/*     */     }
/* 410 */     ReferenceBinding[] interfaces = this.superInterfaces;
/*     */     int length;
/* 412 */     if ((length = interfaces.length) != 0) {
/* 413 */       oldFirstInterface = interfaces[0];
/* 414 */       int i = length;
/*     */       do { ReferenceBinding resolveType = (ReferenceBinding)BinaryTypeBinding.resolveType(interfaces[i], this.environment, true);
/* 416 */         this.tagBits |= resolveType.tagBits & 0x800;
/* 417 */         interfaces[i] = resolveType;
/*     */ 
/* 414 */         i--; } while (i >= 0);
/*     */     }
/*     */ 
/* 421 */     if (this.firstBound != null) {
/* 422 */       if (this.firstBound == oldSuperclass)
/* 423 */         this.firstBound = this.superclass;
/* 424 */       else if (this.firstBound == oldFirstInterface) {
/* 425 */         this.firstBound = interfaces[0];
/*     */       }
/*     */     }
/* 428 */     this.modifiers &= -33554433;
/* 429 */     return this;
/*     */   }
/*     */ 
/*     */   public char[] shortReadableName()
/*     */   {
/* 435 */     return readableName();
/*     */   }
/*     */   public ReferenceBinding superclass() {
/* 438 */     return this.superclass;
/*     */   }
/*     */ 
/*     */   public ReferenceBinding[] superInterfaces() {
/* 442 */     return this.superInterfaces;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 449 */     StringBuffer buffer = new StringBuffer(10);
/* 450 */     buffer.append('<').append(this.sourceName);
/* 451 */     if ((this.superclass != null) && (this.firstBound == this.superclass)) {
/* 452 */       buffer.append(" extends ").append(this.superclass.debugName());
/*     */     }
/* 454 */     if ((this.superInterfaces != null) && (this.superInterfaces != Binding.NO_SUPERINTERFACES)) {
/* 455 */       if (this.firstBound != this.superclass) {
/* 456 */         buffer.append(" extends ");
/*     */       }
/* 458 */       int i = 0; for (int length = this.superInterfaces.length; i < length; i++) {
/* 459 */         if ((i > 0) || (this.firstBound == this.superclass)) {
/* 460 */           buffer.append(" & ");
/*     */         }
/* 462 */         buffer.append(this.superInterfaces[i].debugName());
/*     */       }
/*     */     }
/* 465 */     buffer.append('>');
/* 466 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public TypeBinding upperBound()
/*     */   {
/* 473 */     if (this.firstBound != null) {
/* 474 */       return this.firstBound;
/*     */     }
/* 476 */     return this.superclass;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding
 * JD-Core Version:    0.6.0
 */