/*    */ package org.eclipse.jdt.core.compiler.batch;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import org.eclipse.jdt.core.compiler.CompilationProgress;
/*    */ import org.eclipse.jdt.internal.compiler.batch.Main;
/*    */ 
/*    */ public final class BatchCompiler
/*    */ {
/*    */   public static boolean compile(String commandLine, PrintWriter outWriter, PrintWriter errWriter, CompilationProgress progress)
/*    */   {
/* 52 */     return Main.compile(Main.tokenize(commandLine), outWriter, errWriter, progress);
/*    */   }
/*    */ 
/*    */   public static boolean compile(String[] commandLineArguments, PrintWriter outWriter, PrintWriter errWriter, CompilationProgress progress)
/*    */   {
/* 80 */     return Main.compile(commandLineArguments, outWriter, errWriter, progress);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.core.compiler.batch.BatchCompiler
 * JD-Core Version:    0.6.0
 */