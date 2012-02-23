/*     */ package com.dukascopy.dds2.greed.agent.strategy.ide.jdoc;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorFileHandler;
/*     */ import com.dukascopy.dds2.greed.util.IOUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.analysis.Analyzer;
/*     */ import org.apache.lucene.analysis.TokenStream;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.queryParser.ParseException;
/*     */ import org.apache.lucene.queryParser.QueryParser;
/*     */ import org.apache.lucene.search.IndexSearcher;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.search.ScoreDoc;
/*     */ import org.apache.lucene.search.TopDocs;
/*     */ import org.apache.lucene.search.highlight.Highlighter;
/*     */ import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
/*     */ import org.apache.lucene.search.highlight.QueryScorer;
/*     */ import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
/*     */ import org.apache.lucene.search.highlight.TextFragment;
/*     */ import org.apache.lucene.search.highlight.TokenSources;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.util.Version;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JDocSrch
/*     */ {
/*  36 */   private static final Logger LOGGER = LoggerFactory.getLogger(EditorFileHandler.class);
/*     */ 
/*  38 */   private Directory index = null;
/*     */   private Analyzer analyzer;
/*     */   private boolean isGenerating;
/*  41 */   private List<JDocIndexFinishListener> editorPanelList = new LinkedList();
/*     */ 
/*  43 */   private static JDocSrch jdocSrch = new JDocSrch();
/*     */ 
/*     */   public static JDocSrch getInstance()
/*     */   {
/*  49 */     return jdocSrch;
/*     */   }
/*     */ 
/*     */   public static void initialize(JDocIndexFinishListener listener) {
/*  53 */     getInstance().generateIndex(listener);
/*     */   }
/*     */ 
/*     */   public synchronized void generateIndex(JDocIndexFinishListener listener)
/*     */   {
/*  58 */     if (!this.isGenerating) {
/*  59 */       if (this.index == null) {
/*  60 */         this.isGenerating = true;
/*  61 */         this.editorPanelList.add(listener);
/*  62 */         new GenerateIndex(this).start();
/*     */       } else {
/*  64 */         listener.fire();
/*     */       }
/*     */     }
/*  67 */     else this.editorPanelList.add(listener);
/*     */   }
/*     */ 
/*     */   private synchronized void endGeneration()
/*     */   {
/*  72 */     for (JDocIndexFinishListener listener : this.editorPanelList) {
/*  73 */       listener.fire();
/*     */     }
/*  75 */     this.editorPanelList.clear();
/*  76 */     this.isGenerating = false;
/*     */   }
/*     */ 
/*     */   public List<JDocSrchResult> srch(String querystr)
/*     */   {
/* 102 */     List result = new ArrayList(10);
/*     */ 
/* 104 */     if (this.index == null) {
/* 105 */       LOGGER.error("Index unavailable.");
/* 106 */       return null;
/*     */     }
/* 108 */     if ((querystr == null) || (querystr.length() == 0)) {
/* 109 */       return result;
/*     */     }
/*     */ 
/* 112 */     IndexSearcher searcher = null;
/*     */     try
/*     */     {
/* 115 */       Query q = new QueryParser(Version.LUCENE_34, "tv", this.analyzer).parse(querystr);
/*     */ 
/* 117 */       hitsPerPage = 1000;
/*     */ 
/* 122 */       searcher = new IndexSearcher(this.index);
/* 123 */       TopDocs hits = searcher.search(q, hitsPerPage);
/*     */ 
/* 137 */       SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<span style=\"background:#FFCC33; color:black;\"><B>", "</B></span>");
/* 138 */       Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(q));
/* 139 */       JDocSrchResult bestMatch = null;
/*     */ 
/* 141 */       for (int i = 0; i < hits.totalHits; i++) {
/* 142 */         int id = hits.scoreDocs[i].doc;
/* 143 */         Document doc = searcher.doc(id);
/* 144 */         String text = doc.get("notv");
/*     */         try
/*     */         {
/* 147 */           TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), id, "notv", this.analyzer);
/* 148 */           TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 100);
/*     */ 
/* 150 */           String filePath = null;
/* 151 */           for (int j = 0; j < frag.length; j++) {
/* 152 */             if ((frag[j] == null) || (frag[j].getScore() <= 0.0F))
/*     */               continue;
/* 154 */             if (doc.get("filename").equals(filePath)) {
/*     */               continue;
/*     */             }
/* 157 */             filePath = doc.get("filename");
/* 158 */             String fileName = new File(filePath).getName();
/* 159 */             fileName = fileName.substring(0, fileName.length() - 5);
/*     */ 
/* 161 */             if ((fileName.toUpperCase().equals(querystr.toUpperCase())) || (("I" + querystr.toUpperCase()).equals(fileName.toUpperCase()))) {
/* 162 */               bestMatch = new JDocSrchResult(frag[j].toString(), fileName, filePath, querystr);
/*     */             }
/* 164 */             else if (fileName.toUpperCase().contains(querystr.toUpperCase())) {
/* 165 */               result.add(0, new JDocSrchResult(frag[j].toString(), fileName, filePath, querystr));
/*     */             }
/*     */             else {
/* 168 */               result.add(new JDocSrchResult(frag[j].toString(), fileName, filePath, querystr));
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (InvalidTokenOffsetsException e)
/*     */         {
/* 186 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/*     */ 
/* 190 */       if (bestMatch != null)
/* 191 */         result.add(0, bestMatch);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 195 */       LOGGER.error(e.getMessage(), e);
/* 196 */       hitsPerPage = null;
/*     */       return hitsPerPage;
/*     */     }
/*     */     catch (ParseException e)
/*     */     {
/* 198 */       LOGGER.error(e.getMessage(), e);
/* 199 */       int hitsPerPage = null;
/*     */       return hitsPerPage; } finally { IOUtils.closeQuietly(searcher);
/*     */     }
/* 204 */     return result;
/*     */   }
/*     */ 
/*     */   private class GenerateIndex extends Thread
/*     */   {
/*     */     JDocSrch jdocSrch;
/*     */ 
/*     */     GenerateIndex(JDocSrch jdocSrch)
/*     */     {
/*  83 */       this.jdocSrch = jdocSrch;
/*     */     }
/*     */ 
/*     */     public void run() {
/*     */       try {
/*  88 */         JDocSrch.access$002(JDocSrch.this, new JDocAnalyzer());
/*  89 */         JDocIndexer indexer = new JDocIndexer(JDocSrch.this.analyzer);
/*     */ 
/*  91 */         JDocSrch.access$102(JDocSrch.this, indexer.getIndex());
/*  92 */         if (JDocSrch.this.index == null)
/*  93 */           JDocSrch.LOGGER.error("Unable to create javadoc search index.");
/*     */       }
/*     */       finally {
/*  96 */         this.jdocSrch.endGeneration();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.jdoc.JDocSrch
 * JD-Core Version:    0.6.0
 */