/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.index.CorruptIndexException;
/*    */ import org.apache.lucene.index.IndexFormatTooNewException;
/*    */ import org.apache.lucene.index.IndexFormatTooOldException;
/*    */ import org.apache.lucene.store.DataInput;
/*    */ import org.apache.lucene.store.DataOutput;
/*    */ 
/*    */ public final class CodecUtil
/*    */ {
/*    */   private static final int CODEC_MAGIC = 1071082519;
/*    */ 
/*    */   public static DataOutput writeHeader(DataOutput out, String codec, int version)
/*    */     throws IOException
/*    */   {
/* 40 */     BytesRef bytes = new BytesRef(codec);
/* 41 */     if ((bytes.length != codec.length()) || (bytes.length >= 128)) {
/* 42 */       throw new IllegalArgumentException("codec must be simple ASCII, less than 128 characters in length [got " + codec + "]");
/*    */     }
/* 44 */     out.writeInt(1071082519);
/* 45 */     out.writeString(codec);
/* 46 */     out.writeInt(version);
/*    */ 
/* 48 */     return out;
/*    */   }
/*    */ 
/*    */   public static int headerLength(String codec) {
/* 52 */     return 9 + codec.length();
/*    */   }
/*    */ 
/*    */   public static int checkHeader(DataInput in, String codec, int minVersion, int maxVersion)
/*    */     throws IOException
/*    */   {
/* 59 */     int actualHeader = in.readInt();
/* 60 */     if (actualHeader != 1071082519) {
/* 61 */       throw new CorruptIndexException("codec header mismatch: actual header=" + actualHeader + " vs expected header=" + 1071082519);
/*    */     }
/*    */ 
/* 64 */     String actualCodec = in.readString();
/* 65 */     if (!actualCodec.equals(codec)) {
/* 66 */       throw new CorruptIndexException("codec mismatch: actual codec=" + actualCodec + " vs expected codec=" + codec);
/*    */     }
/*    */ 
/* 69 */     int actualVersion = in.readInt();
/* 70 */     if (actualVersion < minVersion) {
/* 71 */       throw new IndexFormatTooOldException(null, actualVersion, minVersion, maxVersion);
/*    */     }
/* 73 */     if (actualVersion > maxVersion) {
/* 74 */       throw new IndexFormatTooNewException(null, actualVersion, minVersion, maxVersion);
/*    */     }
/*    */ 
/* 77 */     return actualVersion;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.CodecUtil
 * JD-Core Version:    0.6.0
 */