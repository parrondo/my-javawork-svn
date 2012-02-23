/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject.Decoration;
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject.Placement;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Image;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.awt.image.VolatileImage;
/*     */ import java.util.Map;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.ButtonModel;
/*     */ import javax.swing.Icon;
/*     */ 
/*     */ public class DrawingsHelper
/*     */ {
/*     */   public static final float DEFAULT_LINE_WIDTH = 1.0F;
/*     */   public static final int DEFAULT_LINE_END_CAP = 0;
/*     */   public static final int DEFAULT_LINE_JOIN = 2;
/*     */ 
/*     */   public static class PresetsIcon
/*     */     implements Icon, ImageObserver
/*     */   {
/*     */     private static final int WIDTH = 10;
/*     */     private static final int HEIGHT = 10;
/*     */ 
/*     */     public void paintIcon(Component c, Graphics g, int x, int y)
/*     */     {
/* 301 */       AbstractButton b = (AbstractButton)c;
/* 302 */       ButtonModel model = b.getModel();
/* 303 */       boolean isSelected = model.isSelected();
/* 304 */       if (isSelected) {
/* 305 */         g.drawLine(x + 7, y + 1, x + 7, y + 3);
/* 306 */         g.drawLine(x + 6, y + 2, x + 6, y + 4);
/* 307 */         g.drawLine(x + 5, y + 3, x + 5, y + 5);
/* 308 */         g.drawLine(x + 4, y + 4, x + 4, y + 6);
/* 309 */         g.drawLine(x + 3, y + 5, x + 3, y + 7);
/* 310 */         g.drawLine(x + 2, y + 4, x + 2, y + 6);
/* 311 */         g.drawLine(x + 1, y + 3, x + 1, y + 5);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int getIconWidth()
/*     */     {
/* 317 */       return 10;
/*     */     }
/*     */ 
/*     */     public int getIconHeight()
/*     */     {
/* 322 */       return 10;
/*     */     }
/*     */ 
/*     */     public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
/*     */     {
/* 327 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class DecorationIcon
/*     */     implements Icon, ImageObserver
/*     */   {
/*     */     private static final int WIDTH = 50;
/*     */     private static final int HEIGHT = 10;
/*     */     private Map<IDecoratedChartObject.Placement, IDecoratedChartObject.Decoration> decorations;
/*     */ 
/*     */     public DecorationIcon(Map<IDecoratedChartObject.Placement, IDecoratedChartObject.Decoration> decorations)
/*     */     {
/* 233 */       this.decorations = decorations;
/*     */     }
/*     */ 
/*     */     public int getIconHeight()
/*     */     {
/* 238 */       return 10;
/*     */     }
/*     */ 
/*     */     public int getIconWidth()
/*     */     {
/* 243 */       return 50;
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component c, Graphics g, int x, int y)
/*     */     {
/* 248 */       Graphics2D g2d = (Graphics2D)g;
/*     */ 
/* 250 */       VolatileImage icon = g2d.getDeviceConfiguration().createCompatibleVolatileImage(getIconWidth(), getIconHeight(), 2);
/*     */ 
/* 252 */       Graphics2D iconGraphics = (Graphics2D)icon.getGraphics();
/*     */ 
/* 254 */       iconGraphics.setBackground(new Color(255, 255, 255, 0));
/* 255 */       iconGraphics.clearRect(0, 0, getIconWidth(), getIconHeight());
/*     */ 
/* 257 */       iconGraphics.setStroke(new BasicStroke(1.0F));
/* 258 */       iconGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/* 260 */       iconGraphics.setColor(Color.BLACK);
/*     */ 
/* 262 */       iconGraphics.drawLine(0, getIconHeight() / 2, getIconWidth(), getIconHeight() / 2);
/* 263 */       for (IDecoratedChartObject.Placement placement : this.decorations.keySet()) {
/* 264 */         IDecoratedChartObject.Decoration decor = (IDecoratedChartObject.Decoration)this.decorations.get(placement);
/* 265 */         Shape decorationShape = DecoratedChartObject.getShape(decor);
/* 266 */         AffineTransform transform = new AffineTransform();
/*     */ 
/* 268 */         if (IDecoratedChartObject.Placement.Beginning == placement) {
/* 269 */           transform.translate(0.0D, 1.0D);
/*     */         }
/* 272 */         else if (IDecoratedChartObject.Placement.End == placement) {
/* 273 */           transform.translate(decorationShape.getBounds2D().getWidth(), decorationShape.getBounds2D().getHeight());
/* 274 */           transform.quadrantRotate(2);
/* 275 */           decorationShape = transform.createTransformedShape(decorationShape);
/* 276 */           transform = new AffineTransform();
/* 277 */           transform.translate(getIconWidth() - decorationShape.getBounds2D().getWidth(), 1.0D);
/*     */         }
/*     */ 
/* 280 */         iconGraphics.fill(transform.createTransformedShape(decorationShape));
/*     */       }
/*     */ 
/* 283 */       icon.flush();
/*     */ 
/* 285 */       g2d.drawImage(icon, x, y, this);
/*     */     }
/*     */ 
/*     */     public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
/*     */     {
/* 290 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class LinePatternIcon
/*     */     implements Icon, ImageObserver
/*     */   {
/*     */     private static final int WIDTH = 50;
/*     */     private static final int DEFAULT_HEIGHT = 10;
/*     */     private DrawingsHelper.DashPattern dashPattern;
/* 176 */     private int lineHeight = 3;
/*     */ 
/*     */     public LinePatternIcon(DrawingsHelper.DashPattern dashPattern) {
/* 179 */       this.dashPattern = dashPattern;
/*     */     }
/*     */ 
/*     */     public LinePatternIcon(DrawingsHelper.DashPattern dashPattern, int lineHeight) {
/* 183 */       this.dashPattern = dashPattern;
/* 184 */       this.lineHeight = lineHeight;
/*     */     }
/*     */ 
/*     */     public int getIconHeight()
/*     */     {
/* 189 */       return 10;
/*     */     }
/*     */ 
/*     */     public int getIconWidth()
/*     */     {
/* 194 */       return 50;
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component c, Graphics g, int x, int y)
/*     */     {
/* 199 */       Graphics2D g2d = (Graphics2D)g;
/*     */ 
/* 201 */       VolatileImage icon = g2d.getDeviceConfiguration().createCompatibleVolatileImage(getIconWidth(), getIconHeight(), 2);
/*     */ 
/* 203 */       Graphics2D iconGraphics = (Graphics2D)icon.getGraphics();
/*     */ 
/* 205 */       iconGraphics.setBackground(new Color(255, 255, 255, 0));
/* 206 */       iconGraphics.clearRect(0, 0, getIconWidth(), getIconHeight());
/*     */ 
/* 208 */       iconGraphics.setStroke(new BasicStroke(this.lineHeight, 0, 2, 0.0F, this.dashPattern.getDashArray(), 0.0F));
/*     */ 
/* 212 */       iconGraphics.setColor(Color.BLACK);
/* 213 */       iconGraphics.drawLine(0, 5, getIconWidth(), 5);
/* 214 */       icon.flush();
/*     */ 
/* 216 */       g2d.drawImage(icon, x, y, this);
/*     */     }
/*     */ 
/*     */     public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
/*     */     {
/* 221 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ColorIcon
/*     */     implements Icon, ImageObserver
/*     */   {
/*     */     private static final int WIDTH = 55;
/*     */     private static final int CHECK_WIDTH = 11;
/*     */     private static final int HEIGHT = 10;
/*     */     private Color color;
/* 107 */     private float alpha = 1.0F;
/*     */ 
/*     */     public ColorIcon(Color color) {
/* 110 */       this.color = color;
/*     */     }
/*     */ 
/*     */     public int getIconHeight()
/*     */     {
/* 115 */       return 10;
/*     */     }
/*     */ 
/*     */     public int getIconWidth()
/*     */     {
/* 120 */       return 55;
/*     */     }
/*     */ 
/*     */     public void paintCheckIcon(Component c, Graphics g, int x, int y) {
/* 124 */       AbstractButton b = (AbstractButton)c;
/* 125 */       ButtonModel model = b.getModel();
/* 126 */       boolean isSelected = model.isSelected();
/* 127 */       if (isSelected) {
/* 128 */         g.drawLine(x + 7, y + 1, x + 7, y + 3);
/* 129 */         g.drawLine(x + 6, y + 2, x + 6, y + 4);
/* 130 */         g.drawLine(x + 5, y + 3, x + 5, y + 5);
/* 131 */         g.drawLine(x + 4, y + 4, x + 4, y + 6);
/* 132 */         g.drawLine(x + 3, y + 5, x + 3, y + 7);
/* 133 */         g.drawLine(x + 2, y + 4, x + 2, y + 6);
/* 134 */         g.drawLine(x + 1, y + 3, x + 1, y + 5);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component c, Graphics g, int x, int y)
/*     */     {
/* 140 */       Graphics2D g2d = (Graphics2D)g;
/*     */ 
/* 142 */       paintCheckIcon(c, g, x, y);
/*     */ 
/* 144 */       VolatileImage icon = g2d.getDeviceConfiguration().createCompatibleVolatileImage(getIconWidth() - 11, getIconHeight(), 2);
/*     */ 
/* 146 */       Graphics2D iconGraphics = (Graphics2D)icon.getGraphics();
/*     */ 
/* 148 */       iconGraphics.setColor(this.color);
/*     */ 
/* 150 */       Composite composite = g2d.getComposite();
/* 151 */       g2d.setComposite(AlphaComposite.getInstance(3, this.alpha));
/* 152 */       iconGraphics.fillRect(0, 0, getIconWidth(), getIconHeight());
/* 153 */       icon.flush();
/*     */ 
/* 155 */       g2d.drawImage(icon, x + 11, y, this);
/*     */ 
/* 157 */       g2d.setComposite(composite);
/*     */     }
/*     */ 
/*     */     public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
/*     */     {
/* 162 */       return false;
/*     */     }
/*     */ 
/*     */     public void setOpacity(float alpha) {
/* 166 */       this.alpha = alpha;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum DashPattern
/*     */   {
/*  36 */     Solid(0, null), 
/*  37 */     Dashed(1, new float[] { 5.0F }), 
/*  38 */     FineDashed(5, new float[] { 2.0F }), 
/*  39 */     Doted(2, new float[] { 1.0F, 2.0F }), 
/*  40 */     DashesWithDot(3, new float[] { 5.0F, 2.0F, 1.0F, 2.0F }), 
/*  41 */     DashedWithTwoDots(4, new float[] { 5.0F, 2.0F, 1.0F, 2.0F, 1.0F, 2.0F }), 
/*  42 */     LongDashWithShortDash(6, new float[] { 5.0F, 2.0F, 1.0F, 2.0F });
/*     */ 
/*     */     private float[] dashArray;
/*     */     private int style;
/*     */ 
/*  48 */     private DashPattern(int style, float[] dashArray) { this.dashArray = dashArray;
/*  49 */       this.style = style; }
/*     */ 
/*     */     public static DashPattern getPattern(int style)
/*     */     {
/*  53 */       for (DashPattern pattern : values()) {
/*  54 */         if (pattern.getStyle() == style) {
/*  55 */           return pattern;
/*     */         }
/*     */       }
/*  58 */       return null;
/*     */     }
/*     */ 
/*     */     public static int getStyle(float[] patternArray) {
/*  62 */       for (DashPattern pattern : values()) {
/*  63 */         if (pattern.equals(patternArray)) {
/*  64 */           return pattern.getStyle();
/*     */         }
/*     */       }
/*  67 */       return -1;
/*     */     }
/*     */ 
/*     */     public float[] getDashArray() {
/*  71 */       return this.dashArray;
/*     */     }
/*     */     public int getStyle() {
/*  74 */       return this.style;
/*     */     }
/*     */ 
/*     */     public boolean equals(float[] dashArray) {
/*  78 */       if (dashArray == null) {
/*  79 */         return getDashArray() == null;
/*     */       }
/*  81 */       if (getDashArray() != null) {
/*  82 */         if (dashArray.length != getDashArray().length) {
/*  83 */           return false;
/*     */         }
/*  85 */         for (int i = 0; i < dashArray.length; i++) {
/*  86 */           if (dashArray[i] != getDashArray()[i]) {
/*  87 */             return false;
/*     */           }
/*     */         }
/*  90 */         return true;
/*     */       }
/*     */ 
/*  93 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.DrawingsHelper
 * JD-Core Version:    0.6.0
 */