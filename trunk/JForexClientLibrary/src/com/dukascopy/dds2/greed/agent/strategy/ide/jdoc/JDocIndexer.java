/*     */ package com.dukascopy.dds2.greed.agent.strategy.ide.jdoc;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.util.IOUtils;
/*     */ import com.dukascopy.dds2.greed.util.ResourceLoader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.util.List;
/*     */ import javax.swing.text.html.HTMLEditorKit.Parser;
/*     */ import javax.swing.text.html.HTMLEditorKit.ParserCallback;
/*     */ import org.apache.lucene.analysis.Analyzer;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.Field;
/*     */ import org.apache.lucene.document.Field.Index;
/*     */ import org.apache.lucene.document.Field.Store;
/*     */ import org.apache.lucene.document.Field.TermVector;
/*     */ import org.apache.lucene.index.IndexWriter;
/*     */ import org.apache.lucene.index.IndexWriterConfig;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.RAMDirectory;
/*     */ import org.apache.lucene.util.Version;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JDocIndexer
/*     */ {
/*  32 */   private static final Logger LOGGER = LoggerFactory.getLogger(JDocIndexer.class);
/*     */   public static final String JDOC_DIR = "rc/javadoc/";
/*     */   private Analyzer analyzer;
/*  36 */   private Directory directory = null;
/*     */ 
/*     */   public JDocIndexer(Analyzer analyzer) {
/*  39 */     this.analyzer = analyzer;
/*     */   }
/*     */ 
/*     */   public Directory getIndex() {
/*  43 */     if (this.directory == null) {
/*  44 */       this.directory = new RAMDirectory();
/*  45 */       IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_34, this.analyzer);
/*  46 */       IndexWriter indexWriter = null;
/*     */       try {
/*  48 */         indexWriter = new IndexWriter(this.directory, conf);
/*  49 */         List docs = ResourceLoader.getInstance().readTextResource("rc/javadoc/filelist.txt");
/*     */ 
/*  51 */         for (String filePath : docs) {
/*  52 */           LOGGER.debug(new StringBuilder().append("indexing ").append(filePath).toString());
/*  53 */           addDocument(indexWriter, new StringBuilder().append("rc/javadoc/").append(filePath).toString());
/*     */         }
/*     */       } catch (IOException e) {
/*  56 */         LOGGER.error(e.getMessage(), e);
/*     */       } finally {
/*  58 */         IOUtils.closeQuietly(indexWriter);
/*     */       }
/*     */     }
/*  61 */     return this.directory;
/*     */   }
/*     */ 
/*     */   private void addDocument(IndexWriter writer, String filePath)
/*     */   {
/*  66 */     if (!filePath.endsWith("html")) {
/*  67 */       return;
/*     */     }
/*     */ 
/*  70 */     Document doc = new Document();
/*  71 */     ParserGetter kit = new ParserGetter();
/*  72 */     HTMLEditorKit.Parser parser = kit.getParser();
/*     */ 
/*  74 */     String contents = "";
/*  75 */     InputStream in = null;
/*  76 */     StringReader r = null;
/*  77 */     StringWriter w = null;
/*     */     try {
/*  79 */       in = IOUtils.getResourceAsStream(filePath);
/*  80 */       contents = IOUtils.streamToString(in);
/*     */ 
/*  83 */       r = new StringReader(contents);
/*  84 */       w = new StringWriter();
/*  85 */       HTMLEditorKit.ParserCallback callback = new TagStripper(w);
/*     */ 
/*  87 */       parser.parse(r, callback, true);
/*  88 */       contents = w.toString();
/*     */ 
/*  90 */       doc.add(new Field("tv", contents, Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
/*     */ 
/*  92 */       doc.add(new Field("notv", contents, Field.Store.YES, Field.Index.NO));
/*  93 */       doc.add(new Field("filename", filePath, Field.Store.YES, Field.Index.NO));
/*  94 */       writer.addDocument(doc);
/*     */     }
/*     */     catch (IOException e) {
/*  97 */       LOGGER.error(e.getMessage(), e);
/*     */     } finally {
/*  99 */       IOUtils.closeQuietly(w);
/* 100 */       IOUtils.closeQuietly(r);
/* 101 */       IOUtils.closeQuietly(in);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String stripHeaderFooter(String content)
/*     */   {
/* 180 */     StringBuilder sb = new StringBuilder();
/*     */ 
/* 182 */     int hr1 = content.indexOf("<HR>");
/* 183 */     int hr2 = content.indexOf("<HR>", hr1 + 1);
/*     */ 
/* 185 */     int i = content.indexOf("<!-- ========= END OF CLASS DATA ========= -->");
/* 186 */     int hr3 = content.indexOf("<HR>", i);
/* 187 */     int hr4 = content.indexOf("<HR>", hr3 + 1);
/*     */ 
/* 189 */     if ((hr1 >= 0) && (hr2 >= 0) && (hr3 >= 0) && (hr4 >= 0) && (i >= 0)) {
/* 190 */       hr2 += 4;
/* 191 */       hr4 += 4;
/*     */ 
/* 193 */       sb.append(content.substring(0, hr1));
/* 194 */       sb.append(content.substring(hr2, hr3));
/* 195 */       sb.append(content.substring(hr4, content.length()));
/* 196 */       return sb.toString();
/*     */     }
/* 198 */     return content;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.jdoc.JDocIndexer
 * JD-Core Version:    0.6.0
 */