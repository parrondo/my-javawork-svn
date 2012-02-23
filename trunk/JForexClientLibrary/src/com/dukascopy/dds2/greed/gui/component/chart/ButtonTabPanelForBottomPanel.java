/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.Box;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.JLabel;
/*    */ 
/*    */ public class ButtonTabPanelForBottomPanel extends ButtonTabPanel
/*    */ {
/* 15 */   private static final Icon PROGRESS_ICON = new ResizableIcon("titlebar_icon_loading.gif");
/*    */ 
/*    */   public ButtonTabPanelForBottomPanel(int panelId, String title, ActionListener actionListener) {
/* 18 */     super(panelId, title, actionListener, TabedPanelType.OTHER);
/* 19 */     this.textLabel = new JLabel(title);
/* 20 */     setLayout(new FlowLayout(0, 0, 0));
/* 21 */     addCommonComponents();
/*    */   }
/*    */ 
/*    */   protected void addCommonComponents() {
/* 25 */     add(Box.createHorizontalStrut(10));
/* 26 */     add(this.textLabel);
/* 27 */     add(Box.createHorizontalStrut(10));
/* 28 */     add(this.undockTabButton);
/*    */   }
/*    */ 
/*    */   public void setInProgress(boolean value) {
/* 32 */     if (value)
/* 33 */       this.textLabel.setIcon(PROGRESS_ICON);
/*    */     else
/* 35 */       this.textLabel.setIcon(null);
/*    */   }
/*    */ 
/*    */   protected void localizeTitle()
/*    */   {
/* 41 */     this.textLabel.setText(this.title);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ButtonTabPanelForBottomPanel
 * JD-Core Version:    0.6.0
 */