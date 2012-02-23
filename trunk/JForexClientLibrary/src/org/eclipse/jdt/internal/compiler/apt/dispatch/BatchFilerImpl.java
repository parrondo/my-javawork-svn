/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.util.HashSet;
/*     */ import javax.annotation.processing.Filer;
/*     */ import javax.annotation.processing.FilerException;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.tools.FileObject;
/*     */ import javax.tools.JavaFileManager;
/*     */ import javax.tools.JavaFileManager.Location;
/*     */ import javax.tools.JavaFileObject;
/*     */ import javax.tools.JavaFileObject.Kind;
/*     */ import javax.tools.StandardLocation;
/*     */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ 
/*     */ public class BatchFilerImpl
/*     */   implements Filer
/*     */ {
/*     */   protected final BaseAnnotationProcessorManager _dispatchManager;
/*     */   protected final BaseProcessingEnvImpl _env;
/*     */   protected final JavaFileManager _fileManager;
/*     */   protected final HashSet<URI> _createdFiles;
/*     */ 
/*     */   public BatchFilerImpl(BaseAnnotationProcessorManager dispatchManager, BatchProcessingEnvImpl env)
/*     */   {
/*  45 */     this._dispatchManager = dispatchManager;
/*  46 */     this._fileManager = env._fileManager;
/*  47 */     this._env = env;
/*  48 */     this._createdFiles = new HashSet();
/*     */   }
/*     */ 
/*     */   public void addNewUnit(ICompilationUnit unit) {
/*  52 */     this._env.addNewUnit(unit);
/*     */   }
/*     */ 
/*     */   public void addNewClassFile(ReferenceBinding binding) {
/*  56 */     this._env.addNewClassFile(binding);
/*     */   }
/*     */ 
/*     */   public JavaFileObject createClassFile(CharSequence name, Element[] originatingElements)
/*     */     throws IOException
/*     */   {
/*  67 */     JavaFileObject jfo = this._fileManager.getJavaFileForOutput(
/*  68 */       StandardLocation.CLASS_OUTPUT, name.toString(), JavaFileObject.Kind.CLASS, null);
/*  69 */     URI uri = jfo.toUri();
/*  70 */     if (this._createdFiles.contains(uri)) {
/*  71 */       throw new FilerException("Class file already created : " + name);
/*     */     }
/*     */ 
/*  74 */     this._createdFiles.add(uri);
/*  75 */     return new HookedJavaFileObject(jfo, jfo.getName(), this);
/*     */   }
/*     */ 
/*     */   public FileObject createResource(JavaFileManager.Location location, CharSequence pkg, CharSequence relativeName, Element[] originatingElements)
/*     */     throws IOException
/*     */   {
/*  86 */     FileObject fo = this._fileManager.getFileForOutput(
/*  87 */       location, pkg.toString(), relativeName.toString(), null);
/*  88 */     URI uri = fo.toUri();
/*  89 */     if (this._createdFiles.contains(uri)) {
/*  90 */       throw new FilerException("Resource already created : " + location + '/' + pkg + '/' + relativeName);
/*     */     }
/*     */ 
/*  93 */     this._createdFiles.add(uri);
/*  94 */     return fo;
/*     */   }
/*     */ 
/*     */   public JavaFileObject createSourceFile(CharSequence name, Element[] originatingElements)
/*     */     throws IOException
/*     */   {
/* 104 */     JavaFileObject jfo = this._fileManager.getJavaFileForOutput(
/* 105 */       StandardLocation.SOURCE_OUTPUT, name.toString(), JavaFileObject.Kind.SOURCE, null);
/* 106 */     URI uri = jfo.toUri();
/* 107 */     if (this._createdFiles.contains(uri)) {
/* 108 */       throw new FilerException("Source file already created : " + name);
/*     */     }
/*     */ 
/* 111 */     this._createdFiles.add(uri);
/*     */ 
/* 113 */     return new HookedJavaFileObject(jfo, jfo.getName(), this);
/*     */   }
/*     */ 
/*     */   public FileObject getResource(JavaFileManager.Location location, CharSequence pkg, CharSequence relativeName)
/*     */     throws IOException
/*     */   {
/* 123 */     FileObject fo = this._fileManager.getFileForInput(
/* 124 */       location, pkg.toString(), relativeName.toString());
/* 125 */     URI uri = fo.toUri();
/* 126 */     if (this._createdFiles.contains(uri)) {
/* 127 */       throw new FilerException("Resource already created : " + location + '/' + pkg + '/' + relativeName);
/*     */     }
/*     */ 
/* 130 */     this._createdFiles.add(uri);
/* 131 */     return fo;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl
 * JD-Core Version:    0.6.0
 */