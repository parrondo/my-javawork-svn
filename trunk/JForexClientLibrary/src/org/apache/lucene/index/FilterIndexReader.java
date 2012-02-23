/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.FieldSelector;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.util.MapBackedSet;
/*     */ 
/*     */ public class FilterIndexReader extends IndexReader
/*     */ {
/*     */   protected IndexReader in;
/*     */ 
/*     */   public FilterIndexReader(IndexReader in)
/*     */   {
/* 111 */     this.in = in;
/* 112 */     this.readerFinishedListeners = new MapBackedSet(new ConcurrentHashMap());
/*     */   }
/*     */ 
/*     */   public Directory directory()
/*     */   {
/* 117 */     return this.in.directory();
/*     */   }
/*     */ 
/*     */   public TermFreqVector[] getTermFreqVectors(int docNumber)
/*     */     throws IOException
/*     */   {
/* 123 */     ensureOpen();
/* 124 */     return this.in.getTermFreqVectors(docNumber);
/*     */   }
/*     */ 
/*     */   public TermFreqVector getTermFreqVector(int docNumber, String field)
/*     */     throws IOException
/*     */   {
/* 130 */     ensureOpen();
/* 131 */     return this.in.getTermFreqVector(docNumber, field);
/*     */   }
/*     */ 
/*     */   public void getTermFreqVector(int docNumber, String field, TermVectorMapper mapper)
/*     */     throws IOException
/*     */   {
/* 137 */     ensureOpen();
/* 138 */     this.in.getTermFreqVector(docNumber, field, mapper);
/*     */   }
/*     */ 
/*     */   public void getTermFreqVector(int docNumber, TermVectorMapper mapper)
/*     */     throws IOException
/*     */   {
/* 144 */     ensureOpen();
/* 145 */     this.in.getTermFreqVector(docNumber, mapper);
/*     */   }
/*     */ 
/*     */   public int numDocs()
/*     */   {
/* 151 */     return this.in.numDocs();
/*     */   }
/*     */ 
/*     */   public int maxDoc()
/*     */   {
/* 157 */     return this.in.maxDoc();
/*     */   }
/*     */ 
/*     */   public Document document(int n, FieldSelector fieldSelector) throws CorruptIndexException, IOException
/*     */   {
/* 162 */     ensureOpen();
/* 163 */     return this.in.document(n, fieldSelector);
/*     */   }
/*     */ 
/*     */   public boolean isDeleted(int n)
/*     */   {
/* 169 */     return this.in.isDeleted(n);
/*     */   }
/*     */ 
/*     */   public boolean hasDeletions()
/*     */   {
/* 175 */     return this.in.hasDeletions();
/*     */   }
/*     */ 
/*     */   protected void doUndeleteAll() throws CorruptIndexException, IOException {
/* 179 */     this.in.undeleteAll();
/*     */   }
/*     */ 
/*     */   public boolean hasNorms(String field) throws IOException {
/* 183 */     ensureOpen();
/* 184 */     return this.in.hasNorms(field);
/*     */   }
/*     */ 
/*     */   public byte[] norms(String f) throws IOException
/*     */   {
/* 189 */     ensureOpen();
/* 190 */     return this.in.norms(f);
/*     */   }
/*     */ 
/*     */   public void norms(String f, byte[] bytes, int offset) throws IOException
/*     */   {
/* 195 */     ensureOpen();
/* 196 */     this.in.norms(f, bytes, offset);
/*     */   }
/*     */ 
/*     */   protected void doSetNorm(int d, String f, byte b) throws CorruptIndexException, IOException
/*     */   {
/* 201 */     this.in.setNorm(d, f, b);
/*     */   }
/*     */ 
/*     */   public TermEnum terms() throws IOException
/*     */   {
/* 206 */     ensureOpen();
/* 207 */     return this.in.terms();
/*     */   }
/*     */ 
/*     */   public TermEnum terms(Term t) throws IOException
/*     */   {
/* 212 */     ensureOpen();
/* 213 */     return this.in.terms(t);
/*     */   }
/*     */ 
/*     */   public int docFreq(Term t) throws IOException
/*     */   {
/* 218 */     ensureOpen();
/* 219 */     return this.in.docFreq(t);
/*     */   }
/*     */ 
/*     */   public TermDocs termDocs() throws IOException
/*     */   {
/* 224 */     ensureOpen();
/* 225 */     return this.in.termDocs();
/*     */   }
/*     */ 
/*     */   public TermDocs termDocs(Term term) throws IOException
/*     */   {
/* 230 */     ensureOpen();
/* 231 */     return this.in.termDocs(term);
/*     */   }
/*     */ 
/*     */   public TermPositions termPositions() throws IOException
/*     */   {
/* 236 */     ensureOpen();
/* 237 */     return this.in.termPositions();
/*     */   }
/*     */ 
/*     */   protected void doDelete(int n) throws CorruptIndexException, IOException {
/* 241 */     this.in.deleteDocument(n);
/*     */   }
/*     */   protected void doCommit(Map<String, String> commitUserData) throws IOException {
/* 244 */     this.in.commit(commitUserData);
/*     */   }
/*     */ 
/*     */   protected void doClose() throws IOException {
/* 248 */     this.in.close();
/*     */   }
/*     */ 
/*     */   public Collection<String> getFieldNames(IndexReader.FieldOption fieldNames)
/*     */   {
/* 254 */     ensureOpen();
/* 255 */     return this.in.getFieldNames(fieldNames);
/*     */   }
/*     */ 
/*     */   public long getVersion()
/*     */   {
/* 260 */     ensureOpen();
/* 261 */     return this.in.getVersion();
/*     */   }
/*     */ 
/*     */   public boolean isCurrent() throws CorruptIndexException, IOException
/*     */   {
/* 266 */     ensureOpen();
/* 267 */     return this.in.isCurrent();
/*     */   }
/*     */ 
/*     */   public boolean isOptimized()
/*     */   {
/* 272 */     ensureOpen();
/* 273 */     return this.in.isOptimized();
/*     */   }
/*     */ 
/*     */   public IndexReader[] getSequentialSubReaders()
/*     */   {
/* 278 */     return this.in.getSequentialSubReaders();
/*     */   }
/*     */ 
/*     */   public Object getCoreCacheKey()
/*     */   {
/* 286 */     return this.in.getCoreCacheKey();
/*     */   }
/*     */ 
/*     */   public Object getDeletesCacheKey()
/*     */   {
/* 294 */     return this.in.getDeletesCacheKey();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 300 */     StringBuilder buffer = new StringBuilder("FilterReader(");
/* 301 */     buffer.append(this.in);
/* 302 */     buffer.append(')');
/* 303 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public void addReaderFinishedListener(IndexReader.ReaderFinishedListener listener)
/*     */   {
/* 308 */     super.addReaderFinishedListener(listener);
/* 309 */     this.in.addReaderFinishedListener(listener);
/*     */   }
/*     */ 
/*     */   public void removeReaderFinishedListener(IndexReader.ReaderFinishedListener listener)
/*     */   {
/* 314 */     super.removeReaderFinishedListener(listener);
/* 315 */     this.in.removeReaderFinishedListener(listener);
/*     */   }
/*     */ 
/*     */   public static class FilterTermEnum extends TermEnum
/*     */   {
/*     */     protected TermEnum in;
/*     */ 
/*     */     public FilterTermEnum(TermEnum in)
/*     */     {
/*  88 */       this.in = in;
/*     */     }
/*     */     public boolean next() throws IOException {
/*  91 */       return this.in.next();
/*     */     }
/*  93 */     public Term term() { return this.in.term(); } 
/*     */     public int docFreq() {
/*  95 */       return this.in.docFreq();
/*     */     }
/*  97 */     public void close() throws IOException { this.in.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class FilterTermPositions extends FilterIndexReader.FilterTermDocs
/*     */     implements TermPositions
/*     */   {
/*     */     public FilterTermPositions(TermPositions in)
/*     */     {
/*  63 */       super();
/*     */     }
/*     */     public int nextPosition() throws IOException {
/*  66 */       return ((TermPositions)this.in).nextPosition();
/*     */     }
/*     */ 
/*     */     public int getPayloadLength() {
/*  70 */       return ((TermPositions)this.in).getPayloadLength();
/*     */     }
/*     */ 
/*     */     public byte[] getPayload(byte[] data, int offset) throws IOException {
/*  74 */       return ((TermPositions)this.in).getPayload(data, offset);
/*     */     }
/*     */ 
/*     */     public boolean isPayloadAvailable()
/*     */     {
/*  80 */       return ((TermPositions)this.in).isPayloadAvailable();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class FilterTermDocs
/*     */     implements TermDocs
/*     */   {
/*     */     protected TermDocs in;
/*     */ 
/*     */     public FilterTermDocs(TermDocs in)
/*     */     {
/*  45 */       this.in = in;
/*     */     }
/*  47 */     public void seek(Term term) throws IOException { this.in.seek(term); } 
/*  48 */     public void seek(TermEnum termEnum) throws IOException { this.in.seek(termEnum); } 
/*  49 */     public int doc() { return this.in.doc(); } 
/*  50 */     public int freq() { return this.in.freq(); } 
/*  51 */     public boolean next() throws IOException { return this.in.next(); } 
/*     */     public int read(int[] docs, int[] freqs) throws IOException {
/*  53 */       return this.in.read(docs, freqs);
/*     */     }
/*  55 */     public boolean skipTo(int i) throws IOException { return this.in.skipTo(i); } 
/*  56 */     public void close() throws IOException { this.in.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FilterIndexReader
 * JD-Core Version:    0.6.0
 */