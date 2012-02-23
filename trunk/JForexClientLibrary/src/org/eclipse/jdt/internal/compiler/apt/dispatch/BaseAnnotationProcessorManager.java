/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager;
/*     */ import org.eclipse.jdt.internal.compiler.Compiler;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ 
/*     */ public abstract class BaseAnnotationProcessorManager extends AbstractAnnotationProcessorManager
/*     */   implements IProcessorProvider
/*     */ {
/*     */   protected PrintWriter _out;
/*     */   protected PrintWriter _err;
/*     */   protected BaseProcessingEnvImpl _processingEnv;
/*  46 */   protected boolean _isFirstRound = true;
/*     */ 
/*  52 */   protected List<ProcessorInfo> _processors = new ArrayList();
/*     */ 
/*  55 */   protected boolean _printProcessorInfo = false;
/*  56 */   protected boolean _printRounds = false;
/*     */   protected int _round;
/*     */ 
/*     */   public void configure(Object batchCompiler, String[] options)
/*     */   {
/*  65 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void configureFromPlatform(Compiler compiler, Object compilationUnitLocator, Object javaProject)
/*     */   {
/*  74 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public List<ProcessorInfo> getDiscoveredProcessors()
/*     */   {
/*  79 */     return this._processors;
/*     */   }
/*     */ 
/*     */   public ICompilationUnit[] getDeletedUnits()
/*     */   {
/*  84 */     return this._processingEnv.getDeletedUnits();
/*     */   }
/*     */ 
/*     */   public ICompilationUnit[] getNewUnits()
/*     */   {
/*  89 */     return this._processingEnv.getNewUnits();
/*     */   }
/*     */ 
/*     */   public ReferenceBinding[] getNewClassFiles()
/*     */   {
/*  94 */     return this._processingEnv.getNewClassFiles();
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  99 */     this._processingEnv.reset();
/*     */   }
/*     */ 
/*     */   public void setErr(PrintWriter err)
/*     */   {
/* 107 */     this._err = err;
/*     */   }
/*     */ 
/*     */   public void setOut(PrintWriter out)
/*     */   {
/* 115 */     this._out = out;
/*     */   }
/*     */ 
/*     */   public void setProcessors(Object[] processors)
/*     */   {
/* 124 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void processAnnotations(CompilationUnitDeclaration[] units, ReferenceBinding[] referenceBindings, boolean isLastRound)
/*     */   {
/* 148 */     RoundEnvImpl roundEnv = new RoundEnvImpl(units, referenceBindings, isLastRound, this._processingEnv);
/* 149 */     if (this._isFirstRound) {
/* 150 */       this._isFirstRound = false;
/*     */     }
/* 152 */     PrintWriter traceProcessorInfo = this._printProcessorInfo ? this._out : null;
/* 153 */     PrintWriter traceRounds = this._printRounds ? this._out : null;
/* 154 */     if (traceRounds != null) {
/* 155 */       traceRounds.println("Round " + ++this._round + ':');
/*     */     }
/* 157 */     RoundDispatcher dispatcher = new RoundDispatcher(
/* 158 */       this, roundEnv, roundEnv.getRootAnnotations(), traceProcessorInfo, traceRounds);
/* 159 */     dispatcher.round();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.BaseAnnotationProcessorManager
 * JD-Core Version:    0.6.0
 */