/*    */ package com.dukascopy.charts.view.drawingstrategies.main;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.settings.ChartSettings;
/*    */ import com.dukascopy.charts.settings.ChartSettings.GridType;
/*    */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*    */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*    */ import com.dukascopy.charts.view.drawingstrategies.AxisYPanelDrawingStrategy;
/*    */ import com.dukascopy.charts.view.drawingstrategies.util.GridUtil;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MainAxisYPanelGridDrawingStrategy extends AxisYPanelDrawingStrategy
/*    */ {
/* 22 */   private static final Logger LOGGER = LoggerFactory.getLogger(MainAxisYPanelGridDrawingStrategy.class);
/*    */   protected final ValueFormatter valueFormatter;
/*    */   protected final IValueToYMapper valueToYMapper;
/*    */ 
/*    */   public MainAxisYPanelGridDrawingStrategy(ChartState chartState, ValueFormatter valueFormatter, IValueToYMapper valueToYMapper)
/*    */   {
/* 32 */     super(chartState);
/*    */ 
/* 34 */     this.valueFormatter = valueFormatter;
/* 35 */     this.valueToYMapper = valueToYMapper;
/*    */   }
/*    */ 
/*    */   protected void drawLabelsAndLines(Graphics g, JComponent component)
/*    */   {
/* 40 */     int height = component.getHeight();
/* 41 */     ChartSettings.GridType gridType = (ChartSettings.GridType)ChartSettings.get(ChartSettings.Option.GRID);
/*    */ 
/* 43 */     if (gridType == ChartSettings.GridType.STATIC) {
/* 44 */       int gridSize = ((Integer)ChartSettings.get(ChartSettings.Option.GRID_SIZE)).intValue();
/* 45 */       for (int y = height - gridSize; y > 0; y -= gridSize) {
/* 46 */         double value = this.valueToYMapper.vy(y);
/* 47 */         drawLabel(g, component, this.valueFormatter.formatGridPrice(value), y);
/*    */       }
/*    */     } else {
/* 50 */       double gridSize = getGridSize();
/* 51 */       double max = this.valueToYMapper.vy(0);
/* 52 */       double min = this.valueToYMapper.vy(height);
/*    */ 
/* 54 */       if ((!GridUtil.isValid(min)) || (!GridUtil.isValid(max))) {
/* 55 */         return;
/*    */       }
/*    */ 
/* 58 */       Instrument instrument = this.chartState.getInstrument();
/* 59 */       double pipValue = instrument.getPipValue();
/* 60 */       int scale = GridUtil.calculateScale(this.valueToYMapper.getValuesInOnePixel(), instrument, gridSize);
/* 61 */       int iterations = 0;
/* 62 */       for (double value = GridUtil.calculateNearest(instrument, max, gridSize); value >= min; value -= pipValue * gridSize * scale) {
/* 63 */         int y = this.valueToYMapper.yv(value);
/*    */ 
/* 65 */         if ((y >= 0) && (y < height)) {
/* 66 */           g.drawLine(getPriceLineMarkerX(), y, getPriceLineMarkerX() + 5, y);
/* 67 */           drawLabel(g, component, this.valueFormatter.formatGridPrice(value), y);
/*    */         }
/*    */ 
/* 71 */         if (iterations++ > 1000) {
/* 72 */           LOGGER.debug("Lock detected");
/* 73 */           return;
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   protected double getGridSize() {
/* 80 */     return GridUtil.getGridSize(this.chartState);
/*    */   }
/*    */ 
/*    */   protected int getPriceLineMarkerX() {
/* 84 */     return 0;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.main.MainAxisYPanelGridDrawingStrategy
 * JD-Core Version:    0.6.0
 */