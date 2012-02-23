/*    */ package com.dukascopy.dds2.greed.gui.component.moverview;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.event.ContainerAdapter;
/*    */ import java.awt.event.ContainerEvent;
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import javax.swing.BorderFactory;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class TabContentPanel extends JPanel
/*    */ {
/*    */   MarketOverviewFrame moFrame;
/*    */ 
/*    */   TabContentPanel(MarketOverviewFrame moFrame)
/*    */   {
/* 23 */     this.moFrame = moFrame;
/* 24 */     build();
/*    */   }
/*    */ 
/*    */   TabContentPanel()
/*    */   {
/* 29 */     build();
/*    */   }
/*    */ 
/*    */   private void build()
/*    */   {
/* 34 */     FlowLayout flow = new FlowLayout(0);
/* 35 */     flow.setHgap(0);
/* 36 */     flow.setVgap(0);
/*    */ 
/* 38 */     setLayout(flow);
/*    */ 
/* 40 */     setBorder(BorderFactory.createEmptyBorder());
/*    */ 
/* 42 */     addMouseListener(new MouseAdapter()
/*    */     {
/*    */       public void mousePressed(MouseEvent e) {
/* 45 */         TabContentPanel.this.requestFocus();
/*    */       }
/*    */     });
/* 49 */     addContainerListener(new ContainerAdapter() {
/*    */       public void componentAdded(ContainerEvent containerEvent) {
/* 51 */         super.componentAdded(containerEvent);
/* 52 */         TabContentPanel.this.validate();
/* 53 */         TabContentPanel.this.resize();
/*    */       }
/*    */ 
/*    */       public void componentRemoved(ContainerEvent containerEvent) {
/* 57 */         super.componentRemoved(containerEvent);
/* 58 */         TabContentPanel.this.validate();
/* 59 */         TabContentPanel.this.resize();
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   protected void resize()
/*    */   {
/* 68 */     int divideResult = 0;
/*    */     try {
/* 70 */       divideResult = (int)this.moFrame.getSize().getWidth() / this.moFrame.getMiniPanelWidth();
/*    */     }
/*    */     catch (ArithmeticException e) {
/*    */     }
/* 74 */     int columnCount = divideResult < 1 ? divideResult++ : divideResult;
/*    */ 
/* 76 */     int newHeight = 0;
/*    */ 
/* 78 */     int selectedTabInstrumentCount = getComponentCount();
/*    */ 
/* 80 */     float divRes = selectedTabInstrumentCount / columnCount;
/* 81 */     float difference = divRes - Math.round(divRes);
/*    */ 
/* 83 */     int rowsCount = difference != 0.0F ? (int)divRes + 1 : (int)divRes;
/* 84 */     rowsCount = rowsCount < 1 ? rowsCount++ : rowsCount;
/* 85 */     newHeight = rowsCount * this.moFrame.getMiniPanelHeight() + 30;
/*    */ 
/* 87 */     Dimension size = new Dimension(this.moFrame.getWidth(), newHeight);
/*    */ 
/* 89 */     setSize(size);
/* 90 */     setPreferredSize(size);
/*    */   }
/*    */ 
/*    */   public void addMiniPanel(MiniPanel miniPanel)
/*    */   {
/* 95 */     add(miniPanel);
/*    */   }
/*    */ 
/*    */   public void removeMiniPanel(MiniPanel miniPanel) {
/* 99 */     remove(miniPanel);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.moverview.TabContentPanel
 * JD-Core Version:    0.6.0
 */