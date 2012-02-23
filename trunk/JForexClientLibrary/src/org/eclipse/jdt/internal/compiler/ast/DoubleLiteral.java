/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.util.FloatUtil;
/*     */ 
/*     */ public class DoubleLiteral extends NumberLiteral
/*     */ {
/*     */   double value;
/*     */ 
/*     */   public DoubleLiteral(char[] token, int s, int e)
/*     */   {
/*  24 */     super(token, s, e);
/*     */   }
/*     */ 
/*     */   public void computeConstant()
/*     */   {
/*     */     try {
/*  30 */       computedValue = Double.valueOf(String.valueOf(this.source));
/*     */     }
/*     */     catch (NumberFormatException localNumberFormatException1)
/*     */     {
/*     */       try
/*     */       {
/*     */         Double computedValue;
/*  35 */         double v = FloatUtil.valueOfHexDoubleLiteral(this.source);
/*  36 */         if (v == (1.0D / 0.0D))
/*     */         {
/*  38 */           return;
/*     */         }
/*  40 */         if (Double.isNaN(v))
/*     */         {
/*  42 */           return;
/*     */         }
/*  44 */         this.value = v;
/*  45 */         this.constant = DoubleConstant.fromValue(v);
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException2) {
/*     */       }
/*  49 */       return;
/*     */     }
/*     */     Double computedValue;
/*  51 */     double doubleValue = computedValue.doubleValue();
/*  52 */     if (doubleValue > 1.7976931348623157E+308D)
/*     */     {
/*  54 */       return;
/*     */     }
/*  56 */     if (doubleValue < 4.9E-324D)
/*     */     {
/*  60 */       boolean isHexaDecimal = false;
/*  61 */       for (int i = 0; i < this.source.length; i++) {
/*  62 */         switch (this.source[i]) {
/*     */         case '.':
/*     */         case '0':
/*  65 */           break;
/*     */         case 'X':
/*     */         case 'x':
/*  68 */           isHexaDecimal = true;
/*  69 */           break;
/*     */         case 'D':
/*     */         case 'E':
/*     */         case 'F':
/*     */         case 'd':
/*     */         case 'e':
/*     */         case 'f':
/*  76 */           if (!isHexaDecimal) break label236; return;
/*     */         case 'P':
/*     */         case 'p':
/*  84 */           break;
/*     */         default:
/*  87 */           return;
/*     */         }
/*     */       }
/*     */     }
/*  91 */     label236: this.value = doubleValue;
/*  92 */     this.constant = DoubleConstant.fromValue(this.value);
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
/* 111 */     return TypeBinding.DOUBLE;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 115 */     visitor.visit(this, scope);
/* 116 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.DoubleLiteral
 * JD-Core Version:    0.6.0
 */