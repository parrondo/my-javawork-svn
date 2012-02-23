/*     */ package org.eclipse.jdt.internal.compiler.tool;
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
/*  57 */     if (getKind() != JavaFileObject.Kind.CLASS) {
/*  58 */       return null;
/*     */     }
/*  60 */     ClassFileReader reader = null;
/*     */     try {
/*  62 */       reader = ClassFileReader.read(this.f);
/*     */     }
/*     */     catch (ClassFormatException localClassFormatException) {
/*     */     }
/*     */     catch (IOException localIOException) {
/*     */     }
/*  68 */     if (reader == null) {
/*  69 */       return null;
/*     */     }
/*  71 */     int accessFlags = reader.accessFlags();
/*  72 */     if ((accessFlags & 0x1) != 0) {
/*  73 */       return Modifier.PUBLIC;
/*     */     }
/*  75 */     if ((accessFlags & 0x400) != 0) {
/*  76 */       return Modifier.ABSTRACT;
/*     */     }
/*  78 */     if ((accessFlags & 0x10) != 0) {
/*  79 */       return Modifier.FINAL;
/*     */     }
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   public NestingKind getNestingKind()
/*     */   {
/*  89 */     switch ($SWITCH_TABLE$javax$tools$JavaFileObject$Kind()[this.kind.ordinal()]) {
/*     */     case 1:
/*  91 */       return NestingKind.TOP_LEVEL;
/*     */     case 2:
/*  93 */       ClassFileReader reader = null;
/*     */       try {
/*  95 */         reader = ClassFileReader.read(this.f);
/*     */       }
/*     */       catch (ClassFormatException localClassFormatException) {
/*     */       }
/*     */       catch (IOException localIOException) {
/*     */       }
/* 101 */       if (reader == null) {
/* 102 */         return null;
/*     */       }
/* 104 */       if (reader.isAnonymous()) {
/* 105 */         return NestingKind.ANONYMOUS;
/*     */       }
/* 107 */       if (reader.isLocal()) {
/* 108 */         return NestingKind.LOCAL;
/*     */       }
/* 110 */       if (reader.isMember()) {
/* 111 */         return NestingKind.MEMBER;
/*     */       }
/* 113 */       return NestingKind.TOP_LEVEL;
/*     */     }
/* 115 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean delete()
/*     */   {
/* 124 */     return this.f.delete();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 129 */     if (!(o instanceof EclipseFileObject)) {
/* 130 */       return false;
/*     */     }
/* 132 */     EclipseFileObject eclipseFileObject = (EclipseFileObject)o;
/* 133 */     return eclipseFileObject.toUri().equals(this.uri);
/*     */   }
/*     */ 
/*     */   public CharSequence getCharContent(boolean ignoreEncodingErrors)
/*     */     throws IOException
/*     */   {
/* 141 */     return Util.getCharContents(this, ignoreEncodingErrors, org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(this.f), this.charset.toString());
/*     */   }
/*     */ 
/*     */   public long getLastModified()
/*     */   {
/* 149 */     return this.f.lastModified();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 154 */     return this.f.getPath();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 159 */     return this.f.hashCode();
/*     */   }
/*     */ 
/*     */   public InputStream openInputStream()
/*     */     throws IOException
/*     */   {
/* 168 */     return new FileInputStream(this.f);
/*     */   }
/*     */ 
/*     */   public OutputStream openOutputStream()
/*     */     throws IOException
/*     */   {
/* 176 */     ensureParentDirectoriesExist();
/* 177 */     return new FileOutputStream(this.f);
/*     */   }
/*     */ 
/*     */   public Reader openReader(boolean ignoreEncodingErrors)
/*     */     throws IOException
/*     */   {
/* 185 */     return new FileReader(this.f);
/*     */   }
/*     */ 
/*     */   public Writer openWriter()
/*     */     throws IOException
/*     */   {
/* 193 */     ensureParentDirectoriesExist();
/* 194 */     return new FileWriter(this.f);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 199 */     return this.f.getAbsolutePath();
/*     */   }
/*     */ 
/*     */   private void ensureParentDirectoriesExist() throws IOException {
/* 203 */     if (!this.parentsExist) {
/* 204 */       File parent = this.f.getParentFile();
/* 205 */       if ((parent != null) && (!parent.exists()) && 
/* 206 */         (!parent.mkdirs()))
/*     */       {
/* 208 */         if ((!parent.exists()) || (!parent.isDirectory())) {
/* 209 */           throw new IOException("Unable to create parent directories for " + this.f);
/*     */         }
/*     */       }
/* 212 */       this.parentsExist = true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.tool.EclipseFileObject
 * JD-Core Version:    0.6.0
 */