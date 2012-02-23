/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ final class TermsHashPerThread extends InvertedDocConsumerPerThread
/*     */ {
/*     */   final TermsHash termsHash;
/*     */   final TermsHashConsumerPerThread consumer;
/*     */   final TermsHashPerThread nextPerThread;
/*     */   final CharBlockPool charPool;
/*     */   final IntBlockPool intPool;
/*     */   final ByteBlockPool bytePool;
/*     */   final boolean primary;
/*     */   final DocumentsWriter.DocState docState;
/*     */ 
/*     */   public TermsHashPerThread(DocInverterPerThread docInverterPerThread, TermsHash termsHash, TermsHash nextTermsHash, TermsHashPerThread primaryPerThread)
/*     */   {
/*  35 */     this.docState = docInverterPerThread.docState;
/*     */ 
/*  37 */     this.termsHash = termsHash;
/*  38 */     this.consumer = termsHash.consumer.addThread(this);
/*     */ 
/*  40 */     if (nextTermsHash != null)
/*     */     {
/*  42 */       this.charPool = new CharBlockPool(termsHash.docWriter);
/*  43 */       this.primary = true;
/*     */     } else {
/*  45 */       this.charPool = primaryPerThread.charPool;
/*  46 */       this.primary = false;
/*     */     }
/*     */ 
/*  49 */     this.intPool = new IntBlockPool(termsHash.docWriter);
/*  50 */     this.bytePool = new ByteBlockPool(termsHash.docWriter.byteBlockAllocator);
/*     */ 
/*  52 */     if (nextTermsHash != null)
/*  53 */       this.nextPerThread = nextTermsHash.addThread(docInverterPerThread, this);
/*     */     else
/*  55 */       this.nextPerThread = null;
/*     */   }
/*     */ 
/*     */   InvertedDocConsumerPerField addField(DocInverterPerField docInverterPerField, FieldInfo fieldInfo)
/*     */   {
/*  60 */     return new TermsHashPerField(docInverterPerField, this, this.nextPerThread, fieldInfo);
/*     */   }
/*     */ 
/*     */   public synchronized void abort()
/*     */   {
/*  65 */     reset(true);
/*     */     try {
/*  67 */       this.consumer.abort();
/*     */     } finally {
/*  69 */       if (this.nextPerThread != null)
/*  70 */         this.nextPerThread.abort();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void startDocument()
/*     */     throws IOException
/*     */   {
/*  77 */     this.consumer.startDocument();
/*  78 */     if (this.nextPerThread != null)
/*  79 */       this.nextPerThread.consumer.startDocument();
/*     */   }
/*     */ 
/*     */   public DocumentsWriter.DocWriter finishDocument() throws IOException
/*     */   {
/*  84 */     DocumentsWriter.DocWriter doc = this.consumer.finishDocument();
/*     */     DocumentsWriter.DocWriter doc2;
/*     */     DocumentsWriter.DocWriter doc2;
/*  87 */     if (this.nextPerThread != null)
/*  88 */       doc2 = this.nextPerThread.consumer.finishDocument();
/*     */     else
/*  90 */       doc2 = null;
/*  91 */     if (doc == null) {
/*  92 */       return doc2;
/*     */     }
/*  94 */     doc.setNext(doc2);
/*  95 */     return doc;
/*     */   }
/*     */ 
/*     */   void reset(boolean recyclePostings)
/*     */   {
/* 101 */     this.intPool.reset();
/* 102 */     this.bytePool.reset();
/*     */ 
/* 104 */     if (this.primary)
/* 105 */       this.charPool.reset();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermsHashPerThread
 * JD-Core Version:    0.6.0
 */