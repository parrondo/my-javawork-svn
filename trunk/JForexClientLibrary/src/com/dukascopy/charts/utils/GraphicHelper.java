/*      */ package com.dukascopy.charts.utils;
/*      */ 
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Polygon;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.geom.Area;
/*      */ import java.awt.geom.CubicCurve2D.Float;
/*      */ import java.awt.geom.Ellipse2D.Double;
/*      */ import java.awt.geom.GeneralPath;
/*      */ import java.awt.geom.PathIterator;
/*      */ import java.awt.geom.QuadCurve2D.Float;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import javax.swing.JComponent;
/*      */ 
/*      */ public final class GraphicHelper
/*      */ {
/*      */   public static void drawSegmentDashedLine(GeneralPath generalPath, double x1, double y1, double x2, double y2, double dashlength, double spacelength, int screenWidth, int screenHeight)
/*      */   {
/*   45 */     double[] coords = getSegmentLineCoordinates(x1, y1, x2, y2, screenWidth, screenHeight);
/*   46 */     if (coords != null) {
/*   47 */       x1 = coords[0];
/*   48 */       y1 = coords[1];
/*   49 */       x2 = coords[2];
/*   50 */       y2 = coords[3];
/*      */ 
/*   52 */       double linelength = (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
/*   53 */       double xincdashspace = (x2 - x1) / (linelength / (dashlength + spacelength));
/*   54 */       double yincdashspace = (y2 - y1) / (linelength / (dashlength + spacelength));
/*   55 */       double xincdash = (x2 - x1) / (linelength / dashlength);
/*   56 */       double yincdash = (y2 - y1) / (linelength / dashlength);
/*   57 */       int counter = 0;
/*   58 */       for (double i = 0.0D; i < linelength - dashlength; i += dashlength + spacelength) {
/*   59 */         generalPath.moveTo(x1 + xincdashspace * counter, y1 + yincdashspace * counter);
/*   60 */         generalPath.lineTo(x1 + xincdashspace * counter + xincdash, y1 + yincdashspace * counter + yincdash);
/*   61 */         counter++;
/*      */       }
/*   63 */       if ((dashlength + spacelength) * counter <= linelength) {
/*   64 */         generalPath.moveTo(x1 + xincdashspace * counter, y1 + yincdashspace * counter);
/*   65 */         generalPath.lineTo(x2, y2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void drawVerticalDashedLine(GeneralPath path, double x, double y1, double y2, BasicStroke stroke)
/*      */   {
/*   81 */     float[] dashedPattern = stroke.getDashArray();
/*   82 */     if (dashedPattern == null) {
/*   83 */       path.moveTo(x, y1);
/*   84 */       path.lineTo(x, y2);
/*      */     } else {
/*   86 */       int minY = (int)(y1 > y2 ? y2 : y1);
/*   87 */       int maxY = (int)(y1 > y2 ? y1 : y2);
/*      */ 
/*   89 */       if (dashedPattern.length == 1) {
/*   90 */         int dashLength = (int)dashedPattern[0];
/*   91 */         int lastY = maxY - dashLength;
/*   92 */         for (int y = minY; y < lastY; y += 2 * dashLength) {
/*   93 */           path.moveTo(x, y);
/*   94 */           path.lineTo(x, y + dashLength);
/*      */         }
/*      */       } else {
/*   97 */         int dashPos = 0;
/*   98 */         int y = minY;
/*   99 */         while (y + dashedPattern[dashPos] < maxY) {
/*  100 */           path.moveTo(x, y);
/*  101 */           y = (int)(y + dashedPattern[dashPos]);
/*  102 */           path.lineTo(x, y);
/*  103 */           dashPos++;
/*  104 */           y = (int)(y + dashedPattern[dashPos]);
/*  105 */           dashPos++;
/*  106 */           if (dashPos >= dashedPattern.length)
/*  107 */             dashPos = 0;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void drawHorizontalDashedLine(GeneralPath path, double y, double x1, double x2, BasicStroke stroke)
/*      */   {
/*  126 */     float[] dashedPattern = stroke.getDashArray();
/*  127 */     if (dashedPattern == null) {
/*  128 */       path.moveTo(x1, y);
/*  129 */       path.lineTo(x2, y);
/*      */     } else {
/*  131 */       int minX = (int)(x1 > x2 ? x2 : x1);
/*  132 */       int maxX = (int)(x1 > x2 ? x1 : x2);
/*      */ 
/*  134 */       if (dashedPattern.length == 1) {
/*  135 */         int dashLength = (int)dashedPattern[0];
/*  136 */         int lastX = maxX - dashLength;
/*  137 */         for (int x = minX; x < lastX; x += 2 * dashLength) {
/*  138 */           path.moveTo(x, y);
/*  139 */           path.lineTo(x + dashLength, y);
/*      */         }
/*      */       } else {
/*  142 */         int dashPos = 0;
/*  143 */         int x = minX;
/*  144 */         while (x + dashedPattern[dashPos] < maxX) {
/*  145 */           path.moveTo(x, y);
/*  146 */           x = (int)(x + dashedPattern[dashPos]);
/*  147 */           path.lineTo(x, y);
/*  148 */           dashPos++;
/*  149 */           x = (int)(x + dashedPattern[dashPos]);
/*  150 */           dashPos++;
/*  151 */           if (dashPos >= dashedPattern.length)
/*  152 */             dashPos = 0;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void drawSegmentDashedLine(Graphics g, int x1, int y1, int x2, int y2, double dashlength, double spacelength, Color color, int screenWidth, int screenHeight)
/*      */   {
/*  178 */     GeneralPath path = new GeneralPath();
/*  179 */     drawSegmentDashedLine(path, x1, y1, x2, y2, dashlength, spacelength, screenWidth, screenHeight);
/*      */ 
/*  181 */     Color prevColor = g.getColor();
/*  182 */     g.setColor(color);
/*      */ 
/*  184 */     ((Graphics2D)g).draw(path);
/*      */ 
/*  186 */     g.setColor(prevColor);
/*      */   }
/*      */ 
/*      */   public static void drawDashedRect(Graphics g, int x, int y, int width, int height, float dashlength, float spacelength, Color rectColor, int screenWidth, int screenHeight)
/*      */   {
/*  208 */     Color prevColor = g.getColor();
/*  209 */     g.setColor(rectColor);
/*  210 */     GeneralPath generalPath = new GeneralPath();
/*  211 */     drawSegmentDashedLine(generalPath, x, y, x + width, y, dashlength, spacelength, screenWidth, screenHeight);
/*  212 */     drawSegmentDashedLine(generalPath, x + width, y, x + width, y + height, dashlength, spacelength, screenWidth, screenHeight);
/*  213 */     drawSegmentDashedLine(generalPath, x + width, y + height, x, y + height, dashlength, spacelength, screenWidth, screenHeight);
/*  214 */     drawSegmentDashedLine(generalPath, x, y + height, x, y, dashlength, spacelength, screenWidth, screenHeight);
/*  215 */     ((Graphics2D)g).draw(generalPath);
/*  216 */     g.setColor(prevColor);
/*      */   }
/*      */ 
/*      */   public static boolean intersects(GeneralPath path, Rectangle2D rectangle)
/*      */   {
/*  228 */     float[] firstPoint = null;
/*  229 */     float[] previous = new float[2];
/*  230 */     float[] points = new float[6];
/*  231 */     PathIterator pathIterator = path.getPathIterator(null);
/*  232 */     while (!pathIterator.isDone()) {
/*  233 */       int type = pathIterator.currentSegment(points);
/*  234 */       if (type == 0) {
/*  235 */         if (firstPoint == null) {
/*  236 */           firstPoint = new float[] { points[0], points[1] };
/*      */         }
/*  238 */         previous[0] = points[0];
/*  239 */         previous[1] = points[1];
/*  240 */       } else if (type == 1) {
/*  241 */         if (rectangle.intersectsLine(previous[0], previous[1], points[0], points[1])) {
/*  242 */           return true;
/*      */         }
/*  244 */         previous[0] = points[0];
/*  245 */         previous[1] = points[1];
/*  246 */       } else if (type == 2) {
/*  247 */         QuadCurve2D.Float curve = new QuadCurve2D.Float(previous[0], previous[1], points[0], points[1], points[2], points[3]);
/*  248 */         if (curve.intersects(rectangle)) {
/*  249 */           return true;
/*      */         }
/*  251 */         previous[0] = points[2];
/*  252 */         previous[1] = points[3];
/*  253 */       } else if (type == 3) {
/*  254 */         CubicCurve2D.Float curve = new CubicCurve2D.Float(previous[0], previous[1], points[0], points[1], points[2], points[3], points[4], points[5]);
/*  255 */         if (curve.intersects(rectangle)) {
/*  256 */           return true;
/*      */         }
/*  258 */         previous[0] = points[4];
/*  259 */         previous[1] = points[5];
/*      */       }
/*      */       else
/*      */       {
/*  263 */         return (firstPoint != null) && (rectangle.intersectsLine(firstPoint[0], firstPoint[1], previous[0], previous[1]));
/*      */       }
/*      */ 
/*  267 */       pathIterator.next();
/*      */     }
/*  269 */     return false;
/*      */   }
/*      */ 
/*      */   public static double[] getSegmentLinesIntersection(double x11, double y11, double x12, double y12, double x21, double y21, double x22, double y22)
/*      */   {
/*      */     double yi;
/*      */     double xi;
/*      */     double yi;
/*  287 */     if (x12 - x11 == 0.0D)
/*      */     {
/*  289 */       if (x22 - x21 == 0.0D)
/*      */       {
/*  291 */         return null;
/*      */       }
/*  293 */       double b2 = (y22 - y21) / (x22 - x21);
/*  294 */       double a2 = y21 - b2 * x21;
/*  295 */       double xi = x11;
/*  296 */       yi = a2 + b2 * xi;
/*      */     }
/*      */     else
/*      */     {
/*      */       double yi;
/*  298 */       if (x22 - x21 == 0.0D)
/*      */       {
/*  300 */         double b1 = (y12 - y11) / (x12 - x11);
/*  301 */         double a1 = y11 - b1 * x11;
/*  302 */         double xi = x21;
/*  303 */         yi = a1 + b1 * xi;
/*      */       } else {
/*  305 */         double b1 = (y12 - y11) / (x12 - x11);
/*  306 */         double b2 = (y22 - y21) / (x22 - x21);
/*      */ 
/*  308 */         double a1 = y11 - b1 * x11;
/*  309 */         double a2 = y21 - b2 * x21;
/*      */ 
/*  311 */         if (b1 - b2 == 0.0D)
/*      */         {
/*  313 */           return null;
/*      */         }
/*  315 */         xi = -(a1 - a2) / (b1 - b2);
/*  316 */         yi = a1 + b1 * xi;
/*      */ 
/*  318 */         if ((xi < 0.001000000047497451D) && (xi > -0.001000000047497451D)) {
/*  319 */           xi = 0.0D;
/*      */         }
/*  321 */         if ((yi != 0.0D) && (yi < 0.001000000047497451D) && (yi > -0.001000000047497451D))
/*  322 */           yi = 0.0D;
/*      */       }
/*      */     }
/*  325 */     if (((x11 - xi) * (xi - x12) >= -9.999999717180685E-010D) && ((x21 - xi) * (xi - x22) >= -9.999999717180685E-010D) && ((y11 - yi) * (yi - y12) >= -9.999999717180685E-010D) && ((y21 - yi) * (yi - y22) >= -9.999999717180685E-010D)) {
/*  326 */       return new double[] { xi, yi };
/*      */     }
/*  328 */     return null;
/*      */   }
/*      */ 
/*      */   public static boolean checkIntersections(double[] coords, JComponent jComponent)
/*      */   {
/*  333 */     int chartX = jComponent.getX();
/*  334 */     int chartY = jComponent.getY();
/*  335 */     int width = jComponent.getWidth();
/*  336 */     int height = jComponent.getHeight();
/*      */ 
/*  340 */     boolean intersect = false;
/*  341 */     double[] point = getSegmentLinesIntersection(coords[0], coords[1], coords[2], coords[3], chartX, chartY, chartX + width, chartY);
/*  342 */     if (point != null) {
/*  343 */       intersect = true;
/*  344 */       if (coords[3] > coords[1]) {
/*  345 */         coords[0] = point[0];
/*  346 */         coords[1] = point[1];
/*      */       } else {
/*  348 */         coords[2] = point[0];
/*  349 */         coords[3] = point[1];
/*      */       }
/*      */     }
/*      */ 
/*  353 */     point = getSegmentLinesIntersection(coords[0], coords[1], coords[2], coords[3], chartX, chartY + height, chartX + width, chartY + height);
/*  354 */     if (point != null) {
/*  355 */       intersect = true;
/*  356 */       if (coords[1] > coords[3]) {
/*  357 */         coords[0] = point[0];
/*  358 */         coords[1] = point[1];
/*      */       } else {
/*  360 */         coords[2] = point[0];
/*  361 */         coords[3] = point[1];
/*      */       }
/*      */     }
/*      */ 
/*  365 */     point = getSegmentLinesIntersection(coords[0], coords[1], coords[2], coords[3], chartX, chartY, chartX, height);
/*  366 */     if (point != null) {
/*  367 */       intersect = true;
/*  368 */       if (coords[2] > coords[0]) {
/*  369 */         coords[0] = point[0];
/*  370 */         coords[1] = point[1];
/*      */       } else {
/*  372 */         coords[2] = point[0];
/*  373 */         coords[3] = point[1];
/*      */       }
/*      */     }
/*      */ 
/*  377 */     point = getSegmentLinesIntersection(coords[0], coords[1], coords[2], coords[3], chartX + width, chartY, chartX + width, chartY + height);
/*  378 */     if (point != null) {
/*  379 */       intersect = true;
/*  380 */       if (coords[0] > coords[2]) {
/*  381 */         coords[0] = point[0];
/*  382 */         coords[1] = point[1];
/*      */       } else {
/*  384 */         coords[2] = point[0];
/*  385 */         coords[3] = point[1];
/*      */       }
/*      */     }
/*      */ 
/*  389 */     return intersect;
/*      */   }
/*      */ 
/*      */   public static boolean drawInfiniteLine(GeneralPath path, double x1, double y1, double x2, double y2, int screenWidth, int screenHeight)
/*      */   {
/*  407 */     double[] coords = getLineCoordinates(x1, y1, x2, y2, screenWidth, screenHeight, LineType.INFINITE_LINE);
/*  408 */     boolean intersects = coords != null;
/*      */ 
/*  410 */     if (intersects) {
/*  411 */       path.moveTo((int)coords[0], (int)coords[1]);
/*  412 */       path.lineTo((int)coords[2], (int)coords[3]);
/*      */     }
/*      */ 
/*  415 */     return intersects;
/*      */   }
/*      */ 
/*      */   public static boolean drawBeamLine(GeneralPath path, double xBase, double yBase, double x2, double y2, int screenWidth, int screenHeight)
/*      */   {
/*  433 */     double[] coords = getLineCoordinates(xBase, yBase, x2, y2, screenWidth, screenHeight, LineType.BEAM_LINE);
/*  434 */     boolean intersects = coords != null;
/*  435 */     if (coords != null) {
/*  436 */       path.moveTo((int)coords[0], (int)coords[1]);
/*  437 */       path.lineTo((int)coords[2], (int)coords[3]);
/*      */     }
/*      */ 
/*  440 */     return intersects;
/*      */   }
/*      */ 
/*      */   public static void drawSegmentLine(GeneralPath path, double x1, double y1, double x2, double y2, int screenWidth, int screenHeight)
/*      */   {
/*  456 */     double[] coords = getSegmentLineCoordinates(x1, y1, x2, y2, screenWidth, screenHeight);
/*  457 */     if (coords != null) {
/*  458 */       path.moveTo(coords[0], coords[1]);
/*  459 */       path.lineTo(coords[2], coords[3]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void drawSegmentLine(Graphics g, double x1, double y1, double x2, double y2, int screenWidth, int screenHeight)
/*      */   {
/*  476 */     double[] coords = getSegmentLineCoordinates(x1, y1, x2, y2, screenWidth, screenHeight);
/*  477 */     if (coords != null)
/*  478 */       g.drawLine((int)coords[0], (int)coords[1], (int)coords[2], (int)coords[3]);
/*      */   }
/*      */ 
/*      */   public static void drawEllipse(GeneralPath generalPath, double x, double y, double ellipseWidth, double ellipseHeight, int screenWidth, int screenHeight)
/*      */   {
/*  494 */     if ((x <= screenWidth) && (x + ellipseWidth >= 0.0D) && (y <= screenHeight) && (y + ellipseHeight >= 0.0D))
/*      */     {
/*  501 */       Ellipse2D.Double ellipse = new Ellipse2D.Double(x, y, ellipseWidth, ellipseHeight);
/*  502 */       drawShape(generalPath, ellipse, screenWidth, screenHeight);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void drawTriangle(GeneralPath generalPath, double x1, double y1, double x2, double y2, double x3, double y3, int screenWidth, int screenHeight)
/*      */   {
/*  520 */     if (((x1 >= 0.0D) || (x2 >= 0.0D) || (x3 >= 0.0D)) && ((y1 >= 0.0D) || (y2 >= 0.0D) || (y3 >= 0.0D)) && ((x1 <= screenWidth) || (x2 <= screenWidth) || (x3 <= screenWidth)) && ((y1 <= screenHeight) || (y2 <= screenHeight) || (y3 <= screenHeight)))
/*      */     {
/*  529 */       Polygon triangle = new Polygon();
/*  530 */       triangle.addPoint((int)x1, (int)y1);
/*  531 */       triangle.addPoint((int)x2, (int)y2);
/*  532 */       triangle.addPoint((int)x3, (int)y3);
/*      */ 
/*  534 */       drawShape(generalPath, triangle, screenWidth, screenHeight);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void drawShape(GeneralPath generalPath, Shape shape, int screenWidth, int screenHeight)
/*      */   {
/*  547 */     int SCREEN_OFFSET = 10;
/*      */ 
/*  549 */     Polygon screen = new Polygon();
/*  550 */     screen.addPoint(-10, -10);
/*  551 */     screen.addPoint(screenWidth + 10, -10);
/*  552 */     screen.addPoint(screenWidth + 10, screenHeight + 10);
/*  553 */     screen.addPoint(-10, screenHeight + 10);
/*      */ 
/*  555 */     Area a1 = new Area(shape);
/*  556 */     Area a2 = new Area(screen);
/*  557 */     Area a3 = new Area(shape);
/*      */ 
/*  559 */     a1.subtract(a2);
/*  560 */     a3.subtract(a1);
/*      */ 
/*  562 */     PathIterator intersectionsPath = a3.getPathIterator(null);
/*      */ 
/*  564 */     while (!intersectionsPath.isDone()) {
/*  565 */       double[] coordinates = new double[6];
/*  566 */       int type = intersectionsPath.currentSegment(coordinates);
/*  567 */       switch (type) {
/*      */       case 0:
/*  569 */         generalPath.moveTo(coordinates[0], coordinates[1]);
/*  570 */         break;
/*      */       case 1:
/*  572 */         generalPath.lineTo(coordinates[0], coordinates[1]);
/*  573 */         break;
/*      */       case 2:
/*  575 */         generalPath.quadTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
/*  576 */         break;
/*      */       case 3:
/*  578 */         generalPath.curveTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3], coordinates[4], coordinates[5]);
/*  579 */         break;
/*      */       case 4:
/*  581 */         break;
/*      */       }
/*      */ 
/*  586 */       intersectionsPath.next();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Rectangle getRectangle(double x, double y, double rectangleWidth, double rectangleHeight, int screenWidth, int screenHeight)
/*      */   {
/*  603 */     Rectangle rectangle = new Rectangle((int)x, (int)y, (int)rectangleWidth, (int)rectangleHeight);
/*  604 */     Rectangle screen = new Rectangle(0, 0, screenWidth, screenHeight);
/*      */ 
/*  606 */     Rectangle intersection = rectangle.intersection(screen);
/*  607 */     Rectangle result = null;
/*      */ 
/*  609 */     if ((intersection.width > 0) && (intersection.height > 0)) {
/*  610 */       result = intersection;
/*      */     }
/*      */ 
/*  613 */     return result;
/*      */   }
/*      */ 
/*      */   private static double[] getInfiniteLinesIntersection(double x11, double y11, double x12, double y12, double x21, double y21, double x22, double y22)
/*      */   {
/*  629 */     double a1 = a(x11, y11, x12, y12);
/*  630 */     double a2 = a(x21, y21, x22, y22);
/*      */ 
/*  632 */     double b1 = b(x11, y11, a1);
/*  633 */     double b2 = b(x21, y21, a2);
/*      */     double b;
/*      */     double xCross;
/*      */     double a;
/*      */     double b;
/*  639 */     if ((!Double.isInfinite(a1)) && (!Double.isInfinite(a2)))
/*      */     {
/*  643 */       double xCross = xCross(a1, b1, a2, b2);
/*      */       double b;
/*  645 */       if (y21 == y22) {
/*  646 */         double a = a2;
/*  647 */         b = b2;
/*      */       }
/*      */       else {
/*  650 */         double a = a1;
/*  651 */         b = b1;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       double b;
/*  654 */       if ((Double.isInfinite(a1)) && (!Double.isInfinite(a2)))
/*      */       {
/*  658 */         double xCross = x11;
/*  659 */         double a = a2;
/*  660 */         b = b2;
/*      */       }
/*      */       else
/*      */       {
/*      */         double b;
/*  662 */         if ((!Double.isInfinite(a1)) && (Double.isInfinite(a2)))
/*      */         {
/*  666 */           double xCross = x21;
/*  667 */           double a = a1;
/*  668 */           b = b1;
/*      */         }
/*      */         else
/*      */         {
/*  674 */           xCross = (0.0D / 0.0D);
/*  675 */           a = (0.0D / 0.0D);
/*  676 */           b = (0.0D / 0.0D);
/*      */         }
/*      */       }
/*      */     }
/*  679 */     if (Double.isNaN(xCross)) {
/*  680 */       return null;
/*      */     }
/*      */ 
/*  683 */     double yCross = a * xCross + b;
/*  684 */     return new double[] { xCross, yCross };
/*      */   }
/*      */ 
/*      */   private static boolean isPointLocatedOnShortLine(double x, double y, double x1, double y1, double x2, double y2)
/*      */   {
/*  696 */     double a = a(x1, y1, x2, y2);
/*  697 */     if (Double.isInfinite(a))
/*      */     {
/*  701 */       return (x == x1) && (Math.max(y1, y2) >= y) && (y >= Math.min(y1, y2));
/*      */     }
/*      */ 
/*  704 */     double b = b(x1, y1, a);
/*  705 */     double intersectionY = a * x + b;
/*      */ 
/*  711 */     return (y == fastRound(intersectionY)) && 
/*  707 */       (Math.max(x1, x2) >= x) && (x >= Math.min(x1, x2)) && (Math.max(y1, y2) >= y) && (y >= Math.min(y1, y2));
/*      */   }
/*      */ 
/*      */   private static boolean isPointVisible(double x, double y, int width, int height)
/*      */   {
/*  725 */     return (x >= 0.0D) && (x <= width) && (y >= 0.0D) && (y <= height);
/*      */   }
/*      */ 
/*      */   private static int fastRound(double v) {
/*  729 */     return (int)(Math.signum(v) * 0.5D + v);
/*      */   }
/*      */ 
/*      */   private static double[][] getIntersectionsWithScreen(double x1, double y1, double x2, double y2, int width, int height)
/*      */   {
/*  740 */     double[][] intersections = new double[4][];
/*      */ 
/*  742 */     intersections[0] = getInfiniteLinesIntersection(x1, y1, x2, y2, 0.0D, 0.0D, width, 0.0D);
/*  743 */     intersections[1] = getInfiniteLinesIntersection(x1, y1, x2, y2, 0.0D, height, width, height);
/*  744 */     intersections[2] = getInfiniteLinesIntersection(x1, y1, x2, y2, 0.0D, 0.0D, 0.0D, height);
/*  745 */     intersections[3] = getInfiniteLinesIntersection(x1, y1, x2, y2, width, 0.0D, width, height);
/*      */ 
/*  747 */     return intersections;
/*      */   }
/*      */ 
/*      */   static double[] getLineCoordinates(double x1, double y1, double x2, double y2, int screenWidth, int screenHeight, LineType lineType)
/*      */   {
/*  759 */     double[] result = null;
/*      */ 
/*  761 */     if ((screenWidth > 0) && (screenHeight > 0))
/*      */     {
/*  763 */       boolean isOnScreen = isOnScreen(x1, y1, x2, y2, screenWidth, screenHeight, lineType);
/*      */ 
/*  765 */       if (isOnScreen)
/*      */       {
/*  767 */         double x1Result = -1.0D;
/*  768 */         double y1Result = -1.0D;
/*      */ 
/*  770 */         double x2Result = -1.0D;
/*  771 */         double y2Result = -1.0D;
/*      */ 
/*  773 */         double[][] intersectionsWithScreen = getIntersectionsWithScreen(x1, y1, x2, y2, screenWidth, screenHeight);
/*      */ 
/*  775 */         if ((isPointVisible(x1, y1, screenWidth, screenHeight)) && (LineType.SEGMENT_LINE.equals(lineType)))
/*      */         {
/*  779 */           x1Result = x1;
/*  780 */           y1Result = y1;
/*      */         }
/*      */         else {
/*  783 */           double[] intersection = getClosestPoint(x1, y1, screenWidth, screenHeight, intersectionsWithScreen);
/*  784 */           if (intersection != null) {
/*  785 */             x1Result = intersection[0];
/*  786 */             y1Result = intersection[1];
/*  787 */             intersectionsWithScreen = removePoint(intersection, intersectionsWithScreen);
/*      */           }
/*      */         }
/*      */ 
/*  791 */         if ((isPointVisible(x2, y2, screenWidth, screenHeight)) && (LineType.SEGMENT_LINE.equals(lineType)))
/*      */         {
/*  795 */           x2Result = x2;
/*  796 */           y2Result = y2;
/*      */         }
/*      */         else {
/*  799 */           double[] intersection = getClosestPoint(x2, y2, screenWidth, screenHeight, intersectionsWithScreen);
/*  800 */           if (intersection != null) {
/*  801 */             x2Result = intersection[0];
/*  802 */             y2Result = intersection[1];
/*  803 */             intersectionsWithScreen = removePoint(intersection, intersectionsWithScreen);
/*      */           }
/*      */         }
/*      */ 
/*  807 */         if (LineType.BEAM_LINE.equals(lineType))
/*      */         {
/*  811 */           if (isPointVisible(x2, y2, screenWidth, screenHeight)) {
/*  812 */             if ((isPointLocatedOnShortLine(x1, y1, x1Result, y1Result, x2, y2)) || (!isPointVisible(x1, y1, screenWidth, screenHeight)))
/*      */             {
/*  816 */               x2Result = x2;
/*  817 */               y2Result = y2;
/*      */             }
/*      */             else {
/*  820 */               x1Result = x2;
/*  821 */               y1Result = y2;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  826 */         if ((x1Result != x2Result) || (y1Result != y2Result))
/*      */         {
/*  831 */           if ((x1Result != -1.0D) && (y1Result != -1.0D) && (x2Result != -1.0D) && (y2Result != -1.0D))
/*      */           {
/*  837 */             result = new double[] { x1Result, y1Result, x2Result, y2Result };
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  842 */     return result;
/*      */   }
/*      */ 
/*      */   private static boolean isOnScreen(double x1, double y1, double x2, double y2, int width, int height, LineType lineType)
/*      */   {
/*  858 */     boolean pointsOutOfScreen = ((x1 < 0.0D) && (x2 < 0.0D)) || ((x1 > width) && (x2 > width)) || ((y1 < 0.0D) && (y2 < 0.0D)) || ((y1 > height) && (y2 > height));
/*      */ 
/*  864 */     if (pointsOutOfScreen) {
/*  865 */       if (LineType.SEGMENT_LINE.equals(lineType)) {
/*  866 */         return false;
/*      */       }
/*  868 */       if (LineType.INFINITE_LINE.equals(lineType)) {
/*  869 */         return true;
/*      */       }
/*  871 */       if (LineType.BEAM_LINE.equals(lineType))
/*      */       {
/*  876 */         double[] point = null;
/*  877 */         double[][] intersectionsWithScreen = getIntersectionsWithScreen(x1, y1, x2, y2, width, height);
/*      */         do {
/*  879 */           point = getClosestPoint(x1, y1, width, height, intersectionsWithScreen);
/*  880 */           intersectionsWithScreen = removePoint(point, intersectionsWithScreen);
/*      */ 
/*  882 */           if ((point != null) && (!isPointLocatedOnShortLine(x2, y2, x1, y1, point[0], point[1])))
/*  883 */             return true;
/*      */         }
/*  885 */         while (point != null);
/*  886 */         return false;
/*      */       }
/*      */ 
/*  889 */       throw new IllegalArgumentException("Unsupported Line Type '" + lineType + "'");
/*      */     }
/*      */ 
/*  893 */     return true;
/*      */   }
/*      */ 
/*      */   private static double[][] removePoint(double[] point, double[][] points)
/*      */   {
/*  904 */     if (point != null) {
/*  905 */       for (int i = 0; i < points.length; i++) {
/*  906 */         double[] p = points[i];
/*  907 */         if (p == null) {
/*      */           continue;
/*      */         }
/*  910 */         if ((p[0] == point[0]) && (p[1] == point[1])) {
/*  911 */           points[i] = null;
/*  912 */           return points;
/*      */         }
/*      */       }
/*      */     }
/*  916 */     return points;
/*      */   }
/*      */ 
/*      */   private static double[] getSegmentLineCoordinates(double x1, double y1, double x2, double y2, int screenWidth, int screenHeight)
/*      */   {
/*  927 */     return getLineCoordinates(x1, y1, x2, y2, screenWidth, screenHeight, LineType.SEGMENT_LINE);
/*      */   }
/*      */ 
/*      */   private static double[] getClosestPoint(double x1, double y1, int width, int height, double[][] points)
/*      */   {
/*  940 */     double[] closestPoint = null;
/*  941 */     double minimalDistance = 1.7976931348623157E+308D;
/*      */ 
/*  943 */     for (int i = 0; i < points.length; i++) {
/*  944 */       double[] p = points[i];
/*      */ 
/*  946 */       if (p == null)
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/*  953 */       if ((p[0] < 0.0D) || (p[0] > width) || (p[1] < 0.0D) || (p[1] > height))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/*  962 */       double distance = Math.sqrt(Math.abs((x1 - p[0]) * (x1 - p[0])) + Math.abs((y1 - p[1]) * (y1 - p[1])));
/*      */ 
/*  964 */       if (minimalDistance > distance) {
/*  965 */         minimalDistance = distance;
/*  966 */         closestPoint = p;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  971 */     return closestPoint;
/*      */   }
/*      */ 
/*      */   public static double a(double x1, double y1, double x2, double y2)
/*      */   {
/*  981 */     double a = (y2 - y1) / (x2 - x1);
/*  982 */     return a;
/*      */   }
/*      */ 
/*      */   public static double b(double x1, double y1, double a)
/*      */   {
/*  990 */     double b = y1 - a * x1;
/*  991 */     return b;
/*      */   }
/*      */ 
/*      */   public static double xCross(double a1, double b1, double a2, double b2)
/*      */   {
/* 1000 */     if (a1 == a2) {
/* 1001 */       return (0.0D / 0.0D);
/*      */     }
/* 1003 */     return (b2 - b1) / (a1 - a2);
/*      */   }
/*      */ 
/*      */   static enum LineType
/*      */   {
/*   23 */     SEGMENT_LINE, 
/*   24 */     INFINITE_LINE, 
/*   25 */     BEAM_LINE;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.GraphicHelper
 * JD-Core Version:    0.6.0
 */