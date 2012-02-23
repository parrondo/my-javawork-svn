/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.search.Explanation;
/*     */ import org.apache.lucene.search.Scorer;
/*     */ import org.apache.lucene.search.Similarity;
/*     */ import org.apache.lucene.search.Weight;
/*     */ 
/*     */ public class SpanScorer extends Scorer
/*     */ {
/*     */   protected Spans spans;
/*     */   protected byte[] norms;
/*     */   protected float value;
/*  35 */   protected boolean more = true;
/*     */   protected int doc;
/*     */   protected float freq;
/*     */ 
/*     */   protected SpanScorer(Spans spans, Weight weight, Similarity similarity, byte[] norms)
/*     */     throws IOException
/*     */   {
/*  42 */     super(similarity, weight);
/*  43 */     this.spans = spans;
/*  44 */     this.norms = norms;
/*  45 */     this.value = weight.getValue();
/*  46 */     if (this.spans.next()) {
/*  47 */       this.doc = -1;
/*     */     } else {
/*  49 */       this.doc = 2147483647;
/*  50 */       this.more = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int nextDoc() throws IOException
/*     */   {
/*  56 */     if (!setFreqCurrentDoc()) {
/*  57 */       this.doc = 2147483647;
/*     */     }
/*  59 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public int advance(int target) throws IOException
/*     */   {
/*  64 */     if (!this.more) {
/*  65 */       return this.doc = 2147483647;
/*     */     }
/*  67 */     if (this.spans.doc() < target) {
/*  68 */       this.more = this.spans.skipTo(target);
/*     */     }
/*  70 */     if (!setFreqCurrentDoc()) {
/*  71 */       this.doc = 2147483647;
/*     */     }
/*  73 */     return this.doc;
/*     */   }
/*     */ 
/*     */   protected boolean setFreqCurrentDoc() throws IOException {
/*  77 */     if (!this.more) {
/*  78 */       return false;
/*     */     }
/*  80 */     this.doc = this.spans.doc();
/*  81 */     this.freq = 0.0F;
/*     */     do {
/*  83 */       int matchLength = this.spans.end() - this.spans.start();
/*  84 */       this.freq += getSimilarity().sloppyFreq(matchLength);
/*  85 */       this.more = this.spans.next();
/*  86 */     }while ((this.more) && (this.doc == this.spans.doc()));
/*  87 */     return true;
/*     */   }
/*     */ 
/*     */   public int docID() {
/*  91 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public float score() throws IOException {
/*  95 */     float raw = getSimilarity().tf(this.freq) * this.value;
/*  96 */     return this.norms == null ? raw : raw * getSimilarity().decodeNormValue(this.norms[this.doc]);
/*     */   }
/*     */ 
/*     */   public float freq() throws IOException
/*     */   {
/* 101 */     return this.freq;
/*     */   }
/*     */ 
/*     */   protected Explanation explain(int doc)
/*     */     throws IOException
/*     */   {
/* 107 */     Explanation tfExplanation = new Explanation();
/*     */ 
/* 109 */     int expDoc = advance(doc);
/*     */ 
/* 111 */     float phraseFreq = expDoc == doc ? this.freq : 0.0F;
/* 112 */     tfExplanation.setValue(getSimilarity().tf(phraseFreq));
/* 113 */     tfExplanation.setDescription("tf(phraseFreq=" + phraseFreq + ")");
/*     */ 
/* 115 */     return tfExplanation;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanScorer
 * JD-Core Version:    0.6.0
 */