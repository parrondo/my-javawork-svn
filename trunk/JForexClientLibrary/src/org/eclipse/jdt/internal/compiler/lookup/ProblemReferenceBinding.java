/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ 
/*    */ public class ProblemReferenceBinding extends ReferenceBinding
/*    */ {
/*    */   ReferenceBinding closestMatch;
/*    */   private int problemReason;
/*    */ 
/*    */   public ProblemReferenceBinding(char[][] compoundName, ReferenceBinding closestMatch, int problemReason)
/*    */   {
/* 24 */     this.compoundName = compoundName;
/* 25 */     this.closestMatch = closestMatch;
/* 26 */     this.problemReason = problemReason;
/*    */   }
/*    */ 
/*    */   public TypeBinding closestMatch()
/*    */   {
/* 33 */     return this.closestMatch;
/*    */   }
/*    */ 
/*    */   public ReferenceBinding closestReferenceMatch()
/*    */   {
/* 40 */     return this.closestMatch;
/*    */   }
/*    */ 
/*    */   public int problemId()
/*    */   {
/* 48 */     return this.problemReason;
/*    */   }
/*    */ 
/*    */   public static String problemReasonString(int problemReason) {
/*    */     try {
/* 53 */       Class reasons = ProblemReasons.class;
/* 54 */       String simpleName = reasons.getName();
/* 55 */       int lastDot = simpleName.lastIndexOf('.');
/* 56 */       if (lastDot >= 0) {
/* 57 */         simpleName = simpleName.substring(lastDot + 1);
/*    */       }
/* 59 */       Field[] fields = reasons.getFields();
/* 60 */       int i = 0; for (int length = fields.length; i < length; i++) {
/* 61 */         Field field = fields[i];
/* 62 */         if ((field.getType().equals(Integer.TYPE)) && 
/* 63 */           (field.getInt(reasons) == problemReason))
/* 64 */           return simpleName + '.' + field.getName();
/*    */       }
/*    */     }
/*    */     catch (IllegalAccessException localIllegalAccessException)
/*    */     {
/*    */     }
/* 70 */     return "unknown";
/*    */   }
/*    */ 
/*    */   public char[] shortReadableName()
/*    */   {
/* 77 */     return readableName();
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 81 */     StringBuffer buffer = new StringBuffer(10);
/* 82 */     buffer.append("ProblemType:[compoundName=");
/* 83 */     buffer.append(this.compoundName == null ? "<null>" : new String(CharOperation.concatWith(this.compoundName, '.')));
/* 84 */     buffer.append("][problemID=").append(problemReasonString(this.problemReason));
/* 85 */     buffer.append("][closestMatch=");
/* 86 */     buffer.append(this.closestMatch == null ? "<null>" : this.closestMatch.toString());
/* 87 */     buffer.append("]");
/* 88 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding
 * JD-Core Version:    0.6.0
 */