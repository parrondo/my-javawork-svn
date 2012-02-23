/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.store.BufferedIndexInput;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.store.Lock;
/*     */ 
/*     */ class CompoundFileReader extends Directory
/*     */ {
/*     */   private int readBufferSize;
/*     */   private Directory directory;
/*     */   private String fileName;
/*     */   private IndexInput stream;
/*  49 */   private HashMap<String, FileEntry> entries = new HashMap();
/*     */ 
/*     */   public CompoundFileReader(Directory dir, String name) throws IOException {
/*  52 */     this(dir, name, 1024);
/*     */   }
/*     */ 
/*     */   public CompoundFileReader(Directory dir, String name, int readBufferSize) throws IOException {
/*  56 */     assert (!(dir instanceof CompoundFileReader)) : ("compound file inside of compound file: " + name);
/*  57 */     this.directory = dir;
/*  58 */     this.fileName = name;
/*  59 */     this.readBufferSize = readBufferSize;
/*     */ 
/*  61 */     boolean success = false;
/*     */     try
/*     */     {
/*  64 */       this.stream = dir.openInput(name, readBufferSize);
/*     */ 
/*  68 */       int firstInt = this.stream.readVInt();
/*     */       boolean stripSegmentName;
/*     */       int count;
/*     */       boolean stripSegmentName;
/*  72 */       if (firstInt < 0) {
/*  73 */         if (firstInt < -1) {
/*  74 */           throw new CorruptIndexException("Incompatible format version: " + firstInt + " expected " + -1);
/*     */         }
/*     */ 
/*  78 */         int count = this.stream.readVInt();
/*  79 */         stripSegmentName = false;
/*     */       } else {
/*  81 */         count = firstInt;
/*  82 */         stripSegmentName = true;
/*     */       }
/*     */ 
/*  86 */       FileEntry entry = null;
/*  87 */       for (int i = 0; i < count; i++) {
/*  88 */         long offset = this.stream.readLong();
/*  89 */         String id = this.stream.readString();
/*     */ 
/*  91 */         if (stripSegmentName)
/*     */         {
/*  94 */           id = IndexFileNames.stripSegmentName(id);
/*     */         }
/*     */ 
/*  97 */         if (entry != null)
/*     */         {
/*  99 */           entry.length = (offset - entry.offset);
/*     */         }
/*     */ 
/* 102 */         entry = new FileEntry(null);
/* 103 */         entry.offset = offset;
/* 104 */         this.entries.put(id, entry);
/*     */       }
/*     */ 
/* 108 */       if (entry != null) {
/* 109 */         entry.length = (this.stream.length() - entry.offset);
/*     */       }
/*     */ 
/* 112 */       success = true;
/*     */     }
/*     */     finally {
/* 115 */       if ((!success) && (this.stream != null))
/*     */         try {
/* 117 */           this.stream.close();
/*     */         } catch (IOException e) {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Directory getDirectory() {
/* 124 */     return this.directory;
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 128 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public synchronized void close() throws IOException
/*     */   {
/* 133 */     if (this.stream == null) {
/* 134 */       throw new IOException("Already closed");
/*     */     }
/* 136 */     this.entries.clear();
/* 137 */     this.stream.close();
/* 138 */     this.stream = null;
/*     */   }
/*     */ 
/*     */   public synchronized IndexInput openInput(String id)
/*     */     throws IOException
/*     */   {
/* 144 */     return openInput(id, this.readBufferSize);
/*     */   }
/*     */ 
/*     */   public synchronized IndexInput openInput(String id, int readBufferSize) throws IOException
/*     */   {
/* 149 */     if (this.stream == null) {
/* 150 */       throw new IOException("Stream closed");
/*     */     }
/* 152 */     id = IndexFileNames.stripSegmentName(id);
/* 153 */     FileEntry entry = (FileEntry)this.entries.get(id);
/* 154 */     if (entry == null) {
/* 155 */       throw new IOException("No sub-file with id " + id + " found (fileName=" + this.fileName + " files: " + this.entries.keySet() + ")");
/*     */     }
/*     */ 
/* 158 */     return new CSIndexInput(this.stream, entry.offset, entry.length, readBufferSize);
/*     */   }
/*     */ 
/*     */   public String[] listAll()
/*     */   {
/* 164 */     String[] res = (String[])this.entries.keySet().toArray(new String[this.entries.size()]);
/*     */ 
/* 166 */     String seg = this.fileName.substring(0, this.fileName.indexOf('.'));
/* 167 */     for (int i = 0; i < res.length; i++) {
/* 168 */       res[i] = (seg + res[i]);
/*     */     }
/* 170 */     return res;
/*     */   }
/*     */ 
/*     */   public boolean fileExists(String name)
/*     */   {
/* 176 */     return this.entries.containsKey(IndexFileNames.stripSegmentName(name));
/*     */   }
/*     */ 
/*     */   public long fileModified(String name)
/*     */     throws IOException
/*     */   {
/* 182 */     return this.directory.fileModified(this.fileName);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void touchFile(String name)
/*     */     throws IOException
/*     */   {
/* 191 */     this.directory.touchFile(this.fileName);
/*     */   }
/*     */ 
/*     */   public void deleteFile(String name)
/*     */   {
/* 198 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void renameFile(String from, String to)
/*     */   {
/* 204 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public long fileLength(String name)
/*     */     throws IOException
/*     */   {
/* 211 */     FileEntry e = (FileEntry)this.entries.get(IndexFileNames.stripSegmentName(name));
/* 212 */     if (e == null)
/* 213 */       throw new FileNotFoundException(name);
/* 214 */     return e.length;
/*     */   }
/*     */ 
/*     */   public IndexOutput createOutput(String name)
/*     */   {
/* 221 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Lock makeLock(String name)
/*     */   {
/* 228 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   static final class CSIndexInput extends BufferedIndexInput
/*     */   {
/*     */     IndexInput base;
/*     */     long fileOffset;
/*     */     long length;
/*     */ 
/*     */     CSIndexInput(IndexInput base, long fileOffset, long length)
/*     */     {
/* 242 */       this(base, fileOffset, length, 1024);
/*     */     }
/*     */ 
/*     */     CSIndexInput(IndexInput base, long fileOffset, long length, int readBufferSize) {
/* 246 */       super();
/* 247 */       this.base = ((IndexInput)base.clone());
/* 248 */       this.fileOffset = fileOffset;
/* 249 */       this.length = length;
/*     */     }
/*     */ 
/*     */     public Object clone()
/*     */     {
/* 254 */       CSIndexInput clone = (CSIndexInput)super.clone();
/* 255 */       clone.base = ((IndexInput)this.base.clone());
/* 256 */       clone.fileOffset = this.fileOffset;
/* 257 */       clone.length = this.length;
/* 258 */       return clone;
/*     */     }
/*     */ 
/*     */     protected void readInternal(byte[] b, int offset, int len)
/*     */       throws IOException
/*     */     {
/* 269 */       long start = getFilePointer();
/* 270 */       if (start + len > this.length)
/* 271 */         throw new IOException("read past EOF");
/* 272 */       this.base.seek(this.fileOffset + start);
/* 273 */       this.base.readBytes(b, offset, len, false);
/*     */     }
/*     */ 
/*     */     protected void seekInternal(long pos)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 286 */       this.base.close();
/*     */     }
/*     */ 
/*     */     public long length()
/*     */     {
/* 291 */       return this.length;
/*     */     }
/*     */ 
/*     */     public void copyBytes(IndexOutput out, long numBytes)
/*     */       throws IOException
/*     */     {
/* 297 */       numBytes -= flushBuffer(out, numBytes);
/*     */ 
/* 301 */       if (numBytes > 0L) {
/* 302 */         long start = getFilePointer();
/* 303 */         if (start + numBytes > this.length) {
/* 304 */           throw new IOException("read past EOF");
/*     */         }
/* 306 */         this.base.seek(this.fileOffset + start);
/* 307 */         this.base.copyBytes(out, numBytes);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class FileEntry
/*     */   {
/*     */     long offset;
/*     */     long length;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.CompoundFileReader
 * JD-Core Version:    0.6.0
 */