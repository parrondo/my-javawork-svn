/*    */ package org.eclipse.jdt.internal.compiler.batch;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*    */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
/*    */ import org.eclipse.jdt.internal.compiler.util.Util;
/*    */ 
/*    */ public class CompilationUnit
/*    */   implements ICompilationUnit
/*    */ {
/*    */   public char[] contents;
/*    */   public char[] fileName;
/*    */   public char[] mainTypeName;
/*    */   String encoding;
/*    */   public String destinationPath;
/*    */ 
/*    */   public CompilationUnit(char[] contents, String fileName, String encoding)
/*    */   {
/* 36 */     this(contents, fileName, encoding, null);
/*    */   }
/*    */ 
/*    */   public CompilationUnit(char[] contents, String fileName, String encoding, String destinationPath) {
/* 40 */     this.contents = contents;
/* 41 */     char[] fileNameCharArray = fileName.toCharArray();
/* 42 */     switch (File.separatorChar) {
/*    */     case '/':
/* 44 */       if (CharOperation.indexOf('\\', fileNameCharArray) == -1) break;
/* 45 */       CharOperation.replace(fileNameCharArray, '\\', '/');
/*    */ 
/* 47 */       break;
/*    */     case '\\':
/* 49 */       if (CharOperation.indexOf('/', fileNameCharArray) == -1) break;
/* 50 */       CharOperation.replace(fileNameCharArray, '/', '\\');
/*    */     }
/*    */ 
/* 53 */     this.fileName = fileNameCharArray;
/* 54 */     int start = CharOperation.lastIndexOf(File.separatorChar, fileNameCharArray) + 1;
/*    */ 
/* 56 */     int end = CharOperation.lastIndexOf('.', fileNameCharArray);
/* 57 */     if (end == -1) {
/* 58 */       end = fileNameCharArray.length;
/*    */     }
/*    */ 
/* 61 */     this.mainTypeName = CharOperation.subarray(fileNameCharArray, start, end);
/* 62 */     this.encoding = encoding;
/* 63 */     this.destinationPath = destinationPath;
/*    */   }
/*    */   public char[] getContents() {
/* 66 */     if (this.contents != null) {
/* 67 */       return this.contents;
/*    */     }
/*    */     try
/*    */     {
/* 71 */       return Util.getFileCharContent(new File(new String(this.fileName)), this.encoding);
/*    */     } catch (IOException e) {
/* 73 */       this.contents = CharOperation.NO_CHAR;
/* 74 */     }throw new AbortCompilationUnit(null, e, this.encoding);
/*    */   }
/*    */ 
/*    */   public char[] getFileName()
/*    */   {
/* 81 */     return this.fileName;
/*    */   }
/*    */   public char[] getMainTypeName() {
/* 84 */     return this.mainTypeName;
/*    */   }
/*    */   public char[][] getPackageName() {
/* 87 */     return null;
/*    */   }
/*    */   public String toString() {
/* 90 */     return "CompilationUnit[" + new String(this.fileName) + "]";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.batch.CompilationUnit
 * JD-Core Version:    0.6.0
 */