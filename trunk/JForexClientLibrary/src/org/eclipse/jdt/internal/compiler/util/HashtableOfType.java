/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ 
/*     */ public final class HashtableOfType
/*     */ {
/*     */   public char[][] keyTable;
/*     */   public ReferenceBinding[] valueTable;
/*     */   public int elementSize;
/*     */   int threshold;
/*     */ 
/*     */   public HashtableOfType()
/*     */   {
/*  24 */     this(3);
/*     */   }
/*     */   public HashtableOfType(int size) {
/*  27 */     this.elementSize = 0;
/*  28 */     this.threshold = size;
/*  29 */     int extraRoom = (int)(size * 1.75F);
/*  30 */     if (this.threshold == extraRoom)
/*  31 */       extraRoom++;
/*  32 */     this.keyTable = new char[extraRoom][];
/*  33 */     this.valueTable = new ReferenceBinding[extraRoom];
/*     */   }
/*     */   public boolean containsKey(char[] key) {
/*  36 */     int length = this.keyTable.length;
/*  37 */     int index = CharOperation.hashCode(key) % length;
/*  38 */     int keyLength = key.length;
/*     */     char[] currentKey;
/*  40 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/*  41 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key)))
/*  42 */         return true;
/*  43 */       index++; if (index == length) {
/*  44 */         index = 0;
/*     */       }
/*     */     }
/*  47 */     return false;
/*     */   }
/*     */   public ReferenceBinding get(char[] key) {
/*  50 */     int length = this.keyTable.length;
/*  51 */     int index = CharOperation.hashCode(key) % length;
/*  52 */     int keyLength = key.length;
/*     */     char[] currentKey;
/*  54 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/*  55 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key)))
/*  56 */         return this.valueTable[index];
/*  57 */       index++; if (index == length) {
/*  58 */         index = 0;
/*     */       }
/*     */     }
/*  61 */     return null;
/*     */   }
/*     */   public ReferenceBinding put(char[] key, ReferenceBinding value) {
/*  64 */     int length = this.keyTable.length;
/*  65 */     int index = CharOperation.hashCode(key) % length;
/*  66 */     int keyLength = key.length;
/*     */     char[] currentKey;
/*  68 */     while ((currentKey = this.keyTable[index]) != null)
/*     */     {
/*     */       char[] currentKey;
/*  69 */       if ((currentKey.length == keyLength) && (CharOperation.equals(currentKey, key)))
/*  70 */         return this.valueTable[index] =  = value;
/*  71 */       index++; if (index == length) {
/*  72 */         index = 0;
/*     */       }
/*     */     }
/*  75 */     this.keyTable[index] = key;
/*  76 */     this.valueTable[index] = value;
/*     */ 
/*  79 */     if (++this.elementSize > this.threshold)
/*  80 */       rehash();
/*  81 */     return value;
/*     */   }
/*     */   private void rehash() {
/*  84 */     HashtableOfType newHashtable = new HashtableOfType(this.elementSize < 100 ? 100 : this.elementSize * 2);
/*     */ 
/*  86 */     int i = this.keyTable.length;
/*     */     do
/*     */     {
/*     */       char[] currentKey;
/*  87 */       if ((currentKey = this.keyTable[i]) != null)
/*  88 */         newHashtable.put(currentKey, this.valueTable[i]);
/*  86 */       i--; } while (i >= 0);
/*     */ 
/*  90 */     this.keyTable = newHashtable.keyTable;
/*  91 */     this.valueTable = newHashtable.valueTable;
/*  92 */     this.threshold = newHashtable.threshold;
/*     */   }
/*     */   public int size() {
/*  95 */     return this.elementSize;
/*     */   }
/*     */   public String toString() {
/*  98 */     String s = "";
/*     */ 
/* 100 */     int i = 0; for (int length = this.valueTable.length; i < length; i++)
/*     */     {
/*     */       ReferenceBinding type;
/* 101 */       if ((type = this.valueTable[i]) != null)
/* 102 */         s = s + type.toString() + "\n"; 
/*     */     }
/* 103 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.HashtableOfType
 * JD-Core Version:    0.6.0
 */