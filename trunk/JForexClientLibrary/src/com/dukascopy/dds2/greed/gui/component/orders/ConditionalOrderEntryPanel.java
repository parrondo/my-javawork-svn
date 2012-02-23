/*      */ package com.dukascopy.dds2.greed.gui.component.orders;
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
/*      */ import com.dukascopy.dds2.greed.actions.SignalAction;
/*      */ import com.dukascopy.dds2.greed.agent.DDSAgent;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*      */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*      */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*      */ import com.dukascopy.dds2.greed.gui.component.MouseController;
/*      */ import com.dukascopy.dds2.greed.gui.component.PriceAmountTextField;
/*      */ import com.dukascopy.dds2.greed.gui.component.PriceAmountTextFieldM;
/*      */ import com.dukascopy.dds2.greed.gui.component.PriceSpinner;
/*      */ import com.dukascopy.dds2.greed.gui.component.dialog.components.OrderTimeLimitationPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.ifdone.IEntryOrderHolder;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.ifdone.IfDonePanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitPane;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.Hidable;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableComboBox;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableDialog;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableQuoterPanel;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.gui.util.spinners.CommonJSpinner;
/*      */ import com.dukascopy.dds2.greed.gui.util.spinners.SlippageJSpinner;
/*      */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*      */ import com.dukascopy.dds2.greed.model.MarketView;
/*      */ import com.dukascopy.dds2.greed.model.StopOrderType;
/*      */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*      */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*      */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.OfferSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.model.type.StopDirection;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*      */ import com.dukascopy.transport.common.msg.signals.SignalMessage;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.Point;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.KeyListener;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.geom.Arc2D;
/*      */ import java.awt.geom.Arc2D.Double;
/*      */ import java.math.BigDecimal;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.DefaultComboBoxModel;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import javax.swing.event.PopupMenuEvent;
/*      */ import javax.swing.event.PopupMenuListener;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public abstract class ConditionalOrderEntryPanel extends JPanel
/*      */   implements PlatformSpecific, IEntryOrderHolder, Hidable
/*      */ {
/*  148 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrderEntryPanel.class);
/*  149 */   private static final BigDecimal COPY_PRICE = new BigDecimal("-1");
/*      */   public static final String ORDER_ENTRY_LINE = "_order_entry_line";
/*      */   public static final String ORDER_STOP_LOSS_LINE = "_order_stop_loss_line";
/*      */   public static final String ORDER_TAKE_PROFIT_LINE = "_order_take_profit_line";
/*      */   public static final String HLINE_TYPE_ENTRY = "ENTRY";
/*      */   public static final String HLINE_TYPE_SL = "SL";
/*      */   public static final String HLINE_TYPE_TP = "TP";
/*      */   private JLocalizableRoundedBorder border;
/*  161 */   private String lineId = "";
/*      */ 
/*  163 */   private final JLocalizableCheckBox entryCheckBox = new JLocalizableCheckBox("check.entry");
/*  164 */   private final PriceSpinner entryPriceField = new PriceSpinner();
/*      */   private DependandHeightButton entryStopDirectionButton;
/*  167 */   private final JLocalizableCheckBox stopLossCheckBox = new JLocalizableCheckBox("check.stop.loss");
/*      */   private DependandHeightButton stopLossStopDirectionButton;
/*  169 */   private final PriceSpinner stopLossPriceField = new PriceSpinner();
/*      */ 
/*  171 */   private final JLocalizableCheckBox takeProfitCheckBox = new JLocalizableCheckBox("check.take.profit");
/*      */   private DependandHeightButton takeProfitStopDirectionButton;
/*  173 */   private final PriceSpinner takeProfitPriceField = new PriceSpinner();
/*      */ 
/*  175 */   private final JLocalizableCheckBox slippageCheckBox = new JLocalizableCheckBox("check.slippage");
/*  176 */   private final SlippageJSpinner slippageSpinner = new SlippageJSpinner(5.0D, 1000.0D, 0.1D, 1, false);
/*      */ 
/*  178 */   private final JLocalizableButton submitButton = new JLocalizableButton("button.submit.orders");
/*      */ 
/*  180 */   private JPopupMenu conditionsPopup = new JPopupMenu();
/*      */ 
/*  182 */   private StopDirection entryStopDirection = null;
/*  183 */   private StopDirection stopLossStopDirection = null;
/*  184 */   private StopDirection takeProfitStopDirection = null;
/*      */   private JPanel conditionalOrdersPanel;
/*      */   private OrderTimeLimitationPanel timeLimPanel;
/*      */   private IfDonePanel ifDonePanel;
/*      */   private MouseController mController;
/*      */   private ClientSettingsStorage storage;
/*      */   private Instrument instrument;
/*      */   private JLocalizableQuoterPanel quoter;
/*      */   private DDSChartsController ddsChartsController;
/*      */   private SubmitActionListener submitActionListener;
/*      */   private boolean orderSideLocked;
/*      */   private boolean entryLocked;
/*  203 */   private MarketView marketView = (MarketView)GreedContext.get("marketView");
/*      */ 
/*  205 */   private final JLocalizableComboBox sideComboBox = new JLocalizableComboBox(new String[] { LocalizationManager.getText("combo.side.buy"), LocalizationManager.getText("combo.sede.sell") }, null)
/*      */   {
/*      */     public void translate()
/*      */     {
/*  209 */       if (ConditionalOrderEntryPanel.this.sideComboBox != null) {
/*  210 */         int selected = ConditionalOrderEntryPanel.this.sideComboBox.getSelectedIndex();
/*  211 */         DefaultComboBoxModel dcbm = new DefaultComboBoxModel(getLocalizedSides());
/*  212 */         ConditionalOrderEntryPanel.this.sideComboBox.setModel(dcbm);
/*  213 */         ConditionalOrderEntryPanel.this.sideComboBox.setSelectedIndex(selected);
/*      */       }
/*      */     }
/*      */ 
/*      */     private String[] getLocalizedSides() {
/*  218 */       return new String[] { LocalizationManager.getText("combo.side.buy"), LocalizationManager.getText("combo.sede.sell") };
/*      */     }
/*  205 */   };
/*      */ 
/*      */   public ConditionalOrderEntryPanel(Instrument instrument, JLocalizableQuoterPanel quoter, String lineId)
/*      */   {
/*  223 */     this(instrument, quoter);
/*  224 */     this.lineId = lineId;
/*      */   }
/*      */ 
/*      */   public ConditionalOrderEntryPanel(Instrument instrument, JLocalizableQuoterPanel quoter) {
/*  228 */     this.instrument = instrument;
/*  229 */     this.quoter = quoter;
/*  230 */     this.storage = ((ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*      */ 
/*  232 */     build();
/*      */   }
/*      */ 
/*      */   private void build()
/*      */   {
/*  239 */     setOpaque(false);
/*  240 */     setLayout(new GridBagLayout());
/*  241 */     this.border = new JLocalizableRoundedBorder(this, "header.cond.orders", true);
/*  242 */     this.border.setTopInset(12);
/*  243 */     this.border.setRightInset(7);
/*  244 */     this.border.setLeftInset(9);
/*      */ 
/*  246 */     setBorder(this.border);
/*      */ 
/*  248 */     this.conditionalOrdersPanel = new JPanel();
/*  249 */     this.conditionalOrdersPanel.setOpaque(false);
/*      */ 
/*  251 */     this.conditionalOrdersPanel.setLayout(new GridBagLayout());
/*  252 */     GridBagConstraints gbc = new GridBagConstraints();
/*      */ 
/*  254 */     int gridY = 0;
/*      */ 
/*  256 */     JLocalizableLabel sideLabel = new JLocalizableLabel("label.side");
/*  257 */     if (MACOSX) {
/*  258 */       sideLabel.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  260 */     GridBagLayoutHelper.add(0, gridY, 0.0D, 0.0D, 1, 1, 6, 1, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, sideLabel);
/*      */ 
/*  262 */     if (MACOSX) {
/*  263 */       this.sideComboBox.putClientProperty("JComboBox.isSquare", Boolean.TRUE);
/*  264 */       this.sideComboBox.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  266 */     GridBagLayoutHelper.add(1, gridY++, 1.0D, 0.0D, 2, 1, 0, 1, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.sideComboBox);
/*      */ 
/*  268 */     if (MACOSX) {
/*  269 */       this.entryCheckBox.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  271 */     GridBagLayoutHelper.add(0, gridY, 0.0D, 0.0D, 1, 1, 0, 3, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.entryCheckBox);
/*      */ 
/*  273 */     this.entryStopDirectionButton = new DependandHeightButton("L_MK", this.entryPriceField);
/*  274 */     if (MACOSX) {
/*  275 */       this.entryStopDirectionButton.putClientProperty("JButton.buttonType", "textured");
/*  276 */       this.entryStopDirectionButton.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  278 */     this.entryStopDirectionButton.setEnabled(this.entryCheckBox.isSelected());
/*  279 */     GridBagLayoutHelper.add(1, gridY, 0.0D, 0.0D, 1, 1, 0, 3, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.entryStopDirectionButton);
/*      */ 
/*  281 */     this.entryPriceField.setEnabled(false);
/*  282 */     this.entryPriceField.setMargin(new Insets(0, 0, 0, 1));
/*  283 */     this.entryPriceField.setHorizontalAlignment(4);
/*      */ 
/*  285 */     ((PriceAmountTextFieldM)this.entryPriceField.getEditor()).setColumns(7);
/*  286 */     GridBagLayoutHelper.add(2, gridY++, 1.0D, 0.0D, 1, 1, 0, 3, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.entryPriceField);
/*      */ 
/*  288 */     if (MACOSX) {
/*  289 */       this.slippageCheckBox.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  291 */     this.slippageCheckBox.setSelected(false);
/*  292 */     GridBagLayoutHelper.add(0, gridY, 0.0D, 0.0D, 1, 1, 0, 1, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.slippageCheckBox);
/*      */ 
/*  294 */     if (MACOSX) {
/*  295 */       this.slippageSpinner.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  297 */     this.slippageSpinner.setEnabled(false);
/*  298 */     this.slippageSpinner.setHorizontalAlignment(4);
/*  299 */     if (this.storage.restoreApplySlippageToAllMarketOrders()) {
/*  300 */       this.slippageSpinner.setValue(this.storage.restoreDefaultSlippage());
/*      */     }
/*  302 */     GridBagLayoutHelper.add(1, gridY++, 1.0D, 0.0D, 2, 1, 0, 1, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.slippageSpinner);
/*      */ 
/*  304 */     if (MACOSX) {
/*  305 */       this.stopLossCheckBox.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  307 */     GridBagLayoutHelper.add(0, gridY, 0.0D, 0.0D, 1, 1, 0, 1, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.stopLossCheckBox);
/*      */ 
/*  309 */     this.stopLossStopDirectionButton = new DependandHeightButton("DOTS", this.stopLossPriceField);
/*  310 */     if (MACOSX) {
/*  311 */       this.stopLossStopDirectionButton.putClientProperty("JButton.buttonType", "textured");
/*  312 */       this.stopLossStopDirectionButton.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  314 */     this.stopLossStopDirectionButton.setEnabled(this.stopLossCheckBox.isSelected());
/*  315 */     GridBagLayoutHelper.add(1, gridY, 0.0D, 0.0D, 1, 1, 0, 1, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.stopLossStopDirectionButton);
/*      */ 
/*  317 */     this.stopLossPriceField.setHorizontalAlignment(4);
/*  318 */     this.stopLossPriceField.setEnabled(false);
/*  319 */     this.stopLossPriceField.setMargin(new Insets(0, 0, 0, 1));
/*      */ 
/*  321 */     ((PriceAmountTextFieldM)this.stopLossPriceField.getEditor()).setColumns(7);
/*  322 */     GridBagLayoutHelper.add(2, gridY++, 1.0D, 0.0D, 1, 1, 0, 1, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.stopLossPriceField);
/*      */ 
/*  324 */     if (MACOSX) {
/*  325 */       this.takeProfitCheckBox.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  327 */     GridBagLayoutHelper.add(0, gridY, 0.0D, 0.0D, 1, 1, 0, 1, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.takeProfitCheckBox);
/*      */ 
/*  329 */     this.takeProfitStopDirectionButton = new DependandHeightButton("DOTS", this.takeProfitPriceField);
/*  330 */     if (MACOSX) {
/*  331 */       this.takeProfitStopDirectionButton.putClientProperty("JButton.buttonType", "textured");
/*  332 */       this.takeProfitStopDirectionButton.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  334 */     this.takeProfitStopDirectionButton.setEnabled(this.takeProfitCheckBox.isSelected());
/*  335 */     GridBagLayoutHelper.add(1, gridY, 0.0D, 0.0D, 1, 1, 0, 1, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.takeProfitStopDirectionButton);
/*      */ 
/*  337 */     if (MACOSX) {
/*  338 */       this.takeProfitPriceField.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*  340 */     this.takeProfitPriceField.setHorizontalAlignment(4);
/*  341 */     this.takeProfitPriceField.setEnabled(false);
/*  342 */     this.takeProfitPriceField.setMargin(new Insets(0, 0, 0, 1));
/*      */ 
/*  344 */     ((PriceAmountTextFieldM)this.takeProfitPriceField.getEditor()).setColumns(7);
/*  345 */     GridBagLayoutHelper.add(2, gridY++, 1.0D, 0.0D, 1, 1, 0, 1, 0, 0, 2, 21, gbc, this.conditionalOrdersPanel, this.takeProfitPriceField);
/*      */ 
/*  347 */     if (MACOSX) {
/*  348 */       this.submitButton.putClientProperty("JButton.buttonType", "textured");
/*  349 */       this.submitButton.putClientProperty("JComponent.sizeVariant", "small");
/*      */     }
/*      */ 
/*  352 */     if (GreedContext.isGlobalExtended()) {
/*  353 */       this.timeLimPanel = new OrderTimeLimitationPanel((ClientForm)GreedContext.get("clientGui"));
/*  354 */       this.ifDonePanel = new IfDonePanel(this);
/*      */ 
/*  357 */       if (GreedContext.isTest()) {
/*  358 */         this.ifDonePanel.setEnabled(true);
/*  359 */         this.timeLimPanel.setEnabled(true);
/*      */       } else {
/*  361 */         this.ifDonePanel.setEnabled(false);
/*  362 */         this.timeLimPanel.setEnabled(false);
/*      */       }
/*      */ 
/*  365 */       GridBagLayoutHelper.add(0, gridY++, 1.0D, 0.0D, 3, 1, 0, 1, 0, 3, 2, 21, gbc, this.conditionalOrdersPanel, this.timeLimPanel);
/*      */ 
/*  370 */       GridBagLayoutHelper.add(0, gridY++, 1.0D, 0.0D, 3, 1, 0, 1, 0, 3, 2, 21, gbc, this.conditionalOrdersPanel, this.ifDonePanel);
/*      */     }
/*      */ 
/*  377 */     this.submitButton.setEnabled(false);
/*  378 */     GridBagLayoutHelper.add(0, gridY, 1.0D, 0.0D, 3, 5, 0, 1, 0, 1, 2, 21, gbc, this.conditionalOrdersPanel, this.submitButton);
/*      */ 
/*  381 */     this.entryStopDirectionButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
/*  382 */     this.stopLossStopDirectionButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
/*  383 */     this.takeProfitStopDirectionButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
/*      */ 
/*  385 */     int buttonsWidth = this.entryStopDirectionButton.getPreferredSize().width;
/*  386 */     this.entryStopDirectionButton.setPreferredSize(new Dimension(buttonsWidth, this.entryStopDirectionButton.getPreferredSize().height));
/*  387 */     this.stopLossStopDirectionButton.setPreferredSize(new Dimension(buttonsWidth, this.stopLossStopDirectionButton.getPreferredSize().height));
/*  388 */     this.takeProfitStopDirectionButton.setPreferredSize(new Dimension(buttonsWidth, this.takeProfitStopDirectionButton.getPreferredSize().height));
/*  389 */     this.entryStopDirectionButton.setMinimumSize(new Dimension(buttonsWidth, this.entryStopDirectionButton.getPreferredSize().height));
/*  390 */     this.stopLossStopDirectionButton.setMinimumSize(new Dimension(buttonsWidth, this.stopLossStopDirectionButton.getPreferredSize().height));
/*  391 */     this.takeProfitStopDirectionButton.setMinimumSize(new Dimension(buttonsWidth, this.takeProfitStopDirectionButton.getPreferredSize().height));
/*      */ 
/*  393 */     this.submitButton.addMouseListener(new MouseAdapter() {
/*      */       public void mouseEntered(MouseEvent e) {
/*  395 */         GreedContext.setConfig("timein", Long.valueOf(System.currentTimeMillis()));
/*  396 */         GreedContext.setConfig("control", ConditionalOrderEntryPanel.this.submitButton);
/*      */ 
/*  398 */         if (GreedContext.isSmspcEnabled()) {
/*  399 */           OrderSide side = ConditionalOrderEntryPanel.this.sideComboBox.getSelectedIndex() == 0 ? OrderSide.BUY : OrderSide.SELL;
/*  400 */           BigDecimal amoutBDValue = ConditionalOrderEntryPanel.this.getAmount();
/*  401 */           if (amoutBDValue != null)
/*      */             try {
/*  403 */               BigDecimal amount = amoutBDValue.multiply(GuiUtilsAndConstants.ONE_MILLION);
/*  404 */               String slippage = GuiUtilsAndConstants.getSlippageAmount(ConditionalOrderEntryPanel.this.getSlippage() == null ? null : ConditionalOrderEntryPanel.this.getSlippage().toPlainString(), side, ConditionalOrderEntryPanel.this.instrument.toString());
/*  405 */               SignalMessage signal = new SignalMessage(ConditionalOrderEntryPanel.this.instrument.toString(), "tbsb", side, amount, GreedContext.encodeAuth(), slippage);
/*  406 */               GreedContext.publishEvent(new SignalAction(this, signal));
/*      */             } catch (NumberFormatException e1) {
/*  408 */               ConditionalOrderEntryPanel.LOGGER.debug(e1.getMessage());
/*      */             }
/*      */         }
/*      */       }
/*      */ 
/*      */       public void mouseExited(MouseEvent e)
/*      */       {
/*  415 */         GuiUtilsAndConstants.sendResetSignal(this);
/*      */       }
/*      */     });
/*  419 */     this.mController = new MouseController(this);
/*  420 */     addMouseMotionListener(this.mController);
/*  421 */     addMouseListener(this.mController);
/*      */ 
/*  423 */     GridBagLayoutHelper.add(0, 0, 1.0D, 1.0D, 1, 1, 7, 9, 7, 0, 0, 19, gbc, this, this.conditionalOrdersPanel);
/*      */ 
/*  425 */     setupListeners();
/*  426 */     createConditionsPopupMenu();
/*      */ 
/*  428 */     this.sideComboBox.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  430 */         ConditionalOrderEntryPanel.this.setDefaultStopConditionLabels(false);
/*      */ 
/*  432 */         if ((ConditionalOrderEntryPanel.this.entryCheckBox.isSelected()) && 
/*  433 */           (ConditionalOrderEntryPanel.this.entryStopDirection != null)) {
/*  434 */           switch (ConditionalOrderEntryPanel.24.$SwitchMap$com$dukascopy$transport$common$model$type$StopDirection[ConditionalOrderEntryPanel.this.entryStopDirection.ordinal()]) {
/*      */           case 1:
/*  436 */             ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.BID_LESS);
/*  437 */             ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_BL");
/*  438 */             break;
/*      */           case 2:
/*  440 */             ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.ASK_LESS);
/*  441 */             ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_AL");
/*  442 */             break;
/*      */           case 3:
/*  444 */             ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.ASK_GREATER);
/*  445 */             ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_AG");
/*  446 */             break;
/*      */           case 4:
/*  448 */             ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.BID_GREATER);
/*  449 */             ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_BG");
/*  450 */             break;
/*      */           case 5:
/*  452 */             ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.BID_EQUALS);
/*  453 */             if (ConditionalOrderEntryPanel.this.slippageCheckBox.isEnabled())
/*  454 */               ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_BES");
/*      */             else {
/*  456 */               ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_BE");
/*      */             }
/*  458 */             break;
/*      */           case 6:
/*  460 */             ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.ASK_EQUALS);
/*  461 */             if (ConditionalOrderEntryPanel.this.slippageCheckBox.isEnabled())
/*  462 */               ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_AES");
/*      */             else {
/*  464 */               ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_AE");
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  470 */         ConditionalOrderEntryPanel.this.drawOrderEntryLine();
/*  471 */         if (GreedContext.isGlobalExtended())
/*  472 */           ConditionalOrderEntryPanel.this.ifDonePanel.changeSideForPanel();
/*      */       }
/*      */     });
/*  477 */     this.entryPriceField.addChangeListener(new ChangeListener() {
/*      */       public void stateChanged(ChangeEvent e) {
/*  479 */         ConditionalOrderEntryPanel.this.drawOrderEntryLine();
/*      */       }
/*      */     });
/*  482 */     this.takeProfitPriceField.addChangeListener(new ChangeListener() {
/*      */       public void stateChanged(ChangeEvent e) {
/*  484 */         ConditionalOrderEntryPanel.this.drawOrderTakeProfitLine();
/*      */       }
/*      */     });
/*  487 */     this.stopLossPriceField.addChangeListener(new ChangeListener() {
/*      */       public void stateChanged(ChangeEvent e) {
/*  489 */         ConditionalOrderEntryPanel.this.drawOrderStopLossLine();
/*      */       }
/*      */     });
/*  493 */     this.border.setSwitch(this.conditionalOrdersPanel.isVisible());
/*      */   }
/*      */ 
/*      */   protected abstract BigDecimal getAmount();
/*      */ 
/*      */   public Dimension getMaximumSize()
/*      */   {
/*  501 */     return new Dimension(800, 600);
/*      */   }
/*      */ 
/*      */   protected void paintComponent(Graphics g) {
/*  505 */     super.paintComponent(g);
/*  506 */     if (!this.conditionalOrdersPanel.isVisible()) {
/*  507 */       return;
/*      */     }
/*  509 */     Point pAnchor = this.conditionalOrdersPanel.getLocation();
/*  510 */     Point pTop = this.entryCheckBox.getLocation();
/*  511 */     Point pBottom = this.slippageCheckBox.getLocation();
/*  512 */     int DX = 5;
/*      */ 
/*  514 */     g.setColor(Color.LIGHT_GRAY);
/*      */ 
/*  516 */     Graphics2D g2 = (Graphics2D)g;
/*  517 */     Object hint = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
/*  518 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*      */ 
/*  520 */     Arc2D a = new Arc2D.Double();
/*  521 */     a.setArc(new Point((int)(pAnchor.getX() + pTop.getX() - 5.0D), (int)(pAnchor.getY() + pTop.getY() + this.entryCheckBox.getHeight() / 2)), new Dimension(9, 7), 90.0D, 90.0D, 0);
/*      */ 
/*  528 */     g2.draw(a);
/*      */ 
/*  531 */     a.setArc(new Point((int)(pAnchor.getX() + pTop.getX() - 5.0D), (int)(pAnchor.getY() + pBottom.getY() + this.slippageCheckBox.getHeight() / 2) - 6), new Dimension(9, 7), 180.0D, 90.0D, 0);
/*      */ 
/*  538 */     g2.draw(a);
/*      */ 
/*  540 */     g.drawLine((int)(pAnchor.getX() + pTop.getX() - 5.0D), (int)(pAnchor.getY() + pTop.getY() + this.entryCheckBox.getHeight() / 2 + 4.0D), (int)(pAnchor.getX() + pTop.getX() - 5.0D), (int)(pAnchor.getY() + pBottom.getY() + this.slippageCheckBox.getHeight() / 2 - 4.0D));
/*      */ 
/*  545 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, hint);
/*      */   }
/*      */ 
/*      */   public void createConditionsPopupMenu()
/*      */   {
/*  556 */     ButtonGroup group = new ButtonGroup();
/*  557 */     this.conditionsPopup.setBorderPainted(true);
/*      */ 
/*  560 */     this.conditionsPopup.addPopupMenuListener(new PopupMenuListener()
/*      */     {
/*      */       public void popupMenuCanceled(PopupMenuEvent e) {
/*  563 */         if ("L_MK".equals(ConditionalOrderEntryPanel.this.entryStopDirectionButton.getTextKey())) {
/*  564 */           ConditionalOrderEntryPanel.this.setMarketsSlippage();
/*  565 */           ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void popupMenuWillBecomeVisible(PopupMenuEvent e)
/*      */       {
/*      */       }
/*      */     });
/*  580 */     JLocalizableMenuItem item = new JLocalizableMenuItem("SD_MK");
/*  581 */     item.setAction(new AbstractAction("SD_MK") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  583 */         JButton button = (JButton)ConditionalOrderEntryPanel.this.conditionsPopup.getInvoker();
/*  584 */         if (ConditionalOrderEntryPanel.this.entryStopDirectionButton == button)
/*  585 */           ConditionalOrderEntryPanel.this.setEntryMarketCondition(false);
/*      */       }
/*      */     });
/*  590 */     group.add(item);
/*  591 */     item.setName("SD_MK");
/*  592 */     this.conditionsPopup.add(item);
/*      */ 
/*  596 */     item = new JLocalizableMenuItem("SD_B_L");
/*  597 */     item.setAction(new AbstractAction("SD_B_L") {
/*      */       public void actionPerformed(ActionEvent actionEvent) {
/*  599 */         JButton button = (JButton)ConditionalOrderEntryPanel.this.conditionsPopup.getInvoker();
/*  600 */         if (button == ConditionalOrderEntryPanel.this.stopLossStopDirectionButton) {
/*  601 */           ConditionalOrderEntryPanel.access$1602(ConditionalOrderEntryPanel.this, StopDirection.BID_LESS);
/*  602 */           ConditionalOrderEntryPanel.this.stopLossStopDirectionButton.setText("L_BL");
/*  603 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.stopLossPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.STOP_LOSS));
/*  604 */           ConditionalOrderEntryPanel.this.drawOrderStopLossLine();
/*  605 */         } else if (button == ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton) {
/*  606 */           ConditionalOrderEntryPanel.access$2102(ConditionalOrderEntryPanel.this, StopDirection.BID_LESS);
/*  607 */           ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton.setText("L_BL");
/*  608 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.takeProfitPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.TAKE_PROFIT));
/*  609 */           ConditionalOrderEntryPanel.this.drawOrderTakeProfitLine();
/*  610 */         } else if (button == ConditionalOrderEntryPanel.this.entryStopDirectionButton) {
/*  611 */           ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.BID_LESS);
/*  612 */           ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_BL");
/*      */ 
/*  614 */           ConditionalOrderEntryPanel.this.setStopsSlippage();
/*  615 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.entryPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.OPEN_IF));
/*  616 */           ConditionalOrderEntryPanel.this.drawOrderEntryLine();
/*      */         }
/*  618 */         ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */       }
/*      */     });
/*  621 */     group.add(item);
/*  622 */     item.setName("SD_B_L");
/*  623 */     this.conditionsPopup.add(item);
/*      */ 
/*  628 */     if (!GreedContext.isGlobal()) {
/*  629 */       item = new JLocalizableMenuItem("SD_B_G");
/*  630 */       item.setAction(new AbstractAction("SD_B_G") {
/*      */         public void actionPerformed(ActionEvent actionEvent) {
/*  632 */           JButton button = (JButton)ConditionalOrderEntryPanel.this.conditionsPopup.getInvoker();
/*  633 */           if (button == ConditionalOrderEntryPanel.this.stopLossStopDirectionButton) {
/*  634 */             ConditionalOrderEntryPanel.access$1602(ConditionalOrderEntryPanel.this, StopDirection.BID_GREATER);
/*  635 */             ConditionalOrderEntryPanel.this.stopLossStopDirectionButton.setText("L_BG");
/*  636 */             ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.stopLossPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.STOP_LOSS));
/*  637 */             ConditionalOrderEntryPanel.this.drawOrderStopLossLine();
/*  638 */           } else if (button == ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton) {
/*  639 */             ConditionalOrderEntryPanel.access$2102(ConditionalOrderEntryPanel.this, StopDirection.BID_GREATER);
/*  640 */             ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton.setText("L_BG");
/*  641 */             ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.takeProfitPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.TAKE_PROFIT));
/*  642 */             ConditionalOrderEntryPanel.this.drawOrderTakeProfitLine();
/*  643 */           } else if (button == ConditionalOrderEntryPanel.this.entryStopDirectionButton) {
/*  644 */             ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.BID_GREATER);
/*  645 */             ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_BG");
/*      */ 
/*  647 */             ConditionalOrderEntryPanel.this.setStopsSlippage();
/*  648 */             ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.entryPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.OPEN_IF));
/*  649 */             ConditionalOrderEntryPanel.this.drawOrderEntryLine();
/*      */           }
/*  651 */           ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */         }
/*      */       });
/*  654 */       group.add(item);
/*  655 */       item.setName("SD_B_G");
/*  656 */       this.conditionsPopup.add(item);
/*      */ 
/*  660 */       item = new JLocalizableMenuItem("SD_A_L");
/*  661 */       item.setAction(new AbstractAction("SD_A_L") {
/*      */         public void actionPerformed(ActionEvent actionEvent) {
/*  663 */           JButton button = (JButton)ConditionalOrderEntryPanel.this.conditionsPopup.getInvoker();
/*  664 */           if (button == ConditionalOrderEntryPanel.this.stopLossStopDirectionButton) {
/*  665 */             ConditionalOrderEntryPanel.access$1602(ConditionalOrderEntryPanel.this, StopDirection.ASK_LESS);
/*  666 */             ConditionalOrderEntryPanel.this.stopLossStopDirectionButton.setText("L_AL");
/*  667 */             ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.stopLossPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.STOP_LOSS));
/*  668 */             ConditionalOrderEntryPanel.this.drawOrderStopLossLine();
/*  669 */           } else if (button == ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton) {
/*  670 */             ConditionalOrderEntryPanel.access$2102(ConditionalOrderEntryPanel.this, StopDirection.ASK_LESS);
/*  671 */             ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton.setText("L_AL");
/*  672 */             ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.takeProfitPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.TAKE_PROFIT));
/*  673 */             ConditionalOrderEntryPanel.this.drawOrderTakeProfitLine();
/*  674 */           } else if (button == ConditionalOrderEntryPanel.this.entryStopDirectionButton) {
/*  675 */             ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.ASK_LESS);
/*  676 */             ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_AL");
/*  677 */             ConditionalOrderEntryPanel.this.setStopsSlippage();
/*  678 */             ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.entryPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.OPEN_IF));
/*  679 */             ConditionalOrderEntryPanel.this.drawOrderEntryLine();
/*      */           }
/*  681 */           ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */         }
/*      */       });
/*  684 */       group.add(item);
/*  685 */       item.setName("SD_A_L");
/*  686 */       this.conditionsPopup.add(item);
/*      */     }
/*      */ 
/*  692 */     item = new JLocalizableMenuItem("SD_A_G");
/*  693 */     item.setAction(new AbstractAction("SD_A_G") {
/*      */       public void actionPerformed(ActionEvent actionEvent) {
/*  695 */         JButton button = (JButton)ConditionalOrderEntryPanel.this.conditionsPopup.getInvoker();
/*  696 */         if (button == ConditionalOrderEntryPanel.this.stopLossStopDirectionButton) {
/*  697 */           ConditionalOrderEntryPanel.access$1602(ConditionalOrderEntryPanel.this, StopDirection.ASK_GREATER);
/*  698 */           ConditionalOrderEntryPanel.this.stopLossStopDirectionButton.setText("L_AG");
/*  699 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.stopLossPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.STOP_LOSS));
/*  700 */           ConditionalOrderEntryPanel.this.drawOrderStopLossLine();
/*  701 */         } else if (button == ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton) {
/*  702 */           ConditionalOrderEntryPanel.access$2102(ConditionalOrderEntryPanel.this, StopDirection.ASK_GREATER);
/*  703 */           ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton.setText("L_AG");
/*  704 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.takeProfitPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.TAKE_PROFIT));
/*  705 */           ConditionalOrderEntryPanel.this.drawOrderTakeProfitLine();
/*  706 */         } else if (button == ConditionalOrderEntryPanel.this.entryStopDirectionButton) {
/*  707 */           ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.ASK_GREATER);
/*  708 */           ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_AG");
/*      */ 
/*  710 */           ConditionalOrderEntryPanel.this.setStopsSlippage();
/*  711 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.entryPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.OPEN_IF));
/*  712 */           ConditionalOrderEntryPanel.this.drawOrderEntryLine();
/*      */         }
/*  714 */         ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */       }
/*      */     });
/*  717 */     group.add(item);
/*  718 */     item.setName("SD_A_G");
/*  719 */     this.conditionsPopup.add(item);
/*      */ 
/*  722 */     item = new JLocalizableMenuItem("SD_B_E");
/*  723 */     item.setAction(new AbstractAction("SD_B_E") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  725 */         JButton button = (JButton)ConditionalOrderEntryPanel.this.conditionsPopup.getInvoker();
/*  726 */         if (ConditionalOrderEntryPanel.this.entryStopDirectionButton == button) {
/*  727 */           ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.BID_EQUALS);
/*  728 */           ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_BE");
/*  729 */           ConditionalOrderEntryPanel.this.zeroSlippage();
/*  730 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.entryPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.OPEN_IF));
/*  731 */           ConditionalOrderEntryPanel.this.drawOrderEntryLine();
/*  732 */         } else if (ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton == button) {
/*  733 */           ConditionalOrderEntryPanel.access$2102(ConditionalOrderEntryPanel.this, StopDirection.BID_EQUALS);
/*  734 */           ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton.setText("L_BE");
/*  735 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.takeProfitPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.TAKE_PROFIT));
/*  736 */           ConditionalOrderEntryPanel.this.drawOrderTakeProfitLine();
/*      */         }
/*  738 */         ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */       }
/*      */     });
/*  741 */     group.add(item);
/*  742 */     item.setName("SD_B_E");
/*  743 */     this.conditionsPopup.add(item);
/*      */ 
/*  746 */     item = new JLocalizableMenuItem("SD_B_E_MIT");
/*  747 */     item.setAction(new AbstractAction("SD_B_E_MIT") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  749 */         JButton button = (JButton)ConditionalOrderEntryPanel.this.conditionsPopup.getInvoker();
/*  750 */         if (ConditionalOrderEntryPanel.this.entryStopDirectionButton == button) {
/*  751 */           ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.BID_EQUALS);
/*  752 */           ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_BES");
/*  753 */           ConditionalOrderEntryPanel.this.enableSlippageForMIT();
/*  754 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.entryPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.OPEN_IF));
/*  755 */           ConditionalOrderEntryPanel.this.drawOrderEntryLine();
/*      */         }
/*  757 */         ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */       }
/*      */     });
/*  760 */     group.add(item);
/*  761 */     item.setName("SD_B_E_MIT");
/*  762 */     this.conditionsPopup.add(item);
/*      */ 
/*  765 */     item = new JLocalizableMenuItem("SD_A_E");
/*  766 */     item.setAction(new AbstractAction("SD_A_E") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  768 */         JButton button = (JButton)ConditionalOrderEntryPanel.this.conditionsPopup.getInvoker();
/*  769 */         if (ConditionalOrderEntryPanel.this.entryStopDirectionButton == button) {
/*  770 */           ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.ASK_EQUALS);
/*  771 */           ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_AE");
/*  772 */           ConditionalOrderEntryPanel.this.zeroSlippage();
/*  773 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.entryPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.OPEN_IF));
/*  774 */           ConditionalOrderEntryPanel.this.drawOrderEntryLine();
/*  775 */         } else if (ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton == button) {
/*  776 */           ConditionalOrderEntryPanel.access$2102(ConditionalOrderEntryPanel.this, StopDirection.ASK_EQUALS);
/*  777 */           ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton.setText("L_AE");
/*  778 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.takeProfitPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.TAKE_PROFIT));
/*  779 */           ConditionalOrderEntryPanel.this.drawOrderTakeProfitLine();
/*      */         }
/*  781 */         ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */       }
/*      */     });
/*  784 */     group.add(item);
/*  785 */     item.setName("SD_A_E");
/*  786 */     this.conditionsPopup.add(item);
/*      */ 
/*  789 */     item = new JLocalizableMenuItem("SD_A_E_MIT");
/*  790 */     item.setAction(new AbstractAction("SD_A_E_MIT") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  792 */         JButton button = (JButton)ConditionalOrderEntryPanel.this.conditionsPopup.getInvoker();
/*  793 */         if (ConditionalOrderEntryPanel.this.entryStopDirectionButton == button) {
/*  794 */           ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, StopDirection.ASK_EQUALS);
/*  795 */           ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_AES");
/*  796 */           ConditionalOrderEntryPanel.this.enableSlippageForMIT();
/*  797 */           ConditionalOrderEntryPanel.this.populateField(ConditionalOrderEntryPanel.this.entryPriceField, ConditionalOrderEntryPanel.access$1800(ConditionalOrderEntryPanel.this, StopOrderType.OPEN_IF));
/*  798 */           ConditionalOrderEntryPanel.this.drawOrderEntryLine();
/*      */         }
/*  800 */         ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */       }
/*      */     });
/*  803 */     group.add(item);
/*  804 */     item.setName("SD_A_E_MIT");
/*  805 */     this.conditionsPopup.add(item);
/*      */ 
/*  807 */     this.conditionsPopup.addPopupMenuListener(new PopupMenuListener() {
/*      */       public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
/*  809 */         if ((ConditionalOrderEntryPanel.this.entryCheckBox.isSelected()) && (null != ConditionalOrderEntryPanel.this.entryStopDirection)) ConditionalOrderEntryPanel.this.entryPriceField.setEnabled(true); 
/*      */       }
/*      */ 
/*      */       public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
/*      */       }
/*      */ 
/*      */       public void popupMenuCanceled(PopupMenuEvent e) {
/*  815 */         if (null == ConditionalOrderEntryPanel.this.entryStopDirection) ConditionalOrderEntryPanel.this.entryPriceField.setEnabled(false);
/*      */ 
/*  817 */         ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */       }
/*      */     });
/*  821 */     MouseAdapter stopButtonMouseAdapter = new MouseAdapter() {
/*  822 */       public void mouseClicked(MouseEvent mouseEvent) { maybeShowPopup(mouseEvent); } 
/*      */       private void maybeShowPopup(MouseEvent mouseEvent) {
/*  824 */         JButton button = (JButton)mouseEvent.getComponent();
/*  825 */         if (!button.isEnabled()) return;
/*  826 */         ConditionalOrderEntryPanel.this.enablePopupMenuItems("", true);
/*  827 */         if (button.equals(ConditionalOrderEntryPanel.this.stopLossStopDirectionButton)) {
/*  828 */           if (ConditionalOrderEntryPanel.this.sideComboBox.getSelectedIndex() == 0) {
/*  829 */             String[] dirs = { "SD_B_G", "SD_A_G", "SD_A_E", "SD_B_E", "SD_MK", "SD_A_E_MIT", "SD_B_E_MIT" };
/*  830 */             ConditionalOrderEntryPanel.this.enablePopupMenuItems(dirs, false);
/*      */           } else {
/*  832 */             String[] dirs = { "SD_B_L", "SD_A_L", "SD_A_E", "SD_B_E", "SD_MK", "SD_A_E_MIT", "SD_B_E_MIT" };
/*  833 */             ConditionalOrderEntryPanel.this.enablePopupMenuItems(dirs, false);
/*      */           }
/*      */         }
/*  836 */         else if (button.equals(ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton)) {
/*  837 */           if (ConditionalOrderEntryPanel.this.sideComboBox.getSelectedIndex() == 0) {
/*  838 */             String[] dirs = { "SD_B_L", "SD_A_L", "SD_A_E", "SD_A_G", "SD_B_G", "SD_MK", "SD_A_E_MIT", "SD_B_E_MIT" };
/*  839 */             ConditionalOrderEntryPanel.this.enablePopupMenuItems(dirs, false);
/*      */           } else {
/*  841 */             String[] dirs = { "SD_B_G", "SD_A_G", "SD_B_E", "SD_B_L", "SD_A_L", "SD_MK", "SD_A_E_MIT", "SD_B_E_MIT" };
/*  842 */             ConditionalOrderEntryPanel.this.enablePopupMenuItems(dirs, false);
/*      */           }
/*      */         }
/*  845 */         else if (button.equals(ConditionalOrderEntryPanel.this.entryStopDirectionButton)) {
/*  846 */           ConditionalOrderEntryPanel.this.buildEntryConditions();
/*      */         }
/*  848 */         ConditionalOrderEntryPanel.this.conditionsPopup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
/*      */       }
/*      */     };
/*  852 */     this.entryStopDirectionButton.addMouseListener(stopButtonMouseAdapter);
/*  853 */     this.stopLossStopDirectionButton.addMouseListener(stopButtonMouseAdapter);
/*  854 */     this.takeProfitStopDirectionButton.addMouseListener(stopButtonMouseAdapter);
/*      */   }
/*      */ 
/*      */   public void clearEverything(boolean saveSide)
/*      */   {
/*  859 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/*      */ 
/*  861 */     if ((!saveSide) && (!this.orderSideLocked)) {
/*  862 */       this.sideComboBox.setSelectedIndex(0);
/*      */     }
/*      */ 
/*  865 */     if (!this.entryLocked) {
/*  866 */       this.entryCheckBox.setSelected(false);
/*  867 */       this.entryPriceField.clear();
/*  868 */       this.entryPriceField.setEnabled(false);
/*      */     }
/*  870 */     this.stopLossCheckBox.setSelected(false);
/*  871 */     this.stopLossPriceField.clear();
/*  872 */     this.stopLossPriceField.setEnabled(false);
/*  873 */     this.takeProfitCheckBox.setSelected(false);
/*  874 */     this.takeProfitPriceField.clear();
/*  875 */     this.takeProfitPriceField.setEnabled(false);
/*  876 */     this.slippageCheckBox.setSelected(false);
/*      */ 
/*  878 */     setSlippage(this.storage.restoreDefaultSlippageAsText());
/*      */ 
/*  880 */     if (!this.entryLocked) {
/*  881 */       this.entryStopDirectionButton.setText("L_MK");
/*      */     }
/*  883 */     setDefaultStopConditionLabels(true);
/*      */ 
/*  885 */     this.submitButton.setEnabled(false);
/*  886 */     if (!this.entryLocked) {
/*  887 */       this.entryStopDirectionButton.setEnabled(false);
/*      */     }
/*  889 */     this.takeProfitStopDirectionButton.setEnabled(false);
/*  890 */     this.stopLossStopDirectionButton.setEnabled(false);
/*      */ 
/*  892 */     setSlippage();
/*  893 */     this.slippageSpinner.setEnabled(false);
/*      */ 
/*  895 */     removeOrderEntryLine();
/*  896 */     removeOrderStopLossLine();
/*  897 */     removeOrderTakeProfitLine();
/*      */ 
/*  899 */     prepareContestSituation();
/*      */ 
/*  901 */     if (GreedContext.isGlobalExtended()) {
/*  902 */       this.ifDonePanel.clearEverything();
/*  903 */       this.ifDonePanel.setEnabled(false);
/*  904 */       this.timeLimPanel.setEnabled(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void prepareContestSituation() {
/*  909 */     if (GreedContext.isContest()) {
/*  910 */       this.takeProfitCheckBox.setEnabled(false);
/*  911 */       this.stopLossCheckBox.setEnabled(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void contestOnClickState()
/*      */   {
/*  917 */     if (GreedContext.isContest())
/*      */     {
/*  919 */       this.stopLossCheckBox.setSelected(true);
/*  920 */       this.takeProfitCheckBox.setSelected(true);
/*      */ 
/*  922 */       this.stopLossPriceField.setEnabled(true);
/*  923 */       this.takeProfitPriceField.setEnabled(true);
/*      */ 
/*  925 */       this.stopLossStopDirectionButton.setEnabled(true);
/*  926 */       this.takeProfitStopDirectionButton.setEnabled(true);
/*      */ 
/*  928 */       populateField(this.stopLossPriceField, defaultPriceAdjustment(StopOrderType.STOP_LOSS));
/*  929 */       populateField(this.takeProfitPriceField, defaultPriceAdjustment(StopOrderType.TAKE_PROFIT));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDefaultStopConditionLabels(boolean resetPriceFields)
/*      */   {
/*  937 */     if ((resetPriceFields) && (this.stopLossCheckBox.isSelected())) {
/*  938 */       this.stopLossCheckBox.doClick();
/*      */     }
/*  940 */     if ((resetPriceFields) && (this.takeProfitCheckBox.isSelected())) {
/*  941 */       this.takeProfitCheckBox.doClick();
/*      */     }
/*  943 */     if (this.sideComboBox.getSelectedIndex() == 0) {
/*  944 */       this.stopLossStopDirectionButton.setText("L_BL");
/*  945 */       this.stopLossStopDirection = StopDirection.BID_LESS;
/*  946 */       this.takeProfitStopDirectionButton.setText("L_BE");
/*  947 */       this.takeProfitStopDirection = StopDirection.BID_EQUALS;
/*      */     } else {
/*  949 */       this.stopLossStopDirectionButton.setText("L_AG");
/*  950 */       this.stopLossStopDirection = StopDirection.ASK_GREATER;
/*  951 */       this.takeProfitStopDirectionButton.setText("L_AE");
/*  952 */       this.takeProfitStopDirection = StopDirection.ASK_EQUALS;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doEnableDefaultTPandSL()
/*      */   {
/*  958 */     if (this.entryCheckBox.isSelected()) {
/*  959 */       if (this.storage.restoreApplyStopLossToAllMarketOrders()) {
/*  960 */         this.stopLossCheckBox.doClick();
/*      */       }
/*  962 */       if (this.storage.restoreApplyTakeProfitToAllMarketOrders())
/*  963 */         this.takeProfitCheckBox.doClick();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setSlippage()
/*      */   {
/*  969 */     setSlippage(this.storage.restoreDefaultSlippage().toString());
/*      */   }
/*      */ 
/*      */   public void setSlippage(String slippage)
/*      */     throws NumberFormatException
/*      */   {
/*  981 */     if ((this.entryCheckBox.isSelected()) && ((this.entryStopDirectionButton.getTextKey().equals("L_AE")) || (this.entryStopDirectionButton.getTextKey().equals("L_BE")))) {
/*  982 */       this.slippageCheckBox.setEnabled(false);
/*  983 */       this.slippageCheckBox.setSelected(false);
/*  984 */       this.slippageSpinner.clear();
/*      */     }
/*  986 */     else if ((this.entryCheckBox.isSelected()) && ((this.entryStopDirectionButton.getTextKey().equals("SD_A_E_MIT")) || (this.entryStopDirectionButton.getTextKey().equals("SD_B_E_MIT")))) {
/*  987 */       this.slippageCheckBox.setEnabled(false);
/*  988 */       this.slippageCheckBox.setSelected(true);
/*  989 */     } else if (this.storage.restoreApplySlippageToAllMarketOrders())
/*      */     {
/*  991 */       this.slippageSpinner.setValue(new BigDecimal(slippage));
/*  992 */       this.slippageCheckBox.setSelected(true);
/*      */ 
/*  994 */       if (!this.entryCheckBox.isSelected())
/*  995 */         this.slippageCheckBox.setEnabled(false);
/*      */     }
/*      */     else
/*      */     {
/*  999 */       this.slippageCheckBox.setEnabled(true);
/* 1000 */       this.slippageCheckBox.setSelected(false);
/* 1001 */       this.slippageSpinner.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isExpanded()
/*      */   {
/* 1008 */     return this.conditionalOrdersPanel.isVisible();
/*      */   }
/*      */ 
/*      */   public JPanel getCondPanel() {
/* 1012 */     return this.conditionalOrdersPanel;
/*      */   }
/*      */ 
/*      */   public void switchVisibility() {
/* 1016 */     this.conditionalOrdersPanel.setVisible(!this.conditionalOrdersPanel.isVisible());
/* 1017 */     this.border.setSwitch(this.conditionalOrdersPanel.isVisible());
/*      */ 
/* 1019 */     if ((getParent().getParent() instanceof MultiSplitPane)) {
/* 1020 */       ((MultiSplitPane)getParent().getParent()).switchVisibility("conOrderEntry");
/*      */     } else {
/* 1022 */       Component parent = getParent();
/* 1023 */       while ((null != parent) && 
/* 1024 */         (!(parent instanceof JFrame))) {
/* 1025 */         parent = parent.getParent();
/*      */       }
/* 1027 */       ((JFrame)parent).pack();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void zeroSlippage() {
/* 1032 */     this.slippageCheckBox.setSelected(false);
/* 1033 */     this.slippageCheckBox.setEnabled(false);
/* 1034 */     this.slippageSpinner.setValue(BigDecimal.ZERO);
/* 1035 */     this.slippageSpinner.setEnabled(false);
/*      */   }
/*      */ 
/*      */   private void enableSlippageForMIT()
/*      */   {
/* 1040 */     this.slippageCheckBox.setEnabled(false);
/* 1041 */     this.slippageCheckBox.setSelected(true);
/* 1042 */     this.slippageSpinner.setEnabled(true);
/* 1043 */     this.slippageSpinner.setValue(this.storage.restoreDefaultSlippage());
/*      */   }
/*      */ 
/*      */   private void disableSlippage() {
/* 1047 */     this.slippageCheckBox.setEnabled(false);
/* 1048 */     this.slippageCheckBox.setSelected(false);
/* 1049 */     this.slippageSpinner.setEnabled(false);
/* 1050 */     setSlippage();
/*      */   }
/*      */ 
/*      */   private void enablePopupMenuItems(String dir, boolean enable)
/*      */   {
/* 1060 */     for (int i = 0; i < this.conditionsPopup.getComponentCount(); i++) {
/* 1061 */       JLocalizableMenuItem item = (JLocalizableMenuItem)this.conditionsPopup.getComponent(i);
/* 1062 */       if ((dir.length() != 0) && (!item.getTextKey().equalsIgnoreCase(dir))) continue; item.setVisible(enable);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void enablePopupMenuItems(String[] dirs, boolean enable) {
/* 1067 */     List dirsAsList = Arrays.asList(dirs);
/* 1068 */     for (int i = 0; i < this.conditionsPopup.getComponentCount(); i++) {
/* 1069 */       JLocalizableMenuItem item = (JLocalizableMenuItem)this.conditionsPopup.getComponent(i);
/* 1070 */       if ((dirs.length != 0) && (dirsAsList.indexOf(item.getTextKey()) == -1)) continue; item.setVisible(enable);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void disablePopupMenuItems() {
/* 1075 */     for (int i = 0; i < this.conditionsPopup.getComponentCount(); i++) {
/* 1076 */       JLocalizableMenuItem item = (JLocalizableMenuItem)this.conditionsPopup.getComponent(i);
/* 1077 */       item.setVisible(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setupListeners()
/*      */   {
/* 1083 */     addCheckListener(this.entryCheckBox, this.entryPriceField, COPY_PRICE);
/* 1084 */     addCheckListener(this.stopLossCheckBox, this.stopLossPriceField, COPY_PRICE);
/* 1085 */     addCheckListener(this.takeProfitCheckBox, this.takeProfitPriceField, COPY_PRICE);
/*      */ 
/* 1087 */     addCheckListener(this.slippageCheckBox, this.slippageSpinner, new BigDecimal(this.storage.restoreDefaultSlippageAsText()));
/*      */ 
/* 1089 */     disableSlippage();
/*      */ 
/* 1091 */     addSubmitActionListener();
/*      */   }
/*      */ 
/*      */   private void addSubmitActionListener()
/*      */   {
/* 1100 */     this.submitButton.addKeyListener(new KeyListener() {
/* 1101 */       private boolean blockMultipleSubmits = false;
/*      */ 
/*      */       public void keyTyped(KeyEvent e)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void keyPressed(KeyEvent e) {
/* 1108 */         int key = e.getKeyCode();
/*      */ 
/* 1110 */         if ((ConditionalOrderEntryPanel.this.submitButton.isEnabled()) && 
/* 1111 */           (key == 10) && 
/* 1112 */           (!this.blockMultipleSubmits)) {
/* 1113 */           ConditionalOrderEntryPanel.this.doSubmit();
/* 1114 */           this.blockMultipleSubmits = true;
/*      */         }
/*      */       }
/*      */ 
/*      */       public void keyReleased(KeyEvent e)
/*      */       {
/* 1122 */         this.blockMultipleSubmits = false;
/*      */       }
/*      */     });
/* 1125 */     this.submitButton.addMouseListener(new MouseAdapter()
/*      */     {
/*      */       public void mouseClicked(MouseEvent e) {
/* 1128 */         if (ConditionalOrderEntryPanel.this.submitButton.isEnabled())
/* 1129 */           ConditionalOrderEntryPanel.this.doSubmit();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setSubmitActionListener(SubmitActionListener listener) {
/* 1136 */     this.submitActionListener = listener;
/*      */   }
/*      */ 
/*      */   private void showErrorMessagePanel(String errorMessageKey) {
/* 1140 */     JLocalizableDialog previewContent = new JLocalizableDialog(errorMessageKey)
/*      */     {
/*      */     };
/* 1189 */     previewContent.setTitle("dialog.validation");
/* 1190 */     previewContent.setVisible(true);
/*      */   }
/*      */ 
/*      */   public void doSubmit() {
/* 1194 */     if ((this.slippageCheckBox.isSelected()) && (!this.slippageSpinner.validateEditor()) && (this.timeLimPanel != null) && (!this.timeLimPanel.isTimeValid()))
/*      */     {
/* 1196 */       return;
/*      */     }
/*      */ 
/* 1199 */     if ((GreedContext.isContest()) && (OrderUtils.isOpenedPosOrOrdersForInstrument(this.instrument))) {
/* 1200 */       showErrorMessagePanel("validation.one.instrument.message");
/* 1201 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1205 */       OrderGroupMessage orderGroup = new OrderGroupMessage();
/* 1206 */       List orders = new ArrayList();
/*      */ 
/* 1208 */       orderGroup.setTimestamp(new Date());
/* 1209 */       orderGroup.setInstrument(this.instrument.toString());
/*      */ 
/* 1212 */       OrderMessage openingOrder = new OrderMessage();
/*      */ 
/* 1214 */       BigDecimal entry = getEntryPrice();
/*      */ 
/* 1216 */       if (entry != null) {
/* 1217 */         openingOrder.setPriceStop(new Money(entry, this.instrument.getSecondaryCurrency()));
/*      */       }
/*      */ 
/* 1220 */       openingOrder.setOrderGroupId(orderGroup.getOrderGroupId());
/* 1221 */       openingOrder.setInstrument(this.instrument.toString());
/* 1222 */       openingOrder.setOrderDirection(OrderDirection.OPEN);
/* 1223 */       OrderSide side = getOrderSide();
/* 1224 */       openingOrder.setSide(side);
/*      */ 
/* 1226 */       StopDirection entryStopDirection = getEntryStopDirection();
/* 1227 */       BigDecimal trailingLimit = null;
/* 1228 */       BigDecimal slippage = getSlippage();
/* 1229 */       if (slippage != null) {
/* 1230 */         trailingLimit = slippage.multiply(BigDecimal.valueOf(this.instrument.getPipValue()));
/* 1231 */         openingOrder.setPriceTrailingLimit(new Money(trailingLimit, this.instrument.getSecondaryCurrency()));
/*      */       }
/*      */ 
/* 1234 */       if (((StopDirection.ASK_EQUALS.equals(entryStopDirection)) || (StopDirection.BID_EQUALS.equals(entryStopDirection))) && (openingOrder.getPriceStop().getValue() != null))
/*      */       {
/* 1236 */         if ((slippage != null) && (slippage.compareTo(BigDecimal.ZERO) > 0) && (trailingLimit != null))
/* 1237 */           openingOrder.makeMIT(openingOrder.getPriceStop().getValue(), trailingLimit, entryStopDirection);
/*      */         else
/* 1239 */           openingOrder.makeLimit(openingOrder.getPriceStop().getValue(), entryStopDirection);
/*      */       }
/* 1241 */       else if (entryStopDirection != null) {
/* 1242 */         openingOrder.setStopDirection(entryStopDirection);
/*      */       }
/*      */ 
/* 1246 */       String tagForOrder = null;
/* 1247 */       if (openingOrder.getExternalSysId() == null) {
/* 1248 */         tagForOrder = DDSAgent.generateLabel(openingOrder);
/* 1249 */         openingOrder.setExternalSysId(tagForOrder);
/*      */       }
/* 1251 */       if (this.timeLimPanel != null) {
/* 1252 */         Long execTimeout = this.timeLimPanel.getTimeValue();
/* 1253 */         if (execTimeout != null) {
/* 1254 */           openingOrder.setExecTimeoutMillis(execTimeout);
/*      */         }
/*      */       }
/* 1257 */       orders.add(openingOrder);
/*      */ 
/* 1260 */       BigDecimal stopLoss = getStopLossPrice();
/* 1261 */       BigDecimal takeProfit = getTakeProfitPrice();
/*      */ 
/* 1263 */       if (GreedContext.isGlobalExtended()) {
/* 1264 */         openingOrder.setIfdType("ifdm");
/*      */ 
/* 1266 */         OrderSide ifDoneSide = side == OrderSide.BUY ? OrderSide.SELL : OrderSide.BUY;
/*      */ 
/* 1268 */         StopDirection stopStopDirection = this.ifDonePanel.getStopDirection();
/* 1269 */         StopDirection limitStopDirection = this.ifDonePanel.getLimitDirection();
/*      */ 
/* 1271 */         BigDecimal slippagePips = this.ifDonePanel.getStopSlippage();
/*      */ 
/* 1273 */         if (this.ifDonePanel.isStopSelected()) {
/* 1274 */           OrderMessage ifDoneStopOrder = new OrderMessage();
/* 1275 */           ifDoneStopOrder.setInstrument(this.instrument.toString());
/* 1276 */           ifDoneStopOrder.setIfdType("ifds");
/* 1277 */           ifDoneStopOrder.setOrderDirection(OrderDirection.OPEN);
/* 1278 */           ifDoneStopOrder.setSide(ifDoneSide);
/* 1279 */           ifDoneStopOrder.setOrderGroupId(orderGroup.getOrderGroupId());
/* 1280 */           ifDoneStopOrder.setPriceStop(new Money(this.ifDonePanel.getStopPrice(), this.instrument.getSecondaryCurrency()));
/*      */ 
/* 1282 */           if (slippagePips != null) {
/* 1283 */             BigDecimal stopTrailingLimit = slippagePips.multiply(BigDecimal.valueOf(this.instrument.getPipValue()));
/* 1284 */             ifDoneStopOrder.setPriceTrailingLimit(new Money(stopTrailingLimit, this.instrument.getSecondaryCurrency()));
/*      */           }
/* 1286 */           ifDoneStopOrder.setStopDirection(stopStopDirection);
/* 1287 */           ifDoneStopOrder.setExternalSysId(tagForOrder);
/* 1288 */           orders.add(ifDoneStopOrder);
/*      */         }
/*      */ 
/* 1291 */         if (this.ifDonePanel.isLimitSelected()) {
/* 1292 */           OrderMessage ifDoneLimitOrder = new OrderMessage();
/* 1293 */           ifDoneLimitOrder.setIfdType("ifds");
/* 1294 */           ifDoneLimitOrder.setInstrument(this.instrument.toString());
/* 1295 */           ifDoneLimitOrder.setOrderDirection(OrderDirection.OPEN);
/* 1296 */           ifDoneLimitOrder.setSide(ifDoneSide);
/* 1297 */           ifDoneLimitOrder.setOrderGroupId(orderGroup.getOrderGroupId());
/* 1298 */           ifDoneLimitOrder.setPriceStop(new Money(this.ifDonePanel.getLimitPrice(), this.instrument.getSecondaryCurrency()));
/* 1299 */           ifDoneLimitOrder.makeLimit(ifDoneLimitOrder.getPriceStop().getValue(), limitStopDirection);
/* 1300 */           ifDoneLimitOrder.setExternalSysId(tagForOrder);
/* 1301 */           orders.add(ifDoneLimitOrder);
/*      */         }
/*      */       }
/*      */       else {
/* 1305 */         OrderSide closeSide = side == OrderSide.BUY ? OrderSide.SELL : OrderSide.BUY;
/* 1306 */         StopDirection stopLossStopDirection = getStopLossStopDirection();
/* 1307 */         StopDirection takeProfitStopDirection = getTakeProfitStopDirection();
/*      */ 
/* 1315 */         if (stopLoss != null) {
/* 1316 */           OrderMessage closingOrder = new OrderMessage();
/* 1317 */           closingOrder.setInstrument(this.instrument.toString());
/* 1318 */           closingOrder.setOrderDirection(OrderDirection.CLOSE);
/* 1319 */           closingOrder.setSide(closeSide);
/* 1320 */           closingOrder.setOrderGroupId(orderGroup.getOrderGroupId());
/* 1321 */           closingOrder.setPriceStop(new Money(stopLoss, this.instrument.getSecondaryCurrency()));
/*      */ 
/* 1323 */           if (null == stopLossStopDirection)
/* 1324 */             closingOrder.setStopDirection(side == OrderSide.BUY ? StopDirection.BID_LESS : StopDirection.ASK_GREATER);
/*      */           else {
/* 1326 */             closingOrder.setStopDirection(stopLossStopDirection);
/*      */           }
/* 1328 */           closingOrder.setExternalSysId(tagForOrder);
/*      */ 
/* 1330 */           orders.add(closingOrder);
/*      */         }
/*      */ 
/* 1333 */         if (takeProfit != null) {
/* 1334 */           OrderMessage closingOrder = new OrderMessage();
/* 1335 */           closingOrder.setInstrument(this.instrument.toString());
/* 1336 */           closingOrder.setOrderDirection(OrderDirection.CLOSE);
/* 1337 */           closingOrder.setSide(closeSide);
/* 1338 */           closingOrder.setOrderGroupId(orderGroup.getOrderGroupId());
/* 1339 */           closingOrder.setPriceStop(new Money(takeProfit, this.instrument.getSecondaryCurrency()));
/*      */ 
/* 1341 */           if (null == takeProfitStopDirection) {
/* 1342 */             closingOrder.setStopDirection(side == OrderSide.BUY ? StopDirection.BID_GREATER : StopDirection.ASK_LESS);
/*      */           }
/* 1344 */           else if (takeProfitStopDirection.equals(StopDirection.ASK_EQUALS)) {
/* 1345 */             closingOrder.setStopDirection(StopDirection.ASK_LESS);
/* 1346 */             closingOrder.setPriceTrailingLimit(new Money(BigDecimal.ZERO, this.instrument.getSecondaryCurrency()));
/* 1347 */           } else if (takeProfitStopDirection.equals(StopDirection.BID_EQUALS)) {
/* 1348 */             closingOrder.setStopDirection(StopDirection.BID_GREATER);
/* 1349 */             closingOrder.setPriceTrailingLimit(new Money(BigDecimal.ZERO, this.instrument.getSecondaryCurrency()));
/*      */           } else {
/* 1351 */             closingOrder.setStopDirection(takeProfitStopDirection);
/*      */           }
/*      */ 
/* 1354 */           closingOrder.setExternalSysId(tagForOrder);
/* 1355 */           orders.add(closingOrder);
/*      */         }
/*      */       }
/*      */ 
/* 1359 */       orderGroup.setOrders(orders);
/*      */ 
/* 1361 */       if ((!GreedContext.isContest()) || ((takeProfit != null) && (stopLoss != null) && (InstrumentAvailabilityManager.getInstance().isAllowed(orderGroup.getInstrument()))))
/*      */       {
/* 1365 */         this.submitActionListener.submitButtonPressed(orderGroup);
/*      */       }
/*      */     }
/*      */     catch (NumberFormatException ex)
/*      */     {
/* 1370 */       LOGGER.warn(new StringBuilder().append("parsing: ").append(ex.getMessage()).toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addCheckListener(JCheckBox check, JComponent field, BigDecimal spinnerValue)
/*      */   {
/* 1383 */     check.addActionListener(new ActionListener(check, spinnerValue, field)
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1386 */         BigDecimal value = this.val$check == ConditionalOrderEntryPanel.this.slippageCheckBox ? ConditionalOrderEntryPanel.this.storage.restoreDefaultSlippage() : this.val$spinnerValue;
/*      */ 
/* 1388 */         if (this.val$check.isSelected()) {
/* 1389 */           if (this.val$check == ConditionalOrderEntryPanel.this.entryCheckBox) {
/* 1390 */             ConditionalOrderEntryPanel.this.enablePopupMenuItems("", true);
/* 1391 */             ConditionalOrderEntryPanel.this.buildEntryConditions();
/* 1392 */             ConditionalOrderEntryPanel.this.conditionsPopup.show(ConditionalOrderEntryPanel.this.entryStopDirectionButton, 0, 0);
/*      */ 
/* 1394 */             BigDecimal priceAdjusted = ConditionalOrderEntryPanel.this.defaultPriceAdjustment(StopOrderType.OPEN_IF);
/* 1395 */             ConditionalOrderEntryPanel.this.populateField(this.val$field, priceAdjusted);
/* 1396 */             if (GreedContext.isGlobalExtended()) {
/* 1397 */               ConditionalOrderEntryPanel.this.ifDonePanel.setEnabled(true);
/* 1398 */               ConditionalOrderEntryPanel.this.timeLimPanel.setEnabled(true);
/*      */             }
/*      */           } else {
/* 1401 */             BigDecimal priceAdjusted = null;
/* 1402 */             if (this.val$check == ConditionalOrderEntryPanel.this.stopLossCheckBox)
/* 1403 */               priceAdjusted = ConditionalOrderEntryPanel.this.defaultPriceAdjustment(StopOrderType.STOP_LOSS);
/* 1404 */             else if (this.val$check == ConditionalOrderEntryPanel.this.takeProfitCheckBox) {
/* 1405 */               priceAdjusted = ConditionalOrderEntryPanel.this.defaultPriceAdjustment(StopOrderType.TAKE_PROFIT);
/*      */             }
/*      */ 
/* 1408 */             this.val$field.setEnabled(true);
/* 1409 */             this.val$field.requestFocusInWindow();
/* 1410 */             if (value != null) {
/* 1411 */               if ((this.val$field instanceof PriceSpinner)) {
/* 1412 */                 if (value.equals(ConditionalOrderEntryPanel.COPY_PRICE))
/* 1413 */                   ConditionalOrderEntryPanel.this.populateField(this.val$field, priceAdjusted);
/*      */                 else {
/* 1415 */                   ConditionalOrderEntryPanel.this.populateField(this.val$field, value);
/*      */                 }
/*      */               }
/* 1418 */               else if (value.equals(ConditionalOrderEntryPanel.COPY_PRICE))
/* 1419 */                 ConditionalOrderEntryPanel.this.populateField(this.val$field, priceAdjusted);
/*      */               else {
/* 1421 */                 ConditionalOrderEntryPanel.this.populateField(this.val$field, value);
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 1426 */           if ((!ConditionalOrderEntryPanel.this.entryCheckBox.isSelected()) && ((ConditionalOrderEntryPanel.this.stopLossCheckBox == this.val$check) || (ConditionalOrderEntryPanel.this.takeProfitCheckBox == this.val$check) || (ConditionalOrderEntryPanel.this.slippageCheckBox == this.val$check)))
/*      */           {
/* 1428 */             ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_MK");
/* 1429 */             ConditionalOrderEntryPanel.this.contestOnClickState();
/*      */           }
/* 1431 */           if (!ConditionalOrderEntryPanel.this.entryLocked) {
/* 1432 */             ConditionalOrderEntryPanel.this.entryCheckBox.setSelected((ConditionalOrderEntryPanel.this.slippageCheckBox.isSelected()) || (ConditionalOrderEntryPanel.this.takeProfitCheckBox.isSelected()) || (ConditionalOrderEntryPanel.this.stopLossCheckBox.isSelected()) || (ConditionalOrderEntryPanel.this.entryCheckBox.isSelected()));
/*      */           }
/*      */ 
/* 1437 */           if (this.val$check == ConditionalOrderEntryPanel.this.takeProfitCheckBox)
/* 1438 */             ConditionalOrderEntryPanel.this.drawOrderTakeProfitLine();
/* 1439 */           else if (this.val$check == ConditionalOrderEntryPanel.this.stopLossCheckBox)
/* 1440 */             ConditionalOrderEntryPanel.this.drawOrderStopLossLine();
/*      */         }
/*      */         else
/*      */         {
/* 1444 */           this.val$field.setEnabled(false);
/*      */ 
/* 1446 */           if (this.val$check == ConditionalOrderEntryPanel.this.slippageCheckBox)
/*      */           {
/* 1448 */             if (ConditionalOrderEntryPanel.this.storage.restoreApplySlippageToAllMarketOrders()) {
/* 1449 */               if (!ConditionalOrderEntryPanel.this.entryCheckBox.isSelected()) {
/* 1450 */                 ConditionalOrderEntryPanel.this.slippageSpinner.setValue(ConditionalOrderEntryPanel.this.storage.restoreDefaultSlippage());
/*      */               } else {
/* 1452 */                 ConditionalOrderEntryPanel.this.slippageSpinner.clear();
/* 1453 */                 if (("L_MK".equals(ConditionalOrderEntryPanel.this.entryStopDirectionButton.getTextKey())) && (ConditionalOrderEntryPanel.this.entryCheckBox.isSelected()))
/*      */                 {
/* 1455 */                   ConditionalOrderEntryPanel.this.slippageSpinner.setValue(GuiUtilsAndConstants.FIFE);
/*      */                 }
/*      */               }
/*      */             } else {
/* 1458 */               ConditionalOrderEntryPanel.this.slippageSpinner.clear();
/* 1459 */               if (("L_MK".equals(ConditionalOrderEntryPanel.this.entryStopDirectionButton.getTextKey())) && (ConditionalOrderEntryPanel.this.entryCheckBox.isSelected()))
/*      */               {
/* 1461 */                 ConditionalOrderEntryPanel.this.slippageSpinner.setValue(GuiUtilsAndConstants.FIFE);
/*      */               }
/*      */             }
/* 1464 */           } else if ((this.val$field instanceof PriceAmountTextField))
/* 1465 */             ((PriceAmountTextField)this.val$field).clear();
/* 1466 */           else if ((this.val$field instanceof PriceSpinner))
/* 1467 */             ((PriceSpinner)this.val$field).clear();
/*      */           else {
/* 1469 */             ((JTextField)this.val$field).setText("");
/*      */           }
/*      */ 
/* 1472 */           if (this.val$check == ConditionalOrderEntryPanel.this.entryCheckBox) {
/* 1473 */             if (!ConditionalOrderEntryPanel.this.entryLocked) {
/* 1474 */               ConditionalOrderEntryPanel.this.entryStopDirectionButton.setEnabled(ConditionalOrderEntryPanel.this.entryCheckBox.isSelected());
/* 1475 */               ConditionalOrderEntryPanel.this.entryStopDirectionButton.setText("L_MK");
/* 1476 */               ConditionalOrderEntryPanel.access$502(ConditionalOrderEntryPanel.this, null);
/*      */             }
/*      */ 
/* 1479 */             ConditionalOrderEntryPanel.this.slippageCheckBox.setEnabled(true);
/* 1480 */             ConditionalOrderEntryPanel.this.slippageSpinner.clear();
/*      */ 
/* 1482 */             if (ConditionalOrderEntryPanel.this.slippageCheckBox.isSelected()) {
/* 1483 */               ConditionalOrderEntryPanel.this.slippageCheckBox.doClick();
/*      */             }
/* 1485 */             if (ConditionalOrderEntryPanel.this.takeProfitCheckBox.isSelected()) {
/* 1486 */               ConditionalOrderEntryPanel.this.takeProfitCheckBox.doClick();
/*      */             }
/* 1488 */             if (ConditionalOrderEntryPanel.this.stopLossCheckBox.isSelected()) {
/* 1489 */               ConditionalOrderEntryPanel.this.stopLossCheckBox.doClick();
/*      */             }
/* 1491 */             ConditionalOrderEntryPanel.this.removeOrderEntryLine();
/* 1492 */             if (GreedContext.isGlobalExtended()) {
/* 1493 */               ConditionalOrderEntryPanel.this.ifDonePanel.setEnabled(false);
/* 1494 */               ConditionalOrderEntryPanel.this.timeLimPanel.setEnabled(false);
/*      */             }
/*      */           }
/* 1497 */           else if (this.val$check == ConditionalOrderEntryPanel.this.takeProfitCheckBox) {
/* 1498 */             ConditionalOrderEntryPanel.this.removeOrderTakeProfitLine();
/* 1499 */           } else if (this.val$check == ConditionalOrderEntryPanel.this.stopLossCheckBox) {
/* 1500 */             ConditionalOrderEntryPanel.this.removeOrderStopLossLine();
/*      */           }
/*      */ 
/* 1503 */           if (GreedContext.isContest()) {
/* 1504 */             ConditionalOrderEntryPanel.this.clearEverything(true);
/*      */           }
/*      */         }
/*      */ 
/* 1508 */         ConditionalOrderEntryPanel.this.takeProfitStopDirectionButton.setEnabled(ConditionalOrderEntryPanel.this.takeProfitCheckBox.isSelected());
/* 1509 */         ConditionalOrderEntryPanel.this.stopLossStopDirectionButton.setEnabled(ConditionalOrderEntryPanel.this.stopLossCheckBox.isSelected());
/* 1510 */         if (!ConditionalOrderEntryPanel.this.entryLocked) {
/* 1511 */           ConditionalOrderEntryPanel.this.entryStopDirectionButton.setEnabled(ConditionalOrderEntryPanel.this.entryCheckBox.isSelected());
/*      */         }
/*      */ 
/* 1514 */         ConditionalOrderEntryPanel.this.submitButton.setEnabled((ConditionalOrderEntryPanel.this.entryCheckBox.isSelected()) || (ConditionalOrderEntryPanel.this.slippageCheckBox.isSelected()) || (ConditionalOrderEntryPanel.this.takeProfitCheckBox.isSelected()) || (ConditionalOrderEntryPanel.this.stopLossCheckBox.isSelected()));
/*      */ 
/* 1516 */         if (!GreedContext.isContest()) {
/* 1517 */           ConditionalOrderEntryPanel.this.quoter.setTradable(!ConditionalOrderEntryPanel.this.isOrderInProgramming());
/* 1518 */           if (ConditionalOrderEntryPanel.this.quoter.isTradingPossible())
/* 1519 */             ConditionalOrderEntryPanel.this.quoter.setTradable(true);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private BigDecimal defaultPriceAdjustment(StopOrderType otype)
/*      */   {
/* 1528 */     OrderSide orderSide = this.sideComboBox.getSelectedIndex() == 0 ? OrderSide.BUY : OrderSide.SELL;
/* 1529 */     BigDecimal price = null;
/* 1530 */     BigDecimal pipDiff = null;
/*      */ 
/* 1532 */     CurrencyOffer askOffer = this.marketView.getBestOffer(this.instrument.toString(), OfferSide.ASK);
/* 1533 */     CurrencyOffer bidOffer = this.marketView.getBestOffer(this.instrument.toString(), OfferSide.BID);
/*      */ 
/* 1535 */     BigDecimal askPrice = askOffer.getPrice().getValue();
/* 1536 */     BigDecimal bidPrice = bidOffer.getPrice().getValue();
/*      */ 
/* 1538 */     if ((otype.equals(StopOrderType.OPEN_IF)) && (orderSide == OrderSide.BUY))
/*      */     {
/* 1540 */       if ((this.entryStopDirection != null) && (this.entryStopDirection.equals(StopDirection.ASK_EQUALS))) {
/* 1541 */         pipDiff = OrderUtils.NEGATIVE_ONE.multiply(this.storage.restoreDefaultOpenIfOffset());
/* 1542 */         price = askPrice;
/* 1543 */       } else if ((this.entryStopDirection != null) && (this.entryStopDirection.equals(StopDirection.ASK_GREATER))) {
/* 1544 */         pipDiff = this.storage.restoreDefaultOpenIfOffset();
/* 1545 */         price = askPrice;
/* 1546 */       } else if ((this.entryStopDirection != null) && (this.entryStopDirection.equals(StopDirection.BID_GREATER))) {
/* 1547 */         pipDiff = this.storage.restoreDefaultOpenIfOffset();
/* 1548 */         price = bidPrice;
/*      */       }
/* 1550 */     } else if ((otype.equals(StopOrderType.OPEN_IF)) && (orderSide == OrderSide.SELL)) {
/* 1551 */       if ((this.entryStopDirection != null) && (this.entryStopDirection.equals(StopDirection.BID_LESS))) {
/* 1552 */         pipDiff = OrderUtils.NEGATIVE_ONE.multiply(this.storage.restoreDefaultOpenIfOffset());
/* 1553 */         price = bidPrice;
/* 1554 */       } else if ((this.entryStopDirection != null) && (this.entryStopDirection.equals(StopDirection.ASK_LESS))) {
/* 1555 */         pipDiff = OrderUtils.NEGATIVE_ONE.multiply(this.storage.restoreDefaultOpenIfOffset());
/* 1556 */         price = askPrice;
/* 1557 */       } else if ((this.entryStopDirection != null) && (this.entryStopDirection.equals(StopDirection.BID_EQUALS))) {
/* 1558 */         pipDiff = this.storage.restoreDefaultOpenIfOffset();
/* 1559 */         price = bidPrice;
/*      */       }
/*      */     }
/*      */ 
/* 1563 */     if ((otype.equals(StopOrderType.STOP_LOSS)) && (orderSide == OrderSide.BUY)) {
/* 1564 */       if (this.stopLossStopDirection.equals(StopDirection.BID_LESS)) {
/* 1565 */         pipDiff = OrderUtils.NEGATIVE_ONE.multiply(this.storage.restoreDefaultStopLossOffset());
/* 1566 */         price = getBidPrice();
/* 1567 */       } else if (this.stopLossStopDirection.equals(StopDirection.ASK_LESS)) {
/* 1568 */         pipDiff = OrderUtils.NEGATIVE_ONE.multiply(this.storage.restoreDefaultStopLossOffset());
/* 1569 */         price = getAskPrice();
/*      */       }
/* 1571 */     } else if ((otype.equals(StopOrderType.STOP_LOSS)) && (orderSide == OrderSide.SELL)) {
/* 1572 */       if (this.stopLossStopDirection.equals(StopDirection.BID_GREATER)) {
/* 1573 */         pipDiff = this.storage.restoreDefaultStopLossOffset();
/* 1574 */         price = getBidPrice();
/* 1575 */       } else if (this.stopLossStopDirection.equals(StopDirection.ASK_GREATER)) {
/* 1576 */         pipDiff = this.storage.restoreDefaultStopLossOffset();
/* 1577 */         price = getAskPrice();
/*      */       }
/*      */     }
/*      */ 
/* 1581 */     if ((otype.equals(StopOrderType.TAKE_PROFIT)) && (orderSide == OrderSide.BUY)) {
/* 1582 */       price = getBidPrice();
/* 1583 */       pipDiff = this.storage.restoreDefaultTakeProfitOffset();
/* 1584 */     } else if ((otype.equals(StopOrderType.TAKE_PROFIT)) && (orderSide == OrderSide.SELL)) {
/* 1585 */       price = getAskPrice();
/* 1586 */       pipDiff = OrderUtils.NEGATIVE_ONE.multiply(this.storage.restoreDefaultTakeProfitOffset());
/*      */     }
/*      */ 
/* 1589 */     if ((price != null) && (pipDiff != null)) {
/* 1590 */       BigDecimal onePipValue = BigDecimal.valueOf(Instrument.fromString(getInstrument()).getPipValue());
/* 1591 */       price = price.add(onePipValue.multiply(pipDiff));
/*      */     }
/* 1593 */     return price;
/*      */   }
/*      */ 
/*      */   private BigDecimal getAskPrice() {
/* 1597 */     return getPrice(true);
/*      */   }
/*      */ 
/*      */   private BigDecimal getBidPrice() {
/* 1601 */     return getPrice(false);
/*      */   }
/*      */ 
/*      */   private BigDecimal getPrice(boolean isAskPrice) {
/* 1605 */     CurrencyOffer askOffer = this.marketView.getBestOffer(this.instrument.toString(), OfferSide.ASK);
/* 1606 */     CurrencyOffer bidOffer = this.marketView.getBestOffer(this.instrument.toString(), OfferSide.BID);
/*      */ 
/* 1608 */     BigDecimal askPrice = askOffer.getPrice().getValue();
/* 1609 */     BigDecimal bidPrice = bidOffer.getPrice().getValue();
/*      */ 
/* 1611 */     if ((!this.entryPriceField.isEnabled()) || (this.entryPriceField.getText() == null) || (this.entryPriceField.getText().equals(""))) {
/* 1612 */       if (isAskPrice) {
/* 1613 */         return askPrice;
/*      */       }
/* 1615 */       return bidPrice;
/*      */     }
/*      */ 
/* 1618 */     return new BigDecimal(this.entryPriceField.getText());
/*      */   }
/*      */ 
/*      */   private void populateField(JComponent field, BigDecimal value)
/*      */   {
/* 1623 */     if (value != null) {
/* 1624 */       field.setEnabled(true);
/* 1625 */       if ((field instanceof JTextField))
/* 1626 */         ((JTextField)field).setText(value.toPlainString());
/* 1627 */       else if ((field instanceof PriceSpinner))
/* 1628 */         ((PriceSpinner)field).setText(value.toPlainString());
/* 1629 */       else if ((field instanceof CommonJSpinner))
/* 1630 */         ((CommonJSpinner)field).setValue(value);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void buildEntryConditions()
/*      */   {
/* 1636 */     disablePopupMenuItems();
/* 1637 */     AccountInfoMessage accountState = ((AccountStatement)GreedContext.get("accountStatement")).getLastAccountState();
/* 1638 */     if ((null != accountState) && ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended()))) {
/* 1639 */       if (this.sideComboBox.getSelectedIndex() == 0)
/* 1640 */         enbleEntry4buy();
/*      */       else {
/* 1642 */         enableEntry4sell();
/*      */       }
/*      */     }
/* 1645 */     else if (this.sideComboBox.getSelectedIndex() == 0)
/* 1646 */       enbleEntry4buy();
/*      */     else
/* 1648 */       enableEntry4sell();
/*      */   }
/*      */ 
/*      */   private void enbleEntry4buy()
/*      */   {
/* 1654 */     enablePopupMenuItems("SD_MK", true);
/* 1655 */     enablePopupMenuItems("SD_B_G", true);
/* 1656 */     enablePopupMenuItems("SD_A_G", true);
/* 1657 */     enablePopupMenuItems("SD_A_E", true);
/* 1658 */     enablePopupMenuItems("SD_A_E_MIT", true);
/*      */   }
/*      */ 
/*      */   private void enableEntry4sell() {
/* 1662 */     enablePopupMenuItems("SD_MK", true);
/* 1663 */     enablePopupMenuItems("SD_B_L", true);
/* 1664 */     enablePopupMenuItems("SD_A_L", true);
/* 1665 */     enablePopupMenuItems("SD_B_E", true);
/* 1666 */     enablePopupMenuItems("SD_B_E_MIT", true);
/*      */   }
/*      */ 
/*      */   public void setOrderSide(OrderSide side)
/*      */   {
/* 1674 */     if (!this.orderSideLocked)
/* 1675 */       this.sideComboBox.setSelectedIndex(OrderSide.BUY == side ? 0 : 1);
/*      */   }
/*      */ 
/*      */   public void setHedged(boolean isHedged)
/*      */   {
/* 1680 */     this.stopLossCheckBox.setEnabled(isHedged);
/* 1681 */     this.stopLossCheckBox.setSelected(false);
/* 1682 */     this.stopLossStopDirectionButton.setEnabled(false);
/* 1683 */     this.stopLossPriceField.setEnabled(false);
/*      */ 
/* 1685 */     this.takeProfitCheckBox.setEnabled(isHedged);
/* 1686 */     this.takeProfitCheckBox.setSelected(false);
/* 1687 */     this.takeProfitStopDirectionButton.setEnabled(false);
/* 1688 */     this.takeProfitPriceField.setEnabled(false);
/*      */   }
/*      */ 
/*      */   public boolean isOrderInProgramming() {
/* 1692 */     return (this.entryCheckBox.isSelected()) || (this.stopLossCheckBox.isSelected()) || (this.takeProfitCheckBox.isSelected()) || ((this.slippageCheckBox.isSelected()) && (this.slippageCheckBox.isEnabled()));
/*      */   }
/*      */ 
/*      */   private void drawOrderEntryLine() {
/* 1696 */     String objectName = new StringBuilder().append("_order_entry_line_").append(this.lineId).append(this.instrument).toString();
/* 1697 */     if ((this.entryPriceField.isEnabled()) && (this.entryPriceField.getText() != null) && (!this.entryPriceField.getText().equals("")))
/*      */     {
/* 1700 */       BigDecimal entry = new BigDecimal(this.entryPriceField.getText());
/* 1701 */       drawLine(objectName, new StringBuilder().append("ENTRY").append(this.entryStopDirection == null ? "" : new StringBuilder().append(" ").append(this.entryStopDirectionButton.getText()).toString()).toString(), entry, this.entryPriceField);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void drawOrderTakeProfitLine() {
/* 1706 */     String objectName = new StringBuilder().append("_order_take_profit_line_").append(this.lineId).append(this.instrument).toString();
/* 1707 */     if ((this.takeProfitPriceField.getText() != null) && (!this.takeProfitPriceField.getText().equals(""))) {
/* 1708 */       BigDecimal takeProfit = new BigDecimal(this.takeProfitPriceField.getText());
/* 1709 */       drawLine(objectName, "TP", takeProfit, this.takeProfitPriceField);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void drawOrderStopLossLine() {
/* 1714 */     String objectName = new StringBuilder().append("_order_stop_loss_line_").append(this.lineId).append(this.instrument).toString();
/* 1715 */     if ((this.stopLossPriceField.getText() != null) && (!this.stopLossPriceField.getText().equals(""))) {
/* 1716 */       BigDecimal stopLoss = new BigDecimal(this.stopLossPriceField.getText());
/* 1717 */       drawLine(objectName, "SL", stopLoss, this.stopLossPriceField);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void drawLine(String objectName, String type, BigDecimal price, PriceSpinner priceField)
/*      */   {
/*      */     List chartObjects;
/* 1722 */     if ((GreedContext.isStrategyAllowed()) && (getDdsChartsController() != null)) {
/* 1723 */       Set charts = getDdsChartsController().getICharts(this.instrument);
/* 1724 */       chartObjects = new ArrayList();
/* 1725 */       for (IChart chart : charts) {
/* 1726 */         IChartObject hLine = chart.get(objectName);
/* 1727 */         if (hLine == null) {
/* 1728 */           hLine = chart.drawUnlocked(objectName, IChart.Type.PRICEMARKER, 0L, price.doubleValue());
/* 1729 */           if (hLine != null) {
/* 1730 */             hLine.setMenuEnabled(false);
/* 1731 */             Color color = Color.GRAY;
/* 1732 */             if (("SL".equals(type)) || ("TP".equals(type))) {
/* 1733 */               color = ((IChartWrapper)chart).getThemeColor(this.sideComboBox.getSelectedIndex() == 0 ? ITheme.ChartElement.ORDER_CLOSE_BUY : ITheme.ChartElement.ORDER_CLOSE_SELL);
/*      */             }
/* 1735 */             else if (type.startsWith("ENTRY")) {
/* 1736 */               color = ((IChartWrapper)chart).getThemeColor(this.sideComboBox.getSelectedIndex() == 0 ? ITheme.ChartElement.ORDER_OPEN_BUY : ITheme.ChartElement.ORDER_OPEN_SELL);
/*      */             }
/*      */ 
/* 1739 */             hLine.setColor(color);
/*      */ 
/* 1741 */             hLine.setText(type);
/* 1742 */             hLine.setSticky(false);
/* 1743 */             BasicStroke stroke = new BasicStroke(1.0F, 0, 0, 10.0F, new float[] { 2.0F, 4.0F }, 0.0F);
/* 1744 */             hLine.setStroke(stroke);
/* 1745 */             hLine.setChartObjectListener(new ChartObjectAdapter(priceField, chartObjects)
/*      */             {
/*      */               public void moved(ChartObjectEvent e) {
/* 1748 */                 double price = StratUtils.round05Pips(e.getNewDouble());
/* 1749 */                 this.val$priceField.setText(BigDecimal.valueOf(price).stripTrailingZeros().toPlainString());
/* 1750 */                 e.setNewDouble(price);
/*      */ 
/* 1755 */                 for (IChartObject chartObject : this.val$chartObjects) {
/* 1756 */                   chartObject.move(0L, price);
/*      */                 }
/* 1758 */                 ConditionalOrderEntryPanel.this.getDdsChartsController().refreshChartsContent();
/*      */               }
/*      */ 
/*      */               public void deleted(ChartObjectEvent e)
/*      */               {
/* 1763 */                 e.cancel();
/*      */               }
/*      */             });
/* 1766 */             chartObjects.add(hLine);
/*      */           }
/*      */         } else {
/* 1769 */           Color color = Color.GRAY;
/* 1770 */           if (("SL".equals(type)) || ("TP".equals(type))) {
/* 1771 */             color = ((IChartWrapper)chart).getThemeColor(this.sideComboBox.getSelectedIndex() == 0 ? ITheme.ChartElement.ORDER_CLOSE_BUY : ITheme.ChartElement.ORDER_CLOSE_SELL);
/*      */           }
/* 1773 */           else if (type.startsWith("ENTRY")) {
/* 1774 */             color = ((IChartWrapper)chart).getThemeColor(this.sideComboBox.getSelectedIndex() == 0 ? ITheme.ChartElement.ORDER_OPEN_BUY : ITheme.ChartElement.ORDER_OPEN_SELL);
/*      */           }
/*      */ 
/* 1777 */           hLine.setColor(color);
/*      */ 
/* 1779 */           hLine.setText(type);
/* 1780 */           hLine.move(0L, price.doubleValue());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public DDSChartsController getDdsChartsController() {
/* 1787 */     if (this.ddsChartsController == null) {
/* 1788 */       this.ddsChartsController = ((DDSChartsController)GreedContext.get("chartsController"));
/*      */     }
/* 1790 */     return this.ddsChartsController;
/*      */   }
/*      */ 
/*      */   private void removeOrderEntryLine()
/*      */   {
/* 1795 */     String objectName = new StringBuilder().append("_order_entry_line_").append(this.lineId).append(this.instrument).toString();
/* 1796 */     removeLine(objectName);
/*      */   }
/*      */ 
/*      */   private void removeLine(String objectName) {
/* 1800 */     if ((GreedContext.isStrategyAllowed()) && (getDdsChartsController() != null)) {
/* 1801 */       Set charts = getDdsChartsController().getICharts(this.instrument);
/* 1802 */       for (IChart chart : charts)
/* 1803 */         chart.remove(objectName);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeAllLines()
/*      */   {
/* 1809 */     removeOrderEntryLine();
/* 1810 */     removeOrderTakeProfitLine();
/* 1811 */     removeOrderStopLossLine();
/*      */   }
/*      */ 
/*      */   private void removeOrderTakeProfitLine() {
/* 1815 */     String objectName = new StringBuilder().append("_order_take_profit_line_").append(this.lineId).append(this.instrument).toString();
/* 1816 */     removeLine(objectName);
/*      */   }
/*      */ 
/*      */   private void removeOrderStopLossLine() {
/* 1820 */     String objectName = new StringBuilder().append("_order_stop_loss_line_").append(this.lineId).append(this.instrument).toString();
/* 1821 */     removeLine(objectName);
/*      */   }
/*      */ 
/*      */   private void setStopsSlippage() {
/* 1825 */     this.slippageCheckBox.setEnabled(true);
/* 1826 */     this.slippageCheckBox.setSelected(false);
/*      */ 
/* 1828 */     this.slippageSpinner.setEnabled(false);
/* 1829 */     this.slippageSpinner.clear();
/*      */   }
/*      */ 
/*      */   private void setMarketsSlippage() {
/* 1833 */     this.slippageCheckBox.setEnabled(true);
/* 1834 */     this.slippageCheckBox.setSelected(true);
/*      */ 
/* 1836 */     this.slippageSpinner.setEnabled(true);
/* 1837 */     this.slippageSpinner.setValue(this.storage.restoreDefaultSlippage());
/*      */   }
/*      */ 
/*      */   public BigDecimal getSlippage() {
/* 1841 */     if (this.slippageCheckBox.isSelected()) {
/* 1842 */       return (BigDecimal)this.slippageSpinner.getValue();
/*      */     }
/* 1844 */     return null;
/*      */   }
/*      */ 
/*      */   public void setInstrument(Instrument instrument)
/*      */   {
/* 1849 */     this.instrument = instrument;
/*      */ 
/* 1851 */     if (this.stopLossCheckBox.isSelected()) {
/* 1852 */       populateField(this.stopLossPriceField, defaultPriceAdjustment(StopOrderType.STOP_LOSS));
/*      */     }
/* 1854 */     if (this.takeProfitCheckBox.isSelected()) {
/* 1855 */       populateField(this.takeProfitPriceField, defaultPriceAdjustment(StopOrderType.TAKE_PROFIT));
/*      */     }
/*      */ 
/* 1859 */     if (null != this.entryStopDirectionButton) {
/* 1860 */       if (!this.entryLocked) {
/* 1861 */         this.entryStopDirectionButton.setEnabled(this.entryCheckBox.isSelected());
/*      */       }
/* 1863 */       this.takeProfitStopDirectionButton.setEnabled(this.takeProfitCheckBox.isSelected());
/* 1864 */       this.stopLossStopDirectionButton.setEnabled(this.stopLossCheckBox.isSelected());
/*      */     }
/* 1866 */     this.entryStopDirection = null;
/*      */   }
/*      */ 
/*      */   public void setSubmitEnabled(int state) {
/* 1870 */     this.submitButton.setEnabled((state == 0) && ((this.entryCheckBox.isSelected()) || ((this.slippageCheckBox.isSelected()) && (this.slippageCheckBox.isEnabled())) || (this.takeProfitCheckBox.isSelected()) || (this.stopLossCheckBox.isSelected())));
/*      */ 
/* 1873 */     this.entryCheckBox.setEnabled((!this.entryLocked) && (state == 0));
/*      */ 
/* 1875 */     if ((!this.entryCheckBox.isSelected()) && (!this.storage.restoreApplySlippageToAllMarketOrders())) {
/* 1876 */       this.slippageCheckBox.setEnabled(state == 0);
/*      */     }
/*      */ 
/* 1879 */     boolean enabled = !GreedContext.isContest();
/*      */ 
/* 1881 */     this.stopLossCheckBox.setEnabled(enabled);
/* 1882 */     this.takeProfitCheckBox.setEnabled(enabled);
/* 1883 */     this.sideComboBox.setEnabled((!this.orderSideLocked) && (state == 0));
/*      */   }
/*      */ 
/*      */   public void setStopOrdersVisible(boolean visible)
/*      */   {
/* 1888 */     this.stopLossCheckBox.setVisible(visible);
/* 1889 */     this.stopLossStopDirectionButton.setVisible(visible);
/* 1890 */     this.stopLossPriceField.setVisible(visible);
/* 1891 */     this.takeProfitCheckBox.setVisible(visible);
/* 1892 */     this.takeProfitStopDirectionButton.setVisible(visible);
/* 1893 */     this.takeProfitPriceField.setVisible(visible);
/* 1894 */     if (!visible) {
/* 1895 */       this.stopLossCheckBox.setSelected(false);
/* 1896 */       this.takeProfitCheckBox.setSelected(false);
/*      */     }
/*      */ 
/* 1899 */     if (this.quoter.isTradingPossible())
/* 1900 */       this.quoter.setTradable(true);
/*      */   }
/*      */ 
/*      */   public OrderSide getOrderSide()
/*      */   {
/* 1905 */     return this.sideComboBox.getSelectedIndex() == 0 ? OrderSide.BUY : OrderSide.SELL;
/*      */   }
/*      */ 
/*      */   public BigDecimal getEntryPrice() {
/* 1909 */     if ((this.entryCheckBox.isSelected()) && 
/* 1910 */       ((this.entryLocked) || (this.entryPriceField.isEnabled())) && (this.entryPriceField.getText() != null) && (!this.entryPriceField.getText().equals(""))) {
/* 1911 */       return new BigDecimal(this.entryPriceField.getText());
/*      */     }
/*      */ 
/* 1914 */     return null;
/*      */   }
/*      */ 
/*      */   private StopDirection getEntryStopDirection() {
/* 1918 */     return this.entryStopDirection;
/*      */   }
/*      */ 
/*      */   private BigDecimal getStopLossPrice() {
/* 1922 */     if ((this.stopLossCheckBox.isSelected()) && 
/* 1923 */       (this.stopLossPriceField.isEnabled()) && (this.stopLossPriceField.getText() != null) && (!this.stopLossPriceField.getText().equals(""))) {
/* 1924 */       return new BigDecimal(this.stopLossPriceField.getText());
/*      */     }
/*      */ 
/* 1927 */     return null;
/*      */   }
/*      */ 
/*      */   private BigDecimal getTakeProfitPrice() {
/* 1931 */     if ((this.takeProfitCheckBox.isSelected()) && 
/* 1932 */       (this.takeProfitPriceField.isEnabled()) && (this.takeProfitPriceField.getText() != null) && (!this.takeProfitPriceField.getText().equals(""))) {
/* 1933 */       return new BigDecimal(this.takeProfitPriceField.getText());
/*      */     }
/*      */ 
/* 1936 */     return null;
/*      */   }
/*      */ 
/*      */   private StopDirection getStopLossStopDirection() {
/* 1940 */     return this.stopLossStopDirection;
/*      */   }
/*      */ 
/*      */   private StopDirection getTakeProfitStopDirection() {
/* 1944 */     return this.takeProfitStopDirection;
/*      */   }
/*      */ 
/*      */   public String getSelectedSide() {
/* 1948 */     return this.sideComboBox.getSelectedIndex() == 0 ? LocalizationManager.getText("combo.side.buy") : LocalizationManager.getText("combo.sede.sell");
/*      */   }
/*      */ 
/*      */   public void lockOrderSideTo(OrderSide side) {
/* 1952 */     this.sideComboBox.setSelectedIndex(OrderSide.BUY == side ? 0 : 1);
/* 1953 */     this.orderSideLocked = true;
/* 1954 */     this.sideComboBox.setEnabled(false);
/*      */   }
/*      */ 
/*      */   public void lockEntryPriceAndStopDirection(StopDirection stopDirection, double stopPrice) {
/* 1958 */     if (!this.entryCheckBox.isSelected()) {
/* 1959 */       this.entryCheckBox.setSelected(true);
/*      */     }
/* 1961 */     this.entryCheckBox.setEnabled(false);
/* 1962 */     this.entryStopDirectionButton.setEnabled(false);
/* 1963 */     if (stopDirection == null) {
/* 1964 */       this.entryStopDirectionButton.setText("L_MK");
/* 1965 */       this.entryPriceField.clear();
/* 1966 */       this.entryPriceField.setEnabled(false);
/*      */     } else {
/* 1968 */       switch (24.$SwitchMap$com$dukascopy$transport$common$model$type$StopDirection[stopDirection.ordinal()]) {
/*      */       case 2:
/* 1970 */         this.entryStopDirectionButton.setText("L_AG");
/* 1971 */         break;
/*      */       case 6:
/* 1973 */         this.entryStopDirectionButton.setText("L_BE");
/* 1974 */         break;
/*      */       case 4:
/* 1976 */         this.entryStopDirectionButton.setText("L_BL");
/* 1977 */         break;
/*      */       case 5:
/* 1979 */         this.entryStopDirectionButton.setText("L_AE");
/* 1980 */         break;
/*      */       case 3:
/* 1982 */         this.entryStopDirectionButton.setText("L_AL");
/* 1983 */         break;
/*      */       case 1:
/* 1985 */         this.entryStopDirectionButton.setText("L_BG");
/*      */       }
/*      */ 
/* 1988 */       populateField(this.entryPriceField, BigDecimal.valueOf(stopPrice));
/* 1989 */       this.entryPriceField.setEnabled(true);
/* 1990 */       drawOrderEntryLine();
/*      */     }
/* 1992 */     this.entryStopDirection = stopDirection;
/* 1993 */     setSlippage(this.storage.restoreDefaultSlippageAsText());
/* 1994 */     this.slippageCheckBox.setEnabled(true);
/* 1995 */     this.slippageSpinner.setEnabled(this.slippageCheckBox.isSelected());
/* 1996 */     this.entryLocked = true;
/*      */ 
/* 1998 */     if (this.entryStopDirection == null)
/* 1999 */       doEnableDefaultTPandSL();
/*      */   }
/*      */ 
/*      */   public void disableSubmitButton()
/*      */   {
/* 2004 */     this.submitButton.setVisible(false);
/*      */   }
/*      */ 
/*      */   public void setVisibilitySwitchAllowed(boolean visibilitySwitchAllowed) {
/* 2008 */     this.mController.setVisibilitySwitchAllowed(visibilitySwitchAllowed);
/* 2009 */     this.border.setTextKey("header.cond.orders");
/* 2010 */     this.border.setSwitch(this.conditionalOrdersPanel.isVisible());
/*      */   }
/*      */ 
/*      */   public boolean isVisibilitySwitchAllowed() {
/* 2014 */     return this.mController.isVisibilitySwitchAllowed();
/*      */   }
/*      */ 
/*      */   public void setEntryMarketCondition(boolean fromQuoter) {
/* 2018 */     this.entryStopDirectionButton.setText("L_MK");
/* 2019 */     this.entryStopDirection = null;
/* 2020 */     this.entryPriceField.clear();
/* 2021 */     this.entryPriceField.setEnabled(false);
/* 2022 */     setMarketsSlippage();
/* 2023 */     removeOrderEntryLine();
/* 2024 */     doEnableDefaultTPandSL();
/* 2025 */     contestOnClickState();
/*      */ 
/* 2027 */     if ((fromQuoter) && 
/* 2028 */       (!this.entryCheckBox.isSelected())) {
/* 2029 */       this.entryCheckBox.doClick();
/* 2030 */       this.conditionsPopup.setVisible(false);
/* 2031 */       this.entryStopDirectionButton.setEnabled(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getInstrument()
/*      */   {
/* 2039 */     return this.instrument.toString();
/*      */   }
/*      */   public static class DependandHeightButton extends JLocalizableButton {
/*      */     private JComponent sizeEtalon;
/*      */ 
/* 2045 */     public DependandHeightButton(String textKey, JComponent sizeEtalon) { super();
/* 2046 */       this.sizeEtalon = sizeEtalon;
/*      */     }
/*      */ 
/*      */     public Dimension getPreferredSize()
/*      */     {
/* 2051 */       Dimension size = super.getPreferredSize();
/* 2052 */       if (this.sizeEtalon == null) return size;
/* 2053 */       size.height = (this.sizeEtalon.getPreferredSize().height + (PlatformSpecific.LINUX ? 0 : 2));
/* 2054 */       return size;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.ConditionalOrderEntryPanel
 * JD-Core Version:    0.6.0
 */