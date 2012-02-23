/*    */ package org.eclipse.jdt.internal.compiler.classfmt;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
/*    */ 
/*    */ public class MethodInfoWithAnnotations extends MethodInfo
/*    */ {
/*    */   protected AnnotationInfo[] annotations;
/*    */ 
/*    */   MethodInfoWithAnnotations(MethodInfo methodInfo, AnnotationInfo[] annotations)
/*    */   {
/* 19 */     super(methodInfo.reference, methodInfo.constantPoolOffsets, methodInfo.structOffset);
/* 20 */     this.annotations = annotations;
/*    */ 
/* 22 */     this.accessFlags = methodInfo.accessFlags;
/* 23 */     this.attributeBytes = methodInfo.attributeBytes;
/* 24 */     this.descriptor = methodInfo.descriptor;
/* 25 */     this.exceptionNames = methodInfo.exceptionNames;
/* 26 */     this.name = methodInfo.name;
/* 27 */     this.signature = methodInfo.signature;
/* 28 */     this.signatureUtf8Offset = methodInfo.signatureUtf8Offset;
/* 29 */     this.tagBits = methodInfo.tagBits;
/*    */   }
/*    */   public IBinaryAnnotation[] getAnnotations() {
/* 32 */     return this.annotations;
/*    */   }
/*    */   protected void initialize() {
/* 35 */     int i = 0; for (int l = this.annotations == null ? 0 : this.annotations.length; i < l; i++)
/* 36 */       if (this.annotations[i] != null)
/* 37 */         this.annotations[i].initialize();
/* 38 */     super.initialize();
/*    */   }
/*    */   protected void reset() {
/* 41 */     int i = 0; for (int l = this.annotations == null ? 0 : this.annotations.length; i < l; i++)
/* 42 */       if (this.annotations[i] != null)
/* 43 */         this.annotations[i].reset();
/* 44 */     super.reset();
/*    */   }
/*    */   protected void toStringContent(StringBuffer buffer) {
/* 47 */     super.toStringContent(buffer);
/* 48 */     int i = 0; for (int l = this.annotations == null ? 0 : this.annotations.length; i < l; i++) {
/* 49 */       buffer.append(this.annotations[i]);
/* 50 */       buffer.append('\n');
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithAnnotations
 * JD-Core Version:    0.6.0
 */