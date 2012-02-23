/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.search.Query;
/*     */ 
/*     */ public abstract class SpanPositionCheckQuery extends SpanQuery
/*     */   implements Cloneable
/*     */ {
/*     */   protected SpanQuery match;
/*     */ 
/*     */   public SpanPositionCheckQuery(SpanQuery match)
/*     */   {
/*  39 */     this.match = match;
/*     */   }
/*     */ 
/*     */   public SpanQuery getMatch()
/*     */   {
/*  46 */     return this.match;
/*     */   }
/*     */ 
/*     */   public String getField()
/*     */   {
/*  51 */     return this.match.getField();
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/*  57 */     this.match.extractTerms(terms);
/*     */   }
/*     */ 
/*     */   protected abstract AcceptStatus acceptPosition(Spans paramSpans)
/*     */     throws IOException;
/*     */ 
/*     */   public Spans getSpans(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  84 */     return new PositionCheckSpan(reader);
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  90 */     SpanPositionCheckQuery clone = null;
/*     */ 
/*  92 */     SpanQuery rewritten = (SpanQuery)this.match.rewrite(reader);
/*  93 */     if (rewritten != this.match) {
/*  94 */       clone = (SpanPositionCheckQuery)clone();
/*  95 */       clone.match = rewritten;
/*     */     }
/*     */ 
/*  98 */     if (clone != null) {
/*  99 */       return clone;
/*     */     }
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */   protected class PositionCheckSpan extends Spans {
/*     */     private Spans spans;
/*     */ 
/*     */     public PositionCheckSpan(IndexReader reader) throws IOException {
/* 109 */       this.spans = SpanPositionCheckQuery.this.match.getSpans(reader);
/*     */     }
/*     */ 
/*     */     public boolean next() throws IOException
/*     */     {
/* 114 */       if (!this.spans.next()) {
/* 115 */         return false;
/*     */       }
/* 117 */       return doNext();
/*     */     }
/*     */ 
/*     */     public boolean skipTo(int target) throws IOException
/*     */     {
/* 122 */       if (!this.spans.skipTo(target)) {
/* 123 */         return false;
/*     */       }
/* 125 */       return doNext();
/*     */     }
/*     */ 
/*     */     protected boolean doNext() throws IOException {
/*     */       while (true)
/* 130 */         switch (SpanPositionCheckQuery.1.$SwitchMap$org$apache$lucene$search$spans$SpanPositionCheckQuery$AcceptStatus[SpanPositionCheckQuery.this.acceptPosition(this).ordinal()]) { case 1:
/* 131 */           return true;
/*     */         case 2:
/* 133 */           if (this.spans.next()) break;
/* 134 */           return false;
/*     */         case 3:
/* 137 */           if (this.spans.skipTo(this.spans.doc() + 1)) break;
/* 138 */           return false;
/*     */         }
/*     */     }
/*     */ 
/*     */     public int doc()
/*     */     {
/* 145 */       return this.spans.doc();
/*     */     }
/*     */     public int start() {
/* 148 */       return this.spans.start();
/*     */     }
/*     */     public int end() {
/* 151 */       return this.spans.end();
/*     */     }
/*     */ 
/*     */     public Collection<byte[]> getPayload() throws IOException
/*     */     {
/* 156 */       ArrayList result = null;
/* 157 */       if (this.spans.isPayloadAvailable()) {
/* 158 */         result = new ArrayList(this.spans.getPayload());
/*     */       }
/* 160 */       return result;
/*     */     }
/*     */ 
/*     */     public boolean isPayloadAvailable()
/*     */     {
/* 166 */       return this.spans.isPayloadAvailable();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 171 */       return "spans(" + SpanPositionCheckQuery.this.toString() + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static enum AcceptStatus
/*     */   {
/*  64 */     YES, NO, NO_AND_ADVANCE;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanPositionCheckQuery
 * JD-Core Version:    0.6.0
 */