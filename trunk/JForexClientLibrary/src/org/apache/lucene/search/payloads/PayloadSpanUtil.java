/*     */ package org.apache.lucene.search.payloads;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.search.BooleanClause;
/*     */ import org.apache.lucene.search.BooleanQuery;
/*     */ import org.apache.lucene.search.DisjunctionMaxQuery;
/*     */ import org.apache.lucene.search.FilteredQuery;
/*     */ import org.apache.lucene.search.MultiPhraseQuery;
/*     */ import org.apache.lucene.search.PhraseQuery;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.search.TermQuery;
/*     */ import org.apache.lucene.search.spans.SpanNearQuery;
/*     */ import org.apache.lucene.search.spans.SpanOrQuery;
/*     */ import org.apache.lucene.search.spans.SpanQuery;
/*     */ import org.apache.lucene.search.spans.SpanTermQuery;
/*     */ import org.apache.lucene.search.spans.Spans;
/*     */ 
/*     */ public class PayloadSpanUtil
/*     */ {
/*     */   private IndexReader reader;
/*     */ 
/*     */   public PayloadSpanUtil(IndexReader reader)
/*     */   {
/*  58 */     this.reader = reader;
/*     */   }
/*     */ 
/*     */   public Collection<byte[]> getPayloadsForQuery(Query query)
/*     */     throws IOException
/*     */   {
/*  69 */     Collection payloads = new ArrayList();
/*  70 */     queryToSpanQuery(query, payloads);
/*  71 */     return payloads;
/*     */   }
/*     */ 
/*     */   private void queryToSpanQuery(Query query, Collection<byte[]> payloads) throws IOException
/*     */   {
/*  76 */     if ((query instanceof BooleanQuery)) {
/*  77 */       BooleanClause[] queryClauses = ((BooleanQuery)query).getClauses();
/*     */ 
/*  79 */       for (int i = 0; i < queryClauses.length; i++) {
/*  80 */         if (!queryClauses[i].isProhibited()) {
/*  81 */           queryToSpanQuery(queryClauses[i].getQuery(), payloads);
/*     */         }
/*     */       }
/*     */     }
/*  85 */     else if ((query instanceof PhraseQuery)) {
/*  86 */       Term[] phraseQueryTerms = ((PhraseQuery)query).getTerms();
/*  87 */       SpanQuery[] clauses = new SpanQuery[phraseQueryTerms.length];
/*  88 */       for (int i = 0; i < phraseQueryTerms.length; i++) {
/*  89 */         clauses[i] = new SpanTermQuery(phraseQueryTerms[i]);
/*     */       }
/*     */ 
/*  92 */       int slop = ((PhraseQuery)query).getSlop();
/*  93 */       boolean inorder = false;
/*     */ 
/*  95 */       if (slop == 0) {
/*  96 */         inorder = true;
/*     */       }
/*     */ 
/*  99 */       SpanNearQuery sp = new SpanNearQuery(clauses, slop, inorder);
/* 100 */       sp.setBoost(query.getBoost());
/* 101 */       getPayloads(payloads, sp);
/* 102 */     } else if ((query instanceof TermQuery)) {
/* 103 */       SpanTermQuery stq = new SpanTermQuery(((TermQuery)query).getTerm());
/* 104 */       stq.setBoost(query.getBoost());
/* 105 */       getPayloads(payloads, stq);
/* 106 */     } else if ((query instanceof SpanQuery)) {
/* 107 */       getPayloads(payloads, (SpanQuery)query);
/* 108 */     } else if ((query instanceof FilteredQuery)) {
/* 109 */       queryToSpanQuery(((FilteredQuery)query).getQuery(), payloads);
/* 110 */     } else if ((query instanceof DisjunctionMaxQuery))
/*     */     {
/* 112 */       Iterator iterator = ((DisjunctionMaxQuery)query).iterator();
/* 113 */       while (iterator.hasNext()) {
/* 114 */         queryToSpanQuery((Query)iterator.next(), payloads);
/*     */       }
/*     */     }
/* 117 */     else if ((query instanceof MultiPhraseQuery)) {
/* 118 */       MultiPhraseQuery mpq = (MultiPhraseQuery)query;
/* 119 */       List termArrays = mpq.getTermArrays();
/* 120 */       int[] positions = mpq.getPositions();
/* 121 */       if (positions.length > 0)
/*     */       {
/* 123 */         int maxPosition = positions[(positions.length - 1)];
/* 124 */         for (int i = 0; i < positions.length - 1; i++) {
/* 125 */           if (positions[i] > maxPosition) {
/* 126 */             maxPosition = positions[i];
/*     */           }
/*     */         }
/*     */ 
/* 130 */         List[] disjunctLists = new List[maxPosition + 1];
/* 131 */         int distinctPositions = 0;
/*     */ 
/* 133 */         for (int i = 0; i < termArrays.size(); i++) {
/* 134 */           Term[] termArray = (Term[])termArrays.get(i);
/* 135 */           List disjuncts = disjunctLists[positions[i]];
/* 136 */           if (disjuncts == null) {
/* 137 */             disjuncts = disjunctLists[positions[i]] =  = new ArrayList(termArray.length);
/*     */ 
/* 139 */             distinctPositions++;
/*     */           }
/* 141 */           for (Term term : termArray) {
/* 142 */             disjuncts.add(new SpanTermQuery(term));
/*     */           }
/*     */         }
/*     */ 
/* 146 */         int positionGaps = 0;
/* 147 */         int position = 0;
/* 148 */         SpanQuery[] clauses = new SpanQuery[distinctPositions];
/* 149 */         for (int i = 0; i < disjunctLists.length; i++) {
/* 150 */           List disjuncts = disjunctLists[i];
/* 151 */           if (disjuncts != null) {
/* 152 */             clauses[(position++)] = new SpanOrQuery((SpanQuery[])disjuncts.toArray(new SpanQuery[disjuncts.size()]));
/*     */           }
/*     */           else {
/* 155 */             positionGaps++;
/*     */           }
/*     */         }
/*     */ 
/* 159 */         int slop = mpq.getSlop();
/* 160 */         boolean inorder = slop == 0;
/*     */ 
/* 162 */         SpanNearQuery sp = new SpanNearQuery(clauses, slop + positionGaps, inorder);
/*     */ 
/* 164 */         sp.setBoost(query.getBoost());
/* 165 */         getPayloads(payloads, sp);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void getPayloads(Collection<byte[]> payloads, SpanQuery query) throws IOException
/*     */   {
/* 172 */     Spans spans = query.getSpans(this.reader);
/*     */ 
/* 174 */     while (spans.next() == true)
/* 175 */       if (spans.isPayloadAvailable()) {
/* 176 */         Collection payload = spans.getPayload();
/* 177 */         for (byte[] bytes : payload)
/* 178 */           payloads.add(bytes);
/*     */       }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.payloads.PayloadSpanUtil
 * JD-Core Version:    0.6.0
 */