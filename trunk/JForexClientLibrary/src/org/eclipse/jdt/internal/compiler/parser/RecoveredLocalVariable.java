/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*     */ 
/*     */ public class RecoveredLocalVariable extends RecoveredStatement
/*     */ {
/*     */   public RecoveredAnnotation[] annotations;
/*     */   public int annotationCount;
/*     */   public int modifiers;
/*     */   public int modifiersStart;
/*     */   public LocalDeclaration localDeclaration;
/*     */   boolean alreadyCompletedLocalInitialization;
/*     */ 
/*     */   public RecoveredLocalVariable(LocalDeclaration localDeclaration, RecoveredElement parent, int bracketBalance)
/*     */   {
/*  38 */     super(localDeclaration, parent, bracketBalance);
/*  39 */     this.localDeclaration = localDeclaration;
/*  40 */     this.alreadyCompletedLocalInitialization = (localDeclaration.initialization != null);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Statement stmt, int bracketBalanceValue)
/*     */   {
/*  47 */     if ((this.alreadyCompletedLocalInitialization) || (!(stmt instanceof Expression))) {
/*  48 */       return super.add(stmt, bracketBalanceValue);
/*     */     }
/*  50 */     this.alreadyCompletedLocalInitialization = true;
/*  51 */     this.localDeclaration.initialization = ((Expression)stmt);
/*  52 */     this.localDeclaration.declarationSourceEnd = stmt.sourceEnd;
/*  53 */     this.localDeclaration.declarationEnd = stmt.sourceEnd;
/*  54 */     return this;
/*     */   }
/*     */ 
/*     */   public void attach(RecoveredAnnotation[] annots, int annotCount, int mods, int modsSourceStart) {
/*  58 */     if (annotCount > 0) {
/*  59 */       Annotation[] existingAnnotations = this.localDeclaration.annotations;
/*  60 */       if (existingAnnotations != null) {
/*  61 */         this.annotations = new RecoveredAnnotation[annotCount];
/*  62 */         this.annotationCount = 0;
/*  63 */         for (int i = 0; i < annotCount; i++) {
/*  64 */           int j = 0;
/*  65 */           while (annots[i].annotation != existingAnnotations[j])
/*     */           {
/*  64 */             j++; if (j < existingAnnotations.length) {
/*     */               continue;
/*     */             }
/*  67 */             this.annotations[(this.annotationCount++)] = annots[i];
/*     */           }
/*     */         }
/*     */       } else {
/*  70 */         this.annotations = annots;
/*  71 */         this.annotationCount = annotCount;
/*     */       }
/*     */     }
/*     */ 
/*  75 */     if (mods != 0) {
/*  76 */       this.modifiers = mods;
/*  77 */       this.modifiersStart = modsSourceStart;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ASTNode parseTree()
/*     */   {
/*  84 */     return this.localDeclaration;
/*     */   }
/*     */ 
/*     */   public int sourceEnd()
/*     */   {
/*  90 */     return this.localDeclaration.declarationSourceEnd;
/*     */   }
/*     */   public String toString(int tab) {
/*  93 */     return tabString(tab) + "Recovered local variable:\n" + this.localDeclaration.print(tab + 1, new StringBuffer(10));
/*     */   }
/*     */ 
/*     */   public Statement updatedStatement(int depth, Set knownTypes) {
/*  97 */     if (this.modifiers != 0) {
/*  98 */       this.localDeclaration.modifiers |= this.modifiers;
/*  99 */       if (this.modifiersStart < this.localDeclaration.declarationSourceStart) {
/* 100 */         this.localDeclaration.declarationSourceStart = this.modifiersStart;
/*     */       }
/*     */     }
/*     */ 
/* 104 */     if (this.annotationCount > 0) {
/* 105 */       int existingCount = this.localDeclaration.annotations == null ? 0 : this.localDeclaration.annotations.length;
/* 106 */       Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
/* 107 */       if (existingCount > 0) {
/* 108 */         System.arraycopy(this.localDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
/*     */       }
/* 110 */       for (int i = 0; i < this.annotationCount; i++) {
/* 111 */         annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
/*     */       }
/* 113 */       this.localDeclaration.annotations = annotationReferences;
/*     */ 
/* 115 */       int start = this.annotations[0].annotation.sourceStart;
/* 116 */       if (start < this.localDeclaration.declarationSourceStart) {
/* 117 */         this.localDeclaration.declarationSourceStart = start;
/*     */       }
/*     */     }
/* 120 */     return this.localDeclaration;
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd)
/*     */   {
/* 129 */     if (this.bracketBalance > 0) {
/* 130 */       this.bracketBalance -= 1;
/* 131 */       if (this.bracketBalance == 0) this.alreadyCompletedLocalInitialization = true;
/* 132 */       return this;
/*     */     }
/* 134 */     if (this.parent != null) {
/* 135 */       return this.parent.updateOnClosingBrace(braceStart, braceEnd);
/*     */     }
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd)
/*     */   {
/* 144 */     if ((this.localDeclaration.declarationSourceEnd == 0) && 
/* 145 */       (((this.localDeclaration.type instanceof ArrayTypeReference)) || ((this.localDeclaration.type instanceof ArrayQualifiedTypeReference))) && 
/* 146 */       (!this.alreadyCompletedLocalInitialization)) {
/* 147 */       this.bracketBalance += 1;
/* 148 */       return null;
/*     */     }
/*     */ 
/* 151 */     updateSourceEndIfNecessary(braceStart - 1, braceEnd - 1);
/* 152 */     return this.parent.updateOnOpeningBrace(braceStart, braceEnd);
/*     */   }
/*     */   public void updateParseTree() {
/* 155 */     updatedStatement(0, new HashSet());
/*     */   }
/*     */ 
/*     */   public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd)
/*     */   {
/* 161 */     if (this.localDeclaration.declarationSourceEnd == 0) {
/* 162 */       this.localDeclaration.declarationSourceEnd = bodyEnd;
/* 163 */       this.localDeclaration.declarationEnd = bodyEnd;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredLocalVariable
 * JD-Core Version:    0.6.0
 */