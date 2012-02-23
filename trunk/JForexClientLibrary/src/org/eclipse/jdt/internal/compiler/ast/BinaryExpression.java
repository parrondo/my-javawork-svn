/*      */ package org.eclipse.jdt.internal.compiler.ast;
/*      */ 
/*      */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*      */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*      */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ 
/*      */ public class BinaryExpression extends OperatorExpression
/*      */ {
/*      */   public Expression left;
/*      */   public Expression right;
/*      */   public Constant optimizedBooleanConstant;
/*      */ 
/*      */   public BinaryExpression(Expression left, Expression right, int operator)
/*      */   {
/*   39 */     this.left = left;
/*   40 */     this.right = right;
/*   41 */     this.bits |= operator << 6;
/*   42 */     this.sourceStart = left.sourceStart;
/*   43 */     this.sourceEnd = right.sourceEnd;
/*      */   }
/*      */ 
/*      */   public BinaryExpression(BinaryExpression expression)
/*      */   {
/*   53 */     this.left = expression.left;
/*   54 */     this.right = expression.right;
/*   55 */     this.bits = expression.bits;
/*   56 */     this.sourceStart = expression.sourceStart;
/*   57 */     this.sourceEnd = expression.sourceEnd;
/*      */   }
/*      */ 
/*      */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/*   61 */     if (this.resolvedType.id == 11) {
/*   62 */       return this.right.analyseCode(
/*   63 */         currentScope, flowContext, 
/*   64 */         this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits())
/*   65 */         .unconditionalInits();
/*      */     }
/*   67 */     this.left.checkNPE(currentScope, flowContext, flowInfo);
/*   68 */     flowInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
/*   69 */     this.right.checkNPE(currentScope, flowContext, flowInfo);
/*   70 */     return this.right.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
/*      */   }
/*      */ 
/*      */   public void computeConstant(BlockScope scope, int leftId, int rightId)
/*      */   {
/*   76 */     if ((this.left.constant != Constant.NotAConstant) && 
/*   77 */       (this.right.constant != Constant.NotAConstant)) {
/*      */       try {
/*   79 */         this.constant = 
/*   80 */           Constant.computeConstantOperation(
/*   81 */           this.left.constant, 
/*   82 */           leftId, 
/*   83 */           (this.bits & 0xFC0) >> 6, 
/*   84 */           this.right.constant, 
/*   85 */           rightId);
/*      */       } catch (ArithmeticException localArithmeticException) {
/*   87 */         this.constant = Constant.NotAConstant;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*   92 */       this.constant = Constant.NotAConstant;
/*      */ 
/*   94 */       optimizedBooleanConstant(
/*   95 */         leftId, 
/*   96 */         (this.bits & 0xFC0) >> 6, 
/*   97 */         rightId);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Constant optimizedBooleanConstant() {
/*  102 */     return this.optimizedBooleanConstant == null ? this.constant : this.optimizedBooleanConstant;
/*      */   }
/*      */ 
/*      */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*      */   {
/*  112 */     int pc = codeStream.position;
/*  113 */     if (this.constant != Constant.NotAConstant) {
/*  114 */       if (valueRequired)
/*  115 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/*  116 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/*  117 */       return;
/*      */     }
/*  119 */     switch ((this.bits & 0xFC0) >> 6) {
/*      */     case 14:
/*  121 */       switch (this.bits & 0xF)
/*      */       {
/*      */       case 11:
/*  127 */         codeStream.generateStringConcatenationAppend(currentScope, this.left, this.right);
/*  128 */         if (valueRequired) break;
/*  129 */         codeStream.pop();
/*  130 */         break;
/*      */       case 10:
/*  132 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  133 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  134 */         if (!valueRequired) break;
/*  135 */         codeStream.iadd();
/*  136 */         break;
/*      */       case 7:
/*  138 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  139 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  140 */         if (!valueRequired) break;
/*  141 */         codeStream.ladd();
/*  142 */         break;
/*      */       case 8:
/*  144 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  145 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  146 */         if (!valueRequired) break;
/*  147 */         codeStream.dadd();
/*  148 */         break;
/*      */       case 9:
/*  150 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  151 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  152 */         if (!valueRequired) break;
/*  153 */         codeStream.fadd();
/*      */       }
/*      */ 
/*  156 */       break;
/*      */     case 13:
/*  158 */       switch (this.bits & 0xF) {
/*      */       case 10:
/*  160 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  161 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  162 */         if (!valueRequired) break;
/*  163 */         codeStream.isub();
/*  164 */         break;
/*      */       case 7:
/*  166 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  167 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  168 */         if (!valueRequired) break;
/*  169 */         codeStream.lsub();
/*  170 */         break;
/*      */       case 8:
/*  172 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  173 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  174 */         if (!valueRequired) break;
/*  175 */         codeStream.dsub();
/*  176 */         break;
/*      */       case 9:
/*  178 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  179 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  180 */         if (!valueRequired) break;
/*  181 */         codeStream.fsub();
/*      */       }
/*      */ 
/*  184 */       break;
/*      */     case 15:
/*  186 */       switch (this.bits & 0xF) {
/*      */       case 10:
/*  188 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  189 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  190 */         if (!valueRequired) break;
/*  191 */         codeStream.imul();
/*  192 */         break;
/*      */       case 7:
/*  194 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  195 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  196 */         if (!valueRequired) break;
/*  197 */         codeStream.lmul();
/*  198 */         break;
/*      */       case 8:
/*  200 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  201 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  202 */         if (!valueRequired) break;
/*  203 */         codeStream.dmul();
/*  204 */         break;
/*      */       case 9:
/*  206 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  207 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  208 */         if (!valueRequired) break;
/*  209 */         codeStream.fmul();
/*      */       }
/*      */ 
/*  212 */       break;
/*      */     case 9:
/*  214 */       switch (this.bits & 0xF) {
/*      */       case 10:
/*  216 */         this.left.generateCode(currentScope, codeStream, true);
/*  217 */         this.right.generateCode(currentScope, codeStream, true);
/*  218 */         codeStream.idiv();
/*  219 */         if (valueRequired) break;
/*  220 */         codeStream.pop();
/*  221 */         break;
/*      */       case 7:
/*  223 */         this.left.generateCode(currentScope, codeStream, true);
/*  224 */         this.right.generateCode(currentScope, codeStream, true);
/*  225 */         codeStream.ldiv();
/*  226 */         if (valueRequired) break;
/*  227 */         codeStream.pop2();
/*  228 */         break;
/*      */       case 8:
/*  230 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  231 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  232 */         if (!valueRequired) break;
/*  233 */         codeStream.ddiv();
/*  234 */         break;
/*      */       case 9:
/*  236 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  237 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  238 */         if (!valueRequired) break;
/*  239 */         codeStream.fdiv();
/*      */       }
/*      */ 
/*  242 */       break;
/*      */     case 16:
/*  244 */       switch (this.bits & 0xF) {
/*      */       case 10:
/*  246 */         this.left.generateCode(currentScope, codeStream, true);
/*  247 */         this.right.generateCode(currentScope, codeStream, true);
/*  248 */         codeStream.irem();
/*  249 */         if (valueRequired) break;
/*  250 */         codeStream.pop();
/*  251 */         break;
/*      */       case 7:
/*  253 */         this.left.generateCode(currentScope, codeStream, true);
/*  254 */         this.right.generateCode(currentScope, codeStream, true);
/*  255 */         codeStream.lrem();
/*  256 */         if (valueRequired) break;
/*  257 */         codeStream.pop2();
/*  258 */         break;
/*      */       case 8:
/*  260 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  261 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  262 */         if (!valueRequired) break;
/*  263 */         codeStream.drem();
/*  264 */         break;
/*      */       case 9:
/*  266 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  267 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  268 */         if (!valueRequired) break;
/*  269 */         codeStream.frem();
/*      */       }
/*      */ 
/*  272 */       break;
/*      */     case 2:
/*  274 */       switch (this.bits & 0xF)
/*      */       {
/*      */       case 10:
/*  277 */         if ((this.left.constant != Constant.NotAConstant) && 
/*  278 */           (this.left.constant.typeID() == 10) && 
/*  279 */           (this.left.constant.intValue() == 0)) {
/*  280 */           this.right.generateCode(currentScope, codeStream, false);
/*  281 */           if (!valueRequired) break;
/*  282 */           codeStream.iconst_0();
/*      */         }
/*  285 */         else if ((this.right.constant != Constant.NotAConstant) && 
/*  286 */           (this.right.constant.typeID() == 10) && 
/*  287 */           (this.right.constant.intValue() == 0)) {
/*  288 */           this.left.generateCode(currentScope, codeStream, false);
/*  289 */           if (!valueRequired) break;
/*  290 */           codeStream.iconst_0();
/*      */         } else {
/*  292 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/*  293 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*  294 */           if (!valueRequired) break;
/*  295 */           codeStream.iand();
/*      */         }
/*      */ 
/*  298 */         break;
/*      */       case 7:
/*  301 */         if ((this.left.constant != Constant.NotAConstant) && 
/*  302 */           (this.left.constant.typeID() == 7) && 
/*  303 */           (this.left.constant.longValue() == 0L)) {
/*  304 */           this.right.generateCode(currentScope, codeStream, false);
/*  305 */           if (!valueRequired) break;
/*  306 */           codeStream.lconst_0();
/*      */         }
/*  309 */         else if ((this.right.constant != Constant.NotAConstant) && 
/*  310 */           (this.right.constant.typeID() == 7) && 
/*  311 */           (this.right.constant.longValue() == 0L)) {
/*  312 */           this.left.generateCode(currentScope, codeStream, false);
/*  313 */           if (!valueRequired) break;
/*  314 */           codeStream.lconst_0();
/*      */         } else {
/*  316 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/*  317 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*  318 */           if (!valueRequired) break;
/*  319 */           codeStream.land();
/*      */         }
/*      */ 
/*  322 */         break;
/*      */       case 5:
/*  324 */         generateLogicalAnd(currentScope, codeStream, valueRequired);
/*      */       case 6:
/*      */       case 8:
/*  327 */       case 9: } break;
/*      */     case 3:
/*  329 */       switch (this.bits & 0xF)
/*      */       {
/*      */       case 10:
/*  332 */         if ((this.left.constant != Constant.NotAConstant) && 
/*  333 */           (this.left.constant.typeID() == 10) && 
/*  334 */           (this.left.constant.intValue() == 0)) {
/*  335 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*      */         }
/*  338 */         else if ((this.right.constant != Constant.NotAConstant) && 
/*  339 */           (this.right.constant.typeID() == 10) && 
/*  340 */           (this.right.constant.intValue() == 0)) {
/*  341 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/*      */         } else {
/*  343 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/*  344 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*  345 */           if (!valueRequired) break;
/*  346 */           codeStream.ior();
/*      */         }
/*      */ 
/*  349 */         break;
/*      */       case 7:
/*  352 */         if ((this.left.constant != Constant.NotAConstant) && 
/*  353 */           (this.left.constant.typeID() == 7) && 
/*  354 */           (this.left.constant.longValue() == 0L)) {
/*  355 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*      */         }
/*  358 */         else if ((this.right.constant != Constant.NotAConstant) && 
/*  359 */           (this.right.constant.typeID() == 7) && 
/*  360 */           (this.right.constant.longValue() == 0L)) {
/*  361 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/*      */         } else {
/*  363 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/*  364 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*  365 */           if (!valueRequired) break;
/*  366 */           codeStream.lor();
/*      */         }
/*      */ 
/*  369 */         break;
/*      */       case 5:
/*  371 */         generateLogicalOr(currentScope, codeStream, valueRequired);
/*      */       case 6:
/*      */       case 8:
/*  374 */       case 9: } break;
/*      */     case 8:
/*  376 */       switch (this.bits & 0xF)
/*      */       {
/*      */       case 10:
/*  379 */         if ((this.left.constant != Constant.NotAConstant) && 
/*  380 */           (this.left.constant.typeID() == 10) && 
/*  381 */           (this.left.constant.intValue() == 0)) {
/*  382 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*      */         }
/*  385 */         else if ((this.right.constant != Constant.NotAConstant) && 
/*  386 */           (this.right.constant.typeID() == 10) && 
/*  387 */           (this.right.constant.intValue() == 0)) {
/*  388 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/*      */         } else {
/*  390 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/*  391 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*  392 */           if (!valueRequired) break;
/*  393 */           codeStream.ixor();
/*      */         }
/*      */ 
/*  396 */         break;
/*      */       case 7:
/*  399 */         if ((this.left.constant != Constant.NotAConstant) && 
/*  400 */           (this.left.constant.typeID() == 7) && 
/*  401 */           (this.left.constant.longValue() == 0L)) {
/*  402 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*      */         }
/*  405 */         else if ((this.right.constant != Constant.NotAConstant) && 
/*  406 */           (this.right.constant.typeID() == 7) && 
/*  407 */           (this.right.constant.longValue() == 0L)) {
/*  408 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/*      */         } else {
/*  410 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/*  411 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*  412 */           if (!valueRequired) break;
/*  413 */           codeStream.lxor();
/*      */         }
/*      */ 
/*  416 */         break;
/*      */       case 5:
/*  418 */         generateLogicalXor(currentScope, codeStream, valueRequired);
/*      */       case 6:
/*      */       case 8:
/*  421 */       case 9: } break;
/*      */     case 10:
/*  423 */       switch (this.bits & 0xF) {
/*      */       case 10:
/*  425 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  426 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  427 */         if (!valueRequired) break;
/*  428 */         codeStream.ishl();
/*  429 */         break;
/*      */       case 7:
/*  431 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  432 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  433 */         if (!valueRequired) break;
/*  434 */         codeStream.lshl();
/*      */       case 8:
/*  436 */       case 9: } break;
/*      */     case 17:
/*  438 */       switch (this.bits & 0xF) {
/*      */       case 10:
/*  440 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  441 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  442 */         if (!valueRequired) break;
/*  443 */         codeStream.ishr();
/*  444 */         break;
/*      */       case 7:
/*  446 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  447 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  448 */         if (!valueRequired) break;
/*  449 */         codeStream.lshr();
/*      */       case 8:
/*  451 */       case 9: } break;
/*      */     case 19:
/*  453 */       switch (this.bits & 0xF) {
/*      */       case 10:
/*  455 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  456 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  457 */         if (!valueRequired) break;
/*  458 */         codeStream.iushr();
/*  459 */         break;
/*      */       case 7:
/*  461 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  462 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  463 */         if (!valueRequired) break;
/*  464 */         codeStream.lushr();
/*      */       case 8:
/*  466 */       case 9: } break;
/*      */     case 6:
/*      */       BranchLabel falseLabel;
/*  469 */       generateOptimizedGreaterThan(
/*  470 */         currentScope, 
/*  471 */         codeStream, 
/*  472 */         null, 
/*  473 */         falseLabel = new BranchLabel(codeStream), 
/*  474 */         valueRequired);
/*  475 */       if (!valueRequired) break;
/*  476 */       codeStream.iconst_1();
/*  477 */       if ((this.bits & 0x10) != 0) {
/*  478 */         codeStream.generateImplicitConversion(this.implicitConversion);
/*  479 */         codeStream.generateReturnBytecode(this);
/*  480 */         falseLabel.place();
/*  481 */         codeStream.iconst_0();
/*      */       }
/*      */       else
/*      */       {
/*      */         BranchLabel endLabel;
/*  483 */         codeStream.goto_(endLabel = new BranchLabel(codeStream));
/*  484 */         codeStream.decrStackSize(1);
/*  485 */         falseLabel.place();
/*  486 */         codeStream.iconst_0();
/*  487 */         endLabel.place();
/*      */       }
/*      */ 
/*  490 */       break;
/*      */     case 7:
/*      */       BranchLabel falseLabel;
/*  492 */       generateOptimizedGreaterThanOrEqual(
/*  493 */         currentScope, 
/*  494 */         codeStream, 
/*  495 */         null, 
/*  496 */         falseLabel = new BranchLabel(codeStream), 
/*  497 */         valueRequired);
/*  498 */       if (!valueRequired) break;
/*  499 */       codeStream.iconst_1();
/*  500 */       if ((this.bits & 0x10) != 0) {
/*  501 */         codeStream.generateImplicitConversion(this.implicitConversion);
/*  502 */         codeStream.generateReturnBytecode(this);
/*  503 */         falseLabel.place();
/*  504 */         codeStream.iconst_0();
/*      */       }
/*      */       else
/*      */       {
/*      */         BranchLabel endLabel;
/*  506 */         codeStream.goto_(endLabel = new BranchLabel(codeStream));
/*  507 */         codeStream.decrStackSize(1);
/*  508 */         falseLabel.place();
/*  509 */         codeStream.iconst_0();
/*  510 */         endLabel.place();
/*      */       }
/*      */ 
/*  513 */       break;
/*      */     case 4:
/*      */       BranchLabel falseLabel;
/*  515 */       generateOptimizedLessThan(
/*  516 */         currentScope, 
/*  517 */         codeStream, 
/*  518 */         null, 
/*  519 */         falseLabel = new BranchLabel(codeStream), 
/*  520 */         valueRequired);
/*  521 */       if (!valueRequired) break;
/*  522 */       codeStream.iconst_1();
/*  523 */       if ((this.bits & 0x10) != 0) {
/*  524 */         codeStream.generateImplicitConversion(this.implicitConversion);
/*  525 */         codeStream.generateReturnBytecode(this);
/*  526 */         falseLabel.place();
/*  527 */         codeStream.iconst_0();
/*      */       }
/*      */       else
/*      */       {
/*      */         BranchLabel endLabel;
/*  529 */         codeStream.goto_(endLabel = new BranchLabel(codeStream));
/*  530 */         codeStream.decrStackSize(1);
/*  531 */         falseLabel.place();
/*  532 */         codeStream.iconst_0();
/*  533 */         endLabel.place();
/*      */       }
/*      */ 
/*  536 */       break;
/*      */     case 5:
/*      */       BranchLabel falseLabel;
/*  538 */       generateOptimizedLessThanOrEqual(
/*  539 */         currentScope, 
/*  540 */         codeStream, 
/*  541 */         null, 
/*  542 */         falseLabel = new BranchLabel(codeStream), 
/*  543 */         valueRequired);
/*  544 */       if (!valueRequired) break;
/*  545 */       codeStream.iconst_1();
/*  546 */       if ((this.bits & 0x10) != 0) {
/*  547 */         codeStream.generateImplicitConversion(this.implicitConversion);
/*  548 */         codeStream.generateReturnBytecode(this);
/*  549 */         falseLabel.place();
/*  550 */         codeStream.iconst_0();
/*      */       }
/*      */       else
/*      */       {
/*      */         BranchLabel endLabel;
/*  552 */         codeStream.goto_(endLabel = new BranchLabel(codeStream));
/*  553 */         codeStream.decrStackSize(1);
/*  554 */         falseLabel.place();
/*  555 */         codeStream.iconst_0();
/*  556 */         endLabel.place();
/*      */       }case 11:
/*      */     case 12:
/*      */     case 18:
/*  560 */     }if (valueRequired) {
/*  561 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*      */     }
/*  563 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*      */   }
/*      */ 
/*      */   public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*      */   {
/*  571 */     if ((this.constant != Constant.NotAConstant) && (this.constant.typeID() == 5)) {
/*  572 */       super.generateOptimizedBoolean(
/*  573 */         currentScope, 
/*  574 */         codeStream, 
/*  575 */         trueLabel, 
/*  576 */         falseLabel, 
/*  577 */         valueRequired);
/*  578 */       return;
/*      */     }
/*  580 */     switch ((this.bits & 0xFC0) >> 6) {
/*      */     case 4:
/*  582 */       generateOptimizedLessThan(
/*  583 */         currentScope, 
/*  584 */         codeStream, 
/*  585 */         trueLabel, 
/*  586 */         falseLabel, 
/*  587 */         valueRequired);
/*  588 */       return;
/*      */     case 5:
/*  590 */       generateOptimizedLessThanOrEqual(
/*  591 */         currentScope, 
/*  592 */         codeStream, 
/*  593 */         trueLabel, 
/*  594 */         falseLabel, 
/*  595 */         valueRequired);
/*  596 */       return;
/*      */     case 6:
/*  598 */       generateOptimizedGreaterThan(
/*  599 */         currentScope, 
/*  600 */         codeStream, 
/*  601 */         trueLabel, 
/*  602 */         falseLabel, 
/*  603 */         valueRequired);
/*  604 */       return;
/*      */     case 7:
/*  606 */       generateOptimizedGreaterThanOrEqual(
/*  607 */         currentScope, 
/*  608 */         codeStream, 
/*  609 */         trueLabel, 
/*  610 */         falseLabel, 
/*  611 */         valueRequired);
/*  612 */       return;
/*      */     case 2:
/*  614 */       generateOptimizedLogicalAnd(
/*  615 */         currentScope, 
/*  616 */         codeStream, 
/*  617 */         trueLabel, 
/*  618 */         falseLabel, 
/*  619 */         valueRequired);
/*  620 */       return;
/*      */     case 3:
/*  622 */       generateOptimizedLogicalOr(
/*  623 */         currentScope, 
/*  624 */         codeStream, 
/*  625 */         trueLabel, 
/*  626 */         falseLabel, 
/*  627 */         valueRequired);
/*  628 */       return;
/*      */     case 8:
/*  630 */       generateOptimizedLogicalXor(
/*  631 */         currentScope, 
/*  632 */         codeStream, 
/*  633 */         trueLabel, 
/*  634 */         falseLabel, 
/*  635 */         valueRequired);
/*  636 */       return;
/*      */     }
/*  638 */     super.generateOptimizedBoolean(
/*  639 */       currentScope, 
/*  640 */       codeStream, 
/*  641 */       trueLabel, 
/*  642 */       falseLabel, 
/*  643 */       valueRequired);
/*      */   }
/*      */ 
/*      */   public void generateOptimizedGreaterThan(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*      */   {
/*  650 */     int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
/*      */ 
/*  652 */     if (promotedTypeID == 10)
/*      */     {
/*  654 */       if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
/*  655 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  656 */         if (valueRequired) {
/*  657 */           if (falseLabel == null) {
/*  658 */             if (trueLabel != null)
/*      */             {
/*  660 */               codeStream.iflt(trueLabel);
/*      */             }
/*      */           }
/*  663 */           else if (trueLabel == null)
/*      */           {
/*  665 */             codeStream.ifge(falseLabel);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  672 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  673 */         return;
/*      */       }
/*      */ 
/*  676 */       if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
/*  677 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  678 */         if (valueRequired) {
/*  679 */           if (falseLabel == null) {
/*  680 */             if (trueLabel != null)
/*      */             {
/*  682 */               codeStream.ifgt(trueLabel);
/*      */             }
/*      */           }
/*  685 */           else if (trueLabel == null)
/*      */           {
/*  687 */             codeStream.ifle(falseLabel);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  694 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  695 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  699 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/*  700 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/*  701 */     if (valueRequired)
/*  702 */       if (falseLabel == null) {
/*  703 */         if (trueLabel != null)
/*      */         {
/*  705 */           switch (promotedTypeID) {
/*      */           case 10:
/*  707 */             codeStream.if_icmpgt(trueLabel);
/*  708 */             break;
/*      */           case 9:
/*  710 */             codeStream.fcmpl();
/*  711 */             codeStream.ifgt(trueLabel);
/*  712 */             break;
/*      */           case 7:
/*  714 */             codeStream.lcmp();
/*  715 */             codeStream.ifgt(trueLabel);
/*  716 */             break;
/*      */           case 8:
/*  718 */             codeStream.dcmpl();
/*  719 */             codeStream.ifgt(trueLabel);
/*      */           }
/*      */ 
/*  722 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  723 */           return;
/*      */         }
/*      */       }
/*  726 */       else if (trueLabel == null)
/*      */       {
/*  728 */         switch (promotedTypeID) {
/*      */         case 10:
/*  730 */           codeStream.if_icmple(falseLabel);
/*  731 */           break;
/*      */         case 9:
/*  733 */           codeStream.fcmpl();
/*  734 */           codeStream.ifle(falseLabel);
/*  735 */           break;
/*      */         case 7:
/*  737 */           codeStream.lcmp();
/*  738 */           codeStream.ifle(falseLabel);
/*  739 */           break;
/*      */         case 8:
/*  741 */           codeStream.dcmpl();
/*  742 */           codeStream.ifle(falseLabel);
/*      */         }
/*      */ 
/*  745 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  746 */         return;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void generateOptimizedGreaterThanOrEqual(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*      */   {
/*  758 */     int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
/*      */ 
/*  760 */     if (promotedTypeID == 10)
/*      */     {
/*  762 */       if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
/*  763 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  764 */         if (valueRequired) {
/*  765 */           if (falseLabel == null) {
/*  766 */             if (trueLabel != null)
/*      */             {
/*  768 */               codeStream.ifle(trueLabel);
/*      */             }
/*      */           }
/*  771 */           else if (trueLabel == null)
/*      */           {
/*  773 */             codeStream.ifgt(falseLabel);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  780 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  781 */         return;
/*      */       }
/*      */ 
/*  784 */       if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
/*  785 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  786 */         if (valueRequired) {
/*  787 */           if (falseLabel == null) {
/*  788 */             if (trueLabel != null)
/*      */             {
/*  790 */               codeStream.ifge(trueLabel);
/*      */             }
/*      */           }
/*  793 */           else if (trueLabel == null)
/*      */           {
/*  795 */             codeStream.iflt(falseLabel);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  802 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  803 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  807 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/*  808 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/*  809 */     if (valueRequired)
/*  810 */       if (falseLabel == null) {
/*  811 */         if (trueLabel != null)
/*      */         {
/*  813 */           switch (promotedTypeID) {
/*      */           case 10:
/*  815 */             codeStream.if_icmpge(trueLabel);
/*  816 */             break;
/*      */           case 9:
/*  818 */             codeStream.fcmpl();
/*  819 */             codeStream.ifge(trueLabel);
/*  820 */             break;
/*      */           case 7:
/*  822 */             codeStream.lcmp();
/*  823 */             codeStream.ifge(trueLabel);
/*  824 */             break;
/*      */           case 8:
/*  826 */             codeStream.dcmpl();
/*  827 */             codeStream.ifge(trueLabel);
/*      */           }
/*      */ 
/*  830 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  831 */           return;
/*      */         }
/*      */       }
/*  834 */       else if (trueLabel == null)
/*      */       {
/*  836 */         switch (promotedTypeID) {
/*      */         case 10:
/*  838 */           codeStream.if_icmplt(falseLabel);
/*  839 */           break;
/*      */         case 9:
/*  841 */           codeStream.fcmpl();
/*  842 */           codeStream.iflt(falseLabel);
/*  843 */           break;
/*      */         case 7:
/*  845 */           codeStream.lcmp();
/*  846 */           codeStream.iflt(falseLabel);
/*  847 */           break;
/*      */         case 8:
/*  849 */           codeStream.dcmpl();
/*  850 */           codeStream.iflt(falseLabel);
/*      */         }
/*      */ 
/*  853 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  854 */         return;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void generateOptimizedLessThan(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*      */   {
/*  866 */     int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
/*      */ 
/*  868 */     if (promotedTypeID == 10)
/*      */     {
/*  870 */       if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
/*  871 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  872 */         if (valueRequired) {
/*  873 */           if (falseLabel == null) {
/*  874 */             if (trueLabel != null)
/*      */             {
/*  876 */               codeStream.ifgt(trueLabel);
/*      */             }
/*      */           }
/*  879 */           else if (trueLabel == null)
/*      */           {
/*  881 */             codeStream.ifle(falseLabel);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  887 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  888 */         return;
/*      */       }
/*      */ 
/*  891 */       if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
/*  892 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  893 */         if (valueRequired) {
/*  894 */           if (falseLabel == null) {
/*  895 */             if (trueLabel != null)
/*      */             {
/*  897 */               codeStream.iflt(trueLabel);
/*      */             }
/*      */           }
/*  900 */           else if (trueLabel == null)
/*      */           {
/*  902 */             codeStream.ifge(falseLabel);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  908 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  909 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  913 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/*  914 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/*  915 */     if (valueRequired)
/*  916 */       if (falseLabel == null) {
/*  917 */         if (trueLabel != null)
/*      */         {
/*  919 */           switch (promotedTypeID) {
/*      */           case 10:
/*  921 */             codeStream.if_icmplt(trueLabel);
/*  922 */             break;
/*      */           case 9:
/*  924 */             codeStream.fcmpg();
/*  925 */             codeStream.iflt(trueLabel);
/*  926 */             break;
/*      */           case 7:
/*  928 */             codeStream.lcmp();
/*  929 */             codeStream.iflt(trueLabel);
/*  930 */             break;
/*      */           case 8:
/*  932 */             codeStream.dcmpg();
/*  933 */             codeStream.iflt(trueLabel);
/*      */           }
/*  935 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  936 */           return;
/*      */         }
/*      */       }
/*  939 */       else if (trueLabel == null)
/*      */       {
/*  941 */         switch (promotedTypeID) {
/*      */         case 10:
/*  943 */           codeStream.if_icmpge(falseLabel);
/*  944 */           break;
/*      */         case 9:
/*  946 */           codeStream.fcmpg();
/*  947 */           codeStream.ifge(falseLabel);
/*  948 */           break;
/*      */         case 7:
/*  950 */           codeStream.lcmp();
/*  951 */           codeStream.ifge(falseLabel);
/*  952 */           break;
/*      */         case 8:
/*  954 */           codeStream.dcmpg();
/*  955 */           codeStream.ifge(falseLabel);
/*      */         }
/*  957 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  958 */         return;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void generateOptimizedLessThanOrEqual(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*      */   {
/*  970 */     int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
/*      */ 
/*  972 */     if (promotedTypeID == 10)
/*      */     {
/*  974 */       if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
/*  975 */         this.right.generateCode(currentScope, codeStream, valueRequired);
/*  976 */         if (valueRequired) {
/*  977 */           if (falseLabel == null) {
/*  978 */             if (trueLabel != null)
/*      */             {
/*  980 */               codeStream.ifge(trueLabel);
/*      */             }
/*      */           }
/*  983 */           else if (trueLabel == null)
/*      */           {
/*  985 */             codeStream.iflt(falseLabel);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  992 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*  993 */         return;
/*      */       }
/*      */ 
/*  996 */       if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
/*  997 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*  998 */         if (valueRequired) {
/*  999 */           if (falseLabel == null) {
/* 1000 */             if (trueLabel != null)
/*      */             {
/* 1002 */               codeStream.ifle(trueLabel);
/*      */             }
/*      */           }
/* 1005 */           else if (trueLabel == null)
/*      */           {
/* 1007 */             codeStream.ifgt(falseLabel);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1014 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/* 1015 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1019 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1020 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/* 1021 */     if (valueRequired)
/* 1022 */       if (falseLabel == null) {
/* 1023 */         if (trueLabel != null)
/*      */         {
/* 1025 */           switch (promotedTypeID) {
/*      */           case 10:
/* 1027 */             codeStream.if_icmple(trueLabel);
/* 1028 */             break;
/*      */           case 9:
/* 1030 */             codeStream.fcmpg();
/* 1031 */             codeStream.ifle(trueLabel);
/* 1032 */             break;
/*      */           case 7:
/* 1034 */             codeStream.lcmp();
/* 1035 */             codeStream.ifle(trueLabel);
/* 1036 */             break;
/*      */           case 8:
/* 1038 */             codeStream.dcmpg();
/* 1039 */             codeStream.ifle(trueLabel);
/*      */           }
/*      */ 
/* 1042 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/* 1043 */           return;
/*      */         }
/*      */       }
/* 1046 */       else if (trueLabel == null)
/*      */       {
/* 1048 */         switch (promotedTypeID) {
/*      */         case 10:
/* 1050 */           codeStream.if_icmpgt(falseLabel);
/* 1051 */           break;
/*      */         case 9:
/* 1053 */           codeStream.fcmpg();
/* 1054 */           codeStream.ifgt(falseLabel);
/* 1055 */           break;
/*      */         case 7:
/* 1057 */           codeStream.lcmp();
/* 1058 */           codeStream.ifgt(falseLabel);
/* 1059 */           break;
/*      */         case 8:
/* 1061 */           codeStream.dcmpg();
/* 1062 */           codeStream.ifgt(falseLabel);
/*      */         }
/*      */ 
/* 1065 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/* 1066 */         return;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void generateLogicalAnd(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*      */   {
/* 1079 */     if ((this.left.implicitConversion & 0xF) == 5)
/*      */     {
/*      */       Constant condConst;
/* 1080 */       if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1081 */         if (condConst.booleanValue())
/*      */         {
/* 1083 */           this.left.generateCode(currentScope, codeStream, false);
/* 1084 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*      */         }
/*      */         else {
/* 1087 */           this.left.generateCode(currentScope, codeStream, false);
/* 1088 */           this.right.generateCode(currentScope, codeStream, false);
/* 1089 */           if (valueRequired) {
/* 1090 */             codeStream.iconst_0();
/*      */           }
/*      */ 
/* 1093 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */         }
/* 1095 */         return;
/*      */       }
/* 1097 */       if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1098 */         if (condConst.booleanValue())
/*      */         {
/* 1100 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1101 */           this.right.generateCode(currentScope, codeStream, false);
/*      */         }
/*      */         else {
/* 1104 */           this.left.generateCode(currentScope, codeStream, false);
/* 1105 */           this.right.generateCode(currentScope, codeStream, false);
/* 1106 */           if (valueRequired) {
/* 1107 */             codeStream.iconst_0();
/*      */           }
/*      */ 
/* 1110 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */         }
/* 1112 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1116 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1117 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/* 1118 */     if (valueRequired) {
/* 1119 */       codeStream.iand();
/*      */     }
/*      */ 
/* 1122 */     codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */   }
/*      */ 
/*      */   public void generateLogicalOr(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*      */   {
/* 1130 */     if ((this.left.implicitConversion & 0xF) == 5)
/*      */     {
/*      */       Constant condConst;
/* 1131 */       if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1132 */         if (condConst.booleanValue())
/*      */         {
/* 1134 */           this.left.generateCode(currentScope, codeStream, false);
/* 1135 */           this.right.generateCode(currentScope, codeStream, false);
/* 1136 */           if (valueRequired) {
/* 1137 */             codeStream.iconst_1();
/*      */           }
/*      */ 
/* 1140 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */         }
/*      */         else {
/* 1143 */           this.left.generateCode(currentScope, codeStream, false);
/* 1144 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*      */         }
/* 1146 */         return;
/*      */       }
/* 1148 */       if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1149 */         if (condConst.booleanValue())
/*      */         {
/* 1151 */           this.left.generateCode(currentScope, codeStream, false);
/* 1152 */           this.right.generateCode(currentScope, codeStream, false);
/* 1153 */           if (valueRequired) {
/* 1154 */             codeStream.iconst_1();
/*      */           }
/*      */ 
/* 1157 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */         }
/*      */         else {
/* 1160 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1161 */           this.right.generateCode(currentScope, codeStream, false);
/*      */         }
/* 1163 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1167 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1168 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/* 1169 */     if (valueRequired) {
/* 1170 */       codeStream.ior();
/*      */     }
/*      */ 
/* 1173 */     codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */   }
/*      */ 
/*      */   public void generateLogicalXor(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*      */   {
/* 1181 */     if ((this.left.implicitConversion & 0xF) == 5)
/*      */     {
/*      */       Constant condConst;
/* 1182 */       if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1183 */         if (condConst.booleanValue())
/*      */         {
/* 1185 */           this.left.generateCode(currentScope, codeStream, false);
/* 1186 */           if (valueRequired) {
/* 1187 */             codeStream.iconst_1();
/*      */           }
/* 1189 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/* 1190 */           if (valueRequired) {
/* 1191 */             codeStream.ixor();
/* 1192 */             codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */           }
/*      */         }
/*      */         else {
/* 1196 */           this.left.generateCode(currentScope, codeStream, false);
/* 1197 */           this.right.generateCode(currentScope, codeStream, valueRequired);
/*      */         }
/* 1199 */         return;
/*      */       }
/* 1201 */       if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1202 */         if (condConst.booleanValue())
/*      */         {
/* 1204 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1205 */           this.right.generateCode(currentScope, codeStream, false);
/* 1206 */           if (valueRequired) {
/* 1207 */             codeStream.iconst_1();
/* 1208 */             codeStream.ixor();
/* 1209 */             codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */           }
/*      */         }
/*      */         else {
/* 1213 */           this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1214 */           this.right.generateCode(currentScope, codeStream, false);
/*      */         }
/* 1216 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1220 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1221 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/* 1222 */     if (valueRequired) {
/* 1223 */       codeStream.ixor();
/*      */     }
/*      */ 
/* 1226 */     codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */   }
/*      */ 
/*      */   public void generateOptimizedLogicalAnd(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*      */   {
/* 1234 */     if ((this.left.implicitConversion & 0xF) == 5)
/*      */     {
/*      */       Constant condConst;
/* 1235 */       if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1236 */         if (condConst.booleanValue())
/*      */         {
/* 1238 */           this.left.generateOptimizedBoolean(
/* 1239 */             currentScope, 
/* 1240 */             codeStream, 
/* 1241 */             trueLabel, 
/* 1242 */             falseLabel, 
/* 1243 */             false);
/* 1244 */           this.right.generateOptimizedBoolean(
/* 1245 */             currentScope, 
/* 1246 */             codeStream, 
/* 1247 */             trueLabel, 
/* 1248 */             falseLabel, 
/* 1249 */             valueRequired);
/*      */         }
/*      */         else {
/* 1252 */           this.left.generateOptimizedBoolean(
/* 1253 */             currentScope, 
/* 1254 */             codeStream, 
/* 1255 */             trueLabel, 
/* 1256 */             falseLabel, 
/* 1257 */             false);
/* 1258 */           this.right.generateOptimizedBoolean(
/* 1259 */             currentScope, 
/* 1260 */             codeStream, 
/* 1261 */             trueLabel, 
/* 1262 */             falseLabel, 
/* 1263 */             false);
/* 1264 */           if ((valueRequired) && 
/* 1265 */             (falseLabel != null))
/*      */           {
/* 1267 */             codeStream.goto_(falseLabel);
/*      */           }
/*      */ 
/* 1271 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */         }
/* 1273 */         return;
/*      */       }
/* 1275 */       if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1276 */         if (condConst.booleanValue())
/*      */         {
/* 1278 */           this.left.generateOptimizedBoolean(
/* 1279 */             currentScope, 
/* 1280 */             codeStream, 
/* 1281 */             trueLabel, 
/* 1282 */             falseLabel, 
/* 1283 */             valueRequired);
/* 1284 */           this.right.generateOptimizedBoolean(
/* 1285 */             currentScope, 
/* 1286 */             codeStream, 
/* 1287 */             trueLabel, 
/* 1288 */             falseLabel, 
/* 1289 */             false);
/*      */         }
/*      */         else {
/* 1292 */           BranchLabel internalTrueLabel = new BranchLabel(codeStream);
/* 1293 */           this.left.generateOptimizedBoolean(
/* 1294 */             currentScope, 
/* 1295 */             codeStream, 
/* 1296 */             internalTrueLabel, 
/* 1297 */             falseLabel, 
/* 1298 */             false);
/* 1299 */           internalTrueLabel.place();
/* 1300 */           this.right.generateOptimizedBoolean(
/* 1301 */             currentScope, 
/* 1302 */             codeStream, 
/* 1303 */             trueLabel, 
/* 1304 */             falseLabel, 
/* 1305 */             false);
/* 1306 */           if ((valueRequired) && 
/* 1307 */             (falseLabel != null))
/*      */           {
/* 1309 */             codeStream.goto_(falseLabel);
/*      */           }
/*      */ 
/* 1313 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */         }
/* 1315 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1319 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1320 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/* 1321 */     if (valueRequired) {
/* 1322 */       codeStream.iand();
/* 1323 */       if (falseLabel == null) {
/* 1324 */         if (trueLabel != null)
/*      */         {
/* 1326 */           codeStream.ifne(trueLabel);
/*      */         }
/*      */ 
/*      */       }
/* 1330 */       else if (trueLabel == null) {
/* 1331 */         codeStream.ifeq(falseLabel);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1338 */     codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */   }
/*      */ 
/*      */   public void generateOptimizedLogicalOr(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*      */   {
/* 1346 */     if ((this.left.implicitConversion & 0xF) == 5)
/*      */     {
/*      */       Constant condConst;
/* 1347 */       if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1348 */         if (condConst.booleanValue())
/*      */         {
/* 1350 */           this.left.generateOptimizedBoolean(
/* 1351 */             currentScope, 
/* 1352 */             codeStream, 
/* 1353 */             trueLabel, 
/* 1354 */             falseLabel, 
/* 1355 */             false);
/* 1356 */           BranchLabel internalFalseLabel = new BranchLabel(codeStream);
/* 1357 */           this.right.generateOptimizedBoolean(
/* 1358 */             currentScope, 
/* 1359 */             codeStream, 
/* 1360 */             trueLabel, 
/* 1361 */             internalFalseLabel, 
/* 1362 */             false);
/* 1363 */           internalFalseLabel.place();
/* 1364 */           if ((valueRequired) && 
/* 1365 */             (trueLabel != null)) {
/* 1366 */             codeStream.goto_(trueLabel);
/*      */           }
/*      */ 
/* 1370 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */         }
/*      */         else {
/* 1373 */           this.left.generateOptimizedBoolean(
/* 1374 */             currentScope, 
/* 1375 */             codeStream, 
/* 1376 */             trueLabel, 
/* 1377 */             falseLabel, 
/* 1378 */             false);
/* 1379 */           this.right.generateOptimizedBoolean(
/* 1380 */             currentScope, 
/* 1381 */             codeStream, 
/* 1382 */             trueLabel, 
/* 1383 */             falseLabel, 
/* 1384 */             valueRequired);
/*      */         }
/* 1386 */         return;
/*      */       }
/* 1388 */       if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1389 */         if (condConst.booleanValue())
/*      */         {
/* 1391 */           BranchLabel internalFalseLabel = new BranchLabel(codeStream);
/* 1392 */           this.left.generateOptimizedBoolean(
/* 1393 */             currentScope, 
/* 1394 */             codeStream, 
/* 1395 */             trueLabel, 
/* 1396 */             internalFalseLabel, 
/* 1397 */             false);
/* 1398 */           internalFalseLabel.place();
/* 1399 */           this.right.generateOptimizedBoolean(
/* 1400 */             currentScope, 
/* 1401 */             codeStream, 
/* 1402 */             trueLabel, 
/* 1403 */             falseLabel, 
/* 1404 */             false);
/* 1405 */           if ((valueRequired) && 
/* 1406 */             (trueLabel != null)) {
/* 1407 */             codeStream.goto_(trueLabel);
/*      */           }
/*      */ 
/* 1411 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */         }
/*      */         else {
/* 1414 */           this.left.generateOptimizedBoolean(
/* 1415 */             currentScope, 
/* 1416 */             codeStream, 
/* 1417 */             trueLabel, 
/* 1418 */             falseLabel, 
/* 1419 */             valueRequired);
/* 1420 */           this.right.generateOptimizedBoolean(
/* 1421 */             currentScope, 
/* 1422 */             codeStream, 
/* 1423 */             trueLabel, 
/* 1424 */             falseLabel, 
/* 1425 */             false);
/*      */         }
/* 1427 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1431 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1432 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/* 1433 */     if (valueRequired) {
/* 1434 */       codeStream.ior();
/* 1435 */       if (falseLabel == null) {
/* 1436 */         if (trueLabel != null)
/*      */         {
/* 1438 */           codeStream.ifne(trueLabel);
/*      */         }
/*      */ 
/*      */       }
/* 1442 */       else if (trueLabel == null) {
/* 1443 */         codeStream.ifeq(falseLabel);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1450 */     codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */   }
/*      */ 
/*      */   public void generateOptimizedLogicalXor(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*      */   {
/* 1458 */     if ((this.left.implicitConversion & 0xF) == 5)
/*      */     {
/*      */       Constant condConst;
/* 1459 */       if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1460 */         if (condConst.booleanValue())
/*      */         {
/* 1462 */           this.left.generateOptimizedBoolean(
/* 1463 */             currentScope, 
/* 1464 */             codeStream, 
/* 1465 */             trueLabel, 
/* 1466 */             falseLabel, 
/* 1467 */             false);
/* 1468 */           this.right.generateOptimizedBoolean(
/* 1469 */             currentScope, 
/* 1470 */             codeStream, 
/* 1471 */             falseLabel, 
/* 1472 */             trueLabel, 
/* 1473 */             valueRequired);
/*      */         }
/*      */         else {
/* 1476 */           this.left.generateOptimizedBoolean(
/* 1477 */             currentScope, 
/* 1478 */             codeStream, 
/* 1479 */             trueLabel, 
/* 1480 */             falseLabel, 
/* 1481 */             false);
/* 1482 */           this.right.generateOptimizedBoolean(
/* 1483 */             currentScope, 
/* 1484 */             codeStream, 
/* 1485 */             trueLabel, 
/* 1486 */             falseLabel, 
/* 1487 */             valueRequired);
/*      */         }
/* 1489 */         return;
/*      */       }
/* 1491 */       if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1492 */         if (condConst.booleanValue())
/*      */         {
/* 1494 */           this.left.generateOptimizedBoolean(
/* 1495 */             currentScope, 
/* 1496 */             codeStream, 
/* 1497 */             falseLabel, 
/* 1498 */             trueLabel, 
/* 1499 */             valueRequired);
/* 1500 */           this.right.generateOptimizedBoolean(
/* 1501 */             currentScope, 
/* 1502 */             codeStream, 
/* 1503 */             trueLabel, 
/* 1504 */             falseLabel, 
/* 1505 */             false);
/*      */         }
/*      */         else {
/* 1508 */           this.left.generateOptimizedBoolean(
/* 1509 */             currentScope, 
/* 1510 */             codeStream, 
/* 1511 */             trueLabel, 
/* 1512 */             falseLabel, 
/* 1513 */             valueRequired);
/* 1514 */           this.right.generateOptimizedBoolean(
/* 1515 */             currentScope, 
/* 1516 */             codeStream, 
/* 1517 */             trueLabel, 
/* 1518 */             falseLabel, 
/* 1519 */             false);
/*      */         }
/* 1521 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1525 */     this.left.generateCode(currentScope, codeStream, valueRequired);
/* 1526 */     this.right.generateCode(currentScope, codeStream, valueRequired);
/* 1527 */     if (valueRequired) {
/* 1528 */       codeStream.ixor();
/* 1529 */       if (falseLabel == null) {
/* 1530 */         if (trueLabel != null)
/*      */         {
/* 1532 */           codeStream.ifne(trueLabel);
/*      */         }
/*      */ 
/*      */       }
/* 1536 */       else if (trueLabel == null) {
/* 1537 */         codeStream.ifeq(falseLabel);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1544 */     codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*      */   }
/*      */ 
/*      */   public void generateOptimizedStringConcatenation(BlockScope blockScope, CodeStream codeStream, int typeID)
/*      */   {
/* 1555 */     if (((this.bits & 0xFC0) >> 6 == 14) && 
/* 1556 */       ((this.bits & 0xF) == 11)) {
/* 1557 */       if (this.constant != Constant.NotAConstant) {
/* 1558 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/* 1559 */         codeStream.invokeStringConcatenationAppendForType(this.implicitConversion & 0xF);
/*      */       } else {
/* 1561 */         int pc = codeStream.position;
/* 1562 */         this.left.generateOptimizedStringConcatenation(
/* 1563 */           blockScope, 
/* 1564 */           codeStream, 
/* 1565 */           this.left.implicitConversion & 0xF);
/* 1566 */         codeStream.recordPositionsFrom(pc, this.left.sourceStart);
/* 1567 */         pc = codeStream.position;
/* 1568 */         this.right.generateOptimizedStringConcatenation(
/* 1569 */           blockScope, 
/* 1570 */           codeStream, 
/* 1571 */           this.right.implicitConversion & 0xF);
/* 1572 */         codeStream.recordPositionsFrom(pc, this.right.sourceStart);
/*      */       }
/*      */     }
/* 1575 */     else super.generateOptimizedStringConcatenation(blockScope, codeStream, typeID);
/*      */   }
/*      */ 
/*      */   public void generateOptimizedStringConcatenationCreation(BlockScope blockScope, CodeStream codeStream, int typeID)
/*      */   {
/* 1586 */     if (((this.bits & 0xFC0) >> 6 == 14) && 
/* 1587 */       ((this.bits & 0xF) == 11)) {
/* 1588 */       if (this.constant != Constant.NotAConstant) {
/* 1589 */         codeStream.newStringContatenation();
/* 1590 */         codeStream.dup();
/* 1591 */         codeStream.ldc(this.constant.stringValue());
/* 1592 */         codeStream.invokeStringConcatenationStringConstructor();
/*      */       }
/*      */       else {
/* 1595 */         int pc = codeStream.position;
/* 1596 */         this.left.generateOptimizedStringConcatenationCreation(
/* 1597 */           blockScope, 
/* 1598 */           codeStream, 
/* 1599 */           this.left.implicitConversion & 0xF);
/* 1600 */         codeStream.recordPositionsFrom(pc, this.left.sourceStart);
/* 1601 */         pc = codeStream.position;
/* 1602 */         this.right.generateOptimizedStringConcatenation(
/* 1603 */           blockScope, 
/* 1604 */           codeStream, 
/* 1605 */           this.right.implicitConversion & 0xF);
/* 1606 */         codeStream.recordPositionsFrom(pc, this.right.sourceStart);
/*      */       }
/*      */     }
/* 1609 */     else super.generateOptimizedStringConcatenationCreation(blockScope, codeStream, typeID);
/*      */   }
/*      */ 
/*      */   public boolean isCompactableOperation()
/*      */   {
/* 1614 */     return true;
/*      */   }
/*      */ 
/*      */   void nonRecursiveResolveTypeUpwards(BlockScope scope)
/*      */   {
/* 1627 */     TypeBinding leftType = this.left.resolvedType;
/*      */     boolean rightIsCast;
/* 1629 */     if ((rightIsCast = this.right instanceof CastExpression)) {
/* 1630 */       this.right.bits |= 32;
/*      */     }
/* 1632 */     TypeBinding rightType = this.right.resolveType(scope);
/*      */ 
/* 1635 */     if ((leftType == null) || (rightType == null)) {
/* 1636 */       this.constant = Constant.NotAConstant;
/* 1637 */       return;
/*      */     }
/*      */ 
/* 1640 */     int leftTypeID = leftType.id;
/* 1641 */     int rightTypeID = rightType.id;
/*      */ 
/* 1644 */     boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
/* 1645 */     if (use15specifics) {
/* 1646 */       if ((!leftType.isBaseType()) && (rightTypeID != 11) && (rightTypeID != 12)) {
/* 1647 */         leftTypeID = scope.environment().computeBoxingType(leftType).id;
/*      */       }
/* 1649 */       if ((!rightType.isBaseType()) && (leftTypeID != 11) && (leftTypeID != 12)) {
/* 1650 */         rightTypeID = scope.environment().computeBoxingType(rightType).id;
/*      */       }
/*      */     }
/* 1653 */     if ((leftTypeID > 15) || 
/* 1654 */       (rightTypeID > 15)) {
/* 1655 */       if (leftTypeID == 11) {
/* 1656 */         rightTypeID = 1;
/* 1657 */       } else if (rightTypeID == 11) {
/* 1658 */         leftTypeID = 1;
/*      */       } else {
/* 1660 */         this.constant = Constant.NotAConstant;
/* 1661 */         scope.problemReporter().invalidOperator(this, leftType, rightType);
/* 1662 */         return;
/*      */       }
/*      */     }
/* 1665 */     if ((this.bits & 0xFC0) >> 6 == 14) {
/* 1666 */       if (leftTypeID == 11) {
/* 1667 */         this.left.computeConversion(scope, leftType, leftType);
/* 1668 */         if ((rightType.isArrayType()) && (((ArrayBinding)rightType).elementsType() == TypeBinding.CHAR)) {
/* 1669 */           scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.right);
/*      */         }
/*      */       }
/* 1672 */       if (rightTypeID == 11) {
/* 1673 */         this.right.computeConversion(scope, rightType, rightType);
/* 1674 */         if ((leftType.isArrayType()) && (((ArrayBinding)leftType).elementsType() == TypeBinding.CHAR)) {
/* 1675 */           scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.left);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1687 */     int operator = (this.bits & 0xFC0) >> 6;
/* 1688 */     int operatorSignature = OperatorExpression.OperatorSignatures[operator][((leftTypeID << 4) + rightTypeID)];
/*      */ 
/* 1690 */     this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), leftType);
/* 1691 */     this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 8 & 0xF), rightType);
/* 1692 */     this.bits |= operatorSignature & 0xF;
/* 1693 */     switch (operatorSignature & 0xF)
/*      */     {
/*      */     case 5:
/* 1696 */       this.resolvedType = TypeBinding.BOOLEAN;
/* 1697 */       break;
/*      */     case 3:
/* 1699 */       this.resolvedType = TypeBinding.BYTE;
/* 1700 */       break;
/*      */     case 2:
/* 1702 */       this.resolvedType = TypeBinding.CHAR;
/* 1703 */       break;
/*      */     case 8:
/* 1705 */       this.resolvedType = TypeBinding.DOUBLE;
/* 1706 */       break;
/*      */     case 9:
/* 1708 */       this.resolvedType = TypeBinding.FLOAT;
/* 1709 */       break;
/*      */     case 10:
/* 1711 */       this.resolvedType = TypeBinding.INT;
/* 1712 */       break;
/*      */     case 7:
/* 1714 */       this.resolvedType = TypeBinding.LONG;
/* 1715 */       break;
/*      */     case 11:
/* 1717 */       this.resolvedType = scope.getJavaLangString();
/* 1718 */       break;
/*      */     case 4:
/*      */     case 6:
/*      */     default:
/* 1720 */       this.constant = Constant.NotAConstant;
/* 1721 */       scope.problemReporter().invalidOperator(this, leftType, rightType);
/* 1722 */       return;
/*      */     }
/*      */     boolean leftIsCast;
/* 1726 */     if (((leftIsCast = this.left instanceof CastExpression)) || 
/* 1727 */       (rightIsCast)) {
/* 1728 */       CastExpression.checkNeedForArgumentCasts(scope, operator, operatorSignature, this.left, leftTypeID, leftIsCast, this.right, rightTypeID, rightIsCast);
/*      */     }
/*      */ 
/* 1731 */     computeConstant(scope, leftTypeID, rightTypeID);
/*      */   }
/*      */ 
/*      */   public void optimizedBooleanConstant(int leftId, int operator, int rightId) {
/* 1735 */     switch (operator) {
/*      */     case 2:
/* 1737 */       if ((leftId == 5) && (rightId == 5)) break;
/* 1738 */       return;
/*      */     case 0:
/*      */       Constant cst;
/* 1742 */       if ((cst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1743 */         if (!cst.booleanValue()) {
/* 1744 */           this.optimizedBooleanConstant = cst;
/* 1745 */           return;
/*      */         }
/* 1747 */         if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1748 */           this.optimizedBooleanConstant = cst;
/*      */         }
/*      */ 
/* 1751 */         return;
/*      */       }
/*      */ 
/* 1754 */       if (((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) && 
/* 1755 */         (!cst.booleanValue())) {
/* 1756 */         this.optimizedBooleanConstant = cst;
/*      */       }
/*      */ 
/* 1759 */       return;
/*      */     case 3:
/* 1761 */       if ((leftId != 5) || (rightId != 5))
/* 1762 */         return;
/*      */     case 1:
/*      */       Constant cst;
/* 1765 */       if ((cst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1766 */         if (cst.booleanValue()) {
/* 1767 */           this.optimizedBooleanConstant = cst;
/* 1768 */           return;
/*      */         }
/* 1770 */         if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
/* 1771 */           this.optimizedBooleanConstant = cst;
/*      */         }
/* 1773 */         return;
/*      */       }
/*      */ 
/* 1776 */       if (((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) && 
/* 1777 */         (cst.booleanValue()))
/* 1778 */         this.optimizedBooleanConstant = cst;
/*      */     }
/*      */   }
/*      */ 
/*      */   public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output)
/*      */   {
/* 1787 */     this.left.printExpression(indent, output).append(' ').append(operatorToString()).append(' ');
/* 1788 */     return this.right.printExpression(0, output);
/*      */   }
/*      */ 
/*      */   public TypeBinding resolveType(BlockScope scope)
/*      */   {
/* 1795 */     boolean leftIsCast;
/* 1795 */     if ((leftIsCast = this.left instanceof CastExpression)) this.left.bits |= 32;
/* 1796 */     TypeBinding leftType = this.left.resolveType(scope);
/*      */     boolean rightIsCast;
/* 1798 */     if ((rightIsCast = this.right instanceof CastExpression)) this.right.bits |= 32;
/* 1799 */     TypeBinding rightType = this.right.resolveType(scope);
/*      */ 
/* 1802 */     if ((leftType == null) || (rightType == null)) {
/* 1803 */       this.constant = Constant.NotAConstant;
/* 1804 */       return null;
/*      */     }
/*      */ 
/* 1807 */     int leftTypeID = leftType.id;
/* 1808 */     int rightTypeID = rightType.id;
/*      */ 
/* 1811 */     boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
/* 1812 */     if (use15specifics) {
/* 1813 */       if ((!leftType.isBaseType()) && (rightTypeID != 11) && (rightTypeID != 12)) {
/* 1814 */         leftTypeID = scope.environment().computeBoxingType(leftType).id;
/*      */       }
/* 1816 */       if ((!rightType.isBaseType()) && (leftTypeID != 11) && (leftTypeID != 12)) {
/* 1817 */         rightTypeID = scope.environment().computeBoxingType(rightType).id;
/*      */       }
/*      */     }
/* 1820 */     if ((leftTypeID > 15) || 
/* 1821 */       (rightTypeID > 15)) {
/* 1822 */       if (leftTypeID == 11) {
/* 1823 */         rightTypeID = 1;
/* 1824 */       } else if (rightTypeID == 11) {
/* 1825 */         leftTypeID = 1;
/*      */       } else {
/* 1827 */         this.constant = Constant.NotAConstant;
/* 1828 */         scope.problemReporter().invalidOperator(this, leftType, rightType);
/* 1829 */         return null;
/*      */       }
/*      */     }
/* 1832 */     if ((this.bits & 0xFC0) >> 6 == 14) {
/* 1833 */       if (leftTypeID == 11) {
/* 1834 */         this.left.computeConversion(scope, leftType, leftType);
/* 1835 */         if ((rightType.isArrayType()) && (((ArrayBinding)rightType).elementsType() == TypeBinding.CHAR)) {
/* 1836 */           scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.right);
/*      */         }
/*      */       }
/* 1839 */       if (rightTypeID == 11) {
/* 1840 */         this.right.computeConversion(scope, rightType, rightType);
/* 1841 */         if ((leftType.isArrayType()) && (((ArrayBinding)leftType).elementsType() == TypeBinding.CHAR)) {
/* 1842 */           scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.left);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1854 */     int operator = (this.bits & 0xFC0) >> 6;
/* 1855 */     int operatorSignature = OperatorExpression.OperatorSignatures[operator][((leftTypeID << 4) + rightTypeID)];
/*      */ 
/* 1857 */     this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), leftType);
/* 1858 */     this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 8 & 0xF), rightType);
/* 1859 */     this.bits |= operatorSignature & 0xF;
/* 1860 */     switch (operatorSignature & 0xF)
/*      */     {
/*      */     case 5:
/* 1863 */       this.resolvedType = TypeBinding.BOOLEAN;
/* 1864 */       break;
/*      */     case 3:
/* 1866 */       this.resolvedType = TypeBinding.BYTE;
/* 1867 */       break;
/*      */     case 2:
/* 1869 */       this.resolvedType = TypeBinding.CHAR;
/* 1870 */       break;
/*      */     case 8:
/* 1872 */       this.resolvedType = TypeBinding.DOUBLE;
/* 1873 */       break;
/*      */     case 9:
/* 1875 */       this.resolvedType = TypeBinding.FLOAT;
/* 1876 */       break;
/*      */     case 10:
/* 1878 */       this.resolvedType = TypeBinding.INT;
/* 1879 */       break;
/*      */     case 7:
/* 1881 */       this.resolvedType = TypeBinding.LONG;
/* 1882 */       break;
/*      */     case 11:
/* 1884 */       this.resolvedType = scope.getJavaLangString();
/* 1885 */       break;
/*      */     case 4:
/*      */     case 6:
/*      */     default:
/* 1887 */       this.constant = Constant.NotAConstant;
/* 1888 */       scope.problemReporter().invalidOperator(this, leftType, rightType);
/* 1889 */       return null;
/*      */     }
/*      */ 
/* 1893 */     if ((leftIsCast) || (rightIsCast)) {
/* 1894 */       CastExpression.checkNeedForArgumentCasts(scope, operator, operatorSignature, this.left, leftTypeID, leftIsCast, this.right, rightTypeID, rightIsCast);
/*      */     }
/*      */ 
/* 1897 */     computeConstant(scope, leftTypeID, rightTypeID);
/* 1898 */     return this.resolvedType;
/*      */   }
/*      */ 
/*      */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 1902 */     if (visitor.visit(this, scope)) {
/* 1903 */       this.left.traverse(visitor, scope);
/* 1904 */       this.right.traverse(visitor, scope);
/*      */     }
/* 1906 */     visitor.endVisit(this, scope);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.BinaryExpression
 * JD-Core Version:    0.6.0
 */