/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class And extends BoolFunction
/*    */ {
/* 92 */   private static final String[] fname = { " ", "a", "n", "d", " " };
/*    */ 
/*    */   public And()
/*    */   {
/* 16 */     this.ftooltip = "sc.calculator.logical.bitwise.and";
/* 17 */     this.fshortcut = '&';
/*    */   }
/*    */ 
/*    */   public double function(double x, double y)
/*    */   {
/* 27 */     if ((Double.isNaN(x)) || (Double.isNaN(y)) || (Double.isInfinite(x)) || (Double.isInfinite(y)))
/*    */     {
/* 30 */       throw new RuntimeException("Boolean Error");
/* 31 */     }if (Math.abs(y) > Math.abs(x)) {
/* 32 */       double tmp = x;
/* 33 */       x = y;
/* 34 */       y = tmp;
/*    */     }
/* 36 */     long x_bits = Double.doubleToLongBits(x);
/* 37 */     boolean x_sign = x_bits >> 63 == 0L;
/* 38 */     int x_exponent = (int)(x_bits >> 52 & 0x7FF);
/* 39 */     long x_significand = x_exponent == 0 ? (x_bits & 0xFFFFFFFF) << 1 : x_bits & 0xFFFFFFFF | 0x0;
/*    */ 
/* 41 */     long y_bits = Double.doubleToLongBits(y);
/* 42 */     boolean y_sign = y_bits >> 63 == 0L;
/* 43 */     int y_exponent = (int)(y_bits >> 52 & 0x7FF);
/* 44 */     long y_significand = y_exponent == 0 ? (y_bits & 0xFFFFFFFF) << 1 : y_bits & 0xFFFFFFFF | 0x0;
/*    */ 
/* 46 */     y_significand >>= x_exponent - y_exponent;
/*    */ 
/* 49 */     x_significand &= y_significand;
/*    */ 
/* 52 */     if (x_exponent == 0) {
/* 53 */       x_significand >>= 1;
/*    */     } else {
/* 55 */       if (x_significand == 0L) return 0.0D;
/* 56 */       while ((x_significand & 0x0) == 0L) {
/* 57 */         x_significand <<= 1;
/* 58 */         x_exponent--;
/* 59 */         if (x_exponent == 0) {
/* 60 */           x_significand >>= 1;
/*    */         }
/*    */       }
/*    */ 
/* 64 */       x_significand &= 4503599627370495L;
/*    */     }
/*    */ 
/* 67 */     x_bits = x_exponent << 52;
/* 68 */     x_bits |= x_significand;
/*    */ 
/* 70 */     double result = Double.longBitsToDouble(x_bits);
/*    */ 
/* 73 */     if (((!x_sign ? 1 : 0) & (!y_sign ? 1 : 0)) != 0)
/* 74 */       result = -result;
/* 75 */     return result;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x, OObject y)
/*    */   {
/* 85 */     return x.and(y);
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 89 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.And
 * JD-Core Version:    0.6.0
 */