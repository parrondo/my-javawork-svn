/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ 
/*     */ public final class CompoundFileWriter
/*     */ {
/*     */   static final int FORMAT_PRE_VERSION = 0;
/*     */   static final int FORMAT_NO_SEGMENT_PREFIX = -1;
/*     */   static final int FORMAT_CURRENT = -1;
/*     */   private Directory directory;
/*     */   private String fileName;
/*     */   private HashSet<String> ids;
/*     */   private LinkedList<FileEntry> entries;
/*  83 */   private boolean merged = false;
/*     */   private SegmentMerger.CheckAbort checkAbort;
/*     */ 
/*     */   public CompoundFileWriter(Directory dir, String name)
/*     */   {
/*  91 */     this(dir, name, null);
/*     */   }
/*     */ 
/*     */   CompoundFileWriter(Directory dir, String name, SegmentMerger.CheckAbort checkAbort) {
/*  95 */     if (dir == null)
/*  96 */       throw new NullPointerException("directory cannot be null");
/*  97 */     if (name == null)
/*  98 */       throw new NullPointerException("name cannot be null");
/*  99 */     this.checkAbort = checkAbort;
/* 100 */     this.directory = dir;
/* 101 */     this.fileName = name;
/* 102 */     this.ids = new HashSet();
/* 103 */     this.entries = new LinkedList();
/*     */   }
/*     */ 
/*     */   public Directory getDirectory()
/*     */   {
/* 108 */     return this.directory;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 113 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public void addFile(String file)
/*     */   {
/* 125 */     addFile(file, this.directory);
/*     */   }
/*     */ 
/*     */   public void addFile(String file, Directory dir)
/*     */   {
/* 133 */     if (this.merged) {
/* 134 */       throw new IllegalStateException("Can't add extensions after merge has been called");
/*     */     }
/*     */ 
/* 137 */     if (file == null) {
/* 138 */       throw new NullPointerException("file cannot be null");
/*     */     }
/*     */ 
/* 141 */     if (!this.ids.add(file)) {
/* 142 */       throw new IllegalArgumentException("File " + file + " already added");
/*     */     }
/*     */ 
/* 145 */     FileEntry entry = new FileEntry(null);
/* 146 */     entry.file = file;
/* 147 */     entry.dir = dir;
/* 148 */     this.entries.add(entry);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 158 */     if (this.merged) {
/* 159 */       throw new IllegalStateException("Merge already performed");
/*     */     }
/* 161 */     if (this.entries.isEmpty()) {
/* 162 */       throw new IllegalStateException("No entries to merge have been defined");
/*     */     }
/* 164 */     this.merged = true;
/*     */ 
/* 167 */     IndexOutput os = this.directory.createOutput(this.fileName);
/* 168 */     IOException priorException = null;
/*     */     try
/*     */     {
/* 172 */       os.writeVInt(-1);
/*     */ 
/* 175 */       os.writeVInt(this.entries.size());
/*     */ 
/* 180 */       long totalSize = 0L;
/* 181 */       for (FileEntry fe : this.entries) {
/* 182 */         fe.directoryOffset = os.getFilePointer();
/* 183 */         os.writeLong(0L);
/* 184 */         os.writeString(IndexFileNames.stripSegmentName(fe.file));
/* 185 */         totalSize += fe.dir.fileLength(fe.file);
/*     */       }
/*     */ 
/* 194 */       long finalLength = totalSize + os.getFilePointer();
/* 195 */       os.setLength(finalLength);
/*     */ 
/* 199 */       for (FileEntry fe : this.entries) {
/* 200 */         fe.dataOffset = os.getFilePointer();
/* 201 */         copyFile(fe, os);
/*     */       }
/*     */ 
/* 205 */       for (FileEntry fe : this.entries) {
/* 206 */         os.seek(fe.directoryOffset);
/* 207 */         os.writeLong(fe.dataOffset);
/*     */       }
/*     */ 
/* 210 */       assert (finalLength == os.length());
/*     */ 
/* 216 */       IndexOutput tmp = os;
/* 217 */       os = null;
/* 218 */       tmp.close();
/*     */     } catch (IOException e) {
/* 220 */       priorException = e;
/*     */     } finally {
/* 222 */       IOUtils.closeWhileHandlingException(priorException, new Closeable[] { os });
/*     */     }
/*     */   }
/*     */ 
/*     */   private void copyFile(FileEntry source, IndexOutput os)
/*     */     throws IOException
/*     */   {
/* 231 */     IndexInput is = source.dir.openInput(source.file);
/*     */     try {
/* 233 */       long startPtr = os.getFilePointer();
/* 234 */       long length = is.length();
/* 235 */       os.copyBytes(is, length);
/*     */ 
/* 237 */       if (this.checkAbort != null) {
/* 238 */         this.checkAbort.work(length);
/*     */       }
/*     */ 
/* 242 */       long endPtr = os.getFilePointer();
/* 243 */       long diff = endPtr - startPtr;
/* 244 */       if (diff != length)
/* 245 */         throw new IOException("Difference in the output file offsets " + diff + " does not match the original file length " + length);
/*     */     }
/*     */     finally
/*     */     {
/* 249 */       is.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class FileEntry
/*     */   {
/*     */     String file;
/*     */     long directoryOffset;
/*     */     long dataOffset;
/*     */     Directory dir;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.CompoundFileWriter
 * JD-Core Version:    0.6.0
 */