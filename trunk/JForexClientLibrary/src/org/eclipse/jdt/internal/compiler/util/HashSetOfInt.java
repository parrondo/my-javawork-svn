/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ public final class HashSetOfInt
/*     */   implements Cloneable
/*     */ {
/*     */   public int[] set;
/*     */   public int elementSize;
/*     */   int threshold;
/*     */ 
/*     */   public HashSetOfInt()
/*     */   {
/*  25 */     this(13);
/*     */   }
/*     */ 
/*     */   public HashSetOfInt(int size)
/*     */   {
/*  30 */     this.elementSize = 0;
/*  31 */     this.threshold = size;
/*  32 */     int extraRoom = (int)(size * 1.75F);
/*  33 */     if (this.threshold == extraRoom)
/*  34 */       extraRoom++;
/*  35 */     this.set = new int[extraRoom];
/*     */   }
/*     */ 
/*     */   public Object clone() throws CloneNotSupportedException {
/*  39 */     HashSetOfInt result = (HashSetOfInt)super.clone();
/*  40 */     result.elementSize = this.elementSize;
/*  41 */     result.threshold = this.threshold;
/*     */ 
/*  43 */     int length = this.set.length;
/*  44 */     result.set = new int[length];
/*  45 */     System.arraycopy(this.set, 0, result.set, 0, length);
/*     */ 
/*  47 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean contains(int element) {
/*  51 */     int length = this.set.length;
/*  52 */     int index = element % length;
/*     */     int currentElement;
/*  54 */     while ((currentElement = this.set[index]) != 0)
/*     */     {
/*     */       int currentElement;
/*  55 */       if (currentElement == element)
/*  56 */         return true;
/*  57 */       index++; if (index == length) {
/*  58 */         index = 0;
/*     */       }
/*     */     }
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   public int add(int element) {
/*  65 */     int length = this.set.length;
/*  66 */     int index = element % length;
/*     */     int currentElement;
/*  68 */     while ((currentElement = this.set[index]) != 0)
/*     */     {
/*     */       int currentElement;
/*  69 */       if (currentElement == element)
/*  70 */         return this.set[index] = element;
/*  71 */       index++; if (index == length) {
/*  72 */         index = 0;
/*     */       }
/*     */     }
/*  75 */     this.set[index] = element;
/*     */ 
/*  78 */     if (++this.elementSize > this.threshold)
/*  79 */       rehash();
/*  80 */     return element;
/*     */   }
/*     */ 
/*     */   public int remove(int element) {
/*  84 */     int length = this.set.length;
/*  85 */     int index = element % length;
/*     */     int currentElement;
/*  87 */     while ((currentElement = this.set[index]) != 0)
/*     */     {
/*     */       int currentElement;
/*  88 */       if (currentElement == element) {
/*  89 */         int existing = this.set[index];
/*  90 */         this.elementSize -= 1;
/*  91 */         this.set[index] = 0;
/*  92 */         rehash();
/*  93 */         return existing;
/*     */       }
/*  95 */       index++; if (index == length) {
/*  96 */         index = 0;
/*     */       }
/*     */     }
/*  99 */     return 0;
/*     */   }
/*     */ 
/*     */   private void rehash()
/*     */   {
/* 104 */     HashSetOfInt newHashSet = new HashSetOfInt(this.elementSize * 2);
/*     */ 
/* 106 */     int i = this.set.length;
/*     */     do
/*     */     {
/*     */       int currentElement;
/* 107 */       if ((currentElement = this.set[i]) != 0)
/* 108 */         newHashSet.add(currentElement);
/* 106 */       i--; } while (i >= 0);
/*     */ 
/* 110 */     this.set = newHashSet.set;
/* 111 */     this.threshold = newHashSet.threshold;
/*     */   }
/*     */ 
/*     */   public int size() {
/* 115 */     return this.elementSize;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 119 */     StringBuffer buffer = new StringBuffer();
/*     */ 
/* 121 */     int i = 0; for (int length = this.set.length; i < length; i++)
/*     */     {
/*     */       int element;
/* 122 */       if ((element = this.set[i]) != 0) {
/* 123 */         buffer.append(element);
/* 124 */         if (i != length - 1)
/* 125 */           buffer.append('\n'); 
/*     */       }
/*     */     }
/* 127 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.HashSetOfInt
 * JD-Core Version:    0.6.0
 */