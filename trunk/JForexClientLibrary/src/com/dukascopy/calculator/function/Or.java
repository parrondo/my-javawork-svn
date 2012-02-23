/*     */ package com.dukascopy.calculator.function;
/*     */ 
/*     */ import com.dukascopy.calculator.OObject;
/*     */ import javax.swing.JOptionPane;
/*     */ 
/*     */ public class Or extends BoolFunction
/*     */ {
/* 101 */   private static final String[] fname = { " ", "o", "r", " " };
/*     */ 
/*     */   public Or()
/*     */   {
/*  17 */     this.ftooltip = "sc.calculator.logical.bitwise.inclusive.or";
/*  18 */     this.fshortcut = '|';
/*     */   }
/*     */ 
/*     */   public double function(double x, double y)
/*     */   {
/*  28 */     if ((Double.isNaN(x)) || (Double.isNaN(y)) || (Double.isInfinite(x)) || (Double.isInfinite(y)))
/*     */     {
/*  31 */       throw new RuntimeException("Boolean Error");
/*  32 */     }if (Math.abs(y) > Math.abs(x)) {
/*  33 */       double tmp = x;
/*  34 */       x = y;
/*  35 */       y = tmp;
/*     */     }
/*  37 */     long x_bits = Double.doubleToLongBits(x);
/*  38 */     boolean x_sign = x_bits >> 63 == 0L;
/*  39 */     int x_exponent = (int)(x_bits >> 52 & 0x7FF);
/*  40 */     long x_significand = x_exponent == 0 ? (x_bits & 0xFFFFFFFF) << 1 : x_bits & 0xFFFFFFFF | 0x0;
/*     */ 
/*  42 */     long y_bits = Double.doubleToLongBits(y);
/*  43 */     boolean y_sign = y_bits >> 63 == 0L;
/*  44 */     int y_exponent = (int)(y_bits >> 52 & 0x7FF);
/*  45 */     long y_significand = y_exponent == 0 ? (y_bits & 0xFFFFFFFF) << 1 : y_bits & 0xFFFFFFFF | 0x0;
/*     */ 
/*  47 */     y_significand >>= x_exponent - y_exponent;
/*     */ 
/*  50 */     x_significand |= y_significand;
/*     */ 
/*  53 */     if (x_exponent == 0) {
/*  54 */       x_significand >>= 1;
/*     */     } else {
/*  56 */       if (x_significand == 0L) return 0.0D;
/*  57 */       while ((x_significand & 0x0) == 0L) {
/*  58 */         x_significand <<= 1;
/*  59 */         x_exponent--;
/*  60 */         if (x_exponent == 0) {
/*  61 */           x_significand >>= 1;
/*     */         }
/*     */       }
/*     */ 
/*  65 */       x_significand &= 4503599627370495L;
/*     */     }
/*     */ 
/*  68 */     x_bits = x_exponent << 52;
/*  69 */     x_bits |= x_significand;
/*     */ 
/*  71 */     double result = Double.longBitsToDouble(x_bits);
/*     */ 
/*  74 */     if (((!x_sign ? 1 : 0) | (!y_sign ? 1 : 0)) != 0)
/*  75 */       result = -result;
/*  76 */     return result;
/*     */   }
/*     */ 
/*     */   public OObject function(OObject x, OObject y)
/*     */   {
/*  86 */     return x.or(y);
/*     */   }
/*     */ 
/*     */   public String[] name_array() {
/*  90 */     return fname;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/*  94 */     PObject o = new Or();
/*  95 */     StringBuilder s = new StringBuilder("<html>");
/*  96 */     s.append(o.name());
/*  97 */     s.append("</html>");
/*  98 */     JOptionPane.showMessageDialog(null, s.toString());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Or
 * JD-Core Version:    0.6.0
 */