/*     */ package org.apache.lucene.queryParser;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.lucene.analysis.Analyzer;
/*     */ import org.apache.lucene.search.BooleanClause;
/*     */ import org.apache.lucene.search.BooleanClause.Occur;
/*     */ import org.apache.lucene.search.BooleanQuery;
/*     */ import org.apache.lucene.search.MultiPhraseQuery;
/*     */ import org.apache.lucene.search.PhraseQuery;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public class MultiFieldQueryParser extends QueryParser
/*     */ {
/*     */   protected String[] fields;
/*     */   protected Map<String, Float> boosts;
/*     */ 
/*     */   public MultiFieldQueryParser(Version matchVersion, String[] fields, Analyzer analyzer, Map<String, Float> boosts)
/*     */   {
/*  70 */     this(matchVersion, fields, analyzer);
/*  71 */     this.boosts = boosts;
/*     */   }
/*     */ 
/*     */   public MultiFieldQueryParser(Version matchVersion, String[] fields, Analyzer analyzer)
/*     */   {
/*  95 */     super(matchVersion, null, analyzer);
/*  96 */     this.fields = fields;
/*     */   }
/*     */ 
/*     */   protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException
/*     */   {
/* 101 */     if (field == null) {
/* 102 */       List clauses = new ArrayList();
/* 103 */       for (int i = 0; i < this.fields.length; i++) {
/* 104 */         Query q = super.getFieldQuery(this.fields[i], queryText, true);
/* 105 */         if (q == null)
/*     */           continue;
/* 107 */         if (this.boosts != null)
/*     */         {
/* 109 */           Float boost = (Float)this.boosts.get(this.fields[i]);
/* 110 */           if (boost != null) {
/* 111 */             q.setBoost(boost.floatValue());
/*     */           }
/*     */         }
/* 114 */         applySlop(q, slop);
/* 115 */         clauses.add(new BooleanClause(q, BooleanClause.Occur.SHOULD));
/*     */       }
/*     */ 
/* 118 */       if (clauses.size() == 0)
/* 119 */         return null;
/* 120 */       return getBooleanQuery(clauses, true);
/*     */     }
/* 122 */     Query q = super.getFieldQuery(field, queryText, true);
/* 123 */     applySlop(q, slop);
/* 124 */     return q;
/*     */   }
/*     */ 
/*     */   private void applySlop(Query q, int slop) {
/* 128 */     if ((q instanceof PhraseQuery))
/* 129 */       ((PhraseQuery)q).setSlop(slop);
/* 130 */     else if ((q instanceof MultiPhraseQuery))
/* 131 */       ((MultiPhraseQuery)q).setSlop(slop);
/*     */   }
/*     */ 
/*     */   protected Query getFieldQuery(String field, String queryText, boolean quoted)
/*     */     throws ParseException
/*     */   {
/* 138 */     if (field == null) {
/* 139 */       List clauses = new ArrayList();
/* 140 */       for (int i = 0; i < this.fields.length; i++) {
/* 141 */         Query q = super.getFieldQuery(this.fields[i], queryText, quoted);
/* 142 */         if (q == null)
/*     */           continue;
/* 144 */         if (this.boosts != null)
/*     */         {
/* 146 */           Float boost = (Float)this.boosts.get(this.fields[i]);
/* 147 */           if (boost != null) {
/* 148 */             q.setBoost(boost.floatValue());
/*     */           }
/*     */         }
/* 151 */         clauses.add(new BooleanClause(q, BooleanClause.Occur.SHOULD));
/*     */       }
/*     */ 
/* 154 */       if (clauses.size() == 0)
/* 155 */         return null;
/* 156 */       return getBooleanQuery(clauses, true);
/*     */     }
/* 158 */     Query q = super.getFieldQuery(field, queryText, quoted);
/* 159 */     return q;
/*     */   }
/*     */ 
/*     */   protected Query getFuzzyQuery(String field, String termStr, float minSimilarity)
/*     */     throws ParseException
/*     */   {
/* 166 */     if (field == null) {
/* 167 */       List clauses = new ArrayList();
/* 168 */       for (int i = 0; i < this.fields.length; i++) {
/* 169 */         clauses.add(new BooleanClause(getFuzzyQuery(this.fields[i], termStr, minSimilarity), BooleanClause.Occur.SHOULD));
/*     */       }
/*     */ 
/* 172 */       return getBooleanQuery(clauses, true);
/*     */     }
/* 174 */     return super.getFuzzyQuery(field, termStr, minSimilarity);
/*     */   }
/*     */ 
/*     */   protected Query getPrefixQuery(String field, String termStr)
/*     */     throws ParseException
/*     */   {
/* 180 */     if (field == null) {
/* 181 */       List clauses = new ArrayList();
/* 182 */       for (int i = 0; i < this.fields.length; i++) {
/* 183 */         clauses.add(new BooleanClause(getPrefixQuery(this.fields[i], termStr), BooleanClause.Occur.SHOULD));
/*     */       }
/*     */ 
/* 186 */       return getBooleanQuery(clauses, true);
/*     */     }
/* 188 */     return super.getPrefixQuery(field, termStr);
/*     */   }
/*     */ 
/*     */   protected Query getWildcardQuery(String field, String termStr) throws ParseException
/*     */   {
/* 193 */     if (field == null) {
/* 194 */       List clauses = new ArrayList();
/* 195 */       for (int i = 0; i < this.fields.length; i++) {
/* 196 */         clauses.add(new BooleanClause(getWildcardQuery(this.fields[i], termStr), BooleanClause.Occur.SHOULD));
/*     */       }
/*     */ 
/* 199 */       return getBooleanQuery(clauses, true);
/*     */     }
/* 201 */     return super.getWildcardQuery(field, termStr);
/*     */   }
/*     */ 
/*     */   protected Query getRangeQuery(String field, String part1, String part2, boolean inclusive)
/*     */     throws ParseException
/*     */   {
/* 207 */     if (field == null) {
/* 208 */       List clauses = new ArrayList();
/* 209 */       for (int i = 0; i < this.fields.length; i++) {
/* 210 */         clauses.add(new BooleanClause(getRangeQuery(this.fields[i], part1, part2, inclusive), BooleanClause.Occur.SHOULD));
/*     */       }
/*     */ 
/* 213 */       return getBooleanQuery(clauses, true);
/*     */     }
/* 215 */     return super.getRangeQuery(field, part1, part2, inclusive);
/*     */   }
/*     */ 
/*     */   public static Query parse(Version matchVersion, String[] queries, String[] fields, Analyzer analyzer)
/*     */     throws ParseException
/*     */   {
/* 238 */     if (queries.length != fields.length)
/* 239 */       throw new IllegalArgumentException("queries.length != fields.length");
/* 240 */     BooleanQuery bQuery = new BooleanQuery();
/* 241 */     for (int i = 0; i < fields.length; i++)
/*     */     {
/* 243 */       QueryParser qp = new QueryParser(matchVersion, fields[i], analyzer);
/* 244 */       Query q = qp.parse(queries[i]);
/* 245 */       if ((q == null) || (((q instanceof BooleanQuery)) && (((BooleanQuery)q).getClauses().length <= 0)))
/*     */         continue;
/* 247 */       bQuery.add(q, BooleanClause.Occur.SHOULD);
/*     */     }
/*     */ 
/* 250 */     return bQuery;
/*     */   }
/*     */ 
/*     */   public static Query parse(Version matchVersion, String query, String[] fields, BooleanClause.Occur[] flags, Analyzer analyzer)
/*     */     throws ParseException
/*     */   {
/* 286 */     if (fields.length != flags.length)
/* 287 */       throw new IllegalArgumentException("fields.length != flags.length");
/* 288 */     BooleanQuery bQuery = new BooleanQuery();
/* 289 */     for (int i = 0; i < fields.length; i++) {
/* 290 */       QueryParser qp = new QueryParser(matchVersion, fields[i], analyzer);
/* 291 */       Query q = qp.parse(query);
/* 292 */       if ((q == null) || (((q instanceof BooleanQuery)) && (((BooleanQuery)q).getClauses().length <= 0)))
/*     */         continue;
/* 294 */       bQuery.add(q, flags[i]);
/*     */     }
/*     */ 
/* 297 */     return bQuery;
/*     */   }
/*     */ 
/*     */   public static Query parse(Version matchVersion, String[] queries, String[] fields, BooleanClause.Occur[] flags, Analyzer analyzer)
/*     */     throws ParseException
/*     */   {
/* 335 */     if ((queries.length != fields.length) || (queries.length != flags.length))
/* 336 */       throw new IllegalArgumentException("queries, fields, and flags array have have different length");
/* 337 */     BooleanQuery bQuery = new BooleanQuery();
/* 338 */     for (int i = 0; i < fields.length; i++)
/*     */     {
/* 340 */       QueryParser qp = new QueryParser(matchVersion, fields[i], analyzer);
/* 341 */       Query q = qp.parse(queries[i]);
/* 342 */       if ((q == null) || (((q instanceof BooleanQuery)) && (((BooleanQuery)q).getClauses().length <= 0)))
/*     */         continue;
/* 344 */       bQuery.add(q, flags[i]);
/*     */     }
/*     */ 
/* 347 */     return bQuery;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.queryParser.MultiFieldQueryParser
 * JD-Core Version:    0.6.0
 */