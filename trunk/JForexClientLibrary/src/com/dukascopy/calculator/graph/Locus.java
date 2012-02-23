/*     */ package com.dukascopy.calculator.graph;
/*     */ 
/*     */ import com.dukascopy.calculator.OObject;
/*     */ import com.dukascopy.calculator.Substitution;
/*     */ import com.dukascopy.calculator.complex.Complex;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.Path2D.Double;
/*     */ import java.io.PrintStream;
/*     */ import java.util.LinkedList;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Vector;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ public class Locus extends Element
/*     */   implements Runnable
/*     */ {
/* 246 */   protected static final com.dukascopy.calculator.expression.Variable variable = new com.dukascopy.calculator.expression.Variable(new com.dukascopy.calculator.function.Variable('x'));
/*     */   protected Substitution substitution;
/*     */   protected static final double epsilon = 1.E-032D;
/*     */   protected static final double delta = 1.0E-008D;
/* 291 */   private double incrementValue = 10.0D;
/*     */ 
/* 295 */   private double distance = 16.0D;
/*     */   private final OObject oobject;
/*     */   private Path2D.Double path;
/*     */   private View view;
/*     */   private Thread thread;
/*     */   private Object lock;
/*     */   AtomicBoolean restart;
/*     */ 
/*     */   public Locus(OObject oobject, View view)
/*     */   {
/*  17 */     this.lock = new Object();
/*  18 */     this.oobject = oobject;
/*  19 */     this.path = new Path2D.Double();
/*     */ 
/*  21 */     this.incrementValue = 2.0D;
/*  22 */     this.substitution = new Substitution();
/*  23 */     setup(view);
/*     */   }
/*     */ 
/*     */   public void setup(View view)
/*     */   {
/*  31 */     System.out.println("Locus.setup()");
/*  32 */     this.view = view;
/*  33 */     this.restart = new AtomicBoolean(false);
/*  34 */     this.thread = new Thread(this);
/*  35 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  42 */     Thread thisThread = Thread.currentThread();
/*  43 */     while (this.thread == thisThread) {
/*  44 */       this.restart.set(false);
/*  45 */       double xv = -this.view.distance();
/*  46 */       LinkedList list = new LinkedList();
/*     */ 
/*  49 */       Transformation t = this.view.getTransformation();
/*     */ 
/*  52 */       for (double x = -this.view.distance(); x < this.view.getWidth() + this.view.distance(); )
/*     */       {
/*  54 */         list.add(new Point(x, function(x, t)));
/*  55 */         if (this.restart.get() == true)
/*     */           break;
/*  53 */         x += increment();
/*     */       }
/*     */ 
/*  59 */       if (this.restart.get() == true)
/*     */       {
/*     */         continue;
/*     */       }
/*  63 */       ListIterator i = list.listIterator();
/*  64 */       while ((i.hasNext()) && (!this.restart.get())) {
/*  65 */         Point p = (Point)i.next();
/*  66 */         if (!i.hasNext()) break;
/*  67 */         Point q = (Point)i.next();
/*  68 */         i.previous();
/*  69 */         double xp = p.getX();
/*  70 */         double xq = q.getX();
/*  71 */         double d = xq - xp;
/*  72 */         double yp = p.getY();
/*  73 */         double yq = q.getY();
/*     */ 
/*  75 */         if ((yp < this.view.getHeight()) && (yp > 0.0D) && ((Double.isInfinite(yq)) || (Double.isNaN(yq))))
/*     */         {
/*  77 */           if (d > 1.0E-008D) {
/*  78 */             i.add(new Point(xp + d / 2.0D, function(xp + d / 2.0D, t)));
/*  79 */             i.previous();
/*  80 */             if (i.hasPrevious()) i.previous(); 
/*     */           }
/*     */         }
/*  82 */         else if ((yq < this.view.getHeight()) && (yq > 0.0D) && ((Double.isInfinite(yp)) || (Double.isNaN(yp))))
/*     */         {
/*  85 */           if (d > 1.0E-008D) {
/*  86 */             i.add(new Point(xp + d / 2.0D, function(xp + d / 2.0D, t)));
/*  87 */             i.previous();
/*  88 */             if (i.hasPrevious()) i.previous(); 
/*     */           }
/*     */         }
/*  90 */         else if ((!Double.isInfinite(yp)) && (!Double.isNaN(yp)) && (!Double.isInfinite(yq)) && (!Double.isNaN(yq)) && (((yp < this.view.getHeight()) && (yp > 0.0D)) || ((yq < this.view.getHeight()) && (yq > 0.0D))))
/*     */         {
/*  94 */           if ((p.distance(q) > this.distance) && 
/*  95 */             (d > 1.0E-008D)) {
/*  96 */             i.add(new Point(xp + d / 2.0D, function(xp + d / 2.0D, t)));
/*  97 */             i.previous();
/*  98 */             if (i.hasPrevious()) i.previous();
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 103 */       if (this.restart.get() == true)
/*     */         continue;
/* 105 */       double lb = 0.0D;
/* 106 */       double ub = this.view.getHeight();
/* 107 */       Vector pl = new Vector();
/* 108 */       PointList pointList = new PointList();
/* 109 */       ListIterator i = list.listIterator();
/* 110 */       while ((i.hasNext()) && (!this.restart.get())) {
/* 111 */         Point p = (Point)i.next();
/* 112 */         double yp = p.getY();
/* 113 */         if ((Double.isInfinite(yp)) || (Double.isNaN(yp))) {
/*     */           continue;
/*     */         }
/* 116 */         if ((yp > lb) && (yp < ub))
/*     */         {
/* 118 */           pointList.add(p);
/*     */         }
/* 125 */         else if (pointList.isEmpty()) {
/* 126 */           if (i.hasNext()) {
/* 127 */             Point q = (Point)i.next();
/* 128 */             double yq = q.getY();
/* 129 */             i.previous();
/* 130 */             if ((!Double.isInfinite(yq)) && (!Double.isNaN(yq)) && (yq > lb) && (yq < ub))
/*     */             {
/* 133 */               pointList.add(p);
/*     */             }
/*     */           }
/*     */         } else {
/* 137 */           pointList.add(p);
/* 138 */           pl.add(pointList);
/* 139 */           pointList = new PointList();
/*     */         }
/*     */       }
/*     */ 
/* 143 */       System.out.println("restart 4");
/* 144 */       if (this.restart.get() == true)
/*     */         continue;
/* 146 */       if (!pointList.isEmpty()) {
/* 147 */         pl.add(pointList);
/*     */       }
/*     */ 
/* 150 */       Path2D.Double localPath = new Path2D.Double();
/* 151 */       ListIterator j = pl.listIterator();
/* 152 */       while (j.hasNext()) {
/* 153 */         PointList l = (PointList)j.next();
/* 154 */         l.addToPath(localPath, this.restart);
/*     */       }
/* 156 */       synchronized (this.path) {
/* 157 */         this.path = localPath;
/*     */       }
/* 159 */       this.view.setCursor(null);
/* 160 */       this.view.repaint();
/* 161 */       synchronized (this.lock) {
/* 162 */         if (this.restart.get() == true) continue;
/*     */         try {
/* 164 */           System.out.println("Waiting");
/* 165 */           this.lock.wait();
/* 166 */           System.out.println("finished waiting");
/*     */         } catch (InterruptedException exception) {
/* 168 */           System.out.println("interrupted");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updatePath()
/*     */   {
/* 178 */     if (this.thread == null) return;
/* 179 */     this.restart.set(true);
/* 180 */     synchronized (this.lock) {
/* 181 */       System.out.println("updatePath()");
/* 182 */       this.lock.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 190 */     this.thread = null;
/*     */   }
/*     */ 
/*     */   protected OObject substitute(double x)
/*     */   {
/* 199 */     this.substitution.add(variable, new Complex(x));
/* 200 */     OObject result = this.oobject.substitute(this.substitution).auto_simplify();
/* 201 */     return result;
/*     */   }
/*     */ 
/*     */   protected double function(double x, Transformation t)
/*     */   {
/* 212 */     OObject p = substitute(t.toModelX(x));
/* 213 */     if ((p instanceof Complex)) {
/* 214 */       Complex z = (Complex)p;
/* 215 */       if (Math.abs(z.imaginary()) < 1.E-032D) {
/* 216 */         double y = z.real();
/* 217 */         return t.toViewY(z.real());
/*     */       }
/*     */     }
/* 220 */     return (0.0D / 0.0D);
/*     */   }
/*     */ 
/*     */   public void draw(Model model, View view, Graphics2D graphics2d)
/*     */   {
/* 230 */     synchronized (this.path) {
/* 231 */       Rectangle r = this.path.getBounds();
/* 232 */       Rectangle v = view.getBounds();
/* 233 */       graphics2d.draw(this.path);
/*     */     }
/*     */   }
/*     */ 
/*     */   double increment()
/*     */   {
/* 241 */     return this.incrementValue;
/*     */   }
/*     */ 
/*     */   protected class FindResult
/*     */   {
/*     */     public Point point;
/*     */     public boolean interiorPoint;
/*     */     public boolean success;
/*     */ 
/*     */     FindResult(Point point, boolean interiorPoint, boolean success)
/*     */     {
/* 271 */       this.point = point;
/* 272 */       this.interiorPoint = interiorPoint;
/* 273 */       this.success = success;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.Locus
 * JD-Core Version:    0.6.0
 */