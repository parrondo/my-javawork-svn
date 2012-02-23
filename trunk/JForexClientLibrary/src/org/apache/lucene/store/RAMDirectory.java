/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.apache.lucene.index.IndexFileNameFilter;
/*     */ import org.apache.lucene.util.ThreadInterruptedException;
/*     */ 
/*     */ public class RAMDirectory extends Directory
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  42 */   protected final Map<String, RAMFile> fileMap = new ConcurrentHashMap();
/*  43 */   protected final AtomicLong sizeInBytes = new AtomicLong();
/*     */ 
/*     */   public RAMDirectory()
/*     */   {
/*     */     try
/*     */     {
/*  52 */       setLockFactory(new SingleInstanceLockFactory());
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public RAMDirectory(Directory dir)
/*     */     throws IOException
/*     */   {
/*  75 */     this(dir, false);
/*     */   }
/*     */ 
/*     */   private RAMDirectory(Directory dir, boolean closeDir) throws IOException {
/*  79 */     this();
/*     */ 
/*  81 */     IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
/*  82 */     for (String file : dir.listAll()) {
/*  83 */       if (filter.accept(null, file)) {
/*  84 */         dir.copy(this, file, file);
/*     */       }
/*     */     }
/*  87 */     if (closeDir)
/*  88 */       dir.close();
/*     */   }
/*     */ 
/*     */   public final String[] listAll()
/*     */   {
/*  94 */     ensureOpen();
/*     */ 
/*  97 */     Set fileNames = this.fileMap.keySet();
/*  98 */     List names = new ArrayList(fileNames.size());
/*     */     String name;
/*  99 */     for (Iterator i$ = fileNames.iterator(); i$.hasNext(); names.add(name)) name = (String)i$.next();
/* 100 */     return (String[])names.toArray(new String[names.size()]);
/*     */   }
/*     */ 
/*     */   public final boolean fileExists(String name)
/*     */   {
/* 106 */     ensureOpen();
/* 107 */     return this.fileMap.containsKey(name);
/*     */   }
/*     */ 
/*     */   public final long fileModified(String name)
/*     */     throws IOException
/*     */   {
/* 115 */     ensureOpen();
/* 116 */     RAMFile file = (RAMFile)this.fileMap.get(name);
/* 117 */     if (file == null) {
/* 118 */       throw new FileNotFoundException(name);
/*     */     }
/* 120 */     return file.getLastModified();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void touchFile(String name)
/*     */     throws IOException
/*     */   {
/* 130 */     ensureOpen();
/* 131 */     RAMFile file = (RAMFile)this.fileMap.get(name);
/* 132 */     if (file == null) {
/* 133 */       throw new FileNotFoundException(name);
/* 136 */     }
/*     */ long ts1 = System.currentTimeMillis();
/*     */     long ts2;
/*     */     do { try { Thread.sleep(0L, 1);
/*     */       } catch (InterruptedException ie) {
/* 141 */         throw new ThreadInterruptedException(ie);
/*     */       }
/* 143 */       ts2 = System.currentTimeMillis(); }
/* 144 */     while (ts1 == ts2);
/*     */ 
/* 146 */     file.setLastModified(ts2);
/*     */   }
/*     */ 
/*     */   public final long fileLength(String name)
/*     */     throws IOException
/*     */   {
/* 154 */     ensureOpen();
/* 155 */     RAMFile file = (RAMFile)this.fileMap.get(name);
/* 156 */     if (file == null) {
/* 157 */       throw new FileNotFoundException(name);
/*     */     }
/* 159 */     return file.getLength();
/*     */   }
/*     */ 
/*     */   public final long sizeInBytes()
/*     */   {
/* 167 */     ensureOpen();
/* 168 */     return this.sizeInBytes.get();
/*     */   }
/*     */ 
/*     */   public void deleteFile(String name)
/*     */     throws IOException
/*     */   {
/* 176 */     ensureOpen();
/* 177 */     RAMFile file = (RAMFile)this.fileMap.remove(name);
/* 178 */     if (file != null) {
/* 179 */       file.directory = null;
/* 180 */       this.sizeInBytes.addAndGet(-file.sizeInBytes);
/*     */     } else {
/* 182 */       throw new FileNotFoundException(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public IndexOutput createOutput(String name)
/*     */     throws IOException
/*     */   {
/* 189 */     ensureOpen();
/* 190 */     RAMFile file = newRAMFile();
/* 191 */     RAMFile existing = (RAMFile)this.fileMap.remove(name);
/* 192 */     if (existing != null) {
/* 193 */       this.sizeInBytes.addAndGet(-existing.sizeInBytes);
/* 194 */       existing.directory = null;
/*     */     }
/* 196 */     this.fileMap.put(name, file);
/* 197 */     return new RAMOutputStream(file);
/*     */   }
/*     */ 
/*     */   protected RAMFile newRAMFile()
/*     */   {
/* 206 */     return new RAMFile(this);
/*     */   }
/*     */ 
/*     */   public IndexInput openInput(String name)
/*     */     throws IOException
/*     */   {
/* 212 */     ensureOpen();
/* 213 */     RAMFile file = (RAMFile)this.fileMap.get(name);
/* 214 */     if (file == null) {
/* 215 */       throw new FileNotFoundException(name);
/*     */     }
/* 217 */     return new RAMInputStream(file);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 223 */     this.isOpen = false;
/* 224 */     this.fileMap.clear();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.RAMDirectory
 * JD-Core Version:    0.6.0
 */