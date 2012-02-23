/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FilenameFilter;
/*    */ import java.util.HashSet;
/*    */ 
/*    */ public class IndexFileNameFilter
/*    */   implements FilenameFilter
/*    */ {
/* 31 */   private static IndexFileNameFilter singleton = new IndexFileNameFilter();
/*    */   private HashSet<String> extensions;
/*    */   private HashSet<String> extensionsInCFS;
/*    */ 
/*    */   private IndexFileNameFilter()
/*    */   {
/* 37 */     this.extensions = new HashSet();
/* 38 */     for (String ext : IndexFileNames.INDEX_EXTENSIONS) {
/* 39 */       this.extensions.add(ext);
/*    */     }
/* 41 */     this.extensionsInCFS = new HashSet();
/* 42 */     for (String ext : IndexFileNames.INDEX_EXTENSIONS_IN_COMPOUND_FILE)
/* 43 */       this.extensionsInCFS.add(ext);
/*    */   }
/*    */ 
/*    */   public boolean accept(File dir, String name)
/*    */   {
/* 51 */     int i = name.lastIndexOf('.');
/* 52 */     if (i != -1) {
/* 53 */       String extension = name.substring(1 + i);
/* 54 */       if (this.extensions.contains(extension))
/* 55 */         return true;
/* 56 */       if ((extension.startsWith("f")) && (extension.matches("f\\d+")))
/*    */       {
/* 58 */         return true;
/* 59 */       }if ((extension.startsWith("s")) && (extension.matches("s\\d+")))
/*    */       {
/* 61 */         return true;
/*    */       }
/*    */     } else {
/* 64 */       if (name.equals("deletable")) return true;
/* 65 */       if (name.startsWith("segments")) return true;
/*    */     }
/* 67 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isCFSFile(String name)
/*    */   {
/* 77 */     int i = name.lastIndexOf('.');
/* 78 */     if (i != -1) {
/* 79 */       String extension = name.substring(1 + i);
/* 80 */       if (this.extensionsInCFS.contains(extension)) {
/* 81 */         return true;
/*    */       }
/* 83 */       if ((extension.startsWith("f")) && (extension.matches("f\\d+")))
/*    */       {
/* 85 */         return true;
/*    */       }
/*    */     }
/* 88 */     return false;
/*    */   }
/*    */ 
/*    */   public static IndexFileNameFilter getFilter() {
/* 92 */     return singleton;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexFileNameFilter
 * JD-Core Version:    0.6.0
 */