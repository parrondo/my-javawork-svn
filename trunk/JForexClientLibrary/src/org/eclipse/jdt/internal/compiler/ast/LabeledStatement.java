/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.LabelFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class LabeledStatement extends Statement
/*     */ {
/*     */   public Statement statement;
/*     */   public char[] label;
/*     */   public BranchLabel targetLabel;
/*     */   public int labelEnd;
/*  26 */   int mergedInitStateIndex = -1;
/*     */ 
/*     */   public LabeledStatement(char[] label, Statement statement, long labelPosition, int sourceEnd)
/*     */   {
/*  33 */     this.statement = statement;
/*     */ 
/*  35 */     if ((statement instanceof EmptyStatement)) statement.bits |= 1;
/*  36 */     this.label = label;
/*  37 */     this.sourceStart = (int)(labelPosition >>> 32);
/*  38 */     this.labelEnd = (int)labelPosition;
/*  39 */     this.sourceEnd = sourceEnd;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  49 */     if (this.statement == null)
/*  50 */       return flowInfo;
/*     */     LabelFlowContext labelContext;
/*  54 */     FlowInfo statementInfo = this.statement.analyseCode(
/*  55 */       currentScope, 
/*  56 */       labelContext = 
/*  57 */       new LabelFlowContext(
/*  58 */       flowContext, 
/*  59 */       this, 
/*  60 */       this.label, 
/*  61 */       this.targetLabel = new BranchLabel(), 
/*  62 */       currentScope), 
/*  63 */       flowInfo);
/*  64 */     boolean reinjectNullInfo = ((statementInfo.tagBits & 0x1) != 0) && 
/*  65 */       ((labelContext.initsOnBreak.tagBits & 0x1) == 0);
/*  66 */     FlowInfo mergedInfo = statementInfo.mergedWith(labelContext.initsOnBreak);
/*  67 */     if (reinjectNullInfo)
/*     */     {
/*  69 */       ((UnconditionalFlowInfo)mergedInfo).addInitializationsFrom(flowInfo.unconditionalFieldLessCopy())
/*  70 */         .addInitializationsFrom(labelContext.initsOnBreak.unconditionalFieldLessCopy());
/*     */     }
/*  72 */     this.mergedInitStateIndex = 
/*  73 */       currentScope.methodScope().recordInitializationStates(mergedInfo);
/*  74 */     if ((this.bits & 0x40) == 0) {
/*  75 */       currentScope.problemReporter().unusedLabel(this);
/*     */     }
/*  77 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public ASTNode concreteStatement()
/*     */   {
/*  84 */     return this.statement;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*  97 */     if ((this.bits & 0x80000000) == 0) {
/*  98 */       return;
/*     */     }
/* 100 */     int pc = codeStream.position;
/* 101 */     if (this.targetLabel != null) {
/* 102 */       this.targetLabel.initialize(codeStream);
/* 103 */       if (this.statement != null) {
/* 104 */         this.statement.generateCode(currentScope, codeStream);
/*     */       }
/* 106 */       this.targetLabel.place();
/*     */     }
/*     */ 
/* 109 */     if (this.mergedInitStateIndex != -1) {
/* 110 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/* 111 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */     }
/* 113 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int tab, StringBuffer output)
/*     */   {
/* 118 */     printIndent(tab, output).append(this.label).append(": ");
/* 119 */     if (this.statement == null)
/* 120 */       output.append(';');
/*     */     else
/* 122 */       this.statement.printStatement(0, output);
/* 123 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope)
/*     */   {
/* 128 */     if (this.statement != null)
/* 129 */       this.statement.resolve(scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 138 */     if ((visitor.visit(this, blockScope)) && 
/* 139 */       (this.statement != null)) this.statement.traverse(visitor, blockScope);
/*     */ 
/* 141 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.LabeledStatement
 * JD-Core Version:    0.6.0
 */