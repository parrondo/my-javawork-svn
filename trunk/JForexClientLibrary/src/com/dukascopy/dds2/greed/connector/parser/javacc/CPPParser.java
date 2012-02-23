/*       */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*       */ 
/*       */ import com.dukascopy.dds2.greed.connector.helpers.ExternalEngine;
/*       */ import java.io.InputStream;
/*       */ import java.io.PrintStream;
/*       */ import java.io.Reader;
/*       */ import java.io.UnsupportedEncodingException;
/*       */ import java.util.ArrayList;
/*       */ import java.util.Iterator;
/*       */ import java.util.List;
/*       */ 
/*       */ public final class CPPParser
/*       */   implements CPPParserTreeConstants, CPPParserConstants
/*       */ {
/*    10 */   protected static JJTCPPParserState jjtree = new JJTCPPParserState();
/*    11 */   private static String vers = "0.2";
/*    12 */   private static String id = "Metatrader parser";
/*       */   static ParserCallback fgCallback;
/*    16 */   private static ExternalEngine externalEngine = ExternalEngine.MT4STRATEGY;
/*       */ 
/* 14127 */   private static boolean jj_initialized_once = false;
/*       */   public static CPPParserTokenManager token_source;
/*       */   static JavaCharStream jj_input_stream;
/*       */   public static Token token;
/*       */   public static Token jj_nt;
/*       */   private static int jj_ntk;
/*       */   private static Token jj_scanpos;
/*       */   private static Token jj_lastpos;
/*       */   private static int jj_la;
/* 14139 */   private static boolean jj_lookingAhead = false;
/*       */   private static boolean jj_semLA;
/*       */   private static int jj_gen;
/* 14142 */   private static final int[] jj_la1 = new int[''];
/*       */   private static int[] jj_la1_0;
/*       */   private static int[] jj_la1_1;
/*       */   private static int[] jj_la1_2;
/*       */   private static int[] jj_la1_3;
/*       */   private static int[] jj_la1_4;
/*       */   private static int[] jj_la1_5;
/*       */   private static final JJCalls[] jj_2_rtns;
/*       */   private static boolean jj_rescan;
/*       */   private static int jj_gc;
/*       */   private static final LookaheadSuccess jj_ls;
/*       */   private static List<int[]> jj_expentries;
/*       */   private static int[] jj_expentry;
/*       */   private static int jj_kind;
/*       */   private static int[] jj_lasttokens;
/*       */   private static int jj_endpos;
/*       */ 
/*       */   public static void setParserCallback(ParserCallback cb)
/*       */   {
/*    19 */     fgCallback = cb;
/*       */   }
/*       */ 
/*       */   static String getFullyScopedName()
/*       */   {
/*    33 */     Token t = getToken(1);
/*       */ 
/*    35 */     if ((t.kind != 164) && (t.kind != 42)) {
/*    36 */       return null;
/*       */     }
/*    38 */     StringBuffer s = new StringBuffer();
/*       */     int i;
/*       */     int i;
/*    41 */     if (t.kind != 42)
/*       */     {
/*    43 */       s.append(t.image);
/*    44 */       t = getToken(2);
/*    45 */       i = 3;
/*       */     }
/*       */     else {
/*    48 */       i = 2;
/*       */     }
/*    50 */     while (t.kind == 42)
/*       */     {
/*    52 */       s.append(t.image);
/*    53 */       s.append((t = getToken(i++)).image);
/*    54 */       t = getToken(i++);
/*       */     }
/*       */ 
/*    57 */     return s.toString();
/*       */   }
/*       */ 
/*       */   static boolean isNotNull(Object obj) {
/*    61 */     return obj != null;
/*       */   }
/*       */   static void skipToClosedBracket() {
/*    66 */     int count = 1;
/*       */     Token t;
/*       */     do {
/*    69 */       t = getNextToken();
/*    70 */       if (t.kind == 36)
/*    71 */         count++;
/*    72 */       else if (t.kind == 37)
/*    73 */         count--;
/*       */     }
/*    75 */     while ((t.kind != 0) && (count != 0));
/*       */   }
/*       */ 
/*       */   static void synchronize(int kind) {
/*    79 */     if (getToken(0).kind == kind)
/*    80 */       return;
/*       */     Token t;
/*       */     do
/*    84 */       t = getNextToken();
/*    85 */     while ((t.kind != 0) && (t.kind != kind));
/*       */   }
/*       */ 
/*       */   static boolean isCtor()
/*       */   {
/*    96 */     return getFullyScopedName() != null;
/*       */   }
/*       */ 
/*       */   public static final ASTNode translation_unit()
/*       */     throws ParseException
/*       */   {
/*   102 */     ASTNode jjtn000 = new ASTNode(0);
/*   103 */     boolean jjtc000 = true;
/*   104 */     jjtree.openNodeScope(jjtn000);
/*       */     try
/*       */     {
/*   108 */       while (jj_2_1(2))
/*       */       {
/*   113 */         external_declaration();
/*       */       }
/*   115 */       jj_consume_token(0);
/*   116 */       jjtree.closeNodeScope(jjtn000, true);
/*   117 */       jjtc000 = false;
/*   118 */       ASTNode localASTNode1 = jjtn000;
/*       */       return localASTNode1;
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*   120 */       if (jjtc000) {
/*   121 */         jjtree.clearNodeScope(jjtn000);
/*   122 */         jjtc000 = false;
/*       */       } else {
/*   124 */         jjtree.popNode();
/*       */       }
/*   126 */       if ((jjte000 instanceof RuntimeException)) {
/*   127 */         throw ((RuntimeException)jjte000);
/*       */       }
/*   129 */       if ((jjte000 instanceof ParseException)) {
/*   130 */         throw ((ParseException)jjte000);
/*       */       }
/*   132 */       throw ((Error)jjte000);
/*       */     } finally {
/*   134 */       if (jjtc000)
/*   135 */         jjtree.closeNodeScope(jjtn000, true); 
/*   135 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void external_declaration()
/*       */     throws ParseException
/*       */   {
/*   142 */     boolean isTypedef = false;
/*   143 */     Token firstToken = getToken(1);
/*       */     try
/*       */     {
/*   146 */       if (jj_2_5(2147483647)) {
/*   147 */         returnKeyword();
/*       */         while (true)
/*       */         {
/*   150 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */           {
/*       */           case 40:
/*   153 */             break;
/*       */           default:
/*   155 */             jj_la1[0] = jj_gen;
/*   156 */             break;
/*       */           }
/*   158 */           lParenthesis();
/*   159 */           expression();
/*   160 */           rParenthesis();
/*       */         }
/*   162 */         semicolon();
/*   163 */       } else if (jj_2_6(2147483647)) {
/*   164 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 125:
/*   166 */           template_head();
/*   167 */           break;
/*       */         default:
/*   169 */           jj_la1[1] = jj_gen;
/*       */         }
/*       */ 
/*   172 */         declaration(false, firstToken);
/*   173 */       } else if (jj_2_7(2147483647)) {
/*   174 */         enum_specifier();
/*   175 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 40:
/*       */         case 42:
/*       */         case 63:
/*       */         case 74:
/*       */         case 79:
/*       */         case 87:
/*       */         case 88:
/*       */         case 91:
/*       */         case 97:
/*       */         case 102:
/*       */         case 108:
/*       */         case 109:
/*       */         case 117:
/*       */         case 118:
/*       */         case 121:
/*       */         case 130:
/*       */         case 132:
/*       */         case 135:
/*       */         case 139:
/*       */         case 140:
/*       */         case 164:
/*   197 */           init_declarator_list(false, false, firstToken);
/*   198 */           break;
/*       */         default:
/*   200 */           jj_la1[2] = jj_gen;
/*       */         }
/*       */ 
/*   203 */         semicolon();
/*   204 */       } else if (jj_2_8(2147483647)) {
/*   205 */         dtor_definition(firstToken);
/*   206 */       } else if (jj_2_9(2147483647)) {
/*   207 */         ctor_definition(firstToken);
/*   208 */       } else if (jj_2_10(2147483647)) {
/*   209 */         function_definition(firstToken);
/*   210 */       } else if (jj_2_11(2147483647)) {
/*   211 */         conversion_function_decl_or_def(firstToken);
/*       */       } else {
/*   213 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 125:
/*   215 */           template_head();
/*   216 */           if (jj_2_2(2147483647)) {
/*   217 */             ctor_definition(firstToken);
/*   218 */           } else if (jj_2_3(2147483647)) {
/*   219 */             function_definition(firstToken);
/*   220 */           } else if (jj_2_4(1)) {
/*   221 */             isTypedef = declaration_specifiers();
/*   222 */             switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */             case 40:
/*       */             case 42:
/*       */             case 63:
/*       */             case 74:
/*       */             case 79:
/*       */             case 87:
/*       */             case 88:
/*       */             case 91:
/*       */             case 97:
/*       */             case 102:
/*       */             case 108:
/*       */             case 109:
/*       */             case 117:
/*       */             case 118:
/*       */             case 121:
/*       */             case 130:
/*       */             case 132:
/*       */             case 135:
/*       */             case 139:
/*       */             case 140:
/*       */             case 164:
/*   244 */               init_declarator_list(isTypedef, false, firstToken);
/*   245 */               break;
/*       */             default:
/*   247 */               jj_la1[3] = jj_gen;
/*       */             }
/*       */ 
/*   250 */             semicolon();
/*       */           } else {
/*   252 */             jj_consume_token(-1);
/*   253 */             throw new ParseException();
/*       */           }
/*       */ 
/*       */         default:
/*   257 */           jj_la1[4] = jj_gen;
/*   258 */           if (jj_2_12(1))
/*   259 */             declaration(true, firstToken);
/*       */           else
/*   261 */             switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */             case 44:
/*   263 */               semicolon();
/*   264 */               break;
/*       */             default:
/*   266 */               jj_la1[5] = jj_gen;
/*   267 */               jj_consume_token(-1);
/*   268 */               throw new ParseException();
/*       */             }
/*       */         }
/*       */       }
/*       */     }
/*       */     catch (ParseException e) {
/*   274 */       synchronize(44);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void function_definition(Token firstToken) throws ParseException
/*       */   {
/*   280 */     ASTNode jjtn000 = new ASTNode(2);
/*   281 */     boolean jjtc000 = true;
/*   282 */     jjtree.openNodeScope(jjtn000); boolean isTypedef = false;
/*   283 */     jjtn000.setBeginToken(firstToken);
/*       */     try
/*       */     {
/*       */       Token nameToken;
/*   286 */       if (jj_2_13(3)) {
/*   287 */         isTypedef = declaration_specifiers();
/*   288 */         Token nameToken = function_declarator(isTypedef, firstToken);
/*   289 */         func_decl_def(firstToken);
/*       */       } else {
/*   291 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 42:
/*       */         case 63:
/*       */         case 74:
/*       */         case 135:
/*       */         case 164:
/*   297 */           nameToken = function_declarator(isTypedef, firstToken);
/*   298 */           func_decl_def(firstToken);
/*   299 */           break;
/*       */         default:
/*   301 */           jj_la1[6] = jj_gen;
/*   302 */           jj_consume_token(-1);
/*   303 */           throw new ParseException();
/*       */         }
/*       */       }
/*   306 */       jjtree.closeNodeScope(jjtn000, true);
/*   307 */       jjtc000 = false;
/*   308 */       jjtn000.setEndToken(getToken(0));
/*   309 */       fgCallback.functionDeclEnd(nameToken, getToken(0));
/*       */     } catch (Throwable jjte000) {
/*   311 */       if (jjtc000) {
/*   312 */         jjtree.clearNodeScope(jjtn000);
/*   313 */         jjtc000 = false;
/*       */       } else {
/*   315 */         jjtree.popNode();
/*       */       }
/*   317 */       if ((jjte000 instanceof RuntimeException)) {
/*   318 */         throw ((RuntimeException)jjte000);
/*       */       }
/*   320 */       if ((jjte000 instanceof ParseException)) {
/*   321 */         throw ((ParseException)jjte000);
/*       */       }
/*   323 */       throw ((Error)jjte000);
/*       */     } finally {
/*   325 */       if (jjtc000)
/*   326 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void func_decl_def(Token parent)
/*       */     throws ParseException
/*       */   {
/*   333 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 44:
/*   335 */       semicolon();
/*   336 */       break;
/*       */     case 36:
/*   338 */       compound_statement();
/*   339 */       break;
/*       */     default:
/*   341 */       jj_la1[7] = jj_gen;
/*   342 */       jj_consume_token(-1);
/*   343 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void linkage_specification() throws ParseException
/*       */   {
/*   349 */     ASTNode jjtn000 = new ASTNode(3);
/*   350 */     boolean jjtc000 = true;
/*   351 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(1);
/*       */     try {
/*   353 */       externKeyword();
/*   354 */       stringConstant();
/*   355 */       lCurlyBrace();
/*       */ 
/*   358 */       while (jj_2_14(1))
/*       */       {
/*   363 */         external_declaration();
/*       */       }
/*   365 */       rCurlyBrace();
/*   366 */       if (jj_2_15(2147483647)) {
/*   367 */         semicolon();
/*       */       }
/*       */ 
/*   371 */       declaration(false, firstToken);
/*       */     } catch (Throwable jjte000) {
/*   373 */       if (jjtc000) {
/*   374 */         jjtree.clearNodeScope(jjtn000);
/*   375 */         jjtc000 = false;
/*       */       } else {
/*   377 */         jjtree.popNode();
/*       */       }
/*   379 */       if ((jjte000 instanceof RuntimeException)) {
/*   380 */         throw ((RuntimeException)jjte000);
/*       */       }
/*   382 */       if ((jjte000 instanceof ParseException)) {
/*   383 */         throw ((ParseException)jjte000);
/*       */       }
/*   385 */       throw ((Error)jjte000);
/*       */     } finally {
/*   387 */       if (jjtc000)
/*   388 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void declaration(boolean report, Token firstToken)
/*       */     throws ParseException
/*       */   {
/*   395 */     ASTNode jjtn000 = new ASTNode(4);
/*   396 */     boolean jjtc000 = true;
/*   397 */     jjtree.openNodeScope(jjtn000); boolean isTypedef = false;
/*   398 */     boolean isList = false;
/*   399 */     Token endToken = null;
/*   400 */     Token nameToken = getToken(1);
/*   401 */     if (firstToken == null)
/*   402 */       firstToken = getToken(1);
/*       */     try {
/*   404 */       if (jj_2_16(2)) {
/*   405 */         isTypedef = declaration_specifiers();
/*   406 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 40:
/*       */         case 42:
/*       */         case 63:
/*       */         case 74:
/*       */         case 79:
/*       */         case 87:
/*       */         case 88:
/*       */         case 91:
/*       */         case 97:
/*       */         case 102:
/*       */         case 108:
/*       */         case 109:
/*       */         case 117:
/*       */         case 118:
/*       */         case 121:
/*       */         case 130:
/*       */         case 132:
/*       */         case 135:
/*       */         case 139:
/*       */         case 140:
/*       */         case 164:
/*   428 */           nameToken = getToken(1);
/*   429 */           isList = init_declarator_list(isTypedef, report, firstToken);
/*   430 */           break;
/*       */         default:
/*   432 */           jj_la1[8] = jj_gen;
/*       */         }
/*       */ 
/*   435 */         jjtn000.setBeginToken(firstToken);
/*   436 */         endToken = getToken(0);
/*   437 */         jjtn000.setEndToken(endToken);
/*   438 */         semicolon();
/*       */       } else {
/*   440 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 100:
/*   442 */           linkage_specification();
/*   443 */           break;
/*       */         default:
/*   445 */           jj_la1[9] = jj_gen;
/*   446 */           jj_consume_token(-1);
/*   447 */           throw new ParseException();
/*       */         }
/*       */       }
/*   450 */       jjtree.closeNodeScope(jjtn000, true);
/*   451 */       jjtc000 = false;
/*   452 */       if (isList)
/*   453 */         fgCallback.fieldListDecl(nameToken, firstToken, endToken);
/*       */       else
/*   455 */         fgCallback.fieldDecl(nameToken, firstToken, endToken);
/*       */     } catch (Throwable jjte000) {
/*   457 */       if (jjtc000) {
/*   458 */         jjtree.clearNodeScope(jjtn000);
/*   459 */         jjtc000 = false;
/*       */       } else {
/*   461 */         jjtree.popNode();
/*       */       }
/*   463 */       if ((jjte000 instanceof RuntimeException)) {
/*   464 */         throw ((RuntimeException)jjte000);
/*       */       }
/*   466 */       if ((jjte000 instanceof ParseException)) {
/*   467 */         throw ((ParseException)jjte000);
/*       */       }
/*   469 */       throw ((Error)jjte000);
/*       */     } finally {
/*   471 */       if (jjtc000)
/*   472 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void declaration_counter(boolean report, Token firstToken)
/*       */     throws ParseException
/*       */   {
/*   479 */     ASTNode jjtn000 = new ASTNode(5);
/*   480 */     boolean jjtc000 = true;
/*   481 */     jjtree.openNodeScope(jjtn000); boolean isTypedef = false;
/*   482 */     boolean isList = false;
/*   483 */     Token nameToken = getToken(0);
/*   484 */     Token endToken = null;
/*       */ 
/*   486 */     Token counterToken = getToken(1);
/*       */ 
/*   488 */     Token typeToken = null;
/*       */     try {
/*   490 */       if (jj_2_17(2)) {
/*   491 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */         {
/*       */         case 40:
/*       */         case 42:
/*       */         case 63:
/*       */         case 74:
/*       */         case 79:
/*       */         case 87:
/*       */         case 88:
/*       */         case 91:
/*       */         case 97:
/*       */         case 102:
/*       */         case 108:
/*       */         case 109:
/*       */         case 117:
/*       */         case 118:
/*       */         case 121:
/*       */         case 130:
/*       */         case 132:
/*       */         case 135:
/*       */         case 139:
/*       */         case 140:
/*       */         case 164:
/*   514 */           nameToken = getToken(1);
/*   515 */           isList = init_declarator_list(isTypedef, report, firstToken);
/*   516 */           break;
/*       */         default:
/*   518 */           jj_la1[10] = jj_gen;
/*       */         }
/*       */ 
/*   521 */         jjtn000.setBeginToken(counterToken);
/*   522 */         endToken = getToken(0);
/*   523 */         jjtn000.setEndToken(endToken);
/*   524 */         semicolon();
/*       */       } else {
/*   526 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 100:
/*   528 */           linkage_specification();
/*   529 */           break;
/*       */         default:
/*   531 */           jj_la1[11] = jj_gen;
/*   532 */           jj_consume_token(-1);
/*   533 */           throw new ParseException();
/*       */         }
/*       */       }
/*   536 */       jjtree.closeNodeScope(jjtn000, true);
/*   537 */       jjtc000 = false;
/*   538 */       if (isList)
/*   539 */         fgCallback.fieldCounterListDecl(nameToken, firstToken, endToken);
/*       */       else
/*   541 */         fgCallback.fieldCounterDecl(nameToken, counterToken, endToken);
/*       */     } catch (Throwable jjte000) {
/*   543 */       if (jjtc000) {
/*   544 */         jjtree.clearNodeScope(jjtn000);
/*   545 */         jjtc000 = false;
/*       */       } else {
/*   547 */         jjtree.popNode();
/*       */       }
/*   549 */       if ((jjte000 instanceof RuntimeException)) {
/*   550 */         throw ((RuntimeException)jjte000);
/*       */       }
/*   552 */       if ((jjte000 instanceof ParseException)) {
/*   553 */         throw ((ParseException)jjte000);
/*       */       }
/*   555 */       throw ((Error)jjte000);
/*       */     } finally {
/*   557 */       if (jjtc000)
/*   558 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final boolean declaration_specifiers()
/*       */     throws ParseException
/*       */   {
/*   569 */     ASTNode jjtn000 = new ASTNode(6);
/*   570 */     boolean jjtc000 = true;
/*   571 */     jjtree.openNodeScope(jjtn000);
/*   572 */     boolean isTypedef = false;
/*       */     try {
/*   574 */       if (jj_2_32(1)) {
/*       */         boolean tmp;
/*       */         do { tmp = type_modifiers();
/*   578 */           isTypedef |= tmp; }
/*   579 */         while (jj_2_18(2147483647));
/*       */ 
/*   585 */         if (jj_2_26(2)) {
/*   586 */           if (jj_2_24(2147483647)) {
/*   587 */             builtin_type_specifier();
/*       */ 
/*   590 */             while (jj_2_19(2))
/*       */             {
/*   595 */               if (jj_2_20(2147483647)) {
/*   596 */                 builtin_type_specifier();
/*   597 */               } else if (jj_2_21(2147483647)) {
/*   598 */                 tmp = type_modifiers();
/*       */               } else {
/*   600 */                 jj_consume_token(-1);
/*   601 */                 throw new ParseException();
/*       */               }
/*   603 */               isTypedef |= tmp;
/*       */             }
/*       */           }
/*   605 */           if (jj_2_25(1)) {
/*   606 */             switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */             case 122:
/*       */             case 123:
/*       */             case 129:
/*   610 */               class_specifier();
/*   611 */               break;
/*       */             case 99:
/*   613 */               enum_specifier();
/*   614 */               break;
/*       */             default:
/*   616 */               jj_la1[12] = jj_gen;
/*   617 */               if (jj_2_22(1)) {
/*   618 */                 qualified_type();
/*       */               } else {
/*   620 */                 jj_consume_token(-1);
/*   621 */                 throw new ParseException();
/*       */               }
/*       */ 
/*       */             }
/*       */ 
/*   626 */             while (jj_2_23(2))
/*       */             {
/*   631 */               tmp = type_modifiers();
/*   632 */               isTypedef |= tmp;
/*       */             }
/*       */           }
/*   635 */           jj_consume_token(-1);
/*   636 */           throw new ParseException();
/*       */         }
/*       */       }
/*       */       else
/*       */       {
/*   641 */         if (jj_2_33(2147483647)) {
/*   642 */           builtin_type_specifier();
/*       */           while (true)
/*       */           {
/*   645 */             if (!jj_2_27(2))
/*       */             {
/*       */               break label484;
/*       */             }
/*   650 */             if (jj_2_28(2147483647)) {
/*   651 */               builtin_type_specifier(); continue;
/*   652 */             }if (!jj_2_29(1)) break;
/*   653 */             boolean tmp = type_modifiers();
/*   654 */             isTypedef |= tmp;
/*       */           }
/*   656 */           jj_consume_token(-1);
/*   657 */           throw new ParseException();
/*       */         }
/*       */ 
/*   660 */         if (jj_2_34(1)) {
/*   661 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */           case 122:
/*       */           case 123:
/*       */           case 129:
/*   665 */             class_specifier();
/*   666 */             break;
/*       */           case 99:
/*   668 */             enum_specifier();
/*   669 */             break;
/*       */           default:
/*   671 */             jj_la1[13] = jj_gen;
/*   672 */             if (jj_2_30(1)) {
/*   673 */               qualified_type();
/*       */             } else {
/*   675 */               jj_consume_token(-1);
/*   676 */               throw new ParseException();
/*       */             }
/*       */ 
/*       */           }
/*       */ 
/*   681 */           while (jj_2_31(2))
/*       */           {
/*   686 */             boolean tmp = type_modifiers();
/*   687 */             isTypedef |= tmp;
/*       */           }
/*       */         }
/*   690 */         jj_consume_token(-1);
/*   691 */         throw new ParseException();
/*       */       }
/*   693 */       label484: jjtree.closeNodeScope(jjtn000, true);
/*   694 */       jjtc000 = false;
/*   695 */       boolean bool1 = isTypedef;
/*       */       return bool1;
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*   697 */       if (jjtc000) {
/*   698 */         jjtree.clearNodeScope(jjtn000);
/*   699 */         jjtc000 = false;
/*       */       } else {
/*   701 */         jjtree.popNode();
/*       */       }
/*   703 */       if ((jjte000 instanceof RuntimeException)) {
/*   704 */         throw ((RuntimeException)jjte000);
/*       */       }
/*   706 */       if ((jjte000 instanceof ParseException)) {
/*   707 */         throw ((ParseException)jjte000);
/*       */       }
/*   709 */       throw ((Error)jjte000);
/*       */     } finally {
/*   711 */       if (jjtc000)
/*   712 */         jjtree.closeNodeScope(jjtn000, true); 
/*   712 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void type_specifier()
/*       */     throws ParseException
/*       */   {
/*   720 */     ASTNode jjtn000 = new ASTNode(7);
/*   721 */     boolean jjtc000 = true;
/*   722 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*   724 */       if (jj_2_35(1))
/*   725 */         simple_type_specifier();
/*       */       else
/*   727 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 122:
/*       */         case 123:
/*       */         case 129:
/*   731 */           class_specifier();
/*   732 */           break;
/*       */         case 99:
/*   734 */           enum_specifier();
/*   735 */           break;
/*       */         default:
/*   737 */           jj_la1[14] = jj_gen;
/*   738 */           jj_consume_token(-1);
/*   739 */           throw new ParseException();
/*       */         }
/*       */     }
/*       */     catch (Throwable jjte000) {
/*   743 */       if (jjtc000) {
/*   744 */         jjtree.clearNodeScope(jjtn000);
/*   745 */         jjtc000 = false;
/*       */       } else {
/*   747 */         jjtree.popNode();
/*       */       }
/*   749 */       if ((jjte000 instanceof RuntimeException)) {
/*   750 */         throw ((RuntimeException)jjte000);
/*       */       }
/*   752 */       if ((jjte000 instanceof ParseException)) {
/*   753 */         throw ((ParseException)jjte000);
/*       */       }
/*   755 */       throw ((Error)jjte000);
/*       */     } finally {
/*   757 */       if (jjtc000)
/*   758 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void simple_type_specifier()
/*       */     throws ParseException
/*       */   {
/*   765 */     ASTNode jjtn000 = new ASTNode(8);
/*   766 */     boolean jjtc000 = true;
/*   767 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*   769 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 87:
/*       */       case 88:
/*       */       case 91:
/*       */       case 97:
/*       */       case 102:
/*       */       case 108:
/*       */       case 109:
/*       */       case 117:
/*       */       case 118:
/*       */       case 121:
/*       */       case 130:
/*       */       case 132:
/*       */       case 139:
/*       */       case 140:
/*   784 */         builtin_type_specifier();
/*   785 */         break;
/*       */       case 89:
/*       */       case 90:
/*       */       case 92:
/*       */       case 93:
/*       */       case 94:
/*       */       case 95:
/*       */       case 96:
/*       */       case 98:
/*       */       case 99:
/*       */       case 100:
/*       */       case 101:
/*       */       case 103:
/*       */       case 104:
/*       */       case 105:
/*       */       case 106:
/*       */       case 107:
/*       */       case 110:
/*       */       case 111:
/*       */       case 112:
/*       */       case 113:
/*       */       case 114:
/*       */       case 115:
/*       */       case 116:
/*       */       case 119:
/*       */       case 120:
/*       */       case 122:
/*       */       case 123:
/*       */       case 124:
/*       */       case 125:
/*       */       case 126:
/*       */       case 127:
/*       */       case 128:
/*       */       case 129:
/*       */       case 131:
/*       */       case 133:
/*       */       case 134:
/*       */       case 135:
/*       */       case 136:
/*       */       case 137:
/*       */       case 138:
/*       */       default:
/*   787 */         jj_la1[15] = jj_gen;
/*   788 */         if (jj_2_36(1)) {
/*   789 */           qualified_type();
/*       */         } else {
/*   791 */           jj_consume_token(-1);
/*   792 */           throw new ParseException();
/*       */         }
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*   796 */       if (jjtc000) {
/*   797 */         jjtree.clearNodeScope(jjtn000);
/*   798 */         jjtc000 = false;
/*       */       } else {
/*   800 */         jjtree.popNode();
/*       */       }
/*   802 */       if ((jjte000 instanceof RuntimeException)) {
/*   803 */         throw ((RuntimeException)jjte000);
/*       */       }
/*   805 */       if ((jjte000 instanceof ParseException)) {
/*   806 */         throw ((ParseException)jjte000);
/*       */       }
/*   808 */       throw ((Error)jjte000);
/*       */     } finally {
/*   810 */       if (jjtc000)
/*   811 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void scope_override_lookahead() throws ParseException
/*       */   {
/*   817 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 42:
/*   819 */       scope();
/*   820 */       break;
/*       */     case 164:
/*   822 */       id();
/*   823 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 66:
/*   825 */         lessThan();
/*   826 */         template_argument_list();
/*   827 */         greaterThan();
/*   828 */         break;
/*       */       default:
/*   830 */         jj_la1[16] = jj_gen;
/*       */       }
/*       */ 
/*   833 */       scope();
/*   834 */       break;
/*       */     default:
/*   836 */       jj_la1[17] = jj_gen;
/*   837 */       jj_consume_token(-1);
/*   838 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final String scope_override() throws ParseException {
/*   843 */     String name = "";
/*       */ 
/*   845 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 42:
/*   847 */       scope();
/*   848 */       name = name + "::";
/*       */     case 164:
/*       */     }
/*   851 */     while (jj_2_37(2))
/*       */     {
/*   856 */       Token t = id();
/*   857 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 66:
/*   859 */         lessThan();
/*   860 */         template_argument_list();
/*   861 */         greaterThan();
/*   862 */         break;
/*       */       default:
/*   864 */         jj_la1[18] = jj_gen;
/*       */       }
/*       */ 
/*   867 */       scope();
/*   868 */       name = name + t.image + "::"; continue;
/*       */       while (true)
/*       */       {
/*   874 */         t = id();
/*   875 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 66:
/*   877 */           lessThan();
/*   878 */           template_argument_list();
/*   879 */           greaterThan();
/*   880 */           break;
/*       */         default:
/*   882 */           jj_la1[19] = jj_gen;
/*       */         }
/*       */ 
/*   885 */         scope();
/*   886 */         name = name + t.image + "::";
/*   887 */         if (!jj_2_38(2)) break; continue;
/*       */ 
/*   895 */         jj_la1[20] = jj_gen;
/*   896 */         jj_consume_token(-1);
/*   897 */         throw new ParseException();
/*       */       }
/*       */     }
/*   899 */     return name;
/*       */   }
/*       */ 
/*       */   public static final Token qualified_id()
/*       */     throws ParseException
/*       */   {
/*   905 */     ASTNode jjtn000 = new ASTNode(9);
/*   906 */     boolean jjtc000 = true;
/*   907 */     jjtree.openNodeScope(jjtn000); String name = "";
/*   908 */     String scopeName = "";
/*       */ 
/*   910 */     Token beginToken = getToken(1);
/*   911 */     jjtn000.setBeginToken(beginToken);
/*       */     try {
/*   913 */       if (jj_2_39(2147483647))
/*   914 */         scopeName = scope_override();
/*       */       Token localToken1;
/*   918 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 164:
/*   920 */         Token t = id();
/*   921 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 66:
/*   923 */           lessThan();
/*   924 */           template_argument_list();
/*   925 */           greaterThan();
/*   926 */           break;
/*       */         default:
/*   928 */           jj_la1[21] = jj_gen;
/*       */         }
/*       */ 
/*   931 */         jjtree.closeNodeScope(jjtn000, true);
/*   932 */         jjtc000 = false;
/*   933 */         jjtn000.setEndToken(getToken(0));
/*   934 */         localToken1 = ParserCallback.createToken(scopeName + t.image, beginToken, t);
/*       */         return localToken1;
/*       */       case 135:
/*   937 */         operatorKeyword();
/*   938 */         name = optor();
/*   939 */         jjtree.closeNodeScope(jjtn000, true);
/*   940 */         jjtc000 = false;
/*   941 */         jjtn000.setEndToken(getToken(0));
/*   942 */         localToken1 = ParserCallback.createToken(scopeName + "operator" + name, beginToken, getToken(0));
/*       */         return localToken1;
/*       */       }
/*   945 */       jj_la1[22] = jj_gen;
/*   946 */       jj_consume_token(-1);
/*   947 */       throw new ParseException();
/*       */     }
/*       */     catch (Throwable jjte000) {
/*   950 */       if (jjtc000) {
/*   951 */         jjtree.clearNodeScope(jjtn000);
/*   952 */         jjtc000 = false;
/*       */       } else {
/*   954 */         jjtree.popNode();
/*       */       }
/*   956 */       if ((jjte000 instanceof RuntimeException)) {
/*   957 */         throw ((RuntimeException)jjte000);
/*       */       }
/*   959 */       if ((jjte000 instanceof ParseException)) {
/*   960 */         throw ((ParseException)jjte000);
/*       */       }
/*   962 */       throw ((Error)jjte000);
/*       */     } finally {
/*   964 */       if (jjtc000)
/*   965 */         jjtree.closeNodeScope(jjtn000, true); 
/*   965 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void ptr_to_member()
/*       */     throws ParseException
/*       */   {
/*   972 */     scope_override();
/*   973 */     star();
/*       */   }
/*       */ 
/*       */   public static final void qualified_type() throws ParseException {
/*   977 */     if (!isNotNull(getFullyScopedName()))
/*       */     {
/*   980 */       jj_consume_token(-1);
/*   981 */       throw new ParseException();
/*       */     }
/*   983 */     qualified_id();
/*       */   }
/*       */ 
/*       */   public static final boolean type_modifiers()
/*       */     throws ParseException
/*       */   {
/*   992 */     ASTNode jjtn000 = new ASTNode(10);
/*   993 */     boolean jjtc000 = true;
/*   994 */     jjtree.openNodeScope(jjtn000); boolean isTypedef = false;
/*       */     try {
/*   996 */       if (jj_2_40(1))
/*   997 */         isTypedef = storage_class_specifier();
/*       */       else {
/*   999 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 92:
/*       */         case 133:
/*  1002 */           type_qualifier();
/*  1003 */           break;
/*       */         case 107:
/*  1005 */           jj_consume_token(107);
/*  1006 */           break;
/*       */         case 131:
/*  1008 */           jj_consume_token(131);
/*  1009 */           break;
/*       */         case 104:
/*  1011 */           jj_consume_token(104);
/*  1012 */           break;
/*       */         default:
/*  1014 */           jj_la1[23] = jj_gen;
/*  1015 */           jj_consume_token(-1);
/*  1016 */           throw new ParseException();
/*       */         }
/*       */       }
/*  1019 */       jjtree.closeNodeScope(jjtn000, true);
/*  1020 */       jjtc000 = false;
/*  1021 */       boolean bool1 = isTypedef;
/*       */       return bool1;
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  1023 */       if (jjtc000) {
/*  1024 */         jjtree.clearNodeScope(jjtn000);
/*  1025 */         jjtc000 = false;
/*       */       } else {
/*  1027 */         jjtree.popNode();
/*       */       }
/*  1029 */       if ((jjte000 instanceof RuntimeException)) {
/*  1030 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1032 */       if ((jjte000 instanceof ParseException)) {
/*  1033 */         throw ((ParseException)jjte000);
/*       */       }
/*  1035 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1037 */       if (jjtc000)
/*  1038 */         jjtree.closeNodeScope(jjtn000, true); 
/*  1038 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void type_qualifier()
/*       */     throws ParseException
/*       */   {
/*  1046 */     ASTNode jjtn000 = new ASTNode(11);
/*  1047 */     boolean jjtc000 = true;
/*  1048 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  1050 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 92:
/*  1052 */         constKeyword();
/*  1053 */         break;
/*       */       case 133:
/*  1055 */         volatileKeyword();
/*  1056 */         break;
/*       */       default:
/*  1058 */         jj_la1[24] = jj_gen;
/*  1059 */         jj_consume_token(-1);
/*  1060 */         throw new ParseException();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  1063 */       if (jjtc000) {
/*  1064 */         jjtree.clearNodeScope(jjtn000);
/*  1065 */         jjtc000 = false;
/*       */       } else {
/*  1067 */         jjtree.popNode();
/*       */       }
/*  1069 */       if ((jjte000 instanceof RuntimeException)) {
/*  1070 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1072 */       if ((jjte000 instanceof ParseException)) {
/*  1073 */         throw ((ParseException)jjte000);
/*       */       }
/*  1075 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1077 */       if (jjtc000)
/*  1078 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final boolean storage_class_specifier()
/*       */     throws ParseException
/*       */   {
/*  1089 */     ASTNode jjtn000 = new ASTNode(12);
/*  1090 */     boolean jjtc000 = true;
/*  1091 */     jjtree.openNodeScope(jjtn000);
/*       */     try
/*       */     {
/*       */       int i;
/*  1093 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 85:
/*       */       case 100:
/*       */       case 115:
/*       */       case 120:
/*  1098 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 85:
/*  1100 */           autoKeyword();
/*  1101 */           break;
/*       */         case 115:
/*  1103 */           registerKeyword();
/*  1104 */           break;
/*       */         case 120:
/*  1106 */           staticKeyword();
/*  1107 */           break;
/*       */         case 100:
/*  1109 */           externKeyword();
/*  1110 */           break;
/*       */         default:
/*  1112 */           jj_la1[25] = jj_gen;
/*  1113 */           jj_consume_token(-1);
/*  1114 */           throw new ParseException();
/*       */         }
/*  1116 */         jjtree.closeNodeScope(jjtn000, true);
/*  1117 */         jjtc000 = false;
/*  1118 */         i = 0;
/*       */         return i;
/*       */       case 128:
/*  1121 */         typedefKeyword();
/*  1122 */         jjtree.closeNodeScope(jjtn000, true);
/*  1123 */         jjtc000 = false;
/*  1124 */         i = 1;
/*       */         return i;
/*       */       }
/*  1127 */       jj_la1[26] = jj_gen;
/*  1128 */       if (fgCallback.isStorageClassSpecifier(getToken(1))) {
/*  1129 */         id();
/*  1130 */         jjtree.closeNodeScope(jjtn000, true);
/*  1131 */         jjtc000 = false;
/*  1132 */         i = 0;
/*       */         return i;
/*       */       }
/*  1134 */       jj_consume_token(-1);
/*  1135 */       throw new ParseException();
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  1139 */       if (jjtc000) {
/*  1140 */         jjtree.clearNodeScope(jjtn000);
/*  1141 */         jjtc000 = false;
/*       */       } else {
/*  1143 */         jjtree.popNode();
/*       */       }
/*  1145 */       if ((jjte000 instanceof RuntimeException)) {
/*  1146 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1148 */       if ((jjte000 instanceof ParseException)) {
/*  1149 */         throw ((ParseException)jjte000);
/*       */       }
/*  1151 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1153 */       if (jjtc000)
/*  1154 */         jjtree.closeNodeScope(jjtn000, true); 
/*  1154 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token builtin_type_specifier()
/*       */     throws ParseException
/*       */   {
/*  1162 */     ASTNode jjtn000 = new ASTNode(13);
/*  1163 */     boolean jjtc000 = true;
/*  1164 */     jjtree.openNodeScope(jjtn000);
/*       */     try
/*       */     {
/*       */       Token t;
/*  1166 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 132:
/*  1168 */         t = jj_consume_token(132);
/*  1169 */         break;
/*       */       case 91:
/*  1171 */         t = jj_consume_token(91);
/*  1172 */         break;
/*       */       case 117:
/*  1174 */         t = jj_consume_token(117);
/*  1175 */         break;
/*       */       case 108:
/*  1177 */         t = jj_consume_token(108);
/*  1178 */         break;
/*       */       case 109:
/*  1180 */         t = jj_consume_token(109);
/*  1181 */         break;
/*       */       case 102:
/*  1183 */         t = jj_consume_token(102);
/*  1184 */         break;
/*       */       case 97:
/*  1186 */         t = jj_consume_token(97);
/*  1187 */         break;
/*       */       case 118:
/*  1189 */         t = jj_consume_token(118);
/*  1190 */         break;
/*       */       case 130:
/*  1192 */         t = jj_consume_token(130);
/*  1193 */         break;
/*       */       case 87:
/*  1195 */         t = jj_consume_token(87);
/*  1196 */         break;
/*       */       case 88:
/*  1198 */         t = jj_consume_token(88);
/*  1199 */         break;
/*       */       case 121:
/*  1201 */         t = jj_consume_token(121);
/*  1202 */         break;
/*       */       case 139:
/*  1204 */         t = jj_consume_token(139);
/*  1205 */         break;
/*       */       case 140:
/*  1207 */         t = jj_consume_token(140);
/*  1208 */         break;
/*       */       case 89:
/*       */       case 90:
/*       */       case 92:
/*       */       case 93:
/*       */       case 94:
/*       */       case 95:
/*       */       case 96:
/*       */       case 98:
/*       */       case 99:
/*       */       case 100:
/*       */       case 101:
/*       */       case 103:
/*       */       case 104:
/*       */       case 105:
/*       */       case 106:
/*       */       case 107:
/*       */       case 110:
/*       */       case 111:
/*       */       case 112:
/*       */       case 113:
/*       */       case 114:
/*       */       case 115:
/*       */       case 116:
/*       */       case 119:
/*       */       case 120:
/*       */       case 122:
/*       */       case 123:
/*       */       case 124:
/*       */       case 125:
/*       */       case 126:
/*       */       case 127:
/*       */       case 128:
/*       */       case 129:
/*       */       case 131:
/*       */       case 133:
/*       */       case 134:
/*       */       case 135:
/*       */       case 136:
/*       */       case 137:
/*       */       case 138:
/*       */       default:
/*  1210 */         jj_la1[27] = jj_gen;
/*  1211 */         jj_consume_token(-1);
/*  1212 */         throw new ParseException();
/*       */       }
/*  1214 */       jjtree.closeNodeScope(jjtn000, true);
/*  1215 */       jjtc000 = false;
/*  1216 */       jjtn000.setBeginToken(t);
/*  1217 */       jjtn000.setEndToken(t);
/*  1218 */       jjtn000.setSpecialToken(t.specialToken);
/*  1219 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  1221 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  1223 */       if (jjtc000)
/*  1224 */         jjtree.closeNodeScope(jjtn000, true); 
/*  1224 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final boolean init_declarator_list(boolean isTypedef, boolean report, Token firstToken)
/*       */     throws ParseException
/*       */   {
/*  1232 */     ASTNode jjtn000 = new ASTNode(14);
/*  1233 */     boolean jjtc000 = true;
/*  1234 */     jjtree.openNodeScope(jjtn000); boolean isList = false;
/*       */     try {
/*  1236 */       init_declarator(isTypedef, report, firstToken);
/*       */       while (true)
/*       */       {
/*  1239 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */         {
/*       */         case 45:
/*  1242 */           break;
/*       */         default:
/*  1244 */           jj_la1[28] = jj_gen;
/*  1245 */           break;
/*       */         }
/*  1247 */         comma();
/*  1248 */         init_declarator(isTypedef, report, firstToken);
/*  1249 */         isList = true;
/*       */       }
/*  1251 */       jjtree.closeNodeScope(jjtn000, true);
/*  1252 */       jjtc000 = false;
/*  1253 */       boolean bool1 = isList;
/*       */       return bool1;
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  1255 */       if (jjtc000) {
/*  1256 */         jjtree.clearNodeScope(jjtn000);
/*  1257 */         jjtc000 = false;
/*       */       } else {
/*  1259 */         jjtree.popNode();
/*       */       }
/*  1261 */       if ((jjte000 instanceof RuntimeException)) {
/*  1262 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1264 */       if ((jjte000 instanceof ParseException)) {
/*  1265 */         throw ((ParseException)jjte000);
/*       */       }
/*  1267 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1269 */       if (jjtc000)
/*  1270 */         jjtree.closeNodeScope(jjtn000, true); 
/*  1270 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void init_declarator(boolean isTypedef, boolean report, Token firstToken)
/*       */     throws ParseException
/*       */   {
/*  1278 */     ASTNode jjtn000 = new ASTNode(15);
/*  1279 */     boolean jjtc000 = true;
/*  1280 */     jjtree.openNodeScope(jjtn000);
/*  1281 */     if (firstToken == null)
/*  1282 */       firstToken = getToken(1);
/*       */     try {
/*  1284 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 87:
/*       */       case 88:
/*       */       case 91:
/*       */       case 97:
/*       */       case 102:
/*       */       case 108:
/*       */       case 109:
/*       */       case 117:
/*       */       case 118:
/*       */       case 121:
/*       */       case 130:
/*       */       case 132:
/*       */       case 139:
/*       */       case 140:
/*  1299 */         builtin_type_specifier();
/*  1300 */         break;
/*       */       case 89:
/*       */       case 90:
/*       */       case 92:
/*       */       case 93:
/*       */       case 94:
/*       */       case 95:
/*       */       case 96:
/*       */       case 98:
/*       */       case 99:
/*       */       case 100:
/*       */       case 101:
/*       */       case 103:
/*       */       case 104:
/*       */       case 105:
/*       */       case 106:
/*       */       case 107:
/*       */       case 110:
/*       */       case 111:
/*       */       case 112:
/*       */       case 113:
/*       */       case 114:
/*       */       case 115:
/*       */       case 116:
/*       */       case 119:
/*       */       case 120:
/*       */       case 122:
/*       */       case 123:
/*       */       case 124:
/*       */       case 125:
/*       */       case 126:
/*       */       case 127:
/*       */       case 128:
/*       */       case 129:
/*       */       case 131:
/*       */       case 133:
/*       */       case 134:
/*       */       case 135:
/*       */       case 136:
/*       */       case 137:
/*       */       case 138:
/*       */       default:
/*  1302 */         jj_la1[29] = jj_gen;
/*       */       }
/*       */ 
/*  1305 */       Token nameToken = declarator();
/*  1306 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 40:
/*       */       case 48:
/*  1309 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 48:
/*  1311 */           assignEqual();
/*  1312 */           initializer();
/*  1313 */           break;
/*       */         case 40:
/*  1315 */           lParenthesis();
/*  1316 */           expression_list();
/*  1317 */           rParenthesis();
/*  1318 */           break;
/*       */         default:
/*  1320 */           jj_la1[30] = jj_gen;
/*  1321 */           jj_consume_token(-1);
/*  1322 */           throw new ParseException();
/*       */         }
/*       */ 
/*       */       default:
/*  1326 */         jj_la1[31] = jj_gen;
/*       */       }
/*       */ 
/*  1329 */       jjtree.closeNodeScope(jjtn000, true);
/*  1330 */       jjtc000 = false;
/*  1331 */       jjtn000.setSpecialToken(nameToken.specialToken);
/*  1332 */       jjtn000.setParams(nameToken.image, nameToken.beginLine, nameToken.beginColumn, nameToken.endLine, nameToken.endColumn);
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  1337 */       if (jjtc000) {
/*  1338 */         jjtree.clearNodeScope(jjtn000);
/*  1339 */         jjtc000 = false;
/*       */       } else {
/*  1341 */         jjtree.popNode();
/*       */       }
/*  1343 */       if ((jjte000 instanceof RuntimeException)) {
/*  1344 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1346 */       if ((jjte000 instanceof ParseException)) {
/*  1347 */         throw ((ParseException)jjte000);
/*       */       }
/*  1349 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1351 */       if (jjtc000)
/*  1352 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void class_head()
/*       */     throws ParseException
/*       */   {
/*  1360 */     ASTNode jjtn000 = new ASTNode(16);
/*  1361 */     boolean jjtc000 = true;
/*  1362 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  1364 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 122:
/*  1366 */         structKeyword();
/*  1367 */         break;
/*       */       case 129:
/*  1369 */         unionKeyword();
/*  1370 */         break;
/*       */       case 123:
/*  1372 */         classKeyword();
/*  1373 */         break;
/*       */       default:
/*  1375 */         jj_la1[32] = jj_gen;
/*  1376 */         jj_consume_token(-1);
/*  1377 */         throw new ParseException();
/*       */       }
/*  1379 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 178:
/*  1381 */         jj_consume_token(178);
/*  1382 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 43:
/*  1384 */           base_clause();
/*  1385 */           break;
/*       */         default:
/*  1387 */           jj_la1[33] = jj_gen;
/*       */         }
/*       */ 
/*  1390 */         break;
/*       */       default:
/*  1392 */         jj_la1[34] = jj_gen;
/*       */       }
/*       */     }
/*       */     catch (Throwable jjte000) {
/*  1396 */       if (jjtc000) {
/*  1397 */         jjtree.clearNodeScope(jjtn000);
/*  1398 */         jjtc000 = false;
/*       */       } else {
/*  1400 */         jjtree.popNode();
/*       */       }
/*  1402 */       if ((jjte000 instanceof RuntimeException)) {
/*  1403 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1405 */       if ((jjte000 instanceof ParseException)) {
/*  1406 */         throw ((ParseException)jjte000);
/*       */       }
/*  1408 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1410 */       if (jjtc000)
/*  1411 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void class_specifier()
/*       */     throws ParseException
/*       */   {
/*  1418 */     ASTNode jjtn000 = new ASTNode(17);
/*  1419 */     boolean jjtc000 = true;
/*  1420 */     jjtree.openNodeScope(jjtn000);
/*  1421 */     Token firstToken = getToken(1);
/*  1422 */     Token nameToken = null;
/*       */     try
/*       */     {
/*       */       try
/*       */       {
/*       */         int kind;
/*  1426 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 122:
/*  1428 */           structKeyword();
/*  1429 */           kind = 3;
/*  1430 */           break;
/*       */         case 129:
/*  1432 */           unionKeyword();
/*  1433 */           kind = 4;
/*  1434 */           break;
/*       */         case 123:
/*  1436 */           classKeyword();
/*  1437 */           kind = 5;
/*  1438 */           break;
/*       */         default:
/*  1440 */           jj_la1[35] = jj_gen;
/*  1441 */           jj_consume_token(-1);
/*  1442 */           throw new ParseException();
/*       */         }
/*       */         Token u;
/*  1444 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 36:
/*  1446 */           lCurlyBrace();
/*  1447 */           nameToken = getToken(0);
/*  1448 */           fgCallback.structDeclBegin(nameToken, kind, firstToken);
/*       */ 
/*  1451 */           while (jj_2_41(1))
/*       */           {
/*  1456 */             member_declaration();
/*       */           }
/*  1458 */           u = rCurlyBrace();
/*  1459 */           fgCallback.structDeclEnd(nameToken, u);
/*  1460 */           break;
/*       */         default:
/*  1462 */           jj_la1[37] = jj_gen;
/*  1463 */           if (jj_2_44(2)) {
/*  1464 */             Token t = id();
/*  1465 */             nameToken = t;
/*  1466 */             fgCallback.structDeclBegin(t, kind, firstToken);
/*  1467 */             switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */             case 43:
/*  1469 */               base_clause();
/*  1470 */               break;
/*       */             default:
/*  1472 */               jj_la1[36] = jj_gen;
/*       */             }
/*       */ 
/*  1475 */             lCurlyBrace();
/*       */ 
/*  1478 */             while (jj_2_42(1))
/*       */             {
/*  1483 */               member_declaration();
/*       */             }
/*  1485 */             u = rCurlyBrace();
/*  1486 */             fgCallback.structDeclEnd(nameToken, u);
/*       */           } else {
/*  1488 */             switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */             case 164:
/*  1490 */               Token t = id();
/*  1491 */               if (!jj_2_43(2)) break;
/*  1492 */               lessThan();
/*  1493 */               template_argument_list();
/*  1494 */               greaterThan(); break;
/*       */             default:
/*  1500 */               jj_la1[38] = jj_gen;
/*  1501 */               jj_consume_token(-1);
/*  1502 */               throw new ParseException();
/*       */             }
/*       */           }
/*       */         }
/*       */       } catch (ParseException e) {
/*  1507 */         synchronize(44);
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  1510 */       if (jjtc000) {
/*  1511 */         jjtree.clearNodeScope(jjtn000);
/*  1512 */         jjtc000 = false;
/*       */       } else {
/*  1514 */         jjtree.popNode();
/*       */       }
/*  1516 */       if ((jjte000 instanceof RuntimeException)) {
/*  1517 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1519 */       if ((jjte000 instanceof ParseException)) {
/*  1520 */         throw ((ParseException)jjte000);
/*       */       }
/*  1522 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1524 */       if (jjtc000)
/*  1525 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void base_clause() throws ParseException
/*       */   {
/*  1531 */     colon();
/*  1532 */     base_specifier();
/*       */     while (true)
/*       */     {
/*  1535 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 45:
/*  1538 */         break;
/*       */       default:
/*  1540 */         jj_la1[39] = jj_gen;
/*  1541 */         break;
/*       */       }
/*  1543 */       comma();
/*  1544 */       base_specifier();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void base_specifier() throws ParseException
/*       */   {
/*  1550 */     ASTNode jjtn000 = new ASTNode(18);
/*  1551 */     boolean jjtc000 = true;
/*  1552 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  1554 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 111:
/*       */       case 112:
/*       */       case 113:
/*       */       case 131:
/*  1559 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 131:
/*  1561 */           virtualKeyword();
/*  1562 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */           case 111:
/*       */           case 112:
/*       */           case 113:
/*  1566 */             access_specifier();
/*  1567 */             break;
/*       */           default:
/*  1569 */             jj_la1[40] = jj_gen;
/*       */           }
/*       */ 
/*  1572 */           break;
/*       */         case 111:
/*       */         case 112:
/*       */         case 113:
/*  1576 */           access_specifier();
/*  1577 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */           case 131:
/*  1579 */             virtualKeyword();
/*  1580 */             break;
/*       */           default:
/*  1582 */             jj_la1[41] = jj_gen;
/*       */           }
/*       */ 
/*  1585 */           break;
/*       */         default:
/*  1587 */           jj_la1[42] = jj_gen;
/*  1588 */           jj_consume_token(-1);
/*  1589 */           throw new ParseException();
/*       */         }
/*       */ 
/*       */       default:
/*  1593 */         jj_la1[43] = jj_gen;
/*       */       }
/*       */ 
/*  1596 */       if (jj_2_45(2147483647)) {
/*  1597 */         scope_override();
/*       */       }
/*       */ 
/*  1601 */       Token t = id();
/*  1602 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 66:
/*  1604 */         lessThan();
/*  1605 */         template_argument_list();
/*  1606 */         greaterThan();
/*  1607 */         break;
/*       */       default:
/*  1609 */         jj_la1[44] = jj_gen;
/*       */       }
/*       */ 
/*  1612 */       jjtree.closeNodeScope(jjtn000, true);
/*  1613 */       jjtc000 = false;
/*  1614 */       fgCallback.superDecl(t.image);
/*       */     } catch (Throwable jjte000) {
/*  1616 */       if (jjtc000) {
/*  1617 */         jjtree.clearNodeScope(jjtn000);
/*  1618 */         jjtc000 = false;
/*       */       } else {
/*  1620 */         jjtree.popNode();
/*       */       }
/*  1622 */       if ((jjte000 instanceof RuntimeException)) {
/*  1623 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1625 */       if ((jjte000 instanceof ParseException)) {
/*  1626 */         throw ((ParseException)jjte000);
/*       */       }
/*  1628 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1630 */       if (jjtc000)
/*  1631 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token access_specifier()
/*       */     throws ParseException
/*       */   {
/*  1638 */     ASTNode jjtn000 = new ASTNode(19);
/*  1639 */     boolean jjtc000 = true;
/*  1640 */     jjtree.openNodeScope(jjtn000);
/*       */     try
/*       */     {
/*       */       Token t;
/*  1642 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 113:
/*  1644 */         t = publicKeyword();
/*  1645 */         break;
/*       */       case 112:
/*  1647 */         t = protectedKeyword();
/*  1648 */         break;
/*       */       case 111:
/*  1650 */         t = privateKeyword();
/*  1651 */         break;
/*       */       default:
/*  1653 */         jj_la1[45] = jj_gen;
/*  1654 */         jj_consume_token(-1);
/*  1655 */         throw new ParseException();
/*       */       }
/*  1657 */       jjtree.closeNodeScope(jjtn000, true);
/*  1658 */       jjtc000 = false;
/*  1659 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  1661 */       if (jjtc000) {
/*  1662 */         jjtree.clearNodeScope(jjtn000);
/*  1663 */         jjtc000 = false;
/*       */       } else {
/*  1665 */         jjtree.popNode();
/*       */       }
/*  1667 */       if ((jjte000 instanceof RuntimeException)) {
/*  1668 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1670 */       if ((jjte000 instanceof ParseException)) {
/*  1671 */         throw ((ParseException)jjte000);
/*       */       }
/*  1673 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1675 */       if (jjtc000)
/*  1676 */         jjtree.closeNodeScope(jjtn000, true); 
/*  1676 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void member_declaration()
/*       */     throws ParseException
/*       */   {
/*  1684 */     ASTNode jjtn000 = new ASTNode(20);
/*  1685 */     boolean jjtc000 = true;
/*  1686 */     jjtree.openNodeScope(jjtn000); boolean isTypedef = false;
/*  1687 */     Token firstToken = getToken(1);
/*       */     try
/*       */     {
/*       */       try
/*       */       {
/*  1693 */         if (jj_2_46(2147483647)) {
/*  1694 */           declaration(true, firstToken);
/*  1695 */         } else if (jj_2_47(2147483647)) {
/*  1696 */           enum_specifier();
/*  1697 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */           case 40:
/*       */           case 42:
/*       */           case 63:
/*       */           case 74:
/*       */           case 79:
/*       */           case 135:
/*       */           case 164:
/*  1705 */             member_declarator_list(false, firstToken);
/*  1706 */             break;
/*       */           default:
/*  1708 */             jj_la1[46] = jj_gen;
/*       */           }
/*       */ 
/*  1711 */           semicolon();
/*  1712 */         } else if (jj_2_48(2147483647)) {
/*  1713 */           conversion_function_decl_or_def(firstToken);
/*  1714 */         } else if (jj_2_49(2147483647)) {
/*  1715 */           dtor_definition(firstToken);
/*  1716 */         } else if (jj_2_50(2147483647)) {
/*  1717 */           dtor_ctor_decl_spec();
/*  1718 */           Token nameToken = simple_dtor_declarator(firstToken, "", null);
/*  1719 */           Token t = semicolon();
/*  1720 */           jjtn000.setEndToken(t);
/*  1721 */           fgCallback.functionDeclEnd(nameToken, t);
/*  1722 */         } else if (jj_2_51(2147483647)) {
/*  1723 */           ctor_definition(firstToken);
/*  1724 */         } else if (jj_2_52(2147483647)) {
/*  1725 */           dtor_ctor_decl_spec();
/*  1726 */           Token nameToken = ctor_declarator(firstToken);
/*  1727 */           Token t = semicolon();
/*  1728 */           jjtn000.setEndToken(t);
/*  1729 */           fgCallback.functionDeclEnd(nameToken, t);
/*  1730 */         } else if (jj_2_53(2147483647)) {
/*  1731 */           function_definition(firstToken);
/*  1732 */         } else if (jj_2_54(2147483647)) {
/*  1733 */           isTypedef = declaration_specifiers();
/*  1734 */           fgCallback.fieldMemberListDeclBegin(isTypedef, firstToken, getToken(0));
/*  1735 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */           case 40:
/*       */           case 42:
/*       */           case 63:
/*       */           case 74:
/*       */           case 79:
/*       */           case 135:
/*       */           case 164:
/*  1743 */             member_declarator_list(isTypedef, firstToken);
/*  1744 */             break;
/*       */           default:
/*  1746 */             jj_la1[47] = jj_gen;
/*       */           }
/*       */ 
/*  1749 */           semicolon();
/*  1750 */           fgCallback.fieldMemberListDeclEnd(isTypedef, firstToken, getToken(0));
/*  1751 */         } else if (jj_2_55(2147483647)) {
/*  1752 */           Token nameToken = function_declarator(false, firstToken);
/*  1753 */           Token t = semicolon();
/*  1754 */           jjtn000.setEndToken(t);
/*  1755 */           fgCallback.functionDeclEnd(nameToken, t);
/*  1756 */         } else if (jj_2_56(3)) {
/*  1757 */           qualified_id();
/*  1758 */           semicolon();
/*  1759 */         } else if (jj_2_57(2)) {
/*  1760 */           Token t = access_specifier();
/*  1761 */           colon();
/*  1762 */           fgCallback.accessSpecifierDecl(t, getToken(0));
/*       */         } else {
/*  1764 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */           case 44:
/*  1766 */             semicolon();
/*  1767 */             break;
/*       */           default:
/*  1769 */             jj_la1[48] = jj_gen;
/*  1770 */             jj_consume_token(-1);
/*  1771 */             throw new ParseException();
/*       */           }
/*       */         }
/*       */       } catch (ParseException e) {
/*  1775 */         synchronize(44);
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  1778 */       if (jjtc000) {
/*  1779 */         jjtree.clearNodeScope(jjtn000);
/*  1780 */         jjtc000 = false;
/*       */       } else {
/*  1782 */         jjtree.popNode();
/*       */       }
/*  1784 */       if ((jjte000 instanceof RuntimeException)) {
/*  1785 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1787 */       if ((jjte000 instanceof ParseException)) {
/*  1788 */         throw ((ParseException)jjte000);
/*       */       }
/*  1790 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1792 */       if (jjtc000)
/*  1793 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void member_declarator_list(boolean isTypedef, Token firstToken)
/*       */     throws ParseException
/*       */   {
/*  1801 */     Token nameToken = getToken(1);
/*  1802 */     member_declarator(isTypedef, firstToken);
/*  1803 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 48:
/*  1805 */       assignEqual();
/*  1806 */       assignment_expression();
/*  1807 */       break;
/*       */     default:
/*  1809 */       jj_la1[49] = jj_gen;
/*       */     }
/*       */ 
/*  1812 */     fgCallback.fieldMemberDecl(nameToken, firstToken, getToken(1));
/*       */     while (true)
/*       */     {
/*  1815 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 45:
/*  1818 */         break;
/*       */       default:
/*  1820 */         jj_la1[50] = jj_gen;
/*  1821 */         break;
/*       */       }
/*  1823 */       comma();
/*  1824 */       nameToken = getToken(1);
/*  1825 */       member_declarator(isTypedef, firstToken);
/*  1826 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 48:
/*  1828 */         assignEqual();
/*  1829 */         assignment_expression();
/*  1830 */         break;
/*       */       default:
/*  1832 */         jj_la1[51] = jj_gen;
/*       */       }
/*       */ 
/*  1835 */       fgCallback.fieldMemberDecl(nameToken, firstToken, getToken(1));
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token member_declarator(boolean isTypedef, Token firstToken)
/*       */     throws ParseException
/*       */   {
/*  1842 */     ASTNode jjtn000 = new ASTNode(21);
/*  1843 */     boolean jjtc000 = true;
/*  1844 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  1846 */       Token nameToken = declarator();
/*  1847 */       jjtree.closeNodeScope(jjtn000, true);
/*  1848 */       jjtc000 = false;
/*       */ 
/*  1850 */       Token localToken1 = nameToken;
/*       */       return localToken1;
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  1852 */       if (jjtc000) {
/*  1853 */         jjtree.clearNodeScope(jjtn000);
/*  1854 */         jjtc000 = false;
/*       */       } else {
/*  1856 */         jjtree.popNode();
/*       */       }
/*  1858 */       if ((jjte000 instanceof RuntimeException)) {
/*  1859 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1861 */       if ((jjte000 instanceof ParseException)) {
/*  1862 */         throw ((ParseException)jjte000);
/*       */       }
/*  1864 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1866 */       if (jjtc000)
/*  1867 */         jjtree.closeNodeScope(jjtn000, true); 
/*  1867 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void conversion_function_decl_or_def(Token firstToken)
/*       */     throws ParseException
/*       */   {
/*  1875 */     ASTNode jjtn000 = new ASTNode(22);
/*  1876 */     boolean jjtc000 = true;
/*  1877 */     jjtree.openNodeScope(jjtn000); String name = null;
/*  1878 */     StringBuffer buf = new StringBuffer();
/*       */ 
/*  1880 */     Token nameStart = getToken(1);
/*  1881 */     Token nameToken = null;
/*       */     try {
/*  1883 */       if (jj_2_58(2147483647)) {
/*  1884 */         name = scope_override();
/*  1885 */         buf.append(name);
/*       */       }
/*       */ 
/*  1889 */       Token t = jj_consume_token(135);
/*  1890 */       declaration_specifiers();
/*       */ 
/*  1892 */       Token s = getToken(1);
/*  1893 */       while (t != s) {
/*  1894 */         buf.append(t.image);
/*  1895 */         buf.append(' ');
/*  1896 */         t = t.next;
/*       */       }
/*  1898 */       nameToken = ParserCallback.createToken(buf.toString(), nameStart, s);
/*       */ 
/*  1900 */       jjtn000.setBeginToken(firstToken);
/*  1901 */       jjtn000.setSpecialToken(t.specialToken);
/*  1902 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  1907 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 63:
/*       */       case 74:
/*  1910 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 74:
/*  1912 */           star();
/*  1913 */           break;
/*       */         case 63:
/*  1915 */           ampersand();
/*  1916 */           break;
/*       */         default:
/*  1918 */           jj_la1[52] = jj_gen;
/*  1919 */           jj_consume_token(-1);
/*  1920 */           throw new ParseException();
/*       */         }
/*       */ 
/*       */       default:
/*  1924 */         jj_la1[53] = jj_gen;
/*       */       }
/*       */ 
/*  1927 */       lParenthesis();
/*  1928 */       if (jj_2_59(1)) {
/*  1929 */         parameter_list();
/*       */       }
/*       */ 
/*  1933 */       rParenthesis();
/*  1934 */       fgCallback.functionDeclBegin(nameToken, firstToken);
/*  1935 */       if (jj_2_60(2)) {
/*  1936 */         type_qualifier();
/*       */       }
/*       */ 
/*  1940 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 138:
/*  1942 */         exception_spec();
/*  1943 */         break;
/*       */       default:
/*  1945 */         jj_la1[54] = jj_gen;
/*       */       }
/*       */ 
/*  1948 */       func_decl_def(firstToken);
/*  1949 */       jjtree.closeNodeScope(jjtn000, true);
/*  1950 */       jjtc000 = false;
/*  1951 */       jjtn000.setEndToken(getToken(0));
/*  1952 */       fgCallback.functionDeclEnd(nameToken, getToken(0));
/*       */     } catch (Throwable jjte000) {
/*  1954 */       if (jjtc000) {
/*  1955 */         jjtree.clearNodeScope(jjtn000);
/*  1956 */         jjtc000 = false;
/*       */       } else {
/*  1958 */         jjtree.popNode();
/*       */       }
/*  1960 */       if ((jjte000 instanceof RuntimeException)) {
/*  1961 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  1963 */       if ((jjte000 instanceof ParseException)) {
/*  1964 */         throw ((ParseException)jjte000);
/*       */       }
/*  1966 */       throw ((Error)jjte000);
/*       */     } finally {
/*  1968 */       if (jjtc000)
/*  1969 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void enum_definition()
/*       */     throws ParseException
/*       */   {
/*  1976 */     ASTNode jjtn000 = new ASTNode(23);
/*  1977 */     boolean jjtc000 = true;
/*  1978 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(1);
/*       */     try {
/*  1980 */       enumKeyword();
/*  1981 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 36:
/*  1983 */         lCurlyBrace();
/*  1984 */         enumerator_list();
/*  1985 */         rCurlyBrace();
/*  1986 */         break;
/*       */       case 164:
/*  1988 */         id();
/*  1989 */         if (!jj_2_61(2)) break;
/*  1990 */         lCurlyBrace();
/*  1991 */         enumerator_list();
/*  1992 */         rCurlyBrace(); break;
/*       */       default:
/*  1998 */         jj_la1[55] = jj_gen;
/*  1999 */         jj_consume_token(-1);
/*  2000 */         throw new ParseException();
/*       */       }
/*  2002 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 40:
/*       */       case 42:
/*       */       case 63:
/*       */       case 74:
/*       */       case 79:
/*       */       case 87:
/*       */       case 88:
/*       */       case 91:
/*       */       case 97:
/*       */       case 102:
/*       */       case 108:
/*       */       case 109:
/*       */       case 117:
/*       */       case 118:
/*       */       case 121:
/*       */       case 130:
/*       */       case 132:
/*       */       case 135:
/*       */       case 139:
/*       */       case 140:
/*       */       case 164:
/*  2024 */         init_declarator_list(false, false, firstToken);
/*  2025 */         break;
/*       */       default:
/*  2027 */         jj_la1[56] = jj_gen;
/*       */       }
/*       */ 
/*  2030 */       semicolon();
/*       */     } catch (Throwable jjte000) {
/*  2032 */       if (jjtc000) {
/*  2033 */         jjtree.clearNodeScope(jjtn000);
/*  2034 */         jjtc000 = false;
/*       */       } else {
/*  2036 */         jjtree.popNode();
/*       */       }
/*  2038 */       if ((jjte000 instanceof RuntimeException)) {
/*  2039 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2041 */       if ((jjte000 instanceof ParseException)) {
/*  2042 */         throw ((ParseException)jjte000);
/*       */       }
/*  2044 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2046 */       if (jjtc000)
/*  2047 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void enum_specifier()
/*       */     throws ParseException
/*       */   {
/*  2054 */     ASTNode jjtn000 = new ASTNode(24);
/*  2055 */     boolean jjtc000 = true;
/*  2056 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2058 */       enumKeyword();
/*  2059 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 36:
/*  2061 */         lCurlyBrace();
/*  2062 */         enumerator_list();
/*  2063 */         rCurlyBrace();
/*  2064 */         break;
/*       */       case 164:
/*  2066 */         Token t = id();
/*  2067 */         if (!jj_2_62(2)) break;
/*  2068 */         lCurlyBrace();
/*  2069 */         enumerator_list();
/*  2070 */         rCurlyBrace(); break;
/*       */       default:
/*  2076 */         jj_la1[57] = jj_gen;
/*  2077 */         jj_consume_token(-1);
/*  2078 */         throw new ParseException();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  2081 */       if (jjtc000) {
/*  2082 */         jjtree.clearNodeScope(jjtn000);
/*  2083 */         jjtc000 = false;
/*       */       } else {
/*  2085 */         jjtree.popNode();
/*       */       }
/*  2087 */       if ((jjte000 instanceof RuntimeException)) {
/*  2088 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2090 */       if ((jjte000 instanceof ParseException)) {
/*  2091 */         throw ((ParseException)jjte000);
/*       */       }
/*  2093 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2095 */       if (jjtc000)
/*  2096 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void enumerator_list()
/*       */     throws ParseException
/*       */   {
/*  2103 */     ASTNode jjtn000 = new ASTNode(25);
/*  2104 */     boolean jjtc000 = true;
/*  2105 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2107 */       enumerator();
/*       */       while (true)
/*       */       {
/*  2110 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */         {
/*       */         case 45:
/*  2113 */           break;
/*       */         default:
/*  2115 */           jj_la1[58] = jj_gen;
/*  2116 */           break;
/*       */         }
/*  2118 */         comma();
/*  2119 */         enumerator();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  2122 */       if (jjtc000) {
/*  2123 */         jjtree.clearNodeScope(jjtn000);
/*  2124 */         jjtc000 = false;
/*       */       } else {
/*  2126 */         jjtree.popNode();
/*       */       }
/*  2128 */       if ((jjte000 instanceof RuntimeException)) {
/*  2129 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2131 */       if ((jjte000 instanceof ParseException)) {
/*  2132 */         throw ((ParseException)jjte000);
/*       */       }
/*  2134 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2136 */       if (jjtc000)
/*  2137 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void enumerator()
/*       */     throws ParseException
/*       */   {
/*  2144 */     ASTNode jjtn000 = new ASTNode(26);
/*  2145 */     boolean jjtc000 = true;
/*  2146 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2148 */       id();
/*  2149 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 48:
/*  2151 */         assignEqual();
/*  2152 */         constant_expression();
/*  2153 */         break;
/*       */       default:
/*  2155 */         jj_la1[59] = jj_gen;
/*       */       }
/*       */     }
/*       */     catch (Throwable jjte000) {
/*  2159 */       if (jjtc000) {
/*  2160 */         jjtree.clearNodeScope(jjtn000);
/*  2161 */         jjtc000 = false;
/*       */       } else {
/*  2163 */         jjtree.popNode();
/*       */       }
/*  2165 */       if ((jjte000 instanceof RuntimeException)) {
/*  2166 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2168 */       if ((jjte000 instanceof ParseException)) {
/*  2169 */         throw ((ParseException)jjte000);
/*       */       }
/*  2171 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2173 */       if (jjtc000)
/*  2174 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void ptr_operator()
/*       */     throws ParseException
/*       */   {
/*  2181 */     ASTNode jjtn000 = new ASTNode(27);
/*  2182 */     boolean jjtc000 = true;
/*  2183 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2185 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 63:
/*  2187 */         ampersand();
/*  2188 */         cv_qualifier_seq();
/*  2189 */         break;
/*       */       case 74:
/*  2191 */         star();
/*  2192 */         cv_qualifier_seq();
/*  2193 */         break;
/*       */       case 42:
/*       */       case 164:
/*  2196 */         ptr_to_member();
/*  2197 */         cv_qualifier_seq();
/*  2198 */         break;
/*       */       default:
/*  2200 */         jj_la1[60] = jj_gen;
/*  2201 */         jj_consume_token(-1);
/*  2202 */         throw new ParseException();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  2205 */       if (jjtc000) {
/*  2206 */         jjtree.clearNodeScope(jjtn000);
/*  2207 */         jjtc000 = false;
/*       */       } else {
/*  2209 */         jjtree.popNode();
/*       */       }
/*  2211 */       if ((jjte000 instanceof RuntimeException)) {
/*  2212 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2214 */       if ((jjte000 instanceof ParseException)) {
/*  2215 */         throw ((ParseException)jjte000);
/*       */       }
/*  2217 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2219 */       if (jjtc000)
/*  2220 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void cv_qualifier_seq()
/*       */     throws ParseException
/*       */   {
/*  2227 */     ASTNode jjtn000 = new ASTNode(28);
/*  2228 */     boolean jjtc000 = true;
/*  2229 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2231 */       if (jj_2_65(2)) {
/*  2232 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 92:
/*  2234 */           constKeyword();
/*  2235 */           if (!jj_2_63(2)) break;
/*  2236 */           volatileKeyword(); break;
/*       */         case 133:
/*  2242 */           volatileKeyword();
/*  2243 */           if (!jj_2_64(2)) break;
/*  2244 */           constKeyword(); break;
/*       */         default:
/*  2250 */           jj_la1[61] = jj_gen;
/*  2251 */           jj_consume_token(-1);
/*  2252 */           throw new ParseException();
/*       */         }
/*       */       }
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  2258 */       if (jjtc000) {
/*  2259 */         jjtree.clearNodeScope(jjtn000);
/*  2260 */         jjtc000 = false;
/*       */       } else {
/*  2262 */         jjtree.popNode();
/*       */       }
/*  2264 */       if ((jjte000 instanceof RuntimeException)) {
/*  2265 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2267 */       if ((jjte000 instanceof ParseException)) {
/*  2268 */         throw ((ParseException)jjte000);
/*       */       }
/*  2270 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2272 */       if (jjtc000)
/*  2273 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token declarator()
/*       */     throws ParseException
/*       */   {
/*  2280 */     ASTNode jjtn000 = new ASTNode(29);
/*  2281 */     boolean jjtc000 = true;
/*  2282 */     jjtree.openNodeScope(jjtn000);
/*       */     try
/*       */     {
/*       */       Token nameToken;
/*       */       Token nameToken;
/*  2284 */       if (jj_2_66(2147483647)) {
/*  2285 */         ptr_operator();
/*  2286 */         nameToken = declarator();
/*       */       } else {
/*  2288 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 40:
/*       */         case 42:
/*       */         case 79:
/*       */         case 135:
/*       */         case 164:
/*  2294 */           nameToken = direct_declarator();
/*  2295 */           break;
/*       */         default:
/*  2297 */           jj_la1[62] = jj_gen;
/*  2298 */           jj_consume_token(-1);
/*  2299 */           throw new ParseException();
/*       */         }
/*       */       }
/*  2302 */       jjtree.closeNodeScope(jjtn000, true);
/*  2303 */       jjtc000 = false;
/*  2304 */       jjtn000.setSpecialToken(nameToken.specialToken);
/*  2305 */       jjtn000.setParams(nameToken.image, nameToken.beginLine, nameToken.beginColumn, nameToken.endLine, nameToken.endColumn);
/*       */ 
/*  2308 */       Token localToken1 = nameToken;
/*       */       return localToken1;
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  2310 */       if (jjtc000) {
/*  2311 */         jjtree.clearNodeScope(jjtn000);
/*  2312 */         jjtc000 = false;
/*       */       } else {
/*  2314 */         jjtree.popNode();
/*       */       }
/*  2316 */       if ((jjte000 instanceof RuntimeException)) {
/*  2317 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2319 */       if ((jjte000 instanceof ParseException)) {
/*  2320 */         throw ((ParseException)jjte000);
/*       */       }
/*  2322 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2324 */       if (jjtc000)
/*  2325 */         jjtree.closeNodeScope(jjtn000, true); 
/*  2325 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token direct_declarator()
/*       */     throws ParseException
/*       */   {
/*  2333 */     if (jj_2_70(2)) {
/*  2334 */       tilde();
/*  2335 */       Token t = id();
/*  2336 */       if (jj_2_67(2)) {
/*  2337 */         declarator_suffixes();
/*       */       }
/*       */ 
/*  2341 */       return ParserCallback.createToken("~" + t.image, t);
/*       */     }
/*       */     Token t;
/*  2343 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 40:
/*  2345 */       lParenthesis();
/*  2346 */       t = declarator();
/*  2347 */       rParenthesis();
/*  2348 */       if (jj_2_68(2)) {
/*  2349 */         declarator_suffixes();
/*       */       }
/*       */ 
/*  2353 */       return t;
/*       */     case 42:
/*       */     case 135:
/*       */     case 164:
/*  2358 */       t = qualified_id();
/*  2359 */       if (jj_2_69(2)) {
/*  2360 */         declarator_suffixes();
/*       */       }
/*       */ 
/*  2364 */       return t;
/*       */     }
/*       */ 
/*  2367 */     jj_la1[63] = jj_gen;
/*  2368 */     jj_consume_token(-1);
/*  2369 */     throw new ParseException();
/*       */   }
/*       */ 
/*       */   public static final void declarator_suffixes()
/*       */     throws ParseException
/*       */   {
/*  2377 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */     {
/*       */     case 38:
/*       */       while (true) {
/*  2381 */         lSquareBracket();
/*  2382 */         if (jj_2_71(1)) {
/*  2383 */           constant_expression();
/*       */           while (true)
/*       */           {
/*  2386 */             switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */             {
/*       */             case 45:
/*  2389 */               break;
/*       */             default:
/*  2391 */               jj_la1[64] = jj_gen;
/*  2392 */               break;
/*       */             }
/*  2394 */             comma();
/*  2395 */             constant_expression();
/*       */           }
/*       */ 
/*       */         }
/*       */ 
/*  2400 */         rSquareBracket();
/*  2401 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */         {
/*       */         case 38:
/*       */         }
/*       */       }
/*  2406 */       jj_la1[65] = jj_gen;
/*  2407 */       break;
/*       */     case 40:
/*  2412 */       lParenthesis();
/*  2413 */       if (jj_2_72(1)) {
/*  2414 */         parameter_list();
/*       */       }
/*       */ 
/*  2418 */       rParenthesis();
/*  2419 */       if (jj_2_73(2)) {
/*  2420 */         type_qualifier();
/*       */       }
/*       */ 
/*  2424 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 138:
/*  2426 */         exception_spec();
/*  2427 */         break;
/*       */       default:
/*  2429 */         jj_la1[66] = jj_gen;
/*       */       }
/*       */ 
/*  2432 */       break;
/*       */     default:
/*  2434 */       jj_la1[67] = jj_gen;
/*  2435 */       jj_consume_token(-1);
/*  2436 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void function_declarator_lookahead()
/*       */     throws ParseException
/*       */   {
/*  2446 */     while (jj_2_74(2))
/*       */     {
/*  2451 */       ptr_operator();
/*       */     }
/*  2453 */     qualified_id();
/*  2454 */     lParenthesis();
/*       */   }
/*       */ 
/*       */   public static final Token function_declarator(boolean isTypedef, Token firstToken)
/*       */     throws ParseException
/*       */   {
/*       */     Token nameToken;
/*       */     Token nameToken;
/*  2460 */     if (jj_2_75(2147483647)) {
/*  2461 */       ptr_operator();
/*  2462 */       nameToken = function_declarator(isTypedef, firstToken);
/*       */     } else {
/*  2464 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 42:
/*       */       case 135:
/*       */       case 164:
/*  2468 */         nameToken = function_direct_declarator(isTypedef, firstToken);
/*  2469 */         break;
/*       */       default:
/*  2471 */         jj_la1[68] = jj_gen;
/*  2472 */         jj_consume_token(-1);
/*  2473 */         throw new ParseException();
/*       */       }
/*       */     }
/*  2476 */     return nameToken;
/*       */   }
/*       */ 
/*       */   public static final Token function_direct_declarator(boolean isTypedef, Token firstToken)
/*       */     throws ParseException
/*       */   {
/*  2483 */     Token nameToken = qualified_id();
/*       */ 
/*  2485 */     lParenthesis();
/*  2486 */     if (jj_2_76(1)) {
/*  2487 */       parameter_list();
/*       */     }
/*       */ 
/*  2491 */     rParenthesis();
/*  2492 */     fgCallback.functionDeclBegin(nameToken, firstToken);
/*  2493 */     if (jj_2_77(2)) {
/*  2494 */       type_qualifier();
/*       */     }
/*       */ 
/*  2498 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 138:
/*  2500 */       exception_spec();
/*  2501 */       break;
/*       */     default:
/*  2503 */       jj_la1[69] = jj_gen;
/*       */     }
/*       */ 
/*  2506 */     if (jj_2_78(2147483647)) {
/*  2507 */       assignEqual();
/*  2508 */       jj_consume_token(145);
/*       */     }
/*       */ 
/*  2512 */     return nameToken;
/*       */   }
/*       */ 
/*       */   public static final void dtor_ctor_decl_spec() throws ParseException
/*       */   {
/*  2517 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 107:
/*       */     case 131:
/*  2520 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 131:
/*  2522 */         virtualKeyword();
/*  2523 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 107:
/*  2525 */           inlineKeyword();
/*  2526 */           break;
/*       */         default:
/*  2528 */           jj_la1[70] = jj_gen;
/*       */         }
/*       */ 
/*  2531 */         break;
/*       */       case 107:
/*  2533 */         inlineKeyword();
/*  2534 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 131:
/*  2536 */           virtualKeyword();
/*  2537 */           break;
/*       */         default:
/*  2539 */           jj_la1[71] = jj_gen;
/*       */         }
/*       */ 
/*  2542 */         break;
/*       */       default:
/*  2544 */         jj_la1[72] = jj_gen;
/*  2545 */         jj_consume_token(-1);
/*  2546 */         throw new ParseException();
/*       */       }
/*       */ 
/*       */     default:
/*  2550 */       jj_la1[73] = jj_gen;
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void dtor_definition(Token firstToken)
/*       */     throws ParseException
/*       */   {
/*  2557 */     ASTNode jjtn000 = new ASTNode(30);
/*  2558 */     boolean jjtc000 = true;
/*  2559 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2561 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 125:
/*  2563 */         template_head();
/*  2564 */         break;
/*       */       default:
/*  2566 */         jj_la1[74] = jj_gen;
/*       */       }
/*       */ 
/*  2569 */       dtor_ctor_decl_spec();
/*  2570 */       dtor_declarator(firstToken);
/*  2571 */       compound_statement();
/*       */     } catch (Throwable jjte000) {
/*  2573 */       if (jjtc000) {
/*  2574 */         jjtree.clearNodeScope(jjtn000);
/*  2575 */         jjtc000 = false;
/*       */       } else {
/*  2577 */         jjtree.popNode();
/*       */       }
/*  2579 */       if ((jjte000 instanceof RuntimeException)) {
/*  2580 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2582 */       if ((jjte000 instanceof ParseException)) {
/*  2583 */         throw ((ParseException)jjte000);
/*       */       }
/*  2585 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2587 */       if (jjtc000)
/*  2588 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void ctor_definition(Token firstToken)
/*       */     throws ParseException
/*       */   {
/*  2595 */     ASTNode jjtn000 = new ASTNode(31);
/*  2596 */     boolean jjtc000 = true;
/*  2597 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2599 */       dtor_ctor_decl_spec();
/*  2600 */       Token nameToken = ctor_declarator(firstToken);
/*  2601 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 138:
/*  2603 */         exception_spec();
/*  2604 */         break;
/*       */       default:
/*  2606 */         jj_la1[75] = jj_gen;
/*       */       }
/*       */ 
/*  2609 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 44:
/*  2611 */         semicolon();
/*  2612 */         break;
/*       */       case 36:
/*       */       case 43:
/*  2615 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 43:
/*  2617 */           ctor_initializer();
/*  2618 */           break;
/*       */         default:
/*  2620 */           jj_la1[76] = jj_gen;
/*       */         }
/*       */ 
/*  2623 */         compound_statement();
/*  2624 */         break;
/*       */       default:
/*  2626 */         jj_la1[77] = jj_gen;
/*  2627 */         jj_consume_token(-1);
/*  2628 */         throw new ParseException();
/*       */       }
/*  2630 */       jjtree.closeNodeScope(jjtn000, true);
/*  2631 */       jjtc000 = false;
/*  2632 */       jjtn000.setEndToken(getToken(0));
/*  2633 */       fgCallback.functionDeclEnd(nameToken, getToken(0));
/*       */     } catch (Throwable jjte000) {
/*  2635 */       if (jjtc000) {
/*  2636 */         jjtree.clearNodeScope(jjtn000);
/*  2637 */         jjtc000 = false;
/*       */       } else {
/*  2639 */         jjtree.popNode();
/*       */       }
/*  2641 */       if ((jjte000 instanceof RuntimeException)) {
/*  2642 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2644 */       if ((jjte000 instanceof ParseException)) {
/*  2645 */         throw ((ParseException)jjte000);
/*       */       }
/*  2647 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2649 */       if (jjtc000)
/*  2650 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void ctor_declarator_lookahead() throws ParseException
/*       */   {
/*  2656 */     if (!isCtor())
/*       */     {
/*  2659 */       jj_consume_token(-1);
/*  2660 */       throw new ParseException();
/*       */     }
/*  2662 */     qualified_id();
/*  2663 */     lParenthesis();
/*       */   }
/*       */ 
/*       */   public static final Token ctor_declarator(Token firstToken)
/*       */     throws ParseException
/*       */   {
/*  2669 */     ASTNode jjtn000 = new ASTNode(32);
/*  2670 */     boolean jjtc000 = true;
/*  2671 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2673 */       if (!isCtor())
/*       */       {
/*  2676 */         jj_consume_token(-1);
/*  2677 */         throw new ParseException();
/*       */       }
/*  2679 */       Token nameToken = qualified_id();
/*  2680 */       jjtn000.setBeginToken(firstToken);
/*  2681 */       jjtn000.setSpecialToken(nameToken.specialToken);
/*  2682 */       jjtn000.setParams(nameToken.image, nameToken.beginLine, nameToken.beginColumn, nameToken.endLine, nameToken.endColumn);
/*       */ 
/*  2686 */       lParenthesis();
/*  2687 */       if (jj_2_79(2)) {
/*  2688 */         parameter_list();
/*       */       }
/*       */ 
/*  2692 */       rParenthesis();
/*  2693 */       fgCallback.functionDeclBegin(nameToken, firstToken);
/*  2694 */       if (jj_2_80(2)) {
/*  2695 */         exception_spec();
/*       */       }
/*       */ 
/*  2699 */       jjtree.closeNodeScope(jjtn000, true);
/*  2700 */       jjtc000 = false;
/*  2701 */       Token localToken1 = nameToken;
/*       */       return localToken1;
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  2703 */       if (jjtc000) {
/*  2704 */         jjtree.clearNodeScope(jjtn000);
/*  2705 */         jjtc000 = false;
/*       */       } else {
/*  2707 */         jjtree.popNode();
/*       */       }
/*  2709 */       if ((jjte000 instanceof RuntimeException)) {
/*  2710 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2712 */       if ((jjte000 instanceof ParseException)) {
/*  2713 */         throw ((ParseException)jjte000);
/*       */       }
/*  2715 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2717 */       if (jjtc000)
/*  2718 */         jjtree.closeNodeScope(jjtn000, true); 
/*  2718 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void ctor_initializer()
/*       */     throws ParseException
/*       */   {
/*  2726 */     ASTNode jjtn000 = new ASTNode(33);
/*  2727 */     boolean jjtc000 = true;
/*  2728 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2730 */       colon();
/*  2731 */       superclass_init();
/*       */       while (true)
/*       */       {
/*  2734 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */         {
/*       */         case 45:
/*  2737 */           break;
/*       */         default:
/*  2739 */           jj_la1[78] = jj_gen;
/*  2740 */           break;
/*       */         }
/*  2742 */         comma();
/*  2743 */         superclass_init();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  2746 */       if (jjtc000) {
/*  2747 */         jjtree.clearNodeScope(jjtn000);
/*  2748 */         jjtc000 = false;
/*       */       } else {
/*  2750 */         jjtree.popNode();
/*       */       }
/*  2752 */       if ((jjte000 instanceof RuntimeException)) {
/*  2753 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2755 */       if ((jjte000 instanceof ParseException)) {
/*  2756 */         throw ((ParseException)jjte000);
/*       */       }
/*  2758 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2760 */       if (jjtc000)
/*  2761 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void superclass_init()
/*       */     throws ParseException
/*       */   {
/*  2768 */     ASTNode jjtn000 = new ASTNode(34);
/*  2769 */     boolean jjtc000 = true;
/*  2770 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2772 */       qualified_id();
/*  2773 */       lParenthesis();
/*  2774 */       if (jj_2_81(1)) {
/*  2775 */         expression_list();
/*       */       }
/*       */ 
/*  2779 */       rParenthesis();
/*       */     } catch (Throwable jjte000) {
/*  2781 */       if (jjtc000) {
/*  2782 */         jjtree.clearNodeScope(jjtn000);
/*  2783 */         jjtc000 = false;
/*       */       } else {
/*  2785 */         jjtree.popNode();
/*       */       }
/*  2787 */       if ((jjte000 instanceof RuntimeException)) {
/*  2788 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2790 */       if ((jjte000 instanceof ParseException)) {
/*  2791 */         throw ((ParseException)jjte000);
/*       */       }
/*  2793 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2795 */       if (jjtc000)
/*  2796 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token dtor_declarator(Token firstToken)
/*       */     throws ParseException
/*       */   {
/*  2803 */     ASTNode jjtn000 = new ASTNode(35);
/*  2804 */     boolean jjtc000 = true;
/*  2805 */     jjtree.openNodeScope(jjtn000); Token firstNameToken = getToken(1);
/*  2806 */     String name = "";
/*       */     try {
/*  2808 */       if (jj_2_82(2147483647)) {
/*  2809 */         name = scope_override();
/*       */       }
/*       */ 
/*  2813 */       simple_dtor_declarator(firstToken, name, firstNameToken);
/*  2814 */       jjtree.closeNodeScope(jjtn000, true);
/*  2815 */       jjtc000 = false;
/*  2816 */       jjtn000.setEndToken(getToken(0));
/*  2817 */       fgCallback.functionDeclEnd(firstNameToken, getToken(0));
/*  2818 */       Token localToken1 = firstNameToken;
/*       */       return localToken1;
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  2820 */       if (jjtc000) {
/*  2821 */         jjtree.clearNodeScope(jjtn000);
/*  2822 */         jjtc000 = false;
/*       */       } else {
/*  2824 */         jjtree.popNode();
/*       */       }
/*  2826 */       if ((jjte000 instanceof RuntimeException)) {
/*  2827 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2829 */       if ((jjte000 instanceof ParseException)) {
/*  2830 */         throw ((ParseException)jjte000);
/*       */       }
/*  2832 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2834 */       if (jjtc000)
/*  2835 */         jjtree.closeNodeScope(jjtn000, true); 
/*  2835 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token simple_dtor_declarator(Token firstToken, String name, Token firstNameToken)
/*       */     throws ParseException
/*       */   {
/*  2845 */     tilde();
/*  2846 */     if (!isCtor())
/*       */     {
/*  2849 */       jj_consume_token(-1);
/*  2850 */       throw new ParseException();
/*       */     }
/*  2852 */     Token t = id();
/*  2853 */     lParenthesis();
/*  2854 */     if (jj_2_83(1)) {
/*  2855 */       parameter_list();
/*       */     }
/*       */ 
/*  2859 */     rParenthesis();
/*  2860 */     if (firstToken != null) {
/*  2861 */       if (firstNameToken == null) {
/*  2862 */         firstNameToken = t;
/*       */       }
/*  2864 */       Token nameToken = ParserCallback.createToken(name + "~" + t.image, firstNameToken, t);
/*  2865 */       fgCallback.functionDeclBegin(nameToken, firstToken);
/*  2866 */       return nameToken;
/*       */     }
/*  2868 */     throw new Error("Missing return statement in function");
/*       */   }
/*       */ 
/*       */   public static final void parameter_list()
/*       */     throws ParseException
/*       */   {
/*  2880 */     ASTNode jjtn000 = new ASTNode(36);
/*  2881 */     boolean jjtc000 = true;
/*  2882 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  2884 */       if (jj_2_85(1)) {
/*  2885 */         parameter_declaration_list();
/*  2886 */         if (jj_2_84(2)) {
/*  2887 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */           case 45:
/*  2889 */             comma();
/*  2890 */             break;
/*       */           default:
/*  2892 */             jj_la1[79] = jj_gen;
/*       */           }
/*       */ 
/*  2895 */           ellipsis();
/*       */         }
/*       */       }
/*       */       else
/*       */       {
/*  2900 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 47:
/*  2902 */           ellipsis();
/*  2903 */           break;
/*       */         default:
/*  2905 */           jj_la1[80] = jj_gen;
/*  2906 */           jj_consume_token(-1);
/*  2907 */           throw new ParseException();
/*       */         }
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  2911 */       if (jjtc000) {
/*  2912 */         jjtree.clearNodeScope(jjtn000);
/*  2913 */         jjtc000 = false;
/*       */       } else {
/*  2915 */         jjtree.popNode();
/*       */       }
/*  2917 */       if ((jjte000 instanceof RuntimeException)) {
/*  2918 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2920 */       if ((jjte000 instanceof ParseException)) {
/*  2921 */         throw ((ParseException)jjte000);
/*       */       }
/*  2923 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2925 */       if (jjtc000)
/*  2926 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void parameter_declaration_list() throws ParseException
/*       */   {
/*  2932 */     parameter_declaration();
/*       */ 
/*  2935 */     while (jj_2_86(2))
/*       */     {
/*  2940 */       comma();
/*  2941 */       parameter_declaration();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void parameter_declaration() throws ParseException
/*       */   {
/*  2947 */     ASTNode jjtn000 = new ASTNode(37);
/*  2948 */     boolean jjtc000 = true;
/*  2949 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(1);
/*  2950 */     jjtn000.setBeginToken(firstToken);
/*       */     try {
/*  2952 */       declaration_specifiers();
/*  2953 */       if (jj_2_87(2147483647))
/*  2954 */         declarator();
/*       */       else {
/*  2956 */         abstract_declarator();
/*       */       }
/*  2958 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 48:
/*  2960 */         assignEqual();
/*  2961 */         assignment_expression();
/*  2962 */         break;
/*       */       default:
/*  2964 */         jj_la1[81] = jj_gen;
/*       */       }
/*       */ 
/*  2967 */       jjtree.closeNodeScope(jjtn000, true);
/*  2968 */       jjtc000 = false;
/*  2969 */       jjtn000.setEndToken(getToken(0));
/*       */     } catch (Throwable jjte000) {
/*  2971 */       if (jjtc000) {
/*  2972 */         jjtree.clearNodeScope(jjtn000);
/*  2973 */         jjtc000 = false;
/*       */       } else {
/*  2975 */         jjtree.popNode();
/*       */       }
/*  2977 */       if ((jjte000 instanceof RuntimeException)) {
/*  2978 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  2980 */       if ((jjte000 instanceof ParseException)) {
/*  2981 */         throw ((ParseException)jjte000);
/*       */       }
/*  2983 */       throw ((Error)jjte000);
/*       */     } finally {
/*  2985 */       if (jjtc000)
/*  2986 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void initializer() throws ParseException
/*       */   {
/*  2992 */     if (jj_2_88(3)) {
/*  2993 */       lCurlyBrace();
/*  2994 */       initializer();
/*       */       while (true)
/*       */       {
/*  2997 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */         {
/*       */         case 45:
/*  3000 */           break;
/*       */         default:
/*  3002 */           jj_la1[82] = jj_gen;
/*  3003 */           break;
/*       */         }
/*  3005 */         comma();
/*  3006 */         initializer();
/*       */       }
/*  3008 */       rCurlyBrace();
/*       */     }
/*  3010 */     else if (jj_2_89(3)) {
/*  3011 */       datetimeInitializer();
/*  3012 */     } else if (jj_2_90(3)) {
/*  3013 */       colorInitializer();
/*  3014 */     } else if (jj_2_91(1)) {
/*  3015 */       assignment_expression();
/*       */     } else {
/*  3017 */       jj_consume_token(-1);
/*  3018 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void type_name() throws ParseException {
/*  3023 */     declaration_specifiers();
/*  3024 */     abstract_declarator();
/*       */   }
/*       */ 
/*       */   public static final void abstract_declarator() throws ParseException {
/*  3028 */     if (jj_2_93(2))
/*  3029 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 40:
/*  3031 */         lParenthesis();
/*  3032 */         abstract_declarator();
/*  3033 */         rParenthesis();
/*       */         while (true)
/*       */         {
/*  3036 */           abstract_declarator_suffix();
/*  3037 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */           {
/*       */           case 38:
/*       */           case 40:
/*       */           }
/*       */         }
/*  3043 */         jj_la1[83] = jj_gen;
/*  3044 */         break;
/*       */       case 38:
/*       */         while (true)
/*       */         {
/*  3051 */           lSquareBracket();
/*  3052 */           if (jj_2_92(1)) {
/*  3053 */             constant_expression();
/*       */             while (true)
/*       */             {
/*  3056 */               switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */               {
/*       */               case 45:
/*  3059 */                 break;
/*       */               default:
/*  3061 */                 jj_la1[84] = jj_gen;
/*  3062 */                 break;
/*       */               }
/*  3064 */               comma();
/*  3065 */               constant_expression();
/*       */             }
/*       */ 
/*       */           }
/*       */ 
/*  3070 */           rSquareBracket();
/*  3071 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */           {
/*       */           case 38:
/*       */           }
/*       */         }
/*  3076 */         jj_la1[85] = jj_gen;
/*  3077 */         break;
/*       */       case 42:
/*       */       case 63:
/*       */       case 74:
/*       */       case 164:
/*  3085 */         ptr_operator();
/*  3086 */         abstract_declarator();
/*  3087 */         break;
/*       */       default:
/*  3089 */         jj_la1[86] = jj_gen;
/*  3090 */         jj_consume_token(-1);
/*  3091 */         throw new ParseException();
/*       */       }
/*       */   }
/*       */ 
/*       */   public static final void abstract_declarator_suffix()
/*       */     throws ParseException
/*       */   {
/*  3099 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 38:
/*  3101 */       lSquareBracket();
/*  3102 */       if (jj_2_94(1)) {
/*  3103 */         constant_expression();
/*       */         while (true)
/*       */         {
/*  3106 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */           {
/*       */           case 45:
/*  3109 */             break;
/*       */           default:
/*  3111 */             jj_la1[87] = jj_gen;
/*  3112 */             break;
/*       */           }
/*  3114 */           comma();
/*  3115 */           constant_expression();
/*       */         }
/*       */ 
/*       */       }
/*       */ 
/*  3120 */       rSquareBracket();
/*  3121 */       break;
/*       */     case 40:
/*  3123 */       lParenthesis();
/*  3124 */       if (jj_2_95(1)) {
/*  3125 */         parameter_list();
/*       */       }
/*       */ 
/*  3129 */       rParenthesis();
/*  3130 */       break;
/*       */     default:
/*  3132 */       jj_la1[88] = jj_gen;
/*  3133 */       jj_consume_token(-1);
/*  3134 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void template_head() throws ParseException {
/*  3139 */     templateKeyword();
/*  3140 */     lessThan();
/*  3141 */     template_parameter_list();
/*  3142 */     greaterThan();
/*       */   }
/*       */ 
/*       */   public static final void template_parameter_list() throws ParseException {
/*  3146 */     template_parameter();
/*       */     while (true)
/*       */     {
/*  3149 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 45:
/*  3152 */         break;
/*       */       default:
/*  3154 */         jj_la1[89] = jj_gen;
/*  3155 */         break;
/*       */       }
/*  3157 */       comma();
/*  3158 */       template_parameter();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void template_parameter() throws ParseException
/*       */   {
/*  3164 */     if (jj_2_96(3)) {
/*  3165 */       classKeyword();
/*  3166 */       id();
/*  3167 */     } else if (jj_2_97(1)) {
/*  3168 */       parameter_declaration();
/*       */     } else {
/*  3170 */       jj_consume_token(-1);
/*  3171 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void template_id() throws ParseException {
/*  3176 */     id();
/*  3177 */     lessThan();
/*  3178 */     template_argument_list();
/*  3179 */     greaterThan();
/*       */   }
/*       */ 
/*       */   public static final void template_argument_list() throws ParseException {
/*  3183 */     template_argument();
/*       */     while (true)
/*       */     {
/*  3186 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 45:
/*  3189 */         break;
/*       */       default:
/*  3191 */         jj_la1[90] = jj_gen;
/*  3192 */         break;
/*       */       }
/*  3194 */       comma();
/*  3195 */       template_argument();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void template_argument() throws ParseException {
/*  3200 */     if (jj_2_98(3)) {
/*  3201 */       type_name();
/*  3202 */     } else if (jj_2_99(1)) {
/*  3203 */       shift_expression();
/*       */     } else {
/*  3205 */       jj_consume_token(-1);
/*  3206 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void statement_list() throws ParseException
/*       */   {
/*  3212 */     ASTNode jjtn000 = new ASTNode(38);
/*  3213 */     boolean jjtc000 = true;
/*  3214 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(0);
/*  3215 */     jjtn000.setBeginToken(firstToken);
/*       */     try
/*       */     {
/*       */       do
/*  3219 */         statement();
/*  3220 */       while (jj_2_100(2147483647));
/*       */ 
/*  3226 */       jjtree.closeNodeScope(jjtn000, true);
/*  3227 */       jjtc000 = false;
/*       */     }
/*       */     catch (Throwable jjte000) {
/*  3230 */       if (jjtc000) {
/*  3231 */         jjtree.clearNodeScope(jjtn000);
/*  3232 */         jjtc000 = false;
/*       */       } else {
/*  3234 */         jjtree.popNode();
/*       */       }
/*  3236 */       if ((jjte000 instanceof RuntimeException)) {
/*  3237 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3239 */       if ((jjte000 instanceof ParseException)) {
/*  3240 */         throw ((ParseException)jjte000);
/*       */       }
/*  3242 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3244 */       if (jjtc000)
/*  3245 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void statement()
/*       */     throws ParseException
/*       */   {
/*  3252 */     ASTNode jjtn000 = new ASTNode(39);
/*  3253 */     boolean jjtc000 = true;
/*  3254 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(1);
/*  3255 */     jjtn000.setBeginToken(firstToken);
/*       */     try {
/*  3257 */       if (jj_2_101(2147483647)) {
/*  3258 */         declaration(false, null);
/*  3259 */       } else if (jj_2_102(2147483647)) {
/*  3260 */         expression();
/*  3261 */         semicolon();
/*  3262 */         jjtree.closeNodeScope(jjtn000, true);
/*  3263 */         jjtc000 = false;
/*  3264 */         jjtn000.setEndToken(getToken(0));
/*  3265 */         fgCallback.expressionSelection(firstToken, getToken(0));
/*       */       } else {
/*  3267 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 36:
/*  3269 */           compound_statement();
/*  3270 */           break;
/*       */         case 106:
/*       */         case 124:
/*  3273 */           selection_statement();
/*  3274 */           break;
/*       */         case 86:
/*       */         case 93:
/*       */         case 105:
/*       */         case 116:
/*  3279 */           jump_statement();
/*  3280 */           break;
/*       */         case 44:
/*  3282 */           semicolon();
/*  3283 */           break;
/*       */         case 127:
/*  3285 */           try_block();
/*  3286 */           break;
/*       */         case 138:
/*  3288 */           throw_statement();
/*  3289 */           break;
/*       */         default:
/*  3291 */           jj_la1[91] = jj_gen;
/*  3292 */           if (jj_2_103(2))
/*  3293 */             labeled_statement();
/*       */           else
/*  3295 */             switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */             case 96:
/*       */             case 103:
/*       */             case 134:
/*  3299 */               iteration_statement();
/*  3300 */               break;
/*       */             default:
/*  3302 */               jj_la1[92] = jj_gen;
/*  3303 */               jj_consume_token(-1);
/*  3304 */               throw new ParseException();
/*       */             }
/*       */         }
/*       */       }
/*       */     }
/*       */     catch (Throwable jjte000) {
/*  3310 */       if (jjtc000) {
/*  3311 */         jjtree.clearNodeScope(jjtn000);
/*  3312 */         jjtc000 = false;
/*       */       } else {
/*  3314 */         jjtree.popNode();
/*       */       }
/*  3316 */       if ((jjte000 instanceof RuntimeException)) {
/*  3317 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3319 */       if ((jjte000 instanceof ParseException)) {
/*  3320 */         throw ((ParseException)jjte000);
/*       */       }
/*  3322 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3324 */       if (jjtc000)
/*  3325 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void labeled_statement()
/*       */     throws ParseException
/*       */   {
/*  3332 */     ASTNode jjtn000 = new ASTNode(40);
/*  3333 */     boolean jjtc000 = true;
/*  3334 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(0);
/*  3335 */     jjtn000.setBeginToken(firstToken);
/*       */     try {
/*  3337 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 164:
/*  3339 */         id();
/*       */ 
/*  3341 */         colon();
/*  3342 */         statement();
/*  3343 */         break;
/*       */       case 89:
/*  3345 */         caseKeyword();
/*  3346 */         fgCallback.caseStatementBegin(firstToken);
/*  3347 */         constant_expression();
/*  3348 */         colon();
/*  3349 */         statement();
/*       */         while (true)
/*       */         {
/*  3352 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */           {
/*       */           case 86:
/*       */           case 93:
/*       */           case 105:
/*       */           case 116:
/*  3358 */             break;
/*       */           default:
/*  3360 */             jj_la1[93] = jj_gen;
/*  3361 */             break;
/*       */           }
/*  3363 */           jump_statement();
/*       */         }
/*  3365 */         jjtree.closeNodeScope(jjtn000, true);
/*  3366 */         jjtc000 = false;
/*  3367 */         fgCallback.caseStatementEnd(getToken(0));
/*  3368 */         break;
/*       */       case 94:
/*  3370 */         defaultKeyword();
/*  3371 */         fgCallback.defaultStatementBegin(firstToken);
/*  3372 */         colon();
/*  3373 */         statement();
/*       */         while (true)
/*       */         {
/*  3376 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */           {
/*       */           case 86:
/*       */           case 93:
/*       */           case 105:
/*       */           case 116:
/*  3382 */             break;
/*       */           default:
/*  3384 */             jj_la1[94] = jj_gen;
/*  3385 */             break;
/*       */           }
/*  3387 */           jump_statement();
/*       */         }
/*  3389 */         jjtree.closeNodeScope(jjtn000, true);
/*  3390 */         jjtc000 = false;
/*  3391 */         fgCallback.defaultStatementEnd(getToken(0));
/*  3392 */         break;
/*       */       default:
/*  3394 */         jj_la1[95] = jj_gen;
/*  3395 */         jj_consume_token(-1);
/*  3396 */         throw new ParseException();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  3399 */       if (jjtc000) {
/*  3400 */         jjtree.clearNodeScope(jjtn000);
/*  3401 */         jjtc000 = false;
/*       */       } else {
/*  3403 */         jjtree.popNode();
/*       */       }
/*  3405 */       if ((jjte000 instanceof RuntimeException)) {
/*  3406 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3408 */       if ((jjte000 instanceof ParseException)) {
/*  3409 */         throw ((ParseException)jjte000);
/*       */       }
/*  3411 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3413 */       if (jjtc000)
/*  3414 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void compound_statement()
/*       */     throws ParseException
/*       */   {
/*  3440 */     ASTNode jjtn000 = new ASTNode(41);
/*  3441 */     boolean jjtc000 = true;
/*  3442 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(0);
/*  3443 */     jjtn000.setBeginToken(firstToken);
/*       */     try {
/*  3445 */       lCurlyBrace();
/*  3446 */       if (jj_2_104(1)) {
/*  3447 */         statement_list();
/*       */       }
/*       */ 
/*  3451 */       rCurlyBrace();
/*       */     } catch (Throwable jjte000) {
/*  3453 */       if (jjtc000) {
/*  3454 */         jjtree.clearNodeScope(jjtn000);
/*  3455 */         jjtc000 = false;
/*       */       } else {
/*  3457 */         jjtree.popNode();
/*       */       }
/*  3459 */       if ((jjte000 instanceof RuntimeException)) {
/*  3460 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3462 */       if ((jjte000 instanceof ParseException)) {
/*  3463 */         throw ((ParseException)jjte000);
/*       */       }
/*  3465 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3467 */       if (jjtc000)
/*  3468 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void selection_statement()
/*       */     throws ParseException
/*       */   {
/*  3475 */     ASTNode jjtn000 = new ASTNode(42);
/*  3476 */     boolean jjtc000 = true;
/*  3477 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(1);
/*  3478 */     Token lastToken = null;
/*  3479 */     Token elseToken = null;
/*  3480 */     jjtn000.setBeginToken(firstToken);
/*       */     try {
/*  3482 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 106:
/*  3484 */         ifKeyword();
/*  3485 */         fgCallback.ifSelectionBegin(firstToken);
/*  3486 */         lParenthesis();
/*  3487 */         expression();
/*  3488 */         rParenthesis();
/*  3489 */         statement();
/*  3490 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 98:
/*  3492 */           elseToken = elseKeyword();
/*  3493 */           fgCallback.elseSelectionBegin(getToken(0));
/*  3494 */           statement();
/*  3495 */           fgCallback.elseSelectionEnd(getToken(0));
/*  3496 */           break;
/*       */         default:
/*  3498 */           jj_la1[96] = jj_gen;
/*       */         }
/*       */ 
/*  3501 */         jjtree.closeNodeScope(jjtn000, true);
/*  3502 */         jjtc000 = false;
/*  3503 */         fgCallback.ifSelectionEnd(getToken(0));
/*  3504 */         break;
/*       */       case 124:
/*  3506 */         switchKeyword();
/*  3507 */         fgCallback.switchSelectionBegin(getToken(0));
/*  3508 */         lParenthesis();
/*  3509 */         expression();
/*  3510 */         rParenthesis();
/*  3511 */         statement();
/*  3512 */         jjtree.closeNodeScope(jjtn000, true);
/*  3513 */         jjtc000 = false;
/*  3514 */         fgCallback.switchSelectionEnd(getToken(0));
/*  3515 */         break;
/*       */       default:
/*  3517 */         jj_la1[97] = jj_gen;
/*  3518 */         jj_consume_token(-1);
/*  3519 */         throw new ParseException();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  3522 */       if (jjtc000) {
/*  3523 */         jjtree.clearNodeScope(jjtn000);
/*  3524 */         jjtc000 = false;
/*       */       } else {
/*  3526 */         jjtree.popNode();
/*       */       }
/*  3528 */       if ((jjte000 instanceof RuntimeException)) {
/*  3529 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3531 */       if ((jjte000 instanceof ParseException)) {
/*  3532 */         throw ((ParseException)jjte000);
/*       */       }
/*  3534 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3536 */       if (jjtc000)
/*  3537 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void iteration_statement()
/*       */     throws ParseException
/*       */   {
/*  3544 */     ASTNode jjtn000 = new ASTNode(43);
/*  3545 */     boolean jjtc000 = true;
/*  3546 */     jjtree.openNodeScope(jjtn000); jjtn000.setBeginToken(getToken(1));
/*  3547 */     Token firstToken = null;
/*       */     try {
/*  3549 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 134:
/*  3551 */         whileKeyword();
/*  3552 */         fgCallback.whileIterationBegin(getToken(0));
/*  3553 */         lParenthesis();
/*  3554 */         expression();
/*  3555 */         rParenthesis();
/*  3556 */         statement();
/*  3557 */         jjtree.closeNodeScope(jjtn000, true);
/*  3558 */         jjtc000 = false;
/*  3559 */         jjtn000.setEndToken(getToken(0));
/*  3560 */         fgCallback.whileIterationEnd(getToken(0));
/*  3561 */         break;
/*       */       case 96:
/*  3563 */         doKeyword();
/*  3564 */         fgCallback.doIterationBegin(getToken(0));
/*  3565 */         statement();
/*  3566 */         whileKeyword();
/*  3567 */         lParenthesis();
/*  3568 */         expression();
/*  3569 */         rParenthesis();
/*  3570 */         semicolon();
/*  3571 */         jjtree.closeNodeScope(jjtn000, true);
/*  3572 */         jjtc000 = false;
/*  3573 */         jjtn000.setEndToken(getToken(0));
/*  3574 */         fgCallback.doIterationEnd(getToken(0));
/*  3575 */         break;
/*       */       case 103:
/*  3577 */         forKeyword();
/*  3578 */         fgCallback.forIterationBegin(getToken(0));
/*  3579 */         lParenthesis();
/*  3580 */         if (jj_2_105(3)) {
/*  3581 */           declaration_counter(false, getToken(0));
/*  3582 */         } else if (jj_2_106(1)) {
/*  3583 */           firstToken = getToken(1);
/*  3584 */           expression();
/*  3585 */           fgCallback.expressionSelection(firstToken, getToken(0));
/*  3586 */           semicolon();
/*       */         } else {
/*  3588 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */           case 44:
/*  3590 */             semicolon();
/*  3591 */             break;
/*       */           default:
/*  3593 */             jj_la1[98] = jj_gen;
/*  3594 */             jj_consume_token(-1);
/*  3595 */             throw new ParseException();
/*       */           }
/*       */         }
/*  3598 */         if (jj_2_107(1)) {
/*  3599 */           expression();
/*       */         }
/*       */ 
/*  3603 */         semicolon();
/*  3604 */         if (jj_2_108(1)) {
/*  3605 */           expression();
/*       */         }
/*       */ 
/*  3609 */         rParenthesis();
/*  3610 */         statement();
/*  3611 */         jjtree.closeNodeScope(jjtn000, true);
/*  3612 */         jjtc000 = false;
/*  3613 */         jjtn000.setEndToken(getToken(0));
/*  3614 */         fgCallback.forIterationEnd(getToken(0));
/*  3615 */         break;
/*       */       default:
/*  3617 */         jj_la1[99] = jj_gen;
/*  3618 */         jj_consume_token(-1);
/*  3619 */         throw new ParseException();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  3622 */       if (jjtc000) {
/*  3623 */         jjtree.clearNodeScope(jjtn000);
/*  3624 */         jjtc000 = false;
/*       */       } else {
/*  3626 */         jjtree.popNode();
/*       */       }
/*  3628 */       if ((jjte000 instanceof RuntimeException)) {
/*  3629 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3631 */       if ((jjte000 instanceof ParseException)) {
/*  3632 */         throw ((ParseException)jjte000);
/*       */       }
/*  3634 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3636 */       if (jjtc000)
/*  3637 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void jump_statement()
/*       */     throws ParseException
/*       */   {
/*  3644 */     ASTNode jjtn000 = new ASTNode(44);
/*  3645 */     boolean jjtc000 = true;
/*  3646 */     jjtree.openNodeScope(jjtn000); Token beginToken = getToken(1);
/*  3647 */     jjtn000.setBeginToken(getToken(1));
/*       */     try {
/*  3649 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 105:
/*  3651 */         gotoKeyword();
/*  3652 */         id();
/*  3653 */         semicolon();
/*  3654 */         jjtree.closeNodeScope(jjtn000, true);
/*  3655 */         jjtc000 = false;
/*  3656 */         jjtn000.setEndToken(getToken(0));
/*  3657 */         fgCallback.gotoStatement(beginToken, getToken(0));
/*  3658 */         break;
/*       */       case 93:
/*  3660 */         continueKeyword();
/*  3661 */         semicolon();
/*  3662 */         jjtree.closeNodeScope(jjtn000, true);
/*  3663 */         jjtc000 = false;
/*  3664 */         jjtn000.setEndToken(getToken(0));
/*  3665 */         fgCallback.continueStatement(beginToken, getToken(0));
/*  3666 */         break;
/*       */       case 86:
/*  3668 */         breakKeyword();
/*  3669 */         semicolon();
/*  3670 */         jjtree.closeNodeScope(jjtn000, true);
/*  3671 */         jjtc000 = false;
/*  3672 */         jjtn000.setEndToken(getToken(0));
/*  3673 */         fgCallback.breakStatement(beginToken, getToken(0));
/*  3674 */         break;
/*       */       case 116:
/*  3676 */         returnKeyword();
/*       */ 
/*  3679 */         while (jj_2_109(1))
/*       */         {
/*  3684 */           expression();
/*       */         }
/*  3686 */         semicolon();
/*  3687 */         jjtree.closeNodeScope(jjtn000, true);
/*  3688 */         jjtc000 = false;
/*  3689 */         jjtn000.setEndToken(getToken(0));
/*  3690 */         fgCallback.returnStatement(beginToken, getToken(0));
/*  3691 */         break;
/*       */       default:
/*  3693 */         jj_la1[100] = jj_gen;
/*  3694 */         jj_consume_token(-1);
/*  3695 */         throw new ParseException();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  3698 */       if (jjtc000) {
/*  3699 */         jjtree.clearNodeScope(jjtn000);
/*  3700 */         jjtc000 = false;
/*       */       } else {
/*  3702 */         jjtree.popNode();
/*       */       }
/*  3704 */       if ((jjte000 instanceof RuntimeException)) {
/*  3705 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3707 */       if ((jjte000 instanceof ParseException)) {
/*  3708 */         throw ((ParseException)jjte000);
/*       */       }
/*  3710 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3712 */       if (jjtc000)
/*  3713 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void try_block()
/*       */     throws ParseException
/*       */   {
/*  3720 */     ASTNode jjtn000 = new ASTNode(45);
/*  3721 */     boolean jjtc000 = true;
/*  3722 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  3724 */       tryKeyword();
/*  3725 */       compound_statement();
/*       */       while (true)
/*       */       {
/*  3728 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */         {
/*       */         case 90:
/*       */         case 101:
/*  3732 */           break;
/*       */         default:
/*  3734 */           jj_la1[101] = jj_gen;
/*  3735 */           break;
/*       */         }
/*  3737 */         handler();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  3740 */       if (jjtc000) {
/*  3741 */         jjtree.clearNodeScope(jjtn000);
/*  3742 */         jjtc000 = false;
/*       */       } else {
/*  3744 */         jjtree.popNode();
/*       */       }
/*  3746 */       if ((jjte000 instanceof RuntimeException)) {
/*  3747 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3749 */       if ((jjte000 instanceof ParseException)) {
/*  3750 */         throw ((ParseException)jjte000);
/*       */       }
/*  3752 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3754 */       if (jjtc000)
/*  3755 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void handler()
/*       */     throws ParseException
/*       */   {
/*  3762 */     ASTNode jjtn000 = new ASTNode(46);
/*  3763 */     boolean jjtc000 = true;
/*  3764 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  3766 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 90:
/*  3768 */         catchKeyword();
/*  3769 */         lParenthesis();
/*  3770 */         exception_declaration();
/*  3771 */         rParenthesis();
/*  3772 */         compound_statement();
/*  3773 */         break;
/*       */       case 101:
/*  3775 */         finallyKeyword();
/*  3776 */         compound_statement();
/*  3777 */         break;
/*       */       default:
/*  3779 */         jj_la1[102] = jj_gen;
/*  3780 */         jj_consume_token(-1);
/*  3781 */         throw new ParseException();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  3784 */       if (jjtc000) {
/*  3785 */         jjtree.clearNodeScope(jjtn000);
/*  3786 */         jjtc000 = false;
/*       */       } else {
/*  3788 */         jjtree.popNode();
/*       */       }
/*  3790 */       if ((jjte000 instanceof RuntimeException)) {
/*  3791 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3793 */       if ((jjte000 instanceof ParseException)) {
/*  3794 */         throw ((ParseException)jjte000);
/*       */       }
/*  3796 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3798 */       if (jjtc000)
/*  3799 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void exception_declaration()
/*       */     throws ParseException
/*       */   {
/*  3806 */     ASTNode jjtn000 = new ASTNode(47);
/*  3807 */     boolean jjtc000 = true;
/*  3808 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  3810 */       if (jj_2_110(1))
/*  3811 */         parameter_declaration_list();
/*       */       else
/*  3813 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 47:
/*  3815 */           ellipsis();
/*  3816 */           break;
/*       */         default:
/*  3818 */           jj_la1[103] = jj_gen;
/*  3819 */           jj_consume_token(-1);
/*  3820 */           throw new ParseException();
/*       */         }
/*       */     }
/*       */     catch (Throwable jjte000) {
/*  3824 */       if (jjtc000) {
/*  3825 */         jjtree.clearNodeScope(jjtn000);
/*  3826 */         jjtc000 = false;
/*       */       } else {
/*  3828 */         jjtree.popNode();
/*       */       }
/*  3830 */       if ((jjte000 instanceof RuntimeException)) {
/*  3831 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3833 */       if ((jjte000 instanceof ParseException)) {
/*  3834 */         throw ((ParseException)jjte000);
/*       */       }
/*  3836 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3838 */       if (jjtc000)
/*  3839 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void throw_statement()
/*       */     throws ParseException
/*       */   {
/*  3846 */     ASTNode jjtn000 = new ASTNode(48);
/*  3847 */     boolean jjtc000 = true;
/*  3848 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(1);
/*  3849 */     jjtn000.setBeginToken(firstToken);
/*       */     try {
/*  3851 */       throwKeyword();
/*  3852 */       if (jj_2_111(1)) {
/*  3853 */         assignment_expression();
/*       */       }
/*       */ 
/*  3857 */       semicolon();
/*  3858 */       jjtree.closeNodeScope(jjtn000, true);
/*  3859 */       jjtc000 = false;
/*  3860 */       jjtn000.setEndToken(getToken(0));
/*       */     } catch (Throwable jjte000) {
/*  3862 */       if (jjtc000) {
/*  3863 */         jjtree.clearNodeScope(jjtn000);
/*  3864 */         jjtc000 = false;
/*       */       } else {
/*  3866 */         jjtree.popNode();
/*       */       }
/*  3868 */       if ((jjte000 instanceof RuntimeException)) {
/*  3869 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3871 */       if ((jjte000 instanceof ParseException)) {
/*  3872 */         throw ((ParseException)jjte000);
/*       */       }
/*  3874 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3876 */       if (jjtc000)
/*  3877 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void expression()
/*       */     throws ParseException
/*       */   {
/*  3884 */     ASTNode jjtn000 = new ASTNode(49);
/*  3885 */     boolean jjtc000 = true;
/*  3886 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(1);
/*  3887 */     jjtn000.setBeginToken(getToken(1));
/*       */     try {
/*  3889 */       assignment_expression();
/*       */ 
/*  3892 */       while (jj_2_112(2))
/*       */       {
/*  3897 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 45:
/*  3899 */           comma();
/*  3900 */           break;
/*       */         default:
/*  3902 */           jj_la1[104] = jj_gen;
/*       */         }
/*       */ 
/*  3905 */         expression();
/*       */       }
/*  3907 */       jjtree.closeNodeScope(jjtn000, true);
/*  3908 */       jjtc000 = false;
/*       */ 
/*  3910 */       jjtn000.setEndToken(getToken(0));
/*       */     } catch (Throwable jjte000) {
/*  3912 */       if (jjtc000) {
/*  3913 */         jjtree.clearNodeScope(jjtn000);
/*  3914 */         jjtc000 = false;
/*       */       } else {
/*  3916 */         jjtree.popNode();
/*       */       }
/*  3918 */       if ((jjte000 instanceof RuntimeException)) {
/*  3919 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  3921 */       if ((jjte000 instanceof ParseException)) {
/*  3922 */         throw ((ParseException)jjte000);
/*       */       }
/*  3924 */       throw ((Error)jjte000);
/*       */     } finally {
/*  3926 */       if (jjtc000)
/*  3927 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void assignment_expression()
/*       */     throws ParseException
/*       */   {
/*  3934 */     ASTNode jjtn000 = new ASTNode(50);
/*  3935 */     boolean jjtc000 = true;
/*  3936 */     jjtree.openNodeScope(jjtn000); jjtn000.setBeginToken(getToken(1));
/*       */     try {
/*  3938 */       conditional_expression();
/*  3939 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 48:
/*       */       case 49:
/*       */       case 50:
/*       */       case 51:
/*       */       case 52:
/*       */       case 53:
/*       */       case 54:
/*       */       case 55:
/*       */       case 56:
/*       */       case 57:
/*       */       case 58:
/*  3951 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 48:
/*  3953 */           assignEqual();
/*  3954 */           break;
/*       */         case 49:
/*  3956 */           timesEqual();
/*  3957 */           break;
/*       */         case 50:
/*  3959 */           divideEqual();
/*  3960 */           break;
/*       */         case 51:
/*  3962 */           modEqual();
/*  3963 */           break;
/*       */         case 52:
/*  3965 */           plusEqual();
/*  3966 */           break;
/*       */         case 53:
/*  3968 */           minusEqual();
/*  3969 */           break;
/*       */         case 54:
/*  3971 */           shiftLeftEqual();
/*  3972 */           break;
/*       */         case 55:
/*  3974 */           shiftRightEqual();
/*  3975 */           break;
/*       */         case 56:
/*  3977 */           bitwiseAndEqual();
/*  3978 */           break;
/*       */         case 57:
/*  3980 */           bitwiseXorEqual();
/*  3981 */           break;
/*       */         case 58:
/*  3983 */           bitwiseOrEqual();
/*  3984 */           break;
/*       */         default:
/*  3986 */           jj_la1[105] = jj_gen;
/*  3987 */           jj_consume_token(-1);
/*  3988 */           throw new ParseException();
/*       */         }
/*  3990 */         assignment_expression();
/*  3991 */         break;
/*       */       default:
/*  3993 */         jj_la1[106] = jj_gen;
/*       */       }
/*       */ 
/*  3996 */       jjtree.closeNodeScope(jjtn000, true);
/*  3997 */       jjtc000 = false;
/*  3998 */       jjtn000.setEndToken(getToken(0));
/*       */     } catch (Throwable jjte000) {
/*  4000 */       if (jjtc000) {
/*  4001 */         jjtree.clearNodeScope(jjtn000);
/*  4002 */         jjtc000 = false;
/*       */       } else {
/*  4004 */         jjtree.popNode();
/*       */       }
/*  4006 */       if ((jjte000 instanceof RuntimeException)) {
/*  4007 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  4009 */       if ((jjte000 instanceof ParseException)) {
/*  4010 */         throw ((ParseException)jjte000);
/*       */       }
/*  4012 */       throw ((Error)jjte000);
/*       */     } finally {
/*  4014 */       if (jjtc000)
/*  4015 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void conditional_expression() throws ParseException
/*       */   {
/*  4021 */     logical_or_expression();
/*  4022 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 46:
/*  4024 */       questionMark();
/*  4025 */       conditional_expression();
/*  4026 */       colon();
/*  4027 */       conditional_expression();
/*  4028 */       break;
/*       */     default:
/*  4030 */       jj_la1[107] = jj_gen;
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void constant_expression()
/*       */     throws ParseException
/*       */   {
/*  4037 */     ASTNode jjtn000 = new ASTNode(51);
/*  4038 */     boolean jjtc000 = true;
/*  4039 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(1);
/*       */     try {
/*  4041 */       conditional_expression();
/*  4042 */       jjtree.closeNodeScope(jjtn000, true);
/*  4043 */       jjtc000 = false;
/*  4044 */       jjtn000.setBeginToken(firstToken);
/*  4045 */       Token endToken = getToken(0);
/*  4046 */       jjtn000.setEndToken(endToken);
/*       */     } catch (Throwable jjte000) {
/*  4048 */       if (jjtc000) {
/*  4049 */         jjtree.clearNodeScope(jjtn000);
/*  4050 */         jjtc000 = false;
/*       */       } else {
/*  4052 */         jjtree.popNode();
/*       */       }
/*  4054 */       if ((jjte000 instanceof RuntimeException)) {
/*  4055 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  4057 */       if ((jjte000 instanceof ParseException)) {
/*  4058 */         throw ((ParseException)jjte000);
/*       */       }
/*  4060 */       throw ((Error)jjte000);
/*       */     } finally {
/*  4062 */       if (jjtc000)
/*  4063 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void logical_or_expression() throws ParseException
/*       */   {
/*  4069 */     logical_and_expression();
/*       */     while (true)
/*       */     {
/*  4072 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 59:
/*  4075 */         break;
/*       */       default:
/*  4077 */         jj_la1[108] = jj_gen;
/*  4078 */         break;
/*       */       }
/*  4080 */       or();
/*  4081 */       logical_and_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void logical_and_expression() throws ParseException {
/*  4086 */     inclusive_or_expression();
/*       */     while (true)
/*       */     {
/*  4089 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 60:
/*  4092 */         break;
/*       */       default:
/*  4094 */         jj_la1[109] = jj_gen;
/*  4095 */         break;
/*       */       }
/*  4097 */       and();
/*  4098 */       inclusive_or_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void inclusive_or_expression() throws ParseException {
/*  4103 */     exclusive_or_expression();
/*       */     while (true)
/*       */     {
/*  4106 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 61:
/*  4109 */         break;
/*       */       default:
/*  4111 */         jj_la1[110] = jj_gen;
/*  4112 */         break;
/*       */       }
/*  4114 */       bitwiseOr();
/*  4115 */       exclusive_or_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void exclusive_or_expression() throws ParseException {
/*  4120 */     and_expression();
/*       */     while (true)
/*       */     {
/*  4123 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 62:
/*  4126 */         break;
/*       */       default:
/*  4128 */         jj_la1[111] = jj_gen;
/*  4129 */         break;
/*       */       }
/*  4131 */       bitwiseXor();
/*  4132 */       and_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void and_expression() throws ParseException {
/*  4137 */     equality_expression();
/*       */ 
/*  4140 */     while (jj_2_113(2))
/*       */     {
/*  4145 */       ampersand();
/*  4146 */       equality_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void equality_expression() throws ParseException {
/*  4151 */     relational_expression();
/*       */     while (true)
/*       */     {
/*  4154 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 64:
/*       */       case 65:
/*  4158 */         break;
/*       */       default:
/*  4160 */         jj_la1[112] = jj_gen;
/*  4161 */         break;
/*       */       }
/*  4163 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 65:
/*  4165 */         notEqual();
/*  4166 */         break;
/*       */       case 64:
/*  4168 */         equal();
/*  4169 */         break;
/*       */       default:
/*  4171 */         jj_la1[113] = jj_gen;
/*  4172 */         jj_consume_token(-1);
/*  4173 */         throw new ParseException();
/*       */       }
/*  4175 */       relational_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void relational_expression() throws ParseException {
/*  4180 */     shift_expression();
/*       */ 
/*  4183 */     while (jj_2_114(2))
/*       */     {
/*  4188 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 66:
/*  4190 */         lessThan();
/*  4191 */         break;
/*       */       case 67:
/*  4193 */         greaterThan();
/*  4194 */         break;
/*       */       case 68:
/*  4196 */         lessThanOrEqualTo();
/*  4197 */         break;
/*       */       case 69:
/*  4199 */         greaterThanOrEqualTo();
/*  4200 */         break;
/*       */       default:
/*  4202 */         jj_la1[114] = jj_gen;
/*  4203 */         jj_consume_token(-1);
/*  4204 */         throw new ParseException();
/*       */       }
/*  4206 */       shift_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void shift_expression() throws ParseException {
/*  4211 */     additive_expression();
/*       */     while (true)
/*       */     {
/*  4214 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 70:
/*       */       case 71:
/*  4218 */         break;
/*       */       default:
/*  4220 */         jj_la1[115] = jj_gen;
/*  4221 */         break;
/*       */       }
/*  4223 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 70:
/*  4225 */         shiftLeft();
/*  4226 */         break;
/*       */       case 71:
/*  4228 */         shiftRight();
/*  4229 */         break;
/*       */       default:
/*  4231 */         jj_la1[116] = jj_gen;
/*  4232 */         jj_consume_token(-1);
/*  4233 */         throw new ParseException();
/*       */       }
/*  4235 */       additive_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void additive_expression() throws ParseException {
/*  4240 */     multiplicative_expression();
/*       */ 
/*  4243 */     while (jj_2_115(2))
/*       */     {
/*  4248 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 72:
/*  4250 */         plus();
/*  4251 */         break;
/*       */       case 73:
/*  4253 */         minus();
/*  4254 */         break;
/*       */       default:
/*  4256 */         jj_la1[117] = jj_gen;
/*  4257 */         jj_consume_token(-1);
/*  4258 */         throw new ParseException();
/*       */       }
/*  4260 */       multiplicative_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void multiplicative_expression() throws ParseException {
/*  4265 */     pm_expression();
/*       */ 
/*  4268 */     while (jj_2_116(2))
/*       */     {
/*  4273 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 74:
/*  4275 */         star();
/*  4276 */         break;
/*       */       case 75:
/*  4278 */         divide();
/*  4279 */         break;
/*       */       case 76:
/*  4281 */         mod();
/*  4282 */         break;
/*       */       default:
/*  4284 */         jj_la1[118] = jj_gen;
/*  4285 */         jj_consume_token(-1);
/*  4286 */         throw new ParseException();
/*       */       }
/*  4288 */       pm_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void pm_expression() throws ParseException {
/*  4293 */     cast_expression();
/*       */     while (true)
/*       */     {
/*  4296 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 83:
/*       */       case 84:
/*  4300 */         break;
/*       */       default:
/*  4302 */         jj_la1[119] = jj_gen;
/*  4303 */         break;
/*       */       }
/*  4305 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 83:
/*  4307 */         dotStar();
/*  4308 */         break;
/*       */       case 84:
/*  4310 */         arrowStar();
/*  4311 */         break;
/*       */       default:
/*  4313 */         jj_la1[120] = jj_gen;
/*  4314 */         jj_consume_token(-1);
/*  4315 */         throw new ParseException();
/*       */       }
/*  4317 */       cast_expression();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void cast_expression() throws ParseException
/*       */   {
/*  4323 */     ASTNode jjtn000 = new ASTNode(52);
/*  4324 */     boolean jjtc000 = true;
/*  4325 */     jjtree.openNodeScope(jjtn000); Token firstToken = getToken(0);
/*  4326 */     Token firstOperationToken = getToken(1);
/*       */     try {
/*  4328 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 160:
/*  4330 */         datetimeInitializer();
/*  4331 */         break;
/*       */       case 161:
/*  4333 */         colorInitializer();
/*  4334 */         break;
/*       */       default:
/*  4336 */         jj_la1[121] = jj_gen;
/*  4337 */         if (jj_2_117(2147483647)) {
/*  4338 */           lParenthesis();
/*  4339 */           id();
/*  4340 */           rParenthesis();
/*  4341 */         } else if (jj_2_118(2147483647)) {
/*  4342 */           oldCastType();
/*  4343 */         } else if (jj_2_119(2147483647)) {
/*  4344 */           cppCastType();
/*  4345 */         } else if (jj_2_120(1)) {
/*  4346 */           unary_expression();
/*  4347 */         } else if (jj_2_121(1)) {
/*  4348 */           postfix_expression();
/*  4349 */           jjtree.closeNodeScope(jjtn000, true);
/*  4350 */           jjtc000 = false;
/*       */ 
/*  4352 */           jjtn000.setBeginToken(firstOperationToken);
/*  4353 */           Token endToken = getToken(0);
/*       */ 
/*  4355 */           jjtn000.setEndToken(getToken(1));
/*       */         }
/*       */         else
/*       */         {
/*  4359 */           jj_consume_token(-1);
/*  4360 */           throw new ParseException();
/*       */         }
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  4364 */       if (jjtc000) {
/*  4365 */         jjtree.clearNodeScope(jjtn000);
/*  4366 */         jjtc000 = false;
/*       */       } else {
/*  4368 */         jjtree.popNode();
/*       */       }
/*  4370 */       if ((jjte000 instanceof RuntimeException)) {
/*  4371 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  4373 */       if ((jjte000 instanceof ParseException)) {
/*  4374 */         throw ((ParseException)jjte000);
/*       */       }
/*  4376 */       throw ((Error)jjte000);
/*       */     } finally {
/*  4378 */       if (jjtc000)
/*  4379 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void oldCastType()
/*       */     throws ParseException
/*       */   {
/*  4386 */     ASTNode jjtn000 = new ASTNode(53);
/*  4387 */     boolean jjtc000 = true;
/*  4388 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  4390 */       lParenthesis();
/*  4391 */       type_name();
/*  4392 */       rParenthesis();
/*  4393 */       cast_expression();
/*       */     } catch (Throwable jjte000) {
/*  4395 */       if (jjtc000) {
/*  4396 */         jjtree.clearNodeScope(jjtn000);
/*  4397 */         jjtc000 = false;
/*       */       } else {
/*  4399 */         jjtree.popNode();
/*       */       }
/*  4401 */       if ((jjte000 instanceof RuntimeException)) {
/*  4402 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  4404 */       if ((jjte000 instanceof ParseException)) {
/*  4405 */         throw ((ParseException)jjte000);
/*       */       }
/*  4407 */       throw ((Error)jjte000);
/*       */     } finally {
/*  4409 */       if (jjtc000)
/*  4410 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void cppCastType()
/*       */     throws ParseException
/*       */   {
/*  4417 */     ASTNode jjtn000 = new ASTNode(54);
/*  4418 */     boolean jjtc000 = true;
/*  4419 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  4421 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 141:
/*  4423 */         staticCastKeyword();
/*  4424 */         break;
/*       */       case 142:
/*  4426 */         dynamicCastKeyword();
/*  4427 */         break;
/*       */       case 143:
/*  4429 */         constCastKeyword();
/*  4430 */         break;
/*       */       case 144:
/*  4432 */         reinterpretCastKeyword();
/*  4433 */         break;
/*       */       default:
/*  4435 */         jj_la1[122] = jj_gen;
/*  4436 */         jj_consume_token(-1);
/*  4437 */         throw new ParseException();
/*       */       }
/*  4439 */       lessThan();
/*  4440 */       type_name();
/*  4441 */       greaterThan();
/*  4442 */       lParenthesis();
/*  4443 */       cast_expression();
/*  4444 */       rParenthesis();
/*       */     } catch (Throwable jjte000) {
/*  4446 */       if (jjtc000) {
/*  4447 */         jjtree.clearNodeScope(jjtn000);
/*  4448 */         jjtc000 = false;
/*       */       } else {
/*  4450 */         jjtree.popNode();
/*       */       }
/*  4452 */       if ((jjte000 instanceof RuntimeException)) {
/*  4453 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  4455 */       if ((jjte000 instanceof ParseException)) {
/*  4456 */         throw ((ParseException)jjte000);
/*       */       }
/*  4458 */       throw ((Error)jjte000);
/*       */     } finally {
/*  4460 */       if (jjtc000)
/*  4461 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void unary_expression() throws ParseException
/*       */   {
/*  4467 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 77:
/*  4469 */       plusPlus();
/*  4470 */       unary_expression();
/*  4471 */       break;
/*       */     case 78:
/*  4473 */       minusMinus();
/*  4474 */       unary_expression();
/*  4475 */       break;
/*       */     default:
/*  4477 */       jj_la1[123] = jj_gen;
/*  4478 */       if (jj_2_124(3)) {
/*  4479 */         unary_operator();
/*  4480 */         cast_expression();
/*       */       } else {
/*  4482 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 119:
/*  4484 */           sizeofKeyword();
/*  4485 */           if (jj_2_122(2147483647)) {
/*  4486 */             lParenthesis();
/*  4487 */             type_name();
/*  4488 */             rParenthesis();
/*  4489 */           } else if (jj_2_123(1)) {
/*  4490 */             unary_expression();
/*       */           } else {
/*  4492 */             jj_consume_token(-1);
/*  4493 */             throw new ParseException();
/*       */           }
/*       */ 
/*       */         default:
/*  4497 */           jj_la1[124] = jj_gen;
/*  4498 */           if (jj_2_125(1)) {
/*  4499 */             postfix_expression();
/*       */           } else {
/*  4501 */             jj_consume_token(-1);
/*  4502 */             throw new ParseException();
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void new_expression() throws ParseException
/*       */   {
/*  4511 */     ASTNode jjtn000 = new ASTNode(55);
/*  4512 */     boolean jjtc000 = true;
/*  4513 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  4515 */       if (jj_2_126(2147483647)) {
/*  4516 */         scope();
/*       */       }
/*       */ 
/*  4520 */       newKeyword();
/*  4521 */       if (jj_2_130(2147483647)) {
/*  4522 */         lParenthesis();
/*  4523 */         type_name();
/*  4524 */         rParenthesis();
/*  4525 */       } else if (jj_2_131(1)) {
/*  4526 */         if (jj_2_127(2147483647)) {
/*  4527 */           lParenthesis();
/*  4528 */           expression_list();
/*  4529 */           rParenthesis();
/*       */         }
/*       */ 
/*  4533 */         if (jj_2_128(2147483647)) {
/*  4534 */           lParenthesis();
/*  4535 */           type_name();
/*  4536 */           rParenthesis();
/*  4537 */         } else if (jj_2_129(2147483647)) {
/*  4538 */           new_type_id();
/*       */         } else {
/*  4540 */           jj_consume_token(-1);
/*  4541 */           throw new ParseException();
/*       */         }
/*       */       } else {
/*  4544 */         jj_consume_token(-1);
/*  4545 */         throw new ParseException();
/*       */       }
/*  4547 */       if (jj_2_132(2147483647)) {
/*  4548 */         new_initializer();
/*       */       }
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  4553 */       if (jjtc000) {
/*  4554 */         jjtree.clearNodeScope(jjtn000);
/*  4555 */         jjtc000 = false;
/*       */       } else {
/*  4557 */         jjtree.popNode();
/*       */       }
/*  4559 */       if ((jjte000 instanceof RuntimeException)) {
/*  4560 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  4562 */       if ((jjte000 instanceof ParseException)) {
/*  4563 */         throw ((ParseException)jjte000);
/*       */       }
/*  4565 */       throw ((Error)jjte000);
/*       */     } finally {
/*  4567 */       if (jjtc000)
/*  4568 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void new_type_id() throws ParseException
/*       */   {
/*  4574 */     declaration_specifiers();
/*  4575 */     if (jj_2_133(2147483647))
/*  4576 */       new_declarator();
/*       */   }
/*       */ 
/*       */   public static final void new_declarator()
/*       */     throws ParseException
/*       */   {
/*  4583 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 38:
/*  4585 */       direct_new_declarator();
/*  4586 */       break;
/*       */     case 42:
/*       */     case 63:
/*       */     case 74:
/*       */     case 164:
/*  4591 */       ptr_operator();
/*  4592 */       cv_qualifier_seq();
/*  4593 */       if (!jj_2_134(2)) break;
/*  4594 */       new_declarator(); break;
/*       */     default:
/*  4600 */       jj_la1[125] = jj_gen;
/*  4601 */       jj_consume_token(-1);
/*  4602 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void direct_new_declarator() throws ParseException
/*       */   {
/*       */     do {
/*  4609 */       lSquareBracket();
/*  4610 */       expression();
/*       */       while (true)
/*       */       {
/*  4613 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */         {
/*       */         case 45:
/*  4616 */           break;
/*       */         default:
/*  4618 */           jj_la1[126] = jj_gen;
/*  4619 */           break;
/*       */         }
/*  4621 */         comma();
/*  4622 */         expression();
/*       */       }
/*  4624 */       rSquareBracket();
/*  4625 */     }while (jj_2_135(2));
/*       */   }
/*       */ 
/*       */   public static final void new_initializer()
/*       */     throws ParseException
/*       */   {
/*  4634 */     lParenthesis();
/*  4635 */     if (jj_2_136(1)) {
/*  4636 */       expression_list();
/*       */     }
/*       */ 
/*  4640 */     rParenthesis();
/*       */   }
/*       */ 
/*       */   public static final void delete_expression() throws ParseException
/*       */   {
/*  4645 */     ASTNode jjtn000 = new ASTNode(56);
/*  4646 */     boolean jjtc000 = true;
/*  4647 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  4649 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 42:
/*  4651 */         scope();
/*  4652 */         break;
/*       */       default:
/*  4654 */         jj_la1[127] = jj_gen;
/*       */       }
/*       */ 
/*  4657 */       deleteKeyword();
/*  4658 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 38:
/*  4660 */         lSquareBracket();
/*  4661 */         rSquareBracket();
/*  4662 */         break;
/*       */       default:
/*  4664 */         jj_la1[''] = jj_gen;
/*       */       }
/*       */ 
/*  4667 */       cast_expression();
/*       */     } catch (Throwable jjte000) {
/*  4669 */       if (jjtc000) {
/*  4670 */         jjtree.clearNodeScope(jjtn000);
/*  4671 */         jjtc000 = false;
/*       */       } else {
/*  4673 */         jjtree.popNode();
/*       */       }
/*  4675 */       if ((jjte000 instanceof RuntimeException)) {
/*  4676 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  4678 */       if ((jjte000 instanceof ParseException)) {
/*  4679 */         throw ((ParseException)jjte000);
/*       */       }
/*  4681 */       throw ((Error)jjte000);
/*       */     } finally {
/*  4683 */       if (jjtc000)
/*  4684 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void unary_operator()
/*       */     throws ParseException
/*       */   {
/*  4691 */     ASTNode jjtn000 = new ASTNode(57);
/*  4692 */     boolean jjtc000 = true;
/*  4693 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  4695 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 63:
/*  4697 */         ampersand();
/*  4698 */         break;
/*       */       case 74:
/*  4700 */         star();
/*  4701 */         break;
/*       */       case 72:
/*  4703 */         plus();
/*  4704 */         break;
/*       */       case 73:
/*  4706 */         minus();
/*  4707 */         break;
/*       */       case 79:
/*  4709 */         tilde();
/*  4710 */         break;
/*       */       case 80:
/*  4712 */         not();
/*  4713 */         break;
/*       */       case 64:
/*       */       case 65:
/*       */       case 66:
/*       */       case 67:
/*       */       case 68:
/*       */       case 69:
/*       */       case 70:
/*       */       case 71:
/*       */       case 75:
/*       */       case 76:
/*       */       case 77:
/*       */       case 78:
/*       */       default:
/*  4715 */         jj_la1[''] = jj_gen;
/*  4716 */         jj_consume_token(-1);
/*  4717 */         throw new ParseException();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  4720 */       if (jjtc000) {
/*  4721 */         jjtree.clearNodeScope(jjtn000);
/*  4722 */         jjtc000 = false;
/*       */       } else {
/*  4724 */         jjtree.popNode();
/*       */       }
/*  4726 */       if ((jjte000 instanceof RuntimeException)) {
/*  4727 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  4729 */       if ((jjte000 instanceof ParseException)) {
/*  4730 */         throw ((ParseException)jjte000);
/*       */       }
/*  4732 */       throw ((Error)jjte000);
/*       */     } finally {
/*  4734 */       if (jjtc000)
/*  4735 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void literal() throws ParseException
/*       */   {
/*  4741 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 167:
/*  4743 */       jj_consume_token(167);
/*  4744 */       break;
/*       */     case 166:
/*  4746 */       jj_consume_token(166);
/*  4747 */       break;
/*       */     case 171:
/*  4749 */       jj_consume_token(171);
/*  4750 */       break;
/*       */     case 176:
/*  4752 */       jj_consume_token(176);
/*  4753 */       break;
/*       */     case 177:
/*  4755 */       jj_consume_token(177);
/*  4756 */       break;
/*       */     case 168:
/*       */     case 169:
/*       */     case 170:
/*       */     case 172:
/*       */     case 173:
/*       */     case 174:
/*       */     case 175:
/*       */     default:
/*  4758 */       jj_la1[''] = jj_gen;
/*  4759 */       jj_consume_token(-1);
/*  4760 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void postfix_expression() throws ParseException {
/*  4765 */     if (jj_2_140(3)) {
/*  4766 */       primary_expression();
/*       */ 
/*  4769 */       while (jj_2_137(2))
/*       */       {
/*  4774 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */         case 38:
/*  4776 */           lSquareBracket();
/*  4777 */           expression();
/*       */           while (true)
/*       */           {
/*  4780 */             switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */             {
/*       */             case 45:
/*  4783 */               break;
/*       */             default:
/*  4785 */               jj_la1[''] = jj_gen;
/*  4786 */               break;
/*       */             }
/*  4788 */             comma();
/*  4789 */             expression();
/*       */           }
/*  4791 */           rSquareBracket();
/*  4792 */           break;
/*       */         case 40:
/*  4794 */           lParenthesis();
/*  4795 */           if (jj_2_138(1)) {
/*  4796 */             expression_list();
/*       */           }
/*       */ 
/*  4800 */           rParenthesis();
/*  4801 */           break;
/*       */         case 81:
/*  4803 */           dot();
/*  4804 */           id_expression();
/*  4805 */           break;
/*       */         case 82:
/*  4807 */           pointerTo();
/*  4808 */           id_expression();
/*  4809 */           break;
/*       */         case 77:
/*  4811 */           plusPlus();
/*  4812 */           break;
/*       */         case 78:
/*  4814 */           minusMinus();
/*       */         }
/*       */       }
/*  4817 */       jj_la1[''] = jj_gen;
/*  4818 */       jj_consume_token(-1);
/*  4819 */       throw new ParseException();
/*       */     }
/*       */ 
/*  4822 */     if (jj_2_141(1)) {
/*  4823 */       simple_type_specifier();
/*  4824 */       lParenthesis();
/*  4825 */       if (jj_2_139(1)) {
/*  4826 */         expression_list();
/*       */       }
/*       */ 
/*  4830 */       rParenthesis();
/*       */     } else {
/*  4832 */       jj_consume_token(-1);
/*  4833 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void id_expression() throws ParseException {
/*  4838 */     if (jj_2_142(2147483647)) {
/*  4839 */       scope_override();
/*       */     }
/*       */ 
/*  4843 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 178:
/*  4845 */       jj_consume_token(178);
/*  4846 */       break;
/*       */     case 165:
/*  4848 */       jj_consume_token(165);
/*  4849 */       break;
/*       */     case 164:
/*  4851 */       id();
/*  4852 */       break;
/*       */     case 135:
/*  4854 */       operatorKeyword();
/*  4855 */       optor();
/*  4856 */       break;
/*       */     case 79:
/*  4858 */       tilde();
/*  4859 */       id();
/*  4860 */       break;
/*       */     default:
/*  4862 */       jj_la1[''] = jj_gen;
/*  4863 */       jj_consume_token(-1);
/*  4864 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void primary_expression() throws ParseException {
/*  4869 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 126:
/*  4871 */       thisKeyword();
/*  4872 */       break;
/*       */     case 163:
/*       */     case 40:
/*       */     default:
/*       */       while (true) { stringConstant();
/*  4877 */         if (!jj_2_143(2)) break; continue;
/*       */ 
/*  4885 */         lParenthesis();
/*  4886 */         expression();
/*  4887 */         rParenthesis();
/*  4888 */         break;
/*       */ 
/*  4890 */         jj_la1[''] = jj_gen;
/*  4891 */         if (jj_2_144(2147483647))
/*  4892 */           new_expression();
/*  4893 */         else if (jj_2_145(2147483647))
/*  4894 */           delete_expression();
/*       */         else
/*  4896 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */           case 42:
/*       */           case 79:
/*       */           case 135:
/*       */           case 164:
/*       */           case 165:
/*       */           case 178:
/*  4903 */             id_expression();
/*  4904 */             break;
/*       */           case 136:
/*       */           case 137:
/*       */           case 145:
/*       */           case 146:
/*       */           case 147:
/*       */           case 148:
/*       */           case 149:
/*       */           case 150:
/*       */           case 151:
/*       */           case 152:
/*       */           case 153:
/*       */           case 154:
/*       */           case 155:
/*       */           case 156:
/*       */           case 157:
/*       */           case 158:
/*       */           case 159:
/*       */           case 162:
/*       */           case 177:
/*  4924 */             constant();
/*  4925 */             break;
/*       */           default:
/*  4927 */             jj_la1[''] = jj_gen;
/*  4928 */             jj_consume_token(-1);
/*  4929 */             throw new ParseException();
/*       */           } }
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void expression_list()
/*       */     throws ParseException
/*       */   {
/*  4937 */     ASTNode jjtn000 = new ASTNode(58);
/*  4938 */     boolean jjtc000 = true;
/*  4939 */     jjtree.openNodeScope(jjtn000); jjtn000.setBeginToken(getToken(1));
/*       */     try {
/*  4941 */       assignment_expression();
/*       */       while (true)
/*       */       {
/*  4944 */         switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */         {
/*       */         case 45:
/*  4947 */           break;
/*       */         default:
/*  4949 */           jj_la1[''] = jj_gen;
/*  4950 */           break;
/*       */         }
/*  4952 */         comma();
/*  4953 */         assignment_expression();
/*       */       }
/*  4955 */       jjtree.closeNodeScope(jjtn000, true);
/*  4956 */       jjtc000 = false;
/*  4957 */       jjtn000.setEndToken(getToken(0));
/*       */     } catch (Throwable jjte000) {
/*  4959 */       if (jjtc000) {
/*  4960 */         jjtree.clearNodeScope(jjtn000);
/*  4961 */         jjtc000 = false;
/*       */       } else {
/*  4963 */         jjtree.popNode();
/*       */       }
/*  4965 */       if ((jjte000 instanceof RuntimeException)) {
/*  4966 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  4968 */       if ((jjte000 instanceof ParseException)) {
/*  4969 */         throw ((ParseException)jjte000);
/*       */       }
/*  4971 */       throw ((Error)jjte000);
/*       */     } finally {
/*  4973 */       if (jjtc000)
/*  4974 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void constant()
/*       */     throws ParseException
/*       */   {
/*  4981 */     ASTNode jjtn000 = new ASTNode(59);
/*  4982 */     boolean jjtc000 = true;
/*  4983 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  4985 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 149:
/*  4987 */         zeroDecimalInt();
/*  4988 */         break;
/*       */       case 145:
/*  4990 */         octalInt();
/*  4991 */         break;
/*       */       case 146:
/*  4993 */         octalLong();
/*  4994 */         break;
/*       */       case 150:
/*  4996 */         decimalInt();
/*  4997 */         break;
/*       */       case 151:
/*  4999 */         decimalLong();
/*  5000 */         break;
/*       */       case 154:
/*  5002 */         hexadecimalInt();
/*  5003 */         break;
/*       */       case 155:
/*  5005 */         hexadecimalLong();
/*  5006 */         break;
/*       */       case 147:
/*  5008 */         unsignedOctalInt();
/*  5009 */         break;
/*       */       case 148:
/*  5011 */         unsignedOctalLong();
/*  5012 */         break;
/*       */       case 152:
/*  5014 */         unsignedDecimalInt();
/*  5015 */         break;
/*       */       case 153:
/*  5017 */         unsignedDecimalLong();
/*  5018 */         break;
/*       */       case 156:
/*  5020 */         unsignedHexadecimalInt();
/*  5021 */         break;
/*       */       case 157:
/*  5023 */         unsignedHexadecimalLong();
/*  5024 */         break;
/*       */       case 162:
/*  5026 */         characterConstant();
/*  5027 */         break;
/*       */       case 177:
/*  5029 */         jj_consume_token(177);
/*  5030 */         break;
/*       */       case 158:
/*  5032 */         floatOne();
/*  5033 */         break;
/*       */       case 159:
/*  5035 */         floatTwo();
/*  5036 */         break;
/*       */       case 136:
/*  5038 */         trueKeyword();
/*  5039 */         break;
/*       */       case 137:
/*  5041 */         falseKeyword();
/*  5042 */         break;
/*       */       case 138:
/*       */       case 139:
/*       */       case 140:
/*       */       case 141:
/*       */       case 142:
/*       */       case 143:
/*       */       case 144:
/*       */       case 160:
/*       */       case 161:
/*       */       case 163:
/*       */       case 164:
/*       */       case 165:
/*       */       case 166:
/*       */       case 167:
/*       */       case 168:
/*       */       case 169:
/*       */       case 170:
/*       */       case 171:
/*       */       case 172:
/*       */       case 173:
/*       */       case 174:
/*       */       case 175:
/*       */       case 176:
/*       */       default:
/*  5044 */         jj_la1[''] = jj_gen;
/*  5045 */         jj_consume_token(-1);
/*  5046 */         throw new ParseException();
/*       */       }
/*       */     } catch (Throwable jjte000) {
/*  5049 */       if (jjtc000) {
/*  5050 */         jjtree.clearNodeScope(jjtn000);
/*  5051 */         jjtc000 = false;
/*       */       } else {
/*  5053 */         jjtree.popNode();
/*       */       }
/*  5055 */       if ((jjte000 instanceof RuntimeException)) {
/*  5056 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  5058 */       if ((jjte000 instanceof ParseException)) {
/*  5059 */         throw ((ParseException)jjte000);
/*       */       }
/*  5061 */       throw ((Error)jjte000);
/*       */     } finally {
/*  5063 */       if (jjtc000)
/*  5064 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final String optor()
/*       */     throws ParseException
/*       */   {
/*  5071 */     ASTNode jjtn000 = new ASTNode(60);
/*  5072 */     boolean jjtc000 = true;
/*  5073 */     jjtree.openNodeScope(jjtn000); String name = getToken(1).image;
/*  5074 */     Token startToken = getToken(0);
/*       */     try {
/*  5076 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */       case 110:
/*  5078 */         newKeyword();
/*  5079 */         if (!jj_2_146(2)) break;
/*  5080 */         lSquareBracket();
/*  5081 */         rSquareBracket(); break;
/*       */       case 95:
/*  5087 */         deleteKeyword();
/*  5088 */         if (!jj_2_147(2)) break;
/*  5089 */         lSquareBracket();
/*  5090 */         rSquareBracket(); break;
/*       */       case 72:
/*  5096 */         plus();
/*  5097 */         break;
/*       */       case 73:
/*  5099 */         minus();
/*  5100 */         break;
/*       */       case 74:
/*  5102 */         star();
/*  5103 */         break;
/*       */       case 75:
/*  5105 */         divide();
/*  5106 */         break;
/*       */       case 76:
/*  5108 */         mod();
/*  5109 */         break;
/*       */       case 62:
/*  5111 */         bitwiseXor();
/*  5112 */         break;
/*       */       case 63:
/*  5114 */         ampersand();
/*  5115 */         break;
/*       */       case 61:
/*  5117 */         bitwiseOr();
/*  5118 */         break;
/*       */       case 79:
/*  5120 */         tilde();
/*  5121 */         break;
/*       */       case 80:
/*  5123 */         not();
/*  5124 */         break;
/*       */       case 48:
/*  5126 */         assignEqual();
/*  5127 */         break;
/*       */       case 66:
/*  5129 */         lessThan();
/*  5130 */         break;
/*       */       case 67:
/*  5132 */         greaterThan();
/*  5133 */         break;
/*       */       case 52:
/*  5135 */         plusEqual();
/*  5136 */         break;
/*       */       case 53:
/*  5138 */         minusEqual();
/*  5139 */         break;
/*       */       case 49:
/*  5141 */         timesEqual();
/*  5142 */         break;
/*       */       case 50:
/*  5144 */         divideEqual();
/*  5145 */         break;
/*       */       case 51:
/*  5147 */         modEqual();
/*  5148 */         break;
/*       */       case 57:
/*  5150 */         bitwiseXorEqual();
/*  5151 */         break;
/*       */       case 56:
/*  5153 */         bitwiseAndEqual();
/*  5154 */         break;
/*       */       case 58:
/*  5156 */         bitwiseOrEqual();
/*  5157 */         break;
/*       */       case 70:
/*  5159 */         shiftLeft();
/*  5160 */         break;
/*       */       case 71:
/*  5162 */         shiftRight();
/*  5163 */         break;
/*       */       case 54:
/*  5165 */         shiftLeftEqual();
/*  5166 */         break;
/*       */       case 55:
/*  5168 */         shiftRightEqual();
/*  5169 */         break;
/*       */       case 64:
/*  5171 */         equal();
/*  5172 */         break;
/*       */       case 65:
/*  5174 */         notEqual();
/*  5175 */         break;
/*       */       case 68:
/*  5177 */         lessThanOrEqualTo();
/*  5178 */         break;
/*       */       case 69:
/*  5180 */         greaterThanOrEqualTo();
/*  5181 */         break;
/*       */       case 60:
/*  5183 */         and();
/*  5184 */         break;
/*       */       case 59:
/*  5186 */         or();
/*  5187 */         break;
/*       */       case 77:
/*  5189 */         plusPlus();
/*  5190 */         break;
/*       */       case 78:
/*  5192 */         minusMinus();
/*  5193 */         break;
/*       */       case 45:
/*  5195 */         comma();
/*  5196 */         break;
/*       */       case 84:
/*  5198 */         arrowStar();
/*  5199 */         break;
/*       */       case 82:
/*  5201 */         pointerTo();
/*  5202 */         break;
/*       */       case 40:
/*  5204 */         lParenthesis();
/*  5205 */         rParenthesis();
/*  5206 */         break;
/*       */       case 38:
/*  5208 */         lSquareBracket();
/*  5209 */         rSquareBracket();
/*  5210 */         break;
/*       */       case 39:
/*       */       case 41:
/*       */       case 42:
/*       */       case 43:
/*       */       case 44:
/*       */       case 46:
/*       */       case 47:
/*       */       case 81:
/*       */       case 83:
/*       */       case 85:
/*       */       case 86:
/*       */       case 87:
/*       */       case 88:
/*       */       case 89:
/*       */       case 90:
/*       */       case 91:
/*       */       case 92:
/*       */       case 93:
/*       */       case 94:
/*       */       case 96:
/*       */       case 97:
/*       */       case 98:
/*       */       case 99:
/*       */       case 100:
/*       */       case 101:
/*       */       case 102:
/*       */       case 103:
/*       */       case 104:
/*       */       case 105:
/*       */       case 106:
/*       */       case 107:
/*       */       case 108:
/*       */       case 109:
/*       */       default:
/*  5212 */         jj_la1[''] = jj_gen;
/*  5213 */         if (jj_2_149(1)) {
/*  5214 */           declaration_specifiers();
/*  5215 */           if (!jj_2_148(2)) break;
/*  5216 */           switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */           case 74:
/*  5218 */             star();
/*  5219 */             break;
/*       */           case 63:
/*  5221 */             ampersand();
/*  5222 */             break;
/*       */           default:
/*  5224 */             jj_la1[''] = jj_gen;
/*  5225 */             jj_consume_token(-1);
/*  5226 */             throw new ParseException();
/*       */           }
/*       */ 
/*       */         }
/*       */         else
/*       */         {
/*  5232 */           jj_consume_token(-1);
/*  5233 */           throw new ParseException();
/*       */         }
/*       */       }
/*  5236 */       jjtree.closeNodeScope(jjtn000, true);
/*  5237 */       jjtc000 = false;
/*       */ 
/*  5239 */       String str1 = name;
/*       */       return str1;
/*       */     }
/*       */     catch (Throwable jjte000)
/*       */     {
/*  5241 */       if (jjtc000) {
/*  5242 */         jjtree.clearNodeScope(jjtn000);
/*  5243 */         jjtc000 = false;
/*       */       } else {
/*  5245 */         jjtree.popNode();
/*       */       }
/*  5247 */       if ((jjte000 instanceof RuntimeException)) {
/*  5248 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  5250 */       if ((jjte000 instanceof ParseException)) {
/*  5251 */         throw ((ParseException)jjte000);
/*       */       }
/*  5253 */       throw ((Error)jjte000);
/*       */     } finally {
/*  5255 */       if (jjtc000)
/*  5256 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5256 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void exception_spec()
/*       */     throws ParseException
/*       */   {
/*  5264 */     ASTNode jjtn000 = new ASTNode(61);
/*  5265 */     boolean jjtc000 = true;
/*  5266 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5268 */       throwKeyword();
/*  5269 */       lParenthesis();
/*  5270 */       exception_list();
/*  5271 */       rParenthesis();
/*       */     } catch (Throwable jjte000) {
/*  5273 */       if (jjtc000) {
/*  5274 */         jjtree.clearNodeScope(jjtn000);
/*  5275 */         jjtc000 = false;
/*       */       } else {
/*  5277 */         jjtree.popNode();
/*       */       }
/*  5279 */       if ((jjte000 instanceof RuntimeException)) {
/*  5280 */         throw ((RuntimeException)jjte000);
/*       */       }
/*  5282 */       if ((jjte000 instanceof ParseException)) {
/*  5283 */         throw ((ParseException)jjte000);
/*       */       }
/*  5285 */       throw ((Error)jjte000);
/*       */     } finally {
/*  5287 */       if (jjtc000)
/*  5288 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void exception_list() throws ParseException
/*       */   {
/*  5294 */     type_name();
/*       */     while (true)
/*       */     {
/*  5297 */       switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
/*       */       {
/*       */       case 45:
/*  5300 */         break;
/*       */       default:
/*  5302 */         jj_la1[''] = jj_gen;
/*  5303 */         break;
/*       */       }
/*  5305 */       comma();
/*  5306 */       type_name();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token lCurlyBrace()
/*       */     throws ParseException
/*       */   {
/*  5313 */     ASTNode jjtn000 = new ASTNode(62);
/*  5314 */     boolean jjtc000 = true;
/*  5315 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5317 */       Token t = jj_consume_token(36);
/*  5318 */       jjtree.closeNodeScope(jjtn000, true);
/*  5319 */       jjtc000 = false;
/*  5320 */       jjtn000.setSpecialToken(t.specialToken);
/*  5321 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*  5322 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  5324 */       if (jjtc000)
/*  5325 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5325 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token rCurlyBrace()
/*       */     throws ParseException
/*       */   {
/*  5333 */     ASTNode jjtn000 = new ASTNode(63);
/*  5334 */     boolean jjtc000 = true;
/*  5335 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5337 */       Token t = jj_consume_token(37);
/*  5338 */       jjtree.closeNodeScope(jjtn000, true);
/*  5339 */       jjtc000 = false;
/*  5340 */       jjtn000.setSpecialToken(t.specialToken);
/*  5341 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*  5342 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  5344 */       if (jjtc000)
/*  5345 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5345 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void lSquareBracket()
/*       */     throws ParseException
/*       */   {
/*  5353 */     ASTNode jjtn000 = new ASTNode(64);
/*  5354 */     boolean jjtc000 = true;
/*  5355 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5357 */       Token t = jj_consume_token(38);
/*  5358 */       jjtree.closeNodeScope(jjtn000, true);
/*  5359 */       jjtc000 = false;
/*  5360 */       jjtn000.setSpecialToken(t.specialToken);
/*  5361 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5364 */       if (jjtc000)
/*  5365 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void rSquareBracket()
/*       */     throws ParseException
/*       */   {
/*  5372 */     ASTNode jjtn000 = new ASTNode(65);
/*  5373 */     boolean jjtc000 = true;
/*  5374 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5376 */       Token t = jj_consume_token(39);
/*  5377 */       jjtree.closeNodeScope(jjtn000, true);
/*  5378 */       jjtc000 = false;
/*  5379 */       jjtn000.setSpecialToken(t.specialToken);
/*  5380 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5383 */       if (jjtc000)
/*  5384 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token lParenthesis()
/*       */     throws ParseException
/*       */   {
/*  5391 */     ASTNode jjtn000 = new ASTNode(66);
/*  5392 */     boolean jjtc000 = true;
/*  5393 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5395 */       Token t = jj_consume_token(40);
/*  5396 */       jjtree.closeNodeScope(jjtn000, true);
/*  5397 */       jjtc000 = false;
/*  5398 */       jjtn000.setBeginToken(t);
/*  5399 */       jjtn000.setSpecialToken(t.specialToken);
/*  5400 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  5402 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  5404 */       if (jjtc000)
/*  5405 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5405 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token rParenthesis()
/*       */     throws ParseException
/*       */   {
/*  5413 */     ASTNode jjtn000 = new ASTNode(67);
/*  5414 */     boolean jjtc000 = true;
/*  5415 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5417 */       Token t = jj_consume_token(41);
/*  5418 */       jjtree.closeNodeScope(jjtn000, true);
/*  5419 */       jjtc000 = false;
/*  5420 */       jjtn000.setBeginToken(t);
/*  5421 */       jjtn000.setSpecialToken(t.specialToken);
/*  5422 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  5424 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  5426 */       if (jjtc000)
/*  5427 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5427 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token scope()
/*       */     throws ParseException
/*       */   {
/*  5435 */     ASTNode jjtn000 = new ASTNode(68);
/*  5436 */     boolean jjtc000 = true;
/*  5437 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5439 */       Token t = jj_consume_token(42);
/*  5440 */       jjtree.closeNodeScope(jjtn000, true);
/*  5441 */       jjtc000 = false;
/*  5442 */       jjtn000.setSpecialToken(t.specialToken);
/*  5443 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  5445 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  5447 */       if (jjtc000)
/*  5448 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5448 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token colon()
/*       */     throws ParseException
/*       */   {
/*  5456 */     ASTNode jjtn000 = new ASTNode(69);
/*  5457 */     boolean jjtc000 = true;
/*  5458 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5460 */       Token t = jj_consume_token(43);
/*  5461 */       jjtree.closeNodeScope(jjtn000, true);
/*  5462 */       jjtc000 = false;
/*  5463 */       jjtn000.setSpecialToken(t.specialToken);
/*  5464 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  5466 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  5468 */       if (jjtc000)
/*  5469 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5469 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token semicolon()
/*       */     throws ParseException
/*       */   {
/*  5477 */     ASTNode jjtn000 = new ASTNode(70);
/*  5478 */     boolean jjtc000 = true;
/*  5479 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5481 */       Token t = jj_consume_token(44);
/*  5482 */       jjtree.closeNodeScope(jjtn000, true);
/*  5483 */       jjtc000 = false;
/*  5484 */       jjtn000.setSpecialToken(t.specialToken);
/*  5485 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  5487 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  5489 */       if (jjtc000)
/*  5490 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5490 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token comma()
/*       */     throws ParseException
/*       */   {
/*  5498 */     ASTNode jjtn000 = new ASTNode(71);
/*  5499 */     boolean jjtc000 = true;
/*  5500 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5502 */       Token t = jj_consume_token(45);
/*  5503 */       jjtree.closeNodeScope(jjtn000, true);
/*  5504 */       jjtc000 = false;
/*  5505 */       jjtn000.setSpecialToken(t.specialToken);
/*  5506 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  5508 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  5510 */       if (jjtc000)
/*  5511 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5511 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void questionMark()
/*       */     throws ParseException
/*       */   {
/*  5519 */     ASTNode jjtn000 = new ASTNode(72);
/*  5520 */     boolean jjtc000 = true;
/*  5521 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5523 */       Token t = jj_consume_token(46);
/*  5524 */       jjtree.closeNodeScope(jjtn000, true);
/*  5525 */       jjtc000 = false;
/*  5526 */       jjtn000.setSpecialToken(t.specialToken);
/*  5527 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5530 */       if (jjtc000)
/*  5531 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token ellipsis()
/*       */     throws ParseException
/*       */   {
/*  5538 */     ASTNode jjtn000 = new ASTNode(73);
/*  5539 */     boolean jjtc000 = true;
/*  5540 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5542 */       Token t = jj_consume_token(47);
/*  5543 */       jjtree.closeNodeScope(jjtn000, true);
/*  5544 */       jjtc000 = false;
/*  5545 */       jjtn000.setSpecialToken(t.specialToken);
/*  5546 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  5548 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  5550 */       if (jjtc000)
/*  5551 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5551 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token assignEqual()
/*       */     throws ParseException
/*       */   {
/*  5559 */     ASTNode jjtn000 = new ASTNode(74);
/*  5560 */     boolean jjtc000 = true;
/*  5561 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5563 */       Token t = jj_consume_token(48);
/*  5564 */       jjtree.closeNodeScope(jjtn000, true);
/*  5565 */       jjtc000 = false;
/*  5566 */       jjtn000.setSpecialToken(t.specialToken);
/*  5567 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  5569 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  5571 */       if (jjtc000)
/*  5572 */         jjtree.closeNodeScope(jjtn000, true); 
/*  5572 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void timesEqual()
/*       */     throws ParseException
/*       */   {
/*  5580 */     ASTNode jjtn000 = new ASTNode(75);
/*  5581 */     boolean jjtc000 = true;
/*  5582 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5584 */       Token t = jj_consume_token(49);
/*  5585 */       jjtree.closeNodeScope(jjtn000, true);
/*  5586 */       jjtc000 = false;
/*  5587 */       jjtn000.setSpecialToken(t.specialToken);
/*  5588 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5591 */       if (jjtc000)
/*  5592 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void divideEqual()
/*       */     throws ParseException
/*       */   {
/*  5599 */     ASTNode jjtn000 = new ASTNode(76);
/*  5600 */     boolean jjtc000 = true;
/*  5601 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5603 */       Token t = jj_consume_token(50);
/*  5604 */       jjtree.closeNodeScope(jjtn000, true);
/*  5605 */       jjtc000 = false;
/*  5606 */       jjtn000.setSpecialToken(t.specialToken);
/*  5607 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5610 */       if (jjtc000)
/*  5611 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void modEqual()
/*       */     throws ParseException
/*       */   {
/*  5618 */     ASTNode jjtn000 = new ASTNode(77);
/*  5619 */     boolean jjtc000 = true;
/*  5620 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5622 */       Token t = jj_consume_token(51);
/*  5623 */       jjtree.closeNodeScope(jjtn000, true);
/*  5624 */       jjtc000 = false;
/*  5625 */       jjtn000.setSpecialToken(t.specialToken);
/*  5626 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5629 */       if (jjtc000)
/*  5630 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void plusEqual()
/*       */     throws ParseException
/*       */   {
/*  5637 */     ASTNode jjtn000 = new ASTNode(78);
/*  5638 */     boolean jjtc000 = true;
/*  5639 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5641 */       Token t = jj_consume_token(52);
/*  5642 */       jjtree.closeNodeScope(jjtn000, true);
/*  5643 */       jjtc000 = false;
/*  5644 */       jjtn000.setSpecialToken(t.specialToken);
/*  5645 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5648 */       if (jjtc000)
/*  5649 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void minusEqual()
/*       */     throws ParseException
/*       */   {
/*  5656 */     ASTNode jjtn000 = new ASTNode(79);
/*  5657 */     boolean jjtc000 = true;
/*  5658 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5660 */       Token t = jj_consume_token(53);
/*  5661 */       jjtree.closeNodeScope(jjtn000, true);
/*  5662 */       jjtc000 = false;
/*  5663 */       jjtn000.setSpecialToken(t.specialToken);
/*  5664 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5667 */       if (jjtc000)
/*  5668 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void shiftLeftEqual()
/*       */     throws ParseException
/*       */   {
/*  5675 */     ASTNode jjtn000 = new ASTNode(80);
/*  5676 */     boolean jjtc000 = true;
/*  5677 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5679 */       Token t = jj_consume_token(54);
/*  5680 */       jjtree.closeNodeScope(jjtn000, true);
/*  5681 */       jjtc000 = false;
/*  5682 */       jjtn000.setSpecialToken(t.specialToken);
/*  5683 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5686 */       if (jjtc000)
/*  5687 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void shiftRightEqual()
/*       */     throws ParseException
/*       */   {
/*  5694 */     ASTNode jjtn000 = new ASTNode(81);
/*  5695 */     boolean jjtc000 = true;
/*  5696 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5698 */       Token t = jj_consume_token(55);
/*  5699 */       jjtree.closeNodeScope(jjtn000, true);
/*  5700 */       jjtc000 = false;
/*  5701 */       jjtn000.setSpecialToken(t.specialToken);
/*  5702 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5705 */       if (jjtc000)
/*  5706 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void bitwiseAndEqual()
/*       */     throws ParseException
/*       */   {
/*  5713 */     ASTNode jjtn000 = new ASTNode(82);
/*  5714 */     boolean jjtc000 = true;
/*  5715 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5717 */       Token t = jj_consume_token(56);
/*  5718 */       jjtree.closeNodeScope(jjtn000, true);
/*  5719 */       jjtc000 = false;
/*  5720 */       jjtn000.setSpecialToken(t.specialToken);
/*  5721 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5724 */       if (jjtc000)
/*  5725 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void bitwiseXorEqual()
/*       */     throws ParseException
/*       */   {
/*  5732 */     ASTNode jjtn000 = new ASTNode(83);
/*  5733 */     boolean jjtc000 = true;
/*  5734 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5736 */       Token t = jj_consume_token(57);
/*  5737 */       jjtree.closeNodeScope(jjtn000, true);
/*  5738 */       jjtc000 = false;
/*  5739 */       jjtn000.setSpecialToken(t.specialToken);
/*  5740 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5743 */       if (jjtc000)
/*  5744 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void bitwiseOrEqual()
/*       */     throws ParseException
/*       */   {
/*  5751 */     ASTNode jjtn000 = new ASTNode(84);
/*  5752 */     boolean jjtc000 = true;
/*  5753 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5755 */       Token t = jj_consume_token(58);
/*  5756 */       jjtree.closeNodeScope(jjtn000, true);
/*  5757 */       jjtc000 = false;
/*  5758 */       jjtn000.setSpecialToken(t.specialToken);
/*  5759 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5762 */       if (jjtc000)
/*  5763 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void or()
/*       */     throws ParseException
/*       */   {
/*  5770 */     ASTNode jjtn000 = new ASTNode(85);
/*  5771 */     boolean jjtc000 = true;
/*  5772 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5774 */       Token t = jj_consume_token(59);
/*  5775 */       jjtree.closeNodeScope(jjtn000, true);
/*  5776 */       jjtc000 = false;
/*  5777 */       jjtn000.setSpecialToken(t.specialToken);
/*  5778 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5781 */       if (jjtc000)
/*  5782 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void and()
/*       */     throws ParseException
/*       */   {
/*  5789 */     ASTNode jjtn000 = new ASTNode(86);
/*  5790 */     boolean jjtc000 = true;
/*  5791 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5793 */       Token t = jj_consume_token(60);
/*  5794 */       jjtree.closeNodeScope(jjtn000, true);
/*  5795 */       jjtc000 = false;
/*  5796 */       jjtn000.setSpecialToken(t.specialToken);
/*  5797 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5800 */       if (jjtc000)
/*  5801 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void bitwiseOr()
/*       */     throws ParseException
/*       */   {
/*  5808 */     ASTNode jjtn000 = new ASTNode(87);
/*  5809 */     boolean jjtc000 = true;
/*  5810 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5812 */       Token t = jj_consume_token(61);
/*  5813 */       jjtree.closeNodeScope(jjtn000, true);
/*  5814 */       jjtc000 = false;
/*  5815 */       jjtn000.setSpecialToken(t.specialToken);
/*  5816 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5819 */       if (jjtc000)
/*  5820 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void bitwiseXor()
/*       */     throws ParseException
/*       */   {
/*  5827 */     ASTNode jjtn000 = new ASTNode(88);
/*  5828 */     boolean jjtc000 = true;
/*  5829 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5831 */       Token t = jj_consume_token(62);
/*  5832 */       jjtree.closeNodeScope(jjtn000, true);
/*  5833 */       jjtc000 = false;
/*  5834 */       jjtn000.setSpecialToken(t.specialToken);
/*  5835 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5838 */       if (jjtc000)
/*  5839 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void ampersand()
/*       */     throws ParseException
/*       */   {
/*  5846 */     ASTNode jjtn000 = new ASTNode(89);
/*  5847 */     boolean jjtc000 = true;
/*  5848 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5850 */       Token t = jj_consume_token(63);
/*  5851 */       jjtree.closeNodeScope(jjtn000, true);
/*  5852 */       jjtc000 = false;
/*  5853 */       jjtn000.setSpecialToken(t.specialToken);
/*  5854 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5857 */       if (jjtc000)
/*  5858 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void equal()
/*       */     throws ParseException
/*       */   {
/*  5865 */     ASTNode jjtn000 = new ASTNode(90);
/*  5866 */     boolean jjtc000 = true;
/*  5867 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5869 */       Token t = jj_consume_token(64);
/*  5870 */       jjtree.closeNodeScope(jjtn000, true);
/*  5871 */       jjtc000 = false;
/*  5872 */       jjtn000.setSpecialToken(t.specialToken);
/*  5873 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5876 */       if (jjtc000)
/*  5877 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void notEqual()
/*       */     throws ParseException
/*       */   {
/*  5884 */     ASTNode jjtn000 = new ASTNode(91);
/*  5885 */     boolean jjtc000 = true;
/*  5886 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5888 */       Token t = jj_consume_token(65);
/*  5889 */       jjtree.closeNodeScope(jjtn000, true);
/*  5890 */       jjtc000 = false;
/*  5891 */       jjtn000.setSpecialToken(t.specialToken);
/*  5892 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5895 */       if (jjtc000)
/*  5896 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void lessThan()
/*       */     throws ParseException
/*       */   {
/*  5903 */     ASTNode jjtn000 = new ASTNode(92);
/*  5904 */     boolean jjtc000 = true;
/*  5905 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5907 */       Token t = jj_consume_token(66);
/*  5908 */       jjtree.closeNodeScope(jjtn000, true);
/*  5909 */       jjtc000 = false;
/*  5910 */       jjtn000.setSpecialToken(t.specialToken);
/*  5911 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5914 */       if (jjtc000)
/*  5915 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void greaterThan()
/*       */     throws ParseException
/*       */   {
/*  5922 */     ASTNode jjtn000 = new ASTNode(93);
/*  5923 */     boolean jjtc000 = true;
/*  5924 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5926 */       Token t = jj_consume_token(67);
/*  5927 */       jjtree.closeNodeScope(jjtn000, true);
/*  5928 */       jjtc000 = false;
/*  5929 */       jjtn000.setSpecialToken(t.specialToken);
/*  5930 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5933 */       if (jjtc000)
/*  5934 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void lessThanOrEqualTo()
/*       */     throws ParseException
/*       */   {
/*  5941 */     ASTNode jjtn000 = new ASTNode(94);
/*  5942 */     boolean jjtc000 = true;
/*  5943 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5945 */       Token t = jj_consume_token(68);
/*  5946 */       jjtree.closeNodeScope(jjtn000, true);
/*  5947 */       jjtc000 = false;
/*  5948 */       jjtn000.setSpecialToken(t.specialToken);
/*  5949 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5952 */       if (jjtc000)
/*  5953 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void greaterThanOrEqualTo()
/*       */     throws ParseException
/*       */   {
/*  5960 */     ASTNode jjtn000 = new ASTNode(95);
/*  5961 */     boolean jjtc000 = true;
/*  5962 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5964 */       Token t = jj_consume_token(69);
/*  5965 */       jjtree.closeNodeScope(jjtn000, true);
/*  5966 */       jjtc000 = false;
/*  5967 */       jjtn000.setSpecialToken(t.specialToken);
/*  5968 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5971 */       if (jjtc000)
/*  5972 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void shiftLeft()
/*       */     throws ParseException
/*       */   {
/*  5979 */     ASTNode jjtn000 = new ASTNode(96);
/*  5980 */     boolean jjtc000 = true;
/*  5981 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  5983 */       Token t = jj_consume_token(70);
/*  5984 */       jjtree.closeNodeScope(jjtn000, true);
/*  5985 */       jjtc000 = false;
/*  5986 */       jjtn000.setSpecialToken(t.specialToken);
/*  5987 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  5990 */       if (jjtc000)
/*  5991 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void shiftRight()
/*       */     throws ParseException
/*       */   {
/*  5998 */     ASTNode jjtn000 = new ASTNode(97);
/*  5999 */     boolean jjtc000 = true;
/*  6000 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6002 */       Token t = jj_consume_token(71);
/*  6003 */       jjtree.closeNodeScope(jjtn000, true);
/*  6004 */       jjtc000 = false;
/*  6005 */       jjtn000.setSpecialToken(t.specialToken);
/*  6006 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6009 */       if (jjtc000)
/*  6010 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void plus()
/*       */     throws ParseException
/*       */   {
/*  6017 */     ASTNode jjtn000 = new ASTNode(98);
/*  6018 */     boolean jjtc000 = true;
/*  6019 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6021 */       Token t = jj_consume_token(72);
/*  6022 */       jjtree.closeNodeScope(jjtn000, true);
/*  6023 */       jjtc000 = false;
/*  6024 */       jjtn000.setSpecialToken(t.specialToken);
/*  6025 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6028 */       if (jjtc000)
/*  6029 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void minus()
/*       */     throws ParseException
/*       */   {
/*  6036 */     ASTNode jjtn000 = new ASTNode(99);
/*  6037 */     boolean jjtc000 = true;
/*  6038 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6040 */       Token t = jj_consume_token(73);
/*  6041 */       jjtree.closeNodeScope(jjtn000, true);
/*  6042 */       jjtc000 = false;
/*  6043 */       jjtn000.setSpecialToken(t.specialToken);
/*  6044 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6047 */       if (jjtc000)
/*  6048 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void star()
/*       */     throws ParseException
/*       */   {
/*  6055 */     ASTNode jjtn000 = new ASTNode(100);
/*  6056 */     boolean jjtc000 = true;
/*  6057 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6059 */       Token t = jj_consume_token(74);
/*  6060 */       jjtree.closeNodeScope(jjtn000, true);
/*  6061 */       jjtc000 = false;
/*  6062 */       jjtn000.setSpecialToken(t.specialToken);
/*  6063 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6066 */       if (jjtc000)
/*  6067 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void divide()
/*       */     throws ParseException
/*       */   {
/*  6074 */     ASTNode jjtn000 = new ASTNode(101);
/*  6075 */     boolean jjtc000 = true;
/*  6076 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6078 */       Token t = jj_consume_token(75);
/*  6079 */       jjtree.closeNodeScope(jjtn000, true);
/*  6080 */       jjtc000 = false;
/*  6081 */       jjtn000.setSpecialToken(t.specialToken);
/*  6082 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6085 */       if (jjtc000)
/*  6086 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void mod()
/*       */     throws ParseException
/*       */   {
/*  6093 */     ASTNode jjtn000 = new ASTNode(102);
/*  6094 */     boolean jjtc000 = true;
/*  6095 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6097 */       Token t = jj_consume_token(76);
/*  6098 */       jjtree.closeNodeScope(jjtn000, true);
/*  6099 */       jjtc000 = false;
/*  6100 */       jjtn000.setSpecialToken(t.specialToken);
/*  6101 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6104 */       if (jjtc000)
/*  6105 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void plusPlus()
/*       */     throws ParseException
/*       */   {
/*  6112 */     ASTNode jjtn000 = new ASTNode(103);
/*  6113 */     boolean jjtc000 = true;
/*  6114 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6116 */       Token t = jj_consume_token(77);
/*  6117 */       jjtree.closeNodeScope(jjtn000, true);
/*  6118 */       jjtc000 = false;
/*  6119 */       jjtn000.setSpecialToken(t.specialToken);
/*  6120 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6123 */       if (jjtc000)
/*  6124 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void minusMinus()
/*       */     throws ParseException
/*       */   {
/*  6131 */     ASTNode jjtn000 = new ASTNode(104);
/*  6132 */     boolean jjtc000 = true;
/*  6133 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6135 */       Token t = jj_consume_token(78);
/*  6136 */       jjtree.closeNodeScope(jjtn000, true);
/*  6137 */       jjtc000 = false;
/*  6138 */       jjtn000.setSpecialToken(t.specialToken);
/*  6139 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6142 */       if (jjtc000)
/*  6143 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void tilde()
/*       */     throws ParseException
/*       */   {
/*  6150 */     ASTNode jjtn000 = new ASTNode(105);
/*  6151 */     boolean jjtc000 = true;
/*  6152 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6154 */       Token t = jj_consume_token(79);
/*  6155 */       jjtree.closeNodeScope(jjtn000, true);
/*  6156 */       jjtc000 = false;
/*  6157 */       jjtn000.setSpecialToken(t.specialToken);
/*  6158 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6161 */       if (jjtc000)
/*  6162 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void not()
/*       */     throws ParseException
/*       */   {
/*  6169 */     ASTNode jjtn000 = new ASTNode(106);
/*  6170 */     boolean jjtc000 = true;
/*  6171 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6173 */       Token t = jj_consume_token(80);
/*  6174 */       jjtree.closeNodeScope(jjtn000, true);
/*  6175 */       jjtc000 = false;
/*  6176 */       jjtn000.setSpecialToken(t.specialToken);
/*  6177 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6180 */       if (jjtc000)
/*  6181 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void dot()
/*       */     throws ParseException
/*       */   {
/*  6188 */     ASTNode jjtn000 = new ASTNode(107);
/*  6189 */     boolean jjtc000 = true;
/*  6190 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6192 */       Token t = jj_consume_token(81);
/*  6193 */       jjtree.closeNodeScope(jjtn000, true);
/*  6194 */       jjtc000 = false;
/*  6195 */       jjtn000.setSpecialToken(t.specialToken);
/*  6196 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6199 */       if (jjtc000)
/*  6200 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void pointerTo()
/*       */     throws ParseException
/*       */   {
/*  6207 */     ASTNode jjtn000 = new ASTNode(108);
/*  6208 */     boolean jjtc000 = true;
/*  6209 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6211 */       Token t = jj_consume_token(82);
/*  6212 */       jjtree.closeNodeScope(jjtn000, true);
/*  6213 */       jjtc000 = false;
/*  6214 */       jjtn000.setSpecialToken(t.specialToken);
/*  6215 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6218 */       if (jjtc000)
/*  6219 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void dotStar()
/*       */     throws ParseException
/*       */   {
/*  6226 */     ASTNode jjtn000 = new ASTNode(109);
/*  6227 */     boolean jjtc000 = true;
/*  6228 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6230 */       Token t = jj_consume_token(83);
/*  6231 */       jjtree.closeNodeScope(jjtn000, true);
/*  6232 */       jjtc000 = false;
/*  6233 */       jjtn000.setSpecialToken(t.specialToken);
/*  6234 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6237 */       if (jjtc000)
/*  6238 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void arrowStar()
/*       */     throws ParseException
/*       */   {
/*  6245 */     ASTNode jjtn000 = new ASTNode(110);
/*  6246 */     boolean jjtc000 = true;
/*  6247 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6249 */       Token t = jj_consume_token(84);
/*  6250 */       jjtree.closeNodeScope(jjtn000, true);
/*  6251 */       jjtc000 = false;
/*  6252 */       jjtn000.setSpecialToken(t.specialToken);
/*  6253 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6256 */       if (jjtc000)
/*  6257 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token autoKeyword()
/*       */     throws ParseException
/*       */   {
/*  6264 */     ASTNode jjtn000 = new ASTNode(111);
/*  6265 */     boolean jjtc000 = true;
/*  6266 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6268 */       Token t = jj_consume_token(85);
/*  6269 */       jjtree.closeNodeScope(jjtn000, true);
/*  6270 */       jjtc000 = false;
/*  6271 */       jjtn000.setSpecialToken(t.specialToken);
/*  6272 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6274 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6276 */       if (jjtc000)
/*  6277 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6277 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token breakKeyword()
/*       */     throws ParseException
/*       */   {
/*  6285 */     ASTNode jjtn000 = new ASTNode(112);
/*  6286 */     boolean jjtc000 = true;
/*  6287 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6289 */       Token t = jj_consume_token(86);
/*  6290 */       jjtree.closeNodeScope(jjtn000, true);
/*  6291 */       jjtc000 = false;
/*  6292 */       jjtn000.setSpecialToken(t.specialToken);
/*  6293 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6295 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6297 */       if (jjtc000)
/*  6298 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6298 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token caseKeyword()
/*       */     throws ParseException
/*       */   {
/*  6306 */     ASTNode jjtn000 = new ASTNode(113);
/*  6307 */     boolean jjtc000 = true;
/*  6308 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6310 */       Token t = jj_consume_token(89);
/*  6311 */       jjtree.closeNodeScope(jjtn000, true);
/*  6312 */       jjtc000 = false;
/*  6313 */       jjtn000.setSpecialToken(t.specialToken);
/*  6314 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6316 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6318 */       if (jjtc000)
/*  6319 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6319 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void catchKeyword()
/*       */     throws ParseException
/*       */   {
/*  6327 */     ASTNode jjtn000 = new ASTNode(114);
/*  6328 */     boolean jjtc000 = true;
/*  6329 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6331 */       Token t = jj_consume_token(90);
/*  6332 */       jjtree.closeNodeScope(jjtn000, true);
/*  6333 */       jjtc000 = false;
/*  6334 */       jjtn000.setSpecialToken(t.specialToken);
/*  6335 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6338 */       if (jjtc000)
/*  6339 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void charKeyword()
/*       */     throws ParseException
/*       */   {
/*  6346 */     ASTNode jjtn000 = new ASTNode(115);
/*  6347 */     boolean jjtc000 = true;
/*  6348 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6350 */       Token t = jj_consume_token(91);
/*  6351 */       jjtree.closeNodeScope(jjtn000, true);
/*  6352 */       jjtc000 = false;
/*  6353 */       jjtn000.setSpecialToken(t.specialToken);
/*  6354 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6357 */       if (jjtc000)
/*  6358 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void constKeyword()
/*       */     throws ParseException
/*       */   {
/*  6365 */     ASTNode jjtn000 = new ASTNode(116);
/*  6366 */     boolean jjtc000 = true;
/*  6367 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6369 */       Token t = jj_consume_token(92);
/*  6370 */       jjtree.closeNodeScope(jjtn000, true);
/*  6371 */       jjtc000 = false;
/*  6372 */       jjtn000.setSpecialToken(t.specialToken);
/*  6373 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6376 */       if (jjtc000)
/*  6377 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void continueKeyword()
/*       */     throws ParseException
/*       */   {
/*  6384 */     ASTNode jjtn000 = new ASTNode(117);
/*  6385 */     boolean jjtc000 = true;
/*  6386 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6388 */       Token t = jj_consume_token(93);
/*  6389 */       jjtree.closeNodeScope(jjtn000, true);
/*  6390 */       jjtc000 = false;
/*  6391 */       jjtn000.setSpecialToken(t.specialToken);
/*  6392 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6395 */       if (jjtc000)
/*  6396 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token defaultKeyword()
/*       */     throws ParseException
/*       */   {
/*  6403 */     ASTNode jjtn000 = new ASTNode(118);
/*  6404 */     boolean jjtc000 = true;
/*  6405 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6407 */       Token t = jj_consume_token(94);
/*  6408 */       jjtree.closeNodeScope(jjtn000, true);
/*  6409 */       jjtc000 = false;
/*  6410 */       jjtn000.setSpecialToken(t.specialToken);
/*  6411 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6413 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6415 */       if (jjtc000)
/*  6416 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6416 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void deleteKeyword()
/*       */     throws ParseException
/*       */   {
/*  6424 */     ASTNode jjtn000 = new ASTNode(119);
/*  6425 */     boolean jjtc000 = true;
/*  6426 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6428 */       Token t = jj_consume_token(95);
/*  6429 */       jjtree.closeNodeScope(jjtn000, true);
/*  6430 */       jjtc000 = false;
/*  6431 */       jjtn000.setSpecialToken(t.specialToken);
/*  6432 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6435 */       if (jjtc000)
/*  6436 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void doKeyword()
/*       */     throws ParseException
/*       */   {
/*  6443 */     ASTNode jjtn000 = new ASTNode(120);
/*  6444 */     boolean jjtc000 = true;
/*  6445 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6447 */       Token t = jj_consume_token(96);
/*  6448 */       jjtree.closeNodeScope(jjtn000, true);
/*  6449 */       jjtc000 = false;
/*  6450 */       jjtn000.setSpecialToken(t.specialToken);
/*  6451 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6454 */       if (jjtc000)
/*  6455 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void doubleKeyword()
/*       */     throws ParseException
/*       */   {
/*  6462 */     ASTNode jjtn000 = new ASTNode(121);
/*  6463 */     boolean jjtc000 = true;
/*  6464 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6466 */       Token t = jj_consume_token(97);
/*  6467 */       jjtree.closeNodeScope(jjtn000, true);
/*  6468 */       jjtc000 = false;
/*  6469 */       jjtn000.setSpecialToken(t.specialToken);
/*  6470 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6473 */       if (jjtc000)
/*  6474 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token elseKeyword()
/*       */     throws ParseException
/*       */   {
/*  6481 */     ASTNode jjtn000 = new ASTNode(122);
/*  6482 */     boolean jjtc000 = true;
/*  6483 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6485 */       Token t = jj_consume_token(98);
/*  6486 */       jjtree.closeNodeScope(jjtn000, true);
/*  6487 */       jjtc000 = false;
/*  6488 */       jjtn000.setBeginToken(t);
/*  6489 */       jjtn000.setSpecialToken(t.specialToken);
/*  6490 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6492 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6494 */       if (jjtc000)
/*  6495 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6495 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token enumKeyword()
/*       */     throws ParseException
/*       */   {
/*  6503 */     ASTNode jjtn000 = new ASTNode(123);
/*  6504 */     boolean jjtc000 = true;
/*  6505 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6507 */       Token t = jj_consume_token(99);
/*  6508 */       jjtree.closeNodeScope(jjtn000, true);
/*  6509 */       jjtc000 = false;
/*  6510 */       jjtn000.setSpecialToken(t.specialToken);
/*  6511 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6513 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6515 */       if (jjtc000)
/*  6516 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6516 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token externKeyword()
/*       */     throws ParseException
/*       */   {
/*  6524 */     ASTNode jjtn000 = new ASTNode(124);
/*  6525 */     boolean jjtc000 = true;
/*  6526 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6528 */       Token t = jj_consume_token(100);
/*  6529 */       jjtree.closeNodeScope(jjtn000, true);
/*  6530 */       jjtc000 = false;
/*  6531 */       jjtn000.setSpecialToken(t.specialToken);
/*  6532 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6534 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6536 */       if (jjtc000)
/*  6537 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6537 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void finallyKeyword()
/*       */     throws ParseException
/*       */   {
/*  6545 */     ASTNode jjtn000 = new ASTNode(125);
/*  6546 */     boolean jjtc000 = true;
/*  6547 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6549 */       Token t = jj_consume_token(101);
/*  6550 */       jjtree.closeNodeScope(jjtn000, true);
/*  6551 */       jjtc000 = false;
/*  6552 */       jjtn000.setSpecialToken(t.specialToken);
/*  6553 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6556 */       if (jjtc000)
/*  6557 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void floatKeyword()
/*       */     throws ParseException
/*       */   {
/*  6564 */     ASTNode jjtn000 = new ASTNode(126);
/*  6565 */     boolean jjtc000 = true;
/*  6566 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6568 */       Token t = jj_consume_token(102);
/*  6569 */       jjtree.closeNodeScope(jjtn000, true);
/*  6570 */       jjtc000 = false;
/*  6571 */       jjtn000.setSpecialToken(t.specialToken);
/*  6572 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6575 */       if (jjtc000)
/*  6576 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token forKeyword()
/*       */     throws ParseException
/*       */   {
/*  6583 */     ASTNode jjtn000 = new ASTNode(127);
/*  6584 */     boolean jjtc000 = true;
/*  6585 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6587 */       Token t = jj_consume_token(103);
/*  6588 */       jjtree.closeNodeScope(jjtn000, true);
/*  6589 */       jjtc000 = false;
/*  6590 */       jjtn000.setSpecialToken(t.specialToken);
/*  6591 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6593 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6595 */       if (jjtc000)
/*  6596 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6596 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void friendKeyword()
/*       */     throws ParseException
/*       */   {
/*  6604 */     ASTNode jjtn000 = new ASTNode(128);
/*  6605 */     boolean jjtc000 = true;
/*  6606 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6608 */       Token t = jj_consume_token(104);
/*  6609 */       jjtree.closeNodeScope(jjtn000, true);
/*  6610 */       jjtc000 = false;
/*  6611 */       jjtn000.setSpecialToken(t.specialToken);
/*  6612 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6615 */       if (jjtc000)
/*  6616 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void gotoKeyword()
/*       */     throws ParseException
/*       */   {
/*  6623 */     ASTNode jjtn000 = new ASTNode(129);
/*  6624 */     boolean jjtc000 = true;
/*  6625 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6627 */       Token t = jj_consume_token(105);
/*  6628 */       jjtree.closeNodeScope(jjtn000, true);
/*  6629 */       jjtc000 = false;
/*  6630 */       jjtn000.setSpecialToken(t.specialToken);
/*  6631 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6634 */       if (jjtc000)
/*  6635 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void ifKeyword()
/*       */     throws ParseException
/*       */   {
/*  6642 */     ASTNode jjtn000 = new ASTNode(130);
/*  6643 */     boolean jjtc000 = true;
/*  6644 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6646 */       Token t = jj_consume_token(106);
/*  6647 */       jjtree.closeNodeScope(jjtn000, true);
/*  6648 */       jjtc000 = false;
/*  6649 */       jjtn000.setSpecialToken(t.specialToken);
/*  6650 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6653 */       if (jjtc000)
/*  6654 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void inlineKeyword()
/*       */     throws ParseException
/*       */   {
/*  6661 */     ASTNode jjtn000 = new ASTNode(131);
/*  6662 */     boolean jjtc000 = true;
/*  6663 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6665 */       Token t = jj_consume_token(107);
/*  6666 */       jjtree.closeNodeScope(jjtn000, true);
/*  6667 */       jjtc000 = false;
/*  6668 */       jjtn000.setSpecialToken(t.specialToken);
/*  6669 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6672 */       if (jjtc000)
/*  6673 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void intKeyword()
/*       */     throws ParseException
/*       */   {
/*  6680 */     ASTNode jjtn000 = new ASTNode(132);
/*  6681 */     boolean jjtc000 = true;
/*  6682 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6684 */       Token t = jj_consume_token(108);
/*  6685 */       jjtree.closeNodeScope(jjtn000, true);
/*  6686 */       jjtc000 = false;
/*  6687 */       jjtn000.setSpecialToken(t.specialToken);
/*  6688 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6691 */       if (jjtc000)
/*  6692 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void longKeyword()
/*       */     throws ParseException
/*       */   {
/*  6699 */     ASTNode jjtn000 = new ASTNode(133);
/*  6700 */     boolean jjtc000 = true;
/*  6701 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6703 */       Token t = jj_consume_token(109);
/*  6704 */       jjtree.closeNodeScope(jjtn000, true);
/*  6705 */       jjtc000 = false;
/*  6706 */       jjtn000.setSpecialToken(t.specialToken);
/*  6707 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6710 */       if (jjtc000)
/*  6711 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void newKeyword()
/*       */     throws ParseException
/*       */   {
/*  6718 */     ASTNode jjtn000 = new ASTNode(134);
/*  6719 */     boolean jjtc000 = true;
/*  6720 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6722 */       Token t = jj_consume_token(110);
/*  6723 */       jjtree.closeNodeScope(jjtn000, true);
/*  6724 */       jjtc000 = false;
/*  6725 */       jjtn000.setSpecialToken(t.specialToken);
/*  6726 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6729 */       if (jjtc000)
/*  6730 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token privateKeyword()
/*       */     throws ParseException
/*       */   {
/*  6737 */     ASTNode jjtn000 = new ASTNode(135);
/*  6738 */     boolean jjtc000 = true;
/*  6739 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6741 */       Token t = jj_consume_token(111);
/*  6742 */       jjtree.closeNodeScope(jjtn000, true);
/*  6743 */       jjtc000 = false;
/*  6744 */       jjtn000.setSpecialToken(t.specialToken);
/*  6745 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6747 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6749 */       if (jjtc000)
/*  6750 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6750 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token protectedKeyword()
/*       */     throws ParseException
/*       */   {
/*  6758 */     ASTNode jjtn000 = new ASTNode(136);
/*  6759 */     boolean jjtc000 = true;
/*  6760 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6762 */       Token t = jj_consume_token(112);
/*  6763 */       jjtree.closeNodeScope(jjtn000, true);
/*  6764 */       jjtc000 = false;
/*  6765 */       jjtn000.setSpecialToken(t.specialToken);
/*  6766 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6768 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6770 */       if (jjtc000)
/*  6771 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6771 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token publicKeyword()
/*       */     throws ParseException
/*       */   {
/*  6779 */     ASTNode jjtn000 = new ASTNode(137);
/*  6780 */     boolean jjtc000 = true;
/*  6781 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6783 */       Token t = jj_consume_token(113);
/*  6784 */       jjtree.closeNodeScope(jjtn000, true);
/*  6785 */       jjtc000 = false;
/*  6786 */       jjtn000.setSpecialToken(t.specialToken);
/*  6787 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6789 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6791 */       if (jjtc000)
/*  6792 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6792 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void redeclaredKeyword()
/*       */     throws ParseException
/*       */   {
/*  6800 */     ASTNode jjtn000 = new ASTNode(138);
/*  6801 */     boolean jjtc000 = true;
/*  6802 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6804 */       Token t = jj_consume_token(114);
/*  6805 */       jjtree.closeNodeScope(jjtn000, true);
/*  6806 */       jjtc000 = false;
/*  6807 */       jjtn000.setSpecialToken(t.specialToken);
/*  6808 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6811 */       if (jjtc000)
/*  6812 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void registerKeyword()
/*       */     throws ParseException
/*       */   {
/*  6819 */     ASTNode jjtn000 = new ASTNode(139);
/*  6820 */     boolean jjtc000 = true;
/*  6821 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6823 */       Token t = jj_consume_token(115);
/*  6824 */       jjtree.closeNodeScope(jjtn000, true);
/*  6825 */       jjtc000 = false;
/*  6826 */       jjtn000.setSpecialToken(t.specialToken);
/*  6827 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6830 */       if (jjtc000)
/*  6831 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token returnKeyword()
/*       */     throws ParseException
/*       */   {
/*  6838 */     ASTNode jjtn000 = new ASTNode(140);
/*  6839 */     boolean jjtc000 = true;
/*  6840 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6842 */       Token t = jj_consume_token(116);
/*  6843 */       jjtree.closeNodeScope(jjtn000, true);
/*  6844 */       jjtc000 = false;
/*  6845 */       jjtn000.setSpecialToken(t.specialToken);
/*  6846 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6848 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6850 */       if (jjtc000)
/*  6851 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6851 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void boolKeyword()
/*       */     throws ParseException
/*       */   {
/*  6859 */     ASTNode jjtn000 = new ASTNode(141);
/*  6860 */     boolean jjtc000 = true;
/*  6861 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6863 */       Token t = jj_consume_token(87);
/*  6864 */       jjtree.closeNodeScope(jjtn000, true);
/*  6865 */       jjtc000 = false;
/*  6866 */       jjtn000.setSpecialToken(t.specialToken);
/*  6867 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6870 */       if (jjtc000)
/*  6871 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void booleanKeyword()
/*       */     throws ParseException
/*       */   {
/*  6878 */     ASTNode jjtn000 = new ASTNode(142);
/*  6879 */     boolean jjtc000 = true;
/*  6880 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6882 */       Token t = jj_consume_token(88);
/*  6883 */       jjtree.closeNodeScope(jjtn000, true);
/*  6884 */       jjtc000 = false;
/*  6885 */       jjtn000.setSpecialToken(t.specialToken);
/*  6886 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6889 */       if (jjtc000)
/*  6890 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void shortKeyword()
/*       */     throws ParseException
/*       */   {
/*  6897 */     ASTNode jjtn000 = new ASTNode(143);
/*  6898 */     boolean jjtc000 = true;
/*  6899 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6901 */       Token t = jj_consume_token(117);
/*  6902 */       jjtree.closeNodeScope(jjtn000, true);
/*  6903 */       jjtc000 = false;
/*  6904 */       jjtn000.setSpecialToken(t.specialToken);
/*  6905 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6908 */       if (jjtc000)
/*  6909 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void signedKeyword()
/*       */     throws ParseException
/*       */   {
/*  6916 */     ASTNode jjtn000 = new ASTNode(144);
/*  6917 */     boolean jjtc000 = true;
/*  6918 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6920 */       Token t = jj_consume_token(118);
/*  6921 */       jjtree.closeNodeScope(jjtn000, true);
/*  6922 */       jjtc000 = false;
/*  6923 */       jjtn000.setSpecialToken(t.specialToken);
/*  6924 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6927 */       if (jjtc000)
/*  6928 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void sizeofKeyword()
/*       */     throws ParseException
/*       */   {
/*  6935 */     ASTNode jjtn000 = new ASTNode(145);
/*  6936 */     boolean jjtc000 = true;
/*  6937 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6939 */       Token t = jj_consume_token(119);
/*  6940 */       jjtree.closeNodeScope(jjtn000, true);
/*  6941 */       jjtc000 = false;
/*  6942 */       jjtn000.setSpecialToken(t.specialToken);
/*  6943 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6946 */       if (jjtc000)
/*  6947 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void staticKeyword()
/*       */     throws ParseException
/*       */   {
/*  6954 */     ASTNode jjtn000 = new ASTNode(146);
/*  6955 */     boolean jjtc000 = true;
/*  6956 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6958 */       Token t = jj_consume_token(120);
/*  6959 */       jjtree.closeNodeScope(jjtn000, true);
/*  6960 */       jjtc000 = false;
/*  6961 */       jjtn000.setSpecialToken(t.specialToken);
/*  6962 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  6965 */       if (jjtc000)
/*  6966 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token structKeyword()
/*       */     throws ParseException
/*       */   {
/*  6973 */     ASTNode jjtn000 = new ASTNode(147);
/*  6974 */     boolean jjtc000 = true;
/*  6975 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6977 */       Token t = jj_consume_token(122);
/*  6978 */       jjtree.closeNodeScope(jjtn000, true);
/*  6979 */       jjtc000 = false;
/*  6980 */       jjtn000.setSpecialToken(t.specialToken);
/*  6981 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  6983 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  6985 */       if (jjtc000)
/*  6986 */         jjtree.closeNodeScope(jjtn000, true); 
/*  6986 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void classKeyword()
/*       */     throws ParseException
/*       */   {
/*  6994 */     ASTNode jjtn000 = new ASTNode(148);
/*  6995 */     boolean jjtc000 = true;
/*  6996 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  6998 */       Token t = jj_consume_token(123);
/*  6999 */       jjtree.closeNodeScope(jjtn000, true);
/*  7000 */       jjtc000 = false;
/*  7001 */       jjtn000.setSpecialToken(t.specialToken);
/*  7002 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7005 */       if (jjtc000)
/*  7006 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token switchKeyword()
/*       */     throws ParseException
/*       */   {
/*  7013 */     ASTNode jjtn000 = new ASTNode(149);
/*  7014 */     boolean jjtc000 = true;
/*  7015 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7017 */       Token t = jj_consume_token(124);
/*  7018 */       jjtree.closeNodeScope(jjtn000, true);
/*  7019 */       jjtc000 = false;
/*  7020 */       jjtn000.setSpecialToken(t.specialToken);
/*  7021 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  7023 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  7025 */       if (jjtc000)
/*  7026 */         jjtree.closeNodeScope(jjtn000, true); 
/*  7026 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void templateKeyword()
/*       */     throws ParseException
/*       */   {
/*  7034 */     ASTNode jjtn000 = new ASTNode(150);
/*  7035 */     boolean jjtc000 = true;
/*  7036 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7038 */       Token t = jj_consume_token(125);
/*  7039 */       jjtree.closeNodeScope(jjtn000, true);
/*  7040 */       jjtc000 = false;
/*  7041 */       jjtn000.setSpecialToken(t.specialToken);
/*  7042 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7045 */       if (jjtc000)
/*  7046 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void thisKeyword()
/*       */     throws ParseException
/*       */   {
/*  7053 */     ASTNode jjtn000 = new ASTNode(151);
/*  7054 */     boolean jjtc000 = true;
/*  7055 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7057 */       Token t = jj_consume_token(126);
/*  7058 */       jjtree.closeNodeScope(jjtn000, true);
/*  7059 */       jjtc000 = false;
/*  7060 */       jjtn000.setSpecialToken(t.specialToken);
/*  7061 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7064 */       if (jjtc000)
/*  7065 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void tryKeyword()
/*       */     throws ParseException
/*       */   {
/*  7072 */     ASTNode jjtn000 = new ASTNode(152);
/*  7073 */     boolean jjtc000 = true;
/*  7074 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7076 */       Token t = jj_consume_token(127);
/*  7077 */       jjtree.closeNodeScope(jjtn000, true);
/*  7078 */       jjtc000 = false;
/*  7079 */       jjtn000.setSpecialToken(t.specialToken);
/*  7080 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7083 */       if (jjtc000)
/*  7084 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token typedefKeyword()
/*       */     throws ParseException
/*       */   {
/*  7091 */     ASTNode jjtn000 = new ASTNode(153);
/*  7092 */     boolean jjtc000 = true;
/*  7093 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7095 */       Token t = jj_consume_token(128);
/*  7096 */       jjtree.closeNodeScope(jjtn000, true);
/*  7097 */       jjtc000 = false;
/*  7098 */       jjtn000.setSpecialToken(t.specialToken);
/*  7099 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  7101 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  7103 */       if (jjtc000)
/*  7104 */         jjtree.closeNodeScope(jjtn000, true); 
/*  7104 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token unionKeyword()
/*       */     throws ParseException
/*       */   {
/*  7112 */     ASTNode jjtn000 = new ASTNode(154);
/*  7113 */     boolean jjtc000 = true;
/*  7114 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7116 */       Token t = jj_consume_token(129);
/*  7117 */       jjtree.closeNodeScope(jjtn000, true);
/*  7118 */       jjtc000 = false;
/*  7119 */       jjtn000.setSpecialToken(t.specialToken);
/*  7120 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  7122 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  7124 */       if (jjtc000)
/*  7125 */         jjtree.closeNodeScope(jjtn000, true); 
/*  7125 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void unsignedKeyword()
/*       */     throws ParseException
/*       */   {
/*  7133 */     ASTNode jjtn000 = new ASTNode(155);
/*  7134 */     boolean jjtc000 = true;
/*  7135 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7137 */       Token t = jj_consume_token(130);
/*  7138 */       jjtree.closeNodeScope(jjtn000, true);
/*  7139 */       jjtc000 = false;
/*  7140 */       jjtn000.setSpecialToken(t.specialToken);
/*  7141 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7144 */       if (jjtc000)
/*  7145 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token virtualKeyword()
/*       */     throws ParseException
/*       */   {
/*  7152 */     ASTNode jjtn000 = new ASTNode(156);
/*  7153 */     boolean jjtc000 = true;
/*  7154 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7156 */       Token t = jj_consume_token(131);
/*  7157 */       jjtree.closeNodeScope(jjtn000, true);
/*  7158 */       jjtc000 = false;
/*  7159 */       jjtn000.setSpecialToken(t.specialToken);
/*  7160 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  7162 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  7164 */       if (jjtc000)
/*  7165 */         jjtree.closeNodeScope(jjtn000, true); 
/*  7165 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void voidKeyword()
/*       */     throws ParseException
/*       */   {
/*  7173 */     ASTNode jjtn000 = new ASTNode(157);
/*  7174 */     boolean jjtc000 = true;
/*  7175 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7177 */       Token t = jj_consume_token(132);
/*  7178 */       jjtree.closeNodeScope(jjtn000, true);
/*  7179 */       jjtc000 = false;
/*  7180 */       jjtn000.setSpecialToken(t.specialToken);
/*  7181 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7184 */       if (jjtc000)
/*  7185 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void volatileKeyword()
/*       */     throws ParseException
/*       */   {
/*  7192 */     ASTNode jjtn000 = new ASTNode(158);
/*  7193 */     boolean jjtc000 = true;
/*  7194 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7196 */       Token t = jj_consume_token(133);
/*  7197 */       jjtree.closeNodeScope(jjtn000, true);
/*  7198 */       jjtc000 = false;
/*  7199 */       jjtn000.setSpecialToken(t.specialToken);
/*  7200 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7203 */       if (jjtc000)
/*  7204 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void whileKeyword()
/*       */     throws ParseException
/*       */   {
/*  7211 */     ASTNode jjtn000 = new ASTNode(159);
/*  7212 */     boolean jjtc000 = true;
/*  7213 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7215 */       Token t = jj_consume_token(134);
/*  7216 */       jjtree.closeNodeScope(jjtn000, true);
/*  7217 */       jjtc000 = false;
/*  7218 */       jjtn000.setSpecialToken(t.specialToken);
/*  7219 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7222 */       if (jjtc000)
/*  7223 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token operatorKeyword()
/*       */     throws ParseException
/*       */   {
/*  7230 */     ASTNode jjtn000 = new ASTNode(160);
/*  7231 */     boolean jjtc000 = true;
/*  7232 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7234 */       Token t = jj_consume_token(135);
/*  7235 */       jjtree.closeNodeScope(jjtn000, true);
/*  7236 */       jjtc000 = false;
/*  7237 */       jjtn000.setSpecialToken(t.specialToken);
/*  7238 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  7240 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  7242 */       if (jjtc000)
/*  7243 */         jjtree.closeNodeScope(jjtn000, true); 
/*  7243 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void trueKeyword()
/*       */     throws ParseException
/*       */   {
/*  7251 */     ASTNode jjtn000 = new ASTNode(161);
/*  7252 */     boolean jjtc000 = true;
/*  7253 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7255 */       Token t = jj_consume_token(136);
/*  7256 */       jjtree.closeNodeScope(jjtn000, true);
/*  7257 */       jjtc000 = false;
/*  7258 */       jjtn000.setSpecialToken(t.specialToken);
/*  7259 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7262 */       if (jjtc000)
/*  7263 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void falseKeyword()
/*       */     throws ParseException
/*       */   {
/*  7270 */     ASTNode jjtn000 = new ASTNode(162);
/*  7271 */     boolean jjtc000 = true;
/*  7272 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7274 */       Token t = jj_consume_token(137);
/*  7275 */       jjtree.closeNodeScope(jjtn000, true);
/*  7276 */       jjtc000 = false;
/*  7277 */       jjtn000.setSpecialToken(t.specialToken);
/*  7278 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7281 */       if (jjtc000)
/*  7282 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void throwKeyword()
/*       */     throws ParseException
/*       */   {
/*  7289 */     ASTNode jjtn000 = new ASTNode(163);
/*  7290 */     boolean jjtc000 = true;
/*  7291 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7293 */       Token t = jj_consume_token(138);
/*  7294 */       jjtree.closeNodeScope(jjtn000, true);
/*  7295 */       jjtc000 = false;
/*  7296 */       jjtn000.setSpecialToken(t.specialToken);
/*  7297 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7300 */       if (jjtc000)
/*  7301 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void staticCastKeyword()
/*       */     throws ParseException
/*       */   {
/*  7308 */     ASTNode jjtn000 = new ASTNode(164);
/*  7309 */     boolean jjtc000 = true;
/*  7310 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7312 */       Token t = jj_consume_token(141);
/*  7313 */       jjtree.closeNodeScope(jjtn000, true);
/*  7314 */       jjtc000 = false;
/*  7315 */       jjtn000.setSpecialToken(t.specialToken);
/*  7316 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7319 */       if (jjtc000)
/*  7320 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void dynamicCastKeyword()
/*       */     throws ParseException
/*       */   {
/*  7327 */     ASTNode jjtn000 = new ASTNode(165);
/*  7328 */     boolean jjtc000 = true;
/*  7329 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7331 */       Token t = jj_consume_token(142);
/*  7332 */       jjtree.closeNodeScope(jjtn000, true);
/*  7333 */       jjtc000 = false;
/*  7334 */       jjtn000.setSpecialToken(t.specialToken);
/*  7335 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7338 */       if (jjtc000)
/*  7339 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void constCastKeyword()
/*       */     throws ParseException
/*       */   {
/*  7346 */     ASTNode jjtn000 = new ASTNode(166);
/*  7347 */     boolean jjtc000 = true;
/*  7348 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7350 */       Token t = jj_consume_token(143);
/*  7351 */       jjtree.closeNodeScope(jjtn000, true);
/*  7352 */       jjtc000 = false;
/*  7353 */       jjtn000.setSpecialToken(t.specialToken);
/*  7354 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7357 */       if (jjtc000)
/*  7358 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void reinterpretCastKeyword()
/*       */     throws ParseException
/*       */   {
/*  7365 */     ASTNode jjtn000 = new ASTNode(167);
/*  7366 */     boolean jjtc000 = true;
/*  7367 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7369 */       Token t = jj_consume_token(144);
/*  7370 */       jjtree.closeNodeScope(jjtn000, true);
/*  7371 */       jjtc000 = false;
/*  7372 */       jjtn000.setSpecialToken(t.specialToken);
/*  7373 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7376 */       if (jjtc000)
/*  7377 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token id()
/*       */     throws ParseException
/*       */   {
/*  7384 */     ASTNode jjtn000 = new ASTNode(168);
/*  7385 */     boolean jjtc000 = true;
/*  7386 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7388 */       Token t = jj_consume_token(164);
/*  7389 */       jjtree.closeNodeScope(jjtn000, true);
/*  7390 */       jjtc000 = false;
/*  7391 */       jjtn000.setSpecialToken(t.specialToken);
/*  7392 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  7394 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  7396 */       if (jjtc000)
/*  7397 */         jjtree.closeNodeScope(jjtn000, true); 
/*  7397 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token characterConstant()
/*       */     throws ParseException
/*       */   {
/*  7405 */     ASTNode jjtn000 = new ASTNode(169);
/*  7406 */     boolean jjtc000 = true;
/*  7407 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7409 */       Token t = jj_consume_token(162);
/*  7410 */       jjtree.closeNodeScope(jjtn000, true);
/*  7411 */       jjtc000 = false;
/*  7412 */       jjtn000.setSpecialToken(t.specialToken);
/*  7413 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  7415 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  7417 */       if (jjtc000)
/*  7418 */         jjtree.closeNodeScope(jjtn000, true); 
/*  7418 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token stringConstant()
/*       */     throws ParseException
/*       */   {
/*  7426 */     ASTNode jjtn000 = new ASTNode(170);
/*  7427 */     boolean jjtc000 = true;
/*  7428 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7430 */       Token t = jj_consume_token(163);
/*  7431 */       jjtree.closeNodeScope(jjtn000, true);
/*  7432 */       jjtc000 = false;
/*  7433 */       jjtn000.setSpecialToken(t.specialToken);
/*  7434 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  7436 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  7438 */       if (jjtc000)
/*  7439 */         jjtree.closeNodeScope(jjtn000, true); 
/*  7439 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void octalInt()
/*       */     throws ParseException
/*       */   {
/*  7447 */     ASTNode jjtn000 = new ASTNode(171);
/*  7448 */     boolean jjtc000 = true;
/*  7449 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7451 */       Token t = jj_consume_token(145);
/*  7452 */       jjtree.closeNodeScope(jjtn000, true);
/*  7453 */       jjtc000 = false;
/*  7454 */       jjtn000.setSpecialToken(t.specialToken);
/*  7455 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7458 */       if (jjtc000)
/*  7459 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void octalLong()
/*       */     throws ParseException
/*       */   {
/*  7466 */     ASTNode jjtn000 = new ASTNode(172);
/*  7467 */     boolean jjtc000 = true;
/*  7468 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7470 */       Token t = jj_consume_token(146);
/*  7471 */       jjtree.closeNodeScope(jjtn000, true);
/*  7472 */       jjtc000 = false;
/*  7473 */       jjtn000.setSpecialToken(t.specialToken);
/*  7474 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7477 */       if (jjtc000)
/*  7478 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void unsignedOctalInt()
/*       */     throws ParseException
/*       */   {
/*  7485 */     ASTNode jjtn000 = new ASTNode(173);
/*  7486 */     boolean jjtc000 = true;
/*  7487 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7489 */       Token t = jj_consume_token(147);
/*  7490 */       jjtree.closeNodeScope(jjtn000, true);
/*  7491 */       jjtc000 = false;
/*  7492 */       jjtn000.setSpecialToken(t.specialToken);
/*  7493 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7496 */       if (jjtc000)
/*  7497 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void unsignedOctalLong()
/*       */     throws ParseException
/*       */   {
/*  7504 */     ASTNode jjtn000 = new ASTNode(174);
/*  7505 */     boolean jjtc000 = true;
/*  7506 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7508 */       Token t = jj_consume_token(148);
/*  7509 */       jjtree.closeNodeScope(jjtn000, true);
/*  7510 */       jjtc000 = false;
/*  7511 */       jjtn000.setSpecialToken(t.specialToken);
/*  7512 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7515 */       if (jjtc000)
/*  7516 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void zeroDecimalInt()
/*       */     throws ParseException
/*       */   {
/*  7523 */     ASTNode jjtn000 = new ASTNode(175);
/*  7524 */     boolean jjtc000 = true;
/*  7525 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7527 */       Token t = jj_consume_token(149);
/*  7528 */       jjtree.closeNodeScope(jjtn000, true);
/*  7529 */       jjtc000 = false;
/*  7530 */       jjtn000.setSpecialToken(t.specialToken);
/*  7531 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7534 */       if (jjtc000)
/*  7535 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void decimalInt()
/*       */     throws ParseException
/*       */   {
/*  7542 */     ASTNode jjtn000 = new ASTNode(176);
/*  7543 */     boolean jjtc000 = true;
/*  7544 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7546 */       Token t = jj_consume_token(150);
/*  7547 */       jjtree.closeNodeScope(jjtn000, true);
/*  7548 */       jjtc000 = false;
/*  7549 */       jjtn000.setSpecialToken(t.specialToken);
/*  7550 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7553 */       if (jjtc000)
/*  7554 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void decimalLong()
/*       */     throws ParseException
/*       */   {
/*  7561 */     ASTNode jjtn000 = new ASTNode(177);
/*  7562 */     boolean jjtc000 = true;
/*  7563 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7565 */       Token t = jj_consume_token(151);
/*  7566 */       jjtree.closeNodeScope(jjtn000, true);
/*  7567 */       jjtc000 = false;
/*  7568 */       jjtn000.setSpecialToken(t.specialToken);
/*  7569 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7572 */       if (jjtc000)
/*  7573 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void unsignedDecimalInt()
/*       */     throws ParseException
/*       */   {
/*  7580 */     ASTNode jjtn000 = new ASTNode(178);
/*  7581 */     boolean jjtc000 = true;
/*  7582 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7584 */       Token t = jj_consume_token(152);
/*  7585 */       jjtree.closeNodeScope(jjtn000, true);
/*  7586 */       jjtc000 = false;
/*  7587 */       jjtn000.setSpecialToken(t.specialToken);
/*  7588 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7591 */       if (jjtc000)
/*  7592 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void unsignedDecimalLong()
/*       */     throws ParseException
/*       */   {
/*  7599 */     ASTNode jjtn000 = new ASTNode(179);
/*  7600 */     boolean jjtc000 = true;
/*  7601 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7603 */       Token t = jj_consume_token(153);
/*  7604 */       jjtree.closeNodeScope(jjtn000, true);
/*  7605 */       jjtc000 = false;
/*  7606 */       jjtn000.setSpecialToken(t.specialToken);
/*  7607 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7610 */       if (jjtc000)
/*  7611 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void hexadecimalInt()
/*       */     throws ParseException
/*       */   {
/*  7618 */     ASTNode jjtn000 = new ASTNode(180);
/*  7619 */     boolean jjtc000 = true;
/*  7620 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7622 */       Token t = jj_consume_token(154);
/*  7623 */       jjtree.closeNodeScope(jjtn000, true);
/*  7624 */       jjtc000 = false;
/*  7625 */       jjtn000.setSpecialToken(t.specialToken);
/*  7626 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7629 */       if (jjtc000)
/*  7630 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void hexadecimalLong()
/*       */     throws ParseException
/*       */   {
/*  7637 */     ASTNode jjtn000 = new ASTNode(181);
/*  7638 */     boolean jjtc000 = true;
/*  7639 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7641 */       Token t = jj_consume_token(155);
/*  7642 */       jjtree.closeNodeScope(jjtn000, true);
/*  7643 */       jjtc000 = false;
/*  7644 */       jjtn000.setSpecialToken(t.specialToken);
/*  7645 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7648 */       if (jjtc000)
/*  7649 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void unsignedHexadecimalInt()
/*       */     throws ParseException
/*       */   {
/*  7656 */     ASTNode jjtn000 = new ASTNode(182);
/*  7657 */     boolean jjtc000 = true;
/*  7658 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7660 */       Token t = jj_consume_token(156);
/*  7661 */       jjtree.closeNodeScope(jjtn000, true);
/*  7662 */       jjtc000 = false;
/*  7663 */       jjtn000.setSpecialToken(t.specialToken);
/*  7664 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7667 */       if (jjtc000)
/*  7668 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void unsignedHexadecimalLong()
/*       */     throws ParseException
/*       */   {
/*  7675 */     ASTNode jjtn000 = new ASTNode(183);
/*  7676 */     boolean jjtc000 = true;
/*  7677 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7679 */       Token t = jj_consume_token(157);
/*  7680 */       jjtree.closeNodeScope(jjtn000, true);
/*  7681 */       jjtc000 = false;
/*  7682 */       jjtn000.setSpecialToken(t.specialToken);
/*  7683 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7686 */       if (jjtc000)
/*  7687 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void floatOne()
/*       */     throws ParseException
/*       */   {
/*  7694 */     ASTNode jjtn000 = new ASTNode(184);
/*  7695 */     boolean jjtc000 = true;
/*  7696 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7698 */       Token t = jj_consume_token(158);
/*  7699 */       jjtree.closeNodeScope(jjtn000, true);
/*  7700 */       jjtc000 = false;
/*  7701 */       jjtn000.setSpecialToken(t.specialToken);
/*  7702 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7705 */       if (jjtc000)
/*  7706 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void floatTwo()
/*       */     throws ParseException
/*       */   {
/*  7713 */     ASTNode jjtn000 = new ASTNode(185);
/*  7714 */     boolean jjtc000 = true;
/*  7715 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7717 */       Token t = jj_consume_token(159);
/*  7718 */       jjtree.closeNodeScope(jjtn000, true);
/*  7719 */       jjtc000 = false;
/*  7720 */       jjtn000.setSpecialToken(t.specialToken);
/*  7721 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7724 */       if (jjtc000)
/*  7725 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void stringKeyword()
/*       */     throws ParseException
/*       */   {
/*  7732 */     ASTNode jjtn000 = new ASTNode(185);
/*  7733 */     boolean jjtc000 = true;
/*  7734 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7736 */       Token t = jj_consume_token(121);
/*  7737 */       jjtree.closeNodeScope(jjtn000, true);
/*  7738 */       jjtc000 = false;
/*  7739 */       jjtn000.setSpecialToken(t.specialToken);
/*  7740 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7743 */       if (jjtc000)
/*  7744 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void colorKeyword()
/*       */     throws ParseException
/*       */   {
/*  7751 */     ASTNode jjtn000 = new ASTNode(185);
/*  7752 */     boolean jjtc000 = true;
/*  7753 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7755 */       Token t = jj_consume_token(139);
/*  7756 */       jjtree.closeNodeScope(jjtn000, true);
/*  7757 */       jjtc000 = false;
/*  7758 */       jjtn000.setSpecialToken(t.specialToken);
/*  7759 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7762 */       if (jjtc000)
/*  7763 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void datetimeKeyword()
/*       */     throws ParseException
/*       */   {
/*  7770 */     ASTNode jjtn000 = new ASTNode(185);
/*  7771 */     boolean jjtc000 = true;
/*  7772 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7774 */       Token t = jj_consume_token(140);
/*  7775 */       jjtree.closeNodeScope(jjtn000, true);
/*  7776 */       jjtc000 = false;
/*  7777 */       jjtn000.setSpecialToken(t.specialToken);
/*  7778 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */     }
/*       */     finally {
/*  7781 */       if (jjtc000)
/*  7782 */         jjtree.closeNodeScope(jjtn000, true);
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final Token datetimeInitializer()
/*       */     throws ParseException
/*       */   {
/*  7789 */     ASTNode jjtn000 = new ASTNode(186);
/*  7790 */     boolean jjtc000 = true;
/*  7791 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7793 */       Token t = jj_consume_token(160);
/*  7794 */       jjtree.closeNodeScope(jjtn000, true);
/*  7795 */       jjtc000 = false;
/*  7796 */       jjtn000.setSpecialToken(t.specialToken);
/*  7797 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  7799 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  7801 */       if (jjtc000)
/*  7802 */         jjtree.closeNodeScope(jjtn000, true); 
/*  7802 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final Token colorInitializer()
/*       */     throws ParseException
/*       */   {
/*  7810 */     ASTNode jjtn000 = new ASTNode(187);
/*  7811 */     boolean jjtc000 = true;
/*  7812 */     jjtree.openNodeScope(jjtn000);
/*       */     try {
/*  7814 */       Token t = jj_consume_token(161);
/*  7815 */       jjtree.closeNodeScope(jjtn000, true);
/*  7816 */       jjtc000 = false;
/*  7817 */       jjtn000.setSpecialToken(t.specialToken);
/*  7818 */       jjtn000.setParams(t.image, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
/*       */ 
/*  7820 */       Token localToken1 = t;
/*       */       return localToken1;
/*       */     }
/*       */     finally
/*       */     {
/*  7822 */       if (jjtc000)
/*  7823 */         jjtree.closeNodeScope(jjtn000, true); 
/*  7823 */     }throw localObject;
/*       */   }
/*       */ 
/*       */   public static final void boolean_literal()
/*       */     throws ParseException
/*       */   {
/*  7830 */     switch (jj_ntk == -1 ? jj_ntk() : jj_ntk) {
/*       */     case 136:
/*  7832 */       jj_consume_token(136);
/*  7833 */       break;
/*       */     case 137:
/*  7835 */       jj_consume_token(137);
/*  7836 */       break;
/*       */     default:
/*  7838 */       jj_la1[''] = jj_gen;
/*  7839 */       jj_consume_token(-1);
/*  7840 */       throw new ParseException();
/*       */     }
/*       */   }
/*       */ 
/*       */   public static final void null_literal() throws ParseException {
/*  7845 */     jj_consume_token(164);
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_1(int xla) {
/*  7849 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_1() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7851 */       int j = 1;
/*       */       return j; } finally { jj_save(0, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_2(int xla) {
/*  7856 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_2() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7858 */       int j = 1;
/*       */       return j; } finally { jj_save(1, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_3(int xla) {
/*  7863 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_3() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7865 */       int j = 1;
/*       */       return j; } finally { jj_save(2, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_4(int xla) {
/*  7870 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_4() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7872 */       int j = 1;
/*       */       return j; } finally { jj_save(3, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_5(int xla) {
/*  7877 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_5() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7879 */       int j = 1;
/*       */       return j; } finally { jj_save(4, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_6(int xla) {
/*  7884 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_6() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7886 */       int j = 1;
/*       */       return j; } finally { jj_save(5, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_7(int xla) {
/*  7891 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_7() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7893 */       int j = 1;
/*       */       return j; } finally { jj_save(6, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_8(int xla) {
/*  7898 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_8() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7900 */       int j = 1;
/*       */       return j; } finally { jj_save(7, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_9(int xla) {
/*  7905 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_9() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7907 */       int j = 1;
/*       */       return j; } finally { jj_save(8, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_10(int xla) {
/*  7912 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_10() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7914 */       int j = 1;
/*       */       return j; } finally { jj_save(9, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_11(int xla) {
/*  7919 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_11() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7921 */       int j = 1;
/*       */       return j; } finally { jj_save(10, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_12(int xla) {
/*  7926 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_12() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7928 */       int j = 1;
/*       */       return j; } finally { jj_save(11, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_13(int xla) {
/*  7933 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_13() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7935 */       int j = 1;
/*       */       return j; } finally { jj_save(12, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_14(int xla) {
/*  7940 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_14() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7942 */       int j = 1;
/*       */       return j; } finally { jj_save(13, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_15(int xla) {
/*  7947 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_15() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7949 */       int j = 1;
/*       */       return j; } finally { jj_save(14, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_16(int xla) {
/*  7954 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_16() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7956 */       int j = 1;
/*       */       return j; } finally { jj_save(15, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_17(int xla) {
/*  7961 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_17() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7963 */       int j = 1;
/*       */       return j; } finally { jj_save(16, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_18(int xla) {
/*  7968 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_18() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7970 */       int j = 1;
/*       */       return j; } finally { jj_save(17, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_19(int xla) {
/*  7975 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_19() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7977 */       int j = 1;
/*       */       return j; } finally { jj_save(18, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_20(int xla) {
/*  7982 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_20() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7984 */       int j = 1;
/*       */       return j; } finally { jj_save(19, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_21(int xla) {
/*  7989 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_21() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7991 */       int j = 1;
/*       */       return j; } finally { jj_save(20, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_22(int xla) {
/*  7996 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_22() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  7998 */       int j = 1;
/*       */       return j; } finally { jj_save(21, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_23(int xla) {
/*  8003 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_23() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8005 */       int j = 1;
/*       */       return j; } finally { jj_save(22, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_24(int xla) {
/*  8010 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_24() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8012 */       int j = 1;
/*       */       return j; } finally { jj_save(23, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_25(int xla) {
/*  8017 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_25() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8019 */       int j = 1;
/*       */       return j; } finally { jj_save(24, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_26(int xla) {
/*  8024 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_26() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8026 */       int j = 1;
/*       */       return j; } finally { jj_save(25, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_27(int xla) {
/*  8031 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_27() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8033 */       int j = 1;
/*       */       return j; } finally { jj_save(26, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_28(int xla) {
/*  8038 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_28() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8040 */       int j = 1;
/*       */       return j; } finally { jj_save(27, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_29(int xla) {
/*  8045 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_29() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8047 */       int j = 1;
/*       */       return j; } finally { jj_save(28, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_30(int xla) {
/*  8052 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_30() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8054 */       int j = 1;
/*       */       return j; } finally { jj_save(29, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_31(int xla) {
/*  8059 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_31() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8061 */       int j = 1;
/*       */       return j; } finally { jj_save(30, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_32(int xla) {
/*  8066 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_32() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8068 */       int j = 1;
/*       */       return j; } finally { jj_save(31, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_33(int xla) {
/*  8073 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_33() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8075 */       int j = 1;
/*       */       return j; } finally { jj_save(32, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_34(int xla) {
/*  8080 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_34() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8082 */       int j = 1;
/*       */       return j; } finally { jj_save(33, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_35(int xla) {
/*  8087 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_35() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8089 */       int j = 1;
/*       */       return j; } finally { jj_save(34, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_36(int xla) {
/*  8094 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_36() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8096 */       int j = 1;
/*       */       return j; } finally { jj_save(35, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_37(int xla) {
/*  8101 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_37() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8103 */       int j = 1;
/*       */       return j; } finally { jj_save(36, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_38(int xla) {
/*  8108 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_38() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8110 */       int j = 1;
/*       */       return j; } finally { jj_save(37, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_39(int xla) {
/*  8115 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_39() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8117 */       int j = 1;
/*       */       return j; } finally { jj_save(38, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_40(int xla) {
/*  8122 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_40() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8124 */       int j = 1;
/*       */       return j; } finally { jj_save(39, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_41(int xla) {
/*  8129 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_41() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8131 */       int j = 1;
/*       */       return j; } finally { jj_save(40, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_42(int xla) {
/*  8136 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_42() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8138 */       int j = 1;
/*       */       return j; } finally { jj_save(41, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_43(int xla) {
/*  8143 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_43() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8145 */       int j = 1;
/*       */       return j; } finally { jj_save(42, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_44(int xla) {
/*  8150 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_44() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8152 */       int j = 1;
/*       */       return j; } finally { jj_save(43, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_45(int xla) {
/*  8157 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_45() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8159 */       int j = 1;
/*       */       return j; } finally { jj_save(44, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_46(int xla) {
/*  8164 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_46() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8166 */       int j = 1;
/*       */       return j; } finally { jj_save(45, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_47(int xla) {
/*  8171 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_47() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8173 */       int j = 1;
/*       */       return j; } finally { jj_save(46, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_48(int xla) {
/*  8178 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_48() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8180 */       int j = 1;
/*       */       return j; } finally { jj_save(47, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_49(int xla) {
/*  8185 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_49() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8187 */       int j = 1;
/*       */       return j; } finally { jj_save(48, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_50(int xla) {
/*  8192 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_50() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8194 */       int j = 1;
/*       */       return j; } finally { jj_save(49, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_51(int xla) {
/*  8199 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_51() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8201 */       int j = 1;
/*       */       return j; } finally { jj_save(50, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_52(int xla) {
/*  8206 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_52() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8208 */       int j = 1;
/*       */       return j; } finally { jj_save(51, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_53(int xla) {
/*  8213 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_53() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8215 */       int j = 1;
/*       */       return j; } finally { jj_save(52, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_54(int xla) {
/*  8220 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_54() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8222 */       int j = 1;
/*       */       return j; } finally { jj_save(53, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_55(int xla) {
/*  8227 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_55() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8229 */       int j = 1;
/*       */       return j; } finally { jj_save(54, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_56(int xla) {
/*  8234 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_56() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8236 */       int j = 1;
/*       */       return j; } finally { jj_save(55, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_57(int xla) {
/*  8241 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_57() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8243 */       int j = 1;
/*       */       return j; } finally { jj_save(56, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_58(int xla) {
/*  8248 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_58() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8250 */       int j = 1;
/*       */       return j; } finally { jj_save(57, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_59(int xla) {
/*  8255 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_59() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8257 */       int j = 1;
/*       */       return j; } finally { jj_save(58, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_60(int xla) {
/*  8262 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_60() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8264 */       int j = 1;
/*       */       return j; } finally { jj_save(59, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_61(int xla) {
/*  8269 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_61() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8271 */       int j = 1;
/*       */       return j; } finally { jj_save(60, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_62(int xla) {
/*  8276 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_62() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8278 */       int j = 1;
/*       */       return j; } finally { jj_save(61, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_63(int xla) {
/*  8283 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_63() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8285 */       int j = 1;
/*       */       return j; } finally { jj_save(62, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_64(int xla) {
/*  8290 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_64() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8292 */       int j = 1;
/*       */       return j; } finally { jj_save(63, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_65(int xla) {
/*  8297 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_65() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8299 */       int j = 1;
/*       */       return j; } finally { jj_save(64, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_66(int xla) {
/*  8304 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_66() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8306 */       int j = 1;
/*       */       return j; } finally { jj_save(65, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_67(int xla) {
/*  8311 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_67() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8313 */       int j = 1;
/*       */       return j; } finally { jj_save(66, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_68(int xla) {
/*  8318 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_68() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8320 */       int j = 1;
/*       */       return j; } finally { jj_save(67, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_69(int xla) {
/*  8325 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_69() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8327 */       int j = 1;
/*       */       return j; } finally { jj_save(68, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_70(int xla) {
/*  8332 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_70() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8334 */       int j = 1;
/*       */       return j; } finally { jj_save(69, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_71(int xla) {
/*  8339 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_71() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8341 */       int j = 1;
/*       */       return j; } finally { jj_save(70, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_72(int xla) {
/*  8346 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_72() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8348 */       int j = 1;
/*       */       return j; } finally { jj_save(71, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_73(int xla) {
/*  8353 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_73() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8355 */       int j = 1;
/*       */       return j; } finally { jj_save(72, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_74(int xla) {
/*  8360 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_74() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8362 */       int j = 1;
/*       */       return j; } finally { jj_save(73, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_75(int xla) {
/*  8367 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_75() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8369 */       int j = 1;
/*       */       return j; } finally { jj_save(74, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_76(int xla) {
/*  8374 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_76() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8376 */       int j = 1;
/*       */       return j; } finally { jj_save(75, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_77(int xla) {
/*  8381 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_77() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8383 */       int j = 1;
/*       */       return j; } finally { jj_save(76, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_78(int xla) {
/*  8388 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_78() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8390 */       int j = 1;
/*       */       return j; } finally { jj_save(77, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_79(int xla) {
/*  8395 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_79() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8397 */       int j = 1;
/*       */       return j; } finally { jj_save(78, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_80(int xla) {
/*  8402 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_80() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8404 */       int j = 1;
/*       */       return j; } finally { jj_save(79, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_81(int xla) {
/*  8409 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_81() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8411 */       int j = 1;
/*       */       return j; } finally { jj_save(80, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_82(int xla) {
/*  8416 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_82() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8418 */       int j = 1;
/*       */       return j; } finally { jj_save(81, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_83(int xla) {
/*  8423 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_83() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8425 */       int j = 1;
/*       */       return j; } finally { jj_save(82, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_84(int xla) {
/*  8430 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_84() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8432 */       int j = 1;
/*       */       return j; } finally { jj_save(83, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_85(int xla) {
/*  8437 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_85() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8439 */       int j = 1;
/*       */       return j; } finally { jj_save(84, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_86(int xla) {
/*  8444 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_86() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8446 */       int j = 1;
/*       */       return j; } finally { jj_save(85, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_87(int xla) {
/*  8451 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_87() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8453 */       int j = 1;
/*       */       return j; } finally { jj_save(86, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_88(int xla) {
/*  8458 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_88() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8460 */       int j = 1;
/*       */       return j; } finally { jj_save(87, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_89(int xla) {
/*  8465 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_89() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8467 */       int j = 1;
/*       */       return j; } finally { jj_save(88, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_90(int xla) {
/*  8472 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_90() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8474 */       int j = 1;
/*       */       return j; } finally { jj_save(89, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_91(int xla) {
/*  8479 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_91() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8481 */       int j = 1;
/*       */       return j; } finally { jj_save(90, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_92(int xla) {
/*  8486 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_92() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8488 */       int j = 1;
/*       */       return j; } finally { jj_save(91, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_93(int xla) {
/*  8493 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_93() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8495 */       int j = 1;
/*       */       return j; } finally { jj_save(92, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_94(int xla) {
/*  8500 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_94() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8502 */       int j = 1;
/*       */       return j; } finally { jj_save(93, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_95(int xla) {
/*  8507 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_95() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8509 */       int j = 1;
/*       */       return j; } finally { jj_save(94, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_96(int xla) {
/*  8514 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_96() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8516 */       int j = 1;
/*       */       return j; } finally { jj_save(95, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_97(int xla) {
/*  8521 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_97() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8523 */       int j = 1;
/*       */       return j; } finally { jj_save(96, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_98(int xla) {
/*  8528 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_98() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8530 */       int j = 1;
/*       */       return j; } finally { jj_save(97, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_99(int xla) {
/*  8535 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_99() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8537 */       int j = 1;
/*       */       return j; } finally { jj_save(98, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_100(int xla) {
/*  8542 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_100() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8544 */       int j = 1;
/*       */       return j; } finally { jj_save(99, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_101(int xla) {
/*  8549 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_101() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8551 */       int j = 1;
/*       */       return j; } finally { jj_save(100, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_102(int xla) {
/*  8556 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_102() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8558 */       int j = 1;
/*       */       return j; } finally { jj_save(101, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_103(int xla) {
/*  8563 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_103() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8565 */       int j = 1;
/*       */       return j; } finally { jj_save(102, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_104(int xla) {
/*  8570 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_104() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8572 */       int j = 1;
/*       */       return j; } finally { jj_save(103, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_105(int xla) {
/*  8577 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_105() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8579 */       int j = 1;
/*       */       return j; } finally { jj_save(104, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_106(int xla) {
/*  8584 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_106() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8586 */       int j = 1;
/*       */       return j; } finally { jj_save(105, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_107(int xla) {
/*  8591 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_107() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8593 */       int j = 1;
/*       */       return j; } finally { jj_save(106, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_108(int xla) {
/*  8598 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_108() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8600 */       int j = 1;
/*       */       return j; } finally { jj_save(107, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_109(int xla) {
/*  8605 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_109() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8607 */       int j = 1;
/*       */       return j; } finally { jj_save(108, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_110(int xla) {
/*  8612 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_110() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8614 */       int j = 1;
/*       */       return j; } finally { jj_save(109, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_111(int xla) {
/*  8619 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_111() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8621 */       int j = 1;
/*       */       return j; } finally { jj_save(110, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_112(int xla) {
/*  8626 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_112() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8628 */       int j = 1;
/*       */       return j; } finally { jj_save(111, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_113(int xla) {
/*  8633 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_113() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8635 */       int j = 1;
/*       */       return j; } finally { jj_save(112, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_114(int xla) {
/*  8640 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_114() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8642 */       int j = 1;
/*       */       return j; } finally { jj_save(113, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_115(int xla) {
/*  8647 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_115() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8649 */       int j = 1;
/*       */       return j; } finally { jj_save(114, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_116(int xla) {
/*  8654 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_116() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8656 */       int j = 1;
/*       */       return j; } finally { jj_save(115, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_117(int xla) {
/*  8661 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_117() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8663 */       int j = 1;
/*       */       return j; } finally { jj_save(116, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_118(int xla) {
/*  8668 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_118() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8670 */       int j = 1;
/*       */       return j; } finally { jj_save(117, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_119(int xla) {
/*  8675 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_119() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8677 */       int j = 1;
/*       */       return j; } finally { jj_save(118, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_120(int xla) {
/*  8682 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_120() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8684 */       int j = 1;
/*       */       return j; } finally { jj_save(119, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_121(int xla) {
/*  8689 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_121() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8691 */       int j = 1;
/*       */       return j; } finally { jj_save(120, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_122(int xla) {
/*  8696 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_122() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8698 */       int j = 1;
/*       */       return j; } finally { jj_save(121, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_123(int xla) {
/*  8703 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_123() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8705 */       int j = 1;
/*       */       return j; } finally { jj_save(122, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_124(int xla) {
/*  8710 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_124() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8712 */       int j = 1;
/*       */       return j; } finally { jj_save(123, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_125(int xla) {
/*  8717 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_125() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8719 */       int j = 1;
/*       */       return j; } finally { jj_save(124, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_126(int xla) {
/*  8724 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_126() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8726 */       int j = 1;
/*       */       return j; } finally { jj_save(125, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_127(int xla) {
/*  8731 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_127() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8733 */       int j = 1;
/*       */       return j; } finally { jj_save(126, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_128(int xla) {
/*  8738 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_128() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8740 */       int j = 1;
/*       */       return j; } finally { jj_save(127, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_129(int xla) {
/*  8745 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_129() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8747 */       int j = 1;
/*       */       return j; } finally { jj_save(128, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_130(int xla) {
/*  8752 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_130() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8754 */       int j = 1;
/*       */       return j; } finally { jj_save(129, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_131(int xla) {
/*  8759 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_131() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8761 */       int j = 1;
/*       */       return j; } finally { jj_save(130, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_132(int xla) {
/*  8766 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_132() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8768 */       int j = 1;
/*       */       return j; } finally { jj_save(131, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_133(int xla) {
/*  8773 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_133() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8775 */       int j = 1;
/*       */       return j; } finally { jj_save(132, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_134(int xla) {
/*  8780 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_134() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8782 */       int j = 1;
/*       */       return j; } finally { jj_save(133, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_135(int xla) {
/*  8787 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_135() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8789 */       int j = 1;
/*       */       return j; } finally { jj_save(134, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_136(int xla) {
/*  8794 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_136() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8796 */       int j = 1;
/*       */       return j; } finally { jj_save(135, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_137(int xla) {
/*  8801 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_137() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8803 */       int j = 1;
/*       */       return j; } finally { jj_save(136, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_138(int xla) {
/*  8808 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_138() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8810 */       int j = 1;
/*       */       return j; } finally { jj_save(137, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_139(int xla) {
/*  8815 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_139() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8817 */       int j = 1;
/*       */       return j; } finally { jj_save(138, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_140(int xla) {
/*  8822 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_140() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8824 */       int j = 1;
/*       */       return j; } finally { jj_save(139, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_141(int xla) {
/*  8829 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_141() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8831 */       int j = 1;
/*       */       return j; } finally { jj_save(140, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_142(int xla) {
/*  8836 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_142() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8838 */       int j = 1;
/*       */       return j; } finally { jj_save(141, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_143(int xla) {
/*  8843 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_143() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8845 */       int j = 1;
/*       */       return j; } finally { jj_save(142, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_144(int xla) {
/*  8850 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_144() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8852 */       int j = 1;
/*       */       return j; } finally { jj_save(143, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_145(int xla) {
/*  8857 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_145() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8859 */       int j = 1;
/*       */       return j; } finally { jj_save(144, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_146(int xla) {
/*  8864 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_146() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8866 */       int j = 1;
/*       */       return j; } finally { jj_save(145, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_147(int xla) {
/*  8871 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_147() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8873 */       int j = 1;
/*       */       return j; } finally { jj_save(146, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_148(int xla) {
/*  8878 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_148() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8880 */       int j = 1;
/*       */       return j; } finally { jj_save(147, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_2_149(int xla) {
/*  8885 */     jj_la = xla; jj_lastpos = CPPParser.jj_scanpos = token;
/*       */     try { int i = !jj_3_149() ? 1 : 0;
/*       */       return i;
/*       */     }
/*       */     catch (LookaheadSuccess ls)
/*       */     {
/*  8887 */       int j = 1;
/*       */       return j; } finally { jj_save(148, xla); } throw localObject;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_611() {
/*  8892 */     if (jj_3R_122()) return true;
/*  8893 */     return jj_3R_135();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_62()
/*       */   {
/*  8898 */     return jj_scan_token(36);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_35()
/*       */   {
/*  8903 */     return jj_3R_87();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_28()
/*       */   {
/*  8908 */     return jj_3R_78();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_538()
/*       */   {
/*  8913 */     return jj_scan_token(151);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_31()
/*       */   {
/*  8918 */     return jj_3R_75();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_30()
/*       */   {
/*  8923 */     return jj_3R_79();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_29()
/*       */   {
/*  8928 */     return jj_3R_75();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_604()
/*       */   {
/*  8933 */     if (jj_3R_135()) return true; Token xsp;
/*       */     do
/*  8936 */       xsp = jj_scanpos;
/*  8937 */     while (!jj_3R_611()); jj_scanpos = xsp;
/*       */ 
/*  8939 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_86() {
/*  8943 */     return jj_3R_213();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_83()
/*       */   {
/*  8948 */     return jj_3R_78();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_85()
/*       */   {
/*  8953 */     return jj_3R_212();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_33()
/*       */   {
/*  8958 */     return jj_3R_78();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_537()
/*       */   {
/*  8963 */     return jj_scan_token(150);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_34()
/*       */   {
/*  8969 */     Token xsp = jj_scanpos;
/*  8970 */     if (jj_3R_85()) {
/*  8971 */       jj_scanpos = xsp;
/*  8972 */       if (jj_3R_86()) {
/*  8973 */         jj_scanpos = xsp;
/*  8974 */         if (jj_3_30()) return true;
/*       */       }
/*       */     }
/*       */     do
/*  8978 */       xsp = jj_scanpos;
/*  8979 */     while (!jj_3_31()); jj_scanpos = xsp;
/*       */ 
/*  8981 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_27()
/*       */   {
/*  8986 */     Token xsp = jj_scanpos;
/*  8987 */     if (jj_3R_83()) {
/*  8988 */       jj_scanpos = xsp;
/*  8989 */       if (jj_3_29()) return true;
/*       */     }
/*  8991 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_117() {
/*  8995 */     if (jj_3R_252()) return true;
/*  8996 */     if (jj_3R_156()) return true;
/*  8997 */     if (jj_3R_604()) return true;
/*  8998 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_21()
/*       */   {
/*  9003 */     return jj_3R_75();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_23()
/*       */   {
/*  9008 */     return jj_3R_75();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_195()
/*       */   {
/*  9013 */     if (jj_3R_78()) return true; Token xsp;
/*       */     do
/*  9016 */       xsp = jj_scanpos;
/*  9017 */     while (!jj_3_27()); jj_scanpos = xsp;
/*       */ 
/*  9019 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_20() {
/*  9023 */     return jj_3R_78();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_22()
/*       */   {
/*  9028 */     return jj_3R_79();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_438()
/*       */   {
/*  9033 */     if (jj_3R_168()) return true;
/*  9034 */     return jj_3R_181();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_437()
/*       */   {
/*  9039 */     if (jj_3R_156()) return true;
/*  9040 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_81()
/*       */   {
/*  9045 */     return jj_3R_213();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_436()
/*       */   {
/*  9050 */     return jj_3R_305();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_534()
/*       */   {
/*  9055 */     return jj_scan_token(149);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_435()
/*       */   {
/*  9060 */     return jj_3R_512();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_80()
/*       */   {
/*  9065 */     return jj_3R_212();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_434()
/*       */   {
/*  9070 */     return jj_3R_122();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_433()
/*       */   {
/*  9075 */     return jj_3R_307();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_432()
/*       */   {
/*  9080 */     return jj_3R_306();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_431()
/*       */   {
/*  9085 */     return jj_3R_511();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_77()
/*       */   {
/*  9090 */     return jj_3R_75();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_25()
/*       */   {
/*  9096 */     Token xsp = jj_scanpos;
/*  9097 */     if (jj_3R_80()) {
/*  9098 */       jj_scanpos = xsp;
/*  9099 */       if (jj_3R_81()) {
/*  9100 */         jj_scanpos = xsp;
/*  9101 */         if (jj_3_22()) return true;
/*       */       }
/*       */     }
/*       */     do
/*  9105 */       xsp = jj_scanpos;
/*  9106 */     while (!jj_3_23()); jj_scanpos = xsp;
/*       */ 
/*  9108 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_430() {
/*  9112 */     return jj_3R_510();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_24()
/*       */   {
/*  9117 */     return jj_3R_78();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_429()
/*       */   {
/*  9122 */     return jj_3R_275();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_76()
/*       */   {
/*  9127 */     return jj_3R_78();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_428()
/*       */   {
/*  9132 */     return jj_3R_274();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_427()
/*       */   {
/*  9137 */     return jj_3R_509();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_426()
/*       */   {
/*  9142 */     return jj_3R_508();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_425()
/*       */   {
/*  9147 */     return jj_3R_507();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_18()
/*       */   {
/*  9152 */     return jj_3R_75();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_19()
/*       */   {
/*  9158 */     Token xsp = jj_scanpos;
/*  9159 */     if (jj_3R_76()) {
/*  9160 */       jj_scanpos = xsp;
/*  9161 */       if (jj_3R_77()) return true;
/*       */     }
/*  9163 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_424() {
/*  9167 */     return jj_3R_506();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_423()
/*       */   {
/*  9172 */     return jj_3R_505();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_542()
/*       */   {
/*  9177 */     return jj_scan_token(148);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_422()
/*       */   {
/*  9182 */     return jj_3R_504();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_421()
/*       */   {
/*  9187 */     return jj_3R_503();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_420()
/*       */   {
/*  9192 */     return jj_3R_502();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_82()
/*       */   {
/*  9197 */     if (jj_3R_78()) return true; Token xsp;
/*       */     do
/*  9200 */       xsp = jj_scanpos;
/*  9201 */     while (!jj_3_19()); jj_scanpos = xsp;
/*       */ 
/*  9203 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_419() {
/*  9207 */     return jj_3R_501();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_147()
/*       */   {
/*  9212 */     if (jj_3R_168()) return true;
/*  9213 */     return jj_3R_181();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_418()
/*       */   {
/*  9218 */     return jj_3R_500();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_417()
/*       */   {
/*  9223 */     return jj_3R_499();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_26()
/*       */   {
/*  9229 */     Token xsp = jj_scanpos;
/*  9230 */     if (jj_3R_82()) {
/*  9231 */       jj_scanpos = xsp;
/*  9232 */       if (jj_3_25()) return true;
/*       */     }
/*  9234 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_416() {
/*  9238 */     return jj_3R_498();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_415()
/*       */   {
/*  9243 */     return jj_3R_497();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_84()
/*       */   {
/*  9248 */     return jj_3R_75();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_146()
/*       */   {
/*  9253 */     if (jj_3R_168()) return true;
/*  9254 */     return jj_3R_181();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_414()
/*       */   {
/*  9259 */     return jj_3R_496();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_413()
/*       */   {
/*  9264 */     return jj_3R_273();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_412()
/*       */   {
/*  9269 */     return jj_3R_95();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_411()
/*       */   {
/*  9274 */     return jj_3R_116();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_32()
/*       */   {
/*  9280 */     if (jj_3R_84()) return true;
/*       */     do
/*  9282 */       xsp = jj_scanpos;
/*  9283 */     while (!jj_3R_84()); jj_scanpos = xsp;
/*       */ 
/*  9285 */     Token xsp = jj_scanpos;
/*  9286 */     if (jj_3_26()) jj_scanpos = xsp;
/*  9287 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_410() {
/*  9291 */     return jj_3R_370();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_541()
/*       */   {
/*  9296 */     return jj_scan_token(147);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_409()
/*       */   {
/*  9301 */     return jj_3R_101();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_408()
/*       */   {
/*  9306 */     return jj_3R_495();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_58()
/*       */   {
/*  9312 */     Token xsp = jj_scanpos;
/*  9313 */     if (jj_3_32()) {
/*  9314 */       jj_scanpos = xsp;
/*  9315 */       if (jj_3R_195()) {
/*  9316 */         jj_scanpos = xsp;
/*  9317 */         if (jj_3_34()) return true;
/*       */       }
/*       */     }
/*  9320 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_407() {
/*  9324 */     return jj_3R_143();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_406()
/*       */   {
/*  9329 */     return jj_3R_494();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_405()
/*       */   {
/*  9334 */     return jj_3R_280();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_404()
/*       */   {
/*  9339 */     return jj_3R_279();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_403()
/*       */   {
/*  9344 */     return jj_3R_278();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_402()
/*       */   {
/*  9349 */     return jj_3R_277();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_401()
/*       */   {
/*  9354 */     return jj_3R_276();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_400()
/*       */   {
/*  9359 */     if (jj_3R_180()) return true;
/*       */ 
/*  9361 */     Token xsp = jj_scanpos;
/*  9362 */     if (jj_3_147()) jj_scanpos = xsp;
/*  9363 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_399() {
/*  9367 */     if (jj_3R_178()) return true;
/*       */ 
/*  9369 */     Token xsp = jj_scanpos;
/*  9370 */     if (jj_3_146()) jj_scanpos = xsp;
/*  9371 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_536() {
/*  9375 */     return jj_scan_token(146);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_347()
/*       */   {
/*  9381 */     Token xsp = jj_scanpos;
/*  9382 */     if (jj_3R_399()) {
/*  9383 */       jj_scanpos = xsp;
/*  9384 */       if (jj_3R_400()) {
/*  9385 */         jj_scanpos = xsp;
/*  9386 */         if (jj_3R_401()) {
/*  9387 */           jj_scanpos = xsp;
/*  9388 */           if (jj_3R_402()) {
/*  9389 */             jj_scanpos = xsp;
/*  9390 */             if (jj_3R_403()) {
/*  9391 */               jj_scanpos = xsp;
/*  9392 */               if (jj_3R_404()) {
/*  9393 */                 jj_scanpos = xsp;
/*  9394 */                 if (jj_3R_405()) {
/*  9395 */                   jj_scanpos = xsp;
/*  9396 */                   if (jj_3R_406()) {
/*  9397 */                     jj_scanpos = xsp;
/*  9398 */                     if (jj_3R_407()) {
/*  9399 */                       jj_scanpos = xsp;
/*  9400 */                       if (jj_3R_408()) {
/*  9401 */                         jj_scanpos = xsp;
/*  9402 */                         if (jj_3R_409()) {
/*  9403 */                           jj_scanpos = xsp;
/*  9404 */                           if (jj_3R_410()) {
/*  9405 */                             jj_scanpos = xsp;
/*  9406 */                             if (jj_3R_411()) {
/*  9407 */                               jj_scanpos = xsp;
/*  9408 */                               if (jj_3R_412()) {
/*  9409 */                                 jj_scanpos = xsp;
/*  9410 */                                 if (jj_3R_413()) {
/*  9411 */                                   jj_scanpos = xsp;
/*  9412 */                                   if (jj_3R_414()) {
/*  9413 */                                     jj_scanpos = xsp;
/*  9414 */                                     if (jj_3R_415()) {
/*  9415 */                                       jj_scanpos = xsp;
/*  9416 */                                       if (jj_3R_416()) {
/*  9417 */                                         jj_scanpos = xsp;
/*  9418 */                                         if (jj_3R_417()) {
/*  9419 */                                           jj_scanpos = xsp;
/*  9420 */                                           if (jj_3R_418()) {
/*  9421 */                                             jj_scanpos = xsp;
/*  9422 */                                             if (jj_3R_419()) {
/*  9423 */                                               jj_scanpos = xsp;
/*  9424 */                                               if (jj_3R_420()) {
/*  9425 */                                                 jj_scanpos = xsp;
/*  9426 */                                                 if (jj_3R_421()) {
/*  9427 */                                                   jj_scanpos = xsp;
/*  9428 */                                                   if (jj_3R_422()) {
/*  9429 */                                                     jj_scanpos = xsp;
/*  9430 */                                                     if (jj_3R_423()) {
/*  9431 */                                                       jj_scanpos = xsp;
/*  9432 */                                                       if (jj_3R_424()) {
/*  9433 */                                                         jj_scanpos = xsp;
/*  9434 */                                                         if (jj_3R_425()) {
/*  9435 */                                                           jj_scanpos = xsp;
/*  9436 */                                                           if (jj_3R_426()) {
/*  9437 */                                                             jj_scanpos = xsp;
/*  9438 */                                                             if (jj_3R_427()) {
/*  9439 */                                                               jj_scanpos = xsp;
/*  9440 */                                                               if (jj_3R_428()) {
/*  9441 */                                                                 jj_scanpos = xsp;
/*  9442 */                                                                 if (jj_3R_429()) {
/*  9443 */                                                                   jj_scanpos = xsp;
/*  9444 */                                                                   if (jj_3R_430()) {
/*  9445 */                                                                     jj_scanpos = xsp;
/*  9446 */                                                                     if (jj_3R_431()) {
/*  9447 */                                                                       jj_scanpos = xsp;
/*  9448 */                                                                       if (jj_3R_432()) {
/*  9449 */                                                                         jj_scanpos = xsp;
/*  9450 */                                                                         if (jj_3R_433()) {
/*  9451 */                                                                           jj_scanpos = xsp;
/*  9452 */                                                                           if (jj_3R_434()) {
/*  9453 */                                                                             jj_scanpos = xsp;
/*  9454 */                                                                             if (jj_3R_435()) {
/*  9455 */                                                                               jj_scanpos = xsp;
/*  9456 */                                                                               if (jj_3R_436()) {
/*  9457 */                                                                                 jj_scanpos = xsp;
/*  9458 */                                                                                 if (jj_3R_437()) {
/*  9459 */                                                                                   jj_scanpos = xsp;
/*  9460 */                                                                                   if (jj_3R_438()) {
/*  9461 */                                                                                     jj_scanpos = xsp;
/*  9462 */                                                                                     if (jj_3_149()) return true;
/*       */                                                                                   }
/*       */                                                                                 }
/*       */                                                                               }
/*       */                                                                             }
/*       */                                                                           }
/*       */                                                                         }
/*       */                                                                       }
/*       */                                                                     }
/*       */                                                                   }
/*       */                                                                 }
/*       */                                                               }
/*       */                                                             }
/*       */                                                           }
/*       */                                                         }
/*       */                                                       }
/*       */                                                     }
/*       */                                                   }
/*       */                                                 }
/*       */                                               }
/*       */                                             }
/*       */                                           }
/*       */                                         }
/*       */                                       }
/*       */                                     }
/*       */                                   }
/*       */                                 }
/*       */                               }
/*       */                             }
/*       */                           }
/*       */                         }
/*       */                       }
/*       */                     }
/*       */                   }
/*       */                 }
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/*  9503 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_488() {
/*  9507 */     return jj_3R_551();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_271()
/*       */   {
/*  9512 */     return jj_3R_331();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_487()
/*       */   {
/*  9517 */     return jj_3R_550();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_486()
/*       */   {
/*  9522 */     return jj_3R_549();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_74()
/*       */   {
/*  9527 */     return jj_3R_209();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_485()
/*       */   {
/*  9532 */     return jj_3R_548();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_484()
/*       */   {
/*  9537 */     return jj_3R_547();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_535()
/*       */   {
/*  9542 */     return jj_scan_token(145);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_483()
/*       */   {
/*  9547 */     return jj_3R_546();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_482()
/*       */   {
/*  9552 */     return jj_3R_545();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_481()
/*       */   {
/*  9557 */     return jj_3R_544();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_480()
/*       */   {
/*  9562 */     return jj_3R_543();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_299()
/*       */   {
/*  9567 */     if (jj_3R_122()) return true;
/*  9568 */     return jj_3R_130();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_479()
/*       */   {
/*  9573 */     return jj_3R_542();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_478()
/*       */   {
/*  9578 */     return jj_3R_541();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_477()
/*       */   {
/*  9583 */     return jj_3R_540();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_476()
/*       */   {
/*  9588 */     return jj_3R_539();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_17()
/*       */   {
/*  9594 */     Token xsp = jj_scanpos;
/*  9595 */     if (jj_3R_74()) jj_scanpos = xsp;
/*  9596 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_475()
/*       */   {
/*  9601 */     return jj_3R_538();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_474()
/*       */   {
/*  9606 */     return jj_3R_537();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_141()
/*       */   {
/*  9612 */     Token xsp = jj_scanpos;
/*  9613 */     if (jj_3_17()) {
/*  9614 */       jj_scanpos = xsp;
/*  9615 */       if (jj_3R_271()) return true;
/*       */     }
/*  9617 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_473() {
/*  9621 */     return jj_3R_536();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_472()
/*       */   {
/*  9626 */     return jj_3R_535();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_471()
/*       */   {
/*  9631 */     return jj_3R_534();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_176()
/*       */   {
/*  9636 */     return jj_scan_token(163);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_380()
/*       */   {
/*  9642 */     Token xsp = jj_scanpos;
/*  9643 */     if (jj_3R_471()) {
/*  9644 */       jj_scanpos = xsp;
/*  9645 */       if (jj_3R_472()) {
/*  9646 */         jj_scanpos = xsp;
/*  9647 */         if (jj_3R_473()) {
/*  9648 */           jj_scanpos = xsp;
/*  9649 */           if (jj_3R_474()) {
/*  9650 */             jj_scanpos = xsp;
/*  9651 */             if (jj_3R_475()) {
/*  9652 */               jj_scanpos = xsp;
/*  9653 */               if (jj_3R_476()) {
/*  9654 */                 jj_scanpos = xsp;
/*  9655 */                 if (jj_3R_477()) {
/*  9656 */                   jj_scanpos = xsp;
/*  9657 */                   if (jj_3R_478()) {
/*  9658 */                     jj_scanpos = xsp;
/*  9659 */                     if (jj_3R_479()) {
/*  9660 */                       jj_scanpos = xsp;
/*  9661 */                       if (jj_3R_480()) {
/*  9662 */                         jj_scanpos = xsp;
/*  9663 */                         if (jj_3R_481()) {
/*  9664 */                           jj_scanpos = xsp;
/*  9665 */                           if (jj_3R_482()) {
/*  9666 */                             jj_scanpos = xsp;
/*  9667 */                             if (jj_3R_483()) {
/*  9668 */                               jj_scanpos = xsp;
/*  9669 */                               if (jj_3R_484()) {
/*  9670 */                                 jj_scanpos = xsp;
/*  9671 */                                 if (jj_scan_token(177)) {
/*  9672 */                                   jj_scanpos = xsp;
/*  9673 */                                   if (jj_3R_485()) {
/*  9674 */                                     jj_scanpos = xsp;
/*  9675 */                                     if (jj_3R_486()) {
/*  9676 */                                       jj_scanpos = xsp;
/*  9677 */                                       if (jj_3R_487()) {
/*  9678 */                                         jj_scanpos = xsp;
/*  9679 */                                         if (jj_3R_488()) return true;
/*       */                                       }
/*       */                                     }
/*       */                                   }
/*       */                                 }
/*       */                               }
/*       */                             }
/*       */                           }
/*       */                         }
/*       */                       }
/*       */                     }
/*       */                   }
/*       */                 }
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/*  9698 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_139() {
/*  9702 */     return jj_3R_118();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_179()
/*       */   {
/*  9707 */     return jj_3R_90();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_118()
/*       */   {
/*  9712 */     if (jj_3R_130()) return true; Token xsp;
/*       */     do
/*  9715 */       xsp = jj_scanpos;
/*  9716 */     while (!jj_3R_299()); jj_scanpos = xsp;
/*       */ 
/*  9718 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_145()
/*       */   {
/*  9723 */     Token xsp = jj_scanpos;
/*  9724 */     if (jj_3R_179()) jj_scanpos = xsp;
/*  9725 */     return jj_3R_180();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_177()
/*       */   {
/*  9730 */     return jj_3R_90();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_206()
/*       */   {
/*  9735 */     return jj_3R_331();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_144()
/*       */   {
/*  9741 */     Token xsp = jj_scanpos;
/*  9742 */     if (jj_3R_177()) jj_scanpos = xsp;
/*  9743 */     return jj_3R_178();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_547()
/*       */   {
/*  9748 */     return jj_scan_token(162);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_73()
/*       */   {
/*  9753 */     return jj_3R_209();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_314()
/*       */   {
/*  9758 */     return jj_3R_380();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_313()
/*       */   {
/*  9763 */     return jj_3R_304();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_312()
/*       */   {
/*  9768 */     return jj_3R_379();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_311()
/*       */   {
/*  9773 */     return jj_3R_378();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_16()
/*       */   {
/*  9778 */     if (jj_3R_58()) return true;
/*       */ 
/*  9780 */     Token xsp = jj_scanpos;
/*  9781 */     if (jj_3R_73()) jj_scanpos = xsp;
/*  9782 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_143()
/*       */   {
/*  9787 */     return jj_3R_176();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_310()
/*       */   {
/*  9792 */     if (jj_3R_156()) return true;
/*  9793 */     if (jj_3R_138()) return true;
/*  9794 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_309()
/*       */   {
/*  9800 */     if (jj_3_143()) return true; Token xsp;
/*       */     do xsp = jj_scanpos;
/*  9803 */     while (!jj_3_143()); jj_scanpos = xsp;
/*       */ 
/*  9805 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_15() {
/*  9809 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_14()
/*       */   {
/*  9814 */     return jj_3R_53();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_175()
/*       */   {
/*  9820 */     Token xsp = jj_scanpos;
/*  9821 */     if (jj_3R_308()) {
/*  9822 */       jj_scanpos = xsp;
/*  9823 */       if (jj_3R_309()) {
/*  9824 */         jj_scanpos = xsp;
/*  9825 */         if (jj_3R_310()) {
/*  9826 */           jj_scanpos = xsp;
/*  9827 */           if (jj_3R_311()) {
/*  9828 */             jj_scanpos = xsp;
/*  9829 */             if (jj_3R_312()) {
/*  9830 */               jj_scanpos = xsp;
/*  9831 */               if (jj_3R_313()) {
/*  9832 */                 jj_scanpos = xsp;
/*  9833 */                 if (jj_3R_314()) return true;
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/*  9840 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_308() {
/*  9844 */     return jj_3R_377();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_591()
/*       */   {
/*  9849 */     if (jj_3R_122()) return true;
/*  9850 */     return jj_3R_138();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_88()
/*       */   {
/*  9855 */     return jj_scan_token(164);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_70()
/*       */   {
/*  9861 */     Token xsp = jj_scanpos;
/*  9862 */     if (jj_3_16()) {
/*  9863 */       jj_scanpos = xsp;
/*  9864 */       if (jj_3R_206()) return true;
/*       */     }
/*  9866 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_142() {
/*  9870 */     return jj_3R_92();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_376()
/*       */   {
/*  9875 */     if (jj_3R_101()) return true;
/*  9876 */     return jj_3R_88();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_375()
/*       */   {
/*  9881 */     if (jj_3R_69()) return true;
/*  9882 */     return jj_3R_347();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_453()
/*       */   {
/*  9887 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_374()
/*       */   {
/*  9892 */     return jj_3R_88();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_138()
/*       */   {
/*  9897 */     return jj_3R_118();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_373()
/*       */   {
/*  9902 */     return jj_3R_205();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_304()
/*       */   {
/*  9908 */     Token xsp = jj_scanpos;
/*  9909 */     if (jj_3R_373()) jj_scanpos = xsp;
/*  9910 */     xsp = jj_scanpos;
/*  9911 */     if (jj_scan_token(178)) {
/*  9912 */       jj_scanpos = xsp;
/*  9913 */       if (jj_scan_token(165)) {
/*  9914 */         jj_scanpos = xsp;
/*  9915 */         if (jj_3R_374()) {
/*  9916 */           jj_scanpos = xsp;
/*  9917 */           if (jj_3R_375()) {
/*  9918 */             jj_scanpos = xsp;
/*  9919 */             if (jj_3R_376()) return true;
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/*  9924 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_368() {
/*  9928 */     return jj_scan_token(144);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_141()
/*       */   {
/*  9933 */     if (jj_3R_87()) return true;
/*  9934 */     if (jj_3R_156()) return true;
/*       */ 
/*  9936 */     Token xsp = jj_scanpos;
/*  9937 */     if (jj_3_139()) jj_scanpos = xsp;
/*  9938 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_174()
/*       */   {
/*  9943 */     return jj_3R_307();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_331()
/*       */   {
/*  9948 */     if (jj_3R_390()) return true;
/*  9949 */     if (jj_3R_176()) return true;
/*  9950 */     if (jj_3R_62()) return true;
/*       */     do
/*       */     {
/*  9953 */       xsp = jj_scanpos;
/*  9954 */     }while (!jj_3_14()); jj_scanpos = xsp;
/*       */ 
/*  9956 */     if (jj_3R_127()) return true;
/*  9957 */     Token xsp = jj_scanpos;
/*  9958 */     if (jj_3R_453()) jj_scanpos = xsp;
/*  9959 */     return jj_3R_70();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_173()
/*       */   {
/*  9964 */     return jj_3R_306();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_172()
/*       */   {
/*  9969 */     if (jj_3R_305()) return true;
/*  9970 */     return jj_3R_304();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_171()
/*       */   {
/*  9975 */     if (jj_3R_303()) return true;
/*  9976 */     return jj_3R_304();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_170()
/*       */   {
/*  9981 */     if (jj_3R_156()) return true;
/*       */ 
/*  9983 */     Token xsp = jj_scanpos;
/*  9984 */     if (jj_3_138()) jj_scanpos = xsp;
/*  9985 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_169()
/*       */   {
/*  9990 */     if (jj_3R_168()) return true;
/*  9991 */     if (jj_3R_138()) return true; Token xsp;
/*       */     do
/*  9994 */       xsp = jj_scanpos;
/*  9995 */     while (!jj_3R_591()); jj_scanpos = xsp;
/*       */ 
/*  9997 */     return jj_3R_181();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_137()
/*       */   {
/* 10003 */     Token xsp = jj_scanpos;
/* 10004 */     if (jj_3R_169()) {
/* 10005 */       jj_scanpos = xsp;
/* 10006 */       if (jj_3R_170()) {
/* 10007 */         jj_scanpos = xsp;
/* 10008 */         if (jj_3R_171()) {
/* 10009 */           jj_scanpos = xsp;
/* 10010 */           if (jj_3R_172()) {
/* 10011 */             jj_scanpos = xsp;
/* 10012 */             if (jj_3R_173()) {
/* 10013 */               jj_scanpos = xsp;
/* 10014 */               if (jj_3R_174()) return true;
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 10020 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_367() {
/* 10024 */     return jj_scan_token(143);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_600()
/*       */   {
/* 10029 */     return jj_3R_356();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_599()
/*       */   {
/* 10034 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_533()
/*       */   {
/* 10039 */     if (jj_3R_122()) return true;
/* 10040 */     return jj_3R_138();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_160()
/*       */   {
/* 10046 */     Token xsp = jj_scanpos;
/* 10047 */     if (jj_3_140()) {
/* 10048 */       jj_scanpos = xsp;
/* 10049 */       if (jj_3_141()) return true;
/*       */     }
/* 10051 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_140() {
/* 10055 */     if (jj_3R_175()) return true; Token xsp;
/*       */     do
/* 10058 */       xsp = jj_scanpos;
/* 10059 */     while (!jj_3_137()); jj_scanpos = xsp;
/*       */ 
/* 10061 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_588()
/*       */   {
/* 10066 */     Token xsp = jj_scanpos;
/* 10067 */     if (jj_3R_599()) {
/* 10068 */       jj_scanpos = xsp;
/* 10069 */       if (jj_3R_600()) return true;
/*       */     }
/* 10071 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_470() {
/* 10075 */     if (jj_3R_168()) return true;
/* 10076 */     return jj_3R_181();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_366()
/*       */   {
/* 10081 */     return jj_scan_token(142);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_384()
/*       */   {
/* 10086 */     if (jj_3R_71()) return true;
/* 10087 */     return jj_3R_588();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_134()
/*       */   {
/* 10092 */     return jj_3R_167();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_13()
/*       */   {
/* 10097 */     if (jj_3R_58()) return true;
/* 10098 */     if (jj_3R_71()) return true;
/* 10099 */     return jj_3R_588();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_319()
/*       */   {
/* 10105 */     Token xsp = jj_scanpos;
/* 10106 */     if (jj_3_13()) {
/* 10107 */       jj_scanpos = xsp;
/* 10108 */       if (jj_3R_384()) return true;
/*       */     }
/* 10110 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_136() {
/* 10114 */     return jj_3R_118();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_589()
/*       */   {
/* 10119 */     return jj_3R_209();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_56()
/*       */   {
/* 10124 */     return jj_3R_58();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_293()
/*       */   {
/* 10129 */     return jj_3R_370();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_365()
/*       */   {
/* 10134 */     return jj_scan_token(141);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_3()
/*       */   {
/* 10140 */     Token xsp = jj_scanpos;
/* 10141 */     if (jj_3R_56()) jj_scanpos = xsp;
/* 10142 */     return jj_3R_57();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_292()
/*       */   {
/* 10147 */     return jj_3R_101();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_291()
/*       */   {
/* 10152 */     return jj_3R_277();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_290()
/*       */   {
/* 10157 */     return jj_3R_276();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_161()
/*       */   {
/* 10163 */     Token xsp = jj_scanpos;
/* 10164 */     if (jj_3R_288()) {
/* 10165 */       jj_scanpos = xsp;
/* 10166 */       if (jj_3R_289()) {
/* 10167 */         jj_scanpos = xsp;
/* 10168 */         if (jj_3R_290()) {
/* 10169 */           jj_scanpos = xsp;
/* 10170 */           if (jj_3R_291()) {
/* 10171 */             jj_scanpos = xsp;
/* 10172 */             if (jj_3R_292()) {
/* 10173 */               jj_scanpos = xsp;
/* 10174 */               if (jj_3R_293()) return true;
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 10180 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_289() {
/* 10184 */     return jj_3R_278();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_2()
/*       */   {
/* 10189 */     if (jj_3R_54()) return true;
/* 10190 */     return jj_3R_55();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_288()
/*       */   {
/* 10195 */     return jj_3R_143();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_192()
/*       */   {
/* 10200 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_12()
/*       */   {
/* 10205 */     return jj_3R_70();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_4()
/*       */   {
/* 10210 */     if (jj_3R_58()) return true;
/*       */ 
/* 10212 */     Token xsp = jj_scanpos;
/* 10213 */     if (jj_3R_589()) jj_scanpos = xsp;
/* 10214 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_469()
/*       */   {
/* 10219 */     return jj_3R_90();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_379()
/*       */   {
/* 10225 */     Token xsp = jj_scanpos;
/* 10226 */     if (jj_3R_469()) jj_scanpos = xsp;
/* 10227 */     if (jj_3R_180()) return true;
/* 10228 */     xsp = jj_scanpos;
/* 10229 */     if (jj_3R_470()) jj_scanpos = xsp;
/* 10230 */     return jj_3R_162();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_197()
/*       */   {
/* 10235 */     return jj_3R_202();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_571()
/*       */   {
/* 10240 */     return jj_3R_319();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_64()
/*       */   {
/* 10245 */     return jj_3R_88();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_68()
/*       */   {
/* 10250 */     return jj_3R_205();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_252()
/*       */   {
/* 10255 */     return jj_scan_token(138);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_11()
/*       */   {
/* 10261 */     Token xsp = jj_scanpos;
/* 10262 */     if (jj_3R_68()) jj_scanpos = xsp;
/* 10263 */     return jj_3R_69();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_570()
/*       */   {
/* 10268 */     return jj_3R_318();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_569()
/*       */   {
/* 10273 */     return jj_3R_209();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_67()
/*       */   {
/* 10278 */     return jj_3R_58();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_166()
/*       */   {
/* 10283 */     if (jj_3R_156()) return true;
/*       */ 
/* 10285 */     Token xsp = jj_scanpos;
/* 10286 */     if (jj_3_136()) jj_scanpos = xsp;
/* 10287 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_10()
/*       */   {
/* 10293 */     Token xsp = jj_scanpos;
/* 10294 */     if (jj_3R_67()) jj_scanpos = xsp;
/* 10295 */     return jj_3R_57();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_191()
/*       */   {
/* 10300 */     if (jj_3R_202()) return true;
/*       */ 
/* 10302 */     Token xsp = jj_scanpos;
/* 10303 */     if (jj_3R_570()) {
/* 10304 */       jj_scanpos = xsp;
/* 10305 */       if (jj_3R_571()) {
/* 10306 */         jj_scanpos = xsp;
/* 10307 */         if (jj_3_4()) return true;
/*       */       }
/*       */     }
/* 10310 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_9() {
/* 10314 */     if (jj_3R_54()) return true;
/* 10315 */     return jj_3R_55();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_315()
/*       */   {
/* 10320 */     if (jj_3R_156()) return true;
/* 10321 */     if (jj_3R_138()) return true;
/* 10322 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_135()
/*       */   {
/* 10327 */     if (jj_3R_168()) return true;
/* 10328 */     if (jj_3R_138()) return true; Token xsp;
/*       */     do
/* 10331 */       xsp = jj_scanpos;
/* 10332 */     while (!jj_3R_533()); jj_scanpos = xsp;
/*       */ 
/* 10334 */     return jj_3R_181();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_65()
/*       */   {
/* 10339 */     return jj_3R_202();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_372()
/*       */   {
/* 10345 */     if (jj_3_135()) return true; Token xsp;
/*       */     do xsp = jj_scanpos;
/* 10348 */     while (!jj_3_135()); jj_scanpos = xsp;
/*       */ 
/* 10350 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_190() {
/* 10354 */     return jj_3R_320();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_8()
/*       */   {
/* 10360 */     Token xsp = jj_scanpos;
/* 10361 */     if (jj_3R_65()) jj_scanpos = xsp;
/* 10362 */     if (jj_3R_54()) return true;
/* 10363 */     if (jj_3R_66()) return true;
/* 10364 */     return jj_3R_62();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_133()
/*       */   {
/* 10369 */     return jj_3R_167();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_551()
/*       */   {
/* 10374 */     return jj_scan_token(137);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_7()
/*       */   {
/* 10379 */     if (jj_3R_63()) return true;
/*       */ 
/* 10381 */     Token xsp = jj_scanpos;
/* 10382 */     if (jj_3R_64()) jj_scanpos = xsp;
/* 10383 */     return jj_3R_62();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_302()
/*       */   {
/* 10388 */     if (jj_3R_113()) return true;
/* 10389 */     if (jj_3R_351()) return true;
/*       */ 
/* 10391 */     Token xsp = jj_scanpos;
/* 10392 */     if (jj_3_134()) jj_scanpos = xsp;
/* 10393 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_189() {
/* 10397 */     return jj_3R_319();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_167()
/*       */   {
/* 10403 */     Token xsp = jj_scanpos;
/* 10404 */     if (jj_3R_301()) {
/* 10405 */       jj_scanpos = xsp;
/* 10406 */       if (jj_3R_302()) return true;
/*       */     }
/* 10408 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_301() {
/* 10412 */     return jj_3R_372();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_60()
/*       */   {
/* 10418 */     Token xsp = jj_scanpos;
/* 10419 */     if (jj_3R_196()) {
/* 10420 */       jj_scanpos = xsp;
/* 10421 */       if (jj_3R_197()) return true;
/*       */     }
/* 10423 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_196() {
/* 10427 */     return jj_3R_233();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_6()
/*       */   {
/* 10433 */     Token xsp = jj_scanpos;
/* 10434 */     if (jj_3R_60()) jj_scanpos = xsp;
/* 10435 */     if (jj_3R_61()) return true;
/* 10436 */     return jj_3R_62();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_188()
/*       */   {
/* 10441 */     return jj_3R_318();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_132()
/*       */   {
/* 10446 */     return jj_3R_166();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_129()
/*       */   {
/* 10451 */     return jj_3R_58();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_5()
/*       */   {
/* 10456 */     return jj_3R_59();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_576()
/*       */   {
/* 10461 */     return jj_3R_167();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_187()
/*       */   {
/* 10466 */     return jj_3R_317();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_316()
/*       */   {
/* 10471 */     return jj_3R_202();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_128()
/*       */   {
/* 10476 */     if (jj_3R_156()) return true;
/* 10477 */     if (jj_3R_135()) return true;
/* 10478 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_186()
/*       */   {
/* 10483 */     if (jj_3R_213()) return true;
/*       */ 
/* 10485 */     Token xsp = jj_scanpos;
/* 10486 */     if (jj_3R_569()) jj_scanpos = xsp;
/* 10487 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_300()
/*       */   {
/* 10492 */     if (jj_3R_58()) return true;
/*       */ 
/* 10494 */     Token xsp = jj_scanpos;
/* 10495 */     if (jj_3R_576()) jj_scanpos = xsp;
/* 10496 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_550() {
/* 10500 */     return jj_scan_token(136);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_127()
/*       */   {
/* 10505 */     if (jj_3R_156()) return true;
/* 10506 */     return jj_3R_118();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_185()
/*       */   {
/* 10512 */     Token xsp = jj_scanpos;
/* 10513 */     if (jj_3R_316()) jj_scanpos = xsp;
/* 10514 */     return jj_3R_70();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_468()
/*       */   {
/* 10519 */     return jj_3R_166();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_165()
/*       */   {
/* 10524 */     return jj_3R_300();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_130()
/*       */   {
/* 10529 */     if (jj_3R_156()) return true;
/* 10530 */     if (jj_3R_135()) return true;
/* 10531 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_184()
/*       */   {
/* 10536 */     if (jj_3R_59()) return true; Token xsp;
/*       */     do
/* 10539 */       xsp = jj_scanpos;
/* 10540 */     while (!jj_3R_315()); jj_scanpos = xsp;
/*       */ 
/* 10542 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_164()
/*       */   {
/* 10547 */     if (jj_3R_156()) return true;
/* 10548 */     if (jj_3R_135()) return true;
/* 10549 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_126()
/*       */   {
/* 10554 */     return jj_3R_90();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_53()
/*       */   {
/* 10560 */     Token xsp = jj_scanpos;
/* 10561 */     if (jj_3R_184()) {
/* 10562 */       jj_scanpos = xsp;
/* 10563 */       if (jj_3R_185()) {
/* 10564 */         jj_scanpos = xsp;
/* 10565 */         if (jj_3R_186()) {
/* 10566 */           jj_scanpos = xsp;
/* 10567 */           if (jj_3R_187()) {
/* 10568 */             jj_scanpos = xsp;
/* 10569 */             if (jj_3R_188()) {
/* 10570 */               jj_scanpos = xsp;
/* 10571 */               if (jj_3R_189()) {
/* 10572 */                 jj_scanpos = xsp;
/* 10573 */                 if (jj_3R_190()) {
/* 10574 */                   jj_scanpos = xsp;
/* 10575 */                   if (jj_3R_191()) {
/* 10576 */                     jj_scanpos = xsp;
/* 10577 */                     if (jj_3_12()) {
/* 10578 */                       jj_scanpos = xsp;
/* 10579 */                       if (jj_3R_192()) return true;
/*       */                     }
/*       */                   }
/*       */                 }
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 10589 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_163() {
/* 10593 */     if (jj_3R_156()) return true;
/* 10594 */     if (jj_3R_118()) return true;
/* 10595 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_131()
/*       */   {
/* 10601 */     Token xsp = jj_scanpos;
/* 10602 */     if (jj_3R_163()) jj_scanpos = xsp;
/* 10603 */     xsp = jj_scanpos;
/* 10604 */     if (jj_3R_164()) {
/* 10605 */       jj_scanpos = xsp;
/* 10606 */       if (jj_3R_165()) return true;
/*       */     }
/* 10608 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_467() {
/* 10612 */     if (jj_3R_156()) return true;
/* 10613 */     if (jj_3R_135()) return true;
/* 10614 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_69()
/*       */   {
/* 10619 */     return jj_scan_token(135);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_1()
/*       */   {
/* 10624 */     return jj_3R_53();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_122()
/*       */   {
/* 10629 */     return jj_3R_156();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_466()
/*       */   {
/* 10634 */     return jj_3R_90();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_378()
/*       */   {
/* 10640 */     Token xsp = jj_scanpos;
/* 10641 */     if (jj_3R_466()) jj_scanpos = xsp;
/* 10642 */     if (jj_3R_178()) return true;
/* 10643 */     xsp = jj_scanpos;
/* 10644 */     if (jj_3R_467()) {
/* 10645 */       jj_scanpos = xsp;
/* 10646 */       if (jj_3_131()) return true;
/*       */     }
/* 10648 */     xsp = jj_scanpos;
/* 10649 */     if (jj_3R_468()) jj_scanpos = xsp;
/* 10650 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_125() {
/* 10654 */     return jj_3R_160();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_123()
/*       */   {
/* 10659 */     return jj_3R_159();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_531()
/*       */   {
/* 10664 */     if (jj_3R_156()) return true;
/* 10665 */     if (jj_3R_135()) return true;
/* 10666 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_525()
/*       */   {
/* 10671 */     return jj_scan_token(134);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_287()
/*       */   {
/* 10676 */     if (jj_3R_369()) return true;
/*       */ 
/* 10678 */     Token xsp = jj_scanpos;
/* 10679 */     if (jj_3R_531()) {
/* 10680 */       jj_scanpos = xsp;
/* 10681 */       if (jj_3_123()) return true;
/*       */     }
/* 10683 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_124() {
/* 10687 */     if (jj_3R_161()) return true;
/* 10688 */     return jj_3R_162();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_286()
/*       */   {
/* 10693 */     if (jj_3R_307()) return true;
/* 10694 */     return jj_3R_159();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_159()
/*       */   {
/* 10700 */     Token xsp = jj_scanpos;
/* 10701 */     if (jj_3R_285()) {
/* 10702 */       jj_scanpos = xsp;
/* 10703 */       if (jj_3R_286()) {
/* 10704 */         jj_scanpos = xsp;
/* 10705 */         if (jj_3_124()) {
/* 10706 */           jj_scanpos = xsp;
/* 10707 */           if (jj_3R_287()) {
/* 10708 */             jj_scanpos = xsp;
/* 10709 */             if (jj_3_125()) return true;
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 10714 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_285() {
/* 10718 */     if (jj_3R_306()) return true;
/* 10719 */     return jj_3R_159();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_109()
/*       */   {
/* 10724 */     return jj_scan_token(133);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_284()
/*       */   {
/* 10729 */     return jj_3R_368();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_283()
/*       */   {
/* 10734 */     return jj_3R_367();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_282()
/*       */   {
/* 10739 */     return jj_3R_366();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_281()
/*       */   {
/* 10744 */     return jj_3R_365();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_158()
/*       */   {
/* 10750 */     Token xsp = jj_scanpos;
/* 10751 */     if (jj_3R_281()) {
/* 10752 */       jj_scanpos = xsp;
/* 10753 */       if (jj_3R_282()) {
/* 10754 */         jj_scanpos = xsp;
/* 10755 */         if (jj_3R_283()) {
/* 10756 */           jj_scanpos = xsp;
/* 10757 */           if (jj_3R_284()) return true;
/*       */         }
/*       */       }
/*       */     }
/* 10761 */     if (jj_3R_95()) return true;
/* 10762 */     if (jj_3R_135()) return true;
/* 10763 */     if (jj_3R_273()) return true;
/* 10764 */     if (jj_3R_156()) return true;
/* 10765 */     if (jj_3R_162()) return true;
/* 10766 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_371()
/*       */   {
/* 10771 */     if (jj_3R_156()) return true;
/* 10772 */     if (jj_3R_135()) return true;
/* 10773 */     if (jj_3R_157()) return true;
/* 10774 */     return jj_3R_162();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_345()
/*       */   {
/* 10779 */     return jj_scan_token(131);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_617()
/*       */   {
/* 10784 */     return jj_3R_512();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_154()
/*       */   {
/* 10789 */     return jj_3R_280();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_119()
/*       */   {
/* 10794 */     return jj_3R_158();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_118()
/*       */   {
/* 10799 */     if (jj_3R_156()) return true;
/* 10800 */     if (jj_3R_78()) return true;
/* 10801 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_117()
/*       */   {
/* 10806 */     if (jj_3R_156()) return true;
/* 10807 */     if (jj_3R_88()) return true;
/* 10808 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_121()
/*       */   {
/* 10813 */     return jj_3R_160();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_616()
/*       */   {
/* 10818 */     return jj_3R_621();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_593()
/*       */   {
/* 10823 */     return jj_3R_505();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_120()
/*       */   {
/* 10828 */     return jj_3R_159();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_607()
/*       */   {
/* 10834 */     Token xsp = jj_scanpos;
/* 10835 */     if (jj_3R_616()) {
/* 10836 */       jj_scanpos = xsp;
/* 10837 */       if (jj_3R_617()) return true;
/*       */     }
/* 10839 */     return jj_3R_162();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_153()
/*       */   {
/* 10844 */     return jj_3R_279();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_298()
/*       */   {
/* 10849 */     return jj_3R_158();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_297()
/*       */   {
/* 10854 */     return jj_3R_371();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_296()
/*       */   {
/* 10859 */     if (jj_3R_156()) return true;
/* 10860 */     if (jj_3R_88()) return true;
/* 10861 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_295()
/*       */   {
/* 10866 */     return jj_3R_129();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_150()
/*       */   {
/* 10871 */     return jj_3R_277();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_294()
/*       */   {
/* 10876 */     return jj_3R_128();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_162()
/*       */   {
/* 10882 */     Token xsp = jj_scanpos;
/* 10883 */     if (jj_3R_294()) {
/* 10884 */       jj_scanpos = xsp;
/* 10885 */       if (jj_3R_295()) {
/* 10886 */         jj_scanpos = xsp;
/* 10887 */         if (jj_3R_296()) {
/* 10888 */           jj_scanpos = xsp;
/* 10889 */           if (jj_3R_297()) {
/* 10890 */             jj_scanpos = xsp;
/* 10891 */             if (jj_3R_298()) {
/* 10892 */               jj_scanpos = xsp;
/* 10893 */               if (jj_3_120()) {
/* 10894 */                 jj_scanpos = xsp;
/* 10895 */                 if (jj_3_121()) return true;
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 10902 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_152() {
/* 10906 */     return jj_3R_278();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_592()
/*       */   {
/* 10911 */     return jj_3R_504();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_324()
/*       */   {
/* 10916 */     return jj_scan_token(129);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_579()
/*       */   {
/* 10922 */     Token xsp = jj_scanpos;
/* 10923 */     if (jj_3R_592()) {
/* 10924 */       jj_scanpos = xsp;
/* 10925 */       if (jj_3R_593()) return true;
/*       */     }
/* 10927 */     return jj_3R_257();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_149()
/*       */   {
/* 10932 */     return jj_3R_276();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_615()
/*       */   {
/* 10937 */     return jj_3R_508();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_155()
/*       */   {
/* 10942 */     if (jj_3R_162()) return true; Token xsp;
/*       */     do
/* 10945 */       xsp = jj_scanpos;
/* 10946 */     while (!jj_3R_607()); jj_scanpos = xsp;
/*       */ 
/* 10948 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_116()
/*       */   {
/* 10953 */     Token xsp = jj_scanpos;
/* 10954 */     if (jj_3R_152()) {
/* 10955 */       jj_scanpos = xsp;
/* 10956 */       if (jj_3R_153()) {
/* 10957 */         jj_scanpos = xsp;
/* 10958 */         if (jj_3R_154()) return true;
/*       */       }
/*       */     }
/* 10961 */     return jj_3R_155();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_233()
/*       */   {
/* 10966 */     return jj_scan_token(128);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_151()
/*       */   {
/* 10971 */     if (jj_3R_155()) return true; Token xsp;
/*       */     do
/* 10974 */       xsp = jj_scanpos;
/* 10975 */     while (!jj_3_116()); jj_scanpos = xsp;
/*       */ 
/* 10977 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_614() {
/* 10981 */     return jj_3R_509();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_115()
/*       */   {
/* 10987 */     Token xsp = jj_scanpos;
/* 10988 */     if (jj_3R_149()) {
/* 10989 */       jj_scanpos = xsp;
/* 10990 */       if (jj_3R_150()) return true;
/*       */     }
/* 10992 */     return jj_3R_151();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_257()
/*       */   {
/* 10997 */     if (jj_3R_151()) return true; Token xsp;
/*       */     do
/* 11000 */       xsp = jj_scanpos;
/* 11001 */     while (!jj_3_115()); jj_scanpos = xsp;
/*       */ 
/* 11003 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_606()
/*       */   {
/* 11008 */     Token xsp = jj_scanpos;
/* 11009 */     if (jj_3R_614()) {
/* 11010 */       jj_scanpos = xsp;
/* 11011 */       if (jj_3R_615()) return true;
/*       */     }
/* 11013 */     return jj_3R_272();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_136()
/*       */   {
/* 11018 */     if (jj_3R_257()) return true; Token xsp;
/*       */     do
/* 11021 */       xsp = jj_scanpos;
/* 11022 */     while (!jj_3R_579()); jj_scanpos = xsp;
/*       */ 
/* 11024 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_113() {
/* 11028 */     if (jj_3R_143()) return true;
/* 11029 */     return jj_3R_144();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_448()
/*       */   {
/* 11034 */     return jj_scan_token(127);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_148()
/*       */   {
/* 11039 */     return jj_3R_275();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_147()
/*       */   {
/* 11044 */     return jj_3R_274();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_146()
/*       */   {
/* 11049 */     return jj_3R_273();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_145()
/*       */   {
/* 11054 */     return jj_3R_95();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_572()
/*       */   {
/* 11059 */     if (jj_3R_495()) return true;
/* 11060 */     return jj_3R_555();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_590()
/*       */   {
/* 11065 */     if (jj_3R_494()) return true;
/* 11066 */     return jj_3R_565();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_559()
/*       */   {
/* 11071 */     if (jj_3R_510()) return true;
/* 11072 */     return jj_3R_513();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_114()
/*       */   {
/* 11078 */     Token xsp = jj_scanpos;
/* 11079 */     if (jj_3R_145()) {
/* 11080 */       jj_scanpos = xsp;
/* 11081 */       if (jj_3R_146()) {
/* 11082 */         jj_scanpos = xsp;
/* 11083 */         if (jj_3R_147()) {
/* 11084 */           jj_scanpos = xsp;
/* 11085 */           if (jj_3R_148()) return true;
/*       */         }
/*       */       }
/*       */     }
/* 11089 */     return jj_3R_136();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_377()
/*       */   {
/* 11094 */     return jj_scan_token(126);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_272()
/*       */   {
/* 11099 */     if (jj_3R_136()) return true; Token xsp;
/*       */     do
/* 11102 */       xsp = jj_scanpos;
/* 11103 */     while (!jj_3_114()); jj_scanpos = xsp;
/*       */ 
/* 11105 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_529() {
/* 11109 */     if (jj_3R_511()) return true;
/* 11110 */     return jj_3R_439();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_144()
/*       */   {
/* 11115 */     if (jj_3R_272()) return true; Token xsp;
/*       */     do
/* 11118 */       xsp = jj_scanpos;
/* 11119 */     while (!jj_3R_606()); jj_scanpos = xsp;
/*       */ 
/* 11121 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_565() {
/* 11125 */     if (jj_3R_144()) return true; Token xsp;
/*       */     do
/* 11128 */       xsp = jj_scanpos;
/* 11129 */     while (!jj_3_113()); jj_scanpos = xsp;
/*       */ 
/* 11131 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_326() {
/* 11135 */     return jj_scan_token(125);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_555()
/*       */   {
/* 11140 */     if (jj_3R_565()) return true; Token xsp;
/*       */     do
/* 11143 */       xsp = jj_scanpos;
/* 11144 */     while (!jj_3R_590()); jj_scanpos = xsp;
/*       */ 
/* 11146 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_513() {
/* 11150 */     if (jj_3R_555()) return true; Token xsp;
/*       */     do
/* 11153 */       xsp = jj_scanpos;
/* 11154 */     while (!jj_3R_572()); jj_scanpos = xsp;
/*       */ 
/* 11156 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_439() {
/* 11160 */     if (jj_3R_513()) return true; Token xsp;
/*       */     do
/* 11163 */       xsp = jj_scanpos;
/* 11164 */     while (!jj_3R_559()); jj_scanpos = xsp;
/*       */ 
/* 11166 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_518() {
/* 11170 */     return jj_scan_token(124);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_354()
/*       */   {
/* 11175 */     if (jj_3R_439()) return true; Token xsp;
/*       */     do
/* 11178 */       xsp = jj_scanpos;
/* 11179 */     while (!jj_3R_529()); jj_scanpos = xsp;
/*       */ 
/* 11181 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_115() {
/* 11185 */     return jj_3R_251();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_134()
/*       */   {
/* 11190 */     return jj_scan_token(123);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_142()
/*       */   {
/* 11195 */     return jj_3R_122();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_454()
/*       */   {
/* 11200 */     if (jj_3R_530()) return true;
/* 11201 */     if (jj_3R_251()) return true;
/* 11202 */     if (jj_3R_105()) return true;
/* 11203 */     return jj_3R_251();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_251()
/*       */   {
/* 11208 */     if (jj_3R_354()) return true;
/*       */ 
/* 11210 */     Token xsp = jj_scanpos;
/* 11211 */     if (jj_3R_454()) jj_scanpos = xsp;
/* 11212 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_323() {
/* 11216 */     return jj_scan_token(122);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_112()
/*       */   {
/* 11222 */     Token xsp = jj_scanpos;
/* 11223 */     if (jj_3R_142()) jj_scanpos = xsp;
/* 11224 */     return jj_3R_138();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_465()
/*       */   {
/* 11229 */     return jj_3R_503();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_464()
/*       */   {
/* 11234 */     return jj_3R_501();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_463()
/*       */   {
/* 11239 */     return jj_3R_502();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_462()
/*       */   {
/* 11244 */     return jj_3R_507();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_461()
/*       */   {
/* 11249 */     return jj_3R_506();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_460()
/*       */   {
/* 11254 */     return jj_3R_497();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_459()
/*       */   {
/* 11259 */     return jj_3R_496();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_458()
/*       */   {
/* 11264 */     return jj_3R_500();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_457()
/*       */   {
/* 11269 */     return jj_3R_499();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_455()
/*       */   {
/* 11274 */     return jj_3R_116();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_456()
/*       */   {
/* 11279 */     return jj_3R_498();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_397()
/*       */   {
/* 11284 */     return jj_scan_token(120);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_362()
/*       */   {
/* 11290 */     Token xsp = jj_scanpos;
/* 11291 */     if (jj_3R_455()) {
/* 11292 */       jj_scanpos = xsp;
/* 11293 */       if (jj_3R_456()) {
/* 11294 */         jj_scanpos = xsp;
/* 11295 */         if (jj_3R_457()) {
/* 11296 */           jj_scanpos = xsp;
/* 11297 */           if (jj_3R_458()) {
/* 11298 */             jj_scanpos = xsp;
/* 11299 */             if (jj_3R_459()) {
/* 11300 */               jj_scanpos = xsp;
/* 11301 */               if (jj_3R_460()) {
/* 11302 */                 jj_scanpos = xsp;
/* 11303 */                 if (jj_3R_461()) {
/* 11304 */                   jj_scanpos = xsp;
/* 11305 */                   if (jj_3R_462()) {
/* 11306 */                     jj_scanpos = xsp;
/* 11307 */                     if (jj_3R_463()) {
/* 11308 */                       jj_scanpos = xsp;
/* 11309 */                       if (jj_3R_464()) {
/* 11310 */                         jj_scanpos = xsp;
/* 11311 */                         if (jj_3R_465()) return true;
/*       */                       }
/*       */                     }
/*       */                   }
/*       */                 }
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 11322 */     return jj_3R_130();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_130()
/*       */   {
/* 11327 */     if (jj_3R_251()) return true;
/*       */ 
/* 11329 */     Token xsp = jj_scanpos;
/* 11330 */     if (jj_3R_362()) jj_scanpos = xsp;
/* 11331 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_111() {
/* 11335 */     return jj_3R_130();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_369()
/*       */   {
/* 11340 */     return jj_scan_token(119);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_138()
/*       */   {
/* 11345 */     if (jj_3R_130()) return true; Token xsp;
/*       */     do
/* 11348 */       xsp = jj_scanpos;
/* 11349 */     while (!jj_3_112()); jj_scanpos = xsp;
/*       */ 
/* 11351 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_449() {
/* 11355 */     return jj_3R_522();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_360()
/*       */   {
/* 11360 */     if (jj_3R_252()) return true;
/*       */ 
/* 11362 */     Token xsp = jj_scanpos;
/* 11363 */     if (jj_3_111()) jj_scanpos = xsp;
/* 11364 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_582()
/*       */   {
/* 11369 */     return jj_3R_120();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_567()
/*       */   {
/* 11375 */     Token xsp = jj_scanpos;
/* 11376 */     if (jj_3_110()) {
/* 11377 */       jj_scanpos = xsp;
/* 11378 */       if (jj_3R_582()) return true;
/*       */     }
/* 11380 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_110() {
/* 11384 */     return jj_3R_121();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_558()
/*       */   {
/* 11389 */     if (jj_3R_568()) return true;
/* 11390 */     return jj_3R_356();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_109()
/*       */   {
/* 11395 */     return jj_3R_138();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_557()
/*       */   {
/* 11400 */     if (jj_3R_566()) return true;
/* 11401 */     if (jj_3R_156()) return true;
/* 11402 */     if (jj_3R_567()) return true;
/* 11403 */     if (jj_3R_157()) return true;
/* 11404 */     return jj_3R_356();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_522()
/*       */   {
/* 11410 */     Token xsp = jj_scanpos;
/* 11411 */     if (jj_3R_557()) {
/* 11412 */       jj_scanpos = xsp;
/* 11413 */       if (jj_3R_558()) return true;
/*       */     }
/* 11415 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_359() {
/* 11419 */     if (jj_3R_448()) return true;
/* 11420 */     if (jj_3R_356()) return true; Token xsp;
/*       */     do
/* 11423 */       xsp = jj_scanpos;
/* 11424 */     while (!jj_3R_449()); jj_scanpos = xsp;
/*       */ 
/* 11426 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_447() {
/* 11430 */     if (jj_3R_59()) return true; Token xsp;
/*       */     do
/* 11433 */       xsp = jj_scanpos;
/* 11434 */     while (!jj_3_109()); jj_scanpos = xsp;
/*       */ 
/* 11436 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_108()
/*       */   {
/* 11441 */     return jj_3R_138();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_446()
/*       */   {
/* 11446 */     if (jj_3R_521()) return true;
/* 11447 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_445()
/*       */   {
/* 11452 */     if (jj_3R_520()) return true;
/* 11453 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_528()
/*       */   {
/* 11458 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_59()
/*       */   {
/* 11463 */     return jj_scan_token(116);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_358()
/*       */   {
/* 11469 */     Token xsp = jj_scanpos;
/* 11470 */     if (jj_3R_444()) {
/* 11471 */       jj_scanpos = xsp;
/* 11472 */       if (jj_3R_445()) {
/* 11473 */         jj_scanpos = xsp;
/* 11474 */         if (jj_3R_446()) {
/* 11475 */           jj_scanpos = xsp;
/* 11476 */           if (jj_3R_447()) return true;
/*       */         }
/*       */       }
/*       */     }
/* 11480 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_444() {
/* 11484 */     if (jj_3R_519()) return true;
/* 11485 */     if (jj_3R_88()) return true;
/* 11486 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_105()
/*       */   {
/* 11491 */     return jj_3R_141();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_107()
/*       */   {
/* 11496 */     return jj_3R_138();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_396()
/*       */   {
/* 11501 */     return jj_scan_token(115);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_106()
/*       */   {
/* 11506 */     if (jj_3R_138()) return true;
/* 11507 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_452()
/*       */   {
/* 11512 */     if (jj_3R_527()) return true;
/* 11513 */     if (jj_3R_156()) return true;
/*       */ 
/* 11515 */     Token xsp = jj_scanpos;
/* 11516 */     if (jj_3_105()) {
/* 11517 */       jj_scanpos = xsp;
/* 11518 */       if (jj_3_106()) {
/* 11519 */         jj_scanpos = xsp;
/* 11520 */         if (jj_3R_528()) return true;
/*       */       }
/*       */     }
/* 11523 */     xsp = jj_scanpos;
/* 11524 */     if (jj_3_107()) jj_scanpos = xsp;
/* 11525 */     if (jj_3R_72()) return true;
/* 11526 */     xsp = jj_scanpos;
/* 11527 */     if (jj_3_108()) jj_scanpos = xsp;
/* 11528 */     if (jj_3R_157()) return true;
/* 11529 */     return jj_3R_137();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_451()
/*       */   {
/* 11534 */     if (jj_3R_526()) return true;
/* 11535 */     if (jj_3R_137()) return true;
/* 11536 */     if (jj_3R_525()) return true;
/* 11537 */     if (jj_3R_156()) return true;
/* 11538 */     if (jj_3R_138()) return true;
/* 11539 */     if (jj_3R_157()) return true;
/* 11540 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_348()
/*       */   {
/* 11545 */     return jj_scan_token(113);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_361()
/*       */   {
/* 11551 */     Token xsp = jj_scanpos;
/* 11552 */     if (jj_3R_450()) {
/* 11553 */       jj_scanpos = xsp;
/* 11554 */       if (jj_3R_451()) {
/* 11555 */         jj_scanpos = xsp;
/* 11556 */         if (jj_3R_452()) return true;
/*       */       }
/*       */     }
/* 11559 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_450() {
/* 11563 */     if (jj_3R_525()) return true;
/* 11564 */     if (jj_3R_156()) return true;
/* 11565 */     if (jj_3R_138()) return true;
/* 11566 */     if (jj_3R_157()) return true;
/* 11567 */     return jj_3R_137();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_349()
/*       */   {
/* 11572 */     return jj_scan_token(112);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_443()
/*       */   {
/* 11577 */     if (jj_3R_518()) return true;
/* 11578 */     if (jj_3R_156()) return true;
/* 11579 */     if (jj_3R_138()) return true;
/* 11580 */     if (jj_3R_157()) return true;
/* 11581 */     return jj_3R_137();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_350()
/*       */   {
/* 11586 */     return jj_scan_token(111);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_517()
/*       */   {
/* 11591 */     if (jj_3R_556()) return true;
/* 11592 */     return jj_3R_137();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_178()
/*       */   {
/* 11597 */     return jj_scan_token(110);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_357()
/*       */   {
/* 11603 */     Token xsp = jj_scanpos;
/* 11604 */     if (jj_3R_442()) {
/* 11605 */       jj_scanpos = xsp;
/* 11606 */       if (jj_3R_443()) return true;
/*       */     }
/* 11608 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_442() {
/* 11612 */     if (jj_3R_516()) return true;
/* 11613 */     if (jj_3R_156()) return true;
/* 11614 */     if (jj_3R_138()) return true;
/* 11615 */     if (jj_3R_157()) return true;
/* 11616 */     if (jj_3R_137()) return true;
/*       */ 
/* 11618 */     Token xsp = jj_scanpos;
/* 11619 */     if (jj_3R_517()) jj_scanpos = xsp;
/* 11620 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_104() {
/* 11624 */     return jj_3R_140();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_523()
/*       */   {
/* 11629 */     return jj_3R_358();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_356()
/*       */   {
/* 11634 */     if (jj_3R_62()) return true;
/*       */ 
/* 11636 */     Token xsp = jj_scanpos;
/* 11637 */     if (jj_3_104()) jj_scanpos = xsp;
/* 11638 */     return jj_3R_127();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_524()
/*       */   {
/* 11643 */     return jj_3R_358();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_344()
/*       */   {
/* 11648 */     return jj_scan_token(107);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_516()
/*       */   {
/* 11653 */     return jj_scan_token(106);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_269()
/*       */   {
/* 11658 */     if (jj_3R_364()) return true;
/* 11659 */     if (jj_3R_105()) return true;
/* 11660 */     if (jj_3R_137()) return true; Token xsp;
/*       */     do
/* 11663 */       xsp = jj_scanpos;
/* 11664 */     while (!jj_3R_524()); jj_scanpos = xsp;
/*       */ 
/* 11666 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_268() {
/* 11670 */     if (jj_3R_363()) return true;
/* 11671 */     if (jj_3R_115()) return true;
/* 11672 */     if (jj_3R_105()) return true;
/* 11673 */     if (jj_3R_137()) return true; Token xsp;
/*       */     do
/* 11676 */       xsp = jj_scanpos;
/* 11677 */     while (!jj_3R_523()); jj_scanpos = xsp;
/*       */ 
/* 11679 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_519() {
/* 11683 */     return jj_scan_token(105);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_139()
/*       */   {
/* 11689 */     Token xsp = jj_scanpos;
/* 11690 */     if (jj_3R_267()) {
/* 11691 */       jj_scanpos = xsp;
/* 11692 */       if (jj_3R_268()) {
/* 11693 */         jj_scanpos = xsp;
/* 11694 */         if (jj_3R_269()) return true;
/*       */       }
/*       */     }
/* 11697 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_267() {
/* 11701 */     if (jj_3R_88()) return true;
/* 11702 */     if (jj_3R_105()) return true;
/* 11703 */     return jj_3R_137();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_266()
/*       */   {
/* 11708 */     return jj_3R_361();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_103()
/*       */   {
/* 11713 */     return jj_3R_139();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_265()
/*       */   {
/* 11718 */     return jj_3R_360();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_264()
/*       */   {
/* 11723 */     return jj_3R_359();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_263()
/*       */   {
/* 11728 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_262()
/*       */   {
/* 11733 */     return jj_3R_358();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_102()
/*       */   {
/* 11738 */     if (jj_3R_138()) return true;
/* 11739 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_261()
/*       */   {
/* 11744 */     return jj_3R_357();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_101()
/*       */   {
/* 11749 */     return jj_3R_70();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_527()
/*       */   {
/* 11754 */     return jj_scan_token(103);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_260()
/*       */   {
/* 11759 */     return jj_3R_356();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_259()
/*       */   {
/* 11764 */     if (jj_3R_138()) return true;
/* 11765 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_137()
/*       */   {
/* 11771 */     Token xsp = jj_scanpos;
/* 11772 */     if (jj_3R_258()) {
/* 11773 */       jj_scanpos = xsp;
/* 11774 */       if (jj_3R_259()) {
/* 11775 */         jj_scanpos = xsp;
/* 11776 */         if (jj_3R_260()) {
/* 11777 */           jj_scanpos = xsp;
/* 11778 */           if (jj_3R_261()) {
/* 11779 */             jj_scanpos = xsp;
/* 11780 */             if (jj_3R_262()) {
/* 11781 */               jj_scanpos = xsp;
/* 11782 */               if (jj_3R_263()) {
/* 11783 */                 jj_scanpos = xsp;
/* 11784 */                 if (jj_3R_264()) {
/* 11785 */                   jj_scanpos = xsp;
/* 11786 */                   if (jj_3R_265()) {
/* 11787 */                     jj_scanpos = xsp;
/* 11788 */                     if (jj_3_103()) {
/* 11789 */                       jj_scanpos = xsp;
/* 11790 */                       if (jj_3R_266()) return true;
/*       */                     }
/*       */                   }
/*       */                 }
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 11800 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_258() {
/* 11804 */     return jj_3R_70();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_100()
/*       */   {
/* 11809 */     return jj_3R_137();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_493()
/*       */   {
/* 11814 */     if (jj_3R_122()) return true;
/* 11815 */     return jj_3R_231();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_270()
/*       */   {
/* 11820 */     return jj_3R_137();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_140()
/*       */   {
/* 11826 */     if (jj_3R_270()) return true; Token xsp;
/*       */     do xsp = jj_scanpos;
/* 11829 */     while (!jj_3R_270()); jj_scanpos = xsp;
/*       */ 
/* 11831 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_568() {
/* 11835 */     return jj_scan_token(101);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_99()
/*       */   {
/* 11840 */     return jj_3R_136();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_601()
/*       */   {
/* 11845 */     if (jj_3R_122()) return true;
/* 11846 */     return jj_3R_115();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_98()
/*       */   {
/* 11851 */     return jj_3R_135();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_231()
/*       */   {
/* 11857 */     Token xsp = jj_scanpos;
/* 11858 */     if (jj_3_98()) {
/* 11859 */       jj_scanpos = xsp;
/* 11860 */       if (jj_3_99()) return true;
/*       */     }
/* 11862 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_390() {
/* 11866 */     return jj_scan_token(100);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_389()
/*       */   {
/* 11871 */     if (jj_3R_122()) return true;
/* 11872 */     return jj_3R_388();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_575()
/*       */   {
/* 11877 */     if (jj_3R_122()) return true;
/* 11878 */     return jj_3R_115();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_96()
/*       */   {
/* 11883 */     if (jj_3R_231()) return true; Token xsp;
/*       */     do
/* 11886 */       xsp = jj_scanpos;
/* 11887 */     while (!jj_3R_493()); jj_scanpos = xsp;
/*       */ 
/* 11889 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_97() {
/* 11893 */     return jj_3R_123();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_63()
/*       */   {
/* 11898 */     return jj_scan_token(99);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_96()
/*       */   {
/* 11903 */     if (jj_3R_134()) return true;
/* 11904 */     return jj_3R_88();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_388()
/*       */   {
/* 11910 */     Token xsp = jj_scanpos;
/* 11911 */     if (jj_3_96()) {
/* 11912 */       jj_scanpos = xsp;
/* 11913 */       if (jj_3_97()) return true;
/*       */     }
/* 11915 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_94() {
/* 11919 */     if (jj_3R_115()) return true; Token xsp;
/*       */     do
/* 11922 */       xsp = jj_scanpos;
/* 11923 */     while (!jj_3R_601()); jj_scanpos = xsp;
/*       */ 
/* 11925 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_95() {
/* 11929 */     return jj_3R_106();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_327()
/*       */   {
/* 11934 */     if (jj_3R_388()) return true; Token xsp;
/*       */     do
/* 11937 */       xsp = jj_scanpos;
/* 11938 */     while (!jj_3R_389()); jj_scanpos = xsp;
/*       */ 
/* 11940 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_92() {
/* 11944 */     if (jj_3R_115()) return true; Token xsp;
/*       */     do
/* 11947 */       xsp = jj_scanpos;
/* 11948 */     while (!jj_3R_575()); jj_scanpos = xsp;
/*       */ 
/* 11950 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_202() {
/* 11954 */     if (jj_3R_326()) return true;
/* 11955 */     if (jj_3R_95()) return true;
/* 11956 */     if (jj_3R_327()) return true;
/* 11957 */     return jj_3R_273();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_556()
/*       */   {
/* 11962 */     return jj_scan_token(98);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_574()
/*       */   {
/* 11967 */     if (jj_3R_156()) return true;
/*       */ 
/* 11969 */     Token xsp = jj_scanpos;
/* 11970 */     if (jj_3_95()) jj_scanpos = xsp;
/* 11971 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_573()
/*       */   {
/* 11976 */     if (jj_3R_168()) return true;
/*       */ 
/* 11978 */     Token xsp = jj_scanpos;
/* 11979 */     if (jj_3_94()) jj_scanpos = xsp;
/* 11980 */     return jj_3R_181();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_560()
/*       */   {
/* 11986 */     Token xsp = jj_scanpos;
/* 11987 */     if (jj_3R_573()) {
/* 11988 */       jj_scanpos = xsp;
/* 11989 */       if (jj_3R_574()) return true;
/*       */     }
/* 11991 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_133() {
/* 11995 */     if (jj_3R_113()) return true;
/* 11996 */     return jj_3R_255();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_126()
/*       */   {
/* 12001 */     if (jj_3R_122()) return true;
/* 12002 */     return jj_3R_125();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_256()
/*       */   {
/* 12007 */     if (jj_3R_168()) return true;
/*       */ 
/* 12009 */     Token xsp = jj_scanpos;
/* 12010 */     if (jj_3_92()) jj_scanpos = xsp;
/* 12011 */     return jj_3R_181();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_132()
/*       */   {
/* 12017 */     if (jj_3R_256()) return true; Token xsp;
/*       */     do xsp = jj_scanpos;
/* 12020 */     while (!jj_3R_256()); jj_scanpos = xsp;
/*       */ 
/* 12022 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_532() {
/* 12026 */     return jj_3R_560();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_131()
/*       */   {
/* 12031 */     if (jj_3R_156()) return true;
/* 12032 */     if (jj_3R_255()) return true;
/* 12033 */     if (jj_3R_157()) return true;
/*       */ 
/* 12035 */     if (jj_3R_532()) return true; Token xsp;
/*       */     do xsp = jj_scanpos;
/* 12038 */     while (!jj_3R_532()); jj_scanpos = xsp;
/*       */ 
/* 12040 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_93()
/*       */   {
/* 12045 */     Token xsp = jj_scanpos;
/* 12046 */     if (jj_3R_131()) {
/* 12047 */       jj_scanpos = xsp;
/* 12048 */       if (jj_3R_132()) {
/* 12049 */         jj_scanpos = xsp;
/* 12050 */         if (jj_3R_133()) return true;
/*       */       }
/*       */     }
/* 12053 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_255()
/*       */   {
/* 12058 */     Token xsp = jj_scanpos;
/* 12059 */     if (jj_3_93()) jj_scanpos = xsp;
/* 12060 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_135() {
/* 12064 */     if (jj_3R_58()) return true;
/* 12065 */     return jj_3R_255();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_526()
/*       */   {
/* 12070 */     return jj_scan_token(96);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_119()
/*       */   {
/* 12075 */     return jj_3R_122();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_91()
/*       */   {
/* 12080 */     return jj_3R_130();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_90()
/*       */   {
/* 12085 */     return jj_3R_129();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_89()
/*       */   {
/* 12090 */     return jj_3R_128();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_180()
/*       */   {
/* 12095 */     return jj_scan_token(95);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_125()
/*       */   {
/* 12101 */     Token xsp = jj_scanpos;
/* 12102 */     if (jj_3_88()) {
/* 12103 */       jj_scanpos = xsp;
/* 12104 */       if (jj_3_89()) {
/* 12105 */         jj_scanpos = xsp;
/* 12106 */         if (jj_3_90()) {
/* 12107 */           jj_scanpos = xsp;
/* 12108 */           if (jj_3_91()) return true;
/*       */         }
/*       */       }
/*       */     }
/* 12112 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_86() {
/* 12116 */     if (jj_3R_122()) return true;
/* 12117 */     return jj_3R_123();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_88()
/*       */   {
/* 12122 */     if (jj_3R_62()) return true;
/* 12123 */     if (jj_3R_125()) return true; Token xsp;
/*       */     do
/* 12126 */       xsp = jj_scanpos;
/* 12127 */     while (!jj_3R_126()); jj_scanpos = xsp;
/*       */ 
/* 12129 */     return jj_3R_127();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_84()
/*       */   {
/* 12135 */     Token xsp = jj_scanpos;
/* 12136 */     if (jj_3R_119()) jj_scanpos = xsp;
/* 12137 */     return jj_3R_120();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_87()
/*       */   {
/* 12142 */     return jj_3R_124();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_83()
/*       */   {
/* 12147 */     return jj_3R_106();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_563()
/*       */   {
/* 12152 */     if (jj_3R_116()) return true;
/* 12153 */     return jj_3R_130();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_515()
/*       */   {
/* 12158 */     return jj_3R_255();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_364()
/*       */   {
/* 12163 */     return jj_scan_token(94);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_514()
/*       */   {
/* 12168 */     return jj_3R_124();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_123()
/*       */   {
/* 12173 */     if (jj_3R_58()) return true;
/*       */ 
/* 12175 */     Token xsp = jj_scanpos;
/* 12176 */     if (jj_3R_514()) {
/* 12177 */       jj_scanpos = xsp;
/* 12178 */       if (jj_3R_515()) return true;
/*       */     }
/* 12180 */     xsp = jj_scanpos;
/* 12181 */     if (jj_3R_563()) jj_scanpos = xsp;
/* 12182 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_520() {
/* 12186 */     return jj_scan_token(93);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_121()
/*       */   {
/* 12191 */     if (jj_3R_123()) return true; Token xsp;
/*       */     do
/* 12194 */       xsp = jj_scanpos;
/* 12195 */     while (!jj_3_86()); jj_scanpos = xsp;
/*       */ 
/* 12197 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_242() {
/* 12201 */     return jj_3R_120();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_106()
/*       */   {
/* 12207 */     Token xsp = jj_scanpos;
/* 12208 */     if (jj_3_85()) {
/* 12209 */       jj_scanpos = xsp;
/* 12210 */       if (jj_3R_242()) return true;
/*       */     }
/* 12212 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_85() {
/* 12216 */     if (jj_3R_121()) return true;
/*       */ 
/* 12218 */     Token xsp = jj_scanpos;
/* 12219 */     if (jj_3_84()) jj_scanpos = xsp;
/* 12220 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_110() {
/* 12224 */     return jj_scan_token(92);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_81()
/*       */   {
/* 12229 */     return jj_3R_118();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_328()
/*       */   {
/* 12234 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_204() {
/* 12238 */     if (jj_3R_101()) return true;
/* 12239 */     jj_lookingAhead = true;
/* 12240 */     jj_semLA = isCtor();
/* 12241 */     jj_lookingAhead = false;
/* 12242 */     if ((!jj_semLA) || (jj_3R_328())) return true;
/* 12243 */     if (jj_3R_88()) return true;
/* 12244 */     if (jj_3R_156()) return true;
/*       */ 
/* 12246 */     Token xsp = jj_scanpos;
/* 12247 */     if (jj_3_83()) jj_scanpos = xsp;
/* 12248 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_613()
/*       */   {
/* 12253 */     if (jj_3R_122()) return true;
/* 12254 */     return jj_3R_612();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_82()
/*       */   {
/* 12259 */     return jj_3R_92();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_566()
/*       */   {
/* 12264 */     return jj_scan_token(90);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_203()
/*       */   {
/* 12269 */     return jj_3R_205();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_66()
/*       */   {
/* 12275 */     Token xsp = jj_scanpos;
/* 12276 */     if (jj_3R_203()) jj_scanpos = xsp;
/* 12277 */     return jj_3R_204();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_363()
/*       */   {
/* 12282 */     return jj_scan_token(89);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_612()
/*       */   {
/* 12287 */     if (jj_3R_103()) return true;
/* 12288 */     if (jj_3R_156()) return true;
/*       */ 
/* 12290 */     Token xsp = jj_scanpos;
/* 12291 */     if (jj_3_81()) jj_scanpos = xsp;
/* 12292 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_79()
/*       */   {
/* 12297 */     return jj_3R_106();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_605()
/*       */   {
/* 12302 */     if (jj_3R_105()) return true;
/* 12303 */     if (jj_3R_612()) return true; Token xsp;
/*       */     do
/* 12306 */       xsp = jj_scanpos;
/* 12307 */     while (!jj_3R_613()); jj_scanpos = xsp;
/*       */ 
/* 12309 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_521() {
/* 12313 */     return jj_scan_token(86);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_80()
/*       */   {
/* 12318 */     return jj_3R_117();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_398()
/*       */   {
/* 12323 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_395() {
/* 12327 */     return jj_scan_token(85);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_194()
/*       */   {
/* 12332 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_343() {
/* 12336 */     jj_lookingAhead = true;
/* 12337 */     jj_semLA = isCtor();
/* 12338 */     jj_lookingAhead = false;
/* 12339 */     if ((!jj_semLA) || (jj_3R_398())) return true;
/* 12340 */     if (jj_3R_103()) return true;
/* 12341 */     if (jj_3R_156()) return true;
/*       */ 
/* 12343 */     Token xsp = jj_scanpos;
/* 12344 */     if (jj_3_79()) jj_scanpos = xsp;
/* 12345 */     if (jj_3R_157()) return true;
/* 12346 */     xsp = jj_scanpos;
/* 12347 */     if (jj_3_80()) jj_scanpos = xsp;
/* 12348 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_512() {
/* 12352 */     return jj_scan_token(84);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_55()
/*       */   {
/* 12357 */     jj_lookingAhead = true;
/* 12358 */     jj_semLA = isCtor();
/* 12359 */     jj_lookingAhead = false;
/* 12360 */     if ((!jj_semLA) || (jj_3R_194())) return true;
/* 12361 */     if (jj_3R_103()) return true;
/* 12362 */     return jj_3R_156();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_596()
/*       */   {
/* 12367 */     return jj_3R_605();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_585()
/*       */   {
/* 12373 */     Token xsp = jj_scanpos;
/* 12374 */     if (jj_3R_596()) jj_scanpos = xsp;
/* 12375 */     return jj_3R_356();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_621()
/*       */   {
/* 12380 */     return jj_scan_token(83);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_584()
/*       */   {
/* 12385 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_583()
/*       */   {
/* 12390 */     return jj_3R_117();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_387()
/*       */   {
/* 12395 */     return jj_3R_345();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_318()
/*       */   {
/* 12400 */     if (jj_3R_54()) return true;
/* 12401 */     if (jj_3R_343()) return true;
/*       */ 
/* 12403 */     Token xsp = jj_scanpos;
/* 12404 */     if (jj_3R_583()) jj_scanpos = xsp;
/* 12405 */     xsp = jj_scanpos;
/* 12406 */     if (jj_3R_584()) {
/* 12407 */       jj_scanpos = xsp;
/* 12408 */       if (jj_3R_585()) return true;
/*       */     }
/* 12410 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_386() {
/* 12414 */     return jj_3R_344();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_305()
/*       */   {
/* 12419 */     return jj_scan_token(82);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_383()
/*       */   {
/* 12424 */     return jj_3R_202();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_317()
/*       */   {
/* 12430 */     Token xsp = jj_scanpos;
/* 12431 */     if (jj_3R_383()) jj_scanpos = xsp;
/* 12432 */     if (jj_3R_54()) return true;
/* 12433 */     if (jj_3R_66()) return true;
/* 12434 */     return jj_3R_356();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_78()
/*       */   {
/* 12439 */     return jj_3R_116();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_322()
/*       */   {
/* 12444 */     if (jj_3R_344()) return true;
/*       */ 
/* 12446 */     Token xsp = jj_scanpos;
/* 12447 */     if (jj_3R_387()) jj_scanpos = xsp;
/* 12448 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_76() {
/* 12452 */     return jj_3R_106();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_193()
/*       */   {
/* 12458 */     Token xsp = jj_scanpos;
/* 12459 */     if (jj_3R_321()) {
/* 12460 */       jj_scanpos = xsp;
/* 12461 */       if (jj_3R_322()) return true;
/*       */     }
/* 12463 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_321() {
/* 12467 */     if (jj_3R_345()) return true;
/*       */ 
/* 12469 */     Token xsp = jj_scanpos;
/* 12470 */     if (jj_3R_386()) jj_scanpos = xsp;
/* 12471 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_303() {
/* 12475 */     return jj_scan_token(81);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_54()
/*       */   {
/* 12481 */     Token xsp = jj_scanpos;
/* 12482 */     if (jj_3R_193()) jj_scanpos = xsp;
/* 12483 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_620() {
/* 12487 */     if (jj_3R_116()) return true;
/* 12488 */     return jj_scan_token(145);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_619()
/*       */   {
/* 12493 */     return jj_3R_117();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_77()
/*       */   {
/* 12498 */     return jj_3R_107();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_370()
/*       */   {
/* 12503 */     return jj_scan_token(80);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_603()
/*       */   {
/* 12508 */     if (jj_3R_122()) return true;
/* 12509 */     return jj_3R_115();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_332()
/*       */   {
/* 12514 */     if (jj_3R_103()) return true;
/* 12515 */     if (jj_3R_156()) return true;
/*       */ 
/* 12517 */     Token xsp = jj_scanpos;
/* 12518 */     if (jj_3_76()) jj_scanpos = xsp;
/* 12519 */     if (jj_3R_157()) return true;
/* 12520 */     xsp = jj_scanpos;
/* 12521 */     if (jj_3_77()) jj_scanpos = xsp;
/* 12522 */     xsp = jj_scanpos;
/* 12523 */     if (jj_3R_619()) jj_scanpos = xsp;
/* 12524 */     xsp = jj_scanpos;
/* 12525 */     if (jj_3R_620()) jj_scanpos = xsp;
/* 12526 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_101() {
/* 12530 */     return jj_scan_token(79);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_75()
/*       */   {
/* 12535 */     return jj_3R_113();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_68()
/*       */   {
/* 12540 */     return jj_3R_114();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_208()
/*       */   {
/* 12545 */     return jj_3R_332();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_207()
/*       */   {
/* 12550 */     if (jj_3R_113()) return true;
/* 12551 */     return jj_3R_71();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_71()
/*       */   {
/* 12556 */     if (jj_3R_115()) return true; Token xsp;
/*       */     do
/* 12559 */       xsp = jj_scanpos;
/* 12560 */     while (!jj_3R_603()); jj_scanpos = xsp;
/*       */ 
/* 12562 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_307() {
/* 12566 */     return jj_scan_token(78);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_71()
/*       */   {
/* 12572 */     Token xsp = jj_scanpos;
/* 12573 */     if (jj_3R_207()) {
/* 12574 */       jj_scanpos = xsp;
/* 12575 */       if (jj_3R_208()) return true;
/*       */     }
/* 12577 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_72() {
/* 12581 */     return jj_3R_106();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_74()
/*       */   {
/* 12586 */     return jj_3R_113();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_57() {
/*       */     Token xsp;
/*       */     do
/* 12593 */       xsp = jj_scanpos;
/* 12594 */     while (!jj_3_74()); jj_scanpos = xsp;
/*       */ 
/* 12596 */     if (jj_3R_103()) return true;
/* 12597 */     return jj_3R_156();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_306()
/*       */   {
/* 12602 */     return jj_scan_token(77);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_69()
/*       */   {
/* 12607 */     return jj_3R_114();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_581()
/*       */   {
/* 12612 */     return jj_3R_117();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_73()
/*       */   {
/* 12617 */     return jj_3R_107();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_250()
/*       */   {
/* 12622 */     if (jj_3R_156()) return true;
/*       */ 
/* 12624 */     Token xsp = jj_scanpos;
/* 12625 */     if (jj_3_72()) jj_scanpos = xsp;
/* 12626 */     if (jj_3R_157()) return true;
/* 12627 */     xsp = jj_scanpos;
/* 12628 */     if (jj_3_73()) jj_scanpos = xsp;
/* 12629 */     xsp = jj_scanpos;
/* 12630 */     if (jj_3R_581()) jj_scanpos = xsp;
/* 12631 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_353() {
/* 12635 */     if (jj_3R_168()) return true;
/*       */ 
/* 12637 */     Token xsp = jj_scanpos;
/* 12638 */     if (jj_3_71()) jj_scanpos = xsp;
/* 12639 */     return jj_3R_181();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_114()
/*       */   {
/* 12645 */     Token xsp = jj_scanpos;
/* 12646 */     if (jj_3R_249()) {
/* 12647 */       jj_scanpos = xsp;
/* 12648 */       if (jj_3R_250()) return true;
/*       */     }
/* 12650 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_249()
/*       */   {
/* 12655 */     if (jj_3R_353()) return true; Token xsp;
/*       */     do xsp = jj_scanpos;
/* 12658 */     while (!jj_3R_353()); jj_scanpos = xsp;
/*       */ 
/* 12660 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_67() {
/* 12664 */     return jj_3R_114();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_280()
/*       */   {
/* 12669 */     return jj_scan_token(76);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_441()
/*       */   {
/* 12674 */     if (jj_3R_103()) return true;
/*       */ 
/* 12676 */     Token xsp = jj_scanpos;
/* 12677 */     if (jj_3_69()) jj_scanpos = xsp;
/* 12678 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_440() {
/* 12682 */     if (jj_3R_156()) return true;
/* 12683 */     if (jj_3R_124()) return true;
/* 12684 */     if (jj_3R_157()) return true;
/*       */ 
/* 12686 */     Token xsp = jj_scanpos;
/* 12687 */     if (jj_3_68()) jj_scanpos = xsp;
/* 12688 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_279() {
/* 12692 */     return jj_scan_token(75);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_70()
/*       */   {
/* 12697 */     if (jj_3R_101()) return true;
/* 12698 */     if (jj_3R_88()) return true;
/*       */ 
/* 12700 */     Token xsp = jj_scanpos;
/* 12701 */     if (jj_3_67()) jj_scanpos = xsp;
/* 12702 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_355()
/*       */   {
/* 12707 */     Token xsp = jj_scanpos;
/* 12708 */     if (jj_3_70()) {
/* 12709 */       jj_scanpos = xsp;
/* 12710 */       if (jj_3R_440()) {
/* 12711 */         jj_scanpos = xsp;
/* 12712 */         if (jj_3R_441()) return true;
/*       */       }
/*       */     }
/* 12715 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_64() {
/* 12719 */     return jj_3R_110();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_63()
/*       */   {
/* 12724 */     return jj_3R_109();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_278()
/*       */   {
/* 12729 */     return jj_scan_token(74);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_66()
/*       */   {
/* 12734 */     return jj_3R_113();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_254()
/*       */   {
/* 12739 */     return jj_3R_355();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_253()
/*       */   {
/* 12744 */     if (jj_3R_113()) return true;
/* 12745 */     return jj_3R_124();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_277()
/*       */   {
/* 12750 */     return jj_scan_token(73);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_112()
/*       */   {
/* 12755 */     if (jj_3R_109()) return true;
/*       */ 
/* 12757 */     Token xsp = jj_scanpos;
/* 12758 */     if (jj_3_64()) jj_scanpos = xsp;
/* 12759 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_124()
/*       */   {
/* 12764 */     Token xsp = jj_scanpos;
/* 12765 */     if (jj_3R_253()) {
/* 12766 */       jj_scanpos = xsp;
/* 12767 */       if (jj_3R_254()) return true;
/*       */     }
/* 12769 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_111() {
/* 12773 */     if (jj_3R_110()) return true;
/*       */ 
/* 12775 */     Token xsp = jj_scanpos;
/* 12776 */     if (jj_3_63()) jj_scanpos = xsp;
/* 12777 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_65()
/*       */   {
/* 12782 */     Token xsp = jj_scanpos;
/* 12783 */     if (jj_3R_111()) {
/* 12784 */       jj_scanpos = xsp;
/* 12785 */       if (jj_3R_112()) return true;
/*       */     }
/* 12787 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_351()
/*       */   {
/* 12792 */     Token xsp = jj_scanpos;
/* 12793 */     if (jj_3_65()) jj_scanpos = xsp;
/* 12794 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_276() {
/* 12798 */     return jj_scan_token(72);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_248()
/*       */   {
/* 12803 */     if (jj_3R_352()) return true;
/* 12804 */     return jj_3R_351();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_564()
/*       */   {
/* 12809 */     if (jj_3R_122()) return true;
/* 12810 */     return jj_3R_245();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_247()
/*       */   {
/* 12815 */     if (jj_3R_278()) return true;
/* 12816 */     return jj_3R_351();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_580()
/*       */   {
/* 12821 */     if (jj_3R_116()) return true;
/* 12822 */     return jj_3R_115();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_113()
/*       */   {
/* 12828 */     Token xsp = jj_scanpos;
/* 12829 */     if (jj_3R_246()) {
/* 12830 */       jj_scanpos = xsp;
/* 12831 */       if (jj_3R_247()) {
/* 12832 */         jj_scanpos = xsp;
/* 12833 */         if (jj_3R_248()) return true;
/*       */       }
/*       */     }
/* 12836 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_246() {
/* 12840 */     if (jj_3R_143()) return true;
/* 12841 */     return jj_3R_351();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_245()
/*       */   {
/* 12846 */     if (jj_3R_88()) return true;
/*       */ 
/* 12848 */     Token xsp = jj_scanpos;
/* 12849 */     if (jj_3R_580()) jj_scanpos = xsp;
/* 12850 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_505() {
/* 12854 */     return jj_scan_token(71);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_59()
/*       */   {
/* 12859 */     return jj_3R_106();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_62()
/*       */   {
/* 12864 */     if (jj_3R_62()) return true;
/* 12865 */     if (jj_3R_108()) return true;
/* 12866 */     return jj_3R_127();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_108()
/*       */   {
/* 12871 */     if (jj_3R_245()) return true; Token xsp;
/*       */     do
/* 12874 */       xsp = jj_scanpos;
/* 12875 */     while (!jj_3R_564()); jj_scanpos = xsp;
/*       */ 
/* 12877 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_61() {
/* 12881 */     if (jj_3R_62()) return true;
/* 12882 */     return jj_3R_108();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_382()
/*       */   {
/* 12887 */     if (jj_3R_88()) return true;
/*       */ 
/* 12889 */     Token xsp = jj_scanpos;
/* 12890 */     if (jj_3_62()) jj_scanpos = xsp;
/* 12891 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_504() {
/* 12895 */     return jj_scan_token(70);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_381()
/*       */   {
/* 12900 */     if (jj_3R_62()) return true;
/* 12901 */     if (jj_3R_108()) return true;
/* 12902 */     return jj_3R_127();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_213()
/*       */   {
/* 12907 */     if (jj_3R_63()) return true;
/*       */ 
/* 12909 */     Token xsp = jj_scanpos;
/* 12910 */     if (jj_3R_381()) {
/* 12911 */       jj_scanpos = xsp;
/* 12912 */       if (jj_3R_382()) return true;
/*       */     }
/* 12914 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_275() {
/* 12918 */     return jj_scan_token(69);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_598()
/*       */   {
/* 12923 */     return jj_3R_143();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_587()
/*       */   {
/* 12928 */     return jj_3R_117();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_60()
/*       */   {
/* 12933 */     return jj_3R_107();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_274()
/*       */   {
/* 12938 */     return jj_scan_token(68);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_597()
/*       */   {
/* 12943 */     return jj_3R_278();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_586()
/*       */   {
/* 12949 */     Token xsp = jj_scanpos;
/* 12950 */     if (jj_3R_597()) {
/* 12951 */       jj_scanpos = xsp;
/* 12952 */       if (jj_3R_598()) return true;
/*       */     }
/* 12954 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_273() {
/* 12958 */     return jj_scan_token(67);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_58()
/*       */   {
/* 12963 */     return jj_3R_92();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_618()
/*       */   {
/* 12968 */     if (jj_3R_116()) return true;
/* 12969 */     return jj_3R_130();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_385()
/*       */   {
/* 12974 */     return jj_3R_205();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_609()
/*       */   {
/* 12979 */     if (jj_3R_116()) return true;
/* 12980 */     return jj_3R_130();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_320()
/*       */   {
/* 12986 */     Token xsp = jj_scanpos;
/* 12987 */     if (jj_3R_385()) jj_scanpos = xsp;
/* 12988 */     if (jj_scan_token(135)) return true;
/* 12989 */     if (jj_3R_58()) return true;
/* 12990 */     xsp = jj_scanpos;
/* 12991 */     if (jj_3R_586()) jj_scanpos = xsp;
/* 12992 */     if (jj_3R_156()) return true;
/* 12993 */     xsp = jj_scanpos;
/* 12994 */     if (jj_3_59()) jj_scanpos = xsp;
/* 12995 */     if (jj_3R_157()) return true;
/* 12996 */     xsp = jj_scanpos;
/* 12997 */     if (jj_3_60()) jj_scanpos = xsp;
/* 12998 */     xsp = jj_scanpos;
/* 12999 */     if (jj_3R_587()) jj_scanpos = xsp;
/* 13000 */     return jj_3R_588();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_95()
/*       */   {
/* 13005 */     return jj_scan_token(66);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_509()
/*       */   {
/* 13010 */     return jj_scan_token(65);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_608()
/*       */   {
/* 13015 */     return jj_3R_124();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_508()
/*       */   {
/* 13020 */     return jj_scan_token(64);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_610()
/*       */   {
/* 13025 */     if (jj_3R_122()) return true;
/* 13026 */     if (jj_3R_608()) return true;
/*       */ 
/* 13028 */     Token xsp = jj_scanpos;
/* 13029 */     if (jj_3R_618()) jj_scanpos = xsp;
/* 13030 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_143() {
/* 13034 */     return jj_scan_token(63);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_602()
/*       */   {
/* 13039 */     if (jj_3R_608()) return true;
/*       */ 
/* 13041 */     Token xsp = jj_scanpos;
/* 13042 */     if (jj_3R_609()) jj_scanpos = xsp;
/*       */     do
/* 13044 */       xsp = jj_scanpos;
/* 13045 */     while (!jj_3R_610()); jj_scanpos = xsp;
/*       */ 
/* 13047 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_494() {
/* 13051 */     return jj_scan_token(62);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_230()
/*       */   {
/* 13056 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_55()
/*       */   {
/* 13061 */     return jj_3R_88();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_57()
/*       */   {
/* 13066 */     if (jj_3R_104()) return true;
/* 13067 */     return jj_3R_105();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_495()
/*       */   {
/* 13072 */     return jj_scan_token(61);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_56()
/*       */   {
/* 13077 */     if (jj_3R_103()) return true;
/* 13078 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_54()
/*       */   {
/* 13083 */     return jj_3R_58();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_229()
/*       */   {
/* 13088 */     if (jj_3R_71()) return true;
/* 13089 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_102()
/*       */   {
/* 13094 */     return jj_3R_58();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_235()
/*       */   {
/* 13099 */     return jj_3R_345();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_53()
/*       */   {
/* 13105 */     Token xsp = jj_scanpos;
/* 13106 */     if (jj_3R_102()) jj_scanpos = xsp;
/* 13107 */     return jj_3R_57();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_595()
/*       */   {
/* 13112 */     return jj_3R_602();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_510()
/*       */   {
/* 13117 */     return jj_scan_token(60);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_52()
/*       */   {
/* 13122 */     if (jj_3R_54()) return true;
/* 13123 */     if (jj_3R_55()) return true;
/* 13124 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_228()
/*       */   {
/* 13129 */     if (jj_3R_58()) return true;
/*       */ 
/* 13131 */     Token xsp = jj_scanpos;
/* 13132 */     if (jj_3R_595()) jj_scanpos = xsp;
/* 13133 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_51()
/*       */   {
/* 13138 */     if (jj_3R_54()) return true;
/* 13139 */     return jj_3R_55();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_227()
/*       */   {
/* 13144 */     return jj_3R_319();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_99()
/*       */   {
/* 13149 */     return jj_3R_88();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_100()
/*       */   {
/* 13155 */     Token xsp = jj_scanpos;
/* 13156 */     if (jj_3R_234()) {
/* 13157 */       jj_scanpos = xsp;
/* 13158 */       if (jj_3R_235()) return true;
/*       */     }
/* 13160 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_234() {
/* 13164 */     return jj_3R_344();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_511()
/*       */   {
/* 13169 */     return jj_scan_token(59);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_50() {
/*       */     Token xsp;
/*       */     do
/* 13176 */       xsp = jj_scanpos;
/* 13177 */     while (!jj_3R_100()); jj_scanpos = xsp;
/*       */ 
/* 13179 */     return jj_3R_101();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_226()
/*       */   {
/* 13184 */     if (jj_3R_54()) return true;
/* 13185 */     if (jj_3R_343()) return true;
/* 13186 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_49()
/*       */   {
/* 13191 */     if (jj_3R_54()) return true;
/* 13192 */     if (jj_3R_66()) return true;
/* 13193 */     return jj_3R_62();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_225()
/*       */   {
/* 13198 */     return jj_3R_318();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_48()
/*       */   {
/* 13203 */     return jj_3R_69();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_98()
/*       */   {
/* 13208 */     return jj_3R_233();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_47()
/*       */   {
/* 13213 */     if (jj_3R_63()) return true;
/*       */ 
/* 13215 */     Token xsp = jj_scanpos;
/* 13216 */     if (jj_3R_99()) jj_scanpos = xsp;
/* 13217 */     return jj_3R_62();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_46()
/*       */   {
/* 13223 */     Token xsp = jj_scanpos;
/* 13224 */     if (jj_3R_98()) jj_scanpos = xsp;
/* 13225 */     if (jj_3R_61()) return true;
/* 13226 */     return jj_3R_62();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_224()
/*       */   {
/* 13231 */     if (jj_3R_54()) return true;
/* 13232 */     if (jj_3R_204()) return true;
/* 13233 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_594()
/*       */   {
/* 13238 */     return jj_3R_602();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_503()
/*       */   {
/* 13243 */     return jj_scan_token(58);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_223()
/*       */   {
/* 13248 */     return jj_3R_317();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_222()
/*       */   {
/* 13253 */     return jj_3R_320();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_221()
/*       */   {
/* 13258 */     if (jj_3R_213()) return true;
/*       */ 
/* 13260 */     Token xsp = jj_scanpos;
/* 13261 */     if (jj_3R_594()) jj_scanpos = xsp;
/* 13262 */     return jj_3R_72();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_220()
/*       */   {
/* 13267 */     return jj_3R_70();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_94()
/*       */   {
/* 13273 */     Token xsp = jj_scanpos;
/* 13274 */     if (jj_3R_220()) {
/* 13275 */       jj_scanpos = xsp;
/* 13276 */       if (jj_3R_221()) {
/* 13277 */         jj_scanpos = xsp;
/* 13278 */         if (jj_3R_222()) {
/* 13279 */           jj_scanpos = xsp;
/* 13280 */           if (jj_3R_223()) {
/* 13281 */             jj_scanpos = xsp;
/* 13282 */             if (jj_3R_224()) {
/* 13283 */               jj_scanpos = xsp;
/* 13284 */               if (jj_3R_225()) {
/* 13285 */                 jj_scanpos = xsp;
/* 13286 */                 if (jj_3R_226()) {
/* 13287 */                   jj_scanpos = xsp;
/* 13288 */                   if (jj_3R_227()) {
/* 13289 */                     jj_scanpos = xsp;
/* 13290 */                     if (jj_3R_228()) {
/* 13291 */                       jj_scanpos = xsp;
/* 13292 */                       if (jj_3R_229()) {
/* 13293 */                         jj_scanpos = xsp;
/* 13294 */                         if (jj_3_56()) {
/* 13295 */                           jj_scanpos = xsp;
/* 13296 */                           if (jj_3_57()) {
/* 13297 */                             jj_scanpos = xsp;
/* 13298 */                             if (jj_3R_230()) return true;
/*       */                           }
/*       */                         }
/*       */                       }
/*       */                     }
/*       */                   }
/*       */                 }
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 13311 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_501() {
/* 13315 */     return jj_scan_token(57);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_578()
/*       */   {
/* 13320 */     return jj_3R_345();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_490()
/*       */   {
/* 13325 */     if (jj_3R_122()) return true;
/* 13326 */     return jj_3R_489();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_241()
/*       */   {
/* 13331 */     return jj_3R_350();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_577()
/*       */   {
/* 13336 */     return jj_3R_104();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_240()
/*       */   {
/* 13341 */     return jj_3R_349();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_502()
/*       */   {
/* 13346 */     return jj_scan_token(56);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_104()
/*       */   {
/* 13352 */     Token xsp = jj_scanpos;
/* 13353 */     if (jj_3R_239()) {
/* 13354 */       jj_scanpos = xsp;
/* 13355 */       if (jj_3R_240()) {
/* 13356 */         jj_scanpos = xsp;
/* 13357 */         if (jj_3R_241()) return true;
/*       */       }
/*       */     }
/* 13360 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_239() {
/* 13364 */     return jj_3R_348();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_554()
/*       */   {
/* 13369 */     if (jj_3R_95()) return true;
/* 13370 */     if (jj_3R_96()) return true;
/* 13371 */     return jj_3R_273();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_45()
/*       */   {
/* 13376 */     return jj_3R_92();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_553()
/*       */   {
/* 13381 */     return jj_3R_205();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_507()
/*       */   {
/* 13386 */     return jj_scan_token(55);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_562()
/*       */   {
/* 13391 */     if (jj_3R_104()) return true;
/*       */ 
/* 13393 */     Token xsp = jj_scanpos;
/* 13394 */     if (jj_3R_578()) jj_scanpos = xsp;
/* 13395 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_561() {
/* 13399 */     if (jj_3R_345()) return true;
/*       */ 
/* 13401 */     Token xsp = jj_scanpos;
/* 13402 */     if (jj_3R_577()) jj_scanpos = xsp;
/* 13403 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_552()
/*       */   {
/* 13408 */     Token xsp = jj_scanpos;
/* 13409 */     if (jj_3R_561()) {
/* 13410 */       jj_scanpos = xsp;
/* 13411 */       if (jj_3R_562()) return true;
/*       */     }
/* 13413 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_489()
/*       */   {
/* 13418 */     Token xsp = jj_scanpos;
/* 13419 */     if (jj_3R_552()) jj_scanpos = xsp;
/* 13420 */     xsp = jj_scanpos;
/* 13421 */     if (jj_3R_553()) jj_scanpos = xsp;
/* 13422 */     if (jj_3R_88()) return true;
/* 13423 */     xsp = jj_scanpos;
/* 13424 */     if (jj_3R_554()) jj_scanpos = xsp;
/* 13425 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_232() {
/* 13429 */     if (jj_3R_105()) return true;
/* 13430 */     if (jj_3R_489()) return true; Token xsp;
/*       */     do
/* 13433 */       xsp = jj_scanpos;
/* 13434 */     while (!jj_3R_490()); jj_scanpos = xsp;
/*       */ 
/* 13436 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_43() {
/* 13440 */     if (jj_3R_95()) return true;
/* 13441 */     if (jj_3R_96()) return true;
/* 13442 */     return jj_3R_273();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_506()
/*       */   {
/* 13447 */     return jj_scan_token(54);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_394()
/*       */   {
/* 13452 */     if (jj_3R_88()) return true;
/*       */ 
/* 13454 */     Token xsp = jj_scanpos;
/* 13455 */     if (jj_3_43()) jj_scanpos = xsp;
/* 13456 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_42() {
/* 13460 */     return jj_3R_94();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_97()
/*       */   {
/* 13465 */     return jj_3R_232();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_497()
/*       */   {
/* 13470 */     return jj_scan_token(53);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_200()
/*       */   {
/* 13475 */     return jj_3R_134();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_44()
/*       */   {
/* 13480 */     if (jj_3R_88()) return true;
/*       */ 
/* 13482 */     Token xsp = jj_scanpos;
/* 13483 */     if (jj_3R_97()) jj_scanpos = xsp;
/* 13484 */     if (jj_3R_62()) return true;
/*       */     do
/* 13486 */       xsp = jj_scanpos;
/* 13487 */     while (!jj_3_42()); jj_scanpos = xsp;
/*       */ 
/* 13489 */     return jj_3R_127();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_41()
/*       */   {
/* 13494 */     return jj_3R_94();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_496()
/*       */   {
/* 13499 */     return jj_scan_token(52);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_199()
/*       */   {
/* 13504 */     return jj_3R_324();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_337()
/*       */   {
/* 13509 */     return jj_3R_134();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_393()
/*       */   {
/* 13514 */     if (jj_3R_62()) return true; Token xsp;
/*       */     do
/* 13517 */       xsp = jj_scanpos;
/* 13518 */     while (!jj_3_41()); jj_scanpos = xsp;
/*       */ 
/* 13520 */     return jj_3R_127();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_336()
/*       */   {
/* 13525 */     return jj_3R_324();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_325()
/*       */   {
/* 13530 */     return jj_3R_232();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_335()
/*       */   {
/* 13535 */     return jj_3R_323();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_500()
/*       */   {
/* 13540 */     return jj_scan_token(51);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_212()
/*       */   {
/* 13546 */     Token xsp = jj_scanpos;
/* 13547 */     if (jj_3R_335()) {
/* 13548 */       jj_scanpos = xsp;
/* 13549 */       if (jj_3R_336()) {
/* 13550 */         jj_scanpos = xsp;
/* 13551 */         if (jj_3R_337()) return true;
/*       */       }
/*       */     }
/* 13554 */     xsp = jj_scanpos;
/* 13555 */     if (jj_3R_393()) {
/* 13556 */       jj_scanpos = xsp;
/* 13557 */       if (jj_3_44()) {
/* 13558 */         jj_scanpos = xsp;
/* 13559 */         if (jj_3R_394()) return true;
/*       */       }
/*       */     }
/* 13562 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_198() {
/* 13566 */     return jj_3R_323();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_201()
/*       */   {
/* 13571 */     if (jj_scan_token(178)) return true;
/*       */ 
/* 13573 */     Token xsp = jj_scanpos;
/* 13574 */     if (jj_3R_325()) jj_scanpos = xsp;
/* 13575 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_61()
/*       */   {
/* 13580 */     Token xsp = jj_scanpos;
/* 13581 */     if (jj_3R_198()) {
/* 13582 */       jj_scanpos = xsp;
/* 13583 */       if (jj_3R_199()) {
/* 13584 */         jj_scanpos = xsp;
/* 13585 */         if (jj_3R_200()) return true;
/*       */       }
/*       */     }
/* 13588 */     xsp = jj_scanpos;
/* 13589 */     if (jj_3R_201()) jj_scanpos = xsp;
/* 13590 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_499() {
/* 13594 */     return jj_scan_token(50);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_492()
/*       */   {
/* 13599 */     if (jj_3R_156()) return true;
/* 13600 */     if (jj_3R_118()) return true;
/* 13601 */     return jj_3R_157();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_498()
/*       */   {
/* 13606 */     return jj_scan_token(49);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_491()
/*       */   {
/* 13611 */     if (jj_3R_116()) return true;
/* 13612 */     return jj_3R_125();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_392()
/*       */   {
/* 13618 */     Token xsp = jj_scanpos;
/* 13619 */     if (jj_3R_491()) {
/* 13620 */       jj_scanpos = xsp;
/* 13621 */       if (jj_3R_492()) return true;
/*       */     }
/* 13623 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_391() {
/* 13627 */     return jj_3R_78();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_333()
/*       */   {
/* 13633 */     Token xsp = jj_scanpos;
/* 13634 */     if (jj_3R_391()) jj_scanpos = xsp;
/* 13635 */     if (jj_3R_124()) return true;
/* 13636 */     xsp = jj_scanpos;
/* 13637 */     if (jj_3R_392()) jj_scanpos = xsp;
/* 13638 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_334() {
/* 13642 */     if (jj_3R_122()) return true;
/* 13643 */     return jj_3R_333();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_116()
/*       */   {
/* 13648 */     return jj_scan_token(48);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_129()
/*       */   {
/* 13653 */     return jj_scan_token(161);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_209()
/*       */   {
/* 13658 */     if (jj_3R_333()) return true; Token xsp;
/*       */     do
/* 13661 */       xsp = jj_scanpos;
/* 13662 */     while (!jj_3R_334()); jj_scanpos = xsp;
/*       */ 
/* 13664 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_120() {
/* 13668 */     return jj_scan_token(47);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_128()
/*       */   {
/* 13673 */     return jj_scan_token(160);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_530()
/*       */   {
/* 13678 */     return jj_scan_token(46);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_78()
/*       */   {
/* 13684 */     Token xsp = jj_scanpos;
/* 13685 */     if (jj_scan_token(132)) {
/* 13686 */       jj_scanpos = xsp;
/* 13687 */       if (jj_scan_token(91)) {
/* 13688 */         jj_scanpos = xsp;
/* 13689 */         if (jj_scan_token(117)) {
/* 13690 */           jj_scanpos = xsp;
/* 13691 */           if (jj_scan_token(108)) {
/* 13692 */             jj_scanpos = xsp;
/* 13693 */             if (jj_scan_token(109)) {
/* 13694 */               jj_scanpos = xsp;
/* 13695 */               if (jj_scan_token(102)) {
/* 13696 */                 jj_scanpos = xsp;
/* 13697 */                 if (jj_scan_token(97)) {
/* 13698 */                   jj_scanpos = xsp;
/* 13699 */                   if (jj_scan_token(118)) {
/* 13700 */                     jj_scanpos = xsp;
/* 13701 */                     if (jj_scan_token(130)) {
/* 13702 */                       jj_scanpos = xsp;
/* 13703 */                       if (jj_scan_token(87)) {
/* 13704 */                         jj_scanpos = xsp;
/* 13705 */                         if (jj_scan_token(88)) {
/* 13706 */                           jj_scanpos = xsp;
/* 13707 */                           if (jj_scan_token(121)) {
/* 13708 */                             jj_scanpos = xsp;
/* 13709 */                             if (jj_scan_token(139)) {
/* 13710 */                               jj_scanpos = xsp;
/* 13711 */                               if (jj_scan_token(140)) return true;
/*       */                             }
/*       */                           }
/*       */                         }
/*       */                       }
/*       */                     }
/*       */                   }
/*       */                 }
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 13725 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_244() {
/* 13729 */     return jj_3R_109();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_219()
/*       */   {
/* 13734 */     return jj_3R_88();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_218()
/*       */   {
/* 13739 */     return jj_3R_233();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_122()
/*       */   {
/* 13744 */     return jj_scan_token(45);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_342()
/*       */   {
/* 13749 */     return jj_3R_390();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_341()
/*       */   {
/* 13754 */     return jj_3R_397();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_340()
/*       */   {
/* 13759 */     return jj_3R_396();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_339()
/*       */   {
/* 13764 */     return jj_3R_395();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_93()
/*       */   {
/* 13770 */     Token xsp = jj_scanpos;
/* 13771 */     if (jj_3R_217()) {
/* 13772 */       jj_scanpos = xsp;
/* 13773 */       if (jj_3R_218()) {
/* 13774 */         jj_scanpos = xsp;
/* 13775 */         jj_lookingAhead = true;
/* 13776 */         jj_semLA = fgCallback.isStorageClassSpecifier(getToken(1));
/* 13777 */         jj_lookingAhead = false;
/* 13778 */         if ((!jj_semLA) || (jj_3R_219())) return true;
/*       */       }
/*       */     }
/* 13781 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_217()
/*       */   {
/* 13786 */     Token xsp = jj_scanpos;
/* 13787 */     if (jj_3R_339()) {
/* 13788 */       jj_scanpos = xsp;
/* 13789 */       if (jj_3R_340()) {
/* 13790 */         jj_scanpos = xsp;
/* 13791 */         if (jj_3R_341()) {
/* 13792 */           jj_scanpos = xsp;
/* 13793 */           if (jj_3R_342()) return true;
/*       */         }
/*       */       }
/*       */     }
/* 13797 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_72() {
/* 13801 */     return jj_scan_token(44);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_107()
/*       */   {
/* 13807 */     Token xsp = jj_scanpos;
/* 13808 */     if (jj_3R_243()) {
/* 13809 */       jj_scanpos = xsp;
/* 13810 */       if (jj_3R_244()) return true;
/*       */     }
/* 13812 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_243() {
/* 13816 */     return jj_3R_110();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_549()
/*       */   {
/* 13821 */     return jj_scan_token(159);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_105()
/*       */   {
/* 13826 */     return jj_scan_token(43);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_210()
/*       */   {
/* 13831 */     return jj_3R_107();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_40()
/*       */   {
/* 13836 */     return jj_3R_93();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_75()
/*       */   {
/* 13842 */     Token xsp = jj_scanpos;
/* 13843 */     if (jj_3_40()) {
/* 13844 */       jj_scanpos = xsp;
/* 13845 */       if (jj_3R_210()) {
/* 13846 */         jj_scanpos = xsp;
/* 13847 */         if (jj_scan_token(107)) {
/* 13848 */           jj_scanpos = xsp;
/* 13849 */           if (jj_scan_token(131)) {
/* 13850 */             jj_scanpos = xsp;
/* 13851 */             if (jj_scan_token(104)) return true;
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 13856 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_211() {
/* 13860 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_548() {
/* 13864 */     return jj_scan_token(158);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_90()
/*       */   {
/* 13869 */     return jj_scan_token(42);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_79()
/*       */   {
/* 13874 */     jj_lookingAhead = true;
/* 13875 */     jj_semLA = isNotNull(getFullyScopedName());
/* 13876 */     jj_lookingAhead = false;
/* 13877 */     if ((!jj_semLA) || (jj_3R_211())) return true;
/* 13878 */     return jj_3R_103();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_546()
/*       */   {
/* 13883 */     return jj_scan_token(157);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_352()
/*       */   {
/* 13888 */     if (jj_3R_205()) return true;
/* 13889 */     return jj_3R_278();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_346()
/*       */   {
/* 13894 */     if (jj_3R_95()) return true;
/* 13895 */     if (jj_3R_96()) return true;
/* 13896 */     return jj_3R_273();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_39()
/*       */   {
/* 13901 */     return jj_3R_92();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_157()
/*       */   {
/* 13906 */     return jj_scan_token(41);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_238()
/*       */   {
/* 13911 */     if (jj_3R_69()) return true;
/* 13912 */     return jj_3R_347();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_91()
/*       */   {
/* 13917 */     if (jj_3R_95()) return true;
/* 13918 */     if (jj_3R_96()) return true;
/* 13919 */     return jj_3R_273();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_545()
/*       */   {
/* 13924 */     return jj_scan_token(156);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_237()
/*       */   {
/* 13929 */     if (jj_3R_88()) return true;
/*       */ 
/* 13931 */     Token xsp = jj_scanpos;
/* 13932 */     if (jj_3R_346()) jj_scanpos = xsp;
/* 13933 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_183() {
/* 13937 */     return jj_3R_143();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_89()
/*       */   {
/* 13942 */     if (jj_3R_95()) return true;
/* 13943 */     if (jj_3R_96()) return true;
/* 13944 */     return jj_3R_273();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_236()
/*       */   {
/* 13949 */     return jj_3R_205();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_103()
/*       */   {
/* 13955 */     Token xsp = jj_scanpos;
/* 13956 */     if (jj_3R_236()) jj_scanpos = xsp;
/* 13957 */     xsp = jj_scanpos;
/* 13958 */     if (jj_3R_237()) {
/* 13959 */       jj_scanpos = xsp;
/* 13960 */       if (jj_3R_238()) return true;
/*       */     }
/* 13962 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_182() {
/* 13966 */     return jj_3R_278();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_156()
/*       */   {
/* 13971 */     return jj_scan_token(40);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_540()
/*       */   {
/* 13976 */     return jj_scan_token(155);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_38()
/*       */   {
/* 13981 */     if (jj_3R_88()) return true;
/*       */ 
/* 13983 */     Token xsp = jj_scanpos;
/* 13984 */     if (jj_3R_91()) jj_scanpos = xsp;
/* 13985 */     return jj_3R_90();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_181()
/*       */   {
/* 13990 */     return jj_scan_token(39);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_148()
/*       */   {
/* 13996 */     Token xsp = jj_scanpos;
/* 13997 */     if (jj_3R_182()) {
/* 13998 */       jj_scanpos = xsp;
/* 13999 */       if (jj_3R_183()) return true;
/*       */     }
/* 14001 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_330()
/*       */   {
/* 14006 */     if (jj_3_38()) return true; Token xsp;
/*       */     do xsp = jj_scanpos;
/* 14009 */     while (!jj_3_38()); jj_scanpos = xsp;
/*       */ 
/* 14011 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_539() {
/* 14015 */     return jj_scan_token(154);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_37()
/*       */   {
/* 14020 */     if (jj_3R_88()) return true;
/*       */ 
/* 14022 */     Token xsp = jj_scanpos;
/* 14023 */     if (jj_3R_89()) jj_scanpos = xsp;
/* 14024 */     return jj_3R_90();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_329()
/*       */   {
/* 14029 */     if (jj_3R_90()) return true; Token xsp;
/*       */     do
/* 14032 */       xsp = jj_scanpos;
/* 14033 */     while (!jj_3_37()); jj_scanpos = xsp;
/*       */ 
/* 14035 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_205()
/*       */   {
/* 14040 */     Token xsp = jj_scanpos;
/* 14041 */     if (jj_3R_329()) {
/* 14042 */       jj_scanpos = xsp;
/* 14043 */       if (jj_3R_330()) return true;
/*       */     }
/* 14045 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_338() {
/* 14049 */     if (jj_3R_95()) return true;
/* 14050 */     if (jj_3R_96()) return true;
/* 14051 */     return jj_3R_273();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_168()
/*       */   {
/* 14056 */     return jj_scan_token(38);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_544()
/*       */   {
/* 14061 */     return jj_scan_token(153);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_216()
/*       */   {
/* 14066 */     if (jj_3R_88()) return true;
/*       */ 
/* 14068 */     Token xsp = jj_scanpos;
/* 14069 */     if (jj_3R_338()) jj_scanpos = xsp;
/* 14070 */     return jj_3R_90();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_92()
/*       */   {
/* 14076 */     Token xsp = jj_scanpos;
/* 14077 */     if (jj_3R_215()) {
/* 14078 */       jj_scanpos = xsp;
/* 14079 */       if (jj_3R_216()) return true;
/*       */     }
/* 14081 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_215() {
/* 14085 */     return jj_3R_90();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_127()
/*       */   {
/* 14090 */     return jj_scan_token(37);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_36()
/*       */   {
/* 14095 */     return jj_3R_79();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3_149()
/*       */   {
/* 14100 */     if (jj_3R_58()) return true;
/*       */ 
/* 14102 */     Token xsp = jj_scanpos;
/* 14103 */     if (jj_3_148()) jj_scanpos = xsp;
/* 14104 */     return false;
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_543() {
/* 14108 */     return jj_scan_token(152);
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_214()
/*       */   {
/* 14113 */     return jj_3R_78();
/*       */   }
/*       */ 
/*       */   private static boolean jj_3R_87()
/*       */   {
/* 14119 */     Token xsp = jj_scanpos;
/* 14120 */     if (jj_3R_214()) {
/* 14121 */       jj_scanpos = xsp;
/* 14122 */       if (jj_3_36()) return true;
/*       */     }
/* 14124 */     return false;
/*       */   }
/*       */ 
/*       */   private static void jj_la1_init_0()
/*       */   {
/* 14158 */     jj_la1_0 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*       */   }
/*       */   private static void jj_la1_init_1() {
/* 14161 */     jj_la1_1 = new int[] { 256, 0, -2147482368, -2147482368, 0, 4096, -2147482624, 4112, -2147482368, 0, -2147482368, 0, 0, 0, 0, 0, 0, 1024, 0, 0, 1024, 0, 0, 0, 0, 0, 0, 0, 8192, 0, 65792, 65792, 0, 2048, 0, 0, 2048, 16, 0, 8192, 0, 0, 0, 0, 0, 0, -2147482368, -2147482368, 4096, 65536, 8192, 65536, -2147483648, -2147483648, 0, 16, -2147482368, 16, 8192, 65536, -2147482624, 0, 1280, 1280, 8192, 64, 0, 320, 1024, 0, 0, 0, 0, 0, 0, 0, 2048, 6160, 8192, 8192, 32768, 65536, 8192, 320, 8192, 64, -2147482304, 8192, 320, 8192, 8192, 4112, 0, 0, 0, 0, 0, 0, 4096, 0, 0, 0, 0, 32768, 8192, 134152192, 134152192, 16384, 134217728, 268435456, 536870912, 1073741824, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2147482560, 8192, 1024, 64, -2147483648, 0, 8192, 320, 0, 256, 1024, 8192, 0, -2147483648, -57024, 8192, 0 };
/*       */   }
/*       */   private static void jj_la1_init_2() {
/* 14164 */     jj_la1_2 = new int[] { 0, 0, 159417344, 159417344, 0, 0, 1024, 0, 159417344, 0, 159417344, 0, 0, 0, 0, 159383552, 4, 0, 4, 4, 0, 4, 0, 268435456, 268435456, 2097152, 2097152, 159383552, 0, 159383552, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 33792, 33792, 0, 0, 0, 0, 1024, 1024, 0, 0, 159417344, 0, 0, 0, 1024, 268435456, 32768, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1024, 0, 0, 0, 0, 541065216, 0, 541065216, 541065216, 1107296256, 0, 0, 0, 0, 541065216, 67108864, 67108864, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 60, 192, 192, 768, 7168, 1572864, 1572864, 0, 0, 24576, 0, 1024, 0, 0, 0, 100096, 0, 0, 417792, 32768, 0, 32768, 0, 0, 1024, -2146041857, 0, 0 };
/*       */   }
/*       */   private static void jj_la1_init_3() {
/* 14167 */     jj_la1_3 = new int[] { 0, 536870912, 39858242, 39858242, 536870912, 0, 0, 0, 39858242, 16, 39858242, 16, 201326600, 201326600, 201326600, 39858242, 0, 0, 0, 0, 0, 0, 0, 2304, 0, 17301520, 17301520, 39858242, 0, 39858242, 0, 0, 201326592, 0, 0, 201326592, 0, 0, 0, 0, 229376, 0, 229376, 229376, 0, 229376, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39858242, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2048, 0, 2048, 2048, 536870912, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1877998080, 129, 1049088, 1049088, 0, 4, 268436480, 0, 129, 1049088, 32, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8388608, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1073741824, 0, 0, 0, 0, 16384, 0, 0 };
/*       */   }
/*       */   private static void jj_la1_init_4() {
/* 14170 */     jj_la1_4 = new int[] { 0, 0, 6292, 6292, 0, 0, 128, 0, 6292, 0, 6292, 0, 2, 2, 2, 6164, 0, 0, 0, 0, 0, 0, 128, 40, 32, 0, 1, 6164, 0, 6164, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 8, 8, 8, 0, 0, 128, 128, 0, 0, 0, 0, 0, 0, 1024, 0, 6292, 0, 0, 0, 0, 32, 128, 128, 0, 0, 1024, 0, 128, 1024, 0, 8, 8, 8, 0, 1024, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1024, 64, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 122880, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 128, 0, -130176, 0, -130304, 0, 0, 0, 768 };
/*       */   }
/*       */   private static void jj_la1_init_5() {
/* 14173 */     jj_la1_5 = new int[] { 0, 0, 16, 16, 0, 0, 16, 0, 16, 0, 16, 0, 0, 0, 0, 0, 0, 16, 0, 0, 16, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 262144, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 16, 16, 0, 0, 0, 0, 0, 0, 0, 16, 16, 16, 0, 0, 16, 0, 16, 16, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 16, 0, 0, 0, 0, 198848, 0, 0, 262192, 8, 393268, 0, 131076, 0, 0, 0, 0 };
/*       */   }
/*       */ 
/*       */   public CPPParser(InputStream stream)
/*       */   {
/* 14181 */     this(stream, null);
/*       */   }
/*       */ 
/*       */   public CPPParser(InputStream stream, String encoding) {
/* 14185 */     if (jj_initialized_once) {
/* 14186 */       System.out.println("ERROR: Second call to constructor of static parser.  ");
/* 14187 */       System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
/* 14188 */       System.out.println("       during parser generation.");
/* 14189 */       throw new Error();
/*       */     }
/* 14191 */     jj_initialized_once = true;
/*       */     try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
/* 14193 */     token_source = new CPPParserTokenManager(jj_input_stream);
/* 14194 */     token = new Token();
/* 14195 */     jj_ntk = -1;
/* 14196 */     jj_gen = 0;
/* 14197 */     for (int i = 0; i < 142; i++) jj_la1[i] = -1;
/* 14198 */     for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
/*       */   }
/*       */ 
/*       */   public static void ReInit(InputStream stream)
/*       */   {
/* 14203 */     ReInit(stream, null);
/*       */   }
/*       */   public static void ReInit(InputStream stream, String encoding) {
/*       */     try {
/* 14207 */       jj_input_stream.ReInit(stream, encoding, 1, 1); } catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
/* 14208 */     CPPParserTokenManager.ReInit(jj_input_stream);
/* 14209 */     token = new Token();
/* 14210 */     jj_ntk = -1;
/* 14211 */     jjtree.reset();
/* 14212 */     jj_gen = 0;
/* 14213 */     for (int i = 0; i < 142; i++) jj_la1[i] = -1;
/* 14214 */     for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
/*       */   }
/*       */ 
/*       */   public CPPParser(Reader stream)
/*       */   {
/* 14219 */     if (jj_initialized_once) {
/* 14220 */       System.out.println("ERROR: Second call to constructor of static parser. ");
/* 14221 */       System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
/* 14222 */       System.out.println("       during parser generation.");
/* 14223 */       throw new Error();
/*       */     }
/* 14225 */     jj_initialized_once = true;
/* 14226 */     jj_input_stream = new JavaCharStream(stream, 1, 1);
/* 14227 */     token_source = new CPPParserTokenManager(jj_input_stream);
/* 14228 */     token = new Token();
/* 14229 */     jj_ntk = -1;
/* 14230 */     jj_gen = 0;
/* 14231 */     for (int i = 0; i < 142; i++) jj_la1[i] = -1;
/* 14232 */     for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
/*       */   }
/*       */ 
/*       */   public static void ReInit(Reader stream)
/*       */   {
/* 14237 */     jj_input_stream.ReInit(stream, 1, 1);
/* 14238 */     CPPParserTokenManager.ReInit(jj_input_stream);
/* 14239 */     token = new Token();
/* 14240 */     jj_ntk = -1;
/* 14241 */     jjtree.reset();
/* 14242 */     jj_gen = 0;
/* 14243 */     for (int i = 0; i < 142; i++) jj_la1[i] = -1;
/* 14244 */     for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
/*       */   }
/*       */ 
/*       */   public CPPParser(CPPParserTokenManager tm)
/*       */   {
/* 14249 */     if (jj_initialized_once) {
/* 14250 */       System.out.println("ERROR: Second call to constructor of static parser. ");
/* 14251 */       System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
/* 14252 */       System.out.println("       during parser generation.");
/* 14253 */       throw new Error();
/*       */     }
/* 14255 */     jj_initialized_once = true;
/* 14256 */     token_source = tm;
/* 14257 */     token = new Token();
/* 14258 */     jj_ntk = -1;
/* 14259 */     jj_gen = 0;
/* 14260 */     for (int i = 0; i < 142; i++) jj_la1[i] = -1;
/* 14261 */     for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
/*       */   }
/*       */ 
/*       */   public void ReInit(CPPParserTokenManager tm)
/*       */   {
/* 14266 */     token_source = tm;
/* 14267 */     token = new Token();
/* 14268 */     jj_ntk = -1;
/* 14269 */     jjtree.reset();
/* 14270 */     jj_gen = 0;
/* 14271 */     for (int i = 0; i < 142; i++) jj_la1[i] = -1;
/* 14272 */     for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
/*       */   }
/*       */ 
/*       */   private static Token jj_consume_token(int kind)
/*       */     throws ParseException
/*       */   {
/* 14277 */     Token oldToken;
/* 14277 */     if ((oldToken = token).next != null) token = token.next; else
/* 14278 */       token = token.next = CPPParserTokenManager.getNextToken();
/* 14279 */     jj_ntk = -1;
/* 14280 */     if (token.kind == kind) {
/* 14281 */       jj_gen += 1;
/* 14282 */       if (++jj_gc > 100) {
/* 14283 */         jj_gc = 0;
/* 14284 */         for (int i = 0; i < jj_2_rtns.length; i++) {
/* 14285 */           JJCalls c = jj_2_rtns[i];
/* 14286 */           while (c != null) {
/* 14287 */             if (c.gen < jj_gen) c.first = null;
/* 14288 */             c = c.next;
/*       */           }
/*       */         }
/*       */       }
/* 14292 */       return token;
/*       */     }
/* 14294 */     token = oldToken;
/* 14295 */     jj_kind = kind;
/* 14296 */     throw generateParseException();
/*       */   }
/*       */ 
/*       */   private static boolean jj_scan_token(int kind)
/*       */   {
/* 14302 */     if (jj_scanpos == jj_lastpos) {
/* 14303 */       jj_la -= 1;
/* 14304 */       if (jj_scanpos.next == null)
/* 14305 */         jj_lastpos = CPPParser.jj_scanpos = jj_scanpos.next = CPPParserTokenManager.getNextToken();
/*       */       else
/* 14307 */         jj_lastpos = CPPParser.jj_scanpos = jj_scanpos.next;
/*       */     }
/*       */     else {
/* 14310 */       jj_scanpos = jj_scanpos.next;
/*       */     }
/* 14312 */     if (jj_rescan) {
/* 14313 */       int i = 0; Token tok = token;
/* 14314 */       for (; (tok != null) && (tok != jj_scanpos); tok = tok.next) i++;
/* 14315 */       if (tok != null) jj_add_error_token(kind, i);
/*       */     }
/* 14317 */     if (jj_scanpos.kind != kind) return true;
/* 14318 */     if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) throw jj_ls;
/* 14319 */     return false;
/*       */   }
/*       */ 
/*       */   public static final Token getNextToken()
/*       */   {
/* 14325 */     if (token.next != null) token = token.next; else
/* 14326 */       token = token.next = CPPParserTokenManager.getNextToken();
/* 14327 */     jj_ntk = -1;
/* 14328 */     jj_gen += 1;
/* 14329 */     return token;
/*       */   }
/*       */ 
/*       */   public static final Token getToken(int index)
/*       */   {
/* 14334 */     Token t = jj_lookingAhead ? jj_scanpos : token;
/* 14335 */     for (int i = 0; i < index; i++) {
/* 14336 */       if (t.next != null) t = t.next; else
/* 14337 */         t = t.next = CPPParserTokenManager.getNextToken();
/*       */     }
/* 14339 */     return t;
/*       */   }
/*       */ 
/*       */   private static int jj_ntk() {
/* 14343 */     if ((CPPParser.jj_nt = token.next) == null) {
/* 14344 */       return CPPParser.jj_ntk = (token.next = CPPParserTokenManager.getNextToken()).kind;
/*       */     }
/* 14346 */     return CPPParser.jj_ntk = jj_nt.kind;
/*       */   }
/*       */ 
/*       */   private static void jj_add_error_token(int kind, int pos)
/*       */   {
/* 14356 */     if (pos >= 100) return;
/* 14357 */     if (pos == jj_endpos + 1) {
/* 14358 */       jj_lasttokens[(jj_endpos++)] = kind;
/* 14359 */     } else if (jj_endpos != 0) {
/* 14360 */       jj_expentry = new int[jj_endpos];
/* 14361 */       for (int i = 0; i < jj_endpos; i++) {
/* 14362 */         jj_expentry[i] = jj_lasttokens[i];
/*       */       }
/* 14364 */       for (Iterator it = jj_expentries.iterator(); it.hasNext(); ) {
/* 14365 */         int[] oldentry = (int[])(int[])it.next();
/* 14366 */         if (oldentry.length == jj_expentry.length) {
/* 14367 */           for (int i = 0; ; i++) { if (i >= jj_expentry.length) break label146; if (oldentry[i] != jj_expentry[i]) {
/*       */               break;
/*       */             }
/*       */           }
/* 14372 */           jj_expentries.add(jj_expentry);
/* 14373 */           break;
/*       */         }
/*       */       }
/* 14376 */       label146: if (pos != 0) { tmp172_171 = pos; jj_endpos = tmp172_171; jj_lasttokens[(tmp172_171 - 1)] = kind; }
/*       */     }
/*       */   }
/*       */ 
/*       */   public static ParseException generateParseException()
/*       */   {
/* 14382 */     jj_expentries.clear();
/* 14383 */     boolean[] la1tokens = new boolean['µ'];
/* 14384 */     if (jj_kind >= 0) {
/* 14385 */       la1tokens[jj_kind] = true;
/* 14386 */       jj_kind = -1;
/*       */     }
/* 14388 */     for (int i = 0; i < 142; i++) {
/* 14389 */       if (jj_la1[i] == jj_gen) {
/* 14390 */         for (int j = 0; j < 32; j++) {
/* 14391 */           if ((jj_la1_0[i] & 1 << j) != 0) {
/* 14392 */             la1tokens[j] = true;
/*       */           }
/* 14394 */           if ((jj_la1_1[i] & 1 << j) != 0) {
/* 14395 */             la1tokens[(32 + j)] = true;
/*       */           }
/* 14397 */           if ((jj_la1_2[i] & 1 << j) != 0) {
/* 14398 */             la1tokens[(64 + j)] = true;
/*       */           }
/* 14400 */           if ((jj_la1_3[i] & 1 << j) != 0) {
/* 14401 */             la1tokens[(96 + j)] = true;
/*       */           }
/* 14403 */           if ((jj_la1_4[i] & 1 << j) != 0) {
/* 14404 */             la1tokens[(128 + j)] = true;
/*       */           }
/* 14406 */           if ((jj_la1_5[i] & 1 << j) != 0) {
/* 14407 */             la1tokens[(160 + j)] = true;
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/* 14412 */     for (int i = 0; i < 181; i++) {
/* 14413 */       if (la1tokens[i] != 0) {
/* 14414 */         jj_expentry = new int[1];
/* 14415 */         jj_expentry[0] = i;
/* 14416 */         jj_expentries.add(jj_expentry);
/*       */       }
/*       */     }
/* 14419 */     jj_endpos = 0;
/* 14420 */     jj_rescan_token();
/* 14421 */     jj_add_error_token(0, 0);
/* 14422 */     int[][] exptokseq = new int[jj_expentries.size()][];
/* 14423 */     for (int i = 0; i < jj_expentries.size(); i++) {
/* 14424 */       exptokseq[i] = ((int[])jj_expentries.get(i));
/*       */     }
/* 14426 */     return new ParseException(token, exptokseq, tokenImage);
/*       */   }
/*       */ 
/*       */   public static final void enable_tracing()
/*       */   {
/*       */   }
/*       */ 
/*       */   public static final void disable_tracing()
/*       */   {
/*       */   }
/*       */ 
/*       */   private static void jj_rescan_token() {
/* 14438 */     jj_rescan = true;
/* 14439 */     for (int i = 0; i < 149; i++)
/*       */       try {
/* 14441 */         JJCalls p = jj_2_rtns[i];
/*       */         do {
/* 14443 */           if (p.gen > jj_gen) {
/* 14444 */             jj_la = p.arg; jj_lastpos = CPPParser.jj_scanpos = p.first;
/* 14445 */             switch (i) { case 0:
/* 14446 */               jj_3_1(); break;
/*       */             case 1:
/* 14447 */               jj_3_2(); break;
/*       */             case 2:
/* 14448 */               jj_3_3(); break;
/*       */             case 3:
/* 14449 */               jj_3_4(); break;
/*       */             case 4:
/* 14450 */               jj_3_5(); break;
/*       */             case 5:
/* 14451 */               jj_3_6(); break;
/*       */             case 6:
/* 14452 */               jj_3_7(); break;
/*       */             case 7:
/* 14453 */               jj_3_8(); break;
/*       */             case 8:
/* 14454 */               jj_3_9(); break;
/*       */             case 9:
/* 14455 */               jj_3_10(); break;
/*       */             case 10:
/* 14456 */               jj_3_11(); break;
/*       */             case 11:
/* 14457 */               jj_3_12(); break;
/*       */             case 12:
/* 14458 */               jj_3_13(); break;
/*       */             case 13:
/* 14459 */               jj_3_14(); break;
/*       */             case 14:
/* 14460 */               jj_3_15(); break;
/*       */             case 15:
/* 14461 */               jj_3_16(); break;
/*       */             case 16:
/* 14462 */               jj_3_17(); break;
/*       */             case 17:
/* 14463 */               jj_3_18(); break;
/*       */             case 18:
/* 14464 */               jj_3_19(); break;
/*       */             case 19:
/* 14465 */               jj_3_20(); break;
/*       */             case 20:
/* 14466 */               jj_3_21(); break;
/*       */             case 21:
/* 14467 */               jj_3_22(); break;
/*       */             case 22:
/* 14468 */               jj_3_23(); break;
/*       */             case 23:
/* 14469 */               jj_3_24(); break;
/*       */             case 24:
/* 14470 */               jj_3_25(); break;
/*       */             case 25:
/* 14471 */               jj_3_26(); break;
/*       */             case 26:
/* 14472 */               jj_3_27(); break;
/*       */             case 27:
/* 14473 */               jj_3_28(); break;
/*       */             case 28:
/* 14474 */               jj_3_29(); break;
/*       */             case 29:
/* 14475 */               jj_3_30(); break;
/*       */             case 30:
/* 14476 */               jj_3_31(); break;
/*       */             case 31:
/* 14477 */               jj_3_32(); break;
/*       */             case 32:
/* 14478 */               jj_3_33(); break;
/*       */             case 33:
/* 14479 */               jj_3_34(); break;
/*       */             case 34:
/* 14480 */               jj_3_35(); break;
/*       */             case 35:
/* 14481 */               jj_3_36(); break;
/*       */             case 36:
/* 14482 */               jj_3_37(); break;
/*       */             case 37:
/* 14483 */               jj_3_38(); break;
/*       */             case 38:
/* 14484 */               jj_3_39(); break;
/*       */             case 39:
/* 14485 */               jj_3_40(); break;
/*       */             case 40:
/* 14486 */               jj_3_41(); break;
/*       */             case 41:
/* 14487 */               jj_3_42(); break;
/*       */             case 42:
/* 14488 */               jj_3_43(); break;
/*       */             case 43:
/* 14489 */               jj_3_44(); break;
/*       */             case 44:
/* 14490 */               jj_3_45(); break;
/*       */             case 45:
/* 14491 */               jj_3_46(); break;
/*       */             case 46:
/* 14492 */               jj_3_47(); break;
/*       */             case 47:
/* 14493 */               jj_3_48(); break;
/*       */             case 48:
/* 14494 */               jj_3_49(); break;
/*       */             case 49:
/* 14495 */               jj_3_50(); break;
/*       */             case 50:
/* 14496 */               jj_3_51(); break;
/*       */             case 51:
/* 14497 */               jj_3_52(); break;
/*       */             case 52:
/* 14498 */               jj_3_53(); break;
/*       */             case 53:
/* 14499 */               jj_3_54(); break;
/*       */             case 54:
/* 14500 */               jj_3_55(); break;
/*       */             case 55:
/* 14501 */               jj_3_56(); break;
/*       */             case 56:
/* 14502 */               jj_3_57(); break;
/*       */             case 57:
/* 14503 */               jj_3_58(); break;
/*       */             case 58:
/* 14504 */               jj_3_59(); break;
/*       */             case 59:
/* 14505 */               jj_3_60(); break;
/*       */             case 60:
/* 14506 */               jj_3_61(); break;
/*       */             case 61:
/* 14507 */               jj_3_62(); break;
/*       */             case 62:
/* 14508 */               jj_3_63(); break;
/*       */             case 63:
/* 14509 */               jj_3_64(); break;
/*       */             case 64:
/* 14510 */               jj_3_65(); break;
/*       */             case 65:
/* 14511 */               jj_3_66(); break;
/*       */             case 66:
/* 14512 */               jj_3_67(); break;
/*       */             case 67:
/* 14513 */               jj_3_68(); break;
/*       */             case 68:
/* 14514 */               jj_3_69(); break;
/*       */             case 69:
/* 14515 */               jj_3_70(); break;
/*       */             case 70:
/* 14516 */               jj_3_71(); break;
/*       */             case 71:
/* 14517 */               jj_3_72(); break;
/*       */             case 72:
/* 14518 */               jj_3_73(); break;
/*       */             case 73:
/* 14519 */               jj_3_74(); break;
/*       */             case 74:
/* 14520 */               jj_3_75(); break;
/*       */             case 75:
/* 14521 */               jj_3_76(); break;
/*       */             case 76:
/* 14522 */               jj_3_77(); break;
/*       */             case 77:
/* 14523 */               jj_3_78(); break;
/*       */             case 78:
/* 14524 */               jj_3_79(); break;
/*       */             case 79:
/* 14525 */               jj_3_80(); break;
/*       */             case 80:
/* 14526 */               jj_3_81(); break;
/*       */             case 81:
/* 14527 */               jj_3_82(); break;
/*       */             case 82:
/* 14528 */               jj_3_83(); break;
/*       */             case 83:
/* 14529 */               jj_3_84(); break;
/*       */             case 84:
/* 14530 */               jj_3_85(); break;
/*       */             case 85:
/* 14531 */               jj_3_86(); break;
/*       */             case 86:
/* 14532 */               jj_3_87(); break;
/*       */             case 87:
/* 14533 */               jj_3_88(); break;
/*       */             case 88:
/* 14534 */               jj_3_89(); break;
/*       */             case 89:
/* 14535 */               jj_3_90(); break;
/*       */             case 90:
/* 14536 */               jj_3_91(); break;
/*       */             case 91:
/* 14537 */               jj_3_92(); break;
/*       */             case 92:
/* 14538 */               jj_3_93(); break;
/*       */             case 93:
/* 14539 */               jj_3_94(); break;
/*       */             case 94:
/* 14540 */               jj_3_95(); break;
/*       */             case 95:
/* 14541 */               jj_3_96(); break;
/*       */             case 96:
/* 14542 */               jj_3_97(); break;
/*       */             case 97:
/* 14543 */               jj_3_98(); break;
/*       */             case 98:
/* 14544 */               jj_3_99(); break;
/*       */             case 99:
/* 14545 */               jj_3_100(); break;
/*       */             case 100:
/* 14546 */               jj_3_101(); break;
/*       */             case 101:
/* 14547 */               jj_3_102(); break;
/*       */             case 102:
/* 14548 */               jj_3_103(); break;
/*       */             case 103:
/* 14549 */               jj_3_104(); break;
/*       */             case 104:
/* 14550 */               jj_3_105(); break;
/*       */             case 105:
/* 14551 */               jj_3_106(); break;
/*       */             case 106:
/* 14552 */               jj_3_107(); break;
/*       */             case 107:
/* 14553 */               jj_3_108(); break;
/*       */             case 108:
/* 14554 */               jj_3_109(); break;
/*       */             case 109:
/* 14555 */               jj_3_110(); break;
/*       */             case 110:
/* 14556 */               jj_3_111(); break;
/*       */             case 111:
/* 14557 */               jj_3_112(); break;
/*       */             case 112:
/* 14558 */               jj_3_113(); break;
/*       */             case 113:
/* 14559 */               jj_3_114(); break;
/*       */             case 114:
/* 14560 */               jj_3_115(); break;
/*       */             case 115:
/* 14561 */               jj_3_116(); break;
/*       */             case 116:
/* 14562 */               jj_3_117(); break;
/*       */             case 117:
/* 14563 */               jj_3_118(); break;
/*       */             case 118:
/* 14564 */               jj_3_119(); break;
/*       */             case 119:
/* 14565 */               jj_3_120(); break;
/*       */             case 120:
/* 14566 */               jj_3_121(); break;
/*       */             case 121:
/* 14567 */               jj_3_122(); break;
/*       */             case 122:
/* 14568 */               jj_3_123(); break;
/*       */             case 123:
/* 14569 */               jj_3_124(); break;
/*       */             case 124:
/* 14570 */               jj_3_125(); break;
/*       */             case 125:
/* 14571 */               jj_3_126(); break;
/*       */             case 126:
/* 14572 */               jj_3_127(); break;
/*       */             case 127:
/* 14573 */               jj_3_128(); break;
/*       */             case 128:
/* 14574 */               jj_3_129(); break;
/*       */             case 129:
/* 14575 */               jj_3_130(); break;
/*       */             case 130:
/* 14576 */               jj_3_131(); break;
/*       */             case 131:
/* 14577 */               jj_3_132(); break;
/*       */             case 132:
/* 14578 */               jj_3_133(); break;
/*       */             case 133:
/* 14579 */               jj_3_134(); break;
/*       */             case 134:
/* 14580 */               jj_3_135(); break;
/*       */             case 135:
/* 14581 */               jj_3_136(); break;
/*       */             case 136:
/* 14582 */               jj_3_137(); break;
/*       */             case 137:
/* 14583 */               jj_3_138(); break;
/*       */             case 138:
/* 14584 */               jj_3_139(); break;
/*       */             case 139:
/* 14585 */               jj_3_140(); break;
/*       */             case 140:
/* 14586 */               jj_3_141(); break;
/*       */             case 141:
/* 14587 */               jj_3_142(); break;
/*       */             case 142:
/* 14588 */               jj_3_143(); break;
/*       */             case 143:
/* 14589 */               jj_3_144(); break;
/*       */             case 144:
/* 14590 */               jj_3_145(); break;
/*       */             case 145:
/* 14591 */               jj_3_146(); break;
/*       */             case 146:
/* 14592 */               jj_3_147(); break;
/*       */             case 147:
/* 14593 */               jj_3_148(); break;
/*       */             case 148:
/* 14594 */               jj_3_149();
/*       */             }
/*       */           }
/* 14597 */           p = p.next;
/* 14598 */         }while (p != null);
/*       */       } catch (LookaheadSuccess ls) {
/*       */       }
/* 14601 */     jj_rescan = false;
/*       */   }
/*       */ 
/*       */   private static void jj_save(int index, int xla) {
/* 14605 */     JJCalls p = jj_2_rtns[index];
/* 14606 */     while (p.gen > jj_gen) {
/* 14607 */       if (p.next == null) { p = p.next = new JJCalls(); break; }
/* 14608 */       p = p.next;
/*       */     }
/* 14610 */     p.gen = (jj_gen + xla - jj_la); p.first = token; p.arg = xla;
/*       */   }
/*       */ 
/*       */   static
/*       */   {
/* 14150 */     jj_la1_init_0();
/* 14151 */     jj_la1_init_1();
/* 14152 */     jj_la1_init_2();
/* 14153 */     jj_la1_init_3();
/* 14154 */     jj_la1_init_4();
/* 14155 */     jj_la1_init_5();
/*       */ 
/* 14175 */     jj_2_rtns = new JJCalls[''];
/* 14176 */     jj_rescan = false;
/* 14177 */     jj_gc = 0;
/*       */ 
/* 14300 */     jj_ls = new LookaheadSuccess(null);
/*       */ 
/* 14349 */     jj_expentries = new ArrayList();
/*       */ 
/* 14351 */     jj_kind = -1;
/* 14352 */     jj_lasttokens = new int[100];
/*       */   }
/*       */ 
/*       */   static final class JJCalls
/*       */   {
/*       */     int gen;
/*       */     Token first;
/*       */     int arg;
/*       */     JJCalls next;
/*       */   }
/*       */ 
/*       */   private static final class LookaheadSuccess extends Error
/*       */   {
/*       */   }
/*       */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.CPPParser
 * JD-Core Version:    0.6.0
 */