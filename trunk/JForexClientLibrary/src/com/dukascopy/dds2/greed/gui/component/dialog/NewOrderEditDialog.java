/*     */ package com.dukascopy.dds2.greed.gui.component.dialog;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.OrderEntryAction;
/*     */ import com.dukascopy.dds2.greed.actions.PostMessageAction;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.MessageBox;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.ConditionalOrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.SubmitActionListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder.OrderValidationBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableQuoterPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountLabel;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.AmountJSpinner;
/*     */ import com.dukascopy.dds2.greed.model.AccountInfoListener;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketStateWrapperListener;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import com.dukascopy.dds2.greed.util.QuickieOrderSupport;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.awt.Dialog.ModalityType;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.List;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRootPane;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class NewOrderEditDialog extends JDialog
/*     */   implements QuickieOrderSupport, PlatformSpecific, MarketStateWrapperListener, AccountInfoListener
/*     */ {
/*  72 */   private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderEditDialog.class);
/*     */ 
/*  74 */   private static int lineIdInc = 0;
/*     */   private JLocalizableQuoterPanel quoter;
/*     */   private ConditionalOrderEntryPanel conditionalOrderEntryPanel;
/*  79 */   private AmountJSpinner amountSpinner = AmountJSpinner.getInstance();
/*     */   private OrderConfirmationDialog previewDialog;
/*     */   private MessageBox warningsPanel;
/*     */   private MarketView marketView;
/*     */   private final Instrument instrument;
/*     */ 
/*     */   public NewOrderEditDialog(Window window, Instrument instrument, IEngine.OrderCommand orderCommand, double price)
/*     */   {
/*  89 */     super(window == null ? (ClientForm)GreedContext.get("clientGui") : window, instrument.toString(), Dialog.ModalityType.MODELESS);
/*  90 */     Toolkit.getDefaultToolkit().setDynamicLayout(true);
/*     */     try
/*     */     {
/*  93 */       setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*     */     } catch (Exception e) {
/*  95 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/*  98 */     setResizable(false);
/*  99 */     setDefaultCloseOperation(0);
/* 100 */     setAlwaysOnTop(true);
/*     */ 
/* 102 */     this.instrument = instrument;
/*     */ 
/* 104 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/*     */ 
/* 106 */     build();
/* 107 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 108 */     clientForm.addMarketWatcher(this);
/* 109 */     clientForm.addAccountInfoWatcher(this);
/* 110 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent e) {
/* 112 */         NewOrderEditDialog.this.closeDialog();
/*     */       }
/*     */     });
/* 116 */     this.conditionalOrderEntryPanel.setStopOrdersVisible((!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended()));
/*     */ 
/* 118 */     onMarketState(this.marketView.getLastMarketState(instrument.toString()));
/* 119 */     this.conditionalOrderEntryPanel.setDefaultStopConditionLabels(true);
/* 120 */     this.conditionalOrderEntryPanel.clearEverything(false);
/*     */ 
/* 122 */     this.conditionalOrderEntryPanel.lockOrderSideTo(orderCommand.isLong() ? OrderSide.BUY : OrderSide.SELL);
/* 123 */     StopDirection entryStopDirection = null;
/* 124 */     switch (6.$SwitchMap$com$dukascopy$api$IEngine$OrderCommand[orderCommand.ordinal()]) {
/*     */     case 1:
/* 126 */       entryStopDirection = StopDirection.ASK_EQUALS;
/* 127 */       break;
/*     */     case 2:
/* 129 */       entryStopDirection = StopDirection.BID_EQUALS;
/* 130 */       break;
/*     */     case 3:
/* 132 */       entryStopDirection = StopDirection.ASK_GREATER;
/* 133 */       break;
/*     */     case 4:
/* 135 */       entryStopDirection = StopDirection.BID_LESS;
/* 136 */       break;
/*     */     case 5:
/* 138 */       entryStopDirection = StopDirection.BID_GREATER;
/* 139 */       break;
/*     */     case 6:
/* 141 */       entryStopDirection = StopDirection.ASK_LESS;
/* 142 */       break;
/*     */     case 7:
/*     */     case 8:
/* 145 */       break;
/*     */     default:
/* 147 */       throw new RuntimeException("Incorrect order command");
/*     */     }
/* 149 */     this.conditionalOrderEntryPanel.lockEntryPriceAndStopDirection(entryStopDirection, price);
/* 150 */     this.conditionalOrderEntryPanel.disableSubmitButton();
/* 151 */     this.conditionalOrderEntryPanel.setVisibilitySwitchAllowed(false);
/*     */ 
/* 153 */     setMinimumSize(new Dimension(254, 428));
/* 154 */     pack();
/* 155 */     setLocationRelativeTo(window);
/*     */   }
/*     */ 
/*     */   private void build()
/*     */   {
/* 161 */     JPanel mainPanel = new JPanel();
/* 162 */     setContentPane(mainPanel);
/*     */ 
/* 164 */     this.quoter = new JLocalizableQuoterPanel(this.instrument, this);
/* 165 */     this.quoter.setTradable((!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended()));
/*     */ 
/* 167 */     this.conditionalOrderEntryPanel = new ConditionalOrderEntryPanel(this.instrument, this.quoter, "NOED" + lineIdInc++)
/*     */     {
/*     */       protected BigDecimal getAmount() {
/* 170 */         return NewOrderEditDialog.this.amountSpinner.getAmountValueInMillions(NewOrderEditDialog.this.instrument);
/*     */       }
/*     */ 
/*     */       public boolean isOrderInProgramming()
/*     */       {
/* 175 */         return false;
/*     */       }
/*     */     };
/* 178 */     this.conditionalOrderEntryPanel.setSubmitActionListener(new SubmitActionListener()
/*     */     {
/*     */       public void submitButtonPressed(OrderGroupMessage ogm) {
/* 181 */         NewOrderEditDialog.this.doSubmit(ogm);
/*     */       }
/*     */     });
/* 185 */     mainPanel.setLayout(new BoxLayout(mainPanel, 1));
/* 186 */     JPanel innerPanel = new JPanel();
/* 187 */     JLocalizableRoundedBorder borderEntry = new JLocalizableRoundedBorder(innerPanel, "header.order.entry");
/* 188 */     borderEntry.setRightInset(7);
/* 189 */     borderEntry.setLeftInset(9);
/* 190 */     borderEntry.setBottomInset(16);
/*     */ 
/* 192 */     innerPanel.setBorder(borderEntry);
/* 193 */     innerPanel.setLayout(new BoxLayout(innerPanel, 1));
/*     */ 
/* 195 */     HeaderPanel header = new HeaderPanel(this.instrument.toString(), true);
/* 196 */     mainPanel.add(header);
/*     */ 
/* 198 */     JPanel quotesPanel = new JPanel();
/* 199 */     quotesPanel.setLayout(new BoxLayout(quotesPanel, 1));
/*     */ 
/* 202 */     quotesPanel.add(Box.createRigidArea(new Dimension(0, 2)));
/* 203 */     quotesPanel.add(this.quoter);
/* 204 */     quotesPanel.add(Box.createVerticalStrut(4));
/*     */ 
/* 207 */     quotesPanel.add(Box.createRigidArea(new Dimension(0, 6)));
/*     */ 
/* 209 */     LotAmountLabel amountLabel = new LotAmountLabel(LotAmountChanger.getLotAmountForInstrument(this.instrument));
/* 210 */     this.amountSpinner.setHorizontalAlignment(4);
/* 211 */     this.amountSpinner.setMinimum(LotAmountChanger.getMinTradableAmount(this.instrument));
/* 212 */     this.amountSpinner.setStepSize(LotAmountChanger.getAmountStepSize(this.instrument));
/* 213 */     this.amountSpinner.setValue(LotAmountChanger.getDefaultAmountValue(this.instrument));
/*     */ 
/* 215 */     if (MACOSX) {
/* 216 */       amountLabel.putClientProperty("JComponent.sizeVariant", "small");
/*     */     }
/* 218 */     JPanel orderForm = new JPanel(new GridLayout(1, 2, 5, 2));
/* 219 */     JPanel spacer = new JPanel();
/* 220 */     spacer.setLayout(new BoxLayout(spacer, 0));
/* 221 */     spacer.add(Box.createHorizontalStrut(6));
/* 222 */     spacer.add(amountLabel);
/* 223 */     orderForm.add(spacer);
/* 224 */     orderForm.add(this.amountSpinner);
/* 225 */     quotesPanel.add(orderForm);
/*     */ 
/* 228 */     innerPanel.add(quotesPanel);
/*     */ 
/* 231 */     this.warningsPanel = new MessageBox();
/* 232 */     this.warningsPanel.setVisible(false);
/* 233 */     quotesPanel.add(this.warningsPanel);
/*     */ 
/* 235 */     JPanel buttonsPanel = new JPanel(new GridBagLayout());
/* 236 */     JLocalizableButton submitButton = new JLocalizableButton("button.submit");
/* 237 */     submitButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 240 */         NewOrderEditDialog.this.conditionalOrderEntryPanel.doSubmit();
/*     */       }
/*     */     });
/* 243 */     getRootPane().setDefaultButton(submitButton);
/* 244 */     JLocalizableButton cancelButton = new JLocalizableButton("button.cancel");
/* 245 */     cancelButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 248 */         NewOrderEditDialog.this.closeDialog();
/*     */       }
/*     */     });
/* 252 */     GridBagConstraints gbc = new GridBagConstraints();
/* 253 */     GridBagLayoutHelper.add(0, 0, 1.0D, 0.0D, 1, 1, 5, 0, 0, 5, 1, 10, gbc, buttonsPanel, submitButton);
/* 254 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 5, 0, 5, 5, 1, 10, gbc, buttonsPanel, cancelButton);
/*     */ 
/* 256 */     add(Box.createRigidArea(new Dimension(0, 3)));
/* 257 */     add(innerPanel);
/* 258 */     add(this.conditionalOrderEntryPanel);
/* 259 */     add(buttonsPanel);
/*     */   }
/*     */ 
/*     */   public void doSubmit(OrderGroupMessage orderGroup)
/*     */   {
/* 264 */     if (this.instrument != null)
/*     */     {
/* 266 */       OrderSide side = orderGroup.getOpeningOrder().getSide();
/* 267 */       OfferSide offerSide = side == OrderSide.BUY ? OfferSide.ASK : OfferSide.BID;
/*     */ 
/* 269 */       CurrencyOffer bestOffer = this.marketView.getBestOffer(this.instrument.toString(), offerSide);
/* 270 */       if ((bestOffer == null) || (bestOffer.getPrice() == null)) {
/* 271 */         Notification notification = new Notification(null, "No liquidity for " + side.asString() + " " + this.instrument + "!");
/* 272 */         notification.setPriority("WARNING");
/* 273 */         PostMessageAction post = new PostMessageAction(this, notification);
/* 274 */         GreedContext.publishEvent(post);
/* 275 */         return;
/*     */       }
/*     */ 
/* 278 */       if (!this.amountSpinner.validateEditor()) {
/* 279 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 283 */         Money amount = new Money(this.amountSpinner.getAmountValueInMillions(this.instrument).multiply(GuiUtilsAndConstants.ONE_MILLION), this.instrument.getPrimaryCurrency());
/*     */ 
/* 285 */         for (OrderMessage order : orderGroup.getOrders()) {
/* 286 */           order.setAmount(amount);
/*     */         }
/*     */ 
/* 289 */         orderGroup.getOpeningOrder().setPriceClient(bestOffer.getPrice());
/*     */ 
/* 291 */         ValidateOrder.OrderValidationBean result = ValidateOrder.getInstance().validateOrder(orderGroup);
/*     */ 
/* 293 */         ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*     */ 
/* 295 */         OrderConfirmationDialog validationDialog = new OrderConfirmationDialog(orderGroup, result, clientForm, false, false);
/*     */ 
/* 297 */         this.previewDialog = new OrderConfirmationDialog(orderGroup, clientForm);
/*     */ 
/* 299 */         if (clientForm.getStatusBar().getAccountStatement().getOneClickCheckbox().isSelected()) {
/* 300 */           ClientSettingsStorage settings = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 301 */           if (settings.restoreOrderValidationOn()) {
/* 302 */             if (!result.getMessages().isEmpty()) {
/* 303 */               validationDialog.onMarketState(this.marketView.getLastMarketState(this.instrument.toString()));
/* 304 */               validationDialog.addPreviewInfo(this.conditionalOrderEntryPanel.getSelectedSide(), orderGroup);
/* 305 */               validationDialog.setLocationRelativeTo(this.conditionalOrderEntryPanel);
/* 306 */               validationDialog.setDeferedTradeLog(false);
/* 307 */               validationDialog.setVisible(true);
/* 308 */               if (validationDialog.isOrderSubmited())
/* 309 */                 closeDialog();
/*     */             }
/*     */             else {
/* 312 */               fireOrderEntryAction(orderGroup, false);
/*     */             }
/*     */           }
/* 315 */           else fireOrderEntryAction(orderGroup, false);
/*     */         }
/*     */         else
/*     */         {
/* 319 */           ClientSettingsStorage settings = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 320 */           if (settings.restoreOrderValidationOn()) {
/* 321 */             if (!result.getMessages().isEmpty()) {
/* 322 */               validationDialog.addPreviewInfo(this.conditionalOrderEntryPanel.getSelectedSide(), orderGroup);
/* 323 */               validationDialog.onMarketState(this.marketView.getLastMarketState(this.instrument.toString()));
/* 324 */               validationDialog.setLocationRelativeTo(this.conditionalOrderEntryPanel);
/* 325 */               validationDialog.setDeferedTradeLog(false);
/* 326 */               validationDialog.setVisible(true);
/* 327 */               if (validationDialog.isOrderSubmited())
/* 328 */                 closeDialog();
/*     */             }
/*     */             else {
/* 331 */               this.previewDialog.onMarketState(this.marketView.getLastMarketState(this.instrument.toString()));
/* 332 */               this.previewDialog.addPreviewInfo(this.conditionalOrderEntryPanel.getSelectedSide(), orderGroup);
/* 333 */               this.previewDialog.setLocationRelativeTo(this.conditionalOrderEntryPanel);
/* 334 */               this.previewDialog.setDeferedTradeLog(false);
/* 335 */               this.previewDialog.setVisible(true);
/* 336 */               if (this.previewDialog.isOrderSubmited())
/* 337 */                 closeDialog();
/*     */             }
/*     */           }
/*     */           else {
/* 341 */             this.previewDialog.onMarketState(this.marketView.getLastMarketState(this.instrument.toString()));
/* 342 */             this.previewDialog.addPreviewInfo(this.conditionalOrderEntryPanel.getSelectedSide(), orderGroup);
/* 343 */             this.previewDialog.setLocationRelativeTo(this.conditionalOrderEntryPanel);
/* 344 */             this.previewDialog.setDeferedTradeLog(false);
/* 345 */             this.previewDialog.setVisible(true);
/* 346 */             if (this.previewDialog.isOrderSubmited()) {
/* 347 */               closeDialog();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException ex)
/*     */       {
/* 354 */         LOGGER.warn("parsing: " + ex.getMessage());
/*     */       }
/*     */     } else {
/* 357 */       String sNoInstr = LocalizationManager.getText("oep.no.instr");
/* 358 */       StringBuffer sb = new StringBuffer(LocalizationManager.getText("oep.correct.following.errors")).append(null == this.instrument ? sNoInstr : "");
/*     */ 
/* 360 */       this.warningsPanel.setTextAndStartTimer(sb.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void closeDialog() {
/* 365 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 366 */     clientForm.removeMarketWatcher(this);
/* 367 */     clientForm.removeAccountInfoWatcher(this);
/* 368 */     this.conditionalOrderEntryPanel.removeAllLines();
/* 369 */     setVisible(false);
/* 370 */     dispose();
/*     */   }
/*     */ 
/*     */   public void fireOrderEntryAction(OrderGroupMessage orderGroup, boolean defered) {
/* 374 */     PlatformInitUtils.setExtSysIdForOrderGroup(orderGroup);
/* 375 */     OrderEntryAction orderEntryAction = new OrderEntryAction(this, orderGroup, defered);
/* 376 */     GreedContext.publishEvent(orderEntryAction);
/* 377 */     closeDialog();
/*     */   }
/*     */ 
/*     */   public void quickieOrder(String instrument, OrderSide side)
/*     */   {
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmount()
/*     */   {
/* 386 */     return null;
/*     */   }
/*     */ 
/*     */   public String getSlippageAmount()
/*     */   {
/* 391 */     return null;
/*     */   }
/*     */ 
/*     */   public void setSlippage(String slippage) {
/* 395 */     this.conditionalOrderEntryPanel.setSlippage(slippage);
/*     */   }
/*     */ 
/*     */   public void setAmount(String amount) throws NumberFormatException {
/* 399 */     this.amountSpinner.setValue(new BigDecimal(amount));
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market) {
/* 403 */     if ((market == null) || (market.getInstrument() == null)) {
/* 404 */       return;
/*     */     }
/* 406 */     if (market.getInstrument().equals(this.instrument.toString()))
/*     */     {
/* 408 */       this.quoter.onTick(this.instrument);
/*     */ 
/* 410 */       if ((this.previewDialog != null) && (this.previewDialog.isVisible())) {
/* 411 */         this.previewDialog.onMarketState(market);
/*     */       }
/*     */     }
/*     */ 
/* 415 */     InstrumentStatusUpdateMessage instrumentState = this.marketView.getInstrumentState(this.instrument.toString());
/* 416 */     if (instrumentState != null)
/* 417 */       this.conditionalOrderEntryPanel.setSubmitEnabled(instrumentState.getTradable());
/*     */     else
/* 419 */       this.conditionalOrderEntryPanel.setSubmitEnabled(1);
/*     */   }
/*     */ 
/*     */   public void onAccountInfo(AccountInfoMessage accountInfo)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.NewOrderEditDialog
 * JD-Core Version:    0.6.0
 */