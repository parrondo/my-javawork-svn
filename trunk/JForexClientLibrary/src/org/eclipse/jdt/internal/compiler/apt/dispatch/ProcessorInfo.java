/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.processing.Processor;
/*     */ import javax.lang.model.SourceVersion;
/*     */ import javax.lang.model.element.Name;
/*     */ import javax.lang.model.element.TypeElement;
/*     */ 
/*     */ public class ProcessorInfo
/*     */ {
/*     */   final Processor _processor;
/*     */   final Set<String> _supportedOptions;
/*     */   final SourceVersion _supportedSourceVersion;
/*     */   private final Pattern _supportedAnnotationTypesPattern;
/*     */   private final boolean _supportsStar;
/*     */   private boolean _hasBeenCalled;
/*     */ 
/*     */   public ProcessorInfo(Processor p)
/*     */   {
/*  54 */     this._processor = p;
/*  55 */     this._hasBeenCalled = false;
/*  56 */     this._supportedSourceVersion = p.getSupportedSourceVersion();
/*  57 */     this._supportedOptions = p.getSupportedOptions();
/*  58 */     Set supportedAnnotationTypes = p.getSupportedAnnotationTypes();
/*     */ 
/*  60 */     boolean supportsStar = false;
/*  61 */     if ((supportedAnnotationTypes != null) && (!supportedAnnotationTypes.isEmpty())) {
/*  62 */       StringBuilder regex = new StringBuilder();
/*  63 */       Iterator iName = supportedAnnotationTypes.iterator();
/*     */       while (true) {
/*  65 */         String name = (String)iName.next();
/*  66 */         supportsStar |= "*".equals(name);
/*  67 */         String escapedName1 = name.replace(".", "\\.");
/*  68 */         String escapedName2 = escapedName1.replace("*", ".*");
/*  69 */         regex.append(escapedName2);
/*  70 */         if (!iName.hasNext()) {
/*     */           break;
/*     */         }
/*  73 */         regex.append('|');
/*     */       }
/*  75 */       this._supportedAnnotationTypesPattern = Pattern.compile(regex.toString());
/*     */     }
/*     */     else {
/*  78 */       this._supportedAnnotationTypesPattern = null;
/*     */     }
/*  80 */     this._supportsStar = supportsStar;
/*     */   }
/*     */ 
/*     */   public boolean computeSupportedAnnotations(Set<TypeElement> annotations, Set<TypeElement> result)
/*     */   {
/*  98 */     if ((annotations != null) && (!annotations.isEmpty()) && (this._supportedAnnotationTypesPattern != null)) {
/*  99 */       for (TypeElement annotation : annotations) {
/* 100 */         Matcher matcher = this._supportedAnnotationTypesPattern.matcher(annotation.getQualifiedName().toString());
/* 101 */         if (matcher.matches()) {
/* 102 */           result.add(annotation);
/*     */         }
/*     */       }
/*     */     }
/* 106 */     boolean call = (this._hasBeenCalled) || (this._supportsStar) || (!result.isEmpty());
/* 107 */     this._hasBeenCalled |= call;
/* 108 */     return call;
/*     */   }
/*     */ 
/*     */   public boolean supportsStar()
/*     */   {
/* 116 */     return this._supportsStar;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 127 */     this._hasBeenCalled = false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 132 */     return this._processor.getClass().hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 137 */     if (this == obj)
/* 138 */       return true;
/* 139 */     if (obj == null)
/* 140 */       return false;
/* 141 */     if (getClass() != obj.getClass())
/* 142 */       return false;
/* 143 */     ProcessorInfo other = (ProcessorInfo)obj;
/*     */ 
/* 145 */     return this._processor.getClass().equals(other._processor.getClass());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 152 */     return this._processor.getClass().getName();
/*     */   }
/*     */ 
/*     */   public String getSupportedAnnotationTypesAsString()
/*     */   {
/* 161 */     StringBuilder sb = new StringBuilder();
/* 162 */     sb.append('[');
/* 163 */     Iterator iAnnots = this._processor.getSupportedAnnotationTypes().iterator();
/* 164 */     boolean hasNext = iAnnots.hasNext();
/* 165 */     while (hasNext) {
/* 166 */       sb.append((String)iAnnots.next());
/* 167 */       hasNext = iAnnots.hasNext();
/* 168 */       if (hasNext) {
/* 169 */         sb.append(',');
/*     */       }
/*     */     }
/* 172 */     sb.append(']');
/* 173 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.ProcessorInfo
 * JD-Core Version:    0.6.0
 */