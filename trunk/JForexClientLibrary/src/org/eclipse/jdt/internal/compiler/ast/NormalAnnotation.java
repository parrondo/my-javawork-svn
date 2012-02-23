/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
/*    */ 
/*    */ public class NormalAnnotation extends Annotation
/*    */ {
/*    */   public MemberValuePair[] memberValuePairs;
/*    */ 
/*    */   public NormalAnnotation(TypeReference type, int sourceStart)
/*    */   {
/* 24 */     this.type = type;
/* 25 */     this.sourceStart = sourceStart;
/* 26 */     this.sourceEnd = type.sourceEnd;
/*    */   }
/*    */ 
/*    */   public ElementValuePair[] computeElementValuePairs() {
/* 30 */     int numberOfPairs = this.memberValuePairs == null ? 0 : this.memberValuePairs.length;
/* 31 */     if (numberOfPairs == 0) {
/* 32 */       return Binding.NO_ELEMENT_VALUE_PAIRS;
/*    */     }
/* 34 */     ElementValuePair[] pairs = new ElementValuePair[numberOfPairs];
/* 35 */     for (int i = 0; i < numberOfPairs; i++)
/* 36 */       pairs[i] = this.memberValuePairs[i].compilerElementPair;
/* 37 */     return pairs;
/*    */   }
/*    */ 
/*    */   public MemberValuePair[] memberValuePairs()
/*    */   {
/* 44 */     return this.memberValuePairs == null ? NoValuePairs : this.memberValuePairs;
/*    */   }
/*    */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 47 */     super.printExpression(indent, output);
/* 48 */     output.append('(');
/* 49 */     if (this.memberValuePairs != null) {
/* 50 */       int i = 0; for (int max = this.memberValuePairs.length; i < max; i++) {
/* 51 */         if (i > 0) {
/* 52 */           output.append(',');
/*    */         }
/* 54 */         this.memberValuePairs[i].print(indent, output);
/*    */       }
/*    */     }
/* 57 */     output.append(')');
/* 58 */     return output;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 62 */     if (visitor.visit(this, scope)) {
/* 63 */       if (this.type != null) {
/* 64 */         this.type.traverse(visitor, scope);
/*    */       }
/* 66 */       if (this.memberValuePairs != null) {
/* 67 */         int memberValuePairsLength = this.memberValuePairs.length;
/* 68 */         for (int i = 0; i < memberValuePairsLength; i++)
/* 69 */           this.memberValuePairs[i].traverse(visitor, scope);
/*    */       }
/*    */     }
/* 72 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.NormalAnnotation
 * JD-Core Version:    0.6.0
 */