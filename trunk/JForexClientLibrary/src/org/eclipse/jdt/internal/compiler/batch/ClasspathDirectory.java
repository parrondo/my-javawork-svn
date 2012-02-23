/*     */ package org.eclipse.jdt.internal.compiler.batch;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
/*     */ import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
/*     */ import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
/*     */ import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class ClasspathDirectory extends ClasspathLocation
/*     */ {
/*     */   private Hashtable directoryCache;
/*  29 */   private String[] missingPackageHolder = new String[1];
/*     */   private int mode;
/*     */   private String encoding;
/*     */ 
/*     */   ClasspathDirectory(File directory, String encoding, int mode, AccessRuleSet accessRuleSet, String destinationPath)
/*     */   {
/*  35 */     super(accessRuleSet, destinationPath);
/*  36 */     this.mode = mode;
/*     */     try {
/*  38 */       this.path = directory.getCanonicalPath();
/*     */     }
/*     */     catch (IOException localIOException) {
/*  41 */       this.path = directory.getAbsolutePath();
/*     */     }
/*  43 */     if (!this.path.endsWith(File.separator))
/*  44 */       this.path += File.separator;
/*  45 */     this.directoryCache = new Hashtable(11);
/*  46 */     this.encoding = encoding;
/*     */   }
/*     */   String[] directoryList(String qualifiedPackageName) {
/*  49 */     String[] dirList = (String[])this.directoryCache.get(qualifiedPackageName);
/*  50 */     if (dirList == this.missingPackageHolder) return null;
/*  51 */     if (dirList != null) return dirList;
/*     */ 
/*  53 */     File dir = new File(this.path + qualifiedPackageName);
/*  54 */     if (dir.isDirectory())
/*     */     {
/*  57 */       int index = qualifiedPackageName.length();
/*  58 */       int last = qualifiedPackageName.lastIndexOf(File.separatorChar);
/*     */       do index--; while ((index > last) && (!ScannerHelper.isUpperCase(qualifiedPackageName.charAt(index))));
/*  60 */       if (index > last)
/*  61 */         if (last == -1) {
/*  62 */           if (!doesFileExist(qualifiedPackageName, Util.EMPTY_STRING))
/*  63 */             break label186;
/*     */         } else {
/*  65 */           String packageName = qualifiedPackageName.substring(last + 1);
/*  66 */           String parentPackage = qualifiedPackageName.substring(0, last);
/*  67 */           if (!doesFileExist(packageName, parentPackage))
/*     */             break label186;
/*     */         }
/*  71 */       if ((dirList = dir.list()) == null)
/*  72 */         dirList = CharOperation.NO_STRINGS;
/*  73 */       this.directoryCache.put(qualifiedPackageName, dirList);
/*  74 */       return dirList;
/*     */     }
/*  76 */     label186: this.directoryCache.put(qualifiedPackageName, this.missingPackageHolder);
/*  77 */     return null;
/*     */   }
/*     */   boolean doesFileExist(String fileName, String qualifiedPackageName) {
/*  80 */     String[] dirList = directoryList(qualifiedPackageName);
/*  81 */     if (dirList == null) return false;
/*     */ 
/*  83 */     int i = dirList.length;
/*     */     do { if (fileName.equals(dirList[i]))
/*  85 */         return true;
/*  83 */       i--; } while (i >= 0);
/*     */ 
/*  86 */     return false;
/*     */   }
/*     */   public List fetchLinkedJars(FileSystem.ClasspathSectionProblemReporter problemReporter) {
/*  89 */     return null;
/*     */   }
/*     */   public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName) {
/*  92 */     return findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, false);
/*     */   }
/*     */   public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
/*  95 */     if (!isPackage(qualifiedPackageName)) return null;
/*     */ 
/*  97 */     String fileName = new String(typeName);
/*  98 */     boolean binaryExists = ((this.mode & 0x2) != 0) && (doesFileExist(fileName + ".class", qualifiedPackageName));
/*  99 */     boolean sourceExists = ((this.mode & 0x1) != 0) && (doesFileExist(fileName + ".java", qualifiedPackageName));
/* 100 */     if ((sourceExists) && (!asBinaryOnly)) {
/* 101 */       String fullSourcePath = this.path + qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6) + ".java";
/* 102 */       if (!binaryExists)
/* 103 */         return new NameEnvironmentAnswer(
/* 104 */           new CompilationUnit(null, 
/* 104 */           fullSourcePath, this.encoding, this.destinationPath), 
/* 105 */           fetchAccessRestriction(qualifiedBinaryFileName));
/* 106 */       String fullBinaryPath = this.path + qualifiedBinaryFileName;
/* 107 */       long binaryModified = new File(fullBinaryPath).lastModified();
/* 108 */       long sourceModified = new File(fullSourcePath).lastModified();
/* 109 */       if (sourceModified > binaryModified)
/* 110 */         return new NameEnvironmentAnswer(
/* 111 */           new CompilationUnit(null, 
/* 111 */           fullSourcePath, this.encoding, this.destinationPath), 
/* 112 */           fetchAccessRestriction(qualifiedBinaryFileName));
/*     */     }
/* 114 */     if (binaryExists)
/*     */       try {
/* 116 */         ClassFileReader reader = ClassFileReader.read(this.path + qualifiedBinaryFileName);
/* 117 */         if (reader != null)
/* 118 */           return new NameEnvironmentAnswer(
/* 119 */             reader, 
/* 120 */             fetchAccessRestriction(qualifiedBinaryFileName));
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/* 125 */     return null;
/*     */   }
/*     */   public char[][][] findTypeNames(String qualifiedPackageName) {
/* 128 */     if (!isPackage(qualifiedPackageName)) {
/* 129 */       return null;
/*     */     }
/* 131 */     File dir = new File(this.path + qualifiedPackageName);
/* 132 */     if ((!dir.exists()) || (!dir.isDirectory())) {
/* 133 */       return null;
/*     */     }
/* 135 */     String[] listFiles = dir.list(new FilenameFilter() {
/*     */       public boolean accept(File directory, String name) {
/* 137 */         String fileName = name.toLowerCase();
/* 138 */         return (fileName.endsWith(".class")) || (fileName.endsWith(".java"));
/*     */       }
/*     */     });
/*     */     int length;
/* 142 */     if ((listFiles == null) || ((length = listFiles.length) == 0))
/* 143 */       return null;
/*     */     int length;
/* 145 */     char[][][] result = new char[length][][];
/* 146 */     char[][] packageName = CharOperation.splitOn(File.separatorChar, qualifiedPackageName.toCharArray());
/* 147 */     for (int i = 0; i < length; i++) {
/* 148 */       String fileName = listFiles[i];
/* 149 */       int indexOfLastDot = fileName.indexOf('.');
/* 150 */       result[i] = CharOperation.arrayConcat(packageName, fileName.substring(0, indexOfLastDot).toCharArray());
/*     */     }
/* 152 */     return result;
/*     */   }
/*     */   public void initialize() throws IOException {
/*     */   }
/*     */ 
/*     */   public boolean isPackage(String qualifiedPackageName) {
/* 158 */     return directoryList(qualifiedPackageName) != null;
/*     */   }
/*     */   public void reset() {
/* 161 */     this.directoryCache = new Hashtable(11);
/*     */   }
/*     */   public String toString() {
/* 164 */     return "ClasspathDirectory " + this.path;
/*     */   }
/*     */   public char[] normalizedPath() {
/* 167 */     if (this.normalizedPath == null) {
/* 168 */       this.normalizedPath = this.path.toCharArray();
/* 169 */       if (File.separatorChar == '\\') {
/* 170 */         CharOperation.replace(this.normalizedPath, '\\', '/');
/*     */       }
/*     */     }
/* 173 */     return this.normalizedPath;
/*     */   }
/*     */   public String getPath() {
/* 176 */     return this.path;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.batch.ClasspathDirectory
 * JD-Core Version:    0.6.0
 */