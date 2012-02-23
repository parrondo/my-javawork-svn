/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ 
/*    */ public class ProblemBinding extends Binding
/*    */ {
/*    */   public char[] name;
/*    */   public ReferenceBinding searchType;
/*    */   private int problemId;
/*    */ 
/*    */   public ProblemBinding(char[][] compoundName, int problemId)
/*    */   {
/* 22 */     this(CharOperation.concatWith(compoundName, '.'), problemId);
/*    */   }
/*    */ 
/*    */   public ProblemBinding(char[][] compoundName, ReferenceBinding searchType, int problemId)
/*    */   {
/* 27 */     this(CharOperation.concatWith(compoundName, '.'), searchType, problemId);
/*    */   }
/*    */   ProblemBinding(char[] name, int problemId) {
/* 30 */     this.name = name;
/* 31 */     this.problemId = problemId;
/*    */   }
/*    */   ProblemBinding(char[] name, ReferenceBinding searchType, int problemId) {
/* 34 */     this(name, problemId);
/* 35 */     this.searchType = searchType;
/*    */   }
/*    */ 
/*    */   public final int kind()
/*    */   {
/* 42 */     return 7;
/*    */   }
/*    */ 
/*    */   public final int problemId()
/*    */   {
/* 50 */     return this.problemId;
/*    */   }
/*    */   public char[] readableName() {
/* 53 */     return this.name;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ProblemBinding
 * JD-Core Version:    0.6.0
 */