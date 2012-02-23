/*     */ package com.dukascopy.charts.view.staticdynamicdata;
/*     */ 
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import java.awt.Graphics;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ class SubChartPanelStaticDynamicData extends AbstractStaticDynamicData
/*     */ {
/*  15 */   final Map<ViewMode, List<IDisplayableDataPart.TYPE>> dynamicDisplayableDataPartKeys = new EnumMap(ViewMode.class);
/*     */   ViewMode curViewMode;
/*  18 */   IDisplayableDataPart.TYPE[] drawingSequence = new IDisplayableDataPart.TYPE[6];
/*     */ 
/*     */   SubChartPanelStaticDynamicData() {
/*  21 */     initStaticKeys();
/*  22 */     initDynamicKeys();
/*     */   }
/*     */ 
/*     */   public void drawDynamicData(Graphics g, JComponent jComponent) {
/*  26 */     List displayableDataPartKeys = (List)this.dynamicDisplayableDataPartKeys.get(this.curViewMode);
/*  27 */     for (IDisplayableDataPart.TYPE key : displayableDataPartKeys)
/*  28 */       ((IDisplayableDataPart)this.displayableDataParts.get(key)).draw(g, jComponent);
/*     */   }
/*     */ 
/*     */   public void drawStaticData(Graphics g, JComponent jComponent)
/*     */   {
/*  38 */     updateDrawingSequence();
/*     */ 
/*  40 */     ((IDisplayableDataPart)this.displayableDataParts.get(IDisplayableDataPart.TYPE.BACKGROUND)).draw(g, jComponent);
/*  41 */     for (IDisplayableDataPart.TYPE key : this.drawingSequence) {
/*  42 */       if (null != key) {
/*  43 */         ((IDisplayableDataPart)this.displayableDataParts.get(key)).draw(g, jComponent);
/*     */       }
/*     */     }
/*  46 */     ((IDisplayableDataPart)this.displayableDataParts.get(IDisplayableDataPart.TYPE.META_DRAWINGS)).draw(g, jComponent);
/*     */   }
/*     */ 
/*     */   private void updateDrawingSequence() {
/*  50 */     Arrays.fill(this.drawingSequence, null);
/*     */ 
/*  52 */     this.drawingSequence[getOptionPosition(ChartSettings.Option.DRAWING_SEQUENCE_GRID)] = IDisplayableDataPart.TYPE.GRID;
/*  53 */     this.drawingSequence[getOptionPosition(ChartSettings.Option.DRAWING_SEQUENCE_PERIOD_SEPARATORS)] = IDisplayableDataPart.TYPE.PERIOD_SEPARATORS;
/*  54 */     this.drawingSequence[getOptionPosition(ChartSettings.Option.DRAWING_SEQUENCE_INDICATORS)] = IDisplayableDataPart.TYPE.INDICATORS;
/*  55 */     this.drawingSequence[getOptionPosition(ChartSettings.Option.DRAWING_SEQUENCE_DRAWINGS)] = IDisplayableDataPart.TYPE.DRAWINGS;
/*     */   }
/*     */ 
/*     */   private int getOptionPosition(ChartSettings.Option option) {
/*  59 */     return ((Integer)ChartSettings.get(option)).intValue();
/*     */   }
/*     */ 
/*     */   public void viewModeChanged(ViewMode newViewMode) {
/*  63 */     this.curViewMode = newViewMode;
/*     */   }
/*     */ 
/*     */   void initStaticKeys()
/*     */   {
/*     */   }
/*     */ 
/*     */   void initDynamicKeys()
/*     */   {
/*  77 */     List dynamicDrawingKeysFor_ALL_STATIC = new ArrayList()
/*     */     {
/*     */     };
/*  82 */     this.dynamicDisplayableDataPartKeys.put(ViewMode.ALL_STATIC, dynamicDrawingKeysFor_ALL_STATIC);
/*     */ 
/*  84 */     List dynamicDrawingKeysFor_DRAWING = new ArrayList()
/*     */     {
/*     */     };
/*  88 */     this.dynamicDisplayableDataPartKeys.put(ViewMode.DRAWING, dynamicDrawingKeysFor_DRAWING);
/*     */ 
/*  90 */     List dynamicDrawingKeysFor_DRAWING_EDITING = new ArrayList()
/*     */     {
/*     */     };
/*  94 */     this.dynamicDisplayableDataPartKeys.put(ViewMode.DRAWING_EDITING, dynamicDrawingKeysFor_DRAWING_EDITING);
/*     */ 
/*  96 */     this.dynamicDisplayableDataPartKeys.put(ViewMode.ORDER_EDITING, Collections.emptyList());
/*     */ 
/*  98 */     List dynamicDrawingKeysFor_META_DRAWINGS = new ArrayList()
/*     */     {
/*     */     };
/* 101 */     this.dynamicDisplayableDataPartKeys.put(ViewMode.META_DRAWINGS, dynamicDrawingKeysFor_META_DRAWINGS);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.staticdynamicdata.SubChartPanelStaticDynamicData
 * JD-Core Version:    0.6.0
 */