/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class FileSwitchDirectory extends Directory
/*     */ {
/*     */   private final Directory secondaryDir;
/*     */   private final Directory primaryDir;
/*     */   private final Set<String> primaryExtensions;
/*     */   private boolean doClose;
/*     */ 
/*     */   public FileSwitchDirectory(Set<String> primaryExtensions, Directory primaryDir, Directory secondaryDir, boolean doClose)
/*     */   {
/*  49 */     this.primaryExtensions = primaryExtensions;
/*  50 */     this.primaryDir = primaryDir;
/*  51 */     this.secondaryDir = secondaryDir;
/*  52 */     this.doClose = doClose;
/*  53 */     this.lockFactory = primaryDir.getLockFactory();
/*     */   }
/*     */ 
/*     */   public Directory getPrimaryDir()
/*     */   {
/*  58 */     return this.primaryDir;
/*     */   }
/*     */ 
/*     */   public Directory getSecondaryDir()
/*     */   {
/*  63 */     return this.secondaryDir;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/*  68 */     if (this.doClose) {
/*     */       try {
/*  70 */         this.secondaryDir.close();
/*     */       } finally {
/*  72 */         this.primaryDir.close();
/*     */       }
/*  74 */       this.doClose = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] listAll() throws IOException
/*     */   {
/*  80 */     Set files = new HashSet();
/*     */ 
/*  85 */     NoSuchDirectoryException exc = null;
/*     */     try {
/*  87 */       for (String f : this.primaryDir.listAll())
/*  88 */         files.add(f);
/*     */     }
/*     */     catch (NoSuchDirectoryException e) {
/*  91 */       exc = e;
/*     */     }
/*     */     try {
/*  94 */       for (String f : this.secondaryDir.listAll()) {
/*  95 */         files.add(f);
/*     */       }
/*     */     }
/*     */     catch (NoSuchDirectoryException e)
/*     */     {
/* 100 */       if (exc != null) {
/* 101 */         throw exc;
/*     */       }
/*     */ 
/* 105 */       if (files.isEmpty()) {
/* 106 */         throw e;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 111 */     if ((exc != null) && (files.isEmpty())) {
/* 112 */       throw exc;
/*     */     }
/* 114 */     return (String[])files.toArray(new String[files.size()]);
/*     */   }
/*     */ 
/*     */   public static String getExtension(String name)
/*     */   {
/* 119 */     int i = name.lastIndexOf('.');
/* 120 */     if (i == -1) {
/* 121 */       return "";
/*     */     }
/* 123 */     return name.substring(i + 1, name.length());
/*     */   }
/*     */ 
/*     */   private Directory getDirectory(String name) {
/* 127 */     String ext = getExtension(name);
/* 128 */     if (this.primaryExtensions.contains(ext)) {
/* 129 */       return this.primaryDir;
/*     */     }
/* 131 */     return this.secondaryDir;
/*     */   }
/*     */ 
/*     */   public boolean fileExists(String name)
/*     */     throws IOException
/*     */   {
/* 137 */     return getDirectory(name).fileExists(name);
/*     */   }
/*     */ 
/*     */   public long fileModified(String name) throws IOException
/*     */   {
/* 142 */     return getDirectory(name).fileModified(name);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void touchFile(String name)
/*     */     throws IOException
/*     */   {
/* 150 */     getDirectory(name).touchFile(name);
/*     */   }
/*     */ 
/*     */   public void deleteFile(String name) throws IOException
/*     */   {
/* 155 */     getDirectory(name).deleteFile(name);
/*     */   }
/*     */ 
/*     */   public long fileLength(String name) throws IOException
/*     */   {
/* 160 */     return getDirectory(name).fileLength(name);
/*     */   }
/*     */ 
/*     */   public IndexOutput createOutput(String name) throws IOException
/*     */   {
/* 165 */     return getDirectory(name).createOutput(name);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void sync(String name) throws IOException {
/* 171 */     sync(Collections.singleton(name));
/*     */   }
/*     */ 
/*     */   public void sync(Collection<String> names) throws IOException
/*     */   {
/* 176 */     List primaryNames = new ArrayList();
/* 177 */     List secondaryNames = new ArrayList();
/*     */ 
/* 179 */     for (String name : names) {
/* 180 */       if (this.primaryExtensions.contains(getExtension(name)))
/* 181 */         primaryNames.add(name);
/*     */       else
/* 183 */         secondaryNames.add(name);
/*     */     }
/* 185 */     this.primaryDir.sync(primaryNames);
/* 186 */     this.secondaryDir.sync(secondaryNames);
/*     */   }
/*     */ 
/*     */   public IndexInput openInput(String name) throws IOException
/*     */   {
/* 191 */     return getDirectory(name).openInput(name);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.FileSwitchDirectory
 * JD-Core Version:    0.6.0
 */