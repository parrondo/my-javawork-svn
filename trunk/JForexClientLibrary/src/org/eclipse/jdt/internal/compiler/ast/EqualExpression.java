/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class EqualExpression extends BinaryExpression
/*     */ {
/*     */   public EqualExpression(Expression left, Expression right, int operator)
/*     */   {
/*  23 */     super(left, right, operator);
/*     */   }
/*     */ 
/*     */   private void checkNullComparison(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, FlowInfo initsWhenTrue, FlowInfo initsWhenFalse) {
/*  27 */     LocalVariableBinding local = this.left.localVariableBinding();
/*  28 */     if ((local != null) && ((local.type.tagBits & 0x2) == 0L)) {
/*  29 */       checkVariableComparison(scope, flowContext, flowInfo, initsWhenTrue, initsWhenFalse, local, this.right.nullStatus(flowInfo), this.left);
/*     */     }
/*  31 */     local = this.right.localVariableBinding();
/*  32 */     if ((local != null) && ((local.type.tagBits & 0x2) == 0L))
/*  33 */       checkVariableComparison(scope, flowContext, flowInfo, initsWhenTrue, initsWhenFalse, local, this.left.nullStatus(flowInfo), this.right);
/*     */   }
/*     */ 
/*     */   private void checkVariableComparison(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, FlowInfo initsWhenTrue, FlowInfo initsWhenFalse, LocalVariableBinding local, int nullStatus, Expression reference) {
/*  37 */     switch (nullStatus) {
/*     */     case 1:
/*  39 */       if ((this.bits & 0xFC0) >> 6 == 18) {
/*  40 */         flowContext.recordUsingNullReference(scope, local, reference, 
/*  41 */           256, flowInfo);
/*  42 */         initsWhenTrue.markAsComparedEqualToNull(local);
/*  43 */         initsWhenFalse.markAsComparedEqualToNonNull(local);
/*     */       } else {
/*  45 */         flowContext.recordUsingNullReference(scope, local, reference, 
/*  46 */           512, flowInfo);
/*  47 */         initsWhenTrue.markAsComparedEqualToNonNull(local);
/*  48 */         initsWhenFalse.markAsComparedEqualToNull(local);
/*     */       }
/*  50 */       break;
/*     */     case -1:
/*  52 */       if ((this.bits & 0xFC0) >> 6 == 18) {
/*  53 */         flowContext.recordUsingNullReference(scope, local, reference, 
/*  54 */           513, flowInfo);
/*  55 */         initsWhenTrue.markAsComparedEqualToNonNull(local);
/*     */       } else {
/*  57 */         flowContext.recordUsingNullReference(scope, local, reference, 
/*  58 */           257, flowInfo);
/*     */       }
/*     */     case 0:
/*     */     }
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*     */     FlowInfo result;
/*     */     FlowInfo result;
/*  68 */     if ((this.bits & 0xFC0) >> 6 == 18)
/*     */     {
/*     */       FlowInfo result;
/*  69 */       if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == 5))
/*     */       {
/*     */         FlowInfo result;
/*  70 */         if (this.left.constant.booleanValue())
/*     */         {
/*  72 */           result = this.right.analyseCode(currentScope, flowContext, flowInfo);
/*     */         }
/*     */         else
/*  75 */           result = this.right.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
/*     */       }
/*     */       else
/*     */       {
/*     */         FlowInfo result;
/*  78 */         if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.typeID() == 5))
/*     */         {
/*     */           FlowInfo result;
/*  79 */           if (this.right.constant.booleanValue())
/*     */           {
/*  81 */             result = this.left.analyseCode(currentScope, flowContext, flowInfo);
/*     */           }
/*     */           else
/*  84 */             result = this.left.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
/*     */         }
/*     */         else
/*     */         {
/*  88 */           result = this.right.analyseCode(
/*  89 */             currentScope, flowContext, 
/*  90 */             this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits()).unconditionalInits();
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*     */       FlowInfo result;
/*  93 */       if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == 5))
/*     */       {
/*     */         FlowInfo result;
/*  94 */         if (!this.left.constant.booleanValue())
/*     */         {
/*  96 */           result = this.right.analyseCode(currentScope, flowContext, flowInfo);
/*     */         }
/*     */         else
/*  99 */           result = this.right.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
/*     */       }
/*     */       else
/*     */       {
/*     */         FlowInfo result;
/* 102 */         if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.typeID() == 5))
/*     */         {
/*     */           FlowInfo result;
/* 103 */           if (!this.right.constant.booleanValue())
/*     */           {
/* 105 */             result = this.left.analyseCode(currentScope, flowContext, flowInfo);
/*     */           }
/*     */           else
/* 108 */             result = this.left.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
/*     */         }
/*     */         else
/*     */         {
/* 112 */           result = this.right.analyseCode(
/* 113 */             currentScope, flowContext, 
/* 114 */             this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits())
/* 116 */             .unconditionalInits();
/*     */         }
/*     */       }
/*     */     }
/* 119 */     if (((result instanceof UnconditionalFlowInfo)) && 
/* 120 */       ((result.tagBits & 0x1) == 0)) {
/* 121 */       result = FlowInfo.conditional(result.copy(), result.copy());
/*     */     }
/*     */ 
/* 124 */     checkNullComparison(currentScope, flowContext, result, result.initsWhenTrue(), result.initsWhenFalse());
/* 125 */     return result;
/*     */   }
/*     */ 
/*     */   public final void computeConstant(TypeBinding leftType, TypeBinding rightType) {
/* 129 */     if ((this.left.constant != Constant.NotAConstant) && (this.right.constant != Constant.NotAConstant)) {
/* 130 */       this.constant = 
/* 131 */         Constant.computeConstantOperationEQUAL_EQUAL(
/* 132 */         this.left.constant, 
/* 133 */         leftType.id, 
/* 134 */         this.right.constant, 
/* 135 */         rightType.id);
/* 136 */       if ((this.bits & 0xFC0) >> 6 == 29)
/* 137 */         this.constant = BooleanConstant.fromValue(!this.constant.booleanValue());
/*     */     } else {
/* 139 */       this.constant = Constant.NotAConstant;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 152 */     int pc = codeStream.position;
/* 153 */     if (this.constant != Constant.NotAConstant) {
/* 154 */       if (valueRequired)
/* 155 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/* 156 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 157 */       return;
/*     */     }
/*     */ 
/* 160 */     if ((this.left.implicitConversion & 0xF) == 5)
/* 161 */       generateBooleanEqual(currentScope, codeStream, valueRequired);
/*     */     else {
/* 163 */       generateNonBooleanEqual(currentScope, codeStream, valueRequired);
/*     */     }
/* 165 */     if (valueRequired) {
/* 166 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */     }
/* 168 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*     */   {
/* 176 */     if (this.constant != Constant.NotAConstant) {
/* 177 */       super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
/* 178 */       return;
/*     */     }
/* 180 */     if ((this.bits & 0xFC0) >> 6 == 18) {
/* 181 */       if ((this.left.implicitConversion & 0xF) == 5)
/* 182 */         generateOptimizedBooleanEqual(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
/*     */       else {
/* 184 */         generateOptimizedNonBooleanEqual(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
/*     */       }
/*     */     }
/* 187 */     else if ((this.left.implicitConversion & 0xF) == 5)
/* 188 */       generateOptimizedBooleanEqual(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
/*     */     else
/* 190 */       generateOptimizedNonBooleanEqual(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
/*     */   }
/*     */ 
/*     */   public void generateBooleanEqual(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 204 */     boolean isEqualOperator = (this.bits & 0xFC0) >> 6 == 18;
/* 205 */     Constant cst = this.left.optimizedBooleanConstant();
/* 206 */     if (cst != Constant.NotAConstant) {
/* 207 */       Constant rightCst = this.right.optimizedBooleanConstant();
/* 208 */       if (rightCst != Constant.NotAConstant)
/*     */       {
/* 211 */         this.left.generateCode(currentScope, codeStream, false);
/* 212 */         this.right.generateCode(currentScope, codeStream, false);
/* 213 */         if (valueRequired) {
/* 214 */           boolean leftBool = cst.booleanValue();
/* 215 */           boolean rightBool = rightCst.booleanValue();
/* 216 */           if (isEqualOperator) {
/* 217 */             if (leftBool == rightBool)
/* 218 */               codeStream.iconst_1();
/*     */             else {
/* 220 */               codeStream.iconst_0();
/*     */             }
/*     */           }
/* 223 */           else if (leftBool != rightBool)
/* 224 */             codeStream.iconst_1();
/*     */           else {
/* 226 */             codeStream.iconst_0();
/*     */           }
/*     */         }
/*     */       }
/* 230 */       else if (cst.booleanValue() == isEqualOperator)
/*     */       {
/* 232 */         this.left.generateCode(currentScope, codeStream, false);
/* 233 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*     */       }
/* 236 */       else if (valueRequired) {
/* 237 */         BranchLabel falseLabel = new BranchLabel(codeStream);
/* 238 */         this.left.generateCode(currentScope, codeStream, false);
/* 239 */         this.right.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired);
/*     */ 
/* 241 */         codeStream.iconst_0();
/* 242 */         if ((this.bits & 0x10) != 0) {
/* 243 */           codeStream.generateImplicitConversion(this.implicitConversion);
/* 244 */           codeStream.generateReturnBytecode(this);
/*     */ 
/* 246 */           falseLabel.place();
/* 247 */           codeStream.iconst_1();
/*     */         } else {
/* 249 */           BranchLabel endLabel = new BranchLabel(codeStream);
/* 250 */           codeStream.goto_(endLabel);
/* 251 */           codeStream.decrStackSize(1);
/*     */ 
/* 253 */           falseLabel.place();
/* 254 */           codeStream.iconst_1();
/* 255 */           endLabel.place();
/*     */         }
/*     */       } else {
/* 258 */         this.left.generateCode(currentScope, codeStream, false);
/* 259 */         this.right.generateCode(currentScope, codeStream, false);
/*     */       }
/*     */ 
/* 268 */       return;
/*     */     }
/* 270 */     cst = this.right.optimizedBooleanConstant();
/* 271 */     if (cst != Constant.NotAConstant) {
/* 272 */       if (cst.booleanValue() == isEqualOperator)
/*     */       {
/* 274 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/* 275 */         this.right.generateCode(currentScope, codeStream, false);
/*     */       }
/* 278 */       else if (valueRequired) {
/* 279 */         BranchLabel falseLabel = new BranchLabel(codeStream);
/* 280 */         this.left.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired);
/* 281 */         this.right.generateCode(currentScope, codeStream, false);
/*     */ 
/* 283 */         codeStream.iconst_0();
/* 284 */         if ((this.bits & 0x10) != 0) {
/* 285 */           codeStream.generateImplicitConversion(this.implicitConversion);
/* 286 */           codeStream.generateReturnBytecode(this);
/*     */ 
/* 288 */           falseLabel.place();
/* 289 */           codeStream.iconst_1();
/*     */         } else {
/* 291 */           BranchLabel endLabel = new BranchLabel(codeStream);
/* 292 */           codeStream.goto_(endLabel);
/* 293 */           codeStream.decrStackSize(1);
/*     */ 
/* 295 */           falseLabel.place();
/* 296 */           codeStream.iconst_1();
/* 297 */           endLabel.place();
/*     */         }
/*     */       } else {
/* 300 */         this.left.generateCode(currentScope, codeStream, false);
/* 301 */         this.right.generateCode(currentScope, codeStream, false);
/*     */       }
/*     */ 
/* 310 */       return;
/*     */     }
/*     */ 
/* 313 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 314 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/*     */ 
/* 316 */     if (valueRequired)
/* 317 */       if (isEqualOperator)
/*     */       {
/*     */         BranchLabel falseLabel;
/* 319 */         codeStream.if_icmpne(falseLabel = new BranchLabel(codeStream));
/*     */ 
/* 321 */         codeStream.iconst_1();
/* 322 */         if ((this.bits & 0x10) != 0) {
/* 323 */           codeStream.generateImplicitConversion(this.implicitConversion);
/* 324 */           codeStream.generateReturnBytecode(this);
/*     */ 
/* 326 */           falseLabel.place();
/* 327 */           codeStream.iconst_0();
/*     */         } else {
/* 329 */           BranchLabel endLabel = new BranchLabel(codeStream);
/* 330 */           codeStream.goto_(endLabel);
/* 331 */           codeStream.decrStackSize(1);
/*     */ 
/* 333 */           falseLabel.place();
/* 334 */           codeStream.iconst_0();
/* 335 */           endLabel.place();
/*     */         }
/*     */       } else {
/* 338 */         codeStream.ixor();
/*     */       }
/*     */   }
/*     */ 
/*     */   public void generateOptimizedBooleanEqual(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*     */   {
/* 351 */     if (this.left.constant != Constant.NotAConstant) {
/* 352 */       boolean inline = this.left.constant.booleanValue();
/* 353 */       this.right.generateOptimizedBoolean(currentScope, codeStream, inline ? trueLabel : falseLabel, inline ? falseLabel : trueLabel, valueRequired);
/* 354 */       return;
/*     */     }
/* 356 */     if (this.right.constant != Constant.NotAConstant) {
/* 357 */       boolean inline = this.right.constant.booleanValue();
/* 358 */       this.left.generateOptimizedBoolean(currentScope, codeStream, inline ? trueLabel : falseLabel, inline ? falseLabel : trueLabel, valueRequired);
/* 359 */       return;
/*     */     }
/*     */ 
/* 362 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 363 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/* 364 */     if (valueRequired) {
/* 365 */       if (falseLabel == null) {
/* 366 */         if (trueLabel != null)
/*     */         {
/* 368 */           codeStream.if_icmpeq(trueLabel);
/*     */         }
/*     */ 
/*     */       }
/* 372 */       else if (trueLabel == null) {
/* 373 */         codeStream.if_icmpne(falseLabel);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 380 */     codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */   }
/*     */ 
/*     */   public void generateNonBooleanEqual(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 388 */     boolean isEqualOperator = (this.bits & 0xFC0) >> 6 == 18;
/* 389 */     if ((this.left.implicitConversion & 0xFF) >> 4 == 10)
/*     */     {
/*     */       Constant cst;
/* 391 */       if (((cst = this.left.constant) != Constant.NotAConstant) && (cst.intValue() == 0))
/*     */       {
/* 393 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/* 394 */         if (valueRequired) {
/* 395 */           BranchLabel falseLabel = new BranchLabel(codeStream);
/* 396 */           if (isEqualOperator)
/* 397 */             codeStream.ifne(falseLabel);
/*     */           else {
/* 399 */             codeStream.ifeq(falseLabel);
/*     */           }
/*     */ 
/* 402 */           codeStream.iconst_1();
/* 403 */           if ((this.bits & 0x10) != 0) {
/* 404 */             codeStream.generateImplicitConversion(this.implicitConversion);
/* 405 */             codeStream.generateReturnBytecode(this);
/*     */ 
/* 407 */             falseLabel.place();
/* 408 */             codeStream.iconst_0();
/*     */           } else {
/* 410 */             BranchLabel endLabel = new BranchLabel(codeStream);
/* 411 */             codeStream.goto_(endLabel);
/* 412 */             codeStream.decrStackSize(1);
/*     */ 
/* 414 */             falseLabel.place();
/* 415 */             codeStream.iconst_0();
/* 416 */             endLabel.place();
/*     */           }
/*     */         }
/* 419 */         return;
/*     */       }
/* 421 */       if (((cst = this.right.constant) != Constant.NotAConstant) && (cst.intValue() == 0))
/*     */       {
/* 423 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/* 424 */         if (valueRequired) {
/* 425 */           BranchLabel falseLabel = new BranchLabel(codeStream);
/* 426 */           if (isEqualOperator)
/* 427 */             codeStream.ifne(falseLabel);
/*     */           else {
/* 429 */             codeStream.ifeq(falseLabel);
/*     */           }
/*     */ 
/* 432 */           codeStream.iconst_1();
/* 433 */           if ((this.bits & 0x10) != 0) {
/* 434 */             codeStream.generateImplicitConversion(this.implicitConversion);
/* 435 */             codeStream.generateReturnBytecode(this);
/*     */ 
/* 437 */             falseLabel.place();
/* 438 */             codeStream.iconst_0();
/*     */           } else {
/* 440 */             BranchLabel endLabel = new BranchLabel(codeStream);
/* 441 */             codeStream.goto_(endLabel);
/* 442 */             codeStream.decrStackSize(1);
/*     */ 
/* 444 */             falseLabel.place();
/* 445 */             codeStream.iconst_0();
/* 446 */             endLabel.place();
/*     */           }
/*     */         }
/* 449 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 454 */     if ((this.right instanceof NullLiteral)) {
/* 455 */       if ((this.left instanceof NullLiteral))
/*     */       {
/* 457 */         if (valueRequired) {
/* 458 */           if (isEqualOperator)
/* 459 */             codeStream.iconst_1();
/*     */           else
/* 461 */             codeStream.iconst_0();
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 466 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/* 467 */         if (valueRequired) {
/* 468 */           BranchLabel falseLabel = new BranchLabel(codeStream);
/* 469 */           if (isEqualOperator)
/* 470 */             codeStream.ifnonnull(falseLabel);
/*     */           else {
/* 472 */             codeStream.ifnull(falseLabel);
/*     */           }
/*     */ 
/* 475 */           codeStream.iconst_1();
/* 476 */           if ((this.bits & 0x10) != 0) {
/* 477 */             codeStream.generateImplicitConversion(this.implicitConversion);
/* 478 */             codeStream.generateReturnBytecode(this);
/*     */ 
/* 480 */             falseLabel.place();
/* 481 */             codeStream.iconst_0();
/*     */           } else {
/* 483 */             BranchLabel endLabel = new BranchLabel(codeStream);
/* 484 */             codeStream.goto_(endLabel);
/* 485 */             codeStream.decrStackSize(1);
/*     */ 
/* 487 */             falseLabel.place();
/* 488 */             codeStream.iconst_0();
/* 489 */             endLabel.place();
/*     */           }
/*     */         }
/*     */       }
/* 493 */       return;
/* 494 */     }if ((this.left instanceof NullLiteral))
/*     */     {
/* 496 */       this.right.generateCode(currentScope, codeStream, valueRequired);
/* 497 */       if (valueRequired) {
/* 498 */         BranchLabel falseLabel = new BranchLabel(codeStream);
/* 499 */         if (isEqualOperator)
/* 500 */           codeStream.ifnonnull(falseLabel);
/*     */         else {
/* 502 */           codeStream.ifnull(falseLabel);
/*     */         }
/*     */ 
/* 505 */         codeStream.iconst_1();
/* 506 */         if ((this.bits & 0x10) != 0) {
/* 507 */           codeStream.generateImplicitConversion(this.implicitConversion);
/* 508 */           codeStream.generateReturnBytecode(this);
/*     */ 
/* 510 */           falseLabel.place();
/* 511 */           codeStream.iconst_0();
/*     */         } else {
/* 513 */           BranchLabel endLabel = new BranchLabel(codeStream);
/* 514 */           codeStream.goto_(endLabel);
/* 515 */           codeStream.decrStackSize(1);
/*     */ 
/* 517 */           falseLabel.place();
/* 518 */           codeStream.iconst_0();
/* 519 */           endLabel.place();
/*     */         }
/*     */       }
/* 522 */       return;
/*     */     }
/*     */ 
/* 526 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 527 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/* 528 */     if (valueRequired) {
/* 529 */       BranchLabel falseLabel = new BranchLabel(codeStream);
/* 530 */       if (isEqualOperator)
/* 531 */         switch ((this.left.implicitConversion & 0xFF) >> 4) {
/*     */         case 10:
/* 533 */           codeStream.if_icmpne(falseLabel);
/* 534 */           break;
/*     */         case 9:
/* 536 */           codeStream.fcmpl();
/* 537 */           codeStream.ifne(falseLabel);
/* 538 */           break;
/*     */         case 7:
/* 540 */           codeStream.lcmp();
/* 541 */           codeStream.ifne(falseLabel);
/* 542 */           break;
/*     */         case 8:
/* 544 */           codeStream.dcmpl();
/* 545 */           codeStream.ifne(falseLabel);
/* 546 */           break;
/*     */         default:
/* 548 */           codeStream.if_acmpne(falseLabel); break;
/*     */         }
/*     */       else {
/* 551 */         switch ((this.left.implicitConversion & 0xFF) >> 4) {
/*     */         case 10:
/* 553 */           codeStream.if_icmpeq(falseLabel);
/* 554 */           break;
/*     */         case 9:
/* 556 */           codeStream.fcmpl();
/* 557 */           codeStream.ifeq(falseLabel);
/* 558 */           break;
/*     */         case 7:
/* 560 */           codeStream.lcmp();
/* 561 */           codeStream.ifeq(falseLabel);
/* 562 */           break;
/*     */         case 8:
/* 564 */           codeStream.dcmpl();
/* 565 */           codeStream.ifeq(falseLabel);
/* 566 */           break;
/*     */         default:
/* 568 */           codeStream.if_acmpeq(falseLabel);
/*     */         }
/*     */       }
/*     */ 
/* 572 */       codeStream.iconst_1();
/* 573 */       if ((this.bits & 0x10) != 0) {
/* 574 */         codeStream.generateImplicitConversion(this.implicitConversion);
/* 575 */         codeStream.generateReturnBytecode(this);
/*     */ 
/* 577 */         falseLabel.place();
/* 578 */         codeStream.iconst_0();
/*     */       } else {
/* 580 */         BranchLabel endLabel = new BranchLabel(codeStream);
/* 581 */         codeStream.goto_(endLabel);
/* 582 */         codeStream.decrStackSize(1);
/*     */ 
/* 584 */         falseLabel.place();
/* 585 */         codeStream.iconst_0();
/* 586 */         endLabel.place();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateOptimizedNonBooleanEqual(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*     */   {
/* 597 */     int pc = codeStream.position;
/*     */     Constant inline;
/* 599 */     if ((inline = this.right.constant) != Constant.NotAConstant)
/*     */     {
/* 601 */       if (((this.left.implicitConversion & 0xFF) >> 4 == 10) && (inline.intValue() == 0)) {
/* 602 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/* 603 */         if (valueRequired) {
/* 604 */           if (falseLabel == null) {
/* 605 */             if (trueLabel != null)
/*     */             {
/* 607 */               codeStream.ifeq(trueLabel);
/*     */             }
/*     */ 
/*     */           }
/* 611 */           else if (trueLabel == null) {
/* 612 */             codeStream.ifne(falseLabel);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 618 */         codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 619 */         return;
/*     */       }
/*     */     }
/* 622 */     if ((inline = this.left.constant) != Constant.NotAConstant)
/*     */     {
/* 624 */       if (((this.left.implicitConversion & 0xFF) >> 4 == 10) && 
/* 625 */         (inline.intValue() == 0)) {
/* 626 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/* 627 */         if (valueRequired) {
/* 628 */           if (falseLabel == null) {
/* 629 */             if (trueLabel != null)
/*     */             {
/* 631 */               codeStream.ifeq(trueLabel);
/*     */             }
/*     */ 
/*     */           }
/* 635 */           else if (trueLabel == null) {
/* 636 */             codeStream.ifne(falseLabel);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 642 */         codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 643 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 648 */     if ((this.right instanceof NullLiteral)) {
/* 649 */       if ((this.left instanceof NullLiteral))
/*     */       {
/* 651 */         if ((valueRequired) && 
/* 652 */           (falseLabel == null))
/*     */         {
/* 654 */           if (trueLabel != null)
/* 655 */             codeStream.goto_(trueLabel);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 660 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/* 661 */         if (valueRequired) {
/* 662 */           if (falseLabel == null) {
/* 663 */             if (trueLabel != null)
/*     */             {
/* 665 */               codeStream.ifnull(trueLabel);
/*     */             }
/*     */ 
/*     */           }
/* 669 */           else if (trueLabel == null) {
/* 670 */             codeStream.ifnonnull(falseLabel);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 677 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 678 */       return;
/* 679 */     }if ((this.left instanceof NullLiteral)) {
/* 680 */       this.right.generateCode(currentScope, codeStream, valueRequired);
/* 681 */       if (valueRequired) {
/* 682 */         if (falseLabel == null) {
/* 683 */           if (trueLabel != null)
/*     */           {
/* 685 */             codeStream.ifnull(trueLabel);
/*     */           }
/*     */ 
/*     */         }
/* 689 */         else if (trueLabel == null) {
/* 690 */           codeStream.ifnonnull(falseLabel);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 696 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 697 */       return;
/*     */     }
/*     */ 
/* 701 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 702 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/* 703 */     if (valueRequired) {
/* 704 */       if (falseLabel == null) {
/* 705 */         if (trueLabel != null)
/*     */         {
/* 707 */           switch ((this.left.implicitConversion & 0xFF) >> 4) {
/*     */           case 10:
/* 709 */             codeStream.if_icmpeq(trueLabel);
/* 710 */             break;
/*     */           case 9:
/* 712 */             codeStream.fcmpl();
/* 713 */             codeStream.ifeq(trueLabel);
/* 714 */             break;
/*     */           case 7:
/* 716 */             codeStream.lcmp();
/* 717 */             codeStream.ifeq(trueLabel);
/* 718 */             break;
/*     */           case 8:
/* 720 */             codeStream.dcmpl();
/* 721 */             codeStream.ifeq(trueLabel);
/* 722 */             break;
/*     */           default:
/* 724 */             codeStream.if_acmpeq(trueLabel); break;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/* 729 */       else if (trueLabel == null) {
/* 730 */         switch ((this.left.implicitConversion & 0xFF) >> 4) {
/*     */         case 10:
/* 732 */           codeStream.if_icmpne(falseLabel);
/* 733 */           break;
/*     */         case 9:
/* 735 */           codeStream.fcmpl();
/* 736 */           codeStream.ifne(falseLabel);
/* 737 */           break;
/*     */         case 7:
/* 739 */           codeStream.lcmp();
/* 740 */           codeStream.ifne(falseLabel);
/* 741 */           break;
/*     */         case 8:
/* 743 */           codeStream.dcmpl();
/* 744 */           codeStream.ifne(falseLabel);
/* 745 */           break;
/*     */         default:
/* 747 */           codeStream.if_acmpne(falseLabel);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 754 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */   public boolean isCompactableOperation() {
/* 757 */     return false;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 762 */     boolean leftIsCast;
/* 762 */     if ((leftIsCast = this.left instanceof CastExpression)) this.left.bits |= 32;
/* 763 */     TypeBinding originalLeftType = this.left.resolveType(scope);
/*     */     boolean rightIsCast;
/* 765 */     if ((rightIsCast = this.right instanceof CastExpression)) this.right.bits |= 32;
/* 766 */     TypeBinding originalRightType = this.right.resolveType(scope);
/*     */ 
/* 769 */     if ((originalLeftType == null) || (originalRightType == null)) {
/* 770 */       this.constant = Constant.NotAConstant;
/* 771 */       return null;
/*     */     }
/*     */ 
/* 775 */     boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
/* 776 */     TypeBinding leftType = originalLeftType; TypeBinding rightType = originalRightType;
/* 777 */     if (use15specifics) {
/* 778 */       if ((leftType != TypeBinding.NULL) && (leftType.isBaseType())) {
/* 779 */         if (!rightType.isBaseType()) {
/* 780 */           rightType = scope.environment().computeBoxingType(rightType);
/*     */         }
/*     */       }
/* 783 */       else if ((rightType != TypeBinding.NULL) && (rightType.isBaseType())) {
/* 784 */         leftType = scope.environment().computeBoxingType(leftType);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 789 */     if ((leftType.isBaseType()) && (rightType.isBaseType())) {
/* 790 */       int leftTypeID = leftType.id;
/* 791 */       int rightTypeID = rightType.id;
/*     */ 
/* 797 */       int operatorSignature = OperatorSignatures[18][((leftTypeID << 4) + rightTypeID)];
/* 798 */       this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), originalLeftType);
/* 799 */       this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 8 & 0xF), originalRightType);
/* 800 */       this.bits |= operatorSignature & 0xF;
/* 801 */       if ((operatorSignature & 0xF) == 0) {
/* 802 */         this.constant = Constant.NotAConstant;
/* 803 */         scope.problemReporter().invalidOperator(this, leftType, rightType);
/* 804 */         return null;
/*     */       }
/*     */ 
/* 807 */       if ((leftIsCast) || (rightIsCast)) {
/* 808 */         CastExpression.checkNeedForArgumentCasts(scope, 18, operatorSignature, this.left, leftType.id, leftIsCast, this.right, rightType.id, rightIsCast);
/*     */       }
/* 810 */       computeConstant(leftType, rightType);
/*     */ 
/* 813 */       Binding leftDirect = Expression.getDirectBinding(this.left);
/* 814 */       if ((leftDirect != null) && (leftDirect == Expression.getDirectBinding(this.right)) && 
/* 815 */         (!(this.right instanceof Assignment))) {
/* 816 */         scope.problemReporter().comparingIdenticalExpressions(this);
/*     */       }
/* 818 */       return this.resolvedType = TypeBinding.BOOLEAN;
/*     */     }
/*     */ 
/* 823 */     if (((!leftType.isBaseType()) || (leftType == TypeBinding.NULL)) && 
/* 824 */       ((!rightType.isBaseType()) || (rightType == TypeBinding.NULL)) && (
/* 825 */       (checkCastTypesCompatibility(scope, leftType, rightType, null)) || 
/* 826 */       (checkCastTypesCompatibility(scope, rightType, leftType, null))))
/*     */     {
/* 829 */       if ((rightType.id == 11) && (leftType.id == 11))
/* 830 */         computeConstant(leftType, rightType);
/*     */       else {
/* 832 */         this.constant = Constant.NotAConstant;
/*     */       }
/* 834 */       TypeBinding objectType = scope.getJavaLangObject();
/* 835 */       this.left.computeConversion(scope, objectType, leftType);
/* 836 */       this.right.computeConversion(scope, objectType, rightType);
/*     */ 
/* 838 */       boolean unnecessaryLeftCast = (this.left.bits & 0x4000) != 0;
/* 839 */       boolean unnecessaryRightCast = (this.right.bits & 0x4000) != 0;
/* 840 */       if ((unnecessaryLeftCast) || (unnecessaryRightCast)) {
/* 841 */         TypeBinding alternateLeftType = unnecessaryLeftCast ? ((CastExpression)this.left).expression.resolvedType : leftType;
/* 842 */         TypeBinding alternateRightType = unnecessaryRightCast ? ((CastExpression)this.right).expression.resolvedType : rightType;
/* 843 */         if ((checkCastTypesCompatibility(scope, alternateLeftType, alternateRightType, null)) || 
/* 844 */           (checkCastTypesCompatibility(scope, alternateRightType, alternateLeftType, null))) {
/* 845 */           if (unnecessaryLeftCast) scope.problemReporter().unnecessaryCast((CastExpression)this.left);
/* 846 */           if (unnecessaryRightCast) scope.problemReporter().unnecessaryCast((CastExpression)this.right);
/*     */         }
/*     */       }
/*     */ 
/* 850 */       Binding leftDirect = Expression.getDirectBinding(this.left);
/* 851 */       if ((leftDirect != null) && (leftDirect == Expression.getDirectBinding(this.right)) && 
/* 852 */         (!(this.right instanceof Assignment))) {
/* 853 */         scope.problemReporter().comparingIdenticalExpressions(this);
/*     */       }
/*     */ 
/* 856 */       return this.resolvedType = TypeBinding.BOOLEAN;
/*     */     }
/* 858 */     this.constant = Constant.NotAConstant;
/* 859 */     scope.problemReporter().notCompatibleTypesError(this, leftType, rightType);
/* 860 */     return null;
/*     */   }
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 863 */     if (visitor.visit(this, scope)) {
/* 864 */       this.left.traverse(visitor, scope);
/* 865 */       this.right.traverse(visitor, scope);
/*     */     }
/* 867 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.EqualExpression
 * JD-Core Version:    0.6.0
 */