/*     */ package com.dukascopy.dds2.greed.gui.component.orders;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.FullDepthInstrumentSubscribeAction;
/*     */ import com.dukascopy.dds2.greed.actions.OrderEntryAction;
/*     */ import com.dukascopy.dds2.greed.actions.PostMessageAction;
/*     */ import com.dukascopy.dds2.greed.actions.UpdateGuiDefaultsAction;
/*     */ import com.dukascopy.dds2.greed.agent.DDSAgent;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.MessageBox;
/*     */ import com.dukascopy.dds2.greed.gui.component.MouseController;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.OrderConfirmationDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder.OrderValidationBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitable;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableQuoterPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountLabel;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.AmountJSpinner;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import com.dukascopy.dds2.greed.util.QuickieOrderSupport;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.RenderingHints;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JLayeredPane;
/*     */ import javax.swing.JPanel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrderEntryPanel extends JPanel
/*     */   implements QuickieOrderSupport, PlatformSpecific, MultiSplitable
/*     */ {
/*  86 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrderEntryPanel.class);
/*     */   private final JLocalizableQuoterPanel quoter;
/*     */   private CustomRequestPanel customRequest;
/*     */   private ConditionalOrderEntryPanel conditionalOrderEntryPanel;
/*     */   private static final int INSET = 3;
/*     */   private static final int WIDTH = 256;
/*     */   private static final int HEIGHT = 193;
/*     */   private static final int MIN_HEIGHT = 30;
/*     */   private JPanel orderForm;
/*  98 */   private JPanel noTradeAlertPanel = new JPanel(new GridLayout(1, 1, 5, 5)) {
/*     */     protected void paintComponent(Graphics g) {
/* 100 */       super.paintComponent(g);
/* 101 */       if ((isVisible()) && (OrderEntryPanel.this.isExpanded())) {
/* 102 */         Graphics2D g2d = (Graphics2D)g;
/* 103 */         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/* 107 */         g2d.setComposite(AlphaComposite.getInstance(3, 0.8F));
/* 108 */         g2d.setColor(Color.WHITE);
/* 109 */         g2d.fillRoundRect(10, 20, 237, OrderEntryPanel.this.getLayeredPaneH() - 85, 10, 10);
/*     */ 
/* 111 */         g2d.setComposite(AlphaComposite.getInstance(3, 0.8F));
/* 112 */         g2d.setColor(Color.GRAY);
/* 113 */         g2d.setStroke(new BasicStroke(1.5F));
/* 114 */         g2d.drawRoundRect(10, 20, 237, OrderEntryPanel.this.getLayeredPaneH() - 85, 10, 10);
/*     */       }
/*     */     }
/*  98 */   };
/*     */   private JLabel alertText;
/* 120 */   private LotAmountLabel amountLabel = new LotAmountLabel();
/*     */   private AmountJSpinner amountSpinner;
/* 123 */   private String instrument = "EUR/USD";
/*     */   private OrderConfirmationDialog previewDialog;
/*     */   private MessageBox warningsPanel;
/* 128 */   private ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/* 130 */   private JPanel gridPanel = new JPanel();
/* 131 */   private JPanel innerPanel = new JPanel();
/* 132 */   private JPanel quotesPanel = new JPanel();
/*     */   private JLocalizableRoundedBorder borderEntry;
/* 136 */   public boolean doAction = true;
/*     */ 
/*     */   public OrderEntryPanel(String selectedInstrument_, boolean big)
/*     */   {
/* 144 */     String selectedInstrument = (selectedInstrument_ != null) && (Instrument.fromString(selectedInstrument_) != null) ? selectedInstrument_ : this.storage.restoreLastSelectedInstrument();
/*     */ 
/* 147 */     this.amountSpinner = AmountJSpinner.getInstance(Instrument.fromString(selectedInstrument));
/* 148 */     this.quoter = new JLocalizableQuoterPanel(selectedInstrument, this);
/* 149 */     this.customRequest = new CustomRequestPanel(this.instrument);
/* 150 */     this.conditionalOrderEntryPanel = new ConditionalOrderEntryPanel(Instrument.fromString(this.instrument), this.quoter)
/*     */     {
/*     */       protected BigDecimal getAmount() {
/* 153 */         return OrderEntryPanel.this.amountSpinner.getAmountValueInMillions(Instrument.fromString(OrderEntryPanel.this.instrument));
/*     */       }
/*     */     };
/* 156 */     this.conditionalOrderEntryPanel.setSubmitActionListener(new SubmitActionListener()
/*     */     {
/*     */       public void submitButtonPressed(OrderGroupMessage ogm) {
/* 159 */         OrderEntryPanel.this.doSubmit(ogm);
/*     */       }
/*     */     });
/* 163 */     build();
/*     */ 
/* 165 */     if (null != selectedInstrument) {
/* 166 */       this.instrument = selectedInstrument;
/* 167 */       setInstrumentOnInit(this.instrument);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ClientSettingsStorage getStorage()
/*     */   {
/* 173 */     return this.storage;
/*     */   }
/*     */ 
/*     */   public void setStorage(ClientSettingsStorage storage) {
/* 177 */     this.storage = storage;
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market)
/*     */   {
/* 182 */     if ((market == null) || (market.getInstrument() == null)) {
/* 183 */       return;
/*     */     }
/* 185 */     if (market.getInstrument().equals(this.instrument)) {
/* 186 */       this.quoter.onTick(Instrument.fromString(this.instrument));
/* 187 */       if (this.quoter.isTradingPossible()) {
/* 188 */         this.quoter.setTradable(true);
/*     */       }
/*     */     }
/*     */ 
/* 192 */     if ((this.previewDialog != null) && (this.previewDialog.isVisible()))
/* 193 */       this.previewDialog.onMarketState(market);
/*     */   }
/*     */ 
/*     */   public void clearEverything(boolean leaveSide)
/*     */   {
/* 203 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/*     */ 
/* 205 */     this.quoter.setTradable(true);
/*     */ 
/* 207 */     if (this.quoter.isTradingPossible()) {
/* 208 */       this.quoter.setTradable(true);
/*     */     }
/*     */ 
/* 211 */     this.conditionalOrderEntryPanel.clearEverything(leaveSide);
/*     */   }
/*     */ 
/*     */   private void build()
/*     */   {
/* 219 */     setLayout(new BoxLayout(this, 1));
/* 220 */     this.borderEntry = new JLocalizableRoundedBorder(this.gridPanel, "header.order.entry", true);
/* 221 */     this.borderEntry.setRightInset(7);
/* 222 */     this.borderEntry.setLeftInset(9);
/* 223 */     this.borderEntry.setBottomInset(12);
/*     */ 
/* 225 */     this.gridPanel.setLayout(new BoxLayout(this.gridPanel, 1));
/* 226 */     this.gridPanel.setBorder(this.borderEntry);
/*     */ 
/* 228 */     this.innerPanel.setLayout(new BoxLayout(this.innerPanel, 1));
/*     */ 
/* 230 */     this.quoter.setTradable((!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended()));
/*     */ 
/* 232 */     this.quotesPanel.setLayout(new BoxLayout(this.quotesPanel, 1));
/*     */ 
/* 234 */     this.quotesPanel.add(Box.createRigidArea(new Dimension(0, 2)));
/* 235 */     this.quotesPanel.add(this.quoter);
/* 236 */     this.quotesPanel.add(Box.createVerticalStrut(4));
/*     */ 
/* 239 */     this.quotesPanel.add(this.customRequest);
/*     */ 
/* 241 */     this.quotesPanel.add(Box.createRigidArea(new Dimension(0, 6)));
/*     */ 
/* 243 */     this.amountSpinner.setHorizontalAlignment(4);
/* 244 */     this.amountSpinner.setMinimum(LotAmountChanger.getMinTradableAmount());
/*     */ 
/* 246 */     if (MACOSX) {
/* 247 */       this.amountLabel.putClientProperty("JComponent.sizeVariant", "small");
/*     */     }
/* 249 */     if ((LINUX) || (MACOSX)) {
/* 250 */       this.amountSpinner.putClientProperty("JComponent.sizeVariant", "small");
/*     */     }
/* 252 */     this.orderForm = new JPanel(new GridLayout(1, 2, 5, 2));
/* 253 */     JPanel spacer = new JPanel();
/* 254 */     spacer.setLayout(new BoxLayout(spacer, 0));
/* 255 */     spacer.add(Box.createHorizontalStrut(6));
/* 256 */     spacer.add(this.amountLabel);
/* 257 */     this.orderForm.add(spacer);
/* 258 */     this.orderForm.add(this.amountSpinner);
/* 259 */     this.quotesPanel.add(this.orderForm);
/*     */ 
/* 262 */     this.innerPanel.setMinimumSize(new Dimension(256, getLayeredPaneH()));
/* 263 */     this.innerPanel.add(this.quotesPanel);
/*     */ 
/* 266 */     this.alertText = new JLabel();
/*     */ 
/* 268 */     this.alertText.setText(LocalizationManager.getText("trade.restricted"));
/* 269 */     this.alertText.setPreferredSize(new Dimension());
/* 270 */     this.alertText.setFont(this.amountSpinner.getFont());
/* 271 */     this.alertText.setBackground(this.noTradeAlertPanel.getBackground());
/*     */ 
/* 273 */     if (MACOSX) {
/* 274 */       this.alertText.putClientProperty("JComponent.sizeVariant", "small");
/*     */     }
/*     */ 
/* 277 */     this.noTradeAlertPanel.add(this.alertText);
/* 278 */     this.noTradeAlertPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 40, 20));
/* 279 */     this.noTradeAlertPanel.setOpaque(false);
/*     */ 
/* 281 */     this.quotesPanel.add(this.noTradeAlertPanel);
/* 282 */     this.noTradeAlertPanel.setVisible(false);
/*     */ 
/* 287 */     this.warningsPanel = new MessageBox();
/* 288 */     this.warningsPanel.setVisible(false);
/* 289 */     this.quotesPanel.add(this.warningsPanel);
/*     */ 
/* 291 */     this.gridPanel.add(Box.createHorizontalStrut(this.innerPanel.getPreferredSize().width + 10));
/* 292 */     this.gridPanel.add(this.innerPanel);
/* 293 */     add(Box.createRigidArea(new Dimension(0, 3)));
/*     */ 
/* 295 */     JLayeredPane layeredPane = new JLayeredPane();
/* 296 */     layeredPane.setOpaque(false);
/*     */ 
/* 298 */     layeredPane.add(this.gridPanel, new Integer(0), 0);
/* 299 */     layeredPane.add(this.noTradeAlertPanel, new Integer(1), 1);
/*     */ 
/* 301 */     layeredPane.setPreferredSize(new Dimension(256, 193));
/*     */ 
/* 303 */     add(layeredPane);
/* 304 */     add(this.conditionalOrderEntryPanel);
/*     */ 
/* 306 */     this.gridPanel.setBounds(0, 0, 256, 193);
/* 307 */     this.noTradeAlertPanel.setBounds(0, 0, 256, 193);
/*     */ 
/* 309 */     MouseController mController = new MouseController(this);
/* 310 */     addMouseMotionListener(mController);
/* 311 */     addMouseListener(mController);
/*     */ 
/* 313 */     if (GreedContext.isContest()) {
/* 314 */       this.customRequest.setVisible(false);
/*     */     }
/* 316 */     this.borderEntry.setSwitch(this.innerPanel.isVisible());
/*     */   }
/*     */ 
/*     */   public void quickieOrder(String instrument, OrderSide side)
/*     */   {
/* 321 */     if (null == instrument) return;
/* 322 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 323 */     if (!this.amountSpinner.validateEditor()) {
/* 324 */       return;
/*     */     }
/*     */ 
/* 327 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 328 */     OfferSide offerSide = side == OrderSide.BUY ? OfferSide.ASK : OfferSide.BID;
/* 329 */     CurrencyOffer bestOffer = marketView.getBestOffer(instrument, offerSide);
/*     */     Money bestPrice;
/* 332 */     if ((bestOffer != null) && (bestOffer.getPrice() != null)) {
/* 333 */       bestPrice = bestOffer.getPrice();
/*     */     } else {
/* 335 */       Notification notification = new Notification(null, "No liquidity for " + side.asString() + " " + instrument + "!");
/* 336 */       notification.setPriority("WARNING");
/* 337 */       PostMessageAction post = new PostMessageAction(this, notification);
/* 338 */       GreedContext.publishEvent(post);
/* 339 */       return;
/*     */     }
/*     */     Money bestPrice;
/* 342 */     String[] currencies = instrument.split("/");
/*     */ 
/* 344 */     OrderGroupMessage orderGroup = new OrderGroupMessage();
/* 345 */     orderGroup.setTimestamp(new Date());
/* 346 */     orderGroup.setInstrument(instrument);
/*     */ 
/* 348 */     OrderMessage openingOrder = new OrderMessage();
/* 349 */     openingOrder.setOrderGroupId(orderGroup.getOrderGroupId());
/* 350 */     openingOrder.setInstrument(instrument);
/* 351 */     openingOrder.setOrderDirection(OrderDirection.OPEN);
/* 352 */     openingOrder.setSide(side);
/* 353 */     openingOrder.setPriceClient(bestPrice);
/* 354 */     if (LOGGER.isDebugEnabled())
/* 355 */       LOGGER.debug("priceClient:" + openingOrder.getPriceClient());
/*     */     BigDecimal amountValue;
/*     */     try {
/* 359 */       amountValue = this.amountSpinner.getAmountValueInMillions(Instrument.fromString(instrument)).multiply(GuiUtilsAndConstants.ONE_MILLION);
/*     */     } catch (Exception e) {
/* 361 */       LOGGER.error(e.getMessage());
/* 362 */       return;
/*     */     }
/* 364 */     Money amount = new Money(amountValue, Currency.getInstance(currencies[0]));
/*     */ 
/* 366 */     if (this.storage.restoreApplySlippageToAllMarketOrders()) {
/*     */       try {
/* 368 */         BigDecimal trailingLimitValue = this.storage.restoreDefaultSlippage().multiply(BigDecimal.valueOf(Instrument.fromString(instrument).getPipValue()));
/* 369 */         Money trailingLimit = new Money(trailingLimitValue, Currency.getInstance(currencies[1]));
/*     */ 
/* 371 */         openingOrder.setPriceTrailingLimit(trailingLimit);
/*     */       }
/*     */       catch (Exception e) {
/* 374 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */ 
/* 378 */     openingOrder.setAmount(amount);
/*     */ 
/* 380 */     if (openingOrder.getExternalSysId() == null) {
/* 381 */       String tagForOrder = DDSAgent.generateLabel(openingOrder);
/* 382 */       openingOrder.setExternalSysId(tagForOrder);
/*     */     }
/* 384 */     if (openingOrder.getSignalId() == null) {
/* 385 */       String tagForOrder = DDSAgent.generateLabel(openingOrder);
/* 386 */       openingOrder.setSignalId(tagForOrder);
/*     */     }
/*     */ 
/* 389 */     List orders = new ArrayList();
/* 390 */     orders.add(openingOrder);
/*     */ 
/* 392 */     OrderUtils.addDefaultStopLossAndTakeProfitToMarketGroup(openingOrder, orders);
/*     */ 
/* 394 */     orderGroup.setOrders(orders);
/*     */ 
/* 399 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 400 */     if (gui.getStatusBar().getAccountStatement().getOneClickCheckbox().isSelected()) {
/* 401 */       fireOrderEntryAction(orderGroup, true);
/*     */     } else {
/* 403 */       this.previewDialog = new OrderConfirmationDialog(orderGroup, gui);
/* 404 */       this.previewDialog.addPreviewInfo(side.value, orderGroup);
/* 405 */       this.previewDialog.onMarketState(marketView.getLastMarketState(instrument));
/* 406 */       this.previewDialog.setDeferedTradeLog(true);
/* 407 */       this.previewDialog.setVisible(true);
/* 408 */       if (this.previewDialog.isOrderSubmited())
/* 409 */         clearEverything(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void doSubmit(OrderGroupMessage orderGroup)
/*     */   {
/* 415 */     if (!this.amountSpinner.validateEditor()) {
/* 416 */       return;
/*     */     }
/* 418 */     if (this.instrument != null)
/*     */     {
/* 420 */       MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 421 */       OrderSide side = orderGroup.getOpeningOrder().getSide();
/* 422 */       OfferSide offerSide = side == OrderSide.BUY ? OfferSide.ASK : OfferSide.BID;
/*     */ 
/* 424 */       CurrencyOffer bestOffer = marketView.getBestOffer(this.instrument, offerSide);
/* 425 */       if ((bestOffer == null) || (bestOffer.getPrice() == null)) {
/* 426 */         Notification notification = new Notification(null, "No liquidity for " + side.asString() + " " + this.instrument + "!");
/* 427 */         notification.setPriority("WARNING");
/* 428 */         PostMessageAction post = new PostMessageAction(this, notification);
/* 429 */         GreedContext.publishEvent(post);
/* 430 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 434 */         Money amount = new Money(this.amountSpinner.getAmountValueInMillions(Instrument.fromString(this.instrument)).multiply(GuiUtilsAndConstants.ONE_MILLION), Instrument.fromString(this.instrument).getPrimaryCurrency());
/*     */ 
/* 436 */         for (OrderMessage order : orderGroup.getOrders()) {
/* 437 */           order.setAmount(amount);
/*     */         }
/*     */ 
/* 440 */         orderGroup.getOpeningOrder().setPriceClient(bestOffer.getPrice());
/*     */ 
/* 442 */         ValidateOrder.OrderValidationBean result = ValidateOrder.getInstance().validateOrder(orderGroup);
/*     */ 
/* 444 */         ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*     */ 
/* 446 */         OrderConfirmationDialog validationDialog = new OrderConfirmationDialog(orderGroup, result, clientForm, false, false);
/*     */ 
/* 448 */         this.previewDialog = new OrderConfirmationDialog(orderGroup, clientForm);
/*     */ 
/* 450 */         if (clientForm.getStatusBar().getAccountStatement().getOneClickCheckbox().isSelected())
/*     */         {
/* 453 */           ClientSettingsStorage settings = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 454 */           if (settings.restoreOrderValidationOn()) {
/* 455 */             if (!result.getMessages().isEmpty())
/*     */             {
/* 457 */               validationDialog.onMarketState(marketView.getLastMarketState(this.instrument));
/* 458 */               validationDialog.addPreviewInfo(this.conditionalOrderEntryPanel.getSelectedSide(), orderGroup);
/* 459 */               validationDialog.setLocationRelativeTo(this.conditionalOrderEntryPanel);
/* 460 */               validationDialog.setDeferedTradeLog(false);
/* 461 */               validationDialog.setVisible(true);
/* 462 */               if (validationDialog.isOrderSubmited())
/* 463 */                 clearEverything(true);
/*     */             }
/*     */             else {
/* 466 */               fireOrderEntryAction(orderGroup, false);
/*     */             }
/*     */           }
/* 469 */           else fireOrderEntryAction(orderGroup, false);
/*     */         }
/*     */         else
/*     */         {
/* 473 */           ClientSettingsStorage settings = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 474 */           if (settings.restoreOrderValidationOn()) {
/* 475 */             if (!result.getMessages().isEmpty())
/*     */             {
/* 477 */               validationDialog.addPreviewInfo(this.conditionalOrderEntryPanel.getSelectedSide(), orderGroup);
/* 478 */               validationDialog.onMarketState(marketView.getLastMarketState(this.instrument));
/* 479 */               validationDialog.setLocationRelativeTo(this.conditionalOrderEntryPanel);
/* 480 */               validationDialog.setDeferedTradeLog(false);
/* 481 */               validationDialog.setVisible(true);
/* 482 */               if (validationDialog.isOrderSubmited())
/* 483 */                 clearEverything(true);
/*     */             }
/*     */             else
/*     */             {
/* 487 */               this.previewDialog.onMarketState(marketView.getLastMarketState(this.instrument));
/* 488 */               this.previewDialog.addPreviewInfo(this.conditionalOrderEntryPanel.getSelectedSide(), orderGroup);
/* 489 */               this.previewDialog.setLocationRelativeTo(this.conditionalOrderEntryPanel);
/* 490 */               this.previewDialog.setDeferedTradeLog(false);
/*     */ 
/* 492 */               if (this.previewDialog.isOrderSubmited()) {
/* 493 */                 clearEverything(true);
/*     */               }
/*     */ 
/* 496 */               this.previewDialog.setVisible(true);
/*     */             }
/*     */           }
/*     */           else {
/* 500 */             this.previewDialog.onMarketState(marketView.getLastMarketState(this.instrument));
/* 501 */             this.previewDialog.addPreviewInfo(this.conditionalOrderEntryPanel.getSelectedSide(), orderGroup);
/* 502 */             this.previewDialog.setLocationRelativeTo(this.conditionalOrderEntryPanel);
/* 503 */             this.previewDialog.setDeferedTradeLog(false);
/* 504 */             this.previewDialog.setVisible(true);
/* 505 */             if (this.previewDialog.isOrderSubmited()) {
/* 506 */               clearEverything(true);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException ex)
/*     */       {
/* 513 */         LOGGER.warn("parsing: " + ex.getMessage());
/*     */       }
/*     */     } else {
/* 516 */       String sNoInstr = LocalizationManager.getText("oep.no.instr");
/* 517 */       StringBuffer sb = new StringBuffer(LocalizationManager.getText("oep.correct.following.errors")).append(null == this.instrument ? sNoInstr : "");
/*     */ 
/* 519 */       this.warningsPanel.setTextAndStartTimer(sb.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireOrderEntryAction(OrderGroupMessage orderGroup, boolean defered) {
/* 524 */     PlatformInitUtils.setExtSysIdForOrderGroup(orderGroup);
/* 525 */     OrderEntryAction orderEntryAction = new OrderEntryAction(this, orderGroup, defered);
/* 526 */     GreedContext.publishEvent(orderEntryAction);
/* 527 */     clearEverything(true);
/*     */   }
/*     */ 
/*     */   public String getSlippageAmount() {
/* 531 */     BigDecimal slippage = this.conditionalOrderEntryPanel.getSlippage();
/* 532 */     return slippage == null ? null : slippage.toPlainString();
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmount()
/*     */   {
/*     */     try
/*     */     {
/* 542 */       return this.amountSpinner.getAmountValueInMillions(Instrument.fromString(this.instrument)); } catch (NumberFormatException e) {
/*     */     }
/* 544 */     return null;
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/* 549 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public JLocalizableQuoterPanel getPriceQuoter() {
/* 553 */     return this.quoter;
/*     */   }
/*     */ 
/*     */   public void setAmount(String amount)
/*     */     throws NumberFormatException
/*     */   {
/* 562 */     if (!LotAmountChanger.isSelectedInstrCommodity())
/* 563 */       this.amountSpinner.setValue(new BigDecimal(amount));
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/* 567 */     this.instrument = instrument;
/* 568 */     setInstrumentTitle(instrument);
/* 569 */     this.customRequest.setInstrument(instrument);
/* 570 */     this.quoter.setInstrument(Instrument.fromString(instrument));
/* 571 */     this.conditionalOrderEntryPanel.setInstrument(Instrument.fromString(instrument));
/*     */ 
/* 573 */     this.amountSpinner.changeLot();
/* 574 */     this.amountLabel.changeLot();
/*     */ 
/* 576 */     FullDepthInstrumentSubscribeAction action = new FullDepthInstrumentSubscribeAction(this);
/* 577 */     GreedContext.publishEvent(action);
/*     */ 
/* 579 */     UpdateGuiDefaultsAction.updateTablesData();
/*     */   }
/*     */ 
/*     */   public void setInstrumentOnInit(String instrument) {
/* 583 */     this.instrument = instrument;
/* 584 */     this.customRequest.setInstrument(instrument);
/* 585 */     this.quoter.setInstrument(Instrument.fromString(instrument));
/* 586 */     this.conditionalOrderEntryPanel.setInstrument(Instrument.fromString(instrument));
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*     */   }
/*     */ 
/*     */   private void setInstrumentTitle(String title)
/*     */   {
/* 602 */     if ((title == null) || (title.trim().length() <= 0)) {
/* 603 */       LOGGER.warn("Empty instrument is set!");
/*     */     }
/* 605 */     DealPanel dp = ((ClientForm)GreedContext.get("clientGui")).getDealPanel();
/* 606 */     dp.getHeaderPanel().setTitle(title);
/*     */   }
/*     */ 
/*     */   public void setSubmitEnabled(int state)
/*     */   {
/* 617 */     this.conditionalOrderEntryPanel.setSubmitEnabled(state);
/* 618 */     this.customRequest.setSubmitEnabled(state == 0);
/*     */ 
/* 620 */     this.amountSpinner.setEnabled(state == 0);
/*     */ 
/* 622 */     Dimension dim = new Dimension(150, 100);
/*     */ 
/* 624 */     this.noTradeAlertPanel.setPreferredSize(dim);
/* 625 */     this.noTradeAlertPanel.setMinimumSize(dim);
/* 626 */     this.noTradeAlertPanel.setMaximumSize(dim);
/*     */ 
/* 628 */     switch (state) { case 2:
/* 629 */       this.alertText.setText(LocalizationManager.getText("trade.restricted")); break;
/*     */     case 1:
/* 630 */       this.alertText.setText(getTRADE_TEMPORARY_BLOCKED());
/*     */     }
/* 632 */     this.noTradeAlertPanel.setVisible(state != 0);
/*     */   }
/*     */ 
/*     */   public void setStopOrdersVisible(boolean visible)
/*     */   {
/* 638 */     this.conditionalOrderEntryPanel.setStopOrdersVisible(visible);
/*     */   }
/*     */ 
/*     */   public void resetLayout()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static boolean amountStartsWithZero(String amount) {
/* 646 */     return (amount.startsWith("0")) && (amount.indexOf(".") == -1) && (amount.length() > 1);
/*     */   }
/*     */ 
/*     */   private static String getTRADE_TEMPORARY_BLOCKED()
/*     */   {
/* 653 */     if ((GreedContext.isLive()) && (!GreedContext.IS_KAKAKU_LABEL))
/* 654 */       return LocalizationManager.getTextWithArguments("not.tradable.live.message", new Object[] { GuiUtilsAndConstants.LABEL_PHONE });
/* 655 */     if (GreedContext.IS_KAKAKU_LABEL) {
/* 656 */       return LocalizationManager.getText("not.tradable.kakaku.message");
/*     */     }
/* 658 */     return LocalizationManager.getText("not.tradable.other.message");
/*     */   }
/*     */ 
/*     */   public void setSlippage(String slippage)
/*     */   {
/* 663 */     this.conditionalOrderEntryPanel.setSlippage(slippage);
/*     */   }
/*     */ 
/*     */   public void setHedged(boolean hedged) {
/* 667 */     this.conditionalOrderEntryPanel.setHedged(hedged);
/*     */   }
/*     */ 
/*     */   public void switchConditionOrderVisibility() {
/* 671 */     this.conditionalOrderEntryPanel.switchVisibility();
/*     */   }
/*     */ 
/*     */   public boolean isConditionalOrdersPanelExpanded() {
/* 675 */     return this.conditionalOrderEntryPanel.isExpanded();
/*     */   }
/*     */ 
/*     */   public void setDefaultStopConditionLabels() {
/* 679 */     this.conditionalOrderEntryPanel.setDefaultStopConditionLabels(true);
/*     */   }
/*     */ 
/*     */   public void setOrderSide(OrderSide side) {
/* 683 */     this.conditionalOrderEntryPanel.setOrderSide(side);
/*     */   }
/*     */ 
/*     */   public boolean isOrderInProgramming() {
/* 687 */     return this.conditionalOrderEntryPanel.isOrderInProgramming();
/*     */   }
/*     */ 
/*     */   public JPanel getGridPanel() {
/* 691 */     return this.gridPanel;
/*     */   }
/*     */ 
/*     */   public void setGridPanel(JPanel gridPanel)
/*     */   {
/* 696 */     this.gridPanel = gridPanel;
/*     */   }
/*     */ 
/*     */   public boolean isExpanded()
/*     */   {
/* 701 */     return this.innerPanel.isVisible();
/*     */   }
/*     */ 
/*     */   private int getLayeredPaneH() {
/* 705 */     return isExpanded() ? 193 : 30;
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize()
/*     */   {
/* 711 */     return new Dimension(256, this.conditionalOrderEntryPanel.getMinimumSize().height + this.innerPanel.getMinimumSize().height + 3);
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize()
/*     */   {
/* 718 */     return getMinimumSize();
/*     */   }
/*     */ 
/*     */   public void switchVisibility() {
/* 722 */     this.innerPanel.setVisible(!this.innerPanel.isVisible());
/* 723 */     this.borderEntry.setSwitch(this.innerPanel.isVisible());
/*     */ 
/* 725 */     this.innerPanel.setMinimumSize(new Dimension(256, getLayeredPaneH()));
/* 726 */     this.gridPanel.setBounds(0, 0, 256, getLayeredPaneH());
/* 727 */     this.noTradeAlertPanel.setBounds(0, 0, 256, getLayeredPaneH());
/*     */ 
/* 729 */     if ((getParent() instanceof MultiSplitPane)) {
/* 730 */       ((MultiSplitPane)getParent()).switchVisibility("orderEntry");
/*     */     } else {
/* 732 */       Component parent = getParent();
/* 733 */       while ((null != parent) && 
/* 734 */         (!(parent instanceof JFrame))) {
/* 735 */         parent = parent.getParent();
/*     */       }
/* 737 */       ((JFrame)parent).pack();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void prepareContestCoditions(OrderSide side) {
/* 742 */     if (side == null) {
/* 743 */       LOGGER.warn("Wrong Order Side.");
/* 744 */       return;
/*     */     }
/* 746 */     this.conditionalOrderEntryPanel.setOrderSide(side);
/* 747 */     this.conditionalOrderEntryPanel.setEntryMarketCondition(true);
/*     */   }
/*     */ 
/*     */   public int getMaxHeight()
/*     */   {
/* 753 */     return getPreferredSize().height;
/*     */   }
/*     */ 
/*     */   public int getMinHeight()
/*     */   {
/* 759 */     return getMaxHeight();
/*     */   }
/*     */ 
/*     */   public int getPrefHeight()
/*     */   {
/* 765 */     return getMaxHeight();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel
 * JD-Core Version:    0.6.0
 */