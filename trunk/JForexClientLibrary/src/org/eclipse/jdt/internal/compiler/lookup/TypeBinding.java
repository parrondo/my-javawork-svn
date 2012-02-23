/*      */ package org.eclipse.jdt.internal.compiler.lookup;
/*      */ 
/*      */ import java.util.List;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ 
/*      */ public abstract class TypeBinding extends Binding
/*      */ {
/*   30 */   public int id = 2147483647;
/*   31 */   public long tagBits = 0L;
/*      */ 
/*   35 */   public static final BaseTypeBinding INT = new BaseTypeBinding(
/*   36 */     10, TypeConstants.INT, new char[] { 'I' });
/*      */ 
/*   38 */   public static final BaseTypeBinding BYTE = new BaseTypeBinding(
/*   39 */     3, TypeConstants.BYTE, new char[] { 'B' });
/*      */ 
/*   41 */   public static final BaseTypeBinding SHORT = new BaseTypeBinding(
/*   42 */     4, TypeConstants.SHORT, new char[] { 'S' });
/*      */ 
/*   44 */   public static final BaseTypeBinding CHAR = new BaseTypeBinding(
/*   45 */     2, TypeConstants.CHAR, new char[] { 'C' });
/*      */ 
/*   47 */   public static final BaseTypeBinding LONG = new BaseTypeBinding(
/*   48 */     7, TypeConstants.LONG, new char[] { 'J' });
/*      */ 
/*   50 */   public static final BaseTypeBinding FLOAT = new BaseTypeBinding(
/*   51 */     9, TypeConstants.FLOAT, new char[] { 'F' });
/*      */ 
/*   53 */   public static final BaseTypeBinding DOUBLE = new BaseTypeBinding(
/*   54 */     8, TypeConstants.DOUBLE, new char[] { 'D' });
/*      */ 
/*   56 */   public static final BaseTypeBinding BOOLEAN = new BaseTypeBinding(
/*   57 */     5, TypeConstants.BOOLEAN, new char[] { 'Z' });
/*      */ 
/*   59 */   public static final BaseTypeBinding NULL = new BaseTypeBinding(
/*   60 */     12, TypeConstants.NULL, new char[] { 'N' });
/*      */ 
/*   62 */   public static final BaseTypeBinding VOID = new BaseTypeBinding(
/*   63 */     6, TypeConstants.VOID, new char[] { 'V' });
/*      */ 
/*      */   public static final TypeBinding wellKnownType(Scope scope, int id)
/*      */   {
/*   69 */     switch (id) {
/*      */     case 5:
/*   71 */       return BOOLEAN;
/*      */     case 3:
/*   73 */       return BYTE;
/*      */     case 2:
/*   75 */       return CHAR;
/*      */     case 4:
/*   77 */       return SHORT;
/*      */     case 8:
/*   79 */       return DOUBLE;
/*      */     case 9:
/*   81 */       return FLOAT;
/*      */     case 10:
/*   83 */       return INT;
/*      */     case 7:
/*   85 */       return LONG;
/*      */     case 1:
/*   87 */       return scope.getJavaLangObject();
/*      */     case 11:
/*   89 */       return scope.getJavaLangString();
/*      */     case 6:
/*   91 */     }return null;
/*      */   }
/*      */ 
/*      */   public boolean canBeInstantiated()
/*      */   {
/*   98 */     return !isBaseType();
/*      */   }
/*      */ 
/*      */   public TypeBinding capture(Scope scope, int position)
/*      */   {
/*  105 */     return this;
/*      */   }
/*      */ 
/*      */   public TypeBinding closestMatch()
/*      */   {
/*  113 */     return this;
/*      */   }
/*      */ 
/*      */   public List collectMissingTypes(List missingTypes)
/*      */   {
/*  122 */     return missingTypes;
/*      */   }
/*      */ 
/*      */   public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint)
/*      */   {
/*      */   }
/*      */ 
/*      */   public abstract char[] constantPoolName();
/*      */ 
/*      */   public String debugName()
/*      */   {
/*  145 */     return new String(readableName());
/*      */   }
/*      */ 
/*      */   public int dimensions()
/*      */   {
/*  152 */     return 0;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding enclosingType()
/*      */   {
/*  158 */     return null;
/*      */   }
/*      */ 
/*      */   public TypeBinding erasure() {
/*  162 */     return this;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding findSuperTypeOriginatingFrom(int wellKnownOriginalID, boolean originalIsClass)
/*      */   {
/*  174 */     if (!(this instanceof ReferenceBinding)) return null;
/*  175 */     ReferenceBinding reference = (ReferenceBinding)this;
/*      */ 
/*  178 */     if ((reference.id == wellKnownOriginalID) || (original().id == wellKnownOriginalID)) return reference;
/*      */ 
/*  180 */     ReferenceBinding currentType = reference;
/*      */ 
/*  182 */     if (originalIsClass) {
/*  183 */       while ((currentType = currentType.superclass()) != null) {
/*  184 */         if (currentType.id == wellKnownOriginalID)
/*  185 */           return currentType;
/*  186 */         if (currentType.original().id == wellKnownOriginalID)
/*  187 */           return currentType;
/*      */       }
/*  189 */       return null;
/*      */     }
/*  191 */     ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/*  192 */     int nextPosition = 0;
/*      */     do {
/*  194 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  195 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES))
/*  196 */         if (interfacesToVisit == null) {
/*  197 */           interfacesToVisit = itsInterfaces;
/*  198 */           nextPosition = interfacesToVisit.length;
/*      */         } else {
/*  200 */           int itsLength = itsInterfaces.length;
/*  201 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/*  202 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  203 */           for (int a = 0; a < itsLength; a++) {
/*  204 */             ReferenceBinding next = itsInterfaces[a];
/*  205 */             int b = 0;
/*  206 */             while (next != interfacesToVisit[b])
/*      */             {
/*  205 */               b++; if (b < nextPosition)
/*      */                 continue;
/*  207 */               interfacesToVisit[(nextPosition++)] = next;
/*      */             }
/*      */           }
/*      */         }
/*      */     }
/*  211 */     while ((currentType = currentType.superclass()) != null);
/*      */ 
/*  213 */     for (int i = 0; i < nextPosition; i++) {
/*  214 */       currentType = interfacesToVisit[i];
/*  215 */       if (currentType.id == wellKnownOriginalID)
/*  216 */         return currentType;
/*  217 */       if (currentType.original().id == wellKnownOriginalID)
/*  218 */         return currentType;
/*  219 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  220 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/*  221 */         int itsLength = itsInterfaces.length;
/*  222 */         if (nextPosition + itsLength >= interfacesToVisit.length)
/*  223 */           System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  224 */         for (int a = 0; a < itsLength; a++) {
/*  225 */           ReferenceBinding next = itsInterfaces[a];
/*  226 */           int b = 0;
/*  227 */           while (next != interfacesToVisit[b])
/*      */           {
/*  226 */             b++; if (b < nextPosition)
/*      */               continue;
/*  228 */             interfacesToVisit[(nextPosition++)] = next;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  232 */     return null;
/*      */   }
/*      */ 
/*      */   public TypeBinding findSuperTypeOriginatingFrom(TypeBinding otherType)
/*      */   {
/*  239 */     if (this == otherType) return this;
/*  240 */     if (otherType == null) return null;
/*  241 */     switch (kind()) {
/*      */     case 68:
/*  243 */       ArrayBinding arrayType = (ArrayBinding)this;
/*  244 */       int otherDim = otherType.dimensions();
/*  245 */       if (arrayType.dimensions != otherDim) {
/*  246 */         switch (otherType.id) {
/*      */         case 1:
/*      */         case 36:
/*      */         case 37:
/*  250 */           return otherType;
/*      */         }
/*  252 */         if ((otherDim < arrayType.dimensions) && (otherType.leafComponentType().id == 1)) {
/*  253 */           return otherType;
/*      */         }
/*  255 */         return null;
/*      */       }
/*  257 */       if (!(arrayType.leafComponentType instanceof ReferenceBinding)) return null;
/*  258 */       TypeBinding leafSuperType = arrayType.leafComponentType.findSuperTypeOriginatingFrom(otherType.leafComponentType());
/*  259 */       if (leafSuperType == null) return null;
/*  260 */       return arrayType.environment().createArrayType(leafSuperType, arrayType.dimensions);
/*      */     case 4100:
/*  263 */       if (!isCapture()) break;
/*  264 */       CaptureBinding capture = (CaptureBinding)this;
/*  265 */       TypeBinding captureBound = capture.firstBound;
/*  266 */       if (!(captureBound instanceof ArrayBinding)) break;
/*  267 */       TypeBinding match = captureBound.findSuperTypeOriginatingFrom(otherType);
/*  268 */       if (match == null) break; return match;
/*      */     case 4:
/*      */     case 260:
/*      */     case 516:
/*      */     case 1028:
/*      */     case 2052:
/*      */     case 8196:
/*  279 */       otherType = otherType.original();
/*  280 */       if (this == otherType)
/*  281 */         return this;
/*  282 */       if (original() == otherType)
/*  283 */         return this;
/*  284 */       ReferenceBinding currentType = (ReferenceBinding)this;
/*  285 */       if (!otherType.isInterface()) {
/*  286 */         while ((currentType = currentType.superclass()) != null) {
/*  287 */           if (currentType == otherType)
/*  288 */             return currentType;
/*  289 */           if (currentType.original() == otherType)
/*  290 */             return currentType;
/*      */         }
/*  292 */         return null;
/*      */       }
/*  294 */       ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/*  295 */       int nextPosition = 0;
/*      */       do {
/*  297 */         ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  298 */         if (itsInterfaces != Binding.NO_SUPERINTERFACES)
/*  299 */           if (interfacesToVisit == null) {
/*  300 */             interfacesToVisit = itsInterfaces;
/*  301 */             nextPosition = interfacesToVisit.length;
/*      */           } else {
/*  303 */             int itsLength = itsInterfaces.length;
/*  304 */             if (nextPosition + itsLength >= interfacesToVisit.length)
/*  305 */               System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  306 */             for (int a = 0; a < itsLength; a++) {
/*  307 */               ReferenceBinding next = itsInterfaces[a];
/*  308 */               int b = 0;
/*  309 */               while (next != interfacesToVisit[b])
/*      */               {
/*  308 */                 b++; if (b < nextPosition)
/*      */                   continue;
/*  310 */                 interfacesToVisit[(nextPosition++)] = next;
/*      */               }
/*      */             }
/*      */           }
/*      */       }
/*  314 */       while ((currentType = currentType.superclass()) != null);
/*      */ 
/*  316 */       for (int i = 0; i < nextPosition; i++) {
/*  317 */         currentType = interfacesToVisit[i];
/*  318 */         if (currentType == otherType)
/*  319 */           return currentType;
/*  320 */         if (currentType.original() == otherType)
/*  321 */           return currentType;
/*  322 */         ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  323 */         if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
/*  324 */           int itsLength = itsInterfaces.length;
/*  325 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/*  326 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  327 */           for (int a = 0; a < itsLength; a++) {
/*  328 */             ReferenceBinding next = itsInterfaces[a];
/*  329 */             int b = 0;
/*  330 */             while (next != interfacesToVisit[b])
/*      */             {
/*  329 */               b++; if (b < nextPosition)
/*      */                 continue;
/*  331 */               interfacesToVisit[(nextPosition++)] = next;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  336 */     return null;
/*      */   }
/*      */ 
/*      */   public TypeBinding genericCast(TypeBinding targetType)
/*      */   {
/*  343 */     if (this == targetType)
/*  344 */       return null;
/*  345 */     TypeBinding targetErasure = targetType.erasure();
/*      */ 
/*  347 */     if (erasure().findSuperTypeOriginatingFrom(targetErasure) != null)
/*  348 */       return null;
/*  349 */     return targetErasure;
/*      */   }
/*      */ 
/*      */   public char[] genericTypeSignature()
/*      */   {
/*  358 */     return signature();
/*      */   }
/*      */ 
/*      */   public TypeBinding getErasureCompatibleType(TypeBinding declaringClass)
/*      */   {
/*  370 */     switch (kind()) {
/*      */     case 4100:
/*  372 */       TypeVariableBinding variable = (TypeVariableBinding)this;
/*  373 */       if (variable.erasure().findSuperTypeOriginatingFrom(declaringClass) != null) {
/*  374 */         return this;
/*      */       }
/*  376 */       if ((variable.superclass != null) && (variable.superclass.findSuperTypeOriginatingFrom(declaringClass) != null)) {
/*  377 */         return variable.superclass.getErasureCompatibleType(declaringClass);
/*      */       }
/*  379 */       int i = 0; for (int otherLength = variable.superInterfaces.length; i < otherLength; i++) {
/*  380 */         ReferenceBinding superInterface = variable.superInterfaces[i];
/*  381 */         if (superInterface.findSuperTypeOriginatingFrom(declaringClass) != null) {
/*  382 */           return superInterface.getErasureCompatibleType(declaringClass);
/*      */         }
/*      */       }
/*  385 */       return this;
/*      */     case 8196:
/*  387 */       WildcardBinding intersection = (WildcardBinding)this;
/*  388 */       if (intersection.erasure().findSuperTypeOriginatingFrom(declaringClass) != null) {
/*  389 */         return this;
/*      */       }
/*  391 */       if ((intersection.superclass != null) && (intersection.superclass.findSuperTypeOriginatingFrom(declaringClass) != null)) {
/*  392 */         return intersection.superclass.getErasureCompatibleType(declaringClass);
/*      */       }
/*  394 */       int i = 0; for (int otherLength = intersection.superInterfaces.length; i < otherLength; i++) {
/*  395 */         ReferenceBinding superInterface = intersection.superInterfaces[i];
/*  396 */         if (superInterface.findSuperTypeOriginatingFrom(declaringClass) != null) {
/*  397 */           return superInterface.getErasureCompatibleType(declaringClass);
/*      */         }
/*      */       }
/*  400 */       return this;
/*      */     }
/*  402 */     return this;
/*      */   }
/*      */ 
/*      */   public abstract PackageBinding getPackage();
/*      */ 
/*      */   void initializeForStaticImports()
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean isAnnotationType() {
/*  413 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isAnonymousType() {
/*  417 */     return (this.tagBits & 0x20) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isArrayType()
/*      */   {
/*  423 */     return (this.tagBits & 1L) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isBaseType()
/*      */   {
/*  429 */     return (this.tagBits & 0x2) != 0L;
/*      */   }
/*      */ 
/*      */   public boolean isBoundParameterizedType()
/*      */   {
/*  436 */     return (this.tagBits & 0x800000) != 0L;
/*      */   }
/*      */ 
/*      */   public boolean isCapture()
/*      */   {
/*  443 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isClass() {
/*  447 */     return false;
/*      */   }
/*      */ 
/*      */   public abstract boolean isCompatibleWith(TypeBinding paramTypeBinding);
/*      */ 
/*      */   public boolean isEnum()
/*      */   {
/*  455 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isEquivalentTo(TypeBinding otherType)
/*      */   {
/*  463 */     if (this == otherType)
/*  464 */       return true;
/*  465 */     if (otherType == null)
/*  466 */       return false;
/*  467 */     switch (otherType.kind()) {
/*      */     case 516:
/*      */     case 8196:
/*  470 */       return ((WildcardBinding)otherType).boundCheck(this);
/*      */     }
/*  472 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isGenericType() {
/*  476 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isHierarchyInconsistent()
/*      */   {
/*  482 */     return (this.tagBits & 0x20000) != 0L;
/*      */   }
/*      */ 
/*      */   public boolean isInterface() {
/*  486 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isIntersectionType()
/*      */   {
/*  493 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isLocalType() {
/*  497 */     return (this.tagBits & 0x10) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isMemberType() {
/*  501 */     return (this.tagBits & 0x8) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isNestedType() {
/*  505 */     return (this.tagBits & 0x4) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isNumericType() {
/*  509 */     switch (this.id) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*  517 */       return true;
/*      */     case 5:
/*  519 */     case 6: } return false;
/*      */   }
/*      */ 
/*      */   public final boolean isParameterizedType()
/*      */   {
/*  529 */     return kind() == 260;
/*      */   }
/*      */ 
/*      */   public final boolean isParameterizedTypeWithActualArguments()
/*      */   {
/*  540 */     return (kind() == 260) && 
/*  540 */       (((ParameterizedTypeBinding)this).arguments != null);
/*      */   }
/*      */ 
/*      */   public boolean isParameterizedWithOwnVariables()
/*      */   {
/*  547 */     if (kind() != 260)
/*  548 */       return false;
/*  549 */     ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)this;
/*  550 */     if (paramType.arguments == null)
/*  551 */       return false;
/*  552 */     TypeVariableBinding[] variables = erasure().typeVariables();
/*  553 */     int i = 0; for (int length = variables.length; i < length; i++) {
/*  554 */       if (variables[i] != paramType.arguments[i])
/*  555 */         return false;
/*      */     }
/*  557 */     ReferenceBinding enclosing = paramType.enclosingType();
/*      */ 
/*  560 */     return (enclosing == null) || (!enclosing.erasure().isGenericType()) || 
/*  559 */       (enclosing.isParameterizedWithOwnVariables());
/*      */   }
/*      */ 
/*      */   private boolean isProvableDistinctSubType(TypeBinding otherType)
/*      */   {
/*  566 */     if (otherType.isInterface()) {
/*  567 */       if (isInterface())
/*  568 */         return false;
/*  569 */       if ((isArrayType()) || 
/*  570 */         (((this instanceof ReferenceBinding)) && (((ReferenceBinding)this).isFinal())) || (
/*  571 */         (isTypeVariable()) && (((TypeVariableBinding)this).superclass().isFinal()))) {
/*  572 */         return !isCompatibleWith(otherType);
/*      */       }
/*  574 */       return false;
/*      */     }
/*  576 */     if (isInterface()) {
/*  577 */       if ((otherType.isArrayType()) || 
/*  578 */         (((otherType instanceof ReferenceBinding)) && (((ReferenceBinding)otherType).isFinal())) || (
/*  579 */         (otherType.isTypeVariable()) && (((TypeVariableBinding)otherType).superclass().isFinal()))) {
/*  580 */         return !isCompatibleWith(otherType);
/*      */       }
/*      */     }
/*  583 */     else if ((!isTypeVariable()) && (!otherType.isTypeVariable())) {
/*  584 */       return !isCompatibleWith(otherType);
/*      */     }
/*      */ 
/*  588 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isProvablyDistinct(TypeBinding otherType)
/*      */   {
/*  595 */     if (this == otherType)
/*  596 */       return false;
/*  597 */     if (otherType == null) {
/*  598 */       return true;
/*      */     }
/*  600 */     switch (kind())
/*      */     {
/*      */     case 260:
/*  603 */       ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)this;
/*  604 */       switch (otherType.kind()) {
/*      */       case 260:
/*  606 */         ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding)otherType;
/*  607 */         if (paramType.genericType() != otherParamType.genericType())
/*  608 */           return true;
/*  609 */         if (!paramType.isStatic()) {
/*  610 */           ReferenceBinding enclosing = enclosingType();
/*  611 */           if (enclosing != null) {
/*  612 */             ReferenceBinding otherEnclosing = otherParamType.enclosingType();
/*  613 */             if (otherEnclosing == null) return true;
/*  614 */             if ((otherEnclosing.tagBits & 0x40000000) == 0L) {
/*  615 */               if (enclosing != otherEnclosing) return true;
/*      */             }
/*  617 */             else if (!enclosing.isEquivalentTo(otherParamType.enclosingType())) return true;
/*      */           }
/*      */         }
/*      */ 
/*  621 */         int length = paramType.arguments == null ? 0 : paramType.arguments.length;
/*  622 */         TypeBinding[] otherArguments = otherParamType.arguments;
/*  623 */         int otherLength = otherArguments == null ? 0 : otherArguments.length;
/*  624 */         if (otherLength != length)
/*  625 */           return true;
/*  626 */         for (int i = 0; i < length; i++) {
/*  627 */           if (paramType.arguments[i].isProvablyDistinctTypeArgument(otherArguments[i], paramType, i))
/*  628 */             return true;
/*      */         }
/*  630 */         return false;
/*      */       case 2052:
/*  633 */         SourceTypeBinding otherGenericType = (SourceTypeBinding)otherType;
/*  634 */         if (paramType.genericType() != otherGenericType)
/*  635 */           return true;
/*  636 */         if (!paramType.isStatic()) {
/*  637 */           ReferenceBinding enclosing = enclosingType();
/*  638 */           if (enclosing != null) {
/*  639 */             ReferenceBinding otherEnclosing = otherGenericType.enclosingType();
/*  640 */             if (otherEnclosing == null) return true;
/*  641 */             if ((otherEnclosing.tagBits & 0x40000000) == 0L) {
/*  642 */               if (enclosing != otherEnclosing) return true;
/*      */             }
/*  644 */             else if (!enclosing.isEquivalentTo(otherGenericType.enclosingType())) return true;
/*      */           }
/*      */         }
/*      */ 
/*  648 */         int length = paramType.arguments == null ? 0 : paramType.arguments.length;
/*  649 */         TypeBinding[] otherArguments = otherGenericType.typeVariables();
/*  650 */         int otherLength = otherArguments == null ? 0 : otherArguments.length;
/*  651 */         if (otherLength != length)
/*  652 */           return true;
/*  653 */         for (int i = 0; i < length; i++) {
/*  654 */           if (paramType.arguments[i].isProvablyDistinctTypeArgument(otherArguments[i], paramType, i))
/*  655 */             return true;
/*      */         }
/*  657 */         return false;
/*      */       case 1028:
/*  660 */         return erasure() != otherType.erasure();
/*      */       }
/*  662 */       return true;
/*      */     case 1028:
/*  666 */       switch (otherType.kind())
/*      */       {
/*      */       case 260:
/*      */       case 1028:
/*      */       case 2052:
/*  671 */         return erasure() != otherType.erasure();
/*      */       }
/*  673 */       return true;
/*      */     }
/*      */ 
/*  678 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean isProvablyDistinctTypeArgument(TypeBinding otherArgument, ParameterizedTypeBinding paramType, int rank)
/*      */   {
/*  688 */     if (this == otherArgument) {
/*  689 */       return false;
/*      */     }
/*  691 */     TypeBinding upperBound1 = null;
/*  692 */     TypeBinding lowerBound1 = null;
/*  693 */     switch (kind()) {
/*      */     case 516:
/*  695 */       WildcardBinding wildcard = (WildcardBinding)this;
/*  696 */       switch (wildcard.boundKind) {
/*      */       case 1:
/*  698 */         upperBound1 = wildcard.bound;
/*  699 */         break;
/*      */       case 2:
/*  701 */         lowerBound1 = wildcard.bound;
/*  702 */         break;
/*      */       case 0:
/*  704 */         return false;
/*      */       }
/*  706 */       break;
/*      */     case 8196:
/*  708 */       break;
/*      */     case 4100:
/*  710 */       TypeVariableBinding variable = (TypeVariableBinding)this;
/*  711 */       if (variable.isCapture()) {
/*  712 */         CaptureBinding capture = (CaptureBinding)variable;
/*  713 */         switch (capture.wildcard.boundKind) {
/*      */         case 1:
/*  715 */           upperBound1 = capture.wildcard.bound;
/*  716 */           break;
/*      */         case 2:
/*  718 */           lowerBound1 = capture.wildcard.bound;
/*  719 */           break;
/*      */         case 0:
/*  721 */           return false;
/*      */         default:
/*  723 */           break;
/*      */         }
/*      */       } else {
/*  725 */         if (variable.firstBound == null)
/*  726 */           return false;
/*  727 */         TypeBinding eliminatedType = Scope.convertEliminatingTypeVariables(variable, paramType.genericType(), rank, null);
/*  728 */         switch (eliminatedType.kind()) {
/*      */         case 516:
/*      */         case 8196:
/*  731 */           WildcardBinding wildcard = (WildcardBinding)eliminatedType;
/*  732 */           switch (wildcard.boundKind) {
/*      */           case 1:
/*  734 */             upperBound1 = wildcard.bound;
/*  735 */             break;
/*      */           case 2:
/*  737 */             lowerBound1 = wildcard.bound;
/*  738 */             break;
/*      */           case 0:
/*  740 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  746 */     TypeBinding upperBound2 = null;
/*  747 */     TypeBinding lowerBound2 = null;
/*  748 */     switch (otherArgument.kind()) {
/*      */     case 516:
/*  750 */       WildcardBinding otherWildcard = (WildcardBinding)otherArgument;
/*  751 */       switch (otherWildcard.boundKind) {
/*      */       case 1:
/*  753 */         upperBound2 = otherWildcard.bound;
/*  754 */         break;
/*      */       case 2:
/*  756 */         lowerBound2 = otherWildcard.bound;
/*  757 */         break;
/*      */       case 0:
/*  759 */         return false;
/*      */       }
/*  761 */       break;
/*      */     case 8196:
/*  763 */       break;
/*      */     case 4100:
/*  765 */       TypeVariableBinding otherVariable = (TypeVariableBinding)otherArgument;
/*  766 */       if (otherVariable.isCapture()) {
/*  767 */         CaptureBinding otherCapture = (CaptureBinding)otherVariable;
/*  768 */         switch (otherCapture.wildcard.boundKind) {
/*      */         case 1:
/*  770 */           upperBound2 = otherCapture.wildcard.bound;
/*  771 */           break;
/*      */         case 2:
/*  773 */           lowerBound2 = otherCapture.wildcard.bound;
/*  774 */           break;
/*      */         case 0:
/*  776 */           return false;
/*      */         default:
/*  778 */           break;
/*      */         }
/*      */       } else {
/*  780 */         if (otherVariable.firstBound == null)
/*  781 */           return false;
/*  782 */         TypeBinding otherEliminatedType = Scope.convertEliminatingTypeVariables(otherVariable, paramType.genericType(), rank, null);
/*  783 */         switch (otherEliminatedType.kind()) {
/*      */         case 516:
/*      */         case 8196:
/*  786 */           WildcardBinding otherWildcard = (WildcardBinding)otherEliminatedType;
/*  787 */           switch (otherWildcard.boundKind) {
/*      */           case 1:
/*  789 */             upperBound2 = otherWildcard.bound;
/*  790 */             break;
/*      */           case 2:
/*  792 */             lowerBound2 = otherWildcard.bound;
/*  793 */             break;
/*      */           case 0:
/*  795 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  800 */     if (lowerBound1 != null) {
/*  801 */       if (lowerBound2 != null) {
/*  802 */         return false;
/*      */       }
/*  804 */       if (upperBound2 != null) {
/*  805 */         if ((lowerBound1.isTypeVariable()) || (upperBound2.isTypeVariable())) {
/*  806 */           return false;
/*      */         }
/*  808 */         return !lowerBound1.isCompatibleWith(upperBound2);
/*      */       }
/*  810 */       if ((lowerBound1.isTypeVariable()) || (otherArgument.isTypeVariable())) {
/*  811 */         return false;
/*      */       }
/*  813 */       return !lowerBound1.isCompatibleWith(otherArgument);
/*      */     }
/*  815 */     if (upperBound1 != null) {
/*  816 */       if (lowerBound2 != null)
/*  817 */         return !lowerBound2.isCompatibleWith(upperBound1);
/*  818 */       if (upperBound2 != null)
/*      */       {
/*  820 */         return (upperBound1.isProvableDistinctSubType(upperBound2)) && 
/*  820 */           (upperBound2.isProvableDistinctSubType(upperBound1));
/*      */       }
/*  822 */       return otherArgument.isProvableDistinctSubType(upperBound1);
/*      */     }
/*      */ 
/*  825 */     if (lowerBound2 != null) {
/*  826 */       if ((lowerBound2.isTypeVariable()) || (isTypeVariable())) {
/*  827 */         return false;
/*      */       }
/*  829 */       return !lowerBound2.isCompatibleWith(this);
/*  830 */     }if (upperBound2 != null) {
/*  831 */       return isProvableDistinctSubType(upperBound2);
/*      */     }
/*  833 */     return true;
/*      */   }
/*      */ 
/*      */   public final boolean isRawType()
/*      */   {
/*  839 */     return kind() == 1028;
/*      */   }
/*      */ 
/*      */   public boolean isReifiable()
/*      */   {
/*  846 */     TypeBinding leafType = leafComponentType();
/*  847 */     if (!(leafType instanceof ReferenceBinding))
/*  848 */       return true;
/*  849 */     ReferenceBinding current = (ReferenceBinding)leafType;
/*      */     do {
/*  851 */       switch (current.kind()) {
/*      */       case 516:
/*      */       case 2052:
/*      */       case 4100:
/*      */       case 8196:
/*  856 */         return false;
/*      */       case 260:
/*  858 */         if (!current.isBoundParameterizedType()) break;
/*  859 */         return false;
/*      */       case 1028:
/*  862 */         return true;
/*      */       }
/*  864 */       if (current.isStatic())
/*  865 */         return true;
/*  866 */       if (current.isLocalType()) {
/*  867 */         NestedTypeBinding nestedType = (NestedTypeBinding)current.erasure();
/*  868 */         if (nestedType.scope.methodScope().isStatic) return true; 
/*      */       }
/*      */     }
/*  870 */     while ((current = current.enclosingType()) != null);
/*  871 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isThrowable()
/*      */   {
/*  878 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isTypeArgumentContainedBy(TypeBinding otherType) {
/*  882 */     if (this == otherType)
/*  883 */       return true;
/*  884 */     switch (otherType.kind())
/*      */     {
/*      */     case 516:
/*      */     case 8196:
/*  889 */       TypeBinding lowerBound = this;
/*  890 */       TypeBinding upperBound = this;
/*  891 */       switch (kind()) {
/*      */       case 516:
/*      */       case 8196:
/*  894 */         WildcardBinding wildcard = (WildcardBinding)this;
/*  895 */         switch (wildcard.boundKind) {
/*      */         case 1:
/*  897 */           if (wildcard.otherBounds != null)
/*      */             break;
/*  899 */           upperBound = wildcard.bound;
/*  900 */           lowerBound = null;
/*  901 */           break;
/*      */         case 2:
/*  903 */           upperBound = wildcard;
/*  904 */           lowerBound = wildcard.bound;
/*  905 */           break;
/*      */         case 0:
/*  907 */           upperBound = wildcard;
/*  908 */           lowerBound = null;
/*      */         }
/*  910 */         break;
/*      */       case 4100:
/*  912 */         if (!isCapture()) break;
/*  913 */         CaptureBinding capture = (CaptureBinding)this;
/*  914 */         if (capture.lowerBound == null) break;
/*  915 */         lowerBound = capture.lowerBound;
/*      */       }
/*      */ 
/*  918 */       WildcardBinding otherWildcard = (WildcardBinding)otherType;
/*  919 */       if (otherWildcard.otherBounds != null)
/*  920 */         return false;
/*  921 */       TypeBinding otherBound = otherWildcard.bound;
/*  922 */       switch (otherWildcard.boundKind) {
/*      */       case 1:
/*  924 */         if (otherBound == this)
/*  925 */           return true;
/*  926 */         if (upperBound == null)
/*  927 */           return false;
/*  928 */         TypeBinding match = upperBound.findSuperTypeOriginatingFrom(otherBound);
/*  929 */         if ((match != null) && ((match = match.leafComponentType()).isRawType())) {
/*  930 */           return match == otherBound.leafComponentType();
/*      */         }
/*      */ 
/*  933 */         return upperBound.isCompatibleWith(otherBound);
/*      */       case 2:
/*  936 */         if (otherBound == this)
/*  937 */           return true;
/*  938 */         if (lowerBound == null)
/*  939 */           return false;
/*  940 */         TypeBinding match = otherBound.findSuperTypeOriginatingFrom(lowerBound);
/*  941 */         if ((match != null) && ((match = match.leafComponentType()).isRawType())) {
/*  942 */           return match == lowerBound.leafComponentType();
/*      */         }
/*      */ 
/*  945 */         return otherBound.isCompatibleWith(lowerBound);
/*      */       case 0:
/*      */       }
/*      */ 
/*  949 */       return true;
/*      */     case 260:
/*  953 */       if (!isParameterizedType())
/*  954 */         return false;
/*  955 */       ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)this;
/*  956 */       ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding)otherType;
/*  957 */       if (paramType.actualType() != otherParamType.actualType())
/*  958 */         return false;
/*  959 */       if (!paramType.isStatic()) {
/*  960 */         ReferenceBinding enclosing = enclosingType();
/*  961 */         if (enclosing != null) {
/*  962 */           ReferenceBinding otherEnclosing = otherParamType.enclosingType();
/*  963 */           if (otherEnclosing == null)
/*  964 */             return false;
/*  965 */           if ((otherEnclosing.tagBits & 0x40000000) == 0L) {
/*  966 */             if (enclosing != otherEnclosing)
/*  967 */               return false;
/*      */           }
/*  969 */           else if (!enclosing.isEquivalentTo(otherParamType.enclosingType())) {
/*  970 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*  974 */       int length = paramType.arguments == null ? 0 : paramType.arguments.length;
/*  975 */       TypeBinding[] otherArguments = otherParamType.arguments;
/*  976 */       int otherLength = otherArguments == null ? 0 : otherArguments.length;
/*  977 */       if (otherLength != length)
/*  978 */         return false;
/*  979 */       for (int i = 0; i < length; i++) {
/*  980 */         TypeBinding argument = paramType.arguments[i];
/*  981 */         TypeBinding otherArgument = otherArguments[i];
/*  982 */         if (argument == otherArgument)
/*      */           continue;
/*  984 */         int kind = argument.kind();
/*  985 */         if (otherArgument.kind() != kind)
/*  986 */           return false;
/*  987 */         switch (kind) {
/*      */         case 260:
/*  989 */           if (!argument.isTypeArgumentContainedBy(otherArgument)) break;
/*  990 */           break;
/*      */         case 516:
/*      */         case 8196:
/*  994 */           WildcardBinding wildcard = (WildcardBinding)argument;
/*  995 */           WildcardBinding otherWildcard = (WildcardBinding)otherArgument;
/*  996 */           switch (wildcard.boundKind)
/*      */           {
/*      */           case 1:
/*  999 */             if ((otherWildcard.boundKind != 0) || 
/* 1000 */               (wildcard.bound != wildcard.typeVariable().upperBound())) break;
/* 1001 */             break;
/*      */           case 2:
/* 1004 */             break;
/*      */           case 0:
/* 1007 */             if ((otherWildcard.boundKind == 1) && 
/* 1008 */               (otherWildcard.bound == otherWildcard.typeVariable().upperBound()))
/*      */             {
/*      */               continue;
/*      */             }
/*      */           }
/*      */         }
/* 1014 */         return false;
/*      */       }
/* 1016 */       return true;
/*      */     }
/*      */ 
/* 1019 */     if (otherType.id == 
/* 1019 */       1) {
/* 1020 */       switch (kind()) {
/*      */       case 516:
/* 1022 */         WildcardBinding wildcard = (WildcardBinding)this;
/* 1023 */         if ((wildcard.boundKind != 2) || (wildcard.bound.id != 1)) break;
/* 1024 */         return true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1029 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isTypeVariable()
/*      */   {
/* 1036 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isUnboundWildcard()
/*      */   {
/* 1043 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isUncheckedException(boolean includeSupertype)
/*      */   {
/* 1050 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isWildcard()
/*      */   {
/* 1057 */     return false;
/*      */   }
/*      */ 
/*      */   public int kind()
/*      */   {
/* 1064 */     return 4;
/*      */   }
/*      */ 
/*      */   public TypeBinding leafComponentType() {
/* 1068 */     return this;
/*      */   }
/*      */ 
/*      */   public boolean needsUncheckedConversion(TypeBinding targetType)
/*      */   {
/* 1076 */     if (this == targetType)
/* 1077 */       return false;
/* 1078 */     targetType = targetType.leafComponentType();
/* 1079 */     if (!(targetType instanceof ReferenceBinding)) {
/* 1080 */       return false;
/*      */     }
/* 1082 */     TypeBinding currentType = leafComponentType();
/* 1083 */     TypeBinding match = currentType.findSuperTypeOriginatingFrom(targetType);
/* 1084 */     if (!(match instanceof ReferenceBinding))
/* 1085 */       return false;
/* 1086 */     ReferenceBinding compatible = (ReferenceBinding)match;
/* 1087 */     while (compatible.isRawType()) {
/* 1088 */       if (targetType.isBoundParameterizedType())
/* 1089 */         return true;
/* 1090 */       if (compatible.isStatic())
/*      */         break;
/* 1092 */       if ((compatible = compatible.enclosingType()) == null)
/*      */         break;
/* 1094 */       if ((targetType = targetType.enclosingType()) == null)
/*      */         break;
/*      */     }
/* 1097 */     return false;
/*      */   }
/*      */ 
/*      */   public TypeBinding original()
/*      */   {
/* 1105 */     switch (kind()) {
/*      */     case 68:
/*      */     case 260:
/*      */     case 1028:
/* 1109 */       return erasure();
/*      */     }
/* 1111 */     return this;
/*      */   }
/*      */ 
/*      */   public char[] qualifiedPackageName()
/*      */   {
/* 1123 */     PackageBinding packageBinding = getPackage();
/* 1124 */     return (packageBinding == null) || 
/* 1125 */       (packageBinding.compoundName == CharOperation.NO_CHAR_CHAR) ? 
/* 1125 */       CharOperation.NO_CHAR : 
/* 1126 */       packageBinding.readableName();
/*      */   }
/*      */ 
/*      */   public abstract char[] qualifiedSourceName();
/*      */ 
/*      */   public char[] signature()
/*      */   {
/* 1143 */     return constantPoolName();
/*      */   }
/*      */ 
/*      */   public abstract char[] sourceName();
/*      */ 
/*      */   public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType, LookupEnvironment environment)
/*      */   {
/*      */   }
/*      */ 
/*      */   public TypeVariableBinding[] typeVariables() {
/* 1154 */     return Binding.NO_TYPE_VARIABLES;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 * JD-Core Version:    0.6.0
 */