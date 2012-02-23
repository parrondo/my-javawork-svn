/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ 
/*     */ public class RawTypeBinding extends ParameterizedTypeBinding
/*     */ {
/*     */   public RawTypeBinding(ReferenceBinding type, ReferenceBinding enclosingType, LookupEnvironment environment)
/*     */   {
/*  28 */     super(type, null, enclosingType, environment);
/*  29 */     if ((enclosingType == null) || ((enclosingType.modifiers & 0x40000000) == 0))
/*  30 */       this.modifiers &= -1073741825;
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf) {
/*  34 */     StringBuffer sig = new StringBuffer(10);
/*  35 */     if ((isMemberType()) && (enclosingType().isParameterizedType())) {
/*  36 */       char[] typeSig = enclosingType().computeUniqueKey(false);
/*  37 */       sig.append(typeSig, 0, typeSig.length - 1);
/*  38 */       sig.append('.').append(sourceName()).append('<').append('>').append(';');
/*     */     } else {
/*  40 */       sig.append(genericType().computeUniqueKey(false));
/*  41 */       sig.insert(sig.length() - 1, "<>");
/*     */     }
/*     */ 
/*  44 */     int sigLength = sig.length();
/*  45 */     char[] uniqueKey = new char[sigLength];
/*  46 */     sig.getChars(0, sigLength, uniqueKey, 0);
/*  47 */     return uniqueKey;
/*     */   }
/*     */ 
/*     */   public ParameterizedMethodBinding createParameterizedMethod(MethodBinding originalMethod)
/*     */   {
/*  54 */     if ((originalMethod.typeVariables == Binding.NO_TYPE_VARIABLES) || (originalMethod.isStatic())) {
/*  55 */       return super.createParameterizedMethod(originalMethod);
/*     */     }
/*  57 */     return this.environment.createParameterizedGenericMethod(originalMethod, this);
/*     */   }
/*     */ 
/*     */   public int kind() {
/*  61 */     return 1028;
/*     */   }
/*     */ 
/*     */   public String debugName()
/*     */   {
/*  68 */     StringBuffer nameBuffer = new StringBuffer(10);
/*  69 */     nameBuffer.append(actualType().sourceName()).append("#RAW");
/*  70 */     return nameBuffer.toString();
/*     */   }
/*     */ 
/*     */   public char[] genericTypeSignature()
/*     */   {
/*  78 */     if (this.genericTypeSignature == null) {
/*  79 */       if ((this.modifiers & 0x40000000) == 0) {
/*  80 */         this.genericTypeSignature = genericType().signature();
/*     */       } else {
/*  82 */         StringBuffer sig = new StringBuffer(10);
/*  83 */         if (isMemberType()) {
/*  84 */           ReferenceBinding enclosing = enclosingType();
/*  85 */           char[] typeSig = enclosing.genericTypeSignature();
/*  86 */           sig.append(typeSig, 0, typeSig.length - 1);
/*  87 */           if ((enclosing.modifiers & 0x40000000) != 0)
/*  88 */             sig.append('.');
/*     */           else {
/*  90 */             sig.append('$');
/*     */           }
/*  92 */           sig.append(sourceName());
/*     */         } else {
/*  94 */           char[] typeSig = genericType().signature();
/*  95 */           sig.append(typeSig, 0, typeSig.length - 1);
/*     */         }
/*  97 */         sig.append(';');
/*  98 */         int sigLength = sig.length();
/*  99 */         this.genericTypeSignature = new char[sigLength];
/* 100 */         sig.getChars(0, sigLength, this.genericTypeSignature, 0);
/*     */       }
/*     */     }
/* 103 */     return this.genericTypeSignature;
/*     */   }
/*     */ 
/*     */   public boolean isEquivalentTo(TypeBinding otherType) {
/* 107 */     if (this == otherType)
/* 108 */       return true;
/* 109 */     if (otherType == null)
/* 110 */       return false;
/* 111 */     switch (otherType.kind())
/*     */     {
/*     */     case 516:
/*     */     case 8196:
/* 115 */       return ((WildcardBinding)otherType).boundCheck(this);
/*     */     case 260:
/*     */     case 1028:
/*     */     case 2052:
/* 120 */       return erasure() == otherType.erasure();
/*     */     }
/* 122 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isProvablyDistinct(TypeBinding otherType) {
/* 126 */     if (this == otherType)
/* 127 */       return false;
/* 128 */     if (otherType == null)
/* 129 */       return true;
/* 130 */     switch (otherType.kind())
/*     */     {
/*     */     case 260:
/*     */     case 1028:
/*     */     case 2052:
/* 135 */       return erasure() != otherType.erasure();
/*     */     }
/* 137 */     return true;
/*     */   }
/*     */ 
/*     */   protected void initializeArguments() {
/* 141 */     TypeVariableBinding[] typeVariables = genericType().typeVariables();
/* 142 */     int length = typeVariables.length;
/* 143 */     TypeBinding[] typeArguments = new TypeBinding[length];
/* 144 */     for (int i = 0; i < length; i++)
/*     */     {
/* 146 */       typeArguments[i] = this.environment.convertToRawType(typeVariables[i].erasure(), false);
/*     */     }
/* 148 */     this.arguments = typeArguments;
/*     */   }
/*     */ 
/*     */   public char[] readableName()
/*     */   {
/*     */     char[] readableName;
/*     */     char[] readableName;
/* 155 */     if (isMemberType())
/* 156 */       readableName = CharOperation.concat(enclosingType().readableName(), this.sourceName, '.');
/*     */     else {
/* 158 */       readableName = CharOperation.concatWith(actualType().compoundName, '.');
/*     */     }
/* 160 */     return readableName;
/*     */   }
/*     */ 
/*     */   public char[] shortReadableName()
/*     */   {
/*     */     char[] shortReadableName;
/*     */     char[] shortReadableName;
/* 168 */     if (isMemberType())
/* 169 */       shortReadableName = CharOperation.concat(enclosingType().shortReadableName(), this.sourceName, '.');
/*     */     else {
/* 171 */       shortReadableName = actualType().sourceName;
/*     */     }
/* 173 */     return shortReadableName;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding
 * JD-Core Version:    0.6.0
 */