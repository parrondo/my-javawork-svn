/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class SingleTypeReference extends TypeReference
/*    */ {
/*    */   public char[] token;
/*    */ 
/*    */   public SingleTypeReference(char[] source, long pos)
/*    */   {
/* 24 */     this.token = source;
/* 25 */     this.sourceStart = (int)(pos >>> 32);
/* 26 */     this.sourceEnd = (int)(pos & 0xFFFFFFFF);
/*    */   }
/*    */ 
/*    */   public TypeReference copyDims(int dim)
/*    */   {
/* 34 */     return new ArrayTypeReference(this.token, dim, (this.sourceStart << 32) + this.sourceEnd);
/*    */   }
/*    */ 
/*    */   public char[] getLastToken() {
/* 38 */     return this.token;
/*    */   }
/*    */   protected TypeBinding getTypeBinding(Scope scope) {
/* 41 */     if (this.resolvedType != null) {
/* 42 */       return this.resolvedType;
/*    */     }
/* 44 */     this.resolvedType = scope.getType(this.token);
/*    */ 
/* 46 */     if ((scope.kind == 3) && (this.resolvedType.isValidBinding()) && 
/* 47 */       (((ClassScope)scope).detectHierarchyCycle(this.resolvedType, this)))
/* 48 */       return null;
/* 49 */     return this.resolvedType;
/*    */   }
/*    */ 
/*    */   public char[][] getTypeName() {
/* 53 */     return new char[][] { this.token };
/*    */   }
/*    */ 
/*    */   public StringBuffer printExpression(int indent, StringBuffer output)
/*    */   {
/* 58 */     return output.append(this.token);
/*    */   }
/*    */ 
/*    */   public TypeBinding resolveTypeEnclosing(BlockScope scope, ReferenceBinding enclosingType) {
/* 62 */     TypeBinding memberType = this.resolvedType = scope.getMemberType(this.token, enclosingType);
/* 63 */     boolean hasError = false;
/* 64 */     if (!memberType.isValidBinding()) {
/* 65 */       hasError = true;
/* 66 */       scope.problemReporter().invalidEnclosingType(this, memberType, enclosingType);
/* 67 */       memberType = ((ReferenceBinding)memberType).closestMatch();
/* 68 */       if (memberType == null) {
/* 69 */         return null;
/*    */       }
/*    */     }
/* 72 */     if (isTypeUseDeprecated(memberType, scope))
/* 73 */       scope.problemReporter().deprecatedType(memberType, this);
/* 74 */     memberType = scope.environment().convertToRawType(memberType, false);
/* 75 */     if ((memberType.isRawType()) && 
/* 76 */       ((this.bits & 0x40000000) == 0) && 
/* 77 */       (scope.compilerOptions().getSeverity(536936448) != -1)) {
/* 78 */       scope.problemReporter().rawTypeReference(this, memberType);
/*    */     }
/* 80 */     if (hasError)
/*    */     {
/* 82 */       return memberType;
/*    */     }
/* 84 */     return this.resolvedType = memberType;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 88 */     visitor.visit(this, scope);
/* 89 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 93 */     visitor.visit(this, scope);
/* 94 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 * JD-Core Version:    0.6.0
 */