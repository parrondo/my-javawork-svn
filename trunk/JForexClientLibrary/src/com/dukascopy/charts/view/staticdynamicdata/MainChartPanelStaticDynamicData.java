/*     */ package com.dukascopy.charts.view.staticdynamicdata;
/*     */ 
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import java.awt.Graphics;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EnumMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ class MainChartPanelStaticDynamicData extends AbstractStaticDynamicData
/*     */ {
/*  18 */   final Map<ViewMode, List<IDisplayableDataPart.TYPE>> dynamicDisplayableDataPartKeys = new EnumMap(ViewMode.class);
/*     */   ViewMode curViewMode;
/*  21 */   IDisplayableDataPart.TYPE[] drawingSequence = new IDisplayableDataPart.TYPE[6];
/*     */ 
/*     */   public MainChartPanelStaticDynamicData() {
/*  24 */     initStaticKeys();
/*  25 */     initDynamicKeys();
/*     */   }
/*     */ 
/*     */   public void drawDynamicData(Graphics g, JComponent jComponent) {
/*  29 */     List displayableDataPartKeys = (List)this.dynamicDisplayableDataPartKeys.get(this.curViewMode);
/*  30 */     for (IDisplayableDataPart.TYPE key : displayableDataPartKeys)
/*  31 */       ((IDisplayableDataPart)this.displayableDataParts.get(key)).draw(g, jComponent);
/*     */   }
/*     */ 
/*     */   public void drawStaticData(Graphics g, JComponent jComponent)
/*     */   {
/*  40 */     updateDrawingSequence();
/*     */ 
/*  42 */     ((IDisplayableDataPart)this.displayableDataParts.get(IDisplayableDataPart.TYPE.BACKGROUND)).draw(g, jComponent);
/*  43 */     for (IDisplayableDataPart.TYPE key : this.drawingSequence)
/*  44 */       ((IDisplayableDataPart)this.displayableDataParts.get(key)).draw(g, jComponent);
/*     */   }
/*     */ 
/*     */   private void updateDrawingSequence()
/*     */   {
/*  49 */     this.drawingSequence[getOptionPosition(ChartSettings.Option.DRAWING_SEQUENCE_GRID)] = IDisplayableDataPart.TYPE.GRID;
/*  50 */     this.drawingSequence[getOptionPosition(ChartSettings.Option.DRAWING_SEQUENCE_PERIOD_SEPARATORS)] = IDisplayableDataPart.TYPE.PERIOD_SEPARATORS;
/*  51 */     this.drawingSequence[getOptionPosition(ChartSettings.Option.DRAWING_SEQUENCE_CANDLES)] = IDisplayableDataPart.TYPE.RAW_DATA;
/*  52 */     this.drawingSequence[getOptionPosition(ChartSettings.Option.DRAWING_SEQUENCE_INDICATORS)] = IDisplayableDataPart.TYPE.INDICATORS;
/*  53 */     this.drawingSequence[getOptionPosition(ChartSettings.Option.DRAWING_SEQUENCE_DRAWINGS)] = IDisplayableDataPart.TYPE.DRAWINGS;
/*  54 */     this.drawingSequence[getOptionPosition(ChartSettings.Option.DRAWING_SEQUENCE_ORDERS)] = IDisplayableDataPart.TYPE.ORDERS;
/*     */   }
/*     */ 
/*     */   private int getOptionPosition(ChartSettings.Option option) {
/*  58 */     return ((Integer)ChartSettings.get(option)).intValue();
/*     */   }
/*     */ 
/*     */   public void viewModeChanged(ViewMode newViewMode) {
/*  62 */     this.curViewMode = newViewMode;
/*     */   }
/*     */ 
/*     */   void initStaticKeys()
/*     */   {
/*     */   }
/*     */ 
/*     */   void initDynamicKeys()
/*     */   {
/*  74 */     List dynamicDrawingsFor_ALL_STATIC = new ArrayList();
/*  75 */     dynamicDrawingsFor_ALL_STATIC.add(IDisplayableDataPart.TYPE.MOUSE_CURSOR);
/*  76 */     dynamicDrawingsFor_ALL_STATIC.add(IDisplayableDataPart.TYPE.DRAWINGSHANDLERS);
/*  77 */     dynamicDrawingsFor_ALL_STATIC.add(IDisplayableDataPart.TYPE.DYNAMICDRAWINGS);
/*  78 */     dynamicDrawingsFor_ALL_STATIC.add(IDisplayableDataPart.TYPE.META_DRAWINGS);
/*  79 */     this.dynamicDisplayableDataPartKeys.put(ViewMode.ALL_STATIC, dynamicDrawingsFor_ALL_STATIC);
/*     */ 
/*  81 */     List dynamicDrawingsFor_DRAWING = new ArrayList();
/*  82 */     dynamicDrawingsFor_DRAWING.add(IDisplayableDataPart.TYPE.NEWDRAWINGS);
/*  83 */     dynamicDrawingsFor_DRAWING.add(IDisplayableDataPart.TYPE.DYNAMICDRAWINGS);
/*  84 */     this.dynamicDisplayableDataPartKeys.put(ViewMode.DRAWING, dynamicDrawingsFor_DRAWING);
/*     */ 
/*  86 */     List dynamicDrawingsFor_DRAWING_EDITING = new ArrayList();
/*  87 */     dynamicDrawingsFor_DRAWING_EDITING.add(IDisplayableDataPart.TYPE.EDITEDDRAWINGS);
/*  88 */     dynamicDrawingsFor_DRAWING_EDITING.add(IDisplayableDataPart.TYPE.DYNAMICDRAWINGS);
/*  89 */     this.dynamicDisplayableDataPartKeys.put(ViewMode.DRAWING_EDITING, dynamicDrawingsFor_DRAWING_EDITING);
/*     */ 
/*  91 */     List dynamicDrawingsFor_ORDER_EDITING = new ArrayList();
/*  92 */     dynamicDrawingsFor_ORDER_EDITING.add(IDisplayableDataPart.TYPE.SELECTEDORDERS);
/*  93 */     dynamicDrawingsFor_ORDER_EDITING.add(IDisplayableDataPart.TYPE.DYNAMICDRAWINGS);
/*  94 */     this.dynamicDisplayableDataPartKeys.put(ViewMode.ORDER_EDITING, dynamicDrawingsFor_ORDER_EDITING);
/*     */ 
/*  97 */     List dynamicDrawingsFor_META_DRAWINGS = new ArrayList();
/*  98 */     dynamicDrawingsFor_META_DRAWINGS.add(IDisplayableDataPart.TYPE.META_DRAWINGS);
/*  99 */     dynamicDrawingsFor_META_DRAWINGS.add(IDisplayableDataPart.TYPE.DYNAMICDRAWINGS);
/* 100 */     this.dynamicDisplayableDataPartKeys.put(ViewMode.META_DRAWINGS, dynamicDrawingsFor_META_DRAWINGS);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.staticdynamicdata.MainChartPanelStaticDynamicData
 * JD-Core Version:    0.6.0
 */