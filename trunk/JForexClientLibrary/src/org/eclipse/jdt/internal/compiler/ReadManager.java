/*     */ package org.eclipse.jdt.internal.compiler;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*     */ 
/*     */ public class ReadManager
/*     */   implements Runnable
/*     */ {
/*     */   ICompilationUnit[] units;
/*     */   int nextFileToRead;
/*     */   ICompilationUnit[] filesRead;
/*     */   char[][] contentsRead;
/*     */   int readyToReadPosition;
/*     */   int nextAvailablePosition;
/*     */   Thread[] readingThreads;
/*  24 */   char[] readInProcessMarker = new char[0];
/*     */   int sleepingThreadCount;
/*     */   private Throwable caughtException;
/*     */   static final int START_CUSHION = 5;
/*     */   public static final int THRESHOLD = 10;
/*     */   static final int CACHE_SIZE = 15;
/*     */ 
/*     */   public ReadManager(ICompilationUnit[] files, int length)
/*     */   {
/*  34 */     int threadCount = 0;
/*     */     try {
/*  36 */       Class runtime = Class.forName("java.lang.Runtime");
/*  37 */       Method m = runtime.getDeclaredMethod("availableProcessors", new Class[0]);
/*  38 */       if (m != null) {
/*  39 */         Integer result = (Integer)m.invoke(Runtime.getRuntime(), null);
/*  40 */         threadCount = result.intValue() + 1;
/*  41 */         if (threadCount < 2)
/*  42 */           threadCount = 0;
/*  43 */         else if (threadCount > 15)
/*  44 */           threadCount = 15;
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/*  49 */     if (threadCount > 0)
/*  50 */       synchronized (this) {
/*  51 */         this.units = new ICompilationUnit[length];
/*  52 */         System.arraycopy(files, 0, this.units, 0, length);
/*  53 */         this.nextFileToRead = 5;
/*  54 */         this.filesRead = new ICompilationUnit[15];
/*  55 */         this.contentsRead = new char[15][];
/*  56 */         this.readyToReadPosition = 0;
/*  57 */         this.nextAvailablePosition = 0;
/*  58 */         this.sleepingThreadCount = 0;
/*  59 */         this.readingThreads = new Thread[threadCount];
/*  60 */         int i = threadCount;
/*     */         do { this.readingThreads[i] = new Thread(this, "Compiler Source File Reader");
/*  62 */           this.readingThreads[i].setDaemon(true);
/*  63 */           this.readingThreads[i].start();
/*     */ 
/*  60 */           i--; } while (i >= 0);
/*     */       }
/*     */   }
/*     */ 
/*     */   public char[] getContents(ICompilationUnit unit)
/*     */     throws Error
/*     */   {
/*  70 */     if ((this.readingThreads == null) || (this.units.length == 0)) {
/*  71 */       if (this.caughtException != null)
/*     */       {
/*  73 */         if ((this.caughtException instanceof Error))
/*  74 */           throw ((Error)this.caughtException);
/*  75 */         throw ((RuntimeException)this.caughtException);
/*     */       }
/*  77 */       return unit.getContents();
/*     */     }
/*     */ 
/*  80 */     boolean yield = false;
/*  81 */     char[] result = (char[])null;
/*  82 */     synchronized (this) {
/*  83 */       if (unit == this.filesRead[this.readyToReadPosition]) {
/*  84 */         result = this.contentsRead[this.readyToReadPosition];
/*  85 */         while ((result == this.readInProcessMarker) || (result == null))
/*     */         {
/*  88 */           this.contentsRead[this.readyToReadPosition] = null;
/*     */           try {
/*  90 */             wait(250L);
/*     */           } catch (InterruptedException localInterruptedException) {
/*     */           }
/*  93 */           if (this.caughtException != null)
/*     */           {
/*  95 */             if ((this.caughtException instanceof Error))
/*  96 */               throw ((Error)this.caughtException);
/*  97 */             throw ((RuntimeException)this.caughtException);
/*     */           }
/*  99 */           result = this.contentsRead[this.readyToReadPosition];
/*     */         }
/*     */ 
/* 102 */         this.filesRead[this.readyToReadPosition] = null;
/* 103 */         this.contentsRead[this.readyToReadPosition] = null;
/* 104 */         if (++this.readyToReadPosition >= this.contentsRead.length)
/* 105 */           this.readyToReadPosition = 0;
/* 106 */         if (this.sleepingThreadCount > 0)
/*     */         {
/* 109 */           notify();
/* 110 */           yield = this.sleepingThreadCount == this.readingThreads.length;
/*     */         }
/*     */       }
/*     */       else {
/* 114 */         int unitIndex = 0;
/* 115 */         for (int l = this.units.length; unitIndex < l; unitIndex++)
/* 116 */           if (this.units[unitIndex] == unit) break;
/* 117 */         if (unitIndex == this.units.length)
/*     */         {
/* 119 */           this.units = new ICompilationUnit[0];
/* 120 */         } else if (unitIndex >= this.nextFileToRead)
/*     */         {
/* 123 */           this.nextFileToRead = (unitIndex + 5);
/* 124 */           this.readyToReadPosition = 0;
/* 125 */           this.nextAvailablePosition = 0;
/* 126 */           this.filesRead = new ICompilationUnit[15];
/* 127 */           this.contentsRead = new char[15][];
/* 128 */           notifyAll();
/*     */         }
/*     */       }
/*     */     }
/* 132 */     if (yield)
/* 133 */       Thread.yield();
/* 134 */     if (result != null) {
/* 135 */       return result;
/*     */     }
/* 137 */     return unit.getContents();
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     try {
/*     */       do {
/* 143 */         ICompilationUnit unit = null;
/* 144 */         int position = -1;
/* 145 */         synchronized (this) {
/* 146 */           if (this.readingThreads == null) return;
/*     */           do
/*     */           {
/* 149 */             this.sleepingThreadCount += 1;
/*     */             try {
/* 151 */               wait(250L);
/*     */             } catch (InterruptedException localInterruptedException) {
/*     */             }
/* 154 */             this.sleepingThreadCount -= 1;
/* 155 */             if (this.readingThreads == null) return;
/*     */           }
/* 148 */           while (this.filesRead[this.nextAvailablePosition] != null);
/*     */ 
/* 158 */           if (this.nextFileToRead >= this.units.length) return;
/* 159 */           unit = this.units[(this.nextFileToRead++)];
/* 160 */           position = this.nextAvailablePosition;
/* 161 */           if (++this.nextAvailablePosition >= this.contentsRead.length)
/* 162 */             this.nextAvailablePosition = 0;
/* 163 */           this.filesRead[position] = unit;
/* 164 */           this.contentsRead[position] = this.readInProcessMarker;
/*     */         }
/* 166 */         char[] result = unit.getContents();
/* 167 */         synchronized (this) {
/* 168 */           if (this.filesRead[position] == unit) {
/* 169 */             if (this.contentsRead[position] == null)
/* 170 */               notifyAll();
/* 171 */             this.contentsRead[position] = result;
/*     */           }
/*     */         }
/* 142 */         if (this.readingThreads == null) break; 
/* 142 */       }while (this.nextFileToRead < this.units.length);
/*     */     }
/*     */     catch (Error e)
/*     */     {
/* 176 */       synchronized (this) {
/* 177 */         this.caughtException = e;
/* 178 */         shutdown();
/*     */       }
/* 180 */       return;
/*     */     } catch (RuntimeException e) {
/* 182 */       synchronized (this) {
/* 183 */         this.caughtException = e;
/* 184 */         shutdown();
/*     */       }
/* 186 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void shutdown() {
/* 191 */     this.readingThreads = null;
/* 192 */     notifyAll();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ReadManager
 * JD-Core Version:    0.6.0
 */