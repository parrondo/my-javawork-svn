/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ 
/*     */ public class AnnotationBinding
/*     */ {
/*     */   ReferenceBinding type;
/*     */   ElementValuePair[] pairs;
/*     */ 
/*     */   public static AnnotationBinding[] addStandardAnnotations(AnnotationBinding[] recordedAnnotations, long annotationTagBits, LookupEnvironment env)
/*     */   {
/*  35 */     int count = 0;
/*  36 */     if ((annotationTagBits & 0x0) != 0L)
/*  37 */       count++;
/*  38 */     if ((annotationTagBits & 0x0) != 0L)
/*  39 */       count++;
/*  40 */     if ((annotationTagBits & 0x0) != 0L)
/*  41 */       count++;
/*  42 */     if ((annotationTagBits & 0x0) != 0L)
/*  43 */       count++;
/*  44 */     if ((annotationTagBits & 0x0) != 0L)
/*  45 */       count++;
/*  46 */     if ((annotationTagBits & 0x0) != 0L)
/*  47 */       count++;
/*  48 */     if ((annotationTagBits & 0x0) != 0L)
/*  49 */       count++;
/*  50 */     if (count == 0) {
/*  51 */       return recordedAnnotations;
/*     */     }
/*  53 */     int index = recordedAnnotations.length;
/*  54 */     AnnotationBinding[] result = new AnnotationBinding[index + count];
/*  55 */     System.arraycopy(recordedAnnotations, 0, result, 0, index);
/*  56 */     if ((annotationTagBits & 0x0) != 0L)
/*  57 */       result[(index++)] = buildTargetAnnotation(annotationTagBits, env);
/*  58 */     if ((annotationTagBits & 0x0) != 0L)
/*  59 */       result[(index++)] = buildRetentionAnnotation(annotationTagBits, env);
/*  60 */     if ((annotationTagBits & 0x0) != 0L)
/*  61 */       result[(index++)] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_DEPRECATED, env);
/*  62 */     if ((annotationTagBits & 0x0) != 0L)
/*  63 */       result[(index++)] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_ANNOTATION_DOCUMENTED, env);
/*  64 */     if ((annotationTagBits & 0x0) != 0L)
/*  65 */       result[(index++)] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_ANNOTATION_INHERITED, env);
/*  66 */     if ((annotationTagBits & 0x0) != 0L)
/*  67 */       result[(index++)] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, env);
/*  68 */     if ((annotationTagBits & 0x0) != 0L)
/*  69 */       result[(index++)] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_SUPPRESSWARNINGS, env);
/*  70 */     return result;
/*     */   }
/*     */ 
/*     */   private static AnnotationBinding buildMarkerAnnotation(char[][] compoundName, LookupEnvironment env) {
/*  74 */     ReferenceBinding type = env.getResolvedType(compoundName, null);
/*  75 */     return env.createAnnotation(type, Binding.NO_ELEMENT_VALUE_PAIRS);
/*     */   }
/*     */ 
/*     */   private static AnnotationBinding buildRetentionAnnotation(long bits, LookupEnvironment env) {
/*  79 */     ReferenceBinding retentionPolicy = 
/*  80 */       env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_RETENTIONPOLICY, 
/*  81 */       null);
/*  82 */     Object value = null;
/*  83 */     if ((bits & 0x0) == 52776558133248L)
/*  84 */       value = retentionPolicy.getField(TypeConstants.UPPER_RUNTIME, true);
/*  85 */     else if ((bits & 0x0) != 0L)
/*  86 */       value = retentionPolicy.getField(TypeConstants.UPPER_CLASS, true);
/*  87 */     else if ((bits & 0x0) != 0L) {
/*  88 */       value = retentionPolicy.getField(TypeConstants.UPPER_SOURCE, true);
/*     */     }
/*  90 */     return env.createAnnotation(
/*  91 */       env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_RETENTION, null), 
/*  92 */       new ElementValuePair[] { 
/*  93 */       new ElementValuePair(TypeConstants.VALUE, value, null) });
/*     */   }
/*     */ 
/*     */   private static AnnotationBinding buildTargetAnnotation(long bits, LookupEnvironment env)
/*     */   {
/*  98 */     ReferenceBinding target = env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_TARGET, null);
/*  99 */     if ((bits & 0x0) != 0L) {
/* 100 */       return new AnnotationBinding(target, Binding.NO_ELEMENT_VALUE_PAIRS);
/*     */     }
/* 102 */     int arraysize = 0;
/* 103 */     if ((bits & 0x0) != 0L)
/* 104 */       arraysize++;
/* 105 */     if ((bits & 0x0) != 0L)
/* 106 */       arraysize++;
/* 107 */     if ((bits & 0x0) != 0L)
/* 108 */       arraysize++;
/* 109 */     if ((bits & 0x0) != 0L)
/* 110 */       arraysize++;
/* 111 */     if ((bits & 0x0) != 0L)
/* 112 */       arraysize++;
/* 113 */     if ((bits & 0x0) != 0L)
/* 114 */       arraysize++;
/* 115 */     if ((bits & 0x0) != 0L)
/* 116 */       arraysize++;
/* 117 */     if ((bits & 0x0) != 0L)
/* 118 */       arraysize++;
/* 119 */     Object[] value = new Object[arraysize];
/* 120 */     if (arraysize > 0) {
/* 121 */       ReferenceBinding elementType = env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_ELEMENTTYPE, null);
/* 122 */       int index = 0;
/* 123 */       if ((bits & 0x0) != 0L)
/* 124 */         value[(index++)] = elementType.getField(TypeConstants.UPPER_ANNOTATION_TYPE, true);
/* 125 */       if ((bits & 0x0) != 0L)
/* 126 */         value[(index++)] = elementType.getField(TypeConstants.UPPER_CONSTRUCTOR, true);
/* 127 */       if ((bits & 0x0) != 0L)
/* 128 */         value[(index++)] = elementType.getField(TypeConstants.UPPER_FIELD, true);
/* 129 */       if ((bits & 0x0) != 0L)
/* 130 */         value[(index++)] = elementType.getField(TypeConstants.UPPER_LOCAL_VARIABLE, true);
/* 131 */       if ((bits & 0x0) != 0L)
/* 132 */         value[(index++)] = elementType.getField(TypeConstants.UPPER_METHOD, true);
/* 133 */       if ((bits & 0x0) != 0L)
/* 134 */         value[(index++)] = elementType.getField(TypeConstants.UPPER_PACKAGE, true);
/* 135 */       if ((bits & 0x0) != 0L)
/* 136 */         value[(index++)] = elementType.getField(TypeConstants.UPPER_PARAMETER, true);
/* 137 */       if ((bits & 0x0) != 0L)
/* 138 */         value[(index++)] = elementType.getField(TypeConstants.TYPE, true);
/*     */     }
/* 140 */     return env.createAnnotation(
/* 141 */       target, 
/* 142 */       new ElementValuePair[] { 
/* 143 */       new ElementValuePair(TypeConstants.VALUE, value, null) });
/*     */   }
/*     */ 
/*     */   AnnotationBinding(ReferenceBinding type, ElementValuePair[] pairs)
/*     */   {
/* 148 */     this.type = type;
/* 149 */     this.pairs = pairs;
/*     */   }
/*     */ 
/*     */   AnnotationBinding(Annotation astAnnotation) {
/* 153 */     this((ReferenceBinding)astAnnotation.resolvedType, astAnnotation.computeElementValuePairs());
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(char[] recipientKey)
/*     */   {
/* 162 */     char[] typeKey = this.type.computeUniqueKey(false);
/* 163 */     int recipientKeyLength = recipientKey.length;
/* 164 */     char[] uniqueKey = new char[recipientKeyLength + 1 + typeKey.length];
/* 165 */     System.arraycopy(recipientKey, 0, uniqueKey, 0, recipientKeyLength);
/* 166 */     uniqueKey[recipientKeyLength] = '@';
/* 167 */     System.arraycopy(typeKey, 0, uniqueKey, recipientKeyLength + 1, typeKey.length);
/* 168 */     return uniqueKey;
/*     */   }
/*     */ 
/*     */   public ReferenceBinding getAnnotationType() {
/* 172 */     return this.type;
/*     */   }
/*     */ 
/*     */   public ElementValuePair[] getElementValuePairs() {
/* 176 */     return this.pairs;
/*     */   }
/*     */ 
/*     */   public static void setMethodBindings(ReferenceBinding type, ElementValuePair[] pairs)
/*     */   {
/* 181 */     int i = pairs.length;
/*     */     do { ElementValuePair pair = pairs[i];
/* 183 */       MethodBinding[] methods = type.getMethods(pair.getName());
/*     */ 
/* 185 */       if ((methods != null) && (methods.length == 1))
/* 186 */         pair.setMethodBinding(methods[0]);
/* 181 */       i--; } while (i >= 0);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 191 */     StringBuffer buffer = new StringBuffer(5);
/* 192 */     buffer.append('@').append(this.type.sourceName);
/* 193 */     if ((this.pairs != null) && (this.pairs.length > 0)) {
/* 194 */       buffer.append("{ ");
/* 195 */       int i = 0; for (int max = this.pairs.length; i < max; i++) {
/* 196 */         if (i > 0) buffer.append(", ");
/* 197 */         buffer.append(this.pairs[i]);
/*     */       }
/* 199 */       buffer.append('}');
/*     */     }
/* 201 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding
 * JD-Core Version:    0.6.0
 */