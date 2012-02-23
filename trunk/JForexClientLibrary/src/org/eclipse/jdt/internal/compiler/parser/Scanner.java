/*      */ package org.eclipse.jdt.internal.compiler.parser;
/*      */ 
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.core.compiler.InvalidInputException;
/*      */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*      */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class Scanner
/*      */   implements TerminalTokens
/*      */ {
/*      */   public long sourceLevel;
/*      */   public long complianceLevel;
/*   42 */   public boolean useAssertAsAnIndentifier = false;
/*      */ 
/*   44 */   public boolean containsAssertKeyword = false;
/*      */ 
/*   47 */   public boolean useEnumAsAnIndentifier = false;
/*      */ 
/*   49 */   public boolean recordLineSeparator = false;
/*      */   public char currentCharacter;
/*      */   public int startPosition;
/*      */   public int currentPosition;
/*      */   public int initialPosition;
/*      */   public int eofPosition;
/*   56 */   public boolean skipComments = false;
/*   57 */   public boolean tokenizeComments = false;
/*   58 */   public boolean tokenizeWhiteSpace = false;
/*      */   public char[] source;
/*      */   public char[] withoutUnicodeBuffer;
/*      */   public int withoutUnicodePtr;
/*   67 */   public boolean unicodeAsBackSlash = false;
/*      */ 
/*   69 */   public boolean scanningFloatLiteral = false;
/*      */   public static final int COMMENT_ARRAYS_SIZE = 30;
/*   73 */   public int[] commentStops = new int[30];
/*   74 */   public int[] commentStarts = new int[30];
/*   75 */   public int[] commentTagStarts = new int[30];
/*   76 */   public int commentPtr = -1;
/*   77 */   protected int lastCommentLinePosition = -1;
/*      */ 
/*   80 */   public char[][] foundTaskTags = null;
/*      */   public char[][] foundTaskMessages;
/*   82 */   public char[][] foundTaskPriorities = null;
/*      */   public int[][] foundTaskPositions;
/*   84 */   public int foundTaskCount = 0;
/*   85 */   public char[][] taskTags = null;
/*   86 */   public char[][] taskPriorities = null;
/*   87 */   public boolean isTaskCaseSensitive = true;
/*      */ 
/*   90 */   public boolean diet = false;
/*      */ 
/*   94 */   public int[] lineEnds = new int['ú'];
/*   95 */   public int linePtr = -1;
/*   96 */   public boolean wasAcr = false;
/*      */   public static final String END_OF_SOURCE = "End_Of_Source";
/*      */   public static final String INVALID_HEXA = "Invalid_Hexa_Literal";
/*      */   public static final String INVALID_OCTAL = "Invalid_Octal_Literal";
/*      */   public static final String INVALID_CHARACTER_CONSTANT = "Invalid_Character_Constant";
/*      */   public static final String INVALID_ESCAPE = "Invalid_Escape";
/*      */   public static final String INVALID_INPUT = "Invalid_Input";
/*      */   public static final String INVALID_UNICODE_ESCAPE = "Invalid_Unicode_Escape";
/*      */   public static final String INVALID_FLOAT = "Invalid_Float_Literal";
/*      */   public static final String INVALID_LOW_SURROGATE = "Invalid_Low_Surrogate";
/*      */   public static final String INVALID_HIGH_SURROGATE = "Invalid_High_Surrogate";
/*      */   public static final String NULL_SOURCE_STRING = "Null_Source_String";
/*      */   public static final String UNTERMINATED_STRING = "Unterminated_String";
/*      */   public static final String UNTERMINATED_COMMENT = "Unterminated_Comment";
/*      */   public static final String INVALID_CHAR_IN_STRING = "Invalid_Char_In_String";
/*      */   public static final String INVALID_DIGIT = "Invalid_Digit";
/*  115 */   private static final int[] EMPTY_LINE_ENDS = Util.EMPTY_INT_ARRAY;
/*      */ 
/*  118 */   static final char[] charArray_a = { 'a' };
/*  119 */   static final char[] charArray_b = { 'b' };
/*  120 */   static final char[] charArray_c = { 'c' };
/*  121 */   static final char[] charArray_d = { 'd' };
/*  122 */   static final char[] charArray_e = { 'e' };
/*  123 */   static final char[] charArray_f = { 'f' };
/*  124 */   static final char[] charArray_g = { 'g' };
/*  125 */   static final char[] charArray_h = { 'h' };
/*  126 */   static final char[] charArray_i = { 'i' };
/*  127 */   static final char[] charArray_j = { 'j' };
/*  128 */   static final char[] charArray_k = { 'k' };
/*  129 */   static final char[] charArray_l = { 'l' };
/*  130 */   static final char[] charArray_m = { 'm' };
/*  131 */   static final char[] charArray_n = { 'n' };
/*  132 */   static final char[] charArray_o = { 'o' };
/*  133 */   static final char[] charArray_p = { 'p' };
/*  134 */   static final char[] charArray_q = { 'q' };
/*  135 */   static final char[] charArray_r = { 'r' };
/*  136 */   static final char[] charArray_s = { 's' };
/*  137 */   static final char[] charArray_t = { 't' };
/*  138 */   static final char[] charArray_u = { 'u' };
/*  139 */   static final char[] charArray_v = { 'v' };
/*  140 */   static final char[] charArray_w = { 'w' };
/*  141 */   static final char[] charArray_x = { 'x' };
/*  142 */   static final char[] charArray_y = { 'y' };
/*  143 */   static final char[] charArray_z = { 'z' };
/*      */ 
/*  146 */   static final char[] initCharArray = new char[6];
/*      */   static final int TableSize = 30;
/*      */   static final int InternalTableSize = 6;
/*      */   public static final int OptimizedLength = 7;
/*  151 */   public final char[][][][] charArray_length = new char[7][30][6];
/*      */ 
/*  153 */   public static final char[] TAG_PREFIX = "//$NON-NLS-".toCharArray();
/*  154 */   public static final int TAG_PREFIX_LENGTH = TAG_PREFIX.length;
/*      */   public static final char TAG_POSTFIX = '$';
/*      */   public static final int TAG_POSTFIX_LENGTH = 1;
/*  157 */   private NLSTag[] nlsTags = null;
/*      */   protected int nlsTagsPtr;
/*      */   public boolean checkNonExternalizedStringLiterals;
/*      */   protected int lastPosition;
/*  164 */   public boolean returnOnlyGreater = false;
/*      */   int newEntry2;
/*      */   int newEntry3;
/*      */   int newEntry4;
/*      */   int newEntry5;
/*      */   int newEntry6;
/*      */   public boolean insideRecovery;
/*      */   public static final int RoundBracket = 0;
/*      */   public static final int SquareBracket = 1;
/*      */   public static final int CurlyBracket = 2;
/*      */   public static final int BracketKinds = 3;
/*      */   public static final int LOW_SURROGATE_MIN_VALUE = 56320;
/*      */   public static final int HIGH_SURROGATE_MIN_VALUE = 55296;
/*      */   public static final int HIGH_SURROGATE_MAX_VALUE = 56319;
/*      */   public static final int LOW_SURROGATE_MAX_VALUE = 57343;
/*      */ 
/*      */   public Scanner()
/*      */   {
/*  194 */     this(false, false, false, 3080192L, null, null, true);
/*      */   }
/*      */ 
/*      */   public Scanner(boolean tokenizeComments, boolean tokenizeWhiteSpace, boolean checkNonExternalizedStringLiterals, long sourceLevel, long complianceLevel, char[][] taskTags, char[][] taskPriorities, boolean isTaskCaseSensitive)
/*      */   {
/*  167 */     for (int i = 0; i < 6; i++) {
/*  168 */       for (int j = 0; j < 30; j++) {
/*  169 */         for (int k = 0; k < 6; k++) {
/*  170 */           this.charArray_length[i][j][k] = initCharArray;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  175 */     this.newEntry2 = 0;
/*  176 */     this.newEntry3 = 0;
/*  177 */     this.newEntry4 = 0;
/*  178 */     this.newEntry5 = 0;
/*  179 */     this.newEntry6 = 0;
/*  180 */     this.insideRecovery = false;
/*      */ 
/*  207 */     this.eofPosition = 2147483647;
/*  208 */     this.tokenizeComments = tokenizeComments;
/*  209 */     this.tokenizeWhiteSpace = tokenizeWhiteSpace;
/*  210 */     this.sourceLevel = sourceLevel;
/*  211 */     this.complianceLevel = complianceLevel;
/*  212 */     this.checkNonExternalizedStringLiterals = checkNonExternalizedStringLiterals;
/*  213 */     if (taskTags != null) {
/*  214 */       int length = taskTags.length;
/*  215 */       if (taskPriorities != null) {
/*  216 */         int[] initialIndexes = new int[length];
/*  217 */         for (int i = 0; i < length; i++) {
/*  218 */           initialIndexes[i] = i;
/*      */         }
/*  220 */         Util.reverseQuickSort(taskTags, 0, taskTags.length - 1, initialIndexes);
/*  221 */         char[][] temp = new char[length][];
/*  222 */         for (int i = 0; i < length; i++) {
/*  223 */           temp[i] = taskPriorities[initialIndexes[i]];
/*      */         }
/*  225 */         this.taskPriorities = temp;
/*      */       } else {
/*  227 */         Util.reverseQuickSort(taskTags, 0, taskTags.length - 1);
/*      */       }
/*  229 */       this.taskTags = taskTags;
/*  230 */       this.isTaskCaseSensitive = isTaskCaseSensitive;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Scanner(boolean tokenizeComments, boolean tokenizeWhiteSpace, boolean checkNonExternalizedStringLiterals, long sourceLevel, char[][] taskTags, char[][] taskPriorities, boolean isTaskCaseSensitive)
/*      */   {
/*  251 */     this(tokenizeComments, 
/*  245 */       tokenizeWhiteSpace, 
/*  246 */       checkNonExternalizedStringLiterals, 
/*  247 */       sourceLevel, 
/*  248 */       sourceLevel, 
/*  249 */       taskTags, 
/*  250 */       taskPriorities, 
/*  251 */       isTaskCaseSensitive);
/*      */   }
/*      */ 
/*      */   public final boolean atEnd()
/*      */   {
/*  258 */     return this.eofPosition <= this.currentPosition;
/*      */   }
/*      */ 
/*      */   public void checkTaskTag(int commentStart, int commentEnd)
/*      */     throws InvalidInputException
/*      */   {
/*  264 */     char[] src = this.source;
/*      */ 
/*  267 */     if ((this.foundTaskCount > 0) && 
/*  268 */       (this.foundTaskPositions[(this.foundTaskCount - 1)][0] >= commentStart)) {
/*  269 */       return;
/*      */     }
/*  271 */     int foundTaskIndex = this.foundTaskCount;
/*  272 */     char previous = src[(commentStart + 1)];
/*      */ 
/*  274 */     for (int i = commentStart + 2; (i < commentEnd) && (i < this.eofPosition); i++) {
/*  275 */       char[] tag = (char[])null;
/*  276 */       char[] priority = (char[])null;
/*      */ 
/*  278 */       if (previous != '@')
/*  279 */         for (int itag = 0; itag < this.taskTags.length; itag++) {
/*  280 */           tag = this.taskTags[itag];
/*  281 */           int tagLength = tag.length;
/*  282 */           if (tagLength == 0) {
/*      */             continue;
/*      */           }
/*  285 */           if ((ScannerHelper.isJavaIdentifierStart(tag[0])) && 
/*  286 */             (ScannerHelper.isJavaIdentifierPart(previous)))
/*      */           {
/*      */             continue;
/*      */           }
/*      */ 
/*  291 */           int t = 0;
/*      */           while (true) {
/*  293 */             int x = i + t;
/*  294 */             if ((x >= this.eofPosition) || (x >= commentEnd))
/*      */               break;
/*      */             char sc;
/*      */             char tc;
/*  296 */             if (((sc = src[(i + t)]) != (tc = tag[t])) && (
/*  298 */               (this.isTaskCaseSensitive) || (ScannerHelper.toLowerCase(sc) != ScannerHelper.toLowerCase(tc))))
/*      */               break;
/*  291 */             t++; if (t < tagLength)
/*      */             {
/*      */               continue;
/*      */             }
/*      */ 
/*  304 */             if ((i + tagLength < commentEnd) && (ScannerHelper.isJavaIdentifierPart(src[(i + tagLength - 1)])) && 
/*  305 */               (ScannerHelper.isJavaIdentifierPart(src[(i + tagLength)]))) {
/*      */               break;
/*      */             }
/*  308 */             if (this.foundTaskTags == null) {
/*  309 */               this.foundTaskTags = new char[5][];
/*  310 */               this.foundTaskMessages = new char[5][];
/*  311 */               this.foundTaskPriorities = new char[5][];
/*  312 */               this.foundTaskPositions = new int[5][];
/*  313 */             } else if (this.foundTaskCount == this.foundTaskTags.length) {
/*  314 */               System.arraycopy(this.foundTaskTags, 0, this.foundTaskTags = new char[this.foundTaskCount * 2][], 0, this.foundTaskCount);
/*  315 */               System.arraycopy(this.foundTaskMessages, 0, this.foundTaskMessages = new char[this.foundTaskCount * 2][], 0, this.foundTaskCount);
/*  316 */               System.arraycopy(this.foundTaskPriorities, 0, this.foundTaskPriorities = new char[this.foundTaskCount * 2][], 0, this.foundTaskCount);
/*  317 */               System.arraycopy(this.foundTaskPositions, 0, this.foundTaskPositions = new int[this.foundTaskCount * 2][], 0, this.foundTaskCount);
/*      */             }
/*      */ 
/*  320 */             priority = (this.taskPriorities != null) && (itag < this.taskPriorities.length) ? 
/*  321 */               this.taskPriorities[itag] : 
/*  322 */               null;
/*      */ 
/*  324 */             this.foundTaskTags[this.foundTaskCount] = tag;
/*  325 */             this.foundTaskPriorities[this.foundTaskCount] = priority;
/*  326 */             this.foundTaskPositions[this.foundTaskCount] = { i, i + tagLength - 1 };
/*  327 */             this.foundTaskMessages[this.foundTaskCount] = CharOperation.NO_CHAR;
/*  328 */             this.foundTaskCount += 1;
/*  329 */             i += tagLength - 1;
/*  330 */             break label532;
/*      */           }
/*      */         }
/*  333 */       label532: previous = src[i];
/*      */     }
/*  335 */     boolean containsEmptyTask = false;
/*  336 */     for (int i = foundTaskIndex; i < this.foundTaskCount; i++)
/*      */     {
/*  338 */       int msgStart = this.foundTaskPositions[i][0] + this.foundTaskTags[i].length;
/*  339 */       int max_value = i + 1 < this.foundTaskCount ? 
/*  340 */         this.foundTaskPositions[(i + 1)][0] - 1 : 
/*  341 */         commentEnd - 1;
/*      */ 
/*  343 */       if (max_value < msgStart) {
/*  344 */         max_value = msgStart;
/*      */       }
/*  346 */       int end = -1;
/*      */ 
/*  348 */       for (int j = msgStart; j < max_value; j++)
/*      */       {
/*      */         char c;
/*  349 */         if (((c = src[j]) == '\n') || (c == '\r')) {
/*  350 */           end = j - 1;
/*  351 */           break;
/*      */         }
/*      */       }
/*  354 */       if (end == -1) {
/*  355 */         for (int j = max_value; j > msgStart; j--)
/*      */         {
/*      */           char c;
/*  356 */           if ((c = src[j]) == '*') {
/*  357 */             end = j - 1;
/*  358 */             break;
/*      */           }
/*      */         }
/*  361 */         if (end == -1)
/*  362 */           end = max_value;
/*      */       }
/*  364 */       if (msgStart == end)
/*      */       {
/*  367 */         containsEmptyTask = true;
/*      */       }
/*      */       else
/*      */       {
/*      */         do {
/*  372 */           end--;
/*      */ 
/*  371 */           if (!CharOperation.isWhitespace(src[end])) break; 
/*  371 */         }while (msgStart <= end);
/*      */ 
/*  373 */         while ((CharOperation.isWhitespace(src[msgStart])) && (msgStart <= end)) {
/*  374 */           msgStart++;
/*      */         }
/*  376 */         this.foundTaskPositions[i][1] = end;
/*      */ 
/*  378 */         int messageLength = end - msgStart + 1;
/*  379 */         char[] message = new char[messageLength];
/*  380 */         System.arraycopy(src, msgStart, message, 0, messageLength);
/*  381 */         this.foundTaskMessages[i] = message;
/*      */       }
/*      */     }
/*  383 */     if (containsEmptyTask) {
/*  384 */       int i = foundTaskIndex; for (int max = this.foundTaskCount; i < max; i++)
/*  385 */         if (this.foundTaskMessages[i].length == 0)
/*  386 */           for (int j = i + 1; j < max; j++)
/*  387 */             if (this.foundTaskMessages[j].length != 0) {
/*  388 */               this.foundTaskMessages[i] = this.foundTaskMessages[j];
/*  389 */               this.foundTaskPositions[i][1] = this.foundTaskPositions[j][1];
/*  390 */               break;
/*      */             }
/*      */     }
/*      */   }
/*      */ 
/*      */   public char[] getCurrentIdentifierSource()
/*      */   {
/*      */     char[] result;
/*  402 */     if (this.withoutUnicodePtr != 0)
/*      */     {
/*      */       char[] result;
/*  404 */       System.arraycopy(
/*  405 */         this.withoutUnicodeBuffer, 
/*  406 */         1, 
/*  407 */         result = new char[this.withoutUnicodePtr], 
/*  408 */         0, 
/*  409 */         this.withoutUnicodePtr);
/*      */     } else {
/*  411 */       int length = this.currentPosition - this.startPosition;
/*  412 */       if (length == this.eofPosition) return this.source;
/*  413 */       switch (length) {
/*      */       case 1:
/*  415 */         return optimizedCurrentTokenSource1();
/*      */       case 2:
/*  417 */         return optimizedCurrentTokenSource2();
/*      */       case 3:
/*  419 */         return optimizedCurrentTokenSource3();
/*      */       case 4:
/*  421 */         return optimizedCurrentTokenSource4();
/*      */       case 5:
/*  423 */         return optimizedCurrentTokenSource5();
/*      */       case 6:
/*  425 */         return optimizedCurrentTokenSource6();
/*      */       }
/*      */ 
/*  428 */       System.arraycopy(this.source, this.startPosition, result = new char[length], 0, length);
/*      */     }
/*      */ 
/*  431 */     return result;
/*      */   }
/*      */   public int getCurrentTokenEndPosition() {
/*  434 */     return this.currentPosition - 1;
/*      */   }
/*      */ 
/*      */   public char[] getCurrentTokenSource()
/*      */   {
/*      */     char[] result;
/*  440 */     if (this.withoutUnicodePtr != 0)
/*      */     {
/*      */       char[] result;
/*  442 */       System.arraycopy(
/*  443 */         this.withoutUnicodeBuffer, 
/*  444 */         1, 
/*  445 */         result = new char[this.withoutUnicodePtr], 
/*  446 */         0, 
/*  447 */         this.withoutUnicodePtr);
/*      */     }
/*      */     else
/*      */     {
/*      */       int length;
/*  450 */       System.arraycopy(
/*  451 */         this.source, 
/*  452 */         this.startPosition, 
/*  453 */         result = new char[length = this.currentPosition - this.startPosition], 
/*  454 */         0, 
/*  455 */         length);
/*      */     }
/*  457 */     return result;
/*      */   }
/*      */ 
/*      */   public final String getCurrentTokenString()
/*      */   {
/*  462 */     if (this.withoutUnicodePtr != 0)
/*      */     {
/*  464 */       return new String(
/*  465 */         this.withoutUnicodeBuffer, 
/*  466 */         1, 
/*  467 */         this.withoutUnicodePtr);
/*      */     }
/*  469 */     return new String(
/*  470 */       this.source, 
/*  471 */       this.startPosition, 
/*  472 */       this.currentPosition - this.startPosition);
/*      */   }
/*      */ 
/*      */   public char[] getCurrentTokenSourceString()
/*      */   {
/*      */     char[] result;
/*  479 */     if (this.withoutUnicodePtr != 0)
/*      */     {
/*      */       char[] result;
/*  481 */       System.arraycopy(this.withoutUnicodeBuffer, 2, 
/*  483 */         result = new char[this.withoutUnicodePtr - 2], 0, this.withoutUnicodePtr - 2);
/*      */     }
/*      */     else
/*      */     {
/*      */       int length;
/*  486 */       System.arraycopy(
/*  487 */         this.source, 
/*  488 */         this.startPosition + 1, 
/*  489 */         result = new char[length = this.currentPosition - this.startPosition - 2], 
/*  490 */         0, 
/*  491 */         length);
/*      */     }
/*  493 */     return result;
/*      */   }
/*      */ 
/*      */   public final String getCurrentStringLiteral()
/*      */   {
/*  499 */     if (this.withoutUnicodePtr != 0)
/*      */     {
/*  502 */       return new String(this.withoutUnicodeBuffer, 2, this.withoutUnicodePtr - 2);
/*      */     }
/*  504 */     return new String(this.source, this.startPosition + 1, this.currentPosition - this.startPosition - 2);
/*      */   }
/*      */ 
/*      */   public final char[] getRawTokenSource() {
/*  508 */     int length = this.currentPosition - this.startPosition;
/*  509 */     char[] tokenSource = new char[length];
/*  510 */     System.arraycopy(this.source, this.startPosition, tokenSource, 0, length);
/*  511 */     return tokenSource;
/*      */   }
/*      */ 
/*      */   public final char[] getRawTokenSourceEnd() {
/*  515 */     int length = this.eofPosition - this.currentPosition - 1;
/*  516 */     char[] sourceEnd = new char[length];
/*  517 */     System.arraycopy(this.source, this.currentPosition, sourceEnd, 0, length);
/*  518 */     return sourceEnd;
/*      */   }
/*      */ 
/*      */   public int getCurrentTokenStartPosition() {
/*  522 */     return this.startPosition;
/*      */   }
/*      */ 
/*      */   public final int getLineEnd(int lineNumber)
/*      */   {
/*  534 */     if ((this.lineEnds == null) || (this.linePtr == -1))
/*  535 */       return -1;
/*  536 */     if (lineNumber > this.lineEnds.length + 1)
/*  537 */       return -1;
/*  538 */     if (lineNumber <= 0)
/*  539 */       return -1;
/*  540 */     if (lineNumber == this.lineEnds.length + 1)
/*  541 */       return this.eofPosition;
/*  542 */     return this.lineEnds[(lineNumber - 1)];
/*      */   }
/*      */ 
/*      */   public final int[] getLineEnds()
/*      */   {
/*  547 */     if (this.linePtr == -1)
/*  548 */       return EMPTY_LINE_ENDS;
/*      */     int[] copy;
/*  551 */     System.arraycopy(this.lineEnds, 0, copy = new int[this.linePtr + 1], 0, this.linePtr + 1);
/*  552 */     return copy;
/*      */   }
/*      */ 
/*      */   public final int getLineStart(int lineNumber)
/*      */   {
/*  570 */     if ((this.lineEnds == null) || (this.linePtr == -1))
/*  571 */       return -1;
/*  572 */     if (lineNumber > this.lineEnds.length + 1)
/*  573 */       return -1;
/*  574 */     if (lineNumber <= 0) {
/*  575 */       return -1;
/*      */     }
/*  577 */     if (lineNumber == 1)
/*  578 */       return this.initialPosition;
/*  579 */     return this.lineEnds[(lineNumber - 2)] + 1;
/*      */   }
/*      */   public final int getNextChar() {
/*      */     try {
/*  583 */       if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/*  584 */         (this.source[this.currentPosition] == 'u')) {
/*  585 */         getNextUnicodeChar();
/*      */       } else {
/*  587 */         this.unicodeAsBackSlash = false;
/*  588 */         if (this.withoutUnicodePtr != 0) {
/*  589 */           unicodeStore();
/*      */         }
/*      */       }
/*  592 */       return this.currentCharacter;
/*      */     } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*  594 */       return -1; } catch (InvalidInputException localInvalidInputException) {
/*      */     }
/*  596 */     return -1;
/*      */   }
/*      */ 
/*      */   public final int getNextCharWithBoundChecks() {
/*  600 */     if (this.currentPosition >= this.eofPosition) {
/*  601 */       return -1;
/*      */     }
/*  603 */     this.currentCharacter = this.source[(this.currentPosition++)];
/*  604 */     if (this.currentPosition >= this.eofPosition) {
/*  605 */       this.unicodeAsBackSlash = false;
/*  606 */       if (this.withoutUnicodePtr != 0) {
/*  607 */         unicodeStore();
/*      */       }
/*  609 */       return this.currentCharacter;
/*      */     }
/*  611 */     if ((this.currentCharacter == '\\') && (this.source[this.currentPosition] == 'u')) {
/*      */       try {
/*  613 */         getNextUnicodeChar();
/*      */       } catch (InvalidInputException localInvalidInputException) {
/*  615 */         return -1;
/*      */       }
/*      */     } else {
/*  618 */       this.unicodeAsBackSlash = false;
/*  619 */       if (this.withoutUnicodePtr != 0) {
/*  620 */         unicodeStore();
/*      */       }
/*      */     }
/*  623 */     return this.currentCharacter;
/*      */   }
/*      */ 
/*      */   public final boolean getNextChar(char testedChar)
/*      */   {
/*  636 */     if (this.currentPosition >= this.eofPosition) {
/*  637 */       this.unicodeAsBackSlash = false;
/*  638 */       return false;
/*      */     }
/*      */ 
/*  641 */     int temp = this.currentPosition;
/*      */     try {
/*  643 */       if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/*  644 */         (this.source[this.currentPosition] == 'u')) {
/*  645 */         getNextUnicodeChar();
/*  646 */         if (this.currentCharacter != testedChar) {
/*  647 */           this.currentPosition = temp;
/*  648 */           this.withoutUnicodePtr -= 1;
/*  649 */           return false;
/*      */         }
/*  651 */         return true;
/*      */       }
/*      */ 
/*  654 */       if (this.currentCharacter != testedChar) {
/*  655 */         this.currentPosition = temp;
/*  656 */         return false;
/*      */       }
/*  658 */       this.unicodeAsBackSlash = false;
/*  659 */       if (this.withoutUnicodePtr != 0)
/*  660 */         unicodeStore();
/*  661 */       return true;
/*      */     }
/*      */     catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*  664 */       this.unicodeAsBackSlash = false;
/*  665 */       this.currentPosition = temp;
/*  666 */       return false;
/*      */     } catch (InvalidInputException localInvalidInputException) {
/*  668 */       this.unicodeAsBackSlash = false;
/*  669 */       this.currentPosition = temp;
/*  670 */     }return false;
/*      */   }
/*      */ 
/*      */   public final int getNextChar(char testedChar1, char testedChar2)
/*      */   {
/*  684 */     if (this.currentPosition >= this.eofPosition) {
/*  685 */       return -1;
/*      */     }
/*  687 */     int temp = this.currentPosition;
/*      */     try
/*      */     {
/*  690 */       if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/*  691 */         (this.source[this.currentPosition] == 'u')) {
/*  692 */         getNextUnicodeChar();
/*      */         int result;
/*      */         int result;
/*  693 */         if (this.currentCharacter == testedChar1) {
/*  694 */           result = 0;
/*      */         }
/*      */         else
/*      */         {
/*      */           int result;
/*  695 */           if (this.currentCharacter == testedChar2) {
/*  696 */             result = 1;
/*      */           } else {
/*  698 */             this.currentPosition = temp;
/*  699 */             this.withoutUnicodePtr -= 1;
/*  700 */             result = -1;
/*      */           }
/*      */         }
/*  702 */         return result;
/*      */       }
/*      */       int result;
/*  704 */       if (this.currentCharacter == testedChar1) {
/*  705 */         result = 0;
/*      */       }
/*      */       else
/*      */       {
/*      */         int result;
/*  706 */         if (this.currentCharacter == testedChar2) {
/*  707 */           result = 1;
/*      */         } else {
/*  709 */           this.currentPosition = temp;
/*  710 */           return -1;
/*      */         }
/*      */       }
/*      */       int result;
/*  713 */       if (this.withoutUnicodePtr != 0)
/*  714 */         unicodeStore();
/*  715 */       return result;
/*      */     }
/*      */     catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*  718 */       this.currentPosition = temp;
/*  719 */       return -1;
/*      */     } catch (InvalidInputException localInvalidInputException) {
/*  721 */       this.currentPosition = temp;
/*  722 */     }return -1;
/*      */   }
/*      */ 
/*      */   public final boolean getNextCharAsDigit()
/*      */     throws InvalidInputException
/*      */   {
/*  735 */     if (this.currentPosition >= this.eofPosition) {
/*  736 */       return false;
/*      */     }
/*  738 */     int temp = this.currentPosition;
/*      */     try {
/*  740 */       if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/*  741 */         (this.source[this.currentPosition] == 'u')) {
/*  742 */         getNextUnicodeChar();
/*  743 */         if (!ScannerHelper.isDigit(this.currentCharacter)) {
/*  744 */           this.currentPosition = temp;
/*  745 */           this.withoutUnicodePtr -= 1;
/*  746 */           return false;
/*      */         }
/*  748 */         return true;
/*      */       }
/*  750 */       if (!ScannerHelper.isDigit(this.currentCharacter)) {
/*  751 */         this.currentPosition = temp;
/*  752 */         return false;
/*      */       }
/*  754 */       if (this.withoutUnicodePtr != 0)
/*  755 */         unicodeStore();
/*  756 */       return true;
/*      */     }
/*      */     catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*  759 */       this.currentPosition = temp;
/*  760 */       return false;
/*      */     } catch (InvalidInputException localInvalidInputException) {
/*  762 */       this.currentPosition = temp;
/*  763 */     }return false;
/*      */   }
/*      */ 
/*      */   public final boolean getNextCharAsDigit(int radix)
/*      */   {
/*  776 */     if (this.currentPosition >= this.eofPosition) {
/*  777 */       return false;
/*      */     }
/*  779 */     int temp = this.currentPosition;
/*      */     try {
/*  781 */       if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/*  782 */         (this.source[this.currentPosition] == 'u')) {
/*  783 */         getNextUnicodeChar();
/*  784 */         if (ScannerHelper.digit(this.currentCharacter, radix) == -1) {
/*  785 */           this.currentPosition = temp;
/*  786 */           this.withoutUnicodePtr -= 1;
/*  787 */           return false;
/*      */         }
/*  789 */         return true;
/*      */       }
/*  791 */       if (ScannerHelper.digit(this.currentCharacter, radix) == -1) {
/*  792 */         this.currentPosition = temp;
/*  793 */         return false;
/*      */       }
/*  795 */       if (this.withoutUnicodePtr != 0)
/*  796 */         unicodeStore();
/*  797 */       return true;
/*      */     }
/*      */     catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*  800 */       this.currentPosition = temp;
/*  801 */       return false;
/*      */     } catch (InvalidInputException localInvalidInputException) {
/*  803 */       this.currentPosition = temp;
/*  804 */     }return false;
/*      */   }
/*      */ 
/*      */   public boolean getNextCharAsJavaIdentifierPartWithBoundCheck()
/*      */   {
/*  817 */     int pos = this.currentPosition;
/*  818 */     if (pos >= this.eofPosition) {
/*  819 */       return false;
/*      */     }
/*  821 */     int temp2 = this.withoutUnicodePtr;
/*      */     try {
/*  823 */       boolean unicode = false;
/*  824 */       this.currentCharacter = this.source[(this.currentPosition++)];
/*  825 */       if ((this.currentPosition < this.eofPosition) && 
/*  826 */         (this.currentCharacter == '\\') && (this.source[this.currentPosition] == 'u')) {
/*  827 */         getNextUnicodeChar();
/*  828 */         unicode = true;
/*      */       }
/*      */ 
/*  831 */       char c = this.currentCharacter;
/*  832 */       boolean isJavaIdentifierPart = false;
/*  833 */       if ((c >= 55296) && (c <= 56319)) {
/*  834 */         if (this.complianceLevel < 3211264L) {
/*  835 */           this.currentPosition = pos;
/*  836 */           this.withoutUnicodePtr = temp2;
/*  837 */           return false;
/*      */         }
/*      */ 
/*  840 */         char low = (char)getNextCharWithBoundChecks();
/*  841 */         if ((low < 56320) || (low > 57343))
/*      */         {
/*  843 */           this.currentPosition = pos;
/*  844 */           this.withoutUnicodePtr = temp2;
/*  845 */           return false;
/*      */         }
/*  847 */         isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(c, low);
/*      */       } else {
/*  849 */         if ((c >= 56320) && (c <= 57343)) {
/*  850 */           this.currentPosition = pos;
/*  851 */           this.withoutUnicodePtr = temp2;
/*  852 */           return false;
/*      */         }
/*  854 */         isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(c);
/*      */       }
/*  856 */       if (unicode) {
/*  857 */         if (!isJavaIdentifierPart) {
/*  858 */           this.currentPosition = pos;
/*  859 */           this.withoutUnicodePtr = temp2;
/*  860 */           return false;
/*      */         }
/*  862 */         return true;
/*      */       }
/*  864 */       if (!isJavaIdentifierPart) {
/*  865 */         this.currentPosition = pos;
/*  866 */         return false;
/*      */       }
/*      */ 
/*  869 */       if (this.withoutUnicodePtr != 0)
/*  870 */         unicodeStore();
/*  871 */       return true;
/*      */     }
/*      */     catch (InvalidInputException localInvalidInputException) {
/*  874 */       this.currentPosition = pos;
/*  875 */       this.withoutUnicodePtr = temp2;
/*  876 */     }return false;
/*      */   }
/*      */ 
/*      */   public boolean getNextCharAsJavaIdentifierPart()
/*      */   {
/*      */     int pos;
/*  890 */     if ((pos = this.currentPosition) >= this.eofPosition) {
/*  891 */       return false;
/*      */     }
/*  893 */     int temp2 = this.withoutUnicodePtr;
/*      */     try {
/*  895 */       boolean unicode = false;
/*  896 */       if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/*  897 */         (this.source[this.currentPosition] == 'u')) {
/*  898 */         getNextUnicodeChar();
/*  899 */         unicode = true;
/*      */       }
/*  901 */       char c = this.currentCharacter;
/*  902 */       boolean isJavaIdentifierPart = false;
/*  903 */       if ((c >= 55296) && (c <= 56319)) {
/*  904 */         if (this.complianceLevel < 3211264L) {
/*  905 */           this.currentPosition = pos;
/*  906 */           this.withoutUnicodePtr = temp2;
/*  907 */           return false;
/*      */         }
/*      */ 
/*  910 */         char low = (char)getNextChar();
/*  911 */         if ((low < 56320) || (low > 57343))
/*      */         {
/*  913 */           this.currentPosition = pos;
/*  914 */           this.withoutUnicodePtr = temp2;
/*  915 */           return false;
/*      */         }
/*  917 */         isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(c, low);
/*      */       } else {
/*  919 */         if ((c >= 56320) && (c <= 57343)) {
/*  920 */           this.currentPosition = pos;
/*  921 */           this.withoutUnicodePtr = temp2;
/*  922 */           return false;
/*      */         }
/*  924 */         isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(c);
/*      */       }
/*  926 */       if (unicode) {
/*  927 */         if (!isJavaIdentifierPart) {
/*  928 */           this.currentPosition = pos;
/*  929 */           this.withoutUnicodePtr = temp2;
/*  930 */           return false;
/*      */         }
/*  932 */         return true;
/*      */       }
/*  934 */       if (!isJavaIdentifierPart) {
/*  935 */         this.currentPosition = pos;
/*  936 */         return false;
/*      */       }
/*      */ 
/*  939 */       if (this.withoutUnicodePtr != 0)
/*  940 */         unicodeStore();
/*  941 */       return true;
/*      */     }
/*      */     catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*  944 */       this.currentPosition = pos;
/*  945 */       this.withoutUnicodePtr = temp2;
/*  946 */       return false;
/*      */     } catch (InvalidInputException localInvalidInputException) {
/*  948 */       this.currentPosition = pos;
/*  949 */       this.withoutUnicodePtr = temp2;
/*  950 */     }return false; } 
/*  959 */   public int scanIdentifier() throws InvalidInputException { int whiteStart = 0;
/*      */ 
/*  961 */     this.withoutUnicodePtr = 0;
/*      */ 
/*  964 */     whiteStart = this.currentPosition;
/*  965 */     boolean hasWhiteSpaces = false;
/*      */ 
/*  968 */     boolean checkIfUnicode = false;
/*      */     int unicodePtr;
/*      */     int offset;
/*      */     boolean isWhiteSpace;
/*      */     do { unicodePtr = this.withoutUnicodePtr;
/*  971 */       offset = this.currentPosition;
/*  972 */       this.startPosition = this.currentPosition;
/*  973 */       if (this.currentPosition < this.eofPosition) {
/*  974 */         this.currentCharacter = this.source[(this.currentPosition++)];
/*  975 */         checkIfUnicode = (this.currentPosition < this.eofPosition) && 
/*  976 */           (this.currentCharacter == '\\') && 
/*  977 */           (this.source[this.currentPosition] == 'u'); } else {
/*  978 */         if ((this.tokenizeWhiteSpace) && (whiteStart != this.currentPosition - 1))
/*      */         {
/*  980 */           this.currentPosition -= 1;
/*  981 */           this.startPosition = whiteStart;
/*  982 */           return 1000;
/*      */         }
/*  984 */         return 68;
/*      */       }
/*  986 */       if (checkIfUnicode) {
/*  987 */         boolean isWhiteSpace = jumpOverUnicodeWhiteSpace();
/*  988 */         offset = this.currentPosition - offset;
/*      */       } else {
/*  990 */         offset = this.currentPosition - offset;
/*      */         boolean isWhiteSpace;
/*  994 */         switch (this.currentCharacter) {
/*      */         case '\t':
/*      */         case '\n':
/*      */         case '\f':
/*      */         case '\r':
/*      */         case ' ':
/* 1000 */           isWhiteSpace = true;
/* 1001 */           break;
/*      */         default:
/* 1003 */           isWhiteSpace = false;
/*      */         }
/*      */       }
/* 1006 */       if (isWhiteSpace)
/* 1007 */         hasWhiteSpaces = true;
/*      */     }
/*  969 */     while (
/* 1009 */       isWhiteSpace);
/* 1010 */     if (hasWhiteSpaces) {
/* 1011 */       if (this.tokenizeWhiteSpace)
/*      */       {
/* 1013 */         this.currentPosition -= offset;
/* 1014 */         this.startPosition = whiteStart;
/* 1015 */         if (checkIfUnicode) {
/* 1016 */           this.withoutUnicodePtr = unicodePtr;
/*      */         }
/* 1018 */         return 1000;
/* 1019 */       }if (checkIfUnicode) {
/* 1020 */         this.withoutUnicodePtr = 0;
/* 1021 */         unicodeStore();
/*      */       } else {
/* 1023 */         this.withoutUnicodePtr = 0;
/*      */       }
/*      */     }
/* 1026 */     char c = this.currentCharacter;
/* 1027 */     if (c < '') {
/* 1028 */       if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0) {
/* 1029 */         return scanIdentifierOrKeywordWithBoundCheck();
/*      */       }
/* 1031 */       return 110;
/*      */     }
/*      */     boolean isJavaIdStart;
/*      */     boolean isJavaIdStart;
/* 1034 */     if ((c >= 55296) && (c <= 56319)) {
/* 1035 */       if (this.complianceLevel < 3211264L) {
/* 1036 */         throw new InvalidInputException("Invalid_Unicode_Escape");
/*      */       }
/*      */ 
/* 1039 */       char low = (char)getNextCharWithBoundChecks();
/* 1040 */       if ((low < 56320) || (low > 57343))
/*      */       {
/* 1042 */         throw new InvalidInputException("Invalid_Low_Surrogate");
/*      */       }
/* 1044 */       isJavaIdStart = ScannerHelper.isJavaIdentifierStart(c, low); } else {
/* 1045 */       if ((c >= 56320) && (c <= 57343)) {
/* 1046 */         if (this.complianceLevel < 3211264L) {
/* 1047 */           throw new InvalidInputException("Invalid_Unicode_Escape");
/*      */         }
/* 1049 */         throw new InvalidInputException("Invalid_High_Surrogate");
/*      */       }
/*      */ 
/* 1052 */       isJavaIdStart = Character.isJavaIdentifierStart(c);
/*      */     }
/* 1054 */     if (isJavaIdStart)
/* 1055 */       return scanIdentifierOrKeywordWithBoundCheck();
/* 1056 */     return 110; }
/*      */ 
/*      */   public int getNextToken() throws InvalidInputException
/*      */   {
/* 1060 */     this.wasAcr = false;
/* 1061 */     if (this.diet) {
/* 1062 */       jumpOverMethodBody();
/* 1063 */       this.diet = false;
/* 1064 */       return this.currentPosition > this.eofPosition ? 68 : 31;
/*      */     }
/* 1066 */     int whiteStart = 0;
/*      */     try { while (true) { this.withoutUnicodePtr = 0;
/*      */ 
/* 1073 */         whiteStart = this.currentPosition;
/* 1074 */         boolean hasWhiteSpaces = false;
/*      */ 
/* 1077 */         boolean checkIfUnicode = false;
/*      */         int unicodePtr;
/*      */         int offset;
/*      */         boolean isWhiteSpace;
/*      */         do { unicodePtr = this.withoutUnicodePtr;
/* 1080 */           offset = this.currentPosition;
/* 1081 */           this.startPosition = this.currentPosition;
/*      */           try {
/* 1083 */             checkIfUnicode = ((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1084 */               (this.source[this.currentPosition] == 'u');
/*      */           } catch (IndexOutOfBoundsException localIndexOutOfBoundsException1) {
/* 1086 */             if ((this.tokenizeWhiteSpace) && (whiteStart != this.currentPosition - 1))
/*      */             {
/* 1088 */               this.currentPosition -= 1;
/* 1089 */               this.startPosition = whiteStart;
/* 1090 */               return 1000;
/*      */             }
/* 1092 */             if (this.currentPosition > this.eofPosition)
/* 1093 */               return 68;
/*      */           }
/* 1095 */           if (this.currentPosition > this.eofPosition) {
/* 1096 */             if ((this.tokenizeWhiteSpace) && (whiteStart != this.currentPosition - 1)) {
/* 1097 */               this.currentPosition -= 1;
/*      */ 
/* 1099 */               this.startPosition = whiteStart;
/* 1100 */               return 1000;
/*      */             }
/* 1102 */             return 68;
/*      */           }
/* 1104 */           if (checkIfUnicode) {
/* 1105 */             boolean isWhiteSpace = jumpOverUnicodeWhiteSpace();
/* 1106 */             offset = this.currentPosition - offset;
/*      */           } else {
/* 1108 */             offset = this.currentPosition - offset;
/* 1109 */             if (((this.currentCharacter == '\r') || (this.currentCharacter == '\n')) && 
/* 1110 */               (this.recordLineSeparator))
/* 1111 */               pushLineSeparator();
/*      */             boolean isWhiteSpace;
/* 1117 */             switch (this.currentCharacter) {
/*      */             case '\t':
/*      */             case '\n':
/*      */             case '\f':
/*      */             case '\r':
/*      */             case ' ':
/* 1123 */               isWhiteSpace = true;
/* 1124 */               break;
/*      */             default:
/* 1126 */               isWhiteSpace = false;
/*      */             }
/*      */           }
/* 1129 */           if (isWhiteSpace)
/* 1130 */             hasWhiteSpaces = true;
/*      */         }
/* 1078 */         while (
/* 1132 */           isWhiteSpace);
/* 1133 */         if (hasWhiteSpaces) {
/* 1134 */           if (this.tokenizeWhiteSpace)
/*      */           {
/* 1136 */             this.currentPosition -= offset;
/* 1137 */             this.startPosition = whiteStart;
/* 1138 */             if (checkIfUnicode) {
/* 1139 */               this.withoutUnicodePtr = unicodePtr;
/*      */             }
/* 1141 */             return 1000;
/* 1142 */           }if (checkIfUnicode) {
/* 1143 */             this.withoutUnicodePtr = 0;
/* 1144 */             unicodeStore();
/*      */           } else {
/* 1146 */             this.withoutUnicodePtr = 0;
/*      */           }
/*      */         }
/*      */ 
/* 1150 */         switch (this.currentCharacter)
/*      */         {
/*      */         case '@':
/* 1157 */           return 53;
/*      */         case '(':
/* 1159 */           return 28;
/*      */         case ')':
/* 1161 */           return 29;
/*      */         case '{':
/* 1163 */           return 69;
/*      */         case '}':
/* 1165 */           return 31;
/*      */         case '[':
/* 1167 */           return 14;
/*      */         case ']':
/* 1169 */           return 70;
/*      */         case ';':
/* 1171 */           return 27;
/*      */         case ',':
/* 1173 */           return 30;
/*      */         case '.':
/* 1175 */           if (getNextCharAsDigit()) {
/* 1176 */             return scanNumber(true);
/*      */           }
/* 1178 */           int temp = this.currentPosition;
/* 1179 */           if (getNextChar('.')) {
/* 1180 */             if (getNextChar('.')) {
/* 1181 */               return 107;
/*      */             }
/* 1183 */             this.currentPosition = temp;
/* 1184 */             return 3;
/*      */           }
/*      */ 
/* 1187 */           this.currentPosition = temp;
/* 1188 */           return 3;
/*      */         case '+':
/*      */           int test;
/* 1193 */           if ((test = getNextChar('+', '=')) == 0)
/* 1194 */             return 8;
/* 1195 */           if (test > 0)
/* 1196 */             return 84;
/* 1197 */           return 1;
/*      */         case '-':
/*      */           int test;
/* 1202 */           if ((test = getNextChar('-', '=')) == 0)
/* 1203 */             return 9;
/* 1204 */           if (test > 0)
/* 1205 */             return 85;
/* 1206 */           return 2;
/*      */         case '~':
/* 1209 */           return 67;
/*      */         case '!':
/* 1211 */           if (getNextChar('='))
/* 1212 */             return 19;
/* 1213 */           return 66;
/*      */         case '*':
/* 1215 */           if (getNextChar('='))
/* 1216 */             return 86;
/* 1217 */           return 4;
/*      */         case '%':
/* 1219 */           if (getNextChar('='))
/* 1220 */             return 91;
/* 1221 */           return 5;
/*      */         case '<':
/*      */           int test;
/* 1225 */           if ((test = getNextChar('=', '<')) == 0)
/* 1226 */             return 15;
/* 1227 */           if (test > 0) {
/* 1228 */             if (getNextChar('='))
/* 1229 */               return 92;
/* 1230 */             return 17;
/*      */           }
/* 1232 */           return 7;
/*      */         case '>':
/* 1237 */           if (this.returnOnlyGreater)
/* 1238 */             return 13;
/*      */           int test;
/* 1240 */           if ((test = getNextChar('=', '>')) == 0)
/* 1241 */             return 16;
/* 1242 */           if (test > 0) {
/* 1243 */             if ((test = getNextChar('=', '>')) == 0)
/* 1244 */               return 93;
/* 1245 */             if (test > 0) {
/* 1246 */               if (getNextChar('='))
/* 1247 */                 return 94;
/* 1248 */               return 11;
/*      */             }
/* 1250 */             return 10;
/*      */           }
/* 1252 */           return 13;
/*      */         case '=':
/* 1255 */           if (getNextChar('='))
/* 1256 */             return 18;
/* 1257 */           return 71;
/*      */         case '&':
/*      */           int test;
/* 1261 */           if ((test = getNextChar('&', '=')) == 0)
/* 1262 */             return 24;
/* 1263 */           if (test > 0)
/* 1264 */             return 88;
/* 1265 */           return 20;
/*      */         case '|':
/*      */           int test;
/* 1270 */           if ((test = getNextChar('|', '=')) == 0)
/* 1271 */             return 25;
/* 1272 */           if (test > 0)
/* 1273 */             return 89;
/* 1274 */           return 22;
/*      */         case '^':
/* 1277 */           if (getNextChar('='))
/* 1278 */             return 90;
/* 1279 */           return 21;
/*      */         case '?':
/* 1281 */           return 23;
/*      */         case ':':
/* 1283 */           return 65;
/*      */         case '\'':
/*      */           int test;
/* 1287 */           if ((test = getNextChar('\n', '\r')) == 0) {
/* 1288 */             throw new InvalidInputException("Invalid_Character_Constant");
/*      */           }
/* 1290 */           if (test > 0)
/*      */           {
/* 1292 */             for (int lookAhead = 0; lookAhead < 3; lookAhead++) {
/* 1293 */               if (this.currentPosition + lookAhead == this.eofPosition)
/*      */                 break;
/* 1295 */               if (this.source[(this.currentPosition + lookAhead)] == '\n')
/*      */                 break;
/* 1297 */               if (this.source[(this.currentPosition + lookAhead)] == '\'') {
/* 1298 */                 this.currentPosition += lookAhead + 1;
/* 1299 */                 break;
/*      */               }
/*      */             }
/* 1302 */             throw new InvalidInputException("Invalid_Character_Constant");
/*      */           }
/*      */ 
/* 1305 */           if (getNextChar('\''))
/*      */           {
/* 1307 */             for (int lookAhead = 0; lookAhead < 3; lookAhead++) {
/* 1308 */               if (this.currentPosition + lookAhead == this.eofPosition)
/*      */                 break;
/* 1310 */               if (this.source[(this.currentPosition + lookAhead)] == '\n')
/*      */                 break;
/* 1312 */               if (this.source[(this.currentPosition + lookAhead)] == '\'') {
/* 1313 */                 this.currentPosition += lookAhead + 1;
/* 1314 */                 break;
/*      */               }
/*      */             }
/* 1317 */             throw new InvalidInputException("Invalid_Character_Constant");
/*      */           }
/* 1319 */           if (getNextChar('\\')) {
/* 1320 */             if (this.unicodeAsBackSlash)
/*      */             {
/* 1322 */               this.unicodeAsBackSlash = false;
/* 1323 */               if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && (this.source[this.currentPosition] == 'u')) {
/* 1324 */                 getNextUnicodeChar();
/*      */               }
/* 1326 */               else if (this.withoutUnicodePtr != 0)
/* 1327 */                 unicodeStore();
/*      */             }
/*      */             else
/*      */             {
/* 1331 */               this.currentCharacter = this.source[(this.currentPosition++)];
/*      */             }
/* 1333 */             scanEscapeCharacter();
/*      */           } else {
/* 1335 */             this.unicodeAsBackSlash = false;
/* 1336 */             checkIfUnicode = false;
/*      */             try {
/* 1338 */               checkIfUnicode = ((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1339 */                 (this.source[this.currentPosition] == 'u');
/*      */             } catch (IndexOutOfBoundsException localIndexOutOfBoundsException2) {
/* 1341 */               this.currentPosition -= 1;
/* 1342 */               throw new InvalidInputException("Invalid_Character_Constant");
/*      */             }
/* 1344 */             if (checkIfUnicode) {
/* 1345 */               getNextUnicodeChar();
/*      */             }
/* 1347 */             else if (this.withoutUnicodePtr != 0) {
/* 1348 */               unicodeStore();
/*      */             }
/*      */           }
/*      */ 
/* 1352 */           if (getNextChar('\'')) {
/* 1353 */             return 51;
/*      */           }
/* 1355 */           for (int lookAhead = 0; lookAhead < 20; lookAhead++) {
/* 1356 */             if (this.currentPosition + lookAhead == this.eofPosition)
/*      */               break;
/* 1358 */             if (this.source[(this.currentPosition + lookAhead)] == '\n')
/*      */               break;
/* 1360 */             if (this.source[(this.currentPosition + lookAhead)] == '\'') {
/* 1361 */               this.currentPosition += lookAhead + 1;
/* 1362 */               break;
/*      */             }
/*      */           }
/* 1365 */           throw new InvalidInputException("Invalid_Character_Constant");
/*      */         case '"':
/*      */           try
/*      */           {
/* 1369 */             this.unicodeAsBackSlash = false;
/* 1370 */             boolean isUnicode = false;
/* 1371 */             if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1372 */               (this.source[this.currentPosition] == 'u')) {
/* 1373 */               getNextUnicodeChar();
/* 1374 */               isUnicode = true;
/*      */             }
/* 1376 */             else if (this.withoutUnicodePtr != 0) {
/* 1377 */               unicodeStore();
/*      */             }
/*      */ 
/* 1381 */             while (this.currentCharacter != '"')
/*      */             {
/* 1383 */               if ((this.currentCharacter == '\n') || (this.currentCharacter == '\r'))
/*      */               {
/* 1385 */                 if (isUnicode) {
/* 1386 */                   int start = this.currentPosition;
/* 1387 */                   for (int lookAhead = 0; lookAhead < 50; lookAhead++) {
/* 1388 */                     if (this.currentPosition >= this.eofPosition) {
/* 1389 */                       this.currentPosition = start;
/* 1390 */                       break;
/*      */                     }
/* 1392 */                     if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && (this.source[this.currentPosition] == 'u')) {
/* 1393 */                       isUnicode = true;
/* 1394 */                       getNextUnicodeChar();
/*      */                     } else {
/* 1396 */                       isUnicode = false;
/*      */                     }
/* 1398 */                     if ((!isUnicode) && (this.currentCharacter == '\n')) {
/* 1399 */                       this.currentPosition -= 1;
/* 1400 */                       break;
/*      */                     }
/* 1402 */                     if (this.currentCharacter == '"')
/* 1403 */                       throw new InvalidInputException("Invalid_Char_In_String");
/*      */                   }
/*      */                 }
/*      */                 else {
/* 1407 */                   this.currentPosition -= 1;
/*      */                 }
/* 1409 */                 throw new InvalidInputException("Invalid_Char_In_String");
/*      */               }
/* 1411 */               if (this.currentCharacter == '\\') {
/* 1412 */                 if (this.unicodeAsBackSlash) {
/* 1413 */                   this.withoutUnicodePtr -= 1;
/*      */ 
/* 1415 */                   this.unicodeAsBackSlash = false;
/* 1416 */                   if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && (this.source[this.currentPosition] == 'u')) {
/* 1417 */                     getNextUnicodeChar();
/* 1418 */                     isUnicode = true;
/* 1419 */                     this.withoutUnicodePtr -= 1;
/*      */                   } else {
/* 1421 */                     isUnicode = false;
/*      */                   }
/*      */                 } else {
/* 1424 */                   if (this.withoutUnicodePtr == 0) {
/* 1425 */                     unicodeInitializeBuffer(this.currentPosition - this.startPosition);
/*      */                   }
/* 1427 */                   this.withoutUnicodePtr -= 1;
/* 1428 */                   this.currentCharacter = this.source[(this.currentPosition++)];
/*      */                 }
/*      */ 
/* 1431 */                 scanEscapeCharacter();
/* 1432 */                 if (this.withoutUnicodePtr != 0) {
/* 1433 */                   unicodeStore();
/*      */                 }
/*      */               }
/*      */ 
/* 1437 */               this.unicodeAsBackSlash = false;
/* 1438 */               if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1439 */                 (this.source[this.currentPosition] == 'u')) {
/* 1440 */                 getNextUnicodeChar();
/* 1441 */                 isUnicode = true;
/*      */               } else {
/* 1443 */                 isUnicode = false;
/* 1444 */                 if (this.withoutUnicodePtr != 0)
/* 1445 */                   unicodeStore();
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (IndexOutOfBoundsException localIndexOutOfBoundsException3)
/*      */           {
/* 1451 */             this.currentPosition -= 1;
/* 1452 */             throw new InvalidInputException("Unterminated_String");
/*      */           } catch (InvalidInputException e) {
/* 1454 */             if (e.getMessage().equals("Invalid_Escape"))
/*      */             {
/* 1456 */               for (int lookAhead = 0; lookAhead < 50; lookAhead++) {
/* 1457 */                 if (this.currentPosition + lookAhead == this.eofPosition)
/*      */                   break;
/* 1459 */                 if (this.source[(this.currentPosition + lookAhead)] == '\n')
/*      */                   break;
/* 1461 */                 if (this.source[(this.currentPosition + lookAhead)] == '"') {
/* 1462 */                   this.currentPosition += lookAhead + 1;
/* 1463 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 1468 */             throw e;
/*      */           }
/* 1470 */           return 52;
/*      */         case '/':
/* 1472 */           if (!this.skipComments) {
/* 1473 */             int test = getNextChar('/', '*');
/* 1474 */             if (test == 0) {
/* 1475 */               this.lastCommentLinePosition = this.currentPosition;
/*      */               try {
/* 1477 */                 if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1478 */                   (this.source[this.currentPosition] == 'u')) {
/* 1479 */                   getNextUnicodeChar();
/*      */                 }
/*      */ 
/* 1483 */                 if ((this.currentCharacter == '\\') && 
/* 1484 */                   (this.source[this.currentPosition] == '\\')) {
/* 1485 */                   this.currentPosition += 1;
/*      */                 }
/* 1487 */                 boolean isUnicode = false;
/* 1488 */                 while ((this.currentCharacter != '\r') && (this.currentCharacter != '\n')) {
/* 1489 */                   this.lastCommentLinePosition = this.currentPosition;
/*      */ 
/* 1491 */                   isUnicode = false;
/* 1492 */                   if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1493 */                     (this.source[this.currentPosition] == 'u')) {
/* 1494 */                     getNextUnicodeChar();
/* 1495 */                     isUnicode = true;
/*      */                   }
/*      */ 
/* 1498 */                   if ((this.currentCharacter != '\\') || 
/* 1499 */                     (this.source[this.currentPosition] != '\\')) continue;
/* 1500 */                   this.currentPosition += 1;
/*      */                 }
/*      */ 
/* 1506 */                 if ((this.currentCharacter == '\r') && 
/* 1507 */                   (this.eofPosition > this.currentPosition)) {
/* 1508 */                   if (this.source[this.currentPosition] == '\n') {
/* 1509 */                     this.currentPosition += 1;
/* 1510 */                     this.currentCharacter = '\n';
/* 1511 */                   } else if ((this.source[this.currentPosition] == '\\') && 
/* 1512 */                     (this.source[(this.currentPosition + 1)] == 'u')) {
/* 1513 */                     getNextUnicodeChar();
/* 1514 */                     isUnicode = true;
/*      */                   }
/*      */                 }
/* 1517 */                 recordComment(1001);
/* 1518 */                 if (this.taskTags != null) checkTaskTag(this.startPosition, this.currentPosition);
/* 1519 */                 if ((this.currentCharacter == '\r') || (this.currentCharacter == '\n')) {
/* 1520 */                   if ((this.checkNonExternalizedStringLiterals) && 
/* 1521 */                     (this.lastPosition < this.currentPosition)) {
/* 1522 */                     parseTags();
/*      */                   }
/* 1524 */                   if (this.recordLineSeparator) {
/* 1525 */                     if (isUnicode)
/* 1526 */                       pushUnicodeLineSeparator();
/*      */                     else {
/* 1528 */                       pushLineSeparator();
/*      */                     }
/*      */                   }
/*      */                 }
/* 1532 */                 if (this.tokenizeComments)
/* 1533 */                   return 1001;
/*      */               }
/*      */               catch (IndexOutOfBoundsException localIndexOutOfBoundsException4) {
/* 1536 */                 this.currentPosition -= 1;
/* 1537 */                 recordComment(1001);
/* 1538 */                 if (this.taskTags != null) checkTaskTag(this.startPosition, this.currentPosition);
/* 1539 */                 if ((this.checkNonExternalizedStringLiterals) && 
/* 1540 */                   (this.lastPosition < this.currentPosition)) {
/* 1541 */                   parseTags();
/*      */                 }
/* 1543 */                 if (this.tokenizeComments) {
/* 1544 */                   return 1001;
/*      */                 }
/* 1546 */                 this.currentPosition += 1;
/*      */               }
/*      */ 
/* 1549 */               continue;
/*      */             }
/* 1551 */             if (test > 0)
/*      */               try {
/* 1553 */                 boolean isJavadoc = false; boolean star = false;
/* 1554 */                 boolean isUnicode = false;
/*      */ 
/* 1557 */                 this.unicodeAsBackSlash = false;
/* 1558 */                 if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1559 */                   (this.source[this.currentPosition] == 'u')) {
/* 1560 */                   getNextUnicodeChar();
/* 1561 */                   isUnicode = true;
/*      */                 } else {
/* 1563 */                   isUnicode = false;
/* 1564 */                   if (this.withoutUnicodePtr != 0) {
/* 1565 */                     unicodeStore();
/*      */                   }
/*      */                 }
/*      */ 
/* 1569 */                 if (this.currentCharacter == '*') {
/* 1570 */                   isJavadoc = true;
/* 1571 */                   star = true;
/*      */                 }
/* 1573 */                 if (((this.currentCharacter == '\r') || (this.currentCharacter == '\n')) && 
/* 1574 */                   (this.recordLineSeparator)) {
/* 1575 */                   if (isUnicode)
/* 1576 */                     pushUnicodeLineSeparator();
/*      */                   else {
/* 1578 */                     pushLineSeparator();
/*      */                   }
/*      */                 }
/*      */ 
/* 1582 */                 isUnicode = false;
/* 1583 */                 int previous = this.currentPosition;
/* 1584 */                 if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1585 */                   (this.source[this.currentPosition] == 'u'))
/*      */                 {
/* 1587 */                   getNextUnicodeChar();
/* 1588 */                   isUnicode = true;
/*      */                 } else {
/* 1590 */                   isUnicode = false;
/*      */                 }
/*      */ 
/* 1593 */                 if ((this.currentCharacter == '\\') && 
/* 1594 */                   (this.source[this.currentPosition] == '\\')) {
/* 1595 */                   this.currentPosition += 1;
/*      */                 }
/*      */ 
/* 1598 */                 if (this.currentCharacter == '/') {
/* 1599 */                   isJavadoc = false;
/*      */                 }
/*      */ 
/* 1602 */                 int firstTag = 0;
/* 1603 */                 while ((this.currentCharacter != '/') || (!star)) {
/* 1604 */                   if (((this.currentCharacter == '\r') || (this.currentCharacter == '\n')) && 
/* 1605 */                     (this.recordLineSeparator)) {
/* 1606 */                     if (isUnicode)
/* 1607 */                       pushUnicodeLineSeparator();
/*      */                     else {
/* 1609 */                       pushLineSeparator();
/*      */                     }
/*      */                   }
/*      */ 
/* 1613 */                   switch (this.currentCharacter) {
/*      */                   case '*':
/* 1615 */                     star = true;
/* 1616 */                     break;
/*      */                   case '@':
/* 1618 */                     if ((firstTag != 0) || (!isFirstTag())) break;
/* 1619 */                     firstTag = previous;
/*      */                   }
/*      */ 
/* 1623 */                   star = false;
/*      */ 
/* 1626 */                   previous = this.currentPosition;
/* 1627 */                   if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1628 */                     (this.source[this.currentPosition] == 'u'))
/*      */                   {
/* 1630 */                     getNextUnicodeChar();
/* 1631 */                     isUnicode = true;
/*      */                   } else {
/* 1633 */                     isUnicode = false;
/*      */                   }
/*      */ 
/* 1636 */                   if ((this.currentCharacter != '\\') || 
/* 1637 */                     (this.source[this.currentPosition] != '\\')) continue;
/* 1638 */                   this.currentPosition += 1;
/*      */                 }
/*      */ 
/* 1641 */                 int token = isJavadoc ? 1003 : 1002;
/* 1642 */                 recordComment(token);
/* 1643 */                 this.commentTagStarts[this.commentPtr] = firstTag;
/* 1644 */                 if (this.taskTags != null) checkTaskTag(this.startPosition, this.currentPosition);
/* 1645 */                 if (this.tokenizeComments)
/*      */                 {
/* 1651 */                   return token;
/*      */                 }
/*      */               } catch (IndexOutOfBoundsException localIndexOutOfBoundsException5) {
/* 1654 */                 this.currentPosition -= 1;
/* 1655 */                 throw new InvalidInputException("Unterminated_Comment");
/*      */               }
/*      */           }
/*      */         case '\032':
/*      */         } }
/* 1660 */       if (getNextChar('='))
/* 1661 */         return 87;
/* 1662 */       return 6;
/*      */ 
/* 1664 */       if (atEnd()) {
/* 1665 */         return 68;
/*      */       }
/* 1667 */       throw new InvalidInputException("Ctrl-Z");
/*      */ 
/* 1669 */       char c = this.currentCharacter;
/* 1670 */       if (c < '') {
/* 1671 */         if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0)
/* 1672 */           return scanIdentifierOrKeyword();
/* 1673 */         if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x4) != 0) {
/* 1674 */           return scanNumber(false);
/*      */         }
/* 1676 */         return 110;
/*      */       }
/*      */       boolean isJavaIdStart;
/*      */       boolean isJavaIdStart;
/* 1680 */       if ((c >= 55296) && (c <= 56319)) {
/* 1681 */         if (this.complianceLevel < 3211264L) {
/* 1682 */           throw new InvalidInputException("Invalid_Unicode_Escape");
/*      */         }
/*      */ 
/* 1685 */         char low = (char)getNextChar();
/* 1686 */         if ((low < 56320) || (low > 57343))
/*      */         {
/* 1688 */           throw new InvalidInputException("Invalid_Low_Surrogate");
/*      */         }
/* 1690 */         isJavaIdStart = ScannerHelper.isJavaIdentifierStart(c, low);
/*      */       } else {
/* 1692 */         if ((c >= 56320) && (c <= 57343)) {
/* 1693 */           if (this.complianceLevel < 3211264L) {
/* 1694 */             throw new InvalidInputException("Invalid_Unicode_Escape");
/*      */           }
/* 1696 */           throw new InvalidInputException("Invalid_High_Surrogate");
/*      */         }
/*      */ 
/* 1699 */         isJavaIdStart = Character.isJavaIdentifierStart(c);
/*      */       }
/* 1701 */       if (isJavaIdStart)
/* 1702 */         return scanIdentifierOrKeyword();
/* 1703 */       if (ScannerHelper.isDigit(this.currentCharacter)) {
/* 1704 */         return scanNumber(false);
/*      */       }
/* 1706 */       return 110;
/*      */     }
/*      */     catch (IndexOutOfBoundsException localIndexOutOfBoundsException6)
/*      */     {
/* 1711 */       if ((this.tokenizeWhiteSpace) && (whiteStart != this.currentPosition - 1))
/*      */       {
/* 1713 */         this.currentPosition -= 1;
/* 1714 */         this.startPosition = whiteStart;
/* 1715 */         return 1000;
/*      */       }
/*      */     }
/* 1718 */     return 68;
/*      */   }
/*      */ 
/*      */   public void getNextUnicodeChar()
/*      */     throws InvalidInputException
/*      */   {
/* 1729 */     int c1 = 0; int c2 = 0; int c3 = 0; int c4 = 0; int unicodeSize = 6;
/* 1730 */     this.currentPosition += 1;
/* 1731 */     if (this.currentPosition < this.eofPosition) {
/* 1732 */       while (this.source[this.currentPosition] == 'u') {
/* 1733 */         this.currentPosition += 1;
/* 1734 */         if (this.currentPosition >= this.eofPosition) {
/* 1735 */           this.currentPosition -= 1;
/* 1736 */           throw new InvalidInputException("Invalid_Unicode_Escape");
/*      */         }
/* 1738 */         unicodeSize++;
/*      */       }
/*      */     } else {
/* 1741 */       this.currentPosition -= 1;
/* 1742 */       throw new InvalidInputException("Invalid_Unicode_Escape");
/*      */     }
/*      */ 
/* 1745 */     if (this.currentPosition + 4 > this.eofPosition) {
/* 1746 */       this.currentPosition += this.eofPosition - this.currentPosition;
/* 1747 */       throw new InvalidInputException("Invalid_Unicode_Escape");
/*      */     }
/* 1749 */     if (((c1 = ScannerHelper.getNumericValue(this.source[(this.currentPosition++)])) > 15) || 
/* 1750 */       (c1 < 0) || 
/* 1751 */       ((c2 = ScannerHelper.getNumericValue(this.source[(this.currentPosition++)])) > 15) || 
/* 1752 */       (c2 < 0) || 
/* 1753 */       ((c3 = ScannerHelper.getNumericValue(this.source[(this.currentPosition++)])) > 15) || 
/* 1754 */       (c3 < 0) || 
/* 1755 */       ((c4 = ScannerHelper.getNumericValue(this.source[(this.currentPosition++)])) > 15) || 
/* 1756 */       (c4 < 0)) {
/* 1757 */       throw new InvalidInputException("Invalid_Unicode_Escape");
/*      */     }
/* 1759 */     this.currentCharacter = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
/*      */ 
/* 1761 */     if (this.withoutUnicodePtr == 0)
/*      */     {
/* 1763 */       unicodeInitializeBuffer(this.currentPosition - unicodeSize - this.startPosition);
/*      */     }
/*      */ 
/* 1766 */     unicodeStore();
/* 1767 */     this.unicodeAsBackSlash = (this.currentCharacter == '\\');
/*      */   }
/*      */   public NLSTag[] getNLSTags() {
/* 1770 */     int length = this.nlsTagsPtr;
/* 1771 */     if (length != 0) {
/* 1772 */       NLSTag[] result = new NLSTag[length];
/* 1773 */       System.arraycopy(this.nlsTags, 0, result, 0, length);
/* 1774 */       this.nlsTagsPtr = 0;
/* 1775 */       return result;
/*      */     }
/* 1777 */     return null;
/*      */   }
/*      */   public char[] getSource() {
/* 1780 */     return this.source;
/*      */   }
/*      */   protected boolean isFirstTag() {
/* 1783 */     return true;
/*      */   }
/*      */ 
/*      */   public final void jumpOverMethodBody() {
/* 1787 */     this.wasAcr = false;
/* 1788 */     int found = 1;
/*      */     try {
/*      */       while (true) { this.withoutUnicodePtr = 0;
/*      */         boolean isWhiteSpace;
/*      */         do {
/* 1795 */           this.startPosition = this.currentPosition;
/*      */           boolean isWhiteSpace;
/* 1796 */           if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1797 */             (this.source[this.currentPosition] == 'u')) {
/* 1798 */             isWhiteSpace = jumpOverUnicodeWhiteSpace();
/*      */           } else {
/* 1800 */             if ((this.recordLineSeparator) && (
/* 1801 */               (this.currentCharacter == '\r') || (this.currentCharacter == '\n'))) {
/* 1802 */               pushLineSeparator();
/*      */             }
/* 1804 */             isWhiteSpace = CharOperation.isWhitespace(this.currentCharacter);
/*      */           }
/*      */         }
/* 1794 */         while (
/* 1806 */           isWhiteSpace);
/*      */ 
/* 1809 */         switch (this.currentCharacter) {
/*      */         case '{':
/* 1811 */           found++;
/* 1812 */           break;
/*      */         case '}':
/* 1814 */           found--;
/* 1815 */           if (found != 0) continue;
/* 1816 */           return;
/*      */         case '\'':
/* 1821 */           boolean test = getNextChar('\\');
/* 1822 */           if (test)
/*      */             try {
/* 1824 */               if (this.unicodeAsBackSlash)
/*      */               {
/* 1826 */                 this.unicodeAsBackSlash = false;
/* 1827 */                 if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && (this.source[this.currentPosition] == 'u')) {
/* 1828 */                   getNextUnicodeChar();
/*      */                 }
/* 1830 */                 else if (this.withoutUnicodePtr != 0)
/* 1831 */                   unicodeStore();
/*      */               }
/*      */               else
/*      */               {
/* 1835 */                 this.currentCharacter = this.source[(this.currentPosition++)];
/*      */               }
/* 1837 */               scanEscapeCharacter();
/*      */             }
/*      */             catch (InvalidInputException localInvalidInputException1) {
/*      */             }
/*      */           else {
/*      */             try {
/* 1843 */               this.unicodeAsBackSlash = false;
/* 1844 */               if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1845 */                 (this.source[this.currentPosition] == 'u')) {
/* 1846 */                 getNextUnicodeChar();
/*      */               }
/* 1848 */               else if (this.withoutUnicodePtr != 0) {
/* 1849 */                 unicodeStore();
/*      */               }
/*      */             }
/*      */             catch (InvalidInputException localInvalidInputException2)
/*      */             {
/*      */             }
/*      */           }
/* 1856 */           getNextChar('\'');
/* 1857 */           break;
/*      */         case '"':
/*      */           try
/*      */           {
/*      */             try {
/* 1862 */               this.unicodeAsBackSlash = false;
/* 1863 */               if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1864 */                 (this.source[this.currentPosition] == 'u')) {
/* 1865 */                 getNextUnicodeChar();
/*      */               }
/* 1867 */               else if (this.withoutUnicodePtr != 0)
/* 1868 */                 unicodeStore();
/*      */             }
/*      */             catch (InvalidInputException localInvalidInputException3)
/*      */             {
/*      */             }
/*      */             do
/*      */             {
/* 1875 */               if (this.currentCharacter == '\r') {
/* 1876 */                 if (this.source[this.currentPosition] != '\n') break; this.currentPosition += 1;
/* 1877 */                 break;
/*      */               }
/* 1879 */               if (this.currentCharacter == '\n') {
/*      */                 break;
/*      */               }
/* 1882 */               if (this.currentCharacter == '\\')
/*      */                 try {
/* 1884 */                   if (this.unicodeAsBackSlash)
/*      */                   {
/* 1886 */                     this.unicodeAsBackSlash = false;
/* 1887 */                     if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && (this.source[this.currentPosition] == 'u')) {
/* 1888 */                       getNextUnicodeChar();
/*      */                     }
/* 1890 */                     else if (this.withoutUnicodePtr != 0)
/* 1891 */                       unicodeStore();
/*      */                   }
/*      */                   else
/*      */                   {
/* 1895 */                     this.currentCharacter = this.source[(this.currentPosition++)];
/*      */                   }
/* 1897 */                   scanEscapeCharacter();
/*      */                 }
/*      */                 catch (InvalidInputException localInvalidInputException4)
/*      */                 {
/*      */                 }
/*      */               try {
/* 1903 */                 this.unicodeAsBackSlash = false;
/* 1904 */                 if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1905 */                   (this.source[this.currentPosition] == 'u')) {
/* 1906 */                   getNextUnicodeChar();
/*      */                 }
/* 1908 */                 else if (this.withoutUnicodePtr != 0)
/* 1909 */                   unicodeStore();
/*      */               }
/*      */               catch (InvalidInputException localInvalidInputException5)
/*      */               {
/*      */               }
/*      */             }
/* 1874 */             while (this.currentCharacter != '"');
/*      */           }
/*      */           catch (IndexOutOfBoundsException localIndexOutOfBoundsException1)
/*      */           {
/* 1917 */             return;
/*      */           }
/*      */         case '/':
/*      */           int test;
/* 1923 */           if ((test = getNextChar('/', '*')) == 0) {
/*      */             try {
/* 1925 */               this.lastCommentLinePosition = this.currentPosition;
/*      */ 
/* 1927 */               if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1928 */                 (this.source[this.currentPosition] == 'u')) {
/* 1929 */                 getNextUnicodeChar();
/*      */               }
/*      */ 
/* 1932 */               if ((this.currentCharacter == '\\') && 
/* 1933 */                 (this.source[this.currentPosition] == '\\')) {
/* 1934 */                 this.currentPosition += 1;
/*      */               }
/* 1936 */               boolean isUnicode = false;
/* 1937 */               while ((this.currentCharacter != '\r') && (this.currentCharacter != '\n')) {
/* 1938 */                 this.lastCommentLinePosition = this.currentPosition;
/*      */ 
/* 1940 */                 isUnicode = false;
/* 1941 */                 if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 1942 */                   (this.source[this.currentPosition] == 'u')) {
/* 1943 */                   isUnicode = true;
/* 1944 */                   getNextUnicodeChar();
/*      */                 }
/*      */ 
/* 1947 */                 if ((this.currentCharacter != '\\') || 
/* 1948 */                   (this.source[this.currentPosition] != '\\')) continue;
/* 1949 */                 this.currentPosition += 1;
/*      */               }
/*      */ 
/* 1955 */               if ((this.currentCharacter == '\r') && 
/* 1956 */                 (this.eofPosition > this.currentPosition)) {
/* 1957 */                 if (this.source[this.currentPosition] == '\n') {
/* 1958 */                   this.currentPosition += 1;
/* 1959 */                   this.currentCharacter = '\n';
/* 1960 */                 } else if ((this.source[this.currentPosition] == '\\') && 
/* 1961 */                   (this.source[(this.currentPosition + 1)] == 'u')) {
/* 1962 */                   isUnicode = true;
/* 1963 */                   getNextUnicodeChar();
/*      */                 }
/*      */               }
/* 1966 */               recordComment(1001);
/* 1967 */               if ((!this.recordLineSeparator) || (
/* 1968 */                 (this.currentCharacter != '\r') && (this.currentCharacter != '\n'))) continue;
/* 1969 */               if ((this.checkNonExternalizedStringLiterals) && 
/* 1970 */                 (this.lastPosition < this.currentPosition)) {
/* 1971 */                 parseTags();
/*      */               }
/* 1973 */               if (!this.recordLineSeparator) continue;
/* 1974 */               if (isUnicode) {
/* 1975 */                 pushUnicodeLineSeparator(); continue;
/*      */               }
/* 1977 */               pushLineSeparator();
/*      */             }
/*      */             catch (IndexOutOfBoundsException localIndexOutOfBoundsException2)
/*      */             {
/* 1983 */               this.currentPosition -= 1;
/* 1984 */               recordComment(1001);
/* 1985 */               if ((this.checkNonExternalizedStringLiterals) && 
/* 1986 */                 (this.lastPosition < this.currentPosition))
/* 1987 */                 parseTags();
/*      */             }
/* 1989 */             if (this.tokenizeComments) continue;
/* 1990 */             this.currentPosition += 1;
/*      */ 
/* 1993 */             continue;
/*      */           }
/* 1995 */           if (test <= 0) continue;
/* 1996 */           boolean isJavadoc = false;
/*      */           try {
/* 1998 */             boolean star = false;
/*      */ 
/* 2000 */             boolean isUnicode = false;
/*      */ 
/* 2002 */             this.unicodeAsBackSlash = false;
/* 2003 */             if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 2004 */               (this.source[this.currentPosition] == 'u')) {
/* 2005 */               getNextUnicodeChar();
/* 2006 */               isUnicode = true;
/*      */             } else {
/* 2008 */               isUnicode = false;
/* 2009 */               if (this.withoutUnicodePtr != 0) {
/* 2010 */                 unicodeStore();
/*      */               }
/*      */             }
/*      */ 
/* 2014 */             if (this.currentCharacter == '*') {
/* 2015 */               isJavadoc = true;
/* 2016 */               star = true;
/*      */             }
/* 2018 */             if (((this.currentCharacter == '\r') || (this.currentCharacter == '\n')) && 
/* 2019 */               (this.recordLineSeparator)) {
/* 2020 */               if (isUnicode)
/* 2021 */                 pushUnicodeLineSeparator();
/*      */               else {
/* 2023 */                 pushLineSeparator();
/*      */               }
/*      */             }
/*      */ 
/* 2027 */             isUnicode = false;
/* 2028 */             int previous = this.currentPosition;
/* 2029 */             if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 2030 */               (this.source[this.currentPosition] == 'u')) {
/* 2031 */               getNextUnicodeChar();
/* 2032 */               isUnicode = true;
/*      */             } else {
/* 2034 */               isUnicode = false;
/*      */             }
/*      */ 
/* 2037 */             if ((this.currentCharacter == '\\') && 
/* 2038 */               (this.source[this.currentPosition] == '\\')) {
/* 2039 */               this.currentPosition += 1;
/*      */             }
/*      */ 
/* 2042 */             if (this.currentCharacter == '/') {
/* 2043 */               isJavadoc = false;
/*      */             }
/*      */ 
/* 2046 */             int firstTag = 0;
/* 2047 */             while ((this.currentCharacter != '/') || (!star)) {
/* 2048 */               if (((this.currentCharacter == '\r') || (this.currentCharacter == '\n')) && 
/* 2049 */                 (this.recordLineSeparator)) {
/* 2050 */                 if (isUnicode)
/* 2051 */                   pushUnicodeLineSeparator();
/*      */                 else {
/* 2053 */                   pushLineSeparator();
/*      */                 }
/*      */               }
/*      */ 
/* 2057 */               switch (this.currentCharacter) {
/*      */               case '*':
/* 2059 */                 star = true;
/* 2060 */                 break;
/*      */               case '@':
/* 2062 */                 if ((firstTag != 0) || (!isFirstTag())) break;
/* 2063 */                 firstTag = previous;
/*      */               }
/*      */ 
/* 2067 */               star = false;
/*      */ 
/* 2070 */               previous = this.currentPosition;
/* 2071 */               if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 2072 */                 (this.source[this.currentPosition] == 'u')) {
/* 2073 */                 getNextUnicodeChar();
/* 2074 */                 isUnicode = true;
/*      */               } else {
/* 2076 */                 isUnicode = false;
/*      */               }
/*      */ 
/* 2079 */               if ((this.currentCharacter != '\\') || 
/* 2080 */                 (this.source[this.currentPosition] != '\\')) continue;
/* 2081 */               this.currentPosition += 1;
/*      */             }
/*      */ 
/* 2084 */             recordComment(isJavadoc ? 1003 : 1002);
/* 2085 */             this.commentTagStarts[this.commentPtr] = firstTag;
/*      */           } catch (IndexOutOfBoundsException localIndexOutOfBoundsException3) {
/* 2087 */             return;
/*      */           }
/*      */ 
/*      */         default:
/*      */           try
/*      */           {
/* 2096 */             char c = this.currentCharacter;
/* 2097 */             if (c < '') {
/* 2098 */               if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0) {
/* 2099 */                 scanIdentifierOrKeyword();
/* 2100 */                 continue;
/* 2101 */               }if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x4) == 0) continue;
/* 2102 */               scanNumber(false);
/* 2103 */               continue;
/*      */             }
/*      */             boolean isJavaIdStart;
/*      */             boolean isJavaIdStart;
/* 2109 */             if ((c >= 55296) && (c <= 56319)) {
/* 2110 */               if (this.complianceLevel < 3211264L) {
/* 2111 */                 throw new InvalidInputException("Invalid_Unicode_Escape");
/*      */               }
/*      */ 
/* 2114 */               char low = (char)getNextChar();
/* 2115 */               if ((low < 56320) || (low > 57343))
/*      */               {
/*      */                 continue;
/*      */               }
/* 2119 */               isJavaIdStart = ScannerHelper.isJavaIdentifierStart(c, low); } else {
/* 2120 */               if ((c >= 56320) && (c <= 57343))
/*      */               {
/*      */                 continue;
/*      */               }
/* 2124 */               isJavaIdStart = Character.isJavaIdentifierStart(c);
/*      */             }
/* 2126 */             if (!isJavaIdStart) continue;
/* 2127 */             scanIdentifierOrKeyword();
/*      */           }
/*      */           catch (InvalidInputException localInvalidInputException6)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (IndexOutOfBoundsException localIndexOutOfBoundsException4)
/*      */     {
/*      */     }
/*      */     catch (InvalidInputException localInvalidInputException7)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public final boolean jumpOverUnicodeWhiteSpace()
/*      */     throws InvalidInputException
/*      */   {
/* 2154 */     this.wasAcr = false;
/* 2155 */     getNextUnicodeChar();
/* 2156 */     return CharOperation.isWhitespace(this.currentCharacter);
/*      */   }
/*      */ 
/*      */   final char[] optimizedCurrentTokenSource1()
/*      */   {
/* 2163 */     char charOne = this.source[this.startPosition];
/* 2164 */     switch (charOne) {
/*      */     case 'a':
/* 2166 */       return charArray_a;
/*      */     case 'b':
/* 2168 */       return charArray_b;
/*      */     case 'c':
/* 2170 */       return charArray_c;
/*      */     case 'd':
/* 2172 */       return charArray_d;
/*      */     case 'e':
/* 2174 */       return charArray_e;
/*      */     case 'f':
/* 2176 */       return charArray_f;
/*      */     case 'g':
/* 2178 */       return charArray_g;
/*      */     case 'h':
/* 2180 */       return charArray_h;
/*      */     case 'i':
/* 2182 */       return charArray_i;
/*      */     case 'j':
/* 2184 */       return charArray_j;
/*      */     case 'k':
/* 2186 */       return charArray_k;
/*      */     case 'l':
/* 2188 */       return charArray_l;
/*      */     case 'm':
/* 2190 */       return charArray_m;
/*      */     case 'n':
/* 2192 */       return charArray_n;
/*      */     case 'o':
/* 2194 */       return charArray_o;
/*      */     case 'p':
/* 2196 */       return charArray_p;
/*      */     case 'q':
/* 2198 */       return charArray_q;
/*      */     case 'r':
/* 2200 */       return charArray_r;
/*      */     case 's':
/* 2202 */       return charArray_s;
/*      */     case 't':
/* 2204 */       return charArray_t;
/*      */     case 'u':
/* 2206 */       return charArray_u;
/*      */     case 'v':
/* 2208 */       return charArray_v;
/*      */     case 'w':
/* 2210 */       return charArray_w;
/*      */     case 'x':
/* 2212 */       return charArray_x;
/*      */     case 'y':
/* 2214 */       return charArray_y;
/*      */     case 'z':
/* 2216 */       return charArray_z;
/*      */     }
/* 2218 */     return new char[] { charOne };
/*      */   }
/*      */ 
/*      */   final char[] optimizedCurrentTokenSource2()
/*      */   {
/* 2224 */     char[] src = this.source;
/* 2225 */     int start = this.startPosition;
/*      */     char c0;
/*      */     char c1;
/* 2227 */     int hash = (((c0 = src[start]) << '\006') + (c1 = src[(start + 1)])) % 30;
/* 2228 */     char[][] table = this.charArray_length[0][hash];
/* 2229 */     int i = this.newEntry2;
/*      */     do {
/* 2231 */       char[] charArray = table[i];
/* 2232 */       if ((c0 == charArray[0]) && (c1 == charArray[1]))
/* 2233 */         return charArray;
/* 2230 */       i++; } while (i < 6);
/*      */ 
/* 2236 */     i = -1;
/* 2237 */     int max = this.newEntry2;
/*      */     do {
/* 2239 */       char[] charArray = table[i];
/* 2240 */       if ((c0 == charArray[0]) && (c1 == charArray[1]))
/* 2241 */         return charArray;
/* 2238 */       i++; } while (i <= max);
/*      */ 
/* 2244 */     max++; if (max >= 6) max = 0;
/*      */     char[] r;
/* 2246 */     System.arraycopy(src, start, r = new char[2], 0, 2);
/*      */     int tmp169_167 = max; this.newEntry2 = tmp169_167; return table[tmp169_167] =  = r;
/*      */   }
/*      */ 
/*      */   final char[] optimizedCurrentTokenSource3()
/*      */   {
/* 2253 */     char[] src = this.source;
/* 2254 */     int start = this.startPosition;
/* 2255 */     char c1 = src[(start + 1)];
/*      */     char c0;
/*      */     char c2;
/* 2256 */     int hash = (((c0 = src[start]) << '\006') + (c2 = src[(start + 2)])) % 30;
/*      */ 
/* 2258 */     char[][] table = this.charArray_length[1][hash];
/* 2259 */     int i = this.newEntry3;
/*      */     do {
/* 2261 */       char[] charArray = table[i];
/* 2262 */       if ((c0 == charArray[0]) && (c1 == charArray[1]) && (c2 == charArray[2]))
/* 2263 */         return charArray;
/* 2260 */       i++; } while (i < 6);
/*      */ 
/* 2266 */     i = -1;
/* 2267 */     int max = this.newEntry3;
/*      */     do {
/* 2269 */       char[] charArray = table[i];
/* 2270 */       if ((c0 == charArray[0]) && (c1 == charArray[1]) && (c2 == charArray[2]))
/* 2271 */         return charArray;
/* 2268 */       i++; } while (i <= max);
/*      */ 
/* 2274 */     max++; if (max >= 6) max = 0;
/*      */     char[] r;
/* 2276 */     System.arraycopy(src, start, r = new char[3], 0, 3);
/*      */     int tmp194_192 = max; this.newEntry3 = tmp194_192; return table[tmp194_192] =  = r;
/*      */   }
/*      */ 
/*      */   final char[] optimizedCurrentTokenSource4()
/*      */   {
/* 2283 */     char[] src = this.source;
/* 2284 */     int start = this.startPosition;
/* 2285 */     char c1 = src[(start + 1)]; char c3 = src[(start + 3)];
/*      */     char c0;
/*      */     char c2;
/* 2286 */     int hash = (((c0 = src[start]) << '\006') + (c2 = src[(start + 2)])) % 30;
/*      */ 
/* 2288 */     char[][] table = this.charArray_length[2][hash];
/* 2289 */     int i = this.newEntry4;
/*      */     do {
/* 2291 */       char[] charArray = table[i];
/* 2292 */       if ((c0 == charArray[0]) && 
/* 2293 */         (c1 == charArray[1]) && 
/* 2294 */         (c2 == charArray[2]) && 
/* 2295 */         (c3 == charArray[3]))
/* 2296 */         return charArray;
/* 2290 */       i++; } while (i < 6);
/*      */ 
/* 2299 */     i = -1;
/* 2300 */     int max = this.newEntry4;
/*      */     do {
/* 2302 */       char[] charArray = table[i];
/* 2303 */       if ((c0 == charArray[0]) && 
/* 2304 */         (c1 == charArray[1]) && 
/* 2305 */         (c2 == charArray[2]) && 
/* 2306 */         (c3 == charArray[3]))
/* 2307 */         return charArray;
/* 2301 */       i++; } while (i <= max);
/*      */ 
/* 2310 */     max++; if (max >= 6) max = 0;
/*      */     char[] r;
/* 2312 */     System.arraycopy(src, start, r = new char[4], 0, 4);
/*      */     int tmp219_217 = max; this.newEntry4 = tmp219_217; return table[tmp219_217] =  = r;
/*      */   }
/*      */ 
/*      */   final char[] optimizedCurrentTokenSource5()
/*      */   {
/* 2319 */     char[] src = this.source;
/* 2320 */     int start = this.startPosition;
/* 2321 */     char c1 = src[(start + 1)]; char c3 = src[(start + 3)];
/*      */     char c0;
/*      */     char c2;
/*      */     char c4;
/* 2322 */     int hash = (((c0 = src[start]) << '\f') + ((c2 = src[(start + 2)]) << '\006') + (c4 = src[(start + 4)])) % 30;
/*      */ 
/* 2324 */     char[][] table = this.charArray_length[3][hash];
/* 2325 */     int i = this.newEntry5;
/*      */     do {
/* 2327 */       char[] charArray = table[i];
/* 2328 */       if ((c0 == charArray[0]) && 
/* 2329 */         (c1 == charArray[1]) && 
/* 2330 */         (c2 == charArray[2]) && 
/* 2331 */         (c3 == charArray[3]) && 
/* 2332 */         (c4 == charArray[4]))
/* 2333 */         return charArray;
/* 2326 */       i++; } while (i < 6);
/*      */ 
/* 2336 */     i = -1;
/* 2337 */     int max = this.newEntry5;
/*      */     do {
/* 2339 */       char[] charArray = table[i];
/* 2340 */       if ((c0 == charArray[0]) && 
/* 2341 */         (c1 == charArray[1]) && 
/* 2342 */         (c2 == charArray[2]) && 
/* 2343 */         (c3 == charArray[3]) && 
/* 2344 */         (c4 == charArray[4]))
/* 2345 */         return charArray;
/* 2338 */       i++; } while (i <= max);
/*      */ 
/* 2348 */     max++; if (max >= 6) max = 0;
/*      */     char[] r;
/* 2350 */     System.arraycopy(src, start, r = new char[5], 0, 5);
/*      */     int tmp249_247 = max; this.newEntry5 = tmp249_247; return table[tmp249_247] =  = r;
/*      */   }
/*      */ 
/*      */   final char[] optimizedCurrentTokenSource6()
/*      */   {
/* 2357 */     char[] src = this.source;
/* 2358 */     int start = this.startPosition;
/* 2359 */     char c1 = src[(start + 1)]; char c3 = src[(start + 3)]; char c5 = src[(start + 5)];
/*      */     char c0;
/*      */     char c2;
/*      */     char c4;
/* 2360 */     int hash = (((c0 = src[start]) << '\f') + ((c2 = src[(start + 2)]) << '\006') + (c4 = src[(start + 4)])) % 30;
/*      */ 
/* 2362 */     char[][] table = this.charArray_length[4][hash];
/* 2363 */     int i = this.newEntry6;
/*      */     do {
/* 2365 */       char[] charArray = table[i];
/* 2366 */       if ((c0 == charArray[0]) && 
/* 2367 */         (c1 == charArray[1]) && 
/* 2368 */         (c2 == charArray[2]) && 
/* 2369 */         (c3 == charArray[3]) && 
/* 2370 */         (c4 == charArray[4]) && 
/* 2371 */         (c5 == charArray[5]))
/* 2372 */         return charArray;
/* 2364 */       i++; } while (i < 6);
/*      */ 
/* 2375 */     i = -1;
/* 2376 */     int max = this.newEntry6;
/*      */     do {
/* 2378 */       char[] charArray = table[i];
/* 2379 */       if ((c0 == charArray[0]) && 
/* 2380 */         (c1 == charArray[1]) && 
/* 2381 */         (c2 == charArray[2]) && 
/* 2382 */         (c3 == charArray[3]) && 
/* 2383 */         (c4 == charArray[4]) && 
/* 2384 */         (c5 == charArray[5]))
/* 2385 */         return charArray;
/* 2377 */       i++; } while (i <= max);
/*      */ 
/* 2388 */     max++; if (max >= 6) max = 0;
/*      */     char[] r;
/* 2390 */     System.arraycopy(src, start, r = new char[6], 0, 6);
/*      */     int tmp276_274 = max; this.newEntry6 = tmp276_274; return table[tmp276_274] =  = r;
/*      */   }
/*      */ 
/*      */   private void parseTags() {
/* 2396 */     int position = 0;
/* 2397 */     int currentStartPosition = this.startPosition;
/* 2398 */     int currentLinePtr = this.linePtr;
/* 2399 */     if (currentLinePtr >= 0) {
/* 2400 */       position = this.lineEnds[currentLinePtr] + 1;
/*      */     }
/* 2402 */     while (ScannerHelper.isWhitespace(this.source[position])) {
/* 2403 */       position++;
/*      */     }
/* 2405 */     if (currentStartPosition == position)
/*      */     {
/* 2407 */       return;
/*      */     }
/* 2409 */     char[] s = (char[])null;
/* 2410 */     int sourceEnd = this.currentPosition;
/* 2411 */     int sourceStart = currentStartPosition;
/* 2412 */     int sourceDelta = 0;
/* 2413 */     if (this.withoutUnicodePtr != 0)
/*      */     {
/* 2415 */       System.arraycopy(
/* 2416 */         this.withoutUnicodeBuffer, 
/* 2417 */         1, 
/* 2418 */         s = new char[this.withoutUnicodePtr], 
/* 2419 */         0, 
/* 2420 */         this.withoutUnicodePtr);
/* 2421 */       sourceEnd = this.withoutUnicodePtr;
/* 2422 */       sourceStart = 1;
/* 2423 */       sourceDelta = currentStartPosition;
/*      */     } else {
/* 2425 */       s = this.source;
/*      */     }
/* 2427 */     int pos = CharOperation.indexOf(TAG_PREFIX, s, true, sourceStart, sourceEnd);
/* 2428 */     if (pos != -1) {
/* 2429 */       if (this.nlsTags == null) {
/* 2430 */         this.nlsTags = new NLSTag[10];
/* 2431 */         this.nlsTagsPtr = 0;
/*      */       }
/* 2433 */       while (pos != -1) {
/* 2434 */         int start = pos + TAG_PREFIX_LENGTH;
/* 2435 */         int end = CharOperation.indexOf('$', s, start, sourceEnd);
/* 2436 */         if (end != -1) {
/* 2437 */           NLSTag currentTag = null;
/* 2438 */           int currentLine = currentLinePtr + 1;
/*      */           try {
/* 2440 */             currentTag = new NLSTag(pos + sourceDelta, end + sourceDelta, currentLine, extractInt(s, start, end));
/*      */           } catch (NumberFormatException localNumberFormatException) {
/* 2442 */             currentTag = new NLSTag(pos + sourceDelta, end + sourceDelta, currentLine, -1);
/*      */           }
/* 2444 */           if (this.nlsTagsPtr == this.nlsTags.length)
/*      */           {
/* 2446 */             System.arraycopy(this.nlsTags, 0, this.nlsTags = new NLSTag[this.nlsTagsPtr + 10], 0, this.nlsTagsPtr);
/*      */           }
/* 2448 */           this.nlsTags[(this.nlsTagsPtr++)] = currentTag;
/*      */         } else {
/* 2450 */           end = start;
/*      */         }
/* 2452 */         pos = CharOperation.indexOf(TAG_PREFIX, s, true, end, sourceEnd);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int extractInt(char[] array, int start, int end) {
/* 2457 */     int value = 0;
/* 2458 */     for (int i = start; i < end; i++) {
/* 2459 */       char currentChar = array[i];
/* 2460 */       int digit = 0;
/* 2461 */       switch (currentChar) {
/*      */       case '0':
/* 2463 */         digit = 0;
/* 2464 */         break;
/*      */       case '1':
/* 2466 */         digit = 1;
/* 2467 */         break;
/*      */       case '2':
/* 2469 */         digit = 2;
/* 2470 */         break;
/*      */       case '3':
/* 2472 */         digit = 3;
/* 2473 */         break;
/*      */       case '4':
/* 2475 */         digit = 4;
/* 2476 */         break;
/*      */       case '5':
/* 2478 */         digit = 5;
/* 2479 */         break;
/*      */       case '6':
/* 2481 */         digit = 6;
/* 2482 */         break;
/*      */       case '7':
/* 2484 */         digit = 7;
/* 2485 */         break;
/*      */       case '8':
/* 2487 */         digit = 8;
/* 2488 */         break;
/*      */       case '9':
/* 2490 */         digit = 9;
/* 2491 */         break;
/*      */       default:
/* 2493 */         throw new NumberFormatException();
/*      */       }
/* 2495 */       value *= 10;
/* 2496 */       if (digit < 0) throw new NumberFormatException();
/* 2497 */       value += digit;
/*      */     }
/* 2499 */     return value;
/*      */   }
/*      */ 
/*      */   public final void pushLineSeparator()
/*      */   {
/* 2506 */     if (this.currentCharacter == '\r') {
/* 2507 */       int separatorPos = this.currentPosition - 1;
/* 2508 */       if ((this.linePtr >= 0) && (this.lineEnds[this.linePtr] >= separatorPos)) return;
/* 2509 */       int length = this.lineEnds.length;
/* 2510 */       if (++this.linePtr >= length)
/* 2511 */         System.arraycopy(this.lineEnds, 0, this.lineEnds = new int[length + 250], 0, length);
/* 2512 */       this.lineEnds[this.linePtr] = separatorPos;
/*      */       try
/*      */       {
/* 2515 */         if (this.source[this.currentPosition] == '\n')
/*      */         {
/* 2517 */           this.lineEnds[this.linePtr] = this.currentPosition;
/* 2518 */           this.currentPosition += 1;
/* 2519 */           this.wasAcr = false; return;
/*      */         }
/* 2521 */         this.wasAcr = true;
/*      */       }
/*      */       catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/* 2524 */         this.wasAcr = true;
/*      */       }
/*      */ 
/*      */     }
/* 2528 */     else if (this.currentCharacter == '\n') {
/* 2529 */       if ((this.wasAcr) && (this.lineEnds[this.linePtr] == this.currentPosition - 2))
/*      */       {
/* 2531 */         this.lineEnds[this.linePtr] = (this.currentPosition - 1);
/*      */       } else {
/* 2533 */         int separatorPos = this.currentPosition - 1;
/* 2534 */         if ((this.linePtr >= 0) && (this.lineEnds[this.linePtr] >= separatorPos)) return;
/* 2535 */         int length = this.lineEnds.length;
/* 2536 */         if (++this.linePtr >= length)
/* 2537 */           System.arraycopy(this.lineEnds, 0, this.lineEnds = new int[length + 250], 0, length);
/* 2538 */         this.lineEnds[this.linePtr] = separatorPos;
/*      */       }
/* 2540 */       this.wasAcr = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void pushUnicodeLineSeparator()
/*      */   {
/* 2546 */     if (this.currentCharacter == '\r') {
/* 2547 */       if (this.source[this.currentPosition] == '\n')
/* 2548 */         this.wasAcr = false;
/*      */       else {
/* 2550 */         this.wasAcr = true;
/*      */       }
/*      */ 
/*      */     }
/* 2554 */     else if (this.currentCharacter == '\n')
/* 2555 */       this.wasAcr = false;
/*      */   }
/*      */ 
/*      */   public void recordComment(int token)
/*      */   {
/* 2562 */     int commentStart = this.startPosition;
/* 2563 */     int stopPosition = this.currentPosition;
/* 2564 */     switch (token)
/*      */     {
/*      */     case 1001:
/* 2567 */       commentStart = -this.startPosition;
/* 2568 */       stopPosition = -this.lastCommentLinePosition;
/* 2569 */       break;
/*      */     case 1002:
/* 2572 */       stopPosition = -this.currentPosition;
/*      */     }
/*      */ 
/* 2577 */     int length = this.commentStops.length;
/* 2578 */     if (++this.commentPtr >= length) {
/* 2579 */       int newLength = length + 300;
/* 2580 */       System.arraycopy(this.commentStops, 0, this.commentStops = new int[newLength], 0, length);
/* 2581 */       System.arraycopy(this.commentStarts, 0, this.commentStarts = new int[newLength], 0, length);
/* 2582 */       System.arraycopy(this.commentTagStarts, 0, this.commentTagStarts = new int[newLength], 0, length);
/*      */     }
/* 2584 */     this.commentStops[this.commentPtr] = stopPosition;
/* 2585 */     this.commentStarts[this.commentPtr] = commentStart;
/*      */   }
/*      */ 
/*      */   public void resetTo(int begin, int end)
/*      */   {
/* 2598 */     this.diet = false;
/* 2599 */     this.initialPosition = (this.startPosition = this.currentPosition = begin);
/* 2600 */     if ((this.source != null) && (this.source.length < end))
/* 2601 */       this.eofPosition = this.source.length;
/*      */     else {
/* 2603 */       this.eofPosition = (end < 2147483647 ? end + 1 : end);
/*      */     }
/* 2605 */     this.commentPtr = -1;
/* 2606 */     this.foundTaskCount = 0;
/*      */   }
/*      */ 
/*      */   public final void scanEscapeCharacter()
/*      */     throws InvalidInputException
/*      */   {
/* 2612 */     switch (this.currentCharacter) {
/*      */     case 'b':
/* 2614 */       this.currentCharacter = '\b';
/* 2615 */       break;
/*      */     case 't':
/* 2617 */       this.currentCharacter = '\t';
/* 2618 */       break;
/*      */     case 'n':
/* 2620 */       this.currentCharacter = '\n';
/* 2621 */       break;
/*      */     case 'f':
/* 2623 */       this.currentCharacter = '\f';
/* 2624 */       break;
/*      */     case 'r':
/* 2626 */       this.currentCharacter = '\r';
/* 2627 */       break;
/*      */     case '"':
/* 2629 */       this.currentCharacter = '"';
/* 2630 */       break;
/*      */     case '\'':
/* 2632 */       this.currentCharacter = '\'';
/* 2633 */       break;
/*      */     case '\\':
/* 2635 */       this.currentCharacter = '\\';
/* 2636 */       break;
/*      */     default:
/* 2643 */       int number = ScannerHelper.getNumericValue(this.currentCharacter);
/* 2644 */       if ((number >= 0) && (number <= 7)) {
/* 2645 */         boolean zeroToThreeNot = number > 3;
/* 2646 */         if (ScannerHelper.isDigit(this.currentCharacter = this.source[(this.currentPosition++)])) {
/* 2647 */           int digit = ScannerHelper.getNumericValue(this.currentCharacter);
/* 2648 */           if ((digit >= 0) && (digit <= 7)) {
/* 2649 */             number = number * 8 + digit;
/* 2650 */             if (ScannerHelper.isDigit(this.currentCharacter = this.source[(this.currentPosition++)])) {
/* 2651 */               if (zeroToThreeNot) {
/* 2652 */                 this.currentPosition -= 1;
/*      */               } else {
/* 2654 */                 digit = ScannerHelper.getNumericValue(this.currentCharacter);
/* 2655 */                 if ((digit >= 0) && (digit <= 7))
/* 2656 */                   number = number * 8 + digit;
/*      */                 else
/* 2658 */                   this.currentPosition -= 1;
/*      */               }
/*      */             }
/*      */             else
/* 2662 */               this.currentPosition -= 1;
/*      */           }
/*      */           else {
/* 2665 */             this.currentPosition -= 1;
/*      */           }
/*      */         } else {
/* 2668 */           this.currentPosition -= 1;
/*      */         }
/* 2670 */         if (number > 255)
/* 2671 */           throw new InvalidInputException("Invalid_Escape");
/* 2672 */         this.currentCharacter = (char)number;
/*      */       } else {
/* 2674 */         throw new InvalidInputException("Invalid_Escape");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int scanIdentifierOrKeywordWithBoundCheck()
/*      */   {
/* 2684 */     this.useAssertAsAnIndentifier = false;
/* 2685 */     this.useEnumAsAnIndentifier = false;
/*      */ 
/* 2687 */     char[] src = this.source;
/*      */ 
/* 2690 */     int srcLength = this.eofPosition;
/*      */     while (true)
/*      */     {
/*      */       int pos;
/* 2692 */       if ((pos = this.currentPosition) >= srcLength) break label127;
/* 2694 */       char c = src[pos];
/* 2695 */       if (c >= '') break;
/* 2696 */       if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 
/* 2697 */         0x3C) != 0) {
/* 2698 */         if (this.withoutUnicodePtr != 0) {
/* 2699 */           this.currentCharacter = c;
/* 2700 */           unicodeStore();
/*      */         }
/* 2702 */         this.currentPosition += 1; continue;
/* 2703 */       }if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x102) != 0) {
/* 2704 */         this.currentCharacter = c;
/* 2705 */         break label127;
/*      */       }
/*      */ 
/* 2708 */       while (getNextCharAsJavaIdentifierPartWithBoundCheck());
/* 2709 */       break label127;
/*      */     }
/*      */ 
/* 2713 */     while (getNextCharAsJavaIdentifierPartWithBoundCheck());
/*      */     label127: int index;
/*      */     int length;
/*      */     char[] data;
/*      */     int index;
/* 2721 */     if (this.withoutUnicodePtr == 0)
/*      */     {
/*      */       int length;
/* 2725 */       if ((length = this.currentPosition - this.startPosition) == 1) {
/* 2726 */         return 26;
/*      */       }
/* 2728 */       char[] data = this.source;
/* 2729 */       index = this.startPosition;
/*      */     } else {
/* 2731 */       if ((length = this.withoutUnicodePtr) == 1)
/* 2732 */         return 26;
/* 2733 */       data = this.withoutUnicodeBuffer;
/* 2734 */       index = 1;
/*      */     }
/*      */ 
/* 2737 */     return internalScanIdentifierOrKeyword(index, length, data);
/*      */   }
/*      */ 
/*      */   public int scanIdentifierOrKeyword()
/*      */   {
/* 2746 */     this.useAssertAsAnIndentifier = false;
/* 2747 */     this.useEnumAsAnIndentifier = false;
/*      */ 
/* 2749 */     char[] src = this.source;
/*      */ 
/* 2752 */     int srcLength = this.eofPosition;
/*      */     while (true)
/*      */     {
/*      */       int pos;
/* 2754 */       if ((pos = this.currentPosition) >= srcLength) break label127;
/* 2756 */       char c = src[pos];
/* 2757 */       if (c >= '') break;
/* 2758 */       if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 
/* 2759 */         0x3C) != 0) {
/* 2760 */         if (this.withoutUnicodePtr != 0) {
/* 2761 */           this.currentCharacter = c;
/* 2762 */           unicodeStore();
/*      */         }
/* 2764 */         this.currentPosition += 1; continue;
/* 2765 */       }if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x102) != 0) {
/* 2766 */         this.currentCharacter = c;
/* 2767 */         break label127;
/*      */       }
/*      */ 
/* 2770 */       while (getNextCharAsJavaIdentifierPart());
/* 2771 */       break label127;
/*      */     }
/*      */ 
/* 2775 */     while (getNextCharAsJavaIdentifierPart());
/*      */     label127: int index;
/*      */     int length;
/*      */     char[] data;
/*      */     int index;
/* 2783 */     if (this.withoutUnicodePtr == 0)
/*      */     {
/*      */       int length;
/* 2787 */       if ((length = this.currentPosition - this.startPosition) == 1) {
/* 2788 */         return 26;
/*      */       }
/* 2790 */       char[] data = this.source;
/* 2791 */       index = this.startPosition;
/*      */     } else {
/* 2793 */       if ((length = this.withoutUnicodePtr) == 1)
/* 2794 */         return 26;
/* 2795 */       data = this.withoutUnicodeBuffer;
/* 2796 */       index = 1;
/*      */     }
/*      */ 
/* 2799 */     return internalScanIdentifierOrKeyword(index, length, data);
/*      */   }
/*      */ 
/*      */   private int internalScanIdentifierOrKeyword(int index, int length, char[] data) {
/* 2803 */     switch (data[index]) {
/*      */     case 'a':
/* 2805 */       switch (length) {
/*      */       case 8:
/* 2807 */         index++; if (data[index] == 'b') {
/* 2808 */           index++; if (data[index] == 's') {
/* 2809 */             index++; if (data[index] == 't') {
/* 2810 */               index++; if (data[index] == 'r') {
/* 2811 */                 index++; if (data[index] == 'a') {
/* 2812 */                   index++; if (data[index] == 'c') {
/* 2813 */                     index++; if (data[index] == 't')
/* 2814 */                       return 56; 
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2816 */         return 26;
/*      */       case 6:
/* 2819 */         index++; if (data[index] == 's') {
/* 2820 */           index++; if (data[index] == 's') {
/* 2821 */             index++; if (data[index] == 'e') {
/* 2822 */               index++; if (data[index] == 'r') {
/* 2823 */                 index++; if (data[index] == 't') {
/* 2824 */                   if (this.sourceLevel >= 3145728L) {
/* 2825 */                     this.containsAssertKeyword = true;
/* 2826 */                     return 74;
/*      */                   }
/* 2828 */                   this.useAssertAsAnIndentifier = true;
/* 2829 */                   return 26;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2832 */         return 26;
/*      */       case 7:
/*      */       }
/* 2835 */       return 26;
/*      */     case 'b':
/* 2838 */       switch (length) {
/*      */       case 4:
/* 2840 */         index++; if (data[index] == 'y') { index++; if (data[index] == 't') { index++; if (data[index] == 'e')
/* 2841 */               return 33; }
/*      */         }
/* 2843 */         return 26;
/*      */       case 5:
/* 2845 */         index++; if (data[index] == 'r') {
/* 2846 */           index++; if (data[index] == 'e') {
/* 2847 */             index++; if (data[index] == 'a') {
/* 2848 */               index++; if (data[index] == 'k')
/* 2849 */                 return 75; 
/*      */             }
/*      */           }
/*      */         }
/* 2851 */         return 26;
/*      */       case 7:
/* 2853 */         index++; if (data[index] == 'o') {
/* 2854 */           index++; if (data[index] == 'o') {
/* 2855 */             index++; if (data[index] == 'l') {
/* 2856 */               index++; if (data[index] == 'e') {
/* 2857 */                 index++; if (data[index] == 'a') {
/* 2858 */                   index++; if (data[index] == 'n')
/* 2859 */                     return 32; 
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2861 */         return 26;
/*      */       case 6:
/* 2863 */       }return 26;
/*      */     case 'c':
/* 2867 */       switch (length) {
/*      */       case 4:
/* 2869 */         index++; if (data[index] == 'a') {
/* 2870 */           index++; if (data[index] == 's') { index++; if (data[index] == 'e')
/* 2871 */               return 101;
/*      */           }
/* 2873 */           return 26;
/*      */         }
/* 2875 */         if (data[index] == 'h') { index++; if (data[index] == 'a') { index++; if (data[index] == 'r')
/* 2876 */               return 34; }
/*      */         }
/* 2878 */         return 26;
/*      */       case 5:
/* 2880 */         index++; if (data[index] == 'a') {
/* 2881 */           index++; if (data[index] == 't') { index++; if (data[index] == 'c') { index++; if (data[index] == 'h')
/* 2882 */                 return 102; }
/*      */           }
/* 2884 */           return 26;
/*      */         }
/* 2886 */         if (data[index] == 'l') {
/* 2887 */           index++; if (data[index] == 'a') {
/* 2888 */             index++; if (data[index] == 's') {
/* 2889 */               index++; if (data[index] == 's')
/* 2890 */                 return 72; 
/*      */             }
/*      */           }
/* 2892 */           return 26;
/* 2893 */         }if (data[index] == 'o') {
/* 2894 */           index++; if (data[index] == 'n') {
/* 2895 */             index++; if (data[index] == 's') {
/* 2896 */               index++; if (data[index] == 't')
/* 2897 */                 return 108; 
/*      */             }
/*      */           }
/*      */         }
/* 2899 */         return 26;
/*      */       case 8:
/* 2901 */         index++; if (data[index] == 'o') {
/* 2902 */           index++; if (data[index] == 'n') {
/* 2903 */             index++; if (data[index] == 't') {
/* 2904 */               index++; if (data[index] == 'i') {
/* 2905 */                 index++; if (data[index] == 'n') {
/* 2906 */                   index++; if (data[index] == 'u') {
/* 2907 */                     index++; if (data[index] == 'e')
/* 2908 */                       return 76; 
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2910 */         return 26;
/*      */       case 6:
/* 2912 */       case 7: } return 26;
/*      */     case 'd':
/* 2916 */       switch (length) {
/*      */       case 2:
/* 2918 */         index++; if (data[index] == 'o') {
/* 2919 */           return 77;
/*      */         }
/* 2921 */         return 26;
/*      */       case 6:
/* 2923 */         index++; if (data[index] == 'o') {
/* 2924 */           index++; if (data[index] == 'u') {
/* 2925 */             index++; if (data[index] == 'b') {
/* 2926 */               index++; if (data[index] == 'l') {
/* 2927 */                 index++; if (data[index] == 'e')
/* 2928 */                   return 35; 
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2930 */         return 26;
/*      */       case 7:
/* 2932 */         index++; if (data[index] == 'e') {
/* 2933 */           index++; if (data[index] == 'f') {
/* 2934 */             index++; if (data[index] == 'a') {
/* 2935 */               index++; if (data[index] == 'u') {
/* 2936 */                 index++; if (data[index] == 'l') {
/* 2937 */                   index++; if (data[index] == 't')
/* 2938 */                     return 97; 
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2940 */         return 26;
/*      */       case 3:
/*      */       case 4:
/* 2942 */       case 5: } return 26;
/*      */     case 'e':
/* 2945 */       switch (length) {
/*      */       case 4:
/* 2947 */         index++; if (data[index] == 'l') { index++; if (data[index] == 's') { index++; if (data[index] == 'e')
/* 2948 */               return 103; } }
/* 2949 */         if (data[index] == 'n') {
/* 2950 */           index++; if (data[index] == 'u') {
/* 2951 */             index++; if (data[index] == 'm') {
/* 2952 */               if (this.sourceLevel >= 3211264L) {
/* 2953 */                 return 98;
/*      */               }
/* 2955 */               this.useEnumAsAnIndentifier = true;
/* 2956 */               return 26;
/*      */             }
/*      */           }
/*      */         }
/* 2959 */         return 26;
/*      */       case 7:
/* 2962 */         index++; if (data[index] == 'x') {
/* 2963 */           index++; if (data[index] == 't') {
/* 2964 */             index++; if (data[index] == 'e') {
/* 2965 */               index++; if (data[index] == 'n') {
/* 2966 */                 index++; if (data[index] == 'd') {
/* 2967 */                   index++; if (data[index] == 's')
/* 2968 */                     return 99; 
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2970 */         return 26;
/*      */       case 5:
/* 2972 */       case 6: } return 26;
/*      */     case 'f':
/* 2976 */       switch (length) {
/*      */       case 3:
/* 2978 */         index++; if (data[index] == 'o') { index++; if (data[index] == 'r')
/* 2979 */             return 78;
/*      */         }
/* 2981 */         return 26;
/*      */       case 5:
/* 2983 */         index++; if (data[index] == 'i') {
/* 2984 */           index++; if (data[index] == 'n') {
/* 2985 */             index++; if (data[index] == 'a') {
/* 2986 */               index++; if (data[index] == 'l')
/* 2987 */                 return 57; 
/*      */             }
/*      */           }
/* 2989 */           return 26;
/*      */         }
/* 2991 */         if (data[index] == 'l') {
/* 2992 */           index++; if (data[index] == 'o') {
/* 2993 */             index++; if (data[index] == 'a') {
/* 2994 */               index++; if (data[index] == 't')
/* 2995 */                 return 36; 
/*      */             }
/*      */           }
/* 2997 */           return 26;
/*      */         }
/* 2999 */         if (data[index] == 'a') {
/* 3000 */           index++; if (data[index] == 'l') {
/* 3001 */             index++; if (data[index] == 's') {
/* 3002 */               index++; if (data[index] == 'e')
/* 3003 */                 return 44; 
/*      */             }
/*      */           }
/*      */         }
/* 3005 */         return 26;
/*      */       case 7:
/* 3007 */         index++; if (data[index] == 'i') {
/* 3008 */           index++; if (data[index] == 'n') {
/* 3009 */             index++; if (data[index] == 'a') {
/* 3010 */               index++; if (data[index] == 'l') {
/* 3011 */                 index++; if (data[index] == 'l') {
/* 3012 */                   index++; if (data[index] == 'y')
/* 3013 */                     return 104; 
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3015 */         return 26;
/*      */       case 4:
/*      */       case 6:
/* 3018 */       }return 26;
/*      */     case 'g':
/* 3021 */       if (length == 4) {
/* 3022 */         index++; if (data[index] == 'o') {
/* 3023 */           index++; if (data[index] == 't') {
/* 3024 */             index++; if (data[index] == 'o')
/* 3025 */               return 109; 
/*      */           }
/*      */         }
/*      */       }
/* 3028 */       return 26;
/*      */     case 'i':
/* 3031 */       switch (length) {
/*      */       case 2:
/* 3033 */         index++; if (data[index] == 'f') {
/* 3034 */           return 79;
/*      */         }
/* 3036 */         return 26;
/*      */       case 3:
/* 3038 */         index++; if (data[index] == 'n') { index++; if (data[index] == 't')
/* 3039 */             return 37;
/*      */         }
/* 3041 */         return 26;
/*      */       case 6:
/* 3043 */         index++; if (data[index] == 'm') {
/* 3044 */           index++; if (data[index] == 'p') {
/* 3045 */             index++; if (data[index] == 'o') {
/* 3046 */               index++; if (data[index] == 'r') {
/* 3047 */                 index++; if (data[index] == 't')
/* 3048 */                   return 100; 
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3050 */         return 26;
/*      */       case 9:
/* 3052 */         index++; if (data[index] == 'n') {
/* 3053 */           index++; if (data[index] == 't') {
/* 3054 */             index++; if (data[index] == 'e') {
/* 3055 */               index++; if (data[index] == 'r') {
/* 3056 */                 index++; if (data[index] == 'f') {
/* 3057 */                   index++; if (data[index] == 'a') {
/* 3058 */                     index++; if (data[index] == 'c') {
/* 3059 */                       index++; if (data[index] == 'e')
/* 3060 */                         return 95; 
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3062 */         return 26;
/*      */       case 10:
/* 3064 */         index++; if (data[index] == 'm') {
/* 3065 */           index++; if (data[index] == 'p') {
/* 3066 */             index++; if (data[index] == 'l') {
/* 3067 */               index++; if (data[index] == 'e') {
/* 3068 */                 index++; if (data[index] == 'm') {
/* 3069 */                   index++; if (data[index] == 'e') {
/* 3070 */                     index++; if (data[index] == 'n') {
/* 3071 */                       index++; if (data[index] == 't') {
/* 3072 */                         index++; if (data[index] == 's')
/* 3073 */                           return 106; 
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 3075 */           return 26;
/*      */         }
/* 3077 */         if (data[index] == 'n') {
/* 3078 */           index++; if (data[index] == 's') {
/* 3079 */             index++; if (data[index] == 't') {
/* 3080 */               index++; if (data[index] == 'a') {
/* 3081 */                 index++; if (data[index] == 'n') {
/* 3082 */                   index++; if (data[index] == 'c') {
/* 3083 */                     index++; if (data[index] == 'e') {
/* 3084 */                       index++; if (data[index] == 'o') {
/* 3085 */                         index++; if (data[index] == 'f')
/* 3086 */                           return 12; 
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3088 */         return 26;
/*      */       case 4:
/*      */       case 5:
/*      */       case 7:
/* 3091 */       case 8: } return 26;
/*      */     case 'l':
/* 3095 */       if (length == 4) {
/* 3096 */         index++; if (data[index] == 'o') {
/* 3097 */           index++; if (data[index] == 'n') {
/* 3098 */             index++; if (data[index] == 'g')
/* 3099 */               return 38; 
/*      */           }
/*      */         }
/*      */       }
/* 3102 */       return 26;
/*      */     case 'n':
/* 3105 */       switch (length) {
/*      */       case 3:
/* 3107 */         index++; if (data[index] == 'e') { index++; if (data[index] == 'w')
/* 3108 */             return 43;
/*      */         }
/* 3110 */         return 26;
/*      */       case 4:
/* 3112 */         index++; if (data[index] == 'u') { index++; if (data[index] == 'l') { index++; if (data[index] == 'l')
/* 3113 */               return 45; }
/*      */         }
/* 3115 */         return 26;
/*      */       case 6:
/* 3117 */         index++; if (data[index] == 'a') {
/* 3118 */           index++; if (data[index] == 't') {
/* 3119 */             index++; if (data[index] == 'i') {
/* 3120 */               index++; if (data[index] == 'v') {
/* 3121 */                 index++; if (data[index] == 'e')
/* 3122 */                   return 58; 
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3124 */         return 26;
/*      */       case 5:
/* 3126 */       }return 26;
/*      */     case 'p':
/* 3130 */       switch (length) {
/*      */       case 6:
/* 3132 */         index++; if (data[index] == 'u') {
/* 3133 */           index++; if (data[index] == 'b') {
/* 3134 */             index++; if (data[index] == 'l') {
/* 3135 */               index++; if (data[index] == 'i') {
/* 3136 */                 index++; if (data[index] == 'c')
/* 3137 */                   return 61; 
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3139 */         return 26;
/*      */       case 7:
/* 3141 */         index++; if (data[index] == 'a') {
/* 3142 */           index++; if (data[index] == 'c') {
/* 3143 */             index++; if (data[index] == 'k') {
/* 3144 */               index++; if (data[index] == 'a') {
/* 3145 */                 index++; if (data[index] == 'g') {
/* 3146 */                   index++; if (data[index] == 'e')
/* 3147 */                     return 96; 
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 3149 */           return 26;
/*      */         }
/* 3151 */         if (data[index] == 'r') {
/* 3152 */           index++; if (data[index] == 'i') {
/* 3153 */             index++; if (data[index] == 'v') {
/* 3154 */               index++; if (data[index] == 'a') {
/* 3155 */                 index++; if (data[index] == 't') {
/* 3156 */                   index++; if (data[index] == 'e')
/* 3157 */                     return 59; 
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3159 */         return 26;
/*      */       case 9:
/* 3161 */         index++; if (data[index] == 'r') {
/* 3162 */           index++; if (data[index] == 'o') {
/* 3163 */             index++; if (data[index] == 't') {
/* 3164 */               index++; if (data[index] == 'e') {
/* 3165 */                 index++; if (data[index] == 'c') {
/* 3166 */                   index++; if (data[index] == 't') {
/* 3167 */                     index++; if (data[index] == 'e') {
/* 3168 */                       index++; if (data[index] == 'd')
/* 3169 */                         return 60; 
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3171 */         return 26;
/*      */       case 8:
/*      */       }
/* 3174 */       return 26;
/*      */     case 'r':
/* 3178 */       if (length == 6) {
/* 3179 */         index++; if (data[index] == 'e') {
/* 3180 */           index++; if (data[index] == 't') {
/* 3181 */             index++; if (data[index] == 'u') {
/* 3182 */               index++; if (data[index] == 'r') {
/* 3183 */                 index++; if (data[index] == 'n')
/* 3184 */                   return 80; 
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3187 */       return 26;
/*      */     case 's':
/* 3190 */       switch (length) {
/*      */       case 5:
/* 3192 */         index++; if (data[index] == 'h') {
/* 3193 */           index++; if (data[index] == 'o') { index++; if (data[index] == 'r') { index++; if (data[index] == 't')
/* 3194 */                 return 39; }
/*      */           }
/* 3196 */           return 26;
/*      */         }
/* 3198 */         if (data[index] == 'u') {
/* 3199 */           index++; if (data[index] == 'p') {
/* 3200 */             index++; if (data[index] == 'e') {
/* 3201 */               index++; if (data[index] == 'r')
/* 3202 */                 return 41; 
/*      */             }
/*      */           }
/*      */         }
/* 3204 */         return 26;
/*      */       case 6:
/* 3207 */         index++; if (data[index] == 't') {
/* 3208 */           index++; if (data[index] == 'a') {
/* 3209 */             index++; if (data[index] == 't') {
/* 3210 */               index++; if (data[index] == 'i') {
/* 3211 */                 index++; if (data[index] == 'c')
/* 3212 */                   return 54; 
/*      */               }
/*      */             }
/*      */           }
/* 3214 */           return 26;
/*      */         }
/* 3216 */         if (data[index] == 'w') {
/* 3217 */           index++; if (data[index] == 'i') {
/* 3218 */             index++; if (data[index] == 't') {
/* 3219 */               index++; if (data[index] == 'c') {
/* 3220 */                 index++; if (data[index] == 'h')
/* 3221 */                   return 81; 
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3223 */         return 26;
/*      */       case 8:
/* 3225 */         index++; if (data[index] == 't') {
/* 3226 */           index++; if (data[index] == 'r') {
/* 3227 */             index++; if (data[index] == 'i') {
/* 3228 */               index++; if (data[index] == 'c') {
/* 3229 */                 index++; if (data[index] == 't') {
/* 3230 */                   index++; if (data[index] == 'f') {
/* 3231 */                     index++; if (data[index] == 'p')
/* 3232 */                       return 62; 
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3234 */         return 26;
/*      */       case 12:
/* 3236 */         index++; if (data[index] == 'y') {
/* 3237 */           index++; if (data[index] == 'n') {
/* 3238 */             index++; if (data[index] == 'c') {
/* 3239 */               index++; if (data[index] == 'h') {
/* 3240 */                 index++; if (data[index] == 'r') {
/* 3241 */                   index++; if (data[index] == 'o') {
/* 3242 */                     index++; if (data[index] == 'n') {
/* 3243 */                       index++; if (data[index] == 'i') {
/* 3244 */                         index++; if (data[index] == 'z') {
/* 3245 */                           index++; if (data[index] == 'e') {
/* 3246 */                             index++; if (data[index] == 'd')
/* 3247 */                               return 55; 
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3249 */         return 26;
/*      */       case 7:
/*      */       case 9:
/*      */       case 10:
/* 3251 */       case 11: } return 26;
/*      */     case 't':
/* 3255 */       switch (length) {
/*      */       case 3:
/* 3257 */         index++; if (data[index] == 'r') { index++; if (data[index] == 'y')
/* 3258 */             return 83;
/*      */         }
/* 3260 */         return 26;
/*      */       case 4:
/* 3262 */         index++; if (data[index] == 'h') {
/* 3263 */           index++; if (data[index] == 'i') { index++; if (data[index] == 's')
/* 3264 */               return 42;
/*      */           }
/* 3266 */           return 26;
/*      */         }
/* 3268 */         if (data[index] == 'r') { index++; if (data[index] == 'u') { index++; if (data[index] == 'e')
/* 3269 */               return 46; }
/*      */         }
/* 3271 */         return 26;
/*      */       case 5:
/* 3273 */         index++; if (data[index] == 'h') {
/* 3274 */           index++; if (data[index] == 'r') {
/* 3275 */             index++; if (data[index] == 'o') {
/* 3276 */               index++; if (data[index] == 'w')
/* 3277 */                 return 82; 
/*      */             }
/*      */           }
/*      */         }
/* 3279 */         return 26;
/*      */       case 6:
/* 3281 */         index++; if (data[index] == 'h') {
/* 3282 */           index++; if (data[index] == 'r') {
/* 3283 */             index++; if (data[index] == 'o') {
/* 3284 */               index++; if (data[index] == 'w') {
/* 3285 */                 index++; if (data[index] == 's')
/* 3286 */                   return 105; 
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3288 */         return 26;
/*      */       case 9:
/* 3290 */         index++; if (data[index] == 'r') {
/* 3291 */           index++; if (data[index] == 'a') {
/* 3292 */             index++; if (data[index] == 'n') {
/* 3293 */               index++; if (data[index] == 's') {
/* 3294 */                 index++; if (data[index] == 'i') {
/* 3295 */                   index++; if (data[index] == 'e') {
/* 3296 */                     index++; if (data[index] == 'n') {
/* 3297 */                       index++; if (data[index] == 't')
/* 3298 */                         return 63; 
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3300 */         return 26;
/*      */       case 7:
/*      */       case 8:
/* 3303 */       }return 26;
/*      */     case 'v':
/* 3307 */       switch (length) {
/*      */       case 4:
/* 3309 */         index++; if (data[index] == 'o') { index++; if (data[index] == 'i') { index++; if (data[index] == 'd')
/* 3310 */               return 40; }
/*      */         }
/* 3312 */         return 26;
/*      */       case 8:
/* 3314 */         index++; if (data[index] == 'o') {
/* 3315 */           index++; if (data[index] == 'l') {
/* 3316 */             index++; if (data[index] == 'a') {
/* 3317 */               index++; if (data[index] == 't') {
/* 3318 */                 index++; if (data[index] == 'i') {
/* 3319 */                   index++; if (data[index] == 'l') {
/* 3320 */                     index++; if (data[index] == 'e')
/* 3321 */                       return 64; 
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3323 */         return 26;
/*      */       case 5:
/*      */       case 6:
/* 3326 */       case 7: } return 26;
/*      */     case 'w':
/* 3330 */       switch (length) {
/*      */       case 5:
/* 3332 */         index++; if (data[index] == 'h') {
/* 3333 */           index++; if (data[index] == 'i') {
/* 3334 */             index++; if (data[index] == 'l') {
/* 3335 */               index++; if (data[index] == 'e')
/* 3336 */                 return 73; 
/*      */             }
/*      */           }
/*      */         }
/* 3338 */         return 26;
/*      */       }
/*      */ 
/* 3344 */       return 26;
/*      */     case 'h':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'm':
/*      */     case 'o':
/*      */     case 'q':
/* 3348 */     case 'u': } return 26;
/*      */   }
/*      */ 
/*      */   public int scanNumber(boolean dotPrefix)
/*      */     throws InvalidInputException
/*      */   {
/* 3359 */     boolean floating = dotPrefix;
/* 3360 */     if ((!dotPrefix) && (this.currentCharacter == '0')) {
/* 3361 */       if (getNextChar('x', 'X') >= 0) {
/* 3362 */         int start = this.currentPosition;
/* 3363 */         while (getNextCharAsDigit(16));
/* 3364 */         int end = this.currentPosition;
/* 3365 */         if (getNextChar('l', 'L') >= 0) {
/* 3366 */           if (end == start) {
/* 3367 */             throw new InvalidInputException("Invalid_Hexa_Literal");
/*      */           }
/* 3369 */           return 48;
/* 3370 */         }if (getNextChar('.')) {
/* 3371 */           if (this.sourceLevel < 3211264L) {
/* 3372 */             if (end == start) {
/* 3373 */               throw new InvalidInputException("Invalid_Hexa_Literal");
/*      */             }
/* 3375 */             this.currentPosition = end;
/* 3376 */             return 47;
/*      */           }
/*      */ 
/* 3380 */           boolean hasNoDigitsBeforeDot = end == start;
/* 3381 */           start = this.currentPosition;
/* 3382 */           while (getNextCharAsDigit(16));
/* 3383 */           end = this.currentPosition;
/* 3384 */           if ((hasNoDigitsBeforeDot) && (end == start)) {
/* 3385 */             throw new InvalidInputException("Invalid_Hexa_Literal");
/*      */           }
/*      */ 
/* 3388 */           if (getNextChar('p', 'P') >= 0) {
/* 3389 */             this.unicodeAsBackSlash = false;
/* 3390 */             if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 3391 */               (this.source[this.currentPosition] == 'u')) {
/* 3392 */               getNextUnicodeChar();
/*      */             }
/* 3394 */             else if (this.withoutUnicodePtr != 0) {
/* 3395 */               unicodeStore();
/*      */             }
/*      */ 
/* 3399 */             if ((this.currentCharacter == '-') || 
/* 3400 */               (this.currentCharacter == '+')) {
/* 3401 */               this.unicodeAsBackSlash = false;
/* 3402 */               if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 3403 */                 (this.source[this.currentPosition] == 'u')) {
/* 3404 */                 getNextUnicodeChar();
/*      */               }
/* 3406 */               else if (this.withoutUnicodePtr != 0) {
/* 3407 */                 unicodeStore();
/*      */               }
/*      */             }
/*      */ 
/* 3411 */             if (!ScannerHelper.isDigit(this.currentCharacter)) {
/* 3412 */               throw new InvalidInputException("Invalid_Hexa_Literal");
/*      */             }
/* 3414 */             while (getNextCharAsDigit());
/* 3415 */             if (getNextChar('f', 'F') >= 0) {
/* 3416 */               return 49;
/*      */             }
/* 3418 */             if (getNextChar('d', 'D') >= 0) {
/* 3419 */               return 50;
/*      */             }
/* 3421 */             if (getNextChar('l', 'L') >= 0) {
/* 3422 */               throw new InvalidInputException("Invalid_Hexa_Literal");
/*      */             }
/* 3424 */             return 50;
/*      */           }
/* 3426 */           throw new InvalidInputException("Invalid_Hexa_Literal");
/*      */         }
/* 3428 */         if (getNextChar('p', 'P') >= 0) {
/* 3429 */           if (this.sourceLevel < 3211264L)
/*      */           {
/* 3431 */             this.currentPosition = end;
/* 3432 */             return 47;
/*      */           }
/* 3434 */           this.unicodeAsBackSlash = false;
/* 3435 */           if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 3436 */             (this.source[this.currentPosition] == 'u')) {
/* 3437 */             getNextUnicodeChar();
/*      */           }
/* 3439 */           else if (this.withoutUnicodePtr != 0) {
/* 3440 */             unicodeStore();
/*      */           }
/*      */ 
/* 3444 */           if ((this.currentCharacter == '-') || 
/* 3445 */             (this.currentCharacter == '+')) {
/* 3446 */             this.unicodeAsBackSlash = false;
/* 3447 */             if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 3448 */               (this.source[this.currentPosition] == 'u')) {
/* 3449 */               getNextUnicodeChar();
/*      */             }
/* 3451 */             else if (this.withoutUnicodePtr != 0) {
/* 3452 */               unicodeStore();
/*      */             }
/*      */           }
/*      */ 
/* 3456 */           if (!ScannerHelper.isDigit(this.currentCharacter))
/* 3457 */             throw new InvalidInputException("Invalid_Float_Literal");
/* 3458 */           while (getNextCharAsDigit());
/* 3459 */           if (getNextChar('f', 'F') >= 0)
/* 3460 */             return 49;
/* 3461 */           if (getNextChar('d', 'D') >= 0)
/* 3462 */             return 50;
/* 3463 */           if (getNextChar('l', 'L') >= 0) {
/* 3464 */             throw new InvalidInputException("Invalid_Hexa_Literal");
/*      */           }
/* 3466 */           return 50;
/*      */         }
/* 3468 */         if (end == start)
/* 3469 */           throw new InvalidInputException("Invalid_Hexa_Literal");
/* 3470 */         return 47;
/*      */       }
/*      */ 
/* 3476 */       if (getNextCharAsDigit()) {
/* 3477 */         while (getNextCharAsDigit());
/* 3479 */         if (getNextChar('l', 'L') >= 0) {
/* 3480 */           return 48;
/*      */         }
/*      */ 
/* 3483 */         if (getNextChar('f', 'F') >= 0) {
/* 3484 */           return 49;
/*      */         }
/*      */ 
/* 3487 */         if (getNextChar('d', 'D') >= 0) {
/* 3488 */           return 50;
/*      */         }
/* 3490 */         boolean isInteger = true;
/* 3491 */         if (getNextChar('.')) { isInteger = false;
/* 3493 */           while (getNextCharAsDigit()); }
/* 3495 */         if (getNextChar('e', 'E') >= 0) {
/* 3496 */           isInteger = false;
/* 3497 */           this.unicodeAsBackSlash = false;
/* 3498 */           if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 3499 */             (this.source[this.currentPosition] == 'u')) {
/* 3500 */             getNextUnicodeChar();
/*      */           }
/* 3502 */           else if (this.withoutUnicodePtr != 0) {
/* 3503 */             unicodeStore();
/*      */           }
/*      */ 
/* 3507 */           if ((this.currentCharacter == '-') || 
/* 3508 */             (this.currentCharacter == '+')) {
/* 3509 */             this.unicodeAsBackSlash = false;
/* 3510 */             if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 3511 */               (this.source[this.currentPosition] == 'u')) {
/* 3512 */               getNextUnicodeChar();
/*      */             }
/* 3514 */             else if (this.withoutUnicodePtr != 0) {
/* 3515 */               unicodeStore();
/*      */             }
/*      */           }
/*      */ 
/* 3519 */           if (!ScannerHelper.isDigit(this.currentCharacter))
/* 3520 */             throw new InvalidInputException("Invalid_Float_Literal"); while (getNextCharAsDigit());
/*      */         }
/* 3523 */         if (getNextChar('f', 'F') >= 0)
/* 3524 */           return 49;
/* 3525 */         if ((getNextChar('d', 'D') >= 0) || (!isInteger))
/* 3526 */           return 50;
/* 3527 */         return 47;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3534 */     while (getNextCharAsDigit());
/* 3536 */     if ((!dotPrefix) && (getNextChar('l', 'L') >= 0)) {
/* 3537 */       return 48;
/*      */     }
/* 3539 */     if ((!dotPrefix) && (getNextChar('.'))) {
/* 3540 */       while (getNextCharAsDigit());
/* 3541 */       floating = true;
/*      */     }
/*      */ 
/* 3546 */     if (getNextChar('e', 'E') >= 0) {
/* 3547 */       floating = true;
/*      */ 
/* 3549 */       this.unicodeAsBackSlash = false;
/* 3550 */       if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 3551 */         (this.source[this.currentPosition] == 'u')) {
/* 3552 */         getNextUnicodeChar();
/*      */       }
/* 3554 */       else if (this.withoutUnicodePtr != 0) {
/* 3555 */         unicodeStore();
/*      */       }
/*      */ 
/* 3559 */       if ((this.currentCharacter == '-') || 
/* 3560 */         (this.currentCharacter == '+')) {
/* 3561 */         this.unicodeAsBackSlash = false;
/* 3562 */         if (((this.currentCharacter = this.source[(this.currentPosition++)]) == '\\') && 
/* 3563 */           (this.source[this.currentPosition] == 'u')) {
/* 3564 */           getNextUnicodeChar();
/*      */         }
/* 3566 */         else if (this.withoutUnicodePtr != 0) {
/* 3567 */           unicodeStore();
/*      */         }
/*      */       }
/*      */ 
/* 3571 */       if (!ScannerHelper.isDigit(this.currentCharacter))
/* 3572 */         throw new InvalidInputException("Invalid_Float_Literal");
/* 3573 */       while (getNextCharAsDigit());
/*      */     }
/* 3576 */     if (getNextChar('d', 'D') >= 0)
/* 3577 */       return 50;
/* 3578 */     if (getNextChar('f', 'F') >= 0) {
/* 3579 */       return 49;
/*      */     }
/*      */ 
/* 3583 */     return floating ? 50 : 47;
/*      */   }
/*      */ 
/*      */   public final int getLineNumber(int position)
/*      */   {
/* 3592 */     return Util.getLineNumber(position, this.lineEnds, 0, this.linePtr);
/*      */   }
/*      */ 
/*      */   public final void setSource(char[] sourceString)
/*      */   {
/*      */     int sourceLength;
/*      */     int sourceLength;
/* 3598 */     if (sourceString == null) {
/* 3599 */       this.source = CharOperation.NO_CHAR;
/* 3600 */       sourceLength = 0;
/*      */     } else {
/* 3602 */       this.source = sourceString;
/* 3603 */       sourceLength = sourceString.length;
/*      */     }
/* 3605 */     this.startPosition = -1;
/* 3606 */     this.eofPosition = sourceLength;
/* 3607 */     this.initialPosition = (this.currentPosition = 0);
/* 3608 */     this.containsAssertKeyword = false;
/* 3609 */     this.linePtr = -1;
/*      */   }
/*      */ 
/*      */   public final void setSource(char[] contents, CompilationResult compilationResult)
/*      */   {
/* 3616 */     if (contents == null) {
/* 3617 */       char[] cuContents = compilationResult.compilationUnit.getContents();
/* 3618 */       setSource(cuContents);
/*      */     } else {
/* 3620 */       setSource(contents);
/*      */     }
/* 3622 */     int[] lineSeparatorPositions = compilationResult.lineSeparatorPositions;
/* 3623 */     if (lineSeparatorPositions != null) {
/* 3624 */       this.lineEnds = lineSeparatorPositions;
/* 3625 */       this.linePtr = (lineSeparatorPositions.length - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void setSource(CompilationResult compilationResult)
/*      */   {
/* 3633 */     setSource(null, compilationResult);
/*      */   }
/*      */   public String toString() {
/* 3636 */     if (this.startPosition == this.eofPosition)
/* 3637 */       return "EOF\n\n" + new String(this.source);
/* 3638 */     if (this.currentPosition > this.eofPosition)
/* 3639 */       return "behind the EOF\n\n" + new String(this.source);
/* 3640 */     if (this.currentPosition <= 0) {
/* 3641 */       return "NOT started!\n\n" + new String(this.source);
/*      */     }
/* 3643 */     char[] front = new char[this.startPosition];
/* 3644 */     System.arraycopy(this.source, 0, front, 0, this.startPosition);
/*      */ 
/* 3646 */     int middleLength = this.currentPosition - 1 - this.startPosition + 1;
/*      */     char[] middle;
/* 3648 */     if (middleLength > -1) {
/* 3649 */       char[] middle = new char[middleLength];
/* 3650 */       System.arraycopy(
/* 3651 */         this.source, 
/* 3652 */         this.startPosition, 
/* 3653 */         middle, 
/* 3654 */         0, 
/* 3655 */         middleLength);
/*      */     } else {
/* 3657 */       middle = CharOperation.NO_CHAR;
/*      */     }
/*      */ 
/* 3660 */     char[] end = new char[this.eofPosition - (this.currentPosition - 1)];
/* 3661 */     System.arraycopy(
/* 3662 */       this.source, 
/* 3663 */       this.currentPosition - 1 + 1, 
/* 3664 */       end, 
/* 3665 */       0, 
/* 3666 */       this.eofPosition - (this.currentPosition - 1) - 1);
/*      */ 
/* 3668 */     return new String(front) + 
/* 3669 */       "\n===============================\nStarts here -->" + 
/* 3670 */       new String(middle) + 
/* 3671 */       "<-- Ends here\n===============================\n" + 
/* 3672 */       new String(end);
/*      */   }
/*      */   public String toStringAction(int act) {
/* 3675 */     switch (act) {
/*      */     case 26:
/* 3677 */       return "Identifier(" + new String(getCurrentTokenSource()) + ")";
/*      */     case 56:
/* 3679 */       return "abstract";
/*      */     case 32:
/* 3681 */       return "boolean";
/*      */     case 75:
/* 3683 */       return "break";
/*      */     case 33:
/* 3685 */       return "byte";
/*      */     case 101:
/* 3687 */       return "case";
/*      */     case 102:
/* 3689 */       return "catch";
/*      */     case 34:
/* 3691 */       return "char";
/*      */     case 72:
/* 3693 */       return "class";
/*      */     case 76:
/* 3695 */       return "continue";
/*      */     case 97:
/* 3697 */       return "default";
/*      */     case 77:
/* 3699 */       return "do";
/*      */     case 35:
/* 3701 */       return "double";
/*      */     case 103:
/* 3703 */       return "else";
/*      */     case 99:
/* 3705 */       return "extends";
/*      */     case 44:
/* 3707 */       return "false";
/*      */     case 57:
/* 3709 */       return "final";
/*      */     case 104:
/* 3711 */       return "finally";
/*      */     case 36:
/* 3713 */       return "float";
/*      */     case 78:
/* 3715 */       return "for";
/*      */     case 79:
/* 3717 */       return "if";
/*      */     case 106:
/* 3719 */       return "implements";
/*      */     case 100:
/* 3721 */       return "import";
/*      */     case 12:
/* 3723 */       return "instanceof";
/*      */     case 37:
/* 3725 */       return "int";
/*      */     case 95:
/* 3727 */       return "interface";
/*      */     case 38:
/* 3729 */       return "long";
/*      */     case 58:
/* 3731 */       return "native";
/*      */     case 43:
/* 3733 */       return "new";
/*      */     case 45:
/* 3735 */       return "null";
/*      */     case 96:
/* 3737 */       return "package";
/*      */     case 59:
/* 3739 */       return "private";
/*      */     case 60:
/* 3741 */       return "protected";
/*      */     case 61:
/* 3743 */       return "public";
/*      */     case 80:
/* 3745 */       return "return";
/*      */     case 39:
/* 3747 */       return "short";
/*      */     case 54:
/* 3749 */       return "static";
/*      */     case 41:
/* 3751 */       return "super";
/*      */     case 81:
/* 3753 */       return "switch";
/*      */     case 55:
/* 3755 */       return "synchronized";
/*      */     case 42:
/* 3757 */       return "this";
/*      */     case 82:
/* 3759 */       return "throw";
/*      */     case 105:
/* 3761 */       return "throws";
/*      */     case 63:
/* 3763 */       return "transient";
/*      */     case 46:
/* 3765 */       return "true";
/*      */     case 83:
/* 3767 */       return "try";
/*      */     case 40:
/* 3769 */       return "void";
/*      */     case 64:
/* 3771 */       return "volatile";
/*      */     case 73:
/* 3773 */       return "while";
/*      */     case 47:
/* 3776 */       return "Integer(" + new String(getCurrentTokenSource()) + ")";
/*      */     case 48:
/* 3778 */       return "Long(" + new String(getCurrentTokenSource()) + ")";
/*      */     case 49:
/* 3780 */       return "Float(" + new String(getCurrentTokenSource()) + ")";
/*      */     case 50:
/* 3782 */       return "Double(" + new String(getCurrentTokenSource()) + ")";
/*      */     case 51:
/* 3784 */       return "Char(" + new String(getCurrentTokenSource()) + ")";
/*      */     case 52:
/* 3786 */       return "String(" + new String(getCurrentTokenSource()) + ")";
/*      */     case 8:
/* 3789 */       return "++";
/*      */     case 9:
/* 3791 */       return "--";
/*      */     case 18:
/* 3793 */       return "==";
/*      */     case 15:
/* 3795 */       return "<=";
/*      */     case 16:
/* 3797 */       return ">=";
/*      */     case 19:
/* 3799 */       return "!=";
/*      */     case 17:
/* 3801 */       return "<<";
/*      */     case 10:
/* 3803 */       return ">>";
/*      */     case 11:
/* 3805 */       return ">>>";
/*      */     case 84:
/* 3807 */       return "+=";
/*      */     case 85:
/* 3809 */       return "-=";
/*      */     case 86:
/* 3811 */       return "*=";
/*      */     case 87:
/* 3813 */       return "/=";
/*      */     case 88:
/* 3815 */       return "&=";
/*      */     case 89:
/* 3817 */       return "|=";
/*      */     case 90:
/* 3819 */       return "^=";
/*      */     case 91:
/* 3821 */       return "%=";
/*      */     case 92:
/* 3823 */       return "<<=";
/*      */     case 93:
/* 3825 */       return ">>=";
/*      */     case 94:
/* 3827 */       return ">>>=";
/*      */     case 25:
/* 3829 */       return "||";
/*      */     case 24:
/* 3831 */       return "&&";
/*      */     case 1:
/* 3833 */       return "+";
/*      */     case 2:
/* 3835 */       return "-";
/*      */     case 66:
/* 3837 */       return "!";
/*      */     case 5:
/* 3839 */       return "%";
/*      */     case 21:
/* 3841 */       return "^";
/*      */     case 20:
/* 3843 */       return "&";
/*      */     case 4:
/* 3845 */       return "*";
/*      */     case 22:
/* 3847 */       return "|";
/*      */     case 67:
/* 3849 */       return "~";
/*      */     case 6:
/* 3851 */       return "/";
/*      */     case 13:
/* 3853 */       return ">";
/*      */     case 7:
/* 3855 */       return "<";
/*      */     case 28:
/* 3857 */       return "(";
/*      */     case 29:
/* 3859 */       return ")";
/*      */     case 69:
/* 3861 */       return "{";
/*      */     case 31:
/* 3863 */       return "}";
/*      */     case 14:
/* 3865 */       return "[";
/*      */     case 70:
/* 3867 */       return "]";
/*      */     case 27:
/* 3869 */       return ";";
/*      */     case 23:
/* 3871 */       return "?";
/*      */     case 65:
/* 3873 */       return ":";
/*      */     case 30:
/* 3875 */       return ",";
/*      */     case 3:
/* 3877 */       return ".";
/*      */     case 71:
/* 3879 */       return "=";
/*      */     case 68:
/* 3881 */       return "EOF";
/*      */     case 1000:
/* 3883 */       return "white_space(" + new String(getCurrentTokenSource()) + ")";
/*      */     }
/* 3885 */     return "not-a-token";
/*      */   }
/*      */ 
/*      */   public void unicodeInitializeBuffer(int length) {
/* 3889 */     this.withoutUnicodePtr = length;
/* 3890 */     if (this.withoutUnicodeBuffer == null) this.withoutUnicodeBuffer = new char[length + 11];
/* 3891 */     int bLength = this.withoutUnicodeBuffer.length;
/* 3892 */     if (1 + length >= bLength) {
/* 3893 */       System.arraycopy(this.withoutUnicodeBuffer, 0, this.withoutUnicodeBuffer = new char[length + 11], 0, bLength);
/*      */     }
/* 3895 */     System.arraycopy(this.source, this.startPosition, this.withoutUnicodeBuffer, 1, length);
/*      */   }
/*      */   public void unicodeStore() {
/* 3898 */     int pos = ++this.withoutUnicodePtr;
/* 3899 */     if (this.withoutUnicodeBuffer == null) this.withoutUnicodeBuffer = new char[10];
/* 3900 */     int length = this.withoutUnicodeBuffer.length;
/* 3901 */     if (pos == length) {
/* 3902 */       System.arraycopy(this.withoutUnicodeBuffer, 0, this.withoutUnicodeBuffer = new char[length * 2], 0, length);
/*      */     }
/* 3904 */     this.withoutUnicodeBuffer[pos] = this.currentCharacter;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.Scanner
 * JD-Core Version:    0.6.0
 */