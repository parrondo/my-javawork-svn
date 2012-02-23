/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.util.SetOnce;
/*     */ 
/*     */ public abstract class MergePolicy
/*     */   implements Closeable
/*     */ {
/*     */   protected final SetOnce<IndexWriter> writer;
/*     */ 
/*     */   public MergePolicy()
/*     */   {
/* 260 */     this.writer = new SetOnce();
/*     */   }
/*     */ 
/*     */   public void setIndexWriter(IndexWriter writer)
/*     */   {
/* 271 */     this.writer.set(writer);
/*     */   }
/*     */ 
/*     */   public abstract MergeSpecification findMerges(SegmentInfos paramSegmentInfos)
/*     */     throws CorruptIndexException, IOException;
/*     */ 
/*     */   public abstract MergeSpecification findMergesForOptimize(SegmentInfos paramSegmentInfos, int paramInt, Map<SegmentInfo, Boolean> paramMap)
/*     */     throws CorruptIndexException, IOException;
/*     */ 
/*     */   public abstract MergeSpecification findMergesToExpungeDeletes(SegmentInfos paramSegmentInfos)
/*     */     throws CorruptIndexException, IOException;
/*     */ 
/*     */   public abstract void close();
/*     */ 
/*     */   public abstract boolean useCompoundFile(SegmentInfos paramSegmentInfos, SegmentInfo paramSegmentInfo)
/*     */     throws IOException;
/*     */ 
/*     */   public static class MergeAbortedException extends IOException
/*     */   {
/*     */     public MergeAbortedException()
/*     */     {
/* 245 */       super();
/*     */     }
/*     */     public MergeAbortedException(String message) {
/* 248 */       super();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class MergeException extends RuntimeException
/*     */   {
/*     */     private Directory dir;
/*     */ 
/*     */     public MergeException(String message, Directory dir)
/*     */     {
/* 228 */       super();
/* 229 */       this.dir = dir;
/*     */     }
/*     */ 
/*     */     public MergeException(Throwable exc, Directory dir) {
/* 233 */       super();
/* 234 */       this.dir = dir;
/*     */     }
/*     */ 
/*     */     public Directory getDirectory()
/*     */     {
/* 239 */       return this.dir;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class MergeSpecification
/*     */   {
/* 206 */     public final List<MergePolicy.OneMerge> merges = new ArrayList();
/*     */ 
/*     */     public void add(MergePolicy.OneMerge merge) {
/* 209 */       this.merges.add(merge);
/*     */     }
/*     */ 
/*     */     public String segString(Directory dir) {
/* 213 */       StringBuilder b = new StringBuilder();
/* 214 */       b.append("MergeSpec:\n");
/* 215 */       int count = this.merges.size();
/* 216 */       for (int i = 0; i < count; i++)
/* 217 */         b.append("  ").append(1 + i).append(": ").append(((MergePolicy.OneMerge)this.merges.get(i)).segString(dir));
/* 218 */       return b.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class OneMerge
/*     */   {
/*     */     SegmentInfo info;
/*     */     boolean optimize;
/*     */     boolean registerDone;
/*     */     long mergeGen;
/*     */     boolean isExternal;
/*     */     int maxNumSegmentsOptimize;
/*     */     public long estimatedMergeBytes;
/*     */     List<SegmentReader> readers;
/*     */     List<SegmentReader> readerClones;
/*     */     public final List<SegmentInfo> segments;
/*     */     public final int totalDocCount;
/*     */     boolean aborted;
/*     */     Throwable error;
/*     */     boolean paused;
/*     */ 
/*     */     public OneMerge(List<SegmentInfo> segments)
/*     */     {
/*  85 */       if (0 == segments.size()) {
/*  86 */         throw new RuntimeException("segments must include at least one segment");
/*     */       }
/*  88 */       this.segments = new ArrayList(segments);
/*  89 */       int count = 0;
/*  90 */       for (SegmentInfo info : segments) {
/*  91 */         count += info.docCount;
/*     */       }
/*  93 */       this.totalDocCount = count;
/*     */     }
/*     */ 
/*     */     synchronized void setException(Throwable error)
/*     */     {
/*  99 */       this.error = error;
/*     */     }
/*     */ 
/*     */     synchronized Throwable getException()
/*     */     {
/* 105 */       return this.error;
/*     */     }
/*     */ 
/*     */     synchronized void abort()
/*     */     {
/* 112 */       this.aborted = true;
/* 113 */       notifyAll();
/*     */     }
/*     */ 
/*     */     synchronized boolean isAborted()
/*     */     {
/* 118 */       return this.aborted;
/*     */     }
/*     */ 
/*     */     synchronized void checkAborted(Directory dir) throws MergePolicy.MergeAbortedException {
/* 122 */       if (this.aborted) {
/* 123 */         throw new MergePolicy.MergeAbortedException("merge is aborted: " + segString(dir));
/*     */       }
/*     */ 
/* 126 */       while (this.paused)
/*     */       {
/*     */         try
/*     */         {
/* 130 */           wait(1000L);
/*     */         } catch (InterruptedException ie) {
/* 132 */           throw new RuntimeException(ie);
/*     */         }
/* 134 */         if (this.aborted)
/* 135 */           throw new MergePolicy.MergeAbortedException("merge is aborted: " + segString(dir));
/*     */       }
/*     */     }
/*     */ 
/*     */     public synchronized void setPause(boolean paused)
/*     */     {
/* 141 */       this.paused = paused;
/* 142 */       if (!paused)
/*     */       {
/* 144 */         notifyAll();
/*     */       }
/*     */     }
/*     */ 
/*     */     public synchronized boolean getPause() {
/* 149 */       return this.paused;
/*     */     }
/*     */ 
/*     */     public String segString(Directory dir) {
/* 153 */       StringBuilder b = new StringBuilder();
/* 154 */       int numSegments = this.segments.size();
/* 155 */       for (int i = 0; i < numSegments; i++) {
/* 156 */         if (i > 0) b.append(' ');
/* 157 */         b.append(((SegmentInfo)this.segments.get(i)).toString(dir, 0));
/*     */       }
/* 159 */       if (this.info != null)
/* 160 */         b.append(" into ").append(this.info.name);
/* 161 */       if (this.optimize)
/* 162 */         b.append(" [optimize]");
/* 163 */       if (this.aborted) {
/* 164 */         b.append(" [ABORTED]");
/*     */       }
/* 166 */       return b.toString();
/*     */     }
/*     */ 
/*     */     public long totalBytesSize()
/*     */       throws IOException
/*     */     {
/* 174 */       long total = 0L;
/* 175 */       for (SegmentInfo info : this.segments) {
/* 176 */         total += info.sizeInBytes(true);
/*     */       }
/* 178 */       return total;
/*     */     }
/*     */ 
/*     */     public int totalNumDocs()
/*     */       throws IOException
/*     */     {
/* 186 */       int total = 0;
/* 187 */       for (SegmentInfo info : this.segments) {
/* 188 */         total += info.docCount;
/*     */       }
/* 190 */       return total;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.MergePolicy
 * JD-Core Version:    0.6.0
 */