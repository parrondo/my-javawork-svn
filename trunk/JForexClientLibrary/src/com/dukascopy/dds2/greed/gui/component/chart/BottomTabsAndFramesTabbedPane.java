/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
/*     */ 
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposurePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.CommonWorkspaceHellper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.table.TableModel;
/*     */ 
/*     */ public class BottomTabsAndFramesTabbedPane extends TabsAndFramesTabbedPane
/*     */ {
/*     */   ExposurePanel exposurePanel;
/*     */   PositionsPanel positionsPanel;
/*     */   OrdersPanel ordersPanel;
/*     */   JForexClientFormLayoutManager layoutManager;
/*  45 */   private Map<Integer, StrategyTestPanel> testPanels = new HashMap();
/*     */ 
/*     */   public BottomTabsAndFramesTabbedPane(ExposurePanel exposurePanel, PositionsPanel positionsPanel, OrdersPanel ordersPanel, JForexClientFormLayoutManager layoutManager)
/*     */   {
/*  54 */     this.exposurePanel = exposurePanel;
/*  55 */     this.positionsPanel = positionsPanel;
/*  56 */     this.ordersPanel = ordersPanel;
/*  57 */     this.layoutManager = layoutManager;
/*     */   }
/*     */ 
/*     */   protected void addFollowedTabAndFrame(String title, int panelId, JComponent content) {
/*  61 */     Component buttonTabPanel = ButtonTabPanel.createButtonTabPanel(panelId, title, content, this);
/*  62 */     int index = findIndexWhereToPlace(panelId, getTabCount());
/*  63 */     if (index < getTabCount()) {
/*  64 */       if (index == 0) {
/*  65 */         setComponentAt(0, null);
/*     */       }
/*  67 */       Component component = null;
/*  68 */       if (index == 0) {
/*  69 */         component = this.toolBarAndDesktopPanel;
/*     */       }
/*  71 */       insertTab(title, null, component, null, index);
/*     */     } else {
/*  73 */       addTab(title, null);
/*     */     }
/*     */ 
/*  76 */     setTabComponentAt(index, buttonTabPanel);
/*  77 */     if (index == 0)
/*  78 */       setSelectedIndex(0);
/*     */   }
/*     */ 
/*     */   private int findIndexWhereToPlace(int panelId, int tabCount)
/*     */   {
/*  83 */     ArrayList indexList = new ArrayList(tabCount + 1);
/*  84 */     indexList.add(Integer.valueOf(panelId));
/*  85 */     for (int i = 0; i < tabCount; i++) {
/*  86 */       ButtonTabPanel curTabComponent = (ButtonTabPanel)getTabComponentAt(i);
/*  87 */       indexList.add(Integer.valueOf(curTabComponent.getPanelId()));
/*     */     }
/*  89 */     Collections.sort(indexList);
/*  90 */     return indexList.indexOf(Integer.valueOf(panelId));
/*     */   }
/*     */ 
/*     */   public void translate()
/*     */   {
/*  95 */     updateLanguage4TabbedPane();
/*     */   }
/*     */ 
/*     */   private void updateLanguage4TabbedPane() {
/*  99 */     int expRowCount = 0;
/* 100 */     int posRowCount = 0;
/* 101 */     int ordersRowCount = 0;
/*     */     try
/*     */     {
/* 104 */       expRowCount = this.exposurePanel.getTable().getModel().getRowCount();
/* 105 */       posRowCount = this.positionsPanel.getTable().getModel().getRowCount();
/* 106 */       ordersRowCount = this.ordersPanel.getModel().getRowCount();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */ 
/* 112 */     String positionsSummaryText = LocalizationManager.getText(CommonWorkspaceHellper.getPositionsSummaryLabelKey());
/* 113 */     String positionsText = LocalizationManager.getText(CommonWorkspaceHellper.getPositionsLabelKey());
/*     */ 
/* 115 */     String positionSummaryName = positionsSummaryText + " (" + expRowCount + ")";
/* 116 */     String positionsName = positionsText + " (" + posRowCount + ")";
/* 117 */     String ordersName = LocalizationManager.getText("tab.orders") + " (" + ordersRowCount + ")";
/*     */ 
/* 119 */     setTitleForPanelId(0, positionSummaryName);
/* 120 */     setTitleForPanelId(1, positionsName);
/* 121 */     setTitleForPanelId(2, ordersName);
/* 122 */     setTitleForPanelId(3, LocalizationManager.getText("tab.messages"));
/* 123 */     setTitleForPanelId(6, LocalizationManager.getText("tab.dowjones.news"));
/* 124 */     setTitleForPanelId(7, LocalizationManager.getText("tab.dowjones.calendar"));
/* 125 */     setTitleForPanelId(9, LocalizationManager.getText("tab.price.alerter"));
/* 126 */     setTitleForPanelId(5, LocalizationManager.getText("tab.strategies"));
/* 127 */     setTitleForPanelId(10000, LocalizationManager.getText("tab.historical.data.manager"));
/*     */ 
/* 129 */     String testPanelTitle = LocalizationManager.getText("tab.historical.tester");
/* 130 */     if (this.testPanels != null)
/*     */     {
/* 133 */       for (Integer testPanelId : this.testPanels.keySet())
/* 134 */         setTitleForPanelId(testPanelId.intValue(), testPanelTitle);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Dimension getDefaultSize(UndockedJFrame undockedFrame)
/*     */   {
/* 141 */     Dimension displayDimension = GuiUtilsAndConstants.getDisplayDimension();
/* 142 */     Dimension contentPanePrefSize = undockedFrame.getContentPane().getPreferredSize();
/*     */ 
/* 144 */     Dimension undockedFrameSize = null;
/* 145 */     if ((contentPanePrefSize.getWidth() > displayDimension.getWidth()) || (contentPanePrefSize.getHeight() > displayDimension.getHeight()) || (contentPanePrefSize.getWidth() <= 0.0D) || (contentPanePrefSize.getHeight() <= 0.0D))
/*     */     {
/* 150 */       undockedFrameSize = super.getDefaultSize(undockedFrame);
/*     */     }
/*     */     else {
/* 153 */       undockedFrameSize = new Dimension(contentPanePrefSize);
/*     */     }
/*     */ 
/* 156 */     return undockedFrameSize;
/*     */   }
/*     */ 
/*     */   public List<StrategyTestPanel> getStrategyTestPanels() {
/* 160 */     List result = new LinkedList();
/*     */ 
/* 162 */     for (int i = 0; i < getTabCount(); i++) {
/* 163 */       Component c = getTabComponentAt(i);
/* 164 */       if ((c instanceof ButtonTabPanel)) {
/* 165 */         int panelId = ((ButtonTabPanel)c).getPanelId();
/* 166 */         StrategyTestPanel panel = getStrategyTestPanel(panelId);
/* 167 */         if (panel != null) {
/* 168 */           result.add(panel);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 173 */     return result;
/*     */   }
/*     */ 
/*     */   public StrategyTestPanel getStrategyTestPanel(String strategyName, boolean includingBusy)
/*     */   {
/* 178 */     for (StrategyTestPanel panel : getStrategyTestPanels()) {
/* 179 */       if ((strategyName == null) && (panel.getStrategyName() == null) && (
/* 180 */         (includingBusy) || (!panel.isBusy()))) {
/* 181 */         return panel;
/*     */       }
/*     */ 
/* 184 */       if ((strategyName != null) && (strategyName.equals(panel.getStrategyName())) && (
/* 185 */         (includingBusy) || (!panel.isBusy()))) {
/* 186 */         return panel;
/*     */       }
/*     */     }
/*     */ 
/* 190 */     return null;
/*     */   }
/*     */ 
/*     */   public StrategyTestPanel getStrategyTestPanel(int chartPanelId) {
/* 194 */     return (StrategyTestPanel)this.testPanels.get(Integer.valueOf(chartPanelId));
/*     */   }
/*     */ 
/*     */   public int getPanelChartId(StrategyTestPanel panel) {
/* 198 */     for (Integer testPanelId : this.testPanels.keySet()) {
/* 199 */       if (getStrategyTestPanel(testPanelId.intValue()) == panel) {
/* 200 */         return testPanelId.intValue();
/*     */       }
/*     */     }
/* 203 */     return -1;
/*     */   }
/*     */ 
/*     */   public int addTesterPanel(StrategyTestPanel strategyTestPanel, int id, boolean isUndocked, boolean isExpanded)
/*     */   {
/*     */     int panelId;
/*     */     int panelId;
/* 208 */     if (id < 0)
/* 209 */       panelId = IdManager.getInstance().getNextChartId();
/*     */     else {
/* 211 */       panelId = id;
/*     */     }
/* 213 */     this.testPanels.put(Integer.valueOf(panelId), strategyTestPanel);
/*     */ 
/* 215 */     BottomPanelWithoutProfitLossLabel wrappedPanel = new BottomPanelWithoutProfitLossLabel(panelId, new JScrollPane(strategyTestPanel));
/* 216 */     addFrame(wrappedPanel, LocalizationManager.getText("tab.historical.tester"), isUndocked, isExpanded);
/* 217 */     return panelId;
/*     */   }
/*     */ 
/*     */   public void selectStrategyTestPanel(StrategyTestPanel strategyTestPanel) {
/* 221 */     int panelId = getPanelChartId(strategyTestPanel);
/* 222 */     if (panelId >= 0)
/* 223 */       selectPanel(panelId);
/*     */   }
/*     */ 
/*     */   protected void openUndockAndClosePopupMenu(MouseEvent event, boolean addCloneTesterMenu)
/*     */   {
/* 229 */     StrategyTestPanel panel = getStrategyTestPanel(getSelectedPanelId());
/* 230 */     super.openUndockAndClosePopupMenu(event, panel != null);
/*     */   }
/*     */ 
/*     */   public void executeAction(TabsOrderingMenuContainer.Action action, int panelId)
/*     */   {
/* 235 */     if (action == TabsOrderingMenuContainer.Action.CLONE_TESTER)
/*     */     {
/* 237 */       StrategyTestPanel tester = getStrategyTestPanel(getSelectedPanelId());
/* 238 */       if (tester != null) {
/* 239 */         StrategyTestBean bean = new StrategyTestBean();
/* 240 */         tester.save(bean);
/*     */ 
/* 242 */         StrategyTestPanel newPanel = this.layoutManager.addStrategyTesterPanel(-1, false, true);
/* 243 */         newPanel.set(bean);
/* 244 */         newPanel.initWithStrategy(tester.getStrategyName());
/*     */       }
/*     */     } else {
/* 247 */       super.executeAction(action, panelId);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void closeFrameImpl(DockedUndockedFrame frame)
/*     */   {
/* 253 */     if (frame == null) {
/* 254 */       return;
/*     */     }
/*     */ 
/* 257 */     TabsAndFramePanel content = frame.getContent();
/* 258 */     content.removeAll();
/* 259 */     int panelId = content.getPanelId();
/*     */ 
/* 261 */     super.closeFrameImpl(frame);
/*     */ 
/* 263 */     StrategyTestPanel testPanel = (StrategyTestPanel)this.testPanels.get(Integer.valueOf(panelId));
/* 264 */     if (testPanel != null) {
/* 265 */       testPanel.panelClosed();
/* 266 */       this.testPanels.remove(Integer.valueOf(panelId));
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.BottomTabsAndFramesTabbedPane
 * JD-Core Version:    0.6.0
 */