/*      */ package com.dukascopy.charts.drawings;
/*      */ 
/*      */ import com.dukascopy.api.ChartObjectAdapter;
/*      */ import com.dukascopy.api.ChartObjectEvent;
/*      */ import com.dukascopy.api.ChartObjectListener;
/*      */ import com.dukascopy.api.IChart.Type;
/*      */ import com.dukascopy.api.IChartObject;
/*      */ import com.dukascopy.api.IChartObject.ATTR_BOOLEAN;
/*      */ import com.dukascopy.api.IChartObject.ATTR_COLOR;
/*      */ import com.dukascopy.api.IChartObject.ATTR_DOUBLE;
/*      */ import com.dukascopy.api.IChartObject.ATTR_INT;
/*      */ import com.dukascopy.api.IChartObject.ATTR_LONG;
/*      */ import com.dukascopy.api.IChartObject.ATTR_TEXT;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.Unit;
/*      */ import com.dukascopy.charts.dialogs.indicators.ColorJComboBox;
/*      */ import com.dukascopy.charts.mappers.IMapper;
/*      */ import com.dukascopy.charts.persistence.ITheme;
/*      */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*      */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*      */ import com.dukascopy.charts.persistence.ThemeManager;
/*      */ import com.dukascopy.charts.settings.ChartSettings;
/*      */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*      */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.util.ColorUtils;
/*      */ import java.awt.AlphaComposite;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Composite;
/*      */ import java.awt.Font;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.Stroke;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.Serializable;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.Random;
/*      */ import java.util.TimeZone;
/*      */ import javax.swing.event.SwingPropertyChangeSupport;
/*      */ 
/*      */ public abstract class ChartObject
/*      */   implements IChartObject, Serializable
/*      */ {
/*   51 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
/*      */   private static final long serialVersionUID = 2L;
/*      */   private static final Random RANDOM;
/*      */   private static Color COLOR;
/*      */   protected static final int HANDLER_DEFAULT_WIDTH = 2;
/*      */   protected static final Rectangle ZERO_RECTANGLE;
/*      */   final String key;
/*      */   final IChart.Type type;
/*   64 */   String text = "";
/*      */   Font font;
/*   66 */   int horizontalAlignment = 4;
/*      */   Color color;
/*      */   private Float alpha;
/*      */   transient Stroke stroke;
/*      */   protected String tooltip;
/*   73 */   private transient boolean menuEnabled = true;
/*   74 */   protected transient boolean isUnderEdit = false;
/*   75 */   protected transient boolean isHighlighted = false;
/*   76 */   protected transient int selectedHandlerIndex = -1;
/*   77 */   protected transient int currentPoint = 0;
/*      */ 
/*   79 */   protected final long[] times = { -1L, -1L, -1L };
/*      */ 
/*   81 */   protected final double[] prices = { -1.0D, -1.0D, -1.0D };
/*      */ 
/*   84 */   protected boolean sticky = true;
/*      */ 
/*   86 */   private transient ActionListener actionListener = new NullActionListener(null);
/*   87 */   private transient ChartObjectListener chartObjectListener = new ChartObjectAdapter();
/*      */   protected transient SwingPropertyChangeSupport changeSupport;
/*      */   private transient List<Point> handlerMiddlePoints;
/*      */   private String ownerId;
/*      */ 
/*      */   protected ChartObject(String key, IChart.Type type)
/*      */   {
/*  108 */     this.key = (key == null ? Long.toString(RANDOM.nextLong()) : key);
/*  109 */     this.type = type;
/*  110 */     this.font = ThemeManager.getTheme().getFont(ITheme.TextElement.DEFAULT);
/*      */ 
/*  112 */     if (ChartSettings.getBoolean(ChartSettings.Option.RANDOW_DRAWINGS_COLOR))
/*      */       do {
/*  114 */         int colorIndex = RANDOM.nextInt(ColorJComboBox.colArr.length);
/*  115 */         Color randomColor = ColorJComboBox.colArr[colorIndex];
/*      */ 
/*  117 */         if ((randomColor.equals(COLOR)) || (!ColorUtils.isVisible(randomColor, ThemeManager.getTheme().getColor(ITheme.ChartElement.BACKGROUND)))) {
/*      */           continue;
/*      */         }
/*  120 */         COLOR = randomColor;
/*  121 */         this.color = randomColor;
/*      */       }
/*  123 */       while (this.color == null);
/*      */   }
/*      */ 
/*      */   protected ChartObject(ChartObject chartObject)
/*      */   {
/*  128 */     this(null, chartObject.getType());
/*      */ 
/*  130 */     System.arraycopy(chartObject.times, 0, this.times, 0, chartObject.times.length);
/*      */ 
/*  132 */     System.arraycopy(chartObject.prices, 0, this.prices, 0, chartObject.prices.length);
/*      */ 
/*  135 */     this.text = chartObject.text;
/*  136 */     this.font = chartObject.getFont();
/*  137 */     this.color = chartObject.getColor();
/*  138 */     this.alpha = new Float(chartObject.getOpacity());
/*  139 */     this.stroke = chartObject.getStroke();
/*      */   }
/*      */ 
/*      */   public boolean isGlobal()
/*      */   {
/*  150 */     return false;
/*      */   }
/*      */ 
/*      */   public void setAttrLong(IChartObject.ATTR_LONG field, long value)
/*      */   {
/*  155 */     setTime(field, value);
/*      */   }
/*      */ 
/*      */   public long getAttrLong(IChartObject.ATTR_LONG field)
/*      */   {
/*  160 */     return getTime(field);
/*      */   }
/*      */ 
/*      */   public void setAttrDouble(IChartObject.ATTR_DOUBLE field, double value)
/*      */   {
/*  165 */     setPrice(field, value);
/*      */   }
/*      */ 
/*      */   public double getAttrDouble(IChartObject.ATTR_DOUBLE field)
/*      */   {
/*  170 */     return getPrice(field);
/*      */   }
/*      */ 
/*      */   public void setAttrInt(IChartObject.ATTR_INT field, int value)
/*      */   {
/*  176 */     switch (field)
/*      */     {
/*      */     case WIDTH:
/*  179 */       setLineWidth(value);
/*  180 */       break;
/*      */     case STYLE:
/*  183 */       setLineStyle(value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getAttrInt(IChartObject.ATTR_INT field)
/*      */   {
/*  191 */     BasicStroke editedObjectStroke = (BasicStroke)getStroke();
/*      */ 
/*  193 */     switch (field)
/*      */     {
/*      */     case WIDTH:
/*  196 */       return (int)editedObjectStroke.getLineWidth();
/*      */     case STYLE:
/*  199 */       return DrawingsHelper.DashPattern.getStyle(editedObjectStroke.getDashArray());
/*      */     }
/*      */ 
/*  203 */     return -1;
/*      */   }
/*      */ 
/*      */   public void setAttrBoolean(IChartObject.ATTR_BOOLEAN field, boolean value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean getAttrBoolean(IChartObject.ATTR_BOOLEAN field)
/*      */   {
/*  213 */     return false;
/*      */   }
/*      */ 
/*      */   public void setAttrColor(IChartObject.ATTR_COLOR field, Color value)
/*      */   {
/*  218 */     if (IChartObject.ATTR_COLOR.COLOR == field)
/*  219 */       setColor(value);
/*      */   }
/*      */ 
/*      */   public Color getAttrColor(IChartObject.ATTR_COLOR field)
/*      */   {
/*  225 */     if (IChartObject.ATTR_COLOR.COLOR == field) {
/*  226 */       return getColor();
/*      */     }
/*  228 */     return null;
/*      */   }
/*      */ 
/*      */   public void setFont(Font font)
/*      */   {
/*  233 */     Font old = this.font;
/*  234 */     this.font = font;
/*  235 */     firePropertyChange("font", old, font);
/*      */   }
/*      */ 
/*      */   public void setText(String text)
/*      */   {
/*  240 */     if (text == null) {
/*  241 */       text = "";
/*      */     }
/*      */ 
/*  244 */     getActionListener().actionPerformed(null);
/*  245 */     this.text = text;
/*      */   }
/*      */ 
/*      */   public final void setText(String text, Font font)
/*      */   {
/*  250 */     setFont(font);
/*  251 */     setText(text);
/*      */   }
/*      */ 
/*      */   public void setText(String text, int horizontalAlignment)
/*      */   {
/*  256 */     this.horizontalAlignment = horizontalAlignment;
/*  257 */     setText(text);
/*      */   }
/*      */ 
/*      */   public void setText(String text, Font font, int horizontalAlignment)
/*      */   {
/*  262 */     this.horizontalAlignment = horizontalAlignment;
/*  263 */     setText(text, font);
/*      */   }
/*      */ 
/*      */   public final Color getColor()
/*      */   {
/*  268 */     if (this.color == null) {
/*  269 */       return ThemeManager.getTheme().getColor(ITheme.ChartElement.DRAWING);
/*      */     }
/*      */ 
/*  272 */     return this.color;
/*      */   }
/*      */ 
/*      */   public void setColor(Color color)
/*      */   {
/*  277 */     Color old = this.color;
/*  278 */     this.color = color;
/*  279 */     firePropertyChange("color", old, color);
/*  280 */     getActionListener().actionPerformed(null);
/*      */   }
/*      */ 
/*      */   public String getAttrText(IChartObject.ATTR_TEXT field)
/*      */   {
/*  285 */     return null;
/*      */   }
/*      */ 
/*      */   public void setAttrText(IChartObject.ATTR_TEXT field, String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public String getText()
/*      */   {
/*  295 */     return this.text;
/*      */   }
/*      */ 
/*      */   public Font getFont() {
/*  299 */     return this.font;
/*      */   }
/*      */ 
/*      */   public int getHorizontalAlignment() {
/*  303 */     return this.horizontalAlignment;
/*      */   }
/*      */ 
/*      */   public IChart.Type getType()
/*      */   {
/*  308 */     return this.type;
/*      */   }
/*      */ 
/*      */   public String getKey()
/*      */   {
/*  313 */     return this.key;
/*      */   }
/*      */ 
/*      */   public void move(long time, double price)
/*      */   {
/*      */   }
/*      */ 
/*      */   public final Stroke getStroke()
/*      */   {
/*  323 */     return this.stroke;
/*      */   }
/*      */ 
/*      */   public final void setStroke(Stroke stroke)
/*      */   {
/*  328 */     Stroke old = this.stroke;
/*  329 */     this.stroke = stroke;
/*  330 */     firePropertyChange("stroke", old, stroke);
/*  331 */     getActionListener().actionPerformed(null);
/*      */   }
/*      */ 
/*      */   public void setLineStyle(int lineStyle)
/*      */   {
/*  336 */     BasicStroke editedObjectStroke = (BasicStroke)getStroke();
/*      */     Stroke stroke;
/*      */     Stroke stroke;
/*  339 */     if (editedObjectStroke == null) {
/*  340 */       stroke = new BasicStroke(1.0F, 0, 2, 0.0F, DrawingsHelper.DashPattern.getPattern(lineStyle).getDashArray(), 0.0F);
/*      */     }
/*      */     else
/*      */     {
/*  346 */       stroke = new BasicStroke(editedObjectStroke.getLineWidth(), editedObjectStroke.getEndCap(), editedObjectStroke.getLineJoin(), 0.0F, DrawingsHelper.DashPattern.getPattern(lineStyle).getDashArray(), 0.0F);
/*      */     }
/*      */ 
/*  352 */     setStroke(stroke);
/*      */   }
/*      */ 
/*      */   public void setLineWidth(float width)
/*      */   {
/*  357 */     BasicStroke editedObjectStroke = (BasicStroke)getStroke();
/*      */     Stroke stroke;
/*      */     Stroke stroke;
/*  360 */     if (editedObjectStroke == null) {
/*  361 */       stroke = new BasicStroke(width, 0, 2, 0.0F, null, 0.0F);
/*      */     }
/*      */     else
/*      */     {
/*  365 */       stroke = new BasicStroke(width, editedObjectStroke.getEndCap(), editedObjectStroke.getLineJoin(), 0.0F, editedObjectStroke.getDashArray(), 0.0F);
/*      */     }
/*      */ 
/*  370 */     setStroke(stroke);
/*      */   }
/*      */ 
/*      */   public long getTime(int pointIndex)
/*      */   {
/*  376 */     validatePointIndex(Integer.valueOf(pointIndex));
/*  377 */     return this.times[pointIndex];
/*      */   }
/*      */ 
/*      */   public double getPrice(int pointIndex)
/*      */   {
/*  382 */     validatePointIndex(Integer.valueOf(pointIndex));
/*  383 */     return this.prices[pointIndex];
/*      */   }
/*      */ 
/*      */   public boolean supportsStyledLabel() {
/*  387 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isHandlerSelected() {
/*  391 */     return this.selectedHandlerIndex >= 0;
/*      */   }
/*      */ 
/*      */   public void setActionListener(ActionListener actionListener) {
/*  395 */     this.actionListener = actionListener;
/*      */   }
/*      */ 
/*      */   public ActionListener getActionListener() {
/*  399 */     if (this.actionListener == null) {
/*  400 */       this.actionListener = new NullActionListener(null);
/*      */     }
/*  402 */     return this.actionListener;
/*      */   }
/*      */ 
/*      */   public void setChartObjectListener(ChartObjectListener listener) {
/*  406 */     this.chartObjectListener = listener;
/*      */   }
/*      */ 
/*      */   public ChartObjectListener getChartObjectListener() {
/*  410 */     if (this.chartObjectListener == null) {
/*  411 */       return this.chartObjectListener = new ChartObjectAdapter();
/*      */     }
/*  413 */     return this.chartObjectListener;
/*      */   }
/*      */ 
/*      */   private int getHandlerWidth() {
/*  417 */     BasicStroke stroke = (BasicStroke)getStroke();
/*  418 */     if (stroke == null) {
/*  419 */       return 2;
/*      */     }
/*  421 */     return Math.max((int)stroke.getLineWidth(), 2);
/*      */   }
/*      */ 
/*      */   protected int getLineWidth() {
/*  425 */     return 1;
/*      */   }
/*      */ 
/*      */   public void drawHandlers(Graphics g, IMapper mapper) {
/*  429 */     if ((!isDrawable(mapper)) || (!isUnderEdit())) {
/*  430 */       return;
/*      */     }
/*      */ 
/*  433 */     Color prevColor = g.getColor();
/*  434 */     Object hintValue = ((Graphics2D)g).getRenderingHint(RenderingHints.KEY_ANTIALIASING);
/*      */ 
/*  436 */     if (hintValue != RenderingHints.VALUE_ANTIALIAS_ON) {
/*  437 */       ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*      */     }
/*      */ 
/*  440 */     g.setColor(getColor());
/*  441 */     ((Graphics2D)g).setComposite(AlphaComposite.getInstance(3, getOpacity()));
/*      */ 
/*  444 */     int handlerWidth = getHandlerWidth();
/*  445 */     List handlerMiddlePoints = getHandlerMiddlePoints(mapper);
/*  446 */     for (Point handlerMiddlePoint : handlerMiddlePoints) {
/*  447 */       int x = handlerMiddlePoint.x - handlerWidth;
/*  448 */       int y = handlerMiddlePoint.y - handlerWidth;
/*      */ 
/*  450 */       if ((isUnderEdit()) && (isHandlerSelected()) && (isHandlerSelected(this.selectedHandlerIndex, handlerMiddlePoint, mapper, handlerWidth)))
/*      */       {
/*  454 */         g.drawOval(x, y, handlerWidth * 2, handlerWidth * 2);
/*  455 */       } else if (isUnderEdit()) {
/*  456 */         g.fillOval(x - 1, y - 1, 2 + handlerWidth * 2, 2 + handlerWidth * 2);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  461 */     drawSpecificHandlers(g, mapper);
/*      */ 
/*  463 */     ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, hintValue);
/*      */ 
/*  465 */     g.setColor(prevColor);
/*      */   }
/*      */ 
/*      */   protected void drawSpecificHandlers(Graphics g, IMapper dataMapper)
/*      */   {
/*      */   }
/*      */ 
/*      */   public final void drawGlobalOnMain(Graphics g, IMapper mapper, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, boolean hasSubcharts)
/*      */   {
/*  476 */     if (hasSubcharts) {
/*  477 */       draw(g, mapper, formattersManager, drawingsLabelHelper, ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS);
/*      */     }
/*      */     else
/*  480 */       draw(g, mapper, formattersManager, drawingsLabelHelper, ChartObjectDrawingMode.DEFAULT);
/*      */   }
/*      */ 
/*      */   public final void drawGlobalOnSub(Graphics g, IMapper mapper, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, boolean isLast)
/*      */   {
/*  488 */     if (isLast) {
/*  489 */       draw(g, mapper, formattersManager, drawingsLabelHelper, ChartObjectDrawingMode.GLOBAL_ON_LAST_SUBCHART);
/*      */     }
/*      */     else
/*  492 */       draw(g, mapper, formattersManager, drawingsLabelHelper, ChartObjectDrawingMode.GLOBAL_ON_SUBCHART);
/*      */   }
/*      */ 
/*      */   public final void draw(Graphics g, IMapper mapper, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper)
/*      */   {
/*  500 */     draw(g, mapper, formattersManager, drawingsLabelHelper, ChartObjectDrawingMode.DEFAULT);
/*      */   }
/*      */ 
/*      */   protected void draw(Graphics g, IMapper mapper, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*      */   {
/*  508 */     if (!isDrawable(mapper)) {
/*  509 */       return;
/*      */     }
/*      */ 
/*  512 */     Graphics2D g2 = (Graphics2D)g;
/*      */ 
/*  514 */     Font prevFont = g2.getFont();
/*  515 */     Color prevColor = g2.getColor();
/*  516 */     Stroke prevStroke = g2.getStroke();
/*  517 */     Composite prevComposite = g2.getComposite();
/*  518 */     Object hintValue = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
/*  519 */     if (hintValue != RenderingHints.VALUE_ANTIALIAS_ON) {
/*  520 */       g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*      */     }
/*      */ 
/*  524 */     g2.setFont(getFont());
/*  525 */     g2.setColor(getColor());
/*  526 */     g2.setComposite(AlphaComposite.getInstance(3, getOpacity()));
/*      */ 
/*  528 */     if (getStroke() != null) {
/*  529 */       BasicStroke chartStroke = (BasicStroke)getStroke();
/*  530 */       g2.setStroke(new BasicStroke(chartStroke.getLineWidth(), chartStroke.getEndCap(), chartStroke.getLineJoin(), chartStroke.getMiterLimit(), scaleDashArray((int)chartStroke.getLineWidth(), chartStroke.getDashArray()), chartStroke.getDashPhase()));
/*      */     }
/*      */ 
/*  538 */     Rectangle labelDimension = drawFormattedLabel(g2, mapper, formattersManager, drawingsLabelHelper, drawingMode);
/*      */ 
/*  540 */     Rectangle textDimension = drawTextAsLabel(g2, mapper, labelDimension, formattersManager, drawingsLabelHelper, drawingMode);
/*      */ 
/*  542 */     drawChartObject(g2, mapper, labelDimension, textDimension, formattersManager, drawingMode);
/*      */ 
/*  544 */     drawMetaData(g2, mapper);
/*      */ 
/*  546 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, hintValue);
/*  547 */     g2.setFont(prevFont);
/*  548 */     g2.setColor(prevColor);
/*  549 */     g2.setStroke(prevStroke);
/*  550 */     g2.setComposite(prevComposite);
/*      */   }
/*      */ 
/*      */   protected Rectangle drawFormattedLabel(Graphics2D g, IMapper mapper, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*      */   {
/*  557 */     return ZERO_RECTANGLE;
/*      */   }
/*      */ 
/*      */   private static float[] scaleDashArray(int ratio, float[] dashArray) {
/*  561 */     if (dashArray == null) {
/*  562 */       return null;
/*      */     }
/*      */ 
/*  565 */     float[] dashPattern = new float[dashArray.length];
/*      */ 
/*  567 */     for (int i = 0; i < dashPattern.length; i++) {
/*  568 */       dashArray[i] *= ratio;
/*      */     }
/*      */ 
/*  571 */     return dashPattern;
/*      */   }
/*      */ 
/*      */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*      */   {
/*  578 */     return ZERO_RECTANGLE;
/*      */   }
/*      */ 
/*      */   protected boolean isDrawable(IMapper mapper)
/*      */   {
/*  588 */     for (int i = 0; i < getPointsCount(); i++) {
/*  589 */       if (!isValidPoint(i)) {
/*  590 */         return false;
/*      */       }
/*      */ 
/*  593 */       if (isOutOfRange(i, mapper)) {
/*  594 */         return false;
/*      */       }
/*      */     }
/*      */ 
/*  598 */     return true;
/*      */   }
/*      */ 
/*      */   protected boolean arePointsValid() {
/*  602 */     for (int i = 0; i < getPointsCount(); i++) {
/*  603 */       if (!isValidPoint(i)) {
/*  604 */         return false;
/*      */       }
/*      */     }
/*  607 */     return true;
/*      */   }
/*      */ 
/*      */   protected abstract void drawChartObject(Graphics paramGraphics, IMapper paramIMapper, Rectangle paramRectangle1, Rectangle paramRectangle2, FormattersManager paramFormattersManager, ChartObjectDrawingMode paramChartObjectDrawingMode);
/*      */ 
/*      */   protected void drawMetaData(Graphics g, IMapper mapper)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void modifyNewDrawing(Point point, IMapper mapper, int range)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean addNewPoint(Point point, IMapper mapper)
/*      */   {
/*  624 */     this.times[this.currentPoint] = mapper.tx(point.x);
/*  625 */     this.prices[this.currentPoint] = mapper.vy(point.y);
/*      */ 
/*  627 */     if (this.currentPoint + 1 < getPointsCount()) {
/*  628 */       this.currentPoint += 1;
/*  629 */       return false;
/*      */     }
/*      */ 
/*  632 */     return true;
/*      */   }
/*      */ 
/*      */   public void finishDrawing()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*      */   {
/*  645 */     double newPrice = mapper.vy(point.y);
/*      */ 
/*  647 */     if (isHandlerSelected()) {
/*  648 */       this.times[this.selectedHandlerIndex] = mapper.tx(point.x);
/*  649 */       this.prices[this.selectedHandlerIndex] = newPrice;
/*      */     } else {
/*  651 */       if (prevPoint == null) {
/*  652 */         return;
/*      */       }
/*      */ 
/*  655 */       if (getPointsCount() > 0)
/*      */       {
/*  660 */         int mapperCalculationsIssueOffset = mapper.xt(mapper.tx(point.x)) - point.x;
/*      */ 
/*  665 */         int xDiff = mapper.xt(mapper.tx(point.x)) - mapper.xt(mapper.tx(prevPoint.x));
/*      */ 
/*  667 */         long[] timeBuffer = shiftTimesByPixels(xDiff - mapperCalculationsIssueOffset, mapper);
/*  668 */         if (timeBuffer != null) {
/*  669 */           System.arraycopy(timeBuffer, 0, this.times, 0, getPointsCount());
/*      */         }
/*      */ 
/*  672 */         double priceDiff = newPrice - mapper.vy(prevPoint.y);
/*      */ 
/*  674 */         double[] priceBuffer = shiftPricesByValue(priceDiff);
/*  675 */         if (priceBuffer != null)
/*  676 */           System.arraycopy(priceBuffer, 0, this.prices, 0, getPointsCount());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final List<Point> getHandlerMiddlePoints()
/*      */   {
/*  683 */     if (this.handlerMiddlePoints == null)
/*  684 */       this.handlerMiddlePoints = new ArrayList();
/*      */     else {
/*  686 */       this.handlerMiddlePoints.clear();
/*      */     }
/*      */ 
/*  689 */     return this.handlerMiddlePoints;
/*      */   }
/*      */ 
/*      */   public List<Point> getHandlerMiddlePoints(IMapper mapper) {
/*  693 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*      */ 
/*  695 */     for (int i = 0; ((0 == this.currentPoint) || (i <= this.currentPoint)) && (i < getPointsCount()); i++) {
/*  696 */       handlerMiddlePoints.add(i, new Point(mapper.xt(this.times[i]), mapper.yv(this.prices[i])));
/*      */     }
/*      */ 
/*  700 */     return handlerMiddlePoints;
/*      */   }
/*      */   public abstract boolean intersects(Point paramPoint, IMapper paramIMapper, int paramInt);
/*      */ 
/*      */   public void updateSelectedHandler(Point point, IMapper mapper, int range) {
/*  706 */     updateSelectedHandler(getPointsCount(), point, mapper, range);
/*      */   }
/*      */ 
/*      */   protected void updateSelectedHandler(int pointsCount, Point point, IMapper mapper, int range)
/*      */   {
/*  711 */     this.selectedHandlerIndex = -1;
/*      */ 
/*  713 */     for (int i = 0; i < pointsCount; i++)
/*  714 */       if (isHandlerSelected(i, point, mapper, range)) {
/*  715 */         this.selectedHandlerIndex = i;
/*  716 */         break;
/*      */       }
/*      */   }
/*      */ 
/*      */   protected boolean isHandlerSelected(int handlerIndex, Point point, IMapper mapper, int range)
/*      */   {
/*  723 */     if (!isValidPoint(handlerIndex)) {
/*  724 */       return false;
/*      */     }
/*  726 */     int timeX = mapper.xt(this.times[handlerIndex]);
/*  727 */     int priceY = mapper.yv(this.prices[handlerIndex]);
/*  728 */     return isInRange(timeX, priceY, point, range);
/*      */   }
/*      */ 
/*      */   public void moveLeft(IMapper dataMapper) {
/*  732 */     int barWidth = dataMapper.getBarWidth();
/*  733 */     long[] buffer = shiftTimesByPixels(-barWidth, dataMapper);
/*  734 */     if (buffer != null)
/*  735 */       System.arraycopy(buffer, 0, this.times, 0, getPointsCount());
/*      */   }
/*      */ 
/*      */   public void moveRight(IMapper dataMapper)
/*      */   {
/*  740 */     int barWidth = dataMapper.getBarWidth();
/*  741 */     long[] buffer = shiftTimesByPixels(barWidth, dataMapper);
/*  742 */     if (buffer != null)
/*  743 */       System.arraycopy(buffer, 0, this.times, 0, getPointsCount());
/*      */   }
/*      */ 
/*      */   private long[] shiftTimesByPixels(int pixels, IMapper mapper)
/*      */   {
/*  751 */     long[] buffer = null;
/*  752 */     if (getPointsCount() > 0) {
/*  753 */       buffer = Arrays.copyOf(this.times, getPointsCount());
/*      */ 
/*  755 */       if ((Unit.Month.equals(mapper.getPeriod().getUnit())) || (Unit.Year.equals(mapper.getPeriod().getUnit())))
/*      */       {
/*  762 */         int x = mapper.xt(this.times[0]);
/*  763 */         long timeDiff = mapper.tx(x + pixels) - this.times[0];
/*  764 */         for (int i = 0; i < getPointsCount(); i++)
/*  765 */           buffer[i] += timeDiff;
/*      */       }
/*      */       else
/*      */       {
/*  769 */         for (int i = 0; i < getPointsCount(); i++) {
/*  770 */           int x = mapper.xt(this.times[i]);
/*  771 */           if (mapper.isXOutOfRange(x + pixels)) {
/*  772 */             return null;
/*      */           }
/*  774 */           buffer[i] = mapper.tx(x + pixels);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  779 */     return buffer;
/*      */   }
/*      */ 
/*      */   public void moveDown(IMapper mapper) {
/*  783 */     double[] buffer = shiftPricesByValue(-mapper.getValuesInOnePixel());
/*  784 */     if (buffer != null)
/*  785 */       System.arraycopy(buffer, 0, this.prices, 0, getPointsCount());
/*      */   }
/*      */ 
/*      */   public void moveUp(IMapper mapper)
/*      */   {
/*  790 */     double[] buffer = shiftPricesByValue(mapper.getValuesInOnePixel());
/*  791 */     if (buffer != null)
/*  792 */       System.arraycopy(buffer, 0, this.prices, 0, getPointsCount());
/*      */   }
/*      */ 
/*      */   private double[] shiftPricesByValue(double price)
/*      */   {
/*  797 */     double[] buffer = null;
/*  798 */     if (getPointsCount() > 0) {
/*  799 */       buffer = Arrays.copyOf(this.prices, getPointsCount());
/*  800 */       for (int i = 0; i < getPointsCount(); i++) {
/*  801 */         buffer[i] += price;
/*      */       }
/*      */     }
/*  804 */     return buffer;
/*      */   }
/*      */ 
/*      */   public final void mouseWheelDown(IMapper mapper) {
/*  808 */     moveDown(mapper);
/*      */   }
/*      */ 
/*      */   public final void mouseWheelUp(IMapper mapper) {
/*  812 */     moveUp(mapper);
/*      */   }
/*      */ 
/*      */   public final void setHighlighted(boolean isHiglighted) {
/*  816 */     this.isHighlighted = isHiglighted;
/*  817 */     if (isHiglighted) {
/*  818 */       getChartObjectListener().highlighted(new ChartObjectEvent(this));
/*      */     }
/*      */     else
/*  821 */       getChartObjectListener().highlightingRemoved(new ChartObjectEvent(this));
/*      */   }
/*      */ 
/*      */   public final boolean isHighlighted()
/*      */   {
/*  827 */     return this.isHighlighted;
/*      */   }
/*      */ 
/*      */   public void setUnderEdit(boolean isUnderEdit) {
/*  831 */     this.isUnderEdit = isUnderEdit;
/*      */   }
/*      */ 
/*      */   public boolean isUnderEdit() {
/*  835 */     return this.isUnderEdit;
/*      */   }
/*      */ 
/*      */   protected final String getAdjustedLabel(String text) {
/*  839 */     int CHAR_COUNT = 30;
/*  840 */     if ((text != null) && (text.length() > 30)) {
/*  841 */       StringBuffer adjustedText = new StringBuffer();
/*  842 */       adjustedText.append(text.substring(0, 30));
/*  843 */       adjustedText.append("...");
/*  844 */       return adjustedText.toString();
/*      */     }
/*  846 */     return text;
/*      */   }
/*      */ 
/*      */   protected final boolean isInRange(int x, int y, Point point, int range)
/*      */   {
/*  851 */     return (int)point.distance(x, y) <= range;
/*      */   }
/*      */ 
/*      */   protected final void setTime(IChartObject.ATTR_LONG field, long value) {
/*  855 */     switch (1.$SwitchMap$com$dukascopy$api$IChartObject$ATTR_LONG[field.ordinal()]) {
/*      */     case 1:
/*  857 */       this.times[0] = value;
/*  858 */       break;
/*      */     case 2:
/*  860 */       this.times[1] = value;
/*  861 */       break;
/*      */     case 3:
/*  863 */       this.times[2] = value;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(int pointIndex, long value)
/*      */   {
/*  869 */     if ((pointIndex >= 0) && (pointIndex < 3)) {
/*  870 */       Long old = Long.valueOf(this.times[pointIndex]);
/*  871 */       this.times[pointIndex] = value;
/*  872 */       fireIndexedPropertyChange("time", pointIndex, old, new Long(value));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final long getTime(IChartObject.ATTR_LONG field) {
/*  877 */     switch (1.$SwitchMap$com$dukascopy$api$IChartObject$ATTR_LONG[field.ordinal()]) {
/*      */     case 1:
/*  879 */       return this.times[0];
/*      */     case 2:
/*  881 */       return this.times[1];
/*      */     case 3:
/*  883 */       return this.times[2];
/*      */     }
/*  885 */     return -1L;
/*      */   }
/*      */ 
/*      */   protected final void setPrice(IChartObject.ATTR_DOUBLE field, double value)
/*      */   {
/*  890 */     switch (1.$SwitchMap$com$dukascopy$api$IChartObject$ATTR_DOUBLE[field.ordinal()]) {
/*      */     case 1:
/*  892 */       setPrice(0, value);
/*  893 */       break;
/*      */     case 2:
/*  895 */       setPrice(1, value);
/*  896 */       break;
/*      */     case 3:
/*  898 */       setPrice(2, value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setPrice(int pointIndex, double value)
/*      */   {
/*  904 */     if ((pointIndex >= 0) && (pointIndex < 3)) {
/*  905 */       Double old = Double.valueOf(this.prices[pointIndex]);
/*  906 */       this.prices[pointIndex] = value;
/*  907 */       fireIndexedPropertyChange("price", pointIndex, old, new Double(value));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final double getPrice(IChartObject.ATTR_DOUBLE field) {
/*  912 */     switch (1.$SwitchMap$com$dukascopy$api$IChartObject$ATTR_DOUBLE[field.ordinal()]) {
/*      */     case 1:
/*  914 */       return this.prices[0];
/*      */     case 2:
/*  916 */       return this.prices[1];
/*      */     case 3:
/*  918 */       return this.prices[2];
/*      */     }
/*  920 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   protected boolean isOutOfRange(int pointIndex, IMapper dataMapper, int screenCount)
/*      */   {
/*  925 */     int pointX = dataMapper.xt(this.times[pointIndex]);
/*      */ 
/*  927 */     int screenWidth = dataMapper.getWidth();
/*      */ 
/*  929 */     if ((Math.abs(pointX) > screenWidth * screenCount) && (getStroke() != null)) {
/*  930 */       return true;
/*      */     }
/*  932 */     return pointX == -2147483648;
/*      */   }
/*      */ 
/*      */   protected final boolean isOutOfRange(int pointIndex, IMapper dataMapper)
/*      */   {
/*  942 */     int SCREEN_COUNT = 10;
/*      */ 
/*  944 */     return isOutOfRange(pointIndex, dataMapper, 10);
/*      */   }
/*      */ 
/*      */   protected final boolean isValidPoint(int pointIndex) {
/*  948 */     return ValuePoint.isValid(this.times[pointIndex], this.prices[pointIndex]);
/*      */   }
/*      */ 
/*      */   protected final boolean isValidTime(int timeIndex) {
/*  952 */     return ValuePoint.isValidTime(this.times[timeIndex]);
/*      */   }
/*      */ 
/*      */   protected final boolean isValidPrice(int priceIndex) {
/*  956 */     return ValuePoint.isValidValue(this.prices[priceIndex]);
/*      */   }
/*      */ 
/*      */   public void setSticky(boolean sticky)
/*      */   {
/*  961 */     boolean old = isSticky();
/*  962 */     this.sticky = sticky;
/*  963 */     firePropertyChange("sticky", Boolean.valueOf(old), Boolean.valueOf(sticky));
/*      */   }
/*      */ 
/*      */   public boolean isSticky()
/*      */   {
/*  968 */     return this.sticky;
/*      */   }
/*      */ 
/*      */   public final int hashCode()
/*      */   {
/*  973 */     return this.key.hashCode();
/*      */   }
/*      */ 
/*      */   public boolean hasPriceValue()
/*      */   {
/*  980 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean hasTimeValue()
/*      */   {
/*  987 */     return true;
/*      */   }
/*      */ 
/*      */   public abstract IChartObject clone();
/*      */ 
/*      */   public boolean isMenuEnabled()
/*      */   {
/* 1000 */     return this.menuEnabled;
/*      */   }
/*      */ 
/*      */   public void setMenuEnabled(boolean menuEnabled)
/*      */   {
/* 1005 */     this.menuEnabled = menuEnabled;
/*      */   }
/*      */ 
/*      */   public void setOpacity(float alpha)
/*      */   {
/* 1010 */     Float old = Float.valueOf(getOpacity());
/* 1011 */     this.alpha = new Float(alpha);
/* 1012 */     firePropertyChange("alpha", old, this.alpha);
/*      */   }
/*      */ 
/*      */   public float getOpacity()
/*      */   {
/* 1017 */     if (this.alpha == null) {
/* 1018 */       this.alpha = new Float(1.0F);
/*      */     }
/* 1020 */     return this.alpha.floatValue();
/*      */   }
/*      */ 
/*      */   protected void validatePointIndex(Integer pointIndex) {
/* 1024 */     if (pointIndex == null) {
/* 1025 */       throw new IllegalArgumentException(new StringBuilder().append("Parameter [pointIndex] is [").append(pointIndex).append("]. Please specify valid pointIndex.").toString());
/*      */     }
/*      */ 
/* 1029 */     int pointCount = getPointsCount();
/* 1030 */     if ((pointIndex.intValue() < 0) || (pointIndex.intValue() > pointCount - 1))
/* 1031 */       throw new IllegalArgumentException(new StringBuilder().append("Incorrect point index [").append(pointIndex.intValue() >= 0 ? (pointIndex = Integer.valueOf(pointIndex.intValue() + 1)) : pointIndex).append("] , pointCount = [").append(pointCount).append("] , pointIndex must be less or equal to pointCount.").toString());
/*      */   }
/*      */ 
/*      */   public abstract String getLocalizationKey();
/*      */ 
/*      */   protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
/*      */   {
/* 1047 */     if (null != this.changeSupport)
/* 1048 */       this.changeSupport.firePropertyChange(propertyName, oldValue, newValue);
/*      */   }
/*      */ 
/*      */   protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue)
/*      */   {
/* 1053 */     if (null != this.changeSupport)
/* 1054 */       this.changeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
/*      */   }
/*      */ 
/*      */   public synchronized void addPropertyChangeListener(PropertyChangeListener listener)
/*      */   {
/* 1060 */     if (this.changeSupport == null) {
/* 1061 */       this.changeSupport = new SwingPropertyChangeSupport(this);
/*      */     }
/* 1063 */     this.changeSupport.addPropertyChangeListener(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
/*      */   {
/* 1068 */     if (this.changeSupport == null) {
/* 1069 */       return;
/*      */     }
/* 1071 */     this.changeSupport.removePropertyChangeListener(listener);
/*      */   }
/*      */ 
/*      */   public synchronized PropertyChangeListener[] getPropertyChangeListeners()
/*      */   {
/* 1076 */     if (this.changeSupport == null) {
/* 1077 */       return new PropertyChangeListener[0];
/*      */     }
/* 1079 */     return this.changeSupport.getPropertyChangeListeners();
/*      */   }
/*      */ 
/*      */   public String getOwnerId() {
/* 1083 */     return this.ownerId;
/*      */   }
/*      */ 
/*      */   public void setOwnerId(String ownerId) {
/* 1087 */     this.ownerId = ownerId;
/*      */   }
/*      */ 
/*      */   public boolean isLabelEnabled()
/*      */   {
/* 1095 */     return false;
/*      */   }
/*      */ 
/*      */   public void setTooltip(String tooltip)
/*      */   {
/* 1100 */     this.tooltip = tooltip;
/*      */   }
/*      */ 
/*      */   public String getTooltip() {
/* 1104 */     if ((this.tooltip == null) || (this.tooltip.isEmpty())) {
/* 1105 */       return LocalizationManager.getText(getLocalizationKey());
/*      */     }
/* 1107 */     return this.tooltip;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   53 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*      */ 
/*   57 */     RANDOM = new Random();
/*   58 */     COLOR = ThemeManager.getTheme().getColor(ITheme.ChartElement.DRAWING);
/*      */ 
/*   60 */     ZERO_RECTANGLE = new Rectangle(0, 0, 0, 0);
/*      */   }
/*      */ 
/*      */   private static class NullActionListener
/*      */     implements ActionListener
/*      */   {
/*      */     public void actionPerformed(ActionEvent e)
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.ChartObject
 * JD-Core Version:    0.6.0
 */