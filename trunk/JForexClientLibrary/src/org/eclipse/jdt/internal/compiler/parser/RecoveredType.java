/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Block;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Initializer;
/*     */ import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ 
/*     */ public class RecoveredType extends RecoveredStatement
/*     */   implements TerminalTokens
/*     */ {
/*     */   public static final int MAX_TYPE_DEPTH = 256;
/*     */   public TypeDeclaration typeDeclaration;
/*     */   public RecoveredAnnotation[] annotations;
/*     */   public int annotationCount;
/*     */   public int modifiers;
/*     */   public int modifiersStart;
/*     */   public RecoveredType[] memberTypes;
/*     */   public int memberTypeCount;
/*     */   public RecoveredField[] fields;
/*     */   public int fieldCount;
/*     */   public RecoveredMethod[] methods;
/*     */   public int methodCount;
/*  52 */   public boolean preserveContent = false;
/*     */   public int bodyEnd;
/*  55 */   public boolean insideEnumConstantPart = false;
/*     */   public TypeParameter[] pendingTypeParameters;
/*     */   public int pendingTypeParametersStart;
/*     */   int pendingModifiers;
/*  61 */   int pendingModifersSourceStart = -1;
/*     */   RecoveredAnnotation[] pendingAnnotations;
/*     */   int pendingAnnotationCount;
/*     */ 
/*     */   public RecoveredType(TypeDeclaration typeDeclaration, RecoveredElement parent, int bracketBalance)
/*     */   {
/*  66 */     super(typeDeclaration, parent, bracketBalance);
/*  67 */     this.typeDeclaration = typeDeclaration;
/*  68 */     if ((typeDeclaration.allocation != null) && (typeDeclaration.allocation.type == null))
/*     */     {
/*  70 */       this.foundOpeningBrace = true;
/*     */     }
/*  72 */     else this.foundOpeningBrace = (!bodyStartsAtHeaderEnd());
/*     */ 
/*  74 */     this.insideEnumConstantPart = (TypeDeclaration.kind(typeDeclaration.modifiers) == 3);
/*  75 */     if (this.foundOpeningBrace) {
/*  76 */       this.bracketBalance += 1;
/*     */     }
/*     */ 
/*  79 */     this.preserveContent = ((parser().methodRecoveryActivated) || (parser().statementRecoveryActivated));
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(AbstractMethodDeclaration methodDeclaration, int bracketBalanceValue)
/*     */   {
/*  85 */     if ((this.typeDeclaration.declarationSourceEnd != 0) && 
/*  86 */       (methodDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd)) {
/*  87 */       this.pendingTypeParameters = null;
/*  88 */       resetPendingModifiers();
/*     */ 
/*  90 */       return this.parent.add(methodDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/*  93 */     if (this.methods == null) {
/*  94 */       this.methods = new RecoveredMethod[5];
/*  95 */       this.methodCount = 0;
/*     */     }
/*  97 */     else if (this.methodCount == this.methods.length) {
/*  98 */       System.arraycopy(
/*  99 */         this.methods, 
/* 100 */         0, 
/* 101 */         this.methods = new RecoveredMethod[2 * this.methodCount], 
/* 102 */         0, 
/* 103 */         this.methodCount);
/*     */     }
/*     */ 
/* 106 */     RecoveredMethod element = new RecoveredMethod(methodDeclaration, this, bracketBalanceValue, this.recoveringParser);
/* 107 */     this.methods[(this.methodCount++)] = element;
/*     */ 
/* 109 */     if (this.pendingTypeParameters != null) {
/* 110 */       element.attach(this.pendingTypeParameters, this.pendingTypeParametersStart);
/* 111 */       this.pendingTypeParameters = null;
/*     */     }
/*     */ 
/* 114 */     if (this.pendingAnnotationCount > 0) {
/* 115 */       element.attach(
/* 116 */         this.pendingAnnotations, 
/* 117 */         this.pendingAnnotationCount, 
/* 118 */         this.pendingModifiers, 
/* 119 */         this.pendingModifersSourceStart);
/*     */     }
/* 121 */     resetPendingModifiers();
/*     */ 
/* 123 */     this.insideEnumConstantPart = false;
/*     */ 
/* 126 */     if (!this.foundOpeningBrace) {
/* 127 */       this.foundOpeningBrace = true;
/* 128 */       this.bracketBalance += 1;
/*     */     }
/*     */ 
/* 131 */     if (methodDeclaration.declarationSourceEnd == 0) return element;
/* 132 */     return this;
/*     */   }
/*     */   public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue) {
/* 135 */     this.pendingTypeParameters = null;
/* 136 */     resetPendingModifiers();
/*     */ 
/* 138 */     int mods = 0;
/* 139 */     if (parser().recoveredStaticInitializerStart != 0) {
/* 140 */       mods = 8;
/*     */     }
/* 142 */     return add(new Initializer(nestedBlockDeclaration, mods), bracketBalanceValue);
/*     */   }
/*     */   public RecoveredElement add(FieldDeclaration fieldDeclaration, int bracketBalanceValue) {
/* 145 */     this.pendingTypeParameters = null;
/*     */ 
/* 149 */     if ((this.typeDeclaration.declarationSourceEnd != 0) && 
/* 150 */       (fieldDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd))
/*     */     {
/* 152 */       resetPendingModifiers();
/*     */ 
/* 154 */       return this.parent.add(fieldDeclaration, bracketBalanceValue);
/*     */     }
/* 156 */     if (this.fields == null) {
/* 157 */       this.fields = new RecoveredField[5];
/* 158 */       this.fieldCount = 0;
/*     */     }
/* 160 */     else if (this.fieldCount == this.fields.length) {
/* 161 */       System.arraycopy(
/* 162 */         this.fields, 
/* 163 */         0, 
/* 164 */         this.fields = new RecoveredField[2 * this.fieldCount], 
/* 165 */         0, 
/* 166 */         this.fieldCount);
/*     */     }
/*     */     RecoveredField element;
/*     */     RecoveredField element;
/* 170 */     switch (fieldDeclaration.getKind()) {
/*     */     case 1:
/*     */     case 3:
/* 173 */       element = new RecoveredField(fieldDeclaration, this, bracketBalanceValue);
/* 174 */       break;
/*     */     case 2:
/* 176 */       element = new RecoveredInitializer(fieldDeclaration, this, bracketBalanceValue);
/* 177 */       break;
/*     */     default:
/* 180 */       return this;
/*     */     }
/*     */     RecoveredField element;
/* 182 */     this.fields[(this.fieldCount++)] = element;
/*     */ 
/* 184 */     if (this.pendingAnnotationCount > 0) {
/* 185 */       element.attach(
/* 186 */         this.pendingAnnotations, 
/* 187 */         this.pendingAnnotationCount, 
/* 188 */         this.pendingModifiers, 
/* 189 */         this.pendingModifersSourceStart);
/*     */     }
/* 191 */     resetPendingModifiers();
/*     */ 
/* 194 */     if (!this.foundOpeningBrace) {
/* 195 */       this.foundOpeningBrace = true;
/* 196 */       this.bracketBalance += 1;
/*     */     }
/*     */ 
/* 199 */     if (fieldDeclaration.declarationSourceEnd == 0) return element;
/* 200 */     return this;
/*     */   }
/*     */   public RecoveredElement add(TypeDeclaration memberTypeDeclaration, int bracketBalanceValue) {
/* 203 */     this.pendingTypeParameters = null;
/*     */ 
/* 207 */     if ((this.typeDeclaration.declarationSourceEnd != 0) && 
/* 208 */       (memberTypeDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd))
/*     */     {
/* 210 */       resetPendingModifiers();
/*     */ 
/* 212 */       return this.parent.add(memberTypeDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/* 215 */     this.insideEnumConstantPart = false;
/*     */ 
/* 217 */     if ((memberTypeDeclaration.bits & 0x200) != 0) {
/* 218 */       if (this.methodCount > 0)
/*     */       {
/* 220 */         RecoveredMethod lastMethod = this.methods[(this.methodCount - 1)];
/* 221 */         lastMethod.methodDeclaration.bodyEnd = 0;
/* 222 */         lastMethod.methodDeclaration.declarationSourceEnd = 0;
/*     */         RecoveredMethod tmp95_94 = lastMethod; tmp95_94.bracketBalance = (tmp95_94.bracketBalance + 1);
/*     */ 
/* 225 */         resetPendingModifiers();
/*     */ 
/* 227 */         return lastMethod.add(memberTypeDeclaration, bracketBalanceValue);
/*     */       }
/*     */ 
/* 230 */       return this;
/*     */     }
/*     */ 
/* 234 */     if (this.memberTypes == null) {
/* 235 */       this.memberTypes = new RecoveredType[5];
/* 236 */       this.memberTypeCount = 0;
/*     */     }
/* 238 */     else if (this.memberTypeCount == this.memberTypes.length) {
/* 239 */       System.arraycopy(
/* 240 */         this.memberTypes, 
/* 241 */         0, 
/* 242 */         this.memberTypes = new RecoveredType[2 * this.memberTypeCount], 
/* 243 */         0, 
/* 244 */         this.memberTypeCount);
/*     */     }
/*     */ 
/* 247 */     RecoveredType element = new RecoveredType(memberTypeDeclaration, this, bracketBalanceValue);
/* 248 */     this.memberTypes[(this.memberTypeCount++)] = element;
/*     */ 
/* 250 */     if (this.pendingAnnotationCount > 0) {
/* 251 */       element.attach(
/* 252 */         this.pendingAnnotations, 
/* 253 */         this.pendingAnnotationCount, 
/* 254 */         this.pendingModifiers, 
/* 255 */         this.pendingModifersSourceStart);
/*     */     }
/* 257 */     resetPendingModifiers();
/*     */ 
/* 260 */     if (!this.foundOpeningBrace) {
/* 261 */       this.foundOpeningBrace = true;
/* 262 */       this.bracketBalance += 1;
/*     */     }
/*     */ 
/* 265 */     if (memberTypeDeclaration.declarationSourceEnd == 0) return element;
/* 266 */     return this;
/*     */   }
/*     */   public void add(TypeParameter[] parameters, int startPos) {
/* 269 */     this.pendingTypeParameters = parameters;
/* 270 */     this.pendingTypeParametersStart = startPos;
/*     */   }
/*     */   public RecoveredElement addAnnotationName(int identifierPtr, int identifierLengthPtr, int annotationStart, int bracketBalanceValue) {
/* 273 */     if (this.pendingAnnotations == null) {
/* 274 */       this.pendingAnnotations = new RecoveredAnnotation[5];
/* 275 */       this.pendingAnnotationCount = 0;
/*     */     }
/* 277 */     else if (this.pendingAnnotationCount == this.pendingAnnotations.length) {
/* 278 */       System.arraycopy(
/* 279 */         this.pendingAnnotations, 
/* 280 */         0, 
/* 281 */         this.pendingAnnotations = new RecoveredAnnotation[2 * this.pendingAnnotationCount], 
/* 282 */         0, 
/* 283 */         this.pendingAnnotationCount);
/*     */     }
/*     */ 
/* 287 */     RecoveredAnnotation element = new RecoveredAnnotation(identifierPtr, identifierLengthPtr, annotationStart, this, bracketBalanceValue);
/*     */ 
/* 289 */     this.pendingAnnotations[(this.pendingAnnotationCount++)] = element;
/*     */ 
/* 291 */     return element;
/*     */   }
/*     */   public void addModifier(int flag, int modifiersSourceStart) {
/* 294 */     this.pendingModifiers |= flag;
/*     */ 
/* 296 */     if (this.pendingModifersSourceStart < 0)
/* 297 */       this.pendingModifersSourceStart = modifiersSourceStart;
/*     */   }
/*     */ 
/*     */   public void attach(RecoveredAnnotation[] annots, int annotCount, int mods, int modsSourceStart) {
/* 301 */     if (annotCount > 0) {
/* 302 */       Annotation[] existingAnnotations = this.typeDeclaration.annotations;
/* 303 */       if (existingAnnotations != null) {
/* 304 */         this.annotations = new RecoveredAnnotation[annotCount];
/* 305 */         this.annotationCount = 0;
/* 306 */         for (int i = 0; i < annotCount; i++) {
/* 307 */           int j = 0;
/* 308 */           while (annots[i].annotation != existingAnnotations[j])
/*     */           {
/* 307 */             j++; if (j < existingAnnotations.length) {
/*     */               continue;
/*     */             }
/* 310 */             this.annotations[(this.annotationCount++)] = annots[i];
/*     */           }
/*     */         }
/*     */       } else {
/* 313 */         this.annotations = annots;
/* 314 */         this.annotationCount = annotCount;
/*     */       }
/*     */     }
/*     */ 
/* 318 */     if (mods != 0) {
/* 319 */       this.modifiers = mods;
/* 320 */       this.modifiersStart = modsSourceStart;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int bodyEnd()
/*     */   {
/* 327 */     if (this.bodyEnd == 0) return this.typeDeclaration.declarationSourceEnd;
/* 328 */     return this.bodyEnd;
/*     */   }
/*     */   public boolean bodyStartsAtHeaderEnd() {
/* 331 */     if (this.typeDeclaration.superInterfaces == null) {
/* 332 */       if (this.typeDeclaration.superclass == null) {
/* 333 */         if (this.typeDeclaration.typeParameters == null) {
/* 334 */           return this.typeDeclaration.bodyStart == this.typeDeclaration.sourceEnd + 1;
/*     */         }
/* 336 */         return this.typeDeclaration.bodyStart == this.typeDeclaration.typeParameters[(this.typeDeclaration.typeParameters.length - 1)].sourceEnd + 1;
/*     */       }
/*     */ 
/* 339 */       return this.typeDeclaration.bodyStart == this.typeDeclaration.superclass.sourceEnd + 1;
/*     */     }
/*     */ 
/* 342 */     return this.typeDeclaration.bodyStart == 
/* 343 */       this.typeDeclaration.superInterfaces[(this.typeDeclaration.superInterfaces.length - 1)].sourceEnd + 1;
/*     */   }
/*     */ 
/*     */   public RecoveredType enclosingType()
/*     */   {
/* 350 */     RecoveredElement current = this.parent;
/* 351 */     while (current != null) {
/* 352 */       if ((current instanceof RecoveredType)) {
/* 353 */         return (RecoveredType)current;
/*     */       }
/* 355 */       current = current.parent;
/*     */     }
/* 357 */     return null;
/*     */   }
/*     */   public int lastMemberEnd() {
/* 360 */     int lastMemberEnd = this.typeDeclaration.bodyStart;
/*     */ 
/* 362 */     if (this.fieldCount > 0) {
/* 363 */       FieldDeclaration lastField = this.fields[(this.fieldCount - 1)].fieldDeclaration;
/* 364 */       if ((lastMemberEnd < lastField.declarationSourceEnd) && (lastField.declarationSourceEnd != 0)) {
/* 365 */         lastMemberEnd = lastField.declarationSourceEnd;
/*     */       }
/*     */     }
/*     */ 
/* 369 */     if (this.methodCount > 0) {
/* 370 */       AbstractMethodDeclaration lastMethod = this.methods[(this.methodCount - 1)].methodDeclaration;
/* 371 */       if ((lastMemberEnd < lastMethod.declarationSourceEnd) && (lastMethod.declarationSourceEnd != 0)) {
/* 372 */         lastMemberEnd = lastMethod.declarationSourceEnd;
/*     */       }
/*     */     }
/*     */ 
/* 376 */     if (this.memberTypeCount > 0) {
/* 377 */       TypeDeclaration lastType = this.memberTypes[(this.memberTypeCount - 1)].typeDeclaration;
/* 378 */       if ((lastMemberEnd < lastType.declarationSourceEnd) && (lastType.declarationSourceEnd != 0)) {
/* 379 */         lastMemberEnd = lastType.declarationSourceEnd;
/*     */       }
/*     */     }
/*     */ 
/* 383 */     return lastMemberEnd;
/*     */   }
/*     */   public char[] name() {
/* 386 */     return this.typeDeclaration.name;
/*     */   }
/*     */ 
/*     */   public ASTNode parseTree()
/*     */   {
/* 392 */     return this.typeDeclaration;
/*     */   }
/*     */   public void resetPendingModifiers() {
/* 395 */     this.pendingAnnotations = null;
/* 396 */     this.pendingAnnotationCount = 0;
/* 397 */     this.pendingModifiers = 0;
/* 398 */     this.pendingModifersSourceStart = -1;
/*     */   }
/*     */ 
/*     */   public int sourceEnd()
/*     */   {
/* 404 */     return this.typeDeclaration.declarationSourceEnd;
/*     */   }
/*     */   public String toString(int tab) {
/* 407 */     StringBuffer result = new StringBuffer(tabString(tab));
/* 408 */     result.append("Recovered type:\n");
/* 409 */     if ((this.typeDeclaration.bits & 0x200) != 0) {
/* 410 */       result.append(tabString(tab));
/* 411 */       result.append(" ");
/*     */     }
/* 413 */     this.typeDeclaration.print(tab + 1, result);
/* 414 */     if (this.annotations != null) {
/* 415 */       for (int i = 0; i < this.annotationCount; i++) {
/* 416 */         result.append("\n");
/* 417 */         result.append(this.annotations[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 420 */     if (this.memberTypes != null) {
/* 421 */       for (int i = 0; i < this.memberTypeCount; i++) {
/* 422 */         result.append("\n");
/* 423 */         result.append(this.memberTypes[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 426 */     if (this.fields != null) {
/* 427 */       for (int i = 0; i < this.fieldCount; i++) {
/* 428 */         result.append("\n");
/* 429 */         result.append(this.fields[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 432 */     if (this.methods != null) {
/* 433 */       for (int i = 0; i < this.methodCount; i++) {
/* 434 */         result.append("\n");
/* 435 */         result.append(this.methods[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 438 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public void updateBodyStart(int bodyStart)
/*     */   {
/* 444 */     this.foundOpeningBrace = true;
/* 445 */     this.typeDeclaration.bodyStart = bodyStart;
/*     */   }
/*     */ 
/*     */   public Statement updatedStatement(int depth, Set knownTypes)
/*     */   {
/* 450 */     if (((this.typeDeclaration.bits & 0x200) != 0) && (!this.preserveContent)) {
/* 451 */       return null;
/*     */     }
/*     */ 
/* 454 */     TypeDeclaration updatedType = updatedTypeDeclaration(depth + 1, knownTypes);
/* 455 */     if ((updatedType != null) && ((updatedType.bits & 0x200) != 0))
/*     */     {
/* 457 */       QualifiedAllocationExpression allocation = updatedType.allocation;
/*     */ 
/* 459 */       if (allocation.statementEnd == -1) {
/* 460 */         allocation.statementEnd = updatedType.declarationSourceEnd;
/*     */       }
/* 462 */       return allocation;
/*     */     }
/* 464 */     return updatedType;
/*     */   }
/*     */   public TypeDeclaration updatedTypeDeclaration(int depth, Set knownTypes) {
/* 467 */     if (depth >= 256) return null;
/*     */ 
/* 469 */     if (knownTypes.contains(this.typeDeclaration)) return null;
/* 470 */     knownTypes.add(this.typeDeclaration);
/*     */ 
/* 472 */     int lastEnd = this.typeDeclaration.bodyStart;
/*     */ 
/* 474 */     if (this.modifiers != 0) {
/* 475 */       this.typeDeclaration.modifiers |= this.modifiers;
/* 476 */       if (this.modifiersStart < this.typeDeclaration.declarationSourceStart) {
/* 477 */         this.typeDeclaration.declarationSourceStart = this.modifiersStart;
/*     */       }
/*     */     }
/*     */ 
/* 481 */     if (this.annotationCount > 0) {
/* 482 */       int existingCount = this.typeDeclaration.annotations == null ? 0 : this.typeDeclaration.annotations.length;
/* 483 */       Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
/* 484 */       if (existingCount > 0) {
/* 485 */         System.arraycopy(this.typeDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
/*     */       }
/* 487 */       for (int i = 0; i < this.annotationCount; i++) {
/* 488 */         annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
/*     */       }
/* 490 */       this.typeDeclaration.annotations = annotationReferences;
/*     */ 
/* 492 */       int start = this.annotations[0].annotation.sourceStart;
/* 493 */       if (start < this.typeDeclaration.declarationSourceStart) {
/* 494 */         this.typeDeclaration.declarationSourceStart = start;
/*     */       }
/*     */     }
/*     */ 
/* 498 */     if (this.memberTypeCount > 0) {
/* 499 */       int existingCount = this.typeDeclaration.memberTypes == null ? 0 : this.typeDeclaration.memberTypes.length;
/* 500 */       TypeDeclaration[] memberTypeDeclarations = new TypeDeclaration[existingCount + this.memberTypeCount];
/* 501 */       if (existingCount > 0) {
/* 502 */         System.arraycopy(this.typeDeclaration.memberTypes, 0, memberTypeDeclarations, 0, existingCount);
/*     */       }
/*     */ 
/* 505 */       if (this.memberTypes[(this.memberTypeCount - 1)].typeDeclaration.declarationSourceEnd == 0) {
/* 506 */         int bodyEndValue = bodyEnd();
/* 507 */         this.memberTypes[(this.memberTypeCount - 1)].typeDeclaration.declarationSourceEnd = bodyEndValue;
/* 508 */         this.memberTypes[(this.memberTypeCount - 1)].typeDeclaration.bodyEnd = bodyEndValue;
/*     */       }
/*     */ 
/* 511 */       int updatedCount = 0;
/* 512 */       for (int i = 0; i < this.memberTypeCount; i++) {
/* 513 */         TypeDeclaration updatedTypeDeclaration = this.memberTypes[i].updatedTypeDeclaration(depth + 1, knownTypes);
/* 514 */         if (updatedTypeDeclaration != null) {
/* 515 */           memberTypeDeclarations[(existingCount + updatedCount++)] = updatedTypeDeclaration;
/*     */         }
/*     */       }
/* 518 */       if (updatedCount < this.memberTypeCount) {
/* 519 */         int length = existingCount + updatedCount;
/* 520 */         System.arraycopy(memberTypeDeclarations, 0, memberTypeDeclarations = new TypeDeclaration[length], 0, length);
/*     */       }
/*     */ 
/* 523 */       if (memberTypeDeclarations.length > 0) {
/* 524 */         this.typeDeclaration.memberTypes = memberTypeDeclarations;
/* 525 */         if (memberTypeDeclarations[(memberTypeDeclarations.length - 1)].declarationSourceEnd > lastEnd) {
/* 526 */           lastEnd = memberTypeDeclarations[(memberTypeDeclarations.length - 1)].declarationSourceEnd;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 531 */     if (this.fieldCount > 0) {
/* 532 */       int existingCount = this.typeDeclaration.fields == null ? 0 : this.typeDeclaration.fields.length;
/* 533 */       FieldDeclaration[] fieldDeclarations = new FieldDeclaration[existingCount + this.fieldCount];
/* 534 */       if (existingCount > 0) {
/* 535 */         System.arraycopy(this.typeDeclaration.fields, 0, fieldDeclarations, 0, existingCount);
/*     */       }
/*     */ 
/* 538 */       if (this.fields[(this.fieldCount - 1)].fieldDeclaration.declarationSourceEnd == 0) {
/* 539 */         int temp = bodyEnd();
/* 540 */         this.fields[(this.fieldCount - 1)].fieldDeclaration.declarationSourceEnd = temp;
/* 541 */         this.fields[(this.fieldCount - 1)].fieldDeclaration.declarationEnd = temp;
/*     */       }
/* 543 */       for (int i = 0; i < this.fieldCount; i++) {
/* 544 */         fieldDeclarations[(existingCount + i)] = this.fields[i].updatedFieldDeclaration(depth, knownTypes);
/*     */       }
/*     */ 
/* 547 */       for (int i = this.fieldCount - 1; i > 0; i--) {
/* 548 */         if (fieldDeclarations[(existingCount + i - 1)].declarationSourceStart == fieldDeclarations[(existingCount + i)].declarationSourceStart) {
/* 549 */           fieldDeclarations[(existingCount + i - 1)].declarationSourceEnd = fieldDeclarations[(existingCount + i)].declarationSourceEnd;
/* 550 */           fieldDeclarations[(existingCount + i - 1)].declarationEnd = fieldDeclarations[(existingCount + i)].declarationEnd;
/*     */         }
/*     */       }
/*     */ 
/* 554 */       this.typeDeclaration.fields = fieldDeclarations;
/* 555 */       if (fieldDeclarations[(fieldDeclarations.length - 1)].declarationSourceEnd > lastEnd) {
/* 556 */         lastEnd = fieldDeclarations[(fieldDeclarations.length - 1)].declarationSourceEnd;
/*     */       }
/*     */     }
/*     */ 
/* 560 */     int existingCount = this.typeDeclaration.methods == null ? 0 : this.typeDeclaration.methods.length;
/* 561 */     boolean hasConstructor = false; boolean hasRecoveredConstructor = false;
/* 562 */     boolean hasAbstractMethods = false;
/* 563 */     int defaultConstructorIndex = -1;
/* 564 */     if (this.methodCount > 0) {
/* 565 */       AbstractMethodDeclaration[] methodDeclarations = new AbstractMethodDeclaration[existingCount + this.methodCount];
/* 566 */       for (int i = 0; i < existingCount; i++) {
/* 567 */         AbstractMethodDeclaration m = this.typeDeclaration.methods[i];
/* 568 */         if (m.isDefaultConstructor()) defaultConstructorIndex = i;
/* 569 */         if (m.isAbstract()) hasAbstractMethods = true;
/* 570 */         methodDeclarations[i] = m;
/*     */       }
/*     */ 
/* 573 */       if (this.methods[(this.methodCount - 1)].methodDeclaration.declarationSourceEnd == 0) {
/* 574 */         int bodyEndValue = bodyEnd();
/* 575 */         this.methods[(this.methodCount - 1)].methodDeclaration.declarationSourceEnd = bodyEndValue;
/* 576 */         this.methods[(this.methodCount - 1)].methodDeclaration.bodyEnd = bodyEndValue;
/*     */       }
/* 578 */       for (int i = 0; i < this.methodCount; i++) {
/* 579 */         AbstractMethodDeclaration updatedMethod = this.methods[i].updatedMethodDeclaration(depth, knownTypes);
/* 580 */         if (updatedMethod.isConstructor()) hasRecoveredConstructor = true;
/* 581 */         if (updatedMethod.isAbstract()) hasAbstractMethods = true;
/* 582 */         methodDeclarations[(existingCount + i)] = updatedMethod;
/*     */       }
/* 584 */       this.typeDeclaration.methods = methodDeclarations;
/* 585 */       if (methodDeclarations[(methodDeclarations.length - 1)].declarationSourceEnd > lastEnd) {
/* 586 */         lastEnd = methodDeclarations[(methodDeclarations.length - 1)].declarationSourceEnd;
/*     */       }
/* 588 */       if (hasAbstractMethods) this.typeDeclaration.bits |= 2048;
/* 589 */       hasConstructor = this.typeDeclaration.checkConstructors(parser());
/*     */     } else {
/* 591 */       for (int i = 0; i < existingCount; i++) {
/* 592 */         if (!this.typeDeclaration.methods[i].isConstructor()) continue; hasConstructor = true;
/*     */       }
/*     */     }
/*     */ 
/* 596 */     if (this.typeDeclaration.needClassInitMethod()) {
/* 597 */       boolean alreadyHasClinit = false;
/* 598 */       for (int i = 0; i < existingCount; i++) {
/* 599 */         if (this.typeDeclaration.methods[i].isClinit()) {
/* 600 */           alreadyHasClinit = true;
/* 601 */           break;
/*     */         }
/*     */       }
/* 604 */       if (!alreadyHasClinit) this.typeDeclaration.addClinit();
/*     */     }
/*     */ 
/* 607 */     if ((defaultConstructorIndex >= 0) && (hasRecoveredConstructor))
/*     */     {
/* 609 */       AbstractMethodDeclaration[] methodDeclarations = new AbstractMethodDeclaration[this.typeDeclaration.methods.length - 1];
/* 610 */       if (defaultConstructorIndex != 0) {
/* 611 */         System.arraycopy(this.typeDeclaration.methods, 0, methodDeclarations, 0, defaultConstructorIndex);
/*     */       }
/* 613 */       if (defaultConstructorIndex != this.typeDeclaration.methods.length - 1) {
/* 614 */         System.arraycopy(
/* 615 */           this.typeDeclaration.methods, 
/* 616 */           defaultConstructorIndex + 1, 
/* 617 */           methodDeclarations, 
/* 618 */           defaultConstructorIndex, 
/* 619 */           this.typeDeclaration.methods.length - defaultConstructorIndex - 1);
/*     */       }
/* 621 */       this.typeDeclaration.methods = methodDeclarations;
/*     */     } else {
/* 623 */       int kind = TypeDeclaration.kind(this.typeDeclaration.modifiers);
/* 624 */       if ((!hasConstructor) && 
/* 625 */         (kind != 2) && 
/* 626 */         (kind != 4) && 
/* 627 */         (this.typeDeclaration.allocation == null)) {
/* 628 */         boolean insideFieldInitializer = false;
/* 629 */         RecoveredElement parentElement = this.parent;
/* 630 */         while (parentElement != null) {
/* 631 */           if ((parentElement instanceof RecoveredField)) {
/* 632 */             insideFieldInitializer = true;
/* 633 */             break;
/*     */           }
/* 635 */           parentElement = parentElement.parent;
/*     */         }
/* 637 */         this.typeDeclaration.createDefaultConstructor((!parser().diet) || (insideFieldInitializer), true);
/*     */       }
/*     */     }
/* 640 */     if ((this.parent instanceof RecoveredType))
/* 641 */       this.typeDeclaration.bits |= 1024;
/* 642 */     else if ((this.parent instanceof RecoveredMethod)) {
/* 643 */       this.typeDeclaration.bits |= 256;
/*     */     }
/* 645 */     if (this.typeDeclaration.declarationSourceEnd == 0) {
/* 646 */       this.typeDeclaration.declarationSourceEnd = lastEnd;
/* 647 */       this.typeDeclaration.bodyEnd = lastEnd;
/*     */     }
/* 649 */     return this.typeDeclaration;
/*     */   }
/*     */ 
/*     */   public void updateFromParserState()
/*     */   {
/* 658 */     if ((bodyStartsAtHeaderEnd()) && (this.typeDeclaration.allocation == null)) {
/* 659 */       Parser parser = parser();
/*     */ 
/* 662 */       if ((parser.listLength > 0) && (parser.astLengthPtr > 0)) {
/* 663 */         int length = parser.astLengthStack[parser.astLengthPtr];
/* 664 */         int astPtr = parser.astPtr - length;
/* 665 */         boolean canConsume = astPtr >= 0;
/* 666 */         if (canConsume) {
/* 667 */           if (!(parser.astStack[astPtr] instanceof TypeDeclaration)) {
/* 668 */             canConsume = false;
/*     */           }
/* 670 */           int i = 1; for (int max = length + 1; i < max; i++) {
/* 671 */             if (!(parser.astStack[(astPtr + i)] instanceof TypeReference)) {
/* 672 */               canConsume = false;
/*     */             }
/*     */           }
/*     */         }
/* 676 */         if (canConsume) {
/* 677 */           parser.consumeClassHeaderImplements();
/*     */         }
/*     */ 
/*     */       }
/* 681 */       else if (parser.listTypeParameterLength > 0) {
/* 682 */         int length = parser.listTypeParameterLength;
/* 683 */         int genericsPtr = parser.genericsPtr;
/* 684 */         boolean canConsume = (genericsPtr + 1 >= length) && (parser.astPtr > -1);
/* 685 */         if (canConsume) {
/* 686 */           if (!(parser.astStack[parser.astPtr] instanceof TypeDeclaration)) {
/* 687 */             canConsume = false;
/*     */           }
/* 689 */           while ((genericsPtr + 1 > length) && (!(parser.genericsStack[genericsPtr] instanceof TypeParameter))) {
/* 690 */             genericsPtr--;
/*     */           }
/* 692 */           for (int i = 0; i < length; i++) {
/* 693 */             if (!(parser.genericsStack[(genericsPtr - i)] instanceof TypeParameter)) {
/* 694 */               canConsume = false;
/*     */             }
/*     */           }
/*     */         }
/* 698 */         if (canConsume) {
/* 699 */           TypeDeclaration typeDecl = (TypeDeclaration)parser.astStack[parser.astPtr];
/* 700 */           System.arraycopy(parser.genericsStack, genericsPtr - length + 1, typeDecl.typeParameters = new TypeParameter[length], 0, length);
/* 701 */           typeDecl.bodyStart = (typeDecl.typeParameters[(length - 1)].declarationSourceEnd + 1);
/* 702 */           parser.listTypeParameterLength = 0;
/* 703 */           parser.lastCheckPoint = typeDecl.bodyStart;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd)
/*     */   {
/* 713 */     if ((--this.bracketBalance <= 0) && (this.parent != null)) {
/* 714 */       updateSourceEndIfNecessary(braceStart, braceEnd);
/* 715 */       this.bodyEnd = (braceStart - 1);
/* 716 */       return this.parent;
/*     */     }
/* 718 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd)
/*     */   {
/* 726 */     if (this.bracketBalance == 0)
/*     */     {
/* 731 */       Parser parser = parser();
/* 732 */       switch (parser.lastIgnoredToken) {
/*     */       case -1:
/*     */       case 10:
/*     */       case 11:
/*     */       case 13:
/*     */       case 99:
/*     */       case 106:
/* 739 */         if (parser.recoveredStaticInitializerStart == 0)
/*     */           break;
/*     */       default:
/* 742 */         this.foundOpeningBrace = true;
/* 743 */         this.bracketBalance = 1;
/*     */       }
/*     */     }
/*     */ 
/* 747 */     if (this.bracketBalance == 1) {
/* 748 */       Block block = new Block(0);
/* 749 */       Parser parser = parser();
/* 750 */       block.sourceStart = parser.scanner.startPosition;
/*     */       Initializer init;
/*     */       Initializer init;
/* 752 */       if (parser.recoveredStaticInitializerStart == 0) {
/* 753 */         init = new Initializer(block, 0);
/*     */       } else {
/* 755 */         init = new Initializer(block, 8);
/* 756 */         init.declarationSourceStart = parser.recoveredStaticInitializerStart;
/*     */       }
/* 758 */       init.bodyStart = parser.scanner.currentPosition;
/* 759 */       return add(init, 1);
/*     */     }
/* 761 */     return super.updateOnOpeningBrace(braceStart, braceEnd);
/*     */   }
/*     */   public void updateParseTree() {
/* 764 */     updatedTypeDeclaration(0, new HashSet());
/*     */   }
/*     */ 
/*     */   public void updateSourceEndIfNecessary(int start, int end)
/*     */   {
/* 770 */     if (this.typeDeclaration.declarationSourceEnd == 0) {
/* 771 */       this.bodyEnd = 0;
/* 772 */       this.typeDeclaration.declarationSourceEnd = end;
/* 773 */       this.typeDeclaration.bodyEnd = end;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredType
 * JD-Core Version:    0.6.0
 */