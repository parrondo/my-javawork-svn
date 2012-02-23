/*     */ package com.dukascopy.dds2.greed.gui.component.settings.period.panel;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import java.awt.Component;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ 
/*     */ public class CustomPeriodsRenderer extends DefaultListCellRenderer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final boolean markUsed;
/*     */ 
/*     */   public CustomPeriodsRenderer()
/*     */   {
/*  33 */     this(true);
/*     */   }
/*     */ 
/*     */   public CustomPeriodsRenderer(boolean markUsed)
/*     */   {
/*  43 */     this.markUsed = markUsed;
/*     */   }
/*     */ 
/*     */   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */   {
/*  55 */     Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*     */ 
/*  57 */     if ((value instanceof JForexPeriod)) {
/*  58 */       JForexPeriod dtpw = (JForexPeriod)value;
/*  59 */       JLabel label = getRenderedLabel(dtpw);
/*  60 */       label.setText(" " + label.getText());
/*  61 */       label.setOpaque(true);
/*  62 */       label.setForeground(comp.getForeground());
/*  63 */       label.setBackground(comp.getBackground());
/*  64 */       if (this.markUsed) {
/*  65 */         label.setEnabled(!getInUsePeriods().contains(dtpw));
/*     */       }
/*     */ 
/*  68 */       return label;
/*     */     }
/*     */ 
/*  71 */     return comp;
/*     */   }
/*     */ 
/*     */   public static List<JForexPeriod> getInUsePeriods() {
/*  75 */     List periods = new ArrayList();
/*     */ 
/*  77 */     for (Integer i : getChartsController().getChartControllerIdies()) {
/*  78 */       IChart chart = getChartsController().getIChartBy(i);
/*  79 */       DataType dataType = chart.getDataType();
/*  80 */       Period period = chart.getSelectedPeriod();
/*  81 */       PriceRange priceRange = chart.getPriceRange();
/*  82 */       ReversalAmount reversalAmount = chart.getReversalAmount();
/*  83 */       TickBarSize tickBarSize = chart.getTickBarSize();
/*  84 */       JForexPeriod wraper = new JForexPeriod(dataType, period, priceRange, reversalAmount, tickBarSize);
/*     */ 
/*  91 */       periods.add(wraper);
/*     */     }
/*     */ 
/*  94 */     return periods;
/*     */   }
/*     */ 
/*     */   protected static DDSChartsController getChartsController() {
/*  98 */     return (DDSChartsController)GreedContext.get("chartsController");
/*     */   }
/*     */ 
/*     */   private JLabel getRenderedLabel(JForexPeriod dtpw) {
/* 102 */     JLabel lbl = new JLabel();
/* 103 */     String text = ChartsLocalizator.localize(dtpw);
/* 104 */     lbl.setText(text);
/* 105 */     return lbl;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.period.panel.CustomPeriodsRenderer
 * JD-Core Version:    0.6.0
 */