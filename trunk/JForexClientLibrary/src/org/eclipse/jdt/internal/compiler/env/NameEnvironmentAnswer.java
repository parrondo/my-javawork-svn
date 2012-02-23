/*     */ package org.eclipse.jdt.internal.compiler.env;
/*     */ 
/*     */ public class NameEnvironmentAnswer
/*     */ {
/*     */   IBinaryType binaryType;
/*     */   ICompilationUnit compilationUnit;
/*     */   ISourceType[] sourceTypes;
/*     */   AccessRestriction accessRestriction;
/*     */ 
/*     */   public NameEnvironmentAnswer(IBinaryType binaryType, AccessRestriction accessRestriction)
/*     */   {
/*  22 */     this.binaryType = binaryType;
/*  23 */     this.accessRestriction = accessRestriction;
/*     */   }
/*     */ 
/*     */   public NameEnvironmentAnswer(ICompilationUnit compilationUnit, AccessRestriction accessRestriction) {
/*  27 */     this.compilationUnit = compilationUnit;
/*  28 */     this.accessRestriction = accessRestriction;
/*     */   }
/*     */ 
/*     */   public NameEnvironmentAnswer(ISourceType[] sourceTypes, AccessRestriction accessRestriction) {
/*  32 */     this.sourceTypes = sourceTypes;
/*  33 */     this.accessRestriction = accessRestriction;
/*     */   }
/*     */ 
/*     */   public AccessRestriction getAccessRestriction()
/*     */   {
/*  39 */     return this.accessRestriction;
/*     */   }
/*     */ 
/*     */   public IBinaryType getBinaryType()
/*     */   {
/*  46 */     return this.binaryType;
/*     */   }
/*     */ 
/*     */   public ICompilationUnit getCompilationUnit()
/*     */   {
/*  54 */     return this.compilationUnit;
/*     */   }
/*     */ 
/*     */   public ISourceType[] getSourceTypes()
/*     */   {
/*  65 */     return this.sourceTypes;
/*     */   }
/*     */ 
/*     */   public boolean isBinaryType()
/*     */   {
/*  72 */     return this.binaryType != null;
/*     */   }
/*     */ 
/*     */   public boolean isCompilationUnit()
/*     */   {
/*  79 */     return this.compilationUnit != null;
/*     */   }
/*     */ 
/*     */   public boolean isSourceType()
/*     */   {
/*  86 */     return this.sourceTypes != null;
/*     */   }
/*     */ 
/*     */   public boolean ignoreIfBetter() {
/*  90 */     return (this.accessRestriction != null) && (this.accessRestriction.ignoreIfBetter());
/*     */   }
/*     */ 
/*     */   public boolean isBetter(NameEnvironmentAnswer otherAnswer)
/*     */   {
/*  99 */     if (otherAnswer == null) return true;
/* 100 */     if (this.accessRestriction == null) return true;
/*     */ 
/* 102 */     return (otherAnswer.accessRestriction != null) && 
/* 102 */       (this.accessRestriction.getProblemId() < otherAnswer.accessRestriction.getProblemId());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer
 * JD-Core Version:    0.6.0
 */