/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.ChartObjectEvent;
/*     */ import com.dukascopy.api.ChartObjectListener;
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import com.dukascopy.charts.listeners.drawing.DrawingActionListener;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class MainDrawingsManagerImpl extends DrawingsManagerImpl
/*     */   implements IMainDrawingsManager, IMainDrawingsContainer
/*     */ {
/*     */   private static final int COMMENTS_HORIZONTAL_GAP = 5;
/*     */   private static final int COMMENTS_VERTICAL_GAP = 2;
/*  40 */   private String[] comments = null;
/*  41 */   private int commentHorizontalPosition = 2;
/*  42 */   private int commentVerticalPosition = 1;
/*  43 */   private Font commentFont = null;
/*  44 */   private Color commentColor = null;
/*     */ 
/*     */   public MainDrawingsManagerImpl(IMapper mapper, FormattersManager formattersManager, GuiRefresher guiRefresher, DrawingsFactory drawingsFactory, DrawingsLabelHelper drawingsLabelHelper, DrawingActionListener drawingActionListener, ChartsActionListenerRegistry chartsActionListenerRegistry, ChartState chartState, Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders, GeometryCalculator geometryCalculator)
/*     */   {
/*  58 */     super(mapper, formattersManager, guiRefresher, drawingsFactory, drawingsLabelHelper, drawingActionListener, chartsActionListenerRegistry, chartState, allDataSequenceProviders, geometryCalculator);
/*     */   }
/*     */ 
/*     */   public boolean intersectsGlobalDrawing(Point point)
/*     */   {
/*  74 */     ChartObject chartObject = findIntersectionObject(point);
/*  75 */     return (chartObject != null) && (chartObject.isGlobal());
/*     */   }
/*     */ 
/*     */   public boolean intersectsGlobalDrawingToBeEdited(Point point)
/*     */   {
/*  80 */     return (intersectsDrawingToBeEdited(point)) && (this.editedChartObject.isGlobal());
/*     */   }
/*     */ 
/*     */   public boolean isEditingGlobalDrawing()
/*     */   {
/*  85 */     return (isEditingDrawing()) && (this.editedChartObject.isGlobal());
/*     */   }
/*     */ 
/*     */   public boolean triggerHighlighting(Point point)
/*     */   {
/*  90 */     boolean isDrawingHighlighted = false;
/*  91 */     synchronized (this.commonChartObjects) {
/*  92 */       isDrawingHighlighted = triggerHighlightingOfDrawings(point, this.commonChartObjects.values());
/*     */     }
/*  94 */     return isDrawingHighlighted;
/*     */   }
/*     */ 
/*     */   public boolean triggerGlobalHighlighting(Point point)
/*     */   {
/*  99 */     List globalObjects = new LinkedList();
/* 100 */     synchronized (this.commonChartObjects) {
/* 101 */       for (IChartObject element : this.commonChartObjects.values()) {
/* 102 */         if (((ChartObject)element).isGlobal()) {
/* 103 */           globalObjects.add(element);
/*     */         }
/*     */       }
/*     */     }
/* 107 */     return triggerHighlightingOfDrawings(point, globalObjects);
/*     */   }
/*     */ 
/*     */   public void unselectDrawingToBeEdited()
/*     */   {
/* 112 */     if (this.editedChartObject == null) {
/* 113 */       return;
/*     */     }
/*     */ 
/* 116 */     this.editedChartObject.setHighlighted(false);
/* 117 */     this.editedChartObject.setUnderEdit(false);
/*     */ 
/* 119 */     this.editedChartObject.getChartObjectListener().deselected(new ChartObjectEvent(this.editedChartObject));
/* 120 */     this.chartsActionListenerRegistry.drawingChanged(this.editedChartObject);
/*     */ 
/* 122 */     this.editedChartObject = null;
/* 123 */     this.highlightedObject = null;
/*     */   }
/*     */ 
/*     */   protected void selectDrawing(ChartObject chartObject)
/*     */   {
/* 128 */     if (chartObject == null) {
/* 129 */       return;
/*     */     }
/*     */ 
/* 132 */     ChartObjectEvent event = new ChartObjectEvent(chartObject);
/* 133 */     chartObject.getChartObjectListener().selected(event);
/* 134 */     if (event.isCanceled()) {
/* 135 */       return;
/*     */     }
/*     */ 
/* 138 */     this.highlightedObject = null;
/* 139 */     this.editedChartObject = chartObject;
/* 140 */     this.editedChartObject.setUnderEdit(true);
/*     */ 
/* 142 */     this.chartsActionListenerRegistry.drawingChanged(this.editedChartObject);
/*     */ 
/* 144 */     this.drawingActionListener.drawingEditingStarted();
/* 145 */     this.guiRefresher.refreshMainContent();
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1, long time2, double price2, long time3, double price3)
/*     */   {
/* 152 */     IChartObject drawing = this.drawingsFactory.createDrawing(key, type, time1, price1, time2, price2, time3, price3);
/* 153 */     if (drawing == null) {
/* 154 */       return null;
/*     */     }
/* 156 */     addChartObject(drawing);
/* 157 */     return drawing;
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1, long time2, double price2)
/*     */   {
/* 162 */     IChartObject drawing = this.drawingsFactory.createDrawing(key, type, time1, price1, time2, price2);
/* 163 */     if (drawing == null) {
/* 164 */       return null;
/*     */     }
/* 166 */     addChartObject(drawing);
/* 167 */     return drawing;
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1)
/*     */   {
/* 172 */     IChartObject drawing = this.drawingsFactory.createDrawing(key, type, time1, price1);
/* 173 */     if (drawing == null) {
/* 174 */       return null;
/*     */     }
/* 176 */     addChartObject(drawing);
/* 177 */     return drawing;
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1, long time2, double price2, long time3, double price3)
/*     */   {
/* 182 */     IChartObject drawing = this.drawingsFactory.createDrawing(key, type, time1, price1, time2, price2, time3, price3);
/* 183 */     if (drawing == null) {
/* 184 */       return null;
/*     */     }
/* 186 */     addChartObject(drawing);
/* 187 */     return drawing;
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1, long time2, double price2)
/*     */   {
/* 192 */     IChartObject drawing = this.drawingsFactory.createDrawing(key, type, time1, price1, time2, price2);
/* 193 */     if (drawing == null) {
/* 194 */       return null;
/*     */     }
/* 196 */     addChartObject(drawing);
/* 197 */     return drawing;
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1)
/*     */   {
/* 202 */     IChartObject drawing = this.drawingsFactory.createDrawing(key, type, time1, price1);
/* 203 */     if (drawing == null) {
/* 204 */       return null;
/*     */     }
/* 206 */     addChartObject(drawing);
/* 207 */     return drawing;
/*     */   }
/*     */ 
/*     */   public IChartObject get(String key)
/*     */   {
/* 212 */     IChartObject chartObject = (IChartObject)this.commonChartObjects.get(key);
/* 213 */     if (chartObject != null)
/* 214 */       ((ChartObject)chartObject).setActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/* 216 */           MainDrawingsManagerImpl.this.guiRefresher.refreshMainContent();
/*     */         }
/*     */       });
/* 220 */     return chartObject;
/*     */   }
/*     */ 
/*     */   public IChartObject remove(String key)
/*     */   {
/* 225 */     IChartObject objectToBeRemoved = get(key);
/* 226 */     if (objectToBeRemoved != null) {
/* 227 */       if (objectToBeRemoved == this.editedChartObject) {
/* 228 */         this.editedChartObject = null;
/* 229 */         this.highlightedObject = null;
/*     */       }
/* 231 */       return performRemove(objectToBeRemoved);
/*     */     }
/* 233 */     return null;
/*     */   }
/*     */ 
/*     */   public List<IChartObject> removeChartObjectsByKeys(List<String> keys) {
/* 237 */     if (keys == null) {
/* 238 */       return null;
/*     */     }
/*     */ 
/* 241 */     List chartObjects = new ArrayList();
/* 242 */     for (String key : keys) {
/* 243 */       IChartObject objectToBeRemoved = get(key);
/* 244 */       if (objectToBeRemoved != null) {
/* 245 */         chartObjects.add(objectToBeRemoved);
/* 246 */         if (objectToBeRemoved == this.editedChartObject) {
/* 247 */           this.editedChartObject = null;
/* 248 */           this.highlightedObject = null;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 253 */     return performRemove(chartObjects);
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getAll()
/*     */   {
/* 260 */     List result = new ArrayList();
/* 261 */     result.addAll(this.commonChartObjects.values());
/* 262 */     return result;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 267 */     return this.commonChartObjects.size();
/*     */   }
/*     */ 
/*     */   public Iterator<IChartObject> iterator()
/*     */   {
/* 276 */     return getAll().iterator();
/*     */   }
/*     */ 
/*     */   public void addComment(String comment)
/*     */   {
/* 281 */     if (comment != null)
/* 282 */       this.comments = comment.split("\n");
/*     */     else
/* 284 */       this.comments = null;
/*     */   }
/*     */ 
/*     */   public void setCommentColor(Color color)
/*     */   {
/* 290 */     this.commentColor = color;
/*     */   }
/*     */ 
/*     */   public Color getCommentColor()
/*     */   {
/* 295 */     if (this.commentColor != null) {
/* 296 */       return this.commentColor;
/*     */     }
/* 298 */     return this.chartState.getTheme().getColor(ITheme.ChartElement.DRAWING);
/*     */   }
/*     */ 
/*     */   public void setCommentFont(Font font)
/*     */   {
/* 303 */     this.commentFont = font;
/*     */   }
/*     */ 
/*     */   public Font getCommentFont()
/*     */   {
/* 308 */     if (this.commentFont != null) {
/* 309 */       return this.commentFont;
/*     */     }
/* 311 */     return this.chartState.getTheme().getFont(ITheme.TextElement.DEFAULT);
/*     */   }
/*     */ 
/*     */   public void setCommentHorizontalPosition(int position)
/*     */   {
/* 316 */     if ((position == 2) || (position == 0) || (position == 4))
/* 317 */       this.commentHorizontalPosition = position;
/*     */     else
/* 319 */       throw new IllegalArgumentException("Illegal comment horizontal position : " + position);
/*     */   }
/*     */ 
/*     */   public int getCommentHorizontalPosition()
/*     */   {
/* 325 */     return this.commentHorizontalPosition;
/*     */   }
/*     */ 
/*     */   public void setCommentVerticalPosition(int position)
/*     */   {
/* 330 */     if ((position == 1) || (position == 0) || (position == 3))
/* 331 */       this.commentVerticalPosition = position;
/*     */     else
/* 333 */       throw new IllegalArgumentException("Illegal comment vertical position : " + position);
/*     */   }
/*     */ 
/*     */   public int getCommentVerticalPosition()
/*     */   {
/* 339 */     return this.commentVerticalPosition;
/*     */   }
/*     */ 
/*     */   public void drawComment(Graphics g)
/*     */   {
/* 346 */     if (this.comments == null) {
/* 347 */       return;
/*     */     }
/*     */ 
/* 350 */     Font prevFont = g.getFont();
/* 351 */     Color prevColor = g.getColor();
/*     */ 
/* 353 */     g.setFont(getCommentFont());
/* 354 */     g.setColor(getCommentColor());
/* 355 */     FontMetrics fontMetrics = g.getFontMetrics();
/*     */ 
/* 357 */     int x = 0;
/* 358 */     switch (getCommentHorizontalPosition()) {
/*     */     case 2:
/* 360 */       x = 5;
/*     */     }
/*     */ 
/* 364 */     int y = 0;
/* 365 */     switch (getCommentVerticalPosition()) {
/*     */     case 1:
/* 367 */       y = fontMetrics.getHeight() + 2;
/* 368 */       break;
/*     */     case 0:
/* 370 */       y = this.mapper.getHeight() / 2 - (fontMetrics.getHeight() + 2) * this.comments.length / 2;
/* 371 */       break;
/*     */     case 3:
/* 373 */       y = this.mapper.getHeight() - (fontMetrics.getHeight() + 2) * this.comments.length - 2;
/*     */     case 2:
/*     */     }
/*     */ 
/* 377 */     for (String comment : this.comments) {
/* 378 */       if (comment == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 382 */       switch (getCommentHorizontalPosition()) {
/*     */       case 0:
/* 384 */         x = this.mapper.getWidth() / 2 - fontMetrics.stringWidth(comment) / 2;
/* 385 */         break;
/*     */       case 4:
/* 387 */         x = this.mapper.getWidth() - fontMetrics.stringWidth(comment) - 5;
/*     */       }
/*     */ 
/* 391 */       y += fontMetrics.getHeight() + 2;
/* 392 */       g.drawString(comment, x, y);
/*     */     }
/*     */ 
/* 395 */     g.setColor(prevColor);
/* 396 */     g.setFont(prevFont);
/*     */   }
/*     */ 
/*     */   protected void refreshWindow()
/*     */   {
/* 401 */     this.guiRefresher.refreshMainContent();
/*     */   }
/*     */ 
/*     */   protected void repaintWindow()
/*     */   {
/* 406 */     this.guiRefresher.repaintMainContent();
/*     */   }
/*     */ 
/*     */   protected void drawingAdded(IChartObject chartObject)
/*     */   {
/* 411 */     this.chartsActionListenerRegistry.drawingAdded(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawingRemoved(IChartObject chartObject)
/*     */   {
/* 416 */     this.chartsActionListenerRegistry.drawingRemoved(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawingsRemoved(List<IChartObject> chartObjects)
/*     */   {
/* 421 */     this.chartsActionListenerRegistry.drawingsRemoved(chartObjects);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.MainDrawingsManagerImpl
 * JD-Core Version:    0.6.0
 */