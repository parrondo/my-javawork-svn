/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ 
/*    */ public class StringLiteralConcatenation extends StringLiteral
/*    */ {
/*    */   private static final int INITIAL_SIZE = 5;
/*    */   public Expression[] literals;
/*    */   public int counter;
/*    */ 
/*    */   public StringLiteralConcatenation(StringLiteral str1, StringLiteral str2)
/*    */   {
/* 27 */     super(str1.sourceStart, str1.sourceEnd);
/* 28 */     this.source = str1.source;
/* 29 */     this.literals = new StringLiteral[5];
/* 30 */     this.counter = 0;
/* 31 */     this.literals[(this.counter++)] = str1;
/* 32 */     extendsWith(str2);
/*    */   }
/*    */ 
/*    */   public StringLiteralConcatenation extendsWith(StringLiteral lit)
/*    */   {
/* 39 */     this.sourceEnd = lit.sourceEnd;
/* 40 */     int literalsLength = this.literals.length;
/* 41 */     if (this.counter == literalsLength)
/*    */     {
/* 43 */       System.arraycopy(this.literals, 0, this.literals = new StringLiteral[literalsLength + 5], 0, literalsLength);
/*    */     }
/*    */ 
/* 46 */     int length = this.source.length;
/* 47 */     System.arraycopy(
/* 48 */       this.source, 
/* 49 */       0, 
/* 50 */       this.source = new char[length + lit.source.length], 
/* 51 */       0, 
/* 52 */       length);
/* 53 */     System.arraycopy(lit.source, 0, this.source, length, lit.source.length);
/* 54 */     this.literals[(this.counter++)] = lit;
/* 55 */     return this;
/*    */   }
/*    */ 
/*    */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 59 */     output.append("StringLiteralConcatenation{");
/* 60 */     int i = 0; for (int max = this.counter; i < max; i++) {
/* 61 */       this.literals[i].printExpression(indent, output);
/* 62 */       output.append("+\n");
/*    */     }
/* 64 */     return output.append('}');
/*    */   }
/*    */ 
/*    */   public char[] source() {
/* 68 */     return this.source;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 72 */     if (visitor.visit(this, scope)) {
/* 73 */       int i = 0; for (int max = this.counter; i < max; i++) {
/* 74 */         this.literals[i].traverse(visitor, scope);
/*    */       }
/*    */     }
/* 77 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation
 * JD-Core Version:    0.6.0
 */