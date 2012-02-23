/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ public class UnresolvedAnnotationBinding extends AnnotationBinding
/*    */ {
/*    */   private LookupEnvironment env;
/* 15 */   private boolean typeUnresolved = true;
/*    */ 
/*    */   UnresolvedAnnotationBinding(ReferenceBinding type, ElementValuePair[] pairs, LookupEnvironment env) {
/* 18 */     super(type, pairs);
/* 19 */     this.env = env;
/*    */   }
/*    */ 
/*    */   public ReferenceBinding getAnnotationType() {
/* 23 */     if (this.typeUnresolved) {
/* 24 */       this.type = ((ReferenceBinding)BinaryTypeBinding.resolveType(this.type, this.env, false));
/*    */ 
/* 26 */       this.typeUnresolved = false;
/*    */     }
/* 28 */     return this.type;
/*    */   }
/*    */ 
/*    */   public ElementValuePair[] getElementValuePairs() {
/* 32 */     if (this.env != null) {
/* 33 */       if (this.typeUnresolved) {
/* 34 */         getAnnotationType();
/*    */       }
/*    */ 
/* 37 */       int i = this.pairs.length;
/*    */       do { ElementValuePair pair = this.pairs[i];
/* 39 */         MethodBinding[] methods = this.type.getMethods(pair.getName());
/*    */ 
/* 41 */         if ((methods != null) && (methods.length == 1)) {
/* 42 */           pair.setMethodBinding(methods[0]);
/*    */         }
/* 44 */         Object value = pair.getValue();
/* 45 */         if ((value instanceof UnresolvedReferenceBinding))
/* 46 */           pair.setValue(((UnresolvedReferenceBinding)value)
/* 47 */             .resolve(this.env, false));
/* 37 */         i--; } while (i >= 0);
/*    */ 
/* 52 */       this.env = null;
/*    */     }
/* 54 */     return this.pairs;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.UnresolvedAnnotationBinding
 * JD-Core Version:    0.6.0
 */