/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.CandleInfoParams;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.OhlcAlignment;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.PriceAgregatedInfoParams;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.TickInfoParams;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ThemeManager;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import com.dukascopy.charts.view.swing.AbstractChartWidgetPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class OhlcChartObject extends AbstractWidgetChartObject
/*     */   implements IDynamicChartObject, IOhlcChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final float DEFAULT_BACKGROUND_OPACITY = 0.0F;
/*  37 */   private static final Dimension DEFAULT_SIZE = new Dimension(140, 140);
/*     */   private static final String CHART_OBJECT_NAME = "OHLC Informer";
/*     */   private static final String SEMICOLON_SPACE = ": ";
/*     */   private static final int LABEL_OFFSET = 3;
/*     */   private static final int MIN_FONT_SIZE = 8;
/*     */   private static final int MAX_FONT_SIZE = 40;
/*  48 */   private static final Map<DataType, Enum<?>[]> infoParamsByDataTypeMap = Collections.unmodifiableMap(new EnumMap()
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */   });
/*     */ 
/*  62 */   private static final Map<Enum<?>, Enum<?>[]> linkedParams = Collections.unmodifiableMap(new HashMap()
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */   });
/*     */ 
/*  88 */   private int fontSize = -1;
/*     */   private DataType dataType;
/*  92 */   private IOhlcChartObject.OhlcAlignment alignment = IOhlcChartObject.OhlcAlignment.AUTO;
/*     */ 
/*  94 */   private boolean showIndicatorInfo = false;
/*     */   private Map<Enum<?>, ParamInfo> paramsMap;
/*     */   private transient Map<String, List<IndicatorOutpInfo>> indicatorValuesMap;
/*  98 */   private Map<String, Boolean> indVisibilityMap = new HashMap();
/*     */ 
/* 100 */   private transient List<ValuePair> valuesToDisplay = new ArrayList();
/*     */   private transient List<ValuePair> userMessages;
/*     */ 
/*     */   public OhlcChartObject()
/*     */   {
/* 106 */     this((String)null);
/*     */   }
/*     */ 
/*     */   public OhlcChartObject(String key) {
/* 110 */     super(key, IChart.Type.OHLC_INFORMER);
/*     */ 
/* 112 */     init();
/*     */   }
/*     */ 
/*     */   public OhlcChartObject(OhlcChartObject chartObject) {
/* 116 */     super(chartObject);
/*     */ 
/* 118 */     this.dataType = chartObject.dataType;
/* 119 */     this.paramsMap = chartObject.paramsMap;
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/* 124 */     setColor(ThemeManager.getTheme().getColor(ITheme.ChartElement.OHLC));
/* 125 */     setFillColor(ThemeManager.getTheme().getColor(ITheme.ChartElement.OHLC_BACKGROUND));
/*     */ 
/* 127 */     setFillOpacity(0.0F);
/* 128 */     setPreferredSize(DEFAULT_SIZE);
/*     */   }
/*     */ 
/*     */   private Map<Enum<?>, ParamInfo> getParams() {
/* 132 */     if (null == this.paramsMap) {
/* 133 */       this.paramsMap = new HashMap();
/*     */ 
/* 135 */       this.paramsMap.put(IOhlcChartObject.CandleInfoParams.TIME, new ParamInfo(null, "ohlc.v.time", "menu.item.ohlc.time", null));
/* 136 */       this.paramsMap.put(IOhlcChartObject.CandleInfoParams.DATE, new ParamInfo(null, "ohlc.v.date", "menu.item.ohlc.date", null));
/* 137 */       this.paramsMap.put(IOhlcChartObject.CandleInfoParams.OPEN, new ParamInfo("ohlc.h.open", "ohlc.v.open", "menu.item.ohlc.open", null));
/* 138 */       this.paramsMap.put(IOhlcChartObject.CandleInfoParams.HIGH, new ParamInfo("ohlc.h.high", "ohlc.v.high", "menu.item.ohlc.high", null));
/* 139 */       this.paramsMap.put(IOhlcChartObject.CandleInfoParams.LOW, new ParamInfo("ohlc.h.low", "ohlc.v.low", "menu.item.ohlc.low", null));
/* 140 */       this.paramsMap.put(IOhlcChartObject.CandleInfoParams.CLOSE, new ParamInfo("ohlc.h.close", "ohlc.v.close", "menu.item.ohlc.close", null));
/* 141 */       this.paramsMap.put(IOhlcChartObject.CandleInfoParams.VOL, new ParamInfo("ohlc.h.volume", "ohlc.v.volume", "menu.item.ohlc.volume", null));
/* 142 */       this.paramsMap.put(IOhlcChartObject.CandleInfoParams.INDEX, new ParamInfo("ohlc.h.index", "ohlc.v.index", "menu.item.ohlc.index", null));
/*     */ 
/* 144 */       this.paramsMap.put(IOhlcChartObject.TickInfoParams.TIME, new ParamInfo(null, "ohlc.v.time", "menu.item.ohlc.time", null));
/* 145 */       this.paramsMap.put(IOhlcChartObject.TickInfoParams.DATE, new ParamInfo(null, "ohlc.v.date", "menu.item.ohlc.date", null));
/* 146 */       this.paramsMap.put(IOhlcChartObject.TickInfoParams.ASK, new ParamInfo("ohlc.h.ask", "ohlc.v.ask", "menu.item.ohlc.ask", null));
/* 147 */       this.paramsMap.put(IOhlcChartObject.TickInfoParams.BID, new ParamInfo("ohlc.h.bid", "ohlc.v.bid", "menu.item.ohlc.bid", null));
/* 148 */       this.paramsMap.put(IOhlcChartObject.TickInfoParams.ASK_VOL, new ParamInfo("ohlc.h.ask.vol", "ohlc.v.ask.vol", "menu.item.ohlc.ask.volume", null));
/* 149 */       this.paramsMap.put(IOhlcChartObject.TickInfoParams.BID_VOL, new ParamInfo("ohlc.h.bid.vol", "ohlc.v.bid.vol", "menu.item.ohlc.bid.volume", null));
/*     */ 
/* 151 */       this.paramsMap.put(IOhlcChartObject.PriceAgregatedInfoParams.START_TIME, new ParamInfo("ohlc.h.start.time", "ohlc.v.start.time", "menu.item.ohlc.start.time", null));
/* 152 */       this.paramsMap.put(IOhlcChartObject.PriceAgregatedInfoParams.END_TIME, new ParamInfo("ohlc.h.end.time", "ohlc.v.end.time", "menu.item.ohlc.end.time", null));
/* 153 */       this.paramsMap.put(IOhlcChartObject.PriceAgregatedInfoParams.DATE, new ParamInfo("ohlc.h.date", "ohlc.v.date", "menu.item.ohlc.date", null));
/* 154 */       this.paramsMap.put(IOhlcChartObject.PriceAgregatedInfoParams.OPEN, new ParamInfo("ohlc.h.open", "ohlc.v.open", "menu.item.ohlc.open", null));
/* 155 */       this.paramsMap.put(IOhlcChartObject.PriceAgregatedInfoParams.HIGH, new ParamInfo("ohlc.h.high", "ohlc.v.high", "menu.item.ohlc.high", null));
/* 156 */       this.paramsMap.put(IOhlcChartObject.PriceAgregatedInfoParams.LOW, new ParamInfo("ohlc.h.low", "ohlc.v.low", "menu.item.ohlc.low", null));
/* 157 */       this.paramsMap.put(IOhlcChartObject.PriceAgregatedInfoParams.CLOSE, new ParamInfo("ohlc.h.close", "ohlc.v.close", "menu.item.ohlc.close", null));
/* 158 */       this.paramsMap.put(IOhlcChartObject.PriceAgregatedInfoParams.VOL, new ParamInfo("ohlc.h.volume", "ohlc.v.volume", "menu.item.ohlc.volume", null));
/* 159 */       this.paramsMap.put(IOhlcChartObject.PriceAgregatedInfoParams.INDEX, new ParamInfo("ohlc.h.index", "ohlc.v.index", "menu.item.ohlc.index", null));
/*     */     }
/* 161 */     return this.paramsMap;
/*     */   }
/*     */ 
/*     */   private Map<String, List<IndicatorOutpInfo>> getIndicatorValuesMap() {
/* 165 */     if (null == this.indicatorValuesMap) {
/* 166 */       this.indicatorValuesMap = new HashMap();
/*     */     }
/* 168 */     return this.indicatorValuesMap;
/*     */   }
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 173 */     return this.dataType;
/*     */   }
/*     */   public void setDataType(DataType dataType) {
/* 176 */     this.dataType = dataType;
/*     */   }
/*     */ 
/*     */   public String getMenuLocalizationKey(Enum param)
/*     */   {
/* 185 */     if (((param instanceof IOhlcChartObject.CandleInfoParams)) || ((param instanceof IOhlcChartObject.TickInfoParams)) || ((param instanceof IOhlcChartObject.PriceAgregatedInfoParams))) {
/* 186 */       return ((ParamInfo)getParams().get(param)).menuLocalizationKey;
/*     */     }
/* 188 */     throw new RuntimeException("Unsupported param type");
/*     */   }
/*     */ 
/*     */   public boolean isSticky()
/*     */   {
/* 196 */     return false;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 201 */     return 0;
/*     */   }
/*     */ 
/*     */   public void evaluateContent(Graphics g, Dimension d, int maxWidth, int maxHeight)
/*     */   {
/* 206 */     if (g == null) {
/* 207 */       return;
/*     */     }
/*     */ 
/* 210 */     IOhlcChartObject.OhlcAlignment alignment = getAlignment();
/* 211 */     if (IOhlcChartObject.OhlcAlignment.AUTO.equals(alignment)) {
/* 212 */       alignment = d.width / d.height > 4 ? IOhlcChartObject.OhlcAlignment.HORIZONTAL : IOhlcChartObject.OhlcAlignment.VERTICAL;
/*     */     }
/*     */ 
/* 215 */     analyzeMessages(alignment);
/*     */ 
/* 217 */     boolean updateSize = false;
/* 218 */     if (d.width > maxWidth) {
/* 219 */       resetFontSize();
/* 220 */       d.width = maxWidth;
/* 221 */       updateSize = true;
/*     */     }
/* 223 */     if (d.height > maxHeight) {
/* 224 */       resetFontSize();
/* 225 */       d.height = maxHeight;
/* 226 */       updateSize = true;
/*     */     }
/*     */ 
/* 229 */     if (this.fontSize < 8)
/*     */     {
/* 231 */       evaluateFontSize(g, this.valuesToDisplay, alignment, d);
/*     */     }
/*     */ 
/* 234 */     evaluateDimension(g, this.valuesToDisplay, alignment, d, updateSize);
/*     */   }
/*     */ 
/*     */   public void paintContent(Graphics g) {
/* 238 */     IOhlcChartObject.OhlcAlignment alignment = getAlignment();
/* 239 */     if (IOhlcChartObject.OhlcAlignment.AUTO.equals(alignment)) {
/* 240 */       alignment = getSize().width / getSize().height > 4 ? IOhlcChartObject.OhlcAlignment.HORIZONTAL : IOhlcChartObject.OhlcAlignment.VERTICAL;
/*     */     }
/*     */ 
/* 243 */     drawValues((Graphics2D)g, this.valuesToDisplay, alignment);
/*     */   }
/*     */ 
/*     */   private void analyzeMessages(IOhlcChartObject.OhlcAlignment alignment) {
/* 247 */     getValuesToDisplay().clear();
/*     */ 
/* 250 */     if ((this.dataType == null) || (this.formattersManager == null)) {
/* 251 */       return;
/*     */     }
/*     */ 
/* 254 */     Enum[] keys = getAllInfoParamsByDataType(this.dataType);
/* 255 */     if (keys == null) {
/* 256 */       throw new IllegalStateException("Unknown DataType");
/*     */     }
/*     */ 
/* 261 */     for (int i = 0; i < keys.length; i++) {
/* 262 */       if (getParamVisibility(keys[i])) {
/* 263 */         String label = null;
/* 264 */         if (IOhlcChartObject.OhlcAlignment.HORIZONTAL.equals(alignment))
/* 265 */           label = LocalizationManager.getText(((ParamInfo)getParams().get(keys[i])).getHLabel());
/* 266 */         else if (null != ((ParamInfo)getParams().get(keys[i])).getVLabel()) {
/* 267 */           label = LocalizationManager.getText(((ParamInfo)getParams().get(keys[i])).getVLabel());
/*     */         }
/* 269 */         Object value = getParamValue(keys[i]);
/* 270 */         if (null == value)
/*     */           continue;
/*     */         String formattedValue;
/*     */         String formattedValue;
/* 272 */         if ((keys[i].equals(IOhlcChartObject.TickInfoParams.ASK_VOL)) || (keys[i].equals(IOhlcChartObject.TickInfoParams.BID_VOL)) || (keys[i].equals(IOhlcChartObject.CandleInfoParams.VOL)) || (keys[i].equals(IOhlcChartObject.PriceAgregatedInfoParams.VOL)))
/*     */         {
/* 278 */           formattedValue = this.formattersManager.getValueFormatter().formatVolume(((Double)getParamValue(keys[i])).doubleValue());
/*     */         }
/*     */         else
/*     */         {
/*     */           String formattedValue;
/* 280 */           if ((getParamValue(keys[i]) instanceof Double))
/* 281 */             formattedValue = this.formattersManager.getValueFormatter().formatPrice(((Double)getParamValue(keys[i])).doubleValue());
/*     */           else
/* 283 */             formattedValue = getParamValue(keys[i]).toString();
/*     */         }
/* 285 */         this.valuesToDisplay.add(new ValuePair(label, formattedValue, getColor(), null));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 291 */     for (Map.Entry entry : getIndicatorValuesMap().entrySet()) {
/* 292 */       if ((!getShowIndicatorInfo()) && (Boolean.FALSE.equals(this.indVisibilityMap.get(entry.getKey())))) {
/*     */         continue;
/*     */       }
/* 295 */       ValuePair indName = new ValuePair((String)entry.getKey(), null, getColor(), null);
/* 296 */       indName.setHeader(true);
/* 297 */       this.valuesToDisplay.add(indName);
/* 298 */       for (IndicatorOutpInfo outp : (List)entry.getValue()) {
/* 299 */         if (null != outp.getLabel())
/*     */         {
/*     */           String valueStr;
/*     */           String valueStr;
/* 301 */           if (null == outp.getValue()) {
/* 302 */             valueStr = "";
/*     */           }
/*     */           else
/*     */           {
/*     */             String valueStr;
/* 303 */             if ((outp.getValue() instanceof Integer))
/* 304 */               valueStr = outp.getValue().toString();
/*     */             else
/* 306 */               valueStr = this.formattersManager.getValueFormatter().formatPrice(((Double)outp.getValue()).doubleValue());
/*     */           }
/* 308 */           Color outpColor = outp.getColor() == null ? getColor() : outp.getColor();
/* 309 */           this.valuesToDisplay.add(new ValuePair(outp.getLabel(), valueStr, outpColor, null));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 314 */     if ((null != this.userMessages) && (!this.userMessages.isEmpty()))
/* 315 */       this.valuesToDisplay.addAll(this.userMessages);
/*     */   }
/*     */ 
/*     */   private void drawValues(Graphics2D g, List<ValuePair> values, IOhlcChartObject.OhlcAlignment alignment)
/*     */   {
/* 320 */     Font originalFont = getFont();
/* 321 */     Font font = new Font(getFont().getFontName(), getFont().getStyle(), this.fontSize);
/* 322 */     g.setFont(font);
/* 323 */     FontMetrics metrics = g.getFontMetrics(font);
/* 324 */     int lineHeight = metrics.getHeight();
/*     */ 
/* 326 */     int leftX = 2;
/* 327 */     int upperY = 0;
/*     */     int posX;
/*     */     int rightX;
/*     */     int y;
/*     */     Font boldFont;
/* 330 */     if (IOhlcChartObject.OhlcAlignment.HORIZONTAL.equals(alignment)) {
/* 331 */       posX = leftX + 3;
/* 332 */       for (ValuePair value : values) {
/* 333 */         if (value.isHeader()) {
/*     */           continue;
/*     */         }
/* 336 */         g.setColor(value.getColor());
/* 337 */         StringBuilder pairBuilder = new StringBuilder();
/* 338 */         if (null != value.getLabel()) {
/* 339 */           pairBuilder.append(value.getLabel());
/* 340 */           if (null != value.getValue()) {
/* 341 */             pairBuilder.append(": ");
/*     */           }
/*     */         }
/* 344 */         if (null != value.getValue()) {
/* 345 */           pairBuilder.append(value.getValue());
/*     */         }
/* 347 */         String str = pairBuilder.toString();
/* 348 */         g.drawString(str, posX, upperY + 3 + this.fontSize);
/* 349 */         posX += metrics.stringWidth(str) + 6;
/*     */       }
/*     */     } else {
/* 352 */       rightX = getSize().width - 3;
/* 353 */       y = upperY + 3 + this.fontSize;
/* 354 */       boldFont = new Font(originalFont.getFontName(), 1, this.fontSize);
/*     */ 
/* 356 */       for (ValuePair value : values) {
/* 357 */         g.setColor(value.getColor());
/* 358 */         if (value.isBold()) {
/* 359 */           g.setFont(boldFont);
/*     */         }
/* 361 */         if (0 == value.getTextAlignment()) {
/* 362 */           String label = value.getLabel();
/* 363 */           int labelWidth = metrics.stringWidth(label);
/* 364 */           int x = (getSize().width - labelWidth) / 2;
/* 365 */           g.drawString(label, x, y);
/* 366 */         } else if (4 == value.getTextAlignment()) {
/* 367 */           String label = value.getLabel();
/* 368 */           int labelWidth = metrics.stringWidth(label);
/* 369 */           g.drawString(label, rightX - 3 - labelWidth, y);
/*     */         } else {
/* 371 */           if (null != value.getLabel()) {
/* 372 */             g.drawString(value.getLabel(), leftX + 3, y);
/*     */           }
/* 374 */           if (null != value.getValue()) {
/* 375 */             int valueWidth = metrics.stringWidth(value.getValue());
/* 376 */             g.drawString(value.getValue(), rightX - 3 - valueWidth, y);
/*     */           }
/*     */         }
/* 379 */         if (value.isBold()) {
/* 380 */           g.setFont(font);
/*     */         }
/*     */ 
/* 383 */         y += lineHeight;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 388 */     g.setColor(getColor());
/* 389 */     g.setFont(originalFont);
/*     */   }
/*     */ 
/*     */   private void evaluateDimension(Graphics g, List<ValuePair> valuesToDisplay, IOhlcChartObject.OhlcAlignment alignment, Dimension d, boolean updateSize)
/*     */   {
/* 399 */     if (valuesToDisplay.isEmpty()) {
/* 400 */       return;
/*     */     }
/* 402 */     Font currFont = new Font(getFont().getFontName(), getFont().getStyle(), this.fontSize);
/* 403 */     FontMetrics metrics = g.getFontMetrics(currFont);
/* 404 */     int lineHeight = metrics.getHeight();
/*     */ 
/* 406 */     boolean needApplyNewSize = (!d.equals(getSize())) || (updateSize);
/*     */ 
/* 408 */     int newWidth = d.width;
/* 409 */     int newHeight = d.height;
/*     */ 
/* 411 */     int currContentHeight = isHeaderVisible() ? d.height - getChartWidgetPanel().getHeaderHeight() : d.height;
/*     */ 
/* 414 */     if (IOhlcChartObject.OhlcAlignment.HORIZONTAL.equals(alignment)) {
/* 415 */       if (currContentHeight != lineHeight + 6) {
/* 416 */         newHeight = lineHeight + 6;
/* 417 */         if (isHeaderVisible()) {
/* 418 */           newHeight += getChartWidgetPanel().getHeaderHeight();
/*     */         }
/* 420 */         needApplyNewSize = true;
/*     */       }
/*     */ 
/* 423 */       StringBuilder lineBuilder = new StringBuilder();
/* 424 */       int spacing = -6;
/* 425 */       for (ValuePair valuePair : valuesToDisplay) {
/* 426 */         if (valuePair.isHeader()) {
/*     */           continue;
/*     */         }
/* 429 */         if (null != valuePair.getLabel()) {
/* 430 */           lineBuilder.append(valuePair.getLabel());
/* 431 */           lineBuilder.append(": ");
/*     */         }
/* 433 */         if (null != valuePair.getValue()) {
/* 434 */           lineBuilder.append(valuePair.getValue());
/*     */         }
/* 436 */         spacing += 6;
/*     */       }
/* 438 */       int strWidth = metrics.stringWidth(lineBuilder.toString()) + spacing;
/* 439 */       if (d.width < strWidth + 6) {
/* 440 */         newWidth = strWidth + 6;
/* 441 */         needApplyNewSize = true;
/*     */       }
/*     */     }
/*     */     else {
/* 445 */       if (currContentHeight != valuesToDisplay.size() * lineHeight + 6) {
/* 446 */         newHeight = valuesToDisplay.size() * lineHeight + 6;
/* 447 */         if (isHeaderVisible()) {
/* 448 */           newHeight += getChartWidgetPanel().getHeaderHeight();
/*     */         }
/* 450 */         needApplyNewSize = true;
/*     */       }
/*     */ 
/* 453 */       int maxLineWidth = 0;
/* 454 */       for (ValuePair valuePair : valuesToDisplay) {
/* 455 */         int lineWidth = metrics.stringWidth(new StringBuilder().append(valuePair.getLabel()).append(valuePair.getValue()).toString()) + 3;
/* 456 */         if (lineWidth > maxLineWidth) {
/* 457 */           maxLineWidth = lineWidth;
/*     */         }
/*     */       }
/* 460 */       if (d.width < maxLineWidth + 6) {
/* 461 */         newWidth = maxLineWidth + 6;
/* 462 */         needApplyNewSize = true;
/*     */       }
/*     */     }
/*     */ 
/* 466 */     if (needApplyNewSize)
/* 467 */       super.setPreferredSize(new Dimension(newWidth, newHeight));
/*     */   }
/*     */ 
/*     */   private void evaluateFontSize(Graphics g, List<ValuePair> valuesToDisplay, IOhlcChartObject.OhlcAlignment alignment, Dimension d)
/*     */   {
/* 478 */     if (valuesToDisplay.isEmpty()) {
/* 479 */       return;
/*     */     }
/*     */ 
/* 482 */     Font originalFont = getFont();
/*     */ 
/* 484 */     int width = d.width;
/* 485 */     int height = isHeaderVisible() ? d.height - getChartWidgetPanel().getHeaderHeight() : d.height;
/*     */ 
/* 488 */     for (int fontSize = 40; fontSize >= 8; fontSize--) {
/* 489 */       Font currFont = new Font(originalFont.getFontName(), originalFont.getStyle(), fontSize);
/* 490 */       FontMetrics metrics = g.getFontMetrics(currFont);
/* 491 */       int lineHeight = metrics.getHeight();
/*     */ 
/* 493 */       if (IOhlcChartObject.OhlcAlignment.HORIZONTAL.equals(alignment)) {
/* 494 */         if ((height < lineHeight + 6) && (fontSize != 8))
/*     */         {
/*     */           continue;
/*     */         }
/* 498 */         StringBuilder lineBuilder = new StringBuilder();
/* 499 */         int spacing = -6;
/* 500 */         for (ValuePair valuePair : valuesToDisplay) {
/* 501 */           if (valuePair.isHeader()) {
/*     */             continue;
/*     */           }
/* 504 */           if (null != valuePair.getLabel()) {
/* 505 */             lineBuilder.append(valuePair.getLabel());
/* 506 */             lineBuilder.append(": ");
/*     */           }
/* 508 */           if (null != valuePair.getValue()) {
/* 509 */             lineBuilder.append(valuePair.getValue());
/*     */           }
/* 511 */           spacing += 6;
/*     */         }
/* 513 */         int strWidth = metrics.stringWidth(lineBuilder.toString()) + spacing;
/* 514 */         if ((width < strWidth + 6) && (fontSize != 8))
/*     */           continue;
/*     */       }
/*     */       else
/*     */       {
/* 519 */         if ((height < valuesToDisplay.size() * lineHeight + 6) && (fontSize != 8))
/*     */         {
/*     */           continue;
/*     */         }
/* 523 */         int maxWidth = 0;
/* 524 */         for (ValuePair valuePair : valuesToDisplay) {
/* 525 */           int lineWidth = metrics.stringWidth(new StringBuilder().append(valuePair.getLabel()).append(valuePair.getValue()).toString()) + 3;
/* 526 */           if (lineWidth > maxWidth) {
/* 527 */             maxWidth = lineWidth;
/*     */           }
/*     */         }
/* 530 */         if ((width < maxWidth + 6) && (fontSize != 8))
/*     */         {
/*     */           continue;
/*     */         }
/*     */       }
/*     */ 
/* 536 */       this.fontSize = fontSize;
/* 537 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   private List<ValuePair> getValuesToDisplay()
/*     */   {
/* 543 */     if (this.valuesToDisplay == null) {
/* 544 */       this.valuesToDisplay = new ArrayList();
/*     */     }
/* 546 */     return this.valuesToDisplay;
/*     */   }
/*     */ 
/*     */   public ChartObject clone()
/*     */   {
/* 552 */     return new OhlcChartObject(this);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 557 */     return "OHLC Informer";
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 562 */     return "item.ohlc.informer";
/*     */   }
/*     */ 
/*     */   public void setPreferredSize(Dimension dimension)
/*     */   {
/* 572 */     super.setPreferredSize(dimension);
/*     */ 
/* 574 */     resetFontSize();
/*     */   }
/*     */ 
/*     */   public void resetFontSize() {
/* 578 */     this.fontSize = -1;
/*     */   }
/*     */ 
/*     */   public IOhlcChartObject.OhlcAlignment getAlignment() {
/* 582 */     return this.alignment;
/*     */   }
/*     */ 
/*     */   public void setAlignment(IOhlcChartObject.OhlcAlignment alignment) {
/* 586 */     if (this.alignment == alignment) {
/* 587 */       return;
/*     */     }
/* 589 */     IOhlcChartObject.OhlcAlignment old = this.alignment;
/* 590 */     this.alignment = alignment;
/* 591 */     firePropertyChange("ohlc.alignment", old, alignment);
/*     */   }
/*     */ 
/*     */   public boolean getShowIndicatorInfo() {
/* 595 */     return this.showIndicatorInfo;
/*     */   }
/*     */ 
/*     */   public void setShowIndicatorInfo(boolean showIndicatorInfo) {
/* 599 */     if (this.showIndicatorInfo == showIndicatorInfo) {
/* 600 */       return;
/*     */     }
/* 602 */     Boolean old = Boolean.valueOf(this.showIndicatorInfo);
/* 603 */     this.showIndicatorInfo = showIndicatorInfo;
/* 604 */     firePropertyChange("ohlc.show.indicator.info", old, Boolean.valueOf(showIndicatorInfo));
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> boolean getParamVisibility(Enum<E> param)
/*     */   {
/* 609 */     return ((ParamInfo)getParams().get(param)).isVisible();
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> void setParamVisibility(Enum<E> param, boolean visible) {
/* 613 */     Boolean old = Boolean.valueOf(((ParamInfo)getParams().get(param)).isVisible());
/* 614 */     ((ParamInfo)getParams().get(param)).setVisible(visible);
/* 615 */     Enum[] linkedKeys = (Enum[])linkedParams.get(param);
/* 616 */     if (linkedKeys != null) {
/* 617 */       for (Enum key : linkedKeys) {
/* 618 */         ((ParamInfo)getParams().get(key)).setVisible(visible);
/*     */       }
/*     */     }
/* 621 */     firePropertyChange("ohlc.param.visibility", old, Boolean.valueOf(visible));
/*     */   }
/*     */ 
/*     */   public String getParamMenuLocalizationKey(Enum<?> param) {
/* 625 */     return ((ParamInfo)getParams().get(param)).getMenuLocalizationKey();
/*     */   }
/*     */ 
/*     */   public Object getParamValue(Enum<?> param) {
/* 629 */     return ((ParamInfo)getParams().get(param)).getValue();
/*     */   }
/*     */ 
/*     */   public void setParamValue(Enum<?> param, Object value) {
/* 633 */     ((ParamInfo)getParams().get(param)).setValue(value);
/*     */   }
/*     */ 
/*     */   public void clearIndicatorMap() {
/* 637 */     getIndicatorValuesMap().clear();
/*     */   }
/*     */ 
/*     */   public Map<String, Boolean> getIndVisibilityMap()
/*     */   {
/* 642 */     return this.indVisibilityMap;
/*     */   }
/*     */ 
/*     */   public void addIndicatorValue(String indicatorName, String label, Object value, Color color)
/*     */   {
/* 647 */     List list = (List)getIndicatorValuesMap().get(indicatorName);
/* 648 */     if (null == list) {
/* 649 */       list = new ArrayList();
/* 650 */       getIndicatorValuesMap().put(indicatorName, list);
/* 651 */       if (!this.indVisibilityMap.containsKey(indicatorName)) {
/* 652 */         this.indVisibilityMap.put(indicatorName, Boolean.FALSE);
/*     */       }
/*     */     }
/* 655 */     list.add(new IndicatorOutpInfo(label, value, color, null));
/*     */   }
/*     */ 
/*     */   public void cleanIndicatorVisibilityMap() {
/* 659 */     List keysToRemove = null;
/* 660 */     for (String key : this.indVisibilityMap.keySet()) {
/* 661 */       if (!getIndicatorValuesMap().containsKey(key)) {
/* 662 */         if (null == keysToRemove) {
/* 663 */           keysToRemove = new ArrayList();
/*     */         }
/* 665 */         keysToRemove.add(key);
/*     */       }
/*     */     }
/* 668 */     if (null != keysToRemove)
/* 669 */       for (String key : keysToRemove)
/* 670 */         this.indVisibilityMap.remove(key);
/*     */   }
/*     */ 
/*     */   private List<ValuePair> getUserMessages()
/*     */   {
/* 676 */     if (null == this.userMessages) {
/* 677 */       this.userMessages = new ArrayList();
/*     */     }
/* 679 */     return this.userMessages;
/*     */   }
/*     */ 
/*     */   public void clearUserMessages()
/*     */   {
/* 684 */     getUserMessages().clear();
/*     */   }
/*     */ 
/*     */   public void addUserMessage(String label, String value, Color color)
/*     */   {
/* 689 */     if (null == color) {
/* 690 */       color = getColor();
/*     */     }
/* 692 */     if (null == label) {
/* 693 */       throw new IllegalArgumentException("Label cannot be null");
/*     */     }
/* 695 */     ValuePair valuePair = new ValuePair(label, value, color, null);
/* 696 */     List messages = getUserMessages();
/* 697 */     messages.add(valuePair);
/*     */   }
/*     */ 
/*     */   public void addUserMessage(String message, Color color, int textAlignment, boolean bold)
/*     */   {
/* 702 */     if (null == color) {
/* 703 */       color = getColor();
/*     */     }
/* 705 */     if (null == message) {
/* 706 */       throw new IllegalArgumentException("Message cannot be null");
/*     */     }
/* 708 */     ValuePair valuePair = new ValuePair(message, null, color, null);
/* 709 */     valuePair.setTextAlignment(textAlignment);
/* 710 */     valuePair.setBold(bold);
/* 711 */     List messages = getUserMessages();
/* 712 */     messages.add(valuePair);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 847 */     in.defaultReadObject();
/*     */ 
/* 849 */     if (getParams().get(IOhlcChartObject.CandleInfoParams.DATE) == null) {
/* 850 */       getParams().put(IOhlcChartObject.TickInfoParams.DATE, new ParamInfo(null, "Date", "menu.item.ohlc.date", null));
/*     */ 
/* 852 */       ParamInfo oldTime = (ParamInfo)getParams().get(IOhlcChartObject.TickInfoParams.TIME);
/* 853 */       ParamInfo newTime = new ParamInfo(null, "Time", "menu.item.ohlc.time", null);
/* 854 */       newTime.setVisible(oldTime.isVisible());
/* 855 */       getParams().put(IOhlcChartObject.TickInfoParams.TIME, newTime);
/*     */ 
/* 857 */       getParams().put(IOhlcChartObject.CandleInfoParams.DATE, new ParamInfo(null, "Date", "menu.item.ohlc.date", null));
/*     */ 
/* 859 */       oldTime = (ParamInfo)getParams().get(IOhlcChartObject.CandleInfoParams.TIME);
/* 860 */       newTime = new ParamInfo(null, "Time", "menu.item.ohlc.time", null);
/* 861 */       newTime.setVisible(oldTime.isVisible());
/* 862 */       getParams().put(IOhlcChartObject.CandleInfoParams.TIME, newTime);
/*     */ 
/* 864 */       getParams().put(IOhlcChartObject.PriceAgregatedInfoParams.DATE, new ParamInfo("Date", "Date", "menu.item.ohlc.date", null));
/*     */     }
/*     */ 
/* 867 */     if (getSize().width == 0) {
/* 868 */       resetFontSize();
/* 869 */       setPosX(0.01F);
/* 870 */       setPosY(0.01F);
/* 871 */       setPreferredSize(DEFAULT_SIZE);
/* 872 */       setHeaderVisible(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Enum<?>[] getAllInfoParamsByDataType(DataType dataType)
/*     */   {
/* 878 */     return (Enum[])infoParamsByDataTypeMap.get(dataType);
/*     */   }
/*     */ 
/*     */   private class ValuePair
/*     */   {
/*     */     private String label;
/*     */     private String value;
/*     */     private Color color;
/*     */     private boolean header;
/*     */     private boolean bold;
/*     */     private int textAlignment;
/*     */ 
/*     */     private ValuePair(String label, String value, Color color)
/*     */     {
/* 798 */       this.header = false;
/* 799 */       this.bold = false;
/* 800 */       this.textAlignment = 2;
/* 801 */       this.label = label;
/* 802 */       this.value = value;
/* 803 */       this.color = color;
/*     */     }
/*     */ 
/*     */     boolean isHeader() {
/* 807 */       return this.header;
/*     */     }
/*     */     void setHeader(boolean header) {
/* 810 */       this.header = header;
/* 811 */       if (header) {
/* 812 */         this.bold = true;
/* 813 */         this.textAlignment = 0;
/*     */       }
/*     */     }
/*     */ 
/*     */     boolean isBold() {
/* 817 */       return this.bold;
/*     */     }
/*     */     void setBold(boolean bold) {
/* 820 */       this.bold = bold;
/*     */     }
/*     */     int getTextAlignment() {
/* 823 */       return this.textAlignment;
/*     */     }
/*     */     void setTextAlignment(int textAlignment) {
/* 826 */       this.textAlignment = textAlignment;
/*     */     }
/*     */ 
/*     */     String getLabel() {
/* 830 */       return this.label;
/*     */     }
/*     */     String getValue() {
/* 833 */       return this.value;
/*     */     }
/*     */     Color getColor() {
/* 836 */       return this.color;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class IndicatorOutpInfo
/*     */   {
/*     */     private String label;
/*     */     private Object value;
/*     */     private Color color;
/*     */ 
/*     */     private IndicatorOutpInfo(String label, Object value, Color color)
/*     */     {
/* 766 */       this.label = label;
/* 767 */       this.value = value;
/* 768 */       this.color = color;
/*     */     }
/*     */ 
/*     */     String getLabel() {
/* 772 */       return this.label;
/*     */     }
/*     */     Object getValue() {
/* 775 */       return this.value;
/*     */     }
/*     */     Color getColor() {
/* 778 */       return this.color;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ParamInfo
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     private boolean visible;
/*     */     private String hLabel;
/*     */     private String vLabel;
/*     */     private String menuLocalizationKey;
/*     */     private Object value;
/*     */ 
/*     */     private ParamInfo(String hLabel, String vLabel, String menuLocalizationKey)
/*     */     {
/* 730 */       this.visible = true;
/* 731 */       this.hLabel = hLabel;
/* 732 */       this.vLabel = vLabel;
/* 733 */       this.menuLocalizationKey = menuLocalizationKey;
/*     */     }
/*     */ 
/*     */     boolean isVisible() {
/* 737 */       return this.visible;
/*     */     }
/*     */     void setVisible(boolean visible) {
/* 740 */       this.visible = visible;
/*     */     }
/*     */     String getHLabel() {
/* 743 */       return this.hLabel;
/*     */     }
/*     */     String getVLabel() {
/* 746 */       return this.vLabel;
/*     */     }
/*     */     String getMenuLocalizationKey() {
/* 749 */       return this.menuLocalizationKey;
/*     */     }
/*     */     Object getValue() {
/* 752 */       return this.value;
/*     */     }
/*     */     void setValue(Object value) {
/* 755 */       this.value = value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.OhlcChartObject
 * JD-Core Version:    0.6.0
 */