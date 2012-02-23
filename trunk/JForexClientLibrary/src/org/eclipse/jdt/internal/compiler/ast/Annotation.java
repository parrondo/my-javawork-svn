/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.IrritantSet;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public abstract class Annotation extends Expression
/*     */ {
/*  26 */   static final MemberValuePair[] NoValuePairs = new MemberValuePair[0];
/*     */   public int declarationSourceEnd;
/*     */   public Binding recipient;
/*     */   public TypeReference type;
/*  34 */   private AnnotationBinding compilerAnnotation = null;
/*     */ 
/*     */   public static long getRetentionPolicy(char[] policyName) {
/*  37 */     if ((policyName == null) || (policyName.length == 0))
/*  38 */       return 0L;
/*  39 */     switch (policyName[0]) {
/*     */     case 'C':
/*  41 */       if (!CharOperation.equals(policyName, TypeConstants.UPPER_CLASS)) break;
/*  42 */       return 35184372088832L;
/*     */     case 'S':
/*  45 */       if (!CharOperation.equals(policyName, TypeConstants.UPPER_SOURCE)) break;
/*  46 */       return 17592186044416L;
/*     */     case 'R':
/*  49 */       if (!CharOperation.equals(policyName, TypeConstants.UPPER_RUNTIME)) break;
/*  50 */       return 52776558133248L;
/*     */     }
/*     */ 
/*  53 */     return 0L;
/*     */   }
/*     */ 
/*     */   public static long getTargetElementType(char[] elementName) {
/*  57 */     if ((elementName == null) || (elementName.length == 0))
/*  58 */       return 0L;
/*  59 */     switch (elementName[0]) {
/*     */     case 'A':
/*  61 */       if (!CharOperation.equals(elementName, TypeConstants.UPPER_ANNOTATION_TYPE)) break;
/*  62 */       return 4398046511104L;
/*     */     case 'C':
/*  65 */       if (!CharOperation.equals(elementName, TypeConstants.UPPER_CONSTRUCTOR)) break;
/*  66 */       return 1099511627776L;
/*     */     case 'F':
/*  69 */       if (!CharOperation.equals(elementName, TypeConstants.UPPER_FIELD)) break;
/*  70 */       return 137438953472L;
/*     */     case 'L':
/*  73 */       if (!CharOperation.equals(elementName, TypeConstants.UPPER_LOCAL_VARIABLE)) break;
/*  74 */       return 2199023255552L;
/*     */     case 'M':
/*  77 */       if (!CharOperation.equals(elementName, TypeConstants.UPPER_METHOD)) break;
/*  78 */       return 274877906944L;
/*     */     case 'P':
/*  81 */       if (CharOperation.equals(elementName, TypeConstants.UPPER_PARAMETER))
/*  82 */         return 549755813888L;
/*  83 */       if (!CharOperation.equals(elementName, TypeConstants.UPPER_PACKAGE)) break;
/*  84 */       return 8796093022208L;
/*     */     case 'T':
/*  87 */       if (!CharOperation.equals(elementName, TypeConstants.TYPE)) break;
/*  88 */       return 68719476736L;
/*     */     }
/*     */ 
/*  91 */     return 0L;
/*     */   }
/*     */ 
/*     */   public ElementValuePair[] computeElementValuePairs() {
/*  95 */     return Binding.NO_ELEMENT_VALUE_PAIRS;
/*     */   }
/*     */ 
/*     */   private long detectStandardAnnotation(Scope scope, ReferenceBinding annotationType, MemberValuePair valueAttribute)
/*     */   {
/* 102 */     long tagBits = 0L;
/* 103 */     switch (annotationType.id)
/*     */     {
/*     */     case 48:
/* 106 */       if (valueAttribute == null) break;
/* 107 */       Expression expr = valueAttribute.value;
/* 108 */       if ((expr.bits & 0x3) != 1) break;
/* 109 */       FieldBinding field = ((Reference)expr).fieldBinding();
/* 110 */       if ((field == null) || (field.declaringClass.id != 51)) break;
/* 111 */       tagBits |= getRetentionPolicy(field.name);
/*     */ 
/* 115 */       break;
/*     */     case 50:
/* 118 */       tagBits |= 34359738368L;
/* 119 */       if (valueAttribute == null) break;
/* 120 */       Expression expr = valueAttribute.value;
/* 121 */       if ((expr instanceof ArrayInitializer)) {
/* 122 */         ArrayInitializer initializer = (ArrayInitializer)expr;
/* 123 */         Expression[] expressions = initializer.expressions;
/* 124 */         if (expressions == null) break;
/* 125 */         int i = 0; for (int length = expressions.length; i < length; i++) {
/* 126 */           Expression initExpr = expressions[i];
/* 127 */           if ((initExpr.bits & 0x3) == 1) {
/* 128 */             FieldBinding field = ((Reference)initExpr).fieldBinding();
/* 129 */             if ((field != null) && (field.declaringClass.id == 52)) {
/* 130 */               long element = getTargetElementType(field.name);
/* 131 */               if ((tagBits & element) != 0L)
/* 132 */                 scope.problemReporter().duplicateTargetInTargetAnnotation(annotationType, (NameReference)initExpr);
/*     */               else
/* 134 */                 tagBits |= element;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 140 */         if ((expr.bits & 0x3) != 1) break;
/* 141 */         FieldBinding field = ((Reference)expr).fieldBinding();
/* 142 */         if ((field == null) || (field.declaringClass.id != 52)) break;
/* 143 */         tagBits |= getTargetElementType(field.name);
/*     */       }
/*     */ 
/* 147 */       break;
/*     */     case 44:
/* 150 */       tagBits |= 70368744177664L;
/* 151 */       break;
/*     */     case 45:
/* 153 */       tagBits |= 140737488355328L;
/* 154 */       break;
/*     */     case 46:
/* 156 */       tagBits |= 281474976710656L;
/* 157 */       break;
/*     */     case 47:
/* 159 */       tagBits |= 562949953421312L;
/* 160 */       break;
/*     */     case 49:
/* 162 */       tagBits |= 1125899906842624L;
/*     */     }
/*     */ 
/* 165 */     return tagBits;
/*     */   }
/*     */ 
/*     */   public AnnotationBinding getCompilerAnnotation() {
/* 169 */     return this.compilerAnnotation;
/*     */   }
/*     */   public abstract MemberValuePair[] memberValuePairs();
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 175 */     output.append('@');
/* 176 */     this.type.printExpression(0, output);
/* 177 */     return output;
/*     */   }
/*     */ 
/*     */   public void recordSuppressWarnings(Scope scope, int startSuppresss, int endSuppress, boolean isSuppressingWarnings) {
/* 181 */     IrritantSet suppressWarningIrritants = null;
/* 182 */     MemberValuePair[] pairs = memberValuePairs();
/* 183 */     int i = 0; for (int length = pairs.length; i < length; i++) {
/* 184 */       MemberValuePair pair = pairs[i];
/* 185 */       if (CharOperation.equals(pair.name, TypeConstants.VALUE)) {
/* 186 */         Expression value = pair.value;
/* 187 */         if ((value instanceof ArrayInitializer)) {
/* 188 */           ArrayInitializer initializer = (ArrayInitializer)value;
/* 189 */           Expression[] inits = initializer.expressions;
/* 190 */           if (inits == null) break;
/* 191 */           int j = 0; for (int initsLength = inits.length; j < initsLength; j++) {
/* 192 */             Constant cst = inits[j].constant;
/* 193 */             if ((cst != Constant.NotAConstant) && (cst.typeID() == 11)) {
/* 194 */               IrritantSet irritants = CompilerOptions.warningTokenToIrritants(cst.stringValue());
/* 195 */               if (irritants != null) {
/* 196 */                 if (suppressWarningIrritants == null)
/* 197 */                   suppressWarningIrritants = new IrritantSet(irritants);
/* 198 */                 else if (suppressWarningIrritants.set(irritants) == null)
/* 199 */                   scope.problemReporter().unusedWarningToken(inits[j]);
/*     */               }
/*     */               else
/* 202 */                 scope.problemReporter().unhandledWarningToken(inits[j]);
/*     */             }
/*     */           }
/* 191 */           break;
/*     */         }
/*     */ 
/* 208 */         Constant cst = value.constant;
/* 209 */         if ((cst == Constant.NotAConstant) || (cst.typeID() != 11)) break;
/* 210 */         IrritantSet irritants = CompilerOptions.warningTokenToIrritants(cst.stringValue());
/* 211 */         if (irritants != null) {
/* 212 */           suppressWarningIrritants = new IrritantSet(irritants); break;
/*     */         }
/*     */ 
/* 215 */         scope.problemReporter().unhandledWarningToken(value);
/*     */ 
/* 219 */         break;
/*     */       }
/*     */     }
/* 222 */     if ((isSuppressingWarnings) && (suppressWarningIrritants != null))
/* 223 */       scope.referenceCompilationUnit().recordSuppressWarnings(suppressWarningIrritants, this, startSuppresss, endSuppress);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 229 */     if (this.compilerAnnotation != null)
/* 230 */       return this.resolvedType;
/* 231 */     this.constant = Constant.NotAConstant;
/*     */ 
/* 233 */     TypeBinding typeBinding = this.type.resolveType(scope);
/* 234 */     if (typeBinding == null) {
/* 235 */       return null;
/*     */     }
/* 237 */     this.resolvedType = typeBinding;
/*     */ 
/* 239 */     if ((!typeBinding.isAnnotationType()) && (typeBinding.isValidBinding())) {
/* 240 */       scope.problemReporter().typeMismatchError(typeBinding, scope.getJavaLangAnnotationAnnotation(), this.type, null);
/* 241 */       return null;
/*     */     }
/*     */ 
/* 244 */     ReferenceBinding annotationType = (ReferenceBinding)this.resolvedType;
/* 245 */     MethodBinding[] methods = annotationType.methods();
/*     */ 
/* 247 */     MemberValuePair[] originalValuePairs = memberValuePairs();
/* 248 */     MemberValuePair valueAttribute = null;
/*     */ 
/* 250 */     int pairsLength = originalValuePairs.length;
/*     */     MemberValuePair[] pairs;
/* 251 */     if (pairsLength > 0)
/*     */     {
/*     */       MemberValuePair[] pairs;
/* 252 */       System.arraycopy(originalValuePairs, 0, pairs = new MemberValuePair[pairsLength], 0, pairsLength);
/*     */     } else {
/* 254 */       pairs = originalValuePairs;
/*     */     }
/*     */ 
/* 257 */     int i = 0; for (int requiredLength = methods.length; i < requiredLength; i++) {
/* 258 */       MethodBinding method = methods[i];
/* 259 */       char[] selector = method.selector;
/* 260 */       boolean foundValue = false;
/* 261 */       int j = 0;
/*     */       while (true) { MemberValuePair pair = pairs[j];
/* 263 */         if (pair != null) {
/* 264 */           char[] name = pair.name;
/* 265 */           if (CharOperation.equals(name, selector)) {
/* 266 */             if ((valueAttribute == null) && (CharOperation.equals(name, TypeConstants.VALUE))) {
/* 267 */               valueAttribute = pair;
/*     */             }
/* 269 */             pair.binding = method;
/* 270 */             pair.resolveTypeExpecting(scope, method.returnType);
/* 271 */             pairs[j] = null;
/* 272 */             foundValue = true;
/*     */ 
/* 275 */             boolean foundDuplicate = false;
/* 276 */             for (int k = j + 1; k < pairsLength; k++) {
/* 277 */               MemberValuePair otherPair = pairs[k];
/* 278 */               if ((otherPair == null) || 
/* 279 */                 (!CharOperation.equals(otherPair.name, selector))) continue;
/* 280 */               foundDuplicate = true;
/* 281 */               scope.problemReporter().duplicateAnnotationValue(annotationType, otherPair);
/* 282 */               otherPair.binding = method;
/* 283 */               otherPair.resolveTypeExpecting(scope, method.returnType);
/* 284 */               pairs[k] = null;
/*     */             }
/*     */ 
/* 287 */             if (foundDuplicate) {
/* 288 */               scope.problemReporter().duplicateAnnotationValue(annotationType, pair);
/* 289 */               break;
/*     */             }
/*     */           }
/*     */         }
/* 261 */         j++; if (j < pairsLength)
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 293 */         if ((foundValue) || 
/* 294 */           ((method.modifiers & 0x20000) != 0) || 
/* 295 */           ((this.bits & 0x20) != 0)) break;
/* 296 */         scope.problemReporter().missingValueForAnnotationMember(this, selector);
/*     */       }
/*     */     }
/*     */ 
/* 300 */     for (int i = 0; i < pairsLength; i++) {
/* 301 */       if (pairs[i] != null) {
/* 302 */         scope.problemReporter().undefinedAnnotationValue(annotationType, pairs[i]);
/* 303 */         pairs[i].resolveTypeExpecting(scope, null);
/*     */       }
/*     */     }
/*     */ 
/* 307 */     this.compilerAnnotation = scope.environment().createAnnotation((ReferenceBinding)this.resolvedType, computeElementValuePairs());
/*     */ 
/* 309 */     long tagBits = detectStandardAnnotation(scope, annotationType, valueAttribute);
/*     */ 
/* 312 */     scope.referenceCompilationUnit().recordSuppressWarnings(IrritantSet.NLS, null, this.sourceStart, this.declarationSourceEnd);
/* 313 */     if (this.recipient != null) {
/* 314 */       if (tagBits != 0L)
/*     */       {
/* 316 */         switch (this.recipient.kind()) {
/*     */         case 16:
/* 318 */           ((PackageBinding)this.recipient).tagBits |= tagBits;
/* 319 */           break;
/*     */         case 4:
/*     */         case 2052:
/* 322 */           SourceTypeBinding sourceType = (SourceTypeBinding)this.recipient;
/* 323 */           sourceType.tagBits |= tagBits;
/* 324 */           if ((tagBits & 0x0) == 0L) break;
/* 325 */           TypeDeclaration typeDeclaration = sourceType.scope.referenceContext;
/*     */           int start;
/*     */           int start;
/* 327 */           if (scope.referenceCompilationUnit().types[0] == typeDeclaration)
/* 328 */             start = 0;
/*     */           else {
/* 330 */             start = typeDeclaration.declarationSourceStart;
/*     */           }
/* 332 */           recordSuppressWarnings(scope, start, typeDeclaration.declarationSourceEnd, scope.compilerOptions().suppressWarnings);
/*     */ 
/* 334 */           break;
/*     */         case 8:
/* 336 */           MethodBinding sourceMethod = (MethodBinding)this.recipient;
/* 337 */           sourceMethod.tagBits |= tagBits;
/* 338 */           if ((tagBits & 0x0) == 0L) break;
/* 339 */           SourceTypeBinding sourceType = (SourceTypeBinding)sourceMethod.declaringClass;
/* 340 */           AbstractMethodDeclaration methodDeclaration = sourceType.scope.referenceContext.declarationOf(sourceMethod);
/* 341 */           recordSuppressWarnings(scope, methodDeclaration.declarationSourceStart, methodDeclaration.declarationSourceEnd, scope.compilerOptions().suppressWarnings);
/*     */ 
/* 343 */           break;
/*     */         case 1:
/* 345 */           FieldBinding sourceField = (FieldBinding)this.recipient;
/* 346 */           sourceField.tagBits |= tagBits;
/* 347 */           if ((tagBits & 0x0) == 0L) break;
/* 348 */           SourceTypeBinding sourceType = (SourceTypeBinding)sourceField.declaringClass;
/* 349 */           FieldDeclaration fieldDeclaration = sourceType.scope.referenceContext.declarationOf(sourceField);
/* 350 */           recordSuppressWarnings(scope, fieldDeclaration.declarationSourceStart, fieldDeclaration.declarationSourceEnd, scope.compilerOptions().suppressWarnings);
/*     */ 
/* 352 */           break;
/*     */         case 2:
/* 354 */           LocalVariableBinding variable = (LocalVariableBinding)this.recipient;
/* 355 */           variable.tagBits |= tagBits;
/* 356 */           if ((tagBits & 0x0) == 0L) break;
/* 357 */           LocalDeclaration localDeclaration = variable.declaration;
/* 358 */           recordSuppressWarnings(scope, localDeclaration.declarationSourceStart, localDeclaration.declarationSourceEnd, scope.compilerOptions().suppressWarnings);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 365 */       long metaTagBits = annotationType.getAnnotationTagBits();
/* 366 */       if ((metaTagBits & 0x0) != 0L)
/*     */       {
/* 369 */         switch (this.recipient.kind()) {
/*     */         case 16:
/* 371 */           if ((metaTagBits & 0x0) == 0L) break;
/* 372 */           break;
/*     */         case 4:
/*     */         case 2052:
/* 376 */           if (((ReferenceBinding)this.recipient).isAnnotationType()) {
/* 377 */             if ((metaTagBits & 0x0) == 0L) break;
/* 378 */             break label1200; } else {
/* 379 */             if ((metaTagBits & 0x0) != 0L) break label1200;
/* 381 */             if (((metaTagBits & 0x0) == 0L) || 
/* 382 */               (!CharOperation.equals(((ReferenceBinding)this.recipient).sourceName, TypeConstants.PACKAGE_INFO_NAME))) break; 
/*     */           }
/* 383 */           break;
/*     */         case 8:
/* 387 */           if (((MethodBinding)this.recipient).isConstructor()) {
/* 388 */             if ((metaTagBits & 0x0) == 0L) break;
/* 389 */             break label1200; } else {
/* 390 */             if ((metaTagBits & 0x0) == 0L) break; 
/*     */           }
/* 391 */           break;
/*     */         case 1:
/* 394 */           if ((metaTagBits & 0x0) == 0L) break;
/* 395 */           break;
/*     */         case 2:
/* 398 */           if ((((LocalVariableBinding)this.recipient).tagBits & 0x400) != 0L ? 
/* 399 */             (metaTagBits & 0x0) != 0L : 
/* 401 */             (annotationType.tagBits & 0x0) != 0L)
/*     */             break label1200;
/*     */         }
/* 405 */         scope.problemReporter().disallowedTargetForAnnotation(this);
/*     */       }
/*     */     }
/* 408 */     label1200: return this.resolvedType;
/*     */   }
/*     */ 
/*     */   public abstract void traverse(ASTVisitor paramASTVisitor, BlockScope paramBlockScope);
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Annotation
 * JD-Core Version:    0.6.0
 */