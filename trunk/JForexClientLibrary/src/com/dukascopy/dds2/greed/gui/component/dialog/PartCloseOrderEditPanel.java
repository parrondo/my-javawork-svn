/*     */ package com.dukascopy.dds2.greed.gui.component.dialog;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.calc.PriceUtil;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.OrderEntryAction;
/*     */ import com.dukascopy.dds2.greed.actions.SignalAction;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableHeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableQuoterPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountLabel;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.AmountJSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.SlippageJSpinner;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*     */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.signals.SignalMessage;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Currency;
/*     */ import java.util.List;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class PartCloseOrderEditPanel extends AStopOrderEditPanel
/*     */ {
/*  66 */   private static final Logger LOGGER = LoggerFactory.getLogger(PartCloseOrderEditPanel.class);
/*     */ 
/*  68 */   private final JLocalizableButton submitButton = new JLocalizableButton("button.submit");
/*     */   private OrderGroupMessage orderGroup;
/*     */   private JLocalizableQuoterPanel quoter;
/*     */   private AmountJSpinner amountSpinner;
/*  72 */   private JCheckBox slippageCheck = new JCheckBox("Max. Slippage(pips):");
/*  73 */   private final SlippageJSpinner slippageSpinner = new SlippageJSpinner(5.0D, 1000.0D, 0.1D, 1, false);
/*     */   private JFrame parent;
/*  75 */   private OrderMessage closingOrder = new OrderMessage();
/*     */   private BigDecimal initAmount;
/*     */   private String instrument;
/*  78 */   private ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/*     */   public PartCloseOrderEditPanel(JFrame parent, OrderGroupMessage orderGroup) {
/*  81 */     this.orderGroup = orderGroup;
/*  82 */     this.parent = parent;
/*     */     try
/*     */     {
/*  85 */       Position position = OrderUtils.calculatePositionModified(orderGroup);
/*     */ 
/*  87 */       int scale = !GreedContext.isMiniFxAccount() ? 2 : 6;
/*  88 */       this.initAmount = position.getAmount().getValue().movePointLeft(6).setScale(scale, 4).stripTrailingZeros();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  92 */       LOGGER.error(e.getMessage(), e);
/*  93 */       this.initAmount = BigDecimal.ZERO;
/*     */     }
/*     */ 
/*  96 */     this.instrument = orderGroup.getInstrument();
/*     */ 
/*  98 */     build();
/*  99 */     add(Box.createHorizontalStrut(272));
/*     */   }
/*     */ 
/*     */   void build() {
/* 103 */     setLayout(new BoxLayout(this, 1));
/* 104 */     JPanel innerPanel = new JPanel();
/* 105 */     innerPanel.setLayout(new BoxLayout(innerPanel, 1));
/* 106 */     JLocalizableRoundedBorder myBorder = new JLocalizableRoundedBorder(innerPanel, "header.order.entry");
/*     */ 
/* 108 */     myBorder.setLeftInset(17);
/* 109 */     myBorder.setRightInset(17);
/*     */ 
/* 111 */     myBorder.setLeftBorder(10);
/* 112 */     myBorder.setRightBorder(11);
/*     */ 
/* 114 */     innerPanel.setBorder(myBorder);
/*     */ 
/* 116 */     JLocalizableHeaderPanel header = new JLocalizableHeaderPanel("header.cond.close", new String[] { this.instrument }, true);
/* 117 */     add(header);
/* 118 */     this.quoter = new JLocalizableQuoterPanel(this.orderGroup.getInstrument(), this);
/*     */ 
/* 120 */     this.quoter.setTradable(false);
/* 121 */     innerPanel.add(this.quoter);
/* 122 */     innerPanel.add(Box.createVerticalStrut(10));
/*     */ 
/* 124 */     JPanel dataPanel = new JPanel();
/* 125 */     dataPanel.setLayout(new GridBagLayout());
/* 126 */     GridBagConstraints c = new GridBagConstraints();
/* 127 */     c.insets = new Insets(0, 5, 1, 1);
/* 128 */     c.weightx = 1.0D; c.weighty = 1.0D;
/* 129 */     c.anchor = 17;
/*     */ 
/* 132 */     c.gridx = 0; c.gridy = 0; c.fill = 0;
/* 133 */     dataPanel.add(new LotAmountLabel(LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(this.instrument))), c);
/*     */ 
/* 135 */     c.gridx = 1; c.fill = 2;
/*     */ 
/* 138 */     BigDecimal currentLotInitAmount = LotAmountChanger.calculateAmountForDifferentLot(this.initAmount, GuiUtilsAndConstants.ONE_MILLION, LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(this.instrument)));
/*     */ 
/* 142 */     AmountJSpinner initAmountField = AmountJSpinner.getInstance(Instrument.fromString(this.instrument));
/* 143 */     initAmountField.setMinimum(LotAmountChanger.getMinTradableAmount(Instrument.fromString(this.instrument)).compareTo(currentLotInitAmount) > 0 ? currentLotInitAmount : LotAmountChanger.getMinTradableAmount(Instrument.fromString(this.instrument)));
/* 144 */     initAmountField.setValue(currentLotInitAmount);
/*     */ 
/* 146 */     initAmountField.setEnabled(false);
/* 147 */     Font font = initAmountField.getFont();
/* 148 */     initAmountField.setFont(new Font(font.getName(), 1, font.getSize()));
/* 149 */     initAmountField.setHorizontalAlignment(4);
/* 150 */     dataPanel.add(initAmountField, c);
/*     */ 
/* 153 */     c.gridx = 0; c.gridy = 1; c.fill = 0;
/* 154 */     dataPanel.add(new LotAmountLabel(LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(this.instrument))), c);
/*     */ 
/* 156 */     c.gridx = 1; c.fill = 2;
/* 157 */     Instrument currInstrument = Instrument.fromString(this.instrument);
/* 158 */     this.amountSpinner = AmountJSpinner.getInstance(currInstrument);
/* 159 */     this.amountSpinner.setHorizontalAlignment(4);
/* 160 */     this.amountSpinner.setMinimum(LotAmountChanger.getMinTradableAmount(currInstrument).compareTo(currentLotInitAmount) > 0 ? currentLotInitAmount : LotAmountChanger.getMinTradableAmount(Instrument.fromString(this.instrument)));
/* 161 */     this.amountSpinner.setStepSize(LotAmountChanger.getAmountStepSize(currInstrument));
/* 162 */     this.amountSpinner.setPrecision(LotAmountChanger.getAmountPrecision(currInstrument, LotAmountChanger.getLotAmountForInstrument(currInstrument)));
/* 163 */     this.amountSpinner.setValue(currentLotInitAmount);
/* 164 */     dataPanel.add(this.amountSpinner, c);
/*     */ 
/* 167 */     c.gridx = 0; c.gridy = 2; c.fill = 0;
/* 168 */     this.slippageCheck.setBorder(BorderFactory.createEmptyBorder());
/* 169 */     dataPanel.add(this.slippageCheck, c);
/*     */ 
/* 171 */     c.gridx = 1; c.fill = 2;
/* 172 */     this.slippageSpinner.setHorizontalAlignment(4);
/* 173 */     initSlippage();
/* 174 */     this.slippageSpinner.setEnabled(this.slippageCheck.isSelected());
/*     */ 
/* 176 */     dataPanel.add(this.slippageSpinner, c);
/*     */ 
/* 179 */     c.gridx = 0; c.gridy = 3; c.fill = 0;
/* 180 */     dataPanel.add(Box.createHorizontalStrut(1), c);
/*     */ 
/* 182 */     c.gridx = 1; c.fill = 2;
/* 183 */     dataPanel.add(Box.createHorizontalStrut(100), c);
/*     */ 
/* 186 */     innerPanel.add(dataPanel);
/* 187 */     add(innerPanel);
/*     */ 
/* 189 */     JPanel submitPanel = new JPanel();
/* 190 */     submitPanel.setLayout(new BorderLayout());
/* 191 */     submitPanel.setBorder(BorderFactory.createEmptyBorder(2, 10, 7, 11));
/* 192 */     submitPanel.add(this.submitButton, "Center");
/* 193 */     add(submitPanel);
/*     */ 
/* 195 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 196 */     onMarketState(marketView.getLastMarketState(this.instrument));
/*     */ 
/* 199 */     this.slippageCheck.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 201 */         PartCloseOrderEditPanel.this.slippageSpinner.setEnabled(PartCloseOrderEditPanel.this.slippageCheck.isSelected());
/* 202 */         PartCloseOrderEditPanel.this.setSlippage();
/*     */       }
/*     */     });
/* 205 */     this.submitButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 207 */         PartCloseOrderEditPanel.this.submitButtonPressed();
/*     */       }
/*     */     });
/* 211 */     this.submitButton.addMouseListener(new MouseAdapter() {
/*     */       public void mouseEntered(MouseEvent e) {
/* 213 */         Position pos = OrderUtils.calculatePositionModified(PartCloseOrderEditPanel.this.orderGroup);
/*     */ 
/* 215 */         OrderSide side = PositionSide.LONG.equals(pos.getPositionSide()) ? OrderSide.SELL : OrderSide.BUY;
/* 216 */         BigDecimal amountBDValue = PartCloseOrderEditPanel.this.amountSpinner.getAmountValueInMillions(Instrument.fromString(PartCloseOrderEditPanel.this.instrument));
/* 217 */         if (amountBDValue != null)
/*     */           try {
/* 219 */             BigDecimal amount = amountBDValue.multiply(GuiUtilsAndConstants.ONE_MILLION);
/* 220 */             String slippage = GuiUtilsAndConstants.getSlippageAmount(PartCloseOrderEditPanel.this.getSlippageAmount(), side, PartCloseOrderEditPanel.this.instrument);
/* 221 */             SignalMessage signal = new SignalMessage(PartCloseOrderEditPanel.this.instrument, "tbsb", side, amount, GreedContext.encodeAuth(), slippage);
/* 222 */             GreedContext.publishEvent(new SignalAction(this, signal));
/*     */           } catch (NumberFormatException e1) {
/* 224 */             PartCloseOrderEditPanel.LOGGER.debug(e1.getMessage());
/*     */           }
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent e)
/*     */       {
/* 230 */         GuiUtilsAndConstants.sendResetSignal(this);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void initSlippage() {
/* 236 */     if (this.storage.restoreApplySlippageToAllMarketOrders()) {
/* 237 */       this.slippageCheck.setSelected(true);
/* 238 */       this.slippageSpinner.setValue(this.storage.restoreDefaultSlippage());
/*     */     } else {
/* 240 */       this.slippageSpinner.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setSlippage() {
/* 245 */     if (this.slippageSpinner.isEnabled())
/* 246 */       this.slippageSpinner.setValue(this.storage.restoreDefaultSlippage());
/*     */     else
/* 248 */       this.slippageSpinner.clear();
/*     */   }
/*     */ 
/*     */   void submitButtonPressed()
/*     */   {
/* 253 */     if (!this.amountSpinner.validateEditor()) {
/* 254 */       return;
/*     */     }
/* 256 */     if ((this.slippageCheck.isSelected()) && (!this.slippageSpinner.validateEditor())) {
/* 257 */       return;
/*     */     }
/*     */ 
/* 260 */     this.closingOrder.setAmount(new Money(this.amountSpinner.getAmountValueInMillions(Instrument.fromString(this.instrument)).movePointRight(6).toPlainString(), this.orderGroup.getCurrencyPrimary()));
/*     */ 
/* 262 */     this.closingOrder.setSide(PositionSide.LONG.equals(OrderUtils.calculatePositionModified(this.orderGroup).getPositionSide()) ? OrderSide.SELL : OrderSide.BUY);
/*     */ 
/* 264 */     this.closingOrder.setInstrument(this.instrument);
/* 265 */     this.closingOrder.setOrderDirection(OrderDirection.CLOSE);
/* 266 */     this.closingOrder.setOrderGroupId(this.orderGroup.getOrderGroupId());
/* 267 */     this.closingOrder.setExternalSysId("");
/* 268 */     this.closingOrder.setSignalId("");
/*     */ 
/* 272 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 273 */     OfferSide os = this.closingOrder.getSide() == OrderSide.BUY ? OfferSide.ASK : OfferSide.BID;
/* 274 */     CurrencyOffer bestOffer = marketView.getBestOffer(this.instrument, os);
/* 275 */     if (bestOffer == null)
/*     */     {
/* 277 */       LOGGER.warn("no market liquidity for " + this.instrument);
/*     */ 
/* 279 */       return;
/*     */     }
/* 281 */     this.closingOrder.setPriceClient(bestOffer.getPrice());
/*     */ 
/* 285 */     if ((this.slippageCheck.isSelected()) && (this.slippageSpinner.getValue() != null)) {
/* 286 */       OfferSide offerSide = this.closingOrder.getSide() == OrderSide.BUY ? OfferSide.ASK : OfferSide.BID;
/* 287 */       MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 288 */       CurrencyOffer bestOffer = marketView.getBestOffer(this.instrument, offerSide);
/* 289 */       BigDecimal maxSlippage = (BigDecimal)this.slippageSpinner.getValue();
/* 290 */       BigDecimal trailingLimit = maxSlippage.multiply(PriceUtil.pipValue(bestOffer.getPrice().getValue()));
/* 291 */       String[] currencies = this.instrument.split("/");
/* 292 */       this.closingOrder.setPriceTrailingLimit(new Money(trailingLimit, Currency.getInstance(currencies[1])));
/*     */     }
/*     */     OrderGroupMessage newGroup;
/*     */     try {
/* 297 */       newGroup = OrderMessageUtils.copyOrderGroup(this.orderGroup);
/*     */     } catch (ParseException e) {
/* 299 */       LOGGER.error(e.getMessage(), e);
/* 300 */       return;
/*     */     }
/* 302 */     List orders = new ArrayList();
/* 303 */     orders.add(this.closingOrder);
/* 304 */     newGroup.setOrders(orders);
/*     */ 
/* 306 */     PlatformInitUtils.setExtIdforOrderMessages(newGroup);
/*     */ 
/* 309 */     OrderEntryAction orderEntryAction = new OrderEntryAction(this, newGroup, OrderDirection.CLOSE, false);
/* 310 */     GreedContext.publishEvent(orderEntryAction);
/*     */ 
/* 312 */     this.parent.dispose();
/*     */   }
/*     */ 
/*     */   public String getSlippageAmount() {
/* 316 */     if (!this.slippageCheck.isSelected()) {
/* 317 */       return null;
/*     */     }
/* 319 */     return ((BigDecimal)this.slippageSpinner.getValue()).toPlainString();
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmount() {
/* 323 */     return this.amountSpinner.getAmountValueInMillions(Instrument.fromString(this.instrument));
/*     */   }
/*     */ 
/*     */   public void onAccountInfo(AccountInfoMessage accountInfo) {
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market) {
/* 330 */     if ((market != null) && (market.getInstrument().equals(this.orderGroup.getInstrument())))
/* 331 */       this.quoter.onTick(Instrument.fromString(this.instrument));
/*     */   }
/*     */ 
/*     */   public void quickieOrder(String instrument, OrderSide side)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.PartCloseOrderEditPanel
 * JD-Core Version:    0.6.0
 */