/*    */ package com.dukascopy.dds2.greed.gui.component.chart.buttons;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*    */ import javax.swing.Icon;
/*    */ 
/*    */ public class PanelMenuItem extends JLocalizableMenuItem
/*    */ {
/*    */   private Object item;
/*    */   private Integer chartPanelId;
/*    */ 
/*    */   public PanelMenuItem(String label)
/*    */   {
/* 14 */     super(label);
/*    */   }
/*    */ 
/*    */   public PanelMenuItem(String text, Icon icon) {
/* 18 */     super(text, icon);
/*    */   }
/*    */ 
/*    */   public Object getItem() {
/* 22 */     return this.item;
/*    */   }
/*    */ 
/*    */   public void setItem(Object item) {
/* 26 */     this.item = item;
/*    */   }
/*    */ 
/*    */   public void setChartPanelId(Integer chartPanelId) {
/* 30 */     this.chartPanelId = chartPanelId;
/*    */   }
/*    */ 
/*    */   public Integer getChartPanelId() {
/* 34 */     return this.chartPanelId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.buttons.PanelMenuItem
 * JD-Core Version:    0.6.0
 */