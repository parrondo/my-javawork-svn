/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.util.HashtableOfPackage;
/*     */ import org.eclipse.jdt.internal.compiler.util.HashtableOfType;
/*     */ 
/*     */ public class PackageBinding extends Binding
/*     */   implements TypeConstants
/*     */ {
/*  18 */   public long tagBits = 0L;
/*     */   public char[][] compoundName;
/*     */   PackageBinding parent;
/*     */   public LookupEnvironment environment;
/*     */   HashtableOfType knownTypes;
/*     */   HashtableOfPackage knownPackages;
/*     */ 
/*     */   protected PackageBinding()
/*     */   {
/*     */   }
/*     */ 
/*     */   public PackageBinding(char[] topLevelPackageName, LookupEnvironment environment)
/*     */   {
/*  29 */     this(new char[][] { topLevelPackageName }, null, environment);
/*     */   }
/*     */ 
/*     */   public PackageBinding(char[][] compoundName, PackageBinding parent, LookupEnvironment environment)
/*     */   {
/*  34 */     this.compoundName = compoundName;
/*  35 */     this.parent = parent;
/*  36 */     this.environment = environment;
/*  37 */     this.knownTypes = null;
/*  38 */     this.knownPackages = new HashtableOfPackage(3);
/*     */   }
/*     */ 
/*     */   public PackageBinding(LookupEnvironment environment) {
/*  42 */     this(CharOperation.NO_CHAR_CHAR, null, environment);
/*     */   }
/*     */   private void addNotFoundPackage(char[] simpleName) {
/*  45 */     this.knownPackages.put(simpleName, LookupEnvironment.TheNotFoundPackage);
/*     */   }
/*     */   private void addNotFoundType(char[] simpleName) {
/*  48 */     if (this.knownTypes == null)
/*  49 */       this.knownTypes = new HashtableOfType(25);
/*  50 */     this.knownTypes.put(simpleName, LookupEnvironment.TheNotFoundType);
/*     */   }
/*     */   void addPackage(PackageBinding element) {
/*  53 */     if ((element.tagBits & 0x80) == 0L) clearMissingTagBit();
/*  54 */     this.knownPackages.put(element.compoundName[(element.compoundName.length - 1)], element);
/*     */   }
/*     */   void addType(ReferenceBinding element) {
/*  57 */     if ((element.tagBits & 0x80) == 0L) clearMissingTagBit();
/*  58 */     if (this.knownTypes == null)
/*  59 */       this.knownTypes = new HashtableOfType(25);
/*  60 */     this.knownTypes.put(element.compoundName[(element.compoundName.length - 1)], element);
/*     */   }
/*     */ 
/*     */   void clearMissingTagBit() {
/*  64 */     PackageBinding current = this;
/*     */     do
/*  66 */       current.tagBits &= -129L;
/*  67 */     while ((current = current.parent) != null);
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf)
/*     */   {
/*  74 */     return CharOperation.concatWith(this.compoundName, '/');
/*     */   }
/*     */   private PackageBinding findPackage(char[] name) {
/*  77 */     if (!this.environment.isPackage(this.compoundName, name)) {
/*  78 */       return null;
/*     */     }
/*  80 */     char[][] subPkgCompoundName = CharOperation.arrayConcat(this.compoundName, name);
/*  81 */     PackageBinding subPackageBinding = new PackageBinding(subPkgCompoundName, this, this.environment);
/*  82 */     addPackage(subPackageBinding);
/*  83 */     return subPackageBinding;
/*     */   }
/*     */ 
/*     */   PackageBinding getPackage(char[] name)
/*     */   {
/*  91 */     PackageBinding binding = getPackage0(name);
/*  92 */     if (binding != null) {
/*  93 */       if (binding == LookupEnvironment.TheNotFoundPackage) {
/*  94 */         return null;
/*     */       }
/*  96 */       return binding;
/*     */     }
/*  98 */     if ((binding = findPackage(name)) != null) {
/*  99 */       return binding;
/*     */     }
/*     */ 
/* 102 */     addNotFoundPackage(name);
/* 103 */     return null;
/*     */   }
/*     */ 
/*     */   PackageBinding getPackage0(char[] name)
/*     */   {
/* 114 */     return this.knownPackages.get(name);
/*     */   }
/*     */ 
/*     */   ReferenceBinding getType(char[] name)
/*     */   {
/* 125 */     ReferenceBinding referenceBinding = getType0(name);
/* 126 */     if ((referenceBinding == null) && 
/* 127 */       ((referenceBinding = this.environment.askForType(this, name)) == null))
/*     */     {
/* 129 */       addNotFoundType(name);
/* 130 */       return null;
/*     */     }
/*     */ 
/* 134 */     if (referenceBinding == LookupEnvironment.TheNotFoundType) {
/* 135 */       return null;
/*     */     }
/* 137 */     referenceBinding = (ReferenceBinding)BinaryTypeBinding.resolveType(referenceBinding, this.environment, false);
/* 138 */     if (referenceBinding.isNestedType())
/* 139 */       return new ProblemReferenceBinding(new char[][] { name }, referenceBinding, 4);
/* 140 */     return referenceBinding;
/*     */   }
/*     */ 
/*     */   ReferenceBinding getType0(char[] name)
/*     */   {
/* 151 */     if (this.knownTypes == null)
/* 152 */       return null;
/* 153 */     return this.knownTypes.get(name);
/*     */   }
/*     */ 
/*     */   public Binding getTypeOrPackage(char[] name)
/*     */   {
/* 166 */     ReferenceBinding referenceBinding = getType0(name);
/* 167 */     if ((referenceBinding != null) && (referenceBinding != LookupEnvironment.TheNotFoundType)) {
/* 168 */       referenceBinding = (ReferenceBinding)BinaryTypeBinding.resolveType(referenceBinding, this.environment, false);
/* 169 */       if (referenceBinding.isNestedType()) {
/* 170 */         return new ProblemReferenceBinding(new char[][] { name }, referenceBinding, 4);
/*     */       }
/* 172 */       if ((referenceBinding.tagBits & 0x80) == 0L) {
/* 173 */         return referenceBinding;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 178 */     PackageBinding packageBinding = getPackage0(name);
/* 179 */     if ((packageBinding != null) && (packageBinding != LookupEnvironment.TheNotFoundPackage)) {
/* 180 */       return packageBinding;
/*     */     }
/* 182 */     if (referenceBinding == null) {
/* 183 */       if ((referenceBinding = this.environment.askForType(this, name)) != null) {
/* 184 */         if (referenceBinding.isNestedType()) {
/* 185 */           return new ProblemReferenceBinding(new char[][] { name }, referenceBinding, 4);
/*     */         }
/* 187 */         return referenceBinding;
/*     */       }
/*     */ 
/* 192 */       addNotFoundType(name);
/*     */     }
/*     */ 
/* 195 */     if (packageBinding == null) {
/* 196 */       if ((packageBinding = findPackage(name)) != null) {
/* 197 */         return packageBinding;
/*     */       }
/* 199 */       if ((referenceBinding != null) && (referenceBinding != LookupEnvironment.TheNotFoundType)) {
/* 200 */         return referenceBinding;
/*     */       }
/* 202 */       addNotFoundPackage(name);
/*     */     }
/*     */ 
/* 205 */     return null;
/*     */   }
/*     */   public final boolean isViewedAsDeprecated() {
/* 208 */     if ((this.tagBits & 0x0) == 0L) {
/* 209 */       this.tagBits |= 17179869184L;
/* 210 */       if (this.compoundName != CharOperation.NO_CHAR_CHAR) {
/* 211 */         ReferenceBinding packageInfo = getType(TypeConstants.PACKAGE_INFO_NAME);
/* 212 */         if (packageInfo != null) {
/* 213 */           packageInfo.initializeDeprecatedAnnotationTagBits();
/* 214 */           this.tagBits |= packageInfo.tagBits & 0x0;
/*     */         }
/*     */       }
/*     */     }
/* 218 */     return (this.tagBits & 0x0) != 0L;
/*     */   }
/*     */ 
/*     */   public final int kind()
/*     */   {
/* 224 */     return 16;
/*     */   }
/*     */ 
/*     */   public int problemId() {
/* 228 */     if ((this.tagBits & 0x80) != 0L)
/* 229 */       return 1;
/* 230 */     return 0;
/*     */   }
/*     */ 
/*     */   public char[] readableName() {
/* 234 */     return CharOperation.concatWith(this.compoundName, '.');
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     String str;
/*     */     String str;
/* 238 */     if (this.compoundName == CharOperation.NO_CHAR_CHAR)
/* 239 */       str = "The Default Package";
/*     */     else {
/* 241 */       str = "package " + (this.compoundName != null ? CharOperation.toString(this.compoundName) : "UNNAMED");
/*     */     }
/* 243 */     if ((this.tagBits & 0x80) != 0L) {
/* 244 */       str = str + "[MISSING]";
/*     */     }
/* 246 */     return str;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.PackageBinding
 * JD-Core Version:    0.6.0
 */