/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*    */ import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ 
/*    */ public class FalseLiteral extends MagicLiteral
/*    */ {
/* 22 */   static final char[] source = { 'f', 'a', 'l', 's', 'e' };
/*    */ 
/*    */   public FalseLiteral(int s, int e) {
/* 25 */     super(s, e);
/*    */   }
/*    */   public void computeConstant() {
/* 28 */     this.constant = BooleanConstant.fromValue(false);
/*    */   }
/*    */ 
/*    */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*    */   {
/* 38 */     int pc = codeStream.position;
/* 39 */     if (valueRequired) {
/* 40 */       codeStream.generateConstant(this.constant, this.implicitConversion);
/*    */     }
/* 42 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*    */   }
/*    */ 
/*    */   public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*    */   {
/* 48 */     int pc = codeStream.position;
/* 49 */     if ((valueRequired) && 
/* 50 */       (falseLabel != null))
/*    */     {
/* 52 */       if (trueLabel == null) {
/* 53 */         codeStream.goto_(falseLabel);
/*    */       }
/*    */     }
/*    */ 
/* 57 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*    */   }
/*    */   public TypeBinding literalType(BlockScope scope) {
/* 60 */     return TypeBinding.BOOLEAN;
/*    */   }
/*    */ 
/*    */   public char[] source()
/*    */   {
/* 66 */     return source;
/*    */   }
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 69 */     visitor.visit(this, scope);
/* 70 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.FalseLiteral
 * JD-Core Version:    0.6.0
 */