/*     */ package org.eclipse.jdt.internal.compiler;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.IProblem;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Assignment;
/*     */ import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Block;
/*     */ import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CastExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Clinit;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.DoStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
/*     */ import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ExtendedStringLiteral;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ForStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.IfStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Initializer;
/*     */ import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Javadoc;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocArgumentExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocArrayQualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocArraySingleTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocImplicitTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MessageSend;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
/*     */ import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
/*     */ import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SuperReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ThisReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TryStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*     */ import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Wildcard;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ 
/*     */ public abstract class ASTVisitor
/*     */ {
/*     */   public void acceptProblem(IProblem problem)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(AllocationExpression allocationExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(AND_AND_Expression and_and_Expression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(AnnotationMethodDeclaration annotationTypeDeclaration, ClassScope classScope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(Argument argument, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(Argument argument, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ArrayAllocationExpression arrayAllocationExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ArrayInitializer arrayInitializer, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ArrayReference arrayReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ArrayTypeReference arrayTypeReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ArrayTypeReference arrayTypeReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(AssertStatement assertStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(Assignment assignment, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(BinaryExpression binaryExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(Block block, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(BreakStatement breakStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(CaseStatement caseStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(CastExpression castExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(CharLiteral charLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ClassLiteralAccess classLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(Clinit clinit, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(CompilationUnitDeclaration compilationUnitDeclaration, CompilationUnitScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(CompoundAssignment compoundAssignment, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ConditionalExpression conditionalExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ConstructorDeclaration constructorDeclaration, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ContinueStatement continueStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(DoStatement doStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(DoubleLiteral doubleLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(EmptyStatement emptyStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(EqualExpression equalExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ExplicitConstructorCall explicitConstructor, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ExtendedStringLiteral extendedStringLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(FalseLiteral falseLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(FieldDeclaration fieldDeclaration, MethodScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(FieldReference fieldReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(FieldReference fieldReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(FloatLiteral floatLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ForeachStatement forStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ForStatement forStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(IfStatement ifStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ImportReference importRef, CompilationUnitScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(Initializer initializer, MethodScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(InstanceOfExpression instanceOfExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(IntLiteral intLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(Javadoc javadoc, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(Javadoc javadoc, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocAllocationExpression expression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocAllocationExpression expression, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocArgumentExpression expression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocArgumentExpression expression, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocArrayQualifiedTypeReference typeRef, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocArrayQualifiedTypeReference typeRef, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocArraySingleTypeReference typeRef, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocArraySingleTypeReference typeRef, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocFieldReference fieldRef, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocFieldReference fieldRef, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocImplicitTypeReference implicitTypeReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocImplicitTypeReference implicitTypeReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocMessageSend messageSend, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocMessageSend messageSend, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocQualifiedTypeReference typeRef, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocQualifiedTypeReference typeRef, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocReturnStatement statement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocReturnStatement statement, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocSingleNameReference argument, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocSingleNameReference argument, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocSingleTypeReference typeRef, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(JavadocSingleTypeReference typeRef, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(LabeledStatement labeledStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(LocalDeclaration localDeclaration, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(LongLiteral longLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(MarkerAnnotation annotation, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(MemberValuePair pair, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(MessageSend messageSend, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(MethodDeclaration methodDeclaration, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(StringLiteralConcatenation literal, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(NormalAnnotation annotation, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(NullLiteral nullLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(OR_OR_Expression or_or_Expression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(PostfixExpression postfixExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(PrefixExpression prefixExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(QualifiedAllocationExpression qualifiedAllocationExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(QualifiedNameReference qualifiedNameReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(QualifiedNameReference qualifiedNameReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(QualifiedSuperReference qualifiedSuperReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(QualifiedSuperReference qualifiedSuperReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(QualifiedThisReference qualifiedThisReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(QualifiedThisReference qualifiedThisReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(QualifiedTypeReference qualifiedTypeReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(QualifiedTypeReference qualifiedTypeReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ReturnStatement returnStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(SingleMemberAnnotation annotation, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(SingleNameReference singleNameReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(SingleNameReference singleNameReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(SingleTypeReference singleTypeReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(SingleTypeReference singleTypeReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(StringLiteral stringLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(SuperReference superReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(SwitchStatement switchStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(SynchronizedStatement synchronizedStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ThisReference thisReference, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ThisReference thisReference, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(ThrowStatement throwStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(TrueLiteral trueLiteral, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(TryStatement tryStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(TypeDeclaration localTypeDeclaration, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(TypeDeclaration memberTypeDeclaration, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(TypeDeclaration typeDeclaration, CompilationUnitScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(TypeParameter typeParameter, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(TypeParameter typeParameter, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(UnaryExpression unaryExpression, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(WhileStatement whileStatement, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(Wildcard wildcard, BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endVisit(Wildcard wildcard, ClassScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean visit(AllocationExpression allocationExpression, BlockScope scope)
/*     */   {
/* 462 */     return true;
/*     */   }
/*     */   public boolean visit(AND_AND_Expression and_and_Expression, BlockScope scope) {
/* 465 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(AnnotationMethodDeclaration annotationTypeDeclaration, ClassScope classScope)
/*     */   {
/* 470 */     return true;
/*     */   }
/*     */   public boolean visit(Argument argument, BlockScope scope) {
/* 473 */     return true;
/*     */   }
/*     */   public boolean visit(Argument argument, ClassScope scope) {
/* 476 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(ArrayAllocationExpression arrayAllocationExpression, BlockScope scope)
/*     */   {
/* 481 */     return true;
/*     */   }
/*     */   public boolean visit(ArrayInitializer arrayInitializer, BlockScope scope) {
/* 484 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, BlockScope scope)
/*     */   {
/* 489 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, ClassScope scope)
/*     */   {
/* 494 */     return true;
/*     */   }
/*     */   public boolean visit(ArrayReference arrayReference, BlockScope scope) {
/* 497 */     return true;
/*     */   }
/*     */   public boolean visit(ArrayTypeReference arrayTypeReference, BlockScope scope) {
/* 500 */     return true;
/*     */   }
/*     */   public boolean visit(ArrayTypeReference arrayTypeReference, ClassScope scope) {
/* 503 */     return true;
/*     */   }
/*     */   public boolean visit(AssertStatement assertStatement, BlockScope scope) {
/* 506 */     return true;
/*     */   }
/*     */   public boolean visit(Assignment assignment, BlockScope scope) {
/* 509 */     return true;
/*     */   }
/*     */   public boolean visit(BinaryExpression binaryExpression, BlockScope scope) {
/* 512 */     return true;
/*     */   }
/*     */   public boolean visit(Block block, BlockScope scope) {
/* 515 */     return true;
/*     */   }
/*     */   public boolean visit(BreakStatement breakStatement, BlockScope scope) {
/* 518 */     return true;
/*     */   }
/*     */   public boolean visit(CaseStatement caseStatement, BlockScope scope) {
/* 521 */     return true;
/*     */   }
/*     */   public boolean visit(CastExpression castExpression, BlockScope scope) {
/* 524 */     return true;
/*     */   }
/*     */   public boolean visit(CharLiteral charLiteral, BlockScope scope) {
/* 527 */     return true;
/*     */   }
/*     */   public boolean visit(ClassLiteralAccess classLiteral, BlockScope scope) {
/* 530 */     return true;
/*     */   }
/*     */   public boolean visit(Clinit clinit, ClassScope scope) {
/* 533 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(CompilationUnitDeclaration compilationUnitDeclaration, CompilationUnitScope scope)
/*     */   {
/* 538 */     return true;
/*     */   }
/*     */   public boolean visit(CompoundAssignment compoundAssignment, BlockScope scope) {
/* 541 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(ConditionalExpression conditionalExpression, BlockScope scope)
/*     */   {
/* 546 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope)
/*     */   {
/* 551 */     return true;
/*     */   }
/*     */   public boolean visit(ContinueStatement continueStatement, BlockScope scope) {
/* 554 */     return true;
/*     */   }
/*     */   public boolean visit(DoStatement doStatement, BlockScope scope) {
/* 557 */     return true;
/*     */   }
/*     */   public boolean visit(DoubleLiteral doubleLiteral, BlockScope scope) {
/* 560 */     return true;
/*     */   }
/*     */   public boolean visit(EmptyStatement emptyStatement, BlockScope scope) {
/* 563 */     return true;
/*     */   }
/*     */   public boolean visit(EqualExpression equalExpression, BlockScope scope) {
/* 566 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(ExplicitConstructorCall explicitConstructor, BlockScope scope)
/*     */   {
/* 571 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(ExtendedStringLiteral extendedStringLiteral, BlockScope scope)
/*     */   {
/* 576 */     return true;
/*     */   }
/*     */   public boolean visit(FalseLiteral falseLiteral, BlockScope scope) {
/* 579 */     return true;
/*     */   }
/*     */   public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
/* 582 */     return true;
/*     */   }
/*     */   public boolean visit(FieldReference fieldReference, BlockScope scope) {
/* 585 */     return true;
/*     */   }
/*     */   public boolean visit(FieldReference fieldReference, ClassScope scope) {
/* 588 */     return true;
/*     */   }
/*     */   public boolean visit(FloatLiteral floatLiteral, BlockScope scope) {
/* 591 */     return true;
/*     */   }
/*     */   public boolean visit(ForeachStatement forStatement, BlockScope scope) {
/* 594 */     return true;
/*     */   }
/*     */   public boolean visit(ForStatement forStatement, BlockScope scope) {
/* 597 */     return true;
/*     */   }
/*     */   public boolean visit(IfStatement ifStatement, BlockScope scope) {
/* 600 */     return true;
/*     */   }
/*     */   public boolean visit(ImportReference importRef, CompilationUnitScope scope) {
/* 603 */     return true;
/*     */   }
/*     */   public boolean visit(Initializer initializer, MethodScope scope) {
/* 606 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(InstanceOfExpression instanceOfExpression, BlockScope scope)
/*     */   {
/* 611 */     return true;
/*     */   }
/*     */   public boolean visit(IntLiteral intLiteral, BlockScope scope) {
/* 614 */     return true;
/*     */   }
/*     */   public boolean visit(Javadoc javadoc, BlockScope scope) {
/* 617 */     return true;
/*     */   }
/*     */   public boolean visit(Javadoc javadoc, ClassScope scope) {
/* 620 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocAllocationExpression expression, BlockScope scope) {
/* 623 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocAllocationExpression expression, ClassScope scope) {
/* 626 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocArgumentExpression expression, BlockScope scope) {
/* 629 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocArgumentExpression expression, ClassScope scope) {
/* 632 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocArrayQualifiedTypeReference typeRef, BlockScope scope) {
/* 635 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocArrayQualifiedTypeReference typeRef, ClassScope scope) {
/* 638 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocArraySingleTypeReference typeRef, BlockScope scope) {
/* 641 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocArraySingleTypeReference typeRef, ClassScope scope) {
/* 644 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocFieldReference fieldRef, BlockScope scope) {
/* 647 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocFieldReference fieldRef, ClassScope scope) {
/* 650 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocImplicitTypeReference implicitTypeReference, BlockScope scope) {
/* 653 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocImplicitTypeReference implicitTypeReference, ClassScope scope) {
/* 656 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocMessageSend messageSend, BlockScope scope) {
/* 659 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocMessageSend messageSend, ClassScope scope) {
/* 662 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocQualifiedTypeReference typeRef, BlockScope scope) {
/* 665 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocQualifiedTypeReference typeRef, ClassScope scope) {
/* 668 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocReturnStatement statement, BlockScope scope) {
/* 671 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocReturnStatement statement, ClassScope scope) {
/* 674 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocSingleNameReference argument, BlockScope scope) {
/* 677 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocSingleNameReference argument, ClassScope scope) {
/* 680 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocSingleTypeReference typeRef, BlockScope scope) {
/* 683 */     return true;
/*     */   }
/*     */   public boolean visit(JavadocSingleTypeReference typeRef, ClassScope scope) {
/* 686 */     return true;
/*     */   }
/*     */   public boolean visit(LabeledStatement labeledStatement, BlockScope scope) {
/* 689 */     return true;
/*     */   }
/*     */   public boolean visit(LocalDeclaration localDeclaration, BlockScope scope) {
/* 692 */     return true;
/*     */   }
/*     */   public boolean visit(LongLiteral longLiteral, BlockScope scope) {
/* 695 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(MarkerAnnotation annotation, BlockScope scope)
/*     */   {
/* 703 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(MemberValuePair pair, BlockScope scope)
/*     */   {
/* 711 */     return true;
/*     */   }
/*     */   public boolean visit(MessageSend messageSend, BlockScope scope) {
/* 714 */     return true;
/*     */   }
/*     */   public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
/* 717 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(StringLiteralConcatenation literal, BlockScope scope)
/*     */   {
/* 722 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(NormalAnnotation annotation, BlockScope scope)
/*     */   {
/* 730 */     return true;
/*     */   }
/*     */   public boolean visit(NullLiteral nullLiteral, BlockScope scope) {
/* 733 */     return true;
/*     */   }
/*     */   public boolean visit(OR_OR_Expression or_or_Expression, BlockScope scope) {
/* 736 */     return true;
/*     */   }
/*     */   public boolean visit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, BlockScope scope) {
/* 739 */     return true;
/*     */   }
/*     */   public boolean visit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, ClassScope scope) {
/* 742 */     return true;
/*     */   }
/*     */   public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, BlockScope scope) {
/* 745 */     return true;
/*     */   }
/*     */   public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, ClassScope scope) {
/* 748 */     return true;
/*     */   }
/*     */   public boolean visit(PostfixExpression postfixExpression, BlockScope scope) {
/* 751 */     return true;
/*     */   }
/*     */   public boolean visit(PrefixExpression prefixExpression, BlockScope scope) {
/* 754 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(QualifiedAllocationExpression qualifiedAllocationExpression, BlockScope scope)
/*     */   {
/* 759 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(QualifiedNameReference qualifiedNameReference, BlockScope scope)
/*     */   {
/* 764 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(QualifiedNameReference qualifiedNameReference, ClassScope scope)
/*     */   {
/* 769 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(QualifiedSuperReference qualifiedSuperReference, BlockScope scope)
/*     */   {
/* 774 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(QualifiedSuperReference qualifiedSuperReference, ClassScope scope)
/*     */   {
/* 779 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(QualifiedThisReference qualifiedThisReference, BlockScope scope)
/*     */   {
/* 784 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(QualifiedThisReference qualifiedThisReference, ClassScope scope)
/*     */   {
/* 789 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(QualifiedTypeReference qualifiedTypeReference, BlockScope scope)
/*     */   {
/* 794 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(QualifiedTypeReference qualifiedTypeReference, ClassScope scope)
/*     */   {
/* 799 */     return true;
/*     */   }
/*     */   public boolean visit(ReturnStatement returnStatement, BlockScope scope) {
/* 802 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(SingleMemberAnnotation annotation, BlockScope scope)
/*     */   {
/* 810 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(SingleNameReference singleNameReference, BlockScope scope)
/*     */   {
/* 815 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(SingleNameReference singleNameReference, ClassScope scope)
/*     */   {
/* 820 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(SingleTypeReference singleTypeReference, BlockScope scope)
/*     */   {
/* 825 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(SingleTypeReference singleTypeReference, ClassScope scope)
/*     */   {
/* 830 */     return true;
/*     */   }
/*     */   public boolean visit(StringLiteral stringLiteral, BlockScope scope) {
/* 833 */     return true;
/*     */   }
/*     */   public boolean visit(SuperReference superReference, BlockScope scope) {
/* 836 */     return true;
/*     */   }
/*     */   public boolean visit(SwitchStatement switchStatement, BlockScope scope) {
/* 839 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(SynchronizedStatement synchronizedStatement, BlockScope scope)
/*     */   {
/* 844 */     return true;
/*     */   }
/*     */   public boolean visit(ThisReference thisReference, BlockScope scope) {
/* 847 */     return true;
/*     */   }
/*     */   public boolean visit(ThisReference thisReference, ClassScope scope) {
/* 850 */     return true;
/*     */   }
/*     */   public boolean visit(ThrowStatement throwStatement, BlockScope scope) {
/* 853 */     return true;
/*     */   }
/*     */   public boolean visit(TrueLiteral trueLiteral, BlockScope scope) {
/* 856 */     return true;
/*     */   }
/*     */   public boolean visit(TryStatement tryStatement, BlockScope scope) {
/* 859 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(TypeDeclaration localTypeDeclaration, BlockScope scope)
/*     */   {
/* 864 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope)
/*     */   {
/* 869 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope)
/*     */   {
/* 874 */     return true;
/*     */   }
/*     */   public boolean visit(TypeParameter typeParameter, BlockScope scope) {
/* 877 */     return true;
/*     */   }
/*     */   public boolean visit(TypeParameter typeParameter, ClassScope scope) {
/* 880 */     return true;
/*     */   }
/*     */   public boolean visit(UnaryExpression unaryExpression, BlockScope scope) {
/* 883 */     return true;
/*     */   }
/*     */   public boolean visit(WhileStatement whileStatement, BlockScope scope) {
/* 886 */     return true;
/*     */   }
/*     */   public boolean visit(Wildcard wildcard, BlockScope scope) {
/* 889 */     return true;
/*     */   }
/*     */   public boolean visit(Wildcard wildcard, ClassScope scope) {
/* 892 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ASTVisitor
 * JD-Core Version:    0.6.0
 */