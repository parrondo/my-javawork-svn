/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ public class IndexFormatTooNewException extends CorruptIndexException
/*    */ {
/*    */   public IndexFormatTooNewException(String filename, int version, int minVersion, int maxVersion)
/*    */   {
/* 27 */     super("Format version is not supported" + (filename != null ? " in file '" + filename + "'" : "") + ": " + version + " (needs to be between " + minVersion + " and " + maxVersion + ")");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexFormatTooNewException
 * JD-Core Version:    0.6.0
 */