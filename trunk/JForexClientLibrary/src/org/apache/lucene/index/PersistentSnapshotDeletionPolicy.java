/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.Field;
/*     */ import org.apache.lucene.document.Field.Index;
/*     */ import org.apache.lucene.document.Field.Store;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.LockObtainFailedException;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public class PersistentSnapshotDeletionPolicy extends SnapshotDeletionPolicy
/*     */ {
/*     */   private static final String SNAPSHOTS_ID = "$SNAPSHOTS_DOC$";
/*     */   private final IndexWriter writer;
/*     */ 
/*     */   public static Map<String, String> readSnapshotsInfo(Directory dir)
/*     */     throws IOException
/*     */   {
/*  67 */     IndexReader r = IndexReader.open(dir, true);
/*  68 */     Map snapshots = new HashMap();
/*     */     try {
/*  70 */       int numDocs = r.numDocs();
/*     */ 
/*  72 */       if (numDocs == 1) {
/*  73 */         Document doc = r.document(r.maxDoc() - 1);
/*  74 */         Field sid = doc.getField("$SNAPSHOTS_DOC$");
/*  75 */         if (sid == null) {
/*  76 */           throw new IllegalStateException("directory is not a valid snapshots store!");
/*     */         }
/*  78 */         doc.removeField("$SNAPSHOTS_DOC$");
/*  79 */         for (Fieldable f : doc.getFields())
/*  80 */           snapshots.put(f.name(), f.stringValue());
/*     */       }
/*  82 */       else if (numDocs != 0) {
/*  83 */         throw new IllegalStateException("should be at most 1 document in the snapshots directory: " + numDocs);
/*     */       }
/*     */     }
/*     */     finally {
/*  87 */       r.close();
/*     */     }
/*  89 */     return snapshots;
/*     */   }
/*     */ 
/*     */   public PersistentSnapshotDeletionPolicy(IndexDeletionPolicy primary, Directory dir, IndexWriterConfig.OpenMode mode, Version matchVersion)
/*     */     throws CorruptIndexException, LockObtainFailedException, IOException
/*     */   {
/* 114 */     super(primary, null);
/*     */ 
/* 117 */     this.writer = new IndexWriter(dir, new IndexWriterConfig(matchVersion, null).setOpenMode(mode));
/* 118 */     if (mode != IndexWriterConfig.OpenMode.APPEND)
/*     */     {
/* 122 */       this.writer.commit();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 129 */       for (Map.Entry e : readSnapshotsInfo(dir).entrySet())
/* 130 */         registerSnapshotInfo((String)e.getKey(), (String)e.getValue(), null);
/*     */     }
/*     */     catch (RuntimeException e) {
/* 133 */       this.writer.close();
/* 134 */       throw e;
/*     */     } catch (IOException e) {
/* 136 */       this.writer.close();
/* 137 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void onInit(List<? extends IndexCommit> commits)
/*     */     throws IOException
/*     */   {
/* 148 */     super.onInit(commits);
/* 149 */     persistSnapshotInfos(null, null);
/*     */   }
/*     */ 
/*     */   public synchronized IndexCommit snapshot(String id)
/*     */     throws IOException
/*     */   {
/* 160 */     checkSnapshotted(id);
/* 161 */     if ("$SNAPSHOTS_DOC$".equals(id)) {
/* 162 */       throw new IllegalArgumentException(id + " is reserved and cannot be used as a snapshot id");
/*     */     }
/* 164 */     persistSnapshotInfos(id, this.lastCommit.getSegmentsFileName());
/* 165 */     return super.snapshot(id);
/*     */   }
/*     */ 
/*     */   public synchronized void release(String id)
/*     */     throws IOException
/*     */   {
/* 176 */     super.release(id);
/* 177 */     persistSnapshotInfos(null, null);
/*     */   }
/*     */ 
/*     */   public void close() throws CorruptIndexException, IOException
/*     */   {
/* 182 */     this.writer.close();
/*     */   }
/*     */ 
/*     */   private void persistSnapshotInfos(String id, String segment)
/*     */     throws IOException
/*     */   {
/* 190 */     this.writer.deleteAll();
/* 191 */     Document d = new Document();
/* 192 */     d.add(new Field("$SNAPSHOTS_DOC$", "", Field.Store.YES, Field.Index.NO));
/* 193 */     for (Map.Entry e : super.getSnapshots().entrySet()) {
/* 194 */       d.add(new Field((String)e.getKey(), (String)e.getValue(), Field.Store.YES, Field.Index.NO));
/*     */     }
/* 196 */     if (id != null) {
/* 197 */       d.add(new Field(id, segment, Field.Store.YES, Field.Index.NO));
/*     */     }
/* 199 */     this.writer.addDocument(d);
/* 200 */     this.writer.commit();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.PersistentSnapshotDeletionPolicy
 * JD-Core Version:    0.6.0
 */