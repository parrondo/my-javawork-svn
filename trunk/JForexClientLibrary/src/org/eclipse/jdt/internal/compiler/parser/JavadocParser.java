/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.core.compiler.InvalidInputException;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Javadoc;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocArgumentExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocArrayQualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocArraySingleTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocImplicitTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class JavadocParser extends AbstractCommentParser
/*     */ {
/*     */   public Javadoc docComment;
/*  31 */   private int invalidParamReferencesPtr = -1;
/*     */   private ASTNode[] invalidParamReferencesStack;
/*     */   private long validValuePositions;
/*     */   private long invalidValuePositions;
/*  40 */   public boolean shouldReportProblems = true;
/*     */   private int tagWaitingForDescription;
/*     */ 
/*     */   public JavadocParser(Parser sourceParser)
/*     */   {
/*  47 */     super(sourceParser);
/*  48 */     this.kind = 513;
/*     */   }
/*     */ 
/*     */   public boolean checkDeprecation(int commentPtr)
/*     */   {
/*  60 */     this.javadocStart = this.sourceParser.scanner.commentStarts[commentPtr];
/*  61 */     this.javadocEnd = (this.sourceParser.scanner.commentStops[commentPtr] - 1);
/*  62 */     this.firstTagPosition = this.sourceParser.scanner.commentTagStarts[commentPtr];
/*  63 */     this.validValuePositions = -1L;
/*  64 */     this.invalidValuePositions = -1L;
/*  65 */     this.tagWaitingForDescription = 0;
/*     */ 
/*  68 */     if (this.checkDocComment)
/*  69 */       this.docComment = new Javadoc(this.javadocStart, this.javadocEnd);
/*     */     else {
/*  71 */       this.docComment = null;
/*     */     }
/*     */ 
/*  75 */     if (this.firstTagPosition == 0) {
/*  76 */       switch (this.kind & 0xFF) {
/*     */       case 1:
/*     */       case 16:
/*  79 */         return false;
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  85 */       this.source = this.sourceParser.scanner.source;
/*  86 */       if (this.checkDocComment)
/*     */       {
/*  88 */         this.scanner.lineEnds = this.sourceParser.scanner.lineEnds;
/*  89 */         this.scanner.linePtr = this.sourceParser.scanner.linePtr;
/*  90 */         this.lineEnds = this.scanner.lineEnds;
/*  91 */         commentParse();
/*     */       }
/*     */       else
/*     */       {
/*  95 */         Scanner sourceScanner = this.sourceParser.scanner;
/*  96 */         int firstLineNumber = Util.getLineNumber(this.javadocStart, sourceScanner.lineEnds, 0, sourceScanner.linePtr);
/*  97 */         int lastLineNumber = Util.getLineNumber(this.javadocEnd, sourceScanner.lineEnds, 0, sourceScanner.linePtr);
/*  98 */         this.index = (this.javadocStart + 3);
/*     */ 
/* 101 */         this.deprecated = false;
/* 102 */         label467: for (int line = firstLineNumber; line <= lastLineNumber; line++) {
/* 103 */           int lineStart = line == firstLineNumber ? 
/* 104 */             this.javadocStart + 3 : 
/* 105 */             this.sourceParser.scanner.getLineStart(line);
/* 106 */           this.index = lineStart;
/* 107 */           this.lineEnd = (line == lastLineNumber ? 
/* 108 */             this.javadocEnd - 2 : 
/* 109 */             this.sourceParser.scanner.getLineEnd(line));
/* 110 */           while (this.index < this.lineEnd) {
/* 111 */             char c = readChar();
/* 112 */             switch (c)
/*     */             {
/*     */             case '\t':
/*     */             case '\n':
/*     */             case '\f':
/*     */             case '\r':
/*     */             case ' ':
/*     */             case '*':
/* 120 */               break;
/*     */             case '@':
/* 122 */               parseSimpleTag();
/* 123 */               if ((this.tagValue != 1) || 
/* 124 */                 (!this.abort)) break label467; break;
/*     */             default:
/* 127 */               break label467;
/*     */             }
/*     */           }
/*     */         }
/* 130 */         return this.deprecated;
/*     */       }
/*     */     } finally {
/* 133 */       this.source = null;
/*     */     }
/* 135 */     return this.deprecated;
/*     */   }
/*     */ 
/*     */   protected Object createArgumentReference(char[] name, int dim, boolean isVarargs, Object typeRef, long[] dimPositions, long argNamePos)
/*     */     throws InvalidInputException
/*     */   {
/*     */     try
/*     */     {
/* 143 */       TypeReference argTypeRef = (TypeReference)typeRef;
/* 144 */       if (dim > 0) {
/* 145 */         long pos = (argTypeRef.sourceStart << 32) + argTypeRef.sourceEnd;
/* 146 */         if ((typeRef instanceof JavadocSingleTypeReference)) {
/* 147 */           JavadocSingleTypeReference singleRef = (JavadocSingleTypeReference)typeRef;
/* 148 */           argTypeRef = new JavadocArraySingleTypeReference(singleRef.token, dim, pos);
/*     */         } else {
/* 150 */           JavadocQualifiedTypeReference qualifRef = (JavadocQualifiedTypeReference)typeRef;
/* 151 */           argTypeRef = new JavadocArrayQualifiedTypeReference(qualifRef, dim);
/*     */         }
/*     */       }
/* 154 */       int argEnd = argTypeRef.sourceEnd;
/* 155 */       if (dim > 0) {
/* 156 */         argEnd = (int)dimPositions[(dim - 1)];
/* 157 */         if (isVarargs) {
/* 158 */           argTypeRef.bits |= 16384;
/*     */         }
/*     */       }
/* 161 */       if (argNamePos >= 0L) argEnd = (int)argNamePos;
/* 162 */       return new JavadocArgumentExpression(name, argTypeRef.sourceStart, argEnd, argTypeRef);
/*     */     } catch (ClassCastException localClassCastException) {
/*     */     }
/* 165 */     throw new InvalidInputException();
/*     */   }
/*     */ 
/*     */   protected Object createFieldReference(Object receiver)
/*     */     throws InvalidInputException
/*     */   {
/*     */     try
/*     */     {
/* 174 */       TypeReference typeRef = (TypeReference)receiver;
/* 175 */       if (typeRef == null) {
/* 176 */         char[] name = this.sourceParser.compilationUnit.getMainTypeName();
/* 177 */         typeRef = new JavadocImplicitTypeReference(name, this.memberStart);
/*     */       }
/*     */ 
/* 180 */       JavadocFieldReference field = new JavadocFieldReference(this.identifierStack[0], this.identifierPositionStack[0]);
/* 181 */       field.receiver = typeRef;
/* 182 */       field.tagSourceStart = this.tagSourceStart;
/* 183 */       field.tagSourceEnd = this.tagSourceEnd;
/* 184 */       field.tagValue = this.tagValue;
/* 185 */       return field;
/*     */     } catch (ClassCastException localClassCastException) {
/*     */     }
/* 188 */     throw new InvalidInputException();
/*     */   }
/*     */ 
/*     */   protected Object createMethodReference(Object receiver, List arguments)
/*     */     throws InvalidInputException
/*     */   {
/*     */     try
/*     */     {
/* 197 */       TypeReference typeRef = (TypeReference)receiver;
/*     */ 
/* 199 */       boolean isConstructor = false;
/* 200 */       int length = this.identifierLengthStack[0];
/* 201 */       if (typeRef == null) {
/* 202 */         char[] name = this.sourceParser.compilationUnit.getMainTypeName();
/* 203 */         TypeDeclaration typeDecl = getParsedTypeDeclaration();
/* 204 */         if (typeDecl != null) {
/* 205 */           name = typeDecl.name;
/*     */         }
/* 207 */         isConstructor = CharOperation.equals(this.identifierStack[(length - 1)], name);
/* 208 */         typeRef = new JavadocImplicitTypeReference(name, this.memberStart);
/*     */       }
/* 210 */       else if ((typeRef instanceof JavadocSingleTypeReference)) {
/* 211 */         char[] name = ((JavadocSingleTypeReference)typeRef).token;
/* 212 */         isConstructor = CharOperation.equals(this.identifierStack[(length - 1)], name);
/* 213 */       } else if ((typeRef instanceof JavadocQualifiedTypeReference)) {
/* 214 */         char[][] tokens = ((JavadocQualifiedTypeReference)typeRef).tokens;
/* 215 */         int last = tokens.length - 1;
/* 216 */         isConstructor = CharOperation.equals(this.identifierStack[(length - 1)], tokens[last]);
/* 217 */         if (isConstructor) {
/* 218 */           boolean valid = true;
/* 219 */           if (valid) {
/* 220 */             for (int i = 0; (i < length - 1) && (valid); i++) {
/* 221 */               valid = CharOperation.equals(this.identifierStack[i], tokens[i]);
/*     */             }
/*     */           }
/* 224 */           if (!valid) {
/* 225 */             if (this.reportProblems) {
/* 226 */               this.sourceParser.problemReporter().javadocInvalidMemberTypeQualification((int)(this.identifierPositionStack[0] >>> 32), (int)this.identifierPositionStack[(length - 1)], -1);
/*     */             }
/* 228 */             return null;
/*     */           }
/*     */         }
/*     */       } else {
/* 232 */         throw new InvalidInputException();
/*     */       }
/*     */ 
/* 236 */       if (arguments == null) {
/* 237 */         if (isConstructor) {
/* 238 */           JavadocAllocationExpression allocation = new JavadocAllocationExpression(this.identifierPositionStack[(length - 1)]);
/* 239 */           allocation.type = typeRef;
/* 240 */           allocation.tagValue = this.tagValue;
/* 241 */           allocation.sourceEnd = this.scanner.getCurrentTokenEndPosition();
/* 242 */           if (length == 1) {
/* 243 */             allocation.qualification = new char[][] { this.identifierStack[0] };
/*     */           } else {
/* 245 */             System.arraycopy(this.identifierStack, 0, allocation.qualification = new char[length][], 0, length);
/* 246 */             allocation.sourceStart = (int)(this.identifierPositionStack[0] >>> 32);
/*     */           }
/* 248 */           allocation.memberStart = this.memberStart;
/* 249 */           return allocation;
/*     */         }
/* 251 */         JavadocMessageSend msg = new JavadocMessageSend(this.identifierStack[(length - 1)], this.identifierPositionStack[(length - 1)]);
/* 252 */         msg.receiver = typeRef;
/* 253 */         msg.tagValue = this.tagValue;
/* 254 */         msg.sourceEnd = this.scanner.getCurrentTokenEndPosition();
/* 255 */         return msg;
/*     */       }
/*     */ 
/* 258 */       JavadocArgumentExpression[] expressions = new JavadocArgumentExpression[arguments.size()];
/* 259 */       arguments.toArray(expressions);
/* 260 */       if (isConstructor) {
/* 261 */         JavadocAllocationExpression allocation = new JavadocAllocationExpression(this.identifierPositionStack[(length - 1)]);
/* 262 */         allocation.arguments = expressions;
/* 263 */         allocation.type = typeRef;
/* 264 */         allocation.tagValue = this.tagValue;
/* 265 */         allocation.sourceEnd = this.scanner.getCurrentTokenEndPosition();
/* 266 */         if (length == 1) {
/* 267 */           allocation.qualification = new char[][] { this.identifierStack[0] };
/*     */         } else {
/* 269 */           System.arraycopy(this.identifierStack, 0, allocation.qualification = new char[length][], 0, length);
/* 270 */           allocation.sourceStart = (int)(this.identifierPositionStack[0] >>> 32);
/*     */         }
/* 272 */         allocation.memberStart = this.memberStart;
/* 273 */         return allocation;
/*     */       }
/* 275 */       JavadocMessageSend msg = new JavadocMessageSend(this.identifierStack[(length - 1)], this.identifierPositionStack[(length - 1)], expressions);
/* 276 */       msg.receiver = typeRef;
/* 277 */       msg.tagValue = this.tagValue;
/* 278 */       msg.sourceEnd = this.scanner.getCurrentTokenEndPosition();
/* 279 */       return msg;
/*     */     }
/*     */     catch (ClassCastException localClassCastException)
/*     */     {
/*     */     }
/* 284 */     throw new InvalidInputException();
/*     */   }
/*     */ 
/*     */   protected Object createReturnStatement()
/*     */   {
/* 291 */     return new JavadocReturnStatement(this.scanner.getCurrentTokenStartPosition(), 
/* 292 */       this.scanner.getCurrentTokenEndPosition());
/*     */   }
/*     */ 
/*     */   protected void createTag()
/*     */   {
/* 299 */     this.tagValue = 100;
/*     */   }
/*     */ 
/*     */   protected Object createTypeReference(int primitiveToken)
/*     */   {
/* 306 */     TypeReference typeRef = null;
/* 307 */     int size = this.identifierLengthStack[this.identifierLengthPtr];
/* 308 */     if (size == 1) {
/* 309 */       typeRef = new JavadocSingleTypeReference(
/* 310 */         this.identifierStack[this.identifierPtr], 
/* 311 */         this.identifierPositionStack[this.identifierPtr], 
/* 312 */         this.tagSourceStart, 
/* 313 */         this.tagSourceEnd);
/* 314 */     } else if (size > 1) {
/* 315 */       char[][] tokens = new char[size][];
/* 316 */       System.arraycopy(this.identifierStack, this.identifierPtr - size + 1, tokens, 0, size);
/* 317 */       long[] positions = new long[size];
/* 318 */       System.arraycopy(this.identifierPositionStack, this.identifierPtr - size + 1, positions, 0, size);
/* 319 */       typeRef = new JavadocQualifiedTypeReference(tokens, positions, this.tagSourceStart, this.tagSourceEnd);
/*     */     }
/* 321 */     return typeRef;
/*     */   }
/*     */ 
/*     */   protected TypeDeclaration getParsedTypeDeclaration()
/*     */   {
/* 328 */     int ptr = this.sourceParser.astPtr;
/* 329 */     while (ptr >= 0) {
/* 330 */       Object node = this.sourceParser.astStack[ptr];
/* 331 */       if ((node instanceof TypeDeclaration)) {
/* 332 */         TypeDeclaration typeDecl = (TypeDeclaration)node;
/* 333 */         if (typeDecl.bodyEnd == 0) {
/* 334 */           return typeDecl;
/*     */         }
/*     */       }
/* 337 */       ptr--;
/*     */     }
/* 339 */     return null;
/*     */   }
/*     */ 
/*     */   protected boolean parseThrows()
/*     */   {
/* 346 */     boolean valid = super.parseThrows();
/* 347 */     this.tagWaitingForDescription = ((valid) && (this.reportProblems) ? 4 : 0);
/* 348 */     return valid;
/*     */   }
/*     */ 
/*     */   protected boolean parseReturn()
/*     */   {
/* 355 */     if (this.returnStatement == null) {
/* 356 */       this.returnStatement = createReturnStatement();
/* 357 */       return true;
/*     */     }
/* 359 */     if (this.reportProblems) {
/* 360 */       this.sourceParser.problemReporter().javadocDuplicatedReturnTag(
/* 361 */         this.scanner.getCurrentTokenStartPosition(), 
/* 362 */         this.scanner.getCurrentTokenEndPosition());
/*     */     }
/* 364 */     return false;
/*     */   }
/*     */ 
/*     */   protected void parseSimpleTag()
/*     */   {
/* 372 */     char first = this.source[(this.index++)];
/* 373 */     if ((first == '\\') && (this.source[this.index] == 'u'))
/*     */     {
/* 375 */       int pos = this.index;
/* 376 */       this.index += 1;
/* 377 */       while (this.source[this.index] == 'u')
/* 378 */         this.index += 1;
/*     */       int c1;
/*     */       int c2;
/*     */       int c3;
/*     */       int c4;
/* 379 */       if (((c1 = ScannerHelper.getNumericValue(this.source[(this.index++)])) <= 15) && (c1 >= 0) && 
/* 380 */         ((c2 = ScannerHelper.getNumericValue(this.source[(this.index++)])) <= 15) && (c2 >= 0) && 
/* 381 */         ((c3 = ScannerHelper.getNumericValue(this.source[(this.index++)])) <= 15) && (c3 >= 0) && ((c4 = ScannerHelper.getNumericValue(this.source[(this.index++)])) <= 15) && (c4 >= 0))
/* 382 */         first = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
/*     */       else {
/* 384 */         this.index = pos;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 389 */     switch (first) {
/*     */     case 'd':
/* 391 */       if ((readChar() != 'e') || 
/* 392 */         (readChar() != 'p') || (readChar() != 'r') || 
/* 393 */         (readChar() != 'e') || (readChar() != 'c') || 
/* 394 */         (readChar() != 'a') || (readChar() != 't') || 
/* 395 */         (readChar() != 'e') || (readChar() != 'd'))
/*     */         break;
/* 397 */       char c = readChar();
/* 398 */       if ((!ScannerHelper.isWhitespace(c)) && (c != '*')) break;
/* 399 */       this.abort = true;
/* 400 */       this.deprecated = true;
/* 401 */       this.tagValue = 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean parseTag(int previousPosition)
/*     */     throws InvalidInputException
/*     */   {
/* 413 */     switch (this.tagWaitingForDescription) {
/*     */     case 2:
/*     */     case 4:
/* 416 */       if (this.inlineTagStarted) break;
/* 417 */       int start = (int)(this.identifierPositionStack[0] >>> 32);
/* 418 */       int end = (int)this.identifierPositionStack[this.identifierPtr];
/* 419 */       this.sourceParser.problemReporter().javadocMissingTagDescriptionAfterReference(start, end, this.sourceParser.modifiers);
/*     */ 
/* 421 */       break;
/*     */     case 0:
/* 423 */       break;
/*     */     case 1:
/*     */     case 3:
/*     */     default:
/* 425 */       if (this.inlineTagStarted) break;
/* 426 */       this.sourceParser.problemReporter().javadocMissingTagDescription(TAG_NAMES[this.tagWaitingForDescription], this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
/*     */     }
/*     */ 
/* 430 */     this.tagWaitingForDescription = 0;
/*     */ 
/* 433 */     this.tagSourceStart = this.index;
/* 434 */     this.tagSourceEnd = previousPosition;
/* 435 */     this.scanner.startPosition = this.index;
/* 436 */     int currentPosition = this.index;
/* 437 */     char firstChar = readChar();
/* 438 */     switch (firstChar)
/*     */     {
/*     */     case ' ':
/*     */     case '#':
/*     */     case '*':
/*     */     case '}':
/* 444 */       if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidTag(previousPosition, currentPosition);
/* 445 */       if (this.textStart == -1) this.textStart = currentPosition;
/* 446 */       this.scanner.currentCharacter = firstChar;
/* 447 */       return false;
/*     */     }
/* 449 */     if (ScannerHelper.isWhitespace(firstChar))
/*     */     {
/* 451 */       if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidTag(previousPosition, currentPosition);
/* 452 */       if (this.textStart == -1) this.textStart = currentPosition;
/* 453 */       this.scanner.currentCharacter = firstChar;
/* 454 */       return false;
/*     */     }
/*     */ 
/* 460 */     char[] tagName = new char[32];
/* 461 */     int length = 0;
/* 462 */     char currentChar = firstChar;
/* 463 */     int tagNameLength = tagName.length;
/* 464 */     boolean validTag = true;
/*     */     do while (true) {
/* 466 */         if (length == tagNameLength) {
/* 467 */           System.arraycopy(tagName, 0, tagName = new char[tagNameLength + 32], 0, tagNameLength);
/* 468 */           tagNameLength = tagName.length;
/*     */         }
/* 470 */         tagName[(length++)] = currentChar;
/* 471 */         currentPosition = this.index;
/* 472 */         currentChar = readChar();
/* 473 */         switch (currentChar)
/*     */         {
/*     */         case ' ':
/*     */         case '*':
/*     */         case '}':
/* 478 */           break;
/*     */         case '#':
/* 481 */           validTag = false;
/*     */         }
/*     */       }
/* 484 */     while (!ScannerHelper.isWhitespace(currentChar));
/*     */ 
/* 493 */     this.tagSourceEnd = (currentPosition - 1);
/* 494 */     this.scanner.currentCharacter = currentChar;
/* 495 */     this.scanner.currentPosition = currentPosition;
/* 496 */     this.index = (this.tagSourceEnd + 1);
/*     */ 
/* 499 */     if (!validTag) {
/* 500 */       if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidTag(this.tagSourceStart, this.tagSourceEnd);
/* 501 */       if (this.textStart == -1) this.textStart = this.index;
/* 502 */       this.scanner.currentCharacter = currentChar;
/* 503 */       return false;
/*     */     }
/*     */ 
/* 507 */     this.tagValue = 100;
/* 508 */     boolean valid = false;
/* 509 */     switch (firstChar) {
/*     */     case 'a':
/* 511 */       if ((length != TAG_AUTHOR_LENGTH) || (!CharOperation.equals(TAG_AUTHOR, tagName, 0, length))) break;
/* 512 */       this.tagValue = 12;
/* 513 */       this.tagWaitingForDescription = this.tagValue;
/*     */ 
/* 515 */       break;
/*     */     case 'c':
/* 517 */       if ((length == TAG_CATEGORY_LENGTH) && (CharOperation.equals(TAG_CATEGORY, tagName, 0, length))) {
/* 518 */         this.tagValue = 11;
/* 519 */         if (this.inlineTagStarted) break;
/* 520 */         valid = parseIdentifierTag(false);
/*     */       } else {
/* 522 */         if ((length != TAG_CODE_LENGTH) || (!this.inlineTagStarted) || (!CharOperation.equals(TAG_CODE, tagName, 0, length))) break;
/* 523 */         this.tagValue = 18;
/* 524 */         this.tagWaitingForDescription = this.tagValue;
/*     */       }
/* 526 */       break;
/*     */     case 'd':
/* 528 */       if ((length == TAG_DEPRECATED_LENGTH) && (CharOperation.equals(TAG_DEPRECATED, tagName, 0, length))) {
/* 529 */         this.deprecated = true;
/* 530 */         valid = true;
/* 531 */         this.tagValue = 1;
/* 532 */         this.tagWaitingForDescription = this.tagValue; } else {
/* 533 */         if ((length != TAG_DOC_ROOT_LENGTH) || (!CharOperation.equals(TAG_DOC_ROOT, tagName, 0, length))) {
/*     */           break;
/*     */         }
/* 536 */         valid = true;
/* 537 */         this.tagValue = 20;
/*     */       }
/* 539 */       break;
/*     */     case 'e':
/* 541 */       if ((length != TAG_EXCEPTION_LENGTH) || (!CharOperation.equals(TAG_EXCEPTION, tagName, 0, length))) break;
/* 542 */       this.tagValue = 5;
/* 543 */       if (this.inlineTagStarted) break;
/* 544 */       valid = parseThrows();
/*     */ 
/* 547 */       break;
/*     */     case 'i':
/* 549 */       if ((length != TAG_INHERITDOC_LENGTH) || (!CharOperation.equals(TAG_INHERITDOC, tagName, 0, length))) {
/*     */         break;
/*     */       }
/* 552 */       switch (this.lastBlockTagValue) {
/*     */       case 0:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/* 558 */         valid = true;
/* 559 */         if (!this.reportProblems) break;
/* 560 */         recordInheritedPosition((this.tagSourceStart << 32) + this.tagSourceEnd);
/*     */ 
/* 562 */         break;
/*     */       case 1:
/*     */       default:
/* 564 */         valid = false;
/* 565 */         if (!this.reportProblems) break;
/* 566 */         this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, 
/* 567 */           this.tagSourceEnd);
/*     */       }
/*     */ 
/* 570 */       this.tagValue = 9;
/*     */ 
/* 572 */       break;
/*     */     case 'l':
/* 574 */       if ((length == TAG_LINK_LENGTH) && (CharOperation.equals(TAG_LINK, tagName, 0, length))) {
/* 575 */         this.tagValue = 7;
/* 576 */         if ((!this.inlineTagStarted) && ((this.kind & 0x8) == 0)) break;
/* 577 */         valid = parseReference();
/*     */       }
/* 579 */       else if ((length == TAG_LINKPLAIN_LENGTH) && (CharOperation.equals(TAG_LINKPLAIN, tagName, 0, length))) {
/* 580 */         this.tagValue = 8;
/* 581 */         if (!this.inlineTagStarted) break;
/* 582 */         valid = parseReference();
/*     */       } else {
/* 584 */         if ((length != TAG_LITERAL_LENGTH) || (!this.inlineTagStarted) || (!CharOperation.equals(TAG_LITERAL, tagName, 0, length))) break;
/* 585 */         this.tagValue = 19;
/* 586 */         this.tagWaitingForDescription = this.tagValue;
/*     */       }
/* 588 */       break;
/*     */     case 'p':
/* 590 */       if ((length != TAG_PARAM_LENGTH) || (!CharOperation.equals(TAG_PARAM, tagName, 0, length))) break;
/* 591 */       this.tagValue = 2;
/* 592 */       if (this.inlineTagStarted) break;
/* 593 */       valid = parseParam();
/*     */ 
/* 596 */       break;
/*     */     case 'r':
/* 598 */       if ((length != TAG_RETURN_LENGTH) || (!CharOperation.equals(TAG_RETURN, tagName, 0, length))) break;
/* 599 */       this.tagValue = 3;
/* 600 */       if (this.inlineTagStarted) break;
/* 601 */       valid = parseReturn();
/*     */ 
/* 604 */       break;
/*     */     case 's':
/* 606 */       if ((length == TAG_SEE_LENGTH) && (CharOperation.equals(TAG_SEE, tagName, 0, length))) {
/* 607 */         this.tagValue = 6;
/* 608 */         if (this.inlineTagStarted) break;
/* 609 */         valid = parseReference();
/*     */       }
/* 611 */       else if ((length == TAG_SERIAL_LENGTH) && (CharOperation.equals(TAG_SERIAL, tagName, 0, length))) {
/* 612 */         this.tagValue = 13;
/* 613 */         this.tagWaitingForDescription = this.tagValue;
/* 614 */       } else if ((length == TAG_SERIAL_DATA_LENGTH) && (CharOperation.equals(TAG_SERIAL_DATA, tagName, 0, length))) {
/* 615 */         this.tagValue = 14;
/* 616 */         this.tagWaitingForDescription = this.tagValue;
/* 617 */       } else if ((length == TAG_SERIAL_FIELD_LENGTH) && (CharOperation.equals(TAG_SERIAL_FIELD, tagName, 0, length))) {
/* 618 */         this.tagValue = 15;
/* 619 */         this.tagWaitingForDescription = this.tagValue; } else {
/* 620 */         if ((length != TAG_SINCE_LENGTH) || (!CharOperation.equals(TAG_SINCE, tagName, 0, length))) break;
/* 621 */         this.tagValue = 16;
/* 622 */         this.tagWaitingForDescription = this.tagValue;
/*     */       }
/* 624 */       break;
/*     */     case 't':
/* 626 */       if ((length != TAG_THROWS_LENGTH) || (!CharOperation.equals(TAG_THROWS, tagName, 0, length))) break;
/* 627 */       this.tagValue = 4;
/* 628 */       if (this.inlineTagStarted) break;
/* 629 */       valid = parseThrows();
/*     */ 
/* 632 */       break;
/*     */     case 'v':
/* 634 */       if ((length == TAG_VALUE_LENGTH) && (CharOperation.equals(TAG_VALUE, tagName, 0, length))) {
/* 635 */         this.tagValue = 10;
/* 636 */         if (this.sourceLevel >= 3211264L) {
/* 637 */           if (!this.inlineTagStarted) break;
/* 638 */           valid = parseReference();
/*     */         }
/* 641 */         else if (this.validValuePositions == -1L) {
/* 642 */           if ((this.invalidValuePositions != -1L) && 
/* 643 */             (this.reportProblems)) this.sourceParser.problemReporter().javadocUnexpectedTag((int)(this.invalidValuePositions >>> 32), (int)this.invalidValuePositions);
/*     */ 
/* 645 */           if (valid) {
/* 646 */             this.validValuePositions = ((this.tagSourceStart << 32) + this.tagSourceEnd);
/* 647 */             this.invalidValuePositions = -1L;
/*     */           } else {
/* 649 */             this.invalidValuePositions = ((this.tagSourceStart << 32) + this.tagSourceEnd);
/*     */           }
/*     */         } else {
/* 652 */           if (!this.reportProblems) break; this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, this.tagSourceEnd);
/*     */         }
/*     */       }
/* 655 */       else if ((length == TAG_VERSION_LENGTH) && (CharOperation.equals(TAG_VERSION, tagName, 0, length))) {
/* 656 */         this.tagValue = 17;
/* 657 */         this.tagWaitingForDescription = this.tagValue;
/*     */       } else {
/* 659 */         createTag();
/*     */       }
/* 661 */       break;
/*     */     case 'b':
/*     */     case 'f':
/*     */     case 'g':
/*     */     case 'h':
/*     */     case 'j':
/*     */     case 'k':
/*     */     case 'm':
/*     */     case 'n':
/*     */     case 'o':
/*     */     case 'q':
/*     */     case 'u':
/*     */     default:
/* 663 */       createTag();
/*     */     }
/*     */ 
/* 666 */     this.textStart = this.index;
/* 667 */     if (this.tagValue != 100) {
/* 668 */       if (!this.inlineTagStarted) {
/* 669 */         this.lastBlockTagValue = this.tagValue;
/*     */       }
/*     */ 
/* 673 */       if (((this.inlineTagStarted) && (JAVADOC_TAG_TYPE[this.tagValue] == 2)) || (
/* 674 */         (!this.inlineTagStarted) && (JAVADOC_TAG_TYPE[this.tagValue] == 1))) {
/* 675 */         valid = false;
/* 676 */         this.tagValue = 100;
/* 677 */         this.tagWaitingForDescription = 0;
/* 678 */         if (this.reportProblems) {
/* 679 */           this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, this.tagSourceEnd);
/*     */         }
/*     */       }
/*     */     }
/* 683 */     return valid;
/*     */   }
/*     */ 
/*     */   protected boolean parseParam()
/*     */     throws InvalidInputException
/*     */   {
/* 690 */     boolean valid = super.parseParam();
/* 691 */     this.tagWaitingForDescription = ((valid) && (this.reportProblems) ? 2 : 0);
/* 692 */     return valid;
/*     */   }
/*     */ 
/*     */   protected boolean pushParamName(boolean isTypeParam)
/*     */   {
/* 700 */     ASTNode nameRef = null;
/* 701 */     if (isTypeParam) {
/* 702 */       JavadocSingleTypeReference ref = new JavadocSingleTypeReference(this.identifierStack[1], 
/* 703 */         this.identifierPositionStack[1], 
/* 704 */         this.tagSourceStart, 
/* 705 */         this.tagSourceEnd);
/* 706 */       nameRef = ref;
/*     */     } else {
/* 708 */       JavadocSingleNameReference ref = new JavadocSingleNameReference(this.identifierStack[0], 
/* 709 */         this.identifierPositionStack[0], 
/* 710 */         this.tagSourceStart, 
/* 711 */         this.tagSourceEnd);
/* 712 */       nameRef = ref;
/*     */     }
/*     */ 
/* 715 */     if (this.astLengthPtr == -1) {
/* 716 */       pushOnAstStack(nameRef, true);
/*     */     }
/*     */     else {
/* 719 */       if (!isTypeParam) {
/* 720 */         for (int i = 1; i <= this.astLengthPtr; i += 3) {
/* 721 */           if (this.astLengthStack[i] != 0) {
/* 722 */             if (this.reportProblems) this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, this.tagSourceEnd);
/*     */ 
/* 725 */             if (this.invalidParamReferencesPtr == -1L) {
/* 726 */               this.invalidParamReferencesStack = new JavadocSingleNameReference[10];
/*     */             }
/* 728 */             int stackLength = this.invalidParamReferencesStack.length;
/* 729 */             if (++this.invalidParamReferencesPtr >= stackLength) {
/* 730 */               System.arraycopy(
/* 731 */                 this.invalidParamReferencesStack, 0, 
/* 732 */                 this.invalidParamReferencesStack = new JavadocSingleNameReference[stackLength + 10], 0, 
/* 733 */                 stackLength);
/*     */             }
/* 735 */             this.invalidParamReferencesStack[this.invalidParamReferencesPtr] = nameRef;
/* 736 */             return false;
/*     */           }
/*     */         }
/*     */       }
/* 740 */       switch (this.astLengthPtr % 3)
/*     */       {
/*     */       case 0:
/* 743 */         pushOnAstStack(nameRef, false);
/* 744 */         break;
/*     */       case 2:
/* 747 */         pushOnAstStack(nameRef, true);
/* 748 */         break;
/*     */       case 1:
/*     */       default:
/* 750 */         return false;
/*     */       }
/*     */     }
/* 753 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean pushSeeRef(Object statement)
/*     */   {
/* 760 */     if (this.astLengthPtr == -1) {
/* 761 */       pushOnAstStack(null, true);
/* 762 */       pushOnAstStack(null, true);
/* 763 */       pushOnAstStack(statement, true);
/*     */     } else {
/* 765 */       switch (this.astLengthPtr % 3)
/*     */       {
/*     */       case 0:
/* 768 */         pushOnAstStack(null, true);
/* 769 */         pushOnAstStack(statement, true);
/* 770 */         break;
/*     */       case 1:
/* 773 */         pushOnAstStack(statement, true);
/* 774 */         break;
/*     */       case 2:
/* 777 */         pushOnAstStack(statement, false);
/* 778 */         break;
/*     */       default:
/* 780 */         return false;
/*     */       }
/*     */     }
/* 783 */     return true;
/*     */   }
/*     */ 
/*     */   protected void pushText(int start, int end)
/*     */   {
/* 791 */     this.tagWaitingForDescription = 0;
/*     */   }
/*     */ 
/*     */   protected boolean pushThrowName(Object typeRef)
/*     */   {
/* 798 */     if (this.astLengthPtr == -1) {
/* 799 */       pushOnAstStack(null, true);
/* 800 */       pushOnAstStack(typeRef, true);
/*     */     } else {
/* 802 */       switch (this.astLengthPtr % 3)
/*     */       {
/*     */       case 0:
/* 805 */         pushOnAstStack(typeRef, true);
/* 806 */         break;
/*     */       case 1:
/* 809 */         pushOnAstStack(typeRef, false);
/* 810 */         break;
/*     */       case 2:
/* 813 */         pushOnAstStack(null, true);
/* 814 */         pushOnAstStack(typeRef, true);
/* 815 */         break;
/*     */       default:
/* 817 */         return false;
/*     */       }
/*     */     }
/* 820 */     return true;
/*     */   }
/*     */ 
/*     */   protected void refreshInlineTagPosition(int previousPosition)
/*     */   {
/* 829 */     if (this.tagWaitingForDescription != 0) {
/* 830 */       this.sourceParser.problemReporter().javadocMissingTagDescription(TAG_NAMES[this.tagWaitingForDescription], this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
/* 831 */       this.tagWaitingForDescription = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void refreshReturnStatement()
/*     */   {
/* 839 */     ((JavadocReturnStatement)this.returnStatement).bits &= -262145;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 843 */     StringBuffer buffer = new StringBuffer();
/* 844 */     buffer.append("check javadoc: ").append(this.checkDocComment).append("\n");
/* 845 */     buffer.append("javadoc: ").append(this.docComment).append("\n");
/* 846 */     buffer.append(super.toString());
/* 847 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   protected void updateDocComment()
/*     */   {
/* 858 */     switch (this.tagWaitingForDescription) {
/*     */     case 2:
/*     */     case 4:
/* 861 */       if (this.inlineTagStarted) break;
/* 862 */       int start = (int)(this.identifierPositionStack[0] >>> 32);
/* 863 */       int end = (int)this.identifierPositionStack[this.identifierPtr];
/* 864 */       this.sourceParser.problemReporter().javadocMissingTagDescriptionAfterReference(start, end, this.sourceParser.modifiers);
/*     */ 
/* 866 */       break;
/*     */     case 0:
/* 868 */       break;
/*     */     case 1:
/*     */     case 3:
/*     */     default:
/* 870 */       if (this.inlineTagStarted) break;
/* 871 */       this.sourceParser.problemReporter().javadocMissingTagDescription(TAG_NAMES[this.tagWaitingForDescription], this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
/*     */     }
/*     */ 
/* 875 */     this.tagWaitingForDescription = 0;
/*     */ 
/* 878 */     if ((this.inheritedPositions != null) && (this.inheritedPositionsPtr != this.inheritedPositions.length))
/*     */     {
/* 880 */       System.arraycopy(this.inheritedPositions, 0, 
/* 881 */         this.inheritedPositions = new long[this.inheritedPositionsPtr], 0, this.inheritedPositionsPtr);
/*     */     }
/* 883 */     this.docComment.inheritedPositions = this.inheritedPositions;
/* 884 */     this.docComment.valuePositions = (this.validValuePositions != -1L ? this.validValuePositions : this.invalidValuePositions);
/*     */ 
/* 887 */     if (this.returnStatement != null) {
/* 888 */       this.docComment.returnStatement = ((JavadocReturnStatement)this.returnStatement);
/*     */     }
/*     */ 
/* 892 */     if (this.invalidParamReferencesPtr >= 0) {
/* 893 */       this.docComment.invalidParameters = new JavadocSingleNameReference[this.invalidParamReferencesPtr + 1];
/* 894 */       System.arraycopy(this.invalidParamReferencesStack, 0, this.docComment.invalidParameters, 0, this.invalidParamReferencesPtr + 1);
/*     */     }
/*     */ 
/* 898 */     if (this.astLengthPtr == -1) {
/* 899 */       return;
/*     */     }
/*     */ 
/* 903 */     int[] sizes = new int[3];
/* 904 */     for (int i = 0; i <= this.astLengthPtr; i++) {
/* 905 */       sizes[(i % 3)] += this.astLengthStack[i];
/*     */     }
/* 907 */     this.docComment.seeReferences = new Expression[sizes[2]];
/* 908 */     this.docComment.exceptionReferences = new TypeReference[sizes[1]];
/* 909 */     int paramRefPtr = sizes[0];
/* 910 */     this.docComment.paramReferences = new JavadocSingleNameReference[paramRefPtr];
/* 911 */     int paramTypeParamPtr = sizes[0];
/* 912 */     this.docComment.paramTypeParameters = new JavadocSingleTypeReference[paramTypeParamPtr];
/*     */ 
/* 915 */     while (this.astLengthPtr >= 0) {
/* 916 */       int ptr = this.astLengthPtr % 3;
/*     */ 
/* 918 */       switch (ptr) {
/*     */       case 2:
/* 920 */         int size = this.astLengthStack[(this.astLengthPtr--)];
/* 921 */         for (int i = 0; i < size; i++)
/*     */         {
/*     */           int tmp462_460 = ptr;
/*     */           int[] tmp462_459 = sizes;
/*     */           int tmp466_465 = (tmp462_459[tmp462_460] - 1); tmp462_459[tmp462_460] = tmp466_465; this.docComment.seeReferences[tmp466_465] = ((Expression)this.astStack[(this.astPtr--)]);
/*     */         }
/* 924 */         break;
/*     */       case 1:
/* 928 */         int size = this.astLengthStack[(this.astLengthPtr--)];
/* 929 */         for (int i = 0; i < size; i++)
/*     */         {
/*     */           int tmp535_533 = ptr;
/*     */           int[] tmp535_532 = sizes;
/*     */           int tmp539_538 = (tmp535_532[tmp535_533] - 1); tmp535_532[tmp535_533] = tmp539_538; this.docComment.exceptionReferences[tmp539_538] = ((TypeReference)this.astStack[(this.astPtr--)]);
/*     */         }
/* 932 */         break;
/*     */       case 0:
/* 936 */         int size = this.astLengthStack[(this.astLengthPtr--)];
/* 937 */         for (int i = 0; i < size; i++) {
/* 938 */           Expression reference = (Expression)this.astStack[(this.astPtr--)];
/* 939 */           if ((reference instanceof JavadocSingleNameReference)) {
/* 940 */             paramRefPtr--; this.docComment.paramReferences[paramRefPtr] = ((JavadocSingleNameReference)reference);
/* 941 */           } else if ((reference instanceof JavadocSingleTypeReference)) {
/* 942 */             paramTypeParamPtr--; this.docComment.paramTypeParameters[paramTypeParamPtr] = ((JavadocSingleTypeReference)reference);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 949 */     if (paramRefPtr == 0) {
/* 950 */       this.docComment.paramTypeParameters = null;
/* 951 */     } else if (paramTypeParamPtr == 0) {
/* 952 */       this.docComment.paramReferences = null;
/*     */     } else {
/* 954 */       int size = sizes[0];
/* 955 */       System.arraycopy(this.docComment.paramReferences, paramRefPtr, this.docComment.paramReferences = new JavadocSingleNameReference[size - paramRefPtr], 0, size - paramRefPtr);
/* 956 */       System.arraycopy(this.docComment.paramTypeParameters, paramTypeParamPtr, this.docComment.paramTypeParameters = new JavadocSingleTypeReference[size - paramTypeParamPtr], 0, size - paramTypeParamPtr);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.JavadocParser
 * JD-Core Version:    0.6.0
 */