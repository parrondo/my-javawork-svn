/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ConditionalExpression extends OperatorExpression
/*     */ {
/*     */   public Expression condition;
/*     */   public Expression valueIfTrue;
/*     */   public Expression valueIfFalse;
/*     */   public Constant optimizedBooleanConstant;
/*     */   public Constant optimizedIfTrueConstant;
/*     */   public Constant optimizedIfFalseConstant;
/*  28 */   int trueInitStateIndex = -1;
/*  29 */   int falseInitStateIndex = -1;
/*  30 */   int mergedInitStateIndex = -1;
/*     */ 
/*     */   public ConditionalExpression(Expression condition, Expression valueIfTrue, Expression valueIfFalse)
/*     */   {
/*  36 */     this.condition = condition;
/*  37 */     this.valueIfTrue = valueIfTrue;
/*  38 */     this.valueIfFalse = valueIfFalse;
/*  39 */     this.sourceStart = condition.sourceStart;
/*  40 */     this.sourceEnd = valueIfFalse.sourceEnd;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  45 */     Constant cst = this.condition.optimizedBooleanConstant();
/*  46 */     boolean isConditionOptimizedTrue = (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  47 */     boolean isConditionOptimizedFalse = (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  49 */     int mode = flowInfo.reachMode();
/*  50 */     flowInfo = this.condition.analyseCode(currentScope, flowContext, flowInfo, cst == Constant.NotAConstant);
/*     */ 
/*  53 */     FlowInfo trueFlowInfo = flowInfo.initsWhenTrue().copy();
/*  54 */     if ((isConditionOptimizedFalse) && 
/*  55 */       ((mode & 0x1) == 0)) {
/*  56 */       currentScope.problemReporter().fakeReachable(this.valueIfTrue);
/*  57 */       trueFlowInfo.setReachMode(1);
/*     */     }
/*     */ 
/*  60 */     this.trueInitStateIndex = currentScope.methodScope().recordInitializationStates(trueFlowInfo);
/*  61 */     trueFlowInfo = this.valueIfTrue.analyseCode(currentScope, flowContext, trueFlowInfo);
/*     */ 
/*  64 */     FlowInfo falseFlowInfo = flowInfo.initsWhenFalse().copy();
/*  65 */     if ((isConditionOptimizedTrue) && 
/*  66 */       ((mode & 0x1) == 0)) {
/*  67 */       currentScope.problemReporter().fakeReachable(this.valueIfFalse);
/*  68 */       falseFlowInfo.setReachMode(1);
/*     */     }
/*     */ 
/*  71 */     this.falseInitStateIndex = currentScope.methodScope().recordInitializationStates(falseFlowInfo);
/*  72 */     falseFlowInfo = this.valueIfFalse.analyseCode(currentScope, flowContext, falseFlowInfo);
/*     */     FlowInfo mergedInfo;
/*     */     FlowInfo mergedInfo;
/*  76 */     if (isConditionOptimizedTrue) {
/*  77 */       mergedInfo = trueFlowInfo.addPotentialInitializationsFrom(falseFlowInfo);
/*     */     }
/*     */     else
/*     */     {
/*     */       FlowInfo mergedInfo;
/*  78 */       if (isConditionOptimizedFalse) {
/*  79 */         mergedInfo = falseFlowInfo.addPotentialInitializationsFrom(trueFlowInfo);
/*     */       }
/*     */       else {
/*  82 */         cst = this.optimizedIfTrueConstant;
/*  83 */         boolean isValueIfTrueOptimizedTrue = (cst != null) && (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  84 */         boolean isValueIfTrueOptimizedFalse = (cst != null) && (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  86 */         cst = this.optimizedIfFalseConstant;
/*  87 */         boolean isValueIfFalseOptimizedTrue = (cst != null) && (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  88 */         boolean isValueIfFalseOptimizedFalse = (cst != null) && (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  90 */         UnconditionalFlowInfo trueInfoWhenTrue = trueFlowInfo.initsWhenTrue().unconditionalCopy();
/*  91 */         UnconditionalFlowInfo falseInfoWhenTrue = falseFlowInfo.initsWhenTrue().unconditionalCopy();
/*  92 */         UnconditionalFlowInfo trueInfoWhenFalse = trueFlowInfo.initsWhenFalse().unconditionalInits();
/*  93 */         UnconditionalFlowInfo falseInfoWhenFalse = falseFlowInfo.initsWhenFalse().unconditionalInits();
/*  94 */         if (isValueIfTrueOptimizedFalse) {
/*  95 */           trueInfoWhenTrue.setReachMode(1);
/*     */         }
/*  97 */         if (isValueIfFalseOptimizedFalse) {
/*  98 */           falseInfoWhenTrue.setReachMode(1);
/*     */         }
/* 100 */         if (isValueIfTrueOptimizedTrue) {
/* 101 */           trueInfoWhenFalse.setReachMode(1);
/*     */         }
/* 103 */         if (isValueIfFalseOptimizedTrue) {
/* 104 */           falseInfoWhenFalse.setReachMode(1);
/*     */         }
/* 106 */         mergedInfo = 
/* 107 */           FlowInfo.conditional(
/* 108 */           trueInfoWhenTrue.mergedWith(falseInfoWhenTrue), 
/* 109 */           trueInfoWhenFalse.mergedWith(falseInfoWhenFalse));
/*     */       }
/*     */     }
/* 111 */     this.mergedInitStateIndex = 
/* 112 */       currentScope.methodScope().recordInitializationStates(mergedInfo);
/* 113 */     mergedInfo.setReachMode(mode);
/* 114 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 129 */     int pc = codeStream.position;
/*     */ 
/* 131 */     if (this.constant != Constant.NotAConstant) {
/* 132 */       if (valueRequired)
/* 133 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/* 134 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 135 */       return;
/*     */     }
/* 137 */     Constant cst = this.condition.optimizedBooleanConstant();
/* 138 */     boolean needTruePart = (cst == Constant.NotAConstant) || (cst.booleanValue());
/* 139 */     boolean needFalsePart = (cst == Constant.NotAConstant) || (!cst.booleanValue());
/* 140 */     BranchLabel endifLabel = new BranchLabel(codeStream);
/*     */ 
/* 143 */     BranchLabel falseLabel = new BranchLabel(codeStream);
/* 144 */     falseLabel.tagBits |= 2;
/* 145 */     this.condition.generateOptimizedBoolean(
/* 146 */       currentScope, 
/* 147 */       codeStream, 
/* 148 */       null, 
/* 149 */       falseLabel, 
/* 150 */       cst == Constant.NotAConstant);
/*     */ 
/* 152 */     if (this.trueInitStateIndex != -1) {
/* 153 */       codeStream.removeNotDefinitelyAssignedVariables(
/* 154 */         currentScope, 
/* 155 */         this.trueInitStateIndex);
/* 156 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
/*     */     }
/*     */ 
/* 159 */     if (needTruePart) {
/* 160 */       this.valueIfTrue.generateCode(currentScope, codeStream, valueRequired);
/* 161 */       if (needFalsePart)
/*     */       {
/* 163 */         int position = codeStream.position;
/* 164 */         codeStream.goto_(endifLabel);
/* 165 */         codeStream.updateLastRecordedEndPC(currentScope, position);
/*     */ 
/* 167 */         if (valueRequired) {
/* 168 */           switch (this.resolvedType.id) {
/*     */           case 7:
/*     */           case 8:
/* 171 */             codeStream.decrStackSize(2);
/* 172 */             break;
/*     */           default:
/* 174 */             codeStream.decrStackSize(1);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 180 */     if (needFalsePart) {
/* 181 */       if (this.falseInitStateIndex != -1) {
/* 182 */         codeStream.removeNotDefinitelyAssignedVariables(
/* 183 */           currentScope, 
/* 184 */           this.falseInitStateIndex);
/* 185 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
/*     */       }
/* 187 */       if (falseLabel.forwardReferenceCount() > 0) {
/* 188 */         falseLabel.place();
/*     */       }
/* 190 */       this.valueIfFalse.generateCode(currentScope, codeStream, valueRequired);
/* 191 */       if (valueRequired) {
/* 192 */         codeStream.recordExpressionType(this.resolvedType);
/*     */       }
/* 194 */       if (needTruePart)
/*     */       {
/* 196 */         endifLabel.place();
/*     */       }
/*     */     }
/*     */ 
/* 200 */     if (this.mergedInitStateIndex != -1) {
/* 201 */       codeStream.removeNotDefinitelyAssignedVariables(
/* 202 */         currentScope, 
/* 203 */         this.mergedInitStateIndex);
/*     */     }
/*     */ 
/* 206 */     if (valueRequired)
/* 207 */       codeStream.generateImplicitConversion(this.implicitConversion);
/* 208 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*     */   {
/* 221 */     if (((this.constant != Constant.NotAConstant) && (this.constant.typeID() == 5)) || 
/* 222 */       ((this.valueIfTrue.implicitConversion & 0xFF) >> 4 != 5)) {
/* 223 */       super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
/* 224 */       return;
/*     */     }
/* 226 */     Constant cst = this.condition.constant;
/* 227 */     Constant condCst = this.condition.optimizedBooleanConstant();
/* 228 */     boolean needTruePart = 
/* 229 */       ((cst == Constant.NotAConstant) || (cst.booleanValue())) && (
/* 230 */       (condCst == Constant.NotAConstant) || (
/* 230 */       condCst.booleanValue()));
/* 231 */     boolean needFalsePart = 
/* 232 */       ((cst == Constant.NotAConstant) || (!cst.booleanValue())) && (
/* 233 */       (condCst == Constant.NotAConstant) || (!
/* 233 */       condCst.booleanValue()));
/*     */ 
/* 235 */     BranchLabel endifLabel = new BranchLabel(codeStream);
/*     */ 
/* 238 */     boolean needConditionValue = (cst == Constant.NotAConstant) && (condCst == Constant.NotAConstant);
/*     */     BranchLabel internalFalseLabel;
/* 239 */     this.condition.generateOptimizedBoolean(
/* 240 */       currentScope, 
/* 241 */       codeStream, 
/* 242 */       null, 
/* 243 */       internalFalseLabel = new BranchLabel(codeStream), 
/* 244 */       needConditionValue);
/*     */ 
/* 246 */     if (this.trueInitStateIndex != -1) {
/* 247 */       codeStream.removeNotDefinitelyAssignedVariables(
/* 248 */         currentScope, 
/* 249 */         this.trueInitStateIndex);
/* 250 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
/*     */     }
/*     */ 
/* 253 */     if (needTruePart) {
/* 254 */       this.valueIfTrue.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
/*     */ 
/* 256 */       if (needFalsePart)
/*     */       {
/* 259 */         if (falseLabel == null) {
/* 260 */           if (trueLabel != null)
/*     */           {
/* 262 */             cst = this.optimizedIfTrueConstant;
/* 263 */             boolean isValueIfTrueOptimizedTrue = (cst != null) && (cst != Constant.NotAConstant) && (cst.booleanValue());
/* 264 */             if (isValueIfTrueOptimizedTrue) break label368;
/*     */           }
/*     */ 
/*     */         }
/* 268 */         else if (trueLabel == null) {
/* 269 */           cst = this.optimizedIfTrueConstant;
/* 270 */           boolean isValueIfTrueOptimizedFalse = (cst != null) && (cst != Constant.NotAConstant) && (!cst.booleanValue());
/* 271 */           if (isValueIfTrueOptimizedFalse) {
/*     */             break label368;
/*     */           }
/*     */         }
/* 276 */         int position = codeStream.position;
/* 277 */         codeStream.goto_(endifLabel);
/* 278 */         codeStream.updateLastRecordedEndPC(currentScope, position);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 284 */     label368: if (needFalsePart) {
/* 285 */       internalFalseLabel.place();
/* 286 */       if (this.falseInitStateIndex != -1) {
/* 287 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
/* 288 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
/*     */       }
/* 290 */       this.valueIfFalse.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
/*     */ 
/* 293 */       endifLabel.place();
/*     */     }
/*     */ 
/* 296 */     if (this.mergedInitStateIndex != -1) {
/* 297 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */     }
/*     */ 
/* 300 */     codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */   }
/*     */ 
/*     */   public int nullStatus(FlowInfo flowInfo) {
/* 304 */     Constant cst = this.condition.optimizedBooleanConstant();
/* 305 */     if (cst != Constant.NotAConstant) {
/* 306 */       if (cst.booleanValue()) {
/* 307 */         return this.valueIfTrue.nullStatus(flowInfo);
/*     */       }
/* 309 */       return this.valueIfFalse.nullStatus(flowInfo);
/*     */     }
/* 311 */     int ifTrueNullStatus = this.valueIfTrue.nullStatus(flowInfo);
/* 312 */     int ifFalseNullStatus = this.valueIfFalse.nullStatus(flowInfo);
/* 313 */     if (ifTrueNullStatus == ifFalseNullStatus) {
/* 314 */       return ifTrueNullStatus;
/*     */     }
/* 316 */     return 0;
/*     */   }
/*     */ 
/*     */   public Constant optimizedBooleanConstant()
/*     */   {
/* 322 */     return this.optimizedBooleanConstant == null ? this.constant : this.optimizedBooleanConstant;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output)
/*     */   {
/* 327 */     this.condition.printExpression(indent, output).append(" ? ");
/* 328 */     this.valueIfTrue.printExpression(0, output).append(" : ");
/* 329 */     return this.valueIfFalse.printExpression(0, output);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 334 */     this.constant = Constant.NotAConstant;
/* 335 */     LookupEnvironment env = scope.environment();
/* 336 */     boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
/* 337 */     TypeBinding conditionType = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
/* 338 */     this.condition.computeConversion(scope, TypeBinding.BOOLEAN, conditionType);
/*     */ 
/* 340 */     if ((this.valueIfTrue instanceof CastExpression)) this.valueIfTrue.bits |= 32;
/* 341 */     TypeBinding originalValueIfTrueType = this.valueIfTrue.resolveType(scope);
/*     */ 
/* 343 */     if ((this.valueIfFalse instanceof CastExpression)) this.valueIfFalse.bits |= 32;
/* 344 */     TypeBinding originalValueIfFalseType = this.valueIfFalse.resolveType(scope);
/*     */ 
/* 346 */     if ((conditionType == null) || (originalValueIfTrueType == null) || (originalValueIfFalseType == null)) {
/* 347 */       return null;
/*     */     }
/* 349 */     TypeBinding valueIfTrueType = originalValueIfTrueType;
/* 350 */     TypeBinding valueIfFalseType = originalValueIfFalseType;
/* 351 */     if ((use15specifics) && (valueIfTrueType != valueIfFalseType))
/* 352 */       if (valueIfTrueType.isBaseType()) {
/* 353 */         if (valueIfFalseType.isBaseType())
/*     */         {
/* 355 */           if (valueIfTrueType == TypeBinding.NULL)
/* 356 */             valueIfFalseType = env.computeBoxingType(valueIfFalseType);
/* 357 */           else if (valueIfFalseType == TypeBinding.NULL)
/* 358 */             valueIfTrueType = env.computeBoxingType(valueIfTrueType);
/*     */         }
/*     */         else
/*     */         {
/* 362 */           TypeBinding unboxedIfFalseType = valueIfFalseType.isBaseType() ? valueIfFalseType : env.computeBoxingType(valueIfFalseType);
/* 363 */           if ((valueIfTrueType.isNumericType()) && (unboxedIfFalseType.isNumericType()))
/* 364 */             valueIfFalseType = unboxedIfFalseType;
/* 365 */           else if (valueIfTrueType != TypeBinding.NULL)
/* 366 */             valueIfFalseType = env.computeBoxingType(valueIfFalseType);
/*     */         }
/*     */       }
/* 369 */       else if (valueIfFalseType.isBaseType())
/*     */       {
/* 371 */         TypeBinding unboxedIfTrueType = valueIfTrueType.isBaseType() ? valueIfTrueType : env.computeBoxingType(valueIfTrueType);
/* 372 */         if ((unboxedIfTrueType.isNumericType()) && (valueIfFalseType.isNumericType()))
/* 373 */           valueIfTrueType = unboxedIfTrueType;
/* 374 */         else if (valueIfFalseType != TypeBinding.NULL)
/* 375 */           valueIfTrueType = env.computeBoxingType(valueIfTrueType);
/*     */       }
/*     */       else
/*     */       {
/* 379 */         TypeBinding unboxedIfTrueType = env.computeBoxingType(valueIfTrueType);
/* 380 */         TypeBinding unboxedIfFalseType = env.computeBoxingType(valueIfFalseType);
/* 381 */         if ((unboxedIfTrueType.isNumericType()) && (unboxedIfFalseType.isNumericType())) {
/* 382 */           valueIfTrueType = unboxedIfTrueType;
/* 383 */           valueIfFalseType = unboxedIfFalseType;
/*     */         }
/*     */       }
/*     */     Constant condConstant;
/*     */     Constant trueConstant;
/*     */     Constant falseConstant;
/* 389 */     if (((condConstant = this.condition.constant) != Constant.NotAConstant) && 
/* 390 */       ((trueConstant = this.valueIfTrue.constant) != Constant.NotAConstant) && 
/* 391 */       ((falseConstant = this.valueIfFalse.constant) != Constant.NotAConstant))
/*     */     {
/* 394 */       this.constant = (condConstant.booleanValue() ? trueConstant : falseConstant);
/*     */     }
/* 396 */     if (valueIfTrueType == valueIfFalseType) {
/* 397 */       this.valueIfTrue.computeConversion(scope, valueIfTrueType, originalValueIfTrueType);
/* 398 */       this.valueIfFalse.computeConversion(scope, valueIfFalseType, originalValueIfFalseType);
/* 399 */       if (valueIfTrueType == TypeBinding.BOOLEAN) {
/* 400 */         this.optimizedIfTrueConstant = this.valueIfTrue.optimizedBooleanConstant();
/* 401 */         this.optimizedIfFalseConstant = this.valueIfFalse.optimizedBooleanConstant();
/* 402 */         if ((this.optimizedIfTrueConstant != Constant.NotAConstant) && 
/* 403 */           (this.optimizedIfFalseConstant != Constant.NotAConstant) && 
/* 404 */           (this.optimizedIfTrueConstant.booleanValue() == this.optimizedIfFalseConstant.booleanValue()))
/*     */         {
/* 406 */           this.optimizedBooleanConstant = this.optimizedIfTrueConstant;
/* 407 */         } else if ((condConstant = this.condition.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 408 */           this.optimizedBooleanConstant = (condConstant.booleanValue() ? 
/* 409 */             this.optimizedIfTrueConstant : 
/* 410 */             this.optimizedIfFalseConstant);
/*     */         }
/*     */       }
/* 413 */       return this.resolvedType = valueIfTrueType;
/*     */     }
/*     */ 
/* 417 */     if ((valueIfTrueType.isNumericType()) && (valueIfFalseType.isNumericType()))
/*     */     {
/* 419 */       if (((valueIfTrueType == TypeBinding.BYTE) && (valueIfFalseType == TypeBinding.SHORT)) || (
/* 420 */         (valueIfTrueType == TypeBinding.SHORT) && (valueIfFalseType == TypeBinding.BYTE))) {
/* 421 */         this.valueIfTrue.computeConversion(scope, TypeBinding.SHORT, originalValueIfTrueType);
/* 422 */         this.valueIfFalse.computeConversion(scope, TypeBinding.SHORT, originalValueIfFalseType);
/* 423 */         return this.resolvedType = TypeBinding.SHORT;
/*     */       }
/*     */ 
/* 426 */       if (((valueIfTrueType == TypeBinding.BYTE) || (valueIfTrueType == TypeBinding.SHORT) || (valueIfTrueType == TypeBinding.CHAR)) && 
/* 427 */         (valueIfFalseType == TypeBinding.INT) && 
/* 428 */         (this.valueIfFalse.isConstantValueOfTypeAssignableToType(valueIfFalseType, valueIfTrueType))) {
/* 429 */         this.valueIfTrue.computeConversion(scope, valueIfTrueType, originalValueIfTrueType);
/* 430 */         this.valueIfFalse.computeConversion(scope, valueIfTrueType, originalValueIfFalseType);
/* 431 */         return this.resolvedType = valueIfTrueType;
/*     */       }
/* 433 */       if (((valueIfFalseType == TypeBinding.BYTE) || 
/* 434 */         (valueIfFalseType == TypeBinding.SHORT) || 
/* 435 */         (valueIfFalseType == TypeBinding.CHAR)) && 
/* 436 */         (valueIfTrueType == TypeBinding.INT) && 
/* 437 */         (this.valueIfTrue.isConstantValueOfTypeAssignableToType(valueIfTrueType, valueIfFalseType))) {
/* 438 */         this.valueIfTrue.computeConversion(scope, valueIfFalseType, originalValueIfTrueType);
/* 439 */         this.valueIfFalse.computeConversion(scope, valueIfFalseType, originalValueIfFalseType);
/* 440 */         return this.resolvedType = valueIfFalseType;
/*     */       }
/*     */ 
/* 444 */       if ((BaseTypeBinding.isNarrowing(valueIfTrueType.id, 10)) && 
/* 445 */         (BaseTypeBinding.isNarrowing(valueIfFalseType.id, 10))) {
/* 446 */         this.valueIfTrue.computeConversion(scope, TypeBinding.INT, originalValueIfTrueType);
/* 447 */         this.valueIfFalse.computeConversion(scope, TypeBinding.INT, originalValueIfFalseType);
/* 448 */         return this.resolvedType = TypeBinding.INT;
/*     */       }
/*     */ 
/* 451 */       if ((BaseTypeBinding.isNarrowing(valueIfTrueType.id, 7)) && 
/* 452 */         (BaseTypeBinding.isNarrowing(valueIfFalseType.id, 7))) {
/* 453 */         this.valueIfTrue.computeConversion(scope, TypeBinding.LONG, originalValueIfTrueType);
/* 454 */         this.valueIfFalse.computeConversion(scope, TypeBinding.LONG, originalValueIfFalseType);
/* 455 */         return this.resolvedType = TypeBinding.LONG;
/*     */       }
/*     */ 
/* 458 */       if ((BaseTypeBinding.isNarrowing(valueIfTrueType.id, 9)) && 
/* 459 */         (BaseTypeBinding.isNarrowing(valueIfFalseType.id, 9))) {
/* 460 */         this.valueIfTrue.computeConversion(scope, TypeBinding.FLOAT, originalValueIfTrueType);
/* 461 */         this.valueIfFalse.computeConversion(scope, TypeBinding.FLOAT, originalValueIfFalseType);
/* 462 */         return this.resolvedType = TypeBinding.FLOAT;
/*     */       }
/*     */ 
/* 465 */       this.valueIfTrue.computeConversion(scope, TypeBinding.DOUBLE, originalValueIfTrueType);
/* 466 */       this.valueIfFalse.computeConversion(scope, TypeBinding.DOUBLE, originalValueIfFalseType);
/* 467 */       return this.resolvedType = TypeBinding.DOUBLE;
/*     */     }
/*     */ 
/* 470 */     if ((valueIfTrueType.isBaseType()) && (valueIfTrueType != TypeBinding.NULL)) {
/* 471 */       if (use15specifics) {
/* 472 */         valueIfTrueType = env.computeBoxingType(valueIfTrueType);
/*     */       } else {
/* 474 */         scope.problemReporter().conditionalArgumentsIncompatibleTypes(this, valueIfTrueType, valueIfFalseType);
/* 475 */         return null;
/*     */       }
/*     */     }
/* 478 */     if ((valueIfFalseType.isBaseType()) && (valueIfFalseType != TypeBinding.NULL)) {
/* 479 */       if (use15specifics) {
/* 480 */         valueIfFalseType = env.computeBoxingType(valueIfFalseType);
/*     */       } else {
/* 482 */         scope.problemReporter().conditionalArgumentsIncompatibleTypes(this, valueIfTrueType, valueIfFalseType);
/* 483 */         return null;
/*     */       }
/*     */     }
/* 486 */     if (use15specifics)
/*     */     {
/* 488 */       TypeBinding commonType = null;
/* 489 */       if (valueIfTrueType == TypeBinding.NULL)
/* 490 */         commonType = valueIfFalseType;
/* 491 */       else if (valueIfFalseType == TypeBinding.NULL)
/* 492 */         commonType = valueIfTrueType;
/*     */       else {
/* 494 */         commonType = scope.lowerUpperBound(new TypeBinding[] { valueIfTrueType, valueIfFalseType });
/*     */       }
/* 496 */       if (commonType != null) {
/* 497 */         this.valueIfTrue.computeConversion(scope, commonType, originalValueIfTrueType);
/* 498 */         this.valueIfFalse.computeConversion(scope, commonType, originalValueIfFalseType);
/* 499 */         return this.resolvedType = commonType.capture(scope, this.sourceEnd);
/*     */       }
/*     */     }
/*     */     else {
/* 503 */       if (valueIfFalseType.isCompatibleWith(valueIfTrueType)) {
/* 504 */         this.valueIfTrue.computeConversion(scope, valueIfTrueType, originalValueIfTrueType);
/* 505 */         this.valueIfFalse.computeConversion(scope, valueIfTrueType, originalValueIfFalseType);
/* 506 */         return this.resolvedType = valueIfTrueType;
/* 507 */       }if (valueIfTrueType.isCompatibleWith(valueIfFalseType)) {
/* 508 */         this.valueIfTrue.computeConversion(scope, valueIfFalseType, originalValueIfTrueType);
/* 509 */         this.valueIfFalse.computeConversion(scope, valueIfFalseType, originalValueIfFalseType);
/* 510 */         return this.resolvedType = valueIfFalseType;
/*     */       }
/*     */     }
/* 513 */     scope.problemReporter().conditionalArgumentsIncompatibleTypes(
/* 514 */       this, 
/* 515 */       valueIfTrueType, 
/* 516 */       valueIfFalseType);
/* 517 */     return null;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 521 */     if (visitor.visit(this, scope)) {
/* 522 */       this.condition.traverse(visitor, scope);
/* 523 */       this.valueIfTrue.traverse(visitor, scope);
/* 524 */       this.valueIfFalse.traverse(visitor, scope);
/*     */     }
/* 526 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ConditionalExpression
 * JD-Core Version:    0.6.0
 */