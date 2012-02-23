/*     */ package com.dukascopy.dds2.greed.gui.component.detached;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.BasicDecoratedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.PriceSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.ConditionalOrderEntryPanel.DependandHeightButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableQuoterPanel;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountLabel;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.AmountJSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.window.WindowManager;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketStateWrapperListener;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.QuickieOrderSupport;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Point;
/*     */ import java.math.BigDecimal;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class IfDoneDialog extends BasicDecoratedFrame
/*     */   implements MarketStateWrapperListener, QuickieOrderSupport
/*     */ {
/*     */   private final JLocalizableQuoterPanel quoter;
/*  47 */   private final LotAmountLabel amountLabel = new LotAmountLabel();
/*  48 */   private final AmountJSpinner amountSpinner = AmountJSpinner.getInstance();
/*     */ 
/*  50 */   private JPanel content = new JPanel();
/*     */   private JPanel topPanel;
/*     */   private JPanel bottomPanel;
/*  55 */   private final JLocalizableCheckBox entryCheckBox = new JLocalizableCheckBox("check.entry");
/*  56 */   private final PriceSpinner entryPriceField = new PriceSpinner();
/*     */   private ConditionalOrderEntryPanel.DependandHeightButton entryStopDirectionButton;
/*     */   private final String instrument;
/*  62 */   private StopDirection entryStopDirection = null;
/*  63 */   private StopDirection ifDoneStopStopDirection = null;
/*  64 */   private StopDirection isDoneLimitStopDirection = null;
/*     */ 
/*  67 */   private final ClientSettingsStorage settingsSaver = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*  68 */   private final MarketView marketView = (MarketView)GreedContext.get("marketView");
/*  69 */   private final ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/*     */ 
/*  71 */   public IfDoneDialog(String instrument) { this.instrument = instrument;
/*  72 */     this.quoter = new JLocalizableQuoterPanel(instrument, this);
/*     */ 
/*  74 */     setParams(new String[] { instrument, GreedContext.CLIENT_MODE });
/*  75 */     setTitle("frame.detached.title");
/*     */ 
/*  77 */     this.gui.addMarketWatcher(this);
/*     */ 
/*  79 */     build();
/*     */ 
/*  81 */     pack();
/*  82 */     setAlwaysOnTop(true);
/*  83 */     setResizable(false); }
/*     */ 
/*     */   private void build()
/*     */   {
/*  87 */     this.content = new JPanel();
/*  88 */     this.content.setLayout(new BoxLayout(this.content, 1));
/*     */ 
/*  90 */     buildTop();
/*  91 */     buildBottom();
/*     */ 
/*  93 */     this.content.add(this.topPanel);
/*  94 */     this.content.add(this.bottomPanel);
/*     */ 
/*  96 */     setContentPane(this.content);
/*     */   }
/*     */ 
/*     */   private void buildTop() {
/* 100 */     this.topPanel = new JPanel();
/* 101 */     this.topPanel.setLayout(new BoxLayout(this.topPanel, 1));
/*     */ 
/* 103 */     JPanel amountForm = new JPanel(new GridLayout(1, 2, 5, 2));
/* 104 */     JPanel spacer = new JPanel();
/* 105 */     spacer.setLayout(new BoxLayout(spacer, 0));
/* 106 */     spacer.add(Box.createHorizontalStrut(6));
/* 107 */     spacer.add(this.amountLabel);
/* 108 */     amountForm.add(spacer);
/* 109 */     amountForm.add(this.amountSpinner);
/*     */ 
/* 111 */     this.topPanel.add(new HeaderPanel(this.instrument, true));
/* 112 */     this.topPanel.add(Box.createVerticalStrut(4));
/* 113 */     this.topPanel.add(this.quoter);
/* 114 */     this.topPanel.add(Box.createVerticalStrut(4));
/* 115 */     this.topPanel.add(amountForm);
/*     */   }
/*     */ 
/*     */   private void buildBottom() {
/* 119 */     this.bottomPanel = new JPanel();
/* 120 */     this.bottomPanel.setLayout(new BoxLayout(this.bottomPanel, 1));
/* 121 */     this.bottomPanel.add(createConditionalPanel());
/*     */   }
/*     */ 
/*     */   private JPanel createConditionalPanel() {
/* 125 */     JPanel conditionalPanel = new JPanel();
/*     */ 
/* 127 */     conditionalPanel.setOpaque(false);
/*     */ 
/* 129 */     conditionalPanel.setLayout(new GridBagLayout());
/* 130 */     GridBagConstraints gbc = new GridBagConstraints();
/*     */ 
/* 132 */     int gridY = 0;
/*     */ 
/* 134 */     JLocalizableLabel sideLabel = new JLocalizableLabel("label.side");
/*     */ 
/* 165 */     return null;
/*     */   }
/*     */ 
/*     */   public void display() {
/* 169 */     pack();
/* 170 */     setDefaultCloseOperation(2);
/* 171 */     Point location = this.settingsSaver.restoreDetachedLocation(this.instrument);
/* 172 */     if (null != location) {
/* 173 */       setLocation(location);
/*     */     } else {
/* 175 */       WindowManager windowManager = (WindowManager)GreedContext.get("windowManager");
/* 176 */       windowManager.layout(this);
/*     */     }
/* 178 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market) {
/* 182 */     if ((this.instrument == null) || (market == null)) {
/* 183 */       return;
/*     */     }
/* 185 */     if (this.instrument.equals(market.getInstrument())) {
/* 186 */       InstrumentStatusUpdateMessage instrumentState = this.marketView.getInstrumentState(this.instrument);
/* 187 */       if (instrumentState != null)
/* 188 */         setSubmitEnabled(instrumentState.getTradable());
/*     */       else {
/* 190 */         setSubmitEnabled(1);
/*     */       }
/* 192 */       this.quoter.onTick(Instrument.fromString(this.instrument));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setSubmitEnabled(int tradability)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void quickieOrder(String instrument, OrderSide side)
/*     */   {
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmount()
/*     */   {
/* 208 */     return (BigDecimal)this.amountSpinner.getValue();
/*     */   }
/*     */ 
/*     */   public String getSlippageAmount()
/*     */   {
/* 214 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.detached.IfDoneDialog
 * JD-Core Version:    0.6.0
 */