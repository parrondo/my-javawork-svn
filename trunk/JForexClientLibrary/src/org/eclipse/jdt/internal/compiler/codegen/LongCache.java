/*     */ package org.eclipse.jdt.internal.compiler.codegen;
/*     */ 
/*     */ public class LongCache
/*     */ {
/*     */   public long[] keyTable;
/*     */   public int[] valueTable;
/*     */   int elementSize;
/*     */   int threshold;
/*     */ 
/*     */   public LongCache()
/*     */   {
/*  24 */     this(13);
/*     */   }
/*     */ 
/*     */   public LongCache(int initialCapacity)
/*     */   {
/*  33 */     this.elementSize = 0;
/*  34 */     this.threshold = (int)(initialCapacity * 0.66D);
/*  35 */     this.keyTable = new long[initialCapacity];
/*  36 */     this.valueTable = new int[initialCapacity];
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  42 */     int i = this.keyTable.length;
/*     */     do { this.keyTable[i] = 0L;
/*  44 */       this.valueTable[i] = 0;
/*     */ 
/*  42 */       i--; } while (i >= 0);
/*     */ 
/*  46 */     this.elementSize = 0;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(long key)
/*     */   {
/*  54 */     int index = hash(key); int length = this.keyTable.length;
/*  55 */     while ((this.keyTable[index] != 0L) || ((this.keyTable[index] == 0L) && (this.valueTable[index] != 0))) {
/*  56 */       if (this.keyTable[index] == key)
/*  57 */         return true;
/*  58 */       index++; if (index == length) {
/*  59 */         index = 0;
/*     */       }
/*     */     }
/*  62 */     return false;
/*     */   }
/*     */ 
/*     */   public int hash(long key)
/*     */   {
/*  70 */     return ((int)key & 0x7FFFFFFF) % this.keyTable.length;
/*     */   }
/*     */ 
/*     */   public int put(long key, int value)
/*     */   {
/*  81 */     int index = hash(key); int length = this.keyTable.length;
/*  82 */     while ((this.keyTable[index] != 0L) || ((this.keyTable[index] == 0L) && (this.valueTable[index] != 0))) {
/*  83 */       if (this.keyTable[index] == key)
/*  84 */         return this.valueTable[index] = value;
/*  85 */       index++; if (index == length) {
/*  86 */         index = 0;
/*     */       }
/*     */     }
/*  89 */     this.keyTable[index] = key;
/*  90 */     this.valueTable[index] = value;
/*     */ 
/*  93 */     if (++this.elementSize > this.threshold) {
/*  94 */       rehash();
/*     */     }
/*  96 */     return value;
/*     */   }
/*     */ 
/*     */   public int putIfAbsent(long key, int value)
/*     */   {
/* 107 */     int index = hash(key); int length = this.keyTable.length;
/* 108 */     while ((this.keyTable[index] != 0L) || ((this.keyTable[index] == 0L) && (this.valueTable[index] != 0))) {
/* 109 */       if (this.keyTable[index] == key)
/* 110 */         return this.valueTable[index];
/* 111 */       index++; if (index == length) {
/* 112 */         index = 0;
/*     */       }
/*     */     }
/* 115 */     this.keyTable[index] = key;
/* 116 */     this.valueTable[index] = value;
/*     */ 
/* 119 */     if (++this.elementSize > this.threshold) {
/* 120 */       rehash();
/*     */     }
/* 122 */     return -value;
/*     */   }
/*     */ 
/*     */   private void rehash()
/*     */   {
/* 130 */     LongCache newHashtable = new LongCache(this.keyTable.length * 2);
/* 131 */     int i = this.keyTable.length;
/*     */     do { long key = this.keyTable[i];
/* 133 */       int value = this.valueTable[i];
/* 134 */       if ((key != 0L) || ((key == 0L) && (value != 0)))
/* 135 */         newHashtable.put(key, value);
/* 131 */       i--; } while (i >= 0);
/*     */ 
/* 138 */     this.keyTable = newHashtable.keyTable;
/* 139 */     this.valueTable = newHashtable.valueTable;
/* 140 */     this.threshold = newHashtable.threshold;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 148 */     return this.elementSize;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 156 */     int max = size();
/* 157 */     StringBuffer buf = new StringBuffer();
/* 158 */     buf.append("{");
/* 159 */     for (int i = 0; i < max; i++) {
/* 160 */       if ((this.keyTable[i] != 0L) || ((this.keyTable[i] == 0L) && (this.valueTable[i] != 0))) {
/* 161 */         buf.append(this.keyTable[i]).append("->").append(this.valueTable[i]);
/*     */       }
/* 163 */       if (i < max) {
/* 164 */         buf.append(", ");
/*     */       }
/*     */     }
/* 167 */     buf.append("}");
/* 168 */     return buf.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.LongCache
 * JD-Core Version:    0.6.0
 */