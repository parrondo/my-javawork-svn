/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class QualifiedAllocationExpression extends AllocationExpression
/*     */ {
/*     */   public Expression enclosingInstance;
/*     */   public TypeDeclaration anonymousType;
/*     */ 
/*     */   public QualifiedAllocationExpression()
/*     */   {
/*     */   }
/*     */ 
/*     */   public QualifiedAllocationExpression(TypeDeclaration anonymousType)
/*     */   {
/*  52 */     this.anonymousType = anonymousType;
/*  53 */     anonymousType.allocation = this;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  58 */     if (this.enclosingInstance != null) {
/*  59 */       flowInfo = this.enclosingInstance.analyseCode(currentScope, flowContext, flowInfo);
/*     */     }
/*     */ 
/*  63 */     checkCapturedLocalInitializationIfNecessary(
/*  64 */       (ReferenceBinding)(this.anonymousType == null ? 
/*  65 */       this.binding.declaringClass.erasure() : 
/*  66 */       this.binding.declaringClass.superclass().erasure()), 
/*  67 */       currentScope, 
/*  68 */       flowInfo);
/*     */ 
/*  71 */     if (this.arguments != null) {
/*  72 */       int i = 0; for (int count = this.arguments.length; i < count; i++) {
/*  73 */         flowInfo = this.arguments[i].analyseCode(currentScope, flowContext, flowInfo);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  78 */     if (this.anonymousType != null)
/*  79 */       flowInfo = this.anonymousType.analyseCode(currentScope, flowContext, flowInfo);
/*     */     ReferenceBinding[] thrownExceptions;
/*  84 */     if ((thrownExceptions = this.binding.thrownExceptions).length != 0) {
/*  85 */       if (((this.bits & 0x10000) != 0) && (this.genericTypeArguments == null))
/*     */       {
/*  87 */         thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
/*     */       }
/*     */ 
/*  90 */       flowContext.checkExceptionHandlers(
/*  91 */         thrownExceptions, 
/*  92 */         this, 
/*  93 */         flowInfo.unconditionalCopy(), 
/*  94 */         currentScope);
/*     */     }
/*  96 */     manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
/*  97 */     manageSyntheticAccessIfNecessary(currentScope, flowInfo);
/*  98 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public Expression enclosingInstance()
/*     */   {
/* 103 */     return this.enclosingInstance;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
/* 107 */     int pc = codeStream.position;
/* 108 */     MethodBinding codegenBinding = this.binding.original();
/* 109 */     ReferenceBinding allocatedType = codegenBinding.declaringClass;
/* 110 */     codeStream.new_(allocatedType);
/* 111 */     boolean isUnboxing = (this.implicitConversion & 0x400) != 0;
/* 112 */     if ((valueRequired) || (isUnboxing)) {
/* 113 */       codeStream.dup();
/*     */     }
/*     */ 
/* 116 */     if (this.type != null) {
/* 117 */       codeStream.recordPositionsFrom(pc, this.type.sourceStart);
/*     */     }
/*     */     else {
/* 120 */       codeStream.ldc(String.valueOf(this.enumConstant.name));
/* 121 */       codeStream.generateInlinedValue(this.enumConstant.binding.id);
/*     */     }
/*     */ 
/* 124 */     if (allocatedType.isNestedType()) {
/* 125 */       codeStream.generateSyntheticEnclosingInstanceValues(
/* 126 */         currentScope, 
/* 127 */         allocatedType, 
/* 128 */         enclosingInstance(), 
/* 129 */         this);
/*     */     }
/*     */ 
/* 132 */     generateArguments(this.binding, this.arguments, currentScope, codeStream);
/*     */ 
/* 134 */     if (allocatedType.isNestedType()) {
/* 135 */       codeStream.generateSyntheticOuterArgumentValues(
/* 136 */         currentScope, 
/* 137 */         allocatedType, 
/* 138 */         this);
/*     */     }
/*     */ 
/* 142 */     if (this.syntheticAccessor == null) {
/* 143 */       codeStream.invoke(-73, codegenBinding, null);
/*     */     }
/*     */     else {
/* 146 */       int i = 0;
/* 147 */       int max = this.syntheticAccessor.parameters.length - codegenBinding.parameters.length;
/* 148 */       while (i < max)
/*     */       {
/* 150 */         codeStream.aconst_null();
/*     */ 
/* 149 */         i++;
/*     */       }
/*     */ 
/* 152 */       codeStream.invoke(-73, this.syntheticAccessor, null);
/*     */     }
/* 154 */     if (valueRequired) {
/* 155 */       codeStream.generateImplicitConversion(this.implicitConversion);
/* 156 */     } else if (isUnboxing)
/*     */     {
/* 158 */       codeStream.generateImplicitConversion(this.implicitConversion);
/* 159 */       switch (postConversionType(currentScope).id) {
/*     */       case 7:
/*     */       case 8:
/* 162 */         codeStream.pop2();
/* 163 */         break;
/*     */       default:
/* 165 */         codeStream.pop();
/*     */       }
/*     */     }
/* 168 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */ 
/* 170 */     if (this.anonymousType != null)
/* 171 */       this.anonymousType.generateCode(currentScope, codeStream);
/*     */   }
/*     */ 
/*     */   public boolean isSuperAccess()
/*     */   {
/* 178 */     return this.anonymousType != null;
/*     */   }
/*     */ 
/*     */   public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo)
/*     */   {
/* 189 */     if ((flowInfo.tagBits & 0x1) == 0) {
/* 190 */       ReferenceBinding allocatedTypeErasure = (ReferenceBinding)this.binding.declaringClass.erasure();
/*     */ 
/* 193 */       if ((allocatedTypeErasure.isNestedType()) && 
/* 194 */         (currentScope.enclosingSourceType().isLocalType()))
/*     */       {
/* 196 */         if (allocatedTypeErasure.isLocalType()) {
/* 197 */           ((LocalTypeBinding)allocatedTypeErasure).addInnerEmulationDependent(currentScope, this.enclosingInstance != null);
/*     */         }
/*     */         else
/* 200 */           currentScope.propagateInnerEmulation(allocatedTypeErasure, this.enclosingInstance != null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/* 207 */     if (this.enclosingInstance != null)
/* 208 */       this.enclosingInstance.printExpression(0, output).append('.');
/* 209 */     super.printExpression(0, output);
/* 210 */     if (this.anonymousType != null) {
/* 211 */       this.anonymousType.print(indent, output);
/*     */     }
/* 213 */     return output;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 218 */     if ((this.anonymousType == null) && (this.enclosingInstance == null)) {
/* 219 */       return super.resolveType(scope);
/*     */     }
/*     */ 
/* 226 */     this.constant = Constant.NotAConstant;
/* 227 */     TypeBinding enclosingInstanceType = null;
/* 228 */     TypeBinding receiverType = null;
/* 229 */     boolean hasError = false;
/* 230 */     boolean enclosingInstanceContainsCast = false;
/* 231 */     boolean argsContainCast = false;
/*     */ 
/* 233 */     if (this.enclosingInstance != null) {
/* 234 */       if ((this.enclosingInstance instanceof CastExpression)) {
/* 235 */         this.enclosingInstance.bits |= 32;
/* 236 */         enclosingInstanceContainsCast = true;
/*     */       }
/* 238 */       if ((enclosingInstanceType = this.enclosingInstance.resolveType(scope)) == null) {
/* 239 */         hasError = true;
/* 240 */       } else if ((enclosingInstanceType.isBaseType()) || (enclosingInstanceType.isArrayType())) {
/* 241 */         scope.problemReporter().illegalPrimitiveOrArrayTypeForEnclosingInstance(
/* 242 */           enclosingInstanceType, 
/* 243 */           this.enclosingInstance);
/* 244 */         hasError = true;
/* 245 */       } else if ((this.type instanceof QualifiedTypeReference)) {
/* 246 */         scope.problemReporter().illegalUsageOfQualifiedTypeReference((QualifiedTypeReference)this.type);
/* 247 */         hasError = true;
/*     */       } else {
/* 249 */         receiverType = ((SingleTypeReference)this.type).resolveTypeEnclosing(scope, (ReferenceBinding)enclosingInstanceType);
/* 250 */         if ((receiverType != null) && (enclosingInstanceContainsCast)) {
/* 251 */           CastExpression.checkNeedForEnclosingInstanceCast(scope, this.enclosingInstance, enclosingInstanceType, receiverType);
/*     */         }
/*     */       }
/*     */     }
/* 255 */     else if (this.type == null)
/*     */     {
/* 257 */       receiverType = scope.enclosingSourceType();
/*     */     } else {
/* 259 */       receiverType = this.type.resolveType(scope, true);
/*     */ 
/* 261 */       if ((receiverType != null) && (receiverType.isValidBinding()) && 
/* 262 */         ((this.type instanceof ParameterizedQualifiedTypeReference))) {
/* 263 */         ReferenceBinding currentType = (ReferenceBinding)receiverType;
/*     */ 
/* 266 */         while (((currentType.modifiers & 0x8) == 0) && 
/* 267 */           (!currentType.isRawType())) {
/* 268 */           if ((currentType = currentType.enclosingType()) == null) {
/* 269 */             ParameterizedQualifiedTypeReference qRef = (ParameterizedQualifiedTypeReference)this.type;
/* 270 */             for (int i = qRef.typeArguments.length - 2; i >= 0; i--) {
/* 271 */               if (qRef.typeArguments[i] != null) {
/* 272 */                 scope.problemReporter().illegalQualifiedParameterizedTypeAllocation(this.type, receiverType);
/* 273 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 280 */     if ((receiverType == null) || (!receiverType.isValidBinding())) {
/* 281 */       hasError = true;
/*     */     }
/*     */ 
/* 285 */     if (this.typeArguments != null) {
/* 286 */       int length = this.typeArguments.length;
/* 287 */       boolean argHasError = scope.compilerOptions().sourceLevel < 3211264L;
/* 288 */       this.genericTypeArguments = new TypeBinding[length];
/* 289 */       for (int i = 0; i < length; i++) {
/* 290 */         TypeReference typeReference = this.typeArguments[i];
/* 291 */         if ((this.genericTypeArguments[i] =  = typeReference.resolveType(scope, true)) == null) {
/* 292 */           argHasError = true;
/*     */         }
/* 294 */         if ((argHasError) && ((typeReference instanceof Wildcard))) {
/* 295 */           scope.problemReporter().illegalUsageOfWildcard(typeReference);
/*     */         }
/*     */       }
/* 298 */       if (argHasError) {
/* 299 */         if (this.arguments != null) {
/* 300 */           int i = 0; for (int max = this.arguments.length; i < max; i++) {
/* 301 */             this.arguments[i].resolveType(scope);
/*     */           }
/*     */         }
/* 304 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 309 */     TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
/* 310 */     if (this.arguments != null) {
/* 311 */       int length = this.arguments.length;
/* 312 */       argumentTypes = new TypeBinding[length];
/* 313 */       for (int i = 0; i < length; i++) {
/* 314 */         Expression argument = this.arguments[i];
/* 315 */         if ((argument instanceof CastExpression)) {
/* 316 */           argument.bits |= 32;
/* 317 */           argsContainCast = true;
/*     */         }
/* 319 */         if ((argumentTypes[i] =  = argument.resolveType(scope)) == null) {
/* 320 */           hasError = true;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 326 */     if (hasError) {
/* 327 */       if ((receiverType instanceof ReferenceBinding)) {
/* 328 */         ReferenceBinding referenceReceiver = (ReferenceBinding)receiverType;
/* 329 */         if (receiverType.isValidBinding())
/*     */         {
/* 331 */           int length = this.arguments == null ? 0 : this.arguments.length;
/* 332 */           TypeBinding[] pseudoArgs = new TypeBinding[length];
/* 333 */           int i = length;
/*     */           do { pseudoArgs[i] = (argumentTypes[i] == null ? TypeBinding.NULL : argumentTypes[i]);
/*     */ 
/* 333 */             i--; } while (i >= 0);
/*     */ 
/* 336 */           this.binding = scope.findMethod(referenceReceiver, TypeConstants.INIT, pseudoArgs, this);
/* 337 */           if ((this.binding != null) && (!this.binding.isValidBinding())) {
/* 338 */             MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
/*     */ 
/* 340 */             if (closestMatch != null) {
/* 341 */               if (closestMatch.original().typeVariables != Binding.NO_TYPE_VARIABLES)
/*     */               {
/* 343 */                 closestMatch = scope.environment().createParameterizedGenericMethod(closestMatch.original(), null);
/*     */               }
/* 345 */               this.binding = closestMatch;
/* 346 */               MethodBinding closestMatchOriginal = closestMatch.original();
/* 347 */               if ((closestMatchOriginal.isOrEnclosedByPrivateType()) && (!scope.isDefinedInMethod(closestMatchOriginal)))
/*     */               {
/* 349 */                 closestMatchOriginal.modifiers |= 134217728;
/*     */               }
/*     */             }
/*     */           }
/*     */         } else {
/* 354 */           return null;
/*     */         }
/* 356 */         if (this.anonymousType != null)
/*     */         {
/* 358 */           scope.addAnonymousType(this.anonymousType, referenceReceiver);
/* 359 */           this.anonymousType.resolve(scope);
/* 360 */           return this.resolvedType = this.anonymousType.binding;
/*     */         }
/*     */       }
/* 363 */       return this.resolvedType = receiverType;
/*     */     }
/* 365 */     if (this.anonymousType == null)
/*     */     {
/* 367 */       if (!receiverType.canBeInstantiated()) {
/* 368 */         scope.problemReporter().cannotInstantiate(this.type, receiverType);
/* 369 */         return this.resolvedType = receiverType;
/*     */       }
/* 371 */       ReferenceBinding allocationType = (ReferenceBinding)receiverType;
/* 372 */       if ((this.binding = scope.getConstructor(allocationType, argumentTypes, this)).isValidBinding()) {
/* 373 */         if (isMethodUseDeprecated(this.binding, scope, true)) {
/* 374 */           scope.problemReporter().deprecatedMethod(this.binding, this);
/*     */         }
/* 376 */         if (checkInvocationArguments(scope, null, allocationType, this.binding, this.arguments, argumentTypes, argsContainCast, this)) {
/* 377 */           this.bits |= 65536;
/*     */         }
/* 379 */         if ((this.typeArguments != null) && (this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES))
/* 380 */           scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
/*     */       }
/*     */       else {
/* 383 */         if (this.binding.declaringClass == null) {
/* 384 */           this.binding.declaringClass = allocationType;
/*     */         }
/* 386 */         if ((this.type != null) && (!this.type.resolvedType.isValidBinding()))
/*     */         {
/* 388 */           return null;
/*     */         }
/* 390 */         scope.problemReporter().invalidConstructor(this, this.binding);
/* 391 */         return this.resolvedType = receiverType;
/*     */       }
/* 393 */       if ((this.binding.tagBits & 0x80) != 0L) {
/* 394 */         scope.problemReporter().missingTypeInConstructor(this, this.binding);
/*     */       }
/*     */ 
/* 397 */       ReferenceBinding expectedType = this.binding.declaringClass.enclosingType();
/* 398 */       if (expectedType != enclosingInstanceType)
/* 399 */         scope.compilationUnitScope().recordTypeConversion(expectedType, enclosingInstanceType);
/* 400 */       if ((enclosingInstanceType.isCompatibleWith(expectedType)) || (scope.isBoxingCompatibleWith(enclosingInstanceType, expectedType))) {
/* 401 */         this.enclosingInstance.computeConversion(scope, expectedType, enclosingInstanceType);
/* 402 */         return this.resolvedType = receiverType;
/*     */       }
/* 404 */       scope.problemReporter().typeMismatchError(enclosingInstanceType, expectedType, this.enclosingInstance, null);
/* 405 */       return this.resolvedType = receiverType;
/*     */     }
/* 407 */     ReferenceBinding superType = (ReferenceBinding)receiverType;
/* 408 */     if (superType.isTypeVariable()) {
/* 409 */       superType = new ProblemReferenceBinding(new char[][] { superType.sourceName() }, superType, 9);
/* 410 */       scope.problemReporter().invalidType(this, superType);
/* 411 */       return null;
/* 412 */     }if ((this.type != null) && (superType.isEnum())) {
/* 413 */       scope.problemReporter().cannotInstantiate(this.type, superType);
/* 414 */       return this.resolvedType = superType;
/*     */     }
/*     */ 
/* 418 */     ReferenceBinding anonymousSuperclass = superType.isInterface() ? scope.getJavaLangObject() : superType;
/*     */ 
/* 420 */     scope.addAnonymousType(this.anonymousType, superType);
/* 421 */     this.anonymousType.resolve(scope);
/*     */ 
/* 424 */     this.resolvedType = this.anonymousType.binding;
/* 425 */     if ((this.resolvedType.tagBits & 0x20000) != 0L) {
/* 426 */       return null;
/*     */     }
/* 428 */     MethodBinding inheritedBinding = scope.getConstructor(anonymousSuperclass, argumentTypes, this);
/* 429 */     if (!inheritedBinding.isValidBinding()) {
/* 430 */       if (inheritedBinding.declaringClass == null) {
/* 431 */         inheritedBinding.declaringClass = anonymousSuperclass;
/*     */       }
/* 433 */       if ((this.type != null) && (!this.type.resolvedType.isValidBinding()))
/*     */       {
/* 435 */         return null;
/*     */       }
/* 437 */       scope.problemReporter().invalidConstructor(this, inheritedBinding);
/* 438 */       return this.resolvedType;
/*     */     }
/* 440 */     if ((inheritedBinding.tagBits & 0x80) != 0L) {
/* 441 */       scope.problemReporter().missingTypeInConstructor(this, inheritedBinding);
/*     */     }
/* 443 */     if (this.enclosingInstance != null) {
/* 444 */       ReferenceBinding targetEnclosing = inheritedBinding.declaringClass.enclosingType();
/* 445 */       if (targetEnclosing == null) {
/* 446 */         scope.problemReporter().unnecessaryEnclosingInstanceSpecification(this.enclosingInstance, superType);
/* 447 */         return this.resolvedType;
/* 448 */       }if ((!enclosingInstanceType.isCompatibleWith(targetEnclosing)) && (!scope.isBoxingCompatibleWith(enclosingInstanceType, targetEnclosing))) {
/* 449 */         scope.problemReporter().typeMismatchError(enclosingInstanceType, targetEnclosing, this.enclosingInstance, null);
/* 450 */         return this.resolvedType;
/*     */       }
/* 452 */       this.enclosingInstance.computeConversion(scope, targetEnclosing, enclosingInstanceType);
/*     */     }
/* 454 */     if ((this.arguments != null) && 
/* 455 */       (checkInvocationArguments(scope, null, anonymousSuperclass, inheritedBinding, this.arguments, argumentTypes, argsContainCast, this))) {
/* 456 */       this.bits |= 65536;
/*     */     }
/*     */ 
/* 459 */     if ((this.typeArguments != null) && (inheritedBinding.original().typeVariables == Binding.NO_TYPE_VARIABLES)) {
/* 460 */       scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(inheritedBinding, this.genericTypeArguments, this.typeArguments);
/*     */     }
/*     */ 
/* 463 */     this.binding = this.anonymousType.createDefaultConstructorWithBinding(inheritedBinding, ((this.bits & 0x10000) != 0) && (this.genericTypeArguments == null));
/* 464 */     return this.resolvedType;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 468 */     if (visitor.visit(this, scope)) {
/* 469 */       if (this.enclosingInstance != null)
/* 470 */         this.enclosingInstance.traverse(visitor, scope);
/* 471 */       if (this.typeArguments != null) {
/* 472 */         int i = 0; for (int typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; i++) {
/* 473 */           this.typeArguments[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 476 */       if (this.type != null)
/* 477 */         this.type.traverse(visitor, scope);
/* 478 */       if (this.arguments != null) {
/* 479 */         int argumentsLength = this.arguments.length;
/* 480 */         for (int i = 0; i < argumentsLength; i++)
/* 481 */           this.arguments[i].traverse(visitor, scope);
/*     */       }
/* 483 */       if (this.anonymousType != null)
/* 484 */         this.anonymousType.traverse(visitor, scope);
/*     */     }
/* 486 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression
 * JD-Core Version:    0.6.0
 */