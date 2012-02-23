/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class StrategyDataStorageImpl
/*     */   implements IStrategyDataStorage
/*     */ {
/*  29 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategyDataStorageImpl.class);
/*     */   private File cacheTempFile;
/*     */   private DataOutputStream outputStream;
/*  35 */   private FixedSizeList lastReceivedData = new FixedSizeList(2000);
/*     */ 
/*     */   public StrategyDataStorageImpl()
/*     */   {
/*     */     try
/*     */     {
/*  42 */       this.cacheTempFile = File.createTempFile("jforex_tester", ".dat");
/*  43 */       this.cacheTempFile.deleteOnExit();
/*  44 */       FileOutputStream fos = new FileOutputStream(this.cacheTempFile);
/*  45 */       this.outputStream = new DataOutputStream(fos);
/*     */     } catch (IOException ex) {
/*  47 */       LOGGER.error("Error creating cache temporary file.", ex);
/*  48 */       this.cacheTempFile = null;
/*  49 */       this.outputStream = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void put(long time, double balance, double profLoss, double equity)
/*     */   {
/*  57 */     TesterIndicatorData data = new TesterIndicatorData(time, balance, profLoss, equity);
/*     */ 
/*  59 */     TesterIndicatorData lastReceived = this.lastReceivedData.getLast();
/*  60 */     if ((lastReceived != null) && (lastReceived.balance == balance) && (lastReceived.profitLoss == profLoss) && (lastReceived.equity == equity)) {
/*  61 */       return;
/*     */     }
/*     */ 
/*  66 */     if (this.outputStream != null) {
/*     */       try {
/*  68 */         this.outputStream.writeLong(time);
/*  69 */         this.outputStream.writeDouble(balance);
/*  70 */         this.outputStream.writeDouble(profLoss);
/*  71 */         this.outputStream.writeDouble(equity);
/*     */       }
/*     */       catch (IOException ex) {
/*  74 */         LOGGER.error("Error writing data to cache.", ex);
/*     */         try {
/*  76 */           this.outputStream.close();
/*     */         } catch (IOException e) {
/*  78 */           LOGGER.debug("Error closing output stream.", e);
/*     */         }
/*     */ 
/*  81 */         this.outputStream = null;
/*     */       }
/*     */     }
/*     */ 
/*  85 */     synchronized (this.lastReceivedData)
/*     */     {
/*  87 */       this.lastReceivedData.add(data);
/*     */     }
/*     */   }
/*     */ 
/*     */   public TesterIndicatorData getCachedData(long time)
/*     */   {
/*  98 */     synchronized (this.lastReceivedData) {
/*  99 */       TesterIndicatorData last = this.lastReceivedData.getLast();
/* 100 */       if (last == null)
/*     */       {
/* 102 */         return null;
/*     */       }
/* 104 */       if (last.time <= time) {
/* 105 */         return last;
/*     */       }
/*     */ 
/* 108 */       TesterIndicatorData first = this.lastReceivedData.getFirst();
/* 109 */       if (time < first.time)
/*     */       {
/* 111 */         if (!this.lastReceivedData.filled())
/*     */         {
/* 114 */           return first;
/*     */         }
/*     */ 
/* 118 */         return null;
/*     */       }
/*     */ 
/* 121 */       if (time == first.time) {
/* 122 */         return first;
/*     */       }
/*     */ 
/* 125 */       TesterIndicatorData key = new TesterIndicatorData(time, 0.0D, 0.0D, 0.0D);
/*     */ 
/* 127 */       int index = Collections.binarySearch(this.lastReceivedData, key);
/* 128 */       if (index < 0)
/*     */       {
/* 130 */         index = -index - 2;
/*     */       }
/* 132 */       else index -= 1;
/*     */ 
/* 134 */       return this.lastReceivedData.get(index);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */     try
/*     */     {
/* 146 */       if (this.outputStream != null)
/* 147 */         this.outputStream.close();
/*     */     }
/*     */     catch (IOException ex) {
/* 150 */       LOGGER.debug("Error closing output stream.", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int fillCache(TesterIndicatorData[] cache, int fromIndex, long filePosition, long maxTime, int maxCount)
/*     */     throws IOException
/*     */   {
/* 167 */     FileInputStream fis = new FileInputStream(this.cacheTempFile);
/* 168 */     BufferedInputStream bis = new BufferedInputStream(fis, maxCount * 32);
/* 169 */     DataInputStream dis = new DataInputStream(bis);
/*     */     try {
/* 171 */       dis.skip(filePosition * 32L);
/* 172 */       long readElements = 0L;
/*     */ 
/* 174 */       int index = 0;
/*     */       while (true) if (index < maxCount) {
/*     */           try {
/* 177 */             long cachedTime = dis.readLong();
/* 178 */             double cachedBalance = dis.readDouble();
/* 179 */             double cachedProfLoss = dis.readDouble();
/* 180 */             double cachedEquity = dis.readDouble();
/*     */ 
/* 182 */             readElements += 1L;
/*     */ 
/* 184 */             TesterIndicatorData data = new TesterIndicatorData(cachedTime, cachedBalance, cachedProfLoss, cachedEquity);
/* 185 */             cache[(fromIndex + index)] = data;
/* 186 */             index++;
/*     */ 
/* 188 */             if ((index == 1) && (cachedTime > maxTime))
/* 189 */               break label163;
/*     */           }
/*     */           catch (EOFException ex)
/*     */           {
/* 193 */             LOGGER.debug("Whole cache file is read.", ex);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */ 
/* 199 */       label163: ex = index;
/*     */       return ex;
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 203 */         dis.close();
/*     */       } catch (IOException ex) {
/* 205 */         LOGGER.debug("Error closing cache reading stream.", ex);
/*     */       }
/* 206 */     }throw localObject;
/*     */   }
/*     */ 
/*     */   public double getDataPeriod()
/*     */   {
/*     */     TesterIndicatorData first;
/*     */     TesterIndicatorData last;
/*     */     int size;
/* 219 */     synchronized (this.lastReceivedData) {
/* 220 */       first = this.lastReceivedData.getFirst();
/* 221 */       last = this.lastReceivedData.getLast();
/* 222 */       size = this.lastReceivedData.size();
/*     */     }
/* 224 */     return (last.time - first.time) / size;
/*     */   }
/*     */ 
/*     */   public static class TesterIndicatorData
/*     */     implements Comparable<TesterIndicatorData>
/*     */   {
/*     */     public static final int SIZE = 32;
/*     */     public final long time;
/*     */     public final double balance;
/*     */     public final double equity;
/*     */     public final double profitLoss;
/*     */ 
/*     */     public TesterIndicatorData(long time, double balance, double profitLoss, double equity)
/*     */     {
/* 475 */       this.time = time;
/* 476 */       this.balance = balance;
/* 477 */       this.profitLoss = profitLoss;
/* 478 */       this.equity = equity;
/*     */     }
/*     */ 
/*     */     public int compareTo(TesterIndicatorData o)
/*     */     {
/* 483 */       long diff = this.time - o.time;
/* 484 */       if (diff > 2147483647L) {
/* 485 */         return 2147483647;
/*     */       }
/* 487 */       if (diff < -2147483648L) {
/* 488 */         return -2147483648;
/*     */       }
/*     */ 
/* 491 */       return (int)diff;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 497 */       if (obj == this) {
/* 498 */         return true;
/*     */       }
/* 500 */       if ((obj instanceof TesterIndicatorData)) {
/* 501 */         TesterIndicatorData o = (TesterIndicatorData)obj;
/* 502 */         return (o.time == this.time) && (o.balance == this.balance) && (o.equity == this.equity) && (o.profitLoss == this.profitLoss);
/*     */       }
/*     */ 
/* 505 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 511 */       return (int)this.time;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class FixedSizeList
/*     */     implements List<StrategyDataStorageImpl.TesterIndicatorData>
/*     */   {
/*     */     private StrategyDataStorageImpl.TesterIndicatorData[] elements;
/*     */     private int size;
/*     */     private int firstElementOffset;
/*     */ 
/*     */     public FixedSizeList(int size)
/*     */     {
/* 239 */       this.elements = new StrategyDataStorageImpl.TesterIndicatorData[size];
/* 240 */       size = 0;
/* 241 */       this.firstElementOffset = 0;
/*     */     }
/*     */ 
/*     */     public boolean filled()
/*     */     {
/* 246 */       return this.size == this.elements.length;
/*     */     }
/*     */ 
/*     */     public boolean add(StrategyDataStorageImpl.TesterIndicatorData element)
/*     */     {
/* 251 */       add(this.size, element);
/* 252 */       return true;
/*     */     }
/*     */ 
/*     */     public void add(int index, StrategyDataStorageImpl.TesterIndicatorData element)
/*     */     {
/* 257 */       if ((index < 0) || (index > this.size)) {
/* 258 */         throw new IndexOutOfBoundsException(Integer.toString(index));
/*     */       }
/*     */ 
/* 261 */       int idx = getInnerIndex(index);
/* 262 */       this.elements[idx] = element;
/* 263 */       if (++this.size > this.elements.length) {
/* 264 */         this.size = this.elements.length;
/* 265 */         if (++this.firstElementOffset >= this.elements.length)
/* 266 */           this.firstElementOffset = 0;
/*     */       }
/*     */     }
/*     */ 
/*     */     private int getInnerIndex(int index)
/*     */     {
/* 272 */       int idx = this.firstElementOffset + index;
/* 273 */       if (idx >= this.elements.length) {
/* 274 */         idx -= this.elements.length;
/*     */       }
/* 276 */       return idx;
/*     */     }
/*     */ 
/*     */     public boolean addAll(Collection<? extends StrategyDataStorageImpl.TesterIndicatorData> c)
/*     */     {
/* 281 */       throw new UnsupportedOperationException("addAll");
/*     */     }
/*     */ 
/*     */     public boolean addAll(int index, Collection<? extends StrategyDataStorageImpl.TesterIndicatorData> c)
/*     */     {
/* 287 */       throw new UnsupportedOperationException("addAll");
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 293 */       this.size = 0;
/* 294 */       this.firstElementOffset = 0;
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o)
/*     */     {
/* 299 */       for (int i = 0; i < this.size; i++) {
/* 300 */         Object element = this.elements[getInnerIndex(i)];
/* 301 */         if (o == null) {
/* 302 */           if (element == null) {
/* 303 */             return true;
/*     */           }
/*     */         }
/* 306 */         else if (o.equals(element)) {
/* 307 */           return true;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 312 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean containsAll(Collection<?> c)
/*     */     {
/* 317 */       for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object object = i$.next();
/* 318 */         if (!contains(object)) {
/* 319 */           return false;
/*     */         }
/*     */       }
/* 322 */       return true;
/*     */     }
/*     */ 
/*     */     public StrategyDataStorageImpl.TesterIndicatorData get(int index)
/*     */     {
/* 327 */       if ((index < 0) || (index > this.size)) {
/* 328 */         throw new IndexOutOfBoundsException(Integer.toString(index));
/*     */       }
/*     */ 
/* 331 */       return this.elements[getInnerIndex(index)];
/*     */     }
/*     */ 
/*     */     public StrategyDataStorageImpl.TesterIndicatorData getLast()
/*     */     {
/* 336 */       if (this.size == 0) {
/* 337 */         return null;
/*     */       }
/* 339 */       return this.elements[getInnerIndex(this.size - 1)];
/*     */     }
/*     */ 
/*     */     public StrategyDataStorageImpl.TesterIndicatorData getFirst()
/*     */     {
/* 344 */       if (this.size == 0) {
/* 345 */         return null;
/*     */       }
/* 347 */       return this.elements[getInnerIndex(0)];
/*     */     }
/*     */ 
/*     */     public int indexOf(Object o)
/*     */     {
/* 354 */       throw new UnsupportedOperationException("indexOf");
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 360 */       return this.size != 0;
/*     */     }
/*     */ 
/*     */     public Iterator<StrategyDataStorageImpl.TesterIndicatorData> iterator()
/*     */     {
/* 366 */       throw new UnsupportedOperationException("iterator");
/*     */     }
/*     */ 
/*     */     public int lastIndexOf(Object o)
/*     */     {
/* 373 */       throw new UnsupportedOperationException("lastIndexOf");
/*     */     }
/*     */ 
/*     */     public ListIterator<StrategyDataStorageImpl.TesterIndicatorData> listIterator()
/*     */     {
/* 380 */       throw new UnsupportedOperationException("listIterator");
/*     */     }
/*     */ 
/*     */     public ListIterator<StrategyDataStorageImpl.TesterIndicatorData> listIterator(int index)
/*     */     {
/* 387 */       throw new UnsupportedOperationException("listIterator");
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o)
/*     */     {
/* 394 */       throw new UnsupportedOperationException("remove");
/*     */     }
/*     */ 
/*     */     public StrategyDataStorageImpl.TesterIndicatorData remove(int index)
/*     */     {
/* 401 */       throw new UnsupportedOperationException("remove");
/*     */     }
/*     */ 
/*     */     public boolean removeAll(Collection<?> c)
/*     */     {
/* 408 */       throw new UnsupportedOperationException("removeAll");
/*     */     }
/*     */ 
/*     */     public boolean retainAll(Collection<?> c)
/*     */     {
/* 415 */       throw new UnsupportedOperationException("retainAll");
/*     */     }
/*     */ 
/*     */     public StrategyDataStorageImpl.TesterIndicatorData set(int index, StrategyDataStorageImpl.TesterIndicatorData element)
/*     */     {
/* 422 */       throw new UnsupportedOperationException("set");
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 428 */       return this.size;
/*     */     }
/*     */ 
/*     */     public List<StrategyDataStorageImpl.TesterIndicatorData> subList(int fromIndex, int toIndex)
/*     */     {
/* 434 */       throw new UnsupportedOperationException("subList");
/*     */     }
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 441 */       throw new UnsupportedOperationException("toArray");
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] a)
/*     */     {
/* 448 */       throw new UnsupportedOperationException("toArray");
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.StrategyDataStorageImpl
 * JD-Core Version:    0.6.0
 */