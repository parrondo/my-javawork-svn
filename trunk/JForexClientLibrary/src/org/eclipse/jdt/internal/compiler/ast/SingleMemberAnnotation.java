/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
/*    */ 
/*    */ public class SingleMemberAnnotation extends Annotation
/*    */ {
/*    */   public Expression memberValue;
/*    */   private MemberValuePair[] singlePairs;
/*    */ 
/*    */   public SingleMemberAnnotation(TypeReference type, int sourceStart)
/*    */   {
/* 25 */     this.type = type;
/* 26 */     this.sourceStart = sourceStart;
/* 27 */     this.sourceEnd = type.sourceEnd;
/*    */   }
/*    */ 
/*    */   public ElementValuePair[] computeElementValuePairs() {
/* 31 */     return new ElementValuePair[] { memberValuePairs()[0].compilerElementPair };
/*    */   }
/*    */ 
/*    */   public MemberValuePair[] memberValuePairs()
/*    */   {
/* 38 */     if (this.singlePairs == null) {
/* 39 */       this.singlePairs = 
/* 40 */         new MemberValuePair[] { 
/* 41 */         new MemberValuePair(VALUE, this.memberValue.sourceStart, this.memberValue.sourceEnd, this.memberValue) };
/*    */     }
/*    */ 
/* 44 */     return this.singlePairs;
/*    */   }
/*    */ 
/*    */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 48 */     super.printExpression(indent, output);
/* 49 */     output.append('(');
/* 50 */     this.memberValue.printExpression(indent, output);
/* 51 */     return output.append(')');
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 55 */     if (visitor.visit(this, scope)) {
/* 56 */       if (this.type != null) {
/* 57 */         this.type.traverse(visitor, scope);
/*    */       }
/* 59 */       if (this.memberValue != null) {
/* 60 */         this.memberValue.traverse(visitor, scope);
/*    */       }
/*    */     }
/* 63 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation
 * JD-Core Version:    0.6.0
 */