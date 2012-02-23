/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ExplicitConstructorCall extends Statement
/*     */   implements InvocationSite
/*     */ {
/*     */   public Expression[] arguments;
/*     */   public Expression qualification;
/*     */   public MethodBinding binding;
/*     */   MethodBinding syntheticAccessor;
/*     */   public int accessMode;
/*     */   public TypeReference[] typeArguments;
/*     */   public TypeBinding[] genericTypeArguments;
/*     */   public static final int ImplicitSuper = 1;
/*     */   public static final int Super = 2;
/*     */   public static final int This = 3;
/*     */   public VariableBinding[][] implicitArguments;
/*     */   public int typeArgumentsSourceStart;
/*     */ 
/*     */   public ExplicitConstructorCall(int accessMode)
/*     */   {
/*  56 */     this.accessMode = accessMode;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*     */     try
/*     */     {
/*  63 */       ((MethodScope)currentScope).isConstructorCall = true;
/*     */ 
/*  66 */       if (this.qualification != null) {
/*  67 */         flowInfo = 
/*  68 */           this.qualification
/*  69 */           .analyseCode(currentScope, flowContext, flowInfo)
/*  70 */           .unconditionalInits();
/*     */       }
/*     */ 
/*  73 */       if (this.arguments != null) {
/*  74 */         int i = 0; for (int max = this.arguments.length; i < max; i++)
/*  75 */           flowInfo = 
/*  76 */             this.arguments[i]
/*  77 */             .analyseCode(currentScope, flowContext, flowInfo)
/*  78 */             .unconditionalInits();
/*     */       }
/*     */       ReferenceBinding[] thrownExceptions;
/*  83 */       if ((thrownExceptions = this.binding.thrownExceptions) != Binding.NO_EXCEPTIONS) {
/*  84 */         if (((this.bits & 0x10000) != 0) && (this.genericTypeArguments == null))
/*     */         {
/*  86 */           thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
/*     */         }
/*     */ 
/*  89 */         flowContext.checkExceptionHandlers(
/*  90 */           thrownExceptions, 
/*  91 */           this.accessMode == 1 ? 
/*  92 */           (ASTNode)currentScope.methodScope().referenceContext : 
/*  93 */           this, 
/*  94 */           flowInfo, 
/*  95 */           currentScope);
/*     */       }
/*  97 */       manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
/*  98 */       manageSyntheticAccessIfNecessary(currentScope, flowInfo);
/*  99 */       FlowInfo localFlowInfo = flowInfo;
/*     */       return localFlowInfo;
/*     */     } finally {
/* 101 */       ((MethodScope)currentScope).isConstructorCall = false;
/* 102 */     }throw localObject;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/* 112 */     if ((this.bits & 0x80000000) == 0)
/* 113 */       return;
/*     */     try
/*     */     {
/* 116 */       ((MethodScope)currentScope).isConstructorCall = true;
/*     */ 
/* 118 */       int pc = codeStream.position;
/* 119 */       codeStream.aload_0();
/*     */ 
/* 121 */       MethodBinding codegenBinding = this.binding.original();
/* 122 */       ReferenceBinding targetType = codegenBinding.declaringClass;
/*     */ 
/* 125 */       if ((targetType.erasure().id == 41) || (targetType.isEnum())) {
/* 126 */         codeStream.aload_1();
/* 127 */         codeStream.iload_2();
/*     */       }
/*     */ 
/* 131 */       if (targetType.isNestedType()) {
/* 132 */         codeStream.generateSyntheticEnclosingInstanceValues(
/* 133 */           currentScope, 
/* 134 */           targetType, 
/* 135 */           (this.bits & 0x2000) != 0 ? null : this.qualification, 
/* 136 */           this);
/*     */       }
/*     */ 
/* 139 */       generateArguments(this.binding, this.arguments, currentScope, codeStream);
/*     */ 
/* 142 */       if (targetType.isNestedType()) {
/* 143 */         codeStream.generateSyntheticOuterArgumentValues(
/* 144 */           currentScope, 
/* 145 */           targetType, 
/* 146 */           this);
/*     */       }
/* 148 */       if (this.syntheticAccessor != null)
/*     */       {
/* 150 */         int i = 0;
/* 151 */         int max = this.syntheticAccessor.parameters.length - codegenBinding.parameters.length;
/* 152 */         while (i < max)
/*     */         {
/* 154 */           codeStream.aconst_null();
/*     */ 
/* 153 */           i++;
/*     */         }
/*     */ 
/* 156 */         codeStream.invoke(-73, this.syntheticAccessor, null);
/*     */       } else {
/* 158 */         codeStream.invoke(-73, codegenBinding, null);
/*     */       }
/* 160 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */     } finally {
/* 162 */       ((MethodScope)currentScope).isConstructorCall = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public TypeBinding[] genericTypeArguments()
/*     */   {
/* 170 */     return this.genericTypeArguments;
/*     */   }
/*     */ 
/*     */   public boolean isImplicitSuper() {
/* 174 */     return this.accessMode == 1;
/*     */   }
/*     */ 
/*     */   public boolean isSuperAccess() {
/* 178 */     return this.accessMode != 3;
/*     */   }
/*     */ 
/*     */   public boolean isTypeAccess() {
/* 182 */     return true;
/*     */   }
/*     */ 
/*     */   void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo)
/*     */   {
/* 193 */     ReferenceBinding superTypeErasure = (ReferenceBinding)this.binding.declaringClass.erasure();
/*     */ 
/* 195 */     if ((flowInfo.tagBits & 0x1) == 0)
/*     */     {
/* 197 */       if ((superTypeErasure.isNestedType()) && 
/* 198 */         (currentScope.enclosingSourceType().isLocalType()))
/*     */       {
/* 200 */         if (superTypeErasure.isLocalType()) {
/* 201 */           ((LocalTypeBinding)superTypeErasure).addInnerEmulationDependent(currentScope, this.qualification != null);
/*     */         }
/*     */         else
/* 204 */           currentScope.propagateInnerEmulation(superTypeErasure, this.qualification != null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo)
/*     */   {
/* 211 */     if ((flowInfo.tagBits & 0x1) == 0)
/*     */     {
/* 213 */       MethodBinding codegenBinding = this.binding.original();
/*     */ 
/* 216 */       if ((this.binding.isPrivate()) && (this.accessMode != 3)) {
/* 217 */         ReferenceBinding declaringClass = codegenBinding.declaringClass;
/*     */ 
/* 219 */         if (((declaringClass.tagBits & 0x10) != 0L) && (currentScope.compilerOptions().complianceLevel >= 3145728L))
/*     */         {
/* 221 */           codegenBinding.tagBits |= 1024L;
/*     */         } else {
/* 223 */           this.syntheticAccessor = ((SourceTypeBinding)declaringClass).addSyntheticMethod(codegenBinding, isSuperAccess());
/* 224 */           currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output) {
/* 231 */     printIndent(indent, output);
/* 232 */     if (this.qualification != null) this.qualification.printExpression(0, output).append('.');
/* 233 */     if (this.typeArguments != null) {
/* 234 */       output.append('<');
/* 235 */       int max = this.typeArguments.length - 1;
/* 236 */       for (int j = 0; j < max; j++) {
/* 237 */         this.typeArguments[j].print(0, output);
/* 238 */         output.append(", ");
/*     */       }
/* 240 */       this.typeArguments[max].print(0, output);
/* 241 */       output.append('>');
/*     */     }
/* 243 */     if (this.accessMode == 3)
/* 244 */       output.append("this(");
/*     */     else {
/* 246 */       output.append("super(");
/*     */     }
/* 248 */     if (this.arguments != null) {
/* 249 */       for (int i = 0; i < this.arguments.length; i++) {
/* 250 */         if (i > 0) output.append(", ");
/* 251 */         this.arguments[i].printExpression(0, output);
/*     */       }
/*     */     }
/* 254 */     return output.append(");");
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope)
/*     */   {
/* 263 */     MethodScope methodScope = scope.methodScope();
/*     */     try {
/* 265 */       AbstractMethodDeclaration methodDeclaration = methodScope.referenceMethod();
/* 266 */       if ((methodDeclaration == null) || 
/* 267 */         (!methodDeclaration.isConstructor()) || 
/* 268 */         (((ConstructorDeclaration)methodDeclaration).constructorCall != this)) {
/* 269 */         scope.problemReporter().invalidExplicitConstructorCall(this);
/*     */ 
/* 271 */         if (this.qualification != null) {
/* 272 */           this.qualification.resolveType(scope);
/*     */         }
/* 274 */         if (this.typeArguments != null) {
/* 275 */           int i = 0; for (int max = this.typeArguments.length; i < max; i++) {
/* 276 */             this.typeArguments[i].resolveType(scope, true);
/*     */           }
/*     */         }
/* 279 */         if (this.arguments != null) {
/* 280 */           int i = 0; for (int max = this.arguments.length; i < max; i++)
/* 281 */             this.arguments[i].resolveType(scope);
/*     */         }
/*     */         return;
/*     */       }
/* 286 */       methodScope.isConstructorCall = true;
/* 287 */       ReferenceBinding receiverType = scope.enclosingReceiverType();
/* 288 */       boolean rcvHasError = false;
/* 289 */       if (this.accessMode != 3) {
/* 290 */         receiverType = receiverType.superclass();
/* 291 */         TypeReference superclassRef = scope.referenceType().superclass;
/* 292 */         if ((superclassRef != null) && (superclassRef.resolvedType != null) && (!superclassRef.resolvedType.isValidBinding())) {
/* 293 */           rcvHasError = true;
/*     */         }
/*     */       }
/* 296 */       if (receiverType != null)
/*     */       {
/* 298 */         if ((this.accessMode == 2) && (receiverType.erasure().id == 41)) {
/* 299 */           scope.problemReporter().cannotInvokeSuperConstructorInEnum(this, methodScope.referenceMethod().binding);
/*     */         }
/*     */ 
/* 302 */         if (this.qualification != null) {
/* 303 */           if (this.accessMode != 2) {
/* 304 */             scope.problemReporter().unnecessaryEnclosingInstanceSpecification(
/* 305 */               this.qualification, 
/* 306 */               receiverType);
/*     */           }
/* 308 */           if (!rcvHasError) {
/* 309 */             ReferenceBinding enclosingType = receiverType.enclosingType();
/* 310 */             if (enclosingType == null) {
/* 311 */               scope.problemReporter().unnecessaryEnclosingInstanceSpecification(this.qualification, receiverType);
/* 312 */               this.bits |= 8192;
/*     */             } else {
/* 314 */               TypeBinding qTb = this.qualification.resolveTypeExpecting(scope, enclosingType);
/* 315 */               this.qualification.computeConversion(scope, qTb, qTb);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 321 */       if (this.typeArguments != null) {
/* 322 */         boolean argHasError = scope.compilerOptions().sourceLevel < 3211264L;
/* 323 */         int length = this.typeArguments.length;
/* 324 */         this.genericTypeArguments = new TypeBinding[length];
/* 325 */         for (int i = 0; i < length; i++) {
/* 326 */           TypeReference typeReference = this.typeArguments[i];
/* 327 */           if ((this.genericTypeArguments[i] =  = typeReference.resolveType(scope, true)) == null) {
/* 328 */             argHasError = true;
/*     */           }
/* 330 */           if ((argHasError) && ((typeReference instanceof Wildcard))) {
/* 331 */             scope.problemReporter().illegalUsageOfWildcard(typeReference);
/*     */           }
/*     */         }
/* 334 */         if (argHasError) {
/* 335 */           if (this.arguments != null) {
/* 336 */             int i = 0; for (int max = this.arguments.length; i < max; i++) {
/* 337 */               this.arguments[i].resolveType(scope);
/*     */             }
/*     */           }
/*     */           return;
/*     */         }
/*     */       }
/* 344 */       TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
/* 345 */       boolean argsContainCast = false;
/* 346 */       if (this.arguments != null) {
/* 347 */         boolean argHasError = false;
/* 348 */         int length = this.arguments.length;
/* 349 */         argumentTypes = new TypeBinding[length];
/* 350 */         for (int i = 0; i < length; i++) {
/* 351 */           Expression argument = this.arguments[i];
/* 352 */           if ((argument instanceof CastExpression)) {
/* 353 */             argument.bits |= 32;
/* 354 */             argsContainCast = true;
/*     */           }
/* 356 */           if ((argumentTypes[i] =  = argument.resolveType(scope)) == null) {
/* 357 */             argHasError = true;
/*     */           }
/*     */         }
/* 360 */         if (argHasError) {
/* 361 */           if (receiverType == null)
/*     */             return;
/*     */           int length;
/*     */           boolean argHasError;
/* 365 */           TypeBinding[] pseudoArgs = new TypeBinding[length];
/* 366 */           int i = length;
/*     */           do { pseudoArgs[i] = (argumentTypes[i] == null ? TypeBinding.NULL : argumentTypes[i]);
/*     */ 
/* 366 */             i--; } while (i >= 0);
/*     */ 
/* 369 */           this.binding = scope.findMethod(receiverType, TypeConstants.INIT, pseudoArgs, this);
/* 370 */           if ((this.binding != null) && (!this.binding.isValidBinding())) {
/* 371 */             MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
/*     */ 
/* 373 */             if (closestMatch != null) {
/* 374 */               if (closestMatch.original().typeVariables != Binding.NO_TYPE_VARIABLES)
/*     */               {
/* 376 */                 closestMatch = scope.environment().createParameterizedGenericMethod(closestMatch.original(), null);
/*     */               }
/* 378 */               this.binding = closestMatch;
/* 379 */               MethodBinding closestMatchOriginal = closestMatch.original();
/* 380 */               if ((closestMatchOriginal.isOrEnclosedByPrivateType()) && (!scope.isDefinedInMethod(closestMatchOriginal)))
/*     */               {
/* 382 */                 closestMatchOriginal.modifiers |= 134217728;
/*     */               }
/*     */             }
/*     */           }
/*     */           return;
/*     */         }
/* 388 */       } else if (receiverType.erasure().id == 41)
/*     */       {
/* 390 */         argumentTypes = new TypeBinding[] { scope.getJavaLangString(), TypeBinding.INT };
/*     */       }
/* 392 */       if (receiverType == null)
/*     */         return;
/* 395 */       if ((this.binding = scope.getConstructor(receiverType, argumentTypes, this)).isValidBinding()) {
/* 396 */         if (((this.binding.tagBits & 0x80) != 0L) && 
/* 397 */           (!methodScope.enclosingSourceType().isAnonymousType())) {
/* 398 */           scope.problemReporter().missingTypeInConstructor(this, this.binding);
/*     */         }
/*     */ 
/* 401 */         if (isMethodUseDeprecated(this.binding, scope, this.accessMode != 1)) {
/* 402 */           scope.problemReporter().deprecatedMethod(this.binding, this);
/*     */         }
/* 404 */         if (checkInvocationArguments(scope, null, receiverType, this.binding, this.arguments, argumentTypes, argsContainCast, this)) {
/* 405 */           this.bits |= 65536;
/*     */         }
/* 407 */         if (this.binding.isOrEnclosedByPrivateType()) {
/* 408 */           this.binding.original().modifiers |= 134217728;
/*     */         }
/* 410 */         if ((this.typeArguments != null) && 
/* 411 */           (this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES))
/* 412 */           scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
/*     */       }
/*     */       else {
/* 415 */         if (this.binding.declaringClass == null) {
/* 416 */           this.binding.declaringClass = receiverType;
/*     */         }
/* 418 */         if (rcvHasError) return;
/* 420 */         scope.problemReporter().invalidConstructor(this, this.binding);
/*     */       }
/*     */     } finally {
/* 423 */       methodScope.isConstructorCall = false; } methodScope.isConstructorCall = false;
/*     */   }
/*     */ 
/*     */   public void setActualReceiverType(ReferenceBinding receiverType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setDepth(int depth)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setFieldIndex(int depth)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 440 */     if (visitor.visit(this, scope)) {
/* 441 */       if (this.qualification != null) {
/* 442 */         this.qualification.traverse(visitor, scope);
/*     */       }
/* 444 */       if (this.typeArguments != null) {
/* 445 */         int i = 0; for (int typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; i++) {
/* 446 */           this.typeArguments[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 449 */       if (this.arguments != null) {
/* 450 */         int i = 0; for (int argumentLength = this.arguments.length; i < argumentLength; i++)
/* 451 */           this.arguments[i].traverse(visitor, scope);
/*     */       }
/*     */     }
/* 454 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall
 * JD-Core Version:    0.6.0
 */