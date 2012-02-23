/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ public class ProblemMethodBinding extends MethodBinding
/*    */ {
/*    */   private int problemReason;
/*    */   public MethodBinding closestMatch;
/*    */ 
/*    */   public ProblemMethodBinding(char[] selector, TypeBinding[] args, int problemReason)
/*    */   {
/* 19 */     this.selector = selector;
/* 20 */     this.parameters = ((args == null) || (args.length == 0) ? Binding.NO_PARAMETERS : args);
/* 21 */     this.problemReason = problemReason;
/* 22 */     this.thrownExceptions = Binding.NO_EXCEPTIONS;
/*    */   }
/*    */   public ProblemMethodBinding(char[] selector, TypeBinding[] args, ReferenceBinding declaringClass, int problemReason) {
/* 25 */     this.selector = selector;
/* 26 */     this.parameters = ((args == null) || (args.length == 0) ? Binding.NO_PARAMETERS : args);
/* 27 */     this.declaringClass = declaringClass;
/* 28 */     this.problemReason = problemReason;
/* 29 */     this.thrownExceptions = Binding.NO_EXCEPTIONS;
/*    */   }
/*    */   public ProblemMethodBinding(MethodBinding closestMatch, char[] selector, TypeBinding[] args, int problemReason) {
/* 32 */     this(selector, args, problemReason);
/* 33 */     this.closestMatch = closestMatch;
/* 34 */     if ((closestMatch != null) && (problemReason != 3)) this.declaringClass = closestMatch.declaringClass;
/*    */   }
/*    */ 
/*    */   public final int problemId()
/*    */   {
/* 42 */     return this.problemReason;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding
 * JD-Core Version:    0.6.0
 */