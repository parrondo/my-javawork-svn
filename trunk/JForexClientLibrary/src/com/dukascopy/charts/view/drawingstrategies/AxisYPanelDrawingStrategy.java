/*    */ package com.dukascopy.charts.view.drawingstrategies;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*    */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*    */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*    */ import java.awt.Color;
/*    */ import java.awt.Container;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Font;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.LayoutManager;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public abstract class AxisYPanelDrawingStrategy
/*    */   implements IDrawingStrategy
/*    */ {
/*    */   protected final ChartState chartState;
/*    */ 
/*    */   public AxisYPanelDrawingStrategy(ChartState chartState)
/*    */   {
/* 22 */     this.chartState = chartState;
/*    */   }
/*    */ 
/*    */   public final void draw(Graphics g, JComponent jComponent)
/*    */   {
/* 27 */     Font font = g.getFont();
/* 28 */     Color color = g.getColor();
/*    */ 
/* 30 */     Font axisFont = this.chartState.getTheme().getFont(ITheme.TextElement.AXIS);
/*    */ 
/* 32 */     int width = getComponentWidth(g, axisFont);
/* 33 */     setupWidth(jComponent, width);
/*    */ 
/* 35 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_PANEL_BACKGROUND));
/* 36 */     g.fillRect(0, 0, width, jComponent.getHeight());
/*    */ 
/* 38 */     g.setFont(axisFont);
/* 39 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_PANEL_FOREGROUND));
/*    */ 
/* 41 */     drawLabelsAndLines(g, jComponent);
/*    */ 
/* 43 */     g.setColor(color);
/* 44 */     g.setFont(font);
/*    */   }
/*    */ 
/*    */   protected void setupWidth(JComponent jComponent, int width) {
/* 48 */     if (jComponent.getWidth() != width) {
/* 49 */       jComponent.setPreferredSize(new Dimension(width, jComponent.getHeight()));
/* 50 */       jComponent.getParent().getLayout().layoutContainer(jComponent.getParent().getParent());
/*    */     }
/*    */   }
/*    */ 
/*    */   protected int getComponentWidth(Graphics g, Font axisFont) {
/* 55 */     return g.getFontMetrics(axisFont).stringWidth("12345.67890");
/*    */   }
/*    */   protected abstract void drawLabelsAndLines(Graphics paramGraphics, JComponent paramJComponent);
/*    */ 
/*    */   protected final void drawLabel(Graphics g, JComponent jComponent, String labelText, int y) {
/* 61 */     int width = jComponent.getWidth();
/* 62 */     FontMetrics fontMetrics = g.getFontMetrics();
/* 63 */     int fontHeight = fontMetrics.getHeight();
/* 64 */     int labelWidth = fontMetrics.stringWidth(labelText);
/*    */ 
/* 66 */     g.drawString(labelText, getPriceLabelX(width, labelWidth), y + fontHeight / 2 - 2);
/*    */   }
/*    */ 
/*    */   protected int getPriceLabelX(int componentWidth, int labelWidth)
/*    */   {
/* 74 */     return componentWidth / 2 - labelWidth / 2;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.AxisYPanelDrawingStrategy
 * JD-Core Version:    0.6.0
 */