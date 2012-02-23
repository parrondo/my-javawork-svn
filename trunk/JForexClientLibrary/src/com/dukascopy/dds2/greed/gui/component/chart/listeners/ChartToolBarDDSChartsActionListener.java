/*    */ package com.dukascopy.dds2.greed.gui.component.chart.listeners;
/*    */ 
/*    */ import com.dukascopy.api.DataType.DataPresentationType;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.main.DDSChartsActionAdapter;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar.ButtonType;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar.ComboBoxType;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableButton;
/*    */ import javax.swing.JComboBox;
/*    */ 
/*    */ public class ChartToolBarDDSChartsActionListener extends DDSChartsActionAdapter
/*    */ {
/*    */   ChartToolBar chartToolBar;
/*    */ 
/*    */   public ChartToolBarDDSChartsActionListener(int chartPanelId, ChartToolBar chartToolBar)
/*    */   {
/* 15 */     this.chartToolBar = chartToolBar;
/*    */   }
/*    */ 
/*    */   public void timeFrameMoved(boolean isChartShiftActive)
/*    */   {
/* 20 */     this.chartToolBar.setEnabled(!isChartShiftActive, new ChartToolBar.ButtonType[] { ChartToolBar.ButtonType.AUTOSHIFT });
/*    */   }
/*    */ 
/*    */   public void mouseCursorVisibilityChanged(boolean visible)
/*    */   {
/* 25 */     this.chartToolBar.setCursorVisibility(visible);
/*    */   }
/*    */ 
/*    */   public void zoomOutEnabled(boolean enabled)
/*    */   {
/* 30 */     ((JResizableButton)this.chartToolBar.getButton(ChartToolBar.ButtonType.ZOOM_OUT)).setActive(enabled);
/*    */   }
/*    */ 
/*    */   public void zoomInEnabled(boolean enabled)
/*    */   {
/* 35 */     ((JResizableButton)this.chartToolBar.getButton(ChartToolBar.ButtonType.ZOOM_IN)).setActive(enabled);
/*    */   }
/*    */ 
/*    */   public void candleTypeChanged(DataType.DataPresentationType selectedCandleType)
/*    */   {
/* 40 */     this.chartToolBar.getComboBox(ChartToolBar.ComboBoxType.TIME_PERIOD_PRESENTATION_TYPE).setSelectedItem(selectedCandleType);
/*    */   }
/*    */ 
/*    */   public void tickTypeChanged(DataType.DataPresentationType selectedTickType)
/*    */   {
/* 45 */     this.chartToolBar.getComboBox(ChartToolBar.ComboBoxType.TICKS_PRESENTATION_TYPE).setSelectedItem(selectedTickType);
/*    */   }
/*    */ 
/*    */   public void jForexPeriodChanged(JForexPeriod jForexPeriod)
/*    */   {
/* 50 */     this.chartToolBar.getComboBox(ChartToolBar.ComboBoxType.PERIODS).setSelectedItem(jForexPeriod);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.listeners.ChartToolBarDDSChartsActionListener
 * JD-Core Version:    0.6.0
 */