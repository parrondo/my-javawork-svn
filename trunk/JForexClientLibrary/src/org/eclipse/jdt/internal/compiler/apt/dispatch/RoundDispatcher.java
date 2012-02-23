/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.annotation.processing.Processor;
/*     */ import javax.annotation.processing.RoundEnvironment;
/*     */ import javax.lang.model.element.TypeElement;
/*     */ 
/*     */ public class RoundDispatcher
/*     */ {
/*     */   private final Set<TypeElement> _unclaimedAnnotations;
/*     */   private final RoundEnvironment _roundEnv;
/*     */   private final IProcessorProvider _provider;
/*  33 */   private boolean _searchForStar = false;
/*     */   private final PrintWriter _traceProcessorInfo;
/*     */   private final PrintWriter _traceRounds;
/*     */   private final List<ProcessorInfo> _processors;
/*     */ 
/*     */   public RoundDispatcher(IProcessorProvider provider, RoundEnvironment env, Set<TypeElement> rootAnnotations, PrintWriter traceProcessorInfo, PrintWriter traceRounds)
/*     */   {
/*  58 */     this._provider = provider;
/*  59 */     this._processors = provider.getDiscoveredProcessors();
/*  60 */     this._roundEnv = env;
/*  61 */     this._unclaimedAnnotations = new HashSet(rootAnnotations);
/*  62 */     this._traceProcessorInfo = traceProcessorInfo;
/*  63 */     this._traceRounds = traceRounds;
/*     */   }
/*     */ 
/*     */   public void round()
/*     */   {
/*     */     Iterator iElements;
/*  71 */     if (this._traceRounds != null) {
/*  72 */       StringBuilder sbElements = new StringBuilder();
/*  73 */       sbElements.append("\tinput files: {");
/*  74 */       iElements = this._roundEnv.getRootElements().iterator();
/*  75 */       boolean hasNext = iElements.hasNext();
/*  76 */       while (hasNext) {
/*  77 */         sbElements.append(iElements.next());
/*  78 */         hasNext = iElements.hasNext();
/*  79 */         if (hasNext) {
/*  80 */           sbElements.append(',');
/*     */         }
/*     */       }
/*  83 */       sbElements.append('}');
/*  84 */       this._traceRounds.println(sbElements.toString());
/*     */ 
/*  86 */       StringBuilder sbAnnots = new StringBuilder();
/*  87 */       sbAnnots.append("\tannotations: [");
/*  88 */       Iterator iAnnots = this._unclaimedAnnotations.iterator();
/*  89 */       hasNext = iAnnots.hasNext();
/*  90 */       while (hasNext) {
/*  91 */         sbAnnots.append(iAnnots.next());
/*  92 */         hasNext = iAnnots.hasNext();
/*  93 */         if (hasNext) {
/*  94 */           sbAnnots.append(',');
/*     */         }
/*     */       }
/*  97 */       sbAnnots.append(']');
/*  98 */       this._traceRounds.println(sbAnnots.toString());
/*     */ 
/* 100 */       this._traceRounds.println("\tlast round: " + this._roundEnv.processingOver());
/*     */     }
/*     */ 
/* 104 */     this._searchForStar = this._unclaimedAnnotations.isEmpty();
/*     */ 
/* 109 */     for (ProcessorInfo pi : this._processors) {
/* 110 */       handleProcessor(pi);
/*     */     }
/*     */ 
/* 115 */     while ((this._searchForStar) || (!this._unclaimedAnnotations.isEmpty())) {
/* 116 */       ProcessorInfo pi = this._provider.discoverNextProcessor();
/* 117 */       if (pi == null)
/*     */       {
/*     */         break;
/*     */       }
/* 121 */       handleProcessor(pi);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void handleProcessor(ProcessorInfo pi)
/*     */   {
/*     */     try
/*     */     {
/* 135 */       Set annotationsToProcess = new HashSet();
/* 136 */       boolean shouldCall = pi.computeSupportedAnnotations(
/* 137 */         this._unclaimedAnnotations, annotationsToProcess);
/* 138 */       if (shouldCall) {
/* 139 */         boolean claimed = pi._processor.process(annotationsToProcess, this._roundEnv);
/* 140 */         if ((this._traceProcessorInfo != null) && (!this._roundEnv.processingOver())) {
/* 141 */           StringBuilder sb = new StringBuilder();
/* 142 */           sb.append("Processor ");
/* 143 */           sb.append(pi._processor.getClass().getName());
/* 144 */           sb.append(" matches [");
/* 145 */           Iterator i = annotationsToProcess.iterator();
/* 146 */           boolean hasNext = i.hasNext();
/* 147 */           while (hasNext) {
/* 148 */             sb.append(i.next());
/* 149 */             hasNext = i.hasNext();
/* 150 */             if (hasNext) {
/* 151 */               sb.append(' ');
/*     */             }
/*     */           }
/* 154 */           sb.append("] and returns ");
/* 155 */           sb.append(claimed);
/* 156 */           this._traceProcessorInfo.println(sb.toString());
/*     */         }
/* 158 */         if (claimed)
/*     */         {
/* 160 */           this._unclaimedAnnotations.removeAll(annotationsToProcess);
/* 161 */           if (pi.supportsStar()) {
/* 162 */             this._searchForStar = false;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 169 */       this._provider.reportProcessorException(pi._processor, e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.RoundDispatcher
 * JD-Core Version:    0.6.0
 */