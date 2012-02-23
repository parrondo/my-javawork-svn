/*     */ package com.dukascopy.charts.math.dataprovider.priceaggregation.buffer;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class ShiftableBuffer<D>
/*     */   implements IShiftableBuffer<D>
/*     */ {
/*     */   protected final int maxSize;
/*     */   protected final Object[] buffer;
/*     */   protected int lastIndex;
/*     */ 
/*     */   public ShiftableBuffer(int maxSize)
/*     */   {
/*  18 */     if (maxSize < 0) {
/*  19 */       throw new IllegalArgumentException("Negative buffer size");
/*     */     }
/*     */ 
/*  22 */     this.maxSize = maxSize;
/*  23 */     this.buffer = createArray(maxSize);
/*  24 */     this.lastIndex = -1;
/*     */   }
/*     */ 
/*     */   protected Object[] createArray(int size) {
/*  28 */     return new Object[size];
/*     */   }
/*     */ 
/*     */   protected Object[] createArray(D data) {
/*  32 */     return new Object[] { data };
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  37 */     synchronized (this) {
/*  38 */       this.lastIndex = -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaxSize()
/*     */   {
/*  44 */     synchronized (this) {
/*  45 */       return this.maxSize;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */   {
/*  51 */     synchronized (this) {
/*  52 */       return this.lastIndex + 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getLastIndex()
/*     */   {
/*  58 */     synchronized (this) {
/*  59 */       return this.lastIndex;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  65 */     synchronized (this) {
/*  66 */       return this.lastIndex < 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isFull()
/*     */   {
/*  72 */     synchronized (this) {
/*  73 */       return this.lastIndex + 1 >= this.maxSize;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addToEnd(D data)
/*     */   {
/*  79 */     synchronized (this) {
/*  80 */       if (!isFull()) {
/*  81 */         this.lastIndex += 1;
/*  82 */         this.buffer[this.lastIndex] = data;
/*     */       }
/*     */       else {
/*  85 */         TimeDataUtils.shiftBufferLeft(this.buffer, createArray(data));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addToHead(D data)
/*     */   {
/*  92 */     synchronized (this) {
/*  93 */       if (!isFull()) {
/*  94 */         this.lastIndex += 1;
/*     */       }
/*  96 */       TimeDataUtils.shiftBufferRight(this.buffer, createArray(data));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addToEnd(D[] data)
/*     */   {
/* 102 */     synchronized (this) {
/* 103 */       int index = 0;
/* 104 */       while ((!isFull()) && (data.length > index)) {
/* 105 */         addToEnd(data[index]);
/* 106 */         index++;
/*     */       }
/*     */ 
/* 109 */       if (index + 1 < data.length) {
/* 110 */         Object[] tale = Arrays.copyOfRange(data, index, data.length);
/* 111 */         TimeDataUtils.shiftBufferLeft(this.buffer, tale);
/*     */ 
/* 113 */         int newSize = Math.min(this.maxSize, tale.length + this.lastIndex + 1);
/* 114 */         this.lastIndex += newSize;
/*     */ 
/* 116 */         this.lastIndex = (this.lastIndex >= this.maxSize ? this.maxSize - 1 : this.lastIndex);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addToHead(D[] data)
/*     */   {
/* 126 */     synchronized (this) {
/* 127 */       int newSize = Math.max(this.maxSize, data.length + this.lastIndex + 1);
/* 128 */       TimeDataUtils.shiftBufferRight(this.buffer, data);
/* 129 */       this.lastIndex += newSize;
/* 130 */       this.lastIndex = (this.lastIndex >= this.maxSize ? this.maxSize - 1 : this.lastIndex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public D[] getAll(D[] array)
/*     */   {
/* 138 */     synchronized (this) {
/* 139 */       System.arraycopy(this.buffer, 0, array, 0, this.lastIndex + 1);
/* 140 */       return array;
/*     */     }
/*     */   }
/*     */ 
/*     */   public D getFirst()
/*     */   {
/* 146 */     synchronized (this) {
/* 147 */       if (this.lastIndex < 0) {
/* 148 */         return null;
/*     */       }
/* 150 */       return this.buffer[0];
/*     */     }
/*     */   }
/*     */ 
/*     */   public D getLast()
/*     */   {
/* 156 */     synchronized (this) {
/* 157 */       if (this.lastIndex < 0) {
/* 158 */         return null;
/*     */       }
/* 160 */       return this.buffer[this.lastIndex];
/*     */     }
/*     */   }
/*     */ 
/*     */   public void set(D bar, int index)
/*     */   {
/* 166 */     synchronized (this) {
/* 167 */       if ((0 <= index) && (index <= this.lastIndex)) {
/* 168 */         this.buffer[index] = bar;
/*     */       }
/*     */       else
/* 171 */         throw new ArrayIndexOutOfBoundsException(index);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setUp(D[] newBuffer)
/*     */   {
/* 178 */     synchronized (this) {
/* 179 */       if (newBuffer.length > this.maxSize) {
/* 180 */         throw new ArrayIndexOutOfBoundsException();
/*     */       }
/*     */ 
/* 183 */       int newLength = Math.min(this.maxSize, newBuffer.length);
/* 184 */       System.arraycopy(newBuffer, 0, this.buffer, 0, newLength);
/* 185 */       this.lastIndex = (newLength - 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public D get(int index)
/*     */   {
/* 191 */     synchronized (this) {
/* 192 */       if ((index >= 0) && (index <= this.lastIndex)) {
/* 193 */         return this.buffer[index];
/*     */       }
/*     */ 
/* 196 */       throw new ArrayIndexOutOfBoundsException(index);
/*     */     }
/*     */   }
/*     */ 
/*     */   public D[] copyOfRange(D[] toArray, int fromIndex)
/*     */   {
/* 203 */     synchronized (this) {
/* 204 */       System.arraycopy(this.buffer, fromIndex, toArray, 0, toArray.length);
/* 205 */       return toArray;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.buffer.ShiftableBuffer
 * JD-Core Version:    0.6.0
 */