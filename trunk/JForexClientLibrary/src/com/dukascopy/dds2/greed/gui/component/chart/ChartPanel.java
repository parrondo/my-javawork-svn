/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar.ComboBoxType;
/*    */ import java.awt.BorderLayout;
/*    */ import javax.swing.JComboBox;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class ChartPanel extends TabsAndFramePanelWithToolBar
/*    */ {
/*    */   private Instrument instrument;
/*    */   private boolean shouldSaveSettings;
/*    */ 
/*    */   public ChartPanel(int chartPanelId, Instrument instrument, ChartToolBar toolBar, JPanel chartPanel, boolean shouldSaveSettings)
/*    */   {
/* 19 */     super(chartPanelId, toolBar, TabedPanelType.CHART);
/* 20 */     this.instrument = instrument;
/* 21 */     this.shouldSaveSettings = shouldSaveSettings;
/* 22 */     setLayout(new BorderLayout());
/* 23 */     add(chartPanel, "Center");
/*    */   }
/*    */ 
/*    */   public Instrument getInstrument() {
/* 27 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public void setInstrument(Instrument instrument) {
/* 31 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public ChartToolBar getToolBar() {
/* 35 */     return (ChartToolBar)this.toolBar;
/*    */   }
/*    */ 
/*    */   public boolean isShouldSaveSettings() {
/* 39 */     return this.shouldSaveSettings;
/*    */   }
/*    */ 
/*    */   public void changePeriod(JForexPeriod period) {
/* 43 */     JComboBox periodsComboBox = getToolBar().getComboBox(ChartToolBar.ComboBoxType.PERIODS);
/* 44 */     periodsComboBox.setSelectedItem(period);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 48 */     return "ChartPanel(" + getPanelId() + ", " + this.instrument + ")";
/*    */   }
/*    */ 
/*    */   public void changeInstrument(Instrument instrument) {
/* 52 */     getToolBar().changeInstrument(instrument);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ChartPanel
 * JD-Core Version:    0.6.0
 */