/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CharConstant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
/*     */ 
/*     */ public class CharLiteral extends NumberLiteral
/*     */ {
/*     */   char value;
/*     */ 
/*     */   public CharLiteral(char[] token, int s, int e)
/*     */   {
/*  24 */     super(token, s, e);
/*  25 */     computeValue();
/*     */   }
/*     */ 
/*     */   public void computeConstant()
/*     */   {
/*  32 */     this.constant = CharConstant.fromValue(this.value);
/*     */   }
/*     */ 
/*     */   private void computeValue()
/*     */   {
/*  39 */     if ((this.value = this.source[1]) != '\\')
/*  40 */       return;
/*     */     char digit;
/*  42 */     switch (digit = this.source[2]) {
/*     */     case 'b':
/*  44 */       this.value = '\b';
/*  45 */       break;
/*     */     case 't':
/*  47 */       this.value = '\t';
/*  48 */       break;
/*     */     case 'n':
/*  50 */       this.value = '\n';
/*  51 */       break;
/*     */     case 'f':
/*  53 */       this.value = '\f';
/*  54 */       break;
/*     */     case 'r':
/*  56 */       this.value = '\r';
/*  57 */       break;
/*     */     case '"':
/*  59 */       this.value = '"';
/*  60 */       break;
/*     */     case '\'':
/*  62 */       this.value = '\'';
/*  63 */       break;
/*     */     case '\\':
/*  65 */       this.value = '\\';
/*  66 */       break;
/*     */     default:
/*  68 */       int number = ScannerHelper.getNumericValue(digit);
/*  69 */       if ((digit = this.source[3]) != '\'') {
/*  70 */         number = number * 8 + ScannerHelper.getNumericValue(digit);
/*     */       } else {
/*  72 */         this.constant = CharConstant.fromValue(this.value = (char)number);
/*  73 */         break;
/*     */       }
/*  75 */       if ((digit = this.source[4]) != '\'')
/*  76 */         number = number * 8 + ScannerHelper.getNumericValue(digit);
/*  77 */       this.value = (char)number;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  90 */     int pc = codeStream.position;
/*  91 */     if (valueRequired) {
/*  92 */       codeStream.generateConstant(this.constant, this.implicitConversion);
/*     */     }
/*  94 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public TypeBinding literalType(BlockScope scope) {
/*  98 */     return TypeBinding.CHAR;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope) {
/* 102 */     visitor.visit(this, blockScope);
/* 103 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.CharLiteral
 * JD-Core Version:    0.6.0
 */