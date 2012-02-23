/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import com.dukascopy.charts.main.interfaces.ProgressListener;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.Box;
/*    */ import javax.swing.JLabel;
/*    */ 
/*    */ public class ChartButtonTabPanel extends ButtonTabPanel
/*    */   implements ProgressListener
/*    */ {
/* 18 */   private final JLabel progressImageLabel = new JLabel(GuiUtilsAndConstants.ICON_TITLEBAR_CHART_INACTIVE);
/* 19 */   private boolean isLoadingData = false;
/*    */ 
/*    */   public ChartButtonTabPanel(ActionListener actionListener, int panelId, String title) {
/* 22 */     super(panelId, title, actionListener, TabedPanelType.CHART);
/* 23 */     this.textLabel = new JLocalizableLabel();
/* 24 */     localizeTitle();
/* 25 */     setLayout(new FlowLayout(0, 0, 0));
/* 26 */     addCommonComponents();
/*    */   }
/*    */ 
/*    */   protected void addCommonComponents() {
/* 30 */     add(this.progressImageLabel);
/* 31 */     add(Box.createHorizontalStrut(10));
/* 32 */     add(this.textLabel);
/* 33 */     add(Box.createHorizontalStrut(10));
/* 34 */     add(this.undockTabButton);
/* 35 */     add(this.closeTabButton);
/*    */   }
/*    */ 
/*    */   public void setProgress(boolean isProgress, boolean isLoadingOrders) {
/* 39 */     this.isLoadingData = isProgress;
/* 40 */     if (isProgress)
/* 41 */       this.progressImageLabel.setIcon(GuiUtilsAndConstants.ICON_TITLEBAR_IN_PROGRESS);
/*    */     else
/* 43 */       this.progressImageLabel.setIcon(GuiUtilsAndConstants.getTitlbarIcon(TabedPanelType.CHART, this.isActive));
/*    */   }
/*    */ 
/*    */   public void setIconIsActive(boolean iconIsActive)
/*    */   {
/* 48 */     super.setIconIsActive(iconIsActive);
/* 49 */     if (this.isLoadingData) {
/* 50 */       return;
/*    */     }
/* 52 */     this.progressImageLabel.setIcon(GuiUtilsAndConstants.getTitlbarIcon(TabedPanelType.CHART, this.isActive));
/*    */   }
/*    */ 
/*    */   protected void localizeTitle() {
/* 56 */     ((JLocalizableLabel)this.textLabel).setTextKeyParams(this.title.split(","));
/* 57 */     this.textLabel.setText("tab.title.tamplate");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ChartButtonTabPanel
 * JD-Core Version:    0.6.0
 */