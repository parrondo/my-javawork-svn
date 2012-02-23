/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.InputParameterInfo;
/*     */ import com.dukascopy.api.indicators.InputParameterInfo.Type;
/*     */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorChangeListener;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorChangeListener.ParameterType;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class IndicatorParametersTableModel extends AbstractTableModel
/*     */ {
/*  29 */   private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorParametersTableModel.class);
/*     */   private final boolean isTicks;
/*     */   private final IndicatorChangeListener indicatorChangeListener;
/*     */   private IndicatorWrapper indicatorWrapper;
/*  35 */   private final List<Integer> inputIndexes = new ArrayList();
/*  36 */   private int optInputsCount = 0;
/*     */ 
/*     */   public IndicatorParametersTableModel(IndicatorChangeListener indicatorChangeListener, boolean isTicks) {
/*  39 */     this.indicatorChangeListener = indicatorChangeListener;
/*  40 */     this.isTicks = isTicks;
/*     */   }
/*     */ 
/*     */   public void setIndicator(IndicatorWrapper indicatorWrapper) {
/*  44 */     if (LOGGER.isDebugEnabled()) {
/*  45 */       LOGGER.debug("Set indicator : " + indicatorWrapper);
/*     */     }
/*     */ 
/*  48 */     this.indicatorWrapper = indicatorWrapper;
/*     */ 
/*  50 */     this.inputIndexes.clear();
/*     */ 
/*  52 */     if (indicatorWrapper == null) {
/*  53 */       this.optInputsCount = 0;
/*     */     } else {
/*  55 */       IndicatorInfo indicatorInfo = indicatorWrapper.getIndicator().getIndicatorInfo();
/*  56 */       for (int i = 0; i < indicatorInfo.getNumberOfInputs(); i++) {
/*  57 */         if (isInputEditable(i)) {
/*  58 */           this.inputIndexes.add(Integer.valueOf(i));
/*     */         }
/*     */       }
/*  61 */       this.optInputsCount = indicatorInfo.getNumberOfOptionalInputs();
/*     */     }
/*     */ 
/*  64 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/*  69 */     return 2;
/*     */   }
/*     */ 
/*     */   public String getColumnName(int column)
/*     */   {
/*  74 */     switch (column) { case 0:
/*  75 */       return LocalizationManager.getText("table.column.parameter");
/*     */     case 1:
/*  76 */       return LocalizationManager.getText("table.column.value"); }
/*  77 */     throw new IllegalArgumentException("Incorrect column index : " + column);
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/*  83 */     if (this.indicatorWrapper == null) {
/*  84 */       return 0;
/*     */     }
/*     */ 
/*  87 */     return this.inputIndexes.size() + this.optInputsCount;
/*     */   }
/*     */ 
/*     */   public void setValueAt(Object value, int rowIndex, int columnIndex)
/*     */   {
/*  92 */     if (LOGGER.isDebugEnabled()) {
/*  93 */       LOGGER.debug("Set [" + rowIndex + ":" + columnIndex + "] = [" + value + "]");
/*     */     }
/*     */ 
/*  96 */     if (rowIndex < 0) {
/*  97 */       return;
/*     */     }
/*  99 */     if (rowIndex < this.inputIndexes.size()) {
/* 100 */       int inputIndex = ((Integer)this.inputIndexes.get(rowIndex)).intValue();
/* 101 */       if (this.isTicks)
/* 102 */         this.indicatorWrapper.setOfferSideForTicks(inputIndex, (OfferSide)value);
/*     */       else {
/* 104 */         this.indicatorWrapper.setAppliedPriceForCandles(inputIndex, (IIndicators.AppliedPrice)value);
/*     */       }
/* 106 */       this.indicatorChangeListener.indicatorChanged(IndicatorChangeListener.ParameterType.INPUT);
/*     */     } else {
/* 108 */       int optInputIndex = rowIndex - this.inputIndexes.size();
/* 109 */       this.indicatorWrapper.setOptParam(optInputIndex, value);
/* 110 */       this.indicatorChangeListener.indicatorChanged(IndicatorChangeListener.ParameterType.OPTIONAL);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */   {
/* 116 */     return columnIndex == 1;
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/* 121 */     if (this.indicatorWrapper == null) {
/* 122 */       return null;
/*     */     }
/*     */ 
/* 125 */     if (rowIndex < this.inputIndexes.size()) {
/* 126 */       int inputIndex = ((Integer)this.inputIndexes.get(rowIndex)).intValue();
/* 127 */       InputParameterInfo inputParameterInfo = this.indicatorWrapper.getIndicator().getInputParameterInfo(inputIndex);
/*     */ 
/* 129 */       switch (columnIndex) {
/*     */       case 0:
/* 131 */         return inputParameterInfo.getName();
/*     */       case 1:
/* 133 */         if (this.isTicks) {
/* 134 */           return new IndicatorParameter(inputParameterInfo, this.indicatorWrapper.getOfferSidesForTicks()[inputIndex]);
/*     */         }
/* 136 */         return new IndicatorParameter(inputParameterInfo, this.indicatorWrapper.getAppliedPricesForCandles()[inputIndex]);
/*     */       }
/* 138 */       throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */     }
/*     */ 
/* 141 */     int optInputIndex = rowIndex - this.inputIndexes.size();
/* 142 */     OptInputParameterInfo optInputParameterInfo = this.indicatorWrapper.getIndicator().getOptInputParameterInfo(optInputIndex);
/*     */ 
/* 144 */     switch (columnIndex) {
/*     */     case 0:
/* 146 */       return optInputParameterInfo.getName();
/*     */     case 1:
/* 148 */       return new IndicatorParameter(optInputParameterInfo, this.indicatorWrapper.getOptParams()[optInputIndex]);
/* 149 */     }throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */   }
/*     */ 
/*     */   private boolean isInputEditable(int index)
/*     */   {
/* 162 */     return this.indicatorWrapper.getIndicator().getInputParameterInfo(index).getType() == InputParameterInfo.Type.DOUBLE;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorParametersTableModel
 * JD-Core Version:    0.6.0
 */