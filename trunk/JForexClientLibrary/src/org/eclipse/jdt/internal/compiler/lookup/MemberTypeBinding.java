/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ 
/*    */ public final class MemberTypeBinding extends NestedTypeBinding
/*    */ {
/*    */   public MemberTypeBinding(char[][] compoundName, ClassScope scope, SourceTypeBinding enclosingType)
/*    */   {
/* 17 */     super(compoundName, scope, enclosingType);
/* 18 */     this.tagBits |= 2060L;
/*    */   }
/*    */   void checkSyntheticArgsAndFields() {
/* 21 */     if (isStatic()) return;
/* 22 */     if (isInterface()) return;
/* 23 */     addSyntheticArgumentAndField(this.enclosingType);
/*    */   }
/*    */ 
/*    */   public char[] constantPoolName()
/*    */   {
/* 31 */     if (this.constantPoolName != null) {
/* 32 */       return this.constantPoolName;
/*    */     }
/* 34 */     return this.constantPoolName = CharOperation.concat(enclosingType().constantPoolName(), this.sourceName, '$');
/*    */   }
/*    */ 
/*    */   public void initializeDeprecatedAnnotationTagBits()
/*    */   {
/* 41 */     if ((this.tagBits & 0x0) == 0L) {
/* 42 */       super.initializeDeprecatedAnnotationTagBits();
/* 43 */       if ((this.tagBits & 0x0) == 0L)
/*    */       {
/*    */         ReferenceBinding enclosing;
/* 46 */         if (((enclosing = enclosingType()).tagBits & 0x0) == 0L) {
/* 47 */           enclosing.initializeDeprecatedAnnotationTagBits();
/*    */         }
/* 49 */         if (enclosing.isViewedAsDeprecated())
/* 50 */           this.modifiers |= 2097152;
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 56 */     return "Member type : " + new String(sourceName()) + " " + super.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.MemberTypeBinding
 * JD-Core Version:    0.6.0
 */