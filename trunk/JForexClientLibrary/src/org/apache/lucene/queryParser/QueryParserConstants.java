/*    */ package org.apache.lucene.queryParser;
/*    */ 
/*    */ public abstract interface QueryParserConstants
/*    */ {
/*    */   public static final int EOF = 0;
/*    */   public static final int _NUM_CHAR = 1;
/*    */   public static final int _ESCAPED_CHAR = 2;
/*    */   public static final int _TERM_START_CHAR = 3;
/*    */   public static final int _TERM_CHAR = 4;
/*    */   public static final int _WHITESPACE = 5;
/*    */   public static final int _QUOTED_CHAR = 6;
/*    */   public static final int AND = 8;
/*    */   public static final int OR = 9;
/*    */   public static final int NOT = 10;
/*    */   public static final int PLUS = 11;
/*    */   public static final int MINUS = 12;
/*    */   public static final int LPAREN = 13;
/*    */   public static final int RPAREN = 14;
/*    */   public static final int COLON = 15;
/*    */   public static final int STAR = 16;
/*    */   public static final int CARAT = 17;
/*    */   public static final int QUOTED = 18;
/*    */   public static final int TERM = 19;
/*    */   public static final int FUZZY_SLOP = 20;
/*    */   public static final int PREFIXTERM = 21;
/*    */   public static final int WILDTERM = 22;
/*    */   public static final int RANGEIN_START = 23;
/*    */   public static final int RANGEEX_START = 24;
/*    */   public static final int NUMBER = 25;
/*    */   public static final int RANGEIN_TO = 26;
/*    */   public static final int RANGEIN_END = 27;
/*    */   public static final int RANGEIN_QUOTED = 28;
/*    */   public static final int RANGEIN_GOOP = 29;
/*    */   public static final int RANGEEX_TO = 30;
/*    */   public static final int RANGEEX_END = 31;
/*    */   public static final int RANGEEX_QUOTED = 32;
/*    */   public static final int RANGEEX_GOOP = 33;
/*    */   public static final int Boost = 0;
/*    */   public static final int RangeEx = 1;
/*    */   public static final int RangeIn = 2;
/*    */   public static final int DEFAULT = 3;
/* 88 */   public static final String[] tokenImage = { "<EOF>", "<_NUM_CHAR>", "<_ESCAPED_CHAR>", "<_TERM_START_CHAR>", "<_TERM_CHAR>", "<_WHITESPACE>", "<_QUOTED_CHAR>", "<token of kind 7>", "<AND>", "<OR>", "<NOT>", "\"+\"", "\"-\"", "\"(\"", "\")\"", "\":\"", "\"*\"", "\"^\"", "<QUOTED>", "<TERM>", "<FUZZY_SLOP>", "<PREFIXTERM>", "<WILDTERM>", "\"[\"", "\"{\"", "<NUMBER>", "\"TO\"", "\"]\"", "<RANGEIN_QUOTED>", "<RANGEIN_GOOP>", "\"TO\"", "\"}\"", "<RANGEEX_QUOTED>", "<RANGEEX_GOOP>" };
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.queryParser.QueryParserConstants
 * JD-Core Version:    0.6.0
 */