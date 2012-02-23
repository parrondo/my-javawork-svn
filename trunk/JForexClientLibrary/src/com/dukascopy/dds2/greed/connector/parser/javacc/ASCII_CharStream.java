/*     */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ 
/*     */ public final class ASCII_CharStream
/*     */ {
/*     */   public static final boolean staticFlag = true;
/*     */   static int bufsize;
/*     */   static int available;
/*     */   static int tokenBegin;
/*  15 */   public static int bufpos = -1;
/*     */   private static int[] bufline;
/*     */   private static int[] bufcolumn;
/*  19 */   private static int column = 0;
/*  20 */   private static int line = 1;
/*     */ 
/*  22 */   private static boolean prevCharIsCR = false;
/*  23 */   private static boolean prevCharIsLF = false;
/*     */   private static Reader inputStream;
/*     */   private static char[] buffer;
/*  28 */   private static int maxNextCharInd = 0;
/*  29 */   private static int inBuf = 0;
/*     */ 
/*     */   private static final void ExpandBuff(boolean wrapAround) {
/*  32 */     char[] newbuffer = new char[bufsize + 2048];
/*  33 */     int[] newbufline = new int[bufsize + 2048];
/*  34 */     int[] newbufcolumn = new int[bufsize + 2048];
/*     */     try
/*     */     {
/*  37 */       if (wrapAround) {
/*  38 */         System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
/*  39 */         System.arraycopy(buffer, 0, newbuffer, bufsize - tokenBegin, bufpos);
/*  40 */         buffer = newbuffer;
/*     */ 
/*  42 */         System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
/*  43 */         System.arraycopy(bufline, 0, newbufline, bufsize - tokenBegin, bufpos);
/*  44 */         bufline = newbufline;
/*     */ 
/*  46 */         System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
/*  47 */         System.arraycopy(bufcolumn, 0, newbufcolumn, bufsize - tokenBegin, bufpos);
/*  48 */         bufcolumn = newbufcolumn;
/*     */ 
/*  50 */         maxNextCharInd = ASCII_CharStream.bufpos = bufpos + (bufsize - tokenBegin);
/*     */       } else {
/*  52 */         System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
/*  53 */         buffer = newbuffer;
/*     */ 
/*  55 */         System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
/*  56 */         bufline = newbufline;
/*     */ 
/*  58 */         System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
/*  59 */         bufcolumn = newbufcolumn;
/*     */ 
/*  61 */         maxNextCharInd = ASCII_CharStream.bufpos = bufpos - tokenBegin;
/*     */       }
/*     */     } catch (Throwable t) {
/*  64 */       throw new Error(t.getMessage());
/*     */     }
/*     */ 
/*  67 */     bufsize += 2048;
/*  68 */     available = bufsize;
/*  69 */     tokenBegin = 0;
/*     */   }
/*     */ 
/*     */   private static final void FillBuff() throws IOException {
/*  73 */     if (maxNextCharInd == available)
/*  74 */       if (available == bufsize) {
/*  75 */         if (tokenBegin > 2048) {
/*  76 */           bufpos = ASCII_CharStream.maxNextCharInd = 0;
/*  77 */           available = tokenBegin;
/*     */         }
/*  79 */         else if (tokenBegin < 0) {
/*  80 */           bufpos = ASCII_CharStream.maxNextCharInd = 0;
/*     */         } else {
/*  82 */           ExpandBuff(false);
/*     */         }
/*  84 */       } else if (available > tokenBegin) {
/*  85 */         available = bufsize;
/*     */       }
/*  87 */       else if (tokenBegin - available < 2048)
/*  88 */         ExpandBuff(true);
/*     */       else
/*  90 */         available = tokenBegin;
/*     */     try
/*     */     {
/*     */       int i;
/*  95 */       if ((i = inputStream.read(buffer, maxNextCharInd, available - maxNextCharInd)) == -1) {
/*  96 */         inputStream.close();
/*  97 */         throw new IOException();
/*     */       }
/*  99 */       maxNextCharInd += i;
/* 100 */       return;
/*     */     } catch (IOException e) {
/* 102 */       bufpos -= 1;
/* 103 */       backup(0);
/* 104 */       if (tokenBegin == -1)
/* 105 */         tokenBegin = bufpos; 
/*     */     }
/* 106 */     throw e;
/*     */   }
/*     */ 
/*     */   public static final char BeginToken() throws IOException
/*     */   {
/* 111 */     tokenBegin = -1;
/* 112 */     char c = readChar();
/* 113 */     tokenBegin = bufpos;
/*     */ 
/* 115 */     return c;
/*     */   }
/*     */ 
/*     */   private static final void UpdateLineColumn(char c) {
/* 119 */     column += 1;
/*     */ 
/* 121 */     if (prevCharIsLF) {
/* 122 */       prevCharIsLF = false;
/* 123 */       line += (ASCII_CharStream.column = 1);
/*     */     }
/* 125 */     else if (prevCharIsCR) {
/* 126 */       prevCharIsCR = false;
/* 127 */       if (c == '\n')
/* 128 */         prevCharIsLF = true;
/*     */       else {
/* 130 */         line += (ASCII_CharStream.column = 1);
/*     */       }
/*     */     }
/* 133 */     switch (c) {
/*     */     case '\r':
/* 135 */       prevCharIsCR = true;
/* 136 */       break;
/*     */     case '\n':
/* 138 */       prevCharIsLF = true;
/* 139 */       break;
/*     */     }
/*     */ 
/* 144 */     bufline[bufpos] = line;
/* 145 */     bufcolumn[bufpos] = column;
/*     */   }
/*     */ 
/*     */   public static final char readChar() throws IOException {
/* 149 */     if (inBuf > 0) {
/* 150 */       inBuf -= 1;
/* 151 */       return (char)(0xFF & buffer[(++bufpos)]);
/*     */     }
/*     */ 
/* 154 */     if (++bufpos >= maxNextCharInd) {
/* 155 */       FillBuff();
/*     */     }
/* 157 */     char c = (char)(0xFF & buffer[bufpos]);
/*     */ 
/* 159 */     UpdateLineColumn(c);
/* 160 */     return c;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final int getColumn()
/*     */   {
/* 169 */     return bufcolumn[bufpos];
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final int getLine()
/*     */   {
/* 178 */     return bufline[bufpos];
/*     */   }
/*     */ 
/*     */   public static final int getEndColumn() {
/* 182 */     return bufcolumn[bufpos];
/*     */   }
/*     */ 
/*     */   public static final int getEndLine() {
/* 186 */     return bufline[bufpos];
/*     */   }
/*     */ 
/*     */   public static final int getBeginColumn() {
/* 190 */     return bufcolumn[tokenBegin];
/*     */   }
/*     */ 
/*     */   public static final int getBeginLine() {
/* 194 */     return bufline[tokenBegin];
/*     */   }
/*     */ 
/*     */   public static final void backup(int amount)
/*     */   {
/* 199 */     inBuf += amount;
/* 200 */     if ((ASCII_CharStream.bufpos = bufpos - amount) < 0)
/* 201 */       bufpos += bufsize;
/*     */   }
/*     */ 
/*     */   public ASCII_CharStream(Reader dstream, int startline, int startcolumn, int buffersize) {
/* 205 */     if (inputStream != null) {
/* 206 */       throw new Error("\n   ERROR: Second call to the constructor of a static ASCII_CharStream.  You must\n       either use ReInit() or set the JavaCC option STATIC to false\n       during the generation of this class.");
/*     */     }
/*     */ 
/* 210 */     inputStream = dstream;
/* 211 */     line = startline;
/* 212 */     column = startcolumn - 1;
/*     */ 
/* 214 */     available = ASCII_CharStream.bufsize = buffersize;
/* 215 */     buffer = new char[buffersize];
/* 216 */     bufline = new int[buffersize];
/* 217 */     bufcolumn = new int[buffersize];
/*     */   }
/*     */ 
/*     */   public ASCII_CharStream(Reader dstream, int startline, int startcolumn) {
/* 221 */     this(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public static void ReInit(Reader dstream, int startline, int startcolumn, int buffersize) {
/* 225 */     inputStream = dstream;
/* 226 */     line = startline;
/* 227 */     column = startcolumn - 1;
/*     */ 
/* 229 */     if ((buffer == null) || (buffersize != buffer.length)) {
/* 230 */       available = ASCII_CharStream.bufsize = buffersize;
/* 231 */       buffer = new char[buffersize];
/* 232 */       bufline = new int[buffersize];
/* 233 */       bufcolumn = new int[buffersize];
/*     */     }
/* 235 */     prevCharIsLF = ASCII_CharStream.prevCharIsCR = 0;
/* 236 */     tokenBegin = ASCII_CharStream.inBuf = ASCII_CharStream.maxNextCharInd = 0;
/* 237 */     bufpos = -1;
/*     */   }
/*     */ 
/*     */   public static void ReInit(Reader dstream, int startline, int startcolumn) {
/* 241 */     ReInit(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public ASCII_CharStream(InputStream dstream, int startline, int startcolumn, int buffersize) {
/* 245 */     this(new InputStreamReader(dstream), startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public ASCII_CharStream(InputStream dstream, int startline, int startcolumn) {
/* 249 */     this(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public static void ReInit(InputStream dstream, int startline, int startcolumn, int buffersize) {
/* 253 */     ReInit(new InputStreamReader(dstream), startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public static void ReInit(InputStream dstream, int startline, int startcolumn) {
/* 257 */     ReInit(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public static final String GetImage() {
/* 261 */     if (bufpos >= tokenBegin) {
/* 262 */       return new String(buffer, tokenBegin, bufpos - tokenBegin + 1);
/*     */     }
/* 264 */     return new String(buffer, tokenBegin, bufsize - tokenBegin) + new String(buffer, 0, bufpos + 1);
/*     */   }
/*     */ 
/*     */   public static final char[] GetSuffix(int len) {
/* 268 */     char[] ret = new char[len];
/*     */ 
/* 270 */     if (bufpos + 1 >= len) {
/* 271 */       System.arraycopy(buffer, bufpos - len + 1, ret, 0, len);
/*     */     } else {
/* 273 */       System.arraycopy(buffer, bufsize - (len - bufpos - 1), ret, 0, len - bufpos - 1);
/* 274 */       System.arraycopy(buffer, 0, ret, len - bufpos - 1, bufpos + 1);
/*     */     }
/*     */ 
/* 277 */     return ret;
/*     */   }
/*     */ 
/*     */   public static void Done() {
/* 281 */     buffer = null;
/* 282 */     bufline = null;
/* 283 */     bufcolumn = null;
/*     */   }
/*     */ 
/*     */   public static void adjustBeginLineColumn(int newLine, int newCol)
/*     */   {
/* 290 */     int start = tokenBegin;
/*     */     int len;
/*     */     int len;
/* 293 */     if (bufpos >= tokenBegin)
/* 294 */       len = bufpos - tokenBegin + inBuf + 1;
/*     */     else {
/* 296 */       len = bufsize - tokenBegin + bufpos + 1 + inBuf;
/*     */     }
/*     */ 
/* 299 */     int i = 0; int j = 0; int k = 0;
/* 300 */     int nextColDiff = 0; int columnDiff = 0;
/*     */ 
/* 302 */     while (i < len) { start++; if (bufline[(j = start % bufsize)] != bufline[(k = start % bufsize)]) break;
/* 303 */       bufline[j] = newLine;
/* 304 */       nextColDiff = columnDiff + bufcolumn[k] - bufcolumn[j];
/* 305 */       bufcolumn[j] = (newCol + columnDiff);
/* 306 */       columnDiff = nextColDiff;
/* 307 */       i++;
/*     */     }
/*     */ 
/* 310 */     if (i < len) {
/* 311 */       bufline[j] = (newLine++);
/* 312 */       bufcolumn[j] = (newCol + columnDiff);
/*     */ 
/* 314 */       while (i++ < len) {
/* 315 */         start++; if (bufline[(j = start % bufsize)] != bufline[(start % bufsize)]) {
/* 316 */           bufline[j] = (newLine++); continue;
/*     */         }
/* 318 */         bufline[j] = newLine;
/*     */       }
/*     */     }
/*     */ 
/* 322 */     line = bufline[j];
/* 323 */     column = bufcolumn[j];
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.ASCII_CharStream
 * JD-Core Version:    0.6.0
 */