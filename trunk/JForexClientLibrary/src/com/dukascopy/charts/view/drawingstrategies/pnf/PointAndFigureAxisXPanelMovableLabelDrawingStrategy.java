/*    */ package com.dukascopy.charts.view.drawingstrategies.pnf;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*    */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*    */ import com.dukascopy.charts.view.drawingstrategies.common.CommonAxisXPanelMovableLabelDrawingStrategy;
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Point;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class PointAndFigureAxisXPanelMovableLabelDrawingStrategy extends CommonAxisXPanelMovableLabelDrawingStrategy<PointAndFigureDataSequence, PointAndFigureData>
/*    */ {
/*    */   protected PointAndFigureAxisXPanelMovableLabelDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper)
/*    */   {
/* 34 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper);
/*    */   }
/*    */ 
/*    */   protected int getPreferredHeight()
/*    */   {
/* 45 */     return 24;
/*    */   }
/*    */ 
/*    */   public void draw(Graphics g, JComponent jComponent)
/*    */   {
/* 50 */     if (this.chartState.isMouseCursorOnWindow(-2)) {
/* 51 */       return;
/*    */     }
/*    */ 
/* 54 */     super.draw(g, jComponent);
/*    */ 
/* 57 */     int curMouseX = this.chartState.getMouseCursorPoint().x;
/*    */ 
/* 59 */     String formattedMessage = getFormattedMessage(curMouseX);
/* 60 */     String boxNumberMessage = getBoxNumberMessage(curMouseX);
/*    */ 
/* 62 */     if (formattedMessage.isEmpty()) {
/* 63 */       return;
/*    */     }
/*    */ 
/* 66 */     int formattedMessageWidth = getStringWidth(g, formattedMessage);
/*    */ 
/* 68 */     Color color = g.getColor();
/* 69 */     Font font = g.getFont();
/*    */ 
/* 71 */     g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*    */ 
/* 73 */     drawMovableLabelBackground((Graphics2D)g, jComponent, curMouseX, formattedMessageWidth, -1, getHeight(jComponent));
/* 74 */     drawMovableLabelText(g, curMouseX, boxNumberMessage, formattedMessageWidth, jComponent.getHeight() / 2 - 2);
/*    */ 
/* 76 */     g.setColor(color);
/* 77 */     g.setFont(font);
/*    */   }
/*    */ 
/*    */   private String getBoxNumberMessage(int mouseX) {
/* 81 */     long time = this.timeToXMapper.tx(mouseX);
/* 82 */     int timeIndex = ((PointAndFigureDataSequence)this.dataSequenceProvider.getDataSequence()).indexOf(time);
/* 83 */     return String.valueOf(timeIndex + 1);
/*    */   }
/*    */ 
/*    */   protected int getY(JComponent jComponent)
/*    */   {
/* 89 */     return jComponent.getHeight() / 2;
/*    */   }
/*    */ 
/*    */   protected int getHeight(JComponent jComponent)
/*    */   {
/* 94 */     return jComponent.getHeight() / 2;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.PointAndFigureAxisXPanelMovableLabelDrawingStrategy
 * JD-Core Version:    0.6.0
 */