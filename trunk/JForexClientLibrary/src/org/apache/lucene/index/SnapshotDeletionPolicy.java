/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.store.Directory;
/*     */ 
/*     */ public class SnapshotDeletionPolicy
/*     */   implements IndexDeletionPolicy
/*     */ {
/* 145 */   private Map<String, SnapshotInfo> idToSnapshot = new HashMap();
/*     */ 
/* 148 */   private Map<String, Set<String>> segmentsFileToIDs = new HashMap();
/*     */   private IndexDeletionPolicy primary;
/*     */   protected IndexCommit lastCommit;
/*     */ 
/*     */   public SnapshotDeletionPolicy(IndexDeletionPolicy primary)
/*     */   {
/* 154 */     this.primary = primary;
/*     */   }
/*     */ 
/*     */   public SnapshotDeletionPolicy(IndexDeletionPolicy primary, Map<String, String> snapshotsInfo)
/*     */   {
/* 173 */     this(primary);
/*     */ 
/* 175 */     if (snapshotsInfo != null)
/*     */     {
/* 178 */       for (Map.Entry e : snapshotsInfo.entrySet())
/* 179 */         registerSnapshotInfo((String)e.getKey(), (String)e.getValue(), null);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void checkSnapshotted(String id)
/*     */   {
/* 189 */     if (isSnapshotted(id))
/* 190 */       throw new IllegalStateException("Snapshot ID " + id + " is already used - must be unique");
/*     */   }
/*     */ 
/*     */   protected void registerSnapshotInfo(String id, String segment, IndexCommit commit)
/*     */   {
/* 197 */     this.idToSnapshot.put(id, new SnapshotInfo(id, segment, commit));
/* 198 */     Set ids = (Set)this.segmentsFileToIDs.get(segment);
/* 199 */     if (ids == null) {
/* 200 */       ids = new HashSet();
/* 201 */       this.segmentsFileToIDs.put(segment, ids);
/*     */     }
/* 203 */     ids.add(id);
/*     */   }
/*     */ 
/*     */   protected List<IndexCommit> wrapCommits(List<? extends IndexCommit> commits) {
/* 207 */     List wrappedCommits = new ArrayList(commits.size());
/* 208 */     for (IndexCommit ic : commits) {
/* 209 */       wrappedCommits.add(new SnapshotCommitPoint(ic));
/*     */     }
/* 211 */     return wrappedCommits;
/*     */   }
/*     */ 
/*     */   public synchronized IndexCommit getSnapshot(String id)
/*     */   {
/* 227 */     SnapshotInfo snapshotInfo = (SnapshotInfo)this.idToSnapshot.get(id);
/* 228 */     if (snapshotInfo == null) {
/* 229 */       throw new IllegalStateException("No snapshot exists by ID: " + id);
/*     */     }
/* 231 */     return snapshotInfo.commit;
/*     */   }
/*     */ 
/*     */   public synchronized Map<String, String> getSnapshots()
/*     */   {
/* 241 */     Map snapshots = new HashMap();
/* 242 */     for (Map.Entry e : this.idToSnapshot.entrySet()) {
/* 243 */       snapshots.put(e.getKey(), ((SnapshotInfo)e.getValue()).segmentsFileName);
/*     */     }
/* 245 */     return snapshots;
/*     */   }
/*     */ 
/*     */   public boolean isSnapshotted(String id)
/*     */   {
/* 254 */     return this.idToSnapshot.containsKey(id);
/*     */   }
/*     */ 
/*     */   public synchronized void onCommit(List<? extends IndexCommit> commits) throws IOException
/*     */   {
/* 259 */     this.primary.onCommit(wrapCommits(commits));
/* 260 */     this.lastCommit = ((IndexCommit)commits.get(commits.size() - 1));
/*     */   }
/*     */ 
/*     */   public synchronized void onInit(List<? extends IndexCommit> commits) throws IOException
/*     */   {
/* 265 */     this.primary.onInit(wrapCommits(commits));
/* 266 */     this.lastCommit = ((IndexCommit)commits.get(commits.size() - 1));
/*     */ 
/* 272 */     for (Iterator i$ = commits.iterator(); i$.hasNext(); ) { commit = (IndexCommit)i$.next();
/* 273 */       Set ids = (Set)this.segmentsFileToIDs.get(commit.getSegmentsFileName());
/* 274 */       if (ids != null)
/* 275 */         for (String id : ids)
/* 276 */           ((SnapshotInfo)this.idToSnapshot.get(id)).commit = commit;
/*     */     }
/*     */     IndexCommit commit;
/* 291 */     ArrayList idsToRemove = null;
/* 292 */     for (Map.Entry e : this.idToSnapshot.entrySet()) {
/* 293 */       if (((SnapshotInfo)e.getValue()).commit == null) {
/* 294 */         if (idsToRemove == null) {
/* 295 */           idsToRemove = new ArrayList();
/*     */         }
/* 297 */         idsToRemove.add(e.getKey());
/*     */       }
/*     */     }
/*     */ 
/* 301 */     if (idsToRemove != null)
/* 302 */       for (String id : idsToRemove) {
/* 303 */         SnapshotInfo info = (SnapshotInfo)this.idToSnapshot.remove(id);
/* 304 */         this.segmentsFileToIDs.remove(info.segmentsFileName);
/*     */       }
/*     */   }
/*     */ 
/*     */   public synchronized void release(String id)
/*     */     throws IOException
/*     */   {
/* 318 */     SnapshotInfo info = (SnapshotInfo)this.idToSnapshot.remove(id);
/* 319 */     if (info == null) {
/* 320 */       throw new IllegalStateException("Snapshot doesn't exist: " + id);
/*     */     }
/* 322 */     Set ids = (Set)this.segmentsFileToIDs.get(info.segmentsFileName);
/* 323 */     if (ids != null) {
/* 324 */       ids.remove(id);
/* 325 */       if (ids.size() == 0)
/* 326 */         this.segmentsFileToIDs.remove(info.segmentsFileName);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized IndexCommit snapshot(String id)
/*     */     throws IOException
/*     */   {
/* 354 */     if (this.lastCommit == null)
/*     */     {
/* 357 */       throw new IllegalStateException("No index commit to snapshot");
/*     */     }
/*     */ 
/* 361 */     checkSnapshotted(id);
/*     */ 
/* 363 */     registerSnapshotInfo(id, this.lastCommit.getSegmentsFileName(), this.lastCommit);
/* 364 */     return this.lastCommit;
/*     */   }
/*     */ 
/*     */   protected class SnapshotCommitPoint extends IndexCommit
/*     */   {
/*     */     protected IndexCommit cp;
/*     */ 
/*     */     protected SnapshotCommitPoint(IndexCommit cp)
/*     */     {
/*  76 */       this.cp = cp;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  81 */       return "SnapshotDeletionPolicy.SnapshotCommitPoint(" + this.cp + ")";
/*     */     }
/*     */ 
/*     */     protected boolean shouldDelete(String segmentsFileName)
/*     */     {
/*  89 */       return !SnapshotDeletionPolicy.this.segmentsFileToIDs.containsKey(segmentsFileName);
/*     */     }
/*     */ 
/*     */     public void delete()
/*     */     {
/*  94 */       synchronized (SnapshotDeletionPolicy.this)
/*     */       {
/*  97 */         if (shouldDelete(getSegmentsFileName()))
/*  98 */           this.cp.delete();
/*     */       }
/*     */     }
/*     */ 
/*     */     public Directory getDirectory()
/*     */     {
/* 105 */       return this.cp.getDirectory();
/*     */     }
/*     */ 
/*     */     public Collection<String> getFileNames() throws IOException
/*     */     {
/* 110 */       return this.cp.getFileNames();
/*     */     }
/*     */ 
/*     */     public long getGeneration()
/*     */     {
/* 115 */       return this.cp.getGeneration();
/*     */     }
/*     */ 
/*     */     public String getSegmentsFileName()
/*     */     {
/* 120 */       return this.cp.getSegmentsFileName();
/*     */     }
/*     */ 
/*     */     public Map<String, String> getUserData() throws IOException
/*     */     {
/* 125 */       return this.cp.getUserData();
/*     */     }
/*     */ 
/*     */     public long getVersion()
/*     */     {
/* 130 */       return this.cp.getVersion();
/*     */     }
/*     */ 
/*     */     public boolean isDeleted()
/*     */     {
/* 135 */       return this.cp.isDeleted();
/*     */     }
/*     */ 
/*     */     public boolean isOptimized()
/*     */     {
/* 140 */       return this.cp.isOptimized();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SnapshotInfo
/*     */   {
/*     */     String id;
/*     */     String segmentsFileName;
/*     */     IndexCommit commit;
/*     */ 
/*     */     public SnapshotInfo(String id, String segmentsFileName, IndexCommit commit)
/*     */     {
/*  61 */       this.id = id;
/*  62 */       this.segmentsFileName = segmentsFileName;
/*  63 */       this.commit = commit;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  68 */       return this.id + " : " + this.segmentsFileName;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SnapshotDeletionPolicy
 * JD-Core Version:    0.6.0
 */