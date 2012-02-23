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
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class AssertStatement extends Statement
/*     */ {
/*     */   public Expression assertExpression;
/*     */   public Expression exceptionArgument;
/*  25 */   int preAssertInitStateIndex = -1;
/*     */   private FieldBinding assertionSyntheticFieldBinding;
/*     */ 
/*     */   public AssertStatement(Expression exceptionArgument, Expression assertExpression, int startPosition)
/*     */   {
/*  29 */     this.assertExpression = assertExpression;
/*  30 */     this.exceptionArgument = exceptionArgument;
/*  31 */     this.sourceStart = startPosition;
/*  32 */     this.sourceEnd = exceptionArgument.sourceEnd;
/*     */   }
/*     */ 
/*     */   public AssertStatement(Expression assertExpression, int startPosition) {
/*  36 */     this.assertExpression = assertExpression;
/*  37 */     this.sourceStart = startPosition;
/*  38 */     this.sourceEnd = assertExpression.sourceEnd;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/*  42 */     this.preAssertInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
/*     */ 
/*  44 */     Constant cst = this.assertExpression.optimizedBooleanConstant();
/*  45 */     boolean isOptimizedTrueAssertion = (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  46 */     boolean isOptimizedFalseAssertion = (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  48 */     FlowInfo conditionFlowInfo = this.assertExpression.analyseCode(currentScope, flowContext, flowInfo.copy());
/*  49 */     UnconditionalFlowInfo assertWhenTrueInfo = conditionFlowInfo.initsWhenTrue().unconditionalInits();
/*  50 */     FlowInfo assertInfo = conditionFlowInfo.initsWhenFalse();
/*  51 */     if (isOptimizedTrueAssertion) {
/*  52 */       assertInfo.setReachMode(1);
/*     */     }
/*     */ 
/*  55 */     if (this.exceptionArgument != null)
/*     */     {
/*  57 */       FlowInfo exceptionInfo = this.exceptionArgument.analyseCode(currentScope, flowContext, assertInfo.copy());
/*     */ 
/*  59 */       if (isOptimizedTrueAssertion)
/*  60 */         currentScope.problemReporter().fakeReachable(this.exceptionArgument);
/*     */       else {
/*  62 */         flowContext.checkExceptionHandlers(
/*  63 */           currentScope.getJavaLangAssertionError(), 
/*  64 */           this, 
/*  65 */           exceptionInfo, 
/*  66 */           currentScope);
/*     */       }
/*     */     }
/*     */ 
/*  70 */     if (!isOptimizedTrueAssertion)
/*     */     {
/*  72 */       manageSyntheticAccessIfNecessary(currentScope, flowInfo);
/*     */     }
/*  74 */     if (isOptimizedFalseAssertion) {
/*  75 */       return flowInfo;
/*     */     }
/*     */ 
/*  79 */     return flowInfo.mergedWith(assertInfo.nullInfoLessUnconditionalCopy())
/*  80 */       .addInitializationsFrom(assertWhenTrueInfo.discardInitializationInfo());
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*  87 */     if ((this.bits & 0x80000000) == 0) {
/*  88 */       return;
/*     */     }
/*  90 */     int pc = codeStream.position;
/*     */ 
/*  92 */     if (this.assertionSyntheticFieldBinding != null) {
/*  93 */       BranchLabel assertionActivationLabel = new BranchLabel(codeStream);
/*  94 */       codeStream.fieldAccess(-78, this.assertionSyntheticFieldBinding, null);
/*  95 */       codeStream.ifne(assertionActivationLabel);
/*     */       BranchLabel falseLabel;
/*  98 */       this.assertExpression.generateOptimizedBoolean(currentScope, codeStream, falseLabel = new BranchLabel(codeStream), null, true);
/*  99 */       codeStream.newJavaLangAssertionError();
/* 100 */       codeStream.dup();
/* 101 */       if (this.exceptionArgument != null) {
/* 102 */         this.exceptionArgument.generateCode(currentScope, codeStream, true);
/* 103 */         codeStream.invokeJavaLangAssertionErrorConstructor(this.exceptionArgument.implicitConversion & 0xF);
/*     */       } else {
/* 105 */         codeStream.invokeJavaLangAssertionErrorDefaultConstructor();
/*     */       }
/* 107 */       codeStream.athrow();
/*     */ 
/* 110 */       if (this.preAssertInitStateIndex != -1) {
/* 111 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preAssertInitStateIndex);
/*     */       }
/* 113 */       falseLabel.place();
/* 114 */       assertionActivationLabel.place();
/*     */     }
/* 117 */     else if (this.preAssertInitStateIndex != -1) {
/* 118 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preAssertInitStateIndex);
/*     */     }
/*     */ 
/* 121 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope) {
/* 125 */     this.assertExpression.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
/* 126 */     if (this.exceptionArgument != null) {
/* 127 */       TypeBinding exceptionArgumentType = this.exceptionArgument.resolveType(scope);
/* 128 */       if (exceptionArgumentType != null) {
/* 129 */         int id = exceptionArgumentType.id;
/* 130 */         switch (id) {
/*     */         case 6:
/* 132 */           scope.problemReporter().illegalVoidExpression(this.exceptionArgument);
/*     */         default:
/* 135 */           id = 1;
/*     */         case 2:
/*     */         case 3:
/*     */         case 4:
/*     */         case 5:
/*     */         case 7:
/*     */         case 8:
/*     */         case 9:
/*     */         case 10:
/*     */         case 11:
/*     */         }
/* 146 */         this.exceptionArgument.implicitConversion = ((id << 4) + id);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 153 */     if (visitor.visit(this, scope)) {
/* 154 */       this.assertExpression.traverse(visitor, scope);
/* 155 */       if (this.exceptionArgument != null) {
/* 156 */         this.exceptionArgument.traverse(visitor, scope);
/*     */       }
/*     */     }
/* 159 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
/* 163 */     if ((flowInfo.tagBits & 0x1) == 0)
/*     */     {
/* 166 */       SourceTypeBinding outerMostClass = currentScope.enclosingSourceType();
/* 167 */       while (outerMostClass.isLocalType()) {
/* 168 */         ReferenceBinding enclosing = outerMostClass.enclosingType();
/* 169 */         if ((enclosing == null) || (enclosing.isInterface())) break;
/* 170 */         outerMostClass = (SourceTypeBinding)enclosing;
/*     */       }
/* 172 */       this.assertionSyntheticFieldBinding = outerMostClass.addSyntheticFieldForAssert(currentScope);
/*     */ 
/* 175 */       TypeDeclaration typeDeclaration = outerMostClass.scope.referenceType();
/* 176 */       AbstractMethodDeclaration[] methods = typeDeclaration.methods;
/* 177 */       int i = 0; for (int max = methods.length; i < max; i++) {
/* 178 */         AbstractMethodDeclaration method = methods[i];
/* 179 */         if (method.isClinit()) {
/* 180 */           ((Clinit)method).setAssertionSupport(this.assertionSyntheticFieldBinding, currentScope.compilerOptions().sourceLevel < 3211264L);
/* 181 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int tab, StringBuffer output) {
/* 188 */     printIndent(tab, output);
/* 189 */     output.append("assert ");
/* 190 */     this.assertExpression.printExpression(0, output);
/* 191 */     if (this.exceptionArgument != null) {
/* 192 */       output.append(": ");
/* 193 */       this.exceptionArgument.printExpression(0, output);
/*     */     }
/* 195 */     return output.append(';');
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.AssertStatement
 * JD-Core Version:    0.6.0
 */