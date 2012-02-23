/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.util.Constants;
/*     */ import org.apache.lucene.util.ThreadInterruptedException;
/*     */ 
/*     */ public abstract class FSDirectory extends Directory
/*     */ {
/* 121 */   public static final int DEFAULT_READ_CHUNK_SIZE = Constants.JRE_IS_64BIT ? 2147483647 : 104857600;
/*     */   protected final File directory;
/* 124 */   protected final Set<String> staleFiles = Collections.synchronizedSet(new HashSet());
/* 125 */   private int chunkSize = DEFAULT_READ_CHUNK_SIZE;
/*     */ 
/*     */   private static File getCanonicalPath(File file) throws IOException
/*     */   {
/* 129 */     return new File(file.getCanonicalPath());
/*     */   }
/*     */ 
/*     */   protected FSDirectory(File path, LockFactory lockFactory)
/*     */     throws IOException
/*     */   {
/* 140 */     if (lockFactory == null) {
/* 141 */       lockFactory = new NativeFSLockFactory();
/*     */     }
/* 143 */     this.directory = getCanonicalPath(path);
/*     */ 
/* 145 */     if ((this.directory.exists()) && (!this.directory.isDirectory())) {
/* 146 */       throw new NoSuchDirectoryException("file '" + this.directory + "' exists but is not a directory");
/*     */     }
/* 148 */     setLockFactory(lockFactory);
/*     */   }
/*     */ 
/*     */   public static FSDirectory open(File path)
/*     */     throws IOException
/*     */   {
/* 172 */     return open(path, null);
/*     */   }
/*     */ 
/*     */   public static FSDirectory open(File path, LockFactory lockFactory)
/*     */     throws IOException
/*     */   {
/* 178 */     if (((Constants.WINDOWS) || (Constants.SUN_OS) || (Constants.LINUX)) && (Constants.JRE_IS_64BIT) && (MMapDirectory.UNMAP_SUPPORTED))
/*     */     {
/* 180 */       return new MMapDirectory(path, lockFactory);
/* 181 */     }if (Constants.WINDOWS) {
/* 182 */       return new SimpleFSDirectory(path, lockFactory);
/*     */     }
/* 184 */     return new NIOFSDirectory(path, lockFactory);
/*     */   }
/*     */ 
/*     */   public void setLockFactory(LockFactory lockFactory)
/*     */     throws IOException
/*     */   {
/* 190 */     super.setLockFactory(lockFactory);
/*     */ 
/* 194 */     if ((lockFactory instanceof FSLockFactory)) {
/* 195 */       FSLockFactory lf = (FSLockFactory)lockFactory;
/* 196 */       File dir = lf.getLockDir();
/*     */ 
/* 198 */       if (dir == null) {
/* 199 */         lf.setLockDir(this.directory);
/* 200 */         lf.setLockPrefix(null);
/* 201 */       } else if (dir.getCanonicalPath().equals(this.directory.getCanonicalPath())) {
/* 202 */         lf.setLockPrefix(null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String[] listAll(File dir)
/*     */     throws IOException
/*     */   {
/* 217 */     if (!dir.exists())
/* 218 */       throw new NoSuchDirectoryException("directory '" + dir + "' does not exist");
/* 219 */     if (!dir.isDirectory()) {
/* 220 */       throw new NoSuchDirectoryException("file '" + dir + "' exists but is not a directory");
/*     */     }
/*     */ 
/* 223 */     String[] result = dir.list(new FilenameFilter() {
/*     */       public boolean accept(File dir, String file) {
/* 225 */         return !new File(dir, file).isDirectory();
/*     */       }
/*     */     });
/* 229 */     if (result == null) {
/* 230 */       throw new IOException("directory '" + dir + "' exists and is a directory, but cannot be listed: list() returned null");
/*     */     }
/* 232 */     return result;
/*     */   }
/*     */ 
/*     */   public String[] listAll()
/*     */     throws IOException
/*     */   {
/* 240 */     ensureOpen();
/* 241 */     return listAll(this.directory);
/*     */   }
/*     */ 
/*     */   public boolean fileExists(String name)
/*     */   {
/* 247 */     ensureOpen();
/* 248 */     File file = new File(this.directory, name);
/* 249 */     return file.exists();
/*     */   }
/*     */ 
/*     */   public long fileModified(String name)
/*     */   {
/* 255 */     ensureOpen();
/* 256 */     File file = new File(this.directory, name);
/* 257 */     return file.lastModified();
/*     */   }
/*     */ 
/*     */   public static long fileModified(File directory, String name)
/*     */   {
/* 262 */     File file = new File(directory, name);
/* 263 */     return file.lastModified();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void touchFile(String name)
/*     */   {
/* 272 */     ensureOpen();
/* 273 */     File file = new File(this.directory, name);
/* 274 */     file.setLastModified(System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */   public long fileLength(String name)
/*     */     throws IOException
/*     */   {
/* 280 */     ensureOpen();
/* 281 */     File file = new File(this.directory, name);
/* 282 */     long len = file.length();
/* 283 */     if ((len == 0L) && (!file.exists())) {
/* 284 */       throw new FileNotFoundException(name);
/*     */     }
/* 286 */     return len;
/*     */   }
/*     */ 
/*     */   public void deleteFile(String name)
/*     */     throws IOException
/*     */   {
/* 293 */     ensureOpen();
/* 294 */     File file = new File(this.directory, name);
/* 295 */     if (!file.delete())
/* 296 */       throw new IOException("Cannot delete " + file);
/* 297 */     this.staleFiles.remove(name);
/*     */   }
/*     */ 
/*     */   public IndexOutput createOutput(String name)
/*     */     throws IOException
/*     */   {
/* 303 */     ensureOpen();
/*     */ 
/* 305 */     ensureCanWrite(name);
/* 306 */     return new FSIndexOutput(this, name);
/*     */   }
/*     */ 
/*     */   protected void ensureCanWrite(String name) throws IOException {
/* 310 */     if ((!this.directory.exists()) && 
/* 311 */       (!this.directory.mkdirs())) {
/* 312 */       throw new IOException("Cannot create directory: " + this.directory);
/*     */     }
/* 314 */     File file = new File(this.directory, name);
/* 315 */     if ((file.exists()) && (!file.delete()))
/* 316 */       throw new IOException("Cannot overwrite: " + file);
/*     */   }
/*     */ 
/*     */   protected void onIndexOutputClosed(FSIndexOutput io) {
/* 320 */     this.staleFiles.add(io.name);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void sync(String name) throws IOException {
/* 326 */     sync(Collections.singleton(name));
/*     */   }
/*     */ 
/*     */   public void sync(Collection<String> names) throws IOException
/*     */   {
/* 331 */     ensureOpen();
/* 332 */     Set toSync = new HashSet(names);
/* 333 */     toSync.retainAll(this.staleFiles);
/*     */ 
/* 335 */     for (String name : toSync) {
/* 336 */       fsync(name);
/*     */     }
/* 338 */     this.staleFiles.removeAll(toSync);
/*     */   }
/*     */ 
/*     */   public IndexInput openInput(String name)
/*     */     throws IOException
/*     */   {
/* 344 */     ensureOpen();
/* 345 */     return openInput(name, 1024);
/*     */   }
/*     */   public String getLockID() {
/* 350 */     ensureOpen();
/*     */     String dirName;
/*     */     try {
/* 353 */       dirName = this.directory.getCanonicalPath();
/*     */     } catch (IOException e) {
/* 355 */       throw new RuntimeException(e.toString(), e);
/*     */     }
/*     */ 
/* 358 */     int digest = 0;
/* 359 */     for (int charIDX = 0; charIDX < dirName.length(); charIDX++) {
/* 360 */       char ch = dirName.charAt(charIDX);
/* 361 */       digest = 31 * digest + ch;
/*     */     }
/* 363 */     return "lucene-" + Integer.toHexString(digest);
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 369 */     this.isOpen = false;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public File getFile() {
/* 375 */     return getDirectory();
/*     */   }
/*     */ 
/*     */   public File getDirectory()
/*     */   {
/* 380 */     ensureOpen();
/* 381 */     return this.directory;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 387 */     return getClass().getName() + "@" + this.directory + " lockFactory=" + getLockFactory();
/*     */   }
/*     */ 
/*     */   public final void setReadChunkSize(int chunkSize)
/*     */   {
/* 414 */     if (chunkSize <= 0) {
/* 415 */       throw new IllegalArgumentException("chunkSize must be positive");
/*     */     }
/* 417 */     if (!Constants.JRE_IS_64BIT)
/* 418 */       this.chunkSize = chunkSize;
/*     */   }
/*     */ 
/*     */   public final int getReadChunkSize()
/*     */   {
/* 429 */     return this.chunkSize;
/*     */   }
/*     */ 
/*     */   protected void fsync(String name)
/*     */     throws IOException
/*     */   {
/* 494 */     File fullFile = new File(this.directory, name);
/* 495 */     boolean success = false;
/* 496 */     int retryCount = 0;
/* 497 */     IOException exc = null;
/* 498 */     while ((!success) && (retryCount < 5)) {
/* 499 */       retryCount++;
/* 500 */       RandomAccessFile file = null;
/*     */       try {
/*     */         try {
/* 503 */           file = new RandomAccessFile(fullFile, "rw");
/* 504 */           file.getFD().sync();
/* 505 */           success = true;
/*     */         } finally {
/* 507 */           if (file != null)
/* 508 */             file.close();
/*     */         }
/*     */       } catch (IOException ioe) {
/* 511 */         if (exc == null)
/* 512 */           exc = ioe;
/*     */         try
/*     */         {
/* 515 */           Thread.sleep(5L);
/*     */         } catch (InterruptedException ie) {
/* 517 */           throw new ThreadInterruptedException(ie);
/*     */         }
/*     */       }
/*     */     }
/* 521 */     if (!success)
/*     */     {
/* 523 */       throw exc;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static class FSIndexOutput extends BufferedIndexOutput
/*     */   {
/*     */     private final FSDirectory parent;
/*     */     private final String name;
/*     */     private final RandomAccessFile file;
/*     */     private volatile boolean isOpen;
/*     */ 
/*     */     public FSIndexOutput(FSDirectory parent, String name)
/*     */       throws IOException
/*     */     {
/* 439 */       this.parent = parent;
/* 440 */       this.name = name;
/* 441 */       this.file = new RandomAccessFile(new File(parent.directory, name), "rw");
/* 442 */       this.isOpen = true;
/*     */     }
/*     */ 
/*     */     public void flushBuffer(byte[] b, int offset, int size)
/*     */       throws IOException
/*     */     {
/* 448 */       this.file.write(b, offset, size);
/*     */     }
/*     */ 
/*     */     public void close() throws IOException
/*     */     {
/* 453 */       this.parent.onIndexOutputClosed(this);
/*     */ 
/* 455 */       if (this.isOpen) {
/* 456 */         boolean success = false;
/*     */         try {
/* 458 */           super.close();
/* 459 */           success = true;
/*     */         } finally {
/* 461 */           this.isOpen = false;
/* 462 */           if (!success)
/*     */             try {
/* 464 */               this.file.close();
/*     */             }
/*     */             catch (Throwable t) {
/*     */             }
/*     */           else
/* 469 */             this.file.close();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void seek(long pos)
/*     */       throws IOException
/*     */     {
/* 478 */       super.seek(pos);
/* 479 */       this.file.seek(pos);
/*     */     }
/*     */ 
/*     */     public long length() throws IOException
/*     */     {
/* 484 */       return this.file.length();
/*     */     }
/*     */ 
/*     */     public void setLength(long length) throws IOException
/*     */     {
/* 489 */       this.file.setLength(length);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.FSDirectory
 * JD-Core Version:    0.6.0
 */