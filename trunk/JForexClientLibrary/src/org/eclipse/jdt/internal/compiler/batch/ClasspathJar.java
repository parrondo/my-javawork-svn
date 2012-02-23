/*     */ package org.eclipse.jdt.internal.compiler.batch;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
/*     */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
/*     */ import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
/*     */ import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
/*     */ import org.eclipse.jdt.internal.compiler.util.ManifestAnalyzer;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class ClasspathJar extends ClasspathLocation
/*     */ {
/*     */   protected File file;
/*     */   protected ZipFile zipFile;
/*     */   protected boolean closeZipFileAtEnd;
/*     */   protected Hashtable packageCache;
/*     */ 
/*     */   public ClasspathJar(File file, boolean closeZipFileAtEnd, AccessRuleSet accessRuleSet, String destinationPath)
/*     */   {
/*  41 */     super(accessRuleSet, destinationPath);
/*  42 */     this.file = file;
/*  43 */     this.closeZipFileAtEnd = closeZipFileAtEnd;
/*     */   }
/*     */ 
/*     */   public List fetchLinkedJars(FileSystem.ClasspathSectionProblemReporter problemReporter)
/*     */   {
/*  49 */     InputStream inputStream = null;
/*     */     try {
/*  51 */       initialize();
/*  52 */       ArrayList result = new ArrayList();
/*  53 */       ZipEntry manifest = this.zipFile.getEntry("META-INF/MANIFEST.MF");
/*  54 */       if (manifest != null) {
/*  55 */         inputStream = this.zipFile.getInputStream(manifest);
/*  56 */         ManifestAnalyzer analyzer = new ManifestAnalyzer();
/*  57 */         boolean success = analyzer.analyzeManifestContents(inputStream);
/*  58 */         List calledFileNames = analyzer.getCalledFileNames();
/*  59 */         if (problemReporter != null) {
/*  60 */           if ((!success) || ((analyzer.getClasspathSectionsCount() == 1) && (calledFileNames == null)))
/*  61 */             problemReporter.invalidClasspathSection(getPath());
/*  62 */           else if (analyzer.getClasspathSectionsCount() > 1) {
/*  63 */             problemReporter.multipleClasspathSections(getPath());
/*     */           }
/*     */         }
/*  66 */         if (calledFileNames != null) {
/*  67 */           Iterator calledFilesIterator = calledFileNames.iterator();
/*  68 */           String directoryPath = getPath();
/*  69 */           int lastSeparator = directoryPath.lastIndexOf(File.separatorChar);
/*  70 */           directoryPath = directoryPath.substring(0, lastSeparator + 1);
/*  71 */           while (calledFilesIterator.hasNext()) {
/*  72 */             result.add(new ClasspathJar(new File(directoryPath + (String)calledFilesIterator.next()), this.closeZipFileAtEnd, this.accessRuleSet, this.destinationPath));
/*     */           }
/*     */         }
/*     */       }
/*  76 */       ArrayList localArrayList1 = result;
/*     */       return localArrayList1;
/*     */     }
/*     */     catch (IOException localIOException2)
/*     */     {
/*     */       return null;
/*     */     } finally {
/*  80 */       if (inputStream != null)
/*     */         try {
/*  82 */           inputStream.close();
/*     */         }
/*     */         catch (IOException localIOException4) {
/*     */         }
/*     */     }
/*  87 */     throw localObject;
/*     */   }
/*     */   public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName) {
/*  90 */     return findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, false);
/*     */   }
/*     */   public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
/*  93 */     if (!isPackage(qualifiedPackageName))
/*  94 */       return null;
/*     */     try
/*     */     {
/*  97 */       ClassFileReader reader = ClassFileReader.read(this.zipFile, qualifiedBinaryFileName);
/*  98 */       if (reader != null)
/*  99 */         return new NameEnvironmentAnswer(reader, fetchAccessRestriction(qualifiedBinaryFileName));
/*     */     }
/*     */     catch (ClassFormatException localClassFormatException) {
/*     */     }
/*     */     catch (IOException localIOException) {
/*     */     }
/* 105 */     return null;
/*     */   }
/*     */   public char[][][] findTypeNames(String qualifiedPackageName) {
/* 108 */     if (!isPackage(qualifiedPackageName)) {
/* 109 */       return null;
/*     */     }
/* 111 */     ArrayList answers = new ArrayList();
/* 112 */     for (Enumeration e = this.zipFile.entries(); e.hasMoreElements(); ) {
/* 113 */       String fileName = ((ZipEntry)e.nextElement()).getName();
/*     */ 
/* 116 */       int last = fileName.lastIndexOf('/');
/* 117 */       while (last > 0)
/*     */       {
/* 119 */         String packageName = fileName.substring(0, last);
/* 120 */         if (!qualifiedPackageName.equals(packageName))
/*     */           break;
/* 122 */         int indexOfDot = fileName.lastIndexOf('.');
/* 123 */         if (indexOfDot != -1) {
/* 124 */           String typeName = fileName.substring(last + 1, indexOfDot);
/* 125 */           char[] packageArray = packageName.toCharArray();
/* 126 */           answers.add(
/* 127 */             CharOperation.arrayConcat(
/* 128 */             CharOperation.splitOn('/', packageArray), 
/* 129 */             typeName.toCharArray()));
/*     */         }
/*     */       }
/*     */     }
/* 133 */     int size = answers.size();
/* 134 */     if (size != 0) {
/* 135 */       char[][][] result = new char[size][][];
/* 136 */       answers.toArray(result);
/* 137 */       return null;
/*     */     }
/* 139 */     return null;
/*     */   }
/*     */   public void initialize() throws IOException {
/* 142 */     if (this.zipFile == null)
/* 143 */       this.zipFile = new ZipFile(this.file);
/*     */   }
/*     */ 
/*     */   public boolean isPackage(String qualifiedPackageName) {
/* 147 */     if (this.packageCache != null) {
/* 148 */       return this.packageCache.containsKey(qualifiedPackageName);
/*     */     }
/* 150 */     this.packageCache = new Hashtable(41);
/* 151 */     this.packageCache.put(Util.EMPTY_STRING, Util.EMPTY_STRING);
/*     */ 
/* 153 */     for (Enumeration e = this.zipFile.entries(); e.hasMoreElements(); ) {
/* 154 */       String fileName = ((ZipEntry)e.nextElement()).getName();
/*     */ 
/* 157 */       int last = fileName.lastIndexOf('/');
/* 158 */       while (last > 0)
/*     */       {
/* 160 */         String packageName = fileName.substring(0, last);
/* 161 */         if (this.packageCache.containsKey(packageName))
/*     */           break;
/* 163 */         this.packageCache.put(packageName, packageName);
/* 164 */         last = packageName.lastIndexOf('/');
/*     */       }
/*     */     }
/* 167 */     return this.packageCache.containsKey(qualifiedPackageName);
/*     */   }
/*     */   public void reset() {
/* 170 */     if ((this.zipFile != null) && (this.closeZipFileAtEnd)) {
/*     */       try {
/* 172 */         this.zipFile.close();
/*     */       }
/*     */       catch (IOException localIOException) {
/*     */       }
/* 176 */       this.zipFile = null;
/*     */     }
/* 178 */     this.packageCache = null;
/*     */   }
/*     */   public String toString() {
/* 181 */     return "Classpath for jar file " + this.file.getPath();
/*     */   }
/*     */   public char[] normalizedPath() {
/* 184 */     if (this.normalizedPath == null) {
/* 185 */       char[] rawName = getPath().toCharArray();
/* 186 */       if (File.separatorChar == '\\') {
/* 187 */         CharOperation.replace(rawName, '\\', '/');
/*     */       }
/* 189 */       this.normalizedPath = CharOperation.subarray(rawName, 0, CharOperation.lastIndexOf('.', rawName));
/*     */     }
/* 191 */     return this.normalizedPath;
/*     */   }
/*     */   public String getPath() {
/* 194 */     if (this.path == null) {
/*     */       try {
/* 196 */         this.path = this.file.getCanonicalPath();
/*     */       }
/*     */       catch (IOException localIOException) {
/* 199 */         this.path = this.file.getAbsolutePath();
/*     */       }
/*     */     }
/* 202 */     return this.path;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.batch.ClasspathJar
 * JD-Core Version:    0.6.0
 */