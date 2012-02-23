/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ 
/*     */ class PorterStemmer
/*     */ {
/*     */   private char[] b;
/*     */   private int i;
/*     */   private int j;
/*     */   private int k;
/*     */   private int k0;
/*  68 */   private boolean dirty = false;
/*     */   private static final int INITIAL_SIZE = 50;
/*     */ 
/*     */   public PorterStemmer()
/*     */   {
/*  72 */     this.b = new char[50];
/*  73 */     this.i = 0;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  81 */     this.i = 0; this.dirty = false;
/*     */   }
/*     */ 
/*     */   public void add(char ch)
/*     */   {
/*  88 */     if (this.b.length <= this.i) {
/*  89 */       this.b = ArrayUtil.grow(this.b, this.i + 1);
/*     */     }
/*  91 */     this.b[(this.i++)] = ch;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 100 */     return new String(this.b, 0, this.i);
/*     */   }
/*     */ 
/*     */   public int getResultLength()
/*     */   {
/* 105 */     return this.i;
/*     */   }
/*     */ 
/*     */   public char[] getResultBuffer()
/*     */   {
/* 112 */     return this.b;
/*     */   }
/*     */ 
/*     */   private final boolean cons(int i)
/*     */   {
/* 117 */     switch (this.b[i]) { case 'a':
/*     */     case 'e':
/*     */     case 'i':
/*     */     case 'o':
/*     */     case 'u':
/* 119 */       return false;
/*     */     case 'y':
/* 121 */       return i == this.k0;
/*     */     }
/* 123 */     return true;
/*     */   }
/*     */ 
/*     */   private final int m()
/*     */   {
/* 139 */     int n = 0;
/* 140 */     int i = this.k0;
/*     */     while (true) {
/* 142 */       if (i > this.j)
/* 143 */         return n;
/* 144 */       if (!cons(i))
/*     */         break;
/* 146 */       i++;
/*     */     }
/* 148 */     i++;
/*     */     while (true)
/*     */     {
/* 151 */       if (i > this.j)
/* 152 */         return n;
/* 153 */       if (!cons(i))
/*     */       {
/* 155 */         i++; continue;
/*     */       }
/* 157 */       i++;
/* 158 */       n++;
/*     */       while (true) {
/* 160 */         if (i > this.j)
/* 161 */           return n;
/* 162 */         if (!cons(i))
/*     */           break;
/* 164 */         i++;
/*     */       }
/* 166 */       i++;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final boolean vowelinstem()
/*     */   {
/* 174 */     for (int i = this.k0; i <= this.j; i++)
/* 175 */       if (!cons(i))
/* 176 */         return true;
/* 177 */     return false;
/*     */   }
/*     */ 
/*     */   private final boolean doublec(int j)
/*     */   {
/* 183 */     if (j < this.k0 + 1)
/* 184 */       return false;
/* 185 */     if (this.b[j] != this.b[(j - 1)])
/* 186 */       return false;
/* 187 */     return cons(j);
/*     */   }
/*     */ 
/*     */   private final boolean cvc(int i)
/*     */   {
/* 200 */     if ((i < this.k0 + 2) || (!cons(i)) || (cons(i - 1)) || (!cons(i - 2))) {
/* 201 */       return false;
/*     */     }
/* 203 */     int ch = this.b[i];
/* 204 */     return (ch != 119) && (ch != 120) && (ch != 121);
/*     */   }
/*     */ 
/*     */   private final boolean ends(String s)
/*     */   {
/* 210 */     int l = s.length();
/* 211 */     int o = this.k - l + 1;
/* 212 */     if (o < this.k0)
/* 213 */       return false;
/* 214 */     for (int i = 0; i < l; i++)
/* 215 */       if (this.b[(o + i)] != s.charAt(i))
/* 216 */         return false;
/* 217 */     this.j = (this.k - l);
/* 218 */     return true;
/*     */   }
/*     */ 
/*     */   void setto(String s)
/*     */   {
/* 225 */     int l = s.length();
/* 226 */     int o = this.j + 1;
/* 227 */     for (int i = 0; i < l; i++)
/* 228 */       this.b[(o + i)] = s.charAt(i);
/* 229 */     this.k = (this.j + l);
/* 230 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   void r(String s)
/*     */   {
/* 235 */     if (m() > 0) setto(s);
/*     */   }
/*     */ 
/*     */   private final void step1()
/*     */   {
/* 260 */     if (this.b[this.k] == 's') {
/* 261 */       if (ends("sses")) this.k -= 2;
/* 262 */       else if (ends("ies")) setto("i");
/* 263 */       else if (this.b[(this.k - 1)] != 's') this.k -= 1;
/*     */     }
/* 265 */     if (ends("eed")) {
/* 266 */       if (m() > 0)
/* 267 */         this.k -= 1;
/*     */     }
/* 269 */     else if (((ends("ed")) || (ends("ing"))) && (vowelinstem())) {
/* 270 */       this.k = this.j;
/* 271 */       if (ends("at")) { setto("ate");
/* 272 */       } else if (ends("bl")) { setto("ble");
/* 273 */       } else if (ends("iz")) { setto("ize");
/* 274 */       } else if (doublec(this.k)) {
/* 275 */         int ch = this.b[(this.k--)];
/* 276 */         if ((ch == 108) || (ch == 115) || (ch == 122))
/* 277 */           this.k += 1;
/*     */       }
/* 279 */       else if ((m() == 1) && (cvc(this.k))) {
/* 280 */         setto("e");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void step2()
/*     */   {
/* 287 */     if ((ends("y")) && (vowelinstem())) {
/* 288 */       this.b[this.k] = 'i';
/* 289 */       this.dirty = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void step3()
/*     */   {
/* 298 */     if (this.k == this.k0) return;
/* 299 */     switch (this.b[(this.k - 1)]) {
/*     */     case 'a':
/* 301 */       if (ends("ational")) { r("ate"); } else {
/* 302 */         if (!ends("tional")) break; r("tion"); } break;
/*     */     case 'c':
/* 305 */       if (ends("enci")) { r("ence"); } else {
/* 306 */         if (!ends("anci")) break; r("ance"); } break;
/*     */     case 'e':
/* 309 */       if (!ends("izer")) break; r("ize"); break;
/*     */     case 'l':
/* 312 */       if (ends("bli")) { r("ble");
/* 313 */       } else if (ends("alli")) { r("al");
/* 314 */       } else if (ends("entli")) { r("ent");
/* 315 */       } else if (ends("eli")) { r("e"); } else {
/* 316 */         if (!ends("ousli")) break; r("ous"); } break;
/*     */     case 'o':
/* 319 */       if (ends("ization")) { r("ize");
/* 320 */       } else if (ends("ation")) { r("ate"); } else {
/* 321 */         if (!ends("ator")) break; r("ate"); } break;
/*     */     case 's':
/* 324 */       if (ends("alism")) { r("al");
/* 325 */       } else if (ends("iveness")) { r("ive");
/* 326 */       } else if (ends("fulness")) { r("ful"); } else {
/* 327 */         if (!ends("ousness")) break; r("ous"); } break;
/*     */     case 't':
/* 330 */       if (ends("aliti")) { r("al");
/* 331 */       } else if (ends("iviti")) { r("ive"); } else {
/* 332 */         if (!ends("biliti")) break; r("ble"); } break;
/*     */     case 'g':
/* 335 */       if (!ends("logi")) break; r("log");
/*     */     case 'b':
/*     */     case 'd':
/*     */     case 'f':
/*     */     case 'h':
/*     */     case 'i':
/*     */     case 'j':
/*     */     case 'k':
/*     */     case 'm':
/*     */     case 'n':
/*     */     case 'p':
/*     */     case 'q':
/*     */     case 'r': }  } 
/* 342 */   private final void step4() { switch (this.b[this.k]) {
/*     */     case 'e':
/* 344 */       if (ends("icate")) { r("ic");
/* 345 */       } else if (ends("ative")) { r(""); } else {
/* 346 */         if (!ends("alize")) break; r("al"); } break;
/*     */     case 'i':
/* 349 */       if (!ends("iciti")) break; r("ic"); break;
/*     */     case 'l':
/* 352 */       if (ends("ical")) { r("ic"); } else {
/* 353 */         if (!ends("ful")) break; r(""); } break;
/*     */     case 's':
/* 356 */       if (!ends("ness")) break; r("");
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void step5()
/*     */   {
/* 364 */     if (this.k == this.k0) return;
/* 365 */     switch (this.b[(this.k - 1)]) {
/*     */     case 'a':
/* 367 */       if (ends("al")) break;
/* 368 */       return;
/*     */     case 'c':
/* 370 */       if ((ends("ance")) || 
/* 371 */         (ends("ence"))) break;
/* 372 */       return;
/*     */     case 'e':
/* 374 */       if (ends("er")) break; return;
/*     */     case 'i':
/* 376 */       if (ends("ic")) break; return;
/*     */     case 'l':
/* 378 */       if ((ends("able")) || 
/* 379 */         (ends("ible"))) break; return;
/*     */     case 'n':
/* 381 */       if ((ends("ant")) || 
/* 382 */         (ends("ement")) || 
/* 383 */         (ends("ment")))
/*     */         break;
/* 385 */       if (ends("ent")) break;
/* 386 */       return;
/*     */     case 'o':
/* 388 */       if ((ends("ion")) && (this.j >= 0) && ((this.b[this.j] == 's') || (this.b[this.j] == 't')))
/*     */         break;
/* 390 */       if (ends("ou")) break;
/* 391 */       return;
/*     */     case 's':
/* 394 */       if (ends("ism")) break;
/* 395 */       return;
/*     */     case 't':
/* 397 */       if ((ends("ate")) || 
/* 398 */         (ends("iti"))) break;
/* 399 */       return;
/*     */     case 'u':
/* 401 */       if (ends("ous")) break;
/* 402 */       return;
/*     */     case 'v':
/* 404 */       if (ends("ive")) break;
/* 405 */       return;
/*     */     case 'z':
/* 407 */       if (ends("ize")) break;
/* 408 */       return;
/*     */     case 'b':
/*     */     case 'd':
/*     */     case 'f':
/*     */     case 'g':
/*     */     case 'h':
/*     */     case 'j':
/*     */     case 'k':
/*     */     case 'm':
/*     */     case 'p':
/*     */     case 'q':
/*     */     case 'r':
/*     */     case 'w':
/*     */     case 'x':
/*     */     case 'y':
/*     */     default:
/* 410 */       return;
/*     */     }
/* 412 */     if (m() > 1)
/* 413 */       this.k = this.j;
/*     */   }
/*     */ 
/*     */   private final void step6()
/*     */   {
/* 419 */     this.j = this.k;
/* 420 */     if (this.b[this.k] == 'e') {
/* 421 */       int a = m();
/* 422 */       if ((a > 1) || ((a == 1) && (!cvc(this.k - 1))))
/* 423 */         this.k -= 1;
/*     */     }
/* 425 */     if ((this.b[this.k] == 'l') && (doublec(this.k)) && (m() > 1))
/* 426 */       this.k -= 1;
/*     */   }
/*     */ 
/*     */   public String stem(String s)
/*     */   {
/* 434 */     if (stem(s.toCharArray(), s.length())) {
/* 435 */       return toString();
/*     */     }
/* 437 */     return s;
/*     */   }
/*     */ 
/*     */   public boolean stem(char[] word)
/*     */   {
/* 445 */     return stem(word, word.length);
/*     */   }
/*     */ 
/*     */   public boolean stem(char[] wordBuffer, int offset, int wordLen)
/*     */   {
/* 454 */     reset();
/* 455 */     if (this.b.length < wordLen) {
/* 456 */       this.b = new char[ArrayUtil.oversize(wordLen, 2)];
/*     */     }
/* 458 */     System.arraycopy(wordBuffer, offset, this.b, 0, wordLen);
/* 459 */     this.i = wordLen;
/* 460 */     return stem(0);
/*     */   }
/*     */ 
/*     */   public boolean stem(char[] word, int wordLen)
/*     */   {
/* 469 */     return stem(word, 0, wordLen);
/*     */   }
/*     */ 
/*     */   public boolean stem()
/*     */   {
/* 478 */     return stem(0);
/*     */   }
/*     */ 
/*     */   public boolean stem(int i0) {
/* 482 */     this.k = (this.i - 1);
/* 483 */     this.k0 = i0;
/* 484 */     if (this.k > this.k0 + 1) {
/* 485 */       step1(); step2(); step3(); step4(); step5(); step6();
/*     */     }
/*     */ 
/* 489 */     if (this.i != this.k + 1)
/* 490 */       this.dirty = true;
/* 491 */     this.i = (this.k + 1);
/* 492 */     return this.dirty;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 500 */     PorterStemmer s = new PorterStemmer();
/*     */ 
/* 502 */     for (int i = 0; i < args.length; i++)
/*     */       try {
/* 504 */         InputStream in = new FileInputStream(args[i]);
/* 505 */         byte[] buffer = new byte[1024];
/*     */ 
/* 508 */         int bufferLen = in.read(buffer);
/* 509 */         int offset = 0;
/* 510 */         s.reset();
/*     */         while (true)
/*     */         {
/*     */           int ch;
/*     */           int ch;
/* 513 */           if (offset < bufferLen) {
/* 514 */             ch = buffer[(offset++)];
/*     */           } else {
/* 516 */             bufferLen = in.read(buffer);
/* 517 */             offset = 0;
/*     */             int ch;
/* 518 */             if (bufferLen < 0)
/* 519 */               ch = -1;
/*     */             else {
/* 521 */               ch = buffer[(offset++)];
/*     */             }
/*     */           }
/* 524 */           if (Character.isLetter((char)ch)) {
/* 525 */             s.add(Character.toLowerCase((char)ch)); continue;
/*     */           }
/*     */ 
/* 528 */           s.stem();
/* 529 */           System.out.print(s.toString());
/* 530 */           s.reset();
/* 531 */           if (ch < 0) {
/*     */             break;
/*     */           }
/* 534 */           System.out.print((char)ch);
/*     */         }
/*     */ 
/* 539 */         in.close();
/*     */       }
/*     */       catch (IOException e) {
/* 542 */         System.out.println("error reading " + args[i]);
/*     */       }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.PorterStemmer
 * JD-Core Version:    0.6.0
 */