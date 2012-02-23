/*    */ package org.eclipse.jdt.internal.compiler.classfmt;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
/*    */ 
/*    */ public final class FieldInfoWithAnnotation extends FieldInfo
/*    */ {
/*    */   private AnnotationInfo[] annotations;
/*    */ 
/*    */   FieldInfoWithAnnotation(FieldInfo info, AnnotationInfo[] annos)
/*    */   {
/* 17 */     super(info.reference, info.constantPoolOffsets, info.structOffset);
/* 18 */     this.accessFlags = info.accessFlags;
/* 19 */     this.attributeBytes = info.attributeBytes;
/* 20 */     this.constant = info.constant;
/* 21 */     this.constantPoolOffsets = info.constantPoolOffsets;
/* 22 */     this.descriptor = info.descriptor;
/* 23 */     this.name = info.name;
/* 24 */     this.signature = info.signature;
/* 25 */     this.signatureUtf8Offset = info.signatureUtf8Offset;
/* 26 */     this.tagBits = info.tagBits;
/* 27 */     this.wrappedConstantValue = info.wrappedConstantValue;
/* 28 */     this.annotations = annos;
/*    */   }
/*    */   public IBinaryAnnotation[] getAnnotations() {
/* 31 */     return this.annotations;
/*    */   }
/*    */   protected void initialize() {
/* 34 */     int i = 0; for (int max = this.annotations.length; i < max; i++)
/* 35 */       this.annotations[i].initialize();
/* 36 */     super.initialize();
/*    */   }
/*    */   protected void reset() {
/* 39 */     if (this.annotations != null) {
/* 40 */       int i = 0; for (int max = this.annotations.length; i < max; i++)
/* 41 */         this.annotations[i].reset(); 
/*    */     }
/* 42 */     super.reset();
/*    */   }
/*    */   public String toString() {
/* 45 */     StringBuffer buffer = new StringBuffer(getClass().getName());
/* 46 */     if (this.annotations != null) {
/* 47 */       buffer.append('\n');
/* 48 */       for (int i = 0; i < this.annotations.length; i++) {
/* 49 */         buffer.append(this.annotations[i]);
/* 50 */         buffer.append('\n');
/*    */       }
/*    */     }
/* 53 */     toStringContent(buffer);
/* 54 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.FieldInfoWithAnnotation
 * JD-Core Version:    0.6.0
 */