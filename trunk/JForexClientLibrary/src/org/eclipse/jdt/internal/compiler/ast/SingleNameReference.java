/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class SingleNameReference extends NameReference
/*     */   implements OperatorIds
/*     */ {
/*     */   public static final int READ = 0;
/*     */   public static final int WRITE = 1;
/*     */   public char[] token;
/*     */   public MethodBinding[] syntheticAccessors;
/*     */   public TypeBinding genericCast;
/*     */ 
/*     */   public SingleNameReference(char[] source, long pos)
/*     */   {
/*  52 */     this.token = source;
/*  53 */     this.sourceStart = (int)(pos >>> 32);
/*  54 */     this.sourceEnd = (int)pos;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Assignment assignment, boolean isCompound) {
/*  58 */     boolean isReachable = (flowInfo.tagBits & 0x1) == 0;
/*     */ 
/*  60 */     if (isCompound) {
/*  61 */       switch (this.bits & 0x7)
/*     */       {
/*     */       case 1:
/*     */         FieldBinding fieldBinding;
/*  64 */         if (((fieldBinding = (FieldBinding)this.binding).isBlankFinal()) && 
/*  65 */           (currentScope.needBlankFinalFieldInitializationCheck(fieldBinding))) {
/*  66 */           FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo);
/*  67 */           if (!fieldInits.isDefinitelyAssigned(fieldBinding)) {
/*  68 */             currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
/*     */           }
/*     */         }
/*  71 */         manageSyntheticAccessIfNecessary(currentScope, flowInfo, true);
/*  72 */         break;
/*     */       case 2:
/*     */         LocalVariableBinding localBinding;
/*  76 */         if (!flowInfo.isDefinitelyAssigned(localBinding = (LocalVariableBinding)this.binding)) {
/*  77 */           currentScope.problemReporter().uninitializedLocalVariable(localBinding, this);
/*     */         }
/*     */ 
/*  80 */         if (isReachable) {
/*  81 */           localBinding.useFlag = 1; } else {
/*  82 */           if (localBinding.useFlag != 0) break;
/*  83 */           localBinding.useFlag = 2;
/*     */         }
/*     */       }
/*     */     }
/*  87 */     if (assignment.expression != null)
/*     */     {
/*  88 */       flowInfo = assignment.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
/*     */     }
/*  90 */     switch (this.bits & 0x7) {
/*     */     case 1:
/*  92 */       manageSyntheticAccessIfNecessary(currentScope, flowInfo, false);
/*     */ 
/*  95 */       FieldBinding fieldBinding = (FieldBinding)this.binding;
/*  96 */       if (!fieldBinding.isFinal())
/*     */         break;
/*  98 */       if ((!isCompound) && (fieldBinding.isBlankFinal()) && (currentScope.allowBlankFinalFieldAssignment(fieldBinding))) {
/*  99 */         if (flowInfo.isPotentiallyAssigned(fieldBinding))
/* 100 */           currentScope.problemReporter().duplicateInitializationOfBlankFinalField(fieldBinding, this);
/*     */         else {
/* 102 */           flowContext.recordSettingFinal(fieldBinding, this, flowInfo);
/*     */         }
/* 104 */         flowInfo.markAsDefinitelyAssigned(fieldBinding);
/*     */       } else {
/* 106 */         currentScope.problemReporter().cannotAssignToFinalField(fieldBinding, this);
/*     */       }
/*     */ 
/* 109 */       break;
/*     */     case 2:
/* 111 */       LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
/* 112 */       if (!flowInfo.isDefinitelyAssigned(localBinding))
/* 113 */         this.bits |= 8;
/*     */       else {
/* 115 */         this.bits &= -9;
/*     */       }
/* 117 */       if (localBinding.isFinal()) {
/* 118 */         if ((this.bits & 0x1FE0) == 0)
/*     */         {
/* 120 */           if (((isReachable) && (isCompound)) || (!localBinding.isBlankFinal()))
/* 121 */             currentScope.problemReporter().cannotAssignToFinalLocal(localBinding, this);
/* 122 */           else if (flowInfo.isPotentiallyAssigned(localBinding))
/* 123 */             currentScope.problemReporter().duplicateInitializationOfFinalLocal(localBinding, this);
/*     */           else
/* 125 */             flowContext.recordSettingFinal(localBinding, this, flowInfo);
/*     */         }
/*     */         else {
/* 128 */           currentScope.problemReporter().cannotAssignToFinalOuterLocal(localBinding, this);
/*     */         }
/*     */       }
/* 131 */       else if ((localBinding.tagBits & 0x400) != 0L) {
/* 132 */         currentScope.problemReporter().parameterAssignment(localBinding, this);
/*     */       }
/* 134 */       flowInfo.markAsDefinitelyAssigned(localBinding);
/*     */     }
/* 136 */     manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
/* 137 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/* 141 */     return analyseCode(currentScope, flowContext, flowInfo, true);
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, boolean valueRequired) {
/* 145 */     switch (this.bits & 0x7) {
/*     */     case 1:
/* 147 */       if ((valueRequired) || (currentScope.compilerOptions().complianceLevel >= 3145728L)) {
/* 148 */         manageSyntheticAccessIfNecessary(currentScope, flowInfo, true);
/*     */       }
/*     */ 
/* 151 */       FieldBinding fieldBinding = (FieldBinding)this.binding;
/* 152 */       if ((!fieldBinding.isBlankFinal()) || (!currentScope.needBlankFinalFieldInitializationCheck(fieldBinding))) break;
/* 153 */       FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo);
/* 154 */       if (fieldInits.isDefinitelyAssigned(fieldBinding)) break;
/* 155 */       currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
/*     */ 
/* 158 */       break;
/*     */     case 2:
/*     */       LocalVariableBinding localBinding;
/* 161 */       if (!flowInfo.isDefinitelyAssigned(localBinding = (LocalVariableBinding)this.binding)) {
/* 162 */         currentScope.problemReporter().uninitializedLocalVariable(localBinding, this);
/*     */       }
/* 164 */       if ((flowInfo.tagBits & 0x1) == 0) {
/* 165 */         localBinding.useFlag = 1; } else {
/* 166 */         if (localBinding.useFlag != 0) break;
/* 167 */         localBinding.useFlag = 2;
/*     */       }
/*     */     }
/* 170 */     if (valueRequired) {
/* 171 */       manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
/*     */     }
/* 173 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public TypeBinding checkFieldAccess(BlockScope scope) {
/* 177 */     FieldBinding fieldBinding = (FieldBinding)this.binding;
/* 178 */     this.constant = fieldBinding.constant();
/*     */ 
/* 180 */     this.bits &= -8;
/* 181 */     this.bits |= 1;
/* 182 */     MethodScope methodScope = scope.methodScope();
/* 183 */     if (fieldBinding.isStatic())
/*     */     {
/* 185 */       ReferenceBinding declaringClass = fieldBinding.declaringClass;
/* 186 */       if (declaringClass.isEnum()) {
/* 187 */         SourceTypeBinding sourceType = scope.enclosingSourceType();
/* 188 */         if ((this.constant == Constant.NotAConstant) && 
/* 189 */           (!methodScope.isStatic) && 
/* 190 */           ((sourceType == declaringClass) || (sourceType.superclass == declaringClass)) && 
/* 191 */           (methodScope.isInsideInitializerOrConstructor()))
/* 192 */           scope.problemReporter().enumStaticFieldUsedDuringInitialization(fieldBinding, this);
/*     */       }
/*     */     }
/*     */     else {
/* 196 */       if (scope.compilerOptions().getSeverity(4194304) != -1) {
/* 197 */         scope.problemReporter().unqualifiedFieldAccess(this, fieldBinding);
/*     */       }
/*     */ 
/* 200 */       if (methodScope.isStatic) {
/* 201 */         scope.problemReporter().staticFieldAccessToNonStaticVariable(this, fieldBinding);
/* 202 */         return fieldBinding.type;
/*     */       }
/*     */     }
/*     */ 
/* 206 */     if (isFieldUseDeprecated(fieldBinding, scope, (this.bits & 0x2000) != 0)) {
/* 207 */       scope.problemReporter().deprecatedField(fieldBinding, this);
/*     */     }
/* 209 */     if (((this.bits & 0x2000) == 0) && 
/* 210 */       (methodScope.enclosingSourceType() == fieldBinding.original().declaringClass) && 
/* 211 */       (methodScope.lastVisibleFieldID >= 0) && 
/* 212 */       (fieldBinding.id >= methodScope.lastVisibleFieldID) && (
/* 213 */       (!fieldBinding.isStatic()) || (methodScope.isStatic))) {
/* 214 */       scope.problemReporter().forwardReference(this, 0, fieldBinding);
/* 215 */       this.bits |= 536870912;
/*     */     }
/* 217 */     return fieldBinding.type;
/*     */   }
/*     */ 
/*     */   public void computeConversion(Scope scope, TypeBinding runtimeTimeType, TypeBinding compileTimeType)
/*     */   {
/* 225 */     if ((runtimeTimeType == null) || (compileTimeType == null))
/* 226 */       return;
/* 227 */     if (((this.bits & 0x1) != 0) && (this.binding != null) && (this.binding.isValidBinding()))
/*     */     {
/* 229 */       FieldBinding field = (FieldBinding)this.binding;
/* 230 */       FieldBinding originalBinding = field.original();
/* 231 */       TypeBinding originalType = originalBinding.type;
/*     */ 
/* 233 */       if (originalType.leafComponentType().isTypeVariable()) {
/* 234 */         TypeBinding targetType = (!compileTimeType.isBaseType()) && (runtimeTimeType.isBaseType()) ? 
/* 235 */           compileTimeType : 
/* 236 */           runtimeTimeType;
/* 237 */         this.genericCast = originalType.genericCast(scope.boxing(targetType));
/* 238 */         if ((this.genericCast instanceof ReferenceBinding)) {
/* 239 */           ReferenceBinding referenceCast = (ReferenceBinding)this.genericCast;
/* 240 */           if (!referenceCast.canBeSeenBy(scope)) {
/* 241 */             scope.problemReporter().invalidType(this, 
/* 242 */               new ProblemReferenceBinding(
/* 243 */               CharOperation.splitOn('.', referenceCast.shortReadableName()), 
/* 244 */               referenceCast, 
/* 245 */               2));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 250 */     super.computeConversion(scope, runtimeTimeType, compileTimeType);
/*     */   }
/*     */ 
/*     */   public void generateAssignment(BlockScope currentScope, CodeStream codeStream, Assignment assignment, boolean valueRequired)
/*     */   {
/* 255 */     if (assignment.expression.isCompactableOperation()) {
/* 256 */       BinaryExpression operation = (BinaryExpression)assignment.expression;
/* 257 */       int operator = (operation.bits & 0xFC0) >> 6;
/*     */       SingleNameReference variableReference;
/* 259 */       if (((operation.left instanceof SingleNameReference)) && ((variableReference = (SingleNameReference)operation.left).binding == this.binding))
/*     */       {
/* 261 */         variableReference.generateCompoundAssignment(currentScope, codeStream, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], operation.right, operator, operation.implicitConversion, valueRequired);
/* 262 */         if (valueRequired) {
/* 263 */           codeStream.generateImplicitConversion(assignment.implicitConversion);
/*     */         }
/* 265 */         return;
/*     */       }
/*     */       SingleNameReference variableReference;
/* 267 */       if (((operation.right instanceof SingleNameReference)) && 
/* 268 */         ((operator == 14) || (operator == 15)) && 
/* 269 */         ((variableReference = (SingleNameReference)operation.right).binding == this.binding) && 
/* 270 */         (operation.left.constant != Constant.NotAConstant) && 
/* 271 */         ((operation.left.implicitConversion & 0xFF) >> 4 != 11) && 
/* 272 */         ((operation.right.implicitConversion & 0xFF) >> 4 != 11))
/*     */       {
/* 274 */         variableReference.generateCompoundAssignment(currentScope, codeStream, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], operation.left, operator, operation.implicitConversion, valueRequired);
/* 275 */         if (valueRequired) {
/* 276 */           codeStream.generateImplicitConversion(assignment.implicitConversion);
/*     */         }
/* 278 */         return;
/*     */       }
/*     */     }
/* 281 */     switch (this.bits & 0x7) {
/*     */     case 1:
/* 283 */       int pc = codeStream.position;
/* 284 */       FieldBinding codegenBinding = ((FieldBinding)this.binding).original();
/* 285 */       if (!codegenBinding.isStatic()) {
/* 286 */         if ((this.bits & 0x1FE0) != 0) {
/* 287 */           ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
/* 288 */           Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
/* 289 */           codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
/*     */         } else {
/* 291 */           generateReceiver(codeStream);
/*     */         }
/*     */       }
/* 294 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 295 */       assignment.expression.generateCode(currentScope, codeStream, true);
/* 296 */       fieldStore(currentScope, codeStream, codegenBinding, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], this.actualReceiverType, true, valueRequired);
/* 297 */       if (valueRequired) {
/* 298 */         codeStream.generateImplicitConversion(assignment.implicitConversion);
/*     */       }
/*     */ 
/* 301 */       return;
/*     */     case 2:
/* 303 */       LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
/* 304 */       if (localBinding.resolvedPosition != -1) {
/* 305 */         assignment.expression.generateCode(currentScope, codeStream, true);
/*     */       } else {
/* 307 */         if (assignment.expression.constant != Constant.NotAConstant)
/*     */         {
/* 309 */           if (valueRequired)
/* 310 */             codeStream.generateConstant(assignment.expression.constant, assignment.implicitConversion);
/*     */         }
/*     */         else {
/* 313 */           assignment.expression.generateCode(currentScope, codeStream, true);
/*     */ 
/* 316 */           if (valueRequired)
/* 317 */             codeStream.generateImplicitConversion(assignment.implicitConversion);
/*     */           else {
/* 319 */             switch (localBinding.type.id) {
/*     */             case 7:
/*     */             case 8:
/* 322 */               codeStream.pop2();
/* 323 */               break;
/*     */             default:
/* 325 */               codeStream.pop();
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 330 */         return;
/*     */       }
/*     */ 
/* 333 */       if ((localBinding.type.isArrayType()) && (
/* 334 */         (assignment.expression.resolvedType == TypeBinding.NULL) || (
/* 335 */         ((assignment.expression instanceof CastExpression)) && 
/* 336 */         (((CastExpression)assignment.expression).innermostCastedExpression().resolvedType == TypeBinding.NULL)))) {
/* 337 */         codeStream.checkcast(localBinding.type);
/*     */       }
/*     */ 
/* 341 */       codeStream.store(localBinding, valueRequired);
/* 342 */       if ((this.bits & 0x8) != 0) {
/* 343 */         localBinding.recordInitializationStartPC(codeStream.position);
/*     */       }
/*     */ 
/* 346 */       if (!valueRequired) break;
/* 347 */       codeStream.generateImplicitConversion(assignment.implicitConversion);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 353 */     int pc = codeStream.position;
/* 354 */     if (this.constant != Constant.NotAConstant) {
/* 355 */       if (valueRequired) {
/* 356 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/*     */       }
/* 358 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 359 */       return;
/*     */     }
/* 361 */     switch (this.bits & 0x7) {
/*     */     case 1:
/* 363 */       FieldBinding codegenField = ((FieldBinding)this.binding).original();
/* 364 */       Constant fieldConstant = codegenField.constant();
/* 365 */       if (fieldConstant != Constant.NotAConstant)
/*     */       {
/* 367 */         if (valueRequired) {
/* 368 */           codeStream.generateConstant(fieldConstant, this.implicitConversion);
/*     */         }
/* 370 */         codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 371 */         return;
/*     */       }
/* 373 */       if (codegenField.isStatic()) {
/* 374 */         if (!valueRequired)
/*     */         {
/* 376 */           if ((((FieldBinding)this.binding).original().declaringClass == this.actualReceiverType.erasure()) && 
/* 377 */             ((this.implicitConversion & 0x400) == 0) && 
/* 378 */             (this.genericCast == null))
/*     */           {
/* 380 */             codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 381 */             return;
/*     */           }
/*     */         }
/* 384 */         if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 385 */           TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
/* 386 */           codeStream.fieldAccess(-78, codegenField, constantPoolDeclaringClass);
/*     */         } else {
/* 388 */           codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */         }
/*     */       } else {
/* 391 */         if ((!valueRequired) && 
/* 392 */           ((this.implicitConversion & 0x400) == 0) && 
/* 393 */           (this.genericCast == null))
/*     */         {
/* 395 */           codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 396 */           return;
/*     */         }
/*     */ 
/* 399 */         if ((this.bits & 0x1FE0) != 0) {
/* 400 */           ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
/* 401 */           Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
/* 402 */           codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
/*     */         } else {
/* 404 */           generateReceiver(codeStream);
/*     */         }
/*     */ 
/* 407 */         if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 408 */           TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
/* 409 */           codeStream.fieldAccess(-76, codegenField, constantPoolDeclaringClass);
/*     */         } else {
/* 411 */           codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */         }
/*     */       }
/* 414 */       break;
/*     */     case 2:
/* 416 */       LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
/* 417 */       if ((!valueRequired) && ((this.implicitConversion & 0x400) == 0))
/*     */       {
/* 419 */         codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 420 */         return;
/*     */       }
/*     */ 
/* 423 */       if ((this.bits & 0x1FE0) != 0)
/*     */       {
/* 425 */         VariableBinding[] path = currentScope.getEmulationPath(localBinding);
/* 426 */         codeStream.generateOuterAccess(path, this, localBinding, currentScope);
/*     */       }
/*     */       else {
/* 429 */         codeStream.load(localBinding);
/*     */       }
/* 431 */       break;
/*     */     default:
/* 433 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 434 */       return;
/*     */     }
/*     */ 
/* 438 */     if (this.genericCast != null) codeStream.checkcast(this.genericCast);
/* 439 */     if (valueRequired) {
/* 440 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */     } else {
/* 442 */       boolean isUnboxing = (this.implicitConversion & 0x400) != 0;
/*     */ 
/* 444 */       if (isUnboxing) codeStream.generateImplicitConversion(this.implicitConversion);
/* 445 */       switch (isUnboxing ? postConversionType(currentScope).id : this.resolvedType.id) {
/*     */       case 7:
/*     */       case 8:
/* 448 */         codeStream.pop2();
/* 449 */         break;
/*     */       default:
/* 451 */         codeStream.pop();
/*     */       }
/*     */     }
/* 454 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired)
/*     */   {
/* 464 */     generateCompoundAssignment(
/* 465 */       currentScope, 
/* 466 */       codeStream, 
/* 467 */       this.syntheticAccessors == null ? null : this.syntheticAccessors[1], 
/* 468 */       expression, 
/* 469 */       operator, 
/* 470 */       assignmentImplicitConversion, 
/* 471 */       valueRequired);
/*     */   }
/*     */ 
/*     */   public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, MethodBinding writeAccessor, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired)
/*     */   {
/* 479 */     switch (this.bits & 0x7) {
/*     */     case 1:
/* 481 */       FieldBinding codegenField = ((FieldBinding)this.binding).original();
/* 482 */       if (codegenField.isStatic()) {
/* 483 */         if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 484 */           TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
/* 485 */           codeStream.fieldAccess(-78, codegenField, constantPoolDeclaringClass);
/*     */         } else {
/* 487 */           codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */         }
/*     */       } else {
/* 490 */         if ((this.bits & 0x1FE0) != 0) {
/* 491 */           ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
/* 492 */           Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
/* 493 */           codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
/*     */         } else {
/* 495 */           codeStream.aload_0();
/*     */         }
/* 497 */         codeStream.dup();
/* 498 */         if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 499 */           TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
/* 500 */           codeStream.fieldAccess(-76, codegenField, constantPoolDeclaringClass);
/*     */         } else {
/* 502 */           codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */         }
/*     */       }
/* 505 */       break;
/*     */     case 2:
/* 507 */       LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
/*     */ 
/* 509 */       switch (localBinding.type.id) {
/*     */       case 11:
/* 511 */         codeStream.generateStringConcatenationAppend(currentScope, this, expression);
/* 512 */         if (valueRequired) {
/* 513 */           codeStream.dup();
/*     */         }
/* 515 */         codeStream.store(localBinding, false);
/* 516 */         return;
/*     */       case 10:
/*     */         Constant assignConstant;
/* 519 */         if (((assignConstant = expression.constant) == Constant.NotAConstant) || 
/* 520 */           (assignConstant.typeID() == 9) || 
/* 521 */           (assignConstant.typeID() == 8)) break;
/* 522 */         switch (operator) {
/*     */         case 14:
/* 524 */           int increment = assignConstant.intValue();
/* 525 */           if (increment != (short)increment) break;
/* 526 */           codeStream.iinc(localBinding.resolvedPosition, increment);
/* 527 */           if (valueRequired) {
/* 528 */             codeStream.load(localBinding);
/*     */           }
/* 530 */           return;
/*     */         case 13:
/* 532 */           int increment = -assignConstant.intValue();
/* 533 */           if (increment != (short)increment) break;
/* 534 */           codeStream.iinc(localBinding.resolvedPosition, increment);
/* 535 */           if (valueRequired) {
/* 536 */             codeStream.load(localBinding);
/*     */           }
/* 538 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 543 */       codeStream.load(localBinding);
/*     */     }
/*     */     int operationTypeID;
/* 548 */     switch (operationTypeID = (this.implicitConversion & 0xFF) >> 4)
/*     */     {
/*     */     case 0:
/*     */     case 1:
/*     */     case 11:
/* 555 */       codeStream.generateStringConcatenationAppend(currentScope, null, expression);
/*     */ 
/* 557 */       break;
/*     */     default:
/* 560 */       if (this.genericCast != null)
/* 561 */         codeStream.checkcast(this.genericCast);
/* 562 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */ 
/* 564 */       if (expression == IntLiteral.One)
/* 565 */         codeStream.generateConstant(expression.constant, this.implicitConversion);
/*     */       else {
/* 567 */         expression.generateCode(currentScope, codeStream, true);
/*     */       }
/*     */ 
/* 570 */       codeStream.sendOperator(operator, operationTypeID);
/*     */ 
/* 572 */       codeStream.generateImplicitConversion(assignmentImplicitConversion);
/*     */     }
/*     */ 
/* 575 */     switch (this.bits & 0x7) {
/*     */     case 1:
/* 577 */       FieldBinding codegenField = ((FieldBinding)this.binding).original();
/* 578 */       fieldStore(currentScope, codeStream, codegenField, writeAccessor, this.actualReceiverType, true, valueRequired);
/*     */ 
/* 580 */       return;
/*     */     case 2:
/* 582 */       LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
/* 583 */       if (valueRequired) {
/* 584 */         switch (localBinding.type.id) {
/*     */         case 7:
/*     */         case 8:
/* 587 */           codeStream.dup2();
/* 588 */           break;
/*     */         default:
/* 590 */           codeStream.dup();
/*     */         }
/*     */       }
/*     */ 
/* 594 */       codeStream.store(localBinding, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generatePostIncrement(BlockScope currentScope, CodeStream codeStream, CompoundAssignment postIncrement, boolean valueRequired) {
/* 599 */     switch (this.bits & 0x7) {
/*     */     case 1:
/* 601 */       FieldBinding codegenField = ((FieldBinding)this.binding).original();
/* 602 */       if (codegenField.isStatic()) {
/* 603 */         if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 604 */           TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
/* 605 */           codeStream.fieldAccess(-78, codegenField, constantPoolDeclaringClass);
/*     */         } else {
/* 607 */           codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */         }
/*     */       } else {
/* 610 */         if ((this.bits & 0x1FE0) != 0) {
/* 611 */           ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
/* 612 */           Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
/* 613 */           codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
/*     */         } else {
/* 615 */           codeStream.aload_0();
/*     */         }
/* 617 */         codeStream.dup();
/* 618 */         if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 619 */           TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
/* 620 */           codeStream.fieldAccess(-76, codegenField, constantPoolDeclaringClass);
/*     */         } else {
/* 622 */           codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */         }
/*     */       }
/*     */       TypeBinding operandType;
/*     */       TypeBinding operandType;
/* 626 */       if (this.genericCast != null) {
/* 627 */         codeStream.checkcast(this.genericCast);
/* 628 */         operandType = this.genericCast;
/*     */       } else {
/* 630 */         operandType = codegenField.type;
/*     */       }
/* 632 */       if (valueRequired) {
/* 633 */         if (codegenField.isStatic())
/* 634 */           switch (operandType.id) {
/*     */           case 7:
/*     */           case 8:
/* 637 */             codeStream.dup2();
/* 638 */             break;
/*     */           default:
/* 640 */             codeStream.dup();
/* 641 */             break;
/*     */           }
/*     */         else {
/* 644 */           switch (operandType.id) {
/*     */           case 7:
/*     */           case 8:
/* 647 */             codeStream.dup2_x1();
/* 648 */             break;
/*     */           default:
/* 650 */             codeStream.dup_x1();
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 655 */       codeStream.generateImplicitConversion(this.implicitConversion);
/* 656 */       codeStream.generateConstant(postIncrement.expression.constant, this.implicitConversion);
/* 657 */       codeStream.sendOperator(postIncrement.operator, this.implicitConversion & 0xF);
/* 658 */       codeStream.generateImplicitConversion(postIncrement.preAssignImplicitConversion);
/* 659 */       fieldStore(currentScope, codeStream, codegenField, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], this.actualReceiverType, true, false);
/*     */ 
/* 661 */       return;
/*     */     case 2:
/* 663 */       LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
/*     */ 
/* 665 */       if (localBinding.type == TypeBinding.INT) {
/* 666 */         if (valueRequired) {
/* 667 */           codeStream.load(localBinding);
/*     */         }
/* 669 */         if (postIncrement.operator == 14)
/* 670 */           codeStream.iinc(localBinding.resolvedPosition, 1);
/*     */         else
/* 672 */           codeStream.iinc(localBinding.resolvedPosition, -1);
/*     */       }
/*     */       else {
/* 675 */         codeStream.load(localBinding);
/* 676 */         if (valueRequired) {
/* 677 */           switch (localBinding.type.id) {
/*     */           case 7:
/*     */           case 8:
/* 680 */             codeStream.dup2();
/* 681 */             break;
/*     */           default:
/* 683 */             codeStream.dup();
/*     */           }
/*     */         }
/*     */ 
/* 687 */         codeStream.generateImplicitConversion(this.implicitConversion);
/* 688 */         codeStream.generateConstant(postIncrement.expression.constant, this.implicitConversion);
/* 689 */         codeStream.sendOperator(postIncrement.operator, this.implicitConversion & 0xF);
/* 690 */         codeStream.generateImplicitConversion(postIncrement.preAssignImplicitConversion);
/* 691 */         codeStream.store(localBinding, false);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateReceiver(CodeStream codeStream) {
/* 697 */     codeStream.aload_0();
/*     */   }
/*     */ 
/*     */   public TypeBinding[] genericTypeArguments()
/*     */   {
/* 704 */     return null;
/*     */   }
/*     */ 
/*     */   public LocalVariableBinding localVariableBinding()
/*     */   {
/* 712 */     switch (this.bits & 0x7) {
/*     */     case 1:
/* 714 */       break;
/*     */     case 2:
/* 716 */       return (LocalVariableBinding)this.binding;
/*     */     }
/* 718 */     return null;
/*     */   }
/*     */ 
/*     */   public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
/* 722 */     if ((flowInfo.tagBits & 0x1) == 0)
/*     */     {
/* 724 */       if (((this.bits & 0x1FE0) == 0) || (this.constant != Constant.NotAConstant)) return;
/*     */ 
/* 726 */       if ((this.bits & 0x7) == 2)
/* 727 */         currentScope.emulateOuterAccess((LocalVariableBinding)this.binding);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo, boolean isReadAccess)
/*     */   {
/* 733 */     if ((flowInfo.tagBits & 0x1) != 0) return;
/*     */ 
/* 736 */     if (this.constant != Constant.NotAConstant) {
/* 737 */       return;
/*     */     }
/* 739 */     if ((this.bits & 0x1) != 0) {
/* 740 */       FieldBinding fieldBinding = (FieldBinding)this.binding;
/* 741 */       FieldBinding codegenField = fieldBinding.original();
/* 742 */       if (((this.bits & 0x1FE0) != 0) && (
/* 743 */         (codegenField.isPrivate()) || (
/* 744 */         (codegenField.isProtected()) && 
/* 745 */         (codegenField.declaringClass.getPackage() != currentScope.enclosingSourceType().getPackage())))) {
/* 746 */         if (this.syntheticAccessors == null)
/* 747 */           this.syntheticAccessors = new MethodBinding[2];
/* 748 */         this.syntheticAccessors[(isReadAccess ? 0 : 1)] = 
/* 749 */           ((SourceTypeBinding)currentScope.enclosingSourceType()
/* 750 */           .enclosingTypeAt((this.bits & 0x1FE0) >> 5)).addSyntheticMethod(codegenField, isReadAccess, false);
/* 751 */         currentScope.problemReporter().needToEmulateFieldAccess(codegenField, this, isReadAccess);
/* 752 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int nullStatus(FlowInfo flowInfo) {
/* 758 */     if ((this.constant != null) && (this.constant != Constant.NotAConstant)) {
/* 759 */       return -1;
/*     */     }
/* 761 */     switch (this.bits & 0x7) {
/*     */     case 1:
/* 763 */       return 0;
/*     */     case 2:
/* 765 */       LocalVariableBinding local = (LocalVariableBinding)this.binding;
/* 766 */       if (local == null) break;
/* 767 */       if (flowInfo.isDefinitelyNull(local))
/* 768 */         return 1;
/* 769 */       if (flowInfo.isDefinitelyNonNull(local))
/* 770 */         return -1;
/* 771 */       return 0;
/*     */     }
/*     */ 
/* 774 */     return -1;
/*     */   }
/*     */ 
/*     */   public TypeBinding postConversionType(Scope scope)
/*     */   {
/* 781 */     TypeBinding convertedType = this.resolvedType;
/* 782 */     if (this.genericCast != null)
/* 783 */       convertedType = this.genericCast;
/* 784 */     int runtimeType = (this.implicitConversion & 0xFF) >> 4;
/* 785 */     switch (runtimeType) {
/*     */     case 5:
/* 787 */       convertedType = TypeBinding.BOOLEAN;
/* 788 */       break;
/*     */     case 3:
/* 790 */       convertedType = TypeBinding.BYTE;
/* 791 */       break;
/*     */     case 4:
/* 793 */       convertedType = TypeBinding.SHORT;
/* 794 */       break;
/*     */     case 2:
/* 796 */       convertedType = TypeBinding.CHAR;
/* 797 */       break;
/*     */     case 10:
/* 799 */       convertedType = TypeBinding.INT;
/* 800 */       break;
/*     */     case 9:
/* 802 */       convertedType = TypeBinding.FLOAT;
/* 803 */       break;
/*     */     case 7:
/* 805 */       convertedType = TypeBinding.LONG;
/* 806 */       break;
/*     */     case 8:
/* 808 */       convertedType = TypeBinding.DOUBLE;
/*     */     case 6:
/*     */     }
/*     */ 
/* 812 */     if ((this.implicitConversion & 0x200) != 0) {
/* 813 */       convertedType = scope.environment().computeBoxingType(convertedType);
/*     */     }
/* 815 */     return convertedType;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 819 */     return output.append(this.token);
/*     */   }
/*     */ 
/*     */   public TypeBinding reportError(BlockScope scope) {
/* 823 */     this.constant = Constant.NotAConstant;
/* 824 */     if ((this.binding instanceof ProblemFieldBinding))
/* 825 */       scope.problemReporter().invalidField(this, (FieldBinding)this.binding);
/* 826 */     else if (((this.binding instanceof ProblemReferenceBinding)) || ((this.binding instanceof MissingTypeBinding)))
/* 827 */       scope.problemReporter().invalidType(this, (TypeBinding)this.binding);
/*     */     else {
/* 829 */       scope.problemReporter().unresolvableReference(this, this.binding);
/*     */     }
/* 831 */     return null;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 837 */     if (this.actualReceiverType != null) {
/* 838 */       this.binding = scope.getField(this.actualReceiverType, this.token, this);
/*     */     } else {
/* 840 */       this.actualReceiverType = scope.enclosingSourceType();
/* 841 */       this.binding = scope.getBinding(this.token, this.bits & 0x7, this, true);
/*     */     }
/* 843 */     if (this.binding.isValidBinding())
/* 844 */       switch (this.bits & 0x7) {
/*     */       case 3:
/*     */       case 7:
/* 847 */         if ((this.binding instanceof VariableBinding)) {
/* 848 */           VariableBinding variable = (VariableBinding)this.binding;
/*     */           TypeBinding variableType;
/* 850 */           if ((this.binding instanceof LocalVariableBinding)) {
/* 851 */             this.bits &= -8;
/* 852 */             this.bits |= 2;
/* 853 */             if ((!variable.isFinal()) && ((this.bits & 0x1FE0) != 0)) {
/* 854 */               scope.problemReporter().cannotReferToNonFinalOuterLocal((LocalVariableBinding)variable, this);
/*     */             }
/* 856 */             TypeBinding variableType = variable.type;
/* 857 */             this.constant = ((this.bits & 0x2000) == 0 ? variable.constant() : Constant.NotAConstant);
/*     */           }
/*     */           else {
/* 860 */             variableType = checkFieldAccess(scope);
/*     */           }
/*     */ 
/* 863 */           if (variableType != null) {
/* 864 */             this.resolvedType = 
/* 866 */               (variableType = (this.bits & 0x2000) == 0 ? 
/* 865 */               variableType.capture(scope, this.sourceEnd) : 
/* 866 */               variableType);
/* 867 */             if ((variableType.tagBits & 0x80) != 0L) {
/* 868 */               if ((this.bits & 0x2) == 0)
/*     */               {
/* 870 */                 scope.problemReporter().invalidType(this, variableType);
/*     */               }
/* 872 */               return null;
/*     */             }
/*     */           }
/* 875 */           return variableType;
/*     */         }
/*     */ 
/* 879 */         this.bits &= -8;
/* 880 */         this.bits |= 4;
/*     */       case 4:
/* 883 */         this.constant = Constant.NotAConstant;
/*     */ 
/* 885 */         TypeBinding type = (TypeBinding)this.binding;
/* 886 */         if (isTypeUseDeprecated(type, scope))
/* 887 */           scope.problemReporter().deprecatedType(type, this);
/* 888 */         type = scope.environment().convertToRawType(type, false);
/* 889 */         return this.resolvedType = type;
/*     */       case 5:
/*     */       case 6:
/*     */       }
/* 893 */     return this.resolvedType = reportError(scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 897 */     visitor.visit(this, scope);
/* 898 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 902 */     visitor.visit(this, scope);
/* 903 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public String unboundReferenceErrorName() {
/* 907 */     return new String(this.token);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 * JD-Core Version:    0.6.0
 */