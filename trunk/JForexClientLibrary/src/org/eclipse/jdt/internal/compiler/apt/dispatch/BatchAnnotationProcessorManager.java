/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ServiceConfigurationError;
/*     */ import java.util.ServiceLoader;
/*     */ import javax.annotation.processing.Processor;
/*     */ import javax.tools.JavaFileManager;
/*     */ import javax.tools.StandardLocation;
/*     */ import org.eclipse.jdt.internal.compiler.batch.Main;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*     */ 
/*     */ public class BatchAnnotationProcessorManager extends BaseAnnotationProcessorManager
/*     */ {
/*  36 */   private List<Processor> _setProcessors = null;
/*  37 */   private Iterator<Processor> _setProcessorIter = null;
/*     */   private List<String> _commandLineProcessors;
/*  43 */   private Iterator<String> _commandLineProcessorIter = null;
/*     */ 
/*  45 */   private ServiceLoader<Processor> _serviceLoader = null;
/*     */   private Iterator<Processor> _serviceLoaderIter;
/*     */   private ClassLoader _procLoader;
/*     */   private static final boolean VERBOSE_PROCESSOR_DISCOVERY = true;
/*  52 */   private boolean _printProcessorDiscovery = false;
/*     */ 
/*     */   public void configure(Object batchCompiler, String[] commandLineArguments)
/*     */   {
/*  65 */     if (this._processingEnv != null) {
/*  66 */       throw new IllegalStateException(
/*  67 */         "Calling configure() more than once on an AnnotationProcessorManager is not supported");
/*     */     }
/*  69 */     BatchProcessingEnvImpl processingEnv = new BatchProcessingEnvImpl(this, (Main)batchCompiler, commandLineArguments);
/*  70 */     this._processingEnv = processingEnv;
/*  71 */     this._procLoader = processingEnv.getFileManager().getClassLoader(StandardLocation.ANNOTATION_PROCESSOR_PATH);
/*  72 */     parseCommandLine(commandLineArguments);
/*  73 */     this._round = 0;
/*     */   }
/*     */ 
/*     */   private void parseCommandLine(String[] commandLineArguments)
/*     */   {
/*  83 */     List commandLineProcessors = null;
/*  84 */     for (int i = 0; i < commandLineArguments.length; i++) {
/*  85 */       String option = commandLineArguments[i];
/*  86 */       if ("-XprintProcessorInfo".equals(option)) {
/*  87 */         this._printProcessorInfo = true;
/*  88 */         this._printProcessorDiscovery = true;
/*     */       }
/*  90 */       else if ("-XprintRounds".equals(option)) {
/*  91 */         this._printRounds = true;
/*     */       }
/*  93 */       else if ("-processor".equals(option)) {
/*  94 */         commandLineProcessors = new ArrayList();
/*  95 */         i++; String procs = commandLineArguments[i];
/*  96 */         for (String proc : procs.split(",")) {
/*  97 */           commandLineProcessors.add(proc);
/*     */         }
/*  99 */         break;
/*     */       }
/*     */     }
/* 102 */     this._commandLineProcessors = commandLineProcessors;
/* 103 */     if (this._commandLineProcessors != null)
/* 104 */       this._commandLineProcessorIter = this._commandLineProcessors.iterator();
/*     */   }
/*     */ 
/*     */   public ProcessorInfo discoverNextProcessor()
/*     */   {
/* 110 */     if (this._setProcessors != null)
/*     */     {
/* 112 */       if (this._setProcessorIter.hasNext()) {
/* 113 */         Processor p = (Processor)this._setProcessorIter.next();
/* 114 */         p.init(this._processingEnv);
/* 115 */         ProcessorInfo pi = new ProcessorInfo(p);
/* 116 */         this._processors.add(pi);
/* 117 */         if ((this._printProcessorDiscovery) && (this._out != null)) {
/* 118 */           this._out.println("API specified processor: " + pi);
/*     */         }
/* 120 */         return pi;
/*     */       }
/* 122 */       return null;
/*     */     }
/*     */ 
/* 125 */     if (this._commandLineProcessors != null)
/*     */     {
/* 128 */       if (this._commandLineProcessorIter.hasNext()) {
/* 129 */         String proc = (String)this._commandLineProcessorIter.next();
/*     */         try {
/* 131 */           Class clazz = this._procLoader.loadClass(proc);
/* 132 */           Object o = clazz.newInstance();
/* 133 */           Processor p = (Processor)o;
/* 134 */           p.init(this._processingEnv);
/* 135 */           ProcessorInfo pi = new ProcessorInfo(p);
/* 136 */           this._processors.add(pi);
/* 137 */           if ((this._printProcessorDiscovery) && (this._out != null)) {
/* 138 */             this._out.println("Command line specified processor: " + pi);
/*     */           }
/* 140 */           return pi;
/*     */         }
/*     */         catch (Exception e) {
/* 143 */           throw new AbortCompilation(null, e);
/*     */         }
/*     */       }
/* 146 */       return null;
/*     */     }
/*     */ 
/* 151 */     if (this._serviceLoader == null) {
/* 152 */       this._serviceLoader = ServiceLoader.load(Processor.class, this._procLoader);
/* 153 */       this._serviceLoaderIter = this._serviceLoader.iterator();
/*     */     }
/*     */     try {
/* 156 */       if (this._serviceLoaderIter.hasNext()) {
/* 157 */         Processor p = (Processor)this._serviceLoaderIter.next();
/* 158 */         p.init(this._processingEnv);
/* 159 */         ProcessorInfo pi = new ProcessorInfo(p);
/* 160 */         this._processors.add(pi);
/* 161 */         if ((this._printProcessorDiscovery) && (this._out != null)) {
/* 162 */           StringBuilder sb = new StringBuilder();
/* 163 */           sb.append("Discovered processor service ");
/* 164 */           sb.append(pi);
/* 165 */           sb.append("\n  supporting ");
/* 166 */           sb.append(pi.getSupportedAnnotationTypesAsString());
/* 167 */           sb.append("\n  in ");
/* 168 */           sb.append(getProcessorLocation(p));
/* 169 */           this._out.println(sb.toString());
/*     */         }
/* 171 */         return pi;
/*     */       }
/*     */     }
/*     */     catch (ServiceConfigurationError e) {
/* 175 */       throw new AbortCompilation(null, e);
/*     */     }
/* 177 */     return null;
/*     */   }
/*     */ 
/*     */   private String getProcessorLocation(Processor p)
/*     */   {
/* 188 */     boolean isMember = false;
/* 189 */     Class outerClass = p.getClass();
/* 190 */     StringBuilder innerName = new StringBuilder();
/* 191 */     while (outerClass.isMemberClass()) {
/* 192 */       innerName.insert(0, outerClass.getSimpleName());
/* 193 */       innerName.insert(0, '$');
/* 194 */       isMember = true;
/* 195 */       outerClass = outerClass.getEnclosingClass();
/*     */     }
/* 197 */     String path = outerClass.getName();
/* 198 */     path = path.replace('.', '/');
/* 199 */     if (isMember) {
/* 200 */       path = path + innerName;
/*     */     }
/* 202 */     path = path + ".class";
/*     */ 
/* 205 */     String location = this._procLoader.getResource(path).toString();
/* 206 */     if (location.endsWith(path)) {
/* 207 */       location = location.substring(0, location.length() - path.length());
/*     */     }
/* 209 */     return location;
/*     */   }
/*     */ 
/*     */   public void reportProcessorException(Processor p, Exception e)
/*     */   {
/* 215 */     throw new AbortCompilation(null, e);
/*     */   }
/*     */ 
/*     */   public void setProcessors(Object[] processors)
/*     */   {
/* 220 */     if (!this._isFirstRound) {
/* 221 */       throw new IllegalStateException("setProcessors() cannot be called after processing has begun");
/*     */     }
/*     */ 
/* 225 */     this._setProcessors = new ArrayList(processors.length);
/* 226 */     for (Object o : processors) {
/* 227 */       Processor p = (Processor)o;
/* 228 */       this._setProcessors.add(p);
/*     */     }
/* 230 */     this._setProcessorIter = this._setProcessors.iterator();
/*     */ 
/* 233 */     this._commandLineProcessors = null;
/* 234 */     this._commandLineProcessorIter = null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.BatchAnnotationProcessorManager
 * JD-Core Version:    0.6.0
 */