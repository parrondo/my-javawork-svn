/*     */ package org.eclipse.jdt.internal.compiler;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.util.Messages;
/*     */ 
/*     */ public class ProcessTaskManager
/*     */   implements Runnable
/*     */ {
/*     */   Compiler compiler;
/*     */   private int unitIndex;
/*     */   private Thread processingThread;
/*     */   CompilationUnitDeclaration unitToProcess;
/*     */   private Throwable caughtException;
/*     */   volatile int currentIndex;
/*     */   volatile int availableIndex;
/*     */   volatile int size;
/*     */   volatile int sleepCount;
/*     */   CompilationUnitDeclaration[] units;
/*     */   public static final int PROCESSED_QUEUE_SIZE = 12;
/*     */ 
/*     */   public ProcessTaskManager(Compiler compiler)
/*     */   {
/*  32 */     this.compiler = compiler;
/*  33 */     this.unitIndex = 0;
/*     */ 
/*  35 */     this.currentIndex = 0;
/*  36 */     this.availableIndex = 0;
/*  37 */     this.size = 12;
/*  38 */     this.sleepCount = 0;
/*  39 */     this.units = new CompilationUnitDeclaration[this.size];
/*     */ 
/*  41 */     synchronized (this) {
/*  42 */       this.processingThread = new Thread(this, "Compiler Processing Task");
/*  43 */       this.processingThread.setDaemon(true);
/*  44 */       this.processingThread.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void addNextUnit(CompilationUnitDeclaration newElement)
/*     */   {
/*  50 */     while (this.units[this.availableIndex] != null)
/*     */     {
/*  53 */       this.sleepCount = 1;
/*     */       try {
/*  55 */         wait(250L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException) {
/*     */       }
/*  59 */       this.sleepCount = 0;
/*     */     }
/*     */ 
/*  62 */     this.units[(this.availableIndex++)] = newElement;
/*  63 */     if (this.availableIndex >= this.size)
/*  64 */       this.availableIndex = 0;
/*  65 */     if (this.sleepCount <= -1)
/*  66 */       notify();
/*     */   }
/*     */ 
/*     */   public CompilationUnitDeclaration removeNextUnit() throws Error {
/*  70 */     CompilationUnitDeclaration next = null;
/*  71 */     boolean yield = false;
/*  72 */     synchronized (this) {
/*  73 */       next = this.units[this.currentIndex];
/*  74 */       if ((next == null) || (this.caughtException != null)) {
/*     */         do {
/*  76 */           if (this.processingThread == null) {
/*  77 */             if (this.caughtException != null)
/*     */             {
/*  79 */               if ((this.caughtException instanceof Error))
/*  80 */                 throw ((Error)this.caughtException);
/*  81 */               throw ((RuntimeException)this.caughtException);
/*     */             }
/*  83 */             return null;
/*     */           }
/*     */ 
/*  87 */           this.sleepCount = -1;
/*     */           try {
/*  89 */             wait(100L);
/*     */           }
/*     */           catch (InterruptedException localInterruptedException) {
/*     */           }
/*  93 */           this.sleepCount = 0;
/*  94 */           next = this.units[this.currentIndex];
/*  95 */         }while (next == null);
/*     */       }
/*     */ 
/*  98 */       this.units[(this.currentIndex++)] = null;
/*  99 */       if (this.currentIndex >= this.size)
/* 100 */         this.currentIndex = 0;
/* 101 */       if ((this.sleepCount >= 1) && (++this.sleepCount > 4)) {
/* 102 */         notify();
/* 103 */         yield = this.sleepCount > 8;
/*     */       }
/*     */     }
/* 106 */     if (yield)
/* 107 */       Thread.yield();
/* 108 */     return next;
/*     */   }
/*     */ 
/*     */   public void run() {
/* 112 */     while (this.processingThread != null) {
/* 113 */       this.unitToProcess = null;
/* 114 */       int index = -1;
/*     */       try {
/* 116 */         synchronized (this) {
/* 117 */           if (this.processingThread == null) return;
/*     */ 
/* 119 */           this.unitToProcess = this.compiler.getUnitToProcess(this.unitIndex);
/* 120 */           if (this.unitToProcess == null) {
/* 121 */             this.processingThread = null;
/* 122 */             return;
/*     */           }
/* 124 */           index = this.unitIndex++;
/*     */         }
/*     */         try
/*     */         {
/* 128 */           this.compiler.reportProgress(Messages.bind(Messages.compilation_processing, new String(this.unitToProcess.getFileName())));
/* 129 */           if (this.compiler.options.verbose) {
/* 130 */             this.compiler.out.println(
/* 131 */               Messages.bind(Messages.compilation_process, 
/* 132 */               new String[] { 
/* 133 */               String.valueOf(index + 1), 
/* 134 */               String.valueOf(this.compiler.totalUnits), 
/* 135 */               new String(this.unitToProcess.getFileName()) }));
/*     */           }
/* 137 */           this.compiler.process(this.unitToProcess, index);
/*     */         } finally {
/* 139 */           if (this.unitToProcess != null) {
/* 140 */             this.unitToProcess.cleanUp();
/*     */           }
/*     */         }
/* 143 */         addNextUnit(this.unitToProcess);
/*     */       } catch (Error e) {
/* 145 */         synchronized (this) {
/* 146 */           this.processingThread = null;
/* 147 */           this.caughtException = e;
/*     */         }
/* 149 */         return;
/*     */       } catch (RuntimeException e) {
/* 151 */         synchronized (this) {
/* 152 */           this.processingThread = null;
/* 153 */           this.caughtException = e;
/*     */         }
/* 155 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void shutdown() {
/*     */     try {
/* 162 */       Thread t = null;
/* 163 */       synchronized (this) {
/* 164 */         if (this.processingThread != null) {
/* 165 */           t = this.processingThread;
/* 166 */           this.processingThread = null;
/* 167 */           notifyAll();
/*     */         }
/*     */       }
/* 170 */       if (t != null)
/* 171 */         t.join(250L);
/*     */     }
/*     */     catch (InterruptedException localInterruptedException)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ProcessTaskManager
 * JD-Core Version:    0.6.0
 */