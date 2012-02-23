/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ public final class SimpleLookupTable
/*     */   implements Cloneable
/*     */ {
/*     */   public Object[] keyTable;
/*     */   public Object[] valueTable;
/*     */   public int elementSize;
/*     */   public int threshold;
/*     */ 
/*     */   public SimpleLookupTable()
/*     */   {
/*  27 */     this(13);
/*     */   }
/*     */ 
/*     */   public SimpleLookupTable(int size) {
/*  31 */     this.elementSize = 0;
/*  32 */     this.threshold = size;
/*  33 */     int extraRoom = (int)(size * 1.5F);
/*  34 */     if (this.threshold == extraRoom)
/*  35 */       extraRoom++;
/*  36 */     this.keyTable = new Object[extraRoom];
/*  37 */     this.valueTable = new Object[extraRoom];
/*     */   }
/*     */ 
/*     */   public Object clone() throws CloneNotSupportedException {
/*  41 */     SimpleLookupTable result = (SimpleLookupTable)super.clone();
/*  42 */     result.elementSize = this.elementSize;
/*  43 */     result.threshold = this.threshold;
/*     */ 
/*  45 */     int length = this.keyTable.length;
/*  46 */     result.keyTable = new Object[length];
/*  47 */     System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
/*     */ 
/*  49 */     length = this.valueTable.length;
/*  50 */     result.valueTable = new Object[length];
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
/*  60 */       Object currentKey;
/*  60 */       if (currentKey.equals(key)) return true;
/*  61 */       index++; if (index != length) continue; index = 0;
/*     */     }
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   public Object get(Object key) {
/*  67 */     int length = this.keyTable.length;
/*  68 */     int index = (key.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object currentKey;
/*  70 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*  71 */       Object currentKey;
/*  71 */       if (currentKey.equals(key)) return this.valueTable[index];
/*  72 */       index++; if (index != length) continue; index = 0;
/*     */     }
/*  74 */     return null;
/*     */   }
/*     */ 
/*     */   public Object getKey(Object key) {
/*  78 */     int length = this.keyTable.length;
/*  79 */     int index = (key.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object currentKey;
/*  81 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*  82 */       Object currentKey;
/*  82 */       if (currentKey.equals(key)) return currentKey;
/*  83 */       index++; if (index != length) continue; index = 0;
/*     */     }
/*  85 */     return key;
/*     */   }
/*     */ 
/*     */   public Object keyForValue(Object valueToMatch) {
/*  89 */     if (valueToMatch != null) {
/*  90 */       int i = 0; for (int l = this.keyTable.length; i < l; i++)
/*  91 */         if ((this.keyTable[i] != null) && (valueToMatch.equals(this.valueTable[i])))
/*  92 */           return this.keyTable[i]; 
/*     */     }
/*  93 */     return null;
/*     */   }
/*     */ 
/*     */   public Object put(Object key, Object value) {
/*  97 */     int length = this.keyTable.length;
/*  98 */     int index = (key.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object currentKey;
/* 100 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/* 101 */       Object currentKey;
/* 101 */       if (currentKey.equals(key)) return this.valueTable[index] =  = value;
/* 102 */       index++; if (index != length) continue; index = 0;
/*     */     }
/* 104 */     this.keyTable[index] = key;
/* 105 */     this.valueTable[index] = value;
/*     */ 
/* 108 */     if (++this.elementSize > this.threshold) rehash();
/* 109 */     return value;
/*     */   }
/*     */ 
/*     */   public Object removeKey(Object key) {
/* 113 */     int length = this.keyTable.length;
/* 114 */     int index = (key.hashCode() & 0x7FFFFFFF) % length;
/*     */     Object currentKey;
/* 116 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       Object currentKey;
/* 117 */       if (currentKey.equals(key)) {
/* 118 */         this.elementSize -= 1;
/* 119 */         Object oldValue = this.valueTable[index];
/* 120 */         this.keyTable[index] = null;
/* 121 */         this.valueTable[index] = null;
/* 122 */         if (this.keyTable[(index + 1)] != null)
/* 123 */           rehash();
/* 124 */         return oldValue;
/*     */       }
/* 126 */       index++; if (index != length) continue; index = 0;
/*     */     }
/* 128 */     return null;
/*     */   }
/*     */ 
/*     */   public void removeValue(Object valueToRemove) {
/* 132 */     boolean rehash = false;
/* 133 */     int i = 0; for (int l = this.valueTable.length; i < l; i++) {
/* 134 */       Object value = this.valueTable[i];
/* 135 */       if ((value != null) && (value.equals(valueToRemove))) {
/* 136 */         this.elementSize -= 1;
/* 137 */         this.keyTable[i] = null;
/* 138 */         this.valueTable[i] = null;
/* 139 */         if (rehash) continue; if (this.keyTable[(i + 1)] != null)
/* 140 */           rehash = true;
/*     */       }
/*     */     }
/* 143 */     if (rehash) rehash(); 
/*     */   }
/*     */ 
/*     */   private void rehash()
/*     */   {
/* 147 */     SimpleLookupTable newLookupTable = new SimpleLookupTable(this.elementSize * 2);
/*     */ 
/* 149 */     int i = this.keyTable.length;
/*     */     do
/*     */     {
/*     */       Object currentKey;
/* 150 */       if ((currentKey = this.keyTable[i]) != null)
/* 151 */         newLookupTable.put(currentKey, this.valueTable[i]);
/* 149 */       i--; } while (i >= 0);
/*     */ 
/* 153 */     this.keyTable = newLookupTable.keyTable;
/* 154 */     this.valueTable = newLookupTable.valueTable;
/* 155 */     this.elementSize = newLookupTable.elementSize;
/* 156 */     this.threshold = newLookupTable.threshold;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 160 */     String s = "";
/*     */ 
/* 162 */     int i = 0; for (int l = this.valueTable.length; i < l; i++)
/*     */     {
/*     */       Object object;
/* 163 */       if ((object = this.valueTable[i]) != null)
/* 164 */         s = s + this.keyTable[i].toString() + " -> " + object.toString() + "\n"; 
/*     */     }
/* 165 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.SimpleLookupTable
 * JD-Core Version:    0.6.0
 */