/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ 
/*    */ public class MarkerAnnotation extends Annotation
/*    */ {
/*    */   public MarkerAnnotation(TypeReference type, int sourceStart)
/*    */   {
/* 25 */     this.type = type;
/* 26 */     this.sourceStart = sourceStart;
/* 27 */     this.sourceEnd = type.sourceEnd;
/*    */   }
/*    */ 
/*    */   public MemberValuePair[] memberValuePairs()
/*    */   {
/* 34 */     return NoValuePairs;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 38 */     if ((visitor.visit(this, scope)) && 
/* 39 */       (this.type != null)) {
/* 40 */       this.type.traverse(visitor, scope);
/*    */     }
/*    */ 
/* 43 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation
 * JD-Core Version:    0.6.0
 */