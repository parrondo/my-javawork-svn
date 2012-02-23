/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ public final class IntsRef
/*     */   implements Comparable<IntsRef>
/*     */ {
/*     */   public int[] ints;
/*     */   public int offset;
/*     */   public int length;
/*     */ 
/*     */   public IntsRef()
/*     */   {
/*     */   }
/*     */ 
/*     */   public IntsRef(int capacity)
/*     */   {
/*  34 */     this.ints = new int[capacity];
/*     */   }
/*     */ 
/*     */   public IntsRef(int[] ints, int offset, int length) {
/*  38 */     this.ints = ints;
/*  39 */     this.offset = offset;
/*  40 */     this.length = length;
/*     */   }
/*     */ 
/*     */   public IntsRef(IntsRef other) {
/*  44 */     copy(other);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  49 */     return new IntsRef(this);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  54 */     int prime = 31;
/*  55 */     int result = 0;
/*  56 */     int end = this.offset + this.length;
/*  57 */     for (int i = this.offset; i < end; i++) {
/*  58 */       result = 31 * result + this.ints[i];
/*     */     }
/*  60 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/*  65 */     return intsEquals((IntsRef)other);
/*     */   }
/*     */ 
/*     */   public boolean intsEquals(IntsRef other) {
/*  69 */     if (this.length == other.length) {
/*  70 */       int otherUpto = other.offset;
/*  71 */       int[] otherInts = other.ints;
/*  72 */       int end = this.offset + this.length;
/*  73 */       for (int upto = this.offset; upto < end; otherUpto++) {
/*  74 */         if (this.ints[upto] != otherInts[otherUpto])
/*  75 */           return false;
/*  73 */         upto++;
/*     */       }
/*     */ 
/*  78 */       return true;
/*     */     }
/*  80 */     return false;
/*     */   }
/*     */ 
/*     */   public int compareTo(IntsRef other)
/*     */   {
/*  86 */     if (this == other) return 0;
/*     */ 
/*  88 */     int[] aInts = this.ints;
/*  89 */     int aUpto = this.offset;
/*  90 */     int[] bInts = other.ints;
/*  91 */     int bUpto = other.offset;
/*     */ 
/*  93 */     int aStop = aUpto + Math.min(this.length, other.length);
/*     */ 
/*  95 */     while (aUpto < aStop) {
/*  96 */       int aInt = aInts[(aUpto++)];
/*  97 */       int bInt = bInts[(bUpto++)];
/*  98 */       if (aInt > bInt)
/*  99 */         return 1;
/* 100 */       if (aInt < bInt) {
/* 101 */         return -1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 106 */     return this.length - other.length;
/*     */   }
/*     */ 
/*     */   public void copy(IntsRef other) {
/* 110 */     if (this.ints == null)
/* 111 */       this.ints = new int[other.length];
/*     */     else {
/* 113 */       this.ints = ArrayUtil.grow(this.ints, other.length);
/*     */     }
/* 115 */     System.arraycopy(other.ints, other.offset, this.ints, 0, other.length);
/* 116 */     this.length = other.length;
/* 117 */     this.offset = 0;
/*     */   }
/*     */ 
/*     */   public void grow(int newLength) {
/* 121 */     if (this.ints.length < newLength)
/* 122 */       this.ints = ArrayUtil.grow(this.ints, newLength);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 128 */     StringBuilder sb = new StringBuilder();
/* 129 */     sb.append('[');
/* 130 */     int end = this.offset + this.length;
/* 131 */     for (int i = this.offset; i < end; i++) {
/* 132 */       if (i > this.offset) {
/* 133 */         sb.append(' ');
/*     */       }
/* 135 */       sb.append(Integer.toHexString(this.ints[i]));
/*     */     }
/* 137 */     sb.append(']');
/* 138 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.IntsRef
 * JD-Core Version:    0.6.0
 */