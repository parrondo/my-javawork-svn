/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ 
/*    */ public class MissingTypeBinding extends BinaryTypeBinding
/*    */ {
/*    */   public MissingTypeBinding(PackageBinding packageBinding, char[][] compoundName, LookupEnvironment environment)
/*    */   {
/* 28 */     this.compoundName = compoundName;
/* 29 */     computeId();
/* 30 */     this.tagBits |= 131264L;
/* 31 */     this.environment = environment;
/* 32 */     this.fPackage = packageBinding;
/* 33 */     this.fileName = CharOperation.concatWith(compoundName, '/');
/* 34 */     this.sourceName = compoundName[(compoundName.length - 1)];
/* 35 */     this.modifiers = 1;
/* 36 */     this.superclass = null;
/* 37 */     this.superInterfaces = Binding.NO_SUPERINTERFACES;
/* 38 */     this.typeVariables = Binding.NO_TYPE_VARIABLES;
/* 39 */     this.memberTypes = Binding.NO_MEMBER_TYPES;
/* 40 */     this.fields = Binding.NO_FIELDS;
/* 41 */     this.methods = Binding.NO_METHODS;
/*    */   }
/*    */ 
/*    */   public List collectMissingTypes(List missingTypes)
/*    */   {
/* 48 */     if (missingTypes == null)
/* 49 */       missingTypes = new ArrayList(5);
/* 50 */     else if (missingTypes.contains(this)) {
/* 51 */       return missingTypes;
/*    */     }
/* 53 */     missingTypes.add(this);
/* 54 */     return missingTypes;
/*    */   }
/*    */ 
/*    */   public int problemId()
/*    */   {
/* 62 */     return 1;
/*    */   }
/*    */ 
/*    */   void setMissingSuperclass(ReferenceBinding missingSuperclass)
/*    */   {
/* 71 */     this.superclass = missingSuperclass;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 75 */     return "[MISSING:" + new String(CharOperation.concatWith(this.compoundName, '.')) + "]";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding
 * JD-Core Version:    0.6.0
 */