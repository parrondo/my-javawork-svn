/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.core.compiler.InvalidInputException;
/*     */ 
/*     */ public class RecoveryScanner extends Scanner
/*     */ {
/*  18 */   public static final char[] FAKE_IDENTIFIER = "$missing$".toCharArray();
/*     */   private RecoveryScannerData data;
/*     */   private int[] pendingTokens;
/*  23 */   private int pendingTokensPtr = -1;
/*  24 */   private char[] fakeTokenSource = null;
/*  25 */   private boolean isInserted = true;
/*  26 */   private boolean precededByRemoved = false;
/*  27 */   private int skipNextInsertedTokens = -1;
/*     */ 
/*  29 */   public boolean record = true;
/*     */ 
/*     */   public RecoveryScanner(Scanner scanner, RecoveryScannerData data)
/*     */   {
/*  39 */     super(false, 
/*  33 */       scanner.tokenizeWhiteSpace, 
/*  34 */       scanner.checkNonExternalizedStringLiterals, 
/*  35 */       scanner.sourceLevel, 
/*  36 */       scanner.complianceLevel, 
/*  37 */       scanner.taskTags, 
/*  38 */       scanner.taskPriorities, 
/*  39 */       scanner.isTaskCaseSensitive);
/*  40 */     setData(data);
/*     */   }
/*     */ 
/*     */   public RecoveryScanner(boolean tokenizeWhiteSpace, boolean checkNonExternalizedStringLiterals, long sourceLevel, long complianceLevel, char[][] taskTags, char[][] taskPriorities, boolean isTaskCaseSensitive, RecoveryScannerData data)
/*     */   {
/*  59 */     super(false, 
/*  53 */       tokenizeWhiteSpace, 
/*  54 */       checkNonExternalizedStringLiterals, 
/*  55 */       sourceLevel, 
/*  56 */       complianceLevel, 
/*  57 */       taskTags, 
/*  58 */       taskPriorities, 
/*  59 */       isTaskCaseSensitive);
/*  60 */     setData(data);
/*     */   }
/*     */ 
/*     */   public void insertToken(int token, int completedToken, int position) {
/*  64 */     insertTokens(new int[] { token }, completedToken, position);
/*     */   }
/*     */ 
/*     */   private int[] reverse(int[] tokens) {
/*  68 */     int length = tokens.length;
/*  69 */     int i = 0; for (int max = length / 2; i < max; i++) {
/*  70 */       int tmp = tokens[i];
/*  71 */       tokens[i] = tokens[(length - i - 1)];
/*  72 */       tokens[(length - i - 1)] = tmp;
/*     */     }
/*  74 */     return tokens;
/*     */   }
/*     */   public void insertTokens(int[] tokens, int completedToken, int position) {
/*  77 */     if (!this.record) return;
/*     */ 
/*  79 */     if ((completedToken > -1) && (Parser.statements_recovery_filter[completedToken] != 0)) return;
/*     */ 
/*  81 */     this.data.insertedTokensPtr += 1;
/*  82 */     if (this.data.insertedTokens == null) {
/*  83 */       this.data.insertedTokens = new int[10][];
/*  84 */       this.data.insertedTokensPosition = new int[10];
/*  85 */       this.data.insertedTokenUsed = new boolean[10];
/*  86 */     } else if (this.data.insertedTokens.length == this.data.insertedTokensPtr) {
/*  87 */       int length = this.data.insertedTokens.length;
/*  88 */       System.arraycopy(this.data.insertedTokens, 0, this.data.insertedTokens = new int[length * 2][], 0, length);
/*  89 */       System.arraycopy(this.data.insertedTokensPosition, 0, this.data.insertedTokensPosition = new int[length * 2], 0, length);
/*  90 */       System.arraycopy(this.data.insertedTokenUsed, 0, this.data.insertedTokenUsed = new boolean[length * 2], 0, length);
/*     */     }
/*  92 */     this.data.insertedTokens[this.data.insertedTokensPtr] = reverse(tokens);
/*  93 */     this.data.insertedTokensPosition[this.data.insertedTokensPtr] = position;
/*  94 */     this.data.insertedTokenUsed[this.data.insertedTokensPtr] = false;
/*     */   }
/*     */ 
/*     */   public void replaceTokens(int token, int start, int end) {
/*  98 */     replaceTokens(new int[] { token }, start, end);
/*     */   }
/*     */ 
/*     */   public void replaceTokens(int[] tokens, int start, int end) {
/* 102 */     if (!this.record) return;
/* 103 */     this.data.replacedTokensPtr += 1;
/* 104 */     if (this.data.replacedTokensStart == null) {
/* 105 */       this.data.replacedTokens = new int[10][];
/* 106 */       this.data.replacedTokensStart = new int[10];
/* 107 */       this.data.replacedTokensEnd = new int[10];
/* 108 */       this.data.replacedTokenUsed = new boolean[10];
/* 109 */     } else if (this.data.replacedTokensStart.length == this.data.replacedTokensPtr) {
/* 110 */       int length = this.data.replacedTokensStart.length;
/* 111 */       System.arraycopy(this.data.replacedTokens, 0, this.data.replacedTokens = new int[length * 2][], 0, length);
/* 112 */       System.arraycopy(this.data.replacedTokensStart, 0, this.data.replacedTokensStart = new int[length * 2], 0, length);
/* 113 */       System.arraycopy(this.data.replacedTokensEnd, 0, this.data.replacedTokensEnd = new int[length * 2], 0, length);
/* 114 */       System.arraycopy(this.data.replacedTokenUsed, 0, this.data.replacedTokenUsed = new boolean[length * 2], 0, length);
/*     */     }
/* 116 */     this.data.replacedTokens[this.data.replacedTokensPtr] = reverse(tokens);
/* 117 */     this.data.replacedTokensStart[this.data.replacedTokensPtr] = start;
/* 118 */     this.data.replacedTokensEnd[this.data.replacedTokensPtr] = end;
/* 119 */     this.data.replacedTokenUsed[this.data.replacedTokensPtr] = false;
/*     */   }
/*     */ 
/*     */   public void removeTokens(int start, int end) {
/* 123 */     if (!this.record) return;
/* 124 */     this.data.removedTokensPtr += 1;
/* 125 */     if (this.data.removedTokensStart == null) {
/* 126 */       this.data.removedTokensStart = new int[10];
/* 127 */       this.data.removedTokensEnd = new int[10];
/* 128 */       this.data.removedTokenUsed = new boolean[10];
/* 129 */     } else if (this.data.removedTokensStart.length == this.data.removedTokensPtr) {
/* 130 */       int length = this.data.removedTokensStart.length;
/* 131 */       System.arraycopy(this.data.removedTokensStart, 0, this.data.removedTokensStart = new int[length * 2], 0, length);
/* 132 */       System.arraycopy(this.data.removedTokensEnd, 0, this.data.removedTokensEnd = new int[length * 2], 0, length);
/* 133 */       System.arraycopy(this.data.removedTokenUsed, 0, this.data.removedTokenUsed = new boolean[length * 2], 0, length);
/*     */     }
/* 135 */     this.data.removedTokensStart[this.data.removedTokensPtr] = start;
/* 136 */     this.data.removedTokensEnd[this.data.removedTokensPtr] = end;
/* 137 */     this.data.removedTokenUsed[this.data.removedTokensPtr] = false;
/*     */   }
/*     */ 
/*     */   public int getNextToken() throws InvalidInputException {
/* 141 */     if (this.pendingTokensPtr > -1) {
/* 142 */       int nextToken = this.pendingTokens[(this.pendingTokensPtr--)];
/* 143 */       if (nextToken == 26)
/* 144 */         this.fakeTokenSource = FAKE_IDENTIFIER;
/*     */       else {
/* 146 */         this.fakeTokenSource = CharOperation.NO_CHAR;
/*     */       }
/* 148 */       return nextToken;
/*     */     }
/*     */ 
/* 151 */     this.fakeTokenSource = null;
/* 152 */     this.precededByRemoved = false;
/*     */ 
/* 154 */     if (this.data.insertedTokens != null) {
/* 155 */       for (int i = 0; i <= this.data.insertedTokensPtr; i++) {
/* 156 */         if ((this.data.insertedTokensPosition[i] == this.currentPosition - 1) && (i > this.skipNextInsertedTokens)) {
/* 157 */           this.data.insertedTokenUsed[i] = true;
/* 158 */           this.pendingTokens = this.data.insertedTokens[i];
/* 159 */           this.pendingTokensPtr = (this.data.insertedTokens[i].length - 1);
/* 160 */           this.isInserted = true;
/* 161 */           this.startPosition = this.currentPosition;
/* 162 */           this.skipNextInsertedTokens = i;
/* 163 */           int nextToken = this.pendingTokens[(this.pendingTokensPtr--)];
/* 164 */           if (nextToken == 26)
/* 165 */             this.fakeTokenSource = FAKE_IDENTIFIER;
/*     */           else {
/* 167 */             this.fakeTokenSource = CharOperation.NO_CHAR;
/*     */           }
/* 169 */           return nextToken;
/*     */         }
/*     */       }
/* 172 */       this.skipNextInsertedTokens = -1;
/*     */     }
/*     */ 
/* 175 */     int previousLocation = this.currentPosition;
/* 176 */     int currentToken = super.getNextToken();
/*     */ 
/* 178 */     if (this.data.replacedTokens != null) {
/* 179 */       for (int i = 0; i <= this.data.replacedTokensPtr; i++) {
/* 180 */         if ((this.data.replacedTokensStart[i] < previousLocation) || 
/* 181 */           (this.data.replacedTokensStart[i] > this.startPosition) || 
/* 182 */           (this.data.replacedTokensEnd[i] < this.currentPosition - 1)) continue;
/* 183 */         this.data.replacedTokenUsed[i] = true;
/* 184 */         this.pendingTokens = this.data.replacedTokens[i];
/* 185 */         this.pendingTokensPtr = (this.data.replacedTokens[i].length - 1);
/* 186 */         this.fakeTokenSource = FAKE_IDENTIFIER;
/* 187 */         this.isInserted = false;
/* 188 */         this.currentPosition = (this.data.replacedTokensEnd[i] + 1);
/* 189 */         int nextToken = this.pendingTokens[(this.pendingTokensPtr--)];
/* 190 */         if (nextToken == 26)
/* 191 */           this.fakeTokenSource = FAKE_IDENTIFIER;
/*     */         else {
/* 193 */           this.fakeTokenSource = CharOperation.NO_CHAR;
/*     */         }
/* 195 */         return nextToken;
/*     */       }
/*     */     }
/*     */ 
/* 199 */     if (this.data.removedTokensStart != null) {
/* 200 */       for (int i = 0; i <= this.data.removedTokensPtr; i++) {
/* 201 */         if ((this.data.removedTokensStart[i] < previousLocation) || 
/* 202 */           (this.data.removedTokensStart[i] > this.startPosition) || 
/* 203 */           (this.data.removedTokensEnd[i] < this.currentPosition - 1)) continue;
/* 204 */         this.data.removedTokenUsed[i] = true;
/* 205 */         this.currentPosition = (this.data.removedTokensEnd[i] + 1);
/* 206 */         this.precededByRemoved = false;
/* 207 */         return getNextToken();
/*     */       }
/*     */     }
/*     */ 
/* 211 */     return currentToken;
/*     */   }
/*     */ 
/*     */   public char[] getCurrentIdentifierSource() {
/* 215 */     if (this.fakeTokenSource != null) return this.fakeTokenSource;
/* 216 */     return super.getCurrentIdentifierSource();
/*     */   }
/*     */ 
/*     */   public char[] getCurrentTokenSourceString() {
/* 220 */     if (this.fakeTokenSource != null) return this.fakeTokenSource;
/* 221 */     return super.getCurrentTokenSourceString();
/*     */   }
/*     */ 
/*     */   public char[] getCurrentTokenSource() {
/* 225 */     if (this.fakeTokenSource != null) return this.fakeTokenSource;
/* 226 */     return super.getCurrentTokenSource();
/*     */   }
/*     */ 
/*     */   public RecoveryScannerData getData() {
/* 230 */     return this.data;
/*     */   }
/*     */ 
/*     */   public boolean isFakeToken() {
/* 234 */     return this.fakeTokenSource != null;
/*     */   }
/*     */ 
/*     */   public boolean isInsertedToken() {
/* 238 */     return (this.fakeTokenSource != null) && (this.isInserted);
/*     */   }
/*     */ 
/*     */   public boolean isReplacedToken() {
/* 242 */     return (this.fakeTokenSource != null) && (!this.isInserted);
/*     */   }
/*     */ 
/*     */   public boolean isPrecededByRemovedToken() {
/* 246 */     return this.precededByRemoved;
/*     */   }
/*     */ 
/*     */   public void setData(RecoveryScannerData data) {
/* 250 */     if (data == null)
/* 251 */       this.data = new RecoveryScannerData();
/*     */     else
/* 253 */       this.data = data;
/*     */   }
/*     */ 
/*     */   public void setPendingTokens(int[] pendingTokens)
/*     */   {
/* 258 */     this.pendingTokens = pendingTokens;
/* 259 */     this.pendingTokensPtr = (pendingTokens.length - 1);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveryScanner
 * JD-Core Version:    0.6.0
 */