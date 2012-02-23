/*     */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*     */ 
/*     */ public abstract interface CPPParserConstants
/*     */ {
/*     */   public static final int EOF = 0;
/*     */   public static final int LCURLYBRACE = 36;
/*     */   public static final int RCURLYBRACE = 37;
/*     */   public static final int LSQUAREBRACKET = 38;
/*     */   public static final int RSQUAREBRACKET = 39;
/*     */   public static final int LPARENTHESIS = 40;
/*     */   public static final int RPARENTHESIS = 41;
/*     */   public static final int SCOPE = 42;
/*     */   public static final int COLON = 43;
/*     */   public static final int SEMICOLON = 44;
/*     */   public static final int COMMA = 45;
/*     */   public static final int QUESTIONMARK = 46;
/*     */   public static final int ELLIPSIS = 47;
/*     */   public static final int ASSIGNEQUAL = 48;
/*     */   public static final int TIMESEQUAL = 49;
/*     */   public static final int DIVIDEEQUAL = 50;
/*     */   public static final int MODEQUAL = 51;
/*     */   public static final int PLUSEQUAL = 52;
/*     */   public static final int MINUSEQUAL = 53;
/*     */   public static final int SHIFTLEFTEQUAL = 54;
/*     */   public static final int SHIFTRIGHTEQUAL = 55;
/*     */   public static final int BITWISEANDEQUAL = 56;
/*     */   public static final int BITWISEXOREQUAL = 57;
/*     */   public static final int BITWISEOREQUAL = 58;
/*     */   public static final int OR = 59;
/*     */   public static final int AND = 60;
/*     */   public static final int BITWISEOR = 61;
/*     */   public static final int BITWISEXOR = 62;
/*     */   public static final int AMPERSAND = 63;
/*     */   public static final int EQUAL = 64;
/*     */   public static final int NOTEQUAL = 65;
/*     */   public static final int LESSTHAN = 66;
/*     */   public static final int GREATERTHAN = 67;
/*     */   public static final int LESSTHANOREQUALTO = 68;
/*     */   public static final int GREATERTHANOREQUALTO = 69;
/*     */   public static final int SHIFTLEFT = 70;
/*     */   public static final int SHIFTRIGHT = 71;
/*     */   public static final int PLUS = 72;
/*     */   public static final int MINUS = 73;
/*     */   public static final int STAR = 74;
/*     */   public static final int DIVIDE = 75;
/*     */   public static final int MOD = 76;
/*     */   public static final int PLUSPLUS = 77;
/*     */   public static final int MINUSMINUS = 78;
/*     */   public static final int TILDE = 79;
/*     */   public static final int NOT = 80;
/*     */   public static final int DOT = 81;
/*     */   public static final int POINTERTO = 82;
/*     */   public static final int DOTSTAR = 83;
/*     */   public static final int ARROWSTAR = 84;
/*     */   public static final int AUTO = 85;
/*     */   public static final int BREAK = 86;
/*     */   public static final int BOOL = 87;
/*     */   public static final int BOOLEAN = 88;
/*     */   public static final int CASE = 89;
/*     */   public static final int CATCH = 90;
/*     */   public static final int CHAR = 91;
/*     */   public static final int CONST = 92;
/*     */   public static final int CONTINUE = 93;
/*     */   public static final int _DEFAULT = 94;
/*     */   public static final int DELETE = 95;
/*     */   public static final int DO = 96;
/*     */   public static final int DOUBLE = 97;
/*     */   public static final int ELSE = 98;
/*     */   public static final int ENUM = 99;
/*     */   public static final int EXTERN = 100;
/*     */   public static final int FINALLY = 101;
/*     */   public static final int FLOAT = 102;
/*     */   public static final int FOR = 103;
/*     */   public static final int FRIEND = 104;
/*     */   public static final int GOTO = 105;
/*     */   public static final int IF = 106;
/*     */   public static final int INLINE = 107;
/*     */   public static final int INT = 108;
/*     */   public static final int LONG = 109;
/*     */   public static final int NEW = 110;
/*     */   public static final int PRIVATE = 111;
/*     */   public static final int PROTECTED = 112;
/*     */   public static final int PUBLIC = 113;
/*     */   public static final int REDECLARED = 114;
/*     */   public static final int REGISTER = 115;
/*     */   public static final int RETURN = 116;
/*     */   public static final int SHORT = 117;
/*     */   public static final int SIGNED = 118;
/*     */   public static final int SIZEOF = 119;
/*     */   public static final int STATIC = 120;
/*     */   public static final int STRINGTYPE = 121;
/*     */   public static final int STRUCT = 122;
/*     */   public static final int CLASS = 123;
/*     */   public static final int SWITCH = 124;
/*     */   public static final int TEMPLATE = 125;
/*     */   public static final int THIS = 126;
/*     */   public static final int TRY = 127;
/*     */   public static final int TYPEDEF = 128;
/*     */   public static final int UNION = 129;
/*     */   public static final int UNSIGNED = 130;
/*     */   public static final int VIRTUAL = 131;
/*     */   public static final int VOID = 132;
/*     */   public static final int VOLATILE = 133;
/*     */   public static final int WHILE = 134;
/*     */   public static final int OPERATOR = 135;
/*     */   public static final int TRUETOK = 136;
/*     */   public static final int FALSETOK = 137;
/*     */   public static final int THROW = 138;
/*     */   public static final int COLOR = 139;
/*     */   public static final int DATETIME = 140;
/*     */   public static final int STATIC_CAST = 141;
/*     */   public static final int DYNAMIC_CAST = 142;
/*     */   public static final int CONST_CAST = 143;
/*     */   public static final int REINTERPRET_CAST = 144;
/*     */   public static final int OCTALINT = 145;
/*     */   public static final int OCTALLONG = 146;
/*     */   public static final int UNSIGNED_OCTALINT = 147;
/*     */   public static final int UNSIGNED_OCTALLONG = 148;
/*     */   public static final int ZERODECIMALINT = 149;
/*     */   public static final int DECIMALINT = 150;
/*     */   public static final int DECIMALLONG = 151;
/*     */   public static final int UNSIGNED_DECIMALINT = 152;
/*     */   public static final int UNSIGNED_DECIMALLONG = 153;
/*     */   public static final int HEXADECIMALINT = 154;
/*     */   public static final int HEXADECIMALLONG = 155;
/*     */   public static final int UNSIGNED_HEXADECIMALINT = 156;
/*     */   public static final int UNSIGNED_HEXADECIMALLONG = 157;
/*     */   public static final int FLOATONE = 158;
/*     */   public static final int FLOATTWO = 159;
/*     */   public static final int DATETIME_INITIALIZER = 160;
/*     */   public static final int COLOR_INITIALIZER = 161;
/*     */   public static final int CHARACTER = 162;
/*     */   public static final int STRING = 163;
/*     */   public static final int ID = 164;
/*     */   public static final int DOT_ID = 165;
/*     */   public static final int LONG_LITERAL = 166;
/*     */   public static final int INTEGER_LITERAL = 167;
/*     */   public static final int DECIMAL_LITERAL = 168;
/*     */   public static final int HEX_LITERAL = 169;
/*     */   public static final int OCTAL_LITERAL = 170;
/*     */   public static final int FLOATING_POINT_LITERAL = 171;
/*     */   public static final int DECIMAL_FLOATING_POINT_LITERAL = 172;
/*     */   public static final int DECIMAL_EXPONENT = 173;
/*     */   public static final int HEXADECIMAL_FLOATING_POINT_LITERAL = 174;
/*     */   public static final int HEXADECIMAL_EXPONENT = 175;
/*     */   public static final int CHARACTER_LITERAL = 176;
/*     */   public static final int STRING_LITERAL = 177;
/*     */   public static final int IDENTIFIER = 178;
/*     */   public static final int LETTER = 179;
/*     */   public static final int PART_LETTER = 180;
/*     */   public static final int DEFAULT = 0;
/*     */   public static final int DEFINE_STMT = 1;
/*     */   public static final int PROPERTY_STMT = 2;
/*     */   public static final int INCLUDE_STMT = 3;
/*     */   public static final int IMPORT_STMT = 4;
/*     */   public static final int LINE_NUMBER = 5;
/*     */   public static final int LINE_DIRECTIVE = 6;
/*     */   public static final int AFTER_LINE_DIRECTIVE = 7;
/*     */   public static final int IN_LINE_COMMENT = 8;
/*     */   public static final int IN_COMMENT = 9;
/*     */   public static final int PREPROCESSOR_OUTPUT = 10;
/* 328 */   public static final String[] tokenImage = { "<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "\"//\"", "\"/*\"", "<token of kind 7>", "<token of kind 8>", "<token of kind 9>", "<token of kind 10>", "<token of kind 11>", "<token of kind 12>", "\"#\"", "<token of kind 14>", "<token of kind 15>", "<token of kind 16>", "<token of kind 17>", "<token of kind 18>", "<token of kind 19>", "<token of kind 20>", "<token of kind 21>", "<token of kind 22>", "<token of kind 23>", "<token of kind 24>", "<token of kind 25>", "<token of kind 26>", "<token of kind 27>", "<token of kind 28>", "<token of kind 29>", "<token of kind 30>", "<token of kind 31>", "\"*/\"", "<token of kind 33>", "<token of kind 34>", "<token of kind 35>", "\"{\"", "\"}\"", "\"[\"", "\"]\"", "\"(\"", "\")\"", "\"::\"", "\":\"", "\";\"", "\",\"", "\"?\"", "\"...\"", "\"=\"", "\"*=\"", "\"/=\"", "\"%=\"", "\"+=\"", "\"-=\"", "\"<<=\"", "\">>=\"", "\"&=\"", "\"^=\"", "\"|=\"", "\"||\"", "\"&&\"", "\"|\"", "\"^\"", "\"&\"", "\"==\"", "\"!=\"", "\"<\"", "\">\"", "\"<=\"", "\">=\"", "\"<<\"", "\">>\"", "\"+\"", "\"-\"", "\"*\"", "\"/\"", "\"%\"", "\"++\"", "\"--\"", "\"~\"", "\"!\"", "\".\"", "\"->\"", "\".*\"", "\"->*\"", "\"auto\"", "\"break\"", "\"bool\"", "\"boolean\"", "\"case\"", "\"catch\"", "\"char\"", "\"const\"", "\"continue\"", "\"default\"", "\"delete\"", "\"do\"", "\"double\"", "\"else\"", "\"enum\"", "\"extern\"", "\"finally\"", "\"float\"", "\"for\"", "\"friend\"", "\"goto\"", "\"if\"", "\"inline\"", "\"int\"", "\"long\"", "\"new\"", "\"private\"", "\"protected\"", "\"public\"", "\"redeclared\"", "\"register\"", "\"return\"", "\"short\"", "\"signed\"", "\"sizeof\"", "\"static\"", "\"string\"", "\"struct\"", "\"class\"", "\"switch\"", "\"template\"", "\"this\"", "\"try\"", "\"typedef\"", "\"union\"", "\"unsigned\"", "\"virtual\"", "\"void\"", "\"volatile\"", "\"while\"", "\"operator\"", "\"true\"", "\"false\"", "\"throw\"", "\"color\"", "\"datetime\"", "\"static_cast\"", "\"dynamic_cast\"", "\"const_cast\"", "\"reinterpret_cast\"", "<OCTALINT>", "<OCTALLONG>", "<UNSIGNED_OCTALINT>", "<UNSIGNED_OCTALLONG>", "<ZERODECIMALINT>", "<DECIMALINT>", "<DECIMALLONG>", "<UNSIGNED_DECIMALINT>", "<UNSIGNED_DECIMALLONG>", "<HEXADECIMALINT>", "<HEXADECIMALLONG>", "<UNSIGNED_HEXADECIMALINT>", "<UNSIGNED_HEXADECIMALLONG>", "<FLOATONE>", "<FLOATTWO>", "<DATETIME_INITIALIZER>", "<COLOR_INITIALIZER>", "<CHARACTER>", "<STRING>", "<ID>", "<DOT_ID>", "<LONG_LITERAL>", "<INTEGER_LITERAL>", "<DECIMAL_LITERAL>", "<HEX_LITERAL>", "<OCTAL_LITERAL>", "<FLOATING_POINT_LITERAL>", "<DECIMAL_FLOATING_POINT_LITERAL>", "<DECIMAL_EXPONENT>", "<HEXADECIMAL_FLOATING_POINT_LITERAL>", "<HEXADECIMAL_EXPONENT>", "<CHARACTER_LITERAL>", "<STRING_LITERAL>", "<IDENTIFIER>", "<LETTER>", "<PART_LETTER>" };
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.CPPParserConstants
 * JD-Core Version:    0.6.0
 */