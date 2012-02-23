/*    */ package com.dukascopy.charts.view.staticdynamicdata;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import java.util.Map;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ class SubAxisYPanelStaticDynamicData extends AbstractStaticDynamicData
/*    */ {
/*    */   public void drawDynamicData(Graphics g, JComponent jComponent)
/*    */   {
/* 12 */     ((IDisplayableDataPart)this.displayableDataParts.get(IDisplayableDataPart.TYPE.INDICATOR_VALUE)).draw(g, jComponent);
/* 13 */     ((IDisplayableDataPart)this.displayableDataParts.get(IDisplayableDataPart.TYPE.MOVEABLELABEL)).draw(g, jComponent);
/*    */   }
/*    */ 
/*    */   public void drawStaticData(Graphics g, JComponent jComponent) {
/* 17 */     ((IDisplayableDataPart)this.displayableDataParts.get(IDisplayableDataPart.TYPE.BACKGROUND)).draw(g, jComponent);
/* 18 */     ((IDisplayableDataPart)this.displayableDataParts.get(IDisplayableDataPart.TYPE.GRID)).draw(g, jComponent);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.staticdynamicdata.SubAxisYPanelStaticDynamicData
 * JD-Core Version:    0.6.0
 */