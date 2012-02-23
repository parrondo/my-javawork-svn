/*      */ package org.eclipse.jdt.internal.compiler.lookup;
/*      */ 
/*      */ import java.util.List;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class MethodBinding extends Binding
/*      */ {
/*      */   public int modifiers;
/*      */   public char[] selector;
/*      */   public TypeBinding returnType;
/*      */   public TypeBinding[] parameters;
/*      */   public ReferenceBinding[] thrownExceptions;
/*      */   public ReferenceBinding declaringClass;
/*   33 */   public TypeVariableBinding[] typeVariables = Binding.NO_TYPE_VARIABLES;
/*      */   char[] signature;
/*      */   public long tagBits;
/*      */ 
/*      */   protected MethodBinding()
/*      */   {
/*      */   }
/*      */ 
/*      */   public MethodBinding(int modifiers, char[] selector, TypeBinding returnType, TypeBinding[] parameters, ReferenceBinding[] thrownExceptions, ReferenceBinding declaringClass)
/*      */   {
/*   41 */     this.modifiers = modifiers;
/*   42 */     this.selector = selector;
/*   43 */     this.returnType = returnType;
/*   44 */     this.parameters = ((parameters == null) || (parameters.length == 0) ? Binding.NO_PARAMETERS : parameters);
/*   45 */     this.thrownExceptions = ((thrownExceptions == null) || (thrownExceptions.length == 0) ? Binding.NO_EXCEPTIONS : thrownExceptions);
/*   46 */     this.declaringClass = declaringClass;
/*      */ 
/*   49 */     if ((this.declaringClass != null) && 
/*   50 */       (this.declaringClass.isStrictfp()) && 
/*   51 */       (!isNative()) && (!isAbstract()))
/*   52 */       this.modifiers |= 2048;
/*      */   }
/*      */ 
/*      */   public MethodBinding(int modifiers, TypeBinding[] parameters, ReferenceBinding[] thrownExceptions, ReferenceBinding declaringClass) {
/*   56 */     this(modifiers, TypeConstants.INIT, TypeBinding.VOID, parameters, thrownExceptions, declaringClass);
/*      */   }
/*      */ 
/*      */   public MethodBinding(MethodBinding initialMethodBinding, ReferenceBinding declaringClass) {
/*   60 */     this.modifiers = initialMethodBinding.modifiers;
/*   61 */     this.selector = initialMethodBinding.selector;
/*   62 */     this.returnType = initialMethodBinding.returnType;
/*   63 */     this.parameters = initialMethodBinding.parameters;
/*   64 */     this.thrownExceptions = initialMethodBinding.thrownExceptions;
/*   65 */     this.declaringClass = declaringClass;
/*   66 */     declaringClass.storeAnnotationHolder(this, initialMethodBinding.declaringClass.retrieveAnnotationHolder(initialMethodBinding, true));
/*      */   }
/*      */ 
/*      */   public final boolean areParameterErasuresEqual(MethodBinding method)
/*      */   {
/*   71 */     TypeBinding[] args = method.parameters;
/*   72 */     if (this.parameters == args) {
/*   73 */       return true;
/*      */     }
/*   75 */     int length = this.parameters.length;
/*   76 */     if (length != args.length) {
/*   77 */       return false;
/*      */     }
/*   79 */     for (int i = 0; i < length; i++)
/*   80 */       if ((this.parameters[i] != args[i]) && (this.parameters[i].erasure() != args[i].erasure()))
/*   81 */         return false;
/*   82 */     return true;
/*      */   }
/*      */ 
/*      */   public final boolean areParametersCompatibleWith(TypeBinding[] arguments)
/*      */   {
/*   90 */     int paramLength = this.parameters.length;
/*   91 */     int argLength = arguments.length;
/*   92 */     int lastIndex = argLength;
/*   93 */     if (isVarargs()) {
/*   94 */       lastIndex = paramLength - 1;
/*   95 */       if (paramLength == argLength) {
/*   96 */         TypeBinding varArgType = this.parameters[lastIndex];
/*   97 */         TypeBinding lastArgument = arguments[lastIndex];
/*   98 */         if ((varArgType != lastArgument) && (!lastArgument.isCompatibleWith(varArgType)))
/*   99 */           return false;
/*  100 */       } else if (paramLength < argLength) {
/*  101 */         TypeBinding varArgType = ((ArrayBinding)this.parameters[lastIndex]).elementsType();
/*  102 */         for (int i = lastIndex; i < argLength; i++)
/*  103 */           if ((varArgType != arguments[i]) && (!arguments[i].isCompatibleWith(varArgType)))
/*  104 */             return false;
/*  105 */       } else if (lastIndex != argLength) {
/*  106 */         return false;
/*      */       }
/*      */     }
/*      */ 
/*  110 */     for (int i = 0; i < lastIndex; i++)
/*  111 */       if ((this.parameters[i] != arguments[i]) && (!arguments[i].isCompatibleWith(this.parameters[i])))
/*  112 */         return false;
/*  113 */     return true;
/*      */   }
/*      */ 
/*      */   public final boolean areParametersEqual(MethodBinding method)
/*      */   {
/*  118 */     TypeBinding[] args = method.parameters;
/*  119 */     if (this.parameters == args) {
/*  120 */       return true;
/*      */     }
/*  122 */     int length = this.parameters.length;
/*  123 */     if (length != args.length) {
/*  124 */       return false;
/*      */     }
/*  126 */     for (int i = 0; i < length; i++)
/*  127 */       if (this.parameters[i] != args[i])
/*  128 */         return false;
/*  129 */     return true;
/*      */   }
/*      */ 
/*      */   public final boolean areTypeVariableErasuresEqual(MethodBinding method)
/*      */   {
/*  139 */     TypeVariableBinding[] vars = method.typeVariables;
/*  140 */     if (this.typeVariables == vars) {
/*  141 */       return true;
/*      */     }
/*  143 */     int length = this.typeVariables.length;
/*  144 */     if (length != vars.length) {
/*  145 */       return false;
/*      */     }
/*  147 */     for (int i = 0; i < length; i++)
/*  148 */       if ((this.typeVariables[i] != vars[i]) && (this.typeVariables[i].erasure() != vars[i].erasure()))
/*  149 */         return false;
/*  150 */     return true;
/*      */   }
/*      */   MethodBinding asRawMethod(LookupEnvironment env) {
/*  153 */     if (this.typeVariables == Binding.NO_TYPE_VARIABLES) return this;
/*      */ 
/*  156 */     int length = this.typeVariables.length;
/*  157 */     TypeBinding[] arguments = new TypeBinding[length];
/*  158 */     for (int i = 0; i < length; i++) {
/*  159 */       TypeVariableBinding var = this.typeVariables[i];
/*  160 */       if (var.boundsCount() <= 1) {
/*  161 */         arguments[i] = env.convertToRawType(var.upperBound(), false);
/*      */       }
/*      */       else {
/*  164 */         TypeBinding rawSuperclass = env.convertToRawType(var.superclass(), false);
/*  165 */         TypeBinding[] itsSuperinterfaces = var.superInterfaces();
/*  166 */         int superLength = itsSuperinterfaces.length;
/*  167 */         TypeBinding[] rawSuperinterfaces = new TypeBinding[superLength];
/*  168 */         for (int s = 0; s < superLength; s++)
/*  169 */           rawSuperinterfaces[s] = env.convertToRawType(itsSuperinterfaces[s], false);
/*  170 */         arguments[i] = env.createWildcard(null, 0, rawSuperclass, rawSuperinterfaces, 1);
/*      */       }
/*      */     }
/*  173 */     return env.createParameterizedGenericMethod(this, arguments);
/*      */   }
/*      */ 
/*      */   public final boolean canBeSeenBy(InvocationSite invocationSite, Scope scope)
/*      */   {
/*  185 */     if (isPublic()) return true;
/*      */ 
/*  187 */     SourceTypeBinding invocationType = scope.enclosingSourceType();
/*  188 */     if (invocationType == this.declaringClass) return true;
/*      */ 
/*  190 */     if (isProtected())
/*      */     {
/*  192 */       if (invocationType.fPackage == this.declaringClass.fPackage) return true;
/*  193 */       return invocationSite.isSuperAccess();
/*      */     }
/*      */ 
/*  196 */     if (isPrivate())
/*      */     {
/*  199 */       ReferenceBinding outerInvocationType = invocationType;
/*  200 */       ReferenceBinding temp = outerInvocationType.enclosingType();
/*  201 */       while (temp != null) {
/*  202 */         outerInvocationType = temp;
/*  203 */         temp = temp.enclosingType();
/*      */       }
/*      */ 
/*  206 */       ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.declaringClass.erasure();
/*  207 */       temp = outerDeclaringClass.enclosingType();
/*  208 */       while (temp != null) {
/*  209 */         outerDeclaringClass = temp;
/*  210 */         temp = temp.enclosingType();
/*      */       }
/*  212 */       return outerInvocationType == outerDeclaringClass;
/*      */     }
/*      */ 
/*  216 */     return invocationType.fPackage == this.declaringClass.fPackage;
/*      */   }
/*      */   public final boolean canBeSeenBy(PackageBinding invocationPackage) {
/*  219 */     if (isPublic()) return true;
/*  220 */     if (isPrivate()) return false;
/*      */ 
/*  223 */     return invocationPackage == this.declaringClass.getPackage();
/*      */   }
/*      */ 
/*      */   public final boolean canBeSeenBy(TypeBinding receiverType, InvocationSite invocationSite, Scope scope)
/*      */   {
/*  233 */     if (isPublic()) return true;
/*      */ 
/*  235 */     SourceTypeBinding invocationType = scope.enclosingSourceType();
/*  236 */     if ((invocationType == this.declaringClass) && (invocationType == receiverType)) return true;
/*      */ 
/*  238 */     if (invocationType == null) {
/*  239 */       return (!isPrivate()) && (scope.getCurrentPackage() == this.declaringClass.fPackage);
/*      */     }
/*  241 */     if (isProtected())
/*      */     {
/*  247 */       if (invocationType == this.declaringClass) return true;
/*  248 */       if (invocationType.fPackage == this.declaringClass.fPackage) return true;
/*      */ 
/*  250 */       ReferenceBinding currentType = invocationType;
/*  251 */       TypeBinding receiverErasure = receiverType.erasure();
/*  252 */       ReferenceBinding declaringErasure = (ReferenceBinding)this.declaringClass.erasure();
/*  253 */       int depth = 0;
/*      */       do {
/*  255 */         if (currentType.findSuperTypeOriginatingFrom(declaringErasure) != null) {
/*  256 */           if (invocationSite.isSuperAccess()) {
/*  257 */             return true;
/*      */           }
/*  259 */           if ((receiverType instanceof ArrayBinding))
/*  260 */             return false;
/*  261 */           if (isStatic()) {
/*  262 */             if (depth > 0) invocationSite.setDepth(depth);
/*  263 */             return true;
/*      */           }
/*  265 */           if ((currentType == receiverErasure) || (receiverErasure.findSuperTypeOriginatingFrom(currentType) != null)) {
/*  266 */             if (depth > 0) invocationSite.setDepth(depth);
/*  267 */             return true;
/*      */           }
/*      */         }
/*  270 */         depth++;
/*  271 */         currentType = currentType.enclosingType();
/*  272 */       }while (currentType != null);
/*  273 */       return false;
/*      */     }
/*      */ 
/*  276 */     if (isPrivate())
/*      */     {
/*  280 */       if (receiverType != this.declaringClass)
/*      */       {
/*  282 */         if ((!receiverType.isTypeVariable()) || (!((TypeVariableBinding)receiverType).isErasureBoundTo(this.declaringClass.erasure())))
/*      */         {
/*  284 */           return false;
/*      */         }
/*      */       }
/*      */ 
/*  288 */       if (invocationType != this.declaringClass) {
/*  289 */         ReferenceBinding outerInvocationType = invocationType;
/*  290 */         ReferenceBinding temp = outerInvocationType.enclosingType();
/*  291 */         while (temp != null) {
/*  292 */           outerInvocationType = temp;
/*  293 */           temp = temp.enclosingType();
/*      */         }
/*      */ 
/*  296 */         ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.declaringClass.erasure();
/*  297 */         temp = outerDeclaringClass.enclosingType();
/*  298 */         while (temp != null) {
/*  299 */           outerDeclaringClass = temp;
/*  300 */           temp = temp.enclosingType();
/*      */         }
/*  302 */         if (outerInvocationType != outerDeclaringClass) return false;
/*      */       }
/*  304 */       return true;
/*      */     }
/*      */ 
/*  308 */     PackageBinding declaringPackage = this.declaringClass.fPackage;
/*  309 */     if (invocationType.fPackage != declaringPackage) return false;
/*      */ 
/*  312 */     if ((receiverType instanceof ArrayBinding))
/*  313 */       return false;
/*  314 */     TypeBinding originalDeclaringClass = this.declaringClass.original();
/*  315 */     ReferenceBinding currentType = (ReferenceBinding)receiverType;
/*      */     do {
/*  317 */       if (originalDeclaringClass == currentType.original()) return true;
/*  318 */       PackageBinding currentPackage = currentType.fPackage;
/*      */ 
/*  320 */       if ((currentPackage != null) && (currentPackage != declaringPackage)) return false; 
/*      */     }
/*  321 */     while ((currentType = currentType.superclass()) != null);
/*  322 */     return false;
/*      */   }
/*      */ 
/*      */   public List collectMissingTypes(List missingTypes) {
/*  326 */     if ((this.tagBits & 0x80) != 0L) {
/*  327 */       missingTypes = this.returnType.collectMissingTypes(missingTypes);
/*  328 */       int i = 0; for (int max = this.parameters.length; i < max; i++) {
/*  329 */         missingTypes = this.parameters[i].collectMissingTypes(missingTypes);
/*      */       }
/*  331 */       int i = 0; for (int max = this.thrownExceptions.length; i < max; i++) {
/*  332 */         missingTypes = this.thrownExceptions[i].collectMissingTypes(missingTypes);
/*      */       }
/*  334 */       int i = 0; for (int max = this.typeVariables.length; i < max; i++) {
/*  335 */         TypeVariableBinding variable = this.typeVariables[i];
/*  336 */         missingTypes = variable.superclass().collectMissingTypes(missingTypes);
/*  337 */         ReferenceBinding[] interfaces = variable.superInterfaces();
/*  338 */         int j = 0; for (int length = interfaces.length; j < length; j++) {
/*  339 */           missingTypes = interfaces[j].collectMissingTypes(missingTypes);
/*      */         }
/*      */       }
/*      */     }
/*  343 */     return missingTypes;
/*      */   }
/*      */ 
/*      */   MethodBinding computeSubstitutedMethod(MethodBinding method, LookupEnvironment env) {
/*  347 */     int length = this.typeVariables.length;
/*  348 */     TypeVariableBinding[] vars = method.typeVariables;
/*  349 */     if (length != vars.length) {
/*  350 */       return null;
/*      */     }
/*      */ 
/*  355 */     ParameterizedGenericMethodBinding substitute = 
/*  356 */       env.createParameterizedGenericMethod(method, this.typeVariables);
/*  357 */     for (int i = 0; i < length; i++)
/*  358 */       if (!this.typeVariables[i].isInterchangeableWith(vars[i], substitute))
/*  359 */         return null;
/*  360 */     return substitute;
/*      */   }
/*      */ 
/*      */   public char[] computeUniqueKey(boolean isLeaf)
/*      */   {
/*  369 */     char[] declaringKey = this.declaringClass.computeUniqueKey(false);
/*  370 */     int declaringLength = declaringKey.length;
/*      */ 
/*  373 */     int selectorLength = this.selector == TypeConstants.INIT ? 0 : this.selector.length;
/*      */ 
/*  376 */     char[] sig = genericSignature();
/*  377 */     boolean isGeneric = sig != null;
/*  378 */     if (!isGeneric) sig = signature();
/*  379 */     int signatureLength = sig.length;
/*      */ 
/*  382 */     int thrownExceptionsLength = this.thrownExceptions.length;
/*  383 */     int thrownExceptionsSignatureLength = 0;
/*  384 */     char[][] thrownExceptionsSignatures = (char[][])null;
/*  385 */     boolean addThrownExceptions = (thrownExceptionsLength > 0) && ((!isGeneric) || (CharOperation.lastIndexOf('^', sig) < 0));
/*  386 */     if (addThrownExceptions) {
/*  387 */       thrownExceptionsSignatures = new char[thrownExceptionsLength][];
/*  388 */       for (int i = 0; i < thrownExceptionsLength; i++) {
/*  389 */         if (this.thrownExceptions[i] != null) {
/*  390 */           thrownExceptionsSignatures[i] = this.thrownExceptions[i].signature();
/*  391 */           thrownExceptionsSignatureLength += thrownExceptionsSignatures[i].length + 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  396 */     char[] uniqueKey = new char[declaringLength + 1 + selectorLength + signatureLength + thrownExceptionsSignatureLength];
/*  397 */     int index = 0;
/*  398 */     System.arraycopy(declaringKey, 0, uniqueKey, index, declaringLength);
/*  399 */     index = declaringLength;
/*  400 */     uniqueKey[(index++)] = '.';
/*  401 */     System.arraycopy(this.selector, 0, uniqueKey, index, selectorLength);
/*  402 */     index += selectorLength;
/*  403 */     System.arraycopy(sig, 0, uniqueKey, index, signatureLength);
/*  404 */     if (thrownExceptionsSignatureLength > 0) {
/*  405 */       index += signatureLength;
/*  406 */       for (int i = 0; i < thrownExceptionsLength; i++) {
/*  407 */         char[] thrownExceptionSignature = thrownExceptionsSignatures[i];
/*  408 */         if (thrownExceptionSignature != null) {
/*  409 */           uniqueKey[(index++)] = '|';
/*  410 */           int length = thrownExceptionSignature.length;
/*  411 */           System.arraycopy(thrownExceptionSignature, 0, uniqueKey, index, length);
/*  412 */           index += length;
/*      */         }
/*      */       }
/*      */     }
/*  416 */     return uniqueKey;
/*      */   }
/*      */ 
/*      */   public final char[] constantPoolName()
/*      */   {
/*  426 */     return this.selector;
/*      */   }
/*      */ 
/*      */   public MethodBinding findOriginalInheritedMethod(MethodBinding inheritedMethod) {
/*  430 */     MethodBinding inheritedOriginal = inheritedMethod.original();
/*  431 */     TypeBinding superType = this.declaringClass.findSuperTypeOriginatingFrom(inheritedOriginal.declaringClass);
/*  432 */     if ((superType == null) || (!(superType instanceof ReferenceBinding))) return null;
/*      */ 
/*  434 */     if (inheritedOriginal.declaringClass != superType)
/*      */     {
/*  436 */       MethodBinding[] superMethods = ((ReferenceBinding)superType).getMethods(inheritedOriginal.selector, inheritedOriginal.parameters.length);
/*  437 */       int m = 0; for (int l = superMethods.length; m < l; m++)
/*  438 */         if (superMethods[m].original() == inheritedOriginal)
/*  439 */           return superMethods[m];
/*      */     }
/*  441 */     return inheritedOriginal;
/*      */   }
/*      */ 
/*      */   public char[] genericSignature()
/*      */   {
/*  453 */     if ((this.modifiers & 0x40000000) == 0) return null;
/*  454 */     StringBuffer sig = new StringBuffer(10);
/*  455 */     if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
/*  456 */       sig.append('<');
/*  457 */       int i = 0; for (int length = this.typeVariables.length; i < length; i++) {
/*  458 */         sig.append(this.typeVariables[i].genericSignature());
/*      */       }
/*  460 */       sig.append('>');
/*      */     }
/*  462 */     sig.append('(');
/*  463 */     int i = 0; for (int length = this.parameters.length; i < length; i++) {
/*  464 */       sig.append(this.parameters[i].genericTypeSignature());
/*      */     }
/*  466 */     sig.append(')');
/*  467 */     if (this.returnType != null) {
/*  468 */       sig.append(this.returnType.genericTypeSignature());
/*      */     }
/*      */ 
/*  471 */     boolean needExceptionSignatures = false;
/*  472 */     int length = this.thrownExceptions.length;
/*  473 */     for (int i = 0; i < length; i++) {
/*  474 */       if ((this.thrownExceptions[i].modifiers & 0x40000000) != 0) {
/*  475 */         needExceptionSignatures = true;
/*  476 */         break;
/*      */       }
/*      */     }
/*  479 */     if (needExceptionSignatures) {
/*  480 */       for (int i = 0; i < length; i++) {
/*  481 */         sig.append('^');
/*  482 */         sig.append(this.thrownExceptions[i].genericTypeSignature());
/*      */       }
/*      */     }
/*  485 */     int sigLength = sig.length();
/*  486 */     char[] genericSignature = new char[sigLength];
/*  487 */     sig.getChars(0, sigLength, genericSignature, 0);
/*  488 */     return genericSignature;
/*      */   }
/*      */ 
/*      */   public final int getAccessFlags() {
/*  492 */     return this.modifiers & 0xFFFF;
/*      */   }
/*      */ 
/*      */   public AnnotationBinding[] getAnnotations() {
/*  496 */     MethodBinding originalMethod = original();
/*  497 */     return originalMethod.declaringClass.retrieveAnnotations(originalMethod);
/*      */   }
/*      */ 
/*      */   public long getAnnotationTagBits()
/*      */   {
/*  506 */     MethodBinding originalMethod = original();
/*  507 */     if (((originalMethod.tagBits & 0x0) == 0L) && ((originalMethod.declaringClass instanceof SourceTypeBinding))) {
/*  508 */       ClassScope scope = ((SourceTypeBinding)originalMethod.declaringClass).scope;
/*  509 */       if (scope != null) {
/*  510 */         TypeDeclaration typeDecl = scope.referenceContext;
/*  511 */         AbstractMethodDeclaration methodDecl = typeDecl.declarationOf(originalMethod);
/*  512 */         if (methodDecl != null)
/*  513 */           ASTNode.resolveAnnotations(methodDecl.scope, methodDecl.annotations, originalMethod);
/*      */       }
/*      */     }
/*  516 */     return originalMethod.tagBits;
/*      */   }
/*      */ 
/*      */   public Object getDefaultValue()
/*      */   {
/*  523 */     MethodBinding originalMethod = original();
/*  524 */     if ((originalMethod.tagBits & 0x0) == 0L)
/*      */     {
/*  527 */       if ((originalMethod.declaringClass instanceof SourceTypeBinding)) {
/*  528 */         SourceTypeBinding sourceType = (SourceTypeBinding)originalMethod.declaringClass;
/*  529 */         if (sourceType.scope != null) {
/*  530 */           AbstractMethodDeclaration methodDeclaration = originalMethod.sourceMethod();
/*  531 */           if ((methodDeclaration != null) && (methodDeclaration.isAnnotationMethod())) {
/*  532 */             methodDeclaration.resolve(sourceType.scope);
/*      */           }
/*      */         }
/*      */       }
/*  536 */       originalMethod.tagBits |= 2251799813685248L;
/*      */     }
/*  538 */     AnnotationHolder holder = originalMethod.declaringClass.retrieveAnnotationHolder(originalMethod, true);
/*  539 */     return holder == null ? null : holder.getDefaultValue();
/*      */   }
/*      */ 
/*      */   public MethodBinding getHighestOverridenMethod(LookupEnvironment environment)
/*      */   {
/*  546 */     MethodBinding bestMethod = this;
/*  547 */     ReferenceBinding currentType = this.declaringClass;
/*  548 */     if (isConstructor())
/*      */     {
/*      */       do {
/*  551 */         MethodBinding superMethod = currentType.getExactConstructor(this.parameters);
/*  552 */         if (superMethod != null)
/*  553 */           bestMethod = superMethod;
/*      */       }
/*  555 */       while ((currentType = currentType.superclass()) != null);
/*  556 */       return bestMethod;
/*      */     }
/*  558 */     MethodVerifier verifier = environment.methodVerifier();
/*      */ 
/*  560 */     ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/*  561 */     int nextPosition = 0;
/*      */     do {
/*  563 */       MethodBinding[] superMethods = currentType.getMethods(this.selector);
/*  564 */       int i = 0; for (int length = superMethods.length; i < length; i++) {
/*  565 */         if (verifier.doesMethodOverride(this, superMethods[i])) {
/*  566 */           bestMethod = superMethods[i];
/*  567 */           break;
/*      */         }
/*      */       }
/*  570 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  571 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES))
/*  572 */         if (interfacesToVisit == null) {
/*  573 */           interfacesToVisit = itsInterfaces;
/*  574 */           nextPosition = interfacesToVisit.length;
/*      */         } else {
/*  576 */           int itsLength = itsInterfaces.length;
/*  577 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/*  578 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  579 */           for (int a = 0; a < itsLength; a++) {
/*  580 */             ReferenceBinding next = itsInterfaces[a];
/*  581 */             int b = 0;
/*  582 */             while (next != interfacesToVisit[b])
/*      */             {
/*  581 */               b++; if (b < nextPosition)
/*      */                 continue;
/*  583 */               interfacesToVisit[(nextPosition++)] = next;
/*      */             }
/*      */           }
/*      */         }
/*      */     }
/*  587 */     while ((currentType = currentType.superclass()) != null);
/*  588 */     if (bestMethod.declaringClass.id == 1) {
/*  589 */       return bestMethod;
/*      */     }
/*      */ 
/*  592 */     for (int i = 0; i < nextPosition; i++) {
/*  593 */       currentType = interfacesToVisit[i];
/*  594 */       MethodBinding[] superMethods = currentType.getMethods(this.selector);
/*  595 */       int j = 0; for (int length = superMethods.length; j < length; j++) {
/*  596 */         MethodBinding superMethod = superMethods[j];
/*  597 */         if (verifier.doesMethodOverride(this, superMethod)) {
/*  598 */           TypeBinding bestReturnType = bestMethod.returnType;
/*  599 */           if ((bestReturnType != superMethod.returnType) && 
/*  600 */             (bestMethod.returnType.findSuperTypeOriginatingFrom(superMethod.returnType) == null)) break;
/*  601 */           bestMethod = superMethod;
/*      */ 
/*  603 */           break;
/*      */         }
/*      */       }
/*  606 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  607 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/*  608 */         int itsLength = itsInterfaces.length;
/*  609 */         if (nextPosition + itsLength >= interfacesToVisit.length)
/*  610 */           System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  611 */         for (int a = 0; a < itsLength; a++) {
/*  612 */           ReferenceBinding next = itsInterfaces[a];
/*  613 */           int b = 0;
/*  614 */           while (next != interfacesToVisit[b])
/*      */           {
/*  613 */             b++; if (b < nextPosition)
/*      */               continue;
/*  615 */             interfacesToVisit[(nextPosition++)] = next;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  619 */     return bestMethod;
/*      */   }
/*      */ 
/*      */   public AnnotationBinding[][] getParameterAnnotations()
/*      */   {
/*      */     int length;
/*  628 */     if ((length = this.parameters.length) == 0) {
/*  629 */       return null;
/*      */     }
/*  631 */     MethodBinding originalMethod = original();
/*  632 */     AnnotationHolder holder = originalMethod.declaringClass.retrieveAnnotationHolder(originalMethod, true);
/*  633 */     AnnotationBinding[][] allParameterAnnotations = holder == null ? null : holder.getParameterAnnotations();
/*  634 */     if ((allParameterAnnotations == null) && ((this.tagBits & 0x400) != 0L)) {
/*  635 */       allParameterAnnotations = new AnnotationBinding[length][];
/*      */ 
/*  637 */       if ((this.declaringClass instanceof SourceTypeBinding)) {
/*  638 */         SourceTypeBinding sourceType = (SourceTypeBinding)this.declaringClass;
/*  639 */         if (sourceType.scope != null) {
/*  640 */           AbstractMethodDeclaration methodDecl = sourceType.scope.referenceType().declarationOf(this);
/*  641 */           for (int i = 0; i < length; i++) {
/*  642 */             Argument argument = methodDecl.arguments[i];
/*  643 */             if (argument.annotations != null) {
/*  644 */               ASTNode.resolveAnnotations(methodDecl.scope, argument.annotations, argument.binding);
/*  645 */               allParameterAnnotations[i] = argument.binding.getAnnotations();
/*      */             } else {
/*  647 */               allParameterAnnotations[i] = Binding.NO_ANNOTATIONS;
/*      */             }
/*      */           }
/*      */         } else {
/*  651 */           for (int i = 0; i < length; i++)
/*  652 */             allParameterAnnotations[i] = Binding.NO_ANNOTATIONS;
/*      */         }
/*      */       }
/*      */       else {
/*  656 */         for (int i = 0; i < length; i++) {
/*  657 */           allParameterAnnotations[i] = Binding.NO_ANNOTATIONS;
/*      */         }
/*      */       }
/*  660 */       setParameterAnnotations(allParameterAnnotations);
/*      */     }
/*  662 */     return allParameterAnnotations;
/*      */   }
/*      */ 
/*      */   public TypeVariableBinding getTypeVariable(char[] variableName) {
/*  666 */     int i = this.typeVariables.length;
/*      */     do { if (CharOperation.equals(this.typeVariables[i].sourceName, variableName))
/*  668 */         return this.typeVariables[i];
/*  666 */       i--; } while (i >= 0);
/*      */ 
/*  669 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean hasSubstitutedParameters()
/*      */   {
/*  677 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean hasSubstitutedReturnType()
/*      */   {
/*  683 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isAbstract()
/*      */   {
/*  689 */     return (this.modifiers & 0x400) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isBridge()
/*      */   {
/*  695 */     return (this.modifiers & 0x40) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isConstructor()
/*      */   {
/*  701 */     return this.selector == TypeConstants.INIT;
/*      */   }
/*      */ 
/*      */   public final boolean isDefault()
/*      */   {
/*  707 */     return (!isPublic()) && (!isProtected()) && (!isPrivate());
/*      */   }
/*      */ 
/*      */   public final boolean isDefaultAbstract()
/*      */   {
/*  713 */     return (this.modifiers & 0x80000) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isDeprecated()
/*      */   {
/*  719 */     return (this.modifiers & 0x100000) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isFinal()
/*      */   {
/*  725 */     return (this.modifiers & 0x10) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isImplementing()
/*      */   {
/*  733 */     return (this.modifiers & 0x20000000) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isMain()
/*      */   {
/*  740 */     if ((this.selector.length == 4) && (CharOperation.equals(this.selector, TypeConstants.MAIN)) && 
/*  741 */       ((this.modifiers & 0x9) != 0) && 
/*  742 */       (TypeBinding.VOID == this.returnType) && 
/*  743 */       (this.parameters.length == 1)) {
/*  744 */       TypeBinding paramType = this.parameters[0];
/*  745 */       if ((paramType.dimensions() == 1) && (paramType.leafComponentType().id == 11)) {
/*  746 */         return true;
/*      */       }
/*      */     }
/*  749 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isNative()
/*      */   {
/*  755 */     return (this.modifiers & 0x100) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isOverriding()
/*      */   {
/*  762 */     return (this.modifiers & 0x10000000) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isPrivate()
/*      */   {
/*  767 */     return (this.modifiers & 0x2) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isOrEnclosedByPrivateType()
/*      */   {
/*  773 */     if ((this.modifiers & 0x2) != 0)
/*  774 */       return true;
/*  775 */     return (this.declaringClass != null) && (this.declaringClass.isOrEnclosedByPrivateType());
/*      */   }
/*      */ 
/*      */   public final boolean isProtected()
/*      */   {
/*  781 */     return (this.modifiers & 0x4) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isPublic()
/*      */   {
/*  787 */     return (this.modifiers & 0x1) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isStatic()
/*      */   {
/*  793 */     return (this.modifiers & 0x8) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isStrictfp()
/*      */   {
/*  799 */     return (this.modifiers & 0x800) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isSynchronized()
/*      */   {
/*  805 */     return (this.modifiers & 0x20) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isSynthetic()
/*      */   {
/*  811 */     return (this.modifiers & 0x1000) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isUsed()
/*      */   {
/*  817 */     return (this.modifiers & 0x8000000) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isVarargs()
/*      */   {
/*  823 */     return (this.modifiers & 0x80) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isViewedAsDeprecated()
/*      */   {
/*  829 */     return (this.modifiers & 0x300000) != 0;
/*      */   }
/*      */ 
/*      */   public final int kind() {
/*  833 */     return 8;
/*      */   }
/*      */ 
/*      */   public MethodBinding original()
/*      */   {
/*  842 */     return this;
/*      */   }
/*      */ 
/*      */   public char[] readableName() {
/*  846 */     StringBuffer buffer = new StringBuffer(this.parameters.length + 20);
/*  847 */     if (isConstructor())
/*  848 */       buffer.append(this.declaringClass.sourceName());
/*      */     else
/*  850 */       buffer.append(this.selector);
/*  851 */     buffer.append('(');
/*  852 */     if (this.parameters != Binding.NO_PARAMETERS) {
/*  853 */       int i = 0; for (int length = this.parameters.length; i < length; i++) {
/*  854 */         if (i > 0)
/*  855 */           buffer.append(", ");
/*  856 */         buffer.append(this.parameters[i].sourceName());
/*      */       }
/*      */     }
/*  859 */     buffer.append(')');
/*  860 */     return buffer.toString().toCharArray();
/*      */   }
/*      */   public void setAnnotations(AnnotationBinding[] annotations) {
/*  863 */     this.declaringClass.storeAnnotations(this, annotations);
/*      */   }
/*      */   public void setAnnotations(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations, Object defaultValue, LookupEnvironment optionalEnv) {
/*  866 */     this.declaringClass.storeAnnotationHolder(this, AnnotationHolder.storeAnnotations(annotations, parameterAnnotations, defaultValue, optionalEnv));
/*      */   }
/*      */   public void setDefaultValue(Object defaultValue) {
/*  869 */     MethodBinding originalMethod = original();
/*  870 */     originalMethod.tagBits |= 2251799813685248L;
/*      */ 
/*  872 */     AnnotationHolder holder = this.declaringClass.retrieveAnnotationHolder(this, false);
/*  873 */     if (holder == null)
/*  874 */       setAnnotations(null, null, defaultValue, null);
/*      */     else
/*  876 */       setAnnotations(holder.getAnnotations(), holder.getParameterAnnotations(), defaultValue, null); 
/*      */   }
/*      */ 
/*      */   public void setParameterAnnotations(AnnotationBinding[][] parameterAnnotations) {
/*  879 */     AnnotationHolder holder = this.declaringClass.retrieveAnnotationHolder(this, false);
/*  880 */     if (holder == null)
/*  881 */       setAnnotations(null, parameterAnnotations, null, null);
/*      */     else
/*  883 */       setAnnotations(holder.getAnnotations(), parameterAnnotations, holder.getDefaultValue(), null); 
/*      */   }
/*      */ 
/*      */   protected final void setSelector(char[] selector) {
/*  886 */     this.selector = selector;
/*  887 */     this.signature = null;
/*      */   }
/*      */ 
/*      */   public char[] shortReadableName()
/*      */   {
/*  894 */     StringBuffer buffer = new StringBuffer(this.parameters.length + 20);
/*  895 */     if (isConstructor())
/*  896 */       buffer.append(this.declaringClass.shortReadableName());
/*      */     else
/*  898 */       buffer.append(this.selector);
/*  899 */     buffer.append('(');
/*  900 */     if (this.parameters != Binding.NO_PARAMETERS) {
/*  901 */       int i = 0; for (int length = this.parameters.length; i < length; i++) {
/*  902 */         if (i > 0)
/*  903 */           buffer.append(", ");
/*  904 */         buffer.append(this.parameters[i].shortReadableName());
/*      */       }
/*      */     }
/*  907 */     buffer.append(')');
/*  908 */     int nameLength = buffer.length();
/*  909 */     char[] shortReadableName = new char[nameLength];
/*  910 */     buffer.getChars(0, nameLength, shortReadableName, 0);
/*  911 */     return shortReadableName;
/*      */   }
/*      */ 
/*      */   public final char[] signature()
/*      */   {
/*  921 */     if (this.signature != null) {
/*  922 */       return this.signature;
/*      */     }
/*  924 */     StringBuffer buffer = new StringBuffer(this.parameters.length + 20);
/*  925 */     buffer.append('(');
/*      */ 
/*  927 */     TypeBinding[] targetParameters = this.parameters;
/*  928 */     boolean isConstructor = isConstructor();
/*  929 */     if ((isConstructor) && (this.declaringClass.isEnum())) {
/*  930 */       buffer.append(ConstantPool.JavaLangStringSignature);
/*  931 */       buffer.append(TypeBinding.INT.signature());
/*      */     }
/*  933 */     boolean needSynthetics = (isConstructor) && (this.declaringClass.isNestedType());
/*  934 */     if (needSynthetics)
/*      */     {
/*  936 */       ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
/*  937 */       if (syntheticArgumentTypes != null) {
/*  938 */         int i = 0; for (int count = syntheticArgumentTypes.length; i < count; i++) {
/*  939 */           buffer.append(syntheticArgumentTypes[i].signature());
/*      */         }
/*      */       }
/*      */ 
/*  943 */       if ((this instanceof SyntheticMethodBinding)) {
/*  944 */         targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
/*      */       }
/*      */     }
/*      */ 
/*  948 */     if (targetParameters != Binding.NO_PARAMETERS) {
/*  949 */       for (int i = 0; i < targetParameters.length; i++) {
/*  950 */         buffer.append(targetParameters[i].signature());
/*      */       }
/*      */     }
/*  953 */     if (needSynthetics) {
/*  954 */       SyntheticArgumentBinding[] syntheticOuterArguments = this.declaringClass.syntheticOuterLocalVariables();
/*  955 */       int count = syntheticOuterArguments == null ? 0 : syntheticOuterArguments.length;
/*  956 */       for (int i = 0; i < count; i++) {
/*  957 */         buffer.append(syntheticOuterArguments[i].type.signature());
/*      */       }
/*      */ 
/*  960 */       int i = targetParameters.length; for (int extraLength = this.parameters.length; i < extraLength; i++) {
/*  961 */         buffer.append(this.parameters[i].signature());
/*      */       }
/*      */     }
/*  964 */     buffer.append(')');
/*  965 */     if (this.returnType != null)
/*  966 */       buffer.append(this.returnType.signature());
/*  967 */     int nameLength = buffer.length();
/*  968 */     this.signature = new char[nameLength];
/*  969 */     buffer.getChars(0, nameLength, this.signature, 0);
/*      */ 
/*  971 */     return this.signature;
/*      */   }
/*      */ 
/*      */   public final char[] signature(ClassFile classFile)
/*      */   {
/*  980 */     if (this.signature != null) {
/*  981 */       if ((this.tagBits & 0x800) != 0L)
/*      */       {
/*  983 */         boolean isConstructor = isConstructor();
/*  984 */         TypeBinding[] targetParameters = this.parameters;
/*  985 */         boolean needSynthetics = (isConstructor) && (this.declaringClass.isNestedType());
/*  986 */         if (needSynthetics)
/*      */         {
/*  988 */           ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
/*  989 */           if (syntheticArgumentTypes != null) {
/*  990 */             int i = 0; for (int count = syntheticArgumentTypes.length; i < count; i++) {
/*  991 */               ReferenceBinding syntheticArgumentType = syntheticArgumentTypes[i];
/*  992 */               if ((syntheticArgumentType.tagBits & 0x800) != 0L) {
/*  993 */                 Util.recordNestedType(classFile, syntheticArgumentType);
/*      */               }
/*      */             }
/*      */           }
/*  997 */           if ((this instanceof SyntheticMethodBinding)) {
/*  998 */             targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
/*      */           }
/*      */         }
/*      */ 
/* 1002 */         if (targetParameters != Binding.NO_PARAMETERS) {
/* 1003 */           int i = 0; for (int max = targetParameters.length; i < max; i++) {
/* 1004 */             TypeBinding targetParameter = targetParameters[i];
/* 1005 */             TypeBinding leafTargetParameterType = targetParameter.leafComponentType();
/* 1006 */             if ((leafTargetParameterType.tagBits & 0x800) != 0L) {
/* 1007 */               Util.recordNestedType(classFile, leafTargetParameterType);
/*      */             }
/*      */           }
/*      */         }
/* 1011 */         if (needSynthetics)
/*      */         {
/* 1013 */           int i = targetParameters.length; for (int extraLength = this.parameters.length; i < extraLength; i++) {
/* 1014 */             TypeBinding parameter = this.parameters[i];
/* 1015 */             TypeBinding leafParameterType = parameter.leafComponentType();
/* 1016 */             if ((leafParameterType.tagBits & 0x800) != 0L) {
/* 1017 */               Util.recordNestedType(classFile, leafParameterType);
/*      */             }
/*      */           }
/*      */         }
/* 1021 */         if (this.returnType != null) {
/* 1022 */           TypeBinding ret = this.returnType.leafComponentType();
/* 1023 */           if ((ret.tagBits & 0x800) != 0L) {
/* 1024 */             Util.recordNestedType(classFile, ret);
/*      */           }
/*      */         }
/*      */       }
/* 1028 */       return this.signature;
/*      */     }
/*      */ 
/* 1031 */     StringBuffer buffer = new StringBuffer(this.parameters.length + 20);
/* 1032 */     buffer.append('(');
/*      */ 
/* 1034 */     TypeBinding[] targetParameters = this.parameters;
/* 1035 */     boolean isConstructor = isConstructor();
/* 1036 */     if ((isConstructor) && (this.declaringClass.isEnum())) {
/* 1037 */       buffer.append(ConstantPool.JavaLangStringSignature);
/* 1038 */       buffer.append(TypeBinding.INT.signature());
/*      */     }
/* 1040 */     boolean needSynthetics = (isConstructor) && (this.declaringClass.isNestedType());
/* 1041 */     if (needSynthetics)
/*      */     {
/* 1043 */       ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
/* 1044 */       if (syntheticArgumentTypes != null) {
/* 1045 */         int i = 0; for (int count = syntheticArgumentTypes.length; i < count; i++) {
/* 1046 */           ReferenceBinding syntheticArgumentType = syntheticArgumentTypes[i];
/* 1047 */           if ((syntheticArgumentType.tagBits & 0x800) != 0L) {
/* 1048 */             this.tagBits |= 2048L;
/* 1049 */             Util.recordNestedType(classFile, syntheticArgumentType);
/*      */           }
/* 1051 */           buffer.append(syntheticArgumentType.signature());
/*      */         }
/*      */       }
/*      */ 
/* 1055 */       if ((this instanceof SyntheticMethodBinding)) {
/* 1056 */         targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
/*      */       }
/*      */     }
/*      */ 
/* 1060 */     if (targetParameters != Binding.NO_PARAMETERS) {
/* 1061 */       int i = 0; for (int max = targetParameters.length; i < max; i++) {
/* 1062 */         TypeBinding targetParameter = targetParameters[i];
/* 1063 */         TypeBinding leafTargetParameterType = targetParameter.leafComponentType();
/* 1064 */         if ((leafTargetParameterType.tagBits & 0x800) != 0L) {
/* 1065 */           this.tagBits |= 2048L;
/* 1066 */           Util.recordNestedType(classFile, leafTargetParameterType);
/*      */         }
/* 1068 */         buffer.append(targetParameter.signature());
/*      */       }
/*      */     }
/* 1071 */     if (needSynthetics) {
/* 1072 */       SyntheticArgumentBinding[] syntheticOuterArguments = this.declaringClass.syntheticOuterLocalVariables();
/* 1073 */       int count = syntheticOuterArguments == null ? 0 : syntheticOuterArguments.length;
/* 1074 */       for (int i = 0; i < count; i++) {
/* 1075 */         buffer.append(syntheticOuterArguments[i].type.signature());
/*      */       }
/*      */ 
/* 1078 */       int i = targetParameters.length; for (int extraLength = this.parameters.length; i < extraLength; i++) {
/* 1079 */         TypeBinding parameter = this.parameters[i];
/* 1080 */         TypeBinding leafParameterType = parameter.leafComponentType();
/* 1081 */         if ((leafParameterType.tagBits & 0x800) != 0L) {
/* 1082 */           this.tagBits |= 2048L;
/* 1083 */           Util.recordNestedType(classFile, leafParameterType);
/*      */         }
/* 1085 */         buffer.append(parameter.signature());
/*      */       }
/*      */     }
/* 1088 */     buffer.append(')');
/* 1089 */     if (this.returnType != null) {
/* 1090 */       TypeBinding ret = this.returnType.leafComponentType();
/* 1091 */       if ((ret.tagBits & 0x800) != 0L) {
/* 1092 */         this.tagBits |= 2048L;
/* 1093 */         Util.recordNestedType(classFile, ret);
/*      */       }
/* 1095 */       buffer.append(this.returnType.signature());
/*      */     }
/* 1097 */     int nameLength = buffer.length();
/* 1098 */     this.signature = new char[nameLength];
/* 1099 */     buffer.getChars(0, nameLength, this.signature, 0);
/*      */ 
/* 1101 */     return this.signature;
/*      */   }
/*      */   public final int sourceEnd() {
/* 1104 */     AbstractMethodDeclaration method = sourceMethod();
/* 1105 */     if (method == null) {
/* 1106 */       if ((this.declaringClass instanceof SourceTypeBinding))
/* 1107 */         return ((SourceTypeBinding)this.declaringClass).sourceEnd();
/* 1108 */       return 0;
/*      */     }
/* 1110 */     return method.sourceEnd;
/*      */   }
/*      */ 
/*      */   public AbstractMethodDeclaration sourceMethod() {
/*      */     try {
/* 1115 */       sourceType = (SourceTypeBinding)this.declaringClass;
/*      */     }
/*      */     catch (ClassCastException localClassCastException)
/*      */     {
/*      */       SourceTypeBinding sourceType;
/* 1117 */       return null;
/*      */     }
/*      */     SourceTypeBinding sourceType;
/* 1120 */     AbstractMethodDeclaration[] methods = sourceType.scope.referenceContext.methods;
/* 1121 */     int i = methods.length;
/*      */     do { if (this == methods[i].binding)
/* 1123 */         return methods[i];
/* 1121 */       i--; } while (i >= 0);
/*      */ 
/* 1124 */     return null;
/*      */   }
/*      */   public final int sourceStart() {
/* 1127 */     AbstractMethodDeclaration method = sourceMethod();
/* 1128 */     if (method == null) {
/* 1129 */       if ((this.declaringClass instanceof SourceTypeBinding))
/* 1130 */         return ((SourceTypeBinding)this.declaringClass).sourceStart();
/* 1131 */       return 0;
/*      */     }
/* 1133 */     return method.sourceStart;
/*      */   }
/*      */ 
/*      */   public MethodBinding tiebreakMethod()
/*      */   {
/* 1141 */     return this;
/*      */   }
/*      */   public String toString() {
/* 1144 */     StringBuffer output = new StringBuffer(10);
/* 1145 */     if ((this.modifiers & 0x2000000) != 0) {
/* 1146 */       output.append("[unresolved] ");
/*      */     }
/* 1148 */     ASTNode.printModifiers(this.modifiers, output);
/* 1149 */     output.append(this.returnType != null ? this.returnType.debugName() : "<no type>");
/* 1150 */     output.append(" ");
/* 1151 */     output.append(this.selector != null ? new String(this.selector) : "<no selector>");
/* 1152 */     output.append("(");
/* 1153 */     if (this.parameters != null) {
/* 1154 */       if (this.parameters != Binding.NO_PARAMETERS) {
/* 1155 */         int i = 0; for (int length = this.parameters.length; i < length; i++) {
/* 1156 */           if (i > 0)
/* 1157 */             output.append(", ");
/* 1158 */           output.append(this.parameters[i] != null ? this.parameters[i].debugName() : "<no argument type>");
/*      */         }
/*      */       }
/*      */     }
/* 1162 */     else output.append("<no argument types>");
/*      */ 
/* 1164 */     output.append(") ");
/*      */ 
/* 1166 */     if (this.thrownExceptions != null) {
/* 1167 */       if (this.thrownExceptions != Binding.NO_EXCEPTIONS) {
/* 1168 */         output.append("throws ");
/* 1169 */         int i = 0; for (int length = this.thrownExceptions.length; i < length; i++) {
/* 1170 */           if (i > 0)
/* 1171 */             output.append(", ");
/* 1172 */           output.append(this.thrownExceptions[i] != null ? this.thrownExceptions[i].debugName() : "<no exception type>");
/*      */         }
/*      */       }
/*      */     }
/* 1176 */     else output.append("<no exception types>");
/*      */ 
/* 1178 */     return output.toString();
/*      */   }
/*      */   public TypeVariableBinding[] typeVariables() {
/* 1181 */     return this.typeVariables;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 * JD-Core Version:    0.6.0
 */