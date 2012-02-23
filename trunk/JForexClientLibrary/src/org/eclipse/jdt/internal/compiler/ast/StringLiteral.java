/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.StringConstant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ 
/*     */ public class StringLiteral extends Literal
/*     */ {
/*     */   char[] source;
/*     */   int lineNumber;
/*     */ 
/*     */   public StringLiteral(char[] token, int start, int end, int lineNumber)
/*     */   {
/*  26 */     this(start, end);
/*  27 */     this.source = token;
/*  28 */     this.lineNumber = (lineNumber - 1);
/*     */   }
/*     */ 
/*     */   public StringLiteral(int s, int e)
/*     */   {
/*  33 */     super(s, e);
/*     */   }
/*     */ 
/*     */   public void computeConstant()
/*     */   {
/*  38 */     this.constant = StringConstant.fromValue(String.valueOf(this.source));
/*     */   }
/*     */ 
/*     */   public ExtendedStringLiteral extendWith(CharLiteral lit)
/*     */   {
/*  44 */     return new ExtendedStringLiteral(this, lit);
/*     */   }
/*     */ 
/*     */   public ExtendedStringLiteral extendWith(StringLiteral lit)
/*     */   {
/*  50 */     return new ExtendedStringLiteral(this, lit);
/*     */   }
/*     */ 
/*     */   public StringLiteralConcatenation extendsWith(StringLiteral lit)
/*     */   {
/*  57 */     return new StringLiteralConcatenation(this, lit);
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  64 */     int pc = codeStream.position;
/*  65 */     if (valueRequired)
/*  66 */       codeStream.ldc(this.constant.stringValue());
/*  67 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public TypeBinding literalType(BlockScope scope)
/*     */   {
/*  72 */     return scope.getJavaLangString();
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/*  78 */     output.append('"');
/*  79 */     for (int i = 0; i < this.source.length; i++) {
/*  80 */       switch (this.source[i]) {
/*     */       case '\b':
/*  82 */         output.append("\\b");
/*  83 */         break;
/*     */       case '\t':
/*  85 */         output.append("\\t");
/*  86 */         break;
/*     */       case '\n':
/*  88 */         output.append("\\n");
/*  89 */         break;
/*     */       case '\f':
/*  91 */         output.append("\\f");
/*  92 */         break;
/*     */       case '\r':
/*  94 */         output.append("\\r");
/*  95 */         break;
/*     */       case '"':
/*  97 */         output.append("\\\"");
/*  98 */         break;
/*     */       case '\'':
/* 100 */         output.append("\\'");
/* 101 */         break;
/*     */       case '\\':
/* 103 */         output.append("\\\\");
/* 104 */         break;
/*     */       default:
/* 106 */         output.append(this.source[i]);
/*     */       }
/*     */     }
/* 109 */     output.append('"');
/* 110 */     return output;
/*     */   }
/*     */ 
/*     */   public char[] source()
/*     */   {
/* 115 */     return this.source;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 119 */     visitor.visit(this, scope);
/* 120 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.StringLiteral
 * JD-Core Version:    0.6.0
 */