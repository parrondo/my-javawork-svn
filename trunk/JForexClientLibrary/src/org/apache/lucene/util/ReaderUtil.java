/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ 
/*     */ public final class ReaderUtil
/*     */ {
/*     */   public static void gatherSubReaders(List<IndexReader> allSubReaders, IndexReader reader)
/*     */   {
/*  42 */     IndexReader[] subReaders = reader.getSequentialSubReaders();
/*  43 */     if (subReaders == null)
/*     */     {
/*  45 */       allSubReaders.add(reader);
/*     */     }
/*  47 */     else for (int i = 0; i < subReaders.length; i++)
/*  48 */         gatherSubReaders(allSubReaders, subReaders[i]);
/*     */   }
/*     */ 
/*     */   public static IndexReader subReader(int doc, IndexReader reader)
/*     */   {
/* 100 */     List subReadersList = new ArrayList();
/* 101 */     gatherSubReaders(subReadersList, reader);
/* 102 */     IndexReader[] subReaders = (IndexReader[])subReadersList.toArray(new IndexReader[subReadersList.size()]);
/*     */ 
/* 104 */     int[] docStarts = new int[subReaders.length];
/* 105 */     int maxDoc = 0;
/* 106 */     for (int i = 0; i < subReaders.length; i++) {
/* 107 */       docStarts[i] = maxDoc;
/* 108 */       maxDoc += subReaders[i].maxDoc();
/*     */     }
/* 110 */     return subReaders[subIndex(doc, docStarts)];
/*     */   }
/*     */ 
/*     */   public static IndexReader subReader(IndexReader reader, int subIndex)
/*     */   {
/* 121 */     List subReadersList = new ArrayList();
/* 122 */     gatherSubReaders(subReadersList, reader);
/* 123 */     IndexReader[] subReaders = (IndexReader[])subReadersList.toArray(new IndexReader[subReadersList.size()]);
/*     */ 
/* 125 */     return subReaders[subIndex];
/*     */   }
/*     */ 
/*     */   public static int subIndex(int n, int[] docStarts)
/*     */   {
/* 135 */     int size = docStarts.length;
/* 136 */     int lo = 0;
/* 137 */     int hi = size - 1;
/* 138 */     while (hi >= lo) {
/* 139 */       int mid = lo + hi >>> 1;
/* 140 */       int midValue = docStarts[mid];
/* 141 */       if (n < midValue) {
/* 142 */         hi = mid - 1;
/* 143 */       } else if (n > midValue) {
/* 144 */         lo = mid + 1;
/*     */       } else {
/* 146 */         while ((mid + 1 < size) && (docStarts[(mid + 1)] == midValue)) {
/* 147 */           mid++;
/*     */         }
/* 149 */         return mid;
/*     */       }
/*     */     }
/* 152 */     return hi;
/*     */   }
/*     */ 
/*     */   public static abstract class Gather
/*     */   {
/*     */     private final IndexReader topReader;
/*     */ 
/*     */     public Gather(IndexReader r)
/*     */     {
/*  62 */       this.topReader = r;
/*     */     }
/*     */ 
/*     */     public int run() throws IOException {
/*  66 */       return run(0, this.topReader);
/*     */     }
/*     */ 
/*     */     public int run(int docBase) throws IOException {
/*  70 */       return run(docBase, this.topReader);
/*     */     }
/*     */ 
/*     */     private int run(int base, IndexReader reader) throws IOException {
/*  74 */       IndexReader[] subReaders = reader.getSequentialSubReaders();
/*  75 */       if (subReaders == null)
/*     */       {
/*  77 */         add(base, reader);
/*  78 */         base += reader.maxDoc();
/*     */       }
/*     */       else {
/*  81 */         for (int i = 0; i < subReaders.length; i++) {
/*  82 */           base = run(base, subReaders[i]);
/*     */         }
/*     */       }
/*     */ 
/*  86 */       return base;
/*     */     }
/*     */ 
/*     */     protected abstract void add(int paramInt, IndexReader paramIndexReader)
/*     */       throws IOException;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.ReaderUtil
 * JD-Core Version:    0.6.0
 */