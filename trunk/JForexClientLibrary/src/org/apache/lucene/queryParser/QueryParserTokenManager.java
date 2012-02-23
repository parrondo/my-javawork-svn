/*      */ package org.apache.lucene.queryParser;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ 
/*      */ public class QueryParserTokenManager
/*      */   implements QueryParserConstants
/*      */ {
/*   42 */   public PrintStream debugStream = System.out;
/*      */ 
/*   97 */   static final long[] jjbitVec0 = { 1L, 0L, 0L, 0L };
/*      */ 
/*  100 */   static final long[] jjbitVec1 = { -2L, -1L, -1L, -1L };
/*      */ 
/*  103 */   static final long[] jjbitVec3 = { 0L, 0L, -1L, -1L };
/*      */ 
/*  106 */   static final long[] jjbitVec4 = { -281474976710658L, -1L, -1L, -1L };
/*      */ 
/* 1005 */   static final int[] jjnextStates = { 15, 16, 18, 29, 32, 23, 33, 30, 20, 21, 32, 23, 33, 31, 34, 27, 2, 4, 5, 0, 1 };
/*      */ 
/* 1047 */   public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, null, null, null, null, null, "+", "-", "(", ")", ":", "*", "^", null, null, null, null, null, "[", "{", null, "TO", "]", null, null, "TO", "}", null, null };
/*      */ 
/* 1053 */   public static final String[] lexStateNames = { "Boost", "RangeEx", "RangeIn", "DEFAULT" };
/*      */ 
/* 1061 */   public static final int[] jjnewLexState = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 2, 1, 3, -1, 3, -1, -1, -1, 3, -1, -1 };
/*      */ 
/* 1065 */   static final long[] jjtoToken = { 17179868929L };
/*      */ 
/* 1068 */   static final long[] jjtoSkip = { 128L };
/*      */   protected CharStream input_stream;
/* 1072 */   private final int[] jjrounds = new int[36];
/* 1073 */   private final int[] jjstateSet = new int[72];
/*      */   protected char curChar;
/* 1142 */   int curLexState = 3;
/* 1143 */   int defaultLexState = 3;
/*      */   int jjnewStateCnt;
/*      */   int jjround;
/*      */   int jjmatchedPos;
/*      */   int jjmatchedKind;
/*      */ 
/*      */   public void setDebugStream(PrintStream ds)
/*      */   {
/*   44 */     this.debugStream = ds;
/*      */   }
/*      */   private final int jjStopStringLiteralDfa_3(int pos, long active0) {
/*   47 */     switch (pos)
/*      */     {
/*      */     }
/*   50 */     return -1;
/*      */   }
/*      */ 
/*      */   private final int jjStartNfa_3(int pos, long active0)
/*      */   {
/*   55 */     return jjMoveNfa_3(jjStopStringLiteralDfa_3(pos, active0), pos + 1);
/*      */   }
/*      */ 
/*      */   private int jjStopAtPos(int pos, int kind) {
/*   59 */     this.jjmatchedKind = kind;
/*   60 */     this.jjmatchedPos = pos;
/*   61 */     return pos + 1;
/*      */   }
/*      */ 
/*      */   private int jjMoveStringLiteralDfa0_3() {
/*   65 */     switch (this.curChar)
/*      */     {
/*      */     case '(':
/*   68 */       return jjStopAtPos(0, 13);
/*      */     case ')':
/*   70 */       return jjStopAtPos(0, 14);
/*      */     case '*':
/*   72 */       return jjStartNfaWithStates_3(0, 16, 36);
/*      */     case '+':
/*   74 */       return jjStopAtPos(0, 11);
/*      */     case '-':
/*   76 */       return jjStopAtPos(0, 12);
/*      */     case ':':
/*   78 */       return jjStopAtPos(0, 15);
/*      */     case '[':
/*   80 */       return jjStopAtPos(0, 23);
/*      */     case '^':
/*   82 */       return jjStopAtPos(0, 17);
/*      */     case '{':
/*   84 */       return jjStopAtPos(0, 24);
/*      */     }
/*   86 */     return jjMoveNfa_3(0, 0);
/*      */   }
/*      */ 
/*      */   private int jjStartNfaWithStates_3(int pos, int kind, int state)
/*      */   {
/*   91 */     this.jjmatchedKind = kind;
/*   92 */     this.jjmatchedPos = pos;
/*      */     try { this.curChar = this.input_stream.readChar(); } catch (IOException e) {
/*   94 */       return pos + 1;
/*   95 */     }return jjMoveNfa_3(state, pos + 1);
/*      */   }
/*      */ 
/*      */   private int jjMoveNfa_3(int startState, int curPos)
/*      */   {
/*  111 */     int startsAt = 0;
/*  112 */     this.jjnewStateCnt = 36;
/*  113 */     int i = 1;
/*  114 */     this.jjstateSet[0] = startState;
/*  115 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/*  118 */       if (++this.jjround == 2147483647)
/*  119 */         ReInitRounds();
/*  120 */       if (this.curChar < '@')
/*      */       {
/*  122 */         long l = 1L << this.curChar;
/*      */         do
/*      */         {
/*  125 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           case 25:
/*      */           case 36:
/*  129 */             if ((0xFFFFD9FF & l) == 0L)
/*      */               continue;
/*  131 */             if (kind > 22)
/*  132 */               kind = 22;
/*  133 */             jjCheckNAddTwoStates(25, 26);
/*  134 */             break;
/*      */           case 0:
/*  136 */             if ((0xFFFFD9FF & l) != 0L)
/*      */             {
/*  138 */               if (kind > 22)
/*  139 */                 kind = 22;
/*  140 */               jjCheckNAddTwoStates(25, 26);
/*      */             }
/*  142 */             else if ((0x2600 & l) != 0L)
/*      */             {
/*  144 */               if (kind > 7)
/*  145 */                 kind = 7;
/*      */             }
/*  147 */             else if (this.curChar == '"') {
/*  148 */               jjCheckNAddStates(0, 2);
/*  149 */             } else if (this.curChar == '!')
/*      */             {
/*  151 */               if (kind > 10)
/*  152 */                 kind = 10;
/*      */             }
/*  154 */             if ((0xFFFFD9FF & l) != 0L)
/*      */             {
/*  156 */               if (kind > 19)
/*  157 */                 kind = 19;
/*  158 */               jjCheckNAddStates(3, 7);
/*      */             }
/*  160 */             else if (this.curChar == '*')
/*      */             {
/*  162 */               if (kind > 21)
/*  163 */                 kind = 21;
/*      */             }
/*  165 */             if (this.curChar != '&') continue;
/*  166 */             this.jjstateSet[(this.jjnewStateCnt++)] = 4; break;
/*      */           case 4:
/*  169 */             if ((this.curChar != '&') || (kind <= 8)) continue;
/*  170 */             kind = 8; break;
/*      */           case 5:
/*  173 */             if (this.curChar != '&') continue;
/*  174 */             this.jjstateSet[(this.jjnewStateCnt++)] = 4; break;
/*      */           case 13:
/*  177 */             if ((this.curChar != '!') || (kind <= 10)) continue;
/*  178 */             kind = 10; break;
/*      */           case 14:
/*  181 */             if (this.curChar != '"') continue;
/*  182 */             jjCheckNAddStates(0, 2); break;
/*      */           case 15:
/*  185 */             if ((0xFFFFFFFF & l) == 0L) continue;
/*  186 */             jjCheckNAddStates(0, 2); break;
/*      */           case 17:
/*  189 */             jjCheckNAddStates(0, 2);
/*  190 */             break;
/*      */           case 18:
/*  192 */             if ((this.curChar != '"') || (kind <= 18)) continue;
/*  193 */             kind = 18; break;
/*      */           case 20:
/*  196 */             if ((0x0 & l) == 0L)
/*      */               continue;
/*  198 */             if (kind > 20)
/*  199 */               kind = 20;
/*  200 */             jjAddStates(8, 9);
/*  201 */             break;
/*      */           case 21:
/*  203 */             if (this.curChar != '.') continue;
/*  204 */             jjCheckNAdd(22); break;
/*      */           case 22:
/*  207 */             if ((0x0 & l) == 0L)
/*      */               continue;
/*  209 */             if (kind > 20)
/*  210 */               kind = 20;
/*  211 */             jjCheckNAdd(22);
/*  212 */             break;
/*      */           case 23:
/*  214 */             if ((this.curChar != '*') || (kind <= 21)) continue;
/*  215 */             kind = 21; break;
/*      */           case 24:
/*  218 */             if ((0xFFFFD9FF & l) == 0L)
/*      */               continue;
/*  220 */             if (kind > 22)
/*  221 */               kind = 22;
/*  222 */             jjCheckNAddTwoStates(25, 26);
/*  223 */             break;
/*      */           case 27:
/*  225 */             if (kind > 22)
/*  226 */               kind = 22;
/*  227 */             jjCheckNAddTwoStates(25, 26);
/*  228 */             break;
/*      */           case 28:
/*  230 */             if ((0xFFFFD9FF & l) == 0L)
/*      */               continue;
/*  232 */             if (kind > 19)
/*  233 */               kind = 19;
/*  234 */             jjCheckNAddStates(3, 7);
/*  235 */             break;
/*      */           case 29:
/*  237 */             if ((0xFFFFD9FF & l) == 0L)
/*      */               continue;
/*  239 */             if (kind > 19)
/*  240 */               kind = 19;
/*  241 */             jjCheckNAddTwoStates(29, 30);
/*  242 */             break;
/*      */           case 31:
/*  244 */             if (kind > 19)
/*  245 */               kind = 19;
/*  246 */             jjCheckNAddTwoStates(29, 30);
/*  247 */             break;
/*      */           case 32:
/*  249 */             if ((0xFFFFD9FF & l) == 0L) continue;
/*  250 */             jjCheckNAddStates(10, 12); break;
/*      */           case 34:
/*  253 */             jjCheckNAddStates(10, 12);
/*      */           case 1:
/*      */           case 2:
/*      */           case 3:
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/*      */           case 9:
/*      */           case 10:
/*      */           case 11:
/*      */           case 12:
/*      */           case 16:
/*      */           case 19:
/*      */           case 26:
/*      */           case 30:
/*      */           case 33:
/*  257 */           case 35: }  } while (i != startsAt);
/*      */       }
/*  259 */       else if (this.curChar < '')
/*      */       {
/*  261 */         long l = 1L << (this.curChar & 0x3F);
/*      */         do
/*      */         {
/*  264 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           case 36:
/*  267 */             if ((0x87FFFFFF & l) != 0L)
/*      */             {
/*  269 */               if (kind > 22)
/*  270 */                 kind = 22;
/*  271 */               jjCheckNAddTwoStates(25, 26);
/*      */             } else {
/*  273 */               if (this.curChar != '\\') continue;
/*  274 */               jjCheckNAddTwoStates(27, 27); } break;
/*      */           case 0:
/*  277 */             if ((0x87FFFFFF & l) != 0L)
/*      */             {
/*  279 */               if (kind > 19)
/*  280 */                 kind = 19;
/*  281 */               jjCheckNAddStates(3, 7);
/*      */             }
/*  283 */             else if (this.curChar == '\\') {
/*  284 */               jjCheckNAddStates(13, 15);
/*  285 */             } else if (this.curChar == '~')
/*      */             {
/*  287 */               if (kind > 20)
/*  288 */                 kind = 20;
/*  289 */               this.jjstateSet[(this.jjnewStateCnt++)] = 20;
/*      */             }
/*  291 */             if ((0x87FFFFFF & l) != 0L)
/*      */             {
/*  293 */               if (kind > 22)
/*  294 */                 kind = 22;
/*  295 */               jjCheckNAddTwoStates(25, 26);
/*      */             }
/*  297 */             if (this.curChar == 'N') {
/*  298 */               this.jjstateSet[(this.jjnewStateCnt++)] = 11;
/*  299 */             } else if (this.curChar == '|') {
/*  300 */               this.jjstateSet[(this.jjnewStateCnt++)] = 8;
/*  301 */             } else if (this.curChar == 'O') {
/*  302 */               this.jjstateSet[(this.jjnewStateCnt++)] = 6; } else {
/*  303 */               if (this.curChar != 'A') continue;
/*  304 */               this.jjstateSet[(this.jjnewStateCnt++)] = 2; } break;
/*      */           case 1:
/*  307 */             if ((this.curChar != 'D') || (kind <= 8)) continue;
/*  308 */             kind = 8; break;
/*      */           case 2:
/*  311 */             if (this.curChar != 'N') continue;
/*  312 */             this.jjstateSet[(this.jjnewStateCnt++)] = 1; break;
/*      */           case 3:
/*  315 */             if (this.curChar != 'A') continue;
/*  316 */             this.jjstateSet[(this.jjnewStateCnt++)] = 2; break;
/*      */           case 6:
/*  319 */             if ((this.curChar != 'R') || (kind <= 9)) continue;
/*  320 */             kind = 9; break;
/*      */           case 7:
/*  323 */             if (this.curChar != 'O') continue;
/*  324 */             this.jjstateSet[(this.jjnewStateCnt++)] = 6; break;
/*      */           case 8:
/*  327 */             if ((this.curChar != '|') || (kind <= 9)) continue;
/*  328 */             kind = 9; break;
/*      */           case 9:
/*  331 */             if (this.curChar != '|') continue;
/*  332 */             this.jjstateSet[(this.jjnewStateCnt++)] = 8; break;
/*      */           case 10:
/*  335 */             if ((this.curChar != 'T') || (kind <= 10)) continue;
/*  336 */             kind = 10; break;
/*      */           case 11:
/*  339 */             if (this.curChar != 'O') continue;
/*  340 */             this.jjstateSet[(this.jjnewStateCnt++)] = 10; break;
/*      */           case 12:
/*  343 */             if (this.curChar != 'N') continue;
/*  344 */             this.jjstateSet[(this.jjnewStateCnt++)] = 11; break;
/*      */           case 15:
/*  347 */             if ((0xEFFFFFFF & l) == 0L) continue;
/*  348 */             jjCheckNAddStates(0, 2); break;
/*      */           case 16:
/*  351 */             if (this.curChar != '\\') continue;
/*  352 */             this.jjstateSet[(this.jjnewStateCnt++)] = 17; break;
/*      */           case 17:
/*  355 */             jjCheckNAddStates(0, 2);
/*  356 */             break;
/*      */           case 19:
/*  358 */             if (this.curChar != '~')
/*      */               continue;
/*  360 */             if (kind > 20)
/*  361 */               kind = 20;
/*  362 */             this.jjstateSet[(this.jjnewStateCnt++)] = 20;
/*  363 */             break;
/*      */           case 24:
/*  365 */             if ((0x87FFFFFF & l) == 0L)
/*      */               continue;
/*  367 */             if (kind > 22)
/*  368 */               kind = 22;
/*  369 */             jjCheckNAddTwoStates(25, 26);
/*  370 */             break;
/*      */           case 25:
/*  372 */             if ((0x87FFFFFF & l) == 0L)
/*      */               continue;
/*  374 */             if (kind > 22)
/*  375 */               kind = 22;
/*  376 */             jjCheckNAddTwoStates(25, 26);
/*  377 */             break;
/*      */           case 26:
/*  379 */             if (this.curChar != '\\') continue;
/*  380 */             jjCheckNAddTwoStates(27, 27); break;
/*      */           case 27:
/*  383 */             if (kind > 22)
/*  384 */               kind = 22;
/*  385 */             jjCheckNAddTwoStates(25, 26);
/*  386 */             break;
/*      */           case 28:
/*  388 */             if ((0x87FFFFFF & l) == 0L)
/*      */               continue;
/*  390 */             if (kind > 19)
/*  391 */               kind = 19;
/*  392 */             jjCheckNAddStates(3, 7);
/*  393 */             break;
/*      */           case 29:
/*  395 */             if ((0x87FFFFFF & l) == 0L)
/*      */               continue;
/*  397 */             if (kind > 19)
/*  398 */               kind = 19;
/*  399 */             jjCheckNAddTwoStates(29, 30);
/*  400 */             break;
/*      */           case 30:
/*  402 */             if (this.curChar != '\\') continue;
/*  403 */             jjCheckNAddTwoStates(31, 31); break;
/*      */           case 31:
/*  406 */             if (kind > 19)
/*  407 */               kind = 19;
/*  408 */             jjCheckNAddTwoStates(29, 30);
/*  409 */             break;
/*      */           case 32:
/*  411 */             if ((0x87FFFFFF & l) == 0L) continue;
/*  412 */             jjCheckNAddStates(10, 12); break;
/*      */           case 33:
/*  415 */             if (this.curChar != '\\') continue;
/*  416 */             jjCheckNAddTwoStates(34, 34); break;
/*      */           case 34:
/*  419 */             jjCheckNAddStates(10, 12);
/*  420 */             break;
/*      */           case 35:
/*  422 */             if (this.curChar != '\\') continue;
/*  423 */             jjCheckNAddStates(13, 15);
/*      */           case 4:
/*      */           case 5:
/*      */           case 13:
/*      */           case 14:
/*      */           case 18:
/*      */           case 20:
/*      */           case 21:
/*      */           case 22:
/*  427 */           case 23: }  } while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/*  431 */         int hiByte = this.curChar >> '\b';
/*  432 */         int i1 = hiByte >> 6;
/*  433 */         long l1 = 1L << (hiByte & 0x3F);
/*  434 */         int i2 = (this.curChar & 0xFF) >> '\006';
/*  435 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         do
/*      */         {
/*  438 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           case 25:
/*      */           case 36:
/*  442 */             if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  444 */             if (kind > 22)
/*  445 */               kind = 22;
/*  446 */             jjCheckNAddTwoStates(25, 26);
/*  447 */             break;
/*      */           case 0:
/*  449 */             if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */             {
/*  451 */               if (kind > 7)
/*  452 */                 kind = 7;
/*      */             }
/*  454 */             if (jjCanMove_2(hiByte, i1, i2, l1, l2))
/*      */             {
/*  456 */               if (kind > 22)
/*  457 */                 kind = 22;
/*  458 */               jjCheckNAddTwoStates(25, 26);
/*      */             }
/*  460 */             if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  462 */             if (kind > 19)
/*  463 */               kind = 19;
/*  464 */             jjCheckNAddStates(3, 7); break;
/*      */           case 15:
/*      */           case 17:
/*  469 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2)) continue;
/*  470 */             jjCheckNAddStates(0, 2); break;
/*      */           case 24:
/*  473 */             if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  475 */             if (kind > 22)
/*  476 */               kind = 22;
/*  477 */             jjCheckNAddTwoStates(25, 26);
/*  478 */             break;
/*      */           case 27:
/*  480 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  482 */             if (kind > 22)
/*  483 */               kind = 22;
/*  484 */             jjCheckNAddTwoStates(25, 26);
/*  485 */             break;
/*      */           case 28:
/*  487 */             if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  489 */             if (kind > 19)
/*  490 */               kind = 19;
/*  491 */             jjCheckNAddStates(3, 7);
/*  492 */             break;
/*      */           case 29:
/*  494 */             if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  496 */             if (kind > 19)
/*  497 */               kind = 19;
/*  498 */             jjCheckNAddTwoStates(29, 30);
/*  499 */             break;
/*      */           case 31:
/*  501 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  503 */             if (kind > 19)
/*  504 */               kind = 19;
/*  505 */             jjCheckNAddTwoStates(29, 30);
/*  506 */             break;
/*      */           case 32:
/*  508 */             if (!jjCanMove_2(hiByte, i1, i2, l1, l2)) continue;
/*  509 */             jjCheckNAddStates(10, 12); break;
/*      */           case 34:
/*  512 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2)) continue;
/*  513 */             jjCheckNAddStates(10, 12);
/*      */           case 1:
/*      */           case 2:
/*      */           case 3:
/*      */           case 4:
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/*      */           case 9:
/*      */           case 10:
/*      */           case 11:
/*      */           case 12:
/*      */           case 13:
/*      */           case 14:
/*      */           case 16:
/*      */           case 18:
/*      */           case 19:
/*      */           case 20:
/*      */           case 21:
/*      */           case 22:
/*      */           case 23:
/*      */           case 26:
/*      */           case 30:
/*      */           case 33:
/*  517 */           case 35: }  } while (i != startsAt);
/*      */       }
/*  519 */       if (kind != 2147483647)
/*      */       {
/*  521 */         this.jjmatchedKind = kind;
/*  522 */         this.jjmatchedPos = curPos;
/*  523 */         kind = 2147483647;
/*      */       }
/*  525 */       curPos++;
/*  526 */       if ((i = this.jjnewStateCnt) == (startsAt = 36 - (this.jjnewStateCnt = startsAt)))
/*  527 */         return curPos; try {
/*  528 */         this.curChar = this.input_stream.readChar(); } catch (IOException e) {
/*      */       }
/*  529 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private final int jjStopStringLiteralDfa_1(int pos, long active0)
/*      */   {
/*  534 */     switch (pos)
/*      */     {
/*      */     case 0:
/*  537 */       if ((active0 & 0x40000000) != 0L)
/*      */       {
/*  539 */         this.jjmatchedKind = 33;
/*  540 */         return 6;
/*      */       }
/*  542 */       return -1;
/*      */     }
/*  544 */     return -1;
/*      */   }
/*      */ 
/*      */   private final int jjStartNfa_1(int pos, long active0)
/*      */   {
/*  549 */     return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
/*      */   }
/*      */ 
/*      */   private int jjMoveStringLiteralDfa0_1() {
/*  553 */     switch (this.curChar)
/*      */     {
/*      */     case 'T':
/*  556 */       return jjMoveStringLiteralDfa1_1(1073741824L);
/*      */     case '}':
/*  558 */       return jjStopAtPos(0, 31);
/*      */     }
/*  560 */     return jjMoveNfa_1(0, 0);
/*      */   }
/*      */ 
/*      */   private int jjMoveStringLiteralDfa1_1(long active0) {
/*      */     try {
/*  565 */       this.curChar = this.input_stream.readChar();
/*      */     } catch (IOException e) {
/*  567 */       jjStopStringLiteralDfa_1(0, active0);
/*  568 */       return 1;
/*      */     }
/*  570 */     switch (this.curChar)
/*      */     {
/*      */     case 'O':
/*  573 */       if ((active0 & 0x40000000) == 0L) break;
/*  574 */       return jjStartNfaWithStates_1(1, 30, 6);
/*      */     }
/*      */ 
/*  579 */     return jjStartNfa_1(0, active0);
/*      */   }
/*      */ 
/*      */   private int jjStartNfaWithStates_1(int pos, int kind, int state) {
/*  583 */     this.jjmatchedKind = kind;
/*  584 */     this.jjmatchedPos = pos;
/*      */     try { this.curChar = this.input_stream.readChar(); } catch (IOException e) {
/*  586 */       return pos + 1;
/*  587 */     }return jjMoveNfa_1(state, pos + 1);
/*      */   }
/*      */ 
/*      */   private int jjMoveNfa_1(int startState, int curPos) {
/*  591 */     int startsAt = 0;
/*  592 */     this.jjnewStateCnt = 7;
/*  593 */     int i = 1;
/*  594 */     this.jjstateSet[0] = startState;
/*  595 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/*  598 */       if (++this.jjround == 2147483647)
/*  599 */         ReInitRounds();
/*  600 */       if (this.curChar < '@')
/*      */       {
/*  602 */         long l = 1L << this.curChar;
/*      */         do
/*      */         {
/*  605 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           case 0:
/*  608 */             if ((0xFFFFFFFF & l) != 0L)
/*      */             {
/*  610 */               if (kind > 33)
/*  611 */                 kind = 33;
/*  612 */               jjCheckNAdd(6);
/*      */             }
/*  614 */             if ((0x2600 & l) != 0L)
/*      */             {
/*  616 */               if (kind <= 7) continue;
/*  617 */               kind = 7;
/*      */             } else {
/*  619 */               if (this.curChar != '"') continue;
/*  620 */               jjCheckNAddTwoStates(2, 4); } break;
/*      */           case 1:
/*  623 */             if (this.curChar != '"') continue;
/*  624 */             jjCheckNAddTwoStates(2, 4); break;
/*      */           case 2:
/*  627 */             if ((0xFFFFFFFF & l) == 0L) continue;
/*  628 */             jjCheckNAddStates(16, 18); break;
/*      */           case 3:
/*  631 */             if (this.curChar != '"') continue;
/*  632 */             jjCheckNAddStates(16, 18); break;
/*      */           case 5:
/*  635 */             if ((this.curChar != '"') || (kind <= 32)) continue;
/*  636 */             kind = 32; break;
/*      */           case 6:
/*  639 */             if ((0xFFFFFFFF & l) == 0L)
/*      */               continue;
/*  641 */             if (kind > 33)
/*  642 */               kind = 33;
/*  643 */             jjCheckNAdd(6);
/*      */           case 4:
/*      */           }
/*      */         }
/*  647 */         while (i != startsAt);
/*      */       }
/*  649 */       else if (this.curChar < '')
/*      */       {
/*  651 */         long l = 1L << (this.curChar & 0x3F);
/*      */         do
/*      */         {
/*  654 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           case 0:
/*      */           case 6:
/*  658 */             if ((0xFFFFFFFF & l) == 0L)
/*      */               continue;
/*  660 */             if (kind > 33)
/*  661 */               kind = 33;
/*  662 */             jjCheckNAdd(6);
/*  663 */             break;
/*      */           case 2:
/*  665 */             jjAddStates(16, 18);
/*  666 */             break;
/*      */           case 4:
/*  668 */             if (this.curChar != '\\') continue;
/*  669 */             this.jjstateSet[(this.jjnewStateCnt++)] = 3;
/*      */           case 1:
/*      */           case 3:
/*      */           case 5:
/*      */           }
/*  673 */         }while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/*  677 */         int hiByte = this.curChar >> '\b';
/*  678 */         int i1 = hiByte >> 6;
/*  679 */         long l1 = 1L << (hiByte & 0x3F);
/*  680 */         int i2 = (this.curChar & 0xFF) >> '\006';
/*  681 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         do
/*      */         {
/*  684 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           case 0:
/*  687 */             if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */             {
/*  689 */               if (kind > 7)
/*  690 */                 kind = 7;
/*      */             }
/*  692 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  694 */             if (kind > 33)
/*  695 */               kind = 33;
/*  696 */             jjCheckNAdd(6); break;
/*      */           case 2:
/*  700 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2)) continue;
/*  701 */             jjAddStates(16, 18); break;
/*      */           case 6:
/*  704 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  706 */             if (kind > 33)
/*  707 */               kind = 33;
/*  708 */             jjCheckNAdd(6);
/*      */           }
/*      */         }
/*      */ 
/*  712 */         while (i != startsAt);
/*      */       }
/*  714 */       if (kind != 2147483647)
/*      */       {
/*  716 */         this.jjmatchedKind = kind;
/*  717 */         this.jjmatchedPos = curPos;
/*  718 */         kind = 2147483647;
/*      */       }
/*  720 */       curPos++;
/*  721 */       if ((i = this.jjnewStateCnt) == (startsAt = 7 - (this.jjnewStateCnt = startsAt)))
/*  722 */         return curPos; try {
/*  723 */         this.curChar = this.input_stream.readChar(); } catch (IOException e) {
/*      */       }
/*  724 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private int jjMoveStringLiteralDfa0_0()
/*      */   {
/*  729 */     return jjMoveNfa_0(0, 0);
/*      */   }
/*      */ 
/*      */   private int jjMoveNfa_0(int startState, int curPos) {
/*  733 */     int startsAt = 0;
/*  734 */     this.jjnewStateCnt = 3;
/*  735 */     int i = 1;
/*  736 */     this.jjstateSet[0] = startState;
/*  737 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/*  740 */       if (++this.jjround == 2147483647)
/*  741 */         ReInitRounds();
/*  742 */       if (this.curChar < '@')
/*      */       {
/*  744 */         long l = 1L << this.curChar;
/*      */         do
/*      */         {
/*  747 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           case 0:
/*  750 */             if ((0x0 & l) == 0L)
/*      */               continue;
/*  752 */             if (kind > 25)
/*  753 */               kind = 25;
/*  754 */             jjAddStates(19, 20);
/*  755 */             break;
/*      */           case 1:
/*  757 */             if (this.curChar != '.') continue;
/*  758 */             jjCheckNAdd(2); break;
/*      */           case 2:
/*  761 */             if ((0x0 & l) == 0L)
/*      */               continue;
/*  763 */             if (kind > 25)
/*  764 */               kind = 25;
/*  765 */             jjCheckNAdd(2);
/*      */           }
/*      */         }
/*      */ 
/*  769 */         while (i != startsAt);
/*      */       }
/*  771 */       else if (this.curChar < '')
/*      */       {
/*  773 */         long l = 1L << (this.curChar & 0x3F);
/*      */         do
/*      */         {
/*  776 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           }
/*      */         }
/*  780 */         while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/*  784 */         int hiByte = this.curChar >> '\b';
/*  785 */         int i1 = hiByte >> 6;
/*  786 */         long l1 = 1L << (hiByte & 0x3F);
/*  787 */         int i2 = (this.curChar & 0xFF) >> '\006';
/*  788 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         do
/*      */         {
/*  791 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           }
/*      */         }
/*  795 */         while (i != startsAt);
/*      */       }
/*  797 */       if (kind != 2147483647)
/*      */       {
/*  799 */         this.jjmatchedKind = kind;
/*  800 */         this.jjmatchedPos = curPos;
/*  801 */         kind = 2147483647;
/*      */       }
/*  803 */       curPos++;
/*  804 */       if ((i = this.jjnewStateCnt) == (startsAt = 3 - (this.jjnewStateCnt = startsAt)))
/*  805 */         return curPos; try {
/*  806 */         this.curChar = this.input_stream.readChar(); } catch (IOException e) {
/*      */       }
/*  807 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private final int jjStopStringLiteralDfa_2(int pos, long active0)
/*      */   {
/*  812 */     switch (pos)
/*      */     {
/*      */     case 0:
/*  815 */       if ((active0 & 0x4000000) != 0L)
/*      */       {
/*  817 */         this.jjmatchedKind = 29;
/*  818 */         return 6;
/*      */       }
/*  820 */       return -1;
/*      */     }
/*  822 */     return -1;
/*      */   }
/*      */ 
/*      */   private final int jjStartNfa_2(int pos, long active0)
/*      */   {
/*  827 */     return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0), pos + 1);
/*      */   }
/*      */ 
/*      */   private int jjMoveStringLiteralDfa0_2() {
/*  831 */     switch (this.curChar)
/*      */     {
/*      */     case 'T':
/*  834 */       return jjMoveStringLiteralDfa1_2(67108864L);
/*      */     case ']':
/*  836 */       return jjStopAtPos(0, 27);
/*      */     }
/*  838 */     return jjMoveNfa_2(0, 0);
/*      */   }
/*      */ 
/*      */   private int jjMoveStringLiteralDfa1_2(long active0) {
/*      */     try {
/*  843 */       this.curChar = this.input_stream.readChar();
/*      */     } catch (IOException e) {
/*  845 */       jjStopStringLiteralDfa_2(0, active0);
/*  846 */       return 1;
/*      */     }
/*  848 */     switch (this.curChar)
/*      */     {
/*      */     case 'O':
/*  851 */       if ((active0 & 0x4000000) == 0L) break;
/*  852 */       return jjStartNfaWithStates_2(1, 26, 6);
/*      */     }
/*      */ 
/*  857 */     return jjStartNfa_2(0, active0);
/*      */   }
/*      */ 
/*      */   private int jjStartNfaWithStates_2(int pos, int kind, int state) {
/*  861 */     this.jjmatchedKind = kind;
/*  862 */     this.jjmatchedPos = pos;
/*      */     try { this.curChar = this.input_stream.readChar(); } catch (IOException e) {
/*  864 */       return pos + 1;
/*  865 */     }return jjMoveNfa_2(state, pos + 1);
/*      */   }
/*      */ 
/*      */   private int jjMoveNfa_2(int startState, int curPos) {
/*  869 */     int startsAt = 0;
/*  870 */     this.jjnewStateCnt = 7;
/*  871 */     int i = 1;
/*  872 */     this.jjstateSet[0] = startState;
/*  873 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/*  876 */       if (++this.jjround == 2147483647)
/*  877 */         ReInitRounds();
/*  878 */       if (this.curChar < '@')
/*      */       {
/*  880 */         long l = 1L << this.curChar;
/*      */         do
/*      */         {
/*  883 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           case 0:
/*  886 */             if ((0xFFFFFFFF & l) != 0L)
/*      */             {
/*  888 */               if (kind > 29)
/*  889 */                 kind = 29;
/*  890 */               jjCheckNAdd(6);
/*      */             }
/*  892 */             if ((0x2600 & l) != 0L)
/*      */             {
/*  894 */               if (kind <= 7) continue;
/*  895 */               kind = 7;
/*      */             } else {
/*  897 */               if (this.curChar != '"') continue;
/*  898 */               jjCheckNAddTwoStates(2, 4); } break;
/*      */           case 1:
/*  901 */             if (this.curChar != '"') continue;
/*  902 */             jjCheckNAddTwoStates(2, 4); break;
/*      */           case 2:
/*  905 */             if ((0xFFFFFFFF & l) == 0L) continue;
/*  906 */             jjCheckNAddStates(16, 18); break;
/*      */           case 3:
/*  909 */             if (this.curChar != '"') continue;
/*  910 */             jjCheckNAddStates(16, 18); break;
/*      */           case 5:
/*  913 */             if ((this.curChar != '"') || (kind <= 28)) continue;
/*  914 */             kind = 28; break;
/*      */           case 6:
/*  917 */             if ((0xFFFFFFFF & l) == 0L)
/*      */               continue;
/*  919 */             if (kind > 29)
/*  920 */               kind = 29;
/*  921 */             jjCheckNAdd(6);
/*      */           case 4:
/*      */           }
/*      */         }
/*  925 */         while (i != startsAt);
/*      */       }
/*  927 */       else if (this.curChar < '')
/*      */       {
/*  929 */         long l = 1L << (this.curChar & 0x3F);
/*      */         do
/*      */         {
/*  932 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           case 0:
/*      */           case 6:
/*  936 */             if ((0xDFFFFFFF & l) == 0L)
/*      */               continue;
/*  938 */             if (kind > 29)
/*  939 */               kind = 29;
/*  940 */             jjCheckNAdd(6);
/*  941 */             break;
/*      */           case 2:
/*  943 */             jjAddStates(16, 18);
/*  944 */             break;
/*      */           case 4:
/*  946 */             if (this.curChar != '\\') continue;
/*  947 */             this.jjstateSet[(this.jjnewStateCnt++)] = 3;
/*      */           case 1:
/*      */           case 3:
/*      */           case 5:
/*      */           }
/*  951 */         }while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/*  955 */         int hiByte = this.curChar >> '\b';
/*  956 */         int i1 = hiByte >> 6;
/*  957 */         long l1 = 1L << (hiByte & 0x3F);
/*  958 */         int i2 = (this.curChar & 0xFF) >> '\006';
/*  959 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         do
/*      */         {
/*  962 */           i--; switch (this.jjstateSet[i])
/*      */           {
/*      */           case 0:
/*  965 */             if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */             {
/*  967 */               if (kind > 7)
/*  968 */                 kind = 7;
/*      */             }
/*  970 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  972 */             if (kind > 29)
/*  973 */               kind = 29;
/*  974 */             jjCheckNAdd(6); break;
/*      */           case 2:
/*  978 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2)) continue;
/*  979 */             jjAddStates(16, 18); break;
/*      */           case 6:
/*  982 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  984 */             if (kind > 29)
/*  985 */               kind = 29;
/*  986 */             jjCheckNAdd(6);
/*      */           }
/*      */         }
/*      */ 
/*  990 */         while (i != startsAt);
/*      */       }
/*  992 */       if (kind != 2147483647)
/*      */       {
/*  994 */         this.jjmatchedKind = kind;
/*  995 */         this.jjmatchedPos = curPos;
/*  996 */         kind = 2147483647;
/*      */       }
/*  998 */       curPos++;
/*  999 */       if ((i = this.jjnewStateCnt) == (startsAt = 7 - (this.jjnewStateCnt = startsAt)))
/* 1000 */         return curPos; try {
/* 1001 */         this.curChar = this.input_stream.readChar(); } catch (IOException e) {
/*      */       }
/* 1002 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
/*      */   {
/* 1011 */     switch (hiByte)
/*      */     {
/*      */     case 48:
/* 1014 */       return (jjbitVec0[i2] & l2) != 0L;
/*      */     }
/* 1016 */     return false;
/*      */   }
/*      */ 
/*      */   private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2)
/*      */   {
/* 1021 */     switch (hiByte)
/*      */     {
/*      */     case 0:
/* 1024 */       return (jjbitVec3[i2] & l2) != 0L;
/*      */     }
/*      */ 
/* 1027 */     return (jjbitVec1[i1] & l1) != 0L;
/*      */   }
/*      */ 
/*      */   private static final boolean jjCanMove_2(int hiByte, int i1, int i2, long l1, long l2)
/*      */   {
/* 1033 */     switch (hiByte)
/*      */     {
/*      */     case 0:
/* 1036 */       return (jjbitVec3[i2] & l2) != 0L;
/*      */     case 48:
/* 1038 */       return (jjbitVec1[i2] & l2) != 0L;
/*      */     }
/*      */ 
/* 1041 */     return (jjbitVec4[i1] & l1) != 0L;
/*      */   }
/*      */ 
/*      */   public QueryParserTokenManager(CharStream stream)
/*      */   {
/* 1077 */     this.input_stream = stream;
/*      */   }
/*      */ 
/*      */   public QueryParserTokenManager(CharStream stream, int lexState)
/*      */   {
/* 1082 */     this(stream);
/* 1083 */     SwitchTo(lexState);
/*      */   }
/*      */ 
/*      */   public void ReInit(CharStream stream)
/*      */   {
/* 1089 */     this.jjmatchedPos = (this.jjnewStateCnt = 0);
/* 1090 */     this.curLexState = this.defaultLexState;
/* 1091 */     this.input_stream = stream;
/* 1092 */     ReInitRounds();
/*      */   }
/*      */ 
/*      */   private void ReInitRounds()
/*      */   {
/* 1097 */     this.jjround = -2147483647;
/* 1098 */     for (int i = 36; i-- > 0; )
/* 1099 */       this.jjrounds[i] = -2147483648;
/*      */   }
/*      */ 
/*      */   public void ReInit(CharStream stream, int lexState)
/*      */   {
/* 1105 */     ReInit(stream);
/* 1106 */     SwitchTo(lexState);
/*      */   }
/*      */ 
/*      */   public void SwitchTo(int lexState)
/*      */   {
/* 1112 */     if ((lexState >= 4) || (lexState < 0)) {
/* 1113 */       throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
/*      */     }
/* 1115 */     this.curLexState = lexState;
/*      */   }
/*      */ 
/*      */   protected Token jjFillToken()
/*      */   {
/* 1126 */     String im = jjstrLiteralImages[this.jjmatchedKind];
/* 1127 */     String curTokenImage = im == null ? this.input_stream.GetImage() : im;
/* 1128 */     int beginLine = this.input_stream.getBeginLine();
/* 1129 */     int beginColumn = this.input_stream.getBeginColumn();
/* 1130 */     int endLine = this.input_stream.getEndLine();
/* 1131 */     int endColumn = this.input_stream.getEndColumn();
/* 1132 */     Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
/*      */ 
/* 1134 */     t.beginLine = beginLine;
/* 1135 */     t.endLine = endLine;
/* 1136 */     t.beginColumn = beginColumn;
/* 1137 */     t.endColumn = endColumn;
/*      */ 
/* 1139 */     return t;
/*      */   }
/*      */ 
/*      */   public Token getNextToken()
/*      */   {
/* 1153 */     int curPos = 0;
/*      */     while (true)
/*      */     {
/*      */       try
/*      */       {
/* 1160 */         this.curChar = this.input_stream.BeginToken();
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/* 1164 */         this.jjmatchedKind = 0;
/* 1165 */         Token matchedToken = jjFillToken();
/* 1166 */         return matchedToken;
/*      */       }
/*      */ 
/* 1169 */       switch (this.curLexState)
/*      */       {
/*      */       case 0:
/* 1172 */         this.jjmatchedKind = 2147483647;
/* 1173 */         this.jjmatchedPos = 0;
/* 1174 */         curPos = jjMoveStringLiteralDfa0_0();
/* 1175 */         break;
/*      */       case 1:
/* 1177 */         this.jjmatchedKind = 2147483647;
/* 1178 */         this.jjmatchedPos = 0;
/* 1179 */         curPos = jjMoveStringLiteralDfa0_1();
/* 1180 */         break;
/*      */       case 2:
/* 1182 */         this.jjmatchedKind = 2147483647;
/* 1183 */         this.jjmatchedPos = 0;
/* 1184 */         curPos = jjMoveStringLiteralDfa0_2();
/* 1185 */         break;
/*      */       case 3:
/* 1187 */         this.jjmatchedKind = 2147483647;
/* 1188 */         this.jjmatchedPos = 0;
/* 1189 */         curPos = jjMoveStringLiteralDfa0_3();
/*      */       }
/*      */ 
/* 1192 */       if (this.jjmatchedKind == 2147483647)
/*      */         break;
/* 1194 */       if (this.jjmatchedPos + 1 < curPos)
/* 1195 */         this.input_stream.backup(curPos - this.jjmatchedPos - 1);
/* 1196 */       if ((jjtoToken[(this.jjmatchedKind >> 6)] & 1L << (this.jjmatchedKind & 0x3F)) != 0L)
/*      */       {
/* 1198 */         Token matchedToken = jjFillToken();
/* 1199 */         if (jjnewLexState[this.jjmatchedKind] != -1)
/* 1200 */           this.curLexState = jjnewLexState[this.jjmatchedKind];
/* 1201 */         return matchedToken;
/*      */       }
/*      */ 
/* 1205 */       if (jjnewLexState[this.jjmatchedKind] != -1) {
/* 1206 */         this.curLexState = jjnewLexState[this.jjmatchedKind];
/*      */       }
/*      */     }
/*      */ 
/* 1210 */     int error_line = this.input_stream.getEndLine();
/* 1211 */     int error_column = this.input_stream.getEndColumn();
/* 1212 */     String error_after = null;
/* 1213 */     boolean EOFSeen = false;
/*      */     try { this.input_stream.readChar(); this.input_stream.backup(1);
/*      */     } catch (IOException e1) {
/* 1216 */       EOFSeen = true;
/* 1217 */       error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
/* 1218 */       if ((this.curChar == '\n') || (this.curChar == '\r')) {
/* 1219 */         error_line++;
/* 1220 */         error_column = 0;
/*      */       }
/*      */       else {
/* 1223 */         error_column++;
/*      */       }
/*      */     }
/* 1225 */     if (!EOFSeen) {
/* 1226 */       this.input_stream.backup(1);
/* 1227 */       error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
/*      */     }
/* 1229 */     throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
/*      */   }
/*      */ 
/*      */   private void jjCheckNAdd(int state)
/*      */   {
/* 1235 */     if (this.jjrounds[state] != this.jjround)
/*      */     {
/* 1237 */       this.jjstateSet[(this.jjnewStateCnt++)] = state;
/* 1238 */       this.jjrounds[state] = this.jjround;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void jjAddStates(int start, int end) {
/*      */     do
/* 1244 */       this.jjstateSet[(this.jjnewStateCnt++)] = jjnextStates[start];
/* 1245 */     while (start++ != end);
/*      */   }
/*      */ 
/*      */   private void jjCheckNAddTwoStates(int state1, int state2) {
/* 1249 */     jjCheckNAdd(state1);
/* 1250 */     jjCheckNAdd(state2);
/*      */   }
/*      */ 
/*      */   private void jjCheckNAddStates(int start, int end)
/*      */   {
/*      */     do
/* 1256 */       jjCheckNAdd(jjnextStates[start]);
/* 1257 */     while (start++ != end);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.queryParser.QueryParserTokenManager
 * JD-Core Version:    0.6.0
 */