/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ 
/*     */ public class CaptureBinding extends TypeVariableBinding
/*     */ {
/*     */   public TypeBinding lowerBound;
/*     */   public WildcardBinding wildcard;
/*     */   public int captureID;
/*     */   public ReferenceBinding sourceType;
/*     */   public int position;
/*     */ 
/*     */   public CaptureBinding(WildcardBinding wildcard, ReferenceBinding sourceType, int position, int captureID)
/*     */   {
/*  28 */     super(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX, null, 0, wildcard.environment);
/*  29 */     this.wildcard = wildcard;
/*  30 */     this.modifiers = 1073741825;
/*  31 */     this.fPackage = wildcard.fPackage;
/*  32 */     this.sourceType = sourceType;
/*  33 */     this.position = position;
/*  34 */     this.captureID = captureID;
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf)
/*     */   {
/*  43 */     StringBuffer buffer = new StringBuffer();
/*  44 */     if (isLeaf) {
/*  45 */       buffer.append(this.sourceType.computeUniqueKey(false));
/*  46 */       buffer.append('&');
/*     */     }
/*  48 */     buffer.append(TypeConstants.WILDCARD_CAPTURE);
/*  49 */     buffer.append(this.wildcard.computeUniqueKey(false));
/*  50 */     buffer.append(this.position);
/*  51 */     buffer.append(';');
/*  52 */     int length = buffer.length();
/*  53 */     char[] uniqueKey = new char[length];
/*  54 */     buffer.getChars(0, length, uniqueKey, 0);
/*  55 */     return uniqueKey;
/*     */   }
/*     */ 
/*     */   public String debugName()
/*     */   {
/*  60 */     if (this.wildcard != null) {
/*  61 */       StringBuffer buffer = new StringBuffer(10);
/*  62 */       buffer
/*  63 */         .append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX)
/*  64 */         .append(this.captureID)
/*  65 */         .append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX)
/*  66 */         .append(this.wildcard.debugName());
/*  67 */       return buffer.toString();
/*     */     }
/*  69 */     return super.debugName();
/*     */   }
/*     */ 
/*     */   public char[] genericTypeSignature() {
/*  73 */     if (this.genericTypeSignature == null) {
/*  74 */       this.genericTypeSignature = CharOperation.concat(TypeConstants.WILDCARD_CAPTURE, this.wildcard.genericTypeSignature());
/*     */     }
/*  76 */     return this.genericTypeSignature;
/*     */   }
/*     */ 
/*     */   public void initializeBounds(Scope scope, ParameterizedTypeBinding capturedParameterizedType)
/*     */   {
/*  84 */     TypeVariableBinding wildcardVariable = this.wildcard.typeVariable();
/*  85 */     if (wildcardVariable == null)
/*     */     {
/*  88 */       TypeBinding originalWildcardBound = this.wildcard.bound;
/*  89 */       switch (this.wildcard.boundKind)
/*     */       {
/*     */       case 1:
/*  92 */         TypeBinding capturedWildcardBound = originalWildcardBound.capture(scope, this.position);
/*  93 */         if (originalWildcardBound.isInterface()) {
/*  94 */           this.superclass = scope.getJavaLangObject();
/*  95 */           this.superInterfaces = new ReferenceBinding[] { (ReferenceBinding)capturedWildcardBound };
/*     */         }
/*     */         else
/*     */         {
/*  99 */           if ((capturedWildcardBound.isArrayType()) || (capturedWildcardBound == this))
/* 100 */             this.superclass = scope.getJavaLangObject();
/*     */           else {
/* 102 */             this.superclass = ((ReferenceBinding)capturedWildcardBound);
/*     */           }
/* 104 */           this.superInterfaces = Binding.NO_SUPERINTERFACES;
/*     */         }
/* 106 */         this.firstBound = capturedWildcardBound;
/* 107 */         if ((capturedWildcardBound.tagBits & 0x20000000) != 0L) break;
/* 108 */         this.tagBits &= -536870913L;
/* 109 */         break;
/*     */       case 0:
/* 111 */         this.superclass = scope.getJavaLangObject();
/* 112 */         this.superInterfaces = Binding.NO_SUPERINTERFACES;
/* 113 */         this.tagBits &= -536870913L;
/* 114 */         break;
/*     */       case 2:
/* 116 */         this.superclass = scope.getJavaLangObject();
/* 117 */         this.superInterfaces = Binding.NO_SUPERINTERFACES;
/* 118 */         this.lowerBound = this.wildcard.bound;
/* 119 */         if ((originalWildcardBound.tagBits & 0x20000000) != 0L) break;
/* 120 */         this.tagBits &= -536870913L;
/*     */       }
/*     */ 
/* 123 */       return;
/*     */     }
/* 125 */     ReferenceBinding originalVariableSuperclass = wildcardVariable.superclass;
/* 126 */     ReferenceBinding substitutedVariableSuperclass = (ReferenceBinding)Scope.substitute(capturedParameterizedType, originalVariableSuperclass);
/*     */ 
/* 128 */     if (substitutedVariableSuperclass == this) substitutedVariableSuperclass = originalVariableSuperclass;
/*     */ 
/* 130 */     ReferenceBinding[] originalVariableInterfaces = wildcardVariable.superInterfaces();
/* 131 */     ReferenceBinding[] substitutedVariableInterfaces = Scope.substitute(capturedParameterizedType, originalVariableInterfaces);
/* 132 */     if (substitutedVariableInterfaces != originalVariableInterfaces)
/*     */     {
/* 134 */       int i = 0; for (int length = substitutedVariableInterfaces.length; i < length; i++) {
/* 135 */         if (substitutedVariableInterfaces[i] != this) continue; substitutedVariableInterfaces[i] = originalVariableInterfaces[i];
/*     */       }
/*     */     }
/*     */ 
/* 139 */     TypeBinding originalWildcardBound = this.wildcard.bound;
/*     */ 
/* 141 */     switch (this.wildcard.boundKind)
/*     */     {
/*     */     case 1:
/* 144 */       TypeBinding capturedWildcardBound = originalWildcardBound.capture(scope, this.position);
/* 145 */       if (originalWildcardBound.isInterface()) {
/* 146 */         this.superclass = substitutedVariableSuperclass;
/*     */ 
/* 148 */         if (substitutedVariableInterfaces == Binding.NO_SUPERINTERFACES) {
/* 149 */           this.superInterfaces = new ReferenceBinding[] { (ReferenceBinding)capturedWildcardBound };
/*     */         } else {
/* 151 */           int length = substitutedVariableInterfaces.length;
/* 152 */           System.arraycopy(substitutedVariableInterfaces, 0, substitutedVariableInterfaces = new ReferenceBinding[length + 1], 1, length);
/* 153 */           substitutedVariableInterfaces[0] = ((ReferenceBinding)capturedWildcardBound);
/* 154 */           this.superInterfaces = Scope.greaterLowerBound(substitutedVariableInterfaces);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 159 */         if ((capturedWildcardBound.isArrayType()) || (capturedWildcardBound == this)) {
/* 160 */           this.superclass = substitutedVariableSuperclass;
/*     */         } else {
/* 162 */           this.superclass = ((ReferenceBinding)capturedWildcardBound);
/* 163 */           if (this.superclass.isSuperclassOf(substitutedVariableSuperclass)) {
/* 164 */             this.superclass = substitutedVariableSuperclass;
/*     */           }
/*     */         }
/* 167 */         this.superInterfaces = substitutedVariableInterfaces;
/*     */       }
/* 169 */       this.firstBound = capturedWildcardBound;
/* 170 */       if ((capturedWildcardBound.tagBits & 0x20000000) != 0L) break;
/* 171 */       this.tagBits &= -536870913L;
/* 172 */       break;
/*     */     case 0:
/* 174 */       this.superclass = substitutedVariableSuperclass;
/* 175 */       this.superInterfaces = substitutedVariableInterfaces;
/* 176 */       this.tagBits &= -536870913L;
/* 177 */       break;
/*     */     case 2:
/* 179 */       this.superclass = substitutedVariableSuperclass;
/* 180 */       if ((wildcardVariable.firstBound == substitutedVariableSuperclass) || (originalWildcardBound == substitutedVariableSuperclass)) {
/* 181 */         this.firstBound = substitutedVariableSuperclass;
/*     */       }
/* 183 */       this.superInterfaces = substitutedVariableInterfaces;
/* 184 */       this.lowerBound = originalWildcardBound;
/* 185 */       if ((originalWildcardBound.tagBits & 0x20000000) != 0L) break;
/* 186 */       this.tagBits &= -536870913L;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isCapture()
/*     */   {
/* 195 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isEquivalentTo(TypeBinding otherType)
/*     */   {
/* 202 */     if (this == otherType) return true;
/* 203 */     if (otherType == null) return false;
/*     */ 
/* 205 */     if ((this.firstBound != null) && (this.firstBound.isArrayType()) && 
/* 206 */       (this.firstBound.isCompatibleWith(otherType))) {
/* 207 */       return true;
/*     */     }
/* 209 */     switch (otherType.kind()) {
/*     */     case 516:
/*     */     case 8196:
/* 212 */       return ((WildcardBinding)otherType).boundCheck(this);
/*     */     }
/* 214 */     return false;
/*     */   }
/*     */ 
/*     */   public char[] readableName() {
/* 218 */     if (this.wildcard != null) {
/* 219 */       StringBuffer buffer = new StringBuffer(10);
/* 220 */       buffer
/* 221 */         .append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX)
/* 222 */         .append(this.captureID)
/* 223 */         .append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX)
/* 224 */         .append(this.wildcard.readableName());
/* 225 */       int length = buffer.length();
/* 226 */       char[] name = new char[length];
/* 227 */       buffer.getChars(0, length, name, 0);
/* 228 */       return name;
/*     */     }
/* 230 */     return super.readableName();
/*     */   }
/*     */ 
/*     */   public char[] shortReadableName() {
/* 234 */     if (this.wildcard != null) {
/* 235 */       StringBuffer buffer = new StringBuffer(10);
/* 236 */       buffer
/* 237 */         .append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX)
/* 238 */         .append(this.captureID)
/* 239 */         .append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX)
/* 240 */         .append(this.wildcard.shortReadableName());
/* 241 */       int length = buffer.length();
/* 242 */       char[] name = new char[length];
/* 243 */       buffer.getChars(0, length, name, 0);
/* 244 */       return name;
/*     */     }
/* 246 */     return super.shortReadableName();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 250 */     if (this.wildcard != null) {
/* 251 */       StringBuffer buffer = new StringBuffer(10);
/* 252 */       buffer
/* 253 */         .append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX)
/* 254 */         .append(this.captureID)
/* 255 */         .append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX)
/* 256 */         .append(this.wildcard);
/* 257 */       return buffer.toString();
/*     */     }
/* 259 */     return super.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.CaptureBinding
 * JD-Core Version:    0.6.0
 */