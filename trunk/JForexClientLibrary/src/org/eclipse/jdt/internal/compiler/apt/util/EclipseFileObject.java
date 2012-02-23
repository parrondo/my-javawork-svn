/*     */ package org.eclipse.jdt.internal.compiler.apt.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import javax.lang.model.element.Modifier;
/*     */ import javax.lang.model.element.NestingKind;
/*     */ import javax.tools.JavaFileObject.Kind;
/*     */ import javax.tools.SimpleJavaFileObject;
/*     */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
/*     */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
/*     */ 
/*     */ public class EclipseFileObject extends SimpleJavaFileObject
/*     */ {
/*     */   private File f;
/*     */   private Charset charset;
/*     */   private boolean parentsExist;
/*     */ 
/*     */   public EclipseFileObject(String className, URI uri, JavaFileObject.Kind kind, Charset charset)
/*     */   {
/*  45 */     super(uri, kind);
/*  46 */     this.f = new File(this.uri);
/*  47 */     this.charset = charset;
/*  48 */     this.parentsExist = false;
/*     */   }
/*     */ 
/*     */   public Modifier getAccessLevel()
/*     */   {
/*  56 */     if (getKind() != JavaFileObject.Kind.CLASS) {
/*  57 */       return null;
/*     */     }
/*  59 */     ClassFileReader reader = null;
/*     */     try {
/*  61 */       reader = ClassFileReader.read(this.f);
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
/*     */   public NestingKind getNestingKind()
/*     */   {
/*  87 */     switch ($SWITCH_TABLE$javax$tools$JavaFileObject$Kind()[this.kind.ordinal()]) {
/*     */     case 1:
/*  89 */       return NestingKind.TOP_LEVEL;
/*     */     case 2:
/*  91 */       ClassFileReader reader = null;
/*     */       try {
/*  93 */         reader = ClassFileReader.read(this.f);
/*     */       }
/*     */       catch (ClassFormatException localClassFormatException) {
/*     */       }
/*     */       catch (IOException localIOException) {
/*     */       }
/*  99 */       if (reader == null) {
/* 100 */         return null;
/*     */       }
/* 102 */       if (reader.isAnonymous()) {
/* 103 */         return NestingKind.ANONYMOUS;
/*     */       }
/* 105 */       if (reader.isLocal()) {
/* 106 */         return NestingKind.LOCAL;
/*     */       }
/* 108 */       if (reader.isMember()) {
/* 109 */         return NestingKind.MEMBER;
/*     */       }
/* 111 */       return NestingKind.TOP_LEVEL;
/*     */     }
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean delete()
/*     */   {
/* 121 */     return this.f.delete();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o) {
/* 125 */     if (!(o instanceof EclipseFileObject)) {
/* 126 */       return false;
/*     */     }
/* 128 */     EclipseFileObject eclipseFileObject = (EclipseFileObject)o;
/* 129 */     return eclipseFileObject.toUri().equals(this.uri);
/*     */   }
/*     */ 
/*     */   public CharSequence getCharContent(boolean ignoreEncodingErrors)
/*     */     throws IOException
/*     */   {
/* 136 */     return Util.getCharContents(this, ignoreEncodingErrors, org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(this.f), this.charset.toString());
/*     */   }
/*     */ 
/*     */   public long getLastModified()
/*     */   {
/* 143 */     return this.f.lastModified();
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 147 */     return this.f.getPath();
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 151 */     return this.f.hashCode();
/*     */   }
/*     */ 
/*     */   public InputStream openInputStream()
/*     */     throws IOException
/*     */   {
/* 159 */     return new FileInputStream(this.f);
/*     */   }
/*     */ 
/*     */   public OutputStream openOutputStream()
/*     */     throws IOException
/*     */   {
/* 166 */     ensureParentDirectoriesExist();
/* 167 */     return new FileOutputStream(this.f);
/*     */   }
/*     */ 
/*     */   public Reader openReader(boolean ignoreEncodingErrors)
/*     */     throws IOException
/*     */   {
/* 174 */     return new FileReader(this.f);
/*     */   }
/*     */ 
/*     */   public Writer openWriter()
/*     */     throws IOException
/*     */   {
/* 181 */     ensureParentDirectoriesExist();
/* 182 */     return new FileWriter(this.f);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 187 */     return this.f.getAbsolutePath();
/*     */   }
/*     */ 
/*     */   private void ensureParentDirectoriesExist() throws IOException {
/* 191 */     if (!this.parentsExist) {
/* 192 */       File parent = this.f.getParentFile();
/* 193 */       if ((parent != null) && (!parent.exists()) && 
/* 194 */         (!parent.mkdirs()))
/*     */       {
/* 196 */         if ((!parent.exists()) || (!parent.isDirectory())) {
/* 197 */           throw new IOException("Unable to create parent directories for " + this.f);
/*     */         }
/*     */       }
/* 200 */       this.parentsExist = true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.util.EclipseFileObject
 * JD-Core Version:    0.6.0
 */