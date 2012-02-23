/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.view.staticdynamicdata.IDisplayableDataPart;
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ abstract class AbstractDisplayableDataPart
/*    */   implements IDisplayableDataPart
/*    */ {
/*    */   protected final Logger logger;
/*    */   protected final ChartState chartState;
/*    */ 
/*    */   protected AbstractDisplayableDataPart(Logger logger, ChartState chartState)
/*    */   {
/* 17 */     this.logger = logger;
/* 18 */     this.chartState = chartState;
/*    */   }
/*    */ 
/*    */   public final void draw(Graphics g, JComponent jComponent) {
/* 22 */     Font font = g.getFont();
/* 23 */     Color color = g.getColor();
/*    */     try
/*    */     {
/* 26 */       drawInternal(g, jComponent);
/*    */     } catch (Throwable exc) {
/* 28 */       this.logger.error("Exception ocurred while drawing!", exc);
/*    */     }
/*    */ 
/* 31 */     g.setFont(font);
/* 32 */     g.setColor(color);
/*    */   }
/*    */ 
/*    */   protected abstract void drawInternal(Graphics paramGraphics, JComponent paramJComponent);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.AbstractDisplayableDataPart
 * JD-Core Version:    0.6.0
 */