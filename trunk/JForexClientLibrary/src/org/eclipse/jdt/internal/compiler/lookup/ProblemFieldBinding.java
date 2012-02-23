/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ public class ProblemFieldBinding extends FieldBinding
/*    */ {
/*    */   private int problemId;
/*    */   public FieldBinding closestMatch;
/*    */ 
/*    */   public ProblemFieldBinding(ReferenceBinding declaringClass, char[] name, int problemId)
/*    */   {
/* 20 */     this(null, declaringClass, name, problemId);
/*    */   }
/*    */   public ProblemFieldBinding(FieldBinding closestMatch, ReferenceBinding declaringClass, char[] name, int problemId) {
/* 23 */     this.closestMatch = closestMatch;
/* 24 */     this.declaringClass = declaringClass;
/* 25 */     this.name = name;
/* 26 */     this.problemId = problemId;
/*    */   }
/*    */ 
/*    */   public final int problemId()
/*    */   {
/* 34 */     return this.problemId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding
 * JD-Core Version:    0.6.0
 */