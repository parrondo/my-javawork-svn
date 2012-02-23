/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ 
/*     */ public final class PerFieldAnalyzerWrapper extends Analyzer
/*     */ {
/*     */   private Analyzer defaultAnalyzer;
/*  49 */   private Map<String, Analyzer> analyzerMap = new HashMap();
/*     */ 
/*     */   public PerFieldAnalyzerWrapper(Analyzer defaultAnalyzer)
/*     */   {
/*  59 */     this(defaultAnalyzer, null);
/*     */   }
/*     */ 
/*     */   public PerFieldAnalyzerWrapper(Analyzer defaultAnalyzer, Map<String, Analyzer> fieldAnalyzers)
/*     */   {
/*  73 */     this.defaultAnalyzer = defaultAnalyzer;
/*  74 */     if (fieldAnalyzers != null)
/*  75 */       this.analyzerMap.putAll(fieldAnalyzers);
/*     */   }
/*     */ 
/*     */   public void addAnalyzer(String fieldName, Analyzer analyzer)
/*     */   {
/*  87 */     this.analyzerMap.put(fieldName, analyzer);
/*     */   }
/*     */ 
/*     */   public TokenStream tokenStream(String fieldName, Reader reader)
/*     */   {
/*  92 */     Analyzer analyzer = (Analyzer)this.analyzerMap.get(fieldName);
/*  93 */     if (analyzer == null) {
/*  94 */       analyzer = this.defaultAnalyzer;
/*     */     }
/*     */ 
/*  97 */     return analyzer.tokenStream(fieldName, reader);
/*     */   }
/*     */ 
/*     */   public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException
/*     */   {
/* 102 */     Analyzer analyzer = (Analyzer)this.analyzerMap.get(fieldName);
/* 103 */     if (analyzer == null) {
/* 104 */       analyzer = this.defaultAnalyzer;
/*     */     }
/* 106 */     return analyzer.reusableTokenStream(fieldName, reader);
/*     */   }
/*     */ 
/*     */   public int getPositionIncrementGap(String fieldName)
/*     */   {
/* 112 */     Analyzer analyzer = (Analyzer)this.analyzerMap.get(fieldName);
/* 113 */     if (analyzer == null)
/* 114 */       analyzer = this.defaultAnalyzer;
/* 115 */     return analyzer.getPositionIncrementGap(fieldName);
/*     */   }
/*     */ 
/*     */   public int getOffsetGap(Fieldable field)
/*     */   {
/* 121 */     Analyzer analyzer = (Analyzer)this.analyzerMap.get(field.name());
/* 122 */     if (analyzer == null)
/* 123 */       analyzer = this.defaultAnalyzer;
/* 124 */     return analyzer.getOffsetGap(field);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 129 */     return "PerFieldAnalyzerWrapper(" + this.analyzerMap + ", default=" + this.defaultAnalyzer + ")";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.PerFieldAnalyzerWrapper
 * JD-Core Version:    0.6.0
 */