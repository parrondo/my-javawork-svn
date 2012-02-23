/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.MultipleTermPositions;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermPositions;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class MultiPhraseQuery extends Query
/*     */ {
/*     */   private String field;
/*     */   private ArrayList<Term[]> termArrays;
/*     */   private ArrayList<Integer> positions;
/*     */   private int slop;
/*     */ 
/*     */   public MultiPhraseQuery()
/*     */   {
/*  43 */     this.termArrays = new ArrayList();
/*  44 */     this.positions = new ArrayList();
/*     */ 
/*  46 */     this.slop = 0;
/*     */   }
/*     */ 
/*     */   public void setSlop(int s)
/*     */   {
/*  51 */     this.slop = s;
/*     */   }
/*     */ 
/*     */   public int getSlop()
/*     */   {
/*  56 */     return this.slop;
/*     */   }
/*     */ 
/*     */   public void add(Term term)
/*     */   {
/*  61 */     add(new Term[] { term });
/*     */   }
/*     */ 
/*     */   public void add(Term[] terms)
/*     */   {
/*  69 */     int position = 0;
/*  70 */     if (this.positions.size() > 0) {
/*  71 */       position = ((Integer)this.positions.get(this.positions.size() - 1)).intValue() + 1;
/*     */     }
/*  73 */     add(terms, position);
/*     */   }
/*     */ 
/*     */   public void add(Term[] terms, int position)
/*     */   {
/*  84 */     if (this.termArrays.size() == 0) {
/*  85 */       this.field = terms[0].field();
/*     */     }
/*  87 */     for (int i = 0; i < terms.length; i++) {
/*  88 */       if (terms[i].field() != this.field) {
/*  89 */         throw new IllegalArgumentException("All phrase terms must be in the same field (" + this.field + "): " + terms[i]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  95 */     this.termArrays.add(terms);
/*  96 */     this.positions.add(Integer.valueOf(position));
/*     */   }
/*     */ 
/*     */   public List<Term[]> getTermArrays()
/*     */   {
/* 104 */     return Collections.unmodifiableList(this.termArrays);
/*     */   }
/*     */ 
/*     */   public int[] getPositions()
/*     */   {
/* 111 */     int[] result = new int[this.positions.size()];
/* 112 */     for (int i = 0; i < this.positions.size(); i++)
/* 113 */       result[i] = ((Integer)this.positions.get(i)).intValue();
/* 114 */     return result;
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/* 120 */     for (Term[] arr : this.termArrays)
/* 121 */       for (Term term : arr)
/* 122 */         terms.add(term);
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader)
/*     */   {
/* 301 */     if (this.termArrays.size() == 1) {
/* 302 */       Term[] terms = (Term[])this.termArrays.get(0);
/* 303 */       BooleanQuery boq = new BooleanQuery(true);
/* 304 */       for (int i = 0; i < terms.length; i++) {
/* 305 */         boq.add(new TermQuery(terms[i]), BooleanClause.Occur.SHOULD);
/*     */       }
/* 307 */       boq.setBoost(getBoost());
/* 308 */       return boq;
/*     */     }
/* 310 */     return this;
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher)
/*     */     throws IOException
/*     */   {
/* 316 */     return new MultiPhraseWeight(searcher);
/*     */   }
/*     */ 
/*     */   public final String toString(String f)
/*     */   {
/* 322 */     StringBuilder buffer = new StringBuilder();
/* 323 */     if ((this.field == null) || (!this.field.equals(f))) {
/* 324 */       buffer.append(this.field);
/* 325 */       buffer.append(":");
/*     */     }
/*     */ 
/* 328 */     buffer.append("\"");
/* 329 */     Iterator i = this.termArrays.iterator();
/* 330 */     while (i.hasNext()) {
/* 331 */       Term[] terms = (Term[])i.next();
/* 332 */       if (terms.length > 1) {
/* 333 */         buffer.append("(");
/* 334 */         for (int j = 0; j < terms.length; j++) {
/* 335 */           buffer.append(terms[j].text());
/* 336 */           if (j < terms.length - 1)
/* 337 */             buffer.append(" ");
/*     */         }
/* 339 */         buffer.append(")");
/*     */       } else {
/* 341 */         buffer.append(terms[0].text());
/*     */       }
/* 343 */       if (i.hasNext())
/* 344 */         buffer.append(" ");
/*     */     }
/* 346 */     buffer.append("\"");
/*     */ 
/* 348 */     if (this.slop != 0) {
/* 349 */       buffer.append("~");
/* 350 */       buffer.append(this.slop);
/*     */     }
/*     */ 
/* 353 */     buffer.append(ToStringUtils.boost(getBoost()));
/*     */ 
/* 355 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 362 */     if (!(o instanceof MultiPhraseQuery)) return false;
/* 363 */     MultiPhraseQuery other = (MultiPhraseQuery)o;
/* 364 */     return (getBoost() == other.getBoost()) && (this.slop == other.slop) && (termArraysEquals(this.termArrays, other.termArrays)) && (this.positions.equals(other.positions));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 373 */     return Float.floatToIntBits(getBoost()) ^ this.slop ^ termArraysHashCode() ^ this.positions.hashCode() ^ 0x4AC65113;
/*     */   }
/*     */ 
/*     */   private int termArraysHashCode()
/*     */   {
/* 382 */     int hashCode = 1;
/* 383 */     for (Term[] termArray : this.termArrays) {
/* 384 */       hashCode = 31 * hashCode + (termArray == null ? 0 : Arrays.hashCode(termArray));
/*     */     }
/*     */ 
/* 387 */     return hashCode;
/*     */   }
/*     */ 
/*     */   private boolean termArraysEquals(List<Term[]> termArrays1, List<Term[]> termArrays2)
/*     */   {
/* 392 */     if (termArrays1.size() != termArrays2.size()) {
/* 393 */       return false;
/*     */     }
/* 395 */     ListIterator iterator1 = termArrays1.listIterator();
/* 396 */     ListIterator iterator2 = termArrays2.listIterator();
/* 397 */     while (iterator1.hasNext()) {
/* 398 */       Term[] termArray1 = (Term[])iterator1.next();
/* 399 */       Term[] termArray2 = (Term[])iterator2.next();
/* 400 */       if (termArray1 == null ? termArray2 != null : !Arrays.equals(termArray1, termArray2))
/*     */       {
/* 402 */         return false;
/*     */       }
/*     */     }
/* 405 */     return true;
/*     */   }
/*     */ 
/*     */   private class MultiPhraseWeight extends Weight
/*     */   {
/*     */     private Similarity similarity;
/*     */     private float value;
/*     */     private final Explanation.IDFExplanation idfExp;
/*     */     private float idf;
/*     */     private float queryNorm;
/*     */     private float queryWeight;
/*     */ 
/*     */     public MultiPhraseWeight(Searcher searcher)
/*     */       throws IOException
/*     */     {
/* 138 */       this.similarity = MultiPhraseQuery.this.getSimilarity(searcher);
/*     */ 
/* 141 */       ArrayList allTerms = new ArrayList();
/* 142 */       for (Term[] terms : MultiPhraseQuery.this.termArrays) {
/* 143 */         for (Term term : terms) {
/* 144 */           allTerms.add(term);
/*     */         }
/*     */       }
/* 147 */       this.idfExp = this.similarity.idfExplain(allTerms, searcher);
/* 148 */       this.idf = this.idfExp.getIdf();
/*     */     }
/*     */ 
/*     */     public Query getQuery() {
/* 152 */       return MultiPhraseQuery.this;
/*     */     }
/*     */     public float getValue() {
/* 155 */       return this.value;
/*     */     }
/*     */ 
/*     */     public float sumOfSquaredWeights() {
/* 159 */       this.queryWeight = (this.idf * MultiPhraseQuery.this.getBoost());
/* 160 */       return this.queryWeight * this.queryWeight;
/*     */     }
/*     */ 
/*     */     public void normalize(float queryNorm)
/*     */     {
/* 165 */       this.queryNorm = queryNorm;
/* 166 */       this.queryWeight *= queryNorm;
/* 167 */       this.value = (this.queryWeight * this.idf);
/*     */     }
/*     */ 
/*     */     public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException
/*     */     {
/* 172 */       if (MultiPhraseQuery.this.termArrays.size() == 0) {
/* 173 */         return null;
/*     */       }
/* 175 */       PhraseQuery.PostingsAndFreq[] postingsFreqs = new PhraseQuery.PostingsAndFreq[MultiPhraseQuery.this.termArrays.size()];
/*     */ 
/* 177 */       for (int pos = 0; pos < postingsFreqs.length; pos++) {
/* 178 */         Term[] terms = (Term[])MultiPhraseQuery.this.termArrays.get(pos);
/*     */         TermPositions p;
/*     */         int docFreq;
/* 183 */         if (terms.length > 1) {
/* 184 */           TermPositions p = new MultipleTermPositions(reader, terms);
/*     */ 
/* 188 */           int docFreq = 0;
/* 189 */           for (int termIdx = 0; termIdx < terms.length; termIdx++)
/* 190 */             docFreq += reader.docFreq(terms[termIdx]);
/*     */         }
/*     */         else {
/* 193 */           p = reader.termPositions(terms[0]);
/* 194 */           docFreq = reader.docFreq(terms[0]);
/*     */ 
/* 196 */           if (p == null) {
/* 197 */             return null;
/*     */           }
/*     */         }
/* 200 */         postingsFreqs[pos] = new PhraseQuery.PostingsAndFreq(p, docFreq, ((Integer)MultiPhraseQuery.this.positions.get(pos)).intValue(), terms[0]);
/*     */       }
/*     */ 
/* 204 */       if (MultiPhraseQuery.this.slop == 0) {
/* 205 */         ArrayUtil.mergeSort(postingsFreqs);
/*     */       }
/*     */ 
/* 208 */       if (MultiPhraseQuery.this.slop == 0) {
/* 209 */         ExactPhraseScorer s = new ExactPhraseScorer(this, postingsFreqs, this.similarity, reader.norms(MultiPhraseQuery.this.field));
/*     */ 
/* 211 */         if (s.noDocs) {
/* 212 */           return null;
/*     */         }
/* 214 */         return s;
/*     */       }
/*     */ 
/* 217 */       return new SloppyPhraseScorer(this, postingsFreqs, this.similarity, MultiPhraseQuery.this.slop, reader.norms(MultiPhraseQuery.this.field));
/*     */     }
/*     */ 
/*     */     public Explanation explain(IndexReader reader, int doc)
/*     */       throws IOException
/*     */     {
/* 225 */       ComplexExplanation result = new ComplexExplanation();
/* 226 */       result.setDescription("weight(" + getQuery() + " in " + doc + "), product of:");
/*     */ 
/* 228 */       Explanation idfExpl = new Explanation(this.idf, "idf(" + MultiPhraseQuery.this.field + ":" + this.idfExp.explain() + ")");
/*     */ 
/* 231 */       Explanation queryExpl = new Explanation();
/* 232 */       queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");
/*     */ 
/* 234 */       Explanation boostExpl = new Explanation(MultiPhraseQuery.this.getBoost(), "boost");
/* 235 */       if (MultiPhraseQuery.this.getBoost() != 1.0F) {
/* 236 */         queryExpl.addDetail(boostExpl);
/*     */       }
/* 238 */       queryExpl.addDetail(idfExpl);
/*     */ 
/* 240 */       Explanation queryNormExpl = new Explanation(this.queryNorm, "queryNorm");
/* 241 */       queryExpl.addDetail(queryNormExpl);
/*     */ 
/* 243 */       queryExpl.setValue(boostExpl.getValue() * idfExpl.getValue() * queryNormExpl.getValue());
/*     */ 
/* 247 */       result.addDetail(queryExpl);
/*     */ 
/* 250 */       ComplexExplanation fieldExpl = new ComplexExplanation();
/* 251 */       fieldExpl.setDescription("fieldWeight(" + getQuery() + " in " + doc + "), product of:");
/*     */ 
/* 254 */       Scorer scorer = scorer(reader, true, false);
/* 255 */       if (scorer == null) {
/* 256 */         return new Explanation(0.0F, "no matching docs");
/*     */       }
/*     */ 
/* 259 */       Explanation tfExplanation = new Explanation();
/* 260 */       int d = scorer.advance(doc);
/*     */       float phraseFreq;
/*     */       float phraseFreq;
/* 262 */       if (d == doc)
/* 263 */         phraseFreq = scorer.freq();
/*     */       else {
/* 265 */         phraseFreq = 0.0F;
/*     */       }
/*     */ 
/* 268 */       tfExplanation.setValue(this.similarity.tf(phraseFreq));
/* 269 */       tfExplanation.setDescription("tf(phraseFreq=" + phraseFreq + ")");
/* 270 */       fieldExpl.addDetail(tfExplanation);
/* 271 */       fieldExpl.addDetail(idfExpl);
/*     */ 
/* 273 */       Explanation fieldNormExpl = new Explanation();
/* 274 */       byte[] fieldNorms = reader.norms(MultiPhraseQuery.this.field);
/* 275 */       float fieldNorm = fieldNorms != null ? this.similarity.decodeNormValue(fieldNorms[doc]) : 1.0F;
/*     */ 
/* 277 */       fieldNormExpl.setValue(fieldNorm);
/* 278 */       fieldNormExpl.setDescription("fieldNorm(field=" + MultiPhraseQuery.this.field + ", doc=" + doc + ")");
/* 279 */       fieldExpl.addDetail(fieldNormExpl);
/*     */ 
/* 281 */       fieldExpl.setMatch(Boolean.valueOf(tfExplanation.isMatch()));
/* 282 */       fieldExpl.setValue(tfExplanation.getValue() * idfExpl.getValue() * fieldNormExpl.getValue());
/*     */ 
/* 286 */       result.addDetail(fieldExpl);
/* 287 */       result.setMatch(fieldExpl.getMatch());
/*     */ 
/* 290 */       result.setValue(queryExpl.getValue() * fieldExpl.getValue());
/*     */ 
/* 292 */       if (queryExpl.getValue() == 1.0F) {
/* 293 */         return fieldExpl;
/*     */       }
/* 295 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.MultiPhraseQuery
 * JD-Core Version:    0.6.0
 */