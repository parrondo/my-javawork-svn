/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import javax.lang.model.element.AnnotationMirror;
/*     */ import javax.lang.model.element.AnnotationValue;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.tools.Diagnostic.Kind;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.AnnotationMemberValue;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.AnnotationMirrorImpl;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.ExecutableElementImpl;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.VariableElementImpl;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class BaseMessagerImpl
/*     */ {
/*  44 */   static final String[] NO_ARGUMENTS = new String[0];
/*     */ 
/*     */   public static AptProblem createProblem(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v)
/*     */   {
/*  56 */     ReferenceContext referenceContext = null;
/*  57 */     Annotation[] elementAnnotations = (Annotation[])null;
/*  58 */     int startPosition = 0;
/*  59 */     int endPosition = 0;
/*  60 */     if (e != null) {
/*  61 */       switch ($SWITCH_TABLE$javax$lang$model$element$ElementKind()[e.getKind().ordinal()]) {
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/*  66 */         TypeElementImpl typeElementImpl = (TypeElementImpl)e;
/*  67 */         Binding typeBinding = typeElementImpl._binding;
/*  68 */         if (!(typeBinding instanceof SourceTypeBinding)) break;
/*  69 */         SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)typeBinding;
/*  70 */         TypeDeclaration typeDeclaration = (TypeDeclaration)sourceTypeBinding.scope.referenceContext();
/*  71 */         referenceContext = typeDeclaration;
/*  72 */         elementAnnotations = typeDeclaration.annotations;
/*  73 */         startPosition = typeDeclaration.sourceStart;
/*  74 */         endPosition = typeDeclaration.sourceEnd;
/*     */ 
/*  76 */         break;
/*     */       case 1:
/*  79 */         break;
/*     */       case 11:
/*     */       case 12:
/*  82 */         ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)e;
/*  83 */         Binding binding = executableElementImpl._binding;
/*  84 */         if (!(binding instanceof MethodBinding)) break;
/*  85 */         MethodBinding methodBinding = (MethodBinding)binding;
/*  86 */         AbstractMethodDeclaration sourceMethod = methodBinding.sourceMethod();
/*  87 */         if (sourceMethod == null) break;
/*  88 */         referenceContext = sourceMethod;
/*  89 */         elementAnnotations = sourceMethod.annotations;
/*  90 */         startPosition = sourceMethod.sourceStart;
/*  91 */         endPosition = sourceMethod.sourceEnd;
/*     */ 
/*  94 */         break;
/*     */       case 6:
/*  96 */         break;
/*     */       case 10:
/*  98 */         break;
/*     */       case 7:
/*     */       case 8:
/* 101 */         VariableElementImpl variableElementImpl = (VariableElementImpl)e;
/* 102 */         Binding binding = variableElementImpl._binding;
/* 103 */         if ((binding instanceof FieldBinding)) {
/* 104 */           FieldBinding fieldBinding = (FieldBinding)binding;
/* 105 */           FieldDeclaration fieldDeclaration = fieldBinding.sourceField();
/* 106 */           if (fieldDeclaration == null) break;
/* 107 */           ReferenceBinding declaringClass = fieldBinding.declaringClass;
/* 108 */           if ((declaringClass instanceof SourceTypeBinding)) {
/* 109 */             SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)declaringClass;
/* 110 */             TypeDeclaration typeDeclaration = (TypeDeclaration)sourceTypeBinding.scope.referenceContext();
/* 111 */             referenceContext = typeDeclaration;
/*     */           }
/* 113 */           elementAnnotations = fieldDeclaration.annotations;
/* 114 */           startPosition = fieldDeclaration.sourceStart;
/* 115 */           endPosition = fieldDeclaration.sourceEnd;
/*     */         } else {
/* 117 */           if (!(binding instanceof AptSourceLocalVariableBinding)) break;
/* 118 */           AptSourceLocalVariableBinding parameterBinding = (AptSourceLocalVariableBinding)binding;
/* 119 */           LocalDeclaration parameterDeclaration = parameterBinding.declaration;
/* 120 */           if (parameterDeclaration == null) break;
/* 121 */           MethodBinding methodBinding = parameterBinding.methodBinding;
/* 122 */           if (methodBinding != null) {
/* 123 */             referenceContext = methodBinding.sourceMethod();
/*     */           }
/* 125 */           elementAnnotations = parameterDeclaration.annotations;
/* 126 */           startPosition = parameterDeclaration.sourceStart;
/* 127 */           endPosition = parameterDeclaration.sourceEnd;
/*     */         }
/*     */ 
/* 130 */         break;
/*     */       case 13:
/*     */       case 14:
/* 133 */         break;
/*     */       case 9:
/*     */       case 15:
/*     */       }
/*     */     }
/*     */ 
/* 139 */     StringBuilder builder = new StringBuilder();
/* 140 */     if (msg != null) {
/* 141 */       builder.append(msg);
/*     */     }
/* 143 */     if ((a != null) && (elementAnnotations != null)) {
/* 144 */       AnnotationBinding annotationBinding = ((AnnotationMirrorImpl)a)._binding;
/* 145 */       Annotation annotation = null;
/* 146 */       for (int i = 0; (annotation == null) && (i < elementAnnotations.length); i++) {
/* 147 */         if (annotationBinding == elementAnnotations[i].getCompilerAnnotation()) {
/* 148 */           annotation = elementAnnotations[i];
/*     */         }
/*     */       }
/* 151 */       if (annotation != null) {
/* 152 */         startPosition = annotation.sourceStart;
/* 153 */         endPosition = annotation.sourceEnd;
/* 154 */         if ((v != null) && ((v instanceof AnnotationMemberValue))) {
/* 155 */           MethodBinding methodBinding = ((AnnotationMemberValue)v).getMethodBinding();
/* 156 */           MemberValuePair[] memberValuePairs = annotation.memberValuePairs();
/* 157 */           MemberValuePair memberValuePair = null;
/* 158 */           for (int i = 0; (memberValuePair == null) && (i < memberValuePairs.length); i++) {
/* 159 */             if (methodBinding == memberValuePairs[i].binding) {
/* 160 */               memberValuePair = memberValuePairs[i];
/*     */             }
/*     */           }
/* 163 */           if (memberValuePair != null) {
/* 164 */             startPosition = memberValuePair.sourceStart;
/* 165 */             endPosition = memberValuePair.sourceEnd;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 170 */     int lineNumber = 0;
/* 171 */     int columnNumber = 1;
/* 172 */     char[] fileName = (char[])null;
/* 173 */     if (referenceContext != null) {
/* 174 */       CompilationResult result = referenceContext.compilationResult();
/* 175 */       fileName = result.fileName;
/* 176 */       int[] lineEnds = (int[])null;
/* 177 */       lineNumber = startPosition >= 0 ? 
/* 178 */         Util.getLineNumber(startPosition, lineEnds = result.getLineSeparatorPositions(), 0, lineEnds.length - 1) : 
/* 179 */         0;
/* 180 */       columnNumber = startPosition >= 0 ? 
/* 181 */         Util.searchColumnNumber(result.getLineSeparatorPositions(), lineNumber, startPosition) : 
/* 182 */         0;
/*     */     }
/*     */     int severity;
/*     */     int severity;
/* 185 */     switch (kind) {
/*     */     case ERROR:
/* 187 */       severity = 1;
/* 188 */       break;
/*     */     default:
/* 191 */       severity = 0;
/*     */     }
/*     */ 
/* 194 */     return new AptProblem(
/* 195 */       referenceContext, 
/* 196 */       fileName, 
/* 197 */       String.valueOf(builder), 
/* 198 */       0, 
/* 199 */       NO_ARGUMENTS, 
/* 200 */       severity, 
/* 201 */       startPosition, 
/* 202 */       endPosition, 
/* 203 */       lineNumber, 
/* 204 */       columnNumber);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.BaseMessagerImpl
 * JD-Core Version:    0.6.0
 */