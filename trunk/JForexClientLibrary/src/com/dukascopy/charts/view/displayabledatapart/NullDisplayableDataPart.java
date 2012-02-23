/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.IDisplayableDataPartFactory.PART;
/*    */ import com.dukascopy.charts.view.staticdynamicdata.IDisplayableDataPart;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ class NullDisplayableDataPart
/*    */   implements IDisplayableDataPart
/*    */ {
/*    */   final IDisplayableDataPartFactory.PART part;
/*    */   final Logger logger;
/*    */ 
/*    */   NullDisplayableDataPart(Logger logger, IDisplayableDataPartFactory.PART part)
/*    */   {
/* 17 */     this.part = part;
/* 18 */     this.logger = logger;
/*    */   }
/*    */ 
/*    */   public void draw(Graphics g, JComponent jComponent) {
/* 22 */     this.logger.warn("Displayable data part: " + this.part + " not properly initialized!");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.NullDisplayableDataPart
 * JD-Core Version:    0.6.0
 */