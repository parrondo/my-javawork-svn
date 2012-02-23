/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ 
/*     */ final class SegmentTermPositions extends SegmentTermDocs
/*     */   implements TermPositions
/*     */ {
/*     */   private IndexInput proxStream;
/*     */   private int proxCount;
/*     */   private int position;
/*     */   private int payloadLength;
/*     */   private boolean needToLoadPayload;
/*  39 */   private long lazySkipPointer = -1L;
/*  40 */   private int lazySkipProxCount = 0;
/*     */ 
/*     */   SegmentTermPositions(SegmentReader p) {
/*  43 */     super(p);
/*  44 */     this.proxStream = null;
/*     */   }
/*     */ 
/*     */   final void seek(TermInfo ti, Term term) throws IOException
/*     */   {
/*  49 */     super.seek(ti, term);
/*  50 */     if (ti != null) {
/*  51 */       this.lazySkipPointer = ti.proxPointer;
/*     */     }
/*  53 */     this.lazySkipProxCount = 0;
/*  54 */     this.proxCount = 0;
/*  55 */     this.payloadLength = 0;
/*  56 */     this.needToLoadPayload = false;
/*     */   }
/*     */ 
/*     */   public final void close() throws IOException
/*     */   {
/*  61 */     super.close();
/*  62 */     if (this.proxStream != null) this.proxStream.close(); 
/*     */   }
/*     */ 
/*     */   public final int nextPosition() throws IOException
/*     */   {
/*  66 */     if (this.indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
/*     */     {
/*  68 */       return 0;
/*     */     }
/*  70 */     lazySkip();
/*  71 */     this.proxCount -= 1;
/*  72 */     return this.position += readDeltaPosition();
/*     */   }
/*     */ 
/*     */   private final int readDeltaPosition() throws IOException {
/*  76 */     int delta = this.proxStream.readVInt();
/*  77 */     if (this.currentFieldStoresPayloads)
/*     */     {
/*  82 */       if ((delta & 0x1) != 0) {
/*  83 */         this.payloadLength = this.proxStream.readVInt();
/*     */       }
/*  85 */       delta >>>= 1;
/*  86 */       this.needToLoadPayload = true;
/*     */     }
/*  88 */     return delta;
/*     */   }
/*     */ 
/*     */   protected final void skippingDoc()
/*     */     throws IOException
/*     */   {
/*  94 */     this.lazySkipProxCount += this.freq;
/*     */   }
/*     */ 
/*     */   public final boolean next()
/*     */     throws IOException
/*     */   {
/* 101 */     this.lazySkipProxCount += this.proxCount;
/*     */ 
/* 103 */     if (super.next()) {
/* 104 */       this.proxCount = this.freq;
/* 105 */       this.position = 0;
/* 106 */       return true;
/*     */     }
/* 108 */     return false;
/*     */   }
/*     */ 
/*     */   public final int read(int[] docs, int[] freqs)
/*     */   {
/* 113 */     throw new UnsupportedOperationException("TermPositions does not support processing multiple documents in one call. Use TermDocs instead.");
/*     */   }
/*     */ 
/*     */   protected void skipProx(long proxPointer, int payloadLength)
/*     */     throws IOException
/*     */   {
/* 121 */     this.lazySkipPointer = proxPointer;
/* 122 */     this.lazySkipProxCount = 0;
/* 123 */     this.proxCount = 0;
/* 124 */     this.payloadLength = payloadLength;
/* 125 */     this.needToLoadPayload = false;
/*     */   }
/*     */ 
/*     */   private void skipPositions(int n) throws IOException {
/* 129 */     assert (this.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
/* 130 */     for (int f = n; f > 0; f--) {
/* 131 */       readDeltaPosition();
/* 132 */       skipPayload();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void skipPayload() throws IOException {
/* 137 */     if ((this.needToLoadPayload) && (this.payloadLength > 0)) {
/* 138 */       this.proxStream.seek(this.proxStream.getFilePointer() + this.payloadLength);
/*     */     }
/* 140 */     this.needToLoadPayload = false;
/*     */   }
/*     */ 
/*     */   private void lazySkip()
/*     */     throws IOException
/*     */   {
/* 154 */     if (this.proxStream == null)
/*     */     {
/* 156 */       this.proxStream = ((IndexInput)this.parent.core.proxStream.clone());
/*     */     }
/*     */ 
/* 161 */     skipPayload();
/*     */ 
/* 163 */     if (this.lazySkipPointer != -1L) {
/* 164 */       this.proxStream.seek(this.lazySkipPointer);
/* 165 */       this.lazySkipPointer = -1L;
/*     */     }
/*     */ 
/* 168 */     if (this.lazySkipProxCount != 0) {
/* 169 */       skipPositions(this.lazySkipProxCount);
/* 170 */       this.lazySkipProxCount = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getPayloadLength() {
/* 175 */     return this.payloadLength;
/*     */   }
/*     */ 
/*     */   public byte[] getPayload(byte[] data, int offset) throws IOException {
/* 179 */     if (!this.needToLoadPayload)
/* 180 */       throw new IOException("Either no payload exists at this term position or an attempt was made to load it more than once.");
/*     */     int retOffset;
/*     */     byte[] retArray;
/*     */     int retOffset;
/* 186 */     if ((data == null) || (data.length - offset < this.payloadLength))
/*     */     {
/* 189 */       byte[] retArray = new byte[this.payloadLength];
/* 190 */       retOffset = 0;
/*     */     } else {
/* 192 */       retArray = data;
/* 193 */       retOffset = offset;
/*     */     }
/* 195 */     this.proxStream.readBytes(retArray, retOffset, this.payloadLength);
/* 196 */     this.needToLoadPayload = false;
/* 197 */     return retArray;
/*     */   }
/*     */ 
/*     */   public boolean isPayloadAvailable() {
/* 201 */     return (this.needToLoadPayload) && (this.payloadLength > 0);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentTermPositions
 * JD-Core Version:    0.6.0
 */