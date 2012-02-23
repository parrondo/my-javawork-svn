/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ 
/*     */ final class SegmentTermEnum extends TermEnum
/*     */   implements Cloneable
/*     */ {
/*     */   private IndexInput input;
/*     */   FieldInfos fieldInfos;
/*     */   long size;
/*  27 */   long position = -1L;
/*     */ 
/*  29 */   private TermBuffer termBuffer = new TermBuffer();
/*  30 */   private TermBuffer prevBuffer = new TermBuffer();
/*  31 */   private TermBuffer scanBuffer = new TermBuffer();
/*     */ 
/*  33 */   private TermInfo termInfo = new TermInfo();
/*     */   private int format;
/*  36 */   private boolean isIndex = false;
/*  37 */   long indexPointer = 0L;
/*     */   int indexInterval;
/*     */   int skipInterval;
/*     */   int maxSkipLevels;
/*     */   private int formatM1SkipInterval;
/*     */ 
/*     */   SegmentTermEnum(IndexInput i, FieldInfos fis, boolean isi)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/*  45 */     this.input = i;
/*  46 */     this.fieldInfos = fis;
/*  47 */     this.isIndex = isi;
/*  48 */     this.maxSkipLevels = 1;
/*     */ 
/*  50 */     int firstInt = this.input.readInt();
/*  51 */     if (firstInt >= 0)
/*     */     {
/*  53 */       this.format = 0;
/*  54 */       this.size = firstInt;
/*     */ 
/*  57 */       this.indexInterval = 128;
/*  58 */       this.skipInterval = 2147483647;
/*     */     }
/*     */     else {
/*  61 */       this.format = firstInt;
/*     */ 
/*  64 */       if (this.format < -4) {
/*  65 */         throw new CorruptIndexException("Unknown format version:" + this.format + " expected " + -4 + " or higher");
/*     */       }
/*  67 */       this.size = this.input.readLong();
/*     */ 
/*  69 */       if (this.format == -1) {
/*  70 */         if (!this.isIndex) {
/*  71 */           this.indexInterval = this.input.readInt();
/*  72 */           this.formatM1SkipInterval = this.input.readInt();
/*     */         }
/*     */ 
/*  76 */         this.skipInterval = 2147483647;
/*     */       } else {
/*  78 */         this.indexInterval = this.input.readInt();
/*  79 */         this.skipInterval = this.input.readInt();
/*  80 */         if (this.format <= -3)
/*     */         {
/*  82 */           this.maxSkipLevels = this.input.readInt();
/*     */         }
/*     */       }
/*  85 */       assert (this.indexInterval > 0) : ("indexInterval=" + this.indexInterval + " is negative; must be > 0");
/*  86 */       assert (this.skipInterval > 0) : ("skipInterval=" + this.skipInterval + " is negative; must be > 0");
/*     */     }
/*  88 */     if (this.format > -4) {
/*  89 */       this.termBuffer.setPreUTF8Strings();
/*  90 */       this.scanBuffer.setPreUTF8Strings();
/*  91 */       this.prevBuffer.setPreUTF8Strings();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Object clone()
/*     */   {
/*  97 */     SegmentTermEnum clone = null;
/*     */     try {
/*  99 */       clone = (SegmentTermEnum)super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/*     */     }
/* 102 */     clone.input = ((IndexInput)this.input.clone());
/* 103 */     clone.termInfo = new TermInfo(this.termInfo);
/*     */ 
/* 105 */     clone.termBuffer = ((TermBuffer)this.termBuffer.clone());
/* 106 */     clone.prevBuffer = ((TermBuffer)this.prevBuffer.clone());
/* 107 */     clone.scanBuffer = new TermBuffer();
/*     */ 
/* 109 */     return clone;
/*     */   }
/*     */ 
/*     */   final void seek(long pointer, long p, Term t, TermInfo ti) throws IOException
/*     */   {
/* 114 */     this.input.seek(pointer);
/* 115 */     this.position = p;
/* 116 */     this.termBuffer.set(t);
/* 117 */     this.prevBuffer.reset();
/* 118 */     this.termInfo.set(ti);
/*     */   }
/*     */ 
/*     */   public final boolean next()
/*     */     throws IOException
/*     */   {
/* 124 */     if (this.position++ >= this.size - 1L) {
/* 125 */       this.prevBuffer.set(this.termBuffer);
/* 126 */       this.termBuffer.reset();
/* 127 */       return false;
/*     */     }
/*     */ 
/* 130 */     this.prevBuffer.set(this.termBuffer);
/* 131 */     this.termBuffer.read(this.input, this.fieldInfos);
/*     */ 
/* 133 */     this.termInfo.docFreq = this.input.readVInt();
/* 134 */     this.termInfo.freqPointer += this.input.readVLong();
/* 135 */     this.termInfo.proxPointer += this.input.readVLong();
/*     */ 
/* 137 */     if (this.format == -1)
/*     */     {
/* 140 */       if ((!this.isIndex) && 
/* 141 */         (this.termInfo.docFreq > this.formatM1SkipInterval)) {
/* 142 */         this.termInfo.skipOffset = this.input.readVInt();
/*     */       }
/*     */ 
/*     */     }
/* 147 */     else if (this.termInfo.docFreq >= this.skipInterval) {
/* 148 */       this.termInfo.skipOffset = this.input.readVInt();
/*     */     }
/*     */ 
/* 151 */     if (this.isIndex) {
/* 152 */       this.indexPointer += this.input.readVLong();
/*     */     }
/* 154 */     return true;
/*     */   }
/*     */ 
/*     */   final int scanTo(Term term)
/*     */     throws IOException
/*     */   {
/* 164 */     this.scanBuffer.set(term);
/* 165 */     int count = 0;
/* 166 */     while ((this.scanBuffer.compareTo(this.termBuffer) > 0) && (next())) {
/* 167 */       count++;
/*     */     }
/* 169 */     return count;
/*     */   }
/*     */ 
/*     */   public final Term term()
/*     */   {
/* 176 */     return this.termBuffer.toTerm();
/*     */   }
/*     */ 
/*     */   final Term prev()
/*     */   {
/* 181 */     return this.prevBuffer.toTerm();
/*     */   }
/*     */ 
/*     */   final TermInfo termInfo()
/*     */   {
/* 187 */     return new TermInfo(this.termInfo);
/*     */   }
/*     */ 
/*     */   final void termInfo(TermInfo ti)
/*     */   {
/* 193 */     ti.set(this.termInfo);
/*     */   }
/*     */ 
/*     */   public final int docFreq()
/*     */   {
/* 200 */     return this.termInfo.docFreq;
/*     */   }
/*     */ 
/*     */   final long freqPointer()
/*     */   {
/* 206 */     return this.termInfo.freqPointer;
/*     */   }
/*     */ 
/*     */   final long proxPointer()
/*     */   {
/* 212 */     return this.termInfo.proxPointer;
/*     */   }
/*     */ 
/*     */   public final void close()
/*     */     throws IOException
/*     */   {
/* 218 */     this.input.close();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentTermEnum
 * JD-Core Version:    0.6.0
 */