/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ public final class SimpleSet
/*     */   implements Cloneable
/*     */ {
/*     */   public Object[] values;
/*     */   public int elementSize;
/*     */   public int threshold;
/*     */ 
/*     */   public SimpleSet()
/*     */   {
/*  26 */     this(13);
/*     */   }
/*     */ 
/*     */   public SimpleSet(int size) {
/*  30 */     if (size < 3) size = 3;
/*  31 */     this.elementSize = 0;
/*  32 */     this.threshold = (size + 1);
/*  33 */     this.values = new Object[2 * size + 1];
/*     */   }
/*     */ 
/*     */   public Object add(Object object) {
/*  37 */     int length = this.values.length;
/*  38 */     int index = (object.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object current;
/*  40 */     while ((current = this.values[index]) != null)
/*     */     {
/*  41 */       Object current;
/*  41 */       if (current.equals(object)) return this.values[index] =  = object;
/*  42 */       index++; if (index != length) continue; index = 0;
/*     */     }
/*  44 */     this.values[index] = object;
/*     */ 
/*  47 */     if (++this.elementSize > this.threshold) rehash();
/*  48 */     return object;
/*     */   }
/*     */ 
/*     */   public Object addIfNotIncluded(Object object) {
/*  52 */     int length = this.values.length;
/*  53 */     int index = (object.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object current;
/*  55 */     while ((current = this.values[index]) != null)
/*     */     {
/*  56 */       Object current;
/*  56 */       if (current.equals(object)) return null;
/*  57 */       index++; if (index != length) continue; index = 0;
/*     */     }
/*  59 */     this.values[index] = object;
/*     */ 
/*  62 */     if (++this.elementSize > this.threshold) rehash();
/*  63 */     return object;
/*     */   }
/*     */ 
/*     */   public void asArray(Object[] copy) {
/*  67 */     if (this.elementSize != copy.length)
/*  68 */       throw new IllegalArgumentException();
/*  69 */     int index = this.elementSize;
/*  70 */     int i = 0; for (int l = this.values.length; (i < l) && (index > 0); i++)
/*  71 */       if (this.values[i] != null) {
/*  72 */         index--; copy[index] = this.values[i];
/*     */       }
/*     */   }
/*     */ 
/*     */   public void clear() {
/*  76 */     int i = this.values.length;
/*     */     do { this.values[i] = null;
/*     */ 
/*  76 */       i--; } while (i >= 0);
/*     */ 
/*  78 */     this.elementSize = 0;
/*     */   }
/*     */ 
/*     */   public Object clone() throws CloneNotSupportedException {
/*  82 */     SimpleSet result = (SimpleSet)super.clone();
/*  83 */     result.elementSize = this.elementSize;
/*  84 */     result.threshold = this.threshold;
/*     */ 
/*  86 */     int length = this.values.length;
/*  87 */     result.values = new Object[length];
/*  88 */     System.arraycopy(this.values, 0, result.values, 0, length);
/*  89 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean includes(Object object) {
/*  93 */     int length = this.values.length;
/*  94 */     int index = (object.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object current;
/*  96 */     while ((current = this.values[index]) != null)
/*     */     {
/*  97 */       Object current;
/*  97 */       if (current.equals(object)) return true;
/*  98 */       index++; if (index != length) continue; index = 0;
/*     */     }
/* 100 */     return false;
/*     */   }
/*     */ 
/*     */   public Object remove(Object object) {
/* 104 */     int length = this.values.length;
/* 105 */     int index = (object.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object current;
/* 107 */     while ((current = this.values[index]) != null)
/*     */     {
/*     */       Object current;
/* 108 */       if (current.equals(object)) {
/* 109 */         this.elementSize -= 1;
/* 110 */         Object oldValue = this.values[index];
/* 111 */         this.values[index] = null;
/* 112 */         if (this.values[(index + 1)] != null)
/* 113 */           rehash();
/* 114 */         return oldValue;
/*     */       }
/* 116 */       index++; if (index != length) continue; index = 0;
/*     */     }
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */   private void rehash() {
/* 122 */     SimpleSet newSet = new SimpleSet(this.elementSize * 2);
/*     */ 
/* 124 */     int i = this.values.length;
/*     */     do
/*     */     {
/*     */       Object current;
/* 125 */       if ((current = this.values[i]) != null)
/* 126 */         newSet.add(current);
/* 124 */       i--; } while (i >= 0);
/*     */ 
/* 128 */     this.values = newSet.values;
/* 129 */     this.elementSize = newSet.elementSize;
/* 130 */     this.threshold = newSet.threshold;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 134 */     String s = "";
/*     */ 
/* 136 */     int i = 0; for (int l = this.values.length; i < l; i++)
/*     */     {
/*     */       Object object;
/* 137 */       if ((object = this.values[i]) != null)
/* 138 */         s = s + object.toString() + "\n"; 
/*     */     }
/* 139 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.SimpleSet
 * JD-Core Version:    0.6.0
 */