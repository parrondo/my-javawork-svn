/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ 
/*     */ public class WildcardBinding extends ReferenceBinding
/*     */ {
/*     */   public ReferenceBinding genericType;
/*     */   public int rank;
/*     */   public TypeBinding bound;
/*     */   public TypeBinding[] otherBounds;
/*     */   char[] genericSignature;
/*     */   public int boundKind;
/*     */   ReferenceBinding superclass;
/*     */   ReferenceBinding[] superInterfaces;
/*     */   TypeVariableBinding typeVariable;
/*     */   LookupEnvironment environment;
/*     */ 
/*     */   public WildcardBinding(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind, LookupEnvironment environment)
/*     */   {
/*  41 */     this.rank = rank;
/*  42 */     this.boundKind = boundKind;
/*  43 */     this.modifiers = 1073741825;
/*  44 */     this.environment = environment;
/*  45 */     initialize(genericType, bound, otherBounds);
/*     */ 
/*  52 */     if ((genericType instanceof UnresolvedReferenceBinding))
/*  53 */       ((UnresolvedReferenceBinding)genericType).addWrapper(this, environment);
/*  54 */     if ((bound instanceof UnresolvedReferenceBinding))
/*  55 */       ((UnresolvedReferenceBinding)bound).addWrapper(this, environment);
/*  56 */     this.tagBits |= 16777216L;
/*     */   }
/*     */ 
/*     */   public int kind() {
/*  60 */     return this.otherBounds == null ? 516 : 8196;
/*     */   }
/*     */ 
/*     */   public boolean boundCheck(TypeBinding argumentType)
/*     */   {
/*  67 */     switch (this.boundKind) {
/*     */     case 0:
/*  69 */       return true;
/*     */     case 1:
/*  71 */       if (!argumentType.isCompatibleWith(this.bound)) return false;
/*     */ 
/*  73 */       int i = 0; for (int length = this.otherBounds == null ? 0 : this.otherBounds.length; i < length; i++) {
/*  74 */         if (!argumentType.isCompatibleWith(this.otherBounds[i])) return false;
/*     */       }
/*  76 */       return true;
/*     */     }
/*     */ 
/*  79 */     return argumentType.isCompatibleWith(this.bound);
/*     */   }
/*     */ 
/*     */   public boolean canBeInstantiated()
/*     */   {
/*  87 */     return false;
/*     */   }
/*     */ 
/*     */   public List collectMissingTypes(List missingTypes)
/*     */   {
/*  94 */     if ((this.tagBits & 0x80) != 0L) {
/*  95 */       missingTypes = this.bound.collectMissingTypes(missingTypes);
/*     */     }
/*  97 */     return missingTypes;
/*     */   }
/*     */ 
/*     */   public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint)
/*     */   {
/* 110 */     if ((this.tagBits & 0x20000000) == 0L) return;
/* 111 */     if (actualType == TypeBinding.NULL) return;
/*     */ 
/* 113 */     if (actualType.isCapture()) {
/* 114 */       CaptureBinding capture = (CaptureBinding)actualType;
/* 115 */       actualType = capture.wildcard;
/*     */     }
/*     */ 
/* 118 */     switch (constraint) {
/*     */     case 1:
/* 120 */       switch (this.boundKind)
/*     */       {
/*     */       case 0:
/* 139 */         break;
/*     */       case 1:
/* 141 */         switch (actualType.kind()) {
/*     */         case 516:
/* 143 */           WildcardBinding actualWildcard = (WildcardBinding)actualType;
/* 144 */           switch (actualWildcard.boundKind) {
/*     */           case 0:
/* 146 */             break;
/*     */           case 1:
/* 148 */             this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 1);
/*     */           case 2:
/*     */           }
/*     */ 
/* 153 */           break;
/*     */         case 8196:
/* 155 */           WildcardBinding actualIntersection = (WildcardBinding)actualType;
/* 156 */           this.bound.collectSubstitutes(scope, actualIntersection.bound, inferenceContext, 1);
/* 157 */           int i = 0; for (int length = actualIntersection.otherBounds.length; i < length; i++) {
/* 158 */             this.bound.collectSubstitutes(scope, actualIntersection.otherBounds[i], inferenceContext, 1);
/*     */           }
/* 160 */           break;
/*     */         default:
/* 162 */           this.bound.collectSubstitutes(scope, actualType, inferenceContext, 1);
/*     */         }
/*     */ 
/* 165 */         break;
/*     */       case 2:
/* 167 */         switch (actualType.kind()) {
/*     */         case 516:
/* 169 */           WildcardBinding actualWildcard = (WildcardBinding)actualType;
/* 170 */           switch (actualWildcard.boundKind) {
/*     */           case 0:
/* 172 */             break;
/*     */           case 1:
/* 174 */             break;
/*     */           case 2:
/* 176 */             this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 2);
/* 177 */             int i = 0; for (int length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length; i < length; i++) {
/* 178 */               this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, 2);
/*     */             }
/*     */           }
/*     */ 
/* 182 */           break;
/*     */         case 8196:
/* 184 */           break;
/*     */         default:
/* 186 */           this.bound.collectSubstitutes(scope, actualType, inferenceContext, 2);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 191 */       break;
/*     */     case 0:
/* 193 */       switch (this.boundKind)
/*     */       {
/*     */       case 0:
/* 212 */         break;
/*     */       case 1:
/* 214 */         switch (actualType.kind()) {
/*     */         case 516:
/* 216 */           WildcardBinding actualWildcard = (WildcardBinding)actualType;
/* 217 */           switch (actualWildcard.boundKind) {
/*     */           case 0:
/* 219 */             break;
/*     */           case 1:
/* 221 */             this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 0);
/* 222 */             int i = 0; for (int length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length; i < length; i++) {
/* 223 */               this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, 0);
/*     */             }
/*     */ 
/*     */           case 2:
/*     */           }
/*     */ 
/* 229 */           break;
/*     */         case 8196:
/* 231 */           WildcardBinding actuaIntersection = (WildcardBinding)actualType;
/* 232 */           this.bound.collectSubstitutes(scope, actuaIntersection.bound, inferenceContext, 0);
/* 233 */           int i = 0; for (int length = actuaIntersection.otherBounds == null ? 0 : actuaIntersection.otherBounds.length; i < length; i++) {
/* 234 */             this.bound.collectSubstitutes(scope, actuaIntersection.otherBounds[i], inferenceContext, 0);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 240 */         break;
/*     */       case 2:
/* 242 */         switch (actualType.kind()) {
/*     */         case 516:
/* 244 */           WildcardBinding actualWildcard = (WildcardBinding)actualType;
/* 245 */           switch (actualWildcard.boundKind) {
/*     */           case 0:
/* 247 */             break;
/*     */           case 1:
/* 249 */             break;
/*     */           case 2:
/* 251 */             this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 0);
/* 252 */             int i = 0; for (int length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length; i < length; i++) {
/* 253 */               this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, 0);
/*     */             }
/*     */           }
/*     */ 
/* 257 */           break;
/*     */         case 8196:
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 265 */       break;
/*     */     case 2:
/* 267 */       switch (this.boundKind)
/*     */       {
/*     */       case 0:
/* 286 */         break;
/*     */       case 1:
/* 288 */         switch (actualType.kind()) {
/*     */         case 516:
/* 290 */           WildcardBinding actualWildcard = (WildcardBinding)actualType;
/* 291 */           switch (actualWildcard.boundKind) {
/*     */           case 0:
/* 293 */             break;
/*     */           case 1:
/* 295 */             this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 2);
/* 296 */             int i = 0; for (int length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length; i < length; i++) {
/* 297 */               this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, 2);
/*     */             }
/*     */ 
/*     */           case 2:
/*     */           }
/*     */ 
/* 303 */           break;
/*     */         case 8196:
/* 305 */           WildcardBinding actualIntersection = (WildcardBinding)actualType;
/* 306 */           this.bound.collectSubstitutes(scope, actualIntersection.bound, inferenceContext, 2);
/* 307 */           int i = 0; for (int length = actualIntersection.otherBounds == null ? 0 : actualIntersection.otherBounds.length; i < length; i++) {
/* 308 */             this.bound.collectSubstitutes(scope, actualIntersection.otherBounds[i], inferenceContext, 2);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 314 */         break;
/*     */       case 2:
/* 316 */         switch (actualType.kind()) {
/*     */         case 516:
/* 318 */           WildcardBinding actualWildcard = (WildcardBinding)actualType;
/* 319 */           switch (actualWildcard.boundKind) {
/*     */           case 0:
/* 321 */             break;
/*     */           case 1:
/* 323 */             break;
/*     */           case 2:
/* 325 */             this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 2);
/* 326 */             int i = 0; for (int length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length; i < length; i++) {
/* 327 */               this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, 2);
/*     */             }
/*     */           }
/*     */         case 8196:
/*     */         }
/*     */       }
/* 333 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf)
/*     */   {
/* 348 */     char[] genericTypeKey = this.genericType.computeUniqueKey(false);
/*     */ 
/* 351 */     char[] rankComponent = ('{' + String.valueOf(this.rank) + '}').toCharArray();
/*     */     char[] wildCardKey;
/*     */     char[] wildCardKey;
/*     */     char[] wildCardKey;
/* 352 */     switch (this.boundKind) {
/*     */     case 0:
/* 354 */       wildCardKey = TypeConstants.WILDCARD_STAR;
/* 355 */       break;
/*     */     case 1:
/* 357 */       wildCardKey = CharOperation.concat(TypeConstants.WILDCARD_PLUS, this.bound.computeUniqueKey(false));
/* 358 */       break;
/*     */     default:
/* 360 */       wildCardKey = CharOperation.concat(TypeConstants.WILDCARD_MINUS, this.bound.computeUniqueKey(false));
/*     */     }
/*     */ 
/* 363 */     return CharOperation.concat(genericTypeKey, rankComponent, wildCardKey);
/*     */   }
/*     */ 
/*     */   public char[] constantPoolName()
/*     */   {
/* 372 */     return erasure().constantPoolName();
/*     */   }
/*     */ 
/*     */   public String debugName()
/*     */   {
/* 379 */     return toString();
/*     */   }
/*     */ 
/*     */   public TypeBinding erasure()
/*     */   {
/* 386 */     if (this.otherBounds == null) {
/* 387 */       if (this.boundKind == 1)
/* 388 */         return this.bound.erasure();
/* 389 */       return typeVariable().erasure();
/*     */     }
/*     */ 
/* 392 */     return this.bound.id == 1 ? 
/* 393 */       this.otherBounds[0].erasure() : 
/* 394 */       this.bound.erasure();
/*     */   }
/*     */ 
/*     */   public char[] genericTypeSignature()
/*     */   {
/* 401 */     if (this.genericSignature == null) {
/* 402 */       switch (this.boundKind) {
/*     */       case 0:
/* 404 */         this.genericSignature = TypeConstants.WILDCARD_STAR;
/* 405 */         break;
/*     */       case 1:
/* 407 */         this.genericSignature = CharOperation.concat(TypeConstants.WILDCARD_PLUS, this.bound.genericTypeSignature());
/* 408 */         break;
/*     */       default:
/* 410 */         this.genericSignature = CharOperation.concat(TypeConstants.WILDCARD_MINUS, this.bound.genericTypeSignature());
/*     */       }
/*     */     }
/* 413 */     return this.genericSignature;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 417 */     return this.genericType.hashCode();
/*     */   }
/*     */ 
/*     */   void initialize(ReferenceBinding someGenericType, TypeBinding someBound, TypeBinding[] someOtherBounds) {
/* 421 */     this.genericType = someGenericType;
/* 422 */     this.bound = someBound;
/* 423 */     this.otherBounds = someOtherBounds;
/* 424 */     if (someGenericType != null) {
/* 425 */       this.fPackage = someGenericType.getPackage();
/*     */     }
/* 427 */     if (someBound != null) {
/* 428 */       this.tagBits |= someBound.tagBits & 0x20000880;
/*     */     }
/* 430 */     if (someOtherBounds != null) {
/* 431 */       int i = 0; for (int max = someOtherBounds.length; i < max; i++) {
/* 432 */         TypeBinding someOtherBound = someOtherBounds[i];
/* 433 */         this.tagBits |= someOtherBound.tagBits & 0x800;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isSuperclassOf(ReferenceBinding otherType)
/*     */   {
/* 442 */     if (this.boundKind == 2) {
/* 443 */       if ((this.bound instanceof ReferenceBinding)) {
/* 444 */         return ((ReferenceBinding)this.bound).isSuperclassOf(otherType);
/*     */       }
/* 446 */       return otherType.id == 1;
/*     */     }
/*     */ 
/* 449 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isIntersectionType()
/*     */   {
/* 456 */     return this.otherBounds != null;
/*     */   }
/*     */ 
/*     */   public boolean isHierarchyConnected() {
/* 460 */     return (this.superclass != null) && (this.superInterfaces != null);
/*     */   }
/*     */ 
/*     */   public boolean isUnboundWildcard()
/*     */   {
/* 467 */     return this.boundKind == 0;
/*     */   }
/*     */ 
/*     */   public boolean isWildcard()
/*     */   {
/* 474 */     return true;
/*     */   }
/*     */ 
/*     */   public char[] readableName()
/*     */   {
/* 481 */     switch (this.boundKind) {
/*     */     case 0:
/* 483 */       return TypeConstants.WILDCARD_NAME;
/*     */     case 1:
/* 485 */       if (this.otherBounds == null)
/* 486 */         return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.readableName());
/* 487 */       StringBuffer buffer = new StringBuffer(10);
/* 488 */       buffer.append(this.bound.readableName());
/* 489 */       int i = 0; for (int length = this.otherBounds.length; i < length; i++)
/* 490 */         buffer.append('&').append(this.otherBounds[i].readableName());
/*     */       int length;
/* 493 */       char[] result = new char[length = buffer.length()];
/* 494 */       buffer.getChars(0, length, result, 0);
/* 495 */       return result;
/*     */     }
/* 497 */     return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.readableName());
/*     */   }
/*     */ 
/*     */   ReferenceBinding resolve()
/*     */   {
/* 502 */     if ((this.tagBits & 0x1000000) == 0L) {
/* 503 */       return this;
/*     */     }
/* 505 */     this.tagBits &= -16777217L;
/* 506 */     BinaryTypeBinding.resolveType(this.genericType, this.environment, false);
/* 507 */     switch (this.boundKind) {
/*     */     case 1:
/* 509 */       TypeBinding resolveType = BinaryTypeBinding.resolveType(this.bound, this.environment, true);
/* 510 */       this.bound = resolveType;
/* 511 */       this.tagBits |= resolveType.tagBits & 0x800;
/* 512 */       int i = 0; for (int length = this.otherBounds == null ? 0 : this.otherBounds.length; i < length; i++) {
/* 513 */         resolveType = BinaryTypeBinding.resolveType(this.otherBounds[i], this.environment, true);
/* 514 */         this.otherBounds[i] = resolveType;
/* 515 */         this.tagBits |= resolveType.tagBits & 0x800;
/*     */       }
/* 517 */       break;
/*     */     case 2:
/* 519 */       TypeBinding resolveType = BinaryTypeBinding.resolveType(this.bound, this.environment, true);
/* 520 */       this.bound = resolveType;
/* 521 */       this.tagBits |= resolveType.tagBits & 0x800;
/*     */     case 0:
/*     */     }
/*     */ 
/* 525 */     return this;
/*     */   }
/*     */ 
/*     */   public char[] shortReadableName()
/*     */   {
/* 532 */     switch (this.boundKind) {
/*     */     case 0:
/* 534 */       return TypeConstants.WILDCARD_NAME;
/*     */     case 1:
/* 536 */       if (this.otherBounds == null)
/* 537 */         return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.shortReadableName());
/* 538 */       StringBuffer buffer = new StringBuffer(10);
/* 539 */       buffer.append(this.bound.shortReadableName());
/* 540 */       int i = 0; for (int length = this.otherBounds.length; i < length; i++)
/* 541 */         buffer.append('&').append(this.otherBounds[i].shortReadableName());
/*     */       int length;
/* 544 */       char[] result = new char[length = buffer.length()];
/* 545 */       buffer.getChars(0, length, result, 0);
/* 546 */       return result;
/*     */     }
/* 548 */     return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.shortReadableName());
/*     */   }
/*     */ 
/*     */   public char[] signature()
/*     */   {
/* 558 */     if (this.signature == null) {
/* 559 */       switch (this.boundKind) {
/*     */       case 1:
/* 561 */         return this.bound.signature();
/*     */       }
/* 563 */       return typeVariable().signature();
/*     */     }
/*     */ 
/* 566 */     return this.signature;
/*     */   }
/*     */ 
/*     */   public char[] sourceName()
/*     */   {
/* 573 */     switch (this.boundKind) {
/*     */     case 0:
/* 575 */       return TypeConstants.WILDCARD_NAME;
/*     */     case 1:
/* 577 */       return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.sourceName());
/*     */     }
/* 579 */     return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.sourceName());
/*     */   }
/*     */ 
/*     */   public ReferenceBinding superclass()
/*     */   {
/* 587 */     if (this.superclass == null) {
/* 588 */       TypeBinding superType = null;
/* 589 */       if ((this.boundKind == 1) && (!this.bound.isInterface())) {
/* 590 */         superType = this.bound;
/*     */       } else {
/* 592 */         TypeVariableBinding variable = typeVariable();
/* 593 */         if (variable != null) superType = variable.firstBound;
/*     */       }
/* 595 */       this.superclass = (((superType instanceof ReferenceBinding)) && (!superType.isInterface()) ? 
/* 596 */         (ReferenceBinding)superType : 
/* 597 */         this.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null));
/*     */     }
/*     */ 
/* 600 */     return this.superclass;
/*     */   }
/*     */ 
/*     */   public ReferenceBinding[] superInterfaces()
/*     */   {
/* 607 */     if (this.superInterfaces == null) {
/* 608 */       if (typeVariable() != null)
/* 609 */         this.superInterfaces = this.typeVariable.superInterfaces();
/*     */       else {
/* 611 */         this.superInterfaces = Binding.NO_SUPERINTERFACES;
/*     */       }
/* 613 */       if (this.boundKind == 1) {
/* 614 */         if (this.bound.isInterface())
/*     */         {
/* 616 */           int length = this.superInterfaces.length;
/* 617 */           System.arraycopy(this.superInterfaces, 0, this.superInterfaces = new ReferenceBinding[length + 1], 1, length);
/* 618 */           this.superInterfaces[0] = ((ReferenceBinding)this.bound);
/*     */         }
/* 620 */         if (this.otherBounds != null)
/*     */         {
/* 622 */           int length = this.superInterfaces.length;
/* 623 */           int otherLength = this.otherBounds.length;
/* 624 */           System.arraycopy(this.superInterfaces, 0, this.superInterfaces = new ReferenceBinding[length + otherLength], 0, length);
/* 625 */           for (int i = 0; i < otherLength; i++) {
/* 626 */             this.superInterfaces[(length + i)] = ((ReferenceBinding)this.otherBounds[i]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 631 */     return this.superInterfaces;
/*     */   }
/*     */ 
/*     */   public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType, LookupEnvironment env) {
/* 635 */     boolean affected = false;
/* 636 */     if (this.genericType == unresolvedType) {
/* 637 */       this.genericType = resolvedType;
/* 638 */       affected = true;
/*     */     }
/* 640 */     if (this.bound == unresolvedType) {
/* 641 */       this.bound = env.convertUnresolvedBinaryToRawType(resolvedType);
/* 642 */       affected = true;
/*     */     }
/* 644 */     if (this.otherBounds != null) {
/* 645 */       int i = 0; for (int length = this.otherBounds.length; i < length; i++) {
/* 646 */         if (this.otherBounds[i] == unresolvedType) {
/* 647 */           this.otherBounds[i] = env.convertUnresolvedBinaryToRawType(resolvedType);
/* 648 */           affected = true;
/*     */         }
/*     */       }
/*     */     }
/* 652 */     if (affected)
/* 653 */       initialize(this.genericType, this.bound, this.otherBounds);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 660 */     switch (this.boundKind) {
/*     */     case 0:
/* 662 */       return new String(TypeConstants.WILDCARD_NAME);
/*     */     case 1:
/* 664 */       if (this.otherBounds == null)
/* 665 */         return new String(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.debugName().toCharArray()));
/* 666 */       StringBuffer buffer = new StringBuffer(this.bound.debugName());
/* 667 */       int i = 0; for (int length = this.otherBounds.length; i < length; i++) {
/* 668 */         buffer.append('&').append(this.otherBounds[i].debugName());
/*     */       }
/* 670 */       return buffer.toString();
/*     */     }
/* 672 */     return new String(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.debugName().toCharArray()));
/*     */   }
/*     */ 
/*     */   public TypeVariableBinding typeVariable()
/*     */   {
/* 679 */     if (this.typeVariable == null) {
/* 680 */       TypeVariableBinding[] typeVariables = this.genericType.typeVariables();
/* 681 */       if (this.rank < typeVariables.length)
/* 682 */         this.typeVariable = typeVariables[this.rank];
/*     */     }
/* 684 */     return this.typeVariable;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.WildcardBinding
 * JD-Core Version:    0.6.0
 */