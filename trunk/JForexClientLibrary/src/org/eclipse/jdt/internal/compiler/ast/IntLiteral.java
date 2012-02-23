/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.impl.IntConstant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
/*     */ 
/*     */ public class IntLiteral extends NumberLiteral
/*     */ {
/*     */   public int value;
/*  23 */   public static final IntLiteral One = new IntLiteral(new char[] { '1' }, 0, 0, 1);
/*     */ 
/*     */   public IntLiteral(char[] token, int s, int e) {
/*  26 */     super(token, s, e);
/*     */   }
/*     */ 
/*     */   public IntLiteral(char[] token, int s, int e, int value) {
/*  30 */     this(token, s, e);
/*  31 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public IntLiteral(int intValue)
/*     */   {
/*  40 */     super(null, 0, 0);
/*  41 */     this.constant = IntConstant.fromValue(intValue);
/*  42 */     this.value = intValue;
/*     */   }
/*     */ 
/*     */   public void computeConstant()
/*     */   {
/*  49 */     long MAX = 2147483647L;
/*  50 */     if (this == One) {
/*  51 */       this.constant = IntConstant.fromValue(1);
/*  52 */       return;
/*     */     }
/*  54 */     int length = this.source.length;
/*  55 */     long computedValue = 0L;
/*  56 */     if (this.source[0] == '0') {
/*  57 */       MAX = 4294967295L;
/*  58 */       if (length == 1) {
/*  59 */         this.constant = IntConstant.fromValue(0); return;
/*     */       }
/*     */       int radix;
/*     */       int shift;
/*     */       int j;
/*     */       int radix;
/*  63 */       if ((this.source[1] == 'x') || (this.source[1] == 'X')) {
/*  64 */         int shift = 4; int j = 2; radix = 16;
/*     */       } else {
/*  66 */         shift = 3; j = 1; radix = 8;
/*     */       }
/*  68 */       while (this.source[j] == '0') {
/*  69 */         j++;
/*  70 */         if (j != length)
/*     */           continue;
/*  72 */         this.constant = IntConstant.fromValue(this.value = (int)computedValue);
/*  73 */         return;
/*     */       }
/*     */ 
/*  76 */       while (j < length)
/*     */       {
/*     */         int digitValue;
/*  78 */         if ((digitValue = ScannerHelper.digit(this.source[(j++)], radix)) < 0) {
/*  79 */           return;
/*     */         }
/*  81 */         computedValue = computedValue << shift | digitValue;
/*  82 */         if (computedValue > MAX) return; 
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  86 */       for (int i = 0; i < length; i++)
/*     */       {
/*     */         int digitValue;
/*  88 */         if ((digitValue = ScannerHelper.digit(this.source[i], 10)) < 0) {
/*  89 */           return;
/*     */         }
/*  91 */         computedValue = 10L * computedValue + digitValue;
/*  92 */         if (computedValue > MAX) return;
/*     */       }
/*     */     }
/*  95 */     this.constant = IntConstant.fromValue(this.value = (int)computedValue);
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 107 */     int pc = codeStream.position;
/* 108 */     if (valueRequired) {
/* 109 */       codeStream.generateConstant(this.constant, this.implicitConversion);
/*     */     }
/* 111 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public TypeBinding literalType(BlockScope scope) {
/* 115 */     return TypeBinding.INT;
/*     */   }
/*     */ 
/*     */   public final boolean mayRepresentMIN_VALUE()
/*     */   {
/* 134 */     return (this.source.length == 10) && 
/* 124 */       (this.source[0] == '2') && 
/* 125 */       (this.source[1] == '1') && 
/* 126 */       (this.source[2] == '4') && 
/* 127 */       (this.source[3] == '7') && 
/* 128 */       (this.source[4] == '4') && 
/* 129 */       (this.source[5] == '8') && 
/* 130 */       (this.source[6] == '3') && 
/* 131 */       (this.source[7] == '6') && 
/* 132 */       (this.source[8] == '4') && 
/* 133 */       (this.source[9] == '8') && 
/* 134 */       ((this.bits & 0x1FE00000) >> 21 == 0);
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 138 */     if (this.source == null)
/*     */     {
/* 140 */       return output.append(String.valueOf(this.value));
/*     */     }
/* 142 */     return super.printExpression(indent, output);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 146 */     visitor.visit(this, scope);
/* 147 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.IntLiteral
 * JD-Core Version:    0.6.0
 */