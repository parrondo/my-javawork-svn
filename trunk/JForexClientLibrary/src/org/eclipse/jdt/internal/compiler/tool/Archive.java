/*    */ package org.eclipse.jdt.internal.compiler.tool;
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
/* 40 */     this.file = file;
/* 41 */     this.zipFile = new ZipFile(file);
/* 42 */     initialize();
/*    */   }
/*    */ 
/*    */   private void initialize()
/*    */   {
/* 47 */     this.packagesCache = new Hashtable();
/* 48 */     for (Enumeration e = this.zipFile.entries(); e.hasMoreElements(); ) {
/* 49 */       String fileName = ((ZipEntry)e.nextElement()).getName();
/*    */ 
/* 52 */       int last = fileName.lastIndexOf('/');
/*    */ 
/* 54 */       String packageName = fileName.substring(0, last + 1);
/* 55 */       String typeName = fileName.substring(last + 1);
/* 56 */       ArrayList types = (ArrayList)this.packagesCache.get(packageName);
/* 57 */       if (types == null)
/*    */       {
/* 59 */         if (typeName.length() == 0) {
/*    */           continue;
/*    */         }
/* 62 */         types = new ArrayList();
/* 63 */         types.add(typeName);
/* 64 */         this.packagesCache.put(packageName, types);
/*    */       } else {
/* 66 */         types.add(typeName);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public ArchiveFileObject getArchiveFileObject(String entryName, Charset charset) {
/* 72 */     return new ArchiveFileObject(this.file, this.zipFile, entryName, charset);
/*    */   }
/*    */ 
/*    */   public boolean contains(String entryName) {
/* 76 */     return this.zipFile.getEntry(entryName) != null;
/*    */   }
/*    */ 
/*    */   public Set<String> allPackages() {
/* 80 */     if (this.packagesCache == null) {
/* 81 */       initialize();
/*    */     }
/* 83 */     return this.packagesCache.keySet();
/*    */   }
/*    */ 
/*    */   public ArrayList<String> getTypes(String packageName)
/*    */   {
/* 88 */     return (ArrayList)this.packagesCache.get(packageName);
/*    */   }
/*    */ 
/*    */   public void flush() {
/* 92 */     this.packagesCache = null;
/*    */   }
/*    */ 
/*    */   public void close() {
/*    */     try {
/* 97 */       if (this.zipFile != null) this.zipFile.close();
/* 98 */       this.packagesCache = null;
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.tool.Archive
 * JD-Core Version:    0.6.0
 */