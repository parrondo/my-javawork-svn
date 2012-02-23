/*     */ package org.eclipse.jdt.internal.compiler.flow;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ 
/*     */ public abstract class FlowInfo
/*     */ {
/*     */   public int tagBits;
/*     */   public static final int REACHABLE = 0;
/*     */   public static final int UNREACHABLE = 1;
/*     */   public static final int NULL_FLAG_MASK = 2;
/*     */   public static final int UNKNOWN = 0;
/*     */   public static final int NULL = 1;
/*     */   public static final int NON_NULL = -1;
/*  29 */   public static final UnconditionalFlowInfo DEAD_END = new UnconditionalFlowInfo();
/*     */ 
/*  30 */   static { DEAD_END.tagBits = 1;
/*     */   }
/*     */ 
/*     */   public abstract FlowInfo addInitializationsFrom(FlowInfo paramFlowInfo);
/*     */ 
/*     */   public abstract FlowInfo addPotentialInitializationsFrom(FlowInfo paramFlowInfo);
/*     */ 
/*     */   public FlowInfo asNegatedCondition()
/*     */   {
/*  57 */     return this;
/*     */   }
/*     */ 
/*     */   public static FlowInfo conditional(FlowInfo initsWhenTrue, FlowInfo initsWhenFalse) {
/*  61 */     if (initsWhenTrue == initsWhenFalse) return initsWhenTrue;
/*     */ 
/*  63 */     return new ConditionalFlowInfo(initsWhenTrue, initsWhenFalse);
/*     */   }
/*     */ 
/*     */   public boolean cannotBeDefinitelyNullOrNonNull(LocalVariableBinding local)
/*     */   {
/*  79 */     return (isPotentiallyUnknown(local)) || (
/*  79 */       (isPotentiallyNonNull(local)) && (isPotentiallyNull(local)));
/*     */   }
/*     */ 
/*     */   public boolean cannotBeNull(LocalVariableBinding local)
/*     */   {
/*  89 */     return (isDefinitelyNonNull(local)) || (isProtectedNonNull(local));
/*     */   }
/*     */ 
/*     */   public boolean canOnlyBeNull(LocalVariableBinding local)
/*     */   {
/*  99 */     return (isDefinitelyNull(local)) || (isProtectedNull(local));
/*     */   }
/*     */ 
/*     */   public abstract FlowInfo copy();
/*     */ 
/*     */   public static UnconditionalFlowInfo initial(int maxFieldCount)
/*     */   {
/* 109 */     UnconditionalFlowInfo info = new UnconditionalFlowInfo();
/* 110 */     info.maxFieldCount = maxFieldCount;
/* 111 */     return info;
/*     */   }
/*     */ 
/*     */   public abstract FlowInfo initsWhenFalse();
/*     */ 
/*     */   public abstract FlowInfo initsWhenTrue();
/*     */ 
/*     */   public abstract boolean isDefinitelyAssigned(FieldBinding paramFieldBinding);
/*     */ 
/*     */   public abstract boolean isDefinitelyAssigned(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract boolean isDefinitelyNonNull(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract boolean isDefinitelyNull(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract boolean isDefinitelyUnknown(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract boolean isPotentiallyAssigned(FieldBinding paramFieldBinding);
/*     */ 
/*     */   public abstract boolean isPotentiallyAssigned(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract boolean isPotentiallyNonNull(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract boolean isPotentiallyNull(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract boolean isPotentiallyUnknown(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract boolean isProtectedNonNull(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract boolean isProtectedNull(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract void markAsComparedEqualToNonNull(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract void markAsComparedEqualToNull(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract void markAsDefinitelyAssigned(FieldBinding paramFieldBinding);
/*     */ 
/*     */   public abstract void markAsDefinitelyNonNull(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract void markAsDefinitelyNull(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract void markAsDefinitelyAssigned(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public abstract void markAsDefinitelyUnknown(LocalVariableBinding paramLocalVariableBinding);
/*     */ 
/*     */   public static UnconditionalFlowInfo mergedOptimizedBranches(FlowInfo initsWhenTrue, boolean isOptimizedTrue, FlowInfo initsWhenFalse, boolean isOptimizedFalse, boolean allowFakeDeadBranch)
/*     */   {
/*     */     UnconditionalFlowInfo mergedInfo;
/*     */     UnconditionalFlowInfo mergedInfo;
/* 266 */     if (isOptimizedTrue)
/*     */     {
/*     */       UnconditionalFlowInfo mergedInfo;
/* 267 */       if ((initsWhenTrue == DEAD_END) && (allowFakeDeadBranch)) {
/* 268 */         mergedInfo = initsWhenFalse.setReachMode(1)
/* 269 */           .unconditionalInits();
/*     */       }
/*     */       else
/* 272 */         mergedInfo = 
/* 273 */           initsWhenTrue.addPotentialInitializationsFrom(initsWhenFalse
/* 274 */           .nullInfoLessUnconditionalCopy())
/* 275 */           .unconditionalInits();
/*     */     }
/*     */     else
/*     */     {
/*     */       UnconditionalFlowInfo mergedInfo;
/* 278 */       if (isOptimizedFalse)
/*     */       {
/*     */         UnconditionalFlowInfo mergedInfo;
/* 279 */         if ((initsWhenFalse == DEAD_END) && (allowFakeDeadBranch)) {
/* 280 */           mergedInfo = initsWhenTrue.setReachMode(1)
/* 281 */             .unconditionalInits();
/*     */         }
/*     */         else
/* 284 */           mergedInfo = 
/* 285 */             initsWhenFalse.addPotentialInitializationsFrom(initsWhenTrue
/* 286 */             .nullInfoLessUnconditionalCopy())
/* 287 */             .unconditionalInits();
/*     */       }
/*     */       else
/*     */       {
/* 291 */         mergedInfo = initsWhenTrue
/* 292 */           .mergedWith(initsWhenFalse.unconditionalInits());
/*     */       }
/*     */     }
/* 294 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public int reachMode()
/*     */   {
/* 304 */     return this.tagBits & 0x1;
/*     */   }
/*     */ 
/*     */   public abstract FlowInfo safeInitsWhenTrue();
/*     */ 
/*     */   public abstract FlowInfo setReachMode(int paramInt);
/*     */ 
/*     */   public abstract UnconditionalFlowInfo mergedWith(UnconditionalFlowInfo paramUnconditionalFlowInfo);
/*     */ 
/*     */   public abstract UnconditionalFlowInfo nullInfoLessUnconditionalCopy();
/*     */ 
/*     */   public String toString()
/*     */   {
/* 349 */     if (this == DEAD_END) {
/* 350 */       return "FlowInfo.DEAD_END";
/*     */     }
/* 352 */     return super.toString();
/*     */   }
/*     */ 
/*     */   public abstract UnconditionalFlowInfo unconditionalCopy();
/*     */ 
/*     */   public abstract UnconditionalFlowInfo unconditionalFieldLessCopy();
/*     */ 
/*     */   public abstract UnconditionalFlowInfo unconditionalInits();
/*     */ 
/*     */   public abstract UnconditionalFlowInfo unconditionalInitsWithoutSideEffect();
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.FlowInfo
 * JD-Core Version:    0.6.0
 */