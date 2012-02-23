/*    */ package org.eclipse.jdt.internal.compiler.classfmt;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
/*    */ 
/*    */ class MethodInfoWithParameterAnnotations extends MethodInfoWithAnnotations
/*    */ {
/*    */   private AnnotationInfo[][] parameterAnnotations;
/*    */ 
/*    */   MethodInfoWithParameterAnnotations(MethodInfo methodInfo, AnnotationInfo[] annotations, AnnotationInfo[][] parameterAnnotations)
/*    */   {
/* 19 */     super(methodInfo, annotations);
/* 20 */     this.parameterAnnotations = parameterAnnotations;
/*    */   }
/*    */ 
/*    */   public IBinaryAnnotation[] getParameterAnnotations(int index) {
/* 24 */     return this.parameterAnnotations[index];
/*    */   }
/*    */   protected void initialize() {
/* 27 */     int i = 0; for (int l = this.parameterAnnotations == null ? 0 : this.parameterAnnotations.length; i < l; i++) {
/* 28 */       AnnotationInfo[] infos = this.parameterAnnotations[i];
/* 29 */       int j = 0; for (int k = infos == null ? 0 : infos.length; j < k; j++)
/* 30 */         infos[j].initialize();
/*    */     }
/* 32 */     super.initialize();
/*    */   }
/*    */   protected void reset() {
/* 35 */     int i = 0; for (int l = this.parameterAnnotations == null ? 0 : this.parameterAnnotations.length; i < l; i++) {
/* 36 */       AnnotationInfo[] infos = this.parameterAnnotations[i];
/* 37 */       int j = 0; for (int k = infos == null ? 0 : infos.length; j < k; j++)
/* 38 */         infos[j].reset();
/*    */     }
/* 40 */     super.reset();
/*    */   }
/*    */   protected void toStringContent(StringBuffer buffer) {
/* 43 */     super.toStringContent(buffer);
/* 44 */     int i = 0; for (int l = this.parameterAnnotations == null ? 0 : this.parameterAnnotations.length; i < l; i++) {
/* 45 */       buffer.append("param" + (i - 1));
/* 46 */       buffer.append('\n');
/* 47 */       AnnotationInfo[] infos = this.parameterAnnotations[i];
/* 48 */       int j = 0; for (int k = infos == null ? 0 : infos.length; j < k; j++) {
/* 49 */         buffer.append(infos[j]);
/* 50 */         buffer.append('\n');
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithParameterAnnotations
 * JD-Core Version:    0.6.0
 */