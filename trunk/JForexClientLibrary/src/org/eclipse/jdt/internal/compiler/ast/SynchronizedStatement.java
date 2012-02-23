/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class SynchronizedStatement extends SubRoutineStatement
/*     */ {
/*     */   public Expression expression;
/*     */   public Block block;
/*     */   public BlockScope scope;
/*     */   public LocalVariableBinding synchroVariable;
/*  26 */   static final char[] SecretLocalDeclarationName = " syncValue".toCharArray();
/*     */ 
/*  29 */   int preSynchronizedInitStateIndex = -1;
/*  30 */   int mergedSynchronizedInitStateIndex = -1;
/*     */ 
/*     */   public SynchronizedStatement(Expression expression, Block statement, int s, int e)
/*     */   {
/*  38 */     this.expression = expression;
/*  39 */     this.block = statement;
/*  40 */     this.sourceEnd = e;
/*  41 */     this.sourceStart = s;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  49 */     this.preSynchronizedInitStateIndex = 
/*  50 */       currentScope.methodScope().recordInitializationStates(flowInfo);
/*     */ 
/*  54 */     this.synchroVariable.useFlag = 1;
/*     */ 
/*  57 */     flowInfo = 
/*  58 */       this.block.analyseCode(
/*  59 */       this.scope, 
/*  60 */       new InsideSubRoutineFlowContext(flowContext, this), 
/*  61 */       this.expression.analyseCode(this.scope, flowContext, flowInfo));
/*     */ 
/*  63 */     this.mergedSynchronizedInitStateIndex = 
/*  64 */       currentScope.methodScope().recordInitializationStates(flowInfo);
/*     */ 
/*  67 */     if ((flowInfo.tagBits & 0x1) != 0) {
/*  68 */       this.bits |= 536870912;
/*     */     }
/*     */ 
/*  71 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public boolean isSubRoutineEscaping() {
/*  75 */     return false;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*  85 */     if ((this.bits & 0x80000000) == 0) {
/*  86 */       return;
/*     */     }
/*     */ 
/*  90 */     this.anyExceptionLabel = null;
/*     */ 
/*  92 */     int pc = codeStream.position;
/*     */ 
/*  95 */     this.expression.generateCode(this.scope, codeStream, true);
/*  96 */     if (this.block.isEmptyBlock()) {
/*  97 */       switch (this.synchroVariable.type.id) {
/*     */       case 7:
/*     */       case 8:
/* 100 */         codeStream.dup2();
/* 101 */         break;
/*     */       default:
/* 103 */         codeStream.dup();
/*     */       }
/*     */ 
/* 107 */       codeStream.monitorenter();
/* 108 */       codeStream.monitorexit();
/* 109 */       if (this.scope != currentScope)
/* 110 */         codeStream.exitUserScope(this.scope);
/*     */     }
/*     */     else
/*     */     {
/* 114 */       codeStream.store(this.synchroVariable, true);
/* 115 */       codeStream.addVariable(this.synchroVariable);
/* 116 */       codeStream.monitorenter();
/*     */ 
/* 119 */       enterAnyExceptionHandler(codeStream);
/* 120 */       this.block.generateCode(this.scope, codeStream);
/* 121 */       if (this.scope != currentScope)
/*     */       {
/* 123 */         codeStream.exitUserScope(this.scope, this.synchroVariable);
/*     */       }
/*     */ 
/* 126 */       BranchLabel endLabel = new BranchLabel(codeStream);
/* 127 */       if ((this.bits & 0x20000000) == 0) {
/* 128 */         codeStream.load(this.synchroVariable);
/* 129 */         codeStream.monitorexit();
/* 130 */         exitAnyExceptionHandler();
/* 131 */         codeStream.goto_(endLabel);
/* 132 */         enterAnyExceptionHandler(codeStream);
/*     */       }
/*     */ 
/* 135 */       codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
/* 136 */       if (this.preSynchronizedInitStateIndex != -1) {
/* 137 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSynchronizedInitStateIndex);
/*     */       }
/* 139 */       placeAllAnyExceptionHandler();
/* 140 */       codeStream.load(this.synchroVariable);
/* 141 */       codeStream.monitorexit();
/* 142 */       exitAnyExceptionHandler();
/* 143 */       codeStream.athrow();
/*     */ 
/* 145 */       if (this.mergedSynchronizedInitStateIndex != -1) {
/* 146 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedSynchronizedInitStateIndex);
/* 147 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedSynchronizedInitStateIndex);
/*     */       }
/* 149 */       if (this.scope != currentScope) {
/* 150 */         codeStream.removeVariable(this.synchroVariable);
/*     */       }
/* 152 */       if ((this.bits & 0x20000000) == 0) {
/* 153 */         endLabel.place();
/*     */       }
/*     */     }
/* 156 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public boolean generateSubRoutineInvocation(BlockScope currentScope, CodeStream codeStream, Object targetLocation, int stateIndex, LocalVariableBinding secretLocal)
/*     */   {
/* 163 */     codeStream.load(this.synchroVariable);
/* 164 */     codeStream.monitorexit();
/* 165 */     exitAnyExceptionHandler();
/* 166 */     return false;
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope upperScope)
/*     */   {
/* 171 */     this.scope = new BlockScope(upperScope);
/* 172 */     TypeBinding type = this.expression.resolveType(this.scope);
/* 173 */     if (type == null)
/* 174 */       return;
/* 175 */     switch (type.id) {
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/* 184 */       this.scope.problemReporter().invalidTypeToSynchronize(this.expression, type);
/* 185 */       break;
/*     */     case 6:
/* 187 */       this.scope.problemReporter().illegalVoidExpression(this.expression);
/* 188 */       break;
/*     */     case 12:
/* 190 */       this.scope.problemReporter().invalidNullToSynchronize(this.expression);
/*     */     case 11:
/*     */     }
/*     */ 
/* 194 */     this.synchroVariable = new LocalVariableBinding(SecretLocalDeclarationName, type, 0, false);
/* 195 */     this.scope.addLocalVariable(this.synchroVariable);
/* 196 */     this.synchroVariable.setConstant(Constant.NotAConstant);
/* 197 */     this.expression.computeConversion(this.scope, type, type);
/* 198 */     this.block.resolveUsing(this.scope);
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output) {
/* 202 */     printIndent(indent, output);
/* 203 */     output.append("synchronized (");
/* 204 */     this.expression.printExpression(0, output).append(')');
/* 205 */     output.append('\n');
/* 206 */     return this.block.printStatement(indent + 1, output);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope) {
/* 210 */     if (visitor.visit(this, blockScope)) {
/* 211 */       this.expression.traverse(visitor, this.scope);
/* 212 */       this.block.traverse(visitor, this.scope);
/*     */     }
/* 214 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement
 * JD-Core Version:    0.6.0
 */