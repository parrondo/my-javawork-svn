/*     */ package org.apache.lucene.queryParser;
/*     */ 
/*     */ public class TokenMgrError extends Error
/*     */ {
/*     */   static final int LEXICAL_ERROR = 0;
/*     */   static final int STATIC_LEXER_ERROR = 1;
/*     */   static final int INVALID_LEXICAL_STATE = 2;
/*     */   static final int LOOP_DETECTED = 3;
/*     */   int errorCode;
/*     */ 
/*     */   protected static final String addEscapes(String str)
/*     */   {
/*  45 */     StringBuffer retval = new StringBuffer();
/*     */ 
/*  47 */     for (int i = 0; i < str.length(); i++) {
/*  48 */       switch (str.charAt(i))
/*     */       {
/*     */       case '\000':
/*  51 */         break;
/*     */       case '\b':
/*  53 */         retval.append("\\b");
/*  54 */         break;
/*     */       case '\t':
/*  56 */         retval.append("\\t");
/*  57 */         break;
/*     */       case '\n':
/*  59 */         retval.append("\\n");
/*  60 */         break;
/*     */       case '\f':
/*  62 */         retval.append("\\f");
/*  63 */         break;
/*     */       case '\r':
/*  65 */         retval.append("\\r");
/*  66 */         break;
/*     */       case '"':
/*  68 */         retval.append("\\\"");
/*  69 */         break;
/*     */       case '\'':
/*  71 */         retval.append("\\'");
/*  72 */         break;
/*     */       case '\\':
/*  74 */         retval.append("\\\\");
/*  75 */         break;
/*     */       default:
/*     */         char ch;
/*  77 */         if (((ch = str.charAt(i)) < ' ') || (ch > '~')) {
/*  78 */           String s = "0000" + Integer.toString(ch, 16);
/*  79 */           retval.append("\\u" + s.substring(s.length() - 4, s.length()));
/*     */         } else {
/*  81 */           retval.append(ch);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  86 */     return retval.toString();
/*     */   }
/*     */ 
/*     */   protected static String LexicalError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar)
/*     */   {
/* 102 */     return "Lexical error at line " + errorLine + ", column " + errorColumn + ".  Encountered: " + (EOFSeen ? "<EOF> " : new StringBuilder().append("\"").append(addEscapes(String.valueOf(curChar))).append("\"").append(" (").append(curChar).append("), ").toString()) + "after : \"" + addEscapes(errorAfter) + "\"";
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 119 */     return super.getMessage();
/*     */   }
/*     */ 
/*     */   public TokenMgrError()
/*     */   {
/*     */   }
/*     */ 
/*     */   public TokenMgrError(String message, int reason)
/*     */   {
/* 132 */     super(message);
/* 133 */     this.errorCode = reason;
/*     */   }
/*     */ 
/*     */   public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int reason)
/*     */   {
/* 138 */     this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.queryParser.TokenMgrError
 * JD-Core Version:    0.6.0
 */