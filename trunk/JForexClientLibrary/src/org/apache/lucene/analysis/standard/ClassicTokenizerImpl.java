/*     */ package org.apache.lucene.analysis.standard;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ 
/*     */ class ClassicTokenizerImpl
/*     */   implements StandardTokenizerInterface
/*     */ {
/*     */   public static final int YYEOF = -1;
/*     */   private static final int ZZ_BUFFERSIZE = 16384;
/*     */   public static final int YYINITIAL = 0;
/*  56 */   private static final int[] ZZ_LEXSTATE = { 0, 0 };
/*     */   private static final String ZZ_CMAP_PACKED = "";
/* 126 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*     */ 
/* 131 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*     */   private static final String ZZ_ACTION_PACKED_0 = "";
/* 162 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/* 194 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*     */   private static final int ZZ_NO_MATCH = 1;
/*     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/* 265 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*     */ 
/* 274 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*     */   private Reader zzReader;
/*     */   private int zzState;
/* 306 */   private int zzLexicalState = 0;
/*     */ 
/* 310 */   private char[] zzBuffer = new char[16384];
/*     */   private int zzMarkedPos;
/*     */   private int zzCurrentPos;
/*     */   private int zzStartRead;
/*     */   private int zzEndRead;
/*     */   private int yyline;
/*     */   private int yychar;
/*     */   private int yycolumn;
/* 340 */   private boolean zzAtBOL = true;
/*     */   private boolean zzAtEOF;
/*     */   private boolean zzEOFDone;
/*     */   public static final int ALPHANUM = 0;
/*     */   public static final int APOSTROPHE = 1;
/*     */   public static final int ACRONYM = 2;
/*     */   public static final int COMPANY = 3;
/*     */   public static final int EMAIL = 4;
/*     */   public static final int HOST = 5;
/*     */   public static final int NUM = 6;
/*     */   public static final int CJ = 7;
/*     */   public static final int ACRONYM_DEP = 8;
/* 360 */   public static final String[] TOKEN_TYPES = StandardTokenizer.TOKEN_TYPES;
/*     */ 
/*     */   private static int[] zzUnpackAction()
/*     */   {
/* 140 */     int[] result = new int[51];
/* 141 */     int offset = 0;
/* 142 */     offset = zzUnpackAction("", offset, result);
/* 143 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackAction(String packed, int offset, int[] result) {
/* 147 */     int i = 0;
/* 148 */     int j = offset;
/* 149 */     int l = packed.length();
/* 150 */     while (i < l) {
/* 151 */       int count = packed.charAt(i++);
/* 152 */       int value = packed.charAt(i++);
/*     */       do { result[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 155 */     return j;
/*     */   }
/*     */ 
/*     */   private static int[] zzUnpackRowMap()
/*     */   {
/* 174 */     int[] result = new int[51];
/* 175 */     int offset = 0;
/* 176 */     offset = zzUnpackRowMap("", offset, result);
/* 177 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackRowMap(String packed, int offset, int[] result) {
/* 181 */     int i = 0;
/* 182 */     int j = offset;
/* 183 */     int l = packed.length();
/* 184 */     while (i < l) {
/* 185 */       int high = packed.charAt(i++) << '\020';
/* 186 */       result[(j++)] = (high | packed.charAt(i++));
/*     */     }
/* 188 */     return j;
/*     */   }
/*     */ 
/*     */   private static int[] zzUnpackTrans()
/*     */   {
/* 239 */     int[] result = new int[658];
/* 240 */     int offset = 0;
/* 241 */     offset = zzUnpackTrans("", offset, result);
/* 242 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackTrans(String packed, int offset, int[] result) {
/* 246 */     int i = 0;
/* 247 */     int j = offset;
/* 248 */     int l = packed.length();
/* 249 */     while (i < l) {
/* 250 */       int count = packed.charAt(i++);
/* 251 */       int value = packed.charAt(i++);
/* 252 */       value--;
/*     */       do { result[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 255 */     return j;
/*     */   }
/*     */ 
/*     */   private static int[] zzUnpackAttribute()
/*     */   {
/* 281 */     int[] result = new int[51];
/* 282 */     int offset = 0;
/* 283 */     offset = zzUnpackAttribute("", offset, result);
/* 284 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackAttribute(String packed, int offset, int[] result) {
/* 288 */     int i = 0;
/* 289 */     int j = offset;
/* 290 */     int l = packed.length();
/* 291 */     while (i < l) {
/* 292 */       int count = packed.charAt(i++);
/* 293 */       int value = packed.charAt(i++);
/*     */       do { result[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 296 */     return j;
/*     */   }
/*     */ 
/*     */   public final int yychar()
/*     */   {
/* 364 */     return this.yychar;
/*     */   }
/*     */ 
/*     */   public final void getText(CharTermAttribute t)
/*     */   {
/* 371 */     t.copyBuffer(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*     */   }
/*     */ 
/*     */   ClassicTokenizerImpl(Reader in)
/*     */   {
/* 383 */     this.zzReader = in;
/*     */   }
/*     */ 
/*     */   ClassicTokenizerImpl(InputStream in)
/*     */   {
/* 393 */     this(new InputStreamReader(in));
/*     */   }
/*     */ 
/*     */   private static char[] zzUnpackCMap(String packed)
/*     */   {
/* 403 */     char[] map = new char[65536];
/* 404 */     int i = 0;
/* 405 */     int j = 0;
/* 406 */     while (i < 1154) {
/* 407 */       int count = packed.charAt(i++);
/* 408 */       char value = packed.charAt(i++);
/*     */       do { map[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 411 */     return map;
/*     */   }
/*     */ 
/*     */   private boolean zzRefill()
/*     */     throws IOException
/*     */   {
/* 425 */     if (this.zzStartRead > 0) {
/* 426 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*     */ 
/* 431 */       this.zzEndRead -= this.zzStartRead;
/* 432 */       this.zzCurrentPos -= this.zzStartRead;
/* 433 */       this.zzMarkedPos -= this.zzStartRead;
/* 434 */       this.zzStartRead = 0;
/*     */     }
/*     */ 
/* 438 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*     */     {
/* 440 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 441 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 442 */       this.zzBuffer = newBuffer;
/*     */     }
/*     */ 
/* 446 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*     */ 
/* 449 */     if (numRead > 0) {
/* 450 */       this.zzEndRead += numRead;
/* 451 */       return false;
/*     */     }
/*     */ 
/* 454 */     if (numRead == 0) {
/* 455 */       int c = this.zzReader.read();
/* 456 */       if (c == -1) {
/* 457 */         return true;
/*     */       }
/* 459 */       this.zzBuffer[(this.zzEndRead++)] = (char)c;
/* 460 */       return false;
/*     */     }
/*     */ 
/* 465 */     return true;
/*     */   }
/*     */ 
/*     */   public final void yyclose()
/*     */     throws IOException
/*     */   {
/* 473 */     this.zzAtEOF = true;
/* 474 */     this.zzEndRead = this.zzStartRead;
/*     */ 
/* 476 */     if (this.zzReader != null)
/* 477 */       this.zzReader.close();
/*     */   }
/*     */ 
/*     */   public final void yyreset(Reader reader)
/*     */   {
/* 494 */     this.zzReader = reader;
/* 495 */     this.zzAtBOL = true;
/* 496 */     this.zzAtEOF = false;
/* 497 */     this.zzEOFDone = false;
/* 498 */     this.zzEndRead = (this.zzStartRead = 0);
/* 499 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 500 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 501 */     this.zzLexicalState = 0;
/* 502 */     if (this.zzBuffer.length > 16384)
/* 503 */       this.zzBuffer = new char[16384];
/*     */   }
/*     */ 
/*     */   public final int yystate()
/*     */   {
/* 511 */     return this.zzLexicalState;
/*     */   }
/*     */ 
/*     */   public final void yybegin(int newState)
/*     */   {
/* 521 */     this.zzLexicalState = newState;
/*     */   }
/*     */ 
/*     */   public final String yytext()
/*     */   {
/* 529 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*     */   }
/*     */ 
/*     */   public final char yycharat(int pos)
/*     */   {
/* 545 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*     */   }
/*     */ 
/*     */   public final int yylength()
/*     */   {
/* 553 */     return this.zzMarkedPos - this.zzStartRead;
/*     */   }
/*     */ 
/*     */   private void zzScanError(int errorCode)
/*     */   {
/*     */     String message;
/*     */     try
/*     */     {
/* 574 */       message = ZZ_ERROR_MSG[errorCode];
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException e) {
/* 577 */       message = ZZ_ERROR_MSG[0];
/*     */     }
/*     */ 
/* 580 */     throw new Error(message);
/*     */   }
/*     */ 
/*     */   public void yypushback(int number)
/*     */   {
/* 593 */     if (number > yylength()) {
/* 594 */       zzScanError(2);
/*     */     }
/* 596 */     this.zzMarkedPos -= number;
/*     */   }
/*     */ 
/*     */   public int getNextToken()
/*     */     throws IOException
/*     */   {
/* 614 */     int zzEndReadL = this.zzEndRead;
/* 615 */     char[] zzBufferL = this.zzBuffer;
/* 616 */     char[] zzCMapL = ZZ_CMAP;
/*     */ 
/* 618 */     int[] zzTransL = ZZ_TRANS;
/* 619 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 620 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*     */     while (true)
/*     */     {
/* 623 */       int zzMarkedPosL = this.zzMarkedPos;
/*     */ 
/* 625 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*     */ 
/* 627 */       int zzAction = -1;
/*     */ 
/* 629 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*     */ 
/* 631 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*     */ 
/* 634 */       int zzAttributes = zzAttrL[this.zzState];
/* 635 */       if ((zzAttributes & 0x1) == 1)
/* 636 */         zzAction = this.zzState;
/*     */       int zzInput;
/*     */       while (true)
/*     */       {
/*     */         int zzInput;
/* 643 */         if (zzCurrentPosL < zzEndReadL) {
/* 644 */           zzInput = zzBufferL[(zzCurrentPosL++)]; } else {
/* 645 */           if (this.zzAtEOF) {
/* 646 */             int zzInput = -1;
/* 647 */             break;
/*     */           }
/*     */ 
/* 651 */           this.zzCurrentPos = zzCurrentPosL;
/* 652 */           this.zzMarkedPos = zzMarkedPosL;
/* 653 */           boolean eof = zzRefill();
/*     */ 
/* 655 */           zzCurrentPosL = this.zzCurrentPos;
/* 656 */           zzMarkedPosL = this.zzMarkedPos;
/* 657 */           zzBufferL = this.zzBuffer;
/* 658 */           zzEndReadL = this.zzEndRead;
/* 659 */           if (eof) {
/* 660 */             int zzInput = -1;
/* 661 */             break;
/*     */           }
/*     */ 
/* 664 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*     */         }
/*     */ 
/* 667 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 668 */         if (zzNext == -1) break;
/* 669 */         this.zzState = zzNext;
/*     */ 
/* 671 */         zzAttributes = zzAttrL[this.zzState];
/* 672 */         if ((zzAttributes & 0x1) == 1) {
/* 673 */           zzAction = this.zzState;
/* 674 */           zzMarkedPosL = zzCurrentPosL;
/* 675 */           if ((zzAttributes & 0x8) == 8)
/*     */           {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 682 */       this.zzMarkedPos = zzMarkedPosL;
/*     */ 
/* 684 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
/*     */       case 10:
/* 686 */         return 4;
/*     */       case 11:
/* 688 */         break;
/*     */       case 2:
/* 690 */         return 0;
/*     */       case 12:
/* 692 */         break;
/*     */       case 4:
/* 694 */         return 5;
/*     */       case 13:
/* 696 */         break;
/*     */       case 1:
/*     */       case 14:
/* 700 */         break;
/*     */       case 8:
/* 702 */         return 8;
/*     */       case 15:
/* 704 */         break;
/*     */       case 5:
/* 706 */         return 6;
/*     */       case 16:
/* 708 */         break;
/*     */       case 9:
/* 710 */         return 2;
/*     */       case 17:
/* 712 */         break;
/*     */       case 7:
/* 714 */         return 3;
/*     */       case 18:
/* 716 */         break;
/*     */       case 6:
/* 718 */         return 1;
/*     */       case 19:
/* 720 */         break;
/*     */       case 3:
/* 722 */         return 7;
/*     */       case 20:
/* 724 */         break;
/*     */       default:
/* 726 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos)) {
/* 727 */           this.zzAtEOF = true;
/* 728 */           return -1;
/*     */         }
/*     */ 
/* 731 */         zzScanError(1);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.ClassicTokenizerImpl
 * JD-Core Version:    0.6.0
 */