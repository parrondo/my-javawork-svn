/*     */ package com.dukascopy.calculator.expression;
/*     */ 
/*     */ import com.dukascopy.calculator.OObject;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public abstract class Expression extends OObject
/*     */   implements Comparable<Expression>
/*     */ {
/*     */   public int compareTo(Expression expression)
/*     */   {
/*  13 */     if ((this instanceof SumOrProduct))
/*     */     {
/*  15 */       if ((expression instanceof SumOrProduct)) {
/*  16 */         if ((((this instanceof Sum)) && ((expression instanceof Product))) || (((this instanceof Product)) && ((expression instanceof Sum))))
/*     */         {
/*  18 */           System.out.println("Warning: Sum and Product being compared."); } else {
/*  19 */           if ((this instanceof Sum))
/*  20 */             return ((Sum)this).compareTo((Sum)expression);
/*  21 */           if ((this instanceof Product)) {
/*  22 */             return ((Product)this).compareTo((Product)expression);
/*     */           }
/*  24 */           return 1;
/*     */         }
/*     */       } else return -1; 
/*     */     }
/*     */     else {
/*  28 */       if ((expression instanceof SumOrProduct))
/*     */       {
/*  30 */         if ((expression instanceof Sum))
/*  31 */           return new Sum(this).compareTo((Sum)expression);
/*  32 */         if ((expression instanceof Product)) {
/*  33 */           return new Product(this, false).compareTo((Product)expression);
/*     */         }
/*  35 */         return 1;
/*     */       }
/*  37 */       if ((this instanceof Power))
/*     */       {
/*  39 */         if ((expression instanceof Power)) {
/*  40 */           return ((Power)this).compareTo((Power)expression);
/*     */         }
/*  42 */         return -1;
/*     */       }
/*  44 */       if ((expression instanceof Power))
/*     */       {
/*  46 */         return 1;
/*  47 */       }if ((this instanceof Variable))
/*     */       {
/*  49 */         if ((expression instanceof Variable)) {
/*  50 */           return ((Variable)this).compareTo((Variable)expression);
/*     */         }
/*  52 */         return -1;
/*     */       }
/*  54 */       if ((expression instanceof Variable))
/*     */       {
/*  56 */         return 1;
/*  57 */       }if ((this instanceof Exp))
/*     */       {
/*  59 */         if ((expression instanceof Exp)) {
/*  60 */           return 0;
/*     */         }
/*  62 */         return -1;
/*     */       }
/*  64 */       if ((expression instanceof Exp))
/*     */       {
/*  66 */         return 1;
/*  67 */       }if ((this instanceof Sin))
/*     */       {
/*  69 */         if ((expression instanceof Sin)) {
/*  70 */           return 0;
/*     */         }
/*  72 */         return -1;
/*     */       }
/*  74 */       if ((expression instanceof Sin))
/*     */       {
/*  76 */         return 1;
/*  77 */       }if ((this instanceof Cos))
/*     */       {
/*  79 */         if ((expression instanceof Cos)) {
/*  80 */           return 0;
/*     */         }
/*  82 */         return -1;
/*     */       }
/*  84 */       if ((expression instanceof Cos))
/*     */       {
/*  86 */         return 1;
/*  87 */       }if ((this instanceof Tan))
/*     */       {
/*  89 */         if ((expression instanceof Tan)) {
/*  90 */           return 0;
/*     */         }
/*  92 */         return -1;
/*     */       }
/*  94 */       if ((expression instanceof Tan))
/*     */       {
/*  96 */         return 1;
/*  97 */       }if ((this instanceof ASin))
/*     */       {
/*  99 */         if ((expression instanceof ASin)) {
/* 100 */           return 0;
/*     */         }
/* 102 */         return -1;
/*     */       }
/* 104 */       if ((expression instanceof ASin))
/*     */       {
/* 106 */         return 1;
/* 107 */       }if ((this instanceof ACos))
/*     */       {
/* 109 */         if ((expression instanceof ACos)) {
/* 110 */           return 0;
/*     */         }
/* 112 */         return -1;
/*     */       }
/* 114 */       if ((expression instanceof ACos))
/*     */       {
/* 116 */         return 1;
/* 117 */       }if ((this instanceof ATan))
/*     */       {
/* 119 */         if ((expression instanceof ATan)) {
/* 120 */           return 0;
/*     */         }
/* 122 */         return -1;
/*     */       }
/* 124 */       if ((expression instanceof ATan))
/*     */       {
/* 126 */         return 1;
/* 127 */       }if ((this instanceof Ln))
/*     */       {
/* 129 */         if ((expression instanceof Ln)) {
/* 130 */           return 0;
/*     */         }
/* 132 */         return -1;
/*     */       }
/* 134 */       if ((expression instanceof Ln))
/*     */       {
/* 136 */         return 1;
/* 137 */       }if ((this instanceof Log))
/*     */       {
/* 139 */         if ((expression instanceof Log)) {
/* 140 */           return 0;
/*     */         }
/* 142 */         return -1;
/*     */       }
/* 144 */       if ((expression instanceof Log))
/*     */       {
/* 146 */         return 1;
/* 147 */       }if ((this instanceof Factorial))
/*     */       {
/* 149 */         if ((expression instanceof Factorial)) {
/* 150 */           return 0;
/*     */         }
/* 152 */         return -1;
/*     */       }
/* 154 */       if ((expression instanceof Factorial))
/*     */       {
/* 156 */         return 1;
/* 157 */       }if ((this instanceof Permutation))
/*     */       {
/* 159 */         if ((expression instanceof Permutation)) {
/* 160 */           return 0;
/*     */         }
/* 162 */         return -1;
/*     */       }
/* 164 */       if ((expression instanceof Permutation))
/*     */       {
/* 166 */         return 1;
/* 167 */       }if ((this instanceof Combination))
/*     */       {
/* 169 */         if ((expression instanceof Combination)) {
/* 170 */           return 0;
/*     */         }
/* 172 */         return -1;
/*     */       }
/* 174 */       if ((expression instanceof Combination))
/*     */       {
/* 176 */         return 1;
/* 177 */       }if ((this instanceof Conjugate))
/*     */       {
/* 179 */         if ((expression instanceof Conjugate)) {
/* 180 */           return 0;
/*     */         }
/* 182 */         return -1;
/*     */       }
/* 184 */       if ((expression instanceof Conjugate))
/*     */       {
/* 186 */         return 1;
/* 187 */       }if ((this instanceof And))
/*     */       {
/* 189 */         if ((expression instanceof And)) {
/* 190 */           return 0;
/*     */         }
/* 192 */         return -1;
/*     */       }
/* 194 */       if ((expression instanceof And))
/*     */       {
/* 196 */         return 1;
/* 197 */       }if ((this instanceof Or))
/*     */       {
/* 199 */         if ((expression instanceof Or)) {
/* 200 */           return 0;
/*     */         }
/* 202 */         return -1;
/*     */       }
/* 204 */       if ((expression instanceof Or))
/*     */       {
/* 206 */         return 1;
/* 207 */       }if ((this instanceof Xor))
/*     */       {
/* 209 */         if ((expression instanceof Xor)) {
/* 210 */           return 0;
/*     */         }
/* 212 */         return -1;
/*     */       }
/* 214 */       if ((expression instanceof Xor))
/*     */       {
/* 216 */         return 1;
/*     */       }
/* 218 */       System.out.println("Warning: unknown function");
/*     */     }
/* 220 */     return 0;
/*     */   }
/*     */ 
/*     */   public abstract Expression negate();
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Expression
 * JD-Core Version:    0.6.0
 */