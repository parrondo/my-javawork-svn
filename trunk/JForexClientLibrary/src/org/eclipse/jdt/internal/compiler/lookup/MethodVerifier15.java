/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
/*     */ import org.eclipse.jdt.internal.compiler.util.SimpleSet;
/*     */ 
/*     */ class MethodVerifier15 extends MethodVerifier
/*     */ {
/*     */   MethodVerifier15(LookupEnvironment environment)
/*     */   {
/*  21 */     super(environment);
/*     */   }
/*     */ 
/*     */   boolean areMethodsCompatible(MethodBinding one, MethodBinding two) {
/*  25 */     one = one.original();
/*  26 */     two = one.findOriginalInheritedMethod(two);
/*     */ 
/*  28 */     if (two == null) {
/*  29 */       return false;
/*     */     }
/*  31 */     return isParameterSubsignature(one, two);
/*     */   }
/*     */   boolean areParametersEqual(MethodBinding one, MethodBinding two) {
/*  34 */     TypeBinding[] oneArgs = one.parameters;
/*  35 */     TypeBinding[] twoArgs = two.parameters;
/*  36 */     if (oneArgs == twoArgs) return true;
/*     */ 
/*  38 */     int length = oneArgs.length;
/*  39 */     if (length != twoArgs.length) return false;
/*     */ 
/*  41 */     if (one.declaringClass.isInterface()) {
/*  42 */       for (int i = 0; i < length; i++) {
/*  43 */         if (!areTypesEqual(oneArgs[i], twoArgs[i]))
/*  44 */           return false;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  49 */       for (int i = 0; i < length; i++) {
/*  50 */         if (!areTypesEqual(oneArgs[i], twoArgs[i])) {
/*  51 */           if ((oneArgs[i].leafComponentType().isRawType()) && 
/*  52 */             (oneArgs[i].dimensions() == twoArgs[i].dimensions()) && (oneArgs[i].leafComponentType().isEquivalentTo(twoArgs[i].leafComponentType())))
/*     */           {
/*  54 */             if (one.typeVariables != Binding.NO_TYPE_VARIABLES) {
/*  55 */               return false;
/*     */             }
/*     */ 
/*  58 */             for (int j = 0; j < i; j++) {
/*  59 */               if (oneArgs[j].leafComponentType().isParameterizedTypeWithActualArguments())
/*  60 */                 return false;
/*     */             }
/*  62 */             break;
/*     */           }
/*     */ 
/*  65 */           return false;
/*     */         }
/*     */       }
/*     */ 
/*  69 */       for (i++; i < length; i++) {
/*  70 */         if (!areTypesEqual(oneArgs[i], twoArgs[i])) {
/*  71 */           if ((!oneArgs[i].leafComponentType().isRawType()) || 
/*  72 */             (oneArgs[i].dimensions() != twoArgs[i].dimensions()) || (!oneArgs[i].leafComponentType().isEquivalentTo(twoArgs[i].leafComponentType())))
/*     */           {
/*  74 */             return false;
/*     */           }
/*  75 */         } else if (oneArgs[i].leafComponentType().isParameterizedTypeWithActualArguments()) {
/*  76 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*  80 */     return true;
/*     */   }
/*     */   boolean areReturnTypesCompatible(MethodBinding one, MethodBinding two) {
/*  83 */     if (one.returnType == two.returnType) return true;
/*  84 */     return areReturnTypesCompatible0(one, two);
/*     */   }
/*     */   boolean areTypesEqual(TypeBinding one, TypeBinding two) {
/*  87 */     if (one == two) return true;
/*     */ 
/*  90 */     if ((one.isParameterizedType()) && (two.isParameterizedType())) {
/*  91 */       return (one.isEquivalentTo(two)) && (two.isEquivalentTo(one));
/*     */     }
/*     */ 
/*  98 */     return false;
/*     */   }
/*     */   boolean canSkipInheritedMethods() {
/* 101 */     if ((this.type.superclass() != null) && (
/* 102 */       (this.type.superclass().isAbstract()) || (this.type.superclass().isParameterizedType())))
/* 103 */       return false;
/* 104 */     return this.type.superInterfaces() == Binding.NO_SUPERINTERFACES;
/*     */   }
/*     */ 
/*     */   boolean canSkipInheritedMethods(MethodBinding one, MethodBinding two) {
/* 108 */     return (two == null) || (
/* 108 */       (one.declaringClass == two.declaringClass) && (!one.declaringClass.isParameterizedType()));
/*     */   }
/*     */   void checkConcreteInheritedMethod(MethodBinding concreteMethod, MethodBinding[] abstractMethods) {
/* 111 */     super.checkConcreteInheritedMethod(concreteMethod, abstractMethods);
/*     */ 
/* 113 */     int i = 0; for (int l = abstractMethods.length; i < l; i++) {
/* 114 */       MethodBinding abstractMethod = abstractMethods[i];
/* 115 */       if (concreteMethod.isVarargs() != abstractMethod.isVarargs()) {
/* 116 */         problemReporter().varargsConflict(concreteMethod, abstractMethod, this.type);
/*     */       }
/*     */ 
/* 119 */       MethodBinding originalInherited = abstractMethod.original();
/* 120 */       if ((originalInherited.returnType != concreteMethod.returnType) && 
/* 121 */         (!isAcceptableReturnTypeOverride(concreteMethod, abstractMethod))) {
/* 122 */         problemReporter().unsafeReturnTypeOverride(concreteMethod, originalInherited, this.type);
/*     */       }
/*     */ 
/* 125 */       if ((!originalInherited.declaringClass.isInterface()) || (
/* 126 */         ((concreteMethod.declaringClass != this.type.superclass) || (!this.type.superclass.isParameterizedType())) && 
/* 127 */         (this.type.superclass.erasure().findSuperTypeOriginatingFrom(originalInherited.declaringClass) != null))) continue;
/* 128 */       this.type.addSyntheticBridgeMethod(originalInherited, concreteMethod.original());
/*     */     }
/*     */   }
/*     */ 
/*     */   void checkForBridgeMethod(MethodBinding currentMethod, MethodBinding inheritedMethod, MethodBinding[] allInheritedMethods) {
/* 133 */     if (currentMethod.isVarargs() != inheritedMethod.isVarargs()) {
/* 134 */       problemReporter(currentMethod).varargsConflict(currentMethod, inheritedMethod, this.type);
/*     */     }
/*     */ 
/* 137 */     MethodBinding originalInherited = inheritedMethod.original();
/* 138 */     if ((originalInherited.returnType != currentMethod.returnType) && 
/* 139 */       (!isAcceptableReturnTypeOverride(currentMethod, inheritedMethod))) {
/* 140 */       problemReporter(currentMethod).unsafeReturnTypeOverride(currentMethod, originalInherited, this.type);
/*     */     }
/* 142 */     if (this.type.addSyntheticBridgeMethod(originalInherited, currentMethod.original()) != null) {
/* 143 */       int i = 0; for (int l = allInheritedMethods == null ? 0 : allInheritedMethods.length; i < l; i++)
/* 144 */         if ((allInheritedMethods[i] != null) && (detectInheritedNameClash(originalInherited, allInheritedMethods[i].original())))
/* 145 */           return;
/*     */     }
/*     */   }
/*     */ 
/*     */   void checkForNameClash(MethodBinding currentMethod, MethodBinding inheritedMethod)
/*     */   {
/* 182 */     if ((currentMethod.declaringClass.isInterface()) || (inheritedMethod.isStatic())) return;
/*     */ 
/* 184 */     if (!detectNameClash(currentMethod, inheritedMethod)) {
/* 185 */       TypeBinding[] currentParams = currentMethod.parameters;
/* 186 */       TypeBinding[] inheritedParams = inheritedMethod.parameters;
/* 187 */       int length = currentParams.length;
/* 188 */       if (length != inheritedParams.length) return;
/*     */ 
/* 190 */       for (int i = 0; i < length; i++) {
/* 191 */         if ((currentParams[i] != inheritedParams[i]) && (
/* 192 */           (currentParams[i].isBaseType() != inheritedParams[i].isBaseType()) || (!inheritedParams[i].isCompatibleWith(currentParams[i]))))
/* 193 */           return;
/*     */       }
/* 195 */       ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/* 196 */       int nextPosition = 0;
/* 197 */       ReferenceBinding superType = inheritedMethod.declaringClass;
/* 198 */       ReferenceBinding[] itsInterfaces = superType.superInterfaces();
/* 199 */       if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
/* 200 */         nextPosition = itsInterfaces.length;
/* 201 */         interfacesToVisit = itsInterfaces;
/*     */       }
/* 203 */       superType = superType.superclass();
/* 204 */       while ((superType != null) && (superType.isValidBinding())) {
/* 205 */         MethodBinding[] methods = superType.getMethods(currentMethod.selector);
/* 206 */         int m = 0; for (int n = methods.length; m < n; m++) {
/* 207 */           MethodBinding substitute = computeSubstituteMethod(methods[m], currentMethod);
/* 208 */           if ((substitute != null) && (!isSubstituteParameterSubsignature(currentMethod, substitute)) && (detectNameClash(currentMethod, substitute)))
/* 209 */             return;
/*     */         }
/* 211 */         if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES)
/* 212 */           if (interfacesToVisit == null) {
/* 213 */             interfacesToVisit = itsInterfaces;
/* 214 */             nextPosition = interfacesToVisit.length;
/*     */           } else {
/* 216 */             int itsLength = itsInterfaces.length;
/* 217 */             if (nextPosition + itsLength >= interfacesToVisit.length)
/* 218 */               System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 219 */             for (int a = 0; a < itsLength; a++) {
/* 220 */               ReferenceBinding next = itsInterfaces[a];
/* 221 */               int b = 0;
/* 222 */               while (next != interfacesToVisit[b])
/*     */               {
/* 221 */                 b++; if (b < nextPosition)
/*     */                   continue;
/* 223 */                 interfacesToVisit[(nextPosition++)] = next;
/*     */               }
/*     */             }
/*     */           }
/* 227 */         superType = superType.superclass();
/*     */       }
/*     */ 
/* 230 */       for (int i = 0; i < nextPosition; i++) {
/* 231 */         superType = interfacesToVisit[i];
/* 232 */         if (superType.isValidBinding()) {
/* 233 */           MethodBinding[] methods = superType.getMethods(currentMethod.selector);
/* 234 */           int m = 0; for (int n = methods.length; m < n; m++) {
/* 235 */             MethodBinding substitute = computeSubstituteMethod(methods[m], currentMethod);
/* 236 */             if ((substitute != null) && (!isSubstituteParameterSubsignature(currentMethod, substitute)) && (detectNameClash(currentMethod, substitute)))
/* 237 */               return;
/*     */           }
/* 239 */           if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
/* 240 */             int itsLength = itsInterfaces.length;
/* 241 */             if (nextPosition + itsLength >= interfacesToVisit.length)
/* 242 */               System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 243 */             for (int a = 0; a < itsLength; a++) {
/* 244 */               ReferenceBinding next = itsInterfaces[a];
/* 245 */               int b = 0;
/* 246 */               while (next != interfacesToVisit[b])
/*     */               {
/* 245 */                 b++; if (b < nextPosition)
/*     */                   continue;
/* 247 */                 interfacesToVisit[(nextPosition++)] = next;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void checkInheritedMethods(MethodBinding inheritedMethod, MethodBinding otherInheritedMethod) {
/* 256 */     if (inheritedMethod.declaringClass.erasure() == otherInheritedMethod.declaringClass.erasure()) {
/* 257 */       boolean areDuplicates = (inheritedMethod.hasSubstitutedParameters()) && (otherInheritedMethod.hasSubstitutedParameters()) ? 
/* 258 */         inheritedMethod.areParametersEqual(otherInheritedMethod) : 
/* 259 */         inheritedMethod.areParameterErasuresEqual(otherInheritedMethod);
/* 260 */       if (areDuplicates) {
/* 261 */         problemReporter().duplicateInheritedMethods(this.type, inheritedMethod, otherInheritedMethod);
/* 262 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 276 */     if ((inheritedMethod.declaringClass.isInterface()) || (inheritedMethod.isStatic())) return;
/*     */ 
/* 278 */     detectInheritedNameClash(inheritedMethod.original(), otherInheritedMethod.original());
/*     */   }
/*     */   void checkInheritedMethods(MethodBinding[] methods, int length) {
/* 281 */     int count = length;
/* 282 */     int[] skip = new int[count];
/* 283 */     int i = 0; for (int l = length - 1; i < l; i++) {
/* 284 */       if (skip[i] != -1) {
/* 285 */         MethodBinding method = methods[i];
/* 286 */         MethodBinding[] duplicates = (MethodBinding[])null;
/* 287 */         for (int j = i + 1; j <= l; j++) {
/* 288 */           MethodBinding method2 = methods[j];
/* 289 */           if ((method.declaringClass == method2.declaringClass) && (areMethodsCompatible(method, method2))) {
/* 290 */             skip[j] = -1;
/* 291 */             if (duplicates == null)
/* 292 */               duplicates = new MethodBinding[length];
/* 293 */             duplicates[j] = method2;
/*     */           }
/*     */         }
/* 296 */         if (duplicates == null)
/*     */         {
/*     */           continue;
/*     */         }
/* 300 */         int concreteCount = method.isAbstract() ? 0 : 1;
/* 301 */         MethodBinding methodToKeep = method;
/* 302 */         int m = 0; for (int s = duplicates.length; m < s; m++) {
/* 303 */           if ((duplicates[m] == null) || 
/* 304 */             (duplicates[m].isAbstract())) continue;
/* 305 */           methodToKeep = duplicates[m];
/* 306 */           concreteCount++;
/*     */         }
/*     */ 
/* 310 */         if (concreteCount != 1) {
/* 311 */           int m = 0; for (int s = duplicates.length; m < s; m++) {
/* 312 */             if (duplicates[m] != null) {
/* 313 */               problemReporter().duplicateInheritedMethods(this.type, method, duplicates[m]);
/* 314 */               count--;
/* 315 */               if (methodToKeep == duplicates[m])
/* 316 */                 methods[i] = null;
/*     */               else
/* 318 */                 methods[m] = null;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 324 */     if (count < length) {
/* 325 */       if (count == 1) return;
/* 326 */       MethodBinding[] newMethods = new MethodBinding[count];
/* 327 */       int i = length;
/*     */       do { if (methods[i] != null) {
/* 329 */           count--; newMethods[count] = methods[i];
/*     */         }
/* 327 */         i--; } while (i >= 0);
/*     */ 
/* 330 */       methods = newMethods;
/* 331 */       length = newMethods.length;
/*     */     }
/*     */ 
/* 334 */     super.checkInheritedMethods(methods, length);
/*     */   }
/*     */   boolean checkInheritedReturnTypes(MethodBinding method, MethodBinding otherMethod) {
/* 337 */     if (areReturnTypesCompatible(method, otherMethod)) return true;
/*     */ 
/* 339 */     if ((!this.type.isInterface()) && 
/* 340 */       ((method.declaringClass.isClass()) || (!this.type.implementsInterface(method.declaringClass, false))) && (
/* 341 */       (otherMethod.declaringClass.isClass()) || (!this.type.implementsInterface(otherMethod.declaringClass, false)))) {
/* 342 */       return true;
/*     */     }
/*     */ 
/* 345 */     if (isUnsafeReturnTypeOverride(method, otherMethod)) {
/* 346 */       if (!method.declaringClass.implementsInterface(otherMethod.declaringClass, false))
/* 347 */         problemReporter(method).unsafeReturnTypeOverride(method, otherMethod, this.type);
/* 348 */       return true;
/*     */     }
/*     */ 
/* 351 */     return false;
/*     */   }
/*     */   void checkMethods() {
/* 354 */     boolean mustImplementAbstractMethods = mustImplementAbstractMethods();
/* 355 */     boolean skipInheritedMethods = (mustImplementAbstractMethods) && (canSkipInheritedMethods());
/* 356 */     char[][] methodSelectors = this.inheritedMethods.keyTable;
/* 357 */     int s = methodSelectors.length;
/*     */     do { if (methodSelectors[s] != null)
/*     */       {
/* 360 */         MethodBinding[] current = (MethodBinding[])this.currentMethods.get(methodSelectors[s]);
/* 361 */         if ((current != null) || (!skipInheritedMethods))
/*     */         {
/* 364 */           MethodBinding[] inherited = (MethodBinding[])this.inheritedMethods.valueTable[s];
/* 365 */           if ((inherited.length == 1) && (current == null)) {
/* 366 */             if ((mustImplementAbstractMethods) && (inherited[0].isAbstract()))
/* 367 */               checkAbstractMethod(inherited[0]);
/*     */           }
/*     */           else
/*     */           {
/* 371 */             int index = -1;
/* 372 */             int inheritedLength = inherited.length;
/* 373 */             MethodBinding[] matchingInherited = new MethodBinding[inherited.length];
/* 374 */             MethodBinding[] foundMatch = new MethodBinding[inherited.length];
/* 375 */             if (current != null) {
/* 376 */               int i = 0; for (int length1 = current.length; i < length1; i++) {
/* 377 */                 MethodBinding currentMethod = current[i];
/* 378 */                 MethodBinding[] nonMatchingInherited = (MethodBinding[])null;
/* 379 */                 for (int j = 0; j < inheritedLength; j++) {
/* 380 */                   MethodBinding inheritedMethod = computeSubstituteMethod(inherited[j], currentMethod);
/* 381 */                   if (inheritedMethod != null) {
/* 382 */                     if ((foundMatch[j] == null) && (isSubstituteParameterSubsignature(currentMethod, inheritedMethod))) {
/* 383 */                       index++; matchingInherited[index] = inheritedMethod;
/* 384 */                       foundMatch[j] = currentMethod;
/*     */                     }
/*     */                     else {
/* 387 */                       checkForNameClash(currentMethod, inheritedMethod);
/* 388 */                       if (inheritedLength > 1) {
/* 389 */                         if (nonMatchingInherited == null)
/* 390 */                           nonMatchingInherited = new MethodBinding[inheritedLength];
/* 391 */                         nonMatchingInherited[j] = inheritedMethod;
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/* 396 */                 if (index < 0)
/*     */                 {
/*     */                   continue;
/*     */                 }
/* 400 */                 checkAgainstInheritedMethods(currentMethod, matchingInherited, index + 1, nonMatchingInherited);
/* 401 */                 while (index >= 0) matchingInherited[(index--)] = null;
/*     */ 
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 408 */             boolean[] skip = new boolean[inheritedLength];
/* 409 */             for (int i = 0; i < inheritedLength; i++)
/* 410 */               if (skip[i] == 0) {
/* 411 */                 MethodBinding inheritedMethod = inherited[i];
/* 412 */                 MethodBinding matchMethod = foundMatch[i];
/* 413 */                 if (matchMethod == null) {
/* 414 */                   index++; matchingInherited[index] = inheritedMethod;
/* 415 */                 }for (int j = i + 1; j < inheritedLength; j++) {
/* 416 */                   MethodBinding otherInheritedMethod = inherited[j];
/* 417 */                   if ((matchMethod == foundMatch[j]) && (matchMethod != null))
/*     */                     continue;
/* 419 */                   if (canSkipInheritedMethods(inheritedMethod, otherInheritedMethod))
/*     */                     continue;
/* 421 */                   otherInheritedMethod = computeSubstituteMethod(otherInheritedMethod, inheritedMethod);
/* 422 */                   if (otherInheritedMethod != null) {
/* 423 */                     if ((inheritedMethod.declaringClass != otherInheritedMethod.declaringClass) && 
/* 424 */                       (isSubstituteParameterSubsignature(inheritedMethod, otherInheritedMethod))) {
/* 425 */                       if (index == -1) {
/* 426 */                         index++; matchingInherited[index] = inheritedMethod;
/* 427 */                       }if (foundMatch[j] == null) {
/* 428 */                         index++; matchingInherited[index] = otherInheritedMethod;
/* 429 */                       }skip[j] = true;
/* 430 */                     } else if ((matchMethod == null) && (foundMatch[j] == null)) {
/* 431 */                       checkInheritedMethods(inheritedMethod, otherInheritedMethod);
/*     */                     }
/*     */                   }
/*     */                 }
/* 435 */                 if (index == -1)
/*     */                   continue;
/* 437 */                 if (index > 0)
/* 438 */                   checkInheritedMethods(matchingInherited, index + 1);
/* 439 */                 else if ((mustImplementAbstractMethods) && (matchingInherited[0].isAbstract()) && (matchMethod == null))
/* 440 */                   checkAbstractMethod(matchingInherited[0]);
/* 441 */                 while (index >= 0) matchingInherited[(index--)] = null;
/*     */               }
/*     */           }
/*     */         }
/*     */       }
/* 357 */       s--; } while (s >= 0);
/*     */   }
/*     */ 
/*     */   void checkTypeVariableMethods(TypeParameter typeParameter)
/*     */   {
/* 446 */     char[][] methodSelectors = this.inheritedMethods.keyTable;
/* 447 */     int s = methodSelectors.length;
/*     */     do { if (methodSelectors[s] != null) {
/* 449 */         MethodBinding[] inherited = (MethodBinding[])this.inheritedMethods.valueTable[s];
/* 450 */         if (inherited.length != 1)
/*     */         {
/* 452 */           int index = -1;
/* 453 */           MethodBinding[] matchingInherited = new MethodBinding[inherited.length];
/* 454 */           int i = 0; for (int length = inherited.length; i < length; i++) {
/* 455 */             while (index >= 0) matchingInherited[(index--)] = null;
/* 456 */             MethodBinding inheritedMethod = inherited[i];
/* 457 */             if (inheritedMethod != null) {
/* 458 */               index++; matchingInherited[index] = inheritedMethod;
/* 459 */               for (int j = i + 1; j < length; j++) {
/* 460 */                 MethodBinding otherInheritedMethod = inherited[j];
/* 461 */                 if (canSkipInheritedMethods(inheritedMethod, otherInheritedMethod))
/*     */                   continue;
/* 463 */                 otherInheritedMethod = computeSubstituteMethod(otherInheritedMethod, inheritedMethod);
/* 464 */                 if ((otherInheritedMethod != null) && (isSubstituteParameterSubsignature(inheritedMethod, otherInheritedMethod))) {
/* 465 */                   index++; matchingInherited[index] = otherInheritedMethod;
/* 466 */                   inherited[j] = null;
/*     */                 }
/*     */               }
/*     */             }
/* 470 */             if (index > 0) {
/* 471 */               MethodBinding first = matchingInherited[0];
/* 472 */               int count = index + 1;
/*     */               do count--; while ((count > 0) && (areReturnTypesCompatible(first, matchingInherited[count])));
/* 474 */               if (count > 0) {
/* 475 */                 problemReporter().inheritedMethodsHaveIncompatibleReturnTypes(typeParameter, matchingInherited, index + 1);
/* 476 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 447 */       s--; } while (s >= 0);
/*     */   }
/*     */ 
/*     */   MethodBinding computeSubstituteMethod(MethodBinding inheritedMethod, MethodBinding currentMethod)
/*     */   {
/* 483 */     if (inheritedMethod == null) return null;
/* 484 */     if (currentMethod.parameters.length != inheritedMethod.parameters.length) return null;
/*     */ 
/* 487 */     if ((currentMethod.declaringClass instanceof BinaryTypeBinding))
/* 488 */       ((BinaryTypeBinding)currentMethod.declaringClass).resolveTypesFor(currentMethod);
/* 489 */     if ((inheritedMethod.declaringClass instanceof BinaryTypeBinding)) {
/* 490 */       ((BinaryTypeBinding)inheritedMethod.declaringClass).resolveTypesFor(inheritedMethod);
/*     */     }
/* 492 */     TypeVariableBinding[] inheritedTypeVariables = inheritedMethod.typeVariables;
/* 493 */     int inheritedLength = inheritedTypeVariables.length;
/* 494 */     if (inheritedLength == 0) return inheritedMethod;
/* 495 */     TypeVariableBinding[] typeVariables = currentMethod.typeVariables;
/* 496 */     int length = typeVariables.length;
/* 497 */     if (length == 0)
/* 498 */       return inheritedMethod.asRawMethod(this.environment);
/* 499 */     if (length != inheritedLength) {
/* 500 */       return inheritedMethod;
/*     */     }
/*     */ 
/* 505 */     TypeBinding[] arguments = new TypeBinding[length];
/* 506 */     System.arraycopy(typeVariables, 0, arguments, 0, length);
/* 507 */     ParameterizedGenericMethodBinding substitute = 
/* 508 */       this.environment.createParameterizedGenericMethod(inheritedMethod, arguments);
/* 509 */     for (int i = 0; i < inheritedLength; i++) {
/* 510 */       TypeVariableBinding inheritedTypeVariable = inheritedTypeVariables[i];
/* 511 */       TypeBinding argument = arguments[i];
/* 512 */       if ((argument instanceof TypeVariableBinding)) {
/* 513 */         TypeVariableBinding typeVariable = (TypeVariableBinding)argument;
/* 514 */         if (typeVariable.firstBound == inheritedTypeVariable.firstBound) {
/* 515 */           if (typeVariable.firstBound == null)
/* 516 */             continue;
/* 517 */         } else if ((typeVariable.firstBound != null) && (inheritedTypeVariable.firstBound != null) && 
/* 518 */           (typeVariable.firstBound.isClass() != inheritedTypeVariable.firstBound.isClass())) {
/* 519 */           return inheritedMethod;
/*     */         }
/* 521 */         if (Scope.substitute(substitute, inheritedTypeVariable.superclass) != typeVariable.superclass)
/* 522 */           return inheritedMethod;
/* 523 */         int interfaceLength = inheritedTypeVariable.superInterfaces.length;
/* 524 */         ReferenceBinding[] interfaces = typeVariable.superInterfaces;
/* 525 */         if (interfaceLength != interfaces.length) {
/* 526 */           return inheritedMethod;
/*     */         }
/* 528 */         for (int j = 0; j < interfaceLength; j++) {
/* 529 */           TypeBinding superType = Scope.substitute(substitute, inheritedTypeVariable.superInterfaces[j]);
/* 530 */           int k = 0;
/* 531 */           while (superType != interfaces[k])
/*     */           {
/* 530 */             k++; if (k >= interfaceLength)
/*     */             {
/* 533 */               return inheritedMethod;
/*     */             }
/*     */           }
/*     */         }
/* 535 */       } else if (inheritedTypeVariable.boundCheck(substitute, argument) != 0) {
/* 536 */         return inheritedMethod;
/*     */       }
/*     */     }
/* 539 */     return substitute;
/*     */   }
/*     */   boolean detectInheritedNameClash(MethodBinding inherited, MethodBinding otherInherited) {
/* 542 */     if (!inherited.areParameterErasuresEqual(otherInherited))
/* 543 */       return false;
/* 544 */     if (this.environment.globalOptions.sourceLevel < 3342336L)
/*     */     {
/* 548 */       if (inherited.returnType.erasure() != otherInherited.returnType.erasure()) {
/* 549 */         return false;
/*     */       }
/*     */     }
/* 552 */     if ((inherited.declaringClass.erasure() != otherInherited.declaringClass.erasure()) && 
/* 553 */       (inherited.declaringClass.findSuperTypeOriginatingFrom(otherInherited.declaringClass) != null)) {
/* 554 */       return false;
/*     */     }
/* 556 */     problemReporter().inheritedMethodsHaveNameClash(this.type, inherited, otherInherited);
/* 557 */     return true;
/*     */   }
/*     */   boolean detectNameClash(MethodBinding current, MethodBinding inherited) {
/* 560 */     MethodBinding original = inherited.original();
/* 561 */     if (!current.areParameterErasuresEqual(original))
/* 562 */       return false;
/* 563 */     if (this.environment.globalOptions.sourceLevel < 3342336L)
/*     */     {
/* 567 */       if (current.returnType.erasure() != original.returnType.erasure()) {
/* 568 */         return false;
/*     */       }
/*     */     }
/* 571 */     problemReporter(current).methodNameClash(current, inherited.declaringClass.isRawType() ? inherited : original);
/* 572 */     return true;
/*     */   }
/*     */   public boolean doesMethodOverride(MethodBinding method, MethodBinding inheritedMethod) {
/* 575 */     return (couldMethodOverride(method, inheritedMethod)) && (areMethodsCompatible(method, inheritedMethod));
/*     */   }
/*     */ 
/*     */   boolean doTypeVariablesClash(MethodBinding one, MethodBinding substituteTwo) {
/* 579 */     return (one.typeVariables != Binding.NO_TYPE_VARIABLES) && (!(substituteTwo instanceof ParameterizedGenericMethodBinding));
/*     */   }
/*     */   SimpleSet findSuperinterfaceCollisions(ReferenceBinding superclass, ReferenceBinding[] superInterfaces) {
/* 582 */     ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/* 583 */     int nextPosition = 0;
/* 584 */     ReferenceBinding[] itsInterfaces = superInterfaces;
/* 585 */     if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
/* 586 */       nextPosition = itsInterfaces.length;
/* 587 */       interfacesToVisit = itsInterfaces;
/*     */     }
/*     */ 
/* 590 */     boolean isInconsistent = this.type.isHierarchyInconsistent();
/* 591 */     ReferenceBinding superType = superclass;
/* 592 */     while ((superType != null) && (superType.isValidBinding())) {
/* 593 */       isInconsistent |= superType.isHierarchyInconsistent();
/* 594 */       if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES)
/* 595 */         if (interfacesToVisit == null) {
/* 596 */           interfacesToVisit = itsInterfaces;
/* 597 */           nextPosition = interfacesToVisit.length;
/*     */         } else {
/* 599 */           int itsLength = itsInterfaces.length;
/* 600 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/* 601 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 602 */           for (int a = 0; a < itsLength; a++) {
/* 603 */             ReferenceBinding next = itsInterfaces[a];
/* 604 */             int b = 0;
/* 605 */             while (next != interfacesToVisit[b])
/*     */             {
/* 604 */               b++; if (b < nextPosition)
/*     */                 continue;
/* 606 */               interfacesToVisit[(nextPosition++)] = next;
/*     */             }
/*     */           }
/*     */         }
/* 610 */       superType = superType.superclass();
/*     */     }
/*     */ 
/* 613 */     for (int i = 0; i < nextPosition; i++) {
/* 614 */       superType = interfacesToVisit[i];
/* 615 */       if (superType.isValidBinding()) {
/* 616 */         isInconsistent |= superType.isHierarchyInconsistent();
/* 617 */         if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
/* 618 */           int itsLength = itsInterfaces.length;
/* 619 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/* 620 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 621 */           for (int a = 0; a < itsLength; a++) {
/* 622 */             ReferenceBinding next = itsInterfaces[a];
/* 623 */             int b = 0;
/* 624 */             while (next != interfacesToVisit[b])
/*     */             {
/* 623 */               b++; if (b < nextPosition)
/*     */                 continue;
/* 625 */               interfacesToVisit[(nextPosition++)] = next;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 631 */     if (!isInconsistent) return null;
/* 632 */     SimpleSet copy = null;
/* 633 */     for (int i = 0; i < nextPosition; i++) {
/* 634 */       ReferenceBinding current = interfacesToVisit[i];
/* 635 */       if (current.isValidBinding()) {
/* 636 */         TypeBinding erasure = current.erasure();
/* 637 */         for (int j = i + 1; j < nextPosition; j++) {
/* 638 */           ReferenceBinding next = interfacesToVisit[j];
/* 639 */           if ((next.isValidBinding()) && (next.erasure() == erasure)) {
/* 640 */             if (copy == null)
/* 641 */               copy = new SimpleSet(nextPosition);
/* 642 */             copy.add(interfacesToVisit[i]);
/* 643 */             copy.add(interfacesToVisit[j]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 648 */     return copy;
/*     */   }
/*     */   boolean hasGenericParameter(MethodBinding method) {
/* 651 */     if (method.genericSignature() == null) return false;
/*     */ 
/* 654 */     TypeBinding[] params = method.parameters;
/* 655 */     int i = 0; for (int l = params.length; i < l; i++) {
/* 656 */       TypeBinding param = params[i].leafComponentType();
/* 657 */       if ((param instanceof ReferenceBinding)) {
/* 658 */         int modifiers = ((ReferenceBinding)param).modifiers;
/* 659 */         if ((modifiers & 0x40000000) != 0)
/* 660 */           return true;
/*     */       }
/*     */     }
/* 663 */     return false;
/*     */   }
/*     */ 
/*     */   boolean isAcceptableReturnTypeOverride(MethodBinding currentMethod, MethodBinding inheritedMethod)
/*     */   {
/* 668 */     if (inheritedMethod.declaringClass.isRawType()) {
/* 669 */       return true;
/*     */     }
/* 671 */     MethodBinding originalInherited = inheritedMethod.original();
/* 672 */     TypeBinding originalInheritedReturnType = originalInherited.returnType.leafComponentType();
/* 673 */     if (originalInheritedReturnType.isParameterizedTypeWithActualArguments()) {
/* 674 */       return !currentMethod.returnType.leafComponentType().isRawType();
/*     */     }
/* 676 */     TypeBinding currentReturnType = currentMethod.returnType.leafComponentType();
/* 677 */     switch (currentReturnType.kind()) {
/*     */     case 4100:
/* 679 */       if (currentReturnType != inheritedMethod.returnType.leafComponentType()) break;
/* 680 */       return true;
/*     */     }
/*     */ 
/* 685 */     return (!originalInheritedReturnType.isTypeVariable()) || 
/* 684 */       (((TypeVariableBinding)originalInheritedReturnType).declaringElement != originalInherited);
/*     */   }
/*     */ 
/*     */   boolean isInterfaceMethodImplemented(MethodBinding inheritedMethod, MethodBinding existingMethod, ReferenceBinding superType)
/*     */   {
/* 691 */     if ((inheritedMethod.original() != inheritedMethod) && (existingMethod.declaringClass.isInterface())) {
/* 692 */       return false;
/*     */     }
/* 694 */     inheritedMethod = computeSubstituteMethod(inheritedMethod, existingMethod);
/*     */ 
/* 697 */     return (inheritedMethod != null) && 
/* 696 */       (inheritedMethod.returnType == existingMethod.returnType) && 
/* 697 */       (doesMethodOverride(existingMethod, inheritedMethod));
/*     */   }
/*     */   public boolean isMethodSubsignature(MethodBinding method, MethodBinding inheritedMethod) {
/* 700 */     if (!CharOperation.equals(method.selector, inheritedMethod.selector)) {
/* 701 */       return false;
/*     */     }
/*     */ 
/* 704 */     if (method.declaringClass.isParameterizedType()) {
/* 705 */       method = method.original();
/*     */     }
/* 707 */     MethodBinding inheritedOriginal = method.findOriginalInheritedMethod(inheritedMethod);
/* 708 */     return isParameterSubsignature(method, inheritedOriginal == null ? inheritedMethod : inheritedOriginal);
/*     */   }
/*     */   boolean isParameterSubsignature(MethodBinding method, MethodBinding inheritedMethod) {
/* 711 */     MethodBinding substitute = computeSubstituteMethod(inheritedMethod, method);
/* 712 */     return (substitute != null) && (isSubstituteParameterSubsignature(method, substitute));
/*     */   }
/*     */ 
/*     */   boolean isSubstituteParameterSubsignature(MethodBinding method, MethodBinding substituteMethod)
/*     */   {
/* 717 */     if (!areParametersEqual(method, substituteMethod))
/*     */     {
/* 722 */       if ((substituteMethod.hasSubstitutedParameters()) && (method.areParameterErasuresEqual(substituteMethod))) {
/* 723 */         return (method.typeVariables == Binding.NO_TYPE_VARIABLES) && (!hasGenericParameter(method));
/*     */       }
/*     */ 
/* 726 */       if ((method.declaringClass.isRawType()) && (substituteMethod.declaringClass.isRawType()) && 
/* 727 */         (method.hasSubstitutedParameters()) && (substituteMethod.hasSubstitutedParameters())) {
/* 728 */         return areMethodsCompatible(method, substituteMethod);
/*     */       }
/* 730 */       return false;
/*     */     }
/*     */ 
/* 733 */     if ((substituteMethod instanceof ParameterizedGenericMethodBinding)) {
/* 734 */       if (method.typeVariables != Binding.NO_TYPE_VARIABLES) {
/* 735 */         return !((ParameterizedGenericMethodBinding)substituteMethod).isRaw;
/*     */       }
/* 737 */       return !hasGenericParameter(method);
/*     */     }
/*     */ 
/* 741 */     return method.typeVariables == Binding.NO_TYPE_VARIABLES;
/*     */   }
/*     */ 
/*     */   boolean isUnsafeReturnTypeOverride(MethodBinding currentMethod, MethodBinding inheritedMethod)
/*     */   {
/* 747 */     if (currentMethod.returnType == inheritedMethod.returnType.erasure()) {
/* 748 */       TypeBinding[] currentParams = currentMethod.parameters;
/* 749 */       TypeBinding[] inheritedParams = inheritedMethod.parameters;
/* 750 */       int i = 0; for (int l = currentParams.length; i < l; i++) {
/* 751 */         if (!areTypesEqual(currentParams[i], inheritedParams[i])) {
/* 752 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 757 */     return (currentMethod.typeVariables == Binding.NO_TYPE_VARIABLES) && 
/* 755 */       (inheritedMethod.original().typeVariables != Binding.NO_TYPE_VARIABLES) && 
/* 756 */       (currentMethod.returnType.erasure().findSuperTypeOriginatingFrom(inheritedMethod.returnType.erasure()) != null);
/*     */   }
/*     */ 
/*     */   boolean reportIncompatibleReturnTypeError(MethodBinding currentMethod, MethodBinding inheritedMethod)
/*     */   {
/* 762 */     if (isUnsafeReturnTypeOverride(currentMethod, inheritedMethod)) {
/* 763 */       problemReporter(currentMethod).unsafeReturnTypeOverride(currentMethod, inheritedMethod, this.type);
/* 764 */       return false;
/*     */     }
/* 766 */     return super.reportIncompatibleReturnTypeError(currentMethod, inheritedMethod);
/*     */   }
/*     */   void verify() {
/* 769 */     if (this.type.isAnnotationType()) {
/* 770 */       this.type.detectAnnotationCycle();
/*     */     }
/* 772 */     super.verify();
/*     */ 
/* 774 */     int i = this.type.typeVariables.length;
/*     */     do { TypeVariableBinding var = this.type.typeVariables[i];
/*     */ 
/* 777 */       if ((var.superInterfaces != Binding.NO_SUPERINTERFACES) && (
/* 778 */         (var.superInterfaces.length != 1) || (var.superclass.id != 1)))
/*     */       {
/* 780 */         this.currentMethods = new HashtableOfObject(0);
/* 781 */         ReferenceBinding superclass = var.superclass();
/* 782 */         if (superclass.kind() == 4100)
/* 783 */           superclass = (ReferenceBinding)superclass.erasure();
/* 784 */         ReferenceBinding[] itsInterfaces = var.superInterfaces();
/* 785 */         ReferenceBinding[] superInterfaces = new ReferenceBinding[itsInterfaces.length];
/* 786 */         int j = itsInterfaces.length;
/*     */         do { superInterfaces[j] = (itsInterfaces[j].kind() == 4100 ? 
/* 788 */             (ReferenceBinding)itsInterfaces[j].erasure() : 
/* 789 */             itsInterfaces[j]);
/*     */ 
/* 786 */           j--; } while (j >= 0);
/*     */ 
/* 791 */         computeInheritedMethods(superclass, superInterfaces);
/* 792 */         checkTypeVariableMethods(this.type.scope.referenceContext.typeParameters[i]);
/*     */       }
/* 774 */       i--; } while (i >= 0);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.MethodVerifier15
 * JD-Core Version:    0.6.0
 */