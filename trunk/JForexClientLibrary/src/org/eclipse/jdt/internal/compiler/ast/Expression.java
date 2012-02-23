/*      */ package org.eclipse.jdt.internal.compiler.ast;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*      */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*      */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*      */ import org.eclipse.jdt.internal.compiler.flow.NullInfoRegistry;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
/*      */ import org.eclipse.jdt.internal.compiler.util.Messages;
/*      */ 
/*      */ public abstract class Expression extends Statement
/*      */ {
/*      */   public Constant constant;
/*   44 */   public int statementEnd = -1;
/*      */   public int implicitConversion;
/*      */   public TypeBinding resolvedType;
/*      */ 
/*      */   public static final boolean isConstantValueRepresentable(Constant constant, int constantTypeID, int targetTypeID)
/*      */   {
/*   57 */     if (targetTypeID == constantTypeID)
/*   58 */       return true;
/*   59 */     switch (targetTypeID) {
/*      */     case 2:
/*   61 */       switch (constantTypeID) {
/*      */       case 2:
/*   63 */         return true;
/*      */       case 8:
/*   65 */         return constant.doubleValue() == constant.charValue();
/*      */       case 9:
/*   67 */         return constant.floatValue() == constant.charValue();
/*      */       case 10:
/*   69 */         return constant.intValue() == constant.charValue();
/*      */       case 4:
/*   71 */         return constant.shortValue() == constant.charValue();
/*      */       case 3:
/*   73 */         return constant.byteValue() == constant.charValue();
/*      */       case 7:
/*   75 */         return constant.longValue() == constant.charValue();
/*      */       case 5:
/*   77 */       case 6: } return false;
/*      */     case 9:
/*   81 */       switch (constantTypeID) {
/*      */       case 2:
/*   83 */         return constant.charValue() == constant.floatValue();
/*      */       case 8:
/*   85 */         return constant.doubleValue() == constant.floatValue();
/*      */       case 9:
/*   87 */         return true;
/*      */       case 10:
/*   89 */         return constant.intValue() == constant.floatValue();
/*      */       case 4:
/*   91 */         return constant.shortValue() == constant.floatValue();
/*      */       case 3:
/*   93 */         return constant.byteValue() == constant.floatValue();
/*      */       case 7:
/*   95 */         return (float)constant.longValue() == constant.floatValue();
/*      */       case 5:
/*   97 */       case 6: } return false;
/*      */     case 8:
/*  101 */       switch (constantTypeID) {
/*      */       case 2:
/*  103 */         return constant.charValue() == constant.doubleValue();
/*      */       case 8:
/*  105 */         return true;
/*      */       case 9:
/*  107 */         return constant.floatValue() == constant.doubleValue();
/*      */       case 10:
/*  109 */         return constant.intValue() == constant.doubleValue();
/*      */       case 4:
/*  111 */         return constant.shortValue() == constant.doubleValue();
/*      */       case 3:
/*  113 */         return constant.byteValue() == constant.doubleValue();
/*      */       case 7:
/*  115 */         return constant.longValue() == constant.doubleValue();
/*      */       case 5:
/*  117 */       case 6: } return false;
/*      */     case 3:
/*  121 */       switch (constantTypeID) {
/*      */       case 2:
/*  123 */         return constant.charValue() == constant.byteValue();
/*      */       case 8:
/*  125 */         return constant.doubleValue() == constant.byteValue();
/*      */       case 9:
/*  127 */         return constant.floatValue() == constant.byteValue();
/*      */       case 10:
/*  129 */         return constant.intValue() == constant.byteValue();
/*      */       case 4:
/*  131 */         return constant.shortValue() == constant.byteValue();
/*      */       case 3:
/*  133 */         return true;
/*      */       case 7:
/*  135 */         return constant.longValue() == constant.byteValue();
/*      */       case 5:
/*  137 */       case 6: } return false;
/*      */     case 4:
/*  141 */       switch (constantTypeID) {
/*      */       case 2:
/*  143 */         return constant.charValue() == constant.shortValue();
/*      */       case 8:
/*  145 */         return constant.doubleValue() == constant.shortValue();
/*      */       case 9:
/*  147 */         return constant.floatValue() == constant.shortValue();
/*      */       case 10:
/*  149 */         return constant.intValue() == constant.shortValue();
/*      */       case 4:
/*  151 */         return true;
/*      */       case 3:
/*  153 */         return constant.byteValue() == constant.shortValue();
/*      */       case 7:
/*  155 */         return constant.longValue() == constant.shortValue();
/*      */       case 5:
/*  157 */       case 6: } return false;
/*      */     case 10:
/*  161 */       switch (constantTypeID) {
/*      */       case 2:
/*  163 */         return constant.charValue() == constant.intValue();
/*      */       case 8:
/*  165 */         return constant.doubleValue() == constant.intValue();
/*      */       case 9:
/*  167 */         return constant.floatValue() == constant.intValue();
/*      */       case 10:
/*  169 */         return true;
/*      */       case 4:
/*  171 */         return constant.shortValue() == constant.intValue();
/*      */       case 3:
/*  173 */         return constant.byteValue() == constant.intValue();
/*      */       case 7:
/*  175 */         return constant.longValue() == constant.intValue();
/*      */       case 5:
/*  177 */       case 6: } return false;
/*      */     case 7:
/*  181 */       switch (constantTypeID) {
/*      */       case 2:
/*  183 */         return constant.charValue() == constant.longValue();
/*      */       case 8:
/*  185 */         return constant.doubleValue() == constant.longValue();
/*      */       case 9:
/*  187 */         return constant.floatValue() == (float)constant.longValue();
/*      */       case 10:
/*  189 */         return constant.intValue() == constant.longValue();
/*      */       case 4:
/*  191 */         return constant.shortValue() == constant.longValue();
/*      */       case 3:
/*  193 */         return constant.byteValue() == constant.longValue();
/*      */       case 7:
/*  195 */         return true;
/*      */       case 5:
/*  197 */       case 6: } return false;
/*      */     case 5:
/*      */     case 6:
/*      */     }
/*  201 */     return false;
/*      */   }
/*      */ 
/*      */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*      */   {
/*  210 */     return flowInfo;
/*      */   }
/*      */ 
/*      */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, boolean valueRequired)
/*      */   {
/*  224 */     return analyseCode(currentScope, flowContext, flowInfo);
/*      */   }
/*      */ 
/*      */   public final boolean checkCastTypesCompatibility(Scope scope, TypeBinding castType, TypeBinding expressionType, Expression expression)
/*      */   {
/*  238 */     if ((castType == null) || (expressionType == null)) return true;
/*      */ 
/*  242 */     boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
/*  243 */     if (castType.isBaseType()) {
/*  244 */       if (expressionType.isBaseType()) {
/*  245 */         if (expressionType == castType) {
/*  246 */           if (expression != null) {
/*  247 */             this.constant = expression.constant;
/*      */           }
/*  249 */           tagAsUnnecessaryCast(scope, castType);
/*  250 */           return true;
/*      */         }
/*  252 */         boolean necessary = false;
/*  253 */         if ((expressionType.isCompatibleWith(castType)) || 
/*  254 */           ((necessary = BaseTypeBinding.isNarrowing(castType.id, expressionType.id)))) {
/*  255 */           if (expression != null) {
/*  256 */             expression.implicitConversion = ((castType.id << 4) + expressionType.id);
/*  257 */             if (expression.constant != Constant.NotAConstant) {
/*  258 */               this.constant = expression.constant.castTo(expression.implicitConversion);
/*      */             }
/*      */           }
/*  261 */           if (!necessary) tagAsUnnecessaryCast(scope, castType);
/*  262 */           return true;
/*      */         }
/*      */       }
/*  265 */       else if ((use15specifics) && 
/*  266 */         (scope.environment().computeBoxingType(expressionType).isCompatibleWith(castType))) {
/*  267 */         tagAsUnnecessaryCast(scope, castType);
/*  268 */         return true;
/*      */       }
/*  270 */       return false;
/*  271 */     }if ((use15specifics) && 
/*  272 */       (expressionType.isBaseType()) && 
/*  273 */       (scope.environment().computeBoxingType(expressionType).isCompatibleWith(castType))) {
/*  274 */       tagAsUnnecessaryCast(scope, castType);
/*  275 */       return true;
/*      */     }
/*      */ 
/*  278 */     switch (expressionType.kind())
/*      */     {
/*      */     case 132:
/*  281 */       if (expressionType == TypeBinding.NULL) {
/*  282 */         tagAsUnnecessaryCast(scope, castType);
/*  283 */         return true;
/*      */       }
/*  285 */       return false;
/*      */     case 68:
/*  288 */       if (castType == expressionType) {
/*  289 */         tagAsUnnecessaryCast(scope, castType);
/*  290 */         return true;
/*      */       }
/*  292 */       switch (castType.kind())
/*      */       {
/*      */       case 68:
/*  295 */         TypeBinding castElementType = ((ArrayBinding)castType).elementsType();
/*  296 */         TypeBinding exprElementType = ((ArrayBinding)expressionType).elementsType();
/*  297 */         if ((exprElementType.isBaseType()) || (castElementType.isBaseType())) {
/*  298 */           if (castElementType == exprElementType) {
/*  299 */             tagAsNeedCheckCast();
/*  300 */             return true;
/*      */           }
/*  302 */           return false;
/*      */         }
/*      */ 
/*  305 */         return checkCastTypesCompatibility(scope, castElementType, exprElementType, expression);
/*      */       case 4100:
/*  309 */         TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
/*  310 */         if (match == null) {
/*  311 */           checkUnsafeCast(scope, castType, expressionType, null, true);
/*      */         }
/*      */ 
/*  314 */         return checkCastTypesCompatibility(scope, ((TypeVariableBinding)castType).upperBound(), expressionType, expression);
/*      */       }
/*      */ 
/*  318 */       switch (castType.id) {
/*      */       case 36:
/*      */       case 37:
/*  321 */         tagAsNeedCheckCast();
/*  322 */         return true;
/*      */       case 1:
/*  324 */         tagAsUnnecessaryCast(scope, castType);
/*  325 */         return true;
/*      */       }
/*  327 */       return false;
/*      */     case 4100:
/*  332 */       TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
/*  333 */       if (match != null) {
/*  334 */         return checkUnsafeCast(scope, castType, expressionType, match, false);
/*      */       }
/*      */ 
/*  337 */       return checkCastTypesCompatibility(scope, castType, ((TypeVariableBinding)expressionType).upperBound(), expression);
/*      */     case 516:
/*      */     case 8196:
/*  341 */       TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
/*  342 */       if (match != null) {
/*  343 */         return checkUnsafeCast(scope, castType, expressionType, match, false);
/*      */       }
/*      */ 
/*  346 */       return checkCastTypesCompatibility(scope, castType, ((WildcardBinding)expressionType).bound, expression);
/*      */     }
/*      */ 
/*  349 */     if (expressionType.isInterface()) {
/*  350 */       switch (castType.kind())
/*      */       {
/*      */       case 68:
/*  353 */         switch (expressionType.id) {
/*      */         case 36:
/*      */         case 37:
/*  356 */           tagAsNeedCheckCast();
/*  357 */           return true;
/*      */         }
/*  359 */         return false;
/*      */       case 4100:
/*  364 */         TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
/*  365 */         if (match == null) {
/*  366 */           checkUnsafeCast(scope, castType, expressionType, null, true);
/*      */         }
/*      */ 
/*  369 */         return checkCastTypesCompatibility(scope, ((TypeVariableBinding)castType).upperBound(), expressionType, expression);
/*      */       }
/*      */ 
/*  372 */       if (castType.isInterface())
/*      */       {
/*  374 */         ReferenceBinding interfaceType = (ReferenceBinding)expressionType;
/*  375 */         TypeBinding match = interfaceType.findSuperTypeOriginatingFrom(castType);
/*  376 */         if (match != null) {
/*  377 */           return checkUnsafeCast(scope, castType, interfaceType, match, false);
/*      */         }
/*  379 */         tagAsNeedCheckCast();
/*  380 */         match = castType.findSuperTypeOriginatingFrom(interfaceType);
/*  381 */         if (match != null) {
/*  382 */           return checkUnsafeCast(scope, castType, interfaceType, match, true);
/*      */         }
/*  384 */         if (use15specifics) {
/*  385 */           checkUnsafeCast(scope, castType, expressionType, null, true);
/*      */ 
/*  387 */           if (interfaceType.hasIncompatibleSuperType((ReferenceBinding)castType))
/*  388 */             return false;
/*      */         }
/*      */         else {
/*  391 */           MethodBinding[] castTypeMethods = getAllInheritedMethods((ReferenceBinding)castType);
/*  392 */           MethodBinding[] expressionTypeMethods = getAllInheritedMethods((ReferenceBinding)expressionType);
/*  393 */           int exprMethodsLength = expressionTypeMethods.length;
/*  394 */           int i = 0; for (int castMethodsLength = castTypeMethods.length; i < castMethodsLength; i++) {
/*  395 */             for (int j = 0; j < exprMethodsLength; j++) {
/*  396 */               if ((castTypeMethods[i].returnType != expressionTypeMethods[j].returnType) && 
/*  397 */                 (CharOperation.equals(castTypeMethods[i].selector, expressionTypeMethods[j].selector)) && 
/*  398 */                 (castTypeMethods[i].areParametersEqual(expressionTypeMethods[j]))) {
/*  399 */                 return false;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  405 */         return true;
/*      */       }
/*      */ 
/*  408 */       if (castType.id == 1) {
/*  409 */         tagAsUnnecessaryCast(scope, castType);
/*  410 */         return true;
/*      */       }
/*      */ 
/*  413 */       tagAsNeedCheckCast();
/*  414 */       TypeBinding match = castType.findSuperTypeOriginatingFrom(expressionType);
/*  415 */       if (match != null) {
/*  416 */         return checkUnsafeCast(scope, castType, expressionType, match, true);
/*      */       }
/*  418 */       if (((ReferenceBinding)castType).isFinal())
/*      */       {
/*  420 */         return false;
/*      */       }
/*  422 */       if (use15specifics) {
/*  423 */         checkUnsafeCast(scope, castType, expressionType, null, true);
/*      */ 
/*  425 */         if (((ReferenceBinding)castType).hasIncompatibleSuperType((ReferenceBinding)expressionType)) {
/*  426 */           return false;
/*      */         }
/*      */       }
/*  429 */       return true;
/*      */     }
/*      */ 
/*  433 */     switch (castType.kind())
/*      */     {
/*      */     case 68:
/*  436 */       if (expressionType.id == 1) {
/*  437 */         if (use15specifics) checkUnsafeCast(scope, castType, expressionType, expressionType, true);
/*  438 */         tagAsNeedCheckCast();
/*  439 */         return true;
/*      */       }
/*  441 */       return false;
/*      */     case 4100:
/*  445 */       TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
/*  446 */       if (match == null) {
/*  447 */         checkUnsafeCast(scope, castType, expressionType, match, true);
/*      */       }
/*      */ 
/*  450 */       return checkCastTypesCompatibility(scope, ((TypeVariableBinding)castType).upperBound(), expressionType, expression);
/*      */     }
/*      */ 
/*  453 */     if (castType.isInterface())
/*      */     {
/*  455 */       ReferenceBinding refExprType = (ReferenceBinding)expressionType;
/*  456 */       TypeBinding match = refExprType.findSuperTypeOriginatingFrom(castType);
/*  457 */       if (match != null) {
/*  458 */         return checkUnsafeCast(scope, castType, expressionType, match, false);
/*      */       }
/*      */ 
/*  461 */       if (refExprType.isFinal()) {
/*  462 */         return false;
/*      */       }
/*  464 */       tagAsNeedCheckCast();
/*  465 */       match = castType.findSuperTypeOriginatingFrom(expressionType);
/*  466 */       if (match != null) {
/*  467 */         return checkUnsafeCast(scope, castType, expressionType, match, true);
/*      */       }
/*  469 */       if (use15specifics) {
/*  470 */         checkUnsafeCast(scope, castType, expressionType, null, true);
/*      */ 
/*  472 */         if (refExprType.hasIncompatibleSuperType((ReferenceBinding)castType))
/*  473 */           return false;
/*      */       }
/*  475 */       return true;
/*      */     }
/*      */ 
/*  478 */     TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
/*  479 */     if (match != null) {
/*  480 */       if ((expression != null) && (castType.id == 11)) this.constant = expression.constant;
/*  481 */       return checkUnsafeCast(scope, castType, expressionType, match, false);
/*      */     }
/*  483 */     match = castType.findSuperTypeOriginatingFrom(expressionType);
/*  484 */     if (match != null) {
/*  485 */       tagAsNeedCheckCast();
/*  486 */       return checkUnsafeCast(scope, castType, expressionType, match, true);
/*      */     }
/*  488 */     return false;
/*      */   }
/*      */ 
/*      */   public void checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo)
/*      */   {
/*  504 */     LocalVariableBinding local = localVariableBinding();
/*  505 */     if ((local != null) && 
/*  506 */       ((local.type.tagBits & 0x2) == 0L)) {
/*  507 */       if ((this.bits & 0x20000) == 0) {
/*  508 */         flowContext.recordUsingNullReference(scope, local, this, 
/*  509 */           3, flowInfo);
/*      */       }
/*  511 */       flowInfo.markAsComparedEqualToNonNull(local);
/*      */ 
/*  513 */       if (flowContext.initsOnFinally != null)
/*  514 */         flowContext.initsOnFinally.markAsComparedEqualToNonNull(local);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean checkUnsafeCast(Scope scope, TypeBinding castType, TypeBinding expressionType, TypeBinding match, boolean isNarrowing)
/*      */   {
/*  520 */     if (match == castType) {
/*  521 */       if (!isNarrowing) tagAsUnnecessaryCast(scope, castType);
/*  522 */       return true;
/*      */     }
/*  524 */     if ((match != null) && ((castType.isBoundParameterizedType()) || (expressionType.isBoundParameterizedType())) && 
/*  525 */       (isNarrowing ? 
/*  526 */       match.isProvablyDistinct(expressionType) : 
/*  527 */       castType.isProvablyDistinct(match))) {
/*  528 */       return false;
/*      */     }
/*      */ 
/*  531 */     if (!isNarrowing) tagAsUnnecessaryCast(scope, castType);
/*  532 */     return true;
/*      */   }
/*      */ 
/*      */   public void computeConversion(Scope scope, TypeBinding runtimeType, TypeBinding compileTimeType)
/*      */   {
/*  540 */     if ((runtimeType == null) || (compileTimeType == null))
/*  541 */       return;
/*  542 */     if (this.implicitConversion != 0) return;
/*      */ 
/*  547 */     if ((runtimeType != TypeBinding.NULL) && (runtimeType.isBaseType())) {
/*  548 */       if (!compileTimeType.isBaseType()) {
/*  549 */         TypeBinding unboxedType = scope.environment().computeBoxingType(compileTimeType);
/*  550 */         this.implicitConversion = 1024;
/*  551 */         scope.problemReporter().autoboxing(this, compileTimeType, runtimeType);
/*  552 */         compileTimeType = unboxedType;
/*      */       }
/*      */     } else {
/*  554 */       if ((compileTimeType != TypeBinding.NULL) && (compileTimeType.isBaseType())) {
/*  555 */         TypeBinding boxedType = scope.environment().computeBoxingType(runtimeType);
/*  556 */         if (boxedType == runtimeType)
/*  557 */           boxedType = compileTimeType;
/*  558 */         this.implicitConversion = (0x200 | (boxedType.id << 4) + compileTimeType.id);
/*  559 */         scope.problemReporter().autoboxing(this, compileTimeType, scope.environment().computeBoxingType(boxedType));
/*  560 */         return;
/*  561 */       }if ((this.constant != Constant.NotAConstant) && (this.constant.typeID() != 11)) {
/*  562 */         this.implicitConversion = 512;
/*  563 */         return;
/*      */       }
/*      */     }
/*      */     int compileTimeTypeID;
/*  566 */     if ((compileTimeTypeID = compileTimeType.id) == 2147483647)
/*  567 */       compileTimeTypeID = compileTimeType.erasure().id == 11 ? 11 : 1;
/*      */     int runtimeTypeID;
/*  569 */     switch (runtimeTypeID = runtimeType.id) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*  573 */       this.implicitConversion |= 160 + compileTimeTypeID;
/*  574 */       break;
/*      */     case 5:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*  581 */       this.implicitConversion |= (runtimeTypeID << 4) + compileTimeTypeID;
/*      */     case 6:
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*      */   {
/*  598 */     if ((this.bits & 0x80000000) == 0) {
/*  599 */       return;
/*      */     }
/*  601 */     generateCode(currentScope, codeStream, false);
/*      */   }
/*      */ 
/*      */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*      */   {
/*  612 */     if (this.constant != Constant.NotAConstant)
/*      */     {
/*  614 */       int pc = codeStream.position;
/*  615 */       codeStream.generateConstant(this.constant, this.implicitConversion);
/*  616 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/*      */     }
/*      */     else {
/*  619 */       throw new ShouldNotImplement(Messages.ast_missingCode);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*      */   {
/*  635 */     Constant cst = optimizedBooleanConstant();
/*  636 */     generateCode(currentScope, codeStream, (valueRequired) && (cst == Constant.NotAConstant));
/*  637 */     if ((cst != Constant.NotAConstant) && (cst.typeID() == 5)) {
/*  638 */       int pc = codeStream.position;
/*  639 */       if (cst.booleanValue())
/*      */       {
/*  641 */         if ((valueRequired) && 
/*  642 */           (falseLabel == null))
/*      */         {
/*  644 */           if (trueLabel != null) {
/*  645 */             codeStream.goto_(trueLabel);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*  650 */       else if ((valueRequired) && 
/*  651 */         (falseLabel != null))
/*      */       {
/*  653 */         if (trueLabel == null) {
/*  654 */           codeStream.goto_(falseLabel);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  659 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/*  660 */       return;
/*      */     }
/*      */ 
/*  663 */     int position = codeStream.position;
/*  664 */     if (valueRequired) {
/*  665 */       if (falseLabel == null) {
/*  666 */         if (trueLabel != null)
/*      */         {
/*  668 */           codeStream.ifne(trueLabel);
/*      */         }
/*      */       }
/*  671 */       else if (trueLabel == null)
/*      */       {
/*  673 */         codeStream.ifeq(falseLabel);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  680 */     codeStream.updateLastRecordedEndPC(currentScope, position);
/*      */   }
/*      */ 
/*      */   public void generateOptimizedStringConcatenation(BlockScope blockScope, CodeStream codeStream, int typeID)
/*      */   {
/*  689 */     if ((typeID == 11) && (this.constant != Constant.NotAConstant) && (this.constant.stringValue().length() == 0)) {
/*  690 */       return;
/*      */     }
/*  692 */     generateCode(blockScope, codeStream, true);
/*  693 */     codeStream.invokeStringConcatenationAppendForType(typeID);
/*      */   }
/*      */ 
/*      */   public void generateOptimizedStringConcatenationCreation(BlockScope blockScope, CodeStream codeStream, int typeID)
/*      */   {
/*  701 */     codeStream.newStringContatenation();
/*  702 */     codeStream.dup();
/*  703 */     switch (typeID)
/*      */     {
/*      */     case 0:
/*      */     case 1:
/*  708 */       codeStream.invokeStringConcatenationDefaultConstructor();
/*  709 */       generateCode(blockScope, codeStream, true);
/*  710 */       codeStream.invokeStringConcatenationAppendForType(1);
/*  711 */       return;
/*      */     case 11:
/*      */     case 12:
/*  714 */       if (this.constant != Constant.NotAConstant) {
/*  715 */         String stringValue = this.constant.stringValue();
/*  716 */         if (stringValue.length() == 0) {
/*  717 */           codeStream.invokeStringConcatenationDefaultConstructor();
/*  718 */           return;
/*      */         }
/*  720 */         codeStream.ldc(stringValue);
/*      */       }
/*      */       else {
/*  723 */         generateCode(blockScope, codeStream, true);
/*  724 */         codeStream.invokeStringValueOf(1);
/*      */       }
/*  726 */       break;
/*      */     default:
/*  728 */       generateCode(blockScope, codeStream, true);
/*  729 */       codeStream.invokeStringValueOf(typeID);
/*      */     }
/*  731 */     codeStream.invokeStringConcatenationStringConstructor();
/*      */   }
/*      */ 
/*      */   private MethodBinding[] getAllInheritedMethods(ReferenceBinding binding) {
/*  735 */     ArrayList collector = new ArrayList();
/*  736 */     getAllInheritedMethods0(binding, collector);
/*  737 */     return (MethodBinding[])collector.toArray(new MethodBinding[collector.size()]);
/*      */   }
/*      */ 
/*      */   private void getAllInheritedMethods0(ReferenceBinding binding, ArrayList collector) {
/*  741 */     if (!binding.isInterface()) return;
/*  742 */     MethodBinding[] methodBindings = binding.methods();
/*  743 */     int i = 0; for (int max = methodBindings.length; i < max; i++) {
/*  744 */       collector.add(methodBindings[i]);
/*      */     }
/*  746 */     ReferenceBinding[] superInterfaces = binding.superInterfaces();
/*  747 */     int i = 0; for (int max = superInterfaces.length; i < max; i++)
/*  748 */       getAllInheritedMethods0(superInterfaces[i], collector);
/*      */   }
/*      */ 
/*      */   public static Binding getDirectBinding(Expression someExpression)
/*      */   {
/*  753 */     if ((someExpression.bits & 0x20000000) != 0) {
/*  754 */       return null;
/*      */     }
/*  756 */     if ((someExpression instanceof SingleNameReference))
/*  757 */       return ((SingleNameReference)someExpression).binding;
/*  758 */     if ((someExpression instanceof FieldReference)) {
/*  759 */       FieldReference fieldRef = (FieldReference)someExpression;
/*  760 */       if ((fieldRef.receiver.isThis()) && (!(fieldRef.receiver instanceof QualifiedThisReference)))
/*  761 */         return fieldRef.binding;
/*      */     }
/*  763 */     else if ((someExpression instanceof Assignment)) {
/*  764 */       Expression lhs = ((Assignment)someExpression).lhs;
/*  765 */       if ((lhs.bits & 0x2000) != 0)
/*      */       {
/*  767 */         return getDirectBinding(((Assignment)someExpression).lhs);
/*  768 */       }if ((someExpression instanceof PrefixExpression))
/*      */       {
/*  770 */         return getDirectBinding(((Assignment)someExpression).lhs);
/*      */       }
/*  772 */     } else if ((someExpression instanceof QualifiedNameReference)) {
/*  773 */       QualifiedNameReference qualifiedNameReference = (QualifiedNameReference)someExpression;
/*  774 */       if ((qualifiedNameReference.indexOfFirstFieldBinding != 1) && 
/*  775 */         (qualifiedNameReference.otherBindings == null))
/*      */       {
/*  777 */         return qualifiedNameReference.binding;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  783 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isCompactableOperation() {
/*  787 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isConstantValueOfTypeAssignableToType(TypeBinding constantType, TypeBinding targetType)
/*      */   {
/*  796 */     if (this.constant == Constant.NotAConstant)
/*  797 */       return false;
/*  798 */     if (constantType == targetType) {
/*  799 */       return true;
/*      */     }
/*  801 */     if ((BaseTypeBinding.isWidening(10, constantType.id)) && 
/*  802 */       (BaseTypeBinding.isNarrowing(targetType.id, 10)))
/*      */     {
/*  804 */       return isConstantValueRepresentable(this.constant, constantType.id, targetType.id);
/*      */     }
/*  806 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isTypeReference() {
/*  810 */     return false;
/*      */   }
/*      */ 
/*      */   public LocalVariableBinding localVariableBinding()
/*      */   {
/*  818 */     return null;
/*      */   }
/*      */ 
/*      */   public void markAsNonNull()
/*      */   {
/*  827 */     this.bits |= 131072;
/*      */   }
/*      */ 
/*      */   public int nullStatus(FlowInfo flowInfo)
/*      */   {
/*  833 */     if ((this.constant != null) && (this.constant != Constant.NotAConstant)) {
/*  834 */       return -1;
/*      */     }
/*  836 */     LocalVariableBinding local = localVariableBinding();
/*  837 */     if (local != null) {
/*  838 */       if (flowInfo.isDefinitelyNull(local))
/*  839 */         return 1;
/*  840 */       if (flowInfo.isDefinitelyNonNull(local))
/*  841 */         return -1;
/*  842 */       return 0;
/*      */     }
/*  844 */     return -1;
/*      */   }
/*      */ 
/*      */   public Constant optimizedBooleanConstant()
/*      */   {
/*  855 */     return this.constant;
/*      */   }
/*      */ 
/*      */   public TypeBinding postConversionType(Scope scope)
/*      */   {
/*  865 */     TypeBinding convertedType = this.resolvedType;
/*  866 */     int runtimeType = (this.implicitConversion & 0xFF) >> 4;
/*  867 */     switch (runtimeType) {
/*      */     case 5:
/*  869 */       convertedType = TypeBinding.BOOLEAN;
/*  870 */       break;
/*      */     case 3:
/*  872 */       convertedType = TypeBinding.BYTE;
/*  873 */       break;
/*      */     case 4:
/*  875 */       convertedType = TypeBinding.SHORT;
/*  876 */       break;
/*      */     case 2:
/*  878 */       convertedType = TypeBinding.CHAR;
/*  879 */       break;
/*      */     case 10:
/*  881 */       convertedType = TypeBinding.INT;
/*  882 */       break;
/*      */     case 9:
/*  884 */       convertedType = TypeBinding.FLOAT;
/*  885 */       break;
/*      */     case 7:
/*  887 */       convertedType = TypeBinding.LONG;
/*  888 */       break;
/*      */     case 8:
/*  890 */       convertedType = TypeBinding.DOUBLE;
/*      */     case 6:
/*      */     }
/*      */ 
/*  894 */     if ((this.implicitConversion & 0x200) != 0) {
/*  895 */       convertedType = scope.environment().computeBoxingType(convertedType);
/*      */     }
/*  897 */     return convertedType;
/*      */   }
/*      */ 
/*      */   public StringBuffer print(int indent, StringBuffer output) {
/*  901 */     printIndent(indent, output);
/*  902 */     return printExpression(indent, output);
/*      */   }
/*      */   public abstract StringBuffer printExpression(int paramInt, StringBuffer paramStringBuffer);
/*      */ 
/*      */   public StringBuffer printStatement(int indent, StringBuffer output) {
/*  908 */     return print(indent, output).append(";");
/*      */   }
/*      */ 
/*      */   public void resolve(BlockScope scope)
/*      */   {
/*  913 */     resolveType(scope);
/*      */   }
/*      */ 
/*      */   public TypeBinding resolveType(BlockScope scope)
/*      */   {
/*  926 */     return null;
/*      */   }
/*      */ 
/*      */   public TypeBinding resolveType(ClassScope scope)
/*      */   {
/*  938 */     return null;
/*      */   }
/*      */ 
/*      */   public TypeBinding resolveTypeExpecting(BlockScope scope, TypeBinding expectedType) {
/*  942 */     setExpectedType(expectedType);
/*  943 */     TypeBinding expressionType = resolveType(scope);
/*  944 */     if (expressionType == null) return null;
/*  945 */     if (expressionType == expectedType) return expressionType;
/*      */ 
/*  947 */     if (!expressionType.isCompatibleWith(expectedType)) {
/*  948 */       if (scope.isBoxingCompatibleWith(expressionType, expectedType)) {
/*  949 */         computeConversion(scope, expectedType, expressionType);
/*      */       } else {
/*  951 */         scope.problemReporter().typeMismatchError(expressionType, expectedType, this, null);
/*  952 */         return null;
/*      */       }
/*      */     }
/*  955 */     return expressionType;
/*      */   }
/*      */ 
/*      */   public Object reusableJSRTarget()
/*      */   {
/*  964 */     if (this.constant != Constant.NotAConstant)
/*  965 */       return this.constant;
/*  966 */     return null;
/*      */   }
/*      */ 
/*      */   public void setExpectedType(TypeBinding expectedType)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void tagAsNeedCheckCast()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void tagAsUnnecessaryCast(Scope scope, TypeBinding castType)
/*      */   {
/*      */   }
/*      */ 
/*      */   public Expression toTypeReference()
/*      */   {
/* 1003 */     return this;
/*      */   }
/*      */ 
/*      */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void traverse(ASTVisitor visitor, ClassScope scope)
/*      */   {
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Expression
 * JD-Core Version:    0.6.0
 */