/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ 
/*    */ public class ExtendedStringLiteral extends StringLiteral
/*    */ {
/*    */   public ExtendedStringLiteral(StringLiteral str, CharLiteral character)
/*    */   {
/* 23 */     super(str.source, str.sourceStart, str.sourceEnd, str.lineNumber);
/* 24 */     extendWith(character);
/*    */   }
/*    */ 
/*    */   public ExtendedStringLiteral(StringLiteral str1, StringLiteral str2)
/*    */   {
/* 32 */     super(str1.source, str1.sourceStart, str1.sourceEnd, str1.lineNumber);
/* 33 */     extendWith(str2);
/*    */   }
/*    */ 
/*    */   public ExtendedStringLiteral extendWith(CharLiteral lit)
/*    */   {
/* 42 */     int length = this.source.length;
/* 43 */     System.arraycopy(this.source, 0, this.source = new char[length + 1], 0, length);
/* 44 */     this.source[length] = lit.value;
/*    */ 
/* 46 */     this.sourceEnd = lit.sourceEnd;
/* 47 */     return this;
/*    */   }
/*    */ 
/*    */   public ExtendedStringLiteral extendWith(StringLiteral lit)
/*    */   {
/* 56 */     int length = this.source.length;
/* 57 */     System.arraycopy(
/* 58 */       this.source, 
/* 59 */       0, 
/* 60 */       this.source = new char[length + lit.source.length], 
/* 61 */       0, 
/* 62 */       length);
/* 63 */     System.arraycopy(lit.source, 0, this.source, length, lit.source.length);
/*    */ 
/* 65 */     this.sourceEnd = lit.sourceEnd;
/* 66 */     return this;
/*    */   }
/*    */ 
/*    */   public StringBuffer printExpression(int indent, StringBuffer output)
/*    */   {
/* 71 */     return output.append("ExtendedStringLiteral{").append(this.source).append('}');
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*    */   {
/* 76 */     visitor.visit(this, scope);
/* 77 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ExtendedStringLiteral
 * JD-Core Version:    0.6.0
 */