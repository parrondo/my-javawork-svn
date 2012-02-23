/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*    */ import com.dukascopy.charts.persistence.ThemeManager;
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.RenderingHints;
/*    */ import java.awt.geom.Rectangle2D;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class BackGroundDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   BackGroundDisplayableDataPart(ChartState chartState)
/*    */   {
/* 24 */     super(LoggerFactory.getLogger(BackGroundDisplayableDataPart.class), chartState);
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent)
/*    */   {
/* 29 */     boolean drawWaterMark = false;
/*    */ 
/* 32 */     if ((jComponent.getName() != null) && (jComponent.getName().equals("MainChartPanel"))) {
/* 33 */       Object waterMarkSign = jComponent.getClientProperty("watermark");
/* 34 */       if ((waterMarkSign != null) && ((waterMarkSign instanceof Boolean)) && (((Boolean)waterMarkSign).booleanValue())) {
/* 35 */         drawWaterMark = true;
/*    */       }
/*    */     }
/*    */ 
/* 39 */     if (drawWaterMark) {
/* 40 */       Graphics2D g2d = (Graphics2D)g;
/* 41 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*    */ 
/* 44 */       String text = "";
/*    */       try {
/* 46 */         byte[] utf8 = { 68, 117, 107, 97, 115, 99, 111, 112, 121 };
/* 47 */         text = new String(utf8, "UTF-8");
/*    */       }
/*    */       catch (UnsupportedEncodingException e) {
/*    */       }
/* 51 */       Color prevColor = g.getColor();
/* 52 */       Font prevFont = g.getFont();
/*    */ 
/* 54 */       g.setColor(ThemeManager.getTheme().getColor(ITheme.ChartElement.BACKGROUND));
/* 55 */       g.fillRect(0, 0, jComponent.getWidth(), jComponent.getHeight());
/*    */ 
/* 57 */       int minFontSize = 20;
/* 58 */       int maxFontSize = 400;
/* 59 */       int curFontSize = prevFont.getSize();
/* 60 */       Rectangle rectangle = jComponent.getBounds();
/*    */ 
/* 63 */       while (maxFontSize - minFontSize > 2) {
/* 64 */         FontMetrics fm = g.getFontMetrics(new Font(prevFont.getName(), prevFont.getStyle(), curFontSize));
/* 65 */         int fontWidth = fm.stringWidth(text);
/* 66 */         int fontHeight = fm.getLeading() + fm.getMaxAscent() + fm.getMaxDescent();
/*    */ 
/* 68 */         if ((fontWidth > rectangle.width - 50) || (fontHeight > rectangle.height)) {
/* 69 */           maxFontSize = curFontSize;
/* 70 */           curFontSize = (maxFontSize + minFontSize) / 2;
/*    */         }
/*    */         else {
/* 73 */           minFontSize = curFontSize;
/* 74 */           curFontSize = (minFontSize + maxFontSize) / 2;
/*    */         }
/*    */       }
/*    */ 
/* 78 */       g.setColor(new Color(231, 231, 231));
/* 79 */       g.setFont(new Font(prevFont.getFontName(), prevFont.getStyle(), curFontSize));
/*    */ 
/* 81 */       Rectangle2D rectangle2D = g2d.getFontMetrics().getStringBounds(text, g);
/* 82 */       g2d.drawString(text, (int)((jComponent.getWidth() - rectangle2D.getWidth()) / 2.0D), (int)(jComponent.getHeight() / 2 + rectangle2D.getHeight() / 5.0D));
/*    */ 
/* 89 */       g.setColor(prevColor);
/* 90 */       g.setFont(prevFont);
/*    */     } else {
/* 92 */       Color prevColor = g.getColor();
/* 93 */       g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.BACKGROUND));
/* 94 */       g.fillRect(0, 0, jComponent.getWidth(), jComponent.getHeight());
/* 95 */       g.setColor(prevColor);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.BackGroundDisplayableDataPart
 * JD-Core Version:    0.6.0
 */