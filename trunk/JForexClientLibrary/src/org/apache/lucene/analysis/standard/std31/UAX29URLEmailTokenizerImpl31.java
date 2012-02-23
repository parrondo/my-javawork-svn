/*      */ package org.apache.lucene.analysis.standard.std31;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.Reader;
/*      */ import org.apache.lucene.analysis.standard.StandardTokenizerInterface;
/*      */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*      */ 
/*      */ @Deprecated
/*      */ public final class UAX29URLEmailTokenizerImpl31
/*      */   implements StandardTokenizerInterface
/*      */ {
/*      */   public static final int YYEOF = -1;
/*      */   private static final int ZZ_BUFFERSIZE = 16384;
/*      */   public static final int YYINITIAL = 0;
/*   57 */   private static final int[] ZZ_LEXSTATE = { 0, 0 };
/*      */   private static final String ZZ_CMAP_PACKED = "";
/*  210 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*      */ 
/*  215 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*      */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  259 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*      */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  451 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*      */   private static final String ZZ_TRANS_PACKED_0 = "";
/*      */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*      */   private static final int ZZ_NO_MATCH = 1;
/*      */   private static final int ZZ_PUSHBACK_2BIG = 2;
/* 3171 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*      */ 
/* 3180 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*      */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*      */   private Reader zzReader;
/*      */   private int zzState;
/* 3224 */   private int zzLexicalState = 0;
/*      */ 
/* 3228 */   private char[] zzBuffer = new char[16384];
/*      */   private int zzMarkedPos;
/*      */   private int zzCurrentPos;
/*      */   private int zzStartRead;
/*      */   private int zzEndRead;
/*      */   private int yyline;
/*      */   private int yychar;
/*      */   private int yycolumn;
/* 3258 */   private boolean zzAtBOL = true;
/*      */   private boolean zzAtEOF;
/*      */   private boolean zzEOFDone;
/*      */   public static final int WORD_TYPE = 0;
/*      */   public static final int NUMERIC_TYPE = 1;
/*      */   public static final int SOUTH_EAST_ASIAN_TYPE = 2;
/*      */   public static final int IDEOGRAPHIC_TYPE = 3;
/*      */   public static final int HIRAGANA_TYPE = 4;
/*      */   public static final int KATAKANA_TYPE = 5;
/*      */   public static final int HANGUL_TYPE = 6;
/*      */   public static final int EMAIL_TYPE = 8;
/*      */   public static final int URL_TYPE = 7;
/*      */ 
/*      */   private static int[] zzUnpackAction()
/*      */   {
/*  237 */     int[] result = new int[1331];
/*  238 */     int offset = 0;
/*  239 */     offset = zzUnpackAction("", offset, result);
/*  240 */     return result;
/*      */   }
/*      */ 
/*      */   private static int zzUnpackAction(String packed, int offset, int[] result) {
/*  244 */     int i = 0;
/*  245 */     int j = offset;
/*  246 */     int l = packed.length();
/*  247 */     while (i < l) {
/*  248 */       int count = packed.charAt(i++);
/*  249 */       int value = packed.charAt(i++);
/*      */       do { result[(j++)] = value; count--; } while (count > 0);
/*      */     }
/*  252 */     return j;
/*      */   }
/*      */ 
/*      */   private static int[] zzUnpackRowMap()
/*      */   {
/*  431 */     int[] result = new int[1331];
/*  432 */     int offset = 0;
/*  433 */     offset = zzUnpackRowMap("", offset, result);
/*  434 */     return result;
/*      */   }
/*      */ 
/*      */   private static int zzUnpackRowMap(String packed, int offset, int[] result) {
/*  438 */     int i = 0;
/*  439 */     int j = offset;
/*  440 */     int l = packed.length();
/*  441 */     while (i < l) {
/*  442 */       int high = packed.charAt(i++) << '\020';
/*  443 */       result[(j++)] = (high | packed.charAt(i++));
/*      */     }
/*  445 */     return j;
/*      */   }
/*      */ 
/*      */   private static int[] zzUnpackTrans()
/*      */   {
/* 3145 */     int[] result = new int[214182];
/* 3146 */     int offset = 0;
/* 3147 */     offset = zzUnpackTrans("", offset, result);
/* 3148 */     return result;
/*      */   }
/*      */ 
/*      */   private static int zzUnpackTrans(String packed, int offset, int[] result) {
/* 3152 */     int i = 0;
/* 3153 */     int j = offset;
/* 3154 */     int l = packed.length();
/* 3155 */     while (i < l) {
/* 3156 */       int count = packed.charAt(i++);
/* 3157 */       int value = packed.charAt(i++);
/* 3158 */       value--;
/*      */       do { result[(j++)] = value; count--; } while (count > 0);
/*      */     }
/* 3161 */     return j;
/*      */   }
/*      */ 
/*      */   private static int[] zzUnpackAttribute()
/*      */   {
/* 3199 */     int[] result = new int[1331];
/* 3200 */     int offset = 0;
/* 3201 */     offset = zzUnpackAttribute("", offset, result);
/* 3202 */     return result;
/*      */   }
/*      */ 
/*      */   private static int zzUnpackAttribute(String packed, int offset, int[] result) {
/* 3206 */     int i = 0;
/* 3207 */     int j = offset;
/* 3208 */     int l = packed.length();
/* 3209 */     while (i < l) {
/* 3210 */       int count = packed.charAt(i++);
/* 3211 */       int value = packed.charAt(i++);
/*      */       do { result[(j++)] = value; count--; } while (count > 0);
/*      */     }
/* 3214 */     return j;
/*      */   }
/*      */ 
/*      */   public final int yychar()
/*      */   {
/* 3297 */     return this.yychar;
/*      */   }
/*      */ 
/*      */   public final void getText(CharTermAttribute t)
/*      */   {
/* 3304 */     t.copyBuffer(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*      */   }
/*      */ 
/*      */   public UAX29URLEmailTokenizerImpl31(Reader in)
/*      */   {
/* 3315 */     this.zzReader = in;
/*      */   }
/*      */ 
/*      */   public UAX29URLEmailTokenizerImpl31(InputStream in)
/*      */   {
/* 3325 */     this(new InputStreamReader(in));
/*      */   }
/*      */ 
/*      */   private static char[] zzUnpackCMap(String packed)
/*      */   {
/* 3335 */     char[] map = new char[65536];
/* 3336 */     int i = 0;
/* 3337 */     int j = 0;
/* 3338 */     while (i < 2812) {
/* 3339 */       int count = packed.charAt(i++);
/* 3340 */       char value = packed.charAt(i++);
/*      */       do { map[(j++)] = value; count--; } while (count > 0);
/*      */     }
/* 3343 */     return map;
/*      */   }
/*      */ 
/*      */   private boolean zzRefill()
/*      */     throws IOException
/*      */   {
/* 3357 */     if (this.zzStartRead > 0) {
/* 3358 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*      */ 
/* 3363 */       this.zzEndRead -= this.zzStartRead;
/* 3364 */       this.zzCurrentPos -= this.zzStartRead;
/* 3365 */       this.zzMarkedPos -= this.zzStartRead;
/* 3366 */       this.zzStartRead = 0;
/*      */     }
/*      */ 
/* 3370 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*      */     {
/* 3372 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 3373 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 3374 */       this.zzBuffer = newBuffer;
/*      */     }
/*      */ 
/* 3378 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*      */ 
/* 3381 */     if (numRead > 0) {
/* 3382 */       this.zzEndRead += numRead;
/* 3383 */       return false;
/*      */     }
/*      */ 
/* 3386 */     if (numRead == 0) {
/* 3387 */       int c = this.zzReader.read();
/* 3388 */       if (c == -1) {
/* 3389 */         return true;
/*      */       }
/* 3391 */       this.zzBuffer[(this.zzEndRead++)] = (char)c;
/* 3392 */       return false;
/*      */     }
/*      */ 
/* 3397 */     return true;
/*      */   }
/*      */ 
/*      */   public final void yyclose()
/*      */     throws IOException
/*      */   {
/* 3405 */     this.zzAtEOF = true;
/* 3406 */     this.zzEndRead = this.zzStartRead;
/*      */ 
/* 3408 */     if (this.zzReader != null)
/* 3409 */       this.zzReader.close();
/*      */   }
/*      */ 
/*      */   public final void yyreset(Reader reader)
/*      */   {
/* 3426 */     this.zzReader = reader;
/* 3427 */     this.zzAtBOL = true;
/* 3428 */     this.zzAtEOF = false;
/* 3429 */     this.zzEOFDone = false;
/* 3430 */     this.zzEndRead = (this.zzStartRead = 0);
/* 3431 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 3432 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 3433 */     this.zzLexicalState = 0;
/* 3434 */     if (this.zzBuffer.length > 16384)
/* 3435 */       this.zzBuffer = new char[16384];
/*      */   }
/*      */ 
/*      */   public final int yystate()
/*      */   {
/* 3443 */     return this.zzLexicalState;
/*      */   }
/*      */ 
/*      */   public final void yybegin(int newState)
/*      */   {
/* 3453 */     this.zzLexicalState = newState;
/*      */   }
/*      */ 
/*      */   public final String yytext()
/*      */   {
/* 3461 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*      */   }
/*      */ 
/*      */   public final char yycharat(int pos)
/*      */   {
/* 3477 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*      */   }
/*      */ 
/*      */   public final int yylength()
/*      */   {
/* 3485 */     return this.zzMarkedPos - this.zzStartRead;
/*      */   }
/*      */ 
/*      */   private void zzScanError(int errorCode)
/*      */   {
/*      */     String message;
/*      */     try
/*      */     {
/* 3506 */       message = ZZ_ERROR_MSG[errorCode];
/*      */     }
/*      */     catch (ArrayIndexOutOfBoundsException e) {
/* 3509 */       message = ZZ_ERROR_MSG[0];
/*      */     }
/*      */ 
/* 3512 */     throw new Error(message);
/*      */   }
/*      */ 
/*      */   public void yypushback(int number)
/*      */   {
/* 3525 */     if (number > yylength()) {
/* 3526 */       zzScanError(2);
/*      */     }
/* 3528 */     this.zzMarkedPos -= number;
/*      */   }
/*      */ 
/*      */   public int getNextToken()
/*      */     throws IOException
/*      */   {
/* 3546 */     int zzEndReadL = this.zzEndRead;
/* 3547 */     char[] zzBufferL = this.zzBuffer;
/* 3548 */     char[] zzCMapL = ZZ_CMAP;
/*      */ 
/* 3550 */     int[] zzTransL = ZZ_TRANS;
/* 3551 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 3552 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*      */     while (true)
/*      */     {
/* 3555 */       int zzMarkedPosL = this.zzMarkedPos;
/*      */ 
/* 3557 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*      */ 
/* 3559 */       int zzAction = -1;
/*      */ 
/* 3561 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*      */ 
/* 3563 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*      */ 
/* 3566 */       int zzAttributes = zzAttrL[this.zzState];
/* 3567 */       if ((zzAttributes & 0x1) == 1)
/* 3568 */         zzAction = this.zzState;
/*      */       int zzInput;
/*      */       while (true)
/*      */       {
/*      */         int zzInput;
/* 3575 */         if (zzCurrentPosL < zzEndReadL) {
/* 3576 */           zzInput = zzBufferL[(zzCurrentPosL++)]; } else {
/* 3577 */           if (this.zzAtEOF) {
/* 3578 */             int zzInput = -1;
/* 3579 */             break;
/*      */           }
/*      */ 
/* 3583 */           this.zzCurrentPos = zzCurrentPosL;
/* 3584 */           this.zzMarkedPos = zzMarkedPosL;
/* 3585 */           boolean eof = zzRefill();
/*      */ 
/* 3587 */           zzCurrentPosL = this.zzCurrentPos;
/* 3588 */           zzMarkedPosL = this.zzMarkedPos;
/* 3589 */           zzBufferL = this.zzBuffer;
/* 3590 */           zzEndReadL = this.zzEndRead;
/* 3591 */           if (eof) {
/* 3592 */             int zzInput = -1;
/* 3593 */             break;
/*      */           }
/*      */ 
/* 3596 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*      */         }
/*      */ 
/* 3599 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 3600 */         if (zzNext == -1) break;
/* 3601 */         this.zzState = zzNext;
/*      */ 
/* 3603 */         zzAttributes = zzAttrL[this.zzState];
/* 3604 */         if ((zzAttributes & 0x1) == 1) {
/* 3605 */           zzAction = this.zzState;
/* 3606 */           zzMarkedPosL = zzCurrentPosL;
/* 3607 */           if ((zzAttributes & 0x8) == 8)
/*      */           {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 3614 */       this.zzMarkedPos = zzMarkedPosL;
/*      */ 
/* 3616 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
/*      */       case 2:
/* 3618 */         return 0;
/*      */       case 11:
/* 3620 */         break;
/*      */       case 5:
/* 3622 */         return 2;
/*      */       case 12:
/* 3624 */         break;
/*      */       case 10:
/* 3626 */         return 7;
/*      */       case 13:
/* 3628 */         break;
/*      */       case 9:
/* 3630 */         return 8;
/*      */       case 14:
/* 3632 */         break;
/*      */       case 4:
/* 3634 */         return 5;
/*      */       case 15:
/* 3636 */         break;
/*      */       case 6:
/* 3638 */         return 3;
/*      */       case 16:
/* 3640 */         break;
/*      */       case 1:
/*      */       case 17:
/* 3644 */         break;
/*      */       case 8:
/* 3646 */         return 6;
/*      */       case 18:
/* 3648 */         break;
/*      */       case 3:
/* 3650 */         return 1;
/*      */       case 19:
/* 3652 */         break;
/*      */       case 7:
/* 3654 */         return 4;
/*      */       case 20:
/* 3656 */         break;
/*      */       default:
/* 3658 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos)) {
/* 3659 */           this.zzAtEOF = true;
/*      */ 
/* 3661 */           return -1;
/*      */         }
/*      */ 
/* 3665 */         zzScanError(1);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.std31.UAX29URLEmailTokenizerImpl31
 * JD-Core Version:    0.6.0
 */