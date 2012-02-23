/*     */ package org.eclipse.jdt.internal.compiler.codegen;
/*     */ 
/*     */ public class ObjectCache
/*     */ {
/*     */   public Object[] keyTable;
/*     */   public int[] valueTable;
/*     */   int elementSize;
/*     */   int threshold;
/*     */ 
/*     */   public ObjectCache()
/*     */   {
/*  23 */     this(13);
/*     */   }
/*     */ 
/*     */   public ObjectCache(int initialCapacity)
/*     */   {
/*  32 */     this.elementSize = 0;
/*  33 */     this.threshold = (int)(initialCapacity * 0.66F);
/*  34 */     this.keyTable = new Object[initialCapacity];
/*  35 */     this.valueTable = new int[initialCapacity];
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  41 */     int i = this.keyTable.length;
/*     */     do { this.keyTable[i] = null;
/*  43 */       this.valueTable[i] = 0;
/*     */ 
/*  41 */       i--; } while (i >= 0);
/*     */ 
/*  45 */     this.elementSize = 0;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key)
/*     */   {
/*  53 */     int index = hashCode(key); int length = this.keyTable.length;
/*  54 */     while (this.keyTable[index] != null) {
/*  55 */       if (this.keyTable[index] == key)
/*  56 */         return true;
/*  57 */       index++; if (index == length) {
/*  58 */         index = 0;
/*     */       }
/*     */     }
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   public int get(Object key)
/*     */   {
/*  70 */     int index = hashCode(key); int length = this.keyTable.length;
/*  71 */     while (this.keyTable[index] != null) {
/*  72 */       if (this.keyTable[index] == key)
/*  73 */         return this.valueTable[index];
/*  74 */       index++; if (index == length) {
/*  75 */         index = 0;
/*     */       }
/*     */     }
/*  78 */     return -1;
/*     */   }
/*     */ 
/*     */   public int hashCode(Object key)
/*     */   {
/*  87 */     return (key.hashCode() & 0x7FFFFFFF) % this.keyTable.length;
/*     */   }
/*     */ 
/*     */   public int put(Object key, int value)
/*     */   {
/*  99 */     int index = hashCode(key); int length = this.keyTable.length;
/* 100 */     while (this.keyTable[index] != null) {
/* 101 */       if (this.keyTable[index] == key)
/* 102 */         return this.valueTable[index] = value;
/* 103 */       index++; if (index == length) {
/* 104 */         index = 0;
/*     */       }
/*     */     }
/* 107 */     this.keyTable[index] = key;
/* 108 */     this.valueTable[index] = value;
/*     */ 
/* 111 */     if (++this.elementSize > this.threshold)
/* 112 */       rehash();
/* 113 */     return value;
/*     */   }
/*     */ 
/*     */   private void rehash()
/*     */   {
/* 121 */     ObjectCache newHashtable = new ObjectCache(this.keyTable.length * 2);
/* 122 */     int i = this.keyTable.length;
/*     */     do { if (this.keyTable[i] != null)
/* 124 */         newHashtable.put(this.keyTable[i], this.valueTable[i]);
/* 122 */       i--; } while (i >= 0);
/*     */ 
/* 126 */     this.keyTable = newHashtable.keyTable;
/* 127 */     this.valueTable = newHashtable.valueTable;
/* 128 */     this.threshold = newHashtable.threshold;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 136 */     return this.elementSize;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 144 */     int max = size();
/* 145 */     StringBuffer buf = new StringBuffer();
/* 146 */     buf.append("{");
/* 147 */     for (int i = 0; i < max; i++) {
/* 148 */       if (this.keyTable[i] != null) {
/* 149 */         buf.append(this.keyTable[i]).append("->").append(this.valueTable[i]);
/*     */       }
/* 151 */       if (i < max) {
/* 152 */         buf.append(", ");
/*     */       }
/*     */     }
/* 155 */     buf.append("}");
/* 156 */     return buf.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.ObjectCache
 * JD-Core Version:    0.6.0
 */