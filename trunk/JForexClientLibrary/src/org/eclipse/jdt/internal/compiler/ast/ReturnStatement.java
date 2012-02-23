/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ReturnStatement extends Statement
/*     */ {
/*     */   public Expression expression;
/*     */   public SubRoutineStatement[] subroutines;
/*     */   public LocalVariableBinding saveValueVariable;
/*  24 */   public int initStateIndex = -1;
/*     */ 
/*     */   public ReturnStatement(Expression expression, int sourceStart, int sourceEnd) {
/*  27 */     this.sourceStart = sourceStart;
/*  28 */     this.sourceEnd = sourceEnd;
/*  29 */     this.expression = expression;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  37 */     if (this.expression != null) {
/*  38 */       flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo);
/*     */     }
/*  40 */     this.initStateIndex = 
/*  41 */       currentScope.methodScope().recordInitializationStates(flowInfo);
/*     */ 
/*  43 */     FlowContext traversedContext = flowContext;
/*  44 */     int subCount = 0;
/*  45 */     boolean saveValueNeeded = false;
/*  46 */     boolean hasValueToSave = needValueStore();
/*     */     do
/*     */     {
/*     */       SubRoutineStatement sub;
/*  49 */       if ((sub = traversedContext.subroutine()) != null) {
/*  50 */         if (this.subroutines == null) {
/*  51 */           this.subroutines = new SubRoutineStatement[5];
/*     */         }
/*  53 */         if (subCount == this.subroutines.length) {
/*  54 */           System.arraycopy(this.subroutines, 0, this.subroutines = new SubRoutineStatement[subCount * 2], 0, subCount);
/*     */         }
/*  56 */         this.subroutines[(subCount++)] = sub;
/*  57 */         if (sub.isSubRoutineEscaping()) {
/*  58 */           saveValueNeeded = false;
/*  59 */           this.bits |= 536870912;
/*  60 */           break;
/*     */         }
/*     */       }
/*  63 */       traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
/*     */ 
/*  65 */       if ((traversedContext instanceof InsideSubRoutineFlowContext)) {
/*  66 */         ASTNode node = traversedContext.associatedNode;
/*  67 */         if ((node instanceof SynchronizedStatement)) {
/*  68 */           this.bits |= 1073741824;
/*  69 */         } else if ((node instanceof TryStatement)) {
/*  70 */           TryStatement tryStatement = (TryStatement)node;
/*  71 */           flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
/*  72 */           if (hasValueToSave) {
/*  73 */             if (this.saveValueVariable == null) {
/*  74 */               prepareSaveValueLocation(tryStatement);
/*     */             }
/*  76 */             saveValueNeeded = true;
/*  77 */             this.initStateIndex = 
/*  78 */               currentScope.methodScope().recordInitializationStates(flowInfo);
/*     */           }
/*     */         }
/*  81 */       } else if ((traversedContext instanceof InitializationFlowContext)) {
/*  82 */         currentScope.problemReporter().cannotReturnInInitializer(this);
/*  83 */         return FlowInfo.DEAD_END;
/*     */       }
/*     */     }
/*  85 */     while ((traversedContext = traversedContext.parent) != null);
/*     */ 
/*  88 */     if ((this.subroutines != null) && (subCount != this.subroutines.length)) {
/*  89 */       System.arraycopy(this.subroutines, 0, this.subroutines = new SubRoutineStatement[subCount], 0, subCount);
/*     */     }
/*     */ 
/*  93 */     if (saveValueNeeded) {
/*  94 */       if (this.saveValueVariable != null)
/*  95 */         this.saveValueVariable.useFlag = 1;
/*     */     }
/*     */     else {
/*  98 */       this.saveValueVariable = null;
/*  99 */       if (((this.bits & 0x40000000) == 0) && (this.expression != null) && (this.expression.resolvedType == TypeBinding.BOOLEAN)) {
/* 100 */         this.expression.bits |= 16;
/*     */       }
/*     */     }
/* 103 */     return FlowInfo.DEAD_END;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/* 115 */     if ((this.bits & 0x80000000) == 0) {
/* 116 */       return;
/*     */     }
/* 118 */     int pc = codeStream.position;
/* 119 */     boolean alreadyGeneratedExpression = false;
/*     */ 
/* 121 */     if (needValueStore()) {
/* 122 */       alreadyGeneratedExpression = true;
/* 123 */       this.expression.generateCode(currentScope, codeStream, needValue());
/* 124 */       generateStoreSaveValueIfNecessary(codeStream);
/*     */     }
/*     */ 
/* 128 */     if (this.subroutines != null) {
/* 129 */       Object reusableJSRTarget = this.expression == null ? TypeBinding.VOID : this.expression.reusableJSRTarget();
/* 130 */       int i = 0; for (int max = this.subroutines.length; i < max; i++) {
/* 131 */         SubRoutineStatement sub = this.subroutines[i];
/* 132 */         boolean didEscape = sub.generateSubRoutineInvocation(currentScope, codeStream, reusableJSRTarget, this.initStateIndex, this.saveValueVariable);
/* 133 */         if (didEscape) {
/* 134 */           codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 135 */           SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, i, codeStream);
/* 136 */           return;
/*     */         }
/*     */       }
/*     */     }
/* 140 */     if (this.saveValueVariable != null) {
/* 141 */       codeStream.addVariable(this.saveValueVariable);
/* 142 */       codeStream.load(this.saveValueVariable);
/*     */     }
/* 144 */     if ((this.expression != null) && (!alreadyGeneratedExpression)) {
/* 145 */       this.expression.generateCode(currentScope, codeStream, true);
/* 146 */       generateStoreSaveValueIfNecessary(codeStream);
/*     */     }
/*     */ 
/* 149 */     generateReturnBytecode(codeStream);
/* 150 */     if (this.saveValueVariable != null) {
/* 151 */       codeStream.removeVariable(this.saveValueVariable);
/*     */     }
/* 153 */     if (this.initStateIndex != -1) {
/* 154 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.initStateIndex);
/* 155 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.initStateIndex);
/*     */     }
/* 157 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 158 */     SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, -1, codeStream);
/*     */   }
/*     */ 
/*     */   public void generateReturnBytecode(CodeStream codeStream)
/*     */   {
/* 166 */     codeStream.generateReturnBytecode(this.expression);
/*     */   }
/*     */ 
/*     */   public void generateStoreSaveValueIfNecessary(CodeStream codeStream) {
/* 170 */     if (this.saveValueVariable != null)
/* 171 */       codeStream.store(this.saveValueVariable, false);
/*     */   }
/*     */ 
/*     */   private boolean needValueStore()
/*     */   {
/* 178 */     return (this.expression != null) && 
/* 177 */       ((this.expression.constant == Constant.NotAConstant) || ((this.expression.implicitConversion & 0x200) != 0)) && 
/* 178 */       (!(this.expression instanceof NullLiteral));
/*     */   }
/*     */ 
/*     */   public boolean needValue()
/*     */   {
/* 184 */     return (this.saveValueVariable != null) || 
/* 183 */       ((this.bits & 0x40000000) != 0) || 
/* 184 */       ((this.bits & 0x20000000) == 0);
/*     */   }
/*     */ 
/*     */   public void prepareSaveValueLocation(TryStatement targetTryStatement) {
/* 188 */     this.saveValueVariable = targetTryStatement.secretReturnValue;
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int tab, StringBuffer output) {
/* 192 */     printIndent(tab, output).append("return ");
/* 193 */     if (this.expression != null)
/* 194 */       this.expression.printExpression(0, output);
/* 195 */     return output.append(';');
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope) {
/* 199 */     MethodScope methodScope = scope.methodScope();
/*     */     MethodBinding methodBinding;
/* 201 */     TypeBinding methodType = 
/* 202 */       (methodScope.referenceContext instanceof AbstractMethodDeclaration) ? 
/* 205 */       methodBinding.returnType : (methodBinding = ((AbstractMethodDeclaration)methodScope.referenceContext).binding) == null ? 
/* 204 */       null : 
/* 206 */       TypeBinding.VOID;
/*     */ 
/* 208 */     if (methodType == TypeBinding.VOID)
/*     */     {
/* 210 */       if (this.expression == null)
/* 211 */         return;
/*     */       TypeBinding expressionType;
/* 212 */       if ((expressionType = this.expression.resolveType(scope)) != null)
/* 213 */         scope.problemReporter().attemptToReturnNonVoidExpression(this, expressionType);
/* 214 */       return;
/*     */     }
/* 216 */     if (this.expression == null) {
/* 217 */       if (methodType != null) scope.problemReporter().shouldReturn(methodType, this);
/* 218 */       return;
/*     */     }
/* 220 */     this.expression.setExpectedType(methodType);
/*     */     TypeBinding expressionType;
/* 221 */     if ((expressionType = this.expression.resolveType(scope)) == null) return;
/* 222 */     if (expressionType == TypeBinding.VOID) {
/* 223 */       scope.problemReporter().attemptToReturnVoidValue(this);
/* 224 */       return;
/*     */     }
/* 226 */     if (methodType == null) {
/* 227 */       return;
/*     */     }
/* 229 */     if (methodType != expressionType)
/* 230 */       scope.compilationUnitScope().recordTypeConversion(methodType, expressionType);
/* 231 */     if ((this.expression.isConstantValueOfTypeAssignableToType(expressionType, methodType)) || 
/* 232 */       (expressionType.isCompatibleWith(methodType)))
/*     */     {
/* 234 */       this.expression.computeConversion(scope, methodType, expressionType);
/* 235 */       if (expressionType.needsUncheckedConversion(methodType)) {
/* 236 */         scope.problemReporter().unsafeTypeConversion(this.expression, expressionType, methodType);
/*     */       }
/* 238 */       if (((this.expression instanceof CastExpression)) && 
/* 239 */         ((this.expression.bits & 0x4020) == 0)) {
/* 240 */         CastExpression.checkNeedForAssignedCast(scope, methodType, (CastExpression)this.expression);
/*     */       }
/* 242 */       return;
/* 243 */     }if (isBoxingCompatible(expressionType, methodType, this.expression, scope)) {
/* 244 */       this.expression.computeConversion(scope, methodType, expressionType);
/* 245 */       if (((this.expression instanceof CastExpression)) && 
/* 246 */         ((this.expression.bits & 0x4020) == 0))
/* 247 */         CastExpression.checkNeedForAssignedCast(scope, methodType, (CastExpression)this.expression);
/* 248 */       return;
/*     */     }
/* 250 */     if ((methodType.tagBits & 0x80) == 0L)
/*     */     {
/* 252 */       scope.problemReporter().typeMismatchError(expressionType, methodType, this.expression, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 257 */     if ((visitor.visit(this, scope)) && 
/* 258 */       (this.expression != null)) {
/* 259 */       this.expression.traverse(visitor, scope);
/*     */     }
/* 261 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 * JD-Core Version:    0.6.0
 */