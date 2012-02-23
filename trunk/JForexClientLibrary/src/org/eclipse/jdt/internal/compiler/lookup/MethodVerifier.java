/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
/*     */ import org.eclipse.jdt.internal.compiler.util.SimpleSet;
/*     */ 
/*     */ public class MethodVerifier
/*     */ {
/*     */   SourceTypeBinding type;
/*     */   HashtableOfObject inheritedMethods;
/*     */   HashtableOfObject currentMethods;
/*     */   LookupEnvironment environment;
/*     */   private boolean allowCompatibleReturnTypes;
/*     */ 
/*     */   MethodVerifier(LookupEnvironment environment)
/*     */   {
/*  45 */     this.type = null;
/*  46 */     this.inheritedMethods = null;
/*  47 */     this.currentMethods = null;
/*  48 */     this.environment = environment;
/*  49 */     this.allowCompatibleReturnTypes = 
/*  50 */       ((environment.globalOptions.complianceLevel >= 3211264L) && 
/*  51 */       (environment.globalOptions.sourceLevel < 3211264L));
/*     */   }
/*     */   boolean areMethodsCompatible(MethodBinding one, MethodBinding two) {
/*  54 */     return (isParameterSubsignature(one, two)) && (areReturnTypesCompatible(one, two));
/*     */   }
/*     */   boolean areParametersEqual(MethodBinding one, MethodBinding two) {
/*  57 */     TypeBinding[] oneArgs = one.parameters;
/*  58 */     TypeBinding[] twoArgs = two.parameters;
/*  59 */     if (oneArgs == twoArgs) return true;
/*     */ 
/*  61 */     int length = oneArgs.length;
/*  62 */     if (length != twoArgs.length) return false;
/*     */ 
/*  64 */     for (int i = 0; i < length; i++)
/*  65 */       if (!areTypesEqual(oneArgs[i], twoArgs[i])) return false;
/*  66 */     return true;
/*     */   }
/*     */   boolean areReturnTypesCompatible(MethodBinding one, MethodBinding two) {
/*  69 */     if (one.returnType == two.returnType) return true;
/*     */ 
/*  71 */     if (areTypesEqual(one.returnType, two.returnType)) return true;
/*     */ 
/*  74 */     if ((this.allowCompatibleReturnTypes) && 
/*  75 */       ((one.declaringClass instanceof BinaryTypeBinding)) && 
/*  76 */       ((two.declaringClass instanceof BinaryTypeBinding))) {
/*  77 */       return areReturnTypesCompatible0(one, two);
/*     */     }
/*  79 */     return false;
/*     */   }
/*     */ 
/*     */   boolean areReturnTypesCompatible0(MethodBinding one, MethodBinding two) {
/*  83 */     if (one.returnType.isBaseType()) return false;
/*     */ 
/*  85 */     if ((!one.declaringClass.isInterface()) && (one.declaringClass.id == 1)) {
/*  86 */       return two.returnType.isCompatibleWith(one.returnType);
/*     */     }
/*  88 */     return one.returnType.isCompatibleWith(two.returnType);
/*     */   }
/*     */   boolean areTypesEqual(TypeBinding one, TypeBinding two) {
/*  91 */     if (one == two) return true;
/*     */ 
/*  97 */     if ((one instanceof UnresolvedReferenceBinding))
/*  98 */       return ((UnresolvedReferenceBinding)one).resolvedType == two;
/*  99 */     if ((two instanceof UnresolvedReferenceBinding))
/* 100 */       return ((UnresolvedReferenceBinding)two).resolvedType == one;
/* 101 */     return false;
/*     */   }
/*     */   boolean canSkipInheritedMethods() {
/* 104 */     if ((this.type.superclass() != null) && (this.type.superclass().isAbstract()))
/* 105 */       return false;
/* 106 */     return this.type.superInterfaces() == Binding.NO_SUPERINTERFACES;
/*     */   }
/*     */ 
/*     */   boolean canSkipInheritedMethods(MethodBinding one, MethodBinding two) {
/* 110 */     return (two == null) || 
/* 110 */       (one.declaringClass == two.declaringClass);
/*     */   }
/*     */   void checkAbstractMethod(MethodBinding abstractMethod) {
/* 113 */     if (mustImplementAbstractMethod(abstractMethod.declaringClass)) {
/* 114 */       TypeDeclaration typeDeclaration = this.type.scope.referenceContext;
/* 115 */       if (typeDeclaration != null) {
/* 116 */         MethodDeclaration missingAbstractMethod = typeDeclaration.addMissingAbstractMethodFor(abstractMethod);
/* 117 */         missingAbstractMethod.scope.problemReporter().abstractMethodMustBeImplemented(this.type, abstractMethod);
/*     */       } else {
/* 119 */         problemReporter().abstractMethodMustBeImplemented(this.type, abstractMethod);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void checkAgainstInheritedMethods(MethodBinding currentMethod, MethodBinding[] methods, int length, MethodBinding[] allInheritedMethods) {
/* 124 */     if (this.type.isAnnotationType()) {
/* 125 */       problemReporter().annotationCannotOverrideMethod(currentMethod, methods[(length - 1)]);
/* 126 */       return; } CompilerOptions options = this.type.scope.compilerOptions();
/*     */ 
/* 131 */     int[] overriddenInheritedMethods = length > 1 ? findOverriddenInheritedMethods(methods, length) : null;
/* 132 */     int i = length;
/*     */     label408: 
/*     */     do { MethodBinding inheritedMethod = methods[i];
/* 134 */       if ((overriddenInheritedMethods == null) || (overriddenInheritedMethods[i] == 0)) {
/* 135 */         if (currentMethod.isStatic() != inheritedMethod.isStatic()) {
/* 136 */           problemReporter(currentMethod).staticAndInstanceConflict(currentMethod, inheritedMethod);
/* 137 */           break label408;
/*     */         }
/*     */ 
/* 141 */         if (inheritedMethod.isAbstract()) {
/* 142 */           if (inheritedMethod.declaringClass.isInterface())
/* 143 */             currentMethod.modifiers |= 536870912;
/*     */           else {
/* 145 */             currentMethod.modifiers |= 805306368;
/*     */           }
/*     */ 
/*     */         }
/* 151 */         else if ((inheritedMethod.isPublic()) || (!this.type.isInterface()))
/*     */         {
/* 153 */           currentMethod.modifiers |= 268435456;
/*     */         }
/*     */ 
/* 156 */         if ((!areReturnTypesCompatible(currentMethod, inheritedMethod)) && 
/* 157 */           ((currentMethod.returnType.tagBits & 0x80) == 0L) && 
/* 158 */           (reportIncompatibleReturnTypeError(currentMethod, inheritedMethod))) {
/*     */           break label408;
/*     */         }
/* 162 */         if (currentMethod.thrownExceptions != Binding.NO_EXCEPTIONS)
/* 163 */           checkExceptions(currentMethod, inheritedMethod);
/* 164 */         if (inheritedMethod.isFinal())
/* 165 */           problemReporter(currentMethod).finalMethodCannotBeOverridden(currentMethod, inheritedMethod);
/* 166 */         if (!isAsVisible(currentMethod, inheritedMethod))
/* 167 */           problemReporter(currentMethod).visibilityConflict(currentMethod, inheritedMethod);
/* 168 */         if ((inheritedMethod.isSynchronized()) && (!currentMethod.isSynchronized())) {
/* 169 */           problemReporter(currentMethod).missingSynchronizedOnInheritedMethod(currentMethod, inheritedMethod);
/*     */         }
/* 171 */         if ((options.reportDeprecationWhenOverridingDeprecatedMethod) && (inheritedMethod.isViewedAsDeprecated()) && (
/* 172 */           (!currentMethod.isViewedAsDeprecated()) || (options.reportDeprecationInsideDeprecatedCode)))
/*     */         {
/* 174 */           ReferenceBinding declaringClass = inheritedMethod.declaringClass;
/* 175 */           if (declaringClass.isInterface()) {
/* 176 */             int j = length;
/*     */             do { if ((i != j) && (methods[j].declaringClass.implementsInterface(declaringClass, false)))
/*     */                 break;
/* 176 */               j--; } while (j >= 0);
/*     */           }
/*     */ 
/* 180 */           problemReporter(currentMethod).overridesDeprecatedMethod(currentMethod, inheritedMethod);
/*     */         }
/*     */       }
/*     */ 
/* 184 */       checkForBridgeMethod(currentMethod, inheritedMethod, allInheritedMethods);
/*     */ 
/* 132 */       i--; } while (i >= 0);
/*     */   }
/*     */ 
/*     */   void checkConcreteInheritedMethod(MethodBinding concreteMethod, MethodBinding[] abstractMethods)
/*     */   {
/* 190 */     if (concreteMethod.isStatic())
/*     */     {
/* 192 */       problemReporter().staticInheritedMethodConflicts(this.type, concreteMethod, abstractMethods);
/* 193 */     }if (!concreteMethod.isPublic()) {
/* 194 */       int index = 0; int length = abstractMethods.length;
/* 195 */       if (concreteMethod.isProtected())
/* 196 */         for (; index < length; index++)
/* 197 */           if (abstractMethods[index].isPublic()) break;
/* 198 */       else if (concreteMethod.isDefault())
/* 199 */         for (; index < length; index++)
/* 200 */           if (!abstractMethods[index].isDefault())
/*     */             break;
/* 202 */       if (index < length)
/* 203 */         problemReporter().inheritedMethodReducesVisibility(this.type, concreteMethod, abstractMethods);
/*     */     }
/* 205 */     if (concreteMethod.thrownExceptions != Binding.NO_EXCEPTIONS) {
/* 206 */       int i = abstractMethods.length;
/*     */       do { checkExceptions(concreteMethod, abstractMethods[i]);
/*     */ 
/* 206 */         i--; } while (i >= 0);
/*     */     }
/*     */ 
/* 211 */     if (concreteMethod.isOrEnclosedByPrivateType())
/* 212 */       concreteMethod.original().modifiers |= 134217728;
/*     */   }
/*     */ 
/*     */   void checkExceptions(MethodBinding newMethod, MethodBinding inheritedMethod)
/*     */   {
/* 222 */     ReferenceBinding[] newExceptions = resolvedExceptionTypesFor(newMethod);
/* 223 */     ReferenceBinding[] inheritedExceptions = resolvedExceptionTypesFor(inheritedMethod);
/* 224 */     int i = newExceptions.length;
/*     */     do { ReferenceBinding newException = newExceptions[i];
/* 226 */       int j = inheritedExceptions.length;
/*     */       do j--; while ((j > -1) && (!isSameClassOrSubclassOf(newException, inheritedExceptions[j])));
/* 228 */       if ((j == -1) && 
/* 229 */         (!newException.isUncheckedException(false)) && 
/* 230 */         ((newException.tagBits & 0x80) == 0L))
/* 231 */         problemReporter(newMethod).incompatibleExceptionInThrowsClause(this.type, newMethod, inheritedMethod, newException);
/* 224 */       i--; } while (i >= 0);
/*     */   }
/*     */ 
/*     */   void checkForBridgeMethod(MethodBinding currentMethod, MethodBinding inheritedMethod, MethodBinding[] allInheritedMethods)
/*     */   {
/*     */   }
/*     */ 
/*     */   void checkForMissingHashCodeMethod()
/*     */   {
/* 241 */     MethodBinding[] choices = this.type.getMethods(TypeConstants.EQUALS);
/* 242 */     boolean overridesEquals = false;
/* 243 */     int i = choices.length;
/*     */     do { overridesEquals = (choices[i].parameters.length == 1) && (choices[i].parameters[0].id == 1);
/*     */ 
/* 243 */       if (overridesEquals) break; i--; } while (i >= 0);
/*     */ 
/* 245 */     if (overridesEquals) {
/* 246 */       MethodBinding hashCodeMethod = this.type.getExactMethod(TypeConstants.HASHCODE, Binding.NO_PARAMETERS, null);
/* 247 */       if ((hashCodeMethod != null) && (hashCodeMethod.declaringClass.id == 1))
/* 248 */         problemReporter().shouldImplementHashcode(this.type);
/*     */     }
/*     */   }
/*     */ 
/*     */   void checkForRedundantSuperinterfaces(ReferenceBinding superclass, ReferenceBinding[] superInterfaces) {
/* 253 */     if (superInterfaces == Binding.NO_SUPERINTERFACES) return;
/*     */ 
/* 255 */     SimpleSet interfacesToCheck = new SimpleSet(superInterfaces.length);
/* 256 */     int i = 0; for (int l = superInterfaces.length; i < l; i++)
/* 257 */       interfacesToCheck.add(superInterfaces[i]);
/* 258 */     ReferenceBinding[] itsInterfaces = (ReferenceBinding[])null;
/* 259 */     SimpleSet inheritedInterfaces = new SimpleSet(5);
/* 260 */     ReferenceBinding superType = superclass;
/* 261 */     while ((superType != null) && (superType.isValidBinding())) {
/* 262 */       if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
/* 263 */         int i = 0; for (int l = itsInterfaces.length; i < l; i++) {
/* 264 */           ReferenceBinding inheritedInterface = itsInterfaces[i];
/* 265 */           if ((!inheritedInterfaces.includes(inheritedInterface)) && (inheritedInterface.isValidBinding())) {
/* 266 */             if (interfacesToCheck.includes(inheritedInterface)) {
/* 267 */               TypeReference[] refs = this.type.scope.referenceContext.superInterfaces;
/* 268 */               int r = 0; for (int rl = refs.length; r < rl; r++)
/* 269 */                 if (refs[r].resolvedType == inheritedInterface) {
/* 270 */                   problemReporter().redundantSuperInterface(this.type, refs[r], inheritedInterface, superType);
/* 271 */                   break;
/*     */                 }
/*     */             }
/*     */             else {
/* 275 */               inheritedInterfaces.add(inheritedInterface);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 280 */       superType = superType.superclass();
/*     */     }
/*     */ 
/* 283 */     int nextPosition = inheritedInterfaces.elementSize;
/* 284 */     if (nextPosition == 0) return;
/* 285 */     ReferenceBinding[] interfacesToVisit = new ReferenceBinding[nextPosition];
/* 286 */     inheritedInterfaces.asArray(interfacesToVisit);
/* 287 */     for (int i = 0; i < nextPosition; i++) {
/* 288 */       superType = interfacesToVisit[i];
/* 289 */       if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
/* 290 */         int itsLength = itsInterfaces.length;
/* 291 */         if (nextPosition + itsLength >= interfacesToVisit.length)
/* 292 */           System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 293 */         for (int a = 0; a < itsLength; a++) {
/* 294 */           ReferenceBinding inheritedInterface = itsInterfaces[a];
/* 295 */           if ((!inheritedInterfaces.includes(inheritedInterface)) && (inheritedInterface.isValidBinding()))
/* 296 */             if (interfacesToCheck.includes(inheritedInterface)) {
/* 297 */               TypeReference[] refs = this.type.scope.referenceContext.superInterfaces;
/* 298 */               int r = 0; for (int rl = refs.length; r < rl; r++)
/* 299 */                 if (refs[r].resolvedType == inheritedInterface) {
/* 300 */                   problemReporter().redundantSuperInterface(this.type, refs[r], inheritedInterface, superType);
/* 301 */                   break;
/*     */                 }
/*     */             }
/*     */             else {
/* 305 */               inheritedInterfaces.add(inheritedInterface);
/* 306 */               interfacesToVisit[(nextPosition++)] = inheritedInterface;
/*     */             }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void checkInheritedMethods(MethodBinding[] methods, int length)
/*     */   {
/* 326 */     MethodBinding concreteMethod = (this.type.isInterface()) || (methods[0].isAbstract()) ? null : methods[0];
/* 327 */     if (concreteMethod == null) {
/* 328 */       MethodBinding bestAbstractMethod = length == 1 ? methods[0] : findBestInheritedAbstractMethod(methods, length);
/* 329 */       boolean noMatch = bestAbstractMethod == null;
/* 330 */       if (noMatch)
/* 331 */         bestAbstractMethod = methods[0];
/* 332 */       if (mustImplementAbstractMethod(bestAbstractMethod.declaringClass)) {
/* 333 */         TypeDeclaration typeDeclaration = this.type.scope.referenceContext;
/* 334 */         MethodBinding superclassAbstractMethod = methods[0];
/* 335 */         if ((superclassAbstractMethod == bestAbstractMethod) || (superclassAbstractMethod.declaringClass.isInterface())) {
/* 336 */           if (typeDeclaration != null) {
/* 337 */             MethodDeclaration missingAbstractMethod = typeDeclaration.addMissingAbstractMethodFor(bestAbstractMethod);
/* 338 */             missingAbstractMethod.scope.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod);
/*     */           } else {
/* 340 */             problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod);
/*     */           }
/*     */         }
/* 343 */         else if (typeDeclaration != null) {
/* 344 */           MethodDeclaration missingAbstractMethod = typeDeclaration.addMissingAbstractMethodFor(bestAbstractMethod);
/* 345 */           missingAbstractMethod.scope.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod, superclassAbstractMethod);
/*     */         } else {
/* 347 */           problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod, superclassAbstractMethod);
/*     */         }
/*     */       }
/* 350 */       else if (noMatch) {
/* 351 */         problemReporter().inheritedMethodsHaveIncompatibleReturnTypes(this.type, methods, length);
/*     */       }
/* 353 */       return;
/*     */     }
/* 355 */     if (length < 2) return;
/*     */ 
/* 357 */     int index = length;
/*     */     do index--; while ((index > 0) && (checkInheritedReturnTypes(concreteMethod, methods[index])));
/* 359 */     if (index > 0)
/*     */     {
/* 361 */       MethodBinding bestAbstractMethod = findBestInheritedAbstractMethod(methods, length);
/* 362 */       if (bestAbstractMethod == null)
/* 363 */         problemReporter().inheritedMethodsHaveIncompatibleReturnTypes(this.type, methods, length);
/*     */       else
/* 365 */         problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod, concreteMethod);
/* 366 */       return;
/*     */     }
/*     */ 
/* 369 */     MethodBinding[] abstractMethods = new MethodBinding[length - 1];
/* 370 */     index = 0;
/* 371 */     for (int i = 0; i < length; i++)
/* 372 */       if (methods[i].isAbstract())
/* 373 */         abstractMethods[(index++)] = methods[i];
/* 374 */     if (index == 0) return;
/* 375 */     if (index < abstractMethods.length)
/* 376 */       System.arraycopy(abstractMethods, 0, abstractMethods = new MethodBinding[index], 0, index);
/* 377 */     checkConcreteInheritedMethod(concreteMethod, abstractMethods);
/*     */   }
/*     */ 
/*     */   boolean checkInheritedReturnTypes(MethodBinding method, MethodBinding otherMethod) {
/* 381 */     if (areReturnTypesCompatible(method, otherMethod)) return true;
/*     */ 
/* 386 */     return (!this.type.isInterface()) && 
/* 384 */       ((method.declaringClass.isClass()) || (!this.type.implementsInterface(method.declaringClass, false))) && (
/* 385 */       (otherMethod.declaringClass.isClass()) || (!this.type.implementsInterface(otherMethod.declaringClass, false)));
/*     */   }
/*     */ 
/*     */   void checkMethods()
/*     */   {
/* 410 */     boolean mustImplementAbstractMethods = mustImplementAbstractMethods();
/* 411 */     boolean skipInheritedMethods = (mustImplementAbstractMethods) && (canSkipInheritedMethods());
/* 412 */     char[][] methodSelectors = this.inheritedMethods.keyTable;
/* 413 */     int s = methodSelectors.length;
/*     */     do { if (methodSelectors[s] != null)
/*     */       {
/* 416 */         MethodBinding[] current = (MethodBinding[])this.currentMethods.get(methodSelectors[s]);
/* 417 */         if ((current != null) || (!skipInheritedMethods))
/*     */         {
/* 420 */           MethodBinding[] inherited = (MethodBinding[])this.inheritedMethods.valueTable[s];
/* 421 */           if ((inherited.length == 1) && (current == null)) {
/* 422 */             if ((mustImplementAbstractMethods) && (inherited[0].isAbstract()))
/* 423 */               checkAbstractMethod(inherited[0]);
/*     */           }
/*     */           else
/*     */           {
/* 427 */             int index = -1;
/* 428 */             MethodBinding[] matchingInherited = new MethodBinding[inherited.length];
/* 429 */             if (current != null) {
/* 430 */               int i = 0; for (int length1 = current.length; i < length1; i++) {
/* 431 */                 MethodBinding currentMethod = current[i];
/* 432 */                 int j = 0; for (int length2 = inherited.length; j < length2; j++) {
/* 433 */                   MethodBinding inheritedMethod = computeSubstituteMethod(inherited[j], currentMethod);
/* 434 */                   if ((inheritedMethod == null) || 
/* 435 */                     (!isParameterSubsignature(currentMethod, inheritedMethod))) continue;
/* 436 */                   index++; matchingInherited[index] = inheritedMethod;
/* 437 */                   inherited[j] = null;
/*     */                 }
/*     */ 
/* 441 */                 if (index >= 0) {
/* 442 */                   checkAgainstInheritedMethods(currentMethod, matchingInherited, index + 1, inherited);
/* 443 */                   while (index >= 0) matchingInherited[(index--)] = null;
/*     */                 }
/*     */               }
/*     */             }
/*     */ 
/* 448 */             int i = 0; for (int length = inherited.length; i < length; i++) {
/* 449 */               MethodBinding inheritedMethod = inherited[i];
/* 450 */               if (inheritedMethod == null)
/*     */                 continue;
/* 452 */               index++; matchingInherited[index] = inheritedMethod;
/* 453 */               for (int j = i + 1; j < length; j++) {
/* 454 */                 MethodBinding otherInheritedMethod = inherited[j];
/* 455 */                 if (canSkipInheritedMethods(inheritedMethod, otherInheritedMethod))
/*     */                   continue;
/* 457 */                 otherInheritedMethod = computeSubstituteMethod(otherInheritedMethod, inheritedMethod);
/* 458 */                 if ((otherInheritedMethod == null) || 
/* 459 */                   (!isParameterSubsignature(inheritedMethod, otherInheritedMethod))) continue;
/* 460 */                 index++; matchingInherited[index] = otherInheritedMethod;
/* 461 */                 inherited[j] = null;
/*     */               }
/*     */ 
/* 465 */               if (index != -1) {
/* 466 */                 if (index > 0)
/* 467 */                   checkInheritedMethods(matchingInherited, index + 1);
/* 468 */                 else if ((mustImplementAbstractMethods) && (matchingInherited[0].isAbstract()))
/* 469 */                   checkAbstractMethod(matchingInherited[0]);
/* 470 */                 while (index >= 0) matchingInherited[(index--)] = null;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 413 */       s--; } while (s >= 0);
/*     */   }
/*     */ 
/*     */   void checkPackagePrivateAbstractMethod(MethodBinding abstractMethod)
/*     */   {
/* 477 */     PackageBinding necessaryPackage = abstractMethod.declaringClass.fPackage;
/* 478 */     if (necessaryPackage == this.type.fPackage) return;
/*     */ 
/* 480 */     ReferenceBinding superType = this.type.superclass();
/* 481 */     char[] selector = abstractMethod.selector;
/*     */     do {
/* 483 */       if (!superType.isValidBinding()) return;
/* 484 */       if (!superType.isAbstract()) return;
/*     */ 
/* 486 */       if (necessaryPackage == superType.fPackage) {
/* 487 */         MethodBinding[] methods = superType.getMethods(selector);
/* 488 */         int m = methods.length;
/*     */         do { MethodBinding method = methods[m];
/* 490 */           if ((!method.isPrivate()) && (!method.isConstructor()) && (!method.isDefaultAbstract()))
/*     */           {
/* 492 */             if (areMethodsCompatible(method, abstractMethod))
/* 493 */               return;
/*     */           }
/* 488 */           m--; } while (m >= 0);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 496 */     while ((superType = superType.superclass()) != abstractMethod.declaringClass);
/*     */ 
/* 499 */     problemReporter().abstractMethodCannotBeOverridden(this.type, abstractMethod);
/*     */   }
/*     */ 
/*     */   void computeInheritedMethods() {
/* 503 */     ReferenceBinding superclass = this.type.isInterface() ? 
/* 504 */       this.type.scope.getJavaLangObject() : 
/* 505 */       this.type.superclass();
/* 506 */     computeInheritedMethods(superclass, this.type.superInterfaces());
/* 507 */     checkForRedundantSuperinterfaces(superclass, this.type.superInterfaces());
/*     */   }
/*     */ 
/*     */   void computeInheritedMethods(ReferenceBinding superclass, ReferenceBinding[] superInterfaces)
/*     */   {
/* 526 */     this.inheritedMethods = new HashtableOfObject(51);
/* 527 */     ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/* 528 */     int nextPosition = 0;
/* 529 */     ReferenceBinding[] itsInterfaces = superInterfaces;
/* 530 */     if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
/* 531 */       nextPosition = itsInterfaces.length;
/* 532 */       interfacesToVisit = itsInterfaces;
/*     */     }
/*     */ 
/* 535 */     ReferenceBinding superType = superclass;
/* 536 */     HashtableOfObject nonVisibleDefaultMethods = new HashtableOfObject(3);
/* 537 */     boolean allSuperclassesAreAbstract = true;
/*     */ 
/* 539 */     while ((superType != null) && (superType.isValidBinding())) {
/* 540 */       if (allSuperclassesAreAbstract) {
/* 541 */         if (superType.isAbstract())
/*     */         {
/* 543 */           if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES)
/* 544 */             if (interfacesToVisit == null) {
/* 545 */               interfacesToVisit = itsInterfaces;
/* 546 */               nextPosition = interfacesToVisit.length;
/*     */             } else {
/* 548 */               int itsLength = itsInterfaces.length;
/* 549 */               if (nextPosition + itsLength >= interfacesToVisit.length)
/* 550 */                 System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 551 */               for (int a = 0; a < itsLength; a++) {
/* 552 */                 ReferenceBinding next = itsInterfaces[a];
/* 553 */                 int b = 0;
/* 554 */                 while (next != interfacesToVisit[b])
/*     */                 {
/* 553 */                   b++; if (b < nextPosition)
/*     */                     continue;
/* 555 */                   interfacesToVisit[(nextPosition++)] = next;
/*     */                 }
/*     */               }
/*     */             }
/*     */         }
/* 560 */         else allSuperclassesAreAbstract = false;
/*     */ 
/*     */       }
/*     */ 
/* 564 */       MethodBinding[] methods = superType.unResolvedMethods();
/* 565 */       int m = methods.length;
/*     */       do { MethodBinding inheritedMethod = methods[m];
/* 567 */         if ((!inheritedMethod.isPrivate()) && (!inheritedMethod.isConstructor()) && (!inheritedMethod.isDefaultAbstract()))
/*     */         {
/* 569 */           MethodBinding[] existingMethods = (MethodBinding[])this.inheritedMethods.get(inheritedMethod.selector);
/* 570 */           if (existingMethods != null) {
/* 571 */             int i = 0; for (int length = existingMethods.length; i < length; i++) {
/* 572 */               MethodBinding existingMethod = existingMethods[i];
/* 573 */               if ((existingMethod.declaringClass != inheritedMethod.declaringClass) && (areMethodsCompatible(existingMethod, inheritedMethod))) {
/* 574 */                 if (!inheritedMethod.isDefault()) break;
/* 575 */                 if (inheritedMethod.isAbstract()) {
/* 576 */                   checkPackagePrivateAbstractMethod(inheritedMethod); break;
/* 577 */                 }if ((existingMethod.declaringClass.fPackage == inheritedMethod.declaringClass.fPackage) || 
/* 578 */                   (this.type.fPackage != inheritedMethod.declaringClass.fPackage) || (areReturnTypesCompatible(inheritedMethod, existingMethod)))
/*     */                 {
/*     */                   break;
/*     */                 }
/*     */               }
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 587 */           if ((!inheritedMethod.isDefault()) || (inheritedMethod.declaringClass.fPackage == this.type.fPackage)) {
/* 588 */             if (existingMethods == null) {
/* 589 */               existingMethods = new MethodBinding[] { inheritedMethod };
/*     */             } else {
/* 591 */               int length = existingMethods.length;
/* 592 */               System.arraycopy(existingMethods, 0, existingMethods = new MethodBinding[length + 1], 0, length);
/* 593 */               existingMethods[length] = inheritedMethod;
/*     */             }
/* 595 */             this.inheritedMethods.put(inheritedMethod.selector, existingMethods);
/*     */           } else {
/* 597 */             MethodBinding[] nonVisible = (MethodBinding[])nonVisibleDefaultMethods.get(inheritedMethod.selector);
/* 598 */             if (nonVisible != null) {
/* 599 */               int i = 0; for (int l = nonVisible.length; i < l; i++)
/* 600 */                 if (areMethodsCompatible(nonVisible[i], inheritedMethod)) break;
/*     */             }
/* 602 */             if (nonVisible == null) {
/* 603 */               nonVisible = new MethodBinding[] { inheritedMethod };
/*     */             } else {
/* 605 */               int length = nonVisible.length;
/* 606 */               System.arraycopy(nonVisible, 0, nonVisible = new MethodBinding[length + 1], 0, length);
/* 607 */               nonVisible[length] = inheritedMethod;
/*     */             }
/* 609 */             nonVisibleDefaultMethods.put(inheritedMethod.selector, nonVisible);
/*     */ 
/* 611 */             if ((inheritedMethod.isAbstract()) && (!this.type.isAbstract())) {
/* 612 */               problemReporter().abstractMethodCannotBeOverridden(this.type, inheritedMethod);
/*     */             }
/* 614 */             MethodBinding[] current = (MethodBinding[])this.currentMethods.get(inheritedMethod.selector);
/* 615 */             if ((current != null) && (!inheritedMethod.isStatic())) {
/* 616 */               int i = 0; for (int length = current.length; i < length; i++)
/* 617 */                 if ((!current[i].isStatic()) && (areMethodsCompatible(current[i], inheritedMethod))) {
/* 618 */                   problemReporter().overridesPackageDefaultMethod(current[i], inheritedMethod);
/* 619 */                   break;
/*     */                 }
/*     */             }
/*     */           }
/*     */         }
/* 565 */         m--; } while (m >= 0);
/*     */ 
/* 625 */       superType = superType.superclass();
/*     */     }
/* 627 */     if (nextPosition == 0) return;
/*     */ 
/* 629 */     SimpleSet skip = findSuperinterfaceCollisions(superclass, superInterfaces);
/* 630 */     for (int i = 0; i < nextPosition; i++) {
/* 631 */       superType = interfacesToVisit[i];
/* 632 */       if ((!superType.isValidBinding()) || (
/* 633 */         (skip != null) && (skip.includes(superType)))) continue;
/* 634 */       if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
/* 635 */         int itsLength = itsInterfaces.length;
/* 636 */         if (nextPosition + itsLength >= interfacesToVisit.length)
/* 637 */           System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 638 */         for (int a = 0; a < itsLength; a++) {
/* 639 */           ReferenceBinding next = itsInterfaces[a];
/* 640 */           int b = 0;
/* 641 */           while (next != interfacesToVisit[b])
/*     */           {
/* 640 */             b++; if (b < nextPosition)
/*     */               continue;
/* 642 */             interfacesToVisit[(nextPosition++)] = next;
/*     */           }
/*     */         }
/*     */       }
/* 646 */       MethodBinding[] methods = superType.unResolvedMethods();
/* 647 */       int m = methods.length;
/*     */       do { MethodBinding inheritedMethod = methods[m];
/* 649 */         MethodBinding[] existingMethods = (MethodBinding[])this.inheritedMethods.get(inheritedMethod.selector);
/* 650 */         if (existingMethods == null) {
/* 651 */           existingMethods = new MethodBinding[] { inheritedMethod };
/*     */         } else {
/* 653 */           int length = existingMethods.length;
/*     */ 
/* 655 */           for (int e = 0; e < length; e++)
/* 656 */             if (isInterfaceMethodImplemented(inheritedMethod, existingMethods[e], superType))
/*     */               break;
/* 658 */           System.arraycopy(existingMethods, 0, existingMethods = new MethodBinding[length + 1], 0, length);
/* 659 */           existingMethods[length] = inheritedMethod;
/*     */         }
/* 661 */         this.inheritedMethods.put(inheritedMethod.selector, existingMethods);
/*     */ 
/* 647 */         m--; } while (m >= 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   void computeMethods()
/*     */   {
/* 668 */     MethodBinding[] methods = this.type.methods();
/* 669 */     int size = methods.length;
/* 670 */     this.currentMethods = new HashtableOfObject(size == 0 ? 1 : size);
/* 671 */     int m = size;
/*     */     do { MethodBinding method = methods[m];
/* 673 */       if ((!method.isConstructor()) && (!method.isDefaultAbstract())) {
/* 674 */         MethodBinding[] existingMethods = (MethodBinding[])this.currentMethods.get(method.selector);
/* 675 */         if (existingMethods == null)
/* 676 */           existingMethods = new MethodBinding[1];
/*     */         else
/* 678 */           System.arraycopy(existingMethods, 0, 
/* 679 */             existingMethods = new MethodBinding[existingMethods.length + 1], 0, existingMethods.length - 1);
/* 680 */         existingMethods[(existingMethods.length - 1)] = method;
/* 681 */         this.currentMethods.put(method.selector, existingMethods);
/*     */       }
/* 671 */       m--; } while (m >= 0);
/*     */   }
/*     */ 
/*     */   MethodBinding computeSubstituteMethod(MethodBinding inheritedMethod, MethodBinding currentMethod)
/*     */   {
/* 687 */     if (inheritedMethod == null) return null;
/* 688 */     if (currentMethod.parameters.length != inheritedMethod.parameters.length) return null;
/* 689 */     return inheritedMethod;
/*     */   }
/*     */ 
/*     */   boolean couldMethodOverride(MethodBinding method, MethodBinding inheritedMethod) {
/* 693 */     if (!CharOperation.equals(method.selector, inheritedMethod.selector))
/* 694 */       return false;
/* 695 */     if ((method == inheritedMethod) || (method.isStatic()) || (inheritedMethod.isStatic()))
/* 696 */       return false;
/* 697 */     if (inheritedMethod.isPrivate())
/* 698 */       return false;
/* 699 */     if ((inheritedMethod.isDefault()) && (method.declaringClass.getPackage() != inheritedMethod.declaringClass.getPackage()))
/* 700 */       return false;
/* 701 */     if (!method.isPublic()) {
/* 702 */       if (inheritedMethod.isPublic())
/* 703 */         return false;
/* 704 */       if ((inheritedMethod.isProtected()) && (!method.isProtected()))
/* 705 */         return false;
/*     */     }
/* 707 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean doesMethodOverride(MethodBinding method, MethodBinding inheritedMethod)
/*     */   {
/* 714 */     if (!couldMethodOverride(method, inheritedMethod)) {
/* 715 */       return false;
/*     */     }
/* 717 */     inheritedMethod = inheritedMethod.original();
/* 718 */     TypeBinding match = method.declaringClass.findSuperTypeOriginatingFrom(inheritedMethod.declaringClass);
/* 719 */     if (!(match instanceof ReferenceBinding)) {
/* 720 */       return false;
/*     */     }
/* 722 */     return isParameterSubsignature(method, inheritedMethod);
/*     */   }
/*     */ 
/*     */   SimpleSet findSuperinterfaceCollisions(ReferenceBinding superclass, ReferenceBinding[] superInterfaces) {
/* 726 */     return null;
/*     */   }
/*     */ 
/*     */   MethodBinding findBestInheritedAbstractMethod(MethodBinding[] methods, int length) {
/* 730 */     for (int i = 0; i < length; i++) {
/* 731 */       MethodBinding method = methods[i];
/* 732 */       if (method.isAbstract()) {
/* 733 */         int j = 0;
/*     */         while (true) if ((i != j) && 
/* 735 */             (!checkInheritedReturnTypes(method, methods[j]))) {
/* 736 */             if ((!this.type.isInterface()) || (methods[j].declaringClass.id != 1)) break;
/* 737 */             return method;
/*     */           }
/*     */           else
/*     */           {
/* 733 */             j++; if (j >= length)
/*     */             {
/* 741 */               return method;
/*     */             }
/*     */           } 
/*     */       }
/*     */     }
/* 743 */     return null;
/*     */   }
/*     */ 
/*     */   int[] findOverriddenInheritedMethods(MethodBinding[] methods, int length)
/*     */   {
/* 751 */     int[] toSkip = (int[])null;
/* 752 */     int i = 0;
/* 753 */     ReferenceBinding declaringClass = methods[i].declaringClass;
/* 754 */     if (!declaringClass.isInterface())
/*     */     {
/* 758 */       i++; ReferenceBinding declaringClass2 = methods[i].declaringClass;
/* 759 */       while (declaringClass == declaringClass2) {
/* 760 */         i++; if (i == length) return null;
/* 761 */         declaringClass2 = methods[i].declaringClass;
/*     */       }
/* 763 */       if (!declaringClass2.isInterface())
/*     */       {
/* 765 */         if ((declaringClass.fPackage != declaringClass2.fPackage) && (methods[i].isDefault())) return null;
/* 766 */         toSkip = new int[length];
/*     */         do {
/* 768 */           toSkip[i] = -1;
/* 769 */           i++; if (i == length) return toSkip;
/* 770 */           declaringClass2 = methods[i].declaringClass;
/*     */         }
/* 767 */         while (!
/* 771 */           declaringClass2.isInterface());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 776 */     for (; i < length; i++)
/* 777 */       if ((toSkip == null) || (toSkip[i] != -1)) {
/* 778 */         declaringClass = methods[i].declaringClass;
/* 779 */         for (int j = i + 1; j < length; j++)
/* 780 */           if ((toSkip == null) || (toSkip[j] != -1)) {
/* 781 */             ReferenceBinding declaringClass2 = methods[j].declaringClass;
/* 782 */             if (declaringClass != declaringClass2)
/* 783 */               if (declaringClass.implementsInterface(declaringClass2, true)) {
/* 784 */                 if (toSkip == null)
/* 785 */                   toSkip = new int[length];
/* 786 */                 toSkip[j] = -1;
/* 787 */               } else if (declaringClass2.implementsInterface(declaringClass, true)) {
/* 788 */                 if (toSkip == null)
/* 789 */                   toSkip = new int[length];
/* 790 */                 toSkip[i] = -1;
/* 791 */                 break;
/*     */               }
/*     */           }
/*     */       }
/* 795 */     return toSkip;
/*     */   }
/*     */ 
/*     */   boolean isAsVisible(MethodBinding newMethod, MethodBinding inheritedMethod) {
/* 799 */     if (inheritedMethod.modifiers == newMethod.modifiers) return true;
/*     */ 
/* 801 */     if (newMethod.isPublic()) return true;
/* 802 */     if (inheritedMethod.isPublic()) return false;
/*     */ 
/* 804 */     if (newMethod.isProtected()) return true;
/* 805 */     if (inheritedMethod.isProtected()) return false;
/*     */ 
/* 807 */     return !newMethod.isPrivate();
/*     */   }
/*     */ 
/*     */   boolean isInterfaceMethodImplemented(MethodBinding inheritedMethod, MethodBinding existingMethod, ReferenceBinding superType)
/*     */   {
/* 812 */     return (areParametersEqual(existingMethod, inheritedMethod)) && (existingMethod.declaringClass.implementsInterface(superType, true));
/*     */   }
/*     */ 
/*     */   public boolean isMethodSubsignature(MethodBinding method, MethodBinding inheritedMethod)
/*     */   {
/* 817 */     return (CharOperation.equals(method.selector, inheritedMethod.selector)) && 
/* 817 */       (isParameterSubsignature(method, inheritedMethod));
/*     */   }
/*     */ 
/*     */   boolean isParameterSubsignature(MethodBinding method, MethodBinding inheritedMethod) {
/* 821 */     return areParametersEqual(method, inheritedMethod);
/*     */   }
/*     */ 
/*     */   boolean isSameClassOrSubclassOf(ReferenceBinding testClass, ReferenceBinding superclass) {
/*     */     do
/* 826 */       if (testClass == superclass) return true;
/* 827 */     while ((testClass = testClass.superclass()) != null);
/* 828 */     return false;
/*     */   }
/*     */ 
/*     */   boolean mustImplementAbstractMethod(ReferenceBinding declaringClass)
/*     */   {
/* 834 */     if (!mustImplementAbstractMethods()) return false;
/* 835 */     ReferenceBinding superclass = this.type.superclass();
/* 836 */     if (declaringClass.isClass()) {
/*     */       do {
/* 838 */         superclass = superclass.superclass();
/*     */ 
/* 837 */         if (!superclass.isAbstract()) break; 
/* 837 */       }while (superclass != declaringClass);
/*     */     }
/*     */     else {
/* 840 */       if ((this.type.implementsInterface(declaringClass, false)) && 
/* 841 */         (!superclass.implementsInterface(declaringClass, true)))
/* 842 */         return true;
/*     */       do
/* 844 */         superclass = superclass.superclass();
/* 843 */       while ((superclass.isAbstract()) && (!superclass.implementsInterface(declaringClass, false)));
/*     */     }
/*     */ 
/* 846 */     return superclass.isAbstract();
/*     */   }
/*     */ 
/*     */   boolean mustImplementAbstractMethods() {
/* 850 */     return (!this.type.isInterface()) && (!this.type.isAbstract());
/*     */   }
/*     */ 
/*     */   ProblemReporter problemReporter() {
/* 854 */     return this.type.scope.problemReporter();
/*     */   }
/*     */ 
/*     */   ProblemReporter problemReporter(MethodBinding currentMethod) {
/* 858 */     ProblemReporter reporter = problemReporter();
/* 859 */     if ((currentMethod.declaringClass == this.type) && (currentMethod.sourceMethod() != null))
/* 860 */       reporter.referenceContext = currentMethod.sourceMethod();
/* 861 */     return reporter;
/*     */   }
/*     */ 
/*     */   boolean reportIncompatibleReturnTypeError(MethodBinding currentMethod, MethodBinding inheritedMethod)
/*     */   {
/* 875 */     problemReporter(currentMethod).incompatibleReturnType(currentMethod, inheritedMethod);
/* 876 */     return true;
/*     */   }
/*     */ 
/*     */   ReferenceBinding[] resolvedExceptionTypesFor(MethodBinding method) {
/* 880 */     ReferenceBinding[] exceptions = method.thrownExceptions;
/* 881 */     if ((method.modifiers & 0x2000000) == 0) {
/* 882 */       return exceptions;
/*     */     }
/* 884 */     if (!(method.declaringClass instanceof BinaryTypeBinding)) {
/* 885 */       return Binding.NO_EXCEPTIONS;
/*     */     }
/* 887 */     int i = exceptions.length;
/*     */     do { exceptions[i] = ((ReferenceBinding)BinaryTypeBinding.resolveType(exceptions[i], this.environment, true));
/*     */ 
/* 887 */       i--; } while (i >= 0);
/*     */ 
/* 889 */     return exceptions;
/*     */   }
/*     */ 
/*     */   void verify() {
/* 893 */     computeMethods();
/* 894 */     computeInheritedMethods();
/* 895 */     checkMethods();
/* 896 */     if (this.type.isClass())
/* 897 */       checkForMissingHashCodeMethod();
/*     */   }
/*     */ 
/*     */   void verify(SourceTypeBinding someType) {
/* 901 */     if (this.type == null)
/*     */       try {
/* 903 */         this.type = someType;
/* 904 */         verify();
/*     */       } finally {
/* 906 */         this.type = null;
/*     */       }
/*     */     else
/* 909 */       this.environment.newMethodVerifier().verify(someType);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 914 */     StringBuffer buffer = new StringBuffer(10);
/* 915 */     buffer.append("MethodVerifier for type: ");
/* 916 */     buffer.append(this.type.readableName());
/* 917 */     buffer.append('\n');
/* 918 */     buffer.append("\t-inherited methods: ");
/* 919 */     buffer.append(this.inheritedMethods);
/* 920 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.MethodVerifier
 * JD-Core Version:    0.6.0
 */