/*     */ package com.dukascopy.dds2.greed.gui.component.moverview;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.calc.PriceUtil;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.OrderEntryAction;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.component.detached.OrderEntryDetached;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.OrderConfirmationDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.CustomRequestPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableQuoterPanel;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountLabel;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.AmountJSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.MktOverviewAmountJSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.SlippageJSpinner;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketStateWrapperListener;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.GuiResourceLoader;
/*     */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.dds2.greed.util.QuickieOrderSupport;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.JTextArea;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MiniPanel extends JPanel
/*     */   implements QuickieOrderSupport, MarketStateWrapperListener, MouseMotionListener, MouseListener
/*     */ {
/*  61 */   private static Logger LOGGER = LoggerFactory.getLogger(MiniPanel.class);
/*     */ 
/*  63 */   private ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */   private AmountJSpinner amountSpinner;
/*  66 */   private SlippageJSpinner slippageSpinner = new SlippageJSpinner(5.0D, 1000.0D, 0.1D, 1, false);
/*  67 */   private JLocalizableCheckBox slippageCheck = new JLocalizableCheckBox("check.slippage");
/*     */   private String instrument;
/*     */   private JLocalizableQuoterPanel quoter;
/*     */   private CustomRequestPanel customRequest;
/*     */   private int startZOrder;
/*     */   private Point startDrag;
/*     */   private OrderConfirmationDialog confDialog;
/*     */   private MarketView marketView;
/*  78 */   private Color bluredColorColor = new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 180);
/*     */ 
/*     */   public MiniPanel(String instrument)
/*     */   {
/*  84 */     this(instrument, true);
/*     */   }
/*     */ 
/*     */   public MiniPanel(String instrument, boolean defaultsOn)
/*     */   {
/*  90 */     setOpaque(false);
/*     */ 
/*  92 */     this.instrument = (null == instrument ? "EUR/USD" : instrument);
/*  93 */     this.amountSpinner = new MktOverviewAmountJSpinner(Instrument.fromString(instrument));
/*  94 */     this.amountSpinner.setValue(LotAmountChanger.getDefaultAmountValue(Instrument.fromString(instrument)));
/*     */ 
/*  96 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/*     */ 
/*  98 */     setLayout(new BoxLayout(this, 1));
/*     */ 
/* 100 */     add(Box.createHorizontalStrut(240));
/*     */ 
/* 102 */     JRoundedBorder myBorder = new JRoundedBorder(this, null, 7, 4, 5, 2);
/* 103 */     myBorder.setTopBorder(1);
/* 104 */     myBorder.setRightBorder(1);
/* 105 */     myBorder.setLeftBorder(1);
/* 106 */     myBorder.setBottomBorder(1);
/* 107 */     setBorder(myBorder);
/*     */ 
/* 109 */     JPanel top = new JPanel();
/* 110 */     top.setLayout(new BoxLayout(top, 0));
/* 111 */     top.setBackground(SystemColor.controlShadow);
/* 112 */     HeaderPanel header = new HeaderPanel(this.instrument, true);
/* 113 */     String iconFile = "rc/media/minipanel_mainbar_close.png";
/*     */ 
/* 115 */     JLabel buttonClose = new JLabel("");
/*     */     try {
/* 117 */       buttonClose.setIcon(GuiResourceLoader.getInstance().loadImageIcon(iconFile));
/*     */     } catch (Exception e) {
/* 119 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 122 */     buttonClose.setOpaque(false);
/*     */ 
/* 124 */     header.add(Box.createGlue());
/* 125 */     header.add(buttonClose);
/* 126 */     header.add(Box.createHorizontalStrut(10));
/*     */ 
/* 128 */     top.add(header);
/* 129 */     add(top);
/*     */ 
/* 131 */     add(Box.createRigidArea(new Dimension(0, 5)));
/*     */ 
/* 133 */     this.quoter = new JLocalizableQuoterPanel(instrument, this);
/* 134 */     this.quoter.setTradable(false);
/*     */ 
/* 136 */     add(this.quoter);
/*     */ 
/* 139 */     add(Box.createVerticalStrut(4));
/*     */ 
/* 142 */     this.customRequest = new CustomRequestPanel(instrument);
/* 143 */     add(this.customRequest);
/* 144 */     add(Box.createVerticalStrut(5));
/*     */ 
/* 146 */     JPanel panel = new JPanel();
/* 147 */     panel.setLayout(new GridBagLayout());
/* 148 */     GridBagConstraints c = new GridBagConstraints();
/* 149 */     c.insets = new Insets(0, 5, 1, 1);
/* 150 */     c.weightx = 1.0D; c.weighty = 1.0D;
/* 151 */     c.anchor = 17;
/*     */ 
/* 154 */     c.gridx = 0; c.gridy = 0; c.fill = 0;
/* 155 */     panel.add(new LotAmountLabel(Instrument.fromString(instrument)), c);
/* 156 */     c.gridx = 1; c.fill = 2;
/*     */ 
/* 158 */     panel.add(this.amountSpinner, c);
/*     */ 
/* 161 */     c.gridx = 0;
/* 162 */     c.gridy = 1;
/* 163 */     c.fill = 0;
/*     */ 
/* 165 */     this.slippageCheck.setOpaque(false);
/* 166 */     panel.add(this.slippageCheck, c);
/*     */ 
/* 168 */     c.gridx = 1;
/* 169 */     c.fill = 2;
/*     */ 
/* 171 */     this.slippageSpinner.setEnabled(this.slippageCheck.isSelected());
/* 172 */     setSlippage();
/*     */ 
/* 174 */     panel.add(this.slippageSpinner, c);
/*     */ 
/* 177 */     c.gridx = 0;
/* 178 */     c.gridy = 2;
/* 179 */     c.fill = 0;
/* 180 */     panel.add(Box.createHorizontalStrut(1), c);
/*     */ 
/* 182 */     c.gridx = 1;
/* 183 */     c.fill = 2;
/* 184 */     panel.add(Box.createHorizontalStrut(100), c);
/*     */ 
/* 186 */     panel.setOpaque(false);
/* 187 */     add(panel);
/*     */ 
/* 189 */     add(Box.createRigidArea(new Dimension(0, 2)));
/*     */ 
/* 191 */     this.amountSpinner.setHorizontalAlignment(4);
/* 192 */     this.amountSpinner.setMinimum(LotAmountChanger.getMinTradableAmount(Instrument.fromString(getInstrument())));
/*     */ 
/* 194 */     this.slippageSpinner.setHorizontalAlignment(4);
/*     */ 
/* 196 */     this.slippageCheck.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 198 */         if (MiniPanel.this.storage.restoreApplySlippageToAllMarketOrders()) {
/* 199 */           if (MiniPanel.this.slippageCheck.isSelected())
/* 200 */             MiniPanel.this.slippageSpinner.setValue(MiniPanel.this.storage.restoreDefaultSlippage());
/*     */           else
/* 202 */             MiniPanel.this.slippageSpinner.setValue(GuiUtilsAndConstants.FIFE);
/*     */         }
/*     */         else {
/* 205 */           MiniPanel.this.slippageSpinner.setValue(MiniPanel.this.storage.restoreDefaultSlippage());
/*     */         }
/* 207 */         MiniPanel.this.slippageSpinner.setEnabled(MiniPanel.this.slippageCheck.isSelected());
/* 208 */         if (!MiniPanel.this.slippageCheck.isSelected())
/* 209 */           MiniPanel.this.slippageSpinner.setValue(GuiUtilsAndConstants.FIFE);
/*     */       }
/*     */     });
/* 212 */     buttonClose.addMouseListener(new MouseAdapter() {
/*     */       public void mouseClicked(MouseEvent mouseEvent) {
/* 214 */         MiniPanel.this.closeMe();
/*     */       }
/*     */ 
/*     */       public void mouseEntered(MouseEvent mouseEvent) {
/* 218 */         MiniPanel.this.setCursor(Cursor.getPredefinedCursor(12));
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent mouseEvent) {
/* 222 */         MiniPanel.this.setCursor(Cursor.getDefaultCursor());
/*     */       }
/*     */     });
/* 227 */     ((ClientForm)GreedContext.get("clientGui")).addMarketWatcher(this);
/*     */ 
/* 230 */     addMouseMotionListener(this);
/* 231 */     addMouseListener(this);
/*     */ 
/* 235 */     if (instrument == null) {
/* 236 */       LOGGER.error("Instrument is NULL and it is not suppossed to!");
/*     */     }
/*     */ 
/* 239 */     if (this.marketView.getLastMarketState(instrument) != null) {
/* 240 */       onMarketState(this.marketView.getLastMarketState(instrument));
/*     */     }
/*     */ 
/* 243 */     if (GreedContext.isContest())
/* 244 */       this.customRequest.setVisible(false);
/*     */   }
/*     */ 
/*     */   public boolean isOptimizedDrawingEnabled()
/*     */   {
/* 250 */     return false;
/*     */   }
/*     */ 
/*     */   protected void paintComponent(Graphics g)
/*     */   {
/* 255 */     super.paintComponent(g);
/* 256 */     g.setColor(this.bluredColorColor);
/* 257 */     g.fillRect(0, 0, getWidth(), getHeight());
/*     */   }
/*     */ 
/*     */   void setSlippage() {
/* 261 */     if (this.storage.restoreApplySlippageToAllMarketOrders()) {
/* 262 */       this.slippageSpinner.setValue(this.storage.restoreDefaultSlippage());
/* 263 */       this.slippageSpinner.setEnabled(true);
/* 264 */       this.slippageCheck.setSelected(true);
/*     */     } else {
/* 266 */       this.slippageSpinner.clear();
/* 267 */       this.slippageSpinner.setEnabled(false);
/* 268 */       this.slippageCheck.setSelected(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setAmount() {
/* 273 */     this.amountSpinner.setValue(LotAmountChanger.getDefaultAmountValue(Instrument.fromString(getInstrument())));
/*     */   }
/*     */ 
/*     */   private void unsubscribeMe() {
/* 277 */     ((ClientForm)GreedContext.get("clientGui")).removeMarketWatcher(this);
/*     */   }
/*     */ 
/*     */   private void closeMe() {
/* 281 */     unsubscribeMe();
/* 282 */     setVisible(false);
/* 283 */     ((MarketOverviewFrame)GreedContext.get("Dock")).removeMiniPanel(this);
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmount() {
/* 287 */     return this.amountSpinner.getAmountValueInMillions(Instrument.fromString(getInstrument()));
/*     */   }
/*     */ 
/*     */   public String getSlippageAmount() {
/* 291 */     if (!this.slippageCheck.isSelected()) return null;
/* 292 */     return this.slippageSpinner.getValue().toString();
/*     */   }
/*     */ 
/*     */   public void quickieOrder(String instrument, OrderSide side) {
/* 296 */     if (null == instrument) {
/* 297 */       return;
/*     */     }
/*     */ 
/* 300 */     if (!this.amountSpinner.validateEditor()) {
/* 301 */       return;
/*     */     }
/*     */ 
/* 304 */     String[] currencies = instrument.split("/");
/*     */ 
/* 306 */     OrderGroupMessage orderGroup = new OrderGroupMessage();
/* 307 */     orderGroup.setTimestamp(new Date());
/* 308 */     orderGroup.setInstrument(instrument);
/*     */ 
/* 310 */     OrderMessage openingOrder = new OrderMessage();
/* 311 */     openingOrder.setOrderGroupId(orderGroup.getOrderGroupId());
/* 312 */     openingOrder.setInstrument(instrument);
/* 313 */     openingOrder.setOrderDirection(OrderDirection.OPEN);
/* 314 */     openingOrder.setSide(side);
/*     */ 
/* 316 */     OfferSide offerSide = side == OrderSide.BUY ? OfferSide.ASK : OfferSide.BID;
/* 317 */     CurrencyOffer bestOffer = this.marketView.getBestOffer(instrument, offerSide);
/*     */ 
/* 319 */     if ((bestOffer == null) || (bestOffer.getPrice() == null)) {
/* 320 */       return;
/* 323 */     }
/*     */ openingOrder.setPriceClient(bestOffer.getPrice());
/*     */     BigDecimal amountValue;
/*     */     try { amountValue = this.amountSpinner.getAmountValueInMillions(Instrument.fromString(instrument)).multiply(GuiUtilsAndConstants.ONE_MILLION);
/*     */     } catch (Exception e) {
/* 328 */       LOGGER.error(e.getMessage(), e);
/* 329 */       return;
/*     */     }
/*     */ 
/* 333 */     if ((this.slippageCheck.isEnabled()) && (this.slippageSpinner != null)) {
/* 334 */       BigDecimal maxSlippage = (BigDecimal)this.slippageSpinner.getValue();
/* 335 */       BigDecimal trailingLimit = maxSlippage.multiply(PriceUtil.pipValue(bestOffer.getPrice().getValue()));
/* 336 */       openingOrder.setPriceTrailingLimit(new Money(trailingLimit, Currency.getInstance(currencies[1])));
/*     */     }
/*     */ 
/* 339 */     Money mAmount = new Money(amountValue, Currency.getInstance(currencies[0]));
/* 340 */     openingOrder.setAmount(mAmount);
/* 341 */     List orders = new ArrayList();
/* 342 */     orders.add(openingOrder);
/*     */ 
/* 344 */     OrderUtils.addDefaultStopLossAndTakeProfitToMarketGroup(openingOrder, orders);
/*     */ 
/* 346 */     orderGroup.setOrders(orders);
/*     */ 
/* 348 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 349 */     if (gui.getStatusBar().getAccountStatement().getOneClickCheckbox().isSelected()) {
/* 350 */       fireOrderEntryAction(orderGroup);
/*     */     }
/*     */     else {
/* 353 */       this.confDialog = new OrderConfirmationDialog(orderGroup, gui);
/* 354 */       addPreviewInfo(this.confDialog, side);
/* 355 */       this.confDialog.onMarketState(this.marketView.getLastMarketState(instrument));
/* 356 */       this.confDialog.setDeferedTradeLog(true);
/* 357 */       this.confDialog.setVisible(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOrderEntryAction(OrderGroupMessage orderGroup) {
/* 362 */     PlatformInitUtils.setExtSysIdForOrderGroup(orderGroup);
/* 363 */     OrderEntryAction orderEntryAction = new OrderEntryAction(this, orderGroup, true);
/* 364 */     GreedContext.publishEvent(orderEntryAction);
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market) {
/* 368 */     if ((market == null) || (market.getInstrument() == null))
/* 369 */       return;
/*     */     try
/*     */     {
/* 372 */       if (market.getInstrument().equals(this.instrument)) {
/* 373 */         this.quoter.onTick(Instrument.fromString(this.instrument));
/* 374 */         if ((this.confDialog != null) && (this.confDialog.isVisible()))
/* 375 */           this.confDialog.onMarketState(market);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 379 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean takeFocus()
/*     */   {
/* 386 */     MarketOverviewFrame mof = (MarketOverviewFrame)GreedContext.get("Dock");
/* 387 */     TabPanel tp = (TabPanel)mof.getTPane().getTabComponentAt(mof.getTPane().getSelectedIndex());
/* 388 */     if (tp == null) return false;
/* 389 */     tp.getRenamingTextArea();
/* 390 */     if (tp.getRenamingTextArea().isVisible()) {
/* 391 */       requestFocus();
/* 392 */       return true;
/*     */     }
/* 394 */     return false;
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent mouseEvent)
/*     */   {
/* 399 */     Point current = getLocation();
/*     */ 
/* 402 */     Point offset = new Point(mouseEvent.getX() - (int)this.startDrag.getX(), mouseEvent.getY() - (int)this.startDrag.getY());
/*     */ 
/* 406 */     Point newLoc = new Point((int)(current.getX() + (int)offset.getX()), (int)(current.getY() + (int)offset.getY()));
/*     */ 
/* 411 */     setLocation(newLoc);
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent mouseEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent mouseEvent) {
/* 419 */     if (GreedContext.isReadOnly()) {
/* 420 */       LOGGER.info("Operation is not available in view mode.");
/* 421 */       return;
/*     */     }
/*     */ 
/* 424 */     if (0 != this.marketView.getInstrumentState(getInstrument()).getTradable()) {
/* 425 */       LOGGER.info("Operation is not available for restricted instrument.");
/* 426 */       return;
/*     */     }
/*     */ 
/* 429 */     if (2 == mouseEvent.getClickCount()) {
/* 430 */       OrderEntryDetached detachedPanel = new OrderEntryDetached(this.instrument);
/*     */ 
/* 432 */       detachedPanel.onMarketState(this.marketView.getLastMarketState(this.instrument));
/* 433 */       detachedPanel.display();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent mouseEvent)
/*     */   {
/* 439 */     this.startDrag = mouseEvent.getPoint();
/*     */ 
/* 441 */     if (takeFocus()) {
/* 442 */       return;
/*     */     }
/*     */ 
/* 445 */     TabContentPanel tabPanel = (TabContentPanel)getParent();
/* 446 */     this.startZOrder = tabPanel.getComponentZOrder(this);
/* 447 */     tabPanel.setComponentZOrder(this, 0);
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent mouseEvent)
/*     */   {
/* 452 */     Point endPos = getLocation();
/* 453 */     Point endPosition = new Point(endPos.x + this.startDrag.x, endPos.y + this.startDrag.y);
/*     */ 
/* 455 */     JPanel selectedPanel = getEndMiniPanel(endPosition);
/* 456 */     doReplaceMiniPanels(selectedPanel);
/*     */   }
/*     */   public void mouseEntered(MouseEvent mouseEvent) {
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent mouseEvent) {
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/* 465 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   private void doReplaceMiniPanels(JPanel endPanel)
/*     */   {
/* 470 */     if (endPanel == null)
/*     */     {
/* 472 */       TabContentPanel tabPanel = (TabContentPanel)getParent();
/* 473 */       tabPanel.setComponentZOrder(this, this.startZOrder);
/* 474 */       tabPanel.revalidate();
/* 475 */       return;
/*     */     }
/*     */ 
/* 478 */     TabContentPanel tabPanel = (TabContentPanel)getParent();
/* 479 */     tabPanel.setComponentZOrder(this, this.startZOrder);
/*     */ 
/* 481 */     int endZOrder = tabPanel.getComponentZOrder(endPanel);
/*     */ 
/* 483 */     tabPanel.setComponentZOrder(this, endZOrder);
/* 484 */     tabPanel.setComponentZOrder(endPanel, this.startZOrder);
/*     */ 
/* 486 */     tabPanel.revalidate();
/*     */   }
/*     */ 
/*     */   private void addPreviewInfo(OrderConfirmationDialog ocd, OrderSide side) {
/* 490 */     String instrument = getInstrument();
/* 491 */     String slippageVal = null;
/*     */ 
/* 493 */     if (this.slippageCheck.isSelected()) {
/* 494 */       slippageVal = this.slippageSpinner.getValue().toString();
/*     */     }
/*     */ 
/* 497 */     double amount = this.amountSpinner.getAmountValueInMillions(Instrument.fromString(instrument)).doubleValue();
/*     */ 
/* 499 */     String localizedSide = side == OrderSide.BUY ? LocalizationManager.getText("combo.side.buy") : LocalizationManager.getText("combo.sede.sell");
/* 500 */     ocd.getAlertsPanel().add(new JLocalizableLabel("preview.first.line", new Object[] { localizedSide, Double.valueOf(amount), instrument }));
/*     */ 
/* 502 */     if (null != slippageVal) {
/* 503 */       String[] slippArr = { slippageVal };
/* 504 */       JLocalizableLabel slippLabel = new JLocalizableLabel("preview.slippage.line", slippArr);
/* 505 */       ocd.getAlertsPanel().add(slippLabel);
/*     */     }
/*     */   }
/*     */ 
/*     */   private JPanel getEndMiniPanel(Point endPoint) {
/* 510 */     for (int i = 0; i < getParent().getComponents().length; i++) {
/* 511 */       JPanel panel = (JPanel)((TabContentPanel)getParent()).getComponent(i);
/* 512 */       if (isEndPointOverPanel(panel, endPoint)) {
/* 513 */         return panel;
/*     */       }
/*     */     }
/*     */ 
/* 517 */     return null;
/*     */   }
/*     */ 
/*     */   private boolean isEndPointOverPanel(JPanel panel, Point endPoint) {
/* 521 */     int x = panel.getLocation().x;
/* 522 */     int y = panel.getLocation().y;
/*     */ 
/* 524 */     int x1 = (int)(x + panel.getSize().getWidth());
/* 525 */     int y1 = (int)(y + panel.getSize().getHeight());
/*     */ 
/* 527 */     return (endPoint.x >= x) && (endPoint.x <= x1) && (endPoint.y >= y) && (endPoint.y <= y1) && (panel != this);
/*     */   }
/*     */ 
/*     */   public void setTradable(boolean isTradable) {
/* 531 */     this.amountSpinner.setEnabled(isTradable);
/* 532 */     this.customRequest.setSubmitEnabled(isTradable);
/* 533 */     this.quoter.setTradable(isTradable);
/* 534 */     setSlippage();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.moverview.MiniPanel
 * JD-Core Version:    0.6.0
 */