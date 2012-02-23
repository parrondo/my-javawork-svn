/*    */ package com.dukascopy.dds2.greed.agent;
/*    */ 
/*    */ import com.dukascopy.api.IUserInterface;
/*    */ import com.dukascopy.charts.persistence.IdManager;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.BottomPanelWithoutProfitLossLabelCustom;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.DockedUndockedFrame;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class IGUIManagerImpl
/*    */   implements IUserInterface
/*    */ {
/* 20 */   private Map<String, Integer> ids = new HashMap();
/*    */   final IdManager idManager;
/*    */   final IChartTabsAndFramesController iChartTabsAndFramesController;
/*    */   final TabsAndFramesTabbedPane tabsAndFramesTabbedPane;
/*    */ 
/*    */   public IGUIManagerImpl(IChartTabsAndFramesController chartTabsController, TabsAndFramesTabbedPane tabbedPane, IdManager idManager)
/*    */   {
/* 27 */     this.idManager = idManager;
/* 28 */     this.iChartTabsAndFramesController = chartTabsController;
/* 29 */     this.tabsAndFramesTabbedPane = tabbedPane;
/*    */   }
/*    */ 
/*    */   public JPanel getBottomTab(String key)
/*    */   {
/* 34 */     Integer id = convertKeyToId(key);
/* 35 */     DockedUndockedFrame frame = this.tabsAndFramesTabbedPane.getPanelByPanelId(id.intValue());
/* 36 */     if (frame != null) {
/* 37 */       return frame.getContent();
/*    */     }
/* 39 */     JPanel content = createNewPanel();
/* 40 */     this.tabsAndFramesTabbedPane.addFrame(new BottomPanelWithoutProfitLossLabelCustom(id.intValue(), content), key, false, true);
/* 41 */     this.tabsAndFramesTabbedPane.doLayout();
/* 42 */     return content;
/*    */   }
/*    */ 
/*    */   private JPanel createNewPanel() {
/* 46 */     JPanel panel = new JPanel();
/* 47 */     panel.setPreferredSize(GuiUtilsAndConstants.getOneQuarterOfDisplayDimension());
/* 48 */     return panel;
/*    */   }
/*    */ 
/*    */   public void removeBottomTab(String key) {
/* 52 */     Integer id = (Integer)this.ids.remove(key);
/* 53 */     if (id == null) {
/* 54 */       return;
/*    */     }
/* 56 */     this.tabsAndFramesTabbedPane.closeFrame(id.intValue());
/*    */   }
/*    */ 
/*    */   public JPanel getMainTab(String key) {
/* 60 */     Integer id = convertKeyToId(key);
/* 61 */     return this.iChartTabsAndFramesController.createOrGetCustomMainTab(key, id);
/*    */   }
/*    */ 
/*    */   public void removeMainTab(String key) {
/* 65 */     Integer id = (Integer)this.ids.remove(key);
/* 66 */     if (id == null) {
/* 67 */       return;
/*    */     }
/* 69 */     this.iChartTabsAndFramesController.removeCustomMainTab(key, id);
/*    */   }
/*    */ 
/*    */   public JFrame getMainFrame()
/*    */   {
/* 74 */     return (JFrame)GreedContext.get("clientGui");
/*    */   }
/*    */ 
/*    */   private Integer convertKeyToId(String key) {
/* 78 */     Integer id = (Integer)this.ids.get(key);
/* 79 */     if (id == null) {
/* 80 */       id = Integer.valueOf(this.idManager.getNextChartId());
/* 81 */       this.ids.put(key, id);
/*    */     }
/* 83 */     return id;
/*    */   }
/*    */ 
/*    */   public IChartTabsAndFramesController getiChartTabsAndFramesController() {
/* 87 */     return this.iChartTabsAndFramesController;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.IGUIManagerImpl
 * JD-Core Version:    0.6.0
 */