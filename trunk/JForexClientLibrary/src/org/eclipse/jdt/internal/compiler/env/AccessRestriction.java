/*    */ package org.eclipse.jdt.internal.compiler.env;
/*    */ 
/*    */ public class AccessRestriction
/*    */ {
/*    */   private AccessRule accessRule;
/*    */   public byte classpathEntryType;
/*    */   public static final byte COMMAND_LINE = 0;
/*    */   public static final byte PROJECT = 1;
/*    */   public static final byte LIBRARY = 2;
/*    */   public String classpathEntryName;
/*    */ 
/*    */   public AccessRestriction(AccessRule accessRule, byte classpathEntryType, String classpathEntryName)
/*    */   {
/* 24 */     this.accessRule = accessRule;
/* 25 */     this.classpathEntryName = classpathEntryName;
/* 26 */     this.classpathEntryType = classpathEntryType;
/*    */   }
/*    */ 
/*    */   public int getProblemId() {
/* 30 */     return this.accessRule.getProblemId();
/*    */   }
/*    */ 
/*    */   public boolean ignoreIfBetter() {
/* 34 */     return this.accessRule.ignoreIfBetter();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.AccessRestriction
 * JD-Core Version:    0.6.0
 */