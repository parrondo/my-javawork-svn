/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import javax.tools.JavaFileManager;
/*     */ import org.eclipse.jdt.internal.compiler.apt.util.EclipseFileManager;
/*     */ import org.eclipse.jdt.internal.compiler.batch.Main;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*     */ 
/*     */ public class BatchProcessingEnvImpl extends BaseProcessingEnvImpl
/*     */ {
/*     */   protected final BaseAnnotationProcessorManager _dispatchManager;
/*     */   protected final JavaFileManager _fileManager;
/*     */   protected final Main _compilerOwner;
/*     */ 
/*     */   public BatchProcessingEnvImpl(BaseAnnotationProcessorManager dispatchManager, Main batchCompiler, String[] commandLineArguments)
/*     */   {
/*  46 */     this._compilerOwner = batchCompiler;
/*  47 */     this._compiler = batchCompiler.batchCompiler;
/*  48 */     this._dispatchManager = dispatchManager;
/*  49 */     Class c = null;
/*     */     try {
/*  51 */       c = Class.forName("org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl");
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/*     */     }
/*  55 */     Field field = null;
/*  56 */     JavaFileManager javaFileManager = null;
/*  57 */     if (c != null)
/*     */       try {
/*  59 */         field = c.getField("fileManager");
/*     */       }
/*     */       catch (SecurityException localSecurityException)
/*     */       {
/*     */       }
/*     */       catch (IllegalArgumentException localIllegalArgumentException1) {
/*     */       }
/*     */       catch (NoSuchFieldException localNoSuchFieldException) {
/*     */       }
/*  68 */     if (field != null)
/*     */       try {
/*  70 */         javaFileManager = (JavaFileManager)field.get(batchCompiler);
/*     */       }
/*     */       catch (IllegalArgumentException localIllegalArgumentException2)
/*     */       {
/*     */       }
/*     */       catch (IllegalAccessException localIllegalAccessException) {
/*     */       }
/*  77 */     if (javaFileManager != null) {
/*  78 */       this._fileManager = javaFileManager;
/*     */     } else {
/*  80 */       String encoding = (String)batchCompiler.options.get("org.eclipse.jdt.core.encoding");
/*  81 */       Charset charset = encoding != null ? Charset.forName(encoding) : null;
/*  82 */       JavaFileManager manager = new EclipseFileManager(batchCompiler.compilerLocale, charset);
/*  83 */       ArrayList options = new ArrayList();
/*  84 */       for (String argument : commandLineArguments) {
/*  85 */         options.add(argument);
/*     */       }
/*  87 */       for (Iterator iterator = options.iterator(); iterator.hasNext(); ) {
/*  88 */         manager.handleOption((String)iterator.next(), iterator);
/*     */       }
/*  90 */       this._fileManager = manager;
/*     */     }
/*  92 */     this._processorOptions = Collections.unmodifiableMap(parseProcessorOptions(commandLineArguments));
/*  93 */     this._filer = new BatchFilerImpl(this._dispatchManager, this);
/*  94 */     this._messager = new BatchMessagerImpl(this, this._compilerOwner);
/*     */   }
/*     */ 
/*     */   private Map<String, String> parseProcessorOptions(String[] args)
/*     */   {
/* 110 */     Map options = new LinkedHashMap();
/* 111 */     for (String arg : args) {
/* 112 */       if (!arg.startsWith("-A")) {
/*     */         continue;
/*     */       }
/* 115 */       int equals = arg.indexOf('=');
/* 116 */       if (equals == 2)
/*     */       {
/* 118 */         Exception e = new IllegalArgumentException("-A option must have a key before the equals sign");
/* 119 */         throw new AbortCompilation(null, e);
/*     */       }
/* 121 */       if (equals == arg.length() - 1)
/*     */       {
/* 123 */         options.put(arg.substring(2, equals), null);
/* 124 */       } else if (equals == -1)
/*     */       {
/* 126 */         options.put(arg.substring(2), null);
/*     */       }
/*     */       else {
/* 129 */         options.put(arg.substring(2, equals), arg.substring(equals + 1));
/*     */       }
/*     */     }
/* 132 */     return options;
/*     */   }
/*     */ 
/*     */   public JavaFileManager getFileManager() {
/* 136 */     return this._fileManager;
/*     */   }
/*     */ 
/*     */   public Locale getLocale()
/*     */   {
/* 141 */     return this._compilerOwner.compilerLocale;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.BatchProcessingEnvImpl
 * JD-Core Version:    0.6.0
 */