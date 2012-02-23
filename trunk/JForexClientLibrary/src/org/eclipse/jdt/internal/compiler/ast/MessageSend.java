/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class MessageSend extends Expression
/*     */   implements InvocationSite
/*     */ {
/*     */   public Expression receiver;
/*     */   public char[] selector;
/*     */   public Expression[] arguments;
/*     */   public MethodBinding binding;
/*     */   public MethodBinding syntheticAccessor;
/*     */   public TypeBinding expectedType;
/*     */   public long nameSourcePosition;
/*     */   public TypeBinding actualReceiverType;
/*     */   public TypeBinding valueCast;
/*     */   public TypeReference[] typeArguments;
/*     */   public TypeBinding[] genericTypeArguments;
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  58 */     boolean nonStatic = !this.binding.isStatic();
/*  59 */     flowInfo = this.receiver.analyseCode(currentScope, flowContext, flowInfo, nonStatic).unconditionalInits();
/*  60 */     if (nonStatic) {
/*  61 */       this.receiver.checkNPE(currentScope, flowContext, flowInfo);
/*     */     }
/*     */ 
/*  64 */     if (this.arguments != null) {
/*  65 */       int length = this.arguments.length;
/*  66 */       for (int i = 0; i < length; i++)
/*  67 */         flowInfo = this.arguments[i].analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
/*     */     }
/*     */     ReferenceBinding[] thrownExceptions;
/*  71 */     if ((thrownExceptions = this.binding.thrownExceptions) != Binding.NO_EXCEPTIONS) {
/*  72 */       if (((this.bits & 0x10000) != 0) && (this.genericTypeArguments == null))
/*     */       {
/*  74 */         thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
/*     */       }
/*     */ 
/*  77 */       flowContext.checkExceptionHandlers(thrownExceptions, this, flowInfo.copy(), currentScope);
/*     */     }
/*     */ 
/*  82 */     manageSyntheticAccessIfNecessary(currentScope, flowInfo);
/*  83 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void computeConversion(Scope scope, TypeBinding runtimeTimeType, TypeBinding compileTimeType)
/*     */   {
/*  89 */     if ((runtimeTimeType == null) || (compileTimeType == null)) {
/*  90 */       return;
/*     */     }
/*  92 */     if ((this.binding != null) && (this.binding.isValidBinding())) {
/*  93 */       MethodBinding originalBinding = this.binding.original();
/*  94 */       TypeBinding originalType = originalBinding.returnType;
/*     */ 
/*  96 */       if (originalType.leafComponentType().isTypeVariable()) {
/*  97 */         TypeBinding targetType = (!compileTimeType.isBaseType()) && (runtimeTimeType.isBaseType()) ? 
/*  98 */           compileTimeType : 
/*  99 */           runtimeTimeType;
/* 100 */         this.valueCast = originalType.genericCast(targetType);
/* 101 */       } else if ((this.binding == scope.environment().arrayClone) && 
/* 102 */         (runtimeTimeType.id != 1) && 
/* 103 */         (scope.compilerOptions().sourceLevel >= 3211264L))
/*     */       {
/* 105 */         this.valueCast = runtimeTimeType;
/*     */       }
/* 107 */       if ((this.valueCast instanceof ReferenceBinding)) {
/* 108 */         ReferenceBinding referenceCast = (ReferenceBinding)this.valueCast;
/* 109 */         if (!referenceCast.canBeSeenBy(scope)) {
/* 110 */           scope.problemReporter().invalidType(this, 
/* 111 */             new ProblemReferenceBinding(
/* 112 */             CharOperation.splitOn('.', referenceCast.shortReadableName()), 
/* 113 */             referenceCast, 
/* 114 */             2));
/*     */         }
/*     */       }
/*     */     }
/* 118 */     super.computeConversion(scope, runtimeTimeType, compileTimeType);
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 129 */     int pc = codeStream.position;
/*     */ 
/* 131 */     MethodBinding codegenBinding = this.binding.original();
/* 132 */     boolean isStatic = codegenBinding.isStatic();
/* 133 */     if (isStatic) {
/* 134 */       this.receiver.generateCode(currentScope, codeStream, false);
/* 135 */     } else if (((this.bits & 0x1FE0) != 0) && (this.receiver.isImplicitThis()))
/*     */     {
/* 137 */       ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
/* 138 */       Object[] path = currentScope.getEmulationPath(targetType, true, false);
/* 139 */       codeStream.generateOuterAccess(path, this, targetType, currentScope);
/*     */     } else {
/* 141 */       this.receiver.generateCode(currentScope, codeStream, true);
/* 142 */       if ((this.bits & 0x40000) != 0) {
/* 143 */         codeStream.checkcast(this.actualReceiverType);
/*     */       }
/*     */     }
/* 146 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */ 
/* 148 */     generateArguments(this.binding, this.arguments, currentScope, codeStream);
/* 149 */     pc = codeStream.position;
/*     */ 
/* 151 */     if (this.syntheticAccessor == null) {
/* 152 */       TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
/* 153 */       if (isStatic)
/* 154 */         codeStream.invoke(-72, codegenBinding, constantPoolDeclaringClass);
/* 155 */       else if ((this.receiver.isSuper()) || (codegenBinding.isPrivate()))
/* 156 */         codeStream.invoke(-73, codegenBinding, constantPoolDeclaringClass);
/* 157 */       else if (constantPoolDeclaringClass.isInterface())
/* 158 */         codeStream.invoke(-71, codegenBinding, constantPoolDeclaringClass);
/*     */       else
/* 160 */         codeStream.invoke(-74, codegenBinding, constantPoolDeclaringClass);
/*     */     }
/*     */     else {
/* 163 */       codeStream.invoke(-72, this.syntheticAccessor, null);
/*     */     }
/*     */ 
/* 166 */     if (this.valueCast != null) codeStream.checkcast(this.valueCast);
/* 167 */     if (valueRequired)
/*     */     {
/* 169 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */     } else {
/* 171 */       boolean isUnboxing = (this.implicitConversion & 0x400) != 0;
/*     */ 
/* 173 */       if (isUnboxing) codeStream.generateImplicitConversion(this.implicitConversion);
/* 174 */       switch (isUnboxing ? postConversionType(currentScope).id : codegenBinding.returnType.id) {
/*     */       case 7:
/*     */       case 8:
/* 177 */         codeStream.pop2();
/* 178 */         break;
/*     */       case 6:
/* 180 */         break;
/*     */       default:
/* 182 */         codeStream.pop();
/*     */       }
/*     */     }
/* 185 */     codeStream.recordPositionsFrom(pc, (int)(this.nameSourcePosition >>> 32));
/*     */   }
/*     */ 
/*     */   public TypeBinding[] genericTypeArguments()
/*     */   {
/* 191 */     return this.genericTypeArguments;
/*     */   }
/*     */ 
/*     */   public boolean isSuperAccess() {
/* 195 */     return this.receiver.isSuper();
/*     */   }
/*     */   public boolean isTypeAccess() {
/* 198 */     return (this.receiver != null) && (this.receiver.isTypeReference());
/*     */   }
/*     */ 
/*     */   public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
/* 202 */     if ((flowInfo.tagBits & 0x1) != 0) return;
/*     */ 
/* 205 */     MethodBinding codegenBinding = this.binding.original();
/* 206 */     if (this.binding.isPrivate())
/*     */     {
/* 209 */       if (currentScope.enclosingSourceType() != codegenBinding.declaringClass) {
/* 210 */         this.syntheticAccessor = ((SourceTypeBinding)codegenBinding.declaringClass).addSyntheticMethod(codegenBinding, false);
/* 211 */         currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
/* 212 */         return;
/*     */       }
/*     */     } else {
/* 215 */       if ((this.receiver instanceof QualifiedSuperReference))
/*     */       {
/* 218 */         SourceTypeBinding destinationType = (SourceTypeBinding)((QualifiedSuperReference)this.receiver).currentCompatibleType;
/* 219 */         this.syntheticAccessor = destinationType.addSyntheticMethod(codegenBinding, isSuperAccess());
/* 220 */         currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
/* 221 */         return;
/*     */       }
/* 223 */       if (this.binding.isProtected())
/*     */       {
/*     */         SourceTypeBinding enclosingSourceType;
/* 226 */         if (((this.bits & 0x1FE0) != 0) && 
/* 227 */           (codegenBinding.declaringClass.getPackage() != 
/* 228 */           (enclosingSourceType = currentScope.enclosingSourceType()).getPackage()))
/*     */         {
/* 230 */           SourceTypeBinding currentCompatibleType = (SourceTypeBinding)enclosingSourceType.enclosingTypeAt((this.bits & 0x1FE0) >> 5);
/* 231 */           this.syntheticAccessor = currentCompatibleType.addSyntheticMethod(codegenBinding, isSuperAccess());
/* 232 */           currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
/* 233 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int nullStatus(FlowInfo flowInfo) {
/* 238 */     return 0;
/*     */   }
/*     */ 
/*     */   public TypeBinding postConversionType(Scope scope)
/*     */   {
/* 245 */     TypeBinding convertedType = this.resolvedType;
/* 246 */     if (this.valueCast != null)
/* 247 */       convertedType = this.valueCast;
/* 248 */     int runtimeType = (this.implicitConversion & 0xFF) >> 4;
/* 249 */     switch (runtimeType) {
/*     */     case 5:
/* 251 */       convertedType = TypeBinding.BOOLEAN;
/* 252 */       break;
/*     */     case 3:
/* 254 */       convertedType = TypeBinding.BYTE;
/* 255 */       break;
/*     */     case 4:
/* 257 */       convertedType = TypeBinding.SHORT;
/* 258 */       break;
/*     */     case 2:
/* 260 */       convertedType = TypeBinding.CHAR;
/* 261 */       break;
/*     */     case 10:
/* 263 */       convertedType = TypeBinding.INT;
/* 264 */       break;
/*     */     case 9:
/* 266 */       convertedType = TypeBinding.FLOAT;
/* 267 */       break;
/*     */     case 7:
/* 269 */       convertedType = TypeBinding.LONG;
/* 270 */       break;
/*     */     case 8:
/* 272 */       convertedType = TypeBinding.DOUBLE;
/*     */     case 6:
/*     */     }
/*     */ 
/* 276 */     if ((this.implicitConversion & 0x200) != 0) {
/* 277 */       convertedType = scope.environment().computeBoxingType(convertedType);
/*     */     }
/* 279 */     return convertedType;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/* 284 */     if (!this.receiver.isImplicitThis()) this.receiver.printExpression(0, output).append('.');
/* 285 */     if (this.typeArguments != null) {
/* 286 */       output.append('<');
/* 287 */       int max = this.typeArguments.length - 1;
/* 288 */       for (int j = 0; j < max; j++) {
/* 289 */         this.typeArguments[j].print(0, output);
/* 290 */         output.append(", ");
/*     */       }
/* 292 */       this.typeArguments[max].print(0, output);
/* 293 */       output.append('>');
/*     */     }
/* 295 */     output.append(this.selector).append('(');
/* 296 */     if (this.arguments != null) {
/* 297 */       for (int i = 0; i < this.arguments.length; i++) {
/* 298 */         if (i > 0) output.append(", ");
/* 299 */         this.arguments[i].printExpression(0, output);
/*     */       }
/*     */     }
/* 302 */     return output.append(')');
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 309 */     this.constant = Constant.NotAConstant;
/* 310 */     boolean receiverCast = false; boolean argsContainCast = false;
/* 311 */     if ((this.receiver instanceof CastExpression)) {
/* 312 */       this.receiver.bits |= 32;
/* 313 */       receiverCast = true;
/*     */     }
/* 315 */     this.actualReceiverType = this.receiver.resolveType(scope);
/* 316 */     boolean receiverIsType = ((this.receiver instanceof NameReference)) && ((((NameReference)this.receiver).bits & 0x4) != 0);
/* 317 */     if ((receiverCast) && (this.actualReceiverType != null))
/*     */     {
/* 319 */       if (((CastExpression)this.receiver).expression.resolvedType == this.actualReceiverType) {
/* 320 */         scope.problemReporter().unnecessaryCast((CastExpression)this.receiver);
/*     */       }
/*     */     }
/*     */ 
/* 324 */     if (this.typeArguments != null) {
/* 325 */       int length = this.typeArguments.length;
/* 326 */       boolean argHasError = scope.compilerOptions().sourceLevel < 3211264L;
/* 327 */       this.genericTypeArguments = new TypeBinding[length];
/* 328 */       for (int i = 0; i < length; i++) {
/* 329 */         TypeReference typeReference = this.typeArguments[i];
/* 330 */         if ((this.genericTypeArguments[i] =  = typeReference.resolveType(scope, true)) == null) {
/* 331 */           argHasError = true;
/*     */         }
/* 333 */         if ((argHasError) && ((typeReference instanceof Wildcard))) {
/* 334 */           scope.problemReporter().illegalUsageOfWildcard(typeReference);
/*     */         }
/*     */       }
/* 337 */       if (argHasError) {
/* 338 */         if (this.arguments != null) {
/* 339 */           int i = 0; for (int max = this.arguments.length; i < max; i++) {
/* 340 */             this.arguments[i].resolveType(scope);
/*     */           }
/*     */         }
/* 343 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 347 */     TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
/* 348 */     if (this.arguments != null) {
/* 349 */       boolean argHasError = false;
/* 350 */       int length = this.arguments.length;
/* 351 */       argumentTypes = new TypeBinding[length];
/* 352 */       for (int i = 0; i < length; i++) {
/* 353 */         Expression argument = this.arguments[i];
/* 354 */         if ((argument instanceof CastExpression)) {
/* 355 */           argument.bits |= 32;
/* 356 */           argsContainCast = true;
/*     */         }
/* 358 */         if ((argumentTypes[i] =  = argument.resolveType(scope)) == null) {
/* 359 */           argHasError = true;
/*     */         }
/*     */       }
/* 362 */       if (argHasError) {
/* 363 */         if ((this.actualReceiverType instanceof ReferenceBinding))
/*     */         {
/* 365 */           TypeBinding[] pseudoArgs = new TypeBinding[length];
/* 366 */           int i = length;
/*     */           do { pseudoArgs[i] = (argumentTypes[i] == null ? TypeBinding.NULL : argumentTypes[i]);
/*     */ 
/* 366 */             i--; } while (i >= 0);
/*     */ 
/* 368 */           this.binding = 
/* 369 */             (this.receiver.isImplicitThis() ? 
/* 370 */             scope.getImplicitMethod(this.selector, pseudoArgs, this) : 
/* 371 */             scope.findMethod((ReferenceBinding)this.actualReceiverType, this.selector, pseudoArgs, this));
/* 372 */           if ((this.binding != null) && (!this.binding.isValidBinding())) {
/* 373 */             MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
/*     */ 
/* 375 */             if (closestMatch != null) {
/* 376 */               if (closestMatch.original().typeVariables != Binding.NO_TYPE_VARIABLES)
/*     */               {
/* 378 */                 closestMatch = scope.environment().createParameterizedGenericMethod(closestMatch.original(), null);
/*     */               }
/* 380 */               this.binding = closestMatch;
/* 381 */               MethodBinding closestMatchOriginal = closestMatch.original();
/* 382 */               if ((closestMatchOriginal.isOrEnclosedByPrivateType()) && (!scope.isDefinedInMethod(closestMatchOriginal)))
/*     */               {
/* 384 */                 closestMatchOriginal.modifiers |= 134217728;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 389 */         return null;
/*     */       }
/*     */     }
/* 392 */     if (this.actualReceiverType == null) {
/* 393 */       return null;
/*     */     }
/*     */ 
/* 396 */     if (this.actualReceiverType.isBaseType()) {
/* 397 */       scope.problemReporter().errorNoMethodFor(this, this.actualReceiverType, argumentTypes);
/* 398 */       return null;
/*     */     }
/* 400 */     this.binding = (this.receiver.isImplicitThis() ? 
/* 401 */       scope.getImplicitMethod(this.selector, argumentTypes, this) : 
/* 402 */       scope.getMethod(this.actualReceiverType, this.selector, argumentTypes, this));
/* 403 */     if (!this.binding.isValidBinding()) {
/* 404 */       if (this.binding.declaringClass == null) {
/* 405 */         if ((this.actualReceiverType instanceof ReferenceBinding)) {
/* 406 */           this.binding.declaringClass = ((ReferenceBinding)this.actualReceiverType);
/*     */         } else {
/* 408 */           scope.problemReporter().errorNoMethodFor(this, this.actualReceiverType, argumentTypes);
/* 409 */           return null;
/*     */         }
/*     */       }
/* 412 */       scope.problemReporter().invalidMethod(this, this.binding);
/* 413 */       MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
/* 414 */       switch (this.binding.problemId()) {
/*     */       case 3:
/* 416 */         break;
/*     */       case 2:
/*     */       case 6:
/*     */       case 7:
/*     */       case 8:
/*     */       case 10:
/* 423 */         if (closestMatch == null) break; this.resolvedType = closestMatch.returnType;
/*     */       case 4:
/*     */       case 5:
/*     */       case 9:
/* 427 */       }if (closestMatch != null) {
/* 428 */         this.binding = closestMatch;
/* 429 */         MethodBinding closestMatchOriginal = closestMatch.original();
/* 430 */         if ((closestMatchOriginal.isOrEnclosedByPrivateType()) && (!scope.isDefinedInMethod(closestMatchOriginal)))
/*     */         {
/* 432 */           closestMatchOriginal.modifiers |= 134217728;
/*     */         }
/*     */       }
/* 435 */       return (this.resolvedType != null) && ((this.resolvedType.tagBits & 0x80) == 0L) ? 
/* 436 */         this.resolvedType : 
/* 437 */         null;
/*     */     }
/* 439 */     if ((this.binding.tagBits & 0x80) != 0L) {
/* 440 */       scope.problemReporter().missingTypeInMethod(this, this.binding);
/*     */     }
/* 442 */     CompilerOptions compilerOptions = scope.compilerOptions();
/* 443 */     if (!this.binding.isStatic())
/*     */     {
/* 445 */       if (receiverIsType) {
/* 446 */         scope.problemReporter().mustUseAStaticMethod(this, this.binding);
/* 447 */         if ((this.actualReceiverType.isRawType()) && 
/* 448 */           ((this.receiver.bits & 0x40000000) == 0) && 
/* 449 */           (compilerOptions.getSeverity(536936448) != -1)) {
/* 450 */           scope.problemReporter().rawTypeReference(this.receiver, this.actualReceiverType);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 455 */         TypeBinding oldReceiverType = this.actualReceiverType;
/* 456 */         this.actualReceiverType = this.actualReceiverType.getErasureCompatibleType(this.binding.declaringClass);
/* 457 */         this.receiver.computeConversion(scope, this.actualReceiverType, this.actualReceiverType);
/* 458 */         if ((this.actualReceiverType != oldReceiverType) && (this.receiver.postConversionType(scope) != this.actualReceiverType))
/* 459 */           this.bits |= 262144;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 464 */       if ((!this.receiver.isImplicitThis()) && (!this.receiver.isSuper()) && (!receiverIsType)) {
/* 465 */         scope.problemReporter().nonStaticAccessToStaticMethod(this, this.binding);
/*     */       }
/* 467 */       if ((!this.receiver.isImplicitThis()) && (this.binding.declaringClass != this.actualReceiverType)) {
/* 468 */         scope.problemReporter().indirectAccessToStaticMethod(this, this.binding);
/*     */       }
/*     */     }
/* 471 */     if (checkInvocationArguments(scope, this.receiver, this.actualReceiverType, this.binding, this.arguments, argumentTypes, argsContainCast, this)) {
/* 472 */       this.bits |= 65536;
/*     */     }
/*     */ 
/* 476 */     if ((this.binding.isAbstract()) && 
/* 477 */       (this.receiver.isSuper())) {
/* 478 */       scope.problemReporter().cannotDireclyInvokeAbstractMethod(this, this.binding);
/*     */     }
/*     */ 
/* 482 */     if (isMethodUseDeprecated(this.binding, scope, true)) {
/* 483 */       scope.problemReporter().deprecatedMethod(this.binding, this);
/*     */     }
/*     */ 
/* 486 */     if ((this.binding == scope.environment().arrayClone) && (compilerOptions.sourceLevel >= 3211264L)) {
/* 487 */       this.resolvedType = this.actualReceiverType;
/*     */     }
/*     */     else
/*     */     {
/*     */       TypeBinding returnType;
/* 490 */       if (((this.bits & 0x10000) != 0) && (this.genericTypeArguments == null))
/*     */       {
/* 492 */         TypeBinding returnType = this.binding.returnType;
/* 493 */         if (returnType != null)
/* 494 */           returnType = scope.environment().convertToRawType(returnType.erasure(), true);
/*     */       }
/*     */       else {
/* 497 */         returnType = this.binding.returnType;
/* 498 */         if (returnType != null) {
/* 499 */           returnType = returnType.capture(scope, this.sourceEnd);
/*     */         }
/*     */       }
/* 502 */       this.resolvedType = returnType;
/*     */     }
/* 504 */     if ((this.receiver.isSuper()) && (compilerOptions.getSeverity(537919488) != -1)) {
/* 505 */       ReferenceContext referenceContext = scope.methodScope().referenceContext;
/* 506 */       if ((referenceContext instanceof AbstractMethodDeclaration)) {
/* 507 */         AbstractMethodDeclaration abstractMethodDeclaration = (AbstractMethodDeclaration)referenceContext;
/* 508 */         MethodBinding enclosingMethodBinding = abstractMethodDeclaration.binding;
/* 509 */         if ((enclosingMethodBinding.isOverriding()) && 
/* 510 */           (CharOperation.equals(this.binding.selector, enclosingMethodBinding.selector)) && 
/* 511 */           (this.binding.areParametersEqual(enclosingMethodBinding))) {
/* 512 */           abstractMethodDeclaration.bits |= 16;
/*     */         }
/*     */       }
/*     */     }
/* 516 */     if ((this.typeArguments != null) && (this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES)) {
/* 517 */       scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
/*     */     }
/* 519 */     return (this.resolvedType.tagBits & 0x80) == 0L ? 
/* 520 */       this.resolvedType : 
/* 521 */       null;
/*     */   }
/*     */ 
/*     */   public void setActualReceiverType(ReferenceBinding receiverType) {
/* 525 */     if (receiverType == null) return;
/* 526 */     this.actualReceiverType = receiverType;
/*     */   }
/*     */   public void setDepth(int depth) {
/* 529 */     this.bits &= -8161;
/* 530 */     if (depth > 0)
/* 531 */       this.bits |= (depth & 0xFF) << 5;
/*     */   }
/*     */ 
/*     */   public void setExpectedType(TypeBinding expectedType)
/*     */   {
/* 539 */     this.expectedType = expectedType;
/*     */   }
/*     */ 
/*     */   public void setFieldIndex(int depth) {
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope) {
/* 546 */     if (visitor.visit(this, blockScope)) {
/* 547 */       this.receiver.traverse(visitor, blockScope);
/* 548 */       if (this.typeArguments != null) {
/* 549 */         int i = 0; for (int typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; i++) {
/* 550 */           this.typeArguments[i].traverse(visitor, blockScope);
/*     */         }
/*     */       }
/* 553 */       if (this.arguments != null) {
/* 554 */         int argumentsLength = this.arguments.length;
/* 555 */         for (int i = 0; i < argumentsLength; i++)
/* 556 */           this.arguments[i].traverse(visitor, blockScope);
/*     */       }
/*     */     }
/* 559 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.MessageSend
 * JD-Core Version:    0.6.0
 */