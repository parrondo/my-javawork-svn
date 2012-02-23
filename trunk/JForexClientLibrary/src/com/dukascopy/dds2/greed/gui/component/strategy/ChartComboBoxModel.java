/*    */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*    */ 
/*    */ import com.dukascopy.api.IChart;
/*    */ import com.dukascopy.charts.main.nulls.NullIChart;
/*    */ import javax.swing.AbstractListModel;
/*    */ import javax.swing.ComboBoxModel;
/*    */ 
/*    */ public class ChartComboBoxModel extends AbstractListModel
/*    */   implements ComboBoxModel
/*    */ {
/* 11 */   private boolean hasNullIChartInList = true;
/*    */   private IChart selectedChart;
/*    */   private IChart[] charts;
/*    */ 
/*    */   public ChartComboBoxModel(IChart[] charts, boolean isNullIChart)
/*    */   {
/* 16 */     NullIChart emptyChart = null;
/* 17 */     int firstIChartIndex = 0;
/* 18 */     this.hasNullIChartInList = isNullIChart;
/* 19 */     if (isNullIChart) {
/* 20 */       emptyChart = new NullIChart();
/* 21 */       firstIChartIndex = 1;
/*    */     }
/* 23 */     this.charts = new IChart[charts.length + firstIChartIndex];
/*    */ 
/* 25 */     if (isNullIChart) {
/* 26 */       emptyChart.setDescription(" ");
/*    */ 
/* 28 */       this.charts[0] = emptyChart;
/*    */     }
/* 30 */     System.arraycopy(charts, 0, this.charts, firstIChartIndex, charts.length);
/*    */   }
/*    */ 
/*    */   public ChartComboBoxModel(IChart[] charts) {
/* 34 */     this(charts, true);
/*    */   }
/*    */ 
/*    */   public void setCharts(IChart[] charts) {
/* 38 */     if ((this.charts != null) && (this.charts.length > 0)) {
/* 39 */       fireIntervalRemoved(this, 0, this.charts.length - 1);
/*    */     }
/* 41 */     this.charts = charts;
/* 42 */     if (this.charts.length > 0)
/* 43 */       fireIntervalAdded(this, 0, charts.length - 1);
/*    */   }
/*    */ 
/*    */   public Object getChart(int index)
/*    */   {
/* 48 */     return this.charts[index];
/*    */   }
/*    */ 
/*    */   public Object getSelectedItem()
/*    */   {
/* 53 */     return this.selectedChart;
/*    */   }
/*    */ 
/*    */   public void setSelectedItem(Object anItem)
/*    */   {
/* 58 */     this.selectedChart = ((IChart)anItem);
/* 59 */     fireContentsChanged(this, -1, -1);
/*    */   }
/*    */ 
/*    */   public Object getElementAt(int index)
/*    */   {
/* 65 */     return this.charts[index];
/*    */   }
/*    */ 
/*    */   public int getSize()
/*    */   {
/* 70 */     return this.charts.length;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.ChartComboBoxModel
 * JD-Core Version:    0.6.0
 */