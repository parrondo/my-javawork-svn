/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Block;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Initializer;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ 
/*     */ public class RecoveredUnit extends RecoveredElement
/*     */ {
/*     */   public CompilationUnitDeclaration unitDeclaration;
/*     */   public RecoveredImport[] imports;
/*     */   public int importCount;
/*     */   public RecoveredType[] types;
/*     */   public int typeCount;
/*     */   int pendingModifiers;
/*  38 */   int pendingModifersSourceStart = -1;
/*     */   RecoveredAnnotation[] pendingAnnotations;
/*     */   int pendingAnnotationCount;
/*     */ 
/*     */   public RecoveredUnit(CompilationUnitDeclaration unitDeclaration, int bracketBalance, Parser parser)
/*     */   {
/*  43 */     super(null, bracketBalance, parser);
/*  44 */     this.unitDeclaration = unitDeclaration;
/*     */   }
/*     */   public RecoveredElement addAnnotationName(int identifierPtr, int identifierLengthPtr, int annotationStart, int bracketBalanceValue) {
/*  47 */     if (this.pendingAnnotations == null) {
/*  48 */       this.pendingAnnotations = new RecoveredAnnotation[5];
/*  49 */       this.pendingAnnotationCount = 0;
/*     */     }
/*  51 */     else if (this.pendingAnnotationCount == this.pendingAnnotations.length) {
/*  52 */       System.arraycopy(
/*  53 */         this.pendingAnnotations, 
/*  54 */         0, 
/*  55 */         this.pendingAnnotations = new RecoveredAnnotation[2 * this.pendingAnnotationCount], 
/*  56 */         0, 
/*  57 */         this.pendingAnnotationCount);
/*     */     }
/*     */ 
/*  61 */     RecoveredAnnotation element = new RecoveredAnnotation(identifierPtr, identifierLengthPtr, annotationStart, this, bracketBalanceValue);
/*     */ 
/*  63 */     this.pendingAnnotations[(this.pendingAnnotationCount++)] = element;
/*     */ 
/*  65 */     return element;
/*     */   }
/*     */   public void addModifier(int flag, int modifiersSourceStart) {
/*  68 */     this.pendingModifiers |= flag;
/*     */ 
/*  70 */     if (this.pendingModifersSourceStart < 0)
/*  71 */       this.pendingModifersSourceStart = modifiersSourceStart;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(AbstractMethodDeclaration methodDeclaration, int bracketBalanceValue)
/*     */   {
/*  80 */     if (this.typeCount > 0) {
/*  81 */       RecoveredType type = this.types[(this.typeCount - 1)];
/*  82 */       int start = type.bodyEnd;
/*  83 */       int end = type.typeDeclaration.bodyEnd;
/*  84 */       type.bodyEnd = 0;
/*  85 */       type.typeDeclaration.declarationSourceEnd = 0;
/*  86 */       type.typeDeclaration.bodyEnd = 0;
/*     */ 
/*  88 */       int kind = TypeDeclaration.kind(type.typeDeclaration.modifiers);
/*  89 */       if ((start > 0) && 
/*  90 */         (start < end) && 
/*  91 */         (kind != 2) && 
/*  92 */         (kind != 4))
/*     */       {
/*  94 */         Initializer initializer = new Initializer(new Block(0), 0);
/*  95 */         initializer.bodyStart = end;
/*  96 */         initializer.bodyEnd = end;
/*  97 */         initializer.declarationSourceStart = end;
/*  98 */         initializer.declarationSourceEnd = end;
/*  99 */         initializer.sourceStart = end;
/* 100 */         initializer.sourceEnd = end;
/* 101 */         type.add(initializer, bracketBalanceValue);
/*     */       }
/*     */ 
/* 104 */       resetPendingModifiers();
/*     */ 
/* 106 */       return type.add(methodDeclaration, bracketBalanceValue);
/*     */     }
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(FieldDeclaration fieldDeclaration, int bracketBalanceValue)
/*     */   {
/* 116 */     if (this.typeCount > 0) {
/* 117 */       RecoveredType type = this.types[(this.typeCount - 1)];
/* 118 */       type.bodyEnd = 0;
/* 119 */       type.typeDeclaration.declarationSourceEnd = 0;
/* 120 */       type.typeDeclaration.bodyEnd = 0;
/*     */ 
/* 122 */       resetPendingModifiers();
/*     */ 
/* 124 */       return type.add(fieldDeclaration, bracketBalanceValue);
/*     */     }
/* 126 */     return this;
/*     */   }
/*     */   public RecoveredElement add(ImportReference importReference, int bracketBalanceValue) {
/* 129 */     resetPendingModifiers();
/*     */ 
/* 131 */     if (this.imports == null) {
/* 132 */       this.imports = new RecoveredImport[5];
/* 133 */       this.importCount = 0;
/*     */     }
/* 135 */     else if (this.importCount == this.imports.length) {
/* 136 */       System.arraycopy(
/* 137 */         this.imports, 
/* 138 */         0, 
/* 139 */         this.imports = new RecoveredImport[2 * this.importCount], 
/* 140 */         0, 
/* 141 */         this.importCount);
/*     */     }
/*     */ 
/* 144 */     RecoveredImport element = new RecoveredImport(importReference, this, bracketBalanceValue);
/* 145 */     this.imports[(this.importCount++)] = element;
/*     */ 
/* 148 */     if (importReference.declarationSourceEnd == 0) return element;
/* 149 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue) {
/* 153 */     if (((typeDeclaration.bits & 0x200) != 0) && 
/* 154 */       (this.typeCount > 0))
/*     */     {
/* 156 */       RecoveredType lastType = this.types[(this.typeCount - 1)];
/* 157 */       lastType.bodyEnd = 0;
/* 158 */       lastType.typeDeclaration.bodyEnd = 0;
/* 159 */       lastType.typeDeclaration.declarationSourceEnd = 0;
/*     */       RecoveredType tmp52_51 = lastType; tmp52_51.bracketBalance = (tmp52_51.bracketBalance + 1);
/*     */ 
/* 162 */       resetPendingModifiers();
/*     */ 
/* 164 */       return lastType.add(typeDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/* 167 */     if (this.types == null) {
/* 168 */       this.types = new RecoveredType[5];
/* 169 */       this.typeCount = 0;
/*     */     }
/* 171 */     else if (this.typeCount == this.types.length) {
/* 172 */       System.arraycopy(
/* 173 */         this.types, 
/* 174 */         0, 
/* 175 */         this.types = new RecoveredType[2 * this.typeCount], 
/* 176 */         0, 
/* 177 */         this.typeCount);
/*     */     }
/*     */ 
/* 180 */     RecoveredType element = new RecoveredType(typeDeclaration, this, bracketBalanceValue);
/* 181 */     this.types[(this.typeCount++)] = element;
/*     */ 
/* 183 */     if (this.pendingAnnotationCount > 0) {
/* 184 */       element.attach(
/* 185 */         this.pendingAnnotations, 
/* 186 */         this.pendingAnnotationCount, 
/* 187 */         this.pendingModifiers, 
/* 188 */         this.pendingModifersSourceStart);
/*     */     }
/* 190 */     resetPendingModifiers();
/*     */ 
/* 193 */     if (typeDeclaration.declarationSourceEnd == 0) return element;
/* 194 */     return this;
/*     */   }
/*     */ 
/*     */   public ASTNode parseTree()
/*     */   {
/* 200 */     return this.unitDeclaration;
/*     */   }
/*     */   public void resetPendingModifiers() {
/* 203 */     this.pendingAnnotations = null;
/* 204 */     this.pendingAnnotationCount = 0;
/* 205 */     this.pendingModifiers = 0;
/* 206 */     this.pendingModifersSourceStart = -1;
/*     */   }
/*     */ 
/*     */   public int sourceEnd()
/*     */   {
/* 212 */     return this.unitDeclaration.sourceEnd;
/*     */   }
/*     */   public String toString(int tab) {
/* 215 */     StringBuffer result = new StringBuffer(tabString(tab));
/* 216 */     result.append("Recovered unit: [\n");
/* 217 */     this.unitDeclaration.print(tab + 1, result);
/* 218 */     result.append(tabString(tab + 1));
/* 219 */     result.append("]");
/* 220 */     if (this.imports != null) {
/* 221 */       for (int i = 0; i < this.importCount; i++) {
/* 222 */         result.append("\n");
/* 223 */         result.append(this.imports[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 226 */     if (this.types != null) {
/* 227 */       for (int i = 0; i < this.typeCount; i++) {
/* 228 */         result.append("\n");
/* 229 */         result.append(this.types[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 232 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public CompilationUnitDeclaration updatedCompilationUnitDeclaration()
/*     */   {
/* 237 */     if (this.importCount > 0) {
/* 238 */       ImportReference[] importRefences = new ImportReference[this.importCount];
/* 239 */       for (int i = 0; i < this.importCount; i++) {
/* 240 */         importRefences[i] = this.imports[i].updatedImportReference();
/*     */       }
/* 242 */       this.unitDeclaration.imports = importRefences;
/*     */     }
/*     */ 
/* 245 */     if (this.typeCount > 0) {
/* 246 */       int existingCount = this.unitDeclaration.types == null ? 0 : this.unitDeclaration.types.length;
/* 247 */       TypeDeclaration[] typeDeclarations = new TypeDeclaration[existingCount + this.typeCount];
/* 248 */       if (existingCount > 0) {
/* 249 */         System.arraycopy(this.unitDeclaration.types, 0, typeDeclarations, 0, existingCount);
/*     */       }
/*     */ 
/* 252 */       if (this.types[(this.typeCount - 1)].typeDeclaration.declarationSourceEnd == 0) {
/* 253 */         this.types[(this.typeCount - 1)].typeDeclaration.declarationSourceEnd = this.unitDeclaration.sourceEnd;
/* 254 */         this.types[(this.typeCount - 1)].typeDeclaration.bodyEnd = this.unitDeclaration.sourceEnd;
/*     */       }
/*     */ 
/* 257 */       Set knownTypes = new HashSet();
/* 258 */       int actualCount = existingCount;
/* 259 */       for (int i = 0; i < this.typeCount; i++) {
/* 260 */         TypeDeclaration typeDecl = this.types[i].updatedTypeDeclaration(0, knownTypes);
/*     */ 
/* 262 */         if ((typeDecl != null) && ((typeDecl.bits & 0x100) == 0)) {
/* 263 */           typeDeclarations[(actualCount++)] = typeDecl;
/*     */         }
/*     */       }
/* 266 */       if (actualCount != this.typeCount) {
/* 267 */         System.arraycopy(
/* 268 */           typeDeclarations, 
/* 269 */           0, 
/* 270 */           typeDeclarations = new TypeDeclaration[existingCount + actualCount], 
/* 271 */           0, 
/* 272 */           existingCount + actualCount);
/*     */       }
/* 274 */       this.unitDeclaration.types = typeDeclarations;
/*     */     }
/* 276 */     return this.unitDeclaration;
/*     */   }
/*     */   public void updateParseTree() {
/* 279 */     updatedCompilationUnitDeclaration();
/*     */   }
/*     */ 
/*     */   public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd)
/*     */   {
/* 285 */     if (this.unitDeclaration.sourceEnd == 0)
/* 286 */       this.unitDeclaration.sourceEnd = bodyEnd;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredUnit
 * JD-Core Version:    0.6.0
 */