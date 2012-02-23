/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*    */ 
/*    */ public abstract class SubRoutineStatement extends Statement
/*    */ {
/*    */   ExceptionLabel anyExceptionLabel;
/*    */ 
/*    */   public static void reenterAllExceptionHandlers(SubRoutineStatement[] subroutines, int max, CodeStream codeStream)
/*    */   {
/* 24 */     if (subroutines == null) return;
/* 25 */     if (max < 0) max = subroutines.length;
/* 26 */     for (int i = 0; i < max; i++) {
/* 27 */       SubRoutineStatement sub = subroutines[i];
/* 28 */       sub.enterAnyExceptionHandler(codeStream);
/* 29 */       sub.enterDeclaredExceptionHandlers(codeStream);
/*    */     }
/*    */   }
/*    */ 
/*    */   public ExceptionLabel enterAnyExceptionHandler(CodeStream codeStream)
/*    */   {
/* 37 */     if (this.anyExceptionLabel == null) {
/* 38 */       this.anyExceptionLabel = new ExceptionLabel(codeStream, null);
/*    */     }
/* 40 */     this.anyExceptionLabel.placeStart();
/* 41 */     return this.anyExceptionLabel;
/*    */   }
/*    */ 
/*    */   public void enterDeclaredExceptionHandlers(CodeStream codeStream)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void exitAnyExceptionHandler() {
/* 49 */     if (this.anyExceptionLabel != null)
/* 50 */       this.anyExceptionLabel.placeEnd();
/*    */   }
/*    */ 
/*    */   public void exitDeclaredExceptionHandlers(CodeStream codeStream)
/*    */   {
/*    */   }
/*    */ 
/*    */   public abstract boolean generateSubRoutineInvocation(BlockScope paramBlockScope, CodeStream paramCodeStream, Object paramObject, int paramInt, LocalVariableBinding paramLocalVariableBinding);
/*    */ 
/*    */   public abstract boolean isSubRoutineEscaping();
/*    */ 
/*    */   public void placeAllAnyExceptionHandler()
/*    */   {
/* 73 */     this.anyExceptionLabel.place();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement
 * JD-Core Version:    0.6.0
 */