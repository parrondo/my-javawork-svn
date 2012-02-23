/*     */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*     */ 
/*     */ public class ParseException extends Exception
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public Token currentToken;
/*     */   public int[][] expectedTokenSequences;
/*     */   public String[] tokenImage;
/* 134 */   protected String eol = System.getProperty("line.separator", "\n");
/*     */ 
/*     */   public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal)
/*     */   {
/*  34 */     super(initialise(currentTokenVal, expectedTokenSequencesVal, tokenImageVal));
/*  35 */     this.currentToken = currentTokenVal;
/*  36 */     this.expectedTokenSequences = expectedTokenSequencesVal;
/*  37 */     this.tokenImage = tokenImageVal;
/*     */   }
/*     */ 
/*     */   public ParseException()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ParseException(String message)
/*     */   {
/*  56 */     super(message);
/*     */   }
/*     */ 
/*     */   private static String initialise(Token currentToken, int[][] expectedTokenSequences, String[] tokenImage)
/*     */   {
/*  91 */     String eol = System.getProperty("line.separator", "\n");
/*  92 */     StringBuffer expected = new StringBuffer();
/*  93 */     int maxSize = 0;
/*  94 */     for (int i = 0; i < expectedTokenSequences.length; i++) {
/*  95 */       if (maxSize < expectedTokenSequences[i].length) {
/*  96 */         maxSize = expectedTokenSequences[i].length;
/*     */       }
/*  98 */       for (int j = 0; j < expectedTokenSequences[i].length; j++) {
/*  99 */         expected.append(tokenImage[expectedTokenSequences[i][j]]).append(' ');
/*     */       }
/* 101 */       if (expectedTokenSequences[i][(expectedTokenSequences[i].length - 1)] != 0) {
/* 102 */         expected.append("...");
/*     */       }
/* 104 */       expected.append(eol).append("    ");
/*     */     }
/* 106 */     String retval = "Encountered \"";
/* 107 */     Token tok = currentToken.next;
/* 108 */     for (int i = 0; i < maxSize; i++) {
/* 109 */       if (i != 0) retval = retval + " ";
/* 110 */       if (tok.kind == 0) {
/* 111 */         retval = retval + tokenImage[0];
/* 112 */         break;
/*     */       }
/* 114 */       retval = retval + " " + tokenImage[tok.kind];
/* 115 */       retval = retval + " \"";
/* 116 */       retval = retval + add_escapes(tok.image);
/* 117 */       retval = retval + " \"";
/* 118 */       tok = tok.next;
/*     */     }
/* 120 */     retval = retval + "\" at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn;
/* 121 */     retval = retval + "." + eol;
/* 122 */     if (expectedTokenSequences.length == 1)
/* 123 */       retval = retval + "Was expecting:" + eol + "    ";
/*     */     else {
/* 125 */       retval = retval + "Was expecting one of:" + eol + "    ";
/*     */     }
/* 127 */     retval = retval + expected.toString();
/* 128 */     return retval;
/*     */   }
/*     */ 
/*     */   static String add_escapes(String str)
/*     */   {
/* 142 */     StringBuffer retval = new StringBuffer();
/*     */ 
/* 144 */     for (int i = 0; i < str.length(); i++) {
/* 145 */       switch (str.charAt(i))
/*     */       {
/*     */       case '\000':
/* 148 */         break;
/*     */       case '\b':
/* 150 */         retval.append("\\b");
/* 151 */         break;
/*     */       case '\t':
/* 153 */         retval.append("\\t");
/* 154 */         break;
/*     */       case '\n':
/* 156 */         retval.append("\\n");
/* 157 */         break;
/*     */       case '\f':
/* 159 */         retval.append("\\f");
/* 160 */         break;
/*     */       case '\r':
/* 162 */         retval.append("\\r");
/* 163 */         break;
/*     */       case '"':
/* 165 */         retval.append("\\\"");
/* 166 */         break;
/*     */       case '\'':
/* 168 */         retval.append("\\'");
/* 169 */         break;
/*     */       case '\\':
/* 171 */         retval.append("\\\\");
/* 172 */         break;
/*     */       default:
/*     */         char ch;
/* 174 */         if (((ch = str.charAt(i)) < ' ') || (ch > '~')) {
/* 175 */           String s = "0000" + Integer.toString(ch, 16);
/* 176 */           retval.append("\\u" + s.substring(s.length() - 4, s.length()));
/*     */         } else {
/* 178 */           retval.append(ch);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 183 */     return retval.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.ParseException
 * JD-Core Version:    0.6.0
 */