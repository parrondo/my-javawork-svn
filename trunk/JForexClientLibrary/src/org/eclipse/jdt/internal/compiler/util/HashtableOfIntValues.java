/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ 
/*     */ public final class HashtableOfIntValues
/*     */   implements Cloneable
/*     */ {
/*     */   public static final int NO_VALUE = -2147483648;
/*     */   public char[][] keyTable;
/*     */   public int[] valueTable;
/*     */   public int elementSize;
/*     */   int threshold;
/*     */ 
/*     */   public HashtableOfIntValues()
/*     */   {
/*  30 */     this(13);
/*     */   }
/*     */ 
/*     */   public HashtableOfIntValues(int size)
/*     */   {
/*  35 */     this.elementSize = 0;
/*  36 */     this.threshold = size;
/*  37 */     int extraRoom = (int)(size * 1.75F);
/*  38 */     if (this.threshold == extraRoom)
/*  39 */       extraRoom++;
/*  40 */     this.keyTable = new char[extraRoom][];
/*  41 */     this.valueTable = new int[extraRoom];
/*     */   }
/*     */ 
/*     */   public Object clone() throws CloneNotSupportedException {
/*  45 */     HashtableOfIntValues result = (HashtableOfIntValues)super.clone();
/*  46 */     result.elementSize = this.elementSize;
/*  47 */     result.threshold = this.threshold;
/*     */ 
/*  49 */     int length = this.keyTable.length;
/*  50 */     result.keyTable = new char[length][];
/*  51 */     System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
/*     */ 
/*  53 */     length = this.valueTable.length;
/*  54 */     result.valueTable = new int[length];
/*  55 */     System.arraycopy(this.valueTable, 0, result.valueTable, 0, length);
/*  56 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(char[] key) {
/*  60 */     int length = this.keyTable.length;
/*  61 */     int index = CharOperation.hashCode(key) % length;
/*  62 */     int keyLength = key.length;
/*     */     char[] currentKey;
/*  64 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/*  65 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key)))
/*  66 */         return true;
/*  67 */       index++; if (index == length) {
/*  68 */         index = 0;
/*     */       }
/*     */     }
/*  71 */     return false;
/*     */   }
/*     */ 
/*     */   public int get(char[] key) {
/*  75 */     int length = this.keyTable.length;
/*  76 */     int index = CharOperation.hashCode(key) % length;
/*  77 */     int keyLength = key.length;
/*     */     char[] currentKey;
/*  79 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/*  80 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key)))
/*  81 */         return this.valueTable[index];
/*  82 */       index++; if (index == length) {
/*  83 */         index = 0;
/*     */       }
/*     */     }
/*  86 */     return -2147483648;
/*     */   }
/*     */ 
/*     */   public int put(char[] key, int value) {
/*  90 */     int length = this.keyTable.length;
/*  91 */     int index = CharOperation.hashCode(key) % length;
/*  92 */     int keyLength = key.length;
/*     */     char[] currentKey;
/*  94 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/*  95 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key)))
/*  96 */         return this.valueTable[index] = value;
/*  97 */       index++; if (index == length) {
/*  98 */         index = 0;
/*     */       }
/*     */     }
/* 101 */     this.keyTable[index] = key;
/* 102 */     this.valueTable[index] = value;
/*     */ 
/* 105 */     if (++this.elementSize > this.threshold)
/* 106 */       rehash();
/* 107 */     return value;
/*     */   }
/*     */ 
/*     */   public int removeKey(char[] key) {
/* 111 */     int length = this.keyTable.length;
/* 112 */     int index = CharOperation.hashCode(key) % length;
/* 113 */     int keyLength = key.length;
/*     */     char[] currentKey;
/* 115 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/* 116 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key))) {
/* 117 */         int value = this.valueTable[index];
/* 118 */         this.elementSize -= 1;
/* 119 */         this.keyTable[index] = null;
/* 120 */         this.valueTable[index] = -2147483648;
/* 121 */         rehash();
/* 122 */         return value;
/*     */       }
/* 124 */       index++; if (index == length) {
/* 125 */         index = 0;
/*     */       }
/*     */     }
/* 128 */     return -2147483648;
/*     */   }
/*     */ 
/*     */   private void rehash()
/*     */   {
/* 133 */     HashtableOfIntValues newHashtable = new HashtableOfIntValues(this.elementSize * 2);
/*     */ 
/* 135 */     int i = this.keyTable.length;
/*     */     do
/*     */     {
/*     */       char[] currentKey;
/* 136 */       if ((currentKey = this.keyTable[i]) != null)
/* 137 */         newHashtable.put(currentKey, this.valueTable[i]);
/* 135 */       i--; } while (i >= 0);
/*     */ 
/* 139 */     this.keyTable = newHashtable.keyTable;
/* 140 */     this.valueTable = newHashtable.valueTable;
/* 141 */     this.threshold = newHashtable.threshold;
/*     */   }
/*     */ 
/*     */   public int size() {
/* 145 */     return this.elementSize;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 149 */     String s = "";
/*     */ 
/* 151 */     int i = 0; for (int length = this.valueTable.length; i < length; i++)
/*     */     {
/*     */       char[] key;
/* 152 */       if ((key = this.keyTable[i]) != null)
/* 153 */         s = s + new String(key) + " -> " + this.valueTable[i] + "\n"; 
/*     */     }
/* 154 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.HashtableOfIntValues
 * JD-Core Version:    0.6.0
 */