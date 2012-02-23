/*   */ package com.dukascopy.dds2.greed.gui.component.chart.listeners;
/*   */ 
/*   */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramePanel;
/*   */ 
/*   */ public class FrameListenerAdapter
/*   */   implements FrameListener
/*   */ {
/*   */   public boolean isCloseAllowed(TabsAndFramePanel component)
/*   */   {
/* 8 */     return true;
/*   */   }
/*   */ 
/*   */   public void frameClosed(TabsAndFramePanel tabsAndFramePanel, int tabCount)
/*   */   {
/*   */   }
/*   */ 
/*   */   public void frameSelected(int panelId)
/*   */   {
/*   */   }
/*   */ 
/*   */   public void frameAdded(boolean isUndocked, int tabCount)
/*   */   {
/*   */   }
/*   */ 
/*   */   public void frameDocked(int tabCount)
/*   */   {
/*   */   }
/*   */ 
/*   */   public void tabClosed(int tabCount)
/*   */   {
/*   */   }
/*   */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.listeners.FrameListenerAdapter
 * JD-Core Version:    0.6.0
 */