/*    */ package org.eclipse.jdt.internal.compiler.batch;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.zip.ZipEntry;
/*    */ import java.util.zip.ZipFile;
/*    */ import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
/*    */ import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
/*    */ import org.eclipse.jdt.internal.compiler.util.Util;
/*    */ 
/*    */ public class ClasspathSourceJar extends ClasspathJar
/*    */ {
/*    */   private String encoding;
/*    */ 
/*    */   public ClasspathSourceJar(File file, boolean closeZipFileAtEnd, AccessRuleSet accessRuleSet, String encoding, String destinationPath)
/*    */   {
/* 28 */     super(file, closeZipFileAtEnd, accessRuleSet, destinationPath);
/* 29 */     this.encoding = encoding;
/*    */   }
/*    */ 
/*    */   public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
/* 33 */     if (!isPackage(qualifiedPackageName)) {
/* 34 */       return null;
/*    */     }
/* 36 */     ZipEntry sourceEntry = this.zipFile.getEntry(qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6) + ".java");
/* 37 */     if (sourceEntry != null)
/*    */       try {
/* 39 */         InputStream stream = null;
/* 40 */         char[] contents = (char[])null;
/*    */         try {
/* 42 */           stream = this.zipFile.getInputStream(sourceEntry);
/* 43 */           contents = Util.getInputStreamAsCharArray(stream, -1, this.encoding);
/*    */         } finally {
/* 45 */           if (stream != null)
/* 46 */             stream.close();
/*    */         }
/* 48 */         return new NameEnvironmentAnswer(
/* 49 */           new CompilationUnit(
/* 50 */           contents, 
/* 51 */           qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6) + ".java", 
/* 52 */           this.encoding, 
/* 53 */           this.destinationPath), 
/* 54 */           fetchAccessRestriction(qualifiedBinaryFileName));
/*    */       }
/*    */       catch (IOException localIOException)
/*    */       {
/*    */       }
/* 59 */     return null;
/*    */   }
/*    */   public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName) {
/* 62 */     return findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, false);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.batch.ClasspathSourceJar
 * JD-Core Version:    0.6.0
 */