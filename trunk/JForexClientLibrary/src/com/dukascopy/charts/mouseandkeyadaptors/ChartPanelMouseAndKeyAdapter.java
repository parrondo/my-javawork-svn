/*    */ package com.dukascopy.charts.mouseandkeyadaptors;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*    */ import java.awt.event.MouseEvent;
/*    */ 
/*    */ public abstract class ChartPanelMouseAndKeyAdapter extends ChartsMouseAndKeyAdapter
/*    */ {
/*    */   protected final GuiRefresher guiRefresher;
/*    */   protected final ChartState chartState;
/*    */ 
/*    */   protected ChartPanelMouseAndKeyAdapter(GuiRefresher guiRefresher, ChartState chartState)
/*    */   {
/* 17 */     this.guiRefresher = guiRefresher;
/* 18 */     this.chartState = chartState;
/*    */   }
/*    */ 
/*    */   public void mouseEntered(MouseEvent e)
/*    */   {
/* 23 */     super.mouseEntered(e);
/* 24 */     changeMouseCursorWindowLocationOnEnter();
/*    */   }
/*    */ 
/*    */   public void mouseExited(MouseEvent e)
/*    */   {
/* 29 */     super.mouseExited(e);
/* 30 */     changeMouseCursorWindowLocationOnExit();
/*    */   }
/*    */ 
/*    */   public void mouseMoved(MouseEvent e)
/*    */   {
/* 35 */     this.chartState.mouseCrossCursorChanged(e.getPoint());
/*    */   }
/*    */ 
/*    */   public void mouseDragged(MouseEvent e)
/*    */   {
/* 40 */     this.chartState.mouseCrossCursorChanged(e.getPoint());
/*    */   }
/*    */ 
/*    */   protected void changeMouseCursorWindowLocationOnExit() {
/* 44 */     this.chartState.changeMouseCursorWindowLocation(-2);
/* 45 */     this.guiRefresher.refreshAllContent();
/*    */   }
/*    */ 
/*    */   protected void changeMouseCursorWindowLocationOnEnter() {
/* 49 */     this.chartState.changeMouseCursorWindowLocation(getWindowId());
/* 50 */     this.guiRefresher.refreshAllContent();
/*    */   }
/*    */ 
/*    */   protected abstract int getWindowId();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mouseandkeyadaptors.ChartPanelMouseAndKeyAdapter
 * JD-Core Version:    0.6.0
 */