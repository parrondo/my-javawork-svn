/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collection;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.FSDirectory;
/*     */ import org.apache.lucene.util.Constants;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public final class IndexUpgrader
/*     */ {
/*     */   private final Directory dir;
/*     */   private final PrintStream infoStream;
/*     */   private final IndexWriterConfig iwc;
/*     */   private final boolean deletePriorCommits;
/*     */ 
/*     */   private static void printUsage()
/*     */   {
/*  54 */     System.err.println("Upgrades an index so all segments created with a previous Lucene version are rewritten.");
/*  55 */     System.err.println("Usage:");
/*  56 */     System.err.println("  java " + IndexUpgrader.class.getName() + " [-delete-prior-commits] [-verbose] indexDir");
/*  57 */     System.err.println("This tool keeps only the last commit in an index; for this");
/*  58 */     System.err.println("reason, if the incoming index has more than one commit, the tool");
/*  59 */     System.err.println("refuses to run by default. Specify -delete-prior-commits to override");
/*  60 */     System.err.println("this, allowing the tool to delete all but the last commit.");
/*  61 */     System.err.println("WARNING: This tool may reorder document IDs!");
/*  62 */     System.exit(1);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws IOException
/*     */   {
/*  67 */     String dir = null;
/*  68 */     boolean deletePriorCommits = false;
/*  69 */     PrintStream out = null;
/*  70 */     for (String arg : args) {
/*  71 */       if ("-delete-prior-commits".equals(arg))
/*  72 */         deletePriorCommits = true;
/*  73 */       else if ("-verbose".equals(arg))
/*  74 */         out = System.out;
/*  75 */       else if (dir == null)
/*  76 */         dir = arg;
/*     */       else {
/*  78 */         printUsage();
/*     */       }
/*     */     }
/*  81 */     if (dir == null) {
/*  82 */       printUsage();
/*     */     }
/*     */ 
/*  85 */     new IndexUpgrader(FSDirectory.open(new File(dir)), Version.LUCENE_CURRENT, out, deletePriorCommits).upgrade();
/*     */   }
/*     */ 
/*     */   public IndexUpgrader(Directory dir, Version matchVersion)
/*     */   {
/*  96 */     this(dir, new IndexWriterConfig(matchVersion, null), null, false);
/*     */   }
/*     */ 
/*     */   public IndexUpgrader(Directory dir, Version matchVersion, PrintStream infoStream, boolean deletePriorCommits)
/*     */   {
/* 103 */     this(dir, new IndexWriterConfig(matchVersion, null), infoStream, deletePriorCommits);
/*     */   }
/*     */ 
/*     */   public IndexUpgrader(Directory dir, IndexWriterConfig iwc, PrintStream infoStream, boolean deletePriorCommits)
/*     */   {
/* 110 */     this.dir = dir;
/* 111 */     this.iwc = iwc;
/* 112 */     this.infoStream = infoStream;
/* 113 */     this.deletePriorCommits = deletePriorCommits;
/*     */   }
/*     */ 
/*     */   public void upgrade() throws IOException {
/* 117 */     if (!IndexReader.indexExists(this.dir)) {
/* 118 */       throw new IndexNotFoundException(this.dir.toString());
/*     */     }
/*     */ 
/* 121 */     if (!this.deletePriorCommits) {
/* 122 */       Collection commits = IndexReader.listCommits(this.dir);
/* 123 */       if (commits.size() > 1) {
/* 124 */         throw new IllegalArgumentException("This tool was invoked to not delete prior commit points, but the following commits were found: " + commits);
/*     */       }
/*     */     }
/*     */ 
/* 128 */     IndexWriterConfig c = (IndexWriterConfig)this.iwc.clone();
/* 129 */     c.setMergePolicy(new UpgradeIndexMergePolicy(c.getMergePolicy()));
/* 130 */     c.setIndexDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy());
/*     */ 
/* 132 */     IndexWriter w = new IndexWriter(this.dir, c);
/*     */     try {
/* 134 */       w.setInfoStream(this.infoStream);
/* 135 */       w.message("Upgrading all pre-" + Constants.LUCENE_MAIN_VERSION + " segments of index directory '" + this.dir + "' to version " + Constants.LUCENE_MAIN_VERSION + "...");
/* 136 */       w.optimize();
/* 137 */       w.message("All segments upgraded to version " + Constants.LUCENE_MAIN_VERSION);
/*     */     } finally {
/* 139 */       w.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexUpgrader
 * JD-Core Version:    0.6.0
 */