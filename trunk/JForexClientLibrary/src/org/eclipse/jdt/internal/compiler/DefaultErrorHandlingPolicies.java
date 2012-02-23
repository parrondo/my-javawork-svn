/*    */ package org.eclipse.jdt.internal.compiler;
/*    */ 
/*    */ public class DefaultErrorHandlingPolicies
/*    */ {
/*    */   public static IErrorHandlingPolicy exitAfterAllProblems()
/*    */   {
/* 23 */     return new IErrorHandlingPolicy() {
/*    */       public boolean stopOnFirstError() {
/* 25 */         return false;
/*    */       }
/*    */       public boolean proceedOnErrors() {
/* 28 */         return false;
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public static IErrorHandlingPolicy exitOnFirstError()
/*    */   {
/* 38 */     return new IErrorHandlingPolicy() {
/*    */       public boolean stopOnFirstError() {
/* 40 */         return true;
/*    */       }
/*    */       public boolean proceedOnErrors() {
/* 43 */         return false;
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public static IErrorHandlingPolicy proceedOnFirstError()
/*    */   {
/* 52 */     return new IErrorHandlingPolicy() {
/*    */       public boolean stopOnFirstError() {
/* 54 */         return true;
/*    */       }
/*    */       public boolean proceedOnErrors() {
/* 57 */         return true;
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public static IErrorHandlingPolicy proceedWithAllProblems()
/*    */   {
/* 66 */     return new IErrorHandlingPolicy() {
/*    */       public boolean stopOnFirstError() {
/* 68 */         return false;
/*    */       }
/*    */       public boolean proceedOnErrors() {
/* 71 */         return true;
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies
 * JD-Core Version:    0.6.0
 */