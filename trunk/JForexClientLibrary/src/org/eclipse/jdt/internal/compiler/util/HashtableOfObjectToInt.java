/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ public final class HashtableOfObjectToInt
/*     */   implements Cloneable
/*     */ {
/*     */   public Object[] keyTable;
/*     */   public int[] valueTable;
/*     */   public int elementSize;
/*     */   int threshold;
/*     */ 
/*     */   public HashtableOfObjectToInt()
/*     */   {
/*  26 */     this(13);
/*     */   }
/*     */ 
/*     */   public HashtableOfObjectToInt(int size)
/*     */   {
/*  31 */     this.elementSize = 0;
/*  32 */     this.threshold = size;
/*  33 */     int extraRoom = (int)(size * 1.75F);
/*  34 */     if (this.threshold == extraRoom)
/*  35 */       extraRoom++;
/*  36 */     this.keyTable = new Object[extraRoom];
/*  37 */     this.valueTable = new int[extraRoom];
/*     */   }
/*     */ 
/*     */   public Object clone() throws CloneNotSupportedException {
/*  41 */     HashtableOfObjectToInt result = (HashtableOfObjectToInt)super.clone();
/*  42 */     result.elementSize = this.elementSize;
/*  43 */     result.threshold = this.threshold;
/*     */ 
/*  45 */     int length = this.keyTable.length;
/*  46 */     result.keyTable = new Object[length];
/*  47 */     System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
/*     */ 
/*  49 */     length = this.valueTable.length;
/*  50 */     result.valueTable = new int[length];
/*  51 */     System.arraycopy(this.valueTable, 0, result.valueTable, 0, length);
/*  52 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key) {
/*  56 */     int length = this.keyTable.length;
/*  57 */     int index = (key.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object currentKey;
/*  59 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       Object currentKey;
/*  60 */       if (currentKey.equals(key))
/*  61 */         return true;
/*  62 */       index++; if (index == length) {
/*  63 */         index = 0;
/*     */       }
/*     */     }
/*  66 */     return false;
/*     */   }
/*     */ 
/*     */   public int get(Object key) {
/*  70 */     int length = this.keyTable.length;
/*  71 */     int index = (key.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object currentKey;
/*  73 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       Object currentKey;
/*  74 */       if (currentKey.equals(key))
/*  75 */         return this.valueTable[index];
/*  76 */       index++; if (index == length) {
/*  77 */         index = 0;
/*     */       }
/*     */     }
/*  80 */     return -1;
/*     */   }
/*     */ 
/*     */   public void keysToArray(Object[] array) {
/*  84 */     int index = 0;
/*  85 */     int i = 0; for (int length = this.keyTable.length; i < length; i++)
/*  86 */       if (this.keyTable[i] != null)
/*  87 */         array[(index++)] = this.keyTable[i];
/*     */   }
/*     */ 
/*     */   public int put(Object key, int value)
/*     */   {
/*  92 */     int length = this.keyTable.length;
/*  93 */     int index = (key.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object currentKey;
/*  95 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       Object currentKey;
/*  96 */       if (currentKey.equals(key))
/*  97 */         return this.valueTable[index] = value;
/*  98 */       index++; if (index == length) {
/*  99 */         index = 0;
/*     */       }
/*     */     }
/* 102 */     this.keyTable[index] = key;
/* 103 */     this.valueTable[index] = value;
/*     */ 
/* 106 */     if (++this.elementSize > this.threshold)
/* 107 */       rehash();
/* 108 */     return value;
/*     */   }
/*     */ 
/*     */   public int removeKey(Object key) {
/* 112 */     int length = this.keyTable.length;
/* 113 */     int index = (key.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object currentKey;
/* 115 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       Object currentKey;
/* 116 */       if (currentKey.equals(key)) {
/* 117 */         int value = this.valueTable[index];
/* 118 */         this.elementSize -= 1;
/* 119 */         this.keyTable[index] = null;
/* 120 */         rehash();
/* 121 */         return value;
/*     */       }
/* 123 */       index++; if (index == length) {
/* 124 */         index = 0;
/*     */       }
/*     */     }
/* 127 */     return -1;
/*     */   }
/*     */ 
/*     */   private void rehash()
/*     */   {
/* 132 */     HashtableOfObjectToInt newHashtable = new HashtableOfObjectToInt(this.elementSize * 2);
/*     */ 
/* 134 */     int i = this.keyTable.length;
/*     */     do
/*     */     {
/*     */       Object currentKey;
/* 135 */       if ((currentKey = this.keyTable[i]) != null)
/* 136 */         newHashtable.put(currentKey, this.valueTable[i]);
/* 134 */       i--; } while (i >= 0);
/*     */ 
/* 138 */     this.keyTable = newHashtable.keyTable;
/* 139 */     this.valueTable = newHashtable.valueTable;
/* 140 */     this.threshold = newHashtable.threshold;
/*     */   }
/*     */ 
/*     */   public int size() {
/* 144 */     return this.elementSize;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 148 */     String s = "";
/*     */ 
/* 150 */     int i = 0; for (int length = this.keyTable.length; i < length; i++)
/*     */     {
/*     */       Object key;
/* 151 */       if ((key = this.keyTable[i]) != null)
/* 152 */         s = s + key + " -> " + this.valueTable[i] + "\n"; 
/*     */     }
/* 153 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.HashtableOfObjectToInt
 * JD-Core Version:    0.6.0
 */