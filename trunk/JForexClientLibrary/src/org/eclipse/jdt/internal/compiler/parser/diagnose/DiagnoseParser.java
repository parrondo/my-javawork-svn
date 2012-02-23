/*      */ package org.eclipse.jdt.internal.compiler.parser.diagnose;
/*      */ 
/*      */ import java.io.PrintStream;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.parser.Parser;
/*      */ import org.eclipse.jdt.internal.compiler.parser.ParserBasicInformation;
/*      */ import org.eclipse.jdt.internal.compiler.parser.RecoveryScanner;
/*      */ import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
/*      */ import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class DiagnoseParser
/*      */   implements ParserBasicInformation, TerminalTokens
/*      */ {
/*      */   private static final boolean DEBUG = false;
/*   25 */   private boolean DEBUG_PARSECHECK = false;
/*      */   private static final int STACK_INCREMENT = 256;
/*      */   private static final int BEFORE_CODE = 2;
/*      */   private static final int INSERTION_CODE = 3;
/*      */   private static final int INVALID_CODE = 4;
/*      */   private static final int SUBSTITUTION_CODE = 5;
/*      */   private static final int DELETION_CODE = 6;
/*      */   private static final int MERGE_CODE = 7;
/*      */   private static final int MISPLACED_CODE = 8;
/*      */   private static final int SCOPE_CODE = 9;
/*      */   private static final int SECONDARY_CODE = 10;
/*      */   private static final int EOF_CODE = 11;
/*      */   private static final int BUFF_UBOUND = 31;
/*      */   private static final int BUFF_SIZE = 32;
/*      */   private static final int MAX_DISTANCE = 30;
/*      */   private static final int MIN_DISTANCE = 3;
/*      */   private CompilerOptions options;
/*      */   private LexStream lexStream;
/*      */   private int errorToken;
/*      */   private int errorTokenStart;
/*   52 */   private int currentToken = 0;
/*      */   private int stackLength;
/*      */   private int stateStackTop;
/*      */   private int[] stack;
/*      */   private int[] locationStack;
/*      */   private int[] locationStartStack;
/*      */   private int tempStackTop;
/*      */   private int[] tempStack;
/*      */   private int prevStackTop;
/*      */   private int[] prevStack;
/*      */   private int nextStackTop;
/*      */   private int[] nextStack;
/*      */   private int scopeStackTop;
/*      */   private int[] scopeIndex;
/*      */   private int[] scopePosition;
/*   73 */   int[] list = new int[424];
/*   74 */   int[] buffer = new int[32];
/*      */   private static final int NIL = -1;
/*      */   int[] stateSeen;
/*      */   int statePoolTop;
/*      */   StateInfo[] statePool;
/*      */   private Parser parser;
/*      */   private RecoveryScanner recoveryScanner;
/*      */   private boolean reportProblem;
/*      */ 
/*      */   public DiagnoseParser(Parser parser, int firstToken, int start, int end, CompilerOptions options)
/*      */   {
/*  147 */     this(parser, firstToken, start, end, Util.EMPTY_INT_ARRAY, Util.EMPTY_INT_ARRAY, Util.EMPTY_INT_ARRAY, options);
/*      */   }
/*      */ 
/*      */   public DiagnoseParser(Parser parser, int firstToken, int start, int end, int[] intervalStartToSkip, int[] intervalEndToSkip, int[] intervalFlagsToSkip, CompilerOptions options) {
/*  151 */     this.parser = parser;
/*  152 */     this.options = options;
/*  153 */     this.lexStream = new LexStream(32, parser.scanner, intervalStartToSkip, intervalEndToSkip, intervalFlagsToSkip, firstToken, start, end);
/*  154 */     this.recoveryScanner = parser.recoveryScanner;
/*      */   }
/*      */ 
/*      */   private ProblemReporter problemReporter() {
/*  158 */     return this.parser.problemReporter();
/*      */   }
/*      */ 
/*      */   private void reallocateStacks() {
/*  162 */     int old_stack_length = this.stackLength;
/*      */ 
/*  164 */     this.stackLength += 256;
/*      */ 
/*  166 */     if (old_stack_length == 0) {
/*  167 */       this.stack = new int[this.stackLength];
/*  168 */       this.locationStack = new int[this.stackLength];
/*  169 */       this.locationStartStack = new int[this.stackLength];
/*  170 */       this.tempStack = new int[this.stackLength];
/*  171 */       this.prevStack = new int[this.stackLength];
/*  172 */       this.nextStack = new int[this.stackLength];
/*  173 */       this.scopeIndex = new int[this.stackLength];
/*  174 */       this.scopePosition = new int[this.stackLength];
/*      */     } else {
/*  176 */       System.arraycopy(this.stack, 0, this.stack = new int[this.stackLength], 0, old_stack_length);
/*  177 */       System.arraycopy(this.locationStack, 0, this.locationStack = new int[this.stackLength], 0, old_stack_length);
/*  178 */       System.arraycopy(this.locationStartStack, 0, this.locationStartStack = new int[this.stackLength], 0, old_stack_length);
/*  179 */       System.arraycopy(this.tempStack, 0, this.tempStack = new int[this.stackLength], 0, old_stack_length);
/*  180 */       System.arraycopy(this.prevStack, 0, this.prevStack = new int[this.stackLength], 0, old_stack_length);
/*  181 */       System.arraycopy(this.nextStack, 0, this.nextStack = new int[this.stackLength], 0, old_stack_length);
/*  182 */       System.arraycopy(this.scopeIndex, 0, this.scopeIndex = new int[this.stackLength], 0, old_stack_length);
/*  183 */       System.arraycopy(this.scopePosition, 0, this.scopePosition = new int[this.stackLength], 0, old_stack_length);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void diagnoseParse(boolean record)
/*      */   {
/*  190 */     this.reportProblem = true;
/*  191 */     boolean oldRecord = false;
/*  192 */     if (this.recoveryScanner != null) {
/*  193 */       oldRecord = this.recoveryScanner.record;
/*  194 */       this.recoveryScanner.record = record;
/*      */     }
/*      */     try
/*      */     {
/*  197 */       this.lexStream.reset();
/*      */ 
/*  199 */       this.currentToken = this.lexStream.getToken();
/*      */ 
/*  204 */       int act = 942;
/*      */ 
/*  206 */       reallocateStacks();
/*      */ 
/*  211 */       this.stateStackTop = 0;
/*  212 */       this.stack[this.stateStackTop] = act;
/*      */ 
/*  214 */       int tok = this.lexStream.kind(this.currentToken);
/*  215 */       this.locationStack[this.stateStackTop] = this.currentToken;
/*  216 */       this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.currentToken);
/*      */ 
/*  218 */       boolean forceRecoveryAfterLBracketMissing = false;
/*      */       int act;
/*      */       do
/*      */       {
/*  228 */         int prev_pos = -1;
/*  229 */         this.prevStackTop = -1;
/*      */ 
/*  231 */         int next_pos = -1;
/*  232 */         this.nextStackTop = -1;
/*      */ 
/*  234 */         int pos = this.stateStackTop;
/*  235 */         this.tempStackTop = (this.stateStackTop - 1);
/*  236 */         for (int i = 0; i <= this.stateStackTop; i++) {
/*  237 */           this.tempStack[i] = this.stack[i];
/*      */         }
/*  239 */         act = Parser.tAction(act, tok);
/*      */ 
/*  246 */         while (act <= 703) {
/*      */           do {
/*  248 */             this.tempStackTop -= Parser.rhs[act] - 1;
/*  249 */             act = Parser.ntAction(this.tempStack[this.tempStackTop], Parser.lhs[act]);
/*  250 */           }while (act <= 703);
/*      */ 
/*  256 */           if (this.tempStackTop + 1 >= this.stackLength)
/*  257 */             reallocateStacks();
/*  258 */           pos = pos < this.tempStackTop ? pos : this.tempStackTop;
/*  259 */           this.tempStack[(this.tempStackTop + 1)] = act;
/*  260 */           act = Parser.tAction(act, tok);
/*      */         }
/*      */ 
/*  272 */         while ((act > 12741) || (act < 12740)) {
/*  273 */           this.nextStackTop = (this.tempStackTop + 1);
/*  274 */           for (int i = next_pos + 1; i <= this.nextStackTop; i++) {
/*  275 */             this.nextStack[i] = this.tempStack[i];
/*      */           }
/*  277 */           for (int i = pos + 1; i <= this.nextStackTop; i++) {
/*  278 */             this.locationStack[i] = this.locationStack[this.stateStackTop];
/*  279 */             this.locationStartStack[i] = this.locationStartStack[this.stateStackTop];
/*      */           }
/*      */ 
/*  286 */           if (act > 12741) {
/*  287 */             act -= 12741;
/*      */             do {
/*  289 */               this.nextStackTop -= Parser.rhs[act] - 1;
/*  290 */               act = Parser.ntAction(this.nextStack[this.nextStackTop], Parser.lhs[act]);
/*  291 */             }while (act <= 703);
/*  292 */             pos = pos < this.nextStackTop ? pos : this.nextStackTop;
/*      */           }
/*      */ 
/*  295 */           if (this.nextStackTop + 1 >= this.stackLength) {
/*  296 */             reallocateStacks();
/*      */           }
/*  298 */           this.tempStackTop = this.nextStackTop;
/*  299 */           this.nextStack[(++this.nextStackTop)] = act;
/*  300 */           next_pos = this.nextStackTop;
/*      */ 
/*  306 */           this.currentToken = this.lexStream.getToken();
/*  307 */           tok = this.lexStream.kind(this.currentToken);
/*  308 */           act = Parser.tAction(act, tok);
/*  309 */           while (act <= 703)
/*      */           {
/*      */             do
/*      */             {
/*  315 */               int lhs_symbol = Parser.lhs[act];
/*      */ 
/*  319 */               this.tempStackTop -= Parser.rhs[act] - 1;
/*  320 */               act = this.tempStackTop > next_pos ? 
/*  321 */                 this.tempStack[this.tempStackTop] : 
/*  322 */                 this.nextStack[this.tempStackTop];
/*  323 */               act = Parser.ntAction(act, lhs_symbol);
/*  324 */             }while (act <= 703);
/*      */ 
/*  331 */             if (this.tempStackTop + 1 >= this.stackLength) {
/*  332 */               reallocateStacks();
/*      */             }
/*  334 */             next_pos = next_pos < this.tempStackTop ? next_pos : this.tempStackTop;
/*  335 */             this.tempStack[(this.tempStackTop + 1)] = act;
/*  336 */             act = Parser.tAction(act, tok);
/*      */           }
/*      */ 
/*  354 */           if (act != 12741) {
/*  355 */             this.prevStackTop = this.stateStackTop;
/*  356 */             for (int i = prev_pos + 1; i <= this.prevStackTop; i++)
/*  357 */               this.prevStack[i] = this.stack[i];
/*  358 */             prev_pos = pos;
/*      */ 
/*  360 */             this.stateStackTop = this.nextStackTop;
/*  361 */             for (int i = pos + 1; i <= this.stateStackTop; i++)
/*  362 */               this.stack[i] = this.nextStack[i];
/*  363 */             this.locationStack[this.stateStackTop] = this.currentToken;
/*  364 */             this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.currentToken);
/*  365 */             pos = next_pos;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  373 */         if (act != 12741)
/*      */         {
/*      */           continue;
/*      */         }
/*  377 */         RepairCandidate candidate = errorRecovery(this.currentToken, forceRecoveryAfterLBracketMissing);
/*      */ 
/*  379 */         forceRecoveryAfterLBracketMissing = false;
/*      */ 
/*  381 */         if (this.parser.reportOnlyOneSyntaxError);
/*      */         RepairCandidate candidate;
/*      */         int tok;
/*      */         while (true)
/*      */         {
/*      */           return;
/*      */           boolean forceRecoveryAfterLBracketMissing;
/*      */           int next_pos;
/*      */           int pos;
/*      */           int prev_pos;
/*  385 */           if (this.parser.problemReporter().options.maxProblemsPerUnit >= this.parser.compilationUnit.compilationResult.problemCount) break;
/*  386 */           if ((this.recoveryScanner != null) && (this.recoveryScanner.record)) {
/*  387 */             this.reportProblem = false;
/*      */           }
/*      */         }
/*  390 */         act = this.stack[this.stateStackTop];
/*      */ 
/*  396 */         if (candidate.symbol == 0)
/*      */           break;
/*  398 */         if (candidate.symbol > 110) {
/*  399 */           int lhs_symbol = candidate.symbol - 110;
/*      */ 
/*  403 */           act = Parser.ntAction(act, lhs_symbol);
/*  404 */           while (act <= 703) {
/*  405 */             this.stateStackTop -= Parser.rhs[act] - 1;
/*  406 */             act = Parser.ntAction(this.stack[this.stateStackTop], Parser.lhs[act]);
/*      */           }
/*  408 */           this.stack[(++this.stateStackTop)] = act;
/*  409 */           this.currentToken = this.lexStream.getToken();
/*  410 */           tok = this.lexStream.kind(this.currentToken);
/*  411 */           this.locationStack[this.stateStackTop] = this.currentToken;
/*  412 */           this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.currentToken);
/*      */         } else {
/*  414 */           tok = candidate.symbol;
/*  415 */           this.locationStack[this.stateStackTop] = candidate.location;
/*  416 */           this.locationStartStack[this.stateStackTop] = this.lexStream.start(candidate.location);
/*      */         }
/*      */       }
/*  419 */       while (act != 12740);
/*      */     } finally {
/*  421 */       if (this.recoveryScanner != null)
/*  422 */         this.recoveryScanner.record = oldRecord;
/*      */     }
/*  421 */     if (this.recoveryScanner != null)
/*  422 */       this.recoveryScanner.record = oldRecord;
/*      */   }
/*      */ 
/*      */   private static char[] displayEscapeCharacters(char[] tokenSource, int start, int end)
/*      */   {
/*  429 */     StringBuffer tokenSourceBuffer = new StringBuffer();
/*  430 */     for (int i = 0; i < start; i++) {
/*  431 */       tokenSourceBuffer.append(tokenSource[i]);
/*      */     }
/*  433 */     for (int i = start; i < end; i++) {
/*  434 */       char c = tokenSource[i];
/*      */ 
/*  436 */       switch (c) {
/*      */       case '\r':
/*  438 */         tokenSourceBuffer.append("\\r");
/*  439 */         break;
/*      */       case '\n':
/*  441 */         tokenSourceBuffer.append("\\n");
/*  442 */         break;
/*      */       case '\b':
/*  444 */         tokenSourceBuffer.append("\\b");
/*  445 */         break;
/*      */       case '\t':
/*  447 */         tokenSourceBuffer.append("\t");
/*  448 */         break;
/*      */       case '\f':
/*  450 */         tokenSourceBuffer.append("\\f");
/*  451 */         break;
/*      */       case '"':
/*  453 */         tokenSourceBuffer.append("\\\"");
/*  454 */         break;
/*      */       case '\'':
/*  456 */         tokenSourceBuffer.append("\\'");
/*  457 */         break;
/*      */       case '\\':
/*  459 */         tokenSourceBuffer.append("\\\\");
/*  460 */         break;
/*      */       default:
/*  462 */         tokenSourceBuffer.append(c);
/*      */       }
/*      */     }
/*  465 */     for (int i = end; i < tokenSource.length; i++) {
/*  466 */       tokenSourceBuffer.append(tokenSource[i]);
/*      */     }
/*  468 */     return tokenSourceBuffer.toString().toCharArray();
/*      */   }
/*      */ 
/*      */   private RepairCandidate errorRecovery(int error_token, boolean forcedError)
/*      */   {
/*  488 */     this.errorToken = error_token;
/*  489 */     this.errorTokenStart = this.lexStream.start(error_token);
/*      */ 
/*  491 */     int prevtok = this.lexStream.previous(error_token);
/*  492 */     int prevtokKind = this.lexStream.kind(prevtok);
/*      */ 
/*  494 */     if (forcedError) {
/*  495 */       int name_index = Parser.terminal_index[69];
/*      */ 
/*  497 */       reportError(3, name_index, prevtok, prevtok);
/*      */ 
/*  499 */       RepairCandidate candidate = new RepairCandidate();
/*  500 */       candidate.symbol = 69;
/*  501 */       candidate.location = error_token;
/*  502 */       this.lexStream.reset(error_token);
/*      */ 
/*  504 */       this.stateStackTop = this.nextStackTop;
/*  505 */       for (int j = 0; j <= this.stateStackTop; j++) {
/*  506 */         this.stack[j] = this.nextStack[j];
/*      */       }
/*  508 */       this.locationStack[this.stateStackTop] = error_token;
/*  509 */       this.locationStartStack[this.stateStackTop] = this.lexStream.start(error_token);
/*      */ 
/*  511 */       return candidate;
/*      */     }
/*      */ 
/*  519 */     RepairCandidate candidate = primaryPhase(error_token);
/*  520 */     if (candidate.symbol != 0) {
/*  521 */       return candidate;
/*      */     }
/*      */ 
/*  524 */     candidate = secondaryPhase(error_token);
/*  525 */     if (candidate.symbol != 0) {
/*  526 */       return candidate;
/*      */     }
/*      */ 
/*  529 */     if (this.lexStream.kind(error_token) == 68) {
/*  530 */       reportError(11, 
/*  531 */         Parser.terminal_index[68], 
/*  532 */         prevtok, 
/*  533 */         prevtok);
/*  534 */       candidate.symbol = 0;
/*  535 */       candidate.location = error_token;
/*  536 */       return candidate;
/*      */     }
/*      */ 
/*      */     do
/*      */     {
/*  547 */       candidate = secondaryPhase(this.buffer[29]);
/*  548 */       if (candidate.symbol != 0)
/*  549 */         return candidate;
/*      */     }
/*  546 */     while (this.lexStream.kind(this.buffer[31]) != 68);
/*      */ 
/*  558 */     for (int i = 31; this.lexStream.kind(this.buffer[i]) == 68; i--);
/*  560 */     reportError(6, 
/*  561 */       Parser.terminal_index[prevtokKind], 
/*  562 */       error_token, 
/*  563 */       this.buffer[i]);
/*      */ 
/*  565 */     candidate.symbol = 0;
/*  566 */     candidate.location = this.buffer[i];
/*      */ 
/*  568 */     return candidate;
/*      */   }
/*      */ 
/*      */   private RepairCandidate primaryPhase(int error_token)
/*      */   {
/*  579 */     PrimaryRepairInfo repair = new PrimaryRepairInfo();
/*  580 */     RepairCandidate candidate = new RepairCandidate();
/*      */ 
/*  585 */     int i = this.nextStackTop >= 0 ? 3 : 2;
/*  586 */     this.buffer[i] = error_token;
/*      */ 
/*  588 */     for (int j = i; j > 0; j--) {
/*  589 */       this.buffer[(j - 1)] = this.lexStream.previous(this.buffer[j]);
/*      */     }
/*  591 */     for (int k = i + 1; k < 32; k++) {
/*  592 */       this.buffer[k] = this.lexStream.next(this.buffer[(k - 1)]);
/*      */     }
/*      */ 
/*  600 */     if (this.nextStackTop >= 0) {
/*  601 */       repair.bufferPosition = 3;
/*  602 */       repair = checkPrimaryDistance(this.nextStack, this.nextStackTop, repair);
/*      */     }
/*      */ 
/*  608 */     PrimaryRepairInfo new_repair = repair.copy();
/*      */ 
/*  610 */     new_repair.bufferPosition = 2;
/*  611 */     new_repair = checkPrimaryDistance(this.stack, this.stateStackTop, new_repair);
/*  612 */     if ((new_repair.distance > repair.distance) || (new_repair.misspellIndex > repair.misspellIndex)) {
/*  613 */       repair = new_repair;
/*      */     }
/*      */ 
/*  621 */     if (this.prevStackTop >= 0) {
/*  622 */       new_repair = repair.copy();
/*  623 */       new_repair.bufferPosition = 1;
/*  624 */       new_repair = checkPrimaryDistance(this.prevStack, this.prevStackTop, new_repair);
/*  625 */       if ((new_repair.distance > repair.distance) || (new_repair.misspellIndex > repair.misspellIndex)) {
/*  626 */         repair = new_repair;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  635 */     if (this.nextStackTop >= 0) {
/*  636 */       if (secondaryCheck(this.nextStack, this.nextStackTop, 3, repair.distance)) {
/*  637 */         return candidate;
/*      */       }
/*      */     }
/*  640 */     else if (secondaryCheck(this.stack, this.stateStackTop, 2, repair.distance)) {
/*  641 */       return candidate;
/*      */     }
/*      */ 
/*  651 */     repair.distance = (repair.distance - repair.bufferPosition + 1);
/*      */ 
/*  657 */     if ((repair.code == 4) || 
/*  658 */       (repair.code == 6) || 
/*  659 */       (repair.code == 5) || 
/*  660 */       (repair.code == 7)) {
/*  661 */       repair.distance -= 1;
/*      */     }
/*      */ 
/*  669 */     if (repair.distance < 3) {
/*  670 */       return candidate;
/*      */     }
/*      */ 
/*  680 */     if ((repair.code == 3) && 
/*  681 */       (this.buffer[(repair.bufferPosition - 1)] == 0)) {
/*  682 */       repair.code = 2;
/*      */     }
/*      */ 
/*  690 */     if (repair.bufferPosition == 1) {
/*  691 */       this.stateStackTop = this.prevStackTop;
/*  692 */       for (int j = 0; j <= this.stateStackTop; j++)
/*  693 */         this.stack[j] = this.prevStack[j];
/*      */     }
/*  695 */     else if ((this.nextStackTop >= 0) && (repair.bufferPosition >= 3)) {
/*  696 */       this.stateStackTop = this.nextStackTop;
/*  697 */       for (int j = 0; j <= this.stateStackTop; j++) {
/*  698 */         this.stack[j] = this.nextStack[j];
/*      */       }
/*  700 */       this.locationStack[this.stateStackTop] = this.buffer[3];
/*  701 */       this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.buffer[3]);
/*      */     }
/*      */ 
/*  704 */     return primaryDiagnosis(repair);
/*      */   }
/*      */ 
/*      */   private int mergeCandidate(int state, int buffer_position)
/*      */   {
/*  716 */     char[] name1 = this.lexStream.name(this.buffer[buffer_position]);
/*  717 */     char[] name2 = this.lexStream.name(this.buffer[(buffer_position + 1)]);
/*      */ 
/*  719 */     int len = name1.length + name2.length;
/*      */ 
/*  721 */     char[] str = CharOperation.concat(name1, name2);
/*      */ 
/*  723 */     for (int k = Parser.asi(state); Parser.asr[k] != 0; k++) {
/*  724 */       int l = Parser.terminal_index[Parser.asr[k]];
/*      */ 
/*  726 */       if (len == Parser.name[l].length()) {
/*  727 */         char[] name = Parser.name[l].toCharArray();
/*      */ 
/*  729 */         if (CharOperation.equals(str, name, false)) {
/*  730 */           return Parser.asr[k];
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  735 */     return 0;
/*      */   }
/*      */ 
/*      */   private PrimaryRepairInfo checkPrimaryDistance(int[] stck, int stack_top, PrimaryRepairInfo repair)
/*      */   {
/*  762 */     PrimaryRepairInfo scope_repair = scopeTrial(stck, stack_top, repair.copy());
/*  763 */     if (scope_repair.distance > repair.distance) {
/*  764 */       repair = scope_repair;
/*      */     }
/*      */ 
/*  769 */     if ((this.buffer[repair.bufferPosition] != 0) && (this.buffer[(repair.bufferPosition + 1)] != 0)) {
/*  770 */       int symbol = mergeCandidate(stck[stack_top], repair.bufferPosition);
/*  771 */       if (symbol != 0) {
/*  772 */         int j = parseCheck(stck, stack_top, symbol, repair.bufferPosition + 2);
/*  773 */         if ((j > repair.distance) || ((j == repair.distance) && (repair.misspellIndex < 10))) {
/*  774 */           repair.misspellIndex = 10;
/*  775 */           repair.symbol = symbol;
/*  776 */           repair.distance = j;
/*  777 */           repair.code = 7;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  785 */     int j = parseCheck(
/*  786 */       stck, 
/*  787 */       stack_top, 
/*  788 */       this.lexStream.kind(this.buffer[(repair.bufferPosition + 1)]), 
/*  789 */       repair.bufferPosition + 2);
/*      */     int k;
/*      */     int k;
/*  790 */     if ((this.lexStream.kind(this.buffer[repair.bufferPosition]) == 68) && 
/*  791 */       (this.lexStream.afterEol(this.buffer[(repair.bufferPosition + 1)])))
/*  792 */       k = 10;
/*      */     else {
/*  794 */       k = 0;
/*      */     }
/*  796 */     if ((j > repair.distance) || ((j == repair.distance) && (k > repair.misspellIndex))) {
/*  797 */       repair.misspellIndex = k;
/*  798 */       repair.code = 6;
/*  799 */       repair.distance = j;
/*      */     }
/*      */ 
/*  807 */     int next_state = stck[stack_top];
/*  808 */     int max_pos = stack_top;
/*  809 */     this.tempStackTop = (stack_top - 1);
/*      */ 
/*  811 */     int tok = this.lexStream.kind(this.buffer[repair.bufferPosition]);
/*  812 */     this.lexStream.reset(this.buffer[(repair.bufferPosition + 1)]);
/*  813 */     int act = Parser.tAction(next_state, tok);
/*  814 */     while (act <= 703) {
/*      */       do {
/*  816 */         this.tempStackTop -= Parser.rhs[act] - 1;
/*  817 */         int symbol = Parser.lhs[act];
/*  818 */         act = this.tempStackTop > max_pos ? 
/*  819 */           this.tempStack[this.tempStackTop] : 
/*  820 */           stck[this.tempStackTop];
/*  821 */         act = Parser.ntAction(act, symbol);
/*  822 */       }while (act <= 703);
/*  823 */       max_pos = max_pos < this.tempStackTop ? max_pos : this.tempStackTop;
/*  824 */       this.tempStack[(this.tempStackTop + 1)] = act;
/*  825 */       next_state = act;
/*  826 */       act = Parser.tAction(next_state, tok);
/*      */     }
/*      */ 
/*  832 */     int root = 0;
/*  833 */     for (int i = Parser.asi(next_state); Parser.asr[i] != 0; i++) {
/*  834 */       int symbol = Parser.asr[i];
/*  835 */       if ((symbol != 68) && (symbol != 110)) {
/*  836 */         if (root == 0) {
/*  837 */           this.list[symbol] = symbol;
/*      */         } else {
/*  839 */           this.list[symbol] = this.list[root];
/*  840 */           this.list[root] = symbol;
/*      */         }
/*  842 */         root = symbol;
/*      */       }
/*      */     }
/*      */ 
/*  846 */     if (stck[stack_top] != next_state) {
/*  847 */       for (i = Parser.asi(stck[stack_top]); Parser.asr[i] != 0; i++) {
/*  848 */         int symbol = Parser.asr[i];
/*  849 */         if ((symbol != 68) && (symbol != 110) && (this.list[symbol] == 0)) {
/*  850 */           if (root == 0) {
/*  851 */             this.list[symbol] = symbol;
/*      */           } else {
/*  853 */             this.list[symbol] = this.list[root];
/*  854 */             this.list[root] = symbol;
/*      */           }
/*  856 */           root = symbol;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  861 */     i = this.list[root];
/*  862 */     this.list[root] = 0;
/*  863 */     root = i;
/*      */ 
/*  869 */     int symbol = root;
/*  870 */     while (symbol != 0) {
/*  871 */       if ((symbol == 68) && (this.lexStream.afterEol(this.buffer[repair.bufferPosition])))
/*  872 */         k = 10;
/*      */       else {
/*  874 */         k = 0;
/*      */       }
/*  876 */       j = parseCheck(stck, stack_top, symbol, repair.bufferPosition);
/*  877 */       if (j > repair.distance) {
/*  878 */         repair.misspellIndex = k;
/*  879 */         repair.distance = j;
/*  880 */         repair.symbol = symbol;
/*  881 */         repair.code = 3;
/*  882 */       } else if ((j == repair.distance) && (k > repair.misspellIndex)) {
/*  883 */         repair.misspellIndex = k;
/*  884 */         repair.distance = j;
/*  885 */         repair.symbol = symbol;
/*  886 */         repair.code = 3;
/*      */       }
/*      */ 
/*  889 */       symbol = this.list[symbol];
/*      */     }
/*      */ 
/*  896 */     symbol = root;
/*      */ 
/*  898 */     if (this.buffer[repair.bufferPosition] != 0) {
/*  899 */       while (symbol != 0) {
/*  900 */         if ((symbol == 68) && (this.lexStream.afterEol(this.buffer[(repair.bufferPosition + 1)])))
/*  901 */           k = 10;
/*      */         else {
/*  903 */           k = misspell(symbol, this.buffer[repair.bufferPosition]);
/*      */         }
/*  905 */         j = parseCheck(stck, stack_top, symbol, repair.bufferPosition + 1);
/*  906 */         if (j > repair.distance) {
/*  907 */           repair.misspellIndex = k;
/*  908 */           repair.distance = j;
/*  909 */           repair.symbol = symbol;
/*  910 */           repair.code = 5;
/*  911 */         } else if ((j == repair.distance) && (k > repair.misspellIndex)) {
/*  912 */           repair.misspellIndex = k;
/*  913 */           repair.symbol = symbol;
/*  914 */           repair.code = 5;
/*      */         }
/*  916 */         i = symbol;
/*  917 */         symbol = this.list[symbol];
/*  918 */         this.list[i] = 0;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  928 */     for (i = Parser.nasi(stck[stack_top]); Parser.nasr[i] != 0; i++) {
/*  929 */       symbol = Parser.nasr[i] + 'n';
/*  930 */       j = parseCheck(stck, stack_top, symbol, repair.bufferPosition + 1);
/*  931 */       if (j > repair.distance) {
/*  932 */         repair.misspellIndex = 0;
/*  933 */         repair.distance = j;
/*  934 */         repair.symbol = symbol;
/*  935 */         repair.code = 4;
/*      */       }
/*      */ 
/*  938 */       j = parseCheck(stck, stack_top, symbol, repair.bufferPosition);
/*  939 */       if ((j > repair.distance) || ((j == repair.distance) && (repair.code == 4))) {
/*  940 */         repair.misspellIndex = 0;
/*  941 */         repair.distance = j;
/*  942 */         repair.symbol = symbol;
/*  943 */         repair.code = 3;
/*      */       }
/*      */     }
/*      */ 
/*  947 */     return repair;
/*      */   }
/*      */ 
/*      */   private RepairCandidate primaryDiagnosis(PrimaryRepairInfo repair)
/*      */   {
/*  965 */     int prevtok = this.buffer[(repair.bufferPosition - 1)];
/*  966 */     int curtok = this.buffer[repair.bufferPosition];
/*      */ 
/*  968 */     switch (repair.code)
/*      */     {
/*      */     case 2:
/*      */     case 3:
/*      */       int name_index;
/*      */       int name_index;
/*  971 */       if (repair.symbol > 110)
/*  972 */         name_index = getNtermIndex(this.stack[this.stateStackTop], 
/*  973 */           repair.symbol, 
/*  974 */           repair.bufferPosition);
/*  975 */       else name_index = getTermIndex(this.stack, 
/*  976 */           this.stateStackTop, 
/*  977 */           repair.symbol, 
/*  978 */           repair.bufferPosition);
/*      */ 
/*  980 */       int t = repair.code == 3 ? prevtok : curtok;
/*  981 */       reportError(repair.code, name_index, t, t);
/*  982 */       break;
/*      */     case 4:
/*  985 */       int name_index = getNtermIndex(this.stack[this.stateStackTop], 
/*  986 */         repair.symbol, 
/*  987 */         repair.bufferPosition + 1);
/*  988 */       reportError(repair.code, name_index, curtok, curtok);
/*  989 */       break;
/*      */     case 5:
/*      */       int name_index;
/*      */       int name_index;
/*  992 */       if (repair.misspellIndex >= 6) {
/*  993 */         name_index = Parser.terminal_index[repair.symbol];
/*      */       }
/*      */       else {
/*  996 */         name_index = getTermIndex(this.stack, this.stateStackTop, 
/*  997 */           repair.symbol, 
/*  998 */           repair.bufferPosition + 1);
/*  999 */         if (name_index != Parser.terminal_index[repair.symbol])
/* 1000 */           repair.code = 4;
/*      */       }
/* 1002 */       reportError(repair.code, name_index, curtok, curtok);
/* 1003 */       break;
/*      */     case 7:
/* 1006 */       reportError(repair.code, 
/* 1007 */         Parser.terminal_index[repair.symbol], 
/* 1008 */         curtok, 
/* 1009 */         this.lexStream.next(curtok));
/* 1010 */       break;
/*      */     case 9:
/* 1013 */       for (int i = 0; i < this.scopeStackTop; i++) {
/* 1014 */         reportError(repair.code, 
/* 1015 */           -this.scopeIndex[i], 
/* 1016 */           this.locationStack[this.scopePosition[i]], 
/* 1017 */           prevtok, 
/* 1018 */           Parser.non_terminal_index[Parser.scope_lhs[this.scopeIndex[i]]]);
/*      */       }
/*      */ 
/* 1021 */       repair.symbol = (Parser.scope_lhs[this.scopeIndex[this.scopeStackTop]] + 'n');
/* 1022 */       this.stateStackTop = this.scopePosition[this.scopeStackTop];
/* 1023 */       reportError(repair.code, 
/* 1024 */         -this.scopeIndex[this.scopeStackTop], 
/* 1025 */         this.locationStack[this.scopePosition[this.scopeStackTop]], 
/* 1026 */         prevtok, 
/* 1027 */         getNtermIndex(this.stack[this.stateStackTop], 
/* 1028 */         repair.symbol, 
/* 1029 */         repair.bufferPosition));
/*      */ 
/* 1031 */       break;
/*      */     case 6:
/*      */     case 8:
/*      */     default:
/* 1034 */       reportError(repair.code, Parser.terminal_index[110], curtok, curtok);
/*      */     }
/*      */ 
/* 1041 */     RepairCandidate candidate = new RepairCandidate();
/* 1042 */     switch (repair.code) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 9:
/* 1046 */       candidate.symbol = repair.symbol;
/* 1047 */       candidate.location = this.buffer[repair.bufferPosition];
/* 1048 */       this.lexStream.reset(this.buffer[repair.bufferPosition]);
/* 1049 */       break;
/*      */     case 4:
/*      */     case 5:
/* 1053 */       candidate.symbol = repair.symbol;
/* 1054 */       candidate.location = this.buffer[repair.bufferPosition];
/* 1055 */       this.lexStream.reset(this.buffer[(repair.bufferPosition + 1)]);
/* 1056 */       break;
/*      */     case 7:
/* 1059 */       candidate.symbol = repair.symbol;
/* 1060 */       candidate.location = this.buffer[repair.bufferPosition];
/* 1061 */       this.lexStream.reset(this.buffer[(repair.bufferPosition + 2)]);
/* 1062 */       break;
/*      */     case 6:
/*      */     case 8:
/*      */     default:
/* 1065 */       candidate.location = this.buffer[(repair.bufferPosition + 1)];
/* 1066 */       candidate.symbol = 
/* 1067 */         this.lexStream.kind(this.buffer[(repair.bufferPosition + 1)]);
/* 1068 */       this.lexStream.reset(this.buffer[(repair.bufferPosition + 2)]);
/*      */     }
/*      */ 
/* 1073 */     return candidate;
/*      */   }
/*      */ 
/*      */   private int getTermIndex(int[] stck, int stack_top, int tok, int buffer_position)
/*      */   {
/* 1093 */     int act = stck[stack_top];
/* 1094 */     int max_pos = stack_top;
/* 1095 */     int highest_symbol = tok;
/*      */ 
/* 1097 */     this.tempStackTop = (stack_top - 1);
/*      */ 
/* 1105 */     this.lexStream.reset(this.buffer[buffer_position]);
/* 1106 */     act = Parser.tAction(act, tok);
/* 1107 */     while (act <= 703)
/*      */     {
/*      */       do
/*      */       {
/* 1113 */         this.tempStackTop -= Parser.rhs[act] - 1;
/* 1114 */         int lhs_symbol = Parser.lhs[act];
/* 1115 */         act = this.tempStackTop > max_pos ? 
/* 1116 */           this.tempStack[this.tempStackTop] : 
/* 1117 */           stck[this.tempStackTop];
/* 1118 */         act = Parser.ntAction(act, lhs_symbol);
/* 1119 */       }while (act <= 703);
/*      */ 
/* 1126 */       max_pos = max_pos < this.tempStackTop ? max_pos : this.tempStackTop;
/* 1127 */       this.tempStack[(this.tempStackTop + 1)] = act;
/* 1128 */       act = Parser.tAction(act, tok);
/*      */     }
/*      */ 
/* 1143 */     this.tempStackTop += 1;
/*      */ 
/* 1145 */     int threshold = this.tempStackTop;
/*      */ 
/* 1147 */     tok = this.lexStream.kind(this.buffer[buffer_position]);
/* 1148 */     this.lexStream.reset(this.buffer[(buffer_position + 1)]);
/*      */ 
/* 1150 */     if (act > 12741) {
/* 1151 */       act -= 12741;
/*      */     } else {
/* 1153 */       this.tempStack[(this.tempStackTop + 1)] = act;
/* 1154 */       act = Parser.tAction(act, tok);
/*      */     }
/*      */ 
/* 1157 */     while (act <= 703)
/*      */     {
/*      */       do
/*      */       {
/* 1163 */         this.tempStackTop -= Parser.rhs[act] - 1;
/*      */ 
/* 1165 */         if (this.tempStackTop < threshold) {
/* 1166 */           return highest_symbol > 110 ? 
/* 1167 */             Parser.non_terminal_index[(highest_symbol - 110)] : 
/* 1168 */             Parser.terminal_index[highest_symbol];
/*      */         }
/*      */ 
/* 1171 */         int lhs_symbol = Parser.lhs[act];
/* 1172 */         if (this.tempStackTop == threshold)
/* 1173 */           highest_symbol = lhs_symbol + 110;
/* 1174 */         act = this.tempStackTop > max_pos ? 
/* 1175 */           this.tempStack[this.tempStackTop] : 
/* 1176 */           stck[this.tempStackTop];
/* 1177 */         act = Parser.ntAction(act, lhs_symbol);
/* 1178 */       }while (act <= 703);
/*      */ 
/* 1180 */       this.tempStack[(this.tempStackTop + 1)] = act;
/* 1181 */       act = Parser.tAction(act, tok);
/*      */     }
/*      */ 
/* 1184 */     return highest_symbol > 110 ? 
/* 1185 */       Parser.non_terminal_index[(highest_symbol - 110)] : 
/* 1186 */       Parser.terminal_index[highest_symbol];
/*      */   }
/*      */ 
/*      */   private int getNtermIndex(int start, int sym, int buffer_position)
/*      */   {
/* 1200 */     int highest_symbol = sym - 110;
/* 1201 */     int tok = this.lexStream.kind(this.buffer[buffer_position]);
/* 1202 */     this.lexStream.reset(this.buffer[(buffer_position + 1)]);
/*      */ 
/* 1208 */     this.tempStackTop = 0;
/* 1209 */     this.tempStack[this.tempStackTop] = start;
/*      */ 
/* 1211 */     int act = Parser.ntAction(start, highest_symbol);
/* 1212 */     if (act > 703) {
/* 1213 */       this.tempStack[(this.tempStackTop + 1)] = act;
/* 1214 */       act = Parser.tAction(act, tok);
/*      */     }
/*      */ 
/* 1217 */     while (act <= 703)
/*      */     {
/*      */       do
/*      */       {
/* 1223 */         this.tempStackTop -= Parser.rhs[act] - 1;
/* 1224 */         if (this.tempStackTop < 0)
/* 1225 */           return Parser.non_terminal_index[highest_symbol];
/* 1226 */         if (this.tempStackTop == 0)
/* 1227 */           highest_symbol = Parser.lhs[act];
/* 1228 */         act = Parser.ntAction(this.tempStack[this.tempStackTop], Parser.lhs[act]);
/* 1229 */       }while (act <= 703);
/* 1230 */       this.tempStack[(this.tempStackTop + 1)] = act;
/* 1231 */       act = Parser.tAction(act, tok);
/*      */     }
/*      */ 
/* 1234 */     return Parser.non_terminal_index[highest_symbol];
/*      */   }
/*      */ 
/*      */   private int misspell(int sym, int tok)
/*      */   {
/* 1249 */     char[] name = Parser.name[Parser.terminal_index[sym]].toCharArray();
/* 1250 */     int n = name.length;
/* 1251 */     char[] s1 = new char[n + 1];
/* 1252 */     for (int k = 0; k < n; k++) {
/* 1253 */       char c = name[k];
/* 1254 */       s1[k] = ScannerHelper.toLowerCase(c);
/*      */     }
/* 1256 */     s1[n] = '\000';
/*      */ 
/* 1261 */     char[] tokenName = this.lexStream.name(tok);
/* 1262 */     int len = tokenName.length;
/* 1263 */     int m = len < 41 ? len : 41;
/* 1264 */     char[] s2 = new char[m + 1];
/* 1265 */     for (int k = 0; k < m; k++) {
/* 1266 */       char c = tokenName[k];
/* 1267 */       s2[k] = ScannerHelper.toLowerCase(c);
/*      */     }
/* 1269 */     s2[m] = '\000';
/*      */ 
/* 1283 */     if ((n == 1) && (m == 1) && (
/* 1284 */       ((s1[0] == ';') && (s2[0] == ',')) || 
/* 1285 */       ((s1[0] == ',') && (s2[0] == ';')) || 
/* 1286 */       ((s1[0] == ';') && (s2[0] == ':')) || 
/* 1287 */       ((s1[0] == ':') && (s2[0] == ';')) || 
/* 1288 */       ((s1[0] == '.') && (s2[0] == ',')) || 
/* 1289 */       ((s1[0] == ',') && (s2[0] == '.')) || 
/* 1290 */       ((s1[0] == '\'') && (s2[0] == '"')) || (
/* 1291 */       (s1[0] == '"') && (s2[0] == '\'')))) {
/* 1292 */       return 3;
/*      */     }
/*      */ 
/* 1307 */     int count = 0;
/* 1308 */     int prefix_length = 0;
/* 1309 */     int num_errors = 0;
/*      */ 
/* 1311 */     int i = 0;
/* 1312 */     int j = 0;
/* 1313 */     while ((i < n) && (j < m)) {
/* 1314 */       if (s1[i] == s2[j]) {
/* 1315 */         count++;
/* 1316 */         i++;
/* 1317 */         j++;
/* 1318 */         if (num_errors == 0)
/* 1319 */           prefix_length++;
/*      */       }
/* 1321 */       else if ((s1[(i + 1)] == s2[j]) && (s1[i] == s2[(j + 1)])) {
/* 1322 */         count += 2;
/* 1323 */         i += 2;
/* 1324 */         j += 2;
/* 1325 */         num_errors++;
/* 1326 */       } else if (s1[(i + 1)] == s2[(j + 1)]) {
/* 1327 */         i++;
/* 1328 */         j++;
/* 1329 */         num_errors++;
/*      */       } else {
/* 1331 */         if (n - i > m - j) {
/* 1332 */           i++;
/* 1333 */         } else if (m - j > n - i) {
/* 1334 */           j++;
/*      */         } else {
/* 1336 */           i++;
/* 1337 */           j++;
/*      */         }
/* 1339 */         num_errors++;
/*      */       }
/*      */     }
/*      */ 
/* 1343 */     if ((i < n) || (j < m)) {
/* 1344 */       num_errors++;
/*      */     }
/* 1346 */     if (num_errors > (n < m ? n : m) / 6 + 1) {
/* 1347 */       count = prefix_length;
/*      */     }
/* 1349 */     return count * 10 / ((n < len ? len : n) + num_errors);
/*      */   }
/*      */ 
/*      */   private PrimaryRepairInfo scopeTrial(int[] stck, int stack_top, PrimaryRepairInfo repair) {
/* 1353 */     this.stateSeen = new int[this.stackLength];
/* 1354 */     for (int i = 0; i < this.stackLength; i++) {
/* 1355 */       this.stateSeen[i] = -1;
/*      */     }
/* 1357 */     this.statePoolTop = 0;
/* 1358 */     this.statePool = new StateInfo[this.stackLength];
/*      */ 
/* 1360 */     scopeTrialCheck(stck, stack_top, repair, 0);
/*      */ 
/* 1362 */     this.stateSeen = null;
/* 1363 */     this.statePoolTop = 0;
/*      */ 
/* 1365 */     repair.code = 9;
/* 1366 */     repair.misspellIndex = 10;
/*      */ 
/* 1368 */     return repair;
/*      */   }
/*      */ 
/*      */   private void scopeTrialCheck(int[] stck, int stack_top, PrimaryRepairInfo repair, int indx) {
/* 1372 */     if (indx > 20) return;
/*      */ 
/* 1374 */     int act = stck[stack_top];
/*      */ 
/* 1376 */     for (int i = this.stateSeen[stack_top]; i != -1; i = this.statePool[i].next) {
/* 1377 */       if (this.statePool[i].state == act) return;
/*      */     }
/*      */ 
/* 1380 */     int old_state_pool_top = this.statePoolTop++;
/* 1381 */     if (this.statePoolTop >= this.statePool.length) {
/* 1382 */       System.arraycopy(this.statePool, 0, this.statePool = new StateInfo[this.statePoolTop * 2], 0, this.statePoolTop);
/*      */     }
/*      */ 
/* 1385 */     this.statePool[old_state_pool_top] = new StateInfo(act, this.stateSeen[stack_top]);
/* 1386 */     this.stateSeen[stack_top] = old_state_pool_top;
/*      */ 
/* 1388 */     for (int i = 0; i < 134; i++)
/*      */     {
/* 1393 */       act = stck[stack_top];
/* 1394 */       this.tempStackTop = (stack_top - 1);
/* 1395 */       int max_pos = stack_top;
/* 1396 */       int tok = Parser.scope_la[i];
/* 1397 */       this.lexStream.reset(this.buffer[repair.bufferPosition]);
/* 1398 */       act = Parser.tAction(act, tok);
/* 1399 */       while (act <= 703)
/*      */       {
/*      */         do
/*      */         {
/* 1405 */           this.tempStackTop -= Parser.rhs[act] - 1;
/* 1406 */           int lhs_symbol = Parser.lhs[act];
/* 1407 */           act = this.tempStackTop > max_pos ? 
/* 1408 */             this.tempStack[this.tempStackTop] : 
/* 1409 */             stck[this.tempStackTop];
/* 1410 */           act = Parser.ntAction(act, lhs_symbol);
/* 1411 */         }while (act <= 703);
/* 1412 */         if (this.tempStackTop + 1 >= this.stackLength)
/* 1413 */           return;
/* 1414 */         max_pos = max_pos < this.tempStackTop ? max_pos : this.tempStackTop;
/* 1415 */         this.tempStack[(this.tempStackTop + 1)] = act;
/* 1416 */         act = Parser.tAction(act, tok);
/*      */       }
/*      */ 
/* 1425 */       if (act == 12741)
/*      */         continue;
/* 1427 */       int k = Parser.scope_prefix[i];
/* 1428 */       int j = this.tempStackTop + 1;
/* 1429 */       while ((j >= max_pos + 1) && 
/* 1430 */         (Parser.in_symbol(this.tempStack[j]) == Parser.scope_rhs[k])) {
/* 1431 */         k++;
/*      */ 
/* 1430 */         j--;
/*      */       }
/*      */ 
/* 1433 */       if (j == max_pos) {
/* 1434 */         j = max_pos;
/* 1435 */         while ((j >= 1) && (Parser.in_symbol(stck[j]) == Parser.scope_rhs[k]))
/*      */         {
/* 1437 */           k++;
/*      */ 
/* 1436 */           j--;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1448 */       int marked_pos = max_pos < stack_top ? max_pos + 1 : stack_top;
/* 1449 */       if ((Parser.scope_rhs[k] == 0) && (j < marked_pos)) {
/* 1450 */         int stack_position = j;
/* 1451 */         j = Parser.scope_state_set[i];
/* 1452 */         while ((stck[stack_position] != Parser.scope_state[j]) && 
/* 1453 */           (Parser.scope_state[j] != 0)) {
/* 1454 */           j++;
/*      */         }
/*      */ 
/* 1462 */         if (Parser.scope_state[j] != 0) {
/* 1463 */           int previous_distance = repair.distance;
/* 1464 */           int distance = parseCheck(stck, 
/* 1465 */             stack_position, 
/* 1466 */             Parser.scope_lhs[i] + 'n', 
/* 1467 */             repair.bufferPosition);
/*      */ 
/* 1484 */           if (distance - repair.bufferPosition + 1 < 3) {
/* 1485 */             int top = stack_position;
/* 1486 */             act = Parser.ntAction(stck[top], Parser.scope_lhs[i]);
/* 1487 */             while (act <= 703) {
/* 1488 */               if (Parser.rules_compliance[act] > this.options.sourceLevel) {
/*      */                 break;
/*      */               }
/* 1491 */               top -= Parser.rhs[act] - 1;
/* 1492 */               act = Parser.ntAction(stck[top], Parser.lhs[act]);
/*      */             }
/* 1494 */             top++;
/*      */ 
/* 1496 */             j = act;
/* 1497 */             act = stck[top];
/* 1498 */             stck[top] = j;
/* 1499 */             scopeTrialCheck(stck, top, repair, indx + 1);
/* 1500 */             stck[top] = act;
/* 1501 */           } else if (distance > repair.distance) {
/* 1502 */             this.scopeStackTop = indx;
/* 1503 */             repair.distance = distance;
/*      */           }
/*      */ 
/* 1506 */           if ((this.lexStream.kind(this.buffer[repair.bufferPosition]) == 68) && 
/* 1507 */             (repair.distance == previous_distance)) {
/* 1508 */             this.scopeStackTop = indx;
/* 1509 */             repair.distance = 30;
/*      */           }
/*      */ 
/* 1520 */           if (repair.distance > previous_distance) {
/* 1521 */             this.scopeIndex[indx] = i;
/* 1522 */             this.scopePosition[indx] = stack_position;
/* 1523 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean secondaryCheck(int[] stck, int stack_top, int buffer_position, int distance)
/*      */   {
/* 1543 */     for (int top = stack_top - 1; top >= 0; top--) {
/* 1544 */       int j = parseCheck(stck, top, 
/* 1545 */         this.lexStream.kind(this.buffer[buffer_position]), 
/* 1546 */         buffer_position + 1);
/* 1547 */       if ((j - buffer_position + 1 > 3) && (j > distance)) {
/* 1548 */         return true;
/*      */       }
/*      */     }
/* 1551 */     PrimaryRepairInfo repair = new PrimaryRepairInfo();
/* 1552 */     repair.bufferPosition = (buffer_position + 1);
/* 1553 */     repair.distance = distance;
/* 1554 */     repair = scopeTrial(stck, stack_top, repair);
/*      */ 
/* 1556 */     return (repair.distance - buffer_position > 3) && (repair.distance > distance);
/*      */   }
/*      */ 
/*      */   private RepairCandidate secondaryPhase(int error_token)
/*      */   {
/* 1572 */     SecondaryRepairInfo repair = new SecondaryRepairInfo();
/* 1573 */     SecondaryRepairInfo misplaced = new SecondaryRepairInfo();
/*      */ 
/* 1575 */     RepairCandidate candidate = new RepairCandidate();
/*      */ 
/* 1578 */     int next_last_index = 0;
/*      */ 
/* 1581 */     candidate.symbol = 0;
/*      */ 
/* 1583 */     repair.code = 0;
/* 1584 */     repair.distance = 0;
/* 1585 */     repair.recoveryOnNextStack = false;
/*      */ 
/* 1587 */     misplaced.distance = 0;
/* 1588 */     misplaced.recoveryOnNextStack = false;
/*      */ 
/* 1594 */     if (this.nextStackTop >= 0)
/*      */     {
/* 1597 */       this.buffer[2] = error_token;
/* 1598 */       this.buffer[1] = this.lexStream.previous(this.buffer[2]);
/* 1599 */       this.buffer[0] = this.lexStream.previous(this.buffer[1]);
/*      */ 
/* 1601 */       for (int k = 3; k < 31; k++) {
/* 1602 */         this.buffer[k] = this.lexStream.next(this.buffer[(k - 1)]);
/*      */       }
/* 1604 */       this.buffer[31] = this.lexStream.badtoken();
/*      */ 
/* 1611 */       next_last_index = 29;
/* 1612 */       while ((next_last_index >= 1) && 
/* 1613 */         (this.lexStream.kind(this.buffer[next_last_index]) == 68))
/* 1614 */         next_last_index--;
/* 1615 */       next_last_index++;
/*      */ 
/* 1617 */       int save_location = this.locationStack[this.nextStackTop];
/* 1618 */       int save_location_start = this.locationStartStack[this.nextStackTop];
/* 1619 */       this.locationStack[this.nextStackTop] = this.buffer[2];
/* 1620 */       this.locationStartStack[this.nextStackTop] = this.lexStream.start(this.buffer[2]);
/* 1621 */       misplaced.numDeletions = this.nextStackTop;
/* 1622 */       misplaced = misplacementRecovery(this.nextStack, this.nextStackTop, 
/* 1623 */         next_last_index, 
/* 1624 */         misplaced, true);
/* 1625 */       if (misplaced.recoveryOnNextStack) {
/* 1626 */         misplaced.distance += 1;
/*      */       }
/* 1628 */       repair.numDeletions = (this.nextStackTop + 31);
/* 1629 */       repair = secondaryRecovery(this.nextStack, this.nextStackTop, 
/* 1630 */         next_last_index, 
/* 1631 */         repair, true);
/* 1632 */       if (repair.recoveryOnNextStack) {
/* 1633 */         repair.distance += 1;
/*      */       }
/* 1635 */       this.locationStack[this.nextStackTop] = save_location;
/* 1636 */       this.locationStartStack[this.nextStackTop] = save_location_start;
/*      */     } else {
/* 1638 */       misplaced.numDeletions = this.stateStackTop;
/* 1639 */       repair.numDeletions = (this.stateStackTop + 31);
/*      */     }
/*      */ 
/* 1645 */     this.buffer[3] = error_token;
/*      */ 
/* 1647 */     this.buffer[2] = this.lexStream.previous(this.buffer[3]);
/* 1648 */     this.buffer[1] = this.lexStream.previous(this.buffer[2]);
/* 1649 */     this.buffer[0] = this.lexStream.previous(this.buffer[1]);
/*      */ 
/* 1651 */     for (int k = 4; k < 32; k++) {
/* 1652 */       this.buffer[k] = this.lexStream.next(this.buffer[(k - 1)]);
/*      */     }
/* 1654 */     int last_index = 29;
/* 1655 */     while ((last_index >= 1) && (this.lexStream.kind(this.buffer[last_index]) == 68))
/* 1656 */       last_index--;
/* 1657 */     last_index++;
/*      */ 
/* 1659 */     misplaced = misplacementRecovery(this.stack, this.stateStackTop, 
/* 1660 */       last_index, 
/* 1661 */       misplaced, false);
/*      */ 
/* 1663 */     repair = secondaryRecovery(this.stack, this.stateStackTop, 
/* 1664 */       last_index, repair, false);
/*      */ 
/* 1672 */     if ((misplaced.distance > 3) && (
/* 1673 */       (misplaced.numDeletions <= repair.numDeletions) || 
/* 1675 */       (misplaced.distance - misplaced.numDeletions >= 
/* 1675 */       repair.distance - repair.numDeletions))) {
/* 1676 */       repair.code = 8;
/* 1677 */       repair.stackPosition = misplaced.stackPosition;
/* 1678 */       repair.bufferPosition = 2;
/* 1679 */       repair.numDeletions = misplaced.numDeletions;
/* 1680 */       repair.distance = misplaced.distance;
/* 1681 */       repair.recoveryOnNextStack = misplaced.recoveryOnNextStack;
/*      */     }
/*      */ 
/* 1689 */     if (repair.recoveryOnNextStack) {
/* 1690 */       this.stateStackTop = this.nextStackTop;
/* 1691 */       for (int i = 0; i <= this.stateStackTop; i++) {
/* 1692 */         this.stack[i] = this.nextStack[i];
/*      */       }
/* 1694 */       this.buffer[2] = error_token;
/* 1695 */       this.buffer[1] = this.lexStream.previous(this.buffer[2]);
/* 1696 */       this.buffer[0] = this.lexStream.previous(this.buffer[1]);
/*      */ 
/* 1698 */       for (k = 3; k < 31; k++) {
/* 1699 */         this.buffer[k] = this.lexStream.next(this.buffer[(k - 1)]);
/*      */       }
/* 1701 */       this.buffer[31] = this.lexStream.badtoken();
/*      */ 
/* 1703 */       this.locationStack[this.nextStackTop] = this.buffer[2];
/* 1704 */       this.locationStartStack[this.nextStackTop] = this.lexStream.start(this.buffer[2]);
/* 1705 */       last_index = next_last_index;
/*      */     }
/*      */ 
/* 1712 */     if ((repair.code == 10) || (repair.code == 6)) {
/* 1713 */       PrimaryRepairInfo scope_repair = new PrimaryRepairInfo();
/*      */ 
/* 1715 */       scope_repair.distance = 0;
/* 1716 */       scope_repair.bufferPosition = 2;
/* 1717 */       while ((scope_repair.bufferPosition <= repair.bufferPosition) && 
/* 1718 */         (repair.code != 9)) {
/* 1719 */         scope_repair = scopeTrial(this.stack, this.stateStackTop, scope_repair);
/* 1720 */         int j = scope_repair.distance == 30 ? 
/* 1721 */           last_index : 
/* 1722 */           scope_repair.distance;
/* 1723 */         k = scope_repair.bufferPosition - 1;
/* 1724 */         if ((j - k > 3) && (j - k > repair.distance - repair.numDeletions)) {
/* 1725 */           repair.code = 9;
/* 1726 */           int i = this.scopeIndex[this.scopeStackTop];
/* 1727 */           repair.symbol = (Parser.scope_lhs[i] + 'n');
/* 1728 */           repair.stackPosition = this.stateStackTop;
/* 1729 */           repair.bufferPosition = scope_repair.bufferPosition;
/*      */         }
/* 1718 */         scope_repair.bufferPosition += 1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1740 */     if ((repair.code == 0) && (this.lexStream.kind(this.buffer[last_index]) == 68)) {
/* 1741 */       PrimaryRepairInfo scope_repair = new PrimaryRepairInfo();
/*      */ 
/* 1743 */       scope_repair.bufferPosition = last_index;
/* 1744 */       scope_repair.distance = 0;
/* 1745 */       int top = this.stateStackTop;
/* 1746 */       for (; (top >= 0) && (repair.code == 0); top--)
/*      */       {
/* 1748 */         scope_repair = scopeTrial(this.stack, top, scope_repair);
/* 1749 */         if (scope_repair.distance <= 0)
/*      */           continue;
/* 1751 */         repair.code = 9;
/* 1752 */         int i = this.scopeIndex[this.scopeStackTop];
/* 1753 */         repair.symbol = (Parser.scope_lhs[i] + 'n');
/* 1754 */         repair.stackPosition = top;
/* 1755 */         repair.bufferPosition = scope_repair.bufferPosition;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1764 */     if (repair.code == 0) {
/* 1765 */       return candidate;
/*      */     }
/* 1767 */     secondaryDiagnosis(repair);
/*      */ 
/* 1772 */     switch (repair.code) {
/*      */     case 8:
/* 1774 */       candidate.location = this.buffer[2];
/* 1775 */       candidate.symbol = this.lexStream.kind(this.buffer[2]);
/* 1776 */       this.lexStream.reset(this.lexStream.next(this.buffer[2]));
/*      */ 
/* 1778 */       break;
/*      */     case 6:
/* 1781 */       candidate.location = this.buffer[repair.bufferPosition];
/* 1782 */       candidate.symbol = 
/* 1783 */         this.lexStream.kind(this.buffer[repair.bufferPosition]);
/* 1784 */       this.lexStream.reset(this.lexStream.next(this.buffer[repair.bufferPosition]));
/*      */ 
/* 1786 */       break;
/*      */     case 7:
/*      */     default:
/* 1789 */       candidate.symbol = repair.symbol;
/* 1790 */       candidate.location = this.buffer[repair.bufferPosition];
/* 1791 */       this.lexStream.reset(this.buffer[repair.bufferPosition]);
/*      */     }
/*      */ 
/* 1796 */     return candidate;
/*      */   }
/*      */ 
/*      */   private SecondaryRepairInfo misplacementRecovery(int[] stck, int stack_top, int last_index, SecondaryRepairInfo repair, boolean stack_flag)
/*      */   {
/* 1806 */     int previous_loc = this.buffer[2];
/* 1807 */     int stack_deletions = 0;
/*      */ 
/* 1809 */     for (int top = stack_top - 1; top >= 0; top--) {
/* 1810 */       if (this.locationStack[top] < previous_loc) {
/* 1811 */         stack_deletions++;
/*      */       }
/* 1813 */       previous_loc = this.locationStack[top];
/*      */ 
/* 1815 */       int j = parseCheck(stck, top, this.lexStream.kind(this.buffer[2]), 3);
/* 1816 */       if (j == 30) {
/* 1817 */         j = last_index;
/*      */       }
/* 1819 */       if ((j > 3) && (j - stack_deletions > repair.distance - repair.numDeletions)) {
/* 1820 */         repair.stackPosition = top;
/* 1821 */         repair.distance = j;
/* 1822 */         repair.numDeletions = stack_deletions;
/* 1823 */         repair.recoveryOnNextStack = stack_flag;
/*      */       }
/*      */     }
/*      */ 
/* 1827 */     return repair;
/*      */   }
/*      */ 
/*      */   private SecondaryRepairInfo secondaryRecovery(int[] stck, int stack_top, int last_index, SecondaryRepairInfo repair, boolean stack_flag)
/*      */   {
/* 1838 */     int stack_deletions = 0;
/*      */ 
/* 1840 */     int previous_loc = this.buffer[2];
/* 1841 */     for (int top = stack_top; (top >= 0) && (repair.numDeletions >= stack_deletions); top--) {
/* 1842 */       if (this.locationStack[top] < previous_loc) {
/* 1843 */         stack_deletions++;
/*      */       }
/* 1845 */       previous_loc = this.locationStack[top];
/*      */ 
/* 1847 */       int i = 2;
/* 1848 */       while ((i <= last_index - 3 + 1) && 
/* 1849 */         (repair.numDeletions >= stack_deletions + i - 1)) {
/* 1850 */         int j = parseCheck(stck, top, this.lexStream.kind(this.buffer[i]), i + 1);
/*      */ 
/* 1852 */         if (j == 30) {
/* 1853 */           j = last_index;
/*      */         }
/* 1855 */         if (j - i + 1 > 3) {
/* 1856 */           int k = stack_deletions + i - 1;
/* 1857 */           if ((k < repair.numDeletions) || 
/* 1858 */             (j - k > repair.distance - repair.numDeletions) || (
/* 1859 */             (repair.code == 10) && (j - k == repair.distance - repair.numDeletions))) {
/* 1860 */             repair.code = 6;
/* 1861 */             repair.distance = j;
/* 1862 */             repair.stackPosition = top;
/* 1863 */             repair.bufferPosition = i;
/* 1864 */             repair.numDeletions = k;
/* 1865 */             repair.recoveryOnNextStack = stack_flag;
/*      */           }
/*      */         }
/*      */ 
/* 1869 */         for (int l = Parser.nasi(stck[top]); (l >= 0) && (Parser.nasr[l] != 0); l++) {
/* 1870 */           int symbol = Parser.nasr[l] + 'n';
/* 1871 */           j = parseCheck(stck, top, symbol, i);
/* 1872 */           if (j == 30) {
/* 1873 */             j = last_index;
/*      */           }
/* 1875 */           if (j - i + 1 > 3) {
/* 1876 */             int k = stack_deletions + i - 1;
/* 1877 */             if ((k < repair.numDeletions) || (j - k > repair.distance - repair.numDeletions)) {
/* 1878 */               repair.code = 10;
/* 1879 */               repair.symbol = symbol;
/* 1880 */               repair.distance = j;
/* 1881 */               repair.stackPosition = top;
/* 1882 */               repair.bufferPosition = i;
/* 1883 */               repair.numDeletions = k;
/* 1884 */               repair.recoveryOnNextStack = stack_flag;
/*      */             }
/*      */           }
/*      */         }
/* 1849 */         i++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1891 */     return repair;
/*      */   }
/*      */ 
/*      */   private void secondaryDiagnosis(SecondaryRepairInfo repair)
/*      */   {
/* 1902 */     switch (repair.code) {
/*      */     case 9:
/* 1904 */       if (repair.stackPosition < this.stateStackTop) {
/* 1905 */         reportError(6, 
/* 1906 */           Parser.terminal_index[110], 
/* 1907 */           this.locationStack[repair.stackPosition], 
/* 1908 */           this.buffer[1]);
/*      */       }
/* 1910 */       for (int i = 0; i < this.scopeStackTop; i++) {
/* 1911 */         reportError(9, 
/* 1912 */           -this.scopeIndex[i], 
/* 1913 */           this.locationStack[this.scopePosition[i]], 
/* 1914 */           this.buffer[1], 
/* 1915 */           Parser.non_terminal_index[Parser.scope_lhs[this.scopeIndex[i]]]);
/*      */       }
/*      */ 
/* 1918 */       repair.symbol = (Parser.scope_lhs[this.scopeIndex[this.scopeStackTop]] + 'n');
/* 1919 */       this.stateStackTop = this.scopePosition[this.scopeStackTop];
/* 1920 */       reportError(9, 
/* 1921 */         -this.scopeIndex[this.scopeStackTop], 
/* 1922 */         this.locationStack[this.scopePosition[this.scopeStackTop]], 
/* 1923 */         this.buffer[1], 
/* 1924 */         getNtermIndex(this.stack[this.stateStackTop], 
/* 1925 */         repair.symbol, 
/* 1926 */         repair.bufferPosition));
/*      */ 
/* 1928 */       break;
/*      */     default:
/* 1931 */       reportError(repair.code, 
/* 1932 */         repair.code == 10 ? 
/* 1933 */         getNtermIndex(this.stack[repair.stackPosition], 
/* 1934 */         repair.symbol, 
/* 1935 */         repair.bufferPosition) : 
/* 1936 */         Parser.terminal_index[110], 
/* 1937 */         this.locationStack[repair.stackPosition], 
/* 1938 */         this.buffer[(repair.bufferPosition - 1)]);
/* 1939 */       this.stateStackTop = repair.stackPosition;
/*      */     }
/*      */   }
/*      */ 
/*      */   private int parseCheck(int[] stck, int stack_top, int first_token, int buffer_position)
/*      */   {
/* 1962 */     int act = stck[stack_top];
/*      */     int max_pos;
/*      */     int indx;
/*      */     int ct;
/* 1963 */     if (first_token > 110) {
/* 1964 */       this.tempStackTop = stack_top;
/* 1965 */       if (this.DEBUG_PARSECHECK) {
/* 1966 */         System.out.println(this.tempStackTop);
/*      */       }
/* 1968 */       int max_pos = stack_top;
/* 1969 */       int indx = buffer_position;
/* 1970 */       int ct = this.lexStream.kind(this.buffer[indx]);
/* 1971 */       this.lexStream.reset(this.lexStream.next(this.buffer[indx]));
/* 1972 */       int lhs_symbol = first_token - 110;
/* 1973 */       act = Parser.ntAction(act, lhs_symbol);
/* 1974 */       if (act <= 703)
/*      */       {
/*      */         do {
/* 1977 */           this.tempStackTop -= Parser.rhs[act] - 1;
/*      */ 
/* 1979 */           if (this.DEBUG_PARSECHECK) {
/* 1980 */             System.out.print(this.tempStackTop);
/* 1981 */             System.out.print(" (");
/* 1982 */             System.out.print(-(Parser.rhs[act] - 1));
/* 1983 */             System.out.print(") [max:");
/* 1984 */             System.out.print(max_pos);
/* 1985 */             System.out.print("]\tprocess_non_terminal\t");
/* 1986 */             System.out.print(act);
/* 1987 */             System.out.print("\t");
/* 1988 */             System.out.print(Parser.name[Parser.non_terminal_index[Parser.lhs[act]]]);
/* 1989 */             System.out.println();
/*      */           }
/*      */ 
/* 1992 */           if (Parser.rules_compliance[act] > this.options.sourceLevel) {
/* 1993 */             return 0;
/*      */           }
/* 1995 */           lhs_symbol = Parser.lhs[act];
/* 1996 */           act = this.tempStackTop > max_pos ? 
/* 1997 */             this.tempStack[this.tempStackTop] : 
/* 1998 */             stck[this.tempStackTop];
/* 1999 */           act = Parser.ntAction(act, lhs_symbol);
/* 2000 */         }while (act <= 703);
/*      */ 
/* 2002 */         max_pos = max_pos < this.tempStackTop ? max_pos : this.tempStackTop;
/*      */       }
/*      */     } else {
/* 2005 */       this.tempStackTop = (stack_top - 1);
/*      */ 
/* 2007 */       if (this.DEBUG_PARSECHECK) {
/* 2008 */         System.out.println(this.tempStackTop);
/*      */       }
/*      */ 
/* 2011 */       max_pos = this.tempStackTop;
/* 2012 */       indx = buffer_position - 1;
/* 2013 */       ct = first_token;
/* 2014 */       this.lexStream.reset(this.buffer[buffer_position]);
/*      */     }
/*      */     while (true)
/*      */     {
/* 2018 */       if (this.DEBUG_PARSECHECK) {
/* 2019 */         System.out.print(this.tempStackTop + 1);
/* 2020 */         System.out.print(" (+1) [max:");
/* 2021 */         System.out.print(max_pos);
/* 2022 */         System.out.print("]\tprocess_terminal    \t");
/* 2023 */         System.out.print(ct);
/* 2024 */         System.out.print("\t");
/* 2025 */         System.out.print(Parser.name[Parser.terminal_index[ct]]);
/* 2026 */         System.out.println();
/*      */       }
/*      */ 
/* 2029 */       if (++this.tempStackTop >= this.stackLength)
/* 2030 */         return indx;
/* 2031 */       this.tempStack[this.tempStackTop] = act;
/*      */ 
/* 2033 */       act = Parser.tAction(act, ct);
/*      */ 
/* 2035 */       if (act <= 703) {
/* 2036 */         this.tempStackTop -= 1;
/*      */ 
/* 2038 */         if (this.DEBUG_PARSECHECK) {
/* 2039 */           System.out.print(this.tempStackTop);
/* 2040 */           System.out.print(" (-1) [max:");
/* 2041 */           System.out.print(max_pos);
/* 2042 */           System.out.print("]\treduce");
/* 2043 */           System.out.println();
/*      */         }
/* 2045 */       } else if ((act < 12740) || 
/* 2046 */         (act > 12741)) {
/* 2047 */         if (indx == 30)
/* 2048 */           return indx;
/* 2049 */         indx++;
/* 2050 */         ct = this.lexStream.kind(this.buffer[indx]);
/* 2051 */         this.lexStream.reset(this.lexStream.next(this.buffer[indx]));
/* 2052 */         if (act > 12741) {
/* 2053 */           act -= 12741;
/*      */ 
/* 2055 */           if (this.DEBUG_PARSECHECK) {
/* 2056 */             System.out.print(this.tempStackTop);
/* 2057 */             System.out.print("\tshift reduce");
/* 2058 */             System.out.println();
/*      */           }
/*      */         }
/* 2061 */         else if (this.DEBUG_PARSECHECK) {
/* 2062 */           System.out.println("\tshift");
/*      */ 
/* 2064 */           continue;
/*      */         }
/*      */       } else {
/* 2066 */         if (act == 12740) {
/* 2067 */           return 30;
/*      */         }
/* 2069 */         return indx;
/*      */       }
/*      */ 
/*      */       do
/*      */       {
/* 2075 */         this.tempStackTop -= Parser.rhs[act] - 1;
/*      */ 
/* 2077 */         if (this.DEBUG_PARSECHECK) {
/* 2078 */           System.out.print(this.tempStackTop);
/* 2079 */           System.out.print(" (");
/* 2080 */           System.out.print(-(Parser.rhs[act] - 1));
/* 2081 */           System.out.print(") [max:");
/* 2082 */           System.out.print(max_pos);
/* 2083 */           System.out.print("]\tprocess_non_terminal\t");
/* 2084 */           System.out.print(act);
/* 2085 */           System.out.print("\t");
/* 2086 */           System.out.print(Parser.name[Parser.non_terminal_index[Parser.lhs[act]]]);
/* 2087 */           System.out.println();
/*      */         }
/*      */ 
/* 2090 */         if ((act <= 703) && 
/* 2091 */           (Parser.rules_compliance[act] > this.options.sourceLevel)) {
/* 2092 */           return 0;
/*      */         }
/*      */ 
/* 2095 */         int lhs_symbol = Parser.lhs[act];
/* 2096 */         act = this.tempStackTop > max_pos ? 
/* 2097 */           this.tempStack[this.tempStackTop] : 
/* 2098 */           stck[this.tempStackTop];
/* 2099 */         act = Parser.ntAction(act, lhs_symbol);
/* 2100 */       }while (act <= 703);
/*      */ 
/* 2102 */       max_pos = max_pos < this.tempStackTop ? max_pos : this.tempStackTop;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void reportError(int msgCode, int nameIndex, int leftToken, int rightToken) {
/* 2106 */     reportError(msgCode, nameIndex, leftToken, rightToken, 0);
/*      */   }
/*      */ 
/*      */   private void reportError(int msgCode, int nameIndex, int leftToken, int rightToken, int scopeNameIndex) {
/* 2110 */     int lToken = leftToken > rightToken ? rightToken : leftToken;
/*      */ 
/* 2112 */     if (lToken < rightToken)
/* 2113 */       reportSecondaryError(msgCode, nameIndex, lToken, rightToken, scopeNameIndex);
/*      */     else
/* 2115 */       reportPrimaryError(msgCode, nameIndex, rightToken, scopeNameIndex);
/*      */   }
/*      */ 
/*      */   private void reportPrimaryError(int msgCode, int nameIndex, int token, int scopeNameIndex)
/*      */   {
/*      */     String name;
/*      */     String name;
/* 2121 */     if (nameIndex >= 0)
/* 2122 */       name = Parser.readableName[nameIndex];
/*      */     else {
/* 2124 */       name = Util.EMPTY_STRING;
/*      */     }
/*      */ 
/* 2127 */     int errorStart = this.lexStream.start(token);
/* 2128 */     int errorEnd = this.lexStream.end(token);
/* 2129 */     int currentKind = this.lexStream.kind(token);
/* 2130 */     String errorTokenName = Parser.name[Parser.terminal_index[this.lexStream.kind(token)]];
/* 2131 */     char[] errorTokenSource = this.lexStream.name(token);
/* 2132 */     if (currentKind == 52) {
/* 2133 */       errorTokenSource = displayEscapeCharacters(errorTokenSource, 1, errorTokenSource.length - 1);
/*      */     }
/*      */ 
/* 2136 */     int addedToken = -1;
/* 2137 */     if ((this.recoveryScanner != null) && 
/* 2138 */       (nameIndex >= 0)) {
/* 2139 */       addedToken = Parser.reverse_index[nameIndex];
/*      */     }
/*      */ 
/* 2142 */     switch (msgCode) {
/*      */     case 2:
/* 2144 */       if (this.recoveryScanner != null) {
/* 2145 */         if (addedToken > -1) {
/* 2146 */           this.recoveryScanner.insertToken(addedToken, -1, errorStart);
/*      */         } else {
/* 2148 */           int[] template = getNTermTemplate(-addedToken);
/* 2149 */           if (template != null) {
/* 2150 */             this.recoveryScanner.insertTokens(template, -1, errorStart);
/*      */           }
/*      */         }
/*      */       }
/* 2154 */       if (!this.reportProblem) break; problemReporter().parseErrorInsertBeforeToken(
/* 2155 */         errorStart, 
/* 2156 */         errorEnd, 
/* 2157 */         currentKind, 
/* 2158 */         errorTokenSource, 
/* 2159 */         errorTokenName, 
/* 2160 */         name);
/* 2161 */       break;
/*      */     case 3:
/* 2163 */       if (this.recoveryScanner != null) {
/* 2164 */         if (addedToken > -1) {
/* 2165 */           this.recoveryScanner.insertToken(addedToken, -1, errorEnd);
/*      */         } else {
/* 2167 */           int[] template = getNTermTemplate(-addedToken);
/* 2168 */           if (template != null) {
/* 2169 */             this.recoveryScanner.insertTokens(template, -1, errorEnd);
/*      */           }
/*      */         }
/*      */       }
/* 2173 */       if (!this.reportProblem) break; problemReporter().parseErrorInsertAfterToken(
/* 2174 */         errorStart, 
/* 2175 */         errorEnd, 
/* 2176 */         currentKind, 
/* 2177 */         errorTokenSource, 
/* 2178 */         errorTokenName, 
/* 2179 */         name);
/* 2180 */       break;
/*      */     case 6:
/* 2182 */       if (this.recoveryScanner != null) {
/* 2183 */         this.recoveryScanner.removeTokens(errorStart, errorEnd);
/*      */       }
/* 2185 */       if (!this.reportProblem) break; problemReporter().parseErrorDeleteToken(
/* 2186 */         errorStart, 
/* 2187 */         errorEnd, 
/* 2188 */         currentKind, 
/* 2189 */         errorTokenSource, 
/* 2190 */         errorTokenName);
/* 2191 */       break;
/*      */     case 4:
/* 2193 */       if (name.length() == 0) {
/* 2194 */         if (this.recoveryScanner != null) {
/* 2195 */           this.recoveryScanner.removeTokens(errorStart, errorEnd);
/*      */         }
/* 2197 */         if (!this.reportProblem) break; problemReporter().parseErrorReplaceToken(
/* 2198 */           errorStart, 
/* 2199 */           errorEnd, 
/* 2200 */           currentKind, 
/* 2201 */           errorTokenSource, 
/* 2202 */           errorTokenName, 
/* 2203 */           name);
/*      */       } else {
/* 2205 */         if (this.recoveryScanner != null) {
/* 2206 */           if (addedToken > -1) {
/* 2207 */             this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
/*      */           } else {
/* 2209 */             int[] template = getNTermTemplate(-addedToken);
/* 2210 */             if (template != null) {
/* 2211 */               this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
/*      */             }
/*      */           }
/*      */         }
/* 2215 */         if (!this.reportProblem) break; problemReporter().parseErrorInvalidToken(
/* 2216 */           errorStart, 
/* 2217 */           errorEnd, 
/* 2218 */           currentKind, 
/* 2219 */           errorTokenSource, 
/* 2220 */           errorTokenName, 
/* 2221 */           name);
/*      */       }
/* 2223 */       break;
/*      */     case 5:
/* 2225 */       if (this.recoveryScanner != null) {
/* 2226 */         if (addedToken > -1) {
/* 2227 */           this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
/*      */         } else {
/* 2229 */           int[] template = getNTermTemplate(-addedToken);
/* 2230 */           if (template != null) {
/* 2231 */             this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
/*      */           }
/*      */         }
/*      */       }
/* 2235 */       if (!this.reportProblem) break; problemReporter().parseErrorReplaceToken(
/* 2236 */         errorStart, 
/* 2237 */         errorEnd, 
/* 2238 */         currentKind, 
/* 2239 */         errorTokenSource, 
/* 2240 */         errorTokenName, 
/* 2241 */         name);
/* 2242 */       break;
/*      */     case 9:
/* 2244 */       StringBuffer buf = new StringBuffer();
/*      */ 
/* 2246 */       int[] addedTokens = (int[])null;
/* 2247 */       int addedTokenCount = 0;
/* 2248 */       if (this.recoveryScanner != null) {
/* 2249 */         addedTokens = new int[Parser.scope_rhs.length - Parser.scope_suffix[(-nameIndex)]];
/*      */       }
/*      */ 
/* 2252 */       for (int i = Parser.scope_suffix[(-nameIndex)]; Parser.scope_rhs[i] != 0; i++) {
/* 2253 */         buf.append(Parser.readableName[Parser.scope_rhs[i]]);
/* 2254 */         if (Parser.scope_rhs[(i + 1)] != 0) {
/* 2255 */           buf.append(' ');
/*      */         }
/* 2257 */         if (addedTokens != null) {
/* 2258 */           int tmpAddedToken = Parser.reverse_index[Parser.scope_rhs[i]];
/* 2259 */           if (tmpAddedToken > -1) {
/* 2260 */             int length = addedTokens.length;
/* 2261 */             if (addedTokenCount == length) {
/* 2262 */               System.arraycopy(addedTokens, 0, addedTokens = new int[length * 2], 0, length);
/*      */             }
/* 2264 */             addedTokens[(addedTokenCount++)] = tmpAddedToken;
/*      */           } else {
/* 2266 */             int[] template = getNTermTemplate(-tmpAddedToken);
/* 2267 */             if (template != null) {
/* 2268 */               for (int j = 0; j < template.length; j++) {
/* 2269 */                 int length = addedTokens.length;
/* 2270 */                 if (addedTokenCount == length) {
/* 2271 */                   System.arraycopy(addedTokens, 0, addedTokens = new int[length * 2], 0, length);
/*      */                 }
/* 2273 */                 addedTokens[(addedTokenCount++)] = template[j];
/*      */               }
/*      */             } else {
/* 2276 */               addedTokenCount = 0;
/* 2277 */               addedTokens = (int[])null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2283 */       if (addedTokenCount > 0) {
/* 2284 */         System.arraycopy(addedTokens, 0, addedTokens = new int[addedTokenCount], 0, addedTokenCount);
/*      */ 
/* 2286 */         int completedToken = -1;
/* 2287 */         if (scopeNameIndex != 0) {
/* 2288 */           completedToken = -Parser.reverse_index[scopeNameIndex];
/*      */         }
/* 2290 */         this.recoveryScanner.insertTokens(addedTokens, completedToken, errorEnd);
/*      */       }
/*      */ 
/* 2293 */       if (scopeNameIndex != 0) {
/* 2294 */         if (!this.reportProblem) break; problemReporter().parseErrorInsertToComplete(
/* 2295 */           errorStart, 
/* 2296 */           errorEnd, 
/* 2297 */           buf.toString(), 
/* 2298 */           Parser.readableName[scopeNameIndex]);
/*      */       } else {
/* 2300 */         if (!this.reportProblem) break; problemReporter().parseErrorInsertToCompleteScope(
/* 2301 */           errorStart, 
/* 2302 */           errorEnd, 
/* 2303 */           buf.toString());
/*      */       }
/*      */ 
/* 2306 */       break;
/*      */     case 11:
/* 2308 */       if (!this.reportProblem) break; problemReporter().parseErrorUnexpectedEnd(
/* 2309 */         errorStart, 
/* 2310 */         errorEnd);
/* 2311 */       break;
/*      */     case 7:
/* 2313 */       if (this.recoveryScanner != null) {
/* 2314 */         if (addedToken > -1) {
/* 2315 */           this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
/*      */         } else {
/* 2317 */           int[] template = getNTermTemplate(-addedToken);
/* 2318 */           if (template != null) {
/* 2319 */             this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
/*      */           }
/*      */         }
/*      */       }
/* 2323 */       if (!this.reportProblem) break; problemReporter().parseErrorMergeTokens(
/* 2324 */         errorStart, 
/* 2325 */         errorEnd, 
/* 2326 */         name);
/* 2327 */       break;
/*      */     case 8:
/* 2329 */       if (this.recoveryScanner != null) {
/* 2330 */         this.recoveryScanner.removeTokens(errorStart, errorEnd);
/*      */       }
/* 2332 */       if (!this.reportProblem) break; problemReporter().parseErrorMisplacedConstruct(
/* 2333 */         errorStart, 
/* 2334 */         errorEnd);
/* 2335 */       break;
/*      */     case 10:
/*      */     default:
/* 2337 */       if (name.length() == 0) {
/* 2338 */         if (this.recoveryScanner != null) {
/* 2339 */           this.recoveryScanner.removeTokens(errorStart, errorEnd);
/*      */         }
/* 2341 */         if (!this.reportProblem) break; problemReporter().parseErrorNoSuggestion(
/* 2342 */           errorStart, 
/* 2343 */           errorEnd, 
/* 2344 */           currentKind, 
/* 2345 */           errorTokenSource, 
/* 2346 */           errorTokenName);
/*      */       } else {
/* 2348 */         if (this.recoveryScanner != null) {
/* 2349 */           if (addedToken > -1) {
/* 2350 */             this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
/*      */           } else {
/* 2352 */             int[] template = getNTermTemplate(-addedToken);
/* 2353 */             if (template != null) {
/* 2354 */               this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
/*      */             }
/*      */           }
/*      */         }
/* 2358 */         if (!this.reportProblem) break; problemReporter().parseErrorReplaceToken(
/* 2359 */           errorStart, 
/* 2360 */           errorEnd, 
/* 2361 */           currentKind, 
/* 2362 */           errorTokenSource, 
/* 2363 */           errorTokenName, 
/* 2364 */           name);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void reportSecondaryError(int msgCode, int nameIndex, int leftToken, int rightToken, int scopeNameIndex)
/*      */   {
/*      */     String name;
/*      */     String name;
/* 2372 */     if (nameIndex >= 0)
/* 2373 */       name = Parser.readableName[nameIndex];
/*      */     else {
/* 2375 */       name = Util.EMPTY_STRING;
/*      */     }
/*      */ 
/* 2378 */     int errorStart = -1;
/* 2379 */     if (this.lexStream.isInsideStream(leftToken)) {
/* 2380 */       if (leftToken == 0)
/* 2381 */         errorStart = this.lexStream.start(leftToken + 1);
/*      */       else
/* 2383 */         errorStart = this.lexStream.start(leftToken);
/*      */     }
/*      */     else {
/* 2386 */       if (leftToken == this.errorToken)
/* 2387 */         errorStart = this.errorTokenStart;
/*      */       else {
/* 2389 */         for (int i = 0; i <= this.stateStackTop; i++) {
/* 2390 */           if (this.locationStack[i] == leftToken) {
/* 2391 */             errorStart = this.locationStartStack[i];
/*      */           }
/*      */         }
/*      */       }
/* 2395 */       if (errorStart == -1) {
/* 2396 */         errorStart = this.lexStream.start(rightToken);
/*      */       }
/*      */     }
/* 2399 */     int errorEnd = this.lexStream.end(rightToken);
/*      */ 
/* 2401 */     int addedToken = -1;
/* 2402 */     if ((this.recoveryScanner != null) && 
/* 2403 */       (nameIndex >= 0)) {
/* 2404 */       addedToken = Parser.reverse_index[nameIndex];
/*      */     }
/*      */ 
/* 2408 */     switch (msgCode) {
/*      */     case 8:
/* 2410 */       if (this.recoveryScanner != null) {
/* 2411 */         this.recoveryScanner.removeTokens(errorStart, errorEnd);
/*      */       }
/* 2413 */       if (!this.reportProblem) break; problemReporter().parseErrorMisplacedConstruct(
/* 2414 */         errorStart, 
/* 2415 */         errorEnd);
/* 2416 */       break;
/*      */     case 9:
/* 2419 */       errorStart = this.lexStream.start(rightToken);
/*      */ 
/* 2421 */       StringBuffer buf = new StringBuffer();
/*      */ 
/* 2423 */       int[] addedTokens = (int[])null;
/* 2424 */       int addedTokenCount = 0;
/* 2425 */       if (this.recoveryScanner != null) {
/* 2426 */         addedTokens = new int[Parser.scope_rhs.length - Parser.scope_suffix[(-nameIndex)]];
/*      */       }
/*      */ 
/* 2429 */       for (int i = Parser.scope_suffix[(-nameIndex)]; Parser.scope_rhs[i] != 0; i++)
/*      */       {
/* 2431 */         buf.append(Parser.readableName[Parser.scope_rhs[i]]);
/* 2432 */         if (Parser.scope_rhs[(i + 1)] != 0) {
/* 2433 */           buf.append(' ');
/*      */         }
/* 2435 */         if (addedTokens != null) {
/* 2436 */           int tmpAddedToken = Parser.reverse_index[Parser.scope_rhs[i]];
/* 2437 */           if (tmpAddedToken > -1) {
/* 2438 */             int length = addedTokens.length;
/* 2439 */             if (addedTokenCount == length) {
/* 2440 */               System.arraycopy(addedTokens, 0, addedTokens = new int[length * 2], 0, length);
/*      */             }
/* 2442 */             addedTokens[(addedTokenCount++)] = tmpAddedToken;
/*      */           } else {
/* 2444 */             int[] template = getNTermTemplate(-tmpAddedToken);
/* 2445 */             if (template != null) {
/* 2446 */               for (int j = 0; j < template.length; j++) {
/* 2447 */                 int length = addedTokens.length;
/* 2448 */                 if (addedTokenCount == length) {
/* 2449 */                   System.arraycopy(addedTokens, 0, addedTokens = new int[length * 2], 0, length);
/*      */                 }
/* 2451 */                 addedTokens[(addedTokenCount++)] = template[j];
/*      */               }
/*      */             } else {
/* 2454 */               addedTokenCount = 0;
/* 2455 */               addedTokens = (int[])null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2460 */       if (addedTokenCount > 0) {
/* 2461 */         System.arraycopy(addedTokens, 0, addedTokens = new int[addedTokenCount], 0, addedTokenCount);
/* 2462 */         int completedToken = -1;
/* 2463 */         if (scopeNameIndex != 0) {
/* 2464 */           completedToken = -Parser.reverse_index[scopeNameIndex];
/*      */         }
/* 2466 */         this.recoveryScanner.insertTokens(addedTokens, completedToken, errorEnd);
/*      */       }
/* 2468 */       if (scopeNameIndex != 0) {
/* 2469 */         if (!this.reportProblem) break; problemReporter().parseErrorInsertToComplete(
/* 2470 */           errorStart, 
/* 2471 */           errorEnd, 
/* 2472 */           buf.toString(), 
/* 2473 */           Parser.readableName[scopeNameIndex]);
/*      */       } else {
/* 2475 */         if (!this.reportProblem) break; problemReporter().parseErrorInsertToCompletePhrase(
/* 2476 */           errorStart, 
/* 2477 */           errorEnd, 
/* 2478 */           buf.toString());
/*      */       }
/* 2480 */       break;
/*      */     case 7:
/* 2482 */       if (this.recoveryScanner != null) {
/* 2483 */         if (addedToken > -1) {
/* 2484 */           this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
/*      */         } else {
/* 2486 */           int[] template = getNTermTemplate(-addedToken);
/* 2487 */           if (template != null) {
/* 2488 */             this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
/*      */           }
/*      */         }
/*      */       }
/* 2492 */       if (!this.reportProblem) break; problemReporter().parseErrorMergeTokens(
/* 2493 */         errorStart, 
/* 2494 */         errorEnd, 
/* 2495 */         name);
/* 2496 */       break;
/*      */     case 6:
/* 2498 */       if (this.recoveryScanner != null) {
/* 2499 */         this.recoveryScanner.removeTokens(errorStart, errorEnd);
/*      */       }
/* 2501 */       if (!this.reportProblem) break; problemReporter().parseErrorDeleteTokens(
/* 2502 */         errorStart, 
/* 2503 */         errorEnd);
/* 2504 */       break;
/*      */     default:
/* 2506 */       if (name.length() == 0) {
/* 2507 */         if (this.recoveryScanner != null) {
/* 2508 */           this.recoveryScanner.removeTokens(errorStart, errorEnd);
/*      */         }
/* 2510 */         if (!this.reportProblem) break; problemReporter().parseErrorNoSuggestionForTokens(
/* 2511 */           errorStart, 
/* 2512 */           errorEnd);
/*      */       } else {
/* 2514 */         if (this.recoveryScanner != null) {
/* 2515 */           if (addedToken > -1) {
/* 2516 */             this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
/*      */           } else {
/* 2518 */             int[] template = getNTermTemplate(-addedToken);
/* 2519 */             if (template != null) {
/* 2520 */               this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
/*      */             }
/*      */           }
/*      */         }
/* 2524 */         if (!this.reportProblem) break; problemReporter().parseErrorReplaceTokens(
/* 2525 */           errorStart, 
/* 2526 */           errorEnd, 
/* 2527 */           name);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int[] getNTermTemplate(int sym)
/*      */   {
/* 2534 */     int templateIndex = Parser.recovery_templates_index[sym];
/* 2535 */     if (templateIndex > 0) {
/* 2536 */       int[] result = new int[Parser.recovery_templates.length];
/* 2537 */       int count = 0;
/* 2538 */       for (int j = templateIndex; Parser.recovery_templates[j] != 0; j++) {
/* 2539 */         result[(count++)] = Parser.recovery_templates[j];
/*      */       }
/* 2541 */       System.arraycopy(result, 0, result = new int[count], 0, count);
/* 2542 */       return result;
/*      */     }
/* 2544 */     return null;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 2549 */     StringBuffer res = new StringBuffer();
/*      */ 
/* 2551 */     res.append(this.lexStream.toString());
/*      */ 
/* 2553 */     return res.toString();
/*      */   }
/*      */ 
/*      */   private static class PrimaryRepairInfo
/*      */   {
/*      */     public int distance;
/*      */     public int misspellIndex;
/*      */     public int code;
/*      */     public int bufferPosition;
/*      */     public int symbol;
/*      */ 
/*      */     public PrimaryRepairInfo()
/*      */     {
/*  106 */       this.distance = 0;
/*  107 */       this.misspellIndex = 0;
/*  108 */       this.code = 0;
/*  109 */       this.bufferPosition = 0;
/*  110 */       this.symbol = 0;
/*      */     }
/*      */ 
/*      */     public PrimaryRepairInfo copy() {
/*  114 */       PrimaryRepairInfo c = new PrimaryRepairInfo();
/*  115 */       c.distance = this.distance;
/*  116 */       c.misspellIndex = this.misspellIndex;
/*  117 */       c.code = this.code;
/*  118 */       c.bufferPosition = this.bufferPosition;
/*  119 */       c.symbol = this.symbol;
/*  120 */       return c;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class RepairCandidate
/*      */   {
/*      */     public int symbol;
/*      */     public int location;
/*      */ 
/*      */     public RepairCandidate()
/*      */     {
/*   93 */       this.symbol = 0;
/*   94 */       this.location = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class SecondaryRepairInfo
/*      */   {
/*      */     public int code;
/*      */     public int distance;
/*      */     public int bufferPosition;
/*      */     public int stackPosition;
/*      */     public int numDeletions;
/*      */     public int symbol;
/*      */     boolean recoveryOnNextStack;
/*      */   }
/*      */ 
/*      */   private static class StateInfo
/*      */   {
/*      */     int state;
/*      */     int next;
/*      */ 
/*      */     public StateInfo(int state, int next)
/*      */     {
/*  141 */       this.state = state;
/*  142 */       this.next = next;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.diagnose.DiagnoseParser
 * JD-Core Version:    0.6.0
 */