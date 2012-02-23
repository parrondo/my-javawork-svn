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
/*      */ import com.dukascopy.dds2.calc.PriceUtil;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.actions.OrderEntryAction;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*      */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*      */ import com.dukascopy.dds2.greed.gui.component.PriceAmountTextFieldM;
/*      */ import com.dukascopy.dds2.greed.gui.component.PriceSpinner;
/*      */ import com.dukascopy.dds2.greed.gui.component.dialog.components.OrderTimeLimitationPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder.OrderValidationBean;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableHeaderPanel;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableQuoterPanel;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*      */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountLabel;
/*      */ import com.dukascopy.dds2.greed.gui.util.spinners.AmountJSpinner;
/*      */ import com.dukascopy.dds2.greed.gui.util.spinners.CommonJSpinner;
/*      */ import com.dukascopy.dds2.greed.gui.util.spinners.TrailingStepJSpinner;
/*      */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*      */ import com.dukascopy.dds2.greed.model.MarketView;
/*      */ import com.dukascopy.dds2.greed.model.StopOrderType;
/*      */ import com.dukascopy.dds2.greed.util.EmergencyLogger;
/*      */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*      */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*      */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.OfferSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderState;
/*      */ import com.dukascopy.transport.common.model.type.Position;
/*      */ import com.dukascopy.transport.common.model.type.PositionSide;
/*      */ import com.dukascopy.transport.common.model.type.StopDirection;
/*      */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.RoundingMode;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.ParseException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Currency;
/*      */ import java.util.Enumeration;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class StopOrderEditPanel extends AStopOrderEditPanel
/*      */ {
/*  107 */   private static final Logger LOGGER = LoggerFactory.getLogger(StopOrderEditPanel.class);
/*      */ 
/*  109 */   private static int lineCounter = 0;
/*      */   private final int lineId;
/*      */   public static final String ORDER_ENTRY_LINE = "_soep_order_entry_line";
/*      */   public static final String ORDER_STOP_LOSS_LINE = "_soep_order_stop_loss_line";
/*      */   public static final String ORDER_TAKE_PROFIT_LINE = "_soep_order_take_profit_line";
/*      */   public static final String ORDER_IFD_STOP_LINE = "_soep_order_ifd_stop_line";
/*      */   public static final String ORDER_IFD_LIMIT_LINE = "_soep_order_ifd_limit_line";
/*      */   public static final String HLINE_TYPE_ENTRY = "ENTRY";
/*      */   public static final String HLINE_TYPE_SL = "SL";
/*      */   public static final String HLINE_TYPE_TP = "TP";
/*      */   public static final String HLINE_TYPE_IFDS = "IFDS";
/*      */   public static final String HLINE_TYPE_IFDL = "IFDL";
/*      */   public static final String ID_JPANEL_STOPORDEREDITPANEL = "ID_JPANEL_STOPORDEREDITPANEL";
/*      */   public static final String ID_JTEXT_AMOUNT = "ID_JTEXT_AMOUNT";
/*      */   public static final String ID_CHECKBOX_SLIPPAGE = "ID_CHECKBOX_SLIPPAGE";
/*      */   public static final String ID_JTEXT_SLIPPAGE = "ID_JTEXT_SLIPPAGE";
/*      */   public static final String ID_JBUTTON_PRICE = "ID_JBUTTON_PRICE";
/*      */   public static final String ID_JSPINNER_PRICE = "ID_JSPINNER_PRICE";
/*      */   public static final String ID_JBUTTON_SUBMIT = "ID_JBUTTON_SUBMIT";
/*      */   public static final String ID_JPOPUP_POPUP = "ID_JPOPUP_POPUP";
/*  136 */   private final JLocalizableButton submitButton = new JLocalizableButton("button.submit.changes");
/*  137 */   private final JLocalizableButton cancelButton = new JLocalizableButton("button.cancel");
/*  138 */   private final PriceSpinner stopPriceField = new PriceSpinner();
/*  139 */   private final PriceAmountTextFieldM slippageField = new PriceAmountTextFieldM(7);
/*      */   private StopOrderType stopOrderType;
/*      */   private OrderGroupMessage orderGroup;
/*      */   private String instrument;
/*  144 */   private OrderMessage stopOrder = null;
/*      */   private BigDecimal amount;
/*      */   private String slippageAmount;
/*      */   private BigDecimal price;
/*      */   private PositionSide positionSide;
/*  149 */   private boolean newOrder = false;
/*      */   private String orderId;
/*      */   private String openingOrderId;
/*      */   private StopDirection stopDirection;
/*      */   private JLocalizableQuoterPanel quoter;
/*      */   private AmountJSpinner amountSpinner;
/*      */   private OrderTimeLimitationPanel timeLimPanel;
/*      */   private JPanel errorPanel;
/*      */   private JLocalizableLabel errorLabel;
/*      */   private CommonJSpinner trailstepSpinner;
/*      */   private JLocalizableButton stopSelectButton;
/*      */   private JFrame parent;
/*      */   private JPopupMenu popup;
/*      */   private JCheckBox slippageCheck;
/*      */   private JCheckBox trailstepCheck;
/*  167 */   private DecimalFormat df = new DecimalFormat("###0.#");
/*      */ 
/*  169 */   private BigDecimal priceStop = null;
/*      */   private DDSChartsController ddsChartsController;
/*  172 */   private ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*  173 */   private MarketView marketView = (MarketView)GreedContext.get("marketView");
/*      */ 
/*      */   public StopOrderEditPanel(JFrame parent, OrderGroupMessage orderGroup, StopOrderType stopOrderType, String editableOrderId, String openingOrderId, BigDecimal priceStop)
/*      */   {
/*  182 */     this.parent = parent;
/*  183 */     this.orderGroup = orderGroup;
/*  184 */     this.stopOrderType = stopOrderType;
/*  185 */     this.orderId = editableOrderId;
/*  186 */     this.openingOrderId = openingOrderId;
/*  187 */     this.priceStop = priceStop;
/*      */ 
/*  189 */     this.instrument = orderGroup.getInstrument();
/*  190 */     setName("ID_JPANEL_STOPORDEREDITPANEL");
/*  191 */     this.lineId = (lineCounter++);
/*      */ 
/*  193 */     build();
/*  194 */     add(Box.createHorizontalStrut(272));
/*      */   }
/*      */ 
/*      */   void build()
/*      */   {
/*  201 */     setName("ID_JPANEL_STOPORDEREDITPANEL");
/*  202 */     setLayout(new BoxLayout(this, 1));
/*      */ 
/*  204 */     JPanel innerPanel = new JPanel();
/*  205 */     innerPanel.setLayout(new BoxLayout(innerPanel, 1));
/*  206 */     innerPanel.setBorder(getBorder(innerPanel));
/*      */ 
/*  209 */     boolean amountEnabled = false;
/*  210 */     Position position = getPosition();
/*      */ 
/*  212 */     this.stopDirection = null;
/*      */ 
/*  217 */     if ((LOGGER.isDebugEnabled()) && (this.orderId != null))
/*  218 */       LOGGER.debug("orderGroup.getOrderById(orderId, null) != null: " + this.orderGroup.getOrderById(this.orderId, null));
/*      */     String title;
/*  221 */     switch (18.$SwitchMap$com$dukascopy$dds2$greed$model$StopOrderType[this.stopOrderType.ordinal()]) {
/*      */     case 1:
/*  223 */       title = "header.entry";
/*      */       try {
/*  225 */         if (this.orderId != null) {
/*  226 */           if (this.orderGroup.getOrderById(this.orderId, null) != null)
/*  227 */             this.stopOrder = new OrderMessage(new ProtocolMessage(this.orderGroup.getOrderById(this.orderId, null).toString()));
/*      */           else
/*  229 */             this.stopOrder = null;
/*      */         }
/*      */         else
/*  232 */           this.stopDirection = (this.positionSide.equals(PositionSide.LONG) ? StopDirection.BID_LESS : StopDirection.ASK_GREATER);
/*      */       }
/*      */       catch (ParseException e) {
/*  235 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*  237 */       amountEnabled = true;
/*  238 */       break;
/*      */     case 2:
/*  240 */       title = "header.s.loss";
/*      */       try {
/*  242 */         if (this.orderId != null) {
/*  243 */           if (this.orderGroup.getOrderById(this.orderId, null) != null) {
/*  244 */             this.stopOrder = new OrderMessage(new ProtocolMessage(this.orderGroup.getOrderById(this.orderId, null).toString()));
/*      */           } else {
/*  246 */             if (LOGGER.isDebugEnabled()) {
/*  247 */               LOGGER.debug("failed to locate order byId " + this.orderId);
/*      */             }
/*  249 */             this.stopOrder = null;
/*      */           }
/*      */         }
/*  252 */         else this.stopDirection = (this.positionSide.equals(PositionSide.LONG) ? StopDirection.BID_LESS : StopDirection.ASK_GREATER); 
/*      */       }
/*      */       catch (ParseException e)
/*      */       {
/*  255 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*  257 */       if (!LOGGER.isDebugEnabled()) break;
/*  258 */       LOGGER.debug("stopdirection= " + this.stopDirection + " posSide=" + this.positionSide + " orderId=" + this.orderId); break;
/*      */     case 3:
/*  262 */       title = "header.t.profit";
/*      */       try {
/*  264 */         if (this.orderId != null) {
/*  265 */           if (this.orderGroup.getOrderById(this.orderId, null) != null) {
/*  266 */             this.stopOrder = new OrderMessage(new ProtocolMessage(this.orderGroup.getOrderById(this.orderId, null).toString()));
/*      */           } else {
/*  268 */             if (LOGGER.isDebugEnabled()) {
/*  269 */               LOGGER.debug("failed to locate order byId " + this.orderId);
/*      */             }
/*  271 */             this.stopOrder = null;
/*      */           }
/*      */         }
/*  274 */         else this.stopDirection = (this.positionSide.equals(PositionSide.LONG) ? StopDirection.BID_GREATER : StopDirection.ASK_LESS); 
/*      */       }
/*      */       catch (ParseException e)
/*      */       {
/*  277 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */ 
/*      */     case 4:
/*  281 */       title = "header.ifd.stop";
/*      */       try {
/*  283 */         if (this.orderId != null) {
/*  284 */           if (OrderMessageUtils.getIfDoneOrderById(this.orderId) != null) {
/*  285 */             this.stopOrder = new OrderMessage(new ProtocolMessage(OrderMessageUtils.getIfDoneOrderById(this.orderId).toString()));
/*      */           } else {
/*  287 */             if (LOGGER.isDebugEnabled()) {
/*  288 */               LOGGER.debug("failed to locate order byId " + this.orderId);
/*      */             }
/*  290 */             this.stopOrder = null;
/*      */           }
/*      */         }
/*  293 */         else this.stopDirection = (this.positionSide.equals(PositionSide.LONG) ? StopDirection.BID_LESS : StopDirection.ASK_GREATER); 
/*      */       }
/*      */       catch (ParseException e)
/*      */       {
/*  296 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*  298 */       if (!LOGGER.isDebugEnabled()) break;
/*  299 */       LOGGER.debug("stopdirection= " + this.stopDirection + " posSide=" + this.positionSide + " orderId=" + this.orderId); break;
/*      */     case 5:
/*  303 */       title = "header.ifd.limit";
/*      */       try {
/*  305 */         if (this.orderId != null) {
/*  306 */           if (OrderMessageUtils.getIfDoneOrderById(this.orderId) != null) {
/*  307 */             this.stopOrder = new OrderMessage(new ProtocolMessage(OrderMessageUtils.getIfDoneOrderById(this.orderId).toString()));
/*      */           } else {
/*  309 */             if (LOGGER.isDebugEnabled()) {
/*  310 */               LOGGER.debug("failed to locate order byId " + this.orderId);
/*      */             }
/*  312 */             this.stopOrder = null;
/*      */           }
/*      */         }
/*  315 */         else this.stopDirection = (this.positionSide.equals(PositionSide.LONG) ? StopDirection.BID_GREATER : StopDirection.ASK_LESS); 
/*      */       }
/*      */       catch (ParseException e)
/*      */       {
/*  318 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */ 
/*      */     default:
/*  322 */       title = "";
/*      */     }
/*      */ 
/*  326 */     if ((this.orderId != null) && (this.stopOrder != null)) {
/*  327 */       this.stopDirection = this.stopOrder.getStopDirection();
/*      */ 
/*  333 */       if ((StopDirection.BID_LESS.equals(this.stopDirection)) && ((StopOrderType.STOP_LOSS.equals(this.stopOrderType)) || (StopOrderType.IFD_STOP.equals(this.stopOrderType))))
/*      */       {
/*  335 */         this.positionSide = PositionSide.LONG;
/*  336 */       } else if ((StopDirection.ASK_GREATER.equals(this.stopDirection)) && ((StopOrderType.STOP_LOSS.equals(this.stopOrderType)) || (StopOrderType.IFD_STOP.equals(this.stopOrderType))))
/*      */       {
/*  338 */         this.positionSide = PositionSide.SHORT;
/*  339 */       } else if ((StopDirection.BID_GREATER.equals(this.stopDirection)) && ((StopOrderType.STOP_LOSS.equals(this.stopOrderType)) || (StopOrderType.IFD_STOP.equals(this.stopOrderType))))
/*      */       {
/*  341 */         this.positionSide = PositionSide.SHORT;
/*  342 */       } else if ((StopDirection.BID_GREATER.equals(this.stopDirection)) && ((StopOrderType.TAKE_PROFIT.equals(this.stopOrderType)) || (StopOrderType.IFD_LIMIT.equals(this.stopOrderType))))
/*      */       {
/*  344 */         this.positionSide = PositionSide.LONG;
/*  345 */       } else if ((StopDirection.ASK_LESS.equals(this.stopDirection)) && ((StopOrderType.TAKE_PROFIT.equals(this.stopOrderType)) || (StopOrderType.IFD_LIMIT.equals(this.stopOrderType))))
/*      */       {
/*  347 */         this.positionSide = PositionSide.SHORT;
/*  348 */       } else if ((StopDirection.BID_GREATER.equals(this.stopDirection)) && (StopOrderType.OPEN_IF.equals(this.stopOrderType))) {
/*  349 */         if ((null != this.stopOrder.getPriceTrailingLimit()) && (this.stopOrder.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) >= 0) && (OrderSide.SELL.equals(this.stopOrder.getSide())))
/*      */         {
/*  353 */           this.positionSide = PositionSide.SHORT;
/*      */         }
/*  355 */         else this.positionSide = PositionSide.LONG;
/*      */       }
/*  357 */       else if ((StopDirection.ASK_LESS.equals(this.stopDirection)) && (StopOrderType.OPEN_IF.equals(this.stopOrderType))) {
/*  358 */         if ((null != this.stopOrder.getPriceTrailingLimit()) && (this.stopOrder.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) >= 0) && (OrderSide.BUY.equals(this.stopOrder.getSide())))
/*      */         {
/*  361 */           this.positionSide = PositionSide.LONG;
/*      */         }
/*  363 */         else this.positionSide = PositionSide.SHORT;
/*      */       }
/*      */       else {
/*  366 */         LOGGER.error("could not calculate unexpected stopDirection=" + this.stopDirection + " stopOrderType=" + this.stopOrderType);
/*      */       }
/*  368 */       if (LOGGER.isDebugEnabled()) {
/*  369 */         LOGGER.debug("stopDirection = " + this.stopDirection);
/*  370 */         LOGGER.debug("postionside defered = " + this.positionSide);
/*      */       }
/*      */     }
/*      */ 
/*  374 */     if (null == this.stopOrder) {
/*  375 */       this.stopOrder = createNewStopOrder();
/*  376 */       this.stopOrder.setStopDirection(this.stopDirection);
/*  377 */       if (LOGGER.isDebugEnabled()) {
/*  378 */         LOGGER.debug("set to new order stopDirection = " + this.stopDirection);
/*      */       }
/*  380 */       this.stopOrder.setSide(this.positionSide.equals(PositionSide.LONG) ? OrderSide.SELL : OrderSide.BUY);
/*      */     }
/*      */ 
/*  383 */     String[] params = new String[1];
/*  384 */     params[0] = this.instrument;
/*      */ 
/*  386 */     JLocalizableHeaderPanel header = new JLocalizableHeaderPanel(title, params, true);
/*  387 */     add(header);
/*  388 */     this.quoter = new JLocalizableQuoterPanel(this.orderGroup.getInstrument(), this);
/*  389 */     this.quoter.setTradable(false);
/*      */ 
/*  392 */     CurrencyOffer bestBid = this.marketView.getBestOffer(this.instrument, OfferSide.BID);
/*  393 */     CurrencyOffer bestAsk = this.marketView.getBestOffer(this.instrument, OfferSide.ASK);
/*  394 */     BigDecimal bestBidPrice = bestBid != null ? bestBid.getPrice().getValue() : BigDecimal.ZERO;
/*  395 */     BigDecimal bestAskPrice = bestAsk != null ? bestAsk.getPrice().getValue() : BigDecimal.ZERO;
/*      */ 
/*  397 */     this.quoter.onTick(Instrument.fromString(this.instrument));
/*      */ 
/*  399 */     innerPanel.add(this.quoter);
/*  400 */     innerPanel.add(Box.createVerticalStrut(10));
/*      */ 
/*  402 */     LotAmountLabel amountLabel = new LotAmountLabel(Instrument.fromString(this.instrument));
/*      */ 
/*  404 */     this.amountSpinner = AmountJSpinner.getInstance(Instrument.fromString(this.instrument));
/*  405 */     this.amountSpinner.setHorizontalAlignment(4);
/*  406 */     this.stopPriceField.setHorizontalAlignment(4);
/*      */ 
/*  408 */     BigDecimal amountMils = null;
/*  409 */     if (GreedContext.isMiniFxAccount())
/*  410 */       amountMils = this.stopOrder.getAmount().getValue().divide(GuiUtilsAndConstants.ONE_MILLION, 6, RoundingMode.HALF_UP);
/*      */     else {
/*  412 */       amountMils = this.stopOrder.getAmount().getValue().divide(GuiUtilsAndConstants.ONE_MILLION, 5, RoundingMode.HALF_UP);
/*      */     }
/*      */ 
/*  415 */     BigDecimal currentLotInitAmount = LotAmountChanger.calculateAmountForDifferentLot(amountMils, GuiUtilsAndConstants.ONE_MILLION, LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(this.instrument)));
/*      */ 
/*  419 */     this.amountSpinner.setEnabled(amountEnabled);
/*  420 */     this.amountSpinner.setMinimum(LotAmountChanger.getMinTradableAmount(Instrument.fromString(this.instrument)).compareTo(currentLotInitAmount) > 0 ? currentLotInitAmount : LotAmountChanger.getMinTradableAmount(Instrument.fromString(this.instrument)));
/*  421 */     this.amountSpinner.setStepSize(LotAmountChanger.getAmountStepSize(Instrument.fromString(this.instrument)));
/*  422 */     this.amountSpinner.setPrecision(LotAmountChanger.getAmountPrecision(Instrument.fromString(this.instrument), LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(this.instrument))));
/*  423 */     this.amountSpinner.setValue(currentLotInitAmount);
/*  424 */     this.amount = this.stopOrder.getAmount().getValue();
/*      */ 
/*  426 */     JPanel orderForm = new JPanel();
/*  427 */     orderForm.setLayout(new GridBagLayout());
/*  428 */     GridBagConstraints c = new GridBagConstraints();
/*  429 */     int INSET = 5;
/*  430 */     c.insets = new Insets(5, 5, 0, 5);
/*  431 */     c.weightx = 1.0D;
/*  432 */     c.weighty = 1.0D;
/*  433 */     int gridY = 0;
/*      */ 
/*  435 */     c.gridx = 0; c.gridy = (gridY++);
/*  436 */     c.anchor = 17;
/*  437 */     c.fill = 0;
/*  438 */     orderForm.add(amountLabel, c);
/*  439 */     c.gridx = 1;
/*  440 */     c.fill = 2;
/*  441 */     orderForm.add(this.amountSpinner, c);
/*      */ 
/*  443 */     this.slippageCheck = new JCheckBox();
/*  444 */     this.slippageCheck.setName("ID_CHECKBOX_SLIPPAGE");
/*      */ 
/*  446 */     if (StopOrderType.OPEN_IF.equals(this.stopOrderType)) {
/*  447 */       this.slippageField.setHorizontalAlignment(4);
/*  448 */       JPanel slippagePanel = new JPanel();
/*  449 */       slippagePanel.setLayout(new BoxLayout(slippagePanel, 0));
/*  450 */       slippagePanel.add(this.slippageCheck);
/*  451 */       slippagePanel.add(this.slippageField);
/*  452 */       Money slippage = this.stopOrder.getPriceTrailingLimit();
/*  453 */       if (null != slippage) {
/*  454 */         this.slippageCheck.setSelected(true);
/*  455 */         this.slippageField.setEnabled(true);
/*  456 */         CurrencyOffer bestOffer = OrderSide.BUY.equals(this.stopOrder.getSide()) ? bestAsk : bestBid;
/*  457 */         BigDecimal slippageValue = slippage.getValue().divide(PriceUtil.pipValue(bestOffer.getPrice().getValue()), RoundingMode.HALF_EVEN);
/*      */ 
/*  461 */         this.slippageAmount = this.df.format(slippageValue);
/*  462 */         this.slippageField.setText(this.slippageAmount);
/*      */       } else {
/*  464 */         this.slippageCheck.setSelected(false);
/*  465 */         this.slippageField.setEnabled(false);
/*  466 */         this.slippageField.clear();
/*      */       }
/*      */ 
/*  469 */       c.gridx = 0; c.gridy = (gridY++);
/*  470 */       c.fill = 0;
/*  471 */       orderForm.add(new JLocalizableLabel("label.slippage"), c);
/*  472 */       c.gridx = 1;
/*  473 */       c.fill = 2;
/*  474 */       orderForm.add(slippagePanel, c);
/*      */ 
/*  477 */       this.slippageCheck.addActionListener(new ActionListener() {
/*      */         public void actionPerformed(ActionEvent e) {
/*  479 */           if (!StopOrderEditPanel.this.slippageCheck.isSelected())
/*      */           {
/*  481 */             StopOrderEditPanel.this.slippageField.clear();
/*      */           }
/*  484 */           else if (null != StopOrderEditPanel.this.slippageAmount)
/*  485 */             StopOrderEditPanel.this.slippageField.setText(StopOrderEditPanel.this.slippageAmount);
/*      */           else {
/*  487 */             StopOrderEditPanel.this.slippageField.setText(StopOrderEditPanel.this.storage.restoreDefaultSlippageAsText());
/*      */           }
/*      */ 
/*  490 */           StopOrderEditPanel.this.slippageField.setEnabled(StopOrderEditPanel.this.slippageCheck.isSelected());
/*      */         }
/*      */       });
/*  494 */       if ((OrderMessageUtils.isLimit(this.stopOrder)) && (!OrderMessageUtils.isMit(this.stopOrder))) {
/*  495 */         this.slippageCheck.setSelected(true);
/*  496 */         this.slippageCheck.setEnabled(false);
/*  497 */         this.slippageField.setText("0");
/*  498 */         this.slippageField.setEnabled(false);
/*      */       }
/*      */     }
/*      */ 
/*  502 */     if ((StopOrderType.OPEN_IF.equals(this.stopOrderType)) || (StopOrderType.STOP_LOSS.equals(this.stopOrderType)) || (StopOrderType.IFD_STOP.equals(this.stopOrderType)))
/*      */     {
/*  505 */       this.trailstepCheck = new JCheckBox();
/*  506 */       this.trailstepSpinner = new TrailingStepJSpinner(this.storage.restoreDefaultTrailingStep().doubleValue());
/*  507 */       this.trailstepSpinner.setHorizontalAlignment(4);
/*  508 */       JPanel trstoplossPanel = new JPanel();
/*  509 */       trstoplossPanel.setLayout(new BoxLayout(trstoplossPanel, 0));
/*  510 */       trstoplossPanel.add(this.trailstepCheck);
/*  511 */       trstoplossPanel.add(this.trailstepSpinner);
/*  512 */       Money trstoploss = this.stopOrder.getPriceLimit();
/*  513 */       if (null != trstoploss) {
/*  514 */         this.trailstepCheck.setSelected(true);
/*  515 */         this.trailstepSpinner.setEnabled(true);
/*  516 */         CurrencyOffer bestOffer = OrderSide.BUY.equals(this.stopOrder.getSide()) ? bestAsk : bestBid;
/*  517 */         BigDecimal trailstepValue = trstoploss.getValue().divide(PriceUtil.pipValue(bestOffer.getPrice().getValue()), RoundingMode.HALF_EVEN);
/*      */ 
/*  521 */         this.trailstepSpinner.setValue(trailstepValue);
/*      */       } else {
/*  523 */         this.trailstepCheck.setSelected(false);
/*  524 */         this.trailstepSpinner.setEnabled(false);
/*  525 */         this.trailstepSpinner.clear();
/*      */       }
/*      */ 
/*  528 */       if (!GreedContext.isContest()) {
/*  529 */         c.gridx = 0; c.gridy = (gridY++);
/*  530 */         c.fill = 0;
/*  531 */         orderForm.add(new JLocalizableLabel("label.trailing.step"), c);
/*  532 */         c.gridx = 1;
/*  533 */         c.fill = 2;
/*  534 */         orderForm.add(trstoplossPanel, c);
/*      */       }
/*      */ 
/*  538 */       this.trailstepCheck.addActionListener(new ActionListener() {
/*      */         public void actionPerformed(ActionEvent e) {
/*  540 */           if (!StopOrderEditPanel.this.trailstepCheck.isSelected())
/*      */           {
/*  542 */             StopOrderEditPanel.this.trailstepSpinner.clear();
/*      */           }
/*      */           else {
/*  545 */             StopOrderEditPanel.this.trailstepSpinner.setValue(StopOrderEditPanel.this.storage.restoreDefaultTrailingStep());
/*      */           }
/*  547 */           StopOrderEditPanel.this.trailstepSpinner.setEnabled(StopOrderEditPanel.this.trailstepCheck.isSelected());
/*      */         }
/*      */       });
/*  551 */       if ((GreedContext.isGlobal()) || (OrderMessageUtils.isLimit(this.stopOrder)) || (OrderMessageUtils.isMit(this.stopOrder)) || ((GreedContext.isGlobalExtended()) && (OrderMessageUtils.isLimit(this.stopOrder))))
/*      */       {
/*  555 */         this.trailstepCheck.setSelected(false);
/*  556 */         this.trailstepCheck.setEnabled(false);
/*  557 */         this.trailstepSpinner.clear();
/*  558 */         this.trailstepSpinner.setEnabled(false);
/*      */       }
/*      */     }
/*      */ 
/*  562 */     JLocalizableLabel priceLabel = new JLocalizableLabel("label.price");
/*  563 */     JPanel stopPanel = new JPanel();
/*  564 */     String buttonTitle = "DOTS";
/*  565 */     if ((StopOrderType.TAKE_PROFIT.equals(this.stopOrderType)) || (StopOrderType.IFD_LIMIT.equals(this.stopOrderType)))
/*      */     {
/*  567 */       buttonTitle = OrderSide.SELL.equals(this.stopOrder.getSide()) ? "L_BE" : "L_AE";
/*      */     } else {
/*  569 */       if (LOGGER.isDebugEnabled()) {
/*  570 */         LOGGER.debug("is null? " + this.stopOrder);
/*  571 */         LOGGER.debug("is null? " + this.stopOrder.getStopDirection());
/*      */       }
/*  573 */       switch (18.$SwitchMap$com$dukascopy$transport$common$model$type$StopDirection[this.stopOrder.getStopDirection().ordinal()]) {
/*      */       case 1:
/*  575 */         buttonTitle = "L_AG";
/*  576 */         break;
/*      */       case 2:
/*  578 */         if ((null != this.stopOrder.getPriceTrailingLimit()) && (this.stopOrder.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) >= 0) && (OrderSide.BUY.equals(this.stopOrder.getSide())))
/*      */         {
/*  582 */           if (this.stopOrder.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) == 0)
/*  583 */             buttonTitle = "L_AE";
/*      */           else
/*  585 */             buttonTitle = "L_AES";
/*      */         }
/*  587 */         else buttonTitle = "L_AL";
/*      */ 
/*  589 */         break;
/*      */       case 3:
/*  591 */         if ((null != this.stopOrder.getPriceTrailingLimit()) && (this.stopOrder.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) >= 0) && (OrderSide.SELL.equals(this.stopOrder.getSide())))
/*      */         {
/*  595 */           if (this.stopOrder.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) == 0)
/*  596 */             buttonTitle = "L_BE";
/*      */           else
/*  598 */             buttonTitle = "L_BES";
/*      */         }
/*  600 */         else buttonTitle = "L_BG";
/*      */ 
/*  602 */         break;
/*      */       case 4:
/*  604 */         buttonTitle = "L_BL";
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  609 */     this.stopSelectButton = new JLocalizableButton(buttonTitle);
/*  610 */     this.stopSelectButton.setName("ID_JBUTTON_PRICE");
/*  611 */     Insets insets = new Insets(0, 0, 0, 0);
/*  612 */     this.stopSelectButton.setMargin(insets);
/*  613 */     this.stopSelectButton.setPreferredSize(new Dimension(40, this.stopSelectButton.getSize().height));
/*  614 */     stopPanel.setLayout(new BoxLayout(stopPanel, 0));
/*      */     OrderSide orderSide;
/*      */     OrderSide orderSide;
/*  618 */     if (position != null)
/*  619 */       orderSide = PositionSide.LONG.equals(this.positionSide) ? OrderSide.BUY : OrderSide.SELL;
/*      */     else {
/*  621 */       orderSide = this.stopOrder.getSide();
/*      */     }
/*      */ 
/*  624 */     BigDecimal stopPrice = orderSide == OrderSide.SELL ? bestAskPrice : bestBidPrice;
/*  625 */     if (this.priceStop != null)
/*  626 */       stopPrice = this.priceStop;
/*  627 */     else if (this.stopOrder.getPriceStop() != null) {
/*  628 */       stopPrice = this.stopOrder.getPriceStop().getValue();
/*      */     }
/*      */ 
/*  632 */     BigDecimal pipShift = new BigDecimal(0);
/*      */ 
/*  635 */     if (this.stopOrder.getPriceStop() == null) {
/*  636 */       if (isOrderMessageFromOrdersTable() == null) {
/*  637 */         if (PositionSide.LONG == this.positionSide) {
/*  638 */           if ((StopOrderType.STOP_LOSS == this.stopOrderType) || (StopOrderType.IFD_STOP.equals(this.stopOrderType)))
/*      */           {
/*  640 */             pipShift = OrderUtils.NEGATIVE_ONE.multiply(this.storage.restoreDefaultStopLossOffset());
/*  641 */           } else if ((StopOrderType.TAKE_PROFIT == this.stopOrderType) || (StopOrderType.IFD_LIMIT.equals(this.stopOrderType)))
/*      */           {
/*  643 */             pipShift = this.storage.restoreDefaultTakeProfitOffset();
/*      */           }
/*  645 */         } else if (PositionSide.SHORT == this.positionSide) {
/*  646 */           if ((StopOrderType.STOP_LOSS == this.stopOrderType) || (StopOrderType.IFD_STOP.equals(this.stopOrderType)))
/*      */           {
/*  648 */             pipShift = this.storage.restoreDefaultStopLossOffset();
/*  649 */           } else if ((StopOrderType.TAKE_PROFIT == this.stopOrderType) || (StopOrderType.IFD_LIMIT.equals(this.stopOrderType)))
/*      */           {
/*  651 */             pipShift = OrderUtils.NEGATIVE_ONE.multiply(this.storage.restoreDefaultTakeProfitOffset());
/*      */           }
/*      */         }
/*      */       }
/*  655 */       else if ((StopOrderType.STOP_LOSS == this.stopOrderType) || (StopOrderType.IFD_STOP.equals(this.stopOrderType)))
/*      */       {
/*  657 */         if (OrderSide.SELL == orderSide)
/*  658 */           pipShift = OrderUtils.NEGATIVE_ONE.multiply(this.storage.restoreDefaultStopLossOffset());
/*  659 */         else if (OrderSide.BUY == orderSide)
/*  660 */           pipShift = this.storage.restoreDefaultStopLossOffset();
/*      */       }
/*  662 */       else if ((StopOrderType.TAKE_PROFIT == this.stopOrderType) || (StopOrderType.IFD_LIMIT.equals(this.stopOrderType)))
/*      */       {
/*  664 */         if (OrderSide.SELL == orderSide)
/*  665 */           pipShift = this.storage.restoreDefaultTakeProfitOffset();
/*  666 */         else if (OrderSide.BUY == orderSide) {
/*  667 */           pipShift = OrderUtils.NEGATIVE_ONE.multiply(this.storage.restoreDefaultTakeProfitOffset());
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  673 */     BigDecimal pipValue = BigDecimal.valueOf(Instrument.fromString(this.stopOrder.getInstrument()).getPipValue());
/*  674 */     stopPrice = stopPrice.add(pipValue.multiply(pipShift));
/*      */ 
/*  676 */     this.stopPriceField.setText(stopPrice.toPlainString());
/*  677 */     this.stopPriceField.addChangeListener(new ChangeListener() {
/*      */       public void stateChanged(ChangeEvent e) {
/*  679 */         StopOrderEditPanel.this.drawLine();
/*      */       }
/*      */     });
/*  683 */     if (!GreedContext.isContest()) {
/*  684 */       stopPanel.add(this.stopSelectButton);
/*  685 */       stopPanel.add(Box.createHorizontalStrut(5));
/*      */     }
/*  687 */     else if (OrderSide.BUY.equals(this.orderGroup.getOpeningOrder().getSide())) {
/*  688 */       this.stopPriceField.setMinimum(stopPrice);
/*  689 */     } else if (OrderSide.SELL.equals(this.orderGroup.getOpeningOrder().getSide())) {
/*  690 */       this.stopPriceField.setMaximum(stopPrice);
/*      */     }
/*      */ 
/*  694 */     stopPanel.add(this.stopPriceField);
/*      */ 
/*  696 */     c.gridx = 0; c.gridy = (gridY++);
/*  697 */     c.fill = 0;
/*  698 */     orderForm.add(priceLabel, c);
/*  699 */     c.gridx = 1;
/*  700 */     c.fill = 2;
/*  701 */     orderForm.add(stopPanel, c);
/*      */ 
/*  703 */     c.gridx = 0; c.gridy = (gridY++);
/*  704 */     c.gridwidth = 2;
/*  705 */     c.fill = 0;
/*      */ 
/*  707 */     this.timeLimPanel = new OrderTimeLimitationPanel(this.parent);
/*  708 */     orderForm.add(this.timeLimPanel, c);
/*      */ 
/*  710 */     innerPanel.add(orderForm);
/*  711 */     add(innerPanel);
/*      */ 
/*  713 */     JPanel submitPanel = new JPanel();
/*  714 */     this.submitButton.setText("button.submit");
/*      */ 
/*  716 */     if (this.newOrder) {
/*  717 */       submitPanel.setLayout(new BorderLayout());
/*  718 */       submitPanel.add(this.submitButton, "Center");
/*      */     } else {
/*  720 */       submitPanel.setLayout(new GridLayout(1, 2, 2, 0));
/*      */ 
/*  722 */       submitPanel.add(this.submitButton);
/*  723 */       submitPanel.add(this.cancelButton);
/*      */     }
/*  725 */     submitPanel.setBorder(BorderFactory.createEmptyBorder(2, 10, 7, 11));
/*  726 */     add(submitPanel);
/*      */ 
/*  729 */     if (StopOrderType.TAKE_PROFIT.equals(this.stopOrderType)) {
/*  730 */       this.stopSelectButton.setEnabled(false);
/*      */     }
/*      */     else
/*  733 */       this.stopSelectButton.addMouseListener(new MouseAdapter() {
/*      */         public void mousePressed(MouseEvent mouseEvent) {
/*  735 */           maybeShowPopup(mouseEvent);
/*      */         }
/*      */ 
/*      */         public void mouseReleased(MouseEvent mouseEvent) {
/*  739 */           maybeShowPopup(mouseEvent);
/*      */         }
/*      */ 
/*      */         private void maybeShowPopup(MouseEvent mouseEvent) {
/*  743 */           if ((!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended()))
/*  744 */             StopOrderEditPanel.this.popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
/*      */         }
/*      */       });
/*  748 */     this.submitButton.addActionListener(new ActionListener() {
/*      */       private OrderConfirmationDialog ocd;
/*      */ 
/*  752 */       public void actionPerformed(ActionEvent actionEvent) { if ((!StopOrderEditPanel.this.amountSpinner.validateEditor()) || ((null != StopOrderEditPanel.this.trailstepCheck) && (StopOrderEditPanel.this.trailstepCheck.isSelected()) && (!StopOrderEditPanel.this.trailstepSpinner.validateEditor())) || (!StopOrderEditPanel.this.timeLimPanel.isTimeValid()))
/*      */         {
/*  755 */           return;
/*      */         }
/*      */ 
/*  758 */         if ((GreedContext.isContest()) && ((!StopOrderEditPanel.this.stopPriceField.validatePrice()) || (!StopOrderEditPanel.this.isOrdersOffsetValid()))) {
/*  759 */           StopOrderEditPanel.this.errorPanel.setVisible(true);
/*  760 */           StopOrderEditPanel.this.parent.pack();
/*  761 */           return;
/*      */         }
/*      */ 
/*  764 */         ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*  765 */         if (StopOrderEditPanel.this.storage.restoreOrderValidationOn()) {
/*  766 */           ValidateOrder.OrderValidationBean result = StopOrderEditPanel.this.validateOrder();
/*      */ 
/*  768 */           if (!result.getMessages().isEmpty()) {
/*  769 */             boolean isSLorTP = (StopOrderEditPanel.this.stopOrderType.equals(StopOrderType.STOP_LOSS)) || (StopOrderEditPanel.this.stopOrderType.equals(StopOrderType.TAKE_PROFIT)) || (StopOrderType.IFD_STOP.equals(StopOrderEditPanel.this.stopOrderType)) || (StopOrderType.IFD_LIMIT.equals(StopOrderEditPanel.this.stopOrderType));
/*      */ 
/*  772 */             this.ocd = new OrderConfirmationDialog(StopOrderEditPanel.this.orderGroup, result, clientForm, isSLorTP, true);
/*      */ 
/*  774 */             for (ActionListener i : this.ocd.getBOK().getActionListeners()) {
/*  775 */               this.ocd.getBOK().removeActionListener(i);
/*      */             }
/*      */ 
/*  778 */             this.ocd.getBOK().addActionListener(new ActionListener() {
/*      */               public void actionPerformed(ActionEvent e) {
/*  780 */                 StopOrderEditPanel.5.this.ocd.dispose();
/*  781 */                 StopOrderEditPanel.this.submitButtonPressed();
/*      */               }
/*      */             });
/*  785 */             StopOrderEditPanel.this.addPreviewInfo(this.ocd);
/*  786 */             this.ocd.onMarketState(StopOrderEditPanel.this.marketView.getLastMarketState(StopOrderEditPanel.this.instrument));
/*  787 */             this.ocd.setLocationRelativeTo(StopOrderEditPanel.this.submitButton);
/*  788 */             this.ocd.setDeferedTradeLog(false);
/*  789 */             this.ocd.setVisible(true);
/*      */           } else {
/*  791 */             StopOrderEditPanel.this.submitButtonPressed();
/*      */           }
/*      */         } else {
/*  794 */           StopOrderEditPanel.this.submitButtonPressed();
/*      */         }
/*      */       }
/*      */     });
/*  799 */     this.cancelButton.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  801 */         StopOrderEditPanel.this.parent.dispose();
/*      */       }
/*      */     });
/*  812 */     ButtonGroup group = new ButtonGroup();
/*  813 */     this.popup = new JPopupMenu();
/*  814 */     this.popup.setName("ID_JPOPUP_POPUP");
/*  815 */     this.popup.setBorderPainted(true);
/*      */ 
/*  817 */     if (((!this.stopOrderType.equals(StopOrderType.STOP_LOSS)) && (!StopOrderType.IFD_STOP.equals(this.stopOrderType))) || ((this.positionSide.equals(PositionSide.LONG)) || ((StopOrderType.OPEN_IF.equals(this.stopOrderType)) && (this.positionSide.equals(PositionSide.SHORT)))))
/*      */     {
/*  821 */       LOGGER.debug("adding to menu: BID is less");
/*  822 */       JLocalizableMenuItem item = new JLocalizableMenuItem("SD_B_L");
/*  823 */       item.setAction(new AbstractAction("SD_B_L") {
/*      */         public void actionPerformed(ActionEvent actionEvent) {
/*  825 */           StopOrderEditPanel.this.stopOrder.setStopDirection(StopDirection.BID_LESS);
/*  826 */           StopOrderEditPanel.this.stopSelectButton.setText("L_BL");
/*  827 */           if (StopOrderType.OPEN_IF.equals(StopOrderEditPanel.this.stopOrderType))
/*      */           {
/*  829 */             StopOrderEditPanel.this.slippageCheck.setEnabled(true);
/*  830 */             StopOrderEditPanel.this.slippageCheck.setSelected(true);
/*  831 */             StopOrderEditPanel.this.slippageField.setEnabled(true);
/*  832 */             if (null != StopOrderEditPanel.this.slippageAmount)
/*  833 */               StopOrderEditPanel.this.slippageField.setText(StopOrderEditPanel.this.slippageAmount);
/*      */             else {
/*  835 */               StopOrderEditPanel.this.slippageField.setText(StopOrderEditPanel.this.storage.restoreDefaultSlippageAsText());
/*      */             }
/*      */           }
/*      */ 
/*  839 */           StopOrderEditPanel.this.trailstepCheck.setSelected(false);
/*  840 */           StopOrderEditPanel.this.trailstepCheck.setEnabled(true);
/*  841 */           StopOrderEditPanel.this.trailstepSpinner.clear();
/*  842 */           StopOrderEditPanel.this.trailstepSpinner.setEnabled(false);
/*      */         }
/*      */       });
/*  845 */       item.setName("SD_B_L");
/*  846 */       group.add(item);
/*  847 */       this.popup.add(item);
/*      */     }
/*      */ 
/*  850 */     if (!GreedContext.isGlobal())
/*      */     {
/*  852 */       if (((!this.stopOrderType.equals(StopOrderType.STOP_LOSS)) && (!StopOrderType.IFD_STOP.equals(this.stopOrderType))) || ((this.positionSide.equals(PositionSide.SHORT)) || ((StopOrderType.OPEN_IF.equals(this.stopOrderType)) && (this.positionSide.equals(PositionSide.LONG)))))
/*      */       {
/*  856 */         LOGGER.debug("adding to menu: BID is greater");
/*      */ 
/*  858 */         JLocalizableMenuItem item = new JLocalizableMenuItem("SD_B_G");
/*  859 */         item.setAction(new AbstractAction("SD_B_G") {
/*      */           public void actionPerformed(ActionEvent actionEvent) {
/*  861 */             StopOrderEditPanel.this.stopOrder.setStopDirection(StopDirection.BID_GREATER);
/*  862 */             StopOrderEditPanel.this.stopSelectButton.setText("L_BG");
/*  863 */             if (StopOrderType.OPEN_IF.equals(StopOrderEditPanel.this.stopOrderType)) {
/*  864 */               StopOrderEditPanel.this.slippageCheck.setSelected(false);
/*  865 */               StopOrderEditPanel.this.slippageCheck.setEnabled(true);
/*  866 */               StopOrderEditPanel.this.slippageField.setEnabled(false);
/*  867 */               StopOrderEditPanel.this.slippageField.clear();
/*      */             }
/*      */ 
/*  871 */             StopOrderEditPanel.this.trailstepCheck.setSelected(false);
/*  872 */             StopOrderEditPanel.this.trailstepCheck.setEnabled(true);
/*  873 */             StopOrderEditPanel.this.trailstepSpinner.clear();
/*  874 */             StopOrderEditPanel.this.trailstepSpinner.setEnabled(false);
/*      */           }
/*      */         });
/*  877 */         item.setActionCommand("SD_B_G");
/*  878 */         item.setName("SD_B_G");
/*  879 */         group.add(item);
/*  880 */         this.popup.add(item);
/*      */       }
/*      */ 
/*  884 */       if (((!this.stopOrderType.equals(StopOrderType.STOP_LOSS)) && (!StopOrderType.IFD_STOP.equals(this.stopOrderType))) || ((this.positionSide.equals(PositionSide.LONG)) || ((StopOrderType.OPEN_IF.equals(this.stopOrderType)) && (this.positionSide.equals(PositionSide.SHORT)))))
/*      */       {
/*  888 */         LOGGER.debug("adding to menu: ASK is less");
/*  889 */         JLocalizableMenuItem item = new JLocalizableMenuItem("SD_A_L");
/*  890 */         item.setAction(new AbstractAction("SD_A_L") {
/*      */           public void actionPerformed(ActionEvent actionEvent) {
/*  892 */             StopOrderEditPanel.this.stopOrder.setStopDirection(StopDirection.ASK_LESS);
/*  893 */             StopOrderEditPanel.this.stopSelectButton.setText("L_AL");
/*  894 */             if (StopOrderType.OPEN_IF.equals(StopOrderEditPanel.this.stopOrderType)) {
/*  895 */               StopOrderEditPanel.this.slippageCheck.setSelected(false);
/*  896 */               StopOrderEditPanel.this.slippageCheck.setEnabled(true);
/*  897 */               StopOrderEditPanel.this.slippageField.setEnabled(false);
/*  898 */               StopOrderEditPanel.this.slippageField.clear();
/*      */ 
/*  901 */               StopOrderEditPanel.this.trailstepCheck.setSelected(false);
/*  902 */               StopOrderEditPanel.this.trailstepCheck.setEnabled(true);
/*  903 */               StopOrderEditPanel.this.trailstepSpinner.clear();
/*  904 */               StopOrderEditPanel.this.trailstepSpinner.setEnabled(false);
/*      */             }
/*      */           }
/*      */         });
/*  908 */         item.setName("SD_A_L");
/*  909 */         group.add(item);
/*  910 */         this.popup.add(item);
/*      */       }
/*      */     }
/*      */ 
/*  914 */     if (((!this.stopOrderType.equals(StopOrderType.STOP_LOSS)) && (!StopOrderType.IFD_STOP.equals(this.stopOrderType))) || ((this.positionSide.equals(PositionSide.SHORT)) || ((StopOrderType.OPEN_IF.equals(this.stopOrderType)) && (this.positionSide.equals(PositionSide.LONG)))))
/*      */     {
/*  918 */       LOGGER.debug("adding to menu: ASK is greater");
/*  919 */       JLocalizableMenuItem item = new JLocalizableMenuItem("SD_A_G");
/*  920 */       item.setAction(new AbstractAction("SD_A_G") {
/*      */         public void actionPerformed(ActionEvent actionEvent) {
/*  922 */           StopOrderEditPanel.this.stopOrder.setStopDirection(StopDirection.ASK_GREATER);
/*  923 */           StopOrderEditPanel.this.stopSelectButton.setText("L_AG");
/*  924 */           if (StopOrderType.OPEN_IF.equals(StopOrderEditPanel.this.stopOrderType)) {
/*  925 */             StopOrderEditPanel.this.slippageCheck.setEnabled(true);
/*  926 */             StopOrderEditPanel.this.slippageCheck.setSelected(true);
/*  927 */             StopOrderEditPanel.this.slippageField.setEnabled(true);
/*  928 */             if (null != StopOrderEditPanel.this.slippageAmount)
/*  929 */               StopOrderEditPanel.this.slippageField.setText(StopOrderEditPanel.this.slippageAmount);
/*      */             else {
/*  931 */               StopOrderEditPanel.this.slippageField.setText(StopOrderEditPanel.this.storage.restoreDefaultSlippageAsText());
/*      */             }
/*      */ 
/*  935 */             StopOrderEditPanel.this.trailstepCheck.setSelected(false);
/*  936 */             StopOrderEditPanel.this.trailstepCheck.setEnabled(true);
/*  937 */             StopOrderEditPanel.this.trailstepSpinner.clear();
/*  938 */             StopOrderEditPanel.this.trailstepSpinner.setEnabled(false);
/*      */           }
/*      */         }
/*      */       });
/*  942 */       item.setName("SD_A_G");
/*  943 */       group.add(item);
/*  944 */       this.popup.add(item);
/*      */     }
/*      */ 
/*  947 */     if (((StopOrderType.TAKE_PROFIT.equals(this.stopOrderType)) && (PositionSide.SHORT.equals(this.positionSide))) || ((StopOrderType.OPEN_IF.equals(this.stopOrderType)) && (PositionSide.LONG.equals(this.positionSide))))
/*      */     {
/*  951 */       LOGGER.debug("adding to menu: ASK equals");
/*  952 */       JLocalizableMenuItem item = new JLocalizableMenuItem("SD_A_E");
/*  953 */       item.setAction(new AbstractAction("SD_A_E") {
/*      */         public void actionPerformed(ActionEvent e) {
/*  955 */           StopOrderEditPanel.this.stopOrder.setStopDirection(StopDirection.ASK_LESS);
/*  956 */           StopOrderEditPanel.this.stopSelectButton.setText("L_AE");
/*  957 */           if (!StopOrderEditPanel.this.slippageCheck.isSelected()) {
/*  958 */             StopOrderEditPanel.this.slippageCheck.doClick();
/*      */           }
/*  960 */           StopOrderEditPanel.this.slippageCheck.setSelected(true);
/*  961 */           StopOrderEditPanel.this.slippageCheck.setEnabled(false);
/*  962 */           StopOrderEditPanel.this.slippageField.setText("0");
/*  963 */           StopOrderEditPanel.this.slippageField.setEnabled(false);
/*      */ 
/*  966 */           StopOrderEditPanel.this.trailstepCheck.setSelected(false);
/*  967 */           StopOrderEditPanel.this.trailstepCheck.setEnabled(false);
/*  968 */           StopOrderEditPanel.this.trailstepSpinner.clear();
/*  969 */           StopOrderEditPanel.this.trailstepSpinner.setEnabled(false);
/*      */         }
/*      */       });
/*  972 */       item.setName("SD_A_E");
/*  973 */       group.add(item);
/*  974 */       this.popup.add(item);
/*      */     }
/*      */ 
/*  977 */     if ((GreedContext.isStrategyAllowed()) && 
/*  978 */       (StopOrderType.OPEN_IF.equals(this.stopOrderType)) && (PositionSide.LONG.equals(this.positionSide))) {
/*  979 */       LOGGER.debug("adding to menu: MIT ask equals");
/*  980 */       JLocalizableMenuItem item = new JLocalizableMenuItem("SD_A_E_MIT");
/*  981 */       item.setAction(new AbstractAction("SD_A_E_MIT") {
/*      */         public void actionPerformed(ActionEvent e) {
/*  983 */           StopOrderEditPanel.this.stopOrder.setStopDirection(StopDirection.ASK_LESS);
/*  984 */           StopOrderEditPanel.this.stopSelectButton.setText("L_AES");
/*      */ 
/*  986 */           StopOrderEditPanel.this.slippageCheck.setEnabled(true);
/*  987 */           StopOrderEditPanel.this.slippageCheck.setSelected(true);
/*  988 */           StopOrderEditPanel.this.slippageField.setEnabled(true);
/*  989 */           if (null != StopOrderEditPanel.this.slippageAmount)
/*  990 */             StopOrderEditPanel.this.slippageField.setText(StopOrderEditPanel.this.slippageAmount);
/*      */           else {
/*  992 */             StopOrderEditPanel.this.slippageField.setText(StopOrderEditPanel.this.storage.restoreDefaultSlippageAsText());
/*      */           }
/*      */ 
/*  996 */           StopOrderEditPanel.this.trailstepCheck.setSelected(false);
/*  997 */           StopOrderEditPanel.this.trailstepCheck.setEnabled(false);
/*  998 */           StopOrderEditPanel.this.trailstepSpinner.clear();
/*  999 */           StopOrderEditPanel.this.trailstepSpinner.setEnabled(false);
/*      */         }
/*      */       });
/* 1002 */       item.setName("SD_A_E_MIT");
/* 1003 */       group.add(item);
/* 1004 */       this.popup.add(item);
/*      */     }
/*      */ 
/* 1008 */     if (((StopOrderType.TAKE_PROFIT.equals(this.stopOrderType)) && (PositionSide.LONG.equals(this.positionSide))) || ((StopOrderType.OPEN_IF.equals(this.stopOrderType)) && (PositionSide.SHORT.equals(this.positionSide))))
/*      */     {
/* 1012 */       LOGGER.debug("adding to menu: BID equals");
/* 1013 */       JLocalizableMenuItem item = new JLocalizableMenuItem("SD_B_E");
/* 1014 */       item.setAction(new AbstractAction("SD_B_E") {
/*      */         public void actionPerformed(ActionEvent e) {
/* 1016 */           StopOrderEditPanel.this.stopOrder.setStopDirection(StopDirection.BID_GREATER);
/* 1017 */           StopOrderEditPanel.this.stopSelectButton.setText("L_BE");
/*      */ 
/* 1019 */           StopOrderEditPanel.this.slippageCheck.setSelected(true);
/* 1020 */           StopOrderEditPanel.this.slippageCheck.setEnabled(false);
/* 1021 */           StopOrderEditPanel.this.slippageField.setText("0");
/* 1022 */           StopOrderEditPanel.this.slippageField.setEnabled(false);
/*      */ 
/* 1025 */           StopOrderEditPanel.this.trailstepCheck.setSelected(false);
/* 1026 */           StopOrderEditPanel.this.trailstepCheck.setEnabled(false);
/* 1027 */           StopOrderEditPanel.this.trailstepSpinner.clear();
/* 1028 */           StopOrderEditPanel.this.trailstepSpinner.setEnabled(false);
/*      */         }
/*      */       });
/* 1031 */       item.setName("SD_B_E");
/* 1032 */       group.add(item);
/* 1033 */       this.popup.add(item);
/*      */     }
/*      */ 
/* 1036 */     if ((GreedContext.isStrategyAllowed()) && 
/* 1037 */       (StopOrderType.OPEN_IF.equals(this.stopOrderType)) && (PositionSide.SHORT.equals(this.positionSide))) {
/* 1038 */       LOGGER.debug("adding to menu: MIT bid equals");
/* 1039 */       JLocalizableMenuItem item = new JLocalizableMenuItem("SD_B_E_MIT");
/* 1040 */       item.setAction(new AbstractAction("SD_B_E_MIT") {
/*      */         public void actionPerformed(ActionEvent e) {
/* 1042 */           StopOrderEditPanel.this.stopOrder.setStopDirection(StopDirection.BID_GREATER);
/* 1043 */           StopOrderEditPanel.this.stopSelectButton.setText("L_BES");
/*      */ 
/* 1045 */           StopOrderEditPanel.this.slippageCheck.setEnabled(true);
/* 1046 */           StopOrderEditPanel.this.slippageCheck.setSelected(true);
/* 1047 */           StopOrderEditPanel.this.slippageField.setEnabled(true);
/* 1048 */           if (null != StopOrderEditPanel.this.slippageAmount)
/* 1049 */             StopOrderEditPanel.this.slippageField.setText(StopOrderEditPanel.this.slippageAmount);
/*      */           else {
/* 1051 */             StopOrderEditPanel.this.slippageField.setText(StopOrderEditPanel.this.storage.restoreDefaultSlippageAsText());
/*      */           }
/*      */ 
/* 1055 */           StopOrderEditPanel.this.trailstepCheck.setSelected(false);
/* 1056 */           StopOrderEditPanel.this.trailstepCheck.setEnabled(false);
/* 1057 */           StopOrderEditPanel.this.trailstepSpinner.clear();
/* 1058 */           StopOrderEditPanel.this.trailstepSpinner.setEnabled(false);
/*      */         }
/*      */       });
/* 1062 */       item.setText("SD_B_E_MIT");
/* 1063 */       item.setName("SD_B_E_MIT");
/* 1064 */       group.add(item);
/* 1065 */       this.popup.add(item);
/*      */     }
/*      */ 
/* 1069 */     if (GreedContext.isContest())
/*      */     {
/* 1071 */       if (this.trailstepCheck != null) this.trailstepCheck.setVisible(false);
/* 1072 */       if (this.trailstepSpinner != null) this.trailstepSpinner.setVisible(false);
/*      */ 
/* 1074 */       this.timeLimPanel.setVisible(false);
/*      */ 
/* 1076 */       initErrorPanel();
/*      */ 
/* 1078 */       c.gridx = 0; c.gridy = (gridY++);
/* 1079 */       c.gridwidth = 2;
/* 1080 */       c.fill = 0;
/*      */ 
/* 1082 */       orderForm.add(this.errorPanel, c);
/*      */     }
/*      */ 
/* 1088 */     Enumeration items = group.getElements();
/*      */ 
/* 1091 */     while (items.hasMoreElements()) {
/* 1092 */       JLocalizableMenuItem item = (JLocalizableMenuItem)items.nextElement();
/* 1093 */       String command = item.getActionCommand();
/* 1094 */       if (("SD_MK".equalsIgnoreCase(command)) || ("SD_B_E".equalsIgnoreCase(command)) || ("SD_A_E".equalsIgnoreCase(command)) || ("SD_B_G".equalsIgnoreCase(command)) || ("SD_A_G".equalsIgnoreCase(command)) || ("SD_B_L".equalsIgnoreCase(command)) || ("SD_A_L".equalsIgnoreCase(command)))
/*      */       {
/* 1109 */         item.setVisible(true);
/*      */       }
/*      */ 
/* 1113 */       LOGGER.debug("" + item.getActionCommand() + " vis/" + item.isVisible());
/*      */     }
/* 1115 */     LOGGER.debug("positionSide=" + this.positionSide);
/*      */   }
/*      */ 
/*      */   private boolean isOrdersOffsetValid() {
/* 1119 */     if (!GreedContext.isContest()) return true;
/*      */ 
/* 1121 */     Money entryPrice = null;
/*      */ 
/* 1123 */     if (this.orderGroup.getOpeningOrder().getStopDirection() == null) {
/* 1124 */       double marketPrice = ValidateOrder.getCurrentPriceByOfferSide(PositionSide.LONG == this.orderGroup.getSide() ? OfferSide.BID : OfferSide.ASK, this.instrument);
/*      */ 
/* 1126 */       Currency currency = this.orderGroup.getOpeningOrder().getPriceClient().getCurrency();
/* 1127 */       entryPrice = new Money(BigDecimal.valueOf(marketPrice), currency);
/*      */     } else {
/* 1129 */       entryPrice = this.orderGroup.getOpeningOrder().getPriceStop();
/*      */     }
/*      */ 
/* 1132 */     Money newPrice = new Money(new BigDecimal(this.stopPriceField.getText()), entryPrice.getCurrency());
/* 1133 */     boolean isJpyInstrument = (Currency.getInstance("JPY").equals(Instrument.fromString(this.orderGroup.getInstrument()).getSecondaryCurrency())) || (Currency.getInstance("HUF").equals(Instrument.fromString(this.orderGroup.getInstrument()).getSecondaryCurrency()));
/*      */ 
/* 1135 */     BigDecimal multiplayer = isJpyInstrument ? GuiUtilsAndConstants.ONE_HUDRED : GuiUtilsAndConstants.TEN_THUSANDS;
/*      */ 
/* 1137 */     return entryPrice.getValue().subtract(newPrice.getValue()).abs().multiply(multiplayer).compareTo(GuiUtilsAndConstants.TEN) >= 0;
/*      */   }
/*      */ 
/*      */   private void initErrorLabel() {
/* 1141 */     String key = null;
/* 1142 */     if (!this.stopPriceField.validatePrice()) {
/* 1143 */       if (StopOrderType.STOP_LOSS.equals(this.stopOrderType))
/* 1144 */         key = "validation.contest.sl.one.direction";
/* 1145 */       else if (StopOrderType.TAKE_PROFIT.equals(this.stopOrderType))
/* 1146 */         key = "validation.contest.tp.one.direction";
/*      */     }
/* 1148 */     else if (!isOrdersOffsetValid()) {
/* 1149 */       key = "validation.contest.sl.tp.min.offset";
/*      */     }
/*      */ 
/* 1152 */     if (key != null)
/* 1153 */       this.errorLabel.setText(key);
/*      */   }
/*      */ 
/*      */   private void initErrorPanel() {
/* 1157 */     this.errorPanel = new JPanel(new FlowLayout(1))
/*      */     {
/*      */       public void setVisible(boolean isVisible) {
/* 1160 */         if (isVisible) StopOrderEditPanel.this.initErrorLabel();
/* 1161 */         super.setVisible(isVisible);
/*      */       }
/*      */     };
/* 1164 */     this.errorLabel = new JLocalizableLabel();
/*      */ 
/* 1166 */     this.errorLabel.setForeground(Color.red);
/* 1167 */     this.errorPanel.add(this.errorLabel);
/*      */ 
/* 1169 */     this.errorPanel.setVisible(false);
/* 1170 */     this.errorPanel.addMouseListener(new MouseAdapter() {
/*      */       public void mouseReleased(MouseEvent e) {
/* 1172 */         StopOrderEditPanel.this.refreshPriceField();
/* 1173 */         StopOrderEditPanel.this.errorPanel.setVisible(false);
/* 1174 */         StopOrderEditPanel.this.parent.pack();
/*      */       }
/*      */ 
/*      */       public void mouseEntered(MouseEvent e) {
/* 1178 */         StopOrderEditPanel.this.setCursor(Cursor.getPredefinedCursor(12));
/*      */       }
/*      */ 
/*      */       public void mouseExited(MouseEvent e) {
/* 1182 */         StopOrderEditPanel.this.setCursor(Cursor.getDefaultCursor());
/*      */       } } );
/*      */   }
/*      */ 
/*      */   private void refreshPriceField() {
/* 1188 */     if (OrderSide.BUY.equals(this.orderGroup.getOpeningOrder().getSide()))
/* 1189 */       this.stopPriceField.refreshToMin();
/* 1190 */     else if (OrderSide.SELL.equals(this.orderGroup.getOpeningOrder().getSide()))
/* 1191 */       this.stopPriceField.refreshToMax();
/*      */   }
/*      */ 
/*      */   private Position getPosition()
/*      */   {
/* 1196 */     Position position = this.orderGroup.calculatePositionModified();
/* 1197 */     if (null != position) {
/* 1198 */       this.positionSide = position.getPositionSide();
/* 1199 */       if (LOGGER.isDebugEnabled())
/* 1200 */         LOGGER.debug("position calculated side = " + this.positionSide);
/*      */     }
/*      */     else {
/* 1203 */       Money buyAmount = new Money("0", this.orderGroup.getCurrencyPrimary());
/* 1204 */       Money sellAmount = new Money("0", this.orderGroup.getCurrencyPrimary());
/* 1205 */       LOGGER.debug("openingOrderId = " + this.openingOrderId);
/* 1206 */       for (OrderMessage order : this.orderGroup.getOrders()) {
/* 1207 */         if ((this.openingOrderId == null) || ((this.openingOrderId != null) && ((order.getOrderId().equals(this.openingOrderId)) || ((order.getIfdParentOrderId() != null) && (order.getIfdParentOrderId().equals(this.openingOrderId))))))
/*      */         {
/* 1212 */           if (OrderDirection.OPEN == order.getOrderDirection()) {
/* 1213 */             if (order.getSide() == OrderSide.BUY)
/* 1214 */               buyAmount = buyAmount.add(order.getAmount().abs());
/* 1215 */             else if (order.getSide() == OrderSide.SELL) {
/* 1216 */               sellAmount = sellAmount.add(order.getAmount().abs());
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1221 */       if (buyAmount.compareTo(sellAmount) > 0) {
/* 1222 */         this.positionSide = PositionSide.LONG;
/* 1223 */         this.amount = buyAmount.getValue();
/*      */       } else {
/* 1225 */         this.positionSide = PositionSide.SHORT;
/* 1226 */         this.amount = sellAmount.getValue();
/*      */       }
/* 1228 */       if (LOGGER.isDebugEnabled()) {
/* 1229 */         LOGGER.debug("side on site = " + this.positionSide + " buyAmount=" + buyAmount + " sellAmount=" + sellAmount);
/*      */       }
/*      */     }
/*      */ 
/* 1233 */     return position;
/*      */   }
/*      */ 
/*      */   private JLocalizableRoundedBorder getBorder(JComponent parent) {
/* 1237 */     JLocalizableRoundedBorder myBorder = new JLocalizableRoundedBorder(parent, "header.order.entry");
/*      */ 
/* 1239 */     myBorder.setLeftInset(17);
/* 1240 */     myBorder.setRightInset(17);
/*      */ 
/* 1242 */     myBorder.setLeftBorder(10);
/* 1243 */     myBorder.setRightBorder(11);
/*      */ 
/* 1245 */     return myBorder;
/*      */   }
/*      */ 
/*      */   private ValidateOrder.OrderValidationBean validateOrder() {
/* 1249 */     String stopString = this.stopPriceField.getText();
/*      */ 
/* 1251 */     double stopValue = 0.0D;
/*      */     try
/*      */     {
/* 1254 */       stopValue = Double.parseDouble(stopString);
/*      */     } catch (NumberFormatException e) {
/*      */     }
/* 1257 */     StopDirection sd = this.stopOrder.getStopDirection();
/*      */ 
/* 1259 */     double slippage = 0.0D;
/* 1260 */     double entryVal = 0.0D;
/*      */ 
/* 1262 */     OrderMessage entryOrder = isOrderForSomeEntry();
/* 1263 */     OrderMessage bidOfferOrder = isOrderForSomeBidOffer();
/*      */ 
/* 1265 */     if (StopOrderType.OPEN_IF.name().equals(this.stopOrderType.name())) {
/*      */       try {
/* 1267 */         slippage = Double.parseDouble(this.slippageField.getText());
/*      */       } catch (NumberFormatException e) {
/* 1269 */         slippage = 0.0D;
/*      */       }
/* 1271 */       if ("BUY".equals(this.stopOrder.getSide().asString()))
/* 1272 */         entryVal = stopValue + slippage * PriceUtil.pipValue(new BigDecimal(stopValue)).doubleValue();
/* 1273 */       else if ("SELL".equals(this.stopOrder.getSide().asString())) {
/* 1274 */         entryVal = stopValue - slippage * PriceUtil.pipValue(new BigDecimal(stopValue)).doubleValue();
/*      */       }
/*      */ 
/* 1277 */       entryVal = ValidateOrder.round(entryVal, 5);
/*      */     }
/* 1279 */     else if (entryOrder != null) {
/* 1280 */       double slipp = 0.0D;
/*      */       try {
/* 1282 */         Money entrySlippage = entryOrder.getPriceTrailingLimit();
/* 1283 */         BigDecimal slippageValue = null;
/* 1284 */         if (null != entrySlippage) {
/* 1285 */           slippageValue = entrySlippage.getValue();
/* 1286 */           slipp += slippageValue.doubleValue() * PriceUtil.pipValue(new BigDecimal(stopValue)).doubleValue();
/*      */         }
/*      */       } catch (NumberFormatException e) {
/* 1289 */         LOGGER.warn(e.getMessage());
/* 1290 */         slipp = 0.0D;
/*      */       }
/*      */ 
/* 1293 */       double entryV = entryOrder.getPriceStop().getValue().doubleValue();
/* 1294 */       if ("BUY".equals(this.stopOrder.getSide().asString()))
/* 1295 */         entryVal = entryV + slipp;
/* 1296 */       else if ("SELL".equals(this.stopOrder.getSide().asString())) {
/* 1297 */         entryVal = entryV - slipp;
/*      */       }
/* 1299 */       entryVal = ValidateOrder.round(entryVal, 5);
/*      */     }
/*      */ 
/* 1303 */     if (entryOrder != null) {
/* 1304 */       return ValidateOrder.validateOrderAddEditFromTable(this.stopOrder.getSide().asString(), this.stopOrder.getInstrument(), stopValue, entryVal, slippage, this.stopOrderType.name(), sd.name(), this.orderGroup);
/*      */     }
/*      */ 
/* 1312 */     if (bidOfferOrder != null)
/*      */     {
/* 1314 */       return ValidateOrder.validateBidOfferFromOrdersTable(this.stopOrderType, this.stopOrder.getSide().asString(), this.stopOrder.getInstrument(), stopValue, bidOfferOrder.getPriceClient().getValue().doubleValue());
/*      */     }
/*      */ 
/* 1321 */     return ValidateOrder.validateOrderFromPosTable(this.positionSide.name(), this.stopOrder.getInstrument(), stopValue, this.stopOrderType.name(), sd.name());
/*      */   }
/*      */ 
/*      */   private OrderMessage isOrderForSomeEntry()
/*      */   {
/*      */     OrderMessage om;
/* 1334 */     if (null != this.orderGroup) {
/* 1335 */       OrderMessage openingOrder = this.orderGroup.getOpeningOrder();
/* 1336 */       if ((null != openingOrder) && (!OrderState.FILLED.equals(openingOrder.getOrderState()))) {
/* 1337 */         om = null;
/* 1338 */         for (OrderMessage i : this.orderGroup.getOrders()) {
/* 1339 */           if ((null != i.getPriceStop()) && (i.getOrderDirection() == OrderDirection.OPEN)) {
/* 1340 */             om = i;
/* 1341 */             return om;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1347 */     return null;
/*      */   }
/*      */ 
/*      */   private OrderMessage isOrderMessageFromOrdersTable()
/*      */   {
/*      */     OrderMessage om;
/* 1355 */     if (null != this.orderGroup) {
/* 1356 */       OrderMessage openingOrder = this.orderGroup.getOpeningOrder();
/* 1357 */       if ((null != openingOrder) && (!OrderState.FILLED.equals(openingOrder.getOrderState()))) {
/* 1358 */         om = null;
/* 1359 */         for (OrderMessage i : this.orderGroup.getOrders()) {
/* 1360 */           if (((null != i.getPriceStop()) || (null != i.getPriceClient())) && (i.getOrderDirection() == OrderDirection.OPEN)) {
/* 1361 */             om = i;
/* 1362 */             return om;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1368 */     return null;
/*      */   }
/*      */ 
/*      */   private OrderMessage isOrderForSomeBidOffer()
/*      */   {
/*      */     OrderMessage om;
/* 1372 */     if (null != this.orderGroup) {
/* 1373 */       OrderMessage openingOrder = this.orderGroup.getOpeningOrder();
/* 1374 */       if ((null != openingOrder) && (!OrderState.FILLED.equals(openingOrder.getOrderState()))) {
/* 1375 */         om = null;
/* 1376 */         for (OrderMessage i : this.orderGroup.getOrders()) {
/* 1377 */           if ((null != i.getPriceClient()) && (i.getOrderDirection() == OrderDirection.OPEN)) {
/* 1378 */             om = i;
/* 1379 */             return om;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1385 */     return null;
/*      */   }
/*      */ 
/*      */   private OrderMessage createNewStopOrder() {
/* 1389 */     OrderMessage order = new OrderMessage();
/*      */ 
/* 1391 */     order.setInstrument(this.orderGroup.getInstrument());
/* 1392 */     if (GreedContext.isGlobalExtended()) {
/* 1393 */       order.setOrderDirection(OrderDirection.OPEN);
/* 1394 */       order.setIfdType("ifds");
/* 1395 */       order.setIfdParentOrderId(this.openingOrderId);
/*      */     } else {
/* 1397 */       order.setOrderDirection(OrderDirection.CLOSE);
/* 1399 */     }order.setOrderGroupId(this.orderGroup.getOrderGroupId());
/*      */     Money newAmount;
/*      */     try { Position pm = this.orderGroup.calculatePositionModified();
/*      */       Money newAmount;
/* 1403 */       if (pm != null)
/* 1404 */         newAmount = pm.getAmount();
/*      */       else
/* 1406 */         newAmount = new Money("0", this.orderGroup.getCurrencyPrimary());
/*      */     } catch (Exception e)
/*      */     {
/* 1409 */       LOGGER.error(e.getMessage(), e);
/* 1410 */       newAmount = new Money("0", this.orderGroup.getCurrencyPrimary());
/*      */     }
/*      */ 
/* 1414 */     if (newAmount.getValue().compareTo(BigDecimal.ZERO) <= 0)
/*      */     {
/* 1416 */       if (this.openingOrderId != null) {
/* 1417 */         newAmount = new Money(this.amount.toPlainString(), this.orderGroup.getCurrencyPrimary());
/*      */       } else {
/* 1419 */         OrderMessage opening = this.orderGroup.getOpeningOrder();
/* 1420 */         if (opening != null) {
/* 1421 */           newAmount = opening.getAmount();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1426 */     order.setAmount(newAmount);
/* 1427 */     order.setOrderState(OrderState.CREATED);
/* 1428 */     this.newOrder = true;
/*      */ 
/* 1430 */     return order;
/*      */   }
/*      */ 
/*      */   public BigDecimal getAmount() {
/* 1434 */     return this.amountSpinner.getAmountValueInMillions(Instrument.fromString(this.instrument));
/*      */   }
/*      */ 
/*      */   public String getSlippageAmount() {
/* 1438 */     return null;
/*      */   }
/*      */ 
/*      */   public void setStopPrice(String price, StopDirection direction)
/*      */   {
/* 1454 */     this.stopPriceField.setText(price);
/* 1455 */     this.stopOrder.setPriceStop(price);
/*      */ 
/* 1457 */     if (direction != null)
/* 1458 */       this.stopOrder.setStopDirection(direction);
/*      */   }
/*      */ 
/*      */   public void submit()
/*      */   {
/* 1464 */     submitButtonPressed();
/*      */   }
/*      */ 
/*      */   void submitButtonPressed() {
/* 1468 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/*      */ 
/* 1470 */     this.price = new BigDecimal(this.stopPriceField.getText());
/* 1471 */     String[] currencies = this.instrument.split("/");
/*      */ 
/* 1474 */     String currencyPrimary = this.stopOrder.getCurrencyPrimary();
/* 1475 */     this.stopOrder.setAmount(new Money(this.amount.toPlainString(), currencyPrimary));
/* 1476 */     this.stopOrder.setPriceStop(new Money(this.price.toPlainString(), currencyPrimary));
/*      */ 
/* 1479 */     if (this.timeLimPanel != null) {
/* 1480 */       Long execTimeout = this.timeLimPanel.getTimeValue();
/* 1481 */       if (execTimeout != null) {
/* 1482 */         this.stopOrder.setExecTimeoutMillis(execTimeout);
/*      */       }
/*      */     }
/*      */ 
/* 1486 */     BigDecimal bestOfferValue = null;
/* 1487 */     if ((this.stopOrder.getStopDirection().equals(StopDirection.ASK_EQUALS)) || (this.stopOrder.getStopDirection().equals(StopDirection.ASK_GREATER)) || (this.stopOrder.getStopDirection().equals(StopDirection.ASK_LESS)))
/*      */     {
/* 1491 */       CurrencyOffer bestOffer = this.marketView.getBestOffer(this.stopOrder.getInstrument(), OfferSide.ASK);
/* 1492 */       bestOfferValue = bestOffer.getPrice().getValue();
/*      */     } else {
/* 1494 */       CurrencyOffer bestOffer = this.marketView.getBestOffer(this.stopOrder.getInstrument(), OfferSide.BID);
/* 1495 */       bestOfferValue = bestOffer.getPrice().getValue();
/*      */     }
/*      */ 
/* 1498 */     this.stopOrder.setPriceClient(new Money(bestOfferValue, Currency.getInstance(currencies[1])));
/*      */ 
/* 1500 */     if (StopOrderType.OPEN_IF == this.stopOrderType) {
/* 1501 */       if (this.orderId != null)
/* 1502 */         this.stopOrder.setSide(this.stopOrder.getSide());
/*      */       else
/* 1504 */         this.stopOrder.setSide(this.positionSide.equals(PositionSide.SHORT) ? OrderSide.SELL : OrderSide.BUY);
/*      */     }
/*      */     else {
/* 1507 */       if (this.orderId != null)
/* 1508 */         this.stopOrder.setSide(this.stopOrder.getSide());
/*      */       else {
/* 1510 */         this.stopOrder.setSide(this.positionSide.equals(PositionSide.LONG) ? OrderSide.SELL : OrderSide.BUY);
/*      */       }
/* 1512 */       if (this.openingOrderId != null) {
/* 1513 */         this.stopOrder.setIfdParentOrderId(this.openingOrderId);
/*      */       }
/*      */     }
/*      */ 
/* 1517 */     if ((null != this.slippageCheck) && (this.slippageCheck.isSelected()))
/*      */     {
/* 1519 */       BigDecimal maxSlippage = null;
/*      */       try {
/* 1521 */         maxSlippage = new BigDecimal(this.slippageField.getText());
/*      */       } catch (Exception e) {
/* 1523 */         this.slippageField.showMessage("no value entered");
/* 1524 */         return;
/*      */       }
/* 1526 */       BigDecimal trailingLimit = maxSlippage.multiply(PriceUtil.pipValue(bestOfferValue));
/* 1527 */       this.stopOrder.setPriceTrailingLimit(new Money(trailingLimit, Currency.getInstance(currencies[1])));
/*      */     } else {
/* 1529 */       this.stopOrder.remove("trailingLimit");
/*      */     }
/*      */ 
/* 1532 */     if ((null != this.trailstepCheck) && (this.trailstepCheck.isSelected())) {
/* 1533 */       BigDecimal maxTrailStep = null;
/*      */       try {
/* 1535 */         maxTrailStep = (BigDecimal)this.trailstepSpinner.getValue();
/*      */       } catch (Exception e) {
/* 1537 */         return;
/*      */       }
/* 1539 */       BigDecimal priceLimit = maxTrailStep.multiply(PriceUtil.pipValue(bestOfferValue));
/* 1540 */       this.stopOrder.setPriceLimit(new Money(priceLimit, Currency.getInstance(currencies[1])));
/*      */     } else {
/* 1542 */       this.stopOrder.remove("priceLimit");
/*      */     }
/*      */ 
/* 1545 */     if ((this.stopOrder.isTakeProfit()) && ((StopDirection.BID_GREATER.equals(this.stopOrder.getStopDirection())) || (StopDirection.ASK_LESS.equals(this.stopOrder.getStopDirection()))))
/*      */     {
/* 1549 */       this.stopOrder.setPriceTrailingLimit(new Money(BigDecimal.ZERO, Currency.getInstance(currencies[1])));
/*      */     }
/*      */ 
/* 1552 */     OrderGroupMessage newGroup = null;
/*      */     try {
/* 1554 */       newGroup = new OrderGroupMessage(new ProtocolMessage(this.orderGroup.toString()));
/*      */     } catch (ParseException e) {
/* 1556 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */ 
/* 1559 */     if (newGroup == null) {
/* 1560 */       return;
/*      */     }
/* 1562 */     if (this.newOrder) {
/* 1563 */       List orders = new ArrayList();
/* 1564 */       OrderMessage openingOrder = OrderMessageUtils.getOpeningOrder(this.orderGroup, this.stopOrder);
/*      */ 
/* 1566 */       if (LOGGER.isDebugEnabled()) {
/* 1567 */         LOGGER.debug("finding opening order " + this.stopOrder.getIfdParentOrderId() + ": " + openingOrder);
/*      */       }
/* 1569 */       if (openingOrder != null) {
/* 1570 */         orders.add(openingOrder);
/*      */       }
/*      */ 
/* 1573 */       orders.add(this.stopOrder);
/* 1574 */       newGroup.setOrders(orders);
/* 1575 */       if (LOGGER.isDebugEnabled())
/* 1576 */         if (!GreedContext.isGlobalExtended())
/* 1577 */           LOGGER.debug("a new SL/TP is created! " + this.stopOrder);
/*      */         else
/* 1579 */           LOGGER.debug("a IFD Stop/IFD Limit is created! " + this.stopOrder);
/*      */     }
/*      */     else
/*      */     {
/*      */       try {
/* 1584 */         List orders = new ArrayList();
/* 1585 */         if ((!this.stopOrder.isOpening()) && (this.orderGroup.getOpeningOrder() != null)) {
/* 1586 */           orders.add(this.orderGroup.getOpeningOrder());
/*      */         }
/*      */ 
/* 1589 */         orders.add(new OrderMessage(this.stopOrder.toString()));
/* 1590 */         newGroup.setOrders(orders);
/* 1591 */         if (LOGGER.isDebugEnabled())
/* 1592 */           if (!GreedContext.isGlobalExtended())
/* 1593 */             LOGGER.debug("a SL/TP is replaced! " + this.stopOrder);
/*      */           else
/* 1595 */             LOGGER.debug("a IFD Stop/IFD Limit is replaced! " + this.stopOrder);
/*      */       }
/*      */       catch (ParseException e)
/*      */       {
/* 1599 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */ 
/* 1603 */     if (StopOrderType.OPEN_IF.equals(this.stopOrderType)) {
/* 1604 */       LOGGER.debug("open if replace ");
/*      */ 
/* 1606 */       if (!this.amountSpinner.validateEditor()) {
/* 1607 */         return;
/*      */       }
/* 1609 */       String newAmount = this.amountSpinner.getAmountValueInMillions(Instrument.fromString(this.instrument)).toPlainString();
/*      */ 
/* 1611 */       String newGroupString = newGroup.toProtocolString().replaceAll("\"amount\":\"(\\d+(.\\d+)?)\"", "\"amount\":\"" + newAmount + "\"");
/*      */       try {
/* 1613 */         newGroup = OrderMessageUtils.copyOrderGroup(newGroupString);
/*      */       } catch (ParseException e) {
/* 1615 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1620 */     LOGGER.debug("sending out: " + newGroup);
/* 1621 */     LOGGER.debug("old group: " + this.orderGroup);
/*      */ 
/* 1623 */     PlatformInitUtils.setExtIdforOrderMessages(newGroup);
/* 1624 */     OrderEntryAction orderEntryAction = new OrderEntryAction(this, true, newGroup, OrderDirection.CLOSE, false);
/* 1625 */     GreedContext.publishEvent(orderEntryAction);
/* 1626 */     if (GreedContext.isActivityLoggingEnabled()) {
/* 1627 */       LOGGER.debug("sending out opening: " + newGroup.getOpeningOrder());
/* 1628 */       LOGGER.debug("old group opening: " + this.orderGroup.getOpeningOrder());
/* 1629 */       EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/* 1630 */       OrderMessageUtils.roughGroupValidation(this.orderGroup);
/* 1631 */       OrderMessageUtils.roughGroupValidation(newGroup);
/* 1632 */       logger.add(this.orderGroup, newGroup);
/*      */     }
/* 1634 */     removeLine();
/* 1635 */     this.parent.dispose();
/*      */   }
/*      */ 
/*      */   public static boolean amountStartsWithZero(String amount)
/*      */   {
/* 1640 */     return (amount.startsWith("0")) && (amount.indexOf(".") == -1) && (amount.length() > 1);
/*      */   }
/*      */ 
/*      */   public void quickieOrder(String instrument, OrderSide side)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void onAccountInfo(AccountInfoMessage accountInfo)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void onMarketState(CurrencyMarketWrapper market) {
/* 1651 */     if (market.getInstrument().equals(this.orderGroup.getInstrument()))
/* 1652 */       this.quoter.onTick(Instrument.fromString(market.getInstrument()));
/*      */   }
/*      */ 
/*      */   private void addPreviewInfo(OrderConfirmationDialog ocd)
/*      */   {
/* 1657 */     String side = this.stopOrder.getSide().name();
/* 1658 */     String instrument = this.stopOrder.getInstrument();
/* 1659 */     String STOP_TYPE = this.stopOrderType.name();
/*      */ 
/* 1661 */     String stopType = null;
/* 1662 */     if ((null != this.stopOrder.getPriceStop()) && (this.stopOrder.getOrderDirection() == OrderDirection.OPEN)) stopType = "preview.entry";
/* 1663 */     if (this.stopOrder.isStopLoss()) stopType = "preview.s.loss";
/* 1664 */     if (this.stopOrder.isTakeProfit()) stopType = "preview.t.profit";
/*      */ 
/* 1667 */     String stopSubType = this.stopDirection.name();
/*      */ 
/* 1671 */     double amount = this.amountSpinner.getAmountValueInMillions(Instrument.fromString(instrument)).doubleValue();
/*      */ 
/* 1673 */     String stopVal = this.stopPriceField.getText();
/*      */ 
/* 1675 */     double slippage = 0.0D;
/*      */     try {
/* 1677 */       slippage = Double.parseDouble(this.slippageField.getText());
/*      */     } catch (Exception e) {
/*      */     }
/* 1680 */     String localizedSide = "BUY".equals(side) ? LocalizationManager.getText("combo.side.buy") : LocalizationManager.getText("combo.sede.sell");
/* 1681 */     ocd.getAlertsPanel().add(new JLocalizableLabel("preview.first.line", new Object[] { localizedSide, Double.valueOf(amount), instrument }));
/*      */ 
/* 1683 */     if (null != stopType) {
/* 1684 */       if ("BUY".equals(side)) {
/* 1685 */         if (STOP_TYPE.equals(StopOrderType.OPEN_IF.name())) {
/* 1686 */           if (StopDirection.ASK_EQUALS.name().equals(stopSubType)) stopSubType = LocalizationManager.getText("preview.ASK.EQUALS");
/* 1687 */           if (StopDirection.ASK_GREATER.name().equals(stopSubType)) stopSubType = LocalizationManager.getText("preview.ASK.GREATER");
/* 1688 */           if (StopDirection.BID_GREATER.name().equals(stopSubType)) stopSubType = LocalizationManager.getText("preview.BID.GREATER");
/*      */         }
/* 1690 */         if ((STOP_TYPE.equals(StopOrderType.STOP_LOSS.name())) || (StopOrderType.IFD_STOP.equals(STOP_TYPE))) {
/* 1691 */           if (StopDirection.ASK_LESS.name().equals(stopSubType)) stopSubType = LocalizationManager.getText("preview.ASK.LESS");
/* 1692 */           if (StopDirection.BID_LESS.name().equals(stopSubType)) stopSubType = LocalizationManager.getText("preview.BID_LESS");
/*      */         }
/* 1694 */         if (((STOP_TYPE.equals(StopOrderType.TAKE_PROFIT.name())) || (StopOrderType.IFD_LIMIT.equals(STOP_TYPE))) && 
/* 1695 */           (StopDirection.BID_EQUALS.name().equals(stopSubType))) stopSubType = LocalizationManager.getText("preview.BID.EQUALS"); 
/*      */       }
/*      */       else
/*      */       {
/* 1698 */         if (STOP_TYPE.equals(StopOrderType.OPEN_IF.name())) {
/* 1699 */           if (StopDirection.BID_EQUALS.name().equals(stopSubType)) stopSubType = LocalizationManager.getText("preview.BID.EQUALS");
/* 1700 */           if (StopDirection.ASK_LESS.name().equals(stopSubType)) stopSubType = LocalizationManager.getText("preview.ASK.LESS");
/* 1701 */           if (StopDirection.BID_LESS.name().equals(stopSubType)) stopSubType = LocalizationManager.getText("preview.BID_LESS");
/*      */         }
/* 1703 */         if ((STOP_TYPE.equals(StopOrderType.STOP_LOSS.name())) || (StopOrderType.IFD_STOP.equals(STOP_TYPE))) {
/* 1704 */           if (StopDirection.ASK_GREATER.name().equals(stopSubType)) stopSubType = LocalizationManager.getText("preview.ASK.GREATER");
/* 1705 */           if (StopDirection.BID_GREATER.name().equals(stopSubType)) stopSubType = LocalizationManager.getText("preview.BID.GREATER");
/*      */         }
/* 1707 */         if (((STOP_TYPE.equals(StopOrderType.TAKE_PROFIT.name())) || (StopOrderType.IFD_LIMIT.equals(STOP_TYPE))) && 
/* 1708 */           (StopDirection.ASK_EQUALS.name().equals(stopSubType))) stopSubType = LocalizationManager.getText("preview.ASK.EQUALS");
/*      */ 
/*      */       }
/*      */ 
/* 1712 */       ocd.getAlertsPanel().add(new JLocalizableLabel("preview.stop.edit.template", new Object[] { stopType, stopSubType, stopVal }));
/*      */     }
/*      */ 
/* 1715 */     if (0.0D != slippage)
/* 1716 */       ocd.getAlertsPanel().add(new JLocalizableLabel("preview.slippage.line", new Object[] { Double.valueOf(slippage) }));
/*      */   }
/*      */ 
/*      */   private void drawOrderEntryLine()
/*      */   {
/* 1721 */     String objectName = "_soep_order_entry_line_" + this.lineId + this.instrument;
/* 1722 */     if ((this.stopPriceField.isEnabled()) && (this.stopPriceField.getText() != null) && (!this.stopPriceField.getText().equals("")))
/*      */     {
/* 1725 */       BigDecimal entry = new BigDecimal(this.stopPriceField.getText());
/* 1726 */       drawLine(objectName, "ENTRY", entry, this.stopPriceField);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void drawOrderTakeProfitLine() {
/* 1731 */     String objectName = "_soep_order_take_profit_line_" + this.lineId + this.instrument;
/* 1732 */     if ((this.stopPriceField.getText() != null) && (!this.stopPriceField.getText().equals(""))) {
/* 1733 */       BigDecimal takeProfit = new BigDecimal(this.stopPriceField.getText());
/* 1734 */       drawLine(objectName, "TP", takeProfit, this.stopPriceField);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void drawOrderStopLossLine() {
/* 1739 */     String objectName = "_soep_order_stop_loss_line_" + this.lineId + this.instrument;
/* 1740 */     if ((this.stopPriceField.getText() != null) && (!this.stopPriceField.getText().equals(""))) {
/* 1741 */       BigDecimal stopLoss = new BigDecimal(this.stopPriceField.getText());
/* 1742 */       drawLine(objectName, "SL", stopLoss, this.stopPriceField);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void drawOrderCloseStopLine() {
/* 1747 */     String objectName = "_soep_order_ifd_stop_line_" + this.lineId + this.instrument;
/* 1748 */     if ((this.stopPriceField.getText() != null) && (!this.stopPriceField.getText().equals(""))) {
/* 1749 */       BigDecimal closeStop = new BigDecimal(this.stopPriceField.getText());
/* 1750 */       drawLine(objectName, "IFDS", closeStop, this.stopPriceField);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void drawOrderCloseLimitLine() {
/* 1755 */     String objectName = "_soep_order_ifd_limit_line_" + this.lineId + this.instrument;
/* 1756 */     if ((this.stopPriceField.getText() != null) && (!this.stopPriceField.getText().equals(""))) {
/* 1757 */       BigDecimal closeStop = new BigDecimal(this.stopPriceField.getText());
/* 1758 */       drawLine(objectName, "IFDL", closeStop, this.stopPriceField);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawLine() {
/* 1763 */     switch (18.$SwitchMap$com$dukascopy$dds2$greed$model$StopOrderType[this.stopOrderType.ordinal()]) {
/*      */     case 1:
/* 1765 */       drawOrderEntryLine();
/* 1766 */       break;
/*      */     case 2:
/* 1768 */       drawOrderStopLossLine();
/* 1769 */       break;
/*      */     case 3:
/* 1771 */       drawOrderTakeProfitLine();
/* 1772 */       break;
/*      */     case 4:
/* 1774 */       drawOrderCloseStopLine();
/* 1775 */       break;
/*      */     case 5:
/* 1777 */       drawOrderCloseLimitLine();
/*      */     }
/*      */   }
/*      */ 
/*      */   public DDSChartsController getDdsChartsController()
/*      */   {
/* 1783 */     if (this.ddsChartsController == null) {
/* 1784 */       this.ddsChartsController = ((DDSChartsController)GreedContext.get("chartsController"));
/*      */     }
/* 1786 */     return this.ddsChartsController;
/*      */   }
/*      */ 
/*      */   private void drawLine(String objectName, String type, BigDecimal price, PriceSpinner priceField)
/*      */   {
/*      */     List chartObjects;
/* 1790 */     if (GreedContext.isStrategyAllowed()) {
/* 1791 */       Instrument apiInstrument = Instrument.fromString(this.instrument);
/* 1792 */       Set charts = getDdsChartsController().getICharts(apiInstrument);
/* 1793 */       chartObjects = new ArrayList();
/* 1794 */       for (IChart chart : charts) {
/* 1795 */         IChartObject hLine = chart.get(objectName);
/* 1796 */         if (hLine == null) {
/* 1797 */           hLine = chart.drawUnlocked(objectName, IChart.Type.PRICEMARKER, 0L, price.doubleValue());
/* 1798 */           if (hLine != null) {
/* 1799 */             hLine.setMenuEnabled(false);
/*      */ 
/* 1801 */             Color color = Color.GRAY;
/* 1802 */             if (("SL".equals(type)) || ("TP".equals(type)) || (type.startsWith("ENTRY"))) {
/* 1803 */               color = ((IChartWrapper)chart).getThemeColor(this.positionSide.equals(PositionSide.LONG) ? ITheme.ChartElement.ORDER_CLOSE_BUY : ITheme.ChartElement.ORDER_CLOSE_SELL);
/*      */             }
/*      */ 
/* 1806 */             hLine.setColor(color);
/*      */ 
/* 1808 */             hLine.setText(type);
/* 1809 */             hLine.setSticky(false);
/* 1810 */             BasicStroke stroke = new BasicStroke(1.0F, 0, 0, 10.0F, new float[] { 2.0F, 4.0F }, 0.0F);
/* 1811 */             hLine.setStroke(stroke);
/* 1812 */             hLine.setChartObjectListener(new ChartObjectAdapter(priceField, chartObjects)
/*      */             {
/*      */               public void moved(ChartObjectEvent e) {
/* 1815 */                 double price = StratUtils.round05Pips(e.getNewDouble());
/* 1816 */                 this.val$priceField.setText(BigDecimal.valueOf(price).stripTrailingZeros().toPlainString());
/* 1817 */                 e.setNewDouble(price);
/*      */ 
/* 1821 */                 for (IChartObject chartObject : this.val$chartObjects) {
/* 1822 */                   chartObject.move(0L, price);
/*      */                 }
/* 1824 */                 StopOrderEditPanel.this.getDdsChartsController().refreshChartsContent();
/*      */               }
/*      */ 
/*      */               public void deleted(ChartObjectEvent e)
/*      */               {
/* 1829 */                 e.cancel();
/*      */               }
/*      */             });
/* 1832 */             chartObjects.add(hLine);
/*      */           }
/*      */         } else {
/* 1835 */           hLine.move(0L, price.doubleValue());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeLine()
/*      */   {
/*      */     String objectName;
/* 1842 */     if (GreedContext.isStrategyAllowed())
/*      */     {
/* 1844 */       switch (18.$SwitchMap$com$dukascopy$dds2$greed$model$StopOrderType[this.stopOrderType.ordinal()]) {
/*      */       case 1:
/* 1846 */         objectName = "_soep_order_entry_line_" + this.lineId + this.instrument;
/* 1847 */         break;
/*      */       case 2:
/* 1849 */         objectName = "_soep_order_stop_loss_line_" + this.lineId + this.instrument;
/* 1850 */         break;
/*      */       case 3:
/* 1852 */         objectName = "_soep_order_take_profit_line_" + this.lineId + this.instrument;
/* 1853 */         break;
/*      */       case 4:
/* 1855 */         objectName = "_soep_order_ifd_stop_line_" + this.lineId + this.instrument;
/* 1856 */         break;
/*      */       case 5:
/* 1858 */         objectName = "_soep_order_ifd_limit_line_" + this.lineId + this.instrument;
/* 1859 */         break;
/*      */       default:
/* 1862 */         return;
/*      */       }
/*      */ 
/* 1865 */       Set charts = getDdsChartsController().getICharts(Instrument.fromString(this.instrument));
/* 1866 */       for (IChart chart : charts)
/* 1867 */         chart.remove(objectName);
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.StopOrderEditPanel
 * JD-Core Version:    0.6.0
 */