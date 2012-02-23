/*     */ package org.apache.lucene.search.payloads;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermPositions;
/*     */ import org.apache.lucene.search.ComplexExplanation;
/*     */ import org.apache.lucene.search.Explanation;
/*     */ import org.apache.lucene.search.Scorer;
/*     */ import org.apache.lucene.search.Searcher;
/*     */ import org.apache.lucene.search.Similarity;
/*     */ import org.apache.lucene.search.Weight;
/*     */ import org.apache.lucene.search.spans.SpanQuery;
/*     */ import org.apache.lucene.search.spans.SpanScorer;
/*     */ import org.apache.lucene.search.spans.SpanTermQuery;
/*     */ import org.apache.lucene.search.spans.SpanWeight;
/*     */ import org.apache.lucene.search.spans.Spans;
/*     */ import org.apache.lucene.search.spans.TermSpans;
/*     */ 
/*     */ public class PayloadTermQuery extends SpanTermQuery
/*     */ {
/*     */   protected PayloadFunction function;
/*     */   private boolean includeSpanScore;
/*     */ 
/*     */   public PayloadTermQuery(Term term, PayloadFunction function)
/*     */   {
/*  53 */     this(term, function, true);
/*     */   }
/*     */ 
/*     */   public PayloadTermQuery(Term term, PayloadFunction function, boolean includeSpanScore)
/*     */   {
/*  58 */     super(term);
/*  59 */     this.function = function;
/*  60 */     this.includeSpanScore = includeSpanScore;
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher) throws IOException
/*     */   {
/*  65 */     return new PayloadTermWeight(this, searcher);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 194 */     int prime = 31;
/* 195 */     int result = super.hashCode();
/* 196 */     result = 31 * result + (this.function == null ? 0 : this.function.hashCode());
/* 197 */     result = 31 * result + (this.includeSpanScore ? 1231 : 1237);
/* 198 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 203 */     if (this == obj)
/* 204 */       return true;
/* 205 */     if (!super.equals(obj))
/* 206 */       return false;
/* 207 */     if (getClass() != obj.getClass())
/* 208 */       return false;
/* 209 */     PayloadTermQuery other = (PayloadTermQuery)obj;
/* 210 */     if (this.function == null) {
/* 211 */       if (other.function != null)
/* 212 */         return false;
/* 213 */     } else if (!this.function.equals(other.function)) {
/* 214 */       return false;
/*     */     }
/* 216 */     return this.includeSpanScore == other.includeSpanScore;
/*     */   }
/*     */ 
/*     */   protected class PayloadTermWeight extends SpanWeight
/*     */   {
/*     */     public PayloadTermWeight(PayloadTermQuery query, Searcher searcher)
/*     */       throws IOException
/*     */     {
/*  72 */       super(searcher);
/*     */     }
/*     */ 
/*     */     public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer)
/*     */       throws IOException
/*     */     {
/*  78 */       return new PayloadTermSpanScorer((TermSpans)this.query.getSpans(reader), this, this.similarity, reader.norms(this.query.getField()));
/*     */     }
/*     */     protected class PayloadTermSpanScorer extends SpanScorer {
/*  84 */       protected byte[] payload = new byte[256];
/*     */       protected TermPositions positions;
/*     */       protected float payloadScore;
/*     */       protected int payloadsSeen;
/*     */ 
/*  91 */       public PayloadTermSpanScorer(TermSpans spans, Weight weight, Similarity similarity, byte[] norms) throws IOException { super(weight, similarity, norms);
/*  92 */         this.positions = spans.getPositions(); }
/*     */ 
/*     */       protected boolean setFreqCurrentDoc()
/*     */         throws IOException
/*     */       {
/*  97 */         if (!this.more) {
/*  98 */           return false;
/*     */         }
/* 100 */         this.doc = this.spans.doc();
/* 101 */         this.freq = 0.0F;
/* 102 */         this.payloadScore = 0.0F;
/* 103 */         this.payloadsSeen = 0;
/* 104 */         Similarity similarity1 = getSimilarity();
/* 105 */         while ((this.more) && (this.doc == this.spans.doc())) {
/* 106 */           int matchLength = this.spans.end() - this.spans.start();
/*     */ 
/* 108 */           this.freq += similarity1.sloppyFreq(matchLength);
/* 109 */           processPayload(similarity1);
/*     */ 
/* 111 */           this.more = this.spans.next();
/*     */         }
/*     */ 
/* 114 */         return (this.more) || (this.freq != 0.0F);
/*     */       }
/*     */ 
/*     */       protected void processPayload(Similarity similarity) throws IOException {
/* 118 */         if (this.positions.isPayloadAvailable()) {
/* 119 */           this.payload = this.positions.getPayload(this.payload, 0);
/* 120 */           this.payloadScore = PayloadTermQuery.this.function.currentScore(this.doc, PayloadTermQuery.this.term.field(), this.spans.start(), this.spans.end(), this.payloadsSeen, this.payloadScore, similarity.scorePayload(this.doc, PayloadTermQuery.this.term.field(), this.spans.start(), this.spans.end(), this.payload, 0, this.positions.getPayloadLength()));
/*     */ 
/* 124 */           this.payloadsSeen += 1;
/*     */         }
/*     */       }
/*     */ 
/*     */       public float score()
/*     */         throws IOException
/*     */       {
/* 139 */         return PayloadTermQuery.this.includeSpanScore ? getSpanScore() * getPayloadScore() : getPayloadScore();
/*     */       }
/*     */ 
/*     */       protected float getSpanScore()
/*     */         throws IOException
/*     */       {
/* 154 */         return super.score();
/*     */       }
/*     */ 
/*     */       protected float getPayloadScore()
/*     */       {
/* 164 */         return PayloadTermQuery.this.function.docScore(this.doc, PayloadTermQuery.this.term.field(), this.payloadsSeen, this.payloadScore);
/*     */       }
/*     */ 
/*     */       protected Explanation explain(int doc) throws IOException
/*     */       {
/* 169 */         ComplexExplanation result = new ComplexExplanation();
/* 170 */         Explanation nonPayloadExpl = super.explain(doc);
/* 171 */         result.addDetail(nonPayloadExpl);
/*     */ 
/* 174 */         Explanation payloadBoost = new Explanation();
/* 175 */         result.addDetail(payloadBoost);
/*     */ 
/* 177 */         float payloadScore = getPayloadScore();
/* 178 */         payloadBoost.setValue(payloadScore);
/*     */ 
/* 181 */         payloadBoost.setDescription("scorePayload(...)");
/* 182 */         result.setValue(nonPayloadExpl.getValue() * payloadScore);
/* 183 */         result.setDescription("btq, product of:");
/* 184 */         result.setMatch(nonPayloadExpl.getValue() == 0.0F ? Boolean.FALSE : Boolean.TRUE);
/*     */ 
/* 186 */         return result;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.payloads.PayloadTermQuery
 * JD-Core Version:    0.6.0
 */