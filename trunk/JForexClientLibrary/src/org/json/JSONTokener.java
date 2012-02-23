/*     */ package org.json;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ 
/*     */ public class JSONTokener
/*     */ {
/*     */   private int myIndex;
/*     */   private String mySource;
/*     */ 
/*     */   public JSONTokener(String s)
/*     */   {
/*  56 */     this.myIndex = 0;
/*  57 */     this.mySource = s;
/*     */   }
/*     */ 
/*     */   public void back()
/*     */   {
/*  67 */     if (this.myIndex > 0)
/*  68 */       this.myIndex -= 1;
/*     */   }
/*     */ 
/*     */   public static int dehexchar(char c)
/*     */   {
/*  81 */     if ((c >= '0') && (c <= '9')) {
/*  82 */       return c - '0';
/*     */     }
/*  84 */     if ((c >= 'A') && (c <= 'F')) {
/*  85 */       return c + '\n' - 65;
/*     */     }
/*  87 */     if ((c >= 'a') && (c <= 'f')) {
/*  88 */       return c + '\n' - 97;
/*     */     }
/*  90 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean more()
/*     */   {
/* 100 */     return this.myIndex < this.mySource.length();
/*     */   }
/*     */ 
/*     */   public char next()
/*     */   {
/* 110 */     if (more()) {
/* 111 */       char c = this.mySource.charAt(this.myIndex);
/* 112 */       this.myIndex += 1;
/* 113 */       return c;
/*     */     }
/* 115 */     return '\000';
/*     */   }
/*     */ 
/*     */   public char next(char c)
/*     */     throws ParseException
/*     */   {
/* 127 */     char n = next();
/* 128 */     if (n != c) {
/* 129 */       throw syntaxError("Expected '" + c + "' and instead saw '" + n + "'.");
/*     */     }
/*     */ 
/* 132 */     return n;
/*     */   }
/*     */ 
/*     */   public String next(int n)
/*     */     throws ParseException
/*     */   {
/* 146 */     int i = this.myIndex;
/* 147 */     int j = i + n;
/* 148 */     if (j >= this.mySource.length()) {
/* 149 */       throw syntaxError("Substring bounds error");
/*     */     }
/* 151 */     this.myIndex += n;
/* 152 */     return this.mySource.substring(i, j);
/*     */   }
/*     */ 
/*     */   public char nextClean()
/*     */     throws ParseException
/*     */   {
/*     */     while (true)
/*     */     {
/* 164 */       char c = next();
/* 165 */       if (c == '/')
/* 166 */         switch (next()) {
/*     */         case '/':
/*     */           do {
/* 169 */             c = next();
/* 170 */             if ((c == '\n') || (c == '\r')) break; 
/* 170 */           }while (c != 0);
/* 171 */           break;
/*     */         case '*':
/*     */           while (true) {
/* 174 */             c = next();
/* 175 */             if (c == 0) {
/* 176 */               throw syntaxError("Unclosed comment.");
/*     */             }
/* 178 */             if (c == '*') {
/* 179 */               if (next() == '/') {
/*     */                 break;
/*     */               }
/* 182 */               back();
/*     */             }
/*     */           }
/*     */ 
/*     */         default:
/* 187 */           back();
/* 188 */           return '/';
/*     */         }
/* 190 */       else if (c == '#')
/*     */         do {
/* 192 */           c = next();
/* 193 */           if ((c == '\n') || (c == '\r')) break; 
/* 193 */         }while (c != 0);
/* 194 */       else if ((c == 0) || (c > ' '))
/* 195 */         return c;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String nextString(char quote)
/*     */     throws ParseException
/*     */   {
/* 214 */     StringBuffer sb = new StringBuffer();
/*     */     while (true) {
/* 216 */       char c = next();
/* 217 */       switch (c) {
/*     */       case '\000':
/*     */       case '\n':
/*     */       case '\r':
/* 221 */         throw syntaxError("Unterminated string");
/*     */       case '\\':
/* 223 */         c = next();
/* 224 */         switch (c) {
/*     */         case 'b':
/* 226 */           sb.append('\b');
/* 227 */           break;
/*     */         case 't':
/* 229 */           sb.append('\t');
/* 230 */           break;
/*     */         case 'n':
/* 232 */           sb.append('\n');
/* 233 */           break;
/*     */         case 'f':
/* 235 */           sb.append('\f');
/* 236 */           break;
/*     */         case 'r':
/* 238 */           sb.append('\r');
/* 239 */           break;
/*     */         case 'u':
/* 241 */           sb.append((char)Integer.parseInt(next(4), 16));
/* 242 */           break;
/*     */         case 'x':
/* 244 */           sb.append((char)Integer.parseInt(next(2), 16));
/* 245 */           break;
/*     */         case 'c':
/*     */         case 'd':
/*     */         case 'e':
/*     */         case 'g':
/*     */         case 'h':
/*     */         case 'i':
/*     */         case 'j':
/*     */         case 'k':
/*     */         case 'l':
/*     */         case 'm':
/*     */         case 'o':
/*     */         case 'p':
/*     */         case 'q':
/*     */         case 's':
/*     */         case 'v':
/*     */         case 'w':
/*     */         default:
/* 247 */           sb.append(c);
/*     */         }
/* 249 */         break;
/*     */       default:
/* 251 */         if (c == quote) {
/* 252 */           return sb.toString();
/*     */         }
/* 254 */         sb.append(c);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String nextTo(char d)
/*     */   {
/* 267 */     StringBuffer sb = new StringBuffer();
/*     */     while (true) {
/* 269 */       char c = next();
/* 270 */       if ((c == d) || (c == 0) || (c == '\n') || (c == '\r')) {
/* 271 */         if (c != 0) {
/* 272 */           back();
/*     */         }
/* 274 */         return sb.toString().trim();
/*     */       }
/* 276 */       sb.append(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String nextTo(String delimiters)
/*     */   {
/* 289 */     StringBuffer sb = new StringBuffer();
/*     */     while (true) {
/* 291 */       char c = next();
/* 292 */       if ((delimiters.indexOf(c) >= 0) || (c == 0) || (c == '\n') || (c == '\r'))
/*     */       {
/* 294 */         if (c != 0) {
/* 295 */           back();
/*     */         }
/* 297 */         return sb.toString().trim();
/*     */       }
/* 299 */       sb.append(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object nextValue()
/*     */     throws ParseException
/*     */   {
/* 312 */     char c = nextClean();
/*     */ 
/* 315 */     switch (c) {
/*     */     case '"':
/*     */     case '\'':
/* 318 */       return nextString(c);
/*     */     case '{':
/* 320 */       back();
/* 321 */       return new JSONObject(this);
/*     */     case '[':
/* 323 */       back();
/* 324 */       return new JSONArray(this);
/*     */     }
/*     */ 
/* 336 */     StringBuffer sb = new StringBuffer();
/* 337 */     char b = c;
/* 338 */     while ((c >= ' ') && (",:]}/\\\"[{;=#".indexOf(c) < 0)) {
/* 339 */       sb.append(c);
/* 340 */       c = next();
/*     */     }
/* 342 */     back();
/*     */ 
/* 348 */     String s = sb.toString().trim();
/* 349 */     if (s.equals("")) {
/* 350 */       throw syntaxError("Missing value.");
/*     */     }
/* 352 */     if (s.equalsIgnoreCase("true")) {
/* 353 */       return Boolean.TRUE;
/*     */     }
/* 355 */     if (s.equalsIgnoreCase("false")) {
/* 356 */       return Boolean.FALSE;
/*     */     }
/* 358 */     if (s.equalsIgnoreCase("null")) {
/* 359 */       return JSONObject.NULL;
/*     */     }
/*     */ 
/* 370 */     if (((b >= '0') && (b <= '9')) || (b == '.') || (b == '-') || (b == '+')) {
/* 371 */       if (b == '0')
/* 372 */         if ((s.length() > 2) && ((s.charAt(1) == 'x') || (s.charAt(1) == 'X')))
/*     */           try
/*     */           {
/* 375 */             return new Integer(Integer.parseInt(s.substring(2), 16));
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/*     */           }
/*     */         else
/*     */           try {
/* 382 */             return new Integer(Integer.parseInt(s, 8));
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/*     */           }
/*     */       try
/*     */       {
/* 389 */         return new Integer(s);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */         try {
/* 394 */           return new Double(s);
/*     */         } catch (Exception e) {
/*     */         }
/*     */       }
/*     */     }
/* 399 */     return s;
/*     */   }
/*     */   public char skipTo(char to) {
/* 412 */     int index = this.myIndex;
/*     */     char c;
/*     */     do { c = next();
/* 415 */       if (c == 0) {
/* 416 */         this.myIndex = index;
/* 417 */         return c;
/*     */       } }
/* 419 */     while (c != to);
/* 420 */     back();
/* 421 */     return c;
/*     */   }
/*     */ 
/*     */   public void skipPast(String to)
/*     */   {
/* 431 */     this.myIndex = this.mySource.indexOf(to, this.myIndex);
/* 432 */     if (this.myIndex < 0)
/* 433 */       this.myIndex = this.mySource.length();
/*     */     else
/* 435 */       this.myIndex += to.length();
/*     */   }
/*     */ 
/*     */   public ParseException syntaxError(String message)
/*     */   {
/* 447 */     return new ParseException(message + toString(), this.myIndex);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 457 */     return " at character " + this.myIndex + " of " + this.mySource;
/*     */   }
/*     */ 
/*     */   public static String unescape(String s)
/*     */   {
/* 470 */     int len = s.length();
/* 471 */     StringBuffer b = new StringBuffer();
/* 472 */     for (int i = 0; i < len; i++) {
/* 473 */       char c = s.charAt(i);
/* 474 */       if (c == '+') {
/* 475 */         c = ' ';
/* 476 */       } else if ((c == '%') && (i + 2 < len)) {
/* 477 */         int d = dehexchar(s.charAt(i + 1));
/* 478 */         int e = dehexchar(s.charAt(i + 2));
/* 479 */         if ((d >= 0) && (e >= 0)) {
/* 480 */           c = (char)(d * 16 + e);
/* 481 */           i += 2;
/*     */         }
/*     */       }
/* 484 */       b.append(c);
/*     */     }
/* 486 */     return b.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     org.json.JSONTokener
 * JD-Core Version:    0.6.0
 */