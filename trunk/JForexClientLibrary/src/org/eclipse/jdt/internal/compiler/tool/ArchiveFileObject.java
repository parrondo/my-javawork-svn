/*     */ package org.eclipse.jdt.internal.compiler.tool;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import javax.lang.model.element.Modifier;
/*     */ import javax.lang.model.element.NestingKind;
/*     */ import javax.tools.JavaFileObject;
/*     */ import javax.tools.JavaFileObject.Kind;
/*     */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
/*     */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
/*     */ 
/*     */ public class ArchiveFileObject
/*     */   implements JavaFileObject
/*     */ {
/*     */   private ZipEntry zipEntry;
/*     */   private ZipFile zipFile;
/*     */   private String entryName;
/*     */   private File file;
/*     */   private Charset charset;
/*     */ 
/*     */   public ArchiveFileObject(File file, ZipFile zipFile, String entryName, Charset charset)
/*     */   {
/*  44 */     this.zipFile = zipFile;
/*  45 */     this.zipEntry = zipFile.getEntry(entryName);
/*  46 */     this.entryName = entryName;
/*  47 */     this.file = file;
/*  48 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */   public Modifier getAccessLevel()
/*     */   {
/*  56 */     if (getKind() != JavaFileObject.Kind.CLASS) {
/*  57 */       return null;
/*     */     }
/*  59 */     ClassFileReader reader = null;
/*     */     try {
/*  61 */       reader = ClassFileReader.read(this.zipFile, this.entryName);
/*     */     }
/*     */     catch (ClassFormatException localClassFormatException) {
/*     */     }
/*     */     catch (IOException localIOException) {
/*     */     }
/*  67 */     if (reader == null) {
/*  68 */       return null;
/*     */     }
/*  70 */     int accessFlags = reader.accessFlags();
/*  71 */     if ((accessFlags & 0x1) != 0) {
/*  72 */       return Modifier.PUBLIC;
/*     */     }
/*  74 */     if ((accessFlags & 0x400) != 0) {
/*  75 */       return Modifier.ABSTRACT;
/*     */     }
/*  77 */     if ((accessFlags & 0x10) != 0) {
/*  78 */       return Modifier.FINAL;
/*     */     }
/*  80 */     return null;
/*     */   }
/*     */ 
/*     */   public JavaFileObject.Kind getKind()
/*     */   {
/*  87 */     String name = this.entryName.toLowerCase();
/*  88 */     if (name.endsWith(JavaFileObject.Kind.CLASS.extension))
/*  89 */       return JavaFileObject.Kind.CLASS;
/*  90 */     if (name.endsWith(JavaFileObject.Kind.SOURCE.extension))
/*  91 */       return JavaFileObject.Kind.SOURCE;
/*  92 */     if (name.endsWith(JavaFileObject.Kind.HTML.extension)) {
/*  93 */       return JavaFileObject.Kind.HTML;
/*     */     }
/*  95 */     return JavaFileObject.Kind.OTHER;
/*     */   }
/*     */ 
/*     */   public NestingKind getNestingKind()
/*     */   {
/* 102 */     switch ($SWITCH_TABLE$javax$tools$JavaFileObject$Kind()[getKind().ordinal()]) {
/*     */     case 1:
/* 104 */       return NestingKind.TOP_LEVEL;
/*     */     case 2:
/* 106 */       ClassFileReader reader = null;
/*     */       try {
/* 108 */         reader = ClassFileReader.read(this.zipFile, this.entryName);
/*     */       }
/*     */       catch (ClassFormatException localClassFormatException) {
/*     */       }
/*     */       catch (IOException localIOException) {
/*     */       }
/* 114 */       if (reader == null) {
/* 115 */         return null;
/*     */       }
/* 117 */       if (reader.isAnonymous()) {
/* 118 */         return NestingKind.ANONYMOUS;
/*     */       }
/* 120 */       if (reader.isLocal()) {
/* 121 */         return NestingKind.LOCAL;
/*     */       }
/* 123 */       if (reader.isMember()) {
/* 124 */         return NestingKind.MEMBER;
/*     */       }
/* 126 */       return NestingKind.TOP_LEVEL;
/*     */     }
/* 128 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isNameCompatible(String simpleName, JavaFileObject.Kind kind)
/*     */   {
/* 136 */     return this.zipEntry.getName().endsWith(simpleName + kind.extension);
/*     */   }
/*     */ 
/*     */   public boolean delete()
/*     */   {
/* 143 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 148 */     if (!(o instanceof ArchiveFileObject)) {
/* 149 */       return false;
/*     */     }
/* 151 */     ArchiveFileObject archiveFileObject = (ArchiveFileObject)o;
/* 152 */     return archiveFileObject.toUri().equals(toUri());
/*     */   }
/*     */ 
/*     */   public CharSequence getCharContent(boolean ignoreEncodingErrors)
/*     */     throws IOException
/*     */   {
/* 159 */     if (getKind() == JavaFileObject.Kind.SOURCE) {
/* 160 */       return Util.getCharContents(this, ignoreEncodingErrors, org.eclipse.jdt.internal.compiler.util.Util.getZipEntryByteContent(this.zipEntry, this.zipFile), this.charset.toString());
/*     */     }
/* 162 */     return null;
/*     */   }
/*     */ 
/*     */   public long getLastModified()
/*     */   {
/* 169 */     return this.zipEntry.getTime();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 176 */     return this.zipEntry.getName();
/*     */   }
/*     */ 
/*     */   public InputStream openInputStream()
/*     */     throws IOException
/*     */   {
/* 183 */     return this.zipFile.getInputStream(this.zipEntry);
/*     */   }
/*     */ 
/*     */   public OutputStream openOutputStream()
/*     */     throws IOException
/*     */   {
/* 190 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Reader openReader(boolean ignoreEncodingErrors)
/*     */     throws IOException
/*     */   {
/* 197 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Writer openWriter()
/*     */     throws IOException
/*     */   {
/* 204 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public URI toUri()
/*     */   {
/*     */     try
/*     */     {
/* 212 */       return new URI("jar:" + this.file.toURI().getPath() + "!" + this.zipEntry.getName()); } catch (URISyntaxException localURISyntaxException) {
/*     */     }
/* 214 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 221 */     return this.file.getAbsolutePath() + "[" + this.zipEntry.getName() + "]";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.tool.ArchiveFileObject
 * JD-Core Version:    0.6.0
 */