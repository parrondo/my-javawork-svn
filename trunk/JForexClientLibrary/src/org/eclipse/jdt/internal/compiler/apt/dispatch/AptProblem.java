/*    */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*    */ import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
/*    */ 
/*    */ public class AptProblem extends DefaultProblem
/*    */ {
/*    */   private static final String MARKER_ID = "org.eclipse.jdt.apt.pluggable.core.compileProblem";
/*    */   public final ReferenceContext _referenceContext;
/*    */ 
/*    */   public AptProblem(ReferenceContext referenceContext, char[] originatingFileName, String message, int id, String[] stringArguments, int severity, int startPosition, int endPosition, int line, int column)
/*    */   {
/* 47 */     super(originatingFileName, 
/* 40 */       message, 
/* 41 */       id, 
/* 42 */       stringArguments, 
/* 43 */       severity, 
/* 44 */       startPosition, 
/* 45 */       endPosition, 
/* 46 */       line, 
/* 47 */       column);
/* 48 */     this._referenceContext = referenceContext;
/*    */   }
/*    */ 
/*    */   public int getCategoryID()
/*    */   {
/* 53 */     return 0;
/*    */   }
/*    */ 
/*    */   public String getMarkerType()
/*    */   {
/* 58 */     return "org.eclipse.jdt.apt.pluggable.core.compileProblem";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.AptProblem
 * JD-Core Version:    0.6.0
 */