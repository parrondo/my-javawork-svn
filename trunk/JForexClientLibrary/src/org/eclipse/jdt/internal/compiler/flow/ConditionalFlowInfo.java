/*     */ package org.eclipse.jdt.internal.compiler.flow;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ 
/*     */ public class ConditionalFlowInfo extends FlowInfo
/*     */ {
/*     */   public FlowInfo initsWhenTrue;
/*     */   public FlowInfo initsWhenFalse;
/*     */ 
/*     */   ConditionalFlowInfo(FlowInfo initsWhenTrue, FlowInfo initsWhenFalse)
/*     */   {
/*  27 */     this.initsWhenTrue = initsWhenTrue;
/*  28 */     this.initsWhenFalse = initsWhenFalse;
/*     */   }
/*     */ 
/*     */   public FlowInfo addInitializationsFrom(FlowInfo otherInits)
/*     */   {
/*  33 */     this.initsWhenTrue.addInitializationsFrom(otherInits);
/*  34 */     this.initsWhenFalse.addInitializationsFrom(otherInits);
/*  35 */     return this;
/*     */   }
/*     */ 
/*     */   public FlowInfo addPotentialInitializationsFrom(FlowInfo otherInits)
/*     */   {
/*  40 */     this.initsWhenTrue.addPotentialInitializationsFrom(otherInits);
/*  41 */     this.initsWhenFalse.addPotentialInitializationsFrom(otherInits);
/*  42 */     return this;
/*     */   }
/*     */ 
/*     */   public FlowInfo asNegatedCondition()
/*     */   {
/*  47 */     FlowInfo extra = this.initsWhenTrue;
/*  48 */     this.initsWhenTrue = this.initsWhenFalse;
/*  49 */     this.initsWhenFalse = extra;
/*  50 */     return this;
/*     */   }
/*     */ 
/*     */   public FlowInfo copy()
/*     */   {
/*  55 */     return new ConditionalFlowInfo(this.initsWhenTrue.copy(), this.initsWhenFalse.copy());
/*     */   }
/*     */ 
/*     */   public FlowInfo initsWhenFalse()
/*     */   {
/*  60 */     return this.initsWhenFalse;
/*     */   }
/*     */ 
/*     */   public FlowInfo initsWhenTrue()
/*     */   {
/*  65 */     return this.initsWhenTrue;
/*     */   }
/*     */ 
/*     */   public boolean isDefinitelyAssigned(FieldBinding field)
/*     */   {
/*  71 */     return (this.initsWhenTrue.isDefinitelyAssigned(field)) && 
/*  71 */       (this.initsWhenFalse.isDefinitelyAssigned(field));
/*     */   }
/*     */ 
/*     */   public boolean isDefinitelyAssigned(LocalVariableBinding local)
/*     */   {
/*  77 */     return (this.initsWhenTrue.isDefinitelyAssigned(local)) && 
/*  77 */       (this.initsWhenFalse.isDefinitelyAssigned(local));
/*     */   }
/*     */ 
/*     */   public boolean isDefinitelyNonNull(LocalVariableBinding local)
/*     */   {
/*  82 */     return (this.initsWhenTrue.isDefinitelyNonNull(local)) && 
/*  82 */       (this.initsWhenFalse.isDefinitelyNonNull(local));
/*     */   }
/*     */ 
/*     */   public boolean isDefinitelyNull(LocalVariableBinding local)
/*     */   {
/*  87 */     return (this.initsWhenTrue.isDefinitelyNull(local)) && 
/*  87 */       (this.initsWhenFalse.isDefinitelyNull(local));
/*     */   }
/*     */ 
/*     */   public boolean isDefinitelyUnknown(LocalVariableBinding local)
/*     */   {
/*  92 */     return (this.initsWhenTrue.isDefinitelyUnknown(local)) && 
/*  92 */       (this.initsWhenFalse.isDefinitelyUnknown(local));
/*     */   }
/*     */ 
/*     */   public boolean isPotentiallyAssigned(FieldBinding field)
/*     */   {
/*  97 */     return (this.initsWhenTrue.isPotentiallyAssigned(field)) || 
/*  97 */       (this.initsWhenFalse.isPotentiallyAssigned(field));
/*     */   }
/*     */ 
/*     */   public boolean isPotentiallyAssigned(LocalVariableBinding local)
/*     */   {
/* 102 */     return (this.initsWhenTrue.isPotentiallyAssigned(local)) || 
/* 102 */       (this.initsWhenFalse.isPotentiallyAssigned(local));
/*     */   }
/*     */ 
/*     */   public boolean isPotentiallyNonNull(LocalVariableBinding local)
/*     */   {
/* 107 */     return (this.initsWhenTrue.isPotentiallyNonNull(local)) || 
/* 107 */       (this.initsWhenFalse.isPotentiallyNonNull(local));
/*     */   }
/*     */ 
/*     */   public boolean isPotentiallyNull(LocalVariableBinding local)
/*     */   {
/* 112 */     return (this.initsWhenTrue.isPotentiallyNull(local)) || 
/* 112 */       (this.initsWhenFalse.isPotentiallyNull(local));
/*     */   }
/*     */ 
/*     */   public boolean isPotentiallyUnknown(LocalVariableBinding local)
/*     */   {
/* 117 */     return (this.initsWhenTrue.isPotentiallyUnknown(local)) || 
/* 117 */       (this.initsWhenFalse.isPotentiallyUnknown(local));
/*     */   }
/*     */ 
/*     */   public boolean isProtectedNonNull(LocalVariableBinding local)
/*     */   {
/* 122 */     return (this.initsWhenTrue.isProtectedNonNull(local)) && 
/* 122 */       (this.initsWhenFalse.isProtectedNonNull(local));
/*     */   }
/*     */ 
/*     */   public boolean isProtectedNull(LocalVariableBinding local)
/*     */   {
/* 127 */     return (this.initsWhenTrue.isProtectedNull(local)) && 
/* 127 */       (this.initsWhenFalse.isProtectedNull(local));
/*     */   }
/*     */ 
/*     */   public void markAsComparedEqualToNonNull(LocalVariableBinding local) {
/* 131 */     this.initsWhenTrue.markAsComparedEqualToNonNull(local);
/* 132 */     this.initsWhenFalse.markAsComparedEqualToNonNull(local);
/*     */   }
/*     */ 
/*     */   public void markAsComparedEqualToNull(LocalVariableBinding local) {
/* 136 */     this.initsWhenTrue.markAsComparedEqualToNull(local);
/* 137 */     this.initsWhenFalse.markAsComparedEqualToNull(local);
/*     */   }
/*     */ 
/*     */   public void markAsDefinitelyAssigned(FieldBinding field) {
/* 141 */     this.initsWhenTrue.markAsDefinitelyAssigned(field);
/* 142 */     this.initsWhenFalse.markAsDefinitelyAssigned(field);
/*     */   }
/*     */ 
/*     */   public void markAsDefinitelyAssigned(LocalVariableBinding local) {
/* 146 */     this.initsWhenTrue.markAsDefinitelyAssigned(local);
/* 147 */     this.initsWhenFalse.markAsDefinitelyAssigned(local);
/*     */   }
/*     */ 
/*     */   public void markAsDefinitelyNonNull(LocalVariableBinding local) {
/* 151 */     this.initsWhenTrue.markAsDefinitelyNonNull(local);
/* 152 */     this.initsWhenFalse.markAsDefinitelyNonNull(local);
/*     */   }
/*     */ 
/*     */   public void markAsDefinitelyNull(LocalVariableBinding local) {
/* 156 */     this.initsWhenTrue.markAsDefinitelyNull(local);
/* 157 */     this.initsWhenFalse.markAsDefinitelyNull(local);
/*     */   }
/*     */ 
/*     */   public void markAsDefinitelyUnknown(LocalVariableBinding local) {
/* 161 */     this.initsWhenTrue.markAsDefinitelyUnknown(local);
/* 162 */     this.initsWhenFalse.markAsDefinitelyUnknown(local);
/*     */   }
/*     */ 
/*     */   public FlowInfo setReachMode(int reachMode) {
/* 166 */     if (reachMode == 0) {
/* 167 */       this.tagBits &= -2;
/*     */     }
/*     */     else {
/* 170 */       this.tagBits |= 1;
/*     */     }
/* 172 */     this.initsWhenTrue.setReachMode(reachMode);
/* 173 */     this.initsWhenFalse.setReachMode(reachMode);
/* 174 */     return this;
/*     */   }
/*     */ 
/*     */   public UnconditionalFlowInfo mergedWith(UnconditionalFlowInfo otherInits) {
/* 178 */     return unconditionalInits().mergedWith(otherInits);
/*     */   }
/*     */ 
/*     */   public UnconditionalFlowInfo nullInfoLessUnconditionalCopy() {
/* 182 */     return unconditionalInitsWithoutSideEffect()
/* 183 */       .nullInfoLessUnconditionalCopy();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 188 */     return "FlowInfo<true: " + this.initsWhenTrue.toString() + ", false: " + this.initsWhenFalse.toString() + ">";
/*     */   }
/*     */ 
/*     */   public FlowInfo safeInitsWhenTrue() {
/* 192 */     return this.initsWhenTrue;
/*     */   }
/*     */ 
/*     */   public UnconditionalFlowInfo unconditionalCopy() {
/* 196 */     return this.initsWhenTrue.unconditionalCopy()
/* 197 */       .mergedWith(this.initsWhenFalse.unconditionalInits());
/*     */   }
/*     */ 
/*     */   public UnconditionalFlowInfo unconditionalFieldLessCopy() {
/* 201 */     return this.initsWhenTrue.unconditionalFieldLessCopy()
/* 202 */       .mergedWith(this.initsWhenFalse.unconditionalFieldLessCopy());
/*     */   }
/*     */ 
/*     */   public UnconditionalFlowInfo unconditionalInits()
/*     */   {
/* 207 */     return this.initsWhenTrue.unconditionalInits()
/* 208 */       .mergedWith(this.initsWhenFalse.unconditionalInits());
/*     */   }
/*     */ 
/*     */   public UnconditionalFlowInfo unconditionalInitsWithoutSideEffect()
/*     */   {
/* 214 */     return this.initsWhenTrue.unconditionalCopy()
/* 215 */       .mergedWith(this.initsWhenFalse.unconditionalInits());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.ConditionalFlowInfo
 * JD-Core Version:    0.6.0
 */