/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*    */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.geom.GeneralPath;
/*    */ import java.awt.geom.Rectangle2D.Float;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class SubChartPanelMetaInfoDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/* 25 */   final GeneralPath path = new GeneralPath();
/*    */   final SubIndicatorGroup subIndicatorGroup;
/*    */ 
/*    */   SubChartPanelMetaInfoDisplayableDataPart(SubIndicatorGroup subIndicatorGroup, ChartState chartState)
/*    */   {
/* 32 */     super(LoggerFactory.getLogger(SubChartPanelMetaInfoDisplayableDataPart.class.getName()), chartState);
/* 33 */     this.subIndicatorGroup = subIndicatorGroup;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent)
/*    */   {
/* 38 */     Color color = g.getColor();
/* 39 */     Font font = g.getFont();
/*    */ 
/* 41 */     g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*    */ 
/* 43 */     this.path.reset();
/* 44 */     drawClosingCross(g);
/* 45 */     drawFrame(g, jComponent);
/*    */ 
/* 47 */     g.setFont(font);
/* 48 */     g.setColor(color);
/*    */   }
/*    */ 
/*    */   void drawClosingCross(Graphics g) {
/* 52 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.OUTLINE));
/* 53 */     this.path.append(new Rectangle2D.Float(5.0F, 5.0F, 10.0F, 10.0F), false);
/*    */ 
/* 55 */     this.path.moveTo(7.0F, 7.0F);
/* 56 */     this.path.lineTo(13.0F, 13.0F);
/* 57 */     this.path.moveTo(13.0F, 7.0F);
/* 58 */     this.path.lineTo(7.0F, 13.0F);
/*    */ 
/* 60 */     ((Graphics2D)g).draw(this.path);
/*    */   }
/*    */ 
/*    */   void drawFrame(Graphics g, JComponent jComponent) {
/* 64 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.OUTLINE));
/* 65 */     this.path.reset();
/*    */ 
/* 67 */     this.path.moveTo(0.0F, 0.0F);
/* 68 */     this.path.lineTo(0.0F, jComponent.getHeight() - 1);
/* 69 */     this.path.lineTo(jComponent.getWidth() - 1, jComponent.getHeight() - 1);
/* 70 */     this.path.lineTo(jComponent.getWidth() - 1, 0.0F);
/*    */ 
/* 72 */     ((Graphics2D)g).draw(this.path);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.SubChartPanelMetaInfoDisplayableDataPart
 * JD-Core Version:    0.6.0
 */