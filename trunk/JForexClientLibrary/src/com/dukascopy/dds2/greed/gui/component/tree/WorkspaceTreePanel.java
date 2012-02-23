/*     */ package com.dukascopy.dds2.greed.gui.component.tree;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.MouseController;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitable;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CurrencyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.TesterChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.Hidable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.Switchable;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.awt.Dimension;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class WorkspaceTreePanel extends WorkspacePanel
/*     */   implements Hidable, MultiSplitable
/*     */ {
/*     */   private static final int PREF_HEIGHT = 999;
/*     */   private static final int MIN_HEIGHT = 48;
/*     */   private static final int MAX_HEIGHT = 999;
/*     */   private WorkspaceJTree workspaceJTree;
/*     */   private JLocalizableRoundedBorderWithSimpleHeader settingsBorder;
/*     */   private MarketView marketView;
/*     */   private JPanel inner;
/*     */   private JScrollPane workspaceTreeScrollPane;
/*  83 */   private Instrument prevInstrument = Instrument.EURUSD;
/*     */ 
/*     */   public WorkspaceTreePanel(DealPanel dealPanel, WorkspaceTreeController workspaceTreeController)
/*     */   {
/*  52 */     super(dealPanel);
/*  53 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/*     */   }
/*     */ 
/*     */   public void build() {
/*  57 */     setLayout(new BoxLayout(this, 1));
/*     */ 
/*  59 */     this.inner = new JPanel();
/*  60 */     this.inner.setLayout(new BoxLayout(this.inner, 1));
/*     */ 
/*  62 */     this.inner.add(Box.createRigidArea(new Dimension(250, 1)));
/*  63 */     add(this.inner);
/*  64 */     this.inner.setBorder(getSettingsBorder());
/*     */ 
/*  66 */     this.workspaceTreeScrollPane = new JScrollPane(this.workspaceJTree, 20, 31);
/*  67 */     this.workspaceTreeScrollPane.getViewport().setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  68 */     this.inner.add(this.workspaceTreeScrollPane);
/*  69 */     getSettingsBorder().setSwitch(this.workspaceTreeScrollPane.isVisible());
/*     */ 
/*  71 */     MouseController mController = new MouseController(this);
/*  72 */     mController.startY = 5;
/*  73 */     mController.activeY = 20;
/*  74 */     addMouseListener(mController);
/*  75 */     addMouseMotionListener(mController);
/*     */   }
/*     */ 
/*     */   public void setWorkspaceJTree(WorkspaceJTree workspaceJTree)
/*     */   {
/*  80 */     this.workspaceJTree = workspaceJTree;
/*     */   }
/*     */ 
/*     */   public String getSelectedInstrument()
/*     */   {
/*  86 */     Instrument currentInstrument = this.workspaceJTree.getCurrentInstrument();
/*  87 */     if (currentInstrument == null) {
/*  88 */       return this.prevInstrument.toString();
/*     */     }
/*  90 */     this.prevInstrument = currentInstrument;
/*  91 */     return currentInstrument.toString();
/*     */   }
/*     */ 
/*     */   public void setInstruments(List<String> instruments) {
/*  95 */     OrderEntryPanel orderEntryPanel = this.dealPanel.getOrderEntryPanel();
/*  96 */     orderEntryPanel.clearEverything(false);
/*     */ 
/* 100 */     String defaultInstriment = "";
/*     */ 
/* 102 */     if (this.workspaceJTree.getSelectionPath() != null) {
/* 103 */       Object node = this.workspaceJTree.getSelectionPath().getLastPathComponent();
/* 104 */       if (((node instanceof ChartTreeNode)) && (!(node instanceof TesterChartTreeNode))) {
/* 105 */         ChartTreeNode selectedInstrumentNode = (ChartTreeNode)this.workspaceJTree.getSelectionPath().getLastPathComponent();
/* 106 */         defaultInstriment = selectedInstrumentNode.getInstrument().toString();
/*     */       }
/*     */ 
/* 109 */       if ((node instanceof CurrencyTreeNode)) {
/* 110 */         CurrencyTreeNode selectedCurrencyNode = (CurrencyTreeNode)this.workspaceJTree.getSelectionPath().getLastPathComponent();
/* 111 */         defaultInstriment = selectedCurrencyNode.getInstrument().toString();
/*     */       }
/*     */ 
/* 114 */       if ((node instanceof DrawingTreeNode)) {
/* 115 */         DrawingTreeNode selectedDrawingNode = (DrawingTreeNode)this.workspaceJTree.getSelectionPath().getLastPathComponent();
/* 116 */         defaultInstriment = ((ChartTreeNode)selectedDrawingNode.getParent()).getInstrument().toString();
/*     */       }
/*     */     }
/*     */ 
/* 120 */     orderEntryPanel.setInstrument(defaultInstriment);
/*     */ 
/* 122 */     InstrumentStatusUpdateMessage instrumentState = this.marketView.getInstrumentState(defaultInstriment);
/* 123 */     orderEntryPanel.setSubmitEnabled(null == instrumentState ? 1 : instrumentState.getTradable());
/* 124 */     orderEntryPanel.setDefaultStopConditionLabels();
/*     */ 
/* 126 */     if (this.marketView.getLastMarketState(defaultInstriment) != null)
/* 127 */       orderEntryPanel.onMarketState(this.marketView.getLastMarketState(defaultInstriment));
/*     */   }
/*     */ 
/*     */   public List<String> getInstruments()
/*     */   {
/* 133 */     List instruments = new LinkedList();
/* 134 */     DefaultTreeModel workspaceTreeModel = this.workspaceJTree.getModel();
/* 135 */     WorkspaceRootNode root = (WorkspaceRootNode)workspaceTreeModel.getRoot();
/* 136 */     for (int i = 0; i < root.getChildCount(); i++) {
/* 137 */       WorkspaceTreeNode workspaceTreeNode = root.getChildAt(i);
/* 138 */       if ((!(workspaceTreeNode instanceof ChartTreeNode)) || ((workspaceTreeNode instanceof TesterChartTreeNode))) {
/*     */         continue;
/*     */       }
/* 141 */       ChartTreeNode instrumentChartTreeNode = (ChartTreeNode)workspaceTreeNode;
/* 142 */       String instrumentName = instrumentChartTreeNode.getInstrument().toString();
/* 143 */       instruments.add(instrumentName);
/*     */     }
/* 145 */     return instruments;
/*     */   }
/*     */ 
/*     */   public void updateTradability(InstrumentStatusUpdateMessage status)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void updateOrderGroup(OrderGroupMessage orderGroup)
/*     */   {
/* 157 */     this.workspaceJTree.checkDependantCurrenciesAndAddThemIfNecessary(orderGroup.getInstrument());
/*     */   }
/*     */ 
/*     */   protected void populate()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isExpanded() {
/* 165 */     return this.workspaceTreeScrollPane.isVisible();
/*     */   }
/*     */ 
/*     */   public void switchVisibility() {
/* 169 */     this.workspaceTreeScrollPane.setVisible(!this.workspaceTreeScrollPane.isVisible());
/* 170 */     getSettingsBorder().setSwitch(this.workspaceTreeScrollPane.isVisible());
/*     */ 
/* 172 */     if ((getParent() instanceof MultiSplitPane)) {
/* 173 */       ((MultiSplitPane)getParent()).switchVisibility("workspace");
/*     */     }
/*     */ 
/* 176 */     revalidate();
/* 177 */     repaint();
/*     */   }
/*     */ 
/*     */   private JLocalizableRoundedBorderWithSimpleHeader getSettingsBorder() {
/* 181 */     if (this.settingsBorder == null) {
/* 182 */       this.settingsBorder = new JLocalizableRoundedBorderWithSimpleHeader(this.inner);
/* 183 */       this.settingsBorder.setTopInset(22);
/* 184 */       this.settingsBorder.setRightInset(9);
/* 185 */       this.settingsBorder.setLeftInset(11);
/* 186 */       this.settingsBorder.setBottomInset(3);
/*     */ 
/* 188 */       this.settingsBorder.setBottomBorder(1);
/* 189 */       this.settingsBorder.setTopBorder(9);
/*     */ 
/* 191 */       updateSettingsBorder();
/*     */     }
/* 193 */     return this.settingsBorder;
/*     */   }
/*     */ 
/*     */   public void updateSettingsBorder() {
/* 197 */     String settingsFileNameWithoutPrefix = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).getWorkspaceSettingsFileNameWithoutPrefix();
/* 198 */     getSettingsBorder().setHeaderText(settingsFileNameWithoutPrefix);
/* 199 */     repaint();
/*     */   }
/*     */ 
/*     */   public String[] getSelectedInstruments()
/*     */   {
/* 215 */     return new String[] { getSelectedInstrument() };
/*     */   }
/*     */ 
/*     */   public int getPrefHeight()
/*     */   {
/* 220 */     return 999;
/*     */   }
/*     */ 
/*     */   public int getMaxHeight()
/*     */   {
/* 225 */     return 999;
/*     */   }
/*     */ 
/*     */   public int getMinHeight()
/*     */   {
/* 230 */     return isExpanded() ? 48 : 30;
/*     */   }
/*     */ 
/*     */   private class JLocalizableRoundedBorderWithSimpleHeader extends JLocalizableRoundedBorder
/*     */   {
/*     */     public JLocalizableRoundedBorderWithSimpleHeader(JComponent parent)
/*     */     {
/* 204 */       super(true);
/*     */     }
/*     */ 
/*     */     protected void performLocalization()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreePanel
 * JD-Core Version:    0.6.0
 */