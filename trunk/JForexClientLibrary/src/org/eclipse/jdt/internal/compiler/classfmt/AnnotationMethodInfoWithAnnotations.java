/*    */ package org.eclipse.jdt.internal.compiler.classfmt;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
/*    */ 
/*    */ public class AnnotationMethodInfoWithAnnotations extends AnnotationMethodInfo
/*    */ {
/*    */   private AnnotationInfo[] annotations;
/*    */ 
/*    */   AnnotationMethodInfoWithAnnotations(MethodInfo methodInfo, Object defaultValue, AnnotationInfo[] annotations)
/*    */   {
/* 19 */     super(methodInfo, defaultValue);
/* 20 */     this.annotations = annotations;
/*    */   }
/*    */   public IBinaryAnnotation[] getAnnotations() {
/* 23 */     return this.annotations;
/*    */   }
/*    */   protected void initialize() {
/* 26 */     int i = 0; for (int l = this.annotations == null ? 0 : this.annotations.length; i < l; i++)
/* 27 */       if (this.annotations[i] != null)
/* 28 */         this.annotations[i].initialize();
/* 29 */     super.initialize();
/*    */   }
/*    */   protected void reset() {
/* 32 */     int i = 0; for (int l = this.annotations == null ? 0 : this.annotations.length; i < l; i++)
/* 33 */       if (this.annotations[i] != null)
/* 34 */         this.annotations[i].reset();
/* 35 */     super.reset();
/*    */   }
/*    */   protected void toStringContent(StringBuffer buffer) {
/* 38 */     super.toStringContent(buffer);
/* 39 */     int i = 0; for (int l = this.annotations == null ? 0 : this.annotations.length; i < l; i++) {
/* 40 */       buffer.append(this.annotations[i]);
/* 41 */       buffer.append('\n');
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.AnnotationMethodInfoWithAnnotations
 * JD-Core Version:    0.6.0
 */