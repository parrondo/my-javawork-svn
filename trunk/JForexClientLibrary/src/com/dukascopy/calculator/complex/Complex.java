/*      */ package com.dukascopy.calculator.complex;
/*      */ 
/*      */ import com.dukascopy.calculator.Base;
/*      */ import com.dukascopy.calculator.Notation;
/*      */ import com.dukascopy.calculator.OObject;
/*      */ import com.dukascopy.calculator.StringArray;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.MathContext;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class Complex extends OObject
/*      */ {
/*      */   private final double real_part;
/*      */   private final double imaginary_part;
/* 1397 */   private static final Complex THIRD = new Complex(0.3333333333333333D);
/*      */ 
/* 1402 */   private static final Complex LOG10 = new Complex(StrictMath.log(10.0D));
/*      */ 
/* 1407 */   public static final Complex I = new Complex(0.0D, 1.0D);
/*      */   public static final double smallImaginary = 1.0E-010D;
/*      */ 
/*      */   public Complex()
/*      */   {
/*   13 */     this(0.0D, 0.0D);
/*      */   }
/*      */ 
/*      */   public Complex(double real)
/*      */   {
/*   21 */     this(real, 0.0D);
/*      */   }
/*      */ 
/*      */   public Complex(double real, double imaginary)
/*      */   {
/*   30 */     this.real_part = real;
/*   31 */     this.imaginary_part = imaginary;
/*      */   }
/*      */ 
/*      */   public static Complex polar(double r, double theta)
/*      */   {
/*   41 */     return new Complex(r * StrictMath.cos(theta), r * StrictMath.cos(theta));
/*      */   }
/*      */ 
/*      */   public boolean isZero()
/*      */   {
/*   49 */     return (real() == 0.0D) && (imaginary() == 0.0D);
/*      */   }
/*      */ 
/*      */   public Long isInteger()
/*      */   {
/*   57 */     if (imaginary() == 0.0D) {
/*   58 */       long l = ()real();
/*   59 */       if (l == real()) return Long.valueOf(l);
/*      */     }
/*   61 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isNegative()
/*      */   {
/*   68 */     return (real() < 0.0D) || ((real() == 0.0D) && (imaginary() < 0.0D));
/*      */   }
/*      */ 
/*      */   public double real()
/*      */   {
/*   77 */     return this.real_part;
/*      */   }
/*      */ 
/*      */   public double imaginary()
/*      */   {
/*   85 */     return this.imaginary_part;
/*      */   }
/*      */ 
/*      */   public Complex conjugate()
/*      */   {
/*   93 */     return new Complex(real(), -imaginary());
/*      */   }
/*      */ 
/*      */   public Complex negate()
/*      */   {
/*  101 */     return new Complex(-real(), -imaginary());
/*      */   }
/*      */ 
/*      */   public double abs()
/*      */   {
/*  109 */     return StrictMath.hypot(real(), imaginary());
/*      */   }
/*      */ 
/*      */   public double arg()
/*      */   {
/*  117 */     if ((real() == 0.0D) && (imaginary() == 0.0D)) {
/*  118 */       return 0.0D;
/*      */     }
/*  120 */     return StrictMath.atan2(imaginary(), real());
/*      */   }
/*      */ 
/*      */   public Complex inverse()
/*      */   {
/*  131 */     if ((real() == 0.0D) && (imaginary() == 0.0D))
/*  132 */       return new Complex((1.0D / 0.0D), 0.0D);
/*  133 */     if ((Double.isInfinite(real())) || (Double.isInfinite(imaginary()))) {
/*  134 */       Complex one = new Complex(1.0D, 0.0D);
/*  135 */       return one.divide(this);
/*      */     }
/*  137 */     BigDecimal a = new BigDecimal(real());
/*  138 */     BigDecimal b = new BigDecimal(imaginary());
/*  139 */     BigDecimal s = a.multiply(a).add(b.multiply(b));
/*  140 */     double c = a.divide(s, MathContext.DECIMAL64).doubleValue();
/*  141 */     double d = b.negate().divide(s, MathContext.DECIMAL64).doubleValue();
/*  142 */     return new Complex(c, d);
/*      */   }
/*      */ 
/*      */   public Complex imultiply()
/*      */   {
/*  152 */     return new Complex(-imaginary(), real());
/*      */   }
/*      */ 
/*      */   public Complex scale(double real)
/*      */   {
/*  161 */     return new Complex(real * real(), real * imaginary());
/*      */   }
/*      */ 
/*      */   public Complex add(Complex complex)
/*      */   {
/*  170 */     return new Complex(real() + complex.real(), imaginary() + complex.imaginary());
/*      */   }
/*      */ 
/*      */   public Complex subtract(Complex complex)
/*      */   {
/*  179 */     return new Complex(real() - complex.real(), imaginary() - complex.imaginary());
/*      */   }
/*      */ 
/*      */   public Complex multiply(Complex complex)
/*      */   {
/*  190 */     if ((Double.isInfinite(real())) || (Double.isInfinite(imaginary())) || (Double.isInfinite(complex.real())) || (Double.isInfinite(complex.imaginary())))
/*      */     {
/*  193 */       double a = real();
/*  194 */       double b = imaginary();
/*  195 */       double c = complex.real();
/*  196 */       double d = complex.imaginary();
/*  197 */       double ac = a * c;
/*  198 */       double bd = b * d;
/*  199 */       double ad = a * d;
/*  200 */       double bc = b * c;
/*  201 */       if ((Double.isNaN(ac)) && ((a == 0.0D) || (c == 0.0D))) ac = 0.0D;
/*  202 */       if ((Double.isNaN(bd)) && ((b == 0.0D) || (d == 0.0D))) bd = 0.0D;
/*  203 */       if ((Double.isNaN(ad)) && ((a == 0.0D) || (d == 0.0D))) ad = 0.0D;
/*  204 */       if ((Double.isNaN(bc)) && ((b == 0.0D) || (c == 0.0D))) bc = 0.0D;
/*  205 */       double x = ac - bd;
/*  206 */       double y = bc + ad;
/*  207 */       if ((x == 0.0D) && (y == 0.0D)) return new Complex((0.0D / 0.0D), (0.0D / 0.0D));
/*  208 */       return new Complex(x, y);
/*  209 */     }if ((Double.isNaN(real())) || (Double.isNaN(imaginary())) || (Double.isNaN(complex.real())) || (Double.isNaN(complex.imaginary())))
/*      */     {
/*  211 */       return new Complex((0.0D / 0.0D), (0.0D / 0.0D));
/*      */     }
/*  213 */     BigDecimal a = new BigDecimal(real());
/*  214 */     BigDecimal b = new BigDecimal(imaginary());
/*  215 */     BigDecimal c = new BigDecimal(complex.real());
/*  216 */     BigDecimal d = new BigDecimal(complex.imaginary());
/*  217 */     double x = a.multiply(c).subtract(b.multiply(d)).doubleValue();
/*  218 */     double y = a.multiply(d).add(b.multiply(c)).doubleValue();
/*  219 */     return new Complex(x, y);
/*      */   }
/*      */ 
/*      */   public Complex divide(Complex complex)
/*      */   {
/*  239 */     if ((complex.real() == 0.0D) && (complex.imaginary() == 0.0D)) {
/*  240 */       if ((real() == 0.0D) && (imaginary() == 0.0D))
/*  241 */         return new Complex((0.0D / 0.0D));
/*  242 */       double x = 0.0D;
/*  243 */       if (real() > 0.0D) x = (1.0D / 0.0D);
/*  244 */       else if (real() < 0.0D) x = (-1.0D / 0.0D);
/*  245 */       double y = 0.0D;
/*  246 */       if (imaginary() > 0.0D) y = (1.0D / 0.0D);
/*  247 */       else if (imaginary() < 0.0D) y = (-1.0D / 0.0D);
/*  248 */       return new Complex(x, y);
/*      */     }
/*  250 */     double test = complex.real() * complex.real() + complex.imaginary() * complex.imaginary();
/*      */ 
/*  252 */     if (Double.isNaN(test))
/*  253 */       return new Complex((0.0D / 0.0D), (0.0D / 0.0D));
/*  254 */     if (Double.isInfinite(test)) {
/*  255 */       if ((real() == 0.0D) && (imaginary() == 0.0D)) {
/*  256 */         return new Complex((0.0D / 0.0D), (0.0D / 0.0D));
/*      */       }
/*  258 */       return new Complex();
/*      */     }
/*  260 */     BigDecimal a = new BigDecimal(real());
/*  261 */     BigDecimal b = new BigDecimal(imaginary());
/*  262 */     BigDecimal c = new BigDecimal(complex.real());
/*  263 */     BigDecimal d = new BigDecimal(complex.imaginary());
/*  264 */     BigDecimal s = c.multiply(c).add(d.multiply(d));
/*  265 */     double x = a.multiply(c).add(b.multiply(d)).divide(s, MathContext.DECIMAL64).doubleValue();
/*      */ 
/*  267 */     double y = b.multiply(c).subtract(a.multiply(d)).divide(s, MathContext.DECIMAL64).doubleValue();
/*      */ 
/*  269 */     return new Complex(x, y);
/*      */   }
/*      */ 
/*      */   public Complex square()
/*      */   {
/*  281 */     return multiply(this);
/*      */   }
/*      */ 
/*      */   public Complex cube()
/*      */   {
/*  289 */     return multiply(square());
/*      */   }
/*      */ 
/*      */   public Complex sqrt()
/*      */   {
/*  297 */     if ((real() == 0.0D) && (imaginary() == 0.0D)) {
/*  298 */       return new Complex();
/*      */     }
/*  300 */     double x = StrictMath.abs(real());
/*  301 */     double y = StrictMath.abs(imaginary());
/*  302 */     double w = 0.0D;
/*      */ 
/*  304 */     if (x >= y) {
/*  305 */       double t = y / x;
/*  306 */       w = StrictMath.sqrt(x) * StrictMath.sqrt(0.5D * (1.0D + StrictMath.sqrt(1.0D + t * t)));
/*      */     }
/*      */     else {
/*  309 */       double t = x / y;
/*  310 */       w = StrictMath.sqrt(y) * StrictMath.sqrt(0.5D * (t + StrictMath.sqrt(1.0D + t * t)));
/*      */     }
/*      */ 
/*  314 */     if (real() >= 0.0D) {
/*  315 */       return new Complex(w, imaginary() / (2.0D * w));
/*      */     }
/*  317 */     double vi = imaginary() >= 0.0D ? w : -w;
/*  318 */     return new Complex(imaginary() / (2.0D * vi), vi);
/*      */   }
/*      */ 
/*      */   public Complex exp()
/*      */   {
/*  328 */     double rho = StrictMath.exp(real());
/*  329 */     double theta = imaginary();
/*      */ 
/*  331 */     return new Complex(rho * StrictMath.cos(theta), rho * StrictMath.sin(theta));
/*      */   }
/*      */ 
/*      */   public Complex tenx()
/*      */   {
/*  340 */     return multiply(LOG10).exp();
/*      */   }
/*      */ 
/*      */   private double logabs()
/*      */   {
/*  348 */     double xabs = StrictMath.abs(real());
/*  349 */     double yabs = StrictMath.abs(imaginary());
/*      */     double u;
/*      */     double max;
/*      */     double u;
/*  352 */     if (xabs >= yabs) {
/*  353 */       double max = xabs;
/*  354 */       u = yabs / xabs;
/*      */     } else {
/*  356 */       max = yabs;
/*  357 */       u = xabs / yabs;
/*      */     }
/*      */ 
/*  362 */     return StrictMath.log(max) + 0.5D * StrictMath.log1p(u * u);
/*      */   }
/*      */ 
/*      */   private Complex pow(int r)
/*      */   {
/*  371 */     Complex p = new Complex(real(), imaginary());
/*  372 */     Complex y = new Complex(1.0D, 0.0D);
/*  373 */     boolean flag = false;
/*      */ 
/*  375 */     while (r > 0) {
/*  376 */       if (r % 2 == 1) {
/*  377 */         if (flag) {
/*  378 */           y = y.multiply(p);
/*      */         } else {
/*  380 */           flag = true;
/*  381 */           y = new Complex(p.real(), p.imaginary());
/*      */         }
/*      */       }
/*  384 */       r /= 2;
/*  385 */       p = p.square();
/*      */     }
/*  387 */     return y;
/*      */   }
/*      */ 
/*      */   private Complex root(int r)
/*      */   {
/*  395 */     double abs_error = 2.220446049250313E-016D;
/*  396 */     int limit = 10;
/*      */ 
/*  398 */     if (r == 2) return sqrt();
/*      */ 
/*  401 */     double logr = logabs();
/*  402 */     double theta = arg();
/*      */ 
/*  404 */     double rho = StrictMath.exp(logr / r);
/*  405 */     double beta = theta / r;
/*      */ 
/*  407 */     Complex x = new Complex(rho * StrictMath.cos(beta), rho * StrictMath.sin(beta));
/*      */ 
/*  410 */     int count = 0;
/*  411 */     for (double error = 1.0D; (error > 2.220446049250313E-016D) && (count < 10); count++) {
/*  412 */       Complex x_next = x.scale(r - 1).add(divide(x.pow(r - 1)));
/*  413 */       x_next = x_next.scale(1.0D / r);
/*  414 */       error = subtract(x_next.pow(r)).abs() + x_next.subtract(x).abs();
/*  415 */       x = new Complex(x_next.real(), x_next.imaginary());
/*      */     }
/*  417 */     return x;
/*      */   }
/*      */ 
/*      */   public Complex pow(Complex r)
/*      */   {
/*  426 */     if ((real() == 0.0D) && (imaginary() == 0.0D)) {
/*  427 */       if ((r.real() == 0.0D) && (r.imaginary() == 0.0D)) {
/*  428 */         return new Complex(1.0D, 0.0D);
/*      */       }
/*  430 */       return new Complex();
/*      */     }
/*  432 */     if (r.imaginary() == 0.0D) {
/*  433 */       int n = (int)r.real();
/*  434 */       if (r.real() == n) {
/*  435 */         if (n > 0) return pow(n);
/*  436 */         return pow(-n).inverse();
/*      */       }
/*      */     }
/*  439 */     double logr = logabs();
/*  440 */     double theta = arg();
/*      */ 
/*  442 */     double rr = r.real();
/*  443 */     double ri = r.imaginary();
/*      */ 
/*  445 */     double rho = StrictMath.exp(logr * rr - ri * theta);
/*  446 */     double beta = theta * rr + ri * logr;
/*      */ 
/*  448 */     return new Complex(rho * StrictMath.cos(beta), rho * StrictMath.sin(beta));
/*      */   }
/*      */ 
/*      */   public Complex root(Complex r)
/*      */   {
/*  458 */     if (r.imaginary() == 0.0D) {
/*  459 */       if (r.real() == 0.0D)
/*      */       {
/*  461 */         if (arg() != 0.0D) return new Complex((0.0D / 0.0D), (0.0D / 0.0D));
/*  462 */         if (abs() == 1.0D) return new Complex(1.0D, 0.0D);
/*  463 */         if (abs() < 1.0D) return new Complex(0.0D, 0.0D);
/*  464 */         if (abs() > 1.0D) return new Complex((1.0D / 0.0D), 0.0D);
/*  465 */         return null;
/*      */       }
/*  467 */       int n = (int)r.real();
/*  468 */       if (r.real() == n) {
/*  469 */         if (n > 0) return root(n);
/*  470 */         return root(n).inverse();
/*      */       }
/*      */     }
/*  473 */     return pow(r.inverse());
/*      */   }
/*      */ 
/*      */   public Complex log()
/*      */   {
/*  481 */     double logr = logabs();
/*  482 */     double theta = arg();
/*      */ 
/*  484 */     return new Complex(logr, theta);
/*      */   }
/*      */ 
/*      */   public Complex log10()
/*      */   {
/*  492 */     double sc = 1.0D / StrictMath.log(10.0D);
/*  493 */     return log().scale(sc);
/*      */   }
/*      */ 
/*      */   public Complex cuberoot()
/*      */   {
/*  502 */     return root(3);
/*      */   }
/*      */ 
/*      */   public Complex sin()
/*      */   {
/*  514 */     double r = real();
/*  515 */     double i = imaginary();
/*      */ 
/*  517 */     if (i == 0.0D)
/*      */     {
/*  519 */       return new Complex(StrictMath.sin(r), 0.0D);
/*      */     }
/*  521 */     return new Complex(StrictMath.sin(r) * StrictMath.cosh(i), StrictMath.cos(r) * StrictMath.sinh(i));
/*      */   }
/*      */ 
/*      */   public Complex cos()
/*      */   {
/*  531 */     double r = real();
/*  532 */     double i = imaginary();
/*      */ 
/*  534 */     if (i == 0.0D)
/*      */     {
/*  536 */       return new Complex(StrictMath.cos(r), 0.0D);
/*      */     }
/*  538 */     return new Complex(StrictMath.cos(r) * StrictMath.cosh(i), StrictMath.sin(r) * StrictMath.sinh(-i));
/*      */   }
/*      */ 
/*      */   public Complex tan()
/*      */   {
/*  549 */     double r = real();
/*  550 */     double i = imaginary();
/*  551 */     if (i == 0.0D) {
/*  552 */       double s = 2.0D * r - 3.141592653589793D;
/*  553 */       long t = StrictMath.round(s / 3.141592653589793D);
/*  554 */       if ((t % 2L == 0L) && (s == t * 3.141592653589793D)) {
/*  555 */         return new Complex((0.0D / 0.0D), (0.0D / 0.0D));
/*      */       }
/*  557 */       return new Complex(StrictMath.tan(r), 0.0D);
/*      */     }
/*      */ 
/*  560 */     if (StrictMath.abs(i) < 1.0D) {
/*  561 */       double cr = StrictMath.cos(r);
/*  562 */       double si = StrictMath.sinh(i);
/*  563 */       double d = cr * cr + si * si;
/*      */ 
/*  565 */       return new Complex(0.5D * StrictMath.sin(2.0D * r) / d, 0.5D * StrictMath.sinh(2.0D * i) / d);
/*      */     }
/*      */ 
/*  568 */     double u = StrictMath.exp(-i);
/*  569 */     double c = 2.0D * u / (1.0D - u * u);
/*  570 */     double cr = StrictMath.cos(r);
/*  571 */     double s = c * c;
/*  572 */     double d = 1.0D + cr * cr * s;
/*      */ 
/*  574 */     double t = 1.0D / StrictMath.tanh(i);
/*      */ 
/*  576 */     return new Complex(0.5D * StrictMath.sin(2.0D * r) * s / d, t / d);
/*      */   }
/*      */ 
/*      */   private double acosh_real(double y)
/*      */   {
/*  586 */     return StrictMath.log(y + StrictMath.sqrt(y * y - 1.0D));
/*      */   }
/*      */ 
/*      */   private Complex asin_real(double a)
/*      */   {
/*  595 */     if (StrictMath.abs(a) <= 1.0D) {
/*  596 */       return new Complex(StrictMath.asin(a), 0.0D);
/*      */     }
/*  598 */     if (a < 0.0D) {
/*  599 */       return new Complex(-1.570796326794897D, acosh_real(-a));
/*      */     }
/*  601 */     return new Complex(1.570796326794897D, -acosh_real(a));
/*      */   }
/*      */ 
/*      */   public Complex asin()
/*      */   {
/*  609 */     double R = real();
/*  610 */     double I = imaginary();
/*      */ 
/*  612 */     if (I == 0.0D) {
/*  613 */       return asin_real(R);
/*      */     }
/*  615 */     double x = StrictMath.abs(R);
/*  616 */     double y = StrictMath.abs(I);
/*  617 */     double r = StrictMath.hypot(x + 1.0D, y);
/*  618 */     double s = StrictMath.hypot(x - 1.0D, y);
/*  619 */     double A = 0.5D * (r + s);
/*  620 */     double B = x / A;
/*  621 */     double y2 = y * y;
/*      */ 
/*  625 */     double A_crossover = 1.5D;
/*  626 */     double B_crossover = 0.6417000000000001D;
/*      */     double real;
/*      */     double real;
/*  628 */     if (B <= 0.6417000000000001D) {
/*  629 */       real = StrictMath.asin(B);
/*      */     }
/*      */     else
/*      */     {
/*      */       double real;
/*  631 */       if (x <= 1.0D) {
/*  632 */         double D = 0.5D * (A + x) * (y2 / (r + x + 1.0D) + (s + (1.0D - x)));
/*  633 */         real = StrictMath.atan(x / StrictMath.sqrt(D));
/*      */       } else {
/*  635 */         double Apx = A + x;
/*  636 */         double D = 0.5D * (Apx / (r + x + 1.0D) + Apx / (s + (x - 1.0D)));
/*  637 */         real = StrictMath.atan(x / (y * StrictMath.sqrt(D)));
/*      */       }
/*      */     }
/*      */     double imag;
/*      */     double imag;
/*  641 */     if (A <= 1.5D)
/*      */     {
/*      */       double Am1;
/*      */       double Am1;
/*  644 */       if (x < 1.0D)
/*  645 */         Am1 = 0.5D * (y2 / (r + (x + 1.0D)) + y2 / (s + (1.0D - x)));
/*      */       else {
/*  647 */         Am1 = 0.5D * (y2 / (r + (x + 1.0D)) + (s + (x - 1.0D)));
/*      */       }
/*      */ 
/*  650 */       imag = StrictMath.log1p(Am1 + StrictMath.sqrt(Am1 * (A + 1.0D)));
/*      */     } else {
/*  652 */       imag = StrictMath.log(A + StrictMath.sqrt(A * A - 1.0D));
/*      */     }
/*      */ 
/*  655 */     return new Complex(R >= 0.0D ? real : -real, I >= 0.0D ? imag : -imag);
/*      */   }
/*      */ 
/*      */   private Complex acos_real(double a)
/*      */   {
/*  665 */     if (StrictMath.abs(a) <= 1.0D) {
/*  666 */       return new Complex(StrictMath.acos(a), 0.0D);
/*      */     }
/*  668 */     if (a < 0.0D) {
/*  669 */       return new Complex(3.141592653589793D, -acosh_real(-a));
/*      */     }
/*  671 */     return new Complex(0.0D, acosh_real(a));
/*      */   }
/*      */ 
/*      */   public Complex acos()
/*      */   {
/*  679 */     double R = real();
/*  680 */     double I = imaginary();
/*      */ 
/*  682 */     if (I == 0.0D) {
/*  683 */       return acos_real(R);
/*      */     }
/*  685 */     double x = StrictMath.abs(R);
/*  686 */     double y = StrictMath.abs(I);
/*  687 */     double r = StrictMath.hypot(x + 1.0D, y);
/*  688 */     double s = StrictMath.hypot(x - 1.0D, y);
/*  689 */     double A = 0.5D * (r + s);
/*  690 */     double B = x / A;
/*  691 */     double y2 = y * y;
/*      */ 
/*  696 */     double A_crossover = 1.5D;
/*  697 */     double B_crossover = 0.6417000000000001D;
/*      */     double real;
/*      */     double real;
/*  699 */     if (B <= 0.6417000000000001D) {
/*  700 */       real = StrictMath.acos(B);
/*      */     }
/*      */     else
/*      */     {
/*      */       double real;
/*  702 */       if (x <= 1.0D) {
/*  703 */         double D = 0.5D * (A + x) * (y2 / (r + x + 1.0D) + (s + (1.0D - x)));
/*  704 */         real = StrictMath.atan(StrictMath.sqrt(D) / x);
/*      */       } else {
/*  706 */         double Apx = A + x;
/*  707 */         double D = 0.5D * (Apx / (r + x + 1.0D) + Apx / (s + (x - 1.0D)));
/*  708 */         real = StrictMath.atan(y * StrictMath.sqrt(D) / x);
/*      */       }
/*      */     }
/*      */     double imag;
/*      */     double imag;
/*  712 */     if (A <= 1.5D)
/*      */     {
/*      */       double Am1;
/*      */       double Am1;
/*  715 */       if (x < 1.0D)
/*  716 */         Am1 = 0.5D * (y2 / (r + (x + 1.0D)) + y2 / (s + (1.0D - x)));
/*      */       else {
/*  718 */         Am1 = 0.5D * (y2 / (r + (x + 1.0D)) + (s + (x - 1.0D)));
/*      */       }
/*      */ 
/*  721 */       imag = StrictMath.log1p(Am1 + StrictMath.sqrt(Am1 * (A + 1.0D)));
/*      */     } else {
/*  723 */       imag = StrictMath.log(A + StrictMath.sqrt(A * A - 1.0D));
/*      */     }
/*      */ 
/*  726 */     return new Complex(R >= 0.0D ? real : 3.141592653589793D - real, I >= 0.0D ? -imag : imag);
/*      */   }
/*      */ 
/*      */   public Complex atan()
/*      */   {
/*  737 */     double R = real();
/*  738 */     double I = imaginary();
/*      */ 
/*  740 */     if (I == 0.0D) {
/*  741 */       return new Complex(StrictMath.atan(R), 0.0D);
/*      */     }
/*  743 */     double r = StrictMath.hypot(R, I);
/*      */ 
/*  745 */     double u = 2.0D * I / (1.0D + r * r);
/*      */     double imag;
/*      */     double imag;
/*  747 */     if (StrictMath.abs(u) < 0.1D) {
/*  748 */       imag = 0.25D * (StrictMath.log1p(u) - StrictMath.log1p(-u));
/*      */     } else {
/*  750 */       double A = StrictMath.hypot(R, I + 1.0D);
/*  751 */       double B = StrictMath.hypot(R, I - 1.0D);
/*  752 */       imag = 0.5D * StrictMath.log(A / B);
/*      */     }
/*      */ 
/*  755 */     if (R == 0.0D) {
/*  756 */       if (I > 1.0D)
/*  757 */         return new Complex(1.570796326794897D, imag);
/*  758 */       if (I < -1.0D) {
/*  759 */         return new Complex(-1.570796326794897D, imag);
/*      */       }
/*  761 */       return new Complex(0.0D, imag);
/*      */     }
/*      */ 
/*  764 */     return new Complex(0.5D * StrictMath.atan2(2.0D * R, (1.0D + r) * (1.0D - r)), imag);
/*      */   }
/*      */ 
/*      */   public Complex and(Complex z)
/*      */   {
/*  780 */     return new Complex(and(real(), z.real()), and(imaginary(), z.imaginary()));
/*      */   }
/*      */ 
/*      */   private static double and(double x, double y)
/*      */   {
/*  790 */     if ((Double.isNaN(x)) || (Double.isNaN(y)) || (Double.isInfinite(x)) || (Double.isInfinite(y)))
/*      */     {
/*  793 */       throw new RuntimeException("Boolean Error");
/*  794 */     }if (StrictMath.abs(y) > StrictMath.abs(x)) {
/*  795 */       double tmp = x;
/*  796 */       x = y;
/*  797 */       y = tmp;
/*      */     }
/*  799 */     long x_bits = Double.doubleToLongBits(x);
/*  800 */     boolean x_sign = x_bits >> 63 == 0L;
/*  801 */     int x_exponent = (int)(x_bits >> 52 & 0x7FF);
/*  802 */     long x_significand = x_exponent == 0 ? (x_bits & 0xFFFFFFFF) << 1 : x_bits & 0xFFFFFFFF | 0x0;
/*      */ 
/*  804 */     long y_bits = Double.doubleToLongBits(y);
/*  805 */     boolean y_sign = y_bits >> 63 == 0L;
/*  806 */     int y_exponent = (int)(y_bits >> 52 & 0x7FF);
/*  807 */     long y_significand = y_exponent == 0 ? (y_bits & 0xFFFFFFFF) << 1 : y_bits & 0xFFFFFFFF | 0x0;
/*      */ 
/*  809 */     y_significand >>= x_exponent - y_exponent;
/*      */ 
/*  812 */     x_significand &= y_significand;
/*      */ 
/*  815 */     if (x_exponent == 0) {
/*  816 */       x_significand >>= 1;
/*      */     } else {
/*  818 */       if (x_significand == 0L) return 0.0D;
/*  819 */       while ((x_significand & 0x0) == 0L) {
/*  820 */         x_significand <<= 1;
/*  821 */         x_exponent--;
/*  822 */         if (x_exponent == 0) {
/*  823 */           x_significand >>= 1;
/*      */         }
/*      */       }
/*      */ 
/*  827 */       x_significand &= 4503599627370495L;
/*      */     }
/*      */ 
/*  830 */     x_bits = x_exponent << 52;
/*  831 */     x_bits |= x_significand;
/*      */ 
/*  833 */     double result = Double.longBitsToDouble(x_bits);
/*      */ 
/*  836 */     if (((!x_sign ? 1 : 0) & (!y_sign ? 1 : 0)) != 0)
/*  837 */       result = -result;
/*  838 */     return result;
/*      */   }
/*      */ 
/*      */   public Complex or(Complex z)
/*      */   {
/*  847 */     return new Complex(or(real(), z.real()), or(imaginary(), z.imaginary()));
/*      */   }
/*      */ 
/*      */   public static double or(double x, double y)
/*      */   {
/*  857 */     if ((Double.isNaN(x)) || (Double.isNaN(y)) || (Double.isInfinite(x)) || (Double.isInfinite(y)))
/*      */     {
/*  860 */       throw new RuntimeException("Boolean Error");
/*  861 */     }if (StrictMath.abs(y) > StrictMath.abs(x)) {
/*  862 */       double tmp = x;
/*  863 */       x = y;
/*  864 */       y = tmp;
/*      */     }
/*  866 */     long x_bits = Double.doubleToLongBits(x);
/*  867 */     boolean x_sign = x_bits >> 63 == 0L;
/*  868 */     int x_exponent = (int)(x_bits >> 52 & 0x7FF);
/*  869 */     long x_significand = x_exponent == 0 ? (x_bits & 0xFFFFFFFF) << 1 : x_bits & 0xFFFFFFFF | 0x0;
/*      */ 
/*  871 */     long y_bits = Double.doubleToLongBits(y);
/*  872 */     boolean y_sign = y_bits >> 63 == 0L;
/*  873 */     int y_exponent = (int)(y_bits >> 52 & 0x7FF);
/*  874 */     long y_significand = y_exponent == 0 ? (y_bits & 0xFFFFFFFF) << 1 : y_bits & 0xFFFFFFFF | 0x0;
/*      */ 
/*  876 */     y_significand >>= x_exponent - y_exponent;
/*      */ 
/*  879 */     x_significand |= y_significand;
/*      */ 
/*  882 */     if (x_exponent == 0) {
/*  883 */       x_significand >>= 1;
/*      */     } else {
/*  885 */       if (x_significand == 0L) return 0.0D;
/*  886 */       while ((x_significand & 0x0) == 0L) {
/*  887 */         x_significand <<= 1;
/*  888 */         x_exponent--;
/*  889 */         if (x_exponent == 0) {
/*  890 */           x_significand >>= 1;
/*      */         }
/*      */       }
/*      */ 
/*  894 */       x_significand &= 4503599627370495L;
/*      */     }
/*      */ 
/*  897 */     x_bits = x_exponent << 52;
/*  898 */     x_bits |= x_significand;
/*      */ 
/*  900 */     double result = Double.longBitsToDouble(x_bits);
/*      */ 
/*  903 */     if (((!x_sign ? 1 : 0) | (!y_sign ? 1 : 0)) != 0)
/*  904 */       result = -result;
/*  905 */     return result;
/*      */   }
/*      */ 
/*      */   public Complex xor(Complex z)
/*      */   {
/*  914 */     return new Complex(xor(real(), z.real()), xor(imaginary(), z.imaginary()));
/*      */   }
/*      */ 
/*      */   public double xor(double x, double y)
/*      */   {
/*  924 */     if ((Double.isNaN(x)) || (Double.isNaN(y)) || (Double.isInfinite(x)) || (Double.isInfinite(y)))
/*      */     {
/*  927 */       throw new RuntimeException("Boolean Error");
/*  928 */     }if (StrictMath.abs(y) > StrictMath.abs(x)) {
/*  929 */       double tmp = x;
/*  930 */       x = y;
/*  931 */       y = tmp;
/*      */     }
/*  933 */     long x_bits = Double.doubleToLongBits(x);
/*  934 */     boolean x_sign = x_bits >> 63 == 0L;
/*  935 */     int x_exponent = (int)(x_bits >> 52 & 0x7FF);
/*  936 */     long x_significand = x_exponent == 0 ? (x_bits & 0xFFFFFFFF) << 1 : x_bits & 0xFFFFFFFF | 0x0;
/*      */ 
/*  938 */     long y_bits = Double.doubleToLongBits(y);
/*  939 */     boolean y_sign = y_bits >> 63 == 0L;
/*  940 */     int y_exponent = (int)(y_bits >> 52 & 0x7FF);
/*  941 */     long y_significand = y_exponent == 0 ? (y_bits & 0xFFFFFFFF) << 1 : y_bits & 0xFFFFFFFF | 0x0;
/*      */ 
/*  943 */     y_significand >>= x_exponent - y_exponent;
/*      */ 
/*  946 */     x_significand ^= y_significand;
/*      */ 
/*  949 */     if (x_exponent == 0) {
/*  950 */       x_significand >>= 1;
/*      */     } else {
/*  952 */       if (x_significand == 0L) return 0.0D;
/*  953 */       while ((x_significand & 0x0) == 0L) {
/*  954 */         x_significand <<= 1;
/*  955 */         x_exponent--;
/*  956 */         if (x_exponent == 0) {
/*  957 */           x_significand >>= 1;
/*      */         }
/*      */       }
/*      */ 
/*  961 */       x_significand &= 4503599627370495L;
/*      */     }
/*      */ 
/*  964 */     x_bits = x_exponent << 52;
/*  965 */     x_bits |= x_significand;
/*      */ 
/*  967 */     double result = Double.longBitsToDouble(x_bits);
/*      */ 
/*  970 */     if ((x_sign ^ y_sign))
/*  971 */       result = -result;
/*  972 */     return result;
/*      */   }
/*      */ 
/*      */   public Complex factorial()
/*      */   {
/*  980 */     double x = real();
/*  981 */     if ((imaginary() != 0.0D) || (x < 0.0D) || (StrictMath.round(x) - x != 0.0D))
/*  982 */       throw new ArithmeticException("Factorial error");
/*  983 */     if (x > 1024.0D)
/*  984 */       return new Complex((1.0D / 0.0D));
/*      */     try
/*      */     {
/*  987 */       return new Complex(factorial(StrictMath.round(x)), 0.0D); } catch (Exception e) {
/*      */     }
/*  989 */     throw new ArithmeticException("Factorial error");
/*      */   }
/*      */ 
/*      */   private static double factorial(long x)
/*      */   {
/*  999 */     if (x == 0L) {
/* 1000 */       return 1.0D;
/*      */     }
/* 1002 */     return x * factorial(x - 1L);
/*      */   }
/*      */ 
/*      */   public Complex combination(Complex z)
/*      */   {
/* 1012 */     double x = real();
/* 1013 */     double y = z.real();
/* 1014 */     if ((imaginary() != 0.0D) || (x < 0.0D) || (StrictMath.round(x) - x != 0.0D))
/* 1015 */       throw new ArithmeticException("Combination error");
/* 1016 */     if ((z.imaginary() != 0.0D) || (y < 0.0D) || (y > x) || (StrictMath.round(y) - y != 0.0D))
/* 1017 */       throw new ArithmeticException("Combination error");
/*      */     try
/*      */     {
/* 1020 */       return new Complex(combination(StrictMath.round(x), StrictMath.round(y)), 0.0D);
/*      */     } catch (Exception e) {
/*      */     }
/* 1023 */     throw new ArithmeticException("Combination error");
/*      */   }
/*      */ 
/*      */   private static double combination(long x, long y)
/*      */   {
/* 1035 */     if (y == 0L) {
/* 1036 */       return 1.0D;
/*      */     }
/* 1038 */     return x / y * combination(x - 1L, y - 1L);
/*      */   }
/*      */ 
/*      */   public Complex permutation(Complex z)
/*      */   {
/* 1048 */     double x = real();
/* 1049 */     double y = z.real();
/* 1050 */     if ((imaginary() != 0.0D) || (x < 0.0D) || (StrictMath.round(x) - x != 0.0D))
/* 1051 */       throw new ArithmeticException("Combination error");
/* 1052 */     if ((z.imaginary() != 0.0D) || (y < 0.0D) || (y > x) || (StrictMath.round(y) - y != 0.0D))
/* 1053 */       throw new ArithmeticException("Combination error");
/*      */     try
/*      */     {
/* 1056 */       return new Complex(permutation(StrictMath.round(x), StrictMath.round(y)), 0.0D);
/*      */     } catch (Exception e) {
/*      */     }
/* 1059 */     throw new ArithmeticException("Permutation error");
/*      */   }
/*      */ 
/*      */   private static double permutation(long x, long y)
/*      */   {
/* 1071 */     if (y == 0L) {
/* 1072 */       return 1.0D;
/*      */     }
/* 1074 */     return x * permutation(x - 1L, y - 1L);
/*      */   }
/*      */ 
/*      */   public StringArray toHTMLSubString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*      */   {
/* 1099 */     StringArray result = new StringArray();
/*      */ 
/* 1101 */     if ((Double.isNaN(real())) || (Double.isNaN(imaginary()))) {
/* 1102 */       String[] error = { "E", "r", "r", "o", "r" };
/* 1103 */       result.add(error);
/* 1104 */       return result;
/*      */     }
/*      */ 
/* 1108 */     if (maxChars < 4) {
/* 1109 */       throw new RuntimeException("Complex.toHTMLStringmaxChars must be at least four");
/*      */     }
/* 1111 */     if (precision < 1) {
/* 1112 */       throw new RuntimeException("Complex.toHTMLStringprecision must be positive");
/*      */     }
/*      */ 
/* 1115 */     Notation n = new Notation();
/* 1116 */     if (notation.standard()) n.setStandard(); else
/* 1117 */       n.setScientific();
/* 1118 */     if (notation.complex()) n.setComplex(); else
/* 1119 */       n.setNonComplex();
/* 1120 */     if (notation.rectangular()) n.setRectangular(); else {
/* 1121 */       n.setPolar();
/*      */     }
/* 1123 */     if (n.standard()) {
/* 1124 */       result = tryHTMLString(maxChars, precision, base, n).stringVector;
/* 1125 */       if (result != null) return result;
/*      */     }
/*      */ 
/* 1128 */     n.setScientific();
/*      */ 
/* 1130 */     result = tryHTMLString(maxChars, precision, base, n).stringVector;
/* 1131 */     if (result != null) return result;
/*      */ 
/* 1133 */     result = new StringArray();
/* 1134 */     String[] overflow = { "O", "v", "e", "r", "f", "l", "o", "w" };
/* 1135 */     result.add(overflow);
/* 1136 */     return result;
/*      */   }
/*      */ 
/*      */   public StringArray toHTMLParenString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*      */   {
/* 1144 */     StringArray v = new StringArray();
/*      */ 
/* 1146 */     if ((Double.isNaN(real())) || (Double.isNaN(imaginary()))) {
/* 1147 */       String[] error = { "E", "r", "r", "o", "r" };
/* 1148 */       v.add(error);
/* 1149 */       return v;
/*      */     }
/*      */ 
/* 1153 */     if (maxChars < 4) {
/* 1154 */       throw new RuntimeException("Complex.toHTMLStringmaxChars must be at least four");
/*      */     }
/* 1156 */     if (precision < 1) {
/* 1157 */       throw new RuntimeException("Complex.toHTMLStringprecision must be positive");
/*      */     }
/*      */ 
/* 1160 */     Notation n = new Notation();
/* 1161 */     if (notation.standard()) n.setStandard(); else
/* 1162 */       n.setScientific();
/* 1163 */     if (notation.complex()) n.setComplex(); else
/* 1164 */       n.setNonComplex();
/* 1165 */     if (notation.rectangular()) n.setRectangular(); else {
/* 1166 */       n.setPolar();
/*      */     }
/*      */ 
/* 1169 */     if (n.standard()) {
/* 1170 */       HTMLStringResult result = tryHTMLString(maxChars, precision, base, n);
/* 1171 */       if (result.stringVector != null) {
/* 1172 */         if (result.parentheses.booleanValue()) {
/* 1173 */           v.add("(");
/* 1174 */           v.addAll(result.stringVector);
/* 1175 */           v.add(")");
/* 1176 */           return v;
/*      */         }
/* 1178 */         return result.stringVector;
/*      */       }
/*      */     }
/*      */ 
/* 1182 */     n.setScientific();
/*      */ 
/* 1184 */     HTMLStringResult result = tryHTMLString(maxChars, precision, base, n);
/* 1185 */     if (result.stringVector != null) {
/* 1186 */       if (result.parentheses.booleanValue()) {
/* 1187 */         v.add("(");
/* 1188 */         v.addAll(result.stringVector);
/* 1189 */         v.add(")");
/* 1190 */         return v;
/*      */       }
/* 1192 */       return result.stringVector;
/*      */     }
/* 1194 */     String[] overflow = { "O", "v", "e", "r", "f", "l", "o", "w" };
/* 1195 */     v.add(overflow);
/* 1196 */     return v;
/*      */   }
/*      */ 
/*      */   private HTMLStringResult tryHTMLString(int maxChars, int precision, Base base, Notation notation)
/*      */   {
/* 1247 */     HTMLStringResult result = new HTMLStringResult();
/*      */ 
/* 1250 */     Notation xn = new Notation();
/* 1251 */     Notation yn = new Notation();
/*      */     DoubleFormat x;
/*      */     DoubleFormat y;
/* 1252 */     if (notation.rectangular()) {
/* 1253 */       DoubleFormat x = new DoubleFormat(real(), base);
/* 1254 */       DoubleFormat y = new DoubleFormat(imaginary(), base);
/* 1255 */       if (notation.scientific()) {
/* 1256 */         xn.setScientific();
/* 1257 */         yn.setScientific();
/*      */       }
/*      */     } else {
/* 1260 */       x = new DoubleFormat(abs(), base);
/* 1261 */       y = new DoubleFormat(arg(), base);
/* 1262 */       if (notation.scientific()) {
/* 1263 */         xn.setScientific();
/*      */       }
/*      */     }
/* 1266 */     x.setNotation(xn);
/* 1267 */     y.setNotation(yn);
/* 1268 */     int a = precision;
/* 1269 */     int b = precision;
/*      */ 
/* 1271 */     while (b > 0)
/*      */     {
/* 1273 */       DoubleFormat.HTMLStringRepresentation xr = x.NullRepresentation;
/* 1274 */       DoubleFormat.HTMLStringRepresentation yr = y.NullRepresentation;
/*      */ 
/* 1276 */       if ((notation.complex()) || (y.getAbsNumber() > 1.0E-010D)) {
/* 1277 */         y.setPrecision(b);
/* 1278 */         yr = y.representation();
/*      */       }
/*      */ 
/* 1281 */       if ((yr != y.NullRepresentation) && (!notation.complex())) { if ((x.getAbsNumber() == (notation.rectangular() ? 0 : 1)) && ((!notation.polar()) || (!notation.scientific())));
/*      */       } else {
/* 1284 */         x.setPrecision(a);
/* 1285 */         xr = x.representation();
/*      */       }
/*      */ 
/* 1288 */       int length = xr.length + yr.length;
/*      */ 
/* 1290 */       if (notation.rectangular()) {
/* 1291 */         if ((xr != x.NullRepresentation) && (yr != y.NullRepresentation) && (imaginary() >= 0.0D))
/* 1292 */           length++;
/* 1293 */         if (yr != y.NullRepresentation) length++;
/* 1294 */         if ((yr.isOne()) || (yr.isMinusOne()))
/* 1295 */           length--;
/*      */       } else {
/* 1297 */         if (yr != y.NullRepresentation) {
/* 1298 */           length++;
/* 1299 */           length++;
/*      */         }
/* 1301 */         if ((yr.isOne()) || (yr.isMinusOne())) {
/* 1302 */           length--;
/*      */         }
/*      */       }
/* 1305 */       if (length <= maxChars) {
/* 1306 */         StringArray stringBuffer = new StringArray();
/* 1307 */         stringBuffer.addAll(xr.string);
/* 1308 */         if (notation.rectangular()) {
/* 1309 */           if ((xr != x.NullRepresentation) && (yr != y.NullRepresentation)) {
/* 1310 */             result.parentheses = Boolean.valueOf(true);
/* 1311 */             if (imaginary() >= 0.0D) {
/* 1312 */               stringBuffer.add(DoubleFormat.plus);
/*      */             }
/*      */           }
/*      */         }
/* 1316 */         else if (yr != y.NullRepresentation) {
/* 1317 */           stringBuffer.add(DoubleFormat.argumentPrefix);
/* 1318 */           ((Vector)stringBuffer.lastElement()).setElementAt(((String)((Vector)stringBuffer.lastElement()).lastElement()).concat("<sup>"), ((Vector)stringBuffer.lastElement()).size() - 1);
/*      */         }
/*      */ 
/* 1325 */         if (!yr.string.equals("1"))
/*      */         {
/* 1327 */           if (yr.isMinusOne())
/* 1328 */             stringBuffer.add(DoubleFormat.minus);
/*      */           else
/* 1330 */             stringBuffer.addAll(yr.string); 
/*      */         }
/* 1331 */         if (notation.rectangular()) {
/* 1332 */           if (yr != y.NullRepresentation)
/* 1333 */             stringBuffer.add(DoubleFormat.imPrefix);
/*      */         }
/* 1335 */         else if (yr != y.NullRepresentation) {
/* 1336 */           stringBuffer.add(DoubleFormat.imPrefix);
/* 1337 */           ((Vector)stringBuffer.lastElement()).setElementAt(((String)((Vector)stringBuffer.lastElement()).lastElement()).concat("</sup>"), ((Vector)stringBuffer.lastElement()).size() - 1);
/*      */         }
/*      */ 
/* 1344 */         result.stringVector = new StringArray();
/* 1345 */         result.stringVector.addAll(stringBuffer);
/* 1346 */         return result;
/*      */       }
/*      */ 
/* 1349 */       if (a == b)
/* 1350 */         b--;
/*      */       else
/* 1352 */         a--;
/*      */     }
/* 1354 */     return result;
/*      */   }
/*      */ 
/*      */   private static int min(int x, int y)
/*      */   {
/* 1364 */     return x > y ? y : x;
/*      */   }
/*      */ 
/*      */   public int compareTo(Complex complex)
/*      */   {
/* 1373 */     double a = abs();
/* 1374 */     double b = complex.abs();
/* 1375 */     if (a < b) return -1;
/* 1376 */     if (a > b) return 1;
/* 1377 */     a = arg();
/* 1378 */     b = complex.arg();
/* 1379 */     if (a > b) return -1;
/* 1380 */     if (a < b) return 1;
/* 1381 */     return 0;
/*      */   }
/*      */ 
/*      */   private class HTMLStringResult
/*      */   {
/*      */     public StringArray stringVector;
/*      */     public Boolean parentheses;
/*      */ 
/*      */     HTMLStringResult()
/*      */     {
/* 1208 */       this.stringVector = null;
/* 1209 */       this.parentheses = Boolean.valueOf(false);
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.complex.Complex
 * JD-Core Version:    0.6.0
 */