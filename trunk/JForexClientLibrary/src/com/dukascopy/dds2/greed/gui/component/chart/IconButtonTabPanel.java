/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.Box;
/*    */ import javax.swing.JLabel;
/*    */ 
/*    */ public class IconButtonTabPanel extends ButtonTabPanel
/*    */ {
/* 12 */   private final JLabel leftImageLabel = new JLabel(GuiUtilsAndConstants.ICON_TITLEBAR_IN_PROGRESS);
/*    */ 
/*    */   public IconButtonTabPanel(int panelId, String title, ActionListener actionListener, TabedPanelType panelType) {
/* 15 */     super(panelId, title, actionListener, panelType);
/* 16 */     this.textLabel = new JLabel();
/* 17 */     localizeTitle();
/* 18 */     setLayout(new FlowLayout(0, 0, 0));
/* 19 */     addCommonComponents();
/* 20 */     setIconIsActive(true);
/*    */   }
/*    */ 
/*    */   protected void addCommonComponents() {
/* 24 */     add(this.leftImageLabel);
/* 25 */     add(Box.createHorizontalStrut(10));
/* 26 */     add(this.textLabel);
/* 27 */     add(Box.createHorizontalStrut(10));
/* 28 */     add(this.undockTabButton);
/* 29 */     add(this.closeTabButton);
/*    */   }
/*    */ 
/*    */   protected void localizeTitle() {
/* 33 */     this.textLabel.setText(this.title);
/*    */   }
/*    */ 
/*    */   public void setIconIsActive(boolean isIconActive)
/*    */   {
/* 38 */     super.setIconIsActive(isIconActive);
/* 39 */     this.leftImageLabel.setIcon(GuiUtilsAndConstants.getTitlbarIcon(this.panelType, this.isActive));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.IconButtonTabPanel
 * JD-Core Version:    0.6.0
 */