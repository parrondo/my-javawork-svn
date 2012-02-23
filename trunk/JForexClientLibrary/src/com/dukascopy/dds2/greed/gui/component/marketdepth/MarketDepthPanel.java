/*     */ package com.dukascopy.dds2.greed.gui.component.marketdepth;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.MouseController;
/*     */ import com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitable;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.ScrollPaneHeaderRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.border.TitledBorder;
/*     */ import javax.swing.table.DefaultTableModel;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ 
/*     */ public class MarketDepthPanel extends JPanel
/*     */   implements PlatformSpecific, MultiSplitable
/*     */ {
/*     */   private static final int MAX_DEPTH = 10;
/*     */   private static final int SMALL_PREF_HEIGHT = 35;
/*     */   private static final int BIG_PREF_HEIGHT = 130;
/*     */   private static final int MIN_HEIGHT = 46;
/*     */   private static final int MIN = 30;
/*     */   private static final int MAX_HEIGHT = 999;
/*     */   public static final int DEFAULT_DEPTH = 4;
/*  67 */   private static final DecimalFormat PRICE_TOTAL_FORMAT_BIG = new DecimalFormat("0.000##");
/*  68 */   private static final DecimalFormat PRICE_TOTAL_FORMAT_SMALL = new DecimalFormat("0.00000");
/*     */   private static final String THOUSAND = "K";
/*  70 */   private static final BigDecimal TWENTY = BigDecimal.valueOf(20L);
/*     */ 
/*  72 */   private int offersCount = 10;
/*  73 */   private MarketDepthTable marketDepthTable = new MarketDepthTable(this.offersCount, 4);
/*     */   private final Box vertical;
/*     */   private final JScrollPane scroll;
/*  77 */   private final String templateBid = String.format("<html><font color=#640000>{0}</font><font size=%d>{1}</font></html>", new Object[] { Integer.valueOf(MACOSX ? -3 : -2) });
/*  78 */   private final String templateAsk = String.format("<html><font color=#003c00>{0}</font><font size=%d>{1}</font></html>", new Object[] { Integer.valueOf(MACOSX ? -3 : -2) });
/*     */ 
/*  81 */   private Dimension dim = null;
/*  82 */   private JLabel totalVolBidLabel = new JLabel("---"); private JLabel totalVolAskLabel = new JLabel("---"); private JLabel avWeightedPriceBidLabel = new JLabel("---"); private JLabel avWeightedPriceAskLabel = new JLabel("---");
/*     */   private JLocalizableRoundedBorder myBorder;
/* 273 */   static long avgTime = 0L;
/* 274 */   static long maxTime = 0L;
/* 275 */   static long count = 0L;
/* 276 */   static long sumTime = 0L;
/*     */ 
/* 333 */   public static Vector<String> tableHeader = null;
/*     */ 
/*     */   public MarketDepthPanel()
/*     */   {
/*  92 */     setLayout(new BoxLayout(this, 1));
/*     */ 
/*  94 */     if (MACOSX)
/*     */     {
/*  96 */       TitledBorder createTitledBorder = BorderFactory.createTitledBorder("Market Depth (click to hide)");
/*  97 */       setBorder(createTitledBorder);
/*     */     }
/*  99 */     this.myBorder = new JLocalizableRoundedBorder(this, "header.marketd", true);
/*     */ 
/* 101 */     this.myBorder.setTopInset(12);
/* 102 */     this.myBorder.setRightInset(7);
/* 103 */     this.myBorder.setLeftInset(9);
/* 104 */     this.myBorder.setBottomInset(10);
/*     */ 
/* 106 */     setBorder(this.myBorder);
/* 107 */     setOpaque(false);
/* 108 */     setupTable();
/*     */ 
/* 110 */     this.scroll = new JScrollPane(this.marketDepthTable);
/* 111 */     this.scroll.setVerticalScrollBarPolicy(20);
/* 112 */     this.scroll.setCorner("UPPER_RIGHT_CORNER", new ScrollPaneHeaderRenderer());
/*     */ 
/* 114 */     this.vertical = new Box(3);
/* 115 */     Font font = this.totalVolBidLabel.getFont();
/* 116 */     Font boldFont = new Font(font.getName(), 1, font.getSize());
/* 117 */     this.totalVolBidLabel.setForeground(Color.GRAY);
/* 118 */     this.totalVolAskLabel.setForeground(Color.GRAY);
/* 119 */     this.totalVolBidLabel.setFont(boldFont);
/* 120 */     this.totalVolAskLabel.setFont(boldFont);
/* 121 */     JLabel slash1 = new JLabel("/");
/* 122 */     JLabel slash2 = new JLabel("/");
/* 123 */     this.avWeightedPriceBidLabel.setFont(boldFont);
/* 124 */     this.avWeightedPriceAskLabel.setFont(boldFont);
/* 125 */     this.avWeightedPriceAskLabel.setHorizontalAlignment(4);
/* 126 */     this.totalVolBidLabel.setHorizontalAlignment(4);
/*     */ 
/* 129 */     JPanel totalsPanel = new JPanel();
/* 130 */     totalsPanel.setBackground(Color.WHITE);
/* 131 */     totalsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(SystemColor.controlShadow), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
/*     */ 
/* 135 */     totalsPanel.setLayout(new BoxLayout(totalsPanel, 0));
/* 136 */     totalsPanel.add(this.totalVolBidLabel);
/* 137 */     totalsPanel.add(slash1);
/* 138 */     totalsPanel.add(this.avWeightedPriceBidLabel);
/* 139 */     totalsPanel.add(Box.createHorizontalGlue());
/* 140 */     totalsPanel.add(this.avWeightedPriceAskLabel);
/* 141 */     totalsPanel.add(slash2);
/* 142 */     totalsPanel.add(this.totalVolAskLabel);
/*     */ 
/* 145 */     this.vertical.add(Box.createVerticalStrut(8));
/* 146 */     this.vertical.add(totalsPanel);
/* 147 */     this.vertical.add(Box.createVerticalStrut(8));
/* 148 */     this.vertical.add(this.scroll);
/* 149 */     this.vertical.addMouseListener(new MouseAdapter()
/*     */     {
/*     */     });
/* 151 */     add(this.vertical);
/*     */ 
/* 153 */     this.scroll.getViewport().setBackground(GreedContext.GLOBAL_BACKGROUND);
/* 154 */     this.marketDepthTable.setBackground(GreedContext.GLOBAL_BACKGROUND);
/* 155 */     this.marketDepthTable.getTableHeader().setReorderingAllowed(false);
/*     */ 
/* 157 */     MouseController mController = new MouseController(this);
/* 158 */     addMouseMotionListener(mController);
/* 159 */     addMouseListener(mController);
/* 160 */     this.myBorder.setSwitch(this.vertical.isVisible());
/*     */ 
/* 162 */     this.vertical.setPreferredSize(new Dimension(this.vertical.getPreferredSize().width, getPrefHeight()));
/* 163 */     this.vertical.setMinimumSize(new Dimension(this.vertical.getMinimumSize().width, 30));
/*     */   }
/*     */ 
/*     */   public boolean isExpanded() {
/* 167 */     return this.vertical.isVisible();
/*     */   }
/*     */ 
/*     */   public void switchVisibility() {
/* 171 */     this.vertical.setVisible(!this.vertical.isVisible());
/* 172 */     this.myBorder.setSwitch(this.vertical.isVisible());
/*     */ 
/* 174 */     if ((getParent() instanceof MultiSplitPane)) {
/* 175 */       ((MultiSplitPane)getParent()).switchVisibility("marketDepth");
/*     */     } else {
/* 177 */       Component parent = getParent();
/* 178 */       while ((null != parent) && 
/* 179 */         (!(parent instanceof JFrame))) {
/* 180 */         parent = parent.getParent();
/*     */       }
/* 182 */       ((JFrame)parent).pack();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setupTable() {
/* 187 */     this.marketDepthTable.setModel(new MarketDepthTableModel(this.offersCount));
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper marketState)
/*     */   {
/* 192 */     List bids = marketState.getBids();
/* 193 */     List asks = marketState.getAsks();
/*     */ 
/* 195 */     DefaultTableModel tableModel = (DefaultTableModel)this.marketDepthTable.getModel();
/*     */ 
/* 197 */     int loopCount = Math.max(bids.size(), this.offersCount);
/* 198 */     loopCount = Math.max(asks.size(), loopCount);
/* 199 */     this.offersCount = loopCount;
/*     */ 
/* 201 */     boolean isCommodity = LotAmountChanger.isCommodity(Instrument.fromString(marketState.getInstrument()));
/*     */ 
/* 204 */     setTotalVolumeAndWeightedPrice(this.totalVolBidLabel, this.avWeightedPriceBidLabel, this.templateBid, marketState.getCurrencyMarket().getAveragePriceBid(), marketState.getCurrencyMarket().getTotalLiquidityBid(), OfferSide.BID, isCommodity);
/*     */ 
/* 212 */     setTotalVolumeAndWeightedPrice(this.totalVolAskLabel, this.avWeightedPriceAskLabel, this.templateAsk, marketState.getCurrencyMarket().getAveragePriceAsk(), marketState.getCurrencyMarket().getTotalLiquidityAsk(), OfferSide.ASK, isCommodity);
/*     */ 
/* 220 */     tableModel.setDataVector(populateModel(tableModel, bids, asks), getHeader(isCommodity));
/* 221 */     tableModel.fireTableDataChanged();
/*     */ 
/* 223 */     this.marketDepthTable.getColumnModel().getColumn(1).setPreferredWidth(70);
/* 224 */     this.marketDepthTable.getColumnModel().getColumn(1).setMinWidth(60);
/* 225 */     this.marketDepthTable.getColumnModel().getColumn(2).setPreferredWidth(70);
/* 226 */     this.marketDepthTable.getColumnModel().getColumn(2).setMinWidth(60);
/*     */   }
/*     */ 
/*     */   private void setTotalVolumeAndWeightedPrice(JLabel totalVolLabel, JLabel avWeightedPriceLabel, String templateMessageFormat, BigDecimal averagePrice, BigDecimal totalLiquidity, OfferSide side, boolean isCommodity)
/*     */   {
/* 238 */     if ((totalLiquidity != null) && (totalLiquidity.compareTo(BigDecimal.ZERO) > 0))
/*     */     {
/* 240 */       String formatedPrice = getPriceFormat(averagePrice).format(averagePrice);
/*     */ 
/* 243 */       String priceBig = null;
/* 244 */       String priceSmall = null;
/*     */ 
/* 246 */       if ((formatedPrice != null) && (formatedPrice.length() > 1)) {
/* 247 */         int length = formatedPrice.length() - 1;
/* 248 */         priceBig = formatedPrice.substring(0, length);
/* 249 */         priceSmall = formatedPrice.substring(length);
/*     */       } else {
/* 251 */         totalVolLabel.setText("0");
/* 252 */         avWeightedPriceLabel.setText("N/A");
/* 253 */         return;
/*     */       }
/*     */ 
/* 256 */       if (isCommodity)
/* 257 */         totalVolLabel.setText(getCommodityLabel(totalLiquidity.divide(GuiUtilsAndConstants.ONE_THUSAND, 2, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString()));
/*     */       else {
/* 259 */         totalVolLabel.setText(totalLiquidity.divide(GuiUtilsAndConstants.ONE_MILLION, 1, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString());
/*     */       }
/*     */ 
/* 262 */       avWeightedPriceLabel.setText(MessageFormat.format(templateMessageFormat, new Object[] { priceBig, priceSmall }));
/*     */     } else {
/* 264 */       totalVolLabel.setText("0");
/* 265 */       avWeightedPriceLabel.setText("N/A");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static DecimalFormat getPriceFormat(BigDecimal price) {
/* 270 */     return TWENTY.compareTo(price) >= 0 ? PRICE_TOTAL_FORMAT_SMALL : PRICE_TOTAL_FORMAT_BIG;
/*     */   }
/*     */ 
/*     */   private Vector<Vector<String>> populateModel(DefaultTableModel tableModel, List<CurrencyOffer> bids, List<CurrencyOffer> asks)
/*     */   {
/* 279 */     int loopCount = Math.max(bids.size(), asks.size());
/* 280 */     Iterator bidIterator = bids.iterator();
/* 281 */     Iterator askIterator = asks.iterator();
/*     */ 
/* 283 */     Vector dataVector = new Vector(loopCount);
/* 284 */     while ((bidIterator.hasNext()) || (askIterator.hasNext())) {
/* 285 */       Vector tableRow = new Vector(4);
/* 286 */       if (bidIterator.hasNext()) {
/* 287 */         CurrencyOffer bid = (CurrencyOffer)bidIterator.next();
/* 288 */         tableRow.add(formatCellAmount(bid));
/* 289 */         tableRow.add(formatCellPrice(bid));
/*     */       } else {
/* 291 */         tableRow.add("");
/* 292 */         tableRow.add("");
/*     */       }
/* 294 */       if (askIterator.hasNext()) {
/* 295 */         CurrencyOffer ask = (CurrencyOffer)askIterator.next();
/* 296 */         tableRow.add(formatCellPrice(ask));
/* 297 */         tableRow.add(formatCellAmount(ask));
/*     */       } else {
/* 299 */         tableRow.add("");
/* 300 */         tableRow.add("");
/*     */       }
/* 302 */       if (!empty(tableRow)) {
/* 303 */         dataVector.add(tableRow);
/*     */       }
/*     */     }
/* 306 */     return dataVector;
/*     */   }
/*     */ 
/*     */   private boolean empty(Vector<String> tableRow)
/*     */   {
/* 314 */     return (emptyCell(tableRow.get(0))) && (emptyCell(tableRow.get(2))) && (emptyCell(tableRow.get(1))) && (emptyCell(tableRow.get(3)));
/*     */   }
/*     */ 
/*     */   private boolean emptyCell(Object cell) {
/* 318 */     return (cell == null) || ("".equals(cell));
/*     */   }
/*     */ 
/*     */   static Vector<String> getHeader(boolean isCommodity)
/*     */   {
/* 324 */     tableHeader = new Vector(4);
/* 325 */     tableHeader.add(LocalizationManager.getText("column.vol"));
/* 326 */     tableHeader.add(LocalizationManager.getText("column.bid"));
/* 327 */     tableHeader.add(LocalizationManager.getText("column.ask"));
/* 328 */     tableHeader.add(LocalizationManager.getText("column.vol"));
/*     */ 
/* 330 */     return tableHeader;
/*     */   }
/*     */ 
/*     */   private String formatCellAmount(CurrencyOffer offer)
/*     */   {
/* 337 */     boolean isCommodity = LotAmountChanger.isCommodity(Instrument.fromString(offer.getInstrument()));
/*     */ 
/* 339 */     int scale = 6;
/* 340 */     if (isCommodity) scale = 3;
/*     */ 
/* 342 */     String quantityAsString = offer.getAmount().getValue().movePointLeft(scale).stripTrailingZeros().toPlainString();
/* 343 */     if (quantityAsString.length() > 4) {
/* 344 */       quantityAsString = quantityAsString.substring(0, 4);
/*     */     }
/* 346 */     return isCommodity ? getCommodityLabel(quantityAsString) : quantityAsString;
/*     */   }
/*     */ 
/*     */   private String formatCellPrice(CurrencyOffer offer) {
/* 350 */     String priceAsString = offer.getPrice().getValue().toPlainString();
/* 351 */     if (priceAsString.length() > 8)
/* 352 */       priceAsString = priceAsString.substring(0, 8);
/* 353 */     return priceAsString;
/*     */   }
/*     */ 
/*     */   public void paint(Graphics graphics)
/*     */   {
/* 358 */     if ((null == this.dim) && (this.totalVolAskLabel.getText() != null) && (this.totalVolAskLabel.getText().length() > 0) && (this.totalVolBidLabel.getText() != null) && (this.totalVolBidLabel.getText().length() > 0))
/*     */     {
/* 361 */       FontMetrics fm = graphics.getFontMetrics(this.totalVolAskLabel.getFont());
/* 362 */       int width = fm.stringWidth("999.999");
/*     */ 
/* 365 */       int h = fm.getHeight();
/* 366 */       this.dim = new Dimension(width, h);
/* 367 */       this.totalVolAskLabel.setMinimumSize(this.dim);
/* 368 */       this.totalVolAskLabel.setMaximumSize(this.dim);
/* 369 */       this.totalVolAskLabel.setPreferredSize(this.dim);
/* 370 */       this.totalVolBidLabel.setMinimumSize(this.dim);
/* 371 */       this.totalVolBidLabel.setMaximumSize(this.dim);
/* 372 */       this.totalVolBidLabel.setPreferredSize(this.dim);
/*     */     }
/* 374 */     super.paint(graphics);
/*     */   }
/*     */ 
/*     */   private String getCommodityLabel(String label) {
/* 378 */     return label + "K";
/*     */   }
/*     */ 
/*     */   public int getPrefHeight()
/*     */   {
/* 383 */     int storedHeight = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreMarketDepthPanelHeight();
/* 384 */     if (storedHeight != -1) {
/* 385 */       return storedHeight;
/*     */     }
/* 387 */     return GreedContext.isPlatformFrameSmall() ? 35 : 130;
/*     */   }
/*     */ 
/*     */   public int getMaxHeight()
/*     */   {
/* 393 */     return 999;
/*     */   }
/*     */ 
/*     */   public int getMinHeight()
/*     */   {
/* 398 */     return isExpanded() ? 46 : 30;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.marketdepth.MarketDepthPanel
 * JD-Core Version:    0.6.0
 */