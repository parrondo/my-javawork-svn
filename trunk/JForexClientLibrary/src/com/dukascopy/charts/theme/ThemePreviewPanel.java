/*     */ package com.dukascopy.charts.theme;
/*     */ 
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.persistence.ThemeManager;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseMotionAdapter;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class ThemePreviewPanel extends JPanel
/*     */ {
/*     */   private static final int X_AXIS_HEIGHT = 12;
/*     */   private static final int Y_AXIS_WIDTH = 40;
/*     */   private ITheme theme;
/*  36 */   private final List<Candle> candles = new ArrayList();
/*     */   private Point cursorPoint;
/*     */ 
/*     */   public ThemePreviewPanel()
/*     */   {
/*  40 */     this.theme = ThemeManager.getTheme();
/*     */ 
/*  42 */     generateCandles(15);
/*     */ 
/*  44 */     addMouseMotionListener(new MouseMotionAdapter()
/*     */     {
/*     */       public void mouseMoved(MouseEvent e) {
/*  47 */         ThemePreviewPanel.access$002(ThemePreviewPanel.this, e.getPoint());
/*  48 */         ThemePreviewPanel.this.repaint();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public void set(ITheme theme) {
/*  54 */     this.theme = theme;
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g)
/*     */   {
/*  59 */     ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  60 */     g.setColor(this.theme.getColor(ITheme.ChartElement.BACKGROUND));
/*  61 */     g.fillRect(0, 0, getWidth(), getHeight());
/*     */ 
/*  63 */     int rightAxisX = getWidth() - 40;
/*  64 */     int bottomAxisY = getHeight() - 12;
/*     */ 
/*  66 */     drawGrid(g, rightAxisX, bottomAxisY);
/*  67 */     drawAxis(g, rightAxisX, bottomAxisY);
/*  68 */     drawOutline(g, rightAxisX, bottomAxisY);
/*  69 */     drawCandles((Graphics2D)g, rightAxisX);
/*  70 */     drawOHLC(g);
/*     */ 
/*  72 */     drawTimeLine(g, bottomAxisY);
/*  73 */     drawCursorTracker(g, rightAxisX, bottomAxisY);
/*     */   }
/*     */ 
/*     */   private void generateCandles(int candlesCount) {
/*  77 */     Random random = new Random();
/*  78 */     for (int i = 0; i < candlesCount; i++) {
/*  79 */       int open = 50 + random.nextInt(50);
/*  80 */       int close = 50 + random.nextInt(50);
/*  81 */       int high = Math.min(open, close) - random.nextInt(25);
/*  82 */       int low = Math.max(open, close) + random.nextInt(25);
/*     */ 
/*  84 */       this.candles.add(new Candle(open, close, high, low));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void drawOutline(Graphics g, int rightAxisX, int bottomAxisY) {
/*  89 */     g.setColor(this.theme.getColor(ITheme.ChartElement.OUTLINE));
/*  90 */     g.drawRect(0, 0, rightAxisX - 1, bottomAxisY - 1);
/*     */   }
/*     */ 
/*     */   private void drawAxis(Graphics g, int rightAxisX, int bottomAxisY) {
/*  94 */     g.setColor(this.theme.getColor(ITheme.ChartElement.AXIS_PANEL_BACKGROUND));
/*  95 */     g.fillRect(1, bottomAxisY, getWidth() - 1, 11);
/*  96 */     g.fillRect(rightAxisX, 1, 39, getHeight() - 1);
/*     */ 
/*  99 */     g.setColor(this.theme.getColor(ITheme.ChartElement.AXIS_PANEL_FOREGROUND));
/* 100 */     g.drawRect(1, bottomAxisY, getWidth() - 1, 11);
/*     */ 
/* 102 */     Font font = this.theme.getFont(ITheme.TextElement.AXIS);
/* 103 */     int fontHeight = g.getFontMetrics(font).getHeight();
/*     */ 
/* 105 */     g.setFont(font);
/* 106 */     for (int i = 0; i <= rightAxisX / 50; i++) {
/* 107 */       g.drawLine(i * 50, bottomAxisY, i * 50, getHeight());
/* 108 */       g.drawString("12:34:5" + i, i * 50 + 2, getHeight() - 2);
/*     */     }
/*     */ 
/* 111 */     g.drawLine(rightAxisX, bottomAxisY, rightAxisX, getHeight());
/*     */ 
/* 113 */     for (int i = 1; i < bottomAxisY / 20; i++) {
/* 114 */       int y = bottomAxisY - i * 20;
/* 115 */       g.drawLine(rightAxisX, y, rightAxisX + 2, y);
/* 116 */       g.drawString("1.234" + i, rightAxisX + 3, y + fontHeight / 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void drawGrid(Graphics g, int rightAxisX, int bottomAxisY) {
/* 121 */     g.setColor(this.theme.getColor(ITheme.ChartElement.GRID));
/*     */ 
/* 123 */     Graphics2D g2d = (Graphics2D)g;
/* 124 */     Stroke prevStroke = g2d.getStroke();
/* 125 */     g2d.setStroke(this.theme.getStroke(ITheme.StrokeElement.GRID_STROKE));
/*     */ 
/* 128 */     for (int i = 1; i <= rightAxisX / 20; i++) {
/* 129 */       int x = rightAxisX - i * 20;
/* 130 */       g.drawLine(x, 0, x, bottomAxisY);
/*     */     }
/* 132 */     for (int i = 1; i <= bottomAxisY / 20; i++) {
/* 133 */       int y = bottomAxisY - i * 20;
/* 134 */       g.drawLine(0, y, rightAxisX, y);
/*     */     }
/*     */ 
/* 137 */     g2d.setStroke(prevStroke);
/*     */   }
/*     */ 
/*     */   private void drawCandles(Graphics2D g2d, int rightAxisX) {
/* 141 */     Color bullColor = this.theme.getColor(ITheme.ChartElement.CANDLE_BULL);
/* 142 */     Color bearColor = this.theme.getColor(ITheme.ChartElement.CANDLE_BEAR);
/* 143 */     Color bullBorderColor = this.theme.getColor(ITheme.ChartElement.CANDLE_BULL_BORDER);
/* 144 */     Color bearBorderColor = this.theme.getColor(ITheme.ChartElement.CANDLE_BEAR_BORDER);
/*     */ 
/* 146 */     int x = 10;
/* 147 */     for (Candle candle : this.candles) {
/* 148 */       x += 12; candle.draw(g2d, x, bullColor, bearColor, bullBorderColor, bearBorderColor);
/*     */     }
/*     */ 
/* 151 */     g2d.setColor(this.theme.getColor(ITheme.ChartElement.LAST_CANDLE_TRACKING_LINE));
/* 152 */     int lastCandleY = ((Candle)this.candles.get(this.candles.size() - 1)).close;
/* 153 */     g2d.drawLine(x, lastCandleY, rightAxisX, lastCandleY);
/*     */ 
/* 155 */     g2d.setColor(this.theme.getColor(ITheme.ChartElement.AXIS_LABEL_BACKGROUND_ASK));
/* 156 */     g2d.fillRect(rightAxisX + 2, lastCandleY - 5, 35, 10);
/* 157 */     g2d.setColor(this.theme.getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND));
/* 158 */     g2d.drawRect(rightAxisX + 2, lastCandleY - 5, 35, 10);
/* 159 */     g2d.setFont(this.theme.getFont(ITheme.TextElement.AXIS));
/* 160 */     g2d.drawString("1.234" + lastCandleY / 20, rightAxisX + 3, lastCandleY + 5);
/*     */   }
/*     */ 
/*     */   private void drawCursorTracker(Graphics g, int rightAxisX, int bottomAxisY) {
/* 164 */     if (this.cursorPoint == null) {
/* 165 */       this.cursorPoint = new Point(rightAxisX / 2, bottomAxisY / 2);
/*     */     }
/*     */ 
/* 168 */     int x = Math.min((int)this.cursorPoint.getX(), rightAxisX - 20);
/* 169 */     int y = Math.min((int)this.cursorPoint.getY(), bottomAxisY - 6);
/*     */ 
/* 171 */     g.setColor(this.theme.getColor(ITheme.ChartElement.META));
/* 172 */     g.drawLine(x, 1, x, bottomAxisY - 1);
/* 173 */     g.drawLine(1, y, rightAxisX - 1, y);
/*     */ 
/* 175 */     g.setColor(this.theme.getColor(ITheme.ChartElement.AXIS_LABEL_BACKGROUND));
/* 176 */     g.fillRect(x - 20, bottomAxisY + 1, 40, 10);
/* 177 */     g.fillRect(rightAxisX + 2, y - 5, 35, 10);
/*     */ 
/* 179 */     g.setColor(this.theme.getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND));
/* 180 */     g.drawRect(x - 20, bottomAxisY + 1, 40, 10);
/* 181 */     g.drawRect(rightAxisX + 2, y - 5, 35, 10);
/*     */ 
/* 183 */     g.setFont(this.theme.getFont(ITheme.TextElement.AXIS));
/* 184 */     g.setColor(this.theme.getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND));
/*     */ 
/* 186 */     g.drawString("12:34:5" + x / 50, x - 16, bottomAxisY + 10);
/* 187 */     g.drawString("1.234" + (bottomAxisY - y) / 20, rightAxisX + 3, y + 4);
/*     */   }
/*     */ 
/*     */   private void drawTimeLine(Graphics g, int bottomAxisY) {
/* 191 */     g.setColor(this.theme.getColor(ITheme.ChartElement.DRAWING));
/* 192 */     g.setFont(this.theme.getFont(ITheme.TextElement.DEFAULT));
/*     */ 
/* 194 */     g.drawLine(123, 1, 123, bottomAxisY - 1);
/* 195 */     g.drawString("12:34:52", 125, bottomAxisY - 3);
/*     */   }
/*     */ 
/*     */   private void drawOHLC(Graphics g) {
/* 199 */     g.setColor(this.theme.getColor(ITheme.ChartElement.OHLC));
/* 200 */     g.setFont(this.theme.getFont(ITheme.TextElement.OHLC));
/*     */ 
/* 202 */     g.drawString(new Date().toString(), 10, 15);
/*     */   }
/*     */ 
/*     */   private void drawOrderTracking(Graphics g) {
/* 206 */     int x1 = 22;
/* 207 */     int y1 = ((Candle)this.candles.get(0)).low;
/* 208 */     int x2 = 130;
/* 209 */     int y2 = ((Candle)this.candles.get(10)).low;
/*     */ 
/* 211 */     g.setColor(this.theme.getColor(ITheme.ChartElement.ORDER_TRACKING_LINE));
/* 212 */     g.drawLine(x1, y1, x2, y2);
/*     */   }
/*     */ 
/*     */   private static class Candle
/*     */   {
/*     */     public static final int CANDLE_WIDTH = 10;
/*     */     private int open;
/*     */     private int close;
/*     */     private int high;
/*     */     private int low;
/* 222 */     GeneralPath path = new GeneralPath();
/*     */ 
/*     */     public Candle(int open, int close, int high, int low) {
/* 225 */       this.open = open;
/* 226 */       this.close = close;
/* 227 */       this.high = high;
/* 228 */       this.low = low;
/*     */     }
/*     */ 
/*     */     public void draw(Graphics2D g2d, int x, Color bullColor, Color bearColor, Color bullBorderColor, Color bearBorderColor) {
/* 232 */       this.path.reset();
/*     */ 
/* 234 */       this.path.moveTo(x, this.high);
/*     */ 
/* 236 */       int diff = this.open - this.close;
/* 237 */       if (diff > 0) {
/* 238 */         this.path.lineTo(x, this.close);
/* 239 */         this.path.append(new Rectangle(x - 5, this.close, 10, diff), false);
/* 240 */         this.path.moveTo(x, this.close + diff);
/*     */       } else {
/* 242 */         this.path.lineTo(x, this.open);
/* 243 */         this.path.append(new Rectangle(x - 5, this.open, 10, -diff), false);
/* 244 */         this.path.moveTo(x, this.open - diff);
/*     */       }
/*     */ 
/* 247 */       this.path.lineTo(x, this.low);
/*     */ 
/* 249 */       g2d.setColor(diff > 0 ? bullColor : bearColor);
/* 250 */       g2d.fill(this.path);
/*     */ 
/* 252 */       g2d.setColor(diff > 0 ? bullBorderColor : bearBorderColor);
/* 253 */       g2d.draw(this.path);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.theme.ThemePreviewPanel
 * JD-Core Version:    0.6.0
 */