/*     */ package com.dukascopy.dds2.greed.gui;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.marketdepth.MarketDepthPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreePanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableQuoterPanel;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.math.BigDecimal;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class DealPanel extends JPanel
/*     */ {
/*     */   private MultiSplitPane multiSplitPane;
/*     */   private HeaderPanel headerPanel;
/*     */   private OrderEntryPanel orderPanel;
/*     */   private MarketDepthPanel marketDepthPanel;
/*     */   private WorkspacePanel workspacePanel;
/*     */   private WorkspaceTreePanel workspaceTreePanel;
/*     */   private MarketView marketView;
/*  46 */   private String instrument = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreLastSelectedInstrument();
/*     */   public static final String ORDER_ENTRY = "orderEntry";
/*     */   public static final String COND_ORDER_ENTRY = "conOrderEntry";
/*     */   public static final String MARKET_DEPTH = "marketDepth";
/*     */   public static final String TICKER = "ticker";
/*     */   public static final String WORKSPACE_TREE = "workspace";
/*     */ 
/*     */   public DealPanel()
/*     */   {
/*  59 */     this.orderPanel = new OrderEntryPanel(null, true);
/*     */   }
/*     */ 
/*     */   public void build() {
/*  63 */     setLayout(new BoxLayout(this, 1));
/*  64 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/*     */ 
/*  66 */     this.marketDepthPanel = new MarketDepthPanel();
/*     */ 
/*  68 */     setPreferredSize(this.orderPanel.getPreferredSize());
/*  69 */     setMaximumSize(new Dimension(getPreferredSize().width, 32767));
/*     */ 
/*  71 */     String title = this.instrument != null ? this.instrument : "Dealing";
/*  72 */     this.headerPanel = new HeaderPanel(title, true);
/*  73 */     add(this.headerPanel);
/*     */ 
/*  75 */     JPanel splitContainer = new JPanel(new BorderLayout());
/*     */ 
/*  77 */     this.multiSplitPane = new MultiSplitPane();
/*  78 */     this.multiSplitPane.add(this.orderPanel, "orderEntry");
/*  79 */     this.multiSplitPane.add(this.marketDepthPanel, "marketDepth");
/*  80 */     this.multiSplitPane.add(this.workspacePanel, "ticker");
/*  81 */     if (GreedContext.isStrategyAllowed()) this.multiSplitPane.add(this.workspaceTreePanel, "workspace");
/*     */ 
/*  83 */     this.multiSplitPane.setContinuousLayout(true);
/*  84 */     this.multiSplitPane.setBorder(BorderFactory.createEmptyBorder());
/*     */ 
/*  86 */     splitContainer.add(Box.createRigidArea(new Dimension(0, 20)));
/*  87 */     splitContainer.add(this.multiSplitPane, "Center");
/*  88 */     add(splitContainer);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  95 */     this.orderPanel.clear();
/*     */   }
/*     */ 
/*     */   private void updateTicker(String instrument, OfferSide side, BigDecimal price, BigDecimal amount)
/*     */   {
/* 107 */     if ((this.workspacePanel instanceof TickerPanel)) {
/* 108 */       TickerPanel tickerPanel = (TickerPanel)this.workspacePanel;
/*     */ 
/* 110 */       if (price != null)
/* 111 */         tickerPanel.update(instrument, side, price);
/*     */       else
/* 113 */         tickerPanel.update(instrument, side, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void refresh()
/*     */   {
/* 122 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/*     */ 
/* 124 */     String instrument = this.workspacePanel.getSelectedInstrument();
/*     */ 
/* 126 */     if (instrument != null) {
/* 127 */       if (!instrument.equals(this.orderPanel.getInstrument())) {
/* 128 */         this.orderPanel.clearEverything(false);
/* 129 */         this.orderPanel.setInstrument(instrument);
/*     */       }
/* 131 */       InstrumentStatusUpdateMessage instrumentState = this.marketView.getInstrumentState(instrument);
/* 132 */       if (instrumentState != null) {
/* 133 */         this.orderPanel.setSubmitEnabled(instrumentState.getTradable());
/* 134 */         this.orderPanel.getPriceQuoter().setTradable(instrumentState.getTradable() == 0);
/*     */       } else {
/* 136 */         this.orderPanel.setSubmitEnabled(1);
/* 137 */         this.orderPanel.getPriceQuoter().setTradable(false);
/*     */       }
/*     */ 
/* 140 */       setInstrumentTitle(instrument);
/* 141 */       CurrencyMarketWrapper currentMarketState = this.marketView.getLastMarketState(instrument);
/* 142 */       onMarketState(currentMarketState);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market)
/*     */   {
/* 151 */     if (market != null) {
/* 152 */       String instrument = market.getInstrument();
/* 153 */       CurrencyOffer bestBid = market.getBestOffer(OfferSide.BID);
/* 154 */       CurrencyOffer bestAsk = market.getBestOffer(OfferSide.ASK);
/* 155 */       BigDecimal bestBidPrice = bestBid != null ? bestBid.getPrice().getValue() : null;
/* 156 */       BigDecimal bestAskPrice = bestAsk != null ? bestAsk.getPrice().getValue() : null;
/* 157 */       BigDecimal bestBidAmount = bestBid != null ? bestBid.getAmount().getValue() : null;
/* 158 */       BigDecimal bestAskAmount = bestAsk != null ? bestAsk.getAmount().getValue() : null;
/* 159 */       updateTicker(instrument, OfferSide.BID, bestBidPrice, bestBidAmount);
/* 160 */       updateTicker(instrument, OfferSide.ASK, bestAskPrice, bestAskAmount);
/*     */ 
/* 162 */       if (instrument.equals(this.orderPanel.getInstrument())) {
/* 163 */         this.orderPanel.onMarketState(market);
/* 164 */         updateMarketDepth(market);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateMarketDepth(CurrencyMarketWrapper market)
/*     */   {
/* 171 */     if (GreedContext.isStrategyAllowed()) {
/* 172 */       this.marketDepthPanel.onMarketState(market);
/*     */     }
/* 174 */     else if (market.getInstrument().equals(this.workspacePanel.getSelectedInstrument()))
/* 175 */       this.marketDepthPanel.onMarketState(market);
/*     */   }
/*     */ 
/*     */   public void clearMessages()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getSelectedInstrument()
/*     */   {
/* 192 */     return this.workspacePanel.getSelectedInstrument();
/*     */   }
/*     */ 
/*     */   public void setInstrumentTitle(String title)
/*     */   {
/* 200 */     this.orderPanel.setInstrument(title);
/*     */   }
/*     */ 
/*     */   public OrderEntryPanel getOrderEntryPanel() {
/* 204 */     return this.orderPanel;
/*     */   }
/*     */ 
/*     */   public void updateTradability(InstrumentStatusUpdateMessage status) {
/* 208 */     this.workspacePanel.updateTradability(status);
/* 209 */     String instrument = status.getInstrument();
/* 210 */     if (instrument.equals(this.orderPanel.getInstrument())) {
/* 211 */       this.orderPanel.setSubmitEnabled(status.getTradable());
/* 212 */       this.orderPanel.getPriceQuoter().setTradable(status.getTradable() == 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MarketDepthPanel getMarketDepthPanel() {
/* 217 */     return this.marketDepthPanel;
/*     */   }
/*     */ 
/*     */   public OrderEntryPanel getOrderPanel() {
/* 221 */     return this.orderPanel;
/*     */   }
/*     */ 
/*     */   public WorkspacePanel getWorkspacePanel() {
/* 225 */     return this.workspacePanel;
/*     */   }
/*     */ 
/*     */   public void setWorkspacePanel(WorkspacePanel tickerTreePanel) {
/* 229 */     this.workspacePanel = tickerTreePanel;
/*     */   }
/*     */ 
/*     */   public WorkspaceTreePanel getWorkspaceTreePanel() {
/* 233 */     return this.workspaceTreePanel;
/*     */   }
/*     */ 
/*     */   public void setWorkspaceTreePanel(WorkspaceTreePanel workspaceTreePanel) {
/* 237 */     this.workspaceTreePanel = workspaceTreePanel;
/*     */   }
/*     */   public HeaderPanel getHeaderPanel() {
/* 240 */     return this.headerPanel;
/*     */   }
/*     */ 
/*     */   public void setHeaderPanel(HeaderPanel headerPanel) {
/* 244 */     this.headerPanel = headerPanel;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.DealPanel
 * JD-Core Version:    0.6.0
 */