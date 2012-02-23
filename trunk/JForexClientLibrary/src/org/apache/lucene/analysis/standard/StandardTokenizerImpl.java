/*     */ package org.apache.lucene.analysis.standard;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ 
/*     */ public final class StandardTokenizerImpl
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
/* 278 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*     */   private static final int ZZ_NO_MATCH = 1;
/*     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/* 631 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*     */ 
/* 640 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*     */   private Reader zzReader;
/*     */   private int zzState;
/* 672 */   private int zzLexicalState = 0;
/*     */ 
/* 676 */   private char[] zzBuffer = new char[16384];
/*     */   private int zzMarkedPos;
/*     */   private int zzCurrentPos;
/*     */   private int zzStartRead;
/*     */   private int zzEndRead;
/*     */   private int yyline;
/*     */   private int yychar;
/*     */   private int yycolumn;
/* 706 */   private boolean zzAtBOL = true;
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
/* 215 */     int[] result = new int[124];
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
/* 258 */     int[] result = new int[124];
/* 259 */     int offset = 0;
/* 260 */     offset = zzUnpackRowMap("", offset, result);
/* 261 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackRowMap(String packed, int offset, int[] result) {
/* 265 */     int i = 0;
/* 266 */     int j = offset;
/* 267 */     int l = packed.length();
/* 268 */     while (i < l) {
/* 269 */       int high = packed.charAt(i++) << '\020';
/* 270 */       result[(j++)] = (high | packed.charAt(i++));
/*     */     }
/* 272 */     return j;
/*     */   }
/*     */ 
/*     */   private static int[] zzUnpackTrans()
/*     */   {
/* 605 */     int[] result = new int[11845];
/* 606 */     int offset = 0;
/* 607 */     offset = zzUnpackTrans("", offset, result);
/* 608 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackTrans(String packed, int offset, int[] result) {
/* 612 */     int i = 0;
/* 613 */     int j = offset;
/* 614 */     int l = packed.length();
/* 615 */     while (i < l) {
/* 616 */       int count = packed.charAt(i++);
/* 617 */       int value = packed.charAt(i++);
/* 618 */       value--;
/*     */       do { result[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 621 */     return j;
/*     */   }
/*     */ 
/*     */   private static int[] zzUnpackAttribute()
/*     */   {
/* 647 */     int[] result = new int[124];
/* 648 */     int offset = 0;
/* 649 */     offset = zzUnpackAttribute("", offset, result);
/* 650 */     return result;
/*     */   }
/*     */ 
/*     */   private static int zzUnpackAttribute(String packed, int offset, int[] result) {
/* 654 */     int i = 0;
/* 655 */     int j = offset;
/* 656 */     int l = packed.length();
/* 657 */     while (i < l) {
/* 658 */       int count = packed.charAt(i++);
/* 659 */       int value = packed.charAt(i++);
/*     */       do { result[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 662 */     return j;
/*     */   }
/*     */ 
/*     */   public final int yychar()
/*     */   {
/* 741 */     return this.yychar;
/*     */   }
/*     */ 
/*     */   public final void getText(CharTermAttribute t)
/*     */   {
/* 748 */     t.copyBuffer(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*     */   }
/*     */ 
/*     */   public StandardTokenizerImpl(Reader in)
/*     */   {
/* 759 */     this.zzReader = in;
/*     */   }
/*     */ 
/*     */   public StandardTokenizerImpl(InputStream in)
/*     */   {
/* 769 */     this(new InputStreamReader(in));
/*     */   }
/*     */ 
/*     */   private static char[] zzUnpackCMap(String packed)
/*     */   {
/* 779 */     char[] map = new char[65536];
/* 780 */     int i = 0;
/* 781 */     int j = 0;
/* 782 */     while (i < 2650) {
/* 783 */       int count = packed.charAt(i++);
/* 784 */       char value = packed.charAt(i++);
/*     */       do { map[(j++)] = value; count--; } while (count > 0);
/*     */     }
/* 787 */     return map;
/*     */   }
/*     */ 
/*     */   private boolean zzRefill()
/*     */     throws IOException
/*     */   {
/* 801 */     if (this.zzStartRead > 0) {
/* 802 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*     */ 
/* 807 */       this.zzEndRead -= this.zzStartRead;
/* 808 */       this.zzCurrentPos -= this.zzStartRead;
/* 809 */       this.zzMarkedPos -= this.zzStartRead;
/* 810 */       this.zzStartRead = 0;
/*     */     }
/*     */ 
/* 814 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*     */     {
/* 816 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 817 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 818 */       this.zzBuffer = newBuffer;
/*     */     }
/*     */ 
/* 822 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*     */ 
/* 825 */     if (numRead > 0) {
/* 826 */       this.zzEndRead += numRead;
/* 827 */       return false;
/*     */     }
/*     */ 
/* 830 */     if (numRead == 0) {
/* 831 */       int c = this.zzReader.read();
/* 832 */       if (c == -1) {
/* 833 */         return true;
/*     */       }
/* 835 */       this.zzBuffer[(this.zzEndRead++)] = (char)c;
/* 836 */       return false;
/*     */     }
/*     */ 
/* 841 */     return true;
/*     */   }
/*     */ 
/*     */   public final void yyclose()
/*     */     throws IOException
/*     */   {
/* 849 */     this.zzAtEOF = true;
/* 850 */     this.zzEndRead = this.zzStartRead;
/*     */ 
/* 852 */     if (this.zzReader != null)
/* 853 */       this.zzReader.close();
/*     */   }
/*     */ 
/*     */   public final void yyreset(Reader reader)
/*     */   {
/* 870 */     this.zzReader = reader;
/* 871 */     this.zzAtBOL = true;
/* 872 */     this.zzAtEOF = false;
/* 873 */     this.zzEOFDone = false;
/* 874 */     this.zzEndRead = (this.zzStartRead = 0);
/* 875 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 876 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 877 */     this.zzLexicalState = 0;
/* 878 */     if (this.zzBuffer.length > 16384)
/* 879 */       this.zzBuffer = new char[16384];
/*     */   }
/*     */ 
/*     */   public final int yystate()
/*     */   {
/* 887 */     return this.zzLexicalState;
/*     */   }
/*     */ 
/*     */   public final void yybegin(int newState)
/*     */   {
/* 897 */     this.zzLexicalState = newState;
/*     */   }
/*     */ 
/*     */   public final String yytext()
/*     */   {
/* 905 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*     */   }
/*     */ 
/*     */   public final char yycharat(int pos)
/*     */   {
/* 921 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*     */   }
/*     */ 
/*     */   public final int yylength()
/*     */   {
/* 929 */     return this.zzMarkedPos - this.zzStartRead;
/*     */   }
/*     */ 
/*     */   private void zzScanError(int errorCode)
/*     */   {
/*     */     String message;
/*     */     try
/*     */     {
/* 950 */       message = ZZ_ERROR_MSG[errorCode];
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException e) {
/* 953 */       message = ZZ_ERROR_MSG[0];
/*     */     }
/*     */ 
/* 956 */     throw new Error(message);
/*     */   }
/*     */ 
/*     */   public void yypushback(int number)
/*     */   {
/* 969 */     if (number > yylength()) {
/* 970 */       zzScanError(2);
/*     */     }
/* 972 */     this.zzMarkedPos -= number;
/*     */   }
/*     */ 
/*     */   public int getNextToken()
/*     */     throws IOException
/*     */   {
/* 990 */     int zzEndReadL = this.zzEndRead;
/* 991 */     char[] zzBufferL = this.zzBuffer;
/* 992 */     char[] zzCMapL = ZZ_CMAP;
/*     */ 
/* 994 */     int[] zzTransL = ZZ_TRANS;
/* 995 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 996 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*     */     while (true)
/*     */     {
/* 999 */       int zzMarkedPosL = this.zzMarkedPos;
/*     */ 
/* 1001 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*     */ 
/* 1003 */       int zzAction = -1;
/*     */ 
/* 1005 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*     */ 
/* 1007 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*     */ 
/* 1010 */       int zzAttributes = zzAttrL[this.zzState];
/* 1011 */       if ((zzAttributes & 0x1) == 1)
/* 1012 */         zzAction = this.zzState;
/*     */       int zzInput;
/*     */       while (true)
/*     */       {
/*     */         int zzInput;
/* 1019 */         if (zzCurrentPosL < zzEndReadL) {
/* 1020 */           zzInput = zzBufferL[(zzCurrentPosL++)]; } else {
/* 1021 */           if (this.zzAtEOF) {
/* 1022 */             int zzInput = -1;
/* 1023 */             break;
/*     */           }
/*     */ 
/* 1027 */           this.zzCurrentPos = zzCurrentPosL;
/* 1028 */           this.zzMarkedPos = zzMarkedPosL;
/* 1029 */           boolean eof = zzRefill();
/*     */ 
/* 1031 */           zzCurrentPosL = this.zzCurrentPos;
/* 1032 */           zzMarkedPosL = this.zzMarkedPos;
/* 1033 */           zzBufferL = this.zzBuffer;
/* 1034 */           zzEndReadL = this.zzEndRead;
/* 1035 */           if (eof) {
/* 1036 */             int zzInput = -1;
/* 1037 */             break;
/*     */           }
/*     */ 
/* 1040 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*     */         }
/*     */ 
/* 1043 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 1044 */         if (zzNext == -1) break;
/* 1045 */         this.zzState = zzNext;
/*     */ 
/* 1047 */         zzAttributes = zzAttrL[this.zzState];
/* 1048 */         if ((zzAttributes & 0x1) == 1) {
/* 1049 */           zzAction = this.zzState;
/* 1050 */           zzMarkedPosL = zzCurrentPosL;
/* 1051 */           if ((zzAttributes & 0x8) == 8)
/*     */           {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 1058 */       this.zzMarkedPos = zzMarkedPosL;
/*     */ 
/* 1060 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
/*     */       case 2:
/* 1062 */         return 0;
/*     */       case 9:
/* 1064 */         break;
/*     */       case 5:
/* 1066 */         return 9;
/*     */       case 10:
/* 1068 */         break;
/*     */       case 4:
/* 1070 */         return 12;
/*     */       case 11:
/* 1072 */         break;
/*     */       case 6:
/* 1074 */         return 10;
/*     */       case 12:
/* 1076 */         break;
/*     */       case 1:
/*     */       case 13:
/* 1080 */         break;
/*     */       case 8:
/* 1082 */         return 13;
/*     */       case 14:
/* 1084 */         break;
/*     */       case 3:
/* 1086 */         return 6;
/*     */       case 15:
/* 1088 */         break;
/*     */       case 7:
/* 1090 */         return 11;
/*     */       case 16:
/* 1092 */         break;
/*     */       default:
/* 1094 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos)) {
/* 1095 */           this.zzAtEOF = true;
/*     */ 
/* 1097 */           return -1;
/*     */         }
/*     */ 
/* 1101 */         zzScanError(1);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.StandardTokenizerImpl
 * JD-Core Version:    0.6.0
 */