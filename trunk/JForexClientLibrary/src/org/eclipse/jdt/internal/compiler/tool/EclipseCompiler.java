/*     */ package org.eclipse.jdt.internal.compiler.tool;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.annotation.processing.Processor;
/*     */ import javax.lang.model.SourceVersion;
/*     */ import javax.tools.DiagnosticListener;
/*     */ import javax.tools.JavaCompiler;
/*     */ import javax.tools.JavaCompiler.CompilationTask;
/*     */ import javax.tools.JavaFileManager;
/*     */ import javax.tools.JavaFileObject;
/*     */ import javax.tools.StandardJavaFileManager;
/*     */ import javax.tools.StandardLocation;
/*     */ import org.eclipse.jdt.internal.compiler.batch.Main;
/*     */ 
/*     */ public class EclipseCompiler
/*     */   implements JavaCompiler
/*     */ {
/*     */   private static Set<SourceVersion> SupportedSourceVersions;
/*     */   WeakHashMap<Thread, EclipseCompilerImpl> threadCache;
/*     */   public DiagnosticListener<? super JavaFileObject> diagnosticListener;
/*     */ 
/*     */   static
/*     */   {
/*  51 */     EnumSet enumSet = EnumSet.range(SourceVersion.RELEASE_0, SourceVersion.RELEASE_6);
/*     */ 
/*  53 */     SupportedSourceVersions = Collections.unmodifiableSet(enumSet);
/*     */   }
/*     */ 
/*     */   public EclipseCompiler()
/*     */   {
/*  60 */     this.threadCache = new WeakHashMap();
/*     */   }
/*     */ 
/*     */   public Set<SourceVersion> getSourceVersions()
/*     */   {
/*  68 */     return SupportedSourceVersions;
/*     */   }
/*     */ 
/*     */   public StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> someDiagnosticListener, Locale locale, Charset charset)
/*     */   {
/*  77 */     this.diagnosticListener = someDiagnosticListener;
/*  78 */     return new EclipseFileManager(locale, charset);
/*     */   }
/*     */ 
/*     */   public JavaCompiler.CompilationTask getTask(Writer out, JavaFileManager fileManager, DiagnosticListener<? super JavaFileObject> someDiagnosticListener, Iterable<String> options, Iterable<String> classes, Iterable<? extends JavaFileObject> compilationUnits)
/*     */   {
/*  89 */     PrintWriter writerOut = null;
/*  90 */     PrintWriter writerErr = null;
/*  91 */     if (out == null) {
/*  92 */       writerOut = new PrintWriter(System.out);
/*  93 */       writerErr = new PrintWriter(System.err);
/*     */     } else {
/*  95 */       writerOut = new PrintWriter(out);
/*  96 */       writerErr = new PrintWriter(out);
/*     */     }
/*  98 */     Thread currentThread = Thread.currentThread();
/*  99 */     EclipseCompilerImpl eclipseCompiler = (EclipseCompilerImpl)this.threadCache.get(currentThread);
/* 100 */     if (eclipseCompiler == null) {
/* 101 */       eclipseCompiler = new EclipseCompilerImpl(writerOut, writerErr, false);
/* 102 */       this.threadCache.put(currentThread, eclipseCompiler);
/*     */     } else {
/* 104 */       eclipseCompiler.initialize(writerOut, writerErr, false, null, null);
/*     */     }
/* 106 */     EclipseCompilerImpl eclipseCompiler2 = new EclipseCompilerImpl(writerOut, writerErr, false);
/* 107 */     eclipseCompiler2.compilationUnits = compilationUnits;
/* 108 */     eclipseCompiler2.diagnosticListener = someDiagnosticListener;
/* 109 */     if (fileManager != null)
/* 110 */       eclipseCompiler2.fileManager = fileManager;
/*     */     else {
/* 112 */       eclipseCompiler2.fileManager = getStandardFileManager(someDiagnosticListener, null, null);
/*     */     }
/*     */ 
/* 115 */     eclipseCompiler2.options.put("org.eclipse.jdt.core.compiler.compliance", "1.6");
/* 116 */     eclipseCompiler2.options.put("org.eclipse.jdt.core.compiler.source", "1.6");
/* 117 */     eclipseCompiler2.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
/*     */ 
/* 119 */     ArrayList allOptions = new ArrayList();
/* 120 */     if (options != null) {
/* 121 */       for (Iterator iterator = options.iterator(); iterator.hasNext(); ) {
/* 122 */         eclipseCompiler2.fileManager.handleOption((String)iterator.next(), iterator);
/*     */       }
/* 124 */       for (String option : options) {
/* 125 */         allOptions.add(option);
/*     */       }
/*     */     }
/*     */ 
/* 129 */     if (compilationUnits != null) {
/* 130 */       for (JavaFileObject javaFileObject : compilationUnits)
/*     */       {
/* 135 */         URI uri = javaFileObject.toUri();
/* 136 */         if (!uri.isAbsolute()) {
/* 137 */           uri = URI.create("file://" + uri.toString());
/*     */         }
/* 139 */         allOptions.add(new File(uri).getAbsolutePath());
/*     */       }
/*     */     }
/*     */ 
/* 143 */     if (classes != null) {
/* 144 */       allOptions.add("-classNames");
/* 145 */       StringBuilder builder = new StringBuilder();
/* 146 */       int i = 0;
/* 147 */       for (String className : classes) {
/* 148 */         if (i != 0) {
/* 149 */           builder.append(',');
/*     */         }
/* 151 */         builder.append(className);
/* 152 */         i++;
/*     */       }
/* 154 */       allOptions.add(String.valueOf(builder));
/*     */     }
/*     */ 
/* 157 */     String[] optionsToProcess = new String[allOptions.size()];
/* 158 */     allOptions.toArray(optionsToProcess);
/*     */     try {
/* 160 */       eclipseCompiler2.configure(optionsToProcess);
/*     */     } catch (IllegalArgumentException e) {
/* 162 */       throw e;
/*     */     }
/*     */ 
/* 165 */     if ((eclipseCompiler2.fileManager instanceof StandardJavaFileManager)) {
/* 166 */       StandardJavaFileManager javaFileManager = (StandardJavaFileManager)eclipseCompiler2.fileManager;
/*     */ 
/* 168 */       Iterable location = javaFileManager.getLocation(StandardLocation.CLASS_OUTPUT);
/* 169 */       if (location != null) {
/* 170 */         eclipseCompiler2.setDestinationPath(((File)location.iterator().next()).getAbsolutePath());
/*     */       }
/*     */     }
/*     */ 
/* 174 */     return new JavaCompiler.CompilationTask(eclipseCompiler2) {
/* 175 */       private boolean hasRun = false;
/*     */ 
/*     */       public Boolean call() {
/* 178 */         if (this.hasRun) {
/* 179 */           throw new IllegalStateException("This task has already been run");
/*     */         }
/* 181 */         Boolean value = this.val$eclipseCompiler2.call() ? Boolean.TRUE : Boolean.FALSE;
/* 182 */         this.hasRun = true;
/* 183 */         return value;
/*     */       }
/*     */       public void setLocale(Locale locale) {
/* 186 */         this.val$eclipseCompiler2.setLocale(locale);
/*     */       }
/*     */       public void setProcessors(Iterable<? extends Processor> processors) {
/* 189 */         ArrayList temp = new ArrayList();
/* 190 */         for (Processor processor : processors) {
/* 191 */           temp.add(processor);
/*     */         }
/* 193 */         Processor[] processors2 = new Processor[temp.size()];
/* 194 */         temp.toArray(processors2);
/* 195 */         this.val$eclipseCompiler2.processors = processors2;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public int isSupportedOption(String option)
/*     */   {
/* 205 */     return Options.processOptions(option);
/*     */   }
/*     */ 
/*     */   public int run(InputStream in, OutputStream out, OutputStream err, String[] arguments)
/*     */   {
/* 215 */     boolean succeed = new Main(new PrintWriter(new OutputStreamWriter(out)), new PrintWriter(new OutputStreamWriter(err)), true, null, null).compile(arguments);
/* 216 */     return succeed ? 0 : -1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.tool.EclipseCompiler
 * JD-Core Version:    0.6.0
 */