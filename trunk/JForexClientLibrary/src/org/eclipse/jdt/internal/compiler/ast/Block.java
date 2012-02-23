/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class Block extends Statement
/*     */ {
/*     */   public Statement[] statements;
/*     */   public int explicitDeclarations;
/*     */   public BlockScope scope;
/*     */ 
/*     */   public Block(int explicitDeclarations)
/*     */   {
/*  26 */     this.explicitDeclarations = explicitDeclarations;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  31 */     if (this.statements == null) return flowInfo;
/*  32 */     int complaintLevel = (flowInfo.reachMode() & 0x1) != 0 ? 1 : 0;
/*  33 */     int i = 0; for (int max = this.statements.length; i < max; i++) {
/*  34 */       Statement stat = this.statements[i];
/*  35 */       if ((complaintLevel = stat.complainIfUnreachable(flowInfo, this.scope, complaintLevel)) < 2) {
/*  36 */         flowInfo = stat.analyseCode(this.scope, flowContext, flowInfo);
/*     */       }
/*     */     }
/*  39 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*  45 */     if ((this.bits & 0x80000000) == 0) {
/*  46 */       return;
/*     */     }
/*  48 */     int pc = codeStream.position;
/*  49 */     if (this.statements != null) {
/*  50 */       int i = 0; for (int max = this.statements.length; i < max; i++) {
/*  51 */         this.statements[i].generateCode(this.scope, codeStream);
/*     */       }
/*     */     }
/*  54 */     if (this.scope != currentScope) {
/*  55 */       codeStream.exitUserScope(this.scope);
/*     */     }
/*  57 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public boolean isEmptyBlock() {
/*  61 */     return this.statements == null;
/*     */   }
/*     */ 
/*     */   public StringBuffer printBody(int indent, StringBuffer output) {
/*  65 */     if (this.statements == null) return output;
/*  66 */     for (int i = 0; i < this.statements.length; i++) {
/*  67 */       this.statements[i].printStatement(indent + 1, output);
/*  68 */       output.append('\n');
/*     */     }
/*  70 */     return output;
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output) {
/*  74 */     printIndent(indent, output);
/*  75 */     output.append("{\n");
/*  76 */     printBody(indent, output);
/*  77 */     return printIndent(indent, output).append('}');
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope upperScope) {
/*  81 */     if ((this.bits & 0x8) != 0) {
/*  82 */       upperScope.problemReporter().undocumentedEmptyBlock(this.sourceStart, this.sourceEnd);
/*     */     }
/*  84 */     if (this.statements != null) {
/*  85 */       this.scope = 
/*  86 */         (this.explicitDeclarations == 0 ? 
/*  87 */         upperScope : 
/*  88 */         new BlockScope(upperScope, this.explicitDeclarations));
/*  89 */       int i = 0; for (int length = this.statements.length; i < length; i++)
/*  90 */         this.statements[i].resolve(this.scope);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resolveUsing(BlockScope givenScope)
/*     */   {
/*  96 */     if ((this.bits & 0x8) != 0) {
/*  97 */       givenScope.problemReporter().undocumentedEmptyBlock(this.sourceStart, this.sourceEnd);
/*     */     }
/*     */ 
/* 100 */     this.scope = givenScope;
/* 101 */     if (this.statements != null) {
/* 102 */       int i = 0; for (int length = this.statements.length; i < length; i++)
/* 103 */         this.statements[i].resolve(this.scope);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 109 */     if ((visitor.visit(this, blockScope)) && 
/* 110 */       (this.statements != null)) {
/* 111 */       int i = 0; for (int length = this.statements.length; i < length; i++) {
/* 112 */         this.statements[i].traverse(visitor, this.scope);
/*     */       }
/*     */     }
/* 115 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ 
/*     */   public void branchChainTo(BranchLabel label)
/*     */   {
/* 122 */     if (this.statements != null)
/* 123 */       this.statements[(this.statements.length - 1)].branchChainTo(label);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Block
 * JD-Core Version:    0.6.0
 */