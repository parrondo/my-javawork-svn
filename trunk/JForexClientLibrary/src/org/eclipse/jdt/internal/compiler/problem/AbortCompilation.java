/*    */ package org.eclipse.jdt.internal.compiler.problem;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*    */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*    */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
/*    */ import org.eclipse.jdt.internal.compiler.util.Util;
/*    */ 
/*    */ public class AbortCompilation extends RuntimeException
/*    */ {
/*    */   public CompilationResult compilationResult;
/*    */   public Throwable exception;
/*    */   public CategorizedProblem problem;
/*    */   public boolean isSilent;
/*    */   public RuntimeException silentException;
/*    */   private static final long serialVersionUID = -2047226595083244852L;
/*    */ 
/*    */   public AbortCompilation()
/*    */   {
/*    */   }
/*    */ 
/*    */   public AbortCompilation(CompilationResult compilationResult, CategorizedProblem problem)
/*    */   {
/* 42 */     this();
/* 43 */     this.compilationResult = compilationResult;
/* 44 */     this.problem = problem;
/*    */   }
/*    */ 
/*    */   public AbortCompilation(CompilationResult compilationResult, Throwable exception) {
/* 48 */     this();
/* 49 */     this.compilationResult = compilationResult;
/* 50 */     this.exception = exception;
/*    */   }
/*    */ 
/*    */   public AbortCompilation(boolean isSilent, RuntimeException silentException) {
/* 54 */     this();
/* 55 */     this.isSilent = isSilent;
/* 56 */     this.silentException = silentException;
/*    */   }
/*    */   public String getMessage() {
/* 59 */     String message = super.getMessage();
/* 60 */     StringBuffer buffer = new StringBuffer(message == null ? Util.EMPTY_STRING : message);
/* 61 */     if (this.problem != null) {
/* 62 */       buffer.append(this.problem);
/* 63 */     } else if (this.exception != null) {
/* 64 */       message = this.exception.getMessage();
/* 65 */       buffer.append(message == null ? Util.EMPTY_STRING : message);
/* 66 */     } else if (this.silentException != null) {
/* 67 */       message = this.silentException.getMessage();
/* 68 */       buffer.append(message == null ? Util.EMPTY_STRING : message);
/*    */     }
/* 70 */     return String.valueOf(buffer);
/*    */   }
/*    */   public void updateContext(InvocationSite invocationSite, CompilationResult unitResult) {
/* 73 */     if (this.problem == null) return;
/* 74 */     if ((this.problem.getSourceStart() != 0) || (this.problem.getSourceEnd() != 0)) return;
/* 75 */     this.problem.setSourceStart(invocationSite.sourceStart());
/* 76 */     this.problem.setSourceEnd(invocationSite.sourceEnd());
/* 77 */     int[] lineEnds = unitResult.getLineSeparatorPositions();
/* 78 */     this.problem.setSourceLineNumber(Util.getLineNumber(invocationSite.sourceStart(), lineEnds, 0, lineEnds.length - 1));
/* 79 */     this.compilationResult = unitResult;
/*    */   }
/*    */ 
/*    */   public void updateContext(ASTNode astNode, CompilationResult unitResult) {
/* 83 */     if (this.problem == null) return;
/* 84 */     if ((this.problem.getSourceStart() != 0) || (this.problem.getSourceEnd() != 0)) return;
/* 85 */     this.problem.setSourceStart(astNode.sourceStart());
/* 86 */     this.problem.setSourceEnd(astNode.sourceEnd());
/* 87 */     int[] lineEnds = unitResult.getLineSeparatorPositions();
/* 88 */     this.problem.setSourceLineNumber(Util.getLineNumber(astNode.sourceStart(), lineEnds, 0, lineEnds.length - 1));
/* 89 */     this.compilationResult = unitResult;
/*    */   }
/*    */ 
/*    */   public String getKey() {
/* 93 */     StringBuffer buffer = new StringBuffer();
/* 94 */     if (this.problem != null) {
/* 95 */       buffer.append(this.problem);
/*    */     }
/* 97 */     return String.valueOf(buffer);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.problem.AbortCompilation
 * JD-Core Version:    0.6.0
 */