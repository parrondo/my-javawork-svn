/*     */ package com.dukascopy.dds2.greed.gui;
/*     */ 
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.ExpandableSplitPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.FrameListenerAdapter;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposurePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerTable;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.CommonWorkspaceHellper;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.JClientWorkspaceHellper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableHeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.RowSorter;
/*     */ import javax.swing.border.Border;
/*     */ 
/*     */ public class CommonClientFormLayoutManager extends ClientFormLayoutManager
/*     */ {
/*  41 */   private static Border EMPTY_BORDER = BorderFactory.createEmptyBorder();
/*     */   private JPanel mPanel;
/*     */   protected TickerPanel tickerPanel;
/*     */   protected ExpandableSplitPane topSplit;
/*     */   protected ExpandableSplitPane bottomSplit;
/*     */ 
/*     */   protected void setSpecificDependencies()
/*     */   {
/*  50 */     this.dealPanel.setWorkspacePanel(this.tickerPanel);
/*     */   }
/*     */ 
/*     */   protected void initSpecificComponents() {
/*  54 */     this.splitPane.setContinuousLayout(true);
/*  55 */     this.splitPane.setResizeWeight(0.5D);
/*  56 */     this.splitPane.setDividerSize(10);
/*  57 */     this.splitPane.setOrientation(0);
/*     */ 
/*  59 */     this.splitPane.setBorder(EMPTY_BORDER);
/*     */ 
/*  61 */     this.topSplit.setContinuousLayout(true);
/*  62 */     this.topSplit.setResizeWeight(0.5D);
/*  63 */     this.topSplit.setDividerSize(10);
/*  64 */     this.topSplit.setOrientation(0);
/*     */ 
/*  66 */     this.topSplit.setBorder(EMPTY_BORDER);
/*     */ 
/*  68 */     this.bottomSplit.setContinuousLayout(true);
/*  69 */     this.bottomSplit.setResizeWeight(0.5D);
/*  70 */     this.bottomSplit.setDividerSize(10);
/*  71 */     this.bottomSplit.setOrientation(0);
/*  72 */     this.bottomSplit.setBorder(EMPTY_BORDER);
/*     */ 
/*  74 */     this.workspaceHelper = new JClientWorkspaceHellper();
/*     */   }
/*     */ 
/*     */   protected void initSpecificListeners() {
/*  78 */     Component dividerTop = this.topSplit.getComponents()[0];
/*  79 */     dividerTop.addMouseListener(new MouseAdapter() {
/*     */       public void mouseReleased(MouseEvent mouseEvent) {
/*  81 */         CommonClientFormLayoutManager.this.exposurePanel.setMinimumSize(CommonClientFormLayoutManager.this.exposurePanel.getSize());
/*  82 */         CommonClientFormLayoutManager.this.topSplit.setResizeWeight(0.0D);
/*     */       }
/*  84 */       public void mouseEntered(MouseEvent mouseEvent) { CommonClientFormLayoutManager.this.exposurePanel.setMinimumSize(null); } 
/*  85 */       public void mouseExited(MouseEvent mouseEvent) { CommonClientFormLayoutManager.this.exposurePanel.setMinimumSize(CommonClientFormLayoutManager.this.exposurePanel.getSize());
/*     */       }
/*     */     });
/*  87 */     Component dividerBottom = this.bottomSplit.getComponents()[0];
/*  88 */     dividerBottom.addMouseListener(new MouseAdapter() {
/*     */       public void mouseReleased(MouseEvent mouseEvent) {
/*  90 */         CommonClientFormLayoutManager.this.mPanel.setMinimumSize(CommonClientFormLayoutManager.this.mPanel.getSize());
/*  91 */         CommonClientFormLayoutManager.this.bottomSplit.setResizeWeight(1.0D);
/*     */       }
/*  93 */       public void mouseEntered(MouseEvent mouseEvent) { CommonClientFormLayoutManager.this.mPanel.setMinimumSize(null); } 
/*  94 */       public void mouseExited(MouseEvent mouseEvent) { CommonClientFormLayoutManager.this.mPanel.setMinimumSize(CommonClientFormLayoutManager.this.mPanel.getSize());
/*     */       }
/*     */     });
/*  97 */     this.chartTabsAndFramesController.addFrameListener(new FrameListenerAdapter()
/*     */     {
/*     */       public void frameClosed(TabsAndFramePanel tabsAndFramePanel, int tabCount) {
/* 100 */         ((DDSChartsController)GreedContext.get("chartsController")).removeChart(Integer.valueOf(tabsAndFramePanel.getPanelId()));
/* 101 */         ((ClientSettingsStorage)GreedContext.get("settingsStorage")).remove(Integer.valueOf(tabsAndFramePanel.getPanelId()));
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected void createSpecificComponents() {
/* 108 */     this.tickerPanel = new TickerPanel(this.dealPanel);
/*     */ 
/* 110 */     this.topSplit = new ExpandableSplitPane("TopSplitPane");
/* 111 */     this.bottomSplit = new ExpandableSplitPane("BottomSplitPane");
/*     */ 
/* 113 */     this.splitPane = new ExpandableSplitPane("MainSplitPane", 0, this.topSplit, this.bottomSplit);
/*     */   }
/*     */ 
/*     */   protected void placeSpecificComponents() {
/* 117 */     this.body.setLayout(new BoxLayout(this.body, 0));
/* 118 */     this.desktop.setLayout(new BoxLayout(this.desktop, 1));
/*     */ 
/* 120 */     this.rightPanel.setLayout(new BoxLayout(this.rightPanel, 1));
/* 121 */     this.rightPanel.add(this.splitPane);
/*     */ 
/* 123 */     this.body.add(this.dealPanel);
/* 124 */     this.body.add(Box.createHorizontalStrut(5));
/* 125 */     this.body.add(this.rightPanel);
/*     */ 
/* 127 */     this.desktop.add(this.body);
/* 128 */     this.desktop.add(Box.createVerticalStrut(2));
/* 129 */     this.desktop.add(this.statusBar);
/*     */ 
/* 131 */     this.content.setLayout(new BorderLayout());
/* 132 */     this.content.add(this.desktop, "Center");
/*     */ 
/* 135 */     this.topSplit.add(wrapExposurePanel(this.exposurePanel));
/* 136 */     this.topSplit.add(wrapPositionsPanel(this.positionsPanel));
/*     */ 
/* 139 */     this.bottomSplit.add(wrapOrdersPanel(this.ordersPanel));
/* 140 */     this.bottomSplit.add(wrapMessagePanel(this.messagePanel));
/*     */   }
/*     */ 
/*     */   private JPanel wrapOrdersPanel(OrdersPanel ordersPanel) {
/* 144 */     ordersPanel.setLayout(new BoxLayout(ordersPanel, 1));
/* 145 */     HeaderPanel header = new JLocalizableHeaderPanel("header.orders", false, true);
/* 146 */     ordersPanel.addHeader(header);
/* 147 */     return ordersPanel;
/*     */   }
/*     */ 
/*     */   private JPanel wrapPositionsPanel(PositionsPanel positionsPanel) {
/* 151 */     positionsPanel.setLayout(new BoxLayout(positionsPanel, 1));
/* 152 */     HeaderPanel header = new JLocalizableHeaderPanel(CommonWorkspaceHellper.getPositionsLabelKey(), false, true);
/* 153 */     positionsPanel.addHeader(header);
/* 154 */     positionsPanel.setBorder(EMPTY_BORDER);
/* 155 */     return positionsPanel;
/*     */   }
/*     */ 
/*     */   private JPanel wrapExposurePanel(ExposurePanel exposurePanel) {
/* 159 */     exposurePanel.setLayout(new BoxLayout(exposurePanel, 1));
/* 160 */     HeaderPanel header = new JLocalizableHeaderPanel(CommonWorkspaceHellper.getPositionsSummaryLabelKey(), false, true);
/* 161 */     exposurePanel.addHeader(header);
/* 162 */     exposurePanel.setBorder(EMPTY_BORDER);
/* 163 */     return exposurePanel;
/*     */   }
/*     */ 
/*     */   private JPanel wrapMessagePanel(MessagePanel messagePanel)
/*     */   {
/* 168 */     JLocalizableButton btnCopyMessage = new JLocalizableButton(ResizingManager.ComponentSize.SIZE_120X24);
/* 169 */     btnCopyMessage.setAction(messagePanel.getActionCopyMessages());
/* 170 */     JLocalizableButton btnClear = new JLocalizableButton(ResizingManager.ComponentSize.SIZE_120X24);
/* 171 */     btnClear.setAction(messagePanel.getActionClearLog());
/*     */ 
/* 173 */     JPanel pnlButtonsInner = new JPanel(new GridLayout(1, 0, 5, 0));
/* 174 */     pnlButtonsInner.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
/* 175 */     pnlButtonsInner.add(btnCopyMessage);
/* 176 */     pnlButtonsInner.add(btnClear);
/*     */ 
/* 178 */     JPanel pnlButtons = new JPanel(new BorderLayout());
/* 179 */     pnlButtons.add(pnlButtonsInner, "West");
/*     */ 
/* 181 */     JPanel panelToWrap = new JPanel(new BorderLayout());
/* 182 */     panelToWrap.add(pnlButtons, "North");
/* 183 */     panelToWrap.add(messagePanel, "Center");
/*     */ 
/* 185 */     this.mPanel = new JPanel();
/* 186 */     this.mPanel.setLayout(new BoxLayout(this.mPanel, 1));
/* 187 */     this.mPanel.setMinimumSize(new Dimension(0, 0));
/* 188 */     this.mPanel.add(new JLocalizableHeaderPanel("header.messages", false));
/* 189 */     this.mPanel.add(panelToWrap);
/* 190 */     return this.mPanel;
/*     */   }
/*     */ 
/*     */   protected void resetlayout() {
/* 194 */     this.topSplit.setResizeWeight(0.5D);
/* 195 */     this.exposurePanel.setMinimumSize(null);
/* 196 */     this.topSplit.setDividerLocation(0.3D);
/* 197 */     this.bottomSplit.setResizeWeight(0.5D);
/* 198 */     this.mPanel.setMinimumSize(null);
/* 199 */     this.bottomSplit.setDividerLocation(0.7D);
/*     */   }
/*     */ 
/*     */   protected void saveSpecificClientSettings(ClientSettingsStorage settingsStorage) {
/* 203 */     settingsStorage.saveSplitPane(this.topSplit);
/* 204 */     settingsStorage.saveSplitPane(this.bottomSplit);
/*     */ 
/* 208 */     this.chartTabsAndFramesController.saveState();
/*     */ 
/* 210 */     settingsStorage.saveTableSortKeys(this.tickerPanel.getTickerTable().getTableId(), this.tickerPanel.getTickerTable().getRowSorter().getSortKeys());
/*     */ 
/* 212 */     settingsStorage.saveTableColumns(this.tickerPanel.getTickerTable().getTableId(), this.tickerPanel.getTickerTable().getColumnModel());
/*     */   }
/*     */ 
/*     */   protected void resizeSplitters(ClientSettingsStorage settingsSaver) {
/* 216 */     settingsSaver.restoreSplitPane(this.topSplit, 0.3D);
/* 217 */     settingsSaver.restoreSplitPane(this.bottomSplit, 0.7D);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.CommonClientFormLayoutManager
 * JD-Core Version:    0.6.0
 */