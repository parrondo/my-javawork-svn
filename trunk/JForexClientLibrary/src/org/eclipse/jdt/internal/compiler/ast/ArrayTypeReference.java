/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class ArrayTypeReference extends SingleTypeReference
/*    */ {
/*    */   public int dimensions;
/*    */   public int originalSourceEnd;
/*    */ 
/*    */   public ArrayTypeReference(char[] source, int dimensions, long pos)
/*    */   {
/* 32 */     super(source, pos);
/* 33 */     this.originalSourceEnd = this.sourceEnd;
/* 34 */     this.dimensions = dimensions;
/*    */   }
/*    */ 
/*    */   public int dimensions()
/*    */   {
/* 39 */     return this.dimensions;
/*    */   }
/*    */ 
/*    */   public char[][] getParameterizedTypeName()
/*    */   {
/* 45 */     int dim = this.dimensions;
/* 46 */     char[] dimChars = new char[dim * 2];
/* 47 */     for (int i = 0; i < dim; i++) {
/* 48 */       int index = i * 2;
/* 49 */       dimChars[index] = '[';
/* 50 */       dimChars[(index + 1)] = ']';
/*    */     }
/* 52 */     return new char[][] { CharOperation.concat(this.token, dimChars) };
/*    */   }
/*    */ 
/*    */   protected TypeBinding getTypeBinding(Scope scope) {
/* 56 */     if (this.resolvedType != null) {
/* 57 */       return this.resolvedType;
/*    */     }
/* 59 */     if (this.dimensions > 255) {
/* 60 */       scope.problemReporter().tooManyDimensions(this);
/*    */     }
/* 62 */     TypeBinding leafComponentType = scope.getType(this.token);
/* 63 */     return scope.createArrayType(leafComponentType, this.dimensions);
/*    */   }
/*    */ 
/*    */   public StringBuffer printExpression(int indent, StringBuffer output)
/*    */   {
/* 69 */     super.printExpression(indent, output);
/* 70 */     if ((this.bits & 0x4000) != 0) {
/* 71 */       for (int i = 0; i < this.dimensions - 1; i++) {
/* 72 */         output.append("[]");
/*    */       }
/* 74 */       output.append("...");
/*    */     } else {
/* 76 */       for (int i = 0; i < this.dimensions; i++) {
/* 77 */         output.append("[]");
/*    */       }
/*    */     }
/* 80 */     return output;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*    */   {
/* 85 */     visitor.visit(this, scope);
/* 86 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, ClassScope scope)
/*    */   {
/* 91 */     visitor.visit(this, scope);
/* 92 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference
 * JD-Core Version:    0.6.0
 */