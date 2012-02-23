/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ import org.apache.lucene.util.ReaderUtil;
/*     */ 
/*     */ final class SegmentMerger
/*     */ {
/*     */   private Directory directory;
/*     */   private String segment;
/*  47 */   private int termIndexInterval = 128;
/*     */ 
/*  49 */   private List<IndexReader> readers = new ArrayList();
/*     */   private final FieldInfos fieldInfos;
/*     */   private int mergedDocs;
/*     */   private final CheckAbort checkAbort;
/*     */   private static final int MAX_RAW_MERGE_DOCS = 4192;
/*     */   private SegmentWriteState segmentWriteState;
/*     */   private final PayloadProcessorProvider payloadProcessorProvider;
/*     */   private SegmentReader[] matchingSegmentReaders;
/*     */   private int[] rawDocLengths;
/*     */   private int[] rawDocLengths2;
/*     */   private int matchedCount;
/* 461 */   private SegmentMergeQueue queue = null;
/*     */   FieldInfo.IndexOptions indexOptions;
/*     */   private byte[] payloadBuffer;
/*     */   private int[][] docMaps;
/*     */ 
/*     */   SegmentMerger(Directory dir, int termIndexInterval, String name, MergePolicy.OneMerge merge, PayloadProcessorProvider payloadProcessorProvider, FieldInfos fieldInfos)
/*     */   {
/*  65 */     this.payloadProcessorProvider = payloadProcessorProvider;
/*  66 */     this.directory = dir;
/*  67 */     this.fieldInfos = fieldInfos;
/*  68 */     this.segment = name;
/*  69 */     if (merge != null)
/*  70 */       this.checkAbort = new CheckAbort(merge, this.directory);
/*     */     else
/*  72 */       this.checkAbort = new CheckAbort(null, null)
/*     */       {
/*     */         public void work(double units) throws MergePolicy.MergeAbortedException
/*     */         {
/*     */         }
/*     */       };
/*  79 */     this.termIndexInterval = termIndexInterval;
/*     */   }
/*     */ 
/*     */   public FieldInfos fieldInfos() {
/*  83 */     return this.fieldInfos;
/*     */   }
/*     */ 
/*     */   final void add(IndexReader reader)
/*     */   {
/*  91 */     ReaderUtil.gatherSubReaders(this.readers, reader);
/*     */   }
/*     */ 
/*     */   final int merge()
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 108 */     this.mergedDocs = mergeFields();
/* 109 */     mergeTerms();
/* 110 */     mergeNorms();
/*     */ 
/* 112 */     if (this.fieldInfos.hasVectors()) {
/* 113 */       mergeVectors();
/*     */     }
/* 115 */     return this.mergedDocs;
/*     */   }
/*     */ 
/*     */   final Collection<String> createCompoundFile(String fileName, SegmentInfo info)
/*     */     throws IOException
/*     */   {
/* 127 */     Collection files = info.files();
/* 128 */     CompoundFileWriter cfsWriter = new CompoundFileWriter(this.directory, fileName, this.checkAbort);
/* 129 */     for (String file : files)
/*     */     {
/* 131 */       assert (!IndexFileNames.matchesExtension(file, "del")) : (".del file is not allowed in .cfs: " + file);
/*     */ 
/* 133 */       assert (!IndexFileNames.isSeparateNormsFile(file)) : ("separate norms file (.s[0-9]+) is not allowed in .cfs: " + file);
/* 134 */       cfsWriter.addFile(file);
/*     */     }
/*     */ 
/* 138 */     cfsWriter.close();
/*     */ 
/* 140 */     return files;
/*     */   }
/*     */ 
/*     */   private static void addIndexed(IndexReader reader, FieldInfos fInfos, Collection<String> names, boolean storeTermVectors, boolean storePositionWithTermVector, boolean storeOffsetWithTermVector, boolean storePayloads, FieldInfo.IndexOptions indexOptions)
/*     */     throws IOException
/*     */   {
/* 148 */     for (String field : names)
/* 149 */       fInfos.add(field, true, storeTermVectors, storePositionWithTermVector, storeOffsetWithTermVector, !reader.hasNorms(field), storePayloads, indexOptions);
/*     */   }
/*     */ 
/*     */   public int getMatchedSubReaderCount()
/*     */   {
/* 161 */     return this.matchedCount;
/*     */   }
/*     */ 
/*     */   private void setMatchingSegmentReaders()
/*     */   {
/* 168 */     int numReaders = this.readers.size();
/* 169 */     this.matchingSegmentReaders = new SegmentReader[numReaders];
/*     */ 
/* 175 */     for (int i = 0; i < numReaders; i++) {
/* 176 */       IndexReader reader = (IndexReader)this.readers.get(i);
/* 177 */       if ((reader instanceof SegmentReader)) {
/* 178 */         SegmentReader segmentReader = (SegmentReader)reader;
/* 179 */         boolean same = true;
/* 180 */         FieldInfos segmentFieldInfos = segmentReader.fieldInfos();
/* 181 */         int numFieldInfos = segmentFieldInfos.size();
/* 182 */         for (int j = 0; (same) && (j < numFieldInfos); j++) {
/* 183 */           same = this.fieldInfos.fieldName(j).equals(segmentFieldInfos.fieldName(j));
/*     */         }
/* 185 */         if (same) {
/* 186 */           this.matchingSegmentReaders[i] = segmentReader;
/* 187 */           this.matchedCount += 1;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 193 */     this.rawDocLengths = new int[4192];
/* 194 */     this.rawDocLengths2 = new int[4192];
/*     */   }
/*     */ 
/*     */   private int mergeFields()
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 205 */     for (IndexReader reader : this.readers) {
/* 206 */       if ((reader instanceof SegmentReader)) {
/* 207 */         SegmentReader segmentReader = (SegmentReader)reader;
/* 208 */         FieldInfos readerFieldInfos = segmentReader.fieldInfos();
/* 209 */         int numReaderFieldInfos = readerFieldInfos.size();
/* 210 */         for (int j = 0; j < numReaderFieldInfos; j++)
/* 211 */           this.fieldInfos.add(readerFieldInfos.fieldInfo(j));
/*     */       }
/*     */       else {
/* 214 */         addIndexed(reader, this.fieldInfos, reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR_WITH_POSITION_OFFSET), true, true, true, false, FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
/* 215 */         addIndexed(reader, this.fieldInfos, reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR_WITH_POSITION), true, true, false, false, FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
/* 216 */         addIndexed(reader, this.fieldInfos, reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR_WITH_OFFSET), true, false, true, false, FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
/* 217 */         addIndexed(reader, this.fieldInfos, reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR), true, false, false, false, FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
/* 218 */         addIndexed(reader, this.fieldInfos, reader.getFieldNames(IndexReader.FieldOption.OMIT_POSITIONS), false, false, false, false, FieldInfo.IndexOptions.DOCS_AND_FREQS);
/* 219 */         addIndexed(reader, this.fieldInfos, reader.getFieldNames(IndexReader.FieldOption.OMIT_TERM_FREQ_AND_POSITIONS), false, false, false, false, FieldInfo.IndexOptions.DOCS_ONLY);
/* 220 */         addIndexed(reader, this.fieldInfos, reader.getFieldNames(IndexReader.FieldOption.STORES_PAYLOADS), false, false, false, true, FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
/* 221 */         addIndexed(reader, this.fieldInfos, reader.getFieldNames(IndexReader.FieldOption.INDEXED), false, false, false, false, FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
/* 222 */         this.fieldInfos.add(reader.getFieldNames(IndexReader.FieldOption.UNINDEXED), false);
/*     */       }
/*     */     }
/* 225 */     this.fieldInfos.write(this.directory, this.segment + ".fnm");
/*     */ 
/* 227 */     int docCount = 0;
/*     */ 
/* 229 */     setMatchingSegmentReaders();
/*     */ 
/* 231 */     FieldsWriter fieldsWriter = new FieldsWriter(this.directory, this.segment, this.fieldInfos);
/*     */     try
/*     */     {
/* 234 */       idx = 0;
/* 235 */       for (IndexReader reader : this.readers) {
/* 236 */         SegmentReader matchingSegmentReader = this.matchingSegmentReaders[(idx++)];
/* 237 */         FieldsReader matchingFieldsReader = null;
/* 238 */         if (matchingSegmentReader != null) {
/* 239 */           FieldsReader fieldsReader = matchingSegmentReader.getFieldsReader();
/* 240 */           if ((fieldsReader != null) && (fieldsReader.canReadRawDocs())) {
/* 241 */             matchingFieldsReader = fieldsReader;
/*     */           }
/*     */         }
/* 244 */         if (reader.hasDeletions()) {
/* 245 */           docCount += copyFieldsWithDeletions(fieldsWriter, reader, matchingFieldsReader);
/*     */         }
/*     */         else
/* 248 */           docCount += copyFieldsNoDeletions(fieldsWriter, reader, matchingFieldsReader);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       int idx;
/* 253 */       fieldsWriter.close();
/*     */     }
/*     */ 
/* 256 */     String fileName = IndexFileNames.segmentFileName(this.segment, "fdx");
/* 257 */     long fdxFileLength = this.directory.fileLength(fileName);
/*     */ 
/* 259 */     if (4L + docCount * 8L != fdxFileLength)
/*     */     {
/* 265 */       throw new RuntimeException("mergeFields produced an invalid result: docCount is " + docCount + " but fdx file size is " + fdxFileLength + " file=" + fileName + " file exists?=" + this.directory.fileExists(fileName) + "; now aborting this merge to prevent index corruption");
/*     */     }
/* 267 */     this.segmentWriteState = new SegmentWriteState(null, this.directory, this.segment, this.fieldInfos, docCount, this.termIndexInterval, null);
/* 268 */     return docCount;
/*     */   }
/*     */ 
/*     */   private int copyFieldsWithDeletions(FieldsWriter fieldsWriter, IndexReader reader, FieldsReader matchingFieldsReader)
/*     */     throws IOException, MergePolicy.MergeAbortedException, CorruptIndexException
/*     */   {
/* 274 */     int docCount = 0;
/* 275 */     int maxDoc = reader.maxDoc();
/*     */     int j;
/* 276 */     if (matchingFieldsReader != null)
/*     */     {
/* 278 */       for (j = 0; j < maxDoc; ) {
/* 279 */         if (reader.isDeleted(j))
/*     */         {
/* 281 */           j++;
/* 282 */           continue;
/*     */         }
/*     */ 
/* 286 */         int start = j; int numDocs = 0;
/*     */         do {
/* 288 */           j++;
/* 289 */           numDocs++;
/* 290 */           if (j >= maxDoc) break;
/* 291 */           if (reader.isDeleted(j)) {
/* 292 */             j++;
/* 293 */             break;
/*     */           }
/*     */         }
/* 295 */         while (numDocs < 4192);
/*     */ 
/* 297 */         IndexInput stream = matchingFieldsReader.rawDocs(this.rawDocLengths, start, numDocs);
/* 298 */         fieldsWriter.addRawDocuments(stream, this.rawDocLengths, numDocs);
/* 299 */         docCount += numDocs;
/* 300 */         this.checkAbort.work(300 * numDocs);
/*     */       }
/*     */     }
/* 303 */     else for (int j = 0; j < maxDoc; j++) {
/* 304 */         if (reader.isDeleted(j))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 310 */         Document doc = reader.document(j);
/* 311 */         fieldsWriter.addDocument(doc);
/* 312 */         docCount++;
/* 313 */         this.checkAbort.work(300.0D);
/*     */       }
/*     */ 
/* 316 */     return docCount;
/*     */   }
/*     */ 
/*     */   private int copyFieldsNoDeletions(FieldsWriter fieldsWriter, IndexReader reader, FieldsReader matchingFieldsReader)
/*     */     throws IOException, MergePolicy.MergeAbortedException, CorruptIndexException
/*     */   {
/* 322 */     int maxDoc = reader.maxDoc();
/* 323 */     int docCount = 0;
/* 324 */     if (matchingFieldsReader != null)
/*     */     {
/* 326 */       while (docCount < maxDoc) {
/* 327 */         int len = Math.min(4192, maxDoc - docCount);
/* 328 */         IndexInput stream = matchingFieldsReader.rawDocs(this.rawDocLengths, docCount, len);
/* 329 */         fieldsWriter.addRawDocuments(stream, this.rawDocLengths, len);
/* 330 */         docCount += len;
/* 331 */         this.checkAbort.work(300 * len);
/*     */       }
/*     */     }
/* 334 */     for (; docCount < maxDoc; docCount++)
/*     */     {
/* 337 */       Document doc = reader.document(docCount);
/* 338 */       fieldsWriter.addDocument(doc);
/* 339 */       this.checkAbort.work(300.0D);
/*     */     }
/*     */ 
/* 342 */     return docCount;
/*     */   }
/*     */ 
/*     */   private final void mergeVectors()
/*     */     throws IOException
/*     */   {
/* 350 */     TermVectorsWriter termVectorsWriter = new TermVectorsWriter(this.directory, this.segment, this.fieldInfos);
/*     */     try
/*     */     {
/* 354 */       idx = 0;
/* 355 */       for (IndexReader reader : this.readers) {
/* 356 */         SegmentReader matchingSegmentReader = this.matchingSegmentReaders[(idx++)];
/* 357 */         TermVectorsReader matchingVectorsReader = null;
/* 358 */         if (matchingSegmentReader != null) {
/* 359 */           TermVectorsReader vectorsReader = matchingSegmentReader.getTermVectorsReader();
/*     */ 
/* 362 */           if ((vectorsReader != null) && (vectorsReader.canReadRawDocs())) {
/* 363 */             matchingVectorsReader = vectorsReader;
/*     */           }
/*     */         }
/* 366 */         if (reader.hasDeletions())
/* 367 */           copyVectorsWithDeletions(termVectorsWriter, matchingVectorsReader, reader);
/*     */         else
/* 369 */           copyVectorsNoDeletions(termVectorsWriter, matchingVectorsReader, reader);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       int idx;
/* 374 */       termVectorsWriter.close();
/*     */     }
/*     */ 
/* 377 */     String fileName = IndexFileNames.segmentFileName(this.segment, "tvx");
/* 378 */     long tvxSize = this.directory.fileLength(fileName);
/*     */ 
/* 380 */     if (4L + this.mergedDocs * 16L != tvxSize)
/*     */     {
/* 386 */       throw new RuntimeException("mergeVectors produced an invalid result: mergedDocs is " + this.mergedDocs + " but tvx size is " + tvxSize + " file=" + fileName + " file exists?=" + this.directory.fileExists(fileName) + "; now aborting this merge to prevent index corruption");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void copyVectorsWithDeletions(TermVectorsWriter termVectorsWriter, TermVectorsReader matchingVectorsReader, IndexReader reader)
/*     */     throws IOException, MergePolicy.MergeAbortedException
/*     */   {
/* 393 */     int maxDoc = reader.maxDoc();
/*     */     int docNum;
/* 394 */     if (matchingVectorsReader != null)
/*     */     {
/* 396 */       for (docNum = 0; docNum < maxDoc; ) {
/* 397 */         if (reader.isDeleted(docNum))
/*     */         {
/* 399 */           docNum++;
/* 400 */           continue;
/*     */         }
/*     */ 
/* 404 */         int start = docNum; int numDocs = 0;
/*     */         do {
/* 406 */           docNum++;
/* 407 */           numDocs++;
/* 408 */           if (docNum >= maxDoc) break;
/* 409 */           if (reader.isDeleted(docNum)) {
/* 410 */             docNum++;
/* 411 */             break;
/*     */           }
/*     */         }
/* 413 */         while (numDocs < 4192);
/*     */ 
/* 415 */         matchingVectorsReader.rawDocs(this.rawDocLengths, this.rawDocLengths2, start, numDocs);
/* 416 */         termVectorsWriter.addRawDocuments(matchingVectorsReader, this.rawDocLengths, this.rawDocLengths2, numDocs);
/* 417 */         this.checkAbort.work(300 * numDocs);
/*     */       }
/*     */     }
/* 420 */     else for (int docNum = 0; docNum < maxDoc; docNum++) {
/* 421 */         if (reader.isDeleted(docNum))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 428 */         TermFreqVector[] vectors = reader.getTermFreqVectors(docNum);
/* 429 */         termVectorsWriter.addAllDocVectors(vectors);
/* 430 */         this.checkAbort.work(300.0D);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void copyVectorsNoDeletions(TermVectorsWriter termVectorsWriter, TermVectorsReader matchingVectorsReader, IndexReader reader)
/*     */     throws IOException, MergePolicy.MergeAbortedException
/*     */   {
/* 439 */     int maxDoc = reader.maxDoc();
/* 440 */     if (matchingVectorsReader != null)
/*     */     {
/* 442 */       int docCount = 0;
/* 443 */       while (docCount < maxDoc) {
/* 444 */         int len = Math.min(4192, maxDoc - docCount);
/* 445 */         matchingVectorsReader.rawDocs(this.rawDocLengths, this.rawDocLengths2, docCount, len);
/* 446 */         termVectorsWriter.addRawDocuments(matchingVectorsReader, this.rawDocLengths, this.rawDocLengths2, len);
/* 447 */         docCount += len;
/* 448 */         this.checkAbort.work(300 * len);
/*     */       }
/*     */     } else {
/* 451 */       for (int docNum = 0; docNum < maxDoc; docNum++)
/*     */       {
/* 454 */         TermFreqVector[] vectors = reader.getTermFreqVectors(docNum);
/* 455 */         termVectorsWriter.addAllDocVectors(vectors);
/* 456 */         this.checkAbort.work(300.0D);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void mergeTerms()
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 465 */     FormatPostingsFieldsConsumer fieldsConsumer = new FormatPostingsFieldsWriter(this.segmentWriteState, this.fieldInfos);
/*     */     try
/*     */     {
/* 468 */       this.queue = new SegmentMergeQueue(this.readers.size());
/*     */ 
/* 470 */       mergeTermInfos(fieldsConsumer);
/*     */     }
/*     */     finally
/*     */     {
/*     */     }
/*     */ 
/* 477 */     ret;
/*     */   }
/*     */ 
/*     */   private final void mergeTermInfos(FormatPostingsFieldsConsumer consumer)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 486 */     int base = 0;
/* 487 */     int readerCount = this.readers.size();
/* 488 */     for (int i = 0; i < readerCount; i++) {
/* 489 */       IndexReader reader = (IndexReader)this.readers.get(i);
/* 490 */       TermEnum termEnum = reader.terms();
/* 491 */       SegmentMergeInfo smi = new SegmentMergeInfo(base, termEnum, reader);
/* 492 */       if (this.payloadProcessorProvider != null) {
/* 493 */         smi.dirPayloadProcessor = this.payloadProcessorProvider.getDirProcessor(reader.directory());
/*     */       }
/* 495 */       int[] docMap = smi.getDocMap();
/* 496 */       if (docMap != null) {
/* 497 */         if (this.docMaps == null) {
/* 498 */           this.docMaps = new int[readerCount][];
/*     */         }
/* 500 */         this.docMaps[i] = docMap;
/*     */       }
/*     */ 
/* 503 */       base += reader.numDocs();
/*     */ 
/* 505 */       assert (reader.numDocs() == reader.maxDoc() - smi.delCount);
/*     */ 
/* 507 */       if (smi.next())
/* 508 */         this.queue.add(smi);
/*     */       else {
/* 510 */         smi.close();
/*     */       }
/*     */     }
/* 513 */     SegmentMergeInfo[] match = new SegmentMergeInfo[this.readers.size()];
/*     */ 
/* 515 */     String currentField = null;
/* 516 */     FormatPostingsTermsConsumer termsConsumer = null;
/*     */ 
/* 518 */     while (this.queue.size() > 0) {
/* 519 */       int matchSize = 0;
/* 520 */       match[(matchSize++)] = ((SegmentMergeInfo)this.queue.pop());
/* 521 */       Term term = match[0].term;
/* 522 */       SegmentMergeInfo top = (SegmentMergeInfo)this.queue.top();
/*     */ 
/* 524 */       while ((top != null) && (term.compareTo(top.term) == 0)) {
/* 525 */         match[(matchSize++)] = ((SegmentMergeInfo)this.queue.pop());
/* 526 */         top = (SegmentMergeInfo)this.queue.top();
/*     */       }
/*     */ 
/* 529 */       if (currentField != term.field) {
/* 530 */         currentField = term.field;
/* 531 */         if (termsConsumer != null)
/* 532 */           termsConsumer.finish();
/* 533 */         FieldInfo fieldInfo = this.fieldInfos.fieldInfo(currentField);
/* 534 */         termsConsumer = consumer.addField(fieldInfo);
/* 535 */         this.indexOptions = fieldInfo.indexOptions;
/*     */       }
/*     */ 
/* 538 */       int df = appendPostings(termsConsumer, match, matchSize);
/* 539 */       this.checkAbort.work(df / 3.0D);
/*     */ 
/* 541 */       while (matchSize > 0) {
/* 542 */         matchSize--; SegmentMergeInfo smi = match[matchSize];
/* 543 */         if (smi.next())
/* 544 */           this.queue.add(smi);
/*     */         else
/* 546 */           smi.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final int appendPostings(FormatPostingsTermsConsumer termsConsumer, SegmentMergeInfo[] smis, int n)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 567 */     FormatPostingsDocsConsumer docConsumer = termsConsumer.addTerm(smis[0].term.text);
/* 568 */     int df = 0;
/* 569 */     for (int i = 0; i < n; i++) {
/* 570 */       SegmentMergeInfo smi = smis[i];
/* 571 */       TermPositions postings = smi.getPositions();
/* 572 */       assert (postings != null);
/* 573 */       int base = smi.base;
/* 574 */       int[] docMap = smi.getDocMap();
/* 575 */       postings.seek(smi.termEnum);
/*     */ 
/* 577 */       PayloadProcessorProvider.PayloadProcessor payloadProcessor = null;
/* 578 */       if (smi.dirPayloadProcessor != null) {
/* 579 */         payloadProcessor = smi.dirPayloadProcessor.getProcessor(smi.term);
/*     */       }
/*     */ 
/* 582 */       while (postings.next()) {
/* 583 */         df++;
/* 584 */         int doc = postings.doc();
/* 585 */         if (docMap != null)
/* 586 */           doc = docMap[doc];
/* 587 */         doc += base;
/*     */ 
/* 589 */         int freq = postings.freq();
/* 590 */         FormatPostingsPositionsConsumer posConsumer = docConsumer.addDoc(doc, freq);
/*     */ 
/* 592 */         if (this.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
/* 593 */           for (int j = 0; j < freq; j++) {
/* 594 */             int position = postings.nextPosition();
/* 595 */             int payloadLength = postings.getPayloadLength();
/* 596 */             if (payloadLength > 0) {
/* 597 */               if ((this.payloadBuffer == null) || (this.payloadBuffer.length < payloadLength))
/* 598 */                 this.payloadBuffer = new byte[payloadLength];
/* 599 */               postings.getPayload(this.payloadBuffer, 0);
/* 600 */               if (payloadProcessor != null) {
/* 601 */                 this.payloadBuffer = payloadProcessor.processPayload(this.payloadBuffer, 0, payloadLength);
/* 602 */                 payloadLength = payloadProcessor.payloadLength();
/*     */               }
/*     */             }
/* 605 */             posConsumer.addPosition(position, this.payloadBuffer, 0, payloadLength);
/*     */           }
/* 607 */           posConsumer.finish();
/*     */         }
/*     */       }
/*     */     }
/* 611 */     docConsumer.finish();
/*     */ 
/* 613 */     return df;
/*     */   }
/*     */ 
/*     */   public boolean getAnyNonBulkMerges() {
/* 617 */     assert (this.matchedCount <= this.readers.size());
/* 618 */     return this.matchedCount != this.readers.size();
/*     */   }
/*     */ 
/*     */   private void mergeNorms() throws IOException
/*     */   {
/* 623 */     int bufferSize = 0;
/* 624 */     for (IndexReader reader : this.readers) {
/* 625 */       bufferSize = Math.max(bufferSize, reader.maxDoc());
/*     */     }
/*     */ 
/* 628 */     byte[] normBuffer = null;
/* 629 */     IndexOutput output = null;
/* 630 */     boolean success = false;
/*     */     try {
/* 632 */       int numFieldInfos = this.fieldInfos.size();
/*     */       FieldInfo fi;
/* 633 */       for (int i = 0; i < numFieldInfos; i++) {
/* 634 */         fi = this.fieldInfos.fieldInfo(i);
/* 635 */         if ((fi.isIndexed) && (!fi.omitNorms)) {
/* 636 */           if (output == null) {
/* 637 */             output = this.directory.createOutput(IndexFileNames.segmentFileName(this.segment, "nrm"));
/* 638 */             output.writeBytes(SegmentNorms.NORMS_HEADER, SegmentNorms.NORMS_HEADER.length);
/*     */           }
/* 640 */           if (normBuffer == null) {
/* 641 */             normBuffer = new byte[bufferSize];
/*     */           }
/* 643 */           for (IndexReader reader : this.readers) {
/* 644 */             int maxDoc = reader.maxDoc();
/* 645 */             reader.norms(fi.name, normBuffer, 0);
/* 646 */             if (!reader.hasDeletions())
/*     */             {
/* 648 */               output.writeBytes(normBuffer, maxDoc);
/*     */             }
/*     */             else
/*     */             {
/* 652 */               for (int k = 0; k < maxDoc; k++) {
/* 653 */                 if (!reader.isDeleted(k)) {
/* 654 */                   output.writeByte(normBuffer[k]);
/*     */                 }
/*     */               }
/*     */             }
/* 658 */             this.checkAbort.work(maxDoc);
/*     */           }
/*     */         }
/*     */       }
/* 662 */       success = true;
/*     */     } finally {
/* 664 */       if (success)
/* 665 */         IOUtils.close(new Closeable[] { output });
/*     */       else
/* 667 */         IOUtils.closeWhileHandlingException(new Closeable[] { output }); 
/*     */     }
/*     */   }
/*     */   static class CheckAbort {
/*     */     private double workCount;
/*     */     private MergePolicy.OneMerge merge;
/*     */     private Directory dir;
/*     */ 
/*     */     public CheckAbort(MergePolicy.OneMerge merge, Directory dir) {
/* 677 */       this.merge = merge;
/* 678 */       this.dir = dir;
/*     */     }
/*     */ 
/*     */     public void work(double units)
/*     */       throws MergePolicy.MergeAbortedException
/*     */     {
/* 690 */       this.workCount += units;
/* 691 */       if (this.workCount >= 10000.0D) {
/* 692 */         this.merge.checkAborted(this.dir);
/* 693 */         this.workCount = 0.0D;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentMerger
 * JD-Core Version:    0.6.0
 */