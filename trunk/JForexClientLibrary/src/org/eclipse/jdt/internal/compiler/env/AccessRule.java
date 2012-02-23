/*    */ package org.eclipse.jdt.internal.compiler.env;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ 
/*    */ public class AccessRule
/*    */ {
/*    */   public static final int IgnoreIfBetter = 33554432;
/*    */   public char[] pattern;
/*    */   public int problemId;
/*    */ 
/*    */   public AccessRule(char[] pattern, int problemId)
/*    */   {
/* 24 */     this(pattern, problemId, false);
/*    */   }
/*    */ 
/*    */   public AccessRule(char[] pattern, int problemId, boolean keepLooking) {
/* 28 */     this.pattern = pattern;
/* 29 */     this.problemId = (keepLooking ? problemId | 0x2000000 : problemId);
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 33 */     return this.problemId * 17 + CharOperation.hashCode(this.pattern);
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj) {
/* 37 */     if (!(obj instanceof AccessRule)) return false;
/* 38 */     AccessRule other = (AccessRule)obj;
/* 39 */     if (this.problemId != other.problemId) return false;
/* 40 */     return CharOperation.equals(this.pattern, other.pattern);
/*    */   }
/*    */ 
/*    */   public int getProblemId() {
/* 44 */     return this.problemId & 0xFDFFFFFF;
/*    */   }
/*    */ 
/*    */   public boolean ignoreIfBetter() {
/* 48 */     return (this.problemId & 0x2000000) != 0;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 52 */     StringBuffer buffer = new StringBuffer();
/* 53 */     buffer.append("pattern=");
/* 54 */     buffer.append(this.pattern);
/* 55 */     switch (getProblemId()) {
/*    */     case 16777523:
/* 57 */       buffer.append(" (NON ACCESSIBLE");
/* 58 */       break;
/*    */     case 16777496:
/* 60 */       buffer.append(" (DISCOURAGED");
/* 61 */       break;
/*    */     default:
/* 63 */       buffer.append(" (ACCESSIBLE");
/*    */     }
/*    */ 
/* 66 */     if (ignoreIfBetter())
/* 67 */       buffer.append(" | IGNORE IF BETTER");
/* 68 */     buffer.append(')');
/* 69 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.AccessRule
 * JD-Core Version:    0.6.0
 */