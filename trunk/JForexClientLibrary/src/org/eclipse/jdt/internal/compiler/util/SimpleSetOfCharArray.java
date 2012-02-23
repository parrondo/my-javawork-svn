/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ 
/*     */ public final class SimpleSetOfCharArray
/*     */   implements Cloneable
/*     */ {
/*     */   public char[][] values;
/*     */   public int elementSize;
/*     */   public int threshold;
/*     */ 
/*     */   public SimpleSetOfCharArray()
/*     */   {
/*  28 */     this(13);
/*     */   }
/*     */ 
/*     */   public SimpleSetOfCharArray(int size) {
/*  32 */     if (size < 3) size = 3;
/*  33 */     this.elementSize = 0;
/*  34 */     this.threshold = (size + 1);
/*  35 */     this.values = new char[2 * size + 1][];
/*     */   }
/*     */ 
/*     */   public Object add(char[] object) {
/*  39 */     int length = this.values.length;
/*  40 */     int index = (CharOperation.hashCode(object) & 0x7FFFFFFF) % length;
/*     */     char[] current;
/*  42 */     while ((current = this.values[index]) != null)
/*     */     {
/*  43 */       char[] current;
/*  43 */       if (CharOperation.equals(current, object)) return this.values[index] =  = object;
/*  44 */       index++; if (index != length) continue; index = 0;
/*     */     }
/*  46 */     this.values[index] = object;
/*     */ 
/*  49 */     if (++this.elementSize > this.threshold) rehash();
/*  50 */     return object;
/*     */   }
/*     */ 
/*     */   public void asArray(Object[] copy) {
/*  54 */     if (this.elementSize != copy.length)
/*  55 */       throw new IllegalArgumentException();
/*  56 */     int index = this.elementSize;
/*  57 */     int i = 0; for (int l = this.values.length; (i < l) && (index > 0); i++)
/*  58 */       if (this.values[i] != null) {
/*  59 */         index--; copy[index] = this.values[i];
/*     */       }
/*     */   }
/*     */ 
/*     */   public void clear() {
/*  63 */     int i = this.values.length;
/*     */     do { this.values[i] = null;
/*     */ 
/*  63 */       i--; } while (i >= 0);
/*     */ 
/*  65 */     this.elementSize = 0;
/*     */   }
/*     */ 
/*     */   public Object clone() throws CloneNotSupportedException {
/*  69 */     SimpleSetOfCharArray result = (SimpleSetOfCharArray)super.clone();
/*  70 */     result.elementSize = this.elementSize;
/*  71 */     result.threshold = this.threshold;
/*     */ 
/*  73 */     int length = this.values.length;
/*  74 */     result.values = new char[length][];
/*  75 */     System.arraycopy(this.values, 0, result.values, 0, length);
/*  76 */     return result;
/*     */   }
/*     */ 
/*     */   public char[] get(char[] object) {
/*  80 */     int length = this.values.length;
/*  81 */     int index = (CharOperation.hashCode(object) & 0x7FFFFFFF) % length;
/*     */     char[] current;
/*  83 */     while ((current = this.values[index]) != null)
/*     */     {
/*  84 */       char[] current;
/*  84 */       if (CharOperation.equals(current, object)) return current;
/*  85 */       index++; if (index != length) continue; index = 0;
/*     */     }
/*  87 */     this.values[index] = object;
/*     */ 
/*  90 */     if (++this.elementSize > this.threshold) rehash();
/*  91 */     return object;
/*     */   }
/*     */ 
/*     */   public boolean includes(char[] object) {
/*  95 */     int length = this.values.length;
/*  96 */     int index = (CharOperation.hashCode(object) & 0x7FFFFFFF) % length;
/*     */     char[] current;
/*  98 */     while ((current = this.values[index]) != null)
/*     */     {
/*  99 */       char[] current;
/*  99 */       if (CharOperation.equals(current, object)) return true;
/* 100 */       index++; if (index != length) continue; index = 0;
/*     */     }
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   public char[] remove(char[] object) {
/* 106 */     int length = this.values.length;
/* 107 */     int index = (CharOperation.hashCode(object) & 0x7FFFFFFF) % length;
/*     */     char[] current;
/* 109 */     while ((current = this.values[index]) != null)
/*     */     {
/*     */       char[] current;
/* 110 */       if (CharOperation.equals(current, object)) {
/* 111 */         this.elementSize -= 1;
/* 112 */         char[] oldValue = this.values[index];
/* 113 */         this.values[index] = null;
/* 114 */         if (this.values[(index + 1)] != null)
/* 115 */           rehash();
/* 116 */         return oldValue;
/*     */       }
/* 118 */       index++; if (index != length) continue; index = 0;
/*     */     }
/* 120 */     return null;
/*     */   }
/*     */ 
/*     */   private void rehash() {
/* 124 */     SimpleSetOfCharArray newSet = new SimpleSetOfCharArray(this.elementSize * 2);
/*     */ 
/* 126 */     int i = this.values.length;
/*     */     do
/*     */     {
/*     */       char[] current;
/* 127 */       if ((current = this.values[i]) != null)
/* 128 */         newSet.add(current);
/* 126 */       i--; } while (i >= 0);
/*     */ 
/* 130 */     this.values = newSet.values;
/* 131 */     this.elementSize = newSet.elementSize;
/* 132 */     this.threshold = newSet.threshold;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 136 */     String s = "";
/*     */ 
/* 138 */     int i = 0; for (int l = this.values.length; i < l; i++)
/*     */     {
/*     */       char[] object;
/* 139 */       if ((object = this.values[i]) != null)
/* 140 */         s = s + new String(object) + "\n"; 
/*     */     }
/* 141 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.SimpleSetOfCharArray
 * JD-Core Version:    0.6.0
 */