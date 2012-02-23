/*     */ package org.eclipse.jdt.internal.compiler.tool;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import javax.annotation.processing.Processor;
/*     */ import javax.tools.Diagnostic;
/*     */ import javax.tools.Diagnostic.Kind;
/*     */ import javax.tools.DiagnosticListener;
/*     */ import javax.tools.FileObject;
/*     */ import javax.tools.JavaFileManager;
/*     */ import javax.tools.JavaFileObject;
/*     */ import javax.tools.JavaFileObject.Kind;
/*     */ import javax.tools.StandardJavaFileManager;
/*     */ import javax.tools.StandardLocation;
/*     */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.core.compiler.CompilationProgress;
/*     */ import org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager;
/*     */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.Compiler;
/*     */ import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
/*     */ import org.eclipse.jdt.internal.compiler.IProblemFactory;
/*     */ import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.batch.FileSystem;
/*     */ import org.eclipse.jdt.internal.compiler.batch.FileSystem.Classpath;
/*     */ import org.eclipse.jdt.internal.compiler.batch.FileSystem.ClasspathNormalizer;
/*     */ import org.eclipse.jdt.internal.compiler.batch.Main;
/*     */ import org.eclipse.jdt.internal.compiler.batch.Main.Logger;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
/*     */ import org.eclipse.jdt.internal.compiler.util.Messages;
/*     */ import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
/*     */ 
/*     */ public class EclipseCompilerImpl extends Main
/*     */ {
/*  49 */   private static final CompilationUnit[] NO_UNITS = new CompilationUnit[0];
/*     */   private HashMap<CompilationUnit, JavaFileObject> javaFileObjectMap;
/*     */   Iterable<? extends JavaFileObject> compilationUnits;
/*     */   public JavaFileManager fileManager;
/*     */   protected Processor[] processors;
/*     */   public DiagnosticListener<? super JavaFileObject> diagnosticListener;
/*     */ 
/*     */   public EclipseCompilerImpl(PrintWriter out, PrintWriter err, boolean systemExitWhenFinished)
/*     */   {
/*  57 */     super(out, err, systemExitWhenFinished, null, null);
/*     */   }
/*     */ 
/*     */   public boolean call() {
/*     */     try {
/*  62 */       if (this.proceed) {
/*  63 */         this.globalProblemsCount = 0;
/*  64 */         this.globalErrorsCount = 0;
/*  65 */         this.globalWarningsCount = 0;
/*  66 */         this.globalTasksCount = 0;
/*  67 */         this.exportedClassFilesCounter = 0;
/*     */ 
/*  69 */         performCompilation();
/*     */       }
/*     */     } catch (IllegalArgumentException e) {
/*  72 */       this.logger.logException(e);
/*  73 */       if (this.systemExitWhenFinished) {
/*  74 */         cleanup();
/*  75 */         System.exit(-1);
/*     */       }
/*     */       return false;
/*     */     } catch (RuntimeException e) {
/*  79 */       this.logger.logException(e);
/*     */       return false;
/*     */     } finally {
/*  82 */       cleanup(); } cleanup();
/*     */ 
/*  85 */     return this.globalErrorsCount == 0;
/*     */   }
/*     */ 
/*     */   private void cleanup()
/*     */   {
/*  90 */     this.logger.flush();
/*  91 */     this.logger.close();
/*  92 */     this.processors = null;
/*     */     try {
/*  94 */       if (this.fileManager != null)
/*  95 */         this.fileManager.flush();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public CompilationUnit[] getCompilationUnits()
/*     */   {
/* 104 */     if (this.compilationUnits == null) return NO_UNITS;
/* 105 */     ArrayList units = new ArrayList();
/* 106 */     for (JavaFileObject javaFileObject : this.compilationUnits) {
/* 107 */       if (javaFileObject.getKind() != JavaFileObject.Kind.SOURCE) {
/* 108 */         throw new IllegalArgumentException();
/*     */       }
/* 110 */       String name = javaFileObject.getName();
/* 111 */       name = name.replace('\\', '/');
/* 112 */       CompilationUnit compilationUnit = new CompilationUnit(null, 
/* 113 */         name, 
/* 114 */         null, javaFileObject)
/*     */       {
/*     */         public char[] getContents()
/*     */         {
/*     */           try
/*     */           {
/* 119 */             return this.val$javaFileObject.getCharContent(true).toString().toCharArray();
/*     */           } catch (IOException e) {
/* 121 */             e.printStackTrace();
/* 122 */           }throw new AbortCompilationUnit(null, e, null);
/*     */         }
/*     */       };
/* 126 */       units.add(compilationUnit);
/* 127 */       this.javaFileObjectMap.put(compilationUnit, javaFileObject);
/*     */     }
/* 129 */     CompilationUnit[] result = new CompilationUnit[units.size()];
/* 130 */     units.toArray(result);
/* 131 */     return result;
/*     */   }
/*     */ 
/*     */   public IErrorHandlingPolicy getHandlingPolicy()
/*     */   {
/* 139 */     return new IErrorHandlingPolicy() {
/*     */       public boolean proceedOnErrors() {
/* 141 */         return false;
/*     */       }
/*     */       public boolean stopOnFirstError() {
/* 144 */         return false;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public IProblemFactory getProblemFactory() {
/* 151 */     return new DefaultProblemFactory()
/*     */     {
/*     */       public CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments, String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber, int columnNumber)
/*     */       {
/* 164 */         DiagnosticListener diagListener = EclipseCompilerImpl.this.diagnosticListener;
/* 165 */         if (diagListener != null)
/* 166 */           diagListener.report(new Diagnostic(problemId, columnNumber, endPosition, severity, lineNumber, problemArguments, startPosition, originatingFileName) {
/*     */             public String getCode() {
/* 168 */               return Integer.toString(this.val$problemId);
/*     */             }
/*     */             public long getColumnNumber() {
/* 171 */               return this.val$columnNumber;
/*     */             }
/*     */             public long getEndPosition() {
/* 174 */               return this.val$endPosition;
/*     */             }
/*     */             public Diagnostic.Kind getKind() {
/* 177 */               if ((this.val$severity & 0x1) != 0) {
/* 178 */                 return Diagnostic.Kind.ERROR;
/*     */               }
/* 180 */               if ((this.val$severity & 0x20) != 0) {
/* 181 */                 return Diagnostic.Kind.WARNING;
/*     */               }
/* 183 */               if (0 != 0) {
/* 184 */                 return Diagnostic.Kind.MANDATORY_WARNING;
/*     */               }
/* 186 */               return Diagnostic.Kind.OTHER;
/*     */             }
/*     */             public long getLineNumber() {
/* 189 */               return this.val$lineNumber;
/*     */             }
/*     */             public String getMessage(Locale locale) {
/* 192 */               EclipseCompilerImpl.3.this.setLocale(locale);
/* 193 */               return EclipseCompilerImpl.3.this.getLocalizedMessage(this.val$problemId, this.val$problemArguments);
/*     */             }
/*     */             public long getPosition() {
/* 196 */               return this.val$startPosition;
/*     */             }
/*     */             public JavaFileObject getSource() {
/*     */               try {
/* 200 */                 if (EclipseCompilerImpl.this.fileManager.hasLocation(StandardLocation.SOURCE_PATH))
/* 201 */                   return EclipseCompilerImpl.this.fileManager.getJavaFileForInput(
/* 202 */                     StandardLocation.SOURCE_PATH, 
/* 203 */                     new String(this.val$originatingFileName), 
/* 204 */                     JavaFileObject.Kind.SOURCE);
/*     */               }
/*     */               catch (IOException localIOException)
/*     */               {
/*     */               }
/* 209 */               return null;
/*     */             }
/*     */             public long getStartPosition() {
/* 212 */               return this.val$startPosition;
/*     */             }
/*     */           });
/* 216 */         return super.createProblem(originatingFileName, problemId, problemArguments, messageArguments, severity, startPosition, endPosition, lineNumber, columnNumber);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map customDefaultOptions, CompilationProgress compilationProgress)
/*     */   {
/* 224 */     super.initialize(outWriter, errWriter, systemExit, customDefaultOptions, compilationProgress);
/* 225 */     this.javaFileObjectMap = new HashMap();
/*     */   }
/*     */ 
/*     */   protected void initializeAnnotationProcessorManager()
/*     */   {
/* 230 */     super.initializeAnnotationProcessorManager();
/* 231 */     if ((this.batchCompiler.annotationProcessorManager != null) && (this.processors != null))
/* 232 */       this.batchCompiler.annotationProcessorManager.setProcessors(this.processors);
/* 233 */     else if (this.processors != null)
/* 234 */       throw new UnsupportedOperationException("Cannot handle annotation processing");
/*     */   }
/*     */ 
/*     */   public void outputClassFiles(CompilationResult unitResult)
/*     */   {
/* 242 */     if ((unitResult != null) && ((!unitResult.hasErrors()) || (this.proceedOnError))) {
/* 243 */       ClassFile[] classFiles = unitResult.getClassFiles();
/* 244 */       boolean generateClasspathStructure = this.fileManager.hasLocation(StandardLocation.CLASS_OUTPUT);
/* 245 */       String currentDestinationPath = this.destinationPath;
/* 246 */       File outputLocation = null;
/* 247 */       if (currentDestinationPath != null) {
/* 248 */         outputLocation = new File(currentDestinationPath);
/* 249 */         outputLocation.mkdirs();
/*     */       }
/* 251 */       int i = 0; for (int fileCount = classFiles.length; i < fileCount; i++)
/*     */       {
/* 253 */         ClassFile classFile = classFiles[i];
/* 254 */         char[] filename = classFile.fileName();
/* 255 */         int length = filename.length;
/* 256 */         char[] relativeName = new char[length + 6];
/* 257 */         System.arraycopy(filename, 0, relativeName, 0, length);
/* 258 */         System.arraycopy(SuffixConstants.SUFFIX_class, 0, relativeName, length, 6);
/* 259 */         CharOperation.replace(relativeName, '/', File.separatorChar);
/* 260 */         String relativeStringName = new String(relativeName);
/* 261 */         if (this.compilerOptions.verbose) {
/* 262 */           this.out.println(
/* 263 */             Messages.bind(
/* 264 */             Messages.compilation_write, 
/* 265 */             new String[] { 
/* 266 */             String.valueOf(this.exportedClassFilesCounter + 1), 
/* 267 */             relativeStringName }));
/*     */         }
/*     */         try
/*     */         {
/* 271 */           JavaFileObject javaFileForOutput = 
/* 272 */             this.fileManager.getJavaFileForOutput(
/* 273 */             StandardLocation.CLASS_OUTPUT, 
/* 274 */             new String(filename), 
/* 275 */             JavaFileObject.Kind.CLASS, 
/* 276 */             (FileObject)this.javaFileObjectMap.get(unitResult.compilationUnit));
/*     */ 
/* 278 */           if (generateClasspathStructure) {
/* 279 */             if (currentDestinationPath != null) {
/* 280 */               int index = CharOperation.lastIndexOf(File.separatorChar, relativeName);
/* 281 */               if (index != -1) {
/* 282 */                 File currentFolder = new File(currentDestinationPath, relativeStringName.substring(0, index));
/* 283 */                 currentFolder.mkdirs();
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 288 */               String path = javaFileForOutput.toUri().getPath();
/* 289 */               int index = path.lastIndexOf('/');
/* 290 */               if (index != -1) {
/* 291 */                 File file = new File(path.substring(0, index));
/* 292 */                 file.mkdirs();
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 297 */           OutputStream openOutputStream = javaFileForOutput.openOutputStream();
/* 298 */           BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(openOutputStream);
/* 299 */           bufferedOutputStream.write(classFile.header, 0, classFile.headerOffset);
/* 300 */           bufferedOutputStream.write(classFile.contents, 0, classFile.contentsOffset);
/* 301 */           bufferedOutputStream.flush();
/* 302 */           bufferedOutputStream.close();
/*     */         } catch (IOException e) {
/* 304 */           this.logger.logNoClassFileCreated(currentDestinationPath, relativeStringName, e);
/*     */         }
/* 306 */         this.logger.logClassFile(
/* 307 */           generateClasspathStructure, 
/* 308 */           currentDestinationPath, 
/* 309 */           relativeStringName);
/* 310 */         this.exportedClassFilesCounter += 1;
/*     */       }
/* 312 */       this.batchCompiler.lookupEnvironment.releaseClassFiles(classFiles);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setPaths(ArrayList bootclasspaths, String sourcepathClasspathArg, ArrayList sourcepathClasspaths, ArrayList classpaths, ArrayList extdirsClasspaths, ArrayList endorsedDirClasspaths, String customEncoding)
/*     */   {
/* 326 */     ArrayList fileSystemClasspaths = new ArrayList();
/* 327 */     EclipseFileManager javaFileManager = null;
/* 328 */     StandardJavaFileManager standardJavaFileManager = null;
/* 329 */     if ((this.fileManager instanceof EclipseFileManager)) {
/* 330 */       javaFileManager = (EclipseFileManager)this.fileManager;
/*     */     }
/* 332 */     if ((this.fileManager instanceof StandardJavaFileManager)) {
/* 333 */       standardJavaFileManager = (StandardJavaFileManager)this.fileManager;
/*     */     }
/*     */ 
/* 336 */     if ((javaFileManager != null) && 
/* 337 */       ((javaFileManager.flags & 0x4) == 0) && 
/* 338 */       ((javaFileManager.flags & 0x2) != 0)) {
/* 339 */       fileSystemClasspaths.addAll(handleEndorseddirs(null));
/*     */     }
/*     */ 
/* 342 */     Iterable location = null;
/* 343 */     if (standardJavaFileManager != null) {
/* 344 */       location = standardJavaFileManager.getLocation(StandardLocation.PLATFORM_CLASS_PATH);
/*     */     }
/* 346 */     if (location != null) {
/* 347 */       for (File file : location) {
/* 348 */         FileSystem.Classpath classpath = FileSystem.getClasspath(
/* 349 */           file.getAbsolutePath(), 
/* 350 */           null, 
/* 351 */           null);
/* 352 */         if (classpath != null) {
/* 353 */           fileSystemClasspaths.add(classpath);
/*     */         }
/*     */       }
/*     */     }
/* 357 */     if ((javaFileManager != null) && 
/* 358 */       ((javaFileManager.flags & 0x1) == 0) && 
/* 359 */       ((javaFileManager.flags & 0x2) != 0)) {
/* 360 */       fileSystemClasspaths.addAll(handleExtdirs(null));
/*     */     }
/*     */ 
/* 363 */     if (standardJavaFileManager != null)
/* 364 */       location = standardJavaFileManager.getLocation(StandardLocation.SOURCE_PATH);
/*     */     else {
/* 366 */       location = null;
/*     */     }
/* 368 */     if (location != null) {
/* 369 */       for (File file : location) {
/* 370 */         FileSystem.Classpath classpath = FileSystem.getClasspath(
/* 371 */           file.getAbsolutePath(), 
/* 372 */           null, 
/* 373 */           null);
/* 374 */         if (classpath != null) {
/* 375 */           fileSystemClasspaths.add(classpath);
/*     */         }
/*     */       }
/*     */     }
/* 379 */     if (standardJavaFileManager != null)
/* 380 */       location = standardJavaFileManager.getLocation(StandardLocation.CLASS_PATH);
/*     */     else {
/* 382 */       location = null;
/*     */     }
/* 384 */     if (location != null) {
/* 385 */       for (File file : location) {
/* 386 */         FileSystem.Classpath classpath = FileSystem.getClasspath(
/* 387 */           file.getAbsolutePath(), 
/* 388 */           null, 
/* 389 */           null);
/* 390 */         if (classpath != null) {
/* 391 */           fileSystemClasspaths.add(classpath);
/*     */         }
/*     */       }
/*     */     }
/* 395 */     if (this.checkedClasspaths == null) {
/* 396 */       fileSystemClasspaths.addAll(handleBootclasspath(null, null));
/* 397 */       fileSystemClasspaths.addAll(handleClasspath(null, null));
/*     */     }
/* 399 */     fileSystemClasspaths = FileSystem.ClasspathNormalizer.normalize(fileSystemClasspaths);
/* 400 */     int size = fileSystemClasspaths.size();
/* 401 */     if (size != 0) {
/* 402 */       this.checkedClasspaths = new FileSystem.Classpath[size];
/* 403 */       int i = 0;
/* 404 */       for (FileSystem.Classpath classpath : fileSystemClasspaths)
/* 405 */         this.checkedClasspaths[(i++)] = classpath;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl
 * JD-Core Version:    0.6.0
 */