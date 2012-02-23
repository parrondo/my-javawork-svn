/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.util.FloatUtil;
/*     */ 
/*     */ public class FloatLiteral extends NumberLiteral
/*     */ {
/*     */   float value;
/*     */ 
/*     */   public FloatLiteral(char[] token, int s, int e)
/*     */   {
/*  25 */     super(token, s, e);
/*     */   }
/*     */ 
/*     */   public void computeConstant()
/*     */   {
/*     */     try {
/*  31 */       computedValue = Float.valueOf(String.valueOf(this.source));
/*     */     }
/*     */     catch (NumberFormatException localNumberFormatException1)
/*     */     {
/*     */       try
/*     */       {
/*     */         Float computedValue;
/*  36 */         float v = FloatUtil.valueOfHexFloatLiteral(this.source);
/*  37 */         if (v == (1.0F / 1.0F))
/*     */         {
/*  39 */           return;
/*     */         }
/*  41 */         if (Float.isNaN(v))
/*     */         {
/*  43 */           return;
/*     */         }
/*  45 */         this.value = v;
/*  46 */         this.constant = FloatConstant.fromValue(v);
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException2) {
/*     */       }
/*  50 */       return;
/*     */     }
/*     */     Float computedValue;
/*  52 */     float floatValue = computedValue.floatValue();
/*  53 */     if (floatValue > 3.4028235E+38F)
/*     */     {
/*  55 */       return;
/*     */     }
/*  57 */     if (floatValue < 1.4E-45F)
/*     */     {
/*  61 */       boolean isHexaDecimal = false;
/*  62 */       for (int i = 0; i < this.source.length; i++) {
/*  63 */         switch (this.source[i]) {
/*     */         case '.':
/*     */         case '0':
/*  66 */           break;
/*     */         case 'X':
/*     */         case 'x':
/*  69 */           isHexaDecimal = true;
/*  70 */           break;
/*     */         case 'D':
/*     */         case 'E':
/*     */         case 'F':
/*     */         case 'd':
/*     */         case 'e':
/*     */         case 'f':
/*  77 */           if (!isHexaDecimal) break label230; return;
/*     */         case 'P':
/*     */         case 'p':
/*  85 */           break;
/*     */         default:
/*  88 */           return;
/*     */         }
/*     */       }
/*     */     }
/*  92 */     label230: this.value = floatValue;
/*  93 */     this.constant = FloatConstant.fromValue(this.value);
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 104 */     int pc = codeStream.position;
/* 105 */     if (valueRequired) {
/* 106 */       codeStream.generateConstant(this.constant, this.implicitConversion);
/*     */     }
/* 108 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public TypeBinding literalType(BlockScope scope) {
/* 112 */     return TypeBinding.FLOAT;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 116 */     visitor.visit(this, scope);
/* 117 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.FloatLiteral
 * JD-Core Version:    0.6.0
 */