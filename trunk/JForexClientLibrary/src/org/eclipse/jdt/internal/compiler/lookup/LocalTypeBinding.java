/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ 
/*     */ public final class LocalTypeBinding extends NestedTypeBinding
/*     */ {
/*  21 */   static final char[] LocalTypePrefix = { '$', 'L', 'o', 'c', 'a', 'l', '$' };
/*     */   private InnerEmulationDependency[] dependents;
/*     */   public ArrayBinding[] localArrayBindings;
/*     */   public CaseStatement enclosingCase;
/*     */   public int sourceStart;
/*     */   public MethodBinding enclosingMethod;
/*     */ 
/*     */   public LocalTypeBinding(ClassScope scope, SourceTypeBinding enclosingType, CaseStatement switchCase, ReferenceBinding anonymousOriginalSuperType)
/*     */   {
/*  35 */     super(new char[][] { CharOperation.concat(LocalTypePrefix, scope.referenceContext.name) }, 
/*  34 */       scope, 
/*  35 */       enclosingType);
/*  36 */     TypeDeclaration typeDeclaration = scope.referenceContext;
/*  37 */     if ((typeDeclaration.bits & 0x200) != 0)
/*  38 */       this.tagBits |= 2100L;
/*     */     else {
/*  40 */       this.tagBits |= 2068L;
/*     */     }
/*  42 */     this.enclosingCase = switchCase;
/*  43 */     this.sourceStart = typeDeclaration.sourceStart;
/*  44 */     MethodScope methodScope = scope.enclosingMethodScope();
/*  45 */     AbstractMethodDeclaration methodDeclaration = methodScope.referenceMethod();
/*  46 */     if (methodDeclaration != null)
/*  47 */       this.enclosingMethod = methodDeclaration.binding;
/*     */   }
/*     */ 
/*     */   public void addInnerEmulationDependent(BlockScope dependentScope, boolean wasEnclosingInstanceSupplied)
/*     */   {
/*     */     int index;
/*  57 */     if (this.dependents == null) {
/*  58 */       int index = 0;
/*  59 */       this.dependents = new InnerEmulationDependency[1];
/*     */     } else {
/*  61 */       index = this.dependents.length;
/*  62 */       for (int i = 0; i < index; i++)
/*  63 */         if (this.dependents[i].scope == dependentScope)
/*  64 */           return;
/*  65 */       System.arraycopy(this.dependents, 0, this.dependents = new InnerEmulationDependency[index + 1], 0, index);
/*     */     }
/*  67 */     this.dependents[index] = new InnerEmulationDependency(dependentScope, wasEnclosingInstanceSupplied);
/*     */   }
/*     */ 
/*     */   public ReferenceBinding anonymousOriginalSuperType()
/*     */   {
/*  75 */     if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
/*  76 */       return this.superInterfaces[0];
/*     */     }
/*  78 */     if ((this.tagBits & 0x20000) == 0L) {
/*  79 */       return this.superclass;
/*     */     }
/*  81 */     if (this.scope != null) {
/*  82 */       TypeReference typeReference = this.scope.referenceContext.allocation.type;
/*  83 */       if (typeReference != null) {
/*  84 */         return (ReferenceBinding)typeReference.resolvedType;
/*     */       }
/*     */     }
/*  87 */     return this.superclass;
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf) {
/*  91 */     char[] outerKey = outermostEnclosingType().computeUniqueKey(isLeaf);
/*  92 */     int semicolon = CharOperation.lastIndexOf(';', outerKey);
/*     */ 
/*  94 */     StringBuffer sig = new StringBuffer();
/*  95 */     sig.append(outerKey, 0, semicolon);
/*     */ 
/*  98 */     sig.append('$');
/*  99 */     sig.append(String.valueOf(this.sourceStart));
/*     */ 
/* 102 */     if (!isAnonymousType()) {
/* 103 */       sig.append('$');
/* 104 */       sig.append(this.sourceName);
/*     */     }
/*     */ 
/* 108 */     sig.append(outerKey, semicolon, outerKey.length - semicolon);
/*     */ 
/* 110 */     int sigLength = sig.length();
/* 111 */     char[] uniqueKey = new char[sigLength];
/* 112 */     sig.getChars(0, sigLength, uniqueKey, 0);
/* 113 */     return uniqueKey;
/*     */   }
/*     */ 
/*     */   public char[] constantPoolName() {
/* 117 */     return this.constantPoolName;
/*     */   }
/*     */ 
/*     */   ArrayBinding createArrayType(int dimensionCount, LookupEnvironment lookupEnvironment) {
/* 121 */     if (this.localArrayBindings == null) {
/* 122 */       this.localArrayBindings = new ArrayBinding[] { new ArrayBinding(this, dimensionCount, lookupEnvironment) };
/* 123 */       return this.localArrayBindings[0];
/*     */     }
/*     */ 
/* 126 */     int length = this.localArrayBindings.length;
/* 127 */     for (int i = 0; i < length; i++) {
/* 128 */       if (this.localArrayBindings[i].dimensions == dimensionCount) {
/* 129 */         return this.localArrayBindings[i];
/*     */       }
/*     */     }
/* 132 */     System.arraycopy(this.localArrayBindings, 0, this.localArrayBindings = new ArrayBinding[length + 1], 0, length);
/* 133 */     return this.localArrayBindings[length] =  = new ArrayBinding(this, dimensionCount, lookupEnvironment);
/*     */   }
/*     */ 
/*     */   public char[] genericTypeSignature()
/*     */   {
/* 142 */     if ((this.genericReferenceTypeSignature == null) && (constantPoolName() == null)) {
/* 143 */       if (isAnonymousType())
/* 144 */         setConstantPoolName(superclass().sourceName());
/*     */       else
/* 146 */         setConstantPoolName(sourceName());
/*     */     }
/* 148 */     return super.genericTypeSignature();
/*     */   }
/*     */ 
/*     */   public char[] readableName()
/*     */   {
/*     */     char[] readableName;
/*     */     char[] readableName;
/* 153 */     if (isAnonymousType()) {
/* 154 */       readableName = CharOperation.concat(TypeConstants.ANONYM_PREFIX, anonymousOriginalSuperType().readableName(), TypeConstants.ANONYM_SUFFIX);
/*     */     }
/*     */     else
/*     */     {
/*     */       char[] readableName;
/* 155 */       if (isMemberType())
/* 156 */         readableName = CharOperation.concat(enclosingType().readableName(), this.sourceName, '.');
/*     */       else
/* 158 */         readableName = this.sourceName;
/*     */     }
/*     */     TypeVariableBinding[] typeVars;
/* 161 */     if ((typeVars = typeVariables()) != Binding.NO_TYPE_VARIABLES) {
/* 162 */       StringBuffer nameBuffer = new StringBuffer(10);
/* 163 */       nameBuffer.append(readableName).append('<');
/* 164 */       int i = 0; for (int length = typeVars.length; i < length; i++) {
/* 165 */         if (i > 0) nameBuffer.append(',');
/* 166 */         nameBuffer.append(typeVars[i].readableName());
/*     */       }
/* 168 */       nameBuffer.append('>');
/* 169 */       int nameLength = nameBuffer.length();
/* 170 */       readableName = new char[nameLength];
/* 171 */       nameBuffer.getChars(0, nameLength, readableName, 0);
/*     */     }
/* 173 */     return readableName;
/*     */   }
/*     */ 
/*     */   public char[] shortReadableName()
/*     */   {
/*     */     char[] shortReadableName;
/*     */     char[] shortReadableName;
/* 178 */     if (isAnonymousType()) {
/* 179 */       shortReadableName = CharOperation.concat(TypeConstants.ANONYM_PREFIX, anonymousOriginalSuperType().shortReadableName(), TypeConstants.ANONYM_SUFFIX);
/*     */     }
/*     */     else
/*     */     {
/*     */       char[] shortReadableName;
/* 180 */       if (isMemberType())
/* 181 */         shortReadableName = CharOperation.concat(enclosingType().shortReadableName(), this.sourceName, '.');
/*     */       else
/* 183 */         shortReadableName = this.sourceName;
/*     */     }
/*     */     TypeVariableBinding[] typeVars;
/* 186 */     if ((typeVars = typeVariables()) != Binding.NO_TYPE_VARIABLES) {
/* 187 */       StringBuffer nameBuffer = new StringBuffer(10);
/* 188 */       nameBuffer.append(shortReadableName).append('<');
/* 189 */       int i = 0; for (int length = typeVars.length; i < length; i++) {
/* 190 */         if (i > 0) nameBuffer.append(',');
/* 191 */         nameBuffer.append(typeVars[i].shortReadableName());
/*     */       }
/* 193 */       nameBuffer.append('>');
/* 194 */       int nameLength = nameBuffer.length();
/* 195 */       shortReadableName = new char[nameLength];
/* 196 */       nameBuffer.getChars(0, nameLength, shortReadableName, 0);
/*     */     }
/* 198 */     return shortReadableName;
/*     */   }
/*     */ 
/*     */   public void setAsMemberType()
/*     */   {
/* 203 */     this.tagBits |= 2060L;
/*     */   }
/*     */ 
/*     */   public void setConstantPoolName(char[] computedConstantPoolName) {
/* 207 */     this.constantPoolName = computedConstantPoolName;
/*     */   }
/*     */ 
/*     */   public char[] signature()
/*     */   {
/* 216 */     if ((this.signature == null) && (constantPoolName() == null)) {
/* 217 */       if (isAnonymousType())
/* 218 */         setConstantPoolName(superclass().sourceName());
/*     */       else
/* 220 */         setConstantPoolName(sourceName());
/*     */     }
/* 222 */     return super.signature();
/*     */   }
/*     */ 
/*     */   public char[] sourceName() {
/* 226 */     if (isAnonymousType()) {
/* 227 */       return CharOperation.concat(TypeConstants.ANONYM_PREFIX, anonymousOriginalSuperType().sourceName(), TypeConstants.ANONYM_SUFFIX);
/*     */     }
/* 229 */     return this.sourceName;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 233 */     if (isAnonymousType())
/* 234 */       return "Anonymous type : " + super.toString();
/* 235 */     if (isMemberType())
/* 236 */       return "Local member type : " + new String(sourceName()) + " " + super.toString();
/* 237 */     return "Local type : " + new String(sourceName()) + " " + super.toString();
/*     */   }
/*     */ 
/*     */   public void updateInnerEmulationDependents()
/*     */   {
/* 244 */     if (this.dependents != null)
/* 245 */       for (int i = 0; i < this.dependents.length; i++) {
/* 246 */         InnerEmulationDependency dependency = this.dependents[i];
/*     */ 
/* 248 */         dependency.scope.propagateInnerEmulation(this, dependency.wasEnclosingInstanceSupplied);
/*     */       }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding
 * JD-Core Version:    0.6.0
 */