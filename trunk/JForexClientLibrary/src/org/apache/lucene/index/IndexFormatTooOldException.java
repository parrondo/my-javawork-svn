/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ public class IndexFormatTooOldException extends CorruptIndexException
/*    */ {
/*    */   public IndexFormatTooOldException(String filename, String version)
/*    */   {
/* 27 */     super("Format version is not supported" + (filename != null ? " in file '" + filename + "'" : "") + ": " + version + ". This version of Lucene only supports indexes created with release 3.0 and later.");
/*    */   }
/*    */ 
/*    */   public IndexFormatTooOldException(String filename, int version, int minVersion, int maxVersion)
/*    */   {
/* 32 */     super("Format version is not supported" + (filename != null ? " in file '" + filename + "'" : "") + ": " + version + " (needs to be between " + minVersion + " and " + maxVersion + "). This version of Lucene only supports indexes created with release 3.0 and later.");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexFormatTooOldException
 * JD-Core Version:    0.6.0
 */