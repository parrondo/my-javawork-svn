/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ final class TermsHash extends InvertedDocConsumer
/*     */ {
/*     */   final TermsHashConsumer consumer;
/*     */   final TermsHash nextTermsHash;
/*     */   final DocumentsWriter docWriter;
/*     */   boolean trackAllocations;
/*     */ 
/*     */   public TermsHash(DocumentsWriter docWriter, boolean trackAllocations, TermsHashConsumer consumer, TermsHash nextTermsHash)
/*     */   {
/*  44 */     this.docWriter = docWriter;
/*  45 */     this.consumer = consumer;
/*  46 */     this.nextTermsHash = nextTermsHash;
/*  47 */     this.trackAllocations = trackAllocations;
/*     */   }
/*     */ 
/*     */   InvertedDocConsumerPerThread addThread(DocInverterPerThread docInverterPerThread)
/*     */   {
/*  52 */     return new TermsHashPerThread(docInverterPerThread, this, this.nextTermsHash, null);
/*     */   }
/*     */ 
/*     */   TermsHashPerThread addThread(DocInverterPerThread docInverterPerThread, TermsHashPerThread primaryPerThread) {
/*  56 */     return new TermsHashPerThread(docInverterPerThread, this, this.nextTermsHash, primaryPerThread);
/*     */   }
/*     */ 
/*     */   void setFieldInfos(FieldInfos fieldInfos)
/*     */   {
/*  61 */     this.fieldInfos = fieldInfos;
/*  62 */     this.consumer.setFieldInfos(fieldInfos);
/*     */   }
/*     */ 
/*     */   public void abort()
/*     */   {
/*     */     try {
/*  68 */       this.consumer.abort();
/*     */     } finally {
/*  70 */       if (this.nextTermsHash != null)
/*  71 */         this.nextTermsHash.abort();
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void flush(Map<InvertedDocConsumerPerThread, Collection<InvertedDocConsumerPerField>> threadsAndFields, SegmentWriteState state)
/*     */     throws IOException
/*     */   {
/*  78 */     Map childThreadsAndFields = new HashMap();
/*     */     Map nextThreadsAndFields;
/*     */     Map nextThreadsAndFields;
/*  81 */     if (this.nextTermsHash != null)
/*  82 */       nextThreadsAndFields = new HashMap();
/*     */     else {
/*  84 */       nextThreadsAndFields = null;
/*     */     }
/*  86 */     for (Map.Entry entry : threadsAndFields.entrySet())
/*     */     {
/*  88 */       TermsHashPerThread perThread = (TermsHashPerThread)entry.getKey();
/*     */ 
/*  90 */       Collection fields = (Collection)entry.getValue();
/*     */ 
/*  92 */       Iterator fieldsIt = fields.iterator();
/*  93 */       Collection childFields = new HashSet();
/*     */       Collection nextChildFields;
/*     */       Collection nextChildFields;
/*  96 */       if (this.nextTermsHash != null)
/*  97 */         nextChildFields = new HashSet();
/*     */       else {
/*  99 */         nextChildFields = null;
/*     */       }
/* 101 */       while (fieldsIt.hasNext()) {
/* 102 */         TermsHashPerField perField = (TermsHashPerField)fieldsIt.next();
/* 103 */         childFields.add(perField.consumer);
/* 104 */         if (this.nextTermsHash != null) {
/* 105 */           nextChildFields.add(perField.nextPerField);
/*     */         }
/*     */       }
/* 108 */       childThreadsAndFields.put(perThread.consumer, childFields);
/* 109 */       if (this.nextTermsHash != null) {
/* 110 */         nextThreadsAndFields.put(perThread.nextPerThread, nextChildFields);
/*     */       }
/*     */     }
/* 113 */     this.consumer.flush(childThreadsAndFields, state);
/*     */ 
/* 115 */     if (this.nextTermsHash != null)
/* 116 */       this.nextTermsHash.flush(nextThreadsAndFields, state);
/*     */   }
/*     */ 
/*     */   public synchronized boolean freeRAM()
/*     */   {
/* 121 */     return false;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermsHash
 * JD-Core Version:    0.6.0
 */