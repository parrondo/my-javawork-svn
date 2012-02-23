/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorChangeListener;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorChangeListener.ParameterType;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Color;
/*     */ import java.util.Arrays;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class IndicatorOutputsTableModel extends AbstractTableModel
/*     */ {
/*  25 */   private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorOutputsTableModel.class);
/*     */ 
/*  27 */   public static final OutputParameterInfo.DrawingStyle[] LINES = { OutputParameterInfo.DrawingStyle.LINE, OutputParameterInfo.DrawingStyle.DOT_LINE, OutputParameterInfo.DrawingStyle.DASH_LINE, OutputParameterInfo.DrawingStyle.DASHDOT_LINE, OutputParameterInfo.DrawingStyle.DASHDOTDOT_LINE };
/*     */ 
/*  35 */   public static final OutputParameterInfo.DrawingStyle[] LEVELS = { OutputParameterInfo.DrawingStyle.LEVEL_LINE, OutputParameterInfo.DrawingStyle.LEVEL_DOT_LINE, OutputParameterInfo.DrawingStyle.LEVEL_DASH_LINE, OutputParameterInfo.DrawingStyle.LEVEL_DASHDOT_LINE, OutputParameterInfo.DrawingStyle.LEVEL_DASHDOTDOT_LINE };
/*     */   static final int COLUMN_SHOWOUTPUT_IDX = 0;
/*     */   static final int COLUMN_NAME_IDX = 1;
/*     */   static final int COLUMN_COLOR_IDX = 2;
/*     */   static final int COLUMN_COLOR2_IDX = 3;
/*     */   static final int COLUMN_STYLE_IDX = 4;
/*     */   static final int COLUMN_WIDTH_IDX = 5;
/*     */   static final int COLUMN_TRANSPARENCY_IDX = 6;
/*     */   static final int COLUMN_SHIFT_IDX = 7;
/*     */   static final int COLUMN_VALUE_ON_CHART_IDX = 8;
/*     */   private final IndicatorChangeListener indicatorChangeListener;
/*     */   private IndicatorWrapper indicatorWrapper;
/*  57 */   private boolean isIgnoreChanges = true;
/*     */ 
/*     */   public IndicatorOutputsTableModel(IndicatorChangeListener indicatorChangeListener) {
/*  60 */     this.indicatorChangeListener = indicatorChangeListener;
/*     */   }
/*     */ 
/*     */   public void setIndicator(IndicatorWrapper indicatorWrapper) {
/*  64 */     if (LOGGER.isDebugEnabled()) {
/*  65 */       LOGGER.debug("Set indicator : " + indicatorWrapper);
/*     */     }
/*     */ 
/*  68 */     this.isIgnoreChanges = true;
/*  69 */     this.indicatorWrapper = indicatorWrapper;
/*  70 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/*  75 */     return 9;
/*     */   }
/*     */ 
/*     */   public String getColumnName(int column)
/*     */   {
/*  82 */     switch (column) { case 0:
/*  83 */       return LocalizationManager.getText("table.column.show.output");
/*     */     case 1:
/*  84 */       return LocalizationManager.getText("table.column.name");
/*     */     case 2:
/*  85 */       return LocalizationManager.getText("table.column.color1");
/*     */     case 3:
/*  86 */       return LocalizationManager.getText("table.column.color2");
/*     */     case 4:
/*  87 */       return LocalizationManager.getText("table.column.style");
/*     */     case 5:
/*  88 */       return LocalizationManager.getText("table.column.width");
/*     */     case 6:
/*  89 */       return LocalizationManager.getText("table.column.transparency");
/*     */     case 8:
/*  90 */       return LocalizationManager.getText("table.column.show.value.on.chart.short");
/*     */     case 7:
/*  91 */       return LocalizationManager.getText("table.column.shift"); }
/*  92 */     throw new IllegalArgumentException("Incorrect column index : " + column);
/*     */   }
/*     */ 
/*     */   public Class<?> getColumnClass(int columnIndex)
/*     */   {
/*  98 */     switch (columnIndex) { case 0:
/*  99 */       return Boolean.class;
/*     */     case 1:
/* 100 */       return Object.class;
/*     */     case 2:
/* 101 */       return Object.class;
/*     */     case 3:
/* 102 */       return Object.class;
/*     */     case 4:
/* 103 */       return Object.class;
/*     */     case 5:
/* 104 */       return Object.class;
/*     */     case 6:
/* 105 */       return Float.class;
/*     */     case 8:
/* 106 */       return Boolean.class;
/*     */     case 7:
/* 107 */       return Object.class; }
/* 108 */     throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/* 114 */     if (this.indicatorWrapper == null) {
/* 115 */       return 0;
/*     */     }
/*     */ 
/* 118 */     return this.indicatorWrapper.getIndicator().getIndicatorInfo().getNumberOfOutputs();
/*     */   }
/*     */ 
/*     */   public void setValueAt(Object value, int rowIndex, int columnIndex)
/*     */   {
/* 123 */     if (LOGGER.isDebugEnabled()) {
/* 124 */       LOGGER.debug("Set [" + rowIndex + " : " + columnIndex + "] = " + value);
/*     */     }
/*     */ 
/* 127 */     if (value == null) {
/* 128 */       return;
/*     */     }
/*     */ 
/* 131 */     switch (columnIndex) {
/*     */     case 2:
/* 133 */       if (!(value instanceof Color)) break;
/* 134 */       this.indicatorWrapper.setOutputColor(rowIndex, (Color)value); break;
/*     */     case 3:
/* 138 */       if (!(value instanceof Color)) break;
/* 139 */       this.indicatorWrapper.setOutputColor2(rowIndex, (Color)value); break;
/*     */     case 4:
/* 143 */       if (!(value instanceof OutputParameterInfo.DrawingStyle)) break;
/* 144 */       this.indicatorWrapper.setDrawingStyle(rowIndex, (OutputParameterInfo.DrawingStyle)value); break;
/*     */     case 5:
/* 148 */       if (!(value instanceof Integer)) break;
/* 149 */       this.indicatorWrapper.setLineWidth(rowIndex, ((Integer)value).intValue()); break;
/*     */     case 8:
/* 153 */       if ((value instanceof Boolean))
/* 154 */         this.indicatorWrapper.setShowValueOnChart(rowIndex, ((Boolean)value).booleanValue());
/*     */       else {
/* 156 */         this.indicatorWrapper.setShowValueOnChart(rowIndex, false);
/*     */       }
/* 158 */       break;
/*     */     case 6:
/* 160 */       if (!(value instanceof Float)) break;
/* 161 */       this.indicatorWrapper.setOpacityAlpha(rowIndex, ((Float)value).floatValue());
/* 162 */       this.indicatorWrapper.getIndicator().getOutputParameterInfo(rowIndex).setOpacityAlpha(((Float)value).floatValue()); break;
/*     */     case 0:
/* 166 */       if ((value instanceof Boolean)) {
/* 167 */         this.indicatorWrapper.setShowOutput(rowIndex, ((Boolean)value).booleanValue());
/* 168 */         this.indicatorWrapper.getIndicator().getOutputParameterInfo(rowIndex).setShowOutput(((Boolean)value).booleanValue());
/*     */       } else {
/* 170 */         this.indicatorWrapper.setShowOutput(rowIndex, false);
/* 171 */         this.indicatorWrapper.getIndicator().getOutputParameterInfo(rowIndex).setShowOutput(false);
/*     */       }
/* 173 */       fireTableDataChanged();
/* 174 */       break;
/*     */     case 7:
/* 176 */       if (!(value instanceof Integer)) break;
/* 177 */       this.indicatorWrapper.setOutputShift(rowIndex, ((Integer)value).intValue());
/*     */     case 1:
/*     */     }
/*     */ 
/* 182 */     if ((!this.isIgnoreChanges) && (columnIndex == 7)) {
/* 183 */       this.indicatorChangeListener.indicatorChanged(null);
/*     */     }
/* 185 */     else if (!this.isIgnoreChanges)
/* 186 */       this.indicatorChangeListener.indicatorChanged(IndicatorChangeListener.ParameterType.OUTPUT);
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/* 192 */     if (this.indicatorWrapper == null) {
/* 193 */       return null;
/*     */     }
/*     */ 
/* 196 */     OutputParameterInfo info = this.indicatorWrapper.getIndicator().getOutputParameterInfo(rowIndex);
/*     */ 
/* 198 */     switch (columnIndex) { case 1:
/* 199 */       return info.getName();
/*     */     case 2:
/* 200 */       return this.indicatorWrapper.getOutputColors()[rowIndex];
/*     */     case 3:
/* 201 */       return this.indicatorWrapper.getOutputColors2()[rowIndex];
/*     */     case 4:
/* 202 */       return this.indicatorWrapper.getDrawingStyles()[rowIndex];
/*     */     case 5:
/* 203 */       return Integer.valueOf(this.indicatorWrapper.getLineWidths()[rowIndex]);
/*     */     case 6:
/* 204 */       return Float.valueOf(this.indicatorWrapper.getOpacityAlphas()[rowIndex]);
/*     */     case 8:
/* 206 */       if (isCellEditable(rowIndex, columnIndex)) {
/* 207 */         return Boolean.valueOf(this.indicatorWrapper.showValueOnChart(rowIndex));
/*     */       }
/* 209 */       return Boolean.valueOf(false);
/*     */     case 0:
/* 213 */       if (isCellEditable(rowIndex, columnIndex)) {
/* 214 */         return Boolean.valueOf(this.indicatorWrapper.showOutput(rowIndex));
/*     */       }
/* 216 */       return Boolean.valueOf(false);
/*     */     case 7:
/* 219 */       return Integer.valueOf(this.indicatorWrapper.getOutputShifts()[rowIndex]); }
/* 220 */     throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */   {
/* 226 */     switch (columnIndex) { case 1:
/* 227 */       return false;
/*     */     case 2:
/*     */     case 3:
/* 230 */       OutputParameterInfo.DrawingStyle drawingStyle = this.indicatorWrapper.getDrawingStyles()[rowIndex];
/*     */ 
/* 232 */       if (!isCellVisible(rowIndex)) {
/* 233 */         return false;
/*     */       }
/*     */ 
/* 236 */       if (drawingStyle == OutputParameterInfo.DrawingStyle.NONE) {
/* 237 */         return false;
/*     */       }
/*     */ 
/* 240 */       if ((drawingStyle == OutputParameterInfo.DrawingStyle.HISTOGRAM) && (this.indicatorWrapper.getIndicator().getOutputParameterInfo(rowIndex).isHistogramTwoColor())) {
/* 241 */         return true;
/*     */       }
/*     */ 
/* 246 */       return (columnIndex != 3) || (drawingStyle.isOutputAsLine()) || (drawingStyle == OutputParameterInfo.DrawingStyle.PATTERN_BULL_BEAR) || (drawingStyle == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH);
/*     */     case 7:
/* 252 */       OutputParameterInfo.DrawingStyle drawingStyle = this.indicatorWrapper.getDrawingStyles()[rowIndex];
/* 253 */       return (isCellVisible(rowIndex)) && (Arrays.binarySearch(LEVELS, drawingStyle) < 0);
/*     */     case 4:
/*     */     case 5:
/* 257 */       OutputParameterInfo.DrawingStyle drawingStyle = this.indicatorWrapper.getDrawingStyles()[rowIndex];
/*     */ 
/* 259 */       if (!isCellVisible(rowIndex)) {
/* 260 */         return false;
/*     */       }
/*     */ 
/* 263 */       return (Arrays.binarySearch(LINES, drawingStyle) >= 0) || (Arrays.binarySearch(LEVELS, drawingStyle) >= 0);
/*     */     case 8:
/* 271 */       return isCellVisible(rowIndex);
/*     */     case 6:
/* 277 */       return isCellVisible(rowIndex);
/*     */     case 0:
/* 280 */       return true;
/*     */     }
/* 282 */     return false;
/*     */   }
/*     */ 
/*     */   public void setIgnoreChanges(boolean value)
/*     */   {
/* 287 */     this.isIgnoreChanges = value;
/*     */   }
/*     */ 
/*     */   private boolean isCellVisible(int rowIndex) {
/* 291 */     return this.indicatorWrapper.getShowOutputs()[rowIndex];
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorOutputsTableModel
 * JD-Core Version:    0.6.0
 */