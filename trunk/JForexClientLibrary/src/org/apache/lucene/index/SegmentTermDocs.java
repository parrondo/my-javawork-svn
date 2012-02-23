/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.util.BitVector;
/*     */ 
/*     */ class SegmentTermDocs
/*     */   implements TermDocs
/*     */ {
/*     */   protected SegmentReader parent;
/*     */   protected IndexInput freqStream;
/*     */   protected int count;
/*     */   protected int df;
/*     */   protected BitVector deletedDocs;
/*  31 */   int doc = 0;
/*     */   int freq;
/*     */   private int skipInterval;
/*     */   private int maxSkipLevels;
/*     */   private DefaultSkipListReader skipListReader;
/*     */   private long freqBasePointer;
/*     */   private long proxBasePointer;
/*     */   private long skipPointer;
/*     */   private boolean haveSkipped;
/*     */   protected boolean currentFieldStoresPayloads;
/*     */   protected FieldInfo.IndexOptions indexOptions;
/*     */ 
/*     */   protected SegmentTermDocs(SegmentReader parent)
/*     */   {
/*  48 */     this.parent = parent;
/*  49 */     this.freqStream = ((IndexInput)parent.core.freqStream.clone());
/*  50 */     synchronized (parent) {
/*  51 */       this.deletedDocs = parent.deletedDocs;
/*     */     }
/*  53 */     this.skipInterval = parent.core.getTermsReader().getSkipInterval();
/*  54 */     this.maxSkipLevels = parent.core.getTermsReader().getMaxSkipLevels();
/*     */   }
/*     */ 
/*     */   public void seek(Term term) throws IOException {
/*  58 */     TermInfo ti = this.parent.core.getTermsReader().get(term);
/*  59 */     seek(ti, term);
/*     */   }
/*     */ 
/*     */   public void seek(TermEnum termEnum)
/*     */     throws IOException
/*     */   {
/*     */     TermInfo ti;
/*     */     Term term;
/*     */     TermInfo ti;
/*  67 */     if (((termEnum instanceof SegmentTermEnum)) && (((SegmentTermEnum)termEnum).fieldInfos == this.parent.core.fieldInfos)) {
/*  68 */       SegmentTermEnum segmentTermEnum = (SegmentTermEnum)termEnum;
/*  69 */       Term term = segmentTermEnum.term();
/*  70 */       ti = segmentTermEnum.termInfo();
/*     */     } else {
/*  72 */       term = termEnum.term();
/*  73 */       ti = this.parent.core.getTermsReader().get(term);
/*     */     }
/*     */ 
/*  76 */     seek(ti, term);
/*     */   }
/*     */ 
/*     */   void seek(TermInfo ti, Term term) throws IOException {
/*  80 */     this.count = 0;
/*  81 */     FieldInfo fi = this.parent.core.fieldInfos.fieldInfo(term.field);
/*  82 */     this.indexOptions = (fi != null ? fi.indexOptions : FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
/*  83 */     this.currentFieldStoresPayloads = (fi != null ? fi.storePayloads : false);
/*  84 */     if (ti == null) {
/*  85 */       this.df = 0;
/*     */     } else {
/*  87 */       this.df = ti.docFreq;
/*  88 */       this.doc = 0;
/*  89 */       this.freqBasePointer = ti.freqPointer;
/*  90 */       this.proxBasePointer = ti.proxPointer;
/*  91 */       this.skipPointer = (this.freqBasePointer + ti.skipOffset);
/*  92 */       this.freqStream.seek(this.freqBasePointer);
/*  93 */       this.haveSkipped = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/*  98 */     this.freqStream.close();
/*  99 */     if (this.skipListReader != null)
/* 100 */       this.skipListReader.close(); 
/*     */   }
/*     */ 
/*     */   public final int doc() {
/* 103 */     return this.doc; } 
/* 104 */   public final int freq() { return this.freq; }
/*     */ 
/*     */   protected void skippingDoc() throws IOException {
/*     */   }
/*     */ 
/*     */   public boolean next() throws IOException {
/*     */     while (true) {
/* 111 */       if (this.count == this.df)
/* 112 */         return false;
/* 113 */       int docCode = this.freqStream.readVInt();
/*     */ 
/* 115 */       if (this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
/* 116 */         this.doc += docCode;
/* 117 */         this.freq = 1;
/*     */       } else {
/* 119 */         this.doc += (docCode >>> 1);
/* 120 */         if ((docCode & 0x1) != 0)
/* 121 */           this.freq = 1;
/*     */         else {
/* 123 */           this.freq = this.freqStream.readVInt();
/*     */         }
/*     */       }
/* 126 */       this.count += 1;
/*     */ 
/* 128 */       if ((this.deletedDocs == null) || (!this.deletedDocs.get(this.doc)))
/*     */         break;
/* 130 */       skippingDoc();
/*     */     }
/* 132 */     return true;
/*     */   }
/*     */ 
/*     */   public int read(int[] docs, int[] freqs)
/*     */     throws IOException
/*     */   {
/* 138 */     int length = docs.length;
/* 139 */     if (this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
/* 140 */       return readNoTf(docs, freqs, length);
/*     */     }
/* 142 */     int i = 0;
/* 143 */     while ((i < length) && (this.count < this.df))
/*     */     {
/* 145 */       int docCode = this.freqStream.readVInt();
/* 146 */       this.doc += (docCode >>> 1);
/* 147 */       if ((docCode & 0x1) != 0)
/* 148 */         this.freq = 1;
/*     */       else
/* 150 */         this.freq = this.freqStream.readVInt();
/* 151 */       this.count += 1;
/*     */ 
/* 153 */       if ((this.deletedDocs == null) || (!this.deletedDocs.get(this.doc))) {
/* 154 */         docs[i] = this.doc;
/* 155 */         freqs[i] = this.freq;
/* 156 */         i++;
/*     */       }
/*     */     }
/* 159 */     return i;
/*     */   }
/*     */ 
/*     */   private final int readNoTf(int[] docs, int[] freqs, int length) throws IOException
/*     */   {
/* 164 */     int i = 0;
/* 165 */     while ((i < length) && (this.count < this.df))
/*     */     {
/* 167 */       this.doc += this.freqStream.readVInt();
/* 168 */       this.count += 1;
/*     */ 
/* 170 */       if ((this.deletedDocs == null) || (!this.deletedDocs.get(this.doc))) {
/* 171 */         docs[i] = this.doc;
/*     */ 
/* 174 */         freqs[i] = 1;
/* 175 */         i++;
/*     */       }
/*     */     }
/* 178 */     return i;
/*     */   }
/*     */ 
/*     */   protected void skipProx(long proxPointer, int payloadLength) throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean skipTo(int target) throws IOException
/*     */   {
/* 187 */     if ((target - this.skipInterval >= this.doc) && (this.df >= this.skipInterval)) {
/* 188 */       if (this.skipListReader == null) {
/* 189 */         this.skipListReader = new DefaultSkipListReader((IndexInput)this.freqStream.clone(), this.maxSkipLevels, this.skipInterval);
/*     */       }
/* 191 */       if (!this.haveSkipped) {
/* 192 */         this.skipListReader.init(this.skipPointer, this.freqBasePointer, this.proxBasePointer, this.df, this.currentFieldStoresPayloads);
/* 193 */         this.haveSkipped = true;
/*     */       }
/*     */ 
/* 196 */       int newCount = this.skipListReader.skipTo(target);
/* 197 */       if (newCount > this.count) {
/* 198 */         this.freqStream.seek(this.skipListReader.getFreqPointer());
/* 199 */         skipProx(this.skipListReader.getProxPointer(), this.skipListReader.getPayloadLength());
/*     */ 
/* 201 */         this.doc = this.skipListReader.getDoc();
/* 202 */         this.count = newCount;
/*     */       }
/*     */     }
/*     */ 
/*     */     do
/*     */     {
/* 208 */       if (!next())
/* 209 */         return false; 
/*     */     }
/* 210 */     while (target > this.doc);
/* 211 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentTermDocs
 * JD-Core Version:    0.6.0
 */