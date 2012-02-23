/*     */ package org.eclipse.jdt.internal.compiler.codegen;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ 
/*     */ public class CharArrayCache
/*     */ {
/*     */   public char[][] keyTable;
/*     */   public int[] valueTable;
/*     */   int elementSize;
/*     */   int threshold;
/*     */ 
/*     */   public CharArrayCache()
/*     */   {
/*  26 */     this(9);
/*     */   }
/*     */ 
/*     */   public CharArrayCache(int initialCapacity)
/*     */   {
/*  35 */     this.elementSize = 0;
/*  36 */     this.threshold = (initialCapacity * 2 / 3);
/*  37 */     this.keyTable = new char[initialCapacity][];
/*  38 */     this.valueTable = new int[initialCapacity];
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  44 */     int i = this.keyTable.length;
/*     */     do { this.keyTable[i] = null;
/*  46 */       this.valueTable[i] = 0;
/*     */ 
/*  44 */       i--; } while (i >= 0);
/*     */ 
/*  48 */     this.elementSize = 0;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(char[] key)
/*     */   {
/*  56 */     int length = this.keyTable.length; int index = CharOperation.hashCode(key) % length;
/*  57 */     while (this.keyTable[index] != null) {
/*  58 */       if (CharOperation.equals(this.keyTable[index], key))
/*  59 */         return true;
/*  60 */       index++; if (index == length) {
/*  61 */         index = 0;
/*     */       }
/*     */     }
/*  64 */     return false;
/*     */   }
/*     */ 
/*     */   public int get(char[] key)
/*     */   {
/*  73 */     int length = this.keyTable.length; int index = CharOperation.hashCode(key) % length;
/*  74 */     while (this.keyTable[index] != null) {
/*  75 */       if (CharOperation.equals(this.keyTable[index], key))
/*  76 */         return this.valueTable[index];
/*  77 */       index++; if (index == length) {
/*  78 */         index = 0;
/*     */       }
/*     */     }
/*  81 */     return -1;
/*     */   }
/*     */ 
/*     */   public int putIfAbsent(char[] key, int value)
/*     */   {
/*  93 */     int length = this.keyTable.length; int index = CharOperation.hashCode(key) % length;
/*  94 */     while (this.keyTable[index] != null) {
/*  95 */       if (CharOperation.equals(this.keyTable[index], key))
/*  96 */         return this.valueTable[index];
/*  97 */       index++; if (index == length) {
/*  98 */         index = 0;
/*     */       }
/*     */     }
/* 101 */     this.keyTable[index] = key;
/* 102 */     this.valueTable[index] = value;
/*     */ 
/* 105 */     if (++this.elementSize > this.threshold)
/* 106 */       rehash();
/* 107 */     return -value;
/*     */   }
/*     */ 
/*     */   private int put(char[] key, int value)
/*     */   {
/* 120 */     int length = this.keyTable.length; int index = CharOperation.hashCode(key) % length;
/* 121 */     while (this.keyTable[index] != null) {
/* 122 */       if (CharOperation.equals(this.keyTable[index], key))
/* 123 */         return this.valueTable[index] = value;
/* 124 */       index++; if (index == length) {
/* 125 */         index = 0;
/*     */       }
/*     */     }
/* 128 */     this.keyTable[index] = key;
/* 129 */     this.valueTable[index] = value;
/*     */ 
/* 132 */     if (++this.elementSize > this.threshold)
/* 133 */       rehash();
/* 134 */     return value;
/*     */   }
/*     */ 
/*     */   private void rehash()
/*     */   {
/* 142 */     CharArrayCache newHashtable = new CharArrayCache(this.keyTable.length * 2);
/* 143 */     int i = this.keyTable.length;
/*     */     do { if (this.keyTable[i] != null)
/* 145 */         newHashtable.put(this.keyTable[i], this.valueTable[i]);
/* 143 */       i--; } while (i >= 0);
/*     */ 
/* 147 */     this.keyTable = newHashtable.keyTable;
/* 148 */     this.valueTable = newHashtable.valueTable;
/* 149 */     this.threshold = newHashtable.threshold;
/*     */   }
/*     */ 
/*     */   public void remove(char[] key)
/*     */   {
/* 156 */     int length = this.keyTable.length; int index = CharOperation.hashCode(key) % length;
/* 157 */     while (this.keyTable[index] != null) {
/* 158 */       if (CharOperation.equals(this.keyTable[index], key)) {
/* 159 */         this.valueTable[index] = 0;
/* 160 */         this.keyTable[index] = null;
/* 161 */         return;
/*     */       }
/* 163 */       index++; if (index == length)
/* 164 */         index = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public char[] returnKeyFor(int value)
/*     */   {
/* 175 */     for (int i = this.keyTable.length; i-- > 0; ) {
/* 176 */       if (this.valueTable[i] == value) {
/* 177 */         return this.keyTable[i];
/*     */       }
/*     */     }
/* 180 */     return null;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 188 */     return this.elementSize;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 196 */     int max = size();
/* 197 */     StringBuffer buf = new StringBuffer();
/* 198 */     buf.append("{");
/* 199 */     for (int i = 0; i < max; i++) {
/* 200 */       if (this.keyTable[i] != null) {
/* 201 */         buf.append(this.keyTable[i]).append("->").append(this.valueTable[i]);
/*     */       }
/* 203 */       if (i < max) {
/* 204 */         buf.append(", ");
/*     */       }
/*     */     }
/* 207 */     buf.append("}");
/* 208 */     return buf.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.CharArrayCache
 * JD-Core Version:    0.6.0
 */