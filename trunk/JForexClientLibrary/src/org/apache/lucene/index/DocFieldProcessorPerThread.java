/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.RamUsageEstimator;
/*     */ 
/*     */ final class DocFieldProcessorPerThread extends DocConsumerPerThread
/*     */ {
/*     */   float docBoost;
/*     */   int fieldGen;
/*     */   final DocFieldProcessor docFieldProcessor;
/*     */   final FieldInfos fieldInfos;
/*     */   final DocFieldConsumerPerThread consumer;
/*  48 */   DocFieldProcessorPerField[] fields = new DocFieldProcessorPerField[1];
/*     */   int fieldCount;
/*  52 */   DocFieldProcessorPerField[] fieldHash = new DocFieldProcessorPerField[2];
/*  53 */   int hashMask = 1;
/*     */   int totalFieldCount;
/*     */   final StoredFieldsWriterPerThread fieldsWriter;
/*     */   final DocumentsWriter.DocState docState;
/*     */   private static final Comparator<DocFieldProcessorPerField> fieldsComp;
/* 308 */   PerDoc[] docFreeList = new PerDoc[1];
/*     */   int freeCount;
/*     */   int allocCount;
/*     */ 
/*     */   public DocFieldProcessorPerThread(DocumentsWriterThreadState threadState, DocFieldProcessor docFieldProcessor)
/*     */     throws IOException
/*     */   {
/*  61 */     this.docState = threadState.docState;
/*  62 */     this.docFieldProcessor = docFieldProcessor;
/*  63 */     this.fieldInfos = docFieldProcessor.fieldInfos;
/*  64 */     this.consumer = docFieldProcessor.consumer.addThread(this);
/*  65 */     this.fieldsWriter = docFieldProcessor.fieldsWriter.addThread(this.docState);
/*     */   }
/*     */ 
/*     */   public void abort()
/*     */   {
/*  70 */     Throwable th = null;
/*     */ 
/*  72 */     for (DocFieldProcessorPerField field : this.fieldHash) {
/*  73 */       while (field != null) {
/*  74 */         DocFieldProcessorPerField next = field.next;
/*     */         try {
/*  76 */           field.abort();
/*     */         } catch (Throwable t) {
/*  78 */           if (th == null) {
/*  79 */             th = t;
/*     */           }
/*     */         }
/*  82 */         field = next;
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/*  87 */       this.fieldsWriter.abort();
/*     */     } catch (Throwable t) {
/*  89 */       if (th == null) {
/*  90 */         th = t;
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/*  95 */       this.consumer.abort();
/*     */     } catch (Throwable t) {
/*  97 */       if (th == null) {
/*  98 */         th = t;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 103 */     if (th != null) {
/* 104 */       if ((th instanceof RuntimeException)) throw ((RuntimeException)th);
/* 105 */       if ((th instanceof Error)) throw ((Error)th);
/*     */ 
/* 107 */       throw new RuntimeException(th);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Collection<DocFieldConsumerPerField> fields() {
/* 112 */     Collection fields = new HashSet();
/* 113 */     for (int i = 0; i < this.fieldHash.length; i++) {
/* 114 */       DocFieldProcessorPerField field = this.fieldHash[i];
/* 115 */       while (field != null) {
/* 116 */         fields.add(field.consumer);
/* 117 */         field = field.next;
/*     */       }
/*     */     }
/* 120 */     assert (fields.size() == this.totalFieldCount);
/* 121 */     return fields;
/*     */   }
/*     */ 
/*     */   void trimFields(SegmentWriteState state)
/*     */   {
/* 129 */     for (int i = 0; i < this.fieldHash.length; i++) {
/* 130 */       DocFieldProcessorPerField perField = this.fieldHash[i];
/* 131 */       DocFieldProcessorPerField lastPerField = null;
/*     */ 
/* 133 */       while (perField != null)
/*     */       {
/* 135 */         if (perField.lastGen == -1)
/*     */         {
/* 141 */           if (lastPerField == null)
/* 142 */             this.fieldHash[i] = perField.next;
/*     */           else {
/* 144 */             lastPerField.next = perField.next;
/*     */           }
/* 146 */           if (state.infoStream != null) {
/* 147 */             state.infoStream.println("  purge field=" + perField.fieldInfo.name);
/*     */           }
/* 149 */           this.totalFieldCount -= 1;
/*     */         }
/*     */         else
/*     */         {
/* 153 */           perField.lastGen = -1;
/* 154 */           lastPerField = perField;
/*     */         }
/*     */ 
/* 157 */         perField = perField.next;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void rehash() {
/* 163 */     int newHashSize = this.fieldHash.length * 2;
/* 164 */     assert (newHashSize > this.fieldHash.length);
/*     */ 
/* 166 */     DocFieldProcessorPerField[] newHashArray = new DocFieldProcessorPerField[newHashSize];
/*     */ 
/* 169 */     int newHashMask = newHashSize - 1;
/* 170 */     for (int j = 0; j < this.fieldHash.length; j++) {
/* 171 */       DocFieldProcessorPerField fp0 = this.fieldHash[j];
/* 172 */       while (fp0 != null) {
/* 173 */         int hashPos2 = fp0.fieldInfo.name.hashCode() & newHashMask;
/* 174 */         DocFieldProcessorPerField nextFP0 = fp0.next;
/* 175 */         fp0.next = newHashArray[hashPos2];
/* 176 */         newHashArray[hashPos2] = fp0;
/* 177 */         fp0 = nextFP0;
/*     */       }
/*     */     }
/*     */ 
/* 181 */     this.fieldHash = newHashArray;
/* 182 */     this.hashMask = newHashMask;
/*     */   }
/*     */ 
/*     */   public DocumentsWriter.DocWriter processDocument()
/*     */     throws IOException
/*     */   {
/* 188 */     this.consumer.startDocument();
/* 189 */     this.fieldsWriter.startDocument();
/*     */ 
/* 191 */     Document doc = this.docState.doc;
/*     */ 
/* 193 */     assert (this.docFieldProcessor.docWriter.writer.testPoint("DocumentsWriter.ThreadState.init start"));
/*     */ 
/* 195 */     this.fieldCount = 0;
/*     */ 
/* 197 */     int thisFieldGen = this.fieldGen++;
/*     */ 
/* 199 */     List docFields = doc.getFields();
/* 200 */     int numDocFields = docFields.size();
/*     */ 
/* 207 */     for (int i = 0; i < numDocFields; i++) {
/* 208 */       Fieldable field = (Fieldable)docFields.get(i);
/* 209 */       String fieldName = field.name();
/*     */ 
/* 212 */       int hashPos = fieldName.hashCode() & this.hashMask;
/* 213 */       DocFieldProcessorPerField fp = this.fieldHash[hashPos];
/* 214 */       while ((fp != null) && (!fp.fieldInfo.name.equals(fieldName))) {
/* 215 */         fp = fp.next;
/*     */       }
/* 217 */       if (fp == null)
/*     */       {
/* 224 */         FieldInfo fi = this.fieldInfos.add(fieldName, field.isIndexed(), field.isTermVectorStored(), field.isStorePositionWithTermVector(), field.isStoreOffsetWithTermVector(), field.getOmitNorms(), false, field.getIndexOptions());
/*     */ 
/* 228 */         fp = new DocFieldProcessorPerField(this, fi);
/* 229 */         fp.next = this.fieldHash[hashPos];
/* 230 */         this.fieldHash[hashPos] = fp;
/* 231 */         this.totalFieldCount += 1;
/*     */ 
/* 233 */         if (this.totalFieldCount >= this.fieldHash.length / 2)
/* 234 */           rehash();
/*     */       } else {
/* 236 */         fp.fieldInfo.update(field.isIndexed(), field.isTermVectorStored(), field.isStorePositionWithTermVector(), field.isStoreOffsetWithTermVector(), field.getOmitNorms(), false, field.getIndexOptions());
/*     */       }
/*     */ 
/* 241 */       if (thisFieldGen != fp.lastGen)
/*     */       {
/* 244 */         fp.fieldCount = 0;
/*     */ 
/* 246 */         if (this.fieldCount == this.fields.length) {
/* 247 */           int newSize = this.fields.length * 2;
/* 248 */           DocFieldProcessorPerField[] newArray = new DocFieldProcessorPerField[newSize];
/* 249 */           System.arraycopy(this.fields, 0, newArray, 0, this.fieldCount);
/* 250 */           this.fields = newArray;
/*     */         }
/*     */ 
/* 253 */         this.fields[(this.fieldCount++)] = fp;
/* 254 */         fp.lastGen = thisFieldGen;
/*     */       }
/*     */ 
/* 257 */       if (fp.fieldCount == fp.fields.length) {
/* 258 */         Fieldable[] newArray = new Fieldable[fp.fields.length * 2];
/* 259 */         System.arraycopy(fp.fields, 0, newArray, 0, fp.fieldCount);
/* 260 */         fp.fields = newArray;
/*     */       }
/*     */ 
/* 263 */       fp.fields[(fp.fieldCount++)] = field;
/* 264 */       if (field.isStored()) {
/* 265 */         this.fieldsWriter.addField(field, fp.fieldInfo);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 275 */     ArrayUtil.quickSort(this.fields, 0, this.fieldCount, fieldsComp);
/*     */ 
/* 277 */     for (int i = 0; i < this.fieldCount; i++) {
/* 278 */       this.fields[i].consumer.processFields(this.fields[i].fields, this.fields[i].fieldCount);
/*     */     }
/* 280 */     if ((this.docState.maxTermPrefix != null) && (this.docState.infoStream != null)) {
/* 281 */       this.docState.infoStream.println("WARNING: document contains at least one immense term (longer than the max length 16383), all of which were skipped.  Please correct the analyzer to not produce such terms.  The prefix of the first immense term is: '" + this.docState.maxTermPrefix + "...'");
/* 282 */       this.docState.maxTermPrefix = null;
/*     */     }
/*     */ 
/* 285 */     DocumentsWriter.DocWriter one = this.fieldsWriter.finishDocument();
/* 286 */     DocumentsWriter.DocWriter two = this.consumer.finishDocument();
/* 287 */     if (one == null)
/* 288 */       return two;
/* 289 */     if (two == null) {
/* 290 */       return one;
/*     */     }
/* 292 */     PerDoc both = getPerDoc();
/* 293 */     both.docID = this.docState.docID;
/* 294 */     assert (one.docID == this.docState.docID);
/* 295 */     assert (two.docID == this.docState.docID);
/* 296 */     both.one = one;
/* 297 */     both.two = two;
/* 298 */     return both;
/*     */   }
/*     */ 
/*     */   synchronized PerDoc getPerDoc()
/*     */   {
/* 313 */     if (this.freeCount == 0) {
/* 314 */       this.allocCount += 1;
/* 315 */       if (this.allocCount > this.docFreeList.length)
/*     */       {
/* 319 */         assert (this.allocCount == 1 + this.docFreeList.length);
/* 320 */         this.docFreeList = new PerDoc[ArrayUtil.oversize(this.allocCount, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
/*     */       }
/* 322 */       return new PerDoc();
/*     */     }
/* 324 */     return this.docFreeList[(--this.freeCount)];
/*     */   }
/*     */ 
/*     */   synchronized void freePerDoc(PerDoc perDoc) {
/* 328 */     assert (this.freeCount < this.docFreeList.length);
/* 329 */     this.docFreeList[(this.freeCount++)] = perDoc;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 302 */     fieldsComp = new Comparator() {
/*     */       public int compare(DocFieldProcessorPerField o1, DocFieldProcessorPerField o2) {
/* 304 */         return o1.fieldInfo.name.compareTo(o2.fieldInfo.name);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   class PerDoc extends DocumentsWriter.DocWriter
/*     */   {
/*     */     DocumentsWriter.DocWriter one;
/*     */     DocumentsWriter.DocWriter two;
/*     */ 
/*     */     PerDoc()
/*     */     {
/*     */     }
/*     */ 
/*     */     public long sizeInBytes()
/*     */     {
/* 339 */       return this.one.sizeInBytes() + this.two.sizeInBytes();
/*     */     }
/*     */ 
/*     */     public void finish() throws IOException
/*     */     {
/*     */       try {
/*     */         try {
/* 346 */           this.one.finish();
/*     */         } finally {
/* 348 */           this.two.finish();
/*     */         }
/*     */       } finally {
/* 351 */         DocFieldProcessorPerThread.this.freePerDoc(this);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void abort()
/*     */     {
/*     */       try {
/*     */         try {
/* 359 */           this.one.abort();
/*     */         } finally {
/* 361 */           this.two.abort();
/*     */         }
/*     */       } finally {
/* 364 */         DocFieldProcessorPerThread.this.freePerDoc(this);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DocFieldProcessorPerThread
 * JD-Core Version:    0.6.0
 */