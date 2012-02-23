/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.parser.Parser;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class Initializer extends FieldDeclaration
/*     */ {
/*     */   public Block block;
/*     */   public int lastVisibleFieldID;
/*     */   public int bodyStart;
/*     */   public int bodyEnd;
/*     */ 
/*     */   public Initializer(Block block, int modifiers)
/*     */   {
/*  28 */     this.block = block;
/*  29 */     this.modifiers = modifiers;
/*     */ 
/*  31 */     if (block != null)
/*  32 */       this.declarationSourceStart = (this.sourceStart = block.sourceStart);
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(MethodScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  41 */     if (this.block != null) {
/*  42 */       return this.block.analyseCode(currentScope, flowContext, flowInfo);
/*     */     }
/*  44 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*  56 */     if ((this.bits & 0x80000000) == 0) {
/*  57 */       return;
/*     */     }
/*  59 */     int pc = codeStream.position;
/*  60 */     if (this.block != null) this.block.generateCode(currentScope, codeStream);
/*  61 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public int getKind()
/*     */   {
/*  68 */     return 2;
/*     */   }
/*     */ 
/*     */   public boolean isStatic()
/*     */   {
/*  73 */     return (this.modifiers & 0x8) != 0;
/*     */   }
/*     */ 
/*     */   public void parseStatements(Parser parser, TypeDeclaration typeDeclaration, CompilationUnitDeclaration unit)
/*     */   {
/*  82 */     parser.parse(this, typeDeclaration, unit);
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output)
/*     */   {
/*  87 */     if (this.modifiers != 0) {
/*  88 */       printIndent(indent, output);
/*  89 */       printModifiers(this.modifiers, output);
/*  90 */       if (this.annotations != null) printAnnotations(this.annotations, output);
/*  91 */       output.append("{\n");
/*  92 */       if (this.block != null) {
/*  93 */         this.block.printBody(indent, output);
/*     */       }
/*  95 */       printIndent(indent, output).append('}');
/*  96 */       return output;
/*  97 */     }if (this.block != null)
/*  98 */       this.block.printStatement(indent, output);
/*     */     else {
/* 100 */       printIndent(indent, output).append("{}");
/*     */     }
/* 102 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(MethodScope scope)
/*     */   {
/* 107 */     FieldBinding previousField = scope.initializedField;
/* 108 */     int previousFieldID = scope.lastVisibleFieldID;
/*     */     try {
/* 110 */       scope.initializedField = null;
/* 111 */       scope.lastVisibleFieldID = this.lastVisibleFieldID;
/* 112 */       if (isStatic()) {
/* 113 */         ReferenceBinding declaringType = scope.enclosingSourceType();
/* 114 */         if ((declaringType.isNestedType()) && (!declaringType.isStatic()))
/* 115 */           scope.problemReporter().innerTypesCannotDeclareStaticInitializers(
/* 116 */             declaringType, 
/* 117 */             this);
/*     */       }
/* 119 */       if (this.block != null) this.block.resolve(scope); 
/*     */     }
/*     */     finally {
/* 121 */       scope.initializedField = previousField;
/* 122 */       scope.lastVisibleFieldID = previousFieldID;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, MethodScope scope) {
/* 127 */     if ((visitor.visit(this, scope)) && 
/* 128 */       (this.block != null)) this.block.traverse(visitor, scope);
/*     */ 
/* 130 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Initializer
 * JD-Core Version:    0.6.0
 */