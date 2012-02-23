/*     */ package org.eclipse.jdt.internal.compiler.parser.diagnose;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.core.compiler.InvalidInputException;
/*     */ import org.eclipse.jdt.internal.compiler.parser.Scanner;
/*     */ import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class LexStream
/*     */   implements TerminalTokens
/*     */ {
/*     */   public static final int IS_AFTER_JUMP = 1;
/*     */   public static final int LBRACE_MISSING = 2;
/*     */   private int tokenCacheIndex;
/*     */   private int tokenCacheEOFIndex;
/*     */   private Token[] tokenCache;
/*  44 */   private int currentIndex = -1;
/*     */   private Scanner scanner;
/*     */   private int[] intervalStartToSkip;
/*     */   private int[] intervalEndToSkip;
/*     */   private int[] intervalFlagsToSkip;
/*  51 */   private int previousInterval = -1;
/*  52 */   private int currentInterval = -1;
/*     */ 
/*     */   public LexStream(int size, Scanner scanner, int[] intervalStartToSkip, int[] intervalEndToSkip, int[] intervalFlagsToSkip, int firstToken, int init, int eof) {
/*  55 */     this.tokenCache = new Token[size];
/*  56 */     this.tokenCacheIndex = 0;
/*  57 */     this.tokenCacheEOFIndex = 2147483647;
/*  58 */     this.tokenCache[0] = new Token();
/*  59 */     this.tokenCache[0].kind = firstToken;
/*  60 */     this.tokenCache[0].name = CharOperation.NO_CHAR;
/*  61 */     this.tokenCache[0].start = init;
/*  62 */     this.tokenCache[0].end = init;
/*  63 */     this.tokenCache[0].line = 0;
/*     */ 
/*  65 */     this.intervalStartToSkip = intervalStartToSkip;
/*  66 */     this.intervalEndToSkip = intervalEndToSkip;
/*  67 */     this.intervalFlagsToSkip = intervalFlagsToSkip;
/*     */ 
/*  69 */     scanner.resetTo(init, eof);
/*  70 */     this.scanner = scanner;
/*     */   }
/*     */ 
/*     */   private void readTokenFromScanner() {
/*  74 */     int length = this.tokenCache.length;
/*  75 */     boolean tokenNotFound = true;
/*     */ 
/*  77 */     while (tokenNotFound)
/*     */       try {
/*  79 */         int tokenKind = this.scanner.getNextToken();
/*  80 */         if (tokenKind != 68) {
/*  81 */           int start = this.scanner.getCurrentTokenStartPosition();
/*  82 */           int end = this.scanner.getCurrentTokenEndPosition();
/*     */ 
/*  84 */           int nextInterval = this.currentInterval + 1;
/*  85 */           if ((this.intervalStartToSkip.length == 0) || 
/*  86 */             (nextInterval >= this.intervalStartToSkip.length) || 
/*  87 */             (start < this.intervalStartToSkip[nextInterval])) {
/*  88 */             Token token = new Token();
/*  89 */             token.kind = tokenKind;
/*  90 */             token.name = this.scanner.getCurrentTokenSource();
/*  91 */             token.start = start;
/*  92 */             token.end = end;
/*  93 */             token.line = Util.getLineNumber(end, this.scanner.lineEnds, 0, this.scanner.linePtr);
/*     */ 
/*  95 */             if ((this.currentInterval != this.previousInterval) && ((this.intervalFlagsToSkip[this.currentInterval] & 0x2) == 0)) {
/*  96 */               token.flags = 1;
/*  97 */               if ((this.intervalFlagsToSkip[this.currentInterval] & 0x1) != 0) {
/*  98 */                 token.flags |= 2;
/*     */               }
/*     */             }
/* 101 */             this.previousInterval = this.currentInterval;
/*     */ 
/* 103 */             this.tokenCache[(++this.tokenCacheIndex % length)] = token;
/*     */ 
/* 105 */             tokenNotFound = false;
/*     */           } else {
/* 107 */             this.scanner.resetTo(this.intervalEndToSkip[(++this.currentInterval)] + 1, this.scanner.eofPosition - 1);
/*     */           }
/*     */         } else {
/* 110 */           int start = this.scanner.getCurrentTokenStartPosition();
/* 111 */           int end = this.scanner.getCurrentTokenEndPosition();
/* 112 */           Token token = new Token();
/* 113 */           token.kind = tokenKind;
/* 114 */           token.name = CharOperation.NO_CHAR;
/* 115 */           token.start = start;
/* 116 */           token.end = end;
/* 117 */           token.line = Util.getLineNumber(end, this.scanner.lineEnds, 0, this.scanner.linePtr);
/*     */ 
/* 119 */           this.tokenCache[(++this.tokenCacheIndex % length)] = token;
/*     */ 
/* 121 */           this.tokenCacheEOFIndex = this.tokenCacheIndex;
/* 122 */           tokenNotFound = false;
/*     */         }
/*     */       }
/*     */       catch (InvalidInputException localInvalidInputException)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public Token token(int index) {
/* 131 */     if (index < 0) {
/* 132 */       Token eofToken = new Token();
/* 133 */       eofToken.kind = 68;
/* 134 */       eofToken.name = CharOperation.NO_CHAR;
/* 135 */       return eofToken;
/*     */     }
/* 137 */     if ((this.tokenCacheEOFIndex >= 0) && (index > this.tokenCacheEOFIndex)) {
/* 138 */       return token(this.tokenCacheEOFIndex);
/*     */     }
/* 140 */     int length = this.tokenCache.length;
/* 141 */     if (index > this.tokenCacheIndex) {
/* 142 */       int tokensToRead = index - this.tokenCacheIndex;
/* 143 */       while (tokensToRead-- != 0)
/* 144 */         readTokenFromScanner();
/*     */     }
/* 146 */     else if (this.tokenCacheIndex - length >= index) {
/* 147 */       return null;
/*     */     }
/*     */ 
/* 150 */     return this.tokenCache[(index % length)];
/*     */   }
/*     */ 
/*     */   public int getToken()
/*     */   {
/* 156 */     return this.currentIndex = next(this.currentIndex);
/*     */   }
/*     */ 
/*     */   public int previous(int tokenIndex) {
/* 160 */     return tokenIndex > 0 ? tokenIndex - 1 : 0;
/*     */   }
/*     */ 
/*     */   public int next(int tokenIndex) {
/* 164 */     return tokenIndex < this.tokenCacheEOFIndex ? tokenIndex + 1 : this.tokenCacheEOFIndex;
/*     */   }
/*     */ 
/*     */   public boolean afterEol(int i) {
/* 168 */     return i < 1;
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 172 */     this.currentIndex = -1;
/*     */   }
/*     */ 
/*     */   public void reset(int i) {
/* 176 */     this.currentIndex = previous(i);
/*     */   }
/*     */ 
/*     */   public int badtoken() {
/* 180 */     return 0;
/*     */   }
/*     */ 
/*     */   public int kind(int tokenIndex) {
/* 184 */     return token(tokenIndex).kind;
/*     */   }
/*     */ 
/*     */   public char[] name(int tokenIndex) {
/* 188 */     return token(tokenIndex).name;
/*     */   }
/*     */ 
/*     */   public int line(int tokenIndex) {
/* 192 */     return token(tokenIndex).line;
/*     */   }
/*     */ 
/*     */   public int start(int tokenIndex) {
/* 196 */     return token(tokenIndex).start;
/*     */   }
/*     */ 
/*     */   public int end(int tokenIndex) {
/* 200 */     return token(tokenIndex).end;
/*     */   }
/*     */ 
/*     */   public int flags(int tokenIndex) {
/* 204 */     return token(tokenIndex).flags;
/*     */   }
/*     */ 
/*     */   public boolean isInsideStream(int index) {
/* 208 */     if ((this.tokenCacheEOFIndex >= 0) && (index > this.tokenCacheEOFIndex))
/* 209 */       return false;
/* 210 */     if (index > this.tokenCacheIndex) {
/* 211 */       return true;
/*     */     }
/* 213 */     return this.tokenCacheIndex - this.tokenCache.length < index;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 223 */     StringBuffer res = new StringBuffer();
/*     */ 
/* 225 */     String source = new String(this.scanner.source);
/* 226 */     if (this.currentIndex < 0) {
/* 227 */       int previousEnd = -1;
/* 228 */       for (int i = 0; i < this.intervalStartToSkip.length; i++) {
/* 229 */         int intervalStart = this.intervalStartToSkip[i];
/* 230 */         int intervalEnd = this.intervalEndToSkip[i];
/*     */ 
/* 232 */         res.append(source.substring(previousEnd + 1, intervalStart));
/* 233 */         res.append('<');
/* 234 */         res.append('@');
/* 235 */         res.append(source.substring(intervalStart, intervalEnd + 1));
/* 236 */         res.append('@');
/* 237 */         res.append('>');
/*     */ 
/* 239 */         previousEnd = intervalEnd;
/*     */       }
/* 241 */       res.append(source.substring(previousEnd + 1));
/*     */     } else {
/* 243 */       Token token = token(this.currentIndex);
/* 244 */       int curtokKind = token.kind;
/* 245 */       int curtokStart = token.start;
/* 246 */       int curtokEnd = token.end;
/*     */ 
/* 248 */       int previousEnd = -1;
/* 249 */       for (int i = 0; i < this.intervalStartToSkip.length; i++) {
/* 250 */         int intervalStart = this.intervalStartToSkip[i];
/* 251 */         int intervalEnd = this.intervalEndToSkip[i];
/*     */ 
/* 253 */         if ((curtokStart >= previousEnd) && (curtokEnd <= intervalStart)) {
/* 254 */           res.append(source.substring(previousEnd + 1, curtokStart));
/* 255 */           res.append('<');
/* 256 */           res.append('#');
/* 257 */           res.append(source.substring(curtokStart, curtokEnd + 1));
/* 258 */           res.append('#');
/* 259 */           res.append('>');
/* 260 */           res.append(source.substring(curtokEnd + 1, intervalStart));
/*     */         } else {
/* 262 */           res.append(source.substring(previousEnd + 1, intervalStart));
/*     */         }
/* 264 */         res.append('<');
/* 265 */         res.append('@');
/* 266 */         res.append(source.substring(intervalStart, intervalEnd + 1));
/* 267 */         res.append('@');
/* 268 */         res.append('>');
/*     */ 
/* 270 */         previousEnd = intervalEnd;
/*     */       }
/* 272 */       if (curtokStart >= previousEnd) {
/* 273 */         res.append(source.substring(previousEnd + 1, curtokStart));
/* 274 */         res.append('<');
/* 275 */         res.append('#');
/* 276 */         if (curtokKind == 68) {
/* 277 */           res.append("EOF#>");
/*     */         } else {
/* 279 */           res.append(source.substring(curtokStart, curtokEnd + 1));
/* 280 */           res.append('#');
/* 281 */           res.append('>');
/* 282 */           res.append(source.substring(curtokEnd + 1));
/*     */         }
/*     */       } else {
/* 285 */         res.append(source.substring(previousEnd + 1));
/*     */       }
/*     */     }
/*     */ 
/* 289 */     return res.toString();
/*     */   }
/*     */ 
/*     */   public static class Token
/*     */   {
/*     */     int kind;
/*     */     char[] name;
/*     */     int start;
/*     */     int end;
/*     */     int line;
/*     */     int flags;
/*     */ 
/*     */     public String toString()
/*     */     {
/*  32 */       StringBuffer buffer = new StringBuffer();
/*  33 */       buffer.append(this.name).append('[').append(this.kind).append(']');
/*  34 */       buffer.append('{').append(this.start).append(',').append(this.end).append('}').append(this.line);
/*  35 */       return buffer.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.diagnose.LexStream
 * JD-Core Version:    0.6.0
 */