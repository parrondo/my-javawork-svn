/*     */ package com.dukascopy.dds2.greed.gui;
/*     */ 
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.dds2.calc.PriceUtil;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.SignalAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.TabedPanelType;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.EmergencyLogger;
/*     */ import com.dukascopy.dds2.greed.util.GuiResourceLoader;
/*     */ import com.dukascopy.dds2.greed.util.LoginPassEncoder;
/*     */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*     */ import com.dukascopy.dds2.greed.util.UIContext;
/*     */ import com.dukascopy.dds2.greed.util.UIContext.OsType;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.signals.SignalMessage;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Desktop;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.text.ParseException;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class GuiUtilsAndConstants
/*     */ {
/*  70 */   private static final Logger LOGGER = LoggerFactory.getLogger(GuiUtilsAndConstants.class);
/*     */ 
/*  72 */   public static final Font FONT_BID_ASK_BAR = new Font("SansSerif", 1, 11);
/*  73 */   public static final Font FONT_VOLUME = new Font("SansSerif", 0, 11);
/*     */ 
/*  75 */   public static final Font FONT_PRICE_SMALL = new Font("Tahoma", 1, 16);
/*  76 */   public static final Font FONT_PRICE_BIG = new Font("Tahoma", 1, 25);
/*     */ 
/*  78 */   public static final Color BG_BID_ASK_BAR = Color.BLACK;
/*  79 */   public static final Color FG_BID_ASK_BAR = Color.WHITE;
/*  80 */   public static final Color FG_VOLUME = new Color(255, 255, 210);
/*  81 */   public static final Color BG_BID = new Color(250, 210, 200);
/*  82 */   public static final Color FG_BID_BIG = new Color(100, 0, 0);
/*  83 */   public static final Color BG_ASK = new Color(210, 250, 200);
/*  84 */   public static final Color FG_ASK_BIG = new Color(0, 60, 0);
/*     */   public static final int BID_ASK_BAR_HEIGHT = 15;
/*  87 */   public static final Color WARN_COLOR = new Color(250, 240, 190);
/*  88 */   public static final Color ERR_COLOR = new Color(250, 190, 180);
/*  89 */   public static final Color INFOCLIENT_COLOR = new Color(50, 205, 50);
/*  90 */   public static final Color NOTIFCLIENT_COLOR = new Color(0, 191, 255);
/*     */   public static final String OUT_OF_SYNC_MESSAGE = "Deal station state appears to be non-consistent.\nRelogin recommended.\nPerform immediately?";
/*     */   public static final String NEW_VERSION_TEMPLATE = "You try to run an out-of-dated version of {0}.\nPlease visit www.dukascopy.com for an update to proceed trading.\nYour current version is no more valid.";
/*  96 */   public static final BigDecimal ONE_MILLION = new BigDecimal("1000000");
/*  97 */   public static final BigDecimal TEN_THUSANDS = new BigDecimal("10000");
/*  98 */   public static final BigDecimal ONE_THUSAND = new BigDecimal("1000");
/*  99 */   public static final BigDecimal ONE_HUDRED = new BigDecimal("100");
/* 100 */   public static final BigDecimal TEN = new BigDecimal("10");
/* 101 */   public static final BigDecimal FIFE = new BigDecimal("5");
/* 102 */   public static final BigDecimal ONE = new BigDecimal("1");
/* 103 */   public static final BigDecimal MIN_XAG_AMOUNT = new BigDecimal("50");
/*     */ 
/* 105 */   public static final BigDecimal DEFAULT_MILL_AMOUNT = new BigDecimal(0.25D);
/*     */   public static final String SMSPC = "EMERGENCY";
/*     */   public static final String TB_BB = "tbbb";
/*     */   public static final String TB_SB = "tbsb";
/*     */   public static final String TB_CM = "tbcm";
/*     */   public static final String DEFAULT_AMOUNT = "0.5";
/*     */   public static final String DEFAULT_SLIPPAGE = "";
/*     */   public static final String EMPTY_FIELD_MESSAGE = "field is empty";
/*     */   public static final String TEMPLATE_BLACK = "<html><font color=#000000>{0}{1}<font size=-2 color=\"gray\">{2}</font></font></html>";
/*     */   public static final String TEMPLATE_WHITE = "<html><font color=#ffffff>{0}{1}<font size=-2>{2}</font></font></html>";
/*     */   public static final String NEWS_VIA_API = "api";
/* 123 */   public static final Icon SKYPE_ICON = new ResizableIcon("systray_skype_active.png");
/*     */ 
/* 125 */   public static String SKYPE_TO_PARTNER_TEMPLATE = "skype:{0}?call";
/* 126 */   public static String SKYPE_TO_PARTNER = "skype:dukascopy?call";
/* 127 */   private static final BigDecimal ZERO = BigDecimal.ZERO;
/* 128 */   private static final BigDecimal TWENTY = BigDecimal.TEN.multiply(new BigDecimal("2"));
/*     */ 
/* 130 */   public static final ResizableIcon DEFAULT_ICON = new ResizableIcon("systray_question_mark.png");
/*     */   public static final String PLATFORM_ICON_PATH = "rc/media/logo_empty_titlebar.png";
/*     */   private static final String PIN_ABOUT_PATH = "rc/media/pin_scheme.png";
/*     */   private static final String PLATFORM_LOGO_PATH = "rc/media/logo_screen.png";
/*     */   private static final String EU_PLATFORM_LOGO_PATH = "rc/media/duka_logo_eu.png";
/* 136 */   public static ImageIcon PLATFPORM_ICON = GuiResourceLoader.getInstance().loadImageIcon("rc/media/logo_empty_titlebar.png");
/* 137 */   public static ImageIcon PLATFPORM_LOGO = GuiResourceLoader.getInstance().loadImageIcon("rc/media/logo_screen.png");
/* 138 */   public static ImageIcon EU_PLATFPORM_LOGO = GuiResourceLoader.getInstance().loadImageIcon("rc/media/duka_logo_eu.png");
/* 139 */   public static ImageIcon PLATFPORM_SPLASH = null;
/*     */ 
/* 141 */   public static final Icon ICON_TITLEBAR_CHART_ACTIVE = new ResizableIcon("titlebar_chart_active.png");
/* 142 */   public static final Icon ICON_TITLEBAR_CHART_INACTIVE = new ResizableIcon("titlebar_chart_inactive.png");
/* 143 */   public static final Icon ICON_TITLEBAR_INDICATOR_ACTIVE = new ResizableIcon("titlebar_indicator_active.png");
/* 144 */   public static final Icon ICON_TITLEBAR_INDICATOR_INACTIVE = new ResizableIcon("titlebar_indicator_inactive.png");
/* 145 */   public static final Icon ICON_TITLEBAR_STRATEGY_ACTIVE = new ResizableIcon("titlebar_strategy_active.png");
/* 146 */   public static final Icon ICON_TITLEBAR_STRATEGY_INACTIVE = new ResizableIcon("titlebar_strategy_inactive.png");
/* 147 */   public static final Icon ICON_TITLEBAR_IN_PROGRESS = new ResizableIcon("titlebar_icon_loading.gif");
/*     */ 
/* 149 */   public static ImageIcon PIN_ABOUT_LOGO = GuiResourceLoader.getInstance().loadImageIcon("rc/media/pin_scheme.png");
/*     */   public static final String JFOREX_PLATFORM = "jforex";
/*     */   public static final String JCLIENT_PLATFORM = "java";
/*     */   public static final String DUKASCOPY = "Dukascopy";
/* 155 */   public static String LABEL_SHORT_NAME = "Dukascopy Bank";
/* 156 */   public static String LABEL_LONG_NAME = LABEL_SHORT_NAME + " SA";
/* 157 */   public static String LABEL_TITLE_NAME = "Dukascopy - Swiss Forex Marketplace";
/* 158 */   public static String LABEL_URL = "www.dukascopy.com";
/* 159 */   public static String LABEL_PHONE = "+41 (0) 22 799 48 48";
/* 160 */   public static String LABEL_SKYPE_ID = "dukascopy";
/*     */   public static final String REPORT_ISSUE_KEY = "menu.item.report.issue";
/*     */ 
/*     */   public static String[] splitPriceForRendering(BigDecimal price)
/*     */   {
/* 176 */     String[] result = { "", "", "", "" };
/*     */ 
/* 178 */     if (price.compareTo(ZERO) == 0)
/*     */     {
/*     */       String tmp49_48 = (result[2] =  = result[3] =  = ""); result[1] = tmp49_48; result[0] = tmp49_48;
/* 180 */       return result;
/*     */     }
/*     */ 
/* 183 */     int trimScale = TWENTY.compareTo(price) >= 0 ? 5 : 3;
/* 184 */     BigDecimal priceTrimmed = price.setScale(trimScale, RoundingMode.HALF_EVEN);
/*     */     int scale;
/*     */     int scale;
/* 187 */     if (trimScale == 5)
/* 188 */       scale = 2;
/*     */     else {
/* 190 */       scale = 0;
/*     */     }
/* 192 */     BigDecimal firstPart = priceTrimmed.setScale(scale, RoundingMode.DOWN);
/* 193 */     result[0] = firstPart.toPlainString();
/* 194 */     if (result[0].indexOf(46) < 0) {
/* 195 */       result[0] = (result[0] + ".");
/*     */     }
/*     */ 
/* 198 */     BigDecimal remainder = priceTrimmed.subtract(firstPart);
/* 199 */     BigDecimal secondPart = remainder.setScale(scale + 2, RoundingMode.DOWN);
/* 200 */     result[1] = secondPart.movePointRight(scale + 2).toPlainString();
/* 201 */     if (result[1].length() == 1) {
/* 202 */       result[1] = ('0' + result[1]);
/*     */     }
/*     */ 
/* 205 */     remainder = remainder.subtract(secondPart);
/* 206 */     BigDecimal thirdPart = remainder.setScale(scale + 3, RoundingMode.DOWN);
/*     */     String tmp257_254 = thirdPart.movePointRight(scale + 3).toPlainString(); result[3] = tmp257_254; result[2] = tmp257_254;
/*     */ 
/* 209 */     return result;
/*     */   }
/*     */ 
/*     */   public static String receiveLineViaHttpOrHttps(URL url)
/*     */     throws IOException
/*     */   {
/* 221 */     LOGGER.debug("receiveLineViaHttpOrHttps: " + url.toString());
/* 222 */     HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
/* 223 */     urlc.setRequestProperty("User-Agent", getUserAgent());
/* 224 */     urlc.setDoInput(true);
/* 225 */     urlc.connect();
/* 226 */     BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
/* 227 */     LOGGER.debug("Connection header: " + urlc.getHeaderField("Connection"));
/*     */ 
/* 229 */     String line = br.readLine();
/*     */ 
/* 231 */     br.close();
/* 232 */     urlc.disconnect();
/* 233 */     return line;
/*     */   }
/*     */ 
/*     */   public static String getUserAgent() {
/* 237 */     String versionNr = GreedContext.CLIENT_VERSION;
/* 238 */     String stratBuildVersionNr = "";
/* 239 */     String javaVersion = System.getProperty("java.vm.version");
/* 240 */     String osName = System.getProperty("os.name");
/*     */ 
/* 242 */     String stratString = "";
/* 243 */     if (GreedContext.isStrategyAllowed()) {
/* 244 */       stratString = " Strategy Enabled";
/* 245 */       stratBuildVersionNr = IStrategy.class.getPackage().getImplementationVersion();
/* 246 */       if (stratBuildVersionNr == null) {
/* 247 */         stratBuildVersionNr = "0";
/*     */       }
/* 249 */       stratBuildVersionNr = "." + stratBuildVersionNr;
/*     */     }
/* 251 */     String minaString = "";
/*     */     try {
/* 253 */       Class.forName("org.apache.mina.common.IoSession");
/* 254 */       minaString = " Mina ";
/*     */     } catch (ClassNotFoundException e) {
/*     */     }
/* 257 */     String userAgent = "DukascopyJavaClient/" + versionNr + stratBuildVersionNr + " (JVM/" + javaVersion + "; " + osName + ") " + stratString + minaString;
/*     */ 
/* 259 */     return userAgent;
/*     */   }
/*     */ 
/*     */   public static void sendResetSignal(Object source)
/*     */   {
/* 267 */     if (GreedContext.isSmspcEnabled())
/* 268 */       GreedContext.publishEvent(new SignalAction(source, new SignalMessage("all", GreedContext.encodeAuth())));
/*     */   }
/*     */ 
/*     */   public static String generateMD5(String login) {
/*     */     MessageDigest md5;
/*     */     try {
/* 275 */       md5 = MessageDigest.getInstance("MD5");
/*     */     } catch (NoSuchAlgorithmException nsae) {
/* 277 */       LOGGER.error(nsae.getMessage(), nsae);
/* 278 */       return null;
/* 280 */     }String CHARSET_ISO = "ISO-8859-1";
/*     */     char[] encodedChars;
/*     */     try { byte[] encodedBytes = md5.digest((login + "secretto").getBytes("ISO-8859-1"));
/* 284 */       encodedChars = new String(encodedBytes, "ISO-8859-1").toCharArray();
/*     */     } catch (UnsupportedEncodingException uee) {
/* 286 */       LOGGER.error(uee.getMessage(), uee);
/* 287 */       return null;
/*     */     }
/* 289 */     return new String(LoginPassEncoder.toHexString(encodedChars));
/*     */   }
/*     */ 
/*     */   public static String buildAuthorizationRequest(String login) {
/* 293 */     String check = generateMD5(login);
/* 294 */     if (null == check) {
/* 295 */       LOGGER.warn("Unable to generate MD5 hash");
/* 296 */       return null;
/*     */     }
/* 298 */     return "check=" + check + "&login=" + login;
/*     */   }
/*     */ 
/*     */   public static void openUrlByPropertyName(String propertyName)
/*     */   {
/* 306 */     openUrlByPropertyName(propertyName, null);
/*     */   }
/*     */ 
/*     */   public static void openUrlByPropertyName(String propertyName, String defaultUrl) {
/* 310 */     Properties properties = (Properties)GreedContext.get("properties");
/* 311 */     String url = (String)properties.get(propertyName);
/*     */ 
/* 313 */     if ((url == null) || (url.trim().isEmpty())) {
/* 314 */       url = defaultUrl;
/*     */     }
/*     */ 
/* 317 */     openURL(url);
/*     */   }
/*     */ 
/*     */   public static void openURL(String url) {
/*     */     try {
/* 322 */       Desktop.getDesktop().browse(new URI(url));
/*     */     } catch (Exception e) {
/* 324 */       LOGGER.error(e.getMessage(), e);
/* 325 */       JOptionPane.showMessageDialog((ClientForm)GreedContext.get("clientGui"), "Error attempting to launch web browser:\n" + e.getLocalizedMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void logClosePosition(Component originator, OrderGroupMessage orderGroup, Money currentPrice)
/*     */   {
/* 333 */     logClosePosition(originator, orderGroup, currentPrice, false);
/*     */   }
/*     */ 
/* 337 */   public static void logClosePosition(Component originator, OrderGroupMessage orderGroup, Money currentPrice, boolean defered) { EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/*     */     OrderGroupMessage newOgm;
/*     */     try {
/* 341 */       newOgm = OrderMessageUtils.copyOrderGroup(orderGroup);
/* 342 */       OrderMessageUtils.roughGroupValidation(newOgm);
/*     */     } catch (ParseException e) {
/* 344 */       LOGGER.error(e.getMessage(), e);
/* 345 */       return;
/*     */     }
/*     */ 
/* 348 */     long start = System.currentTimeMillis();
/*     */ 
/* 350 */     OrderMessage closingOrder = null;
/* 351 */     if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended()))
/* 352 */       closingOrder = createClosingGlobalOrderModified(currentPrice, newOgm);
/*     */     else {
/* 354 */       closingOrder = newOgm.createClosingOrderModified(currentPrice);
/*     */     }
/*     */ 
/* 357 */     List orders = newOgm.getOrders();
/* 358 */     if (closingOrder != null) {
/* 359 */       orders.add(closingOrder);
/* 360 */       newOgm.setOrders(orders);
/* 361 */       logger.add(originator, newOgm, OrderDirection.CLOSE, defered, start);
/*     */     } }
/*     */ 
/*     */   public static OrderMessage createClosingGlobalOrderModified(Money currentPrice, OrderGroupMessage group)
/*     */   {
/* 366 */     if (group != null) {
/* 367 */       OrderSide closeSide = group.getSide() == PositionSide.LONG ? OrderSide.SELL : OrderSide.BUY;
/* 368 */       OrderMessage result = new OrderMessage();
/* 369 */       result.setAmount(group.getAmount());
/* 370 */       result.setInstrument(group.getInstrument());
/* 371 */       result.setOrderDirection(OrderDirection.CLOSE);
/* 372 */       result.setOrderGroupId(group.getOrderGroupId());
/* 373 */       result.setOrderState(OrderState.CREATED);
/* 374 */       result.setSide(closeSide);
/* 375 */       result.setPriceClient(currentPrice);
/* 376 */       return result;
/*     */     }
/* 378 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getStopDirectionString(StopDirection dir)
/*     */   {
/* 388 */     if (null == dir) return "";
/* 389 */     switch (1.$SwitchMap$com$dukascopy$transport$common$model$type$StopDirection[dir.ordinal()]) { case 1:
/* 390 */       return "BID ≥ ";
/*     */     case 2:
/* 391 */       return "ASK ≥ ";
/*     */     case 3:
/* 392 */       return "BID ≤ ";
/*     */     case 4:
/* 393 */       return "ASK ≤ ";
/*     */     case 5:
/* 394 */       return "BID = ";
/*     */     case 6:
/* 395 */       return "ASK = "; }
/* 396 */     return "";
/*     */   }
/*     */ 
/*     */   public static String getSlippageAmount(String slippageText, OrderSide side, String instrument)
/*     */   {
/* 401 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 402 */     OfferSide offerSide = side == OrderSide.BUY ? OfferSide.ASK : OfferSide.BID;
/* 403 */     CurrencyOffer bestOffer = marketView.getBestOffer(instrument, offerSide);
/* 404 */     if ((bestOffer == null) || (slippageText == null)) {
/* 405 */       return null;
/*     */     }
/* 407 */     BigDecimal maxSlippage = null;
/*     */     try {
/* 409 */       maxSlippage = new BigDecimal(slippageText);
/*     */     } catch (Exception e) {
/* 411 */       return null;
/*     */     }
/* 413 */     BigDecimal trailingLimit = maxSlippage.multiply(PriceUtil.pipValue(bestOffer.getPrice().getValue()));
/* 414 */     return trailingLimit.stripTrailingZeros().toPlainString();
/*     */   }
/*     */ 
/*     */   public static String getLatestOrderGroupId(String instrument) {
/* 418 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 419 */     Position position = ((PositionsTableModel)clientForm.getPositionsPanel().getTable().getModel()).getLatestPosition(instrument);
/* 420 */     if (null == position) {
/* 421 */       return null;
/*     */     }
/* 423 */     return position.getPositionID();
/*     */   }
/*     */ 
/*     */   public static void skypeUs()
/*     */   {
/* 455 */     String skypeCommand = (String)GreedContext.getConfig("skypeCommand");
/* 456 */     if ((skypeCommand == null) || (skypeCommand.trim().isEmpty())) {
/* 457 */       return;
/*     */     }
/*     */ 
/* 460 */     String skypeUri = SKYPE_TO_PARTNER;
/* 461 */     LOGGER.debug("SkypeUri is : [{}]", skypeUri);
/* 462 */     String callDukascopyCommand = null;
/*     */ 
/* 464 */     if (skypeCommand.indexOf("%1") > -1) {
/* 465 */       callDukascopyCommand = skypeCommand.replaceFirst("%1", skypeUri);
/* 466 */     } else if (skypeCommand.indexOf("%l") > -1)
/*     */     {
/* 468 */       callDukascopyCommand = skypeCommand.replaceFirst("%l", skypeUri);
/*     */     }
/*     */     else
/*     */     {
/* 473 */       String[] skypeProgramWords = skypeCommand.split("\"");
/* 474 */       int index = 0;
/* 475 */       if (skypeProgramWords.length == 4)
/* 476 */         index = 1;
/* 477 */       else if (skypeProgramWords.length == 2) {
/* 478 */         index = 0;
/*     */       }
/*     */ 
/* 481 */       callDukascopyCommand = skypeProgramWords[index].trim() + " /callto:" + skypeUri;
/*     */     }
/*     */ 
/* 484 */     if (UIContext.getOperatingSystemType().equals(UIContext.OsType.MACOSX)) {
/* 485 */       callDukascopyCommand = "open callto:dukascopy";
/*     */     }
/*     */     try
/*     */     {
/* 489 */       LOGGER.debug("Launching skype: {}", callDukascopyCommand);
/* 490 */       Runtime.getRuntime().exec(callDukascopyCommand);
/*     */     } catch (Exception ex) {
/* 492 */       LOGGER.error("Error while launching skype", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void resetLayout() {
/* 497 */     ClientForm form = (ClientForm)GreedContext.get("clientGui");
/* 498 */     form.resetlayout();
/*     */   }
/*     */ 
/*     */   public static void ensureEventDispatchThread()
/*     */   {
/* 503 */     if ((LOGGER.isDebugEnabled()) && (!SwingUtilities.isEventDispatchThread()))
/* 504 */       throw new IllegalStateException("Must be called from Event Dispatch Thread!");
/*     */   }
/*     */ 
/*     */   public static Rectangle getScreenBounds()
/*     */   {
/* 509 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 510 */     GraphicsDevice[] gs = ge.getScreenDevices();
/* 511 */     int j = gs.length > 0 ? 0 : -1;
/* 512 */     GraphicsDevice gd = gs[j];
/* 513 */     GraphicsConfiguration[] gc = gd.getConfigurations();
/* 514 */     int i = gc.length > 0 ? 0 : -1;
/* 515 */     Rectangle gcBounds = gc[i].getBounds();
/* 516 */     return gcBounds;
/*     */   }
/*     */ 
/*     */   public static Dimension getDisplayDimension() {
/* 520 */     Dimension displayDimension = null;
/*     */ 
/* 522 */     Toolkit toolkit = Toolkit.getDefaultToolkit();
/* 523 */     displayDimension = toolkit.getScreenSize();
/*     */ 
/* 525 */     if (displayDimension == null) {
/* 526 */       displayDimension = new Dimension(400, 400);
/*     */     }
/*     */ 
/* 529 */     return displayDimension;
/*     */   }
/*     */ 
/*     */   public static Dimension getOneQuarterOfDisplayDimension() {
/* 533 */     Dimension displayDimension = getDisplayDimension();
/* 534 */     Dimension defaultSize = new Dimension(displayDimension.width / 4 * 2, displayDimension.height / 4 * 2);
/* 535 */     return defaultSize;
/*     */   }
/*     */ 
/*     */   public static Icon getTitlbarIcon(TabedPanelType panelType, boolean isActivetedIcon)
/*     */   {
/* 540 */     if (TabedPanelType.CHART.equals(panelType)) {
/* 541 */       if (isActivetedIcon) {
/* 542 */         return ICON_TITLEBAR_CHART_ACTIVE;
/*     */       }
/* 544 */       return ICON_TITLEBAR_CHART_INACTIVE;
/* 545 */     }if (TabedPanelType.STRATEGY.equals(panelType)) {
/* 546 */       if (isActivetedIcon) {
/* 547 */         return ICON_TITLEBAR_STRATEGY_ACTIVE;
/*     */       }
/* 549 */       return ICON_TITLEBAR_STRATEGY_INACTIVE;
/* 550 */     }if (TabedPanelType.INDICATOR.equals(panelType)) {
/* 551 */       if (isActivetedIcon) {
/* 552 */         return ICON_TITLEBAR_INDICATOR_ACTIVE;
/*     */       }
/* 554 */       return ICON_TITLEBAR_INDICATOR_INACTIVE;
/*     */     }
/*     */ 
/* 557 */     return DEFAULT_ICON;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants
 * JD-Core Version:    0.6.0
 */