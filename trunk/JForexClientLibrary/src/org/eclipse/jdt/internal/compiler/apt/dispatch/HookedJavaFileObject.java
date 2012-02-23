/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Writer;
/*     */ import javax.tools.ForwardingJavaFileObject;
/*     */ import javax.tools.JavaFileObject;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.Compiler;
/*     */ import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
/*     */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
/*     */ import org.eclipse.jdt.internal.compiler.env.IBinaryType;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ 
/*     */ public class HookedJavaFileObject extends ForwardingJavaFileObject<JavaFileObject>
/*     */ {
/*     */   protected final BatchFilerImpl _filer;
/*     */   protected final String _fileName;
/* 195 */   private boolean _closed = false;
/*     */ 
/*     */   public HookedJavaFileObject(JavaFileObject fileObject, String fileName, BatchFilerImpl filer) {
/* 198 */     super(fileObject);
/* 199 */     this._filer = filer;
/* 200 */     this._fileName = fileName;
/*     */   }
/*     */ 
/*     */   public OutputStream openOutputStream() throws IOException
/*     */   {
/* 205 */     return new ForwardingOutputStream(super.openOutputStream());
/*     */   }
/*     */ 
/*     */   public Writer openWriter() throws IOException
/*     */   {
/* 210 */     return new ForwardingWriter(super.openWriter());
/*     */   }
/*     */ 
/*     */   protected void closed() {
/* 214 */     if (!this._closed) {
/* 215 */       this._closed = true;
/*     */ 
/* 217 */       switch ($SWITCH_TABLE$javax$tools$JavaFileObject$Kind()[getKind().ordinal()]) {
/*     */       case 1:
/* 219 */         CompilationUnit unit = new CompilationUnit(null, this._fileName, null);
/* 220 */         this._filer.addNewUnit(unit);
/* 221 */         break;
/*     */       case 2:
/* 223 */         IBinaryType binaryType = null;
/*     */         try {
/* 225 */           binaryType = ClassFileReader.read(this._fileName);
/*     */         }
/*     */         catch (ClassFormatException localClassFormatException) {
/*     */         }
/*     */         catch (IOException localIOException) {
/*     */         }
/* 231 */         if (binaryType == null) break;
/* 232 */         char[] name = binaryType.getName();
/* 233 */         ReferenceBinding type = this._filer._env._compiler.lookupEnvironment.getType(CharOperation.splitOn('/', name));
/* 234 */         if ((type == null) || (!type.isValidBinding()) || (!type.isBinaryBinding())) break;
/* 235 */         this._filer.addNewClassFile(type);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ForwardingOutputStream extends OutputStream
/*     */   {
/*     */     private final OutputStream _os;
/*     */ 
/*     */     ForwardingOutputStream(OutputStream os)
/*     */     {
/* 126 */       this._os = os;
/*     */     }
/*     */ 
/*     */     public void close() throws IOException
/*     */     {
/* 131 */       this._os.close();
/* 132 */       HookedJavaFileObject.this.closed();
/*     */     }
/*     */ 
/*     */     public void flush() throws IOException {
/* 136 */       this._os.flush();
/*     */     }
/*     */ 
/*     */     public void write(byte[] b, int off, int len) throws IOException {
/* 140 */       this._os.write(b, off, len);
/*     */     }
/*     */ 
/*     */     public void write(byte[] b) throws IOException {
/* 144 */       this._os.write(b);
/*     */     }
/*     */ 
/*     */     public void write(int b) throws IOException {
/* 148 */       this._os.write(b);
/*     */     }
/*     */ 
/*     */     protected Object clone() throws CloneNotSupportedException {
/* 152 */       return new ForwardingOutputStream(HookedJavaFileObject.this, this._os);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 156 */       return this._os.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj) {
/* 160 */       if (this == obj)
/* 161 */         return true;
/* 162 */       if (obj == null)
/* 163 */         return false;
/* 164 */       if (getClass() != obj.getClass())
/* 165 */         return false;
/* 166 */       ForwardingOutputStream other = (ForwardingOutputStream)obj;
/* 167 */       if (this._os == null) {
/* 168 */         if (other._os != null)
/* 169 */           return false;
/* 170 */       } else if (!this._os.equals(other._os))
/* 171 */         return false;
/* 172 */       return true;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 176 */       return "ForwardingOutputStream wrapping " + this._os.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ForwardingWriter extends Writer
/*     */   {
/*     */     private final Writer _w;
/*     */ 
/*     */     ForwardingWriter(Writer w)
/*     */     {
/*  42 */       this._w = w;
/*     */     }
/*     */ 
/*     */     public Writer append(char c) throws IOException {
/*  46 */       return this._w.append(c);
/*     */     }
/*     */ 
/*     */     public Writer append(CharSequence csq, int start, int end) throws IOException
/*     */     {
/*  51 */       return this._w.append(csq, start, end);
/*     */     }
/*     */ 
/*     */     public Writer append(CharSequence csq) throws IOException {
/*  55 */       return this._w.append(csq);
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/*  61 */       this._w.close();
/*  62 */       HookedJavaFileObject.this.closed();
/*     */     }
/*     */ 
/*     */     public void flush() throws IOException {
/*  66 */       this._w.flush();
/*     */     }
/*     */ 
/*     */     public void write(char[] cbuf) throws IOException {
/*  70 */       this._w.write(cbuf);
/*     */     }
/*     */ 
/*     */     public void write(int c) throws IOException {
/*  74 */       this._w.write(c);
/*     */     }
/*     */ 
/*     */     public void write(String str, int off, int len) throws IOException
/*     */     {
/*  79 */       this._w.write(str, off, len);
/*     */     }
/*     */ 
/*     */     public void write(String str) throws IOException {
/*  83 */       this._w.write(str);
/*     */     }
/*     */ 
/*     */     public void write(char[] cbuf, int off, int len) throws IOException
/*     */     {
/*  88 */       this._w.write(cbuf, off, len);
/*     */     }
/*     */ 
/*     */     protected Object clone() throws CloneNotSupportedException {
/*  92 */       return new ForwardingWriter(HookedJavaFileObject.this, this._w);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/*  96 */       return this._w.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj) {
/* 100 */       if (this == obj)
/* 101 */         return true;
/* 102 */       if (obj == null)
/* 103 */         return false;
/* 104 */       if (getClass() != obj.getClass())
/* 105 */         return false;
/* 106 */       ForwardingWriter other = (ForwardingWriter)obj;
/* 107 */       if (this._w == null) {
/* 108 */         if (other._w != null)
/* 109 */           return false;
/* 110 */       } else if (!this._w.equals(other._w))
/* 111 */         return false;
/* 112 */       return true;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 116 */       return "ForwardingWriter wrapping " + this._w.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.HookedJavaFileObject
 * JD-Core Version:    0.6.0
 */