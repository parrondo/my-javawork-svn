/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ 
/*     */ final class SegmentNorms
/*     */   implements Cloneable
/*     */ {
/*     */   static final byte[] NORMS_HEADER;
/*  40 */   int refCount = 1;
/*     */   private SegmentNorms origNorm;
/*     */   private IndexInput in;
/*     */   private long normSeek;
/*     */   private AtomicInteger bytesRef;
/*     */   private byte[] bytes;
/*     */   private int number;
/*     */   boolean dirty;
/*     */   boolean rollbackDirty;
/*     */   private final SegmentReader owner;
/*     */ 
/*     */   public SegmentNorms(IndexInput in, int number, long normSeek, SegmentReader owner)
/*     */   {
/*  60 */     this.in = in;
/*  61 */     this.number = number;
/*  62 */     this.normSeek = normSeek;
/*  63 */     this.owner = owner;
/*     */   }
/*     */ 
/*     */   public synchronized void incRef() {
/*  67 */     assert ((this.refCount > 0) && ((this.origNorm == null) || (this.origNorm.refCount > 0)));
/*  68 */     this.refCount += 1;
/*     */   }
/*     */ 
/*     */   private void closeInput() throws IOException {
/*  72 */     if (this.in != null) {
/*  73 */       if (this.in != this.owner.singleNormStream)
/*     */       {
/*  75 */         this.in.close();
/*     */       }
/*  79 */       else if (this.owner.singleNormRef.decrementAndGet() == 0) {
/*  80 */         this.owner.singleNormStream.close();
/*  81 */         this.owner.singleNormStream = null;
/*     */       }
/*     */ 
/*  85 */       this.in = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void decRef() throws IOException {
/*  90 */     assert ((this.refCount > 0) && ((this.origNorm == null) || (this.origNorm.refCount > 0)));
/*     */ 
/*  92 */     if (--this.refCount == 0) {
/*  93 */       if (this.origNorm != null) {
/*  94 */         this.origNorm.decRef();
/*  95 */         this.origNorm = null;
/*     */       } else {
/*  97 */         closeInput();
/*     */       }
/*     */ 
/* 100 */       if (this.bytes != null) {
/* 101 */         assert (this.bytesRef != null);
/* 102 */         this.bytesRef.decrementAndGet();
/* 103 */         this.bytes = null;
/* 104 */         this.bytesRef = null;
/*     */       } else {
/* 106 */         assert (this.bytesRef == null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void bytes(byte[] bytesOut, int offset, int len)
/*     */     throws IOException
/*     */   {
/* 114 */     assert ((this.refCount > 0) && ((this.origNorm == null) || (this.origNorm.refCount > 0)));
/* 115 */     if (this.bytes != null)
/*     */     {
/* 117 */       assert (len <= this.owner.maxDoc());
/* 118 */       System.arraycopy(this.bytes, 0, bytesOut, offset, len);
/*     */     }
/* 121 */     else if (this.origNorm != null)
/*     */     {
/* 123 */       this.origNorm.bytes(bytesOut, offset, len);
/*     */     }
/*     */     else {
/* 126 */       synchronized (this.in) {
/* 127 */         this.in.seek(this.normSeek);
/* 128 */         this.in.readBytes(bytesOut, offset, len, false);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized byte[] bytes()
/*     */     throws IOException
/*     */   {
/* 136 */     assert ((this.refCount > 0) && ((this.origNorm == null) || (this.origNorm.refCount > 0)));
/* 137 */     if (this.bytes == null) {
/* 138 */       assert (this.bytesRef == null);
/* 139 */       if (this.origNorm != null)
/*     */       {
/* 143 */         this.bytes = this.origNorm.bytes();
/* 144 */         this.bytesRef = this.origNorm.bytesRef;
/* 145 */         this.bytesRef.incrementAndGet();
/*     */ 
/* 149 */         this.origNorm.decRef();
/* 150 */         this.origNorm = null;
/*     */       }
/*     */       else
/*     */       {
/* 155 */         int count = this.owner.maxDoc();
/* 156 */         this.bytes = new byte[count];
/*     */ 
/* 159 */         assert (this.in != null);
/*     */ 
/* 162 */         synchronized (this.in) {
/* 163 */           this.in.seek(this.normSeek);
/* 164 */           this.in.readBytes(this.bytes, 0, count, false);
/*     */         }
/*     */ 
/* 167 */         this.bytesRef = new AtomicInteger(1);
/* 168 */         closeInput();
/*     */       }
/*     */     }
/*     */ 
/* 172 */     return this.bytes;
/*     */   }
/*     */ 
/*     */   AtomicInteger bytesRef()
/*     */   {
/* 177 */     return this.bytesRef;
/*     */   }
/*     */ 
/*     */   public synchronized byte[] copyOnWrite()
/*     */     throws IOException
/*     */   {
/* 183 */     assert ((this.refCount > 0) && ((this.origNorm == null) || (this.origNorm.refCount > 0)));
/* 184 */     bytes();
/* 185 */     assert (this.bytes != null);
/* 186 */     assert (this.bytesRef != null);
/* 187 */     if (this.bytesRef.get() > 1)
/*     */     {
/* 191 */       assert (this.refCount == 1);
/* 192 */       AtomicInteger oldRef = this.bytesRef;
/* 193 */       this.bytes = this.owner.cloneNormBytes(this.bytes);
/* 194 */       this.bytesRef = new AtomicInteger(1);
/* 195 */       oldRef.decrementAndGet();
/*     */     }
/* 197 */     this.dirty = true;
/* 198 */     return this.bytes;
/*     */   }
/*     */ 
/*     */   public synchronized Object clone() {
/* 205 */     assert ((this.refCount > 0) && ((this.origNorm == null) || (this.origNorm.refCount > 0)));
/*     */     SegmentNorms clone;
/*     */     try {
/* 209 */       clone = (SegmentNorms)super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cnse) {
/* 212 */       throw new RuntimeException("unexpected CloneNotSupportedException", cnse);
/*     */     }
/* 214 */     clone.refCount = 1;
/*     */ 
/* 216 */     if (this.bytes != null) {
/* 217 */       assert (this.bytesRef != null);
/* 218 */       assert (this.origNorm == null);
/*     */ 
/* 221 */       clone.bytesRef.incrementAndGet();
/*     */     } else {
/* 223 */       assert (this.bytesRef == null);
/* 224 */       if (this.origNorm == null)
/*     */       {
/* 226 */         clone.origNorm = this;
/*     */       }
/* 228 */       clone.origNorm.incRef();
/*     */     }
/*     */ 
/* 232 */     clone.in = null;
/*     */ 
/* 234 */     return clone;
/*     */   }
/*     */ 
/*     */   public void reWrite(SegmentInfo si)
/*     */     throws IOException
/*     */   {
/* 240 */     assert ((this.refCount > 0) && ((this.origNorm == null) || (this.origNorm.refCount > 0))) : ("refCount=" + this.refCount + " origNorm=" + this.origNorm);
/*     */ 
/* 243 */     si.advanceNormGen(this.number);
/* 244 */     String normFileName = si.getNormFileName(this.number);
/* 245 */     IndexOutput out = this.owner.directory().createOutput(normFileName);
/* 246 */     boolean success = false;
/*     */     try {
/*     */       try {
/* 249 */         out.writeBytes(NORMS_HEADER, 0, NORMS_HEADER.length);
/* 250 */         out.writeBytes(this.bytes, this.owner.maxDoc());
/*     */       } finally {
/* 252 */         out.close();
/*     */       }
/* 254 */       success = true;
/*     */     } finally {
/* 256 */       if (!success) {
/*     */         try {
/* 258 */           this.owner.directory().deleteFile(normFileName);
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 265 */     this.dirty = false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  38 */     NORMS_HEADER = new byte[] { 78, 82, 77, -1 };
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentNorms
 * JD-Core Version:    0.6.0
 */