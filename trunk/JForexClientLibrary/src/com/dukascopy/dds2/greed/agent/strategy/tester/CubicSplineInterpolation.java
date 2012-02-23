/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Date;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class CubicSplineInterpolation
/*     */ {
/*  41 */   private static final Logger LOGGER = LoggerFactory.getLogger(CubicSplineInterpolation.class);
/*     */ 
/*  71 */   private int nPoints = 0;
/*     */ 
/*  73 */   private double[] y = null;
/*     */ 
/*  75 */   private double[] x = null;
/*     */ 
/*  77 */   private double[] d2ydx2 = null;
/*     */ 
/*  79 */   private double yp1 = (0.0D / 0.0D);
/*     */ 
/*  82 */   private double ypn = (0.0D / 0.0D);
/*     */ 
/*  85 */   private boolean derivCalculated = false;
/*     */ 
/*     */   public static void main(String[] arg)
/*     */   {
/*  45 */     System.out.println(new Date().getTime());
/*     */ 
/*  48 */     double[] wavelength = { 1.0D, 2.0D, 3.0D, 4.0D };
/*     */ 
/*  50 */     double[] refrindex = { 1.001D, 1.003D, 1.0D, 1.002D };
/*     */ 
/*  56 */     CubicSplineInterpolation cs = new CubicSplineInterpolation(wavelength, refrindex);
/*     */ 
/*  60 */     double x1 = 1.5D;
/*  61 */     double y1 = cs.interpolate(x1);
/*  62 */     System.out.println("The refractive index of fused quartz at " + x1 * 1000000000.0D + " nm is " + y1);
/*     */ 
/*  66 */     x1 = 2.5D;
/*  67 */     y1 = cs.interpolate(x1);
/*  68 */     System.out.println("The refractive index of fused quartz at " + x1 * 1000000000.0D + " nm is " + y1);
/*     */   }
/*     */ 
/*     */   public CubicSplineInterpolation(double[] x, double[] y)
/*     */   {
/*  91 */     this.nPoints = x.length;
/*  92 */     if (this.nPoints != y.length)
/*  93 */       throw new IllegalArgumentException("Arrays x and y are of different length" + this.nPoints + " " + y.length);
/*  94 */     if (this.nPoints < 3)
/*  95 */       throw new IllegalArgumentException("A minimum of three data points is needed");
/*  96 */     this.x = new double[this.nPoints];
/*  97 */     this.y = new double[this.nPoints];
/*  98 */     this.d2ydx2 = new double[this.nPoints];
/*  99 */     for (int i = 0; i < this.nPoints; i++) {
/* 100 */       this.x[i] = x[i];
/* 101 */       this.y[i] = y[i];
/*     */     }
/* 103 */     checkForIdenticalPoints();
/*     */   }
/*     */ 
/*     */   public CubicSplineInterpolation(int nPoints)
/*     */   {
/* 110 */     this.nPoints = nPoints;
/* 111 */     if (this.nPoints < 3)
/* 112 */       throw new IllegalArgumentException("A minimum of three data points is needed");
/* 113 */     this.x = new double[nPoints];
/* 114 */     this.y = new double[nPoints];
/* 115 */     this.d2ydx2 = new double[nPoints];
/*     */   }
/*     */ 
/*     */   public void resetData(double[] x, double[] y)
/*     */   {
/* 121 */     if (x.length != y.length)
/* 122 */       throw new IllegalArgumentException("Arrays x and y are of different length");
/* 123 */     if (this.nPoints != x.length)
/* 124 */       throw new IllegalArgumentException("Original array length not matched by new array length");
/* 125 */     for (int i = 0; i < this.nPoints; i++) {
/* 126 */       this.x[i] = x[i];
/* 127 */       this.y[i] = y[i];
/*     */     }
/* 129 */     checkForIdenticalPoints();
/*     */   }
/*     */ 
/*     */   public void checkForIdenticalPoints()
/*     */   {
/* 134 */     int nP = this.nPoints;
/* 135 */     boolean test1 = true;
/* 136 */     int ii = 0;
/* 137 */     while (test1) {
/* 138 */       boolean test2 = true;
/* 139 */       int jj = ii + 1;
/* 140 */       while (test2) {
/* 141 */         if ((this.x[ii] == this.x[jj]) && (this.y[ii] == this.y[jj])) {
/* 142 */           LOGGER.debug("Class CubicSplineInterpolation: Two identical points, " + this.x[ii] + ", " + this.y[ii]);
/* 143 */           LOGGER.debug(", in data array at indices " + ii + " and " + jj + ", one point removed");
/*     */ 
/* 145 */           for (int i = jj; i < nP; i++) {
/* 146 */             this.x[(i - 1)] = this.x[i];
/* 147 */             this.y[(i - 1)] = this.y[i];
/*     */           }
/* 149 */           nP--;
/* 150 */           if (nP - 1 == ii) {
/* 151 */             test2 = false; continue;
/*     */           }
/*     */         }
/* 153 */         jj++;
/* 154 */         if (jj >= nP) {
/* 155 */           test2 = false;
/*     */         }
/*     */       }
/* 158 */       ii++;
/* 159 */       if (ii >= nP - 1)
/* 160 */         test1 = false;
/*     */     }
/* 162 */     this.nPoints = nP;
/*     */   }
/*     */ 
/*     */   public static CubicSplineInterpolation zero(int n)
/*     */   {
/* 169 */     if (n < 3)
/* 170 */       throw new IllegalArgumentException("A minimum of three data points is needed");
/* 171 */     CubicSplineInterpolation aa = new CubicSplineInterpolation(n);
/* 172 */     return aa;
/*     */   }
/*     */ 
/*     */   public static CubicSplineInterpolation[] oneDarray(int n, int m)
/*     */   {
/* 179 */     if (m < 3)
/* 180 */       throw new IllegalArgumentException("A minimum of three data points is needed");
/* 181 */     CubicSplineInterpolation[] a = new CubicSplineInterpolation[n];
/* 182 */     for (int i = 0; i < n; i++) {
/* 183 */       a[i] = zero(m);
/*     */     }
/* 185 */     return a;
/*     */   }
/*     */ 
/*     */   public void setDerivLimits(double yp1, double ypn)
/*     */   {
/* 192 */     this.yp1 = yp1;
/* 193 */     this.ypn = ypn;
/*     */   }
/*     */ 
/*     */   public void setDerivLimits()
/*     */   {
/* 199 */     this.yp1 = (0.0D / 0.0D);
/* 200 */     this.ypn = (0.0D / 0.0D);
/*     */   }
/*     */ 
/*     */   public void setDeriv(double yp1, double ypn)
/*     */   {
/* 209 */     this.yp1 = yp1;
/* 210 */     this.ypn = ypn;
/*     */   }
/*     */ 
/*     */   public double[] getDeriv()
/*     */   {
/* 215 */     if (!this.derivCalculated)
/* 216 */       calcDeriv();
/* 217 */     return this.d2ydx2;
/*     */   }
/*     */ 
/*     */   public void setDeriv(double[] deriv)
/*     */   {
/* 223 */     this.d2ydx2 = deriv;
/* 224 */     this.derivCalculated = true;
/*     */   }
/*     */ 
/*     */   public void calcDeriv()
/*     */   {
/* 232 */     double p = 0.0D; double qn = 0.0D; double sig = 0.0D; double un = 0.0D;
/* 233 */     double[] u = new double[this.nPoints];
/*     */ 
/* 235 */     if (this.yp1 != this.yp1)
/*     */     {
/*     */       double tmp39_38 = 0.0D; u[0] = tmp39_38; this.d2ydx2[0] = tmp39_38;
/*     */     } else {
/* 238 */       this.d2ydx2[0] = -0.5D;
/* 239 */       u[0] = (3.0D / (this.x[1] - this.x[0]) * ((this.y[1] - this.y[0]) / (this.x[1] - this.x[0]) - this.yp1));
/*     */     }
/*     */ 
/* 242 */     for (int i = 1; i <= this.nPoints - 2; i++) {
/* 243 */       sig = (this.x[i] - this.x[(i - 1)]) / (this.x[(i + 1)] - this.x[(i - 1)]);
/* 244 */       p = sig * this.d2ydx2[(i - 1)] + 2.0D;
/* 245 */       this.d2ydx2[i] = ((sig - 1.0D) / p);
/* 246 */       u[i] = ((this.y[(i + 1)] - this.y[i]) / (this.x[(i + 1)] - this.x[i]) - (this.y[i] - this.y[(i - 1)]) / (this.x[i] - this.x[(i - 1)]));
/* 247 */       u[i] = ((6.0D * u[i] / (this.x[(i + 1)] - this.x[(i - 1)]) - sig * u[(i - 1)]) / p);
/*     */     }
/*     */ 
/* 250 */     if (this.ypn != this.ypn) {
/* 251 */       qn = un = 0.0D;
/*     */     } else {
/* 253 */       qn = 0.5D;
/* 254 */       un = 3.0D / (this.x[(this.nPoints - 1)] - this.x[(this.nPoints - 2)]) * (this.ypn - (this.y[(this.nPoints - 1)] - this.y[(this.nPoints - 2)]) / (this.x[(this.nPoints - 1)] - this.x[(this.nPoints - 2)]));
/*     */     }
/*     */ 
/* 257 */     this.d2ydx2[(this.nPoints - 1)] = ((un - qn * u[(this.nPoints - 2)]) / (qn * this.d2ydx2[(this.nPoints - 2)] + 1.0D));
/* 258 */     for (int k = this.nPoints - 2; k >= 0; k--) {
/* 259 */       this.d2ydx2[k] = (this.d2ydx2[k] * this.d2ydx2[(k + 1)] + u[k]);
/*     */     }
/* 261 */     this.derivCalculated = true;
/*     */   }
/*     */ 
/*     */   public double interpolate(double xx)
/*     */   {
/* 273 */     if ((xx < this.x[0]) || (xx > this.x[(this.nPoints - 1)])) {
/* 274 */       throw new IllegalArgumentException("x (" + xx + ") is outside the range of data points (" + this.x[0] + " to " + this.x[(this.nPoints - 1)]);
/*     */     }
/*     */ 
/* 277 */     if (!this.derivCalculated) {
/* 278 */       calcDeriv();
/*     */     }
/* 280 */     double h = 0.0D; double b = 0.0D; double a = 0.0D; double yy = 0.0D;
/* 281 */     int k = 0;
/* 282 */     int klo = 0;
/* 283 */     int khi = this.nPoints - 1;
/* 284 */     while (khi - klo > 1) {
/* 285 */       k = khi + klo >> 1;
/* 286 */       if (this.x[k] > xx) {
/* 287 */         khi = k; continue;
/*     */       }
/* 289 */       klo = k;
/*     */     }
/*     */ 
/* 292 */     h = this.x[khi] - this.x[klo];
/*     */ 
/* 294 */     if (h == 0.0D) {
/* 295 */       throw new IllegalArgumentException("Two values of x are identical: point " + klo + " (" + this.x[klo] + ") and point " + khi + " (" + this.x[khi] + ")");
/*     */     }
/* 297 */     a = (this.x[khi] - xx) / h;
/* 298 */     b = (xx - this.x[klo]) / h;
/* 299 */     yy = a * this.y[klo] + b * this.y[khi] + ((a * a * a - a) * this.d2ydx2[klo] + (b * b * b - b) * this.d2ydx2[khi]) * (h * h) / 6.0D;
/*     */ 
/* 301 */     return yy;
/*     */   }
/*     */ 
/*     */   public static double interpolate(double xx, double[] x, double[] y, double[] deriv)
/*     */   {
/* 309 */     if ((x.length != y.length) || (x.length != deriv.length) || (y.length != deriv.length)) {
/* 310 */       throw new IllegalArgumentException("array lengths are not all equal");
/*     */     }
/* 312 */     int n = x.length;
/* 313 */     double h = 0.0D; double b = 0.0D; double a = 0.0D; double yy = 0.0D;
/*     */ 
/* 315 */     int k = 0;
/* 316 */     int klo = 0;
/* 317 */     int khi = n - 1;
/* 318 */     while (khi - klo > 1) {
/* 319 */       k = khi + klo >> 1;
/* 320 */       if (x[k] > xx) {
/* 321 */         khi = k; continue;
/*     */       }
/* 323 */       klo = k;
/*     */     }
/*     */ 
/* 326 */     h = x[khi] - x[klo];
/*     */ 
/* 328 */     if (h == 0.0D) {
/* 329 */       throw new IllegalArgumentException("Two values of x are identical");
/*     */     }
/* 331 */     a = (x[khi] - xx) / h;
/* 332 */     b = (xx - x[klo]) / h;
/* 333 */     yy = a * y[klo] + b * y[khi] + ((a * a * a - a) * deriv[klo] + (b * b * b - b) * deriv[khi]) * (h * h) / 6.0D;
/*     */ 
/* 335 */     return yy;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.CubicSplineInterpolation
 * JD-Core Version:    0.6.0
 */