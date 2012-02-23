/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.impl.LongConstant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
/*     */ 
/*     */ public class LongLiteral extends NumberLiteral
/*     */ {
/*     */   public LongLiteral(char[] token, int s, int e)
/*     */   {
/*  22 */     super(token, s, e);
/*     */   }
/*     */ 
/*     */   public void computeConstant()
/*     */   {
/*  28 */     int length = this.source.length - 1;
/*     */     long computedValue;
/*  30 */     if (this.source[0] == '0') {
/*  31 */       if (length == 1) {
/*  32 */         this.constant = LongConstant.fromValue(0L);
/*  33 */         return;
/*     */       }
/*     */       int radix;
/*     */       int shift;
/*     */       int j;
/*     */       int radix;
/*  37 */       if ((this.source[1] == 'x') || (this.source[1] == 'X')) {
/*  38 */         int shift = 4; int j = 2; radix = 16;
/*     */       } else {
/*  40 */         shift = 3; j = 1; radix = 8;
/*     */       }
/*  42 */       int nbDigit = 0;
/*  43 */       while (this.source[j] == '0') {
/*  44 */         j++;
/*  45 */         if (j != length)
/*     */           continue;
/*  47 */         this.constant = LongConstant.fromValue(0L);
/*  48 */         return;
/*     */       }
/*     */       int digitValue;
/*  53 */       if ((digitValue = ScannerHelper.digit(this.source[(j++)], radix)) < 0) {
/*  54 */         return;
/*     */       }
/*  56 */       if (digitValue >= 8)
/*  57 */         nbDigit = 4;
/*  58 */       else if (digitValue >= 4)
/*  59 */         nbDigit = 3;
/*  60 */       else if (digitValue >= 2)
/*  61 */         nbDigit = 2;
/*     */       else
/*  63 */         nbDigit = 1;
/*  64 */       long computedValue = digitValue;
/*  65 */       while (j < length) {
/*  66 */         if ((digitValue = ScannerHelper.digit(this.source[(j++)], radix)) < 0) {
/*  67 */           return;
/*     */         }
/*  69 */         if (nbDigit += shift > 64)
/*  70 */           return;
/*  71 */         computedValue = computedValue << shift | digitValue;
/*     */       }
/*     */     }
/*     */     else {
/*  75 */       long previous = 0L;
/*  76 */       computedValue = 0L;
/*     */ 
/*  78 */       for (int i = 0; i < length; i++)
/*     */       {
/*  80 */         int digitValue;
/*  80 */         if ((digitValue = ScannerHelper.digit(this.source[i], 10)) < 0) return;
/*  81 */         previous = computedValue;
/*  82 */         if (computedValue > 922337203685477580L)
/*  83 */           return;
/*  84 */         computedValue *= 10L;
/*  85 */         if (computedValue + digitValue > 9223372036854775807L)
/*  86 */           return;
/*  87 */         computedValue += digitValue;
/*  88 */         if (previous > computedValue)
/*  89 */           return;
/*     */       }
/*     */     }
/*  92 */     this.constant = LongConstant.fromValue(computedValue);
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 103 */     int pc = codeStream.position;
/* 104 */     if (valueRequired) {
/* 105 */       codeStream.generateConstant(this.constant, this.implicitConversion);
/*     */     }
/* 107 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public TypeBinding literalType(BlockScope scope) {
/* 111 */     return TypeBinding.LONG;
/*     */   }
/*     */ 
/*     */   public final boolean mayRepresentMIN_VALUE()
/*     */   {
/* 139 */     return (this.source.length == 20) && 
/* 120 */       (this.source[0] == '9') && 
/* 121 */       (this.source[1] == '2') && 
/* 122 */       (this.source[2] == '2') && 
/* 123 */       (this.source[3] == '3') && 
/* 124 */       (this.source[4] == '3') && 
/* 125 */       (this.source[5] == '7') && 
/* 126 */       (this.source[6] == '2') && 
/* 127 */       (this.source[7] == '0') && 
/* 128 */       (this.source[8] == '3') && 
/* 129 */       (this.source[9] == '6') && 
/* 130 */       (this.source[10] == '8') && 
/* 131 */       (this.source[11] == '5') && 
/* 132 */       (this.source[12] == '4') && 
/* 133 */       (this.source[13] == '7') && 
/* 134 */       (this.source[14] == '7') && 
/* 135 */       (this.source[15] == '5') && 
/* 136 */       (this.source[16] == '8') && 
/* 137 */       (this.source[17] == '0') && 
/* 138 */       (this.source[18] == '8') && 
/* 139 */       ((this.bits & 0x1FE00000) >> 21 == 0);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 143 */     visitor.visit(this, scope);
/* 144 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.LongLiteral
 * JD-Core Version:    0.6.0
 */