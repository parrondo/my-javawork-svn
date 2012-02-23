/*     */ package org.eclipse.jdt.internal.compiler.codegen;
/*     */ 
/*     */ public class DoubleCache
/*     */ {
/*     */   private double[] keyTable;
/*     */   private int[] valueTable;
/*     */   private int elementSize;
/*     */ 
/*     */   public DoubleCache()
/*     */   {
/*  23 */     this(13);
/*     */   }
/*     */ 
/*     */   public DoubleCache(int initialCapacity)
/*     */   {
/*  32 */     this.elementSize = 0;
/*  33 */     this.keyTable = new double[initialCapacity];
/*  34 */     this.valueTable = new int[initialCapacity];
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  40 */     int i = this.keyTable.length;
/*     */     do { this.keyTable[i] = 0.0D;
/*  42 */       this.valueTable[i] = 0;
/*     */ 
/*  40 */       i--; } while (i >= 0);
/*     */ 
/*  44 */     this.elementSize = 0;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(double key)
/*     */   {
/*  52 */     if (key == 0.0D) {
/*  53 */       int i = 0; for (int max = this.elementSize; i < max; i++)
/*  54 */         if (this.keyTable[i] == 0.0D) {
/*  55 */           long value1 = Double.doubleToLongBits(key);
/*  56 */           long value2 = Double.doubleToLongBits(this.keyTable[i]);
/*  57 */           if ((value1 == -9223372036854775808L) && (value2 == -9223372036854775808L))
/*  58 */             return true;
/*  59 */           if ((value1 == 0L) && (value2 == 0L))
/*  60 */             return true;
/*     */         }
/*     */     }
/*     */     else {
/*  64 */       int i = 0; for (int max = this.elementSize; i < max; i++) {
/*  65 */         if (this.keyTable[i] == key) {
/*  66 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*  70 */     return false;
/*     */   }
/*     */ 
/*     */   public int put(double key, int value)
/*     */   {
/*  81 */     if (this.elementSize == this.keyTable.length)
/*     */     {
/*  83 */       System.arraycopy(this.keyTable, 0, this.keyTable = new double[this.elementSize * 2], 0, this.elementSize);
/*  84 */       System.arraycopy(this.valueTable, 0, this.valueTable = new int[this.elementSize * 2], 0, this.elementSize);
/*     */     }
/*  86 */     this.keyTable[this.elementSize] = key;
/*  87 */     this.valueTable[this.elementSize] = value;
/*  88 */     this.elementSize += 1;
/*  89 */     return value;
/*     */   }
/*     */ 
/*     */   public int putIfAbsent(double key, int value)
/*     */   {
/* 100 */     if (key == 0.0D) {
/* 101 */       int i = 0; for (int max = this.elementSize; i < max; i++)
/* 102 */         if (this.keyTable[i] == 0.0D) {
/* 103 */           long value1 = Double.doubleToLongBits(key);
/* 104 */           long value2 = Double.doubleToLongBits(this.keyTable[i]);
/* 105 */           if ((value1 == -9223372036854775808L) && (value2 == -9223372036854775808L))
/* 106 */             return this.valueTable[i];
/* 107 */           if ((value1 == 0L) && (value2 == 0L))
/* 108 */             return this.valueTable[i];
/*     */         }
/*     */     }
/*     */     else {
/* 112 */       int i = 0; for (int max = this.elementSize; i < max; i++) {
/* 113 */         if (this.keyTable[i] == key) {
/* 114 */           return this.valueTable[i];
/*     */         }
/*     */       }
/*     */     }
/* 118 */     if (this.elementSize == this.keyTable.length)
/*     */     {
/* 120 */       System.arraycopy(this.keyTable, 0, this.keyTable = new double[this.elementSize * 2], 0, this.elementSize);
/* 121 */       System.arraycopy(this.valueTable, 0, this.valueTable = new int[this.elementSize * 2], 0, this.elementSize);
/*     */     }
/* 123 */     this.keyTable[this.elementSize] = key;
/* 124 */     this.valueTable[this.elementSize] = value;
/* 125 */     this.elementSize += 1;
/* 126 */     return -value;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 134 */     int max = this.elementSize;
/* 135 */     StringBuffer buf = new StringBuffer();
/* 136 */     buf.append("{");
/* 137 */     for (int i = 0; i < max; i++) {
/* 138 */       if ((this.keyTable[i] != 0.0D) || ((this.keyTable[i] == 0.0D) && (this.valueTable[i] != 0))) {
/* 139 */         buf.append(this.keyTable[i]).append("->").append(this.valueTable[i]);
/*     */       }
/* 141 */       if (i < max) {
/* 142 */         buf.append(", ");
/*     */       }
/*     */     }
/* 145 */     buf.append("}");
/* 146 */     return buf.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.DoubleCache
 * JD-Core Version:    0.6.0
 */