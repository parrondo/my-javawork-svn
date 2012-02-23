/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ 
/*     */ public final class HashtableOfObject
/*     */   implements Cloneable
/*     */ {
/*     */   public char[][] keyTable;
/*     */   public Object[] valueTable;
/*     */   public int elementSize;
/*     */   int threshold;
/*     */ 
/*     */   public HashtableOfObject()
/*     */   {
/*  28 */     this(13);
/*     */   }
/*     */ 
/*     */   public HashtableOfObject(int size)
/*     */   {
/*  33 */     this.elementSize = 0;
/*  34 */     this.threshold = size;
/*  35 */     int extraRoom = (int)(size * 1.75F);
/*  36 */     if (this.threshold == extraRoom)
/*  37 */       extraRoom++;
/*  38 */     this.keyTable = new char[extraRoom][];
/*  39 */     this.valueTable = new Object[extraRoom];
/*     */   }
/*     */ 
/*     */   public void clear() {
/*  43 */     int i = this.keyTable.length;
/*     */     do { this.keyTable[i] = null;
/*  45 */       this.valueTable[i] = null;
/*     */ 
/*  43 */       i--; } while (i >= 0);
/*     */ 
/*  47 */     this.elementSize = 0;
/*     */   }
/*     */ 
/*     */   public Object clone() throws CloneNotSupportedException {
/*  51 */     HashtableOfObject result = (HashtableOfObject)super.clone();
/*  52 */     result.elementSize = this.elementSize;
/*  53 */     result.threshold = this.threshold;
/*     */ 
/*  55 */     int length = this.keyTable.length;
/*  56 */     result.keyTable = new char[length][];
/*  57 */     System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
/*     */ 
/*  59 */     length = this.valueTable.length;
/*  60 */     result.valueTable = new Object[length];
/*  61 */     System.arraycopy(this.valueTable, 0, result.valueTable, 0, length);
/*  62 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(char[] key) {
/*  66 */     int length = this.keyTable.length;
/*  67 */     int index = CharOperation.hashCode(key) % length;
/*  68 */     int keyLength = key.length;
/*     */     char[] currentKey;
/*  70 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/*  71 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key)))
/*  72 */         return true;
/*  73 */       index++; if (index == length) {
/*  74 */         index = 0;
/*     */       }
/*     */     }
/*  77 */     return false;
/*     */   }
/*     */ 
/*     */   public Object get(char[] key) {
/*  81 */     int length = this.keyTable.length;
/*  82 */     int index = CharOperation.hashCode(key) % length;
/*  83 */     int keyLength = key.length;
/*     */     char[] currentKey;
/*  85 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/*  86 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key)))
/*  87 */         return this.valueTable[index];
/*  88 */       index++; if (index == length) {
/*  89 */         index = 0;
/*     */       }
/*     */     }
/*  92 */     return null;
/*     */   }
/*     */ 
/*     */   public Object put(char[] key, Object value) {
/*  96 */     int length = this.keyTable.length;
/*  97 */     int index = CharOperation.hashCode(key) % length;
/*  98 */     int keyLength = key.length;
/*     */     char[] currentKey;
/* 100 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/* 101 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key)))
/* 102 */         return this.valueTable[index] =  = value;
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
/*     */   public Object removeKey(char[] key) {
/* 117 */     int length = this.keyTable.length;
/* 118 */     int index = CharOperation.hashCode(key) % length;
/* 119 */     int keyLength = key.length;
/*     */     char[] currentKey;
/* 121 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/* 122 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key))) {
/* 123 */         Object value = this.valueTable[index];
/* 124 */         this.elementSize -= 1;
/* 125 */         this.keyTable[index] = null;
/* 126 */         this.valueTable[index] = null;
/* 127 */         rehash();
/* 128 */         return value;
/*     */       }
/* 130 */       index++; if (index == length) {
/* 131 */         index = 0;
/*     */       }
/*     */     }
/* 134 */     return null;
/*     */   }
/*     */ 
/*     */   private void rehash()
/*     */   {
/* 139 */     HashtableOfObject newHashtable = new HashtableOfObject(this.elementSize * 2);
/*     */ 
/* 141 */     int i = this.keyTable.length;
/*     */     do
/*     */     {
/*     */       char[] currentKey;
/* 142 */       if ((currentKey = this.keyTable[i]) != null)
/* 143 */         newHashtable.put(currentKey, this.valueTable[i]);
/* 141 */       i--; } while (i >= 0);
/*     */ 
/* 145 */     this.keyTable = newHashtable.keyTable;
/* 146 */     this.valueTable = newHashtable.valueTable;
/* 147 */     this.threshold = newHashtable.threshold;
/*     */   }
/*     */ 
/*     */   public int size() {
/* 151 */     return this.elementSize;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 155 */     String s = "";
/*     */ 
/* 157 */     int i = 0; for (int length = this.valueTable.length; i < length; i++)
/*     */     {
/*     */       Object object;
/* 158 */       if ((object = this.valueTable[i]) != null)
/* 159 */         s = s + new String(this.keyTable[i]) + " -> " + object.toString() + "\n"; 
/*     */     }
/* 160 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.HashtableOfObject
 * JD-Core Version:    0.6.0
 */