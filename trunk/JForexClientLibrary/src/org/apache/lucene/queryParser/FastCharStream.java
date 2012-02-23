/*     */ package org.apache.lucene.queryParser;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Reader;
/*     */ 
/*     */ public final class FastCharStream
/*     */   implements CharStream
/*     */ {
/*  30 */   char[] buffer = null;
/*     */ 
/*  32 */   int bufferLength = 0;
/*  33 */   int bufferPosition = 0;
/*     */ 
/*  35 */   int tokenStart = 0;
/*  36 */   int bufferStart = 0;
/*     */   Reader input;
/*     */ 
/*     */   public FastCharStream(Reader r)
/*     */   {
/*  42 */     this.input = r;
/*     */   }
/*     */ 
/*     */   public final char readChar() throws IOException {
/*  46 */     if (this.bufferPosition >= this.bufferLength)
/*  47 */       refill();
/*  48 */     return this.buffer[(this.bufferPosition++)];
/*     */   }
/*     */ 
/*     */   private final void refill() throws IOException {
/*  52 */     int newPosition = this.bufferLength - this.tokenStart;
/*     */ 
/*  54 */     if (this.tokenStart == 0) {
/*  55 */       if (this.buffer == null) {
/*  56 */         this.buffer = new char[2048];
/*  57 */       } else if (this.bufferLength == this.buffer.length) {
/*  58 */         char[] newBuffer = new char[this.buffer.length * 2];
/*  59 */         System.arraycopy(this.buffer, 0, newBuffer, 0, this.bufferLength);
/*  60 */         this.buffer = newBuffer;
/*     */       }
/*     */     }
/*  63 */     else System.arraycopy(this.buffer, this.tokenStart, this.buffer, 0, newPosition);
/*     */ 
/*  66 */     this.bufferLength = newPosition;
/*  67 */     this.bufferPosition = newPosition;
/*  68 */     this.bufferStart += this.tokenStart;
/*  69 */     this.tokenStart = 0;
/*     */ 
/*  71 */     int charsRead = this.input.read(this.buffer, newPosition, this.buffer.length - newPosition);
/*     */ 
/*  73 */     if (charsRead == -1) {
/*  74 */       throw new IOException("read past eof");
/*     */     }
/*  76 */     this.bufferLength += charsRead;
/*     */   }
/*     */ 
/*     */   public final char BeginToken() throws IOException {
/*  80 */     this.tokenStart = this.bufferPosition;
/*  81 */     return readChar();
/*     */   }
/*     */ 
/*     */   public final void backup(int amount) {
/*  85 */     this.bufferPosition -= amount;
/*     */   }
/*     */ 
/*     */   public final String GetImage() {
/*  89 */     return new String(this.buffer, this.tokenStart, this.bufferPosition - this.tokenStart);
/*     */   }
/*     */ 
/*     */   public final char[] GetSuffix(int len) {
/*  93 */     char[] value = new char[len];
/*  94 */     System.arraycopy(this.buffer, this.bufferPosition - len, value, 0, len);
/*  95 */     return value;
/*     */   }
/*     */ 
/*     */   public final void Done() {
/*     */     try {
/* 100 */       this.input.close();
/*     */     } catch (IOException e) {
/* 102 */       System.err.println("Caught: " + e + "; ignoring.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public final int getColumn() {
/* 107 */     return this.bufferStart + this.bufferPosition;
/*     */   }
/*     */   public final int getLine() {
/* 110 */     return 1;
/*     */   }
/*     */   public final int getEndColumn() {
/* 113 */     return this.bufferStart + this.bufferPosition;
/*     */   }
/*     */   public final int getEndLine() {
/* 116 */     return 1;
/*     */   }
/*     */   public final int getBeginColumn() {
/* 119 */     return this.bufferStart + this.tokenStart;
/*     */   }
/*     */   public final int getBeginLine() {
/* 122 */     return 1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.queryParser.FastCharStream
 * JD-Core Version:    0.6.0
 */