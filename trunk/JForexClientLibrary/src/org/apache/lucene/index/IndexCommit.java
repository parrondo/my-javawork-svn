/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import org.apache.lucene.store.Directory;
/*     */ 
/*     */ public abstract class IndexCommit
/*     */   implements Comparable<IndexCommit>
/*     */ {
/*     */   public abstract String getSegmentsFileName();
/*     */ 
/*     */   public abstract Collection<String> getFileNames()
/*     */     throws IOException;
/*     */ 
/*     */   public abstract Directory getDirectory();
/*     */ 
/*     */   public abstract void delete();
/*     */ 
/*     */   public abstract boolean isDeleted();
/*     */ 
/*     */   public abstract boolean isOptimized();
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/*  84 */     if ((other instanceof IndexCommit)) {
/*  85 */       IndexCommit otherCommit = (IndexCommit)other;
/*  86 */       return (otherCommit.getDirectory().equals(getDirectory())) && (otherCommit.getVersion() == getVersion());
/*     */     }
/*  88 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  93 */     return (int)(getDirectory().hashCode() + getVersion());
/*     */   }
/*     */ 
/*     */   public abstract long getVersion();
/*     */ 
/*     */   public abstract long getGeneration();
/*     */ 
/*     */   public long getTimestamp()
/*     */     throws IOException
/*     */   {
/* 110 */     return getDirectory().fileModified(getSegmentsFileName());
/*     */   }
/*     */ 
/*     */   public abstract Map<String, String> getUserData()
/*     */     throws IOException;
/*     */ 
/*     */   public int compareTo(IndexCommit commit)
/*     */   {
/* 119 */     long gen = getGeneration();
/* 120 */     long comgen = commit.getGeneration();
/* 121 */     if (gen < comgen)
/* 122 */       return -1;
/* 123 */     if (gen > comgen) {
/* 124 */       return 1;
/*     */     }
/* 126 */     return 0;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexCommit
 * JD-Core Version:    0.6.0
 */