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
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.FieldSelector;
/*     */ import org.apache.lucene.document.FieldSelectorResult;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.util.MapBackedSet;
/*     */ 
/*     */ public class ParallelReader extends IndexReader
/*     */ {
/*  49 */   private List<IndexReader> readers = new ArrayList();
/*  50 */   private List<Boolean> decrefOnClose = new ArrayList();
/*  51 */   boolean incRefReaders = false;
/*  52 */   private SortedMap<String, IndexReader> fieldToReader = new TreeMap();
/*  53 */   private Map<IndexReader, Collection<String>> readerToFields = new HashMap();
/*  54 */   private List<IndexReader> storedFieldReaders = new ArrayList();
/*     */   private int maxDoc;
/*     */   private int numDocs;
/*     */   private boolean hasDeletions;
/*     */ 
/*     */   public ParallelReader()
/*     */     throws IOException
/*     */   {
/*  63 */     this(true);
/*     */   }
/*     */ 
/*     */   public ParallelReader(boolean closeSubReaders)
/*     */     throws IOException
/*     */   {
/*  71 */     this.incRefReaders = (!closeSubReaders);
/*  72 */     this.readerFinishedListeners = new MapBackedSet(new ConcurrentHashMap());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  78 */     StringBuilder buffer = new StringBuilder("ParallelReader(");
/*  79 */     Iterator iter = this.readers.iterator();
/*  80 */     if (iter.hasNext()) {
/*  81 */       buffer.append(iter.next());
/*     */     }
/*  83 */     while (iter.hasNext()) {
/*  84 */       buffer.append(", ").append(iter.next());
/*     */     }
/*  86 */     buffer.append(')');
/*  87 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public void add(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  94 */     ensureOpen();
/*  95 */     add(reader, false);
/*     */   }
/*     */ 
/*     */   public void add(IndexReader reader, boolean ignoreStoredFields)
/*     */     throws IOException
/*     */   {
/* 111 */     ensureOpen();
/* 112 */     if (this.readers.size() == 0) {
/* 113 */       this.maxDoc = reader.maxDoc();
/* 114 */       this.numDocs = reader.numDocs();
/* 115 */       this.hasDeletions = reader.hasDeletions();
/*     */     }
/*     */ 
/* 118 */     if (reader.maxDoc() != this.maxDoc) {
/* 119 */       throw new IllegalArgumentException("All readers must have same maxDoc: " + this.maxDoc + "!=" + reader.maxDoc());
/*     */     }
/* 121 */     if (reader.numDocs() != this.numDocs) {
/* 122 */       throw new IllegalArgumentException("All readers must have same numDocs: " + this.numDocs + "!=" + reader.numDocs());
/*     */     }
/*     */ 
/* 125 */     Collection fields = reader.getFieldNames(IndexReader.FieldOption.ALL);
/* 126 */     this.readerToFields.put(reader, fields);
/* 127 */     for (String field : fields) {
/* 128 */       if (this.fieldToReader.get(field) == null) {
/* 129 */         this.fieldToReader.put(field, reader);
/*     */       }
/*     */     }
/* 132 */     if (!ignoreStoredFields)
/* 133 */       this.storedFieldReaders.add(reader);
/* 134 */     this.readers.add(reader);
/*     */ 
/* 136 */     if (this.incRefReaders) {
/* 137 */       reader.incRef();
/*     */     }
/* 139 */     this.decrefOnClose.add(Boolean.valueOf(this.incRefReaders));
/*     */   }
/*     */ 
/*     */   public synchronized Object clone()
/*     */   {
/*     */     try {
/* 145 */       return doReopen(true); } catch (Exception ex) {
/*     */     }
/* 147 */     throw new RuntimeException(ex);
/*     */   }
/*     */ 
/*     */   public synchronized IndexReader reopen()
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 172 */     return doReopen(false);
/*     */   }
/*     */ 
/*     */   protected IndexReader doReopen(boolean doClone) throws CorruptIndexException, IOException {
/* 176 */     ensureOpen();
/*     */ 
/* 178 */     boolean reopened = false;
/* 179 */     List newReaders = new ArrayList();
/*     */ 
/* 181 */     boolean success = false;
/*     */     try
/*     */     {
/* 184 */       for (IndexReader oldReader : this.readers) {
/* 185 */         IndexReader newReader = null;
/* 186 */         if (doClone)
/* 187 */           newReader = (IndexReader)oldReader.clone();
/*     */         else {
/* 189 */           newReader = oldReader.reopen();
/*     */         }
/* 191 */         newReaders.add(newReader);
/*     */ 
/* 194 */         if (newReader != oldReader) {
/* 195 */           reopened = true;
/*     */         }
/*     */       }
/* 198 */       success = true;
/*     */     } finally {
/* 200 */       if ((!success) && (reopened)) {
/* 201 */         for (int i = 0; i < newReaders.size(); i++) {
/* 202 */           IndexReader r = (IndexReader)newReaders.get(i);
/* 203 */           if (r == this.readers.get(i)) continue;
/*     */           try {
/* 205 */             r.close();
/*     */           }
/*     */           catch (IOException ignore)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 214 */     if (reopened) {
/* 215 */       List newDecrefOnClose = new ArrayList();
/* 216 */       ParallelReader pr = new ParallelReader();
/* 217 */       for (int i = 0; i < this.readers.size(); i++) {
/* 218 */         IndexReader oldReader = (IndexReader)this.readers.get(i);
/* 219 */         IndexReader newReader = (IndexReader)newReaders.get(i);
/* 220 */         if (newReader == oldReader) {
/* 221 */           newDecrefOnClose.add(Boolean.TRUE);
/* 222 */           newReader.incRef();
/*     */         }
/*     */         else
/*     */         {
/* 226 */           newDecrefOnClose.add(Boolean.FALSE);
/*     */         }
/* 228 */         pr.add(newReader, !this.storedFieldReaders.contains(oldReader));
/*     */       }
/* 230 */       pr.decrefOnClose = newDecrefOnClose;
/* 231 */       pr.incRefReaders = this.incRefReaders;
/* 232 */       return pr;
/*     */     }
/*     */ 
/* 235 */     return this;
/*     */   }
/*     */ 
/*     */   public int numDocs()
/*     */   {
/* 243 */     return this.numDocs;
/*     */   }
/*     */ 
/*     */   public int maxDoc()
/*     */   {
/* 249 */     return this.maxDoc;
/*     */   }
/*     */ 
/*     */   public boolean hasDeletions()
/*     */   {
/* 255 */     return this.hasDeletions;
/*     */   }
/*     */ 
/*     */   public boolean isDeleted(int n)
/*     */   {
/* 262 */     if (this.readers.size() > 0)
/* 263 */       return ((IndexReader)this.readers.get(0)).isDeleted(n);
/* 264 */     return false;
/*     */   }
/*     */ 
/*     */   protected void doDelete(int n)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 270 */     for (IndexReader reader : this.readers) {
/* 271 */       reader.deleteDocument(n);
/*     */     }
/* 273 */     this.hasDeletions = true;
/*     */   }
/*     */ 
/*     */   protected void doUndeleteAll()
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 279 */     for (IndexReader reader : this.readers) {
/* 280 */       reader.undeleteAll();
/*     */     }
/* 282 */     this.hasDeletions = false;
/*     */   }
/*     */ 
/*     */   public Document document(int n, FieldSelector fieldSelector)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 288 */     ensureOpen();
/* 289 */     Document result = new Document();
/* 290 */     for (IndexReader reader : this.storedFieldReaders)
/*     */     {
/* 292 */       boolean include = fieldSelector == null;
/* 293 */       if (!include) {
/* 294 */         Collection fields = (Collection)this.readerToFields.get(reader);
/* 295 */         for (String field : fields)
/* 296 */           if (fieldSelector.accept(field) != FieldSelectorResult.NO_LOAD) {
/* 297 */             include = true;
/* 298 */             break;
/*     */           }
/*     */       }
/* 301 */       if (include) {
/* 302 */         List fields = reader.document(n, fieldSelector).getFields();
/* 303 */         for (Fieldable field : fields) {
/* 304 */           result.add(field);
/*     */         }
/*     */       }
/*     */     }
/* 308 */     return result;
/*     */   }
/*     */ 
/*     */   public TermFreqVector[] getTermFreqVectors(int n)
/*     */     throws IOException
/*     */   {
/* 314 */     ensureOpen();
/* 315 */     ArrayList results = new ArrayList();
/* 316 */     for (Map.Entry e : this.fieldToReader.entrySet())
/*     */     {
/* 318 */       String field = (String)e.getKey();
/* 319 */       IndexReader reader = (IndexReader)e.getValue();
/* 320 */       TermFreqVector vector = reader.getTermFreqVector(n, field);
/* 321 */       if (vector != null)
/* 322 */         results.add(vector);
/*     */     }
/* 324 */     return (TermFreqVector[])results.toArray(new TermFreqVector[results.size()]);
/*     */   }
/*     */ 
/*     */   public TermFreqVector getTermFreqVector(int n, String field)
/*     */     throws IOException
/*     */   {
/* 330 */     ensureOpen();
/* 331 */     IndexReader reader = (IndexReader)this.fieldToReader.get(field);
/* 332 */     return reader == null ? null : reader.getTermFreqVector(n, field);
/*     */   }
/*     */ 
/*     */   public void getTermFreqVector(int docNumber, String field, TermVectorMapper mapper)
/*     */     throws IOException
/*     */   {
/* 338 */     ensureOpen();
/* 339 */     IndexReader reader = (IndexReader)this.fieldToReader.get(field);
/* 340 */     if (reader != null)
/* 341 */       reader.getTermFreqVector(docNumber, field, mapper);
/*     */   }
/*     */ 
/*     */   public void getTermFreqVector(int docNumber, TermVectorMapper mapper)
/*     */     throws IOException
/*     */   {
/* 347 */     ensureOpen();
/*     */ 
/* 349 */     for (Map.Entry e : this.fieldToReader.entrySet())
/*     */     {
/* 351 */       String field = (String)e.getKey();
/* 352 */       IndexReader reader = (IndexReader)e.getValue();
/* 353 */       reader.getTermFreqVector(docNumber, field, mapper);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasNorms(String field)
/*     */     throws IOException
/*     */   {
/* 360 */     ensureOpen();
/* 361 */     IndexReader reader = (IndexReader)this.fieldToReader.get(field);
/* 362 */     return reader == null ? false : reader.hasNorms(field);
/*     */   }
/*     */ 
/*     */   public byte[] norms(String field) throws IOException
/*     */   {
/* 367 */     ensureOpen();
/* 368 */     IndexReader reader = (IndexReader)this.fieldToReader.get(field);
/* 369 */     return reader == null ? null : reader.norms(field);
/*     */   }
/*     */ 
/*     */   public void norms(String field, byte[] result, int offset)
/*     */     throws IOException
/*     */   {
/* 375 */     ensureOpen();
/* 376 */     IndexReader reader = (IndexReader)this.fieldToReader.get(field);
/* 377 */     if (reader != null)
/* 378 */       reader.norms(field, result, offset);
/*     */   }
/*     */ 
/*     */   protected void doSetNorm(int n, String field, byte value)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 384 */     IndexReader reader = (IndexReader)this.fieldToReader.get(field);
/* 385 */     if (reader != null)
/* 386 */       reader.doSetNorm(n, field, value);
/*     */   }
/*     */ 
/*     */   public TermEnum terms() throws IOException
/*     */   {
/* 391 */     ensureOpen();
/* 392 */     return new ParallelTermEnum();
/*     */   }
/*     */ 
/*     */   public TermEnum terms(Term term) throws IOException
/*     */   {
/* 397 */     ensureOpen();
/* 398 */     return new ParallelTermEnum(term);
/*     */   }
/*     */ 
/*     */   public int docFreq(Term term) throws IOException
/*     */   {
/* 403 */     ensureOpen();
/* 404 */     IndexReader reader = (IndexReader)this.fieldToReader.get(term.field());
/* 405 */     return reader == null ? 0 : reader.docFreq(term);
/*     */   }
/*     */ 
/*     */   public TermDocs termDocs(Term term) throws IOException
/*     */   {
/* 410 */     ensureOpen();
/* 411 */     return new ParallelTermDocs(term);
/*     */   }
/*     */ 
/*     */   public TermDocs termDocs() throws IOException
/*     */   {
/* 416 */     ensureOpen();
/* 417 */     return new ParallelTermDocs();
/*     */   }
/*     */ 
/*     */   public TermPositions termPositions(Term term) throws IOException
/*     */   {
/* 422 */     ensureOpen();
/* 423 */     return new ParallelTermPositions(term);
/*     */   }
/*     */ 
/*     */   public TermPositions termPositions() throws IOException
/*     */   {
/* 428 */     ensureOpen();
/* 429 */     return new ParallelTermPositions();
/*     */   }
/*     */ 
/*     */   public boolean isCurrent()
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 437 */     for (IndexReader reader : this.readers) {
/* 438 */       if (!reader.isCurrent()) {
/* 439 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 444 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isOptimized()
/*     */   {
/* 452 */     for (IndexReader reader : this.readers) {
/* 453 */       if (!reader.isOptimized()) {
/* 454 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 459 */     return true;
/*     */   }
/*     */ 
/*     */   public long getVersion()
/*     */   {
/* 468 */     throw new UnsupportedOperationException("ParallelReader does not support this method.");
/*     */   }
/*     */ 
/*     */   IndexReader[] getSubReaders()
/*     */   {
/* 473 */     return (IndexReader[])this.readers.toArray(new IndexReader[this.readers.size()]);
/*     */   }
/*     */ 
/*     */   protected void doCommit(Map<String, String> commitUserData) throws IOException
/*     */   {
/* 478 */     for (IndexReader reader : this.readers)
/* 479 */       reader.commit(commitUserData);
/*     */   }
/*     */ 
/*     */   protected synchronized void doClose() throws IOException
/*     */   {
/* 484 */     for (int i = 0; i < this.readers.size(); i++)
/* 485 */       if (((Boolean)this.decrefOnClose.get(i)).booleanValue())
/* 486 */         ((IndexReader)this.readers.get(i)).decRef();
/*     */       else
/* 488 */         ((IndexReader)this.readers.get(i)).close();
/*     */   }
/*     */ 
/*     */   public Collection<String> getFieldNames(IndexReader.FieldOption fieldNames)
/*     */   {
/* 495 */     ensureOpen();
/* 496 */     Set fieldSet = new HashSet();
/* 497 */     for (IndexReader reader : this.readers) {
/* 498 */       Collection names = reader.getFieldNames(fieldNames);
/* 499 */       fieldSet.addAll(names);
/*     */     }
/* 501 */     return fieldSet;
/*     */   }
/*     */ 
/*     */   public void addReaderFinishedListener(IndexReader.ReaderFinishedListener listener)
/*     */   {
/* 666 */     super.addReaderFinishedListener(listener);
/* 667 */     for (IndexReader reader : this.readers)
/* 668 */       reader.addReaderFinishedListener(listener);
/*     */   }
/*     */ 
/*     */   public void removeReaderFinishedListener(IndexReader.ReaderFinishedListener listener)
/*     */   {
/* 674 */     super.removeReaderFinishedListener(listener);
/* 675 */     for (IndexReader reader : this.readers)
/* 676 */       reader.removeReaderFinishedListener(listener);
/*     */   }
/*     */ 
/*     */   private class ParallelTermPositions extends ParallelReader.ParallelTermDocs
/*     */     implements TermPositions
/*     */   {
/*     */     public ParallelTermPositions()
/*     */     {
/* 635 */       super(); } 
/* 636 */     public ParallelTermPositions(Term term) throws IOException { super(); seek(term); }
/*     */ 
/*     */     public void seek(Term term) throws IOException
/*     */     {
/* 640 */       IndexReader reader = (IndexReader)ParallelReader.this.fieldToReader.get(term.field());
/* 641 */       this.termDocs = (reader != null ? reader.termPositions(term) : null);
/*     */     }
/*     */ 
/*     */     public int nextPosition() throws IOException
/*     */     {
/* 646 */       return ((TermPositions)this.termDocs).nextPosition();
/*     */     }
/*     */ 
/*     */     public int getPayloadLength() {
/* 650 */       return ((TermPositions)this.termDocs).getPayloadLength();
/*     */     }
/*     */ 
/*     */     public byte[] getPayload(byte[] data, int offset) throws IOException {
/* 654 */       return ((TermPositions)this.termDocs).getPayload(data, offset);
/*     */     }
/*     */ 
/*     */     public boolean isPayloadAvailable()
/*     */     {
/* 660 */       return ((TermPositions)this.termDocs).isPayloadAvailable();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ParallelTermDocs
/*     */     implements TermDocs
/*     */   {
/*     */     protected TermDocs termDocs;
/*     */ 
/*     */     public ParallelTermDocs()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ParallelTermDocs(Term term)
/*     */       throws IOException
/*     */     {
/* 586 */       if (term == null)
/* 587 */         this.termDocs = (ParallelReader.this.readers.isEmpty() ? null : ((IndexReader)ParallelReader.this.readers.get(0)).termDocs(null));
/*     */       else
/* 589 */         seek(term); 
/*     */     }
/*     */ 
/*     */     public int doc() {
/* 592 */       return this.termDocs.doc(); } 
/* 593 */     public int freq() { return this.termDocs.freq(); }
/*     */ 
/*     */     public void seek(Term term) throws IOException {
/* 596 */       IndexReader reader = (IndexReader)ParallelReader.this.fieldToReader.get(term.field());
/* 597 */       this.termDocs = (reader != null ? reader.termDocs(term) : null);
/*     */     }
/*     */ 
/*     */     public void seek(TermEnum termEnum) throws IOException {
/* 601 */       seek(termEnum.term());
/*     */     }
/*     */ 
/*     */     public boolean next() throws IOException {
/* 605 */       if (this.termDocs == null) {
/* 606 */         return false;
/*     */       }
/* 608 */       return this.termDocs.next();
/*     */     }
/*     */ 
/*     */     public int read(int[] docs, int[] freqs) throws IOException {
/* 612 */       if (this.termDocs == null) {
/* 613 */         return 0;
/*     */       }
/* 615 */       return this.termDocs.read(docs, freqs);
/*     */     }
/*     */ 
/*     */     public boolean skipTo(int target) throws IOException {
/* 619 */       if (this.termDocs == null) {
/* 620 */         return false;
/*     */       }
/* 622 */       return this.termDocs.skipTo(target);
/*     */     }
/*     */ 
/*     */     public void close() throws IOException {
/* 626 */       if (this.termDocs != null)
/* 627 */         this.termDocs.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ParallelTermEnum extends TermEnum
/*     */   {
/*     */     private String field;
/*     */     private Iterator<String> fieldIterator;
/*     */     private TermEnum termEnum;
/*     */ 
/*     */     public ParallelTermEnum()
/*     */       throws IOException
/*     */     {
/*     */       try
/*     */       {
/* 511 */         this.field = ((String)ParallelReader.this.fieldToReader.firstKey());
/*     */       }
/*     */       catch (NoSuchElementException e) {
/* 514 */         return;
/*     */       }
/* 516 */       if (this.field != null)
/* 517 */         this.termEnum = ((IndexReader)ParallelReader.this.fieldToReader.get(this.field)).terms();
/*     */     }
/*     */ 
/*     */     public ParallelTermEnum(Term term) throws IOException {
/* 521 */       this.field = term.field();
/* 522 */       IndexReader reader = (IndexReader)ParallelReader.this.fieldToReader.get(this.field);
/* 523 */       if (reader != null)
/* 524 */         this.termEnum = reader.terms(term);
/*     */     }
/*     */ 
/*     */     public boolean next() throws IOException
/*     */     {
/* 529 */       if (this.termEnum == null) {
/* 530 */         return false;
/*     */       }
/*     */ 
/* 533 */       if ((this.termEnum.next()) && (this.termEnum.term().field() == this.field)) {
/* 534 */         return true;
/*     */       }
/* 536 */       this.termEnum.close();
/*     */ 
/* 539 */       if (this.fieldIterator == null) {
/* 540 */         this.fieldIterator = ParallelReader.this.fieldToReader.tailMap(this.field).keySet().iterator();
/* 541 */         this.fieldIterator.next();
/*     */       }
/* 543 */       while (this.fieldIterator.hasNext()) {
/* 544 */         this.field = ((String)this.fieldIterator.next());
/* 545 */         this.termEnum = ((IndexReader)ParallelReader.this.fieldToReader.get(this.field)).terms(new Term(this.field));
/* 546 */         Term term = this.termEnum.term();
/* 547 */         if ((term != null) && (term.field() == this.field)) {
/* 548 */           return true;
/*     */         }
/* 550 */         this.termEnum.close();
/*     */       }
/*     */ 
/* 553 */       return false;
/*     */     }
/*     */ 
/*     */     public Term term()
/*     */     {
/* 558 */       if (this.termEnum == null) {
/* 559 */         return null;
/*     */       }
/* 561 */       return this.termEnum.term();
/*     */     }
/*     */ 
/*     */     public int docFreq()
/*     */     {
/* 566 */       if (this.termEnum == null) {
/* 567 */         return 0;
/*     */       }
/* 569 */       return this.termEnum.docFreq();
/*     */     }
/*     */ 
/*     */     public void close() throws IOException
/*     */     {
/* 574 */       if (this.termEnum != null)
/* 575 */         this.termEnum.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.ParallelReader
 * JD-Core Version:    0.6.0
 */