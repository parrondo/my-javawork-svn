/*    */ package org.eclipse.jdt.internal.compiler.problem;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*    */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*    */ 
/*    */ public class AbortCompilationUnit extends AbortCompilation
/*    */ {
/*    */   private static final long serialVersionUID = -4253893529982226734L;
/*    */   public String encoding;
/*    */ 
/*    */   public AbortCompilationUnit(CompilationResult compilationResult, CategorizedProblem problem)
/*    */   {
/* 31 */     super(compilationResult, problem);
/*    */   }
/*    */ 
/*    */   public AbortCompilationUnit(CompilationResult compilationResult, IOException exception, String encoding)
/*    */   {
/* 38 */     super(compilationResult, exception);
/* 39 */     this.encoding = encoding;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit
 * JD-Core Version:    0.6.0
 */