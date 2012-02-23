/*     */ package org.apache.lucene.analysis.standard.std31;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.analysis.standard.StandardTokenizerInterface;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ 
/*     */ @Deprecated
/*     */ public final class StandardTokenizerImpl31
/*     */   implements StandardTokenizerInterface
/*     */ {
/*     */   public static final int YYEOF = -1;
/*     */   private static final int ZZ_BUFFERSIZE = 16384;
/*     */   public static final int YYINITIAL = 0;
/*  57 */   private static final int[] ZZ_LEXSTATE = { 0, 0 };
/*     */   private static final String ZZ_CMAP_PACKED = "";
/* 202 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*     */ 
/* 207 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*     */   private static final String ZZ_ACTION_PACKED_0 = "";
/* 237 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/* 277 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*     */   private static final int ZZ_NO_MATCH = 1;
/*     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/* 612 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*     */ 
/* 621 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*     */   private Reader zzReader;
/*     */   private int zzState;
/* 653 */   private int zzLexicalState = 0;
/*     */ 
/* 657 */   private char[] zzBuffer = new char[16384];
/*     */   private int zzMarkedPos;
/*     */   private int zzCurrentPos;
/*     */   private int zzStartRead;
/*     */   private int zzEndRead;
/*     */   private int yyline;
/*     */   private int yychar;
/*     */   private int yycolumn;
/* 687 */   private boolean zzAtBOL = true;
/*     */   private boolean zzAtEOF;
/*     */   private boolean zzEOFDone;
/*     */   public static final int WORD_TYPE = 0;
/*     */   public static final int NUMERIC_TYPE = 6;
/*     */   public static final int SOUTH_EAST_ASIAN_TYPE = 9;
/*     */   public static final int IDEOGRAPHIC_TYPE = 10;
/*     */   public static final int HIRAGANA_TYPE = 11;
/*     */   public static final int KATAKANA_TYPE = 12;
/*     */   public static final int HANGUL_TYPE = 13;
/*     */ 
/*     */   private static int[] zzUnpackAction()
/*     */   {
/* 215 */     int[] result = new int[114];
/* 216 */     int offset = 0;
/* 217 */     offset = zzUnpackAction("", offset, result);
/* 218 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackAction(String packed, int offset, int[] result) {
/* 222 */     int i = 0;
/* 223 */     int j = offset;
/* 224 */     int l = packed.length();
/* 225 */     while (i < l) {
/* 226 */       int count = packed.charAt(i++);
/* 227 */       int value = packed.charAt(i++);
/*     */       do { result[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 230 */     return j;
/*     */   }
/*     */ 
/*     */   private static int[] zzUnpackRowMap()
/*     */   {
/* 257 */     int[] result = new int[114];
/* 258 */     int offset = 0;
/* 259 */     offset = zzUnpackRowMap("", offset, result);
/* 260 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackRowMap(String packed, int offset, int[] result) {
/* 264 */     int i = 0;
/* 265 */     int j = offset;
/* 266 */     int l = packed.length();
/* 267 */     while (i < l) {
/* 268 */       int high = packed.charAt(i++) << '\020';
/* 269 */       result[(j++)] = (high | packed.charAt(i++));
/*     */     }
/* 271 */     return j;
/*     */   }
/*     */ 
/*     */   private static int[] zzUnpackTrans()
/*     */   {
/* 586 */     int[] result = new int[10609];
/* 587 */     int offset = 0;
/* 588 */     offset = zzUnpackTrans("", offset, result);
/* 589 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackTrans(String packed, int offset, int[] result) {
/* 593 */     int i = 0;
/* 594 */     int j = offset;
/* 595 */     int l = packed.length();
/* 596 */     while (i < l) {
/* 597 */       int count = packed.charAt(i++);
/* 598 */       int value = packed.charAt(i++);
/* 599 */       value--;
/*     */       do { result[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 602 */     return j;
/*     */   }
/*     */ 
/*     */   private static int[] zzUnpackAttribute()
/*     */   {
/* 628 */     int[] result = new int[114];
/* 629 */     int offset = 0;
/* 630 */     offset = zzUnpackAttribute("", offset, result);
/* 631 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackAttribute(String packed, int offset, int[] result) {
/* 635 */     int i = 0;
/* 636 */     int j = offset;
/* 637 */     int l = packed.length();
/* 638 */     while (i < l) {
/* 639 */       int count = packed.charAt(i++);
/* 640 */       int value = packed.charAt(i++);
/*     */       do { result[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 643 */     return j;
/*     */   }
/*     */ 
/*     */   public final int yychar()
/*     */   {
/* 722 */     return this.yychar;
/*     */   }
/*     */ 
/*     */   public final void getText(CharTermAttribute t)
/*     */   {
/* 729 */     t.copyBuffer(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*     */   }
/*     */ 
/*     */   public StandardTokenizerImpl31(Reader in)
/*     */   {
/* 740 */     this.zzReader = in;
/*     */   }
/*     */ 
/*     */   public StandardTokenizerImpl31(InputStream in)
/*     */   {
/* 750 */     this(new InputStreamReader(in));
/*     */   }
/*     */ 
/*     */   private static char[] zzUnpackCMap(String packed)
/*     */   {
/* 760 */     char[] map = new char[65536];
/* 761 */     int i = 0;
/* 762 */     int j = 0;
/* 763 */     while (i < 2650) {
/* 764 */       int count = packed.charAt(i++);
/* 765 */       char value = packed.charAt(i++);
/*     */       do { map[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 768 */     return map;
/*     */   }
/*     */ 
/*     */   private boolean zzRefill()
/*     */     throws IOException
/*     */   {
/* 782 */     if (this.zzStartRead > 0) {
/* 783 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*     */ 
/* 788 */       this.zzEndRead -= this.zzStartRead;
/* 789 */       this.zzCurrentPos -= this.zzStartRead;
/* 790 */       this.zzMarkedPos -= this.zzStartRead;
/* 791 */       this.zzStartRead = 0;
/*     */     }
/*     */ 
/* 795 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*     */     {
/* 797 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 798 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 799 */       this.zzBuffer = newBuffer;
/*     */     }
/*     */ 
/* 803 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*     */ 
/* 806 */     if (numRead > 0) {
/* 807 */       this.zzEndRead += numRead;
/* 808 */       return false;
/*     */     }
/*     */ 
/* 811 */     if (numRead == 0) {
/* 812 */       int c = this.zzReader.read();
/* 813 */       if (c == -1) {
/* 814 */         return true;
/*     */       }
/* 816 */       this.zzBuffer[(this.zzEndRead++)] = (char)c;
/* 817 */       return false;
/*     */     }
/*     */ 
/* 822 */     return true;
/*     */   }
/*     */ 
/*     */   public final void yyclose()
/*     */     throws IOException
/*     */   {
/* 830 */     this.zzAtEOF = true;
/* 831 */     this.zzEndRead = this.zzStartRead;
/*     */ 
/* 833 */     if (this.zzReader != null)
/* 834 */       this.zzReader.close();
/*     */   }
/*     */ 
/*     */   public final void yyreset(Reader reader)
/*     */   {
/* 851 */     this.zzReader = reader;
/* 852 */     this.zzAtBOL = true;
/* 853 */     this.zzAtEOF = false;
/* 854 */     this.zzEOFDone = false;
/* 855 */     this.zzEndRead = (this.zzStartRead = 0);
/* 856 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 857 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 858 */     this.zzLexicalState = 0;
/* 859 */     if (this.zzBuffer.length > 16384)
/* 860 */       this.zzBuffer = new char[16384];
/*     */   }
/*     */ 
/*     */   public final int yystate()
/*     */   {
/* 868 */     return this.zzLexicalState;
/*     */   }
/*     */ 
/*     */   public final void yybegin(int newState)
/*     */   {
/* 878 */     this.zzLexicalState = newState;
/*     */   }
/*     */ 
/*     */   public final String yytext()
/*     */   {
/* 886 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*     */   }
/*     */ 
/*     */   public final char yycharat(int pos)
/*     */   {
/* 902 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*     */   }
/*     */ 
/*     */   public final int yylength()
/*     */   {
/* 910 */     return this.zzMarkedPos - this.zzStartRead;
/*     */   }
/*     */ 
/*     */   private void zzScanError(int errorCode)
/*     */   {
/*     */     String message;
/*     */     try
/*     */     {
/* 931 */       message = ZZ_ERROR_MSG[errorCode];
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException e) {
/* 934 */       message = ZZ_ERROR_MSG[0];
/*     */     }
/*     */ 
/* 937 */     throw new Error(message);
/*     */   }
/*     */ 
/*     */   public void yypushback(int number)
/*     */   {
/* 950 */     if (number > yylength()) {
/* 951 */       zzScanError(2);
/*     */     }
/* 953 */     this.zzMarkedPos -= number;
/*     */   }
/*     */ 
/*     */   public int getNextToken()
/*     */     throws IOException
/*     */   {
/* 971 */     int zzEndReadL = this.zzEndRead;
/* 972 */     char[] zzBufferL = this.zzBuffer;
/* 973 */     char[] zzCMapL = ZZ_CMAP;
/*     */ 
/* 975 */     int[] zzTransL = ZZ_TRANS;
/* 976 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 977 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*     */     while (true)
/*     */     {
/* 980 */       int zzMarkedPosL = this.zzMarkedPos;
/*     */ 
/* 982 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*     */ 
/* 984 */       int zzAction = -1;
/*     */ 
/* 986 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*     */ 
/* 988 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*     */ 
/* 991 */       int zzAttributes = zzAttrL[this.zzState];
/* 992 */       if ((zzAttributes & 0x1) == 1)
/* 993 */         zzAction = this.zzState;
/*     */       int zzInput;
/*     */       while (true)
/*     */       {
/*     */         int zzInput;
/* 1000 */         if (zzCurrentPosL < zzEndReadL) {
/* 1001 */           zzInput = zzBufferL[(zzCurrentPosL++)]; } else {
/* 1002 */           if (this.zzAtEOF) {
/* 1003 */             int zzInput = -1;
/* 1004 */             break;
/*     */           }
/*     */ 
/* 1008 */           this.zzCurrentPos = zzCurrentPosL;
/* 1009 */           this.zzMarkedPos = zzMarkedPosL;
/* 1010 */           boolean eof = zzRefill();
/*     */ 
/* 1012 */           zzCurrentPosL = this.zzCurrentPos;
/* 1013 */           zzMarkedPosL = this.zzMarkedPos;
/* 1014 */           zzBufferL = this.zzBuffer;
/* 1015 */           zzEndReadL = this.zzEndRead;
/* 1016 */           if (eof) {
/* 1017 */             int zzInput = -1;
/* 1018 */             break;
/*     */           }
/*     */ 
/* 1021 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*     */         }
/*     */ 
/* 1024 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 1025 */         if (zzNext == -1) break;
/* 1026 */         this.zzState = zzNext;
/*     */ 
/* 1028 */         zzAttributes = zzAttrL[this.zzState];
/* 1029 */         if ((zzAttributes & 0x1) == 1) {
/* 1030 */           zzAction = this.zzState;
/* 1031 */           zzMarkedPosL = zzCurrentPosL;
/* 1032 */           if ((zzAttributes & 0x8) == 8)
/*     */           {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 1039 */       this.zzMarkedPos = zzMarkedPosL;
/*     */ 
/* 1041 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
/*     */       case 2:
/* 1043 */         return 0;
/*     */       case 9:
/* 1045 */         break;
/*     */       case 5:
/* 1047 */         return 9;
/*     */       case 10:
/* 1049 */         break;
/*     */       case 4:
/* 1051 */         return 12;
/*     */       case 11:
/* 1053 */         break;
/*     */       case 6:
/* 1055 */         return 10;
/*     */       case 12:
/* 1057 */         break;
/*     */       case 1:
/*     */       case 13:
/* 1061 */         break;
/*     */       case 8:
/* 1063 */         return 13;
/*     */       case 14:
/* 1065 */         break;
/*     */       case 3:
/* 1067 */         return 6;
/*     */       case 15:
/* 1069 */         break;
/*     */       case 7:
/* 1071 */         return 11;
/*     */       case 16:
/* 1073 */         break;
/*     */       default:
/* 1075 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos)) {
/* 1076 */           this.zzAtEOF = true;
/*     */ 
/* 1078 */           return -1;
/*     */         }
/*     */ 
/* 1082 */         zzScanError(1);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.std31.StandardTokenizerImpl31
 * JD-Core Version:    0.6.0
 */