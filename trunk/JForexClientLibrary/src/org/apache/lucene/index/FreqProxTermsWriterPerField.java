/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ 
/*     */ final class FreqProxTermsWriterPerField extends TermsHashConsumerPerField
/*     */   implements Comparable<FreqProxTermsWriterPerField>
/*     */ {
/*     */   final FreqProxTermsWriterPerThread perThread;
/*     */   final TermsHashPerField termsHashPerField;
/*     */   final FieldInfo fieldInfo;
/*     */   final DocumentsWriter.DocState docState;
/*     */   final FieldInvertState fieldState;
/*     */   FieldInfo.IndexOptions indexOptions;
/*     */   PayloadAttribute payloadAttribute;
/*     */   boolean hasPayloads;
/*     */ 
/*     */   public FreqProxTermsWriterPerField(TermsHashPerField termsHashPerField, FreqProxTermsWriterPerThread perThread, FieldInfo fieldInfo)
/*     */   {
/*  41 */     this.termsHashPerField = termsHashPerField;
/*  42 */     this.perThread = perThread;
/*  43 */     this.fieldInfo = fieldInfo;
/*  44 */     this.docState = termsHashPerField.docState;
/*  45 */     this.fieldState = termsHashPerField.fieldState;
/*  46 */     this.indexOptions = fieldInfo.indexOptions;
/*     */   }
/*     */ 
/*     */   int getStreamCount()
/*     */   {
/*  51 */     if (this.fieldInfo.indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
/*  52 */       return 1;
/*     */     }
/*  54 */     return 2;
/*     */   }
/*     */ 
/*     */   void finish()
/*     */   {
/*     */   }
/*     */ 
/*     */   void skippingLongTerm() throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public int compareTo(FreqProxTermsWriterPerField other) {
/*  66 */     return this.fieldInfo.name.compareTo(other.fieldInfo.name);
/*     */   }
/*     */ 
/*     */   void reset()
/*     */   {
/*  72 */     this.indexOptions = this.fieldInfo.indexOptions;
/*  73 */     this.payloadAttribute = null;
/*     */   }
/*     */ 
/*     */   boolean start(Fieldable[] fields, int count)
/*     */   {
/*  78 */     for (int i = 0; i < count; i++)
/*  79 */       if (fields[i].isIndexed())
/*  80 */         return true;
/*  81 */     return false;
/*     */   }
/*     */ 
/*     */   void start(Fieldable f)
/*     */   {
/*  86 */     if (this.fieldState.attributeSource.hasAttribute(PayloadAttribute.class))
/*  87 */       this.payloadAttribute = ((PayloadAttribute)this.fieldState.attributeSource.getAttribute(PayloadAttribute.class));
/*     */     else
/*  89 */       this.payloadAttribute = null;
/*     */   }
/*     */ 
/*     */   void writeProx(int termID, int proxCode)
/*     */   {
/*     */     Payload payload;
/*     */     Payload payload;
/*  95 */     if (this.payloadAttribute == null)
/*  96 */       payload = null;
/*     */     else {
/*  98 */       payload = this.payloadAttribute.getPayload();
/*     */     }
/*     */ 
/* 101 */     if ((payload != null) && (payload.length > 0)) {
/* 102 */       this.termsHashPerField.writeVInt(1, proxCode << 1 | 0x1);
/* 103 */       this.termsHashPerField.writeVInt(1, payload.length);
/* 104 */       this.termsHashPerField.writeBytes(1, payload.data, payload.offset, payload.length);
/* 105 */       this.hasPayloads = true;
/*     */     } else {
/* 107 */       this.termsHashPerField.writeVInt(1, proxCode << 1);
/*     */     }
/* 109 */     FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
/* 110 */     postings.lastPositions[termID] = this.fieldState.position;
/*     */   }
/*     */ 
/*     */   void newTerm(int termID)
/*     */   {
/* 118 */     assert (this.docState.testPoint("FreqProxTermsWriterPerField.newTerm start"));
/*     */ 
/* 120 */     FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
/* 121 */     postings.lastDocIDs[termID] = this.docState.docID;
/* 122 */     if (this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
/* 123 */       postings.lastDocCodes[termID] = this.docState.docID;
/*     */     } else {
/* 125 */       postings.lastDocCodes[termID] = (this.docState.docID << 1);
/* 126 */       postings.docFreqs[termID] = 1;
/* 127 */       if (this.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
/* 128 */         writeProx(termID, this.fieldState.position);
/*     */       }
/*     */     }
/* 131 */     this.fieldState.maxTermFrequency = Math.max(1, this.fieldState.maxTermFrequency);
/* 132 */     this.fieldState.uniqueTermCount += 1;
/*     */   }
/*     */ 
/*     */   void addTerm(int termID)
/*     */   {
/* 138 */     assert (this.docState.testPoint("FreqProxTermsWriterPerField.addTerm start"));
/*     */ 
/* 140 */     FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
/*     */ 
/* 142 */     assert ((this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) || (postings.docFreqs[termID] > 0));
/*     */ 
/* 144 */     if (this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
/* 145 */       if (this.docState.docID != postings.lastDocIDs[termID]) {
/* 146 */         assert (this.docState.docID > postings.lastDocIDs[termID]);
/* 147 */         this.termsHashPerField.writeVInt(0, postings.lastDocCodes[termID]);
/* 148 */         postings.lastDocCodes[termID] = (this.docState.docID - postings.lastDocIDs[termID]);
/* 149 */         postings.lastDocIDs[termID] = this.docState.docID;
/* 150 */         this.fieldState.uniqueTermCount += 1;
/*     */       }
/*     */     }
/* 153 */     else if (this.docState.docID != postings.lastDocIDs[termID]) {
/* 154 */       assert (this.docState.docID > postings.lastDocIDs[termID]);
/*     */ 
/* 160 */       if (1 == postings.docFreqs[termID]) {
/* 161 */         this.termsHashPerField.writeVInt(0, postings.lastDocCodes[termID] | 0x1);
/*     */       } else {
/* 163 */         this.termsHashPerField.writeVInt(0, postings.lastDocCodes[termID]);
/* 164 */         this.termsHashPerField.writeVInt(0, postings.docFreqs[termID]);
/*     */       }
/* 166 */       postings.docFreqs[termID] = 1;
/* 167 */       this.fieldState.maxTermFrequency = Math.max(1, this.fieldState.maxTermFrequency);
/* 168 */       postings.lastDocCodes[termID] = (this.docState.docID - postings.lastDocIDs[termID] << 1);
/* 169 */       postings.lastDocIDs[termID] = this.docState.docID;
/* 170 */       if (this.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
/* 171 */         writeProx(termID, this.fieldState.position);
/*     */       }
/* 173 */       this.fieldState.uniqueTermCount += 1;
/*     */     } else {
/* 175 */       this.fieldState.maxTermFrequency = Math.max(this.fieldState.maxTermFrequency, postings.docFreqs[termID] += 1);
/* 176 */       if (this.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
/* 177 */         writeProx(termID, this.fieldState.position - postings.lastPositions[termID]);
/*     */     }
/*     */   }
/*     */ 
/*     */   ParallelPostingsArray createPostingsArray(int size)
/*     */   {
/* 185 */     return new FreqProxPostingsArray(size); } 
/*     */   public void abort() {  } 
/*     */   static final class FreqProxPostingsArray extends ParallelPostingsArray { int[] docFreqs;
/*     */     int[] lastDocIDs;
/*     */     int[] lastDocCodes;
/*     */     int[] lastPositions;
/*     */ 
/* 190 */     public FreqProxPostingsArray(int size) { super();
/* 191 */       this.docFreqs = new int[size];
/* 192 */       this.lastDocIDs = new int[size];
/* 193 */       this.lastDocCodes = new int[size];
/* 194 */       this.lastPositions = new int[size];
/*     */     }
/*     */ 
/*     */     ParallelPostingsArray newInstance(int size)
/*     */     {
/* 204 */       return new FreqProxPostingsArray(size);
/*     */     }
/*     */ 
/*     */     void copyTo(ParallelPostingsArray toArray, int numToCopy)
/*     */     {
/* 209 */       assert ((toArray instanceof FreqProxPostingsArray));
/* 210 */       FreqProxPostingsArray to = (FreqProxPostingsArray)toArray;
/*     */ 
/* 212 */       super.copyTo(toArray, numToCopy);
/*     */ 
/* 214 */       System.arraycopy(this.docFreqs, 0, to.docFreqs, 0, numToCopy);
/* 215 */       System.arraycopy(this.lastDocIDs, 0, to.lastDocIDs, 0, numToCopy);
/* 216 */       System.arraycopy(this.lastDocCodes, 0, to.lastDocCodes, 0, numToCopy);
/* 217 */       System.arraycopy(this.lastPositions, 0, to.lastPositions, 0, numToCopy);
/*     */     }
/*     */ 
/*     */     int bytesPerPosting()
/*     */     {
/* 222 */       return 28;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FreqProxTermsWriterPerField
 * JD-Core Version:    0.6.0
 */