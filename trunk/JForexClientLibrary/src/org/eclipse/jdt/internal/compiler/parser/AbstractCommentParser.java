/*      */ package org.eclipse.jdt.internal.compiler.parser;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.core.compiler.InvalidInputException;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public abstract class AbstractCommentParser
/*      */   implements JavadocTagConstants
/*      */ {
/*      */   public static final int COMPIL_PARSER = 1;
/*      */   public static final int DOM_PARSER = 2;
/*      */   public static final int SELECTION_PARSER = 4;
/*      */   public static final int COMPLETION_PARSER = 8;
/*      */   public static final int SOURCE_PARSER = 16;
/*      */   public static final int FORMATTER_COMMENT_PARSER = 32;
/*      */   protected static final int PARSER_KIND = 255;
/*      */   protected static final int TEXT_PARSE = 256;
/*      */   protected static final int TEXT_VERIF = 512;
/*      */   protected static final int QUALIFIED_NAME_RECOVERY = 1;
/*      */   protected static final int ARGUMENT_RECOVERY = 2;
/*      */   protected static final int ARGUMENT_TYPE_RECOVERY = 3;
/*      */   protected static final int EMPTY_ARGUMENT_RECOVERY = 4;
/*      */   public Scanner scanner;
/*      */   public char[] source;
/*      */   protected Parser sourceParser;
/*   47 */   private int currentTokenType = -1;
/*      */ 
/*   50 */   public boolean checkDocComment = false;
/*      */   public boolean reportProblems;
/*      */   protected long complianceLevel;
/*      */   protected long sourceLevel;
/*      */   protected long[] inheritedPositions;
/*      */   protected int inheritedPositionsPtr;
/*      */   private static final int INHERITED_POSITIONS_ARRAY_INCREMENT = 4;
/*      */   protected boolean deprecated;
/*      */   protected Object returnStatement;
/*      */   protected int javadocStart;
/*      */   protected int javadocEnd;
/*      */   protected int javadocTextStart;
/*   66 */   protected int javadocTextEnd = -1;
/*      */   protected int firstTagPosition;
/*      */   protected int index;
/*      */   protected int lineEnd;
/*      */   protected int tokenPreviousPosition;
/*      */   protected int lastIdentifierEndPosition;
/*      */   protected int starPosition;
/*      */   protected int textStart;
/*      */   protected int memberStart;
/*      */   protected int tagSourceStart;
/*      */   protected int tagSourceEnd;
/*      */   protected int inlineTagStart;
/*      */   protected int[] lineEnds;
/*   76 */   protected boolean lineStarted = false;
/*   77 */   protected boolean inlineTagStarted = false;
/*   78 */   protected boolean abort = false;
/*      */   protected int kind;
/*   80 */   protected int tagValue = 0;
/*   81 */   protected int lastBlockTagValue = 0;
/*      */   private int linePtr;
/*      */   private int lastLinePtr;
/*      */   protected int identifierPtr;
/*      */   protected char[][] identifierStack;
/*      */   protected int identifierLengthPtr;
/*      */   protected int[] identifierLengthStack;
/*      */   protected long[] identifierPositionStack;
/*      */   protected static final int AST_STACK_INCREMENT = 10;
/*      */   protected int astPtr;
/*      */   protected Object[] astStack;
/*      */   protected int astLengthPtr;
/*      */   protected int[] astLengthStack;
/*      */ 
/*      */   protected AbstractCommentParser(Parser sourceParser)
/*      */   {
/*  102 */     this.sourceParser = sourceParser;
/*  103 */     this.scanner = new Scanner(false, false, false, 3080192L, null, null, true);
/*  104 */     this.identifierStack = new char[20][];
/*  105 */     this.identifierPositionStack = new long[20];
/*  106 */     this.identifierLengthStack = new int[10];
/*  107 */     this.astStack = new Object[30];
/*  108 */     this.astLengthStack = new int[20];
/*  109 */     this.reportProblems = (sourceParser != null);
/*  110 */     if (sourceParser != null) {
/*  111 */       this.checkDocComment = this.sourceParser.options.docCommentSupport;
/*  112 */       this.sourceLevel = this.sourceParser.options.sourceLevel;
/*  113 */       this.scanner.sourceLevel = this.sourceLevel;
/*  114 */       this.complianceLevel = this.sourceParser.options.complianceLevel;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean commentParse()
/*      */   {
/*  126 */     boolean validComment = true;
/*      */     try
/*      */     {
/*  129 */       this.astLengthPtr = -1;
/*  130 */       this.astPtr = -1;
/*  131 */       this.identifierPtr = -1;
/*  132 */       this.currentTokenType = -1;
/*  133 */       this.inlineTagStarted = false;
/*  134 */       this.inlineTagStart = -1;
/*  135 */       this.lineStarted = false;
/*  136 */       this.returnStatement = null;
/*  137 */       this.inheritedPositions = null;
/*  138 */       this.lastBlockTagValue = 0;
/*  139 */       this.deprecated = false;
/*  140 */       this.lastLinePtr = getLineNumber(this.javadocEnd);
/*  141 */       this.textStart = -1;
/*  142 */       this.abort = false;
/*  143 */       char previousChar = '\000';
/*  144 */       int invalidTagLineEnd = -1;
/*  145 */       int invalidInlineTagLineEnd = -1;
/*  146 */       boolean lineHasStar = true;
/*  147 */       boolean verifText = (this.kind & 0x200) != 0;
/*  148 */       boolean isDomParser = (this.kind & 0x2) != 0;
/*  149 */       boolean isFormatterParser = (this.kind & 0x20) != 0;
/*      */ 
/*  152 */       this.linePtr = getLineNumber(this.firstTagPosition);
/*  153 */       int realStart = this.linePtr == 1 ? this.javadocStart : this.scanner.getLineEnd(this.linePtr - 1) + 1;
/*  154 */       if (realStart < this.javadocStart) realStart = this.javadocStart;
/*  155 */       this.scanner.resetTo(realStart, this.javadocEnd);
/*  156 */       this.index = realStart;
/*  157 */       if (realStart == this.javadocStart) {
/*  158 */         readChar();
/*  159 */         readChar();
/*      */       }
/*  161 */       int previousPosition = this.index;
/*  162 */       char nextCharacter = '\000';
/*  163 */       if (realStart == this.javadocStart) {
/*  164 */         nextCharacter = readChar();
/*  165 */         while (peekChar() == '*') {
/*  166 */           nextCharacter = readChar();
/*      */         }
/*  168 */         this.javadocTextStart = this.index;
/*      */       }
/*  170 */       this.lineEnd = (this.linePtr == this.lastLinePtr ? this.javadocEnd : this.scanner.getLineEnd(this.linePtr) - 1);
/*  171 */       this.javadocTextEnd = (this.javadocEnd - 2);
/*      */ 
/*  174 */       int textEndPosition = -1;
/*  175 */       label845: while ((!this.abort) && (this.index < this.javadocEnd))
/*      */       {
/*  178 */         previousPosition = this.index;
/*  179 */         previousChar = nextCharacter;
/*      */ 
/*  182 */         if (this.index > this.lineEnd + 1) {
/*  183 */           updateLineEnd();
/*      */         }
/*      */ 
/*  187 */         if (this.currentTokenType < 0) {
/*  188 */           nextCharacter = readChar();
/*      */         } else {
/*  190 */           previousPosition = this.scanner.getCurrentTokenStartPosition();
/*  191 */           switch (this.currentTokenType) {
/*      */           case 31:
/*  193 */             nextCharacter = '}';
/*  194 */             break;
/*      */           case 4:
/*  196 */             nextCharacter = '*';
/*  197 */             break;
/*      */           default:
/*  199 */             nextCharacter = this.scanner.currentCharacter;
/*      */           }
/*  201 */           consumeToken();
/*      */         }
/*      */ 
/*  205 */         switch (nextCharacter)
/*      */         {
/*      */         case '@':
/*  208 */           if ((!this.lineStarted) || (previousChar == '{')) {
/*  209 */             if (this.inlineTagStarted) {
/*  210 */               this.inlineTagStarted = false;
/*      */ 
/*  213 */               if (this.reportProblems) {
/*  214 */                 int end = previousPosition < invalidInlineTagLineEnd ? previousPosition : invalidInlineTagLineEnd;
/*  215 */                 this.sourceParser.problemReporter().javadocUnterminatedInlineTag(this.inlineTagStart, end);
/*      */               }
/*  217 */               validComment = false;
/*  218 */               if ((this.textStart != -1) && (this.textStart < textEndPosition)) {
/*  219 */                 pushText(this.textStart, textEndPosition);
/*      */               }
/*  221 */               if ((isDomParser) || (isFormatterParser)) {
/*  222 */                 refreshInlineTagPosition(textEndPosition);
/*      */               }
/*      */             }
/*  225 */             if (previousChar == '{') {
/*  226 */               if ((this.textStart != -1) && 
/*  227 */                 (this.textStart < textEndPosition)) {
/*  228 */                 pushText(this.textStart, textEndPosition);
/*      */               }
/*      */ 
/*  231 */               this.inlineTagStarted = true;
/*  232 */               invalidInlineTagLineEnd = this.lineEnd;
/*  233 */             } else if ((this.textStart != -1) && (this.textStart < invalidTagLineEnd)) {
/*  234 */               pushText(this.textStart, invalidTagLineEnd);
/*      */             }
/*  236 */             this.scanner.resetTo(this.index, this.javadocEnd);
/*  237 */             this.currentTokenType = -1;
/*      */             try {
/*  239 */               if (parseTag(previousPosition))
/*      */                 break label845;
/*  242 */               validComment = false;
/*      */ 
/*  245 */               if (isDomParser) {
/*  246 */                 createTag();
/*      */               }
/*  248 */               this.textStart = (this.tagSourceEnd + 1);
/*  249 */               invalidTagLineEnd = this.lineEnd;
/*  250 */               textEndPosition = this.index;
/*      */             }
/*      */             catch (InvalidInputException localInvalidInputException) {
/*  253 */               consumeToken();
/*      */             }
/*      */           } else {
/*  256 */             textEndPosition = this.index;
/*  257 */             if ((verifText) && (this.tagValue == 3) && (this.returnStatement != null))
/*  258 */               refreshReturnStatement();
/*  259 */             else if ((isFormatterParser) && 
/*  260 */               (this.textStart == -1)) this.textStart = previousPosition;
/*      */           }
/*      */ 
/*  263 */           this.lineStarted = true;
/*  264 */           break;
/*      */         case '\n':
/*      */         case '\r':
/*  267 */           if (this.lineStarted) {
/*  268 */             if ((isFormatterParser) && (!ScannerHelper.isWhitespace(previousChar))) {
/*  269 */               textEndPosition = previousPosition;
/*      */             }
/*  271 */             if ((this.textStart != -1) && (this.textStart < textEndPosition)) {
/*  272 */               pushText(this.textStart, textEndPosition);
/*      */             }
/*      */           }
/*  275 */           this.lineStarted = false;
/*  276 */           lineHasStar = false;
/*      */ 
/*  278 */           this.textStart = -1;
/*  279 */           break;
/*      */         case '}':
/*  281 */           if ((verifText) && (this.tagValue == 3) && (this.returnStatement != null)) {
/*  282 */             refreshReturnStatement();
/*      */           }
/*  284 */           if (this.inlineTagStarted) {
/*  285 */             if ((this.lineStarted) && (this.textStart != -1) && (this.textStart < textEndPosition)) {
/*  286 */               pushText(this.textStart, textEndPosition);
/*      */             }
/*  288 */             refreshInlineTagPosition(previousPosition);
/*  289 */             if (!isFormatterParser) this.textStart = this.index;
/*  290 */             this.inlineTagStarted = false;
/*      */           }
/*  292 */           else if (!this.lineStarted) {
/*  293 */             this.textStart = previousPosition;
/*      */           }
/*      */ 
/*  296 */           this.lineStarted = true;
/*  297 */           textEndPosition = this.index;
/*  298 */           break;
/*      */         case '{':
/*  300 */           if ((verifText) && (this.tagValue == 3) && (this.returnStatement != null)) {
/*  301 */             refreshReturnStatement();
/*      */           }
/*  303 */           if (this.inlineTagStarted) {
/*  304 */             this.inlineTagStarted = false;
/*      */ 
/*  307 */             if (this.reportProblems) {
/*  308 */               int end = previousPosition < invalidInlineTagLineEnd ? previousPosition : invalidInlineTagLineEnd;
/*  309 */               this.sourceParser.problemReporter().javadocUnterminatedInlineTag(this.inlineTagStart, end);
/*      */             }
/*  311 */             if ((this.lineStarted) && (this.textStart != -1) && (this.textStart < textEndPosition)) {
/*  312 */               pushText(this.textStart, textEndPosition);
/*      */             }
/*  314 */             refreshInlineTagPosition(textEndPosition);
/*  315 */             textEndPosition = this.index;
/*  316 */           } else if (peekChar() != '@') {
/*  317 */             if (this.textStart == -1) this.textStart = previousPosition;
/*  318 */             textEndPosition = this.index;
/*      */           }
/*  320 */           if (!this.lineStarted) {
/*  321 */             this.textStart = previousPosition;
/*      */           }
/*  323 */           this.lineStarted = true;
/*  324 */           this.inlineTagStart = previousPosition;
/*  325 */           break;
/*      */         case '*':
/*  328 */           if (previousChar == '*') continue;
/*  329 */           this.starPosition = previousPosition;
/*  330 */           if ((!isDomParser) && (!isFormatterParser)) continue;
/*  331 */           if (lineHasStar) {
/*  332 */             this.lineStarted = true;
/*  333 */             if (this.textStart == -1) {
/*  334 */               this.textStart = previousPosition;
/*  335 */               if (this.index <= this.javadocTextEnd) textEndPosition = this.index;
/*      */             }
/*      */           }
/*  338 */           if (this.lineStarted) continue;
/*  339 */           lineHasStar = true;
/*      */ 
/*  343 */           break;
/*      */         case '\t':
/*      */         case '\f':
/*      */         case ' ':
/*  348 */           if (isFormatterParser) {
/*  349 */             if (ScannerHelper.isWhitespace(previousChar)) continue;
/*  350 */             textEndPosition = previousPosition;
/*      */           } else {
/*  352 */             if ((!this.lineStarted) || (!isDomParser)) continue;
/*  353 */             textEndPosition = this.index;
/*      */           }
/*  355 */           break;
/*      */         case '/':
/*  357 */           if (previousChar == '*')
/*      */           {
/*      */             continue;
/*      */           }
/*      */ 
/*      */         default:
/*  363 */           if ((isFormatterParser) && (nextCharacter == '<'))
/*      */           {
/*  365 */             int initialIndex = this.index;
/*  366 */             this.scanner.resetTo(this.index, this.javadocEnd);
/*  367 */             if (!ScannerHelper.isWhitespace(previousChar)) {
/*  368 */               textEndPosition = previousPosition;
/*      */             }
/*  370 */             if (parseHtmlTag(previousPosition, textEndPosition)) {
/*      */               continue;
/*      */             }
/*  373 */             if (this.abort) return false;
/*      */ 
/*  375 */             this.scanner.currentPosition = initialIndex;
/*  376 */             this.index = initialIndex;
/*      */           }
/*  378 */           if ((verifText) && (this.tagValue == 3) && (this.returnStatement != null)) {
/*  379 */             refreshReturnStatement();
/*      */           }
/*  381 */           if ((!this.lineStarted) || (this.textStart == -1)) {
/*  382 */             this.textStart = previousPosition;
/*      */           }
/*  384 */           this.lineStarted = true;
/*  385 */           textEndPosition = this.index;
/*      */         }
/*      */       }
/*      */ 
/*  389 */       this.javadocTextEnd = (this.starPosition - 1);
/*      */ 
/*  393 */       if (this.inlineTagStarted) {
/*  394 */         if (this.reportProblems) {
/*  395 */           int end = this.javadocTextEnd < invalidInlineTagLineEnd ? this.javadocTextEnd : invalidInlineTagLineEnd;
/*  396 */           if (this.index >= this.javadocEnd) end = invalidInlineTagLineEnd;
/*  397 */           this.sourceParser.problemReporter().javadocUnterminatedInlineTag(this.inlineTagStart, end);
/*      */         }
/*  399 */         if ((this.lineStarted) && (this.textStart != -1) && (this.textStart < textEndPosition)) {
/*  400 */           pushText(this.textStart, textEndPosition);
/*      */         }
/*  402 */         refreshInlineTagPosition(textEndPosition);
/*  403 */         this.inlineTagStarted = false;
/*  404 */       } else if ((this.lineStarted) && (this.textStart != -1) && (this.textStart <= textEndPosition)) {
/*  405 */         pushText(this.textStart, textEndPosition);
/*      */       }
/*  407 */       updateDocComment();
/*      */     } catch (Exception localException) {
/*  409 */       validComment = false;
/*      */     }
/*  411 */     return validComment;
/*      */   }
/*      */ 
/*      */   protected void consumeToken() {
/*  415 */     this.currentTokenType = -1;
/*  416 */     updateLineEnd();
/*      */   }
/*      */   protected abstract Object createArgumentReference(char[] paramArrayOfChar, int paramInt, boolean paramBoolean, Object paramObject, long[] paramArrayOfLong, long paramLong) throws InvalidInputException;
/*      */ 
/*      */   protected boolean createFakeReference(int start) {
/*  422 */     return true; } 
/*      */   protected abstract Object createFieldReference(Object paramObject) throws InvalidInputException;
/*      */ 
/*      */   protected abstract Object createMethodReference(Object paramObject, List paramList) throws InvalidInputException;
/*      */ 
/*  426 */   protected Object createReturnStatement() { return null; } 
/*      */   protected abstract void createTag();
/*      */ 
/*      */   protected abstract Object createTypeReference(int paramInt);
/*      */ 
/*  431 */   private int getIndexPosition() { if (this.index > this.lineEnd) {
/*  432 */       return this.lineEnd;
/*      */     }
/*  434 */     return this.index - 1;
/*      */   }
/*      */ 
/*      */   private int getLineNumber(int position)
/*      */   {
/*  446 */     if (this.scanner.linePtr != -1) {
/*  447 */       return Util.getLineNumber(position, this.scanner.lineEnds, 0, this.scanner.linePtr);
/*      */     }
/*  449 */     if (this.lineEnds == null)
/*  450 */       return 1;
/*  451 */     return Util.getLineNumber(position, this.lineEnds, 0, this.lineEnds.length - 1);
/*      */   }
/*      */ 
/*      */   private int getTokenEndPosition() {
/*  455 */     if (this.scanner.getCurrentTokenEndPosition() > this.lineEnd) {
/*  456 */       return this.lineEnd;
/*      */     }
/*  458 */     return this.scanner.getCurrentTokenEndPosition();
/*      */   }
/*      */ 
/*      */   protected int getCurrentTokenType()
/*      */   {
/*  466 */     return this.currentTokenType;
/*      */   }
/*      */ 
/*      */   protected Object parseArguments(Object receiver)
/*      */     throws InvalidInputException
/*      */   {
/*  475 */     int modulo = 0;
/*  476 */     int iToken = 0;
/*  477 */     char[] argName = (char[])null;
/*  478 */     List arguments = new ArrayList(10);
/*  479 */     int start = this.scanner.getCurrentTokenStartPosition();
/*  480 */     Object typeRef = null;
/*  481 */     int dim = 0;
/*  482 */     boolean isVarargs = false;
/*  483 */     long[] dimPositions = new long[20];
/*  484 */     char[] name = (char[])null;
/*  485 */     long argNamePos = -1L;
/*      */ 
/*  488 */     while (this.index < this.scanner.eofPosition)
/*      */     {
/*      */       try
/*      */       {
/*  492 */         typeRef = parseQualifiedName(false);
/*  493 */         if (this.abort) return null; 
/*      */       }
/*      */       catch (InvalidInputException localInvalidInputException)
/*      */       {
/*      */       }
/*  497 */       boolean firstArg = modulo == 0;
/*  498 */       if (firstArg ? 
/*  499 */         iToken != 0 : 
/*  501 */         iToken % modulo != 0) {
/*      */         break;
/*      */       }
/*  504 */       if (typeRef == null) {
/*  505 */         if ((!firstArg) || (this.currentTokenType != 29))
/*      */           break;
/*  507 */         if (!verifySpaceOrEndComment()) {
/*  508 */           int end = this.starPosition == -1 ? this.lineEnd : this.starPosition;
/*  509 */           if (this.source[end] == '\n') end--;
/*  510 */           if (this.reportProblems) this.sourceParser.problemReporter().javadocMalformedSeeReference(start, end);
/*  511 */           return null;
/*      */         }
/*  513 */         this.lineStarted = true;
/*  514 */         return createMethodReference(receiver, null);
/*      */       }
/*      */ 
/*  518 */       iToken++;
/*      */ 
/*  521 */       dim = 0;
/*  522 */       isVarargs = false;
/*  523 */       if (readToken() == 14)
/*      */       {
/*  525 */         int dimStart = this.scanner.getCurrentTokenStartPosition();
/*  526 */         while (readToken() == 14) {
/*  527 */           consumeToken();
/*  528 */           if (readToken() != 70) {
/*      */             break;
/*      */           }
/*  531 */           consumeToken();
/*  532 */           dimPositions[(dim++)] = ((dimStart << 32) + this.scanner.getCurrentTokenEndPosition());
/*      */         }
/*  534 */       } else if (readToken() == 107)
/*      */       {
/*  536 */         int dimStart = this.scanner.getCurrentTokenStartPosition();
/*  537 */         dimPositions[(dim++)] = ((dimStart << 32) + this.scanner.getCurrentTokenEndPosition());
/*  538 */         consumeToken();
/*  539 */         isVarargs = true;
/*      */       }
/*      */ 
/*  543 */       argNamePos = -1L;
/*  544 */       if (readToken() == 26) {
/*  545 */         consumeToken();
/*  546 */         if (firstArg ? 
/*  547 */           iToken != 1 : 
/*  549 */           iToken % modulo != 1) {
/*      */           break;
/*      */         }
/*  552 */         if ((argName == null) && 
/*  553 */           (!firstArg))
/*      */         {
/*      */           break;
/*      */         }
/*  557 */         argName = this.scanner.getCurrentIdentifierSource();
/*  558 */         argNamePos = (this.scanner.getCurrentTokenStartPosition() << 32) + this.scanner.getCurrentTokenEndPosition();
/*  559 */         iToken++; } else {
/*  560 */         if (argName != null)
/*      */         {
/*      */           break;
/*      */         }
/*      */       }
/*  565 */       if (firstArg)
/*  566 */         modulo = iToken + 1;
/*      */       else {
/*  568 */         if (iToken % modulo != modulo - 1)
/*      */         {
/*      */           break;
/*      */         }
/*      */       }
/*      */ 
/*  574 */       int token = readToken();
/*  575 */       name = argName == null ? CharOperation.NO_CHAR : argName;
/*  576 */       if (token == 30)
/*      */       {
/*  578 */         Object argument = createArgumentReference(name, dim, isVarargs, typeRef, dimPositions, argNamePos);
/*  579 */         if (this.abort) return null;
/*  580 */         arguments.add(argument);
/*  581 */         consumeToken();
/*  582 */         iToken++; } else {
/*  583 */         if (token != 29)
/*      */           break;
/*  585 */         if (!verifySpaceOrEndComment()) {
/*  586 */           int end = this.starPosition == -1 ? this.lineEnd : this.starPosition;
/*  587 */           if (this.source[end] == '\n') end--;
/*  588 */           if (this.reportProblems) this.sourceParser.problemReporter().javadocMalformedSeeReference(start, end);
/*  589 */           return null;
/*      */         }
/*      */ 
/*  592 */         Object argument = createArgumentReference(name, dim, isVarargs, typeRef, dimPositions, argNamePos);
/*  593 */         if (this.abort) return null;
/*  594 */         arguments.add(argument);
/*  595 */         consumeToken();
/*  596 */         return createMethodReference(receiver, arguments);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  603 */     throw new InvalidInputException();
/*      */   }
/*      */ 
/*      */   protected boolean parseHtmlTag(int previousPosition, int endTextPosition)
/*      */     throws InvalidInputException
/*      */   {
/*  623 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean parseHref()
/*      */     throws InvalidInputException
/*      */   {
/*  630 */     boolean skipComments = this.scanner.skipComments;
/*  631 */     this.scanner.skipComments = true;
/*      */     try {
/*  633 */       int start = this.scanner.getCurrentTokenStartPosition();
/*  634 */       char currentChar = readChar();
/*  635 */       if ((currentChar == 'a') || (currentChar == 'A')) {
/*  636 */         this.scanner.currentPosition = this.index;
/*  637 */         if (readToken() == 26) {
/*  638 */           consumeToken();
/*      */           try {
/*  640 */             if ((CharOperation.equals(this.scanner.getCurrentIdentifierSource(), HREF_TAG, false)) && 
/*  641 */               (readToken() == 71)) {
/*  642 */               consumeToken();
/*  643 */               if (readToken() == 52) {
/*  644 */                 consumeToken();
/*  645 */                 while (this.index < this.javadocEnd)
/*      */                 {
/*  647 */                   while (readToken() != 13) {
/*  648 */                     if ((this.scanner.currentPosition >= this.scanner.eofPosition) || (this.scanner.currentCharacter == '@') || (
/*  649 */                       (this.inlineTagStarted) && (this.scanner.currentCharacter == '}')))
/*      */                     {
/*  651 */                       this.index = this.tokenPreviousPosition;
/*  652 */                       this.scanner.currentPosition = this.tokenPreviousPosition;
/*  653 */                       this.currentTokenType = -1;
/*      */ 
/*  655 */                       if ((this.tagValue != 10) && 
/*  656 */                         (this.reportProblems)) this.sourceParser.problemReporter().javadocInvalidSeeHref(start, this.lineEnd);
/*      */ 
/*  713 */                       this.scanner.skipComments = skipComments;
/*      */ 
/*  658 */                       return false;
/*      */                     }
/*  660 */                     this.currentTokenType = -1;
/*      */                   }
/*  662 */                   consumeToken();
/*  663 */                   while (readToken() != 7) {
/*  664 */                     if ((this.scanner.currentPosition >= this.scanner.eofPosition) || (this.scanner.currentCharacter == '@') || (
/*  665 */                       (this.inlineTagStarted) && (this.scanner.currentCharacter == '}')))
/*      */                     {
/*  667 */                       this.index = this.tokenPreviousPosition;
/*  668 */                       this.scanner.currentPosition = this.tokenPreviousPosition;
/*  669 */                       this.currentTokenType = -1;
/*      */ 
/*  671 */                       if ((this.tagValue == 10) || 
/*  672 */                         (!this.reportProblems)) break; this.sourceParser.problemReporter().javadocInvalidSeeHref(start, this.lineEnd);
/*      */ 
/*  674 */                       break;
/*      */                     }
/*  676 */                     consumeToken();
/*      */                   }
/*  678 */                   consumeToken();
/*  679 */                   start = this.scanner.getCurrentTokenStartPosition();
/*  680 */                   currentChar = readChar();
/*      */ 
/*  682 */                   if (currentChar == '/') {
/*  683 */                     currentChar = readChar();
/*  684 */                     if ((currentChar == 'a') || (currentChar == 'A')) {
/*  685 */                       currentChar = readChar();
/*  686 */                       if (currentChar == '>')
/*      */                       {
/*  713 */                         this.scanner.skipComments = skipComments;
/*      */ 
/*  687 */                         return true;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */ 
/*  692 */                   if ((currentChar == '\r') || (currentChar == '\n') || (currentChar == '\t') || (currentChar == ' ')) {
/*      */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (InvalidInputException localInvalidInputException)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*  704 */       this.index = this.tokenPreviousPosition;
/*  705 */       this.scanner.currentPosition = this.tokenPreviousPosition;
/*  706 */       this.currentTokenType = -1;
/*      */ 
/*  708 */       if ((this.tagValue != 10) && 
/*  709 */         (this.reportProblems)) this.sourceParser.problemReporter().javadocInvalidSeeHref(start, this.lineEnd);
/*      */     }
/*      */     finally
/*      */     {
/*  713 */       this.scanner.skipComments = skipComments; } this.scanner.skipComments = skipComments;
/*      */ 
/*  715 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean parseIdentifierTag(boolean report)
/*      */   {
/*  722 */     int token = readTokenSafely();
/*  723 */     switch (token) {
/*      */     case 26:
/*  725 */       pushIdentifier(true, false);
/*  726 */       return true;
/*      */     }
/*  728 */     if (report) {
/*  729 */       this.sourceParser.problemReporter().javadocMissingIdentifier(this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
/*      */     }
/*  731 */     return false;
/*      */   }
/*      */ 
/*      */   protected Object parseMember(Object receiver)
/*      */     throws InvalidInputException
/*      */   {
/*  739 */     this.identifierPtr = -1;
/*  740 */     this.identifierLengthPtr = -1;
/*  741 */     int start = this.scanner.getCurrentTokenStartPosition();
/*  742 */     this.memberStart = start;
/*      */ 
/*  745 */     if (readToken() == 26) {
/*  746 */       if (this.scanner.currentCharacter == '.') {
/*  747 */         parseQualifiedName(true);
/*      */       } else {
/*  749 */         consumeToken();
/*  750 */         pushIdentifier(true, false);
/*      */       }
/*      */ 
/*  753 */       int previousPosition = this.index;
/*  754 */       if (readToken() == 28) {
/*  755 */         consumeToken();
/*  756 */         start = this.scanner.getCurrentTokenStartPosition();
/*      */         try {
/*  758 */           return parseArguments(receiver);
/*      */         } catch (InvalidInputException localInvalidInputException) {
/*  760 */           int end = this.scanner.getCurrentTokenEndPosition() < this.lineEnd ? 
/*  761 */             this.scanner.getCurrentTokenEndPosition() : 
/*  762 */             this.scanner.getCurrentTokenStartPosition();
/*  763 */           end = end < this.lineEnd ? end : this.lineEnd;
/*  764 */           if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidSeeReferenceArgs(start, end);
/*      */ 
/*  766 */           return null;
/*      */         }
/*      */       }
/*      */ 
/*  770 */       this.index = previousPosition;
/*  771 */       this.scanner.currentPosition = previousPosition;
/*  772 */       this.currentTokenType = -1;
/*      */ 
/*  775 */       if (!verifySpaceOrEndComment()) {
/*  776 */         int end = this.starPosition == -1 ? this.lineEnd : this.starPosition;
/*  777 */         if (this.source[end] == '\n') end--;
/*  778 */         if (this.reportProblems) this.sourceParser.problemReporter().javadocMalformedSeeReference(start, end);
/*  779 */         return null;
/*      */       }
/*  781 */       return createFieldReference(receiver);
/*      */     }
/*  783 */     int end = getTokenEndPosition() - 1;
/*  784 */     end = start > end ? start : end;
/*  785 */     if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidReference(start, end);
/*      */ 
/*  787 */     this.index = this.tokenPreviousPosition;
/*  788 */     this.scanner.currentPosition = this.tokenPreviousPosition;
/*  789 */     this.currentTokenType = -1;
/*  790 */     return null;
/*      */   }
/*      */ 
/*      */   protected boolean parseParam()
/*      */     throws InvalidInputException
/*      */   {
/*  799 */     int start = this.tagSourceStart;
/*  800 */     int end = this.tagSourceEnd;
/*  801 */     boolean tokenWhiteSpace = this.scanner.tokenizeWhiteSpace;
/*  802 */     this.scanner.tokenizeWhiteSpace = true;
/*      */     try
/*      */     {
/*  806 */       boolean isCompletionParser = (this.kind & 0x8) != 0;
/*  807 */       if ((this.scanner.currentCharacter != ' ') && (!ScannerHelper.isWhitespace(this.scanner.currentCharacter))) {
/*  808 */         if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidTag(start, this.scanner.getCurrentTokenEndPosition());
/*  809 */         if (!isCompletionParser) {
/*  810 */           this.scanner.currentPosition = start;
/*  811 */           this.index = start;
/*      */         }
/*  813 */         this.currentTokenType = -1;
/*      */         return false;
/*      */       }
/*      */ 
/*  818 */       this.identifierPtr = -1;
/*  819 */       this.identifierLengthPtr = -1;
/*  820 */       boolean hasMultiLines = this.scanner.currentPosition > this.lineEnd + 1;
/*  821 */       boolean isTypeParam = false;
/*  822 */       boolean valid = true; boolean empty = true;
/*  823 */       boolean mayBeGeneric = this.sourceLevel >= 3211264L;
/*  824 */       int token = -1;
/*      */       while (true) {
/*  826 */         this.currentTokenType = -1;
/*      */         try {
/*  828 */           token = readToken();
/*      */         } catch (InvalidInputException localInvalidInputException1) {
/*  830 */           valid = false;
/*      */         }
/*  832 */         switch (token) {
/*      */         case 26:
/*  834 */           if (valid)
/*      */           {
/*  836 */             pushIdentifier(true, false);
/*  837 */             start = this.scanner.getCurrentTokenStartPosition();
/*  838 */             end = hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
/*  839 */           }break;
/*      */         case 7:
/*  843 */           if ((valid) && (mayBeGeneric))
/*      */           {
/*  845 */             pushIdentifier(true, true);
/*  846 */             start = this.scanner.getCurrentTokenStartPosition();
/*  847 */             end = hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
/*  848 */             isTypeParam = true;
/*  849 */           }break;
/*      */         default:
/*  853 */           if (token == 17) isTypeParam = true;
/*  854 */           if ((valid) && (!hasMultiLines)) start = this.scanner.getCurrentTokenStartPosition();
/*  855 */           valid = false;
/*  856 */           if (!hasMultiLines) {
/*  857 */             empty = false;
/*  858 */             end = hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
/*  859 */             continue;
/*      */           }
/*  861 */           end = this.lineEnd;
/*      */         case 1000:
/*  864 */           if (this.scanner.currentPosition > this.lineEnd + 1) hasMultiLines = true;
/*  865 */           if (valid)
/*      */             continue;
/*      */         case 68:
/*  868 */           if (this.reportProblems)
/*  869 */             if (empty)
/*  870 */               this.sourceParser.problemReporter().javadocMissingParamName(start, end, this.sourceParser.modifiers);
/*  871 */             else if ((mayBeGeneric) && (isTypeParam))
/*  872 */               this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
/*      */             else
/*  874 */               this.sourceParser.problemReporter().javadocInvalidParamTagName(start, end);
/*  875 */           if (!isCompletionParser) {
/*  876 */             this.scanner.currentPosition = start;
/*  877 */             this.index = start;
/*      */           }
/*  879 */           this.currentTokenType = -1;
/*      */           return false;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  885 */       if ((isTypeParam) && (mayBeGeneric))
/*      */       {
/*      */         while (true) {
/*  888 */           this.currentTokenType = -1;
/*      */           try {
/*  890 */             token = readToken();
/*      */           } catch (InvalidInputException localInvalidInputException2) {
/*  892 */             valid = false;
/*      */           }
/*  894 */           switch (token) {
/*      */           case 1000:
/*  896 */             if ((valid) && (this.scanner.currentPosition <= this.lineEnd + 1))
/*      */             {
/*      */               continue;
/*      */             }
/*      */           case 68:
/*  901 */             if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
/*  902 */             if (!isCompletionParser) {
/*  903 */               this.scanner.currentPosition = start;
/*  904 */               this.index = start;
/*      */             }
/*  906 */             this.currentTokenType = -1;
/*      */             return false;
/*      */           case 26:
/*  909 */             end = hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
/*  910 */             if (!valid)
/*      */               continue;
/*  912 */             pushIdentifier(false, false);
/*  913 */             break;
/*      */           default:
/*  917 */             end = hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
/*  918 */             valid = false;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  924 */         boolean spaces = false;
/*      */         while (true) {
/*  926 */           this.currentTokenType = -1;
/*      */           try {
/*  928 */             token = readToken();
/*      */           } catch (InvalidInputException localInvalidInputException3) {
/*  930 */             valid = false;
/*      */           }
/*  932 */           switch (token) {
/*      */           case 1000:
/*  934 */             if (this.scanner.currentPosition > this.lineEnd + 1)
/*      */             {
/*  936 */               hasMultiLines = true;
/*  937 */               valid = false;
/*      */             }
/*  939 */             spaces = true;
/*  940 */             if (valid)
/*      */               continue;
/*      */           case 68:
/*  943 */             if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
/*  944 */             if (!isCompletionParser) {
/*  945 */               this.scanner.currentPosition = start;
/*  946 */               this.index = start;
/*      */             }
/*  948 */             this.currentTokenType = -1;
/*      */             return false;
/*      */           case 13:
/*  951 */             end = hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
/*  952 */             if (!valid)
/*      */               continue;
/*  954 */             pushIdentifier(false, true);
/*  955 */             break;
/*      */           default:
/*  959 */             if (!spaces) end = hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
/*  960 */             valid = false;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  967 */       if (valid) {
/*  968 */         this.currentTokenType = -1;
/*  969 */         int restart = this.scanner.currentPosition;
/*      */         try {
/*  971 */           token = readTokenAndConsume();
/*      */         } catch (InvalidInputException localInvalidInputException4) {
/*  973 */           valid = false;
/*      */         }
/*  975 */         if (token == 1000) {
/*  976 */           this.scanner.resetTo(restart, this.javadocEnd);
/*  977 */           this.index = restart;
/*  978 */           boolean bool1 = pushParamName(isTypeParam);
/*      */           return bool1;
/*      */         }
/*      */       }
/*      */ 
/*  982 */       this.currentTokenType = -1;
/*  983 */       if (isCompletionParser)
/*      */         return false;
/*  984 */       end = hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
/*  985 */       while (((token = readToken()) != 1000) && (token != 68)) {
/*  986 */         this.currentTokenType = -1;
/*  987 */         end = hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
/*      */       }
/*  989 */       if (this.reportProblems)
/*  990 */         if ((mayBeGeneric) && (isTypeParam))
/*  991 */           this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
/*      */         else
/*  993 */           this.sourceParser.problemReporter().javadocInvalidParamTagName(start, end);
/*  994 */       this.scanner.currentPosition = start;
/*  995 */       this.index = start;
/*  996 */       this.currentTokenType = -1;
/*      */       return false;
/*      */     }
/*      */     finally {
/* 1000 */       this.scanner.tokenizeWhiteSpace = tokenWhiteSpace;
/* 1001 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   protected Object parseQualifiedName(boolean reset)
/*      */     throws InvalidInputException
/*      */   {
/* 1010 */     if (reset) {
/* 1011 */       this.identifierPtr = -1;
/* 1012 */       this.identifierLengthPtr = -1;
/*      */     }
/*      */ 
/* 1016 */     int primitiveToken = -1;
/* 1017 */     int parserKind = this.kind & 0xFF;
/* 1018 */     for (int iToken = 0; ; iToken++) {
/* 1019 */       int token = readTokenSafely();
/* 1020 */       switch (token) {
/*      */       case 26:
/* 1022 */         if ((iToken & 0x1) != 0)
/*      */           break label353;
/* 1025 */         pushIdentifier(iToken == 0, false);
/* 1026 */         consumeToken();
/* 1027 */         break;
/*      */       case 3:
/* 1030 */         if ((iToken & 0x1) == 0) {
/* 1031 */           throw new InvalidInputException();
/*      */         }
/* 1033 */         consumeToken();
/* 1034 */         break;
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/*      */       case 35:
/*      */       case 36:
/*      */       case 37:
/*      */       case 38:
/*      */       case 39:
/*      */       case 40:
/* 1045 */         if (iToken > 0) {
/* 1046 */           throw new InvalidInputException();
/*      */         }
/* 1048 */         pushIdentifier(true, false);
/* 1049 */         primitiveToken = token;
/* 1050 */         consumeToken();
/* 1051 */         break;
/*      */       default:
/* 1054 */         if (iToken == 0) {
/* 1055 */           if (this.identifierPtr >= 0) {
/* 1056 */             this.lastIdentifierEndPosition = (int)this.identifierPositionStack[this.identifierPtr];
/*      */           }
/* 1058 */           return null;
/*      */         }
/* 1060 */         if ((iToken & 0x1) != 0) break label353; switch (parserKind) {
/*      */         case 8:
/* 1063 */           if (this.identifierPtr >= 0) {
/* 1064 */             this.lastIdentifierEndPosition = (int)this.identifierPositionStack[this.identifierPtr];
/*      */           }
/* 1066 */           return syntaxRecoverQualifiedName(primitiveToken);
/*      */         case 2:
/* 1068 */           if (this.currentTokenType == -1)
/*      */             break;
/* 1070 */           this.index = this.tokenPreviousPosition;
/* 1071 */           this.scanner.currentPosition = this.tokenPreviousPosition;
/* 1072 */           this.currentTokenType = -1;
/*      */         }
/*      */ 
/* 1076 */         throw new InvalidInputException();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1083 */     label353: if ((parserKind != 8) && (this.currentTokenType != -1)) {
/* 1084 */       this.index = this.tokenPreviousPosition;
/* 1085 */       this.scanner.currentPosition = this.tokenPreviousPosition;
/* 1086 */       this.currentTokenType = -1;
/*      */     }
/* 1088 */     if (this.identifierPtr >= 0) {
/* 1089 */       this.lastIdentifierEndPosition = (int)this.identifierPositionStack[this.identifierPtr];
/*      */     }
/* 1091 */     return createTypeReference(primitiveToken);
/*      */   }
/*      */ 
/*      */   protected boolean parseReference()
/*      */     throws InvalidInputException
/*      */   {
/* 1098 */     int currentPosition = this.scanner.currentPosition;
/*      */     try {
/* 1100 */       Object typeRef = null;
/* 1101 */       Object reference = null;
/* 1102 */       int previousPosition = -1;
/* 1103 */       int typeRefStartPosition = -1;
/*      */ 
/* 1106 */       while (this.index < this.scanner.eofPosition) {
/* 1107 */         previousPosition = this.index;
/* 1108 */         int token = readTokenSafely();
/* 1109 */         switch (token)
/*      */         {
/*      */         case 52:
/* 1113 */           if (typeRef != null) break label595; consumeToken();
/* 1115 */           int start = this.scanner.getCurrentTokenStartPosition();
/* 1116 */           if (this.tagValue == 10)
/*      */           {
/* 1118 */             if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidValueReference(start, getTokenEndPosition(), this.sourceParser.modifiers);
/* 1119 */             return false;
/*      */           }
/*      */ 
/* 1123 */           if (verifyEndLine(previousPosition)) {
/* 1124 */             return createFakeReference(start);
/*      */           }
/* 1126 */           if (this.reportProblems) this.sourceParser.problemReporter().javadocUnexpectedText(this.scanner.currentPosition, this.lineEnd);
/* 1127 */           return false;
/*      */         case 7:
/* 1131 */           if (typeRef != null) break label595; consumeToken();
/* 1133 */           int start = this.scanner.getCurrentTokenStartPosition();
/* 1134 */           if (parseHref()) {
/* 1135 */             consumeToken();
/* 1136 */             if (this.tagValue == 10)
/*      */             {
/* 1138 */               if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidValueReference(start, getIndexPosition(), this.sourceParser.modifiers);
/* 1139 */               return false;
/*      */             }
/*      */ 
/* 1142 */             if (verifyEndLine(previousPosition)) {
/* 1143 */               return createFakeReference(start);
/*      */             }
/* 1145 */             if (this.reportProblems) this.sourceParser.problemReporter().javadocUnexpectedText(this.scanner.currentPosition, this.lineEnd);
/*      */           }
/* 1147 */           else if ((this.tagValue == 10) && 
/* 1148 */             (this.reportProblems)) { this.sourceParser.problemReporter().javadocInvalidValueReference(start, getIndexPosition(), this.sourceParser.modifiers);
/*      */           }
/* 1150 */           return false;
/*      */         case 110:
/* 1152 */           consumeToken();
/* 1153 */           if (this.scanner.currentCharacter == '#') {
/* 1154 */             reference = parseMember(typeRef);
/* 1155 */             if (reference != null) {
/* 1156 */               return pushSeeRef(reference);
/*      */             }
/* 1158 */             return false;
/*      */           }
/* 1160 */           char[] currentError = this.scanner.getCurrentIdentifierSource();
/* 1161 */           if ((currentError.length <= 0) || (currentError[0] != '"')) break label595; if (this.reportProblems) {
/* 1163 */             boolean isUrlRef = false;
/* 1164 */             if (this.tagValue == 6) {
/* 1165 */               int length = currentError.length; int i = 1;
/* 1166 */               while ((i < length) && (ScannerHelper.isLetter(currentError[i]))) {
/* 1167 */                 i++;
/*      */               }
/* 1169 */               if ((i < length - 2) && (currentError[i] == ':') && (currentError[(i + 1)] == '/') && (currentError[(i + 2)] == '/')) {
/* 1170 */                 isUrlRef = true;
/*      */               }
/*      */             }
/* 1173 */             if (isUrlRef)
/*      */             {
/* 1176 */               this.sourceParser.problemReporter().javadocInvalidSeeUrlReference(this.scanner.getCurrentTokenStartPosition(), getTokenEndPosition());
/*      */             }
/* 1178 */             else this.sourceParser.problemReporter().javadocInvalidReference(this.scanner.getCurrentTokenStartPosition(), getTokenEndPosition());
/*      */           }
/*      */ 
/* 1181 */           return false;
/*      */         case 26:
/* 1185 */           if (typeRef != null) break label595; typeRefStartPosition = this.scanner.getCurrentTokenStartPosition();
/* 1187 */           typeRef = parseQualifiedName(true);
/* 1188 */           if (!this.abort) continue; return false;
/*      */         default:
/* 1193 */           break label595;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1198 */       label595: if (reference == null) reference = typeRef;
/* 1199 */       if (reference == null) {
/* 1200 */         this.index = this.tokenPreviousPosition;
/* 1201 */         this.scanner.currentPosition = this.tokenPreviousPosition;
/* 1202 */         this.currentTokenType = -1;
/* 1203 */         if (this.tagValue == 10) {
/* 1204 */           if ((this.kind & 0x2) != 0) createTag();
/* 1205 */           return true;
/*      */         }
/* 1207 */         if (this.reportProblems) {
/* 1208 */           this.sourceParser.problemReporter().javadocMissingReference(this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
/*      */         }
/* 1210 */         return false;
/*      */       }
/*      */ 
/* 1214 */       if (this.lastIdentifierEndPosition > this.javadocStart) {
/* 1215 */         this.index = (this.lastIdentifierEndPosition + 1);
/* 1216 */         this.scanner.currentPosition = this.index;
/*      */       }
/* 1218 */       this.currentTokenType = -1;
/*      */ 
/* 1221 */       if (this.tagValue == 10) {
/* 1222 */         if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidReference(typeRefStartPosition, this.lineEnd);
/* 1223 */         return false;
/*      */       }
/*      */ 
/* 1226 */       int currentIndex = this.index;
/* 1227 */       char ch = readChar();
/* 1228 */       switch (ch)
/*      */       {
/*      */       case '(':
/* 1232 */         if (this.reportProblems) this.sourceParser.problemReporter().javadocMissingHashCharacter(typeRefStartPosition, this.lineEnd, String.valueOf(this.source, typeRefStartPosition, this.lineEnd - typeRefStartPosition + 1));
/* 1233 */         return false;
/*      */       case ':':
/* 1237 */         ch = readChar();
/* 1238 */         if ((ch != '/') || (ch != readChar()) || 
/* 1239 */           (!this.reportProblems)) break;
/* 1240 */         this.sourceParser.problemReporter().javadocInvalidSeeUrlReference(typeRefStartPosition, this.lineEnd);
/* 1241 */         return false;
/*      */       }
/*      */ 
/* 1246 */       this.index = currentIndex;
/*      */ 
/* 1249 */       if (!verifySpaceOrEndComment()) {
/* 1250 */         this.index = this.tokenPreviousPosition;
/* 1251 */         this.scanner.currentPosition = this.tokenPreviousPosition;
/* 1252 */         this.currentTokenType = -1;
/* 1253 */         int end = this.starPosition == -1 ? this.lineEnd : this.starPosition;
/* 1254 */         if (this.source[end] == '\n') end--;
/* 1255 */         if (this.reportProblems) this.sourceParser.problemReporter().javadocMalformedSeeReference(typeRefStartPosition, end);
/* 1256 */         return false;
/*      */       }
/*      */ 
/* 1260 */       return pushSeeRef(reference);
/*      */     }
/*      */     catch (InvalidInputException localInvalidInputException) {
/* 1263 */       if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidReference(currentPosition, getTokenEndPosition());
/*      */ 
/* 1266 */       this.index = this.tokenPreviousPosition;
/* 1267 */       this.scanner.currentPosition = this.tokenPreviousPosition;
/* 1268 */       this.currentTokenType = -1;
/* 1269 */     }return false;
/*      */   }
/*      */ 
/*      */   protected abstract boolean parseTag(int paramInt)
/*      */     throws InvalidInputException;
/*      */ 
/*      */   protected boolean parseThrows()
/*      */   {
/* 1281 */     int start = this.scanner.currentPosition;
/*      */     try {
/* 1283 */       Object typeRef = parseQualifiedName(true);
/* 1284 */       if (this.abort) return false;
/* 1285 */       if (typeRef == null) {
/* 1286 */         if (this.reportProblems)
/* 1287 */           this.sourceParser.problemReporter().javadocMissingThrowsClassName(this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
/*      */       }
/* 1289 */       else return pushThrowName(typeRef); 
/*      */     }
/*      */     catch (InvalidInputException localInvalidInputException)
/*      */     {
/* 1292 */       if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidThrowsClass(start, getTokenEndPosition());
/*      */     }
/* 1294 */     return false;
/*      */   }
/*      */ 
/*      */   protected char peekChar()
/*      */   {
/* 1301 */     int idx = this.index;
/* 1302 */     char c = this.source[(idx++)];
/* 1303 */     if ((c == '\\') && (this.source[idx] == 'u'))
/*      */     {
/* 1305 */       idx++;
/* 1306 */       while (this.source[idx] == 'u')
/* 1307 */         idx++;
/*      */       int c1;
/*      */       int c2;
/*      */       int c3;
/*      */       int c4;
/* 1308 */       if (((c1 = ScannerHelper.getNumericValue(this.source[(idx++)])) <= 15) && (c1 >= 0) && 
/* 1309 */         ((c2 = ScannerHelper.getNumericValue(this.source[(idx++)])) <= 15) && (c2 >= 0) && 
/* 1310 */         ((c3 = ScannerHelper.getNumericValue(this.source[(idx++)])) <= 15) && (c3 >= 0) && ((c4 = ScannerHelper.getNumericValue(this.source[(idx++)])) <= 15) && (c4 >= 0)) {
/* 1311 */         c = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
/*      */       }
/*      */     }
/* 1314 */     return c;
/*      */   }
/*      */ 
/*      */   protected void pushIdentifier(boolean newLength, boolean isToken)
/*      */   {
/* 1322 */     int stackLength = this.identifierStack.length;
/* 1323 */     if (++this.identifierPtr >= stackLength) {
/* 1324 */       System.arraycopy(
/* 1325 */         this.identifierStack, 0, 
/* 1326 */         this.identifierStack = new char[stackLength + 10][], 0, 
/* 1327 */         stackLength);
/* 1328 */       System.arraycopy(
/* 1329 */         this.identifierPositionStack, 0, 
/* 1330 */         this.identifierPositionStack = new long[stackLength + 10], 0, 
/* 1331 */         stackLength);
/*      */     }
/* 1333 */     this.identifierStack[this.identifierPtr] = (isToken ? this.scanner.getCurrentTokenSource() : this.scanner.getCurrentIdentifierSource());
/* 1334 */     this.identifierPositionStack[this.identifierPtr] = ((this.scanner.startPosition << 32) + (this.scanner.currentPosition - 1));
/*      */ 
/* 1336 */     if (newLength) {
/* 1337 */       stackLength = this.identifierLengthStack.length;
/* 1338 */       if (++this.identifierLengthPtr >= stackLength) {
/* 1339 */         System.arraycopy(
/* 1340 */           this.identifierLengthStack, 0, 
/* 1341 */           this.identifierLengthStack = new int[stackLength + 10], 0, 
/* 1342 */           stackLength);
/*      */       }
/* 1344 */       this.identifierLengthStack[this.identifierLengthPtr] = 1;
/*      */     } else {
/* 1346 */       this.identifierLengthStack[this.identifierLengthPtr] += 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void pushOnAstStack(Object node, boolean newLength)
/*      */   {
/* 1356 */     if (node == null) {
/* 1357 */       int stackLength = this.astLengthStack.length;
/* 1358 */       if (++this.astLengthPtr >= stackLength) {
/* 1359 */         System.arraycopy(
/* 1360 */           this.astLengthStack, 0, 
/* 1361 */           this.astLengthStack = new int[stackLength + 10], 0, 
/* 1362 */           stackLength);
/*      */       }
/* 1364 */       this.astLengthStack[this.astLengthPtr] = 0;
/* 1365 */       return;
/*      */     }
/*      */ 
/* 1368 */     int stackLength = this.astStack.length;
/* 1369 */     if (++this.astPtr >= stackLength) {
/* 1370 */       System.arraycopy(
/* 1371 */         this.astStack, 0, 
/* 1372 */         this.astStack = new Object[stackLength + 10], 0, 
/* 1373 */         stackLength);
/* 1374 */       this.astPtr = stackLength;
/*      */     }
/* 1376 */     this.astStack[this.astPtr] = node;
/*      */ 
/* 1378 */     if (newLength) {
/* 1379 */       stackLength = this.astLengthStack.length;
/* 1380 */       if (++this.astLengthPtr >= stackLength) {
/* 1381 */         System.arraycopy(
/* 1382 */           this.astLengthStack, 0, 
/* 1383 */           this.astLengthStack = new int[stackLength + 10], 0, 
/* 1384 */           stackLength);
/*      */       }
/* 1386 */       this.astLengthStack[this.astLengthPtr] = 1;
/*      */     } else {
/* 1388 */       this.astLengthStack[this.astLengthPtr] += 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected abstract boolean pushParamName(boolean paramBoolean);
/*      */ 
/*      */   protected abstract boolean pushSeeRef(Object paramObject);
/*      */ 
/*      */   protected void pushText(int start, int end)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected abstract boolean pushThrowName(Object paramObject);
/*      */ 
/*      */   protected char readChar()
/*      */   {
/* 1420 */     char c = this.source[(this.index++)];
/* 1421 */     if ((c == '\\') && (this.source[this.index] == 'u'))
/*      */     {
/* 1423 */       int pos = this.index;
/* 1424 */       this.index += 1;
/* 1425 */       while (this.source[this.index] == 'u')
/* 1426 */         this.index += 1;
/*      */       int c1;
/*      */       int c2;
/*      */       int c3;
/*      */       int c4;
/* 1427 */       if (((c1 = ScannerHelper.getNumericValue(this.source[(this.index++)])) <= 15) && (c1 >= 0) && 
/* 1428 */         ((c2 = ScannerHelper.getNumericValue(this.source[(this.index++)])) <= 15) && (c2 >= 0) && 
/* 1429 */         ((c3 = ScannerHelper.getNumericValue(this.source[(this.index++)])) <= 15) && (c3 >= 0) && ((c4 = ScannerHelper.getNumericValue(this.source[(this.index++)])) <= 15) && (c4 >= 0)) {
/* 1430 */         c = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
/*      */       }
/*      */       else {
/* 1433 */         this.index = pos;
/*      */       }
/*      */     }
/* 1436 */     return c;
/*      */   }
/*      */ 
/*      */   protected int readToken()
/*      */     throws InvalidInputException
/*      */   {
/* 1443 */     if (this.currentTokenType < 0) {
/* 1444 */       this.tokenPreviousPosition = this.scanner.currentPosition;
/* 1445 */       this.currentTokenType = this.scanner.getNextToken();
/* 1446 */       if (this.scanner.currentPosition > this.lineEnd + 1) {
/* 1447 */         this.lineStarted = false;
/* 1448 */         while (this.currentTokenType == 4) {
/* 1449 */           this.currentTokenType = this.scanner.getNextToken();
/*      */         }
/*      */       }
/* 1452 */       this.index = this.scanner.currentPosition;
/* 1453 */       this.lineStarted = true;
/*      */     }
/* 1455 */     return this.currentTokenType;
/*      */   }
/*      */ 
/*      */   protected int readTokenAndConsume() throws InvalidInputException {
/* 1459 */     int token = readToken();
/* 1460 */     consumeToken();
/* 1461 */     return token;
/*      */   }
/*      */ 
/*      */   protected int readTokenSafely()
/*      */   {
/* 1469 */     int token = 110;
/*      */     try {
/* 1471 */       token = readToken();
/*      */     }
/*      */     catch (InvalidInputException localInvalidInputException)
/*      */     {
/*      */     }
/* 1476 */     return token;
/*      */   }
/*      */ 
/*      */   protected void recordInheritedPosition(long position) {
/* 1480 */     if (this.inheritedPositions == null) {
/* 1481 */       this.inheritedPositions = new long[4];
/* 1482 */       this.inheritedPositionsPtr = 0;
/*      */     }
/* 1484 */     else if (this.inheritedPositionsPtr == this.inheritedPositions.length) {
/* 1485 */       System.arraycopy(
/* 1486 */         this.inheritedPositions, 0, 
/* 1487 */         this.inheritedPositions = new long[this.inheritedPositionsPtr + 4], 0, 
/* 1488 */         this.inheritedPositionsPtr);
/*      */     }
/*      */ 
/* 1491 */     this.inheritedPositions[(this.inheritedPositionsPtr++)] = position;
/*      */   }
/*      */ 
/*      */   protected void refreshInlineTagPosition(int previousPosition)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void refreshReturnStatement()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected Object syntaxRecoverQualifiedName(int primitiveToken)
/*      */     throws InvalidInputException
/*      */   {
/* 1513 */     return null;
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1517 */     StringBuffer buffer = new StringBuffer();
/* 1518 */     int startPos = this.scanner.currentPosition < this.index ? this.scanner.currentPosition : this.index;
/* 1519 */     int endPos = this.scanner.currentPosition < this.index ? this.index : this.scanner.currentPosition;
/* 1520 */     if (startPos == this.source.length)
/* 1521 */       return "EOF\n\n" + new String(this.source);
/* 1522 */     if (endPos > this.source.length) {
/* 1523 */       return "behind the EOF\n\n" + new String(this.source);
/*      */     }
/* 1525 */     char[] front = new char[startPos];
/* 1526 */     System.arraycopy(this.source, 0, front, 0, startPos);
/*      */ 
/* 1528 */     int middleLength = endPos - 1 - startPos + 1;
/*      */     char[] middle;
/* 1530 */     if (middleLength > -1) {
/* 1531 */       char[] middle = new char[middleLength];
/* 1532 */       System.arraycopy(
/* 1533 */         this.source, 
/* 1534 */         startPos, 
/* 1535 */         middle, 
/* 1536 */         0, 
/* 1537 */         middleLength);
/*      */     } else {
/* 1539 */       middle = CharOperation.NO_CHAR;
/*      */     }
/*      */ 
/* 1542 */     char[] end = new char[this.source.length - (endPos - 1)];
/* 1543 */     System.arraycopy(
/* 1544 */       this.source, 
/* 1545 */       endPos - 1 + 1, 
/* 1546 */       end, 
/* 1547 */       0, 
/* 1548 */       this.source.length - (endPos - 1) - 1);
/*      */ 
/* 1550 */     buffer.append(front);
/* 1551 */     if (this.scanner.currentPosition < this.index)
/* 1552 */       buffer.append("\n===============================\nScanner current position here -->");
/*      */     else {
/* 1554 */       buffer.append("\n===============================\nParser index here -->");
/*      */     }
/* 1556 */     buffer.append(middle);
/* 1557 */     if (this.scanner.currentPosition < this.index)
/* 1558 */       buffer.append("<-- Parser index here\n===============================\n");
/*      */     else {
/* 1560 */       buffer.append("<-- Scanner current position here\n===============================\n");
/*      */     }
/* 1562 */     buffer.append(end);
/*      */ 
/* 1564 */     return buffer.toString();
/*      */   }
/*      */ 
/*      */   protected abstract void updateDocComment();
/*      */ 
/*      */   protected void updateLineEnd()
/*      */   {
/* 1576 */     while (this.index > this.lineEnd + 1)
/* 1577 */       if (this.linePtr < this.lastLinePtr) {
/* 1578 */         this.lineEnd = (this.scanner.getLineEnd(++this.linePtr) - 1);
/*      */       } else {
/* 1580 */         this.lineEnd = this.javadocEnd;
/* 1581 */         return;
/*      */       }
/*      */   }
/*      */ 
/*      */   protected boolean verifyEndLine(int textPosition)
/*      */   {
/* 1591 */     boolean domParser = (this.kind & 0x2) != 0;
/*      */ 
/* 1593 */     if (this.inlineTagStarted)
/*      */     {
/* 1595 */       if (peekChar() == '}') {
/* 1596 */         if (domParser) {
/* 1597 */           createTag();
/* 1598 */           pushText(textPosition, this.starPosition);
/*      */         }
/* 1600 */         return true;
/*      */       }
/* 1602 */       return false;
/*      */     }
/*      */ 
/* 1605 */     int startPosition = this.index;
/* 1606 */     int previousPosition = this.index;
/* 1607 */     this.starPosition = -1;
/* 1608 */     char ch = readChar();
/*      */     while (true) {
/* 1610 */       switch (ch) {
/*      */       case '\n':
/*      */       case '\r':
/* 1613 */         if (domParser) {
/* 1614 */           createTag();
/* 1615 */           pushText(textPosition, previousPosition);
/*      */         }
/* 1617 */         this.index = previousPosition;
/* 1618 */         return true;
/*      */       case '\t':
/*      */       case '\f':
/*      */       case ' ':
/* 1622 */         if (this.starPosition < 0) break; break;
/*      */       case '*':
/* 1625 */         this.starPosition = previousPosition;
/* 1626 */         break;
/*      */       case '/':
/* 1628 */         if (this.starPosition < textPosition) break label231; if (domParser) {
/* 1630 */           createTag();
/* 1631 */           pushText(textPosition, this.starPosition);
/*      */         }
/* 1633 */         return true;
/*      */       default:
/* 1638 */         break;
/*      */       }
/*      */ 
/* 1641 */       previousPosition = this.index;
/* 1642 */       ch = readChar();
/*      */     }
/* 1644 */     label231: this.index = startPosition;
/* 1645 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean verifySpaceOrEndComment()
/*      */   {
/* 1656 */     this.starPosition = -1;
/* 1657 */     int startPosition = this.index;
/*      */ 
/* 1659 */     char ch = peekChar();
/* 1660 */     switch (ch) {
/*      */     case '}':
/* 1662 */       return this.inlineTagStarted;
/*      */     }
/* 1664 */     if (ScannerHelper.isWhitespace(ch)) {
/* 1665 */       return true;
/*      */     }
/*      */ 
/* 1669 */     int previousPosition = this.index;
/* 1670 */     ch = readChar();
/* 1671 */     while (this.index < this.source.length) {
/* 1672 */       switch (ch)
/*      */       {
/*      */       case '*':
/* 1675 */         this.starPosition = previousPosition;
/* 1676 */         break;
/*      */       case '/':
/* 1678 */         if (this.starPosition < startPosition) break;
/* 1679 */         return true;
/*      */       }
/*      */ 
/* 1684 */       this.index = startPosition;
/* 1685 */       return false;
/*      */ 
/* 1688 */       previousPosition = this.index;
/* 1689 */       ch = readChar();
/*      */     }
/* 1691 */     this.index = startPosition;
/* 1692 */     return false;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.AbstractCommentParser
 * JD-Core Version:    0.6.0
 */