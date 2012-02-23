/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermPositions;
/*     */ 
/*     */ public class TermSpans extends Spans
/*     */ {
/*     */   protected TermPositions positions;
/*     */   protected Term term;
/*     */   protected int doc;
/*     */   protected int freq;
/*     */   protected int count;
/*     */   protected int position;
/*     */ 
/*     */   public TermSpans(TermPositions positions, Term term)
/*     */     throws IOException
/*     */   {
/*  41 */     this.positions = positions;
/*  42 */     this.term = term;
/*  43 */     this.doc = -1;
/*     */   }
/*     */ 
/*     */   public boolean next() throws IOException
/*     */   {
/*  48 */     if (this.count == this.freq) {
/*  49 */       if (!this.positions.next()) {
/*  50 */         this.doc = 2147483647;
/*  51 */         return false;
/*     */       }
/*  53 */       this.doc = this.positions.doc();
/*  54 */       this.freq = this.positions.freq();
/*  55 */       this.count = 0;
/*     */     }
/*  57 */     this.position = this.positions.nextPosition();
/*  58 */     this.count += 1;
/*  59 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean skipTo(int target) throws IOException
/*     */   {
/*  64 */     if (!this.positions.skipTo(target)) {
/*  65 */       this.doc = 2147483647;
/*  66 */       return false;
/*     */     }
/*     */ 
/*  69 */     this.doc = this.positions.doc();
/*  70 */     this.freq = this.positions.freq();
/*  71 */     this.count = 0;
/*     */ 
/*  73 */     this.position = this.positions.nextPosition();
/*  74 */     this.count += 1;
/*     */ 
/*  76 */     return true;
/*     */   }
/*     */ 
/*     */   public int doc()
/*     */   {
/*  81 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public int start()
/*     */   {
/*  86 */     return this.position;
/*     */   }
/*     */ 
/*     */   public int end()
/*     */   {
/*  91 */     return this.position + 1;
/*     */   }
/*     */ 
/*     */   public Collection<byte[]> getPayload()
/*     */     throws IOException
/*     */   {
/*  97 */     byte[] bytes = new byte[this.positions.getPayloadLength()];
/*  98 */     bytes = this.positions.getPayload(bytes, 0);
/*  99 */     return Collections.singletonList(bytes);
/*     */   }
/*     */ 
/*     */   public boolean isPayloadAvailable()
/*     */   {
/* 105 */     return this.positions.isPayloadAvailable();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 110 */     return "spans(" + this.term.toString() + ")@" + (this.doc == 2147483647 ? "END" : this.doc == -1 ? "START" : new StringBuilder().append(this.doc).append("-").append(this.position).toString());
/*     */   }
/*     */ 
/*     */   public TermPositions getPositions()
/*     */   {
/* 116 */     return this.positions;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.TermSpans
 * JD-Core Version:    0.6.0
 */