/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ import org.apache.lucene.util.UnicodeUtil;
/*     */ import org.apache.lucene.util.UnicodeUtil.UTF16Result;
/*     */ import org.apache.lucene.util.UnicodeUtil.UTF8Result;
/*     */ 
/*     */ final class TermInfosWriter
/*     */   implements Closeable
/*     */ {
/*     */   public static final int FORMAT = -3;
/*     */   public static final int FORMAT_VERSION_UTF8_LENGTH_IN_BYTES = -4;
/*     */   public static final int FORMAT_CURRENT = -4;
/*     */   private FieldInfos fieldInfos;
/*     */   private IndexOutput output;
/*  46 */   private TermInfo lastTi = new TermInfo();
/*     */   private long size;
/*  61 */   int indexInterval = 128;
/*     */ 
/*  68 */   int skipInterval = 16;
/*     */ 
/*  73 */   int maxSkipLevels = 10;
/*     */   private long lastIndexPointer;
/*     */   private boolean isIndex;
/*  77 */   private byte[] lastTermBytes = new byte[10];
/*  78 */   private int lastTermBytesLength = 0;
/*  79 */   private int lastFieldNumber = -1;
/*     */   private TermInfosWriter other;
/*  82 */   private UnicodeUtil.UTF8Result utf8Result = new UnicodeUtil.UTF8Result();
/*     */   UnicodeUtil.UTF16Result utf16Result1;
/*     */   UnicodeUtil.UTF16Result utf16Result2;
/*     */ 
/*     */   TermInfosWriter(Directory directory, String segment, FieldInfos fis, int interval)
/*     */     throws IOException
/*     */   {
/*  87 */     initialize(directory, segment, fis, interval, false);
/*  88 */     boolean success = false;
/*     */     try {
/*  90 */       this.other = new TermInfosWriter(directory, segment, fis, interval, true);
/*  91 */       this.other.other = this;
/*  92 */       success = true;
/*     */     } finally {
/*  94 */       if (!success)
/*  95 */         IOUtils.closeWhileHandlingException(new Closeable[] { this.output, this.other });
/*     */     }
/*     */   }
/*     */ 
/*     */   private TermInfosWriter(Directory directory, String segment, FieldInfos fis, int interval, boolean isIndex)
/*     */     throws IOException
/*     */   {
/* 102 */     initialize(directory, segment, fis, interval, isIndex);
/*     */   }
/*     */ 
/*     */   private void initialize(Directory directory, String segment, FieldInfos fis, int interval, boolean isi) throws IOException
/*     */   {
/* 107 */     this.indexInterval = interval;
/* 108 */     this.fieldInfos = fis;
/* 109 */     this.isIndex = isi;
/* 110 */     this.output = directory.createOutput(segment + (this.isIndex ? ".tii" : ".tis"));
/* 111 */     boolean success = false;
/*     */     try {
/* 113 */       this.output.writeInt(-4);
/* 114 */       this.output.writeLong(0L);
/* 115 */       this.output.writeInt(this.indexInterval);
/* 116 */       this.output.writeInt(this.skipInterval);
/* 117 */       this.output.writeInt(this.maxSkipLevels);
/* 118 */       assert (initUTF16Results());
/* 119 */       success = true;
/*     */     } finally {
/* 121 */       if (!success)
/* 122 */         IOUtils.closeWhileHandlingException(new Closeable[] { this.output });
/*     */     }
/*     */   }
/*     */ 
/*     */   void add(Term term, TermInfo ti) throws IOException
/*     */   {
/* 128 */     UnicodeUtil.UTF16toUTF8(term.text, 0, term.text.length(), this.utf8Result);
/* 129 */     add(this.fieldInfos.fieldNumber(term.field), this.utf8Result.result, this.utf8Result.length, ti);
/*     */   }
/*     */ 
/*     */   private boolean initUTF16Results()
/*     */   {
/* 138 */     this.utf16Result1 = new UnicodeUtil.UTF16Result();
/* 139 */     this.utf16Result2 = new UnicodeUtil.UTF16Result();
/* 140 */     return true;
/*     */   }
/*     */ 
/*     */   private int compareToLastTerm(int fieldNumber, byte[] termBytes, int termBytesLength)
/*     */   {
/* 146 */     if (this.lastFieldNumber != fieldNumber) {
/* 147 */       int cmp = this.fieldInfos.fieldName(this.lastFieldNumber).compareTo(this.fieldInfos.fieldName(fieldNumber));
/*     */ 
/* 152 */       if ((cmp != 0) || (this.lastFieldNumber != -1)) {
/* 153 */         return cmp;
/*     */       }
/*     */     }
/* 156 */     UnicodeUtil.UTF8toUTF16(this.lastTermBytes, 0, this.lastTermBytesLength, this.utf16Result1);
/* 157 */     UnicodeUtil.UTF8toUTF16(termBytes, 0, termBytesLength, this.utf16Result2);
/*     */     int len;
/*     */     int len;
/* 159 */     if (this.utf16Result1.length < this.utf16Result2.length)
/* 160 */       len = this.utf16Result1.length;
/*     */     else {
/* 162 */       len = this.utf16Result2.length;
/*     */     }
/* 164 */     for (int i = 0; i < len; i++) {
/* 165 */       char ch1 = this.utf16Result1.result[i];
/* 166 */       char ch2 = this.utf16Result2.result[i];
/* 167 */       if (ch1 != ch2)
/* 168 */         return ch1 - ch2;
/*     */     }
/* 170 */     return this.utf16Result1.length - this.utf16Result2.length;
/*     */   }
/*     */ 
/*     */   void add(int fieldNumber, byte[] termBytes, int termBytesLength, TermInfo ti)
/*     */     throws IOException
/*     */   {
/* 181 */     assert ((compareToLastTerm(fieldNumber, termBytes, termBytesLength) < 0) || ((this.isIndex) && (termBytesLength == 0) && (this.lastTermBytesLength == 0))) : ("Terms are out of order: field=" + this.fieldInfos.fieldName(fieldNumber) + " (number " + fieldNumber + ")" + " lastField=" + this.fieldInfos.fieldName(this.lastFieldNumber) + " (number " + this.lastFieldNumber + ")" + " text=" + new String(termBytes, 0, termBytesLength, "UTF-8") + " lastText=" + new String(this.lastTermBytes, 0, this.lastTermBytesLength, "UTF-8"));
/*     */ 
/* 185 */     assert (ti.freqPointer >= this.lastTi.freqPointer) : ("freqPointer out of order (" + ti.freqPointer + " < " + this.lastTi.freqPointer + ")");
/* 186 */     assert (ti.proxPointer >= this.lastTi.proxPointer) : ("proxPointer out of order (" + ti.proxPointer + " < " + this.lastTi.proxPointer + ")");
/*     */ 
/* 188 */     if ((!this.isIndex) && (this.size % this.indexInterval == 0L)) {
/* 189 */       this.other.add(this.lastFieldNumber, this.lastTermBytes, this.lastTermBytesLength, this.lastTi);
/*     */     }
/* 191 */     writeTerm(fieldNumber, termBytes, termBytesLength);
/*     */ 
/* 193 */     this.output.writeVInt(ti.docFreq);
/* 194 */     this.output.writeVLong(ti.freqPointer - this.lastTi.freqPointer);
/* 195 */     this.output.writeVLong(ti.proxPointer - this.lastTi.proxPointer);
/*     */ 
/* 197 */     if (ti.docFreq >= this.skipInterval) {
/* 198 */       this.output.writeVInt(ti.skipOffset);
/*     */     }
/*     */ 
/* 201 */     if (this.isIndex) {
/* 202 */       this.output.writeVLong(this.other.output.getFilePointer() - this.lastIndexPointer);
/* 203 */       this.lastIndexPointer = this.other.output.getFilePointer();
/*     */     }
/*     */ 
/* 206 */     this.lastFieldNumber = fieldNumber;
/* 207 */     this.lastTi.set(ti);
/* 208 */     this.size += 1L;
/*     */   }
/*     */ 
/*     */   private void writeTerm(int fieldNumber, byte[] termBytes, int termBytesLength)
/*     */     throws IOException
/*     */   {
/* 216 */     int start = 0;
/* 217 */     int limit = termBytesLength < this.lastTermBytesLength ? termBytesLength : this.lastTermBytesLength;
/* 218 */     while ((start < limit) && 
/* 219 */       (termBytes[start] == this.lastTermBytes[start]))
/*     */     {
/* 221 */       start++;
/*     */     }
/*     */ 
/* 224 */     int length = termBytesLength - start;
/* 225 */     this.output.writeVInt(start);
/* 226 */     this.output.writeVInt(length);
/* 227 */     this.output.writeBytes(termBytes, start, length);
/* 228 */     this.output.writeVInt(fieldNumber);
/* 229 */     if (this.lastTermBytes.length < termBytesLength) {
/* 230 */       this.lastTermBytes = ArrayUtil.grow(this.lastTermBytes, termBytesLength);
/*     */     }
/* 232 */     System.arraycopy(termBytes, start, this.lastTermBytes, start, length);
/* 233 */     this.lastTermBytesLength = termBytesLength;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/*     */     try {
/* 239 */       this.output.seek(4L);
/* 240 */       this.output.writeLong(this.size);
/*     */     }
/*     */     finally
/*     */     {
/*     */     }
/*     */ 
/* 246 */     ret;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermInfosWriter
 * JD-Core Version:    0.6.0
 */