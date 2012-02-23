/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermPositions;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class PhraseQuery extends Query
/*     */ {
/*     */   private String field;
/*  38 */   private ArrayList<Term> terms = new ArrayList(4);
/*  39 */   private ArrayList<Integer> positions = new ArrayList(4);
/*  40 */   private int maxPosition = 0;
/*  41 */   private int slop = 0;
/*     */ 
/*     */   public void setSlop(int s)
/*     */   {
/*  60 */     this.slop = s;
/*     */   }
/*  62 */   public int getSlop() { return this.slop;
/*     */   }
/*     */ 
/*     */   public void add(Term term)
/*     */   {
/*  69 */     int position = 0;
/*  70 */     if (this.positions.size() > 0) {
/*  71 */       position = ((Integer)this.positions.get(this.positions.size() - 1)).intValue() + 1;
/*     */     }
/*  73 */     add(term, position);
/*     */   }
/*     */ 
/*     */   public void add(Term term, int position)
/*     */   {
/*  86 */     if (this.terms.size() == 0)
/*  87 */       this.field = term.field();
/*  88 */     else if (term.field() != this.field) {
/*  89 */       throw new IllegalArgumentException("All phrase terms must be in the same field: " + term);
/*     */     }
/*  91 */     this.terms.add(term);
/*  92 */     this.positions.add(Integer.valueOf(position));
/*  93 */     if (position > this.maxPosition) this.maxPosition = position;
/*     */   }
/*     */ 
/*     */   public Term[] getTerms()
/*     */   {
/*  98 */     return (Term[])this.terms.toArray(new Term[0]);
/*     */   }
/*     */ 
/*     */   public int[] getPositions()
/*     */   {
/* 105 */     int[] result = new int[this.positions.size()];
/* 106 */     for (int i = 0; i < this.positions.size(); i++)
/* 107 */       result[i] = ((Integer)this.positions.get(i)).intValue();
/* 108 */     return result;
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader) throws IOException
/*     */   {
/* 113 */     if (this.terms.size() == 1) {
/* 114 */       TermQuery tq = new TermQuery((Term)this.terms.get(0));
/* 115 */       tq.setBoost(getBoost());
/* 116 */       return tq;
/*     */     }
/* 118 */     return super.rewrite(reader);
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher)
/*     */     throws IOException
/*     */   {
/* 331 */     if (this.terms.size() == 1) {
/* 332 */       Term term = (Term)this.terms.get(0);
/* 333 */       Query termQuery = new TermQuery(term);
/* 334 */       termQuery.setBoost(getBoost());
/* 335 */       return termQuery.createWeight(searcher);
/*     */     }
/* 337 */     return new PhraseWeight(searcher);
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> queryTerms)
/*     */   {
/* 345 */     queryTerms.addAll(this.terms);
/*     */   }
/*     */ 
/*     */   public String toString(String f)
/*     */   {
/* 351 */     StringBuilder buffer = new StringBuilder();
/* 352 */     if ((this.field != null) && (!this.field.equals(f))) {
/* 353 */       buffer.append(this.field);
/* 354 */       buffer.append(":");
/*     */     }
/*     */ 
/* 357 */     buffer.append("\"");
/* 358 */     String[] pieces = new String[this.maxPosition + 1];
/* 359 */     for (int i = 0; i < this.terms.size(); i++) {
/* 360 */       int pos = ((Integer)this.positions.get(i)).intValue();
/* 361 */       String s = pieces[pos];
/* 362 */       if (s == null)
/* 363 */         s = ((Term)this.terms.get(i)).text();
/*     */       else {
/* 365 */         s = s + "|" + ((Term)this.terms.get(i)).text();
/*     */       }
/* 367 */       pieces[pos] = s;
/*     */     }
/* 369 */     for (int i = 0; i < pieces.length; i++) {
/* 370 */       if (i > 0) {
/* 371 */         buffer.append(' ');
/*     */       }
/* 373 */       String s = pieces[i];
/* 374 */       if (s == null)
/* 375 */         buffer.append('?');
/*     */       else {
/* 377 */         buffer.append(s);
/*     */       }
/*     */     }
/* 380 */     buffer.append("\"");
/*     */ 
/* 382 */     if (this.slop != 0) {
/* 383 */       buffer.append("~");
/* 384 */       buffer.append(this.slop);
/*     */     }
/*     */ 
/* 387 */     buffer.append(ToStringUtils.boost(getBoost()));
/*     */ 
/* 389 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 395 */     if (!(o instanceof PhraseQuery))
/* 396 */       return false;
/* 397 */     PhraseQuery other = (PhraseQuery)o;
/* 398 */     return (getBoost() == other.getBoost()) && (this.slop == other.slop) && (this.terms.equals(other.terms)) && (this.positions.equals(other.positions));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 407 */     return Float.floatToIntBits(getBoost()) ^ this.slop ^ this.terms.hashCode() ^ this.positions.hashCode();
/*     */   }
/*     */ 
/*     */   private class PhraseWeight extends Weight
/*     */   {
/*     */     private final Similarity similarity;
/*     */     private float value;
/*     */     private float idf;
/*     */     private float queryNorm;
/*     */     private float queryWeight;
/*     */     private Explanation.IDFExplanation idfExp;
/*     */ 
/*     */     public PhraseWeight(Searcher searcher)
/*     */       throws IOException
/*     */     {
/* 179 */       this.similarity = PhraseQuery.this.getSimilarity(searcher);
/*     */ 
/* 181 */       this.idfExp = this.similarity.idfExplain(PhraseQuery.this.terms, searcher);
/* 182 */       this.idf = this.idfExp.getIdf();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 186 */       return "weight(" + PhraseQuery.this + ")";
/*     */     }
/*     */     public Query getQuery() {
/* 189 */       return PhraseQuery.this;
/*     */     }
/*     */     public float getValue() {
/* 192 */       return this.value;
/*     */     }
/*     */ 
/*     */     public float sumOfSquaredWeights() {
/* 196 */       this.queryWeight = (this.idf * PhraseQuery.this.getBoost());
/* 197 */       return this.queryWeight * this.queryWeight;
/*     */     }
/*     */ 
/*     */     public void normalize(float queryNorm)
/*     */     {
/* 202 */       this.queryNorm = queryNorm;
/* 203 */       this.queryWeight *= queryNorm;
/* 204 */       this.value = (this.queryWeight * this.idf);
/*     */     }
/*     */ 
/*     */     public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException
/*     */     {
/* 209 */       if (PhraseQuery.this.terms.size() == 0) {
/* 210 */         return null;
/*     */       }
/* 212 */       PhraseQuery.PostingsAndFreq[] postingsFreqs = new PhraseQuery.PostingsAndFreq[PhraseQuery.this.terms.size()];
/* 213 */       for (int i = 0; i < PhraseQuery.this.terms.size(); i++) {
/* 214 */         Term t = (Term)PhraseQuery.this.terms.get(i);
/* 215 */         TermPositions p = reader.termPositions(t);
/* 216 */         if (p == null)
/* 217 */           return null;
/* 218 */         postingsFreqs[i] = new PhraseQuery.PostingsAndFreq(p, reader.docFreq(t), ((Integer)PhraseQuery.this.positions.get(i)).intValue(), t);
/*     */       }
/*     */ 
/* 222 */       if (PhraseQuery.this.slop == 0) {
/* 223 */         ArrayUtil.mergeSort(postingsFreqs);
/*     */       }
/*     */ 
/* 226 */       if (PhraseQuery.this.slop == 0) {
/* 227 */         ExactPhraseScorer s = new ExactPhraseScorer(this, postingsFreqs, this.similarity, reader.norms(PhraseQuery.this.field));
/*     */ 
/* 229 */         if (s.noDocs) {
/* 230 */           return null;
/*     */         }
/* 232 */         return s;
/*     */       }
/*     */ 
/* 235 */       return new SloppyPhraseScorer(this, postingsFreqs, this.similarity, PhraseQuery.this.slop, reader.norms(PhraseQuery.this.field));
/*     */     }
/*     */ 
/*     */     public Explanation explain(IndexReader reader, int doc)
/*     */       throws IOException
/*     */     {
/* 245 */       ComplexExplanation result = new ComplexExplanation();
/* 246 */       result.setDescription("weight(" + getQuery() + " in " + doc + "), product of:");
/*     */ 
/* 248 */       StringBuilder docFreqs = new StringBuilder();
/* 249 */       StringBuilder query = new StringBuilder();
/* 250 */       query.append('"');
/* 251 */       docFreqs.append(this.idfExp.explain());
/* 252 */       for (int i = 0; i < PhraseQuery.this.terms.size(); i++) {
/* 253 */         if (i != 0) {
/* 254 */           query.append(" ");
/*     */         }
/*     */ 
/* 257 */         Term term = (Term)PhraseQuery.this.terms.get(i);
/*     */ 
/* 259 */         query.append(term.text());
/*     */       }
/* 261 */       query.append('"');
/*     */ 
/* 263 */       Explanation idfExpl = new Explanation(this.idf, "idf(" + PhraseQuery.this.field + ":" + docFreqs + ")");
/*     */ 
/* 267 */       Explanation queryExpl = new Explanation();
/* 268 */       queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");
/*     */ 
/* 270 */       Explanation boostExpl = new Explanation(PhraseQuery.this.getBoost(), "boost");
/* 271 */       if (PhraseQuery.this.getBoost() != 1.0F)
/* 272 */         queryExpl.addDetail(boostExpl);
/* 273 */       queryExpl.addDetail(idfExpl);
/*     */ 
/* 275 */       Explanation queryNormExpl = new Explanation(this.queryNorm, "queryNorm");
/* 276 */       queryExpl.addDetail(queryNormExpl);
/*     */ 
/* 278 */       queryExpl.setValue(boostExpl.getValue() * idfExpl.getValue() * queryNormExpl.getValue());
/*     */ 
/* 282 */       result.addDetail(queryExpl);
/*     */ 
/* 285 */       Explanation fieldExpl = new Explanation();
/* 286 */       fieldExpl.setDescription("fieldWeight(" + PhraseQuery.this.field + ":" + query + " in " + doc + "), product of:");
/*     */ 
/* 289 */       Scorer scorer = scorer(reader, true, false);
/* 290 */       if (scorer == null) {
/* 291 */         return new Explanation(0.0F, "no matching docs");
/*     */       }
/* 293 */       Explanation tfExplanation = new Explanation();
/* 294 */       int d = scorer.advance(doc);
/*     */       float phraseFreq;
/*     */       float phraseFreq;
/* 296 */       if (d == doc)
/* 297 */         phraseFreq = scorer.freq();
/*     */       else {
/* 299 */         phraseFreq = 0.0F;
/*     */       }
/*     */ 
/* 302 */       tfExplanation.setValue(this.similarity.tf(phraseFreq));
/* 303 */       tfExplanation.setDescription("tf(phraseFreq=" + phraseFreq + ")");
/*     */ 
/* 305 */       fieldExpl.addDetail(tfExplanation);
/* 306 */       fieldExpl.addDetail(idfExpl);
/*     */ 
/* 308 */       Explanation fieldNormExpl = new Explanation();
/* 309 */       byte[] fieldNorms = reader.norms(PhraseQuery.this.field);
/* 310 */       float fieldNorm = fieldNorms != null ? this.similarity.decodeNormValue(fieldNorms[doc]) : 1.0F;
/*     */ 
/* 312 */       fieldNormExpl.setValue(fieldNorm);
/* 313 */       fieldNormExpl.setDescription("fieldNorm(field=" + PhraseQuery.this.field + ", doc=" + doc + ")");
/* 314 */       fieldExpl.addDetail(fieldNormExpl);
/*     */ 
/* 316 */       fieldExpl.setValue(tfExplanation.getValue() * idfExpl.getValue() * fieldNormExpl.getValue());
/*     */ 
/* 320 */       result.addDetail(fieldExpl);
/*     */ 
/* 323 */       result.setValue(queryExpl.getValue() * fieldExpl.getValue());
/* 324 */       result.setMatch(Boolean.valueOf(tfExplanation.isMatch()));
/* 325 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class PostingsAndFreq
/*     */     implements Comparable<PostingsAndFreq>
/*     */   {
/*     */     final TermPositions postings;
/*     */     final int docFreq;
/*     */     final int position;
/*     */     final Term term;
/*     */ 
/*     */     public PostingsAndFreq(TermPositions postings, int docFreq, int position, Term term)
/*     */     {
/* 128 */       this.postings = postings;
/* 129 */       this.docFreq = docFreq;
/* 130 */       this.position = position;
/* 131 */       this.term = term;
/*     */     }
/*     */ 
/*     */     public int compareTo(PostingsAndFreq other) {
/* 135 */       if (this.docFreq == other.docFreq) {
/* 136 */         if (this.position == other.position) {
/* 137 */           return this.term.compareTo(other.term);
/*     */         }
/* 139 */         return this.position - other.position;
/*     */       }
/* 141 */       return this.docFreq - other.docFreq;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 146 */       int prime = 31;
/* 147 */       int result = 1;
/* 148 */       result = 31 * result + this.docFreq;
/* 149 */       result = 31 * result + this.position;
/* 150 */       result = 31 * result + (this.term == null ? 0 : this.term.hashCode());
/* 151 */       return result;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 156 */       if (this == obj) return true;
/* 157 */       if (obj == null) return false;
/* 158 */       if (getClass() != obj.getClass()) return false;
/* 159 */       PostingsAndFreq other = (PostingsAndFreq)obj;
/* 160 */       if (this.docFreq != other.docFreq) return false;
/* 161 */       if (this.position != other.position) return false;
/* 162 */       if (this.term == null) {
/* 163 */         if (other.term != null) return false; 
/*     */       }
/* 164 */       else if (!this.term.equals(other.term)) return false;
/* 165 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.PhraseQuery
 * JD-Core Version:    0.6.0
 */