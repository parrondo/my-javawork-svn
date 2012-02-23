/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
/*     */ import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ 
/*     */ public class RecoveredAnnotation extends RecoveredElement
/*     */ {
/*     */   public static final int MARKER = 0;
/*     */   public static final int NORMAL = 1;
/*     */   public static final int SINGLE_MEMBER = 2;
/*     */   private int kind;
/*     */   private int identifierPtr;
/*     */   private int identifierLengthPtr;
/*     */   private int sourceStart;
/*     */   public boolean hasPendingMemberValueName;
/*  34 */   public int memberValuPairEqualEnd = -1;
/*     */   public Annotation annotation;
/*     */ 
/*     */   public RecoveredAnnotation(int identifierPtr, int identifierLengthPtr, int sourceStart, RecoveredElement parent, int bracketBalance)
/*     */   {
/*  38 */     super(parent, bracketBalance);
/*  39 */     this.kind = 0;
/*  40 */     this.identifierPtr = identifierPtr;
/*  41 */     this.identifierLengthPtr = identifierLengthPtr;
/*  42 */     this.sourceStart = sourceStart;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue) {
/*  46 */     if ((this.annotation == null) && ((typeDeclaration.bits & 0x200) != 0))
/*     */     {
/*  48 */       return this;
/*     */     }
/*  50 */     return super.add(typeDeclaration, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement addAnnotationName(int identPtr, int identLengthPtr, int annotationStart, int bracketBalanceValue)
/*     */   {
/*  55 */     RecoveredAnnotation element = new RecoveredAnnotation(identPtr, identLengthPtr, annotationStart, this, bracketBalanceValue);
/*     */ 
/*  57 */     return element;
/*     */   }
/*     */ 
/*     */   public RecoveredElement addAnnotation(Annotation annot, int index) {
/*  61 */     this.annotation = annot;
/*     */ 
/*  63 */     if (this.parent != null) return this.parent;
/*  64 */     return this;
/*     */   }
/*     */ 
/*     */   public void updateFromParserState() {
/*  68 */     Parser parser = parser();
/*     */ 
/*  70 */     if ((this.annotation == null) && (this.identifierPtr <= parser.identifierPtr)) {
/*  71 */       Annotation annot = null;
/*     */ 
/*  73 */       boolean needUpdateRParenPos = false;
/*     */ 
/*  75 */       MemberValuePair pendingMemberValueName = null;
/*  76 */       if ((this.hasPendingMemberValueName) && (this.identifierPtr < parser.identifierPtr)) {
/*  77 */         char[] memberValueName = parser.identifierStack[(this.identifierPtr + 1)];
/*     */ 
/*  79 */         long pos = parser.identifierPositionStack[(this.identifierPtr + 1)];
/*  80 */         int start = (int)(pos >>> 32);
/*  81 */         int end = (int)pos;
/*  82 */         int valueEnd = this.memberValuPairEqualEnd > -1 ? this.memberValuPairEqualEnd : end;
/*     */ 
/*  84 */         SingleNameReference fakeExpression = new SingleNameReference(RecoveryScanner.FAKE_IDENTIFIER, (valueEnd + 1L << 32) + valueEnd);
/*  85 */         pendingMemberValueName = new MemberValuePair(memberValueName, start, end, fakeExpression);
/*     */       }
/*  87 */       parser.identifierPtr = this.identifierPtr;
/*  88 */       parser.identifierLengthPtr = this.identifierLengthPtr;
/*  89 */       TypeReference typeReference = parser.getAnnotationType();
/*     */ 
/*  91 */       switch (this.kind) {
/*     */       case 1:
/*  93 */         if ((parser.astPtr <= -1) || (!(parser.astStack[parser.astPtr] instanceof MemberValuePair))) break;
/*  94 */         MemberValuePair[] memberValuePairs = (MemberValuePair[])null;
/*     */ 
/*  96 */         int argLength = parser.astLengthStack[parser.astLengthPtr];
/*  97 */         int argStart = parser.astPtr - argLength + 1;
/*     */ 
/*  99 */         if (argLength <= 0)
/*     */           break;
/*     */         int annotationEnd;
/*     */         int annotationEnd;
/* 101 */         if (pendingMemberValueName != null) {
/* 102 */           memberValuePairs = new MemberValuePair[argLength + 1];
/*     */ 
/* 104 */           System.arraycopy(parser.astStack, argStart, memberValuePairs, 0, argLength);
/* 105 */           parser.astLengthPtr -= 1;
/* 106 */           parser.astPtr -= argLength;
/*     */ 
/* 108 */           memberValuePairs[argLength] = pendingMemberValueName;
/*     */ 
/* 110 */           annotationEnd = pendingMemberValueName.sourceEnd;
/*     */         } else {
/* 112 */           memberValuePairs = new MemberValuePair[argLength];
/*     */ 
/* 114 */           System.arraycopy(parser.astStack, argStart, memberValuePairs, 0, argLength);
/* 115 */           parser.astLengthPtr -= 1;
/* 116 */           parser.astPtr -= argLength;
/*     */ 
/* 118 */           MemberValuePair lastMemberValuePair = memberValuePairs[(memberValuePairs.length - 1)];
/*     */ 
/* 120 */           annotationEnd = 
/* 121 */             lastMemberValuePair.value != null ? 
/* 124 */             lastMemberValuePair.value.sourceEnd : (lastMemberValuePair.value instanceof Annotation) ? 
/* 123 */             ((Annotation)lastMemberValuePair.value).declarationSourceEnd : 
/* 125 */             lastMemberValuePair.sourceEnd;
/*     */         }
/*     */ 
/* 128 */         NormalAnnotation normalAnnotation = new NormalAnnotation(typeReference, this.sourceStart);
/* 129 */         normalAnnotation.memberValuePairs = memberValuePairs;
/* 130 */         normalAnnotation.declarationSourceEnd = annotationEnd;
/* 131 */         normalAnnotation.bits |= 32;
/*     */ 
/* 133 */         annot = normalAnnotation;
/*     */ 
/* 135 */         needUpdateRParenPos = true;
/*     */ 
/* 140 */         break;
/*     */       case 2:
/* 142 */         if (parser.expressionPtr <= -1) break;
/* 143 */         Expression memberValue = parser.expressionStack[(parser.expressionPtr--)];
/*     */ 
/* 145 */         SingleMemberAnnotation singleMemberAnnotation = new SingleMemberAnnotation(typeReference, this.sourceStart);
/* 146 */         singleMemberAnnotation.memberValue = memberValue;
/* 147 */         singleMemberAnnotation.declarationSourceEnd = memberValue.sourceEnd;
/* 148 */         singleMemberAnnotation.bits |= 32;
/*     */ 
/* 150 */         annot = singleMemberAnnotation;
/*     */ 
/* 152 */         needUpdateRParenPos = true;
/*     */       }
/*     */ 
/* 157 */       if (!needUpdateRParenPos) {
/* 158 */         if (pendingMemberValueName != null) {
/* 159 */           NormalAnnotation normalAnnotation = new NormalAnnotation(typeReference, this.sourceStart);
/* 160 */           normalAnnotation.memberValuePairs = new MemberValuePair[] { pendingMemberValueName };
/* 161 */           normalAnnotation.declarationSourceEnd = pendingMemberValueName.value.sourceEnd;
/* 162 */           normalAnnotation.bits |= 32;
/*     */ 
/* 164 */           annot = normalAnnotation;
/*     */         } else {
/* 166 */           MarkerAnnotation markerAnnotation = new MarkerAnnotation(typeReference, this.sourceStart);
/* 167 */           markerAnnotation.declarationSourceEnd = markerAnnotation.sourceEnd;
/* 168 */           markerAnnotation.bits |= 32;
/*     */ 
/* 170 */           annot = markerAnnotation;
/*     */         }
/*     */       }
/*     */ 
/* 174 */       parser.currentElement = addAnnotation(annot, this.identifierPtr);
/* 175 */       parser.annotationRecoveryCheckPoint(annot.sourceStart, annot.declarationSourceEnd);
/* 176 */       if (this.parent != null)
/*     */       {
/* 178 */         this.parent.updateFromParserState();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public ASTNode parseTree() {
/* 184 */     return this.annotation;
/*     */   }
/*     */ 
/*     */   public void resetPendingModifiers() {
/* 188 */     if (this.parent != null) this.parent.resetPendingModifiers(); 
/*     */   }
/*     */ 
/*     */   public void setKind(int kind)
/*     */   {
/* 192 */     this.kind = kind;
/*     */   }
/*     */ 
/*     */   public int sourceEnd() {
/* 196 */     if (this.annotation == null) {
/* 197 */       Parser parser = parser();
/* 198 */       if (this.identifierPtr < parser.identifierPositionStack.length) {
/* 199 */         return (int)parser.identifierPositionStack[this.identifierPtr];
/*     */       }
/* 201 */       return this.sourceStart;
/*     */     }
/*     */ 
/* 204 */     return this.annotation.declarationSourceEnd;
/*     */   }
/*     */ 
/*     */   public String toString(int tab) {
/* 208 */     if (this.annotation != null) {
/* 209 */       return tabString(tab) + "Recovered annotation:\n" + this.annotation.print(tab + 1, new StringBuffer(10));
/*     */     }
/* 211 */     return tabString(tab) + "Recovered annotation: identiferPtr=" + this.identifierPtr + " identiferlengthPtr=" + this.identifierLengthPtr + "\n";
/*     */   }
/*     */ 
/*     */   public Annotation updatedAnnotationReference()
/*     */   {
/* 216 */     return this.annotation;
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd) {
/* 220 */     if (this.bracketBalance > 0) {
/* 221 */       this.bracketBalance -= 1;
/* 222 */       return this;
/*     */     }
/* 224 */     if (this.parent != null) {
/* 225 */       return this.parent.updateOnClosingBrace(braceStart, braceEnd);
/*     */     }
/* 227 */     return this;
/*     */   }
/*     */ 
/*     */   public void updateParseTree() {
/* 231 */     updatedAnnotationReference();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredAnnotation
 * JD-Core Version:    0.6.0
 */