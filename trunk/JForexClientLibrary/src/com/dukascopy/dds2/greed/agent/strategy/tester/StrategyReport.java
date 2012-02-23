/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.DateFormat;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class StrategyReport
/*     */ {
/*  33 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategyReport.class);
/*     */ 
/*     */   public static void createReport(File tempFile, ITesterReport testerReportData, Currency accountCurrency, boolean addEventLog) throws IOException {
/*  36 */     Writer out = new BufferedWriter(new FileWriter(tempFile));
/*     */     try {
/*  38 */       CurrencyConverter currencyConverter = new CurrencyConverter(testerReportData, null);
/*  39 */       DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*  40 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  41 */       DecimalFormat decFormat = new DecimalFormat("0.##");
/*  42 */       BigDecimal openPositionsProfitLoss = BigDecimal.ZERO;
/*  43 */       StringBuilder content = new StringBuilder();
/*  44 */       for (Instrument instrument : Instrument.values()) {
/*  45 */         InstrumentReportData instrumentReportData = testerReportData.getInstrumentReportData(instrument);
/*  46 */         if (instrumentReportData == null) {
/*     */           continue;
/*     */         }
/*  49 */         content.append("<BR>");
/*  50 */         content.append("<h2>Instrument ").append(instrument).append("</h2><BR>");
/*  51 */         content.append("<table class=\"simple\" width=\"400px\">");
/*  52 */         content.append("\n<tr><th>First tick time</th><td>").append(format.format(new Date(instrumentReportData.firstTick.getTime()))).append("</td></tr>");
/*  53 */         content.append("\n<tr><th>First tick bid value</th><td>").append(instrumentReportData.firstTick.getBid()).append("</td></tr>");
/*  54 */         content.append("\n<tr><th>First tick ask value</th><td>").append(instrumentReportData.firstTick.getAsk()).append("</td></tr>");
/*  55 */         content.append("\n<tr><th>Last tick time</th><td>").append(format.format(new Date(instrumentReportData.lastTick.getTime()))).append("</td></tr>");
/*  56 */         content.append("\n<tr><th>Last tick bid value</th><td>").append(instrumentReportData.lastTick.getBid()).append("</td></tr>");
/*  57 */         content.append("\n<tr><th>Last tick ask value</th><td>").append(instrumentReportData.lastTick.getAsk()).append("</td></tr>");
/*     */ 
/*  59 */         content.append("\n<tr><th>Positions total</th><td>").append(instrumentReportData.positionsTotal).append("</td></tr>");
/*  60 */         content.append("\n<tr><th>Closed positions</th><td>").append(instrumentReportData.closedOrders.size()).append("</td></tr>");
/*  61 */         content.append("\n<tr><th>Orders total</th><td>").append(instrumentReportData.ordersTotal).append("</td></tr>");
/*  62 */         BigDecimal bought = BigDecimal.ZERO;
/*  63 */         BigDecimal sold = BigDecimal.ZERO;
/*  64 */         for (IOrder order : instrumentReportData.closedOrders) {
/*  65 */           bought = bought.add(BigDecimal.valueOf(((TesterOrder)order).getInitiallyFilledAmount()));
/*  66 */           sold = sold.add(BigDecimal.valueOf(((TesterOrder)order).getInitiallyFilledAmount()));
/*     */         }
/*  68 */         for (IOrder order : instrumentReportData.openedOrders) {
/*  69 */           if (order.getState() == IOrder.State.FILLED) {
/*  70 */             if (order.isLong())
/*  71 */               bought = bought.add(BigDecimal.valueOf(order.getAmount()));
/*     */             else {
/*  73 */               sold = sold.add(BigDecimal.valueOf(order.getAmount()));
/*     */             }
/*     */           }
/*     */         }
/*  77 */         content.append("\n<tr><th>Bought</th><td>").append(bought.setScale(2, 6)).append("</td></tr>");
/*  78 */         content.append("\n<tr><th>Sold</th><td>").append(sold.setScale(2, 6)).append("</td></tr>");
/*  79 */         content.append("\n<tr><th>Turnover in USD</th><td>").append(decFormat.format(instrumentReportData.turnover));
/*  80 */         content.append("</td></tr>");
/*  81 */         content.append("\n<tr><th>Comission in USD</th><td>").append(instrumentReportData.commission);
/*  82 */         content.append("</td></tr>");
/*  83 */         content.append("\n</table>");
/*  84 */         content.append("<BR>");
/*  85 */         content.append("<h3>Opened orders:</h3><br>");
/*  86 */         content.append("\n<TABLE class=\"simple\">");
/*  87 */         content.append("\n<TR>\n\t<th>Label</th>\n\t<th>Amount</th><th>Direction</th>\n\t<th>Open price</th>\n\t<th>Profit/Loss at the end</th>\n\t<th>Profit/Loss at the end in pips</th>\n\t<th>Open date</th>\n\t<th>Comment</th>\n</TR>");
/*  88 */         for (IOrder order : instrumentReportData.openedOrders) {
/*  89 */           if (order.getState() == IOrder.State.FILLED)
/*     */           {
/*     */             BigDecimal profLossInPips;
/*     */             BigDecimal profLossInSecondaryCCY;
/*     */             BigDecimal profLossInPips;
/*  92 */             if (order.getOrderCommand().isLong()) {
/*  93 */               BigDecimal profLossInSecondaryCCY = BigDecimal.valueOf(instrumentReportData.lastTick.getBid()).subtract(BigDecimal.valueOf(order.getOpenPrice())).multiply(BigDecimal.valueOf(((TesterOrder)order).getAmountInUnits())).setScale(2, 6);
/*  94 */               profLossInPips = BigDecimal.valueOf(instrumentReportData.lastTick.getBid()).subtract(BigDecimal.valueOf(order.getOpenPrice())).divide(BigDecimal.valueOf(order.getInstrument().getPipValue()), 1, 6);
/*     */             } else {
/*  96 */               profLossInSecondaryCCY = BigDecimal.valueOf(order.getOpenPrice()).subtract(BigDecimal.valueOf(instrumentReportData.lastTick.getAsk())).multiply(BigDecimal.valueOf(((TesterOrder)order).getAmountInUnits())).setScale(2, 6);
/*  97 */               profLossInPips = BigDecimal.valueOf(order.getOpenPrice()).subtract(BigDecimal.valueOf(instrumentReportData.lastTick.getAsk())).divide(BigDecimal.valueOf(order.getInstrument().getPipValue()), 1, 6);
/*     */             }
/*  99 */             OfferSide side = order.getOrderCommand().isLong() ? OfferSide.ASK : OfferSide.BID;
/* 100 */             BigDecimal convertedProfLoss = currencyConverter.convert(profLossInSecondaryCCY, instrument.getSecondaryCurrency(), accountCurrency, side).setScale(2, 6);
/* 101 */             openPositionsProfitLoss = openPositionsProfitLoss.add(convertedProfLoss);
/*     */ 
/* 103 */             TesterOrder testerOrder = (TesterOrder)order;
/*     */ 
/* 105 */             if (testerOrder.getClosePriceTotal() != 0.0D) {
/* 106 */               convertedProfLoss = convertedProfLoss.add(BigDecimal.valueOf(testerOrder.getProfitLossInAccountCCY()));
/* 107 */               double closedAmount = testerOrder.getClosedAmount();
/* 108 */               if (testerOrder.isLong())
/* 109 */                 profLossInPips = profLossInPips.multiply(BigDecimal.valueOf(testerOrder.getAmount())).add(BigDecimal.valueOf(testerOrder.getProfitLossInPips()).multiply(BigDecimal.valueOf(closedAmount))).divide(BigDecimal.valueOf(testerOrder.getAmountInUnits() + closedAmount), 2, 6);
/*     */               else {
/* 111 */                 profLossInPips = profLossInPips.multiply(BigDecimal.valueOf(testerOrder.getAmount())).add(BigDecimal.valueOf(testerOrder.getProfitLossInPips()).multiply(BigDecimal.valueOf(closedAmount))).divide(BigDecimal.valueOf(testerOrder.getAmountInUnits() + closedAmount), 2, 6);
/*     */               }
/*     */             }
/* 114 */             content.append("\n<TR>\n\t<TD>").append(order.getLabel()).append("</TD>\n\t<TD>").append(order.getAmount()).append("</TD>\n\t<TD>").append(order.getOrderCommand().isLong() ? "BUY" : "SELL").append("</TD>\n\t<TD>").append(order.getOpenPrice()).append("</TD>\n\t<TD>").append(convertedProfLoss.toPlainString()).append("</TD>\n\t<TD>").append(profLossInPips.toPlainString()).append("</TD>\n\t<TD>").append(format.format(new Date(order.getFillTime()))).append("</TD>\n\t<TD>").append(order.getComment() == null ? "" : order.getComment()).append("</TD>\n</TR>");
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 122 */         content.append("</TABLE><BR>");
/*     */ 
/* 124 */         content.append("<h3>Closed orders:</h3><BR>");
/* 125 */         content.append("<TABLE class=\"simple\">");
/* 126 */         content.append("\n<TR>\n\t<th>Label</th>\n\t<th>Amount</th>\n\t<th>Direction</th>\n\t<th>Open price</th>\n\t<th>Close price</th>\n\t<th>Profit/Loss</th>\n\t<th>Profit/Loss in pips</th>\n\t<th>Open date</th>\n\t<th>Close date</th>\n\t<th>Comment</th>\n</TR>");
/* 127 */         for (IOrder order : instrumentReportData.closedOrders) {
/* 128 */           if (order.getState() == IOrder.State.CLOSED) {
/* 129 */             BigDecimal convertedProfLoss = BigDecimal.valueOf(((TesterOrder)order).getProfitLossInAccountCCY());
/* 130 */             content.append("<TR>\n\t<TD>").append(order.getLabel()).append("</TD>\n\t<TD>").append(((TesterOrder)order).getInitiallyFilledAmount()).append("</TD>\n\t<TD>").append(order.getOrderCommand().isLong() ? "BUY" : "SELL").append("</TD>\n\t<TD>").append(order.getOpenPrice()).append("</TD>\n\t<TD>").append(((TesterOrder)order).getClosePriceTotal() == 0.0D ? "ERROR" : ((TesterOrder)order).wasMerged() ? "MERGED" : new StringBuilder().append(((TesterOrder)order).getClosePriceTotal()).append(((TesterOrder)order).wasMerged() ? " + MERGED" : "").toString()).append("</TD>\n\t<TD>").append(convertedProfLoss.toPlainString()).append("</TD>\n\t<TD>").append(((TesterOrder)order).getProfitLossInPips()).append("</TD>\n\t<TD>").append(format.format(new Date(order.getFillTime()))).append("</TD>\n\t<TD>").append(((TesterOrder)order).getClosePriceTotal() == 0.0D ? "ERROR" : ((TesterOrder)order).wasMerged() ? "MERGED" : new StringBuilder().append(format.format(new Date(order.getCloseTime()))).append(((TesterOrder)order).wasMerged() ? " + MERGED" : "").toString()).append("</TD>\n\t<TD>").append(order.getComment() == null ? "" : order.getComment()).append("</TD>\n</TR>");
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 137 */         content.append("</TABLE>\n<BR>\n");
/*     */       }
/*     */ 
/* 140 */       long[] perfStats = testerReportData.getPerfStats();
/* 141 */       if (perfStats != null) {
/* 142 */         content.append("<h2>Processing statistic:</h2>").append("<BR>");
/* 143 */         content.append("\n<TABLE class=\"simple\">\n");
/* 144 */         content.append("<TR>\n\t<th>Function</th>\n\t<th>Time</th>\n\t<th>Calls</th>\n\t<th>Percent</th>\n</TR>");
/* 145 */         PerfStatsData[] perfStatsData = new PerfStatsData[ITesterReport.PerfStats.values().length];
/* 146 */         long totalTime = 0L;
/* 147 */         for (long time : perfStats) {
/* 148 */           totalTime += time;
/*     */         }
/* 150 */         for (int i = 0; i < perfStats.length; i++) {
/* 151 */           perfStatsData[i] = new PerfStatsData(null);
/* 152 */           perfStatsData[i].perfStat = ITesterReport.PerfStats.values()[i];
/* 153 */           perfStatsData[i].count = testerReportData.getPerfStatCounts()[i];
/* 154 */           if (perfStats[i] == 0L) {
/* 155 */             perfStatsData[i].time = 0L;
/* 156 */             perfStatsData[i].percent = 0.0D;
/*     */           } else {
/* 158 */             perfStatsData[i].time = perfStats[i];
/* 159 */             perfStatsData[i].percent = (perfStats[i] / totalTime * 100.0D);
/*     */           }
/*     */         }
/* 162 */         Arrays.sort(perfStatsData);
/* 163 */         for (PerfStatsData perfStat : perfStatsData) {
/* 164 */           content.append("<TR>\n\t<TD>").append(perfStat.perfStat);
/*     */ 
/* 166 */           long nanos = perfStat.time;
/* 167 */           long days = nanos / 86400000000000L;
/* 168 */           nanos -= days * 24L * 60L * 60L * 1000L * 1000000L;
/* 169 */           long hours = nanos / 3600000000000L;
/* 170 */           nanos -= hours * 60L * 60L * 1000L * 1000000L;
/* 171 */           long mins = nanos / 60000000000L;
/* 172 */           nanos -= mins * 60L * 1000L * 1000000L;
/* 173 */           long secs = nanos / 1000000000L;
/* 174 */           nanos -= secs * 1000L * 1000000L;
/* 175 */           long millis = nanos / 1000000L;
/* 176 */           nanos -= millis * 1000000L;
/* 177 */           long millisParts = nanos / 10000L;
/* 178 */           nanos -= millisParts * 10000L;
/*     */ 
/* 180 */           content.append("</TD>\n\t<TD>");
/* 181 */           if (days > 0L) {
/* 182 */             content.append(days).append("d ");
/*     */           }
/* 184 */           if (hours > 0L) {
/* 185 */             content.append(hours).append("h ");
/*     */           }
/* 187 */           if (mins > 0L) {
/* 188 */             content.append(mins).append("m ");
/*     */           }
/* 190 */           if (secs > 0L) {
/* 191 */             content.append(secs).append("s ");
/*     */           }
/* 193 */           if (millis > 0L) {
/* 194 */             content.append(millis);
/* 195 */             if (millisParts > 0L) {
/* 196 */               content.append(".");
/* 197 */               if (millisParts < 10L) {
/* 198 */                 content.append("0");
/*     */               }
/* 200 */               content.append(millisParts);
/*     */             }
/* 202 */             content.append("ms");
/*     */           }
/* 204 */           content.append("</TD>\n\t<TD>").append(perfStat.count).append("</TD>\n\t<TD>").append(decFormat.format(perfStat.percent)).append("%");
/* 205 */           content.append("</TD>\n</TR>");
/*     */         }
/* 207 */         content.append("\n</TABLE>");
/*     */       }
/*     */ 
/* 210 */       if (addEventLog) {
/* 211 */         content.append("<h2>Event log:</h2>").append("<BR>");
/* 212 */         content.append("\n<TABLE class=\"simple\">\n");
/* 213 */         content.append("<TR>\n\t<th>Time</th>\n\t<th>Event type</th>\n\t<th>Event text</th>\n</TR>");
/* 214 */         for (TesterReportData.TesterEvent event : testerReportData.getEvents()) {
/* 215 */           content.append("<TR>\n\t<TD>").append(format.format(new Date(event.time)));
/* 216 */           switch (1.$SwitchMap$com$dukascopy$dds2$greed$agent$strategy$tester$TesterReportData$TesterEvent$EventType[event.type.ordinal()]) {
/*     */           case 1:
/* 218 */             content.append("</TD>\n\t<TD>").append("Margin Call").append("</TD>\n\t<TD>").append(event.text);
/* 219 */             break;
/*     */           case 2:
/* 221 */             content.append("</TD>\n\t<TD>").append("Margin Cut").append("</TD>\n\t<TD>").append(event.text);
/* 222 */             break;
/*     */           case 3:
/* 224 */             content.append("</TD>\n\t<TD>").append("Message").append("</TD><TD>").append(event.text);
/* 225 */             break;
/*     */           case 4:
/* 227 */             content.append("</TD>\n\t<TD><FONT COLOR=\"Red\">").append("Exception").append("</FONT></TD><TD><FONT COLOR=\"Red\">").append(event.text).append("</FONT>");
/* 228 */             break;
/*     */           case 5:
/* 230 */             content.append("</TD>\n\t<TD>").append("Order canceled").append("</TD>\n\t<TD>").append("Order [").append(event.label).append(", ").append(event.instrument).append(", ").append(event.orderCommand.name()).append(", ").append(event.amount).append(" at ").append(event.openPrice).append("] canceled ");
/* 231 */             switch (1.$SwitchMap$com$dukascopy$dds2$greed$agent$strategy$tester$TesterReportData$TesterEvent$CloseTrigger[event.closeTrigger.ordinal()]) {
/*     */             case 1:
/* 233 */               content.append("because of margin call");
/* 234 */               break;
/*     */             case 2:
/* 236 */               content.append("because of failed validation");
/* 237 */               break;
/*     */             case 3:
/* 239 */               content.append("because no margin available");
/* 240 */               break;
/*     */             case 4:
/* 242 */               content.append("in result of strategy request");
/* 243 */               break;
/*     */             case 5:
/* 245 */               content.append("because of timeout");
/* 246 */               break;
/*     */             case 6:
/* 248 */               content.append("because of no liquidity at the price");
/*     */             }
/* 250 */             break;
/*     */           case 6:
/* 252 */             content.append("</TD>\n\t<TD><FONT COLOR=\"Red\">").append("Canceled").append("</FONT></TD><TD><FONT COLOR=\"Red\">").append(event.text).append("</FONT>");
/* 253 */             break;
/*     */           case 7:
/* 255 */             content.append("</TD>\n\t<TD>").append("Order closed").append("</TD>\n\t<TD>").append("Order [").append(event.label).append(", ").append(event.instrument).append(", ").append(event.orderCommand.name()).append(", ").append(event.amount).append(" at ").append(event.openPrice).append("] closed ");
/*     */ 
/* 258 */             switch (1.$SwitchMap$com$dukascopy$dds2$greed$agent$strategy$tester$TesterReportData$TesterEvent$CloseTrigger[event.closeTrigger.ordinal()]) {
/*     */             case 7:
/* 260 */               content.append("because of margin call");
/* 261 */               break;
/*     */             case 8:
/* 263 */               content.append("by take profit event");
/* 264 */               break;
/*     */             case 9:
/* 266 */               content.append("by stop loss event");
/* 267 */               break;
/*     */             case 10:
/* 269 */               content.append("in result of strategy request");
/*     */             }
/*     */ 
/* 272 */             content.append(", amount ").append(event.closeAmount).append(" at ").append(event.closePrice);
/* 273 */             break;
/*     */           case 8:
/* 275 */             content.append("</TD>\n\t<TD>").append("Order submitted").append("</TD>\n\t<TD>").append("Order [").append(event.label).append(", ").append(event.instrument).append(", ").append(event.orderCommand.name()).append(", ").append(event.amount).append(" at ").append(event.openPrice).append("] submitted ");
/*     */ 
/* 278 */             switch (event.openTrigger) {
/*     */             case OPEN_BY_MC:
/* 280 */               content.append("by the margin cut procedure");
/* 281 */               break;
/*     */             case OPEN_BY_STRATEGY:
/* 283 */               content.append("by the strategy");
/*     */             }
/*     */ 
/* 286 */             break;
/*     */           case 9:
/* 288 */             content.append("</TD>\n\t<TD>").append("Order changed").append("</TD>\n\t<TD>").append("Order [").append(event.label).append(", ").append(event.instrument).append(", ").append(event.orderCommand.name()).append(", ").append(event.amount).append(" at ").append(event.openPrice).append("] changed by strategy");
/*     */ 
/* 291 */             break;
/*     */           case 10:
/* 293 */             content.append("</TD>\n\t<TD>").append("Order filled").append("</TD>\n\t<TD>").append("Order [").append(event.label).append(", ").append(event.instrument).append(", ").append(event.orderCommand.name()).append(", ").append(event.amount).append(" at ").append(event.openPrice).append("] filled");
/*     */ 
/* 296 */             break;
/*     */           case 11:
/* 298 */             content.append("</TD>\n\t<TD>").append("Commissions").append("</TD>\n\t<TD>").append("Commissions [").append(event.amount).append("]");
/* 299 */             break;
/*     */           case 12:
/* 301 */             content.append("</TD>\n\t<TD>").append("Overnights").append("</TD>\n\t<TD>").append("Overnight commission [").append(decFormat.format(event.openPrice)).append("] pips applied to order [").append(event.label).append(", ").append(event.instrument).append(", ").append(event.orderCommand.name()).append(", ").append(event.amount).append("]");
/*     */ 
/* 303 */             break;
/*     */           case 13:
/* 305 */             content.append("</TD><TD>").append("Order merged").append("</TD><TD>");
/* 306 */             content.append("Orders ");
/* 307 */             for (IOrder order : event.ordersMerged) {
/* 308 */               content.append(order.getLabel()).append(", ");
/*     */             }
/* 310 */             content.setLength(content.length() - 2);
/* 311 */             if (event.amount != 0.0D) {
/* 312 */               content.append(" merged into order [").append(event.label).append(", ").append(event.instrument).append(", ").append(event.orderCommand.name()).append(", ").append(event.amount).append(" at ").append(event.openPrice).append("]");
/*     */             }
/*     */             else
/*     */             {
/* 316 */               content.append(" merged with no amount left in result");
/*     */             }
/* 318 */             switch (event.closeTrigger) {
/*     */             case MERGE_BY_MC:
/* 320 */               content.append(" of the margin call procedure");
/* 321 */               break;
/*     */             case MERGE_BY_STRATEGY:
/* 323 */               content.append(" of the strategy request");
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 328 */           content.append("</TD>\n</TR>");
/*     */         }
/* 330 */         content.append("\n</TABLE>");
/*     */       }
/* 332 */       content.append("\n<br><br><br><br><br><br><br><br></BODY>\n</HTML>");
/*     */ 
/* 334 */       StringBuilder headerContent = new StringBuilder();
/* 335 */       headerContent.append("<HTML><HEAD>");
/* 336 */       headerContent.append("<style>\n.header{\nbackground-image:url(http://www.dukascopy.com/swiss/inc/images/headline_bg_menu.gif);\nbackground-repeat:repeat-x;\nclear:both;\ncolor:#FFFFFF;\nfont-size:12pt;\nfont-weight:bold;\nheight:13pt;\nmargin-bottom:10px;\npadding-left:30px;\npadding-bottom:3px;\ntext-align:left;}\nbody{\nmargin:0px;\nfont-size:11pt;\n}\ntable.simple{\nborder-collapse:collapse;\nwidth:890px;\nfont-size:0.95em;\n}\ntable.simple caption {\ncolor:#000000;\nfont-size:0.95em;\npadding-bottom:5px;\ntext-align:left;\n}\ntable.simple th {\nbackground:#E8F1F0 none repeat scroll 0% 0% !important;\nborder:1px solid #D6D6D6 !important;\npadding:2px 3px;\n}\ntable.simple td {\nborder:1px solid #D6D6D6;\npadding:3px;\ntext-align:center;\n}\ntable.simple.left td {\nborder:1px solid #999999;\npadding:3px;\ntext-align:left;\n}\ntable.simple tr.odd td {\nbackground-color:#EEEEEE;\n}\ntable.simple tr td.highlight {\nbackground:#FACDCE none repeat scroll 0% 0%;\n}\ntable.simple.numbers td {\nwhite-space:nowrap;\n}\n#content{\npadding-left:50px;\n}\nh3 {\nborder-bottom:1px solid #CCCCCC;width:890px;\ncolor:#000000;\nfont-size:0.9em;\npadding-bottom:1px;\nmargin-bottom:0px;}\nh2 {\nborder-bottom:1px solid #CCCCCC;width:890px;\ncolor:#000000;\nfont-size:1.1em;\npadding-bottom:1px;\nmargin-bottom:0px;}\nh1 {\nborder-bottom:1px solid #CCCCCC;\ncolor:#000000;font-size:1.45em;\nmargin:0pt;\nmargin-bottom:10px;\npadding-bottom:1px;\npadding-left:20px;\nwidth:900px;}\n</style>\n");
/*     */ 
/* 354 */       headerContent.append("</HEAD>\n<BODY>");
/*     */ 
/* 356 */       headerContent.append("\n<div style=\"background:url('http://www.dukascopy.com/pics/topBackground.png') repeat-x;\"><img src=\"http://www.dukascopy.com/swiss/inc/images/logo_bh40.gif\" style=\"display:block;border:none;\"></div>");
/* 357 */       headerContent.append("\n<div class=\"header\">&nbsp;</div>");
/* 358 */       headerContent.append("\n<div id=\"content\"><h1>").append(testerReportData.getStrategyName()).append(" strategy report for ");
/* 359 */       for (Instrument instrument : Instrument.values()) {
/* 360 */         InstrumentReportData instrumentReportData = testerReportData.getInstrumentReportData(instrument);
/* 361 */         if (instrumentReportData == null) {
/*     */           continue;
/*     */         }
/* 364 */         headerContent.append(instrument).append(", ");
/*     */       }
/* 366 */       headerContent.setLength(headerContent.length() - 2);
/* 367 */       headerContent.append(" instrument(s) from ").append(format.format(Long.valueOf(testerReportData.getFrom()))).append(" to ").append(format.format(Long.valueOf(testerReportData.getTo()))).append("</h1>");
/* 368 */       headerContent.append("<table class=\"simple\" width=\"400px\">");
/* 369 */       headerContent.append("<tr><th>Initial deposit</th><td>").append(decFormat.format(testerReportData.getInitialDeposit())).append("</td></tr>");
/* 370 */       headerContent.append("<tr><th>Finish deposit</th><td>").append(decFormat.format(BigDecimal.valueOf(testerReportData.getFinishDeposit()).add(openPositionsProfitLoss).doubleValue())).append("</td></tr>");
/* 371 */       headerContent.append("\n<tr><th>Turnover in USD</th><td>").append(decFormat.format(testerReportData.getTurnover()));
/* 372 */       headerContent.append("</td></tr>");
/* 373 */       headerContent.append("\n<tr><th>Comission in USD</th><td>").append(testerReportData.getCommission());
/* 374 */       headerContent.append("\n</table>");
/*     */ 
/* 376 */       if (testerReportData.getParameterValues() != null) {
/* 377 */         headerContent.append("<h2>Parameters</h2><BR>");
/* 378 */         headerContent.append("<table class=\"simple\" width=\"400px\">");
/* 379 */         for (String[] value : testerReportData.getParameterValues()) {
/* 380 */           headerContent.append("<tr><th>").append(value[0]).append("</th><td>").append(value[1]).append("</td></tr>");
/*     */         }
/* 382 */         headerContent.append("\n</table>");
/*     */       }
/*     */ 
/* 385 */       content.insert(0, headerContent);
/* 386 */       out.write(content.toString());
/*     */     } finally {
/*     */       try {
/* 389 */         out.close();
/*     */       } catch (IOException e) {
/* 391 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class CurrencyConverter extends AbstractCurrencyConverter
/*     */   {
/*     */     private ITesterReport testerReportData;
/*     */ 
/*     */     private CurrencyConverter(ITesterReport testerReportData)
/*     */     {
/* 418 */       this.testerReportData = testerReportData;
/*     */     }
/*     */ 
/*     */     protected double getLastMarketPrice(Instrument instrument, OfferSide side)
/*     */     {
/* 423 */       InstrumentReportData report = this.testerReportData.getInstrumentReportData(instrument);
/* 424 */       if (side == OfferSide.ASK) {
/* 425 */         return report.lastTick.getAsk();
/*     */       }
/* 427 */       return report.lastTick.getBid();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class PerfStatsData
/*     */     implements Comparable<PerfStatsData>
/*     */   {
/*     */     public ITesterReport.PerfStats perfStat;
/*     */     public long time;
/*     */     public double percent;
/*     */     public int count;
/*     */ 
/*     */     public int compareTo(PerfStatsData p)
/*     */     {
/* 404 */       if (this.time < p.time)
/* 405 */         return 1;
/* 406 */       if (this.time > p.time) {
/* 407 */         return -1;
/*     */       }
/* 409 */       return 0;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.StrategyReport
 * JD-Core Version:    0.6.0
 */