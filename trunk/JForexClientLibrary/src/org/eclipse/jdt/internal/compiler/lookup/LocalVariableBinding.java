/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ 
/*     */ public class LocalVariableBinding extends VariableBinding
/*     */ {
/*     */   public int resolvedPosition;
/*     */   public static final int UNUSED = 0;
/*     */   public static final int USED = 1;
/*     */   public static final int FAKE_USED = 2;
/*     */   public int useFlag;
/*     */   public BlockScope declaringScope;
/*     */   public LocalDeclaration declaration;
/*     */   public int[] initializationPCs;
/*  35 */   public int initializationCount = 0;
/*     */ 
/*     */   public LocalVariableBinding(char[] name, TypeBinding type, int modifiers, boolean isArgument)
/*     */   {
/*  41 */     super(name, type, modifiers, isArgument ? Constant.NotAConstant : null);
/*  42 */     if (isArgument) this.tagBits |= 1024L;
/*     */   }
/*     */ 
/*     */   public LocalVariableBinding(LocalDeclaration declaration, TypeBinding type, int modifiers, boolean isArgument)
/*     */   {
/*  48 */     this(declaration.name, type, modifiers, isArgument);
/*  49 */     this.declaration = declaration;
/*     */   }
/*     */ 
/*     */   public final int kind()
/*     */   {
/*  56 */     return 2;
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf)
/*     */   {
/*  64 */     StringBuffer buffer = new StringBuffer();
/*     */ 
/*  67 */     BlockScope scope = this.declaringScope;
/*  68 */     int occurenceCount = 0;
/*  69 */     if (scope != null)
/*     */     {
/*  71 */       MethodScope methodScope = (scope instanceof MethodScope) ? (MethodScope)scope : scope.enclosingMethodScope();
/*  72 */       ReferenceContext referenceContext = methodScope.referenceContext;
/*  73 */       if ((referenceContext instanceof AbstractMethodDeclaration)) {
/*  74 */         MethodBinding methodBinding = ((AbstractMethodDeclaration)referenceContext).binding;
/*  75 */         if (methodBinding != null)
/*  76 */           buffer.append(methodBinding.computeUniqueKey(false));
/*     */       }
/*  78 */       else if ((referenceContext instanceof TypeDeclaration)) {
/*  79 */         TypeBinding typeBinding = ((TypeDeclaration)referenceContext).binding;
/*  80 */         if (typeBinding != null) {
/*  81 */           buffer.append(typeBinding.computeUniqueKey(false));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  86 */       getScopeKey(scope, buffer);
/*     */ 
/*  89 */       LocalVariableBinding[] locals = scope.locals;
/*  90 */       for (int i = 0; i < scope.localIndex; i++) {
/*  91 */         LocalVariableBinding local = locals[i];
/*  92 */         if (CharOperation.equals(this.name, local.name)) {
/*  93 */           if (this == local)
/*     */             break;
/*  95 */           occurenceCount++;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 100 */     buffer.append('#');
/* 101 */     buffer.append(this.name);
/*     */ 
/* 105 */     if (occurenceCount > 0) {
/* 106 */       buffer.append('#');
/* 107 */       buffer.append(occurenceCount);
/*     */     }
/*     */ 
/* 110 */     int length = buffer.length();
/* 111 */     char[] uniqueKey = new char[length];
/* 112 */     buffer.getChars(0, length, uniqueKey, 0);
/* 113 */     return uniqueKey;
/*     */   }
/*     */ 
/*     */   public AnnotationBinding[] getAnnotations() {
/* 117 */     if (this.declaringScope == null) {
/* 118 */       if ((this.tagBits & 0x0) != 0L)
/*     */       {
/* 120 */         if (this.declaration == null) {
/* 121 */           return Binding.NO_ANNOTATIONS;
/*     */         }
/* 123 */         Annotation[] annotations = this.declaration.annotations;
/* 124 */         if (annotations != null) {
/* 125 */           int length = annotations.length;
/* 126 */           AnnotationBinding[] annotationBindings = new AnnotationBinding[length];
/* 127 */           for (int i = 0; i < length; i++) {
/* 128 */             AnnotationBinding compilerAnnotation = annotations[i].getCompilerAnnotation();
/* 129 */             if (compilerAnnotation == null) {
/* 130 */               return Binding.NO_ANNOTATIONS;
/*     */             }
/* 132 */             annotationBindings[i] = compilerAnnotation;
/*     */           }
/* 134 */           return annotationBindings;
/*     */         }
/*     */       }
/* 137 */       return Binding.NO_ANNOTATIONS;
/*     */     }
/* 139 */     SourceTypeBinding sourceType = this.declaringScope.enclosingSourceType();
/* 140 */     if (sourceType == null) {
/* 141 */       return Binding.NO_ANNOTATIONS;
/*     */     }
/* 143 */     AnnotationBinding[] annotations = sourceType.retrieveAnnotations(this);
/* 144 */     if (((this.tagBits & 0x0) == 0L) && 
/* 145 */       ((this.tagBits & 0x400) != 0L) && (this.declaration != null)) {
/* 146 */       Annotation[] annotationNodes = this.declaration.annotations;
/* 147 */       if (annotationNodes != null) {
/* 148 */         int length = annotationNodes.length;
/* 149 */         ASTNode.resolveAnnotations(this.declaringScope, annotationNodes, this);
/* 150 */         annotations = new AnnotationBinding[length];
/* 151 */         for (int i = 0; i < length; i++)
/* 152 */           annotations[i] = new AnnotationBinding(annotationNodes[i]);
/* 153 */         setAnnotations(annotations);
/*     */       }
/*     */     }
/*     */ 
/* 157 */     return annotations;
/*     */   }
/*     */ 
/*     */   private void getScopeKey(BlockScope scope, StringBuffer buffer) {
/* 161 */     int scopeIndex = scope.scopeIndex();
/* 162 */     if (scopeIndex != -1) {
/* 163 */       getScopeKey((BlockScope)scope.parent, buffer);
/* 164 */       buffer.append('#');
/* 165 */       buffer.append(scopeIndex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isSecret()
/*     */   {
/* 172 */     return (this.declaration == null) && ((this.tagBits & 0x400) == 0L);
/*     */   }
/*     */ 
/*     */   public void recordInitializationEndPC(int pc)
/*     */   {
/* 177 */     if (this.initializationPCs[((this.initializationCount - 1 << 1) + 1)] == -1)
/* 178 */       this.initializationPCs[((this.initializationCount - 1 << 1) + 1)] = pc;
/*     */   }
/*     */ 
/*     */   public void recordInitializationStartPC(int pc)
/*     */   {
/* 183 */     if (this.initializationPCs == null) {
/* 184 */       return;
/*     */     }
/* 186 */     if (this.initializationCount > 0) {
/* 187 */       int previousEndPC = this.initializationPCs[((this.initializationCount - 1 << 1) + 1)];
/*     */ 
/* 189 */       if (previousEndPC == -1) {
/* 190 */         return;
/*     */       }
/*     */ 
/* 193 */       if (previousEndPC == pc) {
/* 194 */         this.initializationPCs[((this.initializationCount - 1 << 1) + 1)] = -1;
/* 195 */         return;
/*     */       }
/*     */     }
/* 198 */     int index = this.initializationCount << 1;
/* 199 */     if (index == this.initializationPCs.length) {
/* 200 */       System.arraycopy(this.initializationPCs, 0, this.initializationPCs = new int[this.initializationCount << 2], 0, index);
/*     */     }
/* 202 */     this.initializationPCs[index] = pc;
/* 203 */     this.initializationPCs[(index + 1)] = -1;
/* 204 */     this.initializationCount += 1;
/*     */   }
/*     */ 
/*     */   public void setAnnotations(AnnotationBinding[] annotations) {
/* 208 */     if (this.declaringScope == null) return;
/*     */ 
/* 210 */     SourceTypeBinding sourceType = this.declaringScope.enclosingSourceType();
/* 211 */     if (sourceType != null)
/* 212 */       sourceType.storeAnnotations(this, annotations);
/*     */   }
/*     */ 
/*     */   public void resetInitializations() {
/* 216 */     this.initializationCount = 0;
/* 217 */     this.initializationPCs = null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 222 */     String s = super.toString();
/* 223 */     switch (this.useFlag) {
/*     */     case 1:
/* 225 */       s = s + "[pos: " + String.valueOf(this.resolvedPosition) + "]";
/* 226 */       break;
/*     */     case 0:
/* 228 */       s = s + "[pos: unused]";
/* 229 */       break;
/*     */     case 2:
/* 231 */       s = s + "[pos: fake_used]";
/*     */     }
/*     */ 
/* 234 */     s = s + "[id:" + String.valueOf(this.id) + "]";
/* 235 */     if (this.initializationCount > 0) {
/* 236 */       s = s + "[pc: ";
/* 237 */       for (int i = 0; i < this.initializationCount; i++) {
/* 238 */         if (i > 0)
/* 239 */           s = s + ", ";
/* 240 */         s = s + String.valueOf(this.initializationPCs[(i << 1)]) + "-" + (this.initializationPCs[((i << 1) + 1)] == -1 ? "?" : String.valueOf(this.initializationPCs[((i << 1) + 1)]));
/*     */       }
/* 242 */       s = s + "]";
/*     */     }
/* 244 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding
 * JD-Core Version:    0.6.0
 */