/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class BooleanQuery extends Query
/*     */   implements Iterable<BooleanClause>
/*     */ {
/*  34 */   private static int maxClauseCount = 1024;
/*     */ 
/*  64 */   private ArrayList<BooleanClause> clauses = new ArrayList();
/*     */   private final boolean disableCoord;
/* 111 */   protected int minNrShouldMatch = 0;
/*     */ 
/*     */   public static int getMaxClauseCount()
/*     */   {
/*  52 */     return maxClauseCount;
/*     */   }
/*     */ 
/*     */   public static void setMaxClauseCount(int maxClauseCount)
/*     */   {
/*  59 */     if (maxClauseCount < 1)
/*  60 */       throw new IllegalArgumentException("maxClauseCount must be >= 1");
/*  61 */     maxClauseCount = maxClauseCount;
/*     */   }
/*     */ 
/*     */   public BooleanQuery()
/*     */   {
/*  69 */     this.disableCoord = false;
/*     */   }
/*     */ 
/*     */   public BooleanQuery(boolean disableCoord)
/*     */   {
/*  82 */     this.disableCoord = disableCoord;
/*     */   }
/*     */ 
/*     */   public boolean isCoordDisabled()
/*     */   {
/*  89 */     return this.disableCoord;
/*     */   }
/*     */ 
/*     */   public void setMinimumNumberShouldMatch(int min)
/*     */   {
/* 109 */     this.minNrShouldMatch = min;
/*     */   }
/*     */ 
/*     */   public int getMinimumNumberShouldMatch()
/*     */   {
/* 118 */     return this.minNrShouldMatch;
/*     */   }
/*     */ 
/*     */   public void add(Query query, BooleanClause.Occur occur)
/*     */   {
/* 127 */     add(new BooleanClause(query, occur));
/*     */   }
/*     */ 
/*     */   public void add(BooleanClause clause)
/*     */   {
/* 135 */     if (this.clauses.size() >= maxClauseCount) {
/* 136 */       throw new TooManyClauses();
/*     */     }
/* 138 */     this.clauses.add(clause);
/*     */   }
/*     */ 
/*     */   public BooleanClause[] getClauses()
/*     */   {
/* 143 */     return (BooleanClause[])this.clauses.toArray(new BooleanClause[this.clauses.size()]);
/*     */   }
/*     */ 
/*     */   public List<BooleanClause> clauses() {
/* 147 */     return this.clauses;
/*     */   }
/*     */ 
/*     */   public final Iterator<BooleanClause> iterator()
/*     */   {
/* 153 */     return clauses().iterator();
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher)
/*     */     throws IOException
/*     */   {
/* 354 */     return new BooleanWeight(searcher, this.disableCoord);
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader) throws IOException
/*     */   {
/* 359 */     if ((this.minNrShouldMatch == 0) && (this.clauses.size() == 1)) {
/* 360 */       BooleanClause c = (BooleanClause)this.clauses.get(0);
/* 361 */       if (!c.isProhibited())
/*     */       {
/* 363 */         Query query = c.getQuery().rewrite(reader);
/*     */ 
/* 365 */         if (getBoost() != 1.0F) {
/* 366 */           if (query == c.getQuery())
/* 367 */             query = (Query)query.clone();
/* 368 */           query.setBoost(getBoost() * query.getBoost());
/*     */         }
/*     */ 
/* 371 */         return query;
/*     */       }
/*     */     }
/*     */ 
/* 375 */     BooleanQuery clone = null;
/* 376 */     for (int i = 0; i < this.clauses.size(); i++) {
/* 377 */       BooleanClause c = (BooleanClause)this.clauses.get(i);
/* 378 */       Query query = c.getQuery().rewrite(reader);
/* 379 */       if (query != c.getQuery()) {
/* 380 */         if (clone == null)
/* 381 */           clone = (BooleanQuery)clone();
/* 382 */         clone.clauses.set(i, new BooleanClause(query, c.getOccur()));
/*     */       }
/*     */     }
/* 385 */     if (clone != null) {
/* 386 */       return clone;
/*     */     }
/* 388 */     return this;
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/* 394 */     for (BooleanClause clause : this.clauses)
/* 395 */       clause.getQuery().extractTerms(terms);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 401 */     BooleanQuery clone = (BooleanQuery)super.clone();
/* 402 */     clone.clauses = ((ArrayList)this.clauses.clone());
/* 403 */     return clone;
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 409 */     StringBuilder buffer = new StringBuilder();
/* 410 */     boolean needParens = (getBoost() != 1.0D) || (getMinimumNumberShouldMatch() > 0);
/* 411 */     if (needParens) {
/* 412 */       buffer.append("(");
/*     */     }
/*     */ 
/* 415 */     for (int i = 0; i < this.clauses.size(); i++) {
/* 416 */       BooleanClause c = (BooleanClause)this.clauses.get(i);
/* 417 */       if (c.isProhibited())
/* 418 */         buffer.append("-");
/* 419 */       else if (c.isRequired()) {
/* 420 */         buffer.append("+");
/*     */       }
/* 422 */       Query subQuery = c.getQuery();
/* 423 */       if (subQuery != null) {
/* 424 */         if ((subQuery instanceof BooleanQuery)) {
/* 425 */           buffer.append("(");
/* 426 */           buffer.append(subQuery.toString(field));
/* 427 */           buffer.append(")");
/*     */         } else {
/* 429 */           buffer.append(subQuery.toString(field));
/*     */         }
/*     */       }
/* 432 */       else buffer.append("null");
/*     */ 
/* 435 */       if (i != this.clauses.size() - 1) {
/* 436 */         buffer.append(" ");
/*     */       }
/*     */     }
/* 439 */     if (needParens) {
/* 440 */       buffer.append(")");
/*     */     }
/*     */ 
/* 443 */     if (getMinimumNumberShouldMatch() > 0) {
/* 444 */       buffer.append('~');
/* 445 */       buffer.append(getMinimumNumberShouldMatch());
/*     */     }
/*     */ 
/* 448 */     if (getBoost() != 1.0F)
/*     */     {
/* 450 */       buffer.append(ToStringUtils.boost(getBoost()));
/*     */     }
/*     */ 
/* 453 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 459 */     if (!(o instanceof BooleanQuery))
/* 460 */       return false;
/* 461 */     BooleanQuery other = (BooleanQuery)o;
/* 462 */     return (getBoost() == other.getBoost()) && (this.clauses.equals(other.clauses)) && (getMinimumNumberShouldMatch() == other.getMinimumNumberShouldMatch()) && (this.disableCoord == other.disableCoord);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 471 */     return Float.floatToIntBits(getBoost()) ^ this.clauses.hashCode() + getMinimumNumberShouldMatch() + (this.disableCoord ? 17 : 0);
/*     */   }
/*     */ 
/*     */   protected class BooleanWeight extends Weight
/*     */   {
/*     */     protected Similarity similarity;
/*     */     protected ArrayList<Weight> weights;
/*     */     protected int maxCoord;
/*     */     private final boolean disableCoord;
/*     */ 
/*     */     public BooleanWeight(Searcher searcher, boolean disableCoord)
/*     */       throws IOException
/*     */     {
/* 171 */       this.similarity = BooleanQuery.this.getSimilarity(searcher);
/* 172 */       this.disableCoord = disableCoord;
/* 173 */       this.weights = new ArrayList(BooleanQuery.this.clauses.size());
/* 174 */       for (int i = 0; i < BooleanQuery.this.clauses.size(); i++) {
/* 175 */         BooleanClause c = (BooleanClause)BooleanQuery.this.clauses.get(i);
/* 176 */         this.weights.add(c.getQuery().createWeight(searcher));
/* 177 */         if (c.isProhibited()) continue; this.maxCoord += 1;
/*     */       }
/*     */     }
/*     */ 
/*     */     public Query getQuery() {
/* 182 */       return BooleanQuery.this;
/*     */     }
/*     */     public float getValue() {
/* 185 */       return BooleanQuery.this.getBoost();
/*     */     }
/*     */ 
/*     */     public float sumOfSquaredWeights() throws IOException {
/* 189 */       float sum = 0.0F;
/* 190 */       for (int i = 0; i < this.weights.size(); i++)
/*     */       {
/* 192 */         float s = ((Weight)this.weights.get(i)).sumOfSquaredWeights();
/* 193 */         if (((BooleanClause)BooleanQuery.this.clauses.get(i)).isProhibited())
/*     */           continue;
/* 195 */         sum += s;
/*     */       }
/*     */ 
/* 198 */       sum *= BooleanQuery.this.getBoost() * BooleanQuery.this.getBoost();
/*     */ 
/* 200 */       return sum;
/*     */     }
/*     */ 
/*     */     public void normalize(float norm)
/*     */     {
/* 206 */       norm *= BooleanQuery.this.getBoost();
/* 207 */       for (Weight w : this.weights)
/*     */       {
/* 209 */         w.normalize(norm);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Explanation explain(IndexReader reader, int doc)
/*     */       throws IOException
/*     */     {
/* 216 */       int minShouldMatch = BooleanQuery.this.getMinimumNumberShouldMatch();
/*     */ 
/* 218 */       ComplexExplanation sumExpl = new ComplexExplanation();
/* 219 */       sumExpl.setDescription("sum of:");
/* 220 */       int coord = 0;
/* 221 */       float sum = 0.0F;
/* 222 */       boolean fail = false;
/* 223 */       int shouldMatchCount = 0;
/* 224 */       Iterator cIter = BooleanQuery.this.clauses.iterator();
/* 225 */       for (Iterator wIter = this.weights.iterator(); wIter.hasNext(); ) {
/* 226 */         Weight w = (Weight)wIter.next();
/* 227 */         BooleanClause c = (BooleanClause)cIter.next();
/* 228 */         if (w.scorer(reader, true, true) == null) {
/* 229 */           if (c.isRequired()) {
/* 230 */             fail = true;
/* 231 */             Explanation r = new Explanation(0.0F, "no match on required clause (" + c.getQuery().toString() + ")");
/* 232 */             sumExpl.addDetail(r);
/* 233 */             continue;
/*     */           }
/*     */         }
/* 236 */         Explanation e = w.explain(reader, doc);
/* 237 */         if (e.isMatch()) {
/* 238 */           if (!c.isProhibited()) {
/* 239 */             sumExpl.addDetail(e);
/* 240 */             sum += e.getValue();
/* 241 */             coord++;
/*     */           } else {
/* 243 */             Explanation r = new Explanation(0.0F, "match on prohibited clause (" + c.getQuery().toString() + ")");
/*     */ 
/* 245 */             r.addDetail(e);
/* 246 */             sumExpl.addDetail(r);
/* 247 */             fail = true;
/*     */           }
/* 249 */           if (c.getOccur() == BooleanClause.Occur.SHOULD)
/* 250 */             shouldMatchCount++;
/* 251 */         } else if (c.isRequired()) {
/* 252 */           Explanation r = new Explanation(0.0F, "no match on required clause (" + c.getQuery().toString() + ")");
/* 253 */           r.addDetail(e);
/* 254 */           sumExpl.addDetail(r);
/* 255 */           fail = true;
/*     */         }
/*     */       }
/* 258 */       if (fail) {
/* 259 */         sumExpl.setMatch(Boolean.FALSE);
/* 260 */         sumExpl.setValue(0.0F);
/* 261 */         sumExpl.setDescription("Failure to meet condition(s) of required/prohibited clause(s)");
/*     */ 
/* 263 */         return sumExpl;
/* 264 */       }if (shouldMatchCount < minShouldMatch) {
/* 265 */         sumExpl.setMatch(Boolean.FALSE);
/* 266 */         sumExpl.setValue(0.0F);
/* 267 */         sumExpl.setDescription("Failure to match minimum number of optional clauses: " + minShouldMatch);
/*     */ 
/* 269 */         return sumExpl;
/*     */       }
/*     */ 
/* 272 */       sumExpl.setMatch(0 < coord ? Boolean.TRUE : Boolean.FALSE);
/* 273 */       sumExpl.setValue(sum);
/*     */ 
/* 275 */       float coordFactor = this.disableCoord ? 1.0F : this.similarity.coord(coord, this.maxCoord);
/* 276 */       if (coordFactor == 1.0F) {
/* 277 */         return sumExpl;
/*     */       }
/* 279 */       ComplexExplanation result = new ComplexExplanation(sumExpl.isMatch(), sum * coordFactor, "product of:");
/*     */ 
/* 282 */       result.addDetail(sumExpl);
/* 283 */       result.addDetail(new Explanation(coordFactor, "coord(" + coord + "/" + this.maxCoord + ")"));
/*     */ 
/* 285 */       return result;
/*     */     }
/*     */ 
/*     */     public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer)
/*     */       throws IOException
/*     */     {
/* 292 */       List required = new ArrayList();
/* 293 */       List prohibited = new ArrayList();
/* 294 */       List optional = new ArrayList();
/* 295 */       Iterator cIter = BooleanQuery.this.clauses.iterator();
/* 296 */       for (Weight w : this.weights) {
/* 297 */         BooleanClause c = (BooleanClause)cIter.next();
/* 298 */         Scorer subScorer = w.scorer(reader, true, false);
/* 299 */         if (subScorer == null) {
/* 300 */           if (c.isRequired())
/* 301 */             return null;
/*     */         }
/* 303 */         else if (c.isRequired())
/* 304 */           required.add(subScorer);
/* 305 */         else if (c.isProhibited())
/* 306 */           prohibited.add(subScorer);
/*     */         else {
/* 308 */           optional.add(subScorer);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 313 */       if ((!scoreDocsInOrder) && (topScorer) && (required.size() == 0) && (prohibited.size() < 32)) {
/* 314 */         return new BooleanScorer(this, this.disableCoord, this.similarity, BooleanQuery.this.minNrShouldMatch, optional, prohibited, this.maxCoord);
/*     */       }
/*     */ 
/* 317 */       if ((required.size() == 0) && (optional.size() == 0))
/*     */       {
/* 319 */         return null;
/* 320 */       }if (optional.size() < BooleanQuery.this.minNrShouldMatch)
/*     */       {
/* 324 */         return null;
/*     */       }
/*     */ 
/* 328 */       return new BooleanScorer2(this, this.disableCoord, this.similarity, BooleanQuery.this.minNrShouldMatch, required, prohibited, optional, this.maxCoord);
/*     */     }
/*     */ 
/*     */     public boolean scoresDocsOutOfOrder()
/*     */     {
/* 333 */       int numProhibited = 0;
/* 334 */       for (BooleanClause c : BooleanQuery.this.clauses) {
/* 335 */         if (c.isRequired())
/* 336 */           return false;
/* 337 */         if (c.isProhibited()) {
/* 338 */           numProhibited++;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 343 */       return numProhibited <= 32;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class TooManyClauses extends RuntimeException
/*     */   {
/*     */     public TooManyClauses()
/*     */     {
/*  43 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.BooleanQuery
 * JD-Core Version:    0.6.0
 */