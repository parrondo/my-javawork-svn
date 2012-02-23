/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.util.CloseableThreadLocal;
/*     */ import org.apache.lucene.util.DoubleBarrelLRUCache;
/*     */ import org.apache.lucene.util.DoubleBarrelLRUCache.CloneableKey;
/*     */ 
/*     */ final class TermInfosReader
/*     */   implements Closeable
/*     */ {
/*     */   private final Directory directory;
/*     */   private final String segment;
/*     */   private final FieldInfos fieldInfos;
/*  36 */   private final CloseableThreadLocal<ThreadResources> threadResources = new CloseableThreadLocal();
/*     */   private final SegmentTermEnum origEnum;
/*     */   private final long size;
/*     */   private final Term[] indexTerms;
/*     */   private final TermInfo[] indexInfos;
/*     */   private final long[] indexPointers;
/*     */   private final int totalIndexInterval;
/*     */   private static final int DEFAULT_CACHE_SIZE = 1024;
/*  82 */   private final DoubleBarrelLRUCache<CloneableTerm, TermInfoAndOrd> termsCache = new DoubleBarrelLRUCache(1024);
/*     */ 
/*     */   TermInfosReader(Directory dir, String seg, FieldInfos fis, int readBufferSize, int indexDivisor)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/*  93 */     boolean success = false;
/*     */ 
/*  95 */     if ((indexDivisor < 1) && (indexDivisor != -1)) {
/*  96 */       throw new IllegalArgumentException("indexDivisor must be -1 (don't load terms index) or greater than 0: got " + indexDivisor);
/*     */     }
/*     */     try
/*     */     {
/* 100 */       this.directory = dir;
/* 101 */       this.segment = seg;
/* 102 */       this.fieldInfos = fis;
/*     */ 
/* 104 */       this.origEnum = new SegmentTermEnum(this.directory.openInput(IndexFileNames.segmentFileName(this.segment, "tis"), readBufferSize), this.fieldInfos, false);
/*     */ 
/* 106 */       this.size = this.origEnum.size;
/*     */ 
/* 109 */       if (indexDivisor != -1)
/*     */       {
/* 111 */         this.totalIndexInterval = (this.origEnum.indexInterval * indexDivisor);
/* 112 */         SegmentTermEnum indexEnum = new SegmentTermEnum(this.directory.openInput(IndexFileNames.segmentFileName(this.segment, "tii"), readBufferSize), this.fieldInfos, true);
/*     */         try
/*     */         {
/* 116 */           int indexSize = 1 + ((int)indexEnum.size - 1) / indexDivisor;
/*     */ 
/* 118 */           this.indexTerms = new Term[indexSize];
/* 119 */           this.indexInfos = new TermInfo[indexSize];
/* 120 */           this.indexPointers = new long[indexSize];
/*     */ 
/* 122 */           for (int i = 0; indexEnum.next(); i++) { this.indexTerms[i] = indexEnum.term();
/* 124 */             this.indexInfos[i] = indexEnum.termInfo();
/* 125 */             this.indexPointers[i] = indexEnum.indexPointer;
/*     */ 
/* 127 */             for (int j = 1; (j < indexDivisor) && 
/* 128 */               (indexEnum.next()); j++); } } finally {
/* 132 */           indexEnum.close();
/*     */         }
/*     */       }
/*     */       else {
/* 136 */         this.totalIndexInterval = -1;
/* 137 */         this.indexTerms = null;
/* 138 */         this.indexInfos = null;
/* 139 */         this.indexPointers = null;
/*     */       }
/* 141 */       success = true;
/*     */     }
/*     */     finally
/*     */     {
/* 148 */       if (!success)
/* 149 */         close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSkipInterval()
/*     */   {
/* 155 */     return this.origEnum.skipInterval;
/*     */   }
/*     */ 
/*     */   public int getMaxSkipLevels() {
/* 159 */     return this.origEnum.maxSkipLevels;
/*     */   }
/*     */ 
/*     */   public final void close() throws IOException {
/* 163 */     if (this.origEnum != null)
/* 164 */       this.origEnum.close();
/* 165 */     this.threadResources.close();
/*     */   }
/*     */ 
/*     */   final long size()
/*     */   {
/* 170 */     return this.size;
/*     */   }
/*     */ 
/*     */   private ThreadResources getThreadResources() {
/* 174 */     ThreadResources resources = (ThreadResources)this.threadResources.get();
/* 175 */     if (resources == null) {
/* 176 */       resources = new ThreadResources(null);
/* 177 */       resources.termEnum = terms();
/* 178 */       this.threadResources.set(resources);
/*     */     }
/* 180 */     return resources;
/*     */   }
/*     */ 
/*     */   private final int getIndexOffset(Term term)
/*     */   {
/* 186 */     int lo = 0;
/* 187 */     int hi = this.indexTerms.length - 1;
/*     */ 
/* 189 */     while (hi >= lo) {
/* 190 */       int mid = lo + hi >>> 1;
/* 191 */       int delta = term.compareTo(this.indexTerms[mid]);
/* 192 */       if (delta < 0)
/* 193 */         hi = mid - 1;
/* 194 */       else if (delta > 0)
/* 195 */         lo = mid + 1;
/*     */       else
/* 197 */         return mid;
/*     */     }
/* 199 */     return hi;
/*     */   }
/*     */ 
/*     */   private final void seekEnum(SegmentTermEnum enumerator, int indexOffset) throws IOException {
/* 203 */     enumerator.seek(this.indexPointers[indexOffset], indexOffset * this.totalIndexInterval - 1L, this.indexTerms[indexOffset], this.indexInfos[indexOffset]);
/*     */   }
/*     */ 
/*     */   TermInfo get(Term term)
/*     */     throws IOException
/*     */   {
/* 210 */     return get(term, false);
/*     */   }
/*     */ 
/*     */   private TermInfo get(Term term, boolean mustSeekEnum) throws IOException
/*     */   {
/* 215 */     if (this.size == 0L) return null;
/*     */ 
/* 217 */     ensureIndexIsRead();
/*     */ 
/* 219 */     CloneableTerm cacheKey = new CloneableTerm(term);
/*     */ 
/* 221 */     TermInfoAndOrd tiOrd = (TermInfoAndOrd)this.termsCache.get(cacheKey);
/* 222 */     ThreadResources resources = getThreadResources();
/*     */ 
/* 224 */     if ((!mustSeekEnum) && (tiOrd != null)) {
/* 225 */       return tiOrd;
/*     */     }
/*     */ 
/* 229 */     SegmentTermEnum enumerator = resources.termEnum;
/* 230 */     if ((enumerator.term() != null) && (((enumerator.prev() != null) && (term.compareTo(enumerator.prev()) > 0)) || (term.compareTo(enumerator.term()) >= 0)))
/*     */     {
/* 233 */       int enumOffset = (int)(enumerator.position / this.totalIndexInterval) + 1;
/* 234 */       if ((this.indexTerms.length == enumOffset) || (term.compareTo(this.indexTerms[enumOffset]) < 0))
/*     */       {
/* 240 */         int numScans = enumerator.scanTo(term);
/*     */         TermInfo ti;
/* 241 */         if ((enumerator.term() != null) && (term.compareTo(enumerator.term()) == 0)) {
/* 242 */           TermInfo ti = enumerator.termInfo();
/* 243 */           if (numScans > 1)
/*     */           {
/* 249 */             if (tiOrd == null) {
/* 250 */               this.termsCache.put(cacheKey, new TermInfoAndOrd(ti, enumerator.position));
/*     */             } else {
/* 252 */               assert (sameTermInfo(ti, tiOrd, enumerator));
/* 253 */               if ((!$assertionsDisabled) && ((int)enumerator.position != tiOrd.termOrd)) throw new AssertionError(); 
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/* 257 */           ti = null;
/*     */         }
/*     */ 
/* 260 */         return ti;
/*     */       }
/*     */     }
/*     */     int indexPos;
/*     */     int indexPos;
/* 266 */     if (tiOrd != null) {
/* 267 */       indexPos = (int)(tiOrd.termOrd / this.totalIndexInterval);
/*     */     }
/*     */     else {
/* 270 */       indexPos = getIndexOffset(term);
/*     */     }
/*     */ 
/* 273 */     seekEnum(enumerator, indexPos);
/* 274 */     enumerator.scanTo(term);
/*     */     TermInfo ti;
/* 276 */     if ((enumerator.term() != null) && (term.compareTo(enumerator.term()) == 0)) {
/* 277 */       TermInfo ti = enumerator.termInfo();
/* 278 */       if (tiOrd == null)
/*     */       {
/* 283 */         if (enumerator.position >= 0L)
/* 284 */           this.termsCache.put(cacheKey, new TermInfoAndOrd(ti, enumerator.position));
/*     */       }
/*     */       else {
/* 287 */         assert (sameTermInfo(ti, tiOrd, enumerator));
/* 288 */         if ((!$assertionsDisabled) && (enumerator.position != tiOrd.termOrd)) throw new AssertionError(); 
/*     */       }
/*     */     }
/*     */     else {
/* 291 */       ti = null;
/*     */     }
/* 293 */     return ti;
/*     */   }
/*     */ 
/*     */   private final boolean sameTermInfo(TermInfo ti1, TermInfo ti2, SegmentTermEnum enumerator)
/*     */   {
/* 298 */     if (ti1.docFreq != ti2.docFreq) {
/* 299 */       return false;
/*     */     }
/* 301 */     if (ti1.freqPointer != ti2.freqPointer) {
/* 302 */       return false;
/*     */     }
/* 304 */     if (ti1.proxPointer != ti2.proxPointer) {
/* 305 */       return false;
/*     */     }
/*     */ 
/* 310 */     return (ti1.docFreq < enumerator.skipInterval) || (ti1.skipOffset == ti2.skipOffset);
/*     */   }
/*     */ 
/*     */   private void ensureIndexIsRead()
/*     */   {
/* 316 */     if (this.indexTerms == null)
/* 317 */       throw new IllegalStateException("terms index was not loaded when this reader was created");
/*     */   }
/*     */ 
/*     */   final long getPosition(Term term)
/*     */     throws IOException
/*     */   {
/* 323 */     if (this.size == 0L) return -1L;
/*     */ 
/* 325 */     ensureIndexIsRead();
/* 326 */     int indexOffset = getIndexOffset(term);
/*     */ 
/* 328 */     SegmentTermEnum enumerator = getThreadResources().termEnum;
/* 329 */     seekEnum(enumerator, indexOffset);
/*     */ 
/* 331 */     while ((term.compareTo(enumerator.term()) > 0) && (enumerator.next()));
/* 333 */     if (term.compareTo(enumerator.term()) == 0) {
/* 334 */       return enumerator.position;
/*     */     }
/* 336 */     return -1L;
/*     */   }
/*     */ 
/*     */   public SegmentTermEnum terms()
/*     */   {
/* 341 */     return (SegmentTermEnum)this.origEnum.clone();
/*     */   }
/*     */ 
/*     */   public SegmentTermEnum terms(Term term) throws IOException
/*     */   {
/* 346 */     get(term, true);
/* 347 */     return (SegmentTermEnum)getThreadResources().termEnum.clone();
/*     */   }
/*     */ 
/*     */   private static final class ThreadResources
/*     */   {
/*     */     SegmentTermEnum termEnum;
/*     */   }
/*     */ 
/*     */   private static class CloneableTerm extends DoubleBarrelLRUCache.CloneableKey
/*     */   {
/*     */     private final Term term;
/*     */ 
/*     */     public CloneableTerm(Term t)
/*     */     {
/*  62 */       this.term = new Term(t.field(), t.text());
/*     */     }
/*     */ 
/*     */     public Object clone()
/*     */     {
/*  67 */       return new CloneableTerm(this.term);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object _other)
/*     */     {
/*  72 */       CloneableTerm other = (CloneableTerm)_other;
/*  73 */       return this.term.equals(other.term);
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/*  78 */       return this.term.hashCode();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class TermInfoAndOrd extends TermInfo
/*     */   {
/*     */     final long termOrd;
/*     */ 
/*     */     public TermInfoAndOrd(TermInfo ti, long termOrd)
/*     */     {
/*  52 */       super();
/*  53 */       assert (termOrd >= 0L);
/*  54 */       this.termOrd = termOrd;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermInfosReader
 * JD-Core Version:    0.6.0
 */