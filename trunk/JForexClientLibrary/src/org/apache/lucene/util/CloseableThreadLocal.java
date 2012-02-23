/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class CloseableThreadLocal<T>
/*     */   implements Closeable
/*     */ {
/*  57 */   private ThreadLocal<WeakReference<T>> t = new ThreadLocal();
/*     */ 
/*  59 */   private Map<Thread, T> hardRefs = new HashMap();
/*     */ 
/*     */   protected T initialValue() {
/*  62 */     return null;
/*     */   }
/*     */ 
/*     */   public T get() {
/*  66 */     WeakReference weakRef = (WeakReference)this.t.get();
/*  67 */     if (weakRef == null) {
/*  68 */       Object iv = initialValue();
/*  69 */       if (iv != null) {
/*  70 */         set(iv);
/*  71 */         return iv;
/*     */       }
/*  73 */       return null;
/*     */     }
/*  75 */     return weakRef.get();
/*     */   }
/*     */ 
/*     */   public void set(T object)
/*     */   {
/*  81 */     this.t.set(new WeakReference(object));
/*     */     Iterator it;
/*  83 */     synchronized (this.hardRefs) {
/*  84 */       this.hardRefs.put(Thread.currentThread(), object);
/*     */ 
/*  87 */       for (it = this.hardRefs.keySet().iterator(); it.hasNext(); ) {
/*  88 */         Thread t = (Thread)it.next();
/*  89 */         if (!t.isAlive())
/*  90 */           it.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*  99 */     this.hardRefs = null;
/*     */ 
/* 102 */     if (this.t != null) {
/* 103 */       this.t.remove();
/*     */     }
/* 105 */     this.t = null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.CloseableThreadLocal
 * JD-Core Version:    0.6.0
 */