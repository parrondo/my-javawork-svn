/*     */ package org.eclipse.jdt.internal.compiler;
/*     */ 
/*     */ import org.eclipse.jdt.core.IType;
/*     */ import org.eclipse.jdt.core.JavaModelException;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
/*     */ import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
/*     */ 
/*     */ public final class ExtraFlags
/*     */ {
/*     */   public static final int HasNonPrivateStaticMemberTypes = 1;
/*     */   public static final int IsMemberType = 2;
/*     */   public static final int IsLocalType = 4;
/*     */   public static final int ParameterTypesStoredAsSignature = 16;
/*     */ 
/*     */   public static int getExtraFlags(ClassFileReader reader)
/*     */   {
/*  29 */     int extraFlags = 0;
/*     */ 
/*  31 */     if (reader.isNestedType()) {
/*  32 */       extraFlags |= 2;
/*     */     }
/*     */ 
/*  35 */     if (reader.isLocal()) {
/*  36 */       extraFlags |= 4;
/*     */     }
/*     */ 
/*  39 */     IBinaryNestedType[] memberTypes = reader.getMemberTypes();
/*  40 */     int memberTypeCounter = memberTypes == null ? 0 : memberTypes.length;
/*  41 */     if (memberTypeCounter > 0) {
/*  42 */       for (int i = 0; i < memberTypeCounter; i++) {
/*  43 */         int modifiers = memberTypes[i].getModifiers();
/*     */ 
/*  45 */         if (((modifiers & 0x8) != 0) && ((modifiers & 0x2) == 0)) {
/*  46 */           extraFlags |= 1;
/*  47 */           break;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  53 */     return extraFlags;
/*     */   }
/*     */ 
/*     */   public static int getExtraFlags(IType type) throws JavaModelException {
/*  57 */     int extraFlags = 0;
/*     */ 
/*  59 */     if (type.isMember()) {
/*  60 */       extraFlags |= 2;
/*     */     }
/*     */ 
/*  63 */     if (type.isLocal()) {
/*  64 */       extraFlags |= 4;
/*     */     }
/*     */ 
/*  67 */     IType[] memberTypes = type.getTypes();
/*  68 */     int memberTypeCounter = memberTypes == null ? 0 : memberTypes.length;
/*  69 */     if (memberTypeCounter > 0) {
/*  70 */       for (int i = 0; i < memberTypeCounter; i++) {
/*  71 */         int flags = memberTypes[i].getFlags();
/*     */ 
/*  73 */         if (((flags & 0x8) != 0) && ((flags & 0x2) == 0)) {
/*  74 */           extraFlags |= 1;
/*  75 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  80 */     return extraFlags;
/*     */   }
/*     */ 
/*     */   public static int getExtraFlags(TypeDeclaration typeDeclaration) {
/*  84 */     int extraFlags = 0;
/*     */ 
/*  86 */     if (typeDeclaration.enclosingType != null) {
/*  87 */       extraFlags |= 2;
/*     */     }
/*  89 */     TypeDeclaration[] memberTypes = typeDeclaration.memberTypes;
/*  90 */     int memberTypeCounter = memberTypes == null ? 0 : memberTypes.length;
/*  91 */     if (memberTypeCounter > 0) {
/*  92 */       for (int i = 0; i < memberTypeCounter; i++) {
/*  93 */         int modifiers = memberTypes[i].modifiers;
/*     */ 
/*  95 */         if (((modifiers & 0x8) != 0) && ((modifiers & 0x2) == 0)) {
/*  96 */           extraFlags |= 1;
/*  97 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 102 */     return extraFlags;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ExtraFlags
 * JD-Core Version:    0.6.0
 */