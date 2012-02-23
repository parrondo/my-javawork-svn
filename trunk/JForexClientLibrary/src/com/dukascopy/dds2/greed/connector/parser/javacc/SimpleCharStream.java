/*     */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class SimpleCharStream
/*     */ {
/*     */   public static final boolean staticFlag = true;
/*     */   static int bufsize;
/*     */   static int available;
/*     */   static int tokenBegin;
/*  18 */   public static int bufpos = -1;
/*     */   protected static int[] bufline;
/*     */   protected static int[] bufcolumn;
/*  22 */   protected static int column = 0;
/*  23 */   protected static int line = 1;
/*     */ 
/*  25 */   protected static boolean prevCharIsCR = false;
/*  26 */   protected static boolean prevCharIsLF = false;
/*     */   protected static Reader inputStream;
/*     */   protected static char[] buffer;
/*  31 */   protected static int maxNextCharInd = 0;
/*  32 */   protected static int inBuf = 0;
/*  33 */   protected static int tabSize = 8;
/*     */ 
/*  35 */   protected static void setTabSize(int i) { tabSize = i; } 
/*  36 */   protected static int getTabSize(int i) { return tabSize;
/*     */   }
/*     */ 
/*     */   protected static void ExpandBuff(boolean wrapAround)
/*     */   {
/*  41 */     char[] newbuffer = new char[bufsize + 2048];
/*  42 */     int[] newbufline = new int[bufsize + 2048];
/*  43 */     int[] newbufcolumn = new int[bufsize + 2048];
/*     */     try
/*     */     {
/*  47 */       if (wrapAround)
/*     */       {
/*  49 */         System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
/*  50 */         System.arraycopy(buffer, 0, newbuffer, bufsize - tokenBegin, bufpos);
/*  51 */         buffer = newbuffer;
/*     */ 
/*  53 */         System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
/*  54 */         System.arraycopy(bufline, 0, newbufline, bufsize - tokenBegin, bufpos);
/*  55 */         bufline = newbufline;
/*     */ 
/*  57 */         System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
/*  58 */         System.arraycopy(bufcolumn, 0, newbufcolumn, bufsize - tokenBegin, bufpos);
/*  59 */         bufcolumn = newbufcolumn;
/*     */ 
/*  61 */         maxNextCharInd = SimpleCharStream.bufpos = bufpos + (bufsize - tokenBegin);
/*     */       }
/*     */       else
/*     */       {
/*  65 */         System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
/*  66 */         buffer = newbuffer;
/*     */ 
/*  68 */         System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
/*  69 */         bufline = newbufline;
/*     */ 
/*  71 */         System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
/*  72 */         bufcolumn = newbufcolumn;
/*     */ 
/*  74 */         maxNextCharInd = SimpleCharStream.bufpos = bufpos - tokenBegin;
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  79 */       throw new Error(t.getMessage());
/*     */     }
/*     */ 
/*  83 */     bufsize += 2048;
/*  84 */     available = bufsize;
/*  85 */     tokenBegin = 0;
/*     */   }
/*     */ 
/*     */   protected static void FillBuff() throws IOException
/*     */   {
/*  90 */     if (maxNextCharInd == available)
/*     */     {
/*  92 */       if (available == bufsize)
/*     */       {
/*  94 */         if (tokenBegin > 2048)
/*     */         {
/*  96 */           bufpos = SimpleCharStream.maxNextCharInd = 0;
/*  97 */           available = tokenBegin;
/*     */         }
/*  99 */         else if (tokenBegin < 0) {
/* 100 */           bufpos = SimpleCharStream.maxNextCharInd = 0;
/*     */         } else {
/* 102 */           ExpandBuff(false);
/*     */         }
/* 104 */       } else if (available > tokenBegin)
/* 105 */         available = bufsize;
/* 106 */       else if (tokenBegin - available < 2048)
/* 107 */         ExpandBuff(true);
/*     */       else
/* 109 */         available = tokenBegin;
/*     */     }
/*     */     try
/*     */     {
/*     */       int i;
/* 114 */       if ((i = inputStream.read(buffer, maxNextCharInd, available - maxNextCharInd)) == -1)
/*     */       {
/* 116 */         inputStream.close();
/* 117 */         throw new IOException();
/*     */       }
/*     */ 
/* 120 */       maxNextCharInd += i;
/* 121 */       return;
/*     */     }
/*     */     catch (IOException e) {
/* 124 */       bufpos -= 1;
/* 125 */       backup(0);
/* 126 */       if (tokenBegin == -1)
/* 127 */         tokenBegin = bufpos; 
/*     */     }
/* 128 */     throw e;
/*     */   }
/*     */ 
/*     */   public static char BeginToken()
/*     */     throws IOException
/*     */   {
/* 135 */     tokenBegin = -1;
/* 136 */     char c = readChar();
/* 137 */     tokenBegin = bufpos;
/*     */ 
/* 139 */     return c;
/*     */   }
/*     */ 
/*     */   protected static void UpdateLineColumn(char c)
/*     */   {
/* 144 */     column += 1;
/*     */ 
/* 146 */     if (prevCharIsLF)
/*     */     {
/* 148 */       prevCharIsLF = false;
/* 149 */       line += (SimpleCharStream.column = 1);
/*     */     }
/* 151 */     else if (prevCharIsCR)
/*     */     {
/* 153 */       prevCharIsCR = false;
/* 154 */       if (c == '\n')
/*     */       {
/* 156 */         prevCharIsLF = true;
/*     */       }
/*     */       else {
/* 159 */         line += (SimpleCharStream.column = 1);
/*     */       }
/*     */     }
/* 162 */     switch (c)
/*     */     {
/*     */     case '\r':
/* 165 */       prevCharIsCR = true;
/* 166 */       break;
/*     */     case '\n':
/* 168 */       prevCharIsLF = true;
/* 169 */       break;
/*     */     case '\t':
/* 171 */       column -= 1;
/* 172 */       column += tabSize - column % tabSize;
/* 173 */       break;
/*     */     case '\013':
/*     */     case '\f':
/*     */     }
/*     */ 
/* 178 */     bufline[bufpos] = line;
/* 179 */     bufcolumn[bufpos] = column;
/*     */   }
/*     */ 
/*     */   public static char readChar()
/*     */     throws IOException
/*     */   {
/* 185 */     if (inBuf > 0)
/*     */     {
/* 187 */       inBuf -= 1;
/*     */ 
/* 189 */       if (++bufpos == bufsize) {
/* 190 */         bufpos = 0;
/*     */       }
/* 192 */       return buffer[bufpos];
/*     */     }
/*     */ 
/* 195 */     if (++bufpos >= maxNextCharInd) {
/* 196 */       FillBuff();
/*     */     }
/* 198 */     char c = buffer[bufpos];
/*     */ 
/* 200 */     UpdateLineColumn(c);
/* 201 */     return c;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static int getColumn()
/*     */   {
/* 211 */     return bufcolumn[bufpos];
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static int getLine()
/*     */   {
/* 221 */     return bufline[bufpos];
/*     */   }
/*     */ 
/*     */   public static int getEndColumn()
/*     */   {
/* 226 */     return bufcolumn[bufpos];
/*     */   }
/*     */ 
/*     */   public static int getEndLine()
/*     */   {
/* 231 */     return bufline[bufpos];
/*     */   }
/*     */ 
/*     */   public static int getBeginColumn()
/*     */   {
/* 236 */     return bufcolumn[tokenBegin];
/*     */   }
/*     */ 
/*     */   public static int getBeginLine()
/*     */   {
/* 241 */     return bufline[tokenBegin];
/*     */   }
/*     */ 
/*     */   public static void backup(int amount)
/*     */   {
/* 247 */     inBuf += amount;
/* 248 */     if ((SimpleCharStream.bufpos = bufpos - amount) < 0)
/* 249 */       bufpos += bufsize;
/*     */   }
/*     */ 
/*     */   public SimpleCharStream(Reader dstream, int startline, int startcolumn, int buffersize)
/*     */   {
/* 256 */     if (inputStream != null) {
/* 257 */       throw new Error("\n   ERROR: Second call to the constructor of a static SimpleCharStream.\n       You must either use ReInit() or set the JavaCC option STATIC to false\n       during the generation of this class.");
/*     */     }
/*     */ 
/* 260 */     inputStream = dstream;
/* 261 */     line = startline;
/* 262 */     column = startcolumn - 1;
/*     */ 
/* 264 */     available = SimpleCharStream.bufsize = buffersize;
/* 265 */     buffer = new char[buffersize];
/* 266 */     bufline = new int[buffersize];
/* 267 */     bufcolumn = new int[buffersize];
/*     */   }
/*     */ 
/*     */   public SimpleCharStream(Reader dstream, int startline, int startcolumn)
/*     */   {
/* 274 */     this(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public SimpleCharStream(Reader dstream)
/*     */   {
/* 280 */     this(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(Reader dstream, int startline, int startcolumn, int buffersize)
/*     */   {
/* 287 */     inputStream = dstream;
/* 288 */     line = startline;
/* 289 */     column = startcolumn - 1;
/*     */ 
/* 291 */     if ((buffer == null) || (buffersize != buffer.length))
/*     */     {
/* 293 */       available = SimpleCharStream.bufsize = buffersize;
/* 294 */       buffer = new char[buffersize];
/* 295 */       bufline = new int[buffersize];
/* 296 */       bufcolumn = new int[buffersize];
/*     */     }
/* 298 */     prevCharIsLF = SimpleCharStream.prevCharIsCR = 0;
/* 299 */     tokenBegin = SimpleCharStream.inBuf = SimpleCharStream.maxNextCharInd = 0;
/* 300 */     bufpos = -1;
/*     */   }
/*     */ 
/*     */   public void ReInit(Reader dstream, int startline, int startcolumn)
/*     */   {
/* 307 */     ReInit(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(Reader dstream)
/*     */   {
/* 313 */     ReInit(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 319 */     this(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */   public SimpleCharStream(InputStream dstream, int startline, int startcolumn, int buffersize)
/*     */   {
/* 326 */     this(new InputStreamReader(dstream), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */   public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 333 */     this(dstream, encoding, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public SimpleCharStream(InputStream dstream, int startline, int startcolumn)
/*     */   {
/* 340 */     this(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public SimpleCharStream(InputStream dstream, String encoding)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 346 */     this(dstream, encoding, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public SimpleCharStream(InputStream dstream)
/*     */   {
/* 352 */     this(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 359 */     ReInit(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream, int startline, int startcolumn, int buffersize)
/*     */   {
/* 366 */     ReInit(new InputStreamReader(dstream), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream, String encoding)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 372 */     ReInit(dstream, encoding, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream)
/*     */   {
/* 378 */     ReInit(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 384 */     ReInit(dstream, encoding, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream, int startline, int startcolumn)
/*     */   {
/* 390 */     ReInit(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public static String GetImage()
/*     */   {
/* 395 */     if (bufpos >= tokenBegin) {
/* 396 */       return new String(buffer, tokenBegin, bufpos - tokenBegin + 1);
/*     */     }
/* 398 */     return new String(buffer, tokenBegin, bufsize - tokenBegin) + new String(buffer, 0, bufpos + 1);
/*     */   }
/*     */ 
/*     */   public static char[] GetSuffix(int len)
/*     */   {
/* 405 */     char[] ret = new char[len];
/*     */ 
/* 407 */     if (bufpos + 1 >= len) {
/* 408 */       System.arraycopy(buffer, bufpos - len + 1, ret, 0, len);
/*     */     }
/*     */     else {
/* 411 */       System.arraycopy(buffer, bufsize - (len - bufpos - 1), ret, 0, len - bufpos - 1);
/*     */ 
/* 413 */       System.arraycopy(buffer, 0, ret, len - bufpos - 1, bufpos + 1);
/*     */     }
/*     */ 
/* 416 */     return ret;
/*     */   }
/*     */ 
/*     */   public static void Done()
/*     */   {
/* 422 */     buffer = null;
/* 423 */     bufline = null;
/* 424 */     bufcolumn = null;
/*     */   }
/*     */ 
/*     */   public static void adjustBeginLineColumn(int newLine, int newCol)
/*     */   {
/* 432 */     int start = tokenBegin;
/*     */     int len;
/*     */     int len;
/* 435 */     if (bufpos >= tokenBegin)
/*     */     {
/* 437 */       len = bufpos - tokenBegin + inBuf + 1;
/*     */     }
/*     */     else
/*     */     {
/* 441 */       len = bufsize - tokenBegin + bufpos + 1 + inBuf;
/*     */     }
/*     */ 
/* 444 */     int i = 0; int j = 0; int k = 0;
/* 445 */     int nextColDiff = 0; int columnDiff = 0;
/*     */ 
/* 447 */     while (i < len) { start++; if (bufline[(j = start % bufsize)] != bufline[(k = start % bufsize)])
/*     */         break;
/* 449 */       bufline[j] = newLine;
/* 450 */       nextColDiff = columnDiff + bufcolumn[k] - bufcolumn[j];
/* 451 */       bufcolumn[j] = (newCol + columnDiff);
/* 452 */       columnDiff = nextColDiff;
/* 453 */       i++;
/*     */     }
/*     */ 
/* 456 */     if (i < len)
/*     */     {
/* 458 */       bufline[j] = (newLine++);
/* 459 */       bufcolumn[j] = (newCol + columnDiff);
/*     */ 
/* 461 */       while (i++ < len)
/*     */       {
/* 463 */         start++; if (bufline[(j = start % bufsize)] != bufline[(start % bufsize)]) {
/* 464 */           bufline[j] = (newLine++); continue;
/*     */         }
/* 466 */         bufline[j] = newLine;
/*     */       }
/*     */     }
/*     */ 
/* 470 */     line = bufline[j];
/* 471 */     column = bufcolumn[j];
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.SimpleCharStream
 * JD-Core Version:    0.6.0
 */