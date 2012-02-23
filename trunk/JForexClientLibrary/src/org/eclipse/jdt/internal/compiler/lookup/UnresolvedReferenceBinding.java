/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class UnresolvedReferenceBinding extends ReferenceBinding
/*    */ {
/*    */   ReferenceBinding resolvedType;
/*    */   TypeBinding[] wrappers;
/*    */ 
/*    */   UnresolvedReferenceBinding(char[][] compoundName, PackageBinding packageBinding)
/*    */   {
/* 21 */     this.compoundName = compoundName;
/* 22 */     this.sourceName = compoundName[(compoundName.length - 1)];
/* 23 */     this.fPackage = packageBinding;
/* 24 */     this.wrappers = null;
/*    */   }
/*    */   void addWrapper(TypeBinding wrapper, LookupEnvironment environment) {
/* 27 */     if (this.resolvedType != null)
/*    */     {
/* 30 */       wrapper.swapUnresolved(this, this.resolvedType, environment);
/* 31 */       return;
/*    */     }
/* 33 */     if (this.wrappers == null) {
/* 34 */       this.wrappers = new TypeBinding[] { wrapper };
/*    */     } else {
/* 36 */       int length = this.wrappers.length;
/* 37 */       System.arraycopy(this.wrappers, 0, this.wrappers = new TypeBinding[length + 1], 0, length);
/* 38 */       this.wrappers[length] = wrapper;
/*    */     }
/*    */   }
/*    */ 
/*    */   public String debugName() {
/* 42 */     return toString();
/*    */   }
/*    */   ReferenceBinding resolve(LookupEnvironment environment, boolean convertGenericToRawType) {
/* 45 */     ReferenceBinding targetType = this.resolvedType;
/* 46 */     if (targetType == null) {
/* 47 */       targetType = this.fPackage.getType0(this.compoundName[(this.compoundName.length - 1)]);
/* 48 */       if (targetType == this) {
/* 49 */         targetType = environment.askForType(this.compoundName);
/*    */       }
/* 51 */       if ((targetType == null) || (targetType == this))
/*    */       {
/* 53 */         if ((this.tagBits & 0x80) == 0L) {
/* 54 */           environment.problemReporter.isClassPathCorrect(
/* 55 */             this.compoundName, 
/* 56 */             environment.unitBeingCompleted, 
/* 57 */             environment.missingClassFileLocation);
/*    */         }
/*    */ 
/* 60 */         targetType = environment.createMissingType(null, this.compoundName);
/*    */       }
/* 62 */       setResolvedType(targetType, environment);
/*    */     }
/* 64 */     if (convertGenericToRawType) {
/* 65 */       targetType = (ReferenceBinding)environment.convertUnresolvedBinaryToRawType(targetType);
/*    */     }
/* 67 */     return targetType;
/*    */   }
/*    */   void setResolvedType(ReferenceBinding targetType, LookupEnvironment environment) {
/* 70 */     if (this.resolvedType == targetType) return;
/*    */ 
/* 73 */     this.resolvedType = targetType;
/*    */ 
/* 76 */     if (this.wrappers != null) {
/* 77 */       int i = 0; for (int l = this.wrappers.length; i < l; i++)
/* 78 */         this.wrappers[i].swapUnresolved(this, targetType, environment); 
/*    */     }
/* 79 */     environment.updateCaches(this, targetType);
/*    */   }
/*    */   public String toString() {
/* 82 */     return "Unresolved type " + (this.compoundName != null ? CharOperation.toString(this.compoundName) : "UNNAMED");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding
 * JD-Core Version:    0.6.0
 */