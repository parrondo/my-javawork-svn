/*     */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class JavaCharStream
/*     */ {
/*     */   public static final boolean staticFlag = true;
/*  64 */   public static int bufpos = -1;
/*     */   static int bufsize;
/*     */   static int available;
/*     */   static int tokenBegin;
/*     */   protected static int[] bufline;
/*     */   protected static int[] bufcolumn;
/*  71 */   protected static int column = 0;
/*  72 */   protected static int line = 1;
/*     */ 
/*  74 */   protected static boolean prevCharIsCR = false;
/*  75 */   protected static boolean prevCharIsLF = false;
/*     */   protected static Reader inputStream;
/*     */   protected static char[] nextCharBuf;
/*     */   protected static char[] buffer;
/*  81 */   protected static int maxNextCharInd = 0;
/*  82 */   protected static int nextCharInd = -1;
/*  83 */   protected static int inBuf = 0;
/*  84 */   protected static int tabSize = 8;
/*     */ 
/*     */   static final int hexval(char c)
/*     */     throws IOException
/*     */   {
/*  17 */     switch (c)
/*     */     {
/*     */     case '0':
/*  20 */       return 0;
/*     */     case '1':
/*  22 */       return 1;
/*     */     case '2':
/*  24 */       return 2;
/*     */     case '3':
/*  26 */       return 3;
/*     */     case '4':
/*  28 */       return 4;
/*     */     case '5':
/*  30 */       return 5;
/*     */     case '6':
/*  32 */       return 6;
/*     */     case '7':
/*  34 */       return 7;
/*     */     case '8':
/*  36 */       return 8;
/*     */     case '9':
/*  38 */       return 9;
/*     */     case 'A':
/*     */     case 'a':
/*  42 */       return 10;
/*     */     case 'B':
/*     */     case 'b':
/*  45 */       return 11;
/*     */     case 'C':
/*     */     case 'c':
/*  48 */       return 12;
/*     */     case 'D':
/*     */     case 'd':
/*  51 */       return 13;
/*     */     case 'E':
/*     */     case 'e':
/*  54 */       return 14;
/*     */     case 'F':
/*     */     case 'f':
/*  57 */       return 15;
/*     */     case ':':
/*     */     case ';':
/*     */     case '<':
/*     */     case '=':
/*     */     case '>':
/*     */     case '?':
/*     */     case '@':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'I':
/*     */     case 'J':
/*     */     case 'K':
/*     */     case 'L':
/*     */     case 'M':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'S':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'V':
/*     */     case 'W':
/*     */     case 'X':
/*     */     case 'Y':
/*     */     case 'Z':
/*     */     case '[':
/*     */     case '\\':
/*     */     case ']':
/*     */     case '^':
/*     */     case '_':
/*  60 */     case '`': } throw new IOException();
/*     */   }
/*     */ 
/*     */   protected static void setTabSize(int i)
/*     */   {
/*  86 */     tabSize = i; } 
/*  87 */   protected static int getTabSize(int i) { return tabSize; }
/*     */ 
/*     */   protected static void ExpandBuff(boolean wrapAround)
/*     */   {
/*  91 */     char[] newbuffer = new char[bufsize + 2048];
/*  92 */     int[] newbufline = new int[bufsize + 2048];
/*  93 */     int[] newbufcolumn = new int[bufsize + 2048];
/*     */     try
/*     */     {
/*  97 */       if (wrapAround)
/*     */       {
/*  99 */         System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
/* 100 */         System.arraycopy(buffer, 0, newbuffer, bufsize - tokenBegin, bufpos);
/* 101 */         buffer = newbuffer;
/*     */ 
/* 103 */         System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
/* 104 */         System.arraycopy(bufline, 0, newbufline, bufsize - tokenBegin, bufpos);
/* 105 */         bufline = newbufline;
/*     */ 
/* 107 */         System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
/* 108 */         System.arraycopy(bufcolumn, 0, newbufcolumn, bufsize - tokenBegin, bufpos);
/* 109 */         bufcolumn = newbufcolumn;
/*     */ 
/* 111 */         bufpos += bufsize - tokenBegin;
/*     */       }
/*     */       else
/*     */       {
/* 115 */         System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
/* 116 */         buffer = newbuffer;
/*     */ 
/* 118 */         System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
/* 119 */         bufline = newbufline;
/*     */ 
/* 121 */         System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
/* 122 */         bufcolumn = newbufcolumn;
/*     */ 
/* 124 */         bufpos -= tokenBegin;
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 129 */       throw new Error(t.getMessage());
/*     */     }
/*     */ 
/* 132 */     available = JavaCharStream.bufsize = bufsize + 2048;
/* 133 */     tokenBegin = 0;
/*     */   }
/*     */ 
/*     */   protected static void FillBuff()
/*     */     throws IOException
/*     */   {
/* 139 */     if (maxNextCharInd == 4096)
/* 140 */       maxNextCharInd = JavaCharStream.nextCharInd = 0;
/*     */     try
/*     */     {
/*     */       int i;
/* 143 */       if ((i = inputStream.read(nextCharBuf, maxNextCharInd, 4096 - maxNextCharInd)) == -1)
/*     */       {
/* 146 */         inputStream.close();
/* 147 */         throw new IOException();
/*     */       }
/*     */ 
/* 150 */       maxNextCharInd += i;
/* 151 */       return;
/*     */     }
/*     */     catch (IOException e) {
/* 154 */       if (bufpos != 0)
/*     */       {
/* 156 */         bufpos -= 1;
/* 157 */         backup(0);
/*     */       }
/*     */       else
/*     */       {
/* 161 */         bufline[bufpos] = line;
/* 162 */         bufcolumn[bufpos] = column;
/*     */       }
/*     */     }
/* 164 */     throw e;
/*     */   }
/*     */ 
/*     */   protected static char ReadByte()
/*     */     throws IOException
/*     */   {
/* 170 */     if (++nextCharInd >= maxNextCharInd) {
/* 171 */       FillBuff();
/*     */     }
/* 173 */     return nextCharBuf[nextCharInd];
/*     */   }
/*     */ 
/*     */   public static char BeginToken()
/*     */     throws IOException
/*     */   {
/* 179 */     if (inBuf > 0)
/*     */     {
/* 181 */       inBuf -= 1;
/*     */ 
/* 183 */       if (++bufpos == bufsize) {
/* 184 */         bufpos = 0;
/*     */       }
/* 186 */       tokenBegin = bufpos;
/* 187 */       return buffer[bufpos];
/*     */     }
/*     */ 
/* 190 */     tokenBegin = 0;
/* 191 */     bufpos = -1;
/*     */ 
/* 193 */     return readChar();
/*     */   }
/*     */ 
/*     */   protected static void AdjustBuffSize()
/*     */   {
/* 198 */     if (available == bufsize)
/*     */     {
/* 200 */       if (tokenBegin > 2048)
/*     */       {
/* 202 */         bufpos = 0;
/* 203 */         available = tokenBegin;
/*     */       }
/*     */       else {
/* 206 */         ExpandBuff(false);
/*     */       }
/* 208 */     } else if (available > tokenBegin)
/* 209 */       available = bufsize;
/* 210 */     else if (tokenBegin - available < 2048)
/* 211 */       ExpandBuff(true);
/*     */     else
/* 213 */       available = tokenBegin;
/*     */   }
/*     */ 
/*     */   protected static void UpdateLineColumn(char c)
/*     */   {
/* 218 */     column += 1;
/*     */ 
/* 220 */     if (prevCharIsLF)
/*     */     {
/* 222 */       prevCharIsLF = false;
/* 223 */       line += (JavaCharStream.column = 1);
/*     */     }
/* 225 */     else if (prevCharIsCR)
/*     */     {
/* 227 */       prevCharIsCR = false;
/* 228 */       if (c == '\n')
/*     */       {
/* 230 */         prevCharIsLF = true;
/*     */       }
/*     */       else {
/* 233 */         line += (JavaCharStream.column = 1);
/*     */       }
/*     */     }
/* 236 */     switch (c)
/*     */     {
/*     */     case '\r':
/* 239 */       prevCharIsCR = true;
/* 240 */       break;
/*     */     case '\n':
/* 242 */       prevCharIsLF = true;
/* 243 */       break;
/*     */     case '\t':
/* 245 */       column -= 1;
/* 246 */       column += tabSize - column % tabSize;
/* 247 */       break;
/*     */     case '\013':
/*     */     case '\f':
/*     */     }
/*     */ 
/* 252 */     bufline[bufpos] = line;
/* 253 */     bufcolumn[bufpos] = column;
/*     */   }
/*     */ 
/*     */   public static char readChar()
/*     */     throws IOException
/*     */   {
/* 259 */     if (inBuf > 0)
/*     */     {
/* 261 */       inBuf -= 1;
/*     */ 
/* 263 */       if (++bufpos == bufsize) {
/* 264 */         bufpos = 0;
/*     */       }
/* 266 */       return buffer[bufpos];
/*     */     }
/*     */ 
/* 271 */     if (++bufpos == available)
/* 272 */       AdjustBuffSize();
/*     */     char c;
/* 274 */     if ((buffer[bufpos] = c = ReadByte()) == '\\')
/*     */     {
/* 276 */       UpdateLineColumn(c);
/*     */ 
/* 278 */       int backSlashCnt = 1;
/*     */       while (true)
/*     */       {
/* 282 */         if (++bufpos == available) {
/* 283 */           AdjustBuffSize();
/*     */         }
/*     */         try
/*     */         {
/* 287 */           if ((buffer[bufpos] = c = ReadByte()) != '\\')
/*     */           {
/* 289 */             UpdateLineColumn(c);
/*     */ 
/* 291 */             if ((c == 'u') && ((backSlashCnt & 0x1) == 1))
/*     */             {
/* 293 */               if (--bufpos < 0) {
/* 294 */                 bufpos = bufsize - 1;
/*     */               }
/* 296 */               break;
/*     */             }
/*     */ 
/* 299 */             backup(backSlashCnt);
/* 300 */             return '\\';
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 306 */           if (backSlashCnt > 1) {
/* 307 */             backup(backSlashCnt - 1);
/*     */           }
/* 309 */           return '\\';
/*     */         }
/*     */ 
/* 312 */         UpdateLineColumn(c);
/* 313 */         backSlashCnt++;
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 319 */         while ((c = ReadByte()) == 'u')
/* 320 */           column += 1;
/*     */         char tmp255_254 = (char)(hexval(c) << 12 | hexval(ReadByte()) << 8 | hexval(ReadByte()) << 4 | hexval(ReadByte())); c = tmp255_254; buffer[bufpos] = tmp255_254;
/*     */ 
/* 327 */         column += 4;
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 331 */         throw new Error("Invalid escape character at line " + line + " column " + column + ".");
/*     */       }
/*     */ 
/* 335 */       if (backSlashCnt == 1) {
/* 336 */         return c;
/*     */       }
/*     */ 
/* 339 */       backup(backSlashCnt - 1);
/* 340 */       return '\\';
/*     */     }
/*     */ 
/* 345 */     UpdateLineColumn(c);
/* 346 */     return c;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static int getColumn()
/*     */   {
/* 356 */     return bufcolumn[bufpos];
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static int getLine()
/*     */   {
/* 365 */     return bufline[bufpos];
/*     */   }
/*     */ 
/*     */   public static int getEndColumn()
/*     */   {
/* 370 */     return bufcolumn[bufpos];
/*     */   }
/*     */ 
/*     */   public static int getEndLine()
/*     */   {
/* 375 */     return bufline[bufpos];
/*     */   }
/*     */ 
/*     */   public static int getBeginColumn()
/*     */   {
/* 380 */     return bufcolumn[tokenBegin];
/*     */   }
/*     */ 
/*     */   public static int getBeginLine()
/*     */   {
/* 385 */     return bufline[tokenBegin];
/*     */   }
/*     */ 
/*     */   public static void backup(int amount)
/*     */   {
/* 391 */     inBuf += amount;
/* 392 */     if ((JavaCharStream.bufpos = bufpos - amount) < 0)
/* 393 */       bufpos += bufsize;
/*     */   }
/*     */ 
/*     */   public JavaCharStream(Reader dstream, int startline, int startcolumn, int buffersize)
/*     */   {
/* 400 */     if (inputStream != null) {
/* 401 */       throw new Error("\n   ERROR: Second call to the constructor of a static JavaCharStream.\n       You must either use ReInit() or set the JavaCC option STATIC to false\n       during the generation of this class.");
/*     */     }
/*     */ 
/* 404 */     inputStream = dstream;
/* 405 */     line = startline;
/* 406 */     column = startcolumn - 1;
/*     */ 
/* 408 */     available = JavaCharStream.bufsize = buffersize;
/* 409 */     buffer = new char[buffersize];
/* 410 */     bufline = new int[buffersize];
/* 411 */     bufcolumn = new int[buffersize];
/* 412 */     nextCharBuf = new char[4096];
/*     */   }
/*     */ 
/*     */   public JavaCharStream(Reader dstream, int startline, int startcolumn)
/*     */   {
/* 419 */     this(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public JavaCharStream(Reader dstream)
/*     */   {
/* 425 */     this(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(Reader dstream, int startline, int startcolumn, int buffersize)
/*     */   {
/* 431 */     inputStream = dstream;
/* 432 */     line = startline;
/* 433 */     column = startcolumn - 1;
/*     */ 
/* 435 */     if ((buffer == null) || (buffersize != buffer.length))
/*     */     {
/* 437 */       available = JavaCharStream.bufsize = buffersize;
/* 438 */       buffer = new char[buffersize];
/* 439 */       bufline = new int[buffersize];
/* 440 */       bufcolumn = new int[buffersize];
/* 441 */       nextCharBuf = new char[4096];
/*     */     }
/* 443 */     prevCharIsLF = JavaCharStream.prevCharIsCR = 0;
/* 444 */     tokenBegin = JavaCharStream.inBuf = JavaCharStream.maxNextCharInd = 0;
/* 445 */     nextCharInd = JavaCharStream.bufpos = -1;
/*     */   }
/*     */ 
/*     */   public void ReInit(Reader dstream, int startline, int startcolumn)
/*     */   {
/* 452 */     ReInit(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(Reader dstream)
/*     */   {
/* 458 */     ReInit(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public JavaCharStream(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 464 */     this(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */   public JavaCharStream(InputStream dstream, int startline, int startcolumn, int buffersize)
/*     */   {
/* 471 */     this(new InputStreamReader(dstream), startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public JavaCharStream(InputStream dstream, String encoding, int startline, int startcolumn)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 478 */     this(dstream, encoding, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public JavaCharStream(InputStream dstream, int startline, int startcolumn)
/*     */   {
/* 485 */     this(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public JavaCharStream(InputStream dstream, String encoding)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 491 */     this(dstream, encoding, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public JavaCharStream(InputStream dstream)
/*     */   {
/* 497 */     this(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 504 */     ReInit(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream, int startline, int startcolumn, int buffersize)
/*     */   {
/* 511 */     ReInit(new InputStreamReader(dstream), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 517 */     ReInit(dstream, encoding, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream, int startline, int startcolumn)
/*     */   {
/* 523 */     ReInit(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream, String encoding) throws UnsupportedEncodingException
/*     */   {
/* 528 */     ReInit(dstream, encoding, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public void ReInit(InputStream dstream)
/*     */   {
/* 534 */     ReInit(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */   public static String GetImage()
/*     */   {
/* 540 */     if (bufpos >= tokenBegin) {
/* 541 */       return new String(buffer, tokenBegin, bufpos - tokenBegin + 1);
/*     */     }
/* 543 */     return new String(buffer, tokenBegin, bufsize - tokenBegin) + new String(buffer, 0, bufpos + 1);
/*     */   }
/*     */ 
/*     */   public static char[] GetSuffix(int len)
/*     */   {
/* 550 */     char[] ret = new char[len];
/*     */ 
/* 552 */     if (bufpos + 1 >= len) {
/* 553 */       System.arraycopy(buffer, bufpos - len + 1, ret, 0, len);
/*     */     }
/*     */     else {
/* 556 */       System.arraycopy(buffer, bufsize - (len - bufpos - 1), ret, 0, len - bufpos - 1);
/*     */ 
/* 558 */       System.arraycopy(buffer, 0, ret, len - bufpos - 1, bufpos + 1);
/*     */     }
/*     */ 
/* 561 */     return ret;
/*     */   }
/*     */ 
/*     */   public static void Done()
/*     */   {
/* 567 */     nextCharBuf = null;
/* 568 */     buffer = null;
/* 569 */     bufline = null;
/* 570 */     bufcolumn = null;
/*     */   }
/*     */ 
/*     */   public static void adjustBeginLineColumn(int newLine, int newCol)
/*     */   {
/* 578 */     int start = tokenBegin;
/*     */     int len;
/*     */     int len;
/* 581 */     if (bufpos >= tokenBegin)
/*     */     {
/* 583 */       len = bufpos - tokenBegin + inBuf + 1;
/*     */     }
/*     */     else
/*     */     {
/* 587 */       len = bufsize - tokenBegin + bufpos + 1 + inBuf;
/*     */     }
/*     */ 
/* 590 */     int i = 0; int j = 0; int k = 0;
/* 591 */     int nextColDiff = 0; int columnDiff = 0;
/*     */ 
/* 593 */     while (i < len) { start++; if (bufline[(j = start % bufsize)] != bufline[(k = start % bufsize)])
/*     */         break;
/* 595 */       bufline[j] = newLine;
/* 596 */       nextColDiff = columnDiff + bufcolumn[k] - bufcolumn[j];
/* 597 */       bufcolumn[j] = (newCol + columnDiff);
/* 598 */       columnDiff = nextColDiff;
/* 599 */       i++;
/*     */     }
/*     */ 
/* 602 */     if (i < len)
/*     */     {
/* 604 */       bufline[j] = (newLine++);
/* 605 */       bufcolumn[j] = (newCol + columnDiff);
/*     */ 
/* 607 */       while (i++ < len)
/*     */       {
/* 609 */         start++; if (bufline[(j = start % bufsize)] != bufline[(start % bufsize)]) {
/* 610 */           bufline[j] = (newLine++); continue;
/*     */         }
/* 612 */         bufline[j] = newLine;
/*     */       }
/*     */     }
/*     */ 
/* 616 */     line = bufline[j];
/* 617 */     column = bufcolumn[j];
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.JavaCharStream
 * JD-Core Version:    0.6.0
 */