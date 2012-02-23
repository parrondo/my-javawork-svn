/*      */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ 
/*      */ public class CPPParserTokenManager
/*      */   implements CPPParserConstants
/*      */ {
/*      */   static int beginLine;
/*      */   static int beginCol;
/*   14 */   static boolean lineDirective = false;
/*      */ 
/*   21 */   public static PrintStream debugStream = System.out;
/*      */ 
/*   28 */   static final long[] jjbitVec0 = { -2L, -1L, -1L, -1L };
/*      */ 
/*   31 */   static final long[] jjbitVec2 = { 0L, 0L, -1L, -1L };
/*      */ 
/* 1615 */   static final long[] jjbitVec3 = { -4503599625273342L, -8193L, -17525614051329L, 1297036692691091455L };
/*      */ 
/* 1618 */   static final long[] jjbitVec4 = { 0L, 0L, 297242231151001600L, -36028797027352577L };
/*      */ 
/* 1621 */   static final long[] jjbitVec5 = { 36028797018963967L, -65536L, -1L, 70501888425923L };
/*      */ 
/* 1624 */   static final long[] jjbitVec6 = { 0L, 288230376151711744L, -17179879616L, 1134907106097332223L };
/*      */ 
/* 1627 */   static final long[] jjbitVec7 = { -1L, -1L, -1021L, 234187180623233023L };
/*      */ 
/* 1630 */   static final long[] jjbitVec8 = { -562949953355777L, -8547991553L, 255L, 1979120929931264L };
/*      */ 
/* 1633 */   static final long[] jjbitVec9 = { 576460743713488896L, -351843720886273L, -1L, -7205547885240254465L };
/*      */ 
/* 1636 */   static final long[] jjbitVec10 = { 281474976514048L, 57344L, 563224831328255L, 0L };
/*      */ 
/* 1639 */   static final long[] jjbitVec11 = { 2594073385365405680L, 17163157504L, 2577745637692514272L, 4222140488351744L };
/*      */ 
/* 1642 */   static final long[] jjbitVec12 = { 247132830528276448L, 7881300924956672L, 2589004636761079776L, 562962838388736L };
/*      */ 
/* 1645 */   static final long[] jjbitVec13 = { 2589004636760940512L, 562965791113216L, 270153412153034728L, 144115188075855872L };
/*      */ 
/* 1648 */   static final long[] jjbitVec14 = { 283724577500946400L, 12884901888L, 2589567586714640352L, 13958643712L };
/*      */ 
/* 1651 */   static final long[] jjbitVec15 = { 288228177128316896L, 12884901888L, 3457638613854978016L, 127L };
/*      */ 
/* 1654 */   static final long[] jjbitVec16 = { -9219431387180826626L, 127L, 2309762420256548246L, 805306463L };
/*      */ 
/* 1657 */   static final long[] jjbitVec17 = { 1L, 8796093021951L, 3840L, 0L };
/*      */ 
/* 1660 */   static final long[] jjbitVec18 = { 7679401525247L, 4128768L, -4294967296L, 144115188075790399L };
/*      */ 
/* 1663 */   static final long[] jjbitVec19 = { -1L, -2080374785L, -1065151889409L, 288230376151711743L };
/*      */ 
/* 1666 */   static final long[] jjbitVec20 = { -129L, -3263218305L, 9168625153884503423L, -140737496776899L };
/*      */ 
/* 1669 */   static final long[] jjbitVec21 = { -2160230401L, 134217599L, -4294967296L, 9007199254740991L };
/*      */ 
/* 1672 */   static final long[] jjbitVec22 = { -1L, 35923243902697471L, -4160749570L, 501377302265855L };
/*      */ 
/* 1675 */   static final long[] jjbitVec23 = { 1125895612129279L, 527761286627327L, 4503599627370495L, 411041792L };
/*      */ 
/* 1678 */   static final long[] jjbitVec24 = { -4294967296L, 72057594037927935L, 2199023255551L, 0L };
/*      */ 
/* 1681 */   static final long[] jjbitVec25 = { 536870911L, 8796093022142464L, 0L, 0L };
/*      */ 
/* 1684 */   static final long[] jjbitVec26 = { -1L, 17592186044415L, 0L, 0L };
/*      */ 
/* 1687 */   static final long[] jjbitVec27 = { -1L, -1L, -4026531841L, 288230376151711743L };
/*      */ 
/* 1690 */   static final long[] jjbitVec28 = { -3233808385L, 4611686017001275199L, 6908521828386340863L, 2295745090394464220L };
/*      */ 
/* 1693 */   static final long[] jjbitVec29 = { -9223372036854775808L, -9222809086900305919L, 1125895611875328L, 0L };
/*      */ 
/* 1696 */   static final long[] jjbitVec30 = { -2018811855607169916L, -4294966304L, 15L, 0L };
/*      */ 
/* 1699 */   static final long[] jjbitVec31 = { 2251241253188403424L, -2L, -4823449601L, -1L };
/*      */ 
/* 1702 */   static final long[] jjbitVec32 = { -527765581332512L, -1L, 72057589742993407L, -281474976710656L };
/*      */ 
/* 1705 */   static final long[] jjbitVec33 = { -1L, -1L, 18014398509481983L, 0L };
/*      */ 
/* 1708 */   static final long[] jjbitVec34 = { -1L, -1L, 274877906943L, 0L };
/*      */ 
/* 1711 */   static final long[] jjbitVec35 = { -1L, -1L, 8191L, 0L };
/*      */ 
/* 1714 */   static final long[] jjbitVec36 = { -1L, -1L, 68719476735L, 0L };
/*      */ 
/* 1717 */   static final long[] jjbitVec37 = { 6L, 0L, 0L, 0L };
/*      */ 
/* 1720 */   static final long[] jjbitVec38 = { -211106232532993L, 8796093022207L, 0L, 0L };
/*      */ 
/* 1723 */   static final long[] jjbitVec39 = { 6881498030004502655L, -37L, 1125899906842623L, -524288L };
/*      */ 
/* 1726 */   static final long[] jjbitVec40 = { 4611686018427387903L, -65536L, -196609L, 2305561534236983551L };
/*      */ 
/* 1729 */   static final long[] jjbitVec41 = { 6755399441055744L, -9286475208138752L, -1L, 2305843009213693951L };
/*      */ 
/* 1732 */   static final long[] jjbitVec42 = { -8646911293141286896L, -137304735746L, 9223372036854775807L, 425688104188L };
/*      */ 
/* 1735 */   static final long[] jjbitVec43 = { 0L, 0L, 297277419818057727L, -36028797027352577L };
/*      */ 
/* 1738 */   static final long[] jjbitVec44 = { -1L, 288511850608328703L, -17179879616L, 1134907106097332223L };
/*      */ 
/* 1741 */   static final long[] jjbitVec45 = { -1L, -1L, -901L, 234187180623233023L };
/*      */ 
/* 1744 */   static final long[] jjbitVec46 = { -562949953355777L, -8547991553L, -4899916411759099649L, 1979120929931286L };
/*      */ 
/* 1747 */   static final long[] jjbitVec47 = { 576460743717617679L, -65974959079425L, -1L, -6917531227739127809L };
/*      */ 
/* 1750 */   static final long[] jjbitVec48 = { -32768L, 59391L, 1125899906842623L, 0L };
/*      */ 
/* 1753 */   static final long[] jjbitVec49 = { -864691128455135234L, 281268803551231L, -881018876128026642L, 4503392135166367L };
/*      */ 
/* 1756 */   static final long[] jjbitVec50 = { -3211631683292264466L, 9006925953907079L, -869759877059461138L, 844214476815295L };
/*      */ 
/* 1759 */   static final long[] jjbitVec51 = { -869759877059600402L, 844165902514575L, -4341532606274353172L, 144396113305157063L };
/*      */ 
/* 1762 */   static final long[] jjbitVec52 = { -4327961440926441490L, 281212990012895L, -869196927105900564L, 281214063754719L };
/*      */ 
/* 1765 */   static final long[] jjbitVec53 = { -4323457841299070996L, 281212992110031L, 3457638613854978028L, 3377704004977791L };
/*      */ 
/* 1768 */   static final long[] jjbitVec54 = { -8646911284551352322L, 67076095L, 4323434403644581270L, 872365919L };
/*      */ 
/* 1771 */   static final long[] jjbitVec55 = { -4422530440275951615L, -554153860399361L, 2305843009196855263L, 64L };
/*      */ 
/* 1774 */   static final long[] jjbitVec56 = { 272457864671395839L, 67044351L, -4294967296L, 144115188075790399L };
/*      */ 
/* 1777 */   static final long[] jjbitVec57 = { -2160230401L, 1123701017804671L, -4294967296L, 9007199254740991L };
/*      */ 
/* 1780 */   static final long[] jjbitVec58 = { 9007194961862655L, 3905461007941631L, -1L, 4394700505087L };
/*      */ 
/* 1783 */   static final long[] jjbitVec59 = { -4227909632L, 72057594037927935L, 4398046511103L, 0L };
/*      */ 
/* 1786 */   static final long[] jjbitVec60 = { 1152657618058084351L, 8796093022207936L, 0L, 0L };
/*      */ 
/* 1789 */   static final long[] jjbitVec61 = { -9223235697412870144L, -9222531945545596927L, 1125895611875328L, 8667780808704L };
/*      */ 
/* 1792 */   static final long[] jjbitVec62 = { 2251518330118602976L, -2L, -4722786305L, -1L };
/*      */ 
/* 1795 */   static final long[] jjbitVec63 = { 4503599627370502L, 0L, 0L, 0L };
/*      */ 
/* 1798 */   static final long[] jjbitVec64 = { 3L, 0L, 0L, 0L };
/*      */ 
/* 1801 */   static final long[] jjbitVec65 = { 0L, 576460752303423488L, 0L, 0L };
/*      */ 
/* 1804 */   static final long[] jjbitVec66 = { 6881498031078244479L, -37L, 1125899906842623L, -524288L };
/*      */ 
/* 1807 */   static final long[] jjbitVec67 = { 6755463865630719L, -9286475208138752L, -1L, -6917529027641081857L };
/*      */ 
/* 1810 */   static final long[] jjbitVec68 = { -8646911293074243568L, -137304735746L, 9223372036854775807L, 1008806742219095292L };
/*      */ 
/* 3456 */   static final int[] jjnextStates = { 87, 88, 89, 95, 96, 98, 99, 103, 104, 79, 107, 108, 111, 112, 165, 166, 168, 193, 194, 201, 202, 152, 154, 178, 180, 191, 11, 15, 16, 17, 18, 28, 29, 35, 36, 43, 44, 53, 118, 119, 120, 121, 122, 123, 125, 127, 128, 67, 129, 131, 133, 136, 139, 55, 56, 57, 58, 59, 60, 62, 64, 65, 68, 67, 69, 71, 72, 80, 146, 147, 79, 97, 91, 94, 20, 21, 60, 62, 64, 73, 74, 81, 82, 83, 90, 91, 94, 103, 104, 79, 113, 114, 79, 123, 125, 127, 134, 135, 137, 138, 140, 142, 144, 161, 163, 165, 166, 168, 165, 166, 170, 168, 165, 166, 172, 168, 174, 176, 165, 166, 175, 168, 193, 194, 201, 202, 193, 194, 204, 201, 202, 151, 164, 5, 6, 75, 76, 77, 78, 84, 85, 92, 93, 100, 101, 105, 106, 109, 110, 115, 116, 148, 149, 155, 156, 158, 160, 167, 169, 171, 173, 181, 182, 184, 195, 203, 205 };
/*      */ 
/* 3667 */   public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "{", "}", "[", "]", "(", ")", "::", ":", ";", ",", "?", "...", "=", "*=", "/=", "%=", "+=", "-=", "<<=", ">>=", "&=", "^=", "|=", "||", "&&", "|", "^", "&", "==", "!=", "<", ">", "<=", ">=", "<<", ">>", "+", "-", "*", "/", "%", "++", "--", "~", "!", ".", "->", ".*", "->*", "auto", "break", "bool", "boolean", "case", "catch", "char", "const", "continue", "default", "delete", "do", "double", "else", "enum", "extern", "finally", "float", "for", "friend", "goto", "if", "inline", "int", "long", "new", "private", "protected", "public", "redeclared", "register", "return", "short", "signed", "sizeof", "static", "string", "struct", "class", "switch", "template", "this", "try", "typedef", "union", "unsigned", "virtual", "void", "volatile", "while", "operator", "true", "false", "throw", "color", "datetime", "static_cast", "dynamic_cast", "const_cast", "reinterpret_cast", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
/*      */ 
/* 3699 */   public static final String[] lexStateNames = { "DEFAULT", "DEFINE_STMT", "PROPERTY_STMT", "INCLUDE_STMT", "IMPORT_STMT", "LINE_NUMBER", "LINE_DIRECTIVE", "AFTER_LINE_DIRECTIVE", "IN_LINE_COMMENT", "IN_COMMENT", "PREPROCESSOR_OUTPUT" };
/*      */ 
/* 3714 */   public static final int[] jjnewLexState = { -1, -1, -1, -1, -1, 8, 9, 5, 5, 3, 4, 1, 2, 10, -1, 10, 0, -1, 10, 0, -1, 10, 0, -1, 10, 0, 6, 7, -1, 0, 0, -1, 0, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
/*      */ 
/* 3724 */   static final long[] jjtoToken = { -68719476735L, -1L, 1980220441624575L };
/*      */ 
/* 3727 */   static final long[] jjtoSkip = { 23622320126L, 0L, 0L };
/*      */ 
/* 3730 */   static final long[] jjtoMore = { 45097156608L, 0L, 0L };
/*      */   protected static JavaCharStream input_stream;
/* 3734 */   private static final int[] jjrounds = new int['Ò'];
/* 3735 */   private static final int[] jjstateSet = new int[420];
/* 3736 */   private static final StringBuilder jjimage = new StringBuilder();
/* 3737 */   private static StringBuilder image = jjimage;
/*      */   private static int jjimageLen;
/*      */   private static int lengthOfMatch;
/*      */   protected static char curChar;
/* 3810 */   static int curLexState = 0;
/* 3811 */   static int defaultLexState = 0;
/*      */   static int jjnewStateCnt;
/*      */   static int jjround;
/*      */   static int jjmatchedPos;
/*      */   static int jjmatchedKind;
/*      */ 
/*      */   static void resetBeginLineCol()
/*      */   {
/*      */   }
/*      */ 
/*      */   public static void setDebugStream(PrintStream ds)
/*      */   {
/*   23 */     debugStream = ds;
/*      */   }
/*      */   private static int jjMoveStringLiteralDfa0_4() {
/*   26 */     return jjMoveNfa_4(1, 0);
/*      */   }
/*      */ 
/*      */   private static int jjMoveNfa_4(int startState, int curPos)
/*      */   {
/*   36 */     int startsAt = 0;
/*   37 */     jjnewStateCnt = 5;
/*   38 */     int i = 1;
/*   39 */     jjstateSet[0] = startState;
/*   40 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/*   43 */       if (++jjround == 2147483647)
/*   44 */         ReInitRounds();
/*   45 */       if (curChar < '@')
/*      */       {
/*   47 */         long l = 1L << curChar;
/*      */         do
/*      */         {
/*   50 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 1:
/*   53 */             if ((0xFFFFDBFF & l) != 0L)
/*      */             {
/*   55 */               if (kind > 23)
/*   56 */                 kind = 23;
/*   57 */               jjCheckNAdd(0);
/*      */             }
/*   59 */             else if ((0x2400 & l) != 0L)
/*      */             {
/*   61 */               if (kind > 25)
/*   62 */                 kind = 25;
/*      */             }
/*   64 */             if ((0x0 & l) != 0L)
/*      */             {
/*   66 */               if (kind <= 24) continue;
/*   67 */               kind = 24;
/*      */             } else {
/*   69 */               if (curChar != '\r') continue;
/*   70 */               jjstateSet[(jjnewStateCnt++)] = 3; } break;
/*      */           case 0:
/*   73 */             if ((0xFFFFDBFF & l) == 0L)
/*      */               continue;
/*   75 */             if (kind > 23)
/*   76 */               kind = 23;
/*   77 */             jjCheckNAdd(0);
/*   78 */             break;
/*      */           case 2:
/*   80 */             if (((0x2400 & l) == 0L) || (kind <= 25)) continue;
/*   81 */             kind = 25; break;
/*      */           case 3:
/*   84 */             if ((curChar != '\n') || (kind <= 25)) continue;
/*   85 */             kind = 25; break;
/*      */           case 4:
/*   88 */             if (curChar != '\r') continue;
/*   89 */             jjstateSet[(jjnewStateCnt++)] = 3;
/*      */           }
/*      */         }
/*      */ 
/*   93 */         while (i != startsAt);
/*      */       }
/*   95 */       else if (curChar < '')
/*      */       {
/*   97 */         long l = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  100 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*      */           case 1:
/*  104 */             kind = 23;
/*  105 */             jjCheckNAdd(0);
/*      */           }
/*      */         }
/*      */ 
/*  109 */         while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/*  113 */         int hiByte = curChar >> '\b';
/*  114 */         int i1 = hiByte >> 6;
/*  115 */         long l1 = 1L << (hiByte & 0x3F);
/*  116 */         int i2 = (curChar & 0xFF) >> '\006';
/*  117 */         long l2 = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  120 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*      */           case 1:
/*  124 */             if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  126 */             if (kind > 23)
/*  127 */               kind = 23;
/*  128 */             jjCheckNAdd(0);
/*      */           }
/*      */         }
/*      */ 
/*  132 */         while (i != startsAt);
/*      */       }
/*  134 */       if (kind != 2147483647)
/*      */       {
/*  136 */         jjmatchedKind = kind;
/*  137 */         jjmatchedPos = curPos;
/*  138 */         kind = 2147483647;
/*      */       }
/*  140 */       curPos++;
/*  141 */       if ((i = jjnewStateCnt) == (startsAt = 5 - (CPPParserTokenManager.jjnewStateCnt = startsAt)))
/*  142 */         return curPos; try {
/*  143 */         curChar = JavaCharStream.readChar(); } catch (IOException e) {
/*      */       }
/*  144 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa0_1()
/*      */   {
/*  149 */     return jjMoveNfa_1(1, 0);
/*      */   }
/*      */ 
/*      */   private static int jjMoveNfa_1(int startState, int curPos) {
/*  153 */     int startsAt = 0;
/*  154 */     jjnewStateCnt = 5;
/*  155 */     int i = 1;
/*  156 */     jjstateSet[0] = startState;
/*  157 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/*  160 */       if (++jjround == 2147483647)
/*  161 */         ReInitRounds();
/*  162 */       if (curChar < '@')
/*      */       {
/*  164 */         long l = 1L << curChar;
/*      */         do
/*      */         {
/*  167 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 1:
/*  170 */             if ((0xFFFFDBFF & l) != 0L)
/*      */             {
/*  172 */               if (kind > 14)
/*  173 */                 kind = 14;
/*  174 */               jjCheckNAdd(0);
/*      */             }
/*  176 */             else if ((0x2400 & l) != 0L)
/*      */             {
/*  178 */               if (kind > 16)
/*  179 */                 kind = 16;
/*      */             }
/*  181 */             if ((0x200 & l) != 0L)
/*      */             {
/*  183 */               if (kind <= 15) continue;
/*  184 */               kind = 15;
/*      */             } else {
/*  186 */               if (curChar != '\r') continue;
/*  187 */               jjstateSet[(jjnewStateCnt++)] = 3; } break;
/*      */           case 0:
/*  190 */             if ((0xFFFFDBFF & l) == 0L)
/*      */               continue;
/*  192 */             if (kind > 14)
/*  193 */               kind = 14;
/*  194 */             jjCheckNAdd(0);
/*  195 */             break;
/*      */           case 2:
/*  197 */             if (((0x2400 & l) == 0L) || (kind <= 16)) continue;
/*  198 */             kind = 16; break;
/*      */           case 3:
/*  201 */             if ((curChar != '\n') || (kind <= 16)) continue;
/*  202 */             kind = 16; break;
/*      */           case 4:
/*  205 */             if (curChar != '\r') continue;
/*  206 */             jjstateSet[(jjnewStateCnt++)] = 3;
/*      */           }
/*      */         }
/*      */ 
/*  210 */         while (i != startsAt);
/*      */       }
/*  212 */       else if (curChar < '')
/*      */       {
/*  214 */         long l = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  217 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*      */           case 1:
/*  221 */             kind = 14;
/*  222 */             jjCheckNAdd(0);
/*      */           }
/*      */         }
/*      */ 
/*  226 */         while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/*  230 */         int hiByte = curChar >> '\b';
/*  231 */         int i1 = hiByte >> 6;
/*  232 */         long l1 = 1L << (hiByte & 0x3F);
/*  233 */         int i2 = (curChar & 0xFF) >> '\006';
/*  234 */         long l2 = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  237 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*      */           case 1:
/*  241 */             if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  243 */             if (kind > 14)
/*  244 */               kind = 14;
/*  245 */             jjCheckNAdd(0);
/*      */           }
/*      */         }
/*      */ 
/*  249 */         while (i != startsAt);
/*      */       }
/*  251 */       if (kind != 2147483647)
/*      */       {
/*  253 */         jjmatchedKind = kind;
/*  254 */         jjmatchedPos = curPos;
/*  255 */         kind = 2147483647;
/*      */       }
/*  257 */       curPos++;
/*  258 */       if ((i = jjnewStateCnt) == (startsAt = 5 - (CPPParserTokenManager.jjnewStateCnt = startsAt)))
/*  259 */         return curPos; try {
/*  260 */         curChar = JavaCharStream.readChar(); } catch (IOException e) {
/*      */       }
/*  261 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa0_5()
/*      */   {
/*  266 */     return jjMoveNfa_5(0, 0);
/*      */   }
/*      */ 
/*      */   private static int jjMoveNfa_5(int startState, int curPos) {
/*  270 */     int startsAt = 0;
/*  271 */     jjnewStateCnt = 1;
/*  272 */     int i = 1;
/*  273 */     jjstateSet[0] = startState;
/*  274 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/*  277 */       if (++jjround == 2147483647)
/*  278 */         ReInitRounds();
/*  279 */       if (curChar < '@')
/*      */       {
/*  281 */         long l = 1L << curChar;
/*      */         do
/*      */         {
/*  284 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*  287 */             if ((0x0 & l) == 0L)
/*      */               continue;
/*  289 */             kind = 26;
/*  290 */             jjstateSet[(jjnewStateCnt++)] = 0;
/*      */           }
/*      */         }
/*      */ 
/*  294 */         while (i != startsAt);
/*      */       }
/*  296 */       else if (curChar < '')
/*      */       {
/*  298 */         long l = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  301 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           }
/*      */         }
/*  305 */         while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/*  309 */         int hiByte = curChar >> '\b';
/*  310 */         int i1 = hiByte >> 6;
/*  311 */         long l1 = 1L << (hiByte & 0x3F);
/*  312 */         int i2 = (curChar & 0xFF) >> '\006';
/*  313 */         long l2 = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  316 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           }
/*      */         }
/*  320 */         while (i != startsAt);
/*      */       }
/*  322 */       if (kind != 2147483647)
/*      */       {
/*  324 */         jjmatchedKind = kind;
/*  325 */         jjmatchedPos = curPos;
/*  326 */         kind = 2147483647;
/*      */       }
/*  328 */       curPos++;
/*  329 */       if ((i = jjnewStateCnt) == (startsAt = 1 - (CPPParserTokenManager.jjnewStateCnt = startsAt)))
/*  330 */         return curPos; try {
/*  331 */         curChar = JavaCharStream.readChar(); } catch (IOException e) {
/*      */       }
/*  332 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa0_6()
/*      */   {
/*  337 */     return jjMoveNfa_6(0, 0);
/*      */   }
/*      */ 
/*      */   private static int jjMoveNfa_6(int startState, int curPos) {
/*  341 */     int startsAt = 0;
/*  342 */     jjnewStateCnt = 3;
/*  343 */     int i = 1;
/*  344 */     jjstateSet[0] = startState;
/*  345 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/*  348 */       if (++jjround == 2147483647)
/*  349 */         ReInitRounds();
/*  350 */       if (curChar < '@')
/*      */       {
/*  352 */         long l = 1L << curChar;
/*      */         do
/*      */         {
/*  355 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*  358 */             if ((0x2400 & l) != 0L)
/*      */             {
/*  360 */               if (kind > 27)
/*  361 */                 kind = 27;
/*      */             }
/*  363 */             if (curChar != '\r') continue;
/*  364 */             jjstateSet[(jjnewStateCnt++)] = 1; break;
/*      */           case 1:
/*  367 */             if ((curChar != '\n') || (kind <= 27)) continue;
/*  368 */             kind = 27; break;
/*      */           case 2:
/*  371 */             if (curChar != '\r') continue;
/*  372 */             jjstateSet[(jjnewStateCnt++)] = 1;
/*      */           }
/*      */         }
/*      */ 
/*  376 */         while (i != startsAt);
/*      */       }
/*  378 */       else if (curChar < '')
/*      */       {
/*  380 */         long l = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  383 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           }
/*      */         }
/*  387 */         while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/*  391 */         int hiByte = curChar >> '\b';
/*  392 */         int i1 = hiByte >> 6;
/*  393 */         long l1 = 1L << (hiByte & 0x3F);
/*  394 */         int i2 = (curChar & 0xFF) >> '\006';
/*  395 */         long l2 = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  398 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           }
/*      */         }
/*  402 */         while (i != startsAt);
/*      */       }
/*  404 */       if (kind != 2147483647)
/*      */       {
/*  406 */         jjmatchedKind = kind;
/*  407 */         jjmatchedPos = curPos;
/*  408 */         kind = 2147483647;
/*      */       }
/*  410 */       curPos++;
/*  411 */       if ((i = jjnewStateCnt) == (startsAt = 3 - (CPPParserTokenManager.jjnewStateCnt = startsAt)))
/*  412 */         return curPos; try {
/*  413 */         curChar = JavaCharStream.readChar(); } catch (IOException e) {
/*      */       }
/*  414 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa0_3()
/*      */   {
/*  419 */     return jjMoveNfa_3(1, 0);
/*      */   }
/*      */ 
/*      */   private static int jjMoveNfa_3(int startState, int curPos) {
/*  423 */     int startsAt = 0;
/*  424 */     jjnewStateCnt = 5;
/*  425 */     int i = 1;
/*  426 */     jjstateSet[0] = startState;
/*  427 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/*  430 */       if (++jjround == 2147483647)
/*  431 */         ReInitRounds();
/*  432 */       if (curChar < '@')
/*      */       {
/*  434 */         long l = 1L << curChar;
/*      */         do
/*      */         {
/*  437 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 1:
/*  440 */             if ((0xFFFFDBFF & l) != 0L)
/*      */             {
/*  442 */               if (kind > 20)
/*  443 */                 kind = 20;
/*  444 */               jjCheckNAdd(0);
/*      */             }
/*  446 */             else if ((0x2400 & l) != 0L)
/*      */             {
/*  448 */               if (kind > 22)
/*  449 */                 kind = 22;
/*      */             }
/*  451 */             else if ((0x0 & l) != 0L)
/*      */             {
/*  453 */               if (kind > 21)
/*  454 */                 kind = 21;
/*      */             }
/*  456 */             if (curChar != '\r') continue;
/*  457 */             jjstateSet[(jjnewStateCnt++)] = 3; break;
/*      */           case 0:
/*  460 */             if ((0xFFFFDBFF & l) == 0L)
/*      */               continue;
/*  462 */             kind = 20;
/*  463 */             jjCheckNAdd(0);
/*  464 */             break;
/*      */           case 2:
/*  466 */             if (((0x2400 & l) == 0L) || (kind <= 22)) continue;
/*  467 */             kind = 22; break;
/*      */           case 3:
/*  470 */             if ((curChar != '\n') || (kind <= 22)) continue;
/*  471 */             kind = 22; break;
/*      */           case 4:
/*  474 */             if (curChar != '\r') continue;
/*  475 */             jjstateSet[(jjnewStateCnt++)] = 3;
/*      */           }
/*      */         }
/*      */ 
/*  479 */         while (i != startsAt);
/*      */       }
/*  481 */       else if (curChar < '')
/*      */       {
/*  483 */         long l = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  486 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*      */           case 1:
/*  490 */             kind = 20;
/*  491 */             jjCheckNAdd(0);
/*      */           }
/*      */         }
/*      */ 
/*  495 */         while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/*  499 */         int hiByte = curChar >> '\b';
/*  500 */         int i1 = hiByte >> 6;
/*  501 */         long l1 = 1L << (hiByte & 0x3F);
/*  502 */         int i2 = (curChar & 0xFF) >> '\006';
/*  503 */         long l2 = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  506 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*      */           case 1:
/*  510 */             if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */               continue;
/*  512 */             if (kind > 20)
/*  513 */               kind = 20;
/*  514 */             jjCheckNAdd(0);
/*      */           }
/*      */         }
/*      */ 
/*  518 */         while (i != startsAt);
/*      */       }
/*  520 */       if (kind != 2147483647)
/*      */       {
/*  522 */         jjmatchedKind = kind;
/*  523 */         jjmatchedPos = curPos;
/*  524 */         kind = 2147483647;
/*      */       }
/*  526 */       curPos++;
/*  527 */       if ((i = jjnewStateCnt) == (startsAt = 5 - (CPPParserTokenManager.jjnewStateCnt = startsAt)))
/*  528 */         return curPos; try {
/*  529 */         curChar = JavaCharStream.readChar(); } catch (IOException e) {
/*      */       }
/*  530 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa0_8()
/*      */   {
/*  535 */     return jjMoveNfa_8(0, 0);
/*      */   }
/*      */ 
/*      */   private static int jjMoveNfa_8(int startState, int curPos) {
/*  539 */     int startsAt = 0;
/*  540 */     jjnewStateCnt = 3;
/*  541 */     int i = 1;
/*  542 */     jjstateSet[0] = startState;
/*  543 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/*  546 */       if (++jjround == 2147483647)
/*  547 */         ReInitRounds();
/*  548 */       if (curChar < '@')
/*      */       {
/*  550 */         long l = 1L << curChar;
/*      */         do
/*      */         {
/*  553 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*  556 */             if ((0x2400 & l) != 0L)
/*      */             {
/*  558 */               if (kind > 30)
/*  559 */                 kind = 30;
/*      */             }
/*  561 */             if (curChar != '\r') continue;
/*  562 */             jjstateSet[(jjnewStateCnt++)] = 1; break;
/*      */           case 1:
/*  565 */             if ((curChar != '\n') || (kind <= 30)) continue;
/*  566 */             kind = 30; break;
/*      */           case 2:
/*  569 */             if (curChar != '\r') continue;
/*  570 */             jjstateSet[(jjnewStateCnt++)] = 1;
/*      */           }
/*      */         }
/*      */ 
/*  574 */         while (i != startsAt);
/*      */       }
/*  576 */       else if (curChar < '')
/*      */       {
/*  578 */         long l = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  581 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           }
/*      */         }
/*  585 */         while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/*  589 */         int hiByte = curChar >> '\b';
/*  590 */         int i1 = hiByte >> 6;
/*  591 */         long l1 = 1L << (hiByte & 0x3F);
/*  592 */         int i2 = (curChar & 0xFF) >> '\006';
/*  593 */         long l2 = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/*  596 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           }
/*      */         }
/*  600 */         while (i != startsAt);
/*      */       }
/*  602 */       if (kind != 2147483647)
/*      */       {
/*  604 */         jjmatchedKind = kind;
/*  605 */         jjmatchedPos = curPos;
/*  606 */         kind = 2147483647;
/*      */       }
/*  608 */       curPos++;
/*  609 */       if ((i = jjnewStateCnt) == (startsAt = 3 - (CPPParserTokenManager.jjnewStateCnt = startsAt)))
/*  610 */         return curPos; try {
/*  611 */         curChar = JavaCharStream.readChar(); } catch (IOException e) {
/*      */       }
/*  612 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private static final int jjStopStringLiteralDfa_0(int pos, long active0, long active1, long active2)
/*      */   {
/*  617 */     switch (pos)
/*      */     {
/*      */     case 0:
/*  620 */       if (((active1 & 0xC0000000) != 0L) || ((active2 & 0x5000) != 0L))
/*      */       {
/*  622 */         jjmatchedKind = 164;
/*  623 */         return 0;
/*      */       }
/*  625 */       if (((active0 & 0x0) != 0L) || ((active1 & 0xA0000) != 0L))
/*  626 */         return 210;
/*  627 */       if (((active1 & 0x3E000000) != 0L) || ((active2 & 0x8800) != 0L))
/*      */       {
/*  629 */         jjmatchedKind = 164;
/*  630 */         return 4;
/*      */       }
/*  632 */       if (((active1 & 0x1E00000) != 0L) || ((active2 & 0x127FF) != 0L))
/*      */       {
/*  634 */         jjmatchedKind = 164;
/*  635 */         return 211;
/*      */       }
/*  637 */       if ((active0 & 0x2000) != 0L)
/*  638 */         return 212;
/*  639 */       return -1;
/*      */     case 1:
/*  641 */       if ((active1 & 0x0) != 0L)
/*  642 */         return 211;
/*  643 */       if (((active1 & 0xFFE00000) != 0L) || ((active2 & 0x1FFFF) != 0L))
/*      */       {
/*  645 */         if (jjmatchedPos != 1)
/*      */         {
/*  647 */           jjmatchedKind = 164;
/*  648 */           jjmatchedPos = 1;
/*      */         }
/*  650 */         return 211;
/*      */       }
/*  652 */       return -1;
/*      */     case 2:
/*  654 */       if ((active1 & 0x0) != 0L)
/*  655 */         return 211;
/*  656 */       if (((active1 & 0xFFE00000) != 0L) || ((active2 & 0x1FFFF) != 0L))
/*      */       {
/*  658 */         jjmatchedKind = 164;
/*  659 */         jjmatchedPos = 2;
/*  660 */         return 211;
/*      */       }
/*  662 */       return -1;
/*      */     case 3:
/*  664 */       if (((active1 & 0xBA00000) != 0L) || ((active2 & 0x110) != 0L))
/*  665 */         return 211;
/*  666 */       if (((active1 & 0xF4400000) != 0L) || ((active2 & 0x1FEEF) != 0L))
/*      */       {
/*  668 */         if (jjmatchedPos != 3)
/*      */         {
/*  670 */           jjmatchedKind = 164;
/*  671 */           jjmatchedPos = 3;
/*      */         }
/*  673 */         return 211;
/*      */       }
/*  675 */       return -1;
/*      */     case 4:
/*  677 */       if (((active1 & 0x14400000) != 0L) || ((active2 & 0x8E42) != 0L))
/*  678 */         return 211;
/*  679 */       if (((active1 & 0xE1000000) != 0L) || ((active2 & 0x170AD) != 0L))
/*      */       {
/*  681 */         if (jjmatchedPos != 4)
/*      */         {
/*  683 */           jjmatchedKind = 164;
/*  684 */           jjmatchedPos = 4;
/*      */         }
/*  686 */         return 211;
/*      */       }
/*  688 */       return -1;
/*      */     case 5:
/*  690 */       if (((active1 & 0x61000000) != 0L) || ((active2 & 0x1D0AD) != 0L))
/*      */       {
/*  692 */         if (jjmatchedPos != 5)
/*      */         {
/*  694 */           jjmatchedKind = 164;
/*  695 */           jjmatchedPos = 5;
/*      */         }
/*  697 */         return 211;
/*      */       }
/*  699 */       if (((active1 & 0x80000000) != 0L) || ((active2 & 0x2000) != 0L))
/*  700 */         return 211;
/*  701 */       return -1;
/*      */     case 6:
/*  703 */       if (((active1 & 0x41000000) != 0L) || ((active2 & 0x9) != 0L))
/*  704 */         return 211;
/*  705 */       if (((active1 & 0x20000000) != 0L) || ((active2 & 0x1F0A4) != 0L))
/*      */       {
/*  707 */         jjmatchedKind = 164;
/*  708 */         jjmatchedPos = 6;
/*  709 */         return 211;
/*      */       }
/*  711 */       return -1;
/*      */     case 7:
/*  713 */       if (((active1 & 0x0) != 0L) || ((active2 & 0x1E000) != 0L))
/*      */       {
/*  715 */         jjmatchedKind = 164;
/*  716 */         jjmatchedPos = 7;
/*  717 */         return 211;
/*      */       }
/*  719 */       if (((active1 & 0x20000000) != 0L) || ((active2 & 0x10A4) != 0L))
/*  720 */         return 211;
/*  721 */       return -1;
/*      */     case 8:
/*  723 */       if ((active1 & 0x0) != 0L)
/*  724 */         return 211;
/*  725 */       if (((active1 & 0x0) != 0L) || ((active2 & 0x1E000) != 0L))
/*      */       {
/*  727 */         jjmatchedKind = 164;
/*  728 */         jjmatchedPos = 8;
/*  729 */         return 211;
/*      */       }
/*  731 */       return -1;
/*      */     case 9:
/*  733 */       if (((active1 & 0x0) != 0L) || ((active2 & 0x8000) != 0L))
/*  734 */         return 211;
/*  735 */       if ((active2 & 0x16000) != 0L)
/*      */       {
/*  737 */         jjmatchedKind = 164;
/*  738 */         jjmatchedPos = 9;
/*  739 */         return 211;
/*      */       }
/*  741 */       return -1;
/*      */     case 10:
/*  743 */       if ((active2 & 0x2000) != 0L)
/*  744 */         return 211;
/*  745 */       if ((active2 & 0x14000) != 0L)
/*      */       {
/*  747 */         jjmatchedKind = 164;
/*  748 */         jjmatchedPos = 10;
/*  749 */         return 211;
/*      */       }
/*  751 */       return -1;
/*      */     case 11:
/*  753 */       if ((active2 & 0x4000) != 0L)
/*  754 */         return 211;
/*  755 */       if ((active2 & 0x10000) != 0L)
/*      */       {
/*  757 */         jjmatchedKind = 164;
/*  758 */         jjmatchedPos = 11;
/*  759 */         return 211;
/*      */       }
/*  761 */       return -1;
/*      */     case 12:
/*  763 */       if ((active2 & 0x10000) != 0L)
/*      */       {
/*  765 */         jjmatchedKind = 164;
/*  766 */         jjmatchedPos = 12;
/*  767 */         return 211;
/*      */       }
/*  769 */       return -1;
/*      */     case 13:
/*  771 */       if ((active2 & 0x10000) != 0L)
/*      */       {
/*  773 */         jjmatchedKind = 164;
/*  774 */         jjmatchedPos = 13;
/*  775 */         return 211;
/*      */       }
/*  777 */       return -1;
/*      */     case 14:
/*  779 */       if ((active2 & 0x10000) != 0L)
/*      */       {
/*  781 */         jjmatchedKind = 164;
/*  782 */         jjmatchedPos = 14;
/*  783 */         return 211;
/*      */       }
/*  785 */       return -1;
/*      */     }
/*  787 */     return -1;
/*      */   }
/*      */ 
/*      */   private static final int jjStartNfa_0(int pos, long active0, long active1, long active2)
/*      */   {
/*  792 */     return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1, active2), pos + 1);
/*      */   }
/*      */ 
/*      */   private static int jjStopAtPos(int pos, int kind) {
/*  796 */     jjmatchedKind = kind;
/*  797 */     jjmatchedPos = pos;
/*  798 */     return pos + 1;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa0_0() {
/*  802 */     switch (curChar)
/*      */     {
/*      */     case '!':
/*  805 */       jjmatchedKind = 80;
/*  806 */       return jjMoveStringLiteralDfa1_0(0L, 2L, 0L);
/*      */     case '#':
/*  808 */       return jjStartNfaWithStates_0(0, 13, 212);
/*      */     case '%':
/*  810 */       jjmatchedKind = 76;
/*  811 */       return jjMoveStringLiteralDfa1_0(2251799813685248L, 0L, 0L);
/*      */     case '&':
/*  813 */       jjmatchedKind = 63;
/*  814 */       return jjMoveStringLiteralDfa1_0(1224979098644774912L, 0L, 0L);
/*      */     case '(':
/*  816 */       return jjStopAtPos(0, 40);
/*      */     case ')':
/*  818 */       return jjStopAtPos(0, 41);
/*      */     case '*':
/*  820 */       jjmatchedKind = 74;
/*  821 */       return jjMoveStringLiteralDfa1_0(562949953421312L, 0L, 0L);
/*      */     case '+':
/*  823 */       jjmatchedKind = 72;
/*  824 */       return jjMoveStringLiteralDfa1_0(4503599627370496L, 8192L, 0L);
/*      */     case ',':
/*  826 */       return jjStopAtPos(0, 45);
/*      */     case '-':
/*  828 */       jjmatchedKind = 73;
/*  829 */       return jjMoveStringLiteralDfa1_0(9007199254740992L, 1327104L, 0L);
/*      */     case '.':
/*  831 */       jjmatchedKind = 81;
/*  832 */       return jjMoveStringLiteralDfa1_0(140737488355328L, 524288L, 0L);
/*      */     case '/':
/*  834 */       jjmatchedKind = 75;
/*  835 */       return jjMoveStringLiteralDfa1_0(1125899906842720L, 0L, 0L);
/*      */     case ':':
/*  837 */       jjmatchedKind = 43;
/*  838 */       return jjMoveStringLiteralDfa1_0(4398046511104L, 0L, 0L);
/*      */     case ';':
/*  840 */       return jjStopAtPos(0, 44);
/*      */     case '<':
/*  842 */       jjmatchedKind = 66;
/*  843 */       return jjMoveStringLiteralDfa1_0(18014398509481984L, 80L, 0L);
/*      */     case '=':
/*  845 */       jjmatchedKind = 48;
/*  846 */       return jjMoveStringLiteralDfa1_0(0L, 1L, 0L);
/*      */     case '>':
/*  848 */       jjmatchedKind = 67;
/*  849 */       return jjMoveStringLiteralDfa1_0(36028797018963968L, 160L, 0L);
/*      */     case '?':
/*  851 */       return jjStopAtPos(0, 46);
/*      */     case '[':
/*  853 */       return jjStopAtPos(0, 38);
/*      */     case ']':
/*  855 */       return jjStopAtPos(0, 39);
/*      */     case '^':
/*  857 */       jjmatchedKind = 62;
/*  858 */       return jjMoveStringLiteralDfa1_0(144115188075855872L, 0L, 0L);
/*      */     case 'a':
/*  860 */       return jjMoveStringLiteralDfa1_0(0L, 2097152L, 0L);
/*      */     case 'b':
/*  862 */       return jjMoveStringLiteralDfa1_0(0L, 29360128L, 0L);
/*      */     case 'c':
/*  864 */       return jjMoveStringLiteralDfa1_0(0L, 576460753343610880L, 34816L);
/*      */     case 'd':
/*  866 */       return jjMoveStringLiteralDfa1_0(0L, 16106127360L, 20480L);
/*      */     case 'e':
/*  868 */       return jjMoveStringLiteralDfa1_0(0L, 120259084288L, 0L);
/*      */     case 'f':
/*  870 */       return jjMoveStringLiteralDfa1_0(0L, 2061584302080L, 512L);
/*      */     case 'g':
/*  872 */       return jjMoveStringLiteralDfa1_0(0L, 2199023255552L, 0L);
/*      */     case 'i':
/*  874 */       return jjMoveStringLiteralDfa1_0(0L, 30786325577728L, 0L);
/*      */     case 'l':
/*  876 */       return jjMoveStringLiteralDfa1_0(0L, 35184372088832L, 0L);
/*      */     case 'n':
/*  878 */       return jjMoveStringLiteralDfa1_0(0L, 70368744177664L, 0L);
/*      */     case 'o':
/*  880 */       return jjMoveStringLiteralDfa1_0(0L, 0L, 128L);
/*      */     case 'p':
/*  882 */       return jjMoveStringLiteralDfa1_0(0L, 985162418487296L, 0L);
/*      */     case 'r':
/*  884 */       return jjMoveStringLiteralDfa1_0(0L, 7881299347898368L, 65536L);
/*      */     case 's':
/*  886 */       return jjMoveStringLiteralDfa1_0(0L, 1720375057655529472L, 8192L);
/*      */     case 't':
/*  888 */       return jjMoveStringLiteralDfa1_0(0L, -2305843009213693952L, 1281L);
/*      */     case 'u':
/*  890 */       return jjMoveStringLiteralDfa1_0(0L, 0L, 6L);
/*      */     case 'v':
/*  892 */       return jjMoveStringLiteralDfa1_0(0L, 0L, 56L);
/*      */     case 'w':
/*  894 */       return jjMoveStringLiteralDfa1_0(0L, 0L, 64L);
/*      */     case '{':
/*  896 */       return jjStopAtPos(0, 36);
/*      */     case '|':
/*  898 */       jjmatchedKind = 61;
/*  899 */       return jjMoveStringLiteralDfa1_0(864691128455135232L, 0L, 0L);
/*      */     case '}':
/*  901 */       return jjStopAtPos(0, 37);
/*      */     case '~':
/*  903 */       return jjStopAtPos(0, 79);
/*      */     case '"':
/*      */     case '$':
/*      */     case '\'':
/*      */     case '0':
/*      */     case '1':
/*      */     case '2':
/*      */     case '3':
/*      */     case '4':
/*      */     case '5':
/*      */     case '6':
/*      */     case '7':
/*      */     case '8':
/*      */     case '9':
/*      */     case '@':
/*      */     case 'A':
/*      */     case 'B':
/*      */     case 'C':
/*      */     case 'D':
/*      */     case 'E':
/*      */     case 'F':
/*      */     case 'G':
/*      */     case 'H':
/*      */     case 'I':
/*      */     case 'J':
/*      */     case 'K':
/*      */     case 'L':
/*      */     case 'M':
/*      */     case 'N':
/*      */     case 'O':
/*      */     case 'P':
/*      */     case 'Q':
/*      */     case 'R':
/*      */     case 'S':
/*      */     case 'T':
/*      */     case 'U':
/*      */     case 'V':
/*      */     case 'W':
/*      */     case 'X':
/*      */     case 'Y':
/*      */     case 'Z':
/*      */     case '\\':
/*      */     case '_':
/*      */     case '`':
/*      */     case 'h':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'm':
/*      */     case 'q':
/*      */     case 'x':
/*      */     case 'y':
/*  905 */     case 'z': } return jjMoveNfa_0(3, 0);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa1_0(long active0, long active1, long active2) {
/*      */     try {
/*  910 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/*  912 */       jjStopStringLiteralDfa_0(0, active0, active1, active2);
/*  913 */       return 1;
/*      */     }
/*  915 */     switch (curChar)
/*      */     {
/*      */     case '&':
/*  918 */       if ((active0 & 0x0) == 0L) break;
/*  919 */       return jjStopAtPos(1, 60);
/*      */     case '*':
/*  922 */       if ((active0 & 0x40) != 0L)
/*  923 */         return jjStopAtPos(1, 6);
/*  924 */       if ((active1 & 0x80000) == 0L) break;
/*  925 */       return jjStopAtPos(1, 83);
/*      */     case '+':
/*  928 */       if ((active1 & 0x2000) == 0L) break;
/*  929 */       return jjStopAtPos(1, 77);
/*      */     case '-':
/*  932 */       if ((active1 & 0x4000) == 0L) break;
/*  933 */       return jjStopAtPos(1, 78);
/*      */     case '.':
/*  936 */       return jjMoveStringLiteralDfa2_0(active0, 140737488355328L, active1, 0L, active2, 0L);
/*      */     case '/':
/*  938 */       if ((active0 & 0x20) == 0L) break;
/*  939 */       return jjStopAtPos(1, 5);
/*      */     case ':':
/*  942 */       if ((active0 & 0x0) == 0L) break;
/*  943 */       return jjStopAtPos(1, 42);
/*      */     case '<':
/*  946 */       if ((active1 & 0x40) != 0L)
/*      */       {
/*  948 */         jjmatchedKind = 70;
/*  949 */         jjmatchedPos = 1;
/*      */       }
/*  951 */       return jjMoveStringLiteralDfa2_0(active0, 18014398509481984L, active1, 0L, active2, 0L);
/*      */     case '=':
/*  953 */       if ((active0 & 0x0) != 0L)
/*  954 */         return jjStopAtPos(1, 49);
/*  955 */       if ((active0 & 0x0) != 0L)
/*  956 */         return jjStopAtPos(1, 50);
/*  957 */       if ((active0 & 0x0) != 0L)
/*  958 */         return jjStopAtPos(1, 51);
/*  959 */       if ((active0 & 0x0) != 0L)
/*  960 */         return jjStopAtPos(1, 52);
/*  961 */       if ((active0 & 0x0) != 0L)
/*  962 */         return jjStopAtPos(1, 53);
/*  963 */       if ((active0 & 0x0) != 0L)
/*  964 */         return jjStopAtPos(1, 56);
/*  965 */       if ((active0 & 0x0) != 0L)
/*  966 */         return jjStopAtPos(1, 57);
/*  967 */       if ((active0 & 0x0) != 0L)
/*  968 */         return jjStopAtPos(1, 58);
/*  969 */       if ((active1 & 1L) != 0L)
/*  970 */         return jjStopAtPos(1, 64);
/*  971 */       if ((active1 & 0x2) != 0L)
/*  972 */         return jjStopAtPos(1, 65);
/*  973 */       if ((active1 & 0x10) != 0L)
/*  974 */         return jjStopAtPos(1, 68);
/*  975 */       if ((active1 & 0x20) == 0L) break;
/*  976 */       return jjStopAtPos(1, 69);
/*      */     case '>':
/*  979 */       if ((active1 & 0x80) != 0L)
/*      */       {
/*  981 */         jjmatchedKind = 71;
/*  982 */         jjmatchedPos = 1;
/*      */       }
/*  984 */       else if ((active1 & 0x40000) != 0L)
/*      */       {
/*  986 */         jjmatchedKind = 82;
/*  987 */         jjmatchedPos = 1;
/*      */       }
/*  989 */       return jjMoveStringLiteralDfa2_0(active0, 36028797018963968L, active1, 1048576L, active2, 0L);
/*      */     case 'a':
/*  991 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 100663296L, active2, 4608L);
/*      */     case 'e':
/*  993 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 2313794680526995456L, active2, 65536L);
/*      */     case 'f':
/*  995 */       if ((active1 & 0x0) == 0L) break;
/*  996 */       return jjStartNfaWithStates_0(1, 106, 211);
/*      */     case 'h':
/*  999 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 4620693217816346624L, active2, 1088L);
/*      */     case 'i':
/* 1001 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 54043332967399424L, active2, 8L);
/*      */     case 'l':
/* 1003 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 576461044361199616L, active2, 0L);
/*      */     case 'n':
/* 1005 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 26422638804992L, active2, 6L);
/*      */     case 'o':
/* 1007 */       if ((active1 & 0x0) != 0L)
/*      */       {
/* 1009 */         jjmatchedKind = 96;
/* 1010 */         jjmatchedPos = 1;
/*      */       }
/* 1012 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 37942571565056L, active2, 34864L);
/*      */     case 'p':
/* 1014 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 128L);
/*      */     case 'r':
/* 1016 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, -9222948724873887744L, active2, 256L);
/*      */     case 't':
/* 1018 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 504403158265495552L, active2, 8192L);
/*      */     case 'u':
/* 1020 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 562949955518464L, active2, 0L);
/*      */     case 'w':
/* 1022 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 1152921504606846976L, active2, 0L);
/*      */     case 'x':
/* 1024 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 68719476736L, active2, 0L);
/*      */     case 'y':
/* 1026 */       return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 16385L);
/*      */     case '|':
/* 1028 */       if ((active0 & 0x0) == 0L) break;
/* 1029 */       return jjStopAtPos(1, 59);
/*      */     case '\'':
/*      */     case '(':
/*      */     case ')':
/*      */     case ',':
/*      */     case '0':
/*      */     case '1':
/*      */     case '2':
/*      */     case '3':
/*      */     case '4':
/*      */     case '5':
/*      */     case '6':
/*      */     case '7':
/*      */     case '8':
/*      */     case '9':
/*      */     case ';':
/*      */     case '?':
/*      */     case '@':
/*      */     case 'A':
/*      */     case 'B':
/*      */     case 'C':
/*      */     case 'D':
/*      */     case 'E':
/*      */     case 'F':
/*      */     case 'G':
/*      */     case 'H':
/*      */     case 'I':
/*      */     case 'J':
/*      */     case 'K':
/*      */     case 'L':
/*      */     case 'M':
/*      */     case 'N':
/*      */     case 'O':
/*      */     case 'P':
/*      */     case 'Q':
/*      */     case 'R':
/*      */     case 'S':
/*      */     case 'T':
/*      */     case 'U':
/*      */     case 'V':
/*      */     case 'W':
/*      */     case 'X':
/*      */     case 'Y':
/*      */     case 'Z':
/*      */     case '[':
/*      */     case '\\':
/*      */     case ']':
/*      */     case '^':
/*      */     case '_':
/*      */     case '`':
/*      */     case 'b':
/*      */     case 'c':
/*      */     case 'd':
/*      */     case 'g':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'm':
/*      */     case 'q':
/*      */     case 's':
/*      */     case 'v':
/*      */     case 'z':
/* 1034 */     case '{': } return jjStartNfa_0(0, active0, active1, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1, long old2, long active2) {
/* 1038 */     if ((active0 &= old0 | active1 &= old1 | active2 &= old2) == 0L)
/* 1039 */       return jjStartNfa_0(0, old0, old1, old2); try {
/* 1040 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1042 */       jjStopStringLiteralDfa_0(1, active0, active1, active2);
/* 1043 */       return 2;
/*      */     }
/* 1045 */     switch (curChar)
/*      */     {
/*      */     case '*':
/* 1048 */       if ((active1 & 0x100000) == 0L) break;
/* 1049 */       return jjStopAtPos(2, 84);
/*      */     case '.':
/* 1052 */       if ((active0 & 0x0) == 0L) break;
/* 1053 */       return jjStopAtPos(2, 47);
/*      */     case '=':
/* 1056 */       if ((active0 & 0x0) != 0L)
/* 1057 */         return jjStopAtPos(2, 54);
/* 1058 */       if ((active0 & 0x0) == 0L) break;
/* 1059 */       return jjStopAtPos(2, 55);
/*      */     case 'a':
/* 1062 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 648518346475569152L, active2, 8192L);
/*      */     case 'b':
/* 1064 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 562949953421312L, active2, 0L);
/*      */     case 'd':
/* 1066 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 1125899906842624L, active2, 0L);
/*      */     case 'e':
/* 1068 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 4194304L, active2, 128L);
/*      */     case 'f':
/* 1070 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 1073741824L, active2, 0L);
/*      */     case 'g':
/* 1072 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 20266198323167232L, active2, 0L);
/*      */     case 'i':
/* 1074 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 5764749360034217984L, active2, 65618L);
/*      */     case 'l':
/* 1076 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 8798240505856L, active2, 2592L);
/*      */     case 'm':
/* 1078 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 2305843009213693952L, active2, 0L);
/*      */     case 'n':
/* 1080 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 35322616348672L, active2, 49152L);
/*      */     case 'o':
/* 1082 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 9288949134524416L, active2, 0L);
/*      */     case 'p':
/* 1084 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0L, active2, 1L);
/*      */     case 'r':
/* 1086 */       if ((active1 & 0x0) != 0L)
/* 1087 */         return jjStartNfaWithStates_0(2, 103, 211);
/* 1088 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 432345564227567616L, active2, 1032L);
/*      */     case 's':
/* 1090 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 17213423616L, active2, 4L);
/*      */     case 't':
/* 1092 */       if ((active1 & 0x0) != 0L)
/* 1093 */         return jjStartNfaWithStates_0(2, 108, 211);
/* 1094 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 4505867439308800L, active2, 4096L);
/*      */     case 'u':
/* 1096 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 42949672960L, active2, 256L);
/*      */     case 'w':
/* 1098 */       if ((active1 & 0x0) == 0L) break;
/* 1099 */       return jjStartNfaWithStates_0(2, 110, 211);
/*      */     case 'y':
/* 1102 */       if ((active1 & 0x0) == 0L) break;
/* 1103 */       return jjStartNfaWithStates_0(2, 127, 211);
/*      */     case 'z':
/* 1106 */       return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 36028797018963968L, active2, 0L);
/*      */     case '+':
/*      */     case ',':
/*      */     case '-':
/*      */     case '/':
/*      */     case '0':
/*      */     case '1':
/*      */     case '2':
/*      */     case '3':
/*      */     case '4':
/*      */     case '5':
/*      */     case '6':
/*      */     case '7':
/*      */     case '8':
/*      */     case '9':
/*      */     case ':':
/*      */     case ';':
/*      */     case '<':
/*      */     case '>':
/*      */     case '?':
/*      */     case '@':
/*      */     case 'A':
/*      */     case 'B':
/*      */     case 'C':
/*      */     case 'D':
/*      */     case 'E':
/*      */     case 'F':
/*      */     case 'G':
/*      */     case 'H':
/*      */     case 'I':
/*      */     case 'J':
/*      */     case 'K':
/*      */     case 'L':
/*      */     case 'M':
/*      */     case 'N':
/*      */     case 'O':
/*      */     case 'P':
/*      */     case 'Q':
/*      */     case 'R':
/*      */     case 'S':
/*      */     case 'T':
/*      */     case 'U':
/*      */     case 'V':
/*      */     case 'W':
/*      */     case 'X':
/*      */     case 'Y':
/*      */     case 'Z':
/*      */     case '[':
/*      */     case '\\':
/*      */     case ']':
/*      */     case '^':
/*      */     case '_':
/*      */     case '`':
/*      */     case 'c':
/*      */     case 'h':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'q':
/*      */     case 'v':
/* 1110 */     case 'x': } return jjStartNfa_0(1, active0, active1, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa3_0(long old0, long active0, long old1, long active1, long old2, long active2) {
/* 1114 */     if ((active0 &= old0 | active1 &= old1 | active2 &= old2) == 0L)
/* 1115 */       return jjStartNfa_0(1, old0, old1, old2); try {
/* 1116 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1118 */       jjStopStringLiteralDfa_0(2, 0L, active1, active2);
/* 1119 */       return 3;
/*      */     }
/* 1121 */     switch (curChar)
/*      */     {
/*      */     case 'a':
/* 1124 */       return jjMoveStringLiteralDfa4_0(active1, 413394796544L, active2, 16416L);
/*      */     case 'b':
/* 1126 */       return jjMoveStringLiteralDfa4_0(active1, 8589934592L, active2, 0L);
/*      */     case 'c':
/* 1128 */       return jjMoveStringLiteralDfa4_0(active1, 67108864L, active2, 0L);
/*      */     case 'd':
/* 1130 */       if ((active2 & 0x10) == 0L) break;
/* 1131 */       return jjStartNfaWithStates_0(3, 132, 211);
/*      */     case 'e':
/* 1134 */       if ((active1 & 0x2000000) != 0L)
/* 1135 */         return jjStartNfaWithStates_0(3, 89, 211);
/* 1136 */       if ((active1 & 0x0) != 0L)
/* 1137 */         return jjStartNfaWithStates_0(3, 98, 211);
/* 1138 */       if ((active2 & 0x100) != 0L)
/* 1139 */         return jjStartNfaWithStates_0(3, 136, 211);
/* 1140 */       return jjMoveStringLiteralDfa4_0(active1, 37155867304394752L, active2, 4097L);
/*      */     case 'g':
/* 1142 */       if ((active1 & 0x0) == 0L) break;
/* 1143 */       return jjStartNfaWithStates_0(3, 109, 211);
/*      */     case 'i':
/* 1146 */       return jjMoveStringLiteralDfa4_0(active1, 146375783982563328L, active2, 4L);
/*      */     case 'l':
/* 1148 */       if ((active1 & 0x800000) != 0L)
/*      */       {
/* 1150 */         jjmatchedKind = 87;
/* 1151 */         jjmatchedPos = 3;
/*      */       }
/* 1153 */       return jjMoveStringLiteralDfa4_0(active1, 562949970198528L, active2, 64L);
/*      */     case 'm':
/* 1155 */       if ((active1 & 0x0) == 0L) break;
/* 1156 */       return jjStartNfaWithStates_0(3, 99, 211);
/*      */     case 'n':
/* 1159 */       return jjMoveStringLiteralDfa4_0(active1, 18014398509481984L, active2, 65536L);
/*      */     case 'o':
/* 1161 */       if ((active1 & 0x200000) != 0L)
/* 1162 */         return jjStartNfaWithStates_0(3, 85, 211);
/* 1163 */       if ((active1 & 0x0) != 0L)
/* 1164 */         return jjStartNfaWithStates_0(3, 105, 211);
/* 1165 */       return jjMoveStringLiteralDfa4_0(active1, 0L, active2, 3074L);
/*      */     case 'p':
/* 1167 */       return jjMoveStringLiteralDfa4_0(active1, 2305843009213693952L, active2, 0L);
/*      */     case 'r':
/* 1169 */       if ((active1 & 0x8000000) != 0L)
/* 1170 */         return jjStartNfaWithStates_0(3, 91, 211);
/* 1171 */       return jjMoveStringLiteralDfa4_0(active1, 9007199254740992L, active2, 128L);
/*      */     case 's':
/* 1173 */       if ((active1 & 0x0) != 0L)
/* 1174 */         return jjStartNfaWithStates_0(3, 126, 211);
/* 1175 */       return jjMoveStringLiteralDfa4_0(active1, 576460752571858944L, active2, 33280L);
/*      */     case 't':
/* 1177 */       return jjMoveStringLiteralDfa4_0(active1, 1225260574158356480L, active2, 8200L);
/*      */     case 'u':
/* 1179 */       return jjMoveStringLiteralDfa4_0(active1, 292733975779082240L, active2, 0L);
/*      */     case 'v':
/* 1181 */       return jjMoveStringLiteralDfa4_0(active1, 140737488355328L, active2, 0L);
/*      */     case 'f':
/*      */     case 'h':
/*      */     case 'j':
/*      */     case 'k':
/* 1185 */     case 'q': } return jjStartNfa_0(2, 0L, active1, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa4_0(long old1, long active1, long old2, long active2) {
/* 1189 */     if ((active1 &= old1 | active2 &= old2) == 0L)
/* 1190 */       return jjStartNfa_0(2, 0L, old1, old2); try {
/* 1191 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1193 */       jjStopStringLiteralDfa_0(3, 0L, active1, active2);
/* 1194 */       return 4;
/*      */     }
/* 1196 */     switch (curChar)
/*      */     {
/*      */     case 'a':
/* 1199 */       return jjMoveStringLiteralDfa5_0(active1, 140737488355328L, active2, 128L);
/*      */     case 'c':
/* 1201 */       return jjMoveStringLiteralDfa5_0(active1, 1442277780665401344L, active2, 0L);
/*      */     case 'd':
/* 1203 */       return jjMoveStringLiteralDfa5_0(active1, 0L, active2, 1L);
/*      */     case 'e':
/* 1205 */       if ((active2 & 0x40) != 0L)
/* 1206 */         return jjStartNfaWithStates_0(4, 134, 211);
/* 1207 */       if ((active2 & 0x200) != 0L)
/* 1208 */         return jjStartNfaWithStates_0(4, 137, 211);
/* 1209 */       return jjMoveStringLiteralDfa5_0(active1, 18295873502969856L, active2, 0L);
/*      */     case 'g':
/* 1211 */       return jjMoveStringLiteralDfa5_0(active1, 0L, active2, 4L);
/*      */     case 'h':
/* 1213 */       if ((active1 & 0x4000000) == 0L) break;
/* 1214 */       return jjStartNfaWithStates_0(4, 90, 211);
/*      */     case 'i':
/* 1217 */       return jjMoveStringLiteralDfa5_0(active1, 72620544528220160L, active2, 8192L);
/*      */     case 'k':
/* 1219 */       if ((active1 & 0x400000) == 0L) break;
/* 1220 */       return jjStartNfaWithStates_0(4, 86, 211);
/*      */     case 'l':
/* 1223 */       return jjMoveStringLiteralDfa5_0(active1, 2305843155242582016L, active2, 0L);
/*      */     case 'm':
/* 1225 */       return jjMoveStringLiteralDfa5_0(active1, 0L, active2, 16384L);
/*      */     case 'n':
/* 1227 */       if ((active2 & 0x2) != 0L)
/* 1228 */         return jjStartNfaWithStates_0(4, 129, 211);
/* 1229 */       return jjMoveStringLiteralDfa5_0(active1, 144125083680505856L, active2, 0L);
/*      */     case 'o':
/* 1231 */       return jjMoveStringLiteralDfa5_0(active1, 36028797018963968L, active2, 0L);
/*      */     case 'r':
/* 1233 */       if ((active2 & 0x800) != 0L)
/* 1234 */         return jjStartNfaWithStates_0(4, 139, 211);
/* 1235 */       return jjMoveStringLiteralDfa5_0(active1, 4503668346847232L, active2, 0L);
/*      */     case 's':
/* 1237 */       if ((active1 & 0x0) != 0L)
/* 1238 */         return jjStartNfaWithStates_0(4, 123, 211);
/* 1239 */       return jjMoveStringLiteralDfa5_0(active1, 2251799813685248L, active2, 0L);
/*      */     case 't':
/* 1241 */       if ((active1 & 0x10000000) != 0L)
/*      */       {
/* 1243 */         jjmatchedKind = 92;
/* 1244 */         jjmatchedPos = 4;
/*      */       } else {
/* 1246 */         if ((active1 & 0x0) != 0L)
/* 1247 */           return jjStartNfaWithStates_0(4, 102, 211);
/* 1248 */         if ((active1 & 0x0) != 0L)
/* 1249 */           return jjStartNfaWithStates_0(4, 117, 211); 
/*      */       }
/* 1250 */       return jjMoveStringLiteralDfa5_0(active1, 2147483648L, active2, 102432L);
/*      */     case 'u':
/* 1252 */       return jjMoveStringLiteralDfa5_0(active1, 1073741824L, active2, 8L);
/*      */     case 'w':
/* 1254 */       if ((active2 & 0x400) == 0L) break;
/* 1255 */       return jjStartNfaWithStates_0(4, 138, 211);
/*      */     case 'b':
/*      */     case 'f':
/*      */     case 'j':
/*      */     case 'p':
/*      */     case 'q':
/* 1260 */     case 'v': } return jjStartNfa_0(3, 0L, active1, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa5_0(long old1, long active1, long old2, long active2) {
/* 1264 */     if ((active1 &= old1 | active2 &= old2) == 0L)
/* 1265 */       return jjStartNfa_0(3, 0L, old1, old2); try {
/* 1266 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1268 */       jjStopStringLiteralDfa_0(4, 0L, active1, active2);
/* 1269 */       return 5;
/*      */     }
/* 1271 */     switch (curChar)
/*      */     {
/*      */     case '_':
/* 1274 */       return jjMoveStringLiteralDfa6_0(active1, 0L, active2, 32768L);
/*      */     case 'a':
/* 1276 */       return jjMoveStringLiteralDfa6_0(active1, 2305843009230471168L, active2, 8L);
/*      */     case 'c':
/* 1278 */       if ((active1 & 0x0) != 0L)
/* 1279 */         return jjStartNfaWithStates_0(5, 113, 211);
/* 1280 */       if ((active1 & 0x0) != 0L)
/*      */       {
/* 1282 */         jjmatchedKind = 120;
/* 1283 */         jjmatchedPos = 5;
/*      */       }
/* 1285 */       return jjMoveStringLiteralDfa6_0(active1, 281474976710656L, active2, 8192L);
/*      */     case 'd':
/* 1287 */       if ((active1 & 0x0) != 0L)
/* 1288 */         return jjStartNfaWithStates_0(5, 104, 211);
/* 1289 */       if ((active1 & 0x0) == 0L) break;
/* 1290 */       return jjStartNfaWithStates_0(5, 118, 211);
/*      */     case 'e':
/* 1293 */       if ((active1 & 0x80000000) != 0L)
/* 1294 */         return jjStartNfaWithStates_0(5, 95, 211);
/* 1295 */       if ((active1 & 0x0) != 0L)
/* 1296 */         return jjStartNfaWithStates_0(5, 97, 211);
/* 1297 */       if ((active1 & 0x0) != 0L)
/* 1298 */         return jjStartNfaWithStates_0(5, 107, 211);
/* 1299 */       return jjMoveStringLiteralDfa6_0(active1, 0L, active2, 65537L);
/*      */     case 'f':
/* 1301 */       if ((active1 & 0x0) == 0L) break;
/* 1302 */       return jjStartNfaWithStates_0(5, 119, 211);
/*      */     case 'g':
/* 1305 */       if ((active1 & 0x0) == 0L) break;
/* 1306 */       return jjStartNfaWithStates_0(5, 121, 211);
/*      */     case 'h':
/* 1309 */       if ((active1 & 0x0) == 0L) break;
/* 1310 */       return jjStartNfaWithStates_0(5, 124, 211);
/*      */     case 'i':
/* 1313 */       return jjMoveStringLiteralDfa6_0(active1, 0L, active2, 20512L);
/*      */     case 'l':
/* 1315 */       return jjMoveStringLiteralDfa6_0(active1, 1126038419537920L, active2, 0L);
/*      */     case 'n':
/* 1317 */       if ((active1 & 0x0) != 0L)
/* 1318 */         return jjStartNfaWithStates_0(5, 100, 211);
/* 1319 */       if ((active1 & 0x0) != 0L)
/* 1320 */         return jjStartNfaWithStates_0(5, 116, 211);
/* 1321 */       return jjMoveStringLiteralDfa6_0(active1, 536870912L, active2, 4L);
/*      */     case 't':
/* 1323 */       if ((active1 & 0x0) != 0L)
/* 1324 */         return jjStartNfaWithStates_0(5, 122, 211);
/* 1325 */       return jjMoveStringLiteralDfa6_0(active1, 2392537302040576L, active2, 128L);
/*      */     case '`':
/*      */     case 'b':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'm':
/*      */     case 'o':
/*      */     case 'p':
/*      */     case 'q':
/*      */     case 'r':
/* 1329 */     case 's': } return jjStartNfa_0(4, 0L, active1, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa6_0(long old1, long active1, long old2, long active2) {
/* 1333 */     if ((active1 &= old1 | active2 &= old2) == 0L)
/* 1334 */       return jjStartNfa_0(4, 0L, old1, old2); try {
/* 1335 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1337 */       jjStopStringLiteralDfa_0(5, 0L, active1, active2);
/* 1338 */       return 6;
/*      */     }
/* 1340 */     switch (curChar)
/*      */     {
/*      */     case '_':
/* 1343 */       return jjMoveStringLiteralDfa7_0(active1, 0L, active2, 8192L);
/*      */     case 'a':
/* 1345 */       return jjMoveStringLiteralDfa7_0(active1, 1125899906842624L, active2, 0L);
/*      */     case 'c':
/* 1347 */       return jjMoveStringLiteralDfa7_0(active1, 0L, active2, 49152L);
/*      */     case 'e':
/* 1349 */       if ((active1 & 0x0) != 0L)
/* 1350 */         return jjStartNfaWithStates_0(6, 111, 211);
/* 1351 */       return jjMoveStringLiteralDfa7_0(active1, 2251799813685248L, active2, 4L);
/*      */     case 'f':
/* 1353 */       if ((active2 & 1L) == 0L) break;
/* 1354 */       return jjStartNfaWithStates_0(6, 128, 211);
/*      */     case 'l':
/* 1357 */       if ((active2 & 0x8) != 0L)
/* 1358 */         return jjStartNfaWithStates_0(6, 131, 211);
/* 1359 */       return jjMoveStringLiteralDfa7_0(active1, 0L, active2, 32L);
/*      */     case 'm':
/* 1361 */       return jjMoveStringLiteralDfa7_0(active1, 0L, active2, 4096L);
/*      */     case 'n':
/* 1363 */       if ((active1 & 0x1000000) == 0L) break;
/* 1364 */       return jjStartNfaWithStates_0(6, 88, 211);
/*      */     case 'o':
/* 1367 */       return jjMoveStringLiteralDfa7_0(active1, 0L, active2, 128L);
/*      */     case 'r':
/* 1369 */       return jjMoveStringLiteralDfa7_0(active1, 0L, active2, 65536L);
/*      */     case 't':
/* 1371 */       if ((active1 & 0x40000000) != 0L)
/* 1372 */         return jjStartNfaWithStates_0(6, 94, 211);
/* 1373 */       return jjMoveStringLiteralDfa7_0(active1, 2306124484190404608L, active2, 0L);
/*      */     case 'u':
/* 1375 */       return jjMoveStringLiteralDfa7_0(active1, 536870912L, active2, 0L);
/*      */     case 'y':
/* 1377 */       if ((active1 & 0x0) == 0L) break;
/* 1378 */       return jjStartNfaWithStates_0(6, 101, 211);
/*      */     case '`':
/*      */     case 'b':
/*      */     case 'd':
/*      */     case 'g':
/*      */     case 'h':
/*      */     case 'i':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'p':
/*      */     case 'q':
/*      */     case 's':
/*      */     case 'v':
/*      */     case 'w':
/* 1383 */     case 'x': } return jjStartNfa_0(5, 0L, active1, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa7_0(long old1, long active1, long old2, long active2) {
/* 1387 */     if ((active1 &= old1 | active2 &= old2) == 0L)
/* 1388 */       return jjStartNfa_0(5, 0L, old1, old2); try {
/* 1389 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1391 */       jjStopStringLiteralDfa_0(6, 0L, active1, active2);
/* 1392 */       return 7;
/*      */     }
/* 1394 */     switch (curChar)
/*      */     {
/*      */     case '_':
/* 1397 */       return jjMoveStringLiteralDfa8_0(active1, 0L, active2, 16384L);
/*      */     case 'a':
/* 1399 */       return jjMoveStringLiteralDfa8_0(active1, 0L, active2, 32768L);
/*      */     case 'c':
/* 1401 */       return jjMoveStringLiteralDfa8_0(active1, 0L, active2, 8192L);
/*      */     case 'd':
/* 1403 */       if ((active2 & 0x4) == 0L) break;
/* 1404 */       return jjStartNfaWithStates_0(7, 130, 211);
/*      */     case 'e':
/* 1407 */       if ((active1 & 0x20000000) != 0L)
/* 1408 */         return jjStartNfaWithStates_0(7, 93, 211);
/* 1409 */       if ((active1 & 0x0) != 0L)
/* 1410 */         return jjStartNfaWithStates_0(7, 125, 211);
/* 1411 */       if ((active2 & 0x20) != 0L)
/* 1412 */         return jjStartNfaWithStates_0(7, 133, 211);
/* 1413 */       if ((active2 & 0x1000) != 0L)
/* 1414 */         return jjStartNfaWithStates_0(7, 140, 211);
/* 1415 */       return jjMoveStringLiteralDfa8_0(active1, 281474976710656L, active2, 0L);
/*      */     case 'p':
/* 1417 */       return jjMoveStringLiteralDfa8_0(active1, 0L, active2, 65536L);
/*      */     case 'r':
/* 1419 */       if ((active1 & 0x0) != 0L)
/* 1420 */         return jjStartNfaWithStates_0(7, 115, 211);
/* 1421 */       if ((active2 & 0x80) != 0L)
/* 1422 */         return jjStartNfaWithStates_0(7, 135, 211);
/* 1423 */       return jjMoveStringLiteralDfa8_0(active1, 1125899906842624L, active2, 0L);
/*      */     case '`':
/*      */     case 'b':
/*      */     case 'f':
/*      */     case 'g':
/*      */     case 'h':
/*      */     case 'i':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'l':
/*      */     case 'm':
/*      */     case 'n':
/*      */     case 'o':
/* 1427 */     case 'q': } return jjStartNfa_0(6, 0L, active1, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa8_0(long old1, long active1, long old2, long active2) {
/* 1431 */     if ((active1 &= old1 | active2 &= old2) == 0L)
/* 1432 */       return jjStartNfa_0(6, 0L, old1, old2); try {
/* 1433 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1435 */       jjStopStringLiteralDfa_0(7, 0L, active1, active2);
/* 1436 */       return 8;
/*      */     }
/* 1438 */     switch (curChar)
/*      */     {
/*      */     case 'a':
/* 1441 */       return jjMoveStringLiteralDfa9_0(active1, 0L, active2, 8192L);
/*      */     case 'c':
/* 1443 */       return jjMoveStringLiteralDfa9_0(active1, 0L, active2, 16384L);
/*      */     case 'd':
/* 1445 */       if ((active1 & 0x0) == 0L) break;
/* 1446 */       return jjStartNfaWithStates_0(8, 112, 211);
/*      */     case 'e':
/* 1449 */       return jjMoveStringLiteralDfa9_0(active1, 1125899906842624L, active2, 0L);
/*      */     case 'r':
/* 1451 */       return jjMoveStringLiteralDfa9_0(active1, 0L, active2, 65536L);
/*      */     case 's':
/* 1453 */       return jjMoveStringLiteralDfa9_0(active1, 0L, active2, 32768L);
/*      */     case 'b':
/*      */     case 'f':
/*      */     case 'g':
/*      */     case 'h':
/*      */     case 'i':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'l':
/*      */     case 'm':
/*      */     case 'n':
/*      */     case 'o':
/*      */     case 'p':
/* 1457 */     case 'q': } return jjStartNfa_0(7, 0L, active1, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa9_0(long old1, long active1, long old2, long active2) {
/* 1461 */     if ((active1 &= old1 | active2 &= old2) == 0L)
/* 1462 */       return jjStartNfa_0(7, 0L, old1, old2); try {
/* 1463 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1465 */       jjStopStringLiteralDfa_0(8, 0L, active1, active2);
/* 1466 */       return 9;
/*      */     }
/* 1468 */     switch (curChar)
/*      */     {
/*      */     case 'a':
/* 1471 */       return jjMoveStringLiteralDfa10_0(active1, 0L, active2, 16384L);
/*      */     case 'd':
/* 1473 */       if ((active1 & 0x0) == 0L) break;
/* 1474 */       return jjStartNfaWithStates_0(9, 114, 211);
/*      */     case 'e':
/* 1477 */       return jjMoveStringLiteralDfa10_0(active1, 0L, active2, 65536L);
/*      */     case 's':
/* 1479 */       return jjMoveStringLiteralDfa10_0(active1, 0L, active2, 8192L);
/*      */     case 't':
/* 1481 */       if ((active2 & 0x8000) == 0L) break;
/* 1482 */       return jjStartNfaWithStates_0(9, 143, 211);
/*      */     }
/*      */ 
/* 1487 */     return jjStartNfa_0(8, 0L, active1, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa10_0(long old1, long active1, long old2, long active2) {
/* 1491 */     if ((active1 &= old1 | active2 &= old2) == 0L)
/* 1492 */       return jjStartNfa_0(8, 0L, old1, old2); try {
/* 1493 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1495 */       jjStopStringLiteralDfa_0(9, 0L, 0L, active2);
/* 1496 */       return 10;
/*      */     }
/* 1498 */     switch (curChar)
/*      */     {
/*      */     case 's':
/* 1501 */       return jjMoveStringLiteralDfa11_0(active2, 16384L);
/*      */     case 't':
/* 1503 */       if ((active2 & 0x2000) != 0L)
/* 1504 */         return jjStartNfaWithStates_0(10, 141, 211);
/* 1505 */       return jjMoveStringLiteralDfa11_0(active2, 65536L);
/*      */     }
/*      */ 
/* 1509 */     return jjStartNfa_0(9, 0L, 0L, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa11_0(long old2, long active2) {
/* 1513 */     if ((active2 &= old2) == 0L)
/* 1514 */       return jjStartNfa_0(9, 0L, 0L, old2); try {
/* 1515 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1517 */       jjStopStringLiteralDfa_0(10, 0L, 0L, active2);
/* 1518 */       return 11;
/*      */     }
/* 1520 */     switch (curChar)
/*      */     {
/*      */     case '_':
/* 1523 */       return jjMoveStringLiteralDfa12_0(active2, 65536L);
/*      */     case 't':
/* 1525 */       if ((active2 & 0x4000) == 0L) break;
/* 1526 */       return jjStartNfaWithStates_0(11, 142, 211);
/*      */     }
/*      */ 
/* 1531 */     return jjStartNfa_0(10, 0L, 0L, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa12_0(long old2, long active2) {
/* 1535 */     if ((active2 &= old2) == 0L)
/* 1536 */       return jjStartNfa_0(10, 0L, 0L, old2); try {
/* 1537 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1539 */       jjStopStringLiteralDfa_0(11, 0L, 0L, active2);
/* 1540 */       return 12;
/*      */     }
/* 1542 */     switch (curChar)
/*      */     {
/*      */     case 'c':
/* 1545 */       return jjMoveStringLiteralDfa13_0(active2, 65536L);
/*      */     }
/*      */ 
/* 1549 */     return jjStartNfa_0(11, 0L, 0L, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa13_0(long old2, long active2) {
/* 1553 */     if ((active2 &= old2) == 0L)
/* 1554 */       return jjStartNfa_0(11, 0L, 0L, old2); try {
/* 1555 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1557 */       jjStopStringLiteralDfa_0(12, 0L, 0L, active2);
/* 1558 */       return 13;
/*      */     }
/* 1560 */     switch (curChar)
/*      */     {
/*      */     case 'a':
/* 1563 */       return jjMoveStringLiteralDfa14_0(active2, 65536L);
/*      */     }
/*      */ 
/* 1567 */     return jjStartNfa_0(12, 0L, 0L, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa14_0(long old2, long active2) {
/* 1571 */     if ((active2 &= old2) == 0L)
/* 1572 */       return jjStartNfa_0(12, 0L, 0L, old2); try {
/* 1573 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1575 */       jjStopStringLiteralDfa_0(13, 0L, 0L, active2);
/* 1576 */       return 14;
/*      */     }
/* 1578 */     switch (curChar)
/*      */     {
/*      */     case 's':
/* 1581 */       return jjMoveStringLiteralDfa15_0(active2, 65536L);
/*      */     }
/*      */ 
/* 1585 */     return jjStartNfa_0(13, 0L, 0L, active2);
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa15_0(long old2, long active2) {
/* 1589 */     if ((active2 &= old2) == 0L)
/* 1590 */       return jjStartNfa_0(13, 0L, 0L, old2); try {
/* 1591 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 1593 */       jjStopStringLiteralDfa_0(14, 0L, 0L, active2);
/* 1594 */       return 15;
/*      */     }
/* 1596 */     switch (curChar)
/*      */     {
/*      */     case 't':
/* 1599 */       if ((active2 & 0x10000) == 0L) break;
/* 1600 */       return jjStartNfaWithStates_0(15, 144, 211);
/*      */     }
/*      */ 
/* 1605 */     return jjStartNfa_0(14, 0L, 0L, active2);
/*      */   }
/*      */ 
/*      */   private static int jjStartNfaWithStates_0(int pos, int kind, int state) {
/* 1609 */     jjmatchedKind = kind;
/* 1610 */     jjmatchedPos = pos;
/*      */     try { curChar = JavaCharStream.readChar(); } catch (IOException e) {
/* 1612 */       return pos + 1;
/* 1613 */     }return jjMoveNfa_0(state, pos + 1);
/*      */   }
/*      */ 
/*      */   private static int jjMoveNfa_0(int startState, int curPos)
/*      */   {
/* 1815 */     int startsAt = 0;
/* 1816 */     jjnewStateCnt = 210;
/* 1817 */     int i = 1;
/* 1818 */     jjstateSet[0] = startState;
/* 1819 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/* 1822 */       if (++jjround == 2147483647)
/* 1823 */         ReInitRounds();
/* 1824 */       if (curChar < '@')
/*      */       {
/* 1826 */         long l = 1L << curChar;
/*      */         do
/*      */         {
/* 1829 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 212:
/* 1832 */             if ((0x0 & l) != 0L)
/*      */             {
/* 1834 */               if (kind > 8)
/* 1835 */                 kind = 8;
/*      */             }
/* 1837 */             else if ((0x200 & l) != 0L)
/* 1838 */               jjCheckNAddTwoStates(44, 53);
/* 1839 */             if ((0x200 & l) != 0L)
/* 1840 */               jjCheckNAddTwoStates(36, 43);
/* 1841 */             if ((0x200 & l) != 0L)
/* 1842 */               jjCheckNAddTwoStates(29, 35);
/* 1843 */             if ((0x200 & l) != 0L)
/* 1844 */               jjCheckNAddTwoStates(18, 28);
/* 1845 */             if ((0x200 & l) != 0L)
/* 1846 */               jjCheckNAddTwoStates(16, 17);
/* 1847 */             if ((0x200 & l) == 0L) continue;
/* 1848 */             jjCheckNAddTwoStates(11, 15); break;
/*      */           case 211:
/* 1851 */             if ((0xFFFC1FF & l) != 0L)
/*      */             {
/* 1853 */               if (kind > 178)
/* 1854 */                 kind = 178;
/* 1855 */               jjCheckNAdd(9);
/*      */             }
/* 1857 */             if ((0x0 & l) != 0L)
/*      */             {
/* 1859 */               if (kind > 165)
/* 1860 */                 kind = 165;
/* 1861 */               jjCheckNAdd(209);
/*      */             }
/* 1863 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 1865 */             if (kind > 164)
/* 1866 */               kind = 164;
/* 1867 */             jjCheckNAdd(208); break;
/*      */           case 4:
/* 1871 */             if ((0xFFFC1FF & l) != 0L)
/*      */             {
/* 1873 */               if (kind > 178)
/* 1874 */                 kind = 178;
/* 1875 */               jjCheckNAdd(9);
/*      */             }
/* 1877 */             else if (curChar == '\'') {
/* 1878 */               jjCheckNAddTwoStates(5, 6);
/* 1879 */             }if ((0x0 & l) != 0L)
/*      */             {
/* 1881 */               if (kind > 165)
/* 1882 */                 kind = 165;
/* 1883 */               jjCheckNAdd(209);
/*      */             }
/* 1885 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 1887 */             if (kind > 164)
/* 1888 */               kind = 164;
/* 1889 */             jjCheckNAdd(208); break;
/*      */           case 3:
/* 1893 */             if ((0x0 & l) != 0L)
/*      */             {
/* 1895 */               if (kind > 149)
/* 1896 */                 kind = 149;
/* 1897 */               jjCheckNAddStates(0, 13);
/*      */             }
/* 1899 */             else if (curChar == '"') {
/* 1900 */               jjCheckNAddStates(14, 20);
/* 1901 */             } else if (curChar == '\'') {
/* 1902 */               jjCheckNAddStates(21, 25);
/* 1903 */             } else if (curChar == '.') {
/* 1904 */               jjCheckNAddTwoStates(97, 146);
/* 1905 */             } else if (curChar == '#') {
/* 1906 */               jjCheckNAddStates(26, 37);
/* 1907 */             } else if (curChar == '$')
/*      */             {
/* 1909 */               if (kind > 178)
/* 1910 */                 kind = 178;
/* 1911 */               jjCheckNAdd(9);
/*      */             }
/* 1913 */             if ((0x0 & l) != 0L)
/*      */             {
/* 1915 */               if (kind > 150)
/* 1916 */                 kind = 150;
/* 1917 */               jjCheckNAddStates(38, 48);
/*      */             }
/* 1919 */             else if (curChar == '0') {
/* 1920 */               jjAddStates(49, 52);
/* 1921 */             }if (curChar != '0')
/*      */               continue;
/* 1923 */             if (kind > 145)
/* 1924 */               kind = 145;
/* 1925 */             jjCheckNAddStates(53, 67); break;
/*      */           case 210:
/* 1929 */             if ((0x0 & l) != 0L)
/*      */             {
/* 1931 */               if (kind > 171)
/* 1932 */                 kind = 171;
/* 1933 */               jjCheckNAddStates(68, 70);
/*      */             }
/* 1935 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 1937 */             if (kind > 158)
/* 1938 */               kind = 158;
/* 1939 */             jjCheckNAddStates(71, 73); break;
/*      */           case 0:
/* 1943 */             if ((0xFFFC1FF & l) != 0L)
/*      */             {
/* 1945 */               if (kind > 178)
/* 1946 */                 kind = 178;
/* 1947 */               jjCheckNAdd(9);
/*      */             }
/* 1949 */             else if (curChar == '\'') {
/* 1950 */               jjCheckNAddTwoStates(1, 2);
/* 1951 */             }if ((0x0 & l) != 0L)
/*      */             {
/* 1953 */               if (kind > 165)
/* 1954 */                 kind = 165;
/* 1955 */               jjCheckNAdd(209);
/*      */             }
/* 1957 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 1959 */             if (kind > 164)
/* 1960 */               kind = 164;
/* 1961 */             jjCheckNAdd(208); break;
/*      */           case 1:
/* 1965 */             if ((0x0 & l) == 0L) continue;
/* 1966 */             jjCheckNAddTwoStates(1, 2); break;
/*      */           case 2:
/* 1969 */             if ((curChar != '\'') || (kind <= 160)) continue;
/* 1970 */             kind = 160; break;
/*      */           case 5:
/* 1973 */             if ((0x0 & l) == 0L) continue;
/* 1974 */             jjCheckNAddTwoStates(5, 6); break;
/*      */           case 6:
/* 1977 */             if ((curChar != '\'') || (kind <= 161)) continue;
/* 1978 */             kind = 161; break;
/*      */           case 8:
/* 1981 */             if (curChar != '$')
/*      */               continue;
/* 1983 */             if (kind > 178)
/* 1984 */               kind = 178;
/* 1985 */             jjCheckNAdd(9);
/* 1986 */             break;
/*      */           case 9:
/* 1988 */             if ((0xFFFC1FF & l) == 0L)
/*      */               continue;
/* 1990 */             if (kind > 178)
/* 1991 */               kind = 178;
/* 1992 */             jjCheckNAdd(9);
/* 1993 */             break;
/*      */           case 10:
/* 1995 */             if (curChar != '#') continue;
/* 1996 */             jjCheckNAddStates(26, 37); break;
/*      */           case 11:
/* 1999 */             if ((0x200 & l) == 0L) continue;
/* 2000 */             jjCheckNAddTwoStates(11, 15); break;
/*      */           case 16:
/* 2003 */             if ((0x200 & l) == 0L) continue;
/* 2004 */             jjCheckNAddTwoStates(16, 17); break;
/*      */           case 17:
/* 2007 */             if (((0x0 & l) == 0L) || (kind <= 8)) continue;
/* 2008 */             kind = 8; break;
/*      */           case 18:
/* 2011 */             if ((0x200 & l) == 0L) continue;
/* 2012 */             jjCheckNAddTwoStates(18, 28); break;
/*      */           case 20:
/* 2015 */             if ((0x200 & l) == 0L) continue;
/* 2016 */             jjAddStates(74, 75); break;
/*      */           case 21:
/* 2019 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2021 */             if (kind > 9)
/* 2022 */               kind = 9;
/* 2023 */             jjCheckNAdd(22);
/* 2024 */             break;
/*      */           case 22:
/* 2026 */             if ((0x200 & l) == 0L)
/*      */               continue;
/* 2028 */             if (kind > 9)
/* 2029 */               kind = 9;
/* 2030 */             jjCheckNAdd(22);
/* 2031 */             break;
/*      */           case 29:
/* 2033 */             if ((0x200 & l) == 0L) continue;
/* 2034 */             jjCheckNAddTwoStates(29, 35); break;
/*      */           case 36:
/* 2037 */             if ((0x200 & l) == 0L) continue;
/* 2038 */             jjCheckNAddTwoStates(36, 43); break;
/*      */           case 38:
/* 2041 */             if ((0x200 & l) == 0L)
/*      */               continue;
/* 2043 */             if (kind > 11)
/* 2044 */               kind = 11;
/* 2045 */             jjstateSet[(jjnewStateCnt++)] = 38;
/* 2046 */             break;
/*      */           case 44:
/* 2048 */             if ((0x200 & l) == 0L) continue;
/* 2049 */             jjCheckNAddTwoStates(44, 53); break;
/*      */           case 46:
/* 2052 */             if ((0x200 & l) == 0L)
/*      */               continue;
/* 2054 */             if (kind > 12)
/* 2055 */               kind = 12;
/* 2056 */             jjstateSet[(jjnewStateCnt++)] = 46;
/* 2057 */             break;
/*      */           case 54:
/* 2059 */             if (curChar != '0')
/*      */               continue;
/* 2061 */             if (kind > 145)
/* 2062 */               kind = 145;
/* 2063 */             jjCheckNAddStates(53, 67);
/* 2064 */             break;
/*      */           case 55:
/* 2066 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2068 */             if (kind > 145)
/* 2069 */               kind = 145;
/* 2070 */             jjCheckNAdd(55);
/* 2071 */             break;
/*      */           case 56:
/* 2073 */             if ((0x0 & l) == 0L) continue;
/* 2074 */             jjCheckNAddTwoStates(56, 57); break;
/*      */           case 58:
/* 2077 */             if ((0x0 & l) == 0L) continue;
/* 2078 */             jjCheckNAddTwoStates(58, 59); break;
/*      */           case 60:
/* 2081 */             if ((0x0 & l) == 0L) continue;
/* 2082 */             jjCheckNAddStates(76, 78); break;
/*      */           case 66:
/* 2085 */             if ((0x0 & l) == 0L) continue;
/* 2086 */             jjCheckNAddTwoStates(66, 67); break;
/*      */           case 68:
/* 2089 */             if ((0x0 & l) == 0L) continue;
/* 2090 */             jjCheckNAddTwoStates(68, 67); break;
/*      */           case 70:
/* 2093 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2095 */             if (kind > 167)
/* 2096 */               kind = 167;
/* 2097 */             jjstateSet[(jjnewStateCnt++)] = 70;
/* 2098 */             break;
/*      */           case 71:
/* 2100 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2102 */             if (kind > 167)
/* 2103 */               kind = 167;
/* 2104 */             jjCheckNAdd(71);
/* 2105 */             break;
/*      */           case 73:
/* 2107 */             if ((0x0 & l) == 0L) continue;
/* 2108 */             jjAddStates(79, 80); break;
/*      */           case 74:
/* 2111 */             if (curChar != '.') continue;
/* 2112 */             jjCheckNAdd(75); break;
/*      */           case 75:
/* 2115 */             if ((0x0 & l) == 0L) continue;
/* 2116 */             jjCheckNAddTwoStates(75, 76); break;
/*      */           case 77:
/* 2119 */             if ((0x0 & l) == 0L) continue;
/* 2120 */             jjCheckNAdd(78); break;
/*      */           case 78:
/* 2123 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2125 */             if (kind > 171)
/* 2126 */               kind = 171;
/* 2127 */             jjCheckNAddTwoStates(78, 79);
/* 2128 */             break;
/*      */           case 81:
/* 2130 */             if ((0x0 & l) == 0L) continue;
/* 2131 */             jjCheckNAddStates(81, 83); break;
/*      */           case 82:
/* 2134 */             if (curChar != '.') continue;
/* 2135 */             jjCheckNAdd(83); break;
/*      */           case 84:
/* 2138 */             if ((0x0 & l) == 0L) continue;
/* 2139 */             jjCheckNAdd(85); break;
/*      */           case 85:
/* 2142 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2144 */             if (kind > 171)
/* 2145 */               kind = 171;
/* 2146 */             jjCheckNAddTwoStates(85, 79);
/* 2147 */             break;
/*      */           case 86:
/* 2149 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2151 */             if (kind > 149)
/* 2152 */               kind = 149;
/* 2153 */             jjCheckNAddStates(0, 13);
/* 2154 */             break;
/*      */           case 87:
/* 2156 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2158 */             if (kind > 149)
/* 2159 */               kind = 149;
/* 2160 */             jjCheckNAdd(87);
/* 2161 */             break;
/*      */           case 88:
/* 2163 */             if ((0x0 & l) == 0L) continue;
/* 2164 */             jjCheckNAddTwoStates(88, 89); break;
/*      */           case 89:
/* 2167 */             if (curChar != '.')
/*      */               continue;
/* 2169 */             if (kind > 158)
/* 2170 */               kind = 158;
/* 2171 */             jjCheckNAddStates(84, 86);
/* 2172 */             break;
/*      */           case 90:
/* 2174 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2176 */             if (kind > 158)
/* 2177 */               kind = 158;
/* 2178 */             jjCheckNAddStates(84, 86);
/* 2179 */             break;
/*      */           case 92:
/* 2181 */             if ((0x0 & l) == 0L) continue;
/* 2182 */             jjCheckNAdd(93); break;
/*      */           case 93:
/* 2185 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2187 */             if (kind > 158)
/* 2188 */               kind = 158;
/* 2189 */             jjCheckNAddTwoStates(93, 94);
/* 2190 */             break;
/*      */           case 95:
/* 2192 */             if ((0x0 & l) == 0L) continue;
/* 2193 */             jjCheckNAddTwoStates(95, 96); break;
/*      */           case 96:
/* 2196 */             if (curChar != '.') continue;
/* 2197 */             jjCheckNAdd(97); break;
/*      */           case 97:
/* 2200 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2202 */             if (kind > 158)
/* 2203 */               kind = 158;
/* 2204 */             jjCheckNAddStates(71, 73);
/* 2205 */             break;
/*      */           case 98:
/* 2207 */             if ((0x0 & l) == 0L) continue;
/* 2208 */             jjCheckNAddTwoStates(98, 99); break;
/*      */           case 100:
/* 2211 */             if ((0x0 & l) == 0L) continue;
/* 2212 */             jjCheckNAdd(101); break;
/*      */           case 101:
/* 2215 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2217 */             if (kind > 159)
/* 2218 */               kind = 159;
/* 2219 */             jjCheckNAddTwoStates(101, 102);
/* 2220 */             break;
/*      */           case 103:
/* 2222 */             if ((0x0 & l) == 0L) continue;
/* 2223 */             jjCheckNAddStates(87, 89); break;
/*      */           case 105:
/* 2226 */             if ((0x0 & l) == 0L) continue;
/* 2227 */             jjCheckNAdd(106); break;
/*      */           case 106:
/* 2230 */             if ((0x0 & l) == 0L) continue;
/* 2231 */             jjCheckNAddTwoStates(106, 79); break;
/*      */           case 107:
/* 2234 */             if ((0x0 & l) == 0L) continue;
/* 2235 */             jjCheckNAddTwoStates(107, 108); break;
/*      */           case 109:
/* 2238 */             if ((0x0 & l) == 0L) continue;
/* 2239 */             jjCheckNAdd(110); break;
/*      */           case 110:
/* 2242 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2244 */             if (kind > 171)
/* 2245 */               kind = 171;
/* 2246 */             jjCheckNAddTwoStates(110, 79);
/* 2247 */             break;
/*      */           case 111:
/* 2249 */             if ((0x0 & l) == 0L) continue;
/* 2250 */             jjCheckNAddTwoStates(111, 112); break;
/*      */           case 112:
/* 2253 */             if (curChar != '.')
/*      */               continue;
/* 2255 */             if (kind > 171)
/* 2256 */               kind = 171;
/* 2257 */             jjCheckNAddStates(90, 92);
/* 2258 */             break;
/*      */           case 113:
/* 2260 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2262 */             if (kind > 171)
/* 2263 */               kind = 171;
/* 2264 */             jjCheckNAddStates(90, 92);
/* 2265 */             break;
/*      */           case 115:
/* 2267 */             if ((0x0 & l) == 0L) continue;
/* 2268 */             jjCheckNAdd(116); break;
/*      */           case 116:
/* 2271 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2273 */             if (kind > 171)
/* 2274 */               kind = 171;
/* 2275 */             jjCheckNAddTwoStates(116, 79);
/* 2276 */             break;
/*      */           case 117:
/* 2278 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2280 */             if (kind > 150)
/* 2281 */               kind = 150;
/* 2282 */             jjCheckNAddStates(38, 48);
/* 2283 */             break;
/*      */           case 118:
/* 2285 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2287 */             if (kind > 150)
/* 2288 */               kind = 150;
/* 2289 */             jjCheckNAdd(118);
/* 2290 */             break;
/*      */           case 119:
/* 2292 */             if ((0x0 & l) == 0L) continue;
/* 2293 */             jjCheckNAddTwoStates(119, 120); break;
/*      */           case 121:
/* 2296 */             if ((0x0 & l) == 0L) continue;
/* 2297 */             jjCheckNAddTwoStates(121, 122); break;
/*      */           case 123:
/* 2300 */             if ((0x0 & l) == 0L) continue;
/* 2301 */             jjCheckNAddStates(93, 95); break;
/*      */           case 128:
/* 2304 */             if ((0x0 & l) == 0L) continue;
/* 2305 */             jjCheckNAddTwoStates(128, 67); break;
/*      */           case 129:
/* 2308 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2310 */             if (kind > 167)
/* 2311 */               kind = 167;
/* 2312 */             jjCheckNAdd(129);
/* 2313 */             break;
/*      */           case 130:
/* 2315 */             if (curChar != '0') continue;
/* 2316 */             jjAddStates(49, 52); break;
/*      */           case 132:
/* 2319 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2321 */             if (kind > 154)
/* 2322 */               kind = 154;
/* 2323 */             jjstateSet[(jjnewStateCnt++)] = 132;
/* 2324 */             break;
/*      */           case 134:
/* 2326 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2328 */             if (kind > 155)
/* 2329 */               kind = 155;
/* 2330 */             jjAddStates(96, 97);
/* 2331 */             break;
/*      */           case 137:
/* 2333 */             if ((0x0 & l) == 0L) continue;
/* 2334 */             jjAddStates(98, 99); break;
/*      */           case 140:
/* 2337 */             if ((0x0 & l) == 0L) continue;
/* 2338 */             jjAddStates(100, 102); break;
/*      */           case 145:
/* 2341 */             if (curChar != '.') continue;
/* 2342 */             jjCheckNAddTwoStates(97, 146); break;
/*      */           case 146:
/* 2345 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2347 */             if (kind > 171)
/* 2348 */               kind = 171;
/* 2349 */             jjCheckNAddStates(68, 70);
/* 2350 */             break;
/*      */           case 148:
/* 2352 */             if ((0x0 & l) == 0L) continue;
/* 2353 */             jjCheckNAdd(149); break;
/*      */           case 149:
/* 2356 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2358 */             if (kind > 171)
/* 2359 */               kind = 171;
/* 2360 */             jjCheckNAddTwoStates(149, 79);
/* 2361 */             break;
/*      */           case 151:
/* 2363 */             if (curChar != '\'') continue;
/* 2364 */             jjCheckNAddTwoStates(152, 154); break;
/*      */           case 152:
/* 2367 */             if ((0xFFFFDBFF & l) == 0L) continue;
/* 2368 */             jjCheckNAdd(153); break;
/*      */           case 153:
/* 2371 */             if ((curChar != '\'') || (kind <= 162)) continue;
/* 2372 */             kind = 162; break;
/*      */           case 155:
/* 2375 */             if ((0x0 & l) == 0L) continue;
/* 2376 */             jjCheckNAdd(153); break;
/*      */           case 156:
/* 2379 */             if (curChar != '0') continue;
/* 2380 */             jjCheckNAddTwoStates(157, 153); break;
/*      */           case 157:
/* 2383 */             if ((0x0 & l) == 0L) continue;
/* 2384 */             jjCheckNAddTwoStates(157, 153); break;
/*      */           case 158:
/* 2387 */             if ((0x0 & l) == 0L) continue;
/* 2388 */             jjCheckNAddTwoStates(159, 153); break;
/*      */           case 159:
/* 2391 */             if ((0x0 & l) == 0L) continue;
/* 2392 */             jjCheckNAddTwoStates(159, 153); break;
/*      */           case 160:
/* 2395 */             if (curChar != '0') continue;
/* 2396 */             jjAddStates(103, 104); break;
/*      */           case 162:
/* 2399 */             if ((0x0 & l) == 0L) continue;
/* 2400 */             jjCheckNAddTwoStates(162, 153); break;
/*      */           case 164:
/* 2403 */             if (curChar != '"') continue;
/* 2404 */             jjCheckNAddStates(105, 107); break;
/*      */           case 165:
/* 2407 */             if ((0xFFFFDBFF & l) == 0L) continue;
/* 2408 */             jjCheckNAddStates(105, 107); break;
/*      */           case 167:
/* 2411 */             if ((0x0 & l) == 0L) continue;
/* 2412 */             jjCheckNAddStates(105, 107); break;
/*      */           case 168:
/* 2415 */             if ((curChar != '"') || (kind <= 163)) continue;
/* 2416 */             kind = 163; break;
/*      */           case 169:
/* 2419 */             if (curChar != '0') continue;
/* 2420 */             jjCheckNAddStates(108, 111); break;
/*      */           case 170:
/* 2423 */             if ((0x0 & l) == 0L) continue;
/* 2424 */             jjCheckNAddStates(108, 111); break;
/*      */           case 171:
/* 2427 */             if ((0x0 & l) == 0L) continue;
/* 2428 */             jjCheckNAddStates(112, 115); break;
/*      */           case 172:
/* 2431 */             if ((0x0 & l) == 0L) continue;
/* 2432 */             jjCheckNAddStates(112, 115); break;
/*      */           case 173:
/* 2435 */             if (curChar != '0') continue;
/* 2436 */             jjAddStates(116, 117); break;
/*      */           case 175:
/* 2439 */             if ((0x0 & l) == 0L) continue;
/* 2440 */             jjCheckNAddStates(118, 121); break;
/*      */           case 177:
/* 2443 */             if (curChar != '\'') continue;
/* 2444 */             jjCheckNAddStates(21, 25); break;
/*      */           case 178:
/* 2447 */             if ((0xFFFFDBFF & l) == 0L) continue;
/* 2448 */             jjCheckNAdd(179); break;
/*      */           case 179:
/* 2451 */             if ((curChar != '\'') || (kind <= 176)) continue;
/* 2452 */             kind = 176; break;
/*      */           case 181:
/* 2455 */             if ((0x0 & l) == 0L) continue;
/* 2456 */             jjCheckNAdd(179); break;
/*      */           case 182:
/* 2459 */             if ((0x0 & l) == 0L) continue;
/* 2460 */             jjCheckNAddTwoStates(183, 179); break;
/*      */           case 183:
/* 2463 */             if ((0x0 & l) == 0L) continue;
/* 2464 */             jjCheckNAdd(179); break;
/*      */           case 184:
/* 2467 */             if ((0x0 & l) == 0L) continue;
/* 2468 */             jjstateSet[(jjnewStateCnt++)] = 185; break;
/*      */           case 185:
/* 2471 */             if ((0x0 & l) == 0L) continue;
/* 2472 */             jjCheckNAdd(183); break;
/*      */           case 187:
/* 2475 */             if ((0x0 & l) == 0L) continue;
/* 2476 */             jjstateSet[(jjnewStateCnt++)] = 188; break;
/*      */           case 188:
/* 2479 */             if ((0x0 & l) == 0L) continue;
/* 2480 */             jjstateSet[(jjnewStateCnt++)] = 189; break;
/*      */           case 189:
/* 2483 */             if ((0x0 & l) == 0L) continue;
/* 2484 */             jjstateSet[(jjnewStateCnt++)] = 190; break;
/*      */           case 190:
/* 2487 */             if ((0x0 & l) == 0L) continue;
/* 2488 */             jjCheckNAdd(179); break;
/*      */           case 192:
/* 2491 */             if (curChar != '"') continue;
/* 2492 */             jjCheckNAddStates(14, 20); break;
/*      */           case 193:
/* 2495 */             if ((0xFFFFDBFF & l) == 0L) continue;
/* 2496 */             jjCheckNAddStates(122, 125); break;
/*      */           case 195:
/* 2499 */             if ((0x0 & l) == 0L) continue;
/* 2500 */             jjCheckNAddStates(122, 125); break;
/*      */           case 197:
/* 2503 */             if ((0x0 & l) == 0L) continue;
/* 2504 */             jjstateSet[(jjnewStateCnt++)] = 198; break;
/*      */           case 198:
/* 2507 */             if ((0x0 & l) == 0L) continue;
/* 2508 */             jjstateSet[(jjnewStateCnt++)] = 199; break;
/*      */           case 199:
/* 2511 */             if ((0x0 & l) == 0L) continue;
/* 2512 */             jjstateSet[(jjnewStateCnt++)] = 200; break;
/*      */           case 200:
/* 2515 */             if ((0x0 & l) == 0L) continue;
/* 2516 */             jjCheckNAddStates(122, 125); break;
/*      */           case 202:
/* 2519 */             if ((curChar != '"') || (kind <= 177)) continue;
/* 2520 */             kind = 177; break;
/*      */           case 203:
/* 2523 */             if ((0x0 & l) == 0L) continue;
/* 2524 */             jjCheckNAddStates(126, 130); break;
/*      */           case 204:
/* 2527 */             if ((0x0 & l) == 0L) continue;
/* 2528 */             jjCheckNAddStates(122, 125); break;
/*      */           case 205:
/* 2531 */             if ((0x0 & l) == 0L) continue;
/* 2532 */             jjstateSet[(jjnewStateCnt++)] = 206; break;
/*      */           case 206:
/* 2535 */             if ((0x0 & l) == 0L) continue;
/* 2536 */             jjCheckNAdd(204); break;
/*      */           case 208:
/* 2539 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2541 */             if (kind > 164)
/* 2542 */               kind = 164;
/* 2543 */             jjCheckNAdd(208);
/* 2544 */             break;
/*      */           case 209:
/* 2546 */             if ((0x0 & l) == 0L)
/*      */               continue;
/* 2548 */             if (kind > 165)
/* 2549 */               kind = 165;
/* 2550 */             jjCheckNAdd(209);
/*      */           case 7:
/*      */           case 12:
/*      */           case 13:
/*      */           case 14:
/*      */           case 15:
/*      */           case 19:
/*      */           case 23:
/*      */           case 24:
/*      */           case 25:
/*      */           case 26:
/*      */           case 27:
/*      */           case 28:
/*      */           case 30:
/*      */           case 31:
/*      */           case 32:
/*      */           case 33:
/*      */           case 34:
/*      */           case 35:
/*      */           case 37:
/*      */           case 39:
/*      */           case 40:
/*      */           case 41:
/*      */           case 42:
/*      */           case 43:
/*      */           case 45:
/*      */           case 47:
/*      */           case 48:
/*      */           case 49:
/*      */           case 50:
/*      */           case 51:
/*      */           case 52:
/*      */           case 53:
/*      */           case 57:
/*      */           case 59:
/*      */           case 61:
/*      */           case 62:
/*      */           case 63:
/*      */           case 64:
/*      */           case 65:
/*      */           case 67:
/*      */           case 69:
/*      */           case 72:
/*      */           case 76:
/*      */           case 79:
/*      */           case 80:
/*      */           case 83:
/*      */           case 91:
/*      */           case 94:
/*      */           case 99:
/*      */           case 102:
/*      */           case 104:
/*      */           case 108:
/*      */           case 114:
/*      */           case 120:
/*      */           case 122:
/*      */           case 124:
/*      */           case 125:
/*      */           case 126:
/*      */           case 127:
/*      */           case 131:
/*      */           case 133:
/*      */           case 135:
/*      */           case 136:
/*      */           case 138:
/*      */           case 139:
/*      */           case 141:
/*      */           case 142:
/*      */           case 143:
/*      */           case 144:
/*      */           case 147:
/*      */           case 150:
/*      */           case 154:
/*      */           case 161:
/*      */           case 163:
/*      */           case 166:
/*      */           case 174:
/*      */           case 176:
/*      */           case 180:
/*      */           case 186:
/*      */           case 191:
/*      */           case 194:
/*      */           case 196:
/*      */           case 201:
/* 2554 */           case 207: }  } while (i != startsAt);
/*      */       }
/* 2556 */       else if (curChar < '')
/*      */       {
/* 2558 */         long l = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/* 2561 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 212:
/* 2564 */             if (curChar == 'p')
/* 2565 */               jjstateSet[(jjnewStateCnt++)] = 52;
/* 2566 */             else if (curChar == 'd')
/* 2567 */               jjstateSet[(jjnewStateCnt++)] = 42;
/* 2568 */             else if (curChar == 'i')
/* 2569 */               jjstateSet[(jjnewStateCnt++)] = 34;
/* 2570 */             else if (curChar == 'l')
/* 2571 */               jjstateSet[(jjnewStateCnt++)] = 14;
/* 2572 */             if (curChar != 'i') continue;
/* 2573 */             jjstateSet[(jjnewStateCnt++)] = 27; break;
/*      */           case 211:
/* 2576 */             if ((0x87FFFFFE & l) != 0L)
/*      */             {
/* 2578 */               if (kind > 178)
/* 2579 */                 kind = 178;
/* 2580 */               jjCheckNAdd(9);
/*      */             }
/* 2582 */             if ((0x87FFFFFE & l) != 0L)
/*      */             {
/* 2584 */               if (kind > 165)
/* 2585 */                 kind = 165;
/* 2586 */               jjCheckNAdd(209);
/*      */             }
/* 2588 */             if ((0x87FFFFFE & l) == 0L)
/*      */               continue;
/* 2590 */             if (kind > 164)
/* 2591 */               kind = 164;
/* 2592 */             jjCheckNAdd(208); break;
/*      */           case 4:
/* 2596 */             if ((0x87FFFFFE & l) != 0L)
/*      */             {
/* 2598 */               if (kind > 178)
/* 2599 */                 kind = 178;
/* 2600 */               jjCheckNAdd(9);
/*      */             }
/* 2602 */             if ((0x87FFFFFE & l) != 0L)
/*      */             {
/* 2604 */               if (kind > 165)
/* 2605 */                 kind = 165;
/* 2606 */               jjCheckNAdd(209);
/*      */             }
/* 2608 */             if ((0x87FFFFFE & l) == 0L)
/*      */               continue;
/* 2610 */             if (kind > 164)
/* 2611 */               kind = 164;
/* 2612 */             jjCheckNAdd(208); break;
/*      */           case 3:
/* 2616 */             if ((0x87FFFFFE & l) != 0L)
/*      */             {
/* 2618 */               if (kind > 164)
/* 2619 */                 kind = 164;
/* 2620 */               jjCheckNAddTwoStates(208, 209);
/*      */             }
/* 2622 */             if ((0x87FFFFFE & l) != 0L)
/*      */             {
/* 2624 */               if (kind > 178)
/* 2625 */                 kind = 178;
/* 2626 */               jjCheckNAdd(9);
/*      */             }
/* 2628 */             if ((0x8 & l) != 0L) {
/* 2629 */               jjstateSet[(jjnewStateCnt++)] = 4;
/* 2630 */             } else if ((0x10 & l) != 0L) {
/* 2631 */               jjstateSet[(jjnewStateCnt++)] = 0; } else {
/* 2632 */               if (curChar != 'L') continue;
/* 2633 */               jjAddStates(131, 132); } break;
/*      */           case 0:
/* 2636 */             if ((0x87FFFFFE & l) != 0L)
/*      */             {
/* 2638 */               if (kind > 178)
/* 2639 */                 kind = 178;
/* 2640 */               jjCheckNAdd(9);
/*      */             }
/* 2642 */             if ((0x87FFFFFE & l) != 0L)
/*      */             {
/* 2644 */               if (kind > 165)
/* 2645 */                 kind = 165;
/* 2646 */               jjCheckNAdd(209);
/*      */             }
/* 2648 */             if ((0x87FFFFFE & l) == 0L)
/*      */               continue;
/* 2650 */             if (kind > 164)
/* 2651 */               kind = 164;
/* 2652 */             jjCheckNAdd(208); break;
/*      */           case 5:
/* 2656 */             if ((0x100007E & l) == 0L) continue;
/* 2657 */             jjAddStates(133, 134); break;
/*      */           case 7:
/* 2660 */             if ((0x8 & l) == 0L) continue;
/* 2661 */             jjstateSet[(jjnewStateCnt++)] = 4; break;
/*      */           case 8:
/* 2664 */             if ((0x87FFFFFE & l) == 0L)
/*      */               continue;
/* 2666 */             if (kind > 178)
/* 2667 */               kind = 178;
/* 2668 */             jjCheckNAdd(9);
/* 2669 */             break;
/*      */           case 9:
/* 2671 */             if ((0x87FFFFFE & l) == 0L)
/*      */               continue;
/* 2673 */             if (kind > 178)
/* 2674 */               kind = 178;
/* 2675 */             jjCheckNAdd(9);
/* 2676 */             break;
/*      */           case 12:
/* 2678 */             if ((curChar != 'e') || (kind <= 7)) continue;
/* 2679 */             kind = 7; break;
/*      */           case 13:
/* 2682 */             if (curChar != 'n') continue;
/* 2683 */             jjstateSet[(jjnewStateCnt++)] = 12; break;
/*      */           case 14:
/* 2686 */             if (curChar != 'i') continue;
/* 2687 */             jjstateSet[(jjnewStateCnt++)] = 13; break;
/*      */           case 15:
/* 2690 */             if (curChar != 'l') continue;
/* 2691 */             jjstateSet[(jjnewStateCnt++)] = 14; break;
/*      */           case 19:
/* 2694 */             if (curChar != 'e') continue;
/* 2695 */             jjAddStates(74, 75); break;
/*      */           case 23:
/* 2698 */             if (curChar != 'd') continue;
/* 2699 */             jjstateSet[(jjnewStateCnt++)] = 19; break;
/*      */           case 24:
/* 2702 */             if (curChar != 'u') continue;
/* 2703 */             jjstateSet[(jjnewStateCnt++)] = 23; break;
/*      */           case 25:
/* 2706 */             if (curChar != 'l') continue;
/* 2707 */             jjstateSet[(jjnewStateCnt++)] = 24; break;
/*      */           case 26:
/* 2710 */             if (curChar != 'c') continue;
/* 2711 */             jjstateSet[(jjnewStateCnt++)] = 25; break;
/*      */           case 27:
/* 2714 */             if (curChar != 'n') continue;
/* 2715 */             jjstateSet[(jjnewStateCnt++)] = 26; break;
/*      */           case 28:
/* 2718 */             if (curChar != 'i') continue;
/* 2719 */             jjstateSet[(jjnewStateCnt++)] = 27; break;
/*      */           case 30:
/* 2722 */             if ((curChar != 't') || (kind <= 10)) continue;
/* 2723 */             kind = 10; break;
/*      */           case 31:
/* 2726 */             if (curChar != 'r') continue;
/* 2727 */             jjstateSet[(jjnewStateCnt++)] = 30; break;
/*      */           case 32:
/* 2730 */             if (curChar != 'o') continue;
/* 2731 */             jjstateSet[(jjnewStateCnt++)] = 31; break;
/*      */           case 33:
/* 2734 */             if (curChar != 'p') continue;
/* 2735 */             jjstateSet[(jjnewStateCnt++)] = 32; break;
/*      */           case 34:
/* 2738 */             if (curChar != 'm') continue;
/* 2739 */             jjstateSet[(jjnewStateCnt++)] = 33; break;
/*      */           case 35:
/* 2742 */             if (curChar != 'i') continue;
/* 2743 */             jjstateSet[(jjnewStateCnt++)] = 34; break;
/*      */           case 37:
/* 2746 */             if (curChar != 'e')
/*      */               continue;
/* 2748 */             if (kind > 11)
/* 2749 */               kind = 11;
/* 2750 */             jjstateSet[(jjnewStateCnt++)] = 38;
/* 2751 */             break;
/*      */           case 39:
/* 2753 */             if (curChar != 'n') continue;
/* 2754 */             jjstateSet[(jjnewStateCnt++)] = 37; break;
/*      */           case 40:
/* 2757 */             if (curChar != 'i') continue;
/* 2758 */             jjstateSet[(jjnewStateCnt++)] = 39; break;
/*      */           case 41:
/* 2761 */             if (curChar != 'f') continue;
/* 2762 */             jjstateSet[(jjnewStateCnt++)] = 40; break;
/*      */           case 42:
/* 2765 */             if (curChar != 'e') continue;
/* 2766 */             jjstateSet[(jjnewStateCnt++)] = 41; break;
/*      */           case 43:
/* 2769 */             if (curChar != 'd') continue;
/* 2770 */             jjstateSet[(jjnewStateCnt++)] = 42; break;
/*      */           case 45:
/* 2773 */             if (curChar != 'y')
/*      */               continue;
/* 2775 */             if (kind > 12)
/* 2776 */               kind = 12;
/* 2777 */             jjstateSet[(jjnewStateCnt++)] = 46;
/* 2778 */             break;
/*      */           case 47:
/* 2780 */             if (curChar != 't') continue;
/* 2781 */             jjstateSet[(jjnewStateCnt++)] = 45; break;
/*      */           case 48:
/* 2784 */             if (curChar != 'r') continue;
/* 2785 */             jjstateSet[(jjnewStateCnt++)] = 47; break;
/*      */           case 49:
/* 2788 */             if (curChar != 'e') continue;
/* 2789 */             jjstateSet[(jjnewStateCnt++)] = 48; break;
/*      */           case 50:
/* 2792 */             if (curChar != 'p') continue;
/* 2793 */             jjstateSet[(jjnewStateCnt++)] = 49; break;
/*      */           case 51:
/* 2796 */             if (curChar != 'o') continue;
/* 2797 */             jjstateSet[(jjnewStateCnt++)] = 50; break;
/*      */           case 52:
/* 2800 */             if (curChar != 'r') continue;
/* 2801 */             jjstateSet[(jjnewStateCnt++)] = 51; break;
/*      */           case 53:
/* 2804 */             if (curChar != 'p') continue;
/* 2805 */             jjstateSet[(jjnewStateCnt++)] = 52; break;
/*      */           case 57:
/* 2808 */             if (((0x1000 & l) == 0L) || (kind <= 146)) continue;
/* 2809 */             kind = 146; break;
/*      */           case 59:
/* 2812 */             if (((0x200000 & l) == 0L) || (kind <= 147)) continue;
/* 2813 */             kind = 147; break;
/*      */           case 61:
/* 2816 */             if (((0x1000 & l) == 0L) || (kind <= 148)) continue;
/* 2817 */             kind = 148; break;
/*      */           case 62:
/* 2820 */             if ((0x200000 & l) == 0L) continue;
/* 2821 */             jjstateSet[(jjnewStateCnt++)] = 61; break;
/*      */           case 63:
/* 2824 */             if (((0x200000 & l) == 0L) || (kind <= 148)) continue;
/* 2825 */             kind = 148; break;
/*      */           case 64:
/* 2828 */             if ((0x1000 & l) == 0L) continue;
/* 2829 */             jjstateSet[(jjnewStateCnt++)] = 63; break;
/*      */           case 65:
/* 2832 */             if ((0x1000000 & l) == 0L) continue;
/* 2833 */             jjCheckNAdd(66); break;
/*      */           case 66:
/* 2836 */             if ((0x7E & l) == 0L) continue;
/* 2837 */             jjCheckNAddTwoStates(66, 67); break;
/*      */           case 67:
/* 2840 */             if (((0x1000 & l) == 0L) || (kind <= 166)) continue;
/* 2841 */             kind = 166; break;
/*      */           case 69:
/* 2844 */             if ((0x1000000 & l) == 0L) continue;
/* 2845 */             jjCheckNAdd(70); break;
/*      */           case 70:
/* 2848 */             if ((0x7E & l) == 0L)
/*      */               continue;
/* 2850 */             if (kind > 167)
/* 2851 */               kind = 167;
/* 2852 */             jjCheckNAdd(70);
/* 2853 */             break;
/*      */           case 72:
/* 2855 */             if ((0x1000000 & l) == 0L) continue;
/* 2856 */             jjCheckNAddTwoStates(73, 74); break;
/*      */           case 73:
/* 2859 */             if ((0x7E & l) == 0L) continue;
/* 2860 */             jjCheckNAddTwoStates(73, 74); break;
/*      */           case 75:
/* 2863 */             if ((0x7E & l) == 0L) continue;
/* 2864 */             jjAddStates(135, 136); break;
/*      */           case 76:
/* 2867 */             if ((0x10000 & l) == 0L) continue;
/* 2868 */             jjAddStates(137, 138); break;
/*      */           case 79:
/* 2871 */             if (((0x50 & l) == 0L) || (kind <= 171)) continue;
/* 2872 */             kind = 171; break;
/*      */           case 80:
/* 2875 */             if ((0x1000000 & l) == 0L) continue;
/* 2876 */             jjCheckNAdd(81); break;
/*      */           case 81:
/* 2879 */             if ((0x7E & l) == 0L) continue;
/* 2880 */             jjCheckNAddStates(81, 83); break;
/*      */           case 83:
/* 2883 */             if ((0x10000 & l) == 0L) continue;
/* 2884 */             jjAddStates(139, 140); break;
/*      */           case 91:
/* 2887 */             if ((0x20 & l) == 0L) continue;
/* 2888 */             jjAddStates(141, 142); break;
/*      */           case 94:
/* 2891 */             if (((0x1040 & l) == 0L) || (kind <= 158)) continue;
/* 2892 */             kind = 158; break;
/*      */           case 99:
/* 2895 */             if ((0x20 & l) == 0L) continue;
/* 2896 */             jjAddStates(143, 144); break;
/*      */           case 102:
/* 2899 */             if (((0x1040 & l) == 0L) || (kind <= 159)) continue;
/* 2900 */             kind = 159; break;
/*      */           case 104:
/* 2903 */             if ((0x20 & l) == 0L) continue;
/* 2904 */             jjAddStates(145, 146); break;
/*      */           case 108:
/* 2907 */             if ((0x20 & l) == 0L) continue;
/* 2908 */             jjAddStates(147, 148); break;
/*      */           case 114:
/* 2911 */             if ((0x20 & l) == 0L) continue;
/* 2912 */             jjAddStates(149, 150); break;
/*      */           case 120:
/* 2915 */             if (((0x201000 & l) == 0L) || (kind <= 151)) continue;
/* 2916 */             kind = 151; break;
/*      */           case 122:
/* 2919 */             if (((0x200000 & l) == 0L) || (kind <= 152)) continue;
/* 2920 */             kind = 152; break;
/*      */           case 124:
/* 2923 */             if (((0x1000 & l) == 0L) || (kind <= 153)) continue;
/* 2924 */             kind = 153; break;
/*      */           case 125:
/* 2927 */             if ((0x200000 & l) == 0L) continue;
/* 2928 */             jjstateSet[(jjnewStateCnt++)] = 124; break;
/*      */           case 126:
/* 2931 */             if (((0x200000 & l) == 0L) || (kind <= 153)) continue;
/* 2932 */             kind = 153; break;
/*      */           case 127:
/* 2935 */             if ((0x1000 & l) == 0L) continue;
/* 2936 */             jjstateSet[(jjnewStateCnt++)] = 126; break;
/*      */           case 131:
/* 2939 */             if ((0x1000000 & l) == 0L) continue;
/* 2940 */             jjCheckNAdd(132); break;
/*      */           case 132:
/* 2943 */             if ((0x7E & l) == 0L)
/*      */               continue;
/* 2945 */             if (kind > 154)
/* 2946 */               kind = 154;
/* 2947 */             jjCheckNAdd(132);
/* 2948 */             break;
/*      */           case 133:
/* 2950 */             if ((0x1000000 & l) == 0L) continue;
/* 2951 */             jjCheckNAdd(134); break;
/*      */           case 134:
/* 2954 */             if ((0x7E & l) == 0L)
/*      */               continue;
/* 2956 */             if (kind > 155)
/* 2957 */               kind = 155;
/* 2958 */             jjCheckNAddTwoStates(134, 135);
/* 2959 */             break;
/*      */           case 135:
/* 2961 */             if (((0x201000 & l) == 0L) || (kind <= 155)) continue;
/* 2962 */             kind = 155; break;
/*      */           case 136:
/* 2965 */             if ((0x1000000 & l) == 0L) continue;
/* 2966 */             jjCheckNAdd(137); break;
/*      */           case 137:
/* 2969 */             if ((0x7E & l) == 0L) continue;
/* 2970 */             jjCheckNAddTwoStates(137, 138); break;
/*      */           case 138:
/* 2973 */             if (((0x200000 & l) == 0L) || (kind <= 156)) continue;
/* 2974 */             kind = 156; break;
/*      */           case 139:
/* 2977 */             if ((0x1000000 & l) == 0L) continue;
/* 2978 */             jjCheckNAdd(140); break;
/*      */           case 140:
/* 2981 */             if ((0x7E & l) == 0L) continue;
/* 2982 */             jjCheckNAddStates(100, 102); break;
/*      */           case 141:
/* 2985 */             if (((0x1000 & l) == 0L) || (kind <= 157)) continue;
/* 2986 */             kind = 157; break;
/*      */           case 142:
/* 2989 */             if ((0x200000 & l) == 0L) continue;
/* 2990 */             jjstateSet[(jjnewStateCnt++)] = 141; break;
/*      */           case 143:
/* 2993 */             if (((0x200000 & l) == 0L) || (kind <= 157)) continue;
/* 2994 */             kind = 157; break;
/*      */           case 144:
/* 2997 */             if ((0x1000 & l) == 0L) continue;
/* 2998 */             jjstateSet[(jjnewStateCnt++)] = 143; break;
/*      */           case 147:
/* 3001 */             if ((0x20 & l) == 0L) continue;
/* 3002 */             jjAddStates(151, 152); break;
/*      */           case 150:
/* 3005 */             if (curChar != 'L') continue;
/* 3006 */             jjAddStates(131, 132); break;
/*      */           case 152:
/* 3009 */             if ((0xEFFFFFFF & l) == 0L) continue;
/* 3010 */             jjCheckNAdd(153); break;
/*      */           case 154:
/* 3013 */             if (curChar != '\\') continue;
/* 3014 */             jjAddStates(153, 156); break;
/*      */           case 155:
/* 3017 */             if ((0x10000000 & l) == 0L) continue;
/* 3018 */             jjCheckNAdd(153); break;
/*      */           case 161:
/* 3021 */             if (curChar != 'x') continue;
/* 3022 */             jjCheckNAdd(162); break;
/*      */           case 162:
/* 3025 */             if ((0x7E & l) == 0L) continue;
/* 3026 */             jjCheckNAddTwoStates(162, 153); break;
/*      */           case 163:
/* 3029 */             if (curChar != 'X') continue;
/* 3030 */             jjCheckNAdd(162); break;
/*      */           case 165:
/* 3033 */             if ((0xEFFFFFFF & l) == 0L) continue;
/* 3034 */             jjCheckNAddStates(105, 107); break;
/*      */           case 166:
/* 3037 */             if (curChar != '\\') continue;
/* 3038 */             jjAddStates(157, 160); break;
/*      */           case 167:
/* 3041 */             if ((0x10000000 & l) == 0L) continue;
/* 3042 */             jjCheckNAddStates(105, 107); break;
/*      */           case 174:
/* 3045 */             if (curChar != 'x') continue;
/* 3046 */             jjCheckNAdd(175); break;
/*      */           case 175:
/* 3049 */             if ((0x7E & l) == 0L) continue;
/* 3050 */             jjCheckNAddStates(118, 121); break;
/*      */           case 176:
/* 3053 */             if (curChar != 'X') continue;
/* 3054 */             jjCheckNAdd(175); break;
/*      */           case 178:
/* 3057 */             if ((0xEFFFFFFF & l) == 0L) continue;
/* 3058 */             jjCheckNAdd(179); break;
/*      */           case 180:
/* 3061 */             if (curChar != '\\') continue;
/* 3062 */             jjAddStates(161, 163); break;
/*      */           case 181:
/* 3065 */             if ((0x10000000 & l) == 0L) continue;
/* 3066 */             jjCheckNAdd(179); break;
/*      */           case 186:
/* 3069 */             if (curChar != 'u') continue;
/* 3070 */             jjstateSet[(jjnewStateCnt++)] = 187; break;
/*      */           case 187:
/* 3073 */             if ((0x7E & l) == 0L) continue;
/* 3074 */             jjstateSet[(jjnewStateCnt++)] = 188; break;
/*      */           case 188:
/* 3077 */             if ((0x7E & l) == 0L) continue;
/* 3078 */             jjstateSet[(jjnewStateCnt++)] = 189; break;
/*      */           case 189:
/* 3081 */             if ((0x7E & l) == 0L) continue;
/* 3082 */             jjstateSet[(jjnewStateCnt++)] = 190; break;
/*      */           case 190:
/* 3085 */             if ((0x7E & l) == 0L) continue;
/* 3086 */             jjCheckNAdd(179); break;
/*      */           case 191:
/* 3089 */             if (curChar != '\\') continue;
/* 3090 */             jjstateSet[(jjnewStateCnt++)] = 186; break;
/*      */           case 193:
/* 3093 */             if ((0xEFFFFFFF & l) == 0L) continue;
/* 3094 */             jjCheckNAddStates(122, 125); break;
/*      */           case 194:
/* 3097 */             if (curChar != '\\') continue;
/* 3098 */             jjAddStates(164, 166); break;
/*      */           case 195:
/* 3101 */             if ((0x10000000 & l) == 0L) continue;
/* 3102 */             jjCheckNAddStates(122, 125); break;
/*      */           case 196:
/* 3105 */             if (curChar != 'u') continue;
/* 3106 */             jjstateSet[(jjnewStateCnt++)] = 197; break;
/*      */           case 197:
/* 3109 */             if ((0x7E & l) == 0L) continue;
/* 3110 */             jjstateSet[(jjnewStateCnt++)] = 198; break;
/*      */           case 198:
/* 3113 */             if ((0x7E & l) == 0L) continue;
/* 3114 */             jjstateSet[(jjnewStateCnt++)] = 199; break;
/*      */           case 199:
/* 3117 */             if ((0x7E & l) == 0L) continue;
/* 3118 */             jjstateSet[(jjnewStateCnt++)] = 200; break;
/*      */           case 200:
/* 3121 */             if ((0x7E & l) == 0L) continue;
/* 3122 */             jjCheckNAddStates(122, 125); break;
/*      */           case 201:
/* 3125 */             if (curChar != '\\') continue;
/* 3126 */             jjstateSet[(jjnewStateCnt++)] = 196; break;
/*      */           case 207:
/* 3129 */             if ((0x87FFFFFE & l) == 0L)
/*      */               continue;
/* 3131 */             if (kind > 164)
/* 3132 */               kind = 164;
/* 3133 */             jjCheckNAddTwoStates(208, 209);
/* 3134 */             break;
/*      */           case 208:
/* 3136 */             if ((0x87FFFFFE & l) == 0L)
/*      */               continue;
/* 3138 */             if (kind > 164)
/* 3139 */               kind = 164;
/* 3140 */             jjCheckNAdd(208);
/* 3141 */             break;
/*      */           case 209:
/* 3143 */             if ((0x87FFFFFE & l) == 0L)
/*      */               continue;
/* 3145 */             if (kind > 165)
/* 3146 */               kind = 165;
/* 3147 */             jjCheckNAdd(209);
/*      */           case 1:
/*      */           case 2:
/*      */           case 6:
/*      */           case 10:
/*      */           case 11:
/*      */           case 16:
/*      */           case 17:
/*      */           case 18:
/*      */           case 20:
/*      */           case 21:
/*      */           case 22:
/*      */           case 29:
/*      */           case 36:
/*      */           case 38:
/*      */           case 44:
/*      */           case 46:
/*      */           case 54:
/*      */           case 55:
/*      */           case 56:
/*      */           case 58:
/*      */           case 60:
/*      */           case 68:
/*      */           case 71:
/*      */           case 74:
/*      */           case 77:
/*      */           case 78:
/*      */           case 82:
/*      */           case 84:
/*      */           case 85:
/*      */           case 86:
/*      */           case 87:
/*      */           case 88:
/*      */           case 89:
/*      */           case 90:
/*      */           case 92:
/*      */           case 93:
/*      */           case 95:
/*      */           case 96:
/*      */           case 97:
/*      */           case 98:
/*      */           case 100:
/*      */           case 101:
/*      */           case 103:
/*      */           case 105:
/*      */           case 106:
/*      */           case 107:
/*      */           case 109:
/*      */           case 110:
/*      */           case 111:
/*      */           case 112:
/*      */           case 113:
/*      */           case 115:
/*      */           case 116:
/*      */           case 117:
/*      */           case 118:
/*      */           case 119:
/*      */           case 121:
/*      */           case 123:
/*      */           case 128:
/*      */           case 129:
/*      */           case 130:
/*      */           case 145:
/*      */           case 146:
/*      */           case 148:
/*      */           case 149:
/*      */           case 151:
/*      */           case 153:
/*      */           case 156:
/*      */           case 157:
/*      */           case 158:
/*      */           case 159:
/*      */           case 160:
/*      */           case 164:
/*      */           case 168:
/*      */           case 169:
/*      */           case 170:
/*      */           case 171:
/*      */           case 172:
/*      */           case 173:
/*      */           case 177:
/*      */           case 179:
/*      */           case 182:
/*      */           case 183:
/*      */           case 184:
/*      */           case 185:
/*      */           case 192:
/*      */           case 202:
/*      */           case 203:
/*      */           case 204:
/*      */           case 205:
/*      */           case 206:
/* 3151 */           case 210: }  } while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/* 3155 */         int hiByte = curChar >> '\b';
/* 3156 */         int i1 = hiByte >> 6;
/* 3157 */         long l1 = 1L << (hiByte & 0x3F);
/* 3158 */         int i2 = (curChar & 0xFF) >> '\006';
/* 3159 */         long l2 = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/* 3162 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 9:
/*      */           case 211:
/* 3166 */             if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
/*      */               continue;
/* 3168 */             if (kind > 178)
/* 3169 */               kind = 178;
/* 3170 */             jjCheckNAdd(9);
/* 3171 */             break;
/*      */           case 4:
/* 3173 */             if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
/*      */               continue;
/* 3175 */             if (kind > 178)
/* 3176 */               kind = 178;
/* 3177 */             jjCheckNAdd(9);
/* 3178 */             break;
/*      */           case 3:
/* 3180 */             if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */               continue;
/* 3182 */             if (kind > 178)
/* 3183 */               kind = 178;
/* 3184 */             jjCheckNAdd(9);
/* 3185 */             break;
/*      */           case 0:
/* 3187 */             if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
/*      */               continue;
/* 3189 */             if (kind > 178)
/* 3190 */               kind = 178;
/* 3191 */             jjCheckNAdd(9);
/* 3192 */             break;
/*      */           case 152:
/* 3194 */             if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) continue;
/* 3195 */             jjstateSet[(jjnewStateCnt++)] = 153; break;
/*      */           case 165:
/* 3198 */             if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) continue;
/* 3199 */             jjAddStates(105, 107); break;
/*      */           case 178:
/* 3202 */             if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) continue;
/* 3203 */             jjstateSet[(jjnewStateCnt++)] = 179; break;
/*      */           case 193:
/* 3206 */             if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) continue;
/* 3207 */             jjAddStates(122, 125);
/*      */           }
/*      */         }
/*      */ 
/* 3211 */         while (i != startsAt);
/*      */       }
/* 3213 */       if (kind != 2147483647)
/*      */       {
/* 3215 */         jjmatchedKind = kind;
/* 3216 */         jjmatchedPos = curPos;
/* 3217 */         kind = 2147483647;
/*      */       }
/* 3219 */       curPos++;
/* 3220 */       if ((i = jjnewStateCnt) == (startsAt = 210 - (CPPParserTokenManager.jjnewStateCnt = startsAt)))
/* 3221 */         return curPos; try {
/* 3222 */         curChar = JavaCharStream.readChar(); } catch (IOException e) {
/*      */       }
/* 3223 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa0_9()
/*      */   {
/* 3228 */     switch (curChar)
/*      */     {
/*      */     case '*':
/* 3231 */       return jjMoveStringLiteralDfa1_9(4294967296L);
/*      */     }
/* 3233 */     return 1;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa1_9(long active0) {
/*      */     try {
/* 3238 */       curChar = JavaCharStream.readChar();
/*      */     } catch (IOException e) {
/* 3240 */       return 1;
/*      */     }
/* 3242 */     switch (curChar)
/*      */     {
/*      */     case '/':
/* 3245 */       if ((active0 & 0x0) == 0L) break;
/* 3246 */       return jjStopAtPos(1, 32);
/*      */     default:
/* 3249 */       return 2;
/*      */     }
/* 3251 */     return 2;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa0_10() {
/* 3255 */     return jjMoveNfa_10(0, 0);
/*      */   }
/*      */ 
/*      */   private static int jjMoveNfa_10(int startState, int curPos) {
/* 3259 */     int startsAt = 0;
/* 3260 */     jjnewStateCnt = 3;
/* 3261 */     int i = 1;
/* 3262 */     jjstateSet[0] = startState;
/* 3263 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/* 3266 */       if (++jjround == 2147483647)
/* 3267 */         ReInitRounds();
/* 3268 */       if (curChar < '@')
/*      */       {
/* 3270 */         long l = 1L << curChar;
/*      */         do
/*      */         {
/* 3273 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/* 3276 */             if ((0x2400 & l) != 0L)
/*      */             {
/* 3278 */               if (kind > 34)
/* 3279 */                 kind = 34;
/*      */             }
/* 3281 */             if (curChar != '\r') continue;
/* 3282 */             jjstateSet[(jjnewStateCnt++)] = 1; break;
/*      */           case 1:
/* 3285 */             if ((curChar != '\n') || (kind <= 34)) continue;
/* 3286 */             kind = 34; break;
/*      */           case 2:
/* 3289 */             if (curChar != '\r') continue;
/* 3290 */             jjstateSet[(jjnewStateCnt++)] = 1;
/*      */           }
/*      */         }
/*      */ 
/* 3294 */         while (i != startsAt);
/*      */       }
/* 3296 */       else if (curChar < '')
/*      */       {
/* 3298 */         long l = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/* 3301 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           }
/*      */         }
/* 3305 */         while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/* 3309 */         int hiByte = curChar >> '\b';
/* 3310 */         int i1 = hiByte >> 6;
/* 3311 */         long l1 = 1L << (hiByte & 0x3F);
/* 3312 */         int i2 = (curChar & 0xFF) >> '\006';
/* 3313 */         long l2 = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/* 3316 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           }
/*      */         }
/* 3320 */         while (i != startsAt);
/*      */       }
/* 3322 */       if (kind != 2147483647)
/*      */       {
/* 3324 */         jjmatchedKind = kind;
/* 3325 */         jjmatchedPos = curPos;
/* 3326 */         kind = 2147483647;
/*      */       }
/* 3328 */       curPos++;
/* 3329 */       if ((i = jjnewStateCnt) == (startsAt = 3 - (CPPParserTokenManager.jjnewStateCnt = startsAt)))
/* 3330 */         return curPos; try {
/* 3331 */         curChar = JavaCharStream.readChar(); } catch (IOException e) {
/*      */       }
/* 3332 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa0_7()
/*      */   {
/* 3337 */     return 1;
/*      */   }
/*      */ 
/*      */   private static int jjMoveStringLiteralDfa0_2() {
/* 3341 */     return jjMoveNfa_2(1, 0);
/*      */   }
/*      */ 
/*      */   private static int jjMoveNfa_2(int startState, int curPos) {
/* 3345 */     int startsAt = 0;
/* 3346 */     jjnewStateCnt = 5;
/* 3347 */     int i = 1;
/* 3348 */     jjstateSet[0] = startState;
/* 3349 */     int kind = 2147483647;
/*      */     while (true)
/*      */     {
/* 3352 */       if (++jjround == 2147483647)
/* 3353 */         ReInitRounds();
/* 3354 */       if (curChar < '@')
/*      */       {
/* 3356 */         long l = 1L << curChar;
/*      */         do
/*      */         {
/* 3359 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 1:
/* 3362 */             if ((0xFFFFDBFF & l) != 0L)
/*      */             {
/* 3364 */               if (kind > 17)
/* 3365 */                 kind = 17;
/* 3366 */               jjCheckNAdd(0);
/*      */             }
/* 3368 */             else if ((0x2400 & l) != 0L)
/*      */             {
/* 3370 */               if (kind > 19)
/* 3371 */                 kind = 19;
/*      */             }
/* 3373 */             if ((0x200 & l) != 0L)
/*      */             {
/* 3375 */               if (kind <= 18) continue;
/* 3376 */               kind = 18;
/*      */             } else {
/* 3378 */               if (curChar != '\r') continue;
/* 3379 */               jjstateSet[(jjnewStateCnt++)] = 3; } break;
/*      */           case 0:
/* 3382 */             if ((0xFFFFDBFF & l) == 0L)
/*      */               continue;
/* 3384 */             if (kind > 17)
/* 3385 */               kind = 17;
/* 3386 */             jjCheckNAdd(0);
/* 3387 */             break;
/*      */           case 2:
/* 3389 */             if (((0x2400 & l) == 0L) || (kind <= 19)) continue;
/* 3390 */             kind = 19; break;
/*      */           case 3:
/* 3393 */             if ((curChar != '\n') || (kind <= 19)) continue;
/* 3394 */             kind = 19; break;
/*      */           case 4:
/* 3397 */             if (curChar != '\r') continue;
/* 3398 */             jjstateSet[(jjnewStateCnt++)] = 3;
/*      */           }
/*      */         }
/*      */ 
/* 3402 */         while (i != startsAt);
/*      */       }
/* 3404 */       else if (curChar < '')
/*      */       {
/* 3406 */         long l = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/* 3409 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*      */           case 1:
/* 3413 */             kind = 17;
/* 3414 */             jjCheckNAdd(0);
/*      */           }
/*      */         }
/*      */ 
/* 3418 */         while (i != startsAt);
/*      */       }
/*      */       else
/*      */       {
/* 3422 */         int hiByte = curChar >> '\b';
/* 3423 */         int i1 = hiByte >> 6;
/* 3424 */         long l1 = 1L << (hiByte & 0x3F);
/* 3425 */         int i2 = (curChar & 0xFF) >> '\006';
/* 3426 */         long l2 = 1L << (curChar & 0x3F);
/*      */         do
/*      */         {
/* 3429 */           i--; switch (jjstateSet[i])
/*      */           {
/*      */           case 0:
/*      */           case 1:
/* 3433 */             if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */               continue;
/* 3435 */             if (kind > 17)
/* 3436 */               kind = 17;
/* 3437 */             jjCheckNAdd(0);
/*      */           }
/*      */         }
/*      */ 
/* 3441 */         while (i != startsAt);
/*      */       }
/* 3443 */       if (kind != 2147483647)
/*      */       {
/* 3445 */         jjmatchedKind = kind;
/* 3446 */         jjmatchedPos = curPos;
/* 3447 */         kind = 2147483647;
/*      */       }
/* 3449 */       curPos++;
/* 3450 */       if ((i = jjnewStateCnt) == (startsAt = 5 - (CPPParserTokenManager.jjnewStateCnt = startsAt)))
/* 3451 */         return curPos; try {
/* 3452 */         curChar = JavaCharStream.readChar(); } catch (IOException e) {
/*      */       }
/* 3453 */     }return curPos;
/*      */   }
/*      */ 
/*      */   private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
/*      */   {
/* 3471 */     switch (hiByte)
/*      */     {
/*      */     case 0:
/* 3474 */       return (jjbitVec2[i2] & l2) != 0L;
/*      */     }
/*      */ 
/* 3477 */     return (jjbitVec0[i1] & l1) != 0L;
/*      */   }
/*      */ 
/*      */   private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2)
/*      */   {
/* 3483 */     switch (hiByte)
/*      */     {
/*      */     case 0:
/* 3486 */       return (jjbitVec4[i2] & l2) != 0L;
/*      */     case 2:
/* 3488 */       return (jjbitVec5[i2] & l2) != 0L;
/*      */     case 3:
/* 3490 */       return (jjbitVec6[i2] & l2) != 0L;
/*      */     case 4:
/* 3492 */       return (jjbitVec7[i2] & l2) != 0L;
/*      */     case 5:
/* 3494 */       return (jjbitVec8[i2] & l2) != 0L;
/*      */     case 6:
/* 3496 */       return (jjbitVec9[i2] & l2) != 0L;
/*      */     case 7:
/* 3498 */       return (jjbitVec10[i2] & l2) != 0L;
/*      */     case 9:
/* 3500 */       return (jjbitVec11[i2] & l2) != 0L;
/*      */     case 10:
/* 3502 */       return (jjbitVec12[i2] & l2) != 0L;
/*      */     case 11:
/* 3504 */       return (jjbitVec13[i2] & l2) != 0L;
/*      */     case 12:
/* 3506 */       return (jjbitVec14[i2] & l2) != 0L;
/*      */     case 13:
/* 3508 */       return (jjbitVec15[i2] & l2) != 0L;
/*      */     case 14:
/* 3510 */       return (jjbitVec16[i2] & l2) != 0L;
/*      */     case 15:
/* 3512 */       return (jjbitVec17[i2] & l2) != 0L;
/*      */     case 16:
/* 3514 */       return (jjbitVec18[i2] & l2) != 0L;
/*      */     case 17:
/* 3516 */       return (jjbitVec19[i2] & l2) != 0L;
/*      */     case 18:
/* 3518 */       return (jjbitVec20[i2] & l2) != 0L;
/*      */     case 19:
/* 3520 */       return (jjbitVec21[i2] & l2) != 0L;
/*      */     case 20:
/* 3522 */       return (jjbitVec0[i2] & l2) != 0L;
/*      */     case 22:
/* 3524 */       return (jjbitVec22[i2] & l2) != 0L;
/*      */     case 23:
/* 3526 */       return (jjbitVec23[i2] & l2) != 0L;
/*      */     case 24:
/* 3528 */       return (jjbitVec24[i2] & l2) != 0L;
/*      */     case 25:
/* 3530 */       return (jjbitVec25[i2] & l2) != 0L;
/*      */     case 29:
/* 3532 */       return (jjbitVec26[i2] & l2) != 0L;
/*      */     case 30:
/* 3534 */       return (jjbitVec27[i2] & l2) != 0L;
/*      */     case 31:
/* 3536 */       return (jjbitVec28[i2] & l2) != 0L;
/*      */     case 32:
/* 3538 */       return (jjbitVec29[i2] & l2) != 0L;
/*      */     case 33:
/* 3540 */       return (jjbitVec30[i2] & l2) != 0L;
/*      */     case 48:
/* 3542 */       return (jjbitVec31[i2] & l2) != 0L;
/*      */     case 49:
/* 3544 */       return (jjbitVec32[i2] & l2) != 0L;
/*      */     case 77:
/* 3546 */       return (jjbitVec33[i2] & l2) != 0L;
/*      */     case 159:
/* 3548 */       return (jjbitVec34[i2] & l2) != 0L;
/*      */     case 164:
/* 3550 */       return (jjbitVec35[i2] & l2) != 0L;
/*      */     case 215:
/* 3552 */       return (jjbitVec36[i2] & l2) != 0L;
/*      */     case 216:
/* 3554 */       return (jjbitVec37[i2] & l2) != 0L;
/*      */     case 250:
/* 3556 */       return (jjbitVec38[i2] & l2) != 0L;
/*      */     case 251:
/* 3558 */       return (jjbitVec39[i2] & l2) != 0L;
/*      */     case 253:
/* 3560 */       return (jjbitVec40[i2] & l2) != 0L;
/*      */     case 254:
/* 3562 */       return (jjbitVec41[i2] & l2) != 0L;
/*      */     case 255:
/* 3564 */       return (jjbitVec42[i2] & l2) != 0L;
/*      */     }
/*      */ 
/* 3567 */     return (jjbitVec3[i1] & l1) != 0L;
/*      */   }
/*      */ 
/*      */   private static final boolean jjCanMove_2(int hiByte, int i1, int i2, long l1, long l2)
/*      */   {
/* 3573 */     switch (hiByte)
/*      */     {
/*      */     case 0:
/* 3576 */       return (jjbitVec43[i2] & l2) != 0L;
/*      */     case 2:
/* 3578 */       return (jjbitVec5[i2] & l2) != 0L;
/*      */     case 3:
/* 3580 */       return (jjbitVec44[i2] & l2) != 0L;
/*      */     case 4:
/* 3582 */       return (jjbitVec45[i2] & l2) != 0L;
/*      */     case 5:
/* 3584 */       return (jjbitVec46[i2] & l2) != 0L;
/*      */     case 6:
/* 3586 */       return (jjbitVec47[i2] & l2) != 0L;
/*      */     case 7:
/* 3588 */       return (jjbitVec48[i2] & l2) != 0L;
/*      */     case 9:
/* 3590 */       return (jjbitVec49[i2] & l2) != 0L;
/*      */     case 10:
/* 3592 */       return (jjbitVec50[i2] & l2) != 0L;
/*      */     case 11:
/* 3594 */       return (jjbitVec51[i2] & l2) != 0L;
/*      */     case 12:
/* 3596 */       return (jjbitVec52[i2] & l2) != 0L;
/*      */     case 13:
/* 3598 */       return (jjbitVec53[i2] & l2) != 0L;
/*      */     case 14:
/* 3600 */       return (jjbitVec54[i2] & l2) != 0L;
/*      */     case 15:
/* 3602 */       return (jjbitVec55[i2] & l2) != 0L;
/*      */     case 16:
/* 3604 */       return (jjbitVec56[i2] & l2) != 0L;
/*      */     case 17:
/* 3606 */       return (jjbitVec19[i2] & l2) != 0L;
/*      */     case 18:
/* 3608 */       return (jjbitVec20[i2] & l2) != 0L;
/*      */     case 19:
/* 3610 */       return (jjbitVec57[i2] & l2) != 0L;
/*      */     case 20:
/* 3612 */       return (jjbitVec0[i2] & l2) != 0L;
/*      */     case 22:
/* 3614 */       return (jjbitVec22[i2] & l2) != 0L;
/*      */     case 23:
/* 3616 */       return (jjbitVec58[i2] & l2) != 0L;
/*      */     case 24:
/* 3618 */       return (jjbitVec59[i2] & l2) != 0L;
/*      */     case 25:
/* 3620 */       return (jjbitVec60[i2] & l2) != 0L;
/*      */     case 29:
/* 3622 */       return (jjbitVec26[i2] & l2) != 0L;
/*      */     case 30:
/* 3624 */       return (jjbitVec27[i2] & l2) != 0L;
/*      */     case 31:
/* 3626 */       return (jjbitVec28[i2] & l2) != 0L;
/*      */     case 32:
/* 3628 */       return (jjbitVec61[i2] & l2) != 0L;
/*      */     case 33:
/* 3630 */       return (jjbitVec30[i2] & l2) != 0L;
/*      */     case 48:
/* 3632 */       return (jjbitVec62[i2] & l2) != 0L;
/*      */     case 49:
/* 3634 */       return (jjbitVec32[i2] & l2) != 0L;
/*      */     case 77:
/* 3636 */       return (jjbitVec33[i2] & l2) != 0L;
/*      */     case 159:
/* 3638 */       return (jjbitVec34[i2] & l2) != 0L;
/*      */     case 164:
/* 3640 */       return (jjbitVec35[i2] & l2) != 0L;
/*      */     case 215:
/* 3642 */       return (jjbitVec36[i2] & l2) != 0L;
/*      */     case 216:
/* 3644 */       return (jjbitVec63[i2] & l2) != 0L;
/*      */     case 220:
/* 3646 */       return (jjbitVec64[i2] & l2) != 0L;
/*      */     case 221:
/* 3648 */       return (jjbitVec65[i2] & l2) != 0L;
/*      */     case 250:
/* 3650 */       return (jjbitVec38[i2] & l2) != 0L;
/*      */     case 251:
/* 3652 */       return (jjbitVec66[i2] & l2) != 0L;
/*      */     case 253:
/* 3654 */       return (jjbitVec40[i2] & l2) != 0L;
/*      */     case 254:
/* 3656 */       return (jjbitVec67[i2] & l2) != 0L;
/*      */     case 255:
/* 3658 */       return (jjbitVec68[i2] & l2) != 0L;
/*      */     }
/*      */ 
/* 3661 */     return (jjbitVec3[i1] & l1) != 0L;
/*      */   }
/*      */ 
/*      */   public CPPParserTokenManager(JavaCharStream stream)
/*      */   {
/* 3743 */     if (input_stream != null)
/* 3744 */       throw new TokenMgrError("ERROR: Second call to constructor of static lexer. You must use ReInit() to initialize the static variables.", 1);
/* 3745 */     input_stream = stream;
/*      */   }
/*      */ 
/*      */   public CPPParserTokenManager(JavaCharStream stream, int lexState)
/*      */   {
/* 3750 */     this(stream);
/* 3751 */     SwitchTo(lexState);
/*      */   }
/*      */ 
/*      */   public static void ReInit(JavaCharStream stream)
/*      */   {
/* 3757 */     jjmatchedPos = CPPParserTokenManager.jjnewStateCnt = 0;
/* 3758 */     curLexState = defaultLexState;
/* 3759 */     input_stream = stream;
/* 3760 */     ReInitRounds();
/*      */   }
/*      */ 
/*      */   private static void ReInitRounds()
/*      */   {
/* 3765 */     jjround = -2147483647;
/* 3766 */     for (int i = 210; i-- > 0; )
/* 3767 */       jjrounds[i] = -2147483648;
/*      */   }
/*      */ 
/*      */   public static void ReInit(JavaCharStream stream, int lexState)
/*      */   {
/* 3773 */     ReInit(stream);
/* 3774 */     SwitchTo(lexState);
/*      */   }
/*      */ 
/*      */   public static void SwitchTo(int lexState)
/*      */   {
/* 3780 */     if ((lexState >= 11) || (lexState < 0)) {
/* 3781 */       throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
/*      */     }
/* 3783 */     curLexState = lexState;
/*      */   }
/*      */ 
/*      */   protected static Token jjFillToken()
/*      */   {
/* 3794 */     String im = jjstrLiteralImages[jjmatchedKind];
/* 3795 */     String curTokenImage = im == null ? JavaCharStream.GetImage() : im;
/* 3796 */     int beginLine = JavaCharStream.getBeginLine();
/* 3797 */     int beginColumn = JavaCharStream.getBeginColumn();
/* 3798 */     int endLine = JavaCharStream.getEndLine();
/* 3799 */     int endColumn = JavaCharStream.getEndColumn();
/* 3800 */     Token t = Token.newToken(jjmatchedKind, curTokenImage);
/*      */ 
/* 3802 */     t.beginLine = beginLine;
/* 3803 */     t.endLine = endLine;
/* 3804 */     t.beginColumn = beginColumn;
/* 3805 */     t.endColumn = endColumn;
/*      */ 
/* 3807 */     return t;
/*      */   }
/*      */ 
/*      */   public static Token getNextToken()
/*      */   {
/* 3821 */     int curPos = 0;
/*      */     try
/*      */     {
/* 3828 */       curChar = JavaCharStream.BeginToken();
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/* 3832 */       jjmatchedKind = 0;
/* 3833 */       Token matchedToken = jjFillToken();
/* 3834 */       return matchedToken;
/*      */     }
/* 3836 */     image = jjimage;
/* 3837 */     image.setLength(0);
/* 3838 */     jjimageLen = 0;
/*      */     while (true)
/*      */     {
/* 3842 */       switch (curLexState) {
/*      */       case 0:
/*      */         try {
/* 3845 */           JavaCharStream.backup(0);
/* 3846 */           while ((curChar <= ' ') && ((0x2600 & 1L << curChar) != 0L))
/* 3847 */             curChar = JavaCharStream.BeginToken(); 
/*      */         } catch (IOException e1) {
/*      */         }
/* 3849 */         break;
/* 3850 */         jjmatchedKind = 2147483647;
/* 3851 */         jjmatchedPos = 0;
/* 3852 */         curPos = jjMoveStringLiteralDfa0_0();
/* 3853 */         break;
/*      */       case 1:
/* 3855 */         jjmatchedKind = 2147483647;
/* 3856 */         jjmatchedPos = 0;
/* 3857 */         curPos = jjMoveStringLiteralDfa0_1();
/* 3858 */         break;
/*      */       case 2:
/* 3860 */         jjmatchedKind = 2147483647;
/* 3861 */         jjmatchedPos = 0;
/* 3862 */         curPos = jjMoveStringLiteralDfa0_2();
/* 3863 */         break;
/*      */       case 3:
/* 3865 */         jjmatchedKind = 2147483647;
/* 3866 */         jjmatchedPos = 0;
/* 3867 */         curPos = jjMoveStringLiteralDfa0_3();
/* 3868 */         break;
/*      */       case 4:
/* 3870 */         jjmatchedKind = 2147483647;
/* 3871 */         jjmatchedPos = 0;
/* 3872 */         curPos = jjMoveStringLiteralDfa0_4();
/* 3873 */         break;
/*      */       case 5:
/* 3875 */         jjmatchedKind = 2147483647;
/* 3876 */         jjmatchedPos = 0;
/* 3877 */         curPos = jjMoveStringLiteralDfa0_5();
/* 3878 */         break;
/*      */       case 6:
/* 3880 */         jjmatchedKind = 2147483647;
/* 3881 */         jjmatchedPos = 0;
/* 3882 */         curPos = jjMoveStringLiteralDfa0_6();
/* 3883 */         if ((jjmatchedPos == 0) && (jjmatchedKind > 28))
/*      */         {
/* 3885 */           jjmatchedKind = 28; } break;
/*      */       case 7:
/* 3889 */         jjmatchedKind = 2147483647;
/* 3890 */         jjmatchedPos = 0;
/* 3891 */         curPos = jjMoveStringLiteralDfa0_7();
/* 3892 */         if ((jjmatchedPos == 0) && (jjmatchedKind > 29))
/*      */         {
/* 3894 */           jjmatchedKind = 29; } break;
/*      */       case 8:
/* 3898 */         jjmatchedKind = 2147483647;
/* 3899 */         jjmatchedPos = 0;
/* 3900 */         curPos = jjMoveStringLiteralDfa0_8();
/* 3901 */         if ((jjmatchedPos == 0) && (jjmatchedKind > 31))
/*      */         {
/* 3903 */           jjmatchedKind = 31; } break;
/*      */       case 9:
/* 3907 */         jjmatchedKind = 2147483647;
/* 3908 */         jjmatchedPos = 0;
/* 3909 */         curPos = jjMoveStringLiteralDfa0_9();
/* 3910 */         if ((jjmatchedPos == 0) && (jjmatchedKind > 33))
/*      */         {
/* 3912 */           jjmatchedKind = 33; } break;
/*      */       case 10:
/* 3916 */         jjmatchedKind = 2147483647;
/* 3917 */         jjmatchedPos = 0;
/* 3918 */         curPos = jjMoveStringLiteralDfa0_10();
/* 3919 */         if ((jjmatchedPos == 0) && (jjmatchedKind > 35))
/*      */         {
/* 3921 */           jjmatchedKind = 35;
/*      */         }
/*      */ 
/*      */       default:
/* 3925 */         if (jjmatchedKind != 2147483647)
/*      */         {
/* 3927 */           if (jjmatchedPos + 1 < curPos)
/* 3928 */             JavaCharStream.backup(curPos - jjmatchedPos - 1);
/* 3929 */           if ((jjtoToken[(jjmatchedKind >> 6)] & 1L << (jjmatchedKind & 0x3F)) != 0L)
/*      */           {
/* 3931 */             Token matchedToken = jjFillToken();
/* 3932 */             if (jjnewLexState[jjmatchedKind] != -1)
/* 3933 */               curLexState = jjnewLexState[jjmatchedKind];
/* 3934 */             return matchedToken;
/*      */           }
/* 3936 */           if ((jjtoSkip[(jjmatchedKind >> 6)] & 1L << (jjmatchedKind & 0x3F)) != 0L)
/*      */           {
/* 3938 */             SkipLexicalActions(null);
/* 3939 */             if (jjnewLexState[jjmatchedKind] == -1) break;
/* 3940 */             curLexState = jjnewLexState[jjmatchedKind]; break;
/*      */           }
/*      */ 
/* 3943 */           jjimageLen += jjmatchedPos + 1;
/* 3944 */           if (jjnewLexState[jjmatchedKind] != -1)
/* 3945 */             curLexState = jjnewLexState[jjmatchedKind];
/* 3946 */           curPos = 0;
/* 3947 */           jjmatchedKind = 2147483647;
/*      */           try {
/* 3949 */             curChar = JavaCharStream.readChar(); } catch (IOException e1) {
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 3954 */     int error_line = JavaCharStream.getEndLine();
/* 3955 */     int error_column = JavaCharStream.getEndColumn();
/* 3956 */     String error_after = null;
/* 3957 */     boolean EOFSeen = false;
/*      */     try { JavaCharStream.readChar(); JavaCharStream.backup(1);
/*      */     } catch (IOException e1) {
/* 3960 */       EOFSeen = true;
/* 3961 */       error_after = curPos <= 1 ? "" : JavaCharStream.GetImage();
/* 3962 */       if ((curChar == '\n') || (curChar == '\r')) {
/* 3963 */         error_line++;
/* 3964 */         error_column = 0;
/*      */       }
/*      */       else {
/* 3967 */         error_column++;
/*      */       }
/*      */     }
/* 3969 */     if (!EOFSeen) {
/* 3970 */       JavaCharStream.backup(1);
/* 3971 */       error_after = curPos <= 1 ? "" : JavaCharStream.GetImage();
/*      */     }
/* 3973 */     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, 0);
/*      */   }
/*      */ 
/*      */   static void SkipLexicalActions(Token matchedToken)
/*      */   {
/* 3980 */     switch (jjmatchedKind)
/*      */     {
/*      */     case 8:
/* 3983 */       image.append(JavaCharStream.GetSuffix(jjimageLen + (CPPParserTokenManager.lengthOfMatch = jjmatchedPos + 1)));
/* 3984 */       JavaCharStream.backup(1);
/* 3985 */       break;
/*      */     case 14:
/* 3987 */       image.append(JavaCharStream.GetSuffix(jjimageLen + (CPPParserTokenManager.lengthOfMatch = jjmatchedPos + 1)));
/*      */ 
/* 3989 */       String defineValue = image.toString();
/* 3990 */       StringBuilder defineStr = new StringBuilder();
/* 3991 */       defineStr.append(JavaCharStream.GetSuffix(JavaCharStream.getEndColumn()));
/* 3992 */       CPPParser.fgCallback.defineDecl(defineValue, defineStr.toString(), JavaCharStream.getBeginLine(), JavaCharStream.getBeginColumn());
/* 3993 */       break;
/*      */     case 17:
/* 3995 */       image.append(JavaCharStream.GetSuffix(jjimageLen + (CPPParserTokenManager.lengthOfMatch = jjmatchedPos + 1)));
/* 3996 */       String propertyName = image.toString();
/*      */ 
/* 3998 */       StringBuilder propertyStr = new StringBuilder();
/* 3999 */       propertyStr.append(JavaCharStream.GetSuffix(JavaCharStream.getEndColumn()));
/* 4000 */       CPPParser.fgCallback.propertyDecl(propertyName, propertyStr.toString(), JavaCharStream.getBeginLine(), JavaCharStream.getBeginColumn());
/* 4001 */       break;
/*      */     case 20:
/* 4003 */       image.append(JavaCharStream.GetSuffix(jjimageLen + (CPPParserTokenManager.lengthOfMatch = jjmatchedPos + 1)));
/* 4004 */       String includeName = image.toString();
/* 4005 */       CPPParser.fgCallback.includeDecl(includeName, JavaCharStream.getBeginLine(), JavaCharStream.getBeginColumn());
/* 4006 */       break;
/*      */     case 23:
/* 4008 */       image.append(JavaCharStream.GetSuffix(jjimageLen + (CPPParserTokenManager.lengthOfMatch = jjmatchedPos + 1)));
/* 4009 */       String endimportName = image.toString().trim();
/* 4010 */       StringBuilder endimportStr = new StringBuilder();
/* 4011 */       endimportStr.append(JavaCharStream.GetSuffix(JavaCharStream.getEndColumn()));
/* 4012 */       CPPParser.fgCallback.importDecl(endimportName, endimportStr, JavaCharStream.getBeginLine(), JavaCharStream.getBeginColumn());
/* 4013 */       break;
/*      */     case 26:
/* 4015 */       image.append(JavaCharStream.GetSuffix(jjimageLen + (CPPParserTokenManager.lengthOfMatch = jjmatchedPos + 1)));
/*      */       try
/*      */       {
/* 4018 */         beginLine = Integer.parseInt(image.toString());
/*      */       }
/*      */       catch (NumberFormatException e) {
/*      */       }
/*      */     case 29:
/* 4023 */       image.append(JavaCharStream.GetSuffix(jjimageLen + (CPPParserTokenManager.lengthOfMatch = jjmatchedPos + 1)));
/* 4024 */       JavaCharStream.adjustBeginLineColumn(beginLine, 1);
/* 4025 */       JavaCharStream.backup(1);
/* 4026 */       break;
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 15:
/*      */     case 16:
/*      */     case 18:
/*      */     case 19:
/*      */     case 21:
/*      */     case 22:
/*      */     case 24:
/*      */     case 25:
/*      */     case 27:
/*      */     case 28: }  } 
/* 4033 */   private static void jjCheckNAdd(int state) { if (jjrounds[state] != jjround)
/*      */     {
/* 4035 */       jjstateSet[(jjnewStateCnt++)] = state;
/* 4036 */       jjrounds[state] = jjround;
/*      */     } }
/*      */ 
/*      */   private static void jjAddStates(int start, int end)
/*      */   {
/*      */     do
/* 4042 */       jjstateSet[(jjnewStateCnt++)] = jjnextStates[start];
/* 4043 */     while (start++ != end);
/*      */   }
/*      */ 
/*      */   private static void jjCheckNAddTwoStates(int state1, int state2) {
/* 4047 */     jjCheckNAdd(state1);
/* 4048 */     jjCheckNAdd(state2);
/*      */   }
/*      */ 
/*      */   private static void jjCheckNAddStates(int start, int end)
/*      */   {
/*      */     do
/* 4054 */       jjCheckNAdd(jjnextStates[start]);
/* 4055 */     while (start++ != end);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.CPPParserTokenManager
 * JD-Core Version:    0.6.0
 */