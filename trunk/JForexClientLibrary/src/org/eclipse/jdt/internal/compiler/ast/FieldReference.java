/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class FieldReference extends Reference
/*     */   implements InvocationSite
/*     */ {
/*     */   public static final int READ = 0;
/*     */   public static final int WRITE = 1;
/*     */   public Expression receiver;
/*     */   public char[] token;
/*     */   public FieldBinding binding;
/*     */   public MethodBinding[] syntheticAccessors;
/*     */   public long nameSourcePosition;
/*     */   public TypeBinding actualReceiverType;
/*     */   public TypeBinding genericCast;
/*     */ 
/*     */   public FieldReference(char[] source, long pos)
/*     */   {
/*  50 */     this.token = source;
/*  51 */     this.nameSourcePosition = pos;
/*     */ 
/*  53 */     this.sourceStart = (int)(pos >>> 32);
/*  54 */     this.sourceEnd = (int)(pos & 0xFFFFFFFF);
/*  55 */     this.bits |= 1;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Assignment assignment, boolean isCompound)
/*     */   {
/*  61 */     if (isCompound) {
/*  62 */       if ((this.binding.isBlankFinal()) && 
/*  63 */         (this.receiver.isThis()) && 
/*  64 */         (currentScope.needBlankFinalFieldInitializationCheck(this.binding))) {
/*  65 */         FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(this.binding.declaringClass.original(), flowInfo);
/*  66 */         if (!fieldInits.isDefinitelyAssigned(this.binding)) {
/*  67 */           currentScope.problemReporter().uninitializedBlankFinalField(this.binding, this);
/*     */         }
/*     */       }
/*     */ 
/*  71 */       manageSyntheticAccessIfNecessary(currentScope, flowInfo, true);
/*     */     }
/*  73 */     flowInfo = 
/*  74 */       this.receiver
/*  75 */       .analyseCode(currentScope, flowContext, flowInfo, !this.binding.isStatic())
/*  76 */       .unconditionalInits();
/*  77 */     if (assignment.expression != null) {
/*  78 */       flowInfo = 
/*  79 */         assignment.expression
/*  81 */         .analyseCode(currentScope, flowContext, flowInfo)
/*  82 */         .unconditionalInits();
/*     */     }
/*  84 */     manageSyntheticAccessIfNecessary(currentScope, flowInfo, false);
/*     */ 
/*  87 */     if (this.binding.isFinal())
/*     */     {
/*  89 */       if ((this.binding.isBlankFinal()) && 
/*  90 */         (!isCompound) && 
/*  91 */         (this.receiver.isThis()) && 
/*  92 */         (!(this.receiver instanceof QualifiedThisReference)) && 
/*  93 */         ((this.receiver.bits & 0x1FE00000) == 0) && 
/*  94 */         (currentScope.allowBlankFinalFieldAssignment(this.binding))) {
/*  95 */         if (flowInfo.isPotentiallyAssigned(this.binding))
/*  96 */           currentScope.problemReporter().duplicateInitializationOfBlankFinalField(
/*  97 */             this.binding, 
/*  98 */             this);
/*     */         else {
/* 100 */           flowContext.recordSettingFinal(this.binding, this, flowInfo);
/*     */         }
/* 102 */         flowInfo.markAsDefinitelyAssigned(this.binding);
/*     */       }
/*     */       else {
/* 105 */         currentScope.problemReporter().cannotAssignToFinalField(this.binding, this);
/*     */       }
/*     */     }
/* 108 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/* 112 */     return analyseCode(currentScope, flowContext, flowInfo, true);
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, boolean valueRequired) {
/* 116 */     boolean nonStatic = !this.binding.isStatic();
/* 117 */     this.receiver.analyseCode(currentScope, flowContext, flowInfo, nonStatic);
/* 118 */     if (nonStatic) {
/* 119 */       this.receiver.checkNPE(currentScope, flowContext, flowInfo);
/*     */     }
/*     */ 
/* 122 */     if ((valueRequired) || (currentScope.compilerOptions().complianceLevel >= 3145728L)) {
/* 123 */       manageSyntheticAccessIfNecessary(currentScope, flowInfo, true);
/*     */     }
/* 125 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void computeConversion(Scope scope, TypeBinding runtimeTimeType, TypeBinding compileTimeType)
/*     */   {
/* 132 */     if ((runtimeTimeType == null) || (compileTimeType == null)) {
/* 133 */       return;
/*     */     }
/* 135 */     if ((this.binding != null) && (this.binding.isValidBinding())) {
/* 136 */       FieldBinding originalBinding = this.binding.original();
/* 137 */       TypeBinding originalType = originalBinding.type;
/*     */ 
/* 139 */       if (originalType.leafComponentType().isTypeVariable()) {
/* 140 */         TypeBinding targetType = (!compileTimeType.isBaseType()) && (runtimeTimeType.isBaseType()) ? 
/* 141 */           compileTimeType : 
/* 142 */           runtimeTimeType;
/* 143 */         this.genericCast = originalBinding.type.genericCast(targetType);
/* 144 */         if ((this.genericCast instanceof ReferenceBinding)) {
/* 145 */           ReferenceBinding referenceCast = (ReferenceBinding)this.genericCast;
/* 146 */           if (!referenceCast.canBeSeenBy(scope)) {
/* 147 */             scope.problemReporter().invalidType(this, 
/* 148 */               new ProblemReferenceBinding(
/* 149 */               CharOperation.splitOn('.', referenceCast.shortReadableName()), 
/* 150 */               referenceCast, 
/* 151 */               2));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 156 */     super.computeConversion(scope, runtimeTimeType, compileTimeType);
/*     */   }
/*     */ 
/*     */   public FieldBinding fieldBinding() {
/* 160 */     return this.binding;
/*     */   }
/*     */ 
/*     */   public void generateAssignment(BlockScope currentScope, CodeStream codeStream, Assignment assignment, boolean valueRequired) {
/* 164 */     int pc = codeStream.position;
/* 165 */     FieldBinding codegenBinding = this.binding.original();
/* 166 */     this.receiver.generateCode(currentScope, codeStream, !codegenBinding.isStatic());
/* 167 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 168 */     assignment.expression.generateCode(currentScope, codeStream, true);
/* 169 */     fieldStore(currentScope, codeStream, codegenBinding, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], this.actualReceiverType, this.receiver.isImplicitThis(), valueRequired);
/* 170 */     if (valueRequired)
/* 171 */       codeStream.generateImplicitConversion(assignment.implicitConversion);
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 184 */     int pc = codeStream.position;
/* 185 */     if (this.constant != Constant.NotAConstant) {
/* 186 */       if (valueRequired) {
/* 187 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/*     */       }
/* 189 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 190 */       return;
/*     */     }
/* 192 */     FieldBinding codegenBinding = this.binding.original();
/* 193 */     boolean isStatic = codegenBinding.isStatic();
/* 194 */     boolean isThisReceiver = this.receiver instanceof ThisReference;
/* 195 */     Constant fieldConstant = codegenBinding.constant();
/* 196 */     if (fieldConstant != Constant.NotAConstant) {
/* 197 */       if (!isThisReceiver) {
/* 198 */         this.receiver.generateCode(currentScope, codeStream, !isStatic);
/* 199 */         if (!isStatic) {
/* 200 */           codeStream.invokeObjectGetClass();
/* 201 */           codeStream.pop();
/*     */         }
/*     */       }
/* 204 */       if (valueRequired) {
/* 205 */         codeStream.generateConstant(fieldConstant, this.implicitConversion);
/*     */       }
/* 207 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 208 */       return;
/*     */     }
/*     */     boolean isUnboxing;
/* 210 */     if ((valueRequired) || 
/* 211 */       ((!isThisReceiver) && (currentScope.compilerOptions().complianceLevel >= 3145728L)) || 
/* 212 */       ((this.implicitConversion & 0x400) != 0) || 
/* 213 */       (this.genericCast != null)) {
/* 214 */       this.receiver.generateCode(currentScope, codeStream, !isStatic);
/* 215 */       if ((this.bits & 0x40000) != 0) {
/* 216 */         codeStream.checkcast(this.actualReceiverType);
/*     */       }
/* 218 */       pc = codeStream.position;
/* 219 */       if (codegenBinding.declaringClass == null) {
/* 220 */         codeStream.arraylength();
/* 221 */         if (valueRequired) {
/* 222 */           codeStream.generateImplicitConversion(this.implicitConversion); break label638;
/*     */         }
/*     */ 
/* 225 */         codeStream.pop(); break label638;
/*     */       }
/*     */ 
/* 228 */       if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 229 */         TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
/* 230 */         if (isStatic)
/* 231 */           codeStream.fieldAccess(-78, codegenBinding, constantPoolDeclaringClass);
/*     */         else
/* 233 */           codeStream.fieldAccess(-76, codegenBinding, constantPoolDeclaringClass);
/*     */       }
/*     */       else {
/* 236 */         codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */       }
/*     */ 
/* 239 */       if (this.genericCast != null) codeStream.checkcast(this.genericCast);
/* 240 */       if (valueRequired) {
/* 241 */         codeStream.generateImplicitConversion(this.implicitConversion); break label638;
/*     */       }
/* 243 */       isUnboxing = (this.implicitConversion & 0x400) != 0;
/*     */ 
/* 245 */       if (isUnboxing) codeStream.generateImplicitConversion(this.implicitConversion); 
/*     */     }
/* 246 */     switch (isUnboxing ? postConversionType(currentScope).id : codegenBinding.type.id) {
/*     */     case 7:
/*     */     case 8:
/* 249 */       codeStream.pop2();
/* 250 */       break;
/*     */     default:
/* 252 */       codeStream.pop(); break;
/*     */ 
/* 257 */       if (isThisReceiver) {
/* 258 */         if (!isStatic)
/*     */           break;
/* 260 */         if (this.binding.original().declaringClass == this.actualReceiverType.erasure()) break;
/* 261 */         MethodBinding accessor = this.syntheticAccessors == null ? null : this.syntheticAccessors[0];
/* 262 */         if (accessor == null) {
/* 263 */           TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
/* 264 */           codeStream.fieldAccess(-78, codegenBinding, constantPoolDeclaringClass);
/*     */         } else {
/* 266 */           codeStream.invoke(-72, accessor, null);
/*     */         }
/* 268 */         switch (codegenBinding.type.id) {
/*     */         case 7:
/*     */         case 8:
/* 271 */           codeStream.pop2();
/* 272 */           break;
/*     */         default:
/* 274 */           codeStream.pop(); break;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 279 */         this.receiver.generateCode(currentScope, codeStream, !isStatic);
/* 280 */         if (isStatic) break;
/* 281 */         codeStream.invokeObjectGetClass();
/* 282 */         codeStream.pop();
/*     */       }
/*     */     }
/*     */ 
/* 286 */     label638: codeStream.recordPositionsFrom(pc, this.sourceEnd);
/*     */   }
/*     */ 
/*     */   public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired)
/*     */   {
/* 291 */     FieldBinding codegenBinding = this.binding.original();
/*     */     boolean isStatic;
/* 292 */     this.receiver.generateCode(currentScope, codeStream, !(isStatic = codegenBinding.isStatic()));
/* 293 */     if (isStatic) {
/* 294 */       if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 295 */         TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
/* 296 */         codeStream.fieldAccess(-78, codegenBinding, constantPoolDeclaringClass);
/*     */       } else {
/* 298 */         codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */       }
/*     */     } else {
/* 301 */       codeStream.dup();
/* 302 */       if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 303 */         TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
/* 304 */         codeStream.fieldAccess(-76, codegenBinding, constantPoolDeclaringClass);
/*     */       } else {
/* 306 */         codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */       }
/*     */     }
/*     */     int operationTypeID;
/* 310 */     switch (operationTypeID = (this.implicitConversion & 0xFF) >> 4) {
/*     */     case 0:
/*     */     case 1:
/*     */     case 11:
/* 314 */       codeStream.generateStringConcatenationAppend(currentScope, null, expression);
/* 315 */       break;
/*     */     default:
/* 317 */       if (this.genericCast != null) {
/* 318 */         codeStream.checkcast(this.genericCast);
/*     */       }
/* 320 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */ 
/* 322 */       if (expression == IntLiteral.One)
/* 323 */         codeStream.generateConstant(expression.constant, this.implicitConversion);
/*     */       else {
/* 325 */         expression.generateCode(currentScope, codeStream, true);
/*     */       }
/*     */ 
/* 328 */       codeStream.sendOperator(operator, operationTypeID);
/*     */ 
/* 330 */       codeStream.generateImplicitConversion(assignmentImplicitConversion);
/*     */     }
/* 332 */     fieldStore(currentScope, codeStream, codegenBinding, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], this.actualReceiverType, this.receiver.isImplicitThis(), valueRequired);
/*     */   }
/*     */ 
/*     */   public void generatePostIncrement(BlockScope currentScope, CodeStream codeStream, CompoundAssignment postIncrement, boolean valueRequired)
/*     */   {
/* 338 */     FieldBinding codegenBinding = this.binding.original();
/*     */     boolean isStatic;
/* 339 */     this.receiver.generateCode(currentScope, codeStream, !(isStatic = codegenBinding.isStatic()));
/* 340 */     if (isStatic) {
/* 341 */       if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 342 */         TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
/* 343 */         codeStream.fieldAccess(-78, codegenBinding, constantPoolDeclaringClass);
/*     */       } else {
/* 345 */         codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */       }
/*     */     } else {
/* 348 */       codeStream.dup();
/* 349 */       if ((this.syntheticAccessors == null) || (this.syntheticAccessors[0] == null)) {
/* 350 */         TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
/* 351 */         codeStream.fieldAccess(-76, codegenBinding, constantPoolDeclaringClass);
/*     */       } else {
/* 353 */         codeStream.invoke(-72, this.syntheticAccessors[0], null);
/*     */       }
/*     */     }
/*     */     TypeBinding operandType;
/*     */     TypeBinding operandType;
/* 357 */     if (this.genericCast != null) {
/* 358 */       codeStream.checkcast(this.genericCast);
/* 359 */       operandType = this.genericCast;
/*     */     } else {
/* 361 */       operandType = codegenBinding.type;
/*     */     }
/* 363 */     if (valueRequired) {
/* 364 */       if (isStatic)
/* 365 */         switch (operandType.id) {
/*     */         case 7:
/*     */         case 8:
/* 368 */           codeStream.dup2();
/* 369 */           break;
/*     */         default:
/* 371 */           codeStream.dup();
/* 372 */           break;
/*     */         }
/*     */       else {
/* 375 */         switch (operandType.id) {
/*     */         case 7:
/*     */         case 8:
/* 378 */           codeStream.dup2_x1();
/* 379 */           break;
/*     */         default:
/* 381 */           codeStream.dup_x1();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 386 */     codeStream.generateImplicitConversion(this.implicitConversion);
/* 387 */     codeStream.generateConstant(
/* 388 */       postIncrement.expression.constant, 
/* 389 */       this.implicitConversion);
/* 390 */     codeStream.sendOperator(postIncrement.operator, this.implicitConversion & 0xF);
/* 391 */     codeStream.generateImplicitConversion(
/* 392 */       postIncrement.preAssignImplicitConversion);
/* 393 */     fieldStore(currentScope, codeStream, codegenBinding, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], this.actualReceiverType, this.receiver.isImplicitThis(), false);
/*     */   }
/*     */ 
/*     */   public TypeBinding[] genericTypeArguments()
/*     */   {
/* 400 */     return null;
/*     */   }
/*     */   public boolean isSuperAccess() {
/* 403 */     return this.receiver.isSuper();
/*     */   }
/*     */ 
/*     */   public boolean isTypeAccess() {
/* 407 */     return (this.receiver != null) && (this.receiver.isTypeReference());
/*     */   }
/*     */ 
/*     */   public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo, boolean isReadAccess)
/*     */   {
/* 414 */     if ((flowInfo.tagBits & 0x1) != 0) return;
/*     */ 
/* 417 */     FieldBinding codegenBinding = this.binding.original();
/* 418 */     if (this.binding.isPrivate()) {
/* 419 */       if ((currentScope.enclosingSourceType() != codegenBinding.declaringClass) && 
/* 420 */         (this.binding.constant() == Constant.NotAConstant)) {
/* 421 */         if (this.syntheticAccessors == null)
/* 422 */           this.syntheticAccessors = new MethodBinding[2];
/* 423 */         this.syntheticAccessors[(isReadAccess ? 0 : 1)] = 
/* 424 */           ((SourceTypeBinding)codegenBinding.declaringClass).addSyntheticMethod(codegenBinding, isReadAccess, false);
/* 425 */         currentScope.problemReporter().needToEmulateFieldAccess(codegenBinding, this, isReadAccess);
/* 426 */         return;
/*     */       }
/*     */     } else {
/* 428 */       if ((this.receiver instanceof QualifiedSuperReference))
/*     */       {
/* 430 */         SourceTypeBinding destinationType = (SourceTypeBinding)((QualifiedSuperReference)this.receiver).currentCompatibleType;
/* 431 */         if (this.syntheticAccessors == null)
/* 432 */           this.syntheticAccessors = new MethodBinding[2];
/* 433 */         this.syntheticAccessors[(isReadAccess ? 0 : 1)] = destinationType.addSyntheticMethod(codegenBinding, isReadAccess, isSuperAccess());
/* 434 */         currentScope.problemReporter().needToEmulateFieldAccess(codegenBinding, this, isReadAccess);
/* 435 */         return;
/*     */       }
/* 437 */       if (this.binding.isProtected())
/*     */       {
/*     */         SourceTypeBinding enclosingSourceType;
/* 439 */         if (((this.bits & 0x1FE0) != 0) && 
/* 440 */           (this.binding.declaringClass.getPackage() != 
/* 441 */           (enclosingSourceType = currentScope.enclosingSourceType()).getPackage()))
/*     */         {
/* 443 */           SourceTypeBinding currentCompatibleType = 
/* 444 */             (SourceTypeBinding)enclosingSourceType.enclosingTypeAt(
/* 445 */             (this.bits & 0x1FE0) >> 5);
/* 446 */           if (this.syntheticAccessors == null)
/* 447 */             this.syntheticAccessors = new MethodBinding[2];
/* 448 */           this.syntheticAccessors[(isReadAccess ? 0 : 1)] = currentCompatibleType.addSyntheticMethod(codegenBinding, isReadAccess, isSuperAccess());
/* 449 */           currentScope.problemReporter().needToEmulateFieldAccess(codegenBinding, this, isReadAccess);
/* 450 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int nullStatus(FlowInfo flowInfo) {
/* 456 */     return 0;
/*     */   }
/*     */ 
/*     */   public Constant optimizedBooleanConstant() {
/* 460 */     switch (this.resolvedType.id) {
/*     */     case 5:
/*     */     case 33:
/* 463 */       return this.constant != Constant.NotAConstant ? this.constant : this.binding.constant();
/*     */     }
/* 465 */     return Constant.NotAConstant;
/*     */   }
/*     */ 
/*     */   public TypeBinding postConversionType(Scope scope)
/*     */   {
/* 473 */     TypeBinding convertedType = this.resolvedType;
/* 474 */     if (this.genericCast != null)
/* 475 */       convertedType = this.genericCast;
/* 476 */     int runtimeType = (this.implicitConversion & 0xFF) >> 4;
/* 477 */     switch (runtimeType) {
/*     */     case 5:
/* 479 */       convertedType = TypeBinding.BOOLEAN;
/* 480 */       break;
/*     */     case 3:
/* 482 */       convertedType = TypeBinding.BYTE;
/* 483 */       break;
/*     */     case 4:
/* 485 */       convertedType = TypeBinding.SHORT;
/* 486 */       break;
/*     */     case 2:
/* 488 */       convertedType = TypeBinding.CHAR;
/* 489 */       break;
/*     */     case 10:
/* 491 */       convertedType = TypeBinding.INT;
/* 492 */       break;
/*     */     case 9:
/* 494 */       convertedType = TypeBinding.FLOAT;
/* 495 */       break;
/*     */     case 7:
/* 497 */       convertedType = TypeBinding.LONG;
/* 498 */       break;
/*     */     case 8:
/* 500 */       convertedType = TypeBinding.DOUBLE;
/*     */     case 6:
/*     */     }
/*     */ 
/* 504 */     if ((this.implicitConversion & 0x200) != 0) {
/* 505 */       convertedType = scope.environment().computeBoxingType(convertedType);
/*     */     }
/* 507 */     return convertedType;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 511 */     return this.receiver.printExpression(0, output).append('.').append(this.token);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 520 */     boolean receiverCast = false;
/* 521 */     if ((this.receiver instanceof CastExpression)) {
/* 522 */       this.receiver.bits |= 32;
/* 523 */       receiverCast = true;
/*     */     }
/* 525 */     this.actualReceiverType = this.receiver.resolveType(scope);
/* 526 */     if (this.actualReceiverType == null) {
/* 527 */       this.constant = Constant.NotAConstant;
/* 528 */       return null;
/*     */     }
/* 530 */     if (receiverCast)
/*     */     {
/* 532 */       if (((CastExpression)this.receiver).expression.resolvedType == this.actualReceiverType) {
/* 533 */         scope.problemReporter().unnecessaryCast((CastExpression)this.receiver);
/*     */       }
/*     */     }
/*     */ 
/* 537 */     FieldBinding fieldBinding = this.binding = scope.getField(this.actualReceiverType, this.token, this);
/* 538 */     if (!fieldBinding.isValidBinding()) {
/* 539 */       this.constant = Constant.NotAConstant;
/* 540 */       if ((this.receiver.resolvedType instanceof ProblemReferenceBinding))
/*     */       {
/* 542 */         return null;
/*     */       }
/* 544 */       scope.problemReporter().invalidField(this, this.actualReceiverType);
/* 545 */       return null;
/*     */     }
/*     */ 
/* 549 */     TypeBinding oldReceiverType = this.actualReceiverType;
/* 550 */     this.actualReceiverType = this.actualReceiverType.getErasureCompatibleType(fieldBinding.declaringClass);
/* 551 */     this.receiver.computeConversion(scope, this.actualReceiverType, this.actualReceiverType);
/* 552 */     if ((this.actualReceiverType != oldReceiverType) && (this.receiver.postConversionType(scope) != this.actualReceiverType)) {
/* 553 */       this.bits |= 262144;
/*     */     }
/* 555 */     if (isFieldUseDeprecated(fieldBinding, scope, (this.bits & 0x2000) != 0)) {
/* 556 */       scope.problemReporter().deprecatedField(fieldBinding, this);
/*     */     }
/* 558 */     boolean isImplicitThisRcv = this.receiver.isImplicitThis();
/* 559 */     this.constant = (isImplicitThisRcv ? fieldBinding.constant() : Constant.NotAConstant);
/* 560 */     if (fieldBinding.isStatic())
/*     */     {
/* 562 */       if ((!isImplicitThisRcv) && (
/* 563 */         (!(this.receiver instanceof NameReference)) || 
/* 564 */         ((((NameReference)this.receiver).bits & 0x4) == 0))) {
/* 565 */         scope.problemReporter().nonStaticAccessToStaticField(this, fieldBinding);
/*     */       }
/* 567 */       ReferenceBinding declaringClass = this.binding.declaringClass;
/* 568 */       if ((!isImplicitThisRcv) && 
/* 569 */         (declaringClass != this.actualReceiverType) && 
/* 570 */         (declaringClass.canBeSeenBy(scope))) {
/* 571 */         scope.problemReporter().indirectAccessToStaticField(this, fieldBinding);
/*     */       }
/*     */ 
/* 574 */       if (declaringClass.isEnum()) {
/* 575 */         MethodScope methodScope = scope.methodScope();
/* 576 */         SourceTypeBinding sourceType = scope.enclosingSourceType();
/* 577 */         if ((this.constant == Constant.NotAConstant) && 
/* 578 */           (!methodScope.isStatic) && 
/* 579 */           ((sourceType == declaringClass) || (sourceType.superclass == declaringClass)) && 
/* 580 */           (methodScope.isInsideInitializerOrConstructor())) {
/* 581 */           scope.problemReporter().enumStaticFieldUsedDuringInitialization(this.binding, this);
/*     */         }
/*     */       }
/*     */     }
/* 585 */     TypeBinding fieldType = fieldBinding.type;
/* 586 */     if (fieldType != null) {
/* 587 */       if ((this.bits & 0x2000) == 0) {
/* 588 */         fieldType = fieldType.capture(scope, this.sourceEnd);
/*     */       }
/* 590 */       this.resolvedType = fieldType;
/* 591 */       if ((fieldType.tagBits & 0x80) != 0L) {
/* 592 */         scope.problemReporter().invalidType(this, fieldType);
/* 593 */         return null;
/*     */       }
/*     */     }
/* 596 */     return fieldType;
/*     */   }
/*     */ 
/*     */   public void setActualReceiverType(ReferenceBinding receiverType) {
/* 600 */     this.actualReceiverType = receiverType;
/*     */   }
/*     */ 
/*     */   public void setDepth(int depth) {
/* 604 */     this.bits &= -8161;
/* 605 */     if (depth > 0)
/* 606 */       this.bits |= (depth & 0xFF) << 5;
/*     */   }
/*     */ 
/*     */   public void setFieldIndex(int index)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 615 */     if (visitor.visit(this, scope)) {
/* 616 */       this.receiver.traverse(visitor, scope);
/*     */     }
/* 618 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.FieldReference
 * JD-Core Version:    0.6.0
 */