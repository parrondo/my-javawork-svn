/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ 
/*     */ public abstract interface JavadocTagConstants
/*     */ {
/*  24 */   public static final char[] TAG_DEPRECATED = "deprecated".toCharArray();
/*  25 */   public static final char[] TAG_PARAM = "param".toCharArray();
/*  26 */   public static final char[] TAG_RETURN = "return".toCharArray();
/*  27 */   public static final char[] TAG_THROWS = "throws".toCharArray();
/*  28 */   public static final char[] TAG_EXCEPTION = "exception".toCharArray();
/*  29 */   public static final char[] TAG_SEE = "see".toCharArray();
/*  30 */   public static final char[] TAG_LINK = "link".toCharArray();
/*  31 */   public static final char[] TAG_LINKPLAIN = "linkplain".toCharArray();
/*  32 */   public static final char[] TAG_INHERITDOC = "inheritDoc".toCharArray();
/*  33 */   public static final char[] TAG_VALUE = "value".toCharArray();
/*  34 */   public static final char[] TAG_AUTHOR = "author".toCharArray();
/*  35 */   public static final char[] TAG_CODE = "code".toCharArray();
/*  36 */   public static final char[] TAG_DOC_ROOT = "docRoot".toCharArray();
/*  37 */   public static final char[] TAG_LITERAL = "literal".toCharArray();
/*  38 */   public static final char[] TAG_SERIAL = "serial".toCharArray();
/*  39 */   public static final char[] TAG_SERIAL_DATA = "serialData".toCharArray();
/*  40 */   public static final char[] TAG_SERIAL_FIELD = "serialField".toCharArray();
/*  41 */   public static final char[] TAG_SINCE = "since".toCharArray();
/*  42 */   public static final char[] TAG_VERSION = "version".toCharArray();
/*  43 */   public static final char[] TAG_CATEGORY = "category".toCharArray();
/*     */ 
/*  46 */   public static final int TAG_DEPRECATED_LENGTH = TAG_DEPRECATED.length;
/*  47 */   public static final int TAG_PARAM_LENGTH = TAG_PARAM.length;
/*  48 */   public static final int TAG_RETURN_LENGTH = TAG_RETURN.length;
/*  49 */   public static final int TAG_THROWS_LENGTH = TAG_THROWS.length;
/*  50 */   public static final int TAG_EXCEPTION_LENGTH = TAG_EXCEPTION.length;
/*  51 */   public static final int TAG_SEE_LENGTH = TAG_SEE.length;
/*  52 */   public static final int TAG_LINK_LENGTH = TAG_LINK.length;
/*  53 */   public static final int TAG_LINKPLAIN_LENGTH = TAG_LINKPLAIN.length;
/*  54 */   public static final int TAG_INHERITDOC_LENGTH = TAG_INHERITDOC.length;
/*  55 */   public static final int TAG_VALUE_LENGTH = TAG_VALUE.length;
/*  56 */   public static final int TAG_CATEGORY_LENGTH = TAG_CATEGORY.length;
/*  57 */   public static final int TAG_AUTHOR_LENGTH = TAG_AUTHOR.length;
/*  58 */   public static final int TAG_SERIAL_LENGTH = TAG_SERIAL.length;
/*  59 */   public static final int TAG_SERIAL_DATA_LENGTH = TAG_SERIAL_DATA.length;
/*  60 */   public static final int TAG_SERIAL_FIELD_LENGTH = TAG_SERIAL_FIELD.length;
/*  61 */   public static final int TAG_SINCE_LENGTH = TAG_SINCE.length;
/*  62 */   public static final int TAG_VERSION_LENGTH = TAG_VERSION.length;
/*  63 */   public static final int TAG_CODE_LENGTH = TAG_CODE.length;
/*  64 */   public static final int TAG_LITERAL_LENGTH = TAG_LITERAL.length;
/*  65 */   public static final int TAG_DOC_ROOT_LENGTH = TAG_DOC_ROOT.length;
/*     */   public static final int NO_TAG_VALUE = 0;
/*     */   public static final int TAG_DEPRECATED_VALUE = 1;
/*     */   public static final int TAG_PARAM_VALUE = 2;
/*     */   public static final int TAG_RETURN_VALUE = 3;
/*     */   public static final int TAG_THROWS_VALUE = 4;
/*     */   public static final int TAG_EXCEPTION_VALUE = 5;
/*     */   public static final int TAG_SEE_VALUE = 6;
/*     */   public static final int TAG_LINK_VALUE = 7;
/*     */   public static final int TAG_LINKPLAIN_VALUE = 8;
/*     */   public static final int TAG_INHERITDOC_VALUE = 9;
/*     */   public static final int TAG_VALUE_VALUE = 10;
/*     */   public static final int TAG_CATEGORY_VALUE = 11;
/*     */   public static final int TAG_AUTHOR_VALUE = 12;
/*     */   public static final int TAG_SERIAL_VALUE = 13;
/*     */   public static final int TAG_SERIAL_DATA_VALUE = 14;
/*     */   public static final int TAG_SERIAL_FIELD_VALUE = 15;
/*     */   public static final int TAG_SINCE_VALUE = 16;
/*     */   public static final int TAG_VERSION_VALUE = 17;
/*     */   public static final int TAG_CODE_VALUE = 18;
/*     */   public static final int TAG_LITERAL_VALUE = 19;
/*     */   public static final int TAG_DOC_ROOT_VALUE = 20;
/*     */   public static final int TAG_OTHERS_VALUE = 100;
/*  92 */   public static final char[][] TAG_NAMES = { 
/*  93 */     CharOperation.NO_CHAR, 
/*  94 */     TAG_DEPRECATED, 
/*  95 */     TAG_PARAM, 
/*  96 */     TAG_RETURN, 
/*  97 */     TAG_THROWS, 
/*  98 */     TAG_EXCEPTION, 
/*  99 */     TAG_SEE, 
/* 100 */     TAG_LINK, 
/* 101 */     TAG_LINKPLAIN, 
/* 102 */     TAG_INHERITDOC, 
/* 103 */     TAG_VALUE, 
/* 104 */     TAG_CATEGORY, 
/* 105 */     TAG_AUTHOR, 
/* 106 */     TAG_SERIAL, 
/* 107 */     TAG_SERIAL_DATA, 
/* 108 */     TAG_SERIAL_FIELD, 
/* 109 */     TAG_SINCE, 
/* 110 */     TAG_VERSION, 
/* 111 */     TAG_CODE, 
/* 112 */     TAG_LITERAL, 
/* 113 */     TAG_DOC_ROOT };
/*     */   public static final int ORDERED_TAGS_NUMBER = 3;
/*     */   public static final int PARAM_TAG_EXPECTED_ORDER = 0;
/*     */   public static final int THROWS_TAG_EXPECTED_ORDER = 1;
/*     */   public static final int SEE_TAG_EXPECTED_ORDER = 2;
/*     */   public static final int BLOCK_IDX = 0;
/*     */   public static final int INLINE_IDX = 1;
/* 129 */   public static final char[] HREF_TAG = { 'h', 'r', 'e', 'f' };
/*     */ 
/* 133 */   public static final char[][][] BLOCK_TAGS = { 
/* 135 */     { TAG_AUTHOR, TAG_DEPRECATED, TAG_EXCEPTION, TAG_PARAM, TAG_RETURN, TAG_SEE, TAG_VERSION, TAG_CATEGORY }, 
/* 137 */     { TAG_SINCE }, 
/* 139 */     { TAG_SERIAL, TAG_SERIAL_DATA, TAG_SERIAL_FIELD, TAG_THROWS }, 
/* 141 */     new char[0][], 
/* 143 */     new char[0][], 
/* 145 */     new char[0][], 
/* 147 */     new char[0][], 
/* 149 */     new char[0][] };
/*     */ 
/* 151 */   public static final char[][][] INLINE_TAGS = { 
/* 153 */     new char[0][], 
/* 155 */     new char[0][], 
/* 157 */     { TAG_LINK }, 
/* 159 */     { TAG_DOC_ROOT }, 
/* 161 */     { TAG_INHERITDOC, TAG_LINKPLAIN, TAG_VALUE }, 
/* 163 */     { TAG_CODE, TAG_LITERAL }, 
/* 165 */     new char[0][], 
/* 167 */     new char[0][] };
/*     */ 
/* 169 */   public static final int INLINE_TAGS_LENGTH = INLINE_TAGS.length;
/* 170 */   public static final int BLOCK_TAGS_LENGTH = BLOCK_TAGS.length;
/* 171 */   public static final int ALL_TAGS_LENGTH = BLOCK_TAGS_LENGTH + INLINE_TAGS_LENGTH;
/*     */   public static final short TAG_TYPE_NONE = 0;
/*     */   public static final short TAG_TYPE_INLINE = 1;
/*     */   public static final short TAG_TYPE_BLOCK = 2;
/* 177 */   public static final short[] JAVADOC_TAG_TYPE = { 
/* 179 */     0, 2, 
/* 180 */     2, 
/* 181 */     2, 
/* 182 */     2, 
/* 183 */     2, 
/* 184 */     2, 
/* 185 */     1, 
/* 186 */     1, 
/* 187 */     1, 
/* 188 */     1, 
/* 189 */     2, 
/* 190 */     2, 
/* 191 */     2, 
/* 192 */     2, 
/* 193 */     2, 
/* 194 */     2, 
/* 195 */     2, 
/* 196 */     1, 
/* 197 */     1, 
/* 198 */     1 };
/*     */ 
/* 203 */   public static final char[][] PACKAGE_TAGS = { 
/* 204 */     TAG_SEE, 
/* 205 */     TAG_SINCE, 
/* 206 */     TAG_SERIAL, 
/* 207 */     TAG_AUTHOR, 
/* 208 */     TAG_VERSION, 
/* 209 */     TAG_CATEGORY, 
/* 210 */     TAG_LINK, 
/* 211 */     TAG_LINKPLAIN, 
/* 212 */     TAG_DOC_ROOT, 
/* 213 */     TAG_VALUE };
/*     */ 
/* 215 */   public static final char[][] COMPILATION_UNIT_TAGS = new char[0][];
/* 216 */   public static final char[][] CLASS_TAGS = { 
/* 217 */     TAG_SEE, 
/* 218 */     TAG_SINCE, 
/* 219 */     TAG_DEPRECATED, 
/* 220 */     TAG_SERIAL, 
/* 221 */     TAG_AUTHOR, 
/* 222 */     TAG_VERSION, 
/* 223 */     TAG_PARAM, 
/* 224 */     TAG_CATEGORY, 
/* 225 */     TAG_LINK, 
/* 226 */     TAG_LINKPLAIN, 
/* 227 */     TAG_DOC_ROOT, 
/* 228 */     TAG_VALUE, 
/* 229 */     TAG_CODE, 
/* 230 */     TAG_LITERAL };
/*     */ 
/* 232 */   public static final char[][] FIELD_TAGS = { 
/* 233 */     TAG_SEE, 
/* 234 */     TAG_SINCE, 
/* 235 */     TAG_DEPRECATED, 
/* 236 */     TAG_SERIAL, 
/* 237 */     TAG_SERIAL_FIELD, 
/* 238 */     TAG_CATEGORY, 
/* 239 */     TAG_LINK, 
/* 240 */     TAG_LINKPLAIN, 
/* 241 */     TAG_DOC_ROOT, 
/* 242 */     TAG_VALUE, 
/* 243 */     TAG_CODE, 
/* 244 */     TAG_LITERAL };
/*     */ 
/* 246 */   public static final char[][] METHOD_TAGS = { 
/* 247 */     TAG_SEE, 
/* 248 */     TAG_SINCE, 
/* 249 */     TAG_DEPRECATED, 
/* 250 */     TAG_PARAM, 
/* 251 */     TAG_RETURN, 
/* 252 */     TAG_THROWS, 
/* 253 */     TAG_EXCEPTION, 
/* 254 */     TAG_SERIAL_DATA, 
/* 255 */     TAG_CATEGORY, 
/* 256 */     TAG_LINK, 
/* 257 */     TAG_LINKPLAIN, 
/* 258 */     TAG_INHERITDOC, 
/* 259 */     TAG_DOC_ROOT, 
/* 260 */     TAG_VALUE, 
/* 261 */     TAG_CODE, 
/* 262 */     TAG_LITERAL };
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.JavadocTagConstants
 * JD-Core Version:    0.6.0
 */