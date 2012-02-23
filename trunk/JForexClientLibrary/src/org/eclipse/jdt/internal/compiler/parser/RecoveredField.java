/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ 
/*     */ public class RecoveredField extends RecoveredElement
/*     */ {
/*     */   public FieldDeclaration fieldDeclaration;
/*     */   boolean alreadyCompletedFieldInitialization;
/*     */   public RecoveredAnnotation[] annotations;
/*     */   public int annotationCount;
/*     */   public int modifiers;
/*     */   public int modifiersStart;
/*     */   public RecoveredType[] anonymousTypes;
/*     */   public int anonymousTypeCount;
/*     */ 
/*     */   public RecoveredField(FieldDeclaration fieldDeclaration, RecoveredElement parent, int bracketBalance)
/*     */   {
/*  43 */     this(fieldDeclaration, parent, bracketBalance, null);
/*     */   }
/*     */   public RecoveredField(FieldDeclaration fieldDeclaration, RecoveredElement parent, int bracketBalance, Parser parser) {
/*  46 */     super(parent, bracketBalance, parser);
/*  47 */     this.fieldDeclaration = fieldDeclaration;
/*  48 */     this.alreadyCompletedFieldInitialization = (fieldDeclaration.initialization != null);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(FieldDeclaration addedfieldDeclaration, int bracketBalanceValue)
/*     */   {
/*  56 */     resetPendingModifiers();
/*  57 */     if (this.parent == null) return this;
/*     */ 
/*  59 */     if (this.fieldDeclaration.declarationSourceStart == addedfieldDeclaration.declarationSourceStart) {
/*  60 */       if (this.fieldDeclaration.initialization != null)
/*  61 */         updateSourceEndIfNecessary(this.fieldDeclaration.initialization.sourceEnd);
/*     */       else
/*  63 */         updateSourceEndIfNecessary(this.fieldDeclaration.sourceEnd);
/*     */     }
/*     */     else {
/*  66 */       updateSourceEndIfNecessary(previousAvailableLineEnd(addedfieldDeclaration.declarationSourceStart - 1));
/*     */     }
/*  68 */     return this.parent.add(addedfieldDeclaration, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Statement statement, int bracketBalanceValue)
/*     */   {
/*  76 */     if ((this.alreadyCompletedFieldInitialization) || (!(statement instanceof Expression))) {
/*  77 */       return super.add(statement, bracketBalanceValue);
/*     */     }
/*  79 */     this.alreadyCompletedFieldInitialization = true;
/*  80 */     this.fieldDeclaration.initialization = ((Expression)statement);
/*  81 */     this.fieldDeclaration.declarationSourceEnd = statement.sourceEnd;
/*  82 */     this.fieldDeclaration.declarationEnd = statement.sourceEnd;
/*  83 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue)
/*     */   {
/*  93 */     if ((this.alreadyCompletedFieldInitialization) || 
/*  94 */       ((typeDeclaration.bits & 0x200) == 0) || (
/*  95 */       (this.fieldDeclaration.declarationSourceEnd != 0) && (typeDeclaration.sourceStart > this.fieldDeclaration.declarationSourceEnd))) {
/*  96 */       return super.add(typeDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/*  99 */     if (this.anonymousTypes == null) {
/* 100 */       this.anonymousTypes = new RecoveredType[5];
/* 101 */       this.anonymousTypeCount = 0;
/*     */     }
/* 103 */     else if (this.anonymousTypeCount == this.anonymousTypes.length) {
/* 104 */       System.arraycopy(
/* 105 */         this.anonymousTypes, 
/* 106 */         0, 
/* 107 */         this.anonymousTypes = new RecoveredType[2 * this.anonymousTypeCount], 
/* 108 */         0, 
/* 109 */         this.anonymousTypeCount);
/*     */     }
/*     */ 
/* 113 */     RecoveredType element = new RecoveredType(typeDeclaration, this, bracketBalanceValue);
/* 114 */     this.anonymousTypes[(this.anonymousTypeCount++)] = element;
/* 115 */     return element;
/*     */   }
/*     */ 
/*     */   public void attach(RecoveredAnnotation[] annots, int annotCount, int mods, int modsSourceStart) {
/* 119 */     if (annotCount > 0) {
/* 120 */       Annotation[] existingAnnotations = this.fieldDeclaration.annotations;
/* 121 */       if (existingAnnotations != null) {
/* 122 */         this.annotations = new RecoveredAnnotation[annotCount];
/* 123 */         this.annotationCount = 0;
/* 124 */         for (int i = 0; i < annotCount; i++) {
/* 125 */           int j = 0;
/* 126 */           while (annots[i].annotation != existingAnnotations[j])
/*     */           {
/* 125 */             j++; if (j < existingAnnotations.length) {
/*     */               continue;
/*     */             }
/* 128 */             this.annotations[(this.annotationCount++)] = annots[i];
/*     */           }
/*     */         }
/*     */       } else {
/* 131 */         this.annotations = annots;
/* 132 */         this.annotationCount = annotCount;
/*     */       }
/*     */     }
/*     */ 
/* 136 */     if (mods != 0) {
/* 137 */       this.modifiers = mods;
/* 138 */       this.modifiersStart = modsSourceStart;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ASTNode parseTree()
/*     */   {
/* 145 */     return this.fieldDeclaration;
/*     */   }
/*     */ 
/*     */   public int sourceEnd()
/*     */   {
/* 151 */     return this.fieldDeclaration.declarationSourceEnd;
/*     */   }
/*     */   public String toString(int tab) {
/* 154 */     StringBuffer buffer = new StringBuffer(tabString(tab));
/* 155 */     buffer.append("Recovered field:\n");
/* 156 */     this.fieldDeclaration.print(tab + 1, buffer);
/* 157 */     if (this.annotations != null) {
/* 158 */       for (int i = 0; i < this.annotationCount; i++) {
/* 159 */         buffer.append("\n");
/* 160 */         buffer.append(this.annotations[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 163 */     if (this.anonymousTypes != null) {
/* 164 */       for (int i = 0; i < this.anonymousTypeCount; i++) {
/* 165 */         buffer.append("\n");
/* 166 */         buffer.append(this.anonymousTypes[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 169 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public FieldDeclaration updatedFieldDeclaration(int depth, Set knownTypes) {
/* 173 */     if (this.modifiers != 0) {
/* 174 */       this.fieldDeclaration.modifiers |= this.modifiers;
/* 175 */       if (this.modifiersStart < this.fieldDeclaration.declarationSourceStart) {
/* 176 */         this.fieldDeclaration.declarationSourceStart = this.modifiersStart;
/*     */       }
/*     */     }
/*     */ 
/* 180 */     if (this.annotationCount > 0) {
/* 181 */       int existingCount = this.fieldDeclaration.annotations == null ? 0 : this.fieldDeclaration.annotations.length;
/* 182 */       Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
/* 183 */       if (existingCount > 0) {
/* 184 */         System.arraycopy(this.fieldDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
/*     */       }
/* 186 */       for (int i = 0; i < this.annotationCount; i++) {
/* 187 */         annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
/*     */       }
/* 189 */       this.fieldDeclaration.annotations = annotationReferences;
/*     */ 
/* 191 */       int start = this.annotations[0].annotation.sourceStart;
/* 192 */       if (start < this.fieldDeclaration.declarationSourceStart) {
/* 193 */         this.fieldDeclaration.declarationSourceStart = start;
/*     */       }
/*     */     }
/*     */ 
/* 197 */     if (this.anonymousTypes != null) {
/* 198 */       if (this.fieldDeclaration.initialization == null) {
/* 199 */         for (int i = 0; i < this.anonymousTypeCount; i++) {
/* 200 */           RecoveredType recoveredType = this.anonymousTypes[i];
/* 201 */           TypeDeclaration typeDeclaration = recoveredType.typeDeclaration;
/* 202 */           if (typeDeclaration.declarationSourceEnd == 0) {
/* 203 */             typeDeclaration.declarationSourceEnd = this.fieldDeclaration.declarationSourceEnd;
/* 204 */             typeDeclaration.bodyEnd = this.fieldDeclaration.declarationSourceEnd;
/*     */           }
/* 206 */           if (recoveredType.preserveContent) {
/* 207 */             TypeDeclaration anonymousType = recoveredType.updatedTypeDeclaration(depth + 1, knownTypes);
/* 208 */             if (anonymousType != null) {
/* 209 */               this.fieldDeclaration.initialization = anonymousType.allocation;
/* 210 */               if (this.fieldDeclaration.declarationSourceEnd == 0) {
/* 211 */                 int end = anonymousType.declarationSourceEnd;
/* 212 */                 this.fieldDeclaration.declarationSourceEnd = end;
/* 213 */                 this.fieldDeclaration.declarationEnd = end;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 218 */         if (this.anonymousTypeCount > 0) this.fieldDeclaration.bits |= 2; 
/*     */       }
/* 219 */       else if (this.fieldDeclaration.getKind() == 3)
/*     */       {
/* 221 */         for (int i = 0; i < this.anonymousTypeCount; i++) {
/* 222 */           RecoveredType recoveredType = this.anonymousTypes[i];
/* 223 */           TypeDeclaration typeDeclaration = recoveredType.typeDeclaration;
/* 224 */           if (typeDeclaration.declarationSourceEnd == 0) {
/* 225 */             typeDeclaration.declarationSourceEnd = this.fieldDeclaration.declarationSourceEnd;
/* 226 */             typeDeclaration.bodyEnd = this.fieldDeclaration.declarationSourceEnd;
/*     */           }
/*     */ 
/* 230 */           recoveredType.updatedTypeDeclaration(depth, knownTypes);
/*     */         }
/*     */       }
/*     */     }
/* 234 */     return this.fieldDeclaration;
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd)
/*     */   {
/* 243 */     if (this.bracketBalance > 0) {
/* 244 */       this.bracketBalance -= 1;
/* 245 */       if (this.bracketBalance == 0) {
/* 246 */         if (this.fieldDeclaration.getKind() == 3) {
/* 247 */           updateSourceEndIfNecessary(braceEnd - 1);
/* 248 */           return this.parent;
/*     */         }
/* 250 */         this.alreadyCompletedFieldInitialization = true;
/*     */       }
/*     */ 
/* 253 */       return this;
/* 254 */     }if (this.bracketBalance == 0) {
/* 255 */       this.alreadyCompletedFieldInitialization = true;
/* 256 */       updateSourceEndIfNecessary(braceEnd - 1);
/*     */     }
/* 258 */     if (this.parent != null) {
/* 259 */       return this.parent.updateOnClosingBrace(braceStart, braceEnd);
/*     */     }
/* 261 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd)
/*     */   {
/* 268 */     if ((this.fieldDeclaration.declarationSourceEnd == 0) && 
/* 269 */       (((this.fieldDeclaration.type instanceof ArrayTypeReference)) || ((this.fieldDeclaration.type instanceof ArrayQualifiedTypeReference))) && 
/* 270 */       (!this.alreadyCompletedFieldInitialization)) {
/* 271 */       this.bracketBalance += 1;
/* 272 */       return null;
/*     */     }
/* 274 */     if ((this.fieldDeclaration.declarationSourceEnd == 0) && 
/* 275 */       (this.fieldDeclaration.getKind() == 3)) {
/* 276 */       this.bracketBalance += 1;
/* 277 */       return null;
/*     */     }
/*     */ 
/* 280 */     updateSourceEndIfNecessary(braceStart - 1, braceEnd - 1);
/* 281 */     return this.parent.updateOnOpeningBrace(braceStart, braceEnd);
/*     */   }
/*     */   public void updateParseTree() {
/* 284 */     updatedFieldDeclaration(0, new HashSet());
/*     */   }
/*     */ 
/*     */   public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd)
/*     */   {
/* 290 */     if (this.fieldDeclaration.declarationSourceEnd == 0) {
/* 291 */       this.fieldDeclaration.declarationSourceEnd = bodyEnd;
/* 292 */       this.fieldDeclaration.declarationEnd = bodyEnd;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredField
 * JD-Core Version:    0.6.0
 */