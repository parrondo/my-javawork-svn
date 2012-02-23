/*     */ package org.eclipse.jdt.internal.compiler.batch;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
/*     */ import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
/*     */ import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class FileSystem
/*     */   implements INameEnvironment, SuffixConstants
/*     */ {
/*     */   Classpath[] classpaths;
/*     */   Set knownFileNames;
/*     */ 
/*     */   public FileSystem(String[] classpathNames, String[] initialFileNames, String encoding)
/*     */   {
/* 112 */     int classpathSize = classpathNames.length;
/* 113 */     this.classpaths = new Classpath[classpathSize];
/* 114 */     int counter = 0;
/* 115 */     for (int i = 0; i < classpathSize; i++) {
/* 116 */       Classpath classpath = getClasspath(classpathNames[i], encoding, null);
/*     */       try {
/* 118 */         classpath.initialize();
/* 119 */         this.classpaths[(counter++)] = classpath;
/*     */       }
/*     */       catch (IOException localIOException) {
/*     */       }
/*     */     }
/* 124 */     if (counter != classpathSize) {
/* 125 */       System.arraycopy(this.classpaths, 0, this.classpaths = new Classpath[counter], 0, counter);
/*     */     }
/* 127 */     initializeKnownFileNames(initialFileNames);
/*     */   }
/*     */   FileSystem(Classpath[] paths, String[] initialFileNames) {
/* 130 */     int length = paths.length;
/* 131 */     int counter = 0;
/* 132 */     this.classpaths = new Classpath[length];
/* 133 */     for (int i = 0; i < length; i++) {
/* 134 */       Classpath classpath = paths[i];
/*     */       try {
/* 136 */         classpath.initialize();
/* 137 */         this.classpaths[(counter++)] = classpath;
/*     */       }
/*     */       catch (IOException localIOException) {
/*     */       }
/*     */     }
/* 142 */     if (counter != length)
/*     */     {
/* 144 */       System.arraycopy(this.classpaths, 0, this.classpaths = new Classpath[counter], 0, counter);
/*     */     }
/* 146 */     initializeKnownFileNames(initialFileNames);
/*     */   }
/*     */   public static Classpath getClasspath(String classpathName, String encoding, AccessRuleSet accessRuleSet) {
/* 149 */     return getClasspath(classpathName, encoding, false, accessRuleSet, null);
/*     */   }
/*     */ 
/*     */   public static Classpath getClasspath(String classpathName, String encoding, boolean isSourceOnly, AccessRuleSet accessRuleSet, String destinationPath)
/*     */   {
/* 154 */     Classpath result = null;
/* 155 */     File file = new File(convertPathSeparators(classpathName));
/* 156 */     if (file.isDirectory()) {
/* 157 */       if (file.exists()) {
/* 158 */         result = new ClasspathDirectory(file, encoding, 
/* 159 */           isSourceOnly ? 1 : 
/* 160 */           3, 
/* 161 */           accessRuleSet, 
/* 162 */           (destinationPath == null) || (destinationPath == "none") ? 
/* 163 */           destinationPath : 
/* 164 */           convertPathSeparators(destinationPath));
/*     */       }
/*     */     }
/* 167 */     else if (Util.isPotentialZipArchive(classpathName)) {
/* 168 */       if (isSourceOnly)
/*     */       {
/* 170 */         result = new ClasspathSourceJar(file, true, accessRuleSet, 
/* 171 */           encoding, 
/* 172 */           (destinationPath == null) || (destinationPath == "none") ? 
/* 173 */           destinationPath : 
/* 174 */           convertPathSeparators(destinationPath));
/* 175 */       } else if (destinationPath == null)
/*     */       {
/* 177 */         result = new ClasspathJar(file, true, accessRuleSet, null);
/*     */       }
/*     */     }
/*     */ 
/* 181 */     return result;
/*     */   }
/*     */   private void initializeKnownFileNames(String[] initialFileNames) {
/* 184 */     if (initialFileNames == null) {
/* 185 */       this.knownFileNames = new HashSet(0);
/* 186 */       return; } this.knownFileNames = new HashSet(initialFileNames.length * 2);
/* 189 */     int i = initialFileNames.length;
/*     */     label250: 
/*     */     do { File compilationUnitFile = new File(initialFileNames[i]);
/* 191 */       char[] fileName = (char[])null;
/*     */       try {
/* 193 */         fileName = compilationUnitFile.getCanonicalPath().toCharArray();
/*     */       }
/*     */       catch (IOException localIOException) {
/* 196 */         break label250;
/*     */       }
/* 198 */       char[] matchingPathName = (char[])null;
/* 199 */       int lastIndexOf = CharOperation.lastIndexOf('.', fileName);
/* 200 */       if (lastIndexOf != -1) {
/* 201 */         fileName = CharOperation.subarray(fileName, 0, lastIndexOf);
/*     */       }
/* 203 */       CharOperation.replace(fileName, '\\', '/');
/* 204 */       int j = 0; for (int max = this.classpaths.length; j < max; j++) {
/* 205 */         char[] matchCandidate = this.classpaths[j].normalizedPath();
/* 206 */         if ((!(this.classpaths[j] instanceof ClasspathDirectory)) || 
/* 207 */           (!CharOperation.prefixEquals(matchCandidate, fileName)) || (
/* 208 */           (matchingPathName != null) && 
/* 209 */           (matchCandidate.length >= matchingPathName.length))) continue;
/* 210 */         matchingPathName = matchCandidate;
/*     */       }
/*     */ 
/* 213 */       if (matchingPathName == null)
/* 214 */         this.knownFileNames.add(new String(fileName));
/*     */       else {
/* 216 */         this.knownFileNames.add(new String(CharOperation.subarray(fileName, matchingPathName.length, fileName.length)));
/*     */       }
/* 218 */       matchingPathName = (char[])null;
/*     */ 
/* 189 */       i--; } while (i >= 0);
/*     */   }
/*     */ 
/*     */   public void cleanup()
/*     */   {
/* 222 */     int i = 0; for (int max = this.classpaths.length; i < max; i++)
/* 223 */       this.classpaths[i].reset(); 
/*     */   }
/*     */ 
/*     */   private static String convertPathSeparators(String path) {
/* 226 */     return File.separatorChar == '/' ? 
/* 227 */       path.replace('\\', '/') : 
/* 228 */       path.replace('/', '\\');
/*     */   }
/*     */   private NameEnvironmentAnswer findClass(String qualifiedTypeName, char[] typeName, boolean asBinaryOnly) {
/* 231 */     if (this.knownFileNames.contains(qualifiedTypeName)) return null;
/*     */ 
/* 233 */     String qualifiedBinaryFileName = qualifiedTypeName + ".class";
/* 234 */     String qualifiedPackageName = 
/* 235 */       qualifiedTypeName.length() == typeName.length ? 
/* 236 */       Util.EMPTY_STRING : 
/* 237 */       qualifiedBinaryFileName.substring(0, qualifiedTypeName.length() - typeName.length - 1);
/* 238 */     String qp2 = File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
/* 239 */     NameEnvironmentAnswer suggestedAnswer = null;
/* 240 */     if (qualifiedPackageName == qp2) {
/* 241 */       int i = 0; for (int length = this.classpaths.length; i < length; i++) {
/* 242 */         NameEnvironmentAnswer answer = this.classpaths[i].findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, asBinaryOnly);
/* 243 */         if (answer != null)
/* 244 */           if (!answer.ignoreIfBetter()) {
/* 245 */             if (answer.isBetter(suggestedAnswer))
/* 246 */               return answer; 
/*     */           } else {
/* 247 */             if (!answer.isBetter(suggestedAnswer))
/*     */               continue;
/* 249 */             suggestedAnswer = answer;
/*     */           }
/*     */       }
/*     */     } else {
/* 253 */       String qb2 = qualifiedBinaryFileName.replace('/', File.separatorChar);
/* 254 */       int i = 0; for (int length = this.classpaths.length; i < length; i++) {
/* 255 */         Classpath p = this.classpaths[i];
/* 256 */         NameEnvironmentAnswer answer = (p instanceof ClasspathJar) ? 
/* 257 */           p.findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, asBinaryOnly) : 
/* 258 */           p.findClass(typeName, qp2, qb2, asBinaryOnly);
/* 259 */         if (answer != null)
/* 260 */           if (!answer.ignoreIfBetter()) {
/* 261 */             if (answer.isBetter(suggestedAnswer))
/* 262 */               return answer; 
/*     */           } else {
/* 263 */             if (!answer.isBetter(suggestedAnswer))
/*     */               continue;
/* 265 */             suggestedAnswer = answer;
/*     */           }
/*     */       }
/*     */     }
/* 269 */     if (suggestedAnswer != null)
/*     */     {
/* 271 */       return suggestedAnswer;
/* 272 */     }return null;
/*     */   }
/*     */   public NameEnvironmentAnswer findType(char[][] compoundName) {
/* 275 */     if (compoundName != null)
/* 276 */       return findClass(
/* 277 */         new String(CharOperation.concatWith(compoundName, '/')), 
/* 278 */         compoundName[(compoundName.length - 1)], 
/* 279 */         false);
/* 280 */     return null;
/*     */   }
/*     */   public char[][][] findTypeNames(char[][] packageName) {
/* 283 */     char[][][] result = (char[][][])null;
/* 284 */     if (packageName != null) {
/* 285 */       String qualifiedPackageName = new String(CharOperation.concatWith(packageName, '/'));
/* 286 */       String qualifiedPackageName2 = File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
/* 287 */       if (qualifiedPackageName == qualifiedPackageName2) {
/* 288 */         int i = 0; for (int length = this.classpaths.length; i < length; i++) {
/* 289 */           char[][][] answers = this.classpaths[i].findTypeNames(qualifiedPackageName);
/* 290 */           if (answers == null)
/*     */             continue;
/* 292 */           if (result == null) {
/* 293 */             result = answers;
/*     */           } else {
/* 295 */             int resultLength = result.length;
/* 296 */             int answersLength = answers.length;
/* 297 */             System.arraycopy(result, 0, result = new char[answersLength + resultLength][][], 0, resultLength);
/* 298 */             System.arraycopy(answers, 0, result, resultLength, answersLength);
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 303 */         int i = 0; for (int length = this.classpaths.length; i < length; i++) {
/* 304 */           Classpath p = this.classpaths[i];
/* 305 */           char[][][] answers = (p instanceof ClasspathJar) ? 
/* 306 */             p.findTypeNames(qualifiedPackageName) : 
/* 307 */             p.findTypeNames(qualifiedPackageName2);
/* 308 */           if (answers == null)
/*     */             continue;
/* 310 */           if (result == null) {
/* 311 */             result = answers;
/*     */           } else {
/* 313 */             int resultLength = result.length;
/* 314 */             int answersLength = answers.length;
/* 315 */             System.arraycopy(result, 0, result = new char[answersLength + resultLength][][], 0, resultLength);
/* 316 */             System.arraycopy(answers, 0, result, resultLength, answersLength);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 322 */     return result;
/*     */   }
/*     */   public NameEnvironmentAnswer findType(char[][] compoundName, boolean asBinaryOnly) {
/* 325 */     if (compoundName != null)
/* 326 */       return findClass(
/* 327 */         new String(CharOperation.concatWith(compoundName, '/')), 
/* 328 */         compoundName[(compoundName.length - 1)], 
/* 329 */         asBinaryOnly);
/* 330 */     return null;
/*     */   }
/*     */   public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
/* 333 */     if (typeName != null)
/* 334 */       return findClass(
/* 335 */         new String(CharOperation.concatWith(packageName, typeName, '/')), 
/* 336 */         typeName, 
/* 337 */         false);
/* 338 */     return null;
/*     */   }
/*     */   public boolean isPackage(char[][] compoundName, char[] packageName) {
/* 341 */     String qualifiedPackageName = new String(CharOperation.concatWith(compoundName, packageName, '/'));
/* 342 */     String qp2 = File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
/* 343 */     if (qualifiedPackageName == qp2) {
/* 344 */       int i = 0; for (int length = this.classpaths.length; i < length; i++)
/* 345 */         if (this.classpaths[i].isPackage(qualifiedPackageName))
/* 346 */           return true;
/*     */     } else {
/* 348 */       int i = 0; for (int length = this.classpaths.length; i < length; i++) {
/* 349 */         Classpath p = this.classpaths[i];
/* 350 */         if ((p instanceof ClasspathJar) ? p.isPackage(qualifiedPackageName) : p.isPackage(qp2))
/* 351 */           return true;
/*     */       }
/*     */     }
/* 354 */     return false;
/*     */   }
/*     */ 
/*     */   public static abstract interface Classpath
/*     */   {
/*     */     public abstract char[][][] findTypeNames(String paramString);
/*     */ 
/*     */     public abstract NameEnvironmentAnswer findClass(char[] paramArrayOfChar, String paramString1, String paramString2);
/*     */ 
/*     */     public abstract NameEnvironmentAnswer findClass(char[] paramArrayOfChar, String paramString1, String paramString2, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isPackage(String paramString);
/*     */ 
/*     */     public abstract List fetchLinkedJars(FileSystem.ClasspathSectionProblemReporter paramClasspathSectionProblemReporter);
/*     */ 
/*     */     public abstract void reset();
/*     */ 
/*     */     public abstract char[] normalizedPath();
/*     */ 
/*     */     public abstract String getPath();
/*     */ 
/*     */     public abstract void initialize()
/*     */       throws IOException;
/*     */   }
/*     */ 
/*     */   public static class ClasspathNormalizer
/*     */   {
/*     */     public static ArrayList normalize(ArrayList classpaths)
/*     */     {
/*  90 */       ArrayList normalizedClasspath = new ArrayList();
/*  91 */       HashSet cache = new HashSet();
/*  92 */       for (Iterator iterator = classpaths.iterator(); iterator.hasNext(); ) {
/*  93 */         FileSystem.Classpath classpath = (FileSystem.Classpath)iterator.next();
/*  94 */         String path = classpath.getPath();
/*  95 */         if (!cache.contains(path)) {
/*  96 */           normalizedClasspath.add(classpath);
/*  97 */           cache.add(path);
/*     */         }
/*     */       }
/* 100 */       return normalizedClasspath;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface ClasspathSectionProblemReporter
/*     */   {
/*     */     public abstract void invalidClasspathSection(String paramString);
/*     */ 
/*     */     public abstract void multipleClasspathSections(String paramString);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.batch.FileSystem
 * JD-Core Version:    0.6.0
 */