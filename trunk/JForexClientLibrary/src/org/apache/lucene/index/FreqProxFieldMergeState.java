/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ final class FreqProxFieldMergeState
/*     */ {
/*     */   final FreqProxTermsWriterPerField field;
/*     */   final int numPostings;
/*     */   final CharBlockPool charPool;
/*     */   final int[] termIDs;
/*     */   final FreqProxTermsWriterPerField.FreqProxPostingsArray postings;
/*     */   int currentTermID;
/*     */   char[] text;
/*     */   int textOffset;
/*  43 */   private int postingUpto = -1;
/*     */ 
/*  45 */   final ByteSliceReader freq = new ByteSliceReader();
/*  46 */   final ByteSliceReader prox = new ByteSliceReader();
/*     */   int docID;
/*     */   int termFreq;
/*     */ 
/*     */   public FreqProxFieldMergeState(FreqProxTermsWriterPerField field)
/*     */   {
/*  52 */     this.field = field;
/*  53 */     this.charPool = field.perThread.termsHashPerThread.charPool;
/*  54 */     this.numPostings = field.termsHashPerField.numPostings;
/*  55 */     this.termIDs = field.termsHashPerField.sortPostings();
/*  56 */     this.postings = ((FreqProxTermsWriterPerField.FreqProxPostingsArray)field.termsHashPerField.postingsArray);
/*     */   }
/*     */ 
/*     */   boolean nextTerm() throws IOException {
/*  60 */     this.postingUpto += 1;
/*  61 */     if (this.postingUpto == this.numPostings) {
/*  62 */       return false;
/*     */     }
/*  64 */     this.currentTermID = this.termIDs[this.postingUpto];
/*  65 */     this.docID = 0;
/*     */ 
/*  67 */     int textStart = this.postings.textStarts[this.currentTermID];
/*  68 */     this.text = this.charPool.buffers[(textStart >> 14)];
/*  69 */     this.textOffset = (textStart & 0x3FFF);
/*     */ 
/*  71 */     this.field.termsHashPerField.initReader(this.freq, this.currentTermID, 0);
/*  72 */     if (this.field.fieldInfo.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
/*  73 */       this.field.termsHashPerField.initReader(this.prox, this.currentTermID, 1);
/*     */     }
/*     */ 
/*  76 */     boolean result = nextDoc();
/*  77 */     assert (result);
/*     */ 
/*  79 */     return true;
/*     */   }
/*     */ 
/*     */   public String termText() {
/*  83 */     int upto = this.textOffset;
/*  84 */     while (this.text[upto] != 65535) {
/*  85 */       upto++;
/*     */     }
/*  87 */     return new String(this.text, this.textOffset, upto - this.textOffset);
/*     */   }
/*     */ 
/*     */   public boolean nextDoc() throws IOException {
/*  91 */     if (this.freq.eof()) {
/*  92 */       if (this.postings.lastDocCodes[this.currentTermID] != -1)
/*     */       {
/*  94 */         this.docID = this.postings.lastDocIDs[this.currentTermID];
/*  95 */         if (this.field.indexOptions != FieldInfo.IndexOptions.DOCS_ONLY)
/*  96 */           this.termFreq = this.postings.docFreqs[this.currentTermID];
/*  97 */         this.postings.lastDocCodes[this.currentTermID] = -1;
/*  98 */         return true;
/*     */       }
/*     */ 
/* 101 */       return false;
/*     */     }
/*     */ 
/* 104 */     int code = this.freq.readVInt();
/* 105 */     if (this.field.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
/* 106 */       this.docID += code;
/*     */     } else {
/* 108 */       this.docID += (code >>> 1);
/* 109 */       if ((code & 0x1) != 0)
/* 110 */         this.termFreq = 1;
/*     */       else {
/* 112 */         this.termFreq = this.freq.readVInt();
/*     */       }
/*     */     }
/* 115 */     assert (this.docID != this.postings.lastDocIDs[this.currentTermID]);
/*     */ 
/* 117 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FreqProxFieldMergeState
 * JD-Core Version:    0.6.0
 */