/*      */ package com.dukascopy.dds2.greed.gui.component.dialog;
/*      */ 
/*      */ import com.dukascopy.api.ChartObjectAdapter;
/*      */ import com.dukascopy.api.ChartObjectEvent;
/*      */ import com.dukascopy.api.IChart;
/*      */ import com.dukascopy.api.IChart.Type;
/*      */ import com.dukascopy.api.IChartObject;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.charts.chartbuilder.IChartWrapper;
/*      */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*      */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*      */ import com.dukascopy.dds2.greed.actions.OrderEntryAction;
/*      */ import com.dukascopy.dds2.greed.actions.SignalAction;
/*      */ import com.dukascopy.dds2.greed.agent.DDSAgent;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*      */ import com.dukascopy.dds2.greed.gui.component.ApplicationClock;
/*      */ import com.dukascopy.dds2.greed.gui.component.BasicDecoratedFrame;
/*      */ import com.dukascopy.dds2.greed.gui.component.PriceSpinner;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder.OrderValidationBean;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableComboBox;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableHeaderPanel;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButton;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*      */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountLabel;
/*      */ import com.dukascopy.dds2.greed.gui.util.spinners.AmountJSpinner;
/*      */ import com.dukascopy.dds2.greed.gui.util.spinners.CommonJSpinner;
/*      */ import com.dukascopy.dds2.greed.model.MarketView;
/*      */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*      */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*      */ import com.dukascopy.dds2.greed.util.QuickieOrderSupport;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.OfferSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*      */ import com.dukascopy.transport.common.msg.signals.SignalMessage;
/*      */ import com.toedter.calendar.JDateChooser;
/*      */ import com.toedter.calendar.JSpinnerDateEditor;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Container;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Dialog.ModalExclusionType;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.SystemColor;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.math.BigDecimal;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Currency;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.InputVerifier;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFormattedTextField;
/*      */ import javax.swing.JFormattedTextField.AbstractFormatter;
/*      */ import javax.swing.JFormattedTextField.AbstractFormatterFactory;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JSpinner;
/*      */ import javax.swing.SpinnerModel;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class CustomRequestDialog extends BasicDecoratedFrame
/*      */ {
/*  117 */   private static final Logger LOGGER = LoggerFactory.getLogger(CustomRequestDialog.class);
/*      */   private static final String PLACE_BID_OFFER_LINE = "_bid_offer_line";
/*      */   private static final int ONE_MINUTE = 60000;
/*  120 */   private static int lineCounter = 0;
/*      */   private OfferSide side;
/*      */   private String instrument;
/*      */   private JPanel errorPanel;
/*      */   private JPanel buttonsPanel;
/*  127 */   private PriceSpinner priceField = new PriceSpinner();
/*      */   private AmountJSpinner amountSpinner;
/*      */   private JTimeSpinner timeSpinner;
/*      */   private CommonJSpinner goodForSpinner;
/*      */   private JLocalizableComboBox comboBox;
/*      */   private JDateChooser dateChooser;
/*      */   private JLocalizableLabel errorLabel;
/*      */   private JLocalizableRadioButton rbGF;
/*      */   private JLocalizableRadioButton rbGT;
/*      */   private JLocalizableButton bRefresh;
/*      */   private static final String DATE_FORMAT = "dd.MM.yyyy";
/*  139 */   private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
/*  140 */   private boolean editMode = false;
/*      */   private OrderMessage order;
/*  143 */   private JPanel pGoodFor = new JPanel();
/*  144 */   private JPanel pGoodTill = new JPanel();
/*  145 */   private JLocalizableRadioButton rbGTC = new JLocalizableRadioButton("radio.GTC");
/*  146 */   private CustomRequestDialog crd = this;
/*      */   private int lineId;
/*      */   private final Integer chartId;
/*      */   private DDSChartsController ddsChartsController;
/*  153 */   private ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*      */ 
/*      */   public CustomRequestDialog(OrderMessage order, Integer chartId) {
/*  156 */     this(order, order.getPriceClient().getValue().toPlainString(), chartId);
/*      */   }
/*      */ 
/*      */   public CustomRequestDialog(OfferSide side, String instrument, Integer chartId) {
/*  160 */     this.chartId = null;
/*  161 */     build(instrument, side, null);
/*      */   }
/*      */ 
/*      */   public CustomRequestDialog(OrderMessage order, String price, Integer chartId) {
/*  165 */     this.chartId = chartId;
/*  166 */     this.side = (order.getSide() == OrderSide.SELL ? OfferSide.ASK : OfferSide.BID);
/*  167 */     this.instrument = order.getInstrument();
/*  168 */     this.order = order;
/*  169 */     this.lineId = (lineCounter++);
/*      */ 
/*  171 */     String title = OfferSide.BID == this.side ? "frame.bid" : "frame.offer";
/*  172 */     String[] params = { this.instrument };
/*      */ 
/*  174 */     this.editMode = true;
/*      */ 
/*  176 */     if (chartId != null) {
/*  177 */       setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
/*      */     }
/*      */ 
/*  180 */     this.priceField.setText(price);
/*  181 */     drawBidOfferLine();
/*      */ 
/*  183 */     BigDecimal amountInMill = order.getAmount().getValue().divide(GuiUtilsAndConstants.ONE_MILLION, 6, 5).stripTrailingZeros();
/*  184 */     BigDecimal amountInCurrLot = LotAmountChanger.calculateAmountForDifferentLot(amountInMill, GuiUtilsAndConstants.ONE_MILLION, LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(this.instrument)));
/*      */ 
/*  188 */     initAndShow(this.side, this.instrument, title, params, amountInCurrLot);
/*      */ 
/*  190 */     this.amountSpinner.setEnabled(false);
/*      */ 
/*  192 */     TimeString ts = new TimeString();
/*  193 */     this.goodForSpinner.setValue(this.storage.restoreOrderValidityTime());
/*  194 */     this.timeSpinner.setValue(ts);
/*  195 */     this.dateChooser.setDate(new Date(ts.time));
/*      */ 
/*  197 */     initRbAndGuessGtGf(order.getExecTimeoutMillis().longValue());
/*      */ 
/*  199 */     this.bRefresh.setEnabled(false);
/*      */   }
/*      */ 
/*      */   private void initRbAndGuessGtGf(long orderTimeoutMillis)
/*      */   {
/*  208 */     long currentPlatformTime = ((ApplicationClock)GreedContext.get("applicationClock")).getTime();
/*      */ 
/*  210 */     long deltaMillis = Math.abs(orderTimeoutMillis - currentPlatformTime);
/*      */ 
/*  212 */     Calendar cal = Calendar.getInstance();
/*  213 */     cal.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  214 */     cal.setTimeInMillis(orderTimeoutMillis);
/*      */ 
/*  216 */     TimeString ts = new TimeString(cal.getTime());
/*  217 */     this.timeSpinner.setValue(ts);
/*  218 */     this.dateChooser.setDate(cal.getTime());
/*      */ 
/*  220 */     if (deltaMillis < 1576800000000L) {
/*  221 */       this.pGoodFor.setVisible(false);
/*  222 */       this.pGoodTill.setVisible(true);
/*      */ 
/*  224 */       this.rbGT.setSelected(true);
/*  225 */       this.comboBox.setSelectedIndex(this.storage.restoreOrderValidityTimeUnit());
/*      */     }
/*      */ 
/*  228 */     pack();
/*      */   }
/*      */ 
/*      */   private void build(String instrument, OfferSide side, QuickieOrderSupport parent) {
/*  232 */     String titleKey = OfferSide.BID == side ? "frame.bid" : "frame.offer";
/*  233 */     String[] params = { instrument };
/*      */ 
/*  236 */     this.side = side;
/*  237 */     this.instrument = instrument;
/*  238 */     this.lineId = (lineCounter++);
/*      */ 
/*  240 */     if (Instrument.fromString(instrument) == null) return;
/*      */ 
/*  242 */     initAndShow(side, instrument, titleKey, params, LotAmountChanger.getDefaultAmountValue(Instrument.fromString(instrument)));
/*      */   }
/*      */ 
/*      */   private void initAndShow(OfferSide side, String instrument, String titleKey, String[] titleParams, BigDecimal amount)
/*      */   {
/*  247 */     this.sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  248 */     int INSET = 5;
/*      */ 
/*  250 */     setParams(titleParams);
/*  251 */     setTitle(titleKey);
/*      */ 
/*  253 */     JPanel content = new JPanel();
/*  254 */     content.setLayout(new GridBagLayout());
/*  255 */     GridBagConstraints c = new GridBagConstraints();
/*  256 */     c.insets = new Insets(5, 5, 5, 5);
/*  257 */     c.weightx = 1.0D;
/*  258 */     c.weighty = 1.0D;
/*  259 */     c.anchor = 17;
/*  260 */     int gridY = 0;
/*      */ 
/*  263 */     LotAmountLabel label = new LotAmountLabel(LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(instrument)));
/*  264 */     JPanel pAmoPri = new JPanel(new GridLayout(2, 2, 5, 5));
/*  265 */     pAmoPri.add(label);
/*      */ 
/*  270 */     this.amountSpinner = AmountJSpinner.getInstance(Instrument.fromString(instrument));
/*  271 */     this.amountSpinner.setHorizontalAlignment(4);
/*  272 */     this.amountSpinner.setMinimum(LotAmountChanger.getMinTradableAmount(Instrument.fromString(instrument)).compareTo(amount) > 0 ? amount : LotAmountChanger.getMinTradableAmount(Instrument.fromString(instrument)));
/*  273 */     this.amountSpinner.setStepSize(LotAmountChanger.getAmountStepSize(Instrument.fromString(instrument)));
/*  274 */     this.amountSpinner.setPrecision(LotAmountChanger.getAmountPrecision(Instrument.fromString(instrument), LotAmountChanger.getCurrentLotAmount()));
/*  275 */     this.amountSpinner.setValue(amount);
/*      */ 
/*  277 */     pAmoPri.add(this.amountSpinner);
/*      */ 
/*  279 */     pAmoPri.add(new JLocalizableLabel("label.price"));
/*  280 */     this.priceField.setHorizontalAlignment(4);
/*  281 */     pAmoPri.add(this.priceField);
/*  282 */     this.priceField.addChangeListener(new ChangeListener() {
/*      */       public void stateChanged(ChangeEvent e) {
/*  284 */         CustomRequestDialog.this.drawBidOfferLine();
/*      */       }
/*      */     });
/*  288 */     c.gridx = 0;
/*  289 */     c.gridy = (gridY++);
/*  290 */     c.fill = 2;
/*  291 */     content.add(pAmoPri, c);
/*      */ 
/*  294 */     JPanel radioPanel = new JPanel(new GridLayout(1, 3, 5, 0));
/*  295 */     this.rbGF = new JLocalizableRadioButton("radio.good.for");
/*  296 */     this.rbGT = new JLocalizableRadioButton("radio.good.till");
/*  297 */     ButtonGroup rbg = new ButtonGroup();
/*  298 */     rbg.add(this.rbGTC);
/*  299 */     rbg.add(this.rbGF);
/*  300 */     rbg.add(this.rbGT);
/*  301 */     this.rbGTC.setSelected(true);
/*  302 */     radioPanel.add(this.rbGTC);
/*  303 */     radioPanel.add(this.rbGF);
/*  304 */     radioPanel.add(this.rbGT);
/*      */ 
/*  306 */     c.gridy = (gridY++);
/*  307 */     c.fill = 2;
/*  308 */     content.add(radioPanel, c);
/*      */ 
/*  310 */     this.pGoodFor.setLayout(new GridLayout(1, 2, 5, 5));
/*  311 */     this.goodForSpinner = new CommonJSpinner(this.storage.restoreOrderValidityTime().doubleValue(), GuiUtilsAndConstants.ONE.doubleValue(), 1.7976931348623157E+308D, GuiUtilsAndConstants.ONE.doubleValue(), 0, false, false);
/*      */ 
/*  313 */     this.goodForSpinner.setHorizontalAlignment(4);
/*  314 */     this.goodForSpinner.setFont(label.getFont());
/*  315 */     this.goodForSpinner.setValue(this.storage.restoreOrderValidityTime());
/*  316 */     this.pGoodFor.add(this.goodForSpinner);
/*      */ 
/*  318 */     this.comboBox = new JTimeUnitComboBox();
/*  319 */     this.comboBox.setSelectedIndex(this.storage.restoreOrderValidityTimeUnit());
/*      */ 
/*  321 */     this.pGoodFor.add(this.comboBox);
/*  322 */     this.pGoodFor.setVisible(false);
/*      */ 
/*  324 */     c.gridy = (gridY++);
/*  325 */     c.fill = 2;
/*  326 */     content.add(this.pGoodFor, c);
/*      */ 
/*  328 */     this.pGoodTill.setLayout(new BoxLayout(this.pGoodTill, 0));
/*      */ 
/*  330 */     this.dateChooser = new JDateChooser(new JSpinnerDateEditor());
/*  331 */     this.dateChooser.setDateFormatString("dd.MM.yyyy");
/*  332 */     this.dateChooser.setDate(new Date());
/*      */ 
/*  334 */     this.pGoodTill.add(this.dateChooser);
/*  335 */     this.pGoodTill.add(Box.createHorizontalStrut(5));
/*      */ 
/*  337 */     this.timeSpinner = new JTimeSpinner();
/*  338 */     this.timeSpinner.setValue(this.timeSpinner.adjustTime(60));
/*  339 */     this.timeSpinner.getEditor().setFont(label.getFont());
/*  340 */     this.timeSpinner.getEditor().setBackground(SystemColor.text);
/*  341 */     this.timeSpinner.getEditor().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
/*  342 */     this.pGoodTill.add(this.timeSpinner);
/*  343 */     this.pGoodTill.add(new JLocalizableLabel("label.gmt"));
/*  344 */     this.pGoodTill.setVisible(false);
/*      */ 
/*  346 */     c.gridy = (gridY++);
/*  347 */     c.fill = 2;
/*  348 */     content.add(this.pGoodTill, c);
/*      */ 
/*  351 */     this.errorPanel = new JPanel(new FlowLayout(1));
/*  352 */     this.errorLabel = new JLocalizableLabel("label.error.ok");
/*  353 */     this.errorLabel.setForeground(Color.red);
/*  354 */     this.errorPanel.add(this.errorLabel);
/*  355 */     this.errorPanel.setVisible(false);
/*  356 */     this.errorPanel.addMouseListener(new MouseAdapter() {
/*      */       public void mouseReleased(MouseEvent e) {
/*  358 */         CustomRequestDialog.this.errorPanel.setVisible(false);
/*  359 */         CustomRequestDialog.this.pack();
/*      */       }
/*      */ 
/*      */       public void mouseEntered(MouseEvent e) {
/*  363 */         CustomRequestDialog.this.setCursor(Cursor.getPredefinedCursor(12));
/*      */       }
/*      */ 
/*      */       public void mouseExited(MouseEvent e) {
/*  367 */         CustomRequestDialog.this.setCursor(Cursor.getDefaultCursor());
/*      */       }
/*      */     });
/*  371 */     c.gridy = (gridY++);
/*  372 */     c.fill = 2;
/*  373 */     content.add(this.errorPanel, c);
/*      */ 
/*  377 */     this.buttonsPanel = new JPanel(new GridBagLayout());
/*  378 */     JLocalizableButton bOK = new JLocalizableNoWidthButton("button.ok", null);
/*  379 */     this.bRefresh = new JLocalizableNoWidthButton("button.refresh", null);
/*  380 */     GridBagConstraints gbc = new GridBagConstraints();
/*  381 */     GridBagLayoutHelper.add(0, 0, 1.0D, 0.0D, 1, 1, 0, 0, 0, 0, 1, 10, gbc, this.buttonsPanel, bOK);
/*  382 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 5, 0, 0, 0, 1, 10, gbc, this.buttonsPanel, this.bRefresh);
/*  383 */     c.gridy = (gridY++);
/*  384 */     c.fill = 2;
/*  385 */     content.add(this.buttonsPanel, c);
/*      */ 
/*  387 */     bOK.addMouseListener(new MouseAdapter(bOK, side, instrument) {
/*      */       public void mouseEntered(MouseEvent e) {
/*  389 */         GreedContext.setConfig("timein", Long.valueOf(System.currentTimeMillis()));
/*  390 */         GreedContext.setConfig("control", this.val$bOK);
/*      */ 
/*  392 */         if (GreedContext.isSmspcEnabled()) {
/*  393 */           OrderSide orderSide = OfferSide.BID == this.val$side ? OrderSide.BUY : OrderSide.SELL;
/*  394 */           BigDecimal amountBDValue = CustomRequestDialog.this.amountSpinner.getAmountValueInMillions(Instrument.fromString(this.val$instrument));
/*  395 */           if (amountBDValue != null)
/*      */             try {
/*  397 */               BigDecimal amount = amountBDValue.multiply(GuiUtilsAndConstants.ONE_MILLION);
/*  398 */               SignalMessage signal = new SignalMessage(this.val$instrument, "tbsb", orderSide, amount, GreedContext.encodeAuth(), "0");
/*  399 */               GreedContext.publishEvent(new SignalAction(this, signal));
/*      */             } catch (NumberFormatException e1) {
/*  401 */               CustomRequestDialog.LOGGER.debug(e1.getMessage());
/*      */             }
/*      */         }
/*      */       }
/*      */ 
/*      */       public void mouseExited(MouseEvent e)
/*      */       {
/*  409 */         GuiUtilsAndConstants.sendResetSignal(this);
/*      */       }
/*      */     });
/*  413 */     bOK.addActionListener(new ActionListener(instrument, bOK)
/*      */     {
/*      */       private OrderConfirmationDialog ocd;
/*      */ 
/*      */       public void actionPerformed(ActionEvent actionEvent)
/*      */       {
/*  420 */         ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*      */ 
/*  422 */         if (clientSettingsStorage.restoreOrderValidationOn()) {
/*  423 */           ValidateOrder.OrderValidationBean result = CustomRequestDialog.this.validateOrder();
/*      */ 
/*  425 */           if ((result != null) && (!result.getMessages().isEmpty())) {
/*  426 */             this.ocd = new OrderConfirmationDialog(result, true, CustomRequestDialog.this.crd, CustomRequestDialog.this.editMode);
/*      */ 
/*  428 */             MarketView marketView = (MarketView)GreedContext.get("marketView");
/*      */ 
/*  430 */             for (ActionListener i : this.ocd.getBOK().getActionListeners()) {
/*  431 */               this.ocd.getBOK().removeActionListener(i);
/*      */             }
/*      */ 
/*  434 */             this.ocd.getBOK().addActionListener(new ActionListener()
/*      */             {
/*      */               public void actionPerformed(ActionEvent e) {
/*  437 */                 if (CustomRequestDialog.this.placeOrder(null)) CustomRequestDialog.this.dispose();
/*  438 */                 CustomRequestDialog.4.this.ocd.dispose();
/*      */               }
/*      */             });
/*  443 */             this.ocd.onMarketState(marketView.getLastMarketState(this.val$instrument));
/*  444 */             this.ocd.setLocationRelativeTo(this.val$bOK);
/*  445 */             this.ocd.setDeferedTradeLog(false);
/*  446 */             this.ocd.setVisible(true);
/*      */           }
/*  449 */           else if (CustomRequestDialog.this.placeOrder(null)) { CustomRequestDialog.this.dispose();
/*      */           }
/*      */         }
/*  452 */         else if (CustomRequestDialog.this.placeOrder(null)) { CustomRequestDialog.this.dispose();
/*      */         }
/*      */       }
/*      */     });
/*  458 */     this.bRefresh.addActionListener(new ActionListener(instrument, side) {
/*      */       public void actionPerformed(ActionEvent e) {
/*  460 */         CustomRequestDialog.this.refresh();
/*  461 */         CustomRequestDialog.this.refreshPriceField(this.val$instrument, this.val$side);
/*      */       }
/*      */     });
/*  465 */     this.rbGTC.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  468 */         CustomRequestDialog.this.pGoodFor.setVisible(false);
/*  469 */         CustomRequestDialog.this.pGoodTill.setVisible(false);
/*  470 */         CustomRequestDialog.this.pack();
/*      */       }
/*      */     });
/*  473 */     this.rbGF.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  475 */         CustomRequestDialog.this.setHeight();
/*  476 */         CustomRequestDialog.this.pGoodFor.setVisible(true);
/*  477 */         CustomRequestDialog.this.pGoodTill.setVisible(false);
/*  478 */         CustomRequestDialog.this.pack();
/*      */       }
/*      */     });
/*  482 */     this.rbGT.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  484 */         CustomRequestDialog.this.setHeight();
/*  485 */         CustomRequestDialog.this.pGoodFor.setVisible(false);
/*  486 */         CustomRequestDialog.this.pGoodTill.setVisible(true);
/*  487 */         CustomRequestDialog.this.pack();
/*      */       }
/*      */     });
/*  491 */     if (!this.editMode) refreshPriceField(instrument, side);
/*      */ 
/*  494 */     Container cont = getContentPane();
/*  495 */     cont.setLayout(new BoxLayout(cont, 1));
/*  496 */     cont.add(new JLocalizableHeaderPanel(titleKey, titleParams, true));
/*  497 */     cont.add(content);
/*  498 */     setResizable(false);
/*  499 */     setAlwaysOnTop(true);
/*      */ 
/*  501 */     addWindowListener(new WindowAdapter() {
/*      */       public void windowClosed(WindowEvent e) {
/*  503 */         CustomRequestDialog.this.cleanup();
/*      */       }
/*      */     });
/*  507 */     if (this.storage.restoreApplyTimeValidationToAllMarketOrders()) {
/*  508 */       this.rbGF.doClick();
/*      */     }
/*  510 */     pack();
/*      */   }
/*      */ 
/*      */   public ValidateOrder.OrderValidationBean validateOrder()
/*      */   {
/*      */     try
/*      */     {
/*  518 */       double stopValue = Double.parseDouble(this.priceField.getText());
/*  519 */       return ValidateOrder.validateBidOfferPlace(this.side.name(), this.instrument, stopValue);
/*      */     } catch (Exception e) {
/*      */     }
/*  522 */     return null;
/*      */   }
/*      */ 
/*      */   public void setHeight()
/*      */   {
/*  527 */     int H = this.amountSpinner.getHeight();
/*  528 */     this.timeSpinner.getEditor().setPreferredSize(new Dimension(0, H));
/*      */   }
/*      */ 
/*      */   private void refreshPriceField(String instrument, OfferSide side) {
/*      */     try {
/*  533 */       CurrencyOffer bestOffer = ((MarketView)GreedContext.get("marketView")).getBestOffer(instrument, OfferSide.BID == side ? OfferSide.BID : OfferSide.ASK);
/*  534 */       if ((bestOffer == null) || (bestOffer.getPrice() == null) || (bestOffer.getPrice().getValue() == null)) {
/*  535 */         return;
/*      */       }
/*  537 */       this.priceField.setText(bestOffer.getPrice().getValue().stripTrailingZeros().toPlainString());
/*  538 */       drawBidOfferLine();
/*      */     } catch (Exception e) {
/*  540 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void refresh() {
/*  545 */     TimeString ts = new TimeString();
/*  546 */     ts.setTime(ts.getTime() + 60000L);
/*  547 */     this.timeSpinner.setValue(ts);
/*  548 */     this.dateChooser.setDate(new Date(ts.getTime()));
/*      */   }
/*      */ 
/*      */   public boolean placeOrder(String tag) {
/*  552 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/*      */ 
/*  555 */     if (this.priceField.getText().length() == 0) {
/*  556 */       this.priceField.showMessage(LocalizationManager.getText("empty.field.message"));
/*  557 */       return false;
/*      */     }
/*      */ 
/*  561 */     if (!isPriceValid()) {
/*  562 */       refreshPriceField(this.instrument, this.side);
/*  563 */       this.priceField.showMessage("label.error.invalid.price.entered");
/*  564 */       return false;
/*      */     }
/*      */ 
/*  567 */     if (!isDateValid()) {
/*  568 */       this.errorPanel.setVisible(true);
/*  569 */       pack();
/*  570 */       return false;
/*      */     }
/*      */ 
/*  573 */     if ((!this.amountSpinner.validateEditor()) || (!this.goodForSpinner.validateEditor()))
/*      */     {
/*  575 */       return false;
/*      */     }
/*      */ 
/*  578 */     long ttl = -1L;
/*  579 */     Calendar calendar = Calendar.getInstance();
/*  580 */     calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  581 */     if (this.rbGF.isSelected())
/*      */       try {
/*  583 */         ttl = GuiUtilsAndConstants.ONE_THUSAND.multiply((BigDecimal)this.goodForSpinner.getValue()).multiply(((JTimeUnitComboBox)this.comboBox).getSelectedTime()).intValue();
/*      */       } catch (Exception e) {
/*  585 */         LOGGER.error(e.getMessage(), e);
/*  586 */         ttl = -1L;
/*      */       }
/*  588 */     else if (this.rbGT.isSelected())
/*      */     {
/*      */       try
/*      */       {
/*  592 */         Date d = this.sdf.parse(this.timeSpinner.getValue().toString());
/*  593 */         calendar.setTime(d);
/*  594 */         int h = calendar.get(11);
/*  595 */         int m = calendar.get(12);
/*  596 */         int s = calendar.get(13);
/*  597 */         calendar.setTimeZone(TimeZone.getDefault());
/*  598 */         calendar.setTime(this.dateChooser.getDate());
/*  599 */         int dom = calendar.get(5);
/*  600 */         int moy = calendar.get(2);
/*  601 */         int y = calendar.get(1);
/*  602 */         calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  603 */         calendar.set(y, moy, dom, h, m, s);
/*  604 */         ttl = calendar.getTime().getTime();
/*      */       } catch (ParseException e) {
/*  606 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */ 
/*  610 */     String[] currencies = this.instrument.split("/");
/*      */ 
/*  613 */     List orders = new ArrayList();
/*  614 */     OrderGroupMessage orderGroup = new OrderGroupMessage();
/*      */ 
/*  616 */     orderGroup.setTimestamp(new Date());
/*  617 */     orderGroup.setInstrument(this.instrument);
/*      */ 
/*  619 */     OrderMessage order = new OrderMessage();
/*      */ 
/*  621 */     if (this.editMode) {
/*  622 */       orderGroup.setOrderGroupId(this.order.getOrderGroupId());
/*      */ 
/*  624 */       order.setOrderState(this.order.getOrderState());
/*  625 */       order.setParentOrderId(this.order.getParentOrderId());
/*      */     }
/*  627 */     order.setTag(tag);
/*  628 */     order.setPlaceOffer(Boolean.valueOf(true));
/*  629 */     order.setSide(this.side == OfferSide.BID ? OrderSide.BUY : OrderSide.SELL);
/*  630 */     order.setOrderDirection(OrderDirection.OPEN);
/*      */ 
/*  632 */     if ((this.order != null) && (this.order.getAmount() != null))
/*  633 */       order.setAmount(this.order.getAmount());
/*      */     else {
/*  635 */       order.setAmount(new Money(this.amountSpinner.getAmountValueInMillions(Instrument.fromString(this.instrument)).multiply(GuiUtilsAndConstants.ONE_MILLION), Currency.getInstance(currencies[0])));
/*      */     }
/*      */ 
/*  638 */     order.setPriceClient(new Money(this.priceField.getText(), currencies[0]));
/*  639 */     order.setOrderGroupId(orderGroup.getOrderGroupId());
/*  640 */     if (this.editMode) {
/*  641 */       order.setOrderId(this.order.getOrderId());
/*      */     }
/*  643 */     order.setInstrument(this.instrument);
/*      */ 
/*  645 */     if (LOGGER.isTraceEnabled()) {
/*  646 */       LOGGER.trace("ttl = " + ttl);
/*      */     }
/*  648 */     if (-1L != ttl) {
/*  649 */       order.setExecTimeoutMillis(Long.valueOf(ttl));
/*      */     } else {
/*  651 */       long currentPlatformTime = ((ApplicationClock)GreedContext.get("applicationClock")).getTime();
/*  652 */       order.setExecTimeoutMillis(Long.valueOf(currentPlatformTime + 3153600000000L));
/*      */     }
/*      */ 
/*  655 */     if (this.order != null) {
/*  656 */       order.setExternalSysId(this.order.getExternalSysId());
/*      */     }
/*  658 */     if (order.getExternalSysId() == null) {
/*  659 */       String tagForOrder = DDSAgent.generateLabel(order);
/*  660 */       order.setExternalSysId(tagForOrder);
/*  661 */       order.setSignalId(tagForOrder);
/*      */     }
/*      */ 
/*  664 */     if ((GreedContext.isSignalServerInUse()) && (this.order != null)) {
/*  665 */       String id = this.order.getExternalSysId();
/*  666 */       String groupId = id.substring(0, id.indexOf("_"));
/*  667 */       orderGroup.setExternalSysId(groupId);
/*  668 */       orderGroup.setSignalId(groupId);
/*      */     }
/*      */ 
/*  671 */     orders.add(order);
/*  672 */     orderGroup.setOrders(orders);
/*      */ 
/*  674 */     if (orderGroup.getExternalSysId() == null) PlatformInitUtils.setExtSysIdForOrderGroup(orderGroup);
/*  675 */     AppActionEvent orderEntryAction = new OrderEntryAction(this, false, orderGroup);
/*  676 */     GreedContext.publishEvent(orderEntryAction);
/*      */ 
/*  678 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean isDateValid() {
/*  682 */     if (this.rbGTC.isSelected()) return true;
/*  683 */     Calendar calendar = Calendar.getInstance();
/*  684 */     calendar.setTime(new Date());
/*  685 */     calendar.set(11, 0);
/*  686 */     calendar.set(12, 0);
/*  687 */     calendar.set(13, 0);
/*  688 */     calendar.set(14, 0);
/*  689 */     Date now = calendar.getTime();
/*  690 */     calendar.setTime(this.dateChooser.getDate());
/*  691 */     calendar.set(11, 0);
/*  692 */     calendar.set(12, 0);
/*  693 */     calendar.set(13, 0);
/*  694 */     calendar.set(14, 0);
/*  695 */     Date then = calendar.getTime();
/*  696 */     if (then.before(now)) {
/*  697 */       this.errorLabel.setText("label.error.select.date");
/*  698 */       return false;
/*      */     }
/*      */ 
/*  701 */     TimeVerifier verifier = this.timeSpinner.getVerifier();
/*  702 */     if (!verifier.verify(this.timeSpinner.getEditor())) {
/*  703 */       this.errorLabel.setText("label.error.valid.time");
/*  704 */       return false;
/*      */     }
/*  706 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean isPriceValid()
/*      */   {
/*  711 */     if (this.editMode) {
/*  712 */       return true;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  719 */       price = new BigDecimal(this.priceField.getText());
/*      */     }
/*      */     catch (NumberFormatException e)
/*      */     {
/*      */       BigDecimal price;
/*  721 */       return false;
/*      */     }
/*      */ 
/*  744 */     return true;
/*      */   }
/*      */ 
/*      */   public void setPlaceData(String amount, String price, int mins)
/*      */   {
/*  896 */     this.amountSpinner.setValue(new BigDecimal(amount));
/*  897 */     this.priceField.setText(price);
/*  898 */     drawBidOfferLine();
/*  899 */     if (mins > 0) {
/*  900 */       this.rbGF.doClick();
/*  901 */       this.goodForSpinner.setValue(new Integer(mins));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void cleanup() {
/*  906 */     this.dateChooser.cleanup();
/*  907 */     removeBidOfferLine();
/*      */   }
/*      */ 
/*      */   public void setPriceDisableRefresh(double price, ActionListener cancelActionListener) {
/*  911 */     if (!Double.isNaN(price)) {
/*  912 */       this.priceField.setText(BigDecimal.valueOf(price).toPlainString());
/*      */     }
/*  914 */     drawBidOfferLine();
/*  915 */     this.bRefresh.setVisible(false);
/*  916 */     GridBagConstraints gbc = new GridBagConstraints();
/*  917 */     JLocalizableNoWidthButton cancelButton = new JLocalizableNoWidthButton("button.cancel", null);
/*  918 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 5, 0, 0, 0, 1, 10, gbc, this.buttonsPanel, cancelButton);
/*  919 */     cancelButton.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  921 */         CustomRequestDialog.this.dispose();
/*      */       }
/*      */     });
/*  924 */     cancelButton.addActionListener(cancelActionListener);
/*      */   }
/*      */ 
/*      */   private void drawBidOfferLine() {
/*  928 */     String objectName = "_bid_offer_line_" + this.lineId + this.instrument;
/*  929 */     if ((this.priceField.isEnabled()) && (this.priceField.getText() != null) && (!this.priceField.getText().equals("")))
/*      */     {
/*  932 */       BigDecimal entry = new BigDecimal(this.priceField.getText());
/*  933 */       drawLine(objectName, "ENTRY", entry, this.priceField);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void drawLine(String objectName, String type, BigDecimal price, PriceSpinner priceField)
/*      */   {
/*      */     List chartObjects;
/*  938 */     if ((GreedContext.isStrategyAllowed()) && (getDdsChartsController() != null)) {
/*  939 */       Instrument apiInstrument = Instrument.fromString(this.instrument);
/*  940 */       Set charts = getDdsChartsController().getICharts(apiInstrument);
/*  941 */       chartObjects = new ArrayList();
/*  942 */       for (IChart iChart : charts) {
/*  943 */         IChartObject hLine = iChart.get(objectName);
/*  944 */         if (hLine == null) {
/*  945 */           hLine = iChart.drawUnlocked(objectName, IChart.Type.PRICEMARKER, 0L, price.doubleValue());
/*  946 */           if (hLine != null) {
/*  947 */             hLine.setMenuEnabled(false);
/*  948 */             Color color = ((IChartWrapper)iChart).getThemeColor(this.side == OfferSide.BID ? ITheme.ChartElement.ORDER_OPEN_BUY : ITheme.ChartElement.ORDER_OPEN_SELL);
/*  949 */             hLine.setColor(color);
/*  950 */             hLine.setText(type);
/*  951 */             hLine.setSticky(false);
/*  952 */             BasicStroke stroke = new BasicStroke(1.0F, 0, 0, 10.0F, new float[] { 2.0F, 4.0F }, 0.0F);
/*  953 */             hLine.setStroke(stroke);
/*  954 */             hLine.setChartObjectListener(new ChartObjectAdapter(priceField, chartObjects)
/*      */             {
/*      */               public void moved(ChartObjectEvent e) {
/*  957 */                 double price = StratUtils.round05Pips(e.getNewDouble());
/*  958 */                 this.val$priceField.setText(BigDecimal.valueOf(price).stripTrailingZeros().toPlainString());
/*  959 */                 e.setNewDouble(price);
/*      */ 
/*  964 */                 for (IChartObject chartObject : this.val$chartObjects) {
/*  965 */                   chartObject.move(0L, price);
/*      */                 }
/*  967 */                 CustomRequestDialog.this.getDdsChartsController().refreshChartsContent();
/*      */               }
/*      */ 
/*      */               public void deleted(ChartObjectEvent e)
/*      */               {
/*  973 */                 e.cancel();
/*      */               }
/*      */             });
/*  976 */             chartObjects.add(hLine);
/*      */           }
/*      */         } else {
/*  979 */           hLine.move(0L, price.doubleValue());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void removeBidOfferLine() {
/*  986 */     String objectName = "_bid_offer_line_" + this.lineId + this.instrument;
/*  987 */     removeLine(objectName);
/*      */   }
/*      */ 
/*      */   private void removeLine(String objectName) {
/*  991 */     if ((GreedContext.isStrategyAllowed()) && (getDdsChartsController() != null)) {
/*  992 */       Instrument apiInstrument = Instrument.fromString(this.instrument);
/*  993 */       Set charts = getDdsChartsController().getICharts(apiInstrument);
/*  994 */       for (IChart chart : charts)
/*  995 */         chart.remove(objectName);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Integer getChartId()
/*      */   {
/* 1028 */     return this.chartId;
/*      */   }
/*      */ 
/*      */   public DDSChartsController getDdsChartsController() {
/* 1032 */     if (this.ddsChartsController == null) {
/* 1033 */       this.ddsChartsController = ((DDSChartsController)GreedContext.get("chartsController"));
/*      */     }
/* 1035 */     return this.ddsChartsController;
/*      */   }
/*      */ 
/*      */   private static class JLocalizableNoWidthButton extends JLocalizableButton
/*      */   {
/*      */     private JLocalizableNoWidthButton(String textKey)
/*      */     {
/* 1002 */       super();
/*      */     }
/*      */ 
/*      */     public Dimension getMinimumSize()
/*      */     {
/* 1007 */       Dimension minimumSize = super.getMinimumSize();
/* 1008 */       minimumSize.width = 0;
/* 1009 */       return minimumSize;
/*      */     }
/*      */ 
/*      */     public Dimension getMaximumSize()
/*      */     {
/* 1014 */       Dimension maximumSize = super.getMaximumSize();
/* 1015 */       maximumSize.width = 0;
/* 1016 */       return maximumSize;
/*      */     }
/*      */ 
/*      */     public Dimension getPreferredSize()
/*      */     {
/* 1021 */       Dimension preferredSize = super.getPreferredSize();
/* 1022 */       preferredSize.width = 0;
/* 1023 */       return preferredSize;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class JTimeSpinner extends JSpinner
/*      */   {
/*      */     private JFormattedTextField editor;
/*      */     private CustomRequestDialog.TimeString timeString;
/*  807 */     private int caretPosition = 0;
/*      */ 
/*      */     public JTimeSpinner()
/*      */     {
/*  811 */       this.timeString = new CustomRequestDialog.TimeString(CustomRequestDialog.this);
/*      */     }
/*      */ 
/*      */     protected JComponent createEditor(SpinnerModel model)
/*      */     {
/*  816 */       this.editor = new JFormattedTextField() {
/*      */         protected void processFocusEvent(FocusEvent e) {
/*  818 */           if (1005 == e.getID())
/*      */             try {
/*  820 */               CustomRequestDialog.JTimeSpinner.access$1502(CustomRequestDialog.JTimeSpinner.this, new CustomRequestDialog.TimeString(CustomRequestDialog.this, CustomRequestDialog.this.sdf.parse(CustomRequestDialog.JTimeSpinner.this.editor.getText())));
/*      */             }
/*      */             catch (ParseException e1) {
/*      */             }
/*  824 */           super.processFocusEvent(e);
/*      */         }
/*      */       };
/*  827 */       this.editor.setMargin(new Insets(0, 5, 0, 5));
/*  828 */       this.editor.setColumns(8);
/*  829 */       this.editor.setValue(new CustomRequestDialog.TimeString(CustomRequestDialog.this));
/*  830 */       this.editor.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() {
/*      */         public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
/*  832 */           return new CustomRequestDialog.TimeFormatter(CustomRequestDialog.this, null);
/*      */         }
/*      */       });
/*  835 */       this.editor.setInputVerifier(new CustomRequestDialog.TimeVerifier(CustomRequestDialog.this, null));
/*  836 */       this.editor.setFocusLostBehavior(1);
/*  837 */       return this.editor;
/*      */     }
/*      */ 
/*      */     public JComponent getEditor() {
/*  841 */       return this.editor;
/*      */     }
/*      */ 
/*      */     public Object getValue() {
/*  845 */       return this.timeString;
/*      */     }
/*      */ 
/*      */     public void setValue(Object value) {
/*  849 */       this.timeString = ((CustomRequestDialog.TimeString)value);
/*  850 */       this.editor.setValue(value);
/*  851 */       this.editor.setCaretPosition(this.caretPosition);
/*      */     }
/*      */ 
/*      */     public Object getPreviousValue() {
/*  855 */       return adjustTime(-1);
/*      */     }
/*      */ 
/*      */     public Object getNextValue() {
/*  859 */       return adjustTime(1);
/*      */     }
/*      */ 
/*      */     private CustomRequestDialog.TimeString adjustTime(int direction) {
/*  863 */       String TIME_SEPARATOR = ":";
/*  864 */       String text = this.editor.getText();
/*  865 */       this.caretPosition = this.editor.getCaretPosition();
/*  866 */       int separatorHours = text.indexOf(":");
/*  867 */       int separatorMinutes = text.indexOf(":", separatorHours + 1);
/*      */       long modifier;
/*      */       long modifier;
/*  869 */       if (this.caretPosition <= separatorHours) {
/*  870 */         modifier = 3600000L;
/*      */       }
/*      */       else
/*      */       {
/*      */         long modifier;
/*  871 */         if (this.caretPosition <= separatorMinutes)
/*  872 */           modifier = 60000L;
/*      */         else {
/*  874 */           modifier = 1000L;
/*      */         }
/*      */       }
/*  877 */       modifier *= direction;
/*  878 */       long oldTime = this.timeString.getTime();
/*  879 */       CustomRequestDialog.TimeString time = new CustomRequestDialog.TimeString(CustomRequestDialog.this);
/*  880 */       time.setTime(oldTime + modifier);
/*  881 */       this.timeString = time;
/*  882 */       return time;
/*      */     }
/*      */ 
/*      */     String getText() {
/*  886 */       return this.editor.getText();
/*      */     }
/*      */ 
/*      */     CustomRequestDialog.TimeVerifier getVerifier() {
/*  890 */       return (CustomRequestDialog.TimeVerifier)this.editor.getInputVerifier();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class TimeString
/*      */   {
/*      */     private long time;
/*      */ 
/*      */     TimeString()
/*      */     {
/*  783 */       this.time = ((ApplicationClock)GreedContext.get("applicationClock")).getTime();
/*      */     }
/*      */ 
/*      */     TimeString(Date date) {
/*  787 */       this.time = date.getTime();
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  791 */       return CustomRequestDialog.this.sdf.format(new Date(this.time));
/*      */     }
/*      */ 
/*      */     long getTime() {
/*  795 */       return this.time;
/*      */     }
/*      */ 
/*      */     void setTime(long millis) {
/*  799 */       this.time = millis;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class TimeFormatter extends JFormattedTextField.AbstractFormatter
/*      */   {
/*      */     private TimeFormatter()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Object stringToValue(String text)
/*      */       throws ParseException
/*      */     {
/*  771 */       return new CustomRequestDialog.TimeString(CustomRequestDialog.this, CustomRequestDialog.this.sdf.parse(text));
/*      */     }
/*      */ 
/*      */     public String valueToString(Object value) throws ParseException {
/*  775 */       return CustomRequestDialog.this.sdf.format(Long.valueOf(((CustomRequestDialog.TimeString)value).getTime()));
/*      */     }
/*      */   }
/*      */ 
/*      */   private class TimeVerifier extends InputVerifier
/*      */   {
/*      */     private TimeVerifier()
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean verify(JComponent input)
/*      */     {
/*  750 */       if ((input instanceof JFormattedTextField)) {
/*  751 */         String text = ((JFormattedTextField)input).getText();
/*  752 */         JFormattedTextField.AbstractFormatter formatter = ((JFormattedTextField)input).getFormatter();
/*      */         try {
/*  754 */           formatter.stringToValue(text);
/*  755 */           return true;
/*      */         } catch (ParseException e) {
/*  757 */           return false;
/*      */         }
/*      */       }
/*  760 */       return true;
/*      */     }
/*      */ 
/*      */     public boolean shouldYieldFocus(JComponent input) {
/*  764 */       return verify(input);
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.CustomRequestDialog
 * JD-Core Version:    0.6.0
 */