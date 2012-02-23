/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Block;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class RecoveredElement
/*     */ {
/*     */   public RecoveredElement parent;
/*     */   public int bracketBalance;
/*     */   public boolean foundOpeningBrace;
/*     */   protected Parser recoveringParser;
/*     */ 
/*     */   public RecoveredElement(RecoveredElement parent, int bracketBalance)
/*     */   {
/*  33 */     this(parent, bracketBalance, null);
/*     */   }
/*     */   public RecoveredElement(RecoveredElement parent, int bracketBalance, Parser parser) {
/*  36 */     this.parent = parent;
/*  37 */     this.bracketBalance = bracketBalance;
/*  38 */     this.recoveringParser = parser;
/*     */   }
/*     */ 
/*     */   public RecoveredElement addAnnotationName(int identifierPtr, int identifierLengthPtr, int annotationStart, int bracketBalanceValue) {
/*  42 */     resetPendingModifiers();
/*  43 */     if (this.parent == null) return this;
/*  44 */     updateSourceEndIfNecessary(previousAvailableLineEnd(annotationStart - 1));
/*  45 */     return this.parent.addAnnotationName(identifierPtr, identifierLengthPtr, annotationStart, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(AbstractMethodDeclaration methodDeclaration, int bracketBalanceValue)
/*     */   {
/*  53 */     resetPendingModifiers();
/*  54 */     if (this.parent == null) return this;
/*  55 */     updateSourceEndIfNecessary(previousAvailableLineEnd(methodDeclaration.declarationSourceStart - 1));
/*  56 */     return this.parent.add(methodDeclaration, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue)
/*     */   {
/*  64 */     resetPendingModifiers();
/*  65 */     if (this.parent == null) return this;
/*  66 */     updateSourceEndIfNecessary(previousAvailableLineEnd(nestedBlockDeclaration.sourceStart - 1));
/*  67 */     return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(FieldDeclaration fieldDeclaration, int bracketBalanceValue)
/*     */   {
/*  75 */     resetPendingModifiers();
/*  76 */     if (this.parent == null) return this;
/*  77 */     updateSourceEndIfNecessary(previousAvailableLineEnd(fieldDeclaration.declarationSourceStart - 1));
/*  78 */     return this.parent.add(fieldDeclaration, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(ImportReference importReference, int bracketBalanceValue)
/*     */   {
/*  86 */     resetPendingModifiers();
/*  87 */     if (this.parent == null) return this;
/*  88 */     updateSourceEndIfNecessary(previousAvailableLineEnd(importReference.declarationSourceStart - 1));
/*  89 */     return this.parent.add(importReference, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue)
/*     */   {
/*  97 */     resetPendingModifiers();
/*  98 */     if (this.parent == null) return this;
/*  99 */     updateSourceEndIfNecessary(previousAvailableLineEnd(localDeclaration.declarationSourceStart - 1));
/* 100 */     return this.parent.add(localDeclaration, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Statement statement, int bracketBalanceValue)
/*     */   {
/* 108 */     resetPendingModifiers();
/* 109 */     if (this.parent == null) return this;
/* 110 */     updateSourceEndIfNecessary(previousAvailableLineEnd(statement.sourceStart - 1));
/* 111 */     return this.parent.add(statement, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue)
/*     */   {
/* 119 */     resetPendingModifiers();
/* 120 */     if (this.parent == null) return this;
/* 121 */     updateSourceEndIfNecessary(previousAvailableLineEnd(typeDeclaration.declarationSourceStart - 1));
/* 122 */     return this.parent.add(typeDeclaration, bracketBalanceValue);
/*     */   }
/*     */   protected void addBlockStatement(RecoveredBlock recoveredBlock) {
/* 125 */     Block block = recoveredBlock.blockDeclaration;
/* 126 */     if (block.statements != null) {
/* 127 */       Statement[] statements = block.statements;
/* 128 */       for (int i = 0; i < statements.length; i++)
/* 129 */         recoveredBlock.add(statements[i], 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addModifier(int flag, int modifiersSourceStart)
/*     */   {
/*     */   }
/*     */ 
/*     */   public int depth()
/*     */   {
/* 140 */     int depth = 0;
/* 141 */     RecoveredElement current = this;
/* 142 */     while ((current = current.parent) != null) depth++;
/* 143 */     return depth;
/*     */   }
/*     */ 
/*     */   public RecoveredInitializer enclosingInitializer()
/*     */   {
/* 149 */     RecoveredElement current = this;
/* 150 */     while (current != null) {
/* 151 */       if ((current instanceof RecoveredInitializer)) {
/* 152 */         return (RecoveredInitializer)current;
/*     */       }
/* 154 */       current = current.parent;
/*     */     }
/* 156 */     return null;
/*     */   }
/*     */ 
/*     */   public RecoveredMethod enclosingMethod()
/*     */   {
/* 162 */     RecoveredElement current = this;
/* 163 */     while (current != null) {
/* 164 */       if ((current instanceof RecoveredMethod)) {
/* 165 */         return (RecoveredMethod)current;
/*     */       }
/* 167 */       current = current.parent;
/*     */     }
/* 169 */     return null;
/*     */   }
/*     */ 
/*     */   public RecoveredType enclosingType()
/*     */   {
/* 175 */     RecoveredElement current = this;
/* 176 */     while (current != null) {
/* 177 */       if ((current instanceof RecoveredType)) {
/* 178 */         return (RecoveredType)current;
/*     */       }
/* 180 */       current = current.parent;
/*     */     }
/* 182 */     return null;
/*     */   }
/*     */ 
/*     */   public Parser parser()
/*     */   {
/* 188 */     RecoveredElement current = this;
/* 189 */     while (current != null) {
/* 190 */       if (current.recoveringParser != null) {
/* 191 */         return current.recoveringParser;
/*     */       }
/* 193 */       current = current.parent;
/*     */     }
/* 195 */     return null;
/*     */   }
/*     */ 
/*     */   public ASTNode parseTree()
/*     */   {
/* 201 */     return null;
/*     */   }
/*     */ 
/*     */   public void resetPendingModifiers()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void preserveEnclosingBlocks()
/*     */   {
/* 211 */     RecoveredElement current = this;
/* 212 */     while (current != null) {
/* 213 */       if ((current instanceof RecoveredBlock)) {
/* 214 */         ((RecoveredBlock)current).preserveContent = true;
/*     */       }
/* 216 */       if ((current instanceof RecoveredType)) {
/* 217 */         ((RecoveredType)current).preserveContent = true;
/*     */       }
/* 219 */       current = current.parent;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int previousAvailableLineEnd(int position)
/*     */   {
/* 229 */     Parser parser = parser();
/* 230 */     if (parser == null) return position;
/*     */ 
/* 232 */     Scanner scanner = parser.scanner;
/* 233 */     if (scanner.lineEnds == null) return position;
/*     */ 
/* 235 */     int index = Util.getLineNumber(position, scanner.lineEnds, 0, scanner.linePtr);
/* 236 */     if (index < 2) return position;
/* 237 */     int previousLineEnd = scanner.lineEnds[(index - 2)];
/*     */ 
/* 239 */     char[] source = scanner.source;
/* 240 */     for (int i = previousLineEnd + 1; i < position; i++) {
/* 241 */       if ((source[i] != ' ') && (source[i] != '\t')) return position;
/*     */     }
/* 243 */     return previousLineEnd;
/*     */   }
/*     */ 
/*     */   public int sourceEnd()
/*     */   {
/* 249 */     return 0;
/*     */   }
/*     */   protected String tabString(int tab) {
/* 252 */     StringBuffer result = new StringBuffer();
/* 253 */     for (int i = tab; i > 0; i--) {
/* 254 */       result.append("  ");
/*     */     }
/* 256 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public RecoveredElement topElement()
/*     */   {
/* 262 */     RecoveredElement current = this;
/* 263 */     while (current.parent != null) {
/* 264 */       current = current.parent;
/*     */     }
/* 266 */     return current;
/*     */   }
/*     */   public String toString() {
/* 269 */     return toString(0);
/*     */   }
/*     */   public String toString(int tab) {
/* 272 */     return super.toString();
/*     */   }
/*     */ 
/*     */   public RecoveredType type()
/*     */   {
/* 278 */     RecoveredElement current = this;
/* 279 */     while (current != null) {
/* 280 */       if ((current instanceof RecoveredType)) {
/* 281 */         return (RecoveredType)current;
/*     */       }
/* 283 */       current = current.parent;
/*     */     }
/* 285 */     return null;
/*     */   }
/*     */ 
/*     */   public void updateBodyStart(int bodyStart)
/*     */   {
/* 291 */     this.foundOpeningBrace = true;
/*     */   }
/*     */ 
/*     */   public void updateFromParserState()
/*     */   {
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd)
/*     */   {
/* 305 */     if ((--this.bracketBalance <= 0) && (this.parent != null)) {
/* 306 */       updateSourceEndIfNecessary(braceStart, braceEnd);
/* 307 */       return this.parent;
/*     */     }
/* 309 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd)
/*     */   {
/* 318 */     if (this.bracketBalance++ == 0) {
/* 319 */       updateBodyStart(braceEnd + 1);
/* 320 */       return this;
/*     */     }
/* 322 */     return null;
/*     */   }
/*     */ 
/*     */   public void updateParseTree()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void updateSourceEndIfNecessary(int braceStart, int braceEnd)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void updateSourceEndIfNecessary(int sourceEnd)
/*     */   {
/* 337 */     updateSourceEndIfNecessary(sourceEnd + 1, sourceEnd);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredElement
 * JD-Core Version:    0.6.0
 */