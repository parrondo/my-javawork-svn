/*     */ package com.dukascopy.dds2.greed.gui.component.detached;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.BasicDecoratedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.marketdepth.MarketDepthPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.window.WindowManager;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketStateWrapperListener;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.ParentWatcher;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class OrderEntryDetached extends BasicDecoratedFrame
/*     */   implements MarketStateWrapperListener
/*     */ {
/*     */   private ClientForm gui;
/*     */   private OrderEntryPanel orderEntryPanel;
/*     */   private MarketDepthPanel marketDepthPanel;
/*     */   private String instrument;
/*     */   private ParentWatcher parentWatcher;
/*  40 */   private MarketView marketView = null;
/*     */   private ClientSettingsStorage settingsSaver;
/*     */   private Point location;
/*     */ 
/*     */   public OrderEntryDetached(String instrument)
/*     */   {
/*  45 */     this.settingsSaver = ((ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*  46 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/*     */ 
/*  48 */     setAlwaysOnTop(true);
/*  49 */     setResizable(false);
/*  50 */     String modeKey = GreedContext.CLIENT_MODE;
/*     */ 
/*  52 */     setParams(new String[] { instrument, modeKey });
/*  53 */     setTitle("frame.detached.title");
/*     */ 
/*  55 */     this.gui = ((ClientForm)GreedContext.get("clientGui"));
/*  56 */     this.instrument = instrument;
/*     */ 
/*  58 */     this.orderEntryPanel = new OrderEntryPanel(this.instrument, true);
/*  59 */     this.orderEntryPanel.setSubmitEnabled(0);
/*  60 */     this.orderEntryPanel.setStopOrdersVisible((!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended()));
/*  61 */     this.orderEntryPanel.setDefaultStopConditionLabels();
/*  62 */     this.marketDepthPanel = new MarketDepthPanel();
/*     */ 
/*  64 */     JPanel content = new JPanel();
/*     */ 
/*  66 */     content.setLayout(new GridBagLayout());
/*  67 */     GridBagConstraints c = new GridBagConstraints();
/*  68 */     c.weightx = 1.0D;
/*  69 */     c.anchor = 11;
/*  70 */     int gridY = 0;
/*     */ 
/*  72 */     c.weighty = 0.0D;
/*  73 */     c.gridx = 0; c.gridy = (gridY++);
/*  74 */     c.fill = 0;
/*  75 */     content.add(this.orderEntryPanel, c);
/*     */ 
/*  78 */     c.fill = 2;
/*  79 */     c.gridy = (gridY++);
/*     */ 
/*  81 */     c.weighty = 1.0D;
/*  82 */     c.gridy = (gridY++);
/*  83 */     c.fill = 1;
/*  84 */     content.add(this.marketDepthPanel, c);
/*     */ 
/*  86 */     setContentPane(content);
/*  87 */     OrderEntryDetached detachedFrame = this;
/*  88 */     this.gui.addMarketWatcher(detachedFrame);
/*  89 */     addWindowListener(new WindowAdapter(detachedFrame, instrument) {
/*     */       public void windowClosed(WindowEvent e) {
/*  91 */         OrderEntryDetached.this.gui.removeMarketWatcher(this.val$detachedFrame);
/*  92 */         if (OrderEntryDetached.this.parentWatcher != null) OrderEntryDetached.this.parentWatcher.onNestedFrameClose(this.val$detachedFrame); 
/*     */       }
/*     */ 
/*     */       public void windowClosing(WindowEvent e)
/*     */       {
/*  96 */         OrderEntryDetached.this.settingsSaver.saveDetachedLocation(this.val$instrument, OrderEntryDetached.this.location);
/*     */       }
/*     */ 
/*     */       public void windowIconified(WindowEvent e) {
/* 100 */         OrderEntryDetached.this.settingsSaver.saveDetachedLocation(this.val$instrument, OrderEntryDetached.this.location);
/*     */       }
/*     */     });
/* 104 */     addComponentListener(new ComponentAdapter() {
/*     */       public void componentMoved(ComponentEvent e) {
/* 106 */         OrderEntryDetached.access$202(OrderEntryDetached.this, OrderEntryDetached.this.getLocationOnScreen());
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public void setParentWatcher(ParentWatcher parentWatcher) {
/* 112 */     this.parentWatcher = parentWatcher;
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/* 116 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public void setHedged(boolean isHedged)
/*     */   {
/* 121 */     this.orderEntryPanel.setHedged(isHedged);
/*     */   }
/*     */ 
/*     */   public void setAmount(String amount)
/*     */   {
/* 126 */     this.orderEntryPanel.setAmount(amount);
/*     */   }
/*     */ 
/*     */   public void setSlippage(String slippage)
/*     */   {
/* 131 */     this.orderEntryPanel.setSlippage(slippage);
/*     */   }
/*     */ 
/*     */   public void display() {
/* 135 */     pack();
/* 136 */     setDefaultCloseOperation(2);
/* 137 */     Point location = this.settingsSaver.restoreDetachedLocation(this.instrument);
/* 138 */     if (null != location) {
/* 139 */       setLocation(location);
/*     */     } else {
/* 141 */       WindowManager windowManager = (WindowManager)GreedContext.get("windowManager");
/* 142 */       windowManager.layout(this);
/*     */     }
/* 144 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market) {
/* 148 */     if ((this.instrument == null) || (market == null)) {
/* 149 */       return;
/*     */     }
/*     */ 
/* 152 */     if (this.instrument.equals(market.getInstrument())) {
/* 153 */       this.marketDepthPanel.onMarketState(market);
/* 154 */       this.orderEntryPanel.onMarketState(market);
/*     */ 
/* 156 */       InstrumentStatusUpdateMessage instrumentState = this.marketView.getInstrumentState(this.instrument);
/* 157 */       if (instrumentState != null)
/* 158 */         this.orderEntryPanel.setSubmitEnabled(instrumentState.getTradable());
/*     */       else
/* 160 */         this.orderEntryPanel.setSubmitEnabled(1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public OrderEntryPanel getOrderEntryPanel()
/*     */   {
/* 166 */     return this.orderEntryPanel;
/*     */   }
/*     */ 
/*     */   public void setOrderEntryPanel(OrderEntryPanel orderEntryPanel) {
/* 170 */     this.orderEntryPanel = orderEntryPanel;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 174 */     OrderEntryDetached panel = new OrderEntryDetached("EUR/USD");
/* 175 */     panel.display();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.detached.OrderEntryDetached
 * JD-Core Version:    0.6.0
 */