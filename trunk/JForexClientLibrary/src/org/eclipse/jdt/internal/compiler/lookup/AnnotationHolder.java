/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ public class AnnotationHolder
/*    */ {
/*    */   AnnotationBinding[] annotations;
/*    */ 
/*    */   static AnnotationHolder storeAnnotations(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations, Object defaultValue, LookupEnvironment optionalEnv)
/*    */   {
/* 17 */     if (parameterAnnotations != null) {
/* 18 */       boolean isEmpty = true;
/* 19 */       int i = parameterAnnotations.length;
/*    */       do { if ((parameterAnnotations[i] != null) && (parameterAnnotations[i].length > 0))
/* 21 */           isEmpty = false;
/* 19 */         if (!isEmpty) break; i--; } while (i >= 0);
/*    */ 
/* 22 */       if (isEmpty) {
/* 23 */         parameterAnnotations = (AnnotationBinding[][])null;
/*    */       }
/*    */     }
/* 26 */     if (defaultValue != null)
/* 27 */       return new AnnotationMethodHolder(annotations, parameterAnnotations, defaultValue, optionalEnv);
/* 28 */     if (parameterAnnotations != null)
/* 29 */       return new MethodHolder(annotations, parameterAnnotations);
/* 30 */     return new AnnotationHolder().setAnnotations(annotations);
/*    */   }
/*    */ 
/*    */   AnnotationBinding[] getAnnotations() {
/* 34 */     return this.annotations;
/*    */   }
/*    */   Object getDefaultValue() {
/* 37 */     return null;
/*    */   }
/*    */   public AnnotationBinding[][] getParameterAnnotations() {
/* 40 */     return null;
/*    */   }
/*    */   AnnotationBinding[] getParameterAnnotations(int paramIndex) {
/* 43 */     return Binding.NO_ANNOTATIONS;
/*    */   }
/*    */   AnnotationHolder setAnnotations(AnnotationBinding[] annotations) {
/* 46 */     if ((annotations == null) || (annotations.length == 0)) {
/* 47 */       return null;
/*    */     }
/* 49 */     this.annotations = annotations;
/* 50 */     return this;
/*    */   }
/*    */ 
/*    */   static class AnnotationMethodHolder extends AnnotationHolder.MethodHolder
/*    */   {
/*    */     Object defaultValue;
/*    */     LookupEnvironment env;
/*    */ 
/*    */     AnnotationMethodHolder(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations, Object defaultValue, LookupEnvironment optionalEnv)
/*    */     {
/* 79 */       super(parameterAnnotations);
/* 80 */       this.defaultValue = defaultValue;
/* 81 */       this.env = optionalEnv;
/*    */     }
/*    */     Object getDefaultValue() {
/* 84 */       if ((this.defaultValue instanceof UnresolvedReferenceBinding)) {
/* 85 */         if (this.env == null)
/* 86 */           throw new IllegalStateException();
/* 87 */         this.defaultValue = ((UnresolvedReferenceBinding)this.defaultValue).resolve(this.env, false);
/*    */       }
/* 89 */       return this.defaultValue;
/*    */     }
/*    */   }
/*    */ 
/*    */   static class MethodHolder extends AnnotationHolder
/*    */   {
/*    */     AnnotationBinding[][] parameterAnnotations;
/*    */ 
/*    */     MethodHolder(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations)
/*    */     {
/* 58 */       setAnnotations(annotations);
/* 59 */       this.parameterAnnotations = parameterAnnotations;
/*    */     }
/*    */     public AnnotationBinding[][] getParameterAnnotations() {
/* 62 */       return this.parameterAnnotations;
/*    */     }
/*    */     AnnotationBinding[] getParameterAnnotations(int paramIndex) {
/* 65 */       AnnotationBinding[] result = this.parameterAnnotations == null ? null : this.parameterAnnotations[paramIndex];
/* 66 */       return result == null ? Binding.NO_ANNOTATIONS : result;
/*    */     }
/*    */     AnnotationHolder setAnnotations(AnnotationBinding[] annotations) {
/* 69 */       this.annotations = ((annotations == null) || (annotations.length == 0) ? Binding.NO_ANNOTATIONS : annotations);
/* 70 */       return this;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.AnnotationHolder
 * JD-Core Version:    0.6.0
 */