/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ public class ProblemPackageBinding extends PackageBinding
/*    */ {
/*    */   private int problemId;
/*    */ 
/*    */   ProblemPackageBinding(char[][] compoundName, int problemId)
/*    */   {
/* 18 */     this.compoundName = compoundName;
/* 19 */     this.problemId = problemId;
/*    */   }
/*    */   ProblemPackageBinding(char[] name, int problemId) {
/* 22 */     this(new char[][] { name }, problemId);
/*    */   }
/*    */ 
/*    */   public final int problemId()
/*    */   {
/* 30 */     return this.problemId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ProblemPackageBinding
 * JD-Core Version:    0.6.0
 */