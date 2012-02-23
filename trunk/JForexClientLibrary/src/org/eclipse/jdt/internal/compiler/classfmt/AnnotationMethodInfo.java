/*    */ package org.eclipse.jdt.internal.compiler.classfmt;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
/*    */ 
/*    */ public class AnnotationMethodInfo extends MethodInfo
/*    */ {
/* 17 */   protected Object defaultValue = null;
/*    */ 
/*    */   public static MethodInfo createAnnotationMethod(byte[] classFileBytes, int[] offsets, int offset) {
/* 20 */     MethodInfo methodInfo = new MethodInfo(classFileBytes, offsets, offset);
/* 21 */     int attributesCount = methodInfo.u2At(6);
/* 22 */     int readOffset = 8;
/* 23 */     AnnotationInfo[] annotations = (AnnotationInfo[])null;
/* 24 */     Object defaultValue = null;
/* 25 */     for (int i = 0; i < attributesCount; i++)
/*    */     {
/* 27 */       int utf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset)] - methodInfo.structOffset;
/* 28 */       char[] attributeName = methodInfo.utf8At(utf8Offset + 3, methodInfo.u2At(utf8Offset + 1));
/* 29 */       if (attributeName.length > 0) {
/* 30 */         switch (attributeName[0]) {
/*    */         case 'A':
/* 32 */           if (!CharOperation.equals(attributeName, AttributeNamesConstants.AnnotationDefaultName)) {
/*    */             break;
/*    */           }
/* 35 */           AnnotationInfo info = 
/* 36 */             new AnnotationInfo(methodInfo.reference, methodInfo.constantPoolOffsets, readOffset + 6 + methodInfo.structOffset);
/* 37 */           defaultValue = info.decodeDefaultValue();
/*    */ 
/* 39 */           break;
/*    */         case 'S':
/* 41 */           if (!CharOperation.equals(AttributeNamesConstants.SignatureName, attributeName)) break;
/* 42 */           methodInfo.signatureUtf8Offset = (methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset + 6)] - methodInfo.structOffset);
/* 43 */           break;
/*    */         case 'R':
/* 45 */           AnnotationInfo[] methodAnnotations = (AnnotationInfo[])null;
/* 46 */           if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName))
/* 47 */             methodAnnotations = decodeMethodAnnotations(readOffset, true, methodInfo);
/* 48 */           else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
/* 49 */             methodAnnotations = decodeMethodAnnotations(readOffset, false, methodInfo);
/*    */           }
/* 51 */           if (methodAnnotations == null) break;
/* 52 */           if (annotations == null) {
/* 53 */             annotations = methodAnnotations;
/*    */           } else {
/* 55 */             int length = annotations.length;
/* 56 */             AnnotationInfo[] newAnnotations = new AnnotationInfo[length + methodAnnotations.length];
/* 57 */             System.arraycopy(annotations, 0, newAnnotations, 0, length);
/* 58 */             System.arraycopy(methodAnnotations, 0, newAnnotations, length, methodAnnotations.length);
/* 59 */             annotations = newAnnotations;
/*    */           }
/*    */         }
/*    */ 
/*    */       }
/*    */ 
/* 65 */       readOffset = (int)(readOffset + (
/* 65 */         6L + methodInfo.u4At(readOffset + 2)));
/*    */     }
/* 67 */     methodInfo.attributeBytes = readOffset;
/*    */ 
/* 69 */     if (defaultValue != null) {
/* 70 */       if (annotations != null) {
/* 71 */         return new AnnotationMethodInfoWithAnnotations(methodInfo, defaultValue, annotations);
/*    */       }
/* 73 */       return new AnnotationMethodInfo(methodInfo, defaultValue);
/*    */     }
/* 75 */     if (annotations != null)
/* 76 */       return new MethodInfoWithAnnotations(methodInfo, annotations);
/* 77 */     return methodInfo;
/*    */   }
/*    */ 
/*    */   AnnotationMethodInfo(MethodInfo methodInfo, Object defaultValue) {
/* 81 */     super(methodInfo.reference, methodInfo.constantPoolOffsets, methodInfo.structOffset);
/* 82 */     this.defaultValue = defaultValue;
/*    */ 
/* 84 */     this.accessFlags = methodInfo.accessFlags;
/* 85 */     this.attributeBytes = methodInfo.attributeBytes;
/* 86 */     this.descriptor = methodInfo.descriptor;
/* 87 */     this.exceptionNames = methodInfo.exceptionNames;
/* 88 */     this.name = methodInfo.name;
/* 89 */     this.signature = methodInfo.signature;
/* 90 */     this.signatureUtf8Offset = methodInfo.signatureUtf8Offset;
/* 91 */     this.tagBits = methodInfo.tagBits;
/*    */   }
/*    */   public Object getDefaultValue() {
/* 94 */     return this.defaultValue;
/*    */   }
/*    */   protected void toStringContent(StringBuffer buffer) {
/* 97 */     super.toStringContent(buffer);
/* 98 */     if (this.defaultValue != null) {
/* 99 */       buffer.append(" default ");
/* 100 */       if ((this.defaultValue instanceof Object[])) {
/* 101 */         buffer.append('{');
/* 102 */         Object[] elements = (Object[])this.defaultValue;
/* 103 */         int i = 0; for (int len = elements.length; i < len; i++) {
/* 104 */           if (i > 0)
/* 105 */             buffer.append(", ");
/* 106 */           buffer.append(elements[i]);
/*    */         }
/* 108 */         buffer.append('}');
/*    */       } else {
/* 110 */         buffer.append(this.defaultValue);
/*    */       }
/* 112 */       buffer.append('\n');
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.AnnotationMethodInfo
 * JD-Core Version:    0.6.0
 */