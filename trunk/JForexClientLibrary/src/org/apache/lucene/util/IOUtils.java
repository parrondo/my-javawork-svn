/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public final class IOUtils
/*     */ {
/*     */   private static final Method SUPPRESS_METHOD;
/*     */ 
/*     */   public static <E extends Exception> void closeWhileHandlingException(E priorException, Closeable[] objects)
/*     */     throws Exception, IOException
/*     */   {
/*  53 */     Throwable th = null;
/*     */ 
/*  55 */     for (Closeable object : objects) {
/*     */       try {
/*  57 */         if (object != null)
/*  58 */           object.close();
/*     */       }
/*     */       catch (Throwable t) {
/*  61 */         addSuppressed(priorException == null ? th : priorException, t);
/*  62 */         if (th == null) {
/*  63 */           th = t;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  68 */     if (priorException != null)
/*  69 */       throw priorException;
/*  70 */     if (th != null) {
/*  71 */       if ((th instanceof IOException)) throw ((IOException)th);
/*  72 */       if ((th instanceof RuntimeException)) throw ((RuntimeException)th);
/*  73 */       if ((th instanceof Error)) throw ((Error)th);
/*  74 */       throw new RuntimeException(th);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <E extends Exception> void closeWhileHandlingException(E priorException, Iterable<Closeable> objects) throws Exception, IOException
/*     */   {
/*  80 */     Throwable th = null;
/*     */ 
/*  82 */     for (Closeable object : objects) {
/*     */       try {
/*  84 */         if (object != null)
/*  85 */           object.close();
/*     */       }
/*     */       catch (Throwable t) {
/*  88 */         addSuppressed(priorException == null ? th : priorException, t);
/*  89 */         if (th == null) {
/*  90 */           th = t;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  95 */     if (priorException != null)
/*  96 */       throw priorException;
/*  97 */     if (th != null) {
/*  98 */       if ((th instanceof IOException)) throw ((IOException)th);
/*  99 */       if ((th instanceof RuntimeException)) throw ((RuntimeException)th);
/* 100 */       if ((th instanceof Error)) throw ((Error)th);
/* 101 */       throw new RuntimeException(th);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void close(Closeable[] objects)
/*     */     throws IOException
/*     */   {
/* 116 */     Throwable th = null;
/*     */ 
/* 118 */     for (Closeable object : objects) {
/*     */       try {
/* 120 */         if (object != null)
/* 121 */           object.close();
/*     */       }
/*     */       catch (Throwable t) {
/* 124 */         addSuppressed(th, t);
/* 125 */         if (th == null) {
/* 126 */           th = t;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 131 */     if (th != null) {
/* 132 */       if ((th instanceof IOException)) throw ((IOException)th);
/* 133 */       if ((th instanceof RuntimeException)) throw ((RuntimeException)th);
/* 134 */       if ((th instanceof Error)) throw ((Error)th);
/* 135 */       throw new RuntimeException(th);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void close(Iterable<? extends Closeable> objects)
/*     */     throws IOException
/*     */   {
/* 143 */     Throwable th = null;
/*     */ 
/* 145 */     for (Closeable object : objects) {
/*     */       try {
/* 147 */         if (object != null)
/* 148 */           object.close();
/*     */       }
/*     */       catch (Throwable t) {
/* 151 */         addSuppressed(th, t);
/* 152 */         if (th == null) {
/* 153 */           th = t;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 158 */     if (th != null) {
/* 159 */       if ((th instanceof IOException)) throw ((IOException)th);
/* 160 */       if ((th instanceof RuntimeException)) throw ((RuntimeException)th);
/* 161 */       if ((th instanceof Error)) throw ((Error)th);
/* 162 */       throw new RuntimeException(th);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void closeWhileHandlingException(Closeable[] objects)
/*     */     throws IOException
/*     */   {
/* 174 */     for (Closeable object : objects)
/*     */       try {
/* 176 */         if (object != null)
/* 177 */           object.close();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void closeWhileHandlingException(Iterable<? extends Closeable> objects)
/*     */     throws IOException
/*     */   {
/* 188 */     for (Closeable object : objects)
/*     */       try {
/* 190 */         if (object != null)
/* 191 */           object.close();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   private static final void addSuppressed(Throwable exception, Throwable suppressed)
/*     */   {
/* 215 */     if ((SUPPRESS_METHOD != null) && (exception != null) && (suppressed != null))
/*     */       try {
/* 217 */         SUPPRESS_METHOD.invoke(exception, new Object[] { suppressed });
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     Method m;
/*     */     try
/*     */     {
/* 203 */       m = Throwable.class.getMethod("addSuppressed", new Class[] { Throwable.class });
/*     */     } catch (Exception e) {
/* 205 */       m = null;
/*     */     }
/* 207 */     SUPPRESS_METHOD = m;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.IOUtils
 * JD-Core Version:    0.6.0
 */