/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Block;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Initializer;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ 
/*     */ public class RecoveredInitializer extends RecoveredField
/*     */   implements TerminalTokens
/*     */ {
/*     */   public RecoveredType[] localTypes;
/*     */   public int localTypeCount;
/*     */   public RecoveredBlock initializerBody;
/*     */   int pendingModifiers;
/*  34 */   int pendingModifersSourceStart = -1;
/*     */   RecoveredAnnotation[] pendingAnnotations;
/*     */   int pendingAnnotationCount;
/*     */ 
/*     */   public RecoveredInitializer(FieldDeclaration fieldDeclaration, RecoveredElement parent, int bracketBalance)
/*     */   {
/*  39 */     this(fieldDeclaration, parent, bracketBalance, null);
/*     */   }
/*     */   public RecoveredInitializer(FieldDeclaration fieldDeclaration, RecoveredElement parent, int bracketBalance, Parser parser) {
/*  42 */     super(fieldDeclaration, parent, bracketBalance, parser);
/*  43 */     this.foundOpeningBrace = true;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue)
/*     */   {
/*  54 */     if ((this.fieldDeclaration.declarationSourceEnd > 0) && 
/*  55 */       (nestedBlockDeclaration.sourceStart > this.fieldDeclaration.declarationSourceEnd)) {
/*  56 */       resetPendingModifiers();
/*  57 */       if (this.parent == null) return this;
/*  58 */       return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/*  61 */     if (!this.foundOpeningBrace) {
/*  62 */       this.foundOpeningBrace = true;
/*  63 */       this.bracketBalance += 1;
/*     */     }
/*  65 */     this.initializerBody = new RecoveredBlock(nestedBlockDeclaration, this, bracketBalanceValue);
/*  66 */     if (nestedBlockDeclaration.sourceEnd == 0) return this.initializerBody;
/*  67 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(FieldDeclaration newFieldDeclaration, int bracketBalanceValue)
/*     */   {
/*  73 */     resetPendingModifiers();
/*     */     char[][] fieldTypeName;
/*  77 */     if (((newFieldDeclaration.modifiers & 0xFFFFFFEF) != 0) || 
/*  78 */       (newFieldDeclaration.type == null) || (
/*  79 */       ((fieldTypeName = newFieldDeclaration.type.getTypeName()).length == 1) && 
/*  80 */       (CharOperation.equals(fieldTypeName[0], TypeBinding.VOID.sourceName())))) {
/*  81 */       if (this.parent == null) return this;
/*  82 */       updateSourceEndIfNecessary(previousAvailableLineEnd(newFieldDeclaration.declarationSourceStart - 1));
/*  83 */       return this.parent.add(newFieldDeclaration, bracketBalanceValue);
/*     */     }
/*     */     char[][] fieldTypeName;
/*  90 */     if ((this.fieldDeclaration.declarationSourceEnd > 0) && 
/*  91 */       (newFieldDeclaration.declarationSourceStart > this.fieldDeclaration.declarationSourceEnd)) {
/*  92 */       if (this.parent == null) return this;
/*  93 */       return this.parent.add(newFieldDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/*  96 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue)
/*     */   {
/* 105 */     if ((this.fieldDeclaration.declarationSourceEnd != 0) && 
/* 106 */       (localDeclaration.declarationSourceStart > this.fieldDeclaration.declarationSourceEnd)) {
/* 107 */       resetPendingModifiers();
/* 108 */       if (this.parent == null) return this;
/* 109 */       return this.parent.add(localDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/* 112 */     Block block = new Block(0);
/* 113 */     block.sourceStart = ((Initializer)this.fieldDeclaration).sourceStart;
/* 114 */     RecoveredElement element = add(block, 1);
/* 115 */     if (this.initializerBody != null) {
/* 116 */       this.initializerBody.attachPendingModifiers(
/* 117 */         this.pendingAnnotations, 
/* 118 */         this.pendingAnnotationCount, 
/* 119 */         this.pendingModifiers, 
/* 120 */         this.pendingModifersSourceStart);
/*     */     }
/* 122 */     resetPendingModifiers();
/* 123 */     return element.add(localDeclaration, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Statement statement, int bracketBalanceValue)
/*     */   {
/* 132 */     if ((this.fieldDeclaration.declarationSourceEnd != 0) && 
/* 133 */       (statement.sourceStart > this.fieldDeclaration.declarationSourceEnd)) {
/* 134 */       resetPendingModifiers();
/* 135 */       if (this.parent == null) return this;
/* 136 */       return this.parent.add(statement, bracketBalanceValue);
/*     */     }
/*     */ 
/* 139 */     Block block = new Block(0);
/* 140 */     block.sourceStart = ((Initializer)this.fieldDeclaration).sourceStart;
/* 141 */     RecoveredElement element = add(block, 1);
/*     */ 
/* 143 */     if (this.initializerBody != null) {
/* 144 */       this.initializerBody.attachPendingModifiers(
/* 145 */         this.pendingAnnotations, 
/* 146 */         this.pendingAnnotationCount, 
/* 147 */         this.pendingModifiers, 
/* 148 */         this.pendingModifersSourceStart);
/*     */     }
/* 150 */     resetPendingModifiers();
/*     */ 
/* 152 */     return element.add(statement, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue)
/*     */   {
/* 158 */     if ((this.fieldDeclaration.declarationSourceEnd != 0) && 
/* 159 */       (typeDeclaration.declarationSourceStart > this.fieldDeclaration.declarationSourceEnd)) {
/* 160 */       resetPendingModifiers();
/* 161 */       if (this.parent == null) return this;
/* 162 */       return this.parent.add(typeDeclaration, bracketBalanceValue);
/*     */     }
/* 164 */     if (((typeDeclaration.bits & 0x100) != 0) || (parser().methodRecoveryActivated) || (parser().statementRecoveryActivated))
/*     */     {
/* 166 */       Block block = new Block(0);
/* 167 */       block.sourceStart = ((Initializer)this.fieldDeclaration).sourceStart;
/* 168 */       RecoveredElement element = add(block, 1);
/* 169 */       if (this.initializerBody != null) {
/* 170 */         this.initializerBody.attachPendingModifiers(
/* 171 */           this.pendingAnnotations, 
/* 172 */           this.pendingAnnotationCount, 
/* 173 */           this.pendingModifiers, 
/* 174 */           this.pendingModifersSourceStart);
/*     */       }
/* 176 */       resetPendingModifiers();
/* 177 */       return element.add(typeDeclaration, bracketBalanceValue);
/*     */     }
/* 179 */     if (this.localTypes == null) {
/* 180 */       this.localTypes = new RecoveredType[5];
/* 181 */       this.localTypeCount = 0;
/*     */     }
/* 183 */     else if (this.localTypeCount == this.localTypes.length) {
/* 184 */       System.arraycopy(
/* 185 */         this.localTypes, 
/* 186 */         0, 
/* 187 */         this.localTypes = new RecoveredType[2 * this.localTypeCount], 
/* 188 */         0, 
/* 189 */         this.localTypeCount);
/*     */     }
/*     */ 
/* 192 */     RecoveredType element = new RecoveredType(typeDeclaration, this, bracketBalanceValue);
/* 193 */     this.localTypes[(this.localTypeCount++)] = element;
/*     */ 
/* 195 */     if (this.pendingAnnotationCount > 0) {
/* 196 */       element.attach(
/* 197 */         this.pendingAnnotations, 
/* 198 */         this.pendingAnnotationCount, 
/* 199 */         this.pendingModifiers, 
/* 200 */         this.pendingModifersSourceStart);
/*     */     }
/* 202 */     resetPendingModifiers();
/*     */ 
/* 205 */     if (!this.foundOpeningBrace) {
/* 206 */       this.foundOpeningBrace = true;
/* 207 */       this.bracketBalance += 1;
/*     */     }
/* 209 */     return element;
/*     */   }
/*     */   public RecoveredElement addAnnotationName(int identifierPtr, int identifierLengthPtr, int annotationStart, int bracketBalanceValue) {
/* 212 */     if (this.pendingAnnotations == null) {
/* 213 */       this.pendingAnnotations = new RecoveredAnnotation[5];
/* 214 */       this.pendingAnnotationCount = 0;
/*     */     }
/* 216 */     else if (this.pendingAnnotationCount == this.pendingAnnotations.length) {
/* 217 */       System.arraycopy(
/* 218 */         this.pendingAnnotations, 
/* 219 */         0, 
/* 220 */         this.pendingAnnotations = new RecoveredAnnotation[2 * this.pendingAnnotationCount], 
/* 221 */         0, 
/* 222 */         this.pendingAnnotationCount);
/*     */     }
/*     */ 
/* 226 */     RecoveredAnnotation element = new RecoveredAnnotation(identifierPtr, identifierLengthPtr, annotationStart, this, bracketBalanceValue);
/*     */ 
/* 228 */     this.pendingAnnotations[(this.pendingAnnotationCount++)] = element;
/*     */ 
/* 230 */     return element;
/*     */   }
/*     */   public void addModifier(int flag, int modifiersSourceStart) {
/* 233 */     this.pendingModifiers |= flag;
/*     */ 
/* 235 */     if (this.pendingModifersSourceStart < 0)
/* 236 */       this.pendingModifersSourceStart = modifiersSourceStart;
/*     */   }
/*     */ 
/*     */   public void resetPendingModifiers() {
/* 240 */     this.pendingAnnotations = null;
/* 241 */     this.pendingAnnotationCount = 0;
/* 242 */     this.pendingModifiers = 0;
/* 243 */     this.pendingModifersSourceStart = -1;
/*     */   }
/*     */   public String toString(int tab) {
/* 246 */     StringBuffer result = new StringBuffer(tabString(tab));
/* 247 */     result.append("Recovered initializer:\n");
/* 248 */     this.fieldDeclaration.print(tab + 1, result);
/* 249 */     if (this.annotations != null) {
/* 250 */       for (int i = 0; i < this.annotationCount; i++) {
/* 251 */         result.append("\n");
/* 252 */         result.append(this.annotations[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 255 */     if (this.initializerBody != null) {
/* 256 */       result.append("\n");
/* 257 */       result.append(this.initializerBody.toString(tab + 1));
/*     */     }
/* 259 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public FieldDeclaration updatedFieldDeclaration(int depth, Set knownTypes) {
/* 263 */     if (this.initializerBody != null) {
/* 264 */       Block block = this.initializerBody.updatedBlock(depth, knownTypes);
/* 265 */       if (block != null) {
/* 266 */         Initializer initializer = (Initializer)this.fieldDeclaration;
/* 267 */         initializer.block = block;
/*     */ 
/* 269 */         if (initializer.declarationSourceEnd == 0) {
/* 270 */           initializer.declarationSourceEnd = block.sourceEnd;
/* 271 */           initializer.bodyEnd = block.sourceEnd;
/*     */         }
/*     */       }
/* 274 */       if (this.localTypeCount > 0) this.fieldDeclaration.bits |= 2;
/*     */     }
/*     */ 
/* 277 */     if (this.fieldDeclaration.sourceEnd == 0) {
/* 278 */       this.fieldDeclaration.sourceEnd = this.fieldDeclaration.declarationSourceEnd;
/*     */     }
/* 280 */     return this.fieldDeclaration;
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd)
/*     */   {
/* 287 */     if ((--this.bracketBalance <= 0) && (this.parent != null)) {
/* 288 */       updateSourceEndIfNecessary(braceStart, braceEnd);
/* 289 */       return this.parent;
/*     */     }
/* 291 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd)
/*     */   {
/* 298 */     this.bracketBalance += 1;
/* 299 */     return this;
/*     */   }
/*     */ 
/*     */   public void updateSourceEndIfNecessary(int braceStart, int braceEnd)
/*     */   {
/* 305 */     if (this.fieldDeclaration.declarationSourceEnd == 0) {
/* 306 */       Initializer initializer = (Initializer)this.fieldDeclaration;
/* 307 */       if (parser().rBraceSuccessorStart >= braceEnd) {
/* 308 */         if (initializer.bodyStart < parser().rBraceEnd)
/* 309 */           initializer.declarationSourceEnd = parser().rBraceEnd;
/*     */         else {
/* 311 */           initializer.declarationSourceEnd = initializer.bodyStart;
/*     */         }
/* 313 */         if (initializer.bodyStart < parser().rBraceStart)
/* 314 */           initializer.bodyEnd = parser().rBraceStart;
/*     */         else
/* 316 */           initializer.bodyEnd = initializer.bodyStart;
/*     */       }
/*     */       else {
/* 319 */         initializer.declarationSourceEnd = braceEnd;
/* 320 */         initializer.bodyEnd = (braceStart - 1);
/*     */       }
/* 322 */       if (initializer.block != null)
/* 323 */         initializer.block.sourceEnd = initializer.declarationSourceEnd;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredInitializer
 * JD-Core Version:    0.6.0
 */