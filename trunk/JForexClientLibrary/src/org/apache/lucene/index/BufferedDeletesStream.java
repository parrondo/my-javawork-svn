/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.apache.lucene.search.DocIdSet;
/*     */ import org.apache.lucene.search.DocIdSetIterator;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.search.QueryWrapperFilter;
/*     */ 
/*     */ class BufferedDeletesStream
/*     */ {
/*  54 */   private final List<FrozenBufferedDeletes> deletes = new ArrayList();
/*     */ 
/*  59 */   private long nextGen = 1L;
/*     */   private Term lastDeleteTerm;
/*     */   private PrintStream infoStream;
/*  65 */   private final AtomicLong bytesUsed = new AtomicLong();
/*  66 */   private final AtomicInteger numTerms = new AtomicInteger();
/*     */   private final int messageID;
/*     */   private static final Comparator<SegmentInfo> sortByDelGen;
/*     */ 
/*     */   public BufferedDeletesStream(int messageID)
/*     */   {
/*  70 */     this.messageID = messageID;
/*     */   }
/*     */ 
/*     */   private synchronized void message(String message) {
/*  74 */     if (this.infoStream != null)
/*  75 */       this.infoStream.println("BD " + this.messageID + " [" + new Date() + "; " + Thread.currentThread().getName() + "]: " + message);
/*     */   }
/*     */ 
/*     */   public synchronized void setInfoStream(PrintStream infoStream)
/*     */   {
/*  80 */     this.infoStream = infoStream;
/*     */   }
/*     */ 
/*     */   public synchronized void push(FrozenBufferedDeletes packet)
/*     */   {
/*  86 */     assert (packet.any());
/*  87 */     assert (checkDeleteStats());
/*  88 */     assert (packet.gen < this.nextGen);
/*  89 */     this.deletes.add(packet);
/*  90 */     this.numTerms.addAndGet(packet.numTermDeletes);
/*  91 */     this.bytesUsed.addAndGet(packet.bytesUsed);
/*  92 */     if (this.infoStream != null) {
/*  93 */       message("push deletes " + packet + " delGen=" + packet.gen + " packetCount=" + this.deletes.size());
/*     */     }
/*  95 */     assert (checkDeleteStats());
/*     */   }
/*     */ 
/*     */   public synchronized void clear() {
/*  99 */     this.deletes.clear();
/* 100 */     this.nextGen = 1L;
/* 101 */     this.numTerms.set(0);
/* 102 */     this.bytesUsed.set(0L);
/*     */   }
/*     */ 
/*     */   public boolean any() {
/* 106 */     return this.bytesUsed.get() != 0L;
/*     */   }
/*     */ 
/*     */   public int numTerms() {
/* 110 */     return this.numTerms.get();
/*     */   }
/*     */ 
/*     */   public long bytesUsed() {
/* 114 */     return this.bytesUsed.get();
/*     */   }
/*     */ 
/*     */   public synchronized ApplyDeletesResult applyDeletes(IndexWriter.ReaderPool readerPool, List<SegmentInfo> infos)
/*     */     throws IOException
/*     */   {
/* 153 */     long t0 = System.currentTimeMillis();
/*     */ 
/* 155 */     if (infos.size() == 0) {
/* 156 */       return new ApplyDeletesResult(false, this.nextGen++, null);
/*     */     }
/*     */ 
/* 159 */     assert (checkDeleteStats());
/*     */ 
/* 161 */     if (!any()) {
/* 162 */       message("applyDeletes: no deletes; skipping");
/* 163 */       return new ApplyDeletesResult(false, this.nextGen++, null);
/*     */     }
/*     */ 
/* 166 */     if (this.infoStream != null) {
/* 167 */       message("applyDeletes: infos=" + infos + " packetCount=" + this.deletes.size());
/*     */     }
/*     */ 
/* 170 */     List infos2 = new ArrayList();
/* 171 */     infos2.addAll(infos);
/* 172 */     Collections.sort(infos2, sortByDelGen);
/*     */ 
/* 174 */     CoalescedDeletes coalescedDeletes = null;
/* 175 */     boolean anyNewDeletes = false;
/*     */ 
/* 177 */     int infosIDX = infos2.size() - 1;
/* 178 */     int delIDX = this.deletes.size() - 1;
/*     */ 
/* 180 */     List allDeleted = null;
/*     */ 
/* 182 */     while (infosIDX >= 0)
/*     */     {
/* 185 */       FrozenBufferedDeletes packet = delIDX >= 0 ? (FrozenBufferedDeletes)this.deletes.get(delIDX) : null;
/* 186 */       SegmentInfo info = (SegmentInfo)infos2.get(infosIDX);
/* 187 */       long segGen = info.getBufferedDeletesGen();
/*     */ 
/* 189 */       if ((packet != null) && (segGen < packet.gen))
/*     */       {
/* 191 */         if (coalescedDeletes == null) {
/* 192 */           coalescedDeletes = new CoalescedDeletes();
/*     */         }
/* 194 */         coalescedDeletes.update(packet);
/* 195 */         delIDX--;
/* 196 */       } else if ((packet != null) && (segGen == packet.gen)) {
/* 200 */         assert (readerPool.infoIsLive(info));
/* 201 */         SegmentReader reader = readerPool.get(info, false);
/* 202 */         int delCount = 0;
/*     */         boolean segAllDeletes;
/*     */         try { if (coalescedDeletes != null)
/*     */           {
/* 207 */             delCount = (int)(delCount + applyTermDeletes(coalescedDeletes.termsIterable(), reader));
/* 208 */             delCount = (int)(delCount + applyQueryDeletes(coalescedDeletes.queriesIterable(), reader));
/*     */           }
/*     */ 
/* 213 */           delCount = (int)(delCount + applyQueryDeletes(packet.queriesIterable(), reader));
/* 214 */           segAllDeletes = reader.numDocs() == 0;
/*     */         } finally {
/* 216 */           readerPool.release(reader);
/*     */         }
/* 218 */         anyNewDeletes |= delCount > 0;
/*     */ 
/* 220 */         if (segAllDeletes) {
/* 221 */           if (allDeleted == null) {
/* 222 */             allDeleted = new ArrayList();
/*     */           }
/* 224 */           allDeleted.add(info);
/*     */         }
/*     */ 
/* 227 */         if (this.infoStream != null) {
/* 228 */           message("seg=" + info + " segGen=" + segGen + " segDeletes=[" + packet + "]; coalesced deletes=[" + (coalescedDeletes == null ? "null" : coalescedDeletes) + "] delCount=" + delCount + (segAllDeletes ? " 100% deleted" : ""));
/*     */         }
/*     */ 
/* 231 */         if (coalescedDeletes == null) {
/* 232 */           coalescedDeletes = new CoalescedDeletes();
/*     */         }
/* 234 */         coalescedDeletes.update(packet);
/* 235 */         delIDX--;
/* 236 */         infosIDX--;
/* 237 */         info.setBufferedDeletesGen(this.nextGen);
/*     */       }
/*     */       else
/*     */       {
/* 242 */         if (coalescedDeletes != null) { assert (readerPool.infoIsLive(info));
/* 245 */           SegmentReader reader = readerPool.get(info, false);
/* 246 */           int delCount = 0;
/*     */           boolean segAllDeletes;
/*     */           try { delCount = (int)(delCount + applyTermDeletes(coalescedDeletes.termsIterable(), reader));
/* 250 */             delCount = (int)(delCount + applyQueryDeletes(coalescedDeletes.queriesIterable(), reader));
/* 251 */             segAllDeletes = reader.numDocs() == 0;
/*     */           } finally {
/* 253 */             readerPool.release(reader);
/*     */           }
/* 255 */           anyNewDeletes |= delCount > 0;
/*     */ 
/* 257 */           if (segAllDeletes) {
/* 258 */             if (allDeleted == null) {
/* 259 */               allDeleted = new ArrayList();
/*     */             }
/* 261 */             allDeleted.add(info);
/*     */           }
/*     */ 
/* 264 */           if (this.infoStream != null) {
/* 265 */             message("seg=" + info + " segGen=" + segGen + " coalesced deletes=[" + (coalescedDeletes == null ? "null" : coalescedDeletes) + "] delCount=" + delCount + (segAllDeletes ? " 100% deleted" : ""));
/*     */           }
/*     */         }
/* 268 */         info.setBufferedDeletesGen(this.nextGen);
/*     */ 
/* 270 */         infosIDX--;
/*     */       }
/*     */     }
/*     */ 
/* 274 */     assert (checkDeleteStats());
/* 275 */     if (this.infoStream != null) {
/* 276 */       message("applyDeletes took " + (System.currentTimeMillis() - t0) + " msec");
/*     */     }
/*     */ 
/* 280 */     return new ApplyDeletesResult(anyNewDeletes, this.nextGen++, allDeleted);
/*     */   }
/*     */ 
/*     */   public synchronized long getNextGen() {
/* 284 */     return this.nextGen++;
/*     */   }
/*     */ 
/*     */   public synchronized void prune(SegmentInfos segmentInfos)
/*     */   {
/* 292 */     assert (checkDeleteStats());
/* 293 */     long minGen = 9223372036854775807L;
/* 294 */     for (SegmentInfo info : segmentInfos) {
/* 295 */       minGen = Math.min(info.getBufferedDeletesGen(), minGen);
/*     */     }
/*     */ 
/* 298 */     if (this.infoStream != null) {
/* 299 */       message("prune sis=" + segmentInfos + " minGen=" + minGen + " packetCount=" + this.deletes.size());
/*     */     }
/*     */ 
/* 302 */     int limit = this.deletes.size();
/* 303 */     for (int delIDX = 0; delIDX < limit; delIDX++) {
/* 304 */       if (((FrozenBufferedDeletes)this.deletes.get(delIDX)).gen >= minGen) {
/* 305 */         prune(delIDX);
/* 306 */         assert (checkDeleteStats());
/* 307 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 312 */     prune(limit);
/* 313 */     assert (!any());
/* 314 */     assert (checkDeleteStats());
/*     */   }
/*     */ 
/*     */   private synchronized void prune(int count) {
/* 318 */     if (count > 0) {
/* 319 */       if (this.infoStream != null) {
/* 320 */         message("pruneDeletes: prune " + count + " packets; " + (this.deletes.size() - count) + " packets remain");
/*     */       }
/* 322 */       for (int delIDX = 0; delIDX < count; delIDX++) {
/* 323 */         FrozenBufferedDeletes packet = (FrozenBufferedDeletes)this.deletes.get(delIDX);
/* 324 */         this.numTerms.addAndGet(-packet.numTermDeletes);
/* 325 */         assert (this.numTerms.get() >= 0);
/* 326 */         this.bytesUsed.addAndGet(-packet.bytesUsed);
/* 327 */         assert (this.bytesUsed.get() >= 0L);
/*     */       }
/* 329 */       this.deletes.subList(0, count).clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized long applyTermDeletes(Iterable<Term> termsIter, SegmentReader reader) throws IOException
/*     */   {
/* 335 */     long delCount = 0L;
/*     */ 
/* 337 */     assert (checkDeleteTerm(null));
/*     */ 
/* 339 */     TermDocs docs = reader.termDocs();
/*     */ 
/* 341 */     for (Term term : termsIter)
/*     */     {
/* 346 */       assert (checkDeleteTerm(term));
/* 347 */       docs.seek(term);
/*     */ 
/* 349 */       while (docs.next()) {
/* 350 */         int docID = docs.doc();
/* 351 */         reader.deleteDocument(docID);
/*     */ 
/* 357 */         delCount += 1L;
/*     */       }
/*     */     }
/*     */ 
/* 361 */     return delCount;
/*     */   }
/*     */ 
/*     */   private synchronized long applyQueryDeletes(Iterable<QueryAndLimit> queriesIter, SegmentReader reader)
/*     */     throws IOException
/*     */   {
/* 375 */     long delCount = 0L;
/*     */ 
/* 377 */     for (QueryAndLimit ent : queriesIter) {
/* 378 */       Query query = ent.query;
/* 379 */       int limit = ent.limit;
/* 380 */       DocIdSet docs = new QueryWrapperFilter(query).getDocIdSet(reader);
/* 381 */       if (docs != null) {
/* 382 */         DocIdSetIterator it = docs.iterator();
/* 383 */         if (it != null) {
/*     */           while (true) {
/* 385 */             int doc = it.nextDoc();
/* 386 */             if (doc >= limit) {
/*     */               break;
/*     */             }
/* 389 */             reader.deleteDocument(doc);
/*     */ 
/* 395 */             delCount += 1L;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 401 */     return delCount;
/*     */   }
/*     */ 
/*     */   private boolean checkDeleteTerm(Term term)
/*     */   {
/* 406 */     if ((term != null) && 
/* 407 */       (!$assertionsDisabled) && (this.lastDeleteTerm != null) && (term.compareTo(this.lastDeleteTerm) <= 0)) throw new AssertionError("lastTerm=" + this.lastDeleteTerm + " vs term=" + term);
/*     */ 
/* 410 */     this.lastDeleteTerm = (term == null ? null : new Term(term.field(), term.text()));
/* 411 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean checkDeleteStats()
/*     */   {
/* 416 */     int numTerms2 = 0;
/* 417 */     long bytesUsed2 = 0L;
/* 418 */     for (FrozenBufferedDeletes packet : this.deletes) {
/* 419 */       numTerms2 += packet.numTermDeletes;
/* 420 */       bytesUsed2 += packet.bytesUsed;
/*     */     }
/* 422 */     assert (numTerms2 == this.numTerms.get()) : ("numTerms2=" + numTerms2 + " vs " + this.numTerms.get());
/* 423 */     assert (bytesUsed2 == this.bytesUsed.get()) : ("bytesUsed2=" + bytesUsed2 + " vs " + this.bytesUsed);
/* 424 */     return true;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 135 */     sortByDelGen = new Comparator()
/*     */     {
/*     */       public int compare(SegmentInfo si1, SegmentInfo si2) {
/* 138 */         long cmp = si1.getBufferedDeletesGen() - si2.getBufferedDeletesGen();
/* 139 */         if (cmp > 0L)
/* 140 */           return 1;
/* 141 */         if (cmp < 0L) {
/* 142 */           return -1;
/*     */         }
/* 144 */         return 0;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static class QueryAndLimit
/*     */   {
/*     */     public final Query query;
/*     */     public final int limit;
/*     */ 
/*     */     public QueryAndLimit(Query query, int limit)
/*     */     {
/* 368 */       this.query = query;
/* 369 */       this.limit = limit;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ApplyDeletesResult
/*     */   {
/*     */     public final boolean anyDeletes;
/*     */     public final long gen;
/*     */     public final List<SegmentInfo> allDeleted;
/*     */ 
/*     */     ApplyDeletesResult(boolean anyDeletes, long gen, List<SegmentInfo> allDeleted)
/*     */     {
/* 128 */       this.anyDeletes = anyDeletes;
/* 129 */       this.gen = gen;
/* 130 */       this.allDeleted = allDeleted;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.BufferedDeletesStream
 * JD-Core Version:    0.6.0
 */