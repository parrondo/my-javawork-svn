/*    */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*    */ 
/*    */ import javax.annotation.processing.Messager;
/*    */ import javax.lang.model.element.AnnotationMirror;
/*    */ import javax.lang.model.element.AnnotationValue;
/*    */ import javax.lang.model.element.Element;
/*    */ import javax.tools.Diagnostic.Kind;
/*    */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*    */ import org.eclipse.jdt.internal.compiler.batch.Main;
/*    */ 
/*    */ public class BatchMessagerImpl extends BaseMessagerImpl
/*    */   implements Messager
/*    */ {
/*    */   private final Main _compiler;
/*    */   private final BaseProcessingEnvImpl _processingEnv;
/*    */ 
/*    */   public BatchMessagerImpl(BaseProcessingEnvImpl processingEnv, Main compiler)
/*    */   {
/* 33 */     this._compiler = compiler;
/* 34 */     this._processingEnv = processingEnv;
/*    */   }
/*    */ 
/*    */   public void printMessage(Diagnostic.Kind kind, CharSequence msg)
/*    */   {
/* 42 */     printMessage(kind, msg, null, null, null);
/*    */   }
/*    */ 
/*    */   public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e)
/*    */   {
/* 50 */     printMessage(kind, msg, e, null, null);
/*    */   }
/*    */ 
/*    */   public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a)
/*    */   {
/* 59 */     printMessage(kind, msg, e, a, null);
/*    */   }
/*    */ 
/*    */   public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v)
/*    */   {
/* 69 */     if (kind == Diagnostic.Kind.ERROR) {
/* 70 */       this._processingEnv.setErrorRaised(true);
/*    */     }
/* 72 */     CategorizedProblem problem = createProblem(kind, msg, e, a, v);
/* 73 */     if (problem != null)
/* 74 */       this._compiler.addExtraProblems(problem);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.BatchMessagerImpl
 * JD-Core Version:    0.6.0
 */