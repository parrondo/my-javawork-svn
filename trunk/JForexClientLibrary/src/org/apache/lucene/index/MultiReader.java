/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.FieldSelector;
/*     */ import org.apache.lucene.search.Similarity;
/*     */ import org.apache.lucene.util.MapBackedSet;
/*     */ 
/*     */ public class MultiReader extends IndexReader
/*     */   implements Cloneable
/*     */ {
/*     */   protected IndexReader[] subReaders;
/*     */   private int[] starts;
/*     */   private boolean[] decrefOnClose;
/*  41 */   private Map<String, byte[]> normsCache = new HashMap();
/*  42 */   private int maxDoc = 0;
/*  43 */   private int numDocs = -1;
/*  44 */   private boolean hasDeletions = false;
/*     */ 
/*     */   public MultiReader(IndexReader[] subReaders)
/*     */   {
/*  54 */     initialize(subReaders, true);
/*     */   }
/*     */ 
/*     */   public MultiReader(IndexReader[] subReaders, boolean closeSubReaders)
/*     */   {
/*  66 */     initialize(subReaders, closeSubReaders);
/*     */   }
/*     */ 
/*     */   private void initialize(IndexReader[] subReaders, boolean closeSubReaders) {
/*  70 */     this.subReaders = ((IndexReader[])subReaders.clone());
/*  71 */     this.starts = new int[subReaders.length + 1];
/*  72 */     this.decrefOnClose = new boolean[subReaders.length];
/*  73 */     for (int i = 0; i < subReaders.length; i++) {
/*  74 */       this.starts[i] = this.maxDoc;
/*  75 */       this.maxDoc += subReaders[i].maxDoc();
/*     */ 
/*  77 */       if (!closeSubReaders) {
/*  78 */         subReaders[i].incRef();
/*  79 */         this.decrefOnClose[i] = true;
/*     */       } else {
/*  81 */         this.decrefOnClose[i] = false;
/*     */       }
/*     */ 
/*  84 */       if (subReaders[i].hasDeletions())
/*  85 */         this.hasDeletions = true;
/*     */     }
/*  87 */     this.starts[subReaders.length] = this.maxDoc;
/*  88 */     this.readerFinishedListeners = new MapBackedSet(new ConcurrentHashMap());
/*     */   }
/*     */ 
/*     */   public synchronized IndexReader reopen()
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 112 */     return doReopen(false);
/*     */   }
/*     */ 
/*     */   public synchronized Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 127 */       return doReopen(true); } catch (Exception ex) {
/*     */     }
/* 129 */     throw new RuntimeException(ex);
/*     */   }
/*     */ 
/*     */   protected IndexReader doReopen(boolean doClone)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 142 */     ensureOpen();
/*     */ 
/* 144 */     boolean reopened = false;
/* 145 */     IndexReader[] newSubReaders = new IndexReader[this.subReaders.length];
/*     */ 
/* 147 */     boolean success = false;
/*     */     try {
/* 149 */       for (int i = 0; i < this.subReaders.length; i++) {
/* 150 */         if (doClone)
/* 151 */           newSubReaders[i] = ((IndexReader)this.subReaders[i].clone());
/*     */         else {
/* 153 */           newSubReaders[i] = this.subReaders[i].reopen();
/*     */         }
/*     */ 
/* 156 */         if (newSubReaders[i] != this.subReaders[i]) {
/* 157 */           reopened = true;
/*     */         }
/*     */       }
/* 160 */       success = true;
/*     */     } finally {
/* 162 */       if ((!success) && (reopened)) {
/* 163 */         for (int i = 0; i < newSubReaders.length; i++) {
/* 164 */           if (newSubReaders[i] == this.subReaders[i]) continue;
/*     */           try {
/* 166 */             newSubReaders[i].close();
/*     */           }
/*     */           catch (IOException ignore)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 175 */     if (reopened) {
/* 176 */       boolean[] newDecrefOnClose = new boolean[this.subReaders.length];
/* 177 */       for (int i = 0; i < this.subReaders.length; i++) {
/* 178 */         if (newSubReaders[i] == this.subReaders[i]) {
/* 179 */           newSubReaders[i].incRef();
/* 180 */           newDecrefOnClose[i] = true;
/*     */         }
/*     */       }
/* 183 */       MultiReader mr = new MultiReader(newSubReaders);
/* 184 */       mr.decrefOnClose = newDecrefOnClose;
/* 185 */       return mr;
/*     */     }
/* 187 */     return this;
/*     */   }
/*     */ 
/*     */   public TermFreqVector[] getTermFreqVectors(int n)
/*     */     throws IOException
/*     */   {
/* 193 */     ensureOpen();
/* 194 */     int i = readerIndex(n);
/* 195 */     return this.subReaders[i].getTermFreqVectors(n - this.starts[i]);
/*     */   }
/*     */ 
/*     */   public TermFreqVector getTermFreqVector(int n, String field)
/*     */     throws IOException
/*     */   {
/* 201 */     ensureOpen();
/* 202 */     int i = readerIndex(n);
/* 203 */     return this.subReaders[i].getTermFreqVector(n - this.starts[i], field);
/*     */   }
/*     */ 
/*     */   public void getTermFreqVector(int docNumber, String field, TermVectorMapper mapper)
/*     */     throws IOException
/*     */   {
/* 209 */     ensureOpen();
/* 210 */     int i = readerIndex(docNumber);
/* 211 */     this.subReaders[i].getTermFreqVector(docNumber - this.starts[i], field, mapper);
/*     */   }
/*     */ 
/*     */   public void getTermFreqVector(int docNumber, TermVectorMapper mapper) throws IOException
/*     */   {
/* 216 */     ensureOpen();
/* 217 */     int i = readerIndex(docNumber);
/* 218 */     this.subReaders[i].getTermFreqVector(docNumber - this.starts[i], mapper);
/*     */   }
/*     */ 
/*     */   public boolean isOptimized()
/*     */   {
/* 223 */     return false;
/*     */   }
/*     */ 
/*     */   public int numDocs()
/*     */   {
/* 231 */     if (this.numDocs == -1) {
/* 232 */       int n = 0;
/* 233 */       for (int i = 0; i < this.subReaders.length; i++)
/* 234 */         n += this.subReaders[i].numDocs();
/* 235 */       this.numDocs = n;
/*     */     }
/* 237 */     return this.numDocs;
/*     */   }
/*     */ 
/*     */   public int maxDoc()
/*     */   {
/* 243 */     return this.maxDoc;
/*     */   }
/*     */ 
/*     */   public Document document(int n, FieldSelector fieldSelector)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 249 */     ensureOpen();
/* 250 */     int i = readerIndex(n);
/* 251 */     return this.subReaders[i].document(n - this.starts[i], fieldSelector);
/*     */   }
/*     */ 
/*     */   public boolean isDeleted(int n)
/*     */   {
/* 257 */     int i = readerIndex(n);
/* 258 */     return this.subReaders[i].isDeleted(n - this.starts[i]);
/*     */   }
/*     */ 
/*     */   public boolean hasDeletions()
/*     */   {
/* 264 */     return this.hasDeletions;
/*     */   }
/*     */ 
/*     */   protected void doDelete(int n) throws CorruptIndexException, IOException
/*     */   {
/* 269 */     this.numDocs = -1;
/* 270 */     int i = readerIndex(n);
/* 271 */     this.subReaders[i].deleteDocument(n - this.starts[i]);
/* 272 */     this.hasDeletions = true;
/*     */   }
/*     */ 
/*     */   protected void doUndeleteAll() throws CorruptIndexException, IOException
/*     */   {
/* 277 */     for (int i = 0; i < this.subReaders.length; i++) {
/* 278 */       this.subReaders[i].undeleteAll();
/*     */     }
/* 280 */     this.hasDeletions = false;
/* 281 */     this.numDocs = -1;
/*     */   }
/*     */ 
/*     */   private int readerIndex(int n) {
/* 285 */     return DirectoryReader.readerIndex(n, this.starts, this.subReaders.length);
/*     */   }
/*     */ 
/*     */   public boolean hasNorms(String field) throws IOException
/*     */   {
/* 290 */     ensureOpen();
/* 291 */     for (int i = 0; i < this.subReaders.length; i++) {
/* 292 */       if (this.subReaders[i].hasNorms(field)) return true;
/*     */     }
/* 294 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized byte[] norms(String field) throws IOException
/*     */   {
/* 299 */     ensureOpen();
/* 300 */     byte[] bytes = (byte[])this.normsCache.get(field);
/* 301 */     if (bytes != null)
/* 302 */       return bytes;
/* 303 */     if (!hasNorms(field)) {
/* 304 */       return null;
/*     */     }
/* 306 */     bytes = new byte[maxDoc()];
/* 307 */     for (int i = 0; i < this.subReaders.length; i++)
/* 308 */       this.subReaders[i].norms(field, bytes, this.starts[i]);
/* 309 */     this.normsCache.put(field, bytes);
/* 310 */     return bytes;
/*     */   }
/*     */ 
/*     */   public synchronized void norms(String field, byte[] result, int offset)
/*     */     throws IOException
/*     */   {
/* 316 */     ensureOpen();
/* 317 */     byte[] bytes = (byte[])this.normsCache.get(field);
/* 318 */     for (int i = 0; i < this.subReaders.length; i++) {
/* 319 */       this.subReaders[i].norms(field, result, offset + this.starts[i]);
/*     */     }
/* 321 */     if ((bytes == null) && (!hasNorms(field)))
/* 322 */       Arrays.fill(result, offset, result.length, Similarity.getDefault().encodeNormValue(1.0F));
/* 323 */     else if (bytes != null)
/* 324 */       System.arraycopy(bytes, 0, result, offset, maxDoc());
/*     */     else
/* 326 */       for (int i = 0; i < this.subReaders.length; i++)
/* 327 */         this.subReaders[i].norms(field, result, offset + this.starts[i]);
/*     */   }
/*     */ 
/*     */   protected void doSetNorm(int n, String field, byte value)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 335 */     synchronized (this.normsCache) {
/* 336 */       this.normsCache.remove(field);
/*     */     }
/* 338 */     int i = readerIndex(n);
/* 339 */     this.subReaders[i].setNorm(n - this.starts[i], field, value);
/*     */   }
/*     */ 
/*     */   public TermEnum terms() throws IOException
/*     */   {
/* 344 */     ensureOpen();
/* 345 */     if (this.subReaders.length == 1)
/*     */     {
/* 347 */       return this.subReaders[0].terms();
/*     */     }
/* 349 */     return new DirectoryReader.MultiTermEnum(this, this.subReaders, this.starts, null);
/*     */   }
/*     */ 
/*     */   public TermEnum terms(Term term)
/*     */     throws IOException
/*     */   {
/* 355 */     ensureOpen();
/* 356 */     if (this.subReaders.length == 1)
/*     */     {
/* 358 */       return this.subReaders[0].terms(term);
/*     */     }
/* 360 */     return new DirectoryReader.MultiTermEnum(this, this.subReaders, this.starts, term);
/*     */   }
/*     */ 
/*     */   public int docFreq(Term t)
/*     */     throws IOException
/*     */   {
/* 366 */     ensureOpen();
/* 367 */     int total = 0;
/* 368 */     for (int i = 0; i < this.subReaders.length; i++)
/* 369 */       total += this.subReaders[i].docFreq(t);
/* 370 */     return total;
/*     */   }
/*     */ 
/*     */   public TermDocs termDocs() throws IOException
/*     */   {
/* 375 */     ensureOpen();
/* 376 */     if (this.subReaders.length == 1)
/*     */     {
/* 378 */       return this.subReaders[0].termDocs();
/*     */     }
/* 380 */     return new DirectoryReader.MultiTermDocs(this, this.subReaders, this.starts);
/*     */   }
/*     */ 
/*     */   public TermDocs termDocs(Term term)
/*     */     throws IOException
/*     */   {
/* 386 */     ensureOpen();
/* 387 */     if (this.subReaders.length == 1)
/*     */     {
/* 389 */       return this.subReaders[0].termDocs(term);
/*     */     }
/* 391 */     return super.termDocs(term);
/*     */   }
/*     */ 
/*     */   public TermPositions termPositions()
/*     */     throws IOException
/*     */   {
/* 397 */     ensureOpen();
/* 398 */     if (this.subReaders.length == 1)
/*     */     {
/* 400 */       return this.subReaders[0].termPositions();
/*     */     }
/* 402 */     return new DirectoryReader.MultiTermPositions(this, this.subReaders, this.starts);
/*     */   }
/*     */ 
/*     */   protected void doCommit(Map<String, String> commitUserData)
/*     */     throws IOException
/*     */   {
/* 408 */     for (int i = 0; i < this.subReaders.length; i++)
/* 409 */       this.subReaders[i].commit(commitUserData);
/*     */   }
/*     */ 
/*     */   protected synchronized void doClose() throws IOException
/*     */   {
/* 414 */     for (int i = 0; i < this.subReaders.length; i++)
/* 415 */       if (this.decrefOnClose[i] != 0)
/* 416 */         this.subReaders[i].decRef();
/*     */       else
/* 418 */         this.subReaders[i].close();
/*     */   }
/*     */ 
/*     */   public Collection<String> getFieldNames(IndexReader.FieldOption fieldNames)
/*     */   {
/* 425 */     ensureOpen();
/* 426 */     return DirectoryReader.getFieldNames(fieldNames, this.subReaders);
/*     */   }
/*     */ 
/*     */   public boolean isCurrent()
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 434 */     for (int i = 0; i < this.subReaders.length; i++) {
/* 435 */       if (!this.subReaders[i].isCurrent()) {
/* 436 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 441 */     return true;
/*     */   }
/*     */ 
/*     */   public long getVersion()
/*     */   {
/* 449 */     throw new UnsupportedOperationException("MultiReader does not support this method.");
/*     */   }
/*     */ 
/*     */   public IndexReader[] getSequentialSubReaders()
/*     */   {
/* 454 */     return this.subReaders;
/*     */   }
/*     */ 
/*     */   public void addReaderFinishedListener(IndexReader.ReaderFinishedListener listener)
/*     */   {
/* 459 */     super.addReaderFinishedListener(listener);
/* 460 */     for (IndexReader sub : this.subReaders)
/* 461 */       sub.addReaderFinishedListener(listener);
/*     */   }
/*     */ 
/*     */   public void removeReaderFinishedListener(IndexReader.ReaderFinishedListener listener)
/*     */   {
/* 467 */     super.removeReaderFinishedListener(listener);
/* 468 */     for (IndexReader sub : this.subReaders)
/* 469 */       sub.removeReaderFinishedListener(listener);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.MultiReader
 * JD-Core Version:    0.6.0
 */