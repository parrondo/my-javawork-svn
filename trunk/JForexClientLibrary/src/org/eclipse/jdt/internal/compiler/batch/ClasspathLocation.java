/*    */ package org.eclipse.jdt.internal.compiler.batch;
/*    */ 
/*    */ import java.io.File;
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
/*    */ import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
/*    */ import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
/*    */ 
/*    */ public abstract class ClasspathLocation
/*    */   implements FileSystem.Classpath, SuffixConstants
/*    */ {
/*    */   public static final int SOURCE = 1;
/*    */   public static final int BINARY = 2;
/*    */   String path;
/*    */   char[] normalizedPath;
/*    */   public AccessRuleSet accessRuleSet;
/*    */   public String destinationPath;
/*    */ 
/*    */   protected ClasspathLocation(AccessRuleSet accessRuleSet, String destinationPath)
/*    */   {
/* 43 */     this.accessRuleSet = accessRuleSet;
/* 44 */     this.destinationPath = destinationPath;
/*    */   }
/*    */ 
/*    */   protected AccessRestriction fetchAccessRestriction(String qualifiedBinaryFileName)
/*    */   {
/* 60 */     if (this.accessRuleSet == null)
/* 61 */       return null;
/* 62 */     char[] qualifiedTypeName = qualifiedBinaryFileName
/* 63 */       .substring(0, qualifiedBinaryFileName.length() - SUFFIX_CLASS.length)
/* 64 */       .toCharArray();
/* 65 */     if (File.separatorChar == '\\') {
/* 66 */       CharOperation.replace(qualifiedTypeName, File.separatorChar, '/');
/*    */     }
/* 68 */     return this.accessRuleSet.getViolatedRestriction(qualifiedTypeName);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.batch.ClasspathLocation
 * JD-Core Version:    0.6.0
 */