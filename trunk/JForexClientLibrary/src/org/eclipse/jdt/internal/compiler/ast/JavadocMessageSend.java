/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class JavadocMessageSend extends MessageSend
/*     */ {
/*     */   public int tagSourceStart;
/*     */   public int tagSourceEnd;
/*     */   public int tagValue;
/*     */ 
/*     */   public JavadocMessageSend(char[] name, long pos)
/*     */   {
/*  25 */     this.selector = name;
/*  26 */     this.nameSourcePosition = pos;
/*  27 */     this.sourceStart = (int)(this.nameSourcePosition >>> 32);
/*  28 */     this.sourceEnd = (int)this.nameSourcePosition;
/*  29 */     this.bits |= 32768;
/*     */   }
/*     */   public JavadocMessageSend(char[] name, long pos, JavadocArgumentExpression[] arguments) {
/*  32 */     this(name, pos);
/*  33 */     this.arguments = arguments;
/*     */   }
/*     */ 
/*     */   private TypeBinding internalResolveType(Scope scope)
/*     */   {
/*  42 */     this.constant = Constant.NotAConstant;
/*  43 */     if (this.receiver == null)
/*  44 */       this.actualReceiverType = scope.enclosingReceiverType();
/*  45 */     else if (scope.kind == 3)
/*  46 */       this.actualReceiverType = this.receiver.resolveType((ClassScope)scope);
/*     */     else {
/*  48 */       this.actualReceiverType = this.receiver.resolveType((BlockScope)scope);
/*     */     }
/*     */ 
/*  53 */     TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
/*  54 */     boolean hasArgsTypeVar = false;
/*  55 */     if (this.arguments != null) {
/*  56 */       boolean argHasError = false;
/*  57 */       int length = this.arguments.length;
/*  58 */       argumentTypes = new TypeBinding[length];
/*  59 */       for (int i = 0; i < length; i++) {
/*  60 */         Expression argument = this.arguments[i];
/*  61 */         if (scope.kind == 3)
/*  62 */           argumentTypes[i] = argument.resolveType((ClassScope)scope);
/*     */         else {
/*  64 */           argumentTypes[i] = argument.resolveType((BlockScope)scope);
/*     */         }
/*  66 */         if (argumentTypes[i] == null)
/*  67 */           argHasError = true;
/*  68 */         else if (!hasArgsTypeVar) {
/*  69 */           hasArgsTypeVar = argumentTypes[i].isTypeVariable();
/*     */         }
/*     */       }
/*  72 */       if (argHasError) {
/*  73 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  78 */     if (this.actualReceiverType == null) {
/*  79 */       return null;
/*     */     }
/*  81 */     this.actualReceiverType = scope.environment().convertToRawType(this.receiver.resolvedType, true);
/*  82 */     ReferenceBinding enclosingType = scope.enclosingReceiverType();
/*  83 */     if ((enclosingType != null) && (enclosingType.isCompatibleWith(this.actualReceiverType))) {
/*  84 */       this.bits |= 16384;
/*     */     }
/*     */ 
/*  88 */     if (this.actualReceiverType.isBaseType()) {
/*  89 */       scope.problemReporter().javadocErrorNoMethodFor(this, this.actualReceiverType, argumentTypes, scope.getDeclarationModifiers());
/*  90 */       return null;
/*     */     }
/*  92 */     this.binding = scope.getMethod(this.actualReceiverType, this.selector, argumentTypes, this);
/*  93 */     if (!this.binding.isValidBinding())
/*     */     {
/*  95 */       TypeBinding enclosingTypeBinding = this.actualReceiverType;
/*  96 */       MethodBinding methodBinding = this.binding;
/*  97 */       while ((!methodBinding.isValidBinding()) && ((enclosingTypeBinding.isMemberType()) || (enclosingTypeBinding.isLocalType()))) {
/*  98 */         enclosingTypeBinding = enclosingTypeBinding.enclosingType();
/*  99 */         methodBinding = scope.getMethod(enclosingTypeBinding, this.selector, argumentTypes, this);
/*     */       }
/* 101 */       if (methodBinding.isValidBinding()) {
/* 102 */         this.binding = methodBinding;
/*     */       }
/*     */       else {
/* 105 */         enclosingTypeBinding = this.actualReceiverType;
/* 106 */         MethodBinding contructorBinding = this.binding;
/* 107 */         while ((!contructorBinding.isValidBinding()) && ((enclosingTypeBinding.isMemberType()) || (enclosingTypeBinding.isLocalType()))) {
/* 108 */           enclosingTypeBinding = enclosingTypeBinding.enclosingType();
/* 109 */           if (CharOperation.equals(this.selector, enclosingTypeBinding.shortReadableName())) {
/* 110 */             contructorBinding = scope.getConstructor((ReferenceBinding)enclosingTypeBinding, argumentTypes, this);
/*     */           }
/*     */         }
/* 113 */         if (contructorBinding.isValidBinding()) {
/* 114 */           this.binding = contructorBinding;
/*     */         }
/*     */       }
/*     */     }
/* 118 */     if (!this.binding.isValidBinding())
/*     */     {
/* 120 */       switch (this.binding.problemId()) {
/*     */       case 3:
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/* 125 */         MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
/* 126 */         if (closestMatch == null) break;
/* 127 */         this.binding = closestMatch;
/*     */       case 4:
/*     */       }
/*     */     }
/* 131 */     if (!this.binding.isValidBinding()) {
/* 132 */       if ((this.receiver.resolvedType instanceof ProblemReferenceBinding))
/*     */       {
/* 134 */         return null;
/*     */       }
/* 136 */       if (this.binding.declaringClass == null) {
/* 137 */         if ((this.actualReceiverType instanceof ReferenceBinding)) {
/* 138 */           this.binding.declaringClass = ((ReferenceBinding)this.actualReceiverType);
/*     */         } else {
/* 140 */           scope.problemReporter().javadocErrorNoMethodFor(this, this.actualReceiverType, argumentTypes, scope.getDeclarationModifiers());
/* 141 */           return null;
/*     */         }
/*     */       }
/* 144 */       scope.problemReporter().javadocInvalidMethod(this, this.binding, scope.getDeclarationModifiers());
/*     */ 
/* 146 */       if ((this.binding instanceof ProblemMethodBinding)) {
/* 147 */         MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
/* 148 */         if (closestMatch != null) this.binding = closestMatch;
/*     */       }
/* 150 */       return this.resolvedType = this.binding == null ? null : this.binding.returnType;
/* 151 */     }if (hasArgsTypeVar) {
/* 152 */       MethodBinding problem = new ProblemMethodBinding(this.binding, this.selector, argumentTypes, 1);
/* 153 */       scope.problemReporter().javadocInvalidMethod(this, problem, scope.getDeclarationModifiers());
/* 154 */     } else if (this.binding.isVarargs()) {
/* 155 */       int length = argumentTypes.length;
/* 156 */       if ((this.binding.parameters.length != length) || (!argumentTypes[(length - 1)].isArrayType())) {
/* 157 */         MethodBinding problem = new ProblemMethodBinding(this.binding, this.selector, argumentTypes, 1);
/* 158 */         scope.problemReporter().javadocInvalidMethod(this, problem, scope.getDeclarationModifiers());
/*     */       }
/*     */     } else {
/* 161 */       int length = argumentTypes.length;
/* 162 */       for (int i = 0; i < length; i++) {
/* 163 */         if (this.binding.parameters[i].erasure() != argumentTypes[i].erasure()) {
/* 164 */           MethodBinding problem = new ProblemMethodBinding(this.binding, this.selector, argumentTypes, 1);
/* 165 */           scope.problemReporter().javadocInvalidMethod(this, problem, scope.getDeclarationModifiers());
/* 166 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 170 */     if (isMethodUseDeprecated(this.binding, scope, true)) {
/* 171 */       scope.problemReporter().javadocDeprecatedMethod(this.binding, this, scope.getDeclarationModifiers());
/*     */     }
/*     */ 
/* 174 */     return this.resolvedType = this.binding.returnType;
/*     */   }
/*     */ 
/*     */   public boolean isSuperAccess()
/*     */   {
/* 181 */     return (this.bits & 0x4000) != 0;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/* 186 */     if (this.receiver != null) {
/* 187 */       this.receiver.printExpression(0, output);
/*     */     }
/* 189 */     output.append('#').append(this.selector).append('(');
/* 190 */     if (this.arguments != null) {
/* 191 */       for (int i = 0; i < this.arguments.length; i++) {
/* 192 */         if (i > 0) output.append(", ");
/* 193 */         this.arguments[i].printExpression(0, output);
/*     */       }
/*     */     }
/* 196 */     return output.append(')');
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope) {
/* 200 */     return internalResolveType(scope);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(ClassScope scope) {
/* 204 */     return internalResolveType(scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 212 */     if (visitor.visit(this, blockScope)) {
/* 213 */       if (this.receiver != null) {
/* 214 */         this.receiver.traverse(visitor, blockScope);
/*     */       }
/* 216 */       if (this.arguments != null) {
/* 217 */         int argumentsLength = this.arguments.length;
/* 218 */         for (int i = 0; i < argumentsLength; i++)
/* 219 */           this.arguments[i].traverse(visitor, blockScope);
/*     */       }
/*     */     }
/* 222 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope)
/*     */   {
/* 229 */     if (visitor.visit(this, scope)) {
/* 230 */       if (this.receiver != null) {
/* 231 */         this.receiver.traverse(visitor, scope);
/*     */       }
/* 233 */       if (this.arguments != null) {
/* 234 */         int argumentsLength = this.arguments.length;
/* 235 */         for (int i = 0; i < argumentsLength; i++)
/* 236 */           this.arguments[i].traverse(visitor, scope);
/*     */       }
/*     */     }
/* 239 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend
 * JD-Core Version:    0.6.0
 */