/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.ITextChartObject;
/*     */ import com.dukascopy.charts.dialogs.drawings.StyledStringTransformer;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.font.TextLayout;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Ellipse2D.Double;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.security.InvalidParameterException;
/*     */ import java.text.AttributedString;
/*     */ import java.util.List;
/*     */ import javax.swing.UIDefaults;
/*     */ import javax.swing.UIManager;
/*     */ 
/*     */ public class TextChartObject extends AbstractStickablePointsChartObject
/*     */   implements ITextChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Text";
/*     */   static final int BORDER_OFFSET = 2;
/*     */   private static final char DEGREE_SYMBOL = '°';
/*     */   transient Rectangle2D stringBounds;
/*     */   transient Polygon stringPolygon;
/*     */   private transient AttributedString[] attributedText;
/*     */   private StyledStringTransformer styledString;
/*  44 */   private transient boolean angleMiddlePointSelected = false;
/*     */ 
/*  46 */   private double textAngle = 0.0D;
/*     */ 
/*     */   public TextChartObject()
/*     */   {
/*  51 */     super(null, IChart.Type.TEXT);
/*  52 */     setUnderEdit(true);
/*  53 */     Font font = getFont();
/*  54 */     int horizontalAlignment = getHorizontalAlignment();
/*  55 */     setStyledString(new StyledStringTransformer("Text", font.getFamily(), getColor(), font.getSize(), horizontalAlignment));
/*     */   }
/*     */ 
/*     */   public TextChartObject(String key)
/*     */   {
/*  67 */     super(key, IChart.Type.TEXT);
/*     */   }
/*     */ 
/*     */   public TextChartObject(String key, long time1, double price1) {
/*  71 */     super(key, IChart.Type.TEXT);
/*  72 */     this.times[0] = time1;
/*  73 */     this.prices[0] = price1;
/*     */   }
/*     */ 
/*     */   public TextChartObject(TextChartObject chartObject) {
/*  77 */     super(chartObject);
/*  78 */     this.styledString = chartObject.styledString;
/*     */   }
/*     */ 
/*     */   public Font getFont()
/*     */   {
/*  83 */     Font font = super.getFont();
/*     */ 
/*  85 */     if (font == null) {
/*  86 */       return (Font)UIManager.getDefaults().get("Panel.font");
/*     */     }
/*  88 */     return font;
/*     */   }
/*     */ 
/*     */   public void setStyledString(StyledStringTransformer styledStringTransformer)
/*     */   {
/*  93 */     getActionListener().actionPerformed(null);
/*  94 */     this.styledString = styledStringTransformer;
/*  95 */     this.attributedText = this.styledString.transformToAttributedString();
/*     */   }
/*     */ 
/*     */   public void setColor(Color color)
/*     */   {
/* 105 */     setFontColor(color);
/* 106 */     super.setColor(color);
/*     */   }
/*     */ 
/*     */   public void setFontColor(Color color)
/*     */   {
/* 111 */     if (null == color) {
/* 112 */       throw new InvalidParameterException("Text color cannot be null");
/*     */     }
/* 114 */     if (null == this.styledString) {
/* 115 */       setText(null);
/*     */     }
/* 117 */     setStyledString(new StyledStringTransformer(this.styledString.getSource(), this.styledString.getFontFamilyName(), color, this.styledString.getFontSize(), this.styledString.getHorizontalAlignment()));
/*     */   }
/*     */ 
/*     */   public Color getFontColor()
/*     */   {
/* 126 */     return null == this.styledString ? null : this.styledString.getFontColor();
/*     */   }
/*     */ 
/*     */   public StyledStringTransformer getStyledStringTransformer() {
/* 130 */     return this.styledString;
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 135 */     if (this.attributedText == null) {
/* 136 */       if (this.styledString == null) {
/* 137 */         return;
/*     */       }
/* 139 */       this.attributedText = this.styledString.transformToAttributedString();
/*     */     }
/*     */ 
/* 142 */     int bottomLeftX = mapper.xt(this.times[0]);
/* 143 */     int bottomLeftY = mapper.yv(this.prices[0]);
/* 144 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/* 146 */     AffineTransform originalTransform = g2.getTransform();
/*     */ 
/* 148 */     g2.translate(bottomLeftX, bottomLeftY);
/* 149 */     g2.transform(AffineTransform.getRotateInstance(getTextAngle()));
/*     */ 
/* 151 */     FontRenderContext frc = g2.getFontRenderContext();
/* 152 */     float maxRowHeight = this.styledString.getFontSize() * 1.1F;
/*     */ 
/* 154 */     float heightToAdd = 0.0F;
/* 155 */     float maxRowWidth = 0.0F;
/* 156 */     for (int i = this.attributedText.length - 1; i >= 0; i--) {
/* 157 */       TextLayout textLayout = new TextLayout(this.attributedText[i].getIterator(), frc);
/*     */ 
/* 160 */       textLayout.draw(g2, 0.0F, 0.0F - heightToAdd);
/* 161 */       heightToAdd += maxRowHeight;
/* 162 */       maxRowWidth = Math.max((float)textLayout.getBounds().getWidth(), maxRowWidth);
/* 163 */       if (i == 0) {
/* 164 */         Rectangle2D tlBounds = textLayout.getBounds();
/* 165 */         tlBounds.setRect(tlBounds.getX(), tlBounds.getY(), maxRowWidth + 2.0F, heightToAdd);
/* 166 */         this.stringBounds = tlBounds;
/*     */       }
/*     */     }
/*     */ 
/* 170 */     g2.setTransform(originalTransform);
/*     */   }
/*     */ 
/*     */   protected void drawSpecificHandlers(Graphics g, IMapper mapper)
/*     */   {
/* 200 */     if (this.stringBounds == null) {
/* 201 */       return;
/*     */     }
/* 203 */     if (this.angleMiddlePointSelected) {
/* 204 */       Graphics2D g2 = (Graphics2D)g;
/*     */ 
/* 206 */       int x = mapper.xt(this.times[0]);
/* 207 */       int y = mapper.yv(this.prices[0]);
/*     */ 
/* 209 */       double radius = Math.sqrt(this.stringBounds.getWidth() * this.stringBounds.getWidth() + this.stringBounds.getHeight() * this.stringBounds.getHeight());
/*     */ 
/* 211 */       Ellipse2D.Double rotationEllipse = new Ellipse2D.Double(x - radius, y - radius, radius * 2.0D, radius * 2.0D);
/*     */ 
/* 213 */       BasicStroke originalStroke = (BasicStroke)g2.getStroke();
/* 214 */       g2.setStroke(new BasicStroke(originalStroke.getLineWidth(), originalStroke.getEndCap(), originalStroke.getLineJoin(), originalStroke.getMiterLimit(), new float[] { 5.0F, 5.0F }, 0.0F));
/*     */ 
/* 216 */       g2.draw(rotationEllipse);
/*     */ 
/* 218 */       g2.setStroke(originalStroke);
/*     */ 
/* 220 */       int x2 = (int)(x + radius * Math.cos(getTextAngle()));
/* 221 */       int y2 = (int)(y + radius * Math.sin(getTextAngle()));
/* 222 */       GraphicHelper.drawSegmentDashedLine(g2, x, y, x2, y2, 5.0D, 5.0D, getColor(), mapper.getWidth(), mapper.getHeight());
/*     */ 
/* 224 */       int angleDegree = (int)((6.283185307179586D - getTextAngle()) % 6.283185307179586D / 3.141592653589793D * 180.0D);
/* 225 */       g.drawString(Integer.toString(angleDegree) + '°', x2 + 4, y2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 231 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 233 */     if (this.stringBounds == null) {
/* 234 */       return handlerMiddlePoints;
/*     */     }
/*     */ 
/* 237 */     int x = mapper.xt(this.times[0]);
/* 238 */     int y = mapper.yv(this.prices[0]);
/* 239 */     handlerMiddlePoints.add(new Point(x, y));
/*     */ 
/* 241 */     double radius = Math.sqrt(this.stringBounds.getWidth() * this.stringBounds.getWidth() + this.stringBounds.getHeight() * this.stringBounds.getHeight());
/*     */ 
/* 243 */     handlerMiddlePoints.add(new Point((int)(x + radius * Math.cos(getTextAngle())), (int)(y + radius * Math.sin(getTextAngle()))));
/*     */ 
/* 245 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/* 250 */     if (!this.angleMiddlePointSelected) {
/* 251 */       super.modifyEditedDrawing(point, prevPoint, mapper, range);
/*     */     } else {
/* 253 */       int x = mapper.xt(this.times[0]);
/* 254 */       int y = mapper.yv(this.prices[0]);
/*     */ 
/* 256 */       double dx = point.x - x;
/* 257 */       double dy = point.y - y;
/*     */ 
/* 259 */       if (Double.compare(dx, 0.0D) == 0) {
/* 260 */         if (dy > 0.0D)
/* 261 */           this.textAngle = 1.570796326794897D;
/*     */         else
/* 263 */           this.textAngle = -1.570796326794897D;
/*     */       }
/* 265 */       else if (dx < 0.0D)
/* 266 */         this.textAngle = (Math.atan(dy / dx) + 3.141592653589793D);
/*     */       else
/* 268 */         this.textAngle = Math.atan(dy / dx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 277 */     if (this.stringBounds == null) {
/* 278 */       return false;
/*     */     }
/*     */ 
/* 281 */     double firstAngle = 6.283185307179586D - getTextAngle();
/* 282 */     double secondAngle = firstAngle + 1.570796326794897D;
/*     */ 
/* 284 */     double width = this.stringBounds.getWidth();
/* 285 */     double height = this.stringBounds.getHeight();
/*     */ 
/* 287 */     int OUTSET = 5;
/* 288 */     width = Math.sqrt(width * width + height * height) + 5.0D;
/*     */ 
/* 290 */     int[] x = new int[4];
/* 291 */     int[] y = new int[4];
/*     */ 
/* 293 */     x[1] = mapper.xt(this.times[0]);
/* 294 */     y[1] = mapper.yv(this.prices[0]);
/*     */ 
/* 296 */     x[0] = (int)(x[1] + width * Math.cos(getTextAngle()));
/* 297 */     y[0] = (int)(y[1] + width * Math.sin(getTextAngle()));
/*     */ 
/* 299 */     x[2] = (int)(x[1] + height * Math.cos(secondAngle));
/* 300 */     y[2] = (int)(y[1] - height * Math.sin(secondAngle));
/*     */ 
/* 302 */     x[3] = (x[0] + x[2] - x[1]);
/* 303 */     y[3] = (y[0] + y[2] - y[1]);
/*     */ 
/* 305 */     this.stringPolygon = new Polygon(x, y, 4);
/*     */ 
/* 308 */     return this.stringPolygon.contains(point);
/*     */   }
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int range)
/*     */   {
/* 313 */     super.updateSelectedHandler(point, mapper, range);
/*     */ 
/* 315 */     if (-1 == this.selectedHandlerIndex) {
/* 316 */       Rectangle2D.Float pointRect = new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2);
/* 317 */       this.angleMiddlePointSelected = isAngleMiddlePointSelected(mapper, pointRect);
/*     */     } else {
/* 319 */       this.angleMiddlePointSelected = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isAngleMiddlePointSelected(IMapper mapper, Rectangle2D.Float pointRect) {
/* 324 */     int x = mapper.xt(this.times[0]);
/* 325 */     int y = mapper.yv(this.prices[0]);
/* 326 */     double radius = Math.sqrt(this.stringBounds.getWidth() * this.stringBounds.getWidth() + this.stringBounds.getHeight() * this.stringBounds.getHeight());
/*     */ 
/* 328 */     return pointRect.contains(new Point((int)(x + radius * Math.cos(getTextAngle())), (int)(y + radius * Math.sin(getTextAngle()))));
/*     */   }
/*     */ 
/*     */   public boolean supportsStyledLabel()
/*     */   {
/* 333 */     return true;
/*     */   }
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 338 */     if ((text == null) || (text.isEmpty())) {
/* 339 */       text = " ";
/*     */     }
/* 341 */     super.setText(text);
/*     */ 
/* 343 */     if (null == this.styledString) {
/* 344 */       Font font = getFont();
/* 345 */       setStyledString(new StyledStringTransformer(text, font.getFamily(), getColor(), font.getSize(), getHorizontalAlignment()));
/*     */     }
/*     */     else
/*     */     {
/* 355 */       setStyledString(new StyledStringTransformer(text, this.styledString.getFontFamilyName(), this.styledString.getFontColor(), this.styledString.getFontSize(), this.styledString.getHorizontalAlignment()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/* 369 */     this.times[0] = time;
/* 370 */     this.prices[0] = price;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 375 */     return 1;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 380 */     return "Text";
/*     */   }
/*     */ 
/*     */   public TextChartObject clone()
/*     */   {
/* 385 */     return new TextChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 390 */     return "item.text";
/*     */   }
/*     */ 
/*     */   public double getTextAngle()
/*     */   {
/* 395 */     return this.textAngle;
/*     */   }
/*     */ 
/*     */   public void setTextAngle(double textAngle)
/*     */   {
/* 400 */     this.textAngle = textAngle;
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 408 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.TextChartObject
 * JD-Core Version:    0.6.0
 */