/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class ArrayQualifiedTypeReference extends QualifiedTypeReference
/*    */ {
/*    */   int dimensions;
/*    */ 
/*    */   public ArrayQualifiedTypeReference(char[][] sources, int dim, long[] poss)
/*    */   {
/* 23 */     super(sources, poss);
/* 24 */     this.dimensions = dim;
/*    */   }
/*    */ 
/*    */   public int dimensions()
/*    */   {
/* 29 */     return this.dimensions;
/*    */   }
/*    */ 
/*    */   public char[][] getParameterizedTypeName()
/*    */   {
/* 36 */     int dim = this.dimensions;
/* 37 */     char[] dimChars = new char[dim * 2];
/* 38 */     for (int i = 0; i < dim; i++) {
/* 39 */       int index = i * 2;
/* 40 */       dimChars[index] = '[';
/* 41 */       dimChars[(index + 1)] = ']';
/*    */     }
/* 43 */     int length = this.tokens.length;
/* 44 */     char[][] qParamName = new char[length][];
/* 45 */     System.arraycopy(this.tokens, 0, qParamName, 0, length - 1);
/* 46 */     qParamName[(length - 1)] = CharOperation.concat(this.tokens[(length - 1)], dimChars);
/* 47 */     return qParamName;
/*    */   }
/*    */ 
/*    */   protected TypeBinding getTypeBinding(Scope scope)
/*    */   {
/* 52 */     if (this.resolvedType != null)
/* 53 */       return this.resolvedType;
/* 54 */     if (this.dimensions > 255) {
/* 55 */       scope.problemReporter().tooManyDimensions(this);
/*    */     }
/* 57 */     LookupEnvironment env = scope.environment();
/*    */     try {
/* 59 */       env.missingClassFileLocation = this;
/* 60 */       TypeBinding leafComponentType = super.getTypeBinding(scope);
/* 61 */       ArrayBinding localArrayBinding = this.resolvedType = scope.createArrayType(leafComponentType, this.dimensions);
/*    */       return localArrayBinding;
/*    */     } catch (AbortCompilation e) {
/* 63 */       e.updateContext(this, scope.referenceCompilationUnit().compilationResult);
/* 64 */       throw e;
/*    */     } finally {
/* 66 */       env.missingClassFileLocation = null;
/* 67 */     }throw localObject;
/*    */   }
/*    */ 
/*    */   public StringBuffer printExpression(int indent, StringBuffer output)
/*    */   {
/* 72 */     super.printExpression(indent, output);
/* 73 */     if ((this.bits & 0x4000) != 0) {
/* 74 */       for (int i = 0; i < this.dimensions - 1; i++) {
/* 75 */         output.append("[]");
/*    */       }
/* 77 */       output.append("...");
/*    */     } else {
/* 79 */       for (int i = 0; i < this.dimensions; i++) {
/* 80 */         output.append("[]");
/*    */       }
/*    */     }
/* 83 */     return output;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*    */   {
/* 88 */     visitor.visit(this, scope);
/* 89 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, ClassScope scope)
/*    */   {
/* 94 */     visitor.visit(this, scope);
/* 95 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference
 * JD-Core Version:    0.6.0
 */