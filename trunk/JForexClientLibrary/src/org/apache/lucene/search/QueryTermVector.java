/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.lucene.analysis.Analyzer;
/*     */ import org.apache.lucene.analysis.TokenStream;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ import org.apache.lucene.index.TermFreqVector;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ 
/*     */ public class QueryTermVector
/*     */   implements TermFreqVector
/*     */ {
/*  40 */   private String[] terms = new String[0];
/*  41 */   private int[] termFreqs = new int[0];
/*     */ 
/*  43 */   public String getField() { return null;
/*     */   }
/*     */ 
/*     */   public QueryTermVector(String[] queryTerms)
/*     */   {
/*  51 */     processTerms(queryTerms);
/*     */   }
/*     */ 
/*     */   public QueryTermVector(String queryString, Analyzer analyzer) {
/*  55 */     if (analyzer != null) {
/*     */       TokenStream stream;
/*     */       try {
/*  59 */         stream = analyzer.reusableTokenStream("", new StringReader(queryString));
/*     */       } catch (IOException e1) {
/*  61 */         stream = null;
/*     */       }
/*  63 */       if (stream != null)
/*     */       {
/*  65 */         List terms = new ArrayList();
/*     */         try {
/*  67 */           boolean hasMoreTokens = false;
/*     */ 
/*  69 */           stream.reset();
/*  70 */           CharTermAttribute termAtt = (CharTermAttribute)stream.addAttribute(CharTermAttribute.class);
/*     */ 
/*  72 */           hasMoreTokens = stream.incrementToken();
/*  73 */           while (hasMoreTokens) {
/*  74 */             terms.add(termAtt.toString());
/*  75 */             hasMoreTokens = stream.incrementToken();
/*     */           }
/*  77 */           processTerms((String[])terms.toArray(new String[terms.size()]));
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processTerms(String[] queryTerms)
/*     */   {
/*     */     int i;
/*  85 */     if (queryTerms != null) {
/*  86 */       ArrayUtil.quickSort(queryTerms);
/*  87 */       Map tmpSet = new HashMap(queryTerms.length);
/*     */ 
/*  89 */       List tmpList = new ArrayList(queryTerms.length);
/*  90 */       List tmpFreqs = new ArrayList(queryTerms.length);
/*  91 */       int j = 0;
/*  92 */       for (int i = 0; i < queryTerms.length; i++) {
/*  93 */         String term = queryTerms[i];
/*  94 */         Integer position = (Integer)tmpSet.get(term);
/*  95 */         if (position == null) {
/*  96 */           tmpSet.put(term, Integer.valueOf(j++));
/*  97 */           tmpList.add(term);
/*  98 */           tmpFreqs.add(Integer.valueOf(1));
/*     */         }
/*     */         else {
/* 101 */           Integer integer = (Integer)tmpFreqs.get(position.intValue());
/* 102 */           tmpFreqs.set(position.intValue(), Integer.valueOf(integer.intValue() + 1));
/*     */         }
/*     */       }
/* 105 */       this.terms = ((String[])tmpList.toArray(this.terms));
/*     */ 
/* 107 */       this.termFreqs = new int[tmpFreqs.size()];
/* 108 */       i = 0;
/* 109 */       for (Integer integer : tmpFreqs)
/* 110 */         this.termFreqs[(i++)] = integer.intValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 117 */     StringBuilder sb = new StringBuilder();
/* 118 */     sb.append('{');
/* 119 */     for (int i = 0; i < this.terms.length; i++) {
/* 120 */       if (i > 0) sb.append(", ");
/* 121 */       sb.append(this.terms[i]).append('/').append(this.termFreqs[i]);
/*     */     }
/* 123 */     sb.append('}');
/* 124 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 129 */     return this.terms.length;
/*     */   }
/*     */ 
/*     */   public String[] getTerms() {
/* 133 */     return this.terms;
/*     */   }
/*     */ 
/*     */   public int[] getTermFrequencies() {
/* 137 */     return this.termFreqs;
/*     */   }
/*     */ 
/*     */   public int indexOf(String term) {
/* 141 */     int res = Arrays.binarySearch(this.terms, term);
/* 142 */     return res >= 0 ? res : -1;
/*     */   }
/*     */ 
/*     */   public int[] indexesOf(String[] terms, int start, int len) {
/* 146 */     int[] res = new int[len];
/*     */ 
/* 148 */     for (int i = 0; i < len; i++) {
/* 149 */       res[i] = indexOf(terms[i]);
/*     */     }
/* 151 */     return res;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.QueryTermVector
 * JD-Core Version:    0.6.0
 */