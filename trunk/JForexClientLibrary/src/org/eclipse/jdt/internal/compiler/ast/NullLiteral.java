/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*    */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ 
/*    */ public class NullLiteral extends MagicLiteral
/*    */ {
/* 21 */   static final char[] source = { 'n', 'u', 'l', 'l' };
/*    */ 
/*    */   public NullLiteral(int s, int e)
/*    */   {
/* 25 */     super(s, e);
/*    */   }
/*    */ 
/*    */   public void computeConstant()
/*    */   {
/* 30 */     this.constant = Constant.NotAConstant;
/*    */   }
/*    */ 
/*    */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*    */   {
/* 41 */     int pc = codeStream.position;
/* 42 */     if (valueRequired) {
/* 43 */       codeStream.aconst_null();
/* 44 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*    */     }
/* 46 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*    */   }
/*    */   public TypeBinding literalType(BlockScope scope) {
/* 49 */     return TypeBinding.NULL;
/*    */   }
/*    */ 
/*    */   public int nullStatus(FlowInfo flowInfo) {
/* 53 */     return 1;
/*    */   }
/*    */ 
/*    */   public Object reusableJSRTarget() {
/* 57 */     return TypeBinding.NULL;
/*    */   }
/*    */ 
/*    */   public char[] source() {
/* 61 */     return source;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 65 */     visitor.visit(this, scope);
/* 66 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.NullLiteral
 * JD-Core Version:    0.6.0
 */