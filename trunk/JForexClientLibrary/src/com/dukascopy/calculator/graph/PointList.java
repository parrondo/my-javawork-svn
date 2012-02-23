/*     */ package com.dukascopy.calculator.graph;
/*     */ 
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.geom.Path2D.Double;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Vector;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class PointList extends Vector<Point>
/*     */ {
/* 130 */   private final double eps = 1.0E-010D;
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public void addToPath(Path2D.Double path, AtomicBoolean restart)
/*     */   {
/*  19 */     System.out.println("add to path");
/*  20 */     if (size() == 0)
/*  21 */       return;
/*  22 */     if (size() == 1)
/*     */     {
/*  24 */       Point p = (Point)firstElement();
/*  25 */       path.moveTo(p.getX(), p.getY());
/*     */     } else {
/*  27 */       double[][] c = controlPoints(restart);
/*  28 */       if (restart.get()) return;
/*     */ 
/*  30 */       Point p = (Point)firstElement();
/*  31 */       double x0 = p.getX();
/*  32 */       double y0 = p.getY();
/*  33 */       path.moveTo(x0, y0);
/*  34 */       for (int j = 1; j < size(); j++)
/*     */       {
/*  36 */         double x1 = c[0][(2 * j - 2)];
/*  37 */         double x2 = c[0][(2 * j - 1)];
/*  38 */         double y1 = c[1][(2 * j - 2)];
/*  39 */         double y2 = c[1][(2 * j - 1)];
/*     */ 
/*  41 */         p = (Point)elementAt(j);
/*  42 */         double x3 = p.getX();
/*  43 */         double y3 = p.getY();
/*  44 */         path.curveTo(x1, y1, x2, y2, x3, y3);
/*     */ 
/*  46 */         x0 = x3;
/*  47 */         y0 = y3;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private double gradient(int j, double[] derivative2)
/*     */   {
/*  58 */     if (j == 0) {
/*  59 */       Point pjm = (Point)elementAt(j);
/*  60 */       Point pj = (Point)elementAt(j + 1);
/*  61 */       double x = pj.getX() - pjm.getX();
/*  62 */       double y = pj.getY() - pjm.getY();
/*  63 */       return y / x - x * (derivative2[(j + 1)] - derivative2[j]) / 6.0D + x * derivative2[(j + 1)] / 2.0D;
/*     */     }
/*     */ 
/*  66 */     Point pjm = (Point)elementAt(j - 1);
/*  67 */     Point pj = (Point)elementAt(j);
/*  68 */     double x = pj.getX() - pjm.getX();
/*  69 */     double y = pj.getY() - pjm.getY();
/*  70 */     return y / x - x * (derivative2[j] - derivative2[(j - 1)]) / 6.0D - x * derivative2[(j - 1)] / 2.0D;
/*     */   }
/*     */ 
/*     */   private double[][] controlPoints(AtomicBoolean restart)
/*     */   {
/*  85 */     int N = size();
/*  86 */     double[][] c = new double[2][2 * N - 2];
/*     */ 
/*  88 */     double[][] A = new double[2 * N - 2][2 * N - 2];
/*  89 */     A[0][0] = 3.0D;
/*  90 */     for (int i = 1; i < N - 1; i++) {
/*  91 */       if (restart.get()) return (double[][])null;
/*  92 */       A[(2 * i - 1)][(2 * i - 2)] = 1.0D;
/*  93 */       A[(2 * i - 1)][(2 * i - 1)] = -2.0D;
/*  94 */       A[(2 * i - 1)][(2 * i)] = 2.0D;
/*  95 */       A[(2 * i - 1)][(2 * i + 1)] = -1.0D;
/*  96 */       A[(2 * i)][(2 * i - 1)] = 1.0D;
/*  97 */       A[(2 * i)][(2 * i)] = 1.0D;
/*     */     }
/*  99 */     A[(2 * N - 3)][(2 * N - 3)] = 3.0D;
/*     */ 
/* 101 */     LU_decompose(A, restart);
/* 102 */     if (restart.get()) return (double[][])null;
/*     */ 
/* 104 */     double[] b = new double[2 * N - 2];
/*     */ 
/* 106 */     b[0] = (2.0D * ((Point)elementAt(0)).getX() + ((Point)elementAt(1)).getX());
/* 107 */     for (int i = 1; i < N - 1; i++) {
/* 108 */       if (restart.get()) return (double[][])null;
/* 109 */       b[(2 * i)] = (2.0D * ((Point)elementAt(i)).getX());
/*     */     }
/* 111 */     b[(2 * N - 3)] = (((Point)elementAt(N - 2)).getX() + 2.0D * ((Point)elementAt(N - 1)).getX());
/*     */ 
/* 113 */     c[0] = LU_solve(A, b, restart);
/* 114 */     if (restart.get()) return (double[][])null;
/*     */ 
/* 116 */     b[0] = (2.0D * ((Point)elementAt(0)).getY() + ((Point)elementAt(1)).getY());
/* 117 */     for (int i = 1; i < N - 1; i++) {
/* 118 */       b[(2 * i)] = (2.0D * ((Point)elementAt(i)).getY());
/*     */     }
/* 120 */     b[(2 * N - 3)] = (((Point)elementAt(N - 2)).getY() + 2.0D * ((Point)elementAt(N - 1)).getY());
/*     */ 
/* 122 */     c[1] = LU_solve(A, b, restart);
/* 123 */     if (restart.get()) return (double[][])null;
/* 124 */     return c;
/*     */   }
/*     */ 
/*     */   private static void LU_decompose(double[][] A, AtomicBoolean restart)
/*     */   {
/* 157 */     int n = A.length;
/* 158 */     for (int i = 0; i < n - 1; i++)
/* 159 */       for (int j = i + 1; j < n; j++) {
/* 160 */         if (restart.get()) return;
/* 161 */         A[j][i] /= A[i][i];
/* 162 */         for (int k = i + 1; k < n; k++)
/* 163 */           A[j][k] -= A[j][i] * A[i][k];
/*     */       }
/*     */   }
/*     */ 
/*     */   private static double[] LU_solve(double[][] LU, double[] b, AtomicBoolean restart)
/*     */   {
/* 177 */     int n = b.length;
/* 178 */     double[] x = new double[n];
/*     */ 
/* 180 */     x[0] = b[0];
/* 181 */     for (int i = 1; i < n; i++) {
/* 182 */       if (restart.get()) return null;
/* 183 */       double total = 0.0D;
/* 184 */       for (int j = 0; j < i; j++) {
/* 185 */         total += LU[i][j] * x[j];
/*     */       }
/* 187 */       b[i] -= total;
/*     */     }
/*     */ 
/* 190 */     x[(n - 1)] /= LU[(n - 1)][(n - 1)];
/* 191 */     for (int i = n - 2; i >= 0; i--) {
/* 192 */       if (restart.get()) return null;
/* 193 */       double total = 0.0D;
/* 194 */       for (int j = i + 1; j < n; j++) {
/* 195 */         total += LU[i][j] * x[j];
/*     */       }
/* 197 */       x[i] -= total;
/* 198 */       x[i] /= LU[i][i];
/*     */     }
/* 200 */     return x;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 208 */     Path2D.Double path = new Path2D.Double();
/* 209 */     PointList pointList = new PointList();
/* 210 */     pointList.add(new Point(100.0D, 280.0D));
/* 211 */     pointList.add(new Point(200.0D, 220.0D));
/* 212 */     pointList.add(new Point(300.0D, 200.0D));
/* 213 */     pointList.add(new Point(400.0D, 180.0D));
/* 214 */     pointList.add(new Point(500.0D, 120.0D));
/* 215 */     AtomicBoolean restart = new AtomicBoolean(false);
/*     */ 
/* 217 */     pointList.addToPath(path, restart);
/* 218 */     MyView view = new MyView(path);
/* 219 */     JFrame frame = new JFrame("PointList Test");
/* 220 */     frame.setSize(600, 400);
/* 221 */     frame.setContentPane(view);
/* 222 */     frame.setDefaultCloseOperation(3);
/* 223 */     frame.setVisible(true);
/*     */   }
/*     */ 
/*     */   static class MyView extends JPanel
/*     */   {
/*     */     Path2D.Double path;
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     MyView(Path2D.Double path)
/*     */     {
/* 134 */       this.path = path;
/*     */     }
/*     */     public void paint(Graphics g) {
/* 137 */       Graphics2D graphics2d = (Graphics2D)g;
/* 138 */       graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/* 140 */       graphics2d.draw(this.path);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.PointList
 * JD-Core Version:    0.6.0
 */