/*      */ package org.apache.lucene.analysis.standard;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.Reader;
/*      */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*      */ 
/*      */ public final class UAX29URLEmailTokenizerImpl
/*      */   implements StandardTokenizerInterface
/*      */ {
/*      */   public static final int YYEOF = -1;
/*      */   private static final int ZZ_BUFFERSIZE = 16384;
/*      */   public static final int YYINITIAL = 0;
/*   60 */   private static final int[] ZZ_LEXSTATE = { 0, 0 };
/*      */   private static final String ZZ_CMAP_PACKED = "";
/*  213 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*      */ 
/*  218 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*      */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  262 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*      */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  460 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*      */   private static final String ZZ_TRANS_PACKED_0 = "";
/*      */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*      */   private static final int ZZ_NO_MATCH = 1;
/*      */   private static final int ZZ_PUSHBACK_2BIG = 2;
/* 3261 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*      */ 
/* 3270 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*      */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*      */   private Reader zzReader;
/*      */   private int zzState;
/* 3314 */   private int zzLexicalState = 0;
/*      */ 
/* 3318 */   private char[] zzBuffer = new char[16384];
/*      */   private int zzMarkedPos;
/*      */   private int zzCurrentPos;
/*      */   private int zzStartRead;
/*      */   private int zzEndRead;
/*      */   private int yyline;
/*      */   private int yychar;
/*      */   private int yycolumn;
/* 3348 */   private boolean zzAtBOL = true;
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
/*  240 */     int[] result = new int[1380];
/*  241 */     int offset = 0;
/*  242 */     offset = zzUnpackAction("", offset, result);
/*  243 */     return result;
/*      */   }
/*      */ 
/*      */   private static int zzUnpackAction(String packed, int offset, int[] result) {
/*  247 */     int i = 0;
/*  248 */     int j = offset;
/*  249 */     int l = packed.length();
/*  250 */     while (i < l) {
/*  251 */       int count = packed.charAt(i++);
/*  252 */       int value = packed.charAt(i++);
/*      */       do { result[(j++)] = value; count--; } while (count > 0);
/*      */     }
/*  255 */     return j;
/*      */   }
/*      */ 
/*      */   private static int[] zzUnpackRowMap()
/*      */   {
/*  440 */     int[] result = new int[1380];
/*  441 */     int offset = 0;
/*  442 */     offset = zzUnpackRowMap("", offset, result);
/*  443 */     return result;
/*      */   }
/*      */ 
/*      */   private static int zzUnpackRowMap(String packed, int offset, int[] result) {
/*  447 */     int i = 0;
/*  448 */     int j = offset;
/*  449 */     int l = packed.length();
/*  450 */     while (i < l) {
/*  451 */       int high = packed.charAt(i++) << '\020';
/*  452 */       result[(j++)] = (high | packed.charAt(i++));
/*      */     }
/*  454 */     return j;
/*      */   }
/*      */ 
/*      */   private static int[] zzUnpackTrans()
/*      */   {
/* 3235 */     int[] result = new int[222495];
/* 3236 */     int offset = 0;
/* 3237 */     offset = zzUnpackTrans("", offset, result);
/* 3238 */     return result;
/*      */   }
/*      */ 
/*      */   private static int zzUnpackTrans(String packed, int offset, int[] result) {
/* 3242 */     int i = 0;
/* 3243 */     int j = offset;
/* 3244 */     int l = packed.length();
/* 3245 */     while (i < l) {
/* 3246 */       int count = packed.charAt(i++);
/* 3247 */       int value = packed.charAt(i++);
/* 3248 */       value--;
/*      */       do { result[(j++)] = value; count--; } while (count > 0);
/*      */     }
/* 3251 */     return j;
/*      */   }
/*      */ 
/*      */   private static int[] zzUnpackAttribute()
/*      */   {
/* 3289 */     int[] result = new int[1380];
/* 3290 */     int offset = 0;
/* 3291 */     offset = zzUnpackAttribute("", offset, result);
/* 3292 */     return result;
/*      */   }
/*      */ 
/*      */   private static int zzUnpackAttribute(String packed, int offset, int[] result) {
/* 3296 */     int i = 0;
/* 3297 */     int j = offset;
/* 3298 */     int l = packed.length();
/* 3299 */     while (i < l) {
/* 3300 */       int count = packed.charAt(i++);
/* 3301 */       int value = packed.charAt(i++);
/*      */       do { result[(j++)] = value; count--; } while (count > 0);
/*      */     }
/* 3304 */     return j;
/*      */   }
/*      */ 
/*      */   public final int yychar()
/*      */   {
/* 3387 */     return this.yychar;
/*      */   }
/*      */ 
/*      */   public final void getText(CharTermAttribute t)
/*      */   {
/* 3394 */     t.copyBuffer(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*      */   }
/*      */ 
/*      */   public UAX29URLEmailTokenizerImpl(Reader in)
/*      */   {
/* 3405 */     this.zzReader = in;
/*      */   }
/*      */ 
/*      */   public UAX29URLEmailTokenizerImpl(InputStream in)
/*      */   {
/* 3415 */     this(new InputStreamReader(in));
/*      */   }
/*      */ 
/*      */   private static char[] zzUnpackCMap(String packed)
/*      */   {
/* 3425 */     char[] map = new char[65536];
/* 3426 */     int i = 0;
/* 3427 */     int j = 0;
/* 3428 */     while (i < 2812) {
/* 3429 */       int count = packed.charAt(i++);
/* 3430 */       char value = packed.charAt(i++);
/*      */       do { map[(j++)] = value; count--; } while (count > 0);
/*      */     }
/* 3433 */     return map;
/*      */   }
/*      */ 
/*      */   private boolean zzRefill()
/*      */     throws IOException
/*      */   {
/* 3447 */     if (this.zzStartRead > 0) {
/* 3448 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*      */ 
/* 3453 */       this.zzEndRead -= this.zzStartRead;
/* 3454 */       this.zzCurrentPos -= this.zzStartRead;
/* 3455 */       this.zzMarkedPos -= this.zzStartRead;
/* 3456 */       this.zzStartRead = 0;
/*      */     }
/*      */ 
/* 3460 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*      */     {
/* 3462 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 3463 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 3464 */       this.zzBuffer = newBuffer;
/*      */     }
/*      */ 
/* 3468 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*      */ 
/* 3471 */     if (numRead > 0) {
/* 3472 */       this.zzEndRead += numRead;
/* 3473 */       return false;
/*      */     }
/*      */ 
/* 3476 */     if (numRead == 0) {
/* 3477 */       int c = this.zzReader.read();
/* 3478 */       if (c == -1) {
/* 3479 */         return true;
/*      */       }
/* 3481 */       this.zzBuffer[(this.zzEndRead++)] = (char)c;
/* 3482 */       return false;
/*      */     }
/*      */ 
/* 3487 */     return true;
/*      */   }
/*      */ 
/*      */   public final void yyclose()
/*      */     throws IOException
/*      */   {
/* 3495 */     this.zzAtEOF = true;
/* 3496 */     this.zzEndRead = this.zzStartRead;
/*      */ 
/* 3498 */     if (this.zzReader != null)
/* 3499 */       this.zzReader.close();
/*      */   }
/*      */ 
/*      */   public final void yyreset(Reader reader)
/*      */   {
/* 3516 */     this.zzReader = reader;
/* 3517 */     this.zzAtBOL = true;
/* 3518 */     this.zzAtEOF = false;
/* 3519 */     this.zzEOFDone = false;
/* 3520 */     this.zzEndRead = (this.zzStartRead = 0);
/* 3521 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 3522 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 3523 */     this.zzLexicalState = 0;
/* 3524 */     if (this.zzBuffer.length > 16384)
/* 3525 */       this.zzBuffer = new char[16384];
/*      */   }
/*      */ 
/*      */   public final int yystate()
/*      */   {
/* 3533 */     return this.zzLexicalState;
/*      */   }
/*      */ 
/*      */   public final void yybegin(int newState)
/*      */   {
/* 3543 */     this.zzLexicalState = newState;
/*      */   }
/*      */ 
/*      */   public final String yytext()
/*      */   {
/* 3551 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*      */   }
/*      */ 
/*      */   public final char yycharat(int pos)
/*      */   {
/* 3567 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*      */   }
/*      */ 
/*      */   public final int yylength()
/*      */   {
/* 3575 */     return this.zzMarkedPos - this.zzStartRead;
/*      */   }
/*      */ 
/*      */   private void zzScanError(int errorCode)
/*      */   {
/*      */     String message;
/*      */     try
/*      */     {
/* 3596 */       message = ZZ_ERROR_MSG[errorCode];
/*      */     }
/*      */     catch (ArrayIndexOutOfBoundsException e) {
/* 3599 */       message = ZZ_ERROR_MSG[0];
/*      */     }
/*      */ 
/* 3602 */     throw new Error(message);
/*      */   }
/*      */ 
/*      */   public void yypushback(int number)
/*      */   {
/* 3615 */     if (number > yylength()) {
/* 3616 */       zzScanError(2);
/*      */     }
/* 3618 */     this.zzMarkedPos -= number;
/*      */   }
/*      */ 
/*      */   public int getNextToken()
/*      */     throws IOException
/*      */   {
/* 3636 */     int zzEndReadL = this.zzEndRead;
/* 3637 */     char[] zzBufferL = this.zzBuffer;
/* 3638 */     char[] zzCMapL = ZZ_CMAP;
/*      */ 
/* 3640 */     int[] zzTransL = ZZ_TRANS;
/* 3641 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 3642 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*      */     while (true)
/*      */     {
/* 3645 */       int zzMarkedPosL = this.zzMarkedPos;
/*      */ 
/* 3647 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*      */ 
/* 3649 */       int zzAction = -1;
/*      */ 
/* 3651 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*      */ 
/* 3653 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*      */ 
/* 3656 */       int zzAttributes = zzAttrL[this.zzState];
/* 3657 */       if ((zzAttributes & 0x1) == 1)
/* 3658 */         zzAction = this.zzState;
/*      */       int zzInput;
/*      */       while (true)
/*      */       {
/*      */         int zzInput;
/* 3665 */         if (zzCurrentPosL < zzEndReadL) {
/* 3666 */           zzInput = zzBufferL[(zzCurrentPosL++)]; } else {
/* 3667 */           if (this.zzAtEOF) {
/* 3668 */             int zzInput = -1;
/* 3669 */             break;
/*      */           }
/*      */ 
/* 3673 */           this.zzCurrentPos = zzCurrentPosL;
/* 3674 */           this.zzMarkedPos = zzMarkedPosL;
/* 3675 */           boolean eof = zzRefill();
/*      */ 
/* 3677 */           zzCurrentPosL = this.zzCurrentPos;
/* 3678 */           zzMarkedPosL = this.zzMarkedPos;
/* 3679 */           zzBufferL = this.zzBuffer;
/* 3680 */           zzEndReadL = this.zzEndRead;
/* 3681 */           if (eof) {
/* 3682 */             int zzInput = -1;
/* 3683 */             break;
/*      */           }
/*      */ 
/* 3686 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*      */         }
/*      */ 
/* 3689 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 3690 */         if (zzNext == -1) break;
/* 3691 */         this.zzState = zzNext;
/*      */ 
/* 3693 */         zzAttributes = zzAttrL[this.zzState];
/* 3694 */         if ((zzAttributes & 0x1) == 1) {
/* 3695 */           zzAction = this.zzState;
/* 3696 */           zzMarkedPosL = zzCurrentPosL;
/* 3697 */           if ((zzAttributes & 0x8) == 8)
/*      */           {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 3704 */       this.zzMarkedPos = zzMarkedPosL;
/*      */ 
/* 3706 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
/*      */       case 2:
/* 3708 */         return 0;
/*      */       case 11:
/* 3710 */         break;
/*      */       case 5:
/* 3712 */         return 2;
/*      */       case 12:
/* 3714 */         break;
/*      */       case 10:
/* 3716 */         return 7;
/*      */       case 13:
/* 3718 */         break;
/*      */       case 9:
/* 3720 */         return 8;
/*      */       case 14:
/* 3722 */         break;
/*      */       case 4:
/* 3724 */         return 5;
/*      */       case 15:
/* 3726 */         break;
/*      */       case 6:
/* 3728 */         return 3;
/*      */       case 16:
/* 3730 */         break;
/*      */       case 1:
/*      */       case 17:
/* 3734 */         break;
/*      */       case 8:
/* 3736 */         return 6;
/*      */       case 18:
/* 3738 */         break;
/*      */       case 3:
/* 3740 */         return 1;
/*      */       case 19:
/* 3742 */         break;
/*      */       case 7:
/* 3744 */         return 4;
/*      */       case 20:
/* 3746 */         break;
/*      */       default:
/* 3748 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos)) {
/* 3749 */           this.zzAtEOF = true;
/*      */ 
/* 3751 */           return -1;
/*      */         }
/*      */ 
/* 3755 */         zzScanError(1);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.UAX29URLEmailTokenizerImpl
 * JD-Core Version:    0.6.0
 */