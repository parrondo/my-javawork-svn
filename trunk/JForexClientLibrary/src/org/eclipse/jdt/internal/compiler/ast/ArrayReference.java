/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ArrayReference extends Reference
/*     */ {
/*     */   public Expression receiver;
/*     */   public Expression position;
/*     */ 
/*     */   public ArrayReference(Expression rec, Expression pos)
/*     */   {
/*  29 */     this.receiver = rec;
/*  30 */     this.position = pos;
/*  31 */     this.sourceStart = rec.sourceStart;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Assignment assignment, boolean compoundAssignment)
/*     */   {
/*  36 */     if (assignment.expression == null) {
/*  37 */       return analyseCode(currentScope, flowContext, flowInfo);
/*     */     }
/*  39 */     return assignment.expression
/*  41 */       .analyseCode(
/*  42 */       currentScope, 
/*  43 */       flowContext, 
/*  44 */       analyseCode(currentScope, flowContext, flowInfo).unconditionalInits());
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/*  48 */     this.receiver.checkNPE(currentScope, flowContext, flowInfo);
/*  49 */     flowInfo = this.receiver.analyseCode(currentScope, flowContext, flowInfo);
/*  50 */     return this.position.analyseCode(currentScope, flowContext, flowInfo);
/*     */   }
/*     */ 
/*     */   public void generateAssignment(BlockScope currentScope, CodeStream codeStream, Assignment assignment, boolean valueRequired) {
/*  54 */     int pc = codeStream.position;
/*  55 */     this.receiver.generateCode(currentScope, codeStream, true);
/*  56 */     if (((this.receiver instanceof CastExpression)) && 
/*  57 */       (((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL)) {
/*  58 */       codeStream.checkcast(this.receiver.resolvedType);
/*     */     }
/*  60 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*  61 */     this.position.generateCode(currentScope, codeStream, true);
/*  62 */     assignment.expression.generateCode(currentScope, codeStream, true);
/*  63 */     codeStream.arrayAtPut(this.resolvedType.id, valueRequired);
/*  64 */     if (valueRequired)
/*  65 */       codeStream.generateImplicitConversion(assignment.implicitConversion);
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  73 */     int pc = codeStream.position;
/*  74 */     this.receiver.generateCode(currentScope, codeStream, true);
/*  75 */     if (((this.receiver instanceof CastExpression)) && 
/*  76 */       (((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL)) {
/*  77 */       codeStream.checkcast(this.receiver.resolvedType);
/*     */     }
/*  79 */     this.position.generateCode(currentScope, codeStream, true);
/*  80 */     codeStream.arrayAt(this.resolvedType.id);
/*     */ 
/*  82 */     if (valueRequired) {
/*  83 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */     } else {
/*  85 */       boolean isUnboxing = (this.implicitConversion & 0x400) != 0;
/*     */ 
/*  87 */       if (isUnboxing) codeStream.generateImplicitConversion(this.implicitConversion);
/*  88 */       switch (isUnboxing ? postConversionType(currentScope).id : this.resolvedType.id) {
/*     */       case 7:
/*     */       case 8:
/*  91 */         codeStream.pop2();
/*  92 */         break;
/*     */       default:
/*  94 */         codeStream.pop();
/*     */       }
/*     */     }
/*  97 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired) {
/* 101 */     this.receiver.generateCode(currentScope, codeStream, true);
/* 102 */     if (((this.receiver instanceof CastExpression)) && 
/* 103 */       (((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL)) {
/* 104 */       codeStream.checkcast(this.receiver.resolvedType);
/*     */     }
/* 106 */     this.position.generateCode(currentScope, codeStream, true);
/* 107 */     codeStream.dup2();
/* 108 */     codeStream.arrayAt(this.resolvedType.id);
/*     */     int operationTypeID;
/* 110 */     switch (operationTypeID = (this.implicitConversion & 0xFF) >> 4) {
/*     */     case 0:
/*     */     case 1:
/*     */     case 11:
/* 114 */       codeStream.generateStringConcatenationAppend(currentScope, null, expression);
/* 115 */       break;
/*     */     default:
/* 118 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */ 
/* 120 */       if (expression == IntLiteral.One)
/* 121 */         codeStream.generateConstant(expression.constant, this.implicitConversion);
/*     */       else {
/* 123 */         expression.generateCode(currentScope, codeStream, true);
/*     */       }
/*     */ 
/* 126 */       codeStream.sendOperator(operator, operationTypeID);
/*     */ 
/* 128 */       codeStream.generateImplicitConversion(assignmentImplicitConversion);
/*     */     }
/* 130 */     codeStream.arrayAtPut(this.resolvedType.id, valueRequired);
/*     */   }
/*     */ 
/*     */   public void generatePostIncrement(BlockScope currentScope, CodeStream codeStream, CompoundAssignment postIncrement, boolean valueRequired) {
/* 134 */     this.receiver.generateCode(currentScope, codeStream, true);
/* 135 */     if (((this.receiver instanceof CastExpression)) && 
/* 136 */       (((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL)) {
/* 137 */       codeStream.checkcast(this.receiver.resolvedType);
/*     */     }
/* 139 */     this.position.generateCode(currentScope, codeStream, true);
/* 140 */     codeStream.dup2();
/* 141 */     codeStream.arrayAt(this.resolvedType.id);
/* 142 */     if (valueRequired) {
/* 143 */       switch (this.resolvedType.id) {
/*     */       case 7:
/*     */       case 8:
/* 146 */         codeStream.dup2_x2();
/* 147 */         break;
/*     */       default:
/* 149 */         codeStream.dup_x2();
/*     */       }
/*     */     }
/*     */ 
/* 153 */     codeStream.generateImplicitConversion(this.implicitConversion);
/* 154 */     codeStream.generateConstant(
/* 155 */       postIncrement.expression.constant, 
/* 156 */       this.implicitConversion);
/* 157 */     codeStream.sendOperator(postIncrement.operator, this.implicitConversion & 0xF);
/* 158 */     codeStream.generateImplicitConversion(
/* 159 */       postIncrement.preAssignImplicitConversion);
/* 160 */     codeStream.arrayAtPut(this.resolvedType.id, false);
/*     */   }
/*     */ 
/*     */   public int nullStatus(FlowInfo flowInfo) {
/* 164 */     return 0;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 168 */     this.receiver.printExpression(0, output).append('[');
/* 169 */     return this.position.printExpression(0, output).append(']');
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope) {
/* 173 */     this.constant = Constant.NotAConstant;
/* 174 */     if (((this.receiver instanceof CastExpression)) && 
/* 175 */       ((((CastExpression)this.receiver).innermostCastedExpression() instanceof NullLiteral))) {
/* 176 */       this.receiver.bits |= 32;
/*     */     }
/* 178 */     TypeBinding arrayType = this.receiver.resolveType(scope);
/* 179 */     if (arrayType != null) {
/* 180 */       this.receiver.computeConversion(scope, arrayType, arrayType);
/* 181 */       if (arrayType.isArrayType()) {
/* 182 */         TypeBinding elementType = ((ArrayBinding)arrayType).elementsType();
/* 183 */         this.resolvedType = ((this.bits & 0x2000) == 0 ? elementType.capture(scope, this.sourceEnd) : elementType);
/*     */       } else {
/* 185 */         scope.problemReporter().referenceMustBeArrayTypeAt(arrayType, this);
/*     */       }
/*     */     }
/* 188 */     TypeBinding positionType = this.position.resolveTypeExpecting(scope, TypeBinding.INT);
/* 189 */     if (positionType != null) {
/* 190 */       this.position.computeConversion(scope, TypeBinding.INT, positionType);
/*     */     }
/* 192 */     return this.resolvedType;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 196 */     if (visitor.visit(this, scope)) {
/* 197 */       this.receiver.traverse(visitor, scope);
/* 198 */       this.position.traverse(visitor, scope);
/*     */     }
/* 200 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ArrayReference
 * JD-Core Version:    0.6.0
 */