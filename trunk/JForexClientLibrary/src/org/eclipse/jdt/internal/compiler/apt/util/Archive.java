/*    */ package org.eclipse.jdt.internal.compiler.apt.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Hashtable;
/*    */ import java.util.Set;
/*    */ import java.util.zip.ZipEntry;
/*    */ import java.util.zip.ZipException;
/*    */ import java.util.zip.ZipFile;
/*    */ 
/*    */ public class Archive
/*    */ {
/* 29 */   public static final Archive UNKNOWN_ARCHIVE = new Archive();
/*    */   ZipFile zipFile;
/*    */   File file;
/*    */   protected Hashtable<String, ArrayList<String>> packagesCache;
/*    */ 
/*    */   private Archive()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Archive(File file)
/*    */     throws ZipException, IOException
/*    */   {
/* 39 */     this.file = file;
/* 40 */     this.zipFile = new ZipFile(file);
/* 41 */     initialize();
/*    */   }
/*    */ 
/*    */   private void initialize()
/*    */   {
/* 46 */     this.packagesCache = new Hashtable();
/* 47 */     for (Enumeration e = this.zipFile.entries(); e.hasMoreElements(); ) {
/* 48 */       String fileName = ((ZipEntry)e.nextElement()).getName();
/*    */ 
/* 51 */       int last = fileName.lastIndexOf('/');
/*    */ 
/* 53 */       String packageName = fileName.substring(0, last + 1);
/* 54 */       String typeName = fileName.substring(last + 1);
/* 55 */       ArrayList types = (ArrayList)this.packagesCache.get(packageName);
/* 56 */       if (types == null)
/*    */       {
/* 58 */         if (typeName.length() == 0) {
/*    */           continue;
/*    */         }
/* 61 */         types = new ArrayList();
/* 62 */         types.add(typeName);
/* 63 */         this.packagesCache.put(packageName, types);
/*    */       } else {
/* 65 */         types.add(typeName);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public ArchiveFileObject getArchiveFileObject(String entryName, Charset charset) {
/* 71 */     return new ArchiveFileObject(this.file, this.zipFile, entryName, charset);
/*    */   }
/*    */ 
/*    */   public boolean contains(String entryName) {
/* 75 */     return this.zipFile.getEntry(entryName) != null;
/*    */   }
/*    */ 
/*    */   public Set<String> allPackages() {
/* 79 */     if (this.packagesCache == null) {
/* 80 */       initialize();
/*    */     }
/* 82 */     return this.packagesCache.keySet();
/*    */   }
/*    */ 
/*    */   public ArrayList<String> getTypes(String packageName)
/*    */   {
/* 87 */     return (ArrayList)this.packagesCache.get(packageName);
/*    */   }
/*    */ 
/*    */   public void flush() {
/* 91 */     this.packagesCache = null;
/*    */   }
/*    */ 
/*    */   public void close() {
/*    */     try {
/* 96 */       if (this.zipFile != null) this.zipFile.close();
/* 97 */       this.packagesCache = null;
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.util.Archive
 * JD-Core Version:    0.6.0
 */