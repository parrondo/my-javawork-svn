/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public final class IndexFileNames
/*     */ {
/*     */   public static final String SEGMENTS = "segments";
/*     */   public static final String SEGMENTS_GEN = "segments.gen";
/*     */   public static final String DELETABLE = "deletable";
/*     */   public static final String NORMS_EXTENSION = "nrm";
/*     */   public static final String FREQ_EXTENSION = "frq";
/*     */   public static final String PROX_EXTENSION = "prx";
/*     */   public static final String TERMS_EXTENSION = "tis";
/*     */   public static final String TERMS_INDEX_EXTENSION = "tii";
/*     */   public static final String FIELDS_INDEX_EXTENSION = "fdx";
/*     */   public static final String FIELDS_EXTENSION = "fdt";
/*     */   public static final String VECTORS_FIELDS_EXTENSION = "tvf";
/*     */   public static final String VECTORS_DOCUMENTS_EXTENSION = "tvd";
/*     */   public static final String VECTORS_INDEX_EXTENSION = "tvx";
/*     */   public static final String COMPOUND_FILE_EXTENSION = "cfs";
/*     */   public static final String COMPOUND_FILE_STORE_EXTENSION = "cfx";
/*     */   public static final String DELETES_EXTENSION = "del";
/*     */   public static final String FIELD_INFOS_EXTENSION = "fnm";
/*     */   public static final String PLAIN_NORMS_EXTENSION = "f";
/*     */   public static final String SEPARATE_NORMS_EXTENSION = "s";
/*     */   public static final String GEN_EXTENSION = "gen";
/* 104 */   public static final String[] INDEX_EXTENSIONS = { "cfs", "fnm", "fdx", "fdt", "tii", "tis", "frq", "prx", "del", "tvx", "tvd", "tvf", "gen", "nrm", "cfx" };
/*     */ 
/* 124 */   public static final String[] INDEX_EXTENSIONS_IN_COMPOUND_FILE = { "fnm", "fdx", "fdt", "tii", "tis", "frq", "prx", "tvx", "tvd", "tvf", "nrm" };
/*     */ 
/* 138 */   public static final String[] STORE_INDEX_EXTENSIONS = { "tvx", "tvf", "tvd", "fdx", "fdt" };
/*     */ 
/* 146 */   public static final String[] NON_STORE_INDEX_EXTENSIONS = { "fnm", "frq", "prx", "tis", "tii", "nrm" };
/*     */ 
/* 156 */   public static final String[] COMPOUND_EXTENSIONS = { "fnm", "frq", "prx", "fdx", "fdt", "tii", "tis" };
/*     */ 
/* 167 */   public static final String[] VECTOR_EXTENSIONS = { "tvx", "tvd", "tvf" };
/*     */ 
/*     */   public static final String fileNameFromGeneration(String base, String ext, long gen)
/*     */   {
/* 186 */     if (gen == -1L)
/* 187 */       return null;
/* 188 */     if (gen == 0L) {
/* 189 */       return segmentFileName(base, ext);
/*     */     }
/*     */ 
/* 194 */     StringBuilder res = new StringBuilder(base.length() + 6 + ext.length()).append(base).append('_').append(Long.toString(gen, 36));
/*     */ 
/* 196 */     if (ext.length() > 0) {
/* 197 */       res.append('.').append(ext);
/*     */     }
/* 199 */     return res.toString();
/*     */   }
/*     */ 
/*     */   public static final boolean isDocStoreFile(String fileName)
/*     */   {
/* 208 */     if (fileName.endsWith("cfx"))
/* 209 */       return true;
/* 210 */     for (String ext : STORE_INDEX_EXTENSIONS) {
/* 211 */       if (fileName.endsWith(ext))
/* 212 */         return true;
/*     */     }
/* 214 */     return false;
/*     */   }
/*     */ 
/*     */   public static final String segmentFileName(String segmentName, String ext)
/*     */   {
/* 226 */     if (ext.length() > 0) {
/* 227 */       return segmentName.length() + 1 + ext.length() + segmentName + '.' + ext;
/*     */     }
/*     */ 
/* 230 */     return segmentName;
/*     */   }
/*     */ 
/*     */   public static final boolean matchesExtension(String filename, String ext)
/*     */   {
/* 241 */     return filename.endsWith("." + ext);
/*     */   }
/*     */ 
/*     */   public static final String stripSegmentName(String filename)
/*     */   {
/* 255 */     int idx = filename.indexOf('_', 1);
/* 256 */     if (idx == -1)
/*     */     {
/* 258 */       idx = filename.indexOf('.');
/*     */     }
/* 260 */     if (idx != -1) {
/* 261 */       filename = filename.substring(idx);
/*     */     }
/* 263 */     return filename;
/*     */   }
/*     */ 
/*     */   public static boolean isSeparateNormsFile(String filename)
/*     */   {
/* 271 */     int idx = filename.lastIndexOf('.');
/* 272 */     if (idx == -1) return false;
/* 273 */     String ext = filename.substring(idx + 1);
/* 274 */     return Pattern.matches("s[0-9]+", ext);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexFileNames
 * JD-Core Version:    0.6.0
 */