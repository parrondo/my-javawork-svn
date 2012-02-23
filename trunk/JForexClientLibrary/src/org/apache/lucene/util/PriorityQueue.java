/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ public abstract class PriorityQueue<T>
/*     */ {
/*     */   private int size;
/*     */   private int maxSize;
/*     */   private T[] heap;
/*     */ 
/*     */   protected abstract boolean lessThan(T paramT1, T paramT2);
/*     */ 
/*     */   protected T getSentinelObject()
/*     */   {
/*  80 */     return null;
/*     */   }
/*     */ 
/*     */   protected final void initialize(int maxSize)
/*     */   {
/*  86 */     this.size = 0;
/*     */     int heapSize;
/*     */     int heapSize;
/*  88 */     if (0 == maxSize)
/*     */     {
/*  90 */       heapSize = 2;
/*     */     }
/*     */     else
/*     */     {
/*     */       int heapSize;
/*  92 */       if (maxSize == 2147483647)
/*     */       {
/* 101 */         heapSize = 2147483647;
/*     */       }
/*     */       else
/*     */       {
/* 105 */         heapSize = maxSize + 1;
/*     */       }
/*     */     }
/* 108 */     this.heap = ((Object[])new Object[heapSize]);
/* 109 */     this.maxSize = maxSize;
/*     */ 
/* 112 */     Object sentinel = getSentinelObject();
/* 113 */     if (sentinel != null) {
/* 114 */       this.heap[1] = sentinel;
/* 115 */       for (int i = 2; i < this.heap.length; i++) {
/* 116 */         this.heap[i] = getSentinelObject();
/*     */       }
/* 118 */       this.size = maxSize;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final T add(T element)
/*     */   {
/* 130 */     this.size += 1;
/* 131 */     this.heap[this.size] = element;
/* 132 */     upHeap();
/* 133 */     return this.heap[1];
/*     */   }
/*     */ 
/*     */   public T insertWithOverflow(T element)
/*     */   {
/* 147 */     if (this.size < this.maxSize) {
/* 148 */       add(element);
/* 149 */       return null;
/* 150 */     }if ((this.size > 0) && (!lessThan(element, this.heap[1]))) {
/* 151 */       Object ret = this.heap[1];
/* 152 */       this.heap[1] = element;
/* 153 */       updateTop();
/* 154 */       return ret;
/*     */     }
/* 156 */     return element;
/*     */   }
/*     */ 
/*     */   public final T top()
/*     */   {
/* 165 */     return this.heap[1];
/*     */   }
/*     */ 
/*     */   public final T pop()
/*     */   {
/* 171 */     if (this.size > 0) {
/* 172 */       Object result = this.heap[1];
/* 173 */       this.heap[1] = this.heap[this.size];
/* 174 */       this.heap[this.size] = null;
/* 175 */       this.size -= 1;
/* 176 */       downHeap();
/* 177 */       return result;
/*     */     }
/* 179 */     return null;
/*     */   }
/*     */ 
/*     */   public final T updateTop()
/*     */   {
/* 202 */     downHeap();
/* 203 */     return this.heap[1];
/*     */   }
/*     */ 
/*     */   public final int size()
/*     */   {
/* 208 */     return this.size;
/*     */   }
/*     */ 
/*     */   public final void clear()
/*     */   {
/* 213 */     for (int i = 0; i <= this.size; i++) {
/* 214 */       this.heap[i] = null;
/*     */     }
/* 216 */     this.size = 0;
/*     */   }
/*     */ 
/*     */   private final void upHeap() {
/* 220 */     int i = this.size;
/* 221 */     Object node = this.heap[i];
/* 222 */     int j = i >>> 1;
/* 223 */     while ((j > 0) && (lessThan(node, this.heap[j]))) {
/* 224 */       this.heap[i] = this.heap[j];
/* 225 */       i = j;
/* 226 */       j >>>= 1;
/*     */     }
/* 228 */     this.heap[i] = node;
/*     */   }
/*     */ 
/*     */   private final void downHeap() {
/* 232 */     int i = 1;
/* 233 */     Object node = this.heap[i];
/* 234 */     int j = i << 1;
/* 235 */     int k = j + 1;
/* 236 */     if ((k <= this.size) && (lessThan(this.heap[k], this.heap[j]))) {
/* 237 */       j = k;
/*     */     }
/* 239 */     while ((j <= this.size) && (lessThan(this.heap[j], node))) {
/* 240 */       this.heap[i] = this.heap[j];
/* 241 */       i = j;
/* 242 */       j = i << 1;
/* 243 */       k = j + 1;
/* 244 */       if ((k <= this.size) && (lessThan(this.heap[k], this.heap[j]))) {
/* 245 */         j = k;
/*     */       }
/*     */     }
/* 248 */     this.heap[i] = node;
/*     */   }
/*     */ 
/*     */   protected final Object[] getHeapArray()
/*     */   {
/* 255 */     return (Object[])this.heap;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.PriorityQueue
 * JD-Core Version:    0.6.0
 */