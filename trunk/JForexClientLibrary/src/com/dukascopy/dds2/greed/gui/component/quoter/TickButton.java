/*     */ package com.dukascopy.dds2.greed.gui.component.quoter;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.QuickieOrderSupport;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Point2D.Double;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.Currency;
/*     */ import javax.swing.JPanel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TickButton extends JPanel
/*     */   implements MouseListener, MouseMotionListener
/*     */ {
/*  38 */   private static final Logger LOGGER = LoggerFactory.getLogger(TickButton.class);
/*  39 */   public static final Dimension SIZE = new Dimension(115, 108);
/*     */   public static final int ANGLE = 10;
/*     */   private static final int MILLION = 1000000;
/*  44 */   private static final DecimalFormat PRICE_FORMAT_LONG = new DecimalFormat("0.00000");
/*  45 */   private static final DecimalFormat PRICE_FORMAT_SMALL = new DecimalFormat("0.000");
/*     */ 
/*  47 */   private static final DecimalFormat VOLUME_FORMAT = new DecimalFormat("0.0");
/*     */ 
/*  50 */   private static final Font FONT_BID_ASK_BAR = new Font("SansSerif", 1, 11);
/*  51 */   private static final Font FONT_VOLUME = new Font("SansSerif", 0, 11);
/*     */ 
/*  53 */   private static final Font FONT_PRICE_BIG = new Font("Verdana", 0, 47);
/*  54 */   private static final Font FONT_PRICE_SMALL = new Font("Verdana", 0, 17);
/*     */ 
/*  57 */   private static final Color BG_BID_ASK_BAR = Color.BLACK;
/*  58 */   private static final Color FG_BID_ASK_BAR = Color.WHITE;
/*     */ 
/*  60 */   private static final Color FG_VOLUME = new Color(255, 255, 210);
/*     */ 
/*  62 */   public static final Color REGULAR_BORDER = Color.LIGHT_GRAY;
/*  63 */   public static final Color MOUSE_OVER_BORDER = new Color(250, 248, 243);
/*  64 */   public static final Color PRESSED_BID_BORDER = new Color(100, 0, 0);
/*  65 */   public static final Color PRESSED_ASK_BORDER = new Color(0, 100, 0);
/*     */ 
/*  68 */   private static final Color BG_NOT_TRADABLE = SystemColor.controlShadow;
/*  69 */   private static final Color FG_BID_BIG = new Color(100, 0, 0);
/*  70 */   private static final Color[][] BG_BID_COLORS = { { new Color(240, 200, 190), new Color(245, 220, 210) }, { new Color(250, 210, 200), new Color(255, 230, 220) }, { new Color(255, 220, 210), new Color(255, 240, 230) } };
/*     */ 
/*  76 */   private static final Color FG_ASK_BIG = new Color(0, 60, 0);
/*  77 */   private static final Color[][] BG_ASK_COLORS = { { new Color(200, 225, 190), new Color(220, 240, 220) }, { new Color(210, 250, 200), new Color(230, 255, 230) }, { new Color(215, 255, 215), new Color(235, 255, 240) } };
/*     */ 
/*  84 */   private boolean tradingAllowed = false;
/*     */   private final boolean enabled;
/*  87 */   private double price = 0.0D;
/*  88 */   private String major = "-.--";
/*  89 */   private String minor = "--";
/*  90 */   private String half = "";
/*     */ 
/*  92 */   private String volumeText = null;
/*     */   private Instrument instrument;
/*     */   private final QuickieOrderSupport amountHolder;
/*     */   private final OrderSide side;
/*  99 */   private boolean pressed = false;
/* 100 */   private boolean mouseOver = false;
/* 101 */   private int brightnessRate = 1;
/* 102 */   private boolean blinking = true;
/*     */   private int centerHorizontal;
/*     */   private int blackBarHeigth;
/*     */   private int width;
/*     */   private int height;
/*     */   private int startX;
/*     */   private int startY;
/* 114 */   private MarketView marketView = (MarketView)GreedContext.get("marketView");
/*     */ 
/*     */   public static TickButton getDefaultTickerInstance(OrderSide orderSide, Instrument instrument, QuickieOrderSupport amountHolder) {
/* 117 */     return new TickButton(orderSide, instrument, amountHolder, true, true, true);
/*     */   }
/*     */ 
/*     */   public static TickButton getOrderEditingTickerInstance(OrderSide orderSide, Instrument instrument, QuickieOrderSupport amountHolder) {
/* 121 */     return new TickButton(orderSide, instrument, amountHolder, true, true, false);
/*     */   }
/*     */   private TickButton(OrderSide orderSide, Instrument instrument, QuickieOrderSupport amountHolder, boolean blinkState, boolean tradingAllowed, boolean enabled) {
/* 124 */     this.side = orderSide;
/* 125 */     this.instrument = instrument;
/* 126 */     this.amountHolder = amountHolder;
/* 127 */     this.blinking = blinkState;
/* 128 */     this.tradingAllowed = tradingAllowed;
/* 129 */     this.enabled = enabled;
/* 130 */     build();
/*     */   }
/*     */ 
/*     */   private void build() {
/* 134 */     setPreferredSize(SIZE);
/* 135 */     setMinimumSize(SIZE);
/* 136 */     setSize(SIZE);
/*     */ 
/* 138 */     addMouseListener(this);
/* 139 */     addMouseMotionListener(this);
/* 140 */     initDefaults();
/*     */   }
/*     */ 
/*     */   private final void initDefaults() {
/* 144 */     initCoordinates();
/*     */   }
/*     */ 
/*     */   private final void initCoordinates() {
/* 148 */     this.centerHorizontal = (getSize().width / 2 + 2);
/* 149 */     this.blackBarHeigth = 17;
/*     */ 
/* 151 */     this.width = (getSize().width + 2);
/* 152 */     this.height = (getSize().height - 1);
/*     */ 
/* 154 */     this.startX = 1;
/* 155 */     this.startY = 0;
/*     */   }
/*     */ 
/*     */   protected void paintComponent(Graphics graphics)
/*     */   {
/* 160 */     super.paintComponent(graphics);
/* 161 */     drawTicker((Graphics2D)graphics);
/*     */   }
/*     */ 
/*     */   private void drawTicker(Graphics2D graphics2D) {
/* 165 */     graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 166 */     drawBackground(graphics2D);
/* 167 */     drawTextInfo(graphics2D);
/* 168 */     drawBorder(graphics2D);
/*     */   }
/*     */ 
/*     */   private void drawBackground(Graphics2D graphics2D) {
/* 172 */     if (isTradingPossible())
/*     */     {
/* 174 */       Point2D pt1 = new Point2D.Double(this.centerHorizontal, this.blackBarHeigth);
/* 175 */       Point2D pt2 = new Point2D.Double(this.centerHorizontal, this.height - 20);
/* 176 */       GradientPaint gp = new GradientPaint(pt1, getBackgroundColor(), pt2, getBackgroundColorBrighter(), true);
/* 177 */       graphics2D.setPaint(gp);
/*     */ 
/* 179 */       graphics2D.fillRoundRect(this.startX, this.startY, this.width, this.height, 10, 10);
/*     */     } else {
/* 181 */       graphics2D.setColor(BG_NOT_TRADABLE);
/* 182 */       graphics2D.fillRoundRect(this.startX, this.startY, this.width, this.height, 10, 10);
/*     */     }
/*     */ 
/* 185 */     graphics2D.setColor(BG_BID_ASK_BAR);
/* 186 */     graphics2D.fillRoundRect(this.startX + 1, this.startY + 1, this.width - 1, this.blackBarHeigth - 1, 10, 10);
/* 187 */     graphics2D.fillRect(this.startX, this.startY + 5, this.width, this.blackBarHeigth - 5);
/*     */   }
/*     */ 
/*     */   private void drawTextInfo(Graphics2D graphics2D) {
/* 191 */     writeTextOnBlackBar(graphics2D);
/* 192 */     writePriceText(graphics2D);
/* 193 */     writeBottomText(graphics2D);
/*     */   }
/*     */ 
/*     */   private void writeTextOnBlackBar(Graphics2D graphics2D) {
/* 197 */     FontMetrics barFontMetrics = graphics2D.getFontMetrics(FONT_BID_ASK_BAR);
/* 198 */     graphics2D.setFont(FONT_BID_ASK_BAR);
/* 199 */     graphics2D.setColor(FG_BID_ASK_BAR);
/* 200 */     int startY = barFontMetrics.getHeight() - 2;
/* 201 */     if (this.side == OrderSide.BUY)
/* 202 */       graphics2D.drawString(LocalizationManager.getText("quoter.ask"), this.width - 4 - barFontMetrics.stringWidth(LocalizationManager.getText("quoter.ask")), startY);
/*     */     else {
/* 204 */       graphics2D.drawString(LocalizationManager.getText("quoter.bid"), this.startX + 8, startY);
/*     */     }
/* 206 */     if (this.volumeText == null) return;
/* 207 */     int volumeStartX = this.side == OrderSide.BUY ? this.startX + 8 : this.width - 4 - barFontMetrics.stringWidth(this.volumeText);
/* 208 */     graphics2D.setFont(FONT_VOLUME);
/* 209 */     graphics2D.setColor(FG_VOLUME);
/* 210 */     graphics2D.drawString(this.volumeText, volumeStartX, startY);
/*     */   }
/*     */ 
/*     */   private void writePriceText(Graphics2D graphics2D) {
/* 214 */     FontMetrics priceSmallFontMetrics = graphics2D.getFontMetrics(FONT_PRICE_SMALL);
/* 215 */     FontMetrics priceBigFontMetrics = graphics2D.getFontMetrics(FONT_PRICE_BIG);
/*     */ 
/* 217 */     graphics2D.setFont(FONT_PRICE_SMALL);
/* 218 */     graphics2D.setColor(this.side == OrderSide.BUY ? FG_ASK_BIG : FG_BID_BIG);
/*     */ 
/* 220 */     int majorPriceX = (int)(this.centerHorizontal - priceSmallFontMetrics.stringWidth(this.major) * 0.5D);
/* 221 */     int majorPriceY = (int)(this.height * 0.5D - 12.0D);
/* 222 */     graphics2D.drawString(this.major, majorPriceX, majorPriceY);
/*     */ 
/* 224 */     graphics2D.setFont(FONT_PRICE_BIG);
/* 225 */     int minorPriceX = (int)(this.centerHorizontal - priceBigFontMetrics.stringWidth(this.minor) * 0.5D - priceSmallFontMetrics.stringWidth(this.half) * 0.5D - 3.0D);
/* 226 */     int minorPriceY = (int)(this.height * 0.5D + priceBigFontMetrics.getHeight() * 0.5D);
/* 227 */     graphics2D.drawString(this.minor, minorPriceX, minorPriceY);
/*     */ 
/* 229 */     graphics2D.setFont(FONT_PRICE_SMALL);
/* 230 */     int halfPriceX = minorPriceX + priceBigFontMetrics.stringWidth(this.minor) + 3;
/* 231 */     graphics2D.drawString(this.half, halfPriceX, minorPriceY);
/*     */   }
/*     */ 
/*     */   private void writeBottomText(Graphics2D graphics2D) {
/* 235 */     graphics2D.setFont(FONT_BID_ASK_BAR);
/* 236 */     graphics2D.setColor(this.side == OrderSide.BUY ? FG_ASK_BIG : FG_BID_BIG);
/* 237 */     if (null != this.instrument) {
/* 238 */       String sideStr = this.side == OrderSide.BUY ? LocalizationManager.getText("quoter.buy") : LocalizationManager.getText("quoter.sell");
/* 239 */       String s = sideStr + " " + this.instrument.getPrimaryCurrency().toString();
/* 240 */       int stringWidth = graphics2D.getFontMetrics().stringWidth(s);
/* 241 */       int stringX = (int)(this.width * 0.5D - stringWidth * 0.5D);
/* 242 */       graphics2D.drawString(s, stringX, this.height - 8);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void drawBorder(Graphics2D graphics2D) {
/* 247 */     if (this.pressed)
/* 248 */       graphics2D.setColor(getPressedBorderColor());
/* 249 */     else if (this.mouseOver)
/* 250 */       graphics2D.setColor(MOUSE_OVER_BORDER);
/*     */     else {
/* 252 */       graphics2D.setColor(REGULAR_BORDER);
/*     */     }
/* 254 */     graphics2D.drawRoundRect(this.startX, this.startY, this.width, this.height, 10, 10);
/*     */   }
/*     */ 
/*     */   public void onTick(Instrument instrument) {
/* 258 */     if (this.instrument == instrument) {
/* 259 */       OfferSide offerSide = this.side == OrderSide.BUY ? OfferSide.ASK : OfferSide.BID;
/* 260 */       CurrencyOffer bestOffer = this.marketView.getBestOffer(instrument.toString(), offerSide);
/*     */ 
/* 262 */       BigDecimal price = bestOffer != null ? bestOffer.getPrice().getValue() : BigDecimal.valueOf(0L);
/* 263 */       BigDecimal volume = bestOffer != null ? bestOffer.getAmount().getValue() : BigDecimal.valueOf(0L);
/*     */ 
/* 265 */       setPrice(price.doubleValue());
/* 266 */       setVolume(volume.doubleValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateTradability(Instrument instrument, boolean tradable) {
/* 271 */     if (this.instrument == instrument)
/* 272 */       setTradable(tradable);
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument)
/*     */   {
/* 277 */     this.instrument = instrument;
/*     */ 
/* 279 */     BigDecimal price = null;
/*     */     try
/*     */     {
/* 282 */       OfferSide offerSide = this.side == OrderSide.BUY ? OfferSide.ASK : OfferSide.BID;
/* 283 */       CurrencyOffer bestOffer = this.marketView.getBestOffer(instrument.toString(), offerSide);
/* 284 */       price = bestOffer.getPrice().getValue();
/*     */     }
/*     */     catch (Exception e) {
/* 287 */       repaint();
/* 288 */       return;
/*     */     }
/* 290 */     setPrice(price.doubleValue());
/*     */   }
/*     */ 
/*     */   private void setVolume(double volume) {
/* 294 */     this.volumeText = "--";
/* 295 */     this.volumeText = VOLUME_FORMAT.format(volume / 1000000.0D);
/*     */   }
/*     */ 
/*     */   private void setPrice(double price) {
/* 299 */     if (this.blinking) {
/* 300 */       int comparation = Double.compare(price, this.price);
/* 301 */       if (comparation < 0) this.brightnessRate = 0;
/* 302 */       else if (comparation == 0) this.brightnessRate = 1; else
/* 303 */         this.brightnessRate = 2;
/*     */     } else {
/* 305 */       this.brightnessRate = 1;
/*     */     }
/*     */ 
/* 308 */     this.major = "-.--";
/* 309 */     this.minor = "--";
/* 310 */     this.half = "";
/*     */ 
/* 312 */     if (price != 0.0D) {
/* 313 */       String formatedPrice = getPriceFormat(this.instrument.getPipScale()).format(price);
/*     */ 
/* 315 */       int commaIndex = formatedPrice.indexOf('.') < 0 ? formatedPrice.indexOf(',') : formatedPrice.indexOf('.');
/* 316 */       int endOfMajor = commaIndex > 3 ? 5 : 4;
/* 317 */       if (this.instrument.getPipScale() == 2) {
/* 318 */         endOfMajor = commaIndex + 1;
/*     */       }
/* 320 */       int endOfMinor = endOfMajor + 2;
/*     */       try
/*     */       {
/* 323 */         this.major = formatedPrice.substring(0, endOfMajor);
/* 324 */         this.minor = formatedPrice.substring(endOfMajor, endOfMinor);
/* 325 */         this.half = formatedPrice.substring(endOfMinor);
/*     */       } catch (StringIndexOutOfBoundsException e) {
/*     */       }
/*     */     }
/*     */     else {
/* 330 */       setTradable(false);
/*     */     }
/*     */ 
/* 333 */     this.price = price;
/* 334 */     repaint();
/* 335 */     revalidate();
/*     */   }
/*     */ 
/*     */   private void doSubmit()
/*     */   {
/* 340 */     if (!isTradingPossible()) {
/* 341 */       LOGGER.info("Trading is not possible.");
/* 342 */       return;
/*     */     }
/*     */ 
/* 345 */     double amount = 0.0D;
/*     */ 
/* 347 */     if (GreedContext.isContest()) {
/* 348 */       if ((this.amountHolder instanceof OrderEntryPanel)) {
/* 349 */         OrderEntryPanel oep = (OrderEntryPanel)this.amountHolder;
/* 350 */         oep.prepareContestCoditions(this.side);
/*     */       }
/*     */     } else {
/*     */       try {
/* 354 */         amount = this.amountHolder.getAmount().doubleValue();
/*     */       } catch (Exception ex) {
/* 356 */         LOGGER.error("Unable to parse amount : " + this.amountHolder.getAmount(), ex);
/*     */       } catch (Throwable ex) {
/* 358 */         LOGGER.error("Unable to parse amount : " + this.amountHolder.getAmount(), ex);
/*     */       }
/*     */ 
/* 361 */       if (amount > 0.0D)
/* 362 */         this.amountHolder.quickieOrder(this.instrument.toString(), this.side);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Color getBackgroundColor(int brightness)
/*     */   {
/* 375 */     return this.side == OrderSide.BUY ? BG_ASK_COLORS[this.brightnessRate][brightness] : BG_BID_COLORS[this.brightnessRate][brightness];
/*     */   }
/*     */ 
/*     */   private Color getBackgroundColor() {
/* 379 */     return getBackgroundColor(0);
/*     */   }
/*     */ 
/*     */   private Color getBackgroundColorBrighter() {
/* 383 */     return getBackgroundColor(1);
/*     */   }
/*     */ 
/*     */   private Color getPressedBorderColor() {
/* 387 */     if (isTradingPossible())
/* 388 */       return this.side == OrderSide.BUY ? PRESSED_ASK_BORDER : PRESSED_BID_BORDER;
/* 389 */     return REGULAR_BORDER;
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent event)
/*     */   {
/* 398 */     if (isTradingPossible())
/* 399 */       setCursor(Cursor.getPredefinedCursor(12));
/* 400 */     this.mouseOver = true;
/* 401 */     repaint();
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent event)
/*     */   {
/* 406 */     setCursor(Cursor.getDefaultCursor());
/* 407 */     this.mouseOver = false;
/* 408 */     repaint();
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent event)
/*     */   {
/* 413 */     this.pressed = true;
/* 414 */     repaint();
/* 415 */     doSubmit();
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent event)
/*     */   {
/* 420 */     this.pressed = false;
/* 421 */     repaint();
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent event)
/*     */   {
/* 426 */     if (isTradingPossible())
/* 427 */       setCursor(Cursor.getPredefinedCursor(12));
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent event)
/*     */   {
/* 432 */     if (isTradingPossible())
/* 433 */       setCursor(Cursor.getPredefinedCursor(12));
/*     */   }
/*     */ 
/*     */   public void setTradable(boolean tradable) {
/* 437 */     this.tradingAllowed = tradable;
/* 438 */     repaint();
/*     */   }
/*     */ 
/*     */   public boolean isTradingPossible() {
/* 442 */     return (this.tradingAllowed) && (this.enabled);
/*     */   }
/*     */ 
/*     */   public boolean isBlinking() {
/* 446 */     return this.blinking;
/*     */   }
/*     */ 
/*     */   public void setBlinking(boolean blinking) {
/* 450 */     this.blinking = blinking;
/*     */   }
/*     */ 
/*     */   public DecimalFormat getPriceFormat(int pipScale) {
/* 454 */     if (pipScale == 2) return PRICE_FORMAT_SMALL;
/* 455 */     return PRICE_FORMAT_LONG;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.quoter.TickButton
 * JD-Core Version:    0.6.0
 */