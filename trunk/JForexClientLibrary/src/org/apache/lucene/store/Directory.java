/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import org.apache.lucene.index.IndexFileNameFilter;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ 
/*     */ public abstract class Directory
/*     */   implements Closeable
/*     */ {
/*  46 */   protected volatile boolean isOpen = true;
/*     */   protected LockFactory lockFactory;
/*     */ 
/*     */   public abstract String[] listAll()
/*     */     throws IOException;
/*     */ 
/*     */   public abstract boolean fileExists(String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract long fileModified(String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   @Deprecated
/*     */   public abstract void touchFile(String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract void deleteFile(String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract long fileLength(String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract IndexOutput createOutput(String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   @Deprecated
/*     */   public void sync(String name)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void sync(Collection<String> names)
/*     */     throws IOException
/*     */   {
/* 127 */     for (String name : names)
/* 128 */       sync(name);
/*     */   }
/*     */ 
/*     */   public abstract IndexInput openInput(String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public IndexInput openInput(String name, int bufferSize)
/*     */     throws IOException
/*     */   {
/* 143 */     return openInput(name);
/*     */   }
/*     */ 
/*     */   public Lock makeLock(String name)
/*     */   {
/* 150 */     return this.lockFactory.makeLock(name);
/*     */   }
/*     */ 
/*     */   public void clearLock(String name)
/*     */     throws IOException
/*     */   {
/* 159 */     if (this.lockFactory != null)
/* 160 */       this.lockFactory.clearLock(name);
/*     */   }
/*     */ 
/*     */   public abstract void close()
/*     */     throws IOException;
/*     */ 
/*     */   public void setLockFactory(LockFactory lockFactory)
/*     */     throws IOException
/*     */   {
/* 178 */     assert (lockFactory != null);
/* 179 */     this.lockFactory = lockFactory;
/* 180 */     lockFactory.setLockPrefix(getLockID());
/*     */   }
/*     */ 
/*     */   public LockFactory getLockFactory()
/*     */   {
/* 190 */     return this.lockFactory;
/*     */   }
/*     */ 
/*     */   public String getLockID()
/*     */   {
/* 202 */     return toString();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 207 */     return super.toString() + " lockFactory=" + getLockFactory();
/*     */   }
/*     */ 
/*     */   public void copy(Directory to, String src, String dest)
/*     */     throws IOException
/*     */   {
/* 228 */     IndexOutput os = null;
/* 229 */     IndexInput is = null;
/* 230 */     IOException priorException = null;
/*     */     try {
/* 232 */       os = to.createOutput(dest);
/* 233 */       is = openInput(src);
/* 234 */       is.copyBytes(os, is.length());
/*     */     } catch (IOException ioe) {
/* 236 */       priorException = ioe;
/*     */     } finally {
/* 238 */       IOUtils.closeWhileHandlingException(priorException, new Closeable[] { os, is });
/*     */     }
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void copy(Directory src, Directory dest, boolean closeDirSrc)
/*     */     throws IOException
/*     */   {
/* 272 */     IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
/* 273 */     for (String file : src.listAll()) {
/* 274 */       if (filter.accept(null, file)) {
/* 275 */         src.copy(dest, file, file);
/*     */       }
/*     */     }
/* 278 */     if (closeDirSrc)
/* 279 */       src.close();
/*     */   }
/*     */ 
/*     */   protected final void ensureOpen()
/*     */     throws AlreadyClosedException
/*     */   {
/* 287 */     if (!this.isOpen)
/* 288 */       throw new AlreadyClosedException("this Directory is closed");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.Directory
 * JD-Core Version:    0.6.0
 */