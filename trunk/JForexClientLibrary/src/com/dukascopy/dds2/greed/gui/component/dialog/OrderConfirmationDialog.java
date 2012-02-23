/*     */ package com.dukascopy.dds2.greed.gui.component.dialog;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.OrderEntryAction;
/*     */ import com.dukascopy.dds2.greed.actions.OrderGroupCloseAction;
/*     */ import com.dukascopy.dds2.greed.actions.SignalAction;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder.OrderValidationBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder.ValidationMessage;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableDialog;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.dds2.greed.util.PrintScreenUtilities;
/*     */ import com.dukascopy.dds2.greed.util.ScreenSendingUtilities;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.signals.SignalMessage;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.text.AttributeSet;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.PlainDocument;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrderConfirmationDialog extends JLocalizableDialog
/*     */ {
/*  62 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrderConfirmationDialog.class);
/*     */ 
/*  65 */   private static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("######.#######");
/*     */ 
/*  67 */   private ValidateOrder ov = ValidateOrder.getInstance();
/*     */   private ValidateOrder.OrderValidationBean validationBean;
/*     */   private JPanel tickerPanel;
/*     */   private JLocalizableLabel labelTicker;
/*     */   private Box alertsPanel;
/*     */   private JLocalizableButton bOK;
/*     */   private JLocalizableButton bCancel;
/*     */   private OrderGroupMessage group;
/*     */   private boolean deferedTradeLog;
/*     */   private boolean fromPlaceBidOffer;
/*     */   private JPanel previewContent;
/*     */   private JPanel commonContent;
/*     */   private BufferedImage printScreen;
/*     */   private GridBagConstraints c;
/*     */   private int gridY;
/*     */   private JTextArea orderCreatingCommentArea;
/*     */   private JTextArea positionClosingCommentArea;
/*     */   private JPanel commentValidationPanel;
/*     */   private JLocalizableLabel validationLabel;
/*     */   JLocalizableRadioButton button1;
/*     */   JLocalizableRadioButton button2;
/*     */   JLocalizableRadioButton button3;
/*     */   private JCheckBox showPicture;
/*     */   public static final String ID_JB_OK = "ID_JB_OK";
/*     */   public static final String ID_JF_ORDER_CONFIRM = "ID_JF_ORDER_CONFIRM";
/*     */   private boolean orderSubmited;
/*     */   private boolean isSLorTP;
/*     */   private boolean editMode;
/*     */   private OrderGroupMessage position2Cancel;
/*     */   private Money currentPrice;
/*     */   private static final String SPLITTER = ":::::::::::::::";
/*     */   String[] contestMessages;
/*     */   private ClientSettingsStorage storage;
/*     */ 
/*     */   public OrderConfirmationDialog(OrderGroupMessage orderGroup, JFrame parent)
/*     */   {
/*     */     ValidateOrder tmp20_17 = this.ov; tmp20_17.getClass(); this.validationBean = new ValidateOrder.OrderValidationBean(tmp20_17);
/*     */ 
/*  73 */     this.alertsPanel = Box.createVerticalBox();
/*     */ 
/*  75 */     this.bOK = new JLocalizableButton("button.validation.submit.orders");
/*  76 */     this.bCancel = new JLocalizableButton("button.validation.cancel");
/*     */ 
/*  78 */     this.group = null;
/*  79 */     this.deferedTradeLog = false;
/*     */ 
/*  81 */     this.fromPlaceBidOffer = false;
/*     */ 
/*  83 */     this.previewContent = new JPanel();
/*  84 */     this.commonContent = new JPanel();
/*     */ 
/*  86 */     this.printScreen = null;
/*     */ 
/*  88 */     this.c = new GridBagConstraints();
/*  89 */     this.gridY = 0;
/*     */ 
/*  91 */     this.orderCreatingCommentArea = new JTextArea();
/*  92 */     this.positionClosingCommentArea = new JTextArea();
/*     */ 
/*  94 */     this.commentValidationPanel = new JPanel()
/*     */     {
/*     */     };
/*  99 */     this.validationLabel = new JLocalizableLabel();
/*     */ 
/* 101 */     this.button1 = new JLocalizableRadioButton("radio.fundament.and.news");
/* 102 */     this.button2 = new JLocalizableRadioButton("radio.technical.and.trends");
/* 103 */     this.button3 = new JLocalizableRadioButton("radio.personal.view");
/*     */ 
/* 105 */     this.showPicture = new JCheckBox("Send Charts image");
/*     */ 
/* 112 */     this.isSLorTP = false;
/* 113 */     this.editMode = false;
/*     */ 
/* 120 */     this.contestMessages = new String[] { "validation.sl.gap.to.large.bm", "validation.tp.gap.to.large.bm", "validation.sl.gap.to.large.am", "validation.tp.gap.to.large.am", "validation.sl.gap.to.large.entry", "validation.tp.gap.to.large.entry" };
/*     */ 
/* 128 */     this.storage = ((ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*     */ 
/* 133 */     this.group = orderGroup;
/* 134 */     build();
/* 135 */     if (GreedContext.isContest())
/* 136 */       validateContest();
/*     */   }
/*     */ 
/*     */   public OrderConfirmationDialog(OrderGroupMessage orderGroup, Money currentPrice)
/*     */   {
/*     */     ValidateOrder tmp20_17 = this.ov; tmp20_17.getClass(); this.validationBean = new ValidateOrder.OrderValidationBean(tmp20_17);
/*     */ 
/*  73 */     this.alertsPanel = Box.createVerticalBox();
/*     */ 
/*  75 */     this.bOK = new JLocalizableButton("button.validation.submit.orders");
/*  76 */     this.bCancel = new JLocalizableButton("button.validation.cancel");
/*     */ 
/*  78 */     this.group = null;
/*  79 */     this.deferedTradeLog = false;
/*     */ 
/*  81 */     this.fromPlaceBidOffer = false;
/*     */ 
/*  83 */     this.previewContent = new JPanel();
/*  84 */     this.commonContent = new JPanel();
/*     */ 
/*  86 */     this.printScreen = null;
/*     */ 
/*  88 */     this.c = new GridBagConstraints();
/*  89 */     this.gridY = 0;
/*     */ 
/*  91 */     this.orderCreatingCommentArea = new JTextArea();
/*  92 */     this.positionClosingCommentArea = new JTextArea();
/*     */ 
/*  94 */     this.commentValidationPanel = new JPanel()
/*     */     {
/*     */     };
/*  99 */     this.validationLabel = new JLocalizableLabel();
/*     */ 
/* 101 */     this.button1 = new JLocalizableRadioButton("radio.fundament.and.news");
/* 102 */     this.button2 = new JLocalizableRadioButton("radio.technical.and.trends");
/* 103 */     this.button3 = new JLocalizableRadioButton("radio.personal.view");
/*     */ 
/* 105 */     this.showPicture = new JCheckBox("Send Charts image");
/*     */ 
/* 112 */     this.isSLorTP = false;
/* 113 */     this.editMode = false;
/*     */ 
/* 120 */     this.contestMessages = new String[] { "validation.sl.gap.to.large.bm", "validation.tp.gap.to.large.bm", "validation.sl.gap.to.large.am", "validation.tp.gap.to.large.am", "validation.sl.gap.to.large.entry", "validation.tp.gap.to.large.entry" };
/*     */ 
/* 128 */     this.storage = ((ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*     */ 
/* 141 */     this.group = orderGroup;
/* 142 */     this.position2Cancel = orderGroup;
/* 143 */     this.currentPrice = currentPrice;
/*     */ 
/* 145 */     if (this.position2Cancel == null) {
/* 146 */       LOGGER.error("Canceling Position is null");
/* 147 */       return;
/*     */     }
/*     */ 
/* 150 */     build();
/*     */ 
/* 152 */     addPreviewInfo(this.position2Cancel.getSide().toString(), orderGroup);
/* 153 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 154 */     onMarketState(marketView.getLastMarketState(this.position2Cancel.getInstrument()));
/*     */   }
/*     */ 
/*     */   public OrderConfirmationDialog(ValidateOrder.OrderValidationBean bean, boolean fromPlaceBidOffer, JFrame parent, boolean editMode)
/*     */   {
/* 159 */     super(parent);
/*     */     ValidateOrder tmp21_18 = this.ov; tmp21_18.getClass(); this.validationBean = new ValidateOrder.OrderValidationBean(tmp21_18);
/*     */ 
/*  73 */     this.alertsPanel = Box.createVerticalBox();
/*     */ 
/*  75 */     this.bOK = new JLocalizableButton("button.validation.submit.orders");
/*  76 */     this.bCancel = new JLocalizableButton("button.validation.cancel");
/*     */ 
/*  78 */     this.group = null;
/*  79 */     this.deferedTradeLog = false;
/*     */ 
/*  81 */     this.fromPlaceBidOffer = false;
/*     */ 
/*  83 */     this.previewContent = new JPanel();
/*  84 */     this.commonContent = new JPanel();
/*     */ 
/*  86 */     this.printScreen = null;
/*     */ 
/*  88 */     this.c = new GridBagConstraints();
/*  89 */     this.gridY = 0;
/*     */ 
/*  91 */     this.orderCreatingCommentArea = new JTextArea();
/*  92 */     this.positionClosingCommentArea = new JTextArea();
/*     */ 
/*  94 */     this.commentValidationPanel = new JPanel()
/*     */     {
/*     */     };
/*  99 */     this.validationLabel = new JLocalizableLabel();
/*     */ 
/* 101 */     this.button1 = new JLocalizableRadioButton("radio.fundament.and.news");
/* 102 */     this.button2 = new JLocalizableRadioButton("radio.technical.and.trends");
/* 103 */     this.button3 = new JLocalizableRadioButton("radio.personal.view");
/*     */ 
/* 105 */     this.showPicture = new JCheckBox("Send Charts image");
/*     */ 
/* 112 */     this.isSLorTP = false;
/* 113 */     this.editMode = false;
/*     */ 
/* 120 */     this.contestMessages = new String[] { "validation.sl.gap.to.large.bm", "validation.tp.gap.to.large.bm", "validation.sl.gap.to.large.am", "validation.tp.gap.to.large.am", "validation.sl.gap.to.large.entry", "validation.tp.gap.to.large.entry" };
/*     */ 
/* 128 */     this.storage = ((ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*     */ 
/* 160 */     this.fromPlaceBidOffer = fromPlaceBidOffer;
/* 161 */     this.validationBean = bean;
/* 162 */     this.editMode = editMode;
/* 163 */     build();
/*     */   }
/*     */ 
/*     */   public OrderConfirmationDialog(OrderGroupMessage orderGroup, ValidateOrder.OrderValidationBean bean, JFrame parent, boolean isSLorTP, boolean editMode) {
/* 167 */     super(parent);
/*     */     ValidateOrder tmp21_18 = this.ov; tmp21_18.getClass(); this.validationBean = new ValidateOrder.OrderValidationBean(tmp21_18);
/*     */ 
/*  73 */     this.alertsPanel = Box.createVerticalBox();
/*     */ 
/*  75 */     this.bOK = new JLocalizableButton("button.validation.submit.orders");
/*  76 */     this.bCancel = new JLocalizableButton("button.validation.cancel");
/*     */ 
/*  78 */     this.group = null;
/*  79 */     this.deferedTradeLog = false;
/*     */ 
/*  81 */     this.fromPlaceBidOffer = false;
/*     */ 
/*  83 */     this.previewContent = new JPanel();
/*  84 */     this.commonContent = new JPanel();
/*     */ 
/*  86 */     this.printScreen = null;
/*     */ 
/*  88 */     this.c = new GridBagConstraints();
/*  89 */     this.gridY = 0;
/*     */ 
/*  91 */     this.orderCreatingCommentArea = new JTextArea();
/*  92 */     this.positionClosingCommentArea = new JTextArea();
/*     */ 
/*  94 */     this.commentValidationPanel = new JPanel()
/*     */     {
/*     */     };
/*  99 */     this.validationLabel = new JLocalizableLabel();
/*     */ 
/* 101 */     this.button1 = new JLocalizableRadioButton("radio.fundament.and.news");
/* 102 */     this.button2 = new JLocalizableRadioButton("radio.technical.and.trends");
/* 103 */     this.button3 = new JLocalizableRadioButton("radio.personal.view");
/*     */ 
/* 105 */     this.showPicture = new JCheckBox("Send Charts image");
/*     */ 
/* 112 */     this.isSLorTP = false;
/* 113 */     this.editMode = false;
/*     */ 
/* 120 */     this.contestMessages = new String[] { "validation.sl.gap.to.large.bm", "validation.tp.gap.to.large.bm", "validation.sl.gap.to.large.am", "validation.tp.gap.to.large.am", "validation.sl.gap.to.large.entry", "validation.tp.gap.to.large.entry" };
/*     */ 
/* 128 */     this.storage = ((ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*     */ 
/* 168 */     this.group = orderGroup;
/* 169 */     this.validationBean = bean;
/* 170 */     this.isSLorTP = isSLorTP;
/* 171 */     this.editMode = editMode;
/* 172 */     build();
/* 173 */     if (GreedContext.isContest())
/* 174 */       validateContest();
/*     */   }
/*     */ 
/*     */   private void build()
/*     */   {
/* 180 */     setName("ID_JF_ORDER_CONFIRM");
/* 181 */     this.previewContent.setLayout(new GridBagLayout());
/*     */ 
/* 183 */     this.c.insets = new Insets(0, 10, 0, 10);
/* 184 */     this.c.weightx = 1.0D;
/* 185 */     this.c.gridx = 0;
/* 186 */     this.c.gridy = (this.gridY++);
/* 187 */     this.c.fill = 2;
/* 188 */     this.c.gridwidth = 0;
/*     */ 
/* 190 */     this.labelTicker = new JLocalizableLabel();
/* 191 */     this.tickerPanel = new JPanel();
/* 192 */     this.tickerPanel.setLayout(new FlowLayout(1));
/* 193 */     this.tickerPanel.add(this.labelTicker);
/* 194 */     JPanel instrPanel = new JPanel();
/*     */ 
/* 196 */     setTitle("dialog.preview");
/*     */ 
/* 198 */     if (this.validationBean.validationOk())
/*     */     {
/* 200 */       instrPanel.setBorder(new JLocalizableRoundedBorder(instrPanel, "border.instrument"));
/* 201 */       instrPanel.add(this.tickerPanel);
/* 202 */       this.previewContent.add(instrPanel, this.c);
/*     */ 
/* 204 */       if ((!this.fromPlaceBidOffer) && (!GreedContext.isContest())) {
/* 205 */         this.alertsPanel.setBorder(new JLocalizableRoundedBorder(instrPanel, "border.orders"));
/* 206 */         this.c.gridx = 0;
/* 207 */         this.c.gridy = (this.gridY++);
/* 208 */         this.c.ipady = 5;
/* 209 */         this.c.fill = 2;
/* 210 */         this.c.gridwidth = 0;
/* 211 */         this.previewContent.add(this.alertsPanel, this.c);
/*     */       }
/*     */     }
/*     */     else {
/* 215 */       JLocalizableLabel mess = new JLocalizableLabel();
/* 216 */       if (!this.isSLorTP)
/*     */       {
/* 218 */         mess.setText("validation.long.message");
/* 219 */         mess.setHorizontalAlignment(0);
/*     */ 
/* 221 */         instrPanel.setLayout(new BoxLayout(instrPanel, 3));
/* 222 */         instrPanel.setBorder(new JRoundedBorder(instrPanel));
/*     */ 
/* 224 */         instrPanel.add(mess);
/*     */       }
/*     */       else
/*     */       {
/* 228 */         mess.setText("validation.short.message");
/* 229 */         mess.setHorizontalAlignment(0);
/* 230 */         instrPanel.setLayout(new BoxLayout(instrPanel, 3));
/* 231 */         instrPanel.add(mess);
/*     */       }
/*     */ 
/* 234 */       this.c.gridx = 0; this.c.gridy = (this.gridY++);
/* 235 */       this.c.fill = 2;
/*     */ 
/* 237 */       this.previewContent.add(instrPanel, this.c);
/*     */ 
/* 239 */       Box messagesPanel = Box.createVerticalBox();
/* 240 */       messagesPanel.setBorder(new JLocalizableRoundedBorder(messagesPanel, "border.details"));
/*     */ 
/* 242 */       for (ValidateOrder.ValidationMessage i : this.validationBean.getMessages()) {
/* 243 */         messagesPanel.add(new JLocalizableLabel(i.getMessageKey(), i.getParams()));
/*     */       }
/*     */ 
/* 246 */       this.c.gridx = 0;
/* 247 */       this.c.gridy = (this.gridY++);
/* 248 */       this.c.ipady = 5;
/* 249 */       this.c.fill = 2;
/* 250 */       this.previewContent.add(messagesPanel, this.c);
/*     */ 
/* 252 */       this.bOK.setText("button.validation.yes");
/* 253 */       this.bCancel.setText("button.validation.no");
/*     */ 
/* 255 */       Box questionPanel = Box.createVerticalBox();
/* 256 */       questionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
/*     */ 
/* 259 */       JLocalizableLabel temp = new JLocalizableLabel("validation.question");
/* 260 */       temp.setAlignmentX(0.5F);
/* 261 */       questionPanel.add(temp);
/*     */ 
/* 263 */       this.c.gridx = 0;
/* 264 */       this.c.gridy = (this.gridY++);
/* 265 */       this.c.ipady = 5;
/* 266 */       this.c.gridwidth = 0;
/* 267 */       this.c.fill = 2;
/* 268 */       this.previewContent.add(questionPanel, this.c);
/*     */ 
/* 270 */       setTitle("dialog.validation");
/* 271 */       setAlwaysOnTop(true);
/*     */     }
/*     */ 
/* 274 */     addAnalystContestPanel();
/*     */ 
/* 276 */     JPanel remoteControl = new JPanel();
/* 277 */     GridLayout gl = new GridLayout(1, 2);
/* 278 */     gl.setHgap(5);
/* 279 */     remoteControl.setLayout(gl);
/* 280 */     remoteControl.setBorder(BorderFactory.createEmptyBorder(5, 0, 3, 0));
/* 281 */     this.bOK.setMnemonic(83);
/* 282 */     this.bCancel.setMnemonic(67);
/* 283 */     remoteControl.add(this.bOK);
/* 284 */     remoteControl.add(this.bCancel);
/*     */ 
/* 286 */     this.c.ipady = 0;
/* 287 */     this.c.gridx = 0;
/* 288 */     this.c.gridy = (this.gridY++);
/* 289 */     this.c.fill = 2;
/* 290 */     this.c.gridwidth = 0;
/*     */ 
/* 292 */     this.previewContent.add(remoteControl, this.c);
/*     */ 
/* 294 */     this.bOK.setName("ID_JB_OK");
/* 295 */     this.bOK.setMinimumSize(new Dimension(40, 22));
/* 296 */     this.bCancel.setMinimumSize(new Dimension(40, 22));
/*     */ 
/* 298 */     this.bOK.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 301 */         if ((GreedContext.isContest()) && (!OrderConfirmationDialog.this.isSLorTP) && (!OrderConfirmationDialog.this.editMode)) {
/* 302 */           if ((OrderConfirmationDialog.this.position2Cancel == null) && (!OrderConfirmationDialog.this.fineComment(OrderConfirmationDialog.this.orderCreatingCommentArea))) {
/* 303 */             OrderConfirmationDialog.this.pack();
/* 304 */             return;
/*     */           }
/* 306 */           if (OrderConfirmationDialog.this.position2Cancel != null) {
/* 307 */             if (!OrderConfirmationDialog.this.fineComment(OrderConfirmationDialog.this.positionClosingCommentArea)) {
/* 308 */               OrderConfirmationDialog.this.pack();
/* 309 */               return;
/*     */             }
/* 311 */             String fullComment = OrderConfirmationDialog.this.positionClosingCommentArea.getText();
/*     */ 
/* 313 */             OrderConfirmationDialog.this.position2Cancel.getOpeningOrder().setExecutingTimes(fullComment);
/*     */           }
/*     */         }
/*     */ 
/* 317 */         if (OrderConfirmationDialog.this.position2Cancel != null)
/* 318 */           OrderConfirmationDialog.this.fireOrderClosingAction();
/*     */         else {
/* 320 */           OrderConfirmationDialog.this.fireOrderEntryAction();
/*     */         }
/* 322 */         OrderConfirmationDialog.this.setVisible(false);
/*     */       }
/*     */     });
/* 325 */     this.bCancel.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 327 */         OrderConfirmationDialog.this.setVisible(false);
/*     */       }
/*     */     });
/* 331 */     if (!this.fromPlaceBidOffer) {
/* 332 */       this.bOK.addMouseListener(new MouseAdapter() {
/*     */         public void mouseEntered(MouseEvent e) {
/* 334 */           GreedContext.setConfig("timein", Long.valueOf(System.currentTimeMillis()));
/* 335 */           GreedContext.setConfig("control", OrderConfirmationDialog.this.bOK);
/*     */ 
/* 337 */           if (GreedContext.isSmspcEnabled()) {
/* 338 */             OrderMessage openingOrder = OrderConfirmationDialog.this.group.getOpeningOrder();
/* 339 */             String instrument = openingOrder.getInstrument();
/* 340 */             OrderSide side = openingOrder.getSide();
/* 341 */             BigDecimal amount = openingOrder.getAmount().getValue();
/* 342 */             Money pritral = openingOrder.getPriceTrailingLimit();
/* 343 */             String slippage = null != pritral ? pritral.getValue().toPlainString() : null;
/* 344 */             SignalMessage signal = new SignalMessage(instrument, "tbsb", side, amount, GreedContext.encodeAuth(), slippage);
/*     */ 
/* 350 */             GreedContext.publishEvent(new SignalAction(this, signal));
/*     */           }
/*     */         }
/*     */ 
/*     */         public void mouseExited(MouseEvent e) {
/* 355 */           GuiUtilsAndConstants.sendResetSignal(this);
/*     */         }
/*     */       });
/*     */     }
/* 360 */     this.commonContent.setLayout(new BoxLayout(this.commonContent, 0));
/* 361 */     this.commonContent.add(this.previewContent);
/*     */ 
/* 363 */     setContentPane(this.commonContent);
/*     */ 
/* 365 */     setDefaultCloseOperation(2);
/*     */ 
/* 367 */     if (GreedContext.isContest())
/* 368 */       setAlwaysOnTop(true);
/*     */     else {
/* 370 */       setModal(true);
/*     */     }
/*     */ 
/* 373 */     setAlwaysOnTop(true);
/* 374 */     setResizable(false);
/* 375 */     setLocationRelativeTo(null);
/* 376 */     pack();
/*     */   }
/*     */ 
/*     */   private boolean conformTPorSLAutoCorrection()
/*     */   {
/* 381 */     setAlwaysOnTop(false);
/* 382 */     int reply = JOptionPane.showConfirmDialog((JFrame)GreedContext.get("clientGui"), LocalizationManager.getTextWithArguments("validation.tp.sl.offset.to.small", new Object[] { Double.valueOf(this.storage.restoreDefaultStopLossOffset().doubleValue()), Double.valueOf(this.storage.restoreDefaultTakeProfitOffset().doubleValue()) }), LocalizationManager.getText("dialog.validation"), 0, 2);
/*     */ 
/* 390 */     if (0 == reply) {
/* 391 */       correctSlAndTpPrice();
/* 392 */       return true;
/*     */     }
/*     */ 
/* 395 */     return 1 != reply;
/*     */   }
/*     */ 
/*     */   private void correctSlAndTpPrice()
/*     */   {
/* 407 */     Money price = null;
/* 408 */     Money slPrice = this.group.getStopLossOrder().getPriceStop();
/* 409 */     Money tpPrice = this.group.getTakeProfitOrder().getPriceStop();
/* 410 */     Instrument currInstrument = Instrument.fromString(this.group.getInstrument());
/*     */ 
/* 412 */     if (this.group.getOpeningOrder().getStopDirection() == null)
/* 413 */       price = this.group.getOpeningOrder().getPriceClient();
/*     */     else {
/* 415 */       price = this.group.getOpeningOrder().getPriceStop();
/*     */     }
/* 417 */     if (OrderSide.BUY.equals(this.group.getOpeningOrder().getSide()))
/*     */     {
/* 419 */       if (price.getValue().subtract(slPrice.getValue()).abs().multiply(GuiUtilsAndConstants.TEN_THUSANDS).compareTo(GuiUtilsAndConstants.TEN) < 0) {
/* 420 */         BigDecimal newPrice = price.getValue().subtract(GuiUtilsAndConstants.TEN.divide(GuiUtilsAndConstants.TEN_THUSANDS));
/* 421 */         this.group.getStopLossOrder().setPriceStop(new Money(newPrice, currInstrument.getSecondaryCurrency()));
/*     */       }
/*     */ 
/* 424 */       if (price.getValue().subtract(tpPrice.getValue()).abs().multiply(GuiUtilsAndConstants.TEN_THUSANDS).compareTo(GuiUtilsAndConstants.TEN) < 0) {
/* 425 */         BigDecimal newPrice = price.getValue().add(GuiUtilsAndConstants.TEN.divide(GuiUtilsAndConstants.TEN_THUSANDS));
/* 426 */         this.group.getTakeProfitOrder().setPriceStop(new Money(newPrice, currInstrument.getSecondaryCurrency()));
/*     */       }
/*     */     }
/* 429 */     else if (OrderSide.SELL.equals(this.group.getOpeningOrder().getSide()))
/*     */     {
/* 431 */       if (price.getValue().subtract(slPrice.getValue()).abs().multiply(GuiUtilsAndConstants.TEN_THUSANDS).compareTo(GuiUtilsAndConstants.TEN) < 0) {
/* 432 */         BigDecimal newPrice = price.getValue().add(GuiUtilsAndConstants.TEN.divide(GuiUtilsAndConstants.TEN_THUSANDS));
/* 433 */         this.group.getStopLossOrder().setPriceStop(new Money(newPrice, currInstrument.getSecondaryCurrency()));
/*     */       }
/*     */ 
/* 436 */       if (price.getValue().subtract(tpPrice.getValue()).abs().multiply(GuiUtilsAndConstants.TEN_THUSANDS).compareTo(GuiUtilsAndConstants.TEN) < 0) {
/* 437 */         BigDecimal newPrice = price.getValue().subtract(GuiUtilsAndConstants.TEN.divide(GuiUtilsAndConstants.TEN_THUSANDS));
/* 438 */         this.group.getTakeProfitOrder().setPriceStop(new Money(newPrice, currInstrument.getSecondaryCurrency()));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isSLandTPOk(OrderGroupMessage group)
/*     */   {
/* 445 */     if (!GreedContext.isContest()) return true;
/*     */ 
/* 447 */     Money price = null;
/* 448 */     Money slPrice = group.getStopLossOrder().getPriceStop();
/* 449 */     Money tpPrice = group.getTakeProfitOrder().getPriceStop();
/*     */ 
/* 451 */     if (group.getOpeningOrder().getStopDirection() == null)
/* 452 */       price = group.getOpeningOrder().getPriceClient();
/*     */     else {
/* 454 */       price = group.getOpeningOrder().getPriceStop();
/*     */     }
/* 456 */     if ((price.getValue().subtract(slPrice.getValue()).abs().multiply(GuiUtilsAndConstants.TEN_THUSANDS).compareTo(GuiUtilsAndConstants.TEN) < 0) || (price.getValue().subtract(tpPrice.getValue()).abs().multiply(GuiUtilsAndConstants.TEN_THUSANDS).compareTo(GuiUtilsAndConstants.TEN) < 0))
/*     */     {
/* 458 */       return conformTPorSLAutoCorrection();
/*     */     }
/* 460 */     return true;
/*     */   }
/*     */ 
/*     */   private void validateContest()
/*     */   {
/* 465 */     List thrownMessages = new ArrayList();
/*     */ 
/* 467 */     for (Iterator i$ = this.validationBean.getMessages().iterator(); i$.hasNext(); ) { mess = (ValidateOrder.ValidationMessage)i$.next();
/* 468 */       for (String contMess : Arrays.asList(this.contestMessages))
/* 469 */         if (contMess.equals(mess.getMessageKey()))
/* 470 */           thrownMessages.add(new JLocalizableLabel(contMess, mess.getParams()));
/*     */     }
/*     */     ValidateOrder.ValidationMessage mess;
/* 475 */     if (!thrownMessages.isEmpty()) {
/* 476 */       this.previewContent = new JPanel()
/*     */       {
/*     */       };
/* 494 */       Box messagesPanel = Box.createVerticalBox();
/* 495 */       messagesPanel.setBorder(new JLocalizableRoundedBorder(messagesPanel, "border.details"));
/* 496 */       messagesPanel.add(Box.createVerticalStrut(10));
/*     */ 
/* 498 */       for (JLocalizableLabel message : thrownMessages) {
/* 499 */         messagesPanel.add(message);
/*     */       }
/*     */ 
/* 502 */       this.previewContent.add(messagesPanel, "Center");
/* 503 */       setContentPane(this.previewContent);
/* 504 */       setTitle("dialog.validation");
/*     */ 
/* 506 */       JLocalizableButton cancelButton = new JLocalizableButton("button.cancel");
/* 507 */       cancelButton.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 510 */           OrderConfirmationDialog.this.dispose();
/*     */         }
/*     */       });
/* 514 */       add(new JPanel(new FlowLayout(1, 25, 10), cancelButton)
/*     */       {
/*     */       }
/*     */       , "South");
/*     */ 
/* 519 */       pack();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean b)
/*     */   {
/* 525 */     if (b) pack();
/* 526 */     super.setVisible(b);
/*     */   }
/*     */ 
/*     */   public void hideDialog() {
/* 530 */     setVisible(false);
/*     */   }
/*     */ 
/*     */   private void fireOrderEntryAction()
/*     */   {
/* 535 */     OrderMessage openingOrder = this.group.getOpeningOrder();
/*     */ 
/* 537 */     if (GreedContext.isContest()) {
/* 538 */       if (!isSLandTPOk(this.group)) return;
/* 539 */       openingOrder.setExecutingTimes(getCommentType() + ":::::::::::::::" + this.orderCreatingCommentArea.getText());
/*     */     }
/*     */ 
/* 543 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 544 */     String instrument = openingOrder.getInstrument();
/* 545 */     OfferSide offerSide = OrderSide.BUY.equals(openingOrder.getSide()) ? OfferSide.ASK : OfferSide.BID;
/* 546 */     CurrencyOffer bestOffer = marketView.getBestOffer(instrument, offerSide);
/*     */ 
/* 548 */     if ((null == bestOffer) || (null == bestOffer.getPrice())) {
/* 549 */       PlatformInitUtils.setExtSysIdForOrderGroup(this.group);
/* 550 */       OrderEntryAction orderEntryAction = new OrderEntryAction(this, this.group, this.deferedTradeLog);
/* 551 */       GreedContext.publishEvent(orderEntryAction);
/*     */ 
/* 553 */       return;
/*     */     }
/* 555 */     String priceClient = bestOffer.getPrice().getValue().stripTrailingZeros().toPlainString();
/*     */ 
/* 557 */     String newGroupString = this.group.toProtocolString().replaceAll("\"priceClient\":\"(\\d+(.\\d+)?)\"", "\"priceClient\":\"" + priceClient + "\"");
/* 558 */     OrderGroupMessage newGroup = null;
/*     */     try {
/* 560 */       newGroup = new OrderGroupMessage(new ProtocolMessage(newGroupString));
/*     */     } catch (ParseException e) {
/* 562 */       LOGGER.error(e.getMessage(), e);
/*     */ 
/* 564 */       return;
/*     */     }
/*     */ 
/* 567 */     PlatformInitUtils.setExtSysIdForOrderGroup(newGroup);
/* 568 */     OrderEntryAction orderEntryAction = new OrderEntryAction(this, newGroup, this.deferedTradeLog);
/* 569 */     GreedContext.publishEvent(orderEntryAction);
/* 570 */     this.orderSubmited = true;
/*     */ 
/* 572 */     if (GreedContext.isContest()) {
/* 573 */       ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 574 */       gui.getDealPanel().getOrderEntryPanel().clearEverything(true);
/*     */     }
/*     */ 
/* 577 */     if ((GreedContext.isContest()) && (this.showPicture.isSelected())) {
/* 578 */       PlatformInitUtils.setExtSysIdForContest(newGroup);
/* 579 */       ScreenSendingUtilities.IMAGE_CACHE.put(newGroup.getExternalSysId(), this.printScreen);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOrderClosingAction()
/*     */   {
/* 585 */     if (this.group != null) {
/* 586 */       this.group.getPosition().setInClosingState(true);
/*     */     }
/*     */ 
/* 589 */     if ((GreedContext.isContest()) && (this.showPicture.isSelected())) {
/* 590 */       PlatformInitUtils.setExtSysIdForContest(this.position2Cancel);
/* 591 */       ScreenSendingUtilities.IMAGE_CACHE.put(this.position2Cancel.getExternalSysId(), this.printScreen);
/*     */     }
/*     */ 
/* 594 */     GreedContext.publishEvent(new OrderGroupCloseAction(this, this.position2Cancel, this.currentPrice));
/*     */   }
/*     */ 
/*     */   public boolean isOrderSubmited() {
/* 598 */     return this.orderSubmited;
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market)
/*     */   {
/* 606 */     CurrencyOffer bestBid = market.getBestOffer(OfferSide.BID);
/* 607 */     CurrencyOffer bestAsk = market.getBestOffer(OfferSide.ASK);
/* 608 */     BigDecimal bestBidPrice = bestBid != null ? bestBid.getPrice().getValue() : BigDecimal.ZERO;
/* 609 */     BigDecimal bestAskPrice = bestAsk != null ? bestAsk.getPrice().getValue() : BigDecimal.ZERO;
/*     */ 
/* 611 */     String[] splitPriceBid = GuiUtilsAndConstants.splitPriceForRendering(bestBidPrice);
/* 612 */     String[] splitPriceAsk = GuiUtilsAndConstants.splitPriceForRendering(bestAskPrice);
/*     */ 
/* 614 */     List both = new ArrayList();
/* 615 */     Collections.addAll(both, splitPriceBid);
/* 616 */     Collections.addAll(both, splitPriceAsk);
/* 617 */     String[] bothArr = (String[])both.toArray(new String[0]);
/*     */ 
/* 619 */     this.labelTicker.setTextParams(bothArr);
/*     */ 
/* 621 */     this.labelTicker.setText("validation.ticker.string.tamplate");
/*     */   }
/*     */ 
/*     */   public void addPreviewInfo(String side, OrderGroupMessage orderGroup) {
/* 625 */     String instrument = orderGroup.getInstrument();
/*     */ 
/* 627 */     OrderMessage openingOrder = orderGroup.getOpeningOrder();
/* 628 */     BigDecimal entryPrice = openingOrder.getPriceStop() == null ? null : openingOrder.getPriceStop().getValue();
/* 629 */     StopDirection entryStopDirection = openingOrder.getStopDirection();
/* 630 */     String entryVal = entryPrice == null ? null : entryPrice.toPlainString();
/* 631 */     String entryType = entryStopDirection == null ? null : entryStopDirection.name();
/*     */ 
/* 633 */     BigDecimal slippage = openingOrder.getPriceTrailingLimit() == null ? null : openingOrder.getPriceTrailingLimit().getValue().divide(BigDecimal.valueOf(Instrument.fromString(instrument).getPipValue()), 1, 1);
/*     */ 
/* 637 */     String slippageVal = slippage == null ? null : slippage.toPlainString();
/*     */ 
/* 640 */     BigDecimal stopLossPrice = null;
/* 641 */     StopDirection stopLossStopDirection = null;
/* 642 */     OrderMessage stopLossOrder = orderGroup.getStopLossOrder();
/* 643 */     if (stopLossOrder != null) {
/* 644 */       stopLossPrice = stopLossOrder.getPriceStop().getValue();
/* 645 */       stopLossStopDirection = stopLossOrder.getStopDirection();
/*     */     }
/* 647 */     String stopLossVal = stopLossPrice == null ? null : stopLossPrice.toPlainString();
/* 648 */     String stopLossType = stopLossStopDirection == null ? null : stopLossStopDirection.name();
/*     */ 
/* 650 */     BigDecimal takeProfitPrice = null;
/* 651 */     StopDirection takeProfitStopDirection = null;
/* 652 */     OrderMessage takeProfitOrder = orderGroup.getTakeProfitOrder();
/* 653 */     if (takeProfitOrder != null) {
/* 654 */       takeProfitPrice = takeProfitOrder.getPriceStop().getValue();
/* 655 */       takeProfitStopDirection = takeProfitOrder.getStopDirection();
/*     */     }
/* 657 */     String takeProfitVal = takeProfitPrice == null ? null : takeProfitPrice.toPlainString();
/* 658 */     String takeProfitType = takeProfitStopDirection == null ? null : takeProfitStopDirection.name();
/*     */ 
/* 660 */     double amount = openingOrder.getAmount().getValue().divide(BigDecimal.valueOf(1000000L), 1).doubleValue();
/*     */ 
/* 662 */     getAlertsPanel().add(new JLocalizableLabel("preview.first.line", new Object[] { side, AMOUNT_FORMAT.format(amount), instrument }));
/* 663 */     if (null != entryVal) {
/* 664 */       if (LocalizationManager.getText("combo.side.buy").equals(side)) {
/* 665 */         if (StopDirection.ASK_EQUALS.name().equals(entryType)) entryType = LocalizationManager.getText("preview.ASK.EQUALS");
/* 666 */         if (StopDirection.ASK_GREATER.name().equals(entryType)) entryType = LocalizationManager.getText("preview.ASK.GREATER");
/* 667 */         if (StopDirection.BID_GREATER.name().equals(entryType)) entryType = LocalizationManager.getText("preview.BID.GREATER"); 
/*     */       }
/*     */       else {
/* 669 */         if (StopDirection.BID_EQUALS.name().equals(entryType)) entryType = LocalizationManager.getText("preview.BID.EQUALS");
/* 670 */         if (StopDirection.ASK_LESS.name().equals(entryType)) entryType = LocalizationManager.getText("preview.ASK.LESS");
/* 671 */         if (StopDirection.BID_LESS.name().equals(entryType)) entryType = LocalizationManager.getText("preview.BID_LESS");
/*     */       }
/* 673 */       if (entryType != null) {
/* 674 */         getAlertsPanel().add(new JLocalizableLabel("preview.entry.line", new Object[] { entryType, entryVal }));
/*     */       }
/*     */     }
/*     */ 
/* 678 */     if (null != slippageVal) {
/* 679 */       getAlertsPanel().add(new JLocalizableLabel("preview.slippage.line", new Object[] { slippageVal }));
/*     */     }
/*     */ 
/* 682 */     if (null != stopLossVal) {
/* 683 */       if (LocalizationManager.getText("combo.side.buy").equals(side)) {
/* 684 */         if (StopDirection.ASK_LESS.name().equals(stopLossType)) stopLossType = LocalizationManager.getText("preview.ASK.LESS");
/* 685 */         if (StopDirection.BID_LESS.name().equals(stopLossType)) stopLossType = LocalizationManager.getText("preview.BID_LESS"); 
/*     */       }
/*     */       else {
/* 687 */         if (StopDirection.ASK_GREATER.name().equals(stopLossType)) stopLossType = LocalizationManager.getText("preview.ASK.GREATER");
/* 688 */         if (StopDirection.BID_GREATER.name().equals(stopLossType)) stopLossType = LocalizationManager.getText("preview.BID.GREATER");
/*     */       }
/* 690 */       getAlertsPanel().add(new JLocalizableLabel("preview.slippage.line", new Object[] { stopLossType, stopLossVal }));
/*     */     }
/*     */ 
/* 693 */     if (null != takeProfitVal) {
/* 694 */       if (LocalizationManager.getText("combo.side.buy").equals(side)) {
/* 695 */         if (StopDirection.BID_EQUALS.name().equals(takeProfitType)) takeProfitType = LocalizationManager.getText("preview.BID.EQUALS");
/* 697 */         else if (StopDirection.ASK_EQUALS.name().equals(takeProfitType)) takeProfitType = LocalizationManager.getText("preview.ASK.EQUALS");
/*     */       }
/* 699 */       getAlertsPanel().add(new JLocalizableLabel("preview.slippage.line", new Object[] { takeProfitType, takeProfitVal }));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addAnalystContestPanel()
/*     */   {
/* 706 */     if (!GreedContext.isContest()) return;
/*     */ 
/* 710 */     if ((this.position2Cancel != null) && (this.position2Cancel.getOpeningOrder() != null) && ((this.position2Cancel.getOpeningOrder().isStopLoss()) || (this.position2Cancel.getOpeningOrder().isTakeProfit())))
/*     */     {
/* 713 */       return;
/*     */     }
/* 715 */     if (this.isSLorTP) {
/* 716 */       return;
/*     */     }
/* 718 */     if (this.editMode) {
/* 719 */       return;
/*     */     }
/*     */ 
/* 722 */     JPanel contestPanel = new JPanel()
/*     */     {
/*     */     };
/* 729 */     JPanel closingPositionCommentPanel = new JPanel()
/*     */     {
/*     */     };
/* 739 */     JPanel creatingOrderCommentPanel = new JPanel()
/*     */     {
/*     */     };
/* 751 */     JPanel radioPanel = new JPanel()
/*     */     {
/*     */     };
/* 765 */     this.validationLabel.setForeground(Color.RED);
/* 766 */     this.commentValidationPanel.add(this.validationLabel);
/*     */ 
/* 768 */     contestPanel.add(radioPanel);
/* 769 */     contestPanel.add(Box.createVerticalStrut(5));
/*     */ 
/* 771 */     contestPanel.add(new JLocalizableLabel("analyst.contest.comment.forecast"));
/* 772 */     contestPanel.add(creatingOrderCommentPanel);
/* 773 */     if (this.position2Cancel != null)
/*     */     {
/* 775 */       this.orderCreatingCommentArea.setEnabled(false);
/* 776 */       this.button1.setEnabled(false);
/* 777 */       this.button2.setEnabled(false);
/* 778 */       this.button3.setEnabled(false);
/*     */ 
/* 780 */       contestPanel.add(Box.createVerticalStrut(5));
/* 781 */       contestPanel.add(new JLocalizableLabel("analyst.contest.comment.close.reason"));
/* 782 */       contestPanel.add(closingPositionCommentPanel);
/*     */     }
/* 784 */     contestPanel.add(Box.createVerticalStrut(5));
/* 785 */     contestPanel.add(this.commentValidationPanel);
/*     */ 
/* 787 */     if ((GreedContext.getContestImageSendingURL() != null) && (this.position2Cancel == null)) {
/* 788 */       contestPanel.add(this.showPicture);
/*     */     }
/*     */ 
/* 791 */     this.c.gridx = 0;
/* 792 */     this.c.gridy = (this.gridY++);
/* 793 */     this.c.ipady = 40;
/* 794 */     this.c.fill = 2;
/*     */ 
/* 796 */     this.previewContent.add(contestPanel, this.c);
/*     */ 
/* 798 */     this.orderCreatingCommentArea.setDocument(new LimitDocument(200));
/* 799 */     this.positionClosingCommentArea.setDocument(new LimitDocument(200));
/*     */ 
/* 802 */     this.showPicture.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 805 */         if (OrderConfirmationDialog.this.showPicture.isSelected())
/*     */         {
/*     */           try {
/* 808 */             OrderConfirmationDialog.access$1102(OrderConfirmationDialog.this, PrintScreenUtilities.printChartsTabbedPane());
/* 809 */             OrderConfirmationDialog.this.commonContent.add(PrintScreenUtilities.createScaledPanel(OrderConfirmationDialog.this.printScreen, 3));
/* 810 */             OrderConfirmationDialog.this.pack();
/*     */           } catch (Exception exc) {
/* 812 */             OrderConfirmationDialog.LOGGER.warn(exc.getMessage());
/* 813 */             OrderConfirmationDialog.this.showCommentValidation("Cannot print charts. Please bring charts on top of the desktop.");
/* 814 */             OrderConfirmationDialog.this.pack();
/*     */           }
/*     */         } else {
/* 817 */           OrderConfirmationDialog.access$1102(OrderConfirmationDialog.this, null);
/* 818 */           OrderConfirmationDialog.this.commonContent.remove(1);
/* 819 */           OrderConfirmationDialog.this.pack();
/*     */         }
/*     */       }
/*     */     });
/* 825 */     populateComment();
/*     */   }
/*     */ 
/*     */   private void showCommentValidation(String message) {
/* 829 */     if (!GreedContext.isContest()) return;
/*     */ 
/* 831 */     this.validationLabel.setText(message);
/* 832 */     this.commentValidationPanel.setVisible(true);
/* 833 */     this.bOK.setEnabled(false);
/*     */ 
/* 835 */     TimerTask task = new TimerTask()
/*     */     {
/*     */       public void run() {
/* 838 */         OrderConfirmationDialog.this.commentValidationPanel.setVisible(false);
/* 839 */         OrderConfirmationDialog.this.bOK.setEnabled(true);
/*     */       }
/*     */     };
/* 842 */     Timer timer = new Timer();
/* 843 */     timer.schedule(task, 3000L);
/*     */   }
/*     */ 
/*     */   private boolean fineComment(JTextArea area) {
/* 847 */     return (!isEmptyComment(area)) && (!isLargeComment(area)) && (!isQuotedComment(area));
/*     */   }
/*     */ 
/*     */   private boolean isQuotedComment(JTextArea area) {
/* 851 */     if (!GreedContext.isContest()) return false;
/* 852 */     if (area.getText() == null) return false;
/*     */ 
/* 855 */     return area.getText().contains("\"");
/*     */   }
/*     */ 
/*     */   private boolean isEmptyComment(JTextArea area)
/*     */   {
/* 861 */     if (!GreedContext.isContest()) return false;
/* 862 */     if ((area.getText() == null) || ("".equals(area.getText().trim()))) {
/* 863 */       showCommentValidation("validation.message.empty.comment");
/* 864 */       return true;
/*     */     }
/* 866 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean isLargeComment(JTextArea area) {
/* 870 */     if (!GreedContext.isContest()) return false;
/* 871 */     if (area.getText() == null) return false;
/* 872 */     if (area.getText().trim().length() > 200) {
/* 873 */       showCommentValidation("validation.message.large.comment");
/* 874 */       return true;
/*     */     }
/* 876 */     return false;
/*     */   }
/*     */ 
/*     */   private void populateComment()
/*     */   {
/* 881 */     this.button1.setSelected(true);
/*     */ 
/* 883 */     if (this.position2Cancel != null) {
/* 884 */       if (this.position2Cancel.getOpeningOrder().getExecutionTime() == null) return;
/* 885 */       String[] commentData = this.position2Cancel.getOpeningOrder().getExecutionTime().split(":::::::::::::::");
/* 886 */       if ((commentData == null) || (commentData.length != 2)) return;
/*     */ 
/* 888 */       String commentType = commentData[0];
/* 889 */       if ("1".equals(commentType))
/* 890 */         this.button1.setSelected(true);
/* 891 */       else if ("2".equals(commentType))
/* 892 */         this.button2.setSelected(true);
/* 893 */       else if ("3".equals(commentType)) {
/* 894 */         this.button3.setSelected(true);
/*     */       }
/*     */ 
/* 897 */       String comment = commentData[1];
/* 898 */       this.orderCreatingCommentArea.setText(comment);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getCommentType()
/*     */   {
/* 904 */     if (this.button1.isSelected()) return "1";
/* 905 */     if (this.button2.isSelected()) return "2";
/* 906 */     if (this.button3.isSelected()) return "3";
/* 907 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isDeferedTradeLog() {
/* 911 */     return this.deferedTradeLog;
/*     */   }
/*     */ 
/*     */   public void setDeferedTradeLog(boolean deferedTradeLog) {
/* 915 */     this.deferedTradeLog = deferedTradeLog;
/*     */   }
/*     */   public JButton getBOK() {
/* 918 */     return this.bOK;
/*     */   }
/*     */ 
/*     */   public JPanel getTickerPanel() {
/* 922 */     return this.tickerPanel;
/*     */   }
/*     */ 
/*     */   public Box getAlertsPanel() {
/* 926 */     return this.alertsPanel;
/*     */   }
/*     */   class LimitDocument extends PlainDocument {
/*     */     int limit;
/*     */ 
/*     */     public LimitDocument(int limit) {
/* 933 */       this.limit = limit;
/*     */     }
/*     */ 
/*     */     public void insertString(int offset, String s, AttributeSet a) throws BadLocationException {
/* 937 */       if (offset + s.length() < this.limit)
/* 938 */         super.insertString(offset, s, a);
/*     */       else
/* 940 */         Toolkit.getDefaultToolkit().beep();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.OrderConfirmationDialog
 * JD-Core Version:    0.6.0
 */